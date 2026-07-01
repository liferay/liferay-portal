/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.segments.exception.DuplicateSegmentsExperienceExternalReferenceCodeException;
import com.liferay.segments.exception.NoSuchExperienceException;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperienceTable;
import com.liferay.segments.model.impl.SegmentsExperienceImpl;
import com.liferay.segments.model.impl.SegmentsExperienceModelImpl;
import com.liferay.segments.service.persistence.SegmentsExperiencePersistence;
import com.liferay.segments.service.persistence.SegmentsExperienceUtil;
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
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the segments experience service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Eduardo Garcia
 * @generated
 */
@Component(service = SegmentsExperiencePersistence.class)
public class SegmentsExperiencePersistenceImpl
	extends BasePersistenceImpl<SegmentsExperience, NoSuchExperienceException>
	implements SegmentsExperiencePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SegmentsExperienceUtil</code> to access the segments experience persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SegmentsExperienceImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SegmentsExperience, NoSuchExperienceException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the segments experiences where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first segments experience in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByUuid_First(
			String uuid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByUuid_First(
		String uuid, OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the segments experiences where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of segments experiences where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<SegmentsExperience, NoSuchExperienceException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the segments experience where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByUUID_G(String uuid, long groupId)
		throws NoSuchExperienceException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the segments experience where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the segments experience where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the segments experience that was removed
	 */
	@Override
	public SegmentsExperience removeByUUID_G(String uuid, long groupId)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = findByUUID_G(uuid, groupId);

		return remove(segmentsExperience);
	}

	/**
	 * Returns the number of segments experiences where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<SegmentsExperience, NoSuchExperienceException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the segments experiences where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the segments experiences where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of segments experiences where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<SegmentsExperience, NoSuchExperienceException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByGroupId_First(
			long groupId,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByGroupId_First(
		long groupId, OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupId}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<SegmentsExperience, NoSuchExperienceException>
			_collectionPersistenceFinderByG_P;

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P(
		long groupId, long plid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P.find(
			finderCache, new Object[] {groupId, plid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_P_First(
			long groupId, long plid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		return _collectionPersistenceFinderByG_P.findFirst(
			finderCache, new Object[] {groupId, plid}, orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_P_First(
		long groupId, long plid,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByG_P.fetchFirst(
			finderCache, new Object[] {groupId, plid}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P(
		long groupId, long plid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByG_P.filterFind(
			finderCache, new Object[] {groupId, plid}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 */
	@Override
	public void removeByG_P(long groupId, long plid) {
		_collectionPersistenceFinderByG_P.remove(
			finderCache, new Object[] {groupId, plid});
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_P(long groupId, long plid) {
		return _collectionPersistenceFinderByG_P.count(
			finderCache, new Object[] {groupId, plid});
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(long groupId, long plid) {
		return _collectionPersistenceFinderByG_P.filterCount(
			finderCache, new Object[] {groupId, plid}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<SegmentsExperience, NoSuchExperienceException>
			_collectionPersistenceFinderByG_A;

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_A(
		long groupId, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_A.find(
			finderCache, new Object[] {new long[] {groupId}, active}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_A_First(
			long groupId, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByG_A_First(
			groupId, active, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_A_First(
		long groupId, boolean active,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByG_A.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}, active},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_A(
		long groupId, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByG_A.filterFind(
			finderCache, new Object[] {new long[] {groupId}, active}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permission to view where groupId = any &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_A(
		long[] groupIds, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_A.filterFind(
			finderCache, new Object[] {groupIds, active}, start, end,
			orderByComparator, groupIds);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and active = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_A(
		long[] groupIds, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_A.find(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), active}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 */
	@Override
	public void removeByG_A(long groupId, boolean active) {
		_collectionPersistenceFinderByG_A.remove(
			finderCache, new Object[] {new long[] {groupId}, active});
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_A(long groupId, boolean active) {
		return _collectionPersistenceFinderByG_A.count(
			finderCache, new Object[] {new long[] {groupId}, active});
	}

	/**
	 * Returns the number of segments experiences where groupId = any &#63; and active = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_A(long[] groupIds, boolean active) {
		return _collectionPersistenceFinderByG_A.count(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), active});
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	@Override
	public int filterCountByG_A(long groupId, boolean active) {
		return _collectionPersistenceFinderByG_A.filterCount(
			finderCache, new Object[] {new long[] {groupId}, active}, groupId);
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = any &#63; and active = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	@Override
	public int filterCountByG_A(long[] groupIds, boolean active) {
		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_A.filterCount(
			finderCache, new Object[] {groupIds, active}, groupIds);
	}

	private CollectionPersistenceFinder
		<SegmentsExperience, NoSuchExperienceException>
			_collectionPersistenceFinderBySEERC_SESERC;

	/**
	 * Returns an ordered range of all the segments experiences where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findBySEERC_SESERC(
		String segmentsEntryERC, String segmentsEntryScopeERC, int start,
		int end, OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySEERC_SESERC.find(
			finderCache, new Object[] {segmentsEntryERC, segmentsEntryScopeERC},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience in the ordered set where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findBySEERC_SESERC_First(
			String segmentsEntryERC, String segmentsEntryScopeERC,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		return _collectionPersistenceFinderBySEERC_SESERC.findFirst(
			finderCache, new Object[] {segmentsEntryERC, segmentsEntryScopeERC},
			orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchBySEERC_SESERC_First(
		String segmentsEntryERC, String segmentsEntryScopeERC,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderBySEERC_SESERC.fetchFirst(
			finderCache, new Object[] {segmentsEntryERC, segmentsEntryScopeERC},
			orderByComparator);
	}

	/**
	 * Removes all the segments experiences where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; from the database.
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 */
	@Override
	public void removeBySEERC_SESERC(
		String segmentsEntryERC, String segmentsEntryScopeERC) {

		_collectionPersistenceFinderBySEERC_SESERC.remove(
			finderCache,
			new Object[] {segmentsEntryERC, segmentsEntryScopeERC});
	}

	/**
	 * Returns the number of segments experiences where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countBySEERC_SESERC(
		String segmentsEntryERC, String segmentsEntryScopeERC) {

		return _collectionPersistenceFinderBySEERC_SESERC.count(
			finderCache,
			new Object[] {segmentsEntryERC, segmentsEntryScopeERC});
	}

	private FilterCollectionPersistenceFinder
		<SegmentsExperience, NoSuchExperienceException>
			_collectionPersistenceFinderByG_SEERC_SESERC;

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_SEERC_SESERC.find(
			finderCache,
			new Object[] {groupId, segmentsEntryERC, segmentsEntryScopeERC},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_SEERC_SESERC_First(
			long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		return _collectionPersistenceFinderByG_SEERC_SESERC.findFirst(
			finderCache,
			new Object[] {groupId, segmentsEntryERC, segmentsEntryScopeERC},
			orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_SEERC_SESERC_First(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByG_SEERC_SESERC.fetchFirst(
			finderCache,
			new Object[] {groupId, segmentsEntryERC, segmentsEntryScopeERC},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByG_SEERC_SESERC.filterFind(
			finderCache,
			new Object[] {groupId, segmentsEntryERC, segmentsEntryScopeERC},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 */
	@Override
	public void removeByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC) {

		_collectionPersistenceFinderByG_SEERC_SESERC.remove(
			finderCache,
			new Object[] {groupId, segmentsEntryERC, segmentsEntryScopeERC});
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC) {

		return _collectionPersistenceFinderByG_SEERC_SESERC.count(
			finderCache,
			new Object[] {groupId, segmentsEntryERC, segmentsEntryScopeERC});
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	@Override
	public int filterCountByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC) {

		return _collectionPersistenceFinderByG_SEERC_SESERC.filterCount(
			finderCache,
			new Object[] {groupId, segmentsEntryERC, segmentsEntryScopeERC},
			groupId);
	}

	private UniquePersistenceFinder
		<SegmentsExperience, NoSuchExperienceException>
			_uniquePersistenceFinderByG_SEK_P;

	/**
	 * Returns the segments experience where groupId = &#63; and segmentsExperienceKey = &#63; and plid = &#63; or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceKey the segments experience key
	 * @param plid the plid
	 * @return the matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_SEK_P(
			long groupId, String segmentsExperienceKey, long plid)
		throws NoSuchExperienceException {

		return _uniquePersistenceFinderByG_SEK_P.find(
			finderCache, new Object[] {groupId, segmentsExperienceKey, plid});
	}

	/**
	 * Returns the segments experience where groupId = &#63; and segmentsExperienceKey = &#63; and plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceKey the segments experience key
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_SEK_P(
		long groupId, String segmentsExperienceKey, long plid,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_SEK_P.fetch(
			finderCache, new Object[] {groupId, segmentsExperienceKey, plid},
			useFinderCache);
	}

	/**
	 * Removes the segments experience where groupId = &#63; and segmentsExperienceKey = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceKey the segments experience key
	 * @param plid the plid
	 * @return the segments experience that was removed
	 */
	@Override
	public SegmentsExperience removeByG_SEK_P(
			long groupId, String segmentsExperienceKey, long plid)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = findByG_SEK_P(
			groupId, segmentsExperienceKey, plid);

		return remove(segmentsExperience);
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and segmentsExperienceKey = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceKey the segments experience key
	 * @param plid the plid
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_SEK_P(
		long groupId, String segmentsExperienceKey, long plid) {

		return _uniquePersistenceFinderByG_SEK_P.count(
			finderCache, new Object[] {groupId, segmentsExperienceKey, plid});
	}

	private UniquePersistenceFinder
		<SegmentsExperience, NoSuchExperienceException>
			_uniquePersistenceFinderByG_P_P;

	/**
	 * Returns the segments experience where groupId = &#63; and plid = &#63; and priority = &#63; or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_P_P(long groupId, long plid, int priority)
		throws NoSuchExperienceException {

		return _uniquePersistenceFinderByG_P_P.find(
			finderCache, new Object[] {groupId, plid, priority});
	}

	/**
	 * Returns the segments experience where groupId = &#63; and plid = &#63; and priority = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_P_P(
		long groupId, long plid, int priority, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_P_P.fetch(
			finderCache, new Object[] {groupId, plid, priority},
			useFinderCache);
	}

	/**
	 * Removes the segments experience where groupId = &#63; and plid = &#63; and priority = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the segments experience that was removed
	 */
	@Override
	public SegmentsExperience removeByG_P_P(
			long groupId, long plid, int priority)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = findByG_P_P(
			groupId, plid, priority);

		return remove(segmentsExperience);
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63; and priority = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_P_P(long groupId, long plid, int priority) {
		return _uniquePersistenceFinderByG_P_P.count(
			finderCache, new Object[] {groupId, plid, priority});
	}

	private FilterCollectionPersistenceFinder
		<SegmentsExperience, NoSuchExperienceException>
			_collectionPersistenceFinderByG_P_GtP;

	/**
	 * Returns all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_GtP(
		long groupId, long plid, int priority) {

		return findByG_P_GtP(
			groupId, plid, priority, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_GtP(
		long groupId, long plid, int priority, int start, int end) {

		return findByG_P_GtP(groupId, plid, priority, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_GtP(
		long groupId, long plid, int priority, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return findByG_P_GtP(
			groupId, plid, priority, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_GtP(
		long groupId, long plid, int priority, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_GtP.find(
			finderCache, new Object[] {groupId, plid, priority}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_P_GtP_First(
			long groupId, long plid, int priority,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		return _collectionPersistenceFinderByG_P_GtP.findFirst(
			finderCache, new Object[] {groupId, plid, priority},
			orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_P_GtP_First(
		long groupId, long plid, int priority,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByG_P_GtP.fetchFirst(
			finderCache, new Object[] {groupId, plid, priority},
			orderByComparator);
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P_GtP(
		long groupId, long plid, int priority) {

		return filterFindByG_P_GtP(
			groupId, plid, priority, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P_GtP(
		long groupId, long plid, int priority, int start, int end) {

		return filterFindByG_P_GtP(groupId, plid, priority, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P_GtP(
		long groupId, long plid, int priority, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByG_P_GtP.filterFind(
			finderCache, new Object[] {groupId, plid, priority}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 */
	@Override
	public void removeByG_P_GtP(long groupId, long plid, int priority) {
		_collectionPersistenceFinderByG_P_GtP.remove(
			finderCache, new Object[] {groupId, plid, priority});
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_P_GtP(long groupId, long plid, int priority) {
		return _collectionPersistenceFinderByG_P_GtP.count(
			finderCache, new Object[] {groupId, plid, priority});
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_GtP(long groupId, long plid, int priority) {
		return _collectionPersistenceFinderByG_P_GtP.filterCount(
			finderCache, new Object[] {groupId, plid, priority}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<SegmentsExperience, NoSuchExperienceException>
			_collectionPersistenceFinderByG_P_LtP;

	/**
	 * Returns all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_LtP(
		long groupId, long plid, int priority) {

		return findByG_P_LtP(
			groupId, plid, priority, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_LtP(
		long groupId, long plid, int priority, int start, int end) {

		return findByG_P_LtP(groupId, plid, priority, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_LtP(
		long groupId, long plid, int priority, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return findByG_P_LtP(
			groupId, plid, priority, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_LtP(
		long groupId, long plid, int priority, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_LtP.find(
			finderCache, new Object[] {groupId, plid, priority}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_P_LtP_First(
			long groupId, long plid, int priority,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		return _collectionPersistenceFinderByG_P_LtP.findFirst(
			finderCache, new Object[] {groupId, plid, priority},
			orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_P_LtP_First(
		long groupId, long plid, int priority,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByG_P_LtP.fetchFirst(
			finderCache, new Object[] {groupId, plid, priority},
			orderByComparator);
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P_LtP(
		long groupId, long plid, int priority) {

		return filterFindByG_P_LtP(
			groupId, plid, priority, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P_LtP(
		long groupId, long plid, int priority, int start, int end) {

		return filterFindByG_P_LtP(groupId, plid, priority, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P_LtP(
		long groupId, long plid, int priority, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByG_P_LtP.filterFind(
			finderCache, new Object[] {groupId, plid, priority}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 */
	@Override
	public void removeByG_P_LtP(long groupId, long plid, int priority) {
		_collectionPersistenceFinderByG_P_LtP.remove(
			finderCache, new Object[] {groupId, plid, priority});
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_P_LtP(long groupId, long plid, int priority) {
		return _collectionPersistenceFinderByG_P_LtP.count(
			finderCache, new Object[] {groupId, plid, priority});
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_LtP(long groupId, long plid, int priority) {
		return _collectionPersistenceFinderByG_P_LtP.filterCount(
			finderCache, new Object[] {groupId, plid, priority}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<SegmentsExperience, NoSuchExperienceException>
			_collectionPersistenceFinderByG_P_A;

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_A(
		long groupId, long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_A.find(
			finderCache, new Object[] {groupId, plid, active}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_P_A_First(
			long groupId, long plid, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		return _collectionPersistenceFinderByG_P_A.findFirst(
			finderCache, new Object[] {groupId, plid, active},
			orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_P_A_First(
		long groupId, long plid, boolean active,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByG_P_A.fetchFirst(
			finderCache, new Object[] {groupId, plid, active},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P_A(
		long groupId, long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByG_P_A.filterFind(
			finderCache, new Object[] {groupId, plid, active}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and plid = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 */
	@Override
	public void removeByG_P_A(long groupId, long plid, boolean active) {
		_collectionPersistenceFinderByG_P_A.remove(
			finderCache, new Object[] {groupId, plid, active});
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_P_A(long groupId, long plid, boolean active) {
		return _collectionPersistenceFinderByG_P_A.count(
			finderCache, new Object[] {groupId, plid, active});
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_A(long groupId, long plid, boolean active) {
		return _collectionPersistenceFinderByG_P_A.filterCount(
			finderCache, new Object[] {groupId, plid, active}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<SegmentsExperience, NoSuchExperienceException>
			_collectionPersistenceFinderByG_SEERC_SESERC_P;

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_SEERC_SESERC_P(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_SEERC_SESERC_P.find(
			finderCache,
			new Object[] {
				groupId, segmentsEntryERC, segmentsEntryScopeERC, plid
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_SEERC_SESERC_P_First(
			long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
			long plid, OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		return _collectionPersistenceFinderByG_SEERC_SESERC_P.findFirst(
			finderCache,
			new Object[] {
				groupId, segmentsEntryERC, segmentsEntryScopeERC, plid
			},
			orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_SEERC_SESERC_P_First(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByG_SEERC_SESERC_P.fetchFirst(
			finderCache,
			new Object[] {
				groupId, segmentsEntryERC, segmentsEntryScopeERC, plid
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_SEERC_SESERC_P(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByG_SEERC_SESERC_P.filterFind(
			finderCache,
			new Object[] {
				groupId, segmentsEntryERC, segmentsEntryScopeERC, plid
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 */
	@Override
	public void removeByG_SEERC_SESERC_P(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid) {

		_collectionPersistenceFinderByG_SEERC_SESERC_P.remove(
			finderCache,
			new Object[] {
				groupId, segmentsEntryERC, segmentsEntryScopeERC, plid
			});
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_SEERC_SESERC_P(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid) {

		return _collectionPersistenceFinderByG_SEERC_SESERC_P.count(
			finderCache,
			new Object[] {
				groupId, segmentsEntryERC, segmentsEntryScopeERC, plid
			});
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	@Override
	public int filterCountByG_SEERC_SESERC_P(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid) {

		return _collectionPersistenceFinderByG_SEERC_SESERC_P.filterCount(
			finderCache,
			new Object[] {
				groupId, segmentsEntryERC, segmentsEntryScopeERC, plid
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<SegmentsExperience, NoSuchExperienceException>
			_collectionPersistenceFinderByG_SEERC_SESERC_P_A;

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_SEERC_SESERC_P_A.find(
			finderCache,
			new Object[] {
				groupId, new String[] {segmentsEntryERC}, segmentsEntryScopeERC,
				plid, active
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_SEERC_SESERC_P_A_First(
			long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
			long plid, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByG_SEERC_SESERC_P_A_First(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active,
			orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(12);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", segmentsEntryERC=");
		sb.append(segmentsEntryERC);

		sb.append(", segmentsEntryScopeERC=");
		sb.append(segmentsEntryScopeERC);

		sb.append(", plid=");
		sb.append(plid);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_SEERC_SESERC_P_A_First(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByG_SEERC_SESERC_P_A.fetchFirst(
			finderCache,
			new Object[] {
				groupId, new String[] {segmentsEntryERC}, segmentsEntryScopeERC,
				plid, active
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByG_SEERC_SESERC_P_A.filterFind(
			finderCache,
			new Object[] {
				groupId, new String[] {segmentsEntryERC}, segmentsEntryScopeERC,
				plid, active
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = any &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERCs the segments entry ercs
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return _collectionPersistenceFinderByG_SEERC_SESERC_P_A.filterFind(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(segmentsEntryERCs),
				segmentsEntryScopeERC, plid, active
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERCs the segments entry ercs
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_SEERC_SESERC_P_A.find(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(segmentsEntryERCs),
				segmentsEntryScopeERC, plid, active
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 */
	@Override
	public void removeByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active) {

		_collectionPersistenceFinderByG_SEERC_SESERC_P_A.remove(
			finderCache,
			new Object[] {
				groupId, new String[] {segmentsEntryERC}, segmentsEntryScopeERC,
				plid, active
			});
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active) {

		return _collectionPersistenceFinderByG_SEERC_SESERC_P_A.count(
			finderCache,
			new Object[] {
				groupId, new String[] {segmentsEntryERC}, segmentsEntryScopeERC,
				plid, active
			});
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and segmentsEntryERC = any &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERCs the segments entry ercs
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active) {

		return _collectionPersistenceFinderByG_SEERC_SESERC_P_A.count(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(segmentsEntryERCs),
				segmentsEntryScopeERC, plid, active
			});
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	@Override
	public int filterCountByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active) {

		return _collectionPersistenceFinderByG_SEERC_SESERC_P_A.filterCount(
			finderCache,
			new Object[] {
				groupId, new String[] {segmentsEntryERC}, segmentsEntryScopeERC,
				plid, active
			},
			groupId);
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = any &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERCs the segments entry ercs
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	@Override
	public int filterCountByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active) {

		return _collectionPersistenceFinderByG_SEERC_SESERC_P_A.filterCount(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(segmentsEntryERCs),
				segmentsEntryScopeERC, plid, active
			},
			groupId);
	}

	private UniquePersistenceFinder
		<SegmentsExperience, NoSuchExperienceException>
			_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the segments experience where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchExperienceException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the segments experience where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the segments experience where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the segments experience that was removed
	 */
	@Override
	public SegmentsExperience removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = findByERC_G(
			externalReferenceCode, groupId);

		return remove(segmentsExperience);
	}

	/**
	 * Returns the number of segments experiences where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public SegmentsExperiencePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(SegmentsExperience.class);

		setModelImplClass(SegmentsExperienceImpl.class);
		setModelPKClass(long.class);

		setTable(SegmentsExperienceTable.INSTANCE);
	}

	/**
	 * Creates a new segments experience with the primary key. Does not add the segments experience to the database.
	 *
	 * @param segmentsExperienceId the primary key for the new segments experience
	 * @return the new segments experience
	 */
	@Override
	public SegmentsExperience create(long segmentsExperienceId) {
		SegmentsExperience segmentsExperience = new SegmentsExperienceImpl();

		segmentsExperience.setNew(true);
		segmentsExperience.setPrimaryKey(segmentsExperienceId);

		String uuid = PortalUUIDUtil.generate();

		segmentsExperience.setUuid(uuid);

		segmentsExperience.setCompanyId(CompanyThreadLocal.getCompanyId());

		return segmentsExperience;
	}

	/**
	 * Removes the segments experience with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param segmentsExperienceId the primary key of the segments experience
	 * @return the segments experience that was removed
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience remove(long segmentsExperienceId)
		throws NoSuchExperienceException {

		return remove((Serializable)segmentsExperienceId);
	}

	@Override
	protected SegmentsExperience removeImpl(
		SegmentsExperience segmentsExperience) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(segmentsExperience)) {
				segmentsExperience = (SegmentsExperience)session.get(
					SegmentsExperienceImpl.class,
					segmentsExperience.getPrimaryKeyObj());
			}

			if ((segmentsExperience != null) &&
				ctPersistenceHelper.isRemove(segmentsExperience)) {

				session.delete(segmentsExperience);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (segmentsExperience != null) {
			clearCache(segmentsExperience);
		}

		return segmentsExperience;
	}

	@Override
	public SegmentsExperience updateImpl(
		SegmentsExperience segmentsExperience) {

		boolean isNew = segmentsExperience.isNew();

		if (!(segmentsExperience instanceof SegmentsExperienceModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(segmentsExperience.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					segmentsExperience);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in segmentsExperience proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SegmentsExperience implementation " +
					segmentsExperience.getClass());
		}

		SegmentsExperienceModelImpl segmentsExperienceModelImpl =
			(SegmentsExperienceModelImpl)segmentsExperience;

		if (Validator.isNull(segmentsExperience.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			segmentsExperience.setUuid(uuid);
		}

		if (Validator.isNull(segmentsExperience.getExternalReferenceCode())) {
			segmentsExperience.setExternalReferenceCode(
				segmentsExperience.getUuid());
		}
		else {
			if (!Objects.equals(
					segmentsExperienceModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					segmentsExperience.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = segmentsExperience.getCompanyId();

					long groupId = segmentsExperience.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = segmentsExperience.getPrimaryKey();
					}

					try {
						segmentsExperience.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								SegmentsExperience.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								segmentsExperience.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			SegmentsExperience ercSegmentsExperience = fetchByERC_G(
				segmentsExperience.getExternalReferenceCode(),
				segmentsExperience.getGroupId());

			if (isNew) {
				if (ercSegmentsExperience != null) {
					throw new DuplicateSegmentsExperienceExternalReferenceCodeException(
						"Duplicate segments experience with external reference code " +
							segmentsExperience.getExternalReferenceCode() +
								" and group " +
									segmentsExperience.getGroupId());
				}
			}
			else {
				if ((ercSegmentsExperience != null) &&
					(segmentsExperience.getSegmentsExperienceId() !=
						ercSegmentsExperience.getSegmentsExperienceId())) {

					throw new DuplicateSegmentsExperienceExternalReferenceCodeException(
						"Duplicate segments experience with external reference code " +
							segmentsExperience.getExternalReferenceCode() +
								" and group " +
									segmentsExperience.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (segmentsExperience.getCreateDate() == null)) {
			if (serviceContext == null) {
				segmentsExperience.setCreateDate(date);
			}
			else {
				segmentsExperience.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!segmentsExperienceModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				segmentsExperience.setModifiedDate(date);
			}
			else {
				segmentsExperience.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(segmentsExperience)) {
				if (!isNew) {
					session.evict(
						SegmentsExperienceImpl.class,
						segmentsExperience.getPrimaryKeyObj());
				}

				session.save(segmentsExperience);
			}
			else {
				segmentsExperience = (SegmentsExperience)session.merge(
					segmentsExperience);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(segmentsExperience, false);

		if (isNew) {
			segmentsExperience.setNew(false);
		}

		segmentsExperience.resetOriginalValues();

		return segmentsExperience;
	}

	/**
	 * Returns the segments experience with the primary key or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param segmentsExperienceId the primary key of the segments experience
	 * @return the segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience findByPrimaryKey(long segmentsExperienceId)
		throws NoSuchExperienceException {

		return findByPrimaryKey((Serializable)segmentsExperienceId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the segments experience with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param segmentsExperienceId the primary key of the segments experience
	 * @return the segments experience, or <code>null</code> if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience fetchByPrimaryKey(long segmentsExperienceId) {
		return fetchByPrimaryKey((Serializable)segmentsExperienceId);
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
		return "segmentsExperienceId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SEGMENTSEXPERIENCE;
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
		return SegmentsExperienceModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "SegmentsExperience";
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
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("segmentsEntryERC");
		ctMergeColumnNames.add("segmentsEntryScopeERC");
		ctMergeColumnNames.add("segmentsExperienceKey");
		ctMergeColumnNames.add("plid");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("active_");
		ctMergeColumnNames.add("typeSettings");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("segmentsExperienceId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "segmentsExperienceKey", "plid"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "plid", "priority"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the segments experience persistence.
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
			_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE,
			_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE,
			SegmentsExperienceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"segmentsExperience.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				SegmentsExperience::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(SegmentsExperience::getUuid),
				SegmentsExperience::getGroupId),
			_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE, "",
			new FinderColumn<>(
				"segmentsExperience.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				SegmentsExperience::getUuid),
			new FinderColumn<>(
				"segmentsExperience.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, SegmentsExperience::getGroupId));

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
				_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE,
				_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE,
				SegmentsExperienceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"segmentsExperience.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					SegmentsExperience::getUuid),
				new FinderColumn<>(
					"segmentsExperience.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, SegmentsExperience::getCompanyId));

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
				_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE,
				_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE,
				SegmentsExperienceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"segmentsExperience.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, SegmentsExperience::getGroupId));

		_collectionPersistenceFinderByG_P =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "plid"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "plid"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "plid"}, false),
				_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE,
				_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE,
				SegmentsExperienceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"segmentsExperience.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, SegmentsExperience::getGroupId),
				new FinderColumn<>(
					"segmentsExperience.", "plid", FinderColumn.Type.LONG, "=",
					true, true, SegmentsExperience::getPlid));

		_collectionPersistenceFinderByG_A =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_A",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_A",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_A",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "active_"}, false),
				_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE,
				_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE,
				SegmentsExperienceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new ArrayableFinderColumn<>(
					"segmentsExperience.", "groupId", FinderColumn.Type.LONG,
					"=", false, true, true, SegmentsExperience::getGroupId),
				new FinderColumn<>(
					"segmentsExperience.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					SegmentsExperience::isActive));

		_collectionPersistenceFinderBySEERC_SESERC =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findBySEERC_SESERC",
					new String[] {
						String.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"segmentsEntryERC", "segmentsEntryScopeERC"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findBySEERC_SESERC",
					new String[] {
						String.class.getName(), String.class.getName()
					},
					new String[] {"segmentsEntryERC", "segmentsEntryScopeERC"},
					0, 3, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countBySEERC_SESERC",
					new String[] {
						String.class.getName(), String.class.getName()
					},
					new String[] {"segmentsEntryERC", "segmentsEntryScopeERC"},
					0, 3, false, null),
				_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE,
				_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE,
				SegmentsExperienceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"segmentsExperience.", "segmentsEntryERC",
					FinderColumn.Type.STRING, "=", true, true,
					SegmentsExperience::getSegmentsEntryERC),
				new FinderColumn<>(
					"segmentsExperience.", "segmentsEntryScopeERC",
					FinderColumn.Type.STRING, "=", true, true,
					SegmentsExperience::getSegmentsEntryScopeERC));

		_collectionPersistenceFinderByG_SEERC_SESERC =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_SEERC_SESERC",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "segmentsEntryERC", "segmentsEntryScopeERC"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_SEERC_SESERC",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {
						"groupId", "segmentsEntryERC", "segmentsEntryScopeERC"
					},
					0, 6, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_SEERC_SESERC",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {
						"groupId", "segmentsEntryERC", "segmentsEntryScopeERC"
					},
					0, 6, false, null),
				_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE,
				_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE,
				SegmentsExperienceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"segmentsExperience.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, SegmentsExperience::getGroupId),
				new FinderColumn<>(
					"segmentsExperience.", "segmentsEntryERC",
					FinderColumn.Type.STRING, "=", true, true,
					SegmentsExperience::getSegmentsEntryERC),
				new FinderColumn<>(
					"segmentsExperience.", "segmentsEntryScopeERC",
					FinderColumn.Type.STRING, "=", true, true,
					SegmentsExperience::getSegmentsEntryScopeERC));

		_uniquePersistenceFinderByG_SEK_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_SEK_P",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "segmentsExperienceKey", "plid"}, 0, 2,
				false, SegmentsExperience::getGroupId,
				convertNullFunction(
					SegmentsExperience::getSegmentsExperienceKey),
				SegmentsExperience::getPlid),
			_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE, "",
			new FinderColumn<>(
				"segmentsExperience.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, SegmentsExperience::getGroupId),
			new FinderColumn<>(
				"segmentsExperience.", "segmentsExperienceKey",
				FinderColumn.Type.STRING, "=", true, true,
				SegmentsExperience::getSegmentsExperienceKey),
			new FinderColumn<>(
				"segmentsExperience.", "plid", FinderColumn.Type.LONG, "=",
				true, true, SegmentsExperience::getPlid));

		_uniquePersistenceFinderByG_P_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_P_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"groupId", "plid", "priority"}, 0, 0, false,
				SegmentsExperience::getGroupId, SegmentsExperience::getPlid,
				SegmentsExperience::getPriority),
			_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE, "",
			new FinderColumn<>(
				"segmentsExperience.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, SegmentsExperience::getGroupId),
			new FinderColumn<>(
				"segmentsExperience.", "plid", FinderColumn.Type.LONG, "=",
				true, true, SegmentsExperience::getPlid),
			new FinderColumn<>(
				"segmentsExperience.", "priority", FinderColumn.Type.INTEGER,
				"=", true, true, SegmentsExperience::getPriority));

		_collectionPersistenceFinderByG_P_GtP =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_GtP",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "plid", "priority"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_GtP",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "plid", "priority"}, false),
				_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE,
				_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE,
				SegmentsExperienceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"segmentsExperience.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, SegmentsExperience::getGroupId),
				new FinderColumn<>(
					"segmentsExperience.", "plid", FinderColumn.Type.LONG, "=",
					true, true, SegmentsExperience::getPlid),
				new FinderColumn<>(
					"segmentsExperience.", "priority",
					FinderColumn.Type.INTEGER, ">", true, true,
					SegmentsExperience::getPriority));

		_collectionPersistenceFinderByG_P_LtP =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_LtP",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "plid", "priority"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_LtP",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "plid", "priority"}, false),
				_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE,
				_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE,
				SegmentsExperienceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"segmentsExperience.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, SegmentsExperience::getGroupId),
				new FinderColumn<>(
					"segmentsExperience.", "plid", FinderColumn.Type.LONG, "=",
					true, true, SegmentsExperience::getPlid),
				new FinderColumn<>(
					"segmentsExperience.", "priority",
					FinderColumn.Type.INTEGER, "<", true, true,
					SegmentsExperience::getPriority));

		_collectionPersistenceFinderByG_P_A =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_A",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "plid", "active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_A",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "plid", "active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_A",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "plid", "active_"}, false),
				_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE,
				_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE,
				SegmentsExperienceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"segmentsExperience.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, SegmentsExperience::getGroupId),
				new FinderColumn<>(
					"segmentsExperience.", "plid", FinderColumn.Type.LONG, "=",
					true, true, SegmentsExperience::getPlid),
				new FinderColumn<>(
					"segmentsExperience.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					SegmentsExperience::isActive));

		_collectionPersistenceFinderByG_SEERC_SESERC_P =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_SEERC_SESERC_P",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "segmentsEntryERC", "segmentsEntryScopeERC",
						"plid"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_SEERC_SESERC_P",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Long.class.getName()
					},
					new String[] {
						"groupId", "segmentsEntryERC", "segmentsEntryScopeERC",
						"plid"
					},
					0, 6, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_SEERC_SESERC_P",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Long.class.getName()
					},
					new String[] {
						"groupId", "segmentsEntryERC", "segmentsEntryScopeERC",
						"plid"
					},
					0, 6, false, null),
				_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE,
				_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE,
				SegmentsExperienceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"segmentsExperience.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, SegmentsExperience::getGroupId),
				new FinderColumn<>(
					"segmentsExperience.", "segmentsEntryERC",
					FinderColumn.Type.STRING, "=", true, true,
					SegmentsExperience::getSegmentsEntryERC),
				new FinderColumn<>(
					"segmentsExperience.", "segmentsEntryScopeERC",
					FinderColumn.Type.STRING, "=", true, true,
					SegmentsExperience::getSegmentsEntryScopeERC),
				new FinderColumn<>(
					"segmentsExperience.", "plid", FinderColumn.Type.LONG, "=",
					true, true, SegmentsExperience::getPlid));

		_collectionPersistenceFinderByG_SEERC_SESERC_P_A =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_SEERC_SESERC_P_A",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "segmentsEntryERC", "segmentsEntryScopeERC",
						"plid", "active_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_SEERC_SESERC_P_A",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {
						"groupId", "segmentsEntryERC", "segmentsEntryScopeERC",
						"plid", "active_"
					},
					0, 6, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_SEERC_SESERC_P_A",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {
						"groupId", "segmentsEntryERC", "segmentsEntryScopeERC",
						"plid", "active_"
					},
					0, 6, false, null),
				_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE,
				_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE,
				SegmentsExperienceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"segmentsExperience.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, SegmentsExperience::getGroupId),
				new ArrayableFinderColumn<>(
					"segmentsExperience.", "segmentsEntryERC",
					FinderColumn.Type.STRING, "=", false, true, true,
					SegmentsExperience::getSegmentsEntryERC),
				new FinderColumn<>(
					"segmentsExperience.", "segmentsEntryScopeERC",
					FinderColumn.Type.STRING, "=", true, true,
					SegmentsExperience::getSegmentsEntryScopeERC),
				new FinderColumn<>(
					"segmentsExperience.", "plid", FinderColumn.Type.LONG, "=",
					true, true, SegmentsExperience::getPlid),
				new FinderColumn<>(
					"segmentsExperience.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					SegmentsExperience::isActive));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(
					SegmentsExperience::getExternalReferenceCode),
				SegmentsExperience::getGroupId),
			_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE, "",
			new FinderColumn<>(
				"segmentsExperience.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				SegmentsExperience::getExternalReferenceCode),
			new FinderColumn<>(
				"segmentsExperience.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, SegmentsExperience::getGroupId));

		SegmentsExperienceUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SegmentsExperienceUtil.setPersistence(null);

		entityCache.removeCache(SegmentsExperienceImpl.class.getName());
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
		SegmentsExperienceModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SEGMENTSEXPERIENCE =
		"SELECT segmentsExperience FROM SegmentsExperience segmentsExperience";

	private static final String _SQL_SELECT_SEGMENTSEXPERIENCE_WHERE =
		"SELECT segmentsExperience FROM SegmentsExperience segmentsExperience WHERE ";

	private static final String _SQL_COUNT_SEGMENTSEXPERIENCE_WHERE =
		"SELECT COUNT(segmentsExperience) FROM SegmentsExperience segmentsExperience WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SegmentsExperience exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsExperiencePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1046354540