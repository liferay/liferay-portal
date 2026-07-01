/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
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
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchLVEntryVersionException;
import com.liferay.portal.tools.service.builder.test.model.LVEntryVersion;
import com.liferay.portal.tools.service.builder.test.model.LVEntryVersionTable;
import com.liferay.portal.tools.service.builder.test.model.impl.LVEntryVersionImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.LVEntryVersionModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.BigDecimalEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.LVEntryLocalizationVersionPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.LVEntryVersionPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.LVEntryVersionUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the lv entry version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LVEntryVersionPersistenceImpl
	extends BasePersistenceImpl<LVEntryVersion, NoSuchLVEntryVersionException>
	implements LVEntryVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LVEntryVersionUtil</code> to access the lv entry version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LVEntryVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LVEntryVersion, NoSuchLVEntryVersionException>
			_collectionPersistenceFinderByLvEntryId;

	/**
	 * Returns an ordered range of all the lv entry versions where lvEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param lvEntryId the lv entry ID
	 * @param start the lower bound of the range of lv entry versions
	 * @param end the upper bound of the range of lv entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entry versions
	 */
	@Override
	public List<LVEntryVersion> findByLvEntryId(
		long lvEntryId, int start, int end,
		OrderByComparator<LVEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLvEntryId.find(
			finderCache, new Object[] {lvEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lv entry version in the ordered set where lvEntryId = &#63;.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry version
	 * @throws NoSuchLVEntryVersionException if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion findByLvEntryId_First(
			long lvEntryId, OrderByComparator<LVEntryVersion> orderByComparator)
		throws NoSuchLVEntryVersionException {

		return _collectionPersistenceFinderByLvEntryId.findFirst(
			finderCache, new Object[] {lvEntryId}, orderByComparator);
	}

	/**
	 * Returns the first lv entry version in the ordered set where lvEntryId = &#63;.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry version, or <code>null</code> if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion fetchByLvEntryId_First(
		long lvEntryId, OrderByComparator<LVEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByLvEntryId.fetchFirst(
			finderCache, new Object[] {lvEntryId}, orderByComparator);
	}

	/**
	 * Removes all the lv entry versions where lvEntryId = &#63; from the database.
	 *
	 * @param lvEntryId the lv entry ID
	 */
	@Override
	public void removeByLvEntryId(long lvEntryId) {
		_collectionPersistenceFinderByLvEntryId.remove(
			finderCache, new Object[] {lvEntryId});
	}

	/**
	 * Returns the number of lv entry versions where lvEntryId = &#63;.
	 *
	 * @param lvEntryId the lv entry ID
	 * @return the number of matching lv entry versions
	 */
	@Override
	public int countByLvEntryId(long lvEntryId) {
		return _collectionPersistenceFinderByLvEntryId.count(
			finderCache, new Object[] {lvEntryId});
	}

	private UniquePersistenceFinder
		<LVEntryVersion, NoSuchLVEntryVersionException>
			_uniquePersistenceFinderByLvEntryId_Version;

	/**
	 * Returns the lv entry version where lvEntryId = &#63; and version = &#63; or throws a <code>NoSuchLVEntryVersionException</code> if it could not be found.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param version the version
	 * @return the matching lv entry version
	 * @throws NoSuchLVEntryVersionException if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion findByLvEntryId_Version(long lvEntryId, int version)
		throws NoSuchLVEntryVersionException {

		return _uniquePersistenceFinderByLvEntryId_Version.find(
			finderCache, new Object[] {lvEntryId, version});
	}

	/**
	 * Returns the lv entry version where lvEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching lv entry version, or <code>null</code> if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion fetchByLvEntryId_Version(
		long lvEntryId, int version, boolean useFinderCache) {

		return _uniquePersistenceFinderByLvEntryId_Version.fetch(
			finderCache, new Object[] {lvEntryId, version}, useFinderCache);
	}

	/**
	 * Removes the lv entry version where lvEntryId = &#63; and version = &#63; from the database.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param version the version
	 * @return the lv entry version that was removed
	 */
	@Override
	public LVEntryVersion removeByLvEntryId_Version(long lvEntryId, int version)
		throws NoSuchLVEntryVersionException {

		LVEntryVersion lvEntryVersion = findByLvEntryId_Version(
			lvEntryId, version);

		return remove(lvEntryVersion);
	}

	/**
	 * Returns the number of lv entry versions where lvEntryId = &#63; and version = &#63;.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param version the version
	 * @return the number of matching lv entry versions
	 */
	@Override
	public int countByLvEntryId_Version(long lvEntryId, int version) {
		return _uniquePersistenceFinderByLvEntryId_Version.count(
			finderCache, new Object[] {lvEntryId, version});
	}

	private CollectionPersistenceFinder
		<LVEntryVersion, NoSuchLVEntryVersionException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the lv entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of lv entry versions
	 * @param end the upper bound of the range of lv entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entry versions
	 */
	@Override
	public List<LVEntryVersion> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LVEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first lv entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry version
	 * @throws NoSuchLVEntryVersionException if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion findByUuid_First(
			String uuid, OrderByComparator<LVEntryVersion> orderByComparator)
		throws NoSuchLVEntryVersionException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first lv entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry version, or <code>null</code> if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion fetchByUuid_First(
		String uuid, OrderByComparator<LVEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the lv entry versions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of lv entry versions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching lv entry versions
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<LVEntryVersion, NoSuchLVEntryVersionException>
			_collectionPersistenceFinderByUuid_Version;

	/**
	 * Returns an ordered range of all the lv entry versions where uuid = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param start the lower bound of the range of lv entry versions
	 * @param end the upper bound of the range of lv entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entry versions
	 */
	@Override
	public List<LVEntryVersion> findByUuid_Version(
		String uuid, int version, int start, int end,
		OrderByComparator<LVEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_Version.find(
			finderCache, new Object[] {uuid, version}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lv entry version in the ordered set where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry version
	 * @throws NoSuchLVEntryVersionException if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion findByUuid_Version_First(
			String uuid, int version,
			OrderByComparator<LVEntryVersion> orderByComparator)
		throws NoSuchLVEntryVersionException {

		return _collectionPersistenceFinderByUuid_Version.findFirst(
			finderCache, new Object[] {uuid, version}, orderByComparator);
	}

	/**
	 * Returns the first lv entry version in the ordered set where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry version, or <code>null</code> if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion fetchByUuid_Version_First(
		String uuid, int version,
		OrderByComparator<LVEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByUuid_Version.fetchFirst(
			finderCache, new Object[] {uuid, version}, orderByComparator);
	}

	/**
	 * Removes all the lv entry versions where uuid = &#63; and version = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 */
	@Override
	public void removeByUuid_Version(String uuid, int version) {
		_collectionPersistenceFinderByUuid_Version.remove(
			finderCache, new Object[] {uuid, version});
	}

	/**
	 * Returns the number of lv entry versions where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @return the number of matching lv entry versions
	 */
	@Override
	public int countByUuid_Version(String uuid, int version) {
		return _collectionPersistenceFinderByUuid_Version.count(
			finderCache, new Object[] {uuid, version});
	}

	private CollectionPersistenceFinder
		<LVEntryVersion, NoSuchLVEntryVersionException>
			_collectionPersistenceFinderByUUID_G;

	/**
	 * Returns an ordered range of all the lv entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param start the lower bound of the range of lv entry versions
	 * @param end the upper bound of the range of lv entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entry versions
	 */
	@Override
	public List<LVEntryVersion> findByUUID_G(
		String uuid, long groupId, int start, int end,
		OrderByComparator<LVEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lv entry version in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry version
	 * @throws NoSuchLVEntryVersionException if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion findByUUID_G_First(
			String uuid, long groupId,
			OrderByComparator<LVEntryVersion> orderByComparator)
		throws NoSuchLVEntryVersionException {

		return _collectionPersistenceFinderByUUID_G.findFirst(
			finderCache, new Object[] {uuid, groupId}, orderByComparator);
	}

	/**
	 * Returns the first lv entry version in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry version, or <code>null</code> if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion fetchByUUID_G_First(
		String uuid, long groupId,
		OrderByComparator<LVEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByUUID_G.fetchFirst(
			finderCache, new Object[] {uuid, groupId}, orderByComparator);
	}

	/**
	 * Removes all the lv entry versions where uuid = &#63; and groupId = &#63; from the database.
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
	 * Returns the number of lv entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching lv entry versions
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _collectionPersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private UniquePersistenceFinder
		<LVEntryVersion, NoSuchLVEntryVersionException>
			_uniquePersistenceFinderByUUID_G_Version;

	/**
	 * Returns the lv entry version where uuid = &#63; and groupId = &#63; and version = &#63; or throws a <code>NoSuchLVEntryVersionException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the matching lv entry version
	 * @throws NoSuchLVEntryVersionException if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion findByUUID_G_Version(
			String uuid, long groupId, int version)
		throws NoSuchLVEntryVersionException {

		return _uniquePersistenceFinderByUUID_G_Version.find(
			finderCache, new Object[] {uuid, groupId, version});
	}

	/**
	 * Returns the lv entry version where uuid = &#63; and groupId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching lv entry version, or <code>null</code> if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion fetchByUUID_G_Version(
		String uuid, long groupId, int version, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G_Version.fetch(
			finderCache, new Object[] {uuid, groupId, version}, useFinderCache);
	}

	/**
	 * Removes the lv entry version where uuid = &#63; and groupId = &#63; and version = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the lv entry version that was removed
	 */
	@Override
	public LVEntryVersion removeByUUID_G_Version(
			String uuid, long groupId, int version)
		throws NoSuchLVEntryVersionException {

		LVEntryVersion lvEntryVersion = findByUUID_G_Version(
			uuid, groupId, version);

		return remove(lvEntryVersion);
	}

	/**
	 * Returns the number of lv entry versions where uuid = &#63; and groupId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the number of matching lv entry versions
	 */
	@Override
	public int countByUUID_G_Version(String uuid, long groupId, int version) {
		return _uniquePersistenceFinderByUUID_G_Version.count(
			finderCache, new Object[] {uuid, groupId, version});
	}

	private CollectionPersistenceFinder
		<LVEntryVersion, NoSuchLVEntryVersionException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the lv entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of lv entry versions
	 * @param end the upper bound of the range of lv entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entry versions
	 */
	@Override
	public List<LVEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LVEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lv entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry version
	 * @throws NoSuchLVEntryVersionException if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LVEntryVersion> orderByComparator)
		throws NoSuchLVEntryVersionException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first lv entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry version, or <code>null</code> if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LVEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the lv entry versions where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of lv entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching lv entry versions
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<LVEntryVersion, NoSuchLVEntryVersionException>
			_collectionPersistenceFinderByUuid_C_Version;

	/**
	 * Returns an ordered range of all the lv entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param start the lower bound of the range of lv entry versions
	 * @param end the upper bound of the range of lv entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entry versions
	 */
	@Override
	public List<LVEntryVersion> findByUuid_C_Version(
		String uuid, long companyId, int version, int start, int end,
		OrderByComparator<LVEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C_Version.find(
			finderCache, new Object[] {uuid, companyId, version}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lv entry version in the ordered set where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry version
	 * @throws NoSuchLVEntryVersionException if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion findByUuid_C_Version_First(
			String uuid, long companyId, int version,
			OrderByComparator<LVEntryVersion> orderByComparator)
		throws NoSuchLVEntryVersionException {

		return _collectionPersistenceFinderByUuid_C_Version.findFirst(
			finderCache, new Object[] {uuid, companyId, version},
			orderByComparator);
	}

	/**
	 * Returns the first lv entry version in the ordered set where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry version, or <code>null</code> if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion fetchByUuid_C_Version_First(
		String uuid, long companyId, int version,
		OrderByComparator<LVEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C_Version.fetchFirst(
			finderCache, new Object[] {uuid, companyId, version},
			orderByComparator);
	}

	/**
	 * Removes all the lv entry versions where uuid = &#63; and companyId = &#63; and version = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 */
	@Override
	public void removeByUuid_C_Version(
		String uuid, long companyId, int version) {

		_collectionPersistenceFinderByUuid_C_Version.remove(
			finderCache, new Object[] {uuid, companyId, version});
	}

	/**
	 * Returns the number of lv entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @return the number of matching lv entry versions
	 */
	@Override
	public int countByUuid_C_Version(String uuid, long companyId, int version) {
		return _collectionPersistenceFinderByUuid_C_Version.count(
			finderCache, new Object[] {uuid, companyId, version});
	}

	private CollectionPersistenceFinder
		<LVEntryVersion, NoSuchLVEntryVersionException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the lv entry versions where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of lv entry versions
	 * @param end the upper bound of the range of lv entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entry versions
	 */
	@Override
	public List<LVEntryVersion> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LVEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first lv entry version in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry version
	 * @throws NoSuchLVEntryVersionException if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion findByGroupId_First(
			long groupId, OrderByComparator<LVEntryVersion> orderByComparator)
		throws NoSuchLVEntryVersionException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first lv entry version in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry version, or <code>null</code> if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion fetchByGroupId_First(
		long groupId, OrderByComparator<LVEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the lv entry versions where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of lv entry versions where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching lv entry versions
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<LVEntryVersion, NoSuchLVEntryVersionException>
			_collectionPersistenceFinderByGroupId_Version;

	/**
	 * Returns an ordered range of all the lv entry versions where groupId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param start the lower bound of the range of lv entry versions
	 * @param end the upper bound of the range of lv entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entry versions
	 */
	@Override
	public List<LVEntryVersion> findByGroupId_Version(
		long groupId, int version, int start, int end,
		OrderByComparator<LVEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId_Version.find(
			finderCache, new Object[] {groupId, version}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lv entry version in the ordered set where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry version
	 * @throws NoSuchLVEntryVersionException if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion findByGroupId_Version_First(
			long groupId, int version,
			OrderByComparator<LVEntryVersion> orderByComparator)
		throws NoSuchLVEntryVersionException {

		return _collectionPersistenceFinderByGroupId_Version.findFirst(
			finderCache, new Object[] {groupId, version}, orderByComparator);
	}

	/**
	 * Returns the first lv entry version in the ordered set where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry version, or <code>null</code> if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion fetchByGroupId_Version_First(
		long groupId, int version,
		OrderByComparator<LVEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByGroupId_Version.fetchFirst(
			finderCache, new Object[] {groupId, version}, orderByComparator);
	}

	/**
	 * Removes all the lv entry versions where groupId = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 */
	@Override
	public void removeByGroupId_Version(long groupId, int version) {
		_collectionPersistenceFinderByGroupId_Version.remove(
			finderCache, new Object[] {groupId, version});
	}

	/**
	 * Returns the number of lv entry versions where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @return the number of matching lv entry versions
	 */
	@Override
	public int countByGroupId_Version(long groupId, int version) {
		return _collectionPersistenceFinderByGroupId_Version.count(
			finderCache, new Object[] {groupId, version});
	}

	private CollectionPersistenceFinder
		<LVEntryVersion, NoSuchLVEntryVersionException>
			_collectionPersistenceFinderByG_UGK;

	/**
	 * Returns an ordered range of all the lv entry versions where groupId = &#63; and uniqueGroupKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param uniqueGroupKey the unique group key
	 * @param start the lower bound of the range of lv entry versions
	 * @param end the upper bound of the range of lv entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entry versions
	 */
	@Override
	public List<LVEntryVersion> findByG_UGK(
		long groupId, String uniqueGroupKey, int start, int end,
		OrderByComparator<LVEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_UGK.find(
			finderCache, new Object[] {groupId, uniqueGroupKey}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lv entry version in the ordered set where groupId = &#63; and uniqueGroupKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param uniqueGroupKey the unique group key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry version
	 * @throws NoSuchLVEntryVersionException if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion findByG_UGK_First(
			long groupId, String uniqueGroupKey,
			OrderByComparator<LVEntryVersion> orderByComparator)
		throws NoSuchLVEntryVersionException {

		return _collectionPersistenceFinderByG_UGK.findFirst(
			finderCache, new Object[] {groupId, uniqueGroupKey},
			orderByComparator);
	}

	/**
	 * Returns the first lv entry version in the ordered set where groupId = &#63; and uniqueGroupKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param uniqueGroupKey the unique group key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry version, or <code>null</code> if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion fetchByG_UGK_First(
		long groupId, String uniqueGroupKey,
		OrderByComparator<LVEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_UGK.fetchFirst(
			finderCache, new Object[] {groupId, uniqueGroupKey},
			orderByComparator);
	}

	/**
	 * Removes all the lv entry versions where groupId = &#63; and uniqueGroupKey = &#63; from the database.
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
	 * Returns the number of lv entry versions where groupId = &#63; and uniqueGroupKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param uniqueGroupKey the unique group key
	 * @return the number of matching lv entry versions
	 */
	@Override
	public int countByG_UGK(long groupId, String uniqueGroupKey) {
		return _collectionPersistenceFinderByG_UGK.count(
			finderCache, new Object[] {groupId, uniqueGroupKey});
	}

	private UniquePersistenceFinder
		<LVEntryVersion, NoSuchLVEntryVersionException>
			_uniquePersistenceFinderByG_UGK_Version;

	/**
	 * Returns the lv entry version where groupId = &#63; and uniqueGroupKey = &#63; and version = &#63; or throws a <code>NoSuchLVEntryVersionException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param uniqueGroupKey the unique group key
	 * @param version the version
	 * @return the matching lv entry version
	 * @throws NoSuchLVEntryVersionException if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion findByG_UGK_Version(
			long groupId, String uniqueGroupKey, int version)
		throws NoSuchLVEntryVersionException {

		return _uniquePersistenceFinderByG_UGK_Version.find(
			finderCache, new Object[] {groupId, uniqueGroupKey, version});
	}

	/**
	 * Returns the lv entry version where groupId = &#63; and uniqueGroupKey = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param uniqueGroupKey the unique group key
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching lv entry version, or <code>null</code> if a matching lv entry version could not be found
	 */
	@Override
	public LVEntryVersion fetchByG_UGK_Version(
		long groupId, String uniqueGroupKey, int version,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_UGK_Version.fetch(
			finderCache, new Object[] {groupId, uniqueGroupKey, version},
			useFinderCache);
	}

	/**
	 * Removes the lv entry version where groupId = &#63; and uniqueGroupKey = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param uniqueGroupKey the unique group key
	 * @param version the version
	 * @return the lv entry version that was removed
	 */
	@Override
	public LVEntryVersion removeByG_UGK_Version(
			long groupId, String uniqueGroupKey, int version)
		throws NoSuchLVEntryVersionException {

		LVEntryVersion lvEntryVersion = findByG_UGK_Version(
			groupId, uniqueGroupKey, version);

		return remove(lvEntryVersion);
	}

	/**
	 * Returns the number of lv entry versions where groupId = &#63; and uniqueGroupKey = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param uniqueGroupKey the unique group key
	 * @param version the version
	 * @return the number of matching lv entry versions
	 */
	@Override
	public int countByG_UGK_Version(
		long groupId, String uniqueGroupKey, int version) {

		return _uniquePersistenceFinderByG_UGK_Version.count(
			finderCache, new Object[] {groupId, uniqueGroupKey, version});
	}

	public LVEntryVersionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LVEntryVersion.class);

		setModelImplClass(LVEntryVersionImpl.class);
		setModelPKClass(long.class);

		setTable(LVEntryVersionTable.INSTANCE);
	}

	/**
	 * Creates a new lv entry version with the primary key. Does not add the lv entry version to the database.
	 *
	 * @param lvEntryVersionId the primary key for the new lv entry version
	 * @return the new lv entry version
	 */
	@Override
	public LVEntryVersion create(long lvEntryVersionId) {
		LVEntryVersion lvEntryVersion = new LVEntryVersionImpl();

		lvEntryVersion.setNew(true);
		lvEntryVersion.setPrimaryKey(lvEntryVersionId);

		lvEntryVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return lvEntryVersion;
	}

	/**
	 * Removes the lv entry version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param lvEntryVersionId the primary key of the lv entry version
	 * @return the lv entry version that was removed
	 * @throws NoSuchLVEntryVersionException if a lv entry version with the primary key could not be found
	 */
	@Override
	public LVEntryVersion remove(long lvEntryVersionId)
		throws NoSuchLVEntryVersionException {

		return remove((Serializable)lvEntryVersionId);
	}

	@Override
	protected LVEntryVersion removeImpl(LVEntryVersion lvEntryVersion) {
		lvEntryVersionToBigDecimalEntryTableMapper.
			deleteLeftPrimaryKeyTableMappings(lvEntryVersion.getPrimaryKey());

		lvEntryLocalizationVersionPersistence.removeByLvEntryId_Version(
			lvEntryVersion.getVersionedModelId(), lvEntryVersion.getVersion());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(lvEntryVersion)) {
				lvEntryVersion = (LVEntryVersion)session.get(
					LVEntryVersionImpl.class,
					lvEntryVersion.getPrimaryKeyObj());
			}

			if (lvEntryVersion != null) {
				session.delete(lvEntryVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (lvEntryVersion != null) {
			clearCache(lvEntryVersion);
		}

		return lvEntryVersion;
	}

	@Override
	public LVEntryVersion updateImpl(LVEntryVersion lvEntryVersion) {
		boolean isNew = lvEntryVersion.isNew();

		if (!(lvEntryVersion instanceof LVEntryVersionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(lvEntryVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					lvEntryVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in lvEntryVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LVEntryVersion implementation " +
					lvEntryVersion.getClass());
		}

		LVEntryVersionModelImpl lvEntryVersionModelImpl =
			(LVEntryVersionModelImpl)lvEntryVersion;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(lvEntryVersion);
			}
			else {
				throw new IllegalArgumentException(
					"LVEntryVersion is read only, create a new version instead");
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(lvEntryVersion, false);

		if (isNew) {
			lvEntryVersion.setNew(false);
		}

		lvEntryVersion.resetOriginalValues();

		return lvEntryVersion;
	}

	/**
	 * Returns the lv entry version with the primary key or throws a <code>NoSuchLVEntryVersionException</code> if it could not be found.
	 *
	 * @param lvEntryVersionId the primary key of the lv entry version
	 * @return the lv entry version
	 * @throws NoSuchLVEntryVersionException if a lv entry version with the primary key could not be found
	 */
	@Override
	public LVEntryVersion findByPrimaryKey(long lvEntryVersionId)
		throws NoSuchLVEntryVersionException {

		return findByPrimaryKey((Serializable)lvEntryVersionId);
	}

	/**
	 * Returns the lv entry version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param lvEntryVersionId the primary key of the lv entry version
	 * @return the lv entry version, or <code>null</code> if a lv entry version with the primary key could not be found
	 */
	@Override
	public LVEntryVersion fetchByPrimaryKey(long lvEntryVersionId) {
		return fetchByPrimaryKey((Serializable)lvEntryVersionId);
	}

	/**
	 * Returns the primaryKeys of big decimal entries associated with the lv entry version.
	 *
	 * @param pk the primary key of the lv entry version
	 * @return long[] of the primaryKeys of big decimal entries associated with the lv entry version
	 */
	@Override
	public long[] getBigDecimalEntryPrimaryKeys(long pk) {
		long[] pks =
			lvEntryVersionToBigDecimalEntryTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the big decimal entries associated with the lv entry version.
	 *
	 * @param pk the primary key of the lv entry version
	 * @return the big decimal entries associated with the lv entry version
	 */
	@Override
	public List
		<com.liferay.portal.tools.service.builder.test.model.BigDecimalEntry>
			getBigDecimalEntries(long pk) {

		return getBigDecimalEntries(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the big decimal entries associated with the lv entry version.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the lv entry version
	 * @param start the lower bound of the range of lv entry versions
	 * @param end the upper bound of the range of lv entry versions (not inclusive)
	 * @return the range of big decimal entries associated with the lv entry version
	 */
	@Override
	public List
		<com.liferay.portal.tools.service.builder.test.model.BigDecimalEntry>
			getBigDecimalEntries(long pk, int start, int end) {

		return getBigDecimalEntries(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the big decimal entries associated with the lv entry version.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the lv entry version
	 * @param start the lower bound of the range of lv entry versions
	 * @param end the upper bound of the range of lv entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of big decimal entries associated with the lv entry version
	 */
	@Override
	public List
		<com.liferay.portal.tools.service.builder.test.model.BigDecimalEntry>
			getBigDecimalEntries(
				long pk, int start, int end,
				OrderByComparator
					<com.liferay.portal.tools.service.builder.test.model.
						BigDecimalEntry> orderByComparator) {

		return lvEntryVersionToBigDecimalEntryTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of big decimal entries associated with the lv entry version.
	 *
	 * @param pk the primary key of the lv entry version
	 * @return the number of big decimal entries associated with the lv entry version
	 */
	@Override
	public int getBigDecimalEntriesSize(long pk) {
		long[] pks =
			lvEntryVersionToBigDecimalEntryTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the big decimal entry is associated with the lv entry version.
	 *
	 * @param pk the primary key of the lv entry version
	 * @param bigDecimalEntryPK the primary key of the big decimal entry
	 * @return <code>true</code> if the big decimal entry is associated with the lv entry version; <code>false</code> otherwise
	 */
	@Override
	public boolean containsBigDecimalEntry(long pk, long bigDecimalEntryPK) {
		return lvEntryVersionToBigDecimalEntryTableMapper.containsTableMapping(
			pk, bigDecimalEntryPK);
	}

	/**
	 * Returns <code>true</code> if the lv entry version has any big decimal entries associated with it.
	 *
	 * @param pk the primary key of the lv entry version to check for associations with big decimal entries
	 * @return <code>true</code> if the lv entry version has any big decimal entries associated with it; <code>false</code> otherwise
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
	 * Adds an association between the lv entry version and the big decimal entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry version
	 * @param bigDecimalEntryPK the primary key of the big decimal entry
	 * @return <code>true</code> if an association between the lv entry version and the big decimal entry was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addBigDecimalEntry(long pk, long bigDecimalEntryPK) {
		LVEntryVersion lvEntryVersion = fetchByPrimaryKey(pk);

		if (lvEntryVersion == null) {
			return lvEntryVersionToBigDecimalEntryTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, bigDecimalEntryPK);
		}
		else {
			return lvEntryVersionToBigDecimalEntryTableMapper.addTableMapping(
				lvEntryVersion.getCompanyId(), pk, bigDecimalEntryPK);
		}
	}

	/**
	 * Adds an association between the lv entry version and the big decimal entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry version
	 * @param bigDecimalEntry the big decimal entry
	 * @return <code>true</code> if an association between the lv entry version and the big decimal entry was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addBigDecimalEntry(
		long pk,
		com.liferay.portal.tools.service.builder.test.model.BigDecimalEntry
			bigDecimalEntry) {

		LVEntryVersion lvEntryVersion = fetchByPrimaryKey(pk);

		if (lvEntryVersion == null) {
			return lvEntryVersionToBigDecimalEntryTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				bigDecimalEntry.getPrimaryKey());
		}
		else {
			return lvEntryVersionToBigDecimalEntryTableMapper.addTableMapping(
				lvEntryVersion.getCompanyId(), pk,
				bigDecimalEntry.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the lv entry version and the big decimal entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry version
	 * @param bigDecimalEntryPKs the primary keys of the big decimal entries
	 * @return <code>true</code> if at least one association between the lv entry version and the big decimal entries was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addBigDecimalEntries(long pk, long[] bigDecimalEntryPKs) {
		long companyId = 0;

		LVEntryVersion lvEntryVersion = fetchByPrimaryKey(pk);

		if (lvEntryVersion == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = lvEntryVersion.getCompanyId();
		}

		long[] addedKeys =
			lvEntryVersionToBigDecimalEntryTableMapper.addTableMappings(
				companyId, pk, bigDecimalEntryPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the lv entry version and the big decimal entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry version
	 * @param bigDecimalEntries the big decimal entries
	 * @return <code>true</code> if at least one association between the lv entry version and the big decimal entries was added; <code>false</code> if they were all already associated
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
	 * Clears all associations between the lv entry version and its big decimal entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry version to clear the associated big decimal entries from
	 */
	@Override
	public void clearBigDecimalEntries(long pk) {
		lvEntryVersionToBigDecimalEntryTableMapper.
			deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the lv entry version and the big decimal entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry version
	 * @param bigDecimalEntryPK the primary key of the big decimal entry
	 */
	@Override
	public void removeBigDecimalEntry(long pk, long bigDecimalEntryPK) {
		lvEntryVersionToBigDecimalEntryTableMapper.deleteTableMapping(
			pk, bigDecimalEntryPK);
	}

	/**
	 * Removes the association between the lv entry version and the big decimal entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry version
	 * @param bigDecimalEntry the big decimal entry
	 */
	@Override
	public void removeBigDecimalEntry(
		long pk,
		com.liferay.portal.tools.service.builder.test.model.BigDecimalEntry
			bigDecimalEntry) {

		lvEntryVersionToBigDecimalEntryTableMapper.deleteTableMapping(
			pk, bigDecimalEntry.getPrimaryKey());
	}

	/**
	 * Removes the association between the lv entry version and the big decimal entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry version
	 * @param bigDecimalEntryPKs the primary keys of the big decimal entries
	 */
	@Override
	public void removeBigDecimalEntries(long pk, long[] bigDecimalEntryPKs) {
		lvEntryVersionToBigDecimalEntryTableMapper.deleteTableMappings(
			pk, bigDecimalEntryPKs);
	}

	/**
	 * Removes the association between the lv entry version and the big decimal entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry version
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
	 * Sets the big decimal entries associated with the lv entry version, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry version
	 * @param bigDecimalEntryPKs the primary keys of the big decimal entries to be associated with the lv entry version
	 */
	@Override
	public void setBigDecimalEntries(long pk, long[] bigDecimalEntryPKs) {
		Set<Long> newBigDecimalEntryPKsSet = SetUtil.fromArray(
			bigDecimalEntryPKs);
		Set<Long> oldBigDecimalEntryPKsSet = SetUtil.fromArray(
			lvEntryVersionToBigDecimalEntryTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeBigDecimalEntryPKsSet = new HashSet<Long>(
			oldBigDecimalEntryPKsSet);

		removeBigDecimalEntryPKsSet.removeAll(newBigDecimalEntryPKsSet);

		lvEntryVersionToBigDecimalEntryTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeBigDecimalEntryPKsSet));

		newBigDecimalEntryPKsSet.removeAll(oldBigDecimalEntryPKsSet);

		long companyId = 0;

		LVEntryVersion lvEntryVersion = fetchByPrimaryKey(pk);

		if (lvEntryVersion == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = lvEntryVersion.getCompanyId();
		}

		lvEntryVersionToBigDecimalEntryTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newBigDecimalEntryPKsSet));
	}

	/**
	 * Sets the big decimal entries associated with the lv entry version, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the lv entry version
	 * @param bigDecimalEntries the big decimal entries to be associated with the lv entry version
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
		return "lvEntryVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LVENTRYVERSION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return LVEntryVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the lv entry version persistence.
	 */
	public void afterPropertiesSet() {
		lvEntryVersionToBigDecimalEntryTableMapper =
			TableMapperFactory.getTableMapper(
				"BigDecimalEntries_LVEntries", "companyId", "lvEntryVersionId",
				"bigDecimalEntryId", this, bigDecimalEntryPersistence);

		_collectionPersistenceFinderByLvEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLvEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"lvEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLvEntryId", new String[] {Long.class.getName()},
					new String[] {"lvEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLvEntryId", new String[] {Long.class.getName()},
					new String[] {"lvEntryId"}, false),
				_SQL_SELECT_LVENTRYVERSION_WHERE,
				_SQL_COUNT_LVENTRYVERSION_WHERE,
				LVEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"lvEntryVersion.", "lvEntryId", FinderColumn.Type.LONG, "=",
					true, true, LVEntryVersion::getLvEntryId));

		_uniquePersistenceFinderByLvEntryId_Version =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByLvEntryId_Version",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"lvEntryId", "version"}, 0, 0, false,
					LVEntryVersion::getLvEntryId, LVEntryVersion::getVersion),
				_SQL_SELECT_LVENTRYVERSION_WHERE, "",
				new FinderColumn<>(
					"lvEntryVersion.", "lvEntryId", FinderColumn.Type.LONG, "=",
					true, true, LVEntryVersion::getLvEntryId),
				new FinderColumn<>(
					"lvEntryVersion.", "version", FinderColumn.Type.INTEGER,
					"=", true, true, LVEntryVersion::getVersion));

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
			_SQL_SELECT_LVENTRYVERSION_WHERE, _SQL_COUNT_LVENTRYVERSION_WHERE,
			LVEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"lvEntryVersion.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, LVEntryVersion::getUuid));

		_collectionPersistenceFinderByUuid_Version =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByUuid_Version",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "version"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByUuid_Version",
					new String[] {
						String.class.getName(), Integer.class.getName()
					},
					new String[] {"uuid_", "version"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByUuid_Version",
					new String[] {
						String.class.getName(), Integer.class.getName()
					},
					new String[] {"uuid_", "version"}, 0, 1, false, null),
				_SQL_SELECT_LVENTRYVERSION_WHERE,
				_SQL_COUNT_LVENTRYVERSION_WHERE,
				LVEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"lvEntryVersion.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					LVEntryVersion::getUuid),
				new FinderColumn<>(
					"lvEntryVersion.", "version", FinderColumn.Type.INTEGER,
					"=", true, true, LVEntryVersion::getVersion));

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
				_SQL_SELECT_LVENTRYVERSION_WHERE,
				_SQL_COUNT_LVENTRYVERSION_WHERE,
				LVEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"lvEntryVersion.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					LVEntryVersion::getUuid),
				new FinderColumn<>(
					"lvEntryVersion.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, LVEntryVersion::getGroupId));

		_uniquePersistenceFinderByUUID_G_Version =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G_Version",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"uuid_", "groupId", "version"}, 0, 1, false,
					convertNullFunction(LVEntryVersion::getUuid),
					LVEntryVersion::getGroupId, LVEntryVersion::getVersion),
				_SQL_SELECT_LVENTRYVERSION_WHERE, "",
				new FinderColumn<>(
					"lvEntryVersion.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					LVEntryVersion::getUuid),
				new FinderColumn<>(
					"lvEntryVersion.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, LVEntryVersion::getGroupId),
				new FinderColumn<>(
					"lvEntryVersion.", "version", FinderColumn.Type.INTEGER,
					"=", true, true, LVEntryVersion::getVersion));

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
				_SQL_SELECT_LVENTRYVERSION_WHERE,
				_SQL_COUNT_LVENTRYVERSION_WHERE,
				LVEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"lvEntryVersion.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					LVEntryVersion::getUuid),
				new FinderColumn<>(
					"lvEntryVersion.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, LVEntryVersion::getCompanyId));

		_collectionPersistenceFinderByUuid_C_Version =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByUuid_C_Version",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId", "version"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByUuid_C_Version",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"uuid_", "companyId", "version"}, 0, 1, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByUuid_C_Version",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"uuid_", "companyId", "version"}, 0, 1, false,
					null),
				_SQL_SELECT_LVENTRYVERSION_WHERE,
				_SQL_COUNT_LVENTRYVERSION_WHERE,
				LVEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"lvEntryVersion.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					LVEntryVersion::getUuid),
				new FinderColumn<>(
					"lvEntryVersion.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, LVEntryVersion::getCompanyId),
				new FinderColumn<>(
					"lvEntryVersion.", "version", FinderColumn.Type.INTEGER,
					"=", true, true, LVEntryVersion::getVersion));

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
				_SQL_SELECT_LVENTRYVERSION_WHERE,
				_SQL_COUNT_LVENTRYVERSION_WHERE,
				LVEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"lvEntryVersion.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, LVEntryVersion::getGroupId));

		_collectionPersistenceFinderByGroupId_Version =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByGroupId_Version",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "version"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByGroupId_Version",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "version"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByGroupId_Version",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "version"}, false),
				_SQL_SELECT_LVENTRYVERSION_WHERE,
				_SQL_COUNT_LVENTRYVERSION_WHERE,
				LVEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"lvEntryVersion.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, LVEntryVersion::getGroupId),
				new FinderColumn<>(
					"lvEntryVersion.", "version", FinderColumn.Type.INTEGER,
					"=", true, true, LVEntryVersion::getVersion));

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
			_SQL_SELECT_LVENTRYVERSION_WHERE, _SQL_COUNT_LVENTRYVERSION_WHERE,
			LVEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"lvEntryVersion.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, LVEntryVersion::getGroupId),
			new FinderColumn<>(
				"lvEntryVersion.", "uniqueGroupKey", FinderColumn.Type.STRING,
				"=", true, true, LVEntryVersion::getUniqueGroupKey));

		_uniquePersistenceFinderByG_UGK_Version = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_UGK_Version",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName()
				},
				new String[] {"groupId", "uniqueGroupKey", "version"}, 0, 2,
				false, LVEntryVersion::getGroupId,
				convertNullFunction(LVEntryVersion::getUniqueGroupKey),
				LVEntryVersion::getVersion),
			_SQL_SELECT_LVENTRYVERSION_WHERE, "",
			new FinderColumn<>(
				"lvEntryVersion.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, LVEntryVersion::getGroupId),
			new FinderColumn<>(
				"lvEntryVersion.", "uniqueGroupKey", FinderColumn.Type.STRING,
				"=", true, true, LVEntryVersion::getUniqueGroupKey),
			new FinderColumn<>(
				"lvEntryVersion.", "version", FinderColumn.Type.INTEGER, "=",
				true, true, LVEntryVersion::getVersion));

		LVEntryVersionUtil.setPersistence(this);
	}

	public void destroy() {
		LVEntryVersionUtil.setPersistence(null);

		entityCache.removeCache(LVEntryVersionImpl.class.getName());

		TableMapperFactory.removeTableMapper("BigDecimalEntries_LVEntries");
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	@BeanReference(type = BigDecimalEntryPersistence.class)
	protected BigDecimalEntryPersistence bigDecimalEntryPersistence;

	protected TableMapper
		<LVEntryVersion,
		 com.liferay.portal.tools.service.builder.test.model.BigDecimalEntry>
			lvEntryVersionToBigDecimalEntryTableMapper;

	@BeanReference(type = LVEntryLocalizationVersionPersistence.class)
	protected LVEntryLocalizationVersionPersistence
		lvEntryLocalizationVersionPersistence;

	private static final String _ENTITY_ALIAS_PREFIX =
		LVEntryVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LVENTRYVERSION =
		"SELECT lvEntryVersion FROM LVEntryVersion lvEntryVersion";

	private static final String _SQL_SELECT_LVENTRYVERSION_WHERE =
		"SELECT lvEntryVersion FROM LVEntryVersion lvEntryVersion WHERE ";

	private static final String _SQL_COUNT_LVENTRYVERSION_WHERE =
		"SELECT COUNT(lvEntryVersion) FROM LVEntryVersion lvEntryVersion WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1167978348