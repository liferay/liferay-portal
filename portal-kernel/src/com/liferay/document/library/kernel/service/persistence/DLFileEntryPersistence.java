/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service.persistence;

import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the document library file entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DLFileEntryUtil
 * @generated
 */
@ProviderType
public interface DLFileEntryPersistence
	extends BasePersistence<DLFileEntry>, CTPersistence<DLFileEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link DLFileEntryUtil} to access the document library file entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the document library file entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public java.util.List<DLFileEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first document library file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public DLFileEntry findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws NoSuchFileEntryException;

	/**
	 * Returns the first document library file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Removes all the document library file entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of document library file entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching document library file entries
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the document library file entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public DLFileEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchFileEntryException;

	/**
	 * Returns the document library file entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the document library file entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the document library file entry that was removed
	 */
	public DLFileEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchFileEntryException;

	/**
	 * Returns the number of document library file entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching document library file entries
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the document library file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
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
	public java.util.List<DLFileEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first document library file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public DLFileEntry findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws NoSuchFileEntryException;

	/**
	 * Returns the first document library file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Removes all the document library file entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of document library file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching document library file entries
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the document library file entries where repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public java.util.List<DLFileEntry> findByRepositoryId(
		long repositoryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first document library file entry in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public DLFileEntry findByRepositoryId_First(
			long repositoryId,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws NoSuchFileEntryException;

	/**
	 * Returns the first document library file entry in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchByRepositoryId_First(
		long repositoryId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Removes all the document library file entries where repositoryId = &#63; from the database.
	 *
	 * @param repositoryId the repository ID
	 */
	public void removeByRepositoryId(long repositoryId);

	/**
	 * Returns the number of document library file entries where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @return the number of matching document library file entries
	 */
	public int countByRepositoryId(long repositoryId);

	/**
	 * Returns an ordered range of all the document library file entries where mimeType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param mimeType the mime type
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public java.util.List<DLFileEntry> findByMimeType(
		String mimeType, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first document library file entry in the ordered set where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public DLFileEntry findByMimeType_First(
			String mimeType,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws NoSuchFileEntryException;

	/**
	 * Returns the first document library file entry in the ordered set where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchByMimeType_First(
		String mimeType,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Removes all the document library file entries where mimeType = &#63; from the database.
	 *
	 * @param mimeType the mime type
	 */
	public void removeByMimeType(String mimeType);

	/**
	 * Returns the number of document library file entries where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @return the number of matching document library file entries
	 */
	public int countByMimeType(String mimeType);

	/**
	 * Returns an ordered range of all the document library file entries where fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public java.util.List<DLFileEntry> findByFileEntryTypeId(
		long fileEntryTypeId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first document library file entry in the ordered set where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public DLFileEntry findByFileEntryTypeId_First(
			long fileEntryTypeId,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws NoSuchFileEntryException;

	/**
	 * Returns the first document library file entry in the ordered set where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchByFileEntryTypeId_First(
		long fileEntryTypeId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Removes all the document library file entries where fileEntryTypeId = &#63; from the database.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 */
	public void removeByFileEntryTypeId(long fileEntryTypeId);

	/**
	 * Returns the number of document library file entries where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries
	 */
	public int countByFileEntryTypeId(long fileEntryTypeId);

	/**
	 * Returns an ordered range of all the document library file entries where smallImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public java.util.List<DLFileEntry> findBySmallImageId(
		long smallImageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first document library file entry in the ordered set where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public DLFileEntry findBySmallImageId_First(
			long smallImageId,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws NoSuchFileEntryException;

	/**
	 * Returns the first document library file entry in the ordered set where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchBySmallImageId_First(
		long smallImageId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Removes all the document library file entries where smallImageId = &#63; from the database.
	 *
	 * @param smallImageId the small image ID
	 */
	public void removeBySmallImageId(long smallImageId);

	/**
	 * Returns the number of document library file entries where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @return the number of matching document library file entries
	 */
	public int countBySmallImageId(long smallImageId);

	/**
	 * Returns an ordered range of all the document library file entries where largeImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param largeImageId the large image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public java.util.List<DLFileEntry> findByLargeImageId(
		long largeImageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first document library file entry in the ordered set where largeImageId = &#63;.
	 *
	 * @param largeImageId the large image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public DLFileEntry findByLargeImageId_First(
			long largeImageId,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws NoSuchFileEntryException;

	/**
	 * Returns the first document library file entry in the ordered set where largeImageId = &#63;.
	 *
	 * @param largeImageId the large image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchByLargeImageId_First(
		long largeImageId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Removes all the document library file entries where largeImageId = &#63; from the database.
	 *
	 * @param largeImageId the large image ID
	 */
	public void removeByLargeImageId(long largeImageId);

	/**
	 * Returns the number of document library file entries where largeImageId = &#63;.
	 *
	 * @param largeImageId the large image ID
	 * @return the number of matching document library file entries
	 */
	public int countByLargeImageId(long largeImageId);

	/**
	 * Returns an ordered range of all the document library file entries where custom1ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public java.util.List<DLFileEntry> findByCustom1ImageId(
		long custom1ImageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first document library file entry in the ordered set where custom1ImageId = &#63;.
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public DLFileEntry findByCustom1ImageId_First(
			long custom1ImageId,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws NoSuchFileEntryException;

	/**
	 * Returns the first document library file entry in the ordered set where custom1ImageId = &#63;.
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchByCustom1ImageId_First(
		long custom1ImageId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Removes all the document library file entries where custom1ImageId = &#63; from the database.
	 *
	 * @param custom1ImageId the custom1 image ID
	 */
	public void removeByCustom1ImageId(long custom1ImageId);

	/**
	 * Returns the number of document library file entries where custom1ImageId = &#63;.
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @return the number of matching document library file entries
	 */
	public int countByCustom1ImageId(long custom1ImageId);

	/**
	 * Returns an ordered range of all the document library file entries where custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public java.util.List<DLFileEntry> findByCustom2ImageId(
		long custom2ImageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first document library file entry in the ordered set where custom2ImageId = &#63;.
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public DLFileEntry findByCustom2ImageId_First(
			long custom2ImageId,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws NoSuchFileEntryException;

	/**
	 * Returns the first document library file entry in the ordered set where custom2ImageId = &#63;.
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchByCustom2ImageId_First(
		long custom2ImageId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Removes all the document library file entries where custom2ImageId = &#63; from the database.
	 *
	 * @param custom2ImageId the custom2 image ID
	 */
	public void removeByCustom2ImageId(long custom2ImageId);

	/**
	 * Returns the number of document library file entries where custom2ImageId = &#63;.
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @return the number of matching document library file entries
	 */
	public int countByCustom2ImageId(long custom2ImageId);

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
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
	public java.util.List<DLFileEntry> findByG_U(
		long groupId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public DLFileEntry findByG_U_First(
			long groupId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws NoSuchFileEntryException;

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchByG_U_First(
		long groupId, long userId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the document library file entries that the user has permissions to view where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	public java.util.List<DLFileEntry> filterFindByG_U(
		long groupId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Removes all the document library file entries where groupId = &#63; and userId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 */
	public void removeByG_U(long groupId, long userId);

	/**
	 * Returns the number of document library file entries where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching document library file entries
	 */
	public int countByG_U(long groupId, long userId);

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	public int filterCountByG_U(long groupId, long userId);

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
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
	public java.util.List<DLFileEntry> findByG_F(
		long groupId, long folderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public DLFileEntry findByG_F_First(
			long groupId, long folderId,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws NoSuchFileEntryException;

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchByG_F_First(
		long groupId, long folderId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the document library file entries that the user has permissions to view where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	public java.util.List<DLFileEntry> filterFindByG_F(
		long groupId, long folderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	public java.util.List<DLFileEntry> filterFindByG_F(
		long groupId, long[] folderIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
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
	public java.util.List<DLFileEntry> findByG_F(
		long groupId, long[] folderIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the document library file entries where groupId = &#63; and folderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 */
	public void removeByG_F(long groupId, long folderId);

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file entries
	 */
	public int countByG_F(long groupId, long folderId);

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the number of matching document library file entries
	 */
	public int countByG_F(long groupId, long[] folderIds);

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	public int filterCountByG_F(long groupId, long folderId);

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	public int filterCountByG_F(long groupId, long[] folderIds);

	/**
	 * Returns an ordered range of all the document library file entries where folderId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
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
	public java.util.List<DLFileEntry> findByF_N(
		long folderId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first document library file entry in the ordered set where folderId = &#63; and name = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public DLFileEntry findByF_N_First(
			long folderId, String name,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws NoSuchFileEntryException;

	/**
	 * Returns the first document library file entry in the ordered set where folderId = &#63; and name = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchByF_N_First(
		long folderId, String name,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Removes all the document library file entries where folderId = &#63; and name = &#63; from the database.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 */
	public void removeByF_N(long folderId, String name);

	/**
	 * Returns the number of document library file entries where folderId = &#63; and name = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the number of matching document library file entries
	 */
	public int countByF_N(long folderId, String name);

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
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
	public java.util.List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long folderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator,
		boolean useFinderCache);

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
	public DLFileEntry findByG_U_F_First(
			long groupId, long userId, long folderId,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws NoSuchFileEntryException;

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchByG_U_F_First(
		long groupId, long userId, long folderId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the document library file entries that the user has permissions to view where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
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
	public java.util.List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long folderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
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
	public java.util.List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long[] folderIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
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
	public java.util.List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long[] folderIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 */
	public void removeByG_U_F(long groupId, long userId, long folderId);

	/**
	 * Returns the number of document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file entries
	 */
	public int countByG_U_F(long groupId, long userId, long folderId);

	/**
	 * Returns the number of document library file entries where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @return the number of matching document library file entries
	 */
	public int countByG_U_F(long groupId, long userId, long[] folderIds);

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	public int filterCountByG_U_F(long groupId, long userId, long folderId);

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	public int filterCountByG_U_F(long groupId, long userId, long[] folderIds);

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and name = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public DLFileEntry findByG_F_N(long groupId, long folderId, String name)
		throws NoSuchFileEntryException;

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchByG_F_N(
		long groupId, long folderId, String name, boolean useFinderCache);

	/**
	 * Removes the document library file entry where groupId = &#63; and folderId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the document library file entry that was removed
	 */
	public DLFileEntry removeByG_F_N(long groupId, long folderId, String name)
		throws NoSuchFileEntryException;

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the number of matching document library file entries
	 */
	public int countByG_F_N(long groupId, long folderId, String name);

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and fileName = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileName the file name
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public DLFileEntry findByG_F_FN(
			long groupId, long folderId, String fileName)
		throws NoSuchFileEntryException;

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and fileName = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileName the file name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchByG_F_FN(
		long groupId, long folderId, String fileName, boolean useFinderCache);

	/**
	 * Removes the document library file entry where groupId = &#63; and folderId = &#63; and fileName = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileName the file name
	 * @return the document library file entry that was removed
	 */
	public DLFileEntry removeByG_F_FN(
			long groupId, long folderId, String fileName)
		throws NoSuchFileEntryException;

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63; and fileName = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileName the file name
	 * @return the number of matching document library file entries
	 */
	public int countByG_F_FN(long groupId, long folderId, String fileName);

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and title = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public DLFileEntry findByG_F_T(long groupId, long folderId, String title)
		throws NoSuchFileEntryException;

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and title = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchByG_F_T(
		long groupId, long folderId, String title, boolean useFinderCache);

	/**
	 * Removes the document library file entry where groupId = &#63; and folderId = &#63; and title = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @return the document library file entry that was removed
	 */
	public DLFileEntry removeByG_F_T(long groupId, long folderId, String title)
		throws NoSuchFileEntryException;

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63; and title = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @return the number of matching document library file entries
	 */
	public int countByG_F_T(long groupId, long folderId, String title);

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
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
	public java.util.List<DLFileEntry> findByG_F_F(
		long groupId, long folderId, long fileEntryTypeId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator,
		boolean useFinderCache);

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
	public DLFileEntry findByG_F_F_First(
			long groupId, long folderId, long fileEntryTypeId,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws NoSuchFileEntryException;

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchByG_F_F_First(
		long groupId, long folderId, long fileEntryTypeId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the document library file entries that the user has permissions to view where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
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
	public java.util.List<DLFileEntry> filterFindByG_F_F(
		long groupId, long folderId, long fileEntryTypeId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
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
	public java.util.List<DLFileEntry> filterFindByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
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
	public java.util.List<DLFileEntry> findByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 */
	public void removeByG_F_F(
		long groupId, long folderId, long fileEntryTypeId);

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries
	 */
	public int countByG_F_F(long groupId, long folderId, long fileEntryTypeId);

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries
	 */
	public int countByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId);

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	public int filterCountByG_F_F(
		long groupId, long folderId, long fileEntryTypeId);

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	public int filterCountByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId);

	/**
	 * Returns an ordered range of all the document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
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
	public java.util.List<DLFileEntry> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator,
		boolean useFinderCache);

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
	public DLFileEntry findByC_C_C_First(
			long companyId, long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws NoSuchFileEntryException;

	/**
	 * Returns the first document library file entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchByC_C_C_First(
		long companyId, long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Removes all the document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public void removeByC_C_C(long companyId, long classNameId, long classPK);

	/**
	 * Returns the number of document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching document library file entries
	 */
	public int countByC_C_C(long companyId, long classNameId, long classPK);

	/**
	 * Returns an ordered range of all the document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
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
	public java.util.List<DLFileEntry> findByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator,
		boolean useFinderCache);

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
	public DLFileEntry findByS_L_C1_C2_First(
			long smallImageId, long largeImageId, long custom1ImageId,
			long custom2ImageId,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws NoSuchFileEntryException;

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
	public DLFileEntry fetchByS_L_C1_C2_First(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator);

	/**
	 * Removes all the document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63; from the database.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 */
	public void removeByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId);

	/**
	 * Returns the number of document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @return the number of matching document library file entries
	 */
	public int countByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId);

	/**
	 * Returns the document library file entry where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public DLFileEntry findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchFileEntryException;

	/**
	 * Returns the document library file entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public DLFileEntry fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache);

	/**
	 * Removes the document library file entry where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the document library file entry that was removed
	 */
	public DLFileEntry removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchFileEntryException;

	/**
	 * Returns the number of document library file entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching document library file entries
	 */
	public int countByERC_G(String externalReferenceCode, long groupId);

	/**
	 * Creates a new document library file entry with the primary key. Does not add the document library file entry to the database.
	 *
	 * @param fileEntryId the primary key for the new document library file entry
	 * @return the new document library file entry
	 */
	public DLFileEntry create(long fileEntryId);

	/**
	 * Removes the document library file entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fileEntryId the primary key of the document library file entry
	 * @return the document library file entry that was removed
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public DLFileEntry remove(long fileEntryId) throws NoSuchFileEntryException;

	public DLFileEntry updateImpl(DLFileEntry dlFileEntry);

	/**
	 * Returns the document library file entry with the primary key or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param fileEntryId the primary key of the document library file entry
	 * @return the document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public DLFileEntry findByPrimaryKey(long fileEntryId)
		throws NoSuchFileEntryException;

	/**
	 * Returns the document library file entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fileEntryId the primary key of the document library file entry
	 * @return the document library file entry, or <code>null</code> if a document library file entry with the primary key could not be found
	 */
	public DLFileEntry fetchByPrimaryKey(long fileEntryId);

	/**
	 * Returns the document library file entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public default DLFileEntry fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public default DLFileEntry fetchByG_F_N(
		long groupId, long folderId, String name) {

		return fetchByG_F_N(groupId, folderId, name, true);
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and fileName = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileName the file name
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public default DLFileEntry fetchByG_F_FN(
		long groupId, long folderId, String fileName) {

		return fetchByG_F_FN(groupId, folderId, fileName, true);
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and title = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public default DLFileEntry fetchByG_F_T(
		long groupId, long folderId, String title) {

		return fetchByG_F_T(groupId, folderId, title, true);
	}

	/**
	 * Returns the document library file entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public default DLFileEntry fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns all the document library file entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByUuid(String uuid) {
		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library file entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library file entries where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @return the matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByRepositoryId(
		long repositoryId) {

		return findByRepositoryId(
			repositoryId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library file entries where repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByRepositoryId(
		long repositoryId, int start, int end) {

		return findByRepositoryId(repositoryId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByRepositoryId(
		long repositoryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return findByRepositoryId(
			repositoryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library file entries where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @return the matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByMimeType(String mimeType) {
		return findByMimeType(
			mimeType, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library file entries where mimeType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param mimeType the mime type
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByMimeType(
		String mimeType, int start, int end) {

		return findByMimeType(mimeType, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where mimeType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param mimeType the mime type
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByMimeType(
		String mimeType, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return findByMimeType(mimeType, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library file entries where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @return the matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByFileEntryTypeId(
		long fileEntryTypeId) {

		return findByFileEntryTypeId(
			fileEntryTypeId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library file entries where fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByFileEntryTypeId(
		long fileEntryTypeId, int start, int end) {

		return findByFileEntryTypeId(fileEntryTypeId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByFileEntryTypeId(
		long fileEntryTypeId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return findByFileEntryTypeId(
			fileEntryTypeId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library file entries where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @return the matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findBySmallImageId(
		long smallImageId) {

		return findBySmallImageId(
			smallImageId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library file entries where smallImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findBySmallImageId(
		long smallImageId, int start, int end) {

		return findBySmallImageId(smallImageId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where smallImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findBySmallImageId(
		long smallImageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return findBySmallImageId(
			smallImageId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library file entries where largeImageId = &#63;.
	 *
	 * @param largeImageId the large image ID
	 * @return the matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByLargeImageId(
		long largeImageId) {

		return findByLargeImageId(
			largeImageId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library file entries where largeImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param largeImageId the large image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByLargeImageId(
		long largeImageId, int start, int end) {

		return findByLargeImageId(largeImageId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where largeImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param largeImageId the large image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByLargeImageId(
		long largeImageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return findByLargeImageId(
			largeImageId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library file entries where custom1ImageId = &#63;.
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @return the matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByCustom1ImageId(
		long custom1ImageId) {

		return findByCustom1ImageId(
			custom1ImageId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library file entries where custom1ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByCustom1ImageId(
		long custom1ImageId, int start, int end) {

		return findByCustom1ImageId(custom1ImageId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where custom1ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByCustom1ImageId(
		long custom1ImageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return findByCustom1ImageId(
			custom1ImageId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library file entries where custom2ImageId = &#63;.
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @return the matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByCustom2ImageId(
		long custom2ImageId) {

		return findByCustom2ImageId(
			custom2ImageId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library file entries where custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByCustom2ImageId(
		long custom2ImageId, int start, int end) {

		return findByCustom2ImageId(custom2ImageId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByCustom2ImageId(
		long custom2ImageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return findByCustom2ImageId(
			custom2ImageId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library file entries where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_U(
		long groupId, long userId) {

		return findByG_U(
			groupId, userId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_U(
		long groupId, long userId, int start, int end) {

		return findByG_U(groupId, userId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_U(
		long groupId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return findByG_U(groupId, userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the matching document library file entries that the user has permission to view
	 */
	public default java.util.List<DLFileEntry> filterFindByG_U(
		long groupId, long userId) {

		return filterFindByG_U(
			groupId, userId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	public default java.util.List<DLFileEntry> filterFindByG_U(
		long groupId, long userId, int start, int end) {

		return filterFindByG_U(groupId, userId, start, end, null);
	}

	/**
	 * Returns all the document library file entries where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_F(
		long groupId, long folderId) {

		return findByG_F(
			groupId, folderId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_F(
		long groupId, long folderId, int start, int end) {

		return findByG_F(groupId, folderId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_F(
		long groupId, long folderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return findByG_F(
			groupId, folderId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library file entries where groupId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_F(
		long groupId, long[] folderIds) {

		return findByG_F(
			groupId, folderIds,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_F(
		long groupId, long[] folderIds, int start, int end) {

		return findByG_F(groupId, folderIds, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_F(
		long groupId, long[] folderIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return findByG_F(
			groupId, folderIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the matching document library file entries that the user has permission to view
	 */
	public default java.util.List<DLFileEntry> filterFindByG_F(
		long groupId, long folderId) {

		return filterFindByG_F(
			groupId, folderId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	public default java.util.List<DLFileEntry> filterFindByG_F(
		long groupId, long folderId, int start, int end) {

		return filterFindByG_F(groupId, folderId, start, end, null);
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the matching document library file entries that the user has permission to view
	 */
	public default java.util.List<DLFileEntry> filterFindByG_F(
		long groupId, long[] folderIds) {

		return filterFindByG_F(
			groupId, folderIds,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	public default java.util.List<DLFileEntry> filterFindByG_F(
		long groupId, long[] folderIds, int start, int end) {

		return filterFindByG_F(groupId, folderIds, start, end, null);
	}

	/**
	 * Returns all the document library file entries where folderId = &#63; and name = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByF_N(
		long folderId, String name) {

		return findByF_N(
			folderId, name, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library file entries where folderId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByF_N(
		long folderId, String name, int start, int end) {

		return findByF_N(folderId, name, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where folderId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByF_N(
		long folderId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return findByF_N(folderId, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @return the matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long folderId) {

		return findByG_U_F(
			groupId, userId, folderId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long folderId, int start, int end) {

		return findByG_U_F(groupId, userId, folderId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long folderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return findByG_U_F(
			groupId, userId, folderId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library file entries where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @return the matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long[] folderIds) {

		return findByG_U_F(
			groupId, userId, folderIds,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long[] folderIds, int start, int end) {

		return findByG_U_F(groupId, userId, folderIds, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long[] folderIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return findByG_U_F(
			groupId, userId, folderIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @return the matching document library file entries that the user has permission to view
	 */
	public default java.util.List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long folderId) {

		return filterFindByG_U_F(
			groupId, userId, folderId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	public default java.util.List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long folderId, int start, int end) {

		return filterFindByG_U_F(groupId, userId, folderId, start, end, null);
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @return the matching document library file entries that the user has permission to view
	 */
	public default java.util.List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long[] folderIds) {

		return filterFindByG_U_F(
			groupId, userId, folderIds,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	public default java.util.List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long[] folderIds, int start, int end) {

		return filterFindByG_U_F(groupId, userId, folderIds, start, end, null);
	}

	/**
	 * Returns all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @return the matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_F_F(
		long groupId, long folderId, long fileEntryTypeId) {

		return findByG_F_F(
			groupId, folderId, fileEntryTypeId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_F_F(
		long groupId, long folderId, long fileEntryTypeId, int start, int end) {

		return findByG_F_F(
			groupId, folderId, fileEntryTypeId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_F_F(
		long groupId, long folderId, long fileEntryTypeId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return findByG_F_F(
			groupId, folderId, fileEntryTypeId, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the document library file entries where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @return the matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId) {

		return findByG_F_F(
			groupId, folderIds, fileEntryTypeId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId, int start,
		int end) {

		return findByG_F_F(
			groupId, folderIds, fileEntryTypeId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return findByG_F_F(
			groupId, folderIds, fileEntryTypeId, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @return the matching document library file entries that the user has permission to view
	 */
	public default java.util.List<DLFileEntry> filterFindByG_F_F(
		long groupId, long folderId, long fileEntryTypeId) {

		return filterFindByG_F_F(
			groupId, folderId, fileEntryTypeId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	public default java.util.List<DLFileEntry> filterFindByG_F_F(
		long groupId, long folderId, long fileEntryTypeId, int start, int end) {

		return filterFindByG_F_F(
			groupId, folderId, fileEntryTypeId, start, end, null);
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @return the matching document library file entries that the user has permission to view
	 */
	public default java.util.List<DLFileEntry> filterFindByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId) {

		return filterFindByG_F_F(
			groupId, folderIds, fileEntryTypeId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	public default java.util.List<DLFileEntry> filterFindByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId, int start,
		int end) {

		return filterFindByG_F_F(
			groupId, folderIds, fileEntryTypeId, start, end, null);
	}

	/**
	 * Returns all the document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByC_C_C(
		long companyId, long classNameId, long classPK) {

		return findByC_C_C(
			companyId, classNameId, classPK,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end) {

		return findByC_C_C(
			companyId, classNameId, classPK, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return findByC_C_C(
			companyId, classNameId, classPK, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @return the matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId) {

		return findByS_L_C1_C2(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId, int start, int end) {

		return findByS_L_C1_C2(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId, start,
			end, null, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public default java.util.List<DLFileEntry> findByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return findByS_L_C1_C2(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId, start,
			end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:356750564