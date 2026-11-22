/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link WebDAVPropsLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see WebDAVPropsLocalService
 * @generated
 */
public class WebDAVPropsLocalServiceWrapper
	implements ServiceWrapper<WebDAVPropsLocalService>,
			   WebDAVPropsLocalService {

	public WebDAVPropsLocalServiceWrapper() {
		this(null);
	}

	public WebDAVPropsLocalServiceWrapper(
		WebDAVPropsLocalService webDAVPropsLocalService) {

		_webDAVPropsLocalService = webDAVPropsLocalService;
	}

	/**
	 * Adds the web dav props to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WebDAVPropsLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param webDAVProps the web dav props
	 * @return the web dav props that was added
	 */
	@Override
	public com.liferay.portal.kernel.model.WebDAVProps addWebDAVProps(
		com.liferay.portal.kernel.model.WebDAVProps webDAVProps) {

		return _webDAVPropsLocalService.addWebDAVProps(webDAVProps);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _webDAVPropsLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new web dav props with the primary key. Does not add the web dav props to the database.
	 *
	 * @param webDavPropsId the primary key for the new web dav props
	 * @return the new web dav props
	 */
	@Override
	public com.liferay.portal.kernel.model.WebDAVProps createWebDAVProps(
		long webDavPropsId) {

		return _webDAVPropsLocalService.createWebDAVProps(webDavPropsId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _webDAVPropsLocalService.deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the web dav props with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WebDAVPropsLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param webDavPropsId the primary key of the web dav props
	 * @return the web dav props that was removed
	 * @throws PortalException if a web dav props with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.WebDAVProps deleteWebDAVProps(
			long webDavPropsId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _webDAVPropsLocalService.deleteWebDAVProps(webDavPropsId);
	}

	/**
	 * Deletes the web dav props from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WebDAVPropsLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param webDAVProps the web dav props
	 * @return the web dav props that was removed
	 */
	@Override
	public com.liferay.portal.kernel.model.WebDAVProps deleteWebDAVProps(
		com.liferay.portal.kernel.model.WebDAVProps webDAVProps) {

		return _webDAVPropsLocalService.deleteWebDAVProps(webDAVProps);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _webDAVPropsLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _webDAVPropsLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _webDAVPropsLocalService.dynamicQuery();
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

		return _webDAVPropsLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.WebDAVPropsModelImpl</code>.
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

		return _webDAVPropsLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.WebDAVPropsModelImpl</code>.
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

		return _webDAVPropsLocalService.dynamicQuery(
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

		return _webDAVPropsLocalService.dynamicQueryCount(dynamicQuery);
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

		return _webDAVPropsLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.kernel.model.WebDAVProps fetchWebDAVProps(
		long webDavPropsId) {

		return _webDAVPropsLocalService.fetchWebDAVProps(webDavPropsId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _webDAVPropsLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _webDAVPropsLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _webDAVPropsLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _webDAVPropsLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the web dav props with the primary key.
	 *
	 * @param webDavPropsId the primary key of the web dav props
	 * @return the web dav props
	 * @throws PortalException if a web dav props with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.WebDAVProps getWebDAVProps(
			long webDavPropsId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _webDAVPropsLocalService.getWebDAVProps(webDavPropsId);
	}

	@Override
	public com.liferay.portal.kernel.model.WebDAVProps getWebDAVProps(
		long companyId, String className, long classPK) {

		return _webDAVPropsLocalService.getWebDAVProps(
			companyId, className, classPK);
	}

	/**
	 * Returns a range of all the web dav propses.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.WebDAVPropsModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of web dav propses
	 * @param end the upper bound of the range of web dav propses (not inclusive)
	 * @return the range of web dav propses
	 */
	@Override
	public java.util.List<com.liferay.portal.kernel.model.WebDAVProps>
		getWebDAVPropses(int start, int end) {

		return _webDAVPropsLocalService.getWebDAVPropses(start, end);
	}

	/**
	 * Returns the number of web dav propses.
	 *
	 * @return the number of web dav propses
	 */
	@Override
	public int getWebDAVPropsesCount() {
		return _webDAVPropsLocalService.getWebDAVPropsesCount();
	}

	@Override
	public void storeWebDAVProps(
			com.liferay.portal.kernel.model.WebDAVProps webDAVProps)
		throws com.liferay.portal.kernel.exception.PortalException {

		_webDAVPropsLocalService.storeWebDAVProps(webDAVProps);
	}

	/**
	 * Updates the web dav props in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WebDAVPropsLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param webDAVProps the web dav props
	 * @return the web dav props that was updated
	 */
	@Override
	public com.liferay.portal.kernel.model.WebDAVProps updateWebDAVProps(
		com.liferay.portal.kernel.model.WebDAVProps webDAVProps) {

		return _webDAVPropsLocalService.updateWebDAVProps(webDAVProps);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _webDAVPropsLocalService.getBasePersistence();
	}

	@Override
	public WebDAVPropsLocalService getWrappedService() {
		return _webDAVPropsLocalService;
	}

	@Override
	public void setWrappedService(
		WebDAVPropsLocalService webDAVPropsLocalService) {

		_webDAVPropsLocalService = webDAVPropsLocalService;
	}

	private WebDAVPropsLocalService _webDAVPropsLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1455854710