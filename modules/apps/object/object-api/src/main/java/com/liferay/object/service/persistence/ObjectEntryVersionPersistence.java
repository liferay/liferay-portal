/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence;

import com.liferay.object.exception.NoSuchObjectEntryVersionException;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import java.util.Date;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the object entry version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see ObjectEntryVersionUtil
 * @generated
 */
@ProviderType
public interface ObjectEntryVersionPersistence
	extends BasePersistence<ObjectEntryVersion> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ObjectEntryVersionUtil} to access the object entry version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the object entry versions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByUuid(String uuid);

	/**
	 * Returns a range of all the object entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the object entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public ObjectEntryVersion findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
				orderByComparator)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Returns the first object entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public ObjectEntryVersion fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator);

	/**
	 * Returns the last object entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public ObjectEntryVersion findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
				orderByComparator)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Returns the last object entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public ObjectEntryVersion fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator);

	/**
	 * Returns the object entry versions before and after the current object entry version in the ordered set where uuid = &#63;.
	 *
	 * @param objectEntryVersionId the primary key of the current object entry version
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	public ObjectEntryVersion[] findByUuid_PrevAndNext(
			long objectEntryVersionId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
				orderByComparator)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Removes all the object entry versions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of object entry versions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object entry versions
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns all the object entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the object entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the object entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public ObjectEntryVersion findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
				orderByComparator)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Returns the first object entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public ObjectEntryVersion fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator);

	/**
	 * Returns the last object entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public ObjectEntryVersion findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
				orderByComparator)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Returns the last object entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public ObjectEntryVersion fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator);

	/**
	 * Returns the object entry versions before and after the current object entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectEntryVersionId the primary key of the current object entry version
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	public ObjectEntryVersion[] findByUuid_C_PrevAndNext(
			long objectEntryVersionId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
				orderByComparator)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Removes all the object entry versions where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of object entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object entry versions
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns all the object entry versions where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByObjectDefinitionId(
		long objectDefinitionId);

	/**
	 * Returns a range of all the object entry versions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end);

	/**
	 * Returns an ordered range of all the object entry versions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entry versions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object entry version in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public ObjectEntryVersion findByObjectDefinitionId_First(
			long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
				orderByComparator)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Returns the first object entry version in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public ObjectEntryVersion fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator);

	/**
	 * Returns the last object entry version in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public ObjectEntryVersion findByObjectDefinitionId_Last(
			long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
				orderByComparator)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Returns the last object entry version in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public ObjectEntryVersion fetchByObjectDefinitionId_Last(
		long objectDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator);

	/**
	 * Returns the object entry versions before and after the current object entry version in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectEntryVersionId the primary key of the current object entry version
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	public ObjectEntryVersion[] findByObjectDefinitionId_PrevAndNext(
			long objectEntryVersionId, long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
				orderByComparator)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Removes all the object entry versions where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	public void removeByObjectDefinitionId(long objectDefinitionId);

	/**
	 * Returns the number of object entry versions where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object entry versions
	 */
	public int countByObjectDefinitionId(long objectDefinitionId);

	/**
	 * Returns all the object entry versions where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @return the matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByObjectEntryId(
		long objectEntryId);

	/**
	 * Returns a range of all the object entry versions where objectEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByObjectEntryId(
		long objectEntryId, int start, int end);

	/**
	 * Returns an ordered range of all the object entry versions where objectEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByObjectEntryId(
		long objectEntryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entry versions where objectEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByObjectEntryId(
		long objectEntryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object entry version in the ordered set where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public ObjectEntryVersion findByObjectEntryId_First(
			long objectEntryId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
				orderByComparator)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Returns the first object entry version in the ordered set where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public ObjectEntryVersion fetchByObjectEntryId_First(
		long objectEntryId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator);

	/**
	 * Returns the last object entry version in the ordered set where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public ObjectEntryVersion findByObjectEntryId_Last(
			long objectEntryId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
				orderByComparator)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Returns the last object entry version in the ordered set where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public ObjectEntryVersion fetchByObjectEntryId_Last(
		long objectEntryId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator);

	/**
	 * Returns the object entry versions before and after the current object entry version in the ordered set where objectEntryId = &#63;.
	 *
	 * @param objectEntryVersionId the primary key of the current object entry version
	 * @param objectEntryId the object entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	public ObjectEntryVersion[] findByObjectEntryId_PrevAndNext(
			long objectEntryVersionId, long objectEntryId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
				orderByComparator)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Removes all the object entry versions where objectEntryId = &#63; from the database.
	 *
	 * @param objectEntryId the object entry ID
	 */
	public void removeByObjectEntryId(long objectEntryId);

	/**
	 * Returns the number of object entry versions where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @return the number of matching object entry versions
	 */
	public int countByObjectEntryId(long objectEntryId);

	/**
	 * Returns all the object entry versions where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @return the matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByC_CD(
		long companyId, Date createDate);

	/**
	 * Returns a range of all the object entry versions where companyId = &#63; and createDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByC_CD(
		long companyId, Date createDate, int start, int end);

	/**
	 * Returns an ordered range of all the object entry versions where companyId = &#63; and createDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByC_CD(
		long companyId, Date createDate, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entry versions where companyId = &#63; and createDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByC_CD(
		long companyId, Date createDate, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object entry version in the ordered set where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public ObjectEntryVersion findByC_CD_First(
			long companyId, Date createDate,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
				orderByComparator)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Returns the first object entry version in the ordered set where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public ObjectEntryVersion fetchByC_CD_First(
		long companyId, Date createDate,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator);

	/**
	 * Returns the last object entry version in the ordered set where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public ObjectEntryVersion findByC_CD_Last(
			long companyId, Date createDate,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
				orderByComparator)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Returns the last object entry version in the ordered set where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public ObjectEntryVersion fetchByC_CD_Last(
		long companyId, Date createDate,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator);

	/**
	 * Returns the object entry versions before and after the current object entry version in the ordered set where companyId = &#63; and createDate = &#63;.
	 *
	 * @param objectEntryVersionId the primary key of the current object entry version
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	public ObjectEntryVersion[] findByC_CD_PrevAndNext(
			long objectEntryVersionId, long companyId, Date createDate,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
				orderByComparator)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Removes all the object entry versions where companyId = &#63; and createDate = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 */
	public void removeByC_CD(long companyId, Date createDate);

	/**
	 * Returns the number of object entry versions where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @return the number of matching object entry versions
	 */
	public int countByC_CD(long companyId, Date createDate);

	/**
	 * Returns the object entry version where objectEntryId = &#63; and version = &#63; or throws a <code>NoSuchObjectEntryVersionException</code> if it could not be found.
	 *
	 * @param objectEntryId the object entry ID
	 * @param version the version
	 * @return the matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public ObjectEntryVersion findByOEI_V(long objectEntryId, int version)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Returns the object entry version where objectEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectEntryId the object entry ID
	 * @param version the version
	 * @return the matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public ObjectEntryVersion fetchByOEI_V(long objectEntryId, int version);

	/**
	 * Returns the object entry version where objectEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectEntryId the object entry ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public ObjectEntryVersion fetchByOEI_V(
		long objectEntryId, int version, boolean useFinderCache);

	/**
	 * Removes the object entry version where objectEntryId = &#63; and version = &#63; from the database.
	 *
	 * @param objectEntryId the object entry ID
	 * @param version the version
	 * @return the object entry version that was removed
	 */
	public ObjectEntryVersion removeByOEI_V(long objectEntryId, int version)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Returns the number of object entry versions where objectEntryId = &#63; and version = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param version the version
	 * @return the number of matching object entry versions
	 */
	public int countByOEI_V(long objectEntryId, int version);

	/**
	 * Returns all the object entry versions where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @return the matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByOEI_S(
		long objectEntryId, int status);

	/**
	 * Returns a range of all the object entry versions where objectEntryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByOEI_S(
		long objectEntryId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the object entry versions where objectEntryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByOEI_S(
		long objectEntryId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entry versions where objectEntryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findByOEI_S(
		long objectEntryId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object entry version in the ordered set where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public ObjectEntryVersion findByOEI_S_First(
			long objectEntryId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
				orderByComparator)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Returns the first object entry version in the ordered set where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public ObjectEntryVersion fetchByOEI_S_First(
		long objectEntryId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator);

	/**
	 * Returns the last object entry version in the ordered set where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public ObjectEntryVersion findByOEI_S_Last(
			long objectEntryId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
				orderByComparator)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Returns the last object entry version in the ordered set where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public ObjectEntryVersion fetchByOEI_S_Last(
		long objectEntryId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator);

	/**
	 * Returns the object entry versions before and after the current object entry version in the ordered set where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryVersionId the primary key of the current object entry version
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	public ObjectEntryVersion[] findByOEI_S_PrevAndNext(
			long objectEntryVersionId, long objectEntryId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
				orderByComparator)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Removes all the object entry versions where objectEntryId = &#63; and status = &#63; from the database.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 */
	public void removeByOEI_S(long objectEntryId, int status);

	/**
	 * Returns the number of object entry versions where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @return the number of matching object entry versions
	 */
	public int countByOEI_S(long objectEntryId, int status);

	/**
	 * Caches the object entry version in the entity cache if it is enabled.
	 *
	 * @param objectEntryVersion the object entry version
	 */
	public void cacheResult(ObjectEntryVersion objectEntryVersion);

	/**
	 * Caches the object entry versions in the entity cache if it is enabled.
	 *
	 * @param objectEntryVersions the object entry versions
	 */
	public void cacheResult(
		java.util.List<ObjectEntryVersion> objectEntryVersions);

	/**
	 * Creates a new object entry version with the primary key. Does not add the object entry version to the database.
	 *
	 * @param objectEntryVersionId the primary key for the new object entry version
	 * @return the new object entry version
	 */
	public ObjectEntryVersion create(long objectEntryVersionId);

	/**
	 * Removes the object entry version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectEntryVersionId the primary key of the object entry version
	 * @return the object entry version that was removed
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	public ObjectEntryVersion remove(long objectEntryVersionId)
		throws NoSuchObjectEntryVersionException;

	public ObjectEntryVersion updateImpl(ObjectEntryVersion objectEntryVersion);

	/**
	 * Returns the object entry version with the primary key or throws a <code>NoSuchObjectEntryVersionException</code> if it could not be found.
	 *
	 * @param objectEntryVersionId the primary key of the object entry version
	 * @return the object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	public ObjectEntryVersion findByPrimaryKey(long objectEntryVersionId)
		throws NoSuchObjectEntryVersionException;

	/**
	 * Returns the object entry version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectEntryVersionId the primary key of the object entry version
	 * @return the object entry version, or <code>null</code> if a object entry version with the primary key could not be found
	 */
	public ObjectEntryVersion fetchByPrimaryKey(long objectEntryVersionId);

	/**
	 * Returns all the object entry versions.
	 *
	 * @return the object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findAll();

	/**
	 * Returns a range of all the object entry versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the object entry versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entry versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object entry versions
	 */
	public java.util.List<ObjectEntryVersion> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntryVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the object entry versions from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of object entry versions.
	 *
	 * @return the number of object entry versions
	 */
	public int countAll();

}