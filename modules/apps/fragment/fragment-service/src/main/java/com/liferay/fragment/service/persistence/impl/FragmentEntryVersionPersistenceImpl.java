/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.persistence.impl;

import com.liferay.fragment.exception.NoSuchEntryVersionException;
import com.liferay.fragment.model.FragmentEntryVersion;
import com.liferay.fragment.model.FragmentEntryVersionTable;
import com.liferay.fragment.model.impl.FragmentEntryVersionImpl;
import com.liferay.fragment.model.impl.FragmentEntryVersionModelImpl;
import com.liferay.fragment.service.persistence.FragmentEntryVersionPersistence;
import com.liferay.fragment.service.persistence.FragmentEntryVersionUtil;
import com.liferay.fragment.service.persistence.impl.constants.FragmentPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
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
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
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
 * The persistence implementation for the fragment entry version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = FragmentEntryVersionPersistence.class)
public class FragmentEntryVersionPersistenceImpl
	extends BasePersistenceImpl
		<FragmentEntryVersion, NoSuchEntryVersionException>
	implements FragmentEntryVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>FragmentEntryVersionUtil</code> to access the fragment entry version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		FragmentEntryVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByFragmentEntryId;

	/**
	 * Returns an ordered range of all the fragment entry versions where fragmentEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByFragmentEntryId(
		long fragmentEntryId, int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByFragmentEntryId.find(
			finderCache, new Object[] {fragmentEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where fragmentEntryId = &#63;.
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByFragmentEntryId_First(
			long fragmentEntryId,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByFragmentEntryId.findFirst(
			finderCache, new Object[] {fragmentEntryId}, orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where fragmentEntryId = &#63;.
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByFragmentEntryId_First(
		long fragmentEntryId,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByFragmentEntryId.fetchFirst(
			finderCache, new Object[] {fragmentEntryId}, orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where fragmentEntryId = &#63; from the database.
	 *
	 * @param fragmentEntryId the fragment entry ID
	 */
	@Override
	public void removeByFragmentEntryId(long fragmentEntryId) {
		_collectionPersistenceFinderByFragmentEntryId.remove(
			finderCache, new Object[] {fragmentEntryId});
	}

	/**
	 * Returns the number of fragment entry versions where fragmentEntryId = &#63;.
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByFragmentEntryId(long fragmentEntryId) {
		return _collectionPersistenceFinderByFragmentEntryId.count(
			finderCache, new Object[] {fragmentEntryId});
	}

	private UniquePersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_uniquePersistenceFinderByFragmentEntryId_Version;

	/**
	 * Returns the fragment entry version where fragmentEntryId = &#63; and version = &#63; or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @param version the version
	 * @return the matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByFragmentEntryId_Version(
			long fragmentEntryId, int version)
		throws NoSuchEntryVersionException {

		return _uniquePersistenceFinderByFragmentEntryId_Version.find(
			finderCache, new Object[] {fragmentEntryId, version});
	}

	/**
	 * Returns the fragment entry version where fragmentEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByFragmentEntryId_Version(
		long fragmentEntryId, int version, boolean useFinderCache) {

		return _uniquePersistenceFinderByFragmentEntryId_Version.fetch(
			finderCache, new Object[] {fragmentEntryId, version},
			useFinderCache);
	}

	/**
	 * Removes the fragment entry version where fragmentEntryId = &#63; and version = &#63; from the database.
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @param version the version
	 * @return the fragment entry version that was removed
	 */
	@Override
	public FragmentEntryVersion removeByFragmentEntryId_Version(
			long fragmentEntryId, int version)
		throws NoSuchEntryVersionException {

		FragmentEntryVersion fragmentEntryVersion =
			findByFragmentEntryId_Version(fragmentEntryId, version);

		return remove(fragmentEntryVersion);
	}

	/**
	 * Returns the number of fragment entry versions where fragmentEntryId = &#63; and version = &#63;.
	 *
	 * @param fragmentEntryId the fragment entry ID
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByFragmentEntryId_Version(
		long fragmentEntryId, int version) {

		return _uniquePersistenceFinderByFragmentEntryId_Version.count(
			finderCache, new Object[] {fragmentEntryId, version});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the fragment entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByUuid_First(
			String uuid,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByUuid_First(
		String uuid,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of fragment entry versions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByUuid_Version;

	/**
	 * Returns an ordered range of all the fragment entry versions where uuid = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByUuid_Version(
		String uuid, int version, int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_Version.find(
			finderCache, new Object[] {uuid, version}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByUuid_Version_First(
			String uuid, int version,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByUuid_Version.findFirst(
			finderCache, new Object[] {uuid, version}, orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByUuid_Version_First(
		String uuid, int version,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByUuid_Version.fetchFirst(
			finderCache, new Object[] {uuid, version}, orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where uuid = &#63; and version = &#63; from the database.
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
	 * Returns the number of fragment entry versions where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByUuid_Version(String uuid, int version) {
		return _collectionPersistenceFinderByUuid_Version.count(
			finderCache, new Object[] {uuid, version});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByUUID_G;

	/**
	 * Returns an ordered range of all the fragment entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByUUID_G(
		String uuid, long groupId, int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByUUID_G_First(
			String uuid, long groupId,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByUUID_G.findFirst(
			finderCache, new Object[] {uuid, groupId}, orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByUUID_G_First(
		String uuid, long groupId,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByUUID_G.fetchFirst(
			finderCache, new Object[] {uuid, groupId}, orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where uuid = &#63; and groupId = &#63; from the database.
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
	 * Returns the number of fragment entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _collectionPersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private UniquePersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_uniquePersistenceFinderByUUID_G_Version;

	/**
	 * Returns the fragment entry version where uuid = &#63; and groupId = &#63; and version = &#63; or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByUUID_G_Version(
			String uuid, long groupId, int version)
		throws NoSuchEntryVersionException {

		return _uniquePersistenceFinderByUUID_G_Version.find(
			finderCache, new Object[] {uuid, groupId, version});
	}

	/**
	 * Returns the fragment entry version where uuid = &#63; and groupId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByUUID_G_Version(
		String uuid, long groupId, int version, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G_Version.fetch(
			finderCache, new Object[] {uuid, groupId, version}, useFinderCache);
	}

	/**
	 * Removes the fragment entry version where uuid = &#63; and groupId = &#63; and version = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the fragment entry version that was removed
	 */
	@Override
	public FragmentEntryVersion removeByUUID_G_Version(
			String uuid, long groupId, int version)
		throws NoSuchEntryVersionException {

		FragmentEntryVersion fragmentEntryVersion = findByUUID_G_Version(
			uuid, groupId, version);

		return remove(fragmentEntryVersion);
	}

	/**
	 * Returns the number of fragment entry versions where uuid = &#63; and groupId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByUUID_G_Version(String uuid, long groupId, int version) {
		return _uniquePersistenceFinderByUUID_G_Version.count(
			finderCache, new Object[] {uuid, groupId, version});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the fragment entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of fragment entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByUuid_C_Version;

	/**
	 * Returns an ordered range of all the fragment entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByUuid_C_Version(
		String uuid, long companyId, int version, int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C_Version.find(
			finderCache, new Object[] {uuid, companyId, version}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByUuid_C_Version_First(
			String uuid, long companyId, int version,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByUuid_C_Version.findFirst(
			finderCache, new Object[] {uuid, companyId, version},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByUuid_C_Version_First(
		String uuid, long companyId, int version,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C_Version.fetchFirst(
			finderCache, new Object[] {uuid, companyId, version},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where uuid = &#63; and companyId = &#63; and version = &#63; from the database.
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
	 * Returns the number of fragment entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByUuid_C_Version(String uuid, long companyId, int version) {
		return _collectionPersistenceFinderByUuid_C_Version.count(
			finderCache, new Object[] {uuid, companyId, version});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByGroupId_First(
			long groupId,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByGroupId_First(
		long groupId,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of fragment entry versions where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByGroupId_Version;

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByGroupId_Version(
		long groupId, int version, int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId_Version.find(
			finderCache, new Object[] {groupId, version}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByGroupId_Version_First(
			long groupId, int version,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByGroupId_Version.findFirst(
			finderCache, new Object[] {groupId, version}, orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByGroupId_Version_First(
		long groupId, int version,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByGroupId_Version.fetchFirst(
			finderCache, new Object[] {groupId, version}, orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and version = &#63; from the database.
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
	 * Returns the number of fragment entry versions where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByGroupId_Version(long groupId, int version) {
		return _collectionPersistenceFinderByGroupId_Version.count(
			finderCache, new Object[] {groupId, version});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByFragmentCollectionId;

	/**
	 * Returns an ordered range of all the fragment entry versions where fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByFragmentCollectionId(
		long fragmentCollectionId, int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByFragmentCollectionId.find(
			finderCache, new Object[] {fragmentCollectionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where fragmentCollectionId = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByFragmentCollectionId_First(
			long fragmentCollectionId,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByFragmentCollectionId.findFirst(
			finderCache, new Object[] {fragmentCollectionId},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where fragmentCollectionId = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByFragmentCollectionId_First(
		long fragmentCollectionId,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByFragmentCollectionId.fetchFirst(
			finderCache, new Object[] {fragmentCollectionId},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where fragmentCollectionId = &#63; from the database.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 */
	@Override
	public void removeByFragmentCollectionId(long fragmentCollectionId) {
		_collectionPersistenceFinderByFragmentCollectionId.remove(
			finderCache, new Object[] {fragmentCollectionId});
	}

	/**
	 * Returns the number of fragment entry versions where fragmentCollectionId = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByFragmentCollectionId(long fragmentCollectionId) {
		return _collectionPersistenceFinderByFragmentCollectionId.count(
			finderCache, new Object[] {fragmentCollectionId});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByFragmentCollectionId_Version;

	/**
	 * Returns an ordered range of all the fragment entry versions where fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByFragmentCollectionId_Version(
		long fragmentCollectionId, int version, int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByFragmentCollectionId_Version.find(
			finderCache, new Object[] {fragmentCollectionId, version}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByFragmentCollectionId_Version_First(
			long fragmentCollectionId, int version,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByFragmentCollectionId_Version.
			findFirst(
				finderCache, new Object[] {fragmentCollectionId, version},
				orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByFragmentCollectionId_Version_First(
		long fragmentCollectionId, int version,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByFragmentCollectionId_Version.
			fetchFirst(
				finderCache, new Object[] {fragmentCollectionId, version},
				orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where fragmentCollectionId = &#63; and version = &#63; from the database.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 */
	@Override
	public void removeByFragmentCollectionId_Version(
		long fragmentCollectionId, int version) {

		_collectionPersistenceFinderByFragmentCollectionId_Version.remove(
			finderCache, new Object[] {fragmentCollectionId, version});
	}

	/**
	 * Returns the number of fragment entry versions where fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByFragmentCollectionId_Version(
		long fragmentCollectionId, int version) {

		return _collectionPersistenceFinderByFragmentCollectionId_Version.count(
			finderCache, new Object[] {fragmentCollectionId, version});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByType;

	/**
	 * Returns an ordered range of all the fragment entry versions where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByType(
		int type, int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByType.find(
			finderCache, new Object[] {type}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByType_First(
			int type, OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByType.findFirst(
			finderCache, new Object[] {type}, orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByType_First(
		int type, OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByType.fetchFirst(
			finderCache, new Object[] {type}, orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	@Override
	public void removeByType(int type) {
		_collectionPersistenceFinderByType.remove(
			finderCache, new Object[] {type});
	}

	/**
	 * Returns the number of fragment entry versions where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByType(int type) {
		return _collectionPersistenceFinderByType.count(
			finderCache, new Object[] {type});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByType_Version;

	/**
	 * Returns an ordered range of all the fragment entry versions where type = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByType_Version(
		int type, int version, int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByType_Version.find(
			finderCache, new Object[] {type, version}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where type = &#63; and version = &#63;.
	 *
	 * @param type the type
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByType_Version_First(
			int type, int version,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByType_Version.findFirst(
			finderCache, new Object[] {type, version}, orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where type = &#63; and version = &#63;.
	 *
	 * @param type the type
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByType_Version_First(
		int type, int version,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByType_Version.fetchFirst(
			finderCache, new Object[] {type, version}, orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where type = &#63; and version = &#63; from the database.
	 *
	 * @param type the type
	 * @param version the version
	 */
	@Override
	public void removeByType_Version(int type, int version) {
		_collectionPersistenceFinderByType_Version.remove(
			finderCache, new Object[] {type, version});
	}

	/**
	 * Returns the number of fragment entry versions where type = &#63; and version = &#63;.
	 *
	 * @param type the type
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByType_Version(int type, int version) {
		return _collectionPersistenceFinderByType_Version.count(
			finderCache, new Object[] {type, version});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_FCI;

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByG_FCI(
		long groupId, long fragmentCollectionId, int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_FCI.find(
			finderCache, new Object[] {groupId, fragmentCollectionId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByG_FCI_First(
			long groupId, long fragmentCollectionId,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_FCI.findFirst(
			finderCache, new Object[] {groupId, fragmentCollectionId},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByG_FCI_First(
		long groupId, long fragmentCollectionId,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_FCI.fetchFirst(
			finderCache, new Object[] {groupId, fragmentCollectionId},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 */
	@Override
	public void removeByG_FCI(long groupId, long fragmentCollectionId) {
		_collectionPersistenceFinderByG_FCI.remove(
			finderCache, new Object[] {groupId, fragmentCollectionId});
	}

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByG_FCI(long groupId, long fragmentCollectionId) {
		return _collectionPersistenceFinderByG_FCI.count(
			finderCache, new Object[] {groupId, fragmentCollectionId});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_FCI_Version;

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByG_FCI_Version(
		long groupId, long fragmentCollectionId, int version, int start,
		int end, OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_FCI_Version.find(
			finderCache, new Object[] {groupId, fragmentCollectionId, version},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByG_FCI_Version_First(
			long groupId, long fragmentCollectionId, int version,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_FCI_Version.findFirst(
			finderCache, new Object[] {groupId, fragmentCollectionId, version},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByG_FCI_Version_First(
		long groupId, long fragmentCollectionId, int version,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_FCI_Version.fetchFirst(
			finderCache, new Object[] {groupId, fragmentCollectionId, version},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 */
	@Override
	public void removeByG_FCI_Version(
		long groupId, long fragmentCollectionId, int version) {

		_collectionPersistenceFinderByG_FCI_Version.remove(
			finderCache, new Object[] {groupId, fragmentCollectionId, version});
	}

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByG_FCI_Version(
		long groupId, long fragmentCollectionId, int version) {

		return _collectionPersistenceFinderByG_FCI_Version.count(
			finderCache, new Object[] {groupId, fragmentCollectionId, version});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_FEK;

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentEntryKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByG_FEK(
		long groupId, String fragmentEntryKey, int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_FEK.find(
			finderCache, new Object[] {groupId, fragmentEntryKey}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByG_FEK_First(
			long groupId, String fragmentEntryKey,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_FEK.findFirst(
			finderCache, new Object[] {groupId, fragmentEntryKey},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByG_FEK_First(
		long groupId, String fragmentEntryKey,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_FEK.fetchFirst(
			finderCache, new Object[] {groupId, fragmentEntryKey},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentEntryKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 */
	@Override
	public void removeByG_FEK(long groupId, String fragmentEntryKey) {
		_collectionPersistenceFinderByG_FEK.remove(
			finderCache, new Object[] {groupId, fragmentEntryKey});
	}

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByG_FEK(long groupId, String fragmentEntryKey) {
		return _collectionPersistenceFinderByG_FEK.count(
			finderCache, new Object[] {groupId, fragmentEntryKey});
	}

	private UniquePersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_uniquePersistenceFinderByG_FEK_Version;

	/**
	 * Returns the fragment entry version where groupId = &#63; and fragmentEntryKey = &#63; and version = &#63; or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param version the version
	 * @return the matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByG_FEK_Version(
			long groupId, String fragmentEntryKey, int version)
		throws NoSuchEntryVersionException {

		return _uniquePersistenceFinderByG_FEK_Version.find(
			finderCache, new Object[] {groupId, fragmentEntryKey, version});
	}

	/**
	 * Returns the fragment entry version where groupId = &#63; and fragmentEntryKey = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByG_FEK_Version(
		long groupId, String fragmentEntryKey, int version,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_FEK_Version.fetch(
			finderCache, new Object[] {groupId, fragmentEntryKey, version},
			useFinderCache);
	}

	/**
	 * Removes the fragment entry version where groupId = &#63; and fragmentEntryKey = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param version the version
	 * @return the fragment entry version that was removed
	 */
	@Override
	public FragmentEntryVersion removeByG_FEK_Version(
			long groupId, String fragmentEntryKey, int version)
		throws NoSuchEntryVersionException {

		FragmentEntryVersion fragmentEntryVersion = findByG_FEK_Version(
			groupId, fragmentEntryKey, version);

		return remove(fragmentEntryVersion);
	}

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentEntryKey = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByG_FEK_Version(
		long groupId, String fragmentEntryKey, int version) {

		return _uniquePersistenceFinderByG_FEK_Version.count(
			finderCache, new Object[] {groupId, fragmentEntryKey, version});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_FCI_LikeN;

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name, int start,
		int end, OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_FCI_LikeN.find(
			finderCache, new Object[] {groupId, fragmentCollectionId, name},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByG_FCI_LikeN_First(
			long groupId, long fragmentCollectionId, String name,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_FCI_LikeN.findFirst(
			finderCache, new Object[] {groupId, fragmentCollectionId, name},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByG_FCI_LikeN_First(
		long groupId, long fragmentCollectionId, String name,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_FCI_LikeN.fetchFirst(
			finderCache, new Object[] {groupId, fragmentCollectionId, name},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 */
	@Override
	public void removeByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name) {

		_collectionPersistenceFinderByG_FCI_LikeN.remove(
			finderCache, new Object[] {groupId, fragmentCollectionId, name});
	}

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name) {

		return _collectionPersistenceFinderByG_FCI_LikeN.count(
			finderCache, new Object[] {groupId, fragmentCollectionId, name});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_FCI_LikeN_Version;

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByG_FCI_LikeN_Version(
		long groupId, long fragmentCollectionId, String name, int version,
		int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_FCI_LikeN_Version.find(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, name, version}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByG_FCI_LikeN_Version_First(
			long groupId, long fragmentCollectionId, String name, int version,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_FCI_LikeN_Version.findFirst(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, name, version},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByG_FCI_LikeN_Version_First(
		long groupId, long fragmentCollectionId, String name, int version,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_FCI_LikeN_Version.fetchFirst(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, name, version},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param version the version
	 */
	@Override
	public void removeByG_FCI_LikeN_Version(
		long groupId, long fragmentCollectionId, String name, int version) {

		_collectionPersistenceFinderByG_FCI_LikeN_Version.remove(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, name, version});
	}

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByG_FCI_LikeN_Version(
		long groupId, long fragmentCollectionId, String name, int version) {

		return _collectionPersistenceFinderByG_FCI_LikeN_Version.count(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, name, version});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_FCI_T;

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByG_FCI_T(
		long groupId, long fragmentCollectionId, int type, int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_FCI_T.find(
			finderCache, new Object[] {groupId, fragmentCollectionId, type},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByG_FCI_T_First(
			long groupId, long fragmentCollectionId, int type,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_FCI_T.findFirst(
			finderCache, new Object[] {groupId, fragmentCollectionId, type},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByG_FCI_T_First(
		long groupId, long fragmentCollectionId, int type,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_FCI_T.fetchFirst(
			finderCache, new Object[] {groupId, fragmentCollectionId, type},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 */
	@Override
	public void removeByG_FCI_T(
		long groupId, long fragmentCollectionId, int type) {

		_collectionPersistenceFinderByG_FCI_T.remove(
			finderCache, new Object[] {groupId, fragmentCollectionId, type});
	}

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByG_FCI_T(
		long groupId, long fragmentCollectionId, int type) {

		return _collectionPersistenceFinderByG_FCI_T.count(
			finderCache, new Object[] {groupId, fragmentCollectionId, type});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_FCI_T_Version;

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByG_FCI_T_Version(
		long groupId, long fragmentCollectionId, int type, int version,
		int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_FCI_T_Version.find(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, type, version}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByG_FCI_T_Version_First(
			long groupId, long fragmentCollectionId, int type, int version,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_FCI_T_Version.findFirst(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, type, version},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByG_FCI_T_Version_First(
		long groupId, long fragmentCollectionId, int type, int version,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_FCI_T_Version.fetchFirst(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, type, version},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param version the version
	 */
	@Override
	public void removeByG_FCI_T_Version(
		long groupId, long fragmentCollectionId, int type, int version) {

		_collectionPersistenceFinderByG_FCI_T_Version.remove(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, type, version});
	}

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByG_FCI_T_Version(
		long groupId, long fragmentCollectionId, int type, int version) {

		return _collectionPersistenceFinderByG_FCI_T_Version.count(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, type, version});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_FCI_S;

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByG_FCI_S(
		long groupId, long fragmentCollectionId, int status, int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_FCI_S.find(
			finderCache, new Object[] {groupId, fragmentCollectionId, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByG_FCI_S_First(
			long groupId, long fragmentCollectionId, int status,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_FCI_S.findFirst(
			finderCache, new Object[] {groupId, fragmentCollectionId, status},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByG_FCI_S_First(
		long groupId, long fragmentCollectionId, int status,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_FCI_S.fetchFirst(
			finderCache, new Object[] {groupId, fragmentCollectionId, status},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 */
	@Override
	public void removeByG_FCI_S(
		long groupId, long fragmentCollectionId, int status) {

		_collectionPersistenceFinderByG_FCI_S.remove(
			finderCache, new Object[] {groupId, fragmentCollectionId, status});
	}

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByG_FCI_S(
		long groupId, long fragmentCollectionId, int status) {

		return _collectionPersistenceFinderByG_FCI_S.count(
			finderCache, new Object[] {groupId, fragmentCollectionId, status});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_FCI_S_Version;

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByG_FCI_S_Version(
		long groupId, long fragmentCollectionId, int status, int version,
		int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_FCI_S_Version.find(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, status, version},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByG_FCI_S_Version_First(
			long groupId, long fragmentCollectionId, int status, int version,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_FCI_S_Version.findFirst(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, status, version},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByG_FCI_S_Version_First(
		long groupId, long fragmentCollectionId, int status, int version,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_FCI_S_Version.fetchFirst(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, status, version},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param version the version
	 */
	@Override
	public void removeByG_FCI_S_Version(
		long groupId, long fragmentCollectionId, int status, int version) {

		_collectionPersistenceFinderByG_FCI_S_Version.remove(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, status, version});
	}

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByG_FCI_S_Version(
		long groupId, long fragmentCollectionId, int status, int version) {

		return _collectionPersistenceFinderByG_FCI_S_Version.count(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, status, version});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_FCI_LikeN_S;

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status,
		int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_FCI_LikeN_S.find(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, name, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByG_FCI_LikeN_S_First(
			long groupId, long fragmentCollectionId, String name, int status,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_FCI_LikeN_S.findFirst(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, name, status},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByG_FCI_LikeN_S_First(
		long groupId, long fragmentCollectionId, String name, int status,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_FCI_LikeN_S.fetchFirst(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, name, status},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 */
	@Override
	public void removeByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status) {

		_collectionPersistenceFinderByG_FCI_LikeN_S.remove(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, name, status});
	}

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status) {

		return _collectionPersistenceFinderByG_FCI_LikeN_S.count(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, name, status});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_FCI_LikeN_S_Version;

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByG_FCI_LikeN_S_Version(
		long groupId, long fragmentCollectionId, String name, int status,
		int version, int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_FCI_LikeN_S_Version.find(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, name, status, version},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByG_FCI_LikeN_S_Version_First(
			long groupId, long fragmentCollectionId, String name, int status,
			int version,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_FCI_LikeN_S_Version.findFirst(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, name, status, version},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByG_FCI_LikeN_S_Version_First(
		long groupId, long fragmentCollectionId, String name, int status,
		int version,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_FCI_LikeN_S_Version.fetchFirst(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, name, status, version},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param version the version
	 */
	@Override
	public void removeByG_FCI_LikeN_S_Version(
		long groupId, long fragmentCollectionId, String name, int status,
		int version) {

		_collectionPersistenceFinderByG_FCI_LikeN_S_Version.remove(
			finderCache,
			new Object[] {
				groupId, fragmentCollectionId, name, status, version
			});
	}

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and name = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByG_FCI_LikeN_S_Version(
		long groupId, long fragmentCollectionId, String name, int status,
		int version) {

		return _collectionPersistenceFinderByG_FCI_LikeN_S_Version.count(
			finderCache,
			new Object[] {
				groupId, fragmentCollectionId, name, status, version
			});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_FCI_T_S;

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByG_FCI_T_S(
		long groupId, long fragmentCollectionId, int type, int status,
		int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_FCI_T_S.find(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, type, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByG_FCI_T_S_First(
			long groupId, long fragmentCollectionId, int type, int status,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_FCI_T_S.findFirst(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, type, status},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByG_FCI_T_S_First(
		long groupId, long fragmentCollectionId, int type, int status,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_FCI_T_S.fetchFirst(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, type, status},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 */
	@Override
	public void removeByG_FCI_T_S(
		long groupId, long fragmentCollectionId, int type, int status) {

		_collectionPersistenceFinderByG_FCI_T_S.remove(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, type, status});
	}

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByG_FCI_T_S(
		long groupId, long fragmentCollectionId, int type, int status) {

		return _collectionPersistenceFinderByG_FCI_T_S.count(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, type, status});
	}

	private CollectionPersistenceFinder
		<FragmentEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_FCI_T_S_Version;

	/**
	 * Returns an ordered range of all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param version the version
	 * @param start the lower bound of the range of fragment entry versions
	 * @param end the upper bound of the range of fragment entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry versions
	 */
	@Override
	public List<FragmentEntryVersion> findByG_FCI_T_S_Version(
		long groupId, long fragmentCollectionId, int type, int status,
		int version, int start, int end,
		OrderByComparator<FragmentEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_FCI_T_S_Version.find(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, type, status, version},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version
	 * @throws NoSuchEntryVersionException if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion findByG_FCI_T_S_Version_First(
			long groupId, long fragmentCollectionId, int type, int status,
			int version,
			OrderByComparator<FragmentEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_FCI_T_S_Version.findFirst(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, type, status, version},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry version in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry version, or <code>null</code> if a matching fragment entry version could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByG_FCI_T_S_Version_First(
		long groupId, long fragmentCollectionId, int type, int status,
		int version,
		OrderByComparator<FragmentEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_FCI_T_S_Version.fetchFirst(
			finderCache,
			new Object[] {groupId, fragmentCollectionId, type, status, version},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param version the version
	 */
	@Override
	public void removeByG_FCI_T_S_Version(
		long groupId, long fragmentCollectionId, int type, int status,
		int version) {

		_collectionPersistenceFinderByG_FCI_T_S_Version.remove(
			finderCache,
			new Object[] {
				groupId, fragmentCollectionId, type, status, version
			});
	}

	/**
	 * Returns the number of fragment entry versions where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param version the version
	 * @return the number of matching fragment entry versions
	 */
	@Override
	public int countByG_FCI_T_S_Version(
		long groupId, long fragmentCollectionId, int type, int status,
		int version) {

		return _collectionPersistenceFinderByG_FCI_T_S_Version.count(
			finderCache,
			new Object[] {
				groupId, fragmentCollectionId, type, status, version
			});
	}

	public FragmentEntryVersionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(FragmentEntryVersion.class);

		setModelImplClass(FragmentEntryVersionImpl.class);
		setModelPKClass(long.class);

		setTable(FragmentEntryVersionTable.INSTANCE);
	}

	/**
	 * Creates a new fragment entry version with the primary key. Does not add the fragment entry version to the database.
	 *
	 * @param fragmentEntryVersionId the primary key for the new fragment entry version
	 * @return the new fragment entry version
	 */
	@Override
	public FragmentEntryVersion create(long fragmentEntryVersionId) {
		FragmentEntryVersion fragmentEntryVersion =
			new FragmentEntryVersionImpl();

		fragmentEntryVersion.setNew(true);
		fragmentEntryVersion.setPrimaryKey(fragmentEntryVersionId);

		fragmentEntryVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return fragmentEntryVersion;
	}

	/**
	 * Removes the fragment entry version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fragmentEntryVersionId the primary key of the fragment entry version
	 * @return the fragment entry version that was removed
	 * @throws NoSuchEntryVersionException if a fragment entry version with the primary key could not be found
	 */
	@Override
	public FragmentEntryVersion remove(long fragmentEntryVersionId)
		throws NoSuchEntryVersionException {

		return remove((Serializable)fragmentEntryVersionId);
	}

	@Override
	protected FragmentEntryVersion removeImpl(
		FragmentEntryVersion fragmentEntryVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(fragmentEntryVersion)) {
				fragmentEntryVersion = (FragmentEntryVersion)session.get(
					FragmentEntryVersionImpl.class,
					fragmentEntryVersion.getPrimaryKeyObj());
			}

			if ((fragmentEntryVersion != null) &&
				ctPersistenceHelper.isRemove(fragmentEntryVersion)) {

				session.delete(fragmentEntryVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (fragmentEntryVersion != null) {
			clearCache(fragmentEntryVersion);
		}

		return fragmentEntryVersion;
	}

	@Override
	public FragmentEntryVersion updateImpl(
		FragmentEntryVersion fragmentEntryVersion) {

		boolean isNew = fragmentEntryVersion.isNew();

		if (!(fragmentEntryVersion instanceof FragmentEntryVersionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(fragmentEntryVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					fragmentEntryVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in fragmentEntryVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom FragmentEntryVersion implementation " +
					fragmentEntryVersion.getClass());
		}

		FragmentEntryVersionModelImpl fragmentEntryVersionModelImpl =
			(FragmentEntryVersionModelImpl)fragmentEntryVersion;

		if (Validator.isNull(fragmentEntryVersion.getExternalReferenceCode())) {
			fragmentEntryVersion.setExternalReferenceCode(
				String.valueOf(fragmentEntryVersion.getPrimaryKey()));
		}
		else {
			if (!Objects.equals(
					fragmentEntryVersionModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					fragmentEntryVersion.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = fragmentEntryVersion.getCompanyId();

					long groupId = fragmentEntryVersion.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = fragmentEntryVersion.getPrimaryKey();
					}

					try {
						fragmentEntryVersion.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								FragmentEntryVersion.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								fragmentEntryVersion.getExternalReferenceCode(),
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

		if (isNew && (fragmentEntryVersion.getCreateDate() == null)) {
			if (serviceContext == null) {
				fragmentEntryVersion.setCreateDate(date);
			}
			else {
				fragmentEntryVersion.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!fragmentEntryVersionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				fragmentEntryVersion.setModifiedDate(date);
			}
			else {
				fragmentEntryVersion.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(fragmentEntryVersion)) {
				if (!isNew) {
					session.evict(
						FragmentEntryVersionImpl.class,
						fragmentEntryVersion.getPrimaryKeyObj());
				}

				session.save(fragmentEntryVersion);
			}
			else {
				throw new IllegalArgumentException(
					"FragmentEntryVersion is read only, create a new version instead");
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(fragmentEntryVersion, false);

		if (isNew) {
			fragmentEntryVersion.setNew(false);
		}

		fragmentEntryVersion.resetOriginalValues();

		return fragmentEntryVersion;
	}

	/**
	 * Returns the fragment entry version with the primary key or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param fragmentEntryVersionId the primary key of the fragment entry version
	 * @return the fragment entry version
	 * @throws NoSuchEntryVersionException if a fragment entry version with the primary key could not be found
	 */
	@Override
	public FragmentEntryVersion findByPrimaryKey(long fragmentEntryVersionId)
		throws NoSuchEntryVersionException {

		return findByPrimaryKey((Serializable)fragmentEntryVersionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the fragment entry version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fragmentEntryVersionId the primary key of the fragment entry version
	 * @return the fragment entry version, or <code>null</code> if a fragment entry version with the primary key could not be found
	 */
	@Override
	public FragmentEntryVersion fetchByPrimaryKey(long fragmentEntryVersionId) {
		return fetchByPrimaryKey((Serializable)fragmentEntryVersionId);
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
		return "fragmentEntryVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_FRAGMENTENTRYVERSION;
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
		return FragmentEntryVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "FragmentEntryVersion";
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
		ctMergeColumnNames.add("version");
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("externalReferenceCode");
		ctMergeColumnNames.add("fragmentEntryId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("fragmentCollectionId");
		ctMergeColumnNames.add("fragmentEntryKey");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("css");
		ctMergeColumnNames.add("html");
		ctMergeColumnNames.add("js");
		ctMergeColumnNames.add("cacheable");
		ctMergeColumnNames.add("configuration");
		ctMergeColumnNames.add("icon");
		ctMergeColumnNames.add("previewFileEntryId");
		ctMergeColumnNames.add("marketplace");
		ctMergeColumnNames.add("readOnly");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("typeOptions");
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
			CTColumnResolutionType.PK,
			Collections.singleton("fragmentEntryVersionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"fragmentEntryId", "version"});

		_uniqueIndexColumnNames.add(
			new String[] {"uuid_", "groupId", "version"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "fragmentEntryKey", "version"});
	}

	/**
	 * Initializes the fragment entry version persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByFragmentEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByFragmentEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"fragmentEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByFragmentEntryId",
					new String[] {Long.class.getName()},
					new String[] {"fragmentEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByFragmentEntryId",
					new String[] {Long.class.getName()},
					new String[] {"fragmentEntryId"}, false),
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "fragmentEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					FragmentEntryVersion::getFragmentEntryId));

		_uniquePersistenceFinderByFragmentEntryId_Version =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByFragmentEntryId_Version",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"fragmentEntryId", "version"}, 0, 0, false,
					FragmentEntryVersion::getFragmentEntryId,
					FragmentEntryVersion::getVersion),
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE, "",
				new FinderColumn<>(
					"fragmentEntryVersion.", "fragmentEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					FragmentEntryVersion::getFragmentEntryId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getVersion));

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
			_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
			_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
			FragmentEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"fragmentEntryVersion.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				FragmentEntryVersion::getUuid));

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
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryVersion::getUuid),
				new FinderColumn<>(
					"fragmentEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getVersion));

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
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryVersion::getUuid),
				new FinderColumn<>(
					"fragmentEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryVersion::getGroupId));

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
					convertNullFunction(FragmentEntryVersion::getUuid),
					FragmentEntryVersion::getGroupId,
					FragmentEntryVersion::getVersion),
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE, "",
				new FinderColumn<>(
					"fragmentEntryVersion.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryVersion::getUuid),
				new FinderColumn<>(
					"fragmentEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryVersion::getGroupId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getVersion));

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
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryVersion::getUuid),
				new FinderColumn<>(
					"fragmentEntryVersion.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					FragmentEntryVersion::getCompanyId));

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
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryVersion::getUuid),
				new FinderColumn<>(
					"fragmentEntryVersion.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					FragmentEntryVersion::getCompanyId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getVersion));

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
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryVersion::getGroupId));

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
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryVersion::getGroupId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getVersion));

		_collectionPersistenceFinderByFragmentCollectionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByFragmentCollectionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"fragmentCollectionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByFragmentCollectionId",
					new String[] {Long.class.getName()},
					new String[] {"fragmentCollectionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByFragmentCollectionId",
					new String[] {Long.class.getName()},
					new String[] {"fragmentCollectionId"}, false),
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "fragmentCollectionId",
					FinderColumn.Type.LONG, "=", true, true,
					FragmentEntryVersion::getFragmentCollectionId));

		_collectionPersistenceFinderByFragmentCollectionId_Version =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByFragmentCollectionId_Version",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"fragmentCollectionId", "version"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByFragmentCollectionId_Version",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"fragmentCollectionId", "version"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByFragmentCollectionId_Version",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"fragmentCollectionId", "version"}, false),
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "fragmentCollectionId",
					FinderColumn.Type.LONG, "=", true, true,
					FragmentEntryVersion::getFragmentCollectionId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getVersion));

		_collectionPersistenceFinderByType = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByType",
				new String[] {
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByType",
				new String[] {Integer.class.getName()}, new String[] {"type_"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByType",
				new String[] {Integer.class.getName()}, new String[] {"type_"},
				false),
			_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
			_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
			FragmentEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"fragmentEntryVersion.", "type", "type_",
				FinderColumn.Type.INTEGER, "=", true, true,
				FragmentEntryVersion::getType));

		_collectionPersistenceFinderByType_Version =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByType_Version",
					new String[] {
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"type_", "version"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByType_Version",
					new String[] {
						Integer.class.getName(), Integer.class.getName()
					},
					new String[] {"type_", "version"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByType_Version",
					new String[] {
						Integer.class.getName(), Integer.class.getName()
					},
					new String[] {"type_", "version"}, false),
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getType),
				new FinderColumn<>(
					"fragmentEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getVersion));

		_collectionPersistenceFinderByG_FCI = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_FCI",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "fragmentCollectionId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_FCI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "fragmentCollectionId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_FCI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "fragmentCollectionId"}, false),
			_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
			_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
			FragmentEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"fragmentEntryVersion.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, FragmentEntryVersion::getGroupId),
			new FinderColumn<>(
				"fragmentEntryVersion.", "fragmentCollectionId",
				FinderColumn.Type.LONG, "=", true, true,
				FragmentEntryVersion::getFragmentCollectionId));

		_collectionPersistenceFinderByG_FCI_Version =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_FCI_Version",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "fragmentCollectionId", "version"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_FCI_Version",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "fragmentCollectionId", "version"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_FCI_Version",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "fragmentCollectionId", "version"},
					false),
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryVersion::getGroupId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "fragmentCollectionId",
					FinderColumn.Type.LONG, "=", true, true,
					FragmentEntryVersion::getFragmentCollectionId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getVersion));

		_collectionPersistenceFinderByG_FEK = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_FEK",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "fragmentEntryKey"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_FEK",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "fragmentEntryKey"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_FEK",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "fragmentEntryKey"}, 0, 2, false,
				null),
			_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
			_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
			FragmentEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"fragmentEntryVersion.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, FragmentEntryVersion::getGroupId),
			new FinderColumn<>(
				"fragmentEntryVersion.", "fragmentEntryKey",
				FinderColumn.Type.STRING, "=", true, true,
				FragmentEntryVersion::getFragmentEntryKey));

		_uniquePersistenceFinderByG_FEK_Version = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_FEK_Version",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName()
				},
				new String[] {"groupId", "fragmentEntryKey", "version"}, 0, 2,
				false, FragmentEntryVersion::getGroupId,
				convertNullFunction(FragmentEntryVersion::getFragmentEntryKey),
				FragmentEntryVersion::getVersion),
			_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE, "",
			new FinderColumn<>(
				"fragmentEntryVersion.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, FragmentEntryVersion::getGroupId),
			new FinderColumn<>(
				"fragmentEntryVersion.", "fragmentEntryKey",
				FinderColumn.Type.STRING, "=", true, true,
				FragmentEntryVersion::getFragmentEntryKey),
			new FinderColumn<>(
				"fragmentEntryVersion.", "version", FinderColumn.Type.INTEGER,
				"=", true, true, FragmentEntryVersion::getVersion));

		_collectionPersistenceFinderByG_FCI_LikeN =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_FCI_LikeN",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "fragmentCollectionId", "name"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_FCI_LikeN",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName()
					},
					new String[] {"groupId", "fragmentCollectionId", "name"}, 0,
					4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_FCI_LikeN",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName()
					},
					new String[] {"groupId", "fragmentCollectionId", "name"}, 0,
					4, false, null),
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryVersion::getGroupId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "fragmentCollectionId",
					FinderColumn.Type.LONG, "=", true, true,
					FragmentEntryVersion::getFragmentCollectionId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "name", FinderColumn.Type.STRING,
					"=", true, true, FragmentEntryVersion::getName));

		_collectionPersistenceFinderByG_FCI_LikeN_Version =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_FCI_LikeN_Version",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "name", "version"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_FCI_LikeN_Version",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "name", "version"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_FCI_LikeN_Version",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "name", "version"
					},
					0, 4, false, null),
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryVersion::getGroupId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "fragmentCollectionId",
					FinderColumn.Type.LONG, "=", true, true,
					FragmentEntryVersion::getFragmentCollectionId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "name", FinderColumn.Type.STRING,
					"=", true, true, FragmentEntryVersion::getName),
				new FinderColumn<>(
					"fragmentEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getVersion));

		_collectionPersistenceFinderByG_FCI_T =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_FCI_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "fragmentCollectionId", "type_"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_FCI_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "fragmentCollectionId", "type_"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_FCI_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "fragmentCollectionId", "type_"},
					false),
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryVersion::getGroupId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "fragmentCollectionId",
					FinderColumn.Type.LONG, "=", true, true,
					FragmentEntryVersion::getFragmentCollectionId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getType));

		_collectionPersistenceFinderByG_FCI_T_Version =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_FCI_T_Version",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "type_", "version"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_FCI_T_Version",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "type_", "version"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_FCI_T_Version",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "type_", "version"
					},
					false),
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryVersion::getGroupId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "fragmentCollectionId",
					FinderColumn.Type.LONG, "=", true, true,
					FragmentEntryVersion::getFragmentCollectionId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getType),
				new FinderColumn<>(
					"fragmentEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getVersion));

		_collectionPersistenceFinderByG_FCI_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_FCI_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "fragmentCollectionId", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_FCI_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "fragmentCollectionId", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_FCI_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "fragmentCollectionId", "status"},
					false),
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryVersion::getGroupId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "fragmentCollectionId",
					FinderColumn.Type.LONG, "=", true, true,
					FragmentEntryVersion::getFragmentCollectionId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getStatus));

		_collectionPersistenceFinderByG_FCI_S_Version =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_FCI_S_Version",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "status", "version"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_FCI_S_Version",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "status", "version"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_FCI_S_Version",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "status", "version"
					},
					false),
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryVersion::getGroupId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "fragmentCollectionId",
					FinderColumn.Type.LONG, "=", true, true,
					FragmentEntryVersion::getFragmentCollectionId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getStatus),
				new FinderColumn<>(
					"fragmentEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getVersion));

		_collectionPersistenceFinderByG_FCI_LikeN_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_FCI_LikeN_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "name", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_FCI_LikeN_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "name", "status"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_FCI_LikeN_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "name", "status"
					},
					0, 4, false, null),
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryVersion::getGroupId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "fragmentCollectionId",
					FinderColumn.Type.LONG, "=", true, true,
					FragmentEntryVersion::getFragmentCollectionId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "name", FinderColumn.Type.STRING,
					"=", true, true, FragmentEntryVersion::getName),
				new FinderColumn<>(
					"fragmentEntryVersion.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getStatus));

		_collectionPersistenceFinderByG_FCI_LikeN_S_Version =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_FCI_LikeN_S_Version",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "name", "status",
						"version"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_FCI_LikeN_S_Version",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "name", "status",
						"version"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_FCI_LikeN_S_Version",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "name", "status",
						"version"
					},
					0, 4, false, null),
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryVersion::getGroupId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "fragmentCollectionId",
					FinderColumn.Type.LONG, "=", true, true,
					FragmentEntryVersion::getFragmentCollectionId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "name", FinderColumn.Type.STRING,
					"=", true, true, FragmentEntryVersion::getName),
				new FinderColumn<>(
					"fragmentEntryVersion.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getStatus),
				new FinderColumn<>(
					"fragmentEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getVersion));

		_collectionPersistenceFinderByG_FCI_T_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_FCI_T_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "type_", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_FCI_T_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "type_", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_FCI_T_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "type_", "status"
					},
					false),
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryVersion::getGroupId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "fragmentCollectionId",
					FinderColumn.Type.LONG, "=", true, true,
					FragmentEntryVersion::getFragmentCollectionId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getType),
				new FinderColumn<>(
					"fragmentEntryVersion.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getStatus));

		_collectionPersistenceFinderByG_FCI_T_S_Version =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_FCI_T_S_Version",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "type_", "status",
						"version"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_FCI_T_S_Version",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "type_", "status",
						"version"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_FCI_T_S_Version",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "fragmentCollectionId", "type_", "status",
						"version"
					},
					false),
				_SQL_SELECT_FRAGMENTENTRYVERSION_WHERE,
				_SQL_COUNT_FRAGMENTENTRYVERSION_WHERE,
				FragmentEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"fragmentEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryVersion::getGroupId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "fragmentCollectionId",
					FinderColumn.Type.LONG, "=", true, true,
					FragmentEntryVersion::getFragmentCollectionId),
				new FinderColumn<>(
					"fragmentEntryVersion.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getType),
				new FinderColumn<>(
					"fragmentEntryVersion.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getStatus),
				new FinderColumn<>(
					"fragmentEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					FragmentEntryVersion::getVersion));

		FragmentEntryVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		FragmentEntryVersionUtil.setPersistence(null);

		entityCache.removeCache(FragmentEntryVersionImpl.class.getName());
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
		FragmentEntryVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_FRAGMENTENTRYVERSION =
		"SELECT fragmentEntryVersion FROM FragmentEntryVersion fragmentEntryVersion";

	private static final String _SQL_SELECT_FRAGMENTENTRYVERSION_WHERE =
		"SELECT fragmentEntryVersion FROM FragmentEntryVersion fragmentEntryVersion WHERE ";

	private static final String _SQL_COUNT_FRAGMENTENTRYVERSION_WHERE =
		"SELECT COUNT(fragmentEntryVersion) FROM FragmentEntryVersion fragmentEntryVersion WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1497869728