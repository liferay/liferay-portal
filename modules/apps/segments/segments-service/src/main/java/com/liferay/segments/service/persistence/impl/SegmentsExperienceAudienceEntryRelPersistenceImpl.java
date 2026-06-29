/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
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
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.segments.exception.NoSuchExperienceAudienceEntryRelException;
import com.liferay.segments.model.SegmentsExperienceAudienceEntryRel;
import com.liferay.segments.model.SegmentsExperienceAudienceEntryRelTable;
import com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelImpl;
import com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl;
import com.liferay.segments.service.persistence.SegmentsExperienceAudienceEntryRelPersistence;
import com.liferay.segments.service.persistence.SegmentsExperienceAudienceEntryRelUtil;
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
 * The persistence implementation for the segments experience audience entry rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Eduardo Garcia
 * @generated
 */
@Component(service = SegmentsExperienceAudienceEntryRelPersistence.class)
public class SegmentsExperienceAudienceEntryRelPersistenceImpl
	extends BasePersistenceImpl
		<SegmentsExperienceAudienceEntryRel,
		 NoSuchExperienceAudienceEntryRelException>
	implements SegmentsExperienceAudienceEntryRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SegmentsExperienceAudienceEntryRelUtil</code> to access the segments experience audience entry rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SegmentsExperienceAudienceEntryRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SegmentsExperienceAudienceEntryRel,
		 NoSuchExperienceAudienceEntryRelException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the segments experience audience entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of segments experience audience entry rels
	 * @param end the upper bound of the range of segments experience audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experience audience entry rels
	 */
	@Override
	public List<SegmentsExperienceAudienceEntryRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SegmentsExperienceAudienceEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a matching segments experience audience entry rel could not be found
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel findByUuid_First(
			String uuid,
			OrderByComparator<SegmentsExperienceAudienceEntryRel>
				orderByComparator)
		throws NoSuchExperienceAudienceEntryRelException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel fetchByUuid_First(
		String uuid,
		OrderByComparator<SegmentsExperienceAudienceEntryRel>
			orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the segments experience audience entry rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of segments experience audience entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching segments experience audience entry rels
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<SegmentsExperienceAudienceEntryRel,
		 NoSuchExperienceAudienceEntryRelException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the segments experience audience entry rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchExperienceAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a matching segments experience audience entry rel could not be found
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel findByUUID_G(
			String uuid, long groupId)
		throws NoSuchExperienceAudienceEntryRelException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the segments experience audience entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the segments experience audience entry rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the segments experience audience entry rel that was removed
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel removeByUUID_G(
			String uuid, long groupId)
		throws NoSuchExperienceAudienceEntryRelException {

		SegmentsExperienceAudienceEntryRel segmentsExperienceAudienceEntryRel =
			findByUUID_G(uuid, groupId);

		return remove(segmentsExperienceAudienceEntryRel);
	}

	/**
	 * Returns the number of segments experience audience entry rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching segments experience audience entry rels
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<SegmentsExperienceAudienceEntryRel,
		 NoSuchExperienceAudienceEntryRelException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the segments experience audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of segments experience audience entry rels
	 * @param end the upper bound of the range of segments experience audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experience audience entry rels
	 */
	@Override
	public List<SegmentsExperienceAudienceEntryRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SegmentsExperienceAudienceEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a matching segments experience audience entry rel could not be found
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<SegmentsExperienceAudienceEntryRel>
				orderByComparator)
		throws NoSuchExperienceAudienceEntryRelException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<SegmentsExperienceAudienceEntryRel>
			orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the segments experience audience entry rels where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of segments experience audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching segments experience audience entry rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<SegmentsExperienceAudienceEntryRel,
		 NoSuchExperienceAudienceEntryRelException>
			_collectionPersistenceFinderBySegmentsExperienceERC;

	/**
	 * Returns an ordered range of all the segments experience audience entry rels where segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of segments experience audience entry rels
	 * @param end the upper bound of the range of segments experience audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experience audience entry rels
	 */
	@Override
	public List<SegmentsExperienceAudienceEntryRel> findBySegmentsExperienceERC(
		String segmentsExperienceERC, int start, int end,
		OrderByComparator<SegmentsExperienceAudienceEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySegmentsExperienceERC.find(
			finderCache, new Object[] {segmentsExperienceERC}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where segmentsExperienceERC = &#63;.
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a matching segments experience audience entry rel could not be found
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel findBySegmentsExperienceERC_First(
			String segmentsExperienceERC,
			OrderByComparator<SegmentsExperienceAudienceEntryRel>
				orderByComparator)
		throws NoSuchExperienceAudienceEntryRelException {

		return _collectionPersistenceFinderBySegmentsExperienceERC.findFirst(
			finderCache, new Object[] {segmentsExperienceERC},
			orderByComparator);
	}

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where segmentsExperienceERC = &#63;.
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel
		fetchBySegmentsExperienceERC_First(
			String segmentsExperienceERC,
			OrderByComparator<SegmentsExperienceAudienceEntryRel>
				orderByComparator) {

		return _collectionPersistenceFinderBySegmentsExperienceERC.fetchFirst(
			finderCache, new Object[] {segmentsExperienceERC},
			orderByComparator);
	}

	/**
	 * Removes all the segments experience audience entry rels where segmentsExperienceERC = &#63; from the database.
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 */
	@Override
	public void removeBySegmentsExperienceERC(String segmentsExperienceERC) {
		_collectionPersistenceFinderBySegmentsExperienceERC.remove(
			finderCache, new Object[] {segmentsExperienceERC});
	}

	/**
	 * Returns the number of segments experience audience entry rels where segmentsExperienceERC = &#63;.
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching segments experience audience entry rels
	 */
	@Override
	public int countBySegmentsExperienceERC(String segmentsExperienceERC) {
		return _collectionPersistenceFinderBySegmentsExperienceERC.count(
			finderCache, new Object[] {segmentsExperienceERC});
	}

	private UniquePersistenceFinder
		<SegmentsExperienceAudienceEntryRel,
		 NoSuchExperienceAudienceEntryRelException>
			_uniquePersistenceFinderByAEERC_SEERC;

	/**
	 * Returns the segments experience audience entry rel where audienceEntryERC = &#63; and segmentsExperienceERC = &#63; or throws a <code>NoSuchExperienceAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a matching segments experience audience entry rel could not be found
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel findByAEERC_SEERC(
			String audienceEntryERC, String segmentsExperienceERC)
		throws NoSuchExperienceAudienceEntryRelException {

		return _uniquePersistenceFinderByAEERC_SEERC.find(
			finderCache,
			new Object[] {audienceEntryERC, segmentsExperienceERC});
	}

	/**
	 * Returns the segments experience audience entry rel where audienceEntryERC = &#63; and segmentsExperienceERC = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsExperienceERC the segments experience erc
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel fetchByAEERC_SEERC(
		String audienceEntryERC, String segmentsExperienceERC,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByAEERC_SEERC.fetch(
			finderCache, new Object[] {audienceEntryERC, segmentsExperienceERC},
			useFinderCache);
	}

	/**
	 * Removes the segments experience audience entry rel where audienceEntryERC = &#63; and segmentsExperienceERC = &#63; from the database.
	 *
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the segments experience audience entry rel that was removed
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel removeByAEERC_SEERC(
			String audienceEntryERC, String segmentsExperienceERC)
		throws NoSuchExperienceAudienceEntryRelException {

		SegmentsExperienceAudienceEntryRel segmentsExperienceAudienceEntryRel =
			findByAEERC_SEERC(audienceEntryERC, segmentsExperienceERC);

		return remove(segmentsExperienceAudienceEntryRel);
	}

	/**
	 * Returns the number of segments experience audience entry rels where audienceEntryERC = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching segments experience audience entry rels
	 */
	@Override
	public int countByAEERC_SEERC(
		String audienceEntryERC, String segmentsExperienceERC) {

		return _uniquePersistenceFinderByAEERC_SEERC.count(
			finderCache,
			new Object[] {audienceEntryERC, segmentsExperienceERC});
	}

	public SegmentsExperienceAudienceEntryRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"segmentsExperienceAudienceEntryRelId",
			"sExperienceAudienceEntryRelId");

		setDBColumnNames(dbColumnNames);

		setModelClass(SegmentsExperienceAudienceEntryRel.class);

		setModelImplClass(SegmentsExperienceAudienceEntryRelImpl.class);
		setModelPKClass(long.class);

		setTable(SegmentsExperienceAudienceEntryRelTable.INSTANCE);
	}

	/**
	 * Creates a new segments experience audience entry rel with the primary key. Does not add the segments experience audience entry rel to the database.
	 *
	 * @param segmentsExperienceAudienceEntryRelId the primary key for the new segments experience audience entry rel
	 * @return the new segments experience audience entry rel
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel create(
		long segmentsExperienceAudienceEntryRelId) {

		SegmentsExperienceAudienceEntryRel segmentsExperienceAudienceEntryRel =
			new SegmentsExperienceAudienceEntryRelImpl();

		segmentsExperienceAudienceEntryRel.setNew(true);
		segmentsExperienceAudienceEntryRel.setPrimaryKey(
			segmentsExperienceAudienceEntryRelId);

		String uuid = PortalUUIDUtil.generate();

		segmentsExperienceAudienceEntryRel.setUuid(uuid);

		segmentsExperienceAudienceEntryRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return segmentsExperienceAudienceEntryRel;
	}

	/**
	 * Removes the segments experience audience entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param segmentsExperienceAudienceEntryRelId the primary key of the segments experience audience entry rel
	 * @return the segments experience audience entry rel that was removed
	 * @throws NoSuchExperienceAudienceEntryRelException if a segments experience audience entry rel with the primary key could not be found
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel remove(
			long segmentsExperienceAudienceEntryRelId)
		throws NoSuchExperienceAudienceEntryRelException {

		return remove((Serializable)segmentsExperienceAudienceEntryRelId);
	}

	@Override
	protected SegmentsExperienceAudienceEntryRel removeImpl(
		SegmentsExperienceAudienceEntryRel segmentsExperienceAudienceEntryRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(segmentsExperienceAudienceEntryRel)) {
				segmentsExperienceAudienceEntryRel =
					(SegmentsExperienceAudienceEntryRel)session.get(
						SegmentsExperienceAudienceEntryRelImpl.class,
						segmentsExperienceAudienceEntryRel.getPrimaryKeyObj());
			}

			if ((segmentsExperienceAudienceEntryRel != null) &&
				ctPersistenceHelper.isRemove(
					segmentsExperienceAudienceEntryRel)) {

				session.delete(segmentsExperienceAudienceEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (segmentsExperienceAudienceEntryRel != null) {
			clearCache(segmentsExperienceAudienceEntryRel);
		}

		return segmentsExperienceAudienceEntryRel;
	}

	@Override
	public SegmentsExperienceAudienceEntryRel updateImpl(
		SegmentsExperienceAudienceEntryRel segmentsExperienceAudienceEntryRel) {

		boolean isNew = segmentsExperienceAudienceEntryRel.isNew();

		if (!(segmentsExperienceAudienceEntryRel instanceof
				SegmentsExperienceAudienceEntryRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					segmentsExperienceAudienceEntryRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					segmentsExperienceAudienceEntryRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in segmentsExperienceAudienceEntryRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SegmentsExperienceAudienceEntryRel implementation " +
					segmentsExperienceAudienceEntryRel.getClass());
		}

		SegmentsExperienceAudienceEntryRelModelImpl
			segmentsExperienceAudienceEntryRelModelImpl =
				(SegmentsExperienceAudienceEntryRelModelImpl)
					segmentsExperienceAudienceEntryRel;

		if (Validator.isNull(segmentsExperienceAudienceEntryRel.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			segmentsExperienceAudienceEntryRel.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew &&
			(segmentsExperienceAudienceEntryRel.getCreateDate() == null)) {

			if (serviceContext == null) {
				segmentsExperienceAudienceEntryRel.setCreateDate(date);
			}
			else {
				segmentsExperienceAudienceEntryRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!segmentsExperienceAudienceEntryRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				segmentsExperienceAudienceEntryRel.setModifiedDate(date);
			}
			else {
				segmentsExperienceAudienceEntryRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(
					segmentsExperienceAudienceEntryRel)) {

				if (!isNew) {
					session.evict(
						SegmentsExperienceAudienceEntryRelImpl.class,
						segmentsExperienceAudienceEntryRel.getPrimaryKeyObj());
				}

				session.save(segmentsExperienceAudienceEntryRel);
			}
			else {
				segmentsExperienceAudienceEntryRel =
					(SegmentsExperienceAudienceEntryRel)session.merge(
						segmentsExperienceAudienceEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(segmentsExperienceAudienceEntryRel, false);

		if (isNew) {
			segmentsExperienceAudienceEntryRel.setNew(false);
		}

		segmentsExperienceAudienceEntryRel.resetOriginalValues();

		return segmentsExperienceAudienceEntryRel;
	}

	/**
	 * Returns the segments experience audience entry rel with the primary key or throws a <code>NoSuchExperienceAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param segmentsExperienceAudienceEntryRelId the primary key of the segments experience audience entry rel
	 * @return the segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a segments experience audience entry rel with the primary key could not be found
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel findByPrimaryKey(
			long segmentsExperienceAudienceEntryRelId)
		throws NoSuchExperienceAudienceEntryRelException {

		return findByPrimaryKey(
			(Serializable)segmentsExperienceAudienceEntryRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the segments experience audience entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param segmentsExperienceAudienceEntryRelId the primary key of the segments experience audience entry rel
	 * @return the segments experience audience entry rel, or <code>null</code> if a segments experience audience entry rel with the primary key could not be found
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel fetchByPrimaryKey(
		long segmentsExperienceAudienceEntryRelId) {

		return fetchByPrimaryKey(
			(Serializable)segmentsExperienceAudienceEntryRelId);
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
		return "sExperienceAudienceEntryRelId";
	}

	@Override
	protected String getPKFieldName() {
		return "segmentsExperienceAudienceEntryRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SEGMENTSEXPERIENCEAUDIENCEENTRYREL;
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
		return SegmentsExperienceAudienceEntryRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "SExperienceAudienceEntryRel";
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
		ctMergeColumnNames.add("audienceEntryERC");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("segmentsExperienceERC");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("sExperienceAudienceEntryRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"audienceEntryERC", "segmentsExperienceERC"});
	}

	/**
	 * Initializes the segments experience audience entry rel persistence.
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
			_SQL_SELECT_SEGMENTSEXPERIENCEAUDIENCEENTRYREL_WHERE,
			_SQL_COUNT_SEGMENTSEXPERIENCEAUDIENCEENTRYREL_WHERE,
			SegmentsExperienceAudienceEntryRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"segmentsExperienceAudienceEntryRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				SegmentsExperienceAudienceEntryRel::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(
					SegmentsExperienceAudienceEntryRel::getUuid),
				SegmentsExperienceAudienceEntryRel::getGroupId),
			_SQL_SELECT_SEGMENTSEXPERIENCEAUDIENCEENTRYREL_WHERE, "",
			new FinderColumn<>(
				"segmentsExperienceAudienceEntryRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				SegmentsExperienceAudienceEntryRel::getUuid),
			new FinderColumn<>(
				"segmentsExperienceAudienceEntryRel.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				SegmentsExperienceAudienceEntryRel::getGroupId));

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
				_SQL_SELECT_SEGMENTSEXPERIENCEAUDIENCEENTRYREL_WHERE,
				_SQL_COUNT_SEGMENTSEXPERIENCEAUDIENCEENTRYREL_WHERE,
				SegmentsExperienceAudienceEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"segmentsExperienceAudienceEntryRel.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					SegmentsExperienceAudienceEntryRel::getUuid),
				new FinderColumn<>(
					"segmentsExperienceAudienceEntryRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					SegmentsExperienceAudienceEntryRel::getCompanyId));

		_collectionPersistenceFinderBySegmentsExperienceERC =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findBySegmentsExperienceERC",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"segmentsExperienceERC"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findBySegmentsExperienceERC",
					new String[] {String.class.getName()},
					new String[] {"segmentsExperienceERC"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countBySegmentsExperienceERC",
					new String[] {String.class.getName()},
					new String[] {"segmentsExperienceERC"}, 0, 1, false, null),
				_SQL_SELECT_SEGMENTSEXPERIENCEAUDIENCEENTRYREL_WHERE,
				_SQL_COUNT_SEGMENTSEXPERIENCEAUDIENCEENTRYREL_WHERE,
				SegmentsExperienceAudienceEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"segmentsExperienceAudienceEntryRel.",
					"segmentsExperienceERC", FinderColumn.Type.STRING, "=",
					true, true,
					SegmentsExperienceAudienceEntryRel::
						getSegmentsExperienceERC));

		_uniquePersistenceFinderByAEERC_SEERC = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByAEERC_SEERC",
				new String[] {String.class.getName(), String.class.getName()},
				new String[] {"audienceEntryERC", "segmentsExperienceERC"}, 0,
				3, false,
				convertNullFunction(
					SegmentsExperienceAudienceEntryRel::getAudienceEntryERC),
				convertNullFunction(
					SegmentsExperienceAudienceEntryRel::
						getSegmentsExperienceERC)),
			_SQL_SELECT_SEGMENTSEXPERIENCEAUDIENCEENTRYREL_WHERE, "",
			new FinderColumn<>(
				"segmentsExperienceAudienceEntryRel.", "audienceEntryERC",
				FinderColumn.Type.STRING, "=", true, true,
				SegmentsExperienceAudienceEntryRel::getAudienceEntryERC),
			new FinderColumn<>(
				"segmentsExperienceAudienceEntryRel.", "segmentsExperienceERC",
				FinderColumn.Type.STRING, "=", true, true,
				SegmentsExperienceAudienceEntryRel::getSegmentsExperienceERC));

		SegmentsExperienceAudienceEntryRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SegmentsExperienceAudienceEntryRelUtil.setPersistence(null);

		entityCache.removeCache(
			SegmentsExperienceAudienceEntryRelImpl.class.getName());
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
		SegmentsExperienceAudienceEntryRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SEGMENTSEXPERIENCEAUDIENCEENTRYREL =
		"SELECT segmentsExperienceAudienceEntryRel FROM SegmentsExperienceAudienceEntryRel segmentsExperienceAudienceEntryRel";

	private static final String
		_SQL_SELECT_SEGMENTSEXPERIENCEAUDIENCEENTRYREL_WHERE =
			"SELECT segmentsExperienceAudienceEntryRel FROM SegmentsExperienceAudienceEntryRel segmentsExperienceAudienceEntryRel WHERE ";

	private static final String
		_SQL_COUNT_SEGMENTSEXPERIENCEAUDIENCEENTRYREL_WHERE =
			"SELECT COUNT(segmentsExperienceAudienceEntryRel) FROM SegmentsExperienceAudienceEntryRel segmentsExperienceAudienceEntryRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SegmentsExperienceAudienceEntryRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsExperienceAudienceEntryRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "segmentsExperienceAudienceEntryRelId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:415800864