/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.persistence.impl;

import com.liferay.document.library.kernel.exception.DuplicateDLFileEntryMetadataExternalReferenceCodeException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryMetadataException;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryMetadataTable;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryMetadataPersistence;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryMetadataUtil;
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
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
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
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryMetadataImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryMetadataModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The persistence implementation for the document library file entry metadata service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DLFileEntryMetadataPersistenceImpl
	extends BasePersistenceImpl
		<DLFileEntryMetadata, NoSuchFileEntryMetadataException>
	implements DLFileEntryMetadataPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DLFileEntryMetadataUtil</code> to access the document library file entry metadata persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DLFileEntryMetadataImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DLFileEntryMetadata, NoSuchFileEntryMetadataException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the document library file entry metadatas where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library file entry metadatas
	 * @param end the upper bound of the range of document library file entry metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entry metadatas
	 */
	@Override
	public List<DLFileEntryMetadata> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DLFileEntryMetadata> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry metadata in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry metadata
	 * @throws NoSuchFileEntryMetadataException if a matching document library file entry metadata could not be found
	 */
	@Override
	public DLFileEntryMetadata findByUuid_First(
			String uuid,
			OrderByComparator<DLFileEntryMetadata> orderByComparator)
		throws NoSuchFileEntryMetadataException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry metadata in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry metadata, or <code>null</code> if a matching document library file entry metadata could not be found
	 */
	@Override
	public DLFileEntryMetadata fetchByUuid_First(
		String uuid, OrderByComparator<DLFileEntryMetadata> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the document library file entry metadatas where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of document library file entry metadatas where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching document library file entry metadatas
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<DLFileEntryMetadata, NoSuchFileEntryMetadataException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the document library file entry metadatas where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file entry metadatas
	 * @param end the upper bound of the range of document library file entry metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entry metadatas
	 */
	@Override
	public List<DLFileEntryMetadata> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFileEntryMetadata> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry metadata in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry metadata
	 * @throws NoSuchFileEntryMetadataException if a matching document library file entry metadata could not be found
	 */
	@Override
	public DLFileEntryMetadata findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DLFileEntryMetadata> orderByComparator)
		throws NoSuchFileEntryMetadataException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry metadata in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry metadata, or <code>null</code> if a matching document library file entry metadata could not be found
	 */
	@Override
	public DLFileEntryMetadata fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DLFileEntryMetadata> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the document library file entry metadatas where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of document library file entry metadatas where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching document library file entry metadatas
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<DLFileEntryMetadata, NoSuchFileEntryMetadataException>
			_collectionPersistenceFinderByFileEntryId;

	/**
	 * Returns an ordered range of all the document library file entry metadatas where fileEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryId the file entry ID
	 * @param start the lower bound of the range of document library file entry metadatas
	 * @param end the upper bound of the range of document library file entry metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entry metadatas
	 */
	@Override
	public List<DLFileEntryMetadata> findByFileEntryId(
		long fileEntryId, int start, int end,
		OrderByComparator<DLFileEntryMetadata> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByFileEntryId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {fileEntryId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry metadata in the ordered set where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry metadata
	 * @throws NoSuchFileEntryMetadataException if a matching document library file entry metadata could not be found
	 */
	@Override
	public DLFileEntryMetadata findByFileEntryId_First(
			long fileEntryId,
			OrderByComparator<DLFileEntryMetadata> orderByComparator)
		throws NoSuchFileEntryMetadataException {

		return _collectionPersistenceFinderByFileEntryId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {fileEntryId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry metadata in the ordered set where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry metadata, or <code>null</code> if a matching document library file entry metadata could not be found
	 */
	@Override
	public DLFileEntryMetadata fetchByFileEntryId_First(
		long fileEntryId,
		OrderByComparator<DLFileEntryMetadata> orderByComparator) {

		return _collectionPersistenceFinderByFileEntryId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {fileEntryId},
			orderByComparator);
	}

	/**
	 * Removes all the document library file entry metadatas where fileEntryId = &#63; from the database.
	 *
	 * @param fileEntryId the file entry ID
	 */
	@Override
	public void removeByFileEntryId(long fileEntryId) {
		_collectionPersistenceFinderByFileEntryId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {fileEntryId});
	}

	/**
	 * Returns the number of document library file entry metadatas where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @return the number of matching document library file entry metadatas
	 */
	@Override
	public int countByFileEntryId(long fileEntryId) {
		return _collectionPersistenceFinderByFileEntryId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {fileEntryId});
	}

	private CollectionPersistenceFinder
		<DLFileEntryMetadata, NoSuchFileEntryMetadataException>
			_collectionPersistenceFinderByFileVersionId;

	/**
	 * Returns an ordered range of all the document library file entry metadatas where fileVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param fileVersionId the file version ID
	 * @param start the lower bound of the range of document library file entry metadatas
	 * @param end the upper bound of the range of document library file entry metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entry metadatas
	 */
	@Override
	public List<DLFileEntryMetadata> findByFileVersionId(
		long fileVersionId, int start, int end,
		OrderByComparator<DLFileEntryMetadata> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByFileVersionId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {fileVersionId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry metadata in the ordered set where fileVersionId = &#63;.
	 *
	 * @param fileVersionId the file version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry metadata
	 * @throws NoSuchFileEntryMetadataException if a matching document library file entry metadata could not be found
	 */
	@Override
	public DLFileEntryMetadata findByFileVersionId_First(
			long fileVersionId,
			OrderByComparator<DLFileEntryMetadata> orderByComparator)
		throws NoSuchFileEntryMetadataException {

		return _collectionPersistenceFinderByFileVersionId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {fileVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry metadata in the ordered set where fileVersionId = &#63;.
	 *
	 * @param fileVersionId the file version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry metadata, or <code>null</code> if a matching document library file entry metadata could not be found
	 */
	@Override
	public DLFileEntryMetadata fetchByFileVersionId_First(
		long fileVersionId,
		OrderByComparator<DLFileEntryMetadata> orderByComparator) {

		return _collectionPersistenceFinderByFileVersionId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {fileVersionId},
			orderByComparator);
	}

	/**
	 * Removes all the document library file entry metadatas where fileVersionId = &#63; from the database.
	 *
	 * @param fileVersionId the file version ID
	 */
	@Override
	public void removeByFileVersionId(long fileVersionId) {
		_collectionPersistenceFinderByFileVersionId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {fileVersionId});
	}

	/**
	 * Returns the number of document library file entry metadatas where fileVersionId = &#63;.
	 *
	 * @param fileVersionId the file version ID
	 * @return the number of matching document library file entry metadatas
	 */
	@Override
	public int countByFileVersionId(long fileVersionId) {
		return _collectionPersistenceFinderByFileVersionId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {fileVersionId});
	}

	private UniquePersistenceFinder
		<DLFileEntryMetadata, NoSuchFileEntryMetadataException>
			_uniquePersistenceFinderByD_F;

	/**
	 * Returns the document library file entry metadata where DDMStructureId = &#63; and fileVersionId = &#63; or throws a <code>NoSuchFileEntryMetadataException</code> if it could not be found.
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @param fileVersionId the file version ID
	 * @return the matching document library file entry metadata
	 * @throws NoSuchFileEntryMetadataException if a matching document library file entry metadata could not be found
	 */
	@Override
	public DLFileEntryMetadata findByD_F(
			long DDMStructureId, long fileVersionId)
		throws NoSuchFileEntryMetadataException {

		return _uniquePersistenceFinderByD_F.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {DDMStructureId, fileVersionId});
	}

	/**
	 * Returns the document library file entry metadata where DDMStructureId = &#63; and fileVersionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @param fileVersionId the file version ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry metadata, or <code>null</code> if a matching document library file entry metadata could not be found
	 */
	@Override
	public DLFileEntryMetadata fetchByD_F(
		long DDMStructureId, long fileVersionId, boolean useFinderCache) {

		return _uniquePersistenceFinderByD_F.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {DDMStructureId, fileVersionId}, useFinderCache);
	}

	/**
	 * Removes the document library file entry metadata where DDMStructureId = &#63; and fileVersionId = &#63; from the database.
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @param fileVersionId the file version ID
	 * @return the document library file entry metadata that was removed
	 */
	@Override
	public DLFileEntryMetadata removeByD_F(
			long DDMStructureId, long fileVersionId)
		throws NoSuchFileEntryMetadataException {

		DLFileEntryMetadata dlFileEntryMetadata = findByD_F(
			DDMStructureId, fileVersionId);

		return remove(dlFileEntryMetadata);
	}

	/**
	 * Returns the number of document library file entry metadatas where DDMStructureId = &#63; and fileVersionId = &#63;.
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @param fileVersionId the file version ID
	 * @return the number of matching document library file entry metadatas
	 */
	@Override
	public int countByD_F(long DDMStructureId, long fileVersionId) {
		return _uniquePersistenceFinderByD_F.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {DDMStructureId, fileVersionId});
	}

	private UniquePersistenceFinder
		<DLFileEntryMetadata, NoSuchFileEntryMetadataException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the document library file entry metadata where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchFileEntryMetadataException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching document library file entry metadata
	 * @throws NoSuchFileEntryMetadataException if a matching document library file entry metadata could not be found
	 */
	@Override
	public DLFileEntryMetadata findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchFileEntryMetadataException {

		return _uniquePersistenceFinderByERC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the document library file entry metadata where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry metadata, or <code>null</code> if a matching document library file entry metadata could not be found
	 */
	@Override
	public DLFileEntryMetadata fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId}, useFinderCache);
	}

	/**
	 * Removes the document library file entry metadata where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the document library file entry metadata that was removed
	 */
	@Override
	public DLFileEntryMetadata removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchFileEntryMetadataException {

		DLFileEntryMetadata dlFileEntryMetadata = findByERC_C(
			externalReferenceCode, companyId);

		return remove(dlFileEntryMetadata);
	}

	/**
	 * Returns the number of document library file entry metadatas where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching document library file entry metadatas
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId});
	}

	public DLFileEntryMetadataPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DLFileEntryMetadata.class);

		setModelImplClass(DLFileEntryMetadataImpl.class);
		setModelPKClass(long.class);

		setTable(DLFileEntryMetadataTable.INSTANCE);
	}

	/**
	 * Creates a new document library file entry metadata with the primary key. Does not add the document library file entry metadata to the database.
	 *
	 * @param fileEntryMetadataId the primary key for the new document library file entry metadata
	 * @return the new document library file entry metadata
	 */
	@Override
	public DLFileEntryMetadata create(long fileEntryMetadataId) {
		DLFileEntryMetadata dlFileEntryMetadata = new DLFileEntryMetadataImpl();

		dlFileEntryMetadata.setNew(true);
		dlFileEntryMetadata.setPrimaryKey(fileEntryMetadataId);

		String uuid = PortalUUIDUtil.generate();

		dlFileEntryMetadata.setUuid(uuid);

		dlFileEntryMetadata.setCompanyId(CompanyThreadLocal.getCompanyId());

		return dlFileEntryMetadata;
	}

	/**
	 * Removes the document library file entry metadata with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fileEntryMetadataId the primary key of the document library file entry metadata
	 * @return the document library file entry metadata that was removed
	 * @throws NoSuchFileEntryMetadataException if a document library file entry metadata with the primary key could not be found
	 */
	@Override
	public DLFileEntryMetadata remove(long fileEntryMetadataId)
		throws NoSuchFileEntryMetadataException {

		return remove((Serializable)fileEntryMetadataId);
	}

	@Override
	protected DLFileEntryMetadata removeImpl(
		DLFileEntryMetadata dlFileEntryMetadata) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dlFileEntryMetadata)) {
				dlFileEntryMetadata = (DLFileEntryMetadata)session.get(
					DLFileEntryMetadataImpl.class,
					dlFileEntryMetadata.getPrimaryKeyObj());
			}

			if ((dlFileEntryMetadata != null) &&
				CTPersistenceHelperUtil.isRemove(dlFileEntryMetadata)) {

				session.delete(dlFileEntryMetadata);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dlFileEntryMetadata != null) {
			clearCache(dlFileEntryMetadata);
		}

		return dlFileEntryMetadata;
	}

	@Override
	public DLFileEntryMetadata updateImpl(
		DLFileEntryMetadata dlFileEntryMetadata) {

		boolean isNew = dlFileEntryMetadata.isNew();

		if (!(dlFileEntryMetadata instanceof DLFileEntryMetadataModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dlFileEntryMetadata.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					dlFileEntryMetadata);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dlFileEntryMetadata proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DLFileEntryMetadata implementation " +
					dlFileEntryMetadata.getClass());
		}

		DLFileEntryMetadataModelImpl dlFileEntryMetadataModelImpl =
			(DLFileEntryMetadataModelImpl)dlFileEntryMetadata;

		if (Validator.isNull(dlFileEntryMetadata.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			dlFileEntryMetadata.setUuid(uuid);
		}

		if (Validator.isNull(dlFileEntryMetadata.getExternalReferenceCode())) {
			dlFileEntryMetadata.setExternalReferenceCode(
				dlFileEntryMetadata.getUuid());
		}
		else {
			if (!Objects.equals(
					dlFileEntryMetadataModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					dlFileEntryMetadata.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = dlFileEntryMetadata.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = dlFileEntryMetadata.getPrimaryKey();
					}

					try {
						dlFileEntryMetadata.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								DLFileEntryMetadata.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								dlFileEntryMetadata.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			DLFileEntryMetadata ercDLFileEntryMetadata = fetchByERC_C(
				dlFileEntryMetadata.getExternalReferenceCode(),
				dlFileEntryMetadata.getCompanyId());

			if (isNew) {
				if (ercDLFileEntryMetadata != null) {
					throw new DuplicateDLFileEntryMetadataExternalReferenceCodeException(
						"Duplicate document library file entry metadata with external reference code " +
							dlFileEntryMetadata.getExternalReferenceCode() +
								" and company " +
									dlFileEntryMetadata.getCompanyId());
				}
			}
			else {
				if ((ercDLFileEntryMetadata != null) &&
					(dlFileEntryMetadata.getFileEntryMetadataId() !=
						ercDLFileEntryMetadata.getFileEntryMetadataId())) {

					throw new DuplicateDLFileEntryMetadataExternalReferenceCodeException(
						"Duplicate document library file entry metadata with external reference code " +
							dlFileEntryMetadata.getExternalReferenceCode() +
								" and company " +
									dlFileEntryMetadata.getCompanyId());
				}
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(dlFileEntryMetadata)) {
				if (!isNew) {
					session.evict(
						DLFileEntryMetadataImpl.class,
						dlFileEntryMetadata.getPrimaryKeyObj());
				}

				session.save(dlFileEntryMetadata);
			}
			else {
				dlFileEntryMetadata = (DLFileEntryMetadata)session.merge(
					dlFileEntryMetadata);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(dlFileEntryMetadata, false);

		if (isNew) {
			dlFileEntryMetadata.setNew(false);
		}

		dlFileEntryMetadata.resetOriginalValues();

		return dlFileEntryMetadata;
	}

	/**
	 * Returns the document library file entry metadata with the primary key or throws a <code>NoSuchFileEntryMetadataException</code> if it could not be found.
	 *
	 * @param fileEntryMetadataId the primary key of the document library file entry metadata
	 * @return the document library file entry metadata
	 * @throws NoSuchFileEntryMetadataException if a document library file entry metadata with the primary key could not be found
	 */
	@Override
	public DLFileEntryMetadata findByPrimaryKey(long fileEntryMetadataId)
		throws NoSuchFileEntryMetadataException {

		return findByPrimaryKey((Serializable)fileEntryMetadataId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the document library file entry metadata with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fileEntryMetadataId the primary key of the document library file entry metadata
	 * @return the document library file entry metadata, or <code>null</code> if a document library file entry metadata with the primary key could not be found
	 */
	@Override
	public DLFileEntryMetadata fetchByPrimaryKey(long fileEntryMetadataId) {
		return fetchByPrimaryKey((Serializable)fileEntryMetadataId);
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
		return "fileEntryMetadataId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DLFILEENTRYMETADATA;
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
		return DLFileEntryMetadataModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DLFileEntryMetadata";
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
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("companyId");
		ctMergeColumnNames.add("DDMStorageId");
		ctMergeColumnNames.add("DDMStructureId");
		ctMergeColumnNames.add("fileEntryId");
		ctMergeColumnNames.add("fileVersionId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("fileEntryMetadataId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"DDMStructureId", "fileVersionId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the document library file entry metadata persistence.
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
			_SQL_SELECT_DLFILEENTRYMETADATA_WHERE,
			_SQL_COUNT_DLFILEENTRYMETADATA_WHERE,
			DLFileEntryMetadataModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"dlFileEntryMetadata.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				DLFileEntryMetadata::getUuid));

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
				_SQL_SELECT_DLFILEENTRYMETADATA_WHERE,
				_SQL_COUNT_DLFILEENTRYMETADATA_WHERE,
				DLFileEntryMetadataModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"dlFileEntryMetadata.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					DLFileEntryMetadata::getUuid),
				new FinderColumn<>(
					"dlFileEntryMetadata.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, DLFileEntryMetadata::getCompanyId));

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
				_SQL_SELECT_DLFILEENTRYMETADATA_WHERE,
				_SQL_COUNT_DLFILEENTRYMETADATA_WHERE,
				DLFileEntryMetadataModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"dlFileEntryMetadata.", "fileEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					DLFileEntryMetadata::getFileEntryId));

		_collectionPersistenceFinderByFileVersionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByFileVersionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"fileVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByFileVersionId", new String[] {Long.class.getName()},
					new String[] {"fileVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByFileVersionId", new String[] {Long.class.getName()},
					new String[] {"fileVersionId"}, false),
				_SQL_SELECT_DLFILEENTRYMETADATA_WHERE,
				_SQL_COUNT_DLFILEENTRYMETADATA_WHERE,
				DLFileEntryMetadataModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"dlFileEntryMetadata.", "fileVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					DLFileEntryMetadata::getFileVersionId));

		_uniquePersistenceFinderByD_F = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByD_F",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"DDMStructureId", "fileVersionId"}, 0, 0, false,
				DLFileEntryMetadata::getDDMStructureId,
				DLFileEntryMetadata::getFileVersionId),
			_SQL_SELECT_DLFILEENTRYMETADATA_WHERE, "",
			new FinderColumn<>(
				"dlFileEntryMetadata.", "DDMStructureId",
				FinderColumn.Type.LONG, "=", true, true,
				DLFileEntryMetadata::getDDMStructureId),
			new FinderColumn<>(
				"dlFileEntryMetadata.", "fileVersionId", FinderColumn.Type.LONG,
				"=", true, true, DLFileEntryMetadata::getFileVersionId));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					DLFileEntryMetadata::getExternalReferenceCode),
				DLFileEntryMetadata::getCompanyId),
			_SQL_SELECT_DLFILEENTRYMETADATA_WHERE, "",
			new FinderColumn<>(
				"dlFileEntryMetadata.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				DLFileEntryMetadata::getExternalReferenceCode),
			new FinderColumn<>(
				"dlFileEntryMetadata.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, DLFileEntryMetadata::getCompanyId));

		DLFileEntryMetadataUtil.setPersistence(this);
	}

	public void destroy() {
		DLFileEntryMetadataUtil.setPersistence(null);

		EntityCacheUtil.removeCache(DLFileEntryMetadataImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		DLFileEntryMetadataModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DLFILEENTRYMETADATA =
		"SELECT dlFileEntryMetadata FROM DLFileEntryMetadata dlFileEntryMetadata";

	private static final String _SQL_SELECT_DLFILEENTRYMETADATA_WHERE =
		"SELECT dlFileEntryMetadata FROM DLFileEntryMetadata dlFileEntryMetadata WHERE ";

	private static final String _SQL_COUNT_DLFILEENTRYMETADATA_WHERE =
		"SELECT COUNT(dlFileEntryMetadata) FROM DLFileEntryMetadata dlFileEntryMetadata WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1487475230