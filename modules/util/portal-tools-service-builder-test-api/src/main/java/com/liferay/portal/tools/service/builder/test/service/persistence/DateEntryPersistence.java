/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDateEntryException;
import com.liferay.portal.tools.service.builder.test.model.DateEntry;

import java.util.Date;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the date entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DateEntryUtil
 * @generated
 */
@ProviderType
public interface DateEntryPersistence extends BasePersistence<DateEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link DateEntryUtil} to access the date entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the date entries where snapshotDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.DateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param snapshotDate the snapshot date
	 * @param start the lower bound of the range of date entries
	 * @param end the upper bound of the range of date entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching date entries
	 */
	public java.util.List<DateEntry> findBySnapshotDate(
		Date snapshotDate, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DateEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first date entry in the ordered set where snapshotDate = &#63;.
	 *
	 * @param snapshotDate the snapshot date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching date entry
	 * @throws NoSuchDateEntryException if a matching date entry could not be found
	 */
	public DateEntry findBySnapshotDate_First(
			Date snapshotDate,
			com.liferay.portal.kernel.util.OrderByComparator<DateEntry>
				orderByComparator)
		throws NoSuchDateEntryException;

	/**
	 * Returns the first date entry in the ordered set where snapshotDate = &#63;.
	 *
	 * @param snapshotDate the snapshot date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching date entry, or <code>null</code> if a matching date entry could not be found
	 */
	public DateEntry fetchBySnapshotDate_First(
		Date snapshotDate,
		com.liferay.portal.kernel.util.OrderByComparator<DateEntry>
			orderByComparator);

	/**
	 * Removes all the date entries where snapshotDate = &#63; from the database.
	 *
	 * @param snapshotDate the snapshot date
	 */
	public void removeBySnapshotDate(Date snapshotDate);

	/**
	 * Returns the number of date entries where snapshotDate = &#63;.
	 *
	 * @param snapshotDate the snapshot date
	 * @return the number of matching date entries
	 */
	public int countBySnapshotDate(Date snapshotDate);

	/**
	 * Returns the date entry where companyId = &#63; and snapshotDate = &#63; or throws a <code>NoSuchDateEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param snapshotDate the snapshot date
	 * @return the matching date entry
	 * @throws NoSuchDateEntryException if a matching date entry could not be found
	 */
	public DateEntry findByC_S(long companyId, Date snapshotDate)
		throws NoSuchDateEntryException;

	/**
	 * Returns the date entry where companyId = &#63; and snapshotDate = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param snapshotDate the snapshot date
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching date entry, or <code>null</code> if a matching date entry could not be found
	 */
	public DateEntry fetchByC_S(
		long companyId, Date snapshotDate, boolean useFinderCache);

	/**
	 * Removes the date entry where companyId = &#63; and snapshotDate = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param snapshotDate the snapshot date
	 * @return the date entry that was removed
	 */
	public DateEntry removeByC_S(long companyId, Date snapshotDate)
		throws NoSuchDateEntryException;

	/**
	 * Returns the number of date entries where companyId = &#63; and snapshotDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param snapshotDate the snapshot date
	 * @return the number of matching date entries
	 */
	public int countByC_S(long companyId, Date snapshotDate);

	/**
	 * Creates a new date entry with the primary key. Does not add the date entry to the database.
	 *
	 * @param dateEntryId the primary key for the new date entry
	 * @return the new date entry
	 */
	public DateEntry create(long dateEntryId);

	/**
	 * Removes the date entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dateEntryId the primary key of the date entry
	 * @return the date entry that was removed
	 * @throws NoSuchDateEntryException if a date entry with the primary key could not be found
	 */
	public DateEntry remove(long dateEntryId) throws NoSuchDateEntryException;

	public DateEntry updateImpl(DateEntry dateEntry);

	/**
	 * Returns the date entry with the primary key or throws a <code>NoSuchDateEntryException</code> if it could not be found.
	 *
	 * @param dateEntryId the primary key of the date entry
	 * @return the date entry
	 * @throws NoSuchDateEntryException if a date entry with the primary key could not be found
	 */
	public DateEntry findByPrimaryKey(long dateEntryId)
		throws NoSuchDateEntryException;

	/**
	 * Returns the date entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dateEntryId the primary key of the date entry
	 * @return the date entry, or <code>null</code> if a date entry with the primary key could not be found
	 */
	public DateEntry fetchByPrimaryKey(long dateEntryId);

	/**
	 * Returns the date entry where companyId = &#63; and snapshotDate = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param snapshotDate the snapshot date
	 * @return the matching date entry, or <code>null</code> if a matching date entry could not be found
	 */
	public default DateEntry fetchByC_S(long companyId, Date snapshotDate) {
		return fetchByC_S(companyId, snapshotDate, true);
	}

	/**
	 * Returns all the date entries where snapshotDate = &#63;.
	 *
	 * @param snapshotDate the snapshot date
	 * @return the matching date entries
	 */
	public default java.util.List<DateEntry> findBySnapshotDate(
		Date snapshotDate) {

		return findBySnapshotDate(
			snapshotDate, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the date entries where snapshotDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.DateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param snapshotDate the snapshot date
	 * @param start the lower bound of the range of date entries
	 * @param end the upper bound of the range of date entries (not inclusive)
	 * @return the range of matching date entries
	 */
	public default java.util.List<DateEntry> findBySnapshotDate(
		Date snapshotDate, int start, int end) {

		return findBySnapshotDate(snapshotDate, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the date entries where snapshotDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.DateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param snapshotDate the snapshot date
	 * @param start the lower bound of the range of date entries
	 * @param end the upper bound of the range of date entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching date entries
	 */
	public default java.util.List<DateEntry> findBySnapshotDate(
		Date snapshotDate, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DateEntry>
			orderByComparator) {

		return findBySnapshotDate(
			snapshotDate, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1842348354