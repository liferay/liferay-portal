/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.persistence.impl;

import com.liferay.document.library.kernel.exception.DuplicateDLFileEntryTypeExternalReferenceCodeException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryTypeException;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeTable;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryTypePersistence;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryTypeUtil;
import com.liferay.document.library.kernel.service.persistence.DLFolderPersistence;
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
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
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
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryTypeImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryTypeModelImpl;

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
 * The persistence implementation for the document library file entry type service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DLFileEntryTypePersistenceImpl
	extends BasePersistenceImpl<DLFileEntryType, NoSuchFileEntryTypeException>
	implements DLFileEntryTypePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DLFileEntryTypeUtil</code> to access the document library file entry type persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DLFileEntryTypeImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DLFileEntryType, NoSuchFileEntryTypeException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the document library file entry types where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryTypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library file entry types
	 * @param end the upper bound of the range of document library file entry types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entry types
	 */
	@Override
	public List<DLFileEntryType> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DLFileEntryType> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry type in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry type
	 * @throws NoSuchFileEntryTypeException if a matching document library file entry type could not be found
	 */
	@Override
	public DLFileEntryType findByUuid_First(
			String uuid, OrderByComparator<DLFileEntryType> orderByComparator)
		throws NoSuchFileEntryTypeException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry type in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry type, or <code>null</code> if a matching document library file entry type could not be found
	 */
	@Override
	public DLFileEntryType fetchByUuid_First(
		String uuid, OrderByComparator<DLFileEntryType> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the document library file entry types where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of document library file entry types where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching document library file entry types
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<DLFileEntryType, NoSuchFileEntryTypeException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the document library file entry type where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFileEntryTypeException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching document library file entry type
	 * @throws NoSuchFileEntryTypeException if a matching document library file entry type could not be found
	 */
	@Override
	public DLFileEntryType findByUUID_G(String uuid, long groupId)
		throws NoSuchFileEntryTypeException {

		return _uniquePersistenceFinderByUUID_G.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	/**
	 * Returns the document library file entry type where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry type, or <code>null</code> if a matching document library file entry type could not be found
	 */
	@Override
	public DLFileEntryType fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId},
			useFinderCache);
	}

	/**
	 * Removes the document library file entry type where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the document library file entry type that was removed
	 */
	@Override
	public DLFileEntryType removeByUUID_G(String uuid, long groupId)
		throws NoSuchFileEntryTypeException {

		DLFileEntryType dlFileEntryType = findByUUID_G(uuid, groupId);

		return remove(dlFileEntryType);
	}

	/**
	 * Returns the number of document library file entry types where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching document library file entry types
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<DLFileEntryType, NoSuchFileEntryTypeException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the document library file entry types where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryTypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file entry types
	 * @param end the upper bound of the range of document library file entry types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entry types
	 */
	@Override
	public List<DLFileEntryType> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFileEntryType> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry type in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry type
	 * @throws NoSuchFileEntryTypeException if a matching document library file entry type could not be found
	 */
	@Override
	public DLFileEntryType findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DLFileEntryType> orderByComparator)
		throws NoSuchFileEntryTypeException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry type in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry type, or <code>null</code> if a matching document library file entry type could not be found
	 */
	@Override
	public DLFileEntryType fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DLFileEntryType> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the document library file entry types where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of document library file entry types where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching document library file entry types
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<DLFileEntryType, NoSuchFileEntryTypeException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the document library file entry types where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryTypeModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file entry types
	 * @param end the upper bound of the range of document library file entry types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entry types
	 */
	@Override
	public List<DLFileEntryType> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFileEntryType> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first document library file entry type in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry type
	 * @throws NoSuchFileEntryTypeException if a matching document library file entry type could not be found
	 */
	@Override
	public DLFileEntryType findByGroupId_First(
			long groupId, OrderByComparator<DLFileEntryType> orderByComparator)
		throws NoSuchFileEntryTypeException {

		DLFileEntryType dlFileEntryType = fetchByGroupId_First(
			groupId, orderByComparator);

		if (dlFileEntryType != null) {
			return dlFileEntryType;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchFileEntryTypeException(sb.toString());
	}

	/**
	 * Returns the first document library file entry type in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry type, or <code>null</code> if a matching document library file entry type could not be found
	 */
	@Override
	public DLFileEntryType fetchByGroupId_First(
		long groupId, OrderByComparator<DLFileEntryType> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entry types that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryTypeModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file entry types
	 * @param end the upper bound of the range of document library file entry types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entry types that the user has permission to view
	 */
	@Override
	public List<DLFileEntryType> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFileEntryType> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Returns an ordered range of all the document library file entry types that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryTypeModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of document library file entry types
	 * @param end the upper bound of the range of document library file entry types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entry types that the user has permission to view
	 */
	@Override
	public List<DLFileEntryType> filterFindByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<DLFileEntryType> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByGroupId.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {groupIds}, start,
			end, orderByComparator, groupIds);
	}

	/**
	 * Returns an ordered range of all the document library file entry types where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryTypeModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of document library file entry types
	 * @param end the upper bound of the range of document library file entry types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entry types
	 */
	@Override
	public List<DLFileEntryType> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<DLFileEntryType> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ArrayUtil.sortedUnique(groupIds)}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the document library file entry types where groupId = &#63; from the database.
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
	 * Returns the number of document library file entry types where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching document library file entry types
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of document library file entry types where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching document library file entry types
	 */
	@Override
	public int countByGroupId(long[] groupIds) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ArrayUtil.sortedUnique(groupIds)});
	}

	/**
	 * Returns the number of document library file entry types that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching document library file entry types that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {groupId}}, groupId);
	}

	/**
	 * Returns the number of document library file entry types that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching document library file entry types that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long[] groupIds) {
		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByGroupId.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {groupIds},
			groupIds);
	}

	private CollectionPersistenceFinder
		<DLFileEntryType, NoSuchFileEntryTypeException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the document library file entry types where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryTypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file entry types
	 * @param end the upper bound of the range of document library file entry types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entry types
	 */
	@Override
	public List<DLFileEntryType> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DLFileEntryType> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry type in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry type
	 * @throws NoSuchFileEntryTypeException if a matching document library file entry type could not be found
	 */
	@Override
	public DLFileEntryType findByCompanyId_First(
			long companyId,
			OrderByComparator<DLFileEntryType> orderByComparator)
		throws NoSuchFileEntryTypeException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry type in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry type, or <code>null</code> if a matching document library file entry type could not be found
	 */
	@Override
	public DLFileEntryType fetchByCompanyId_First(
		long companyId, OrderByComparator<DLFileEntryType> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the document library file entry types where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of document library file entry types where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching document library file entry types
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private UniquePersistenceFinder
		<DLFileEntryType, NoSuchFileEntryTypeException>
			_uniquePersistenceFinderByG_DDI;

	/**
	 * Returns the document library file entry type where groupId = &#63; and dataDefinitionId = &#63; or throws a <code>NoSuchFileEntryTypeException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param dataDefinitionId the data definition ID
	 * @return the matching document library file entry type
	 * @throws NoSuchFileEntryTypeException if a matching document library file entry type could not be found
	 */
	@Override
	public DLFileEntryType findByG_DDI(long groupId, long dataDefinitionId)
		throws NoSuchFileEntryTypeException {

		return _uniquePersistenceFinderByG_DDI.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, dataDefinitionId});
	}

	/**
	 * Returns the document library file entry type where groupId = &#63; and dataDefinitionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param dataDefinitionId the data definition ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry type, or <code>null</code> if a matching document library file entry type could not be found
	 */
	@Override
	public DLFileEntryType fetchByG_DDI(
		long groupId, long dataDefinitionId, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_DDI.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, dataDefinitionId}, useFinderCache);
	}

	/**
	 * Removes the document library file entry type where groupId = &#63; and dataDefinitionId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param dataDefinitionId the data definition ID
	 * @return the document library file entry type that was removed
	 */
	@Override
	public DLFileEntryType removeByG_DDI(long groupId, long dataDefinitionId)
		throws NoSuchFileEntryTypeException {

		DLFileEntryType dlFileEntryType = findByG_DDI(
			groupId, dataDefinitionId);

		return remove(dlFileEntryType);
	}

	/**
	 * Returns the number of document library file entry types where groupId = &#63; and dataDefinitionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param dataDefinitionId the data definition ID
	 * @return the number of matching document library file entry types
	 */
	@Override
	public int countByG_DDI(long groupId, long dataDefinitionId) {
		return _uniquePersistenceFinderByG_DDI.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, dataDefinitionId});
	}

	private UniquePersistenceFinder
		<DLFileEntryType, NoSuchFileEntryTypeException>
			_uniquePersistenceFinderByG_F;

	/**
	 * Returns the document library file entry type where groupId = &#63; and fileEntryTypeKey = &#63; or throws a <code>NoSuchFileEntryTypeException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param fileEntryTypeKey the file entry type key
	 * @return the matching document library file entry type
	 * @throws NoSuchFileEntryTypeException if a matching document library file entry type could not be found
	 */
	@Override
	public DLFileEntryType findByG_F(long groupId, String fileEntryTypeKey)
		throws NoSuchFileEntryTypeException {

		return _uniquePersistenceFinderByG_F.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, fileEntryTypeKey});
	}

	/**
	 * Returns the document library file entry type where groupId = &#63; and fileEntryTypeKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param fileEntryTypeKey the file entry type key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry type, or <code>null</code> if a matching document library file entry type could not be found
	 */
	@Override
	public DLFileEntryType fetchByG_F(
		long groupId, String fileEntryTypeKey, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_F.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, fileEntryTypeKey}, useFinderCache);
	}

	/**
	 * Removes the document library file entry type where groupId = &#63; and fileEntryTypeKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fileEntryTypeKey the file entry type key
	 * @return the document library file entry type that was removed
	 */
	@Override
	public DLFileEntryType removeByG_F(long groupId, String fileEntryTypeKey)
		throws NoSuchFileEntryTypeException {

		DLFileEntryType dlFileEntryType = findByG_F(groupId, fileEntryTypeKey);

		return remove(dlFileEntryType);
	}

	/**
	 * Returns the number of document library file entry types where groupId = &#63; and fileEntryTypeKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fileEntryTypeKey the file entry type key
	 * @return the number of matching document library file entry types
	 */
	@Override
	public int countByG_F(long groupId, String fileEntryTypeKey) {
		return _uniquePersistenceFinderByG_F.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, fileEntryTypeKey});
	}

	private UniquePersistenceFinder
		<DLFileEntryType, NoSuchFileEntryTypeException>
			_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the document library file entry type where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchFileEntryTypeException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching document library file entry type
	 * @throws NoSuchFileEntryTypeException if a matching document library file entry type could not be found
	 */
	@Override
	public DLFileEntryType findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchFileEntryTypeException {

		return _uniquePersistenceFinderByERC_G.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the document library file entry type where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry type, or <code>null</code> if a matching document library file entry type could not be found
	 */
	@Override
	public DLFileEntryType fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId}, useFinderCache);
	}

	/**
	 * Removes the document library file entry type where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the document library file entry type that was removed
	 */
	@Override
	public DLFileEntryType removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchFileEntryTypeException {

		DLFileEntryType dlFileEntryType = findByERC_G(
			externalReferenceCode, groupId);

		return remove(dlFileEntryType);
	}

	/**
	 * Returns the number of document library file entry types where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching document library file entry types
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	public DLFileEntryTypePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DLFileEntryType.class);

		setModelImplClass(DLFileEntryTypeImpl.class);
		setModelPKClass(long.class);

		setTable(DLFileEntryTypeTable.INSTANCE);
	}

	/**
	 * Creates a new document library file entry type with the primary key. Does not add the document library file entry type to the database.
	 *
	 * @param fileEntryTypeId the primary key for the new document library file entry type
	 * @return the new document library file entry type
	 */
	@Override
	public DLFileEntryType create(long fileEntryTypeId) {
		DLFileEntryType dlFileEntryType = new DLFileEntryTypeImpl();

		dlFileEntryType.setNew(true);
		dlFileEntryType.setPrimaryKey(fileEntryTypeId);

		String uuid = PortalUUIDUtil.generate();

		dlFileEntryType.setUuid(uuid);

		dlFileEntryType.setCompanyId(CompanyThreadLocal.getCompanyId());

		return dlFileEntryType;
	}

	/**
	 * Removes the document library file entry type with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fileEntryTypeId the primary key of the document library file entry type
	 * @return the document library file entry type that was removed
	 * @throws NoSuchFileEntryTypeException if a document library file entry type with the primary key could not be found
	 */
	@Override
	public DLFileEntryType remove(long fileEntryTypeId)
		throws NoSuchFileEntryTypeException {

		return remove((Serializable)fileEntryTypeId);
	}

	@Override
	protected DLFileEntryType removeImpl(DLFileEntryType dlFileEntryType) {
		dlFileEntryTypeToDLFolderTableMapper.deleteLeftPrimaryKeyTableMappings(
			dlFileEntryType.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dlFileEntryType)) {
				dlFileEntryType = (DLFileEntryType)session.get(
					DLFileEntryTypeImpl.class,
					dlFileEntryType.getPrimaryKeyObj());
			}

			if ((dlFileEntryType != null) &&
				CTPersistenceHelperUtil.isRemove(dlFileEntryType)) {

				session.delete(dlFileEntryType);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dlFileEntryType != null) {
			clearCache(dlFileEntryType);
		}

		return dlFileEntryType;
	}

	@Override
	public DLFileEntryType updateImpl(DLFileEntryType dlFileEntryType) {
		boolean isNew = dlFileEntryType.isNew();

		if (!(dlFileEntryType instanceof DLFileEntryTypeModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dlFileEntryType.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					dlFileEntryType);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dlFileEntryType proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DLFileEntryType implementation " +
					dlFileEntryType.getClass());
		}

		DLFileEntryTypeModelImpl dlFileEntryTypeModelImpl =
			(DLFileEntryTypeModelImpl)dlFileEntryType;

		if (Validator.isNull(dlFileEntryType.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			dlFileEntryType.setUuid(uuid);
		}

		if (Validator.isNull(dlFileEntryType.getExternalReferenceCode())) {
			dlFileEntryType.setExternalReferenceCode(dlFileEntryType.getUuid());
		}
		else {
			if (!Objects.equals(
					dlFileEntryTypeModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					dlFileEntryType.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = dlFileEntryType.getCompanyId();

					long groupId = dlFileEntryType.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = dlFileEntryType.getPrimaryKey();
					}

					try {
						dlFileEntryType.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								DLFileEntryType.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								dlFileEntryType.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			DLFileEntryType ercDLFileEntryType = fetchByERC_G(
				dlFileEntryType.getExternalReferenceCode(),
				dlFileEntryType.getGroupId());

			if (isNew) {
				if (ercDLFileEntryType != null) {
					throw new DuplicateDLFileEntryTypeExternalReferenceCodeException(
						"Duplicate document library file entry type with external reference code " +
							dlFileEntryType.getExternalReferenceCode() +
								" and group " + dlFileEntryType.getGroupId());
				}
			}
			else {
				if ((ercDLFileEntryType != null) &&
					(dlFileEntryType.getFileEntryTypeId() !=
						ercDLFileEntryType.getFileEntryTypeId())) {

					throw new DuplicateDLFileEntryTypeExternalReferenceCodeException(
						"Duplicate document library file entry type with external reference code " +
							dlFileEntryType.getExternalReferenceCode() +
								" and group " + dlFileEntryType.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (dlFileEntryType.getCreateDate() == null)) {
			if (serviceContext == null) {
				dlFileEntryType.setCreateDate(date);
			}
			else {
				dlFileEntryType.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!dlFileEntryTypeModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				dlFileEntryType.setModifiedDate(date);
			}
			else {
				dlFileEntryType.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(dlFileEntryType)) {
				if (!isNew) {
					session.evict(
						DLFileEntryTypeImpl.class,
						dlFileEntryType.getPrimaryKeyObj());
				}

				session.save(dlFileEntryType);
			}
			else {
				dlFileEntryType = (DLFileEntryType)session.merge(
					dlFileEntryType);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(dlFileEntryType, false);

		if (isNew) {
			dlFileEntryType.setNew(false);
		}

		dlFileEntryType.resetOriginalValues();

		return dlFileEntryType;
	}

	/**
	 * Returns the document library file entry type with the primary key or throws a <code>NoSuchFileEntryTypeException</code> if it could not be found.
	 *
	 * @param fileEntryTypeId the primary key of the document library file entry type
	 * @return the document library file entry type
	 * @throws NoSuchFileEntryTypeException if a document library file entry type with the primary key could not be found
	 */
	@Override
	public DLFileEntryType findByPrimaryKey(long fileEntryTypeId)
		throws NoSuchFileEntryTypeException {

		return findByPrimaryKey((Serializable)fileEntryTypeId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the document library file entry type with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fileEntryTypeId the primary key of the document library file entry type
	 * @return the document library file entry type, or <code>null</code> if a document library file entry type with the primary key could not be found
	 */
	@Override
	public DLFileEntryType fetchByPrimaryKey(long fileEntryTypeId) {
		return fetchByPrimaryKey((Serializable)fileEntryTypeId);
	}

	/**
	 * Returns the primaryKeys of document library folders associated with the document library file entry type.
	 *
	 * @param pk the primary key of the document library file entry type
	 * @return long[] of the primaryKeys of document library folders associated with the document library file entry type
	 */
	@Override
	public long[] getDLFolderPrimaryKeys(long pk) {
		long[] pks = dlFileEntryTypeToDLFolderTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the document library folders associated with the document library file entry type.
	 *
	 * @param pk the primary key of the document library file entry type
	 * @return the document library folders associated with the document library file entry type
	 */
	@Override
	public List<com.liferay.document.library.kernel.model.DLFolder>
		getDLFolders(long pk) {

		return getDLFolders(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the document library folders associated with the document library file entry type.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryTypeModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the document library file entry type
	 * @param start the lower bound of the range of document library file entry types
	 * @param end the upper bound of the range of document library file entry types (not inclusive)
	 * @return the range of document library folders associated with the document library file entry type
	 */
	@Override
	public List<com.liferay.document.library.kernel.model.DLFolder>
		getDLFolders(long pk, int start, int end) {

		return getDLFolders(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders associated with the document library file entry type.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryTypeModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the document library file entry type
	 * @param start the lower bound of the range of document library file entry types
	 * @param end the upper bound of the range of document library file entry types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of document library folders associated with the document library file entry type
	 */
	@Override
	public List<com.liferay.document.library.kernel.model.DLFolder>
		getDLFolders(
			long pk, int start, int end,
			OrderByComparator
				<com.liferay.document.library.kernel.model.DLFolder>
					orderByComparator) {

		return dlFileEntryTypeToDLFolderTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of document library folders associated with the document library file entry type.
	 *
	 * @param pk the primary key of the document library file entry type
	 * @return the number of document library folders associated with the document library file entry type
	 */
	@Override
	public int getDLFoldersSize(long pk) {
		long[] pks = dlFileEntryTypeToDLFolderTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the document library folder is associated with the document library file entry type.
	 *
	 * @param pk the primary key of the document library file entry type
	 * @param dlFolderPK the primary key of the document library folder
	 * @return <code>true</code> if the document library folder is associated with the document library file entry type; <code>false</code> otherwise
	 */
	@Override
	public boolean containsDLFolder(long pk, long dlFolderPK) {
		return dlFileEntryTypeToDLFolderTableMapper.containsTableMapping(
			pk, dlFolderPK);
	}

	/**
	 * Returns <code>true</code> if the document library file entry type has any document library folders associated with it.
	 *
	 * @param pk the primary key of the document library file entry type to check for associations with document library folders
	 * @return <code>true</code> if the document library file entry type has any document library folders associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsDLFolders(long pk) {
		if (getDLFoldersSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the document library file entry type and the document library folder. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library file entry type
	 * @param dlFolderPK the primary key of the document library folder
	 * @return <code>true</code> if an association between the document library file entry type and the document library folder was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addDLFolder(long pk, long dlFolderPK) {
		DLFileEntryType dlFileEntryType = fetchByPrimaryKey(pk);

		if (dlFileEntryType == null) {
			return dlFileEntryTypeToDLFolderTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, dlFolderPK);
		}
		else {
			return dlFileEntryTypeToDLFolderTableMapper.addTableMapping(
				dlFileEntryType.getCompanyId(), pk, dlFolderPK);
		}
	}

	/**
	 * Adds an association between the document library file entry type and the document library folder. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library file entry type
	 * @param dlFolder the document library folder
	 * @return <code>true</code> if an association between the document library file entry type and the document library folder was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addDLFolder(
		long pk, com.liferay.document.library.kernel.model.DLFolder dlFolder) {

		DLFileEntryType dlFileEntryType = fetchByPrimaryKey(pk);

		if (dlFileEntryType == null) {
			return dlFileEntryTypeToDLFolderTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				dlFolder.getPrimaryKey());
		}
		else {
			return dlFileEntryTypeToDLFolderTableMapper.addTableMapping(
				dlFileEntryType.getCompanyId(), pk, dlFolder.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the document library file entry type and the document library folders. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library file entry type
	 * @param dlFolderPKs the primary keys of the document library folders
	 * @return <code>true</code> if at least one association between the document library file entry type and the document library folders was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addDLFolders(long pk, long[] dlFolderPKs) {
		long companyId = 0;

		DLFileEntryType dlFileEntryType = fetchByPrimaryKey(pk);

		if (dlFileEntryType == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = dlFileEntryType.getCompanyId();
		}

		long[] addedKeys =
			dlFileEntryTypeToDLFolderTableMapper.addTableMappings(
				companyId, pk, dlFolderPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the document library file entry type and the document library folders. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library file entry type
	 * @param dlFolders the document library folders
	 * @return <code>true</code> if at least one association between the document library file entry type and the document library folders was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addDLFolders(
		long pk,
		List<com.liferay.document.library.kernel.model.DLFolder> dlFolders) {

		return addDLFolders(
			pk,
			ListUtil.toLongArray(
				dlFolders,
				com.liferay.document.library.kernel.model.DLFolder.
					FOLDER_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the document library file entry type and its document library folders. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library file entry type to clear the associated document library folders from
	 */
	@Override
	public void clearDLFolders(long pk) {
		dlFileEntryTypeToDLFolderTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the document library file entry type and the document library folder. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library file entry type
	 * @param dlFolderPK the primary key of the document library folder
	 */
	@Override
	public void removeDLFolder(long pk, long dlFolderPK) {
		dlFileEntryTypeToDLFolderTableMapper.deleteTableMapping(pk, dlFolderPK);
	}

	/**
	 * Removes the association between the document library file entry type and the document library folder. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library file entry type
	 * @param dlFolder the document library folder
	 */
	@Override
	public void removeDLFolder(
		long pk, com.liferay.document.library.kernel.model.DLFolder dlFolder) {

		dlFileEntryTypeToDLFolderTableMapper.deleteTableMapping(
			pk, dlFolder.getPrimaryKey());
	}

	/**
	 * Removes the association between the document library file entry type and the document library folders. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library file entry type
	 * @param dlFolderPKs the primary keys of the document library folders
	 */
	@Override
	public void removeDLFolders(long pk, long[] dlFolderPKs) {
		dlFileEntryTypeToDLFolderTableMapper.deleteTableMappings(
			pk, dlFolderPKs);
	}

	/**
	 * Removes the association between the document library file entry type and the document library folders. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library file entry type
	 * @param dlFolders the document library folders
	 */
	@Override
	public void removeDLFolders(
		long pk,
		List<com.liferay.document.library.kernel.model.DLFolder> dlFolders) {

		removeDLFolders(
			pk,
			ListUtil.toLongArray(
				dlFolders,
				com.liferay.document.library.kernel.model.DLFolder.
					FOLDER_ID_ACCESSOR));
	}

	/**
	 * Sets the document library folders associated with the document library file entry type, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library file entry type
	 * @param dlFolderPKs the primary keys of the document library folders to be associated with the document library file entry type
	 */
	@Override
	public void setDLFolders(long pk, long[] dlFolderPKs) {
		Set<Long> newDLFolderPKsSet = SetUtil.fromArray(dlFolderPKs);
		Set<Long> oldDLFolderPKsSet = SetUtil.fromArray(
			dlFileEntryTypeToDLFolderTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeDLFolderPKsSet = new HashSet<Long>(oldDLFolderPKsSet);

		removeDLFolderPKsSet.removeAll(newDLFolderPKsSet);

		dlFileEntryTypeToDLFolderTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeDLFolderPKsSet));

		newDLFolderPKsSet.removeAll(oldDLFolderPKsSet);

		long companyId = 0;

		DLFileEntryType dlFileEntryType = fetchByPrimaryKey(pk);

		if (dlFileEntryType == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = dlFileEntryType.getCompanyId();
		}

		dlFileEntryTypeToDLFolderTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newDLFolderPKsSet));
	}

	/**
	 * Sets the document library folders associated with the document library file entry type, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library file entry type
	 * @param dlFolders the document library folders to be associated with the document library file entry type
	 */
	@Override
	public void setDLFolders(
		long pk,
		List<com.liferay.document.library.kernel.model.DLFolder> dlFolders) {

		try {
			long[] dlFolderPKs = new long[dlFolders.size()];

			for (int i = 0; i < dlFolders.size(); i++) {
				com.liferay.document.library.kernel.model.DLFolder dlFolder =
					dlFolders.get(i);

				dlFolderPKs[i] = dlFolder.getPrimaryKey();
			}

			setDLFolders(pk, dlFolderPKs);
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
		return "fileEntryTypeId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DLFILEENTRYTYPE;
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
		return DLFileEntryTypeModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DLFileEntryType";
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
		ctMergeColumnNames.add("dataDefinitionId");
		ctMergeColumnNames.add("fileEntryTypeKey");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("scope");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("folders");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("fileEntryTypeId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_mappingTableNames.add("DLFileEntryTypes_DLFolders");

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "dataDefinitionId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "fileEntryTypeKey"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the document library file entry type persistence.
	 */
	public void afterPropertiesSet() {
		dlFileEntryTypeToDLFolderTableMapper =
			TableMapperFactory.getTableMapper(
				"DLFileEntryTypes_DLFolders", "companyId", "fileEntryTypeId",
				"folderId", this, dlFolderPersistence);

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
			_SQL_SELECT_DLFILEENTRYTYPE_WHERE, _SQL_COUNT_DLFILEENTRYTYPE_WHERE,
			DLFileEntryTypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"dlFileEntryType.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, DLFileEntryType::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(DLFileEntryType::getUuid),
				DLFileEntryType::getGroupId),
			_SQL_SELECT_DLFILEENTRYTYPE_WHERE, "",
			new FinderColumn<>(
				"dlFileEntryType.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, DLFileEntryType::getUuid),
			new FinderColumn<>(
				"dlFileEntryType.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, DLFileEntryType::getGroupId));

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
				_SQL_SELECT_DLFILEENTRYTYPE_WHERE,
				_SQL_COUNT_DLFILEENTRYTYPE_WHERE,
				DLFileEntryTypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"dlFileEntryType.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					DLFileEntryType::getUuid),
				new FinderColumn<>(
					"dlFileEntryType.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, DLFileEntryType::getCompanyId));

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
				_SQL_SELECT_DLFILEENTRYTYPE_WHERE,
				_SQL_COUNT_DLFILEENTRYTYPE_WHERE,
				DLFileEntryTypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new ArrayableFinderColumn<>(
					"dlFileEntryType.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, DLFileEntryType::getGroupId));

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
				_SQL_SELECT_DLFILEENTRYTYPE_WHERE,
				_SQL_COUNT_DLFILEENTRYTYPE_WHERE,
				DLFileEntryTypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"dlFileEntryType.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, DLFileEntryType::getCompanyId));

		_uniquePersistenceFinderByG_DDI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_DDI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "dataDefinitionId"}, 0, 0, false,
				DLFileEntryType::getGroupId,
				DLFileEntryType::getDataDefinitionId),
			_SQL_SELECT_DLFILEENTRYTYPE_WHERE, "",
			new FinderColumn<>(
				"dlFileEntryType.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, DLFileEntryType::getGroupId),
			new FinderColumn<>(
				"dlFileEntryType.", "dataDefinitionId", FinderColumn.Type.LONG,
				"=", true, true, DLFileEntryType::getDataDefinitionId));

		_uniquePersistenceFinderByG_F = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_F",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "fileEntryTypeKey"}, 0, 2, false,
				DLFileEntryType::getGroupId,
				convertNullFunction(DLFileEntryType::getFileEntryTypeKey)),
			_SQL_SELECT_DLFILEENTRYTYPE_WHERE, "",
			new FinderColumn<>(
				"dlFileEntryType.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, DLFileEntryType::getGroupId),
			new FinderColumn<>(
				"dlFileEntryType.", "fileEntryTypeKey",
				FinderColumn.Type.STRING, "=", true, true,
				DLFileEntryType::getFileEntryTypeKey));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(DLFileEntryType::getExternalReferenceCode),
				DLFileEntryType::getGroupId),
			_SQL_SELECT_DLFILEENTRYTYPE_WHERE, "",
			new FinderColumn<>(
				"dlFileEntryType.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				DLFileEntryType::getExternalReferenceCode),
			new FinderColumn<>(
				"dlFileEntryType.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, DLFileEntryType::getGroupId));

		DLFileEntryTypeUtil.setPersistence(this);
	}

	public void destroy() {
		DLFileEntryTypeUtil.setPersistence(null);

		EntityCacheUtil.removeCache(DLFileEntryTypeImpl.class.getName());

		TableMapperFactory.removeTableMapper("DLFileEntryTypes_DLFolders");
	}

	@BeanReference(type = DLFolderPersistence.class)
	protected DLFolderPersistence dlFolderPersistence;

	protected TableMapper
		<DLFileEntryType, com.liferay.document.library.kernel.model.DLFolder>
			dlFileEntryTypeToDLFolderTableMapper;

	private static final String _ENTITY_ALIAS_PREFIX =
		DLFileEntryTypeModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DLFILEENTRYTYPE =
		"SELECT dlFileEntryType FROM DLFileEntryType dlFileEntryType";

	private static final String _SQL_SELECT_DLFILEENTRYTYPE_WHERE =
		"SELECT dlFileEntryType FROM DLFileEntryType dlFileEntryType WHERE ";

	private static final String _SQL_COUNT_DLFILEENTRYTYPE_WHERE =
		"SELECT COUNT(dlFileEntryType) FROM DLFileEntryType dlFileEntryType WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DLFileEntryType exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileEntryTypePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1512224286