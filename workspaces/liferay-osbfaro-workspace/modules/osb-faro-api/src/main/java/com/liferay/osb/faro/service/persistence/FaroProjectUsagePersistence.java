/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence;

import com.liferay.osb.faro.exception.NoSuchFaroProjectUsageException;
import com.liferay.osb.faro.model.FaroProjectUsage;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the faro project usage service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matthew Kong
 * @see FaroProjectUsageUtil
 * @generated
 */
@ProviderType
public interface FaroProjectUsagePersistence
	extends BasePersistence<FaroProjectUsage> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link FaroProjectUsageUtil} to access the faro project usage persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns the faro project usage where faroProjectId = &#63; and usageTime = &#63; or throws a <code>NoSuchFaroProjectUsageException</code> if it could not be found.
	 *
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the matching faro project usage
	 * @throws NoSuchFaroProjectUsageException if a matching faro project usage could not be found
	 */
	public FaroProjectUsage findByF_U(long faroProjectId, long usageTime)
		throws NoSuchFaroProjectUsageException;

	/**
	 * Returns the faro project usage where faroProjectId = &#63; and usageTime = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro project usage, or <code>null</code> if a matching faro project usage could not be found
	 */
	public FaroProjectUsage fetchByF_U(
		long faroProjectId, long usageTime, boolean useFinderCache);

	/**
	 * Removes the faro project usage where faroProjectId = &#63; and usageTime = &#63; from the database.
	 *
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the faro project usage that was removed
	 */
	public FaroProjectUsage removeByF_U(long faroProjectId, long usageTime)
		throws NoSuchFaroProjectUsageException;

	/**
	 * Returns the number of faro project usages where faroProjectId = &#63; and usageTime = &#63;.
	 *
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the number of matching faro project usages
	 */
	public int countByF_U(long faroProjectId, long usageTime);

	/**
	 * Creates a new faro project usage with the primary key. Does not add the faro project usage to the database.
	 *
	 * @param faroProjectUsageId the primary key for the new faro project usage
	 * @return the new faro project usage
	 */
	public FaroProjectUsage create(long faroProjectUsageId);

	/**
	 * Removes the faro project usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param faroProjectUsageId the primary key of the faro project usage
	 * @return the faro project usage that was removed
	 * @throws NoSuchFaroProjectUsageException if a faro project usage with the primary key could not be found
	 */
	public FaroProjectUsage remove(long faroProjectUsageId)
		throws NoSuchFaroProjectUsageException;

	public FaroProjectUsage updateImpl(FaroProjectUsage faroProjectUsage);

	/**
	 * Returns the faro project usage with the primary key or throws a <code>NoSuchFaroProjectUsageException</code> if it could not be found.
	 *
	 * @param faroProjectUsageId the primary key of the faro project usage
	 * @return the faro project usage
	 * @throws NoSuchFaroProjectUsageException if a faro project usage with the primary key could not be found
	 */
	public FaroProjectUsage findByPrimaryKey(long faroProjectUsageId)
		throws NoSuchFaroProjectUsageException;

	/**
	 * Returns the faro project usage with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param faroProjectUsageId the primary key of the faro project usage
	 * @return the faro project usage, or <code>null</code> if a faro project usage with the primary key could not be found
	 */
	public FaroProjectUsage fetchByPrimaryKey(long faroProjectUsageId);

	/**
	 * Returns the faro project usage where faroProjectId = &#63; and usageTime = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the matching faro project usage, or <code>null</code> if a matching faro project usage could not be found
	 */
	public default FaroProjectUsage fetchByF_U(
		long faroProjectId, long usageTime) {

		return fetchByF_U(faroProjectId, usageTime, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2085247603