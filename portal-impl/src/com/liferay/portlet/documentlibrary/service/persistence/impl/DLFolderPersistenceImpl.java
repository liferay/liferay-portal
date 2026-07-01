/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.persistence.impl;

import com.liferay.document.library.kernel.exception.DuplicateDLFolderExternalReferenceCodeException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderTable;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryTypePersistence;
import com.liferay.document.library.kernel.service.persistence.DLFolderPersistence;
import com.liferay.document.library.kernel.service.persistence.DLFolderUtil;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portlet.documentlibrary.model.impl.DLFolderImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl;

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
 * The persistence implementation for the document library folder service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DLFolderPersistenceImpl
	extends BasePersistenceImpl<DLFolder, NoSuchFolderException>
	implements DLFolderPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DLFolderUtil</code> to access the document library folder persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DLFolderImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<DLFolder, NoSuchFolderException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the document library folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByUuid_First(
			String uuid, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first document library folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByUuid_First(
		String uuid, OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the document library folders where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of document library folders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private UniquePersistenceFinder<DLFolder, NoSuchFolderException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the document library folder where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByUUID_G(String uuid, long groupId)
		throws NoSuchFolderException {

		return _uniquePersistenceFinderByUUID_G.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	/**
	 * Returns the document library folder where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId},
			useFinderCache);
	}

	/**
	 * Removes the document library folder where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the document library folder that was removed
	 */
	@Override
	public DLFolder removeByUUID_G(String uuid, long groupId)
		throws NoSuchFolderException {

		DLFolder dlFolder = findByUUID_G(uuid, groupId);

		return remove(dlFolder);
	}

	/**
	 * Returns the number of document library folders where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<DLFolder, NoSuchFolderException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the document library folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first document library folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the document library folders where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of document library folders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder<DLFolder, NoSuchFolderException>
		_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByGroupId_First(
			long groupId, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByGroupId_First(
		long groupId, OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the document library folders where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of document library folders where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching document library folders that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, groupId);
	}

	private CollectionPersistenceFinder<DLFolder, NoSuchFolderException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the document library folders where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByCompanyId_First(
			long companyId, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first document library folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByCompanyId_First(
		long companyId, OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the document library folders where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of document library folders where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private CollectionPersistenceFinder<DLFolder, NoSuchFolderException>
		_collectionPersistenceFinderByRepositoryId;

	/**
	 * Returns an ordered range of all the document library folders where repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByRepositoryId(
		long repositoryId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByRepositoryId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {repositoryId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library folder in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByRepositoryId_First(
			long repositoryId, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByRepositoryId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {repositoryId},
			orderByComparator);
	}

	/**
	 * Returns the first document library folder in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByRepositoryId_First(
		long repositoryId, OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByRepositoryId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {repositoryId},
			orderByComparator);
	}

	/**
	 * Removes all the document library folders where repositoryId = &#63; from the database.
	 *
	 * @param repositoryId the repository ID
	 */
	@Override
	public void removeByRepositoryId(long repositoryId) {
		_collectionPersistenceFinderByRepositoryId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {repositoryId});
	}

	/**
	 * Returns the number of document library folders where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByRepositoryId(long repositoryId) {
		return _collectionPersistenceFinderByRepositoryId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {repositoryId});
	}

	private FilterCollectionPersistenceFinder<DLFolder, NoSuchFolderException>
		_collectionPersistenceFinderByG_P;

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_P(
		long groupId, long parentFolderId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentFolderId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_P_First(
			long groupId, long parentFolderId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByG_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentFolderId}, orderByComparator);
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_P_First(
		long groupId, long parentFolderId,
		OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentFolderId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_P(
		long groupId, long parentFolderId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_P.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentFolderId}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the document library folders where groupId = &#63; and parentFolderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 */
	@Override
	public void removeByG_P(long groupId, long parentFolderId) {
		_collectionPersistenceFinderByG_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentFolderId});
	}

	/**
	 * Returns the number of document library folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByG_P(long groupId, long parentFolderId) {
		return _collectionPersistenceFinderByG_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentFolderId});
	}

	/**
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching document library folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(long groupId, long parentFolderId) {
		return _collectionPersistenceFinderByG_P.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentFolderId}, groupId);
	}

	private CollectionPersistenceFinder<DLFolder, NoSuchFolderException>
		_collectionPersistenceFinderByC_NotS;

	/**
	 * Returns all the document library folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByC_NotS(long companyId, int status) {
		return findByC_NotS(
			companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByC_NotS(
		long companyId, int status, int start, int end) {

		return findByC_NotS(companyId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return findByC_NotS(
			companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_NotS.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library folder in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByC_NotS_First(
			long companyId, int status,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByC_NotS.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, status},
			orderByComparator);
	}

	/**
	 * Returns the first document library folder in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByC_NotS_First(
		long companyId, int status,
		OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByC_NotS.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, status},
			orderByComparator);
	}

	/**
	 * Removes all the document library folders where companyId = &#63; and status &ne; &#63; from the database.
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
	 * Returns the number of document library folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByC_NotS(long companyId, int status) {
		return _collectionPersistenceFinderByC_NotS.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, status});
	}

	private UniquePersistenceFinder<DLFolder, NoSuchFolderException>
		_uniquePersistenceFinderByR_M;

	/**
	 * Returns the document library folder where repositoryId = &#63; and mountPoint = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param repositoryId the repository ID
	 * @param mountPoint the mount point
	 * @return the matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByR_M(long repositoryId, boolean mountPoint)
		throws NoSuchFolderException {

		return _uniquePersistenceFinderByR_M.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {repositoryId, mountPoint});
	}

	/**
	 * Returns the document library folder where repositoryId = &#63; and mountPoint = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param repositoryId the repository ID
	 * @param mountPoint the mount point
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByR_M(
		long repositoryId, boolean mountPoint, boolean useFinderCache) {

		return _uniquePersistenceFinderByR_M.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {repositoryId, mountPoint}, useFinderCache);
	}

	/**
	 * Removes the document library folder where repositoryId = &#63; and mountPoint = &#63; from the database.
	 *
	 * @param repositoryId the repository ID
	 * @param mountPoint the mount point
	 * @return the document library folder that was removed
	 */
	@Override
	public DLFolder removeByR_M(long repositoryId, boolean mountPoint)
		throws NoSuchFolderException {

		DLFolder dlFolder = findByR_M(repositoryId, mountPoint);

		return remove(dlFolder);
	}

	/**
	 * Returns the number of document library folders where repositoryId = &#63; and mountPoint = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param mountPoint the mount point
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByR_M(long repositoryId, boolean mountPoint) {
		return _uniquePersistenceFinderByR_M.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {repositoryId, mountPoint});
	}

	private CollectionPersistenceFinder<DLFolder, NoSuchFolderException>
		_collectionPersistenceFinderByR_P;

	/**
	 * Returns an ordered range of all the document library folders where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByR_P(
		long repositoryId, long parentFolderId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByR_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {repositoryId, parentFolderId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library folder in the ordered set where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByR_P_First(
			long repositoryId, long parentFolderId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByR_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {repositoryId, parentFolderId}, orderByComparator);
	}

	/**
	 * Returns the first document library folder in the ordered set where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByR_P_First(
		long repositoryId, long parentFolderId,
		OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByR_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {repositoryId, parentFolderId}, orderByComparator);
	}

	/**
	 * Removes all the document library folders where repositoryId = &#63; and parentFolderId = &#63; from the database.
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 */
	@Override
	public void removeByR_P(long repositoryId, long parentFolderId) {
		_collectionPersistenceFinderByR_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {repositoryId, parentFolderId});
	}

	/**
	 * Returns the number of document library folders where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByR_P(long repositoryId, long parentFolderId) {
		return _collectionPersistenceFinderByR_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {repositoryId, parentFolderId});
	}

	private CollectionPersistenceFinder<DLFolder, NoSuchFolderException>
		_collectionPersistenceFinderByP_N;

	/**
	 * Returns an ordered range of all the document library folders where parentFolderId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByP_N(
		long parentFolderId, String name, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByP_N.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {parentFolderId, name}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first document library folder in the ordered set where parentFolderId = &#63; and name = &#63;.
	 *
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByP_N_First(
			long parentFolderId, String name,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByP_N.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {parentFolderId, name}, orderByComparator);
	}

	/**
	 * Returns the first document library folder in the ordered set where parentFolderId = &#63; and name = &#63;.
	 *
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByP_N_First(
		long parentFolderId, String name,
		OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByP_N.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {parentFolderId, name}, orderByComparator);
	}

	/**
	 * Removes all the document library folders where parentFolderId = &#63; and name = &#63; from the database.
	 *
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 */
	@Override
	public void removeByP_N(long parentFolderId, String name) {
		_collectionPersistenceFinderByP_N.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {parentFolderId, name});
	}

	/**
	 * Returns the number of document library folders where parentFolderId = &#63; and name = &#63;.
	 *
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByP_N(long parentFolderId, String name) {
		return _collectionPersistenceFinderByP_N.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {parentFolderId, name});
	}

	private CollectionPersistenceFinder<DLFolder, NoSuchFolderException>
		_collectionPersistenceFinderByGtF_C_P;

	/**
	 * Returns all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByGtF_C_P(
		long folderId, long companyId, long parentFolderId) {

		return findByGtF_C_P(
			folderId, companyId, parentFolderId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByGtF_C_P(
		long folderId, long companyId, long parentFolderId, int start,
		int end) {

		return findByGtF_C_P(
			folderId, companyId, parentFolderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByGtF_C_P(
		long folderId, long companyId, long parentFolderId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return findByGtF_C_P(
			folderId, companyId, parentFolderId, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByGtF_C_P(
		long folderId, long companyId, long parentFolderId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByGtF_C_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {folderId, companyId, parentFolderId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByGtF_C_P_First(
			long folderId, long companyId, long parentFolderId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByGtF_C_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {folderId, companyId, parentFolderId},
			orderByComparator);
	}

	/**
	 * Returns the first document library folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByGtF_C_P_First(
		long folderId, long companyId, long parentFolderId,
		OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByGtF_C_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {folderId, companyId, parentFolderId},
			orderByComparator);
	}

	/**
	 * Removes all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; from the database.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 */
	@Override
	public void removeByGtF_C_P(
		long folderId, long companyId, long parentFolderId) {

		_collectionPersistenceFinderByGtF_C_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {folderId, companyId, parentFolderId});
	}

	/**
	 * Returns the number of document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByGtF_C_P(
		long folderId, long companyId, long parentFolderId) {

		return _collectionPersistenceFinderByGtF_C_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {folderId, companyId, parentFolderId});
	}

	private FilterCollectionPersistenceFinder<DLFolder, NoSuchFolderException>
		_collectionPersistenceFinderByG_M_P;

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId, int start,
		int end, OrderByComparator<DLFolder> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_M_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_M_P_First(
			long groupId, boolean mountPoint, long parentFolderId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByG_M_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId},
			orderByComparator);
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_M_P_First(
		long groupId, boolean mountPoint, long parentFolderId,
		OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_M_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId, int start,
		int end, OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_M_P.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 */
	@Override
	public void removeByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId) {

		_collectionPersistenceFinderByG_M_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId});
	}

	/**
	 * Returns the number of document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId) {

		return _collectionPersistenceFinderByG_M_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId});
	}

	/**
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching document library folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId) {

		return _collectionPersistenceFinderByG_M_P.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId}, groupId);
	}

	private UniquePersistenceFinder<DLFolder, NoSuchFolderException>
		_uniquePersistenceFinderByG_P_N;

	/**
	 * Returns the document library folder where groupId = &#63; and parentFolderId = &#63; and name = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @return the matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_P_N(long groupId, long parentFolderId, String name)
		throws NoSuchFolderException {

		return _uniquePersistenceFinderByG_P_N.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentFolderId, name});
	}

	/**
	 * Returns the document library folder where groupId = &#63; and parentFolderId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_P_N(
		long groupId, long parentFolderId, String name,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_P_N.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentFolderId, name}, useFinderCache);
	}

	/**
	 * Removes the document library folder where groupId = &#63; and parentFolderId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @return the document library folder that was removed
	 */
	@Override
	public DLFolder removeByG_P_N(
			long groupId, long parentFolderId, String name)
		throws NoSuchFolderException {

		DLFolder dlFolder = findByG_P_N(groupId, parentFolderId, name);

		return remove(dlFolder);
	}

	/**
	 * Returns the number of document library folders where groupId = &#63; and parentFolderId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByG_P_N(long groupId, long parentFolderId, String name) {
		return _uniquePersistenceFinderByG_P_N.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentFolderId, name});
	}

	private CollectionPersistenceFinder<DLFolder, NoSuchFolderException>
		_collectionPersistenceFinderByGtF_C_P_NotS;

	/**
	 * Returns all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status) {

		return findByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status,
		int start, int end) {

		return findByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status,
		int start, int end, OrderByComparator<DLFolder> orderByComparator) {

		return findByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status,
		int start, int end, OrderByComparator<DLFolder> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGtF_C_P_NotS.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {folderId, companyId, parentFolderId, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByGtF_C_P_NotS_First(
			long folderId, long companyId, long parentFolderId, int status,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByGtF_C_P_NotS.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {folderId, companyId, parentFolderId, status},
			orderByComparator);
	}

	/**
	 * Returns the first document library folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByGtF_C_P_NotS_First(
		long folderId, long companyId, long parentFolderId, int status,
		OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByGtF_C_P_NotS.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {folderId, companyId, parentFolderId, status},
			orderByComparator);
	}

	/**
	 * Removes all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 */
	@Override
	public void removeByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status) {

		_collectionPersistenceFinderByGtF_C_P_NotS.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {folderId, companyId, parentFolderId, status});
	}

	/**
	 * Returns the number of document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status) {

		return _collectionPersistenceFinderByGtF_C_P_NotS.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {folderId, companyId, parentFolderId, status});
	}

	private FilterCollectionPersistenceFinder<DLFolder, NoSuchFolderException>
		_collectionPersistenceFinderByG_M_P_H;

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int start, int end, OrderByComparator<DLFolder> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_M_P_H.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId, hidden}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_M_P_H_First(
			long groupId, boolean mountPoint, long parentFolderId,
			boolean hidden, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByG_M_P_H.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId, hidden},
			orderByComparator);
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_M_P_H_First(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_M_P_H.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId, hidden},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int start, int end, OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_M_P_H.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId, hidden}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 */
	@Override
	public void removeByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden) {

		_collectionPersistenceFinderByG_M_P_H.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId, hidden});
	}

	/**
	 * Returns the number of document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden) {

		return _collectionPersistenceFinderByG_M_P_H.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId, hidden});
	}

	/**
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @return the number of matching document library folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden) {

		return _collectionPersistenceFinderByG_M_P_H.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId, hidden},
			groupId);
	}

	private FilterCollectionPersistenceFinder<DLFolder, NoSuchFolderException>
		_collectionPersistenceFinderByG_M_LikeT_H;

	/**
	 * Returns all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden) {

		return findByG_M_LikeT_H(
			groupId, mountPoint, treePath, hidden, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int start, int end) {

		return findByG_M_LikeT_H(
			groupId, mountPoint, treePath, hidden, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int start, int end, OrderByComparator<DLFolder> orderByComparator) {

		return findByG_M_LikeT_H(
			groupId, mountPoint, treePath, hidden, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int start, int end, OrderByComparator<DLFolder> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_M_LikeT_H.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, treePath, hidden}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_M_LikeT_H_First(
			long groupId, boolean mountPoint, String treePath, boolean hidden,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByG_M_LikeT_H.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, treePath, hidden},
			orderByComparator);
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_M_LikeT_H_First(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_M_LikeT_H.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, treePath, hidden},
			orderByComparator);
	}

	/**
	 * Returns all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @return the matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden) {

		return filterFindByG_M_LikeT_H(
			groupId, mountPoint, treePath, hidden, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int start, int end) {

		return filterFindByG_M_LikeT_H(
			groupId, mountPoint, treePath, hidden, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int start, int end, OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_M_LikeT_H.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, treePath, hidden}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 */
	@Override
	public void removeByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden) {

		_collectionPersistenceFinderByG_M_LikeT_H.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, treePath, hidden});
	}

	/**
	 * Returns the number of document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden) {

		return _collectionPersistenceFinderByG_M_LikeT_H.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, treePath, hidden});
	}

	/**
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @return the number of matching document library folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden) {

		return _collectionPersistenceFinderByG_M_LikeT_H.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, treePath, hidden}, groupId);
	}

	private FilterCollectionPersistenceFinder<DLFolder, NoSuchFolderException>
		_collectionPersistenceFinderByG_P_H_S;

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status,
		int start, int end, OrderByComparator<DLFolder> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_H_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentFolderId, hidden, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_P_H_S_First(
			long groupId, long parentFolderId, boolean hidden, int status,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByG_P_H_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentFolderId, hidden, status},
			orderByComparator);
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_P_H_S_First(
		long groupId, long parentFolderId, boolean hidden, int status,
		OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_P_H_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentFolderId, hidden, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status,
		int start, int end, OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_P_H_S.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentFolderId, hidden, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the document library folders where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 */
	@Override
	public void removeByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status) {

		_collectionPersistenceFinderByG_P_H_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentFolderId, hidden, status});
	}

	/**
	 * Returns the number of document library folders where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status) {

		return _collectionPersistenceFinderByG_P_H_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentFolderId, hidden, status});
	}

	/**
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @return the number of matching document library folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status) {

		return _collectionPersistenceFinderByG_P_H_S.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentFolderId, hidden, status}, groupId);
	}

	private FilterCollectionPersistenceFinder<DLFolder, NoSuchFolderException>
		_collectionPersistenceFinderByG_M_P_H_S;

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_M_P_H_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId, hidden, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_M_P_H_S_First(
			long groupId, boolean mountPoint, long parentFolderId,
			boolean hidden, int status,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByG_M_P_H_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId, hidden, status},
			orderByComparator);
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_M_P_H_S_First(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status, OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_M_P_H_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId, hidden, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_M_P_H_S.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId, hidden, status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 */
	@Override
	public void removeByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status) {

		_collectionPersistenceFinderByG_M_P_H_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId, hidden, status});
	}

	/**
	 * Returns the number of document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status) {

		return _collectionPersistenceFinderByG_M_P_H_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId, hidden, status});
	}

	/**
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @return the number of matching document library folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status) {

		return _collectionPersistenceFinderByG_M_P_H_S.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, parentFolderId, hidden, status},
			groupId);
	}

	private FilterCollectionPersistenceFinder<DLFolder, NoSuchFolderException>
		_collectionPersistenceFinderByG_M_LikeT_H_NotS;

	/**
	 * Returns all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status) {

		return findByG_M_LikeT_H_NotS(
			groupId, mountPoint, treePath, hidden, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status, int start, int end) {

		return findByG_M_LikeT_H_NotS(
			groupId, mountPoint, treePath, hidden, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return findByG_M_LikeT_H_NotS(
			groupId, mountPoint, treePath, hidden, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_M_LikeT_H_NotS.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, treePath, hidden, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_M_LikeT_H_NotS_First(
			long groupId, boolean mountPoint, String treePath, boolean hidden,
			int status, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByG_M_LikeT_H_NotS.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, treePath, hidden, status},
			orderByComparator);
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_M_LikeT_H_NotS_First(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status, OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_M_LikeT_H_NotS.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, treePath, hidden, status},
			orderByComparator);
	}

	/**
	 * Returns all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @return the matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status) {

		return filterFindByG_M_LikeT_H_NotS(
			groupId, mountPoint, treePath, hidden, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status, int start, int end) {

		return filterFindByG_M_LikeT_H_NotS(
			groupId, mountPoint, treePath, hidden, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_M_LikeT_H_NotS.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, treePath, hidden, status}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 */
	@Override
	public void removeByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status) {

		_collectionPersistenceFinderByG_M_LikeT_H_NotS.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, treePath, hidden, status});
	}

	/**
	 * Returns the number of document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status) {

		return _collectionPersistenceFinderByG_M_LikeT_H_NotS.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, treePath, hidden, status});
	}

	/**
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @return the number of matching document library folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status) {

		return _collectionPersistenceFinderByG_M_LikeT_H_NotS.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, mountPoint, treePath, hidden, status},
			groupId);
	}

	private UniquePersistenceFinder<DLFolder, NoSuchFolderException>
		_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the document library folder where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchFolderException {

		return _uniquePersistenceFinderByERC_G.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the document library folder where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId}, useFinderCache);
	}

	/**
	 * Removes the document library folder where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the document library folder that was removed
	 */
	@Override
	public DLFolder removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchFolderException {

		DLFolder dlFolder = findByERC_G(externalReferenceCode, groupId);

		return remove(dlFolder);
	}

	/**
	 * Returns the number of document library folders where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	public DLFolderPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("hidden", "hidden_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DLFolder.class);

		setModelImplClass(DLFolderImpl.class);
		setModelPKClass(long.class);

		setTable(DLFolderTable.INSTANCE);
	}

	/**
	 * Creates a new document library folder with the primary key. Does not add the document library folder to the database.
	 *
	 * @param folderId the primary key for the new document library folder
	 * @return the new document library folder
	 */
	@Override
	public DLFolder create(long folderId) {
		DLFolder dlFolder = new DLFolderImpl();

		dlFolder.setNew(true);
		dlFolder.setPrimaryKey(folderId);

		String uuid = PortalUUIDUtil.generate();

		dlFolder.setUuid(uuid);

		dlFolder.setCompanyId(CompanyThreadLocal.getCompanyId());

		return dlFolder;
	}

	/**
	 * Removes the document library folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param folderId the primary key of the document library folder
	 * @return the document library folder that was removed
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder remove(long folderId) throws NoSuchFolderException {
		return remove((Serializable)folderId);
	}

	@Override
	protected DLFolder removeImpl(DLFolder dlFolder) {
		dlFolderToDLFileEntryTypeTableMapper.deleteLeftPrimaryKeyTableMappings(
			dlFolder.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dlFolder)) {
				dlFolder = (DLFolder)session.get(
					DLFolderImpl.class, dlFolder.getPrimaryKeyObj());
			}

			if ((dlFolder != null) &&
				CTPersistenceHelperUtil.isRemove(dlFolder)) {

				session.delete(dlFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dlFolder != null) {
			clearCache(dlFolder);
		}

		return dlFolder;
	}

	@Override
	public DLFolder updateImpl(DLFolder dlFolder) {
		boolean isNew = dlFolder.isNew();

		if (!(dlFolder instanceof DLFolderModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dlFolder.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(dlFolder);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dlFolder proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DLFolder implementation " +
					dlFolder.getClass());
		}

		DLFolderModelImpl dlFolderModelImpl = (DLFolderModelImpl)dlFolder;

		if (Validator.isNull(dlFolder.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			dlFolder.setUuid(uuid);
		}

		if (Validator.isNull(dlFolder.getExternalReferenceCode())) {
			dlFolder.setExternalReferenceCode(dlFolder.getUuid());
		}
		else {
			if (!Objects.equals(
					dlFolderModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					dlFolder.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = dlFolder.getCompanyId();

					long groupId = dlFolder.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = dlFolder.getPrimaryKey();
					}

					try {
						dlFolder.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								DLFolder.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								dlFolder.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			DLFolder ercDLFolder = fetchByERC_G(
				dlFolder.getExternalReferenceCode(), dlFolder.getGroupId());

			if (isNew) {
				if (ercDLFolder != null) {
					throw new DuplicateDLFolderExternalReferenceCodeException(
						"Duplicate document library folder with external reference code " +
							dlFolder.getExternalReferenceCode() +
								" and group " + dlFolder.getGroupId());
				}
			}
			else {
				if ((ercDLFolder != null) &&
					(dlFolder.getFolderId() != ercDLFolder.getFolderId())) {

					throw new DuplicateDLFolderExternalReferenceCodeException(
						"Duplicate document library folder with external reference code " +
							dlFolder.getExternalReferenceCode() +
								" and group " + dlFolder.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (dlFolder.getCreateDate() == null)) {
			if (serviceContext == null) {
				dlFolder.setCreateDate(date);
			}
			else {
				dlFolder.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!dlFolderModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				dlFolder.setModifiedDate(date);
			}
			else {
				dlFolder.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(dlFolder)) {
				if (!isNew) {
					session.evict(
						DLFolderImpl.class, dlFolder.getPrimaryKeyObj());
				}

				session.save(dlFolder);
			}
			else {
				dlFolder = (DLFolder)session.merge(dlFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(dlFolder, false);

		if (isNew) {
			dlFolder.setNew(false);
		}

		dlFolder.resetOriginalValues();

		return dlFolder;
	}

	/**
	 * Returns the document library folder with the primary key or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param folderId the primary key of the document library folder
	 * @return the document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder findByPrimaryKey(long folderId)
		throws NoSuchFolderException {

		return findByPrimaryKey((Serializable)folderId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the document library folder with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param folderId the primary key of the document library folder
	 * @return the document library folder, or <code>null</code> if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder fetchByPrimaryKey(long folderId) {
		return fetchByPrimaryKey((Serializable)folderId);
	}

	/**
	 * Returns the primaryKeys of document library file entry types associated with the document library folder.
	 *
	 * @param pk the primary key of the document library folder
	 * @return long[] of the primaryKeys of document library file entry types associated with the document library folder
	 */
	@Override
	public long[] getDLFileEntryTypePrimaryKeys(long pk) {
		long[] pks = dlFolderToDLFileEntryTypeTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the document library file entry types associated with the document library folder.
	 *
	 * @param pk the primary key of the document library folder
	 * @return the document library file entry types associated with the document library folder
	 */
	@Override
	public List<com.liferay.document.library.kernel.model.DLFileEntryType>
		getDLFileEntryTypes(long pk) {

		return getDLFileEntryTypes(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the document library file entry types associated with the document library folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the document library folder
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of document library file entry types associated with the document library folder
	 */
	@Override
	public List<com.liferay.document.library.kernel.model.DLFileEntryType>
		getDLFileEntryTypes(long pk, int start, int end) {

		return getDLFileEntryTypes(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entry types associated with the document library folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the document library folder
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of document library file entry types associated with the document library folder
	 */
	@Override
	public List<com.liferay.document.library.kernel.model.DLFileEntryType>
		getDLFileEntryTypes(
			long pk, int start, int end,
			OrderByComparator
				<com.liferay.document.library.kernel.model.DLFileEntryType>
					orderByComparator) {

		return dlFolderToDLFileEntryTypeTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of document library file entry types associated with the document library folder.
	 *
	 * @param pk the primary key of the document library folder
	 * @return the number of document library file entry types associated with the document library folder
	 */
	@Override
	public int getDLFileEntryTypesSize(long pk) {
		long[] pks = dlFolderToDLFileEntryTypeTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the document library file entry type is associated with the document library folder.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypePK the primary key of the document library file entry type
	 * @return <code>true</code> if the document library file entry type is associated with the document library folder; <code>false</code> otherwise
	 */
	@Override
	public boolean containsDLFileEntryType(long pk, long dlFileEntryTypePK) {
		return dlFolderToDLFileEntryTypeTableMapper.containsTableMapping(
			pk, dlFileEntryTypePK);
	}

	/**
	 * Returns <code>true</code> if the document library folder has any document library file entry types associated with it.
	 *
	 * @param pk the primary key of the document library folder to check for associations with document library file entry types
	 * @return <code>true</code> if the document library folder has any document library file entry types associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsDLFileEntryTypes(long pk) {
		if (getDLFileEntryTypesSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the document library folder and the document library file entry type. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypePK the primary key of the document library file entry type
	 * @return <code>true</code> if an association between the document library folder and the document library file entry type was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addDLFileEntryType(long pk, long dlFileEntryTypePK) {
		DLFolder dlFolder = fetchByPrimaryKey(pk);

		if (dlFolder == null) {
			return dlFolderToDLFileEntryTypeTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, dlFileEntryTypePK);
		}
		else {
			return dlFolderToDLFileEntryTypeTableMapper.addTableMapping(
				dlFolder.getCompanyId(), pk, dlFileEntryTypePK);
		}
	}

	/**
	 * Adds an association between the document library folder and the document library file entry type. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryType the document library file entry type
	 * @return <code>true</code> if an association between the document library folder and the document library file entry type was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addDLFileEntryType(
		long pk,
		com.liferay.document.library.kernel.model.DLFileEntryType
			dlFileEntryType) {

		DLFolder dlFolder = fetchByPrimaryKey(pk);

		if (dlFolder == null) {
			return dlFolderToDLFileEntryTypeTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				dlFileEntryType.getPrimaryKey());
		}
		else {
			return dlFolderToDLFileEntryTypeTableMapper.addTableMapping(
				dlFolder.getCompanyId(), pk, dlFileEntryType.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the document library folder and the document library file entry types. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypePKs the primary keys of the document library file entry types
	 * @return <code>true</code> if at least one association between the document library folder and the document library file entry types was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addDLFileEntryTypes(long pk, long[] dlFileEntryTypePKs) {
		long companyId = 0;

		DLFolder dlFolder = fetchByPrimaryKey(pk);

		if (dlFolder == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = dlFolder.getCompanyId();
		}

		long[] addedKeys =
			dlFolderToDLFileEntryTypeTableMapper.addTableMappings(
				companyId, pk, dlFileEntryTypePKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the document library folder and the document library file entry types. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypes the document library file entry types
	 * @return <code>true</code> if at least one association between the document library folder and the document library file entry types was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addDLFileEntryTypes(
		long pk,
		List<com.liferay.document.library.kernel.model.DLFileEntryType>
			dlFileEntryTypes) {

		return addDLFileEntryTypes(
			pk,
			ListUtil.toLongArray(
				dlFileEntryTypes,
				com.liferay.document.library.kernel.model.DLFileEntryType.
					FILE_ENTRY_TYPE_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the document library folder and its document library file entry types. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder to clear the associated document library file entry types from
	 */
	@Override
	public void clearDLFileEntryTypes(long pk) {
		dlFolderToDLFileEntryTypeTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the document library folder and the document library file entry type. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypePK the primary key of the document library file entry type
	 */
	@Override
	public void removeDLFileEntryType(long pk, long dlFileEntryTypePK) {
		dlFolderToDLFileEntryTypeTableMapper.deleteTableMapping(
			pk, dlFileEntryTypePK);
	}

	/**
	 * Removes the association between the document library folder and the document library file entry type. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryType the document library file entry type
	 */
	@Override
	public void removeDLFileEntryType(
		long pk,
		com.liferay.document.library.kernel.model.DLFileEntryType
			dlFileEntryType) {

		dlFolderToDLFileEntryTypeTableMapper.deleteTableMapping(
			pk, dlFileEntryType.getPrimaryKey());
	}

	/**
	 * Removes the association between the document library folder and the document library file entry types. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypePKs the primary keys of the document library file entry types
	 */
	@Override
	public void removeDLFileEntryTypes(long pk, long[] dlFileEntryTypePKs) {
		dlFolderToDLFileEntryTypeTableMapper.deleteTableMappings(
			pk, dlFileEntryTypePKs);
	}

	/**
	 * Removes the association between the document library folder and the document library file entry types. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypes the document library file entry types
	 */
	@Override
	public void removeDLFileEntryTypes(
		long pk,
		List<com.liferay.document.library.kernel.model.DLFileEntryType>
			dlFileEntryTypes) {

		removeDLFileEntryTypes(
			pk,
			ListUtil.toLongArray(
				dlFileEntryTypes,
				com.liferay.document.library.kernel.model.DLFileEntryType.
					FILE_ENTRY_TYPE_ID_ACCESSOR));
	}

	/**
	 * Sets the document library file entry types associated with the document library folder, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypePKs the primary keys of the document library file entry types to be associated with the document library folder
	 */
	@Override
	public void setDLFileEntryTypes(long pk, long[] dlFileEntryTypePKs) {
		Set<Long> newDLFileEntryTypePKsSet = SetUtil.fromArray(
			dlFileEntryTypePKs);
		Set<Long> oldDLFileEntryTypePKsSet = SetUtil.fromArray(
			dlFolderToDLFileEntryTypeTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeDLFileEntryTypePKsSet = new HashSet<Long>(
			oldDLFileEntryTypePKsSet);

		removeDLFileEntryTypePKsSet.removeAll(newDLFileEntryTypePKsSet);

		dlFolderToDLFileEntryTypeTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeDLFileEntryTypePKsSet));

		newDLFileEntryTypePKsSet.removeAll(oldDLFileEntryTypePKsSet);

		long companyId = 0;

		DLFolder dlFolder = fetchByPrimaryKey(pk);

		if (dlFolder == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = dlFolder.getCompanyId();
		}

		dlFolderToDLFileEntryTypeTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newDLFileEntryTypePKsSet));
	}

	/**
	 * Sets the document library file entry types associated with the document library folder, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypes the document library file entry types to be associated with the document library folder
	 */
	@Override
	public void setDLFileEntryTypes(
		long pk,
		List<com.liferay.document.library.kernel.model.DLFileEntryType>
			dlFileEntryTypes) {

		try {
			long[] dlFileEntryTypePKs = new long[dlFileEntryTypes.size()];

			for (int i = 0; i < dlFileEntryTypes.size(); i++) {
				com.liferay.document.library.kernel.model.DLFileEntryType
					dlFileEntryType = dlFileEntryTypes.get(i);

				dlFileEntryTypePKs[i] = dlFileEntryType.getPrimaryKey();
			}

			setDLFileEntryTypes(pk, dlFileEntryTypePKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
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
		return "folderId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DLFOLDER;
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
		return DLFolderModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DLFolder";
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
		Set<String> ctMaxColumnNames = new HashSet<String>();
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
		ctMergeColumnNames.add("repositoryId");
		ctMergeColumnNames.add("mountPoint");
		ctMergeColumnNames.add("parentFolderId");
		ctMergeColumnNames.add("treePath");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMaxColumnNames.add("lastPostDate");
		ctMergeColumnNames.add("defaultFileEntryTypeId");
		ctMergeColumnNames.add("hidden_");
		ctMergeColumnNames.add("restrictionType");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");
		ctMergeColumnNames.add("fileEntryTypes");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MAX, ctMaxColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("folderId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_mappingTableNames.add("DLFileEntryTypes_DLFolders");

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "parentFolderId", "name"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the document library folder persistence.
	 */
	public void afterPropertiesSet() {
		dlFolderToDLFileEntryTypeTableMapper =
			TableMapperFactory.getTableMapper(
				"DLFileEntryTypes_DLFolders", "companyId", "folderId",
				"fileEntryTypeId", this, dlFileEntryTypePersistence);

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
			_SQL_SELECT_DLFOLDER_WHERE, _SQL_COUNT_DLFOLDER_WHERE,
			DLFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"dlFolder.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, DLFolder::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(DLFolder::getUuid), DLFolder::getGroupId),
			_SQL_SELECT_DLFOLDER_WHERE, "",
			new FinderColumn<>(
				"dlFolder.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, DLFolder::getUuid),
			new FinderColumn<>(
				"dlFolder.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				DLFolder::getGroupId));

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
				_SQL_SELECT_DLFOLDER_WHERE, _SQL_COUNT_DLFOLDER_WHERE,
				DLFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"dlFolder.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, DLFolder::getUuid),
				new FinderColumn<>(
					"dlFolder.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, DLFolder::getCompanyId));

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
				_SQL_SELECT_DLFOLDER_WHERE, _SQL_COUNT_DLFOLDER_WHERE,
				DLFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"dlFolder.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, DLFolder::getGroupId));

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
				_SQL_SELECT_DLFOLDER_WHERE, _SQL_COUNT_DLFOLDER_WHERE,
				DLFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"dlFolder.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, DLFolder::getCompanyId));

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
				_SQL_SELECT_DLFOLDER_WHERE, _SQL_COUNT_DLFOLDER_WHERE,
				DLFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"dlFolder.", "repositoryId", FinderColumn.Type.LONG, "=",
					true, true, DLFolder::getRepositoryId));

		_collectionPersistenceFinderByG_P =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "parentFolderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "parentFolderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "parentFolderId"}, false),
				_SQL_SELECT_DLFOLDER_WHERE, _SQL_COUNT_DLFOLDER_WHERE,
				DLFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"dlFolder.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, DLFolder::getGroupId),
				new FinderColumn<>(
					"dlFolder.", "parentFolderId", FinderColumn.Type.LONG, "=",
					true, true, DLFolder::getParentFolderId));

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
				_SQL_SELECT_DLFOLDER_WHERE, _SQL_COUNT_DLFOLDER_WHERE,
				DLFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"dlFolder.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, DLFolder::getCompanyId),
				new FinderColumn<>(
					"dlFolder.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, DLFolder::getStatus));

		_uniquePersistenceFinderByR_M = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByR_M",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"repositoryId", "mountPoint"}, 0, 0, false,
				DLFolder::getRepositoryId, DLFolder::isMountPoint),
			_SQL_SELECT_DLFOLDER_WHERE, "",
			new FinderColumn<>(
				"dlFolder.", "repositoryId", FinderColumn.Type.LONG, "=", true,
				true, DLFolder::getRepositoryId),
			new FinderColumn<>(
				"dlFolder.", "mountPoint", FinderColumn.Type.BOOLEAN, "=", true,
				true, DLFolder::isMountPoint));

		_collectionPersistenceFinderByR_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"repositoryId", "parentFolderId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_P",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"repositoryId", "parentFolderId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_P",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"repositoryId", "parentFolderId"}, false),
			_SQL_SELECT_DLFOLDER_WHERE, _SQL_COUNT_DLFOLDER_WHERE,
			DLFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"dlFolder.", "repositoryId", FinderColumn.Type.LONG, "=", true,
				true, DLFolder::getRepositoryId),
			new FinderColumn<>(
				"dlFolder.", "parentFolderId", FinderColumn.Type.LONG, "=",
				true, true, DLFolder::getParentFolderId));

		_collectionPersistenceFinderByP_N = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_N",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"parentFolderId", "name"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"parentFolderId", "name"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"parentFolderId", "name"}, 0, 2, false, null),
			_SQL_SELECT_DLFOLDER_WHERE, _SQL_COUNT_DLFOLDER_WHERE,
			DLFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"dlFolder.", "parentFolderId", FinderColumn.Type.LONG, "=",
				true, true, DLFolder::getParentFolderId),
			new FinderColumn<>(
				"dlFolder.", "name", FinderColumn.Type.STRING, "=", true, true,
				DLFolder::getName));

		_collectionPersistenceFinderByGtF_C_P =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGtF_C_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"folderId", "companyId", "parentFolderId"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGtF_C_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"folderId", "companyId", "parentFolderId"},
					false),
				_SQL_SELECT_DLFOLDER_WHERE, _SQL_COUNT_DLFOLDER_WHERE,
				DLFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"dlFolder.", "folderId", FinderColumn.Type.LONG, ">", true,
					true, DLFolder::getFolderId),
				new FinderColumn<>(
					"dlFolder.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, DLFolder::getCompanyId),
				new FinderColumn<>(
					"dlFolder.", "parentFolderId", FinderColumn.Type.LONG, "=",
					true, true, DLFolder::getParentFolderId));

		_collectionPersistenceFinderByG_M_P =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_M_P",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "mountPoint", "parentFolderId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_M_P",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "mountPoint", "parentFolderId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_M_P",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "mountPoint", "parentFolderId"},
					false),
				_SQL_SELECT_DLFOLDER_WHERE, _SQL_COUNT_DLFOLDER_WHERE,
				DLFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"dlFolder.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, DLFolder::getGroupId),
				new FinderColumn<>(
					"dlFolder.", "mountPoint", FinderColumn.Type.BOOLEAN, "=",
					true, true, DLFolder::isMountPoint),
				new FinderColumn<>(
					"dlFolder.", "parentFolderId", FinderColumn.Type.LONG, "=",
					true, true, DLFolder::getParentFolderId));

		_uniquePersistenceFinderByG_P_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_P_N",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "parentFolderId", "name"}, 0, 4, false,
				DLFolder::getGroupId, DLFolder::getParentFolderId,
				convertNullFunction(DLFolder::getName)),
			_SQL_SELECT_DLFOLDER_WHERE, "",
			new FinderColumn<>(
				"dlFolder.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				DLFolder::getGroupId),
			new FinderColumn<>(
				"dlFolder.", "parentFolderId", FinderColumn.Type.LONG, "=",
				true, true, DLFolder::getParentFolderId),
			new FinderColumn<>(
				"dlFolder.", "name", FinderColumn.Type.STRING, "=", true, true,
				DLFolder::getName));

		_collectionPersistenceFinderByGtF_C_P_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByGtF_C_P_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"folderId", "companyId", "parentFolderId", "status"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByGtF_C_P_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"folderId", "companyId", "parentFolderId", "status"
					},
					false),
				_SQL_SELECT_DLFOLDER_WHERE, _SQL_COUNT_DLFOLDER_WHERE,
				DLFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"dlFolder.", "folderId", FinderColumn.Type.LONG, ">", true,
					true, DLFolder::getFolderId),
				new FinderColumn<>(
					"dlFolder.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, DLFolder::getCompanyId),
				new FinderColumn<>(
					"dlFolder.", "parentFolderId", FinderColumn.Type.LONG, "=",
					true, true, DLFolder::getParentFolderId),
				new FinderColumn<>(
					"dlFolder.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, DLFolder::getStatus));

		_collectionPersistenceFinderByG_M_P_H =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_M_P_H",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "mountPoint", "parentFolderId", "hidden_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_M_P_H",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"groupId", "mountPoint", "parentFolderId", "hidden_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_M_P_H",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"groupId", "mountPoint", "parentFolderId", "hidden_"
					},
					false),
				_SQL_SELECT_DLFOLDER_WHERE, _SQL_COUNT_DLFOLDER_WHERE,
				DLFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"dlFolder.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, DLFolder::getGroupId),
				new FinderColumn<>(
					"dlFolder.", "mountPoint", FinderColumn.Type.BOOLEAN, "=",
					true, true, DLFolder::isMountPoint),
				new FinderColumn<>(
					"dlFolder.", "parentFolderId", FinderColumn.Type.LONG, "=",
					true, true, DLFolder::getParentFolderId),
				new FinderColumn<>(
					"dlFolder.", "hidden", "hidden_", FinderColumn.Type.BOOLEAN,
					"=", true, true, DLFolder::isHidden));

		_collectionPersistenceFinderByG_M_LikeT_H =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_M_LikeT_H",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "mountPoint", "treePath", "hidden_"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_M_LikeT_H",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"groupId", "mountPoint", "treePath", "hidden_"
					},
					false),
				_SQL_SELECT_DLFOLDER_WHERE, _SQL_COUNT_DLFOLDER_WHERE,
				DLFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"dlFolder.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, DLFolder::getGroupId),
				new FinderColumn<>(
					"dlFolder.", "mountPoint", FinderColumn.Type.BOOLEAN, "=",
					true, true, DLFolder::isMountPoint),
				new FinderColumn<>(
					"dlFolder.", "treePath", FinderColumn.Type.STRING, "LIKE",
					true, true, DLFolder::getTreePath),
				new FinderColumn<>(
					"dlFolder.", "hidden", "hidden_", FinderColumn.Type.BOOLEAN,
					"=", true, true, DLFolder::isHidden));

		_collectionPersistenceFinderByG_P_H_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_H_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "parentFolderId", "hidden_", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_H_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "parentFolderId", "hidden_", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_H_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "parentFolderId", "hidden_", "status"
					},
					false),
				_SQL_SELECT_DLFOLDER_WHERE, _SQL_COUNT_DLFOLDER_WHERE,
				DLFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"dlFolder.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, DLFolder::getGroupId),
				new FinderColumn<>(
					"dlFolder.", "parentFolderId", FinderColumn.Type.LONG, "=",
					true, true, DLFolder::getParentFolderId),
				new FinderColumn<>(
					"dlFolder.", "hidden", "hidden_", FinderColumn.Type.BOOLEAN,
					"=", true, true, DLFolder::isHidden),
				new FinderColumn<>(
					"dlFolder.", "status", FinderColumn.Type.INTEGER, "=", true,
					true, DLFolder::getStatus));

		_collectionPersistenceFinderByG_M_P_H_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_M_P_H_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "mountPoint", "parentFolderId", "hidden_",
						"status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_M_P_H_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "mountPoint", "parentFolderId", "hidden_",
						"status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_M_P_H_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "mountPoint", "parentFolderId", "hidden_",
						"status"
					},
					false),
				_SQL_SELECT_DLFOLDER_WHERE, _SQL_COUNT_DLFOLDER_WHERE,
				DLFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"dlFolder.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, DLFolder::getGroupId),
				new FinderColumn<>(
					"dlFolder.", "mountPoint", FinderColumn.Type.BOOLEAN, "=",
					true, true, DLFolder::isMountPoint),
				new FinderColumn<>(
					"dlFolder.", "parentFolderId", FinderColumn.Type.LONG, "=",
					true, true, DLFolder::getParentFolderId),
				new FinderColumn<>(
					"dlFolder.", "hidden", "hidden_", FinderColumn.Type.BOOLEAN,
					"=", true, true, DLFolder::isHidden),
				new FinderColumn<>(
					"dlFolder.", "status", FinderColumn.Type.INTEGER, "=", true,
					true, DLFolder::getStatus));

		_collectionPersistenceFinderByG_M_LikeT_H_NotS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_M_LikeT_H_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "mountPoint", "treePath", "hidden_", "status"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_M_LikeT_H_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "mountPoint", "treePath", "hidden_", "status"
					},
					false),
				_SQL_SELECT_DLFOLDER_WHERE, _SQL_COUNT_DLFOLDER_WHERE,
				DLFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"dlFolder.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, DLFolder::getGroupId),
				new FinderColumn<>(
					"dlFolder.", "mountPoint", FinderColumn.Type.BOOLEAN, "=",
					true, true, DLFolder::isMountPoint),
				new FinderColumn<>(
					"dlFolder.", "treePath", FinderColumn.Type.STRING, "LIKE",
					true, true, DLFolder::getTreePath),
				new FinderColumn<>(
					"dlFolder.", "hidden", "hidden_", FinderColumn.Type.BOOLEAN,
					"=", true, true, DLFolder::isHidden),
				new FinderColumn<>(
					"dlFolder.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, DLFolder::getStatus));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(DLFolder::getExternalReferenceCode),
				DLFolder::getGroupId),
			_SQL_SELECT_DLFOLDER_WHERE, "",
			new FinderColumn<>(
				"dlFolder.", "externalReferenceCode", FinderColumn.Type.STRING,
				"=", true, true, DLFolder::getExternalReferenceCode),
			new FinderColumn<>(
				"dlFolder.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				DLFolder::getGroupId));

		DLFolderUtil.setPersistence(this);
	}

	public void destroy() {
		DLFolderUtil.setPersistence(null);

		EntityCacheUtil.removeCache(DLFolderImpl.class.getName());

		TableMapperFactory.removeTableMapper("DLFileEntryTypes_DLFolders");
	}

	@BeanReference(type = DLFileEntryTypePersistence.class)
	protected DLFileEntryTypePersistence dlFileEntryTypePersistence;

	protected TableMapper
		<DLFolder, com.liferay.document.library.kernel.model.DLFileEntryType>
			dlFolderToDLFileEntryTypeTableMapper;

	private static final String _ENTITY_ALIAS_PREFIX =
		DLFolderModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DLFOLDER =
		"SELECT dlFolder FROM DLFolder dlFolder";

	private static final String _SQL_SELECT_DLFOLDER_WHERE =
		"SELECT dlFolder FROM DLFolder dlFolder WHERE ";

	private static final String _SQL_COUNT_DLFOLDER_WHERE =
		"SELECT COUNT(dlFolder) FROM DLFolder dlFolder WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "hidden"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:662178245