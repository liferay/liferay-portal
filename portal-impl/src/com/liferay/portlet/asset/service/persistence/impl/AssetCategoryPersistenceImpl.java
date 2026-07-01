/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.persistence.impl;

import com.liferay.asset.kernel.exception.DuplicateAssetCategoryExternalReferenceCodeException;
import com.liferay.asset.kernel.exception.NoSuchCategoryException;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryTable;
import com.liferay.asset.kernel.service.persistence.AssetCategoryPersistence;
import com.liferay.asset.kernel.service.persistence.AssetCategoryUtil;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
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
import com.liferay.portlet.asset.model.impl.AssetCategoryImpl;
import com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl;

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

/**
 * The persistence implementation for the asset category service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AssetCategoryPersistenceImpl
	extends BasePersistenceImpl<AssetCategory, NoSuchCategoryException>
	implements AssetCategoryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AssetCategoryUtil</code> to access the asset category persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AssetCategoryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<AssetCategory, NoSuchCategoryException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the asset categories where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByUuid_First(
			String uuid, OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByUuid_First(
		String uuid, OrderByComparator<AssetCategory> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the asset categories where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of asset categories where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private UniquePersistenceFinder<AssetCategory, NoSuchCategoryException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the asset category where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByUUID_G(String uuid, long groupId)
		throws NoSuchCategoryException {

		return _uniquePersistenceFinderByUUID_G.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	/**
	 * Returns the asset category where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId},
			useFinderCache);
	}

	/**
	 * Removes the asset category where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset category that was removed
	 */
	@Override
	public AssetCategory removeByUUID_G(String uuid, long groupId)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = findByUUID_G(uuid, groupId);

		return remove(assetCategory);
	}

	/**
	 * Returns the number of asset categories where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<AssetCategory, NoSuchCategoryException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the asset categories where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the asset categories where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of asset categories where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<AssetCategory, NoSuchCategoryException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByGroupId_First(
			long groupId, OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByGroupId_First(
		long groupId, OrderByComparator<AssetCategory> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the asset categories where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of asset categories where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of asset categories that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset categories that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, groupId);
	}

	private CollectionPersistenceFinder<AssetCategory, NoSuchCategoryException>
		_collectionPersistenceFinderByParentCategoryId;

	/**
	 * Returns an ordered range of all the asset categories where parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByParentCategoryId(
		long parentCategoryId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByParentCategoryId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {parentCategoryId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where parentCategoryId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByParentCategoryId_First(
			long parentCategoryId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		return _collectionPersistenceFinderByParentCategoryId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {parentCategoryId},
			orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where parentCategoryId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByParentCategoryId_First(
		long parentCategoryId,
		OrderByComparator<AssetCategory> orderByComparator) {

		return _collectionPersistenceFinderByParentCategoryId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {parentCategoryId},
			orderByComparator);
	}

	/**
	 * Removes all the asset categories where parentCategoryId = &#63; from the database.
	 *
	 * @param parentCategoryId the parent category ID
	 */
	@Override
	public void removeByParentCategoryId(long parentCategoryId) {
		_collectionPersistenceFinderByParentCategoryId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {parentCategoryId});
	}

	/**
	 * Returns the number of asset categories where parentCategoryId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByParentCategoryId(long parentCategoryId) {
		return _collectionPersistenceFinderByParentCategoryId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {parentCategoryId});
	}

	private CollectionPersistenceFinder<AssetCategory, NoSuchCategoryException>
		_collectionPersistenceFinderByVocabularyId;

	/**
	 * Returns an ordered range of all the asset categories where vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByVocabularyId(
		long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByVocabularyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {vocabularyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where vocabularyId = &#63;.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByVocabularyId_First(
			long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		return _collectionPersistenceFinderByVocabularyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {vocabularyId},
			orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where vocabularyId = &#63;.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByVocabularyId_First(
		long vocabularyId, OrderByComparator<AssetCategory> orderByComparator) {

		return _collectionPersistenceFinderByVocabularyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {vocabularyId},
			orderByComparator);
	}

	/**
	 * Removes all the asset categories where vocabularyId = &#63; from the database.
	 *
	 * @param vocabularyId the vocabulary ID
	 */
	@Override
	public void removeByVocabularyId(long vocabularyId) {
		_collectionPersistenceFinderByVocabularyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {vocabularyId});
	}

	/**
	 * Returns the number of asset categories where vocabularyId = &#63;.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByVocabularyId(long vocabularyId) {
		return _collectionPersistenceFinderByVocabularyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {vocabularyId});
	}

	private FilterCollectionPersistenceFinder
		<AssetCategory, NoSuchCategoryException>
			_collectionPersistenceFinderByG_P;

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_P(
		long groupId, long parentCategoryId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentCategoryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByG_P_First(
			long groupId, long parentCategoryId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		return _collectionPersistenceFinderByG_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentCategoryId}, orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByG_P_First(
		long groupId, long parentCategoryId,
		OrderByComparator<AssetCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentCategoryId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permissions to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_P(
		long groupId, long parentCategoryId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_P.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentCategoryId}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the asset categories where groupId = &#63; and parentCategoryId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 */
	@Override
	public void removeByG_P(long groupId, long parentCategoryId) {
		_collectionPersistenceFinderByG_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentCategoryId});
	}

	/**
	 * Returns the number of asset categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByG_P(long groupId, long parentCategoryId) {
		return _collectionPersistenceFinderByG_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentCategoryId});
	}

	/**
	 * Returns the number of asset categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching asset categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(long groupId, long parentCategoryId) {
		return _collectionPersistenceFinderByG_P.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentCategoryId}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<AssetCategory, NoSuchCategoryException>
			_collectionPersistenceFinderByG_V;

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_V(
		long groupId, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_V.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}, new long[] {vocabularyId}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByG_V_First(
			long groupId, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		return _collectionPersistenceFinderByG_V.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}, new long[] {vocabularyId}},
			orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByG_V_First(
		long groupId, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_V.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}, new long[] {vocabularyId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permissions to view where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_V(
		long groupId, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_V.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}, new long[] {vocabularyId}},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permission to view where groupId = any &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_V(
		long[] groupIds, long[] vocabularyIds, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_V.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupIds, ArrayUtil.sortedUnique(vocabularyIds)},
			start, end, orderByComparator, groupIds);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and vocabularyId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_V(
		long[] groupIds, long[] vocabularyIds, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_V.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				ArrayUtil.sortedUnique(groupIds),
				ArrayUtil.sortedUnique(vocabularyIds)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset categories where groupId = &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 */
	@Override
	public void removeByG_V(long groupId, long vocabularyId) {
		_collectionPersistenceFinderByG_V.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}, new long[] {vocabularyId}});
	}

	/**
	 * Returns the number of asset categories where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByG_V(long groupId, long vocabularyId) {
		return _collectionPersistenceFinderByG_V.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}, new long[] {vocabularyId}});
	}

	/**
	 * Returns the number of asset categories where groupId = any &#63; and vocabularyId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByG_V(long[] groupIds, long[] vocabularyIds) {
		return _collectionPersistenceFinderByG_V.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				ArrayUtil.sortedUnique(groupIds),
				ArrayUtil.sortedUnique(vocabularyIds)
			});
	}

	/**
	 * Returns the number of asset categories that the user has permission to view where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_V(long groupId, long vocabularyId) {
		return _collectionPersistenceFinderByG_V.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}, new long[] {vocabularyId}},
			groupId);
	}

	/**
	 * Returns the number of asset categories that the user has permission to view where groupId = any &#63; and vocabularyId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @return the number of matching asset categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_V(long[] groupIds, long[] vocabularyIds) {
		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_V.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupIds, ArrayUtil.sortedUnique(vocabularyIds)},
			groupIds);
	}

	private CollectionPersistenceFinder<AssetCategory, NoSuchCategoryException>
		_collectionPersistenceFinderByP_N;

	/**
	 * Returns an ordered range of all the asset categories where parentCategoryId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByP_N(
		long parentCategoryId, String name, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_N.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {parentCategoryId, name}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where parentCategoryId = &#63; and name = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByP_N_First(
			long parentCategoryId, String name,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		return _collectionPersistenceFinderByP_N.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {parentCategoryId, name}, orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where parentCategoryId = &#63; and name = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByP_N_First(
		long parentCategoryId, String name,
		OrderByComparator<AssetCategory> orderByComparator) {

		return _collectionPersistenceFinderByP_N.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {parentCategoryId, name}, orderByComparator);
	}

	/**
	 * Removes all the asset categories where parentCategoryId = &#63; and name = &#63; from the database.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 */
	@Override
	public void removeByP_N(long parentCategoryId, String name) {
		_collectionPersistenceFinderByP_N.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {parentCategoryId, name});
	}

	/**
	 * Returns the number of asset categories where parentCategoryId = &#63; and name = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByP_N(long parentCategoryId, String name) {
		return _collectionPersistenceFinderByP_N.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {parentCategoryId, name});
	}

	private CollectionPersistenceFinder<AssetCategory, NoSuchCategoryException>
		_collectionPersistenceFinderByP_V;

	/**
	 * Returns an ordered range of all the asset categories where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByP_V(
		long parentCategoryId, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_V.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {parentCategoryId, vocabularyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByP_V_First(
			long parentCategoryId, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		return _collectionPersistenceFinderByP_V.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {parentCategoryId, vocabularyId}, orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByP_V_First(
		long parentCategoryId, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		return _collectionPersistenceFinderByP_V.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {parentCategoryId, vocabularyId}, orderByComparator);
	}

	/**
	 * Removes all the asset categories where parentCategoryId = &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 */
	@Override
	public void removeByP_V(long parentCategoryId, long vocabularyId) {
		_collectionPersistenceFinderByP_V.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {parentCategoryId, vocabularyId});
	}

	/**
	 * Returns the number of asset categories where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByP_V(long parentCategoryId, long vocabularyId) {
		return _collectionPersistenceFinderByP_V.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {parentCategoryId, vocabularyId});
	}

	private CollectionPersistenceFinder<AssetCategory, NoSuchCategoryException>
		_collectionPersistenceFinderByN_V;

	/**
	 * Returns an ordered range of all the asset categories where name = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByN_V(
		String name, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByN_V.find(
			FinderCacheUtil.getFinderCache(), new Object[] {name, vocabularyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where name = &#63; and vocabularyId = &#63;.
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByN_V_First(
			String name, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		return _collectionPersistenceFinderByN_V.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {name, vocabularyId},
			orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where name = &#63; and vocabularyId = &#63;.
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByN_V_First(
		String name, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		return _collectionPersistenceFinderByN_V.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {name, vocabularyId},
			orderByComparator);
	}

	/**
	 * Removes all the asset categories where name = &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 */
	@Override
	public void removeByN_V(String name, long vocabularyId) {
		_collectionPersistenceFinderByN_V.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {name, vocabularyId});
	}

	/**
	 * Returns the number of asset categories where name = &#63; and vocabularyId = &#63;.
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByN_V(String name, long vocabularyId) {
		return _collectionPersistenceFinderByN_V.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {name, vocabularyId});
	}

	private FilterCollectionPersistenceFinder
		<AssetCategory, NoSuchCategoryException>
			_collectionPersistenceFinderByG_P_V;

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId, int start,
		int end, OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_V.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentCategoryId, vocabularyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByG_P_V_First(
			long groupId, long parentCategoryId, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		return _collectionPersistenceFinderByG_P_V.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentCategoryId, vocabularyId},
			orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByG_P_V_First(
		long groupId, long parentCategoryId, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_P_V.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentCategoryId, vocabularyId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permissions to view where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId, int start,
		int end, OrderByComparator<AssetCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_P_V.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentCategoryId, vocabularyId}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the asset categories where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 */
	@Override
	public void removeByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId) {

		_collectionPersistenceFinderByG_P_V.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentCategoryId, vocabularyId});
	}

	/**
	 * Returns the number of asset categories where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId) {

		return _collectionPersistenceFinderByG_P_V.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentCategoryId, vocabularyId});
	}

	/**
	 * Returns the number of asset categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId) {

		return _collectionPersistenceFinderByG_P_V.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, parentCategoryId, vocabularyId}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<AssetCategory, NoSuchCategoryException>
			_collectionPersistenceFinderByG_LikeT_V;

	/**
	 * Returns all the asset categories where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeT_V(
		long groupId, String treePath, long vocabularyId) {

		return findByG_LikeT_V(
			groupId, treePath, vocabularyId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset categories where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeT_V(
		long groupId, String treePath, long vocabularyId, int start, int end) {

		return findByG_LikeT_V(
			groupId, treePath, vocabularyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeT_V(
		long groupId, String treePath, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return findByG_LikeT_V(
			groupId, treePath, vocabularyId, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeT_V(
		long groupId, String treePath, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeT_V.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, treePath, vocabularyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByG_LikeT_V_First(
			long groupId, String treePath, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		return _collectionPersistenceFinderByG_LikeT_V.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, treePath, vocabularyId}, orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByG_LikeT_V_First(
		long groupId, String treePath, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeT_V.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, treePath, vocabularyId}, orderByComparator);
	}

	/**
	 * Returns all the asset categories that the user has permission to view where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_LikeT_V(
		long groupId, String treePath, long vocabularyId) {

		return filterFindByG_LikeT_V(
			groupId, treePath, vocabularyId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset categories that the user has permission to view where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_LikeT_V(
		long groupId, String treePath, long vocabularyId, int start, int end) {

		return filterFindByG_LikeT_V(
			groupId, treePath, vocabularyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permissions to view where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_LikeT_V(
		long groupId, String treePath, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeT_V.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, treePath, vocabularyId}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the asset categories where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 */
	@Override
	public void removeByG_LikeT_V(
		long groupId, String treePath, long vocabularyId) {

		_collectionPersistenceFinderByG_LikeT_V.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, treePath, vocabularyId});
	}

	/**
	 * Returns the number of asset categories where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByG_LikeT_V(
		long groupId, String treePath, long vocabularyId) {

		return _collectionPersistenceFinderByG_LikeT_V.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, treePath, vocabularyId});
	}

	/**
	 * Returns the number of asset categories that the user has permission to view where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeT_V(
		long groupId, String treePath, long vocabularyId) {

		return _collectionPersistenceFinderByG_LikeT_V.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, treePath, vocabularyId}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<AssetCategory, NoSuchCategoryException>
			_collectionPersistenceFinderByG_LikeN_V;

	/**
	 * Returns all the asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeN_V(
		long groupId, String name, long vocabularyId) {

		return findByG_LikeN_V(
			groupId, name, vocabularyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeN_V(
		long groupId, String name, long vocabularyId, int start, int end) {

		return findByG_LikeN_V(groupId, name, vocabularyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeN_V(
		long groupId, String name, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return findByG_LikeN_V(
			groupId, name, vocabularyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeN_V(
		long groupId, String name, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN_V.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				new long[] {groupId}, name, new long[] {vocabularyId}
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByG_LikeN_V_First(
			long groupId, String name, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		return _collectionPersistenceFinderByG_LikeN_V.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				new long[] {groupId}, name, new long[] {vocabularyId}
			},
			orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByG_LikeN_V_First(
		long groupId, String name, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN_V.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				new long[] {groupId}, name, new long[] {vocabularyId}
			},
			orderByComparator);
	}

	/**
	 * Returns all the asset categories that the user has permission to view where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_LikeN_V(
		long groupId, String name, long vocabularyId) {

		return filterFindByG_LikeN_V(
			groupId, name, vocabularyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the asset categories that the user has permission to view where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_LikeN_V(
		long groupId, String name, long vocabularyId, int start, int end) {

		return filterFindByG_LikeN_V(
			groupId, name, vocabularyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permissions to view where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_LikeN_V(
		long groupId, String name, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN_V.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				new long[] {groupId}, name, new long[] {vocabularyId}
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the asset categories that the user has permission to view where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @return the matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds) {

		return filterFindByG_LikeN_V(
			groupIds, name, vocabularyIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the asset categories that the user has permission to view where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds, int start,
		int end) {

		return filterFindByG_LikeN_V(
			groupIds, name, vocabularyIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permission to view where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_LikeN_V.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupIds, name, ArrayUtil.sortedUnique(vocabularyIds)
			},
			start, end, orderByComparator, groupIds);
	}

	/**
	 * Returns all the asset categories where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @return the matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds) {

		return findByG_LikeN_V(
			groupIds, name, vocabularyIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the asset categories where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds, int start,
		int end) {

		return findByG_LikeN_V(groupIds, name, vocabularyIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return findByG_LikeN_V(
			groupIds, name, vocabularyIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN_V.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), name,
				ArrayUtil.sortedUnique(vocabularyIds)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 */
	@Override
	public void removeByG_LikeN_V(
		long groupId, String name, long vocabularyId) {

		_collectionPersistenceFinderByG_LikeN_V.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				new long[] {groupId}, name, new long[] {vocabularyId}
			});
	}

	/**
	 * Returns the number of asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByG_LikeN_V(long groupId, String name, long vocabularyId) {
		return _collectionPersistenceFinderByG_LikeN_V.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				new long[] {groupId}, name, new long[] {vocabularyId}
			});
	}

	/**
	 * Returns the number of asset categories where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds) {

		return _collectionPersistenceFinderByG_LikeN_V.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), name,
				ArrayUtil.sortedUnique(vocabularyIds)
			});
	}

	/**
	 * Returns the number of asset categories that the user has permission to view where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeN_V(
		long groupId, String name, long vocabularyId) {

		return _collectionPersistenceFinderByG_LikeN_V.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				new long[] {groupId}, name, new long[] {vocabularyId}
			},
			groupId);
	}

	/**
	 * Returns the number of asset categories that the user has permission to view where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @return the number of matching asset categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_LikeN_V.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupIds, name, ArrayUtil.sortedUnique(vocabularyIds)
			},
			groupIds);
	}

	private UniquePersistenceFinder<AssetCategory, NoSuchCategoryException>
		_uniquePersistenceFinderByP_N_V;

	/**
	 * Returns the asset category where parentCategoryId = &#63; and name = &#63; and vocabularyId = &#63; or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByP_N_V(
			long parentCategoryId, String name, long vocabularyId)
		throws NoSuchCategoryException {

		return _uniquePersistenceFinderByP_N_V.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {parentCategoryId, name, vocabularyId});
	}

	/**
	 * Returns the asset category where parentCategoryId = &#63; and name = &#63; and vocabularyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByP_N_V(
		long parentCategoryId, String name, long vocabularyId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByP_N_V.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {parentCategoryId, name, vocabularyId},
			useFinderCache);
	}

	/**
	 * Removes the asset category where parentCategoryId = &#63; and name = &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the asset category that was removed
	 */
	@Override
	public AssetCategory removeByP_N_V(
			long parentCategoryId, String name, long vocabularyId)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = findByP_N_V(
			parentCategoryId, name, vocabularyId);

		return remove(assetCategory);
	}

	/**
	 * Returns the number of asset categories where parentCategoryId = &#63; and name = &#63; and vocabularyId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByP_N_V(
		long parentCategoryId, String name, long vocabularyId) {

		return _uniquePersistenceFinderByP_N_V.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {parentCategoryId, name, vocabularyId});
	}

	private UniquePersistenceFinder<AssetCategory, NoSuchCategoryException>
		_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the asset category where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchCategoryException {

		return _uniquePersistenceFinderByERC_G.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the asset category where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId}, useFinderCache);
	}

	/**
	 * Removes the asset category where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the asset category that was removed
	 */
	@Override
	public AssetCategory removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = findByERC_G(
			externalReferenceCode, groupId);

		return remove(assetCategory);
	}

	/**
	 * Returns the number of asset categories where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	public AssetCategoryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("system", "system_");

		setDBColumnNames(dbColumnNames);

		setModelClass(AssetCategory.class);

		setModelImplClass(AssetCategoryImpl.class);
		setModelPKClass(long.class);

		setTable(AssetCategoryTable.INSTANCE);
	}

	/**
	 * Creates a new asset category with the primary key. Does not add the asset category to the database.
	 *
	 * @param categoryId the primary key for the new asset category
	 * @return the new asset category
	 */
	@Override
	public AssetCategory create(long categoryId) {
		AssetCategory assetCategory = new AssetCategoryImpl();

		assetCategory.setNew(true);
		assetCategory.setPrimaryKey(categoryId);

		String uuid = PortalUUIDUtil.generate();

		assetCategory.setUuid(uuid);

		assetCategory.setCompanyId(CompanyThreadLocal.getCompanyId());

		return assetCategory;
	}

	/**
	 * Removes the asset category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param categoryId the primary key of the asset category
	 * @return the asset category that was removed
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory remove(long categoryId)
		throws NoSuchCategoryException {

		return remove((Serializable)categoryId);
	}

	@Override
	protected AssetCategory removeImpl(AssetCategory assetCategory) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(assetCategory)) {
				assetCategory = (AssetCategory)session.get(
					AssetCategoryImpl.class, assetCategory.getPrimaryKeyObj());
			}

			if ((assetCategory != null) &&
				CTPersistenceHelperUtil.isRemove(assetCategory)) {

				session.delete(assetCategory);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (assetCategory != null) {
			clearCache(assetCategory);
		}

		return assetCategory;
	}

	@Override
	public AssetCategory updateImpl(AssetCategory assetCategory) {
		boolean isNew = assetCategory.isNew();

		if (!(assetCategory instanceof AssetCategoryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(assetCategory.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					assetCategory);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in assetCategory proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AssetCategory implementation " +
					assetCategory.getClass());
		}

		AssetCategoryModelImpl assetCategoryModelImpl =
			(AssetCategoryModelImpl)assetCategory;

		if (Validator.isNull(assetCategory.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			assetCategory.setUuid(uuid);
		}

		if (Validator.isNull(assetCategory.getExternalReferenceCode())) {
			assetCategory.setExternalReferenceCode(assetCategory.getUuid());
		}
		else {
			if (!Objects.equals(
					assetCategoryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					assetCategory.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = assetCategory.getCompanyId();

					long groupId = assetCategory.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = assetCategory.getPrimaryKey();
					}

					try {
						assetCategory.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								AssetCategory.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								assetCategory.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			AssetCategory ercAssetCategory = fetchByERC_G(
				assetCategory.getExternalReferenceCode(),
				assetCategory.getGroupId());

			if (isNew) {
				if (ercAssetCategory != null) {
					throw new DuplicateAssetCategoryExternalReferenceCodeException(
						"Duplicate asset category with external reference code " +
							assetCategory.getExternalReferenceCode() +
								" and group " + assetCategory.getGroupId());
				}
			}
			else {
				if ((ercAssetCategory != null) &&
					(assetCategory.getCategoryId() !=
						ercAssetCategory.getCategoryId())) {

					throw new DuplicateAssetCategoryExternalReferenceCodeException(
						"Duplicate asset category with external reference code " +
							assetCategory.getExternalReferenceCode() +
								" and group " + assetCategory.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (assetCategory.getCreateDate() == null)) {
			if (serviceContext == null) {
				assetCategory.setCreateDate(date);
			}
			else {
				assetCategory.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!assetCategoryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				assetCategory.setModifiedDate(date);
			}
			else {
				assetCategory.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(assetCategory)) {
				if (!isNew) {
					session.evict(
						AssetCategoryImpl.class,
						assetCategory.getPrimaryKeyObj());
				}

				session.save(assetCategory);
			}
			else {
				assetCategory = (AssetCategory)session.merge(assetCategory);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(assetCategory, false);

		if (isNew) {
			assetCategory.setNew(false);
		}

		assetCategory.resetOriginalValues();

		return assetCategory;
	}

	/**
	 * Returns the asset category with the primary key or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param categoryId the primary key of the asset category
	 * @return the asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory findByPrimaryKey(long categoryId)
		throws NoSuchCategoryException {

		return findByPrimaryKey((Serializable)categoryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the asset category with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param categoryId the primary key of the asset category
	 * @return the asset category, or <code>null</code> if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory fetchByPrimaryKey(long categoryId) {
		return fetchByPrimaryKey((Serializable)categoryId);
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
		return "categoryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ASSETCATEGORY;
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
		return AssetCategoryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AssetCategory";
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
		ctMergeColumnNames.add("parentCategoryId");
		ctMergeColumnNames.add("treePath");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("vocabularyId");
		ctMergeColumnNames.add("system_");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("categoryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"parentCategoryId", "name", "vocabularyId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the asset category persistence.
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
			_SQL_SELECT_ASSETCATEGORY_WHERE, _SQL_COUNT_ASSETCATEGORY_WHERE,
			AssetCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"assetCategory.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, AssetCategory::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(AssetCategory::getUuid),
				AssetCategory::getGroupId),
			_SQL_SELECT_ASSETCATEGORY_WHERE, "",
			new FinderColumn<>(
				"assetCategory.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, AssetCategory::getUuid),
			new FinderColumn<>(
				"assetCategory.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, AssetCategory::getGroupId));

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
				_SQL_SELECT_ASSETCATEGORY_WHERE, _SQL_COUNT_ASSETCATEGORY_WHERE,
				AssetCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"assetCategory.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, AssetCategory::getUuid),
				new FinderColumn<>(
					"assetCategory.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, AssetCategory::getCompanyId));

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
				_SQL_SELECT_ASSETCATEGORY_WHERE, _SQL_COUNT_ASSETCATEGORY_WHERE,
				AssetCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"assetCategory.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, AssetCategory::getGroupId));

		_collectionPersistenceFinderByParentCategoryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByParentCategoryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"parentCategoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByParentCategoryId",
					new String[] {Long.class.getName()},
					new String[] {"parentCategoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByParentCategoryId",
					new String[] {Long.class.getName()},
					new String[] {"parentCategoryId"}, false),
				_SQL_SELECT_ASSETCATEGORY_WHERE, _SQL_COUNT_ASSETCATEGORY_WHERE,
				AssetCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"assetCategory.", "parentCategoryId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetCategory::getParentCategoryId));

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
				_SQL_SELECT_ASSETCATEGORY_WHERE, _SQL_COUNT_ASSETCATEGORY_WHERE,
				AssetCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"assetCategory.", "vocabularyId", FinderColumn.Type.LONG,
					"=", true, true, AssetCategory::getVocabularyId));

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
					new String[] {"groupId", "parentCategoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "parentCategoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "parentCategoryId"}, false),
				_SQL_SELECT_ASSETCATEGORY_WHERE, _SQL_COUNT_ASSETCATEGORY_WHERE,
				AssetCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"assetCategory.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, AssetCategory::getGroupId),
				new FinderColumn<>(
					"assetCategory.", "parentCategoryId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetCategory::getParentCategoryId));

		_collectionPersistenceFinderByG_V =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_V",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "vocabularyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_V",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "vocabularyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_V",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "vocabularyId"}, false),
				_SQL_SELECT_ASSETCATEGORY_WHERE, _SQL_COUNT_ASSETCATEGORY_WHERE,
				AssetCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new ArrayableFinderColumn<>(
					"assetCategory.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, AssetCategory::getGroupId),
				new ArrayableFinderColumn<>(
					"assetCategory.", "vocabularyId", FinderColumn.Type.LONG,
					"=", false, true, true, AssetCategory::getVocabularyId));

		_collectionPersistenceFinderByP_N = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_N",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"parentCategoryId", "name"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"parentCategoryId", "name"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"parentCategoryId", "name"}, 0, 2, false, null),
			_SQL_SELECT_ASSETCATEGORY_WHERE, _SQL_COUNT_ASSETCATEGORY_WHERE,
			AssetCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"assetCategory.", "parentCategoryId", FinderColumn.Type.LONG,
				"=", true, true, AssetCategory::getParentCategoryId),
			new FinderColumn<>(
				"assetCategory.", "name", FinderColumn.Type.STRING, "=", true,
				true, AssetCategory::getName));

		_collectionPersistenceFinderByP_V = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_V",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"parentCategoryId", "vocabularyId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_V",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"parentCategoryId", "vocabularyId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_V",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"parentCategoryId", "vocabularyId"}, false),
			_SQL_SELECT_ASSETCATEGORY_WHERE, _SQL_COUNT_ASSETCATEGORY_WHERE,
			AssetCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"assetCategory.", "parentCategoryId", FinderColumn.Type.LONG,
				"=", true, true, AssetCategory::getParentCategoryId),
			new FinderColumn<>(
				"assetCategory.", "vocabularyId", FinderColumn.Type.LONG, "=",
				true, true, AssetCategory::getVocabularyId));

		_collectionPersistenceFinderByN_V = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_V",
				new String[] {
					String.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"name", "vocabularyId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_V",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"name", "vocabularyId"}, 0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_V",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"name", "vocabularyId"}, 0, 1, false, null),
			_SQL_SELECT_ASSETCATEGORY_WHERE, _SQL_COUNT_ASSETCATEGORY_WHERE,
			AssetCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"assetCategory.", "name", FinderColumn.Type.STRING, "=", true,
				true, AssetCategory::getName),
			new FinderColumn<>(
				"assetCategory.", "vocabularyId", FinderColumn.Type.LONG, "=",
				true, true, AssetCategory::getVocabularyId));

		_collectionPersistenceFinderByG_P_V =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_V",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "parentCategoryId", "vocabularyId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_V",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"groupId", "parentCategoryId", "vocabularyId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_V",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"groupId", "parentCategoryId", "vocabularyId"
					},
					false),
				_SQL_SELECT_ASSETCATEGORY_WHERE, _SQL_COUNT_ASSETCATEGORY_WHERE,
				AssetCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"assetCategory.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, AssetCategory::getGroupId),
				new FinderColumn<>(
					"assetCategory.", "parentCategoryId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetCategory::getParentCategoryId),
				new FinderColumn<>(
					"assetCategory.", "vocabularyId", FinderColumn.Type.LONG,
					"=", true, true, AssetCategory::getVocabularyId));

		_collectionPersistenceFinderByG_LikeT_V =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeT_V",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "treePath", "vocabularyId"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LikeT_V",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "treePath", "vocabularyId"},
					false),
				_SQL_SELECT_ASSETCATEGORY_WHERE, _SQL_COUNT_ASSETCATEGORY_WHERE,
				AssetCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"assetCategory.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, AssetCategory::getGroupId),
				new FinderColumn<>(
					"assetCategory.", "treePath", FinderColumn.Type.STRING,
					"LIKE", true, true, AssetCategory::getTreePath),
				new FinderColumn<>(
					"assetCategory.", "vocabularyId", FinderColumn.Type.LONG,
					"=", true, true, AssetCategory::getVocabularyId));

		_collectionPersistenceFinderByG_LikeN_V =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeN_V",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name", "vocabularyId"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LikeN_V",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "name", "vocabularyId"}, false),
				_SQL_SELECT_ASSETCATEGORY_WHERE, _SQL_COUNT_ASSETCATEGORY_WHERE,
				AssetCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new ArrayableFinderColumn<>(
					"assetCategory.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, AssetCategory::getGroupId),
				new FinderColumn<>(
					"assetCategory.", "name", FinderColumn.Type.STRING, "LIKE",
					false, true, AssetCategory::getName),
				new ArrayableFinderColumn<>(
					"assetCategory.", "vocabularyId", FinderColumn.Type.LONG,
					"=", false, true, true, AssetCategory::getVocabularyId));

		_uniquePersistenceFinderByP_N_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByP_N_V",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Long.class.getName()
				},
				new String[] {"parentCategoryId", "name", "vocabularyId"}, 0, 2,
				false, AssetCategory::getParentCategoryId,
				convertNullFunction(AssetCategory::getName),
				AssetCategory::getVocabularyId),
			_SQL_SELECT_ASSETCATEGORY_WHERE, "",
			new FinderColumn<>(
				"assetCategory.", "parentCategoryId", FinderColumn.Type.LONG,
				"=", true, true, AssetCategory::getParentCategoryId),
			new FinderColumn<>(
				"assetCategory.", "name", FinderColumn.Type.STRING, "=", true,
				true, AssetCategory::getName),
			new FinderColumn<>(
				"assetCategory.", "vocabularyId", FinderColumn.Type.LONG, "=",
				true, true, AssetCategory::getVocabularyId));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(AssetCategory::getExternalReferenceCode),
				AssetCategory::getGroupId),
			_SQL_SELECT_ASSETCATEGORY_WHERE, "",
			new FinderColumn<>(
				"assetCategory.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				AssetCategory::getExternalReferenceCode),
			new FinderColumn<>(
				"assetCategory.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, AssetCategory::getGroupId));

		AssetCategoryUtil.setPersistence(this);
	}

	public void destroy() {
		AssetCategoryUtil.setPersistence(null);

		EntityCacheUtil.removeCache(AssetCategoryImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		AssetCategoryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ASSETCATEGORY =
		"SELECT assetCategory FROM AssetCategory assetCategory";

	private static final String _SQL_SELECT_ASSETCATEGORY_WHERE =
		"SELECT assetCategory FROM AssetCategory assetCategory WHERE ";

	private static final String _SQL_COUNT_ASSETCATEGORY_WHERE =
		"SELECT COUNT(assetCategory) FROM AssetCategory assetCategory WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "system"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-992189092