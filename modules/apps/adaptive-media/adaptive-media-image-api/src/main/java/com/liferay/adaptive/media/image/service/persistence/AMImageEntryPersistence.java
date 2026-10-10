/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.service.persistence;

import com.liferay.adaptive.media.image.exception.NoSuchAMImageEntryException;
import com.liferay.adaptive.media.image.model.AMImageEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the am image entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AMImageEntryUtil
 * @generated
 */
@ProviderType
public interface AMImageEntryPersistence
	extends BasePersistence<AMImageEntry>, CTPersistence<AMImageEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link AMImageEntryUtil} to access the am image entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the am image entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching am image entries
	 */
	public java.util.List<AMImageEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AMImageEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first am image entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching am image entry
	 * @throws NoSuchAMImageEntryException if a matching am image entry could not be found
	 */
	public AMImageEntry findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<AMImageEntry>
				orderByComparator)
		throws NoSuchAMImageEntryException;

	/**
	 * Returns the first am image entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching am image entry, or <code>null</code> if a matching am image entry could not be found
	 */
	public AMImageEntry fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<AMImageEntry>
			orderByComparator);

	/**
	 * Removes all the am image entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of am image entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching am image entries
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the am image entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchAMImageEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching am image entry
	 * @throws NoSuchAMImageEntryException if a matching am image entry could not be found
	 */
	public AMImageEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchAMImageEntryException;

	/**
	 * Returns the am image entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching am image entry, or <code>null</code> if a matching am image entry could not be found
	 */
	public AMImageEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the am image entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the am image entry that was removed
	 */
	public AMImageEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchAMImageEntryException;

	/**
	 * Returns the number of am image entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching am image entries
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the am image entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching am image entries
	 */
	public java.util.List<AMImageEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AMImageEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first am image entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching am image entry
	 * @throws NoSuchAMImageEntryException if a matching am image entry could not be found
	 */
	public AMImageEntry findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<AMImageEntry>
				orderByComparator)
		throws NoSuchAMImageEntryException;

	/**
	 * Returns the first am image entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching am image entry, or <code>null</code> if a matching am image entry could not be found
	 */
	public AMImageEntry fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<AMImageEntry>
			orderByComparator);

	/**
	 * Removes all the am image entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of am image entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching am image entries
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the am image entries where fileVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fileVersionId the file version ID
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching am image entries
	 */
	public java.util.List<AMImageEntry> findByFileVersionId(
		long fileVersionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AMImageEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first am image entry in the ordered set where fileVersionId = &#63;.
	 *
	 * @param fileVersionId the file version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching am image entry
	 * @throws NoSuchAMImageEntryException if a matching am image entry could not be found
	 */
	public AMImageEntry findByFileVersionId_First(
			long fileVersionId,
			com.liferay.portal.kernel.util.OrderByComparator<AMImageEntry>
				orderByComparator)
		throws NoSuchAMImageEntryException;

	/**
	 * Returns the first am image entry in the ordered set where fileVersionId = &#63;.
	 *
	 * @param fileVersionId the file version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching am image entry, or <code>null</code> if a matching am image entry could not be found
	 */
	public AMImageEntry fetchByFileVersionId_First(
		long fileVersionId,
		com.liferay.portal.kernel.util.OrderByComparator<AMImageEntry>
			orderByComparator);

	/**
	 * Removes all the am image entries where fileVersionId = &#63; from the database.
	 *
	 * @param fileVersionId the file version ID
	 */
	public void removeByFileVersionId(long fileVersionId);

	/**
	 * Returns the number of am image entries where fileVersionId = &#63;.
	 *
	 * @param fileVersionId the file version ID
	 * @return the number of matching am image entries
	 */
	public int countByFileVersionId(long fileVersionId);

	/**
	 * Returns an ordered range of all the am image entries where companyId = &#63; and configurationUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param configurationUuid the configuration uuid
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching am image entries
	 */
	public java.util.List<AMImageEntry> findByC_C(
		long companyId, String configurationUuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AMImageEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first am image entry in the ordered set where companyId = &#63; and configurationUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param configurationUuid the configuration uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching am image entry
	 * @throws NoSuchAMImageEntryException if a matching am image entry could not be found
	 */
	public AMImageEntry findByC_C_First(
			long companyId, String configurationUuid,
			com.liferay.portal.kernel.util.OrderByComparator<AMImageEntry>
				orderByComparator)
		throws NoSuchAMImageEntryException;

	/**
	 * Returns the first am image entry in the ordered set where companyId = &#63; and configurationUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param configurationUuid the configuration uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching am image entry, or <code>null</code> if a matching am image entry could not be found
	 */
	public AMImageEntry fetchByC_C_First(
		long companyId, String configurationUuid,
		com.liferay.portal.kernel.util.OrderByComparator<AMImageEntry>
			orderByComparator);

	/**
	 * Removes all the am image entries where companyId = &#63; and configurationUuid = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param configurationUuid the configuration uuid
	 */
	public void removeByC_C(long companyId, String configurationUuid);

	/**
	 * Returns the number of am image entries where companyId = &#63; and configurationUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param configurationUuid the configuration uuid
	 * @return the number of matching am image entries
	 */
	public int countByC_C(long companyId, String configurationUuid);

	/**
	 * Returns the am image entry where configurationUuid = &#63; and fileVersionId = &#63; or throws a <code>NoSuchAMImageEntryException</code> if it could not be found.
	 *
	 * @param configurationUuid the configuration uuid
	 * @param fileVersionId the file version ID
	 * @return the matching am image entry
	 * @throws NoSuchAMImageEntryException if a matching am image entry could not be found
	 */
	public AMImageEntry findByC_F(String configurationUuid, long fileVersionId)
		throws NoSuchAMImageEntryException;

	/**
	 * Returns the am image entry where configurationUuid = &#63; and fileVersionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param configurationUuid the configuration uuid
	 * @param fileVersionId the file version ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching am image entry, or <code>null</code> if a matching am image entry could not be found
	 */
	public AMImageEntry fetchByC_F(
		String configurationUuid, long fileVersionId, boolean useFinderCache);

	/**
	 * Removes the am image entry where configurationUuid = &#63; and fileVersionId = &#63; from the database.
	 *
	 * @param configurationUuid the configuration uuid
	 * @param fileVersionId the file version ID
	 * @return the am image entry that was removed
	 */
	public AMImageEntry removeByC_F(
			String configurationUuid, long fileVersionId)
		throws NoSuchAMImageEntryException;

	/**
	 * Returns the number of am image entries where configurationUuid = &#63; and fileVersionId = &#63;.
	 *
	 * @param configurationUuid the configuration uuid
	 * @param fileVersionId the file version ID
	 * @return the number of matching am image entries
	 */
	public int countByC_F(String configurationUuid, long fileVersionId);

	/**
	 * Creates a new am image entry with the primary key. Does not add the am image entry to the database.
	 *
	 * @param amImageEntryId the primary key for the new am image entry
	 * @return the new am image entry
	 */
	public AMImageEntry create(long amImageEntryId);

	/**
	 * Removes the am image entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param amImageEntryId the primary key of the am image entry
	 * @return the am image entry that was removed
	 * @throws NoSuchAMImageEntryException if a am image entry with the primary key could not be found
	 */
	public AMImageEntry remove(long amImageEntryId)
		throws NoSuchAMImageEntryException;

	public AMImageEntry updateImpl(AMImageEntry amImageEntry);

	/**
	 * Returns the am image entry with the primary key or throws a <code>NoSuchAMImageEntryException</code> if it could not be found.
	 *
	 * @param amImageEntryId the primary key of the am image entry
	 * @return the am image entry
	 * @throws NoSuchAMImageEntryException if a am image entry with the primary key could not be found
	 */
	public AMImageEntry findByPrimaryKey(long amImageEntryId)
		throws NoSuchAMImageEntryException;

	/**
	 * Returns the am image entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param amImageEntryId the primary key of the am image entry
	 * @return the am image entry, or <code>null</code> if a am image entry with the primary key could not be found
	 */
	public AMImageEntry fetchByPrimaryKey(long amImageEntryId);

	/**
	 * Returns the am image entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching am image entry, or <code>null</code> if a matching am image entry could not be found
	 */
	public default AMImageEntry fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the am image entry where configurationUuid = &#63; and fileVersionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param configurationUuid the configuration uuid
	 * @param fileVersionId the file version ID
	 * @return the matching am image entry, or <code>null</code> if a matching am image entry could not be found
	 */
	public default AMImageEntry fetchByC_F(
		String configurationUuid, long fileVersionId) {

		return fetchByC_F(configurationUuid, fileVersionId, true);
	}

	/**
	 * Returns all the am image entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching am image entries
	 */
	public default java.util.List<AMImageEntry> findByUuid(String uuid) {
		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the am image entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @return the range of matching am image entries
	 */
	public default java.util.List<AMImageEntry> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the am image entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching am image entries
	 */
	public default java.util.List<AMImageEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AMImageEntry>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the am image entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching am image entries
	 */
	public default java.util.List<AMImageEntry> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the am image entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @return the range of matching am image entries
	 */
	public default java.util.List<AMImageEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the am image entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching am image entries
	 */
	public default java.util.List<AMImageEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AMImageEntry>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the am image entries where fileVersionId = &#63;.
	 *
	 * @param fileVersionId the file version ID
	 * @return the matching am image entries
	 */
	public default java.util.List<AMImageEntry> findByFileVersionId(
		long fileVersionId) {

		return findByFileVersionId(
			fileVersionId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the am image entries where fileVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fileVersionId the file version ID
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @return the range of matching am image entries
	 */
	public default java.util.List<AMImageEntry> findByFileVersionId(
		long fileVersionId, int start, int end) {

		return findByFileVersionId(fileVersionId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the am image entries where fileVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fileVersionId the file version ID
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching am image entries
	 */
	public default java.util.List<AMImageEntry> findByFileVersionId(
		long fileVersionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AMImageEntry>
			orderByComparator) {

		return findByFileVersionId(
			fileVersionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the am image entries where companyId = &#63; and configurationUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param configurationUuid the configuration uuid
	 * @return the matching am image entries
	 */
	public default java.util.List<AMImageEntry> findByC_C(
		long companyId, String configurationUuid) {

		return findByC_C(
			companyId, configurationUuid,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the am image entries where companyId = &#63; and configurationUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param configurationUuid the configuration uuid
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @return the range of matching am image entries
	 */
	public default java.util.List<AMImageEntry> findByC_C(
		long companyId, String configurationUuid, int start, int end) {

		return findByC_C(companyId, configurationUuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the am image entries where companyId = &#63; and configurationUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param configurationUuid the configuration uuid
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching am image entries
	 */
	public default java.util.List<AMImageEntry> findByC_C(
		long companyId, String configurationUuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AMImageEntry>
			orderByComparator) {

		return findByC_C(
			companyId, configurationUuid, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1518751018