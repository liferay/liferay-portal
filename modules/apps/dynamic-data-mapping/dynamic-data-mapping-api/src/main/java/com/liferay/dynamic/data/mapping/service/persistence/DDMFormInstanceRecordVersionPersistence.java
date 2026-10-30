/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence;

import com.liferay.dynamic.data.mapping.exception.NoSuchFormInstanceRecordVersionException;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the ddm form instance record version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDMFormInstanceRecordVersionUtil
 * @generated
 */
@ProviderType
public interface DDMFormInstanceRecordVersionPersistence
	extends BasePersistence<DDMFormInstanceRecordVersion>,
			CTPersistence<DDMFormInstanceRecordVersion> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link DDMFormInstanceRecordVersionUtil} to access the ddm form instance record version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the ddm form instance record versions where formInstanceRecordId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance record versions
	 */
	public java.util.List<DDMFormInstanceRecordVersion>
		findByFormInstanceRecordId(
			long formInstanceRecordId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<DDMFormInstanceRecordVersion> orderByComparator,
			boolean useFinderCache);

	/**
	 * Returns the first ddm form instance record version in the ordered set where formInstanceRecordId = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version
	 * @throws NoSuchFormInstanceRecordVersionException if a matching ddm form instance record version could not be found
	 */
	public DDMFormInstanceRecordVersion findByFormInstanceRecordId_First(
			long formInstanceRecordId,
			com.liferay.portal.kernel.util.OrderByComparator
				<DDMFormInstanceRecordVersion> orderByComparator)
		throws NoSuchFormInstanceRecordVersionException;

	/**
	 * Returns the first ddm form instance record version in the ordered set where formInstanceRecordId = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version, or <code>null</code> if a matching ddm form instance record version could not be found
	 */
	public DDMFormInstanceRecordVersion fetchByFormInstanceRecordId_First(
		long formInstanceRecordId,
		com.liferay.portal.kernel.util.OrderByComparator
			<DDMFormInstanceRecordVersion> orderByComparator);

	/**
	 * Removes all the ddm form instance record versions where formInstanceRecordId = &#63; from the database.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 */
	public void removeByFormInstanceRecordId(long formInstanceRecordId);

	/**
	 * Returns the number of ddm form instance record versions where formInstanceRecordId = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @return the number of matching ddm form instance record versions
	 */
	public int countByFormInstanceRecordId(long formInstanceRecordId);

	/**
	 * Returns an ordered range of all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance record versions
	 */
	public java.util.List<DDMFormInstanceRecordVersion> findByU_F(
		long userId, long formInstanceId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<DDMFormInstanceRecordVersion> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm form instance record version in the ordered set where userId = &#63; and formInstanceId = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version
	 * @throws NoSuchFormInstanceRecordVersionException if a matching ddm form instance record version could not be found
	 */
	public DDMFormInstanceRecordVersion findByU_F_First(
			long userId, long formInstanceId,
			com.liferay.portal.kernel.util.OrderByComparator
				<DDMFormInstanceRecordVersion> orderByComparator)
		throws NoSuchFormInstanceRecordVersionException;

	/**
	 * Returns the first ddm form instance record version in the ordered set where userId = &#63; and formInstanceId = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version, or <code>null</code> if a matching ddm form instance record version could not be found
	 */
	public DDMFormInstanceRecordVersion fetchByU_F_First(
		long userId, long formInstanceId,
		com.liferay.portal.kernel.util.OrderByComparator
			<DDMFormInstanceRecordVersion> orderByComparator);

	/**
	 * Removes all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 */
	public void removeByU_F(long userId, long formInstanceId);

	/**
	 * Returns the number of ddm form instance record versions where userId = &#63; and formInstanceId = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @return the number of matching ddm form instance record versions
	 */
	public int countByU_F(long userId, long formInstanceId);

	/**
	 * Returns the ddm form instance record version where formInstanceRecordId = &#63; and version = &#63; or throws a <code>NoSuchFormInstanceRecordVersionException</code> if it could not be found.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param version the version
	 * @return the matching ddm form instance record version
	 * @throws NoSuchFormInstanceRecordVersionException if a matching ddm form instance record version could not be found
	 */
	public DDMFormInstanceRecordVersion findByF_V(
			long formInstanceRecordId, String version)
		throws NoSuchFormInstanceRecordVersionException;

	/**
	 * Returns the ddm form instance record version where formInstanceRecordId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm form instance record version, or <code>null</code> if a matching ddm form instance record version could not be found
	 */
	public DDMFormInstanceRecordVersion fetchByF_V(
		long formInstanceRecordId, String version, boolean useFinderCache);

	/**
	 * Removes the ddm form instance record version where formInstanceRecordId = &#63; and version = &#63; from the database.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param version the version
	 * @return the ddm form instance record version that was removed
	 */
	public DDMFormInstanceRecordVersion removeByF_V(
			long formInstanceRecordId, String version)
		throws NoSuchFormInstanceRecordVersionException;

	/**
	 * Returns the number of ddm form instance record versions where formInstanceRecordId = &#63; and version = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param version the version
	 * @return the number of matching ddm form instance record versions
	 */
	public int countByF_V(long formInstanceRecordId, String version);

	/**
	 * Returns an ordered range of all the ddm form instance record versions where formInstanceRecordId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance record versions
	 */
	public java.util.List<DDMFormInstanceRecordVersion> findByF_S(
		long formInstanceRecordId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<DDMFormInstanceRecordVersion> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm form instance record version in the ordered set where formInstanceRecordId = &#63; and status = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version
	 * @throws NoSuchFormInstanceRecordVersionException if a matching ddm form instance record version could not be found
	 */
	public DDMFormInstanceRecordVersion findByF_S_First(
			long formInstanceRecordId, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<DDMFormInstanceRecordVersion> orderByComparator)
		throws NoSuchFormInstanceRecordVersionException;

	/**
	 * Returns the first ddm form instance record version in the ordered set where formInstanceRecordId = &#63; and status = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version, or <code>null</code> if a matching ddm form instance record version could not be found
	 */
	public DDMFormInstanceRecordVersion fetchByF_S_First(
		long formInstanceRecordId, int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<DDMFormInstanceRecordVersion> orderByComparator);

	/**
	 * Removes all the ddm form instance record versions where formInstanceRecordId = &#63; and status = &#63; from the database.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 */
	public void removeByF_S(long formInstanceRecordId, int status);

	/**
	 * Returns the number of ddm form instance record versions where formInstanceRecordId = &#63; and status = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 * @return the number of matching ddm form instance record versions
	 */
	public int countByF_S(long formInstanceRecordId, int status);

	/**
	 * Returns an ordered range of all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance record versions
	 */
	public java.util.List<DDMFormInstanceRecordVersion> findByU_F_F_S(
		long userId, long formInstanceId, String formInstanceVersion,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<DDMFormInstanceRecordVersion> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm form instance record version in the ordered set where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version
	 * @throws NoSuchFormInstanceRecordVersionException if a matching ddm form instance record version could not be found
	 */
	public DDMFormInstanceRecordVersion findByU_F_F_S_First(
			long userId, long formInstanceId, String formInstanceVersion,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<DDMFormInstanceRecordVersion> orderByComparator)
		throws NoSuchFormInstanceRecordVersionException;

	/**
	 * Returns the first ddm form instance record version in the ordered set where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version, or <code>null</code> if a matching ddm form instance record version could not be found
	 */
	public DDMFormInstanceRecordVersion fetchByU_F_F_S_First(
		long userId, long formInstanceId, String formInstanceVersion,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<DDMFormInstanceRecordVersion> orderByComparator);

	/**
	 * Removes all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 */
	public void removeByU_F_F_S(
		long userId, long formInstanceId, String formInstanceVersion,
		int status);

	/**
	 * Returns the number of ddm form instance record versions where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 * @return the number of matching ddm form instance record versions
	 */
	public int countByU_F_F_S(
		long userId, long formInstanceId, String formInstanceVersion,
		int status);

	/**
	 * Creates a new ddm form instance record version with the primary key. Does not add the ddm form instance record version to the database.
	 *
	 * @param formInstanceRecordVersionId the primary key for the new ddm form instance record version
	 * @return the new ddm form instance record version
	 */
	public DDMFormInstanceRecordVersion create(
		long formInstanceRecordVersionId);

	/**
	 * Removes the ddm form instance record version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param formInstanceRecordVersionId the primary key of the ddm form instance record version
	 * @return the ddm form instance record version that was removed
	 * @throws NoSuchFormInstanceRecordVersionException if a ddm form instance record version with the primary key could not be found
	 */
	public DDMFormInstanceRecordVersion remove(long formInstanceRecordVersionId)
		throws NoSuchFormInstanceRecordVersionException;

	public DDMFormInstanceRecordVersion updateImpl(
		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion);

	/**
	 * Returns the ddm form instance record version with the primary key or throws a <code>NoSuchFormInstanceRecordVersionException</code> if it could not be found.
	 *
	 * @param formInstanceRecordVersionId the primary key of the ddm form instance record version
	 * @return the ddm form instance record version
	 * @throws NoSuchFormInstanceRecordVersionException if a ddm form instance record version with the primary key could not be found
	 */
	public DDMFormInstanceRecordVersion findByPrimaryKey(
			long formInstanceRecordVersionId)
		throws NoSuchFormInstanceRecordVersionException;

	/**
	 * Returns the ddm form instance record version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param formInstanceRecordVersionId the primary key of the ddm form instance record version
	 * @return the ddm form instance record version, or <code>null</code> if a ddm form instance record version with the primary key could not be found
	 */
	public DDMFormInstanceRecordVersion fetchByPrimaryKey(
		long formInstanceRecordVersionId);

	/**
	 * Returns the ddm form instance record version where formInstanceRecordId = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param version the version
	 * @return the matching ddm form instance record version, or <code>null</code> if a matching ddm form instance record version could not be found
	 */
	public default DDMFormInstanceRecordVersion fetchByF_V(
		long formInstanceRecordId, String version) {

		return fetchByF_V(formInstanceRecordId, version, true);
	}

	/**
	 * Returns all the ddm form instance record versions where formInstanceRecordId = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @return the matching ddm form instance record versions
	 */
	public default java.util.List<DDMFormInstanceRecordVersion>
		findByFormInstanceRecordId(long formInstanceRecordId) {

		return findByFormInstanceRecordId(
			formInstanceRecordId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ddm form instance record versions where formInstanceRecordId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @return the range of matching ddm form instance record versions
	 */
	public default java.util.List<DDMFormInstanceRecordVersion>
		findByFormInstanceRecordId(
			long formInstanceRecordId, int start, int end) {

		return findByFormInstanceRecordId(
			formInstanceRecordId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the ddm form instance record versions where formInstanceRecordId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instance record versions
	 */
	public default java.util.List<DDMFormInstanceRecordVersion>
		findByFormInstanceRecordId(
			long formInstanceRecordId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<DDMFormInstanceRecordVersion> orderByComparator) {

		return findByFormInstanceRecordId(
			formInstanceRecordId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @return the matching ddm form instance record versions
	 */
	public default java.util.List<DDMFormInstanceRecordVersion> findByU_F(
		long userId, long formInstanceId) {

		return findByU_F(
			userId, formInstanceId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @return the range of matching ddm form instance record versions
	 */
	public default java.util.List<DDMFormInstanceRecordVersion> findByU_F(
		long userId, long formInstanceId, int start, int end) {

		return findByU_F(userId, formInstanceId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instance record versions
	 */
	public default java.util.List<DDMFormInstanceRecordVersion> findByU_F(
		long userId, long formInstanceId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<DDMFormInstanceRecordVersion> orderByComparator) {

		return findByU_F(
			userId, formInstanceId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the ddm form instance record versions where formInstanceRecordId = &#63; and status = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 * @return the matching ddm form instance record versions
	 */
	public default java.util.List<DDMFormInstanceRecordVersion> findByF_S(
		long formInstanceRecordId, int status) {

		return findByF_S(
			formInstanceRecordId, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ddm form instance record versions where formInstanceRecordId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @return the range of matching ddm form instance record versions
	 */
	public default java.util.List<DDMFormInstanceRecordVersion> findByF_S(
		long formInstanceRecordId, int status, int start, int end) {

		return findByF_S(formInstanceRecordId, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the ddm form instance record versions where formInstanceRecordId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instance record versions
	 */
	public default java.util.List<DDMFormInstanceRecordVersion> findByF_S(
		long formInstanceRecordId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<DDMFormInstanceRecordVersion> orderByComparator) {

		return findByF_S(
			formInstanceRecordId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 * @return the matching ddm form instance record versions
	 */
	public default java.util.List<DDMFormInstanceRecordVersion> findByU_F_F_S(
		long userId, long formInstanceId, String formInstanceVersion,
		int status) {

		return findByU_F_F_S(
			userId, formInstanceId, formInstanceVersion, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @return the range of matching ddm form instance record versions
	 */
	public default java.util.List<DDMFormInstanceRecordVersion> findByU_F_F_S(
		long userId, long formInstanceId, String formInstanceVersion,
		int status, int start, int end) {

		return findByU_F_F_S(
			userId, formInstanceId, formInstanceVersion, status, start, end,
			null, true);
	}

	/**
	 * Returns an ordered range of all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instance record versions
	 */
	public default java.util.List<DDMFormInstanceRecordVersion> findByU_F_F_S(
		long userId, long formInstanceId, String formInstanceVersion,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<DDMFormInstanceRecordVersion> orderByComparator) {

		return findByU_F_F_S(
			userId, formInstanceId, formInstanceVersion, status, start, end,
			orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1374509645