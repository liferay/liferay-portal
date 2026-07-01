/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.persistence.impl;

import com.liferay.asset.list.exception.NoSuchEntryUsageException;
import com.liferay.asset.list.model.AssetListEntryUsage;
import com.liferay.asset.list.model.AssetListEntryUsageTable;
import com.liferay.asset.list.model.impl.AssetListEntryUsageImpl;
import com.liferay.asset.list.model.impl.AssetListEntryUsageModelImpl;
import com.liferay.asset.list.service.persistence.AssetListEntryUsagePersistence;
import com.liferay.asset.list.service.persistence.AssetListEntryUsageUtil;
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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
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
 * The persistence implementation for the asset list entry usage service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AssetListEntryUsagePersistence.class)
public class AssetListEntryUsagePersistenceImpl
	extends BasePersistenceImpl<AssetListEntryUsage, NoSuchEntryUsageException>
	implements AssetListEntryUsagePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AssetListEntryUsageUtil</code> to access the asset list entry usage persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AssetListEntryUsageImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AssetListEntryUsage, NoSuchEntryUsageException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the asset list entry usages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entry usages
	 * @param end the upper bound of the range of asset list entry usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry usages
	 */
	@Override
	public List<AssetListEntryUsage> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetListEntryUsage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset list entry usage in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry usage
	 * @throws NoSuchEntryUsageException if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage findByUuid_First(
			String uuid,
			OrderByComparator<AssetListEntryUsage> orderByComparator)
		throws NoSuchEntryUsageException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first asset list entry usage in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry usage, or <code>null</code> if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage fetchByUuid_First(
		String uuid, OrderByComparator<AssetListEntryUsage> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the asset list entry usages where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of asset list entry usages where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset list entry usages
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<AssetListEntryUsage, NoSuchEntryUsageException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the asset list entry usage where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryUsageException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset list entry usage
	 * @throws NoSuchEntryUsageException if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage findByUUID_G(String uuid, long groupId)
		throws NoSuchEntryUsageException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the asset list entry usage where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry usage, or <code>null</code> if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the asset list entry usage where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset list entry usage that was removed
	 */
	@Override
	public AssetListEntryUsage removeByUUID_G(String uuid, long groupId)
		throws NoSuchEntryUsageException {

		AssetListEntryUsage assetListEntryUsage = findByUUID_G(uuid, groupId);

		return remove(assetListEntryUsage);
	}

	/**
	 * Returns the number of asset list entry usages where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset list entry usages
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<AssetListEntryUsage, NoSuchEntryUsageException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the asset list entry usages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entry usages
	 * @param end the upper bound of the range of asset list entry usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry usages
	 */
	@Override
	public List<AssetListEntryUsage> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetListEntryUsage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry usage in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry usage
	 * @throws NoSuchEntryUsageException if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<AssetListEntryUsage> orderByComparator)
		throws NoSuchEntryUsageException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first asset list entry usage in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry usage, or <code>null</code> if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<AssetListEntryUsage> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the asset list entry usages where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of asset list entry usages where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset list entry usages
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<AssetListEntryUsage, NoSuchEntryUsageException>
			_collectionPersistenceFinderByPlid;

	/**
	 * Returns an ordered range of all the asset list entry usages where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryUsageModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of asset list entry usages
	 * @param end the upper bound of the range of asset list entry usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry usages
	 */
	@Override
	public List<AssetListEntryUsage> findByPlid(
		long plid, int start, int end,
		OrderByComparator<AssetListEntryUsage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPlid.find(
			finderCache, new Object[] {plid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset list entry usage in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry usage
	 * @throws NoSuchEntryUsageException if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage findByPlid_First(
			long plid, OrderByComparator<AssetListEntryUsage> orderByComparator)
		throws NoSuchEntryUsageException {

		return _collectionPersistenceFinderByPlid.findFirst(
			finderCache, new Object[] {plid}, orderByComparator);
	}

	/**
	 * Returns the first asset list entry usage in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry usage, or <code>null</code> if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage fetchByPlid_First(
		long plid, OrderByComparator<AssetListEntryUsage> orderByComparator) {

		return _collectionPersistenceFinderByPlid.fetchFirst(
			finderCache, new Object[] {plid}, orderByComparator);
	}

	/**
	 * Removes all the asset list entry usages where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	@Override
	public void removeByPlid(long plid) {
		_collectionPersistenceFinderByPlid.remove(
			finderCache, new Object[] {plid});
	}

	/**
	 * Returns the number of asset list entry usages where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching asset list entry usages
	 */
	@Override
	public int countByPlid(long plid) {
		return _collectionPersistenceFinderByPlid.count(
			finderCache, new Object[] {plid});
	}

	private CollectionPersistenceFinder
		<AssetListEntryUsage, NoSuchEntryUsageException>
			_collectionPersistenceFinderByCT_P;

	/**
	 * Returns an ordered range of all the asset list entry usages where containerType = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryUsageModelImpl</code>.
	 * </p>
	 *
	 * @param containerType the container type
	 * @param plid the plid
	 * @param start the lower bound of the range of asset list entry usages
	 * @param end the upper bound of the range of asset list entry usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry usages
	 */
	@Override
	public List<AssetListEntryUsage> findByCT_P(
		long containerType, long plid, int start, int end,
		OrderByComparator<AssetListEntryUsage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCT_P.find(
			finderCache, new Object[] {containerType, plid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry usage in the ordered set where containerType = &#63; and plid = &#63;.
	 *
	 * @param containerType the container type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry usage
	 * @throws NoSuchEntryUsageException if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage findByCT_P_First(
			long containerType, long plid,
			OrderByComparator<AssetListEntryUsage> orderByComparator)
		throws NoSuchEntryUsageException {

		return _collectionPersistenceFinderByCT_P.findFirst(
			finderCache, new Object[] {containerType, plid}, orderByComparator);
	}

	/**
	 * Returns the first asset list entry usage in the ordered set where containerType = &#63; and plid = &#63;.
	 *
	 * @param containerType the container type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry usage, or <code>null</code> if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage fetchByCT_P_First(
		long containerType, long plid,
		OrderByComparator<AssetListEntryUsage> orderByComparator) {

		return _collectionPersistenceFinderByCT_P.fetchFirst(
			finderCache, new Object[] {containerType, plid}, orderByComparator);
	}

	/**
	 * Removes all the asset list entry usages where containerType = &#63; and plid = &#63; from the database.
	 *
	 * @param containerType the container type
	 * @param plid the plid
	 */
	@Override
	public void removeByCT_P(long containerType, long plid) {
		_collectionPersistenceFinderByCT_P.remove(
			finderCache, new Object[] {containerType, plid});
	}

	/**
	 * Returns the number of asset list entry usages where containerType = &#63; and plid = &#63;.
	 *
	 * @param containerType the container type
	 * @param plid the plid
	 * @return the number of matching asset list entry usages
	 */
	@Override
	public int countByCT_P(long containerType, long plid) {
		return _collectionPersistenceFinderByCT_P.count(
			finderCache, new Object[] {containerType, plid});
	}

	private CollectionPersistenceFinder
		<AssetListEntryUsage, NoSuchEntryUsageException>
			_collectionPersistenceFinderByG_C_K;

	/**
	 * Returns an ordered range of all the asset list entry usages where groupId = &#63; and classNameId = &#63; and key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param key the key
	 * @param start the lower bound of the range of asset list entry usages
	 * @param end the upper bound of the range of asset list entry usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry usages
	 */
	@Override
	public List<AssetListEntryUsage> findByG_C_K(
		long groupId, long classNameId, String key, int start, int end,
		OrderByComparator<AssetListEntryUsage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_K.find(
			finderCache, new Object[] {groupId, classNameId, key}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry usage in the ordered set where groupId = &#63; and classNameId = &#63; and key = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry usage
	 * @throws NoSuchEntryUsageException if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage findByG_C_K_First(
			long groupId, long classNameId, String key,
			OrderByComparator<AssetListEntryUsage> orderByComparator)
		throws NoSuchEntryUsageException {

		return _collectionPersistenceFinderByG_C_K.findFirst(
			finderCache, new Object[] {groupId, classNameId, key},
			orderByComparator);
	}

	/**
	 * Returns the first asset list entry usage in the ordered set where groupId = &#63; and classNameId = &#63; and key = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry usage, or <code>null</code> if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage fetchByG_C_K_First(
		long groupId, long classNameId, String key,
		OrderByComparator<AssetListEntryUsage> orderByComparator) {

		return _collectionPersistenceFinderByG_C_K.fetchFirst(
			finderCache, new Object[] {groupId, classNameId, key},
			orderByComparator);
	}

	/**
	 * Removes all the asset list entry usages where groupId = &#63; and classNameId = &#63; and key = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param key the key
	 */
	@Override
	public void removeByG_C_K(long groupId, long classNameId, String key) {
		_collectionPersistenceFinderByG_C_K.remove(
			finderCache, new Object[] {groupId, classNameId, key});
	}

	/**
	 * Returns the number of asset list entry usages where groupId = &#63; and classNameId = &#63; and key = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param key the key
	 * @return the number of matching asset list entry usages
	 */
	@Override
	public int countByG_C_K(long groupId, long classNameId, String key) {
		return _collectionPersistenceFinderByG_C_K.count(
			finderCache, new Object[] {groupId, classNameId, key});
	}

	private CollectionPersistenceFinder
		<AssetListEntryUsage, NoSuchEntryUsageException>
			_collectionPersistenceFinderByC_C_K;

	/**
	 * Returns an ordered range of all the asset list entry usages where companyId = &#63; and classNameId = &#63; and key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param key the key
	 * @param start the lower bound of the range of asset list entry usages
	 * @param end the upper bound of the range of asset list entry usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry usages
	 */
	@Override
	public List<AssetListEntryUsage> findByC_C_K(
		long companyId, long classNameId, String key, int start, int end,
		OrderByComparator<AssetListEntryUsage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_K.find(
			finderCache, new Object[] {companyId, classNameId, key}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry usage in the ordered set where companyId = &#63; and classNameId = &#63; and key = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry usage
	 * @throws NoSuchEntryUsageException if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage findByC_C_K_First(
			long companyId, long classNameId, String key,
			OrderByComparator<AssetListEntryUsage> orderByComparator)
		throws NoSuchEntryUsageException {

		return _collectionPersistenceFinderByC_C_K.findFirst(
			finderCache, new Object[] {companyId, classNameId, key},
			orderByComparator);
	}

	/**
	 * Returns the first asset list entry usage in the ordered set where companyId = &#63; and classNameId = &#63; and key = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry usage, or <code>null</code> if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage fetchByC_C_K_First(
		long companyId, long classNameId, String key,
		OrderByComparator<AssetListEntryUsage> orderByComparator) {

		return _collectionPersistenceFinderByC_C_K.fetchFirst(
			finderCache, new Object[] {companyId, classNameId, key},
			orderByComparator);
	}

	/**
	 * Removes all the asset list entry usages where companyId = &#63; and classNameId = &#63; and key = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param key the key
	 */
	@Override
	public void removeByC_C_K(long companyId, long classNameId, String key) {
		_collectionPersistenceFinderByC_C_K.remove(
			finderCache, new Object[] {companyId, classNameId, key});
	}

	/**
	 * Returns the number of asset list entry usages where companyId = &#63; and classNameId = &#63; and key = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param key the key
	 * @return the number of matching asset list entry usages
	 */
	@Override
	public int countByC_C_K(long companyId, long classNameId, String key) {
		return _collectionPersistenceFinderByC_C_K.count(
			finderCache, new Object[] {companyId, classNameId, key});
	}

	private CollectionPersistenceFinder
		<AssetListEntryUsage, NoSuchEntryUsageException>
			_collectionPersistenceFinderByCK_CT_P;

	/**
	 * Returns an ordered range of all the asset list entry usages where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryUsageModelImpl</code>.
	 * </p>
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param start the lower bound of the range of asset list entry usages
	 * @param end the upper bound of the range of asset list entry usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry usages
	 */
	@Override
	public List<AssetListEntryUsage> findByCK_CT_P(
		String containerKey, long containerType, long plid, int start, int end,
		OrderByComparator<AssetListEntryUsage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCK_CT_P.find(
			finderCache, new Object[] {containerKey, containerType, plid},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry usage in the ordered set where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry usage
	 * @throws NoSuchEntryUsageException if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage findByCK_CT_P_First(
			String containerKey, long containerType, long plid,
			OrderByComparator<AssetListEntryUsage> orderByComparator)
		throws NoSuchEntryUsageException {

		return _collectionPersistenceFinderByCK_CT_P.findFirst(
			finderCache, new Object[] {containerKey, containerType, plid},
			orderByComparator);
	}

	/**
	 * Returns the first asset list entry usage in the ordered set where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry usage, or <code>null</code> if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage fetchByCK_CT_P_First(
		String containerKey, long containerType, long plid,
		OrderByComparator<AssetListEntryUsage> orderByComparator) {

		return _collectionPersistenceFinderByCK_CT_P.fetchFirst(
			finderCache, new Object[] {containerKey, containerType, plid},
			orderByComparator);
	}

	/**
	 * Removes all the asset list entry usages where containerKey = &#63; and containerType = &#63; and plid = &#63; from the database.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 */
	@Override
	public void removeByCK_CT_P(
		String containerKey, long containerType, long plid) {

		_collectionPersistenceFinderByCK_CT_P.remove(
			finderCache, new Object[] {containerKey, containerType, plid});
	}

	/**
	 * Returns the number of asset list entry usages where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @return the number of matching asset list entry usages
	 */
	@Override
	public int countByCK_CT_P(
		String containerKey, long containerType, long plid) {

		return _collectionPersistenceFinderByCK_CT_P.count(
			finderCache, new Object[] {containerKey, containerType, plid});
	}

	private CollectionPersistenceFinder
		<AssetListEntryUsage, NoSuchEntryUsageException>
			_collectionPersistenceFinderByG_C_K_T;

	/**
	 * Returns an ordered range of all the asset list entry usages where groupId = &#63; and classNameId = &#63; and key = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetListEntryUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param key the key
	 * @param type the type
	 * @param start the lower bound of the range of asset list entry usages
	 * @param end the upper bound of the range of asset list entry usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry usages
	 */
	@Override
	public List<AssetListEntryUsage> findByG_C_K_T(
		long groupId, long classNameId, String key, int type, int start,
		int end, OrderByComparator<AssetListEntryUsage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_K_T.find(
			finderCache, new Object[] {groupId, classNameId, key, type}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry usage in the ordered set where groupId = &#63; and classNameId = &#63; and key = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param key the key
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry usage
	 * @throws NoSuchEntryUsageException if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage findByG_C_K_T_First(
			long groupId, long classNameId, String key, int type,
			OrderByComparator<AssetListEntryUsage> orderByComparator)
		throws NoSuchEntryUsageException {

		return _collectionPersistenceFinderByG_C_K_T.findFirst(
			finderCache, new Object[] {groupId, classNameId, key, type},
			orderByComparator);
	}

	/**
	 * Returns the first asset list entry usage in the ordered set where groupId = &#63; and classNameId = &#63; and key = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param key the key
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry usage, or <code>null</code> if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage fetchByG_C_K_T_First(
		long groupId, long classNameId, String key, int type,
		OrderByComparator<AssetListEntryUsage> orderByComparator) {

		return _collectionPersistenceFinderByG_C_K_T.fetchFirst(
			finderCache, new Object[] {groupId, classNameId, key, type},
			orderByComparator);
	}

	/**
	 * Removes all the asset list entry usages where groupId = &#63; and classNameId = &#63; and key = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param key the key
	 * @param type the type
	 */
	@Override
	public void removeByG_C_K_T(
		long groupId, long classNameId, String key, int type) {

		_collectionPersistenceFinderByG_C_K_T.remove(
			finderCache, new Object[] {groupId, classNameId, key, type});
	}

	/**
	 * Returns the number of asset list entry usages where groupId = &#63; and classNameId = &#63; and key = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param key the key
	 * @param type the type
	 * @return the number of matching asset list entry usages
	 */
	@Override
	public int countByG_C_K_T(
		long groupId, long classNameId, String key, int type) {

		return _collectionPersistenceFinderByG_C_K_T.count(
			finderCache, new Object[] {groupId, classNameId, key, type});
	}

	private UniquePersistenceFinder
		<AssetListEntryUsage, NoSuchEntryUsageException>
			_uniquePersistenceFinderByG_C_CK_CT_K_P;

	/**
	 * Returns the asset list entry usage where groupId = &#63; and classNameId = &#63; and containerKey = &#63; and containerType = &#63; and key = &#63; and plid = &#63; or throws a <code>NoSuchEntryUsageException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param key the key
	 * @param plid the plid
	 * @return the matching asset list entry usage
	 * @throws NoSuchEntryUsageException if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage findByG_C_CK_CT_K_P(
			long groupId, long classNameId, String containerKey,
			long containerType, String key, long plid)
		throws NoSuchEntryUsageException {

		return _uniquePersistenceFinderByG_C_CK_CT_K_P.find(
			finderCache,
			new Object[] {
				groupId, classNameId, containerKey, containerType, key, plid
			});
	}

	/**
	 * Returns the asset list entry usage where groupId = &#63; and classNameId = &#63; and containerKey = &#63; and containerType = &#63; and key = &#63; and plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param key the key
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry usage, or <code>null</code> if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage fetchByG_C_CK_CT_K_P(
		long groupId, long classNameId, String containerKey, long containerType,
		String key, long plid, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_C_CK_CT_K_P.fetch(
			finderCache,
			new Object[] {
				groupId, classNameId, containerKey, containerType, key, plid
			},
			useFinderCache);
	}

	/**
	 * Removes the asset list entry usage where groupId = &#63; and classNameId = &#63; and containerKey = &#63; and containerType = &#63; and key = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param key the key
	 * @param plid the plid
	 * @return the asset list entry usage that was removed
	 */
	@Override
	public AssetListEntryUsage removeByG_C_CK_CT_K_P(
			long groupId, long classNameId, String containerKey,
			long containerType, String key, long plid)
		throws NoSuchEntryUsageException {

		AssetListEntryUsage assetListEntryUsage = findByG_C_CK_CT_K_P(
			groupId, classNameId, containerKey, containerType, key, plid);

		return remove(assetListEntryUsage);
	}

	/**
	 * Returns the number of asset list entry usages where groupId = &#63; and classNameId = &#63; and containerKey = &#63; and containerType = &#63; and key = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param key the key
	 * @param plid the plid
	 * @return the number of matching asset list entry usages
	 */
	@Override
	public int countByG_C_CK_CT_K_P(
		long groupId, long classNameId, String containerKey, long containerType,
		String key, long plid) {

		return _uniquePersistenceFinderByG_C_CK_CT_K_P.count(
			finderCache,
			new Object[] {
				groupId, classNameId, containerKey, containerType, key, plid
			});
	}

	public AssetListEntryUsagePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("key", "key_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(AssetListEntryUsage.class);

		setModelImplClass(AssetListEntryUsageImpl.class);
		setModelPKClass(long.class);

		setTable(AssetListEntryUsageTable.INSTANCE);
	}

	/**
	 * Creates a new asset list entry usage with the primary key. Does not add the asset list entry usage to the database.
	 *
	 * @param assetListEntryUsageId the primary key for the new asset list entry usage
	 * @return the new asset list entry usage
	 */
	@Override
	public AssetListEntryUsage create(long assetListEntryUsageId) {
		AssetListEntryUsage assetListEntryUsage = new AssetListEntryUsageImpl();

		assetListEntryUsage.setNew(true);
		assetListEntryUsage.setPrimaryKey(assetListEntryUsageId);

		String uuid = PortalUUIDUtil.generate();

		assetListEntryUsage.setUuid(uuid);

		assetListEntryUsage.setCompanyId(CompanyThreadLocal.getCompanyId());

		return assetListEntryUsage;
	}

	/**
	 * Removes the asset list entry usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param assetListEntryUsageId the primary key of the asset list entry usage
	 * @return the asset list entry usage that was removed
	 * @throws NoSuchEntryUsageException if a asset list entry usage with the primary key could not be found
	 */
	@Override
	public AssetListEntryUsage remove(long assetListEntryUsageId)
		throws NoSuchEntryUsageException {

		return remove((Serializable)assetListEntryUsageId);
	}

	@Override
	protected AssetListEntryUsage removeImpl(
		AssetListEntryUsage assetListEntryUsage) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(assetListEntryUsage)) {
				assetListEntryUsage = (AssetListEntryUsage)session.get(
					AssetListEntryUsageImpl.class,
					assetListEntryUsage.getPrimaryKeyObj());
			}

			if ((assetListEntryUsage != null) &&
				ctPersistenceHelper.isRemove(assetListEntryUsage)) {

				session.delete(assetListEntryUsage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (assetListEntryUsage != null) {
			clearCache(assetListEntryUsage);
		}

		return assetListEntryUsage;
	}

	@Override
	public AssetListEntryUsage updateImpl(
		AssetListEntryUsage assetListEntryUsage) {

		boolean isNew = assetListEntryUsage.isNew();

		if (!(assetListEntryUsage instanceof AssetListEntryUsageModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(assetListEntryUsage.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					assetListEntryUsage);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in assetListEntryUsage proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AssetListEntryUsage implementation " +
					assetListEntryUsage.getClass());
		}

		AssetListEntryUsageModelImpl assetListEntryUsageModelImpl =
			(AssetListEntryUsageModelImpl)assetListEntryUsage;

		if (Validator.isNull(assetListEntryUsage.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			assetListEntryUsage.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (assetListEntryUsage.getCreateDate() == null)) {
			if (serviceContext == null) {
				assetListEntryUsage.setCreateDate(date);
			}
			else {
				assetListEntryUsage.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!assetListEntryUsageModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				assetListEntryUsage.setModifiedDate(date);
			}
			else {
				assetListEntryUsage.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(assetListEntryUsage)) {
				if (!isNew) {
					session.evict(
						AssetListEntryUsageImpl.class,
						assetListEntryUsage.getPrimaryKeyObj());
				}

				session.save(assetListEntryUsage);
			}
			else {
				assetListEntryUsage = (AssetListEntryUsage)session.merge(
					assetListEntryUsage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(assetListEntryUsage, false);

		if (isNew) {
			assetListEntryUsage.setNew(false);
		}

		assetListEntryUsage.resetOriginalValues();

		return assetListEntryUsage;
	}

	/**
	 * Returns the asset list entry usage with the primary key or throws a <code>NoSuchEntryUsageException</code> if it could not be found.
	 *
	 * @param assetListEntryUsageId the primary key of the asset list entry usage
	 * @return the asset list entry usage
	 * @throws NoSuchEntryUsageException if a asset list entry usage with the primary key could not be found
	 */
	@Override
	public AssetListEntryUsage findByPrimaryKey(long assetListEntryUsageId)
		throws NoSuchEntryUsageException {

		return findByPrimaryKey((Serializable)assetListEntryUsageId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the asset list entry usage with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param assetListEntryUsageId the primary key of the asset list entry usage
	 * @return the asset list entry usage, or <code>null</code> if a asset list entry usage with the primary key could not be found
	 */
	@Override
	public AssetListEntryUsage fetchByPrimaryKey(long assetListEntryUsageId) {
		return fetchByPrimaryKey((Serializable)assetListEntryUsageId);
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
		return "assetListEntryUsageId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ASSETLISTENTRYUSAGE;
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
		return AssetListEntryUsageModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AssetListEntryUsage";
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
		ctStrictColumnNames.add("classNameId");
		ctMergeColumnNames.add("containerKey");
		ctMergeColumnNames.add("containerType");
		ctMergeColumnNames.add("key_");
		ctMergeColumnNames.add("plid");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("assetListEntryUsageId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {
				"groupId", "classNameId", "containerKey", "containerType",
				"key_", "plid"
			});
	}

	/**
	 * Initializes the asset list entry usage persistence.
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
			_SQL_SELECT_ASSETLISTENTRYUSAGE_WHERE,
			_SQL_COUNT_ASSETLISTENTRYUSAGE_WHERE,
			AssetListEntryUsageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"assetListEntryUsage.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				AssetListEntryUsage::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(AssetListEntryUsage::getUuid),
				AssetListEntryUsage::getGroupId),
			_SQL_SELECT_ASSETLISTENTRYUSAGE_WHERE, "",
			new FinderColumn<>(
				"assetListEntryUsage.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				AssetListEntryUsage::getUuid),
			new FinderColumn<>(
				"assetListEntryUsage.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, AssetListEntryUsage::getGroupId));

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
				_SQL_SELECT_ASSETLISTENTRYUSAGE_WHERE,
				_SQL_COUNT_ASSETLISTENTRYUSAGE_WHERE,
				AssetListEntryUsageModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"assetListEntryUsage.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					AssetListEntryUsage::getUuid),
				new FinderColumn<>(
					"assetListEntryUsage.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, AssetListEntryUsage::getCompanyId));

		_collectionPersistenceFinderByPlid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPlid",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"plid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByPlid",
				new String[] {Long.class.getName()}, new String[] {"plid"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByPlid",
				new String[] {Long.class.getName()}, new String[] {"plid"},
				false),
			_SQL_SELECT_ASSETLISTENTRYUSAGE_WHERE,
			_SQL_COUNT_ASSETLISTENTRYUSAGE_WHERE,
			AssetListEntryUsageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"assetListEntryUsage.", "plid", FinderColumn.Type.LONG, "=",
				true, true, AssetListEntryUsage::getPlid));

		_collectionPersistenceFinderByCT_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCT_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"containerType", "plid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCT_P",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"containerType", "plid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCT_P",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"containerType", "plid"}, false),
			_SQL_SELECT_ASSETLISTENTRYUSAGE_WHERE,
			_SQL_COUNT_ASSETLISTENTRYUSAGE_WHERE,
			AssetListEntryUsageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"assetListEntryUsage.", "containerType", FinderColumn.Type.LONG,
				"=", true, true, AssetListEntryUsage::getContainerType),
			new FinderColumn<>(
				"assetListEntryUsage.", "plid", FinderColumn.Type.LONG, "=",
				true, true, AssetListEntryUsage::getPlid));

		_collectionPersistenceFinderByG_C_K = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_K",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "classNameId", "key_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_K",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "classNameId", "key_"}, 0, 4, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_K",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "classNameId", "key_"}, 0, 4, false,
				null),
			_SQL_SELECT_ASSETLISTENTRYUSAGE_WHERE,
			_SQL_COUNT_ASSETLISTENTRYUSAGE_WHERE,
			AssetListEntryUsageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"assetListEntryUsage.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, AssetListEntryUsage::getGroupId),
			new FinderColumn<>(
				"assetListEntryUsage.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, AssetListEntryUsage::getClassNameId),
			new FinderColumn<>(
				"assetListEntryUsage.", "key", "key_", FinderColumn.Type.STRING,
				"=", true, true, AssetListEntryUsage::getKey));

		_collectionPersistenceFinderByC_C_K = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_K",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "classNameId", "key_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_K",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "classNameId", "key_"}, 0, 4, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_K",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "classNameId", "key_"}, 0, 4, false,
				null),
			_SQL_SELECT_ASSETLISTENTRYUSAGE_WHERE,
			_SQL_COUNT_ASSETLISTENTRYUSAGE_WHERE,
			AssetListEntryUsageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"assetListEntryUsage.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, AssetListEntryUsage::getCompanyId),
			new FinderColumn<>(
				"assetListEntryUsage.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, AssetListEntryUsage::getClassNameId),
			new FinderColumn<>(
				"assetListEntryUsage.", "key", "key_", FinderColumn.Type.STRING,
				"=", true, true, AssetListEntryUsage::getKey));

		_collectionPersistenceFinderByCK_CT_P =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCK_CT_P",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"containerKey", "containerType", "plid"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCK_CT_P",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"containerKey", "containerType", "plid"}, 0,
					1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCK_CT_P",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"containerKey", "containerType", "plid"}, 0,
					1, false, null),
				_SQL_SELECT_ASSETLISTENTRYUSAGE_WHERE,
				_SQL_COUNT_ASSETLISTENTRYUSAGE_WHERE,
				AssetListEntryUsageModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"assetListEntryUsage.", "containerKey",
					FinderColumn.Type.STRING, "=", true, true,
					AssetListEntryUsage::getContainerKey),
				new FinderColumn<>(
					"assetListEntryUsage.", "containerType",
					FinderColumn.Type.LONG, "=", true, true,
					AssetListEntryUsage::getContainerType),
				new FinderColumn<>(
					"assetListEntryUsage.", "plid", FinderColumn.Type.LONG, "=",
					true, true, AssetListEntryUsage::getPlid));

		_collectionPersistenceFinderByG_C_K_T =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_K_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "classNameId", "key_", "type_"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_K_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "classNameId", "key_", "type_"}, 0,
					4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_K_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "classNameId", "key_", "type_"}, 0,
					4, false, null),
				_SQL_SELECT_ASSETLISTENTRYUSAGE_WHERE,
				_SQL_COUNT_ASSETLISTENTRYUSAGE_WHERE,
				AssetListEntryUsageModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"assetListEntryUsage.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, AssetListEntryUsage::getGroupId),
				new FinderColumn<>(
					"assetListEntryUsage.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetListEntryUsage::getClassNameId),
				new FinderColumn<>(
					"assetListEntryUsage.", "key", "key_",
					FinderColumn.Type.STRING, "=", true, true,
					AssetListEntryUsage::getKey),
				new FinderColumn<>(
					"assetListEntryUsage.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					AssetListEntryUsage::getType));

		_uniquePersistenceFinderByG_C_CK_CT_K_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_C_CK_CT_K_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName(), Long.class.getName(),
					String.class.getName(), Long.class.getName()
				},
				new String[] {
					"groupId", "classNameId", "containerKey", "containerType",
					"key_", "plid"
				},
				0, 20, false, AssetListEntryUsage::getGroupId,
				AssetListEntryUsage::getClassNameId,
				convertNullFunction(AssetListEntryUsage::getContainerKey),
				AssetListEntryUsage::getContainerType,
				convertNullFunction(AssetListEntryUsage::getKey),
				AssetListEntryUsage::getPlid),
			_SQL_SELECT_ASSETLISTENTRYUSAGE_WHERE, "",
			new FinderColumn<>(
				"assetListEntryUsage.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, AssetListEntryUsage::getGroupId),
			new FinderColumn<>(
				"assetListEntryUsage.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, AssetListEntryUsage::getClassNameId),
			new FinderColumn<>(
				"assetListEntryUsage.", "containerKey",
				FinderColumn.Type.STRING, "=", true, true,
				AssetListEntryUsage::getContainerKey),
			new FinderColumn<>(
				"assetListEntryUsage.", "containerType", FinderColumn.Type.LONG,
				"=", true, true, AssetListEntryUsage::getContainerType),
			new FinderColumn<>(
				"assetListEntryUsage.", "key", "key_", FinderColumn.Type.STRING,
				"=", true, true, AssetListEntryUsage::getKey),
			new FinderColumn<>(
				"assetListEntryUsage.", "plid", FinderColumn.Type.LONG, "=",
				true, true, AssetListEntryUsage::getPlid));

		AssetListEntryUsageUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AssetListEntryUsageUtil.setPersistence(null);

		entityCache.removeCache(AssetListEntryUsageImpl.class.getName());
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
		AssetListEntryUsageModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ASSETLISTENTRYUSAGE =
		"SELECT assetListEntryUsage FROM AssetListEntryUsage assetListEntryUsage";

	private static final String _SQL_SELECT_ASSETLISTENTRYUSAGE_WHERE =
		"SELECT assetListEntryUsage FROM AssetListEntryUsage assetListEntryUsage WHERE ";

	private static final String _SQL_COUNT_ASSETLISTENTRYUSAGE_WHERE =
		"SELECT COUNT(assetListEntryUsage) FROM AssetListEntryUsage assetListEntryUsage WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "key", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-352420783