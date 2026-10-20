/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service;

import com.liferay.journal.model.JournalFeed;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for JournalFeed. This utility wraps
 * <code>com.liferay.journal.service.impl.JournalFeedLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see JournalFeedLocalService
 * @generated
 */
public class JournalFeedLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.journal.service.impl.JournalFeedLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static JournalFeed addFeed(
			long userId, long groupId, String feedId, boolean autoFeedId,
			String name, String description, long ddmStructureId,
			String ddmTemplateKey, String ddmRendererTemplateKey, int delta,
			String orderByCol, String orderByType,
			String targetLayoutFriendlyUrl, String targetPortletId,
			String contentField, String feedFormat, double feedVersion,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFeed(
			userId, groupId, feedId, autoFeedId, name, description,
			ddmStructureId, ddmTemplateKey, ddmRendererTemplateKey, delta,
			orderByCol, orderByType, targetLayoutFriendlyUrl, targetPortletId,
			contentField, feedFormat, feedVersion, serviceContext);
	}

	public static void addFeedResources(
			JournalFeed feed, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addFeedResources(
			feed, addGroupPermissions, addGuestPermissions);
	}

	public static void addFeedResources(
			JournalFeed feed,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().addFeedResources(feed, modelPermissions);
	}

	public static void addFeedResources(
			long feedId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addFeedResources(
			feedId, addGroupPermissions, addGuestPermissions);
	}

	/**
	 * Adds the journal feed to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalFeedLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalFeed the journal feed
	 * @return the journal feed that was added
	 */
	public static JournalFeed addJournalFeed(JournalFeed journalFeed) {
		return getService().addJournalFeed(journalFeed);
	}

	/**
	 * Creates a new journal feed with the primary key. Does not add the journal feed to the database.
	 *
	 * @param id the primary key for the new journal feed
	 * @return the new journal feed
	 */
	public static JournalFeed createJournalFeed(long id) {
		return getService().createJournalFeed(id);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteFeed(JournalFeed feed) throws PortalException {
		getService().deleteFeed(feed);
	}

	public static void deleteFeed(long feedId) throws PortalException {
		getService().deleteFeed(feedId);
	}

	public static void deleteFeed(long groupId, String feedId)
		throws PortalException {

		getService().deleteFeed(groupId, feedId);
	}

	/**
	 * Deletes the journal feed from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalFeedLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalFeed the journal feed
	 * @return the journal feed that was removed
	 */
	public static JournalFeed deleteJournalFeed(JournalFeed journalFeed) {
		return getService().deleteJournalFeed(journalFeed);
	}

	/**
	 * Deletes the journal feed with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalFeedLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param id the primary key of the journal feed
	 * @return the journal feed that was removed
	 * @throws PortalException if a journal feed with the primary key could not be found
	 */
	public static JournalFeed deleteJournalFeed(long id)
		throws PortalException {

		return getService().deleteJournalFeed(id);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalFeedModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalFeedModelImpl</code>.
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

	public static JournalFeed fetchFeed(long groupId, String feedId) {
		return getService().fetchFeed(groupId, feedId);
	}

	public static JournalFeed fetchJournalFeed(long id) {
		return getService().fetchJournalFeed(id);
	}

	/**
	 * Returns the journal feed matching the UUID and group.
	 *
	 * @param uuid the journal feed's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal feed, or <code>null</code> if a matching journal feed could not be found
	 */
	public static JournalFeed fetchJournalFeedByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchJournalFeedByUuidAndGroupId(uuid, groupId);
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

	public static JournalFeed getFeed(long feedId) throws PortalException {
		return getService().getFeed(feedId);
	}

	public static JournalFeed getFeed(long groupId, String feedId)
		throws PortalException {

		return getService().getFeed(groupId, feedId);
	}

	public static List<JournalFeed> getFeeds() {
		return getService().getFeeds();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the journal feed with the primary key.
	 *
	 * @param id the primary key of the journal feed
	 * @return the journal feed
	 * @throws PortalException if a journal feed with the primary key could not be found
	 */
	public static JournalFeed getJournalFeed(long id) throws PortalException {
		return getService().getJournalFeed(id);
	}

	/**
	 * Returns the journal feed matching the UUID and group.
	 *
	 * @param uuid the journal feed's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal feed
	 * @throws PortalException if a matching journal feed could not be found
	 */
	public static JournalFeed getJournalFeedByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getJournalFeedByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the journal feeds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalFeedModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of journal feeds
	 * @param end the upper bound of the range of journal feeds (not inclusive)
	 * @return the range of journal feeds
	 */
	public static List<JournalFeed> getJournalFeeds(int start, int end) {
		return getService().getJournalFeeds(start, end);
	}

	/**
	 * Returns all the journal feeds matching the UUID and company.
	 *
	 * @param uuid the UUID of the journal feeds
	 * @param companyId the primary key of the company
	 * @return the matching journal feeds, or an empty list if no matches were found
	 */
	public static List<JournalFeed> getJournalFeedsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getJournalFeedsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of journal feeds matching the UUID and company.
	 *
	 * @param uuid the UUID of the journal feeds
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of journal feeds
	 * @param end the upper bound of the range of journal feeds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching journal feeds, or an empty list if no matches were found
	 */
	public static List<JournalFeed> getJournalFeedsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<JournalFeed> orderByComparator) {

		return getService().getJournalFeedsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of journal feeds.
	 *
	 * @return the number of journal feeds
	 */
	public static int getJournalFeedsCount() {
		return getService().getJournalFeedsCount();
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

	public static List<JournalFeed> search(
		long companyId, long groupId, String keywords, int start, int end,
		OrderByComparator<JournalFeed> orderByComparator) {

		return getService().search(
			companyId, groupId, keywords, start, end, orderByComparator);
	}

	public static int searchCount(
		long companyId, long groupId, String keywords) {

		return getService().searchCount(companyId, groupId, keywords);
	}

	public static JournalFeed updateFeed(
			long groupId, String feedId, String name, String description,
			long ddmStructureId, String ddmTemplateKey,
			String ddmRendererTemplateKey, int delta, String orderByCol,
			String orderByType, String targetLayoutFriendlyUrl,
			String targetPortletId, String contentField, String feedFormat,
			double feedVersion,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFeed(
			groupId, feedId, name, description, ddmStructureId, ddmTemplateKey,
			ddmRendererTemplateKey, delta, orderByCol, orderByType,
			targetLayoutFriendlyUrl, targetPortletId, contentField, feedFormat,
			feedVersion, serviceContext);
	}

	/**
	 * Updates the journal feed in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalFeedLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalFeed the journal feed
	 * @return the journal feed that was updated
	 */
	public static JournalFeed updateJournalFeed(JournalFeed journalFeed) {
		return getService().updateJournalFeed(journalFeed);
	}

	public static JournalFeedLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<JournalFeedLocalService> _serviceSnapshot =
		new Snapshot<>(
			JournalFeedLocalServiceUtil.class, JournalFeedLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-270094806