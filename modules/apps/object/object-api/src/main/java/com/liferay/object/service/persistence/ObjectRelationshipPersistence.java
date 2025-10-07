/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence;

import com.liferay.object.exception.NoSuchObjectRelationshipException;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the object relationship service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see ObjectRelationshipUtil
 * @generated
 */
@ProviderType
public interface ObjectRelationshipPersistence
	extends BasePersistence<ObjectRelationship> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ObjectRelationshipUtil} to access the object relationship persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the object relationships where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByUuid(String uuid);

	/**
	 * Returns a range of all the object relationships where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the object relationships where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object relationships where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object relationship in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the first object relationship in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the last object relationship in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the last object relationship in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where uuid = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship[] findByUuid_PrevAndNext(
			long objectRelationshipId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Removes all the object relationships where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of object relationships where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object relationships
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns all the object relationships where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the object relationships where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the object relationships where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object relationships where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object relationship in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the first object relationship in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the last object relationship in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the last object relationship in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship[] findByUuid_C_PrevAndNext(
			long objectRelationshipId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Removes all the object relationships where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of object relationships where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object relationships
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns all the object relationships where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByCompanyId(long companyId);

	/**
	 * Returns a range of all the object relationships where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the object relationships where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object relationships where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object relationship in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the first object relationship in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the last object relationship in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByCompanyId_Last(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the last object relationship in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where companyId = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship[] findByCompanyId_PrevAndNext(
			long objectRelationshipId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Removes all the object relationships where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of object relationships where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching object relationships
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @return the matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByObjectDefinitionId1(
		long objectDefinitionId1);

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByObjectDefinitionId1(
		long objectDefinitionId1, int start, int end);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByObjectDefinitionId1(
		long objectDefinitionId1, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByObjectDefinitionId1(
		long objectDefinitionId1, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByObjectDefinitionId1_First(
			long objectDefinitionId1,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByObjectDefinitionId1_First(
		long objectDefinitionId1,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByObjectDefinitionId1_Last(
			long objectDefinitionId1,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByObjectDefinitionId1_Last(
		long objectDefinitionId1,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship[] findByObjectDefinitionId1_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 */
	public void removeByObjectDefinitionId1(long objectDefinitionId1);

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @return the number of matching object relationships
	 */
	public int countByObjectDefinitionId1(long objectDefinitionId1);

	/**
	 * Returns all the object relationships where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @return the matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByObjectDefinitionId2(
		long objectDefinitionId2);

	/**
	 * Returns a range of all the object relationships where objectDefinitionId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByObjectDefinitionId2(
		long objectDefinitionId2, int start, int end);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByObjectDefinitionId2(
		long objectDefinitionId2, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByObjectDefinitionId2(
		long objectDefinitionId2, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByObjectDefinitionId2_First(
			long objectDefinitionId2,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByObjectDefinitionId2_First(
		long objectDefinitionId2,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByObjectDefinitionId2_Last(
			long objectDefinitionId2,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByObjectDefinitionId2_Last(
		long objectDefinitionId2,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId2 = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId2 the object definition id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship[] findByObjectDefinitionId2_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId2,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Removes all the object relationships where objectDefinitionId2 = &#63; from the database.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 */
	public void removeByObjectDefinitionId2(long objectDefinitionId2);

	/**
	 * Returns the number of object relationships where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @return the number of matching object relationships
	 */
	public int countByObjectDefinitionId2(long objectDefinitionId2);

	/**
	 * Returns the object relationship where objectFieldId2 = &#63; or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param objectFieldId2 the object field id2
	 * @return the matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByObjectFieldId2(long objectFieldId2)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the object relationship where objectFieldId2 = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectFieldId2 the object field id2
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByObjectFieldId2(long objectFieldId2);

	/**
	 * Returns the object relationship where objectFieldId2 = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectFieldId2 the object field id2
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByObjectFieldId2(
		long objectFieldId2, boolean useFinderCache);

	/**
	 * Removes the object relationship where objectFieldId2 = &#63; from the database.
	 *
	 * @param objectFieldId2 the object field id2
	 * @return the object relationship that was removed
	 */
	public ObjectRelationship removeByObjectFieldId2(long objectFieldId2)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the number of object relationships where objectFieldId2 = &#63;.
	 *
	 * @param objectFieldId2 the object field id2
	 * @return the number of matching object relationships
	 */
	public int countByObjectFieldId2(long objectFieldId2);

	/**
	 * Returns all the object relationships where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @return the matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByParameterObjectFieldId(
		long parameterObjectFieldId);

	/**
	 * Returns a range of all the object relationships where parameterObjectFieldId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByParameterObjectFieldId(
		long parameterObjectFieldId, int start, int end);

	/**
	 * Returns an ordered range of all the object relationships where parameterObjectFieldId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByParameterObjectFieldId(
		long parameterObjectFieldId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object relationships where parameterObjectFieldId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByParameterObjectFieldId(
		long parameterObjectFieldId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object relationship in the ordered set where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByParameterObjectFieldId_First(
			long parameterObjectFieldId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the first object relationship in the ordered set where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByParameterObjectFieldId_First(
		long parameterObjectFieldId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the last object relationship in the ordered set where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByParameterObjectFieldId_Last(
			long parameterObjectFieldId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the last object relationship in the ordered set where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByParameterObjectFieldId_Last(
		long parameterObjectFieldId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where parameterObjectFieldId = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship[] findByParameterObjectFieldId_PrevAndNext(
			long objectRelationshipId, long parameterObjectFieldId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Removes all the object relationships where parameterObjectFieldId = &#63; from the database.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 */
	public void removeByParameterObjectFieldId(long parameterObjectFieldId);

	/**
	 * Returns the number of object relationships where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @return the number of matching object relationships
	 */
	public int countByParameterObjectFieldId(long parameterObjectFieldId);

	/**
	 * Returns all the object relationships where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByC_U(
		long companyId, long userId);

	/**
	 * Returns a range of all the object relationships where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByC_U(
		long companyId, long userId, int start, int end);

	/**
	 * Returns an ordered range of all the object relationships where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByC_U(
		long companyId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object relationships where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByC_U(
		long companyId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object relationship in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByC_U_First(
			long companyId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the first object relationship in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByC_U_First(
		long companyId, long userId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the last object relationship in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByC_U_Last(
			long companyId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the last object relationship in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByC_U_Last(
		long companyId, long userId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship[] findByC_U_PrevAndNext(
			long objectRelationshipId, long companyId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Removes all the object relationships where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	public void removeByC_U(long companyId, long userId);

	/**
	 * Returns the number of object relationships where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching object relationships
	 */
	public int countByC_U(long companyId, long userId);

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @return the matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_E(
		long objectDefinitionId1, boolean edge);

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_E(
		long objectDefinitionId1, boolean edge, int start, int end);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_E(
		long objectDefinitionId1, boolean edge, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_E(
		long objectDefinitionId1, boolean edge, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI1_E_First(
			long objectDefinitionId1, boolean edge,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI1_E_First(
		long objectDefinitionId1, boolean edge,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI1_E_Last(
			long objectDefinitionId1, boolean edge,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI1_E_Last(
		long objectDefinitionId1, boolean edge,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship[] findByODI1_E_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1, boolean edge,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and edge = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 */
	public void removeByODI1_E(long objectDefinitionId1, boolean edge);

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @return the number of matching object relationships
	 */
	public int countByODI1_E(long objectDefinitionId1, boolean edge);

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @return the matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_N(
		long objectDefinitionId1, String name);

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_N(
		long objectDefinitionId1, String name, int start, int end);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_N(
		long objectDefinitionId1, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_N(
		long objectDefinitionId1, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI1_N_First(
			long objectDefinitionId1, String name,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI1_N_First(
		long objectDefinitionId1, String name,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI1_N_Last(
			long objectDefinitionId1, String name,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI1_N_Last(
		long objectDefinitionId1, String name,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship[] findByODI1_N_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1, String name,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and name = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 */
	public void removeByODI1_N(long objectDefinitionId1, String name);

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @return the number of matching object relationships
	 */
	public int countByODI1_N(long objectDefinitionId1, String name);

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @return the matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_R(
		long objectDefinitionId1, boolean reverse);

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_R(
		long objectDefinitionId1, boolean reverse, int start, int end);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_R(
		long objectDefinitionId1, boolean reverse, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_R(
		long objectDefinitionId1, boolean reverse, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI1_R_First(
			long objectDefinitionId1, boolean reverse,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI1_R_First(
		long objectDefinitionId1, boolean reverse,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI1_R_Last(
			long objectDefinitionId1, boolean reverse,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI1_R_Last(
		long objectDefinitionId1, boolean reverse,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship[] findByODI1_R_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1,
			boolean reverse,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 */
	public void removeByODI1_R(long objectDefinitionId1, boolean reverse);

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @return the number of matching object relationships
	 */
	public int countByODI1_R(long objectDefinitionId1, boolean reverse);

	/**
	 * Returns all the object relationships where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @return the matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI2_E(
		long objectDefinitionId2, boolean edge);

	/**
	 * Returns a range of all the object relationships where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI2_E(
		long objectDefinitionId2, boolean edge, int start, int end);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI2_E(
		long objectDefinitionId2, boolean edge, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI2_E(
		long objectDefinitionId2, boolean edge, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI2_E_First(
			long objectDefinitionId2, boolean edge,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI2_E_First(
		long objectDefinitionId2, boolean edge,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI2_E_Last(
			long objectDefinitionId2, boolean edge,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI2_E_Last(
		long objectDefinitionId2, boolean edge,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship[] findByODI2_E_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId2, boolean edge,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Removes all the object relationships where objectDefinitionId2 = &#63; and edge = &#63; from the database.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 */
	public void removeByODI2_E(long objectDefinitionId2, boolean edge);

	/**
	 * Returns the number of object relationships where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @return the number of matching object relationships
	 */
	public int countByODI2_E(long objectDefinitionId2, boolean edge);

	/**
	 * Returns all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @return the matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI2_R(
		long objectDefinitionId2, boolean reverse);

	/**
	 * Returns a range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI2_R(
		long objectDefinitionId2, boolean reverse, int start, int end);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI2_R(
		long objectDefinitionId2, boolean reverse, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI2_R(
		long objectDefinitionId2, boolean reverse, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI2_R_First(
			long objectDefinitionId2, boolean reverse,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI2_R_First(
		long objectDefinitionId2, boolean reverse,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI2_R_Last(
			long objectDefinitionId2, boolean reverse,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI2_R_Last(
		long objectDefinitionId2, boolean reverse,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship[] findByODI2_R_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId2,
			boolean reverse,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Removes all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; from the database.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 */
	public void removeByODI2_R(long objectDefinitionId2, boolean reverse);

	/**
	 * Returns the number of object relationships where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @return the number of matching object relationships
	 */
	public int countByODI2_R(long objectDefinitionId2, boolean reverse);

	/**
	 * Returns the object relationship where dbTableName = &#63; and reverse = &#63; or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param dbTableName the db table name
	 * @param reverse the reverse
	 * @return the matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByDTN_R(String dbTableName, boolean reverse)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the object relationship where dbTableName = &#63; and reverse = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param dbTableName the db table name
	 * @param reverse the reverse
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByDTN_R(String dbTableName, boolean reverse);

	/**
	 * Returns the object relationship where dbTableName = &#63; and reverse = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param dbTableName the db table name
	 * @param reverse the reverse
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByDTN_R(
		String dbTableName, boolean reverse, boolean useFinderCache);

	/**
	 * Removes the object relationship where dbTableName = &#63; and reverse = &#63; from the database.
	 *
	 * @param dbTableName the db table name
	 * @param reverse the reverse
	 * @return the object relationship that was removed
	 */
	public ObjectRelationship removeByDTN_R(String dbTableName, boolean reverse)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the number of object relationships where dbTableName = &#63; and reverse = &#63;.
	 *
	 * @param dbTableName the db table name
	 * @param reverse the reverse
	 * @return the number of matching object relationships
	 */
	public int countByDTN_R(String dbTableName, boolean reverse);

	/**
	 * Returns the object relationship where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId1 = &#63; or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId1 the object definition id1
	 * @return the matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByERC_C_ODI1(
			String externalReferenceCode, long companyId,
			long objectDefinitionId1)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the object relationship where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId1 = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId1 the object definition id1
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByERC_C_ODI1(
		String externalReferenceCode, long companyId, long objectDefinitionId1);

	/**
	 * Returns the object relationship where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId1 = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId1 the object definition id1
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByERC_C_ODI1(
		String externalReferenceCode, long companyId, long objectDefinitionId1,
		boolean useFinderCache);

	/**
	 * Removes the object relationship where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId1 = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId1 the object definition id1
	 * @return the object relationship that was removed
	 */
	public ObjectRelationship removeByERC_C_ODI1(
			String externalReferenceCode, long companyId,
			long objectDefinitionId1)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the number of object relationships where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId1 = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId1 the object definition id1
	 * @return the number of matching object relationships
	 */
	public int countByERC_C_ODI1(
		String externalReferenceCode, long companyId, long objectDefinitionId1);

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @return the matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type);

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type,
		int start, int end);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI1_ODI2_T_First(
			long objectDefinitionId1, long objectDefinitionId2, String type,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI1_ODI2_T_First(
		long objectDefinitionId1, long objectDefinitionId2, String type,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI1_ODI2_T_Last(
			long objectDefinitionId1, long objectDefinitionId2, String type,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI1_ODI2_T_Last(
		long objectDefinitionId1, long objectDefinitionId2, String type,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship[] findByODI1_ODI2_T_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1,
			long objectDefinitionId2, String type,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 */
	public void removeByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type);

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	public int countByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type);

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @return the matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse);

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse,
		int start, int end);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI1_DT_R_First(
			long objectDefinitionId1, String deletionType, boolean reverse,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI1_DT_R_First(
		long objectDefinitionId1, String deletionType, boolean reverse,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI1_DT_R_Last(
			long objectDefinitionId1, String deletionType, boolean reverse,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI1_DT_R_Last(
		long objectDefinitionId1, String deletionType, boolean reverse,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship[] findByODI1_DT_R_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1,
			String deletionType, boolean reverse,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 */
	public void removeByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse);

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @return the number of matching object relationships
	 */
	public int countByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse);

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @return the matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type);

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type, int start,
		int end);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI1_R_T_First(
			long objectDefinitionId1, boolean reverse, String type,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI1_R_T_First(
		long objectDefinitionId1, boolean reverse, String type,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI1_R_T_Last(
			long objectDefinitionId1, boolean reverse, String type,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI1_R_T_Last(
		long objectDefinitionId1, boolean reverse, String type,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship[] findByODI1_R_T_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1,
			boolean reverse, String type,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 */
	public void removeByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type);

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	public int countByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type);

	/**
	 * Returns all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @return the matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type);

	/**
	 * Returns a range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type, int start,
		int end);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI2_R_T_First(
			long objectDefinitionId2, boolean reverse, String type,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI2_R_T_First(
		long objectDefinitionId2, boolean reverse, String type,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI2_R_T_Last(
			long objectDefinitionId2, boolean reverse, String type,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI2_R_T_Last(
		long objectDefinitionId2, boolean reverse, String type,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship[] findByODI2_R_T_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId2,
			boolean reverse, String type,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Removes all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 */
	public void removeByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type);

	/**
	 * Returns the number of object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	public int countByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type);

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @return the matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type);

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type, int start, int end);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public java.util.List<ObjectRelationship> findByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI1_ODI2_N_T_First(
			long objectDefinitionId1, long objectDefinitionId2, String name,
			String type,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI1_ODI2_N_T_First(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI1_ODI2_N_T_Last(
			long objectDefinitionId1, long objectDefinitionId2, String name,
			String type,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI1_ODI2_N_T_Last(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship[] findByODI1_ODI2_N_T_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1,
			long objectDefinitionId2, String name, String type,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
				orderByComparator)
		throws NoSuchObjectRelationshipException;

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 */
	public void removeByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type);

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	public int countByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type);

	/**
	 * Returns the object relationship where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and reverse = &#63; and type = &#63; or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param reverse the reverse
	 * @param type the type
	 * @return the matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public ObjectRelationship findByODI1_ODI2_N_R_T(
			long objectDefinitionId1, long objectDefinitionId2, String name,
			boolean reverse, String type)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the object relationship where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and reverse = &#63; and type = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param reverse the reverse
	 * @param type the type
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI1_ODI2_N_R_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		boolean reverse, String type);

	/**
	 * Returns the object relationship where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and reverse = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param reverse the reverse
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public ObjectRelationship fetchByODI1_ODI2_N_R_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		boolean reverse, String type, boolean useFinderCache);

	/**
	 * Removes the object relationship where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and reverse = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param reverse the reverse
	 * @param type the type
	 * @return the object relationship that was removed
	 */
	public ObjectRelationship removeByODI1_ODI2_N_R_T(
			long objectDefinitionId1, long objectDefinitionId2, String name,
			boolean reverse, String type)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param reverse the reverse
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	public int countByODI1_ODI2_N_R_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		boolean reverse, String type);

	/**
	 * Caches the object relationship in the entity cache if it is enabled.
	 *
	 * @param objectRelationship the object relationship
	 */
	public void cacheResult(ObjectRelationship objectRelationship);

	/**
	 * Caches the object relationships in the entity cache if it is enabled.
	 *
	 * @param objectRelationships the object relationships
	 */
	public void cacheResult(
		java.util.List<ObjectRelationship> objectRelationships);

	/**
	 * Creates a new object relationship with the primary key. Does not add the object relationship to the database.
	 *
	 * @param objectRelationshipId the primary key for the new object relationship
	 * @return the new object relationship
	 */
	public ObjectRelationship create(long objectRelationshipId);

	/**
	 * Removes the object relationship with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectRelationshipId the primary key of the object relationship
	 * @return the object relationship that was removed
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship remove(long objectRelationshipId)
		throws NoSuchObjectRelationshipException;

	public ObjectRelationship updateImpl(ObjectRelationship objectRelationship);

	/**
	 * Returns the object relationship with the primary key or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param objectRelationshipId the primary key of the object relationship
	 * @return the object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship findByPrimaryKey(long objectRelationshipId)
		throws NoSuchObjectRelationshipException;

	/**
	 * Returns the object relationship with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectRelationshipId the primary key of the object relationship
	 * @return the object relationship, or <code>null</code> if a object relationship with the primary key could not be found
	 */
	public ObjectRelationship fetchByPrimaryKey(long objectRelationshipId);

	/**
	 * Returns all the object relationships.
	 *
	 * @return the object relationships
	 */
	public java.util.List<ObjectRelationship> findAll();

	/**
	 * Returns a range of all the object relationships.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of object relationships
	 */
	public java.util.List<ObjectRelationship> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the object relationships.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object relationships
	 */
	public java.util.List<ObjectRelationship> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object relationships.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object relationships
	 */
	public java.util.List<ObjectRelationship> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectRelationship>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the object relationships from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of object relationships.
	 *
	 * @return the number of object relationships
	 */
	public int countAll();

}