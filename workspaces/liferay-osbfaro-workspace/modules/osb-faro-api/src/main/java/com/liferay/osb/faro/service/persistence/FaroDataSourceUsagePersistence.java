/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence;

import com.liferay.osb.faro.exception.NoSuchFaroDataSourceUsageException;
import com.liferay.osb.faro.model.FaroDataSourceUsage;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the faro data source usage service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matthew Kong
 * @see FaroDataSourceUsageUtil
 * @generated
 */
@ProviderType
public interface FaroDataSourceUsagePersistence
	extends BasePersistence<FaroDataSourceUsage> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link FaroDataSourceUsageUtil} to access the faro data source usage persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns the faro data source usage where dataSourceId = &#63; and faroProjectId = &#63; and usageTime = &#63; or throws a <code>NoSuchFaroDataSourceUsageException</code> if it could not be found.
	 *
	 * @param dataSourceId the data source ID
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the matching faro data source usage
	 * @throws NoSuchFaroDataSourceUsageException if a matching faro data source usage could not be found
	 */
	public FaroDataSourceUsage findByF_D_U(
			long dataSourceId, long faroProjectId, long usageTime)
		throws NoSuchFaroDataSourceUsageException;

	/**
	 * Returns the faro data source usage where dataSourceId = &#63; and faroProjectId = &#63; and usageTime = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param dataSourceId the data source ID
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro data source usage, or <code>null</code> if a matching faro data source usage could not be found
	 */
	public FaroDataSourceUsage fetchByF_D_U(
		long dataSourceId, long faroProjectId, long usageTime,
		boolean useFinderCache);

	/**
	 * Removes the faro data source usage where dataSourceId = &#63; and faroProjectId = &#63; and usageTime = &#63; from the database.
	 *
	 * @param dataSourceId the data source ID
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the faro data source usage that was removed
	 */
	public FaroDataSourceUsage removeByF_D_U(
			long dataSourceId, long faroProjectId, long usageTime)
		throws NoSuchFaroDataSourceUsageException;

	/**
	 * Returns the number of faro data source usages where dataSourceId = &#63; and faroProjectId = &#63; and usageTime = &#63;.
	 *
	 * @param dataSourceId the data source ID
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the number of matching faro data source usages
	 */
	public int countByF_D_U(
		long dataSourceId, long faroProjectId, long usageTime);

	/**
	 * Creates a new faro data source usage with the primary key. Does not add the faro data source usage to the database.
	 *
	 * @param faroDataSourceUsageId the primary key for the new faro data source usage
	 * @return the new faro data source usage
	 */
	public FaroDataSourceUsage create(long faroDataSourceUsageId);

	/**
	 * Removes the faro data source usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param faroDataSourceUsageId the primary key of the faro data source usage
	 * @return the faro data source usage that was removed
	 * @throws NoSuchFaroDataSourceUsageException if a faro data source usage with the primary key could not be found
	 */
	public FaroDataSourceUsage remove(long faroDataSourceUsageId)
		throws NoSuchFaroDataSourceUsageException;

	public FaroDataSourceUsage updateImpl(
		FaroDataSourceUsage faroDataSourceUsage);

	/**
	 * Returns the faro data source usage with the primary key or throws a <code>NoSuchFaroDataSourceUsageException</code> if it could not be found.
	 *
	 * @param faroDataSourceUsageId the primary key of the faro data source usage
	 * @return the faro data source usage
	 * @throws NoSuchFaroDataSourceUsageException if a faro data source usage with the primary key could not be found
	 */
	public FaroDataSourceUsage findByPrimaryKey(long faroDataSourceUsageId)
		throws NoSuchFaroDataSourceUsageException;

	/**
	 * Returns the faro data source usage with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param faroDataSourceUsageId the primary key of the faro data source usage
	 * @return the faro data source usage, or <code>null</code> if a faro data source usage with the primary key could not be found
	 */
	public FaroDataSourceUsage fetchByPrimaryKey(long faroDataSourceUsageId);

	/**
	 * Returns the faro data source usage where dataSourceId = &#63; and faroProjectId = &#63; and usageTime = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param dataSourceId the data source ID
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the matching faro data source usage, or <code>null</code> if a matching faro data source usage could not be found
	 */
	public default FaroDataSourceUsage fetchByF_D_U(
		long dataSourceId, long faroProjectId, long usageTime) {

		return fetchByF_D_U(dataSourceId, faroProjectId, usageTime, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1803932342