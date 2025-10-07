/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SiteNavigationMenuItem. This utility wraps
 * <code>com.liferay.site.navigation.service.impl.SiteNavigationMenuItemLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see SiteNavigationMenuItemLocalService
 * @generated
 */
public class SiteNavigationMenuItemLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.site.navigation.service.impl.SiteNavigationMenuItemLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static SiteNavigationMenuItem addOrUpdateSiteNavigationMenuItem(
			String externalReferenceCode, long userId, long groupId,
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			String type, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		return getService().addOrUpdateSiteNavigationMenuItem(
			externalReferenceCode, userId, groupId, siteNavigationMenuId,
			parentSiteNavigationMenuItemId, type, typeSettings, serviceContext);
	}

	/**
	 * Adds the site navigation menu item to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SiteNavigationMenuItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param siteNavigationMenuItem the site navigation menu item
	 * @return the site navigation menu item that was added
	 */
	public static SiteNavigationMenuItem addSiteNavigationMenuItem(
		SiteNavigationMenuItem siteNavigationMenuItem) {

		return getService().addSiteNavigationMenuItem(siteNavigationMenuItem);
	}

	public static SiteNavigationMenuItem addSiteNavigationMenuItem(
			String externalReferenceCode, long userId, long groupId,
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			String type, int order, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addSiteNavigationMenuItem(
			externalReferenceCode, userId, groupId, siteNavigationMenuId,
			parentSiteNavigationMenuItemId, type, order, typeSettings,
			serviceContext);
	}

	public static SiteNavigationMenuItem addSiteNavigationMenuItem(
			String externalReferenceCode, long userId, long groupId,
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			String type, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addSiteNavigationMenuItem(
			externalReferenceCode, userId, groupId, siteNavigationMenuId,
			parentSiteNavigationMenuItemId, type, typeSettings, serviceContext);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new site navigation menu item with the primary key. Does not add the site navigation menu item to the database.
	 *
	 * @param siteNavigationMenuItemId the primary key for the new site navigation menu item
	 * @return the new site navigation menu item
	 */
	public static SiteNavigationMenuItem createSiteNavigationMenuItem(
		long siteNavigationMenuItemId) {

		return getService().createSiteNavigationMenuItem(
			siteNavigationMenuItemId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the site navigation menu item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SiteNavigationMenuItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param siteNavigationMenuItemId the primary key of the site navigation menu item
	 * @return the site navigation menu item that was removed
	 * @throws PortalException if a site navigation menu item with the primary key could not be found
	 */
	public static SiteNavigationMenuItem deleteSiteNavigationMenuItem(
			long siteNavigationMenuItemId)
		throws PortalException {

		return getService().deleteSiteNavigationMenuItem(
			siteNavigationMenuItemId);
	}

	public static SiteNavigationMenuItem deleteSiteNavigationMenuItem(
			long siteNavigationMenuItemId, boolean deleteChildren)
		throws PortalException {

		return getService().deleteSiteNavigationMenuItem(
			siteNavigationMenuItemId, deleteChildren);
	}

	/**
	 * Deletes the site navigation menu item from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SiteNavigationMenuItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param siteNavigationMenuItem the site navigation menu item
	 * @return the site navigation menu item that was removed
	 */
	public static SiteNavigationMenuItem deleteSiteNavigationMenuItem(
		SiteNavigationMenuItem siteNavigationMenuItem) {

		return getService().deleteSiteNavigationMenuItem(
			siteNavigationMenuItem);
	}

	public static SiteNavigationMenuItem deleteSiteNavigationMenuItem(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteSiteNavigationMenuItem(
			externalReferenceCode, groupId);
	}

	public static void deleteSiteNavigationMenuItems(
		long siteNavigationMenuId) {

		getService().deleteSiteNavigationMenuItems(siteNavigationMenuId);
	}

	public static void deleteSiteNavigationMenuItemsByGroupId(long groupId) {
		getService().deleteSiteNavigationMenuItemsByGroupId(groupId);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.navigation.model.impl.SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.navigation.model.impl.SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static SiteNavigationMenuItem fetchSiteNavigationMenuItem(
		long siteNavigationMenuItemId) {

		return getService().fetchSiteNavigationMenuItem(
			siteNavigationMenuItemId);
	}

	public static SiteNavigationMenuItem
		fetchSiteNavigationMenuItemByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return getService().fetchSiteNavigationMenuItemByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the site navigation menu item matching the UUID and group.
	 *
	 * @param uuid the site navigation menu item's UUID
	 * @param groupId the primary key of the group
	 * @return the matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem
		fetchSiteNavigationMenuItemByUuidAndGroupId(String uuid, long groupId) {

		return getService().fetchSiteNavigationMenuItemByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<Long> getParentSiteNavigationMenuItemIds(
		long siteNavigationMenuId, String typeSettingsKeyword) {

		return getService().getParentSiteNavigationMenuItemIds(
			siteNavigationMenuId, typeSettingsKeyword);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the site navigation menu item with the primary key.
	 *
	 * @param siteNavigationMenuItemId the primary key of the site navigation menu item
	 * @return the site navigation menu item
	 * @throws PortalException if a site navigation menu item with the primary key could not be found
	 */
	public static SiteNavigationMenuItem getSiteNavigationMenuItem(
			long siteNavigationMenuItemId)
		throws PortalException {

		return getService().getSiteNavigationMenuItem(siteNavigationMenuItemId);
	}

	public static SiteNavigationMenuItem
			getSiteNavigationMenuItemByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getSiteNavigationMenuItemByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the site navigation menu item matching the UUID and group.
	 *
	 * @param uuid the site navigation menu item's UUID
	 * @param groupId the primary key of the group
	 * @return the matching site navigation menu item
	 * @throws PortalException if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem
			getSiteNavigationMenuItemByUuidAndGroupId(String uuid, long groupId)
		throws PortalException {

		return getService().getSiteNavigationMenuItemByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the site navigation menu items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.navigation.model.impl.SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @return the range of site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
		int start, int end) {

		return getService().getSiteNavigationMenuItems(start, end);
	}

	public static List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
		long siteNavigationMenuId) {

		return getService().getSiteNavigationMenuItems(siteNavigationMenuId);
	}

	public static List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
		long siteNavigationMenuId, long parentSiteNavigationMenuItemId) {

		return getService().getSiteNavigationMenuItems(
			siteNavigationMenuId, parentSiteNavigationMenuItemId);
	}

	public static List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
		long siteNavigationMenuId,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getService().getSiteNavigationMenuItems(
			siteNavigationMenuId, orderByComparator);
	}

	public static List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
		String type) {

		return getService().getSiteNavigationMenuItems(type);
	}

	/**
	 * Returns all the site navigation menu items matching the UUID and company.
	 *
	 * @param uuid the UUID of the site navigation menu items
	 * @param companyId the primary key of the company
	 * @return the matching site navigation menu items, or an empty list if no matches were found
	 */
	public static List<SiteNavigationMenuItem>
		getSiteNavigationMenuItemsByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getSiteNavigationMenuItemsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of site navigation menu items matching the UUID and company.
	 *
	 * @param uuid the UUID of the site navigation menu items
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching site navigation menu items, or an empty list if no matches were found
	 */
	public static List<SiteNavigationMenuItem>
		getSiteNavigationMenuItemsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getService().getSiteNavigationMenuItemsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of site navigation menu items.
	 *
	 * @return the number of site navigation menu items
	 */
	public static int getSiteNavigationMenuItemsCount() {
		return getService().getSiteNavigationMenuItemsCount();
	}

	public static int getSiteNavigationMenuItemsCount(
		long siteNavigationMenuId) {

		return getService().getSiteNavigationMenuItemsCount(
			siteNavigationMenuId);
	}

	public static SiteNavigationMenuItem updateSiteNavigationMenuItem(
			long siteNavigationMenuItemId, long parentSiteNavigationMenuItemId,
			int order)
		throws PortalException {

		return getService().updateSiteNavigationMenuItem(
			siteNavigationMenuItemId, parentSiteNavigationMenuItemId, order);
	}

	public static SiteNavigationMenuItem updateSiteNavigationMenuItem(
			long userId, long siteNavigationMenuItemId, long groupId,
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			String type, int order, String typeSettings)
		throws PortalException {

		return getService().updateSiteNavigationMenuItem(
			userId, siteNavigationMenuItemId, groupId, siteNavigationMenuId,
			parentSiteNavigationMenuItemId, type, order, typeSettings);
	}

	public static SiteNavigationMenuItem updateSiteNavigationMenuItem(
			long userId, long siteNavigationMenuItemId, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateSiteNavigationMenuItem(
			userId, siteNavigationMenuItemId, typeSettings, serviceContext);
	}

	/**
	 * Updates the site navigation menu item in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SiteNavigationMenuItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param siteNavigationMenuItem the site navigation menu item
	 * @return the site navigation menu item that was updated
	 */
	public static SiteNavigationMenuItem updateSiteNavigationMenuItem(
		SiteNavigationMenuItem siteNavigationMenuItem) {

		return getService().updateSiteNavigationMenuItem(
			siteNavigationMenuItem);
	}

	public static SiteNavigationMenuItemLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SiteNavigationMenuItemLocalService>
		_serviceSnapshot = new Snapshot<>(
			SiteNavigationMenuItemLocalServiceUtil.class,
			SiteNavigationMenuItemLocalService.class);

}