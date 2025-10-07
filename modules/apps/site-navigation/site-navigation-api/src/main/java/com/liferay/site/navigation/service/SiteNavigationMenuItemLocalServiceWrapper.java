/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;

/**
 * Provides a wrapper for {@link SiteNavigationMenuItemLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see SiteNavigationMenuItemLocalService
 * @generated
 */
public class SiteNavigationMenuItemLocalServiceWrapper
	implements ServiceWrapper<SiteNavigationMenuItemLocalService>,
			   SiteNavigationMenuItemLocalService {

	public SiteNavigationMenuItemLocalServiceWrapper() {
		this(null);
	}

	public SiteNavigationMenuItemLocalServiceWrapper(
		SiteNavigationMenuItemLocalService siteNavigationMenuItemLocalService) {

		_siteNavigationMenuItemLocalService =
			siteNavigationMenuItemLocalService;
	}

	@Override
	public SiteNavigationMenuItem addOrUpdateSiteNavigationMenuItem(
			String externalReferenceCode, long userId, long groupId,
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			String type, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		return _siteNavigationMenuItemLocalService.
			addOrUpdateSiteNavigationMenuItem(
				externalReferenceCode, userId, groupId, siteNavigationMenuId,
				parentSiteNavigationMenuItemId, type, typeSettings,
				serviceContext);
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
	@Override
	public SiteNavigationMenuItem addSiteNavigationMenuItem(
		SiteNavigationMenuItem siteNavigationMenuItem) {

		return _siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
			siteNavigationMenuItem);
	}

	@Override
	public SiteNavigationMenuItem addSiteNavigationMenuItem(
			String externalReferenceCode, long userId, long groupId,
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			String type, int order, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
			externalReferenceCode, userId, groupId, siteNavigationMenuId,
			parentSiteNavigationMenuItemId, type, order, typeSettings,
			serviceContext);
	}

	@Override
	public SiteNavigationMenuItem addSiteNavigationMenuItem(
			String externalReferenceCode, long userId, long groupId,
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			String type, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
			externalReferenceCode, userId, groupId, siteNavigationMenuId,
			parentSiteNavigationMenuItemId, type, typeSettings, serviceContext);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _siteNavigationMenuItemLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new site navigation menu item with the primary key. Does not add the site navigation menu item to the database.
	 *
	 * @param siteNavigationMenuItemId the primary key for the new site navigation menu item
	 * @return the new site navigation menu item
	 */
	@Override
	public SiteNavigationMenuItem createSiteNavigationMenuItem(
		long siteNavigationMenuItemId) {

		return _siteNavigationMenuItemLocalService.createSiteNavigationMenuItem(
			siteNavigationMenuItemId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _siteNavigationMenuItemLocalService.deletePersistedModel(
			persistedModel);
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
	@Override
	public SiteNavigationMenuItem deleteSiteNavigationMenuItem(
			long siteNavigationMenuItemId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _siteNavigationMenuItemLocalService.deleteSiteNavigationMenuItem(
			siteNavigationMenuItemId);
	}

	@Override
	public SiteNavigationMenuItem deleteSiteNavigationMenuItem(
			long siteNavigationMenuItemId, boolean deleteChildren)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _siteNavigationMenuItemLocalService.deleteSiteNavigationMenuItem(
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
	@Override
	public SiteNavigationMenuItem deleteSiteNavigationMenuItem(
		SiteNavigationMenuItem siteNavigationMenuItem) {

		return _siteNavigationMenuItemLocalService.deleteSiteNavigationMenuItem(
			siteNavigationMenuItem);
	}

	@Override
	public SiteNavigationMenuItem deleteSiteNavigationMenuItem(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _siteNavigationMenuItemLocalService.deleteSiteNavigationMenuItem(
			externalReferenceCode, groupId);
	}

	@Override
	public void deleteSiteNavigationMenuItems(long siteNavigationMenuId) {
		_siteNavigationMenuItemLocalService.deleteSiteNavigationMenuItems(
			siteNavigationMenuId);
	}

	@Override
	public void deleteSiteNavigationMenuItemsByGroupId(long groupId) {
		_siteNavigationMenuItemLocalService.
			deleteSiteNavigationMenuItemsByGroupId(groupId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _siteNavigationMenuItemLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _siteNavigationMenuItemLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _siteNavigationMenuItemLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _siteNavigationMenuItemLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _siteNavigationMenuItemLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _siteNavigationMenuItemLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _siteNavigationMenuItemLocalService.dynamicQueryCount(
			dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _siteNavigationMenuItemLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public SiteNavigationMenuItem fetchSiteNavigationMenuItem(
		long siteNavigationMenuItemId) {

		return _siteNavigationMenuItemLocalService.fetchSiteNavigationMenuItem(
			siteNavigationMenuItemId);
	}

	@Override
	public SiteNavigationMenuItem
		fetchSiteNavigationMenuItemByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return _siteNavigationMenuItemLocalService.
			fetchSiteNavigationMenuItemByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the site navigation menu item matching the UUID and group.
	 *
	 * @param uuid the site navigation menu item's UUID
	 * @param groupId the primary key of the group
	 * @return the matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	@Override
	public SiteNavigationMenuItem fetchSiteNavigationMenuItemByUuidAndGroupId(
		String uuid, long groupId) {

		return _siteNavigationMenuItemLocalService.
			fetchSiteNavigationMenuItemByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _siteNavigationMenuItemLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _siteNavigationMenuItemLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _siteNavigationMenuItemLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _siteNavigationMenuItemLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<Long> getParentSiteNavigationMenuItemIds(
		long siteNavigationMenuId, String typeSettingsKeyword) {

		return _siteNavigationMenuItemLocalService.
			getParentSiteNavigationMenuItemIds(
				siteNavigationMenuId, typeSettingsKeyword);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _siteNavigationMenuItemLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Returns the site navigation menu item with the primary key.
	 *
	 * @param siteNavigationMenuItemId the primary key of the site navigation menu item
	 * @return the site navigation menu item
	 * @throws PortalException if a site navigation menu item with the primary key could not be found
	 */
	@Override
	public SiteNavigationMenuItem getSiteNavigationMenuItem(
			long siteNavigationMenuItemId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _siteNavigationMenuItemLocalService.getSiteNavigationMenuItem(
			siteNavigationMenuItemId);
	}

	@Override
	public SiteNavigationMenuItem
			getSiteNavigationMenuItemByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _siteNavigationMenuItemLocalService.
			getSiteNavigationMenuItemByExternalReferenceCode(
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
	@Override
	public SiteNavigationMenuItem getSiteNavigationMenuItemByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _siteNavigationMenuItemLocalService.
			getSiteNavigationMenuItemByUuidAndGroupId(uuid, groupId);
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
	@Override
	public java.util.List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
		int start, int end) {

		return _siteNavigationMenuItemLocalService.getSiteNavigationMenuItems(
			start, end);
	}

	@Override
	public java.util.List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
		long siteNavigationMenuId) {

		return _siteNavigationMenuItemLocalService.getSiteNavigationMenuItems(
			siteNavigationMenuId);
	}

	@Override
	public java.util.List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
		long siteNavigationMenuId, long parentSiteNavigationMenuItemId) {

		return _siteNavigationMenuItemLocalService.getSiteNavigationMenuItems(
			siteNavigationMenuId, parentSiteNavigationMenuItemId);
	}

	@Override
	public java.util.List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
		long siteNavigationMenuId,
		com.liferay.portal.kernel.util.OrderByComparator<SiteNavigationMenuItem>
			orderByComparator) {

		return _siteNavigationMenuItemLocalService.getSiteNavigationMenuItems(
			siteNavigationMenuId, orderByComparator);
	}

	@Override
	public java.util.List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
		String type) {

		return _siteNavigationMenuItemLocalService.getSiteNavigationMenuItems(
			type);
	}

	/**
	 * Returns all the site navigation menu items matching the UUID and company.
	 *
	 * @param uuid the UUID of the site navigation menu items
	 * @param companyId the primary key of the company
	 * @return the matching site navigation menu items, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<SiteNavigationMenuItem>
		getSiteNavigationMenuItemsByUuidAndCompanyId(
			String uuid, long companyId) {

		return _siteNavigationMenuItemLocalService.
			getSiteNavigationMenuItemsByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<SiteNavigationMenuItem>
		getSiteNavigationMenuItemsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<SiteNavigationMenuItem> orderByComparator) {

		return _siteNavigationMenuItemLocalService.
			getSiteNavigationMenuItemsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of site navigation menu items.
	 *
	 * @return the number of site navigation menu items
	 */
	@Override
	public int getSiteNavigationMenuItemsCount() {
		return _siteNavigationMenuItemLocalService.
			getSiteNavigationMenuItemsCount();
	}

	@Override
	public int getSiteNavigationMenuItemsCount(long siteNavigationMenuId) {
		return _siteNavigationMenuItemLocalService.
			getSiteNavigationMenuItemsCount(siteNavigationMenuId);
	}

	@Override
	public SiteNavigationMenuItem updateSiteNavigationMenuItem(
			long siteNavigationMenuItemId, long parentSiteNavigationMenuItemId,
			int order)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _siteNavigationMenuItemLocalService.updateSiteNavigationMenuItem(
			siteNavigationMenuItemId, parentSiteNavigationMenuItemId, order);
	}

	@Override
	public SiteNavigationMenuItem updateSiteNavigationMenuItem(
			long userId, long siteNavigationMenuItemId, long groupId,
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			String type, int order, String typeSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _siteNavigationMenuItemLocalService.updateSiteNavigationMenuItem(
			userId, siteNavigationMenuItemId, groupId, siteNavigationMenuId,
			parentSiteNavigationMenuItemId, type, order, typeSettings);
	}

	@Override
	public SiteNavigationMenuItem updateSiteNavigationMenuItem(
			long userId, long siteNavigationMenuItemId, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _siteNavigationMenuItemLocalService.updateSiteNavigationMenuItem(
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
	@Override
	public SiteNavigationMenuItem updateSiteNavigationMenuItem(
		SiteNavigationMenuItem siteNavigationMenuItem) {

		return _siteNavigationMenuItemLocalService.updateSiteNavigationMenuItem(
			siteNavigationMenuItem);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _siteNavigationMenuItemLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<SiteNavigationMenuItem> getCTPersistence() {
		return _siteNavigationMenuItemLocalService.getCTPersistence();
	}

	@Override
	public Class<SiteNavigationMenuItem> getModelClass() {
		return _siteNavigationMenuItemLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<SiteNavigationMenuItem>, R, E>
				updateUnsafeFunction)
		throws E {

		return _siteNavigationMenuItemLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public SiteNavigationMenuItemLocalService getWrappedService() {
		return _siteNavigationMenuItemLocalService;
	}

	@Override
	public void setWrappedService(
		SiteNavigationMenuItemLocalService siteNavigationMenuItemLocalService) {

		_siteNavigationMenuItemLocalService =
			siteNavigationMenuItemLocalService;
	}

	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

}