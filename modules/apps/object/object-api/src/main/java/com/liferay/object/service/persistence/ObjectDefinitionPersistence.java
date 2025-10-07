/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence;

import com.liferay.object.exception.NoSuchObjectDefinitionException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the object definition service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see ObjectDefinitionUtil
 * @generated
 */
@ProviderType
public interface ObjectDefinitionPersistence
	extends BasePersistence<ObjectDefinition> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ObjectDefinitionUtil} to access the object definition persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the object definitions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByUuid(String uuid);

	/**
	 * Returns a range of all the object definitions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object definitions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object definition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the first object definition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the last object definition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the last object definition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where uuid = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] findByUuid_PrevAndNext(
			long objectDefinitionId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns all the object definitions that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByUuid(String uuid);

	/**
	 * Returns a range of all the object definitions that the user has permission to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where uuid = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] filterFindByUuid_PrevAndNext(
			long objectDefinitionId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Removes all the object definitions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of object definitions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object definitions
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the number of object definitions that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public int filterCountByUuid(String uuid);

	/**
	 * Returns all the object definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the object definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the first object definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the last object definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the last object definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] findByUuid_C_PrevAndNext(
			long objectDefinitionId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns all the object definitions that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the object definitions that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] filterFindByUuid_C_PrevAndNext(
			long objectDefinitionId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Removes all the object definitions where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of object definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object definitions
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of object definitions that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public int filterCountByUuid_C(String uuid, long companyId);

	/**
	 * Returns all the object definitions where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByCompanyId(long companyId);

	/**
	 * Returns a range of all the object definitions where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByCompanyId_Last(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where companyId = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] findByCompanyId_PrevAndNext(
			long objectDefinitionId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns all the object definitions that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByCompanyId(
		long companyId);

	/**
	 * Returns a range of all the object definitions that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where companyId = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] filterFindByCompanyId_PrevAndNext(
			long objectDefinitionId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Removes all the object definitions where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of object definitions where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching object definitions
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public int filterCountByCompanyId(long companyId);

	/**
	 * Returns all the object definitions where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @return the matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByObjectFolderId(
		long objectFolderId);

	/**
	 * Returns a range of all the object definitions where objectFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param objectFolderId the object folder ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByObjectFolderId(
		long objectFolderId, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions where objectFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param objectFolderId the object folder ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByObjectFolderId(
		long objectFolderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object definitions where objectFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param objectFolderId the object folder ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByObjectFolderId(
		long objectFolderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object definition in the ordered set where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByObjectFolderId_First(
			long objectFolderId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the first object definition in the ordered set where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByObjectFolderId_First(
		long objectFolderId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the last object definition in the ordered set where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByObjectFolderId_Last(
			long objectFolderId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the last object definition in the ordered set where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByObjectFolderId_Last(
		long objectFolderId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where objectFolderId = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param objectFolderId the object folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] findByObjectFolderId_PrevAndNext(
			long objectDefinitionId, long objectFolderId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns all the object definitions that the user has permission to view where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @return the matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByObjectFolderId(
		long objectFolderId);

	/**
	 * Returns a range of all the object definitions that the user has permission to view where objectFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param objectFolderId the object folder ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByObjectFolderId(
		long objectFolderId, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where objectFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param objectFolderId the object folder ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByObjectFolderId(
		long objectFolderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where objectFolderId = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param objectFolderId the object folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] filterFindByObjectFolderId_PrevAndNext(
			long objectDefinitionId, long objectFolderId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Removes all the object definitions where objectFolderId = &#63; from the database.
	 *
	 * @param objectFolderId the object folder ID
	 */
	public void removeByObjectFolderId(long objectFolderId);

	/**
	 * Returns the number of object definitions where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @return the number of matching object definitions
	 */
	public int countByObjectFolderId(long objectFolderId);

	/**
	 * Returns the number of object definitions that the user has permission to view where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public int filterCountByObjectFolderId(long objectFolderId);

	/**
	 * Returns all the object definitions where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @return the matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByAccountEntryRestricted(
		boolean accountEntryRestricted);

	/**
	 * Returns a range of all the object definitions where accountEntryRestricted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByAccountEntryRestricted(
		boolean accountEntryRestricted, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions where accountEntryRestricted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByAccountEntryRestricted(
		boolean accountEntryRestricted, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object definitions where accountEntryRestricted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByAccountEntryRestricted(
		boolean accountEntryRestricted, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object definition in the ordered set where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByAccountEntryRestricted_First(
			boolean accountEntryRestricted,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the first object definition in the ordered set where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByAccountEntryRestricted_First(
		boolean accountEntryRestricted,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the last object definition in the ordered set where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByAccountEntryRestricted_Last(
			boolean accountEntryRestricted,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the last object definition in the ordered set where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByAccountEntryRestricted_Last(
		boolean accountEntryRestricted,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where accountEntryRestricted = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param accountEntryRestricted the account entry restricted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] findByAccountEntryRestricted_PrevAndNext(
			long objectDefinitionId, boolean accountEntryRestricted,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns all the object definitions that the user has permission to view where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @return the matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByAccountEntryRestricted(
		boolean accountEntryRestricted);

	/**
	 * Returns a range of all the object definitions that the user has permission to view where accountEntryRestricted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByAccountEntryRestricted(
		boolean accountEntryRestricted, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where accountEntryRestricted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByAccountEntryRestricted(
		boolean accountEntryRestricted, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where accountEntryRestricted = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param accountEntryRestricted the account entry restricted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] filterFindByAccountEntryRestricted_PrevAndNext(
			long objectDefinitionId, boolean accountEntryRestricted,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Removes all the object definitions where accountEntryRestricted = &#63; from the database.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 */
	public void removeByAccountEntryRestricted(boolean accountEntryRestricted);

	/**
	 * Returns the number of object definitions where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @return the number of matching object definitions
	 */
	public int countByAccountEntryRestricted(boolean accountEntryRestricted);

	/**
	 * Returns the number of object definitions that the user has permission to view where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public int filterCountByAccountEntryRestricted(
		boolean accountEntryRestricted);

	/**
	 * Returns the object definition where className = &#63; or throws a <code>NoSuchObjectDefinitionException</code> if it could not be found.
	 *
	 * @param className the class name
	 * @return the matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByClassName(String className)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the object definition where className = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param className the class name
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByClassName(String className);

	/**
	 * Returns the object definition where className = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param className the class name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByClassName(
		String className, boolean useFinderCache);

	/**
	 * Removes the object definition where className = &#63; from the database.
	 *
	 * @param className the class name
	 * @return the object definition that was removed
	 */
	public ObjectDefinition removeByClassName(String className)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the number of object definitions where className = &#63;.
	 *
	 * @param className the class name
	 * @return the number of matching object definitions
	 */
	public int countByClassName(String className);

	/**
	 * Returns all the object definitions where system = &#63;.
	 *
	 * @param system the system
	 * @return the matching object definitions
	 */
	public java.util.List<ObjectDefinition> findBySystem(boolean system);

	/**
	 * Returns a range of all the object definitions where system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findBySystem(
		boolean system, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions where system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findBySystem(
		boolean system, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object definitions where system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findBySystem(
		boolean system, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object definition in the ordered set where system = &#63;.
	 *
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findBySystem_First(
			boolean system,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the first object definition in the ordered set where system = &#63;.
	 *
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchBySystem_First(
		boolean system,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the last object definition in the ordered set where system = &#63;.
	 *
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findBySystem_Last(
			boolean system,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the last object definition in the ordered set where system = &#63;.
	 *
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchBySystem_Last(
		boolean system,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where system = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] findBySystem_PrevAndNext(
			long objectDefinitionId, boolean system,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns all the object definitions that the user has permission to view where system = &#63;.
	 *
	 * @param system the system
	 * @return the matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindBySystem(boolean system);

	/**
	 * Returns a range of all the object definitions that the user has permission to view where system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindBySystem(
		boolean system, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindBySystem(
		boolean system, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where system = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] filterFindBySystem_PrevAndNext(
			long objectDefinitionId, boolean system,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Removes all the object definitions where system = &#63; from the database.
	 *
	 * @param system the system
	 */
	public void removeBySystem(boolean system);

	/**
	 * Returns the number of object definitions where system = &#63;.
	 *
	 * @param system the system
	 * @return the number of matching object definitions
	 */
	public int countBySystem(boolean system);

	/**
	 * Returns the number of object definitions that the user has permission to view where system = &#63;.
	 *
	 * @param system the system
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public int filterCountBySystem(boolean system);

	/**
	 * Returns all the object definitions where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_U(
		long companyId, long userId);

	/**
	 * Returns a range of all the object definitions where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_U(
		long companyId, long userId, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_U(
		long companyId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_U(
		long companyId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByC_U_First(
			long companyId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByC_U_First(
		long companyId, long userId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByC_U_Last(
			long companyId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByC_U_Last(
		long companyId, long userId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] findByC_U_PrevAndNext(
			long objectDefinitionId, long companyId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns all the object definitions that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_U(
		long companyId, long userId);

	/**
	 * Returns a range of all the object definitions that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_U(
		long companyId, long userId, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_U(
		long companyId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] filterFindByC_U_PrevAndNext(
			long objectDefinitionId, long companyId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Removes all the object definitions where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	public void removeByC_U(long companyId, long userId);

	/**
	 * Returns the number of object definitions where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching object definitions
	 */
	public int countByC_U(long companyId, long userId);

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public int filterCountByC_U(long companyId, long userId);

	/**
	 * Returns the object definition where companyId = &#63; and className = &#63; or throws a <code>NoSuchObjectDefinitionException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @return the matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByC_C(long companyId, String className)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the object definition where companyId = &#63; and className = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByC_C(long companyId, String className);

	/**
	 * Returns the object definition where companyId = &#63; and className = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByC_C(
		long companyId, String className, boolean useFinderCache);

	/**
	 * Removes the object definition where companyId = &#63; and className = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @return the object definition that was removed
	 */
	public ObjectDefinition removeByC_C(long companyId, String className)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the number of object definitions where companyId = &#63; and className = &#63;.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @return the number of matching object definitions
	 */
	public int countByC_C(long companyId, String className);

	/**
	 * Returns the object definition where companyId = &#63; and name = &#63; or throws a <code>NoSuchObjectDefinitionException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByC_N(long companyId, String name)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the object definition where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByC_N(long companyId, String name);

	/**
	 * Returns the object definition where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByC_N(
		long companyId, String name, boolean useFinderCache);

	/**
	 * Removes the object definition where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the object definition that was removed
	 */
	public ObjectDefinition removeByC_N(long companyId, String name)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the number of object definitions where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching object definitions
	 */
	public int countByC_N(long companyId, String name);

	/**
	 * Returns all the object definitions where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_S(
		long companyId, int status);

	/**
	 * Returns a range of all the object definitions where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_S(
		long companyId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_S(
		long companyId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_S(
		long companyId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByC_S_First(
			long companyId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByC_S_First(
		long companyId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByC_S_Last(
			long companyId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByC_S_Last(
		long companyId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] findByC_S_PrevAndNext(
			long objectDefinitionId, long companyId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns all the object definitions that the user has permission to view where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_S(
		long companyId, int status);

	/**
	 * Returns a range of all the object definitions that the user has permission to view where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_S(
		long companyId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_S(
		long companyId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where companyId = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] filterFindByC_S_PrevAndNext(
			long objectDefinitionId, long companyId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Removes all the object definitions where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	public void removeByC_S(long companyId, int status);

	/**
	 * Returns the number of object definitions where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching object definitions
	 */
	public int countByC_S(long companyId, int status);

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public int filterCountByC_S(long companyId, int status);

	/**
	 * Returns all the object definitions where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @return the matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByS_S(
		boolean system, int status);

	/**
	 * Returns a range of all the object definitions where system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByS_S(
		boolean system, int status, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions where system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByS_S(
		boolean system, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object definitions where system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByS_S(
		boolean system, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object definition in the ordered set where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByS_S_First(
			boolean system, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the first object definition in the ordered set where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByS_S_First(
		boolean system, int status,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the last object definition in the ordered set where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByS_S_Last(
			boolean system, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the last object definition in the ordered set where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByS_S_Last(
		boolean system, int status,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where system = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] findByS_S_PrevAndNext(
			long objectDefinitionId, boolean system, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns all the object definitions that the user has permission to view where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @return the matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByS_S(
		boolean system, int status);

	/**
	 * Returns a range of all the object definitions that the user has permission to view where system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByS_S(
		boolean system, int status, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByS_S(
		boolean system, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where system = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] filterFindByS_S_PrevAndNext(
			long objectDefinitionId, boolean system, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Removes all the object definitions where system = &#63; and status = &#63; from the database.
	 *
	 * @param system the system
	 * @param status the status
	 */
	public void removeByS_S(boolean system, int status);

	/**
	 * Returns the number of object definitions where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @return the number of matching object definitions
	 */
	public int countByS_S(boolean system, int status);

	/**
	 * Returns the number of object definitions that the user has permission to view where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public int filterCountByS_S(boolean system, int status);

	/**
	 * Returns all the object definitions where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @return the matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_A_S(
		long companyId, boolean active, int status);

	/**
	 * Returns a range of all the object definitions where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_A_S(
		long companyId, boolean active, int status, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_A_S(
		long companyId, boolean active, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_A_S(
		long companyId, boolean active, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByC_A_S_First(
			long companyId, boolean active, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByC_A_S_First(
		long companyId, boolean active, int status,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByC_A_S_Last(
			long companyId, boolean active, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByC_A_S_Last(
		long companyId, boolean active, int status,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] findByC_A_S_PrevAndNext(
			long objectDefinitionId, long companyId, boolean active, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns all the object definitions that the user has permission to view where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @return the matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_A_S(
		long companyId, boolean active, int status);

	/**
	 * Returns a range of all the object definitions that the user has permission to view where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_A_S(
		long companyId, boolean active, int status, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_A_S(
		long companyId, boolean active, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] filterFindByC_A_S_PrevAndNext(
			long objectDefinitionId, long companyId, boolean active, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Removes all the object definitions where companyId = &#63; and active = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 */
	public void removeByC_A_S(long companyId, boolean active, int status);

	/**
	 * Returns the number of object definitions where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @return the number of matching object definitions
	 */
	public int countByC_A_S(long companyId, boolean active, int status);

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public int filterCountByC_A_S(long companyId, boolean active, int status);

	/**
	 * Returns all the object definitions where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @return the matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_M_S(
		long companyId, boolean modifiable, boolean system);

	/**
	 * Returns a range of all the object definitions where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_M_S(
		long companyId, boolean modifiable, boolean system, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_M_S(
		long companyId, boolean modifiable, boolean system, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_M_S(
		long companyId, boolean modifiable, boolean system, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByC_M_S_First(
			long companyId, boolean modifiable, boolean system,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByC_M_S_First(
		long companyId, boolean modifiable, boolean system,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByC_M_S_Last(
			long companyId, boolean modifiable, boolean system,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByC_M_S_Last(
		long companyId, boolean modifiable, boolean system,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] findByC_M_S_PrevAndNext(
			long objectDefinitionId, long companyId, boolean modifiable,
			boolean system,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns all the object definitions that the user has permission to view where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @return the matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_M_S(
		long companyId, boolean modifiable, boolean system);

	/**
	 * Returns a range of all the object definitions that the user has permission to view where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_M_S(
		long companyId, boolean modifiable, boolean system, int start, int end);

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_M_S(
		long companyId, boolean modifiable, boolean system, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] filterFindByC_M_S_PrevAndNext(
			long objectDefinitionId, long companyId, boolean modifiable,
			boolean system,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Removes all the object definitions where companyId = &#63; and modifiable = &#63; and system = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 */
	public void removeByC_M_S(
		long companyId, boolean modifiable, boolean system);

	/**
	 * Returns the number of object definitions where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @return the number of matching object definitions
	 */
	public int countByC_M_S(long companyId, boolean modifiable, boolean system);

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public int filterCountByC_M_S(
		long companyId, boolean modifiable, boolean system);

	/**
	 * Returns all the object definitions where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @return the matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_A_S_S(
		long companyId, boolean active, boolean system, int status);

	/**
	 * Returns a range of all the object definitions where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_A_S_S(
		long companyId, boolean active, boolean system, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_A_S_S(
		long companyId, boolean active, boolean system, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_A_S_S(
		long companyId, boolean active, boolean system, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByC_A_S_S_First(
			long companyId, boolean active, boolean system, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByC_A_S_S_First(
		long companyId, boolean active, boolean system, int status,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByC_A_S_S_Last(
			long companyId, boolean active, boolean system, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByC_A_S_S_Last(
		long companyId, boolean active, boolean system, int status,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] findByC_A_S_S_PrevAndNext(
			long objectDefinitionId, long companyId, boolean active,
			boolean system, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns all the object definitions that the user has permission to view where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @return the matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_A_S_S(
		long companyId, boolean active, boolean system, int status);

	/**
	 * Returns a range of all the object definitions that the user has permission to view where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_A_S_S(
		long companyId, boolean active, boolean system, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_A_S_S(
		long companyId, boolean active, boolean system, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] filterFindByC_A_S_S_PrevAndNext(
			long objectDefinitionId, long companyId, boolean active,
			boolean system, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Removes all the object definitions where companyId = &#63; and active = &#63; and system = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 */
	public void removeByC_A_S_S(
		long companyId, boolean active, boolean system, int status);

	/**
	 * Returns the number of object definitions where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @return the number of matching object definitions
	 */
	public int countByC_A_S_S(
		long companyId, boolean active, boolean system, int status);

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public int filterCountByC_A_S_S(
		long companyId, boolean active, boolean system, int status);

	/**
	 * Returns all the object definitions where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status);

	/**
	 * Returns a range of all the object definitions where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByC_OFI_A_E_S_S_First(
			long companyId, long objectFolderId, boolean active,
			boolean enableObjectEntryDraft, String scope, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByC_OFI_A_E_S_S_First(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByC_OFI_A_E_S_S_Last(
			long companyId, long objectFolderId, boolean active,
			boolean enableObjectEntryDraft, String scope, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByC_OFI_A_E_S_S_Last(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] findByC_OFI_A_E_S_S_PrevAndNext(
			long objectDefinitionId, long companyId, long objectFolderId,
			boolean active, boolean enableObjectEntryDraft, String scope,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns all the object definitions that the user has permission to view where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status);

	/**
	 * Returns a range of all the object definitions that the user has permission to view where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition[] filterFindByC_OFI_A_E_S_S_PrevAndNext(
			long objectDefinitionId, long companyId, long objectFolderId,
			boolean active, boolean enableObjectEntryDraft, String scope,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
				orderByComparator)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns all the object definitions that the user has permission to view where companyId = &#63; and objectFolderId = any &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status);

	/**
	 * Returns a range of all the object definitions that the user has permission to view where companyId = &#63; and objectFolderId = any &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the object definitions that the user has permission to view where companyId = &#63; and objectFolderId = any &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public java.util.List<ObjectDefinition> filterFindByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns all the object definitions where companyId = &#63; and objectFolderId = any &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status);

	/**
	 * Returns a range of all the object definitions where companyId = &#63; and objectFolderId = any &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and objectFolderId = any &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public java.util.List<ObjectDefinition> findByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the object definitions where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 */
	public void removeByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status);

	/**
	 * Returns the number of object definitions where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the number of matching object definitions
	 */
	public int countByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status);

	/**
	 * Returns the number of object definitions where companyId = &#63; and objectFolderId = any &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the number of matching object definitions
	 */
	public int countByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status);

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public int filterCountByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status);

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and objectFolderId = any &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public int filterCountByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status);

	/**
	 * Returns the object definition where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchObjectDefinitionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public ObjectDefinition findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the object definition where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByERC_C(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the object definition where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public ObjectDefinition fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the object definition where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the object definition that was removed
	 */
	public ObjectDefinition removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the number of object definitions where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching object definitions
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Caches the object definition in the entity cache if it is enabled.
	 *
	 * @param objectDefinition the object definition
	 */
	public void cacheResult(ObjectDefinition objectDefinition);

	/**
	 * Caches the object definitions in the entity cache if it is enabled.
	 *
	 * @param objectDefinitions the object definitions
	 */
	public void cacheResult(java.util.List<ObjectDefinition> objectDefinitions);

	/**
	 * Creates a new object definition with the primary key. Does not add the object definition to the database.
	 *
	 * @param objectDefinitionId the primary key for the new object definition
	 * @return the new object definition
	 */
	public ObjectDefinition create(long objectDefinitionId);

	/**
	 * Removes the object definition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectDefinitionId the primary key of the object definition
	 * @return the object definition that was removed
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition remove(long objectDefinitionId)
		throws NoSuchObjectDefinitionException;

	public ObjectDefinition updateImpl(ObjectDefinition objectDefinition);

	/**
	 * Returns the object definition with the primary key or throws a <code>NoSuchObjectDefinitionException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the primary key of the object definition
	 * @return the object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public ObjectDefinition findByPrimaryKey(long objectDefinitionId)
		throws NoSuchObjectDefinitionException;

	/**
	 * Returns the object definition with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectDefinitionId the primary key of the object definition
	 * @return the object definition, or <code>null</code> if a object definition with the primary key could not be found
	 */
	public ObjectDefinition fetchByPrimaryKey(long objectDefinitionId);

	/**
	 * Returns all the object definitions.
	 *
	 * @return the object definitions
	 */
	public java.util.List<ObjectDefinition> findAll();

	/**
	 * Returns a range of all the object definitions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of object definitions
	 */
	public java.util.List<ObjectDefinition> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the object definitions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object definitions
	 */
	public java.util.List<ObjectDefinition> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object definitions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object definitions
	 */
	public java.util.List<ObjectDefinition> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectDefinition>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the object definitions from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of object definitions.
	 *
	 * @return the number of object definitions
	 */
	public int countAll();

}