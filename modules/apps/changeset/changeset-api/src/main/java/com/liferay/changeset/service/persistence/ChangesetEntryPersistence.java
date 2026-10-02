/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.changeset.service.persistence;

import com.liferay.changeset.exception.NoSuchEntryException;
import com.liferay.changeset.model.ChangesetEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the changeset entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ChangesetEntryUtil
 * @generated
 */
@ProviderType
public interface ChangesetEntryPersistence
	extends BasePersistence<ChangesetEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ChangesetEntryUtil} to access the changeset entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the changeset entries where changesetCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param start the lower bound of the range of changeset entries
	 * @param end the upper bound of the range of changeset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching changeset entries
	 */
	public java.util.List<ChangesetEntry> findByChangesetCollectionId(
		long changesetCollectionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ChangesetEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first changeset entry in the ordered set where changesetCollectionId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching changeset entry
	 * @throws NoSuchEntryException if a matching changeset entry could not be found
	 */
	public ChangesetEntry findByChangesetCollectionId_First(
			long changesetCollectionId,
			com.liferay.portal.kernel.util.OrderByComparator<ChangesetEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first changeset entry in the ordered set where changesetCollectionId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching changeset entry, or <code>null</code> if a matching changeset entry could not be found
	 */
	public ChangesetEntry fetchByChangesetCollectionId_First(
		long changesetCollectionId,
		com.liferay.portal.kernel.util.OrderByComparator<ChangesetEntry>
			orderByComparator);

	/**
	 * Removes all the changeset entries where changesetCollectionId = &#63; from the database.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 */
	public void removeByChangesetCollectionId(long changesetCollectionId);

	/**
	 * Returns the number of changeset entries where changesetCollectionId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @return the number of matching changeset entries
	 */
	public int countByChangesetCollectionId(long changesetCollectionId);

	/**
	 * Returns an ordered range of all the changeset entries where changesetCollectionId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of changeset entries
	 * @param end the upper bound of the range of changeset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching changeset entries
	 */
	public java.util.List<ChangesetEntry> findByC_C(
		long changesetCollectionId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ChangesetEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first changeset entry in the ordered set where changesetCollectionId = &#63; and classNameId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching changeset entry
	 * @throws NoSuchEntryException if a matching changeset entry could not be found
	 */
	public ChangesetEntry findByC_C_First(
			long changesetCollectionId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<ChangesetEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first changeset entry in the ordered set where changesetCollectionId = &#63; and classNameId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching changeset entry, or <code>null</code> if a matching changeset entry could not be found
	 */
	public ChangesetEntry fetchByC_C_First(
		long changesetCollectionId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<ChangesetEntry>
			orderByComparator);

	/**
	 * Removes all the changeset entries where changesetCollectionId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 */
	public void removeByC_C(long changesetCollectionId, long classNameId);

	/**
	 * Returns the number of changeset entries where changesetCollectionId = &#63; and classNameId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @return the number of matching changeset entries
	 */
	public int countByC_C(long changesetCollectionId, long classNameId);

	/**
	 * Returns the changeset entry where changesetCollectionId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @return the matching changeset entry
	 * @throws NoSuchEntryException if a matching changeset entry could not be found
	 */
	public ChangesetEntry findByC_CERC_C(
			long changesetCollectionId, String classExternalReferenceCode,
			long classNameId)
		throws NoSuchEntryException;

	/**
	 * Returns the changeset entry where changesetCollectionId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching changeset entry, or <code>null</code> if a matching changeset entry could not be found
	 */
	public ChangesetEntry fetchByC_CERC_C(
		long changesetCollectionId, String classExternalReferenceCode,
		long classNameId, boolean useFinderCache);

	/**
	 * Removes the changeset entry where changesetCollectionId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; from the database.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @return the changeset entry that was removed
	 */
	public ChangesetEntry removeByC_CERC_C(
			long changesetCollectionId, String classExternalReferenceCode,
			long classNameId)
		throws NoSuchEntryException;

	/**
	 * Returns the number of changeset entries where changesetCollectionId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @return the number of matching changeset entries
	 */
	public int countByC_CERC_C(
		long changesetCollectionId, String classExternalReferenceCode,
		long classNameId);

	/**
	 * Returns the changeset entry where changesetCollectionId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching changeset entry
	 * @throws NoSuchEntryException if a matching changeset entry could not be found
	 */
	public ChangesetEntry findByC_C_C(
			long changesetCollectionId, long classNameId, long classPK)
		throws NoSuchEntryException;

	/**
	 * Returns the changeset entry where changesetCollectionId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching changeset entry, or <code>null</code> if a matching changeset entry could not be found
	 */
	public ChangesetEntry fetchByC_C_C(
		long changesetCollectionId, long classNameId, long classPK,
		boolean useFinderCache);

	/**
	 * Removes the changeset entry where changesetCollectionId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the changeset entry that was removed
	 */
	public ChangesetEntry removeByC_C_C(
			long changesetCollectionId, long classNameId, long classPK)
		throws NoSuchEntryException;

	/**
	 * Returns the number of changeset entries where changesetCollectionId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching changeset entries
	 */
	public int countByC_C_C(
		long changesetCollectionId, long classNameId, long classPK);

	/**
	 * Creates a new changeset entry with the primary key. Does not add the changeset entry to the database.
	 *
	 * @param changesetEntryId the primary key for the new changeset entry
	 * @return the new changeset entry
	 */
	public ChangesetEntry create(long changesetEntryId);

	/**
	 * Removes the changeset entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param changesetEntryId the primary key of the changeset entry
	 * @return the changeset entry that was removed
	 * @throws NoSuchEntryException if a changeset entry with the primary key could not be found
	 */
	public ChangesetEntry remove(long changesetEntryId)
		throws NoSuchEntryException;

	public ChangesetEntry updateImpl(ChangesetEntry changesetEntry);

	/**
	 * Returns the changeset entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param changesetEntryId the primary key of the changeset entry
	 * @return the changeset entry
	 * @throws NoSuchEntryException if a changeset entry with the primary key could not be found
	 */
	public ChangesetEntry findByPrimaryKey(long changesetEntryId)
		throws NoSuchEntryException;

	/**
	 * Returns the changeset entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param changesetEntryId the primary key of the changeset entry
	 * @return the changeset entry, or <code>null</code> if a changeset entry with the primary key could not be found
	 */
	public ChangesetEntry fetchByPrimaryKey(long changesetEntryId);

	/**
	 * Returns the changeset entry where changesetCollectionId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @return the matching changeset entry, or <code>null</code> if a matching changeset entry could not be found
	 */
	public default ChangesetEntry fetchByC_CERC_C(
		long changesetCollectionId, String classExternalReferenceCode,
		long classNameId) {

		return fetchByC_CERC_C(
			changesetCollectionId, classExternalReferenceCode, classNameId,
			true);
	}

	/**
	 * Returns the changeset entry where changesetCollectionId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching changeset entry, or <code>null</code> if a matching changeset entry could not be found
	 */
	public default ChangesetEntry fetchByC_C_C(
		long changesetCollectionId, long classNameId, long classPK) {

		return fetchByC_C_C(changesetCollectionId, classNameId, classPK, true);
	}

	/**
	 * Returns all the changeset entries where changesetCollectionId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @return the matching changeset entries
	 */
	public default java.util.List<ChangesetEntry> findByChangesetCollectionId(
		long changesetCollectionId) {

		return findByChangesetCollectionId(
			changesetCollectionId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the changeset entries where changesetCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param start the lower bound of the range of changeset entries
	 * @param end the upper bound of the range of changeset entries (not inclusive)
	 * @return the range of matching changeset entries
	 */
	public default java.util.List<ChangesetEntry> findByChangesetCollectionId(
		long changesetCollectionId, int start, int end) {

		return findByChangesetCollectionId(
			changesetCollectionId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the changeset entries where changesetCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param start the lower bound of the range of changeset entries
	 * @param end the upper bound of the range of changeset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching changeset entries
	 */
	public default java.util.List<ChangesetEntry> findByChangesetCollectionId(
		long changesetCollectionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ChangesetEntry>
			orderByComparator) {

		return findByChangesetCollectionId(
			changesetCollectionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the changeset entries where changesetCollectionId = &#63; and classNameId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @return the matching changeset entries
	 */
	public default java.util.List<ChangesetEntry> findByC_C(
		long changesetCollectionId, long classNameId) {

		return findByC_C(
			changesetCollectionId, classNameId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the changeset entries where changesetCollectionId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of changeset entries
	 * @param end the upper bound of the range of changeset entries (not inclusive)
	 * @return the range of matching changeset entries
	 */
	public default java.util.List<ChangesetEntry> findByC_C(
		long changesetCollectionId, long classNameId, int start, int end) {

		return findByC_C(
			changesetCollectionId, classNameId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the changeset entries where changesetCollectionId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of changeset entries
	 * @param end the upper bound of the range of changeset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching changeset entries
	 */
	public default java.util.List<ChangesetEntry> findByC_C(
		long changesetCollectionId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ChangesetEntry>
			orderByComparator) {

		return findByC_C(
			changesetCollectionId, classNameId, start, end, orderByComparator,
			true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1558503794