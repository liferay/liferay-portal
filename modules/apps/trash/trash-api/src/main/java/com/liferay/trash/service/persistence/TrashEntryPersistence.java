/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.trash.exception.NoSuchEntryException;
import com.liferay.trash.model.TrashEntry;

import java.util.Date;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the trash entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see TrashEntryUtil
 * @generated
 */
@ProviderType
public interface TrashEntryPersistence
	extends BasePersistence<TrashEntry>, CTPersistence<TrashEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link TrashEntryUtil} to access the trash entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the trash entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching trash entries
	 */
	public java.util.List<TrashEntry> findByGroupId(long groupId);

	/**
	 * Returns a range of all the trash entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of matching trash entries
	 */
	public java.util.List<TrashEntry> findByGroupId(
		long groupId, int start, int end);

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching trash entries
	 */
	public java.util.List<TrashEntry> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	public java.util.List<TrashEntry> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public TrashEntry findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public TrashEntry fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator);

	/**
	 * Returns the last trash entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public TrashEntry findByGroupId_Last(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the last trash entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public TrashEntry fetchByGroupId_Last(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator);

	/**
	 * Returns the trash entries before and after the current trash entry in the ordered set where groupId = &#63;.
	 *
	 * @param entryId the primary key of the current trash entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	public TrashEntry[] findByGroupId_PrevAndNext(
			long entryId, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Removes all the trash entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of trash entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching trash entries
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns all the trash entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching trash entries
	 */
	public java.util.List<TrashEntry> findByCompanyId(long companyId);

	/**
	 * Returns a range of all the trash entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of matching trash entries
	 */
	public java.util.List<TrashEntry> findByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the trash entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching trash entries
	 */
	public java.util.List<TrashEntry> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the trash entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	public java.util.List<TrashEntry> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first trash entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public TrashEntry findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first trash entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public TrashEntry fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator);

	/**
	 * Returns the last trash entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public TrashEntry findByCompanyId_Last(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the last trash entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public TrashEntry fetchByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator);

	/**
	 * Returns the trash entries before and after the current trash entry in the ordered set where companyId = &#63;.
	 *
	 * @param entryId the primary key of the current trash entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	public TrashEntry[] findByCompanyId_PrevAndNext(
			long entryId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Removes all the trash entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of trash entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching trash entries
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns all the trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @return the matching trash entries
	 */
	public java.util.List<TrashEntry> findByG_LtCD(
		long groupId, Date createDate);

	/**
	 * Returns a range of all the trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of matching trash entries
	 */
	public java.util.List<TrashEntry> findByG_LtCD(
		long groupId, Date createDate, int start, int end);

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching trash entries
	 */
	public java.util.List<TrashEntry> findByG_LtCD(
		long groupId, Date createDate, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	public java.util.List<TrashEntry> findByG_LtCD(
		long groupId, Date createDate, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public TrashEntry findByG_LtCD_First(
			long groupId, Date createDate,
			com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public TrashEntry fetchByG_LtCD_First(
		long groupId, Date createDate,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator);

	/**
	 * Returns the last trash entry in the ordered set where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public TrashEntry findByG_LtCD_Last(
			long groupId, Date createDate,
			com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the last trash entry in the ordered set where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public TrashEntry fetchByG_LtCD_Last(
		long groupId, Date createDate,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator);

	/**
	 * Returns the trash entries before and after the current trash entry in the ordered set where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param entryId the primary key of the current trash entry
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	public TrashEntry[] findByG_LtCD_PrevAndNext(
			long entryId, long groupId, Date createDate,
			com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Removes all the trash entries where groupId = &#63; and createDate &lt; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 */
	public void removeByG_LtCD(long groupId, Date createDate);

	/**
	 * Returns the number of trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @return the number of matching trash entries
	 */
	public int countByG_LtCD(long groupId, Date createDate);

	/**
	 * Returns all the trash entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching trash entries
	 */
	public java.util.List<TrashEntry> findByG_CN(
		long groupId, long classNameId);

	/**
	 * Returns a range of all the trash entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of matching trash entries
	 */
	public java.util.List<TrashEntry> findByG_CN(
		long groupId, long classNameId, int start, int end);

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching trash entries
	 */
	public java.util.List<TrashEntry> findByG_CN(
		long groupId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	public java.util.List<TrashEntry> findByG_CN(
		long groupId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public TrashEntry findByG_CN_First(
			long groupId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public TrashEntry fetchByG_CN_First(
		long groupId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator);

	/**
	 * Returns the last trash entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public TrashEntry findByG_CN_Last(
			long groupId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the last trash entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public TrashEntry fetchByG_CN_Last(
		long groupId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator);

	/**
	 * Returns the trash entries before and after the current trash entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the primary key of the current trash entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	public TrashEntry[] findByG_CN_PrevAndNext(
			long entryId, long groupId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Removes all the trash entries where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	public void removeByG_CN(long groupId, long classNameId);

	/**
	 * Returns the number of trash entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching trash entries
	 */
	public int countByG_CN(long groupId, long classNameId);

	/**
	 * Returns all the trash entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching trash entries
	 */
	public java.util.List<TrashEntry> findByC_CN(
		long companyId, long classNameId);

	/**
	 * Returns a range of all the trash entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of matching trash entries
	 */
	public java.util.List<TrashEntry> findByC_CN(
		long companyId, long classNameId, int start, int end);

	/**
	 * Returns an ordered range of all the trash entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching trash entries
	 */
	public java.util.List<TrashEntry> findByC_CN(
		long companyId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the trash entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	public java.util.List<TrashEntry> findByC_CN(
		long companyId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first trash entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public TrashEntry findByC_CN_First(
			long companyId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first trash entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public TrashEntry fetchByC_CN_First(
		long companyId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator);

	/**
	 * Returns the last trash entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public TrashEntry findByC_CN_Last(
			long companyId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the last trash entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public TrashEntry fetchByC_CN_Last(
		long companyId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator);

	/**
	 * Returns the trash entries before and after the current trash entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the primary key of the current trash entry
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	public TrashEntry[] findByC_CN_PrevAndNext(
			long entryId, long companyId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Removes all the trash entries where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	public void removeByC_CN(long companyId, long classNameId);

	/**
	 * Returns the number of trash entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching trash entries
	 */
	public int countByC_CN(long companyId, long classNameId);

	/**
	 * Returns the trash entry where classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public TrashEntry findByCN_CPK(long classNameId, long classPK)
		throws NoSuchEntryException;

	/**
	 * Returns the trash entry where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public TrashEntry fetchByCN_CPK(long classNameId, long classPK);

	/**
	 * Returns the trash entry where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public TrashEntry fetchByCN_CPK(
		long classNameId, long classPK, boolean useFinderCache);

	/**
	 * Removes the trash entry where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the trash entry that was removed
	 */
	public TrashEntry removeByCN_CPK(long classNameId, long classPK)
		throws NoSuchEntryException;

	/**
	 * Returns the number of trash entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching trash entries
	 */
	public int countByCN_CPK(long classNameId, long classPK);

	/**
	 * Caches the trash entry in the entity cache if it is enabled.
	 *
	 * @param trashEntry the trash entry
	 */
	public void cacheResult(TrashEntry trashEntry);

	/**
	 * Caches the trash entries in the entity cache if it is enabled.
	 *
	 * @param trashEntries the trash entries
	 */
	public void cacheResult(java.util.List<TrashEntry> trashEntries);

	/**
	 * Creates a new trash entry with the primary key. Does not add the trash entry to the database.
	 *
	 * @param entryId the primary key for the new trash entry
	 * @return the new trash entry
	 */
	public TrashEntry create(long entryId);

	/**
	 * Removes the trash entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry that was removed
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	public TrashEntry remove(long entryId) throws NoSuchEntryException;

	public TrashEntry updateImpl(TrashEntry trashEntry);

	/**
	 * Returns the trash entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	public TrashEntry findByPrimaryKey(long entryId)
		throws NoSuchEntryException;

	/**
	 * Returns the trash entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry, or <code>null</code> if a trash entry with the primary key could not be found
	 */
	public TrashEntry fetchByPrimaryKey(long entryId);

	/**
	 * Returns all the trash entries.
	 *
	 * @return the trash entries
	 */
	public java.util.List<TrashEntry> findAll();

	/**
	 * Returns a range of all the trash entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of trash entries
	 */
	public java.util.List<TrashEntry> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the trash entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of trash entries
	 */
	public java.util.List<TrashEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the trash entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of trash entries
	 */
	public java.util.List<TrashEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the trash entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of trash entries.
	 *
	 * @return the number of trash entries
	 */
	public int countAll();

}