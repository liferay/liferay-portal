/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link WebsiteLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see WebsiteLocalService
 * @generated
 */
public class WebsiteLocalServiceWrapper
	implements ServiceWrapper<WebsiteLocalService>, WebsiteLocalService {

	public WebsiteLocalServiceWrapper() {
		this(null);
	}

	public WebsiteLocalServiceWrapper(WebsiteLocalService websiteLocalService) {
		_websiteLocalService = websiteLocalService;
	}

	@Override
	public com.liferay.portal.kernel.model.Website addWebsite(
			String externalReferenceCode, long userId, String className,
			long classPK, String url, long listTypeId, boolean primary,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _websiteLocalService.addWebsite(
			externalReferenceCode, userId, className, classPK, url, listTypeId,
			primary, serviceContext);
	}

	/**
	 * Adds the website to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WebsiteLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param website the website
	 * @return the website that was added
	 */
	@Override
	public com.liferay.portal.kernel.model.Website addWebsite(
		com.liferay.portal.kernel.model.Website website) {

		return _websiteLocalService.addWebsite(website);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _websiteLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new website with the primary key. Does not add the website to the database.
	 *
	 * @param websiteId the primary key for the new website
	 * @return the new website
	 */
	@Override
	public com.liferay.portal.kernel.model.Website createWebsite(
		long websiteId) {

		return _websiteLocalService.createWebsite(websiteId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _websiteLocalService.deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the website with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WebsiteLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param websiteId the primary key of the website
	 * @return the website that was removed
	 * @throws PortalException if a website with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.Website deleteWebsite(long websiteId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _websiteLocalService.deleteWebsite(websiteId);
	}

	/**
	 * Deletes the website from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WebsiteLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param website the website
	 * @return the website that was removed
	 */
	@Override
	public com.liferay.portal.kernel.model.Website deleteWebsite(
		com.liferay.portal.kernel.model.Website website) {

		return _websiteLocalService.deleteWebsite(website);
	}

	@Override
	public void deleteWebsites(long companyId, String className, long classPK) {
		_websiteLocalService.deleteWebsites(companyId, className, classPK);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _websiteLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _websiteLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _websiteLocalService.dynamicQuery();
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

		return _websiteLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.WebsiteModelImpl</code>.
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

		return _websiteLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.WebsiteModelImpl</code>.
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

		return _websiteLocalService.dynamicQuery(
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

		return _websiteLocalService.dynamicQueryCount(dynamicQuery);
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

		return _websiteLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.kernel.model.Website fetchWebsite(
		long websiteId) {

		return _websiteLocalService.fetchWebsite(websiteId);
	}

	@Override
	public com.liferay.portal.kernel.model.Website
		fetchWebsiteByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _websiteLocalService.fetchWebsiteByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the website with the matching UUID and company.
	 *
	 * @param uuid the website's UUID
	 * @param companyId the primary key of the company
	 * @return the matching website, or <code>null</code> if a matching website could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.Website
		fetchWebsiteByUuidAndCompanyId(String uuid, long companyId) {

		return _websiteLocalService.fetchWebsiteByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _websiteLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _websiteLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _websiteLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _websiteLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _websiteLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the website with the primary key.
	 *
	 * @param websiteId the primary key of the website
	 * @return the website
	 * @throws PortalException if a website with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.Website getWebsite(long websiteId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _websiteLocalService.getWebsite(websiteId);
	}

	@Override
	public com.liferay.portal.kernel.model.Website
			getWebsiteByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _websiteLocalService.getWebsiteByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the website with the matching UUID and company.
	 *
	 * @param uuid the website's UUID
	 * @param companyId the primary key of the company
	 * @return the matching website
	 * @throws PortalException if a matching website could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.Website getWebsiteByUuidAndCompanyId(
			String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _websiteLocalService.getWebsiteByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the websites.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.WebsiteModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of websites
	 * @param end the upper bound of the range of websites (not inclusive)
	 * @return the range of websites
	 */
	@Override
	public java.util.List<com.liferay.portal.kernel.model.Website> getWebsites(
		int start, int end) {

		return _websiteLocalService.getWebsites(start, end);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.Website> getWebsites(
		long companyId, String className, long classPK) {

		return _websiteLocalService.getWebsites(companyId, className, classPK);
	}

	/**
	 * Returns the number of websites.
	 *
	 * @return the number of websites
	 */
	@Override
	public int getWebsitesCount() {
		return _websiteLocalService.getWebsitesCount();
	}

	@Override
	public com.liferay.portal.kernel.model.Website updateWebsite(
			String externalReferenceCode, long websiteId, String url,
			long listTypeId, boolean primary)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _websiteLocalService.updateWebsite(
			externalReferenceCode, websiteId, url, listTypeId, primary);
	}

	/**
	 * Updates the website in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WebsiteLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param website the website
	 * @return the website that was updated
	 */
	@Override
	public com.liferay.portal.kernel.model.Website updateWebsite(
		com.liferay.portal.kernel.model.Website website) {

		return _websiteLocalService.updateWebsite(website);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _websiteLocalService.getBasePersistence();
	}

	@Override
	public WebsiteLocalService getWrappedService() {
		return _websiteLocalService;
	}

	@Override
	public void setWrappedService(WebsiteLocalService websiteLocalService) {
		_websiteLocalService = websiteLocalService;
	}

	private WebsiteLocalService _websiteLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:140355360