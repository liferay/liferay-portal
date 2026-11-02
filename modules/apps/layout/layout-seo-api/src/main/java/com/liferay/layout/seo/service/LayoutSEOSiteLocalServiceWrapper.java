/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.service;

import com.liferay.layout.seo.model.LayoutSEOSite;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link LayoutSEOSiteLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutSEOSiteLocalService
 * @generated
 */
public class LayoutSEOSiteLocalServiceWrapper
	implements LayoutSEOSiteLocalService,
			   ServiceWrapper<LayoutSEOSiteLocalService> {

	public LayoutSEOSiteLocalServiceWrapper() {
		this(null);
	}

	public LayoutSEOSiteLocalServiceWrapper(
		LayoutSEOSiteLocalService layoutSEOSiteLocalService) {

		_layoutSEOSiteLocalService = layoutSEOSiteLocalService;
	}

	/**
	 * Adds the layout seo site to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSEOSiteLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSEOSite the layout seo site
	 * @return the layout seo site that was added
	 */
	@Override
	public LayoutSEOSite addLayoutSEOSite(LayoutSEOSite layoutSEOSite) {
		return _layoutSEOSiteLocalService.addLayoutSEOSite(layoutSEOSite);
	}

	/**
	 * Creates a new layout seo site with the primary key. Does not add the layout seo site to the database.
	 *
	 * @param layoutSEOSiteId the primary key for the new layout seo site
	 * @return the new layout seo site
	 */
	@Override
	public LayoutSEOSite createLayoutSEOSite(long layoutSEOSiteId) {
		return _layoutSEOSiteLocalService.createLayoutSEOSite(layoutSEOSiteId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSEOSiteLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the layout seo site from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSEOSiteLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSEOSite the layout seo site
	 * @return the layout seo site that was removed
	 */
	@Override
	public LayoutSEOSite deleteLayoutSEOSite(LayoutSEOSite layoutSEOSite) {
		return _layoutSEOSiteLocalService.deleteLayoutSEOSite(layoutSEOSite);
	}

	/**
	 * Deletes the layout seo site with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSEOSiteLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSEOSiteId the primary key of the layout seo site
	 * @return the layout seo site that was removed
	 * @throws PortalException if a layout seo site with the primary key could not be found
	 */
	@Override
	public LayoutSEOSite deleteLayoutSEOSite(long layoutSEOSiteId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSEOSiteLocalService.deleteLayoutSEOSite(layoutSEOSiteId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSEOSiteLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _layoutSEOSiteLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _layoutSEOSiteLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _layoutSEOSiteLocalService.dynamicQuery();
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

		return _layoutSEOSiteLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.seo.model.impl.LayoutSEOSiteModelImpl</code>.
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

		return _layoutSEOSiteLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.seo.model.impl.LayoutSEOSiteModelImpl</code>.
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

		return _layoutSEOSiteLocalService.dynamicQuery(
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

		return _layoutSEOSiteLocalService.dynamicQueryCount(dynamicQuery);
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

		return _layoutSEOSiteLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public LayoutSEOSite fetchLayoutSEOSite(long layoutSEOSiteId) {
		return _layoutSEOSiteLocalService.fetchLayoutSEOSite(layoutSEOSiteId);
	}

	@Override
	public LayoutSEOSite fetchLayoutSEOSiteByGroupId(long groupId) {
		return _layoutSEOSiteLocalService.fetchLayoutSEOSiteByGroupId(groupId);
	}

	/**
	 * Returns the layout seo site matching the UUID and group.
	 *
	 * @param uuid the layout seo site's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout seo site, or <code>null</code> if a matching layout seo site could not be found
	 */
	@Override
	public LayoutSEOSite fetchLayoutSEOSiteByUuidAndGroupId(
		String uuid, long groupId) {

		return _layoutSEOSiteLocalService.fetchLayoutSEOSiteByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _layoutSEOSiteLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _layoutSEOSiteLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _layoutSEOSiteLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the layout seo site with the primary key.
	 *
	 * @param layoutSEOSiteId the primary key of the layout seo site
	 * @return the layout seo site
	 * @throws PortalException if a layout seo site with the primary key could not be found
	 */
	@Override
	public LayoutSEOSite getLayoutSEOSite(long layoutSEOSiteId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSEOSiteLocalService.getLayoutSEOSite(layoutSEOSiteId);
	}

	/**
	 * Returns the layout seo site matching the UUID and group.
	 *
	 * @param uuid the layout seo site's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout seo site
	 * @throws PortalException if a matching layout seo site could not be found
	 */
	@Override
	public LayoutSEOSite getLayoutSEOSiteByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSEOSiteLocalService.getLayoutSEOSiteByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the layout seo sites.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.seo.model.impl.LayoutSEOSiteModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout seo sites
	 * @param end the upper bound of the range of layout seo sites (not inclusive)
	 * @return the range of layout seo sites
	 */
	@Override
	public java.util.List<LayoutSEOSite> getLayoutSEOSites(int start, int end) {
		return _layoutSEOSiteLocalService.getLayoutSEOSites(start, end);
	}

	/**
	 * Returns all the layout seo sites matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout seo sites
	 * @param companyId the primary key of the company
	 * @return the matching layout seo sites, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<LayoutSEOSite> getLayoutSEOSitesByUuidAndCompanyId(
		String uuid, long companyId) {

		return _layoutSEOSiteLocalService.getLayoutSEOSitesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of layout seo sites matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout seo sites
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of layout seo sites
	 * @param end the upper bound of the range of layout seo sites (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching layout seo sites, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<LayoutSEOSite> getLayoutSEOSitesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSEOSite>
			orderByComparator) {

		return _layoutSEOSiteLocalService.getLayoutSEOSitesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of layout seo sites.
	 *
	 * @return the number of layout seo sites
	 */
	@Override
	public int getLayoutSEOSitesCount() {
		return _layoutSEOSiteLocalService.getLayoutSEOSitesCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _layoutSEOSiteLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSEOSiteLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the layout seo site in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSEOSiteLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSEOSite the layout seo site
	 * @return the layout seo site that was updated
	 */
	@Override
	public LayoutSEOSite updateLayoutSEOSite(LayoutSEOSite layoutSEOSite) {
		return _layoutSEOSiteLocalService.updateLayoutSEOSite(layoutSEOSite);
	}

	@Override
	public LayoutSEOSite updateLayoutSEOSite(
			long userId, long groupId, boolean openGraphEnabled,
			java.util.Map<java.util.Locale, String> openGraphImageAltMap,
			long openGraphImageFileEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSEOSiteLocalService.updateLayoutSEOSite(
			userId, groupId, openGraphEnabled, openGraphImageAltMap,
			openGraphImageFileEntryId, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _layoutSEOSiteLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<LayoutSEOSite> getCTPersistence() {
		return _layoutSEOSiteLocalService.getCTPersistence();
	}

	@Override
	public Class<LayoutSEOSite> getModelClass() {
		return _layoutSEOSiteLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<LayoutSEOSite>, R, E>
				updateUnsafeFunction)
		throws E {

		return _layoutSEOSiteLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public LayoutSEOSiteLocalService getWrappedService() {
		return _layoutSEOSiteLocalService;
	}

	@Override
	public void setWrappedService(
		LayoutSEOSiteLocalService layoutSEOSiteLocalService) {

		_layoutSEOSiteLocalService = layoutSEOSiteLocalService;
	}

	private LayoutSEOSiteLocalService _layoutSEOSiteLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-87493769