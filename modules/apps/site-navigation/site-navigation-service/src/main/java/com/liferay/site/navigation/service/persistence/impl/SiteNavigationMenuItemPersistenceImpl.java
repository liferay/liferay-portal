/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.site.navigation.exception.DuplicateSiteNavigationMenuItemExternalReferenceCodeException;
import com.liferay.site.navigation.exception.NoSuchMenuItemException;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.model.SiteNavigationMenuItemTable;
import com.liferay.site.navigation.model.impl.SiteNavigationMenuItemImpl;
import com.liferay.site.navigation.model.impl.SiteNavigationMenuItemModelImpl;
import com.liferay.site.navigation.service.persistence.SiteNavigationMenuItemPersistence;
import com.liferay.site.navigation.service.persistence.SiteNavigationMenuItemUtil;
import com.liferay.site.navigation.service.persistence.impl.constants.SiteNavigationPersistenceConstants;

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
 * The persistence implementation for the site navigation menu item service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = SiteNavigationMenuItemPersistence.class)
public class SiteNavigationMenuItemPersistenceImpl
	extends BasePersistenceImpl<SiteNavigationMenuItem, NoSuchMenuItemException>
	implements SiteNavigationMenuItemPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SiteNavigationMenuItemUtil</code> to access the site navigation menu item persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SiteNavigationMenuItemImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SiteNavigationMenuItem, NoSuchMenuItemException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the site navigation menu items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site navigation menu items
	 */
	@Override
	public List<SiteNavigationMenuItem> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem findByUuid_First(
			String uuid,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws NoSuchMenuItemException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem fetchByUuid_First(
		String uuid,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the site navigation menu items where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of site navigation menu items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching site navigation menu items
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<SiteNavigationMenuItem, NoSuchMenuItemException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the site navigation menu item where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchMenuItemException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem findByUUID_G(String uuid, long groupId)
		throws NoSuchMenuItemException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the site navigation menu item where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the site navigation menu item where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the site navigation menu item that was removed
	 */
	@Override
	public SiteNavigationMenuItem removeByUUID_G(String uuid, long groupId)
		throws NoSuchMenuItemException {

		SiteNavigationMenuItem siteNavigationMenuItem = findByUUID_G(
			uuid, groupId);

		return remove(siteNavigationMenuItem);
	}

	/**
	 * Returns the number of site navigation menu items where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching site navigation menu items
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<SiteNavigationMenuItem, NoSuchMenuItemException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the site navigation menu items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site navigation menu items
	 */
	@Override
	public List<SiteNavigationMenuItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws NoSuchMenuItemException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the site navigation menu items where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of site navigation menu items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching site navigation menu items
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<SiteNavigationMenuItem, NoSuchMenuItemException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the site navigation menu items where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site navigation menu items
	 */
	@Override
	public List<SiteNavigationMenuItem> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem findByCompanyId_First(
			long companyId,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws NoSuchMenuItemException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem fetchByCompanyId_First(
		long companyId,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the site navigation menu items where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of site navigation menu items where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching site navigation menu items
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<SiteNavigationMenuItem, NoSuchMenuItemException>
			_collectionPersistenceFinderBySiteNavigationMenuId;

	/**
	 * Returns an ordered range of all the site navigation menu items where siteNavigationMenuId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site navigation menu items
	 */
	@Override
	public List<SiteNavigationMenuItem> findBySiteNavigationMenuId(
		long siteNavigationMenuId, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySiteNavigationMenuId.find(
			finderCache, new Object[] {siteNavigationMenuId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where siteNavigationMenuId = &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem findBySiteNavigationMenuId_First(
			long siteNavigationMenuId,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws NoSuchMenuItemException {

		return _collectionPersistenceFinderBySiteNavigationMenuId.findFirst(
			finderCache, new Object[] {siteNavigationMenuId},
			orderByComparator);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where siteNavigationMenuId = &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem fetchBySiteNavigationMenuId_First(
		long siteNavigationMenuId,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return _collectionPersistenceFinderBySiteNavigationMenuId.fetchFirst(
			finderCache, new Object[] {siteNavigationMenuId},
			orderByComparator);
	}

	/**
	 * Removes all the site navigation menu items where siteNavigationMenuId = &#63; from the database.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 */
	@Override
	public void removeBySiteNavigationMenuId(long siteNavigationMenuId) {
		_collectionPersistenceFinderBySiteNavigationMenuId.remove(
			finderCache, new Object[] {siteNavigationMenuId});
	}

	/**
	 * Returns the number of site navigation menu items where siteNavigationMenuId = &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @return the number of matching site navigation menu items
	 */
	@Override
	public int countBySiteNavigationMenuId(long siteNavigationMenuId) {
		return _collectionPersistenceFinderBySiteNavigationMenuId.count(
			finderCache, new Object[] {siteNavigationMenuId});
	}

	private CollectionPersistenceFinder
		<SiteNavigationMenuItem, NoSuchMenuItemException>
			_collectionPersistenceFinderByParentSiteNavigationMenuItemId;

	/**
	 * Returns an ordered range of all the site navigation menu items where parentSiteNavigationMenuItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site navigation menu items
	 */
	@Override
	public List<SiteNavigationMenuItem> findByParentSiteNavigationMenuItemId(
		long parentSiteNavigationMenuItemId, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByParentSiteNavigationMenuItemId.
			find(
				finderCache, new Object[] {parentSiteNavigationMenuItemId},
				start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem findByParentSiteNavigationMenuItemId_First(
			long parentSiteNavigationMenuItemId,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws NoSuchMenuItemException {

		return _collectionPersistenceFinderByParentSiteNavigationMenuItemId.
			findFirst(
				finderCache, new Object[] {parentSiteNavigationMenuItemId},
				orderByComparator);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem fetchByParentSiteNavigationMenuItemId_First(
		long parentSiteNavigationMenuItemId,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return _collectionPersistenceFinderByParentSiteNavigationMenuItemId.
			fetchFirst(
				finderCache, new Object[] {parentSiteNavigationMenuItemId},
				orderByComparator);
	}

	/**
	 * Removes all the site navigation menu items where parentSiteNavigationMenuItemId = &#63; from the database.
	 *
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 */
	@Override
	public void removeByParentSiteNavigationMenuItemId(
		long parentSiteNavigationMenuItemId) {

		_collectionPersistenceFinderByParentSiteNavigationMenuItemId.remove(
			finderCache, new Object[] {parentSiteNavigationMenuItemId});
	}

	/**
	 * Returns the number of site navigation menu items where parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @return the number of matching site navigation menu items
	 */
	@Override
	public int countByParentSiteNavigationMenuItemId(
		long parentSiteNavigationMenuItemId) {

		return _collectionPersistenceFinderByParentSiteNavigationMenuItemId.
			count(finderCache, new Object[] {parentSiteNavigationMenuItemId});
	}

	private CollectionPersistenceFinder
		<SiteNavigationMenuItem, NoSuchMenuItemException>
			_collectionPersistenceFinderByType;

	/**
	 * Returns an ordered range of all the site navigation menu items where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site navigation menu items
	 */
	@Override
	public List<SiteNavigationMenuItem> findByType(
		String type, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByType.find(
			finderCache, new Object[] {type}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem findByType_First(
			String type,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws NoSuchMenuItemException {

		return _collectionPersistenceFinderByType.findFirst(
			finderCache, new Object[] {type}, orderByComparator);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem fetchByType_First(
		String type,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return _collectionPersistenceFinderByType.fetchFirst(
			finderCache, new Object[] {type}, orderByComparator);
	}

	/**
	 * Removes all the site navigation menu items where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	@Override
	public void removeByType(String type) {
		_collectionPersistenceFinderByType.remove(
			finderCache, new Object[] {type});
	}

	/**
	 * Returns the number of site navigation menu items where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching site navigation menu items
	 */
	@Override
	public int countByType(String type) {
		return _collectionPersistenceFinderByType.count(
			finderCache, new Object[] {type});
	}

	private CollectionPersistenceFinder
		<SiteNavigationMenuItem, NoSuchMenuItemException>
			_collectionPersistenceFinderByS_P;

	/**
	 * Returns an ordered range of all the site navigation menu items where siteNavigationMenuId = &#63; and parentSiteNavigationMenuItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site navigation menu items
	 */
	@Override
	public List<SiteNavigationMenuItem> findByS_P(
		long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
		int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByS_P.find(
			finderCache,
			new Object[] {siteNavigationMenuId, parentSiteNavigationMenuItemId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where siteNavigationMenuId = &#63; and parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem findByS_P_First(
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws NoSuchMenuItemException {

		return _collectionPersistenceFinderByS_P.findFirst(
			finderCache,
			new Object[] {siteNavigationMenuId, parentSiteNavigationMenuItemId},
			orderByComparator);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where siteNavigationMenuId = &#63; and parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem fetchByS_P_First(
		long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return _collectionPersistenceFinderByS_P.fetchFirst(
			finderCache,
			new Object[] {siteNavigationMenuId, parentSiteNavigationMenuItemId},
			orderByComparator);
	}

	/**
	 * Removes all the site navigation menu items where siteNavigationMenuId = &#63; and parentSiteNavigationMenuItemId = &#63; from the database.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 */
	@Override
	public void removeByS_P(
		long siteNavigationMenuId, long parentSiteNavigationMenuItemId) {

		_collectionPersistenceFinderByS_P.remove(
			finderCache,
			new Object[] {
				siteNavigationMenuId, parentSiteNavigationMenuItemId
			});
	}

	/**
	 * Returns the number of site navigation menu items where siteNavigationMenuId = &#63; and parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @return the number of matching site navigation menu items
	 */
	@Override
	public int countByS_P(
		long siteNavigationMenuId, long parentSiteNavigationMenuItemId) {

		return _collectionPersistenceFinderByS_P.count(
			finderCache,
			new Object[] {
				siteNavigationMenuId, parentSiteNavigationMenuItemId
			});
	}

	private CollectionPersistenceFinder
		<SiteNavigationMenuItem, NoSuchMenuItemException>
			_collectionPersistenceFinderByS_LikeN;

	/**
	 * Returns all the site navigation menu items where siteNavigationMenuId = &#63; and name LIKE &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param name the name
	 * @return the matching site navigation menu items
	 */
	@Override
	public List<SiteNavigationMenuItem> findByS_LikeN(
		long siteNavigationMenuId, String name) {

		return findByS_LikeN(
			siteNavigationMenuId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the site navigation menu items where siteNavigationMenuId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param name the name
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @return the range of matching site navigation menu items
	 */
	@Override
	public List<SiteNavigationMenuItem> findByS_LikeN(
		long siteNavigationMenuId, String name, int start, int end) {

		return findByS_LikeN(siteNavigationMenuId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items where siteNavigationMenuId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param name the name
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching site navigation menu items
	 */
	@Override
	public List<SiteNavigationMenuItem> findByS_LikeN(
		long siteNavigationMenuId, String name, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return findByS_LikeN(
			siteNavigationMenuId, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items where siteNavigationMenuId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param name the name
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site navigation menu items
	 */
	@Override
	public List<SiteNavigationMenuItem> findByS_LikeN(
		long siteNavigationMenuId, String name, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByS_LikeN.find(
			finderCache, new Object[] {siteNavigationMenuId, name}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where siteNavigationMenuId = &#63; and name LIKE &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem findByS_LikeN_First(
			long siteNavigationMenuId, String name,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws NoSuchMenuItemException {

		return _collectionPersistenceFinderByS_LikeN.findFirst(
			finderCache, new Object[] {siteNavigationMenuId, name},
			orderByComparator);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where siteNavigationMenuId = &#63; and name LIKE &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem fetchByS_LikeN_First(
		long siteNavigationMenuId, String name,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return _collectionPersistenceFinderByS_LikeN.fetchFirst(
			finderCache, new Object[] {siteNavigationMenuId, name},
			orderByComparator);
	}

	/**
	 * Removes all the site navigation menu items where siteNavigationMenuId = &#63; and name LIKE &#63; from the database.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param name the name
	 */
	@Override
	public void removeByS_LikeN(long siteNavigationMenuId, String name) {
		_collectionPersistenceFinderByS_LikeN.remove(
			finderCache, new Object[] {siteNavigationMenuId, name});
	}

	/**
	 * Returns the number of site navigation menu items where siteNavigationMenuId = &#63; and name LIKE &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param name the name
	 * @return the number of matching site navigation menu items
	 */
	@Override
	public int countByS_LikeN(long siteNavigationMenuId, String name) {
		return _collectionPersistenceFinderByS_LikeN.count(
			finderCache, new Object[] {siteNavigationMenuId, name});
	}

	private UniquePersistenceFinder
		<SiteNavigationMenuItem, NoSuchMenuItemException>
			_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the site navigation menu item where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchMenuItemException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchMenuItemException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the site navigation menu item where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the site navigation menu item where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the site navigation menu item that was removed
	 */
	@Override
	public SiteNavigationMenuItem removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchMenuItemException {

		SiteNavigationMenuItem siteNavigationMenuItem = findByERC_G(
			externalReferenceCode, groupId);

		return remove(siteNavigationMenuItem);
	}

	/**
	 * Returns the number of site navigation menu items where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching site navigation menu items
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public SiteNavigationMenuItemPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");
		dbColumnNames.put("order", "order_");

		setDBColumnNames(dbColumnNames);

		setModelClass(SiteNavigationMenuItem.class);

		setModelImplClass(SiteNavigationMenuItemImpl.class);
		setModelPKClass(long.class);

		setTable(SiteNavigationMenuItemTable.INSTANCE);
	}

	/**
	 * Creates a new site navigation menu item with the primary key. Does not add the site navigation menu item to the database.
	 *
	 * @param siteNavigationMenuItemId the primary key for the new site navigation menu item
	 * @return the new site navigation menu item
	 */
	@Override
	public SiteNavigationMenuItem create(long siteNavigationMenuItemId) {
		SiteNavigationMenuItem siteNavigationMenuItem =
			new SiteNavigationMenuItemImpl();

		siteNavigationMenuItem.setNew(true);
		siteNavigationMenuItem.setPrimaryKey(siteNavigationMenuItemId);

		String uuid = PortalUUIDUtil.generate();

		siteNavigationMenuItem.setUuid(uuid);

		siteNavigationMenuItem.setCompanyId(CompanyThreadLocal.getCompanyId());

		return siteNavigationMenuItem;
	}

	/**
	 * Removes the site navigation menu item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param siteNavigationMenuItemId the primary key of the site navigation menu item
	 * @return the site navigation menu item that was removed
	 * @throws NoSuchMenuItemException if a site navigation menu item with the primary key could not be found
	 */
	@Override
	public SiteNavigationMenuItem remove(long siteNavigationMenuItemId)
		throws NoSuchMenuItemException {

		return remove((Serializable)siteNavigationMenuItemId);
	}

	@Override
	protected SiteNavigationMenuItem removeImpl(
		SiteNavigationMenuItem siteNavigationMenuItem) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(siteNavigationMenuItem)) {
				siteNavigationMenuItem = (SiteNavigationMenuItem)session.get(
					SiteNavigationMenuItemImpl.class,
					siteNavigationMenuItem.getPrimaryKeyObj());
			}

			if ((siteNavigationMenuItem != null) &&
				ctPersistenceHelper.isRemove(siteNavigationMenuItem)) {

				session.delete(siteNavigationMenuItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (siteNavigationMenuItem != null) {
			clearCache(siteNavigationMenuItem);
		}

		return siteNavigationMenuItem;
	}

	@Override
	public SiteNavigationMenuItem updateImpl(
		SiteNavigationMenuItem siteNavigationMenuItem) {

		boolean isNew = siteNavigationMenuItem.isNew();

		if (!(siteNavigationMenuItem instanceof
				SiteNavigationMenuItemModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(siteNavigationMenuItem.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					siteNavigationMenuItem);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in siteNavigationMenuItem proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SiteNavigationMenuItem implementation " +
					siteNavigationMenuItem.getClass());
		}

		SiteNavigationMenuItemModelImpl siteNavigationMenuItemModelImpl =
			(SiteNavigationMenuItemModelImpl)siteNavigationMenuItem;

		if (Validator.isNull(siteNavigationMenuItem.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			siteNavigationMenuItem.setUuid(uuid);
		}

		if (Validator.isNull(
				siteNavigationMenuItem.getExternalReferenceCode())) {

			siteNavigationMenuItem.setExternalReferenceCode(
				siteNavigationMenuItem.getUuid());
		}
		else {
			if (!Objects.equals(
					siteNavigationMenuItemModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					siteNavigationMenuItem.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = siteNavigationMenuItem.getCompanyId();

					long groupId = siteNavigationMenuItem.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = siteNavigationMenuItem.getPrimaryKey();
					}

					try {
						siteNavigationMenuItem.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								SiteNavigationMenuItem.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								siteNavigationMenuItem.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			SiteNavigationMenuItem ercSiteNavigationMenuItem = fetchByERC_G(
				siteNavigationMenuItem.getExternalReferenceCode(),
				siteNavigationMenuItem.getGroupId());

			if (isNew) {
				if (ercSiteNavigationMenuItem != null) {
					throw new DuplicateSiteNavigationMenuItemExternalReferenceCodeException(
						"Duplicate site navigation menu item with external reference code " +
							siteNavigationMenuItem.getExternalReferenceCode() +
								" and group " +
									siteNavigationMenuItem.getGroupId());
				}
			}
			else {
				if ((ercSiteNavigationMenuItem != null) &&
					(siteNavigationMenuItem.getSiteNavigationMenuItemId() !=
						ercSiteNavigationMenuItem.
							getSiteNavigationMenuItemId())) {

					throw new DuplicateSiteNavigationMenuItemExternalReferenceCodeException(
						"Duplicate site navigation menu item with external reference code " +
							siteNavigationMenuItem.getExternalReferenceCode() +
								" and group " +
									siteNavigationMenuItem.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (siteNavigationMenuItem.getCreateDate() == null)) {
			if (serviceContext == null) {
				siteNavigationMenuItem.setCreateDate(date);
			}
			else {
				siteNavigationMenuItem.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!siteNavigationMenuItemModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				siteNavigationMenuItem.setModifiedDate(date);
			}
			else {
				siteNavigationMenuItem.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(siteNavigationMenuItem)) {
				if (!isNew) {
					session.evict(
						SiteNavigationMenuItemImpl.class,
						siteNavigationMenuItem.getPrimaryKeyObj());
				}

				session.save(siteNavigationMenuItem);
			}
			else {
				siteNavigationMenuItem = (SiteNavigationMenuItem)session.merge(
					siteNavigationMenuItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(siteNavigationMenuItem, false);

		if (isNew) {
			siteNavigationMenuItem.setNew(false);
		}

		siteNavigationMenuItem.resetOriginalValues();

		return siteNavigationMenuItem;
	}

	/**
	 * Returns the site navigation menu item with the primary key or throws a <code>NoSuchMenuItemException</code> if it could not be found.
	 *
	 * @param siteNavigationMenuItemId the primary key of the site navigation menu item
	 * @return the site navigation menu item
	 * @throws NoSuchMenuItemException if a site navigation menu item with the primary key could not be found
	 */
	@Override
	public SiteNavigationMenuItem findByPrimaryKey(
			long siteNavigationMenuItemId)
		throws NoSuchMenuItemException {

		return findByPrimaryKey((Serializable)siteNavigationMenuItemId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the site navigation menu item with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param siteNavigationMenuItemId the primary key of the site navigation menu item
	 * @return the site navigation menu item, or <code>null</code> if a site navigation menu item with the primary key could not be found
	 */
	@Override
	public SiteNavigationMenuItem fetchByPrimaryKey(
		long siteNavigationMenuItemId) {

		return fetchByPrimaryKey((Serializable)siteNavigationMenuItemId);
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
		return "siteNavigationMenuItemId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SITENAVIGATIONMENUITEM;
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
		return SiteNavigationMenuItemModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "SiteNavigationMenuItem";
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
		ctMergeColumnNames.add("siteNavigationMenuId");
		ctMergeColumnNames.add("parentSiteNavigationMenuItemId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("typeSettings");
		ctMergeColumnNames.add("order_");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("siteNavigationMenuItemId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the site navigation menu item persistence.
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
			_SQL_SELECT_SITENAVIGATIONMENUITEM_WHERE,
			_SQL_COUNT_SITENAVIGATIONMENUITEM_WHERE,
			SiteNavigationMenuItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"siteNavigationMenuItem.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				SiteNavigationMenuItem::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(SiteNavigationMenuItem::getUuid),
				SiteNavigationMenuItem::getGroupId),
			_SQL_SELECT_SITENAVIGATIONMENUITEM_WHERE, "",
			new FinderColumn<>(
				"siteNavigationMenuItem.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				SiteNavigationMenuItem::getUuid),
			new FinderColumn<>(
				"siteNavigationMenuItem.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, SiteNavigationMenuItem::getGroupId));

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
				_SQL_SELECT_SITENAVIGATIONMENUITEM_WHERE,
				_SQL_COUNT_SITENAVIGATIONMENUITEM_WHERE,
				SiteNavigationMenuItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"siteNavigationMenuItem.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					SiteNavigationMenuItem::getUuid),
				new FinderColumn<>(
					"siteNavigationMenuItem.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					SiteNavigationMenuItem::getCompanyId));

		_collectionPersistenceFinderByCompanyId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, false),
				_SQL_SELECT_SITENAVIGATIONMENUITEM_WHERE,
				_SQL_COUNT_SITENAVIGATIONMENUITEM_WHERE,
				SiteNavigationMenuItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"siteNavigationMenuItem.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					SiteNavigationMenuItem::getCompanyId));

		_collectionPersistenceFinderBySiteNavigationMenuId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findBySiteNavigationMenuId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"siteNavigationMenuId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findBySiteNavigationMenuId",
					new String[] {Long.class.getName()},
					new String[] {"siteNavigationMenuId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countBySiteNavigationMenuId",
					new String[] {Long.class.getName()},
					new String[] {"siteNavigationMenuId"}, false),
				_SQL_SELECT_SITENAVIGATIONMENUITEM_WHERE,
				_SQL_COUNT_SITENAVIGATIONMENUITEM_WHERE,
				SiteNavigationMenuItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"siteNavigationMenuItem.", "siteNavigationMenuId",
					FinderColumn.Type.LONG, "=", true, true,
					SiteNavigationMenuItem::getSiteNavigationMenuId));

		_collectionPersistenceFinderByParentSiteNavigationMenuItemId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByParentSiteNavigationMenuItemId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"parentSiteNavigationMenuItemId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByParentSiteNavigationMenuItemId",
					new String[] {Long.class.getName()},
					new String[] {"parentSiteNavigationMenuItemId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByParentSiteNavigationMenuItemId",
					new String[] {Long.class.getName()},
					new String[] {"parentSiteNavigationMenuItemId"}, false),
				_SQL_SELECT_SITENAVIGATIONMENUITEM_WHERE,
				_SQL_COUNT_SITENAVIGATIONMENUITEM_WHERE,
				SiteNavigationMenuItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"siteNavigationMenuItem.", "parentSiteNavigationMenuItemId",
					FinderColumn.Type.LONG, "=", true, true,
					SiteNavigationMenuItem::getParentSiteNavigationMenuItemId));

		_collectionPersistenceFinderByType = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByType",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByType",
				new String[] {String.class.getName()}, new String[] {"type_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByType",
				new String[] {String.class.getName()}, new String[] {"type_"},
				0, 1, false, null),
			_SQL_SELECT_SITENAVIGATIONMENUITEM_WHERE,
			_SQL_COUNT_SITENAVIGATIONMENUITEM_WHERE,
			SiteNavigationMenuItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"siteNavigationMenuItem.", "type", "type_",
				FinderColumn.Type.STRING, "=", true, true,
				SiteNavigationMenuItem::getType));

		_collectionPersistenceFinderByS_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByS_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {
					"siteNavigationMenuId", "parentSiteNavigationMenuItemId"
				},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByS_P",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {
					"siteNavigationMenuId", "parentSiteNavigationMenuItemId"
				},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByS_P",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {
					"siteNavigationMenuId", "parentSiteNavigationMenuItemId"
				},
				false),
			_SQL_SELECT_SITENAVIGATIONMENUITEM_WHERE,
			_SQL_COUNT_SITENAVIGATIONMENUITEM_WHERE,
			SiteNavigationMenuItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"siteNavigationMenuItem.", "siteNavigationMenuId",
				FinderColumn.Type.LONG, "=", true, true,
				SiteNavigationMenuItem::getSiteNavigationMenuId),
			new FinderColumn<>(
				"siteNavigationMenuItem.", "parentSiteNavigationMenuItemId",
				FinderColumn.Type.LONG, "=", true, true,
				SiteNavigationMenuItem::getParentSiteNavigationMenuItemId));

		_collectionPersistenceFinderByS_LikeN =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByS_LikeN",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"siteNavigationMenuId", "name"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByS_LikeN",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"siteNavigationMenuId", "name"}, false),
				_SQL_SELECT_SITENAVIGATIONMENUITEM_WHERE,
				_SQL_COUNT_SITENAVIGATIONMENUITEM_WHERE,
				SiteNavigationMenuItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"siteNavigationMenuItem.", "siteNavigationMenuId",
					FinderColumn.Type.LONG, "=", true, true,
					SiteNavigationMenuItem::getSiteNavigationMenuId),
				new FinderColumn<>(
					"siteNavigationMenuItem.", "name", FinderColumn.Type.STRING,
					"LIKE", true, true, SiteNavigationMenuItem::getName));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(
					SiteNavigationMenuItem::getExternalReferenceCode),
				SiteNavigationMenuItem::getGroupId),
			_SQL_SELECT_SITENAVIGATIONMENUITEM_WHERE, "",
			new FinderColumn<>(
				"siteNavigationMenuItem.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				SiteNavigationMenuItem::getExternalReferenceCode),
			new FinderColumn<>(
				"siteNavigationMenuItem.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, SiteNavigationMenuItem::getGroupId));

		SiteNavigationMenuItemUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SiteNavigationMenuItemUtil.setPersistence(null);

		entityCache.removeCache(SiteNavigationMenuItemImpl.class.getName());
	}

	@Override
	@Reference(
		target = SiteNavigationPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SiteNavigationPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SiteNavigationPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		SiteNavigationMenuItemModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SITENAVIGATIONMENUITEM =
		"SELECT siteNavigationMenuItem FROM SiteNavigationMenuItem siteNavigationMenuItem";

	private static final String _SQL_SELECT_SITENAVIGATIONMENUITEM_WHERE =
		"SELECT siteNavigationMenuItem FROM SiteNavigationMenuItem siteNavigationMenuItem WHERE ";

	private static final String _SQL_COUNT_SITENAVIGATIONMENUITEM_WHERE =
		"SELECT COUNT(siteNavigationMenuItem) FROM SiteNavigationMenuItem siteNavigationMenuItem WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SiteNavigationMenuItem exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SiteNavigationMenuItemPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type", "order"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-113220304