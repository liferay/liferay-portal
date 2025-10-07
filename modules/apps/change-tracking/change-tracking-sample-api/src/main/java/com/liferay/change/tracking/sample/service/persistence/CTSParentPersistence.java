/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service.persistence;

import com.liferay.change.tracking.sample.exception.NoSuchCTSParentException;
import com.liferay.change.tracking.sample.model.CTSParent;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the cts parent service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see CTSParentUtil
 * @generated
 */
@ProviderType
public interface CTSParentPersistence
	extends BasePersistence<CTSParent>, CTPersistence<CTSParent> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CTSParentUtil} to access the cts parent persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the cts parents where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching cts parents
	 */
	public java.util.List<CTSParent> findByCompanyId(long companyId);

	/**
	 * Returns a range of all the cts parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @return the range of matching cts parents
	 */
	public java.util.List<CTSParent> findByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the cts parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cts parents
	 */
	public java.util.List<CTSParent> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTSParent>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cts parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts parents
	 */
	public java.util.List<CTSParent> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTSParent>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cts parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts parent
	 * @throws NoSuchCTSParentException if a matching cts parent could not be found
	 */
	public CTSParent findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CTSParent>
				orderByComparator)
		throws NoSuchCTSParentException;

	/**
	 * Returns the first cts parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts parent, or <code>null</code> if a matching cts parent could not be found
	 */
	public CTSParent fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CTSParent>
			orderByComparator);

	/**
	 * Returns the last cts parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts parent
	 * @throws NoSuchCTSParentException if a matching cts parent could not be found
	 */
	public CTSParent findByCompanyId_Last(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CTSParent>
				orderByComparator)
		throws NoSuchCTSParentException;

	/**
	 * Returns the last cts parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts parent, or <code>null</code> if a matching cts parent could not be found
	 */
	public CTSParent fetchByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CTSParent>
			orderByComparator);

	/**
	 * Returns the cts parents before and after the current cts parent in the ordered set where companyId = &#63;.
	 *
	 * @param ctsParentId the primary key of the current cts parent
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cts parent
	 * @throws NoSuchCTSParentException if a cts parent with the primary key could not be found
	 */
	public CTSParent[] findByCompanyId_PrevAndNext(
			long ctsParentId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CTSParent>
				orderByComparator)
		throws NoSuchCTSParentException;

	/**
	 * Removes all the cts parents where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of cts parents where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cts parents
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns all the cts parents where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @return the matching cts parents
	 */
	public java.util.List<CTSParent> findByC_C(
		long companyId, long ctsGrandParentId);

	/**
	 * Returns a range of all the cts parents where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @return the range of matching cts parents
	 */
	public java.util.List<CTSParent> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end);

	/**
	 * Returns an ordered range of all the cts parents where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cts parents
	 */
	public java.util.List<CTSParent> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTSParent>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cts parents where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts parents
	 */
	public java.util.List<CTSParent> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTSParent>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cts parent in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts parent
	 * @throws NoSuchCTSParentException if a matching cts parent could not be found
	 */
	public CTSParent findByC_C_First(
			long companyId, long ctsGrandParentId,
			com.liferay.portal.kernel.util.OrderByComparator<CTSParent>
				orderByComparator)
		throws NoSuchCTSParentException;

	/**
	 * Returns the first cts parent in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts parent, or <code>null</code> if a matching cts parent could not be found
	 */
	public CTSParent fetchByC_C_First(
		long companyId, long ctsGrandParentId,
		com.liferay.portal.kernel.util.OrderByComparator<CTSParent>
			orderByComparator);

	/**
	 * Returns the last cts parent in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts parent
	 * @throws NoSuchCTSParentException if a matching cts parent could not be found
	 */
	public CTSParent findByC_C_Last(
			long companyId, long ctsGrandParentId,
			com.liferay.portal.kernel.util.OrderByComparator<CTSParent>
				orderByComparator)
		throws NoSuchCTSParentException;

	/**
	 * Returns the last cts parent in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts parent, or <code>null</code> if a matching cts parent could not be found
	 */
	public CTSParent fetchByC_C_Last(
		long companyId, long ctsGrandParentId,
		com.liferay.portal.kernel.util.OrderByComparator<CTSParent>
			orderByComparator);

	/**
	 * Returns the cts parents before and after the current cts parent in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param ctsParentId the primary key of the current cts parent
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cts parent
	 * @throws NoSuchCTSParentException if a cts parent with the primary key could not be found
	 */
	public CTSParent[] findByC_C_PrevAndNext(
			long ctsParentId, long companyId, long ctsGrandParentId,
			com.liferay.portal.kernel.util.OrderByComparator<CTSParent>
				orderByComparator)
		throws NoSuchCTSParentException;

	/**
	 * Removes all the cts parents where companyId = &#63; and ctsGrandParentId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 */
	public void removeByC_C(long companyId, long ctsGrandParentId);

	/**
	 * Returns the number of cts parents where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @return the number of matching cts parents
	 */
	public int countByC_C(long companyId, long ctsGrandParentId);

	/**
	 * Caches the cts parent in the entity cache if it is enabled.
	 *
	 * @param ctsParent the cts parent
	 */
	public void cacheResult(CTSParent ctsParent);

	/**
	 * Caches the cts parents in the entity cache if it is enabled.
	 *
	 * @param ctsParents the cts parents
	 */
	public void cacheResult(java.util.List<CTSParent> ctsParents);

	/**
	 * Creates a new cts parent with the primary key. Does not add the cts parent to the database.
	 *
	 * @param ctsParentId the primary key for the new cts parent
	 * @return the new cts parent
	 */
	public CTSParent create(long ctsParentId);

	/**
	 * Removes the cts parent with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctsParentId the primary key of the cts parent
	 * @return the cts parent that was removed
	 * @throws NoSuchCTSParentException if a cts parent with the primary key could not be found
	 */
	public CTSParent remove(long ctsParentId) throws NoSuchCTSParentException;

	public CTSParent updateImpl(CTSParent ctsParent);

	/**
	 * Returns the cts parent with the primary key or throws a <code>NoSuchCTSParentException</code> if it could not be found.
	 *
	 * @param ctsParentId the primary key of the cts parent
	 * @return the cts parent
	 * @throws NoSuchCTSParentException if a cts parent with the primary key could not be found
	 */
	public CTSParent findByPrimaryKey(long ctsParentId)
		throws NoSuchCTSParentException;

	/**
	 * Returns the cts parent with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctsParentId the primary key of the cts parent
	 * @return the cts parent, or <code>null</code> if a cts parent with the primary key could not be found
	 */
	public CTSParent fetchByPrimaryKey(long ctsParentId);

	/**
	 * Returns all the cts parents.
	 *
	 * @return the cts parents
	 */
	public java.util.List<CTSParent> findAll();

	/**
	 * Returns a range of all the cts parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @return the range of cts parents
	 */
	public java.util.List<CTSParent> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the cts parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cts parents
	 */
	public java.util.List<CTSParent> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTSParent>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cts parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cts parents
	 */
	public java.util.List<CTSParent> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTSParent>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the cts parents from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of cts parents.
	 *
	 * @return the number of cts parents
	 */
	public int countAll();

}