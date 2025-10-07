/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence;

import com.liferay.change.tracking.exception.NoSuchCollectionException;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the ct collection service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see CTCollectionUtil
 * @generated
 */
@ProviderType
public interface CTCollectionPersistence extends BasePersistence<CTCollection> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CTCollectionUtil} to access the ct collection persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the ct collections where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching ct collections
	 */
	public java.util.List<CTCollection> findByUuid(String uuid);

	/**
	 * Returns a range of all the ct collections where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @return the range of matching ct collections
	 */
	public java.util.List<CTCollection> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the ct collections where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct collections
	 */
	public java.util.List<CTCollection> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ct collections where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct collections
	 */
	public java.util.List<CTCollection> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ct collection in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct collection
	 * @throws NoSuchCollectionException if a matching ct collection could not be found
	 */
	public CTCollection findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Returns the first ct collection in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct collection, or <code>null</code> if a matching ct collection could not be found
	 */
	public CTCollection fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns the last ct collection in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ct collection
	 * @throws NoSuchCollectionException if a matching ct collection could not be found
	 */
	public CTCollection findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Returns the last ct collection in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ct collection, or <code>null</code> if a matching ct collection could not be found
	 */
	public CTCollection fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns the ct collections before and after the current ct collection in the ordered set where uuid = &#63;.
	 *
	 * @param ctCollectionId the primary key of the current ct collection
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ct collection
	 * @throws NoSuchCollectionException if a ct collection with the primary key could not be found
	 */
	public CTCollection[] findByUuid_PrevAndNext(
			long ctCollectionId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Returns all the ct collections that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByUuid(String uuid);

	/**
	 * Returns a range of all the ct collections that the user has permission to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @return the range of matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the ct collections that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns the ct collections before and after the current ct collection in the ordered set of ct collections that the user has permission to view where uuid = &#63;.
	 *
	 * @param ctCollectionId the primary key of the current ct collection
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ct collection
	 * @throws NoSuchCollectionException if a ct collection with the primary key could not be found
	 */
	public CTCollection[] filterFindByUuid_PrevAndNext(
			long ctCollectionId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Removes all the ct collections where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of ct collections where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching ct collections
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the number of ct collections that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching ct collections that the user has permission to view
	 */
	public int filterCountByUuid(String uuid);

	/**
	 * Returns all the ct collections where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching ct collections
	 */
	public java.util.List<CTCollection> findByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the ct collections where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @return the range of matching ct collections
	 */
	public java.util.List<CTCollection> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the ct collections where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct collections
	 */
	public java.util.List<CTCollection> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ct collections where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct collections
	 */
	public java.util.List<CTCollection> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ct collection in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct collection
	 * @throws NoSuchCollectionException if a matching ct collection could not be found
	 */
	public CTCollection findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Returns the first ct collection in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct collection, or <code>null</code> if a matching ct collection could not be found
	 */
	public CTCollection fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns the last ct collection in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ct collection
	 * @throws NoSuchCollectionException if a matching ct collection could not be found
	 */
	public CTCollection findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Returns the last ct collection in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ct collection, or <code>null</code> if a matching ct collection could not be found
	 */
	public CTCollection fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns the ct collections before and after the current ct collection in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param ctCollectionId the primary key of the current ct collection
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ct collection
	 * @throws NoSuchCollectionException if a ct collection with the primary key could not be found
	 */
	public CTCollection[] findByUuid_C_PrevAndNext(
			long ctCollectionId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Returns all the ct collections that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the ct collections that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @return the range of matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the ct collections that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns the ct collections before and after the current ct collection in the ordered set of ct collections that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param ctCollectionId the primary key of the current ct collection
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ct collection
	 * @throws NoSuchCollectionException if a ct collection with the primary key could not be found
	 */
	public CTCollection[] filterFindByUuid_C_PrevAndNext(
			long ctCollectionId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Removes all the ct collections where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of ct collections where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching ct collections
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of ct collections that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching ct collections that the user has permission to view
	 */
	public int filterCountByUuid_C(String uuid, long companyId);

	/**
	 * Returns all the ct collections where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching ct collections
	 */
	public java.util.List<CTCollection> findByCompanyId(long companyId);

	/**
	 * Returns a range of all the ct collections where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @return the range of matching ct collections
	 */
	public java.util.List<CTCollection> findByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the ct collections where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct collections
	 */
	public java.util.List<CTCollection> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ct collections where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct collections
	 */
	public java.util.List<CTCollection> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ct collection in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct collection
	 * @throws NoSuchCollectionException if a matching ct collection could not be found
	 */
	public CTCollection findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Returns the first ct collection in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct collection, or <code>null</code> if a matching ct collection could not be found
	 */
	public CTCollection fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns the last ct collection in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ct collection
	 * @throws NoSuchCollectionException if a matching ct collection could not be found
	 */
	public CTCollection findByCompanyId_Last(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Returns the last ct collection in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ct collection, or <code>null</code> if a matching ct collection could not be found
	 */
	public CTCollection fetchByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns the ct collections before and after the current ct collection in the ordered set where companyId = &#63;.
	 *
	 * @param ctCollectionId the primary key of the current ct collection
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ct collection
	 * @throws NoSuchCollectionException if a ct collection with the primary key could not be found
	 */
	public CTCollection[] findByCompanyId_PrevAndNext(
			long ctCollectionId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Returns all the ct collections that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByCompanyId(long companyId);

	/**
	 * Returns a range of all the ct collections that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @return the range of matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the ct collections that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns the ct collections before and after the current ct collection in the ordered set of ct collections that the user has permission to view where companyId = &#63;.
	 *
	 * @param ctCollectionId the primary key of the current ct collection
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ct collection
	 * @throws NoSuchCollectionException if a ct collection with the primary key could not be found
	 */
	public CTCollection[] filterFindByCompanyId_PrevAndNext(
			long ctCollectionId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Removes all the ct collections where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of ct collections where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching ct collections
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns the number of ct collections that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching ct collections that the user has permission to view
	 */
	public int filterCountByCompanyId(long companyId);

	/**
	 * Returns all the ct collections where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching ct collections
	 */
	public java.util.List<CTCollection> findByC_U(long companyId, long userId);

	/**
	 * Returns a range of all the ct collections where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @return the range of matching ct collections
	 */
	public java.util.List<CTCollection> findByC_U(
		long companyId, long userId, int start, int end);

	/**
	 * Returns an ordered range of all the ct collections where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct collections
	 */
	public java.util.List<CTCollection> findByC_U(
		long companyId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ct collections where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct collections
	 */
	public java.util.List<CTCollection> findByC_U(
		long companyId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ct collection in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct collection
	 * @throws NoSuchCollectionException if a matching ct collection could not be found
	 */
	public CTCollection findByC_U_First(
			long companyId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Returns the first ct collection in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct collection, or <code>null</code> if a matching ct collection could not be found
	 */
	public CTCollection fetchByC_U_First(
		long companyId, long userId,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns the last ct collection in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ct collection
	 * @throws NoSuchCollectionException if a matching ct collection could not be found
	 */
	public CTCollection findByC_U_Last(
			long companyId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Returns the last ct collection in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ct collection, or <code>null</code> if a matching ct collection could not be found
	 */
	public CTCollection fetchByC_U_Last(
		long companyId, long userId,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns the ct collections before and after the current ct collection in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param ctCollectionId the primary key of the current ct collection
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ct collection
	 * @throws NoSuchCollectionException if a ct collection with the primary key could not be found
	 */
	public CTCollection[] findByC_U_PrevAndNext(
			long ctCollectionId, long companyId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Returns all the ct collections that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByC_U(
		long companyId, long userId);

	/**
	 * Returns a range of all the ct collections that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @return the range of matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByC_U(
		long companyId, long userId, int start, int end);

	/**
	 * Returns an ordered range of all the ct collections that the user has permissions to view where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByC_U(
		long companyId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns the ct collections before and after the current ct collection in the ordered set of ct collections that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * @param ctCollectionId the primary key of the current ct collection
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ct collection
	 * @throws NoSuchCollectionException if a ct collection with the primary key could not be found
	 */
	public CTCollection[] filterFindByC_U_PrevAndNext(
			long ctCollectionId, long companyId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Removes all the ct collections where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	public void removeByC_U(long companyId, long userId);

	/**
	 * Returns the number of ct collections where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching ct collections
	 */
	public int countByC_U(long companyId, long userId);

	/**
	 * Returns the number of ct collections that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching ct collections that the user has permission to view
	 */
	public int filterCountByC_U(long companyId, long userId);

	/**
	 * Returns all the ct collections where companyId = &#63; and schemaVersionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param schemaVersionId the schema version ID
	 * @return the matching ct collections
	 */
	public java.util.List<CTCollection> findByC_SVI(
		long companyId, long schemaVersionId);

	/**
	 * Returns a range of all the ct collections where companyId = &#63; and schemaVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param schemaVersionId the schema version ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @return the range of matching ct collections
	 */
	public java.util.List<CTCollection> findByC_SVI(
		long companyId, long schemaVersionId, int start, int end);

	/**
	 * Returns an ordered range of all the ct collections where companyId = &#63; and schemaVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param schemaVersionId the schema version ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct collections
	 */
	public java.util.List<CTCollection> findByC_SVI(
		long companyId, long schemaVersionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ct collections where companyId = &#63; and schemaVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param schemaVersionId the schema version ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct collections
	 */
	public java.util.List<CTCollection> findByC_SVI(
		long companyId, long schemaVersionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ct collection in the ordered set where companyId = &#63; and schemaVersionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param schemaVersionId the schema version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct collection
	 * @throws NoSuchCollectionException if a matching ct collection could not be found
	 */
	public CTCollection findByC_SVI_First(
			long companyId, long schemaVersionId,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Returns the first ct collection in the ordered set where companyId = &#63; and schemaVersionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param schemaVersionId the schema version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct collection, or <code>null</code> if a matching ct collection could not be found
	 */
	public CTCollection fetchByC_SVI_First(
		long companyId, long schemaVersionId,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns the last ct collection in the ordered set where companyId = &#63; and schemaVersionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param schemaVersionId the schema version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ct collection
	 * @throws NoSuchCollectionException if a matching ct collection could not be found
	 */
	public CTCollection findByC_SVI_Last(
			long companyId, long schemaVersionId,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Returns the last ct collection in the ordered set where companyId = &#63; and schemaVersionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param schemaVersionId the schema version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ct collection, or <code>null</code> if a matching ct collection could not be found
	 */
	public CTCollection fetchByC_SVI_Last(
		long companyId, long schemaVersionId,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns the ct collections before and after the current ct collection in the ordered set where companyId = &#63; and schemaVersionId = &#63;.
	 *
	 * @param ctCollectionId the primary key of the current ct collection
	 * @param companyId the company ID
	 * @param schemaVersionId the schema version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ct collection
	 * @throws NoSuchCollectionException if a ct collection with the primary key could not be found
	 */
	public CTCollection[] findByC_SVI_PrevAndNext(
			long ctCollectionId, long companyId, long schemaVersionId,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Returns all the ct collections that the user has permission to view where companyId = &#63; and schemaVersionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param schemaVersionId the schema version ID
	 * @return the matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByC_SVI(
		long companyId, long schemaVersionId);

	/**
	 * Returns a range of all the ct collections that the user has permission to view where companyId = &#63; and schemaVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param schemaVersionId the schema version ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @return the range of matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByC_SVI(
		long companyId, long schemaVersionId, int start, int end);

	/**
	 * Returns an ordered range of all the ct collections that the user has permissions to view where companyId = &#63; and schemaVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param schemaVersionId the schema version ID
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByC_SVI(
		long companyId, long schemaVersionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns the ct collections before and after the current ct collection in the ordered set of ct collections that the user has permission to view where companyId = &#63; and schemaVersionId = &#63;.
	 *
	 * @param ctCollectionId the primary key of the current ct collection
	 * @param companyId the company ID
	 * @param schemaVersionId the schema version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ct collection
	 * @throws NoSuchCollectionException if a ct collection with the primary key could not be found
	 */
	public CTCollection[] filterFindByC_SVI_PrevAndNext(
			long ctCollectionId, long companyId, long schemaVersionId,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Removes all the ct collections where companyId = &#63; and schemaVersionId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param schemaVersionId the schema version ID
	 */
	public void removeByC_SVI(long companyId, long schemaVersionId);

	/**
	 * Returns the number of ct collections where companyId = &#63; and schemaVersionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param schemaVersionId the schema version ID
	 * @return the number of matching ct collections
	 */
	public int countByC_SVI(long companyId, long schemaVersionId);

	/**
	 * Returns the number of ct collections that the user has permission to view where companyId = &#63; and schemaVersionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param schemaVersionId the schema version ID
	 * @return the number of matching ct collections that the user has permission to view
	 */
	public int filterCountByC_SVI(long companyId, long schemaVersionId);

	/**
	 * Returns all the ct collections where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching ct collections
	 */
	public java.util.List<CTCollection> findByC_S(long companyId, int status);

	/**
	 * Returns a range of all the ct collections where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @return the range of matching ct collections
	 */
	public java.util.List<CTCollection> findByC_S(
		long companyId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the ct collections where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct collections
	 */
	public java.util.List<CTCollection> findByC_S(
		long companyId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ct collections where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct collections
	 */
	public java.util.List<CTCollection> findByC_S(
		long companyId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ct collection in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct collection
	 * @throws NoSuchCollectionException if a matching ct collection could not be found
	 */
	public CTCollection findByC_S_First(
			long companyId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Returns the first ct collection in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct collection, or <code>null</code> if a matching ct collection could not be found
	 */
	public CTCollection fetchByC_S_First(
		long companyId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns the last ct collection in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ct collection
	 * @throws NoSuchCollectionException if a matching ct collection could not be found
	 */
	public CTCollection findByC_S_Last(
			long companyId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Returns the last ct collection in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ct collection, or <code>null</code> if a matching ct collection could not be found
	 */
	public CTCollection fetchByC_S_Last(
		long companyId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns the ct collections before and after the current ct collection in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param ctCollectionId the primary key of the current ct collection
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ct collection
	 * @throws NoSuchCollectionException if a ct collection with the primary key could not be found
	 */
	public CTCollection[] findByC_S_PrevAndNext(
			long ctCollectionId, long companyId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Returns all the ct collections that the user has permission to view where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByC_S(
		long companyId, int status);

	/**
	 * Returns a range of all the ct collections that the user has permission to view where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @return the range of matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByC_S(
		long companyId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the ct collections that the user has permissions to view where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByC_S(
		long companyId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns the ct collections before and after the current ct collection in the ordered set of ct collections that the user has permission to view where companyId = &#63; and status = &#63;.
	 *
	 * @param ctCollectionId the primary key of the current ct collection
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ct collection
	 * @throws NoSuchCollectionException if a ct collection with the primary key could not be found
	 */
	public CTCollection[] filterFindByC_S_PrevAndNext(
			long ctCollectionId, long companyId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
				orderByComparator)
		throws NoSuchCollectionException;

	/**
	 * Returns all the ct collections that the user has permission to view where companyId = &#63; and status = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param statuses the statuses
	 * @return the matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByC_S(
		long companyId, int[] statuses);

	/**
	 * Returns a range of all the ct collections that the user has permission to view where companyId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @return the range of matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByC_S(
		long companyId, int[] statuses, int start, int end);

	/**
	 * Returns an ordered range of all the ct collections that the user has permission to view where companyId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct collections that the user has permission to view
	 */
	public java.util.List<CTCollection> filterFindByC_S(
		long companyId, int[] statuses, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns all the ct collections where companyId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param statuses the statuses
	 * @return the matching ct collections
	 */
	public java.util.List<CTCollection> findByC_S(
		long companyId, int[] statuses);

	/**
	 * Returns a range of all the ct collections where companyId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @return the range of matching ct collections
	 */
	public java.util.List<CTCollection> findByC_S(
		long companyId, int[] statuses, int start, int end);

	/**
	 * Returns an ordered range of all the ct collections where companyId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct collections
	 */
	public java.util.List<CTCollection> findByC_S(
		long companyId, int[] statuses, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ct collections where companyId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct collections
	 */
	public java.util.List<CTCollection> findByC_S(
		long companyId, int[] statuses, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the ct collections where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	public void removeByC_S(long companyId, int status);

	/**
	 * Returns the number of ct collections where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching ct collections
	 */
	public int countByC_S(long companyId, int status);

	/**
	 * Returns the number of ct collections where companyId = &#63; and status = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param statuses the statuses
	 * @return the number of matching ct collections
	 */
	public int countByC_S(long companyId, int[] statuses);

	/**
	 * Returns the number of ct collections that the user has permission to view where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching ct collections that the user has permission to view
	 */
	public int filterCountByC_S(long companyId, int status);

	/**
	 * Returns the number of ct collections that the user has permission to view where companyId = &#63; and status = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param statuses the statuses
	 * @return the number of matching ct collections that the user has permission to view
	 */
	public int filterCountByC_S(long companyId, int[] statuses);

	/**
	 * Returns the ct collection where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCollectionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching ct collection
	 * @throws NoSuchCollectionException if a matching ct collection could not be found
	 */
	public CTCollection findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCollectionException;

	/**
	 * Returns the ct collection where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching ct collection, or <code>null</code> if a matching ct collection could not be found
	 */
	public CTCollection fetchByERC_C(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the ct collection where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ct collection, or <code>null</code> if a matching ct collection could not be found
	 */
	public CTCollection fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the ct collection where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the ct collection that was removed
	 */
	public CTCollection removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCollectionException;

	/**
	 * Returns the number of ct collections where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching ct collections
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Caches the ct collection in the entity cache if it is enabled.
	 *
	 * @param ctCollection the ct collection
	 */
	public void cacheResult(CTCollection ctCollection);

	/**
	 * Caches the ct collections in the entity cache if it is enabled.
	 *
	 * @param ctCollections the ct collections
	 */
	public void cacheResult(java.util.List<CTCollection> ctCollections);

	/**
	 * Creates a new ct collection with the primary key. Does not add the ct collection to the database.
	 *
	 * @param ctCollectionId the primary key for the new ct collection
	 * @return the new ct collection
	 */
	public CTCollection create(long ctCollectionId);

	/**
	 * Removes the ct collection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctCollectionId the primary key of the ct collection
	 * @return the ct collection that was removed
	 * @throws NoSuchCollectionException if a ct collection with the primary key could not be found
	 */
	public CTCollection remove(long ctCollectionId)
		throws NoSuchCollectionException;

	public CTCollection updateImpl(CTCollection ctCollection);

	/**
	 * Returns the ct collection with the primary key or throws a <code>NoSuchCollectionException</code> if it could not be found.
	 *
	 * @param ctCollectionId the primary key of the ct collection
	 * @return the ct collection
	 * @throws NoSuchCollectionException if a ct collection with the primary key could not be found
	 */
	public CTCollection findByPrimaryKey(long ctCollectionId)
		throws NoSuchCollectionException;

	/**
	 * Returns the ct collection with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctCollectionId the primary key of the ct collection
	 * @return the ct collection, or <code>null</code> if a ct collection with the primary key could not be found
	 */
	public CTCollection fetchByPrimaryKey(long ctCollectionId);

	/**
	 * Returns all the ct collections.
	 *
	 * @return the ct collections
	 */
	public java.util.List<CTCollection> findAll();

	/**
	 * Returns a range of all the ct collections.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @return the range of ct collections
	 */
	public java.util.List<CTCollection> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the ct collections.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of ct collections
	 */
	public java.util.List<CTCollection> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ct collections.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of ct collections
	 */
	public java.util.List<CTCollection> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTCollection>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the ct collections from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of ct collections.
	 *
	 * @return the number of ct collections
	 */
	public int countAll();

}