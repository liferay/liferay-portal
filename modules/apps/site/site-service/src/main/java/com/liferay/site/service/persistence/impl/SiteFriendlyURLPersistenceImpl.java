/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.site.exception.NoSuchFriendlyURLException;
import com.liferay.site.model.SiteFriendlyURL;
import com.liferay.site.model.SiteFriendlyURLTable;
import com.liferay.site.model.impl.SiteFriendlyURLImpl;
import com.liferay.site.model.impl.SiteFriendlyURLModelImpl;
import com.liferay.site.service.persistence.SiteFriendlyURLPersistence;
import com.liferay.site.service.persistence.SiteFriendlyURLUtil;
import com.liferay.site.service.persistence.impl.constants.SitePersistenceConstants;

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
 * The persistence implementation for the site friendly url service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = SiteFriendlyURLPersistence.class)
public class SiteFriendlyURLPersistenceImpl
	extends BasePersistenceImpl<SiteFriendlyURL, NoSuchFriendlyURLException>
	implements SiteFriendlyURLPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SiteFriendlyURLUtil</code> to access the site friendly url persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SiteFriendlyURLImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SiteFriendlyURL, NoSuchFriendlyURLException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the site friendly urls where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of site friendly urls
	 * @param end the upper bound of the range of site friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site friendly urls
	 */
	@Override
	public List<SiteFriendlyURL> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SiteFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first site friendly url in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site friendly url
	 * @throws NoSuchFriendlyURLException if a matching site friendly url could not be found
	 */
	@Override
	public SiteFriendlyURL findByUuid_First(
			String uuid, OrderByComparator<SiteFriendlyURL> orderByComparator)
		throws NoSuchFriendlyURLException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first site friendly url in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site friendly url, or <code>null</code> if a matching site friendly url could not be found
	 */
	@Override
	public SiteFriendlyURL fetchByUuid_First(
		String uuid, OrderByComparator<SiteFriendlyURL> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the site friendly urls where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of site friendly urls where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching site friendly urls
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<SiteFriendlyURL, NoSuchFriendlyURLException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the site friendly url where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFriendlyURLException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching site friendly url
	 * @throws NoSuchFriendlyURLException if a matching site friendly url could not be found
	 */
	@Override
	public SiteFriendlyURL findByUUID_G(String uuid, long groupId)
		throws NoSuchFriendlyURLException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the site friendly url where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching site friendly url, or <code>null</code> if a matching site friendly url could not be found
	 */
	@Override
	public SiteFriendlyURL fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the site friendly url where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the site friendly url that was removed
	 */
	@Override
	public SiteFriendlyURL removeByUUID_G(String uuid, long groupId)
		throws NoSuchFriendlyURLException {

		SiteFriendlyURL siteFriendlyURL = findByUUID_G(uuid, groupId);

		return remove(siteFriendlyURL);
	}

	/**
	 * Returns the number of site friendly urls where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching site friendly urls
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<SiteFriendlyURL, NoSuchFriendlyURLException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the site friendly urls where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of site friendly urls
	 * @param end the upper bound of the range of site friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site friendly urls
	 */
	@Override
	public List<SiteFriendlyURL> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SiteFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first site friendly url in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site friendly url
	 * @throws NoSuchFriendlyURLException if a matching site friendly url could not be found
	 */
	@Override
	public SiteFriendlyURL findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<SiteFriendlyURL> orderByComparator)
		throws NoSuchFriendlyURLException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first site friendly url in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site friendly url, or <code>null</code> if a matching site friendly url could not be found
	 */
	@Override
	public SiteFriendlyURL fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<SiteFriendlyURL> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the site friendly urls where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of site friendly urls where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching site friendly urls
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<SiteFriendlyURL, NoSuchFriendlyURLException>
			_collectionPersistenceFinderByG_C;

	/**
	 * Returns an ordered range of all the site friendly urls where groupId = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param start the lower bound of the range of site friendly urls
	 * @param end the upper bound of the range of site friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site friendly urls
	 */
	@Override
	public List<SiteFriendlyURL> findByG_C(
		long groupId, long companyId, int start, int end,
		OrderByComparator<SiteFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C.find(
			finderCache, new Object[] {groupId, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first site friendly url in the ordered set where groupId = &#63; and companyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site friendly url
	 * @throws NoSuchFriendlyURLException if a matching site friendly url could not be found
	 */
	@Override
	public SiteFriendlyURL findByG_C_First(
			long groupId, long companyId,
			OrderByComparator<SiteFriendlyURL> orderByComparator)
		throws NoSuchFriendlyURLException {

		return _collectionPersistenceFinderByG_C.findFirst(
			finderCache, new Object[] {groupId, companyId}, orderByComparator);
	}

	/**
	 * Returns the first site friendly url in the ordered set where groupId = &#63; and companyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site friendly url, or <code>null</code> if a matching site friendly url could not be found
	 */
	@Override
	public SiteFriendlyURL fetchByG_C_First(
		long groupId, long companyId,
		OrderByComparator<SiteFriendlyURL> orderByComparator) {

		return _collectionPersistenceFinderByG_C.fetchFirst(
			finderCache, new Object[] {groupId, companyId}, orderByComparator);
	}

	/**
	 * Removes all the site friendly urls where groupId = &#63; and companyId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 */
	@Override
	public void removeByG_C(long groupId, long companyId) {
		_collectionPersistenceFinderByG_C.remove(
			finderCache, new Object[] {groupId, companyId});
	}

	/**
	 * Returns the number of site friendly urls where groupId = &#63; and companyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @return the number of matching site friendly urls
	 */
	@Override
	public int countByG_C(long groupId, long companyId) {
		return _collectionPersistenceFinderByG_C.count(
			finderCache, new Object[] {groupId, companyId});
	}

	private UniquePersistenceFinder<SiteFriendlyURL, NoSuchFriendlyURLException>
		_uniquePersistenceFinderByC_F;

	/**
	 * Returns the site friendly url where companyId = &#63; and friendlyURL = &#63; or throws a <code>NoSuchFriendlyURLException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the matching site friendly url
	 * @throws NoSuchFriendlyURLException if a matching site friendly url could not be found
	 */
	@Override
	public SiteFriendlyURL findByC_F(long companyId, String friendlyURL)
		throws NoSuchFriendlyURLException {

		return _uniquePersistenceFinderByC_F.find(
			finderCache, new Object[] {companyId, friendlyURL});
	}

	/**
	 * Returns the site friendly url where companyId = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching site friendly url, or <code>null</code> if a matching site friendly url could not be found
	 */
	@Override
	public SiteFriendlyURL fetchByC_F(
		long companyId, String friendlyURL, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_F.fetch(
			finderCache, new Object[] {companyId, friendlyURL}, useFinderCache);
	}

	/**
	 * Removes the site friendly url where companyId = &#63; and friendlyURL = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the site friendly url that was removed
	 */
	@Override
	public SiteFriendlyURL removeByC_F(long companyId, String friendlyURL)
		throws NoSuchFriendlyURLException {

		SiteFriendlyURL siteFriendlyURL = findByC_F(companyId, friendlyURL);

		return remove(siteFriendlyURL);
	}

	/**
	 * Returns the number of site friendly urls where companyId = &#63; and friendlyURL = &#63;.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the number of matching site friendly urls
	 */
	@Override
	public int countByC_F(long companyId, String friendlyURL) {
		return _uniquePersistenceFinderByC_F.count(
			finderCache, new Object[] {companyId, friendlyURL});
	}

	private UniquePersistenceFinder<SiteFriendlyURL, NoSuchFriendlyURLException>
		_uniquePersistenceFinderByG_C_L;

	/**
	 * Returns the site friendly url where groupId = &#63; and companyId = &#63; and languageId = &#63; or throws a <code>NoSuchFriendlyURLException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param languageId the language ID
	 * @return the matching site friendly url
	 * @throws NoSuchFriendlyURLException if a matching site friendly url could not be found
	 */
	@Override
	public SiteFriendlyURL findByG_C_L(
			long groupId, long companyId, String languageId)
		throws NoSuchFriendlyURLException {

		return _uniquePersistenceFinderByG_C_L.find(
			finderCache, new Object[] {groupId, companyId, languageId});
	}

	/**
	 * Returns the site friendly url where groupId = &#63; and companyId = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching site friendly url, or <code>null</code> if a matching site friendly url could not be found
	 */
	@Override
	public SiteFriendlyURL fetchByG_C_L(
		long groupId, long companyId, String languageId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_C_L.fetch(
			finderCache, new Object[] {groupId, companyId, languageId},
			useFinderCache);
	}

	/**
	 * Removes the site friendly url where groupId = &#63; and companyId = &#63; and languageId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param languageId the language ID
	 * @return the site friendly url that was removed
	 */
	@Override
	public SiteFriendlyURL removeByG_C_L(
			long groupId, long companyId, String languageId)
		throws NoSuchFriendlyURLException {

		SiteFriendlyURL siteFriendlyURL = findByG_C_L(
			groupId, companyId, languageId);

		return remove(siteFriendlyURL);
	}

	/**
	 * Returns the number of site friendly urls where groupId = &#63; and companyId = &#63; and languageId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param languageId the language ID
	 * @return the number of matching site friendly urls
	 */
	@Override
	public int countByG_C_L(long groupId, long companyId, String languageId) {
		return _uniquePersistenceFinderByG_C_L.count(
			finderCache, new Object[] {groupId, companyId, languageId});
	}

	private UniquePersistenceFinder<SiteFriendlyURL, NoSuchFriendlyURLException>
		_uniquePersistenceFinderByC_F_L;

	/**
	 * Returns the site friendly url where companyId = &#63; and friendlyURL = &#63; and languageId = &#63; or throws a <code>NoSuchFriendlyURLException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @param languageId the language ID
	 * @return the matching site friendly url
	 * @throws NoSuchFriendlyURLException if a matching site friendly url could not be found
	 */
	@Override
	public SiteFriendlyURL findByC_F_L(
			long companyId, String friendlyURL, String languageId)
		throws NoSuchFriendlyURLException {

		return _uniquePersistenceFinderByC_F_L.find(
			finderCache, new Object[] {companyId, friendlyURL, languageId});
	}

	/**
	 * Returns the site friendly url where companyId = &#63; and friendlyURL = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching site friendly url, or <code>null</code> if a matching site friendly url could not be found
	 */
	@Override
	public SiteFriendlyURL fetchByC_F_L(
		long companyId, String friendlyURL, String languageId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_F_L.fetch(
			finderCache, new Object[] {companyId, friendlyURL, languageId},
			useFinderCache);
	}

	/**
	 * Removes the site friendly url where companyId = &#63; and friendlyURL = &#63; and languageId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @param languageId the language ID
	 * @return the site friendly url that was removed
	 */
	@Override
	public SiteFriendlyURL removeByC_F_L(
			long companyId, String friendlyURL, String languageId)
		throws NoSuchFriendlyURLException {

		SiteFriendlyURL siteFriendlyURL = findByC_F_L(
			companyId, friendlyURL, languageId);

		return remove(siteFriendlyURL);
	}

	/**
	 * Returns the number of site friendly urls where companyId = &#63; and friendlyURL = &#63; and languageId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @param languageId the language ID
	 * @return the number of matching site friendly urls
	 */
	@Override
	public int countByC_F_L(
		long companyId, String friendlyURL, String languageId) {

		return _uniquePersistenceFinderByC_F_L.count(
			finderCache, new Object[] {companyId, friendlyURL, languageId});
	}

	public SiteFriendlyURLPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(SiteFriendlyURL.class);

		setModelImplClass(SiteFriendlyURLImpl.class);
		setModelPKClass(long.class);

		setTable(SiteFriendlyURLTable.INSTANCE);
	}

	/**
	 * Creates a new site friendly url with the primary key. Does not add the site friendly url to the database.
	 *
	 * @param siteFriendlyURLId the primary key for the new site friendly url
	 * @return the new site friendly url
	 */
	@Override
	public SiteFriendlyURL create(long siteFriendlyURLId) {
		SiteFriendlyURL siteFriendlyURL = new SiteFriendlyURLImpl();

		siteFriendlyURL.setNew(true);
		siteFriendlyURL.setPrimaryKey(siteFriendlyURLId);

		String uuid = PortalUUIDUtil.generate();

		siteFriendlyURL.setUuid(uuid);

		siteFriendlyURL.setCompanyId(CompanyThreadLocal.getCompanyId());

		return siteFriendlyURL;
	}

	/**
	 * Removes the site friendly url with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param siteFriendlyURLId the primary key of the site friendly url
	 * @return the site friendly url that was removed
	 * @throws NoSuchFriendlyURLException if a site friendly url with the primary key could not be found
	 */
	@Override
	public SiteFriendlyURL remove(long siteFriendlyURLId)
		throws NoSuchFriendlyURLException {

		return remove((Serializable)siteFriendlyURLId);
	}

	@Override
	protected SiteFriendlyURL removeImpl(SiteFriendlyURL siteFriendlyURL) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(siteFriendlyURL)) {
				siteFriendlyURL = (SiteFriendlyURL)session.get(
					SiteFriendlyURLImpl.class,
					siteFriendlyURL.getPrimaryKeyObj());
			}

			if (siteFriendlyURL != null) {
				session.delete(siteFriendlyURL);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (siteFriendlyURL != null) {
			clearCache(siteFriendlyURL);
		}

		return siteFriendlyURL;
	}

	@Override
	public SiteFriendlyURL updateImpl(SiteFriendlyURL siteFriendlyURL) {
		boolean isNew = siteFriendlyURL.isNew();

		if (!(siteFriendlyURL instanceof SiteFriendlyURLModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(siteFriendlyURL.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					siteFriendlyURL);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in siteFriendlyURL proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SiteFriendlyURL implementation " +
					siteFriendlyURL.getClass());
		}

		SiteFriendlyURLModelImpl siteFriendlyURLModelImpl =
			(SiteFriendlyURLModelImpl)siteFriendlyURL;

		if (Validator.isNull(siteFriendlyURL.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			siteFriendlyURL.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (siteFriendlyURL.getCreateDate() == null)) {
			if (serviceContext == null) {
				siteFriendlyURL.setCreateDate(date);
			}
			else {
				siteFriendlyURL.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!siteFriendlyURLModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				siteFriendlyURL.setModifiedDate(date);
			}
			else {
				siteFriendlyURL.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(siteFriendlyURL);
			}
			else {
				siteFriendlyURL = (SiteFriendlyURL)session.merge(
					siteFriendlyURL);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(siteFriendlyURL, false);

		if (isNew) {
			siteFriendlyURL.setNew(false);
		}

		siteFriendlyURL.resetOriginalValues();

		return siteFriendlyURL;
	}

	/**
	 * Returns the site friendly url with the primary key or throws a <code>NoSuchFriendlyURLException</code> if it could not be found.
	 *
	 * @param siteFriendlyURLId the primary key of the site friendly url
	 * @return the site friendly url
	 * @throws NoSuchFriendlyURLException if a site friendly url with the primary key could not be found
	 */
	@Override
	public SiteFriendlyURL findByPrimaryKey(long siteFriendlyURLId)
		throws NoSuchFriendlyURLException {

		return findByPrimaryKey((Serializable)siteFriendlyURLId);
	}

	/**
	 * Returns the site friendly url with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param siteFriendlyURLId the primary key of the site friendly url
	 * @return the site friendly url, or <code>null</code> if a site friendly url with the primary key could not be found
	 */
	@Override
	public SiteFriendlyURL fetchByPrimaryKey(long siteFriendlyURLId) {
		return fetchByPrimaryKey((Serializable)siteFriendlyURLId);
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
		return "siteFriendlyURLId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SITEFRIENDLYURL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SiteFriendlyURLModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the site friendly url persistence.
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
			_SQL_SELECT_SITEFRIENDLYURL_WHERE, _SQL_COUNT_SITEFRIENDLYURL_WHERE,
			SiteFriendlyURLModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"siteFriendlyURL.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, SiteFriendlyURL::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(SiteFriendlyURL::getUuid),
				SiteFriendlyURL::getGroupId),
			_SQL_SELECT_SITEFRIENDLYURL_WHERE, "",
			new FinderColumn<>(
				"siteFriendlyURL.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, SiteFriendlyURL::getUuid),
			new FinderColumn<>(
				"siteFriendlyURL.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, SiteFriendlyURL::getGroupId));

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
				_SQL_SELECT_SITEFRIENDLYURL_WHERE,
				_SQL_COUNT_SITEFRIENDLYURL_WHERE,
				SiteFriendlyURLModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"siteFriendlyURL.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					SiteFriendlyURL::getUuid),
				new FinderColumn<>(
					"siteFriendlyURL.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, SiteFriendlyURL::getCompanyId));

		_collectionPersistenceFinderByG_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "companyId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "companyId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "companyId"}, false),
			_SQL_SELECT_SITEFRIENDLYURL_WHERE, _SQL_COUNT_SITEFRIENDLYURL_WHERE,
			SiteFriendlyURLModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"siteFriendlyURL.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, SiteFriendlyURL::getGroupId),
			new FinderColumn<>(
				"siteFriendlyURL.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, SiteFriendlyURL::getCompanyId));

		_uniquePersistenceFinderByC_F = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_F",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "friendlyURL"}, 0, 2, false,
				SiteFriendlyURL::getCompanyId,
				convertNullFunction(SiteFriendlyURL::getFriendlyURL)),
			_SQL_SELECT_SITEFRIENDLYURL_WHERE, "",
			new FinderColumn<>(
				"siteFriendlyURL.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, SiteFriendlyURL::getCompanyId),
			new FinderColumn<>(
				"siteFriendlyURL.", "friendlyURL", FinderColumn.Type.STRING,
				"=", true, true, SiteFriendlyURL::getFriendlyURL));

		_uniquePersistenceFinderByG_C_L = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_C_L",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "companyId", "languageId"}, 0, 4,
				false, SiteFriendlyURL::getGroupId,
				SiteFriendlyURL::getCompanyId,
				convertNullFunction(SiteFriendlyURL::getLanguageId)),
			_SQL_SELECT_SITEFRIENDLYURL_WHERE, "",
			new FinderColumn<>(
				"siteFriendlyURL.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, SiteFriendlyURL::getGroupId),
			new FinderColumn<>(
				"siteFriendlyURL.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, SiteFriendlyURL::getCompanyId),
			new FinderColumn<>(
				"siteFriendlyURL.", "languageId", FinderColumn.Type.STRING, "=",
				true, true, SiteFriendlyURL::getLanguageId));

		_uniquePersistenceFinderByC_F_L = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_F_L",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "friendlyURL", "languageId"}, 0, 6,
				false, SiteFriendlyURL::getCompanyId,
				convertNullFunction(SiteFriendlyURL::getFriendlyURL),
				convertNullFunction(SiteFriendlyURL::getLanguageId)),
			_SQL_SELECT_SITEFRIENDLYURL_WHERE, "",
			new FinderColumn<>(
				"siteFriendlyURL.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, SiteFriendlyURL::getCompanyId),
			new FinderColumn<>(
				"siteFriendlyURL.", "friendlyURL", FinderColumn.Type.STRING,
				"=", true, true, SiteFriendlyURL::getFriendlyURL),
			new FinderColumn<>(
				"siteFriendlyURL.", "languageId", FinderColumn.Type.STRING, "=",
				true, true, SiteFriendlyURL::getLanguageId));

		SiteFriendlyURLUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SiteFriendlyURLUtil.setPersistence(null);

		entityCache.removeCache(SiteFriendlyURLImpl.class.getName());
	}

	@Override
	@Reference(
		target = SitePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SitePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SitePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		SiteFriendlyURLModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SITEFRIENDLYURL =
		"SELECT siteFriendlyURL FROM SiteFriendlyURL siteFriendlyURL";

	private static final String _SQL_SELECT_SITEFRIENDLYURL_WHERE =
		"SELECT siteFriendlyURL FROM SiteFriendlyURL siteFriendlyURL WHERE ";

	private static final String _SQL_COUNT_SITEFRIENDLYURL_WHERE =
		"SELECT COUNT(siteFriendlyURL) FROM SiteFriendlyURL siteFriendlyURL WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-311113881