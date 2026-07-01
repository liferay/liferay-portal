/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.exception.NoSuchStructureLayoutException;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayoutTable;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutModelImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureLayoutPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureLayoutUtil;
import com.liferay.dynamic.data.mapping.service.persistence.impl.constants.DDMPersistenceConstants;
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
 * The persistence implementation for the ddm structure layout service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDMStructureLayoutPersistence.class)
public class DDMStructureLayoutPersistenceImpl
	extends BasePersistenceImpl
		<DDMStructureLayout, NoSuchStructureLayoutException>
	implements DDMStructureLayoutPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDMStructureLayoutUtil</code> to access the ddm structure layout persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDMStructureLayoutImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DDMStructureLayout, NoSuchStructureLayoutException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the ddm structure layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm structure layouts
	 * @param end the upper bound of the range of ddm structure layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structure layouts
	 */
	@Override
	public List<DDMStructureLayout> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DDMStructureLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm structure layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure layout
	 * @throws NoSuchStructureLayoutException if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout findByUuid_First(
			String uuid,
			OrderByComparator<DDMStructureLayout> orderByComparator)
		throws NoSuchStructureLayoutException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first ddm structure layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure layout, or <code>null</code> if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout fetchByUuid_First(
		String uuid, OrderByComparator<DDMStructureLayout> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the ddm structure layouts where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of ddm structure layouts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching ddm structure layouts
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<DDMStructureLayout, NoSuchStructureLayoutException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the ddm structure layout where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchStructureLayoutException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddm structure layout
	 * @throws NoSuchStructureLayoutException if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout findByUUID_G(String uuid, long groupId)
		throws NoSuchStructureLayoutException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the ddm structure layout where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm structure layout, or <code>null</code> if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the ddm structure layout where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the ddm structure layout that was removed
	 */
	@Override
	public DDMStructureLayout removeByUUID_G(String uuid, long groupId)
		throws NoSuchStructureLayoutException {

		DDMStructureLayout ddmStructureLayout = findByUUID_G(uuid, groupId);

		return remove(ddmStructureLayout);
	}

	/**
	 * Returns the number of ddm structure layouts where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching ddm structure layouts
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<DDMStructureLayout, NoSuchStructureLayoutException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the ddm structure layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm structure layouts
	 * @param end the upper bound of the range of ddm structure layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structure layouts
	 */
	@Override
	public List<DDMStructureLayout> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMStructureLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure layout
	 * @throws NoSuchStructureLayoutException if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DDMStructureLayout> orderByComparator)
		throws NoSuchStructureLayoutException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first ddm structure layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure layout, or <code>null</code> if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DDMStructureLayout> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the ddm structure layouts where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of ddm structure layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching ddm structure layouts
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<DDMStructureLayout, NoSuchStructureLayoutException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the ddm structure layouts where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm structure layouts
	 * @param end the upper bound of the range of ddm structure layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structure layouts
	 */
	@Override
	public List<DDMStructureLayout> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDMStructureLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm structure layout in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure layout
	 * @throws NoSuchStructureLayoutException if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout findByGroupId_First(
			long groupId,
			OrderByComparator<DDMStructureLayout> orderByComparator)
		throws NoSuchStructureLayoutException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first ddm structure layout in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure layout, or <code>null</code> if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout fetchByGroupId_First(
		long groupId, OrderByComparator<DDMStructureLayout> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the ddm structure layouts where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of ddm structure layouts where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching ddm structure layouts
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<DDMStructureLayout, NoSuchStructureLayoutException>
			_collectionPersistenceFinderByStructureLayoutKey;

	/**
	 * Returns an ordered range of all the ddm structure layouts where structureLayoutKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param structureLayoutKey the structure layout key
	 * @param start the lower bound of the range of ddm structure layouts
	 * @param end the upper bound of the range of ddm structure layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structure layouts
	 */
	@Override
	public List<DDMStructureLayout> findByStructureLayoutKey(
		String structureLayoutKey, int start, int end,
		OrderByComparator<DDMStructureLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByStructureLayoutKey.find(
			finderCache, new Object[] {structureLayoutKey}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure layout in the ordered set where structureLayoutKey = &#63;.
	 *
	 * @param structureLayoutKey the structure layout key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure layout
	 * @throws NoSuchStructureLayoutException if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout findByStructureLayoutKey_First(
			String structureLayoutKey,
			OrderByComparator<DDMStructureLayout> orderByComparator)
		throws NoSuchStructureLayoutException {

		return _collectionPersistenceFinderByStructureLayoutKey.findFirst(
			finderCache, new Object[] {structureLayoutKey}, orderByComparator);
	}

	/**
	 * Returns the first ddm structure layout in the ordered set where structureLayoutKey = &#63;.
	 *
	 * @param structureLayoutKey the structure layout key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure layout, or <code>null</code> if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout fetchByStructureLayoutKey_First(
		String structureLayoutKey,
		OrderByComparator<DDMStructureLayout> orderByComparator) {

		return _collectionPersistenceFinderByStructureLayoutKey.fetchFirst(
			finderCache, new Object[] {structureLayoutKey}, orderByComparator);
	}

	/**
	 * Removes all the ddm structure layouts where structureLayoutKey = &#63; from the database.
	 *
	 * @param structureLayoutKey the structure layout key
	 */
	@Override
	public void removeByStructureLayoutKey(String structureLayoutKey) {
		_collectionPersistenceFinderByStructureLayoutKey.remove(
			finderCache, new Object[] {structureLayoutKey});
	}

	/**
	 * Returns the number of ddm structure layouts where structureLayoutKey = &#63;.
	 *
	 * @param structureLayoutKey the structure layout key
	 * @return the number of matching ddm structure layouts
	 */
	@Override
	public int countByStructureLayoutKey(String structureLayoutKey) {
		return _collectionPersistenceFinderByStructureLayoutKey.count(
			finderCache, new Object[] {structureLayoutKey});
	}

	private UniquePersistenceFinder
		<DDMStructureLayout, NoSuchStructureLayoutException>
			_uniquePersistenceFinderByStructureVersionId;

	/**
	 * Returns the ddm structure layout where structureVersionId = &#63; or throws a <code>NoSuchStructureLayoutException</code> if it could not be found.
	 *
	 * @param structureVersionId the structure version ID
	 * @return the matching ddm structure layout
	 * @throws NoSuchStructureLayoutException if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout findByStructureVersionId(long structureVersionId)
		throws NoSuchStructureLayoutException {

		return _uniquePersistenceFinderByStructureVersionId.find(
			finderCache, new Object[] {structureVersionId});
	}

	/**
	 * Returns the ddm structure layout where structureVersionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param structureVersionId the structure version ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm structure layout, or <code>null</code> if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout fetchByStructureVersionId(
		long structureVersionId, boolean useFinderCache) {

		return _uniquePersistenceFinderByStructureVersionId.fetch(
			finderCache, new Object[] {structureVersionId}, useFinderCache);
	}

	/**
	 * Removes the ddm structure layout where structureVersionId = &#63; from the database.
	 *
	 * @param structureVersionId the structure version ID
	 * @return the ddm structure layout that was removed
	 */
	@Override
	public DDMStructureLayout removeByStructureVersionId(
			long structureVersionId)
		throws NoSuchStructureLayoutException {

		DDMStructureLayout ddmStructureLayout = findByStructureVersionId(
			structureVersionId);

		return remove(ddmStructureLayout);
	}

	/**
	 * Returns the number of ddm structure layouts where structureVersionId = &#63;.
	 *
	 * @param structureVersionId the structure version ID
	 * @return the number of matching ddm structure layouts
	 */
	@Override
	public int countByStructureVersionId(long structureVersionId) {
		return _uniquePersistenceFinderByStructureVersionId.count(
			finderCache, new Object[] {structureVersionId});
	}

	private CollectionPersistenceFinder
		<DDMStructureLayout, NoSuchStructureLayoutException>
			_collectionPersistenceFinderByG_C;

	/**
	 * Returns an ordered range of all the ddm structure layouts where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structure layouts
	 * @param end the upper bound of the range of ddm structure layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structure layouts
	 */
	@Override
	public List<DDMStructureLayout> findByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<DDMStructureLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C.find(
			finderCache, new Object[] {groupId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure layout in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure layout
	 * @throws NoSuchStructureLayoutException if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout findByG_C_First(
			long groupId, long classNameId,
			OrderByComparator<DDMStructureLayout> orderByComparator)
		throws NoSuchStructureLayoutException {

		return _collectionPersistenceFinderByG_C.findFirst(
			finderCache, new Object[] {groupId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first ddm structure layout in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure layout, or <code>null</code> if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout fetchByG_C_First(
		long groupId, long classNameId,
		OrderByComparator<DDMStructureLayout> orderByComparator) {

		return _collectionPersistenceFinderByG_C.fetchFirst(
			finderCache, new Object[] {groupId, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the ddm structure layouts where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_C(long groupId, long classNameId) {
		_collectionPersistenceFinderByG_C.remove(
			finderCache, new Object[] {groupId, classNameId});
	}

	/**
	 * Returns the number of ddm structure layouts where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structure layouts
	 */
	@Override
	public int countByG_C(long groupId, long classNameId) {
		return _collectionPersistenceFinderByG_C.count(
			finderCache, new Object[] {groupId, classNameId});
	}

	private UniquePersistenceFinder
		<DDMStructureLayout, NoSuchStructureLayoutException>
			_uniquePersistenceFinderByG_C_S;

	/**
	 * Returns the ddm structure layout where groupId = &#63; and classNameId = &#63; and structureLayoutKey = &#63; or throws a <code>NoSuchStructureLayoutException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureLayoutKey the structure layout key
	 * @return the matching ddm structure layout
	 * @throws NoSuchStructureLayoutException if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout findByG_C_S(
			long groupId, long classNameId, String structureLayoutKey)
		throws NoSuchStructureLayoutException {

		return _uniquePersistenceFinderByG_C_S.find(
			finderCache,
			new Object[] {groupId, classNameId, structureLayoutKey});
	}

	/**
	 * Returns the ddm structure layout where groupId = &#63; and classNameId = &#63; and structureLayoutKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureLayoutKey the structure layout key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm structure layout, or <code>null</code> if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout fetchByG_C_S(
		long groupId, long classNameId, String structureLayoutKey,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_C_S.fetch(
			finderCache,
			new Object[] {groupId, classNameId, structureLayoutKey},
			useFinderCache);
	}

	/**
	 * Removes the ddm structure layout where groupId = &#63; and classNameId = &#63; and structureLayoutKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureLayoutKey the structure layout key
	 * @return the ddm structure layout that was removed
	 */
	@Override
	public DDMStructureLayout removeByG_C_S(
			long groupId, long classNameId, String structureLayoutKey)
		throws NoSuchStructureLayoutException {

		DDMStructureLayout ddmStructureLayout = findByG_C_S(
			groupId, classNameId, structureLayoutKey);

		return remove(ddmStructureLayout);
	}

	/**
	 * Returns the number of ddm structure layouts where groupId = &#63; and classNameId = &#63; and structureLayoutKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureLayoutKey the structure layout key
	 * @return the number of matching ddm structure layouts
	 */
	@Override
	public int countByG_C_S(
		long groupId, long classNameId, String structureLayoutKey) {

		return _uniquePersistenceFinderByG_C_S.count(
			finderCache,
			new Object[] {groupId, classNameId, structureLayoutKey});
	}

	private CollectionPersistenceFinder
		<DDMStructureLayout, NoSuchStructureLayoutException>
			_collectionPersistenceFinderByG_C_SV;

	/**
	 * Returns an ordered range of all the ddm structure layouts where groupId = &#63; and classNameId = &#63; and structureVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureVersionId the structure version ID
	 * @param start the lower bound of the range of ddm structure layouts
	 * @param end the upper bound of the range of ddm structure layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structure layouts
	 */
	@Override
	public List<DDMStructureLayout> findByG_C_SV(
		long groupId, long classNameId, long structureVersionId, int start,
		int end, OrderByComparator<DDMStructureLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_SV.find(
			finderCache,
			new Object[] {groupId, classNameId, structureVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure layout in the ordered set where groupId = &#63; and classNameId = &#63; and structureVersionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureVersionId the structure version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure layout
	 * @throws NoSuchStructureLayoutException if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout findByG_C_SV_First(
			long groupId, long classNameId, long structureVersionId,
			OrderByComparator<DDMStructureLayout> orderByComparator)
		throws NoSuchStructureLayoutException {

		return _collectionPersistenceFinderByG_C_SV.findFirst(
			finderCache,
			new Object[] {groupId, classNameId, structureVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first ddm structure layout in the ordered set where groupId = &#63; and classNameId = &#63; and structureVersionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureVersionId the structure version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure layout, or <code>null</code> if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout fetchByG_C_SV_First(
		long groupId, long classNameId, long structureVersionId,
		OrderByComparator<DDMStructureLayout> orderByComparator) {

		return _collectionPersistenceFinderByG_C_SV.fetchFirst(
			finderCache,
			new Object[] {groupId, classNameId, structureVersionId},
			orderByComparator);
	}

	/**
	 * Removes all the ddm structure layouts where groupId = &#63; and classNameId = &#63; and structureVersionId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureVersionId the structure version ID
	 */
	@Override
	public void removeByG_C_SV(
		long groupId, long classNameId, long structureVersionId) {

		_collectionPersistenceFinderByG_C_SV.remove(
			finderCache,
			new Object[] {groupId, classNameId, structureVersionId});
	}

	/**
	 * Returns the number of ddm structure layouts where groupId = &#63; and classNameId = &#63; and structureVersionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureVersionId the structure version ID
	 * @return the number of matching ddm structure layouts
	 */
	@Override
	public int countByG_C_SV(
		long groupId, long classNameId, long structureVersionId) {

		return _collectionPersistenceFinderByG_C_SV.count(
			finderCache,
			new Object[] {groupId, classNameId, structureVersionId});
	}

	public DDMStructureLayoutPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DDMStructureLayout.class);

		setModelImplClass(DDMStructureLayoutImpl.class);
		setModelPKClass(long.class);

		setTable(DDMStructureLayoutTable.INSTANCE);
	}

	/**
	 * Creates a new ddm structure layout with the primary key. Does not add the ddm structure layout to the database.
	 *
	 * @param structureLayoutId the primary key for the new ddm structure layout
	 * @return the new ddm structure layout
	 */
	@Override
	public DDMStructureLayout create(long structureLayoutId) {
		DDMStructureLayout ddmStructureLayout = new DDMStructureLayoutImpl();

		ddmStructureLayout.setNew(true);
		ddmStructureLayout.setPrimaryKey(structureLayoutId);

		String uuid = PortalUUIDUtil.generate();

		ddmStructureLayout.setUuid(uuid);

		ddmStructureLayout.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ddmStructureLayout;
	}

	/**
	 * Removes the ddm structure layout with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param structureLayoutId the primary key of the ddm structure layout
	 * @return the ddm structure layout that was removed
	 * @throws NoSuchStructureLayoutException if a ddm structure layout with the primary key could not be found
	 */
	@Override
	public DDMStructureLayout remove(long structureLayoutId)
		throws NoSuchStructureLayoutException {

		return remove((Serializable)structureLayoutId);
	}

	@Override
	protected DDMStructureLayout removeImpl(
		DDMStructureLayout ddmStructureLayout) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddmStructureLayout)) {
				ddmStructureLayout = (DDMStructureLayout)session.get(
					DDMStructureLayoutImpl.class,
					ddmStructureLayout.getPrimaryKeyObj());
			}

			if ((ddmStructureLayout != null) &&
				ctPersistenceHelper.isRemove(ddmStructureLayout)) {

				session.delete(ddmStructureLayout);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmStructureLayout != null) {
			clearCache(ddmStructureLayout);
		}

		return ddmStructureLayout;
	}

	@Override
	public DDMStructureLayout updateImpl(
		DDMStructureLayout ddmStructureLayout) {

		boolean isNew = ddmStructureLayout.isNew();

		if (!(ddmStructureLayout instanceof DDMStructureLayoutModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddmStructureLayout.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ddmStructureLayout);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddmStructureLayout proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDMStructureLayout implementation " +
					ddmStructureLayout.getClass());
		}

		DDMStructureLayoutModelImpl ddmStructureLayoutModelImpl =
			(DDMStructureLayoutModelImpl)ddmStructureLayout;

		if (Validator.isNull(ddmStructureLayout.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			ddmStructureLayout.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (ddmStructureLayout.getCreateDate() == null)) {
			if (serviceContext == null) {
				ddmStructureLayout.setCreateDate(date);
			}
			else {
				ddmStructureLayout.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!ddmStructureLayoutModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				ddmStructureLayout.setModifiedDate(date);
			}
			else {
				ddmStructureLayout.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddmStructureLayout)) {
				if (!isNew) {
					session.evict(
						DDMStructureLayoutImpl.class,
						ddmStructureLayout.getPrimaryKeyObj());
				}

				session.save(ddmStructureLayout);
			}
			else {
				ddmStructureLayout = (DDMStructureLayout)session.merge(
					ddmStructureLayout);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ddmStructureLayout, false);

		if (isNew) {
			ddmStructureLayout.setNew(false);
		}

		ddmStructureLayout.resetOriginalValues();

		return ddmStructureLayout;
	}

	/**
	 * Returns the ddm structure layout with the primary key or throws a <code>NoSuchStructureLayoutException</code> if it could not be found.
	 *
	 * @param structureLayoutId the primary key of the ddm structure layout
	 * @return the ddm structure layout
	 * @throws NoSuchStructureLayoutException if a ddm structure layout with the primary key could not be found
	 */
	@Override
	public DDMStructureLayout findByPrimaryKey(long structureLayoutId)
		throws NoSuchStructureLayoutException {

		return findByPrimaryKey((Serializable)structureLayoutId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the ddm structure layout with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param structureLayoutId the primary key of the ddm structure layout
	 * @return the ddm structure layout, or <code>null</code> if a ddm structure layout with the primary key could not be found
	 */
	@Override
	public DDMStructureLayout fetchByPrimaryKey(long structureLayoutId) {
		return fetchByPrimaryKey((Serializable)structureLayoutId);
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
		return "structureLayoutId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDMSTRUCTURELAYOUT;
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
		return DDMStructureLayoutModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDMStructureLayout";
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
		ctMergeColumnNames.add("structureLayoutKey");
		ctMergeColumnNames.add("structureVersionId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("definition");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("structureLayoutId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "classNameId", "structureLayoutKey"});
	}

	/**
	 * Initializes the ddm structure layout persistence.
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
			_SQL_SELECT_DDMSTRUCTURELAYOUT_WHERE,
			_SQL_COUNT_DDMSTRUCTURELAYOUT_WHERE,
			DDMStructureLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"ddmStructureLayout.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				DDMStructureLayout::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(DDMStructureLayout::getUuid),
				DDMStructureLayout::getGroupId),
			_SQL_SELECT_DDMSTRUCTURELAYOUT_WHERE, "",
			new FinderColumn<>(
				"ddmStructureLayout.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				DDMStructureLayout::getUuid),
			new FinderColumn<>(
				"ddmStructureLayout.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, DDMStructureLayout::getGroupId));

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
				_SQL_SELECT_DDMSTRUCTURELAYOUT_WHERE,
				_SQL_COUNT_DDMSTRUCTURELAYOUT_WHERE,
				DDMStructureLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"ddmStructureLayout.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					DDMStructureLayout::getUuid),
				new FinderColumn<>(
					"ddmStructureLayout.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, DDMStructureLayout::getCompanyId));

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
				_SQL_SELECT_DDMSTRUCTURELAYOUT_WHERE,
				_SQL_COUNT_DDMSTRUCTURELAYOUT_WHERE,
				DDMStructureLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"ddmStructureLayout.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, DDMStructureLayout::getGroupId));

		_collectionPersistenceFinderByStructureLayoutKey =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByStructureLayoutKey",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"structureLayoutKey"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByStructureLayoutKey",
					new String[] {String.class.getName()},
					new String[] {"structureLayoutKey"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByStructureLayoutKey",
					new String[] {String.class.getName()},
					new String[] {"structureLayoutKey"}, 0, 1, false, null),
				_SQL_SELECT_DDMSTRUCTURELAYOUT_WHERE,
				_SQL_COUNT_DDMSTRUCTURELAYOUT_WHERE,
				DDMStructureLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"ddmStructureLayout.", "structureLayoutKey",
					FinderColumn.Type.STRING, "=", true, true,
					DDMStructureLayout::getStructureLayoutKey));

		_uniquePersistenceFinderByStructureVersionId =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByStructureVersionId",
					new String[] {Long.class.getName()},
					new String[] {"structureVersionId"}, 0, 0, false,
					DDMStructureLayout::getStructureVersionId),
				_SQL_SELECT_DDMSTRUCTURELAYOUT_WHERE, "",
				new FinderColumn<>(
					"ddmStructureLayout.", "structureVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					DDMStructureLayout::getStructureVersionId));

		_collectionPersistenceFinderByG_C = new CollectionPersistenceFinder<>(
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
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "classNameId"}, false),
			_SQL_SELECT_DDMSTRUCTURELAYOUT_WHERE,
			_SQL_COUNT_DDMSTRUCTURELAYOUT_WHERE,
			DDMStructureLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"ddmStructureLayout.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, DDMStructureLayout::getGroupId),
			new FinderColumn<>(
				"ddmStructureLayout.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, DDMStructureLayout::getClassNameId));

		_uniquePersistenceFinderByG_C_S = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_C_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "classNameId", "structureLayoutKey"},
				0, 4, false, DDMStructureLayout::getGroupId,
				DDMStructureLayout::getClassNameId,
				convertNullFunction(DDMStructureLayout::getStructureLayoutKey)),
			_SQL_SELECT_DDMSTRUCTURELAYOUT_WHERE, "",
			new FinderColumn<>(
				"ddmStructureLayout.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, DDMStructureLayout::getGroupId),
			new FinderColumn<>(
				"ddmStructureLayout.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, DDMStructureLayout::getClassNameId),
			new FinderColumn<>(
				"ddmStructureLayout.", "structureLayoutKey",
				FinderColumn.Type.STRING, "=", true, true,
				DDMStructureLayout::getStructureLayoutKey));

		_collectionPersistenceFinderByG_C_SV =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_SV",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "structureVersionId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_SV",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "structureVersionId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_SV",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "structureVersionId"
					},
					false),
				_SQL_SELECT_DDMSTRUCTURELAYOUT_WHERE,
				_SQL_COUNT_DDMSTRUCTURELAYOUT_WHERE,
				DDMStructureLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"ddmStructureLayout.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, DDMStructureLayout::getGroupId),
				new FinderColumn<>(
					"ddmStructureLayout.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					DDMStructureLayout::getClassNameId),
				new FinderColumn<>(
					"ddmStructureLayout.", "structureVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					DDMStructureLayout::getStructureVersionId));

		DDMStructureLayoutUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDMStructureLayoutUtil.setPersistence(null);

		entityCache.removeCache(DDMStructureLayoutImpl.class.getName());
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
		DDMStructureLayoutModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DDMSTRUCTURELAYOUT =
		"SELECT ddmStructureLayout FROM DDMStructureLayout ddmStructureLayout";

	private static final String _SQL_SELECT_DDMSTRUCTURELAYOUT_WHERE =
		"SELECT ddmStructureLayout FROM DDMStructureLayout ddmStructureLayout WHERE ";

	private static final String _SQL_COUNT_DDMSTRUCTURELAYOUT_WHERE =
		"SELECT COUNT(ddmStructureLayout) FROM DDMStructureLayout ddmStructureLayout WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DDMStructureLayout exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DDMStructureLayoutPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:290623090