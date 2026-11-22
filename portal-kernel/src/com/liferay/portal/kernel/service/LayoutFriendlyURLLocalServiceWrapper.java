/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.LayoutFriendlyURL;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link LayoutFriendlyURLLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutFriendlyURLLocalService
 * @generated
 */
public class LayoutFriendlyURLLocalServiceWrapper
	implements LayoutFriendlyURLLocalService,
			   ServiceWrapper<LayoutFriendlyURLLocalService> {

	public LayoutFriendlyURLLocalServiceWrapper() {
		this(null);
	}

	public LayoutFriendlyURLLocalServiceWrapper(
		LayoutFriendlyURLLocalService layoutFriendlyURLLocalService) {

		_layoutFriendlyURLLocalService = layoutFriendlyURLLocalService;
	}

	/**
	 * Adds the layout friendly url to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutFriendlyURLLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutFriendlyURL the layout friendly url
	 * @return the layout friendly url that was added
	 */
	@Override
	public LayoutFriendlyURL addLayoutFriendlyURL(
		LayoutFriendlyURL layoutFriendlyURL) {

		return _layoutFriendlyURLLocalService.addLayoutFriendlyURL(
			layoutFriendlyURL);
	}

	@Override
	public LayoutFriendlyURL addLayoutFriendlyURL(
			long userId, long companyId, long groupId, long plid,
			boolean privateLayout, String friendlyURL, String languageId,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutFriendlyURLLocalService.addLayoutFriendlyURL(
			userId, companyId, groupId, plid, privateLayout, friendlyURL,
			languageId, serviceContext);
	}

	/**
	 * Creates a new layout friendly url with the primary key. Does not add the layout friendly url to the database.
	 *
	 * @param layoutFriendlyURLId the primary key for the new layout friendly url
	 * @return the new layout friendly url
	 */
	@Override
	public LayoutFriendlyURL createLayoutFriendlyURL(long layoutFriendlyURLId) {
		return _layoutFriendlyURLLocalService.createLayoutFriendlyURL(
			layoutFriendlyURLId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutFriendlyURLLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the layout friendly url from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutFriendlyURLLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutFriendlyURL the layout friendly url
	 * @return the layout friendly url that was removed
	 */
	@Override
	public LayoutFriendlyURL deleteLayoutFriendlyURL(
		LayoutFriendlyURL layoutFriendlyURL) {

		return _layoutFriendlyURLLocalService.deleteLayoutFriendlyURL(
			layoutFriendlyURL);
	}

	/**
	 * Deletes the layout friendly url with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutFriendlyURLLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutFriendlyURLId the primary key of the layout friendly url
	 * @return the layout friendly url that was removed
	 * @throws PortalException if a layout friendly url with the primary key could not be found
	 */
	@Override
	public LayoutFriendlyURL deleteLayoutFriendlyURL(long layoutFriendlyURLId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutFriendlyURLLocalService.deleteLayoutFriendlyURL(
			layoutFriendlyURLId);
	}

	@Override
	public void deleteLayoutFriendlyURLs(long plid) {
		_layoutFriendlyURLLocalService.deleteLayoutFriendlyURLs(plid);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutFriendlyURLLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _layoutFriendlyURLLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _layoutFriendlyURLLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _layoutFriendlyURLLocalService.dynamicQuery();
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

		return _layoutFriendlyURLLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
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

		return _layoutFriendlyURLLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
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

		return _layoutFriendlyURLLocalService.dynamicQuery(
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

		return _layoutFriendlyURLLocalService.dynamicQueryCount(dynamicQuery);
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

		return _layoutFriendlyURLLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public LayoutFriendlyURL fetchFirstLayoutFriendlyURL(
		long groupId, boolean privateLayout, String friendlyURL) {

		return _layoutFriendlyURLLocalService.fetchFirstLayoutFriendlyURL(
			groupId, privateLayout, friendlyURL);
	}

	@Override
	public LayoutFriendlyURL fetchLayoutFriendlyURL(long layoutFriendlyURLId) {
		return _layoutFriendlyURLLocalService.fetchLayoutFriendlyURL(
			layoutFriendlyURLId);
	}

	@Override
	public LayoutFriendlyURL fetchLayoutFriendlyURL(
		long groupId, boolean privateLayout, String friendlyURL,
		String languageId) {

		return _layoutFriendlyURLLocalService.fetchLayoutFriendlyURL(
			groupId, privateLayout, friendlyURL, languageId);
	}

	@Override
	public LayoutFriendlyURL fetchLayoutFriendlyURL(
		long plid, String languageId, boolean useDefault) {

		return _layoutFriendlyURLLocalService.fetchLayoutFriendlyURL(
			plid, languageId, useDefault);
	}

	/**
	 * Returns the layout friendly url matching the UUID and group.
	 *
	 * @param uuid the layout friendly url's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL fetchLayoutFriendlyURLByUuidAndGroupId(
		String uuid, long groupId) {

		return _layoutFriendlyURLLocalService.
			fetchLayoutFriendlyURLByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _layoutFriendlyURLLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _layoutFriendlyURLLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _layoutFriendlyURLLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the layout friendly url with the primary key.
	 *
	 * @param layoutFriendlyURLId the primary key of the layout friendly url
	 * @return the layout friendly url
	 * @throws PortalException if a layout friendly url with the primary key could not be found
	 */
	@Override
	public LayoutFriendlyURL getLayoutFriendlyURL(long layoutFriendlyURLId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutFriendlyURLLocalService.getLayoutFriendlyURL(
			layoutFriendlyURLId);
	}

	@Override
	public LayoutFriendlyURL getLayoutFriendlyURL(long plid, String languageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutFriendlyURLLocalService.getLayoutFriendlyURL(
			plid, languageId);
	}

	@Override
	public LayoutFriendlyURL getLayoutFriendlyURL(
			long plid, String languageId, boolean useDefault)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutFriendlyURLLocalService.getLayoutFriendlyURL(
			plid, languageId, useDefault);
	}

	/**
	 * Returns the layout friendly url matching the UUID and group.
	 *
	 * @param uuid the layout friendly url's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout friendly url
	 * @throws PortalException if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL getLayoutFriendlyURLByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutFriendlyURLLocalService.
			getLayoutFriendlyURLByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public java.util.Map<Long, String> getLayoutFriendlyURLs(
		com.liferay.portal.kernel.model.Group siteGroup,
		java.util.List<com.liferay.portal.kernel.model.Layout> layouts,
		String languageId) {

		return _layoutFriendlyURLLocalService.getLayoutFriendlyURLs(
			siteGroup, layouts, languageId);
	}

	/**
	 * Returns a range of all the layout friendly urls.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @return the range of layout friendly urls
	 */
	@Override
	public java.util.List<LayoutFriendlyURL> getLayoutFriendlyURLs(
		int start, int end) {

		return _layoutFriendlyURLLocalService.getLayoutFriendlyURLs(start, end);
	}

	@Override
	public java.util.List<LayoutFriendlyURL> getLayoutFriendlyURLs(long plid) {
		return _layoutFriendlyURLLocalService.getLayoutFriendlyURLs(plid);
	}

	@Override
	public java.util.List<LayoutFriendlyURL> getLayoutFriendlyURLs(
		long plid, String friendlyURL, int start, int end) {

		return _layoutFriendlyURLLocalService.getLayoutFriendlyURLs(
			plid, friendlyURL, start, end);
	}

	/**
	 * Returns all the layout friendly urls matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout friendly urls
	 * @param companyId the primary key of the company
	 * @return the matching layout friendly urls, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<LayoutFriendlyURL>
		getLayoutFriendlyURLsByUuidAndCompanyId(String uuid, long companyId) {

		return _layoutFriendlyURLLocalService.
			getLayoutFriendlyURLsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of layout friendly urls matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout friendly urls
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching layout friendly urls, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<LayoutFriendlyURL>
		getLayoutFriendlyURLsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutFriendlyURL>
				orderByComparator) {

		return _layoutFriendlyURLLocalService.
			getLayoutFriendlyURLsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of layout friendly urls.
	 *
	 * @return the number of layout friendly urls
	 */
	@Override
	public int getLayoutFriendlyURLsCount() {
		return _layoutFriendlyURLLocalService.getLayoutFriendlyURLsCount();
	}

	@Override
	public int getLayoutFriendlyURLsCount(long companyId, String friendlyURL) {
		return _layoutFriendlyURLLocalService.getLayoutFriendlyURLsCount(
			companyId, friendlyURL);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _layoutFriendlyURLLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutFriendlyURLLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the layout friendly url in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutFriendlyURLLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutFriendlyURL the layout friendly url
	 * @return the layout friendly url that was updated
	 */
	@Override
	public LayoutFriendlyURL updateLayoutFriendlyURL(
		LayoutFriendlyURL layoutFriendlyURL) {

		return _layoutFriendlyURLLocalService.updateLayoutFriendlyURL(
			layoutFriendlyURL);
	}

	@Override
	public LayoutFriendlyURL updateLayoutFriendlyURL(
			long userId, long companyId, long groupId, long plid,
			boolean privateLayout, String friendlyURL, String languageId,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutFriendlyURLLocalService.updateLayoutFriendlyURL(
			userId, companyId, groupId, plid, privateLayout, friendlyURL,
			languageId, serviceContext);
	}

	@Override
	public java.util.List<LayoutFriendlyURL> updateLayoutFriendlyURLs(
			long userId, long companyId, long groupId, long plid,
			boolean privateLayout,
			java.util.Map<java.util.Locale, String> friendlyURLMap,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutFriendlyURLLocalService.updateLayoutFriendlyURLs(
			userId, companyId, groupId, plid, privateLayout, friendlyURLMap,
			serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _layoutFriendlyURLLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<LayoutFriendlyURL> getCTPersistence() {
		return _layoutFriendlyURLLocalService.getCTPersistence();
	}

	@Override
	public Class<LayoutFriendlyURL> getModelClass() {
		return _layoutFriendlyURLLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<LayoutFriendlyURL>, R, E>
				updateUnsafeFunction)
		throws E {

		return _layoutFriendlyURLLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public LayoutFriendlyURLLocalService getWrappedService() {
		return _layoutFriendlyURLLocalService;
	}

	@Override
	public void setWrappedService(
		LayoutFriendlyURLLocalService layoutFriendlyURLLocalService) {

		_layoutFriendlyURLLocalService = layoutFriendlyURLLocalService;
	}

	private LayoutFriendlyURLLocalService _layoutFriendlyURLLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1701198509