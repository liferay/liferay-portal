/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.persistence.impl;

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
import com.liferay.style.book.exception.NoSuchEntryVersionException;
import com.liferay.style.book.model.StyleBookEntryVersion;
import com.liferay.style.book.model.StyleBookEntryVersionTable;
import com.liferay.style.book.model.impl.StyleBookEntryVersionImpl;
import com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl;
import com.liferay.style.book.service.persistence.StyleBookEntryVersionPersistence;
import com.liferay.style.book.service.persistence.StyleBookEntryVersionUtil;
import com.liferay.style.book.service.persistence.impl.constants.StyleBookPersistenceConstants;

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
 * The persistence implementation for the style book entry version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = StyleBookEntryVersionPersistence.class)
public class StyleBookEntryVersionPersistenceImpl
	extends BasePersistenceImpl
		<StyleBookEntryVersion, NoSuchEntryVersionException>
	implements StyleBookEntryVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>StyleBookEntryVersionUtil</code> to access the style book entry version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		StyleBookEntryVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByStyleBookEntryId;

	/**
	 * Returns an ordered range of all the style book entry versions where styleBookEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByStyleBookEntryId(
		long styleBookEntryId, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByStyleBookEntryId.find(
			finderCache, new Object[] {styleBookEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByStyleBookEntryId_First(
			long styleBookEntryId,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByStyleBookEntryId.findFirst(
			finderCache, new Object[] {styleBookEntryId}, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByStyleBookEntryId_First(
		long styleBookEntryId,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByStyleBookEntryId.fetchFirst(
			finderCache, new Object[] {styleBookEntryId}, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where styleBookEntryId = &#63; from the database.
	 *
	 * @param styleBookEntryId the style book entry ID
	 */
	@Override
	public void removeByStyleBookEntryId(long styleBookEntryId) {
		_collectionPersistenceFinderByStyleBookEntryId.remove(
			finderCache, new Object[] {styleBookEntryId});
	}

	/**
	 * Returns the number of style book entry versions where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByStyleBookEntryId(long styleBookEntryId) {
		return _collectionPersistenceFinderByStyleBookEntryId.count(
			finderCache, new Object[] {styleBookEntryId});
	}

	private UniquePersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_uniquePersistenceFinderByStyleBookEntryId_Version;

	/**
	 * Returns the style book entry version where styleBookEntryId = &#63; and version = &#63; or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param version the version
	 * @return the matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByStyleBookEntryId_Version(
			long styleBookEntryId, int version)
		throws NoSuchEntryVersionException {

		return _uniquePersistenceFinderByStyleBookEntryId_Version.find(
			finderCache, new Object[] {styleBookEntryId, version});
	}

	/**
	 * Returns the style book entry version where styleBookEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByStyleBookEntryId_Version(
		long styleBookEntryId, int version, boolean useFinderCache) {

		return _uniquePersistenceFinderByStyleBookEntryId_Version.fetch(
			finderCache, new Object[] {styleBookEntryId, version},
			useFinderCache);
	}

	/**
	 * Removes the style book entry version where styleBookEntryId = &#63; and version = &#63; from the database.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param version the version
	 * @return the style book entry version that was removed
	 */
	@Override
	public StyleBookEntryVersion removeByStyleBookEntryId_Version(
			long styleBookEntryId, int version)
		throws NoSuchEntryVersionException {

		StyleBookEntryVersion styleBookEntryVersion =
			findByStyleBookEntryId_Version(styleBookEntryId, version);

		return remove(styleBookEntryVersion);
	}

	/**
	 * Returns the number of style book entry versions where styleBookEntryId = &#63; and version = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByStyleBookEntryId_Version(
		long styleBookEntryId, int version) {

		return _uniquePersistenceFinderByStyleBookEntryId_Version.count(
			finderCache, new Object[] {styleBookEntryId, version});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByUuid_First(
			String uuid,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByUuid_First(
		String uuid,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of style book entry versions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByUuid_Version;

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByUuid_Version(
		String uuid, int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_Version.find(
			finderCache, new Object[] {uuid, version}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByUuid_Version_First(
			String uuid, int version,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByUuid_Version.findFirst(
			finderCache, new Object[] {uuid, version}, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByUuid_Version_First(
		String uuid, int version,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByUuid_Version.fetchFirst(
			finderCache, new Object[] {uuid, version}, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where uuid = &#63; and version = &#63; from the database.
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
	 * Returns the number of style book entry versions where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByUuid_Version(String uuid, int version) {
		return _collectionPersistenceFinderByUuid_Version.count(
			finderCache, new Object[] {uuid, version});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByUUID_G;

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByUUID_G(
		String uuid, long groupId, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByUUID_G_First(
			String uuid, long groupId,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByUUID_G.findFirst(
			finderCache, new Object[] {uuid, groupId}, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByUUID_G_First(
		String uuid, long groupId,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByUUID_G.fetchFirst(
			finderCache, new Object[] {uuid, groupId}, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where uuid = &#63; and groupId = &#63; from the database.
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
	 * Returns the number of style book entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _collectionPersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private UniquePersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_uniquePersistenceFinderByUUID_G_Version;

	/**
	 * Returns the style book entry version where uuid = &#63; and groupId = &#63; and version = &#63; or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByUUID_G_Version(
			String uuid, long groupId, int version)
		throws NoSuchEntryVersionException {

		return _uniquePersistenceFinderByUUID_G_Version.find(
			finderCache, new Object[] {uuid, groupId, version});
	}

	/**
	 * Returns the style book entry version where uuid = &#63; and groupId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByUUID_G_Version(
		String uuid, long groupId, int version, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G_Version.fetch(
			finderCache, new Object[] {uuid, groupId, version}, useFinderCache);
	}

	/**
	 * Removes the style book entry version where uuid = &#63; and groupId = &#63; and version = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the style book entry version that was removed
	 */
	@Override
	public StyleBookEntryVersion removeByUUID_G_Version(
			String uuid, long groupId, int version)
		throws NoSuchEntryVersionException {

		StyleBookEntryVersion styleBookEntryVersion = findByUUID_G_Version(
			uuid, groupId, version);

		return remove(styleBookEntryVersion);
	}

	/**
	 * Returns the number of style book entry versions where uuid = &#63; and groupId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByUUID_G_Version(String uuid, long groupId, int version) {
		return _uniquePersistenceFinderByUUID_G_Version.count(
			finderCache, new Object[] {uuid, groupId, version});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of style book entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByUuid_C_Version;

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByUuid_C_Version(
		String uuid, long companyId, int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C_Version.find(
			finderCache, new Object[] {uuid, companyId, version}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByUuid_C_Version_First(
			String uuid, long companyId, int version,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByUuid_C_Version.findFirst(
			finderCache, new Object[] {uuid, companyId, version},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByUuid_C_Version_First(
		String uuid, long companyId, int version,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C_Version.fetchFirst(
			finderCache, new Object[] {uuid, companyId, version},
			orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where uuid = &#63; and companyId = &#63; and version = &#63; from the database.
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
	 * Returns the number of style book entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByUuid_C_Version(String uuid, long companyId, int version) {
		return _collectionPersistenceFinderByUuid_C_Version.count(
			finderCache, new Object[] {uuid, companyId, version});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByGroupId_First(
			long groupId,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByGroupId_First(
		long groupId,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByGroupId_Version;

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByGroupId_Version(
		long groupId, int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId_Version.find(
			finderCache, new Object[] {groupId, version}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByGroupId_Version_First(
			long groupId, int version,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByGroupId_Version.findFirst(
			finderCache, new Object[] {groupId, version}, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByGroupId_Version_First(
		long groupId, int version,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByGroupId_Version.fetchFirst(
			finderCache, new Object[] {groupId, version}, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and version = &#63; from the database.
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
	 * Returns the number of style book entry versions where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByGroupId_Version(long groupId, int version) {
		return _collectionPersistenceFinderByGroupId_Version.count(
			finderCache, new Object[] {groupId, version});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_D;

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByG_D(
		long groupId, boolean defaultStyleBookEntry, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_D.find(
			finderCache, new Object[] {groupId, defaultStyleBookEntry}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByG_D_First(
			long groupId, boolean defaultStyleBookEntry,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_D.findFirst(
			finderCache, new Object[] {groupId, defaultStyleBookEntry},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByG_D_First(
		long groupId, boolean defaultStyleBookEntry,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_D.fetchFirst(
			finderCache, new Object[] {groupId, defaultStyleBookEntry},
			orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 */
	@Override
	public void removeByG_D(long groupId, boolean defaultStyleBookEntry) {
		_collectionPersistenceFinderByG_D.remove(
			finderCache, new Object[] {groupId, defaultStyleBookEntry});
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByG_D(long groupId, boolean defaultStyleBookEntry) {
		return _collectionPersistenceFinderByG_D.count(
			finderCache, new Object[] {groupId, defaultStyleBookEntry});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_D_Version;

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByG_D_Version(
		long groupId, boolean defaultStyleBookEntry, int version, int start,
		int end, OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_D_Version.find(
			finderCache, new Object[] {groupId, defaultStyleBookEntry, version},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByG_D_Version_First(
			long groupId, boolean defaultStyleBookEntry, int version,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_D_Version.findFirst(
			finderCache, new Object[] {groupId, defaultStyleBookEntry, version},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByG_D_Version_First(
		long groupId, boolean defaultStyleBookEntry, int version,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_D_Version.fetchFirst(
			finderCache, new Object[] {groupId, defaultStyleBookEntry, version},
			orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 */
	@Override
	public void removeByG_D_Version(
		long groupId, boolean defaultStyleBookEntry, int version) {

		_collectionPersistenceFinderByG_D_Version.remove(
			finderCache,
			new Object[] {groupId, defaultStyleBookEntry, version});
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByG_D_Version(
		long groupId, boolean defaultStyleBookEntry, int version) {

		return _collectionPersistenceFinderByG_D_Version.count(
			finderCache,
			new Object[] {groupId, defaultStyleBookEntry, version});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_N;

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByG_N(
		long groupId, String name, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_N.find(
			finderCache, new Object[] {groupId, name}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByG_N_First(
			long groupId, String name,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_N.findFirst(
			finderCache, new Object[] {groupId, name}, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByG_N_First(
		long groupId, String name,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_N.fetchFirst(
			finderCache, new Object[] {groupId, name}, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 */
	@Override
	public void removeByG_N(long groupId, String name) {
		_collectionPersistenceFinderByG_N.remove(
			finderCache, new Object[] {groupId, name});
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByG_N(long groupId, String name) {
		return _collectionPersistenceFinderByG_N.count(
			finderCache, new Object[] {groupId, name});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_N_Version;

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByG_N_Version(
		long groupId, String name, int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_N_Version.find(
			finderCache, new Object[] {groupId, name, version}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByG_N_Version_First(
			long groupId, String name, int version,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_N_Version.findFirst(
			finderCache, new Object[] {groupId, name, version},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByG_N_Version_First(
		long groupId, String name, int version,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_N_Version.fetchFirst(
			finderCache, new Object[] {groupId, name, version},
			orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 */
	@Override
	public void removeByG_N_Version(long groupId, String name, int version) {
		_collectionPersistenceFinderByG_N_Version.remove(
			finderCache, new Object[] {groupId, name, version});
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByG_N_Version(long groupId, String name, int version) {
		return _collectionPersistenceFinderByG_N_Version.count(
			finderCache, new Object[] {groupId, name, version});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_LikeN;

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByG_LikeN(
		long groupId, String name, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN.find(
			finderCache, new Object[] {groupId, name}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByG_LikeN_First(
			long groupId, String name,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_LikeN.findFirst(
			finderCache, new Object[] {groupId, name}, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByG_LikeN_First(
		long groupId, String name,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN.fetchFirst(
			finderCache, new Object[] {groupId, name}, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 */
	@Override
	public void removeByG_LikeN(long groupId, String name) {
		_collectionPersistenceFinderByG_LikeN.remove(
			finderCache, new Object[] {groupId, name});
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByG_LikeN(long groupId, String name) {
		return _collectionPersistenceFinderByG_LikeN.count(
			finderCache, new Object[] {groupId, name});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_LikeN_Version;

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByG_LikeN_Version(
		long groupId, String name, int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN_Version.find(
			finderCache, new Object[] {groupId, name, version}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByG_LikeN_Version_First(
			long groupId, String name, int version,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_LikeN_Version.findFirst(
			finderCache, new Object[] {groupId, name, version},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByG_LikeN_Version_First(
		long groupId, String name, int version,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN_Version.fetchFirst(
			finderCache, new Object[] {groupId, name, version},
			orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 */
	@Override
	public void removeByG_LikeN_Version(
		long groupId, String name, int version) {

		_collectionPersistenceFinderByG_LikeN_Version.remove(
			finderCache, new Object[] {groupId, name, version});
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByG_LikeN_Version(long groupId, String name, int version) {
		return _collectionPersistenceFinderByG_LikeN_Version.count(
			finderCache, new Object[] {groupId, name, version});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_SBEK;

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByG_SBEK(
		long groupId, String styleBookEntryKey, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_SBEK.find(
			finderCache, new Object[] {groupId, styleBookEntryKey}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByG_SBEK_First(
			long groupId, String styleBookEntryKey,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_SBEK.findFirst(
			finderCache, new Object[] {groupId, styleBookEntryKey},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByG_SBEK_First(
		long groupId, String styleBookEntryKey,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_SBEK.fetchFirst(
			finderCache, new Object[] {groupId, styleBookEntryKey},
			orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and styleBookEntryKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 */
	@Override
	public void removeByG_SBEK(long groupId, String styleBookEntryKey) {
		_collectionPersistenceFinderByG_SBEK.remove(
			finderCache, new Object[] {groupId, styleBookEntryKey});
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByG_SBEK(long groupId, String styleBookEntryKey) {
		return _collectionPersistenceFinderByG_SBEK.count(
			finderCache, new Object[] {groupId, styleBookEntryKey});
	}

	private UniquePersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_uniquePersistenceFinderByG_SBEK_Version;

	/**
	 * Returns the style book entry version where groupId = &#63; and styleBookEntryKey = &#63; and version = &#63; or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param version the version
	 * @return the matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByG_SBEK_Version(
			long groupId, String styleBookEntryKey, int version)
		throws NoSuchEntryVersionException {

		return _uniquePersistenceFinderByG_SBEK_Version.find(
			finderCache, new Object[] {groupId, styleBookEntryKey, version});
	}

	/**
	 * Returns the style book entry version where groupId = &#63; and styleBookEntryKey = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByG_SBEK_Version(
		long groupId, String styleBookEntryKey, int version,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_SBEK_Version.fetch(
			finderCache, new Object[] {groupId, styleBookEntryKey, version},
			useFinderCache);
	}

	/**
	 * Removes the style book entry version where groupId = &#63; and styleBookEntryKey = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param version the version
	 * @return the style book entry version that was removed
	 */
	@Override
	public StyleBookEntryVersion removeByG_SBEK_Version(
			long groupId, String styleBookEntryKey, int version)
		throws NoSuchEntryVersionException {

		StyleBookEntryVersion styleBookEntryVersion = findByG_SBEK_Version(
			groupId, styleBookEntryKey, version);

		return remove(styleBookEntryVersion);
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and styleBookEntryKey = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByG_SBEK_Version(
		long groupId, String styleBookEntryKey, int version) {

		return _uniquePersistenceFinderByG_SBEK_Version.count(
			finderCache, new Object[] {groupId, styleBookEntryKey, version});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_T;

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByG_T(
		long groupId, String themeId, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T.find(
			finderCache, new Object[] {groupId, themeId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByG_T_First(
			long groupId, String themeId,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_T.findFirst(
			finderCache, new Object[] {groupId, themeId}, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByG_T_First(
		long groupId, String themeId,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_T.fetchFirst(
			finderCache, new Object[] {groupId, themeId}, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and themeId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 */
	@Override
	public void removeByG_T(long groupId, String themeId) {
		_collectionPersistenceFinderByG_T.remove(
			finderCache, new Object[] {groupId, themeId});
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByG_T(long groupId, String themeId) {
		return _collectionPersistenceFinderByG_T.count(
			finderCache, new Object[] {groupId, themeId});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_T_Version;

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByG_T_Version(
		long groupId, String themeId, int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T_Version.find(
			finderCache, new Object[] {groupId, themeId, version}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByG_T_Version_First(
			long groupId, String themeId, int version,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_T_Version.findFirst(
			finderCache, new Object[] {groupId, themeId, version},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByG_T_Version_First(
		long groupId, String themeId, int version,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_T_Version.fetchFirst(
			finderCache, new Object[] {groupId, themeId, version},
			orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and themeId = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 */
	@Override
	public void removeByG_T_Version(long groupId, String themeId, int version) {
		_collectionPersistenceFinderByG_T_Version.remove(
			finderCache, new Object[] {groupId, themeId, version});
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByG_T_Version(long groupId, String themeId, int version) {
		return _collectionPersistenceFinderByG_T_Version.count(
			finderCache, new Object[] {groupId, themeId, version});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_D_T;

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByG_D_T(
		long groupId, boolean defaultStyleBookEntry, String themeId, int start,
		int end, OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_D_T.find(
			finderCache, new Object[] {groupId, defaultStyleBookEntry, themeId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByG_D_T_First(
			long groupId, boolean defaultStyleBookEntry, String themeId,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_D_T.findFirst(
			finderCache, new Object[] {groupId, defaultStyleBookEntry, themeId},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByG_D_T_First(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_D_T.fetchFirst(
			finderCache, new Object[] {groupId, defaultStyleBookEntry, themeId},
			orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 */
	@Override
	public void removeByG_D_T(
		long groupId, boolean defaultStyleBookEntry, String themeId) {

		_collectionPersistenceFinderByG_D_T.remove(
			finderCache,
			new Object[] {groupId, defaultStyleBookEntry, themeId});
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByG_D_T(
		long groupId, boolean defaultStyleBookEntry, String themeId) {

		return _collectionPersistenceFinderByG_D_T.count(
			finderCache,
			new Object[] {groupId, defaultStyleBookEntry, themeId});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_D_T_Version;

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByG_D_T_Version(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_D_T_Version.find(
			finderCache,
			new Object[] {groupId, defaultStyleBookEntry, themeId, version},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByG_D_T_Version_First(
			long groupId, boolean defaultStyleBookEntry, String themeId,
			int version,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_D_T_Version.findFirst(
			finderCache,
			new Object[] {groupId, defaultStyleBookEntry, themeId, version},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByG_D_T_Version_First(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		int version,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_D_T_Version.fetchFirst(
			finderCache,
			new Object[] {groupId, defaultStyleBookEntry, themeId, version},
			orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 */
	@Override
	public void removeByG_D_T_Version(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		int version) {

		_collectionPersistenceFinderByG_D_T_Version.remove(
			finderCache,
			new Object[] {groupId, defaultStyleBookEntry, themeId, version});
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByG_D_T_Version(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		int version) {

		return _collectionPersistenceFinderByG_D_T_Version.count(
			finderCache,
			new Object[] {groupId, defaultStyleBookEntry, themeId, version});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_LikeN_T;

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByG_LikeN_T(
		long groupId, String name, String themeId, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN_T.find(
			finderCache, new Object[] {groupId, name, themeId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByG_LikeN_T_First(
			long groupId, String name, String themeId,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_LikeN_T.findFirst(
			finderCache, new Object[] {groupId, name, themeId},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByG_LikeN_T_First(
		long groupId, String name, String themeId,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN_T.fetchFirst(
			finderCache, new Object[] {groupId, name, themeId},
			orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 */
	@Override
	public void removeByG_LikeN_T(long groupId, String name, String themeId) {
		_collectionPersistenceFinderByG_LikeN_T.remove(
			finderCache, new Object[] {groupId, name, themeId});
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByG_LikeN_T(long groupId, String name, String themeId) {
		return _collectionPersistenceFinderByG_LikeN_T.count(
			finderCache, new Object[] {groupId, name, themeId});
	}

	private CollectionPersistenceFinder
		<StyleBookEntryVersion, NoSuchEntryVersionException>
			_collectionPersistenceFinderByG_LikeN_T_Version;

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	@Override
	public List<StyleBookEntryVersion> findByG_LikeN_T_Version(
		long groupId, String name, String themeId, int version, int start,
		int end, OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN_T_Version.find(
			finderCache, new Object[] {groupId, name, themeId, version}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion findByG_LikeN_T_Version_First(
			long groupId, String name, String themeId, int version,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws NoSuchEntryVersionException {

		return _collectionPersistenceFinderByG_LikeN_T_Version.findFirst(
			finderCache, new Object[] {groupId, name, themeId, version},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByG_LikeN_T_Version_First(
		long groupId, String name, String themeId, int version,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN_T_Version.fetchFirst(
			finderCache, new Object[] {groupId, name, themeId, version},
			orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 */
	@Override
	public void removeByG_LikeN_T_Version(
		long groupId, String name, String themeId, int version) {

		_collectionPersistenceFinderByG_LikeN_T_Version.remove(
			finderCache, new Object[] {groupId, name, themeId, version});
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	@Override
	public int countByG_LikeN_T_Version(
		long groupId, String name, String themeId, int version) {

		return _collectionPersistenceFinderByG_LikeN_T_Version.count(
			finderCache, new Object[] {groupId, name, themeId, version});
	}

	public StyleBookEntryVersionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(StyleBookEntryVersion.class);

		setModelImplClass(StyleBookEntryVersionImpl.class);
		setModelPKClass(long.class);

		setTable(StyleBookEntryVersionTable.INSTANCE);
	}

	/**
	 * Creates a new style book entry version with the primary key. Does not add the style book entry version to the database.
	 *
	 * @param styleBookEntryVersionId the primary key for the new style book entry version
	 * @return the new style book entry version
	 */
	@Override
	public StyleBookEntryVersion create(long styleBookEntryVersionId) {
		StyleBookEntryVersion styleBookEntryVersion =
			new StyleBookEntryVersionImpl();

		styleBookEntryVersion.setNew(true);
		styleBookEntryVersion.setPrimaryKey(styleBookEntryVersionId);

		styleBookEntryVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return styleBookEntryVersion;
	}

	/**
	 * Removes the style book entry version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param styleBookEntryVersionId the primary key of the style book entry version
	 * @return the style book entry version that was removed
	 * @throws NoSuchEntryVersionException if a style book entry version with the primary key could not be found
	 */
	@Override
	public StyleBookEntryVersion remove(long styleBookEntryVersionId)
		throws NoSuchEntryVersionException {

		return remove((Serializable)styleBookEntryVersionId);
	}

	@Override
	protected StyleBookEntryVersion removeImpl(
		StyleBookEntryVersion styleBookEntryVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(styleBookEntryVersion)) {
				styleBookEntryVersion = (StyleBookEntryVersion)session.get(
					StyleBookEntryVersionImpl.class,
					styleBookEntryVersion.getPrimaryKeyObj());
			}

			if ((styleBookEntryVersion != null) &&
				ctPersistenceHelper.isRemove(styleBookEntryVersion)) {

				session.delete(styleBookEntryVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (styleBookEntryVersion != null) {
			clearCache(styleBookEntryVersion);
		}

		return styleBookEntryVersion;
	}

	@Override
	public StyleBookEntryVersion updateImpl(
		StyleBookEntryVersion styleBookEntryVersion) {

		boolean isNew = styleBookEntryVersion.isNew();

		if (!(styleBookEntryVersion instanceof
				StyleBookEntryVersionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(styleBookEntryVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					styleBookEntryVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in styleBookEntryVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom StyleBookEntryVersion implementation " +
					styleBookEntryVersion.getClass());
		}

		StyleBookEntryVersionModelImpl styleBookEntryVersionModelImpl =
			(StyleBookEntryVersionModelImpl)styleBookEntryVersion;

		if (Validator.isNull(
				styleBookEntryVersion.getExternalReferenceCode())) {

			styleBookEntryVersion.setExternalReferenceCode(
				String.valueOf(styleBookEntryVersion.getPrimaryKey()));
		}
		else {
			if (!Objects.equals(
					styleBookEntryVersionModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					styleBookEntryVersion.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = styleBookEntryVersion.getCompanyId();

					long groupId = styleBookEntryVersion.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = styleBookEntryVersion.getPrimaryKey();
					}

					try {
						styleBookEntryVersion.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								StyleBookEntryVersion.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								styleBookEntryVersion.
									getExternalReferenceCode(),
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

		if (isNew && (styleBookEntryVersion.getCreateDate() == null)) {
			if (serviceContext == null) {
				styleBookEntryVersion.setCreateDate(date);
			}
			else {
				styleBookEntryVersion.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!styleBookEntryVersionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				styleBookEntryVersion.setModifiedDate(date);
			}
			else {
				styleBookEntryVersion.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(styleBookEntryVersion)) {
				if (!isNew) {
					session.evict(
						StyleBookEntryVersionImpl.class,
						styleBookEntryVersion.getPrimaryKeyObj());
				}

				session.save(styleBookEntryVersion);
			}
			else {
				throw new IllegalArgumentException(
					"StyleBookEntryVersion is read only, create a new version instead");
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(styleBookEntryVersion, false);

		if (isNew) {
			styleBookEntryVersion.setNew(false);
		}

		styleBookEntryVersion.resetOriginalValues();

		return styleBookEntryVersion;
	}

	/**
	 * Returns the style book entry version with the primary key or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param styleBookEntryVersionId the primary key of the style book entry version
	 * @return the style book entry version
	 * @throws NoSuchEntryVersionException if a style book entry version with the primary key could not be found
	 */
	@Override
	public StyleBookEntryVersion findByPrimaryKey(long styleBookEntryVersionId)
		throws NoSuchEntryVersionException {

		return findByPrimaryKey((Serializable)styleBookEntryVersionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the style book entry version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param styleBookEntryVersionId the primary key of the style book entry version
	 * @return the style book entry version, or <code>null</code> if a style book entry version with the primary key could not be found
	 */
	@Override
	public StyleBookEntryVersion fetchByPrimaryKey(
		long styleBookEntryVersionId) {

		return fetchByPrimaryKey((Serializable)styleBookEntryVersionId);
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
		return "styleBookEntryVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_STYLEBOOKENTRYVERSION;
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
		return StyleBookEntryVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "StyleBookEntryVersion";
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
		ctMergeColumnNames.add("styleBookEntryId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("defaultStyleBookEntry");
		ctMergeColumnNames.add("frontendTokensValues");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("previewFileEntryId");
		ctMergeColumnNames.add("styleBookEntryKey");
		ctMergeColumnNames.add("themeId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("styleBookEntryVersionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"styleBookEntryId", "version"});

		_uniqueIndexColumnNames.add(
			new String[] {"uuid_", "groupId", "version"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "styleBookEntryKey", "version"});
	}

	/**
	 * Initializes the style book entry version persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByStyleBookEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByStyleBookEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"styleBookEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByStyleBookEntryId",
					new String[] {Long.class.getName()},
					new String[] {"styleBookEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByStyleBookEntryId",
					new String[] {Long.class.getName()},
					new String[] {"styleBookEntryId"}, false),
				_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
				_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
				StyleBookEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"styleBookEntryVersion.", "styleBookEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					StyleBookEntryVersion::getStyleBookEntryId));

		_uniquePersistenceFinderByStyleBookEntryId_Version =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByStyleBookEntryId_Version",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"styleBookEntryId", "version"}, 0, 0, false,
					StyleBookEntryVersion::getStyleBookEntryId,
					StyleBookEntryVersion::getVersion),
				_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE, "",
				new FinderColumn<>(
					"styleBookEntryVersion.", "styleBookEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					StyleBookEntryVersion::getStyleBookEntryId),
				new FinderColumn<>(
					"styleBookEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					StyleBookEntryVersion::getVersion));

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
			_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
			_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
			StyleBookEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"styleBookEntryVersion.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				StyleBookEntryVersion::getUuid));

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
				_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
				_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
				StyleBookEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"styleBookEntryVersion.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					StyleBookEntryVersion::getUuid),
				new FinderColumn<>(
					"styleBookEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					StyleBookEntryVersion::getVersion));

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
				_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
				_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
				StyleBookEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"styleBookEntryVersion.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					StyleBookEntryVersion::getUuid),
				new FinderColumn<>(
					"styleBookEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, StyleBookEntryVersion::getGroupId));

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
					convertNullFunction(StyleBookEntryVersion::getUuid),
					StyleBookEntryVersion::getGroupId,
					StyleBookEntryVersion::getVersion),
				_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE, "",
				new FinderColumn<>(
					"styleBookEntryVersion.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					StyleBookEntryVersion::getUuid),
				new FinderColumn<>(
					"styleBookEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, StyleBookEntryVersion::getGroupId),
				new FinderColumn<>(
					"styleBookEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					StyleBookEntryVersion::getVersion));

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
				_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
				_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
				StyleBookEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"styleBookEntryVersion.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					StyleBookEntryVersion::getUuid),
				new FinderColumn<>(
					"styleBookEntryVersion.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					StyleBookEntryVersion::getCompanyId));

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
				_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
				_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
				StyleBookEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"styleBookEntryVersion.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					StyleBookEntryVersion::getUuid),
				new FinderColumn<>(
					"styleBookEntryVersion.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					StyleBookEntryVersion::getCompanyId),
				new FinderColumn<>(
					"styleBookEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					StyleBookEntryVersion::getVersion));

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
				_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
				_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
				StyleBookEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"styleBookEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, StyleBookEntryVersion::getGroupId));

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
				_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
				_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
				StyleBookEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"styleBookEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, StyleBookEntryVersion::getGroupId),
				new FinderColumn<>(
					"styleBookEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					StyleBookEntryVersion::getVersion));

		_collectionPersistenceFinderByG_D = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_D",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "defaultStyleBookEntry"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_D",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"groupId", "defaultStyleBookEntry"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_D",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"groupId", "defaultStyleBookEntry"}, false),
			_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
			_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
			StyleBookEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"styleBookEntryVersion.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, StyleBookEntryVersion::getGroupId),
			new FinderColumn<>(
				"styleBookEntryVersion.", "defaultStyleBookEntry",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				StyleBookEntryVersion::isDefaultStyleBookEntry));

		_collectionPersistenceFinderByG_D_Version =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_D_Version",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "defaultStyleBookEntry", "version"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_D_Version",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "defaultStyleBookEntry", "version"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_D_Version",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "defaultStyleBookEntry", "version"
					},
					false),
				_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
				_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
				StyleBookEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"styleBookEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, StyleBookEntryVersion::getGroupId),
				new FinderColumn<>(
					"styleBookEntryVersion.", "defaultStyleBookEntry",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					StyleBookEntryVersion::isDefaultStyleBookEntry),
				new FinderColumn<>(
					"styleBookEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					StyleBookEntryVersion::getVersion));

		_collectionPersistenceFinderByG_N = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "name"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "name"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "name"}, 0, 2, false, null),
			_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
			_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
			StyleBookEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"styleBookEntryVersion.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, StyleBookEntryVersion::getGroupId),
			new FinderColumn<>(
				"styleBookEntryVersion.", "name", FinderColumn.Type.STRING, "=",
				true, true, StyleBookEntryVersion::getName));

		_collectionPersistenceFinderByG_N_Version =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N_Version",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name", "version"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_N_Version",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "name", "version"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_N_Version",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "name", "version"}, 0, 2, false,
					null),
				_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
				_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
				StyleBookEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"styleBookEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, StyleBookEntryVersion::getGroupId),
				new FinderColumn<>(
					"styleBookEntryVersion.", "name", FinderColumn.Type.STRING,
					"=", true, true, StyleBookEntryVersion::getName),
				new FinderColumn<>(
					"styleBookEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					StyleBookEntryVersion::getVersion));

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
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_LikeN",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "name"}, 0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_LikeN",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "name"}, 0, 2, false, null),
				_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
				_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
				StyleBookEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"styleBookEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, StyleBookEntryVersion::getGroupId),
				new FinderColumn<>(
					"styleBookEntryVersion.", "name", FinderColumn.Type.STRING,
					"=", true, true, StyleBookEntryVersion::getName));

		_collectionPersistenceFinderByG_LikeN_Version =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_LikeN_Version",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name", "version"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_LikeN_Version",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "name", "version"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_LikeN_Version",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "name", "version"}, 0, 2, false,
					null),
				_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
				_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
				StyleBookEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"styleBookEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, StyleBookEntryVersion::getGroupId),
				new FinderColumn<>(
					"styleBookEntryVersion.", "name", FinderColumn.Type.STRING,
					"=", true, true, StyleBookEntryVersion::getName),
				new FinderColumn<>(
					"styleBookEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					StyleBookEntryVersion::getVersion));

		_collectionPersistenceFinderByG_SBEK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_SBEK",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "styleBookEntryKey"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_SBEK",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "styleBookEntryKey"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_SBEK",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "styleBookEntryKey"}, 0, 2, false,
					null),
				_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
				_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
				StyleBookEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"styleBookEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, StyleBookEntryVersion::getGroupId),
				new FinderColumn<>(
					"styleBookEntryVersion.", "styleBookEntryKey",
					FinderColumn.Type.STRING, "=", true, true,
					StyleBookEntryVersion::getStyleBookEntryKey));

		_uniquePersistenceFinderByG_SBEK_Version =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByG_SBEK_Version",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "styleBookEntryKey", "version"}, 0,
					2, false, StyleBookEntryVersion::getGroupId,
					convertNullFunction(
						StyleBookEntryVersion::getStyleBookEntryKey),
					StyleBookEntryVersion::getVersion),
				_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE, "",
				new FinderColumn<>(
					"styleBookEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, StyleBookEntryVersion::getGroupId),
				new FinderColumn<>(
					"styleBookEntryVersion.", "styleBookEntryKey",
					FinderColumn.Type.STRING, "=", true, true,
					StyleBookEntryVersion::getStyleBookEntryKey),
				new FinderColumn<>(
					"styleBookEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					StyleBookEntryVersion::getVersion));

		_collectionPersistenceFinderByG_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "themeId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "themeId"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_T",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "themeId"}, 0, 2, false, null),
			_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
			_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
			StyleBookEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"styleBookEntryVersion.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, StyleBookEntryVersion::getGroupId),
			new FinderColumn<>(
				"styleBookEntryVersion.", "themeId", FinderColumn.Type.STRING,
				"=", true, true, StyleBookEntryVersion::getThemeId));

		_collectionPersistenceFinderByG_T_Version =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T_Version",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "themeId", "version"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_T_Version",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "themeId", "version"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_T_Version",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "themeId", "version"}, 0, 2, false,
					null),
				_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
				_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
				StyleBookEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"styleBookEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, StyleBookEntryVersion::getGroupId),
				new FinderColumn<>(
					"styleBookEntryVersion.", "themeId",
					FinderColumn.Type.STRING, "=", true, true,
					StyleBookEntryVersion::getThemeId),
				new FinderColumn<>(
					"styleBookEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					StyleBookEntryVersion::getVersion));

		_collectionPersistenceFinderByG_D_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_D_T",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "defaultStyleBookEntry", "themeId"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_D_T",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "defaultStyleBookEntry", "themeId"}, 0,
				4, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_D_T",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "defaultStyleBookEntry", "themeId"}, 0,
				4, false, null),
			_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
			_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
			StyleBookEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"styleBookEntryVersion.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, StyleBookEntryVersion::getGroupId),
			new FinderColumn<>(
				"styleBookEntryVersion.", "defaultStyleBookEntry",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				StyleBookEntryVersion::isDefaultStyleBookEntry),
			new FinderColumn<>(
				"styleBookEntryVersion.", "themeId", FinderColumn.Type.STRING,
				"=", true, true, StyleBookEntryVersion::getThemeId));

		_collectionPersistenceFinderByG_D_T_Version =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_D_T_Version",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "defaultStyleBookEntry", "themeId", "version"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_D_T_Version",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "defaultStyleBookEntry", "themeId", "version"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_D_T_Version",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "defaultStyleBookEntry", "themeId", "version"
					},
					0, 4, false, null),
				_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
				_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
				StyleBookEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"styleBookEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, StyleBookEntryVersion::getGroupId),
				new FinderColumn<>(
					"styleBookEntryVersion.", "defaultStyleBookEntry",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					StyleBookEntryVersion::isDefaultStyleBookEntry),
				new FinderColumn<>(
					"styleBookEntryVersion.", "themeId",
					FinderColumn.Type.STRING, "=", true, true,
					StyleBookEntryVersion::getThemeId),
				new FinderColumn<>(
					"styleBookEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					StyleBookEntryVersion::getVersion));

		_collectionPersistenceFinderByG_LikeN_T =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeN_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name", "themeId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_LikeN_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {"groupId", "name", "themeId"}, 0, 6, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_LikeN_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {"groupId", "name", "themeId"}, 0, 6, false,
					null),
				_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
				_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
				StyleBookEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"styleBookEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, StyleBookEntryVersion::getGroupId),
				new FinderColumn<>(
					"styleBookEntryVersion.", "name", FinderColumn.Type.STRING,
					"=", true, true, StyleBookEntryVersion::getName),
				new FinderColumn<>(
					"styleBookEntryVersion.", "themeId",
					FinderColumn.Type.STRING, "=", true, true,
					StyleBookEntryVersion::getThemeId));

		_collectionPersistenceFinderByG_LikeN_T_Version =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_LikeN_T_Version",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name", "themeId", "version"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_LikeN_T_Version",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "name", "themeId", "version"}, 0,
					6, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_LikeN_T_Version",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "name", "themeId", "version"}, 0,
					6, false, null),
				_SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE,
				_SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE,
				StyleBookEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"styleBookEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, StyleBookEntryVersion::getGroupId),
				new FinderColumn<>(
					"styleBookEntryVersion.", "name", FinderColumn.Type.STRING,
					"=", true, true, StyleBookEntryVersion::getName),
				new FinderColumn<>(
					"styleBookEntryVersion.", "themeId",
					FinderColumn.Type.STRING, "=", true, true,
					StyleBookEntryVersion::getThemeId),
				new FinderColumn<>(
					"styleBookEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					StyleBookEntryVersion::getVersion));

		StyleBookEntryVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		StyleBookEntryVersionUtil.setPersistence(null);

		entityCache.removeCache(StyleBookEntryVersionImpl.class.getName());
	}

	@Override
	@Reference(
		target = StyleBookPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = StyleBookPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = StyleBookPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		StyleBookEntryVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_STYLEBOOKENTRYVERSION =
		"SELECT styleBookEntryVersion FROM StyleBookEntryVersion styleBookEntryVersion";

	private static final String _SQL_SELECT_STYLEBOOKENTRYVERSION_WHERE =
		"SELECT styleBookEntryVersion FROM StyleBookEntryVersion styleBookEntryVersion WHERE ";

	private static final String _SQL_COUNT_STYLEBOOKENTRYVERSION_WHERE =
		"SELECT COUNT(styleBookEntryVersion) FROM StyleBookEntryVersion styleBookEntryVersion WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-367747365