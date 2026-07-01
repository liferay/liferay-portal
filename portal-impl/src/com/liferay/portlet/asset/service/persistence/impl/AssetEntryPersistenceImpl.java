/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.persistence.impl;

import com.liferay.asset.kernel.exception.NoSuchEntryException;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetEntryTable;
import com.liferay.asset.kernel.service.persistence.AssetEntryPersistence;
import com.liferay.asset.kernel.service.persistence.AssetEntryUtil;
import com.liferay.asset.kernel.service.persistence.AssetTagPersistence;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portlet.asset.model.impl.AssetEntryImpl;
import com.liferay.portlet.asset.model.impl.AssetEntryModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the asset entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AssetEntryPersistenceImpl
	extends BasePersistenceImpl<AssetEntry, NoSuchEntryException>
	implements AssetEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AssetEntryUtil</code> to access the asset entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AssetEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<AssetEntry, NoSuchEntryException>
		_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the asset entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset entries
	 */
	@Override
	public List<AssetEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry
	 * @throws NoSuchEntryException if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry findByGroupId_First(
			long groupId, OrderByComparator<AssetEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first asset entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry fetchByGroupId_First(
		long groupId, OrderByComparator<AssetEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Removes all the asset entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of asset entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	private CollectionPersistenceFinder<AssetEntry, NoSuchEntryException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the asset entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset entries
	 */
	@Override
	public List<AssetEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AssetEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry
	 * @throws NoSuchEntryException if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry findByCompanyId_First(
			long companyId, OrderByComparator<AssetEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first asset entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<AssetEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the asset entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of asset entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching asset entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private CollectionPersistenceFinder<AssetEntry, NoSuchEntryException>
		_collectionPersistenceFinderByClassUuid;

	/**
	 * Returns an ordered range of all the asset entries where classUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classUuid the class uuid
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset entries
	 */
	@Override
	public List<AssetEntry> findByClassUuid(
		String classUuid, int start, int end,
		OrderByComparator<AssetEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByClassUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {classUuid}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset entry in the ordered set where classUuid = &#63;.
	 *
	 * @param classUuid the class uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry
	 * @throws NoSuchEntryException if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry findByClassUuid_First(
			String classUuid, OrderByComparator<AssetEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByClassUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {classUuid},
			orderByComparator);
	}

	/**
	 * Returns the first asset entry in the ordered set where classUuid = &#63;.
	 *
	 * @param classUuid the class uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry fetchByClassUuid_First(
		String classUuid, OrderByComparator<AssetEntry> orderByComparator) {

		return _collectionPersistenceFinderByClassUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {classUuid},
			orderByComparator);
	}

	/**
	 * Removes all the asset entries where classUuid = &#63; from the database.
	 *
	 * @param classUuid the class uuid
	 */
	@Override
	public void removeByClassUuid(String classUuid) {
		_collectionPersistenceFinderByClassUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {classUuid});
	}

	/**
	 * Returns the number of asset entries where classUuid = &#63;.
	 *
	 * @param classUuid the class uuid
	 * @return the number of matching asset entries
	 */
	@Override
	public int countByClassUuid(String classUuid) {
		return _collectionPersistenceFinderByClassUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {classUuid});
	}

	private CollectionPersistenceFinder<AssetEntry, NoSuchEntryException>
		_collectionPersistenceFinderByVisible;

	/**
	 * Returns an ordered range of all the asset entries where visible = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param visible the visible
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset entries
	 */
	@Override
	public List<AssetEntry> findByVisible(
		boolean visible, int start, int end,
		OrderByComparator<AssetEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByVisible.find(
			FinderCacheUtil.getFinderCache(), new Object[] {visible}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset entry in the ordered set where visible = &#63;.
	 *
	 * @param visible the visible
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry
	 * @throws NoSuchEntryException if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry findByVisible_First(
			boolean visible, OrderByComparator<AssetEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByVisible.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {visible},
			orderByComparator);
	}

	/**
	 * Returns the first asset entry in the ordered set where visible = &#63;.
	 *
	 * @param visible the visible
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry fetchByVisible_First(
		boolean visible, OrderByComparator<AssetEntry> orderByComparator) {

		return _collectionPersistenceFinderByVisible.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {visible},
			orderByComparator);
	}

	/**
	 * Removes all the asset entries where visible = &#63; from the database.
	 *
	 * @param visible the visible
	 */
	@Override
	public void removeByVisible(boolean visible) {
		_collectionPersistenceFinderByVisible.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {visible});
	}

	/**
	 * Returns the number of asset entries where visible = &#63;.
	 *
	 * @param visible the visible
	 * @return the number of matching asset entries
	 */
	@Override
	public int countByVisible(boolean visible) {
		return _collectionPersistenceFinderByVisible.count(
			FinderCacheUtil.getFinderCache(), new Object[] {visible});
	}

	private CollectionPersistenceFinder<AssetEntry, NoSuchEntryException>
		_collectionPersistenceFinderByPublishDate;

	/**
	 * Returns an ordered range of all the asset entries where publishDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param publishDate the publish date
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset entries
	 */
	@Override
	public List<AssetEntry> findByPublishDate(
		Date publishDate, int start, int end,
		OrderByComparator<AssetEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPublishDate.find(
			FinderCacheUtil.getFinderCache(), new Object[] {publishDate}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset entry in the ordered set where publishDate = &#63;.
	 *
	 * @param publishDate the publish date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry
	 * @throws NoSuchEntryException if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry findByPublishDate_First(
			Date publishDate, OrderByComparator<AssetEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByPublishDate.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {publishDate},
			orderByComparator);
	}

	/**
	 * Returns the first asset entry in the ordered set where publishDate = &#63;.
	 *
	 * @param publishDate the publish date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry fetchByPublishDate_First(
		Date publishDate, OrderByComparator<AssetEntry> orderByComparator) {

		return _collectionPersistenceFinderByPublishDate.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {publishDate},
			orderByComparator);
	}

	/**
	 * Removes all the asset entries where publishDate = &#63; from the database.
	 *
	 * @param publishDate the publish date
	 */
	@Override
	public void removeByPublishDate(Date publishDate) {
		_collectionPersistenceFinderByPublishDate.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {publishDate});
	}

	/**
	 * Returns the number of asset entries where publishDate = &#63;.
	 *
	 * @param publishDate the publish date
	 * @return the number of matching asset entries
	 */
	@Override
	public int countByPublishDate(Date publishDate) {
		return _collectionPersistenceFinderByPublishDate.count(
			FinderCacheUtil.getFinderCache(), new Object[] {publishDate});
	}

	private CollectionPersistenceFinder<AssetEntry, NoSuchEntryException>
		_collectionPersistenceFinderByExpirationDate;

	/**
	 * Returns an ordered range of all the asset entries where expirationDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset entries
	 */
	@Override
	public List<AssetEntry> findByExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<AssetEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByExpirationDate.find(
			FinderCacheUtil.getFinderCache(), new Object[] {expirationDate},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset entry in the ordered set where expirationDate = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry
	 * @throws NoSuchEntryException if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry findByExpirationDate_First(
			Date expirationDate,
			OrderByComparator<AssetEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByExpirationDate.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {expirationDate},
			orderByComparator);
	}

	/**
	 * Returns the first asset entry in the ordered set where expirationDate = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry fetchByExpirationDate_First(
		Date expirationDate, OrderByComparator<AssetEntry> orderByComparator) {

		return _collectionPersistenceFinderByExpirationDate.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {expirationDate},
			orderByComparator);
	}

	/**
	 * Removes all the asset entries where expirationDate = &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 */
	@Override
	public void removeByExpirationDate(Date expirationDate) {
		_collectionPersistenceFinderByExpirationDate.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {expirationDate});
	}

	/**
	 * Returns the number of asset entries where expirationDate = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the number of matching asset entries
	 */
	@Override
	public int countByExpirationDate(Date expirationDate) {
		return _collectionPersistenceFinderByExpirationDate.count(
			FinderCacheUtil.getFinderCache(), new Object[] {expirationDate});
	}

	private CollectionPersistenceFinder<AssetEntry, NoSuchEntryException>
		_collectionPersistenceFinderByLayoutUuid;

	/**
	 * Returns an ordered range of all the asset entries where layoutUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset entries
	 */
	@Override
	public List<AssetEntry> findByLayoutUuid(
		String layoutUuid, int start, int end,
		OrderByComparator<AssetEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLayoutUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutUuid}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset entry in the ordered set where layoutUuid = &#63;.
	 *
	 * @param layoutUuid the layout uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry
	 * @throws NoSuchEntryException if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry findByLayoutUuid_First(
			String layoutUuid, OrderByComparator<AssetEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByLayoutUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutUuid},
			orderByComparator);
	}

	/**
	 * Returns the first asset entry in the ordered set where layoutUuid = &#63;.
	 *
	 * @param layoutUuid the layout uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry fetchByLayoutUuid_First(
		String layoutUuid, OrderByComparator<AssetEntry> orderByComparator) {

		return _collectionPersistenceFinderByLayoutUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutUuid},
			orderByComparator);
	}

	/**
	 * Removes all the asset entries where layoutUuid = &#63; from the database.
	 *
	 * @param layoutUuid the layout uuid
	 */
	@Override
	public void removeByLayoutUuid(String layoutUuid) {
		_collectionPersistenceFinderByLayoutUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutUuid});
	}

	/**
	 * Returns the number of asset entries where layoutUuid = &#63;.
	 *
	 * @param layoutUuid the layout uuid
	 * @return the number of matching asset entries
	 */
	@Override
	public int countByLayoutUuid(String layoutUuid) {
		return _collectionPersistenceFinderByLayoutUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutUuid});
	}

	private UniquePersistenceFinder<AssetEntry, NoSuchEntryException>
		_uniquePersistenceFinderByG_CU;

	/**
	 * Returns the asset entry where groupId = &#63; and classUuid = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classUuid the class uuid
	 * @return the matching asset entry
	 * @throws NoSuchEntryException if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry findByG_CU(long groupId, String classUuid)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByG_CU.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classUuid});
	}

	/**
	 * Returns the asset entry where groupId = &#63; and classUuid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classUuid the class uuid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry fetchByG_CU(
		long groupId, String classUuid, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_CU.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, classUuid},
			useFinderCache);
	}

	/**
	 * Removes the asset entry where groupId = &#63; and classUuid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classUuid the class uuid
	 * @return the asset entry that was removed
	 */
	@Override
	public AssetEntry removeByG_CU(long groupId, String classUuid)
		throws NoSuchEntryException {

		AssetEntry assetEntry = findByG_CU(groupId, classUuid);

		return remove(assetEntry);
	}

	/**
	 * Returns the number of asset entries where groupId = &#63; and classUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classUuid the class uuid
	 * @return the number of matching asset entries
	 */
	@Override
	public int countByG_CU(long groupId, String classUuid) {
		return _uniquePersistenceFinderByG_CU.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classUuid});
	}

	private CollectionPersistenceFinder<AssetEntry, NoSuchEntryException>
		_collectionPersistenceFinderByC_CN;

	/**
	 * Returns an ordered range of all the asset entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset entries
	 */
	@Override
	public List<AssetEntry> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<AssetEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CN.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry
	 * @throws NoSuchEntryException if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry findByC_CN_First(
			long companyId, long classNameId,
			OrderByComparator<AssetEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_CN.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, orderByComparator);
	}

	/**
	 * Returns the first asset entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry fetchByC_CN_First(
		long companyId, long classNameId,
		OrderByComparator<AssetEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_CN.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, orderByComparator);
	}

	/**
	 * Removes all the asset entries where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_CN(long companyId, long classNameId) {
		_collectionPersistenceFinderByC_CN.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId});
	}

	/**
	 * Returns the number of asset entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching asset entries
	 */
	@Override
	public int countByC_CN(long companyId, long classNameId) {
		return _collectionPersistenceFinderByC_CN.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId});
	}

	private UniquePersistenceFinder<AssetEntry, NoSuchEntryException>
		_uniquePersistenceFinderByC_C;

	/**
	 * Returns the asset entry where classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching asset entry
	 * @throws NoSuchEntryException if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry findByC_C(long classNameId, long classPK)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the asset entry where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry fetchByC_C(
		long classNameId, long classPK, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, useFinderCache);
	}

	/**
	 * Removes the asset entry where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the asset entry that was removed
	 */
	@Override
	public AssetEntry removeByC_C(long classNameId, long classPK)
		throws NoSuchEntryException {

		AssetEntry assetEntry = findByC_C(classNameId, classPK);

		return remove(assetEntry);
	}

	/**
	 * Returns the number of asset entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching asset entries
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _uniquePersistenceFinderByC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK});
	}

	private CollectionPersistenceFinder<AssetEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_C_V;

	/**
	 * Returns an ordered range of all the asset entries where groupId = &#63; and classNameId = &#63; and visible = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param visible the visible
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset entries
	 */
	@Override
	public List<AssetEntry> findByG_C_V(
		long groupId, long classNameId, boolean visible, int start, int end,
		OrderByComparator<AssetEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_V.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, visible}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset entry in the ordered set where groupId = &#63; and classNameId = &#63; and visible = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param visible the visible
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry
	 * @throws NoSuchEntryException if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry findByG_C_V_First(
			long groupId, long classNameId, boolean visible,
			OrderByComparator<AssetEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_C_V.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, visible}, orderByComparator);
	}

	/**
	 * Returns the first asset entry in the ordered set where groupId = &#63; and classNameId = &#63; and visible = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param visible the visible
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry fetchByG_C_V_First(
		long groupId, long classNameId, boolean visible,
		OrderByComparator<AssetEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_V.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, visible}, orderByComparator);
	}

	/**
	 * Removes all the asset entries where groupId = &#63; and classNameId = &#63; and visible = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param visible the visible
	 */
	@Override
	public void removeByG_C_V(long groupId, long classNameId, boolean visible) {
		_collectionPersistenceFinderByG_C_V.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, visible});
	}

	/**
	 * Returns the number of asset entries where groupId = &#63; and classNameId = &#63; and visible = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param visible the visible
	 * @return the number of matching asset entries
	 */
	@Override
	public int countByG_C_V(long groupId, long classNameId, boolean visible) {
		return _collectionPersistenceFinderByG_C_V.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, visible});
	}

	private CollectionPersistenceFinder<AssetEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_C_P_E;

	/**
	 * Returns an ordered range of all the asset entries where groupId = &#63; and classNameId = &#63; and publishDate = &#63; and expirationDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param publishDate the publish date
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset entries
	 */
	@Override
	public List<AssetEntry> findByG_C_P_E(
		long groupId, long classNameId, Date publishDate, Date expirationDate,
		int start, int end, OrderByComparator<AssetEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_P_E.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, publishDate, expirationDate},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset entry in the ordered set where groupId = &#63; and classNameId = &#63; and publishDate = &#63; and expirationDate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param publishDate the publish date
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry
	 * @throws NoSuchEntryException if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry findByG_C_P_E_First(
			long groupId, long classNameId, Date publishDate,
			Date expirationDate,
			OrderByComparator<AssetEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_C_P_E.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, publishDate, expirationDate},
			orderByComparator);
	}

	/**
	 * Returns the first asset entry in the ordered set where groupId = &#63; and classNameId = &#63; and publishDate = &#63; and expirationDate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param publishDate the publish date
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	@Override
	public AssetEntry fetchByG_C_P_E_First(
		long groupId, long classNameId, Date publishDate, Date expirationDate,
		OrderByComparator<AssetEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_P_E.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, publishDate, expirationDate},
			orderByComparator);
	}

	/**
	 * Removes all the asset entries where groupId = &#63; and classNameId = &#63; and publishDate = &#63; and expirationDate = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param publishDate the publish date
	 * @param expirationDate the expiration date
	 */
	@Override
	public void removeByG_C_P_E(
		long groupId, long classNameId, Date publishDate, Date expirationDate) {

		_collectionPersistenceFinderByG_C_P_E.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, publishDate, expirationDate});
	}

	/**
	 * Returns the number of asset entries where groupId = &#63; and classNameId = &#63; and publishDate = &#63; and expirationDate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param publishDate the publish date
	 * @param expirationDate the expiration date
	 * @return the number of matching asset entries
	 */
	@Override
	public int countByG_C_P_E(
		long groupId, long classNameId, Date publishDate, Date expirationDate) {

		return _collectionPersistenceFinderByG_C_P_E.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, publishDate, expirationDate});
	}

	public AssetEntryPersistenceImpl() {
		setModelClass(AssetEntry.class);

		setModelImplClass(AssetEntryImpl.class);
		setModelPKClass(long.class);

		setTable(AssetEntryTable.INSTANCE);
	}

	/**
	 * Creates a new asset entry with the primary key. Does not add the asset entry to the database.
	 *
	 * @param entryId the primary key for the new asset entry
	 * @return the new asset entry
	 */
	@Override
	public AssetEntry create(long entryId) {
		AssetEntry assetEntry = new AssetEntryImpl();

		assetEntry.setNew(true);
		assetEntry.setPrimaryKey(entryId);

		assetEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return assetEntry;
	}

	/**
	 * Removes the asset entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the asset entry
	 * @return the asset entry that was removed
	 * @throws NoSuchEntryException if a asset entry with the primary key could not be found
	 */
	@Override
	public AssetEntry remove(long entryId) throws NoSuchEntryException {
		return remove((Serializable)entryId);
	}

	@Override
	protected AssetEntry removeImpl(AssetEntry assetEntry) {
		assetEntryToAssetTagTableMapper.deleteLeftPrimaryKeyTableMappings(
			assetEntry.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(assetEntry)) {
				assetEntry = (AssetEntry)session.get(
					AssetEntryImpl.class, assetEntry.getPrimaryKeyObj());
			}

			if ((assetEntry != null) &&
				CTPersistenceHelperUtil.isRemove(assetEntry)) {

				session.delete(assetEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (assetEntry != null) {
			clearCache(assetEntry);
		}

		return assetEntry;
	}

	@Override
	public AssetEntry updateImpl(AssetEntry assetEntry) {
		boolean isNew = assetEntry.isNew();

		if (!(assetEntry instanceof AssetEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(assetEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(assetEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in assetEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AssetEntry implementation " +
					assetEntry.getClass());
		}

		AssetEntryModelImpl assetEntryModelImpl =
			(AssetEntryModelImpl)assetEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (assetEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				assetEntry.setCreateDate(date);
			}
			else {
				assetEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!assetEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				assetEntry.setModifiedDate(date);
			}
			else {
				assetEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(assetEntry)) {
				if (!isNew) {
					session.evict(
						AssetEntryImpl.class, assetEntry.getPrimaryKeyObj());
				}

				session.save(assetEntry);
			}
			else {
				assetEntry = (AssetEntry)session.merge(assetEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(assetEntry, false);

		if (isNew) {
			assetEntry.setNew(false);
		}

		assetEntry.resetOriginalValues();

		return assetEntry;
	}

	/**
	 * Returns the asset entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param entryId the primary key of the asset entry
	 * @return the asset entry
	 * @throws NoSuchEntryException if a asset entry with the primary key could not be found
	 */
	@Override
	public AssetEntry findByPrimaryKey(long entryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)entryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the asset entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param entryId the primary key of the asset entry
	 * @return the asset entry, or <code>null</code> if a asset entry with the primary key could not be found
	 */
	@Override
	public AssetEntry fetchByPrimaryKey(long entryId) {
		return fetchByPrimaryKey((Serializable)entryId);
	}

	/**
	 * Returns the primaryKeys of asset tags associated with the asset entry.
	 *
	 * @param pk the primary key of the asset entry
	 * @return long[] of the primaryKeys of asset tags associated with the asset entry
	 */
	@Override
	public long[] getAssetTagPrimaryKeys(long pk) {
		long[] pks = assetEntryToAssetTagTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the asset tags associated with the asset entry.
	 *
	 * @param pk the primary key of the asset entry
	 * @return the asset tags associated with the asset entry
	 */
	@Override
	public List<com.liferay.asset.kernel.model.AssetTag> getAssetTags(long pk) {
		return getAssetTags(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the asset tags associated with the asset entry.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the asset entry
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @return the range of asset tags associated with the asset entry
	 */
	@Override
	public List<com.liferay.asset.kernel.model.AssetTag> getAssetTags(
		long pk, int start, int end) {

		return getAssetTags(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset tags associated with the asset entry.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the asset entry
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of asset tags associated with the asset entry
	 */
	@Override
	public List<com.liferay.asset.kernel.model.AssetTag> getAssetTags(
		long pk, int start, int end,
		OrderByComparator<com.liferay.asset.kernel.model.AssetTag>
			orderByComparator) {

		return assetEntryToAssetTagTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset tags associated with the asset entry.
	 *
	 * @param pk the primary key of the asset entry
	 * @return the number of asset tags associated with the asset entry
	 */
	@Override
	public int getAssetTagsSize(long pk) {
		long[] pks = assetEntryToAssetTagTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the asset tag is associated with the asset entry.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTagPK the primary key of the asset tag
	 * @return <code>true</code> if the asset tag is associated with the asset entry; <code>false</code> otherwise
	 */
	@Override
	public boolean containsAssetTag(long pk, long assetTagPK) {
		return assetEntryToAssetTagTableMapper.containsTableMapping(
			pk, assetTagPK);
	}

	/**
	 * Returns <code>true</code> if the asset entry has any asset tags associated with it.
	 *
	 * @param pk the primary key of the asset entry to check for associations with asset tags
	 * @return <code>true</code> if the asset entry has any asset tags associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsAssetTags(long pk) {
		if (getAssetTagsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the asset entry and the asset tag. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTagPK the primary key of the asset tag
	 * @return <code>true</code> if an association between the asset entry and the asset tag was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addAssetTag(long pk, long assetTagPK) {
		AssetEntry assetEntry = fetchByPrimaryKey(pk);

		if (assetEntry == null) {
			return assetEntryToAssetTagTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, assetTagPK);
		}
		else {
			return assetEntryToAssetTagTableMapper.addTableMapping(
				assetEntry.getCompanyId(), pk, assetTagPK);
		}
	}

	/**
	 * Adds an association between the asset entry and the asset tag. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTag the asset tag
	 * @return <code>true</code> if an association between the asset entry and the asset tag was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addAssetTag(
		long pk, com.liferay.asset.kernel.model.AssetTag assetTag) {

		AssetEntry assetEntry = fetchByPrimaryKey(pk);

		if (assetEntry == null) {
			return assetEntryToAssetTagTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				assetTag.getPrimaryKey());
		}
		else {
			return assetEntryToAssetTagTableMapper.addTableMapping(
				assetEntry.getCompanyId(), pk, assetTag.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the asset entry and the asset tags. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTagPKs the primary keys of the asset tags
	 * @return <code>true</code> if at least one association between the asset entry and the asset tags was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addAssetTags(long pk, long[] assetTagPKs) {
		long companyId = 0;

		AssetEntry assetEntry = fetchByPrimaryKey(pk);

		if (assetEntry == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = assetEntry.getCompanyId();
		}

		long[] addedKeys = assetEntryToAssetTagTableMapper.addTableMappings(
			companyId, pk, assetTagPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the asset entry and the asset tags. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTags the asset tags
	 * @return <code>true</code> if at least one association between the asset entry and the asset tags was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addAssetTags(
		long pk, List<com.liferay.asset.kernel.model.AssetTag> assetTags) {

		return addAssetTags(
			pk,
			ListUtil.toLongArray(
				assetTags,
				com.liferay.asset.kernel.model.AssetTag.TAG_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the asset entry and its asset tags. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry to clear the associated asset tags from
	 */
	@Override
	public void clearAssetTags(long pk) {
		assetEntryToAssetTagTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the asset entry and the asset tag. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTagPK the primary key of the asset tag
	 */
	@Override
	public void removeAssetTag(long pk, long assetTagPK) {
		assetEntryToAssetTagTableMapper.deleteTableMapping(pk, assetTagPK);
	}

	/**
	 * Removes the association between the asset entry and the asset tag. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTag the asset tag
	 */
	@Override
	public void removeAssetTag(
		long pk, com.liferay.asset.kernel.model.AssetTag assetTag) {

		assetEntryToAssetTagTableMapper.deleteTableMapping(
			pk, assetTag.getPrimaryKey());
	}

	/**
	 * Removes the association between the asset entry and the asset tags. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTagPKs the primary keys of the asset tags
	 */
	@Override
	public void removeAssetTags(long pk, long[] assetTagPKs) {
		assetEntryToAssetTagTableMapper.deleteTableMappings(pk, assetTagPKs);
	}

	/**
	 * Removes the association between the asset entry and the asset tags. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTags the asset tags
	 */
	@Override
	public void removeAssetTags(
		long pk, List<com.liferay.asset.kernel.model.AssetTag> assetTags) {

		removeAssetTags(
			pk,
			ListUtil.toLongArray(
				assetTags,
				com.liferay.asset.kernel.model.AssetTag.TAG_ID_ACCESSOR));
	}

	/**
	 * Sets the asset tags associated with the asset entry, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTagPKs the primary keys of the asset tags to be associated with the asset entry
	 */
	@Override
	public void setAssetTags(long pk, long[] assetTagPKs) {
		Set<Long> newAssetTagPKsSet = SetUtil.fromArray(assetTagPKs);
		Set<Long> oldAssetTagPKsSet = SetUtil.fromArray(
			assetEntryToAssetTagTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeAssetTagPKsSet = new HashSet<Long>(oldAssetTagPKsSet);

		removeAssetTagPKsSet.removeAll(newAssetTagPKsSet);

		assetEntryToAssetTagTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeAssetTagPKsSet));

		newAssetTagPKsSet.removeAll(oldAssetTagPKsSet);

		long companyId = 0;

		AssetEntry assetEntry = fetchByPrimaryKey(pk);

		if (assetEntry == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = assetEntry.getCompanyId();
		}

		assetEntryToAssetTagTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newAssetTagPKsSet));
	}

	/**
	 * Sets the asset tags associated with the asset entry, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTags the asset tags to be associated with the asset entry
	 */
	@Override
	public void setAssetTags(
		long pk, List<com.liferay.asset.kernel.model.AssetTag> assetTags) {

		try {
			long[] assetTagPKs = new long[assetTags.size()];

			for (int i = 0; i < assetTags.size(); i++) {
				com.liferay.asset.kernel.model.AssetTag assetTag =
					assetTags.get(i);

				assetTagPKs[i] = assetTag.getPrimaryKey();
			}

			setAssetTags(pk, assetTagPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "entryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ASSETENTRY;
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
		return AssetEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AssetEntry";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("classUuid");
		ctMergeColumnNames.add("classTypeId");
		ctMergeColumnNames.add("listable");
		ctMergeColumnNames.add("visible");
		ctMergeColumnNames.add("startDate");
		ctMergeColumnNames.add("endDate");
		ctMergeColumnNames.add("publishDate");
		ctMergeColumnNames.add("expirationDate");
		ctMergeColumnNames.add("mimeType");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("summary");
		ctMergeColumnNames.add("url");
		ctMergeColumnNames.add("layoutUuid");
		ctMergeColumnNames.add("height");
		ctMergeColumnNames.add("width");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("tags");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("entryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_mappingTableNames.add("AssetEntries_AssetTags");

		_uniqueIndexColumnNames.add(new String[] {"classNameId", "classPK"});
	}

	/**
	 * Initializes the asset entry persistence.
	 */
	public void afterPropertiesSet() {
		assetEntryToAssetTagTableMapper = TableMapperFactory.getTableMapper(
			"AssetEntries_AssetTags", "companyId", "entryId", "tagId", this,
			assetTagPersistence);

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
				_SQL_SELECT_ASSETENTRY_WHERE, _SQL_COUNT_ASSETENTRY_WHERE,
				AssetEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"assetEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, AssetEntry::getGroupId));

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
				_SQL_SELECT_ASSETENTRY_WHERE, _SQL_COUNT_ASSETENTRY_WHERE,
				AssetEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"assetEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, AssetEntry::getCompanyId));

		_collectionPersistenceFinderByClassUuid =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByClassUuid",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"classUuid"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByClassUuid", new String[] {String.class.getName()},
					new String[] {"classUuid"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByClassUuid", new String[] {String.class.getName()},
					new String[] {"classUuid"}, 0, 1, false, null),
				_SQL_SELECT_ASSETENTRY_WHERE, _SQL_COUNT_ASSETENTRY_WHERE,
				AssetEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"assetEntry.", "classUuid", FinderColumn.Type.STRING, "=",
					true, true, AssetEntry::getClassUuid));

		_collectionPersistenceFinderByVisible =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByVisible",
					new String[] {
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"visible"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByVisible",
					new String[] {Boolean.class.getName()},
					new String[] {"visible"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByVisible",
					new String[] {Boolean.class.getName()},
					new String[] {"visible"}, false),
				_SQL_SELECT_ASSETENTRY_WHERE, _SQL_COUNT_ASSETENTRY_WHERE,
				AssetEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"assetEntry.", "visible", FinderColumn.Type.BOOLEAN, "=",
					true, true, AssetEntry::isVisible));

		_collectionPersistenceFinderByPublishDate =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPublishDate",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"publishDate"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByPublishDate", new String[] {Date.class.getName()},
					new String[] {"publishDate"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByPublishDate", new String[] {Date.class.getName()},
					new String[] {"publishDate"}, false),
				_SQL_SELECT_ASSETENTRY_WHERE, _SQL_COUNT_ASSETENTRY_WHERE,
				AssetEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"assetEntry.", "publishDate", FinderColumn.Type.DATE, "=",
					true, true, AssetEntry::getPublishDate));

		_collectionPersistenceFinderByExpirationDate =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByExpirationDate",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"expirationDate"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByExpirationDate", new String[] {Date.class.getName()},
					new String[] {"expirationDate"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByExpirationDate",
					new String[] {Date.class.getName()},
					new String[] {"expirationDate"}, false),
				_SQL_SELECT_ASSETENTRY_WHERE, _SQL_COUNT_ASSETENTRY_WHERE,
				AssetEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"assetEntry.", "expirationDate", FinderColumn.Type.DATE,
					"=", true, true, AssetEntry::getExpirationDate));

		_collectionPersistenceFinderByLayoutUuid =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLayoutUuid",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"layoutUuid"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLayoutUuid", new String[] {String.class.getName()},
					new String[] {"layoutUuid"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLayoutUuid", new String[] {String.class.getName()},
					new String[] {"layoutUuid"}, 0, 1, false, null),
				_SQL_SELECT_ASSETENTRY_WHERE, _SQL_COUNT_ASSETENTRY_WHERE,
				AssetEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"assetEntry.", "layoutUuid", FinderColumn.Type.STRING, "=",
					true, true, AssetEntry::getLayoutUuid));

		_uniquePersistenceFinderByG_CU = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_CU",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "classUuid"}, 0, 2, false,
				AssetEntry::getGroupId,
				convertNullFunction(AssetEntry::getClassUuid)),
			_SQL_SELECT_ASSETENTRY_WHERE, "",
			new FinderColumn<>(
				"assetEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, AssetEntry::getGroupId),
			new FinderColumn<>(
				"assetEntry.", "classUuid", FinderColumn.Type.STRING, "=", true,
				true, AssetEntry::getClassUuid));

		_collectionPersistenceFinderByC_CN = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CN",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CN",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_CN",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, false),
			_SQL_SELECT_ASSETENTRY_WHERE, _SQL_COUNT_ASSETENTRY_WHERE,
			AssetEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"assetEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, AssetEntry::getCompanyId),
			new FinderColumn<>(
				"assetEntry.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, AssetEntry::getClassNameId));

		_uniquePersistenceFinderByC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, 0, 0, false,
				AssetEntry::getClassNameId, AssetEntry::getClassPK),
			_SQL_SELECT_ASSETENTRY_WHERE, "",
			new FinderColumn<>(
				"assetEntry.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, AssetEntry::getClassNameId),
			new FinderColumn<>(
				"assetEntry.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, AssetEntry::getClassPK));

		_collectionPersistenceFinderByG_C_V = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_V",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "classNameId", "visible"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_V",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"groupId", "classNameId", "visible"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_V",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"groupId", "classNameId", "visible"}, false),
			_SQL_SELECT_ASSETENTRY_WHERE, _SQL_COUNT_ASSETENTRY_WHERE,
			AssetEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"assetEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, AssetEntry::getGroupId),
			new FinderColumn<>(
				"assetEntry.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, AssetEntry::getClassNameId),
			new FinderColumn<>(
				"assetEntry.", "visible", FinderColumn.Type.BOOLEAN, "=", true,
				true, AssetEntry::isVisible));

		_collectionPersistenceFinderByG_C_P_E =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_P_E",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Date.class.getName(), Date.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "publishDate",
						"expirationDate"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_P_E",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Date.class.getName(), Date.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "publishDate",
						"expirationDate"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_P_E",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Date.class.getName(), Date.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "publishDate",
						"expirationDate"
					},
					false),
				_SQL_SELECT_ASSETENTRY_WHERE, _SQL_COUNT_ASSETENTRY_WHERE,
				AssetEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"assetEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, AssetEntry::getGroupId),
				new FinderColumn<>(
					"assetEntry.", "classNameId", FinderColumn.Type.LONG, "=",
					true, true, AssetEntry::getClassNameId),
				new FinderColumn<>(
					"assetEntry.", "publishDate", FinderColumn.Type.DATE, "=",
					true, true, AssetEntry::getPublishDate),
				new FinderColumn<>(
					"assetEntry.", "expirationDate", FinderColumn.Type.DATE,
					"=", true, true, AssetEntry::getExpirationDate));

		AssetEntryUtil.setPersistence(this);
	}

	public void destroy() {
		AssetEntryUtil.setPersistence(null);

		EntityCacheUtil.removeCache(AssetEntryImpl.class.getName());

		TableMapperFactory.removeTableMapper("AssetEntries_AssetTags");
	}

	@BeanReference(type = AssetTagPersistence.class)
	protected AssetTagPersistence assetTagPersistence;

	protected TableMapper<AssetEntry, com.liferay.asset.kernel.model.AssetTag>
		assetEntryToAssetTagTableMapper;

	private static final String _ENTITY_ALIAS_PREFIX =
		AssetEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ASSETENTRY =
		"SELECT assetEntry FROM AssetEntry assetEntry";

	private static final String _SQL_SELECT_ASSETENTRY_WHERE =
		"SELECT assetEntry FROM AssetEntry assetEntry WHERE ";

	private static final String _SQL_COUNT_ASSETENTRY_WHERE =
		"SELECT COUNT(assetEntry) FROM AssetEntry assetEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No AssetEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		AssetEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:666931190