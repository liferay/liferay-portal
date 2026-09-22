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
	 * Returns an ordered range of all the compound pk entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.CompoundPKEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of compound pk entries
	 * @param end the upper bound of the range of compound pk entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching compound pk entries
	 */
	public java.util.List<CompoundPKEntry> findByC_CN(
		long companyId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CompoundPKEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first compound pk entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching compound pk entry
	 * @throws NoSuchCompoundPKEntryException if a matching compound pk entry could not be found
	 */
	public CompoundPKEntry findByC_CN_First(
			long companyId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<CompoundPKEntry>
				orderByComparator)
		throws NoSuchCompoundPKEntryException;

	/**
	 * Returns the first compound pk entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching compound pk entry, or <code>null</code> if a matching compound pk entry could not be found
	 */
	public CompoundPKEntry fetchByC_CN_First(
		long companyId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<CompoundPKEntry>
			orderByComparator);

	/**
	 * Removes all the compound pk entries where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	public void removeByC_CN(long companyId, long classNameId);

	/**
	 * Returns the number of compound pk entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching compound pk entries
	 */
	public int countByC_CN(long companyId, long classNameId);

	/**
	 * Returns the compound pk entry where companyId = &#63; and name = &#63; or throws a <code>NoSuchCompoundPKEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching compound pk entry
	 * @throws NoSuchCompoundPKEntryException if a matching compound pk entry could not be found
	 */
	public CompoundPKEntry findByC_N(long companyId, String name)
		throws NoSuchCompoundPKEntryException;

	/**
	 * Returns the compound pk entry where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching compound pk entry, or <code>null</code> if a matching compound pk entry could not be found
	 */
	public CompoundPKEntry fetchByC_N(
		long companyId, String name, boolean useFinderCache);

	/**
	 * Removes the compound pk entry where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the compound pk entry that was removed
	 */
	public CompoundPKEntry removeByC_N(long companyId, String name)
		throws NoSuchCompoundPKEntryException;

	/**
	 * Returns the number of compound pk entries where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching compound pk entries
	 */
	public int countByC_N(long companyId, String name);

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

	/**
	 * Returns the compound pk entry where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching compound pk entry, or <code>null</code> if a matching compound pk entry could not be found
	 */
	public default CompoundPKEntry fetchByC_N(long companyId, String name) {
		return fetchByC_N(companyId, name, true);
	}

	/**
	 * Returns all the compound pk entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching compound pk entries
	 */
	public default java.util.List<CompoundPKEntry> findByC_CN(
		long companyId, long classNameId) {

		return findByC_CN(
			companyId, classNameId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the compound pk entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.CompoundPKEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of compound pk entries
	 * @param end the upper bound of the range of compound pk entries (not inclusive)
	 * @return the range of matching compound pk entries
	 */
	public default java.util.List<CompoundPKEntry> findByC_CN(
		long companyId, long classNameId, int start, int end) {

		return findByC_CN(companyId, classNameId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the compound pk entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.CompoundPKEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of compound pk entries
	 * @param end the upper bound of the range of compound pk entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching compound pk entries
	 */
	public default java.util.List<CompoundPKEntry> findByC_CN(
		long companyId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CompoundPKEntry>
			orderByComparator) {

		return findByC_CN(
			companyId, classNameId, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1534671917