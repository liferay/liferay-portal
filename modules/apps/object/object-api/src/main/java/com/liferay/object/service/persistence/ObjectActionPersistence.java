/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence;

import com.liferay.object.exception.NoSuchObjectActionException;
import com.liferay.object.model.ObjectAction;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the object action service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see ObjectActionUtil
 * @generated
 */
@ProviderType
public interface ObjectActionPersistence extends BasePersistence<ObjectAction> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ObjectActionUtil} to access the object action persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the object actions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object actions
	 */
	public java.util.List<ObjectAction> findByUuid(String uuid);

	/**
	 * Returns a range of all the object actions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of matching object actions
	 */
	public java.util.List<ObjectAction> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the object actions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object actions
	 */
	public java.util.List<ObjectAction> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object actions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	public java.util.List<ObjectAction> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object action in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public ObjectAction findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
				orderByComparator)
		throws NoSuchObjectActionException;

	/**
	 * Returns the first object action in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public ObjectAction fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator);

	/**
	 * Returns the last object action in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public ObjectAction findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
				orderByComparator)
		throws NoSuchObjectActionException;

	/**
	 * Returns the last object action in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public ObjectAction fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator);

	/**
	 * Returns the object actions before and after the current object action in the ordered set where uuid = &#63;.
	 *
	 * @param objectActionId the primary key of the current object action
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	public ObjectAction[] findByUuid_PrevAndNext(
			long objectActionId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
				orderByComparator)
		throws NoSuchObjectActionException;

	/**
	 * Removes all the object actions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of object actions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object actions
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns all the object actions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object actions
	 */
	public java.util.List<ObjectAction> findByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the object actions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of matching object actions
	 */
	public java.util.List<ObjectAction> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the object actions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object actions
	 */
	public java.util.List<ObjectAction> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object actions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	public java.util.List<ObjectAction> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object action in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public ObjectAction findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
				orderByComparator)
		throws NoSuchObjectActionException;

	/**
	 * Returns the first object action in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public ObjectAction fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator);

	/**
	 * Returns the last object action in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public ObjectAction findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
				orderByComparator)
		throws NoSuchObjectActionException;

	/**
	 * Returns the last object action in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public ObjectAction fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator);

	/**
	 * Returns the object actions before and after the current object action in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectActionId the primary key of the current object action
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	public ObjectAction[] findByUuid_C_PrevAndNext(
			long objectActionId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
				orderByComparator)
		throws NoSuchObjectActionException;

	/**
	 * Removes all the object actions where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of object actions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object actions
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns all the object actions where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object actions
	 */
	public java.util.List<ObjectAction> findByObjectDefinitionId(
		long objectDefinitionId);

	/**
	 * Returns a range of all the object actions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of matching object actions
	 */
	public java.util.List<ObjectAction> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end);

	/**
	 * Returns an ordered range of all the object actions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object actions
	 */
	public java.util.List<ObjectAction> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object actions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	public java.util.List<ObjectAction> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object action in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public ObjectAction findByObjectDefinitionId_First(
			long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
				orderByComparator)
		throws NoSuchObjectActionException;

	/**
	 * Returns the first object action in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public ObjectAction fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator);

	/**
	 * Returns the last object action in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public ObjectAction findByObjectDefinitionId_Last(
			long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
				orderByComparator)
		throws NoSuchObjectActionException;

	/**
	 * Returns the last object action in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public ObjectAction fetchByObjectDefinitionId_Last(
		long objectDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator);

	/**
	 * Returns the object actions before and after the current object action in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectActionId the primary key of the current object action
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	public ObjectAction[] findByObjectDefinitionId_PrevAndNext(
			long objectActionId, long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
				orderByComparator)
		throws NoSuchObjectActionException;

	/**
	 * Removes all the object actions where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	public void removeByObjectDefinitionId(long objectDefinitionId);

	/**
	 * Returns the number of object actions where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object actions
	 */
	public int countByObjectDefinitionId(long objectDefinitionId);

	/**
	 * Returns the object action where objectDefinitionId = &#63; and name = &#63; or throws a <code>NoSuchObjectActionException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public ObjectAction findByODI_N(long objectDefinitionId, String name)
		throws NoSuchObjectActionException;

	/**
	 * Returns the object action where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public ObjectAction fetchByODI_N(long objectDefinitionId, String name);

	/**
	 * Returns the object action where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public ObjectAction fetchByODI_N(
		long objectDefinitionId, String name, boolean useFinderCache);

	/**
	 * Removes the object action where objectDefinitionId = &#63; and name = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the object action that was removed
	 */
	public ObjectAction removeByODI_N(long objectDefinitionId, String name)
		throws NoSuchObjectActionException;

	/**
	 * Returns the number of object actions where objectDefinitionId = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the number of matching object actions
	 */
	public int countByODI_N(long objectDefinitionId, String name);

	/**
	 * Returns all the object actions where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @return the matching object actions
	 */
	public java.util.List<ObjectAction> findByA_OAEK(
		boolean active, String objectActionExecutorKey);

	/**
	 * Returns a range of all the object actions where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of matching object actions
	 */
	public java.util.List<ObjectAction> findByA_OAEK(
		boolean active, String objectActionExecutorKey, int start, int end);

	/**
	 * Returns an ordered range of all the object actions where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object actions
	 */
	public java.util.List<ObjectAction> findByA_OAEK(
		boolean active, String objectActionExecutorKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object actions where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	public java.util.List<ObjectAction> findByA_OAEK(
		boolean active, String objectActionExecutorKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object action in the ordered set where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public ObjectAction findByA_OAEK_First(
			boolean active, String objectActionExecutorKey,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
				orderByComparator)
		throws NoSuchObjectActionException;

	/**
	 * Returns the first object action in the ordered set where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public ObjectAction fetchByA_OAEK_First(
		boolean active, String objectActionExecutorKey,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator);

	/**
	 * Returns the last object action in the ordered set where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public ObjectAction findByA_OAEK_Last(
			boolean active, String objectActionExecutorKey,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
				orderByComparator)
		throws NoSuchObjectActionException;

	/**
	 * Returns the last object action in the ordered set where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public ObjectAction fetchByA_OAEK_Last(
		boolean active, String objectActionExecutorKey,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator);

	/**
	 * Returns the object actions before and after the current object action in the ordered set where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param objectActionId the primary key of the current object action
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	public ObjectAction[] findByA_OAEK_PrevAndNext(
			long objectActionId, boolean active, String objectActionExecutorKey,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
				orderByComparator)
		throws NoSuchObjectActionException;

	/**
	 * Removes all the object actions where active = &#63; and objectActionExecutorKey = &#63; from the database.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 */
	public void removeByA_OAEK(boolean active, String objectActionExecutorKey);

	/**
	 * Returns the number of object actions where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @return the number of matching object actions
	 */
	public int countByA_OAEK(boolean active, String objectActionExecutorKey);

	/**
	 * Returns the object action where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or throws a <code>NoSuchObjectActionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public ObjectAction findByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectActionException;

	/**
	 * Returns the object action where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public ObjectAction fetchByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId);

	/**
	 * Returns the object action where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public ObjectAction fetchByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId,
		boolean useFinderCache);

	/**
	 * Removes the object action where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the object action that was removed
	 */
	public ObjectAction removeByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectActionException;

	/**
	 * Returns the number of object actions where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object actions
	 */
	public int countByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId);

	/**
	 * Returns all the object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the matching object actions
	 */
	public java.util.List<ObjectAction> findByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey);

	/**
	 * Returns a range of all the object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of matching object actions
	 */
	public java.util.List<ObjectAction> findByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey,
		int start, int end);

	/**
	 * Returns an ordered range of all the object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object actions
	 */
	public java.util.List<ObjectAction> findByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	public java.util.List<ObjectAction> findByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object action in the ordered set where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public ObjectAction findByC_A_OATK_First(
			long companyId, boolean active, String objectActionTriggerKey,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
				orderByComparator)
		throws NoSuchObjectActionException;

	/**
	 * Returns the first object action in the ordered set where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public ObjectAction fetchByC_A_OATK_First(
		long companyId, boolean active, String objectActionTriggerKey,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator);

	/**
	 * Returns the last object action in the ordered set where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public ObjectAction findByC_A_OATK_Last(
			long companyId, boolean active, String objectActionTriggerKey,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
				orderByComparator)
		throws NoSuchObjectActionException;

	/**
	 * Returns the last object action in the ordered set where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public ObjectAction fetchByC_A_OATK_Last(
		long companyId, boolean active, String objectActionTriggerKey,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator);

	/**
	 * Returns the object actions before and after the current object action in the ordered set where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectActionId the primary key of the current object action
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	public ObjectAction[] findByC_A_OATK_PrevAndNext(
			long objectActionId, long companyId, boolean active,
			String objectActionTriggerKey,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
				orderByComparator)
		throws NoSuchObjectActionException;

	/**
	 * Removes all the object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 */
	public void removeByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey);

	/**
	 * Returns the number of object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the number of matching object actions
	 */
	public int countByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey);

	/**
	 * Returns all the object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the matching object actions
	 */
	public java.util.List<ObjectAction> findByO_A_OATK(
		long objectDefinitionId, boolean active, String objectActionTriggerKey);

	/**
	 * Returns a range of all the object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of matching object actions
	 */
	public java.util.List<ObjectAction> findByO_A_OATK(
		long objectDefinitionId, boolean active, String objectActionTriggerKey,
		int start, int end);

	/**
	 * Returns an ordered range of all the object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object actions
	 */
	public java.util.List<ObjectAction> findByO_A_OATK(
		long objectDefinitionId, boolean active, String objectActionTriggerKey,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	public java.util.List<ObjectAction> findByO_A_OATK(
		long objectDefinitionId, boolean active, String objectActionTriggerKey,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object action in the ordered set where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public ObjectAction findByO_A_OATK_First(
			long objectDefinitionId, boolean active,
			String objectActionTriggerKey,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
				orderByComparator)
		throws NoSuchObjectActionException;

	/**
	 * Returns the first object action in the ordered set where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public ObjectAction fetchByO_A_OATK_First(
		long objectDefinitionId, boolean active, String objectActionTriggerKey,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator);

	/**
	 * Returns the last object action in the ordered set where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public ObjectAction findByO_A_OATK_Last(
			long objectDefinitionId, boolean active,
			String objectActionTriggerKey,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
				orderByComparator)
		throws NoSuchObjectActionException;

	/**
	 * Returns the last object action in the ordered set where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public ObjectAction fetchByO_A_OATK_Last(
		long objectDefinitionId, boolean active, String objectActionTriggerKey,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator);

	/**
	 * Returns the object actions before and after the current object action in the ordered set where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectActionId the primary key of the current object action
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	public ObjectAction[] findByO_A_OATK_PrevAndNext(
			long objectActionId, long objectDefinitionId, boolean active,
			String objectActionTriggerKey,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
				orderByComparator)
		throws NoSuchObjectActionException;

	/**
	 * Removes all the object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 */
	public void removeByO_A_OATK(
		long objectDefinitionId, boolean active, String objectActionTriggerKey);

	/**
	 * Returns the number of object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the number of matching object actions
	 */
	public int countByO_A_OATK(
		long objectDefinitionId, boolean active, String objectActionTriggerKey);

	/**
	 * Returns the object action where objectDefinitionId = &#63; and active = &#63; and name = &#63; and objectActionTriggerKey = &#63; or throws a <code>NoSuchObjectActionException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param name the name
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public ObjectAction findByODI_A_N_OATK(
			long objectDefinitionId, boolean active, String name,
			String objectActionTriggerKey)
		throws NoSuchObjectActionException;

	/**
	 * Returns the object action where objectDefinitionId = &#63; and active = &#63; and name = &#63; and objectActionTriggerKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param name the name
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public ObjectAction fetchByODI_A_N_OATK(
		long objectDefinitionId, boolean active, String name,
		String objectActionTriggerKey);

	/**
	 * Returns the object action where objectDefinitionId = &#63; and active = &#63; and name = &#63; and objectActionTriggerKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param name the name
	 * @param objectActionTriggerKey the object action trigger key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public ObjectAction fetchByODI_A_N_OATK(
		long objectDefinitionId, boolean active, String name,
		String objectActionTriggerKey, boolean useFinderCache);

	/**
	 * Removes the object action where objectDefinitionId = &#63; and active = &#63; and name = &#63; and objectActionTriggerKey = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param name the name
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the object action that was removed
	 */
	public ObjectAction removeByODI_A_N_OATK(
			long objectDefinitionId, boolean active, String name,
			String objectActionTriggerKey)
		throws NoSuchObjectActionException;

	/**
	 * Returns the number of object actions where objectDefinitionId = &#63; and active = &#63; and name = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param name the name
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the number of matching object actions
	 */
	public int countByODI_A_N_OATK(
		long objectDefinitionId, boolean active, String name,
		String objectActionTriggerKey);

	/**
	 * Caches the object action in the entity cache if it is enabled.
	 *
	 * @param objectAction the object action
	 */
	public void cacheResult(ObjectAction objectAction);

	/**
	 * Caches the object actions in the entity cache if it is enabled.
	 *
	 * @param objectActions the object actions
	 */
	public void cacheResult(java.util.List<ObjectAction> objectActions);

	/**
	 * Creates a new object action with the primary key. Does not add the object action to the database.
	 *
	 * @param objectActionId the primary key for the new object action
	 * @return the new object action
	 */
	public ObjectAction create(long objectActionId);

	/**
	 * Removes the object action with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectActionId the primary key of the object action
	 * @return the object action that was removed
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	public ObjectAction remove(long objectActionId)
		throws NoSuchObjectActionException;

	public ObjectAction updateImpl(ObjectAction objectAction);

	/**
	 * Returns the object action with the primary key or throws a <code>NoSuchObjectActionException</code> if it could not be found.
	 *
	 * @param objectActionId the primary key of the object action
	 * @return the object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	public ObjectAction findByPrimaryKey(long objectActionId)
		throws NoSuchObjectActionException;

	/**
	 * Returns the object action with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectActionId the primary key of the object action
	 * @return the object action, or <code>null</code> if a object action with the primary key could not be found
	 */
	public ObjectAction fetchByPrimaryKey(long objectActionId);

	/**
	 * Returns all the object actions.
	 *
	 * @return the object actions
	 */
	public java.util.List<ObjectAction> findAll();

	/**
	 * Returns a range of all the object actions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of object actions
	 */
	public java.util.List<ObjectAction> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the object actions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object actions
	 */
	public java.util.List<ObjectAction> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object actions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object actions
	 */
	public java.util.List<ObjectAction> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectAction>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the object actions from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of object actions.
	 *
	 * @return the number of object actions
	 */
	public int countAll();

}