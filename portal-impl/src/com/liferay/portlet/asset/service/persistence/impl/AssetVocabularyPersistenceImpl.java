/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.persistence.impl;

import com.liferay.asset.kernel.exception.DuplicateAssetVocabularyExternalReferenceCodeException;
import com.liferay.asset.kernel.exception.NoSuchVocabularyException;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyTable;
import com.liferay.asset.kernel.service.persistence.AssetVocabularyPersistence;
import com.liferay.asset.kernel.service.persistence.AssetVocabularyUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
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
import com.liferay.portlet.asset.model.impl.AssetVocabularyImpl;
import com.liferay.portlet.asset.model.impl.AssetVocabularyModelImpl;

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
 * The persistence implementation for the asset vocabulary service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AssetVocabularyPersistenceImpl
	extends BasePersistenceImpl<AssetVocabulary, NoSuchVocabularyException>
	implements AssetVocabularyPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AssetVocabularyUtil</code> to access the asset vocabulary persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AssetVocabularyImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AssetVocabulary, NoSuchVocabularyException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the asset vocabularies where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset vocabulary in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByUuid_First(
			String uuid, OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first asset vocabulary in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByUuid_First(
		String uuid, OrderByComparator<AssetVocabulary> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the asset vocabularies where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of asset vocabularies where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private UniquePersistenceFinder<AssetVocabulary, NoSuchVocabularyException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the asset vocabulary where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchVocabularyException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByUUID_G(String uuid, long groupId)
		throws NoSuchVocabularyException {

		return _uniquePersistenceFinderByUUID_G.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	/**
	 * Returns the asset vocabulary where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId},
			useFinderCache);
	}

	/**
	 * Removes the asset vocabulary where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset vocabulary that was removed
	 */
	@Override
	public AssetVocabulary removeByUUID_G(String uuid, long groupId)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = findByUUID_G(uuid, groupId);

		return remove(assetVocabulary);
	}

	/**
	 * Returns the number of asset vocabularies where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<AssetVocabulary, NoSuchVocabularyException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the asset vocabularies where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset vocabulary in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first asset vocabulary in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the asset vocabularies where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of asset vocabularies where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<AssetVocabulary, NoSuchVocabularyException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the asset vocabularies where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset vocabulary in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByGroupId_First(
			long groupId, OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = fetchByGroupId_First(
			groupId, orderByComparator);

		if (assetVocabulary != null) {
			return assetVocabulary;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchVocabularyException(sb.toString());
	}

	/**
	 * Returns the first asset vocabulary in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByGroupId_First(
		long groupId, OrderByComparator<AssetVocabulary> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByGroupId.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {groupIds}, start,
			end, orderByComparator, groupIds);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ArrayUtil.sortedUnique(groupIds)}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset vocabularies where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of asset vocabularies where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of asset vocabularies where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByGroupId(long[] groupIds) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ArrayUtil.sortedUnique(groupIds)});
	}

	/**
	 * Returns the number of asset vocabularies that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}}, groupId);
	}

	/**
	 * Returns the number of asset vocabularies that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long[] groupIds) {
		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByGroupId.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {groupIds},
			groupIds);
	}

	private CollectionPersistenceFinder
		<AssetVocabulary, NoSuchVocabularyException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the asset vocabularies where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset vocabulary in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByCompanyId_First(
			long companyId,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first asset vocabulary in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByCompanyId_First(
		long companyId, OrderByComparator<AssetVocabulary> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the asset vocabularies where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of asset vocabularies where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private UniquePersistenceFinder<AssetVocabulary, NoSuchVocabularyException>
		_uniquePersistenceFinderByG_N;

	/**
	 * Returns the asset vocabulary where groupId = &#63; and name = &#63; or throws a <code>NoSuchVocabularyException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByG_N(long groupId, String name)
		throws NoSuchVocabularyException {

		return _uniquePersistenceFinderByG_N.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name});
	}

	/**
	 * Returns the asset vocabulary where groupId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByG_N(
		long groupId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_N.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name},
			useFinderCache);
	}

	/**
	 * Removes the asset vocabulary where groupId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the asset vocabulary that was removed
	 */
	@Override
	public AssetVocabulary removeByG_N(long groupId, String name)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = findByG_N(groupId, name);

		return remove(assetVocabulary);
	}

	/**
	 * Returns the number of asset vocabularies where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByG_N(long groupId, String name) {
		return _uniquePersistenceFinderByG_N.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name});
	}

	private FilterCollectionPersistenceFinder
		<AssetVocabulary, NoSuchVocabularyException>
			_collectionPersistenceFinderByG_LikeN;

	/**
	 * Returns all the asset vocabularies where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByG_LikeN(long groupId, String name) {
		return findByG_LikeN(
			groupId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset vocabularies where groupId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @return the range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByG_LikeN(
		long groupId, String name, int start, int end) {

		return findByG_LikeN(groupId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where groupId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByG_LikeN(
		long groupId, String name, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return findByG_LikeN(
			groupId, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where groupId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByG_LikeN(
		long groupId, String name, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset vocabulary in the ordered set where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByG_LikeN_First(
			long groupId, String name,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		return _collectionPersistenceFinderByG_LikeN.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name},
			orderByComparator);
	}

	/**
	 * Returns the first asset vocabulary in the ordered set where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByG_LikeN_First(
		long groupId, String name,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name},
			orderByComparator);
	}

	/**
	 * Returns all the asset vocabularies that the user has permission to view where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByG_LikeN(
		long groupId, String name) {

		return filterFindByG_LikeN(
			groupId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset vocabularies that the user has permission to view where groupId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @return the range of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByG_LikeN(
		long groupId, String name, int start, int end) {

		return filterFindByG_LikeN(groupId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies that the user has permissions to view where groupId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByG_LikeN(
		long groupId, String name, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the asset vocabularies where groupId = &#63; and name LIKE &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 */
	@Override
	public void removeByG_LikeN(long groupId, String name) {
		_collectionPersistenceFinderByG_LikeN.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name});
	}

	/**
	 * Returns the number of asset vocabularies where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByG_LikeN(long groupId, String name) {
		return _collectionPersistenceFinderByG_LikeN.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name});
	}

	/**
	 * Returns the number of asset vocabularies that the user has permission to view where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeN(long groupId, String name) {
		return _collectionPersistenceFinderByG_LikeN.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<AssetVocabulary, NoSuchVocabularyException>
			_collectionPersistenceFinderByG_V;

	/**
	 * Returns an ordered range of all the asset vocabularies where groupId = &#63; and visibilityType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByG_V(
		long groupId, int visibilityType, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_V.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}, new int[] {visibilityType}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset vocabulary in the ordered set where groupId = &#63; and visibilityType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByG_V_First(
			long groupId, int visibilityType,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = fetchByG_V_First(
			groupId, visibilityType, orderByComparator);

		if (assetVocabulary != null) {
			return assetVocabulary;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", visibilityType=");
		sb.append(visibilityType);

		sb.append("}");

		throw new NoSuchVocabularyException(sb.toString());
	}

	/**
	 * Returns the first asset vocabulary in the ordered set where groupId = &#63; and visibilityType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByG_V_First(
		long groupId, int visibilityType,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return _collectionPersistenceFinderByG_V.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}, new int[] {visibilityType}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies that the user has permissions to view where groupId = &#63; and visibilityType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByG_V(
		long groupId, int visibilityType, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return _collectionPersistenceFinderByG_V.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}, new int[] {visibilityType}},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies that the user has permission to view where groupId = any &#63; and visibilityType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param visibilityTypes the visibility types
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByG_V(
		long[] groupIds, int[] visibilityTypes, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_V.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupIds, ArrayUtil.sortedUnique(visibilityTypes)},
			start, end, orderByComparator, groupIds);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where groupId = &#63; and visibilityType = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param visibilityTypes the visibility types
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByG_V(
		long[] groupIds, int[] visibilityTypes, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_V.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				ArrayUtil.sortedUnique(groupIds),
				ArrayUtil.sortedUnique(visibilityTypes)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset vocabularies where groupId = &#63; and visibilityType = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 */
	@Override
	public void removeByG_V(long groupId, int visibilityType) {
		_collectionPersistenceFinderByG_V.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}, new int[] {visibilityType}});
	}

	/**
	 * Returns the number of asset vocabularies where groupId = &#63; and visibilityType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByG_V(long groupId, int visibilityType) {
		return _collectionPersistenceFinderByG_V.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}, new int[] {visibilityType}});
	}

	/**
	 * Returns the number of asset vocabularies where groupId = any &#63; and visibilityType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param visibilityTypes the visibility types
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByG_V(long[] groupIds, int[] visibilityTypes) {
		return _collectionPersistenceFinderByG_V.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				ArrayUtil.sortedUnique(groupIds),
				ArrayUtil.sortedUnique(visibilityTypes)
			});
	}

	/**
	 * Returns the number of asset vocabularies that the user has permission to view where groupId = &#63; and visibilityType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @return the number of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public int filterCountByG_V(long groupId, int visibilityType) {
		return _collectionPersistenceFinderByG_V.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}, new int[] {visibilityType}},
			groupId);
	}

	/**
	 * Returns the number of asset vocabularies that the user has permission to view where groupId = any &#63; and visibilityType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param visibilityTypes the visibility types
	 * @return the number of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public int filterCountByG_V(long[] groupIds, int[] visibilityTypes) {
		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_V.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupIds, ArrayUtil.sortedUnique(visibilityTypes)},
			groupIds);
	}

	private UniquePersistenceFinder<AssetVocabulary, NoSuchVocabularyException>
		_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the asset vocabulary where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchVocabularyException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchVocabularyException {

		return _uniquePersistenceFinderByERC_G.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the asset vocabulary where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId}, useFinderCache);
	}

	/**
	 * Removes the asset vocabulary where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the asset vocabulary that was removed
	 */
	@Override
	public AssetVocabulary removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = findByERC_G(
			externalReferenceCode, groupId);

		return remove(assetVocabulary);
	}

	/**
	 * Returns the number of asset vocabularies where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	public AssetVocabularyPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("settings", "settings_");

		setDBColumnNames(dbColumnNames);

		setModelClass(AssetVocabulary.class);

		setModelImplClass(AssetVocabularyImpl.class);
		setModelPKClass(long.class);

		setTable(AssetVocabularyTable.INSTANCE);
	}

	/**
	 * Creates a new asset vocabulary with the primary key. Does not add the asset vocabulary to the database.
	 *
	 * @param vocabularyId the primary key for the new asset vocabulary
	 * @return the new asset vocabulary
	 */
	@Override
	public AssetVocabulary create(long vocabularyId) {
		AssetVocabulary assetVocabulary = new AssetVocabularyImpl();

		assetVocabulary.setNew(true);
		assetVocabulary.setPrimaryKey(vocabularyId);

		String uuid = PortalUUIDUtil.generate();

		assetVocabulary.setUuid(uuid);

		assetVocabulary.setCompanyId(CompanyThreadLocal.getCompanyId());

		return assetVocabulary;
	}

	/**
	 * Removes the asset vocabulary with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param vocabularyId the primary key of the asset vocabulary
	 * @return the asset vocabulary that was removed
	 * @throws NoSuchVocabularyException if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary remove(long vocabularyId)
		throws NoSuchVocabularyException {

		return remove((Serializable)vocabularyId);
	}

	@Override
	protected AssetVocabulary removeImpl(AssetVocabulary assetVocabulary) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(assetVocabulary)) {
				assetVocabulary = (AssetVocabulary)session.get(
					AssetVocabularyImpl.class,
					assetVocabulary.getPrimaryKeyObj());
			}

			if ((assetVocabulary != null) &&
				CTPersistenceHelperUtil.isRemove(assetVocabulary)) {

				session.delete(assetVocabulary);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (assetVocabulary != null) {
			clearCache(assetVocabulary);
		}

		return assetVocabulary;
	}

	@Override
	public AssetVocabulary updateImpl(AssetVocabulary assetVocabulary) {
		boolean isNew = assetVocabulary.isNew();

		if (!(assetVocabulary instanceof AssetVocabularyModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(assetVocabulary.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					assetVocabulary);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in assetVocabulary proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AssetVocabulary implementation " +
					assetVocabulary.getClass());
		}

		AssetVocabularyModelImpl assetVocabularyModelImpl =
			(AssetVocabularyModelImpl)assetVocabulary;

		if (Validator.isNull(assetVocabulary.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			assetVocabulary.setUuid(uuid);
		}

		if (Validator.isNull(assetVocabulary.getExternalReferenceCode())) {
			assetVocabulary.setExternalReferenceCode(assetVocabulary.getUuid());
		}
		else {
			if (!Objects.equals(
					assetVocabularyModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					assetVocabulary.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = assetVocabulary.getCompanyId();

					long groupId = assetVocabulary.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = assetVocabulary.getPrimaryKey();
					}

					try {
						assetVocabulary.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								AssetVocabulary.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								assetVocabulary.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			AssetVocabulary ercAssetVocabulary = fetchByERC_G(
				assetVocabulary.getExternalReferenceCode(),
				assetVocabulary.getGroupId());

			if (isNew) {
				if (ercAssetVocabulary != null) {
					throw new DuplicateAssetVocabularyExternalReferenceCodeException(
						"Duplicate asset vocabulary with external reference code " +
							assetVocabulary.getExternalReferenceCode() +
								" and group " + assetVocabulary.getGroupId());
				}
			}
			else {
				if ((ercAssetVocabulary != null) &&
					(assetVocabulary.getVocabularyId() !=
						ercAssetVocabulary.getVocabularyId())) {

					throw new DuplicateAssetVocabularyExternalReferenceCodeException(
						"Duplicate asset vocabulary with external reference code " +
							assetVocabulary.getExternalReferenceCode() +
								" and group " + assetVocabulary.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (assetVocabulary.getCreateDate() == null)) {
			if (serviceContext == null) {
				assetVocabulary.setCreateDate(date);
			}
			else {
				assetVocabulary.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!assetVocabularyModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				assetVocabulary.setModifiedDate(date);
			}
			else {
				assetVocabulary.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(assetVocabulary)) {
				if (!isNew) {
					session.evict(
						AssetVocabularyImpl.class,
						assetVocabulary.getPrimaryKeyObj());
				}

				session.save(assetVocabulary);
			}
			else {
				assetVocabulary = (AssetVocabulary)session.merge(
					assetVocabulary);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(assetVocabulary, false);

		if (isNew) {
			assetVocabulary.setNew(false);
		}

		assetVocabulary.resetOriginalValues();

		return assetVocabulary;
	}

	/**
	 * Returns the asset vocabulary with the primary key or throws a <code>NoSuchVocabularyException</code> if it could not be found.
	 *
	 * @param vocabularyId the primary key of the asset vocabulary
	 * @return the asset vocabulary
	 * @throws NoSuchVocabularyException if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary findByPrimaryKey(long vocabularyId)
		throws NoSuchVocabularyException {

		return findByPrimaryKey((Serializable)vocabularyId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the asset vocabulary with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param vocabularyId the primary key of the asset vocabulary
	 * @return the asset vocabulary, or <code>null</code> if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary fetchByPrimaryKey(long vocabularyId) {
		return fetchByPrimaryKey((Serializable)vocabularyId);
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
		return "vocabularyId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ASSETVOCABULARY;
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
		return AssetVocabularyModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AssetVocabulary";
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
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("settings_");
		ctMergeColumnNames.add("visibilityType");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("vocabularyId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"groupId", "name"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the asset vocabulary persistence.
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
			_SQL_SELECT_ASSETVOCABULARY_WHERE, _SQL_COUNT_ASSETVOCABULARY_WHERE,
			AssetVocabularyModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"assetVocabulary.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, AssetVocabulary::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(AssetVocabulary::getUuid),
				AssetVocabulary::getGroupId),
			_SQL_SELECT_ASSETVOCABULARY_WHERE, "",
			new FinderColumn<>(
				"assetVocabulary.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, AssetVocabulary::getUuid),
			new FinderColumn<>(
				"assetVocabulary.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, AssetVocabulary::getGroupId));

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
				_SQL_SELECT_ASSETVOCABULARY_WHERE,
				_SQL_COUNT_ASSETVOCABULARY_WHERE,
				AssetVocabularyModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"assetVocabulary.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					AssetVocabulary::getUuid),
				new FinderColumn<>(
					"assetVocabulary.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, AssetVocabulary::getCompanyId));

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
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_ASSETVOCABULARY_WHERE,
				_SQL_COUNT_ASSETVOCABULARY_WHERE,
				AssetVocabularyModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new ArrayableFinderColumn<>(
					"assetVocabulary.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, AssetVocabulary::getGroupId));

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
				_SQL_SELECT_ASSETVOCABULARY_WHERE,
				_SQL_COUNT_ASSETVOCABULARY_WHERE,
				AssetVocabularyModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"assetVocabulary.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, AssetVocabulary::getCompanyId));

		_uniquePersistenceFinderByG_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "name"}, 2, 2, false,
				AssetVocabulary::getGroupId,
				convertCaseFunction(AssetVocabulary::getName)),
			_SQL_SELECT_ASSETVOCABULARY_WHERE, "",
			new FinderColumn<>(
				"assetVocabulary.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, AssetVocabulary::getGroupId),
			new FinderColumn<>(
				"assetVocabulary.", "name", FinderColumn.Type.STRING, "=",
				false, true, AssetVocabulary::getName));

		_collectionPersistenceFinderByG_LikeN =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeN",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LikeN",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "name"}, false),
				_SQL_SELECT_ASSETVOCABULARY_WHERE,
				_SQL_COUNT_ASSETVOCABULARY_WHERE,
				AssetVocabularyModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"assetVocabulary.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, AssetVocabulary::getGroupId),
				new FinderColumn<>(
					"assetVocabulary.", "name", FinderColumn.Type.STRING,
					"LIKE", false, true, AssetVocabulary::getName));

		_collectionPersistenceFinderByG_V =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_V",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "visibilityType"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_V",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "visibilityType"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_V",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "visibilityType"}, false),
				_SQL_SELECT_ASSETVOCABULARY_WHERE,
				_SQL_COUNT_ASSETVOCABULARY_WHERE,
				AssetVocabularyModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new ArrayableFinderColumn<>(
					"assetVocabulary.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, AssetVocabulary::getGroupId),
				new ArrayableFinderColumn<>(
					"assetVocabulary.", "visibilityType",
					FinderColumn.Type.INTEGER, "=", false, true, true,
					AssetVocabulary::getVisibilityType));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(AssetVocabulary::getExternalReferenceCode),
				AssetVocabulary::getGroupId),
			_SQL_SELECT_ASSETVOCABULARY_WHERE, "",
			new FinderColumn<>(
				"assetVocabulary.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				AssetVocabulary::getExternalReferenceCode),
			new FinderColumn<>(
				"assetVocabulary.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, AssetVocabulary::getGroupId));

		AssetVocabularyUtil.setPersistence(this);
	}

	public void destroy() {
		AssetVocabularyUtil.setPersistence(null);

		EntityCacheUtil.removeCache(AssetVocabularyImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		AssetVocabularyModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ASSETVOCABULARY =
		"SELECT assetVocabulary FROM AssetVocabulary assetVocabulary";

	private static final String _SQL_SELECT_ASSETVOCABULARY_WHERE =
		"SELECT assetVocabulary FROM AssetVocabulary assetVocabulary WHERE ";

	private static final String _SQL_COUNT_ASSETVOCABULARY_WHERE =
		"SELECT COUNT(assetVocabulary) FROM AssetVocabulary assetVocabulary WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No AssetVocabulary exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		AssetVocabularyPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "settings"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1933739440