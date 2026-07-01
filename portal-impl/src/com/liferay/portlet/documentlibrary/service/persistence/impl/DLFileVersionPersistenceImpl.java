/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.persistence.impl;

import com.liferay.document.library.kernel.exception.NoSuchFileVersionException;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFileVersionTable;
import com.liferay.document.library.kernel.service.persistence.DLFileVersionPersistence;
import com.liferay.document.library.kernel.service.persistence.DLFileVersionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portlet.documentlibrary.model.impl.DLFileVersionImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileVersionModelImpl;

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

/**
 * The persistence implementation for the document library file version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DLFileVersionPersistenceImpl
	extends BasePersistenceImpl<DLFileVersion, NoSuchFileVersionException>
	implements DLFileVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DLFileVersionUtil</code> to access the document library file version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DLFileVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DLFileVersion, NoSuchFileVersionException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the document library file versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DLFileVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version
	 * @throws NoSuchFileVersionException if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion findByUuid_First(
			String uuid, OrderByComparator<DLFileVersion> orderByComparator)
		throws NoSuchFileVersionException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first document library file version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version, or <code>null</code> if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion fetchByUuid_First(
		String uuid, OrderByComparator<DLFileVersion> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the document library file versions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of document library file versions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching document library file versions
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private UniquePersistenceFinder<DLFileVersion, NoSuchFileVersionException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the document library file version where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFileVersionException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching document library file version
	 * @throws NoSuchFileVersionException if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion findByUUID_G(String uuid, long groupId)
		throws NoSuchFileVersionException {

		return _uniquePersistenceFinderByUUID_G.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	/**
	 * Returns the document library file version where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file version, or <code>null</code> if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId},
			useFinderCache);
	}

	/**
	 * Removes the document library file version where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the document library file version that was removed
	 */
	@Override
	public DLFileVersion removeByUUID_G(String uuid, long groupId)
		throws NoSuchFileVersionException {

		DLFileVersion dlFileVersion = findByUUID_G(uuid, groupId);

		return remove(dlFileVersion);
	}

	/**
	 * Returns the number of document library file versions where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching document library file versions
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<DLFileVersion, NoSuchFileVersionException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the document library file versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFileVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version
	 * @throws NoSuchFileVersionException if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DLFileVersion> orderByComparator)
		throws NoSuchFileVersionException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version, or <code>null</code> if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DLFileVersion> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the document library file versions where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of document library file versions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching document library file versions
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<DLFileVersion, NoSuchFileVersionException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the document library file versions where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DLFileVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file version in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version
	 * @throws NoSuchFileVersionException if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion findByCompanyId_First(
			long companyId, OrderByComparator<DLFileVersion> orderByComparator)
		throws NoSuchFileVersionException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file version in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version, or <code>null</code> if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion fetchByCompanyId_First(
		long companyId, OrderByComparator<DLFileVersion> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the document library file versions where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of document library file versions where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching document library file versions
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<DLFileVersion, NoSuchFileVersionException>
			_collectionPersistenceFinderByFileEntryId;

	/**
	 * Returns an ordered range of all the document library file versions where fileEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryId the file entry ID
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByFileEntryId(
		long fileEntryId, int start, int end,
		OrderByComparator<DLFileVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByFileEntryId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {fileEntryId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file version in the ordered set where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version
	 * @throws NoSuchFileVersionException if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion findByFileEntryId_First(
			long fileEntryId,
			OrderByComparator<DLFileVersion> orderByComparator)
		throws NoSuchFileVersionException {

		return _collectionPersistenceFinderByFileEntryId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {fileEntryId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file version in the ordered set where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version, or <code>null</code> if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion fetchByFileEntryId_First(
		long fileEntryId, OrderByComparator<DLFileVersion> orderByComparator) {

		return _collectionPersistenceFinderByFileEntryId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {fileEntryId},
			orderByComparator);
	}

	/**
	 * Removes all the document library file versions where fileEntryId = &#63; from the database.
	 *
	 * @param fileEntryId the file entry ID
	 */
	@Override
	public void removeByFileEntryId(long fileEntryId) {
		_collectionPersistenceFinderByFileEntryId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {fileEntryId});
	}

	/**
	 * Returns the number of document library file versions where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @return the number of matching document library file versions
	 */
	@Override
	public int countByFileEntryId(long fileEntryId) {
		return _collectionPersistenceFinderByFileEntryId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {fileEntryId});
	}

	private CollectionPersistenceFinder
		<DLFileVersion, NoSuchFileVersionException>
			_collectionPersistenceFinderByMimeType;

	/**
	 * Returns an ordered range of all the document library file versions where mimeType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionModelImpl</code>.
	 * </p>
	 *
	 * @param mimeType the mime type
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByMimeType(
		String mimeType, int start, int end,
		OrderByComparator<DLFileVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByMimeType.find(
			FinderCacheUtil.getFinderCache(), new Object[] {mimeType}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file version in the ordered set where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version
	 * @throws NoSuchFileVersionException if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion findByMimeType_First(
			String mimeType, OrderByComparator<DLFileVersion> orderByComparator)
		throws NoSuchFileVersionException {

		return _collectionPersistenceFinderByMimeType.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {mimeType},
			orderByComparator);
	}

	/**
	 * Returns the first document library file version in the ordered set where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version, or <code>null</code> if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion fetchByMimeType_First(
		String mimeType, OrderByComparator<DLFileVersion> orderByComparator) {

		return _collectionPersistenceFinderByMimeType.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {mimeType},
			orderByComparator);
	}

	/**
	 * Removes all the document library file versions where mimeType = &#63; from the database.
	 *
	 * @param mimeType the mime type
	 */
	@Override
	public void removeByMimeType(String mimeType) {
		_collectionPersistenceFinderByMimeType.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {mimeType});
	}

	/**
	 * Returns the number of document library file versions where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @return the number of matching document library file versions
	 */
	@Override
	public int countByMimeType(String mimeType) {
		return _collectionPersistenceFinderByMimeType.count(
			FinderCacheUtil.getFinderCache(), new Object[] {mimeType});
	}

	private CollectionPersistenceFinder
		<DLFileVersion, NoSuchFileVersionException>
			_collectionPersistenceFinderByC_SU;

	/**
	 * Returns an ordered range of all the document library file versions where companyId = &#63; and storeUUID = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param storeUUID the store uuid
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByC_SU(
		long companyId, String storeUUID, int start, int end,
		OrderByComparator<DLFileVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_SU.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, storeUUID}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first document library file version in the ordered set where companyId = &#63; and storeUUID = &#63;.
	 *
	 * @param companyId the company ID
	 * @param storeUUID the store uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version
	 * @throws NoSuchFileVersionException if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion findByC_SU_First(
			long companyId, String storeUUID,
			OrderByComparator<DLFileVersion> orderByComparator)
		throws NoSuchFileVersionException {

		return _collectionPersistenceFinderByC_SU.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, storeUUID}, orderByComparator);
	}

	/**
	 * Returns the first document library file version in the ordered set where companyId = &#63; and storeUUID = &#63;.
	 *
	 * @param companyId the company ID
	 * @param storeUUID the store uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version, or <code>null</code> if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion fetchByC_SU_First(
		long companyId, String storeUUID,
		OrderByComparator<DLFileVersion> orderByComparator) {

		return _collectionPersistenceFinderByC_SU.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, storeUUID}, orderByComparator);
	}

	/**
	 * Removes all the document library file versions where companyId = &#63; and storeUUID = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param storeUUID the store uuid
	 */
	@Override
	public void removeByC_SU(long companyId, String storeUUID) {
		_collectionPersistenceFinderByC_SU.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, storeUUID});
	}

	/**
	 * Returns the number of document library file versions where companyId = &#63; and storeUUID = &#63;.
	 *
	 * @param companyId the company ID
	 * @param storeUUID the store uuid
	 * @return the number of matching document library file versions
	 */
	@Override
	public int countByC_SU(long companyId, String storeUUID) {
		return _collectionPersistenceFinderByC_SU.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, storeUUID});
	}

	private CollectionPersistenceFinder
		<DLFileVersion, NoSuchFileVersionException>
			_collectionPersistenceFinderByC_NotS;

	/**
	 * Returns all the document library file versions where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByC_NotS(long companyId, int status) {
		return findByC_NotS(
			companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file versions where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @return the range of matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByC_NotS(
		long companyId, int status, int start, int end) {

		return findByC_NotS(companyId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file versions where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<DLFileVersion> orderByComparator) {

		return findByC_NotS(
			companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file versions where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<DLFileVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_NotS.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file version in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version
	 * @throws NoSuchFileVersionException if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion findByC_NotS_First(
			long companyId, int status,
			OrderByComparator<DLFileVersion> orderByComparator)
		throws NoSuchFileVersionException {

		return _collectionPersistenceFinderByC_NotS.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, status},
			orderByComparator);
	}

	/**
	 * Returns the first document library file version in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version, or <code>null</code> if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion fetchByC_NotS_First(
		long companyId, int status,
		OrderByComparator<DLFileVersion> orderByComparator) {

		return _collectionPersistenceFinderByC_NotS.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, status},
			orderByComparator);
	}

	/**
	 * Removes all the document library file versions where companyId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_NotS(long companyId, int status) {
		_collectionPersistenceFinderByC_NotS.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, status});
	}

	/**
	 * Returns the number of document library file versions where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching document library file versions
	 */
	@Override
	public int countByC_NotS(long companyId, int status) {
		return _collectionPersistenceFinderByC_NotS.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, status});
	}

	private UniquePersistenceFinder<DLFileVersion, NoSuchFileVersionException>
		_uniquePersistenceFinderByF_V;

	/**
	 * Returns the document library file version where fileEntryId = &#63; and version = &#63; or throws a <code>NoSuchFileVersionException</code> if it could not be found.
	 *
	 * @param fileEntryId the file entry ID
	 * @param version the version
	 * @return the matching document library file version
	 * @throws NoSuchFileVersionException if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion findByF_V(long fileEntryId, String version)
		throws NoSuchFileVersionException {

		return _uniquePersistenceFinderByF_V.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {fileEntryId, version});
	}

	/**
	 * Returns the document library file version where fileEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param fileEntryId the file entry ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file version, or <code>null</code> if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion fetchByF_V(
		long fileEntryId, String version, boolean useFinderCache) {

		return _uniquePersistenceFinderByF_V.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {fileEntryId, version}, useFinderCache);
	}

	/**
	 * Removes the document library file version where fileEntryId = &#63; and version = &#63; from the database.
	 *
	 * @param fileEntryId the file entry ID
	 * @param version the version
	 * @return the document library file version that was removed
	 */
	@Override
	public DLFileVersion removeByF_V(long fileEntryId, String version)
		throws NoSuchFileVersionException {

		DLFileVersion dlFileVersion = findByF_V(fileEntryId, version);

		return remove(dlFileVersion);
	}

	/**
	 * Returns the number of document library file versions where fileEntryId = &#63; and version = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param version the version
	 * @return the number of matching document library file versions
	 */
	@Override
	public int countByF_V(long fileEntryId, String version) {
		return _uniquePersistenceFinderByF_V.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {fileEntryId, version});
	}

	private CollectionPersistenceFinder
		<DLFileVersion, NoSuchFileVersionException>
			_collectionPersistenceFinderByF_S;

	/**
	 * Returns an ordered range of all the document library file versions where fileEntryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryId the file entry ID
	 * @param status the status
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByF_S(
		long fileEntryId, int status, int start, int end,
		OrderByComparator<DLFileVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByF_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {fileEntryId, new int[] {status}}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file version in the ordered set where fileEntryId = &#63; and status = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version
	 * @throws NoSuchFileVersionException if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion findByF_S_First(
			long fileEntryId, int status,
			OrderByComparator<DLFileVersion> orderByComparator)
		throws NoSuchFileVersionException {

		DLFileVersion dlFileVersion = fetchByF_S_First(
			fileEntryId, status, orderByComparator);

		if (dlFileVersion != null) {
			return dlFileVersion;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("fileEntryId=");
		sb.append(fileEntryId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFileVersionException(sb.toString());
	}

	/**
	 * Returns the first document library file version in the ordered set where fileEntryId = &#63; and status = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version, or <code>null</code> if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion fetchByF_S_First(
		long fileEntryId, int status,
		OrderByComparator<DLFileVersion> orderByComparator) {

		return _collectionPersistenceFinderByF_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {fileEntryId, new int[] {status}}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file versions where fileEntryId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryId the file entry ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByF_S(
		long fileEntryId, int[] statuses, int start, int end,
		OrderByComparator<DLFileVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByF_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {fileEntryId, ArrayUtil.sortedUnique(statuses)}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the document library file versions where fileEntryId = &#63; and status = &#63; from the database.
	 *
	 * @param fileEntryId the file entry ID
	 * @param status the status
	 */
	@Override
	public void removeByF_S(long fileEntryId, int status) {
		_collectionPersistenceFinderByF_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {fileEntryId, new int[] {status}});
	}

	/**
	 * Returns the number of document library file versions where fileEntryId = &#63; and status = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param status the status
	 * @return the number of matching document library file versions
	 */
	@Override
	public int countByF_S(long fileEntryId, int status) {
		return _collectionPersistenceFinderByF_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {fileEntryId, new int[] {status}});
	}

	/**
	 * Returns the number of document library file versions where fileEntryId = &#63; and status = any &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param statuses the statuses
	 * @return the number of matching document library file versions
	 */
	@Override
	public int countByF_S(long fileEntryId, int[] statuses) {
		return _collectionPersistenceFinderByF_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {fileEntryId, ArrayUtil.sortedUnique(statuses)});
	}

	private CollectionPersistenceFinder
		<DLFileVersion, NoSuchFileVersionException>
			_collectionPersistenceFinderByLtD_S;

	/**
	 * Returns all the document library file versions where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByLtD_S(Date displayDate, int status) {
		return findByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file versions where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @return the range of matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return findByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file versions where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<DLFileVersion> orderByComparator) {

		return findByLtD_S(
			displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file versions where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<DLFileVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtD_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {displayDate, status}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first document library file version in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version
	 * @throws NoSuchFileVersionException if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<DLFileVersion> orderByComparator)
		throws NoSuchFileVersionException {

		return _collectionPersistenceFinderByLtD_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Returns the first document library file version in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version, or <code>null</code> if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<DLFileVersion> orderByComparator) {

		return _collectionPersistenceFinderByLtD_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Removes all the document library file versions where displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByLtD_S(Date displayDate, int status) {
		_collectionPersistenceFinderByLtD_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {displayDate, status});
	}

	/**
	 * Returns the number of document library file versions where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching document library file versions
	 */
	@Override
	public int countByLtD_S(Date displayDate, int status) {
		return _collectionPersistenceFinderByLtD_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {displayDate, status});
	}

	private CollectionPersistenceFinder
		<DLFileVersion, NoSuchFileVersionException>
			_collectionPersistenceFinderByG_F_S;

	/**
	 * Returns an ordered range of all the document library file versions where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByG_F_S(
		long groupId, long folderId, int status, int start, int end,
		OrderByComparator<DLFileVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_F_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file version in the ordered set where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version
	 * @throws NoSuchFileVersionException if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion findByG_F_S_First(
			long groupId, long folderId, int status,
			OrderByComparator<DLFileVersion> orderByComparator)
		throws NoSuchFileVersionException {

		return _collectionPersistenceFinderByG_F_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, status}, orderByComparator);
	}

	/**
	 * Returns the first document library file version in the ordered set where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version, or <code>null</code> if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion fetchByG_F_S_First(
		long groupId, long folderId, int status,
		OrderByComparator<DLFileVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_F_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, status}, orderByComparator);
	}

	/**
	 * Removes all the document library file versions where groupId = &#63; and folderId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 */
	@Override
	public void removeByG_F_S(long groupId, long folderId, int status) {
		_collectionPersistenceFinderByG_F_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, status});
	}

	/**
	 * Returns the number of document library file versions where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @return the number of matching document library file versions
	 */
	@Override
	public int countByG_F_S(long groupId, long folderId, int status) {
		return _collectionPersistenceFinderByG_F_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, status});
	}

	private CollectionPersistenceFinder
		<DLFileVersion, NoSuchFileVersionException>
			_collectionPersistenceFinderByC_E_S;

	/**
	 * Returns an ordered range of all the document library file versions where companyId = &#63; and expirationDate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByC_E_S(
		long companyId, Date expirationDate, int status, int start, int end,
		OrderByComparator<DLFileVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_E_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, expirationDate, new int[] {status}}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file version in the ordered set where companyId = &#63; and expirationDate = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version
	 * @throws NoSuchFileVersionException if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion findByC_E_S_First(
			long companyId, Date expirationDate, int status,
			OrderByComparator<DLFileVersion> orderByComparator)
		throws NoSuchFileVersionException {

		DLFileVersion dlFileVersion = fetchByC_E_S_First(
			companyId, expirationDate, status, orderByComparator);

		if (dlFileVersion != null) {
			return dlFileVersion;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", expirationDate=");
		sb.append(expirationDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFileVersionException(sb.toString());
	}

	/**
	 * Returns the first document library file version in the ordered set where companyId = &#63; and expirationDate = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version, or <code>null</code> if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion fetchByC_E_S_First(
		long companyId, Date expirationDate, int status,
		OrderByComparator<DLFileVersion> orderByComparator) {

		return _collectionPersistenceFinderByC_E_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, expirationDate, new int[] {status}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file versions where companyId = &#63; and expirationDate = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param expirationDate the expiration date
	 * @param statuses the statuses
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByC_E_S(
		long companyId, Date expirationDate, int[] statuses, int start, int end,
		OrderByComparator<DLFileVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_E_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, expirationDate, ArrayUtil.sortedUnique(statuses)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the document library file versions where companyId = &#63; and expirationDate = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param expirationDate the expiration date
	 * @param status the status
	 */
	@Override
	public void removeByC_E_S(long companyId, Date expirationDate, int status) {
		_collectionPersistenceFinderByC_E_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, expirationDate, new int[] {status}});
	}

	/**
	 * Returns the number of document library file versions where companyId = &#63; and expirationDate = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the number of matching document library file versions
	 */
	@Override
	public int countByC_E_S(long companyId, Date expirationDate, int status) {
		return _collectionPersistenceFinderByC_E_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, expirationDate, new int[] {status}});
	}

	/**
	 * Returns the number of document library file versions where companyId = &#63; and expirationDate = &#63; and status = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param expirationDate the expiration date
	 * @param statuses the statuses
	 * @return the number of matching document library file versions
	 */
	@Override
	public int countByC_E_S(
		long companyId, Date expirationDate, int[] statuses) {

		return _collectionPersistenceFinderByC_E_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, expirationDate, ArrayUtil.sortedUnique(statuses)
			});
	}

	private CollectionPersistenceFinder
		<DLFileVersion, NoSuchFileVersionException>
			_collectionPersistenceFinderByG_F_T_V;

	/**
	 * Returns an ordered range of all the document library file versions where groupId = &#63; and folderId = &#63; and title = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @param version the version
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file versions
	 */
	@Override
	public List<DLFileVersion> findByG_F_T_V(
		long groupId, long folderId, String title, String version, int start,
		int end, OrderByComparator<DLFileVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_F_T_V.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, title, version}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file version in the ordered set where groupId = &#63; and folderId = &#63; and title = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version
	 * @throws NoSuchFileVersionException if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion findByG_F_T_V_First(
			long groupId, long folderId, String title, String version,
			OrderByComparator<DLFileVersion> orderByComparator)
		throws NoSuchFileVersionException {

		return _collectionPersistenceFinderByG_F_T_V.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, title, version},
			orderByComparator);
	}

	/**
	 * Returns the first document library file version in the ordered set where groupId = &#63; and folderId = &#63; and title = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file version, or <code>null</code> if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion fetchByG_F_T_V_First(
		long groupId, long folderId, String title, String version,
		OrderByComparator<DLFileVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_F_T_V.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, title, version},
			orderByComparator);
	}

	/**
	 * Removes all the document library file versions where groupId = &#63; and folderId = &#63; and title = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @param version the version
	 */
	@Override
	public void removeByG_F_T_V(
		long groupId, long folderId, String title, String version) {

		_collectionPersistenceFinderByG_F_T_V.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, title, version});
	}

	/**
	 * Returns the number of document library file versions where groupId = &#63; and folderId = &#63; and title = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @param version the version
	 * @return the number of matching document library file versions
	 */
	@Override
	public int countByG_F_T_V(
		long groupId, long folderId, String title, String version) {

		return _collectionPersistenceFinderByG_F_T_V.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, title, version});
	}

	public DLFileVersionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("size", "size_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DLFileVersion.class);

		setModelImplClass(DLFileVersionImpl.class);
		setModelPKClass(long.class);

		setTable(DLFileVersionTable.INSTANCE);
	}

	/**
	 * Creates a new document library file version with the primary key. Does not add the document library file version to the database.
	 *
	 * @param fileVersionId the primary key for the new document library file version
	 * @return the new document library file version
	 */
	@Override
	public DLFileVersion create(long fileVersionId) {
		DLFileVersion dlFileVersion = new DLFileVersionImpl();

		dlFileVersion.setNew(true);
		dlFileVersion.setPrimaryKey(fileVersionId);

		String uuid = PortalUUIDUtil.generate();

		dlFileVersion.setUuid(uuid);

		dlFileVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return dlFileVersion;
	}

	/**
	 * Removes the document library file version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fileVersionId the primary key of the document library file version
	 * @return the document library file version that was removed
	 * @throws NoSuchFileVersionException if a document library file version with the primary key could not be found
	 */
	@Override
	public DLFileVersion remove(long fileVersionId)
		throws NoSuchFileVersionException {

		return remove((Serializable)fileVersionId);
	}

	@Override
	protected DLFileVersion removeImpl(DLFileVersion dlFileVersion) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dlFileVersion)) {
				dlFileVersion = (DLFileVersion)session.get(
					DLFileVersionImpl.class, dlFileVersion.getPrimaryKeyObj());
			}

			if ((dlFileVersion != null) &&
				CTPersistenceHelperUtil.isRemove(dlFileVersion)) {

				session.delete(dlFileVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dlFileVersion != null) {
			clearCache(dlFileVersion);
		}

		return dlFileVersion;
	}

	@Override
	public DLFileVersion updateImpl(DLFileVersion dlFileVersion) {
		boolean isNew = dlFileVersion.isNew();

		if (!(dlFileVersion instanceof DLFileVersionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dlFileVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					dlFileVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dlFileVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DLFileVersion implementation " +
					dlFileVersion.getClass());
		}

		DLFileVersionModelImpl dlFileVersionModelImpl =
			(DLFileVersionModelImpl)dlFileVersion;

		if (Validator.isNull(dlFileVersion.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			dlFileVersion.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (dlFileVersion.getCreateDate() == null)) {
			if (serviceContext == null) {
				dlFileVersion.setCreateDate(date);
			}
			else {
				dlFileVersion.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!dlFileVersionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				dlFileVersion.setModifiedDate(date);
			}
			else {
				dlFileVersion.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(dlFileVersion)) {
				if (!isNew) {
					session.evict(
						DLFileVersionImpl.class,
						dlFileVersion.getPrimaryKeyObj());
				}

				session.save(dlFileVersion);
			}
			else {
				dlFileVersion = (DLFileVersion)session.merge(dlFileVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(dlFileVersion, false);

		if (isNew) {
			dlFileVersion.setNew(false);
		}

		dlFileVersion.resetOriginalValues();

		return dlFileVersion;
	}

	/**
	 * Returns the document library file version with the primary key or throws a <code>NoSuchFileVersionException</code> if it could not be found.
	 *
	 * @param fileVersionId the primary key of the document library file version
	 * @return the document library file version
	 * @throws NoSuchFileVersionException if a document library file version with the primary key could not be found
	 */
	@Override
	public DLFileVersion findByPrimaryKey(long fileVersionId)
		throws NoSuchFileVersionException {

		return findByPrimaryKey((Serializable)fileVersionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the document library file version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fileVersionId the primary key of the document library file version
	 * @return the document library file version, or <code>null</code> if a document library file version with the primary key could not be found
	 */
	@Override
	public DLFileVersion fetchByPrimaryKey(long fileVersionId) {
		return fetchByPrimaryKey((Serializable)fileVersionId);
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
		return "fileVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DLFILEVERSION;
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
		return DLFileVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DLFileVersion";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("repositoryId");
		ctMergeColumnNames.add("folderId");
		ctMergeColumnNames.add("fileEntryId");
		ctMergeColumnNames.add("treePath");
		ctMergeColumnNames.add("fileName");
		ctMergeColumnNames.add("extension");
		ctMergeColumnNames.add("mimeType");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("changeLog");
		ctMergeColumnNames.add("extraSettings");
		ctMergeColumnNames.add("fileEntryTypeId");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("size_");
		ctMergeColumnNames.add("checksum");
		ctMergeColumnNames.add("storeUUID");
		ctMergeColumnNames.add("displayDate");
		ctMergeColumnNames.add("expirationDate");
		ctMergeColumnNames.add("reviewDate");
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
			CTColumnResolutionType.PK, Collections.singleton("fileVersionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"fileEntryId", "version"});
	}

	/**
	 * Initializes the document library file version persistence.
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
			_SQL_SELECT_DLFILEVERSION_WHERE, _SQL_COUNT_DLFILEVERSION_WHERE,
			DLFileVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"dlFileVersion.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, DLFileVersion::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(DLFileVersion::getUuid),
				DLFileVersion::getGroupId),
			_SQL_SELECT_DLFILEVERSION_WHERE, "",
			new FinderColumn<>(
				"dlFileVersion.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, DLFileVersion::getUuid),
			new FinderColumn<>(
				"dlFileVersion.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, DLFileVersion::getGroupId));

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
				_SQL_SELECT_DLFILEVERSION_WHERE, _SQL_COUNT_DLFILEVERSION_WHERE,
				DLFileVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"dlFileVersion.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, DLFileVersion::getUuid),
				new FinderColumn<>(
					"dlFileVersion.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, DLFileVersion::getCompanyId));

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
				_SQL_SELECT_DLFILEVERSION_WHERE, _SQL_COUNT_DLFILEVERSION_WHERE,
				DLFileVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"dlFileVersion.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, DLFileVersion::getCompanyId));

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
				_SQL_SELECT_DLFILEVERSION_WHERE, _SQL_COUNT_DLFILEVERSION_WHERE,
				DLFileVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"dlFileVersion.", "fileEntryId", FinderColumn.Type.LONG,
					"=", true, true, DLFileVersion::getFileEntryId));

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
				_SQL_SELECT_DLFILEVERSION_WHERE, _SQL_COUNT_DLFILEVERSION_WHERE,
				DLFileVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"dlFileVersion.", "mimeType", FinderColumn.Type.STRING, "=",
					true, true, DLFileVersion::getMimeType));

		_collectionPersistenceFinderByC_SU = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_SU",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "storeUUID"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_SU",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "storeUUID"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_SU",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "storeUUID"}, 0, 2, false, null),
			_SQL_SELECT_DLFILEVERSION_WHERE, _SQL_COUNT_DLFILEVERSION_WHERE,
			DLFileVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"dlFileVersion.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, DLFileVersion::getCompanyId),
			new FinderColumn<>(
				"dlFileVersion.", "storeUUID", FinderColumn.Type.STRING, "=",
				true, true, DLFileVersion::getStoreUUID));

		_collectionPersistenceFinderByC_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_NotS",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_NotS",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"companyId", "status"}, false),
				_SQL_SELECT_DLFILEVERSION_WHERE, _SQL_COUNT_DLFILEVERSION_WHERE,
				DLFileVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"dlFileVersion.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, DLFileVersion::getCompanyId),
				new FinderColumn<>(
					"dlFileVersion.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, DLFileVersion::getStatus));

		_uniquePersistenceFinderByF_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByF_V",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"fileEntryId", "version"}, 0, 2, false,
				DLFileVersion::getFileEntryId,
				convertNullFunction(DLFileVersion::getVersion)),
			_SQL_SELECT_DLFILEVERSION_WHERE, "",
			new FinderColumn<>(
				"dlFileVersion.", "fileEntryId", FinderColumn.Type.LONG, "=",
				true, true, DLFileVersion::getFileEntryId),
			new FinderColumn<>(
				"dlFileVersion.", "version", FinderColumn.Type.STRING, "=",
				true, true, DLFileVersion::getVersion));

		_collectionPersistenceFinderByF_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByF_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"fileEntryId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByF_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"fileEntryId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByF_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"fileEntryId", "status"}, false),
			_SQL_SELECT_DLFILEVERSION_WHERE, _SQL_COUNT_DLFILEVERSION_WHERE,
			DLFileVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"dlFileVersion.", "fileEntryId", FinderColumn.Type.LONG, "=",
				true, true, DLFileVersion::getFileEntryId),
			new ArrayableFinderColumn<>(
				"dlFileVersion.", "status", FinderColumn.Type.INTEGER, "=",
				false, true, true, DLFileVersion::getStatus));

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
			_SQL_SELECT_DLFILEVERSION_WHERE, _SQL_COUNT_DLFILEVERSION_WHERE,
			DLFileVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"dlFileVersion.", "displayDate", FinderColumn.Type.DATE, "<",
				true, true, DLFileVersion::getDisplayDate),
			new FinderColumn<>(
				"dlFileVersion.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, DLFileVersion::getStatus));

		_collectionPersistenceFinderByG_F_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_F_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "folderId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_F_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"groupId", "folderId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_F_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"groupId", "folderId", "status"}, false),
			_SQL_SELECT_DLFILEVERSION_WHERE, _SQL_COUNT_DLFILEVERSION_WHERE,
			DLFileVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"dlFileVersion.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, DLFileVersion::getGroupId),
			new FinderColumn<>(
				"dlFileVersion.", "folderId", FinderColumn.Type.LONG, "=", true,
				true, DLFileVersion::getFolderId),
			new FinderColumn<>(
				"dlFileVersion.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, DLFileVersion::getStatus));

		_collectionPersistenceFinderByC_E_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_E_S",
				new String[] {
					Long.class.getName(), Date.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "expirationDate", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_E_S",
				new String[] {
					Long.class.getName(), Date.class.getName(),
					Integer.class.getName()
				},
				new String[] {"companyId", "expirationDate", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_E_S",
				new String[] {
					Long.class.getName(), Date.class.getName(),
					Integer.class.getName()
				},
				new String[] {"companyId", "expirationDate", "status"}, false),
			_SQL_SELECT_DLFILEVERSION_WHERE, _SQL_COUNT_DLFILEVERSION_WHERE,
			DLFileVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"dlFileVersion.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, DLFileVersion::getCompanyId),
			new FinderColumn<>(
				"dlFileVersion.", "expirationDate", FinderColumn.Type.DATE, "=",
				true, true, DLFileVersion::getExpirationDate),
			new ArrayableFinderColumn<>(
				"dlFileVersion.", "status", FinderColumn.Type.INTEGER, "=",
				false, true, true, DLFileVersion::getStatus));

		_collectionPersistenceFinderByG_F_T_V =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_F_T_V",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "folderId", "title", "version"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_F_T_V",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), String.class.getName()
					},
					new String[] {"groupId", "folderId", "title", "version"}, 0,
					12, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_F_T_V",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), String.class.getName()
					},
					new String[] {"groupId", "folderId", "title", "version"}, 0,
					12, false, null),
				_SQL_SELECT_DLFILEVERSION_WHERE, _SQL_COUNT_DLFILEVERSION_WHERE,
				DLFileVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"dlFileVersion.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, DLFileVersion::getGroupId),
				new FinderColumn<>(
					"dlFileVersion.", "folderId", FinderColumn.Type.LONG, "=",
					true, true, DLFileVersion::getFolderId),
				new FinderColumn<>(
					"dlFileVersion.", "title", FinderColumn.Type.STRING, "=",
					true, true, DLFileVersion::getTitle),
				new FinderColumn<>(
					"dlFileVersion.", "version", FinderColumn.Type.STRING, "=",
					true, true, DLFileVersion::getVersion));

		DLFileVersionUtil.setPersistence(this);
	}

	public void destroy() {
		DLFileVersionUtil.setPersistence(null);

		EntityCacheUtil.removeCache(DLFileVersionImpl.class.getName());
	}

	private static Long _getTime(Date date) {
		if (date == null) {
			return null;
		}

		return date.getTime();
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		DLFileVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DLFILEVERSION =
		"SELECT dlFileVersion FROM DLFileVersion dlFileVersion";

	private static final String _SQL_SELECT_DLFILEVERSION_WHERE =
		"SELECT dlFileVersion FROM DLFileVersion dlFileVersion WHERE ";

	private static final String _SQL_COUNT_DLFILEVERSION_WHERE =
		"SELECT COUNT(dlFileVersion) FROM DLFileVersion dlFileVersion WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DLFileVersion exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileVersionPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "size"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:864371827