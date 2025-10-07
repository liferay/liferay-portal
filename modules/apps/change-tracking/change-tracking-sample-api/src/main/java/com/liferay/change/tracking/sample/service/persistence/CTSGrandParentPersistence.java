/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service.persistence;

import com.liferay.change.tracking.sample.exception.NoSuchCTSGrandParentException;
import com.liferay.change.tracking.sample.model.CTSGrandParent;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the cts grand parent service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see CTSGrandParentUtil
 * @generated
 */
@ProviderType
public interface CTSGrandParentPersistence
	extends BasePersistence<CTSGrandParent> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CTSGrandParentUtil} to access the cts grand parent persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the cts grand parents where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching cts grand parents
	 */
	public java.util.List<CTSGrandParent> findByCompanyId(long companyId);

	/**
	 * Returns a range of all the cts grand parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @return the range of matching cts grand parents
	 */
	public java.util.List<CTSGrandParent> findByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the cts grand parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cts grand parents
	 */
	public java.util.List<CTSGrandParent> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTSGrandParent>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cts grand parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts grand parents
	 */
	public java.util.List<CTSGrandParent> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTSGrandParent>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cts grand parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts grand parent
	 * @throws NoSuchCTSGrandParentException if a matching cts grand parent could not be found
	 */
	public CTSGrandParent findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CTSGrandParent>
				orderByComparator)
		throws NoSuchCTSGrandParentException;

	/**
	 * Returns the first cts grand parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts grand parent, or <code>null</code> if a matching cts grand parent could not be found
	 */
	public CTSGrandParent fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CTSGrandParent>
			orderByComparator);

	/**
	 * Returns the last cts grand parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts grand parent
	 * @throws NoSuchCTSGrandParentException if a matching cts grand parent could not be found
	 */
	public CTSGrandParent findByCompanyId_Last(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CTSGrandParent>
				orderByComparator)
		throws NoSuchCTSGrandParentException;

	/**
	 * Returns the last cts grand parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts grand parent, or <code>null</code> if a matching cts grand parent could not be found
	 */
	public CTSGrandParent fetchByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CTSGrandParent>
			orderByComparator);

	/**
	 * Returns the cts grand parents before and after the current cts grand parent in the ordered set where companyId = &#63;.
	 *
	 * @param ctsGrandParentId the primary key of the current cts grand parent
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cts grand parent
	 * @throws NoSuchCTSGrandParentException if a cts grand parent with the primary key could not be found
	 */
	public CTSGrandParent[] findByCompanyId_PrevAndNext(
			long ctsGrandParentId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CTSGrandParent>
				orderByComparator)
		throws NoSuchCTSGrandParentException;

	/**
	 * Removes all the cts grand parents where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of cts grand parents where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cts grand parents
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Caches the cts grand parent in the entity cache if it is enabled.
	 *
	 * @param ctsGrandParent the cts grand parent
	 */
	public void cacheResult(CTSGrandParent ctsGrandParent);

	/**
	 * Caches the cts grand parents in the entity cache if it is enabled.
	 *
	 * @param ctsGrandParents the cts grand parents
	 */
	public void cacheResult(java.util.List<CTSGrandParent> ctsGrandParents);

	/**
	 * Creates a new cts grand parent with the primary key. Does not add the cts grand parent to the database.
	 *
	 * @param ctsGrandParentId the primary key for the new cts grand parent
	 * @return the new cts grand parent
	 */
	public CTSGrandParent create(long ctsGrandParentId);

	/**
	 * Removes the cts grand parent with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctsGrandParentId the primary key of the cts grand parent
	 * @return the cts grand parent that was removed
	 * @throws NoSuchCTSGrandParentException if a cts grand parent with the primary key could not be found
	 */
	public CTSGrandParent remove(long ctsGrandParentId)
		throws NoSuchCTSGrandParentException;

	public CTSGrandParent updateImpl(CTSGrandParent ctsGrandParent);

	/**
	 * Returns the cts grand parent with the primary key or throws a <code>NoSuchCTSGrandParentException</code> if it could not be found.
	 *
	 * @param ctsGrandParentId the primary key of the cts grand parent
	 * @return the cts grand parent
	 * @throws NoSuchCTSGrandParentException if a cts grand parent with the primary key could not be found
	 */
	public CTSGrandParent findByPrimaryKey(long ctsGrandParentId)
		throws NoSuchCTSGrandParentException;

	/**
	 * Returns the cts grand parent with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctsGrandParentId the primary key of the cts grand parent
	 * @return the cts grand parent, or <code>null</code> if a cts grand parent with the primary key could not be found
	 */
	public CTSGrandParent fetchByPrimaryKey(long ctsGrandParentId);

	/**
	 * Returns all the cts grand parents.
	 *
	 * @return the cts grand parents
	 */
	public java.util.List<CTSGrandParent> findAll();

	/**
	 * Returns a range of all the cts grand parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @return the range of cts grand parents
	 */
	public java.util.List<CTSGrandParent> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the cts grand parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cts grand parents
	 */
	public java.util.List<CTSGrandParent> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTSGrandParent>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cts grand parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cts grand parents
	 */
	public java.util.List<CTSGrandParent> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTSGrandParent>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the cts grand parents from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of cts grand parents.
	 *
	 * @return the number of cts grand parents
	 */
	public int countAll();

}