/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.persistence.impl;

import com.liferay.asset.list.exception.NoSuchEntryAudienceEntryRelException;
import com.liferay.asset.list.model.AssetListEntryAudienceEntryRel;
import com.liferay.asset.list.model.AssetListEntryAudienceEntryRelTable;
import com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelImpl;
import com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl;
import com.liferay.asset.list.service.persistence.AssetListEntryAudienceEntryRelPersistence;
import com.liferay.asset.list.service.persistence.AssetListEntryAudienceEntryRelUtil;
import com.liferay.asset.list.service.persistence.impl.constants.AssetListPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
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
 * The persistence implementation for the asset list entry audience entry rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AssetListEntryAudienceEntryRelPersistence.class)
public class AssetListEntryAudienceEntryRelPersistenceImpl
	extends BasePersistenceImpl
		<AssetListEntryAudienceEntryRel, NoSuchEntryAudienceEntryRelException>
	implements AssetListEntryAudienceEntryRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AssetListEntryAudienceEntryRelUtil</code> to access the asset list entry audience entry rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AssetListEntryAudienceEntryRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AssetListEntryAudienceEntryRel, NoSuchEntryAudienceEntryRelException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	@Override
	public List<AssetListEntryAudienceEntryRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel findByUuid_First(
			String uuid,
			OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator)
		throws NoSuchEntryAudienceEntryRelException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel fetchByUuid_First(
		String uuid,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the asset list entry audience entry rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of asset list entry audience entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset list entry audience entry rels
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<AssetListEntryAudienceEntryRel, NoSuchEntryAudienceEntryRelException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the asset list entry audience entry rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel findByUUID_G(
			String uuid, long groupId)
		throws NoSuchEntryAudienceEntryRelException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the asset list entry audience entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the asset list entry audience entry rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset list entry audience entry rel that was removed
	 */
	@Override
	public AssetListEntryAudienceEntryRel removeByUUID_G(
			String uuid, long groupId)
		throws NoSuchEntryAudienceEntryRelException {

		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel =
			findByUUID_G(uuid, groupId);

		return remove(assetListEntryAudienceEntryRel);
	}

	/**
	 * Returns the number of asset list entry audience entry rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset list entry audience entry rels
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<AssetListEntryAudienceEntryRel, NoSuchEntryAudienceEntryRelException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	@Override
	public List<AssetListEntryAudienceEntryRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator)
		throws NoSuchEntryAudienceEntryRelException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the asset list entry audience entry rels where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of asset list entry audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset list entry audience entry rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<AssetListEntryAudienceEntryRel, NoSuchEntryAudienceEntryRelException>
			_collectionPersistenceFinderByAssetListEntryId;

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where assetListEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	@Override
	public List<AssetListEntryAudienceEntryRel> findByAssetListEntryId(
		long assetListEntryId, int start, int end,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAssetListEntryId.find(
			finderCache, new Object[] {assetListEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel findByAssetListEntryId_First(
			long assetListEntryId,
			OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator)
		throws NoSuchEntryAudienceEntryRelException {

		return _collectionPersistenceFinderByAssetListEntryId.findFirst(
			finderCache, new Object[] {assetListEntryId}, orderByComparator);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel fetchByAssetListEntryId_First(
		long assetListEntryId,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByAssetListEntryId.fetchFirst(
			finderCache, new Object[] {assetListEntryId}, orderByComparator);
	}

	/**
	 * Removes all the asset list entry audience entry rels where assetListEntryId = &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 */
	@Override
	public void removeByAssetListEntryId(long assetListEntryId) {
		_collectionPersistenceFinderByAssetListEntryId.remove(
			finderCache, new Object[] {assetListEntryId});
	}

	/**
	 * Returns the number of asset list entry audience entry rels where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @return the number of matching asset list entry audience entry rels
	 */
	@Override
	public int countByAssetListEntryId(long assetListEntryId) {
		return _collectionPersistenceFinderByAssetListEntryId.count(
			finderCache, new Object[] {assetListEntryId});
	}

	private CollectionPersistenceFinder
		<AssetListEntryAudienceEntryRel, NoSuchEntryAudienceEntryRelException>
			_collectionPersistenceFinderByC_AEERC;

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	@Override
	public List<AssetListEntryAudienceEntryRel> findByC_AEERC(
		long companyId, String audienceEntryERC, int start, int end,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_AEERC.find(
			finderCache, new Object[] {companyId, audienceEntryERC}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel findByC_AEERC_First(
			long companyId, String audienceEntryERC,
			OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator)
		throws NoSuchEntryAudienceEntryRelException {

		return _collectionPersistenceFinderByC_AEERC.findFirst(
			finderCache, new Object[] {companyId, audienceEntryERC},
			orderByComparator);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel fetchByC_AEERC_First(
		long companyId, String audienceEntryERC,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByC_AEERC.fetchFirst(
			finderCache, new Object[] {companyId, audienceEntryERC},
			orderByComparator);
	}

	/**
	 * Removes all the asset list entry audience entry rels where companyId = &#63; and audienceEntryERC = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 */
	@Override
	public void removeByC_AEERC(long companyId, String audienceEntryERC) {
		_collectionPersistenceFinderByC_AEERC.remove(
			finderCache, new Object[] {companyId, audienceEntryERC});
	}

	/**
	 * Returns the number of asset list entry audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the number of matching asset list entry audience entry rels
	 */
	@Override
	public int countByC_AEERC(long companyId, String audienceEntryERC) {
		return _collectionPersistenceFinderByC_AEERC.count(
			finderCache, new Object[] {companyId, audienceEntryERC});
	}

	private UniquePersistenceFinder
		<AssetListEntryAudienceEntryRel, NoSuchEntryAudienceEntryRelException>
			_uniquePersistenceFinderByA_AEERC;

	/**
	 * Returns the asset list entry audience entry rel where assetListEntryId = &#63; and audienceEntryERC = &#63; or throws a <code>NoSuchEntryAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel findByA_AEERC(
			long assetListEntryId, String audienceEntryERC)
		throws NoSuchEntryAudienceEntryRelException {

		return _uniquePersistenceFinderByA_AEERC.find(
			finderCache, new Object[] {assetListEntryId, audienceEntryERC});
	}

	/**
	 * Returns the asset list entry audience entry rel where assetListEntryId = &#63; and audienceEntryERC = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel fetchByA_AEERC(
		long assetListEntryId, String audienceEntryERC,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByA_AEERC.fetch(
			finderCache, new Object[] {assetListEntryId, audienceEntryERC},
			useFinderCache);
	}

	/**
	 * Removes the asset list entry audience entry rel where assetListEntryId = &#63; and audienceEntryERC = &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the asset list entry audience entry rel that was removed
	 */
	@Override
	public AssetListEntryAudienceEntryRel removeByA_AEERC(
			long assetListEntryId, String audienceEntryERC)
		throws NoSuchEntryAudienceEntryRelException {

		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel =
			findByA_AEERC(assetListEntryId, audienceEntryERC);

		return remove(assetListEntryAudienceEntryRel);
	}

	/**
	 * Returns the number of asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the number of matching asset list entry audience entry rels
	 */
	@Override
	public int countByA_AEERC(long assetListEntryId, String audienceEntryERC) {
		return _uniquePersistenceFinderByA_AEERC.count(
			finderCache, new Object[] {assetListEntryId, audienceEntryERC});
	}

	private CollectionPersistenceFinder
		<AssetListEntryAudienceEntryRel, NoSuchEntryAudienceEntryRelException>
			_collectionPersistenceFinderByA_AEERC_C;

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	@Override
	public List<AssetListEntryAudienceEntryRel> findByA_AEERC_C(
		long assetListEntryId, String audienceEntryERC, int start, int end,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByA_AEERC_C.find(
			finderCache,
			new Object[] {assetListEntryId, new String[] {audienceEntryERC}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel findByA_AEERC_C_First(
			long assetListEntryId, String audienceEntryERC,
			OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator)
		throws NoSuchEntryAudienceEntryRelException {

		return _collectionPersistenceFinderByA_AEERC_C.findFirst(
			finderCache,
			new Object[] {assetListEntryId, new String[] {audienceEntryERC}},
			orderByComparator);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel fetchByA_AEERC_C_First(
		long assetListEntryId, String audienceEntryERC,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByA_AEERC_C.fetchFirst(
			finderCache,
			new Object[] {assetListEntryId, new String[] {audienceEntryERC}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERCs the audience entry ercs
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	@Override
	public List<AssetListEntryAudienceEntryRel> findByA_AEERC_C(
		long assetListEntryId, String[] audienceEntryERCs, int start, int end,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByA_AEERC_C.find(
			finderCache,
			new Object[] {
				assetListEntryId, ArrayUtil.sortedUnique(audienceEntryERCs)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 */
	@Override
	public void removeByA_AEERC_C(
		long assetListEntryId, String audienceEntryERC) {

		_collectionPersistenceFinderByA_AEERC_C.remove(
			finderCache,
			new Object[] {assetListEntryId, new String[] {audienceEntryERC}});
	}

	/**
	 * Returns the number of asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the number of matching asset list entry audience entry rels
	 */
	@Override
	public int countByA_AEERC_C(
		long assetListEntryId, String audienceEntryERC) {

		return _collectionPersistenceFinderByA_AEERC_C.count(
			finderCache,
			new Object[] {assetListEntryId, new String[] {audienceEntryERC}});
	}

	/**
	 * Returns the number of asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = any &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERCs the audience entry ercs
	 * @return the number of matching asset list entry audience entry rels
	 */
	@Override
	public int countByA_AEERC_C(
		long assetListEntryId, String[] audienceEntryERCs) {

		return _collectionPersistenceFinderByA_AEERC_C.count(
			finderCache,
			new Object[] {
				assetListEntryId, ArrayUtil.sortedUnique(audienceEntryERCs)
			});
	}

	public AssetListEntryAudienceEntryRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"assetListEntryAudienceEntryRelId", "alEntryAudienceEntryRelId");

		setDBColumnNames(dbColumnNames);

		setModelClass(AssetListEntryAudienceEntryRel.class);

		setModelImplClass(AssetListEntryAudienceEntryRelImpl.class);
		setModelPKClass(long.class);

		setTable(AssetListEntryAudienceEntryRelTable.INSTANCE);
	}

	/**
	 * Creates a new asset list entry audience entry rel with the primary key. Does not add the asset list entry audience entry rel to the database.
	 *
	 * @param assetListEntryAudienceEntryRelId the primary key for the new asset list entry audience entry rel
	 * @return the new asset list entry audience entry rel
	 */
	@Override
	public AssetListEntryAudienceEntryRel create(
		long assetListEntryAudienceEntryRelId) {

		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel =
			new AssetListEntryAudienceEntryRelImpl();

		assetListEntryAudienceEntryRel.setNew(true);
		assetListEntryAudienceEntryRel.setPrimaryKey(
			assetListEntryAudienceEntryRelId);

		String uuid = PortalUUIDUtil.generate();

		assetListEntryAudienceEntryRel.setUuid(uuid);

		assetListEntryAudienceEntryRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return assetListEntryAudienceEntryRel;
	}

	/**
	 * Removes the asset list entry audience entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param assetListEntryAudienceEntryRelId the primary key of the asset list entry audience entry rel
	 * @return the asset list entry audience entry rel that was removed
	 * @throws NoSuchEntryAudienceEntryRelException if a asset list entry audience entry rel with the primary key could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel remove(
			long assetListEntryAudienceEntryRelId)
		throws NoSuchEntryAudienceEntryRelException {

		return remove((Serializable)assetListEntryAudienceEntryRelId);
	}

	@Override
	protected AssetListEntryAudienceEntryRel removeImpl(
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(assetListEntryAudienceEntryRel)) {
				assetListEntryAudienceEntryRel =
					(AssetListEntryAudienceEntryRel)session.get(
						AssetListEntryAudienceEntryRelImpl.class,
						assetListEntryAudienceEntryRel.getPrimaryKeyObj());
			}

			if ((assetListEntryAudienceEntryRel != null) &&
				ctPersistenceHelper.isRemove(assetListEntryAudienceEntryRel)) {

				session.delete(assetListEntryAudienceEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (assetListEntryAudienceEntryRel != null) {
			clearCache(assetListEntryAudienceEntryRel);
		}

		return assetListEntryAudienceEntryRel;
	}

	@Override
	public AssetListEntryAudienceEntryRel updateImpl(
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel) {

		boolean isNew = assetListEntryAudienceEntryRel.isNew();

		if (!(assetListEntryAudienceEntryRel instanceof
				AssetListEntryAudienceEntryRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					assetListEntryAudienceEntryRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					assetListEntryAudienceEntryRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in assetListEntryAudienceEntryRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AssetListEntryAudienceEntryRel implementation " +
					assetListEntryAudienceEntryRel.getClass());
		}

		AssetListEntryAudienceEntryRelModelImpl
			assetListEntryAudienceEntryRelModelImpl =
				(AssetListEntryAudienceEntryRelModelImpl)
					assetListEntryAudienceEntryRel;

		if (Validator.isNull(assetListEntryAudienceEntryRel.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			assetListEntryAudienceEntryRel.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (assetListEntryAudienceEntryRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				assetListEntryAudienceEntryRel.setCreateDate(date);
			}
			else {
				assetListEntryAudienceEntryRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!assetListEntryAudienceEntryRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				assetListEntryAudienceEntryRel.setModifiedDate(date);
			}
			else {
				assetListEntryAudienceEntryRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(assetListEntryAudienceEntryRel)) {
				if (!isNew) {
					session.evict(
						AssetListEntryAudienceEntryRelImpl.class,
						assetListEntryAudienceEntryRel.getPrimaryKeyObj());
				}

				session.save(assetListEntryAudienceEntryRel);
			}
			else {
				assetListEntryAudienceEntryRel =
					(AssetListEntryAudienceEntryRel)session.merge(
						assetListEntryAudienceEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(assetListEntryAudienceEntryRel, false);

		if (isNew) {
			assetListEntryAudienceEntryRel.setNew(false);
		}

		assetListEntryAudienceEntryRel.resetOriginalValues();

		return assetListEntryAudienceEntryRel;
	}

	/**
	 * Returns the asset list entry audience entry rel with the primary key or throws a <code>NoSuchEntryAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param assetListEntryAudienceEntryRelId the primary key of the asset list entry audience entry rel
	 * @return the asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a asset list entry audience entry rel with the primary key could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel findByPrimaryKey(
			long assetListEntryAudienceEntryRelId)
		throws NoSuchEntryAudienceEntryRelException {

		return findByPrimaryKey((Serializable)assetListEntryAudienceEntryRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the asset list entry audience entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param assetListEntryAudienceEntryRelId the primary key of the asset list entry audience entry rel
	 * @return the asset list entry audience entry rel, or <code>null</code> if a asset list entry audience entry rel with the primary key could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel fetchByPrimaryKey(
		long assetListEntryAudienceEntryRelId) {

		return fetchByPrimaryKey(
			(Serializable)assetListEntryAudienceEntryRelId);
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
		return "alEntryAudienceEntryRelId";
	}

	@Override
	protected String getPKFieldName() {
		return "assetListEntryAudienceEntryRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ASSETLISTENTRYAUDIENCEENTRYREL;
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
		return AssetListEntryAudienceEntryRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AssetListEntryAudienceEntryRel";
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
		ctMergeColumnNames.add("audienceEntryERC");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("typeSettings");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("alEntryAudienceEntryRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"assetListEntryId", "audienceEntryERC"});
	}

	/**
	 * Initializes the asset list entry audience entry rel persistence.
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
			_SQL_SELECT_ASSETLISTENTRYAUDIENCEENTRYREL_WHERE,
			_SQL_COUNT_ASSETLISTENTRYAUDIENCEENTRYREL_WHERE,
			AssetListEntryAudienceEntryRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"assetListEntryAudienceEntryRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				AssetListEntryAudienceEntryRel::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(AssetListEntryAudienceEntryRel::getUuid),
				AssetListEntryAudienceEntryRel::getGroupId),
			_SQL_SELECT_ASSETLISTENTRYAUDIENCEENTRYREL_WHERE, "",
			new FinderColumn<>(
				"assetListEntryAudienceEntryRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				AssetListEntryAudienceEntryRel::getUuid),
			new FinderColumn<>(
				"assetListEntryAudienceEntryRel.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				AssetListEntryAudienceEntryRel::getGroupId));

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
				_SQL_SELECT_ASSETLISTENTRYAUDIENCEENTRYREL_WHERE,
				_SQL_COUNT_ASSETLISTENTRYAUDIENCEENTRYREL_WHERE,
				AssetListEntryAudienceEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"assetListEntryAudienceEntryRel.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					AssetListEntryAudienceEntryRel::getUuid),
				new FinderColumn<>(
					"assetListEntryAudienceEntryRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetListEntryAudienceEntryRel::getCompanyId));

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
				_SQL_SELECT_ASSETLISTENTRYAUDIENCEENTRYREL_WHERE,
				_SQL_COUNT_ASSETLISTENTRYAUDIENCEENTRYREL_WHERE,
				AssetListEntryAudienceEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"assetListEntryAudienceEntryRel.", "assetListEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetListEntryAudienceEntryRel::getAssetListEntryId));

		_collectionPersistenceFinderByC_AEERC =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_AEERC",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "audienceEntryERC"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_AEERC",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "audienceEntryERC"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_AEERC",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "audienceEntryERC"}, 0, 2, false,
					null),
				_SQL_SELECT_ASSETLISTENTRYAUDIENCEENTRYREL_WHERE,
				_SQL_COUNT_ASSETLISTENTRYAUDIENCEENTRYREL_WHERE,
				AssetListEntryAudienceEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"assetListEntryAudienceEntryRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetListEntryAudienceEntryRel::getCompanyId),
				new FinderColumn<>(
					"assetListEntryAudienceEntryRel.", "audienceEntryERC",
					FinderColumn.Type.STRING, "=", true, true,
					AssetListEntryAudienceEntryRel::getAudienceEntryERC));

		_uniquePersistenceFinderByA_AEERC = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByA_AEERC",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"assetListEntryId", "audienceEntryERC"}, 0, 2,
				false, AssetListEntryAudienceEntryRel::getAssetListEntryId,
				convertNullFunction(
					AssetListEntryAudienceEntryRel::getAudienceEntryERC)),
			_SQL_SELECT_ASSETLISTENTRYAUDIENCEENTRYREL_WHERE, "",
			new FinderColumn<>(
				"assetListEntryAudienceEntryRel.", "assetListEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				AssetListEntryAudienceEntryRel::getAssetListEntryId),
			new FinderColumn<>(
				"assetListEntryAudienceEntryRel.", "audienceEntryERC",
				FinderColumn.Type.STRING, "=", true, true,
				AssetListEntryAudienceEntryRel::getAudienceEntryERC));

		_collectionPersistenceFinderByA_AEERC_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByA_AEERC_C",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"assetListEntryId", "audienceEntryERC"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByA_AEERC_C",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"assetListEntryId", "audienceEntryERC"}, 0, 2,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByA_AEERC_C",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"assetListEntryId", "audienceEntryERC"}, 0, 2,
					false, null),
				_SQL_SELECT_ASSETLISTENTRYAUDIENCEENTRYREL_WHERE,
				_SQL_COUNT_ASSETLISTENTRYAUDIENCEENTRYREL_WHERE,
				AssetListEntryAudienceEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"assetListEntryAudienceEntryRel.", "assetListEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetListEntryAudienceEntryRel::getAssetListEntryId),
				new ArrayableFinderColumn<>(
					"assetListEntryAudienceEntryRel.", "audienceEntryERC",
					FinderColumn.Type.STRING, "=", false, true, true,
					AssetListEntryAudienceEntryRel::getAudienceEntryERC));

		AssetListEntryAudienceEntryRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AssetListEntryAudienceEntryRelUtil.setPersistence(null);

		entityCache.removeCache(
			AssetListEntryAudienceEntryRelImpl.class.getName());
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
		AssetListEntryAudienceEntryRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ASSETLISTENTRYAUDIENCEENTRYREL =
		"SELECT assetListEntryAudienceEntryRel FROM AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel";

	private static final String
		_SQL_SELECT_ASSETLISTENTRYAUDIENCEENTRYREL_WHERE =
			"SELECT assetListEntryAudienceEntryRel FROM AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel WHERE ";

	private static final String
		_SQL_COUNT_ASSETLISTENTRYAUDIENCEENTRYREL_WHERE =
			"SELECT COUNT(assetListEntryAudienceEntryRel) FROM AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "assetListEntryAudienceEntryRelId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1066879990