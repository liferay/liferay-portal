/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.persistence.impl;

import com.liferay.asset.kernel.exception.DuplicateAssetTagExternalReferenceCodeException;
import com.liferay.asset.kernel.exception.NoSuchTagException;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetTagTable;
import com.liferay.asset.kernel.service.persistence.AssetEntryPersistence;
import com.liferay.asset.kernel.service.persistence.AssetTagPersistence;
import com.liferay.asset.kernel.service.persistence.AssetTagUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.BeanReference;
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
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portlet.asset.model.impl.AssetTagImpl;
import com.liferay.portlet.asset.model.impl.AssetTagModelImpl;

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
 * The persistence implementation for the asset tag service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AssetTagPersistenceImpl
	extends BasePersistenceImpl<AssetTag, NoSuchTagException>
	implements AssetTagPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AssetTagUtil</code> to access the asset tag persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AssetTagImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<AssetTag, NoSuchTagException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the asset tags where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset tags
	 */
	@Override
	public List<AssetTag> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetTag> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset tag in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag
	 * @throws NoSuchTagException if a matching asset tag could not be found
	 */
	@Override
	public AssetTag findByUuid_First(
			String uuid, OrderByComparator<AssetTag> orderByComparator)
		throws NoSuchTagException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first asset tag in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag, or <code>null</code> if a matching asset tag could not be found
	 */
	@Override
	public AssetTag fetchByUuid_First(
		String uuid, OrderByComparator<AssetTag> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the asset tags where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of asset tags where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset tags
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private UniquePersistenceFinder<AssetTag, NoSuchTagException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the asset tag where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchTagException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset tag
	 * @throws NoSuchTagException if a matching asset tag could not be found
	 */
	@Override
	public AssetTag findByUUID_G(String uuid, long groupId)
		throws NoSuchTagException {

		return _uniquePersistenceFinderByUUID_G.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	/**
	 * Returns the asset tag where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset tag, or <code>null</code> if a matching asset tag could not be found
	 */
	@Override
	public AssetTag fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId},
			useFinderCache);
	}

	/**
	 * Removes the asset tag where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset tag that was removed
	 */
	@Override
	public AssetTag removeByUUID_G(String uuid, long groupId)
		throws NoSuchTagException {

		AssetTag assetTag = findByUUID_G(uuid, groupId);

		return remove(assetTag);
	}

	/**
	 * Returns the number of asset tags where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset tags
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<AssetTag, NoSuchTagException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the asset tags where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset tags
	 */
	@Override
	public List<AssetTag> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetTag> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset tag in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag
	 * @throws NoSuchTagException if a matching asset tag could not be found
	 */
	@Override
	public AssetTag findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<AssetTag> orderByComparator)
		throws NoSuchTagException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first asset tag in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag, or <code>null</code> if a matching asset tag could not be found
	 */
	@Override
	public AssetTag fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<AssetTag> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the asset tags where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of asset tags where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset tags
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<AssetTag, NoSuchTagException>
		_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the asset tags where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset tags
	 */
	@Override
	public List<AssetTag> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetTag> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset tag in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag
	 * @throws NoSuchTagException if a matching asset tag could not be found
	 */
	@Override
	public AssetTag findByGroupId_First(
			long groupId, OrderByComparator<AssetTag> orderByComparator)
		throws NoSuchTagException {

		AssetTag assetTag = fetchByGroupId_First(groupId, orderByComparator);

		if (assetTag != null) {
			return assetTag;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchTagException(sb.toString());
	}

	/**
	 * Returns the first asset tag in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag, or <code>null</code> if a matching asset tag could not be found
	 */
	@Override
	public AssetTag fetchByGroupId_First(
		long groupId, OrderByComparator<AssetTag> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset tags where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset tags
	 */
	@Override
	public List<AssetTag> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<AssetTag> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ArrayUtil.sortedUnique(groupIds)}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset tags where groupId = &#63; from the database.
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
	 * Returns the number of asset tags where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset tags
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of asset tags where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching asset tags
	 */
	@Override
	public int countByGroupId(long[] groupIds) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ArrayUtil.sortedUnique(groupIds)});
	}

	private CollectionPersistenceFinder<AssetTag, NoSuchTagException>
		_collectionPersistenceFinderByName;

	/**
	 * Returns an ordered range of all the asset tags where name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset tags
	 */
	@Override
	public List<AssetTag> findByName(
		String name, int start, int end,
		OrderByComparator<AssetTag> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByName.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new String[] {name}}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset tag in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag
	 * @throws NoSuchTagException if a matching asset tag could not be found
	 */
	@Override
	public AssetTag findByName_First(
			String name, OrderByComparator<AssetTag> orderByComparator)
		throws NoSuchTagException {

		AssetTag assetTag = fetchByName_First(name, orderByComparator);

		if (assetTag != null) {
			return assetTag;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("name=");
		sb.append(name);

		sb.append("}");

		throw new NoSuchTagException(sb.toString());
	}

	/**
	 * Returns the first asset tag in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag, or <code>null</code> if a matching asset tag could not be found
	 */
	@Override
	public AssetTag fetchByName_First(
		String name, OrderByComparator<AssetTag> orderByComparator) {

		return _collectionPersistenceFinderByName.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new String[] {name}}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset tags where name = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagModelImpl</code>.
	 * </p>
	 *
	 * @param names the names
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset tags
	 */
	@Override
	public List<AssetTag> findByName(
		String[] names, int start, int end,
		OrderByComparator<AssetTag> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByName.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ArrayUtil.sortedUnique(names)}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset tags where name = &#63; from the database.
	 *
	 * @param name the name
	 */
	@Override
	public void removeByName(String name) {
		_collectionPersistenceFinderByName.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new String[] {name}});
	}

	/**
	 * Returns the number of asset tags where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching asset tags
	 */
	@Override
	public int countByName(String name) {
		return _collectionPersistenceFinderByName.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new String[] {name}});
	}

	/**
	 * Returns the number of asset tags where name = any &#63;.
	 *
	 * @param names the names
	 * @return the number of matching asset tags
	 */
	@Override
	public int countByName(String[] names) {
		return _collectionPersistenceFinderByName.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ArrayUtil.sortedUnique(names)});
	}

	private CollectionPersistenceFinder<AssetTag, NoSuchTagException>
		_collectionPersistenceFinderByG_N;

	/**
	 * Returns an ordered range of all the asset tags where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset tags
	 */
	@Override
	public List<AssetTag> findByG_N(
		long groupId, String name, int start, int end,
		OrderByComparator<AssetTag> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_N.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset tag in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag
	 * @throws NoSuchTagException if a matching asset tag could not be found
	 */
	@Override
	public AssetTag findByG_N_First(
			long groupId, String name,
			OrderByComparator<AssetTag> orderByComparator)
		throws NoSuchTagException {

		return _collectionPersistenceFinderByG_N.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name},
			orderByComparator);
	}

	/**
	 * Returns the first asset tag in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag, or <code>null</code> if a matching asset tag could not be found
	 */
	@Override
	public AssetTag fetchByG_N_First(
		long groupId, String name,
		OrderByComparator<AssetTag> orderByComparator) {

		return _collectionPersistenceFinderByG_N.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name},
			orderByComparator);
	}

	/**
	 * Removes all the asset tags where groupId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 */
	@Override
	public void removeByG_N(long groupId, String name) {
		_collectionPersistenceFinderByG_N.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name});
	}

	/**
	 * Returns the number of asset tags where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching asset tags
	 */
	@Override
	public int countByG_N(long groupId, String name) {
		return _collectionPersistenceFinderByG_N.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name});
	}

	private CollectionPersistenceFinder<AssetTag, NoSuchTagException>
		_collectionPersistenceFinderByG_LikeN;

	/**
	 * Returns all the asset tags where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching asset tags
	 */
	@Override
	public List<AssetTag> findByG_LikeN(long groupId, String name) {
		return findByG_LikeN(
			groupId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset tags where groupId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @return the range of matching asset tags
	 */
	@Override
	public List<AssetTag> findByG_LikeN(
		long groupId, String name, int start, int end) {

		return findByG_LikeN(groupId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset tags where groupId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset tags
	 */
	@Override
	public List<AssetTag> findByG_LikeN(
		long groupId, String name, int start, int end,
		OrderByComparator<AssetTag> orderByComparator) {

		return findByG_LikeN(
			groupId, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset tags where groupId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset tags
	 */
	@Override
	public List<AssetTag> findByG_LikeN(
		long groupId, String name, int start, int end,
		OrderByComparator<AssetTag> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}, name}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset tag in the ordered set where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag
	 * @throws NoSuchTagException if a matching asset tag could not be found
	 */
	@Override
	public AssetTag findByG_LikeN_First(
			long groupId, String name,
			OrderByComparator<AssetTag> orderByComparator)
		throws NoSuchTagException {

		AssetTag assetTag = fetchByG_LikeN_First(
			groupId, name, orderByComparator);

		if (assetTag != null) {
			return assetTag;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", nameLIKE");
		sb.append(name);

		sb.append("}");

		throw new NoSuchTagException(sb.toString());
	}

	/**
	 * Returns the first asset tag in the ordered set where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset tag, or <code>null</code> if a matching asset tag could not be found
	 */
	@Override
	public AssetTag fetchByG_LikeN_First(
		long groupId, String name,
		OrderByComparator<AssetTag> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}, name}, orderByComparator);
	}

	/**
	 * Returns all the asset tags where groupId = any &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @return the matching asset tags
	 */
	@Override
	public List<AssetTag> findByG_LikeN(long[] groupIds, String name) {
		return findByG_LikeN(
			groupIds, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset tags where groupId = any &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @return the range of matching asset tags
	 */
	@Override
	public List<AssetTag> findByG_LikeN(
		long[] groupIds, String name, int start, int end) {

		return findByG_LikeN(groupIds, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset tags where groupId = any &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset tags
	 */
	@Override
	public List<AssetTag> findByG_LikeN(
		long[] groupIds, String name, int start, int end,
		OrderByComparator<AssetTag> orderByComparator) {

		return findByG_LikeN(
			groupIds, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset tags where groupId = &#63; and name LIKE &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset tags
	 */
	@Override
	public List<AssetTag> findByG_LikeN(
		long[] groupIds, String name, int start, int end,
		OrderByComparator<AssetTag> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ArrayUtil.sortedUnique(groupIds), name}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset tags where groupId = &#63; and name LIKE &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 */
	@Override
	public void removeByG_LikeN(long groupId, String name) {
		_collectionPersistenceFinderByG_LikeN.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}, name});
	}

	/**
	 * Returns the number of asset tags where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching asset tags
	 */
	@Override
	public int countByG_LikeN(long groupId, String name) {
		return _collectionPersistenceFinderByG_LikeN.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}, name});
	}

	/**
	 * Returns the number of asset tags where groupId = any &#63; and name LIKE &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @return the number of matching asset tags
	 */
	@Override
	public int countByG_LikeN(long[] groupIds, String name) {
		return _collectionPersistenceFinderByG_LikeN.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ArrayUtil.sortedUnique(groupIds), name});
	}

	private UniquePersistenceFinder<AssetTag, NoSuchTagException>
		_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the asset tag where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchTagException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching asset tag
	 * @throws NoSuchTagException if a matching asset tag could not be found
	 */
	@Override
	public AssetTag findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchTagException {

		return _uniquePersistenceFinderByERC_G.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the asset tag where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset tag, or <code>null</code> if a matching asset tag could not be found
	 */
	@Override
	public AssetTag fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId}, useFinderCache);
	}

	/**
	 * Removes the asset tag where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the asset tag that was removed
	 */
	@Override
	public AssetTag removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchTagException {

		AssetTag assetTag = findByERC_G(externalReferenceCode, groupId);

		return remove(assetTag);
	}

	/**
	 * Returns the number of asset tags where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching asset tags
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	public AssetTagPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(AssetTag.class);

		setModelImplClass(AssetTagImpl.class);
		setModelPKClass(long.class);

		setTable(AssetTagTable.INSTANCE);
	}

	/**
	 * Creates a new asset tag with the primary key. Does not add the asset tag to the database.
	 *
	 * @param tagId the primary key for the new asset tag
	 * @return the new asset tag
	 */
	@Override
	public AssetTag create(long tagId) {
		AssetTag assetTag = new AssetTagImpl();

		assetTag.setNew(true);
		assetTag.setPrimaryKey(tagId);

		String uuid = PortalUUIDUtil.generate();

		assetTag.setUuid(uuid);

		assetTag.setCompanyId(CompanyThreadLocal.getCompanyId());

		return assetTag;
	}

	/**
	 * Removes the asset tag with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param tagId the primary key of the asset tag
	 * @return the asset tag that was removed
	 * @throws NoSuchTagException if a asset tag with the primary key could not be found
	 */
	@Override
	public AssetTag remove(long tagId) throws NoSuchTagException {
		return remove((Serializable)tagId);
	}

	@Override
	protected AssetTag removeImpl(AssetTag assetTag) {
		assetTagToAssetEntryTableMapper.deleteLeftPrimaryKeyTableMappings(
			assetTag.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(assetTag)) {
				assetTag = (AssetTag)session.get(
					AssetTagImpl.class, assetTag.getPrimaryKeyObj());
			}

			if ((assetTag != null) &&
				CTPersistenceHelperUtil.isRemove(assetTag)) {

				session.delete(assetTag);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (assetTag != null) {
			clearCache(assetTag);
		}

		return assetTag;
	}

	@Override
	public AssetTag updateImpl(AssetTag assetTag) {
		boolean isNew = assetTag.isNew();

		if (!(assetTag instanceof AssetTagModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(assetTag.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(assetTag);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in assetTag proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AssetTag implementation " +
					assetTag.getClass());
		}

		AssetTagModelImpl assetTagModelImpl = (AssetTagModelImpl)assetTag;

		if (Validator.isNull(assetTag.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			assetTag.setUuid(uuid);
		}

		if (Validator.isNull(assetTag.getExternalReferenceCode())) {
			assetTag.setExternalReferenceCode(assetTag.getUuid());
		}
		else {
			if (!Objects.equals(
					assetTagModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					assetTag.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = assetTag.getCompanyId();

					long groupId = assetTag.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = assetTag.getPrimaryKey();
					}

					try {
						assetTag.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								AssetTag.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								assetTag.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			AssetTag ercAssetTag = fetchByERC_G(
				assetTag.getExternalReferenceCode(), assetTag.getGroupId());

			if (isNew) {
				if (ercAssetTag != null) {
					throw new DuplicateAssetTagExternalReferenceCodeException(
						"Duplicate asset tag with external reference code " +
							assetTag.getExternalReferenceCode() +
								" and group " + assetTag.getGroupId());
				}
			}
			else {
				if ((ercAssetTag != null) &&
					(assetTag.getTagId() != ercAssetTag.getTagId())) {

					throw new DuplicateAssetTagExternalReferenceCodeException(
						"Duplicate asset tag with external reference code " +
							assetTag.getExternalReferenceCode() +
								" and group " + assetTag.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (assetTag.getCreateDate() == null)) {
			if (serviceContext == null) {
				assetTag.setCreateDate(date);
			}
			else {
				assetTag.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!assetTagModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				assetTag.setModifiedDate(date);
			}
			else {
				assetTag.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(assetTag)) {
				if (!isNew) {
					session.evict(
						AssetTagImpl.class, assetTag.getPrimaryKeyObj());
				}

				session.save(assetTag);
			}
			else {
				assetTag = (AssetTag)session.merge(assetTag);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(assetTag, false);

		if (isNew) {
			assetTag.setNew(false);
		}

		assetTag.resetOriginalValues();

		return assetTag;
	}

	/**
	 * Returns the asset tag with the primary key or throws a <code>NoSuchTagException</code> if it could not be found.
	 *
	 * @param tagId the primary key of the asset tag
	 * @return the asset tag
	 * @throws NoSuchTagException if a asset tag with the primary key could not be found
	 */
	@Override
	public AssetTag findByPrimaryKey(long tagId) throws NoSuchTagException {
		return findByPrimaryKey((Serializable)tagId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the asset tag with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param tagId the primary key of the asset tag
	 * @return the asset tag, or <code>null</code> if a asset tag with the primary key could not be found
	 */
	@Override
	public AssetTag fetchByPrimaryKey(long tagId) {
		return fetchByPrimaryKey((Serializable)tagId);
	}

	/**
	 * Returns the primaryKeys of asset entries associated with the asset tag.
	 *
	 * @param pk the primary key of the asset tag
	 * @return long[] of the primaryKeys of asset entries associated with the asset tag
	 */
	@Override
	public long[] getAssetEntryPrimaryKeys(long pk) {
		long[] pks = assetTagToAssetEntryTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the asset entries associated with the asset tag.
	 *
	 * @param pk the primary key of the asset tag
	 * @return the asset entries associated with the asset tag
	 */
	@Override
	public List<com.liferay.asset.kernel.model.AssetEntry> getAssetEntries(
		long pk) {

		return getAssetEntries(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the asset entries associated with the asset tag.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the asset tag
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @return the range of asset entries associated with the asset tag
	 */
	@Override
	public List<com.liferay.asset.kernel.model.AssetEntry> getAssetEntries(
		long pk, int start, int end) {

		return getAssetEntries(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset entries associated with the asset tag.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetTagModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the asset tag
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of asset entries associated with the asset tag
	 */
	@Override
	public List<com.liferay.asset.kernel.model.AssetEntry> getAssetEntries(
		long pk, int start, int end,
		OrderByComparator<com.liferay.asset.kernel.model.AssetEntry>
			orderByComparator) {

		return assetTagToAssetEntryTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset entries associated with the asset tag.
	 *
	 * @param pk the primary key of the asset tag
	 * @return the number of asset entries associated with the asset tag
	 */
	@Override
	public int getAssetEntriesSize(long pk) {
		long[] pks = assetTagToAssetEntryTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the asset entry is associated with the asset tag.
	 *
	 * @param pk the primary key of the asset tag
	 * @param assetEntryPK the primary key of the asset entry
	 * @return <code>true</code> if the asset entry is associated with the asset tag; <code>false</code> otherwise
	 */
	@Override
	public boolean containsAssetEntry(long pk, long assetEntryPK) {
		return assetTagToAssetEntryTableMapper.containsTableMapping(
			pk, assetEntryPK);
	}

	/**
	 * Returns <code>true</code> if the asset tag has any asset entries associated with it.
	 *
	 * @param pk the primary key of the asset tag to check for associations with asset entries
	 * @return <code>true</code> if the asset tag has any asset entries associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsAssetEntries(long pk) {
		if (getAssetEntriesSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the asset tag and the asset entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset tag
	 * @param assetEntryPK the primary key of the asset entry
	 * @return <code>true</code> if an association between the asset tag and the asset entry was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addAssetEntry(long pk, long assetEntryPK) {
		AssetTag assetTag = fetchByPrimaryKey(pk);

		if (assetTag == null) {
			return assetTagToAssetEntryTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, assetEntryPK);
		}
		else {
			return assetTagToAssetEntryTableMapper.addTableMapping(
				assetTag.getCompanyId(), pk, assetEntryPK);
		}
	}

	/**
	 * Adds an association between the asset tag and the asset entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset tag
	 * @param assetEntry the asset entry
	 * @return <code>true</code> if an association between the asset tag and the asset entry was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addAssetEntry(
		long pk, com.liferay.asset.kernel.model.AssetEntry assetEntry) {

		AssetTag assetTag = fetchByPrimaryKey(pk);

		if (assetTag == null) {
			return assetTagToAssetEntryTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				assetEntry.getPrimaryKey());
		}
		else {
			return assetTagToAssetEntryTableMapper.addTableMapping(
				assetTag.getCompanyId(), pk, assetEntry.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the asset tag and the asset entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset tag
	 * @param assetEntryPKs the primary keys of the asset entries
	 * @return <code>true</code> if at least one association between the asset tag and the asset entries was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addAssetEntries(long pk, long[] assetEntryPKs) {
		long companyId = 0;

		AssetTag assetTag = fetchByPrimaryKey(pk);

		if (assetTag == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = assetTag.getCompanyId();
		}

		long[] addedKeys = assetTagToAssetEntryTableMapper.addTableMappings(
			companyId, pk, assetEntryPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the asset tag and the asset entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset tag
	 * @param assetEntries the asset entries
	 * @return <code>true</code> if at least one association between the asset tag and the asset entries was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addAssetEntries(
		long pk, List<com.liferay.asset.kernel.model.AssetEntry> assetEntries) {

		return addAssetEntries(
			pk,
			ListUtil.toLongArray(
				assetEntries,
				com.liferay.asset.kernel.model.AssetEntry.ENTRY_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the asset tag and its asset entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset tag to clear the associated asset entries from
	 */
	@Override
	public void clearAssetEntries(long pk) {
		assetTagToAssetEntryTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the asset tag and the asset entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset tag
	 * @param assetEntryPK the primary key of the asset entry
	 */
	@Override
	public void removeAssetEntry(long pk, long assetEntryPK) {
		assetTagToAssetEntryTableMapper.deleteTableMapping(pk, assetEntryPK);
	}

	/**
	 * Removes the association between the asset tag and the asset entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset tag
	 * @param assetEntry the asset entry
	 */
	@Override
	public void removeAssetEntry(
		long pk, com.liferay.asset.kernel.model.AssetEntry assetEntry) {

		assetTagToAssetEntryTableMapper.deleteTableMapping(
			pk, assetEntry.getPrimaryKey());
	}

	/**
	 * Removes the association between the asset tag and the asset entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset tag
	 * @param assetEntryPKs the primary keys of the asset entries
	 */
	@Override
	public void removeAssetEntries(long pk, long[] assetEntryPKs) {
		assetTagToAssetEntryTableMapper.deleteTableMappings(pk, assetEntryPKs);
	}

	/**
	 * Removes the association between the asset tag and the asset entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset tag
	 * @param assetEntries the asset entries
	 */
	@Override
	public void removeAssetEntries(
		long pk, List<com.liferay.asset.kernel.model.AssetEntry> assetEntries) {

		removeAssetEntries(
			pk,
			ListUtil.toLongArray(
				assetEntries,
				com.liferay.asset.kernel.model.AssetEntry.ENTRY_ID_ACCESSOR));
	}

	/**
	 * Sets the asset entries associated with the asset tag, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset tag
	 * @param assetEntryPKs the primary keys of the asset entries to be associated with the asset tag
	 */
	@Override
	public void setAssetEntries(long pk, long[] assetEntryPKs) {
		Set<Long> newAssetEntryPKsSet = SetUtil.fromArray(assetEntryPKs);
		Set<Long> oldAssetEntryPKsSet = SetUtil.fromArray(
			assetTagToAssetEntryTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeAssetEntryPKsSet = new HashSet<Long>(
			oldAssetEntryPKsSet);

		removeAssetEntryPKsSet.removeAll(newAssetEntryPKsSet);

		assetTagToAssetEntryTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeAssetEntryPKsSet));

		newAssetEntryPKsSet.removeAll(oldAssetEntryPKsSet);

		long companyId = 0;

		AssetTag assetTag = fetchByPrimaryKey(pk);

		if (assetTag == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = assetTag.getCompanyId();
		}

		assetTagToAssetEntryTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newAssetEntryPKsSet));
	}

	/**
	 * Sets the asset entries associated with the asset tag, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset tag
	 * @param assetEntries the asset entries to be associated with the asset tag
	 */
	@Override
	public void setAssetEntries(
		long pk, List<com.liferay.asset.kernel.model.AssetEntry> assetEntries) {

		try {
			long[] assetEntryPKs = new long[assetEntries.size()];

			for (int i = 0; i < assetEntries.size(); i++) {
				com.liferay.asset.kernel.model.AssetEntry assetEntry =
					assetEntries.get(i);

				assetEntryPKs[i] = assetEntry.getPrimaryKey();
			}

			setAssetEntries(pk, assetEntryPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
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
		return "tagId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ASSETTAG;
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
		return AssetTagModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AssetTag";
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
		ctMergeColumnNames.add("assetCount");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("entries");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("tagId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_mappingTableNames.add("AssetEntries_AssetTags");

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the asset tag persistence.
	 */
	public void afterPropertiesSet() {
		assetTagToAssetEntryTableMapper = TableMapperFactory.getTableMapper(
			"AssetEntries_AssetTags", "companyId", "tagId", "entryId", this,
			assetEntryPersistence);

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
			_SQL_SELECT_ASSETTAG_WHERE, _SQL_COUNT_ASSETTAG_WHERE,
			AssetTagModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"assetTag.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, AssetTag::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(AssetTag::getUuid), AssetTag::getGroupId),
			_SQL_SELECT_ASSETTAG_WHERE, "",
			new FinderColumn<>(
				"assetTag.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, AssetTag::getUuid),
			new FinderColumn<>(
				"assetTag.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				AssetTag::getGroupId));

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
				_SQL_SELECT_ASSETTAG_WHERE, _SQL_COUNT_ASSETTAG_WHERE,
				AssetTagModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"assetTag.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, AssetTag::getUuid),
				new FinderColumn<>(
					"assetTag.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, AssetTag::getCompanyId));

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
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_ASSETTAG_WHERE, _SQL_COUNT_ASSETTAG_WHERE,
				AssetTagModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new ArrayableFinderColumn<>(
					"assetTag.", "groupId", FinderColumn.Type.LONG, "=", false,
					true, true, AssetTag::getGroupId));

		_collectionPersistenceFinderByName = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByName",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"name"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByName",
				new String[] {String.class.getName()}, new String[] {"name"}, 1,
				1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByName",
				new String[] {String.class.getName()}, new String[] {"name"}, 1,
				1, false, null),
			_SQL_SELECT_ASSETTAG_WHERE, _SQL_COUNT_ASSETTAG_WHERE,
			AssetTagModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new ArrayableFinderColumn<>(
				"assetTag.", "name", FinderColumn.Type.STRING, "=", false,
				false, true, AssetTag::getName));

		_collectionPersistenceFinderByG_N = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "name"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "name"}, 2, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "name"}, 2, 2, false, null),
			_SQL_SELECT_ASSETTAG_WHERE, _SQL_COUNT_ASSETTAG_WHERE,
			AssetTagModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"assetTag.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				AssetTag::getGroupId),
			new FinderColumn<>(
				"assetTag.", "name", FinderColumn.Type.STRING, "=", false, true,
				AssetTag::getName));

		_collectionPersistenceFinderByG_LikeN =
			new CollectionPersistenceFinder<>(
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
				_SQL_SELECT_ASSETTAG_WHERE, _SQL_COUNT_ASSETTAG_WHERE,
				AssetTagModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new ArrayableFinderColumn<>(
					"assetTag.", "groupId", FinderColumn.Type.LONG, "=", false,
					true, true, AssetTag::getGroupId),
				new FinderColumn<>(
					"assetTag.", "name", FinderColumn.Type.STRING, "LIKE",
					false, true, AssetTag::getName));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(AssetTag::getExternalReferenceCode),
				AssetTag::getGroupId),
			_SQL_SELECT_ASSETTAG_WHERE, "",
			new FinderColumn<>(
				"assetTag.", "externalReferenceCode", FinderColumn.Type.STRING,
				"=", true, true, AssetTag::getExternalReferenceCode),
			new FinderColumn<>(
				"assetTag.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				AssetTag::getGroupId));

		AssetTagUtil.setPersistence(this);
	}

	public void destroy() {
		AssetTagUtil.setPersistence(null);

		EntityCacheUtil.removeCache(AssetTagImpl.class.getName());

		TableMapperFactory.removeTableMapper("AssetEntries_AssetTags");
	}

	@BeanReference(type = AssetEntryPersistence.class)
	protected AssetEntryPersistence assetEntryPersistence;

	protected TableMapper<AssetTag, com.liferay.asset.kernel.model.AssetEntry>
		assetTagToAssetEntryTableMapper;

	private static final String _ENTITY_ALIAS_PREFIX =
		AssetTagModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ASSETTAG =
		"SELECT assetTag FROM AssetTag assetTag";

	private static final String _SQL_SELECT_ASSETTAG_WHERE =
		"SELECT assetTag FROM AssetTag assetTag WHERE ";

	private static final String _SQL_COUNT_ASSETTAG_WHERE =
		"SELECT COUNT(assetTag) FROM AssetTag assetTag WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No AssetTag exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		AssetTagPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2008529728