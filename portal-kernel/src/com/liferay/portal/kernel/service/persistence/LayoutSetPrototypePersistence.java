/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.exception.NoSuchLayoutSetPrototypeException;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the layout set prototype service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutSetPrototypeUtil
 * @generated
 */
@ProviderType
public interface LayoutSetPrototypePersistence
	extends BasePersistence<LayoutSetPrototype>,
			CTPersistence<LayoutSetPrototype> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link LayoutSetPrototypeUtil} to access the layout set prototype persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the layout set prototypes where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findByUuid(String uuid);

	/**
	 * Returns a range of all the layout set prototypes where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @return the range of matching layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the layout set prototypes where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layout set prototypes where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout set prototype in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a matching layout set prototype could not be found
	 */
	public LayoutSetPrototype findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
				orderByComparator)
		throws NoSuchLayoutSetPrototypeException;

	/**
	 * Returns the first layout set prototype in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set prototype, or <code>null</code> if a matching layout set prototype could not be found
	 */
	public LayoutSetPrototype fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator);

	/**
	 * Returns the last layout set prototype in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a matching layout set prototype could not be found
	 */
	public LayoutSetPrototype findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
				orderByComparator)
		throws NoSuchLayoutSetPrototypeException;

	/**
	 * Returns the last layout set prototype in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout set prototype, or <code>null</code> if a matching layout set prototype could not be found
	 */
	public LayoutSetPrototype fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator);

	/**
	 * Returns the layout set prototypes before and after the current layout set prototype in the ordered set where uuid = &#63;.
	 *
	 * @param layoutSetPrototypeId the primary key of the current layout set prototype
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a layout set prototype with the primary key could not be found
	 */
	public LayoutSetPrototype[] findByUuid_PrevAndNext(
			long layoutSetPrototypeId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
				orderByComparator)
		throws NoSuchLayoutSetPrototypeException;

	/**
	 * Returns all the layout set prototypes that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layout set prototypes that the user has permission to view
	 */
	public java.util.List<LayoutSetPrototype> filterFindByUuid(String uuid);

	/**
	 * Returns a range of all the layout set prototypes that the user has permission to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @return the range of matching layout set prototypes that the user has permission to view
	 */
	public java.util.List<LayoutSetPrototype> filterFindByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the layout set prototypes that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set prototypes that the user has permission to view
	 */
	public java.util.List<LayoutSetPrototype> filterFindByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator);

	/**
	 * Returns the layout set prototypes before and after the current layout set prototype in the ordered set of layout set prototypes that the user has permission to view where uuid = &#63;.
	 *
	 * @param layoutSetPrototypeId the primary key of the current layout set prototype
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a layout set prototype with the primary key could not be found
	 */
	public LayoutSetPrototype[] filterFindByUuid_PrevAndNext(
			long layoutSetPrototypeId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
				orderByComparator)
		throws NoSuchLayoutSetPrototypeException;

	/**
	 * Removes all the layout set prototypes where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of layout set prototypes where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout set prototypes
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the number of layout set prototypes that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout set prototypes that the user has permission to view
	 */
	public int filterCountByUuid(String uuid);

	/**
	 * Returns all the layout set prototypes where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the layout set prototypes where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @return the range of matching layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the layout set prototypes where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layout set prototypes where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout set prototype in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a matching layout set prototype could not be found
	 */
	public LayoutSetPrototype findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
				orderByComparator)
		throws NoSuchLayoutSetPrototypeException;

	/**
	 * Returns the first layout set prototype in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set prototype, or <code>null</code> if a matching layout set prototype could not be found
	 */
	public LayoutSetPrototype fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator);

	/**
	 * Returns the last layout set prototype in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a matching layout set prototype could not be found
	 */
	public LayoutSetPrototype findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
				orderByComparator)
		throws NoSuchLayoutSetPrototypeException;

	/**
	 * Returns the last layout set prototype in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout set prototype, or <code>null</code> if a matching layout set prototype could not be found
	 */
	public LayoutSetPrototype fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator);

	/**
	 * Returns the layout set prototypes before and after the current layout set prototype in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param layoutSetPrototypeId the primary key of the current layout set prototype
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a layout set prototype with the primary key could not be found
	 */
	public LayoutSetPrototype[] findByUuid_C_PrevAndNext(
			long layoutSetPrototypeId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
				orderByComparator)
		throws NoSuchLayoutSetPrototypeException;

	/**
	 * Returns all the layout set prototypes that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layout set prototypes that the user has permission to view
	 */
	public java.util.List<LayoutSetPrototype> filterFindByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the layout set prototypes that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @return the range of matching layout set prototypes that the user has permission to view
	 */
	public java.util.List<LayoutSetPrototype> filterFindByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the layout set prototypes that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set prototypes that the user has permission to view
	 */
	public java.util.List<LayoutSetPrototype> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator);

	/**
	 * Returns the layout set prototypes before and after the current layout set prototype in the ordered set of layout set prototypes that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param layoutSetPrototypeId the primary key of the current layout set prototype
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a layout set prototype with the primary key could not be found
	 */
	public LayoutSetPrototype[] filterFindByUuid_C_PrevAndNext(
			long layoutSetPrototypeId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
				orderByComparator)
		throws NoSuchLayoutSetPrototypeException;

	/**
	 * Removes all the layout set prototypes where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of layout set prototypes where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout set prototypes
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of layout set prototypes that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout set prototypes that the user has permission to view
	 */
	public int filterCountByUuid_C(String uuid, long companyId);

	/**
	 * Returns all the layout set prototypes where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findByCompanyId(long companyId);

	/**
	 * Returns a range of all the layout set prototypes where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @return the range of matching layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the layout set prototypes where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layout set prototypes where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout set prototype in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a matching layout set prototype could not be found
	 */
	public LayoutSetPrototype findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
				orderByComparator)
		throws NoSuchLayoutSetPrototypeException;

	/**
	 * Returns the first layout set prototype in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set prototype, or <code>null</code> if a matching layout set prototype could not be found
	 */
	public LayoutSetPrototype fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator);

	/**
	 * Returns the last layout set prototype in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a matching layout set prototype could not be found
	 */
	public LayoutSetPrototype findByCompanyId_Last(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
				orderByComparator)
		throws NoSuchLayoutSetPrototypeException;

	/**
	 * Returns the last layout set prototype in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout set prototype, or <code>null</code> if a matching layout set prototype could not be found
	 */
	public LayoutSetPrototype fetchByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator);

	/**
	 * Returns the layout set prototypes before and after the current layout set prototype in the ordered set where companyId = &#63;.
	 *
	 * @param layoutSetPrototypeId the primary key of the current layout set prototype
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a layout set prototype with the primary key could not be found
	 */
	public LayoutSetPrototype[] findByCompanyId_PrevAndNext(
			long layoutSetPrototypeId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
				orderByComparator)
		throws NoSuchLayoutSetPrototypeException;

	/**
	 * Returns all the layout set prototypes that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching layout set prototypes that the user has permission to view
	 */
	public java.util.List<LayoutSetPrototype> filterFindByCompanyId(
		long companyId);

	/**
	 * Returns a range of all the layout set prototypes that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @return the range of matching layout set prototypes that the user has permission to view
	 */
	public java.util.List<LayoutSetPrototype> filterFindByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the layout set prototypes that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set prototypes that the user has permission to view
	 */
	public java.util.List<LayoutSetPrototype> filterFindByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator);

	/**
	 * Returns the layout set prototypes before and after the current layout set prototype in the ordered set of layout set prototypes that the user has permission to view where companyId = &#63;.
	 *
	 * @param layoutSetPrototypeId the primary key of the current layout set prototype
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a layout set prototype with the primary key could not be found
	 */
	public LayoutSetPrototype[] filterFindByCompanyId_PrevAndNext(
			long layoutSetPrototypeId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
				orderByComparator)
		throws NoSuchLayoutSetPrototypeException;

	/**
	 * Removes all the layout set prototypes where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of layout set prototypes where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching layout set prototypes
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns the number of layout set prototypes that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching layout set prototypes that the user has permission to view
	 */
	public int filterCountByCompanyId(long companyId);

	/**
	 * Returns all the layout set prototypes where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the matching layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findByC_A(
		long companyId, boolean active);

	/**
	 * Returns a range of all the layout set prototypes where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @return the range of matching layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findByC_A(
		long companyId, boolean active, int start, int end);

	/**
	 * Returns an ordered range of all the layout set prototypes where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findByC_A(
		long companyId, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layout set prototypes where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findByC_A(
		long companyId, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout set prototype in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a matching layout set prototype could not be found
	 */
	public LayoutSetPrototype findByC_A_First(
			long companyId, boolean active,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
				orderByComparator)
		throws NoSuchLayoutSetPrototypeException;

	/**
	 * Returns the first layout set prototype in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set prototype, or <code>null</code> if a matching layout set prototype could not be found
	 */
	public LayoutSetPrototype fetchByC_A_First(
		long companyId, boolean active,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator);

	/**
	 * Returns the last layout set prototype in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a matching layout set prototype could not be found
	 */
	public LayoutSetPrototype findByC_A_Last(
			long companyId, boolean active,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
				orderByComparator)
		throws NoSuchLayoutSetPrototypeException;

	/**
	 * Returns the last layout set prototype in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout set prototype, or <code>null</code> if a matching layout set prototype could not be found
	 */
	public LayoutSetPrototype fetchByC_A_Last(
		long companyId, boolean active,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator);

	/**
	 * Returns the layout set prototypes before and after the current layout set prototype in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param layoutSetPrototypeId the primary key of the current layout set prototype
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a layout set prototype with the primary key could not be found
	 */
	public LayoutSetPrototype[] findByC_A_PrevAndNext(
			long layoutSetPrototypeId, long companyId, boolean active,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
				orderByComparator)
		throws NoSuchLayoutSetPrototypeException;

	/**
	 * Returns all the layout set prototypes that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the matching layout set prototypes that the user has permission to view
	 */
	public java.util.List<LayoutSetPrototype> filterFindByC_A(
		long companyId, boolean active);

	/**
	 * Returns a range of all the layout set prototypes that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @return the range of matching layout set prototypes that the user has permission to view
	 */
	public java.util.List<LayoutSetPrototype> filterFindByC_A(
		long companyId, boolean active, int start, int end);

	/**
	 * Returns an ordered range of all the layout set prototypes that the user has permissions to view where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set prototypes that the user has permission to view
	 */
	public java.util.List<LayoutSetPrototype> filterFindByC_A(
		long companyId, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator);

	/**
	 * Returns the layout set prototypes before and after the current layout set prototype in the ordered set of layout set prototypes that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * @param layoutSetPrototypeId the primary key of the current layout set prototype
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a layout set prototype with the primary key could not be found
	 */
	public LayoutSetPrototype[] filterFindByC_A_PrevAndNext(
			long layoutSetPrototypeId, long companyId, boolean active,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
				orderByComparator)
		throws NoSuchLayoutSetPrototypeException;

	/**
	 * Removes all the layout set prototypes where companyId = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 */
	public void removeByC_A(long companyId, boolean active);

	/**
	 * Returns the number of layout set prototypes where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching layout set prototypes
	 */
	public int countByC_A(long companyId, boolean active);

	/**
	 * Returns the number of layout set prototypes that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching layout set prototypes that the user has permission to view
	 */
	public int filterCountByC_A(long companyId, boolean active);

	/**
	 * Caches the layout set prototype in the entity cache if it is enabled.
	 *
	 * @param layoutSetPrototype the layout set prototype
	 */
	public void cacheResult(LayoutSetPrototype layoutSetPrototype);

	/**
	 * Caches the layout set prototypes in the entity cache if it is enabled.
	 *
	 * @param layoutSetPrototypes the layout set prototypes
	 */
	public void cacheResult(
		java.util.List<LayoutSetPrototype> layoutSetPrototypes);

	/**
	 * Creates a new layout set prototype with the primary key. Does not add the layout set prototype to the database.
	 *
	 * @param layoutSetPrototypeId the primary key for the new layout set prototype
	 * @return the new layout set prototype
	 */
	public LayoutSetPrototype create(long layoutSetPrototypeId);

	/**
	 * Removes the layout set prototype with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutSetPrototypeId the primary key of the layout set prototype
	 * @return the layout set prototype that was removed
	 * @throws NoSuchLayoutSetPrototypeException if a layout set prototype with the primary key could not be found
	 */
	public LayoutSetPrototype remove(long layoutSetPrototypeId)
		throws NoSuchLayoutSetPrototypeException;

	public LayoutSetPrototype updateImpl(LayoutSetPrototype layoutSetPrototype);

	/**
	 * Returns the layout set prototype with the primary key or throws a <code>NoSuchLayoutSetPrototypeException</code> if it could not be found.
	 *
	 * @param layoutSetPrototypeId the primary key of the layout set prototype
	 * @return the layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a layout set prototype with the primary key could not be found
	 */
	public LayoutSetPrototype findByPrimaryKey(long layoutSetPrototypeId)
		throws NoSuchLayoutSetPrototypeException;

	/**
	 * Returns the layout set prototype with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutSetPrototypeId the primary key of the layout set prototype
	 * @return the layout set prototype, or <code>null</code> if a layout set prototype with the primary key could not be found
	 */
	public LayoutSetPrototype fetchByPrimaryKey(long layoutSetPrototypeId);

	/**
	 * Returns all the layout set prototypes.
	 *
	 * @return the layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findAll();

	/**
	 * Returns a range of all the layout set prototypes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @return the range of layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the layout set prototypes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layout set prototypes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of layout set prototypes
	 */
	public java.util.List<LayoutSetPrototype> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the layout set prototypes from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of layout set prototypes.
	 *
	 * @return the number of layout set prototypes
	 */
	public int countAll();

}