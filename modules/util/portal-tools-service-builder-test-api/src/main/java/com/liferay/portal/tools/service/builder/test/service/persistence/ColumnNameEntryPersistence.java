/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchColumnNameEntryException;
import com.liferay.portal.tools.service.builder.test.model.ColumnNameEntry;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the column name entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ColumnNameEntryUtil
 * @generated
 */
@ProviderType
public interface ColumnNameEntryPersistence
	extends BasePersistence<ColumnNameEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ColumnNameEntryUtil} to access the column name entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Creates a new column name entry with the primary key. Does not add the column name entry to the database.
	 *
	 * @param columnNameEntryId the primary key for the new column name entry
	 * @return the new column name entry
	 */
	public ColumnNameEntry create(long columnNameEntryId);

	/**
	 * Removes the column name entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param columnNameEntryId the primary key of the column name entry
	 * @return the column name entry that was removed
	 * @throws NoSuchColumnNameEntryException if a column name entry with the primary key could not be found
	 */
	public ColumnNameEntry remove(long columnNameEntryId)
		throws NoSuchColumnNameEntryException;

	public ColumnNameEntry updateImpl(ColumnNameEntry columnNameEntry);

	/**
	 * Returns the column name entry with the primary key or throws a <code>NoSuchColumnNameEntryException</code> if it could not be found.
	 *
	 * @param columnNameEntryId the primary key of the column name entry
	 * @return the column name entry
	 * @throws NoSuchColumnNameEntryException if a column name entry with the primary key could not be found
	 */
	public ColumnNameEntry findByPrimaryKey(long columnNameEntryId)
		throws NoSuchColumnNameEntryException;

	/**
	 * Returns the column name entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param columnNameEntryId the primary key of the column name entry
	 * @return the column name entry, or <code>null</code> if a column name entry with the primary key could not be found
	 */
	public ColumnNameEntry fetchByPrimaryKey(long columnNameEntryId);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1757298648