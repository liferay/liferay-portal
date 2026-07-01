/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.exception.NoSuchFormInstanceException;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceTable;
import com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceModelImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstancePersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstanceUtil;
import com.liferay.dynamic.data.mapping.service.persistence.impl.constants.DDMPersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
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
 * The persistence implementation for the ddm form instance service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDMFormInstancePersistence.class)
public class DDMFormInstancePersistenceImpl
	extends BasePersistenceImpl<DDMFormInstance, NoSuchFormInstanceException>
	implements DDMFormInstancePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDMFormInstanceUtil</code> to access the ddm form instance persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDMFormInstanceImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DDMFormInstance, NoSuchFormInstanceException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the ddm form instances where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm form instances
	 * @param end the upper bound of the range of ddm form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instances
	 */
	@Override
	public List<DDMFormInstance> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm form instance in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance
	 * @throws NoSuchFormInstanceException if a matching ddm form instance could not be found
	 */
	@Override
	public DDMFormInstance findByUuid_First(
			String uuid, OrderByComparator<DDMFormInstance> orderByComparator)
		throws NoSuchFormInstanceException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first ddm form instance in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance, or <code>null</code> if a matching ddm form instance could not be found
	 */
	@Override
	public DDMFormInstance fetchByUuid_First(
		String uuid, OrderByComparator<DDMFormInstance> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the ddm form instances where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of ddm form instances where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching ddm form instances
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<DDMFormInstance, NoSuchFormInstanceException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the ddm form instance where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFormInstanceException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddm form instance
	 * @throws NoSuchFormInstanceException if a matching ddm form instance could not be found
	 */
	@Override
	public DDMFormInstance findByUUID_G(String uuid, long groupId)
		throws NoSuchFormInstanceException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the ddm form instance where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm form instance, or <code>null</code> if a matching ddm form instance could not be found
	 */
	@Override
	public DDMFormInstance fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the ddm form instance where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the ddm form instance that was removed
	 */
	@Override
	public DDMFormInstance removeByUUID_G(String uuid, long groupId)
		throws NoSuchFormInstanceException {

		DDMFormInstance ddmFormInstance = findByUUID_G(uuid, groupId);

		return remove(ddmFormInstance);
	}

	/**
	 * Returns the number of ddm form instances where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching ddm form instances
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<DDMFormInstance, NoSuchFormInstanceException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the ddm form instances where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm form instances
	 * @param end the upper bound of the range of ddm form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instances
	 */
	@Override
	public List<DDMFormInstance> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm form instance in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance
	 * @throws NoSuchFormInstanceException if a matching ddm form instance could not be found
	 */
	@Override
	public DDMFormInstance findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DDMFormInstance> orderByComparator)
		throws NoSuchFormInstanceException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first ddm form instance in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance, or <code>null</code> if a matching ddm form instance could not be found
	 */
	@Override
	public DDMFormInstance fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DDMFormInstance> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the ddm form instances where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of ddm form instances where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching ddm form instances
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<DDMFormInstance, NoSuchFormInstanceException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the ddm form instances where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm form instances
	 * @param end the upper bound of the range of ddm form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instances
	 */
	@Override
	public List<DDMFormInstance> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {new long[] {groupId}}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm form instance in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance
	 * @throws NoSuchFormInstanceException if a matching ddm form instance could not be found
	 */
	@Override
	public DDMFormInstance findByGroupId_First(
			long groupId, OrderByComparator<DDMFormInstance> orderByComparator)
		throws NoSuchFormInstanceException {

		DDMFormInstance ddmFormInstance = fetchByGroupId_First(
			groupId, orderByComparator);

		if (ddmFormInstance != null) {
			return ddmFormInstance;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchFormInstanceException(sb.toString());
	}

	/**
	 * Returns the first ddm form instance in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance, or <code>null</code> if a matching ddm form instance could not be found
	 */
	@Override
	public DDMFormInstance fetchByGroupId_First(
		long groupId, OrderByComparator<DDMFormInstance> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm form instances that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm form instances
	 * @param end the upper bound of the range of ddm form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instances that the user has permission to view
	 */
	@Override
	public List<DDMFormInstance> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {new long[] {groupId}}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the ddm form instances that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm form instances
	 * @param end the upper bound of the range of ddm form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instances that the user has permission to view
	 */
	@Override
	public List<DDMFormInstance> filterFindByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupIds}, start, end, orderByComparator,
			groupIds);
	}

	/**
	 * Returns an ordered range of all the ddm form instances where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm form instances
	 * @param end the upper bound of the range of ddm form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instances
	 */
	@Override
	public List<DDMFormInstance> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds)}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the ddm form instances where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of ddm form instances where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching ddm form instances
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of ddm form instances where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching ddm form instances
	 */
	@Override
	public int countByGroupId(long[] groupIds) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds)});
	}

	/**
	 * Returns the number of ddm form instances that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching ddm form instances that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {new long[] {groupId}}, groupId);
	}

	/**
	 * Returns the number of ddm form instances that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching ddm form instances that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long[] groupIds) {
		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupIds}, groupIds);
	}

	private UniquePersistenceFinder
		<DDMFormInstance, NoSuchFormInstanceException>
			_uniquePersistenceFinderByStructureId;

	/**
	 * Returns the ddm form instance where structureId = &#63; or throws a <code>NoSuchFormInstanceException</code> if it could not be found.
	 *
	 * @param structureId the structure ID
	 * @return the matching ddm form instance
	 * @throws NoSuchFormInstanceException if a matching ddm form instance could not be found
	 */
	@Override
	public DDMFormInstance findByStructureId(long structureId)
		throws NoSuchFormInstanceException {

		return _uniquePersistenceFinderByStructureId.find(
			finderCache, new Object[] {structureId});
	}

	/**
	 * Returns the ddm form instance where structureId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param structureId the structure ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm form instance, or <code>null</code> if a matching ddm form instance could not be found
	 */
	@Override
	public DDMFormInstance fetchByStructureId(
		long structureId, boolean useFinderCache) {

		return _uniquePersistenceFinderByStructureId.fetch(
			finderCache, new Object[] {structureId}, useFinderCache);
	}

	/**
	 * Removes the ddm form instance where structureId = &#63; from the database.
	 *
	 * @param structureId the structure ID
	 * @return the ddm form instance that was removed
	 */
	@Override
	public DDMFormInstance removeByStructureId(long structureId)
		throws NoSuchFormInstanceException {

		DDMFormInstance ddmFormInstance = findByStructureId(structureId);

		return remove(ddmFormInstance);
	}

	/**
	 * Returns the number of ddm form instances where structureId = &#63;.
	 *
	 * @param structureId the structure ID
	 * @return the number of matching ddm form instances
	 */
	@Override
	public int countByStructureId(long structureId) {
		return _uniquePersistenceFinderByStructureId.count(
			finderCache, new Object[] {structureId});
	}

	public DDMFormInstancePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("settings", "settings_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DDMFormInstance.class);

		setModelImplClass(DDMFormInstanceImpl.class);
		setModelPKClass(long.class);

		setTable(DDMFormInstanceTable.INSTANCE);
	}

	/**
	 * Creates a new ddm form instance with the primary key. Does not add the ddm form instance to the database.
	 *
	 * @param formInstanceId the primary key for the new ddm form instance
	 * @return the new ddm form instance
	 */
	@Override
	public DDMFormInstance create(long formInstanceId) {
		DDMFormInstance ddmFormInstance = new DDMFormInstanceImpl();

		ddmFormInstance.setNew(true);
		ddmFormInstance.setPrimaryKey(formInstanceId);

		String uuid = PortalUUIDUtil.generate();

		ddmFormInstance.setUuid(uuid);

		ddmFormInstance.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ddmFormInstance;
	}

	/**
	 * Removes the ddm form instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param formInstanceId the primary key of the ddm form instance
	 * @return the ddm form instance that was removed
	 * @throws NoSuchFormInstanceException if a ddm form instance with the primary key could not be found
	 */
	@Override
	public DDMFormInstance remove(long formInstanceId)
		throws NoSuchFormInstanceException {

		return remove((Serializable)formInstanceId);
	}

	@Override
	protected DDMFormInstance removeImpl(DDMFormInstance ddmFormInstance) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddmFormInstance)) {
				ddmFormInstance = (DDMFormInstance)session.get(
					DDMFormInstanceImpl.class,
					ddmFormInstance.getPrimaryKeyObj());
			}

			if ((ddmFormInstance != null) &&
				ctPersistenceHelper.isRemove(ddmFormInstance)) {

				session.delete(ddmFormInstance);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmFormInstance != null) {
			clearCache(ddmFormInstance);
		}

		return ddmFormInstance;
	}

	@Override
	public DDMFormInstance updateImpl(DDMFormInstance ddmFormInstance) {
		boolean isNew = ddmFormInstance.isNew();

		if (!(ddmFormInstance instanceof DDMFormInstanceModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddmFormInstance.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ddmFormInstance);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddmFormInstance proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDMFormInstance implementation " +
					ddmFormInstance.getClass());
		}

		DDMFormInstanceModelImpl ddmFormInstanceModelImpl =
			(DDMFormInstanceModelImpl)ddmFormInstance;

		if (Validator.isNull(ddmFormInstance.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			ddmFormInstance.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (ddmFormInstance.getCreateDate() == null)) {
			if (serviceContext == null) {
				ddmFormInstance.setCreateDate(date);
			}
			else {
				ddmFormInstance.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!ddmFormInstanceModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				ddmFormInstance.setModifiedDate(date);
			}
			else {
				ddmFormInstance.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddmFormInstance)) {
				if (!isNew) {
					session.evict(
						DDMFormInstanceImpl.class,
						ddmFormInstance.getPrimaryKeyObj());
				}

				session.save(ddmFormInstance);
			}
			else {
				ddmFormInstance = (DDMFormInstance)session.merge(
					ddmFormInstance);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ddmFormInstance, false);

		if (isNew) {
			ddmFormInstance.setNew(false);
		}

		ddmFormInstance.resetOriginalValues();

		return ddmFormInstance;
	}

	/**
	 * Returns the ddm form instance with the primary key or throws a <code>NoSuchFormInstanceException</code> if it could not be found.
	 *
	 * @param formInstanceId the primary key of the ddm form instance
	 * @return the ddm form instance
	 * @throws NoSuchFormInstanceException if a ddm form instance with the primary key could not be found
	 */
	@Override
	public DDMFormInstance findByPrimaryKey(long formInstanceId)
		throws NoSuchFormInstanceException {

		return findByPrimaryKey((Serializable)formInstanceId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the ddm form instance with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param formInstanceId the primary key of the ddm form instance
	 * @return the ddm form instance, or <code>null</code> if a ddm form instance with the primary key could not be found
	 */
	@Override
	public DDMFormInstance fetchByPrimaryKey(long formInstanceId) {
		return fetchByPrimaryKey((Serializable)formInstanceId);
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
		return "formInstanceId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDMFORMINSTANCE;
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
		return DDMFormInstanceModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDMFormInstance";
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
		ctMergeColumnNames.add("versionUserId");
		ctMergeColumnNames.add("versionUserName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("structureId");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("settings_");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("formInstanceId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"structureId"});
	}

	/**
	 * Initializes the ddm form instance persistence.
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
			_SQL_SELECT_DDMFORMINSTANCE_WHERE, _SQL_COUNT_DDMFORMINSTANCE_WHERE,
			DDMFormInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"ddmFormInstance.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, DDMFormInstance::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(DDMFormInstance::getUuid),
				DDMFormInstance::getGroupId),
			_SQL_SELECT_DDMFORMINSTANCE_WHERE, "",
			new FinderColumn<>(
				"ddmFormInstance.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, DDMFormInstance::getUuid),
			new FinderColumn<>(
				"ddmFormInstance.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, DDMFormInstance::getGroupId));

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
				_SQL_SELECT_DDMFORMINSTANCE_WHERE,
				_SQL_COUNT_DDMFORMINSTANCE_WHERE,
				DDMFormInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"ddmFormInstance.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					DDMFormInstance::getUuid),
				new FinderColumn<>(
					"ddmFormInstance.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, DDMFormInstance::getCompanyId));

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
				_SQL_SELECT_DDMFORMINSTANCE_WHERE,
				_SQL_COUNT_DDMFORMINSTANCE_WHERE,
				DDMFormInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new ArrayableFinderColumn<>(
					"ddmFormInstance.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, DDMFormInstance::getGroupId));

		_uniquePersistenceFinderByStructureId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByStructureId",
				new String[] {Long.class.getName()},
				new String[] {"structureId"}, 0, 0, false,
				DDMFormInstance::getStructureId),
			_SQL_SELECT_DDMFORMINSTANCE_WHERE, "",
			new FinderColumn<>(
				"ddmFormInstance.", "structureId", FinderColumn.Type.LONG, "=",
				true, true, DDMFormInstance::getStructureId));

		DDMFormInstanceUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDMFormInstanceUtil.setPersistence(null);

		entityCache.removeCache(DDMFormInstanceImpl.class.getName());
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
		DDMFormInstanceModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DDMFORMINSTANCE =
		"SELECT ddmFormInstance FROM DDMFormInstance ddmFormInstance";

	private static final String _SQL_SELECT_DDMFORMINSTANCE_WHERE =
		"SELECT ddmFormInstance FROM DDMFormInstance ddmFormInstance WHERE ";

	private static final String _SQL_COUNT_DDMFORMINSTANCE_WHERE =
		"SELECT COUNT(ddmFormInstance) FROM DDMFormInstance ddmFormInstance WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DDMFormInstance exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormInstancePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "settings"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1297444098