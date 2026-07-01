/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.persistence.impl;

import com.liferay.asset.list.exception.DuplicateAssetListEntryExternalReferenceCodeException;
import com.liferay.asset.list.exception.NoSuchEntryException;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryTable;
import com.liferay.asset.list.model.impl.AssetListEntryImpl;
import com.liferay.asset.list.model.impl.AssetListEntryModelImpl;
import com.liferay.asset.list.service.persistence.AssetListEntryPersistence;
import com.liferay.asset.list.service.persistence.AssetListEntryUtil;
import com.liferay.asset.list.service.persistence.impl.constants.AssetListPersistenceConstants;
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
 * The persistence implementation for the asset list entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AssetListEntryPersistence.class)
public class AssetListEntryPersistenceImpl
	extends BasePersistenceImpl<AssetListEntry, NoSuchEntryException>
	implements AssetListEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AssetListEntryUtil</code> to access the asset list entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AssetListEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<AssetListEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the asset list entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset list entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry findByUuid_First(
			String uuid, OrderByComparator<AssetListEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first asset list entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry fetchByUuid_First(
		String uuid, OrderByComparator<AssetListEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the asset list entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of asset list entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset list entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<AssetListEntry, NoSuchEntryException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the asset list entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the asset list entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the asset list entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset list entry that was removed
	 */
	@Override
	public AssetListEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException {

		AssetListEntry assetListEntry = findByUUID_G(uuid, groupId);

		return remove(assetListEntry);
	}

	/**
	 * Returns the number of asset list entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset list entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<AssetListEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the asset list entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<AssetListEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first asset list entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the asset list entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of asset list entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset list entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<AssetListEntry, NoSuchEntryException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {new long[] {groupId}}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry findByGroupId_First(
			long groupId, OrderByComparator<AssetListEntry> orderByComparator)
		throws NoSuchEntryException {

		AssetListEntry assetListEntry = fetchByGroupId_First(
			groupId, orderByComparator);

		if (assetListEntry != null) {
			return assetListEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry fetchByGroupId_First(
		long groupId, OrderByComparator<AssetListEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {new long[] {groupId}}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupIds}, start, end, orderByComparator,
			groupIds);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds)}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset list entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of asset list entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset list entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of asset list entries where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching asset list entries
	 */
	@Override
	public int countByGroupId(long[] groupIds) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds)});
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {new long[] {groupId}}, groupId);
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long[] groupIds) {
		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupIds}, groupIds);
	}

	private UniquePersistenceFinder<AssetListEntry, NoSuchEntryException>
		_uniquePersistenceFinderByG_ALEK;

	/**
	 * Returns the asset list entry where groupId = &#63; and assetListEntryKey = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param assetListEntryKey the asset list entry key
	 * @return the matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry findByG_ALEK(long groupId, String assetListEntryKey)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByG_ALEK.find(
			finderCache, new Object[] {groupId, assetListEntryKey});
	}

	/**
	 * Returns the asset list entry where groupId = &#63; and assetListEntryKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param assetListEntryKey the asset list entry key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry fetchByG_ALEK(
		long groupId, String assetListEntryKey, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_ALEK.fetch(
			finderCache, new Object[] {groupId, assetListEntryKey},
			useFinderCache);
	}

	/**
	 * Removes the asset list entry where groupId = &#63; and assetListEntryKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param assetListEntryKey the asset list entry key
	 * @return the asset list entry that was removed
	 */
	@Override
	public AssetListEntry removeByG_ALEK(long groupId, String assetListEntryKey)
		throws NoSuchEntryException {

		AssetListEntry assetListEntry = findByG_ALEK(
			groupId, assetListEntryKey);

		return remove(assetListEntry);
	}

	/**
	 * Returns the number of asset list entries where groupId = &#63; and assetListEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetListEntryKey the asset list entry key
	 * @return the number of matching asset list entries
	 */
	@Override
	public int countByG_ALEK(long groupId, String assetListEntryKey) {
		return _uniquePersistenceFinderByG_ALEK.count(
			finderCache, new Object[] {groupId, assetListEntryKey});
	}

	private UniquePersistenceFinder<AssetListEntry, NoSuchEntryException>
		_uniquePersistenceFinderByG_T;

	/**
	 * Returns the asset list entry where groupId = &#63; and title = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry findByG_T(long groupId, String title)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByG_T.find(
			finderCache, new Object[] {groupId, title});
	}

	/**
	 * Returns the asset list entry where groupId = &#63; and title = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry fetchByG_T(
		long groupId, String title, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_T.fetch(
			finderCache, new Object[] {groupId, title}, useFinderCache);
	}

	/**
	 * Removes the asset list entry where groupId = &#63; and title = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the asset list entry that was removed
	 */
	@Override
	public AssetListEntry removeByG_T(long groupId, String title)
		throws NoSuchEntryException {

		AssetListEntry assetListEntry = findByG_T(groupId, title);

		return remove(assetListEntry);
	}

	/**
	 * Returns the number of asset list entries where groupId = &#63; and title = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the number of matching asset list entries
	 */
	@Override
	public int countByG_T(long groupId, String title) {
		return _uniquePersistenceFinderByG_T.count(
			finderCache, new Object[] {groupId, title});
	}

	private FilterCollectionPersistenceFinder
		<AssetListEntry, NoSuchEntryException>
			_collectionPersistenceFinderByG_LikeT;

	/**
	 * Returns all the asset list entries where groupId = &#63; and title LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT(long groupId, String title) {
		return findByG_LikeT(
			groupId, title, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT(
		long groupId, String title, int start, int end) {

		return findByG_LikeT(groupId, title, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT(
		long groupId, String title, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return findByG_LikeT(
			groupId, title, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT(
		long groupId, String title, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeT.find(
			finderCache, new Object[] {new long[] {groupId}, title}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and title LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry findByG_LikeT_First(
			long groupId, String title,
			OrderByComparator<AssetListEntry> orderByComparator)
		throws NoSuchEntryException {

		AssetListEntry assetListEntry = fetchByG_LikeT_First(
			groupId, title, orderByComparator);

		if (assetListEntry != null) {
			return assetListEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", titleLIKE");
		sb.append(title);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and title LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry fetchByG_LikeT_First(
		long groupId, String title,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeT.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}, title},
			orderByComparator);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_LikeT(
		long groupId, String title) {

		return filterFindByG_LikeT(
			groupId, title, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_LikeT(
		long groupId, String title, int start, int end) {

		return filterFindByG_LikeT(groupId, title, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permissions to view where groupId = &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_LikeT(
		long groupId, String title, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeT.filterFind(
			finderCache, new Object[] {new long[] {groupId}, title}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @return the matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_LikeT(
		long[] groupIds, String title) {

		return filterFindByG_LikeT(
			groupIds, title, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_LikeT(
		long[] groupIds, String title, int start, int end) {

		return filterFindByG_LikeT(groupIds, title, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_LikeT(
		long[] groupIds, String title, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_LikeT.filterFind(
			finderCache, new Object[] {groupIds, title}, start, end,
			orderByComparator, groupIds);
	}

	/**
	 * Returns all the asset list entries where groupId = any &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @return the matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT(long[] groupIds, String title) {
		return findByG_LikeT(
			groupIds, title, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = any &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT(
		long[] groupIds, String title, int start, int end) {

		return findByG_LikeT(groupIds, title, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = any &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT(
		long[] groupIds, String title, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return findByG_LikeT(
			groupIds, title, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT(
		long[] groupIds, String title, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeT.find(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds), title},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset list entries where groupId = &#63; and title LIKE &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 */
	@Override
	public void removeByG_LikeT(long groupId, String title) {
		_collectionPersistenceFinderByG_LikeT.remove(
			finderCache, new Object[] {new long[] {groupId}, title});
	}

	/**
	 * Returns the number of asset list entries where groupId = &#63; and title LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the number of matching asset list entries
	 */
	@Override
	public int countByG_LikeT(long groupId, String title) {
		return _collectionPersistenceFinderByG_LikeT.count(
			finderCache, new Object[] {new long[] {groupId}, title});
	}

	/**
	 * Returns the number of asset list entries where groupId = any &#63; and title LIKE &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @return the number of matching asset list entries
	 */
	@Override
	public int countByG_LikeT(long[] groupIds, String title) {
		return _collectionPersistenceFinderByG_LikeT.count(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), title});
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeT(long groupId, String title) {
		return _collectionPersistenceFinderByG_LikeT.filterCount(
			finderCache, new Object[] {new long[] {groupId}, title}, groupId);
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeT(long[] groupIds, String title) {
		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_LikeT.filterCount(
			finderCache, new Object[] {groupIds, title}, groupIds);
	}

	private FilterCollectionPersistenceFinder
		<AssetListEntry, NoSuchEntryException>
			_collectionPersistenceFinderByG_TY;

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_TY(
		long groupId, int type, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_TY.find(
			finderCache, new Object[] {groupId, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry findByG_TY_First(
			long groupId, int type,
			OrderByComparator<AssetListEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_TY.findFirst(
			finderCache, new Object[] {groupId, type}, orderByComparator);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry fetchByG_TY_First(
		long groupId, int type,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_TY.fetchFirst(
			finderCache, new Object[] {groupId, type}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permissions to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_TY(
		long groupId, int type, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_TY.filterFind(
			finderCache, new Object[] {groupId, type}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the asset list entries where groupId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 */
	@Override
	public void removeByG_TY(long groupId, int type) {
		_collectionPersistenceFinderByG_TY.remove(
			finderCache, new Object[] {groupId, type});
	}

	/**
	 * Returns the number of asset list entries where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching asset list entries
	 */
	@Override
	public int countByG_TY(long groupId, int type) {
		return _collectionPersistenceFinderByG_TY.count(
			finderCache, new Object[] {groupId, type});
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_TY(long groupId, int type) {
		return _collectionPersistenceFinderByG_TY.filterCount(
			finderCache, new Object[] {groupId, type}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<AssetListEntry, NoSuchEntryException>
			_collectionPersistenceFinderByG_AET;

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_AET(
		long groupId, String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_AET.find(
			finderCache,
			new Object[] {new long[] {groupId}, new String[] {assetEntryType}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry findByG_AET_First(
			long groupId, String assetEntryType,
			OrderByComparator<AssetListEntry> orderByComparator)
		throws NoSuchEntryException {

		AssetListEntry assetListEntry = fetchByG_AET_First(
			groupId, assetEntryType, orderByComparator);

		if (assetListEntry != null) {
			return assetListEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", assetEntryType=");
		sb.append(assetEntryType);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry fetchByG_AET_First(
		long groupId, String assetEntryType,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_AET.fetchFirst(
			finderCache,
			new Object[] {new long[] {groupId}, new String[] {assetEntryType}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permissions to view where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_AET(
		long groupId, String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_AET.filterFind(
			finderCache,
			new Object[] {new long[] {groupId}, new String[] {assetEntryType}},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permission to view where groupId = any &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_AET(
		long[] groupIds, String[] assetEntryTypes, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_AET.filterFind(
			finderCache,
			new Object[] {groupIds, ArrayUtil.sortedUnique(assetEntryTypes)},
			start, end, orderByComparator, groupIds);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and assetEntryType = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_AET(
		long[] groupIds, String[] assetEntryTypes, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_AET.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds),
				ArrayUtil.sortedUnique(assetEntryTypes)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset list entries where groupId = &#63; and assetEntryType = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 */
	@Override
	public void removeByG_AET(long groupId, String assetEntryType) {
		_collectionPersistenceFinderByG_AET.remove(
			finderCache,
			new Object[] {new long[] {groupId}, new String[] {assetEntryType}});
	}

	/**
	 * Returns the number of asset list entries where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries
	 */
	@Override
	public int countByG_AET(long groupId, String assetEntryType) {
		return _collectionPersistenceFinderByG_AET.count(
			finderCache,
			new Object[] {new long[] {groupId}, new String[] {assetEntryType}});
	}

	/**
	 * Returns the number of asset list entries where groupId = any &#63; and assetEntryType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @return the number of matching asset list entries
	 */
	@Override
	public int countByG_AET(long[] groupIds, String[] assetEntryTypes) {
		return _collectionPersistenceFinderByG_AET.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds),
				ArrayUtil.sortedUnique(assetEntryTypes)
			});
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_AET(long groupId, String assetEntryType) {
		return _collectionPersistenceFinderByG_AET.filterCount(
			finderCache,
			new Object[] {new long[] {groupId}, new String[] {assetEntryType}},
			groupId);
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = any &#63; and assetEntryType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_AET(long[] groupIds, String[] assetEntryTypes) {
		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_AET.filterCount(
			finderCache,
			new Object[] {groupIds, ArrayUtil.sortedUnique(assetEntryTypes)},
			groupIds);
	}

	private FilterCollectionPersistenceFinder
		<AssetListEntry, NoSuchEntryException>
			_collectionPersistenceFinderByG_LikeT_AET;

	/**
	 * Returns all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT_AET(
		long groupId, String title, String assetEntryType) {

		return findByG_LikeT_AET(
			groupId, title, assetEntryType, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT_AET(
		long groupId, String title, String assetEntryType, int start, int end) {

		return findByG_LikeT_AET(
			groupId, title, assetEntryType, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT_AET(
		long groupId, String title, String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return findByG_LikeT_AET(
			groupId, title, assetEntryType, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT_AET(
		long groupId, String title, String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeT_AET.find(
			finderCache,
			new Object[] {
				new long[] {groupId}, title, new String[] {assetEntryType}
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry findByG_LikeT_AET_First(
			long groupId, String title, String assetEntryType,
			OrderByComparator<AssetListEntry> orderByComparator)
		throws NoSuchEntryException {

		AssetListEntry assetListEntry = fetchByG_LikeT_AET_First(
			groupId, title, assetEntryType, orderByComparator);

		if (assetListEntry != null) {
			return assetListEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", titleLIKE");
		sb.append(title);

		sb.append(", assetEntryType=");
		sb.append(assetEntryType);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry fetchByG_LikeT_AET_First(
		long groupId, String title, String assetEntryType,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeT_AET.fetchFirst(
			finderCache,
			new Object[] {
				new long[] {groupId}, title, new String[] {assetEntryType}
			},
			orderByComparator);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_LikeT_AET(
		long groupId, String title, String assetEntryType) {

		return filterFindByG_LikeT_AET(
			groupId, title, assetEntryType, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_LikeT_AET(
		long groupId, String title, String assetEntryType, int start, int end) {

		return filterFindByG_LikeT_AET(
			groupId, title, assetEntryType, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permissions to view where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_LikeT_AET(
		long groupId, String title, String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeT_AET.filterFind(
			finderCache,
			new Object[] {
				new long[] {groupId}, title, new String[] {assetEntryType}
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @return the matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes) {

		return filterFindByG_LikeT_AET(
			groupIds, title, assetEntryTypes, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes, int start,
		int end) {

		return filterFindByG_LikeT_AET(
			groupIds, title, assetEntryTypes, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes, int start,
		int end, OrderByComparator<AssetListEntry> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_LikeT_AET.filterFind(
			finderCache,
			new Object[] {
				groupIds, title, ArrayUtil.sortedUnique(assetEntryTypes)
			},
			start, end, orderByComparator, groupIds);
	}

	/**
	 * Returns all the asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @return the matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes) {

		return findByG_LikeT_AET(
			groupIds, title, assetEntryTypes, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes, int start,
		int end) {

		return findByG_LikeT_AET(
			groupIds, title, assetEntryTypes, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes, int start,
		int end, OrderByComparator<AssetListEntry> orderByComparator) {

		return findByG_LikeT_AET(
			groupIds, title, assetEntryTypes, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes, int start,
		int end, OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeT_AET.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), title,
				ArrayUtil.sortedUnique(assetEntryTypes)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 */
	@Override
	public void removeByG_LikeT_AET(
		long groupId, String title, String assetEntryType) {

		_collectionPersistenceFinderByG_LikeT_AET.remove(
			finderCache,
			new Object[] {
				new long[] {groupId}, title, new String[] {assetEntryType}
			});
	}

	/**
	 * Returns the number of asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries
	 */
	@Override
	public int countByG_LikeT_AET(
		long groupId, String title, String assetEntryType) {

		return _collectionPersistenceFinderByG_LikeT_AET.count(
			finderCache,
			new Object[] {
				new long[] {groupId}, title, new String[] {assetEntryType}
			});
	}

	/**
	 * Returns the number of asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @return the number of matching asset list entries
	 */
	@Override
	public int countByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes) {

		return _collectionPersistenceFinderByG_LikeT_AET.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), title,
				ArrayUtil.sortedUnique(assetEntryTypes)
			});
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeT_AET(
		long groupId, String title, String assetEntryType) {

		return _collectionPersistenceFinderByG_LikeT_AET.filterCount(
			finderCache,
			new Object[] {
				new long[] {groupId}, title, new String[] {assetEntryType}
			},
			groupId);
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_LikeT_AET.filterCount(
			finderCache,
			new Object[] {
				groupIds, title, ArrayUtil.sortedUnique(assetEntryTypes)
			},
			groupIds);
	}

	private FilterCollectionPersistenceFinder
		<AssetListEntry, NoSuchEntryException>
			_collectionPersistenceFinderByG_AES_AET;

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType,
		int start, int end, OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_AES_AET.find(
			finderCache,
			new Object[] {
				new long[] {groupId}, assetEntrySubtype, assetEntryType
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry findByG_AES_AET_First(
			long groupId, String assetEntrySubtype, String assetEntryType,
			OrderByComparator<AssetListEntry> orderByComparator)
		throws NoSuchEntryException {

		AssetListEntry assetListEntry = fetchByG_AES_AET_First(
			groupId, assetEntrySubtype, assetEntryType, orderByComparator);

		if (assetListEntry != null) {
			return assetListEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", assetEntrySubtype=");
		sb.append(assetEntrySubtype);

		sb.append(", assetEntryType=");
		sb.append(assetEntryType);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry fetchByG_AES_AET_First(
		long groupId, String assetEntrySubtype, String assetEntryType,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_AES_AET.fetchFirst(
			finderCache,
			new Object[] {
				new long[] {groupId}, assetEntrySubtype, assetEntryType
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permissions to view where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType,
		int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_AES_AET.filterFind(
			finderCache,
			new Object[] {
				new long[] {groupId}, assetEntrySubtype, assetEntryType
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permission to view where groupId = any &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType,
		int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_AES_AET.filterFind(
			finderCache,
			new Object[] {groupIds, assetEntrySubtype, assetEntryType}, start,
			end, orderByComparator, groupIds);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType,
		int start, int end, OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_AES_AET.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), assetEntrySubtype,
				assetEntryType
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset list entries where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 */
	@Override
	public void removeByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType) {

		_collectionPersistenceFinderByG_AES_AET.remove(
			finderCache,
			new Object[] {
				new long[] {groupId}, assetEntrySubtype, assetEntryType
			});
	}

	/**
	 * Returns the number of asset list entries where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries
	 */
	@Override
	public int countByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType) {

		return _collectionPersistenceFinderByG_AES_AET.count(
			finderCache,
			new Object[] {
				new long[] {groupId}, assetEntrySubtype, assetEntryType
			});
	}

	/**
	 * Returns the number of asset list entries where groupId = any &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries
	 */
	@Override
	public int countByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType) {

		return _collectionPersistenceFinderByG_AES_AET.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), assetEntrySubtype,
				assetEntryType
			});
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType) {

		return _collectionPersistenceFinderByG_AES_AET.filterCount(
			finderCache,
			new Object[] {
				new long[] {groupId}, assetEntrySubtype, assetEntryType
			},
			groupId);
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = any &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_AES_AET.filterCount(
			finderCache,
			new Object[] {groupIds, assetEntrySubtype, assetEntryType},
			groupIds);
	}

	private FilterCollectionPersistenceFinder
		<AssetListEntry, NoSuchEntryException>
			_collectionPersistenceFinderByG_LikeT_AES_AET;

	/**
	 * Returns all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType) {

		return findByG_LikeT_AES_AET(
			groupId, title, assetEntrySubtype, assetEntryType,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end) {

		return findByG_LikeT_AES_AET(
			groupId, title, assetEntrySubtype, assetEntryType, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return findByG_LikeT_AES_AET(
			groupId, title, assetEntrySubtype, assetEntryType, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeT_AES_AET.find(
			finderCache,
			new Object[] {
				new long[] {groupId}, title, assetEntrySubtype, assetEntryType
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry findByG_LikeT_AES_AET_First(
			long groupId, String title, String assetEntrySubtype,
			String assetEntryType,
			OrderByComparator<AssetListEntry> orderByComparator)
		throws NoSuchEntryException {

		AssetListEntry assetListEntry = fetchByG_LikeT_AES_AET_First(
			groupId, title, assetEntrySubtype, assetEntryType,
			orderByComparator);

		if (assetListEntry != null) {
			return assetListEntry;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", titleLIKE");
		sb.append(title);

		sb.append(", assetEntrySubtype=");
		sb.append(assetEntrySubtype);

		sb.append(", assetEntryType=");
		sb.append(assetEntryType);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry fetchByG_LikeT_AES_AET_First(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeT_AES_AET.fetchFirst(
			finderCache,
			new Object[] {
				new long[] {groupId}, title, assetEntrySubtype, assetEntryType
			},
			orderByComparator);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType) {

		return filterFindByG_LikeT_AES_AET(
			groupId, title, assetEntrySubtype, assetEntryType,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end) {

		return filterFindByG_LikeT_AES_AET(
			groupId, title, assetEntrySubtype, assetEntryType, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permissions to view where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeT_AES_AET.filterFind(
			finderCache,
			new Object[] {
				new long[] {groupId}, title, assetEntrySubtype, assetEntryType
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType) {

		return filterFindByG_LikeT_AES_AET(
			groupIds, title, assetEntrySubtype, assetEntryType,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end) {

		return filterFindByG_LikeT_AES_AET(
			groupIds, title, assetEntrySubtype, assetEntryType, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	@Override
	public List<AssetListEntry> filterFindByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_LikeT_AES_AET.filterFind(
			finderCache,
			new Object[] {groupIds, title, assetEntrySubtype, assetEntryType},
			start, end, orderByComparator, groupIds);
	}

	/**
	 * Returns all the asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType) {

		return findByG_LikeT_AES_AET(
			groupIds, title, assetEntrySubtype, assetEntryType,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end) {

		return findByG_LikeT_AES_AET(
			groupIds, title, assetEntrySubtype, assetEntryType, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return findByG_LikeT_AES_AET(
			groupIds, title, assetEntrySubtype, assetEntryType, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	@Override
	public List<AssetListEntry> findByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeT_AES_AET.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), title, assetEntrySubtype,
				assetEntryType
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 */
	@Override
	public void removeByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType) {

		_collectionPersistenceFinderByG_LikeT_AES_AET.remove(
			finderCache,
			new Object[] {
				new long[] {groupId}, title, assetEntrySubtype, assetEntryType
			});
	}

	/**
	 * Returns the number of asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries
	 */
	@Override
	public int countByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType) {

		return _collectionPersistenceFinderByG_LikeT_AES_AET.count(
			finderCache,
			new Object[] {
				new long[] {groupId}, title, assetEntrySubtype, assetEntryType
			});
	}

	/**
	 * Returns the number of asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries
	 */
	@Override
	public int countByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType) {

		return _collectionPersistenceFinderByG_LikeT_AES_AET.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), title, assetEntrySubtype,
				assetEntryType
			});
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType) {

		return _collectionPersistenceFinderByG_LikeT_AES_AET.filterCount(
			finderCache,
			new Object[] {
				new long[] {groupId}, title, assetEntrySubtype, assetEntryType
			},
			groupId);
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_LikeT_AES_AET.filterCount(
			finderCache,
			new Object[] {groupIds, title, assetEntrySubtype, assetEntryType},
			groupIds);
	}

	private UniquePersistenceFinder<AssetListEntry, NoSuchEntryException>
		_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the asset list entry where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the asset list entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	@Override
	public AssetListEntry fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the asset list entry where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the asset list entry that was removed
	 */
	@Override
	public AssetListEntry removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchEntryException {

		AssetListEntry assetListEntry = findByERC_G(
			externalReferenceCode, groupId);

		return remove(assetListEntry);
	}

	/**
	 * Returns the number of asset list entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching asset list entries
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public AssetListEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(AssetListEntry.class);

		setModelImplClass(AssetListEntryImpl.class);
		setModelPKClass(long.class);

		setTable(AssetListEntryTable.INSTANCE);
	}

	/**
	 * Creates a new asset list entry with the primary key. Does not add the asset list entry to the database.
	 *
	 * @param assetListEntryId the primary key for the new asset list entry
	 * @return the new asset list entry
	 */
	@Override
	public AssetListEntry create(long assetListEntryId) {
		AssetListEntry assetListEntry = new AssetListEntryImpl();

		assetListEntry.setNew(true);
		assetListEntry.setPrimaryKey(assetListEntryId);

		String uuid = PortalUUIDUtil.generate();

		assetListEntry.setUuid(uuid);

		assetListEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return assetListEntry;
	}

	/**
	 * Removes the asset list entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param assetListEntryId the primary key of the asset list entry
	 * @return the asset list entry that was removed
	 * @throws NoSuchEntryException if a asset list entry with the primary key could not be found
	 */
	@Override
	public AssetListEntry remove(long assetListEntryId)
		throws NoSuchEntryException {

		return remove((Serializable)assetListEntryId);
	}

	@Override
	protected AssetListEntry removeImpl(AssetListEntry assetListEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(assetListEntry)) {
				assetListEntry = (AssetListEntry)session.get(
					AssetListEntryImpl.class,
					assetListEntry.getPrimaryKeyObj());
			}

			if ((assetListEntry != null) &&
				ctPersistenceHelper.isRemove(assetListEntry)) {

				session.delete(assetListEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (assetListEntry != null) {
			clearCache(assetListEntry);
		}

		return assetListEntry;
	}

	@Override
	public AssetListEntry updateImpl(AssetListEntry assetListEntry) {
		boolean isNew = assetListEntry.isNew();

		if (!(assetListEntry instanceof AssetListEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(assetListEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					assetListEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in assetListEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AssetListEntry implementation " +
					assetListEntry.getClass());
		}

		AssetListEntryModelImpl assetListEntryModelImpl =
			(AssetListEntryModelImpl)assetListEntry;

		if (Validator.isNull(assetListEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			assetListEntry.setUuid(uuid);
		}

		if (Validator.isNull(assetListEntry.getExternalReferenceCode())) {
			assetListEntry.setExternalReferenceCode(assetListEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					assetListEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					assetListEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = assetListEntry.getCompanyId();

					long groupId = assetListEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = assetListEntry.getPrimaryKey();
					}

					try {
						assetListEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								AssetListEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								assetListEntry.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			AssetListEntry ercAssetListEntry = fetchByERC_G(
				assetListEntry.getExternalReferenceCode(),
				assetListEntry.getGroupId());

			if (isNew) {
				if (ercAssetListEntry != null) {
					throw new DuplicateAssetListEntryExternalReferenceCodeException(
						"Duplicate asset list entry with external reference code " +
							assetListEntry.getExternalReferenceCode() +
								" and group " + assetListEntry.getGroupId());
				}
			}
			else {
				if ((ercAssetListEntry != null) &&
					(assetListEntry.getAssetListEntryId() !=
						ercAssetListEntry.getAssetListEntryId())) {

					throw new DuplicateAssetListEntryExternalReferenceCodeException(
						"Duplicate asset list entry with external reference code " +
							assetListEntry.getExternalReferenceCode() +
								" and group " + assetListEntry.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (assetListEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				assetListEntry.setCreateDate(date);
			}
			else {
				assetListEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!assetListEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				assetListEntry.setModifiedDate(date);
			}
			else {
				assetListEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(assetListEntry)) {
				if (!isNew) {
					session.evict(
						AssetListEntryImpl.class,
						assetListEntry.getPrimaryKeyObj());
				}

				session.save(assetListEntry);
			}
			else {
				assetListEntry = (AssetListEntry)session.merge(assetListEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(assetListEntry, false);

		if (isNew) {
			assetListEntry.setNew(false);
		}

		assetListEntry.resetOriginalValues();

		return assetListEntry;
	}

	/**
	 * Returns the asset list entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param assetListEntryId the primary key of the asset list entry
	 * @return the asset list entry
	 * @throws NoSuchEntryException if a asset list entry with the primary key could not be found
	 */
	@Override
	public AssetListEntry findByPrimaryKey(long assetListEntryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)assetListEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the asset list entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param assetListEntryId the primary key of the asset list entry
	 * @return the asset list entry, or <code>null</code> if a asset list entry with the primary key could not be found
	 */
	@Override
	public AssetListEntry fetchByPrimaryKey(long assetListEntryId) {
		return fetchByPrimaryKey((Serializable)assetListEntryId);
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
		return "assetListEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ASSETLISTENTRY;
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
		return AssetListEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AssetListEntry";
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
		ctMergeColumnNames.add("assetListEntryKey");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("assetEntrySubtype");
		ctMergeColumnNames.add("assetEntryType");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("assetListEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "assetListEntryKey"});

		_uniqueIndexColumnNames.add(new String[] {"groupId", "title"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the asset list entry persistence.
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
			_SQL_SELECT_ASSETLISTENTRY_WHERE, _SQL_COUNT_ASSETLISTENTRY_WHERE,
			AssetListEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"assetListEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, AssetListEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(AssetListEntry::getUuid),
				AssetListEntry::getGroupId),
			_SQL_SELECT_ASSETLISTENTRY_WHERE, "",
			new FinderColumn<>(
				"assetListEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, AssetListEntry::getUuid),
			new FinderColumn<>(
				"assetListEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, AssetListEntry::getGroupId));

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
				_SQL_SELECT_ASSETLISTENTRY_WHERE,
				_SQL_COUNT_ASSETLISTENTRY_WHERE,
				AssetListEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"assetListEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					AssetListEntry::getUuid),
				new FinderColumn<>(
					"assetListEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, AssetListEntry::getCompanyId));

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
				_SQL_SELECT_ASSETLISTENTRY_WHERE,
				_SQL_COUNT_ASSETLISTENTRY_WHERE,
				AssetListEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new ArrayableFinderColumn<>(
					"assetListEntry.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, AssetListEntry::getGroupId));

		_uniquePersistenceFinderByG_ALEK = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_ALEK",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "assetListEntryKey"}, 0, 2, false,
				AssetListEntry::getGroupId,
				convertNullFunction(AssetListEntry::getAssetListEntryKey)),
			_SQL_SELECT_ASSETLISTENTRY_WHERE, "",
			new FinderColumn<>(
				"assetListEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, AssetListEntry::getGroupId),
			new FinderColumn<>(
				"assetListEntry.", "assetListEntryKey",
				FinderColumn.Type.STRING, "=", true, true,
				AssetListEntry::getAssetListEntryKey));

		_uniquePersistenceFinderByG_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_T",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "title"}, 0, 2, false,
				AssetListEntry::getGroupId,
				convertNullFunction(AssetListEntry::getTitle)),
			_SQL_SELECT_ASSETLISTENTRY_WHERE, "",
			new FinderColumn<>(
				"assetListEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, AssetListEntry::getGroupId),
			new FinderColumn<>(
				"assetListEntry.", "title", FinderColumn.Type.STRING, "=", true,
				true, AssetListEntry::getTitle));

		_collectionPersistenceFinderByG_LikeT =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeT",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "title"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LikeT",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "title"}, false),
				_SQL_SELECT_ASSETLISTENTRY_WHERE,
				_SQL_COUNT_ASSETLISTENTRY_WHERE,
				AssetListEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new ArrayableFinderColumn<>(
					"assetListEntry.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, AssetListEntry::getGroupId),
				new FinderColumn<>(
					"assetListEntry.", "title", FinderColumn.Type.STRING,
					"LIKE", true, true, AssetListEntry::getTitle));

		_collectionPersistenceFinderByG_TY =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_TY",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_TY",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_TY",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "type_"}, false),
				_SQL_SELECT_ASSETLISTENTRY_WHERE,
				_SQL_COUNT_ASSETLISTENTRY_WHERE,
				AssetListEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"assetListEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, AssetListEntry::getGroupId),
				new FinderColumn<>(
					"assetListEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					AssetListEntry::getType));

		_collectionPersistenceFinderByG_AET =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_AET",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "assetEntryType"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_AET",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "assetEntryType"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_AET",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "assetEntryType"}, 0, 2, false,
					null),
				_SQL_SELECT_ASSETLISTENTRY_WHERE,
				_SQL_COUNT_ASSETLISTENTRY_WHERE,
				AssetListEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new ArrayableFinderColumn<>(
					"assetListEntry.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, AssetListEntry::getGroupId),
				new ArrayableFinderColumn<>(
					"assetListEntry.", "assetEntryType",
					FinderColumn.Type.STRING, "=", false, true, true,
					AssetListEntry::getAssetEntryType));

		_collectionPersistenceFinderByG_LikeT_AET =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeT_AET",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "title", "assetEntryType"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_LikeT_AET",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {"groupId", "title", "assetEntryType"}, false),
				_SQL_SELECT_ASSETLISTENTRY_WHERE,
				_SQL_COUNT_ASSETLISTENTRY_WHERE,
				AssetListEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new ArrayableFinderColumn<>(
					"assetListEntry.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, AssetListEntry::getGroupId),
				new FinderColumn<>(
					"assetListEntry.", "title", FinderColumn.Type.STRING,
					"LIKE", true, true, AssetListEntry::getTitle),
				new ArrayableFinderColumn<>(
					"assetListEntry.", "assetEntryType",
					FinderColumn.Type.STRING, "=", false, true, true,
					AssetListEntry::getAssetEntryType));

		_collectionPersistenceFinderByG_AES_AET =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_AES_AET",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "assetEntrySubtype", "assetEntryType"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_AES_AET",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {
						"groupId", "assetEntrySubtype", "assetEntryType"
					},
					0, 6, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_AES_AET",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {
						"groupId", "assetEntrySubtype", "assetEntryType"
					},
					0, 6, false, null),
				_SQL_SELECT_ASSETLISTENTRY_WHERE,
				_SQL_COUNT_ASSETLISTENTRY_WHERE,
				AssetListEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new ArrayableFinderColumn<>(
					"assetListEntry.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, AssetListEntry::getGroupId),
				new FinderColumn<>(
					"assetListEntry.", "assetEntrySubtype",
					FinderColumn.Type.STRING, "=", true, true,
					AssetListEntry::getAssetEntrySubtype),
				new FinderColumn<>(
					"assetListEntry.", "assetEntryType",
					FinderColumn.Type.STRING, "=", true, true,
					AssetListEntry::getAssetEntryType));

		_collectionPersistenceFinderByG_LikeT_AES_AET =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_LikeT_AES_AET",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "title", "assetEntrySubtype",
						"assetEntryType"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_LikeT_AES_AET",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), String.class.getName()
					},
					new String[] {
						"groupId", "title", "assetEntrySubtype",
						"assetEntryType"
					},
					false),
				_SQL_SELECT_ASSETLISTENTRY_WHERE,
				_SQL_COUNT_ASSETLISTENTRY_WHERE,
				AssetListEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new ArrayableFinderColumn<>(
					"assetListEntry.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, AssetListEntry::getGroupId),
				new FinderColumn<>(
					"assetListEntry.", "title", FinderColumn.Type.STRING,
					"LIKE", true, true, AssetListEntry::getTitle),
				new FinderColumn<>(
					"assetListEntry.", "assetEntrySubtype",
					FinderColumn.Type.STRING, "=", true, true,
					AssetListEntry::getAssetEntrySubtype),
				new FinderColumn<>(
					"assetListEntry.", "assetEntryType",
					FinderColumn.Type.STRING, "=", true, true,
					AssetListEntry::getAssetEntryType));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(AssetListEntry::getExternalReferenceCode),
				AssetListEntry::getGroupId),
			_SQL_SELECT_ASSETLISTENTRY_WHERE, "",
			new FinderColumn<>(
				"assetListEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				AssetListEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"assetListEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, AssetListEntry::getGroupId));

		AssetListEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AssetListEntryUtil.setPersistence(null);

		entityCache.removeCache(AssetListEntryImpl.class.getName());
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
		AssetListEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ASSETLISTENTRY =
		"SELECT assetListEntry FROM AssetListEntry assetListEntry";

	private static final String _SQL_SELECT_ASSETLISTENTRY_WHERE =
		"SELECT assetListEntry FROM AssetListEntry assetListEntry WHERE ";

	private static final String _SQL_COUNT_ASSETLISTENTRY_WHERE =
		"SELECT COUNT(assetListEntry) FROM AssetListEntry assetListEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No AssetListEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		AssetListEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-976612269