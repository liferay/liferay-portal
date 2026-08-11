/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchClobEntryException;
import com.liferay.portal.tools.service.builder.test.model.ClobEntry;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the clob entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ClobEntryUtil
 * @generated
 */
@ProviderType
public interface ClobEntryPersistence extends BasePersistence<ClobEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ClobEntryUtil} to access the clob entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Creates a new clob entry with the primary key. Does not add the clob entry to the database.
	 *
	 * @param clobEntryId the primary key for the new clob entry
	 * @return the new clob entry
	 */
	public ClobEntry create(long clobEntryId);

	/**
	 * Removes the clob entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param clobEntryId the primary key of the clob entry
	 * @return the clob entry that was removed
	 * @throws NoSuchClobEntryException if a clob entry with the primary key could not be found
	 */
	public ClobEntry remove(long clobEntryId) throws NoSuchClobEntryException;

	public ClobEntry updateImpl(ClobEntry clobEntry);

	/**
	 * Returns the clob entry with the primary key or throws a <code>NoSuchClobEntryException</code> if it could not be found.
	 *
	 * @param clobEntryId the primary key of the clob entry
	 * @return the clob entry
	 * @throws NoSuchClobEntryException if a clob entry with the primary key could not be found
	 */
	public ClobEntry findByPrimaryKey(long clobEntryId)
		throws NoSuchClobEntryException;

	/**
	 * Returns the clob entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param clobEntryId the primary key of the clob entry
	 * @return the clob entry, or <code>null</code> if a clob entry with the primary key could not be found
	 */
	public ClobEntry fetchByPrimaryKey(long clobEntryId);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1124046226