/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service.persistence;

import com.liferay.change.tracking.sample.exception.NoSuchCTSChildException;
import com.liferay.change.tracking.sample.model.CTSChild;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the cts child service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see CTSChildUtil
 * @generated
 */
@ProviderType
public interface CTSChildPersistence
	extends BasePersistence<CTSChild>, CTPersistence<CTSChild> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CTSChildUtil} to access the cts child persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the cts childs where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching cts childs
	 */
	public java.util.List<CTSChild> findByCompanyId(long companyId);

	/**
	 * Returns a range of all the cts childs where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @return the range of matching cts childs
	 */
	public java.util.List<CTSChild> findByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cts childs
	 */
	public java.util.List<CTSChild> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts childs
	 */
	public java.util.List<CTSChild> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	public CTSChild findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
				orderByComparator)
		throws NoSuchCTSChildException;

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	public CTSChild fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
			orderByComparator);

	/**
	 * Returns the last cts child in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	public CTSChild findByCompanyId_Last(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
				orderByComparator)
		throws NoSuchCTSChildException;

	/**
	 * Returns the last cts child in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	public CTSChild fetchByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
			orderByComparator);

	/**
	 * Returns the cts childs before and after the current cts child in the ordered set where companyId = &#63;.
	 *
	 * @param ctsChildId the primary key of the current cts child
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cts child
	 * @throws NoSuchCTSChildException if a cts child with the primary key could not be found
	 */
	public CTSChild[] findByCompanyId_PrevAndNext(
			long ctsChildId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
				orderByComparator)
		throws NoSuchCTSChildException;

	/**
	 * Removes all the cts childs where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of cts childs where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cts childs
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns all the cts childs where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @return the matching cts childs
	 */
	public java.util.List<CTSChild> findByC_C(
		long companyId, long ctsGrandParentId);

	/**
	 * Returns a range of all the cts childs where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @return the range of matching cts childs
	 */
	public java.util.List<CTSChild> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end);

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cts childs
	 */
	public java.util.List<CTSChild> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts childs
	 */
	public java.util.List<CTSChild> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	public CTSChild findByC_C_First(
			long companyId, long ctsGrandParentId,
			com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
				orderByComparator)
		throws NoSuchCTSChildException;

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	public CTSChild fetchByC_C_First(
		long companyId, long ctsGrandParentId,
		com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
			orderByComparator);

	/**
	 * Returns the last cts child in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	public CTSChild findByC_C_Last(
			long companyId, long ctsGrandParentId,
			com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
				orderByComparator)
		throws NoSuchCTSChildException;

	/**
	 * Returns the last cts child in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	public CTSChild fetchByC_C_Last(
		long companyId, long ctsGrandParentId,
		com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
			orderByComparator);

	/**
	 * Returns the cts childs before and after the current cts child in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param ctsChildId the primary key of the current cts child
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cts child
	 * @throws NoSuchCTSChildException if a cts child with the primary key could not be found
	 */
	public CTSChild[] findByC_C_PrevAndNext(
			long ctsChildId, long companyId, long ctsGrandParentId,
			com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
				orderByComparator)
		throws NoSuchCTSChildException;

	/**
	 * Removes all the cts childs where companyId = &#63; and ctsGrandParentId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 */
	public void removeByC_C(long companyId, long ctsGrandParentId);

	/**
	 * Returns the number of cts childs where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @return the number of matching cts childs
	 */
	public int countByC_C(long companyId, long ctsGrandParentId);

	/**
	 * Returns all the cts childs where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @return the matching cts childs
	 */
	public java.util.List<CTSChild> findByC_P(
		long companyId, long parentCTSChildId);

	/**
	 * Returns a range of all the cts childs where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @return the range of matching cts childs
	 */
	public java.util.List<CTSChild> findByC_P(
		long companyId, long parentCTSChildId, int start, int end);

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cts childs
	 */
	public java.util.List<CTSChild> findByC_P(
		long companyId, long parentCTSChildId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts childs
	 */
	public java.util.List<CTSChild> findByC_P(
		long companyId, long parentCTSChildId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	public CTSChild findByC_P_First(
			long companyId, long parentCTSChildId,
			com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
				orderByComparator)
		throws NoSuchCTSChildException;

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	public CTSChild fetchByC_P_First(
		long companyId, long parentCTSChildId,
		com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
			orderByComparator);

	/**
	 * Returns the last cts child in the ordered set where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	public CTSChild findByC_P_Last(
			long companyId, long parentCTSChildId,
			com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
				orderByComparator)
		throws NoSuchCTSChildException;

	/**
	 * Returns the last cts child in the ordered set where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	public CTSChild fetchByC_P_Last(
		long companyId, long parentCTSChildId,
		com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
			orderByComparator);

	/**
	 * Returns the cts childs before and after the current cts child in the ordered set where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param ctsChildId the primary key of the current cts child
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cts child
	 * @throws NoSuchCTSChildException if a cts child with the primary key could not be found
	 */
	public CTSChild[] findByC_P_PrevAndNext(
			long ctsChildId, long companyId, long parentCTSChildId,
			com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
				orderByComparator)
		throws NoSuchCTSChildException;

	/**
	 * Removes all the cts childs where companyId = &#63; and parentCTSChildId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 */
	public void removeByC_P(long companyId, long parentCTSChildId);

	/**
	 * Returns the number of cts childs where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @return the number of matching cts childs
	 */
	public int countByC_P(long companyId, long parentCTSChildId);

	/**
	 * Caches the cts child in the entity cache if it is enabled.
	 *
	 * @param ctsChild the cts child
	 */
	public void cacheResult(CTSChild ctsChild);

	/**
	 * Caches the cts childs in the entity cache if it is enabled.
	 *
	 * @param ctsChilds the cts childs
	 */
	public void cacheResult(java.util.List<CTSChild> ctsChilds);

	/**
	 * Creates a new cts child with the primary key. Does not add the cts child to the database.
	 *
	 * @param ctsChildId the primary key for the new cts child
	 * @return the new cts child
	 */
	public CTSChild create(long ctsChildId);

	/**
	 * Removes the cts child with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctsChildId the primary key of the cts child
	 * @return the cts child that was removed
	 * @throws NoSuchCTSChildException if a cts child with the primary key could not be found
	 */
	public CTSChild remove(long ctsChildId) throws NoSuchCTSChildException;

	public CTSChild updateImpl(CTSChild ctsChild);

	/**
	 * Returns the cts child with the primary key or throws a <code>NoSuchCTSChildException</code> if it could not be found.
	 *
	 * @param ctsChildId the primary key of the cts child
	 * @return the cts child
	 * @throws NoSuchCTSChildException if a cts child with the primary key could not be found
	 */
	public CTSChild findByPrimaryKey(long ctsChildId)
		throws NoSuchCTSChildException;

	/**
	 * Returns the cts child with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctsChildId the primary key of the cts child
	 * @return the cts child, or <code>null</code> if a cts child with the primary key could not be found
	 */
	public CTSChild fetchByPrimaryKey(long ctsChildId);

	/**
	 * Returns all the cts childs.
	 *
	 * @return the cts childs
	 */
	public java.util.List<CTSChild> findAll();

	/**
	 * Returns a range of all the cts childs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @return the range of cts childs
	 */
	public java.util.List<CTSChild> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the cts childs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cts childs
	 */
	public java.util.List<CTSChild> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cts childs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cts childs
	 */
	public java.util.List<CTSChild> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTSChild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the cts childs from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of cts childs.
	 *
	 * @return the number of cts childs
	 */
	public int countAll();

}