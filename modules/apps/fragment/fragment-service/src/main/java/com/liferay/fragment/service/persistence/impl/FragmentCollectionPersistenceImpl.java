/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.persistence.impl;

import com.liferay.fragment.exception.DuplicateFragmentCollectionExternalReferenceCodeException;
import com.liferay.fragment.exception.NoSuchCollectionException;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentCollectionTable;
import com.liferay.fragment.model.impl.FragmentCollectionImpl;
import com.liferay.fragment.model.impl.FragmentCollectionModelImpl;
import com.liferay.fragment.service.persistence.FragmentCollectionPersistence;
import com.liferay.fragment.service.persistence.FragmentCollectionUtil;
import com.liferay.fragment.service.persistence.impl.constants.FragmentPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
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
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
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
 * The persistence implementation for the fragment collection service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = FragmentCollectionPersistence.class)
public class FragmentCollectionPersistenceImpl
	extends BasePersistenceImpl<FragmentCollection, NoSuchCollectionException>
	implements FragmentCollectionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>FragmentCollectionUtil</code> to access the fragment collection persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		FragmentCollectionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<FragmentCollection, NoSuchCollectionException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the fragment collections where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment collection in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment collection
	 * @throws NoSuchCollectionException if a matching fragment collection could not be found
	 */
	@Override
	public FragmentCollection findByUuid_First(
			String uuid,
			OrderByComparator<FragmentCollection> orderByComparator)
		throws NoSuchCollectionException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first fragment collection in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment collection, or <code>null</code> if a matching fragment collection could not be found
	 */
	@Override
	public FragmentCollection fetchByUuid_First(
		String uuid, OrderByComparator<FragmentCollection> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the fragment collections where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of fragment collections where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching fragment collections
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<FragmentCollection, NoSuchCollectionException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the fragment collection where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCollectionException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching fragment collection
	 * @throws NoSuchCollectionException if a matching fragment collection could not be found
	 */
	@Override
	public FragmentCollection findByUUID_G(String uuid, long groupId)
		throws NoSuchCollectionException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the fragment collection where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment collection, or <code>null</code> if a matching fragment collection could not be found
	 */
	@Override
	public FragmentCollection fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the fragment collection where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the fragment collection that was removed
	 */
	@Override
	public FragmentCollection removeByUUID_G(String uuid, long groupId)
		throws NoSuchCollectionException {

		FragmentCollection fragmentCollection = findByUUID_G(uuid, groupId);

		return remove(fragmentCollection);
	}

	/**
	 * Returns the number of fragment collections where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching fragment collections
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<FragmentCollection, NoSuchCollectionException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the fragment collections where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment collection in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment collection
	 * @throws NoSuchCollectionException if a matching fragment collection could not be found
	 */
	@Override
	public FragmentCollection findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<FragmentCollection> orderByComparator)
		throws NoSuchCollectionException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first fragment collection in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment collection, or <code>null</code> if a matching fragment collection could not be found
	 */
	@Override
	public FragmentCollection fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the fragment collections where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of fragment collections where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching fragment collections
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<FragmentCollection, NoSuchCollectionException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the fragment collections where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {new long[] {groupId}}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment collection in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment collection
	 * @throws NoSuchCollectionException if a matching fragment collection could not be found
	 */
	@Override
	public FragmentCollection findByGroupId_First(
			long groupId,
			OrderByComparator<FragmentCollection> orderByComparator)
		throws NoSuchCollectionException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {new long[] {groupId}},
			orderByComparator);
	}

	/**
	 * Returns the first fragment collection in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment collection, or <code>null</code> if a matching fragment collection could not be found
	 */
	@Override
	public FragmentCollection fetchByGroupId_First(
		long groupId, OrderByComparator<FragmentCollection> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the fragment collections where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds)}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the fragment collections where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of fragment collections where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching fragment collections
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of fragment collections where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching fragment collections
	 */
	@Override
	public int countByGroupId(long[] groupIds) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds)});
	}

	private UniquePersistenceFinder
		<FragmentCollection, NoSuchCollectionException>
			_uniquePersistenceFinderByG_FCK;

	/**
	 * Returns the fragment collection where groupId = &#63; and fragmentCollectionKey = &#63; or throws a <code>NoSuchCollectionException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionKey the fragment collection key
	 * @return the matching fragment collection
	 * @throws NoSuchCollectionException if a matching fragment collection could not be found
	 */
	@Override
	public FragmentCollection findByG_FCK(
			long groupId, String fragmentCollectionKey)
		throws NoSuchCollectionException {

		return _uniquePersistenceFinderByG_FCK.find(
			finderCache, new Object[] {groupId, fragmentCollectionKey});
	}

	/**
	 * Returns the fragment collection where groupId = &#63; and fragmentCollectionKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionKey the fragment collection key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment collection, or <code>null</code> if a matching fragment collection could not be found
	 */
	@Override
	public FragmentCollection fetchByG_FCK(
		long groupId, String fragmentCollectionKey, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_FCK.fetch(
			finderCache, new Object[] {groupId, fragmentCollectionKey},
			useFinderCache);
	}

	/**
	 * Removes the fragment collection where groupId = &#63; and fragmentCollectionKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionKey the fragment collection key
	 * @return the fragment collection that was removed
	 */
	@Override
	public FragmentCollection removeByG_FCK(
			long groupId, String fragmentCollectionKey)
		throws NoSuchCollectionException {

		FragmentCollection fragmentCollection = findByG_FCK(
			groupId, fragmentCollectionKey);

		return remove(fragmentCollection);
	}

	/**
	 * Returns the number of fragment collections where groupId = &#63; and fragmentCollectionKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionKey the fragment collection key
	 * @return the number of matching fragment collections
	 */
	@Override
	public int countByG_FCK(long groupId, String fragmentCollectionKey) {
		return _uniquePersistenceFinderByG_FCK.count(
			finderCache, new Object[] {groupId, fragmentCollectionKey});
	}

	private CollectionPersistenceFinder
		<FragmentCollection, NoSuchCollectionException>
			_collectionPersistenceFinderByG_LikeN;

	/**
	 * Returns all the fragment collections where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByG_LikeN(long groupId, String name) {
		return findByG_LikeN(
			groupId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the fragment collections where groupId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @return the range of matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByG_LikeN(
		long groupId, String name, int start, int end) {

		return findByG_LikeN(groupId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the fragment collections where groupId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByG_LikeN(
		long groupId, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return findByG_LikeN(
			groupId, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the fragment collections where groupId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByG_LikeN(
		long groupId, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN.find(
			finderCache, new Object[] {new long[] {groupId}, name}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment collection in the ordered set where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment collection
	 * @throws NoSuchCollectionException if a matching fragment collection could not be found
	 */
	@Override
	public FragmentCollection findByG_LikeN_First(
			long groupId, String name,
			OrderByComparator<FragmentCollection> orderByComparator)
		throws NoSuchCollectionException {

		return _collectionPersistenceFinderByG_LikeN.findFirst(
			finderCache, new Object[] {new long[] {groupId}, name},
			orderByComparator);
	}

	/**
	 * Returns the first fragment collection in the ordered set where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment collection, or <code>null</code> if a matching fragment collection could not be found
	 */
	@Override
	public FragmentCollection fetchByG_LikeN_First(
		long groupId, String name,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}, name},
			orderByComparator);
	}

	/**
	 * Returns all the fragment collections where groupId = any &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @return the matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByG_LikeN(
		long[] groupIds, String name) {

		return findByG_LikeN(
			groupIds, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the fragment collections where groupId = any &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @return the range of matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByG_LikeN(
		long[] groupIds, String name, int start, int end) {

		return findByG_LikeN(groupIds, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the fragment collections where groupId = any &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByG_LikeN(
		long[] groupIds, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return findByG_LikeN(
			groupIds, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the fragment collections where groupId = &#63; and name LIKE &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByG_LikeN(
		long[] groupIds, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN.find(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds), name},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the fragment collections where groupId = &#63; and name LIKE &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 */
	@Override
	public void removeByG_LikeN(long groupId, String name) {
		_collectionPersistenceFinderByG_LikeN.remove(
			finderCache, new Object[] {new long[] {groupId}, name});
	}

	/**
	 * Returns the number of fragment collections where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching fragment collections
	 */
	@Override
	public int countByG_LikeN(long groupId, String name) {
		return _collectionPersistenceFinderByG_LikeN.count(
			finderCache, new Object[] {new long[] {groupId}, name});
	}

	/**
	 * Returns the number of fragment collections where groupId = any &#63; and name LIKE &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @return the number of matching fragment collections
	 */
	@Override
	public int countByG_LikeN(long[] groupIds, String name) {
		return _collectionPersistenceFinderByG_LikeN.count(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds), name});
	}

	private CollectionPersistenceFinder
		<FragmentCollection, NoSuchCollectionException>
			_collectionPersistenceFinderByG_M;

	/**
	 * Returns an ordered range of all the fragment collections where groupId = &#63; and marketplace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param marketplace the marketplace
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByG_M(
		long groupId, boolean marketplace, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_M.find(
			finderCache, new Object[] {new long[] {groupId}, marketplace},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment collection in the ordered set where groupId = &#63; and marketplace = &#63;.
	 *
	 * @param groupId the group ID
	 * @param marketplace the marketplace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment collection
	 * @throws NoSuchCollectionException if a matching fragment collection could not be found
	 */
	@Override
	public FragmentCollection findByG_M_First(
			long groupId, boolean marketplace,
			OrderByComparator<FragmentCollection> orderByComparator)
		throws NoSuchCollectionException {

		return _collectionPersistenceFinderByG_M.findFirst(
			finderCache, new Object[] {new long[] {groupId}, marketplace},
			orderByComparator);
	}

	/**
	 * Returns the first fragment collection in the ordered set where groupId = &#63; and marketplace = &#63;.
	 *
	 * @param groupId the group ID
	 * @param marketplace the marketplace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment collection, or <code>null</code> if a matching fragment collection could not be found
	 */
	@Override
	public FragmentCollection fetchByG_M_First(
		long groupId, boolean marketplace,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return _collectionPersistenceFinderByG_M.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}, marketplace},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the fragment collections where groupId = &#63; and marketplace = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param marketplace the marketplace
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByG_M(
		long[] groupIds, boolean marketplace, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_M.find(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), marketplace}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the fragment collections where groupId = &#63; and marketplace = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param marketplace the marketplace
	 */
	@Override
	public void removeByG_M(long groupId, boolean marketplace) {
		_collectionPersistenceFinderByG_M.remove(
			finderCache, new Object[] {new long[] {groupId}, marketplace});
	}

	/**
	 * Returns the number of fragment collections where groupId = &#63; and marketplace = &#63;.
	 *
	 * @param groupId the group ID
	 * @param marketplace the marketplace
	 * @return the number of matching fragment collections
	 */
	@Override
	public int countByG_M(long groupId, boolean marketplace) {
		return _collectionPersistenceFinderByG_M.count(
			finderCache, new Object[] {new long[] {groupId}, marketplace});
	}

	/**
	 * Returns the number of fragment collections where groupId = any &#63; and marketplace = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param marketplace the marketplace
	 * @return the number of matching fragment collections
	 */
	@Override
	public int countByG_M(long[] groupIds, boolean marketplace) {
		return _collectionPersistenceFinderByG_M.count(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), marketplace});
	}

	private CollectionPersistenceFinder
		<FragmentCollection, NoSuchCollectionException>
			_collectionPersistenceFinderByG_LikeN_M;

	/**
	 * Returns all the fragment collections where groupId = &#63; and name LIKE &#63; and marketplace = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param marketplace the marketplace
	 * @return the matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByG_LikeN_M(
		long groupId, String name, boolean marketplace) {

		return findByG_LikeN_M(
			groupId, name, marketplace, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the fragment collections where groupId = &#63; and name LIKE &#63; and marketplace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param marketplace the marketplace
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @return the range of matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByG_LikeN_M(
		long groupId, String name, boolean marketplace, int start, int end) {

		return findByG_LikeN_M(groupId, name, marketplace, start, end, null);
	}

	/**
	 * Returns an ordered range of all the fragment collections where groupId = &#63; and name LIKE &#63; and marketplace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param marketplace the marketplace
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByG_LikeN_M(
		long groupId, String name, boolean marketplace, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return findByG_LikeN_M(
			groupId, name, marketplace, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the fragment collections where groupId = &#63; and name LIKE &#63; and marketplace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param marketplace the marketplace
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByG_LikeN_M(
		long groupId, String name, boolean marketplace, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN_M.find(
			finderCache, new Object[] {new long[] {groupId}, name, marketplace},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment collection in the ordered set where groupId = &#63; and name LIKE &#63; and marketplace = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param marketplace the marketplace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment collection
	 * @throws NoSuchCollectionException if a matching fragment collection could not be found
	 */
	@Override
	public FragmentCollection findByG_LikeN_M_First(
			long groupId, String name, boolean marketplace,
			OrderByComparator<FragmentCollection> orderByComparator)
		throws NoSuchCollectionException {

		return _collectionPersistenceFinderByG_LikeN_M.findFirst(
			finderCache, new Object[] {new long[] {groupId}, name, marketplace},
			orderByComparator);
	}

	/**
	 * Returns the first fragment collection in the ordered set where groupId = &#63; and name LIKE &#63; and marketplace = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param marketplace the marketplace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment collection, or <code>null</code> if a matching fragment collection could not be found
	 */
	@Override
	public FragmentCollection fetchByG_LikeN_M_First(
		long groupId, String name, boolean marketplace,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN_M.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}, name, marketplace},
			orderByComparator);
	}

	/**
	 * Returns all the fragment collections where groupId = any &#63; and name LIKE &#63; and marketplace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param marketplace the marketplace
	 * @return the matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByG_LikeN_M(
		long[] groupIds, String name, boolean marketplace) {

		return findByG_LikeN_M(
			groupIds, name, marketplace, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the fragment collections where groupId = any &#63; and name LIKE &#63; and marketplace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param marketplace the marketplace
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @return the range of matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByG_LikeN_M(
		long[] groupIds, String name, boolean marketplace, int start, int end) {

		return findByG_LikeN_M(groupIds, name, marketplace, start, end, null);
	}

	/**
	 * Returns an ordered range of all the fragment collections where groupId = any &#63; and name LIKE &#63; and marketplace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param marketplace the marketplace
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByG_LikeN_M(
		long[] groupIds, String name, boolean marketplace, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return findByG_LikeN_M(
			groupIds, name, marketplace, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the fragment collections where groupId = &#63; and name LIKE &#63; and marketplace = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param marketplace the marketplace
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment collections
	 */
	@Override
	public List<FragmentCollection> findByG_LikeN_M(
		long[] groupIds, String name, boolean marketplace, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN_M.find(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), name, marketplace},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the fragment collections where groupId = &#63; and name LIKE &#63; and marketplace = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param marketplace the marketplace
	 */
	@Override
	public void removeByG_LikeN_M(
		long groupId, String name, boolean marketplace) {

		_collectionPersistenceFinderByG_LikeN_M.remove(
			finderCache,
			new Object[] {new long[] {groupId}, name, marketplace});
	}

	/**
	 * Returns the number of fragment collections where groupId = &#63; and name LIKE &#63; and marketplace = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param marketplace the marketplace
	 * @return the number of matching fragment collections
	 */
	@Override
	public int countByG_LikeN_M(
		long groupId, String name, boolean marketplace) {

		return _collectionPersistenceFinderByG_LikeN_M.count(
			finderCache,
			new Object[] {new long[] {groupId}, name, marketplace});
	}

	/**
	 * Returns the number of fragment collections where groupId = any &#63; and name LIKE &#63; and marketplace = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param marketplace the marketplace
	 * @return the number of matching fragment collections
	 */
	@Override
	public int countByG_LikeN_M(
		long[] groupIds, String name, boolean marketplace) {

		return _collectionPersistenceFinderByG_LikeN_M.count(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), name, marketplace});
	}

	private UniquePersistenceFinder
		<FragmentCollection, NoSuchCollectionException>
			_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the fragment collection where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchCollectionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching fragment collection
	 * @throws NoSuchCollectionException if a matching fragment collection could not be found
	 */
	@Override
	public FragmentCollection findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchCollectionException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the fragment collection where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment collection, or <code>null</code> if a matching fragment collection could not be found
	 */
	@Override
	public FragmentCollection fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the fragment collection where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the fragment collection that was removed
	 */
	@Override
	public FragmentCollection removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchCollectionException {

		FragmentCollection fragmentCollection = findByERC_G(
			externalReferenceCode, groupId);

		return remove(fragmentCollection);
	}

	/**
	 * Returns the number of fragment collections where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching fragment collections
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public FragmentCollectionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(FragmentCollection.class);

		setModelImplClass(FragmentCollectionImpl.class);
		setModelPKClass(long.class);

		setTable(FragmentCollectionTable.INSTANCE);
	}

	/**
	 * Creates a new fragment collection with the primary key. Does not add the fragment collection to the database.
	 *
	 * @param fragmentCollectionId the primary key for the new fragment collection
	 * @return the new fragment collection
	 */
	@Override
	public FragmentCollection create(long fragmentCollectionId) {
		FragmentCollection fragmentCollection = new FragmentCollectionImpl();

		fragmentCollection.setNew(true);
		fragmentCollection.setPrimaryKey(fragmentCollectionId);

		String uuid = PortalUUIDUtil.generate();

		fragmentCollection.setUuid(uuid);

		fragmentCollection.setCompanyId(CompanyThreadLocal.getCompanyId());

		return fragmentCollection;
	}

	/**
	 * Removes the fragment collection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fragmentCollectionId the primary key of the fragment collection
	 * @return the fragment collection that was removed
	 * @throws NoSuchCollectionException if a fragment collection with the primary key could not be found
	 */
	@Override
	public FragmentCollection remove(long fragmentCollectionId)
		throws NoSuchCollectionException {

		return remove((Serializable)fragmentCollectionId);
	}

	@Override
	protected FragmentCollection removeImpl(
		FragmentCollection fragmentCollection) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(fragmentCollection)) {
				fragmentCollection = (FragmentCollection)session.get(
					FragmentCollectionImpl.class,
					fragmentCollection.getPrimaryKeyObj());
			}

			if ((fragmentCollection != null) &&
				ctPersistenceHelper.isRemove(fragmentCollection)) {

				session.delete(fragmentCollection);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (fragmentCollection != null) {
			clearCache(fragmentCollection);
		}

		return fragmentCollection;
	}

	@Override
	public FragmentCollection updateImpl(
		FragmentCollection fragmentCollection) {

		boolean isNew = fragmentCollection.isNew();

		if (!(fragmentCollection instanceof FragmentCollectionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(fragmentCollection.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					fragmentCollection);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in fragmentCollection proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom FragmentCollection implementation " +
					fragmentCollection.getClass());
		}

		FragmentCollectionModelImpl fragmentCollectionModelImpl =
			(FragmentCollectionModelImpl)fragmentCollection;

		if (Validator.isNull(fragmentCollection.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			fragmentCollection.setUuid(uuid);
		}

		if (Validator.isNull(fragmentCollection.getExternalReferenceCode())) {
			fragmentCollection.setExternalReferenceCode(
				fragmentCollection.getUuid());
		}
		else {
			if (!Objects.equals(
					fragmentCollectionModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					fragmentCollection.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = fragmentCollection.getCompanyId();

					long groupId = fragmentCollection.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = fragmentCollection.getPrimaryKey();
					}

					try {
						fragmentCollection.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								FragmentCollection.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								fragmentCollection.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			FragmentCollection ercFragmentCollection = fetchByERC_G(
				fragmentCollection.getExternalReferenceCode(),
				fragmentCollection.getGroupId());

			if (isNew) {
				if (ercFragmentCollection != null) {
					throw new DuplicateFragmentCollectionExternalReferenceCodeException(
						"Duplicate fragment collection with external reference code " +
							fragmentCollection.getExternalReferenceCode() +
								" and group " +
									fragmentCollection.getGroupId());
				}
			}
			else {
				if ((ercFragmentCollection != null) &&
					(fragmentCollection.getFragmentCollectionId() !=
						ercFragmentCollection.getFragmentCollectionId())) {

					throw new DuplicateFragmentCollectionExternalReferenceCodeException(
						"Duplicate fragment collection with external reference code " +
							fragmentCollection.getExternalReferenceCode() +
								" and group " +
									fragmentCollection.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (fragmentCollection.getCreateDate() == null)) {
			if (serviceContext == null) {
				fragmentCollection.setCreateDate(date);
			}
			else {
				fragmentCollection.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!fragmentCollectionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				fragmentCollection.setModifiedDate(date);
			}
			else {
				fragmentCollection.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(fragmentCollection)) {
				if (!isNew) {
					session.evict(
						FragmentCollectionImpl.class,
						fragmentCollection.getPrimaryKeyObj());
				}

				session.save(fragmentCollection);
			}
			else {
				fragmentCollection = (FragmentCollection)session.merge(
					fragmentCollection);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(fragmentCollection, false);

		if (isNew) {
			fragmentCollection.setNew(false);
		}

		fragmentCollection.resetOriginalValues();

		return fragmentCollection;
	}

	/**
	 * Returns the fragment collection with the primary key or throws a <code>NoSuchCollectionException</code> if it could not be found.
	 *
	 * @param fragmentCollectionId the primary key of the fragment collection
	 * @return the fragment collection
	 * @throws NoSuchCollectionException if a fragment collection with the primary key could not be found
	 */
	@Override
	public FragmentCollection findByPrimaryKey(long fragmentCollectionId)
		throws NoSuchCollectionException {

		return findByPrimaryKey((Serializable)fragmentCollectionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the fragment collection with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fragmentCollectionId the primary key of the fragment collection
	 * @return the fragment collection, or <code>null</code> if a fragment collection with the primary key could not be found
	 */
	@Override
	public FragmentCollection fetchByPrimaryKey(long fragmentCollectionId) {
		return fetchByPrimaryKey((Serializable)fragmentCollectionId);
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
		return "fragmentCollectionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_FRAGMENTCOLLECTION;
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
		return FragmentCollectionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "FragmentCollection";
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
		ctMergeColumnNames.add("fragmentCollectionKey");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("marketplace");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("fragmentCollectionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "fragmentCollectionKey"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the fragment collection persistence.
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
			_SQL_SELECT_FRAGMENTCOLLECTION_WHERE,
			_SQL_COUNT_FRAGMENTCOLLECTION_WHERE,
			FragmentCollectionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"fragmentCollection.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				FragmentCollection::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(FragmentCollection::getUuid),
				FragmentCollection::getGroupId),
			_SQL_SELECT_FRAGMENTCOLLECTION_WHERE, "",
			new FinderColumn<>(
				"fragmentCollection.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				FragmentCollection::getUuid),
			new FinderColumn<>(
				"fragmentCollection.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, FragmentCollection::getGroupId));

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
				_SQL_SELECT_FRAGMENTCOLLECTION_WHERE,
				_SQL_COUNT_FRAGMENTCOLLECTION_WHERE,
				FragmentCollectionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"fragmentCollection.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentCollection::getUuid),
				new FinderColumn<>(
					"fragmentCollection.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, FragmentCollection::getCompanyId));

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
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_FRAGMENTCOLLECTION_WHERE,
				_SQL_COUNT_FRAGMENTCOLLECTION_WHERE,
				FragmentCollectionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new ArrayableFinderColumn<>(
					"fragmentCollection.", "groupId", FinderColumn.Type.LONG,
					"=", false, true, true, FragmentCollection::getGroupId));

		_uniquePersistenceFinderByG_FCK = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_FCK",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "fragmentCollectionKey"}, 0, 2, false,
				FragmentCollection::getGroupId,
				convertNullFunction(
					FragmentCollection::getFragmentCollectionKey)),
			_SQL_SELECT_FRAGMENTCOLLECTION_WHERE, "",
			new FinderColumn<>(
				"fragmentCollection.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, FragmentCollection::getGroupId),
			new FinderColumn<>(
				"fragmentCollection.", "fragmentCollectionKey",
				FinderColumn.Type.STRING, "=", true, true,
				FragmentCollection::getFragmentCollectionKey));

		_collectionPersistenceFinderByG_LikeN =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeN",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LikeN",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "name"}, false),
				_SQL_SELECT_FRAGMENTCOLLECTION_WHERE,
				_SQL_COUNT_FRAGMENTCOLLECTION_WHERE,
				FragmentCollectionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new ArrayableFinderColumn<>(
					"fragmentCollection.", "groupId", FinderColumn.Type.LONG,
					"=", false, true, true, FragmentCollection::getGroupId),
				new FinderColumn<>(
					"fragmentCollection.", "name", FinderColumn.Type.STRING,
					"LIKE", true, true, FragmentCollection::getName));

		_collectionPersistenceFinderByG_M = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_M",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "marketplace"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_M",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"groupId", "marketplace"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_M",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"groupId", "marketplace"}, false),
			_SQL_SELECT_FRAGMENTCOLLECTION_WHERE,
			_SQL_COUNT_FRAGMENTCOLLECTION_WHERE,
			FragmentCollectionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new ArrayableFinderColumn<>(
				"fragmentCollection.", "groupId", FinderColumn.Type.LONG, "=",
				false, true, true, FragmentCollection::getGroupId),
			new FinderColumn<>(
				"fragmentCollection.", "marketplace", FinderColumn.Type.BOOLEAN,
				"=", true, true, FragmentCollection::isMarketplace));

		_collectionPersistenceFinderByG_LikeN_M =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeN_M",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name", "marketplace"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LikeN_M",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "name", "marketplace"}, false),
				_SQL_SELECT_FRAGMENTCOLLECTION_WHERE,
				_SQL_COUNT_FRAGMENTCOLLECTION_WHERE,
				FragmentCollectionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new ArrayableFinderColumn<>(
					"fragmentCollection.", "groupId", FinderColumn.Type.LONG,
					"=", false, true, true, FragmentCollection::getGroupId),
				new FinderColumn<>(
					"fragmentCollection.", "name", FinderColumn.Type.STRING,
					"LIKE", true, true, FragmentCollection::getName),
				new FinderColumn<>(
					"fragmentCollection.", "marketplace",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					FragmentCollection::isMarketplace));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(
					FragmentCollection::getExternalReferenceCode),
				FragmentCollection::getGroupId),
			_SQL_SELECT_FRAGMENTCOLLECTION_WHERE, "",
			new FinderColumn<>(
				"fragmentCollection.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				FragmentCollection::getExternalReferenceCode),
			new FinderColumn<>(
				"fragmentCollection.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, FragmentCollection::getGroupId));

		FragmentCollectionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		FragmentCollectionUtil.setPersistence(null);

		entityCache.removeCache(FragmentCollectionImpl.class.getName());
	}

	@Override
	@Reference(
		target = FragmentPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = FragmentPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = FragmentPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		FragmentCollectionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_FRAGMENTCOLLECTION =
		"SELECT fragmentCollection FROM FragmentCollection fragmentCollection";

	private static final String _SQL_SELECT_FRAGMENTCOLLECTION_WHERE =
		"SELECT fragmentCollection FROM FragmentCollection fragmentCollection WHERE ";

	private static final String _SQL_COUNT_FRAGMENTCOLLECTION_WHERE =
		"SELECT COUNT(fragmentCollection) FROM FragmentCollection fragmentCollection WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:337855566