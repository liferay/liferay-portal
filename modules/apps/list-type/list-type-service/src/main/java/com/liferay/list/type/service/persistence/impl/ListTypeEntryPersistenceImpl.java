/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service.persistence.impl;

import com.liferay.list.type.exception.NoSuchListTypeEntryException;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.model.ListTypeEntryTable;
import com.liferay.list.type.model.impl.ListTypeEntryImpl;
import com.liferay.list.type.model.impl.ListTypeEntryModelImpl;
import com.liferay.list.type.service.persistence.ListTypeEntryPersistence;
import com.liferay.list.type.service.persistence.ListTypeEntryUtil;
import com.liferay.list.type.service.persistence.impl.constants.ListTypePersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
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
 * The persistence implementation for the list type entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Gabriel Albuquerque
 * @generated
 */
@Component(service = ListTypeEntryPersistence.class)
public class ListTypeEntryPersistenceImpl
	extends BasePersistenceImpl<ListTypeEntry, NoSuchListTypeEntryException>
	implements ListTypeEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ListTypeEntryUtil</code> to access the list type entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ListTypeEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ListTypeEntry, NoSuchListTypeEntryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the list type entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ListTypeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of list type entries
	 * @param end the upper bound of the range of list type entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching list type entries
	 */
	@Override
	public List<ListTypeEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ListTypeEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first list type entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type entry
	 * @throws NoSuchListTypeEntryException if a matching list type entry could not be found
	 */
	@Override
	public ListTypeEntry findByUuid_First(
			String uuid, OrderByComparator<ListTypeEntry> orderByComparator)
		throws NoSuchListTypeEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first list type entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type entry, or <code>null</code> if a matching list type entry could not be found
	 */
	@Override
	public ListTypeEntry fetchByUuid_First(
		String uuid, OrderByComparator<ListTypeEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the list type entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of list type entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching list type entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ListTypeEntry, NoSuchListTypeEntryException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the list type entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ListTypeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of list type entries
	 * @param end the upper bound of the range of list type entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching list type entries
	 */
	@Override
	public List<ListTypeEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ListTypeEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first list type entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type entry
	 * @throws NoSuchListTypeEntryException if a matching list type entry could not be found
	 */
	@Override
	public ListTypeEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ListTypeEntry> orderByComparator)
		throws NoSuchListTypeEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first list type entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type entry, or <code>null</code> if a matching list type entry could not be found
	 */
	@Override
	public ListTypeEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ListTypeEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the list type entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of list type entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching list type entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ListTypeEntry, NoSuchListTypeEntryException>
			_collectionPersistenceFinderByListTypeEntryId;

	/**
	 * Returns an ordered range of all the list type entries where listTypeEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ListTypeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeEntryId the list type entry ID
	 * @param start the lower bound of the range of list type entries
	 * @param end the upper bound of the range of list type entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching list type entries
	 */
	@Override
	public List<ListTypeEntry> findByListTypeEntryId(
		long listTypeEntryId, int start, int end,
		OrderByComparator<ListTypeEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByListTypeEntryId.find(
			finderCache, new Object[] {new long[] {listTypeEntryId}}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first list type entry in the ordered set where listTypeEntryId = &#63;.
	 *
	 * @param listTypeEntryId the list type entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type entry
	 * @throws NoSuchListTypeEntryException if a matching list type entry could not be found
	 */
	@Override
	public ListTypeEntry findByListTypeEntryId_First(
			long listTypeEntryId,
			OrderByComparator<ListTypeEntry> orderByComparator)
		throws NoSuchListTypeEntryException {

		return _collectionPersistenceFinderByListTypeEntryId.findFirst(
			finderCache, new Object[] {new long[] {listTypeEntryId}},
			orderByComparator);
	}

	/**
	 * Returns the first list type entry in the ordered set where listTypeEntryId = &#63;.
	 *
	 * @param listTypeEntryId the list type entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type entry, or <code>null</code> if a matching list type entry could not be found
	 */
	@Override
	public ListTypeEntry fetchByListTypeEntryId_First(
		long listTypeEntryId,
		OrderByComparator<ListTypeEntry> orderByComparator) {

		return _collectionPersistenceFinderByListTypeEntryId.fetchFirst(
			finderCache, new Object[] {new long[] {listTypeEntryId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the list type entries where listTypeEntryId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ListTypeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeEntryIds the list type entry IDs
	 * @param start the lower bound of the range of list type entries
	 * @param end the upper bound of the range of list type entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching list type entries
	 */
	@Override
	public List<ListTypeEntry> findByListTypeEntryId(
		long[] listTypeEntryIds, int start, int end,
		OrderByComparator<ListTypeEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByListTypeEntryId.find(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(listTypeEntryIds)}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the list type entries where listTypeEntryId = &#63; from the database.
	 *
	 * @param listTypeEntryId the list type entry ID
	 */
	@Override
	public void removeByListTypeEntryId(long listTypeEntryId) {
		_collectionPersistenceFinderByListTypeEntryId.remove(
			finderCache, new Object[] {new long[] {listTypeEntryId}});
	}

	/**
	 * Returns the number of list type entries where listTypeEntryId = &#63;.
	 *
	 * @param listTypeEntryId the list type entry ID
	 * @return the number of matching list type entries
	 */
	@Override
	public int countByListTypeEntryId(long listTypeEntryId) {
		return _collectionPersistenceFinderByListTypeEntryId.count(
			finderCache, new Object[] {new long[] {listTypeEntryId}});
	}

	/**
	 * Returns the number of list type entries where listTypeEntryId = any &#63;.
	 *
	 * @param listTypeEntryIds the list type entry IDs
	 * @return the number of matching list type entries
	 */
	@Override
	public int countByListTypeEntryId(long[] listTypeEntryIds) {
		return _collectionPersistenceFinderByListTypeEntryId.count(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(listTypeEntryIds)});
	}

	private CollectionPersistenceFinder
		<ListTypeEntry, NoSuchListTypeEntryException>
			_collectionPersistenceFinderByListTypeDefinitionId;

	/**
	 * Returns an ordered range of all the list type entries where listTypeDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ListTypeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param start the lower bound of the range of list type entries
	 * @param end the upper bound of the range of list type entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching list type entries
	 */
	@Override
	public List<ListTypeEntry> findByListTypeDefinitionId(
		long listTypeDefinitionId, int start, int end,
		OrderByComparator<ListTypeEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByListTypeDefinitionId.find(
			finderCache, new Object[] {new long[] {listTypeDefinitionId}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first list type entry in the ordered set where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type entry
	 * @throws NoSuchListTypeEntryException if a matching list type entry could not be found
	 */
	@Override
	public ListTypeEntry findByListTypeDefinitionId_First(
			long listTypeDefinitionId,
			OrderByComparator<ListTypeEntry> orderByComparator)
		throws NoSuchListTypeEntryException {

		return _collectionPersistenceFinderByListTypeDefinitionId.findFirst(
			finderCache, new Object[] {new long[] {listTypeDefinitionId}},
			orderByComparator);
	}

	/**
	 * Returns the first list type entry in the ordered set where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type entry, or <code>null</code> if a matching list type entry could not be found
	 */
	@Override
	public ListTypeEntry fetchByListTypeDefinitionId_First(
		long listTypeDefinitionId,
		OrderByComparator<ListTypeEntry> orderByComparator) {

		return _collectionPersistenceFinderByListTypeDefinitionId.fetchFirst(
			finderCache, new Object[] {new long[] {listTypeDefinitionId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the list type entries where listTypeDefinitionId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ListTypeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionIds the list type definition IDs
	 * @param start the lower bound of the range of list type entries
	 * @param end the upper bound of the range of list type entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching list type entries
	 */
	@Override
	public List<ListTypeEntry> findByListTypeDefinitionId(
		long[] listTypeDefinitionIds, int start, int end,
		OrderByComparator<ListTypeEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByListTypeDefinitionId.find(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(listTypeDefinitionIds)}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the list type entries where listTypeDefinitionId = &#63; from the database.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 */
	@Override
	public void removeByListTypeDefinitionId(long listTypeDefinitionId) {
		_collectionPersistenceFinderByListTypeDefinitionId.remove(
			finderCache, new Object[] {new long[] {listTypeDefinitionId}});
	}

	/**
	 * Returns the number of list type entries where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @return the number of matching list type entries
	 */
	@Override
	public int countByListTypeDefinitionId(long listTypeDefinitionId) {
		return _collectionPersistenceFinderByListTypeDefinitionId.count(
			finderCache, new Object[] {new long[] {listTypeDefinitionId}});
	}

	/**
	 * Returns the number of list type entries where listTypeDefinitionId = any &#63;.
	 *
	 * @param listTypeDefinitionIds the list type definition IDs
	 * @return the number of matching list type entries
	 */
	@Override
	public int countByListTypeDefinitionId(long[] listTypeDefinitionIds) {
		return _collectionPersistenceFinderByListTypeDefinitionId.count(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(listTypeDefinitionIds)});
	}

	private CollectionPersistenceFinder
		<ListTypeEntry, NoSuchListTypeEntryException>
			_collectionPersistenceFinderByC_U;

	/**
	 * Returns an ordered range of all the list type entries where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ListTypeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of list type entries
	 * @param end the upper bound of the range of list type entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching list type entries
	 */
	@Override
	public List<ListTypeEntry> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<ListTypeEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_U.find(
			finderCache, new Object[] {companyId, userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first list type entry in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type entry
	 * @throws NoSuchListTypeEntryException if a matching list type entry could not be found
	 */
	@Override
	public ListTypeEntry findByC_U_First(
			long companyId, long userId,
			OrderByComparator<ListTypeEntry> orderByComparator)
		throws NoSuchListTypeEntryException {

		return _collectionPersistenceFinderByC_U.findFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Returns the first list type entry in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type entry, or <code>null</code> if a matching list type entry could not be found
	 */
	@Override
	public ListTypeEntry fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<ListTypeEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_U.fetchFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Removes all the list type entries where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByC_U(long companyId, long userId) {
		_collectionPersistenceFinderByC_U.remove(
			finderCache, new Object[] {companyId, userId});
	}

	/**
	 * Returns the number of list type entries where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching list type entries
	 */
	@Override
	public int countByC_U(long companyId, long userId) {
		return _collectionPersistenceFinderByC_U.count(
			finderCache, new Object[] {companyId, userId});
	}

	private UniquePersistenceFinder<ListTypeEntry, NoSuchListTypeEntryException>
		_uniquePersistenceFinderByLTDI_K;

	/**
	 * Returns the list type entry where listTypeDefinitionId = &#63; and key = &#63; or throws a <code>NoSuchListTypeEntryException</code> if it could not be found.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param key the key
	 * @return the matching list type entry
	 * @throws NoSuchListTypeEntryException if a matching list type entry could not be found
	 */
	@Override
	public ListTypeEntry findByLTDI_K(long listTypeDefinitionId, String key)
		throws NoSuchListTypeEntryException {

		return _uniquePersistenceFinderByLTDI_K.find(
			finderCache, new Object[] {listTypeDefinitionId, key});
	}

	/**
	 * Returns the list type entry where listTypeDefinitionId = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching list type entry, or <code>null</code> if a matching list type entry could not be found
	 */
	@Override
	public ListTypeEntry fetchByLTDI_K(
		long listTypeDefinitionId, String key, boolean useFinderCache) {

		return _uniquePersistenceFinderByLTDI_K.fetch(
			finderCache, new Object[] {listTypeDefinitionId, key},
			useFinderCache);
	}

	/**
	 * Removes the list type entry where listTypeDefinitionId = &#63; and key = &#63; from the database.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param key the key
	 * @return the list type entry that was removed
	 */
	@Override
	public ListTypeEntry removeByLTDI_K(long listTypeDefinitionId, String key)
		throws NoSuchListTypeEntryException {

		ListTypeEntry listTypeEntry = findByLTDI_K(listTypeDefinitionId, key);

		return remove(listTypeEntry);
	}

	/**
	 * Returns the number of list type entries where listTypeDefinitionId = &#63; and key = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param key the key
	 * @return the number of matching list type entries
	 */
	@Override
	public int countByLTDI_K(long listTypeDefinitionId, String key) {
		return _uniquePersistenceFinderByLTDI_K.count(
			finderCache, new Object[] {listTypeDefinitionId, key});
	}

	private UniquePersistenceFinder<ListTypeEntry, NoSuchListTypeEntryException>
		_uniquePersistenceFinderByERC_C_LTDI;

	/**
	 * Returns the list type entry where externalReferenceCode = &#63; and companyId = &#63; and listTypeDefinitionId = &#63; or throws a <code>NoSuchListTypeEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param listTypeDefinitionId the list type definition ID
	 * @return the matching list type entry
	 * @throws NoSuchListTypeEntryException if a matching list type entry could not be found
	 */
	@Override
	public ListTypeEntry findByERC_C_LTDI(
			String externalReferenceCode, long companyId,
			long listTypeDefinitionId)
		throws NoSuchListTypeEntryException {

		return _uniquePersistenceFinderByERC_C_LTDI.find(
			finderCache,
			new Object[] {
				externalReferenceCode, companyId, listTypeDefinitionId
			});
	}

	/**
	 * Returns the list type entry where externalReferenceCode = &#63; and companyId = &#63; and listTypeDefinitionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param listTypeDefinitionId the list type definition ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching list type entry, or <code>null</code> if a matching list type entry could not be found
	 */
	@Override
	public ListTypeEntry fetchByERC_C_LTDI(
		String externalReferenceCode, long companyId, long listTypeDefinitionId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C_LTDI.fetch(
			finderCache,
			new Object[] {
				externalReferenceCode, companyId, listTypeDefinitionId
			},
			useFinderCache);
	}

	/**
	 * Removes the list type entry where externalReferenceCode = &#63; and companyId = &#63; and listTypeDefinitionId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param listTypeDefinitionId the list type definition ID
	 * @return the list type entry that was removed
	 */
	@Override
	public ListTypeEntry removeByERC_C_LTDI(
			String externalReferenceCode, long companyId,
			long listTypeDefinitionId)
		throws NoSuchListTypeEntryException {

		ListTypeEntry listTypeEntry = findByERC_C_LTDI(
			externalReferenceCode, companyId, listTypeDefinitionId);

		return remove(listTypeEntry);
	}

	/**
	 * Returns the number of list type entries where externalReferenceCode = &#63; and companyId = &#63; and listTypeDefinitionId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param listTypeDefinitionId the list type definition ID
	 * @return the number of matching list type entries
	 */
	@Override
	public int countByERC_C_LTDI(
		String externalReferenceCode, long companyId,
		long listTypeDefinitionId) {

		return _uniquePersistenceFinderByERC_C_LTDI.count(
			finderCache,
			new Object[] {
				externalReferenceCode, companyId, listTypeDefinitionId
			});
	}

	public ListTypeEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("key", "key_");
		dbColumnNames.put("system", "system_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ListTypeEntry.class);

		setModelImplClass(ListTypeEntryImpl.class);
		setModelPKClass(long.class);

		setTable(ListTypeEntryTable.INSTANCE);
	}

	/**
	 * Creates a new list type entry with the primary key. Does not add the list type entry to the database.
	 *
	 * @param listTypeEntryId the primary key for the new list type entry
	 * @return the new list type entry
	 */
	@Override
	public ListTypeEntry create(long listTypeEntryId) {
		ListTypeEntry listTypeEntry = new ListTypeEntryImpl();

		listTypeEntry.setNew(true);
		listTypeEntry.setPrimaryKey(listTypeEntryId);

		String uuid = PortalUUIDUtil.generate();

		listTypeEntry.setUuid(uuid);

		listTypeEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return listTypeEntry;
	}

	/**
	 * Removes the list type entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param listTypeEntryId the primary key of the list type entry
	 * @return the list type entry that was removed
	 * @throws NoSuchListTypeEntryException if a list type entry with the primary key could not be found
	 */
	@Override
	public ListTypeEntry remove(long listTypeEntryId)
		throws NoSuchListTypeEntryException {

		return remove((Serializable)listTypeEntryId);
	}

	@Override
	protected ListTypeEntry removeImpl(ListTypeEntry listTypeEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(listTypeEntry)) {
				listTypeEntry = (ListTypeEntry)session.get(
					ListTypeEntryImpl.class, listTypeEntry.getPrimaryKeyObj());
			}

			if (listTypeEntry != null) {
				session.delete(listTypeEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (listTypeEntry != null) {
			clearCache(listTypeEntry);
		}

		return listTypeEntry;
	}

	@Override
	public ListTypeEntry updateImpl(ListTypeEntry listTypeEntry) {
		boolean isNew = listTypeEntry.isNew();

		if (!(listTypeEntry instanceof ListTypeEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(listTypeEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					listTypeEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in listTypeEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ListTypeEntry implementation " +
					listTypeEntry.getClass());
		}

		ListTypeEntryModelImpl listTypeEntryModelImpl =
			(ListTypeEntryModelImpl)listTypeEntry;

		if (Validator.isNull(listTypeEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			listTypeEntry.setUuid(uuid);
		}

		if (Validator.isNull(listTypeEntry.getExternalReferenceCode())) {
			listTypeEntry.setExternalReferenceCode(listTypeEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					listTypeEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					listTypeEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = listTypeEntry.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = listTypeEntry.getPrimaryKey();
					}

					try {
						listTypeEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ListTypeEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								listTypeEntry.getExternalReferenceCode(),
								null));
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

		if (isNew && (listTypeEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				listTypeEntry.setCreateDate(date);
			}
			else {
				listTypeEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!listTypeEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				listTypeEntry.setModifiedDate(date);
			}
			else {
				listTypeEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(listTypeEntry);
			}
			else {
				listTypeEntry = (ListTypeEntry)session.merge(listTypeEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(listTypeEntry, false);

		if (isNew) {
			listTypeEntry.setNew(false);
		}

		listTypeEntry.resetOriginalValues();

		return listTypeEntry;
	}

	/**
	 * Returns the list type entry with the primary key or throws a <code>NoSuchListTypeEntryException</code> if it could not be found.
	 *
	 * @param listTypeEntryId the primary key of the list type entry
	 * @return the list type entry
	 * @throws NoSuchListTypeEntryException if a list type entry with the primary key could not be found
	 */
	@Override
	public ListTypeEntry findByPrimaryKey(long listTypeEntryId)
		throws NoSuchListTypeEntryException {

		return findByPrimaryKey((Serializable)listTypeEntryId);
	}

	/**
	 * Returns the list type entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param listTypeEntryId the primary key of the list type entry
	 * @return the list type entry, or <code>null</code> if a list type entry with the primary key could not be found
	 */
	@Override
	public ListTypeEntry fetchByPrimaryKey(long listTypeEntryId) {
		return fetchByPrimaryKey((Serializable)listTypeEntryId);
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
		return "listTypeEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LISTTYPEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ListTypeEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the list type entry persistence.
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
			_SQL_SELECT_LISTTYPEENTRY_WHERE, _SQL_COUNT_LISTTYPEENTRY_WHERE,
			ListTypeEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"listTypeEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, ListTypeEntry::getUuid));

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
				_SQL_SELECT_LISTTYPEENTRY_WHERE, _SQL_COUNT_LISTTYPEENTRY_WHERE,
				ListTypeEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"listTypeEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, ListTypeEntry::getUuid),
				new FinderColumn<>(
					"listTypeEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, ListTypeEntry::getCompanyId));

		_collectionPersistenceFinderByListTypeEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByListTypeEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"listTypeEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByListTypeEntryId",
					new String[] {Long.class.getName()},
					new String[] {"listTypeEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByListTypeEntryId",
					new String[] {Long.class.getName()},
					new String[] {"listTypeEntryId"}, false),
				_SQL_SELECT_LISTTYPEENTRY_WHERE, _SQL_COUNT_LISTTYPEENTRY_WHERE,
				ListTypeEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new ArrayableFinderColumn<>(
					"listTypeEntry.", "listTypeEntryId", FinderColumn.Type.LONG,
					"=", false, true, true, ListTypeEntry::getListTypeEntryId));

		_collectionPersistenceFinderByListTypeDefinitionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByListTypeDefinitionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"listTypeDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByListTypeDefinitionId",
					new String[] {Long.class.getName()},
					new String[] {"listTypeDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByListTypeDefinitionId",
					new String[] {Long.class.getName()},
					new String[] {"listTypeDefinitionId"}, false),
				_SQL_SELECT_LISTTYPEENTRY_WHERE, _SQL_COUNT_LISTTYPEENTRY_WHERE,
				ListTypeEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new ArrayableFinderColumn<>(
					"listTypeEntry.", "listTypeDefinitionId",
					FinderColumn.Type.LONG, "=", false, true, true,
					ListTypeEntry::getListTypeDefinitionId));

		_collectionPersistenceFinderByC_U = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_U",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "userId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "userId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "userId"}, false),
			_SQL_SELECT_LISTTYPEENTRY_WHERE, _SQL_COUNT_LISTTYPEENTRY_WHERE,
			ListTypeEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"listTypeEntry.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, ListTypeEntry::getCompanyId),
			new FinderColumn<>(
				"listTypeEntry.", "userId", FinderColumn.Type.LONG, "=", true,
				true, ListTypeEntry::getUserId));

		_uniquePersistenceFinderByLTDI_K = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByLTDI_K",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"listTypeDefinitionId", "key_"}, 0, 2, false,
				ListTypeEntry::getListTypeDefinitionId,
				convertNullFunction(ListTypeEntry::getKey)),
			_SQL_SELECT_LISTTYPEENTRY_WHERE, "",
			new FinderColumn<>(
				"listTypeEntry.", "listTypeDefinitionId",
				FinderColumn.Type.LONG, "=", true, true,
				ListTypeEntry::getListTypeDefinitionId),
			new FinderColumn<>(
				"listTypeEntry.", "key", "key_", FinderColumn.Type.STRING, "=",
				true, true, ListTypeEntry::getKey));

		_uniquePersistenceFinderByERC_C_LTDI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C_LTDI",
				new String[] {
					String.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"externalReferenceCode", "companyId", "listTypeDefinitionId"
				},
				0, 1, false,
				convertNullFunction(ListTypeEntry::getExternalReferenceCode),
				ListTypeEntry::getCompanyId,
				ListTypeEntry::getListTypeDefinitionId),
			_SQL_SELECT_LISTTYPEENTRY_WHERE, "",
			new FinderColumn<>(
				"listTypeEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				ListTypeEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"listTypeEntry.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, ListTypeEntry::getCompanyId),
			new FinderColumn<>(
				"listTypeEntry.", "listTypeDefinitionId",
				FinderColumn.Type.LONG, "=", true, true,
				ListTypeEntry::getListTypeDefinitionId));

		ListTypeEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ListTypeEntryUtil.setPersistence(null);

		entityCache.removeCache(ListTypeEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = ListTypePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = ListTypePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = ListTypePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		ListTypeEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LISTTYPEENTRY =
		"SELECT listTypeEntry FROM ListTypeEntry listTypeEntry";

	private static final String _SQL_SELECT_LISTTYPEENTRY_WHERE =
		"SELECT listTypeEntry FROM ListTypeEntry listTypeEntry WHERE ";

	private static final String _SQL_COUNT_LISTTYPEENTRY_WHERE =
		"SELECT COUNT(listTypeEntry) FROM ListTypeEntry listTypeEntry WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "key", "system", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1907130449