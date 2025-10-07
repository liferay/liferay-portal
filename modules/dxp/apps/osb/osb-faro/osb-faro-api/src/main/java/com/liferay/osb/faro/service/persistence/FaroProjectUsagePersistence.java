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
	 * Returns the faro project usage where faroProjectId = &#63; and usageTime = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the matching faro project usage, or <code>null</code> if a matching faro project usage could not be found
	 */
	public FaroProjectUsage fetchByF_U(long faroProjectId, long usageTime);

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
	 * Caches the faro project usage in the entity cache if it is enabled.
	 *
	 * @param faroProjectUsage the faro project usage
	 */
	public void cacheResult(FaroProjectUsage faroProjectUsage);

	/**
	 * Caches the faro project usages in the entity cache if it is enabled.
	 *
	 * @param faroProjectUsages the faro project usages
	 */
	public void cacheResult(java.util.List<FaroProjectUsage> faroProjectUsages);

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
	 * Returns all the faro project usages.
	 *
	 * @return the faro project usages
	 */
	public java.util.List<FaroProjectUsage> findAll();

	/**
	 * Returns a range of all the faro project usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroProjectUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro project usages
	 * @param end the upper bound of the range of faro project usages (not inclusive)
	 * @return the range of faro project usages
	 */
	public java.util.List<FaroProjectUsage> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the faro project usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroProjectUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro project usages
	 * @param end the upper bound of the range of faro project usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of faro project usages
	 */
	public java.util.List<FaroProjectUsage> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FaroProjectUsage>
			orderByComparator);

	/**
	 * Returns an ordered range of all the faro project usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroProjectUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro project usages
	 * @param end the upper bound of the range of faro project usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of faro project usages
	 */
	public java.util.List<FaroProjectUsage> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FaroProjectUsage>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the faro project usages from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of faro project usages.
	 *
	 * @return the number of faro project usages
	 */
	public int countAll();

}