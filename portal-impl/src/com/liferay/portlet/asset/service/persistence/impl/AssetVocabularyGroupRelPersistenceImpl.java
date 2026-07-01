/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.persistence.impl;

import com.liferay.asset.kernel.exception.NoSuchVocabularyGroupRelException;
import com.liferay.asset.kernel.model.AssetVocabularyGroupRel;
import com.liferay.asset.kernel.model.AssetVocabularyGroupRelTable;
import com.liferay.asset.kernel.service.persistence.AssetVocabularyGroupRelPersistence;
import com.liferay.asset.kernel.service.persistence.AssetVocabularyGroupRelUtil;
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
import com.liferay.portlet.asset.model.impl.AssetVocabularyGroupRelImpl;
import com.liferay.portlet.asset.model.impl.AssetVocabularyGroupRelModelImpl;

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
 * The persistence implementation for the asset vocabulary group rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AssetVocabularyGroupRelPersistenceImpl
	extends BasePersistenceImpl
		<AssetVocabularyGroupRel, NoSuchVocabularyGroupRelException>
	implements AssetVocabularyGroupRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AssetVocabularyGroupRelUtil</code> to access the asset vocabulary group rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AssetVocabularyGroupRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AssetVocabularyGroupRel, NoSuchVocabularyGroupRelException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the asset vocabulary group rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset vocabulary group rels
	 * @param end the upper bound of the range of asset vocabulary group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabulary group rels
	 */
	@Override
	public List<AssetVocabularyGroupRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetVocabularyGroupRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset vocabulary group rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary group rel
	 * @throws NoSuchVocabularyGroupRelException if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel findByUuid_First(
			String uuid,
			OrderByComparator<AssetVocabularyGroupRel> orderByComparator)
		throws NoSuchVocabularyGroupRelException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first asset vocabulary group rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary group rel, or <code>null</code> if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel fetchByUuid_First(
		String uuid,
		OrderByComparator<AssetVocabularyGroupRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the asset vocabulary group rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of asset vocabulary group rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset vocabulary group rels
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<AssetVocabularyGroupRel, NoSuchVocabularyGroupRelException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the asset vocabulary group rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchVocabularyGroupRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset vocabulary group rel
	 * @throws NoSuchVocabularyGroupRelException if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel findByUUID_G(String uuid, long groupId)
		throws NoSuchVocabularyGroupRelException {

		return _uniquePersistenceFinderByUUID_G.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	/**
	 * Returns the asset vocabulary group rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset vocabulary group rel, or <code>null</code> if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId},
			useFinderCache);
	}

	/**
	 * Removes the asset vocabulary group rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset vocabulary group rel that was removed
	 */
	@Override
	public AssetVocabularyGroupRel removeByUUID_G(String uuid, long groupId)
		throws NoSuchVocabularyGroupRelException {

		AssetVocabularyGroupRel assetVocabularyGroupRel = findByUUID_G(
			uuid, groupId);

		return remove(assetVocabularyGroupRel);
	}

	/**
	 * Returns the number of asset vocabulary group rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset vocabulary group rels
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<AssetVocabularyGroupRel, NoSuchVocabularyGroupRelException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the asset vocabulary group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset vocabulary group rels
	 * @param end the upper bound of the range of asset vocabulary group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabulary group rels
	 */
	@Override
	public List<AssetVocabularyGroupRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetVocabularyGroupRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset vocabulary group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary group rel
	 * @throws NoSuchVocabularyGroupRelException if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<AssetVocabularyGroupRel> orderByComparator)
		throws NoSuchVocabularyGroupRelException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first asset vocabulary group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary group rel, or <code>null</code> if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<AssetVocabularyGroupRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the asset vocabulary group rels where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of asset vocabulary group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset vocabulary group rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<AssetVocabularyGroupRel, NoSuchVocabularyGroupRelException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the asset vocabulary group rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset vocabulary group rels
	 * @param end the upper bound of the range of asset vocabulary group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabulary group rels
	 */
	@Override
	public List<AssetVocabularyGroupRel> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetVocabularyGroupRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset vocabulary group rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary group rel
	 * @throws NoSuchVocabularyGroupRelException if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel findByGroupId_First(
			long groupId,
			OrderByComparator<AssetVocabularyGroupRel> orderByComparator)
		throws NoSuchVocabularyGroupRelException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first asset vocabulary group rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary group rel, or <code>null</code> if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel fetchByGroupId_First(
		long groupId,
		OrderByComparator<AssetVocabularyGroupRel> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Removes all the asset vocabulary group rels where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of asset vocabulary group rels where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset vocabulary group rels
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<AssetVocabularyGroupRel, NoSuchVocabularyGroupRelException>
			_collectionPersistenceFinderByVocabularyId;

	/**
	 * Returns an ordered range of all the asset vocabulary group rels where vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset vocabulary group rels
	 * @param end the upper bound of the range of asset vocabulary group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabulary group rels
	 */
	@Override
	public List<AssetVocabularyGroupRel> findByVocabularyId(
		long vocabularyId, int start, int end,
		OrderByComparator<AssetVocabularyGroupRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByVocabularyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {vocabularyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset vocabulary group rel in the ordered set where vocabularyId = &#63;.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary group rel
	 * @throws NoSuchVocabularyGroupRelException if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel findByVocabularyId_First(
			long vocabularyId,
			OrderByComparator<AssetVocabularyGroupRel> orderByComparator)
		throws NoSuchVocabularyGroupRelException {

		return _collectionPersistenceFinderByVocabularyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {vocabularyId},
			orderByComparator);
	}

	/**
	 * Returns the first asset vocabulary group rel in the ordered set where vocabularyId = &#63;.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary group rel, or <code>null</code> if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel fetchByVocabularyId_First(
		long vocabularyId,
		OrderByComparator<AssetVocabularyGroupRel> orderByComparator) {

		return _collectionPersistenceFinderByVocabularyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {vocabularyId},
			orderByComparator);
	}

	/**
	 * Removes all the asset vocabulary group rels where vocabularyId = &#63; from the database.
	 *
	 * @param vocabularyId the vocabulary ID
	 */
	@Override
	public void removeByVocabularyId(long vocabularyId) {
		_collectionPersistenceFinderByVocabularyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {vocabularyId});
	}

	/**
	 * Returns the number of asset vocabulary group rels where vocabularyId = &#63;.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset vocabulary group rels
	 */
	@Override
	public int countByVocabularyId(long vocabularyId) {
		return _collectionPersistenceFinderByVocabularyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {vocabularyId});
	}

	private UniquePersistenceFinder
		<AssetVocabularyGroupRel, NoSuchVocabularyGroupRelException>
			_uniquePersistenceFinderByG_V;

	/**
	 * Returns the asset vocabulary group rel where groupId = &#63; and vocabularyId = &#63; or throws a <code>NoSuchVocabularyGroupRelException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset vocabulary group rel
	 * @throws NoSuchVocabularyGroupRelException if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel findByG_V(long groupId, long vocabularyId)
		throws NoSuchVocabularyGroupRelException {

		return _uniquePersistenceFinderByG_V.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, vocabularyId});
	}

	/**
	 * Returns the asset vocabulary group rel where groupId = &#63; and vocabularyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset vocabulary group rel, or <code>null</code> if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel fetchByG_V(
		long groupId, long vocabularyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_V.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, vocabularyId}, useFinderCache);
	}

	/**
	 * Removes the asset vocabulary group rel where groupId = &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @return the asset vocabulary group rel that was removed
	 */
	@Override
	public AssetVocabularyGroupRel removeByG_V(long groupId, long vocabularyId)
		throws NoSuchVocabularyGroupRelException {

		AssetVocabularyGroupRel assetVocabularyGroupRel = findByG_V(
			groupId, vocabularyId);

		return remove(assetVocabularyGroupRel);
	}

	/**
	 * Returns the number of asset vocabulary group rels where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset vocabulary group rels
	 */
	@Override
	public int countByG_V(long groupId, long vocabularyId) {
		return _uniquePersistenceFinderByG_V.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, vocabularyId});
	}

	private CollectionPersistenceFinder
		<AssetVocabularyGroupRel, NoSuchVocabularyGroupRelException>
			_collectionPersistenceFinderByG_D;

	/**
	 * Returns an ordered range of all the asset vocabulary group rels where groupId = &#63; and depotEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param depotEntryType the depot entry type
	 * @param start the lower bound of the range of asset vocabulary group rels
	 * @param end the upper bound of the range of asset vocabulary group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabulary group rels
	 */
	@Override
	public List<AssetVocabularyGroupRel> findByG_D(
		long groupId, int depotEntryType, int start, int end,
		OrderByComparator<AssetVocabularyGroupRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_D.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, depotEntryType}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset vocabulary group rel in the ordered set where groupId = &#63; and depotEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param depotEntryType the depot entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary group rel
	 * @throws NoSuchVocabularyGroupRelException if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel findByG_D_First(
			long groupId, int depotEntryType,
			OrderByComparator<AssetVocabularyGroupRel> orderByComparator)
		throws NoSuchVocabularyGroupRelException {

		return _collectionPersistenceFinderByG_D.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, depotEntryType}, orderByComparator);
	}

	/**
	 * Returns the first asset vocabulary group rel in the ordered set where groupId = &#63; and depotEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param depotEntryType the depot entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary group rel, or <code>null</code> if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel fetchByG_D_First(
		long groupId, int depotEntryType,
		OrderByComparator<AssetVocabularyGroupRel> orderByComparator) {

		return _collectionPersistenceFinderByG_D.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, depotEntryType}, orderByComparator);
	}

	/**
	 * Removes all the asset vocabulary group rels where groupId = &#63; and depotEntryType = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param depotEntryType the depot entry type
	 */
	@Override
	public void removeByG_D(long groupId, int depotEntryType) {
		_collectionPersistenceFinderByG_D.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, depotEntryType});
	}

	/**
	 * Returns the number of asset vocabulary group rels where groupId = &#63; and depotEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param depotEntryType the depot entry type
	 * @return the number of matching asset vocabulary group rels
	 */
	@Override
	public int countByG_D(long groupId, int depotEntryType) {
		return _collectionPersistenceFinderByG_D.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, depotEntryType});
	}

	private CollectionPersistenceFinder
		<AssetVocabularyGroupRel, NoSuchVocabularyGroupRelException>
			_collectionPersistenceFinderByV_D;

	/**
	 * Returns an ordered range of all the asset vocabulary group rels where vocabularyId = &#63; and depotEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param depotEntryType the depot entry type
	 * @param start the lower bound of the range of asset vocabulary group rels
	 * @param end the upper bound of the range of asset vocabulary group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabulary group rels
	 */
	@Override
	public List<AssetVocabularyGroupRel> findByV_D(
		long vocabularyId, int depotEntryType, int start, int end,
		OrderByComparator<AssetVocabularyGroupRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByV_D.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {vocabularyId, depotEntryType}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset vocabulary group rel in the ordered set where vocabularyId = &#63; and depotEntryType = &#63;.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param depotEntryType the depot entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary group rel
	 * @throws NoSuchVocabularyGroupRelException if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel findByV_D_First(
			long vocabularyId, int depotEntryType,
			OrderByComparator<AssetVocabularyGroupRel> orderByComparator)
		throws NoSuchVocabularyGroupRelException {

		return _collectionPersistenceFinderByV_D.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {vocabularyId, depotEntryType}, orderByComparator);
	}

	/**
	 * Returns the first asset vocabulary group rel in the ordered set where vocabularyId = &#63; and depotEntryType = &#63;.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param depotEntryType the depot entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary group rel, or <code>null</code> if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel fetchByV_D_First(
		long vocabularyId, int depotEntryType,
		OrderByComparator<AssetVocabularyGroupRel> orderByComparator) {

		return _collectionPersistenceFinderByV_D.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {vocabularyId, depotEntryType}, orderByComparator);
	}

	/**
	 * Removes all the asset vocabulary group rels where vocabularyId = &#63; and depotEntryType = &#63; from the database.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param depotEntryType the depot entry type
	 */
	@Override
	public void removeByV_D(long vocabularyId, int depotEntryType) {
		_collectionPersistenceFinderByV_D.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {vocabularyId, depotEntryType});
	}

	/**
	 * Returns the number of asset vocabulary group rels where vocabularyId = &#63; and depotEntryType = &#63;.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param depotEntryType the depot entry type
	 * @return the number of matching asset vocabulary group rels
	 */
	@Override
	public int countByV_D(long vocabularyId, int depotEntryType) {
		return _collectionPersistenceFinderByV_D.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {vocabularyId, depotEntryType});
	}

	private UniquePersistenceFinder
		<AssetVocabularyGroupRel, NoSuchVocabularyGroupRelException>
			_uniquePersistenceFinderByG_V_D;

	/**
	 * Returns the asset vocabulary group rel where groupId = &#63; and vocabularyId = &#63; and depotEntryType = &#63; or throws a <code>NoSuchVocabularyGroupRelException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param depotEntryType the depot entry type
	 * @return the matching asset vocabulary group rel
	 * @throws NoSuchVocabularyGroupRelException if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel findByG_V_D(
			long groupId, long vocabularyId, int depotEntryType)
		throws NoSuchVocabularyGroupRelException {

		return _uniquePersistenceFinderByG_V_D.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, vocabularyId, depotEntryType});
	}

	/**
	 * Returns the asset vocabulary group rel where groupId = &#63; and vocabularyId = &#63; and depotEntryType = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param depotEntryType the depot entry type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset vocabulary group rel, or <code>null</code> if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel fetchByG_V_D(
		long groupId, long vocabularyId, int depotEntryType,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_V_D.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, vocabularyId, depotEntryType},
			useFinderCache);
	}

	/**
	 * Removes the asset vocabulary group rel where groupId = &#63; and vocabularyId = &#63; and depotEntryType = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param depotEntryType the depot entry type
	 * @return the asset vocabulary group rel that was removed
	 */
	@Override
	public AssetVocabularyGroupRel removeByG_V_D(
			long groupId, long vocabularyId, int depotEntryType)
		throws NoSuchVocabularyGroupRelException {

		AssetVocabularyGroupRel assetVocabularyGroupRel = findByG_V_D(
			groupId, vocabularyId, depotEntryType);

		return remove(assetVocabularyGroupRel);
	}

	/**
	 * Returns the number of asset vocabulary group rels where groupId = &#63; and vocabularyId = &#63; and depotEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param depotEntryType the depot entry type
	 * @return the number of matching asset vocabulary group rels
	 */
	@Override
	public int countByG_V_D(
		long groupId, long vocabularyId, int depotEntryType) {

		return _uniquePersistenceFinderByG_V_D.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, vocabularyId, depotEntryType});
	}

	public AssetVocabularyGroupRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(AssetVocabularyGroupRel.class);

		setModelImplClass(AssetVocabularyGroupRelImpl.class);
		setModelPKClass(long.class);

		setTable(AssetVocabularyGroupRelTable.INSTANCE);
	}

	/**
	 * Creates a new asset vocabulary group rel with the primary key. Does not add the asset vocabulary group rel to the database.
	 *
	 * @param assetVocabularyGroupRelId the primary key for the new asset vocabulary group rel
	 * @return the new asset vocabulary group rel
	 */
	@Override
	public AssetVocabularyGroupRel create(long assetVocabularyGroupRelId) {
		AssetVocabularyGroupRel assetVocabularyGroupRel =
			new AssetVocabularyGroupRelImpl();

		assetVocabularyGroupRel.setNew(true);
		assetVocabularyGroupRel.setPrimaryKey(assetVocabularyGroupRelId);

		String uuid = PortalUUIDUtil.generate();

		assetVocabularyGroupRel.setUuid(uuid);

		assetVocabularyGroupRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return assetVocabularyGroupRel;
	}

	/**
	 * Removes the asset vocabulary group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param assetVocabularyGroupRelId the primary key of the asset vocabulary group rel
	 * @return the asset vocabulary group rel that was removed
	 * @throws NoSuchVocabularyGroupRelException if a asset vocabulary group rel with the primary key could not be found
	 */
	@Override
	public AssetVocabularyGroupRel remove(long assetVocabularyGroupRelId)
		throws NoSuchVocabularyGroupRelException {

		return remove((Serializable)assetVocabularyGroupRelId);
	}

	@Override
	protected AssetVocabularyGroupRel removeImpl(
		AssetVocabularyGroupRel assetVocabularyGroupRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(assetVocabularyGroupRel)) {
				assetVocabularyGroupRel = (AssetVocabularyGroupRel)session.get(
					AssetVocabularyGroupRelImpl.class,
					assetVocabularyGroupRel.getPrimaryKeyObj());
			}

			if ((assetVocabularyGroupRel != null) &&
				CTPersistenceHelperUtil.isRemove(assetVocabularyGroupRel)) {

				session.delete(assetVocabularyGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (assetVocabularyGroupRel != null) {
			clearCache(assetVocabularyGroupRel);
		}

		return assetVocabularyGroupRel;
	}

	@Override
	public AssetVocabularyGroupRel updateImpl(
		AssetVocabularyGroupRel assetVocabularyGroupRel) {

		boolean isNew = assetVocabularyGroupRel.isNew();

		if (!(assetVocabularyGroupRel instanceof
				AssetVocabularyGroupRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(assetVocabularyGroupRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					assetVocabularyGroupRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in assetVocabularyGroupRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AssetVocabularyGroupRel implementation " +
					assetVocabularyGroupRel.getClass());
		}

		AssetVocabularyGroupRelModelImpl assetVocabularyGroupRelModelImpl =
			(AssetVocabularyGroupRelModelImpl)assetVocabularyGroupRel;

		if (Validator.isNull(assetVocabularyGroupRel.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			assetVocabularyGroupRel.setUuid(uuid);
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(assetVocabularyGroupRel)) {
				if (!isNew) {
					session.evict(
						AssetVocabularyGroupRelImpl.class,
						assetVocabularyGroupRel.getPrimaryKeyObj());
				}

				session.save(assetVocabularyGroupRel);
			}
			else {
				assetVocabularyGroupRel =
					(AssetVocabularyGroupRel)session.merge(
						assetVocabularyGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(assetVocabularyGroupRel, false);

		if (isNew) {
			assetVocabularyGroupRel.setNew(false);
		}

		assetVocabularyGroupRel.resetOriginalValues();

		return assetVocabularyGroupRel;
	}

	/**
	 * Returns the asset vocabulary group rel with the primary key or throws a <code>NoSuchVocabularyGroupRelException</code> if it could not be found.
	 *
	 * @param assetVocabularyGroupRelId the primary key of the asset vocabulary group rel
	 * @return the asset vocabulary group rel
	 * @throws NoSuchVocabularyGroupRelException if a asset vocabulary group rel with the primary key could not be found
	 */
	@Override
	public AssetVocabularyGroupRel findByPrimaryKey(
			long assetVocabularyGroupRelId)
		throws NoSuchVocabularyGroupRelException {

		return findByPrimaryKey((Serializable)assetVocabularyGroupRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the asset vocabulary group rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param assetVocabularyGroupRelId the primary key of the asset vocabulary group rel
	 * @return the asset vocabulary group rel, or <code>null</code> if a asset vocabulary group rel with the primary key could not be found
	 */
	@Override
	public AssetVocabularyGroupRel fetchByPrimaryKey(
		long assetVocabularyGroupRelId) {

		return fetchByPrimaryKey((Serializable)assetVocabularyGroupRelId);
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
		return "assetVocabularyGroupRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ASSETVOCABULARYGROUPREL;
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
		return AssetVocabularyGroupRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AssetVocabularyGroupRel";
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
		ctMergeColumnNames.add("vocabularyId");
		ctMergeColumnNames.add("depotEntryType");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("assetVocabularyGroupRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});
	}

	/**
	 * Initializes the asset vocabulary group rel persistence.
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
			_SQL_SELECT_ASSETVOCABULARYGROUPREL_WHERE,
			_SQL_COUNT_ASSETVOCABULARYGROUPREL_WHERE,
			AssetVocabularyGroupRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"assetVocabularyGroupRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				AssetVocabularyGroupRel::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(AssetVocabularyGroupRel::getUuid),
				AssetVocabularyGroupRel::getGroupId),
			_SQL_SELECT_ASSETVOCABULARYGROUPREL_WHERE, "",
			new FinderColumn<>(
				"assetVocabularyGroupRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				AssetVocabularyGroupRel::getUuid),
			new FinderColumn<>(
				"assetVocabularyGroupRel.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, AssetVocabularyGroupRel::getGroupId));

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
				_SQL_SELECT_ASSETVOCABULARYGROUPREL_WHERE,
				_SQL_COUNT_ASSETVOCABULARYGROUPREL_WHERE,
				AssetVocabularyGroupRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"assetVocabularyGroupRel.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					AssetVocabularyGroupRel::getUuid),
				new FinderColumn<>(
					"assetVocabularyGroupRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetVocabularyGroupRel::getCompanyId));

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
				_SQL_SELECT_ASSETVOCABULARYGROUPREL_WHERE,
				_SQL_COUNT_ASSETVOCABULARYGROUPREL_WHERE,
				AssetVocabularyGroupRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"assetVocabularyGroupRel.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetVocabularyGroupRel::getGroupId));

		_collectionPersistenceFinderByVocabularyId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByVocabularyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"vocabularyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByVocabularyId", new String[] {Long.class.getName()},
					new String[] {"vocabularyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByVocabularyId", new String[] {Long.class.getName()},
					new String[] {"vocabularyId"}, false),
				_SQL_SELECT_ASSETVOCABULARYGROUPREL_WHERE,
				_SQL_COUNT_ASSETVOCABULARYGROUPREL_WHERE,
				AssetVocabularyGroupRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"assetVocabularyGroupRel.", "vocabularyId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetVocabularyGroupRel::getVocabularyId));

		_uniquePersistenceFinderByG_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_V",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "vocabularyId"}, 0, 0, false,
				AssetVocabularyGroupRel::getGroupId,
				AssetVocabularyGroupRel::getVocabularyId),
			_SQL_SELECT_ASSETVOCABULARYGROUPREL_WHERE, "",
			new FinderColumn<>(
				"assetVocabularyGroupRel.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, AssetVocabularyGroupRel::getGroupId),
			new FinderColumn<>(
				"assetVocabularyGroupRel.", "vocabularyId",
				FinderColumn.Type.LONG, "=", true, true,
				AssetVocabularyGroupRel::getVocabularyId));

		_collectionPersistenceFinderByG_D = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_D",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "depotEntryType"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_D",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"groupId", "depotEntryType"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_D",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"groupId", "depotEntryType"}, false),
			_SQL_SELECT_ASSETVOCABULARYGROUPREL_WHERE,
			_SQL_COUNT_ASSETVOCABULARYGROUPREL_WHERE,
			AssetVocabularyGroupRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"assetVocabularyGroupRel.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, AssetVocabularyGroupRel::getGroupId),
			new FinderColumn<>(
				"assetVocabularyGroupRel.", "depotEntryType",
				FinderColumn.Type.INTEGER, "=", true, true,
				AssetVocabularyGroupRel::getDepotEntryType));

		_collectionPersistenceFinderByV_D = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByV_D",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"vocabularyId", "depotEntryType"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByV_D",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"vocabularyId", "depotEntryType"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByV_D",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"vocabularyId", "depotEntryType"}, false),
			_SQL_SELECT_ASSETVOCABULARYGROUPREL_WHERE,
			_SQL_COUNT_ASSETVOCABULARYGROUPREL_WHERE,
			AssetVocabularyGroupRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"assetVocabularyGroupRel.", "vocabularyId",
				FinderColumn.Type.LONG, "=", true, true,
				AssetVocabularyGroupRel::getVocabularyId),
			new FinderColumn<>(
				"assetVocabularyGroupRel.", "depotEntryType",
				FinderColumn.Type.INTEGER, "=", true, true,
				AssetVocabularyGroupRel::getDepotEntryType));

		_uniquePersistenceFinderByG_V_D = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_V_D",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"groupId", "vocabularyId", "depotEntryType"}, 0,
				0, false, AssetVocabularyGroupRel::getGroupId,
				AssetVocabularyGroupRel::getVocabularyId,
				AssetVocabularyGroupRel::getDepotEntryType),
			_SQL_SELECT_ASSETVOCABULARYGROUPREL_WHERE, "",
			new FinderColumn<>(
				"assetVocabularyGroupRel.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, AssetVocabularyGroupRel::getGroupId),
			new FinderColumn<>(
				"assetVocabularyGroupRel.", "vocabularyId",
				FinderColumn.Type.LONG, "=", true, true,
				AssetVocabularyGroupRel::getVocabularyId),
			new FinderColumn<>(
				"assetVocabularyGroupRel.", "depotEntryType",
				FinderColumn.Type.INTEGER, "=", true, true,
				AssetVocabularyGroupRel::getDepotEntryType));

		AssetVocabularyGroupRelUtil.setPersistence(this);
	}

	public void destroy() {
		AssetVocabularyGroupRelUtil.setPersistence(null);

		EntityCacheUtil.removeCache(
			AssetVocabularyGroupRelImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		AssetVocabularyGroupRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ASSETVOCABULARYGROUPREL =
		"SELECT assetVocabularyGroupRel FROM AssetVocabularyGroupRel assetVocabularyGroupRel";

	private static final String _SQL_SELECT_ASSETVOCABULARYGROUPREL_WHERE =
		"SELECT assetVocabularyGroupRel FROM AssetVocabularyGroupRel assetVocabularyGroupRel WHERE ";

	private static final String _SQL_COUNT_ASSETVOCABULARYGROUPREL_WHERE =
		"SELECT COUNT(assetVocabularyGroupRel) FROM AssetVocabularyGroupRel assetVocabularyGroupRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No AssetVocabularyGroupRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		AssetVocabularyGroupRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1135276955