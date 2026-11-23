/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.wiki.model.WikiPageResource;

/**
 * Provides a wrapper for {@link WikiPageResourceLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see WikiPageResourceLocalService
 * @generated
 */
public class WikiPageResourceLocalServiceWrapper
	implements ServiceWrapper<WikiPageResourceLocalService>,
			   WikiPageResourceLocalService {

	public WikiPageResourceLocalServiceWrapper() {
		this(null);
	}

	public WikiPageResourceLocalServiceWrapper(
		WikiPageResourceLocalService wikiPageResourceLocalService) {

		_wikiPageResourceLocalService = wikiPageResourceLocalService;
	}

	@Override
	public WikiPageResource addPageResource(
		long groupId, long nodeId, String title) {

		return _wikiPageResourceLocalService.addPageResource(
			groupId, nodeId, title);
	}

	/**
	 * Adds the wiki page resource to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WikiPageResourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param wikiPageResource the wiki page resource
	 * @return the wiki page resource that was added
	 */
	@Override
	public WikiPageResource addWikiPageResource(
		WikiPageResource wikiPageResource) {

		return _wikiPageResourceLocalService.addWikiPageResource(
			wikiPageResource);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageResourceLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new wiki page resource with the primary key. Does not add the wiki page resource to the database.
	 *
	 * @param resourcePrimKey the primary key for the new wiki page resource
	 * @return the new wiki page resource
	 */
	@Override
	public WikiPageResource createWikiPageResource(long resourcePrimKey) {
		return _wikiPageResourceLocalService.createWikiPageResource(
			resourcePrimKey);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageResourceLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the wiki page resource with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WikiPageResourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param resourcePrimKey the primary key of the wiki page resource
	 * @return the wiki page resource that was removed
	 * @throws PortalException if a wiki page resource with the primary key could not be found
	 */
	@Override
	public WikiPageResource deleteWikiPageResource(long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageResourceLocalService.deleteWikiPageResource(
			resourcePrimKey);
	}

	/**
	 * Deletes the wiki page resource from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WikiPageResourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param wikiPageResource the wiki page resource
	 * @return the wiki page resource that was removed
	 */
	@Override
	public WikiPageResource deleteWikiPageResource(
		WikiPageResource wikiPageResource) {

		return _wikiPageResourceLocalService.deleteWikiPageResource(
			wikiPageResource);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _wikiPageResourceLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _wikiPageResourceLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _wikiPageResourceLocalService.dynamicQuery();
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

		return _wikiPageResourceLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiPageResourceModelImpl</code>.
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

		return _wikiPageResourceLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiPageResourceModelImpl</code>.
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

		return _wikiPageResourceLocalService.dynamicQuery(
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

		return _wikiPageResourceLocalService.dynamicQueryCount(dynamicQuery);
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

		return _wikiPageResourceLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public WikiPageResource fetchPageResource(long nodeId, String title) {
		return _wikiPageResourceLocalService.fetchPageResource(nodeId, title);
	}

	@Override
	public WikiPageResource fetchWikiPageResource(long resourcePrimKey) {
		return _wikiPageResourceLocalService.fetchWikiPageResource(
			resourcePrimKey);
	}

	/**
	 * Returns the wiki page resource matching the UUID and group.
	 *
	 * @param uuid the wiki page resource's UUID
	 * @param groupId the primary key of the group
	 * @return the matching wiki page resource, or <code>null</code> if a matching wiki page resource could not be found
	 */
	@Override
	public WikiPageResource fetchWikiPageResourceByUuidAndGroupId(
		String uuid, long groupId) {

		return _wikiPageResourceLocalService.
			fetchWikiPageResourceByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _wikiPageResourceLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _wikiPageResourceLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _wikiPageResourceLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public WikiPageResource getPageResource(long pageResourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageResourceLocalService.getPageResource(
			pageResourcePrimKey);
	}

	@Override
	public long getPageResourcePrimKey(
		long groupId, long nodeId, String title) {

		return _wikiPageResourceLocalService.getPageResourcePrimKey(
			groupId, nodeId, title);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageResourceLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the wiki page resource with the primary key.
	 *
	 * @param resourcePrimKey the primary key of the wiki page resource
	 * @return the wiki page resource
	 * @throws PortalException if a wiki page resource with the primary key could not be found
	 */
	@Override
	public WikiPageResource getWikiPageResource(long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageResourceLocalService.getWikiPageResource(
			resourcePrimKey);
	}

	/**
	 * Returns the wiki page resource matching the UUID and group.
	 *
	 * @param uuid the wiki page resource's UUID
	 * @param groupId the primary key of the group
	 * @return the matching wiki page resource
	 * @throws PortalException if a matching wiki page resource could not be found
	 */
	@Override
	public WikiPageResource getWikiPageResourceByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageResourceLocalService.
			getWikiPageResourceByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the wiki page resources.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiPageResourceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of wiki page resources
	 * @param end the upper bound of the range of wiki page resources (not inclusive)
	 * @return the range of wiki page resources
	 */
	@Override
	public java.util.List<WikiPageResource> getWikiPageResources(
		int start, int end) {

		return _wikiPageResourceLocalService.getWikiPageResources(start, end);
	}

	/**
	 * Returns all the wiki page resources matching the UUID and company.
	 *
	 * @param uuid the UUID of the wiki page resources
	 * @param companyId the primary key of the company
	 * @return the matching wiki page resources, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<WikiPageResource>
		getWikiPageResourcesByUuidAndCompanyId(String uuid, long companyId) {

		return _wikiPageResourceLocalService.
			getWikiPageResourcesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of wiki page resources matching the UUID and company.
	 *
	 * @param uuid the UUID of the wiki page resources
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of wiki page resources
	 * @param end the upper bound of the range of wiki page resources (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching wiki page resources, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<WikiPageResource>
		getWikiPageResourcesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<WikiPageResource>
				orderByComparator) {

		return _wikiPageResourceLocalService.
			getWikiPageResourcesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of wiki page resources.
	 *
	 * @return the number of wiki page resources
	 */
	@Override
	public int getWikiPageResourcesCount() {
		return _wikiPageResourceLocalService.getWikiPageResourcesCount();
	}

	/**
	 * Updates the wiki page resource in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WikiPageResourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param wikiPageResource the wiki page resource
	 * @return the wiki page resource that was updated
	 */
	@Override
	public WikiPageResource updateWikiPageResource(
		WikiPageResource wikiPageResource) {

		return _wikiPageResourceLocalService.updateWikiPageResource(
			wikiPageResource);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _wikiPageResourceLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<WikiPageResource> getCTPersistence() {
		return _wikiPageResourceLocalService.getCTPersistence();
	}

	@Override
	public Class<WikiPageResource> getModelClass() {
		return _wikiPageResourceLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<WikiPageResource>, R, E>
				updateUnsafeFunction)
		throws E {

		return _wikiPageResourceLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public WikiPageResourceLocalService getWrappedService() {
		return _wikiPageResourceLocalService;
	}

	@Override
	public void setWrappedService(
		WikiPageResourceLocalService wikiPageResourceLocalService) {

		_wikiPageResourceLocalService = wikiPageResourceLocalService;
	}

	private WikiPageResourceLocalService _wikiPageResourceLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1372609715