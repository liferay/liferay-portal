/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.qualifier.service.persistence;

import com.liferay.commerce.qualifier.exception.NoSuchCommerceQualifierEntryException;
import com.liferay.commerce.qualifier.model.CommerceQualifierEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the commerce qualifier entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Riccardo Alberti
 * @see CommerceQualifierEntryUtil
 * @generated
 */
@ProviderType
public interface CommerceQualifierEntryPersistence
	extends BasePersistence<CommerceQualifierEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CommerceQualifierEntryUtil} to access the commerce qualifier entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryModelImpl</code>.
	 * </p>
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param start the lower bound of the range of commerce qualifier entries
	 * @param end the upper bound of the range of commerce qualifier entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce qualifier entries
	 */
	public java.util.List<CommerceQualifierEntry> findByS_S(
		long sourceClassNameId, long sourceClassPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceQualifierEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce qualifier entry in the ordered set where sourceClassNameId = &#63; and sourceClassPK = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce qualifier entry
	 * @throws NoSuchCommerceQualifierEntryException if a matching commerce qualifier entry could not be found
	 */
	public CommerceQualifierEntry findByS_S_First(
			long sourceClassNameId, long sourceClassPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceQualifierEntry> orderByComparator)
		throws NoSuchCommerceQualifierEntryException;

	/**
	 * Returns the first commerce qualifier entry in the ordered set where sourceClassNameId = &#63; and sourceClassPK = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce qualifier entry, or <code>null</code> if a matching commerce qualifier entry could not be found
	 */
	public CommerceQualifierEntry fetchByS_S_First(
		long sourceClassNameId, long sourceClassPK,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceQualifierEntry>
			orderByComparator);

	/**
	 * Removes all the commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63; from the database.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 */
	public void removeByS_S(long sourceClassNameId, long sourceClassPK);

	/**
	 * Returns the number of commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @return the number of matching commerce qualifier entries
	 */
	public int countByS_S(long sourceClassNameId, long sourceClassPK);

	/**
	 * Returns an ordered range of all the commerce qualifier entries where targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryModelImpl</code>.
	 * </p>
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @param start the lower bound of the range of commerce qualifier entries
	 * @param end the upper bound of the range of commerce qualifier entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce qualifier entries
	 */
	public java.util.List<CommerceQualifierEntry> findByT_T(
		long targetClassNameId, long targetClassPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceQualifierEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce qualifier entry in the ordered set where targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce qualifier entry
	 * @throws NoSuchCommerceQualifierEntryException if a matching commerce qualifier entry could not be found
	 */
	public CommerceQualifierEntry findByT_T_First(
			long targetClassNameId, long targetClassPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceQualifierEntry> orderByComparator)
		throws NoSuchCommerceQualifierEntryException;

	/**
	 * Returns the first commerce qualifier entry in the ordered set where targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce qualifier entry, or <code>null</code> if a matching commerce qualifier entry could not be found
	 */
	public CommerceQualifierEntry fetchByT_T_First(
		long targetClassNameId, long targetClassPK,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceQualifierEntry>
			orderByComparator);

	/**
	 * Removes all the commerce qualifier entries where targetClassNameId = &#63; and targetClassPK = &#63; from the database.
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 */
	public void removeByT_T(long targetClassNameId, long targetClassPK);

	/**
	 * Returns the number of commerce qualifier entries where targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @return the number of matching commerce qualifier entries
	 */
	public int countByT_T(long targetClassNameId, long targetClassPK);

	/**
	 * Returns the commerce qualifier entry where sourceClassNameId = &#63; and sourceClassPK = &#63; and targetClassNameId = &#63; and targetClassPK = &#63; or throws a <code>NoSuchCommerceQualifierEntryException</code> if it could not be found.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @return the matching commerce qualifier entry
	 * @throws NoSuchCommerceQualifierEntryException if a matching commerce qualifier entry could not be found
	 */
	public CommerceQualifierEntry findByS_S_T_T(
			long sourceClassNameId, long sourceClassPK, long targetClassNameId,
			long targetClassPK)
		throws NoSuchCommerceQualifierEntryException;

	/**
	 * Returns the commerce qualifier entry where sourceClassNameId = &#63; and sourceClassPK = &#63; and targetClassNameId = &#63; and targetClassPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce qualifier entry, or <code>null</code> if a matching commerce qualifier entry could not be found
	 */
	public CommerceQualifierEntry fetchByS_S_T_T(
		long sourceClassNameId, long sourceClassPK, long targetClassNameId,
		long targetClassPK, boolean useFinderCache);

	/**
	 * Removes the commerce qualifier entry where sourceClassNameId = &#63; and sourceClassPK = &#63; and targetClassNameId = &#63; and targetClassPK = &#63; from the database.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @return the commerce qualifier entry that was removed
	 */
	public CommerceQualifierEntry removeByS_S_T_T(
			long sourceClassNameId, long sourceClassPK, long targetClassNameId,
			long targetClassPK)
		throws NoSuchCommerceQualifierEntryException;

	/**
	 * Returns the number of commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63; and targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @return the number of matching commerce qualifier entries
	 */
	public int countByS_S_T_T(
		long sourceClassNameId, long sourceClassPK, long targetClassNameId,
		long targetClassPK);

	/**
	 * Creates a new commerce qualifier entry with the primary key. Does not add the commerce qualifier entry to the database.
	 *
	 * @param commerceQualifierEntryId the primary key for the new commerce qualifier entry
	 * @return the new commerce qualifier entry
	 */
	public CommerceQualifierEntry create(long commerceQualifierEntryId);

	/**
	 * Removes the commerce qualifier entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceQualifierEntryId the primary key of the commerce qualifier entry
	 * @return the commerce qualifier entry that was removed
	 * @throws NoSuchCommerceQualifierEntryException if a commerce qualifier entry with the primary key could not be found
	 */
	public CommerceQualifierEntry remove(long commerceQualifierEntryId)
		throws NoSuchCommerceQualifierEntryException;

	public CommerceQualifierEntry updateImpl(
		CommerceQualifierEntry commerceQualifierEntry);

	/**
	 * Returns the commerce qualifier entry with the primary key or throws a <code>NoSuchCommerceQualifierEntryException</code> if it could not be found.
	 *
	 * @param commerceQualifierEntryId the primary key of the commerce qualifier entry
	 * @return the commerce qualifier entry
	 * @throws NoSuchCommerceQualifierEntryException if a commerce qualifier entry with the primary key could not be found
	 */
	public CommerceQualifierEntry findByPrimaryKey(
			long commerceQualifierEntryId)
		throws NoSuchCommerceQualifierEntryException;

	/**
	 * Returns the commerce qualifier entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceQualifierEntryId the primary key of the commerce qualifier entry
	 * @return the commerce qualifier entry, or <code>null</code> if a commerce qualifier entry with the primary key could not be found
	 */
	public CommerceQualifierEntry fetchByPrimaryKey(
		long commerceQualifierEntryId);

	/**
	 * Returns the commerce qualifier entry where sourceClassNameId = &#63; and sourceClassPK = &#63; and targetClassNameId = &#63; and targetClassPK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @return the matching commerce qualifier entry, or <code>null</code> if a matching commerce qualifier entry could not be found
	 */
	public default CommerceQualifierEntry fetchByS_S_T_T(
		long sourceClassNameId, long sourceClassPK, long targetClassNameId,
		long targetClassPK) {

		return fetchByS_S_T_T(
			sourceClassNameId, sourceClassPK, targetClassNameId, targetClassPK,
			true);
	}

	/**
	 * Returns all the commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @return the matching commerce qualifier entries
	 */
	public default java.util.List<CommerceQualifierEntry> findByS_S(
		long sourceClassNameId, long sourceClassPK) {

		return findByS_S(
			sourceClassNameId, sourceClassPK,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryModelImpl</code>.
	 * </p>
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param start the lower bound of the range of commerce qualifier entries
	 * @param end the upper bound of the range of commerce qualifier entries (not inclusive)
	 * @return the range of matching commerce qualifier entries
	 */
	public default java.util.List<CommerceQualifierEntry> findByS_S(
		long sourceClassNameId, long sourceClassPK, int start, int end) {

		return findByS_S(
			sourceClassNameId, sourceClassPK, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryModelImpl</code>.
	 * </p>
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param start the lower bound of the range of commerce qualifier entries
	 * @param end the upper bound of the range of commerce qualifier entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce qualifier entries
	 */
	public default java.util.List<CommerceQualifierEntry> findByS_S(
		long sourceClassNameId, long sourceClassPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceQualifierEntry>
			orderByComparator) {

		return findByS_S(
			sourceClassNameId, sourceClassPK, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the commerce qualifier entries where targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @return the matching commerce qualifier entries
	 */
	public default java.util.List<CommerceQualifierEntry> findByT_T(
		long targetClassNameId, long targetClassPK) {

		return findByT_T(
			targetClassNameId, targetClassPK,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce qualifier entries where targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryModelImpl</code>.
	 * </p>
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @param start the lower bound of the range of commerce qualifier entries
	 * @param end the upper bound of the range of commerce qualifier entries (not inclusive)
	 * @return the range of matching commerce qualifier entries
	 */
	public default java.util.List<CommerceQualifierEntry> findByT_T(
		long targetClassNameId, long targetClassPK, int start, int end) {

		return findByT_T(
			targetClassNameId, targetClassPK, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce qualifier entries where targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryModelImpl</code>.
	 * </p>
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @param start the lower bound of the range of commerce qualifier entries
	 * @param end the upper bound of the range of commerce qualifier entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce qualifier entries
	 */
	public default java.util.List<CommerceQualifierEntry> findByT_T(
		long targetClassNameId, long targetClassPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceQualifierEntry>
			orderByComparator) {

		return findByT_T(
			targetClassNameId, targetClassPK, start, end, orderByComparator,
			true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-722797388