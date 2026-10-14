/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.service.persistence;

import com.liferay.dynamic.data.lists.exception.NoSuchRecordSetVersionException;
import com.liferay.dynamic.data.lists.model.DDLRecordSetVersion;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the ddl record set version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDLRecordSetVersionUtil
 * @generated
 */
@ProviderType
public interface DDLRecordSetVersionPersistence
	extends BasePersistence<DDLRecordSetVersion>,
			CTPersistence<DDLRecordSetVersion> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link DDLRecordSetVersionUtil} to access the ddl record set version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the ddl record set versions where recordSetId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordSetVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordSetId the record set ID
	 * @param start the lower bound of the range of ddl record set versions
	 * @param end the upper bound of the range of ddl record set versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddl record set versions
	 */
	public java.util.List<DDLRecordSetVersion> findByRecordSetId(
		long recordSetId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDLRecordSetVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddl record set version in the ordered set where recordSetId = &#63;.
	 *
	 * @param recordSetId the record set ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record set version
	 * @throws NoSuchRecordSetVersionException if a matching ddl record set version could not be found
	 */
	public DDLRecordSetVersion findByRecordSetId_First(
			long recordSetId,
			com.liferay.portal.kernel.util.OrderByComparator
				<DDLRecordSetVersion> orderByComparator)
		throws NoSuchRecordSetVersionException;

	/**
	 * Returns the first ddl record set version in the ordered set where recordSetId = &#63;.
	 *
	 * @param recordSetId the record set ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record set version, or <code>null</code> if a matching ddl record set version could not be found
	 */
	public DDLRecordSetVersion fetchByRecordSetId_First(
		long recordSetId,
		com.liferay.portal.kernel.util.OrderByComparator<DDLRecordSetVersion>
			orderByComparator);

	/**
	 * Removes all the ddl record set versions where recordSetId = &#63; from the database.
	 *
	 * @param recordSetId the record set ID
	 */
	public void removeByRecordSetId(long recordSetId);

	/**
	 * Returns the number of ddl record set versions where recordSetId = &#63;.
	 *
	 * @param recordSetId the record set ID
	 * @return the number of matching ddl record set versions
	 */
	public int countByRecordSetId(long recordSetId);

	/**
	 * Returns the ddl record set version where recordSetId = &#63; and version = &#63; or throws a <code>NoSuchRecordSetVersionException</code> if it could not be found.
	 *
	 * @param recordSetId the record set ID
	 * @param version the version
	 * @return the matching ddl record set version
	 * @throws NoSuchRecordSetVersionException if a matching ddl record set version could not be found
	 */
	public DDLRecordSetVersion findByRS_V(long recordSetId, String version)
		throws NoSuchRecordSetVersionException;

	/**
	 * Returns the ddl record set version where recordSetId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param recordSetId the record set ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddl record set version, or <code>null</code> if a matching ddl record set version could not be found
	 */
	public DDLRecordSetVersion fetchByRS_V(
		long recordSetId, String version, boolean useFinderCache);

	/**
	 * Removes the ddl record set version where recordSetId = &#63; and version = &#63; from the database.
	 *
	 * @param recordSetId the record set ID
	 * @param version the version
	 * @return the ddl record set version that was removed
	 */
	public DDLRecordSetVersion removeByRS_V(long recordSetId, String version)
		throws NoSuchRecordSetVersionException;

	/**
	 * Returns the number of ddl record set versions where recordSetId = &#63; and version = &#63;.
	 *
	 * @param recordSetId the record set ID
	 * @param version the version
	 * @return the number of matching ddl record set versions
	 */
	public int countByRS_V(long recordSetId, String version);

	/**
	 * Creates a new ddl record set version with the primary key. Does not add the ddl record set version to the database.
	 *
	 * @param recordSetVersionId the primary key for the new ddl record set version
	 * @return the new ddl record set version
	 */
	public DDLRecordSetVersion create(long recordSetVersionId);

	/**
	 * Removes the ddl record set version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param recordSetVersionId the primary key of the ddl record set version
	 * @return the ddl record set version that was removed
	 * @throws NoSuchRecordSetVersionException if a ddl record set version with the primary key could not be found
	 */
	public DDLRecordSetVersion remove(long recordSetVersionId)
		throws NoSuchRecordSetVersionException;

	public DDLRecordSetVersion updateImpl(
		DDLRecordSetVersion ddlRecordSetVersion);

	/**
	 * Returns the ddl record set version with the primary key or throws a <code>NoSuchRecordSetVersionException</code> if it could not be found.
	 *
	 * @param recordSetVersionId the primary key of the ddl record set version
	 * @return the ddl record set version
	 * @throws NoSuchRecordSetVersionException if a ddl record set version with the primary key could not be found
	 */
	public DDLRecordSetVersion findByPrimaryKey(long recordSetVersionId)
		throws NoSuchRecordSetVersionException;

	/**
	 * Returns the ddl record set version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param recordSetVersionId the primary key of the ddl record set version
	 * @return the ddl record set version, or <code>null</code> if a ddl record set version with the primary key could not be found
	 */
	public DDLRecordSetVersion fetchByPrimaryKey(long recordSetVersionId);

	/**
	 * Returns the ddl record set version where recordSetId = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param recordSetId the record set ID
	 * @param version the version
	 * @return the matching ddl record set version, or <code>null</code> if a matching ddl record set version could not be found
	 */
	public default DDLRecordSetVersion fetchByRS_V(
		long recordSetId, String version) {

		return fetchByRS_V(recordSetId, version, true);
	}

	/**
	 * Returns all the ddl record set versions where recordSetId = &#63;.
	 *
	 * @param recordSetId the record set ID
	 * @return the matching ddl record set versions
	 */
	public default java.util.List<DDLRecordSetVersion> findByRecordSetId(
		long recordSetId) {

		return findByRecordSetId(
			recordSetId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ddl record set versions where recordSetId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordSetVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordSetId the record set ID
	 * @param start the lower bound of the range of ddl record set versions
	 * @param end the upper bound of the range of ddl record set versions (not inclusive)
	 * @return the range of matching ddl record set versions
	 */
	public default java.util.List<DDLRecordSetVersion> findByRecordSetId(
		long recordSetId, int start, int end) {

		return findByRecordSetId(recordSetId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the ddl record set versions where recordSetId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordSetVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordSetId the record set ID
	 * @param start the lower bound of the range of ddl record set versions
	 * @param end the upper bound of the range of ddl record set versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddl record set versions
	 */
	public default java.util.List<DDLRecordSetVersion> findByRecordSetId(
		long recordSetId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDLRecordSetVersion>
			orderByComparator) {

		return findByRecordSetId(
			recordSetId, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1408674328