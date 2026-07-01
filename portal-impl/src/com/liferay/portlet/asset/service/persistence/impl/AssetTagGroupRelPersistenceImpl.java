/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.persistence.impl;

import com.liferay.asset.kernel.exception.NoSuchTagGroupRelException;
import com.liferay.asset.kernel.model.AssetTagGroupRel;
import com.liferay.asset.kernel.model.AssetTagGroupRelTable;
import com.liferay.asset.kernel.service.persistence.AssetTagGroupRelPersistence;
import com.liferay.asset.kernel.service.persistence.AssetTagGroupRelUtil;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
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
import com.liferay.portlet.asset.model.impl.AssetTagGroupRelImpl;
import com.liferay.portlet.asset.model.impl.AssetTagGroupRelModelImpl;

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
 * The persistence implementation for the asset tag group rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AssetTagGroupRelPersistenceImpl
	extends BasePersistenceImpl<AssetTagGroupRel, NoSuchTagGroupRelException>
	implements AssetTagGroupRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AssetTagGroupRelUtil</code> to access the asset tag group rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AssetTagGroupRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AssetTagGroupRel, NoSuchTagGroupRelException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the asset tag group rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset tag group rels
	 * @param end the upper bound of the range of asset tag group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset tag group rels
	 */
	@Override
	public List<AssetTagGroupRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetTagGroupRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset tag group rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag group rel
	 * @throws NoSuchTagGroupRelException if a matching asset tag group rel could not be found
	 */
	@Override
	public AssetTagGroupRel findByUuid_First(
			String uuid, OrderByComparator<AssetTagGroupRel> orderByComparator)
		throws NoSuchTagGroupRelException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first asset tag group rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag group rel, or <code>null</code> if a matching asset tag group rel could not be found
	 */
	@Override
	public AssetTagGroupRel fetchByUuid_First(
		String uuid, OrderByComparator<AssetTagGroupRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the asset tag group rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of asset tag group rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset tag group rels
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<AssetTagGroupRel, NoSuchTagGroupRelException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the asset tag group rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchTagGroupRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset tag group rel
	 * @throws NoSuchTagGroupRelException if a matching asset tag group rel could not be found
	 */
	@Override
	public AssetTagGroupRel findByUUID_G(String uuid, long groupId)
		throws NoSuchTagGroupRelException {

		return _uniquePersistenceFinderByUUID_G.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	/**
	 * Returns the asset tag group rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset tag group rel, or <code>null</code> if a matching asset tag group rel could not be found
	 */
	@Override
	public AssetTagGroupRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId},
			useFinderCache);
	}

	/**
	 * Removes the asset tag group rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset tag group rel that was removed
	 */
	@Override
	public AssetTagGroupRel removeByUUID_G(String uuid, long groupId)
		throws NoSuchTagGroupRelException {

		AssetTagGroupRel assetTagGroupRel = findByUUID_G(uuid, groupId);

		return remove(assetTagGroupRel);
	}

	/**
	 * Returns the number of asset tag group rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset tag group rels
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<AssetTagGroupRel, NoSuchTagGroupRelException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the asset tag group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset tag group rels
	 * @param end the upper bound of the range of asset tag group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset tag group rels
	 */
	@Override
	public List<AssetTagGroupRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetTagGroupRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset tag group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag group rel
	 * @throws NoSuchTagGroupRelException if a matching asset tag group rel could not be found
	 */
	@Override
	public AssetTagGroupRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<AssetTagGroupRel> orderByComparator)
		throws NoSuchTagGroupRelException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first asset tag group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag group rel, or <code>null</code> if a matching asset tag group rel could not be found
	 */
	@Override
	public AssetTagGroupRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<AssetTagGroupRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the asset tag group rels where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of asset tag group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset tag group rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<AssetTagGroupRel, NoSuchTagGroupRelException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the asset tag group rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset tag group rels
	 * @param end the upper bound of the range of asset tag group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset tag group rels
	 */
	@Override
	public List<AssetTagGroupRel> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetTagGroupRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset tag group rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag group rel
	 * @throws NoSuchTagGroupRelException if a matching asset tag group rel could not be found
	 */
	@Override
	public AssetTagGroupRel findByGroupId_First(
			long groupId, OrderByComparator<AssetTagGroupRel> orderByComparator)
		throws NoSuchTagGroupRelException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first asset tag group rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag group rel, or <code>null</code> if a matching asset tag group rel could not be found
	 */
	@Override
	public AssetTagGroupRel fetchByGroupId_First(
		long groupId, OrderByComparator<AssetTagGroupRel> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Removes all the asset tag group rels where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of asset tag group rels where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset tag group rels
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<AssetTagGroupRel, NoSuchTagGroupRelException>
			_collectionPersistenceFinderByTagId;

	/**
	 * Returns an ordered range of all the asset tag group rels where tagId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param tagId the tag ID
	 * @param start the lower bound of the range of asset tag group rels
	 * @param end the upper bound of the range of asset tag group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset tag group rels
	 */
	@Override
	public List<AssetTagGroupRel> findByTagId(
		long tagId, int start, int end,
		OrderByComparator<AssetTagGroupRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByTagId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {tagId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset tag group rel in the ordered set where tagId = &#63;.
	 *
	 * @param tagId the tag ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag group rel
	 * @throws NoSuchTagGroupRelException if a matching asset tag group rel could not be found
	 */
	@Override
	public AssetTagGroupRel findByTagId_First(
			long tagId, OrderByComparator<AssetTagGroupRel> orderByComparator)
		throws NoSuchTagGroupRelException {

		return _collectionPersistenceFinderByTagId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {tagId},
			orderByComparator);
	}

	/**
	 * Returns the first asset tag group rel in the ordered set where tagId = &#63;.
	 *
	 * @param tagId the tag ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag group rel, or <code>null</code> if a matching asset tag group rel could not be found
	 */
	@Override
	public AssetTagGroupRel fetchByTagId_First(
		long tagId, OrderByComparator<AssetTagGroupRel> orderByComparator) {

		return _collectionPersistenceFinderByTagId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {tagId},
			orderByComparator);
	}

	/**
	 * Removes all the asset tag group rels where tagId = &#63; from the database.
	 *
	 * @param tagId the tag ID
	 */
	@Override
	public void removeByTagId(long tagId) {
		_collectionPersistenceFinderByTagId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {tagId});
	}

	/**
	 * Returns the number of asset tag group rels where tagId = &#63;.
	 *
	 * @param tagId the tag ID
	 * @return the number of matching asset tag group rels
	 */
	@Override
	public int countByTagId(long tagId) {
		return _collectionPersistenceFinderByTagId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {tagId});
	}

	private UniquePersistenceFinder
		<AssetTagGroupRel, NoSuchTagGroupRelException>
			_uniquePersistenceFinderByG_T;

	/**
	 * Returns the asset tag group rel where groupId = &#63; and tagId = &#63; or throws a <code>NoSuchTagGroupRelException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param tagId the tag ID
	 * @return the matching asset tag group rel
	 * @throws NoSuchTagGroupRelException if a matching asset tag group rel could not be found
	 */
	@Override
	public AssetTagGroupRel findByG_T(long groupId, long tagId)
		throws NoSuchTagGroupRelException {

		return _uniquePersistenceFinderByG_T.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, tagId});
	}

	/**
	 * Returns the asset tag group rel where groupId = &#63; and tagId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param tagId the tag ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset tag group rel, or <code>null</code> if a matching asset tag group rel could not be found
	 */
	@Override
	public AssetTagGroupRel fetchByG_T(
		long groupId, long tagId, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_T.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, tagId},
			useFinderCache);
	}

	/**
	 * Removes the asset tag group rel where groupId = &#63; and tagId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param tagId the tag ID
	 * @return the asset tag group rel that was removed
	 */
	@Override
	public AssetTagGroupRel removeByG_T(long groupId, long tagId)
		throws NoSuchTagGroupRelException {

		AssetTagGroupRel assetTagGroupRel = findByG_T(groupId, tagId);

		return remove(assetTagGroupRel);
	}

	/**
	 * Returns the number of asset tag group rels where groupId = &#63; and tagId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param tagId the tag ID
	 * @return the number of matching asset tag group rels
	 */
	@Override
	public int countByG_T(long groupId, long tagId) {
		return _uniquePersistenceFinderByG_T.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, tagId});
	}

	public AssetTagGroupRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(AssetTagGroupRel.class);

		setModelImplClass(AssetTagGroupRelImpl.class);
		setModelPKClass(long.class);

		setTable(AssetTagGroupRelTable.INSTANCE);
	}

	/**
	 * Creates a new asset tag group rel with the primary key. Does not add the asset tag group rel to the database.
	 *
	 * @param assetTagGroupRelId the primary key for the new asset tag group rel
	 * @return the new asset tag group rel
	 */
	@Override
	public AssetTagGroupRel create(long assetTagGroupRelId) {
		AssetTagGroupRel assetTagGroupRel = new AssetTagGroupRelImpl();

		assetTagGroupRel.setNew(true);
		assetTagGroupRel.setPrimaryKey(assetTagGroupRelId);

		String uuid = PortalUUIDUtil.generate();

		assetTagGroupRel.setUuid(uuid);

		assetTagGroupRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return assetTagGroupRel;
	}

	/**
	 * Removes the asset tag group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param assetTagGroupRelId the primary key of the asset tag group rel
	 * @return the asset tag group rel that was removed
	 * @throws NoSuchTagGroupRelException if a asset tag group rel with the primary key could not be found
	 */
	@Override
	public AssetTagGroupRel remove(long assetTagGroupRelId)
		throws NoSuchTagGroupRelException {

		return remove((Serializable)assetTagGroupRelId);
	}

	@Override
	protected AssetTagGroupRel removeImpl(AssetTagGroupRel assetTagGroupRel) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(assetTagGroupRel)) {
				assetTagGroupRel = (AssetTagGroupRel)session.get(
					AssetTagGroupRelImpl.class,
					assetTagGroupRel.getPrimaryKeyObj());
			}

			if ((assetTagGroupRel != null) &&
				CTPersistenceHelperUtil.isRemove(assetTagGroupRel)) {

				session.delete(assetTagGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (assetTagGroupRel != null) {
			clearCache(assetTagGroupRel);
		}

		return assetTagGroupRel;
	}

	@Override
	public AssetTagGroupRel updateImpl(AssetTagGroupRel assetTagGroupRel) {
		boolean isNew = assetTagGroupRel.isNew();

		if (!(assetTagGroupRel instanceof AssetTagGroupRelModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(assetTagGroupRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					assetTagGroupRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in assetTagGroupRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AssetTagGroupRel implementation " +
					assetTagGroupRel.getClass());
		}

		AssetTagGroupRelModelImpl assetTagGroupRelModelImpl =
			(AssetTagGroupRelModelImpl)assetTagGroupRel;

		if (Validator.isNull(assetTagGroupRel.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			assetTagGroupRel.setUuid(uuid);
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(assetTagGroupRel)) {
				if (!isNew) {
					session.evict(
						AssetTagGroupRelImpl.class,
						assetTagGroupRel.getPrimaryKeyObj());
				}

				session.save(assetTagGroupRel);
			}
			else {
				assetTagGroupRel = (AssetTagGroupRel)session.merge(
					assetTagGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(assetTagGroupRel, false);

		if (isNew) {
			assetTagGroupRel.setNew(false);
		}

		assetTagGroupRel.resetOriginalValues();

		return assetTagGroupRel;
	}

	/**
	 * Returns the asset tag group rel with the primary key or throws a <code>NoSuchTagGroupRelException</code> if it could not be found.
	 *
	 * @param assetTagGroupRelId the primary key of the asset tag group rel
	 * @return the asset tag group rel
	 * @throws NoSuchTagGroupRelException if a asset tag group rel with the primary key could not be found
	 */
	@Override
	public AssetTagGroupRel findByPrimaryKey(long assetTagGroupRelId)
		throws NoSuchTagGroupRelException {

		return findByPrimaryKey((Serializable)assetTagGroupRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the asset tag group rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param assetTagGroupRelId the primary key of the asset tag group rel
	 * @return the asset tag group rel, or <code>null</code> if a asset tag group rel with the primary key could not be found
	 */
	@Override
	public AssetTagGroupRel fetchByPrimaryKey(long assetTagGroupRelId) {
		return fetchByPrimaryKey((Serializable)assetTagGroupRelId);
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
		return "assetTagGroupRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ASSETTAGGROUPREL;
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
		return AssetTagGroupRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AssetTagGroupRel";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctMergeColumnNames.add("tagId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("assetTagGroupRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});
	}

	/**
	 * Initializes the asset tag group rel persistence.
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
			_SQL_SELECT_ASSETTAGGROUPREL_WHERE,
			_SQL_COUNT_ASSETTAGGROUPREL_WHERE,
			AssetTagGroupRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"assetTagGroupRel.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, AssetTagGroupRel::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(AssetTagGroupRel::getUuid),
				AssetTagGroupRel::getGroupId),
			_SQL_SELECT_ASSETTAGGROUPREL_WHERE, "",
			new FinderColumn<>(
				"assetTagGroupRel.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, AssetTagGroupRel::getUuid),
			new FinderColumn<>(
				"assetTagGroupRel.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, AssetTagGroupRel::getGroupId));

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
				_SQL_SELECT_ASSETTAGGROUPREL_WHERE,
				_SQL_COUNT_ASSETTAGGROUPREL_WHERE,
				AssetTagGroupRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"assetTagGroupRel.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					AssetTagGroupRel::getUuid),
				new FinderColumn<>(
					"assetTagGroupRel.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, AssetTagGroupRel::getCompanyId));

		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
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
				_SQL_SELECT_ASSETTAGGROUPREL_WHERE,
				_SQL_COUNT_ASSETTAGGROUPREL_WHERE,
				AssetTagGroupRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"assetTagGroupRel.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, AssetTagGroupRel::getGroupId));

		_collectionPersistenceFinderByTagId = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByTagId",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"tagId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByTagId",
				new String[] {Long.class.getName()}, new String[] {"tagId"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByTagId",
				new String[] {Long.class.getName()}, new String[] {"tagId"},
				false),
			_SQL_SELECT_ASSETTAGGROUPREL_WHERE,
			_SQL_COUNT_ASSETTAGGROUPREL_WHERE,
			AssetTagGroupRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"assetTagGroupRel.", "tagId", FinderColumn.Type.LONG, "=", true,
				true, AssetTagGroupRel::getTagId));

		_uniquePersistenceFinderByG_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_T",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "tagId"}, 0, 0, false,
				AssetTagGroupRel::getGroupId, AssetTagGroupRel::getTagId),
			_SQL_SELECT_ASSETTAGGROUPREL_WHERE, "",
			new FinderColumn<>(
				"assetTagGroupRel.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, AssetTagGroupRel::getGroupId),
			new FinderColumn<>(
				"assetTagGroupRel.", "tagId", FinderColumn.Type.LONG, "=", true,
				true, AssetTagGroupRel::getTagId));

		AssetTagGroupRelUtil.setPersistence(this);
	}

	public void destroy() {
		AssetTagGroupRelUtil.setPersistence(null);

		EntityCacheUtil.removeCache(AssetTagGroupRelImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		AssetTagGroupRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ASSETTAGGROUPREL =
		"SELECT assetTagGroupRel FROM AssetTagGroupRel assetTagGroupRel";

	private static final String _SQL_SELECT_ASSETTAGGROUPREL_WHERE =
		"SELECT assetTagGroupRel FROM AssetTagGroupRel assetTagGroupRel WHERE ";

	private static final String _SQL_COUNT_ASSETTAGGROUPREL_WHERE =
		"SELECT COUNT(assetTagGroupRel) FROM AssetTagGroupRel assetTagGroupRel WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2115172177