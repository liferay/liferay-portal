/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence;

import com.liferay.dynamic.data.mapping.exception.NoSuchFormInstanceRecordException;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the ddm form instance record service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDMFormInstanceRecordUtil
 * @generated
 */
@ProviderType
public interface DDMFormInstanceRecordPersistence
	extends BasePersistence<DDMFormInstanceRecord>,
			CTPersistence<DDMFormInstanceRecord> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link DDMFormInstanceRecordUtil} to access the ddm form instance record persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the ddm form instance records where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance records
	 */
	public java.util.List<DDMFormInstanceRecord> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFormInstanceRecord>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm form instance record in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record
	 * @throws NoSuchFormInstanceRecordException if a matching ddm form instance record could not be found
	 */
	public DDMFormInstanceRecord findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<DDMFormInstanceRecord> orderByComparator)
		throws NoSuchFormInstanceRecordException;

	/**
	 * Returns the first ddm form instance record in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record, or <code>null</code> if a matching ddm form instance record could not be found
	 */
	public DDMFormInstanceRecord fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFormInstanceRecord>
			orderByComparator);

	/**
	 * Removes all the ddm form instance records where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of ddm form instance records where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching ddm form instance records
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the ddm form instance record where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFormInstanceRecordException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddm form instance record
	 * @throws NoSuchFormInstanceRecordException if a matching ddm form instance record could not be found
	 */
	public DDMFormInstanceRecord findByUUID_G(String uuid, long groupId)
		throws NoSuchFormInstanceRecordException;

	/**
	 * Returns the ddm form instance record where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm form instance record, or <code>null</code> if a matching ddm form instance record could not be found
	 */
	public DDMFormInstanceRecord fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the ddm form instance record where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the ddm form instance record that was removed
	 */
	public DDMFormInstanceRecord removeByUUID_G(String uuid, long groupId)
		throws NoSuchFormInstanceRecordException;

	/**
	 * Returns the number of ddm form instance records where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching ddm form instance records
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the ddm form instance records where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance records
	 */
	public java.util.List<DDMFormInstanceRecord> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFormInstanceRecord>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm form instance record in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record
	 * @throws NoSuchFormInstanceRecordException if a matching ddm form instance record could not be found
	 */
	public DDMFormInstanceRecord findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<DDMFormInstanceRecord> orderByComparator)
		throws NoSuchFormInstanceRecordException;

	/**
	 * Returns the first ddm form instance record in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record, or <code>null</code> if a matching ddm form instance record could not be found
	 */
	public DDMFormInstanceRecord fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFormInstanceRecord>
			orderByComparator);

	/**
	 * Removes all the ddm form instance records where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of ddm form instance records where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching ddm form instance records
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the ddm form instance records where formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance records
	 */
	public java.util.List<DDMFormInstanceRecord> findByFormInstanceId(
		long formInstanceId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFormInstanceRecord>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm form instance record in the ordered set where formInstanceId = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record
	 * @throws NoSuchFormInstanceRecordException if a matching ddm form instance record could not be found
	 */
	public DDMFormInstanceRecord findByFormInstanceId_First(
			long formInstanceId,
			com.liferay.portal.kernel.util.OrderByComparator
				<DDMFormInstanceRecord> orderByComparator)
		throws NoSuchFormInstanceRecordException;

	/**
	 * Returns the first ddm form instance record in the ordered set where formInstanceId = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record, or <code>null</code> if a matching ddm form instance record could not be found
	 */
	public DDMFormInstanceRecord fetchByFormInstanceId_First(
		long formInstanceId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFormInstanceRecord>
			orderByComparator);

	/**
	 * Removes all the ddm form instance records where formInstanceId = &#63; from the database.
	 *
	 * @param formInstanceId the form instance ID
	 */
	public void removeByFormInstanceId(long formInstanceId);

	/**
	 * Returns the number of ddm form instance records where formInstanceId = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @return the number of matching ddm form instance records
	 */
	public int countByFormInstanceId(long formInstanceId);

	/**
	 * Returns an ordered range of all the ddm form instance records where userId = &#63; and formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance records
	 */
	public java.util.List<DDMFormInstanceRecord> findByU_F(
		long userId, long formInstanceId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFormInstanceRecord>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm form instance record in the ordered set where userId = &#63; and formInstanceId = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record
	 * @throws NoSuchFormInstanceRecordException if a matching ddm form instance record could not be found
	 */
	public DDMFormInstanceRecord findByU_F_First(
			long userId, long formInstanceId,
			com.liferay.portal.kernel.util.OrderByComparator
				<DDMFormInstanceRecord> orderByComparator)
		throws NoSuchFormInstanceRecordException;

	/**
	 * Returns the first ddm form instance record in the ordered set where userId = &#63; and formInstanceId = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record, or <code>null</code> if a matching ddm form instance record could not be found
	 */
	public DDMFormInstanceRecord fetchByU_F_First(
		long userId, long formInstanceId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFormInstanceRecord>
			orderByComparator);

	/**
	 * Removes all the ddm form instance records where userId = &#63; and formInstanceId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 */
	public void removeByU_F(long userId, long formInstanceId);

	/**
	 * Returns the number of ddm form instance records where userId = &#63; and formInstanceId = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @return the number of matching ddm form instance records
	 */
	public int countByU_F(long userId, long formInstanceId);

	/**
	 * Returns an ordered range of all the ddm form instance records where formInstanceId = &#63; and ipAddress = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceId the form instance ID
	 * @param ipAddress the ip address
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance records
	 */
	public java.util.List<DDMFormInstanceRecord> findByF_I(
		long formInstanceId, String ipAddress, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFormInstanceRecord>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm form instance record in the ordered set where formInstanceId = &#63; and ipAddress = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param ipAddress the ip address
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record
	 * @throws NoSuchFormInstanceRecordException if a matching ddm form instance record could not be found
	 */
	public DDMFormInstanceRecord findByF_I_First(
			long formInstanceId, String ipAddress,
			com.liferay.portal.kernel.util.OrderByComparator
				<DDMFormInstanceRecord> orderByComparator)
		throws NoSuchFormInstanceRecordException;

	/**
	 * Returns the first ddm form instance record in the ordered set where formInstanceId = &#63; and ipAddress = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param ipAddress the ip address
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record, or <code>null</code> if a matching ddm form instance record could not be found
	 */
	public DDMFormInstanceRecord fetchByF_I_First(
		long formInstanceId, String ipAddress,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFormInstanceRecord>
			orderByComparator);

	/**
	 * Removes all the ddm form instance records where formInstanceId = &#63; and ipAddress = &#63; from the database.
	 *
	 * @param formInstanceId the form instance ID
	 * @param ipAddress the ip address
	 */
	public void removeByF_I(long formInstanceId, String ipAddress);

	/**
	 * Returns the number of ddm form instance records where formInstanceId = &#63; and ipAddress = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param ipAddress the ip address
	 * @return the number of matching ddm form instance records
	 */
	public int countByF_I(long formInstanceId, String ipAddress);

	/**
	 * Creates a new ddm form instance record with the primary key. Does not add the ddm form instance record to the database.
	 *
	 * @param formInstanceRecordId the primary key for the new ddm form instance record
	 * @return the new ddm form instance record
	 */
	public DDMFormInstanceRecord create(long formInstanceRecordId);

	/**
	 * Removes the ddm form instance record with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param formInstanceRecordId the primary key of the ddm form instance record
	 * @return the ddm form instance record that was removed
	 * @throws NoSuchFormInstanceRecordException if a ddm form instance record with the primary key could not be found
	 */
	public DDMFormInstanceRecord remove(long formInstanceRecordId)
		throws NoSuchFormInstanceRecordException;

	public DDMFormInstanceRecord updateImpl(
		DDMFormInstanceRecord ddmFormInstanceRecord);

	/**
	 * Returns the ddm form instance record with the primary key or throws a <code>NoSuchFormInstanceRecordException</code> if it could not be found.
	 *
	 * @param formInstanceRecordId the primary key of the ddm form instance record
	 * @return the ddm form instance record
	 * @throws NoSuchFormInstanceRecordException if a ddm form instance record with the primary key could not be found
	 */
	public DDMFormInstanceRecord findByPrimaryKey(long formInstanceRecordId)
		throws NoSuchFormInstanceRecordException;

	/**
	 * Returns the ddm form instance record with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param formInstanceRecordId the primary key of the ddm form instance record
	 * @return the ddm form instance record, or <code>null</code> if a ddm form instance record with the primary key could not be found
	 */
	public DDMFormInstanceRecord fetchByPrimaryKey(long formInstanceRecordId);

	/**
	 * Returns the ddm form instance record where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddm form instance record, or <code>null</code> if a matching ddm form instance record could not be found
	 */
	public default DDMFormInstanceRecord fetchByUUID_G(
		String uuid, long groupId) {

		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns all the ddm form instance records where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching ddm form instance records
	 */
	public default java.util.List<DDMFormInstanceRecord> findByUuid(
		String uuid) {

		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ddm form instance records where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @return the range of matching ddm form instance records
	 */
	public default java.util.List<DDMFormInstanceRecord> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the ddm form instance records where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instance records
	 */
	public default java.util.List<DDMFormInstanceRecord> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFormInstanceRecord>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the ddm form instance records where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching ddm form instance records
	 */
	public default java.util.List<DDMFormInstanceRecord> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ddm form instance records where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @return the range of matching ddm form instance records
	 */
	public default java.util.List<DDMFormInstanceRecord> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the ddm form instance records where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instance records
	 */
	public default java.util.List<DDMFormInstanceRecord> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFormInstanceRecord>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the ddm form instance records where formInstanceId = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @return the matching ddm form instance records
	 */
	public default java.util.List<DDMFormInstanceRecord> findByFormInstanceId(
		long formInstanceId) {

		return findByFormInstanceId(
			formInstanceId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ddm form instance records where formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @return the range of matching ddm form instance records
	 */
	public default java.util.List<DDMFormInstanceRecord> findByFormInstanceId(
		long formInstanceId, int start, int end) {

		return findByFormInstanceId(formInstanceId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the ddm form instance records where formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instance records
	 */
	public default java.util.List<DDMFormInstanceRecord> findByFormInstanceId(
		long formInstanceId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFormInstanceRecord>
			orderByComparator) {

		return findByFormInstanceId(
			formInstanceId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the ddm form instance records where userId = &#63; and formInstanceId = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @return the matching ddm form instance records
	 */
	public default java.util.List<DDMFormInstanceRecord> findByU_F(
		long userId, long formInstanceId) {

		return findByU_F(
			userId, formInstanceId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ddm form instance records where userId = &#63; and formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @return the range of matching ddm form instance records
	 */
	public default java.util.List<DDMFormInstanceRecord> findByU_F(
		long userId, long formInstanceId, int start, int end) {

		return findByU_F(userId, formInstanceId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the ddm form instance records where userId = &#63; and formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instance records
	 */
	public default java.util.List<DDMFormInstanceRecord> findByU_F(
		long userId, long formInstanceId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFormInstanceRecord>
			orderByComparator) {

		return findByU_F(
			userId, formInstanceId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the ddm form instance records where formInstanceId = &#63; and ipAddress = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param ipAddress the ip address
	 * @return the matching ddm form instance records
	 */
	public default java.util.List<DDMFormInstanceRecord> findByF_I(
		long formInstanceId, String ipAddress) {

		return findByF_I(
			formInstanceId, ipAddress,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ddm form instance records where formInstanceId = &#63; and ipAddress = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceId the form instance ID
	 * @param ipAddress the ip address
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @return the range of matching ddm form instance records
	 */
	public default java.util.List<DDMFormInstanceRecord> findByF_I(
		long formInstanceId, String ipAddress, int start, int end) {

		return findByF_I(formInstanceId, ipAddress, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the ddm form instance records where formInstanceId = &#63; and ipAddress = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceId the form instance ID
	 * @param ipAddress the ip address
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instance records
	 */
	public default java.util.List<DDMFormInstanceRecord> findByF_I(
		long formInstanceId, String ipAddress, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMFormInstanceRecord>
			orderByComparator) {

		return findByF_I(
			formInstanceId, ipAddress, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:425908228