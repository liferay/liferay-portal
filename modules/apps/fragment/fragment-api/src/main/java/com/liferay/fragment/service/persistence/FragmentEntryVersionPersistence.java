/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.persistence;

import com.liferay.fragment.exception.NoSuchEntryVersionException;
import com.liferay.fragment.model.FragmentEntryVersion;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the fragment entry version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryVersionUtil
 * @generated
 */
@ProviderType
public interface FragmentEntryVersionPersistence
	extends BasePersistence<FragmentEntryVersion>,
			CTPersistence<FragmentEntryVersion> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link FragmentEntryVersionUtil} to access the fragment entry version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the fragment entry versions where fragmentEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByFragmentEntryId(
		long fragmentEntryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where fragmentEntryId = &#63;.
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByFragmentEntryId_First(
			long fragmentEntryId,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where fragmentEntryId = &#63;.
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByFragmentEntryId_First(
		long fragmentEntryId,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where fragmentEntryId = &#63; from the database.
	 *
	 * @param fragmentEntryId the fragment entry ID
	 */
	public void removeByFragmentEntryId(long fragmentEntryId);

	/**
	 * Returns the number of fragment entry versions where fragmentEntryId = &#63;.
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @return the number of matching fragment entry versions
	 */
	public int countByFragmentEntryId(long fragmentEntryId);

	/**
	 * Returns the fragment entry version where fragmentEntryId = &#63; and version = &#63; or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @param version the version
	 * @return the matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByFragmentEntryId_Version(
			long fragmentEntryId, int version)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the fragment entry version where fragmentEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByFragmentEntryId_Version(
		long fragmentEntryId, int version, boolean useFinderCache);

	/**
	 * Removes the fragment entry version where fragmentEntryId = &#63; and version = &#63; from the database.
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @param version the version
	 * @return the fragment entry version that was removed
	 */
	public FragmentEntryVersion removeByFragmentEntryId_Version(
			long fragmentEntryId, int version)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the number of fragment entry versions where fragmentEntryId = &#63; and version = &#63;.
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	public int countByFragmentEntryId_Version(
		long fragmentEntryId, int version);

	/**
	 * Returns an ordered range of all the fragment entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of fragment entry versions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching fragment entry versions
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns an ordered range of all the fragment entry versions where uuid = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByUuid_Version(
		String uuid, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByUuid_Version_First(
			String uuid, int version,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByUuid_Version_First(
		String uuid, int version,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where uuid = &#63; and version = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 */
	public void removeByUuid_Version(String uuid, int version);

	/**
	 * Returns the number of fragment entry versions where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	public int countByUuid_Version(String uuid, int version);

	/**
	 * Returns an ordered range of all the fragment entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByUUID_G(
		String uuid, long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByUUID_G_First(
			String uuid, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByUUID_G_First(
		String uuid, long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 */
	public void removeByUUID_G(String uuid, long groupId);

	/**
	 * Returns the number of fragment entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching fragment entry versions
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns the fragment entry version where uuid = &#63; and groupId = &#63; and version = &#63; or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByUUID_G_Version(
			String uuid, long groupId, int version)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the fragment entry version where uuid = &#63; and groupId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByUUID_G_Version(
		String uuid, long groupId, int version, boolean useFinderCache);

	/**
	 * Removes the fragment entry version where uuid = &#63; and groupId = &#63; and version = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the fragment entry version that was removed
	 */
	public FragmentEntryVersion removeByUUID_G_Version(
			String uuid, long groupId, int version)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the number of fragment entry versions where uuid = &#63; and groupId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	public int countByUUID_G_Version(String uuid, long groupId, int version);

	/**
	 * Returns an ordered range of all the fragment entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of fragment entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching fragment entry versions
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the fragment entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByUuid_C_Version(
		String uuid, long companyId, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByUuid_C_Version_First(
			String uuid, long companyId, int version,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByUuid_C_Version_First(
		String uuid, long companyId, int version,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where uuid = &#63; and companyId = &#63; and version = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 */
	public void removeByUuid_C_Version(
		String uuid, long companyId, int version);

	/**
	 * Returns the number of fragment entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	public int countByUuid_C_Version(String uuid, long companyId, int version);

	/**
	 * Returns an ordered range of all the fragment entry versions where fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByFragmentCollectionId(
		long fragmentCollectionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where fragmentCollectionId = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByFragmentCollectionId_First(
			long fragmentCollectionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where fragmentCollectionId = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByFragmentCollectionId_First(
		long fragmentCollectionId,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where fragmentCollectionId = &#63; from the database.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 */
	public void removeByFragmentCollectionId(long fragmentCollectionId);

	/**
	 * Returns the number of fragment entry versions where fragmentCollectionId = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @return the number of matching fragment entry versions
	 */
	public int countByFragmentCollectionId(long fragmentCollectionId);

	/**
	 * Returns an ordered range of all the fragment entry versions where fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion>
		findByFragmentCollectionId_Version(
			long fragmentCollectionId, int version, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator,
			boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByFragmentCollectionId_Version_First(
			long fragmentCollectionId, int version,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByFragmentCollectionId_Version_First(
		long fragmentCollectionId, int version,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where fragmentCollectionId = &#63; and version = &#63; from the database.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 */
	public void removeByFragmentCollectionId_Version(
		long fragmentCollectionId, int version);

	/**
	 * Returns the number of fragment entry versions where fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	public int countByFragmentCollectionId_Version(
		long fragmentCollectionId, int version);

	/**
	 * Returns an ordered range of all the fragment entry versions where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByType(
		int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByType_First(
			int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByType_First(
		int type,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	public void removeByType(int type);

	/**
	 * Returns the number of fragment entry versions where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching fragment entry versions
	 */
	public int countByType(int type);

	/**
	 * Returns an ordered range of all the fragment entry versions where type = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByType_Version(
		int type, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where type = &#63; and version = &#63;.
	 *
	 * @param type the type
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByType_Version_First(
			int type, int version,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where type = &#63; and version = &#63;.
	 *
	 * @param type the type
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByType_Version_First(
		int type, int version,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where type = &#63; and version = &#63; from the database.
	 *
	 * @param type the type
	 * @param version the version
	 */
	public void removeByType_Version(int type, int version);

	/**
	 * Returns the number of fragment entry versions where type = &#63; and version = &#63;.
	 *
	 * @param type the type
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	public int countByType_Version(int type, int version);

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByG_FCI(
		long groupId, long fragmentCollectionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByG_FCI_First(
			long groupId, long fragmentCollectionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByG_FCI_First(
		long groupId, long fragmentCollectionId,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 */
	public void removeByG_FCI(long groupId, long fragmentCollectionId);

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @return the number of matching fragment entry versions
	 */
	public int countByG_FCI(long groupId, long fragmentCollectionId);

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByG_FCI_Version(
		long groupId, long fragmentCollectionId, int version, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByG_FCI_Version_First(
			long groupId, long fragmentCollectionId, int version,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByG_FCI_Version_First(
		long groupId, long fragmentCollectionId, int version,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 */
	public void removeByG_FCI_Version(
		long groupId, long fragmentCollectionId, int version);

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	public int countByG_FCI_Version(
		long groupId, long fragmentCollectionId, int version);

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentEntryKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByG_FEK(
		long groupId, String fragmentEntryKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByG_FEK_First(
			long groupId, String fragmentEntryKey,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByG_FEK_First(
		long groupId, String fragmentEntryKey,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentEntryKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 */
	public void removeByG_FEK(long groupId, String fragmentEntryKey);

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @return the number of matching fragment entry versions
	 */
	public int countByG_FEK(long groupId, String fragmentEntryKey);

	/**
	 * Returns the fragment entry version where groupId = &#63; and fragmentEntryKey = &#63; and version = &#63; or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param version the version
	 * @return the matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByG_FEK_Version(
			long groupId, String fragmentEntryKey, int version)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the fragment entry version where groupId = &#63; and fragmentEntryKey = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByG_FEK_Version(
		long groupId, String fragmentEntryKey, int version,
		boolean useFinderCache);

	/**
	 * Removes the fragment entry version where groupId = &#63; and fragmentEntryKey = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param version the version
	 * @return the fragment entry version that was removed
	 */
	public FragmentEntryVersion removeByG_FEK_Version(
			long groupId, String fragmentEntryKey, int version)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentEntryKey = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	public int countByG_FEK_Version(
		long groupId, String fragmentEntryKey, int version);

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByG_FCI_LikeN_First(
			long groupId, long fragmentCollectionId, String name,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByG_FCI_LikeN_First(
		long groupId, long fragmentCollectionId, String name,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 */
	public void removeByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name);

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @return the number of matching fragment entry versions
	 */
	public int countByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name);

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByG_FCI_LikeN_Version(
		long groupId, long fragmentCollectionId, String name, int version,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByG_FCI_LikeN_Version_First(
			long groupId, long fragmentCollectionId, String name, int version,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByG_FCI_LikeN_Version_First(
		long groupId, long fragmentCollectionId, String name, int version,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param version the version
	 */
	public void removeByG_FCI_LikeN_Version(
		long groupId, long fragmentCollectionId, String name, int version);

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	public int countByG_FCI_LikeN_Version(
		long groupId, long fragmentCollectionId, String name, int version);

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByG_FCI_T(
		long groupId, long fragmentCollectionId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByG_FCI_T_First(
			long groupId, long fragmentCollectionId, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByG_FCI_T_First(
		long groupId, long fragmentCollectionId, int type,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 */
	public void removeByG_FCI_T(
		long groupId, long fragmentCollectionId, int type);

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @return the number of matching fragment entry versions
	 */
	public int countByG_FCI_T(
		long groupId, long fragmentCollectionId, int type);

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByG_FCI_T_Version(
		long groupId, long fragmentCollectionId, int type, int version,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByG_FCI_T_Version_First(
			long groupId, long fragmentCollectionId, int type, int version,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByG_FCI_T_Version_First(
		long groupId, long fragmentCollectionId, int type, int version,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param version the version
	 */
	public void removeByG_FCI_T_Version(
		long groupId, long fragmentCollectionId, int type, int version);

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	public int countByG_FCI_T_Version(
		long groupId, long fragmentCollectionId, int type, int version);

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByG_FCI_S(
		long groupId, long fragmentCollectionId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByG_FCI_S_First(
			long groupId, long fragmentCollectionId, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByG_FCI_S_First(
		long groupId, long fragmentCollectionId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 */
	public void removeByG_FCI_S(
		long groupId, long fragmentCollectionId, int status);

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @return the number of matching fragment entry versions
	 */
	public int countByG_FCI_S(
		long groupId, long fragmentCollectionId, int status);

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByG_FCI_S_Version(
		long groupId, long fragmentCollectionId, int status, int version,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByG_FCI_S_Version_First(
			long groupId, long fragmentCollectionId, int status, int version,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByG_FCI_S_Version_First(
		long groupId, long fragmentCollectionId, int status, int version,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param version the version
	 */
	public void removeByG_FCI_S_Version(
		long groupId, long fragmentCollectionId, int status, int version);

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	public int countByG_FCI_S_Version(
		long groupId, long fragmentCollectionId, int status, int version);

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByG_FCI_LikeN_S_First(
			long groupId, long fragmentCollectionId, String name, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByG_FCI_LikeN_S_First(
		long groupId, long fragmentCollectionId, String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 */
	public void removeByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status);

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @return the number of matching fragment entry versions
	 */
	public int countByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status);

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByG_FCI_LikeN_S_Version(
		long groupId, long fragmentCollectionId, String name, int status,
		int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByG_FCI_LikeN_S_Version_First(
			long groupId, long fragmentCollectionId, String name, int status,
			int version,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByG_FCI_LikeN_S_Version_First(
		long groupId, long fragmentCollectionId, String name, int status,
		int version,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param version the version
	 */
	public void removeByG_FCI_LikeN_S_Version(
		long groupId, long fragmentCollectionId, String name, int status,
		int version);

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	public int countByG_FCI_LikeN_S_Version(
		long groupId, long fragmentCollectionId, String name, int status,
		int version);

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByG_FCI_T_S(
		long groupId, long fragmentCollectionId, int type, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByG_FCI_T_S_First(
			long groupId, long fragmentCollectionId, int type, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByG_FCI_T_S_First(
		long groupId, long fragmentCollectionId, int type, int status,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 */
	public void removeByG_FCI_T_S(
		long groupId, long fragmentCollectionId, int type, int status);

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @return the number of matching fragment entry versions
	 */
	public int countByG_FCI_T_S(
		long groupId, long fragmentCollectionId, int type, int status);

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	public java.util.List<FragmentEntryVersion> findByG_FCI_T_S_Version(
		long groupId, long fragmentCollectionId, int type, int status,
		int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion findByG_FCI_T_S_Version_First(
			long groupId, long fragmentCollectionId, int type, int status,
			int version,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public FragmentEntryVersion fetchByG_FCI_T_S_Version_First(
		long groupId, long fragmentCollectionId, int type, int status,
		int version,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator);

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param version the version
	 */
	public void removeByG_FCI_T_S_Version(
		long groupId, long fragmentCollectionId, int type, int status,
		int version);

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	public int countByG_FCI_T_S_Version(
		long groupId, long fragmentCollectionId, int type, int status,
		int version);

	/**
	 * Creates a new fragment entry version with the primary key. Does not add the fragment entry version to the database.
	 *
	 * @param fragmentEntryVersionId the primary key for the new fragment entry version
	 * @return the new fragment entry version
	 */
	public FragmentEntryVersion create(long fragmentEntryVersionId);

	/**
	 * Removes the fragment entry version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fragmentEntryVersionId the primary key of the fragment entry version
	 * @return the fragment entry version that was removed
	 * @throws NoSuchEntryVersionException if a fragment entry version with the primary key could not be found
	 */
	public FragmentEntryVersion remove(long fragmentEntryVersionId)
		throws NoSuchEntryVersionException;

	public FragmentEntryVersion updateImpl(
		FragmentEntryVersion fragmentEntryVersion);

	/**
	 * Returns the fragment entry version with the primary key or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param fragmentEntryVersionId the primary key of the fragment entry version
	 * @return the fragment entry version
	 * @throws NoSuchEntryVersionException if a fragment entry version with the primary key could not be found
	 */
	public FragmentEntryVersion findByPrimaryKey(long fragmentEntryVersionId)
		throws NoSuchEntryVersionException;

	/**
	 * Returns the fragment entry version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fragmentEntryVersionId the primary key of the fragment entry version
	 * @return the fragment entry version, or <code>null</code> if a fragment entry version with the primary key could not be found
	 */
	public FragmentEntryVersion fetchByPrimaryKey(long fragmentEntryVersionId);

	/**
	 * Returns the fragment entry version where fragmentEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @param version the version
	 * @return the matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public default FragmentEntryVersion fetchByFragmentEntryId_Version(
		long fragmentEntryId, int version) {

		return fetchByFragmentEntryId_Version(fragmentEntryId, version, true);
	}

	/**
	 * Returns the fragment entry version where uuid = &#63; and groupId = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public default FragmentEntryVersion fetchByUUID_G_Version(
		String uuid, long groupId, int version) {

		return fetchByUUID_G_Version(uuid, groupId, version, true);
	}

	/**
	 * Returns the fragment entry version where groupId = &#63; and fragmentEntryKey = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param version the version
	 * @return the matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	public default FragmentEntryVersion fetchByG_FEK_Version(
		long groupId, String fragmentEntryKey, int version) {

		return fetchByG_FEK_Version(groupId, fragmentEntryKey, version, true);
	}

	/**
	 * Returns all the fragment entry versions where fragmentEntryId = &#63;.
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByFragmentEntryId(
		long fragmentEntryId) {

		return findByFragmentEntryId(
			fragmentEntryId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where fragmentEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByFragmentEntryId(
		long fragmentEntryId, int start, int end) {

		return findByFragmentEntryId(fragmentEntryId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where fragmentEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByFragmentEntryId(
		long fragmentEntryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator) {

		return findByFragmentEntryId(
			fragmentEntryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByUuid(
		String uuid) {

		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByUuid_Version(
		String uuid, int version) {

		return findByUuid_Version(
			uuid, version, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where uuid = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByUuid_Version(
		String uuid, int version, int start, int end) {

		return findByUuid_Version(uuid, version, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where uuid = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByUuid_Version(
		String uuid, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator) {

		return findByUuid_Version(
			uuid, version, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByUUID_G(
		String uuid, long groupId) {

		return findByUUID_G(
			uuid, groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByUUID_G(
		String uuid, long groupId, int start, int end) {

		return findByUUID_G(uuid, groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByUUID_G(
		String uuid, long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator) {

		return findByUUID_G(uuid, groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByUuid_C_Version(
		String uuid, long companyId, int version) {

		return findByUuid_C_Version(
			uuid, companyId, version,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByUuid_C_Version(
		String uuid, long companyId, int version, int start, int end) {

		return findByUuid_C_Version(
			uuid, companyId, version, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByUuid_C_Version(
		String uuid, long companyId, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator) {

		return findByUuid_C_Version(
			uuid, companyId, version, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where fragmentCollectionId = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion>
		findByFragmentCollectionId(long fragmentCollectionId) {

		return findByFragmentCollectionId(
			fragmentCollectionId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion>
		findByFragmentCollectionId(
			long fragmentCollectionId, int start, int end) {

		return findByFragmentCollectionId(
			fragmentCollectionId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion>
		findByFragmentCollectionId(
			long fragmentCollectionId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator) {

		return findByFragmentCollectionId(
			fragmentCollectionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion>
		findByFragmentCollectionId_Version(
			long fragmentCollectionId, int version) {

		return findByFragmentCollectionId_Version(
			fragmentCollectionId, version,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion>
		findByFragmentCollectionId_Version(
			long fragmentCollectionId, int version, int start, int end) {

		return findByFragmentCollectionId_Version(
			fragmentCollectionId, version, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion>
		findByFragmentCollectionId_Version(
			long fragmentCollectionId, int version, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator) {

		return findByFragmentCollectionId_Version(
			fragmentCollectionId, version, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where type = &#63;.
	 *
	 * @param type the type
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByType(int type) {
		return findByType(
			type, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByType(
		int type, int start, int end) {

		return findByType(type, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByType(
		int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator) {

		return findByType(type, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where type = &#63; and version = &#63;.
	 *
	 * @param type the type
	 * @param version the version
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByType_Version(
		int type, int version) {

		return findByType_Version(
			type, version, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where type = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByType_Version(
		int type, int version, int start, int end) {

		return findByType_Version(type, version, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where type = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByType_Version(
		int type, int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator) {

		return findByType_Version(
			type, version, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI(
		long groupId, long fragmentCollectionId) {

		return findByG_FCI(
			groupId, fragmentCollectionId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI(
		long groupId, long fragmentCollectionId, int start, int end) {

		return findByG_FCI(
			groupId, fragmentCollectionId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI(
		long groupId, long fragmentCollectionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator) {

		return findByG_FCI(
			groupId, fragmentCollectionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_Version(
		long groupId, long fragmentCollectionId, int version) {

		return findByG_FCI_Version(
			groupId, fragmentCollectionId, version,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_Version(
		long groupId, long fragmentCollectionId, int version, int start,
		int end) {

		return findByG_FCI_Version(
			groupId, fragmentCollectionId, version, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_Version(
		long groupId, long fragmentCollectionId, int version, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator) {

		return findByG_FCI_Version(
			groupId, fragmentCollectionId, version, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where groupId = &#63; and fragmentEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FEK(
		long groupId, String fragmentEntryKey) {

		return findByG_FEK(
			groupId, fragmentEntryKey,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where groupId = &#63; and fragmentEntryKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FEK(
		long groupId, String fragmentEntryKey, int start, int end) {

		return findByG_FEK(groupId, fragmentEntryKey, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentEntryKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FEK(
		long groupId, String fragmentEntryKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator) {

		return findByG_FEK(
			groupId, fragmentEntryKey, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name) {

		return findByG_FCI_LikeN(
			groupId, fragmentCollectionId, name,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name, int start,
		int end) {

		return findByG_FCI_LikeN(
			groupId, fragmentCollectionId, name, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator) {

		return findByG_FCI_LikeN(
			groupId, fragmentCollectionId, name, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param version the version
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion>
		findByG_FCI_LikeN_Version(
			long groupId, long fragmentCollectionId, String name, int version) {

		return findByG_FCI_LikeN_Version(
			groupId, fragmentCollectionId, name, version,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion>
		findByG_FCI_LikeN_Version(
			long groupId, long fragmentCollectionId, String name, int version,
			int start, int end) {

		return findByG_FCI_LikeN_Version(
			groupId, fragmentCollectionId, name, version, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion>
		findByG_FCI_LikeN_Version(
			long groupId, long fragmentCollectionId, String name, int version,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator) {

		return findByG_FCI_LikeN_Version(
			groupId, fragmentCollectionId, name, version, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_T(
		long groupId, long fragmentCollectionId, int type) {

		return findByG_FCI_T(
			groupId, fragmentCollectionId, type,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_T(
		long groupId, long fragmentCollectionId, int type, int start, int end) {

		return findByG_FCI_T(
			groupId, fragmentCollectionId, type, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_T(
		long groupId, long fragmentCollectionId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator) {

		return findByG_FCI_T(
			groupId, fragmentCollectionId, type, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param version the version
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_T_Version(
		long groupId, long fragmentCollectionId, int type, int version) {

		return findByG_FCI_T_Version(
			groupId, fragmentCollectionId, type, version,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_T_Version(
		long groupId, long fragmentCollectionId, int type, int version,
		int start, int end) {

		return findByG_FCI_T_Version(
			groupId, fragmentCollectionId, type, version, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_T_Version(
		long groupId, long fragmentCollectionId, int type, int version,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator) {

		return findByG_FCI_T_Version(
			groupId, fragmentCollectionId, type, version, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_S(
		long groupId, long fragmentCollectionId, int status) {

		return findByG_FCI_S(
			groupId, fragmentCollectionId, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_S(
		long groupId, long fragmentCollectionId, int status, int start,
		int end) {

		return findByG_FCI_S(
			groupId, fragmentCollectionId, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_S(
		long groupId, long fragmentCollectionId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator) {

		return findByG_FCI_S(
			groupId, fragmentCollectionId, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param version the version
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_S_Version(
		long groupId, long fragmentCollectionId, int status, int version) {

		return findByG_FCI_S_Version(
			groupId, fragmentCollectionId, status, version,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_S_Version(
		long groupId, long fragmentCollectionId, int status, int version,
		int start, int end) {

		return findByG_FCI_S_Version(
			groupId, fragmentCollectionId, status, version, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_S_Version(
		long groupId, long fragmentCollectionId, int status, int version,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator) {

		return findByG_FCI_S_Version(
			groupId, fragmentCollectionId, status, version, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status) {

		return findByG_FCI_LikeN_S(
			groupId, fragmentCollectionId, name, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status,
		int start, int end) {

		return findByG_FCI_LikeN_S(
			groupId, fragmentCollectionId, name, status, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator) {

		return findByG_FCI_LikeN_S(
			groupId, fragmentCollectionId, name, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param version the version
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion>
		findByG_FCI_LikeN_S_Version(
			long groupId, long fragmentCollectionId, String name, int status,
			int version) {

		return findByG_FCI_LikeN_S_Version(
			groupId, fragmentCollectionId, name, status, version,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion>
		findByG_FCI_LikeN_S_Version(
			long groupId, long fragmentCollectionId, String name, int status,
			int version, int start, int end) {

		return findByG_FCI_LikeN_S_Version(
			groupId, fragmentCollectionId, name, status, version, start, end,
			null, true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion>
		findByG_FCI_LikeN_S_Version(
			long groupId, long fragmentCollectionId, String name, int status,
			int version, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentEntryVersion> orderByComparator) {

		return findByG_FCI_LikeN_S_Version(
			groupId, fragmentCollectionId, name, status, version, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_T_S(
		long groupId, long fragmentCollectionId, int type, int status) {

		return findByG_FCI_T_S(
			groupId, fragmentCollectionId, type, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_T_S(
		long groupId, long fragmentCollectionId, int type, int status,
		int start, int end) {

		return findByG_FCI_T_S(
			groupId, fragmentCollectionId, type, status, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_T_S(
		long groupId, long fragmentCollectionId, int type, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator) {

		return findByG_FCI_T_S(
			groupId, fragmentCollectionId, type, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param version the version
	 * @return the matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_T_S_Version(
		long groupId, long fragmentCollectionId, int type, int status,
		int version) {

		return findByG_FCI_T_S_Version(
			groupId, fragmentCollectionId, type, status, version,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @return the range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_T_S_Version(
		long groupId, long fragmentCollectionId, int type, int status,
		int version, int start, int end) {

		return findByG_FCI_T_S_Version(
			groupId, fragmentCollectionId, type, status, version, start, end,
			null, true);
	}

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry versions
	 */
	public default java.util.List<FragmentEntryVersion> findByG_FCI_T_S_Version(
		long groupId, long fragmentCollectionId, int type, int status,
		int version, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryVersion>
			orderByComparator) {

		return findByG_FCI_T_S_Version(
			groupId, fragmentCollectionId, type, status, version, start, end,
			orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:221923026