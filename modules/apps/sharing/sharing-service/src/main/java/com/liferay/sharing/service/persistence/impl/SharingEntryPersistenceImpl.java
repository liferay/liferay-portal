/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.sharing.exception.DuplicateSharingEntryExternalReferenceCodeException;
import com.liferay.sharing.exception.NoSuchEntryException;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.model.SharingEntryTable;
import com.liferay.sharing.model.impl.SharingEntryImpl;
import com.liferay.sharing.model.impl.SharingEntryModelImpl;
import com.liferay.sharing.service.persistence.SharingEntryPersistence;
import com.liferay.sharing.service.persistence.SharingEntryUtil;
import com.liferay.sharing.service.persistence.impl.constants.SharingPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
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
 * The persistence implementation for the sharing entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = SharingEntryPersistence.class)
public class SharingEntryPersistenceImpl
	extends BasePersistenceImpl<SharingEntry, NoSuchEntryException>
	implements SharingEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SharingEntryUtil</code> to access the sharing entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SharingEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<SharingEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the sharing entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of sharing entries
	 * @param end the upper bound of the range of sharing entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching sharing entries
	 */
	@Override
	public List<SharingEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first sharing entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry
	 * @throws NoSuchEntryException if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry findByUuid_First(
			String uuid, OrderByComparator<SharingEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first sharing entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry, or <code>null</code> if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry fetchByUuid_First(
		String uuid, OrderByComparator<SharingEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the sharing entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of sharing entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching sharing entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<SharingEntry, NoSuchEntryException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the sharing entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching sharing entry
	 * @throws NoSuchEntryException if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the sharing entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching sharing entry, or <code>null</code> if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the sharing entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the sharing entry that was removed
	 */
	@Override
	public SharingEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException {

		SharingEntry sharingEntry = findByUUID_G(uuid, groupId);

		return remove(sharingEntry);
	}

	/**
	 * Returns the number of sharing entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching sharing entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<SharingEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the sharing entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of sharing entries
	 * @param end the upper bound of the range of sharing entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching sharing entries
	 */
	@Override
	public List<SharingEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first sharing entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry
	 * @throws NoSuchEntryException if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<SharingEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first sharing entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry, or <code>null</code> if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<SharingEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the sharing entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of sharing entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching sharing entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<SharingEntry, NoSuchEntryException>
		_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the sharing entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of sharing entries
	 * @param end the upper bound of the range of sharing entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching sharing entries
	 */
	@Override
	public List<SharingEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first sharing entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry
	 * @throws NoSuchEntryException if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry findByGroupId_First(
			long groupId, OrderByComparator<SharingEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first sharing entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry, or <code>null</code> if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry fetchByGroupId_First(
		long groupId, OrderByComparator<SharingEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the sharing entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of sharing entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching sharing entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder<SharingEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the sharing entries where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of sharing entries
	 * @param end the upper bound of the range of sharing entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching sharing entries
	 */
	@Override
	public List<SharingEntry> findByUserId(
		long userId, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			finderCache, new Object[] {userId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first sharing entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry
	 * @throws NoSuchEntryException if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry findByUserId_First(
			long userId, OrderByComparator<SharingEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUserId.findFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Returns the first sharing entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry, or <code>null</code> if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry fetchByUserId_First(
		long userId, OrderByComparator<SharingEntry> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Removes all the sharing entries where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the number of sharing entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching sharing entries
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			finderCache, new Object[] {userId});
	}

	private CollectionPersistenceFinder<SharingEntry, NoSuchEntryException>
		_collectionPersistenceFinderByToTicketId;

	/**
	 * Returns an ordered range of all the sharing entries where toTicketId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param toTicketId the to ticket ID
	 * @param start the lower bound of the range of sharing entries
	 * @param end the upper bound of the range of sharing entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching sharing entries
	 */
	@Override
	public List<SharingEntry> findByToTicketId(
		long toTicketId, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByToTicketId.find(
			finderCache, new Object[] {toTicketId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first sharing entry in the ordered set where toTicketId = &#63;.
	 *
	 * @param toTicketId the to ticket ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry
	 * @throws NoSuchEntryException if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry findByToTicketId_First(
			long toTicketId, OrderByComparator<SharingEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByToTicketId.findFirst(
			finderCache, new Object[] {toTicketId}, orderByComparator);
	}

	/**
	 * Returns the first sharing entry in the ordered set where toTicketId = &#63;.
	 *
	 * @param toTicketId the to ticket ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry, or <code>null</code> if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry fetchByToTicketId_First(
		long toTicketId, OrderByComparator<SharingEntry> orderByComparator) {

		return _collectionPersistenceFinderByToTicketId.fetchFirst(
			finderCache, new Object[] {toTicketId}, orderByComparator);
	}

	/**
	 * Removes all the sharing entries where toTicketId = &#63; from the database.
	 *
	 * @param toTicketId the to ticket ID
	 */
	@Override
	public void removeByToTicketId(long toTicketId) {
		_collectionPersistenceFinderByToTicketId.remove(
			finderCache, new Object[] {toTicketId});
	}

	/**
	 * Returns the number of sharing entries where toTicketId = &#63;.
	 *
	 * @param toTicketId the to ticket ID
	 * @return the number of matching sharing entries
	 */
	@Override
	public int countByToTicketId(long toTicketId) {
		return _collectionPersistenceFinderByToTicketId.count(
			finderCache, new Object[] {toTicketId});
	}

	private CollectionPersistenceFinder<SharingEntry, NoSuchEntryException>
		_collectionPersistenceFinderByToUserGroupId;

	/**
	 * Returns an ordered range of all the sharing entries where toUserGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param toUserGroupId the to user group ID
	 * @param start the lower bound of the range of sharing entries
	 * @param end the upper bound of the range of sharing entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching sharing entries
	 */
	@Override
	public List<SharingEntry> findByToUserGroupId(
		long toUserGroupId, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByToUserGroupId.find(
			finderCache, new Object[] {toUserGroupId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first sharing entry in the ordered set where toUserGroupId = &#63;.
	 *
	 * @param toUserGroupId the to user group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry
	 * @throws NoSuchEntryException if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry findByToUserGroupId_First(
			long toUserGroupId,
			OrderByComparator<SharingEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByToUserGroupId.findFirst(
			finderCache, new Object[] {toUserGroupId}, orderByComparator);
	}

	/**
	 * Returns the first sharing entry in the ordered set where toUserGroupId = &#63;.
	 *
	 * @param toUserGroupId the to user group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry, or <code>null</code> if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry fetchByToUserGroupId_First(
		long toUserGroupId, OrderByComparator<SharingEntry> orderByComparator) {

		return _collectionPersistenceFinderByToUserGroupId.fetchFirst(
			finderCache, new Object[] {toUserGroupId}, orderByComparator);
	}

	/**
	 * Removes all the sharing entries where toUserGroupId = &#63; from the database.
	 *
	 * @param toUserGroupId the to user group ID
	 */
	@Override
	public void removeByToUserGroupId(long toUserGroupId) {
		_collectionPersistenceFinderByToUserGroupId.remove(
			finderCache, new Object[] {toUserGroupId});
	}

	/**
	 * Returns the number of sharing entries where toUserGroupId = &#63;.
	 *
	 * @param toUserGroupId the to user group ID
	 * @return the number of matching sharing entries
	 */
	@Override
	public int countByToUserGroupId(long toUserGroupId) {
		return _collectionPersistenceFinderByToUserGroupId.count(
			finderCache, new Object[] {toUserGroupId});
	}

	private CollectionPersistenceFinder<SharingEntry, NoSuchEntryException>
		_collectionPersistenceFinderByToUserId;

	/**
	 * Returns an ordered range of all the sharing entries where toUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param toUserId the to user ID
	 * @param start the lower bound of the range of sharing entries
	 * @param end the upper bound of the range of sharing entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching sharing entries
	 */
	@Override
	public List<SharingEntry> findByToUserId(
		long toUserId, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByToUserId.find(
			finderCache, new Object[] {toUserId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first sharing entry in the ordered set where toUserId = &#63;.
	 *
	 * @param toUserId the to user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry
	 * @throws NoSuchEntryException if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry findByToUserId_First(
			long toUserId, OrderByComparator<SharingEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByToUserId.findFirst(
			finderCache, new Object[] {toUserId}, orderByComparator);
	}

	/**
	 * Returns the first sharing entry in the ordered set where toUserId = &#63;.
	 *
	 * @param toUserId the to user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry, or <code>null</code> if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry fetchByToUserId_First(
		long toUserId, OrderByComparator<SharingEntry> orderByComparator) {

		return _collectionPersistenceFinderByToUserId.fetchFirst(
			finderCache, new Object[] {toUserId}, orderByComparator);
	}

	/**
	 * Removes all the sharing entries where toUserId = &#63; from the database.
	 *
	 * @param toUserId the to user ID
	 */
	@Override
	public void removeByToUserId(long toUserId) {
		_collectionPersistenceFinderByToUserId.remove(
			finderCache, new Object[] {toUserId});
	}

	/**
	 * Returns the number of sharing entries where toUserId = &#63;.
	 *
	 * @param toUserId the to user ID
	 * @return the number of matching sharing entries
	 */
	@Override
	public int countByToUserId(long toUserId) {
		return _collectionPersistenceFinderByToUserId.count(
			finderCache, new Object[] {toUserId});
	}

	private CollectionPersistenceFinder<SharingEntry, NoSuchEntryException>
		_collectionPersistenceFinderByLtExpirationDate;

	/**
	 * Returns all the sharing entries where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the matching sharing entries
	 */
	@Override
	public List<SharingEntry> findByLtExpirationDate(Date expirationDate) {
		return findByLtExpirationDate(
			expirationDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the sharing entries where expirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of sharing entries
	 * @param end the upper bound of the range of sharing entries (not inclusive)
	 * @return the range of matching sharing entries
	 */
	@Override
	public List<SharingEntry> findByLtExpirationDate(
		Date expirationDate, int start, int end) {

		return findByLtExpirationDate(expirationDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the sharing entries where expirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of sharing entries
	 * @param end the upper bound of the range of sharing entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching sharing entries
	 */
	@Override
	public List<SharingEntry> findByLtExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator) {

		return findByLtExpirationDate(
			expirationDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the sharing entries where expirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of sharing entries
	 * @param end the upper bound of the range of sharing entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching sharing entries
	 */
	@Override
	public List<SharingEntry> findByLtExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtExpirationDate.find(
			finderCache, new Object[] {expirationDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first sharing entry in the ordered set where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry
	 * @throws NoSuchEntryException if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry findByLtExpirationDate_First(
			Date expirationDate,
			OrderByComparator<SharingEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByLtExpirationDate.findFirst(
			finderCache, new Object[] {expirationDate}, orderByComparator);
	}

	/**
	 * Returns the first sharing entry in the ordered set where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry, or <code>null</code> if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry fetchByLtExpirationDate_First(
		Date expirationDate,
		OrderByComparator<SharingEntry> orderByComparator) {

		return _collectionPersistenceFinderByLtExpirationDate.fetchFirst(
			finderCache, new Object[] {expirationDate}, orderByComparator);
	}

	/**
	 * Removes all the sharing entries where expirationDate &lt; &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 */
	@Override
	public void removeByLtExpirationDate(Date expirationDate) {
		_collectionPersistenceFinderByLtExpirationDate.remove(
			finderCache, new Object[] {expirationDate});
	}

	/**
	 * Returns the number of sharing entries where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the number of matching sharing entries
	 */
	@Override
	public int countByLtExpirationDate(Date expirationDate) {
		return _collectionPersistenceFinderByLtExpirationDate.count(
			finderCache, new Object[] {expirationDate});
	}

	private CollectionPersistenceFinder<SharingEntry, NoSuchEntryException>
		_collectionPersistenceFinderByC_CN;

	/**
	 * Returns an ordered range of all the sharing entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of sharing entries
	 * @param end the upper bound of the range of sharing entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching sharing entries
	 */
	@Override
	public List<SharingEntry> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CN.find(
			finderCache, new Object[] {companyId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first sharing entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry
	 * @throws NoSuchEntryException if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry findByC_CN_First(
			long companyId, long classNameId,
			OrderByComparator<SharingEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_CN.findFirst(
			finderCache, new Object[] {companyId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first sharing entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry, or <code>null</code> if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry fetchByC_CN_First(
		long companyId, long classNameId,
		OrderByComparator<SharingEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_CN.fetchFirst(
			finderCache, new Object[] {companyId, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the sharing entries where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_CN(long companyId, long classNameId) {
		_collectionPersistenceFinderByC_CN.remove(
			finderCache, new Object[] {companyId, classNameId});
	}

	/**
	 * Returns the number of sharing entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching sharing entries
	 */
	@Override
	public int countByC_CN(long companyId, long classNameId) {
		return _collectionPersistenceFinderByC_CN.count(
			finderCache, new Object[] {companyId, classNameId});
	}

	private CollectionPersistenceFinder<SharingEntry, NoSuchEntryException>
		_collectionPersistenceFinderByU_C;

	/**
	 * Returns an ordered range of all the sharing entries where userId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of sharing entries
	 * @param end the upper bound of the range of sharing entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching sharing entries
	 */
	@Override
	public List<SharingEntry> findByU_C(
		long userId, long classNameId, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_C.find(
			finderCache, new Object[] {userId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first sharing entry in the ordered set where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry
	 * @throws NoSuchEntryException if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry findByU_C_First(
			long userId, long classNameId,
			OrderByComparator<SharingEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByU_C.findFirst(
			finderCache, new Object[] {userId, classNameId}, orderByComparator);
	}

	/**
	 * Returns the first sharing entry in the ordered set where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry, or <code>null</code> if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry fetchByU_C_First(
		long userId, long classNameId,
		OrderByComparator<SharingEntry> orderByComparator) {

		return _collectionPersistenceFinderByU_C.fetchFirst(
			finderCache, new Object[] {userId, classNameId}, orderByComparator);
	}

	/**
	 * Removes all the sharing entries where userId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByU_C(long userId, long classNameId) {
		_collectionPersistenceFinderByU_C.remove(
			finderCache, new Object[] {userId, classNameId});
	}

	/**
	 * Returns the number of sharing entries where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @return the number of matching sharing entries
	 */
	@Override
	public int countByU_C(long userId, long classNameId) {
		return _collectionPersistenceFinderByU_C.count(
			finderCache, new Object[] {userId, classNameId});
	}

	private CollectionPersistenceFinder<SharingEntry, NoSuchEntryException>
		_collectionPersistenceFinderByTU_C;

	/**
	 * Returns an ordered range of all the sharing entries where toUserId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param toUserId the to user ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of sharing entries
	 * @param end the upper bound of the range of sharing entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching sharing entries
	 */
	@Override
	public List<SharingEntry> findByTU_C(
		long toUserId, long classNameId, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByTU_C.find(
			finderCache, new Object[] {toUserId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first sharing entry in the ordered set where toUserId = &#63; and classNameId = &#63;.
	 *
	 * @param toUserId the to user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry
	 * @throws NoSuchEntryException if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry findByTU_C_First(
			long toUserId, long classNameId,
			OrderByComparator<SharingEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByTU_C.findFirst(
			finderCache, new Object[] {toUserId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first sharing entry in the ordered set where toUserId = &#63; and classNameId = &#63;.
	 *
	 * @param toUserId the to user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry, or <code>null</code> if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry fetchByTU_C_First(
		long toUserId, long classNameId,
		OrderByComparator<SharingEntry> orderByComparator) {

		return _collectionPersistenceFinderByTU_C.fetchFirst(
			finderCache, new Object[] {toUserId, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the sharing entries where toUserId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param toUserId the to user ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByTU_C(long toUserId, long classNameId) {
		_collectionPersistenceFinderByTU_C.remove(
			finderCache, new Object[] {toUserId, classNameId});
	}

	/**
	 * Returns the number of sharing entries where toUserId = &#63; and classNameId = &#63;.
	 *
	 * @param toUserId the to user ID
	 * @param classNameId the class name ID
	 * @return the number of matching sharing entries
	 */
	@Override
	public int countByTU_C(long toUserId, long classNameId) {
		return _collectionPersistenceFinderByTU_C.count(
			finderCache, new Object[] {toUserId, classNameId});
	}

	private CollectionPersistenceFinder<SharingEntry, NoSuchEntryException>
		_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the sharing entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of sharing entries
	 * @param end the upper bound of the range of sharing entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching sharing entries
	 */
	@Override
	public List<SharingEntry> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first sharing entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry
	 * @throws NoSuchEntryException if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<SharingEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first sharing entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharing entry, or <code>null</code> if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<SharingEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the sharing entries where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C(long classNameId, long classPK) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the number of sharing entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching sharing entries
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private UniquePersistenceFinder<SharingEntry, NoSuchEntryException>
		_uniquePersistenceFinderByTT_TUG_TU_C_C;

	/**
	 * Returns the sharing entry where toTicketId = &#63; and toUserGroupId = &#63; and toUserId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param toTicketId the to ticket ID
	 * @param toUserGroupId the to user group ID
	 * @param toUserId the to user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching sharing entry
	 * @throws NoSuchEntryException if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry findByTT_TUG_TU_C_C(
			long toTicketId, long toUserGroupId, long toUserId,
			long classNameId, long classPK)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByTT_TUG_TU_C_C.find(
			finderCache,
			new Object[] {
				toTicketId, toUserGroupId, toUserId, classNameId, classPK
			});
	}

	/**
	 * Returns the sharing entry where toTicketId = &#63; and toUserGroupId = &#63; and toUserId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param toTicketId the to ticket ID
	 * @param toUserGroupId the to user group ID
	 * @param toUserId the to user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching sharing entry, or <code>null</code> if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry fetchByTT_TUG_TU_C_C(
		long toTicketId, long toUserGroupId, long toUserId, long classNameId,
		long classPK, boolean useFinderCache) {

		return _uniquePersistenceFinderByTT_TUG_TU_C_C.fetch(
			finderCache,
			new Object[] {
				toTicketId, toUserGroupId, toUserId, classNameId, classPK
			},
			useFinderCache);
	}

	/**
	 * Removes the sharing entry where toTicketId = &#63; and toUserGroupId = &#63; and toUserId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param toTicketId the to ticket ID
	 * @param toUserGroupId the to user group ID
	 * @param toUserId the to user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the sharing entry that was removed
	 */
	@Override
	public SharingEntry removeByTT_TUG_TU_C_C(
			long toTicketId, long toUserGroupId, long toUserId,
			long classNameId, long classPK)
		throws NoSuchEntryException {

		SharingEntry sharingEntry = findByTT_TUG_TU_C_C(
			toTicketId, toUserGroupId, toUserId, classNameId, classPK);

		return remove(sharingEntry);
	}

	/**
	 * Returns the number of sharing entries where toTicketId = &#63; and toUserGroupId = &#63; and toUserId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param toTicketId the to ticket ID
	 * @param toUserGroupId the to user group ID
	 * @param toUserId the to user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching sharing entries
	 */
	@Override
	public int countByTT_TUG_TU_C_C(
		long toTicketId, long toUserGroupId, long toUserId, long classNameId,
		long classPK) {

		return _uniquePersistenceFinderByTT_TUG_TU_C_C.count(
			finderCache,
			new Object[] {
				toTicketId, toUserGroupId, toUserId, classNameId, classPK
			});
	}

	private UniquePersistenceFinder<SharingEntry, NoSuchEntryException>
		_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the sharing entry where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching sharing entry
	 * @throws NoSuchEntryException if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the sharing entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching sharing entry, or <code>null</code> if a matching sharing entry could not be found
	 */
	@Override
	public SharingEntry fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the sharing entry where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the sharing entry that was removed
	 */
	@Override
	public SharingEntry removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchEntryException {

		SharingEntry sharingEntry = findByERC_G(externalReferenceCode, groupId);

		return remove(sharingEntry);
	}

	/**
	 * Returns the number of sharing entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching sharing entries
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public SharingEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(SharingEntry.class);

		setModelImplClass(SharingEntryImpl.class);
		setModelPKClass(long.class);

		setTable(SharingEntryTable.INSTANCE);
	}

	/**
	 * Creates a new sharing entry with the primary key. Does not add the sharing entry to the database.
	 *
	 * @param sharingEntryId the primary key for the new sharing entry
	 * @return the new sharing entry
	 */
	@Override
	public SharingEntry create(long sharingEntryId) {
		SharingEntry sharingEntry = new SharingEntryImpl();

		sharingEntry.setNew(true);
		sharingEntry.setPrimaryKey(sharingEntryId);

		String uuid = PortalUUIDUtil.generate();

		sharingEntry.setUuid(uuid);

		sharingEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return sharingEntry;
	}

	/**
	 * Removes the sharing entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param sharingEntryId the primary key of the sharing entry
	 * @return the sharing entry that was removed
	 * @throws NoSuchEntryException if a sharing entry with the primary key could not be found
	 */
	@Override
	public SharingEntry remove(long sharingEntryId)
		throws NoSuchEntryException {

		return remove((Serializable)sharingEntryId);
	}

	@Override
	protected SharingEntry removeImpl(SharingEntry sharingEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(sharingEntry)) {
				sharingEntry = (SharingEntry)session.get(
					SharingEntryImpl.class, sharingEntry.getPrimaryKeyObj());
			}

			if (sharingEntry != null) {
				session.delete(sharingEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (sharingEntry != null) {
			clearCache(sharingEntry);
		}

		return sharingEntry;
	}

	@Override
	public SharingEntry updateImpl(SharingEntry sharingEntry) {
		boolean isNew = sharingEntry.isNew();

		if (!(sharingEntry instanceof SharingEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(sharingEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					sharingEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in sharingEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SharingEntry implementation " +
					sharingEntry.getClass());
		}

		SharingEntryModelImpl sharingEntryModelImpl =
			(SharingEntryModelImpl)sharingEntry;

		if (Validator.isNull(sharingEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			sharingEntry.setUuid(uuid);
		}

		if (Validator.isNull(sharingEntry.getExternalReferenceCode())) {
			sharingEntry.setExternalReferenceCode(sharingEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					sharingEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					sharingEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = sharingEntry.getCompanyId();

					long groupId = sharingEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = sharingEntry.getPrimaryKey();
					}

					try {
						sharingEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								SharingEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								sharingEntry.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			SharingEntry ercSharingEntry = fetchByERC_G(
				sharingEntry.getExternalReferenceCode(),
				sharingEntry.getGroupId());

			if (isNew) {
				if (ercSharingEntry != null) {
					throw new DuplicateSharingEntryExternalReferenceCodeException(
						"Duplicate sharing entry with external reference code " +
							sharingEntry.getExternalReferenceCode() +
								" and group " + sharingEntry.getGroupId());
				}
			}
			else {
				if ((ercSharingEntry != null) &&
					(sharingEntry.getSharingEntryId() !=
						ercSharingEntry.getSharingEntryId())) {

					throw new DuplicateSharingEntryExternalReferenceCodeException(
						"Duplicate sharing entry with external reference code " +
							sharingEntry.getExternalReferenceCode() +
								" and group " + sharingEntry.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (sharingEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				sharingEntry.setCreateDate(date);
			}
			else {
				sharingEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!sharingEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				sharingEntry.setModifiedDate(date);
			}
			else {
				sharingEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(sharingEntry);
			}
			else {
				sharingEntry = (SharingEntry)session.merge(sharingEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(sharingEntry, false);

		if (isNew) {
			sharingEntry.setNew(false);
		}

		sharingEntry.resetOriginalValues();

		return sharingEntry;
	}

	/**
	 * Returns the sharing entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param sharingEntryId the primary key of the sharing entry
	 * @return the sharing entry
	 * @throws NoSuchEntryException if a sharing entry with the primary key could not be found
	 */
	@Override
	public SharingEntry findByPrimaryKey(long sharingEntryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)sharingEntryId);
	}

	/**
	 * Returns the sharing entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param sharingEntryId the primary key of the sharing entry
	 * @return the sharing entry, or <code>null</code> if a sharing entry with the primary key could not be found
	 */
	@Override
	public SharingEntry fetchByPrimaryKey(long sharingEntryId) {
		return fetchByPrimaryKey((Serializable)sharingEntryId);
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
		return "sharingEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SHARINGENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SharingEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the sharing entry persistence.
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
			_SQL_SELECT_SHARINGENTRY_WHERE, _SQL_COUNT_SHARINGENTRY_WHERE,
			SharingEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"sharingEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, SharingEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(SharingEntry::getUuid),
				SharingEntry::getGroupId),
			_SQL_SELECT_SHARINGENTRY_WHERE, "",
			new FinderColumn<>(
				"sharingEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, SharingEntry::getUuid),
			new FinderColumn<>(
				"sharingEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, SharingEntry::getGroupId));

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
				_SQL_SELECT_SHARINGENTRY_WHERE, _SQL_COUNT_SHARINGENTRY_WHERE,
				SharingEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"sharingEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, SharingEntry::getUuid),
				new FinderColumn<>(
					"sharingEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, SharingEntry::getCompanyId));

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
				_SQL_SELECT_SHARINGENTRY_WHERE, _SQL_COUNT_SHARINGENTRY_WHERE,
				SharingEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"sharingEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, SharingEntry::getGroupId));

		_collectionPersistenceFinderByUserId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, false),
				_SQL_SELECT_SHARINGENTRY_WHERE, _SQL_COUNT_SHARINGENTRY_WHERE,
				SharingEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"sharingEntry.", "userId", FinderColumn.Type.LONG, "=",
					true, true, SharingEntry::getUserId));

		_collectionPersistenceFinderByToTicketId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByToTicketId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"toTicketId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByToTicketId", new String[] {Long.class.getName()},
					new String[] {"toTicketId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByToTicketId", new String[] {Long.class.getName()},
					new String[] {"toTicketId"}, false),
				_SQL_SELECT_SHARINGENTRY_WHERE, _SQL_COUNT_SHARINGENTRY_WHERE,
				SharingEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"sharingEntry.", "toTicketId", FinderColumn.Type.LONG, "=",
					true, true, SharingEntry::getToTicketId));

		_collectionPersistenceFinderByToUserGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByToUserGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"toUserGroupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByToUserGroupId", new String[] {Long.class.getName()},
					new String[] {"toUserGroupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByToUserGroupId", new String[] {Long.class.getName()},
					new String[] {"toUserGroupId"}, false),
				_SQL_SELECT_SHARINGENTRY_WHERE, _SQL_COUNT_SHARINGENTRY_WHERE,
				SharingEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"sharingEntry.", "toUserGroupId", FinderColumn.Type.LONG,
					"=", true, true, SharingEntry::getToUserGroupId));

		_collectionPersistenceFinderByToUserId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByToUserId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"toUserId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByToUserId",
					new String[] {Long.class.getName()},
					new String[] {"toUserId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByToUserId", new String[] {Long.class.getName()},
					new String[] {"toUserId"}, false),
				_SQL_SELECT_SHARINGENTRY_WHERE, _SQL_COUNT_SHARINGENTRY_WHERE,
				SharingEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"sharingEntry.", "toUserId", FinderColumn.Type.LONG, "=",
					true, true, SharingEntry::getToUserId));

		_collectionPersistenceFinderByLtExpirationDate =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLtExpirationDate",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"expirationDate"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByLtExpirationDate",
					new String[] {Date.class.getName()},
					new String[] {"expirationDate"}, false),
				_SQL_SELECT_SHARINGENTRY_WHERE, _SQL_COUNT_SHARINGENTRY_WHERE,
				SharingEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"sharingEntry.", "expirationDate", FinderColumn.Type.DATE,
					"<", true, true, SharingEntry::getExpirationDate));

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
			_SQL_SELECT_SHARINGENTRY_WHERE, _SQL_COUNT_SHARINGENTRY_WHERE,
			SharingEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"sharingEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, SharingEntry::getCompanyId),
			new FinderColumn<>(
				"sharingEntry.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, SharingEntry::getClassNameId));

		_collectionPersistenceFinderByU_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"userId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId", "classNameId"}, false),
			_SQL_SELECT_SHARINGENTRY_WHERE, _SQL_COUNT_SHARINGENTRY_WHERE,
			SharingEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"sharingEntry.", "userId", FinderColumn.Type.LONG, "=", true,
				true, SharingEntry::getUserId),
			new FinderColumn<>(
				"sharingEntry.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, SharingEntry::getClassNameId));

		_collectionPersistenceFinderByTU_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByTU_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"toUserId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByTU_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"toUserId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByTU_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"toUserId", "classNameId"}, false),
			_SQL_SELECT_SHARINGENTRY_WHERE, _SQL_COUNT_SHARINGENTRY_WHERE,
			SharingEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"sharingEntry.", "toUserId", FinderColumn.Type.LONG, "=", true,
				true, SharingEntry::getToUserId),
			new FinderColumn<>(
				"sharingEntry.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, SharingEntry::getClassNameId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, false),
			_SQL_SELECT_SHARINGENTRY_WHERE, _SQL_COUNT_SHARINGENTRY_WHERE,
			SharingEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"sharingEntry.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, SharingEntry::getClassNameId),
			new FinderColumn<>(
				"sharingEntry.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, SharingEntry::getClassPK));

		_uniquePersistenceFinderByTT_TUG_TU_C_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByTT_TUG_TU_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"toTicketId", "toUserGroupId", "toUserId", "classNameId",
					"classPK"
				},
				0, 0, false, SharingEntry::getToTicketId,
				SharingEntry::getToUserGroupId, SharingEntry::getToUserId,
				SharingEntry::getClassNameId, SharingEntry::getClassPK),
			_SQL_SELECT_SHARINGENTRY_WHERE, "",
			new FinderColumn<>(
				"sharingEntry.", "toTicketId", FinderColumn.Type.LONG, "=",
				true, true, SharingEntry::getToTicketId),
			new FinderColumn<>(
				"sharingEntry.", "toUserGroupId", FinderColumn.Type.LONG, "=",
				true, true, SharingEntry::getToUserGroupId),
			new FinderColumn<>(
				"sharingEntry.", "toUserId", FinderColumn.Type.LONG, "=", true,
				true, SharingEntry::getToUserId),
			new FinderColumn<>(
				"sharingEntry.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, SharingEntry::getClassNameId),
			new FinderColumn<>(
				"sharingEntry.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, SharingEntry::getClassPK));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(SharingEntry::getExternalReferenceCode),
				SharingEntry::getGroupId),
			_SQL_SELECT_SHARINGENTRY_WHERE, "",
			new FinderColumn<>(
				"sharingEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				SharingEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"sharingEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, SharingEntry::getGroupId));

		SharingEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SharingEntryUtil.setPersistence(null);

		entityCache.removeCache(SharingEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = SharingPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SharingPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SharingPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		SharingEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SHARINGENTRY =
		"SELECT sharingEntry FROM SharingEntry sharingEntry";

	private static final String _SQL_SELECT_SHARINGENTRY_WHERE =
		"SELECT sharingEntry FROM SharingEntry sharingEntry WHERE ";

	private static final String _SQL_COUNT_SHARINGENTRY_WHERE =
		"SELECT COUNT(sharingEntry) FROM SharingEntry sharingEntry WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1910546403