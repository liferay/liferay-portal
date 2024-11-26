/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence;

import com.liferay.object.exception.NoSuchObjectLayoutException;
import com.liferay.object.model.ObjectLayout;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the object layout service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see ObjectLayoutUtil
 * @generated
 */
@ProviderType
public interface ObjectLayoutPersistence extends BasePersistence<ObjectLayout> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ObjectLayoutUtil} to access the object layout persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the object layouts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object layouts
	 */
	public java.util.List<ObjectLayout> findByUuid(String uuid);

	/**
	 * Returns a range of all the object layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of matching object layouts
	 */
	public java.util.List<ObjectLayout> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the object layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object layouts
	 */
	public java.util.List<ObjectLayout> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	public java.util.List<ObjectLayout> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public ObjectLayout findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
				orderByComparator)
		throws NoSuchObjectLayoutException;

	/**
	 * Returns the first object layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public ObjectLayout fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator);

	/**
	 * Returns the last object layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public ObjectLayout findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
				orderByComparator)
		throws NoSuchObjectLayoutException;

	/**
	 * Returns the last object layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public ObjectLayout fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator);

	/**
	 * Returns the object layouts before and after the current object layout in the ordered set where uuid = &#63;.
	 *
	 * @param objectLayoutId the primary key of the current object layout
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	public ObjectLayout[] findByUuid_PrevAndNext(
			long objectLayoutId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
				orderByComparator)
		throws NoSuchObjectLayoutException;

	/**
	 * Removes all the object layouts where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of object layouts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object layouts
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns all the object layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object layouts
	 */
	public java.util.List<ObjectLayout> findByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the object layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of matching object layouts
	 */
	public java.util.List<ObjectLayout> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the object layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object layouts
	 */
	public java.util.List<ObjectLayout> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	public java.util.List<ObjectLayout> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public ObjectLayout findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
				orderByComparator)
		throws NoSuchObjectLayoutException;

	/**
	 * Returns the first object layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public ObjectLayout fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator);

	/**
	 * Returns the last object layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public ObjectLayout findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
				orderByComparator)
		throws NoSuchObjectLayoutException;

	/**
	 * Returns the last object layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public ObjectLayout fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator);

	/**
	 * Returns the object layouts before and after the current object layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectLayoutId the primary key of the current object layout
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	public ObjectLayout[] findByUuid_C_PrevAndNext(
			long objectLayoutId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
				orderByComparator)
		throws NoSuchObjectLayoutException;

	/**
	 * Removes all the object layouts where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of object layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object layouts
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns all the object layouts where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object layouts
	 */
	public java.util.List<ObjectLayout> findByObjectDefinitionId(
		long objectDefinitionId);

	/**
	 * Returns a range of all the object layouts where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of matching object layouts
	 */
	public java.util.List<ObjectLayout> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end);

	/**
	 * Returns an ordered range of all the object layouts where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object layouts
	 */
	public java.util.List<ObjectLayout> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object layouts where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	public java.util.List<ObjectLayout> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object layout in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public ObjectLayout findByObjectDefinitionId_First(
			long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
				orderByComparator)
		throws NoSuchObjectLayoutException;

	/**
	 * Returns the first object layout in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public ObjectLayout fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator);

	/**
	 * Returns the last object layout in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public ObjectLayout findByObjectDefinitionId_Last(
			long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
				orderByComparator)
		throws NoSuchObjectLayoutException;

	/**
	 * Returns the last object layout in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public ObjectLayout fetchByObjectDefinitionId_Last(
		long objectDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator);

	/**
	 * Returns the object layouts before and after the current object layout in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectLayoutId the primary key of the current object layout
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	public ObjectLayout[] findByObjectDefinitionId_PrevAndNext(
			long objectLayoutId, long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
				orderByComparator)
		throws NoSuchObjectLayoutException;

	/**
	 * Removes all the object layouts where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	public void removeByObjectDefinitionId(long objectDefinitionId);

	/**
	 * Returns the number of object layouts where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object layouts
	 */
	public int countByObjectDefinitionId(long objectDefinitionId);

	/**
	 * Returns all the object layouts where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @return the matching object layouts
	 */
	public java.util.List<ObjectLayout> findByC_DOL(
		long companyId, boolean defaultObjectLayout);

	/**
	 * Returns a range of all the object layouts where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of matching object layouts
	 */
	public java.util.List<ObjectLayout> findByC_DOL(
		long companyId, boolean defaultObjectLayout, int start, int end);

	/**
	 * Returns an ordered range of all the object layouts where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object layouts
	 */
	public java.util.List<ObjectLayout> findByC_DOL(
		long companyId, boolean defaultObjectLayout, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object layouts where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	public java.util.List<ObjectLayout> findByC_DOL(
		long companyId, boolean defaultObjectLayout, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object layout in the ordered set where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public ObjectLayout findByC_DOL_First(
			long companyId, boolean defaultObjectLayout,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
				orderByComparator)
		throws NoSuchObjectLayoutException;

	/**
	 * Returns the first object layout in the ordered set where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public ObjectLayout fetchByC_DOL_First(
		long companyId, boolean defaultObjectLayout,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator);

	/**
	 * Returns the last object layout in the ordered set where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public ObjectLayout findByC_DOL_Last(
			long companyId, boolean defaultObjectLayout,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
				orderByComparator)
		throws NoSuchObjectLayoutException;

	/**
	 * Returns the last object layout in the ordered set where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public ObjectLayout fetchByC_DOL_Last(
		long companyId, boolean defaultObjectLayout,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator);

	/**
	 * Returns the object layouts before and after the current object layout in the ordered set where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectLayoutId the primary key of the current object layout
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	public ObjectLayout[] findByC_DOL_PrevAndNext(
			long objectLayoutId, long companyId, boolean defaultObjectLayout,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
				orderByComparator)
		throws NoSuchObjectLayoutException;

	/**
	 * Removes all the object layouts where companyId = &#63; and defaultObjectLayout = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 */
	public void removeByC_DOL(long companyId, boolean defaultObjectLayout);

	/**
	 * Returns the number of object layouts where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @return the number of matching object layouts
	 */
	public int countByC_DOL(long companyId, boolean defaultObjectLayout);

	/**
	 * Returns all the object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @return the matching object layouts
	 */
	public java.util.List<ObjectLayout> findByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout);

	/**
	 * Returns a range of all the object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of matching object layouts
	 */
	public java.util.List<ObjectLayout> findByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout, int start,
		int end);

	/**
	 * Returns an ordered range of all the object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object layouts
	 */
	public java.util.List<ObjectLayout> findByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	public java.util.List<ObjectLayout> findByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object layout in the ordered set where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public ObjectLayout findByODI_DOL_First(
			long objectDefinitionId, boolean defaultObjectLayout,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
				orderByComparator)
		throws NoSuchObjectLayoutException;

	/**
	 * Returns the first object layout in the ordered set where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public ObjectLayout fetchByODI_DOL_First(
		long objectDefinitionId, boolean defaultObjectLayout,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator);

	/**
	 * Returns the last object layout in the ordered set where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public ObjectLayout findByODI_DOL_Last(
			long objectDefinitionId, boolean defaultObjectLayout,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
				orderByComparator)
		throws NoSuchObjectLayoutException;

	/**
	 * Returns the last object layout in the ordered set where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public ObjectLayout fetchByODI_DOL_Last(
		long objectDefinitionId, boolean defaultObjectLayout,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator);

	/**
	 * Returns the object layouts before and after the current object layout in the ordered set where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectLayoutId the primary key of the current object layout
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	public ObjectLayout[] findByODI_DOL_PrevAndNext(
			long objectLayoutId, long objectDefinitionId,
			boolean defaultObjectLayout,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
				orderByComparator)
		throws NoSuchObjectLayoutException;

	/**
	 * Removes all the object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 */
	public void removeByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout);

	/**
	 * Returns the number of object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @return the number of matching object layouts
	 */
	public int countByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout);

	/**
	 * Caches the object layout in the entity cache if it is enabled.
	 *
	 * @param objectLayout the object layout
	 */
	public void cacheResult(ObjectLayout objectLayout);

	/**
	 * Caches the object layouts in the entity cache if it is enabled.
	 *
	 * @param objectLayouts the object layouts
	 */
	public void cacheResult(java.util.List<ObjectLayout> objectLayouts);

	/**
	 * Creates a new object layout with the primary key. Does not add the object layout to the database.
	 *
	 * @param objectLayoutId the primary key for the new object layout
	 * @return the new object layout
	 */
	public ObjectLayout create(long objectLayoutId);

	/**
	 * Removes the object layout with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectLayoutId the primary key of the object layout
	 * @return the object layout that was removed
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	public ObjectLayout remove(long objectLayoutId)
		throws NoSuchObjectLayoutException;

	public ObjectLayout updateImpl(ObjectLayout objectLayout);

	/**
	 * Returns the object layout with the primary key or throws a <code>NoSuchObjectLayoutException</code> if it could not be found.
	 *
	 * @param objectLayoutId the primary key of the object layout
	 * @return the object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	public ObjectLayout findByPrimaryKey(long objectLayoutId)
		throws NoSuchObjectLayoutException;

	/**
	 * Returns the object layout with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectLayoutId the primary key of the object layout
	 * @return the object layout, or <code>null</code> if a object layout with the primary key could not be found
	 */
	public ObjectLayout fetchByPrimaryKey(long objectLayoutId);

	/**
	 * Returns all the object layouts.
	 *
	 * @return the object layouts
	 */
	public java.util.List<ObjectLayout> findAll();

	/**
	 * Returns a range of all the object layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of object layouts
	 */
	public java.util.List<ObjectLayout> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the object layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object layouts
	 */
	public java.util.List<ObjectLayout> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object layouts
	 */
	public java.util.List<ObjectLayout> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectLayout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the object layouts from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of object layouts.
	 *
	 * @return the number of object layouts
	 */
	public int countAll();

}