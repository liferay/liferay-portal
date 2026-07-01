/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.persistence.impl;

import com.liferay.asset.list.exception.NoSuchEntryAssetEntryRelException;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRelTable;
import com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelImpl;
import com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl;
import com.liferay.asset.list.service.persistence.AssetListEntryAssetEntryRelPersistence;
import com.liferay.asset.list.service.persistence.AssetListEntryAssetEntryRelUtil;
import com.liferay.asset.list.service.persistence.impl.constants.AssetListPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
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
 * The persistence implementation for the asset list entry asset entry rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AssetListEntryAssetEntryRelPersistence.class)
public class AssetListEntryAssetEntryRelPersistenceImpl
	extends BasePersistenceImpl
		<AssetListEntryAssetEntryRel, NoSuchEntryAssetEntryRelException>
	implements AssetListEntryAssetEntryRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AssetListEntryAssetEntryRelUtil</code> to access the asset list entry asset entry rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AssetListEntryAssetEntryRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AssetListEntryAssetEntryRel, NoSuchEntryAssetEntryRelException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	@Override
	public List<AssetListEntryAssetEntryRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel
	 * @throws NoSuchEntryAssetEntryRelException if a matching asset list entry asset entry rel could not be found
	 */
	@Override
	public AssetListEntryAssetEntryRel findByUuid_First(
			String uuid,
			OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator)
		throws NoSuchEntryAssetEntryRelException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel, or <code>null</code> if a matching asset list entry asset entry rel could not be found
	 */
	@Override
	public AssetListEntryAssetEntryRel fetchByUuid_First(
		String uuid,
		OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the asset list entry asset entry rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of asset list entry asset entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset list entry asset entry rels
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<AssetListEntryAssetEntryRel, NoSuchEntryAssetEntryRelException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the asset list entry asset entry rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryAssetEntryRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset list entry asset entry rel
	 * @throws NoSuchEntryAssetEntryRelException if a matching asset list entry asset entry rel could not be found
	 */
	@Override
	public AssetListEntryAssetEntryRel findByUUID_G(String uuid, long groupId)
		throws NoSuchEntryAssetEntryRelException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the asset list entry asset entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry asset entry rel, or <code>null</code> if a matching asset list entry asset entry rel could not be found
	 */
	@Override
	public AssetListEntryAssetEntryRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the asset list entry asset entry rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset list entry asset entry rel that was removed
	 */
	@Override
	public AssetListEntryAssetEntryRel removeByUUID_G(String uuid, long groupId)
		throws NoSuchEntryAssetEntryRelException {

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel = findByUUID_G(
			uuid, groupId);

		return remove(assetListEntryAssetEntryRel);
	}

	/**
	 * Returns the number of asset list entry asset entry rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset list entry asset entry rels
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<AssetListEntryAssetEntryRel, NoSuchEntryAssetEntryRelException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	@Override
	public List<AssetListEntryAssetEntryRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel
	 * @throws NoSuchEntryAssetEntryRelException if a matching asset list entry asset entry rel could not be found
	 */
	@Override
	public AssetListEntryAssetEntryRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator)
		throws NoSuchEntryAssetEntryRelException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel, or <code>null</code> if a matching asset list entry asset entry rel could not be found
	 */
	@Override
	public AssetListEntryAssetEntryRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the asset list entry asset entry rels where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of asset list entry asset entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset list entry asset entry rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<AssetListEntryAssetEntryRel, NoSuchEntryAssetEntryRelException>
			_collectionPersistenceFinderByAssetListEntryId;

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where assetListEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	@Override
	public List<AssetListEntryAssetEntryRel> findByAssetListEntryId(
		long assetListEntryId, int start, int end,
		OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAssetListEntryId.find(
			finderCache, new Object[] {assetListEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel
	 * @throws NoSuchEntryAssetEntryRelException if a matching asset list entry asset entry rel could not be found
	 */
	@Override
	public AssetListEntryAssetEntryRel findByAssetListEntryId_First(
			long assetListEntryId,
			OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator)
		throws NoSuchEntryAssetEntryRelException {

		return _collectionPersistenceFinderByAssetListEntryId.findFirst(
			finderCache, new Object[] {assetListEntryId}, orderByComparator);
	}

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel, or <code>null</code> if a matching asset list entry asset entry rel could not be found
	 */
	@Override
	public AssetListEntryAssetEntryRel fetchByAssetListEntryId_First(
		long assetListEntryId,
		OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByAssetListEntryId.fetchFirst(
			finderCache, new Object[] {assetListEntryId}, orderByComparator);
	}

	/**
	 * Removes all the asset list entry asset entry rels where assetListEntryId = &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 */
	@Override
	public void removeByAssetListEntryId(long assetListEntryId) {
		_collectionPersistenceFinderByAssetListEntryId.remove(
			finderCache, new Object[] {assetListEntryId});
	}

	/**
	 * Returns the number of asset list entry asset entry rels where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @return the number of matching asset list entry asset entry rels
	 */
	@Override
	public int countByAssetListEntryId(long assetListEntryId) {
		return _collectionPersistenceFinderByAssetListEntryId.count(
			finderCache, new Object[] {assetListEntryId});
	}

	private CollectionPersistenceFinder
		<AssetListEntryAssetEntryRel, NoSuchEntryAssetEntryRelException>
			_collectionPersistenceFinderByAssetEntryId;

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where assetEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetEntryId the asset entry ID
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	@Override
	public List<AssetListEntryAssetEntryRel> findByAssetEntryId(
		long assetEntryId, int start, int end,
		OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAssetEntryId.find(
			finderCache, new Object[] {assetEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where assetEntryId = &#63;.
	 *
	 * @param assetEntryId the asset entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel
	 * @throws NoSuchEntryAssetEntryRelException if a matching asset list entry asset entry rel could not be found
	 */
	@Override
	public AssetListEntryAssetEntryRel findByAssetEntryId_First(
			long assetEntryId,
			OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator)
		throws NoSuchEntryAssetEntryRelException {

		return _collectionPersistenceFinderByAssetEntryId.findFirst(
			finderCache, new Object[] {assetEntryId}, orderByComparator);
	}

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where assetEntryId = &#63;.
	 *
	 * @param assetEntryId the asset entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel, or <code>null</code> if a matching asset list entry asset entry rel could not be found
	 */
	@Override
	public AssetListEntryAssetEntryRel fetchByAssetEntryId_First(
		long assetEntryId,
		OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByAssetEntryId.fetchFirst(
			finderCache, new Object[] {assetEntryId}, orderByComparator);
	}

	/**
	 * Removes all the asset list entry asset entry rels where assetEntryId = &#63; from the database.
	 *
	 * @param assetEntryId the asset entry ID
	 */
	@Override
	public void removeByAssetEntryId(long assetEntryId) {
		_collectionPersistenceFinderByAssetEntryId.remove(
			finderCache, new Object[] {assetEntryId});
	}

	/**
	 * Returns the number of asset list entry asset entry rels where assetEntryId = &#63;.
	 *
	 * @param assetEntryId the asset entry ID
	 * @return the number of matching asset list entry asset entry rels
	 */
	@Override
	public int countByAssetEntryId(long assetEntryId) {
		return _collectionPersistenceFinderByAssetEntryId.count(
			finderCache, new Object[] {assetEntryId});
	}

	private CollectionPersistenceFinder
		<AssetListEntryAssetEntryRel, NoSuchEntryAssetEntryRelException>
			_collectionPersistenceFinderByA_S;

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where assetListEntryId = &#63; and segmentsEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	@Override
	public List<AssetListEntryAssetEntryRel> findByA_S(
		long assetListEntryId, long segmentsEntryId, int start, int end,
		OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByA_S.find(
			finderCache,
			new Object[] {assetListEntryId, new long[] {segmentsEntryId}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where assetListEntryId = &#63; and segmentsEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel
	 * @throws NoSuchEntryAssetEntryRelException if a matching asset list entry asset entry rel could not be found
	 */
	@Override
	public AssetListEntryAssetEntryRel findByA_S_First(
			long assetListEntryId, long segmentsEntryId,
			OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator)
		throws NoSuchEntryAssetEntryRelException {

		return _collectionPersistenceFinderByA_S.findFirst(
			finderCache,
			new Object[] {assetListEntryId, new long[] {segmentsEntryId}},
			orderByComparator);
	}

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where assetListEntryId = &#63; and segmentsEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel, or <code>null</code> if a matching asset list entry asset entry rel could not be found
	 */
	@Override
	public AssetListEntryAssetEntryRel fetchByA_S_First(
		long assetListEntryId, long segmentsEntryId,
		OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByA_S.fetchFirst(
			finderCache,
			new Object[] {assetListEntryId, new long[] {segmentsEntryId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where assetListEntryId = &#63; and segmentsEntryId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryIds the segments entry IDs
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	@Override
	public List<AssetListEntryAssetEntryRel> findByA_S(
		long assetListEntryId, long[] segmentsEntryIds, int start, int end,
		OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByA_S.find(
			finderCache,
			new Object[] {
				assetListEntryId, ArrayUtil.sortedUnique(segmentsEntryIds)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset list entry asset entry rels where assetListEntryId = &#63; and segmentsEntryId = &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 */
	@Override
	public void removeByA_S(long assetListEntryId, long segmentsEntryId) {
		_collectionPersistenceFinderByA_S.remove(
			finderCache,
			new Object[] {assetListEntryId, new long[] {segmentsEntryId}});
	}

	/**
	 * Returns the number of asset list entry asset entry rels where assetListEntryId = &#63; and segmentsEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @return the number of matching asset list entry asset entry rels
	 */
	@Override
	public int countByA_S(long assetListEntryId, long segmentsEntryId) {
		return _collectionPersistenceFinderByA_S.count(
			finderCache,
			new Object[] {assetListEntryId, new long[] {segmentsEntryId}});
	}

	/**
	 * Returns the number of asset list entry asset entry rels where assetListEntryId = &#63; and segmentsEntryId = any &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryIds the segments entry IDs
	 * @return the number of matching asset list entry asset entry rels
	 */
	@Override
	public int countByA_S(long assetListEntryId, long[] segmentsEntryIds) {
		return _collectionPersistenceFinderByA_S.count(
			finderCache,
			new Object[] {
				assetListEntryId, ArrayUtil.sortedUnique(segmentsEntryIds)
			});
	}

	private UniquePersistenceFinder
		<AssetListEntryAssetEntryRel, NoSuchEntryAssetEntryRelException>
			_uniquePersistenceFinderByA_S_P;

	/**
	 * Returns the asset list entry asset entry rel where assetListEntryId = &#63; and segmentsEntryId = &#63; and position = &#63; or throws a <code>NoSuchEntryAssetEntryRelException</code> if it could not be found.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @return the matching asset list entry asset entry rel
	 * @throws NoSuchEntryAssetEntryRelException if a matching asset list entry asset entry rel could not be found
	 */
	@Override
	public AssetListEntryAssetEntryRel findByA_S_P(
			long assetListEntryId, long segmentsEntryId, int position)
		throws NoSuchEntryAssetEntryRelException {

		return _uniquePersistenceFinderByA_S_P.find(
			finderCache,
			new Object[] {assetListEntryId, segmentsEntryId, position});
	}

	/**
	 * Returns the asset list entry asset entry rel where assetListEntryId = &#63; and segmentsEntryId = &#63; and position = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry asset entry rel, or <code>null</code> if a matching asset list entry asset entry rel could not be found
	 */
	@Override
	public AssetListEntryAssetEntryRel fetchByA_S_P(
		long assetListEntryId, long segmentsEntryId, int position,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByA_S_P.fetch(
			finderCache,
			new Object[] {assetListEntryId, segmentsEntryId, position},
			useFinderCache);
	}

	/**
	 * Removes the asset list entry asset entry rel where assetListEntryId = &#63; and segmentsEntryId = &#63; and position = &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @return the asset list entry asset entry rel that was removed
	 */
	@Override
	public AssetListEntryAssetEntryRel removeByA_S_P(
			long assetListEntryId, long segmentsEntryId, int position)
		throws NoSuchEntryAssetEntryRelException {

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel = findByA_S_P(
			assetListEntryId, segmentsEntryId, position);

		return remove(assetListEntryAssetEntryRel);
	}

	/**
	 * Returns the number of asset list entry asset entry rels where assetListEntryId = &#63; and segmentsEntryId = &#63; and position = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @return the number of matching asset list entry asset entry rels
	 */
	@Override
	public int countByA_S_P(
		long assetListEntryId, long segmentsEntryId, int position) {

		return _uniquePersistenceFinderByA_S_P.count(
			finderCache,
			new Object[] {assetListEntryId, segmentsEntryId, position});
	}

	private CollectionPersistenceFinder
		<AssetListEntryAssetEntryRel, NoSuchEntryAssetEntryRelException>
			_collectionPersistenceFinderByA_S_GtP;

	/**
	 * Returns all the asset list entry asset entry rels where assetListEntryId = &#63; and segmentsEntryId = &#63; and position &gt; &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @return the matching asset list entry asset entry rels
	 */
	@Override
	public List<AssetListEntryAssetEntryRel> findByA_S_GtP(
		long assetListEntryId, long segmentsEntryId, int position) {

		return findByA_S_GtP(
			assetListEntryId, segmentsEntryId, position, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset list entry asset entry rels where assetListEntryId = &#63; and segmentsEntryId = &#63; and position &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @return the range of matching asset list entry asset entry rels
	 */
	@Override
	public List<AssetListEntryAssetEntryRel> findByA_S_GtP(
		long assetListEntryId, long segmentsEntryId, int position, int start,
		int end) {

		return findByA_S_GtP(
			assetListEntryId, segmentsEntryId, position, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where assetListEntryId = &#63; and segmentsEntryId = &#63; and position &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	@Override
	public List<AssetListEntryAssetEntryRel> findByA_S_GtP(
		long assetListEntryId, long segmentsEntryId, int position, int start,
		int end,
		OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator) {

		return findByA_S_GtP(
			assetListEntryId, segmentsEntryId, position, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where assetListEntryId = &#63; and segmentsEntryId = &#63; and position &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	@Override
	public List<AssetListEntryAssetEntryRel> findByA_S_GtP(
		long assetListEntryId, long segmentsEntryId, int position, int start,
		int end,
		OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByA_S_GtP.find(
			finderCache,
			new Object[] {assetListEntryId, segmentsEntryId, position}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where assetListEntryId = &#63; and segmentsEntryId = &#63; and position &gt; &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel
	 * @throws NoSuchEntryAssetEntryRelException if a matching asset list entry asset entry rel could not be found
	 */
	@Override
	public AssetListEntryAssetEntryRel findByA_S_GtP_First(
			long assetListEntryId, long segmentsEntryId, int position,
			OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator)
		throws NoSuchEntryAssetEntryRelException {

		return _collectionPersistenceFinderByA_S_GtP.findFirst(
			finderCache,
			new Object[] {assetListEntryId, segmentsEntryId, position},
			orderByComparator);
	}

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where assetListEntryId = &#63; and segmentsEntryId = &#63; and position &gt; &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel, or <code>null</code> if a matching asset list entry asset entry rel could not be found
	 */
	@Override
	public AssetListEntryAssetEntryRel fetchByA_S_GtP_First(
		long assetListEntryId, long segmentsEntryId, int position,
		OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByA_S_GtP.fetchFirst(
			finderCache,
			new Object[] {assetListEntryId, segmentsEntryId, position},
			orderByComparator);
	}

	/**
	 * Removes all the asset list entry asset entry rels where assetListEntryId = &#63; and segmentsEntryId = &#63; and position &gt; &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 */
	@Override
	public void removeByA_S_GtP(
		long assetListEntryId, long segmentsEntryId, int position) {

		_collectionPersistenceFinderByA_S_GtP.remove(
			finderCache,
			new Object[] {assetListEntryId, segmentsEntryId, position});
	}

	/**
	 * Returns the number of asset list entry asset entry rels where assetListEntryId = &#63; and segmentsEntryId = &#63; and position &gt; &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @return the number of matching asset list entry asset entry rels
	 */
	@Override
	public int countByA_S_GtP(
		long assetListEntryId, long segmentsEntryId, int position) {

		return _collectionPersistenceFinderByA_S_GtP.count(
			finderCache,
			new Object[] {assetListEntryId, segmentsEntryId, position});
	}

	public AssetListEntryAssetEntryRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(AssetListEntryAssetEntryRel.class);

		setModelImplClass(AssetListEntryAssetEntryRelImpl.class);
		setModelPKClass(long.class);

		setTable(AssetListEntryAssetEntryRelTable.INSTANCE);
	}

	/**
	 * Creates a new asset list entry asset entry rel with the primary key. Does not add the asset list entry asset entry rel to the database.
	 *
	 * @param assetListEntryAssetEntryRelId the primary key for the new asset list entry asset entry rel
	 * @return the new asset list entry asset entry rel
	 */
	@Override
	public AssetListEntryAssetEntryRel create(
		long assetListEntryAssetEntryRelId) {

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
			new AssetListEntryAssetEntryRelImpl();

		assetListEntryAssetEntryRel.setNew(true);
		assetListEntryAssetEntryRel.setPrimaryKey(
			assetListEntryAssetEntryRelId);

		String uuid = PortalUUIDUtil.generate();

		assetListEntryAssetEntryRel.setUuid(uuid);

		assetListEntryAssetEntryRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return assetListEntryAssetEntryRel;
	}

	/**
	 * Removes the asset list entry asset entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param assetListEntryAssetEntryRelId the primary key of the asset list entry asset entry rel
	 * @return the asset list entry asset entry rel that was removed
	 * @throws NoSuchEntryAssetEntryRelException if a asset list entry asset entry rel with the primary key could not be found
	 */
	@Override
	public AssetListEntryAssetEntryRel remove(
			long assetListEntryAssetEntryRelId)
		throws NoSuchEntryAssetEntryRelException {

		return remove((Serializable)assetListEntryAssetEntryRelId);
	}

	@Override
	protected AssetListEntryAssetEntryRel removeImpl(
		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(assetListEntryAssetEntryRel)) {
				assetListEntryAssetEntryRel =
					(AssetListEntryAssetEntryRel)session.get(
						AssetListEntryAssetEntryRelImpl.class,
						assetListEntryAssetEntryRel.getPrimaryKeyObj());
			}

			if ((assetListEntryAssetEntryRel != null) &&
				ctPersistenceHelper.isRemove(assetListEntryAssetEntryRel)) {

				session.delete(assetListEntryAssetEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (assetListEntryAssetEntryRel != null) {
			clearCache(assetListEntryAssetEntryRel);
		}

		return assetListEntryAssetEntryRel;
	}

	@Override
	public AssetListEntryAssetEntryRel updateImpl(
		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel) {

		boolean isNew = assetListEntryAssetEntryRel.isNew();

		if (!(assetListEntryAssetEntryRel instanceof
				AssetListEntryAssetEntryRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					assetListEntryAssetEntryRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					assetListEntryAssetEntryRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in assetListEntryAssetEntryRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AssetListEntryAssetEntryRel implementation " +
					assetListEntryAssetEntryRel.getClass());
		}

		AssetListEntryAssetEntryRelModelImpl
			assetListEntryAssetEntryRelModelImpl =
				(AssetListEntryAssetEntryRelModelImpl)
					assetListEntryAssetEntryRel;

		if (Validator.isNull(assetListEntryAssetEntryRel.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			assetListEntryAssetEntryRel.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (assetListEntryAssetEntryRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				assetListEntryAssetEntryRel.setCreateDate(date);
			}
			else {
				assetListEntryAssetEntryRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!assetListEntryAssetEntryRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				assetListEntryAssetEntryRel.setModifiedDate(date);
			}
			else {
				assetListEntryAssetEntryRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(assetListEntryAssetEntryRel)) {
				if (!isNew) {
					session.evict(
						AssetListEntryAssetEntryRelImpl.class,
						assetListEntryAssetEntryRel.getPrimaryKeyObj());
				}

				session.save(assetListEntryAssetEntryRel);
			}
			else {
				assetListEntryAssetEntryRel =
					(AssetListEntryAssetEntryRel)session.merge(
						assetListEntryAssetEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(assetListEntryAssetEntryRel, false);

		if (isNew) {
			assetListEntryAssetEntryRel.setNew(false);
		}

		assetListEntryAssetEntryRel.resetOriginalValues();

		return assetListEntryAssetEntryRel;
	}

	/**
	 * Returns the asset list entry asset entry rel with the primary key or throws a <code>NoSuchEntryAssetEntryRelException</code> if it could not be found.
	 *
	 * @param assetListEntryAssetEntryRelId the primary key of the asset list entry asset entry rel
	 * @return the asset list entry asset entry rel
	 * @throws NoSuchEntryAssetEntryRelException if a asset list entry asset entry rel with the primary key could not be found
	 */
	@Override
	public AssetListEntryAssetEntryRel findByPrimaryKey(
			long assetListEntryAssetEntryRelId)
		throws NoSuchEntryAssetEntryRelException {

		return findByPrimaryKey((Serializable)assetListEntryAssetEntryRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the asset list entry asset entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param assetListEntryAssetEntryRelId the primary key of the asset list entry asset entry rel
	 * @return the asset list entry asset entry rel, or <code>null</code> if a asset list entry asset entry rel with the primary key could not be found
	 */
	@Override
	public AssetListEntryAssetEntryRel fetchByPrimaryKey(
		long assetListEntryAssetEntryRelId) {

		return fetchByPrimaryKey((Serializable)assetListEntryAssetEntryRelId);
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
		return "assetListEntryAssetEntryRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ASSETLISTENTRYASSETENTRYREL;
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
		return AssetListEntryAssetEntryRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AssetListEntryAssetEntryRel";
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
		ctMergeColumnNames.add("assetEntryId");
		ctMergeColumnNames.add("segmentsEntryId");
		ctMergeColumnNames.add("position");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("assetListEntryAssetEntryRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"assetListEntryId", "segmentsEntryId", "position"});
	}

	/**
	 * Initializes the asset list entry asset entry rel persistence.
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
			_SQL_SELECT_ASSETLISTENTRYASSETENTRYREL_WHERE,
			_SQL_COUNT_ASSETLISTENTRYASSETENTRYREL_WHERE,
			AssetListEntryAssetEntryRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"assetListEntryAssetEntryRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				AssetListEntryAssetEntryRel::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(AssetListEntryAssetEntryRel::getUuid),
				AssetListEntryAssetEntryRel::getGroupId),
			_SQL_SELECT_ASSETLISTENTRYASSETENTRYREL_WHERE, "",
			new FinderColumn<>(
				"assetListEntryAssetEntryRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				AssetListEntryAssetEntryRel::getUuid),
			new FinderColumn<>(
				"assetListEntryAssetEntryRel.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				AssetListEntryAssetEntryRel::getGroupId));

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
				_SQL_SELECT_ASSETLISTENTRYASSETENTRYREL_WHERE,
				_SQL_COUNT_ASSETLISTENTRYASSETENTRYREL_WHERE,
				AssetListEntryAssetEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"assetListEntryAssetEntryRel.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					AssetListEntryAssetEntryRel::getUuid),
				new FinderColumn<>(
					"assetListEntryAssetEntryRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetListEntryAssetEntryRel::getCompanyId));

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
				_SQL_SELECT_ASSETLISTENTRYASSETENTRYREL_WHERE,
				_SQL_COUNT_ASSETLISTENTRYASSETENTRYREL_WHERE,
				AssetListEntryAssetEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"assetListEntryAssetEntryRel.", "assetListEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetListEntryAssetEntryRel::getAssetListEntryId));

		_collectionPersistenceFinderByAssetEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByAssetEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"assetEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByAssetEntryId", new String[] {Long.class.getName()},
					new String[] {"assetEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByAssetEntryId", new String[] {Long.class.getName()},
					new String[] {"assetEntryId"}, false),
				_SQL_SELECT_ASSETLISTENTRYASSETENTRYREL_WHERE,
				_SQL_COUNT_ASSETLISTENTRYASSETENTRYREL_WHERE,
				AssetListEntryAssetEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"assetListEntryAssetEntryRel.", "assetEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetListEntryAssetEntryRel::getAssetEntryId));

		_collectionPersistenceFinderByA_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByA_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"assetListEntryId", "segmentsEntryId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByA_S",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"assetListEntryId", "segmentsEntryId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByA_S",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"assetListEntryId", "segmentsEntryId"}, false),
			_SQL_SELECT_ASSETLISTENTRYASSETENTRYREL_WHERE,
			_SQL_COUNT_ASSETLISTENTRYASSETENTRYREL_WHERE,
			AssetListEntryAssetEntryRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"assetListEntryAssetEntryRel.", "assetListEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				AssetListEntryAssetEntryRel::getAssetListEntryId),
			new ArrayableFinderColumn<>(
				"assetListEntryAssetEntryRel.", "segmentsEntryId",
				FinderColumn.Type.LONG, "=", false, true, true,
				AssetListEntryAssetEntryRel::getSegmentsEntryId));

		_uniquePersistenceFinderByA_S_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByA_S_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {
					"assetListEntryId", "segmentsEntryId", "position"
				},
				0, 0, false, AssetListEntryAssetEntryRel::getAssetListEntryId,
				AssetListEntryAssetEntryRel::getSegmentsEntryId,
				AssetListEntryAssetEntryRel::getPosition),
			_SQL_SELECT_ASSETLISTENTRYASSETENTRYREL_WHERE, "",
			new FinderColumn<>(
				"assetListEntryAssetEntryRel.", "assetListEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				AssetListEntryAssetEntryRel::getAssetListEntryId),
			new FinderColumn<>(
				"assetListEntryAssetEntryRel.", "segmentsEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				AssetListEntryAssetEntryRel::getSegmentsEntryId),
			new FinderColumn<>(
				"assetListEntryAssetEntryRel.", "position",
				FinderColumn.Type.INTEGER, "=", true, true,
				AssetListEntryAssetEntryRel::getPosition));

		_collectionPersistenceFinderByA_S_GtP =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByA_S_GtP",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"assetListEntryId", "segmentsEntryId", "position"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByA_S_GtP",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"assetListEntryId", "segmentsEntryId", "position"
					},
					false),
				_SQL_SELECT_ASSETLISTENTRYASSETENTRYREL_WHERE,
				_SQL_COUNT_ASSETLISTENTRYASSETENTRYREL_WHERE,
				AssetListEntryAssetEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"assetListEntryAssetEntryRel.", "assetListEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetListEntryAssetEntryRel::getAssetListEntryId),
				new FinderColumn<>(
					"assetListEntryAssetEntryRel.", "segmentsEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetListEntryAssetEntryRel::getSegmentsEntryId),
				new FinderColumn<>(
					"assetListEntryAssetEntryRel.", "position",
					FinderColumn.Type.INTEGER, ">", true, true,
					AssetListEntryAssetEntryRel::getPosition));

		AssetListEntryAssetEntryRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AssetListEntryAssetEntryRelUtil.setPersistence(null);

		entityCache.removeCache(
			AssetListEntryAssetEntryRelImpl.class.getName());
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
		AssetListEntryAssetEntryRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ASSETLISTENTRYASSETENTRYREL =
		"SELECT assetListEntryAssetEntryRel FROM AssetListEntryAssetEntryRel assetListEntryAssetEntryRel";

	private static final String _SQL_SELECT_ASSETLISTENTRYASSETENTRYREL_WHERE =
		"SELECT assetListEntryAssetEntryRel FROM AssetListEntryAssetEntryRel assetListEntryAssetEntryRel WHERE ";

	private static final String _SQL_COUNT_ASSETLISTENTRYASSETENTRYREL_WHERE =
		"SELECT COUNT(assetListEntryAssetEntryRel) FROM AssetListEntryAssetEntryRel assetListEntryAssetEntryRel WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1099250218