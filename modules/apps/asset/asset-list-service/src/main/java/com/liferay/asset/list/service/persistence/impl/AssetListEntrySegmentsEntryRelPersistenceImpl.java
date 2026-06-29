/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.persistence.impl;

import com.liferay.asset.list.exception.NoSuchEntrySegmentsEntryRelException;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRelTable;
import com.liferay.asset.list.model.impl.AssetListEntrySegmentsEntryRelImpl;
import com.liferay.asset.list.model.impl.AssetListEntrySegmentsEntryRelModelImpl;
import com.liferay.asset.list.service.persistence.AssetListEntrySegmentsEntryRelPersistence;
import com.liferay.asset.list.service.persistence.AssetListEntrySegmentsEntryRelUtil;
import com.liferay.asset.list.service.persistence.impl.constants.AssetListPersistenceConstants;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

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
 * The persistence implementation for the asset list entry segments entry rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AssetListEntrySegmentsEntryRelPersistence.class)
public class AssetListEntrySegmentsEntryRelPersistenceImpl
	extends BasePersistenceImpl
		<AssetListEntrySegmentsEntryRel, NoSuchEntrySegmentsEntryRelException>
	implements AssetListEntrySegmentsEntryRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AssetListEntrySegmentsEntryRelUtil</code> to access the asset list entry segments entry rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AssetListEntrySegmentsEntryRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AssetListEntrySegmentsEntryRel, NoSuchEntrySegmentsEntryRelException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the asset list entry segments entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntrySegmentsEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entry segments entry rels
	 * @param end the upper bound of the range of asset list entry segments entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry segments entry rels
	 */
	@Override
	public List<AssetListEntrySegmentsEntryRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetListEntrySegmentsEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset list entry segments entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry segments entry rel
	 * @throws NoSuchEntrySegmentsEntryRelException if a matching asset list entry segments entry rel could not be found
	 */
	@Override
	public AssetListEntrySegmentsEntryRel findByUuid_First(
			String uuid,
			OrderByComparator<AssetListEntrySegmentsEntryRel> orderByComparator)
		throws NoSuchEntrySegmentsEntryRelException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first asset list entry segments entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry segments entry rel, or <code>null</code> if a matching asset list entry segments entry rel could not be found
	 */
	@Override
	public AssetListEntrySegmentsEntryRel fetchByUuid_First(
		String uuid,
		OrderByComparator<AssetListEntrySegmentsEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the asset list entry segments entry rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of asset list entry segments entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset list entry segments entry rels
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<AssetListEntrySegmentsEntryRel, NoSuchEntrySegmentsEntryRelException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the asset list entry segments entry rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntrySegmentsEntryRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset list entry segments entry rel
	 * @throws NoSuchEntrySegmentsEntryRelException if a matching asset list entry segments entry rel could not be found
	 */
	@Override
	public AssetListEntrySegmentsEntryRel findByUUID_G(
			String uuid, long groupId)
		throws NoSuchEntrySegmentsEntryRelException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the asset list entry segments entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry segments entry rel, or <code>null</code> if a matching asset list entry segments entry rel could not be found
	 */
	@Override
	public AssetListEntrySegmentsEntryRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the asset list entry segments entry rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset list entry segments entry rel that was removed
	 */
	@Override
	public AssetListEntrySegmentsEntryRel removeByUUID_G(
			String uuid, long groupId)
		throws NoSuchEntrySegmentsEntryRelException {

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			findByUUID_G(uuid, groupId);

		return remove(assetListEntrySegmentsEntryRel);
	}

	/**
	 * Returns the number of asset list entry segments entry rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset list entry segments entry rels
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<AssetListEntrySegmentsEntryRel, NoSuchEntrySegmentsEntryRelException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the asset list entry segments entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntrySegmentsEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entry segments entry rels
	 * @param end the upper bound of the range of asset list entry segments entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry segments entry rels
	 */
	@Override
	public List<AssetListEntrySegmentsEntryRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetListEntrySegmentsEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry segments entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry segments entry rel
	 * @throws NoSuchEntrySegmentsEntryRelException if a matching asset list entry segments entry rel could not be found
	 */
	@Override
	public AssetListEntrySegmentsEntryRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<AssetListEntrySegmentsEntryRel> orderByComparator)
		throws NoSuchEntrySegmentsEntryRelException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first asset list entry segments entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry segments entry rel, or <code>null</code> if a matching asset list entry segments entry rel could not be found
	 */
	@Override
	public AssetListEntrySegmentsEntryRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<AssetListEntrySegmentsEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the asset list entry segments entry rels where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of asset list entry segments entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset list entry segments entry rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<AssetListEntrySegmentsEntryRel, NoSuchEntrySegmentsEntryRelException>
			_collectionPersistenceFinderByAssetListEntryId;

	/**
	 * Returns an ordered range of all the asset list entry segments entry rels where assetListEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntrySegmentsEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param start the lower bound of the range of asset list entry segments entry rels
	 * @param end the upper bound of the range of asset list entry segments entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry segments entry rels
	 */
	@Override
	public List<AssetListEntrySegmentsEntryRel> findByAssetListEntryId(
		long assetListEntryId, int start, int end,
		OrderByComparator<AssetListEntrySegmentsEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAssetListEntryId.find(
			finderCache, new Object[] {assetListEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry segments entry rel in the ordered set where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry segments entry rel
	 * @throws NoSuchEntrySegmentsEntryRelException if a matching asset list entry segments entry rel could not be found
	 */
	@Override
	public AssetListEntrySegmentsEntryRel findByAssetListEntryId_First(
			long assetListEntryId,
			OrderByComparator<AssetListEntrySegmentsEntryRel> orderByComparator)
		throws NoSuchEntrySegmentsEntryRelException {

		return _collectionPersistenceFinderByAssetListEntryId.findFirst(
			finderCache, new Object[] {assetListEntryId}, orderByComparator);
	}

	/**
	 * Returns the first asset list entry segments entry rel in the ordered set where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry segments entry rel, or <code>null</code> if a matching asset list entry segments entry rel could not be found
	 */
	@Override
	public AssetListEntrySegmentsEntryRel fetchByAssetListEntryId_First(
		long assetListEntryId,
		OrderByComparator<AssetListEntrySegmentsEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByAssetListEntryId.fetchFirst(
			finderCache, new Object[] {assetListEntryId}, orderByComparator);
	}

	/**
	 * Removes all the asset list entry segments entry rels where assetListEntryId = &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 */
	@Override
	public void removeByAssetListEntryId(long assetListEntryId) {
		_collectionPersistenceFinderByAssetListEntryId.remove(
			finderCache, new Object[] {assetListEntryId});
	}

	/**
	 * Returns the number of asset list entry segments entry rels where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @return the number of matching asset list entry segments entry rels
	 */
	@Override
	public int countByAssetListEntryId(long assetListEntryId) {
		return _collectionPersistenceFinderByAssetListEntryId.count(
			finderCache, new Object[] {assetListEntryId});
	}

	private CollectionPersistenceFinder
		<AssetListEntrySegmentsEntryRel, NoSuchEntrySegmentsEntryRelException>
			_collectionPersistenceFinderBySegmentsEntryId;

	/**
	 * Returns an ordered range of all the asset list entry segments entry rels where segmentsEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntrySegmentsEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param start the lower bound of the range of asset list entry segments entry rels
	 * @param end the upper bound of the range of asset list entry segments entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry segments entry rels
	 */
	@Override
	public List<AssetListEntrySegmentsEntryRel> findBySegmentsEntryId(
		long segmentsEntryId, int start, int end,
		OrderByComparator<AssetListEntrySegmentsEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySegmentsEntryId.find(
			finderCache, new Object[] {segmentsEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry segments entry rel in the ordered set where segmentsEntryId = &#63;.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry segments entry rel
	 * @throws NoSuchEntrySegmentsEntryRelException if a matching asset list entry segments entry rel could not be found
	 */
	@Override
	public AssetListEntrySegmentsEntryRel findBySegmentsEntryId_First(
			long segmentsEntryId,
			OrderByComparator<AssetListEntrySegmentsEntryRel> orderByComparator)
		throws NoSuchEntrySegmentsEntryRelException {

		return _collectionPersistenceFinderBySegmentsEntryId.findFirst(
			finderCache, new Object[] {segmentsEntryId}, orderByComparator);
	}

	/**
	 * Returns the first asset list entry segments entry rel in the ordered set where segmentsEntryId = &#63;.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry segments entry rel, or <code>null</code> if a matching asset list entry segments entry rel could not be found
	 */
	@Override
	public AssetListEntrySegmentsEntryRel fetchBySegmentsEntryId_First(
		long segmentsEntryId,
		OrderByComparator<AssetListEntrySegmentsEntryRel> orderByComparator) {

		return _collectionPersistenceFinderBySegmentsEntryId.fetchFirst(
			finderCache, new Object[] {segmentsEntryId}, orderByComparator);
	}

	/**
	 * Removes all the asset list entry segments entry rels where segmentsEntryId = &#63; from the database.
	 *
	 * @param segmentsEntryId the segments entry ID
	 */
	@Override
	public void removeBySegmentsEntryId(long segmentsEntryId) {
		_collectionPersistenceFinderBySegmentsEntryId.remove(
			finderCache, new Object[] {segmentsEntryId});
	}

	/**
	 * Returns the number of asset list entry segments entry rels where segmentsEntryId = &#63;.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @return the number of matching asset list entry segments entry rels
	 */
	@Override
	public int countBySegmentsEntryId(long segmentsEntryId) {
		return _collectionPersistenceFinderBySegmentsEntryId.count(
			finderCache, new Object[] {segmentsEntryId});
	}

	private UniquePersistenceFinder
		<AssetListEntrySegmentsEntryRel, NoSuchEntrySegmentsEntryRelException>
			_uniquePersistenceFinderByA_S;

	/**
	 * Returns the asset list entry segments entry rel where assetListEntryId = &#63; and segmentsEntryId = &#63; or throws a <code>NoSuchEntrySegmentsEntryRelException</code> if it could not be found.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @return the matching asset list entry segments entry rel
	 * @throws NoSuchEntrySegmentsEntryRelException if a matching asset list entry segments entry rel could not be found
	 */
	@Override
	public AssetListEntrySegmentsEntryRel findByA_S(
			long assetListEntryId, long segmentsEntryId)
		throws NoSuchEntrySegmentsEntryRelException {

		return _uniquePersistenceFinderByA_S.find(
			finderCache, new Object[] {assetListEntryId, segmentsEntryId});
	}

	/**
	 * Returns the asset list entry segments entry rel where assetListEntryId = &#63; and segmentsEntryId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry segments entry rel, or <code>null</code> if a matching asset list entry segments entry rel could not be found
	 */
	@Override
	public AssetListEntrySegmentsEntryRel fetchByA_S(
		long assetListEntryId, long segmentsEntryId, boolean useFinderCache) {

		return _uniquePersistenceFinderByA_S.fetch(
			finderCache, new Object[] {assetListEntryId, segmentsEntryId},
			useFinderCache);
	}

	/**
	 * Removes the asset list entry segments entry rel where assetListEntryId = &#63; and segmentsEntryId = &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @return the asset list entry segments entry rel that was removed
	 */
	@Override
	public AssetListEntrySegmentsEntryRel removeByA_S(
			long assetListEntryId, long segmentsEntryId)
		throws NoSuchEntrySegmentsEntryRelException {

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			findByA_S(assetListEntryId, segmentsEntryId);

		return remove(assetListEntrySegmentsEntryRel);
	}

	/**
	 * Returns the number of asset list entry segments entry rels where assetListEntryId = &#63; and segmentsEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @return the number of matching asset list entry segments entry rels
	 */
	@Override
	public int countByA_S(long assetListEntryId, long segmentsEntryId) {
		return _uniquePersistenceFinderByA_S.count(
			finderCache, new Object[] {assetListEntryId, segmentsEntryId});
	}

	private CollectionPersistenceFinder
		<AssetListEntrySegmentsEntryRel, NoSuchEntrySegmentsEntryRelException>
			_collectionPersistenceFinderByA_S_C;

	/**
	 * Returns an ordered range of all the asset list entry segments entry rels where assetListEntryId = &#63; and segmentsEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntrySegmentsEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @param start the lower bound of the range of asset list entry segments entry rels
	 * @param end the upper bound of the range of asset list entry segments entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry segments entry rels
	 */
	@Override
	public List<AssetListEntrySegmentsEntryRel> findByA_S_C(
		long assetListEntryId, long segmentsEntryId, int start, int end,
		OrderByComparator<AssetListEntrySegmentsEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByA_S_C.find(
			finderCache,
			new Object[] {assetListEntryId, new long[] {segmentsEntryId}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry segments entry rel in the ordered set where assetListEntryId = &#63; and segmentsEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry segments entry rel
	 * @throws NoSuchEntrySegmentsEntryRelException if a matching asset list entry segments entry rel could not be found
	 */
	@Override
	public AssetListEntrySegmentsEntryRel findByA_S_C_First(
			long assetListEntryId, long segmentsEntryId,
			OrderByComparator<AssetListEntrySegmentsEntryRel> orderByComparator)
		throws NoSuchEntrySegmentsEntryRelException {

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			fetchByA_S_C_First(
				assetListEntryId, segmentsEntryId, orderByComparator);

		if (assetListEntrySegmentsEntryRel != null) {
			return assetListEntrySegmentsEntryRel;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("assetListEntryId=");
		sb.append(assetListEntryId);

		sb.append(", segmentsEntryId=");
		sb.append(segmentsEntryId);

		sb.append("}");

		throw new NoSuchEntrySegmentsEntryRelException(sb.toString());
	}

	/**
	 * Returns the first asset list entry segments entry rel in the ordered set where assetListEntryId = &#63; and segmentsEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry segments entry rel, or <code>null</code> if a matching asset list entry segments entry rel could not be found
	 */
	@Override
	public AssetListEntrySegmentsEntryRel fetchByA_S_C_First(
		long assetListEntryId, long segmentsEntryId,
		OrderByComparator<AssetListEntrySegmentsEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByA_S_C.fetchFirst(
			finderCache,
			new Object[] {assetListEntryId, new long[] {segmentsEntryId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entry segments entry rels where assetListEntryId = &#63; and segmentsEntryId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntrySegmentsEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryIds the segments entry IDs
	 * @param start the lower bound of the range of asset list entry segments entry rels
	 * @param end the upper bound of the range of asset list entry segments entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry segments entry rels
	 */
	@Override
	public List<AssetListEntrySegmentsEntryRel> findByA_S_C(
		long assetListEntryId, long[] segmentsEntryIds, int start, int end,
		OrderByComparator<AssetListEntrySegmentsEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByA_S_C.find(
			finderCache,
			new Object[] {
				assetListEntryId, ArrayUtil.sortedUnique(segmentsEntryIds)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset list entry segments entry rels where assetListEntryId = &#63; and segmentsEntryId = &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 */
	@Override
	public void removeByA_S_C(long assetListEntryId, long segmentsEntryId) {
		_collectionPersistenceFinderByA_S_C.remove(
			finderCache,
			new Object[] {assetListEntryId, new long[] {segmentsEntryId}});
	}

	/**
	 * Returns the number of asset list entry segments entry rels where assetListEntryId = &#63; and segmentsEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @return the number of matching asset list entry segments entry rels
	 */
	@Override
	public int countByA_S_C(long assetListEntryId, long segmentsEntryId) {
		return _collectionPersistenceFinderByA_S_C.count(
			finderCache,
			new Object[] {assetListEntryId, new long[] {segmentsEntryId}});
	}

	/**
	 * Returns the number of asset list entry segments entry rels where assetListEntryId = &#63; and segmentsEntryId = any &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryIds the segments entry IDs
	 * @return the number of matching asset list entry segments entry rels
	 */
	@Override
	public int countByA_S_C(long assetListEntryId, long[] segmentsEntryIds) {
		return _collectionPersistenceFinderByA_S_C.count(
			finderCache,
			new Object[] {
				assetListEntryId, ArrayUtil.sortedUnique(segmentsEntryIds)
			});
	}

	public AssetListEntrySegmentsEntryRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"assetListEntrySegmentsEntryRelId", "alEntrySegmentsEntryRelId");

		setDBColumnNames(dbColumnNames);

		setModelClass(AssetListEntrySegmentsEntryRel.class);

		setModelImplClass(AssetListEntrySegmentsEntryRelImpl.class);
		setModelPKClass(long.class);

		setTable(AssetListEntrySegmentsEntryRelTable.INSTANCE);
	}

	/**
	 * Creates a new asset list entry segments entry rel with the primary key. Does not add the asset list entry segments entry rel to the database.
	 *
	 * @param assetListEntrySegmentsEntryRelId the primary key for the new asset list entry segments entry rel
	 * @return the new asset list entry segments entry rel
	 */
	@Override
	public AssetListEntrySegmentsEntryRel create(
		long assetListEntrySegmentsEntryRelId) {

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			new AssetListEntrySegmentsEntryRelImpl();

		assetListEntrySegmentsEntryRel.setNew(true);
		assetListEntrySegmentsEntryRel.setPrimaryKey(
			assetListEntrySegmentsEntryRelId);

		String uuid = PortalUUIDUtil.generate();

		assetListEntrySegmentsEntryRel.setUuid(uuid);

		assetListEntrySegmentsEntryRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return assetListEntrySegmentsEntryRel;
	}

	/**
	 * Removes the asset list entry segments entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param assetListEntrySegmentsEntryRelId the primary key of the asset list entry segments entry rel
	 * @return the asset list entry segments entry rel that was removed
	 * @throws NoSuchEntrySegmentsEntryRelException if a asset list entry segments entry rel with the primary key could not be found
	 */
	@Override
	public AssetListEntrySegmentsEntryRel remove(
			long assetListEntrySegmentsEntryRelId)
		throws NoSuchEntrySegmentsEntryRelException {

		return remove((Serializable)assetListEntrySegmentsEntryRelId);
	}

	@Override
	protected AssetListEntrySegmentsEntryRel removeImpl(
		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(assetListEntrySegmentsEntryRel)) {
				assetListEntrySegmentsEntryRel =
					(AssetListEntrySegmentsEntryRel)session.get(
						AssetListEntrySegmentsEntryRelImpl.class,
						assetListEntrySegmentsEntryRel.getPrimaryKeyObj());
			}

			if ((assetListEntrySegmentsEntryRel != null) &&
				ctPersistenceHelper.isRemove(assetListEntrySegmentsEntryRel)) {

				session.delete(assetListEntrySegmentsEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (assetListEntrySegmentsEntryRel != null) {
			clearCache(assetListEntrySegmentsEntryRel);
		}

		return assetListEntrySegmentsEntryRel;
	}

	@Override
	public AssetListEntrySegmentsEntryRel updateImpl(
		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel) {

		boolean isNew = assetListEntrySegmentsEntryRel.isNew();

		if (!(assetListEntrySegmentsEntryRel instanceof
				AssetListEntrySegmentsEntryRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					assetListEntrySegmentsEntryRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					assetListEntrySegmentsEntryRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in assetListEntrySegmentsEntryRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AssetListEntrySegmentsEntryRel implementation " +
					assetListEntrySegmentsEntryRel.getClass());
		}

		AssetListEntrySegmentsEntryRelModelImpl
			assetListEntrySegmentsEntryRelModelImpl =
				(AssetListEntrySegmentsEntryRelModelImpl)
					assetListEntrySegmentsEntryRel;

		if (Validator.isNull(assetListEntrySegmentsEntryRel.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			assetListEntrySegmentsEntryRel.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (assetListEntrySegmentsEntryRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				assetListEntrySegmentsEntryRel.setCreateDate(date);
			}
			else {
				assetListEntrySegmentsEntryRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!assetListEntrySegmentsEntryRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				assetListEntrySegmentsEntryRel.setModifiedDate(date);
			}
			else {
				assetListEntrySegmentsEntryRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(assetListEntrySegmentsEntryRel)) {
				if (!isNew) {
					session.evict(
						AssetListEntrySegmentsEntryRelImpl.class,
						assetListEntrySegmentsEntryRel.getPrimaryKeyObj());
				}

				session.save(assetListEntrySegmentsEntryRel);
			}
			else {
				assetListEntrySegmentsEntryRel =
					(AssetListEntrySegmentsEntryRel)session.merge(
						assetListEntrySegmentsEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(assetListEntrySegmentsEntryRel, false);

		if (isNew) {
			assetListEntrySegmentsEntryRel.setNew(false);
		}

		assetListEntrySegmentsEntryRel.resetOriginalValues();

		return assetListEntrySegmentsEntryRel;
	}

	/**
	 * Returns the asset list entry segments entry rel with the primary key or throws a <code>NoSuchEntrySegmentsEntryRelException</code> if it could not be found.
	 *
	 * @param assetListEntrySegmentsEntryRelId the primary key of the asset list entry segments entry rel
	 * @return the asset list entry segments entry rel
	 * @throws NoSuchEntrySegmentsEntryRelException if a asset list entry segments entry rel with the primary key could not be found
	 */
	@Override
	public AssetListEntrySegmentsEntryRel findByPrimaryKey(
			long assetListEntrySegmentsEntryRelId)
		throws NoSuchEntrySegmentsEntryRelException {

		return findByPrimaryKey((Serializable)assetListEntrySegmentsEntryRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the asset list entry segments entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param assetListEntrySegmentsEntryRelId the primary key of the asset list entry segments entry rel
	 * @return the asset list entry segments entry rel, or <code>null</code> if a asset list entry segments entry rel with the primary key could not be found
	 */
	@Override
	public AssetListEntrySegmentsEntryRel fetchByPrimaryKey(
		long assetListEntrySegmentsEntryRelId) {

		return fetchByPrimaryKey(
			(Serializable)assetListEntrySegmentsEntryRelId);
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
		return "alEntrySegmentsEntryRelId";
	}

	@Override
	protected String getPKFieldName() {
		return "assetListEntrySegmentsEntryRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ASSETLISTENTRYSEGMENTSENTRYREL;
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
		return AssetListEntrySegmentsEntryRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AssetListEntrySegmentsEntryRel";
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
		ctMergeColumnNames.add("assetListEntryId");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("segmentsEntryId");
		ctMergeColumnNames.add("typeSettings");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("alEntrySegmentsEntryRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"assetListEntryId", "segmentsEntryId"});
	}

	/**
	 * Initializes the asset list entry segments entry rel persistence.
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
			_SQL_SELECT_ASSETLISTENTRYSEGMENTSENTRYREL_WHERE,
			_SQL_COUNT_ASSETLISTENTRYSEGMENTSENTRYREL_WHERE,
			AssetListEntrySegmentsEntryRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"assetListEntrySegmentsEntryRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				AssetListEntrySegmentsEntryRel::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(AssetListEntrySegmentsEntryRel::getUuid),
				AssetListEntrySegmentsEntryRel::getGroupId),
			_SQL_SELECT_ASSETLISTENTRYSEGMENTSENTRYREL_WHERE, "",
			new FinderColumn<>(
				"assetListEntrySegmentsEntryRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				AssetListEntrySegmentsEntryRel::getUuid),
			new FinderColumn<>(
				"assetListEntrySegmentsEntryRel.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				AssetListEntrySegmentsEntryRel::getGroupId));

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
				_SQL_SELECT_ASSETLISTENTRYSEGMENTSENTRYREL_WHERE,
				_SQL_COUNT_ASSETLISTENTRYSEGMENTSENTRYREL_WHERE,
				AssetListEntrySegmentsEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"assetListEntrySegmentsEntryRel.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					AssetListEntrySegmentsEntryRel::getUuid),
				new FinderColumn<>(
					"assetListEntrySegmentsEntryRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetListEntrySegmentsEntryRel::getCompanyId));

		_collectionPersistenceFinderByAssetListEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByAssetListEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"assetListEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByAssetListEntryId",
					new String[] {Long.class.getName()},
					new String[] {"assetListEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByAssetListEntryId",
					new String[] {Long.class.getName()},
					new String[] {"assetListEntryId"}, false),
				_SQL_SELECT_ASSETLISTENTRYSEGMENTSENTRYREL_WHERE,
				_SQL_COUNT_ASSETLISTENTRYSEGMENTSENTRYREL_WHERE,
				AssetListEntrySegmentsEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"assetListEntrySegmentsEntryRel.", "assetListEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetListEntrySegmentsEntryRel::getAssetListEntryId));

		_collectionPersistenceFinderBySegmentsEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findBySegmentsEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"segmentsEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findBySegmentsEntryId",
					new String[] {Long.class.getName()},
					new String[] {"segmentsEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countBySegmentsEntryId",
					new String[] {Long.class.getName()},
					new String[] {"segmentsEntryId"}, false),
				_SQL_SELECT_ASSETLISTENTRYSEGMENTSENTRYREL_WHERE,
				_SQL_COUNT_ASSETLISTENTRYSEGMENTSENTRYREL_WHERE,
				AssetListEntrySegmentsEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"assetListEntrySegmentsEntryRel.", "segmentsEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetListEntrySegmentsEntryRel::getSegmentsEntryId));

		_uniquePersistenceFinderByA_S = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByA_S",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"assetListEntryId", "segmentsEntryId"}, 0, 0,
				false, AssetListEntrySegmentsEntryRel::getAssetListEntryId,
				AssetListEntrySegmentsEntryRel::getSegmentsEntryId),
			_SQL_SELECT_ASSETLISTENTRYSEGMENTSENTRYREL_WHERE, "",
			new FinderColumn<>(
				"assetListEntrySegmentsEntryRel.", "assetListEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				AssetListEntrySegmentsEntryRel::getAssetListEntryId),
			new FinderColumn<>(
				"assetListEntrySegmentsEntryRel.", "segmentsEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				AssetListEntrySegmentsEntryRel::getSegmentsEntryId));

		_collectionPersistenceFinderByA_S_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByA_S_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"assetListEntryId", "segmentsEntryId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByA_S_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"assetListEntryId", "segmentsEntryId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByA_S_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"assetListEntryId", "segmentsEntryId"}, false),
			_SQL_SELECT_ASSETLISTENTRYSEGMENTSENTRYREL_WHERE,
			_SQL_COUNT_ASSETLISTENTRYSEGMENTSENTRYREL_WHERE,
			AssetListEntrySegmentsEntryRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"assetListEntrySegmentsEntryRel.", "assetListEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				AssetListEntrySegmentsEntryRel::getAssetListEntryId),
			new ArrayableFinderColumn<>(
				"assetListEntrySegmentsEntryRel.", "segmentsEntryId",
				FinderColumn.Type.LONG, "=", false, true, true,
				AssetListEntrySegmentsEntryRel::getSegmentsEntryId));

		AssetListEntrySegmentsEntryRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AssetListEntrySegmentsEntryRelUtil.setPersistence(null);

		entityCache.removeCache(
			AssetListEntrySegmentsEntryRelImpl.class.getName());
	}

	@Override
	@Reference(
		target = AssetListPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = AssetListPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = AssetListPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		AssetListEntrySegmentsEntryRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ASSETLISTENTRYSEGMENTSENTRYREL =
		"SELECT assetListEntrySegmentsEntryRel FROM AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel";

	private static final String
		_SQL_SELECT_ASSETLISTENTRYSEGMENTSENTRYREL_WHERE =
			"SELECT assetListEntrySegmentsEntryRel FROM AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel WHERE ";

	private static final String
		_SQL_COUNT_ASSETLISTENTRYSEGMENTSENTRYREL_WHERE =
			"SELECT COUNT(assetListEntrySegmentsEntryRel) FROM AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No AssetListEntrySegmentsEntryRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		AssetListEntrySegmentsEntryRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "assetListEntrySegmentsEntryRelId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2115560819