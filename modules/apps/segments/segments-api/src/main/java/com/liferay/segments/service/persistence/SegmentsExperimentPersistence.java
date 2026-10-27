/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.segments.exception.NoSuchExperimentException;
import com.liferay.segments.model.SegmentsExperiment;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the segments experiment service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Eduardo Garcia
 * @see SegmentsExperimentUtil
 * @generated
 */
@ProviderType
public interface SegmentsExperimentPersistence
	extends BasePersistence<SegmentsExperiment>,
			CTPersistence<SegmentsExperiment> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link SegmentsExperimentUtil} to access the segments experiment persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the segments experiments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperimentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of segments experiments
	 * @param end the upper bound of the range of segments experiments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiments
	 */
	public java.util.List<SegmentsExperiment> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperiment>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first segments experiment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experiment
	 * @throws NoSuchExperimentException if a matching segments experiment could not be found
	 */
	public SegmentsExperiment findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperiment>
				orderByComparator)
		throws NoSuchExperimentException;

	/**
	 * Returns the first segments experiment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experiment, or <code>null</code> if a matching segments experiment could not be found
	 */
	public SegmentsExperiment fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperiment>
			orderByComparator);

	/**
	 * Removes all the segments experiments where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of segments experiments where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching segments experiments
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the segments experiment where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchExperimentException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching segments experiment
	 * @throws NoSuchExperimentException if a matching segments experiment could not be found
	 */
	public SegmentsExperiment findByUUID_G(String uuid, long groupId)
		throws NoSuchExperimentException;

	/**
	 * Returns the segments experiment where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experiment, or <code>null</code> if a matching segments experiment could not be found
	 */
	public SegmentsExperiment fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the segments experiment where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the segments experiment that was removed
	 */
	public SegmentsExperiment removeByUUID_G(String uuid, long groupId)
		throws NoSuchExperimentException;

	/**
	 * Returns the number of segments experiments where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching segments experiments
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the segments experiments where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperimentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of segments experiments
	 * @param end the upper bound of the range of segments experiments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiments
	 */
	public java.util.List<SegmentsExperiment> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperiment>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first segments experiment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experiment
	 * @throws NoSuchExperimentException if a matching segments experiment could not be found
	 */
	public SegmentsExperiment findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperiment>
				orderByComparator)
		throws NoSuchExperimentException;

	/**
	 * Returns the first segments experiment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experiment, or <code>null</code> if a matching segments experiment could not be found
	 */
	public SegmentsExperiment fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperiment>
			orderByComparator);

	/**
	 * Removes all the segments experiments where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of segments experiments where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching segments experiments
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the segments experiments where segmentsExperimentKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperimentModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsExperimentKey the segments experiment key
	 * @param start the lower bound of the range of segments experiments
	 * @param end the upper bound of the range of segments experiments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiments
	 */
	public java.util.List<SegmentsExperiment> findBySegmentsExperimentKey(
		String segmentsExperimentKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperiment>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first segments experiment in the ordered set where segmentsExperimentKey = &#63;.
	 *
	 * @param segmentsExperimentKey the segments experiment key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experiment
	 * @throws NoSuchExperimentException if a matching segments experiment could not be found
	 */
	public SegmentsExperiment findBySegmentsExperimentKey_First(
			String segmentsExperimentKey,
			com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperiment>
				orderByComparator)
		throws NoSuchExperimentException;

	/**
	 * Returns the first segments experiment in the ordered set where segmentsExperimentKey = &#63;.
	 *
	 * @param segmentsExperimentKey the segments experiment key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experiment, or <code>null</code> if a matching segments experiment could not be found
	 */
	public SegmentsExperiment fetchBySegmentsExperimentKey_First(
		String segmentsExperimentKey,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperiment>
			orderByComparator);

	/**
	 * Removes all the segments experiments where segmentsExperimentKey = &#63; from the database.
	 *
	 * @param segmentsExperimentKey the segments experiment key
	 */
	public void removeBySegmentsExperimentKey(String segmentsExperimentKey);

	/**
	 * Returns the number of segments experiments where segmentsExperimentKey = &#63;.
	 *
	 * @param segmentsExperimentKey the segments experiment key
	 * @return the number of matching segments experiments
	 */
	public int countBySegmentsExperimentKey(String segmentsExperimentKey);

	/**
	 * Returns the segments experiment where groupId = &#63; and segmentsExperimentKey = &#63; or throws a <code>NoSuchExperimentException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperimentKey the segments experiment key
	 * @return the matching segments experiment
	 * @throws NoSuchExperimentException if a matching segments experiment could not be found
	 */
	public SegmentsExperiment findByG_S(
			long groupId, String segmentsExperimentKey)
		throws NoSuchExperimentException;

	/**
	 * Returns the segments experiment where groupId = &#63; and segmentsExperimentKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperimentKey the segments experiment key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experiment, or <code>null</code> if a matching segments experiment could not be found
	 */
	public SegmentsExperiment fetchByG_S(
		long groupId, String segmentsExperimentKey, boolean useFinderCache);

	/**
	 * Removes the segments experiment where groupId = &#63; and segmentsExperimentKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperimentKey the segments experiment key
	 * @return the segments experiment that was removed
	 */
	public SegmentsExperiment removeByG_S(
			long groupId, String segmentsExperimentKey)
		throws NoSuchExperimentException;

	/**
	 * Returns the number of segments experiments where groupId = &#63; and segmentsExperimentKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperimentKey the segments experiment key
	 * @return the number of matching segments experiments
	 */
	public int countByG_S(long groupId, String segmentsExperimentKey);

	/**
	 * Returns the segments experiment where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; or throws a <code>NoSuchExperimentException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @return the matching segments experiment
	 * @throws NoSuchExperimentException if a matching segments experiment could not be found
	 */
	public SegmentsExperiment findByG_S_P(
			long groupId, long segmentsExperienceId, long plid)
		throws NoSuchExperimentException;

	/**
	 * Returns the segments experiment where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experiment, or <code>null</code> if a matching segments experiment could not be found
	 */
	public SegmentsExperiment fetchByG_S_P(
		long groupId, long segmentsExperienceId, long plid,
		boolean useFinderCache);

	/**
	 * Removes the segments experiment where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @return the segments experiment that was removed
	 */
	public SegmentsExperiment removeByG_S_P(
			long groupId, long segmentsExperienceId, long plid)
		throws NoSuchExperimentException;

	/**
	 * Returns the number of segments experiments where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @return the number of matching segments experiments
	 */
	public int countByG_S_P(long groupId, long segmentsExperienceId, long plid);

	/**
	 * Creates a new segments experiment with the primary key. Does not add the segments experiment to the database.
	 *
	 * @param segmentsExperimentId the primary key for the new segments experiment
	 * @return the new segments experiment
	 */
	public SegmentsExperiment create(long segmentsExperimentId);

	/**
	 * Removes the segments experiment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param segmentsExperimentId the primary key of the segments experiment
	 * @return the segments experiment that was removed
	 * @throws NoSuchExperimentException if a segments experiment with the primary key could not be found
	 */
	public SegmentsExperiment remove(long segmentsExperimentId)
		throws NoSuchExperimentException;

	public SegmentsExperiment updateImpl(SegmentsExperiment segmentsExperiment);

	/**
	 * Returns the segments experiment with the primary key or throws a <code>NoSuchExperimentException</code> if it could not be found.
	 *
	 * @param segmentsExperimentId the primary key of the segments experiment
	 * @return the segments experiment
	 * @throws NoSuchExperimentException if a segments experiment with the primary key could not be found
	 */
	public SegmentsExperiment findByPrimaryKey(long segmentsExperimentId)
		throws NoSuchExperimentException;

	/**
	 * Returns the segments experiment with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param segmentsExperimentId the primary key of the segments experiment
	 * @return the segments experiment, or <code>null</code> if a segments experiment with the primary key could not be found
	 */
	public SegmentsExperiment fetchByPrimaryKey(long segmentsExperimentId);

	/**
	 * Returns the segments experiment where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching segments experiment, or <code>null</code> if a matching segments experiment could not be found
	 */
	public default SegmentsExperiment fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the segments experiment where groupId = &#63; and segmentsExperimentKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperimentKey the segments experiment key
	 * @return the matching segments experiment, or <code>null</code> if a matching segments experiment could not be found
	 */
	public default SegmentsExperiment fetchByG_S(
		long groupId, String segmentsExperimentKey) {

		return fetchByG_S(groupId, segmentsExperimentKey, true);
	}

	/**
	 * Returns the segments experiment where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @return the matching segments experiment, or <code>null</code> if a matching segments experiment could not be found
	 */
	public default SegmentsExperiment fetchByG_S_P(
		long groupId, long segmentsExperienceId, long plid) {

		return fetchByG_S_P(groupId, segmentsExperienceId, plid, true);
	}

	/**
	 * Returns all the segments experiments where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching segments experiments
	 */
	public default java.util.List<SegmentsExperiment> findByUuid(String uuid) {
		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the segments experiments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperimentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of segments experiments
	 * @param end the upper bound of the range of segments experiments (not inclusive)
	 * @return the range of matching segments experiments
	 */
	public default java.util.List<SegmentsExperiment> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the segments experiments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperimentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of segments experiments
	 * @param end the upper bound of the range of segments experiments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiments
	 */
	public default java.util.List<SegmentsExperiment> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperiment>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the segments experiments where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching segments experiments
	 */
	public default java.util.List<SegmentsExperiment> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the segments experiments where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperimentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of segments experiments
	 * @param end the upper bound of the range of segments experiments (not inclusive)
	 * @return the range of matching segments experiments
	 */
	public default java.util.List<SegmentsExperiment> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the segments experiments where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperimentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of segments experiments
	 * @param end the upper bound of the range of segments experiments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiments
	 */
	public default java.util.List<SegmentsExperiment> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperiment>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the segments experiments where segmentsExperimentKey = &#63;.
	 *
	 * @param segmentsExperimentKey the segments experiment key
	 * @return the matching segments experiments
	 */
	public default java.util.List<SegmentsExperiment>
		findBySegmentsExperimentKey(String segmentsExperimentKey) {

		return findBySegmentsExperimentKey(
			segmentsExperimentKey,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the segments experiments where segmentsExperimentKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperimentModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsExperimentKey the segments experiment key
	 * @param start the lower bound of the range of segments experiments
	 * @param end the upper bound of the range of segments experiments (not inclusive)
	 * @return the range of matching segments experiments
	 */
	public default java.util.List<SegmentsExperiment>
		findBySegmentsExperimentKey(
			String segmentsExperimentKey, int start, int end) {

		return findBySegmentsExperimentKey(
			segmentsExperimentKey, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the segments experiments where segmentsExperimentKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperimentModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsExperimentKey the segments experiment key
	 * @param start the lower bound of the range of segments experiments
	 * @param end the upper bound of the range of segments experiments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiments
	 */
	public default java.util.List<SegmentsExperiment>
		findBySegmentsExperimentKey(
			String segmentsExperimentKey, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperiment>
				orderByComparator) {

		return findBySegmentsExperimentKey(
			segmentsExperimentKey, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1527689202