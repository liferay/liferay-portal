/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDateEntryException;
import com.liferay.portal.tools.service.builder.test.model.DateEntry;

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

}
// LIFERAY-SERVICE-BUILDER-HASH:-99503214