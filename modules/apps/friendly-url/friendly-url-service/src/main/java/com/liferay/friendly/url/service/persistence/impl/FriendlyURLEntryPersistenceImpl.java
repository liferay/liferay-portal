/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.service.persistence.impl;

import com.liferay.friendly.url.exception.NoSuchFriendlyURLEntryException;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryTable;
import com.liferay.friendly.url.model.impl.FriendlyURLEntryImpl;
import com.liferay.friendly.url.model.impl.FriendlyURLEntryModelImpl;
import com.liferay.friendly.url.service.persistence.FriendlyURLEntryLocalizationPersistence;
import com.liferay.friendly.url.service.persistence.FriendlyURLEntryPersistence;
import com.liferay.friendly.url.service.persistence.FriendlyURLEntryUtil;
import com.liferay.friendly.url.service.persistence.impl.constants.FURLPersistenceConstants;
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
 * The persistence implementation for the friendly url entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = FriendlyURLEntryPersistence.class)
public class FriendlyURLEntryPersistenceImpl
	extends BasePersistenceImpl
		<FriendlyURLEntry, NoSuchFriendlyURLEntryException>
	implements FriendlyURLEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>FriendlyURLEntryUtil</code> to access the friendly url entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		FriendlyURLEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<FriendlyURLEntry, NoSuchFriendlyURLEntryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the friendly url entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of friendly url entries
	 * @param end the upper bound of the range of friendly url entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching friendly url entries
	 */
	@Override
	public List<FriendlyURLEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<FriendlyURLEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first friendly url entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry
	 * @throws NoSuchFriendlyURLEntryException if a matching friendly url entry could not be found
	 */
	@Override
	public FriendlyURLEntry findByUuid_First(
			String uuid, OrderByComparator<FriendlyURLEntry> orderByComparator)
		throws NoSuchFriendlyURLEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first friendly url entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry, or <code>null</code> if a matching friendly url entry could not be found
	 */
	@Override
	public FriendlyURLEntry fetchByUuid_First(
		String uuid, OrderByComparator<FriendlyURLEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the friendly url entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of friendly url entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching friendly url entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<FriendlyURLEntry, NoSuchFriendlyURLEntryException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the friendly url entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFriendlyURLEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching friendly url entry
	 * @throws NoSuchFriendlyURLEntryException if a matching friendly url entry could not be found
	 */
	@Override
	public FriendlyURLEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchFriendlyURLEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the friendly url entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching friendly url entry, or <code>null</code> if a matching friendly url entry could not be found
	 */
	@Override
	public FriendlyURLEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the friendly url entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the friendly url entry that was removed
	 */
	@Override
	public FriendlyURLEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchFriendlyURLEntryException {

		FriendlyURLEntry friendlyURLEntry = findByUUID_G(uuid, groupId);

		return remove(friendlyURLEntry);
	}

	/**
	 * Returns the number of friendly url entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching friendly url entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<FriendlyURLEntry, NoSuchFriendlyURLEntryException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the friendly url entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of friendly url entries
	 * @param end the upper bound of the range of friendly url entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching friendly url entries
	 */
	@Override
	public List<FriendlyURLEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<FriendlyURLEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first friendly url entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry
	 * @throws NoSuchFriendlyURLEntryException if a matching friendly url entry could not be found
	 */
	@Override
	public FriendlyURLEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<FriendlyURLEntry> orderByComparator)
		throws NoSuchFriendlyURLEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first friendly url entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry, or <code>null</code> if a matching friendly url entry could not be found
	 */
	@Override
	public FriendlyURLEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<FriendlyURLEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the friendly url entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of friendly url entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching friendly url entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<FriendlyURLEntry, NoSuchFriendlyURLEntryException>
			_collectionPersistenceFinderByG_C;

	/**
	 * Returns an ordered range of all the friendly url entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of friendly url entries
	 * @param end the upper bound of the range of friendly url entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching friendly url entries
	 */
	@Override
	public List<FriendlyURLEntry> findByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<FriendlyURLEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C.find(
			finderCache, new Object[] {groupId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first friendly url entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry
	 * @throws NoSuchFriendlyURLEntryException if a matching friendly url entry could not be found
	 */
	@Override
	public FriendlyURLEntry findByG_C_First(
			long groupId, long classNameId,
			OrderByComparator<FriendlyURLEntry> orderByComparator)
		throws NoSuchFriendlyURLEntryException {

		return _collectionPersistenceFinderByG_C.findFirst(
			finderCache, new Object[] {groupId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first friendly url entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry, or <code>null</code> if a matching friendly url entry could not be found
	 */
	@Override
	public FriendlyURLEntry fetchByG_C_First(
		long groupId, long classNameId,
		OrderByComparator<FriendlyURLEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C.fetchFirst(
			finderCache, new Object[] {groupId, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the friendly url entries where groupId = &#63; and classNameId = &#63; from the database.
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
	 * Returns the number of friendly url entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching friendly url entries
	 */
	@Override
	public int countByG_C(long groupId, long classNameId) {
		return _collectionPersistenceFinderByG_C.count(
			finderCache, new Object[] {groupId, classNameId});
	}

	private CollectionPersistenceFinder
		<FriendlyURLEntry, NoSuchFriendlyURLEntryException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the friendly url entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of friendly url entries
	 * @param end the upper bound of the range of friendly url entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching friendly url entries
	 */
	@Override
	public List<FriendlyURLEntry> findByC_C(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<FriendlyURLEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {companyId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first friendly url entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry
	 * @throws NoSuchFriendlyURLEntryException if a matching friendly url entry could not be found
	 */
	@Override
	public FriendlyURLEntry findByC_C_First(
			long companyId, long classNameId,
			OrderByComparator<FriendlyURLEntry> orderByComparator)
		throws NoSuchFriendlyURLEntryException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {companyId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first friendly url entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry, or <code>null</code> if a matching friendly url entry could not be found
	 */
	@Override
	public FriendlyURLEntry fetchByC_C_First(
		long companyId, long classNameId,
		OrderByComparator<FriendlyURLEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {companyId, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the friendly url entries where companyId = &#63; and classNameId = &#63; from the database.
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
	 * Returns the number of friendly url entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching friendly url entries
	 */
	@Override
	public int countByC_C(long companyId, long classNameId) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {companyId, classNameId});
	}

	private CollectionPersistenceFinder
		<FriendlyURLEntry, NoSuchFriendlyURLEntryException>
			_collectionPersistenceFinderByG_C_C;

	/**
	 * Returns an ordered range of all the friendly url entries where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of friendly url entries
	 * @param end the upper bound of the range of friendly url entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching friendly url entries
	 */
	@Override
	public List<FriendlyURLEntry> findByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end,
		OrderByComparator<FriendlyURLEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C.find(
			finderCache, new Object[] {groupId, classNameId, classPK}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first friendly url entry in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry
	 * @throws NoSuchFriendlyURLEntryException if a matching friendly url entry could not be found
	 */
	@Override
	public FriendlyURLEntry findByG_C_C_First(
			long groupId, long classNameId, long classPK,
			OrderByComparator<FriendlyURLEntry> orderByComparator)
		throws NoSuchFriendlyURLEntryException {

		return _collectionPersistenceFinderByG_C_C.findFirst(
			finderCache, new Object[] {groupId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first friendly url entry in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry, or <code>null</code> if a matching friendly url entry could not be found
	 */
	@Override
	public FriendlyURLEntry fetchByG_C_C_First(
		long groupId, long classNameId, long classPK,
		OrderByComparator<FriendlyURLEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C.fetchFirst(
			finderCache, new Object[] {groupId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the friendly url entries where groupId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByG_C_C(long groupId, long classNameId, long classPK) {
		_collectionPersistenceFinderByG_C_C.remove(
			finderCache, new Object[] {groupId, classNameId, classPK});
	}

	/**
	 * Returns the number of friendly url entries where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching friendly url entries
	 */
	@Override
	public int countByG_C_C(long groupId, long classNameId, long classPK) {
		return _collectionPersistenceFinderByG_C_C.count(
			finderCache, new Object[] {groupId, classNameId, classPK});
	}

	public FriendlyURLEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(FriendlyURLEntry.class);

		setModelImplClass(FriendlyURLEntryImpl.class);
		setModelPKClass(long.class);

		setTable(FriendlyURLEntryTable.INSTANCE);
	}

	/**
	 * Creates a new friendly url entry with the primary key. Does not add the friendly url entry to the database.
	 *
	 * @param friendlyURLEntryId the primary key for the new friendly url entry
	 * @return the new friendly url entry
	 */
	@Override
	public FriendlyURLEntry create(long friendlyURLEntryId) {
		FriendlyURLEntry friendlyURLEntry = new FriendlyURLEntryImpl();

		friendlyURLEntry.setNew(true);
		friendlyURLEntry.setPrimaryKey(friendlyURLEntryId);

		String uuid = PortalUUIDUtil.generate();

		friendlyURLEntry.setUuid(uuid);

		friendlyURLEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return friendlyURLEntry;
	}

	/**
	 * Removes the friendly url entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param friendlyURLEntryId the primary key of the friendly url entry
	 * @return the friendly url entry that was removed
	 * @throws NoSuchFriendlyURLEntryException if a friendly url entry with the primary key could not be found
	 */
	@Override
	public FriendlyURLEntry remove(long friendlyURLEntryId)
		throws NoSuchFriendlyURLEntryException {

		return remove((Serializable)friendlyURLEntryId);
	}

	@Override
	protected FriendlyURLEntry removeImpl(FriendlyURLEntry friendlyURLEntry) {
		friendlyURLEntryLocalizationPersistence.removeByFriendlyURLEntryId(
			friendlyURLEntry.getFriendlyURLEntryId());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(friendlyURLEntry)) {
				friendlyURLEntry = (FriendlyURLEntry)session.get(
					FriendlyURLEntryImpl.class,
					friendlyURLEntry.getPrimaryKeyObj());
			}

			if ((friendlyURLEntry != null) &&
				ctPersistenceHelper.isRemove(friendlyURLEntry)) {

				session.delete(friendlyURLEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (friendlyURLEntry != null) {
			clearCache(friendlyURLEntry);
		}

		return friendlyURLEntry;
	}

	@Override
	public FriendlyURLEntry updateImpl(FriendlyURLEntry friendlyURLEntry) {
		boolean isNew = friendlyURLEntry.isNew();

		if (!(friendlyURLEntry instanceof FriendlyURLEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(friendlyURLEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					friendlyURLEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in friendlyURLEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom FriendlyURLEntry implementation " +
					friendlyURLEntry.getClass());
		}

		FriendlyURLEntryModelImpl friendlyURLEntryModelImpl =
			(FriendlyURLEntryModelImpl)friendlyURLEntry;

		if (Validator.isNull(friendlyURLEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			friendlyURLEntry.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (friendlyURLEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				friendlyURLEntry.setCreateDate(date);
			}
			else {
				friendlyURLEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!friendlyURLEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				friendlyURLEntry.setModifiedDate(date);
			}
			else {
				friendlyURLEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(friendlyURLEntry)) {
				if (!isNew) {
					session.evict(
						FriendlyURLEntryImpl.class,
						friendlyURLEntry.getPrimaryKeyObj());
				}

				session.save(friendlyURLEntry);
			}
			else {
				friendlyURLEntry = (FriendlyURLEntry)session.merge(
					friendlyURLEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(friendlyURLEntry, false);

		if (isNew) {
			friendlyURLEntry.setNew(false);
		}

		friendlyURLEntry.resetOriginalValues();

		return friendlyURLEntry;
	}

	/**
	 * Returns the friendly url entry with the primary key or throws a <code>NoSuchFriendlyURLEntryException</code> if it could not be found.
	 *
	 * @param friendlyURLEntryId the primary key of the friendly url entry
	 * @return the friendly url entry
	 * @throws NoSuchFriendlyURLEntryException if a friendly url entry with the primary key could not be found
	 */
	@Override
	public FriendlyURLEntry findByPrimaryKey(long friendlyURLEntryId)
		throws NoSuchFriendlyURLEntryException {

		return findByPrimaryKey((Serializable)friendlyURLEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the friendly url entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param friendlyURLEntryId the primary key of the friendly url entry
	 * @return the friendly url entry, or <code>null</code> if a friendly url entry with the primary key could not be found
	 */
	@Override
	public FriendlyURLEntry fetchByPrimaryKey(long friendlyURLEntryId) {
		return fetchByPrimaryKey((Serializable)friendlyURLEntryId);
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
		return "friendlyURLEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_FRIENDLYURLENTRY;
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
		return FriendlyURLEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "FriendlyURLEntry";
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
		ctStrictColumnNames.add("defaultLanguageId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctMergeColumnNames.add("parentClassPK");
		ctStrictColumnNames.add("classPK");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("friendlyURLEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});
	}

	/**
	 * Initializes the friendly url entry persistence.
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
			_SQL_SELECT_FRIENDLYURLENTRY_WHERE,
			_SQL_COUNT_FRIENDLYURLENTRY_WHERE,
			FriendlyURLEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"friendlyURLEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, FriendlyURLEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(FriendlyURLEntry::getUuid),
				FriendlyURLEntry::getGroupId),
			_SQL_SELECT_FRIENDLYURLENTRY_WHERE, "",
			new FinderColumn<>(
				"friendlyURLEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, FriendlyURLEntry::getUuid),
			new FinderColumn<>(
				"friendlyURLEntry.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, FriendlyURLEntry::getGroupId));

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
				_SQL_SELECT_FRIENDLYURLENTRY_WHERE,
				_SQL_COUNT_FRIENDLYURLENTRY_WHERE,
				FriendlyURLEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"friendlyURLEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					FriendlyURLEntry::getUuid),
				new FinderColumn<>(
					"friendlyURLEntry.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, FriendlyURLEntry::getCompanyId));

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
			_SQL_SELECT_FRIENDLYURLENTRY_WHERE,
			_SQL_COUNT_FRIENDLYURLENTRY_WHERE,
			FriendlyURLEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"friendlyURLEntry.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, FriendlyURLEntry::getGroupId),
			new FinderColumn<>(
				"friendlyURLEntry.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, FriendlyURLEntry::getClassNameId));

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
			_SQL_SELECT_FRIENDLYURLENTRY_WHERE,
			_SQL_COUNT_FRIENDLYURLENTRY_WHERE,
			FriendlyURLEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"friendlyURLEntry.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, FriendlyURLEntry::getCompanyId),
			new FinderColumn<>(
				"friendlyURLEntry.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, FriendlyURLEntry::getClassNameId));

		_collectionPersistenceFinderByG_C_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "classNameId", "classPK"}, false),
			_SQL_SELECT_FRIENDLYURLENTRY_WHERE,
			_SQL_COUNT_FRIENDLYURLENTRY_WHERE,
			FriendlyURLEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"friendlyURLEntry.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, FriendlyURLEntry::getGroupId),
			new FinderColumn<>(
				"friendlyURLEntry.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, FriendlyURLEntry::getClassNameId),
			new FinderColumn<>(
				"friendlyURLEntry.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, FriendlyURLEntry::getClassPK));

		FriendlyURLEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		FriendlyURLEntryUtil.setPersistence(null);

		entityCache.removeCache(FriendlyURLEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = FURLPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = FURLPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = FURLPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	@Reference
	protected FriendlyURLEntryLocalizationPersistence
		friendlyURLEntryLocalizationPersistence;

	private static final String _ENTITY_ALIAS_PREFIX =
		FriendlyURLEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_FRIENDLYURLENTRY =
		"SELECT friendlyURLEntry FROM FriendlyURLEntry friendlyURLEntry";

	private static final String _SQL_SELECT_FRIENDLYURLENTRY_WHERE =
		"SELECT friendlyURLEntry FROM FriendlyURLEntry friendlyURLEntry WHERE ";

	private static final String _SQL_COUNT_FRIENDLYURLENTRY_WHERE =
		"SELECT COUNT(friendlyURLEntry) FROM FriendlyURLEntry friendlyURLEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No FriendlyURLEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		FriendlyURLEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1633399874