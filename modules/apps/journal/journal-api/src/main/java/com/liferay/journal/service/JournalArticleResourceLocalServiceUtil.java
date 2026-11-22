/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service;

import com.liferay.journal.model.JournalArticleResource;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for JournalArticleResource. This utility wraps
 * <code>com.liferay.journal.service.impl.JournalArticleResourceLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see JournalArticleResourceLocalService
 * @generated
 */
public class JournalArticleResourceLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.journal.service.impl.JournalArticleResourceLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the journal article resource to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalArticleResourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalArticleResource the journal article resource
	 * @return the journal article resource that was added
	 */
	public static JournalArticleResource addJournalArticleResource(
		JournalArticleResource journalArticleResource) {

		return getService().addJournalArticleResource(journalArticleResource);
	}

	/**
	 * Creates a new journal article resource with the primary key. Does not add the journal article resource to the database.
	 *
	 * @param resourcePrimKey the primary key for the new journal article resource
	 * @return the new journal article resource
	 */
	public static JournalArticleResource createJournalArticleResource(
		long resourcePrimKey) {

		return getService().createJournalArticleResource(resourcePrimKey);
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
	 * Deletes the journal article resource from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalArticleResourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalArticleResource the journal article resource
	 * @return the journal article resource that was removed
	 */
	public static JournalArticleResource deleteJournalArticleResource(
		JournalArticleResource journalArticleResource) {

		return getService().deleteJournalArticleResource(
			journalArticleResource);
	}

	/**
	 * Deletes the journal article resource with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalArticleResourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param resourcePrimKey the primary key of the journal article resource
	 * @return the journal article resource that was removed
	 * @throws PortalException if a journal article resource with the primary key could not be found
	 */
	public static JournalArticleResource deleteJournalArticleResource(
			long resourcePrimKey)
		throws PortalException {

		return getService().deleteJournalArticleResource(resourcePrimKey);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleResourceModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleResourceModelImpl</code>.
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

	public static JournalArticleResource fetchArticleResource(
		long groupId, String articleId) {

		return getService().fetchArticleResource(groupId, articleId);
	}

	public static JournalArticleResource fetchArticleResource(
		String uuid, long groupId) {

		return getService().fetchArticleResource(uuid, groupId);
	}

	public static JournalArticleResource fetchJournalArticleResource(
		long resourcePrimKey) {

		return getService().fetchJournalArticleResource(resourcePrimKey);
	}

	/**
	 * Returns the journal article resource matching the UUID and group.
	 *
	 * @param uuid the journal article resource's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal article resource, or <code>null</code> if a matching journal article resource could not be found
	 */
	public static JournalArticleResource
		fetchJournalArticleResourceByUuidAndGroupId(String uuid, long groupId) {

		return getService().fetchJournalArticleResourceByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static JournalArticleResource getArticleResource(
			long articleResourcePrimKey)
		throws PortalException {

		return getService().getArticleResource(articleResourcePrimKey);
	}

	public static long getArticleResourcePrimKey(
		long groupId, String articleId) {

		return getService().getArticleResourcePrimKey(groupId, articleId);
	}

	public static long getArticleResourcePrimKey(
		String uuid, long groupId, String articleId) {

		return getService().getArticleResourcePrimKey(uuid, groupId, articleId);
	}

	public static List<JournalArticleResource> getArticleResources(
		long groupId) {

		return getService().getArticleResources(groupId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the journal article resource with the primary key.
	 *
	 * @param resourcePrimKey the primary key of the journal article resource
	 * @return the journal article resource
	 * @throws PortalException if a journal article resource with the primary key could not be found
	 */
	public static JournalArticleResource getJournalArticleResource(
			long resourcePrimKey)
		throws PortalException {

		return getService().getJournalArticleResource(resourcePrimKey);
	}

	/**
	 * Returns the journal article resource matching the UUID and group.
	 *
	 * @param uuid the journal article resource's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal article resource
	 * @throws PortalException if a matching journal article resource could not be found
	 */
	public static JournalArticleResource
			getJournalArticleResourceByUuidAndGroupId(String uuid, long groupId)
		throws PortalException {

		return getService().getJournalArticleResourceByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the journal article resources.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleResourceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of journal article resources
	 * @param end the upper bound of the range of journal article resources (not inclusive)
	 * @return the range of journal article resources
	 */
	public static List<JournalArticleResource> getJournalArticleResources(
		int start, int end) {

		return getService().getJournalArticleResources(start, end);
	}

	/**
	 * Returns all the journal article resources matching the UUID and company.
	 *
	 * @param uuid the UUID of the journal article resources
	 * @param companyId the primary key of the company
	 * @return the matching journal article resources, or an empty list if no matches were found
	 */
	public static List<JournalArticleResource>
		getJournalArticleResourcesByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getJournalArticleResourcesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of journal article resources matching the UUID and company.
	 *
	 * @param uuid the UUID of the journal article resources
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of journal article resources
	 * @param end the upper bound of the range of journal article resources (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching journal article resources, or an empty list if no matches were found
	 */
	public static List<JournalArticleResource>
		getJournalArticleResourcesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<JournalArticleResource> orderByComparator) {

		return getService().getJournalArticleResourcesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of journal article resources.
	 *
	 * @return the number of journal article resources
	 */
	public static int getJournalArticleResourcesCount() {
		return getService().getJournalArticleResourcesCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the journal article resource in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalArticleResourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalArticleResource the journal article resource
	 * @return the journal article resource that was updated
	 */
	public static JournalArticleResource updateJournalArticleResource(
		JournalArticleResource journalArticleResource) {

		return getService().updateJournalArticleResource(
			journalArticleResource);
	}

	public static JournalArticleResourceLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<JournalArticleResourceLocalService>
		_serviceSnapshot = new Snapshot<>(
			JournalArticleResourceLocalServiceUtil.class,
			JournalArticleResourceLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:915998774