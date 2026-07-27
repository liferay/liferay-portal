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
	 * Returns the faro data source usage where dataSourceId = &#63; and faroProjectId = &#63; and usageTime = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param dataSourceId the data source ID
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the matching faro data source usage, or <code>null</code> if a matching faro data source usage could not be found
	 */
	public FaroDataSourceUsage fetchByF_D_U(
		long dataSourceId, long faroProjectId, long usageTime);

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
	 * Caches the faro data source usage in the entity cache if it is enabled.
	 *
	 * @param faroDataSourceUsage the faro data source usage
	 */
	public void cacheResult(FaroDataSourceUsage faroDataSourceUsage);

	/**
	 * Caches the faro data source usages in the entity cache if it is enabled.
	 *
	 * @param faroDataSourceUsages the faro data source usages
	 */
	public void cacheResult(
		java.util.List<FaroDataSourceUsage> faroDataSourceUsages);

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
	 * Returns all the faro data source usages.
	 *
	 * @return the faro data source usages
	 */
	public java.util.List<FaroDataSourceUsage> findAll();

	/**
	 * Returns a range of all the faro data source usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroDataSourceUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro data source usages
	 * @param end the upper bound of the range of faro data source usages (not inclusive)
	 * @return the range of faro data source usages
	 */
	public java.util.List<FaroDataSourceUsage> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the faro data source usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroDataSourceUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro data source usages
	 * @param end the upper bound of the range of faro data source usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of faro data source usages
	 */
	public java.util.List<FaroDataSourceUsage> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FaroDataSourceUsage>
			orderByComparator);

	/**
	 * Returns an ordered range of all the faro data source usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroDataSourceUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro data source usages
	 * @param end the upper bound of the range of faro data source usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of faro data source usages
	 */
	public java.util.List<FaroDataSourceUsage> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FaroDataSourceUsage>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the faro data source usages from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of faro data source usages.
	 *
	 * @return the number of faro data source usages
	 */
	public int countAll();

}
// LIFERAY-SERVICE-BUILDER-HASH:2136718469