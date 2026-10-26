/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service.persistence;

import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the document library folder service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DLFolderUtil
 * @generated
 */
@ProviderType
public interface DLFolderPersistence
	extends BasePersistence<DLFolder>, CTPersistence<DLFolder> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link DLFolderUtil} to access the document library folder persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the document library folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	public java.util.List<DLFolder> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first document library folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	public DLFolder findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
				orderByComparator)
		throws NoSuchFolderException;

	/**
	 * Returns the first document library folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	public DLFolder fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Removes all the document library folders where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of document library folders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching document library folders
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the document library folder where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	public DLFolder findByUUID_G(String uuid, long groupId)
		throws NoSuchFolderException;

	/**
	 * Returns the document library folder where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	public DLFolder fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the document library folder where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the document library folder that was removed
	 */
	public DLFolder removeByUUID_G(String uuid, long groupId)
		throws NoSuchFolderException;

	/**
	 * Returns the number of document library folders where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching document library folders
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the document library folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first document library folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	public DLFolder findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
				orderByComparator)
		throws NoSuchFolderException;

	/**
	 * Returns the first document library folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	public DLFolder fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Removes all the document library folders where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of document library folders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching document library folders
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the document library folders where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	public java.util.List<DLFolder> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first document library folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	public DLFolder findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
				orderByComparator)
		throws NoSuchFolderException;

	/**
	 * Returns the first document library folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	public DLFolder fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Removes all the document library folders where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of document library folders where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching document library folders
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns an ordered range of all the document library folders where repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	public java.util.List<DLFolder> findByRepositoryId(
		long repositoryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first document library folder in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	public DLFolder findByRepositoryId_First(
			long repositoryId,
			com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
				orderByComparator)
		throws NoSuchFolderException;

	/**
	 * Returns the first document library folder in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	public DLFolder fetchByRepositoryId_First(
		long repositoryId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Removes all the document library folders where repositoryId = &#63; from the database.
	 *
	 * @param repositoryId the repository ID
	 */
	public void removeByRepositoryId(long repositoryId);

	/**
	 * Returns the number of document library folders where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @return the number of matching document library folders
	 */
	public int countByRepositoryId(long repositoryId);

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> findByG_P(
		long groupId, long parentFolderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	public DLFolder findByG_P_First(
			long groupId, long parentFolderId,
			com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
				orderByComparator)
		throws NoSuchFolderException;

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	public DLFolder fetchByG_P_First(
		long groupId, long parentFolderId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders that the user has permission to view
	 */
	public java.util.List<DLFolder> filterFindByG_P(
		long groupId, long parentFolderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Removes all the document library folders where groupId = &#63; and parentFolderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 */
	public void removeByG_P(long groupId, long parentFolderId);

	/**
	 * Returns the number of document library folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching document library folders
	 */
	public int countByG_P(long groupId, long parentFolderId);

	/**
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching document library folders that the user has permission to view
	 */
	public int filterCountByG_P(long groupId, long parentFolderId);

	/**
	 * Returns the document library folder where repositoryId = &#63; and mountPoint = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param repositoryId the repository ID
	 * @param mountPoint the mount point
	 * @return the matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	public DLFolder findByR_M(long repositoryId, boolean mountPoint)
		throws NoSuchFolderException;

	/**
	 * Returns the document library folder where repositoryId = &#63; and mountPoint = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param repositoryId the repository ID
	 * @param mountPoint the mount point
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	public DLFolder fetchByR_M(
		long repositoryId, boolean mountPoint, boolean useFinderCache);

	/**
	 * Removes the document library folder where repositoryId = &#63; and mountPoint = &#63; from the database.
	 *
	 * @param repositoryId the repository ID
	 * @param mountPoint the mount point
	 * @return the document library folder that was removed
	 */
	public DLFolder removeByR_M(long repositoryId, boolean mountPoint)
		throws NoSuchFolderException;

	/**
	 * Returns the number of document library folders where repositoryId = &#63; and mountPoint = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param mountPoint the mount point
	 * @return the number of matching document library folders
	 */
	public int countByR_M(long repositoryId, boolean mountPoint);

	/**
	 * Returns an ordered range of all the document library folders where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> findByR_P(
		long repositoryId, long parentFolderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first document library folder in the ordered set where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	public DLFolder findByR_P_First(
			long repositoryId, long parentFolderId,
			com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
				orderByComparator)
		throws NoSuchFolderException;

	/**
	 * Returns the first document library folder in the ordered set where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	public DLFolder fetchByR_P_First(
		long repositoryId, long parentFolderId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Removes all the document library folders where repositoryId = &#63; and parentFolderId = &#63; from the database.
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 */
	public void removeByR_P(long repositoryId, long parentFolderId);

	/**
	 * Returns the number of document library folders where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching document library folders
	 */
	public int countByR_P(long repositoryId, long parentFolderId);

	/**
	 * Returns all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @return the matching document library folders
	 */
	public java.util.List<DLFolder> findByGtF_C_P(
		long folderId, long companyId, long parentFolderId);

	/**
	 * Returns a range of all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	public java.util.List<DLFolder> findByGtF_C_P(
		long folderId, long companyId, long parentFolderId, int start, int end);

	/**
	 * Returns an ordered range of all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> findByGtF_C_P(
		long folderId, long companyId, long parentFolderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Returns an ordered range of all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> findByGtF_C_P(
		long folderId, long companyId, long parentFolderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator,
		boolean useFinderCache);

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
	public DLFolder findByGtF_C_P_First(
			long folderId, long companyId, long parentFolderId,
			com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
				orderByComparator)
		throws NoSuchFolderException;

	/**
	 * Returns the first document library folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	public DLFolder fetchByGtF_C_P_First(
		long folderId, long companyId, long parentFolderId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Removes all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; from the database.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 */
	public void removeByGtF_C_P(
		long folderId, long companyId, long parentFolderId);

	/**
	 * Returns the number of document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching document library folders
	 */
	public int countByGtF_C_P(
		long folderId, long companyId, long parentFolderId);

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> findByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator,
		boolean useFinderCache);

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
	public DLFolder findByG_M_P_First(
			long groupId, boolean mountPoint, long parentFolderId,
			com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
				orderByComparator)
		throws NoSuchFolderException;

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	public DLFolder fetchByG_M_P_First(
		long groupId, boolean mountPoint, long parentFolderId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> filterFindByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Removes all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 */
	public void removeByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId);

	/**
	 * Returns the number of document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching document library folders
	 */
	public int countByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId);

	/**
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching document library folders that the user has permission to view
	 */
	public int filterCountByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId);

	/**
	 * Returns the document library folder where groupId = &#63; and parentFolderId = &#63; and name = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @return the matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	public DLFolder findByG_P_N(long groupId, long parentFolderId, String name)
		throws NoSuchFolderException;

	/**
	 * Returns the document library folder where groupId = &#63; and parentFolderId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	public DLFolder fetchByG_P_N(
		long groupId, long parentFolderId, String name, boolean useFinderCache);

	/**
	 * Removes the document library folder where groupId = &#63; and parentFolderId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @return the document library folder that was removed
	 */
	public DLFolder removeByG_P_N(
			long groupId, long parentFolderId, String name)
		throws NoSuchFolderException;

	/**
	 * Returns the number of document library folders where groupId = &#63; and parentFolderId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @return the number of matching document library folders
	 */
	public int countByG_P_N(long groupId, long parentFolderId, String name);

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> findByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator,
		boolean useFinderCache);

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
	public DLFolder findByG_M_P_H_First(
			long groupId, boolean mountPoint, long parentFolderId,
			boolean hidden,
			com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
				orderByComparator)
		throws NoSuchFolderException;

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
	public DLFolder fetchByG_M_P_H_First(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> filterFindByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Removes all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 */
	public void removeByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden);

	/**
	 * Returns the number of document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @return the number of matching document library folders
	 */
	public int countByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden);

	/**
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @return the number of matching document library folders that the user has permission to view
	 */
	public int filterCountByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden);

	/**
	 * Returns all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @return the matching document library folders
	 */
	public java.util.List<DLFolder> findByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden);

	/**
	 * Returns a range of all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> findByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int start, int end);

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> findByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> findByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator,
		boolean useFinderCache);

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
	public DLFolder findByG_M_LikeT_H_First(
			long groupId, boolean mountPoint, String treePath, boolean hidden,
			com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
				orderByComparator)
		throws NoSuchFolderException;

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
	public DLFolder fetchByG_M_LikeT_H_First(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Returns all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @return the matching document library folders that the user has permission to view
	 */
	public java.util.List<DLFolder> filterFindByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden);

	/**
	 * Returns a range of all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> filterFindByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int start, int end);

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> filterFindByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Removes all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 */
	public void removeByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden);

	/**
	 * Returns the number of document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @return the number of matching document library folders
	 */
	public int countByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden);

	/**
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @return the number of matching document library folders that the user has permission to view
	 */
	public int filterCountByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden);

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> findByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator,
		boolean useFinderCache);

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
	public DLFolder findByG_P_H_S_First(
			long groupId, long parentFolderId, boolean hidden, int status,
			com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
				orderByComparator)
		throws NoSuchFolderException;

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
	public DLFolder fetchByG_P_H_S_First(
		long groupId, long parentFolderId, boolean hidden, int status,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> filterFindByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Removes all the document library folders where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 */
	public void removeByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status);

	/**
	 * Returns the number of document library folders where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @return the number of matching document library folders
	 */
	public int countByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status);

	/**
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @return the number of matching document library folders that the user has permission to view
	 */
	public int filterCountByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status);

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> findByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator,
		boolean useFinderCache);

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
	public DLFolder findByG_M_P_H_S_First(
			long groupId, boolean mountPoint, long parentFolderId,
			boolean hidden, int status,
			com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
				orderByComparator)
		throws NoSuchFolderException;

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
	public DLFolder fetchByG_M_P_H_S_First(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> filterFindByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Removes all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 */
	public void removeByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status);

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
	public int countByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status);

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
	public int filterCountByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status);

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
	public java.util.List<DLFolder> findByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status);

	/**
	 * Returns a range of all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> findByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status, int start, int end);

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> findByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> findByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator,
		boolean useFinderCache);

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
	public DLFolder findByG_M_LikeT_H_NotS_First(
			long groupId, boolean mountPoint, String treePath, boolean hidden,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
				orderByComparator)
		throws NoSuchFolderException;

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
	public DLFolder fetchByG_M_LikeT_H_NotS_First(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

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
	public java.util.List<DLFolder> filterFindByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status);

	/**
	 * Returns a range of all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> filterFindByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status, int start, int end);

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	public java.util.List<DLFolder> filterFindByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator);

	/**
	 * Removes all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 */
	public void removeByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status);

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
	public int countByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status);

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
	public int filterCountByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status);

	/**
	 * Returns the document library folder where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	public DLFolder findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchFolderException;

	/**
	 * Returns the document library folder where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	public DLFolder fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache);

	/**
	 * Removes the document library folder where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the document library folder that was removed
	 */
	public DLFolder removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchFolderException;

	/**
	 * Returns the number of document library folders where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching document library folders
	 */
	public int countByERC_G(String externalReferenceCode, long groupId);

	/**
	 * Creates a new document library folder with the primary key. Does not add the document library folder to the database.
	 *
	 * @param folderId the primary key for the new document library folder
	 * @return the new document library folder
	 */
	public DLFolder create(long folderId);

	/**
	 * Removes the document library folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param folderId the primary key of the document library folder
	 * @return the document library folder that was removed
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	public DLFolder remove(long folderId) throws NoSuchFolderException;

	public DLFolder updateImpl(DLFolder dlFolder);

	/**
	 * Returns the document library folder with the primary key or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param folderId the primary key of the document library folder
	 * @return the document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	public DLFolder findByPrimaryKey(long folderId)
		throws NoSuchFolderException;

	/**
	 * Returns the document library folder with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param folderId the primary key of the document library folder
	 * @return the document library folder, or <code>null</code> if a document library folder with the primary key could not be found
	 */
	public DLFolder fetchByPrimaryKey(long folderId);

	/**
	 * Returns the primaryKeys of document library file entry types associated with the document library folder.
	 *
	 * @param pk the primary key of the document library folder
	 * @return long[] of the primaryKeys of document library file entry types associated with the document library folder
	 */
	public long[] getDLFileEntryTypePrimaryKeys(long pk);

	/**
	 * Returns all the document library file entry types associated with the document library folder.
	 *
	 * @param pk the primary key of the document library folder
	 * @return the document library file entry types associated with the document library folder
	 */
	public java.util.List
		<com.liferay.document.library.kernel.model.DLFileEntryType>
			getDLFileEntryTypes(long pk);

	/**
	 * Returns a range of all the document library file entry types associated with the document library folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the document library folder
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of document library file entry types associated with the document library folder
	 */
	public java.util.List
		<com.liferay.document.library.kernel.model.DLFileEntryType>
			getDLFileEntryTypes(long pk, int start, int end);

	/**
	 * Returns an ordered range of all the document library file entry types associated with the document library folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the document library folder
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of document library file entry types associated with the document library folder
	 */
	public java.util.List
		<com.liferay.document.library.kernel.model.DLFileEntryType>
			getDLFileEntryTypes(
				long pk, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.document.library.kernel.model.DLFileEntryType>
						orderByComparator);

	/**
	 * Returns the number of document library file entry types associated with the document library folder.
	 *
	 * @param pk the primary key of the document library folder
	 * @return the number of document library file entry types associated with the document library folder
	 */
	public int getDLFileEntryTypesSize(long pk);

	/**
	 * Returns <code>true</code> if the document library file entry type is associated with the document library folder.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypePK the primary key of the document library file entry type
	 * @return <code>true</code> if the document library file entry type is associated with the document library folder; <code>false</code> otherwise
	 */
	public boolean containsDLFileEntryType(long pk, long dlFileEntryTypePK);

	/**
	 * Returns <code>true</code> if the document library folder has any document library file entry types associated with it.
	 *
	 * @param pk the primary key of the document library folder to check for associations with document library file entry types
	 * @return <code>true</code> if the document library folder has any document library file entry types associated with it; <code>false</code> otherwise
	 */
	public boolean containsDLFileEntryTypes(long pk);

	/**
	 * Adds an association between the document library folder and the document library file entry type. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypePK the primary key of the document library file entry type
	 * @return <code>true</code> if an association between the document library folder and the document library file entry type was added; <code>false</code> if they were already associated
	 */
	public boolean addDLFileEntryType(long pk, long dlFileEntryTypePK);

	/**
	 * Adds an association between the document library folder and the document library file entry type. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryType the document library file entry type
	 * @return <code>true</code> if an association between the document library folder and the document library file entry type was added; <code>false</code> if they were already associated
	 */
	public boolean addDLFileEntryType(
		long pk,
		com.liferay.document.library.kernel.model.DLFileEntryType
			dlFileEntryType);

	/**
	 * Adds an association between the document library folder and the document library file entry types. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypePKs the primary keys of the document library file entry types
	 * @return <code>true</code> if at least one association between the document library folder and the document library file entry types was added; <code>false</code> if they were all already associated
	 */
	public boolean addDLFileEntryTypes(long pk, long[] dlFileEntryTypePKs);

	/**
	 * Adds an association between the document library folder and the document library file entry types. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypes the document library file entry types
	 * @return <code>true</code> if at least one association between the document library folder and the document library file entry types was added; <code>false</code> if they were all already associated
	 */
	public boolean addDLFileEntryTypes(
		long pk,
		java.util.List
			<com.liferay.document.library.kernel.model.DLFileEntryType>
				dlFileEntryTypes);

	/**
	 * Clears all associations between the document library folder and its document library file entry types. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder to clear the associated document library file entry types from
	 */
	public void clearDLFileEntryTypes(long pk);

	/**
	 * Removes the association between the document library folder and the document library file entry type. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypePK the primary key of the document library file entry type
	 */
	public void removeDLFileEntryType(long pk, long dlFileEntryTypePK);

	/**
	 * Removes the association between the document library folder and the document library file entry type. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryType the document library file entry type
	 */
	public void removeDLFileEntryType(
		long pk,
		com.liferay.document.library.kernel.model.DLFileEntryType
			dlFileEntryType);

	/**
	 * Removes the association between the document library folder and the document library file entry types. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypePKs the primary keys of the document library file entry types
	 */
	public void removeDLFileEntryTypes(long pk, long[] dlFileEntryTypePKs);

	/**
	 * Removes the association between the document library folder and the document library file entry types. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypes the document library file entry types
	 */
	public void removeDLFileEntryTypes(
		long pk,
		java.util.List
			<com.liferay.document.library.kernel.model.DLFileEntryType>
				dlFileEntryTypes);

	/**
	 * Sets the document library file entry types associated with the document library folder, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypePKs the primary keys of the document library file entry types to be associated with the document library folder
	 */
	public void setDLFileEntryTypes(long pk, long[] dlFileEntryTypePKs);

	/**
	 * Sets the document library file entry types associated with the document library folder, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypes the document library file entry types to be associated with the document library folder
	 */
	public void setDLFileEntryTypes(
		long pk,
		java.util.List
			<com.liferay.document.library.kernel.model.DLFileEntryType>
				dlFileEntryTypes);

	/**
	 * Returns the document library folder where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	public default DLFolder fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the document library folder where repositoryId = &#63; and mountPoint = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param repositoryId the repository ID
	 * @param mountPoint the mount point
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	public default DLFolder fetchByR_M(long repositoryId, boolean mountPoint) {
		return fetchByR_M(repositoryId, mountPoint, true);
	}

	/**
	 * Returns the document library folder where groupId = &#63; and parentFolderId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	public default DLFolder fetchByG_P_N(
		long groupId, long parentFolderId, String name) {

		return fetchByG_P_N(groupId, parentFolderId, name, true);
	}

	/**
	 * Returns the document library folder where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	public default DLFolder fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns all the document library folders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching document library folders
	 */
	public default java.util.List<DLFolder> findByUuid(String uuid) {
		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library folders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching document library folders
	 */
	public default java.util.List<DLFolder> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library folders where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching document library folders
	 */
	public default java.util.List<DLFolder> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library folders where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library folders where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @return the matching document library folders
	 */
	public default java.util.List<DLFolder> findByRepositoryId(
		long repositoryId) {

		return findByRepositoryId(
			repositoryId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library folders where repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByRepositoryId(
		long repositoryId, int start, int end) {

		return findByRepositoryId(repositoryId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByRepositoryId(
		long repositoryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator) {

		return findByRepositoryId(
			repositoryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @return the matching document library folders
	 */
	public default java.util.List<DLFolder> findByG_P(
		long groupId, long parentFolderId) {

		return findByG_P(
			groupId, parentFolderId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByG_P(
		long groupId, long parentFolderId, int start, int end) {

		return findByG_P(groupId, parentFolderId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByG_P(
		long groupId, long parentFolderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator) {

		return findByG_P(
			groupId, parentFolderId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @return the matching document library folders that the user has permission to view
	 */
	public default java.util.List<DLFolder> filterFindByG_P(
		long groupId, long parentFolderId) {

		return filterFindByG_P(
			groupId, parentFolderId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders that the user has permission to view
	 */
	public default java.util.List<DLFolder> filterFindByG_P(
		long groupId, long parentFolderId, int start, int end) {

		return filterFindByG_P(groupId, parentFolderId, start, end, null);
	}

	/**
	 * Returns all the document library folders where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @return the matching document library folders
	 */
	public default java.util.List<DLFolder> findByR_P(
		long repositoryId, long parentFolderId) {

		return findByR_P(
			repositoryId, parentFolderId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library folders where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByR_P(
		long repositoryId, long parentFolderId, int start, int end) {

		return findByR_P(repositoryId, parentFolderId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByR_P(
		long repositoryId, long parentFolderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator) {

		return findByR_P(
			repositoryId, parentFolderId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @return the matching document library folders
	 */
	public default java.util.List<DLFolder> findByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId) {

		return findByG_M_P(
			groupId, mountPoint, parentFolderId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId, int start,
		int end) {

		return findByG_M_P(
			groupId, mountPoint, parentFolderId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator) {

		return findByG_M_P(
			groupId, mountPoint, parentFolderId, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @return the matching document library folders that the user has permission to view
	 */
	public default java.util.List<DLFolder> filterFindByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId) {

		return filterFindByG_M_P(
			groupId, mountPoint, parentFolderId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders that the user has permission to view
	 */
	public default java.util.List<DLFolder> filterFindByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId, int start,
		int end) {

		return filterFindByG_M_P(
			groupId, mountPoint, parentFolderId, start, end, null);
	}

	/**
	 * Returns all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @return the matching document library folders
	 */
	public default java.util.List<DLFolder> findByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden) {

		return findByG_M_P_H(
			groupId, mountPoint, parentFolderId, hidden,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int start, int end) {

		return findByG_M_P_H(
			groupId, mountPoint, parentFolderId, hidden, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator) {

		return findByG_M_P_H(
			groupId, mountPoint, parentFolderId, hidden, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @return the matching document library folders that the user has permission to view
	 */
	public default java.util.List<DLFolder> filterFindByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden) {

		return filterFindByG_M_P_H(
			groupId, mountPoint, parentFolderId, hidden,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders that the user has permission to view
	 */
	public default java.util.List<DLFolder> filterFindByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int start, int end) {

		return filterFindByG_M_P_H(
			groupId, mountPoint, parentFolderId, hidden, start, end, null);
	}

	/**
	 * Returns all the document library folders where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @return the matching document library folders
	 */
	public default java.util.List<DLFolder> findByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status) {

		return findByG_P_H_S(
			groupId, parentFolderId, hidden, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library folders where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status,
		int start, int end) {

		return findByG_P_H_S(
			groupId, parentFolderId, hidden, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator) {

		return findByG_P_H_S(
			groupId, parentFolderId, hidden, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the document library folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @return the matching document library folders that the user has permission to view
	 */
	public default java.util.List<DLFolder> filterFindByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status) {

		return filterFindByG_P_H_S(
			groupId, parentFolderId, hidden, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders that the user has permission to view
	 */
	public default java.util.List<DLFolder> filterFindByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status,
		int start, int end) {

		return filterFindByG_P_H_S(
			groupId, parentFolderId, hidden, status, start, end, null);
	}

	/**
	 * Returns all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @return the matching document library folders
	 */
	public default java.util.List<DLFolder> findByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status) {

		return findByG_M_P_H_S(
			groupId, mountPoint, parentFolderId, hidden, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status, int start, int end) {

		return findByG_M_P_H_S(
			groupId, mountPoint, parentFolderId, hidden, status, start, end,
			null, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	 * @return the ordered range of matching document library folders
	 */
	public default java.util.List<DLFolder> findByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator) {

		return findByG_M_P_H_S(
			groupId, mountPoint, parentFolderId, hidden, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @return the matching document library folders that the user has permission to view
	 */
	public default java.util.List<DLFolder> filterFindByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status) {

		return filterFindByG_M_P_H_S(
			groupId, mountPoint, parentFolderId, hidden, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders that the user has permission to view
	 */
	public default java.util.List<DLFolder> filterFindByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status, int start, int end) {

		return filterFindByG_M_P_H_S(
			groupId, mountPoint, parentFolderId, hidden, status, start, end,
			null);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-869428174