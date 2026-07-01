/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.exception.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureTable;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureModelImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructurePersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureUtil;
import com.liferay.dynamic.data.mapping.service.persistence.impl.constants.DDMPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
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
 * The persistence implementation for the ddm structure service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDMStructurePersistence.class)
public class DDMStructurePersistenceImpl
	extends BasePersistenceImpl<DDMStructure, NoSuchStructureException>
	implements DDMStructurePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDMStructureUtil</code> to access the ddm structure persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDMStructureImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<DDMStructure, NoSuchStructureException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the ddm structures where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByUuid_First(
			String uuid, OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByUuid_First(
		String uuid, OrderByComparator<DDMStructure> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the ddm structures where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of ddm structures where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<DDMStructure, NoSuchStructureException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the ddm structure where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchStructureException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByUUID_G(String uuid, long groupId)
		throws NoSuchStructureException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the ddm structure where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the ddm structure where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the ddm structure that was removed
	 */
	@Override
	public DDMStructure removeByUUID_G(String uuid, long groupId)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = findByUUID_G(uuid, groupId);

		return remove(ddmStructure);
	}

	/**
	 * Returns the number of ddm structures where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<DDMStructure, NoSuchStructureException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the ddm structures where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DDMStructure> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the ddm structures where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of ddm structures where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<DDMStructure, NoSuchStructureException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {new long[] {groupId}}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByGroupId_First(
			long groupId, OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {new long[] {groupId}},
			orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByGroupId_First(
		long groupId, OrderByComparator<DDMStructure> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {new long[] {groupId}}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupIds}, start, end, orderByComparator,
			groupIds);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds)}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the ddm structures where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of ddm structures where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of ddm structures where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByGroupId(long[] groupIds) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds)});
	}

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {new long[] {groupId}}, groupId);
	}

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long[] groupIds) {
		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupIds}, groupIds);
	}

	private CollectionPersistenceFinder<DDMStructure, NoSuchStructureException>
		_collectionPersistenceFinderByParentStructureId;

	/**
	 * Returns an ordered range of all the ddm structures where parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByParentStructureId(
		long parentStructureId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByParentStructureId.find(
			finderCache, new Object[] {parentStructureId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByParentStructureId_First(
			long parentStructureId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		return _collectionPersistenceFinderByParentStructureId.findFirst(
			finderCache, new Object[] {parentStructureId}, orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByParentStructureId_First(
		long parentStructureId,
		OrderByComparator<DDMStructure> orderByComparator) {

		return _collectionPersistenceFinderByParentStructureId.fetchFirst(
			finderCache, new Object[] {parentStructureId}, orderByComparator);
	}

	/**
	 * Removes all the ddm structures where parentStructureId = &#63; from the database.
	 *
	 * @param parentStructureId the parent structure ID
	 */
	@Override
	public void removeByParentStructureId(long parentStructureId) {
		_collectionPersistenceFinderByParentStructureId.remove(
			finderCache, new Object[] {parentStructureId});
	}

	/**
	 * Returns the number of ddm structures where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByParentStructureId(long parentStructureId) {
		return _collectionPersistenceFinderByParentStructureId.count(
			finderCache, new Object[] {parentStructureId});
	}

	private CollectionPersistenceFinder<DDMStructure, NoSuchStructureException>
		_collectionPersistenceFinderByStructureKey;

	/**
	 * Returns an ordered range of all the ddm structures where structureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param structureKey the structure key
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByStructureKey(
		String structureKey, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByStructureKey.find(
			finderCache, new Object[] {structureKey}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByStructureKey_First(
			String structureKey,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		return _collectionPersistenceFinderByStructureKey.findFirst(
			finderCache, new Object[] {structureKey}, orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByStructureKey_First(
		String structureKey,
		OrderByComparator<DDMStructure> orderByComparator) {

		return _collectionPersistenceFinderByStructureKey.fetchFirst(
			finderCache, new Object[] {structureKey}, orderByComparator);
	}

	/**
	 * Removes all the ddm structures where structureKey = &#63; from the database.
	 *
	 * @param structureKey the structure key
	 */
	@Override
	public void removeByStructureKey(String structureKey) {
		_collectionPersistenceFinderByStructureKey.remove(
			finderCache, new Object[] {structureKey});
	}

	/**
	 * Returns the number of ddm structures where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByStructureKey(String structureKey) {
		return _collectionPersistenceFinderByStructureKey.count(
			finderCache, new Object[] {structureKey});
	}

	private FilterCollectionPersistenceFinder
		<DDMStructure, NoSuchStructureException>
			_collectionPersistenceFinderByG_P;

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_P(
		long groupId, long parentStructureId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P.find(
			finderCache, new Object[] {groupId, parentStructureId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByG_P_First(
			long groupId, long parentStructureId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		return _collectionPersistenceFinderByG_P.findFirst(
			finderCache, new Object[] {groupId, parentStructureId},
			orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByG_P_First(
		long groupId, long parentStructureId,
		OrderByComparator<DDMStructure> orderByComparator) {

		return _collectionPersistenceFinderByG_P.fetchFirst(
			finderCache, new Object[] {groupId, parentStructureId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_P(
		long groupId, long parentStructureId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return _collectionPersistenceFinderByG_P.filterFind(
			finderCache, new Object[] {groupId, parentStructureId}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the ddm structures where groupId = &#63; and parentStructureId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 */
	@Override
	public void removeByG_P(long groupId, long parentStructureId) {
		_collectionPersistenceFinderByG_P.remove(
			finderCache, new Object[] {groupId, parentStructureId});
	}

	/**
	 * Returns the number of ddm structures where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByG_P(long groupId, long parentStructureId) {
		return _collectionPersistenceFinderByG_P.count(
			finderCache, new Object[] {groupId, parentStructureId});
	}

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(long groupId, long parentStructureId) {
		return _collectionPersistenceFinderByG_P.filterCount(
			finderCache, new Object[] {groupId, parentStructureId}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<DDMStructure, NoSuchStructureException>
			_collectionPersistenceFinderByG_C;

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C.find(
			finderCache, new Object[] {new long[] {groupId}, classNameId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByG_C_First(
			long groupId, long classNameId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		return _collectionPersistenceFinderByG_C.findFirst(
			finderCache, new Object[] {new long[] {groupId}, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByG_C_First(
		long groupId, long classNameId,
		OrderByComparator<DDMStructure> orderByComparator) {

		return _collectionPersistenceFinderByG_C.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}, classNameId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return _collectionPersistenceFinderByG_C.filterFind(
			finderCache, new Object[] {new long[] {groupId}, classNameId},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_C(
		long[] groupIds, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_C.filterFind(
			finderCache, new Object[] {groupIds, classNameId}, start, end,
			orderByComparator, groupIds);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C(
		long[] groupIds, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C.find(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), classNameId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the ddm structures where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_C(long groupId, long classNameId) {
		_collectionPersistenceFinderByG_C.remove(
			finderCache, new Object[] {new long[] {groupId}, classNameId});
	}

	/**
	 * Returns the number of ddm structures where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByG_C(long groupId, long classNameId) {
		return _collectionPersistenceFinderByG_C.count(
			finderCache, new Object[] {new long[] {groupId}, classNameId});
	}

	/**
	 * Returns the number of ddm structures where groupId = any &#63; and classNameId = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByG_C(long[] groupIds, long classNameId) {
		return _collectionPersistenceFinderByG_C.count(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), classNameId});
	}

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	@Override
	public int filterCountByG_C(long groupId, long classNameId) {
		return _collectionPersistenceFinderByG_C.filterCount(
			finderCache, new Object[] {new long[] {groupId}, classNameId},
			groupId);
	}

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	@Override
	public int filterCountByG_C(long[] groupIds, long classNameId) {
		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_C.filterCount(
			finderCache, new Object[] {groupIds, classNameId}, groupIds);
	}

	private CollectionPersistenceFinder<DDMStructure, NoSuchStructureException>
		_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the ddm structures where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByC_C(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {companyId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByC_C_First(
			long companyId, long classNameId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {companyId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByC_C_First(
		long companyId, long classNameId,
		OrderByComparator<DDMStructure> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {companyId, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the ddm structures where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_C(long companyId, long classNameId) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {companyId, classNameId});
	}

	/**
	 * Returns the number of ddm structures where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByC_C(long companyId, long classNameId) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {companyId, classNameId});
	}

	private UniquePersistenceFinder<DDMStructure, NoSuchStructureException>
		_uniquePersistenceFinderByERC_G_C;

	/**
	 * Returns the ddm structure where externalReferenceCode = &#63; and groupId = &#63; and classNameId = &#63; or throws a <code>NoSuchStructureException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByERC_G_C(
			String externalReferenceCode, long groupId, long classNameId)
		throws NoSuchStructureException {

		return _uniquePersistenceFinderByERC_G_C.find(
			finderCache,
			new Object[] {externalReferenceCode, groupId, classNameId});
	}

	/**
	 * Returns the ddm structure where externalReferenceCode = &#63; and groupId = &#63; and classNameId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByERC_G_C(
		String externalReferenceCode, long groupId, long classNameId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G_C.fetch(
			finderCache,
			new Object[] {externalReferenceCode, groupId, classNameId},
			useFinderCache);
	}

	/**
	 * Removes the ddm structure where externalReferenceCode = &#63; and groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the ddm structure that was removed
	 */
	@Override
	public DDMStructure removeByERC_G_C(
			String externalReferenceCode, long groupId, long classNameId)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = findByERC_G_C(
			externalReferenceCode, groupId, classNameId);

		return remove(ddmStructure);
	}

	/**
	 * Returns the number of ddm structures where externalReferenceCode = &#63; and groupId = &#63; and classNameId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByERC_G_C(
		String externalReferenceCode, long groupId, long classNameId) {

		return _uniquePersistenceFinderByERC_G_C.count(
			finderCache,
			new Object[] {externalReferenceCode, groupId, classNameId});
	}

	private UniquePersistenceFinder<DDMStructure, NoSuchStructureException>
		_uniquePersistenceFinderByG_C_S;

	/**
	 * Returns the ddm structure where groupId = &#63; and classNameId = &#63; and structureKey = &#63; or throws a <code>NoSuchStructureException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureKey the structure key
	 * @return the matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByG_C_S(
			long groupId, long classNameId, String structureKey)
		throws NoSuchStructureException {

		return _uniquePersistenceFinderByG_C_S.find(
			finderCache, new Object[] {groupId, classNameId, structureKey});
	}

	/**
	 * Returns the ddm structure where groupId = &#63; and classNameId = &#63; and structureKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureKey the structure key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByG_C_S(
		long groupId, long classNameId, String structureKey,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_C_S.fetch(
			finderCache, new Object[] {groupId, classNameId, structureKey},
			useFinderCache);
	}

	/**
	 * Removes the ddm structure where groupId = &#63; and classNameId = &#63; and structureKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureKey the structure key
	 * @return the ddm structure that was removed
	 */
	@Override
	public DDMStructure removeByG_C_S(
			long groupId, long classNameId, String structureKey)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = findByG_C_S(
			groupId, classNameId, structureKey);

		return remove(ddmStructure);
	}

	/**
	 * Returns the number of ddm structures where groupId = &#63; and classNameId = &#63; and structureKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureKey the structure key
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByG_C_S(
		long groupId, long classNameId, String structureKey) {

		return _uniquePersistenceFinderByG_C_S.count(
			finderCache, new Object[] {groupId, classNameId, structureKey});
	}

	private FilterCollectionPersistenceFinder
		<DDMStructure, NoSuchStructureException>
			_collectionPersistenceFinderByG_N_D;

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_N_D(
		long groupId, String name, String description, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_N_D.find(
			finderCache, new Object[] {groupId, name, description}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByG_N_D_First(
			long groupId, String name, String description,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		return _collectionPersistenceFinderByG_N_D.findFirst(
			finderCache, new Object[] {groupId, name, description},
			orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByG_N_D_First(
		long groupId, String name, String description,
		OrderByComparator<DDMStructure> orderByComparator) {

		return _collectionPersistenceFinderByG_N_D.fetchFirst(
			finderCache, new Object[] {groupId, name, description},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_N_D(
		long groupId, String name, String description, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return _collectionPersistenceFinderByG_N_D.filterFind(
			finderCache, new Object[] {groupId, name, description}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the ddm structures where groupId = &#63; and name = &#63; and description = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 */
	@Override
	public void removeByG_N_D(long groupId, String name, String description) {
		_collectionPersistenceFinderByG_N_D.remove(
			finderCache, new Object[] {groupId, name, description});
	}

	/**
	 * Returns the number of ddm structures where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByG_N_D(long groupId, String name, String description) {
		return _collectionPersistenceFinderByG_N_D.count(
			finderCache, new Object[] {groupId, name, description});
	}

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	@Override
	public int filterCountByG_N_D(
		long groupId, String name, String description) {

		return _collectionPersistenceFinderByG_N_D.filterCount(
			finderCache, new Object[] {groupId, name, description}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<DDMStructure, NoSuchStructureException>
			_collectionPersistenceFinderByG_C_N_D;

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C_N_D(
		long groupId, long classNameId, String name, String description,
		int start, int end, OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_N_D.find(
			finderCache,
			new Object[] {new long[] {groupId}, classNameId, name, description},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByG_C_N_D_First(
			long groupId, long classNameId, String name, String description,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		return _collectionPersistenceFinderByG_C_N_D.findFirst(
			finderCache,
			new Object[] {new long[] {groupId}, classNameId, name, description},
			orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByG_C_N_D_First(
		long groupId, long classNameId, String name, String description,
		OrderByComparator<DDMStructure> orderByComparator) {

		return _collectionPersistenceFinderByG_C_N_D.fetchFirst(
			finderCache,
			new Object[] {new long[] {groupId}, classNameId, name, description},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_C_N_D(
		long groupId, long classNameId, String name, String description,
		int start, int end, OrderByComparator<DDMStructure> orderByComparator) {

		return _collectionPersistenceFinderByG_C_N_D.filterFind(
			finderCache,
			new Object[] {new long[] {groupId}, classNameId, name, description},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description,
		int start, int end, OrderByComparator<DDMStructure> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_C_N_D.filterFind(
			finderCache,
			new Object[] {groupIds, classNameId, name, description}, start, end,
			orderByComparator, groupIds);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description,
		int start, int end, OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_N_D.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), classNameId, name, description
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 */
	@Override
	public void removeByG_C_N_D(
		long groupId, long classNameId, String name, String description) {

		_collectionPersistenceFinderByG_C_N_D.remove(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, name, description
			});
	}

	/**
	 * Returns the number of ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByG_C_N_D(
		long groupId, long classNameId, String name, String description) {

		return _collectionPersistenceFinderByG_C_N_D.count(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, name, description
			});
	}

	/**
	 * Returns the number of ddm structures where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description) {

		return _collectionPersistenceFinderByG_C_N_D.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), classNameId, name, description
			});
	}

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_N_D(
		long groupId, long classNameId, String name, String description) {

		return _collectionPersistenceFinderByG_C_N_D.filterCount(
			finderCache,
			new Object[] {new long[] {groupId}, classNameId, name, description},
			groupId);
	}

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_C_N_D.filterCount(
			finderCache,
			new Object[] {groupIds, classNameId, name, description}, groupIds);
	}

	public DDMStructurePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DDMStructure.class);

		setModelImplClass(DDMStructureImpl.class);
		setModelPKClass(long.class);

		setTable(DDMStructureTable.INSTANCE);
	}

	/**
	 * Creates a new ddm structure with the primary key. Does not add the ddm structure to the database.
	 *
	 * @param structureId the primary key for the new ddm structure
	 * @return the new ddm structure
	 */
	@Override
	public DDMStructure create(long structureId) {
		DDMStructure ddmStructure = new DDMStructureImpl();

		ddmStructure.setNew(true);
		ddmStructure.setPrimaryKey(structureId);

		String uuid = PortalUUIDUtil.generate();

		ddmStructure.setUuid(uuid);

		ddmStructure.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ddmStructure;
	}

	/**
	 * Removes the ddm structure with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param structureId the primary key of the ddm structure
	 * @return the ddm structure that was removed
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure remove(long structureId)
		throws NoSuchStructureException {

		return remove((Serializable)structureId);
	}

	@Override
	protected DDMStructure removeImpl(DDMStructure ddmStructure) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddmStructure)) {
				ddmStructure = (DDMStructure)session.get(
					DDMStructureImpl.class, ddmStructure.getPrimaryKeyObj());
			}

			if ((ddmStructure != null) &&
				ctPersistenceHelper.isRemove(ddmStructure)) {

				session.delete(ddmStructure);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmStructure != null) {
			clearCache(ddmStructure);
		}

		return ddmStructure;
	}

	@Override
	public DDMStructure updateImpl(DDMStructure ddmStructure) {
		boolean isNew = ddmStructure.isNew();

		if (!(ddmStructure instanceof DDMStructureModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddmStructure.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ddmStructure);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddmStructure proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDMStructure implementation " +
					ddmStructure.getClass());
		}

		DDMStructureModelImpl ddmStructureModelImpl =
			(DDMStructureModelImpl)ddmStructure;

		if (Validator.isNull(ddmStructure.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			ddmStructure.setUuid(uuid);
		}

		if (Validator.isNull(ddmStructure.getExternalReferenceCode())) {
			ddmStructure.setExternalReferenceCode(ddmStructure.getUuid());
		}
		else {
			if (!Objects.equals(
					ddmStructureModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					ddmStructure.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = ddmStructure.getCompanyId();

					long groupId = ddmStructure.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = ddmStructure.getPrimaryKey();
					}

					try {
						ddmStructure.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								DDMStructure.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								ddmStructure.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (ddmStructure.getCreateDate() == null)) {
			if (serviceContext == null) {
				ddmStructure.setCreateDate(date);
			}
			else {
				ddmStructure.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!ddmStructureModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				ddmStructure.setModifiedDate(date);
			}
			else {
				ddmStructure.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddmStructure)) {
				if (!isNew) {
					session.evict(
						DDMStructureImpl.class,
						ddmStructure.getPrimaryKeyObj());
				}

				session.save(ddmStructure);
			}
			else {
				ddmStructure = (DDMStructure)session.merge(ddmStructure);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ddmStructure, false);

		if (isNew) {
			ddmStructure.setNew(false);
		}

		ddmStructure.resetOriginalValues();

		return ddmStructure;
	}

	/**
	 * Returns the ddm structure with the primary key or throws a <code>NoSuchStructureException</code> if it could not be found.
	 *
	 * @param structureId the primary key of the ddm structure
	 * @return the ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure findByPrimaryKey(long structureId)
		throws NoSuchStructureException {

		return findByPrimaryKey((Serializable)structureId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the ddm structure with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param structureId the primary key of the ddm structure
	 * @return the ddm structure, or <code>null</code> if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure fetchByPrimaryKey(long structureId) {
		return fetchByPrimaryKey((Serializable)structureId);
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
		return "structureId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDMSTRUCTURE;
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
		return DDMStructureModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDMStructure";
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
		ctMergeColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctMergeColumnNames.add("versionUserId");
		ctMergeColumnNames.add("versionUserName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("parentStructureId");
		ctStrictColumnNames.add("classNameId");
		ctMergeColumnNames.add("structureKey");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("definition");
		ctMergeColumnNames.add("storageType");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("structureId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId", "classNameId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "classNameId", "structureKey"});
	}

	/**
	 * Initializes the ddm structure persistence.
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
			_SQL_SELECT_DDMSTRUCTURE_WHERE, _SQL_COUNT_DDMSTRUCTURE_WHERE,
			DDMStructureModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"ddmStructure.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, DDMStructure::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(DDMStructure::getUuid),
				DDMStructure::getGroupId),
			_SQL_SELECT_DDMSTRUCTURE_WHERE, "",
			new FinderColumn<>(
				"ddmStructure.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, DDMStructure::getUuid),
			new FinderColumn<>(
				"ddmStructure.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, DDMStructure::getGroupId));

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
				_SQL_SELECT_DDMSTRUCTURE_WHERE, _SQL_COUNT_DDMSTRUCTURE_WHERE,
				DDMStructureModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"ddmStructure.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, DDMStructure::getUuid),
				new FinderColumn<>(
					"ddmStructure.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, DDMStructure::getCompanyId));

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
				_SQL_SELECT_DDMSTRUCTURE_WHERE, _SQL_COUNT_DDMSTRUCTURE_WHERE,
				DDMStructureModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new ArrayableFinderColumn<>(
					"ddmStructure.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, DDMStructure::getGroupId));

		_collectionPersistenceFinderByParentStructureId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByParentStructureId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"parentStructureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByParentStructureId",
					new String[] {Long.class.getName()},
					new String[] {"parentStructureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByParentStructureId",
					new String[] {Long.class.getName()},
					new String[] {"parentStructureId"}, false),
				_SQL_SELECT_DDMSTRUCTURE_WHERE, _SQL_COUNT_DDMSTRUCTURE_WHERE,
				DDMStructureModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"ddmStructure.", "parentStructureId",
					FinderColumn.Type.LONG, "=", true, true,
					DDMStructure::getParentStructureId));

		_collectionPersistenceFinderByStructureKey =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByStructureKey",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"structureKey"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByStructureKey", new String[] {String.class.getName()},
					new String[] {"structureKey"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByStructureKey",
					new String[] {String.class.getName()},
					new String[] {"structureKey"}, 0, 1, false, null),
				_SQL_SELECT_DDMSTRUCTURE_WHERE, _SQL_COUNT_DDMSTRUCTURE_WHERE,
				DDMStructureModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"ddmStructure.", "structureKey", FinderColumn.Type.STRING,
					"=", true, true, DDMStructure::getStructureKey));

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
					new String[] {"groupId", "parentStructureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "parentStructureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "parentStructureId"}, false),
				_SQL_SELECT_DDMSTRUCTURE_WHERE, _SQL_COUNT_DDMSTRUCTURE_WHERE,
				DDMStructureModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"ddmStructure.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, DDMStructure::getGroupId),
				new FinderColumn<>(
					"ddmStructure.", "parentStructureId",
					FinderColumn.Type.LONG, "=", true, true,
					DDMStructure::getParentStructureId));

		_collectionPersistenceFinderByG_C =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "classNameId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "classNameId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_C",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "classNameId"}, false),
				_SQL_SELECT_DDMSTRUCTURE_WHERE, _SQL_COUNT_DDMSTRUCTURE_WHERE,
				DDMStructureModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new ArrayableFinderColumn<>(
					"ddmStructure.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, DDMStructure::getGroupId),
				new FinderColumn<>(
					"ddmStructure.", "classNameId", FinderColumn.Type.LONG, "=",
					true, true, DDMStructure::getClassNameId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, false),
			_SQL_SELECT_DDMSTRUCTURE_WHERE, _SQL_COUNT_DDMSTRUCTURE_WHERE,
			DDMStructureModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"ddmStructure.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, DDMStructure::getCompanyId),
			new FinderColumn<>(
				"ddmStructure.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, DDMStructure::getClassNameId));

		_uniquePersistenceFinderByERC_G_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G_C",
				new String[] {
					String.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"externalReferenceCode", "groupId", "classNameId"
				},
				0, 1, false,
				convertNullFunction(DDMStructure::getExternalReferenceCode),
				DDMStructure::getGroupId, DDMStructure::getClassNameId),
			_SQL_SELECT_DDMSTRUCTURE_WHERE, "",
			new FinderColumn<>(
				"ddmStructure.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				DDMStructure::getExternalReferenceCode),
			new FinderColumn<>(
				"ddmStructure.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, DDMStructure::getGroupId),
			new FinderColumn<>(
				"ddmStructure.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, DDMStructure::getClassNameId));

		_uniquePersistenceFinderByG_C_S = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_C_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "classNameId", "structureKey"}, 0, 4,
				false, DDMStructure::getGroupId, DDMStructure::getClassNameId,
				convertNullFunction(DDMStructure::getStructureKey)),
			_SQL_SELECT_DDMSTRUCTURE_WHERE, "",
			new FinderColumn<>(
				"ddmStructure.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, DDMStructure::getGroupId),
			new FinderColumn<>(
				"ddmStructure.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, DDMStructure::getClassNameId),
			new FinderColumn<>(
				"ddmStructure.", "structureKey", FinderColumn.Type.STRING, "=",
				true, true, DDMStructure::getStructureKey));

		_collectionPersistenceFinderByG_N_D =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N_D",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name", "description"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N_D",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {"groupId", "name", "description"}, 0, 6, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_N_D",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {"groupId", "name", "description"}, 0, 6,
					false, null),
				_SQL_SELECT_DDMSTRUCTURE_WHERE, _SQL_COUNT_DDMSTRUCTURE_WHERE,
				DDMStructureModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"ddmStructure.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, DDMStructure::getGroupId),
				new FinderColumn<>(
					"ddmStructure.", "name", FinderColumn.Type.STRING, "=",
					true, true, DDMStructure::getName),
				new FinderColumn<>(
					"ddmStructure.", "description", FinderColumn.Type.STRING,
					"=", true, true, DDMStructure::getDescription));

		_collectionPersistenceFinderByG_C_N_D =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_N_D",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "name", "description"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_N_D",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), String.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "name", "description"
					},
					0, 12, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_C_N_D",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), String.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "name", "description"
					},
					0, 12, false, null),
				_SQL_SELECT_DDMSTRUCTURE_WHERE, _SQL_COUNT_DDMSTRUCTURE_WHERE,
				DDMStructureModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new ArrayableFinderColumn<>(
					"ddmStructure.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, DDMStructure::getGroupId),
				new FinderColumn<>(
					"ddmStructure.", "classNameId", FinderColumn.Type.LONG, "=",
					true, true, DDMStructure::getClassNameId),
				new FinderColumn<>(
					"ddmStructure.", "name", FinderColumn.Type.STRING, "=",
					true, true, DDMStructure::getName),
				new FinderColumn<>(
					"ddmStructure.", "description", FinderColumn.Type.STRING,
					"=", true, true, DDMStructure::getDescription));

		DDMStructureUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDMStructureUtil.setPersistence(null);

		entityCache.removeCache(DDMStructureImpl.class.getName());
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		DDMStructureModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DDMSTRUCTURE =
		"SELECT ddmStructure FROM DDMStructure ddmStructure";

	private static final String _SQL_SELECT_DDMSTRUCTURE_WHERE =
		"SELECT ddmStructure FROM DDMStructure ddmStructure WHERE ";

	private static final String _SQL_COUNT_DDMSTRUCTURE_WHERE =
		"SELECT COUNT(ddmStructure) FROM DDMStructure ddmStructure WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1858720436