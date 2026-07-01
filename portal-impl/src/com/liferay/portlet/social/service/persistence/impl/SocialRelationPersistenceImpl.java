/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portlet.social.model.impl.SocialRelationImpl;
import com.liferay.portlet.social.model.impl.SocialRelationModelImpl;
import com.liferay.social.kernel.exception.NoSuchRelationException;
import com.liferay.social.kernel.model.SocialRelation;
import com.liferay.social.kernel.model.SocialRelationTable;
import com.liferay.social.kernel.service.persistence.SocialRelationPersistence;
import com.liferay.social.kernel.service.persistence.SocialRelationUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the social relation service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class SocialRelationPersistenceImpl
	extends BasePersistenceImpl<SocialRelation, NoSuchRelationException>
	implements SocialRelationPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SocialRelationUtil</code> to access the social relation persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SocialRelationImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<SocialRelation, NoSuchRelationException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the social relations where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social relations
	 */
	@Override
	public List<SocialRelation> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SocialRelation> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social relation in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	@Override
	public SocialRelation findByUuid_First(
			String uuid, OrderByComparator<SocialRelation> orderByComparator)
		throws NoSuchRelationException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first social relation in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	@Override
	public SocialRelation fetchByUuid_First(
		String uuid, OrderByComparator<SocialRelation> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the social relations where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of social relations where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching social relations
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private CollectionPersistenceFinder<SocialRelation, NoSuchRelationException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the social relations where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social relations
	 */
	@Override
	public List<SocialRelation> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SocialRelation> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social relation in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	@Override
	public SocialRelation findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<SocialRelation> orderByComparator)
		throws NoSuchRelationException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first social relation in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	@Override
	public SocialRelation fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<SocialRelation> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the social relations where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of social relations where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching social relations
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<SocialRelation, NoSuchRelationException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the social relations where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social relations
	 */
	@Override
	public List<SocialRelation> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<SocialRelation> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social relation in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	@Override
	public SocialRelation findByCompanyId_First(
			long companyId, OrderByComparator<SocialRelation> orderByComparator)
		throws NoSuchRelationException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first social relation in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	@Override
	public SocialRelation fetchByCompanyId_First(
		long companyId, OrderByComparator<SocialRelation> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the social relations where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of social relations where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching social relations
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private CollectionPersistenceFinder<SocialRelation, NoSuchRelationException>
		_collectionPersistenceFinderByUserId1;

	/**
	 * Returns an ordered range of all the social relations where userId1 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId1 the user id1
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social relations
	 */
	@Override
	public List<SocialRelation> findByUserId1(
		long userId1, int start, int end,
		OrderByComparator<SocialRelation> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId1.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId1}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social relation in the ordered set where userId1 = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	@Override
	public SocialRelation findByUserId1_First(
			long userId1, OrderByComparator<SocialRelation> orderByComparator)
		throws NoSuchRelationException {

		return _collectionPersistenceFinderByUserId1.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId1},
			orderByComparator);
	}

	/**
	 * Returns the first social relation in the ordered set where userId1 = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	@Override
	public SocialRelation fetchByUserId1_First(
		long userId1, OrderByComparator<SocialRelation> orderByComparator) {

		return _collectionPersistenceFinderByUserId1.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId1},
			orderByComparator);
	}

	/**
	 * Removes all the social relations where userId1 = &#63; from the database.
	 *
	 * @param userId1 the user id1
	 */
	@Override
	public void removeByUserId1(long userId1) {
		_collectionPersistenceFinderByUserId1.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId1});
	}

	/**
	 * Returns the number of social relations where userId1 = &#63;.
	 *
	 * @param userId1 the user id1
	 * @return the number of matching social relations
	 */
	@Override
	public int countByUserId1(long userId1) {
		return _collectionPersistenceFinderByUserId1.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId1});
	}

	private CollectionPersistenceFinder<SocialRelation, NoSuchRelationException>
		_collectionPersistenceFinderByUserId2;

	/**
	 * Returns an ordered range of all the social relations where userId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId2 the user id2
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social relations
	 */
	@Override
	public List<SocialRelation> findByUserId2(
		long userId2, int start, int end,
		OrderByComparator<SocialRelation> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId2.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId2}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social relation in the ordered set where userId2 = &#63;.
	 *
	 * @param userId2 the user id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	@Override
	public SocialRelation findByUserId2_First(
			long userId2, OrderByComparator<SocialRelation> orderByComparator)
		throws NoSuchRelationException {

		return _collectionPersistenceFinderByUserId2.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId2},
			orderByComparator);
	}

	/**
	 * Returns the first social relation in the ordered set where userId2 = &#63;.
	 *
	 * @param userId2 the user id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	@Override
	public SocialRelation fetchByUserId2_First(
		long userId2, OrderByComparator<SocialRelation> orderByComparator) {

		return _collectionPersistenceFinderByUserId2.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId2},
			orderByComparator);
	}

	/**
	 * Removes all the social relations where userId2 = &#63; from the database.
	 *
	 * @param userId2 the user id2
	 */
	@Override
	public void removeByUserId2(long userId2) {
		_collectionPersistenceFinderByUserId2.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId2});
	}

	/**
	 * Returns the number of social relations where userId2 = &#63;.
	 *
	 * @param userId2 the user id2
	 * @return the number of matching social relations
	 */
	@Override
	public int countByUserId2(long userId2) {
		return _collectionPersistenceFinderByUserId2.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId2});
	}

	private CollectionPersistenceFinder<SocialRelation, NoSuchRelationException>
		_collectionPersistenceFinderByType;

	/**
	 * Returns an ordered range of all the social relations where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social relations
	 */
	@Override
	public List<SocialRelation> findByType(
		int type, int start, int end,
		OrderByComparator<SocialRelation> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByType.find(
			FinderCacheUtil.getFinderCache(), new Object[] {type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social relation in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	@Override
	public SocialRelation findByType_First(
			int type, OrderByComparator<SocialRelation> orderByComparator)
		throws NoSuchRelationException {

		return _collectionPersistenceFinderByType.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {type},
			orderByComparator);
	}

	/**
	 * Returns the first social relation in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	@Override
	public SocialRelation fetchByType_First(
		int type, OrderByComparator<SocialRelation> orderByComparator) {

		return _collectionPersistenceFinderByType.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {type},
			orderByComparator);
	}

	/**
	 * Removes all the social relations where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	@Override
	public void removeByType(int type) {
		_collectionPersistenceFinderByType.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {type});
	}

	/**
	 * Returns the number of social relations where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching social relations
	 */
	@Override
	public int countByType(int type) {
		return _collectionPersistenceFinderByType.count(
			FinderCacheUtil.getFinderCache(), new Object[] {type});
	}

	private CollectionPersistenceFinder<SocialRelation, NoSuchRelationException>
		_collectionPersistenceFinderByC_T;

	/**
	 * Returns an ordered range of all the social relations where companyId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social relations
	 */
	@Override
	public List<SocialRelation> findByC_T(
		long companyId, int type, int start, int end,
		OrderByComparator<SocialRelation> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_T.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, type},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social relation in the ordered set where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	@Override
	public SocialRelation findByC_T_First(
			long companyId, int type,
			OrderByComparator<SocialRelation> orderByComparator)
		throws NoSuchRelationException {

		return _collectionPersistenceFinderByC_T.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, type},
			orderByComparator);
	}

	/**
	 * Returns the first social relation in the ordered set where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	@Override
	public SocialRelation fetchByC_T_First(
		long companyId, int type,
		OrderByComparator<SocialRelation> orderByComparator) {

		return _collectionPersistenceFinderByC_T.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, type},
			orderByComparator);
	}

	/**
	 * Removes all the social relations where companyId = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 */
	@Override
	public void removeByC_T(long companyId, int type) {
		_collectionPersistenceFinderByC_T.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, type});
	}

	/**
	 * Returns the number of social relations where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the number of matching social relations
	 */
	@Override
	public int countByC_T(long companyId, int type) {
		return _collectionPersistenceFinderByC_T.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, type});
	}

	private CollectionPersistenceFinder<SocialRelation, NoSuchRelationException>
		_collectionPersistenceFinderByU1_U2;

	/**
	 * Returns an ordered range of all the social relations where userId1 = &#63; and userId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social relations
	 */
	@Override
	public List<SocialRelation> findByU1_U2(
		long userId1, long userId2, int start, int end,
		OrderByComparator<SocialRelation> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU1_U2.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId1, userId2},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social relation in the ordered set where userId1 = &#63; and userId2 = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	@Override
	public SocialRelation findByU1_U2_First(
			long userId1, long userId2,
			OrderByComparator<SocialRelation> orderByComparator)
		throws NoSuchRelationException {

		return _collectionPersistenceFinderByU1_U2.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId1, userId2},
			orderByComparator);
	}

	/**
	 * Returns the first social relation in the ordered set where userId1 = &#63; and userId2 = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	@Override
	public SocialRelation fetchByU1_U2_First(
		long userId1, long userId2,
		OrderByComparator<SocialRelation> orderByComparator) {

		return _collectionPersistenceFinderByU1_U2.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId1, userId2},
			orderByComparator);
	}

	/**
	 * Removes all the social relations where userId1 = &#63; and userId2 = &#63; from the database.
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 */
	@Override
	public void removeByU1_U2(long userId1, long userId2) {
		_collectionPersistenceFinderByU1_U2.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId1, userId2});
	}

	/**
	 * Returns the number of social relations where userId1 = &#63; and userId2 = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @return the number of matching social relations
	 */
	@Override
	public int countByU1_U2(long userId1, long userId2) {
		return _collectionPersistenceFinderByU1_U2.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId1, userId2});
	}

	private CollectionPersistenceFinder<SocialRelation, NoSuchRelationException>
		_collectionPersistenceFinderByU1_T;

	/**
	 * Returns an ordered range of all the social relations where userId1 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId1 the user id1
	 * @param type the type
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social relations
	 */
	@Override
	public List<SocialRelation> findByU1_T(
		long userId1, int type, int start, int end,
		OrderByComparator<SocialRelation> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU1_T.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId1, type},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social relation in the ordered set where userId1 = &#63; and type = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	@Override
	public SocialRelation findByU1_T_First(
			long userId1, int type,
			OrderByComparator<SocialRelation> orderByComparator)
		throws NoSuchRelationException {

		return _collectionPersistenceFinderByU1_T.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId1, type},
			orderByComparator);
	}

	/**
	 * Returns the first social relation in the ordered set where userId1 = &#63; and type = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	@Override
	public SocialRelation fetchByU1_T_First(
		long userId1, int type,
		OrderByComparator<SocialRelation> orderByComparator) {

		return _collectionPersistenceFinderByU1_T.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId1, type},
			orderByComparator);
	}

	/**
	 * Removes all the social relations where userId1 = &#63; and type = &#63; from the database.
	 *
	 * @param userId1 the user id1
	 * @param type the type
	 */
	@Override
	public void removeByU1_T(long userId1, int type) {
		_collectionPersistenceFinderByU1_T.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId1, type});
	}

	/**
	 * Returns the number of social relations where userId1 = &#63; and type = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param type the type
	 * @return the number of matching social relations
	 */
	@Override
	public int countByU1_T(long userId1, int type) {
		return _collectionPersistenceFinderByU1_T.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId1, type});
	}

	private CollectionPersistenceFinder<SocialRelation, NoSuchRelationException>
		_collectionPersistenceFinderByU2_T;

	/**
	 * Returns an ordered range of all the social relations where userId2 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId2 the user id2
	 * @param type the type
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social relations
	 */
	@Override
	public List<SocialRelation> findByU2_T(
		long userId2, int type, int start, int end,
		OrderByComparator<SocialRelation> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU2_T.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId2, type},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social relation in the ordered set where userId2 = &#63; and type = &#63;.
	 *
	 * @param userId2 the user id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	@Override
	public SocialRelation findByU2_T_First(
			long userId2, int type,
			OrderByComparator<SocialRelation> orderByComparator)
		throws NoSuchRelationException {

		return _collectionPersistenceFinderByU2_T.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId2, type},
			orderByComparator);
	}

	/**
	 * Returns the first social relation in the ordered set where userId2 = &#63; and type = &#63;.
	 *
	 * @param userId2 the user id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	@Override
	public SocialRelation fetchByU2_T_First(
		long userId2, int type,
		OrderByComparator<SocialRelation> orderByComparator) {

		return _collectionPersistenceFinderByU2_T.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId2, type},
			orderByComparator);
	}

	/**
	 * Removes all the social relations where userId2 = &#63; and type = &#63; from the database.
	 *
	 * @param userId2 the user id2
	 * @param type the type
	 */
	@Override
	public void removeByU2_T(long userId2, int type) {
		_collectionPersistenceFinderByU2_T.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId2, type});
	}

	/**
	 * Returns the number of social relations where userId2 = &#63; and type = &#63;.
	 *
	 * @param userId2 the user id2
	 * @param type the type
	 * @return the number of matching social relations
	 */
	@Override
	public int countByU2_T(long userId2, int type) {
		return _collectionPersistenceFinderByU2_T.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId2, type});
	}

	private UniquePersistenceFinder<SocialRelation, NoSuchRelationException>
		_uniquePersistenceFinderByU1_U2_T;

	/**
	 * Returns the social relation where userId1 = &#63; and userId2 = &#63; and type = &#63; or throws a <code>NoSuchRelationException</code> if it could not be found.
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @param type the type
	 * @return the matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	@Override
	public SocialRelation findByU1_U2_T(long userId1, long userId2, int type)
		throws NoSuchRelationException {

		return _uniquePersistenceFinderByU1_U2_T.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId1, userId2, type});
	}

	/**
	 * Returns the social relation where userId1 = &#63; and userId2 = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	@Override
	public SocialRelation fetchByU1_U2_T(
		long userId1, long userId2, int type, boolean useFinderCache) {

		return _uniquePersistenceFinderByU1_U2_T.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId1, userId2, type}, useFinderCache);
	}

	/**
	 * Removes the social relation where userId1 = &#63; and userId2 = &#63; and type = &#63; from the database.
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @param type the type
	 * @return the social relation that was removed
	 */
	@Override
	public SocialRelation removeByU1_U2_T(long userId1, long userId2, int type)
		throws NoSuchRelationException {

		SocialRelation socialRelation = findByU1_U2_T(userId1, userId2, type);

		return remove(socialRelation);
	}

	/**
	 * Returns the number of social relations where userId1 = &#63; and userId2 = &#63; and type = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @param type the type
	 * @return the number of matching social relations
	 */
	@Override
	public int countByU1_U2_T(long userId1, long userId2, int type) {
		return _uniquePersistenceFinderByU1_U2_T.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId1, userId2, type});
	}

	public SocialRelationPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(SocialRelation.class);

		setModelImplClass(SocialRelationImpl.class);
		setModelPKClass(long.class);

		setTable(SocialRelationTable.INSTANCE);
	}

	/**
	 * Creates a new social relation with the primary key. Does not add the social relation to the database.
	 *
	 * @param relationId the primary key for the new social relation
	 * @return the new social relation
	 */
	@Override
	public SocialRelation create(long relationId) {
		SocialRelation socialRelation = new SocialRelationImpl();

		socialRelation.setNew(true);
		socialRelation.setPrimaryKey(relationId);

		String uuid = PortalUUIDUtil.generate();

		socialRelation.setUuid(uuid);

		socialRelation.setCompanyId(CompanyThreadLocal.getCompanyId());

		return socialRelation;
	}

	/**
	 * Removes the social relation with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param relationId the primary key of the social relation
	 * @return the social relation that was removed
	 * @throws NoSuchRelationException if a social relation with the primary key could not be found
	 */
	@Override
	public SocialRelation remove(long relationId)
		throws NoSuchRelationException {

		return remove((Serializable)relationId);
	}

	@Override
	protected SocialRelation removeImpl(SocialRelation socialRelation) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(socialRelation)) {
				socialRelation = (SocialRelation)session.get(
					SocialRelationImpl.class,
					socialRelation.getPrimaryKeyObj());
			}

			if ((socialRelation != null) &&
				CTPersistenceHelperUtil.isRemove(socialRelation)) {

				session.delete(socialRelation);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (socialRelation != null) {
			clearCache(socialRelation);
		}

		return socialRelation;
	}

	@Override
	public SocialRelation updateImpl(SocialRelation socialRelation) {
		boolean isNew = socialRelation.isNew();

		if (!(socialRelation instanceof SocialRelationModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(socialRelation.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					socialRelation);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in socialRelation proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SocialRelation implementation " +
					socialRelation.getClass());
		}

		SocialRelationModelImpl socialRelationModelImpl =
			(SocialRelationModelImpl)socialRelation;

		if (Validator.isNull(socialRelation.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			socialRelation.setUuid(uuid);
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(socialRelation)) {
				if (!isNew) {
					session.evict(
						SocialRelationImpl.class,
						socialRelation.getPrimaryKeyObj());
				}

				session.save(socialRelation);
			}
			else {
				socialRelation = (SocialRelation)session.merge(socialRelation);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(socialRelation, false);

		if (isNew) {
			socialRelation.setNew(false);
		}

		socialRelation.resetOriginalValues();

		return socialRelation;
	}

	/**
	 * Returns the social relation with the primary key or throws a <code>NoSuchRelationException</code> if it could not be found.
	 *
	 * @param relationId the primary key of the social relation
	 * @return the social relation
	 * @throws NoSuchRelationException if a social relation with the primary key could not be found
	 */
	@Override
	public SocialRelation findByPrimaryKey(long relationId)
		throws NoSuchRelationException {

		return findByPrimaryKey((Serializable)relationId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the social relation with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param relationId the primary key of the social relation
	 * @return the social relation, or <code>null</code> if a social relation with the primary key could not be found
	 */
	@Override
	public SocialRelation fetchByPrimaryKey(long relationId) {
		return fetchByPrimaryKey((Serializable)relationId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "relationId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SOCIALRELATION;
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
		return SocialRelationModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "SocialRelation";
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
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("createDate");
		ctMergeColumnNames.add("userId1");
		ctMergeColumnNames.add("userId2");
		ctMergeColumnNames.add("type_");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("relationId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"userId1", "userId2", "type_"});
	}

	/**
	 * Initializes the social relation persistence.
	 */
	public void afterPropertiesSet() {
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
			_SQL_SELECT_SOCIALRELATION_WHERE, _SQL_COUNT_SOCIALRELATION_WHERE,
			SocialRelationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"socialRelation.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, SocialRelation::getUuid));

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
				_SQL_SELECT_SOCIALRELATION_WHERE,
				_SQL_COUNT_SOCIALRELATION_WHERE,
				SocialRelationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"socialRelation.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					SocialRelation::getUuid),
				new FinderColumn<>(
					"socialRelation.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, SocialRelation::getCompanyId));

		_collectionPersistenceFinderByCompanyId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, false),
				_SQL_SELECT_SOCIALRELATION_WHERE,
				_SQL_COUNT_SOCIALRELATION_WHERE,
				SocialRelationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"socialRelation.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, SocialRelation::getCompanyId));

		_collectionPersistenceFinderByUserId1 =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId1",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId1"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId1",
					new String[] {Long.class.getName()},
					new String[] {"userId1"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId1",
					new String[] {Long.class.getName()},
					new String[] {"userId1"}, false),
				_SQL_SELECT_SOCIALRELATION_WHERE,
				_SQL_COUNT_SOCIALRELATION_WHERE,
				SocialRelationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"socialRelation.", "userId1", FinderColumn.Type.LONG, "=",
					true, true, SocialRelation::getUserId1));

		_collectionPersistenceFinderByUserId2 =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId2",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId2"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId2",
					new String[] {Long.class.getName()},
					new String[] {"userId2"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId2",
					new String[] {Long.class.getName()},
					new String[] {"userId2"}, false),
				_SQL_SELECT_SOCIALRELATION_WHERE,
				_SQL_COUNT_SOCIALRELATION_WHERE,
				SocialRelationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"socialRelation.", "userId2", FinderColumn.Type.LONG, "=",
					true, true, SocialRelation::getUserId2));

		_collectionPersistenceFinderByType = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByType",
				new String[] {
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByType",
				new String[] {Integer.class.getName()}, new String[] {"type_"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByType",
				new String[] {Integer.class.getName()}, new String[] {"type_"},
				false),
			_SQL_SELECT_SOCIALRELATION_WHERE, _SQL_COUNT_SOCIALRELATION_WHERE,
			SocialRelationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"socialRelation.", "type", "type_", FinderColumn.Type.INTEGER,
				"=", true, true, SocialRelation::getType));

		_collectionPersistenceFinderByC_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_T",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"companyId", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"companyId", "type_"}, false),
			_SQL_SELECT_SOCIALRELATION_WHERE, _SQL_COUNT_SOCIALRELATION_WHERE,
			SocialRelationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"socialRelation.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, SocialRelation::getCompanyId),
			new FinderColumn<>(
				"socialRelation.", "type", "type_", FinderColumn.Type.INTEGER,
				"=", true, true, SocialRelation::getType));

		_collectionPersistenceFinderByU1_U2 = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU1_U2",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"userId1", "userId2"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU1_U2",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId1", "userId2"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU1_U2",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId1", "userId2"}, false),
			_SQL_SELECT_SOCIALRELATION_WHERE, _SQL_COUNT_SOCIALRELATION_WHERE,
			SocialRelationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"socialRelation.", "userId1", FinderColumn.Type.LONG, "=", true,
				true, SocialRelation::getUserId1),
			new FinderColumn<>(
				"socialRelation.", "userId2", FinderColumn.Type.LONG, "=", true,
				true, SocialRelation::getUserId2));

		_collectionPersistenceFinderByU1_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU1_T",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"userId1", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU1_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"userId1", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU1_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"userId1", "type_"}, false),
			_SQL_SELECT_SOCIALRELATION_WHERE, _SQL_COUNT_SOCIALRELATION_WHERE,
			SocialRelationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"socialRelation.", "userId1", FinderColumn.Type.LONG, "=", true,
				true, SocialRelation::getUserId1),
			new FinderColumn<>(
				"socialRelation.", "type", "type_", FinderColumn.Type.INTEGER,
				"=", true, true, SocialRelation::getType));

		_collectionPersistenceFinderByU2_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU2_T",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"userId2", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU2_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"userId2", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU2_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"userId2", "type_"}, false),
			_SQL_SELECT_SOCIALRELATION_WHERE, _SQL_COUNT_SOCIALRELATION_WHERE,
			SocialRelationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"socialRelation.", "userId2", FinderColumn.Type.LONG, "=", true,
				true, SocialRelation::getUserId2),
			new FinderColumn<>(
				"socialRelation.", "type", "type_", FinderColumn.Type.INTEGER,
				"=", true, true, SocialRelation::getType));

		_uniquePersistenceFinderByU1_U2_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU1_U2_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"userId1", "userId2", "type_"}, 0, 0, false,
				SocialRelation::getUserId1, SocialRelation::getUserId2,
				SocialRelation::getType),
			_SQL_SELECT_SOCIALRELATION_WHERE, "",
			new FinderColumn<>(
				"socialRelation.", "userId1", FinderColumn.Type.LONG, "=", true,
				true, SocialRelation::getUserId1),
			new FinderColumn<>(
				"socialRelation.", "userId2", FinderColumn.Type.LONG, "=", true,
				true, SocialRelation::getUserId2),
			new FinderColumn<>(
				"socialRelation.", "type", "type_", FinderColumn.Type.INTEGER,
				"=", true, true, SocialRelation::getType));

		SocialRelationUtil.setPersistence(this);
	}

	public void destroy() {
		SocialRelationUtil.setPersistence(null);

		EntityCacheUtil.removeCache(SocialRelationImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		SocialRelationModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SOCIALRELATION =
		"SELECT socialRelation FROM SocialRelation socialRelation";

	private static final String _SQL_SELECT_SOCIALRELATION_WHERE =
		"SELECT socialRelation FROM SocialRelation socialRelation WHERE ";

	private static final String _SQL_COUNT_SOCIALRELATION_WHERE =
		"SELECT COUNT(socialRelation) FROM SocialRelation socialRelation WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SocialRelation exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SocialRelationPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1780999389