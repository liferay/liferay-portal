/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl;

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
import com.liferay.portal.tools.service.builder.test.compat740.exception.DuplicateERCVersionedEntryExternalReferenceCodeException;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchERCVersionedEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.ERCVersionedEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.ERCVersionedEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.ERCVersionedEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.ERCVersionedEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.ERCVersionedEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.ERCVersionedEntryUtil;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl.constants.SBCompat740PersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

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
 * The persistence implementation for the erc versioned entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = ERCVersionedEntryPersistence.class)
public class ERCVersionedEntryPersistenceImpl
	extends BasePersistenceImpl
		<ERCVersionedEntry, NoSuchERCVersionedEntryException>
	implements ERCVersionedEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ERCVersionedEntryUtil</code> to access the erc versioned entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ERCVersionedEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ERCVersionedEntry, NoSuchERCVersionedEntryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the erc versioned entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCVersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of erc versioned entries
	 * @param end the upper bound of the range of erc versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching erc versioned entries
	 */
	@Override
	public List<ERCVersionedEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ERCVersionedEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first erc versioned entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry findByUuid_First(
			String uuid, OrderByComparator<ERCVersionedEntry> orderByComparator)
		throws NoSuchERCVersionedEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first erc versioned entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc versioned entry, or <code>null</code> if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry fetchByUuid_First(
		String uuid, OrderByComparator<ERCVersionedEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the erc versioned entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of erc versioned entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching erc versioned entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ERCVersionedEntry, NoSuchERCVersionedEntryException>
			_collectionPersistenceFinderByUuid_Head;

	/**
	 * Returns an ordered range of all the erc versioned entries where uuid = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCVersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param head the head
	 * @param start the lower bound of the range of erc versioned entries
	 * @param end the upper bound of the range of erc versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching erc versioned entries
	 */
	@Override
	public List<ERCVersionedEntry> findByUuid_Head(
		String uuid, boolean head, int start, int end,
		OrderByComparator<ERCVersionedEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_Head.find(
			finderCache, new Object[] {uuid, head}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first erc versioned entry in the ordered set where uuid = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry findByUuid_Head_First(
			String uuid, boolean head,
			OrderByComparator<ERCVersionedEntry> orderByComparator)
		throws NoSuchERCVersionedEntryException {

		return _collectionPersistenceFinderByUuid_Head.findFirst(
			finderCache, new Object[] {uuid, head}, orderByComparator);
	}

	/**
	 * Returns the first erc versioned entry in the ordered set where uuid = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc versioned entry, or <code>null</code> if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry fetchByUuid_Head_First(
		String uuid, boolean head,
		OrderByComparator<ERCVersionedEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_Head.fetchFirst(
			finderCache, new Object[] {uuid, head}, orderByComparator);
	}

	/**
	 * Removes all the erc versioned entries where uuid = &#63; and head = &#63; from the database.
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
	 * Returns the number of erc versioned entries where uuid = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param head the head
	 * @return the number of matching erc versioned entries
	 */
	@Override
	public int countByUuid_Head(String uuid, boolean head) {
		return _collectionPersistenceFinderByUuid_Head.count(
			finderCache, new Object[] {uuid, head});
	}

	private CollectionPersistenceFinder
		<ERCVersionedEntry, NoSuchERCVersionedEntryException>
			_collectionPersistenceFinderByUUID_G;

	/**
	 * Returns an ordered range of all the erc versioned entries where uuid = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCVersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param start the lower bound of the range of erc versioned entries
	 * @param end the upper bound of the range of erc versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching erc versioned entries
	 */
	@Override
	public List<ERCVersionedEntry> findByUUID_G(
		String uuid, long groupId, int start, int end,
		OrderByComparator<ERCVersionedEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first erc versioned entry in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry findByUUID_G_First(
			String uuid, long groupId,
			OrderByComparator<ERCVersionedEntry> orderByComparator)
		throws NoSuchERCVersionedEntryException {

		return _collectionPersistenceFinderByUUID_G.findFirst(
			finderCache, new Object[] {uuid, groupId}, orderByComparator);
	}

	/**
	 * Returns the first erc versioned entry in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc versioned entry, or <code>null</code> if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry fetchByUUID_G_First(
		String uuid, long groupId,
		OrderByComparator<ERCVersionedEntry> orderByComparator) {

		return _collectionPersistenceFinderByUUID_G.fetchFirst(
			finderCache, new Object[] {uuid, groupId}, orderByComparator);
	}

	/**
	 * Removes all the erc versioned entries where uuid = &#63; and groupId = &#63; from the database.
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
	 * Returns the number of erc versioned entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching erc versioned entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _collectionPersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private UniquePersistenceFinder
		<ERCVersionedEntry, NoSuchERCVersionedEntryException>
			_uniquePersistenceFinderByUUID_G_Head;

	/**
	 * Returns the erc versioned entry where uuid = &#63; and groupId = &#63; and head = &#63; or throws a <code>NoSuchERCVersionedEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param head the head
	 * @return the matching erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry findByUUID_G_Head(
			String uuid, long groupId, boolean head)
		throws NoSuchERCVersionedEntryException {

		return _uniquePersistenceFinderByUUID_G_Head.find(
			finderCache, new Object[] {uuid, groupId, head});
	}

	/**
	 * Returns the erc versioned entry where uuid = &#63; and groupId = &#63; and head = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param head the head
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching erc versioned entry, or <code>null</code> if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry fetchByUUID_G_Head(
		String uuid, long groupId, boolean head, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G_Head.fetch(
			finderCache, new Object[] {uuid, groupId, head}, useFinderCache);
	}

	/**
	 * Removes the erc versioned entry where uuid = &#63; and groupId = &#63; and head = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param head the head
	 * @return the erc versioned entry that was removed
	 */
	@Override
	public ERCVersionedEntry removeByUUID_G_Head(
			String uuid, long groupId, boolean head)
		throws NoSuchERCVersionedEntryException {

		ERCVersionedEntry ercVersionedEntry = findByUUID_G_Head(
			uuid, groupId, head);

		return remove(ercVersionedEntry);
	}

	/**
	 * Returns the number of erc versioned entries where uuid = &#63; and groupId = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param head the head
	 * @return the number of matching erc versioned entries
	 */
	@Override
	public int countByUUID_G_Head(String uuid, long groupId, boolean head) {
		return _uniquePersistenceFinderByUUID_G_Head.count(
			finderCache, new Object[] {uuid, groupId, head});
	}

	private CollectionPersistenceFinder
		<ERCVersionedEntry, NoSuchERCVersionedEntryException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the erc versioned entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCVersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of erc versioned entries
	 * @param end the upper bound of the range of erc versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching erc versioned entries
	 */
	@Override
	public List<ERCVersionedEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ERCVersionedEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first erc versioned entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ERCVersionedEntry> orderByComparator)
		throws NoSuchERCVersionedEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first erc versioned entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc versioned entry, or <code>null</code> if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ERCVersionedEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the erc versioned entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of erc versioned entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching erc versioned entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ERCVersionedEntry, NoSuchERCVersionedEntryException>
			_collectionPersistenceFinderByUuid_C_Head;

	/**
	 * Returns an ordered range of all the erc versioned entries where uuid = &#63; and companyId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCVersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 * @param start the lower bound of the range of erc versioned entries
	 * @param end the upper bound of the range of erc versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching erc versioned entries
	 */
	@Override
	public List<ERCVersionedEntry> findByUuid_C_Head(
		String uuid, long companyId, boolean head, int start, int end,
		OrderByComparator<ERCVersionedEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C_Head.find(
			finderCache, new Object[] {uuid, companyId, head}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first erc versioned entry in the ordered set where uuid = &#63; and companyId = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry findByUuid_C_Head_First(
			String uuid, long companyId, boolean head,
			OrderByComparator<ERCVersionedEntry> orderByComparator)
		throws NoSuchERCVersionedEntryException {

		return _collectionPersistenceFinderByUuid_C_Head.findFirst(
			finderCache, new Object[] {uuid, companyId, head},
			orderByComparator);
	}

	/**
	 * Returns the first erc versioned entry in the ordered set where uuid = &#63; and companyId = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc versioned entry, or <code>null</code> if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry fetchByUuid_C_Head_First(
		String uuid, long companyId, boolean head,
		OrderByComparator<ERCVersionedEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C_Head.fetchFirst(
			finderCache, new Object[] {uuid, companyId, head},
			orderByComparator);
	}

	/**
	 * Removes all the erc versioned entries where uuid = &#63; and companyId = &#63; and head = &#63; from the database.
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
	 * Returns the number of erc versioned entries where uuid = &#63; and companyId = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 * @return the number of matching erc versioned entries
	 */
	@Override
	public int countByUuid_C_Head(String uuid, long companyId, boolean head) {
		return _collectionPersistenceFinderByUuid_C_Head.count(
			finderCache, new Object[] {uuid, companyId, head});
	}

	private CollectionPersistenceFinder
		<ERCVersionedEntry, NoSuchERCVersionedEntryException>
			_collectionPersistenceFinderByERC_G;

	/**
	 * Returns an ordered range of all the erc versioned entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCVersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param start the lower bound of the range of erc versioned entries
	 * @param end the upper bound of the range of erc versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching erc versioned entries
	 */
	@Override
	public List<ERCVersionedEntry> findByERC_G(
		String externalReferenceCode, long groupId, int start, int end,
		OrderByComparator<ERCVersionedEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first erc versioned entry in the ordered set where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry findByERC_G_First(
			String externalReferenceCode, long groupId,
			OrderByComparator<ERCVersionedEntry> orderByComparator)
		throws NoSuchERCVersionedEntryException {

		return _collectionPersistenceFinderByERC_G.findFirst(
			finderCache, new Object[] {externalReferenceCode, groupId},
			orderByComparator);
	}

	/**
	 * Returns the first erc versioned entry in the ordered set where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc versioned entry, or <code>null</code> if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry fetchByERC_G_First(
		String externalReferenceCode, long groupId,
		OrderByComparator<ERCVersionedEntry> orderByComparator) {

		return _collectionPersistenceFinderByERC_G.fetchFirst(
			finderCache, new Object[] {externalReferenceCode, groupId},
			orderByComparator);
	}

	/**
	 * Removes all the erc versioned entries where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 */
	@Override
	public void removeByERC_G(String externalReferenceCode, long groupId) {
		_collectionPersistenceFinderByERC_G.remove(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the number of erc versioned entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching erc versioned entries
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _collectionPersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	private UniquePersistenceFinder
		<ERCVersionedEntry, NoSuchERCVersionedEntryException>
			_uniquePersistenceFinderByERC_G_Head;

	/**
	 * Returns the erc versioned entry where externalReferenceCode = &#63; and groupId = &#63; and head = &#63; or throws a <code>NoSuchERCVersionedEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param head the head
	 * @return the matching erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry findByERC_G_Head(
			String externalReferenceCode, long groupId, boolean head)
		throws NoSuchERCVersionedEntryException {

		return _uniquePersistenceFinderByERC_G_Head.find(
			finderCache, new Object[] {externalReferenceCode, groupId, head});
	}

	/**
	 * Returns the erc versioned entry where externalReferenceCode = &#63; and groupId = &#63; and head = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param head the head
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching erc versioned entry, or <code>null</code> if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry fetchByERC_G_Head(
		String externalReferenceCode, long groupId, boolean head,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G_Head.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId, head},
			useFinderCache);
	}

	/**
	 * Removes the erc versioned entry where externalReferenceCode = &#63; and groupId = &#63; and head = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param head the head
	 * @return the erc versioned entry that was removed
	 */
	@Override
	public ERCVersionedEntry removeByERC_G_Head(
			String externalReferenceCode, long groupId, boolean head)
		throws NoSuchERCVersionedEntryException {

		ERCVersionedEntry ercVersionedEntry = findByERC_G_Head(
			externalReferenceCode, groupId, head);

		return remove(ercVersionedEntry);
	}

	/**
	 * Returns the number of erc versioned entries where externalReferenceCode = &#63; and groupId = &#63; and head = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param head the head
	 * @return the number of matching erc versioned entries
	 */
	@Override
	public int countByERC_G_Head(
		String externalReferenceCode, long groupId, boolean head) {

		return _uniquePersistenceFinderByERC_G_Head.count(
			finderCache, new Object[] {externalReferenceCode, groupId, head});
	}

	private UniquePersistenceFinder
		<ERCVersionedEntry, NoSuchERCVersionedEntryException>
			_uniquePersistenceFinderByHeadId;

	/**
	 * Returns the erc versioned entry where headId = &#63; or throws a <code>NoSuchERCVersionedEntryException</code> if it could not be found.
	 *
	 * @param headId the head ID
	 * @return the matching erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry findByHeadId(long headId)
		throws NoSuchERCVersionedEntryException {

		return _uniquePersistenceFinderByHeadId.find(
			finderCache, new Object[] {headId});
	}

	/**
	 * Returns the erc versioned entry where headId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param headId the head ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching erc versioned entry, or <code>null</code> if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry fetchByHeadId(
		long headId, boolean useFinderCache) {

		return _uniquePersistenceFinderByHeadId.fetch(
			finderCache, new Object[] {headId}, useFinderCache);
	}

	/**
	 * Removes the erc versioned entry where headId = &#63; from the database.
	 *
	 * @param headId the head ID
	 * @return the erc versioned entry that was removed
	 */
	@Override
	public ERCVersionedEntry removeByHeadId(long headId)
		throws NoSuchERCVersionedEntryException {

		ERCVersionedEntry ercVersionedEntry = findByHeadId(headId);

		return remove(ercVersionedEntry);
	}

	/**
	 * Returns the number of erc versioned entries where headId = &#63;.
	 *
	 * @param headId the head ID
	 * @return the number of matching erc versioned entries
	 */
	@Override
	public int countByHeadId(long headId) {
		return _uniquePersistenceFinderByHeadId.count(
			finderCache, new Object[] {headId});
	}

	public ERCVersionedEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ERCVersionedEntry.class);

		setModelImplClass(ERCVersionedEntryImpl.class);
		setModelPKClass(long.class);

		setTable(ERCVersionedEntryTable.INSTANCE);
	}

	/**
	 * Creates a new erc versioned entry with the primary key. Does not add the erc versioned entry to the database.
	 *
	 * @param ercVersionedEntryId the primary key for the new erc versioned entry
	 * @return the new erc versioned entry
	 */
	@Override
	public ERCVersionedEntry create(long ercVersionedEntryId) {
		ERCVersionedEntry ercVersionedEntry = new ERCVersionedEntryImpl();

		ercVersionedEntry.setNew(true);
		ercVersionedEntry.setPrimaryKey(ercVersionedEntryId);

		String uuid = PortalUUIDUtil.generate();

		ercVersionedEntry.setUuid(uuid);

		ercVersionedEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ercVersionedEntry;
	}

	/**
	 * Removes the erc versioned entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ercVersionedEntryId the primary key of the erc versioned entry
	 * @return the erc versioned entry that was removed
	 * @throws NoSuchERCVersionedEntryException if a erc versioned entry with the primary key could not be found
	 */
	@Override
	public ERCVersionedEntry remove(long ercVersionedEntryId)
		throws NoSuchERCVersionedEntryException {

		return remove((Serializable)ercVersionedEntryId);
	}

	@Override
	protected ERCVersionedEntry removeImpl(
		ERCVersionedEntry ercVersionedEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ercVersionedEntry)) {
				ercVersionedEntry = (ERCVersionedEntry)session.get(
					ERCVersionedEntryImpl.class,
					ercVersionedEntry.getPrimaryKeyObj());
			}

			if (ercVersionedEntry != null) {
				session.delete(ercVersionedEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ercVersionedEntry != null) {
			clearCache(ercVersionedEntry);
		}

		return ercVersionedEntry;
	}

	@Override
	public ERCVersionedEntry updateImpl(ERCVersionedEntry ercVersionedEntry) {
		boolean isNew = ercVersionedEntry.isNew();

		if (!(ercVersionedEntry instanceof ERCVersionedEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ercVersionedEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ercVersionedEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ercVersionedEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ERCVersionedEntry implementation " +
					ercVersionedEntry.getClass());
		}

		ERCVersionedEntryModelImpl ercVersionedEntryModelImpl =
			(ERCVersionedEntryModelImpl)ercVersionedEntry;

		if (Validator.isNull(ercVersionedEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			ercVersionedEntry.setUuid(uuid);
		}

		if (Validator.isNull(ercVersionedEntry.getExternalReferenceCode())) {
			ercVersionedEntry.setExternalReferenceCode(
				ercVersionedEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					ercVersionedEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					ercVersionedEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = ercVersionedEntry.getCompanyId();

					long groupId = ercVersionedEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = ercVersionedEntry.getPrimaryKey();
					}

					try {
						ercVersionedEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ERCVersionedEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								ercVersionedEntry.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			ERCVersionedEntry ercERCVersionedEntry = fetchByERC_G_Head(
				ercVersionedEntry.getExternalReferenceCode(),
				ercVersionedEntry.getGroupId(), ercVersionedEntry.isHead());

			if (isNew) {
				if (ercERCVersionedEntry != null) {
					throw new DuplicateERCVersionedEntryExternalReferenceCodeException(
						"Duplicate erc versioned entry with external reference code " +
							ercVersionedEntry.getExternalReferenceCode() +
								" and group " + ercVersionedEntry.getGroupId());
				}
			}
			else {
				if ((ercERCVersionedEntry != null) &&
					(ercVersionedEntry.getErcVersionedEntryId() !=
						ercERCVersionedEntry.getErcVersionedEntryId())) {

					throw new DuplicateERCVersionedEntryExternalReferenceCodeException(
						"Duplicate erc versioned entry with external reference code " +
							ercVersionedEntry.getExternalReferenceCode() +
								" and group " + ercVersionedEntry.getGroupId());
				}
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ercVersionedEntry);
			}
			else {
				ercVersionedEntry = (ERCVersionedEntry)session.merge(
					ercVersionedEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ercVersionedEntry, false);

		if (isNew) {
			ercVersionedEntry.setNew(false);
		}

		ercVersionedEntry.resetOriginalValues();

		return ercVersionedEntry;
	}

	/**
	 * Returns the erc versioned entry with the primary key or throws a <code>NoSuchERCVersionedEntryException</code> if it could not be found.
	 *
	 * @param ercVersionedEntryId the primary key of the erc versioned entry
	 * @return the erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a erc versioned entry with the primary key could not be found
	 */
	@Override
	public ERCVersionedEntry findByPrimaryKey(long ercVersionedEntryId)
		throws NoSuchERCVersionedEntryException {

		return findByPrimaryKey((Serializable)ercVersionedEntryId);
	}

	/**
	 * Returns the erc versioned entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ercVersionedEntryId the primary key of the erc versioned entry
	 * @return the erc versioned entry, or <code>null</code> if a erc versioned entry with the primary key could not be found
	 */
	@Override
	public ERCVersionedEntry fetchByPrimaryKey(long ercVersionedEntryId) {
		return fetchByPrimaryKey((Serializable)ercVersionedEntryId);
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
		return "ercVersionedEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ERCVERSIONEDENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ERCVersionedEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the erc versioned entry persistence.
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
			_SQL_SELECT_ERCVERSIONEDENTRY_WHERE,
			_SQL_COUNT_ERCVERSIONEDENTRY_WHERE,
			ERCVersionedEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"ercVersionedEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, ERCVersionedEntry::getUuid));

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
				_SQL_SELECT_ERCVERSIONEDENTRY_WHERE,
				_SQL_COUNT_ERCVERSIONEDENTRY_WHERE,
				ERCVersionedEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"ercVersionedEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ERCVersionedEntry::getUuid),
				new FinderColumn<>(
					"ercVersionedEntry.", "head", FinderColumn.Type.BOOLEAN,
					"=", true, true, ERCVersionedEntry::isHead));

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
				_SQL_SELECT_ERCVERSIONEDENTRY_WHERE,
				_SQL_COUNT_ERCVERSIONEDENTRY_WHERE,
				ERCVersionedEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"ercVersionedEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ERCVersionedEntry::getUuid),
				new FinderColumn<>(
					"ercVersionedEntry.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, ERCVersionedEntry::getGroupId));

		_uniquePersistenceFinderByUUID_G_Head = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G_Head",
				new String[] {
					String.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"uuid_", "groupId", "head"}, 0, 1, false,
				convertNullFunction(ERCVersionedEntry::getUuid),
				ERCVersionedEntry::getGroupId, ERCVersionedEntry::isHead),
			_SQL_SELECT_ERCVERSIONEDENTRY_WHERE, "",
			new FinderColumn<>(
				"ercVersionedEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, ERCVersionedEntry::getUuid),
			new FinderColumn<>(
				"ercVersionedEntry.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, ERCVersionedEntry::getGroupId),
			new FinderColumn<>(
				"ercVersionedEntry.", "head", FinderColumn.Type.BOOLEAN, "=",
				true, true, ERCVersionedEntry::isHead));

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
				_SQL_SELECT_ERCVERSIONEDENTRY_WHERE,
				_SQL_COUNT_ERCVERSIONEDENTRY_WHERE,
				ERCVersionedEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"ercVersionedEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ERCVersionedEntry::getUuid),
				new FinderColumn<>(
					"ercVersionedEntry.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ERCVersionedEntry::getCompanyId));

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
				_SQL_SELECT_ERCVERSIONEDENTRY_WHERE,
				_SQL_COUNT_ERCVERSIONEDENTRY_WHERE,
				ERCVersionedEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"ercVersionedEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ERCVersionedEntry::getUuid),
				new FinderColumn<>(
					"ercVersionedEntry.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ERCVersionedEntry::getCompanyId),
				new FinderColumn<>(
					"ercVersionedEntry.", "head", FinderColumn.Type.BOOLEAN,
					"=", true, true, ERCVersionedEntry::isHead));

		_collectionPersistenceFinderByERC_G = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByERC_G",
				new String[] {
					String.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"externalReferenceCode", "groupId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				null),
			_SQL_SELECT_ERCVERSIONEDENTRY_WHERE,
			_SQL_COUNT_ERCVERSIONEDENTRY_WHERE,
			ERCVersionedEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"ercVersionedEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				ERCVersionedEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"ercVersionedEntry.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, ERCVersionedEntry::getGroupId));

		_uniquePersistenceFinderByERC_G_Head = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G_Head",
				new String[] {
					String.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"externalReferenceCode", "groupId", "head"}, 0, 1,
				false,
				convertNullFunction(
					ERCVersionedEntry::getExternalReferenceCode),
				ERCVersionedEntry::getGroupId, ERCVersionedEntry::isHead),
			_SQL_SELECT_ERCVERSIONEDENTRY_WHERE, "",
			new FinderColumn<>(
				"ercVersionedEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				ERCVersionedEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"ercVersionedEntry.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, ERCVersionedEntry::getGroupId),
			new FinderColumn<>(
				"ercVersionedEntry.", "head", FinderColumn.Type.BOOLEAN, "=",
				true, true, ERCVersionedEntry::isHead));

		_uniquePersistenceFinderByHeadId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByHeadId",
				new String[] {Long.class.getName()}, new String[] {"headId"}, 0,
				0, false, ERCVersionedEntry::getHeadId),
			_SQL_SELECT_ERCVERSIONEDENTRY_WHERE, "",
			new FinderColumn<>(
				"ercVersionedEntry.", "headId", FinderColumn.Type.LONG, "=",
				true, true, ERCVersionedEntry::getHeadId));

		ERCVersionedEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ERCVersionedEntryUtil.setPersistence(null);

		entityCache.removeCache(ERCVersionedEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		ERCVersionedEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ERCVERSIONEDENTRY =
		"SELECT ercVersionedEntry FROM ERCVersionedEntry ercVersionedEntry";

	private static final String _SQL_SELECT_ERCVERSIONEDENTRY_WHERE =
		"SELECT ercVersionedEntry FROM ERCVersionedEntry ercVersionedEntry WHERE ";

	private static final String _SQL_COUNT_ERCVERSIONEDENTRY_WHERE =
		"SELECT COUNT(ercVersionedEntry) FROM ERCVersionedEntry ercVersionedEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ERCVersionedEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ERCVersionedEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1890836630