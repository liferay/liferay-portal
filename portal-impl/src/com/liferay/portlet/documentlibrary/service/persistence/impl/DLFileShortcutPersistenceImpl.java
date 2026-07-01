/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.persistence.impl;

import com.liferay.document.library.kernel.exception.DuplicateDLFileShortcutExternalReferenceCodeException;
import com.liferay.document.library.kernel.exception.NoSuchFileShortcutException;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFileShortcutTable;
import com.liferay.document.library.kernel.service.persistence.DLFileShortcutPersistence;
import com.liferay.document.library.kernel.service.persistence.DLFileShortcutUtil;
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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portlet.documentlibrary.model.impl.DLFileShortcutImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileShortcutModelImpl;

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
 * The persistence implementation for the document library file shortcut service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DLFileShortcutPersistenceImpl
	extends BasePersistenceImpl<DLFileShortcut, NoSuchFileShortcutException>
	implements DLFileShortcutPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DLFileShortcutUtil</code> to access the document library file shortcut persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DLFileShortcutImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DLFileShortcut, NoSuchFileShortcutException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the document library file shortcuts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByUuid_First(
			String uuid, OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByUuid_First(
		String uuid, OrderByComparator<DLFileShortcut> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the document library file shortcuts where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of document library file shortcuts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private UniquePersistenceFinder<DLFileShortcut, NoSuchFileShortcutException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the document library file shortcut where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFileShortcutException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByUUID_G(String uuid, long groupId)
		throws NoSuchFileShortcutException {

		return _uniquePersistenceFinderByUUID_G.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	/**
	 * Returns the document library file shortcut where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId},
			useFinderCache);
	}

	/**
	 * Removes the document library file shortcut where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the document library file shortcut that was removed
	 */
	@Override
	public DLFileShortcut removeByUUID_G(String uuid, long groupId)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = findByUUID_G(uuid, groupId);

		return remove(dlFileShortcut);
	}

	/**
	 * Returns the number of document library file shortcuts where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<DLFileShortcut, NoSuchFileShortcutException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the document library file shortcuts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the document library file shortcuts where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of document library file shortcuts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<DLFileShortcut, NoSuchFileShortcutException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the document library file shortcuts where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByGroupId_First(
			long groupId, OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByGroupId_First(
		long groupId, OrderByComparator<DLFileShortcut> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public List<DLFileShortcut> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the document library file shortcuts where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of document library file shortcuts where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of document library file shortcuts that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, groupId);
	}

	private CollectionPersistenceFinder
		<DLFileShortcut, NoSuchFileShortcutException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the document library file shortcuts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByCompanyId_First(
			long companyId, OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByCompanyId_First(
		long companyId, OrderByComparator<DLFileShortcut> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the document library file shortcuts where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of document library file shortcuts where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<DLFileShortcut, NoSuchFileShortcutException>
			_collectionPersistenceFinderByToFileEntryId;

	/**
	 * Returns an ordered range of all the document library file shortcuts where toFileEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param toFileEntryId the to file entry ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByToFileEntryId(
		long toFileEntryId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByToFileEntryId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {toFileEntryId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where toFileEntryId = &#63;.
	 *
	 * @param toFileEntryId the to file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByToFileEntryId_First(
			long toFileEntryId,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		return _collectionPersistenceFinderByToFileEntryId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {toFileEntryId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where toFileEntryId = &#63;.
	 *
	 * @param toFileEntryId the to file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByToFileEntryId_First(
		long toFileEntryId,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return _collectionPersistenceFinderByToFileEntryId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {toFileEntryId},
			orderByComparator);
	}

	/**
	 * Removes all the document library file shortcuts where toFileEntryId = &#63; from the database.
	 *
	 * @param toFileEntryId the to file entry ID
	 */
	@Override
	public void removeByToFileEntryId(long toFileEntryId) {
		_collectionPersistenceFinderByToFileEntryId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {toFileEntryId});
	}

	/**
	 * Returns the number of document library file shortcuts where toFileEntryId = &#63;.
	 *
	 * @param toFileEntryId the to file entry ID
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByToFileEntryId(long toFileEntryId) {
		return _collectionPersistenceFinderByToFileEntryId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {toFileEntryId});
	}

	private FilterCollectionPersistenceFinder
		<DLFileShortcut, NoSuchFileShortcutException>
			_collectionPersistenceFinderByG_F;

	/**
	 * Returns an ordered range of all the document library file shortcuts where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByG_F(
		long groupId, long folderId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_F.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, folderId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByG_F_First(
			long groupId, long folderId,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		return _collectionPersistenceFinderByG_F.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, folderId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByG_F_First(
		long groupId, long folderId,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return _collectionPersistenceFinderByG_F.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, folderId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts that the user has permissions to view where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public List<DLFileShortcut> filterFindByG_F(
		long groupId, long folderId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return _collectionPersistenceFinderByG_F.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, folderId},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the document library file shortcuts where groupId = &#63; and folderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 */
	@Override
	public void removeByG_F(long groupId, long folderId) {
		_collectionPersistenceFinderByG_F.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, folderId});
	}

	/**
	 * Returns the number of document library file shortcuts where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByG_F(long groupId, long folderId) {
		return _collectionPersistenceFinderByG_F.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, folderId});
	}

	/**
	 * Returns the number of document library file shortcuts that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public int filterCountByG_F(long groupId, long folderId) {
		return _collectionPersistenceFinderByG_F.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, folderId},
			groupId);
	}

	private CollectionPersistenceFinder
		<DLFileShortcut, NoSuchFileShortcutException>
			_collectionPersistenceFinderByC_NotS;

	/**
	 * Returns all the document library file shortcuts where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByC_NotS(long companyId, int status) {
		return findByC_NotS(
			companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file shortcuts where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @return the range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByC_NotS(
		long companyId, int status, int start, int end) {

		return findByC_NotS(companyId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return findByC_NotS(
			companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_NotS.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByC_NotS_First(
			long companyId, int status,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		return _collectionPersistenceFinderByC_NotS.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, status},
			orderByComparator);
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByC_NotS_First(
		long companyId, int status,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return _collectionPersistenceFinderByC_NotS.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, status},
			orderByComparator);
	}

	/**
	 * Removes all the document library file shortcuts where companyId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_NotS(long companyId, int status) {
		_collectionPersistenceFinderByC_NotS.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, status});
	}

	/**
	 * Returns the number of document library file shortcuts where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByC_NotS(long companyId, int status) {
		return _collectionPersistenceFinderByC_NotS.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, status});
	}

	private FilterCollectionPersistenceFinder
		<DLFileShortcut, NoSuchFileShortcutException>
			_collectionPersistenceFinderByG_F_A;

	/**
	 * Returns an ordered range of all the document library file shortcuts where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByG_F_A(
		long groupId, long folderId, boolean active, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_F_A.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, active}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByG_F_A_First(
			long groupId, long folderId, boolean active,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		return _collectionPersistenceFinderByG_F_A.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, active}, orderByComparator);
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByG_F_A_First(
		long groupId, long folderId, boolean active,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return _collectionPersistenceFinderByG_F_A.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, active}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts that the user has permissions to view where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public List<DLFileShortcut> filterFindByG_F_A(
		long groupId, long folderId, boolean active, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return _collectionPersistenceFinderByG_F_A.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, active}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the document library file shortcuts where groupId = &#63; and folderId = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 */
	@Override
	public void removeByG_F_A(long groupId, long folderId, boolean active) {
		_collectionPersistenceFinderByG_F_A.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, active});
	}

	/**
	 * Returns the number of document library file shortcuts where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByG_F_A(long groupId, long folderId, boolean active) {
		return _collectionPersistenceFinderByG_F_A.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, active});
	}

	/**
	 * Returns the number of document library file shortcuts that the user has permission to view where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @return the number of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public int filterCountByG_F_A(long groupId, long folderId, boolean active) {
		return _collectionPersistenceFinderByG_F_A.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, active}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<DLFileShortcut, NoSuchFileShortcutException>
			_collectionPersistenceFinderByG_F_A_S;

	/**
	 * Returns an ordered range of all the document library file shortcuts where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByG_F_A_S(
		long groupId, long folderId, boolean active, int status, int start,
		int end, OrderByComparator<DLFileShortcut> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_F_A_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, active, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByG_F_A_S_First(
			long groupId, long folderId, boolean active, int status,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		return _collectionPersistenceFinderByG_F_A_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, active, status},
			orderByComparator);
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByG_F_A_S_First(
		long groupId, long folderId, boolean active, int status,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return _collectionPersistenceFinderByG_F_A_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, active, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts that the user has permissions to view where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public List<DLFileShortcut> filterFindByG_F_A_S(
		long groupId, long folderId, boolean active, int status, int start,
		int end, OrderByComparator<DLFileShortcut> orderByComparator) {

		return _collectionPersistenceFinderByG_F_A_S.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, active, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the document library file shortcuts where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 */
	@Override
	public void removeByG_F_A_S(
		long groupId, long folderId, boolean active, int status) {

		_collectionPersistenceFinderByG_F_A_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, active, status});
	}

	/**
	 * Returns the number of document library file shortcuts where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByG_F_A_S(
		long groupId, long folderId, boolean active, int status) {

		return _collectionPersistenceFinderByG_F_A_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, active, status});
	}

	/**
	 * Returns the number of document library file shortcuts that the user has permission to view where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @return the number of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public int filterCountByG_F_A_S(
		long groupId, long folderId, boolean active, int status) {

		return _collectionPersistenceFinderByG_F_A_S.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, active, status}, groupId);
	}

	private UniquePersistenceFinder<DLFileShortcut, NoSuchFileShortcutException>
		_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the document library file shortcut where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchFileShortcutException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchFileShortcutException {

		return _uniquePersistenceFinderByERC_G.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the document library file shortcut where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId}, useFinderCache);
	}

	/**
	 * Removes the document library file shortcut where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the document library file shortcut that was removed
	 */
	@Override
	public DLFileShortcut removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = findByERC_G(
			externalReferenceCode, groupId);

		return remove(dlFileShortcut);
	}

	/**
	 * Returns the number of document library file shortcuts where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	public DLFileShortcutPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DLFileShortcut.class);

		setModelImplClass(DLFileShortcutImpl.class);
		setModelPKClass(long.class);

		setTable(DLFileShortcutTable.INSTANCE);
	}

	/**
	 * Creates a new document library file shortcut with the primary key. Does not add the document library file shortcut to the database.
	 *
	 * @param fileShortcutId the primary key for the new document library file shortcut
	 * @return the new document library file shortcut
	 */
	@Override
	public DLFileShortcut create(long fileShortcutId) {
		DLFileShortcut dlFileShortcut = new DLFileShortcutImpl();

		dlFileShortcut.setNew(true);
		dlFileShortcut.setPrimaryKey(fileShortcutId);

		String uuid = PortalUUIDUtil.generate();

		dlFileShortcut.setUuid(uuid);

		dlFileShortcut.setCompanyId(CompanyThreadLocal.getCompanyId());

		return dlFileShortcut;
	}

	/**
	 * Removes the document library file shortcut with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fileShortcutId the primary key of the document library file shortcut
	 * @return the document library file shortcut that was removed
	 * @throws NoSuchFileShortcutException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut remove(long fileShortcutId)
		throws NoSuchFileShortcutException {

		return remove((Serializable)fileShortcutId);
	}

	@Override
	protected DLFileShortcut removeImpl(DLFileShortcut dlFileShortcut) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dlFileShortcut)) {
				dlFileShortcut = (DLFileShortcut)session.get(
					DLFileShortcutImpl.class,
					dlFileShortcut.getPrimaryKeyObj());
			}

			if ((dlFileShortcut != null) &&
				CTPersistenceHelperUtil.isRemove(dlFileShortcut)) {

				session.delete(dlFileShortcut);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dlFileShortcut != null) {
			clearCache(dlFileShortcut);
		}

		return dlFileShortcut;
	}

	@Override
	public DLFileShortcut updateImpl(DLFileShortcut dlFileShortcut) {
		boolean isNew = dlFileShortcut.isNew();

		if (!(dlFileShortcut instanceof DLFileShortcutModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dlFileShortcut.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					dlFileShortcut);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dlFileShortcut proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DLFileShortcut implementation " +
					dlFileShortcut.getClass());
		}

		DLFileShortcutModelImpl dlFileShortcutModelImpl =
			(DLFileShortcutModelImpl)dlFileShortcut;

		if (Validator.isNull(dlFileShortcut.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			dlFileShortcut.setUuid(uuid);
		}

		if (Validator.isNull(dlFileShortcut.getExternalReferenceCode())) {
			dlFileShortcut.setExternalReferenceCode(dlFileShortcut.getUuid());
		}
		else {
			if (!Objects.equals(
					dlFileShortcutModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					dlFileShortcut.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = dlFileShortcut.getCompanyId();

					long groupId = dlFileShortcut.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = dlFileShortcut.getPrimaryKey();
					}

					try {
						dlFileShortcut.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								DLFileShortcut.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								dlFileShortcut.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			DLFileShortcut ercDLFileShortcut = fetchByERC_G(
				dlFileShortcut.getExternalReferenceCode(),
				dlFileShortcut.getGroupId());

			if (isNew) {
				if (ercDLFileShortcut != null) {
					throw new DuplicateDLFileShortcutExternalReferenceCodeException(
						"Duplicate document library file shortcut with external reference code " +
							dlFileShortcut.getExternalReferenceCode() +
								" and group " + dlFileShortcut.getGroupId());
				}
			}
			else {
				if ((ercDLFileShortcut != null) &&
					(dlFileShortcut.getFileShortcutId() !=
						ercDLFileShortcut.getFileShortcutId())) {

					throw new DuplicateDLFileShortcutExternalReferenceCodeException(
						"Duplicate document library file shortcut with external reference code " +
							dlFileShortcut.getExternalReferenceCode() +
								" and group " + dlFileShortcut.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (dlFileShortcut.getCreateDate() == null)) {
			if (serviceContext == null) {
				dlFileShortcut.setCreateDate(date);
			}
			else {
				dlFileShortcut.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!dlFileShortcutModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				dlFileShortcut.setModifiedDate(date);
			}
			else {
				dlFileShortcut.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(dlFileShortcut)) {
				if (!isNew) {
					session.evict(
						DLFileShortcutImpl.class,
						dlFileShortcut.getPrimaryKeyObj());
				}

				session.save(dlFileShortcut);
			}
			else {
				dlFileShortcut = (DLFileShortcut)session.merge(dlFileShortcut);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(dlFileShortcut, false);

		if (isNew) {
			dlFileShortcut.setNew(false);
		}

		dlFileShortcut.resetOriginalValues();

		return dlFileShortcut;
	}

	/**
	 * Returns the document library file shortcut with the primary key or throws a <code>NoSuchFileShortcutException</code> if it could not be found.
	 *
	 * @param fileShortcutId the primary key of the document library file shortcut
	 * @return the document library file shortcut
	 * @throws NoSuchFileShortcutException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut findByPrimaryKey(long fileShortcutId)
		throws NoSuchFileShortcutException {

		return findByPrimaryKey((Serializable)fileShortcutId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the document library file shortcut with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fileShortcutId the primary key of the document library file shortcut
	 * @return the document library file shortcut, or <code>null</code> if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut fetchByPrimaryKey(long fileShortcutId) {
		return fetchByPrimaryKey((Serializable)fileShortcutId);
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
		return "fileShortcutId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DLFILESHORTCUT;
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
		return DLFileShortcutModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DLFileShortcut";
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
		ctMergeColumnNames.add("repositoryId");
		ctMergeColumnNames.add("folderId");
		ctMergeColumnNames.add("toFileEntryId");
		ctMergeColumnNames.add("treePath");
		ctMergeColumnNames.add("active_");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("fileShortcutId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the document library file shortcut persistence.
	 */
	public void afterPropertiesSet() {
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
			_SQL_SELECT_DLFILESHORTCUT_WHERE, _SQL_COUNT_DLFILESHORTCUT_WHERE,
			DLFileShortcutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"dlFileShortcut.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, DLFileShortcut::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(DLFileShortcut::getUuid),
				DLFileShortcut::getGroupId),
			_SQL_SELECT_DLFILESHORTCUT_WHERE, "",
			new FinderColumn<>(
				"dlFileShortcut.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, DLFileShortcut::getUuid),
			new FinderColumn<>(
				"dlFileShortcut.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, DLFileShortcut::getGroupId));

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
				_SQL_SELECT_DLFILESHORTCUT_WHERE,
				_SQL_COUNT_DLFILESHORTCUT_WHERE,
				DLFileShortcutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"dlFileShortcut.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					DLFileShortcut::getUuid),
				new FinderColumn<>(
					"dlFileShortcut.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, DLFileShortcut::getCompanyId));

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
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_DLFILESHORTCUT_WHERE,
				_SQL_COUNT_DLFILESHORTCUT_WHERE,
				DLFileShortcutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"dlFileShortcut.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, DLFileShortcut::getGroupId));

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
				_SQL_SELECT_DLFILESHORTCUT_WHERE,
				_SQL_COUNT_DLFILESHORTCUT_WHERE,
				DLFileShortcutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"dlFileShortcut.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, DLFileShortcut::getCompanyId));

		_collectionPersistenceFinderByToFileEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByToFileEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"toFileEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByToFileEntryId", new String[] {Long.class.getName()},
					new String[] {"toFileEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByToFileEntryId", new String[] {Long.class.getName()},
					new String[] {"toFileEntryId"}, false),
				_SQL_SELECT_DLFILESHORTCUT_WHERE,
				_SQL_COUNT_DLFILESHORTCUT_WHERE,
				DLFileShortcutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"dlFileShortcut.", "toFileEntryId", FinderColumn.Type.LONG,
					"=", true, true, DLFileShortcut::getToFileEntryId));

		_collectionPersistenceFinderByG_F =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_F",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "folderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_F",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "folderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_F",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "folderId"}, false),
				_SQL_SELECT_DLFILESHORTCUT_WHERE,
				_SQL_COUNT_DLFILESHORTCUT_WHERE,
				DLFileShortcutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"dlFileShortcut.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, DLFileShortcut::getGroupId),
				new FinderColumn<>(
					"dlFileShortcut.", "folderId", FinderColumn.Type.LONG, "=",
					true, true, DLFileShortcut::getFolderId));

		_collectionPersistenceFinderByC_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_NotS",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_NotS",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"companyId", "status"}, false),
				_SQL_SELECT_DLFILESHORTCUT_WHERE,
				_SQL_COUNT_DLFILESHORTCUT_WHERE,
				DLFileShortcutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"dlFileShortcut.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, DLFileShortcut::getCompanyId),
				new FinderColumn<>(
					"dlFileShortcut.", "status", FinderColumn.Type.INTEGER,
					"!=", true, true, DLFileShortcut::getStatus));

		_collectionPersistenceFinderByG_F_A =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_F_A",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "folderId", "active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_F_A",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "folderId", "active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_F_A",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "folderId", "active_"}, false),
				_SQL_SELECT_DLFILESHORTCUT_WHERE,
				_SQL_COUNT_DLFILESHORTCUT_WHERE,
				DLFileShortcutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"dlFileShortcut.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, DLFileShortcut::getGroupId),
				new FinderColumn<>(
					"dlFileShortcut.", "folderId", FinderColumn.Type.LONG, "=",
					true, true, DLFileShortcut::getFolderId),
				new FinderColumn<>(
					"dlFileShortcut.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					DLFileShortcut::isActive));

		_collectionPersistenceFinderByG_F_A_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_F_A_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "folderId", "active_", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_F_A_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "folderId", "active_", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_F_A_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "folderId", "active_", "status"},
					false),
				_SQL_SELECT_DLFILESHORTCUT_WHERE,
				_SQL_COUNT_DLFILESHORTCUT_WHERE,
				DLFileShortcutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"dlFileShortcut.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, DLFileShortcut::getGroupId),
				new FinderColumn<>(
					"dlFileShortcut.", "folderId", FinderColumn.Type.LONG, "=",
					true, true, DLFileShortcut::getFolderId),
				new FinderColumn<>(
					"dlFileShortcut.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					DLFileShortcut::isActive),
				new FinderColumn<>(
					"dlFileShortcut.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, DLFileShortcut::getStatus));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(DLFileShortcut::getExternalReferenceCode),
				DLFileShortcut::getGroupId),
			_SQL_SELECT_DLFILESHORTCUT_WHERE, "",
			new FinderColumn<>(
				"dlFileShortcut.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				DLFileShortcut::getExternalReferenceCode),
			new FinderColumn<>(
				"dlFileShortcut.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, DLFileShortcut::getGroupId));

		DLFileShortcutUtil.setPersistence(this);
	}

	public void destroy() {
		DLFileShortcutUtil.setPersistence(null);

		EntityCacheUtil.removeCache(DLFileShortcutImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		DLFileShortcutModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DLFILESHORTCUT =
		"SELECT dlFileShortcut FROM DLFileShortcut dlFileShortcut";

	private static final String _SQL_SELECT_DLFILESHORTCUT_WHERE =
		"SELECT dlFileShortcut FROM DLFileShortcut dlFileShortcut WHERE ";

	private static final String _SQL_COUNT_DLFILESHORTCUT_WHERE =
		"SELECT COUNT(dlFileShortcut) FROM DLFileShortcut dlFileShortcut WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DLFileShortcut exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileShortcutPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1583703252