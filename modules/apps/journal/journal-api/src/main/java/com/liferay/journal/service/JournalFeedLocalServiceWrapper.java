/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service;

import com.liferay.journal.model.JournalFeed;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link JournalFeedLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see JournalFeedLocalService
 * @generated
 */
public class JournalFeedLocalServiceWrapper
	implements JournalFeedLocalService,
			   ServiceWrapper<JournalFeedLocalService> {

	public JournalFeedLocalServiceWrapper() {
		this(null);
	}

	public JournalFeedLocalServiceWrapper(
		JournalFeedLocalService journalFeedLocalService) {

		_journalFeedLocalService = journalFeedLocalService;
	}

	@Override
	public JournalFeed addFeed(
			long userId, long groupId, String feedId, boolean autoFeedId,
			String name, String description, long ddmStructureId,
			String ddmTemplateKey, String ddmRendererTemplateKey, int delta,
			String orderByCol, String orderByType,
			String targetLayoutFriendlyUrl, String targetPortletId,
			String contentField, String feedFormat, double feedVersion,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFeedLocalService.addFeed(
			userId, groupId, feedId, autoFeedId, name, description,
			ddmStructureId, ddmTemplateKey, ddmRendererTemplateKey, delta,
			orderByCol, orderByType, targetLayoutFriendlyUrl, targetPortletId,
			contentField, feedFormat, feedVersion, serviceContext);
	}

	@Override
	public void addFeedResources(
			JournalFeed feed, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalFeedLocalService.addFeedResources(
			feed, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addFeedResources(
			JournalFeed feed,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalFeedLocalService.addFeedResources(feed, modelPermissions);
	}

	@Override
	public void addFeedResources(
			long feedId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalFeedLocalService.addFeedResources(
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
	@Override
	public JournalFeed addJournalFeed(JournalFeed journalFeed) {
		return _journalFeedLocalService.addJournalFeed(journalFeed);
	}

	/**
	 * Creates a new journal feed with the primary key. Does not add the journal feed to the database.
	 *
	 * @param id the primary key for the new journal feed
	 * @return the new journal feed
	 */
	@Override
	public JournalFeed createJournalFeed(long id) {
		return _journalFeedLocalService.createJournalFeed(id);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFeedLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteFeed(JournalFeed feed)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalFeedLocalService.deleteFeed(feed);
	}

	@Override
	public void deleteFeed(long feedId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalFeedLocalService.deleteFeed(feedId);
	}

	@Override
	public void deleteFeed(long groupId, String feedId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalFeedLocalService.deleteFeed(groupId, feedId);
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
	@Override
	public JournalFeed deleteJournalFeed(JournalFeed journalFeed) {
		return _journalFeedLocalService.deleteJournalFeed(journalFeed);
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
	@Override
	public JournalFeed deleteJournalFeed(long id)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFeedLocalService.deleteJournalFeed(id);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFeedLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _journalFeedLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _journalFeedLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _journalFeedLocalService.dynamicQuery();
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

		return _journalFeedLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _journalFeedLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _journalFeedLocalService.dynamicQuery(
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

		return _journalFeedLocalService.dynamicQueryCount(dynamicQuery);
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

		return _journalFeedLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public JournalFeed fetchFeed(long groupId, String feedId) {
		return _journalFeedLocalService.fetchFeed(groupId, feedId);
	}

	@Override
	public JournalFeed fetchJournalFeed(long id) {
		return _journalFeedLocalService.fetchJournalFeed(id);
	}

	/**
	 * Returns the journal feed matching the UUID and group.
	 *
	 * @param uuid the journal feed's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal feed, or <code>null</code> if a matching journal feed could not be found
	 */
	@Override
	public JournalFeed fetchJournalFeedByUuidAndGroupId(
		String uuid, long groupId) {

		return _journalFeedLocalService.fetchJournalFeedByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _journalFeedLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _journalFeedLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public JournalFeed getFeed(long feedId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFeedLocalService.getFeed(feedId);
	}

	@Override
	public JournalFeed getFeed(long groupId, String feedId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFeedLocalService.getFeed(groupId, feedId);
	}

	@Override
	public java.util.List<JournalFeed> getFeeds() {
		return _journalFeedLocalService.getFeeds();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _journalFeedLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the journal feed with the primary key.
	 *
	 * @param id the primary key of the journal feed
	 * @return the journal feed
	 * @throws PortalException if a journal feed with the primary key could not be found
	 */
	@Override
	public JournalFeed getJournalFeed(long id)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFeedLocalService.getJournalFeed(id);
	}

	/**
	 * Returns the journal feed matching the UUID and group.
	 *
	 * @param uuid the journal feed's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal feed
	 * @throws PortalException if a matching journal feed could not be found
	 */
	@Override
	public JournalFeed getJournalFeedByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFeedLocalService.getJournalFeedByUuidAndGroupId(
			uuid, groupId);
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
	@Override
	public java.util.List<JournalFeed> getJournalFeeds(int start, int end) {
		return _journalFeedLocalService.getJournalFeeds(start, end);
	}

	/**
	 * Returns all the journal feeds matching the UUID and company.
	 *
	 * @param uuid the UUID of the journal feeds
	 * @param companyId the primary key of the company
	 * @return the matching journal feeds, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<JournalFeed> getJournalFeedsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _journalFeedLocalService.getJournalFeedsByUuidAndCompanyId(
			uuid, companyId);
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
	@Override
	public java.util.List<JournalFeed> getJournalFeedsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<JournalFeed>
			orderByComparator) {

		return _journalFeedLocalService.getJournalFeedsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of journal feeds.
	 *
	 * @return the number of journal feeds
	 */
	@Override
	public int getJournalFeedsCount() {
		return _journalFeedLocalService.getJournalFeedsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _journalFeedLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFeedLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<JournalFeed> search(
		long companyId, long groupId, String keywords, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<JournalFeed>
			orderByComparator) {

		return _journalFeedLocalService.search(
			companyId, groupId, keywords, start, end, orderByComparator);
	}

	@Override
	public int searchCount(long companyId, long groupId, String keywords) {
		return _journalFeedLocalService.searchCount(
			companyId, groupId, keywords);
	}

	@Override
	public JournalFeed updateFeed(
			long groupId, String feedId, String name, String description,
			long ddmStructureId, String ddmTemplateKey,
			String ddmRendererTemplateKey, int delta, String orderByCol,
			String orderByType, String targetLayoutFriendlyUrl,
			String targetPortletId, String contentField, String feedFormat,
			double feedVersion,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFeedLocalService.updateFeed(
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
	@Override
	public JournalFeed updateJournalFeed(JournalFeed journalFeed) {
		return _journalFeedLocalService.updateJournalFeed(journalFeed);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _journalFeedLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<JournalFeed> getCTPersistence() {
		return _journalFeedLocalService.getCTPersistence();
	}

	@Override
	public Class<JournalFeed> getModelClass() {
		return _journalFeedLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<JournalFeed>, R, E>
				updateUnsafeFunction)
		throws E {

		return _journalFeedLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public JournalFeedLocalService getWrappedService() {
		return _journalFeedLocalService;
	}

	@Override
	public void setWrappedService(
		JournalFeedLocalService journalFeedLocalService) {

		_journalFeedLocalService = journalFeedLocalService;
	}

	private JournalFeedLocalService _journalFeedLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1110997716