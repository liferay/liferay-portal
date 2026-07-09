/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.style.book.exception.NoSuchEntryVersionException;
import com.liferay.style.book.model.StyleBookEntryVersion;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the style book entry version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see StyleBookEntryVersionUtil
 * @generated
 */
@ProviderType
public interface StyleBookEntryVersionPersistence
	extends BasePersistence<StyleBookEntryVersion>,
			CTPersistence<StyleBookEntryVersion> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link StyleBookEntryVersionUtil} to access the style book entry version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the style book entry versions where styleBookEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByStyleBookEntryId(
		long styleBookEntryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByStyleBookEntryId_First(
			long styleBookEntryId,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByStyleBookEntryId_First(
		long styleBookEntryId,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where styleBookEntryId = &#63; from the database.
	 *
	 * @param styleBookEntryId the style book entry ID
	 */
	public void removeByStyleBookEntryId(long styleBookEntryId);

	/**
	 * Returns the number of style book entry versions where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @return the number of matching style book entry versions
	 */
	public int countByStyleBookEntryId(long styleBookEntryId);

	/**
	 * Returns the style book entry version where styleBookEntryId = &#63; and version = &#63; or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param version the version
	 * @return the matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByStyleBookEntryId_Version(
			long styleBookEntryId, int version)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the style book entry version where styleBookEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByStyleBookEntryId_Version(
		long styleBookEntryId, int version, boolean useFinderCache);

	/**
	 * Removes the style book entry version where styleBookEntryId = &#63; and version = &#63; from the database.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param version the version
	 * @return the style book entry version that was removed
	 */
	public StyleBookEntryVersion removeByStyleBookEntryId_Version(
			long styleBookEntryId, int version)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the number of style book entry versions where styleBookEntryId = &#63; and version = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public int countByStyleBookEntryId_Version(
		long styleBookEntryId, int version);

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of style book entry versions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching style book entry versions
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByUuid_Version(
		String uuid, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByUuid_Version_First(
			String uuid, int version,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByUuid_Version_First(
		String uuid, int version,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where uuid = &#63; and version = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 */
	public void removeByUuid_Version(String uuid, int version);

	/**
	 * Returns the number of style book entry versions where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public int countByUuid_Version(String uuid, int version);

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByUUID_G(
		String uuid, long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByUUID_G_First(
			String uuid, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByUUID_G_First(
		String uuid, long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 */
	public void removeByUUID_G(String uuid, long groupId);

	/**
	 * Returns the number of style book entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching style book entry versions
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns the style book entry version where uuid = &#63; and groupId = &#63; and version = &#63; or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByUUID_G_Version(
			String uuid, long groupId, int version)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the style book entry version where uuid = &#63; and groupId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByUUID_G_Version(
		String uuid, long groupId, int version, boolean useFinderCache);

	/**
	 * Removes the style book entry version where uuid = &#63; and groupId = &#63; and version = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the style book entry version that was removed
	 */
	public StyleBookEntryVersion removeByUUID_G_Version(
			String uuid, long groupId, int version)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the number of style book entry versions where uuid = &#63; and groupId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public int countByUUID_G_Version(String uuid, long groupId, int version);

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of style book entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching style book entry versions
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByUuid_C_Version(
		String uuid, long companyId, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByUuid_C_Version_First(
			String uuid, long companyId, int version,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByUuid_C_Version_First(
		String uuid, long companyId, int version,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where uuid = &#63; and companyId = &#63; and version = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 */
	public void removeByUuid_C_Version(
		String uuid, long companyId, int version);

	/**
	 * Returns the number of style book entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public int countByUuid_C_Version(String uuid, long companyId, int version);

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of style book entry versions where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching style book entry versions
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByGroupId_Version(
		long groupId, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByGroupId_Version_First(
			long groupId, int version,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByGroupId_Version_First(
		long groupId, int version,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where groupId = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 */
	public void removeByGroupId_Version(long groupId, int version);

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public int countByGroupId_Version(long groupId, int version);

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByG_D(
		long groupId, boolean defaultStyleBookEntry, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByG_D_First(
			long groupId, boolean defaultStyleBookEntry,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByG_D_First(
		long groupId, boolean defaultStyleBookEntry,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 */
	public void removeByG_D(long groupId, boolean defaultStyleBookEntry);

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @return the number of matching style book entry versions
	 */
	public int countByG_D(long groupId, boolean defaultStyleBookEntry);

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByG_D_Version(
		long groupId, boolean defaultStyleBookEntry, int version, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByG_D_Version_First(
			long groupId, boolean defaultStyleBookEntry, int version,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByG_D_Version_First(
		long groupId, boolean defaultStyleBookEntry, int version,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 */
	public void removeByG_D_Version(
		long groupId, boolean defaultStyleBookEntry, int version);

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public int countByG_D_Version(
		long groupId, boolean defaultStyleBookEntry, int version);

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByG_N(
		long groupId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByG_N_First(
			long groupId, String name,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByG_N_First(
		long groupId, String name,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where groupId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 */
	public void removeByG_N(long groupId, String name);

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching style book entry versions
	 */
	public int countByG_N(long groupId, String name);

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByG_N_Version(
		long groupId, String name, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByG_N_Version_First(
			long groupId, String name, int version,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByG_N_Version_First(
		long groupId, String name, int version,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 */
	public void removeByG_N_Version(long groupId, String name, int version);

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public int countByG_N_Version(long groupId, String name, int version);

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByG_LikeN(
		long groupId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByG_LikeN_First(
			long groupId, String name,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByG_LikeN_First(
		long groupId, String name,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where groupId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 */
	public void removeByG_LikeN(long groupId, String name);

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching style book entry versions
	 */
	public int countByG_LikeN(long groupId, String name);

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByG_LikeN_Version(
		long groupId, String name, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByG_LikeN_Version_First(
			long groupId, String name, int version,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByG_LikeN_Version_First(
		long groupId, String name, int version,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 */
	public void removeByG_LikeN_Version(long groupId, String name, int version);

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public int countByG_LikeN_Version(long groupId, String name, int version);

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByG_SBEK(
		long groupId, String styleBookEntryKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByG_SBEK_First(
			long groupId, String styleBookEntryKey,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByG_SBEK_First(
		long groupId, String styleBookEntryKey,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where groupId = &#63; and styleBookEntryKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 */
	public void removeByG_SBEK(long groupId, String styleBookEntryKey);

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @return the number of matching style book entry versions
	 */
	public int countByG_SBEK(long groupId, String styleBookEntryKey);

	/**
	 * Returns the style book entry version where groupId = &#63; and styleBookEntryKey = &#63; and version = &#63; or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param version the version
	 * @return the matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByG_SBEK_Version(
			long groupId, String styleBookEntryKey, int version)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the style book entry version where groupId = &#63; and styleBookEntryKey = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByG_SBEK_Version(
		long groupId, String styleBookEntryKey, int version,
		boolean useFinderCache);

	/**
	 * Removes the style book entry version where groupId = &#63; and styleBookEntryKey = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param version the version
	 * @return the style book entry version that was removed
	 */
	public StyleBookEntryVersion removeByG_SBEK_Version(
			long groupId, String styleBookEntryKey, int version)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and styleBookEntryKey = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public int countByG_SBEK_Version(
		long groupId, String styleBookEntryKey, int version);

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByG_T(
		long groupId, String themeId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByG_T_First(
			long groupId, String themeId,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByG_T_First(
		long groupId, String themeId,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where groupId = &#63; and themeId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 */
	public void removeByG_T(long groupId, String themeId);

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @return the number of matching style book entry versions
	 */
	public int countByG_T(long groupId, String themeId);

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByG_T_Version(
		long groupId, String themeId, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByG_T_Version_First(
			long groupId, String themeId, int version,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByG_T_Version_First(
		long groupId, String themeId, int version,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where groupId = &#63; and themeId = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 */
	public void removeByG_T_Version(long groupId, String themeId, int version);

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public int countByG_T_Version(long groupId, String themeId, int version);

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByG_D_T(
		long groupId, boolean defaultStyleBookEntry, String themeId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByG_D_T_First(
			long groupId, boolean defaultStyleBookEntry, String themeId,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByG_D_T_First(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 */
	public void removeByG_D_T(
		long groupId, boolean defaultStyleBookEntry, String themeId);

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @return the number of matching style book entry versions
	 */
	public int countByG_D_T(
		long groupId, boolean defaultStyleBookEntry, String themeId);

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByG_D_T_Version(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByG_D_T_Version_First(
			long groupId, boolean defaultStyleBookEntry, String themeId,
			int version,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByG_D_T_Version_First(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		int version,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 */
	public void removeByG_D_T_Version(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		int version);

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public int countByG_D_T_Version(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		int version);

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByG_LikeN_T(
		long groupId, String name, String themeId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByG_LikeN_T_First(
			long groupId, String name, String themeId,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByG_LikeN_T_First(
		long groupId, String name, String themeId,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 */
	public void removeByG_LikeN_T(long groupId, String name, String themeId);

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @return the number of matching style book entry versions
	 */
	public int countByG_LikeN_T(long groupId, String name, String themeId);

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public java.util.List<StyleBookEntryVersion> findByG_LikeN_T_Version(
		long groupId, String name, String themeId, int version, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion findByG_LikeN_T_Version_First(
			long groupId, String name, String themeId, int version,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public StyleBookEntryVersion fetchByG_LikeN_T_Version_First(
		long groupId, String name, String themeId, int version,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator);

	/**
	 * Removes all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 */
	public void removeByG_LikeN_T_Version(
		long groupId, String name, String themeId, int version);

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public int countByG_LikeN_T_Version(
		long groupId, String name, String themeId, int version);

	/**
	 * Creates a new style book entry version with the primary key. Does not add the style book entry version to the database.
	 *
	 * @param styleBookEntryVersionId the primary key for the new style book entry version
	 * @return the new style book entry version
	 */
	public StyleBookEntryVersion create(long styleBookEntryVersionId);

	/**
	 * Removes the style book entry version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param styleBookEntryVersionId the primary key of the style book entry version
	 * @return the style book entry version that was removed
	 * @throws NoSuchEntryVersionException if a style book entry version with the primary key could not be found
	 */
	public StyleBookEntryVersion remove(long styleBookEntryVersionId)
		throws NoSuchEntryVersionException;

	public StyleBookEntryVersion updateImpl(
		StyleBookEntryVersion styleBookEntryVersion);

	/**
	 * Returns the style book entry version with the primary key or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param styleBookEntryVersionId the primary key of the style book entry version
	 * @return the style book entry version
	 * @throws NoSuchEntryVersionException if a style book entry version with the primary key could not be found
	 */
	public StyleBookEntryVersion findByPrimaryKey(long styleBookEntryVersionId)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the style book entry version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param styleBookEntryVersionId the primary key of the style book entry version
	 * @return the style book entry version, or <code>null</code> if a style book entry version with the primary key could not be found
	 */
	public StyleBookEntryVersion fetchByPrimaryKey(
		long styleBookEntryVersionId);

	/**
	 * Returns the style book entry version where styleBookEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param version the version
	 * @return the matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public default StyleBookEntryVersion fetchByStyleBookEntryId_Version(
		long styleBookEntryId, int version) {

		return fetchByStyleBookEntryId_Version(styleBookEntryId, version, true);
	}

	/**
	 * Returns the style book entry version where uuid = &#63; and groupId = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public default StyleBookEntryVersion fetchByUUID_G_Version(
		String uuid, long groupId, int version) {

		return fetchByUUID_G_Version(uuid, groupId, version, true);
	}

	/**
	 * Returns the style book entry version where groupId = &#63; and styleBookEntryKey = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param version the version
	 * @return the matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public default StyleBookEntryVersion fetchByG_SBEK_Version(
		long groupId, String styleBookEntryKey, int version) {

		return fetchByG_SBEK_Version(groupId, styleBookEntryKey, version, true);
	}

	/**
	 * Returns all the style book entry versions where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByStyleBookEntryId(
		long styleBookEntryId) {

		return findByStyleBookEntryId(
			styleBookEntryId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where styleBookEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByStyleBookEntryId(
		long styleBookEntryId, int start, int end) {

		return findByStyleBookEntryId(styleBookEntryId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where styleBookEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByStyleBookEntryId(
		long styleBookEntryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByStyleBookEntryId(
			styleBookEntryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the style book entry versions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByUuid(
		String uuid) {

		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the style book entry versions where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByUuid_Version(
		String uuid, int version) {

		return findByUuid_Version(
			uuid, version, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where uuid = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByUuid_Version(
		String uuid, int version, int start, int end) {

		return findByUuid_Version(uuid, version, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByUuid_Version(
		String uuid, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByUuid_Version(
			uuid, version, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the style book entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByUUID_G(
		String uuid, long groupId) {

		return findByUUID_G(
			uuid, groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByUUID_G(
		String uuid, long groupId, int start, int end) {

		return findByUUID_G(uuid, groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByUUID_G(
		String uuid, long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByUUID_G(uuid, groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the style book entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the style book entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByUuid_C_Version(
		String uuid, long companyId, int version) {

		return findByUuid_C_Version(
			uuid, companyId, version,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByUuid_C_Version(
		String uuid, long companyId, int version, int start, int end) {

		return findByUuid_C_Version(
			uuid, companyId, version, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByUuid_C_Version(
		String uuid, long companyId, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByUuid_C_Version(
			uuid, companyId, version, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByGroupId(
		long groupId) {

		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByGroupId_Version(
		long groupId, int version) {

		return findByGroupId_Version(
			groupId, version,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByGroupId_Version(
		long groupId, int version, int start, int end) {

		return findByGroupId_Version(groupId, version, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByGroupId_Version(
		long groupId, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByGroupId_Version(
			groupId, version, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_D(
		long groupId, boolean defaultStyleBookEntry) {

		return findByG_D(
			groupId, defaultStyleBookEntry,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_D(
		long groupId, boolean defaultStyleBookEntry, int start, int end) {

		return findByG_D(
			groupId, defaultStyleBookEntry, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_D(
		long groupId, boolean defaultStyleBookEntry, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByG_D(
			groupId, defaultStyleBookEntry, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_D_Version(
		long groupId, boolean defaultStyleBookEntry, int version) {

		return findByG_D_Version(
			groupId, defaultStyleBookEntry, version,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_D_Version(
		long groupId, boolean defaultStyleBookEntry, int version, int start,
		int end) {

		return findByG_D_Version(
			groupId, defaultStyleBookEntry, version, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_D_Version(
		long groupId, boolean defaultStyleBookEntry, int version, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByG_D_Version(
			groupId, defaultStyleBookEntry, version, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_N(
		long groupId, String name) {

		return findByG_N(
			groupId, name, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_N(
		long groupId, String name, int start, int end) {

		return findByG_N(groupId, name, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_N(
		long groupId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByG_N(groupId, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_N_Version(
		long groupId, String name, int version) {

		return findByG_N_Version(
			groupId, name, version,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_N_Version(
		long groupId, String name, int version, int start, int end) {

		return findByG_N_Version(
			groupId, name, version, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_N_Version(
		long groupId, String name, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByG_N_Version(
			groupId, name, version, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_LikeN(
		long groupId, String name) {

		return findByG_LikeN(
			groupId, name, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_LikeN(
		long groupId, String name, int start, int end) {

		return findByG_LikeN(groupId, name, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_LikeN(
		long groupId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByG_LikeN(
			groupId, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_LikeN_Version(
		long groupId, String name, int version) {

		return findByG_LikeN_Version(
			groupId, name, version,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_LikeN_Version(
		long groupId, String name, int version, int start, int end) {

		return findByG_LikeN_Version(
			groupId, name, version, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_LikeN_Version(
		long groupId, String name, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByG_LikeN_Version(
			groupId, name, version, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_SBEK(
		long groupId, String styleBookEntryKey) {

		return findByG_SBEK(
			groupId, styleBookEntryKey,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_SBEK(
		long groupId, String styleBookEntryKey, int start, int end) {

		return findByG_SBEK(groupId, styleBookEntryKey, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_SBEK(
		long groupId, String styleBookEntryKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByG_SBEK(
			groupId, styleBookEntryKey, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_T(
		long groupId, String themeId) {

		return findByG_T(
			groupId, themeId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_T(
		long groupId, String themeId, int start, int end) {

		return findByG_T(groupId, themeId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_T(
		long groupId, String themeId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByG_T(groupId, themeId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_T_Version(
		long groupId, String themeId, int version) {

		return findByG_T_Version(
			groupId, themeId, version,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_T_Version(
		long groupId, String themeId, int version, int start, int end) {

		return findByG_T_Version(
			groupId, themeId, version, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_T_Version(
		long groupId, String themeId, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByG_T_Version(
			groupId, themeId, version, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_D_T(
		long groupId, boolean defaultStyleBookEntry, String themeId) {

		return findByG_D_T(
			groupId, defaultStyleBookEntry, themeId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_D_T(
		long groupId, boolean defaultStyleBookEntry, String themeId, int start,
		int end) {

		return findByG_D_T(
			groupId, defaultStyleBookEntry, themeId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_D_T(
		long groupId, boolean defaultStyleBookEntry, String themeId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByG_D_T(
			groupId, defaultStyleBookEntry, themeId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_D_T_Version(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		int version) {

		return findByG_D_T_Version(
			groupId, defaultStyleBookEntry, themeId, version,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_D_T_Version(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		int version, int start, int end) {

		return findByG_D_T_Version(
			groupId, defaultStyleBookEntry, themeId, version, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_D_T_Version(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByG_D_T_Version(
			groupId, defaultStyleBookEntry, themeId, version, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_LikeN_T(
		long groupId, String name, String themeId) {

		return findByG_LikeN_T(
			groupId, name, themeId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_LikeN_T(
		long groupId, String name, String themeId, int start, int end) {

		return findByG_LikeN_T(groupId, name, themeId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion> findByG_LikeN_T(
		long groupId, String name, String themeId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntryVersion>
			orderByComparator) {

		return findByG_LikeN_T(
			groupId, name, themeId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 * @return the matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion>
		findByG_LikeN_T_Version(
			long groupId, String name, String themeId, int version) {

		return findByG_LikeN_T_Version(
			groupId, name, themeId, version,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion>
		findByG_LikeN_T_Version(
			long groupId, String name, String themeId, int version, int start,
			int end) {

		return findByG_LikeN_T_Version(
			groupId, name, themeId, version, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public default java.util.List<StyleBookEntryVersion>
		findByG_LikeN_T_Version(
			long groupId, String name, String themeId, int version, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookEntryVersion> orderByComparator) {

		return findByG_LikeN_T_Version(
			groupId, name, themeId, version, start, end, orderByComparator,
			true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1683857048