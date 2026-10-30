/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.service.persistence;

import com.liferay.dynamic.data.lists.exception.NoSuchRecordVersionException;
import com.liferay.dynamic.data.lists.model.DDLRecordVersion;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the ddl record version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDLRecordVersionUtil
 * @generated
 */
@ProviderType
public interface DDLRecordVersionPersistence
	extends BasePersistence<DDLRecordVersion>, CTPersistence<DDLRecordVersion> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link DDLRecordVersionUtil} to access the ddl record version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the ddl record versions where recordId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordId the record ID
	 * @param start the lower bound of the range of ddl record versions
	 * @param end the upper bound of the range of ddl record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddl record versions
	 */
	public java.util.List<DDLRecordVersion> findByRecordId(
		long recordId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDLRecordVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddl record version in the ordered set where recordId = &#63;.
	 *
	 * @param recordId the record ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record version
	 * @throws NoSuchRecordVersionException if a matching ddl record version could not be found
	 */
	public DDLRecordVersion findByRecordId_First(
			long recordId,
			com.liferay.portal.kernel.util.OrderByComparator<DDLRecordVersion>
				orderByComparator)
		throws NoSuchRecordVersionException;

	/**
	 * Returns the first ddl record version in the ordered set where recordId = &#63;.
	 *
	 * @param recordId the record ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record version, or <code>null</code> if a matching ddl record version could not be found
	 */
	public DDLRecordVersion fetchByRecordId_First(
		long recordId,
		com.liferay.portal.kernel.util.OrderByComparator<DDLRecordVersion>
			orderByComparator);

	/**
	 * Removes all the ddl record versions where recordId = &#63; from the database.
	 *
	 * @param recordId the record ID
	 */
	public void removeByRecordId(long recordId);

	/**
	 * Returns the number of ddl record versions where recordId = &#63;.
	 *
	 * @param recordId the record ID
	 * @return the number of matching ddl record versions
	 */
	public int countByRecordId(long recordId);

	/**
	 * Returns the ddl record version where recordId = &#63; and version = &#63; or throws a <code>NoSuchRecordVersionException</code> if it could not be found.
	 *
	 * @param recordId the record ID
	 * @param version the version
	 * @return the matching ddl record version
	 * @throws NoSuchRecordVersionException if a matching ddl record version could not be found
	 */
	public DDLRecordVersion findByR_V(long recordId, String version)
		throws NoSuchRecordVersionException;

	/**
	 * Returns the ddl record version where recordId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param recordId the record ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddl record version, or <code>null</code> if a matching ddl record version could not be found
	 */
	public DDLRecordVersion fetchByR_V(
		long recordId, String version, boolean useFinderCache);

	/**
	 * Removes the ddl record version where recordId = &#63; and version = &#63; from the database.
	 *
	 * @param recordId the record ID
	 * @param version the version
	 * @return the ddl record version that was removed
	 */
	public DDLRecordVersion removeByR_V(long recordId, String version)
		throws NoSuchRecordVersionException;

	/**
	 * Returns the number of ddl record versions where recordId = &#63; and version = &#63;.
	 *
	 * @param recordId the record ID
	 * @param version the version
	 * @return the number of matching ddl record versions
	 */
	public int countByR_V(long recordId, String version);

	/**
	 * Returns an ordered range of all the ddl record versions where recordId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordId the record ID
	 * @param status the status
	 * @param start the lower bound of the range of ddl record versions
	 * @param end the upper bound of the range of ddl record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddl record versions
	 */
	public java.util.List<DDLRecordVersion> findByR_S(
		long recordId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDLRecordVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddl record version in the ordered set where recordId = &#63; and status = &#63;.
	 *
	 * @param recordId the record ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record version
	 * @throws NoSuchRecordVersionException if a matching ddl record version could not be found
	 */
	public DDLRecordVersion findByR_S_First(
			long recordId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<DDLRecordVersion>
				orderByComparator)
		throws NoSuchRecordVersionException;

	/**
	 * Returns the first ddl record version in the ordered set where recordId = &#63; and status = &#63;.
	 *
	 * @param recordId the record ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record version, or <code>null</code> if a matching ddl record version could not be found
	 */
	public DDLRecordVersion fetchByR_S_First(
		long recordId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<DDLRecordVersion>
			orderByComparator);

	/**
	 * Removes all the ddl record versions where recordId = &#63; and status = &#63; from the database.
	 *
	 * @param recordId the record ID
	 * @param status the status
	 */
	public void removeByR_S(long recordId, int status);

	/**
	 * Returns the number of ddl record versions where recordId = &#63; and status = &#63;.
	 *
	 * @param recordId the record ID
	 * @param status the status
	 * @return the number of matching ddl record versions
	 */
	public int countByR_S(long recordId, int status);

	/**
	 * Creates a new ddl record version with the primary key. Does not add the ddl record version to the database.
	 *
	 * @param recordVersionId the primary key for the new ddl record version
	 * @return the new ddl record version
	 */
	public DDLRecordVersion create(long recordVersionId);

	/**
	 * Removes the ddl record version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param recordVersionId the primary key of the ddl record version
	 * @return the ddl record version that was removed
	 * @throws NoSuchRecordVersionException if a ddl record version with the primary key could not be found
	 */
	public DDLRecordVersion remove(long recordVersionId)
		throws NoSuchRecordVersionException;

	public DDLRecordVersion updateImpl(DDLRecordVersion ddlRecordVersion);

	/**
	 * Returns the ddl record version with the primary key or throws a <code>NoSuchRecordVersionException</code> if it could not be found.
	 *
	 * @param recordVersionId the primary key of the ddl record version
	 * @return the ddl record version
	 * @throws NoSuchRecordVersionException if a ddl record version with the primary key could not be found
	 */
	public DDLRecordVersion findByPrimaryKey(long recordVersionId)
		throws NoSuchRecordVersionException;

	/**
	 * Returns the ddl record version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param recordVersionId the primary key of the ddl record version
	 * @return the ddl record version, or <code>null</code> if a ddl record version with the primary key could not be found
	 */
	public DDLRecordVersion fetchByPrimaryKey(long recordVersionId);

	/**
	 * Returns the ddl record version where recordId = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param recordId the record ID
	 * @param version the version
	 * @return the matching ddl record version, or <code>null</code> if a matching ddl record version could not be found
	 */
	public default DDLRecordVersion fetchByR_V(long recordId, String version) {
		return fetchByR_V(recordId, version, true);
	}

	/**
	 * Returns all the ddl record versions where recordId = &#63;.
	 *
	 * @param recordId the record ID
	 * @return the matching ddl record versions
	 */
	public default java.util.List<DDLRecordVersion> findByRecordId(
		long recordId) {

		return findByRecordId(
			recordId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ddl record versions where recordId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordId the record ID
	 * @param start the lower bound of the range of ddl record versions
	 * @param end the upper bound of the range of ddl record versions (not inclusive)
	 * @return the range of matching ddl record versions
	 */
	public default java.util.List<DDLRecordVersion> findByRecordId(
		long recordId, int start, int end) {

		return findByRecordId(recordId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the ddl record versions where recordId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordId the record ID
	 * @param start the lower bound of the range of ddl record versions
	 * @param end the upper bound of the range of ddl record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddl record versions
	 */
	public default java.util.List<DDLRecordVersion> findByRecordId(
		long recordId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDLRecordVersion>
			orderByComparator) {

		return findByRecordId(recordId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the ddl record versions where recordId = &#63; and status = &#63;.
	 *
	 * @param recordId the record ID
	 * @param status the status
	 * @return the matching ddl record versions
	 */
	public default java.util.List<DDLRecordVersion> findByR_S(
		long recordId, int status) {

		return findByR_S(
			recordId, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ddl record versions where recordId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordId the record ID
	 * @param status the status
	 * @param start the lower bound of the range of ddl record versions
	 * @param end the upper bound of the range of ddl record versions (not inclusive)
	 * @return the range of matching ddl record versions
	 */
	public default java.util.List<DDLRecordVersion> findByR_S(
		long recordId, int status, int start, int end) {

		return findByR_S(recordId, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the ddl record versions where recordId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordId the record ID
	 * @param status the status
	 * @param start the lower bound of the range of ddl record versions
	 * @param end the upper bound of the range of ddl record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddl record versions
	 */
	public default java.util.List<DDLRecordVersion> findByR_S(
		long recordId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDLRecordVersion>
			orderByComparator) {

		return findByR_S(recordId, status, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1394283775