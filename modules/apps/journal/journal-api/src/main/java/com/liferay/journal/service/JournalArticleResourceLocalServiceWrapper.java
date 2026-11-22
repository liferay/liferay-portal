/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service;

import com.liferay.journal.model.JournalArticleResource;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link JournalArticleResourceLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see JournalArticleResourceLocalService
 * @generated
 */
public class JournalArticleResourceLocalServiceWrapper
	implements JournalArticleResourceLocalService,
			   ServiceWrapper<JournalArticleResourceLocalService> {

	public JournalArticleResourceLocalServiceWrapper() {
		this(null);
	}

	public JournalArticleResourceLocalServiceWrapper(
		JournalArticleResourceLocalService journalArticleResourceLocalService) {

		_journalArticleResourceLocalService =
			journalArticleResourceLocalService;
	}

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
	@Override
	public JournalArticleResource addJournalArticleResource(
		JournalArticleResource journalArticleResource) {

		return _journalArticleResourceLocalService.addJournalArticleResource(
			journalArticleResource);
	}

	/**
	 * Creates a new journal article resource with the primary key. Does not add the journal article resource to the database.
	 *
	 * @param resourcePrimKey the primary key for the new journal article resource
	 * @return the new journal article resource
	 */
	@Override
	public JournalArticleResource createJournalArticleResource(
		long resourcePrimKey) {

		return _journalArticleResourceLocalService.createJournalArticleResource(
			resourcePrimKey);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleResourceLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public JournalArticleResource deleteJournalArticleResource(
		JournalArticleResource journalArticleResource) {

		return _journalArticleResourceLocalService.deleteJournalArticleResource(
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
	@Override
	public JournalArticleResource deleteJournalArticleResource(
			long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleResourceLocalService.deleteJournalArticleResource(
			resourcePrimKey);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleResourceLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _journalArticleResourceLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _journalArticleResourceLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _journalArticleResourceLocalService.dynamicQuery();
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

		return _journalArticleResourceLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _journalArticleResourceLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _journalArticleResourceLocalService.dynamicQuery(
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

		return _journalArticleResourceLocalService.dynamicQueryCount(
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

		return _journalArticleResourceLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public JournalArticleResource fetchArticleResource(
		long groupId, String articleId) {

		return _journalArticleResourceLocalService.fetchArticleResource(
			groupId, articleId);
	}

	@Override
	public JournalArticleResource fetchArticleResource(
		String uuid, long groupId) {

		return _journalArticleResourceLocalService.fetchArticleResource(
			uuid, groupId);
	}

	@Override
	public JournalArticleResource fetchJournalArticleResource(
		long resourcePrimKey) {

		return _journalArticleResourceLocalService.fetchJournalArticleResource(
			resourcePrimKey);
	}

	/**
	 * Returns the journal article resource matching the UUID and group.
	 *
	 * @param uuid the journal article resource's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal article resource, or <code>null</code> if a matching journal article resource could not be found
	 */
	@Override
	public JournalArticleResource fetchJournalArticleResourceByUuidAndGroupId(
		String uuid, long groupId) {

		return _journalArticleResourceLocalService.
			fetchJournalArticleResourceByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _journalArticleResourceLocalService.getActionableDynamicQuery();
	}

	@Override
	public JournalArticleResource getArticleResource(
			long articleResourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleResourceLocalService.getArticleResource(
			articleResourcePrimKey);
	}

	@Override
	public long getArticleResourcePrimKey(long groupId, String articleId) {
		return _journalArticleResourceLocalService.getArticleResourcePrimKey(
			groupId, articleId);
	}

	@Override
	public long getArticleResourcePrimKey(
		String uuid, long groupId, String articleId) {

		return _journalArticleResourceLocalService.getArticleResourcePrimKey(
			uuid, groupId, articleId);
	}

	@Override
	public java.util.List<JournalArticleResource> getArticleResources(
		long groupId) {

		return _journalArticleResourceLocalService.getArticleResources(groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _journalArticleResourceLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the journal article resource with the primary key.
	 *
	 * @param resourcePrimKey the primary key of the journal article resource
	 * @return the journal article resource
	 * @throws PortalException if a journal article resource with the primary key could not be found
	 */
	@Override
	public JournalArticleResource getJournalArticleResource(
			long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleResourceLocalService.getJournalArticleResource(
			resourcePrimKey);
	}

	/**
	 * Returns the journal article resource matching the UUID and group.
	 *
	 * @param uuid the journal article resource's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal article resource
	 * @throws PortalException if a matching journal article resource could not be found
	 */
	@Override
	public JournalArticleResource getJournalArticleResourceByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleResourceLocalService.
			getJournalArticleResourceByUuidAndGroupId(uuid, groupId);
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
	@Override
	public java.util.List<JournalArticleResource> getJournalArticleResources(
		int start, int end) {

		return _journalArticleResourceLocalService.getJournalArticleResources(
			start, end);
	}

	/**
	 * Returns all the journal article resources matching the UUID and company.
	 *
	 * @param uuid the UUID of the journal article resources
	 * @param companyId the primary key of the company
	 * @return the matching journal article resources, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<JournalArticleResource>
		getJournalArticleResourcesByUuidAndCompanyId(
			String uuid, long companyId) {

		return _journalArticleResourceLocalService.
			getJournalArticleResourcesByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<JournalArticleResource>
		getJournalArticleResourcesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<JournalArticleResource> orderByComparator) {

		return _journalArticleResourceLocalService.
			getJournalArticleResourcesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of journal article resources.
	 *
	 * @return the number of journal article resources
	 */
	@Override
	public int getJournalArticleResourcesCount() {
		return _journalArticleResourceLocalService.
			getJournalArticleResourcesCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _journalArticleResourceLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleResourceLocalService.getPersistedModel(
			primaryKeyObj);
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
	@Override
	public JournalArticleResource updateJournalArticleResource(
		JournalArticleResource journalArticleResource) {

		return _journalArticleResourceLocalService.updateJournalArticleResource(
			journalArticleResource);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _journalArticleResourceLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<JournalArticleResource> getCTPersistence() {
		return _journalArticleResourceLocalService.getCTPersistence();
	}

	@Override
	public Class<JournalArticleResource> getModelClass() {
		return _journalArticleResourceLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<JournalArticleResource>, R, E>
				updateUnsafeFunction)
		throws E {

		return _journalArticleResourceLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public JournalArticleResourceLocalService getWrappedService() {
		return _journalArticleResourceLocalService;
	}

	@Override
	public void setWrappedService(
		JournalArticleResourceLocalService journalArticleResourceLocalService) {

		_journalArticleResourceLocalService =
			journalArticleResourceLocalService;
	}

	private JournalArticleResourceLocalService
		_journalArticleResourceLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1716045863