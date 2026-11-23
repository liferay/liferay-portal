/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.wiki.model.WikiPageResource;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for WikiPageResource. This utility wraps
 * <code>com.liferay.wiki.service.impl.WikiPageResourceLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see WikiPageResourceLocalService
 * @generated
 */
public class WikiPageResourceLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.wiki.service.impl.WikiPageResourceLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static WikiPageResource addPageResource(
		long groupId, long nodeId, String title) {

		return getService().addPageResource(groupId, nodeId, title);
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
	public static WikiPageResource addWikiPageResource(
		WikiPageResource wikiPageResource) {

		return getService().addWikiPageResource(wikiPageResource);
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
	 * Creates a new wiki page resource with the primary key. Does not add the wiki page resource to the database.
	 *
	 * @param resourcePrimKey the primary key for the new wiki page resource
	 * @return the new wiki page resource
	 */
	public static WikiPageResource createWikiPageResource(
		long resourcePrimKey) {

		return getService().createWikiPageResource(resourcePrimKey);
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
	public static WikiPageResource deleteWikiPageResource(long resourcePrimKey)
		throws PortalException {

		return getService().deleteWikiPageResource(resourcePrimKey);
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
	public static WikiPageResource deleteWikiPageResource(
		WikiPageResource wikiPageResource) {

		return getService().deleteWikiPageResource(wikiPageResource);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiPageResourceModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiPageResourceModelImpl</code>.
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

	public static WikiPageResource fetchPageResource(
		long nodeId, String title) {

		return getService().fetchPageResource(nodeId, title);
	}

	public static WikiPageResource fetchWikiPageResource(long resourcePrimKey) {
		return getService().fetchWikiPageResource(resourcePrimKey);
	}

	/**
	 * Returns the wiki page resource matching the UUID and group.
	 *
	 * @param uuid the wiki page resource's UUID
	 * @param groupId the primary key of the group
	 * @return the matching wiki page resource, or <code>null</code> if a matching wiki page resource could not be found
	 */
	public static WikiPageResource fetchWikiPageResourceByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchWikiPageResourceByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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

	public static WikiPageResource getPageResource(long pageResourcePrimKey)
		throws PortalException {

		return getService().getPageResource(pageResourcePrimKey);
	}

	public static long getPageResourcePrimKey(
		long groupId, long nodeId, String title) {

		return getService().getPageResourcePrimKey(groupId, nodeId, title);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the wiki page resource with the primary key.
	 *
	 * @param resourcePrimKey the primary key of the wiki page resource
	 * @return the wiki page resource
	 * @throws PortalException if a wiki page resource with the primary key could not be found
	 */
	public static WikiPageResource getWikiPageResource(long resourcePrimKey)
		throws PortalException {

		return getService().getWikiPageResource(resourcePrimKey);
	}

	/**
	 * Returns the wiki page resource matching the UUID and group.
	 *
	 * @param uuid the wiki page resource's UUID
	 * @param groupId the primary key of the group
	 * @return the matching wiki page resource
	 * @throws PortalException if a matching wiki page resource could not be found
	 */
	public static WikiPageResource getWikiPageResourceByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getWikiPageResourceByUuidAndGroupId(uuid, groupId);
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
	public static List<WikiPageResource> getWikiPageResources(
		int start, int end) {

		return getService().getWikiPageResources(start, end);
	}

	/**
	 * Returns all the wiki page resources matching the UUID and company.
	 *
	 * @param uuid the UUID of the wiki page resources
	 * @param companyId the primary key of the company
	 * @return the matching wiki page resources, or an empty list if no matches were found
	 */
	public static List<WikiPageResource> getWikiPageResourcesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getWikiPageResourcesByUuidAndCompanyId(
			uuid, companyId);
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
	public static List<WikiPageResource> getWikiPageResourcesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<WikiPageResource> orderByComparator) {

		return getService().getWikiPageResourcesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of wiki page resources.
	 *
	 * @return the number of wiki page resources
	 */
	public static int getWikiPageResourcesCount() {
		return getService().getWikiPageResourcesCount();
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
	public static WikiPageResource updateWikiPageResource(
		WikiPageResource wikiPageResource) {

		return getService().updateWikiPageResource(wikiPageResource);
	}

	public static WikiPageResourceLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<WikiPageResourceLocalService>
		_serviceSnapshot = new Snapshot<>(
			WikiPageResourceLocalServiceUtil.class,
			WikiPageResourceLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:305875388