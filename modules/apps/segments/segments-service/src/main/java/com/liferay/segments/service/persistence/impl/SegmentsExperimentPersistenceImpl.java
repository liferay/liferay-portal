/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.segments.exception.NoSuchExperimentException;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.model.SegmentsExperimentTable;
import com.liferay.segments.model.impl.SegmentsExperimentImpl;
import com.liferay.segments.model.impl.SegmentsExperimentModelImpl;
import com.liferay.segments.service.persistence.SegmentsExperimentPersistence;
import com.liferay.segments.service.persistence.SegmentsExperimentUtil;
import com.liferay.segments.service.persistence.impl.constants.SegmentsPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the segments experiment service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Eduardo Garcia
 * @generated
 */
@Component(service = SegmentsExperimentPersistence.class)
public class SegmentsExperimentPersistenceImpl
	extends BasePersistenceImpl<SegmentsExperiment, NoSuchExperimentException>
	implements SegmentsExperimentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SegmentsExperimentUtil</code> to access the segments experiment persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SegmentsExperimentImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SegmentsExperiment, NoSuchExperimentException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the segments experiments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperimentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of segments experiments
	 * @param end the upper bound of the range of segments experiments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiments
	 */
	@Override
	public List<SegmentsExperiment> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SegmentsExperiment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first segments experiment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experiment
	 * @throws NoSuchExperimentException if a matching segments experiment could not be found
	 */
	@Override
	public SegmentsExperiment findByUuid_First(
			String uuid,
			OrderByComparator<SegmentsExperiment> orderByComparator)
		throws NoSuchExperimentException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first segments experiment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experiment, or <code>null</code> if a matching segments experiment could not be found
	 */
	@Override
	public SegmentsExperiment fetchByUuid_First(
		String uuid, OrderByComparator<SegmentsExperiment> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the segments experiments where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of segments experiments where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching segments experiments
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<SegmentsExperiment, NoSuchExperimentException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the segments experiment where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchExperimentException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching segments experiment
	 * @throws NoSuchExperimentException if a matching segments experiment could not be found
	 */
	@Override
	public SegmentsExperiment findByUUID_G(String uuid, long groupId)
		throws NoSuchExperimentException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the segments experiment where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experiment, or <code>null</code> if a matching segments experiment could not be found
	 */
	@Override
	public SegmentsExperiment fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the segments experiment where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the segments experiment that was removed
	 */
	@Override
	public SegmentsExperiment removeByUUID_G(String uuid, long groupId)
		throws NoSuchExperimentException {

		SegmentsExperiment segmentsExperiment = findByUUID_G(uuid, groupId);

		return remove(segmentsExperiment);
	}

	/**
	 * Returns the number of segments experiments where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching segments experiments
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<SegmentsExperiment, NoSuchExperimentException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the segments experiments where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperimentModelImpl</code>.
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
	@Override
	public List<SegmentsExperiment> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SegmentsExperiment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experiment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experiment
	 * @throws NoSuchExperimentException if a matching segments experiment could not be found
	 */
	@Override
	public SegmentsExperiment findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<SegmentsExperiment> orderByComparator)
		throws NoSuchExperimentException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first segments experiment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experiment, or <code>null</code> if a matching segments experiment could not be found
	 */
	@Override
	public SegmentsExperiment fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<SegmentsExperiment> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the segments experiments where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of segments experiments where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching segments experiments
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<SegmentsExperiment, NoSuchExperimentException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the segments experiments where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperimentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of segments experiments
	 * @param end the upper bound of the range of segments experiments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiments
	 */
	@Override
	public List<SegmentsExperiment> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SegmentsExperiment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first segments experiment in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experiment
	 * @throws NoSuchExperimentException if a matching segments experiment could not be found
	 */
	@Override
	public SegmentsExperiment findByGroupId_First(
			long groupId,
			OrderByComparator<SegmentsExperiment> orderByComparator)
		throws NoSuchExperimentException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first segments experiment in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experiment, or <code>null</code> if a matching segments experiment could not be found
	 */
	@Override
	public SegmentsExperiment fetchByGroupId_First(
		long groupId, OrderByComparator<SegmentsExperiment> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiments that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperimentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of segments experiments
	 * @param end the upper bound of the range of segments experiments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiments that the user has permission to view
	 */
	@Override
	public List<SegmentsExperiment> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SegmentsExperiment> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Removes all the segments experiments where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of segments experiments where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching segments experiments
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of segments experiments that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching segments experiments that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupId}, groupId);
	}

	private CollectionPersistenceFinder
		<SegmentsExperiment, NoSuchExperimentException>
			_collectionPersistenceFinderBySegmentsExperimentKey;

	/**
	 * Returns an ordered range of all the segments experiments where segmentsExperimentKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperimentModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsExperimentKey the segments experiment key
	 * @param start the lower bound of the range of segments experiments
	 * @param end the upper bound of the range of segments experiments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiments
	 */
	@Override
	public List<SegmentsExperiment> findBySegmentsExperimentKey(
		String segmentsExperimentKey, int start, int end,
		OrderByComparator<SegmentsExperiment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySegmentsExperimentKey.find(
			finderCache, new Object[] {segmentsExperimentKey}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experiment in the ordered set where segmentsExperimentKey = &#63;.
	 *
	 * @param segmentsExperimentKey the segments experiment key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experiment
	 * @throws NoSuchExperimentException if a matching segments experiment could not be found
	 */
	@Override
	public SegmentsExperiment findBySegmentsExperimentKey_First(
			String segmentsExperimentKey,
			OrderByComparator<SegmentsExperiment> orderByComparator)
		throws NoSuchExperimentException {

		return _collectionPersistenceFinderBySegmentsExperimentKey.findFirst(
			finderCache, new Object[] {segmentsExperimentKey},
			orderByComparator);
	}

	/**
	 * Returns the first segments experiment in the ordered set where segmentsExperimentKey = &#63;.
	 *
	 * @param segmentsExperimentKey the segments experiment key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experiment, or <code>null</code> if a matching segments experiment could not be found
	 */
	@Override
	public SegmentsExperiment fetchBySegmentsExperimentKey_First(
		String segmentsExperimentKey,
		OrderByComparator<SegmentsExperiment> orderByComparator) {

		return _collectionPersistenceFinderBySegmentsExperimentKey.fetchFirst(
			finderCache, new Object[] {segmentsExperimentKey},
			orderByComparator);
	}

	/**
	 * Removes all the segments experiments where segmentsExperimentKey = &#63; from the database.
	 *
	 * @param segmentsExperimentKey the segments experiment key
	 */
	@Override
	public void removeBySegmentsExperimentKey(String segmentsExperimentKey) {
		_collectionPersistenceFinderBySegmentsExperimentKey.remove(
			finderCache, new Object[] {segmentsExperimentKey});
	}

	/**
	 * Returns the number of segments experiments where segmentsExperimentKey = &#63;.
	 *
	 * @param segmentsExperimentKey the segments experiment key
	 * @return the number of matching segments experiments
	 */
	@Override
	public int countBySegmentsExperimentKey(String segmentsExperimentKey) {
		return _collectionPersistenceFinderBySegmentsExperimentKey.count(
			finderCache, new Object[] {segmentsExperimentKey});
	}

	private UniquePersistenceFinder
		<SegmentsExperiment, NoSuchExperimentException>
			_uniquePersistenceFinderByG_S;

	/**
	 * Returns the segments experiment where groupId = &#63; and segmentsExperimentKey = &#63; or throws a <code>NoSuchExperimentException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperimentKey the segments experiment key
	 * @return the matching segments experiment
	 * @throws NoSuchExperimentException if a matching segments experiment could not be found
	 */
	@Override
	public SegmentsExperiment findByG_S(
			long groupId, String segmentsExperimentKey)
		throws NoSuchExperimentException {

		return _uniquePersistenceFinderByG_S.find(
			finderCache, new Object[] {groupId, segmentsExperimentKey});
	}

	/**
	 * Returns the segments experiment where groupId = &#63; and segmentsExperimentKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperimentKey the segments experiment key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experiment, or <code>null</code> if a matching segments experiment could not be found
	 */
	@Override
	public SegmentsExperiment fetchByG_S(
		long groupId, String segmentsExperimentKey, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_S.fetch(
			finderCache, new Object[] {groupId, segmentsExperimentKey},
			useFinderCache);
	}

	/**
	 * Removes the segments experiment where groupId = &#63; and segmentsExperimentKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperimentKey the segments experiment key
	 * @return the segments experiment that was removed
	 */
	@Override
	public SegmentsExperiment removeByG_S(
			long groupId, String segmentsExperimentKey)
		throws NoSuchExperimentException {

		SegmentsExperiment segmentsExperiment = findByG_S(
			groupId, segmentsExperimentKey);

		return remove(segmentsExperiment);
	}

	/**
	 * Returns the number of segments experiments where groupId = &#63; and segmentsExperimentKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperimentKey the segments experiment key
	 * @return the number of matching segments experiments
	 */
	@Override
	public int countByG_S(long groupId, String segmentsExperimentKey) {
		return _uniquePersistenceFinderByG_S.count(
			finderCache, new Object[] {groupId, segmentsExperimentKey});
	}

	private UniquePersistenceFinder
		<SegmentsExperiment, NoSuchExperimentException>
			_uniquePersistenceFinderByG_S_P;

	/**
	 * Returns the segments experiment where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; or throws a <code>NoSuchExperimentException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @return the matching segments experiment
	 * @throws NoSuchExperimentException if a matching segments experiment could not be found
	 */
	@Override
	public SegmentsExperiment findByG_S_P(
			long groupId, long segmentsExperienceId, long plid)
		throws NoSuchExperimentException {

		return _uniquePersistenceFinderByG_S_P.find(
			finderCache, new Object[] {groupId, segmentsExperienceId, plid});
	}

	/**
	 * Returns the segments experiment where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experiment, or <code>null</code> if a matching segments experiment could not be found
	 */
	@Override
	public SegmentsExperiment fetchByG_S_P(
		long groupId, long segmentsExperienceId, long plid,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_S_P.fetch(
			finderCache, new Object[] {groupId, segmentsExperienceId, plid},
			useFinderCache);
	}

	/**
	 * Removes the segments experiment where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @return the segments experiment that was removed
	 */
	@Override
	public SegmentsExperiment removeByG_S_P(
			long groupId, long segmentsExperienceId, long plid)
		throws NoSuchExperimentException {

		SegmentsExperiment segmentsExperiment = findByG_S_P(
			groupId, segmentsExperienceId, plid);

		return remove(segmentsExperiment);
	}

	/**
	 * Returns the number of segments experiments where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @return the number of matching segments experiments
	 */
	@Override
	public int countByG_S_P(
		long groupId, long segmentsExperienceId, long plid) {

		return _uniquePersistenceFinderByG_S_P.count(
			finderCache, new Object[] {groupId, segmentsExperienceId, plid});
	}

	public SegmentsExperimentPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(SegmentsExperiment.class);

		setModelImplClass(SegmentsExperimentImpl.class);
		setModelPKClass(long.class);

		setTable(SegmentsExperimentTable.INSTANCE);
	}

	/**
	 * Creates a new segments experiment with the primary key. Does not add the segments experiment to the database.
	 *
	 * @param segmentsExperimentId the primary key for the new segments experiment
	 * @return the new segments experiment
	 */
	@Override
	public SegmentsExperiment create(long segmentsExperimentId) {
		SegmentsExperiment segmentsExperiment = new SegmentsExperimentImpl();

		segmentsExperiment.setNew(true);
		segmentsExperiment.setPrimaryKey(segmentsExperimentId);

		String uuid = PortalUUIDUtil.generate();

		segmentsExperiment.setUuid(uuid);

		segmentsExperiment.setCompanyId(CompanyThreadLocal.getCompanyId());

		return segmentsExperiment;
	}

	/**
	 * Removes the segments experiment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param segmentsExperimentId the primary key of the segments experiment
	 * @return the segments experiment that was removed
	 * @throws NoSuchExperimentException if a segments experiment with the primary key could not be found
	 */
	@Override
	public SegmentsExperiment remove(long segmentsExperimentId)
		throws NoSuchExperimentException {

		return remove((Serializable)segmentsExperimentId);
	}

	@Override
	protected SegmentsExperiment removeImpl(
		SegmentsExperiment segmentsExperiment) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(segmentsExperiment)) {
				segmentsExperiment = (SegmentsExperiment)session.get(
					SegmentsExperimentImpl.class,
					segmentsExperiment.getPrimaryKeyObj());
			}

			if ((segmentsExperiment != null) &&
				ctPersistenceHelper.isRemove(segmentsExperiment)) {

				session.delete(segmentsExperiment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (segmentsExperiment != null) {
			clearCache(segmentsExperiment);
		}

		return segmentsExperiment;
	}

	@Override
	public SegmentsExperiment updateImpl(
		SegmentsExperiment segmentsExperiment) {

		boolean isNew = segmentsExperiment.isNew();

		if (!(segmentsExperiment instanceof SegmentsExperimentModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(segmentsExperiment.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					segmentsExperiment);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in segmentsExperiment proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SegmentsExperiment implementation " +
					segmentsExperiment.getClass());
		}

		SegmentsExperimentModelImpl segmentsExperimentModelImpl =
			(SegmentsExperimentModelImpl)segmentsExperiment;

		if (Validator.isNull(segmentsExperiment.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			segmentsExperiment.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (segmentsExperiment.getCreateDate() == null)) {
			if (serviceContext == null) {
				segmentsExperiment.setCreateDate(date);
			}
			else {
				segmentsExperiment.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!segmentsExperimentModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				segmentsExperiment.setModifiedDate(date);
			}
			else {
				segmentsExperiment.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(segmentsExperiment)) {
				if (!isNew) {
					session.evict(
						SegmentsExperimentImpl.class,
						segmentsExperiment.getPrimaryKeyObj());
				}

				session.save(segmentsExperiment);
			}
			else {
				segmentsExperiment = (SegmentsExperiment)session.merge(
					segmentsExperiment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(segmentsExperiment, false);

		if (isNew) {
			segmentsExperiment.setNew(false);
		}

		segmentsExperiment.resetOriginalValues();

		return segmentsExperiment;
	}

	/**
	 * Returns the segments experiment with the primary key or throws a <code>NoSuchExperimentException</code> if it could not be found.
	 *
	 * @param segmentsExperimentId the primary key of the segments experiment
	 * @return the segments experiment
	 * @throws NoSuchExperimentException if a segments experiment with the primary key could not be found
	 */
	@Override
	public SegmentsExperiment findByPrimaryKey(long segmentsExperimentId)
		throws NoSuchExperimentException {

		return findByPrimaryKey((Serializable)segmentsExperimentId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the segments experiment with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param segmentsExperimentId the primary key of the segments experiment
	 * @return the segments experiment, or <code>null</code> if a segments experiment with the primary key could not be found
	 */
	@Override
	public SegmentsExperiment fetchByPrimaryKey(long segmentsExperimentId) {
		return fetchByPrimaryKey((Serializable)segmentsExperimentId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "segmentsExperimentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SEGMENTSEXPERIMENT;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return SegmentsExperimentModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "SegmentsExperiment";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("segmentsEntryId");
		ctMergeColumnNames.add("segmentsExperienceId");
		ctMergeColumnNames.add("segmentsExperimentKey");
		ctMergeColumnNames.add("plid");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("typeSettings");
		ctMergeColumnNames.add("status");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("segmentsExperimentId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "segmentsExperimentKey"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "segmentsExperienceId", "plid"});
	}

	/**
	 * Initializes the segments experiment persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_SEGMENTSEXPERIMENT_WHERE,
			_SQL_COUNT_SEGMENTSEXPERIMENT_WHERE,
			SegmentsExperimentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"segmentsExperiment.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				SegmentsExperiment::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(SegmentsExperiment::getUuid),
				SegmentsExperiment::getGroupId),
			_SQL_SELECT_SEGMENTSEXPERIMENT_WHERE, "",
			new FinderColumn<>(
				"segmentsExperiment.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				SegmentsExperiment::getUuid),
			new FinderColumn<>(
				"segmentsExperiment.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, SegmentsExperiment::getGroupId));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_SEGMENTSEXPERIMENT_WHERE,
				_SQL_COUNT_SEGMENTSEXPERIMENT_WHERE,
				SegmentsExperimentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"segmentsExperiment.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					SegmentsExperiment::getUuid),
				new FinderColumn<>(
					"segmentsExperiment.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, SegmentsExperiment::getCompanyId));

		_collectionPersistenceFinderByGroupId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_SEGMENTSEXPERIMENT_WHERE,
				_SQL_COUNT_SEGMENTSEXPERIMENT_WHERE,
				SegmentsExperimentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"segmentsExperiment.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, SegmentsExperiment::getGroupId));

		_collectionPersistenceFinderBySegmentsExperimentKey =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findBySegmentsExperimentKey",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"segmentsExperimentKey"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findBySegmentsExperimentKey",
					new String[] {String.class.getName()},
					new String[] {"segmentsExperimentKey"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countBySegmentsExperimentKey",
					new String[] {String.class.getName()},
					new String[] {"segmentsExperimentKey"}, 0, 1, false, null),
				_SQL_SELECT_SEGMENTSEXPERIMENT_WHERE,
				_SQL_COUNT_SEGMENTSEXPERIMENT_WHERE,
				SegmentsExperimentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"segmentsExperiment.", "segmentsExperimentKey",
					FinderColumn.Type.STRING, "=", true, true,
					SegmentsExperiment::getSegmentsExperimentKey));

		_uniquePersistenceFinderByG_S = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_S",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "segmentsExperimentKey"}, 0, 2, false,
				SegmentsExperiment::getGroupId,
				convertNullFunction(
					SegmentsExperiment::getSegmentsExperimentKey)),
			_SQL_SELECT_SEGMENTSEXPERIMENT_WHERE, "",
			new FinderColumn<>(
				"segmentsExperiment.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, SegmentsExperiment::getGroupId),
			new FinderColumn<>(
				"segmentsExperiment.", "segmentsExperimentKey",
				FinderColumn.Type.STRING, "=", true, true,
				SegmentsExperiment::getSegmentsExperimentKey));

		_uniquePersistenceFinderByG_S_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_S_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "segmentsExperienceId", "plid"}, 0, 0,
				false, SegmentsExperiment::getGroupId,
				SegmentsExperiment::getSegmentsExperienceId,
				SegmentsExperiment::getPlid),
			_SQL_SELECT_SEGMENTSEXPERIMENT_WHERE, "",
			new FinderColumn<>(
				"segmentsExperiment.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, SegmentsExperiment::getGroupId),
			new FinderColumn<>(
				"segmentsExperiment.", "segmentsExperienceId",
				FinderColumn.Type.LONG, "=", true, true,
				SegmentsExperiment::getSegmentsExperienceId),
			new FinderColumn<>(
				"segmentsExperiment.", "plid", FinderColumn.Type.LONG, "=",
				true, true, SegmentsExperiment::getPlid));

		SegmentsExperimentUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SegmentsExperimentUtil.setPersistence(null);

		entityCache.removeCache(SegmentsExperimentImpl.class.getName());
	}

	@Override
	@Reference(
		target = SegmentsPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SegmentsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SegmentsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		SegmentsExperimentModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SEGMENTSEXPERIMENT =
		"SELECT segmentsExperiment FROM SegmentsExperiment segmentsExperiment";

	private static final String _SQL_SELECT_SEGMENTSEXPERIMENT_WHERE =
		"SELECT segmentsExperiment FROM SegmentsExperiment segmentsExperiment WHERE ";

	private static final String _SQL_COUNT_SEGMENTSEXPERIMENT_WHERE =
		"SELECT COUNT(segmentsExperiment) FROM SegmentsExperiment segmentsExperiment WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SegmentsExperiment exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsExperimentPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:96438730