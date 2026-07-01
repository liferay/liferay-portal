/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchLVEntryException;
import com.liferay.portal.tools.service.builder.test.model.LVEntry;
import com.liferay.portal.tools.service.builder.test.model.LVEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.LVEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.LVEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.BigDecimalEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.LVEntryLocalizationPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.LVEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.LVEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the lv entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LVEntryPersistenceImpl
	extends BasePersistenceImpl<LVEntry, NoSuchLVEntryException>
	implements LVEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LVEntryUtil</code> to access the lv entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LVEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<LVEntry, NoSuchLVEntryException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the lv entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of lv entries
	 * @param end the upper bound of the range of lv entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entries
	 */
	@Override
	public List<LVEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LVEntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first lv entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry
	 * @throws NoSuchLVEntryException if a matching lv entry could not be found
	 */
	@Override
	public LVEntry findByUuid_First(
			String uuid, OrderByComparator<LVEntry> orderByComparator)
		throws NoSuchLVEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first lv entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry, or <code>null</code> if a matching lv entry could not be found
	 */
	@Override
	public LVEntry fetchByUuid_First(
		String uuid, OrderByComparator<LVEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the lv entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of lv entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching lv entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder<LVEntry, NoSuchLVEntryException>
		_collectionPersistenceFinderByUuid_Head;

	/**
	 * Returns an ordered range of all the lv entries where uuid = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param head the head
	 * @param start the lower bound of the range of lv entries
	 * @param end the upper bound of the range of lv entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entries
	 */
	@Override
	public List<LVEntry> findByUuid_Head(
		String uuid, boolean head, int start, int end,
		OrderByComparator<LVEntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_Head.find(
			finderCache, new Object[] {uuid, head}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lv entry in the ordered set where uuid = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry
	 * @throws NoSuchLVEntryException if a matching lv entry could not be found
	 */
	@Override
	public LVEntry findByUuid_Head_First(
			String uuid, boolean head,
			OrderByComparator<LVEntry> orderByComparator)
		throws NoSuchLVEntryException {

		return _collectionPersistenceFinderByUuid_Head.findFirst(
			finderCache, new Object[] {uuid, head}, orderByComparator);
	}

	/**
	 * Returns the first lv entry in the ordered set where uuid = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry, or <code>null</code> if a matching lv entry could not be found
	 */
	@Override
	public LVEntry fetchByUuid_Head_First(
		String uuid, boolean head,
		OrderByComparator<LVEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_Head.fetchFirst(
			finderCache, new Object[] {uuid, head}, orderByComparator);
	}

	/**
	 * Removes all the lv entries where uuid = &#63; and head = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param head the head
	 */
	@Override
	public void removeByUuid_Head(String uuid, boolean head) {
		_collectionPersistenceFinderByUuid_Head.remove(
			finderCache, new Object[] {uuid, head});
	}

	/**
	 * Returns the number of lv entries where uuid = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param head the head
	 * @return the number of matching lv entries
	 */
	@Override
	public int countByUuid_Head(String uuid, boolean head) {
		return _collectionPersistenceFinderByUuid_Head.count(
			finderCache, new Object[] {uuid, head});
	}

	private CollectionPersistenceFinder<LVEntry, NoSuchLVEntryException>
		_collectionPersistenceFinderByUUID_G;

	/**
	 * Returns an ordered range of all the lv entries where uuid = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param start the lower bound of the range of lv entries
	 * @param end the upper bound of the range of lv entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entries
	 */
	@Override
	public List<LVEntry> findByUUID_G(
		String uuid, long groupId, int start, int end,
		OrderByComparator<LVEntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lv entry in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry
	 * @throws NoSuchLVEntryException if a matching lv entry could not be found
	 */
	@Override
	public LVEntry findByUUID_G_First(
			String uuid, long groupId,
			OrderByComparator<LVEntry> orderByComparator)
		throws NoSuchLVEntryException {

		return _collectionPersistenceFinderByUUID_G.findFirst(
			finderCache, new Object[] {uuid, groupId}, orderByComparator);
	}

	/**
	 * Returns the first lv entry in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry, or <code>null</code> if a matching lv entry could not be found
	 */
	@Override
	public LVEntry fetchByUUID_G_First(
		String uuid, long groupId,
		OrderByComparator<LVEntry> orderByComparator) {

		return _collectionPersistenceFinderByUUID_G.fetchFirst(
			finderCache, new Object[] {uuid, groupId}, orderByComparator);
	}

	/**
	 * Removes all the lv entries where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 */
	@Override
	public void removeByUUID_G(String uuid, long groupId) {
		_collectionPersistenceFinderByUUID_G.remove(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the number of lv entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching lv entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _collectionPersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private UniquePersistenceFinder<LVEntry, NoSuchLVEntryException>
		_uniquePersistenceFinderByUUID_G_Head;

	/**
	 * Returns the lv entry where uuid = &#63; and groupId = &#63; and head = &#63; or throws a <code>NoSuchLVEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param head the head
	 * @return the matching lv entry
	 * @throws NoSuchLVEntryException if a matching lv entry could not be found
	 */
	@Override
	public LVEntry findByUUID_G_Head(String uuid, long groupId, boolean head)
		throws NoSuchLVEntryException {

		return _uniquePersistenceFinderByUUID_G_Head.find(
			finderCache, new Object[] {uuid, groupId, head});
	}

	/**
	 * Returns the lv entry where uuid = &#63; and groupId = &#63; and head = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param head the head
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching lv entry, or <code>null</code> if a matching lv entry could not be found
	 */
	@Override
	public LVEntry fetchByUUID_G_Head(
		String uuid, long groupId, boolean head, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G_Head.fetch(
			finderCache, new Object[] {uuid, groupId, head}, useFinderCache);
	}

	/**
	 * Removes the lv entry where uuid = &#63; and groupId = &#63; and head = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param head the head
	 * @return the lv entry that was removed
	 */
	@Override
	public LVEntry removeByUUID_G_Head(String uuid, long groupId, boolean head)
		throws NoSuchLVEntryException {

		LVEntry lvEntry = findByUUID_G_Head(uuid, groupId, head);

		return remove(lvEntry);
	}

	/**
	 * Returns the number of lv entries where uuid = &#63; and groupId = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param head the head
	 * @return the number of matching lv entries
	 */
	@Override
	public int countByUUID_G_Head(String uuid, long groupId, boolean head) {
		return _uniquePersistenceFinderByUUID_G_Head.count(
			finderCache, new Object[] {uuid, groupId, head});
	}

	private CollectionPersistenceFinder<LVEntry, NoSuchLVEntryException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the lv entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of lv entries
	 * @param end the upper bound of the range of lv entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entries
	 */
	@Override
	public List<LVEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LVEntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lv entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry
	 * @throws NoSuchLVEntryException if a matching lv entry could not be found
	 */
	@Override
	public LVEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LVEntry> orderByComparator)
		throws NoSuchLVEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first lv entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry, or <code>null</code> if a matching lv entry could not be found
	 */
	@Override
	public LVEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LVEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the lv entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of lv entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching lv entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<LVEntry, NoSuchLVEntryException>
		_collectionPersistenceFinderByUuid_C_Head;

	/**
	 * Returns an ordered range of all the lv entries where uuid = &#63; and companyId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 * @param start the lower bound of the range of lv entries
	 * @param end the upper bound of the range of lv entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entries
	 */
	@Override
	public List<LVEntry> findByUuid_C_Head(
		String uuid, long companyId, boolean head, int start, int end,
		OrderByComparator<LVEntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C_Head.find(
			finderCache, new Object[] {uuid, companyId, head}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lv entry in the ordered set where uuid = &#63; and companyId = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry
	 * @throws NoSuchLVEntryException if a matching lv entry could not be found
	 */
	@Override
	public LVEntry findByUuid_C_Head_First(
			String uuid, long companyId, boolean head,
			OrderByComparator<LVEntry> orderByComparator)
		throws NoSuchLVEntryException {

		return _collectionPersistenceFinderByUuid_C_Head.findFirst(
			finderCache, new Object[] {uuid, companyId, head},
			orderByComparator);
	}

	/**
	 * Returns the first lv entry in the ordered set where uuid = &#63; and companyId = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry, or <code>null</code> if a matching lv entry could not be found
	 */
	@Override
	public LVEntry fetchByUuid_C_Head_First(
		String uuid, long companyId, boolean head,
		OrderByComparator<LVEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C_Head.fetchFirst(
			finderCache, new Object[] {uuid, companyId, head},
			orderByComparator);
	}

	/**
	 * Removes all the lv entries where uuid = &#63; and companyId = &#63; and head = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 */
	@Override
	public void removeByUuid_C_Head(String uuid, long companyId, boolean head) {
		_collectionPersistenceFinderByUuid_C_Head.remove(
			finderCache, new Object[] {uuid, companyId, head});
	}

	/**
	 * Returns the number of lv entries where uuid = &#63; and companyId = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 * @return the number of matching lv entries
	 */
	@Override
	public int countByUuid_C_Head(String uuid, long companyId, boolean head) {
		return _collectionPersistenceFinderByUuid_C_Head.count(
			finderCache, new Object[] {uuid, companyId, head});
	}

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;
	private FinderPath _finderPathWithPaginationCountByGroupId;

	/**
	 * Returns an ordered range of all the lv entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of lv entries
	 * @param end the upper bound of the range of lv entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entries
	 */
	@Override
	public List<LVEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LVEntry> orderByComparator, boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByGroupId;
				finderArgs = new Object[] {groupId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByGroupId;
			finderArgs = new Object[] {groupId, start, end, orderByComparator};
		}

		List<LVEntry> list = null;

		if (useFinderCache) {
			list = (List<LVEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (LVEntry lvEntry : list) {
					if (groupId != lvEntry.getGroupId()) {
						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					3 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(3);
			}

			sb.append(_SQL_SELECT_LVENTRY_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
			}
			else {
				sb.append(LVEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				list = (List<LVEntry>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first lv entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry
	 * @throws NoSuchLVEntryException if a matching lv entry could not be found
	 */
	@Override
	public LVEntry findByGroupId_First(
			long groupId, OrderByComparator<LVEntry> orderByComparator)
		throws NoSuchLVEntryException {

		LVEntry lvEntry = fetchByGroupId_First(groupId, orderByComparator);

		if (lvEntry != null) {
			return lvEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchLVEntryException(sb.toString());
	}

	/**
	 * Returns the first lv entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry, or <code>null</code> if a matching lv entry could not be found
	 */
	@Override
	public LVEntry fetchByGroupId_First(
		long groupId, OrderByComparator<LVEntry> orderByComparator) {

		List<LVEntry> list = findByGroupId(groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns all the lv entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @return the matching lv entries
	 */
	@Override
	public List<LVEntry> findByGroupId(long[] groupIds) {
		return findByGroupId(
			groupIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the lv entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of lv entries
	 * @param end the upper bound of the range of lv entries (not inclusive)
	 * @return the range of matching lv entries
	 */
	@Override
	public List<LVEntry> findByGroupId(long[] groupIds, int start, int end) {
		return findByGroupId(groupIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the lv entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of lv entries
	 * @param end the upper bound of the range of lv entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching lv entries
	 */
	@Override
	public List<LVEntry> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<LVEntry> orderByComparator) {

		return findByGroupId(groupIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the lv entries where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of lv entries
	 * @param end the upper bound of the range of lv entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entries
	 */
	@Override
	public List<LVEntry> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<LVEntry> orderByComparator, boolean useFinderCache) {

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		if (groupIds.length == 1) {
			return findByGroupId(groupIds[0], start, end, orderByComparator);
		}

		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderArgs = new Object[] {StringUtil.merge(groupIds)};
			}
		}
		else if (useFinderCache) {
			finderArgs = new Object[] {
				StringUtil.merge(groupIds), start, end, orderByComparator
			};
		}

		List<LVEntry> list = null;

		if (useFinderCache) {
			list = (List<LVEntry>)finderCache.getResult(
				_finderPathWithPaginationFindByGroupId, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (LVEntry lvEntry : list) {
					if (!ArrayUtil.contains(groupIds, lvEntry.getGroupId())) {
						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			try {
				if ((start == QueryUtil.ALL_POS) &&
					(end == QueryUtil.ALL_POS) &&
					(databaseInMaxParameters > 0) &&
					(groupIds.length > databaseInMaxParameters)) {

					list = new ArrayList<LVEntry>();

					long[][] groupIdsPages = (long[][])ArrayUtil.split(
						groupIds, databaseInMaxParameters);

					for (long[] groupIdsPage : groupIdsPages) {
						list.addAll(
							_findByGroupId(
								groupIdsPage, start, end, orderByComparator));
					}

					Collections.sort(list, orderByComparator);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = _findByGroupId(
						groupIds, start, end, orderByComparator);
				}

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(
						_finderPathWithPaginationFindByGroupId, finderArgs,
						list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
		}

		return list;
	}

	private List<LVEntry> _findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<LVEntry> orderByComparator) {

		List<LVEntry> list = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_SELECT_LVENTRY_WHERE);

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND lvEntry.lvEntryId > 0");

		if (orderByComparator != null) {
			appendOrderByComparator(
				sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
		}
		else {
			sb.append(LVEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			list = (List<LVEntry>)QueryUtil.list(
				query, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return list;
	}

	/**
	 * Removes all the lv entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (LVEntry lvEntry :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(lvEntry);
		}
	}

	/**
	 * Returns the number of lv entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching lv entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		FinderPath finderPath = _finderPathCountByGroupId;

		Object[] finderArgs = new Object[] {groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_LVENTRY_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	/**
	 * Returns the number of lv entries where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching lv entries
	 */
	@Override
	public int countByGroupId(long[] groupIds) {
		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		Object[] finderArgs = new Object[] {StringUtil.merge(groupIds)};

		Long count = (Long)finderCache.getResult(
			_finderPathWithPaginationCountByGroupId, finderArgs, this);

		if (count == null) {
			try {
				if ((databaseInMaxParameters > 0) &&
					(groupIds.length > databaseInMaxParameters)) {

					count = Long.valueOf(0);

					long[][] groupIdsPages = (long[][])ArrayUtil.split(
						groupIds, databaseInMaxParameters);

					for (long[] groupIdsPage : groupIdsPages) {
						count += Long.valueOf(_countByGroupId(groupIdsPage));
					}
				}
				else {
					count = Long.valueOf(_countByGroupId(groupIds));
				}

				finderCache.putResult(
					_finderPathWithPaginationCountByGroupId, finderArgs, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
		}

		return count.intValue();
	}

	private int _countByGroupId(long[] groupIds) {
		Long count = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_COUNT_LVENTRY_WHERE);

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND lvEntry.lvEntryId > 0");

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			count = (Long)query.uniqueResult();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_2 =
		"lvEntry.groupId = ? AND lvEntry.lvEntryId > 0";

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_7 =
		"lvEntry.groupId IN (";

	private FinderPath _finderPathWithPaginationFindByGroupId_Head;
	private FinderPath _finderPathWithoutPaginationFindByGroupId_Head;
	private FinderPath _finderPathCountByGroupId_Head;
	private FinderPath _finderPathWithPaginationCountByGroupId_Head;

	/**
	 * Returns an ordered range of all the lv entries where groupId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @param start the lower bound of the range of lv entries
	 * @param end the upper bound of the range of lv entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entries
	 */
	@Override
	public List<LVEntry> findByGroupId_Head(
		long groupId, boolean head, int start, int end,
		OrderByComparator<LVEntry> orderByComparator, boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByGroupId_Head;
				finderArgs = new Object[] {groupId, head};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByGroupId_Head;
			finderArgs = new Object[] {
				groupId, head, start, end, orderByComparator
			};
		}

		List<LVEntry> list = null;

		if (useFinderCache) {
			list = (List<LVEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (LVEntry lvEntry : list) {
					if ((groupId != lvEntry.getGroupId()) ||
						(head != lvEntry.isHead())) {

						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					4 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(4);
			}

			sb.append(_SQL_SELECT_LVENTRY_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_HEAD_GROUPID_2);

			sb.append(_FINDER_COLUMN_GROUPID_HEAD_HEAD_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
			}
			else {
				sb.append(LVEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(head);

				list = (List<LVEntry>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first lv entry in the ordered set where groupId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry
	 * @throws NoSuchLVEntryException if a matching lv entry could not be found
	 */
	@Override
	public LVEntry findByGroupId_Head_First(
			long groupId, boolean head,
			OrderByComparator<LVEntry> orderByComparator)
		throws NoSuchLVEntryException {

		LVEntry lvEntry = fetchByGroupId_Head_First(
			groupId, head, orderByComparator);

		if (lvEntry != null) {
			return lvEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", head=");
		sb.append(head);

		sb.append("}");

		throw new NoSuchLVEntryException(sb.toString());
	}

	/**
	 * Returns the first lv entry in the ordered set where groupId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry, or <code>null</code> if a matching lv entry could not be found
	 */
	@Override
	public LVEntry fetchByGroupId_Head_First(
		long groupId, boolean head,
		OrderByComparator<LVEntry> orderByComparator) {

		List<LVEntry> list = findByGroupId_Head(
			groupId, head, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns all the lv entries where groupId = any &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param head the head
	 * @return the matching lv entries
	 */
	@Override
	public List<LVEntry> findByGroupId_Head(long[] groupIds, boolean head) {
		return findByGroupId_Head(
			groupIds, head, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the lv entries where groupId = any &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param head the head
	 * @param start the lower bound of the range of lv entries
	 * @param end the upper bound of the range of lv entries (not inclusive)
	 * @return the range of matching lv entries
	 */
	@Override
	public List<LVEntry> findByGroupId_Head(
		long[] groupIds, boolean head, int start, int end) {

		return findByGroupId_Head(groupIds, head, start, end, null);
	}

	/**
	 * Returns an ordered range of all the lv entries where groupId = any &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param head the head
	 * @param start the lower bound of the range of lv entries
	 * @param end the upper bound of the range of lv entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching lv entries
	 */
	@Override
	public List<LVEntry> findByGroupId_Head(
		long[] groupIds, boolean head, int start, int end,
		OrderByComparator<LVEntry> orderByComparator) {

		return findByGroupId_Head(
			groupIds, head, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the lv entries where groupId = &#63; and head = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param head the head
	 * @param start the lower bound of the range of lv entries
	 * @param end the upper bound of the range of lv entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entries
	 */
	@Override
	public List<LVEntry> findByGroupId_Head(
		long[] groupIds, boolean head, int start, int end,
		OrderByComparator<LVEntry> orderByComparator, boolean useFinderCache) {

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		if (groupIds.length == 1) {
			return findByGroupId_Head(
				groupIds[0], head, start, end, orderByComparator);
		}

		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderArgs = new Object[] {StringUtil.merge(groupIds), head};
			}
		}
		else if (useFinderCache) {
			finderArgs = new Object[] {
				StringUtil.merge(groupIds), head, start, end, orderByComparator
			};
		}

		List<LVEntry> list = null;

		if (useFinderCache) {
			list = (List<LVEntry>)finderCache.getResult(
				_finderPathWithPaginationFindByGroupId_Head, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (LVEntry lvEntry : list) {
					if (!ArrayUtil.contains(groupIds, lvEntry.getGroupId()) ||
						(head != lvEntry.isHead())) {

						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			try {
				if ((start == QueryUtil.ALL_POS) &&
					(end == QueryUtil.ALL_POS) &&
					(databaseInMaxParameters > 0) &&
					(groupIds.length > databaseInMaxParameters)) {

					list = new ArrayList<LVEntry>();

					long[][] groupIdsPages = (long[][])ArrayUtil.split(
						groupIds, databaseInMaxParameters);

					for (long[] groupIdsPage : groupIdsPages) {
						list.addAll(
							_findByGroupId_Head(
								groupIdsPage, head, start, end,
								orderByComparator));
					}

					Collections.sort(list, orderByComparator);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = _findByGroupId_Head(
						groupIds, head, start, end, orderByComparator);
				}

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(
						_finderPathWithPaginationFindByGroupId_Head, finderArgs,
						list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
		}

		return list;
	}

	private List<LVEntry> _findByGroupId_Head(
		long[] groupIds, boolean head, int start, int end,
		OrderByComparator<LVEntry> orderByComparator) {

		List<LVEntry> list = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_SELECT_LVENTRY_WHERE);

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_GROUPID_HEAD_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_GROUPID_HEAD_HEAD_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND lvEntry.lvEntryId > 0");

		if (orderByComparator != null) {
			appendOrderByComparator(
				sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
		}
		else {
			sb.append(LVEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(query);

			queryPos.add(head);

			list = (List<LVEntry>)QueryUtil.list(
				query, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return list;
	}

	/**
	 * Removes all the lv entries where groupId = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param head the head
	 */
	@Override
	public void removeByGroupId_Head(long groupId, boolean head) {
		for (LVEntry lvEntry :
				findByGroupId_Head(
					groupId, head, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(lvEntry);
		}
	}

	/**
	 * Returns the number of lv entries where groupId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @return the number of matching lv entries
	 */
	@Override
	public int countByGroupId_Head(long groupId, boolean head) {
		FinderPath finderPath = _finderPathCountByGroupId_Head;

		Object[] finderArgs = new Object[] {groupId, head};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_LVENTRY_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_HEAD_GROUPID_2);

			sb.append(_FINDER_COLUMN_GROUPID_HEAD_HEAD_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(head);

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	/**
	 * Returns the number of lv entries where groupId = any &#63; and head = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param head the head
	 * @return the number of matching lv entries
	 */
	@Override
	public int countByGroupId_Head(long[] groupIds, boolean head) {
		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		Object[] finderArgs = new Object[] {StringUtil.merge(groupIds), head};

		Long count = (Long)finderCache.getResult(
			_finderPathWithPaginationCountByGroupId_Head, finderArgs, this);

		if (count == null) {
			try {
				if ((databaseInMaxParameters > 0) &&
					(groupIds.length > databaseInMaxParameters)) {

					count = Long.valueOf(0);

					long[][] groupIdsPages = (long[][])ArrayUtil.split(
						groupIds, databaseInMaxParameters);

					for (long[] groupIdsPage : groupIdsPages) {
						count += Long.valueOf(
							_countByGroupId_Head(groupIdsPage, head));
					}
				}
				else {
					count = Long.valueOf(_countByGroupId_Head(groupIds, head));
				}

				finderCache.putResult(
					_finderPathWithPaginationCountByGroupId_Head, finderArgs,
					count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
		}

		return count.intValue();
	}

	private int _countByGroupId_Head(long[] groupIds, boolean head) {
		Long count = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_COUNT_LVENTRY_WHERE);

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_GROUPID_HEAD_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_GROUPID_HEAD_HEAD_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND lvEntry.lvEntryId > 0");

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(query);

			queryPos.add(head);

			count = (Long)query.uniqueResult();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_GROUPID_HEAD_GROUPID_2 =
		"lvEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_GROUPID_HEAD_GROUPID_7 =
		"lvEntry.groupId IN (";

	private static final String _FINDER_COLUMN_GROUPID_HEAD_HEAD_2 =
		"lvEntry.head = ? AND lvEntry.lvEntryId > 0";

	private CollectionPersistenceFinder<LVEntry, NoSuchLVEntryException>
		_collectionPersistenceFinderByG_UGK;

	/**
	 * Returns an ordered range of all the lv entries where groupId = &#63; and uniqueGroupKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param uniqueGroupKey the unique group key
	 * @param start the lower bound of the range of lv entries
	 * @param end the upper bound of the range of lv entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entries
	 */
	@Override
	public List<LVEntry> findByG_UGK(
		long groupId, String uniqueGroupKey, int start, int end,
		OrderByComparator<LVEntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_UGK.find(
			finderCache, new Object[] {groupId, uniqueGroupKey}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lv entry in the ordered set where groupId = &#63; and uniqueGroupKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param uniqueGroupKey the unique group key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry
	 * @throws NoSuchLVEntryException if a matching lv entry could not be found
	 */
	@Override
	public LVEntry findByG_UGK_First(
			long groupId, String uniqueGroupKey,
			OrderByComparator<LVEntry> orderByComparator)
		throws NoSuchLVEntryException {

		return _collectionPersistenceFinderByG_UGK.findFirst(
			finderCache, new Object[] {groupId, uniqueGroupKey},
			orderByComparator);
	}

	/**
	 * Returns the first lv entry in the ordered set where groupId = &#63; and uniqueGroupKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param uniqueGroupKey the unique group key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry, or <code>null</code> if a matching lv entry could not be found
	 */
	@Override
	public LVEntry fetchByG_UGK_First(
		long groupId, String uniqueGroupKey,
		OrderByComparator<LVEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_UGK.fetchFirst(
			finderCache, new Object[] {groupId, uniqueGroupKey},
			orderByComparator);
	}

	/**
	 * Removes all the lv entries where groupId = &#63; and uniqueGroupKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param uniqueGroupKey the unique group key
	 */
	@Override
	public void removeByG_UGK(long groupId, String uniqueGroupKey) {
		_collectionPersistenceFinderByG_UGK.remove(
			finderCache, new Object[] {groupId, uniqueGroupKey});
	}

	/**
	 * Returns the number of lv entries where groupId = &#63; and uniqueGroupKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param uniqueGroupKey the unique group key
	 * @return the number of matching lv entries
	 */
	@Override
	public int countByG_UGK(long groupId, String uniqueGroupKey) {
		return _collectionPersistenceFinderByG_UGK.count(
			finderCache, new Object[] {groupId, uniqueGroupKey});
	}

	private UniquePersistenceFinder<LVEntry, NoSuchLVEntryException>
		_uniquePersistenceFinderByG_UGK_Head;

	/**
	 * Returns the lv entry where groupId = &#63; and uniqueGroupKey = &#63; and head = &#63; or throws a <code>NoSuchLVEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param uniqueGroupKey the unique group key
	 * @param head the head
	 * @return the matching lv entry
	 * @throws NoSuchLVEntryException if a matching lv entry could not be found
	 */
	@Override
	public LVEntry findByG_UGK_Head(
			long groupId, String uniqueGroupKey, boolean head)
		throws NoSuchLVEntryException {

		return _uniquePersistenceFinderByG_UGK_Head.find(
			finderCache, new Object[] {groupId, uniqueGroupKey, head});
	}

	/**
	 * Returns the lv entry where groupId = &#63; and uniqueGroupKey = &#63; and head = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param uniqueGroupKey the unique group key
	 * @param head the head
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching lv entry, or <code>null</code> if a matching lv entry could not be found
	 */
	@Override
	public LVEntry fetchByG_UGK_Head(
		long groupId, String uniqueGroupKey, boolean head,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_UGK_Head.fetch(
			finderCache, new Object[] {groupId, uniqueGroupKey, head},
			useFinderCache);
	}

	/**
	 * Removes the lv entry where groupId = &#63; and uniqueGroupKey = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param uniqueGroupKey the unique group key
	 * @param head the head
	 * @return the lv entry that was removed
	 */
	@Override
	public LVEntry removeByG_UGK_Head(
			long groupId, String uniqueGroupKey, boolean head)
		throws NoSuchLVEntryException {

		LVEntry lvEntry = findByG_UGK_Head(groupId, uniqueGroupKey, head);

		return remove(lvEntry);
	}

	/**
	 * Returns the number of lv entries where groupId = &#63; and uniqueGroupKey = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param uniqueGroupKey the unique group key
	 * @param head the head
	 * @return the number of matching lv entries
	 */
	@Override
	public int countByG_UGK_Head(
		long groupId, String uniqueGroupKey, boolean head) {

		return _uniquePersistenceFinderByG_UGK_Head.count(
			finderCache, new Object[] {groupId, uniqueGroupKey, head});
	}

	private UniquePersistenceFinder<LVEntry, NoSuchLVEntryException>
		_uniquePersistenceFinderByHeadId;

	/**
	 * Returns the lv entry where headId = &#63; or throws a <code>NoSuchLVEntryException</code> if it could not be found.
	 *
	 * @param headId the head ID
	 * @return the matching lv entry
	 * @throws NoSuchLVEntryException if a matching lv entry could not be found
	 */
	@Override
	public LVEntry findByHeadId(long headId) throws NoSuchLVEntryException {
		return _uniquePersistenceFinderByHeadId.find(
			finderCache, new Object[] {headId});
	}

	/**
	 * Returns the lv entry where headId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param headId the head ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching lv entry, or <code>null</code> if a matching lv entry could not be found
	 */
	@Override
	public LVEntry fetchByHeadId(long headId, boolean useFinderCache) {
		return _uniquePersistenceFinderByHeadId.fetch(
			finderCache, new Object[] {headId}, useFinderCache);
	}

	/**
	 * Removes the lv entry where headId = &#63; from the database.
	 *
	 * @param headId the head ID
	 * @return the lv entry that was removed
	 */
	@Override
	public LVEntry removeByHeadId(long headId) throws NoSuchLVEntryException {
		LVEntry lvEntry = findByHeadId(headId);

		return remove(lvEntry);
	}

	/**
	 * Returns the number of lv entries where headId = &#63;.
	 *
	 * @param headId the head ID
	 * @return the number of matching lv entries
	 */
	@Override
	public int countByHeadId(long headId) {
		return _uniquePersistenceFinderByHeadId.count(
			finderCache, new Object[] {headId});
	}

	public LVEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LVEntry.class);

		setModelImplClass(LVEntryImpl.class);
		setModelPKClass(long.class);

		setTable(LVEntryTable.INSTANCE);
	}

	/**
	 * Creates a new lv entry with the primary key. Does not add the lv entry to the database.
	 *
	 * @param lvEntryId the primary key for the new lv entry
	 * @return the new lv entry
	 */
	@Override
	public LVEntry create(long lvEntryId) {
		LVEntry lvEntry = new LVEntryImpl();

		lvEntry.setNew(true);
		lvEntry.setPrimaryKey(lvEntryId);

		String uuid = PortalUUIDUtil.generate();

		lvEntry.setUuid(uuid);

		lvEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return lvEntry;
	}

	/**
	 * Removes the lv entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param lvEntryId the primary key of the lv entry
	 * @return the lv entry that was removed
	 * @throws NoSuchLVEntryException if a lv entry with the primary key could not be found
	 */
	@Override
	public LVEntry remove(long lvEntryId) throws NoSuchLVEntryException {
		return remove((Serializable)lvEntryId);
	}

	@Override
	protected LVEntry removeImpl(LVEntry lvEntry) {
		lvEntryToBigDecimalEntryTableMapper.deleteLeftPrimaryKeyTableMappings(
			lvEntry.getPrimaryKey());

		lvEntryLocalizationPersistence.removeByLvEntryId(
			lvEntry.getLvEntryId());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(lvEntry)) {
				lvEntry = (LVEntry)session.get(
					LVEntryImpl.class, lvEntry.getPrimaryKeyObj());
			}

			if (lvEntry != null) {
				session.delete(lvEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (lvEntry != null) {
			clearCache(lvEntry);
		}

		return lvEntry;
	}

	@Override
	public LVEntry updateImpl(LVEntry lvEntry) {
		boolean isNew = lvEntry.isNew();

		if (!(lvEntry instanceof LVEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(lvEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(lvEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in lvEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LVEntry implementation " +
					lvEntry.getClass());
		}

		LVEntryModelImpl lvEntryModelImpl = (LVEntryModelImpl)lvEntry;

		if (Validator.isNull(lvEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			lvEntry.setUuid(uuid);
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(lvEntry);
			}
			else {
				lvEntry = (LVEntry)session.merge(lvEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(lvEntry, false);

		if (isNew) {
			lvEntry.setNew(false);
		}

		lvEntry.resetOriginalValues();

		return lvEntry;
	}

	/**
	 * Returns the lv entry with the primary key or throws a <code>NoSuchLVEntryException</code> if it could not be found.
	 *
	 * @param lvEntryId the primary key of the lv entry
	 * @return the lv entry
	 * @throws NoSuchLVEntryException if a lv entry with the primary key could not be found
	 */
	@Override
	public LVEntry findByPrimaryKey(long lvEntryId)
		throws NoSuchLVEntryException {

		return findByPrimaryKey((Serializable)lvEntryId);
	}

	/**
	 * Returns the lv entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param lvEntryId the primary key of the lv entry
	 * @return the lv entry, or <code>null</code> if a lv entry with the primary key could not be found
	 */
	@Override
	public LVEntry fetchByPrimaryKey(long lvEntryId) {
		return fetchByPrimaryKey((Serializable)lvEntryId);
	}

	/**
	 * Returns the primaryKeys of big decimal entries associated with the lv entry.
	 *
	 * @param pk the primary key of the lv entry
	 * @return long[] of the primaryKeys of big decimal entries associated with the lv entry
	 */
	@Override
	public long[] getBigDecimalEntryPrimaryKeys(long pk) {
		long[] pks = lvEntryToBigDecimalEntryTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the big decimal entries associated with the lv entry.
	 *
	 * @param pk the primary key of the lv entry
	 * @return the big decimal entries associated with the lv entry
	 */
	@Override
	public List
		<com.liferay.portal.tools.service.builder.test.model.BigDecimalEntry>
			getBigDecimalEntries(long pk) {

		return getBigDecimalEntries(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the big decimal entries associated with the lv entry.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the lv entry
	 * @param start the lower bound of the range of lv entries
	 * @param end the upper bound of the range of lv entries (not inclusive)
	 * @return the range of big decimal entries associated with the lv entry
	 */
	@Override
	public List
		<com.liferay.portal.tools.service.builder.test.model.BigDecimalEntry>
			getBigDecimalEntries(long pk, int start, int end) {

		return getBigDecimalEntries(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the big decimal entries associated with the lv entry.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the lv entry
	 * @param start the lower bound of the range of lv entries
	 * @param end the upper bound of the range of lv entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of big decimal entries associated with the lv entry
	 */
	@Override
	public List
		<com.liferay.portal.tools.service.builder.test.model.BigDecimalEntry>
			getBigDecimalEntries(
				long pk, int start, int end,
				OrderByComparator
					<com.liferay.portal.tools.service.builder.test.model.
						BigDecimalEntry> orderByComparator) {

		return lvEntryToBigDecimalEntryTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of big decimal entries associated with the lv entry.
	 *
	 * @param pk the primary key of the lv entry
	 * @return the number of big decimal entries associated with the lv entry
	 */
	@Override
	public int getBigDecimalEntriesSize(long pk) {
		long[] pks = lvEntryToBigDecimalEntryTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the big decimal entry is associated with the lv entry.
	 *
	 * @param pk the primary key of the lv entry
	 * @param bigDecimalEntryPK the primary key of the big decimal entry
	 * @return <code>true</code> if the big decimal entry is associated with the lv entry; <code>false</code> otherwise
	 */
	@Override
	public boolean containsBigDecimalEntry(long pk, long bigDecimalEntryPK) {
		return lvEntryToBigDecimalEntryTableMapper.containsTableMapping(
			pk, bigDecimalEntryPK);
	}

	/**
	 * Returns <code>true</code> if the lv entry has any big decimal entries associated with it.
	 *
	 * @param pk the primary key of the lv entry to check for associations with big decimal entries
	 * @return <code>true</code> if the lv entry has any big decimal entries associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsBigDecimalEntries(long pk) {
		if (getBigDecimalEntriesSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the lv entry and the big decimal entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry
	 * @param bigDecimalEntryPK the primary key of the big decimal entry
	 * @return <code>true</code> if an association between the lv entry and the big decimal entry was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addBigDecimalEntry(long pk, long bigDecimalEntryPK) {
		LVEntry lvEntry = fetchByPrimaryKey(pk);

		if (lvEntry == null) {
			return lvEntryToBigDecimalEntryTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, bigDecimalEntryPK);
		}
		else {
			return lvEntryToBigDecimalEntryTableMapper.addTableMapping(
				lvEntry.getCompanyId(), pk, bigDecimalEntryPK);
		}
	}

	/**
	 * Adds an association between the lv entry and the big decimal entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry
	 * @param bigDecimalEntry the big decimal entry
	 * @return <code>true</code> if an association between the lv entry and the big decimal entry was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addBigDecimalEntry(
		long pk,
		com.liferay.portal.tools.service.builder.test.model.BigDecimalEntry
			bigDecimalEntry) {

		LVEntry lvEntry = fetchByPrimaryKey(pk);

		if (lvEntry == null) {
			return lvEntryToBigDecimalEntryTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				bigDecimalEntry.getPrimaryKey());
		}
		else {
			return lvEntryToBigDecimalEntryTableMapper.addTableMapping(
				lvEntry.getCompanyId(), pk, bigDecimalEntry.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the lv entry and the big decimal entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry
	 * @param bigDecimalEntryPKs the primary keys of the big decimal entries
	 * @return <code>true</code> if at least one association between the lv entry and the big decimal entries was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addBigDecimalEntries(long pk, long[] bigDecimalEntryPKs) {
		long companyId = 0;

		LVEntry lvEntry = fetchByPrimaryKey(pk);

		if (lvEntry == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = lvEntry.getCompanyId();
		}

		long[] addedKeys = lvEntryToBigDecimalEntryTableMapper.addTableMappings(
			companyId, pk, bigDecimalEntryPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the lv entry and the big decimal entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry
	 * @param bigDecimalEntries the big decimal entries
	 * @return <code>true</code> if at least one association between the lv entry and the big decimal entries was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addBigDecimalEntries(
		long pk,
		List
			<com.liferay.portal.tools.service.builder.test.model.
				BigDecimalEntry> bigDecimalEntries) {

		return addBigDecimalEntries(
			pk,
			ListUtil.toLongArray(
				bigDecimalEntries,
				com.liferay.portal.tools.service.builder.test.model.
					BigDecimalEntry.BIG_DECIMAL_ENTRY_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the lv entry and its big decimal entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry to clear the associated big decimal entries from
	 */
	@Override
	public void clearBigDecimalEntries(long pk) {
		lvEntryToBigDecimalEntryTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the lv entry and the big decimal entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry
	 * @param bigDecimalEntryPK the primary key of the big decimal entry
	 */
	@Override
	public void removeBigDecimalEntry(long pk, long bigDecimalEntryPK) {
		lvEntryToBigDecimalEntryTableMapper.deleteTableMapping(
			pk, bigDecimalEntryPK);
	}

	/**
	 * Removes the association between the lv entry and the big decimal entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry
	 * @param bigDecimalEntry the big decimal entry
	 */
	@Override
	public void removeBigDecimalEntry(
		long pk,
		com.liferay.portal.tools.service.builder.test.model.BigDecimalEntry
			bigDecimalEntry) {

		lvEntryToBigDecimalEntryTableMapper.deleteTableMapping(
			pk, bigDecimalEntry.getPrimaryKey());
	}

	/**
	 * Removes the association between the lv entry and the big decimal entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry
	 * @param bigDecimalEntryPKs the primary keys of the big decimal entries
	 */
	@Override
	public void removeBigDecimalEntries(long pk, long[] bigDecimalEntryPKs) {
		lvEntryToBigDecimalEntryTableMapper.deleteTableMappings(
			pk, bigDecimalEntryPKs);
	}

	/**
	 * Removes the association between the lv entry and the big decimal entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry
	 * @param bigDecimalEntries the big decimal entries
	 */
	@Override
	public void removeBigDecimalEntries(
		long pk,
		List
			<com.liferay.portal.tools.service.builder.test.model.
				BigDecimalEntry> bigDecimalEntries) {

		removeBigDecimalEntries(
			pk,
			ListUtil.toLongArray(
				bigDecimalEntries,
				com.liferay.portal.tools.service.builder.test.model.
					BigDecimalEntry.BIG_DECIMAL_ENTRY_ID_ACCESSOR));
	}

	/**
	 * Sets the big decimal entries associated with the lv entry, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry
	 * @param bigDecimalEntryPKs the primary keys of the big decimal entries to be associated with the lv entry
	 */
	@Override
	public void setBigDecimalEntries(long pk, long[] bigDecimalEntryPKs) {
		Set<Long> newBigDecimalEntryPKsSet = SetUtil.fromArray(
			bigDecimalEntryPKs);
		Set<Long> oldBigDecimalEntryPKsSet = SetUtil.fromArray(
			lvEntryToBigDecimalEntryTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeBigDecimalEntryPKsSet = new HashSet<Long>(
			oldBigDecimalEntryPKsSet);

		removeBigDecimalEntryPKsSet.removeAll(newBigDecimalEntryPKsSet);

		lvEntryToBigDecimalEntryTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeBigDecimalEntryPKsSet));

		newBigDecimalEntryPKsSet.removeAll(oldBigDecimalEntryPKsSet);

		long companyId = 0;

		LVEntry lvEntry = fetchByPrimaryKey(pk);

		if (lvEntry == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = lvEntry.getCompanyId();
		}

		lvEntryToBigDecimalEntryTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newBigDecimalEntryPKsSet));
	}

	/**
	 * Sets the big decimal entries associated with the lv entry, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry
	 * @param bigDecimalEntries the big decimal entries to be associated with the lv entry
	 */
	@Override
	public void setBigDecimalEntries(
		long pk,
		List
			<com.liferay.portal.tools.service.builder.test.model.
				BigDecimalEntry> bigDecimalEntries) {

		try {
			long[] bigDecimalEntryPKs = new long[bigDecimalEntries.size()];

			for (int i = 0; i < bigDecimalEntries.size(); i++) {
				com.liferay.portal.tools.service.builder.test.model.
					BigDecimalEntry bigDecimalEntry = bigDecimalEntries.get(i);

				bigDecimalEntryPKs[i] = bigDecimalEntry.getPrimaryKey();
			}

			setBigDecimalEntries(pk, bigDecimalEntryPKs);
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
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "lvEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LVENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return LVEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the lv entry persistence.
	 */
	public void afterPropertiesSet() {
		lvEntryToBigDecimalEntryTableMapper = TableMapperFactory.getTableMapper(
			"BigDecimalEntries_LVEntries", "companyId", "lvEntryId",
			"bigDecimalEntryId", this, bigDecimalEntryPersistence);

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
			_SQL_SELECT_LVENTRY_WHERE, _SQL_COUNT_LVENTRY_WHERE,
			LVEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"lvEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, LVEntry::getUuid));

		_collectionPersistenceFinderByUuid_Head =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_Head",
					new String[] {
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "head"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByUuid_Head",
					new String[] {
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {"uuid_", "head"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByUuid_Head",
					new String[] {
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {"uuid_", "head"}, 0, 1, false, null),
				_SQL_SELECT_LVENTRY_WHERE, _SQL_COUNT_LVENTRY_WHERE,
				LVEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"lvEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, LVEntry::getUuid),
				new FinderColumn<>(
					"lvEntry.", "head", FinderColumn.Type.BOOLEAN, "=", true,
					true, LVEntry::isHead));

		_collectionPersistenceFinderByUUID_G =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUUID_G",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUUID_G",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "groupId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUUID_G",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "groupId"}, 0, 1, false, null),
				_SQL_SELECT_LVENTRY_WHERE, _SQL_COUNT_LVENTRY_WHERE,
				LVEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"lvEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, LVEntry::getUuid),
				new FinderColumn<>(
					"lvEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, LVEntry::getGroupId));

		_uniquePersistenceFinderByUUID_G_Head = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G_Head",
				new String[] {
					String.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"uuid_", "groupId", "head"}, 0, 1, false,
				convertNullFunction(LVEntry::getUuid), LVEntry::getGroupId,
				LVEntry::isHead),
			_SQL_SELECT_LVENTRY_WHERE, "",
			new FinderColumn<>(
				"lvEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, LVEntry::getUuid),
			new FinderColumn<>(
				"lvEntry.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				LVEntry::getGroupId),
			new FinderColumn<>(
				"lvEntry.", "head", FinderColumn.Type.BOOLEAN, "=", true, true,
				LVEntry::isHead));

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
				_SQL_SELECT_LVENTRY_WHERE, _SQL_COUNT_LVENTRY_WHERE,
				LVEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"lvEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, LVEntry::getUuid),
				new FinderColumn<>(
					"lvEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, LVEntry::getCompanyId));

		_collectionPersistenceFinderByUuid_C_Head =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C_Head",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId", "head"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByUuid_C_Head",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"uuid_", "companyId", "head"}, 0, 1, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByUuid_C_Head",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"uuid_", "companyId", "head"}, 0, 1, false,
					null),
				_SQL_SELECT_LVENTRY_WHERE, _SQL_COUNT_LVENTRY_WHERE,
				LVEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"lvEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, LVEntry::getUuid),
				new FinderColumn<>(
					"lvEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, LVEntry::getCompanyId),
				new FinderColumn<>(
					"lvEntry.", "head", FinderColumn.Type.BOOLEAN, "=", true,
					true, LVEntry::isHead));

		_finderPathWithPaginationFindByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId"}, true);

		_finderPathWithoutPaginationFindByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			true);

		_finderPathCountByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			false);

		_finderPathWithPaginationCountByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			false);

		_finderPathWithPaginationFindByGroupId_Head = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId_Head",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "head"}, true);

		_finderPathWithoutPaginationFindByGroupId_Head = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId_Head",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"groupId", "head"}, true);

		_finderPathCountByGroupId_Head = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGroupId_Head",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"groupId", "head"}, false);

		_finderPathWithPaginationCountByGroupId_Head = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGroupId_Head",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"groupId", "head"}, false);

		_collectionPersistenceFinderByG_UGK = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_UGK",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "uniqueGroupKey"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_UGK",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "uniqueGroupKey"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_UGK",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "uniqueGroupKey"}, 0, 2, false, null),
			_SQL_SELECT_LVENTRY_WHERE, _SQL_COUNT_LVENTRY_WHERE,
			LVEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"lvEntry.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				LVEntry::getGroupId),
			new FinderColumn<>(
				"lvEntry.", "uniqueGroupKey", FinderColumn.Type.STRING, "=",
				true, true, LVEntry::getUniqueGroupKey));

		_uniquePersistenceFinderByG_UGK_Head = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_UGK_Head",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"groupId", "uniqueGroupKey", "head"}, 0, 2, false,
				LVEntry::getGroupId,
				convertNullFunction(LVEntry::getUniqueGroupKey),
				LVEntry::isHead),
			_SQL_SELECT_LVENTRY_WHERE, "",
			new FinderColumn<>(
				"lvEntry.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				LVEntry::getGroupId),
			new FinderColumn<>(
				"lvEntry.", "uniqueGroupKey", FinderColumn.Type.STRING, "=",
				true, true, LVEntry::getUniqueGroupKey),
			new FinderColumn<>(
				"lvEntry.", "head", FinderColumn.Type.BOOLEAN, "=", true, true,
				LVEntry::isHead));

		_uniquePersistenceFinderByHeadId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByHeadId",
				new String[] {Long.class.getName()}, new String[] {"headId"}, 0,
				0, false, LVEntry::getHeadId),
			_SQL_SELECT_LVENTRY_WHERE, "",
			new FinderColumn<>(
				"lvEntry.", "headId", FinderColumn.Type.LONG, "=", true, true,
				LVEntry::getHeadId));

		LVEntryUtil.setPersistence(this);
	}

	public void destroy() {
		LVEntryUtil.setPersistence(null);

		entityCache.removeCache(LVEntryImpl.class.getName());

		TableMapperFactory.removeTableMapper("BigDecimalEntries_LVEntries");
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	@BeanReference(type = BigDecimalEntryPersistence.class)
	protected BigDecimalEntryPersistence bigDecimalEntryPersistence;

	protected TableMapper
		<LVEntry,
		 com.liferay.portal.tools.service.builder.test.model.BigDecimalEntry>
			lvEntryToBigDecimalEntryTableMapper;

	@BeanReference(type = LVEntryLocalizationPersistence.class)
	protected LVEntryLocalizationPersistence lvEntryLocalizationPersistence;

	private static final String _ENTITY_ALIAS_PREFIX =
		LVEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LVENTRY =
		"SELECT lvEntry FROM LVEntry lvEntry";

	private static final String _SQL_SELECT_LVENTRY_WHERE =
		"SELECT lvEntry FROM LVEntry lvEntry WHERE ";

	private static final String _SQL_COUNT_LVENTRY_WHERE =
		"SELECT COUNT(lvEntry) FROM LVEntry lvEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LVEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LVEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2120650141