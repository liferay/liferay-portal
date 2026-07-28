/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchCompoundPKEntryException;
import com.liferay.portal.tools.service.builder.test.model.CompoundPKEntry;

import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the compound pk entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see CompoundPKEntryUtil
 * @generated
 */
@ProviderType
public interface CompoundPKEntryPersistence
	extends BasePersistence<CompoundPKEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CompoundPKEntryUtil} to access the compound pk entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Creates a new compound pk entry with the primary key. Does not add the compound pk entry to the database.
	 *
	 * @param compoundPKEntryPK the primary key for the new compound pk entry
	 * @return the new compound pk entry
	 */
	public CompoundPKEntry create(CompoundPKEntryPK compoundPKEntryPK);

	/**
	 * Removes the compound pk entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param compoundPKEntryPK the primary key of the compound pk entry
	 * @return the compound pk entry that was removed
	 * @throws NoSuchCompoundPKEntryException if a compound pk entry with the primary key could not be found
	 */
	public CompoundPKEntry remove(CompoundPKEntryPK compoundPKEntryPK)
		throws NoSuchCompoundPKEntryException;

	public CompoundPKEntry updateImpl(CompoundPKEntry compoundPKEntry);

	/**
	 * Returns the compound pk entry with the primary key or throws a <code>NoSuchCompoundPKEntryException</code> if it could not be found.
	 *
	 * @param compoundPKEntryPK the primary key of the compound pk entry
	 * @return the compound pk entry
	 * @throws NoSuchCompoundPKEntryException if a compound pk entry with the primary key could not be found
	 */
	public CompoundPKEntry findByPrimaryKey(CompoundPKEntryPK compoundPKEntryPK)
		throws NoSuchCompoundPKEntryException;

	/**
	 * Returns the compound pk entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param compoundPKEntryPK the primary key of the compound pk entry
	 * @return the compound pk entry, or <code>null</code> if a compound pk entry with the primary key could not be found
	 */
	public CompoundPKEntry fetchByPrimaryKey(
		CompoundPKEntryPK compoundPKEntryPK);

	public Set<String> getCompoundPKColumnNames();

}
// LIFERAY-SERVICE-BUILDER-HASH:-1607447672