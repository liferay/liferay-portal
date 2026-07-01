/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
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
import com.liferay.redirect.exception.NoSuchEntryException;
import com.liferay.redirect.model.RedirectEntry;
import com.liferay.redirect.model.RedirectEntryTable;
import com.liferay.redirect.model.impl.RedirectEntryImpl;
import com.liferay.redirect.model.impl.RedirectEntryModelImpl;
import com.liferay.redirect.service.persistence.RedirectEntryPersistence;
import com.liferay.redirect.service.persistence.RedirectEntryUtil;
import com.liferay.redirect.service.persistence.impl.constants.RedirectPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the redirect entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = RedirectEntryPersistence.class)
public class RedirectEntryPersistenceImpl
	extends BasePersistenceImpl<RedirectEntry, NoSuchEntryException>
	implements RedirectEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>RedirectEntryUtil</code> to access the redirect entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		RedirectEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<RedirectEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the redirect entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<RedirectEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first redirect entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching redirect entry
	 * @throws NoSuchEntryException if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry findByUuid_First(
			String uuid, OrderByComparator<RedirectEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first redirect entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry fetchByUuid_First(
		String uuid, OrderByComparator<RedirectEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the redirect entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of redirect entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching redirect entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<RedirectEntry, NoSuchEntryException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the redirect entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching redirect entry
	 * @throws NoSuchEntryException if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the redirect entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the redirect entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the redirect entry that was removed
	 */
	@Override
	public RedirectEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException {

		RedirectEntry redirectEntry = findByUUID_G(uuid, groupId);

		return remove(redirectEntry);
	}

	/**
	 * Returns the number of redirect entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching redirect entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<RedirectEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the redirect entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<RedirectEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first redirect entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching redirect entry
	 * @throws NoSuchEntryException if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<RedirectEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first redirect entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<RedirectEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the redirect entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of redirect entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching redirect entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<RedirectEntry, NoSuchEntryException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the redirect entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<RedirectEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first redirect entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching redirect entry
	 * @throws NoSuchEntryException if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry findByGroupId_First(
			long groupId, OrderByComparator<RedirectEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first redirect entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry fetchByGroupId_First(
		long groupId, OrderByComparator<RedirectEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the redirect entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching redirect entries that the user has permission to view
	 */
	@Override
	public List<RedirectEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<RedirectEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Removes all the redirect entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of redirect entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching redirect entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of redirect entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching redirect entries that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupId}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<RedirectEntry, NoSuchEntryException> _collectionPersistenceFinderByG_D;

	/**
	 * Returns an ordered range of all the redirect entries where groupId = &#63; and destinationURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByG_D(
		long groupId, String destinationURL, int start, int end,
		OrderByComparator<RedirectEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_D.find(
			finderCache, new Object[] {groupId, destinationURL}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first redirect entry in the ordered set where groupId = &#63; and destinationURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching redirect entry
	 * @throws NoSuchEntryException if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry findByG_D_First(
			long groupId, String destinationURL,
			OrderByComparator<RedirectEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_D.findFirst(
			finderCache, new Object[] {groupId, destinationURL},
			orderByComparator);
	}

	/**
	 * Returns the first redirect entry in the ordered set where groupId = &#63; and destinationURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry fetchByG_D_First(
		long groupId, String destinationURL,
		OrderByComparator<RedirectEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_D.fetchFirst(
			finderCache, new Object[] {groupId, destinationURL},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the redirect entries that the user has permissions to view where groupId = &#63; and destinationURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching redirect entries that the user has permission to view
	 */
	@Override
	public List<RedirectEntry> filterFindByG_D(
		long groupId, String destinationURL, int start, int end,
		OrderByComparator<RedirectEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_D.filterFind(
			finderCache, new Object[] {groupId, destinationURL}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the redirect entries where groupId = &#63; and destinationURL = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 */
	@Override
	public void removeByG_D(long groupId, String destinationURL) {
		_collectionPersistenceFinderByG_D.remove(
			finderCache, new Object[] {groupId, destinationURL});
	}

	/**
	 * Returns the number of redirect entries where groupId = &#63; and destinationURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @return the number of matching redirect entries
	 */
	@Override
	public int countByG_D(long groupId, String destinationURL) {
		return _collectionPersistenceFinderByG_D.count(
			finderCache, new Object[] {groupId, destinationURL});
	}

	/**
	 * Returns the number of redirect entries that the user has permission to view where groupId = &#63; and destinationURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @return the number of matching redirect entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_D(long groupId, String destinationURL) {
		return _collectionPersistenceFinderByG_D.filterCount(
			finderCache, new Object[] {groupId, destinationURL}, groupId);
	}

	private UniquePersistenceFinder<RedirectEntry, NoSuchEntryException>
		_uniquePersistenceFinderByG_S;

	/**
	 * Returns the redirect entry where groupId = &#63; and sourceURL = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param sourceURL the source url
	 * @return the matching redirect entry
	 * @throws NoSuchEntryException if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry findByG_S(long groupId, String sourceURL)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByG_S.find(
			finderCache, new Object[] {groupId, sourceURL});
	}

	/**
	 * Returns the redirect entry where groupId = &#63; and sourceURL = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param sourceURL the source url
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry fetchByG_S(
		long groupId, String sourceURL, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_S.fetch(
			finderCache, new Object[] {groupId, sourceURL}, useFinderCache);
	}

	/**
	 * Removes the redirect entry where groupId = &#63; and sourceURL = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param sourceURL the source url
	 * @return the redirect entry that was removed
	 */
	@Override
	public RedirectEntry removeByG_S(long groupId, String sourceURL)
		throws NoSuchEntryException {

		RedirectEntry redirectEntry = findByG_S(groupId, sourceURL);

		return remove(redirectEntry);
	}

	/**
	 * Returns the number of redirect entries where groupId = &#63; and sourceURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sourceURL the source url
	 * @return the number of matching redirect entries
	 */
	@Override
	public int countByG_S(long groupId, String sourceURL) {
		return _uniquePersistenceFinderByG_S.count(
			finderCache, new Object[] {groupId, sourceURL});
	}

	public RedirectEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("permanent", "permanent_");

		setDBColumnNames(dbColumnNames);

		setModelClass(RedirectEntry.class);

		setModelImplClass(RedirectEntryImpl.class);
		setModelPKClass(long.class);

		setTable(RedirectEntryTable.INSTANCE);
	}

	/**
	 * Creates a new redirect entry with the primary key. Does not add the redirect entry to the database.
	 *
	 * @param redirectEntryId the primary key for the new redirect entry
	 * @return the new redirect entry
	 */
	@Override
	public RedirectEntry create(long redirectEntryId) {
		RedirectEntry redirectEntry = new RedirectEntryImpl();

		redirectEntry.setNew(true);
		redirectEntry.setPrimaryKey(redirectEntryId);

		String uuid = PortalUUIDUtil.generate();

		redirectEntry.setUuid(uuid);

		redirectEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return redirectEntry;
	}

	/**
	 * Removes the redirect entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param redirectEntryId the primary key of the redirect entry
	 * @return the redirect entry that was removed
	 * @throws NoSuchEntryException if a redirect entry with the primary key could not be found
	 */
	@Override
	public RedirectEntry remove(long redirectEntryId)
		throws NoSuchEntryException {

		return remove((Serializable)redirectEntryId);
	}

	@Override
	protected RedirectEntry removeImpl(RedirectEntry redirectEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(redirectEntry)) {
				redirectEntry = (RedirectEntry)session.get(
					RedirectEntryImpl.class, redirectEntry.getPrimaryKeyObj());
			}

			if (redirectEntry != null) {
				session.delete(redirectEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (redirectEntry != null) {
			clearCache(redirectEntry);
		}

		return redirectEntry;
	}

	@Override
	public RedirectEntry updateImpl(RedirectEntry redirectEntry) {
		boolean isNew = redirectEntry.isNew();

		if (!(redirectEntry instanceof RedirectEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(redirectEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					redirectEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in redirectEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom RedirectEntry implementation " +
					redirectEntry.getClass());
		}

		RedirectEntryModelImpl redirectEntryModelImpl =
			(RedirectEntryModelImpl)redirectEntry;

		if (Validator.isNull(redirectEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			redirectEntry.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (redirectEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				redirectEntry.setCreateDate(date);
			}
			else {
				redirectEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!redirectEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				redirectEntry.setModifiedDate(date);
			}
			else {
				redirectEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		long userId = GetterUtil.getLong(PrincipalThreadLocal.getName());

		if (userId > 0) {
			long companyId = redirectEntry.getCompanyId();

			long groupId = redirectEntry.getGroupId();

			long redirectEntryId = 0;

			if (!isNew) {
				redirectEntryId = redirectEntry.getPrimaryKey();
			}

			try {
				redirectEntry.setDestinationURL(
					SanitizerUtil.sanitize(
						companyId, groupId, userId,
						RedirectEntry.class.getName(), redirectEntryId,
						ContentTypes.TEXT_PLAIN, Sanitizer.MODE_ALL,
						redirectEntry.getDestinationURL(), null));

				redirectEntry.setSourceURL(
					SanitizerUtil.sanitize(
						companyId, groupId, userId,
						RedirectEntry.class.getName(), redirectEntryId,
						ContentTypes.TEXT_PLAIN, Sanitizer.MODE_ALL,
						redirectEntry.getSourceURL(), null));
			}
			catch (SanitizerException sanitizerException) {
				throw new SystemException(sanitizerException);
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(redirectEntry);
			}
			else {
				redirectEntry = (RedirectEntry)session.merge(redirectEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(redirectEntry, false);

		if (isNew) {
			redirectEntry.setNew(false);
		}

		redirectEntry.resetOriginalValues();

		return redirectEntry;
	}

	/**
	 * Returns the redirect entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param redirectEntryId the primary key of the redirect entry
	 * @return the redirect entry
	 * @throws NoSuchEntryException if a redirect entry with the primary key could not be found
	 */
	@Override
	public RedirectEntry findByPrimaryKey(long redirectEntryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)redirectEntryId);
	}

	/**
	 * Returns the redirect entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param redirectEntryId the primary key of the redirect entry
	 * @return the redirect entry, or <code>null</code> if a redirect entry with the primary key could not be found
	 */
	@Override
	public RedirectEntry fetchByPrimaryKey(long redirectEntryId) {
		return fetchByPrimaryKey((Serializable)redirectEntryId);
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
		return "redirectEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_REDIRECTENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return RedirectEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the redirect entry persistence.
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
			_SQL_SELECT_REDIRECTENTRY_WHERE, _SQL_COUNT_REDIRECTENTRY_WHERE,
			RedirectEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"redirectEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, RedirectEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(RedirectEntry::getUuid),
				RedirectEntry::getGroupId),
			_SQL_SELECT_REDIRECTENTRY_WHERE, "",
			new FinderColumn<>(
				"redirectEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, RedirectEntry::getUuid),
			new FinderColumn<>(
				"redirectEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, RedirectEntry::getGroupId));

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
				_SQL_SELECT_REDIRECTENTRY_WHERE, _SQL_COUNT_REDIRECTENTRY_WHERE,
				RedirectEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"redirectEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, RedirectEntry::getUuid),
				new FinderColumn<>(
					"redirectEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, RedirectEntry::getCompanyId));

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
				_SQL_SELECT_REDIRECTENTRY_WHERE, _SQL_COUNT_REDIRECTENTRY_WHERE,
				RedirectEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"redirectEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, RedirectEntry::getGroupId));

		_collectionPersistenceFinderByG_D =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_D",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "destinationURL"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_D",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "destinationURL"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_D",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "destinationURL"}, 0, 2, false,
					null),
				_SQL_SELECT_REDIRECTENTRY_WHERE, _SQL_COUNT_REDIRECTENTRY_WHERE,
				RedirectEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"redirectEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, RedirectEntry::getGroupId),
				new FinderColumn<>(
					"redirectEntry.", "destinationURL",
					FinderColumn.Type.STRING, "=", true, true,
					RedirectEntry::getDestinationURL));

		_uniquePersistenceFinderByG_S = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_S",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "sourceURL"}, 0, 2, false,
				RedirectEntry::getGroupId,
				convertNullFunction(RedirectEntry::getSourceURL)),
			_SQL_SELECT_REDIRECTENTRY_WHERE, "",
			new FinderColumn<>(
				"redirectEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, RedirectEntry::getGroupId),
			new FinderColumn<>(
				"redirectEntry.", "sourceURL", FinderColumn.Type.STRING, "=",
				true, true, RedirectEntry::getSourceURL));

		RedirectEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		RedirectEntryUtil.setPersistence(null);

		entityCache.removeCache(RedirectEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = RedirectPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = RedirectPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = RedirectPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		RedirectEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_REDIRECTENTRY =
		"SELECT redirectEntry FROM RedirectEntry redirectEntry";

	private static final String _SQL_SELECT_REDIRECTENTRY_WHERE =
		"SELECT redirectEntry FROM RedirectEntry redirectEntry WHERE ";

	private static final String _SQL_COUNT_REDIRECTENTRY_WHERE =
		"SELECT COUNT(redirectEntry) FROM RedirectEntry redirectEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No RedirectEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		RedirectEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "permanent"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1211171624