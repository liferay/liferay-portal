/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service;

import com.liferay.journal.model.JournalContentSearch;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link JournalContentSearchLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see JournalContentSearchLocalService
 * @generated
 */
public class JournalContentSearchLocalServiceWrapper
	implements JournalContentSearchLocalService,
			   ServiceWrapper<JournalContentSearchLocalService> {

	public JournalContentSearchLocalServiceWrapper() {
		this(null);
	}

	public JournalContentSearchLocalServiceWrapper(
		JournalContentSearchLocalService journalContentSearchLocalService) {

		_journalContentSearchLocalService = journalContentSearchLocalService;
	}

	/**
	 * Adds the journal content search to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalContentSearchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalContentSearch the journal content search
	 * @return the journal content search that was added
	 */
	@Override
	public JournalContentSearch addJournalContentSearch(
		JournalContentSearch journalContentSearch) {

		return _journalContentSearchLocalService.addJournalContentSearch(
			journalContentSearch);
	}

	/**
	 * Creates a new journal content search with the primary key. Does not add the journal content search to the database.
	 *
	 * @param contentSearchId the primary key for the new journal content search
	 * @return the new journal content search
	 */
	@Override
	public JournalContentSearch createJournalContentSearch(
		long contentSearchId) {

		return _journalContentSearchLocalService.createJournalContentSearch(
			contentSearchId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalContentSearchLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteArticleContentSearch(
		long groupId, boolean privateLayout, long layoutId, String portletId,
		String articleId) {

		_journalContentSearchLocalService.deleteArticleContentSearch(
			groupId, privateLayout, layoutId, portletId, articleId);
	}

	/**
	 * Deletes the journal content search from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalContentSearchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalContentSearch the journal content search
	 * @return the journal content search that was removed
	 */
	@Override
	public JournalContentSearch deleteJournalContentSearch(
		JournalContentSearch journalContentSearch) {

		return _journalContentSearchLocalService.deleteJournalContentSearch(
			journalContentSearch);
	}

	/**
	 * Deletes the journal content search with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalContentSearchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param contentSearchId the primary key of the journal content search
	 * @return the journal content search that was removed
	 * @throws PortalException if a journal content search with the primary key could not be found
	 */
	@Override
	public JournalContentSearch deleteJournalContentSearch(long contentSearchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalContentSearchLocalService.deleteJournalContentSearch(
			contentSearchId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalContentSearchLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _journalContentSearchLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _journalContentSearchLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _journalContentSearchLocalService.dynamicQuery();
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

		return _journalContentSearchLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalContentSearchModelImpl</code>.
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

		return _journalContentSearchLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalContentSearchModelImpl</code>.
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

		return _journalContentSearchLocalService.dynamicQuery(
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

		return _journalContentSearchLocalService.dynamicQueryCount(
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

		return _journalContentSearchLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public JournalContentSearch fetchJournalContentSearch(
		long contentSearchId) {

		return _journalContentSearchLocalService.fetchJournalContentSearch(
			contentSearchId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _journalContentSearchLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<JournalContentSearch> getArticleContentSearches() {
		return _journalContentSearchLocalService.getArticleContentSearches();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _journalContentSearchLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the journal content search with the primary key.
	 *
	 * @param contentSearchId the primary key of the journal content search
	 * @return the journal content search
	 * @throws PortalException if a journal content search with the primary key could not be found
	 */
	@Override
	public JournalContentSearch getJournalContentSearch(long contentSearchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalContentSearchLocalService.getJournalContentSearch(
			contentSearchId);
	}

	/**
	 * Returns a range of all the journal content searches.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalContentSearchModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of journal content searches
	 * @param end the upper bound of the range of journal content searches (not inclusive)
	 * @return the range of journal content searches
	 */
	@Override
	public java.util.List<JournalContentSearch> getJournalContentSearchs(
		int start, int end) {

		return _journalContentSearchLocalService.getJournalContentSearchs(
			start, end);
	}

	/**
	 * Returns the number of journal content searches.
	 *
	 * @return the number of journal content searches
	 */
	@Override
	public int getJournalContentSearchsCount() {
		return _journalContentSearchLocalService.
			getJournalContentSearchsCount();
	}

	@Override
	public java.util.List<Long> getLayoutIds(
		long groupId, boolean privateLayout, String articleId) {

		return _journalContentSearchLocalService.getLayoutIds(
			groupId, privateLayout, articleId);
	}

	@Override
	public int getLayoutIdsCount(
		long groupId, boolean privateLayout, String articleId) {

		return _journalContentSearchLocalService.getLayoutIdsCount(
			groupId, privateLayout, articleId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _journalContentSearchLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalContentSearchLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the journal content search in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalContentSearchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalContentSearch the journal content search
	 * @return the journal content search that was updated
	 */
	@Override
	public JournalContentSearch updateJournalContentSearch(
		JournalContentSearch journalContentSearch) {

		return _journalContentSearchLocalService.updateJournalContentSearch(
			journalContentSearch);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _journalContentSearchLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<JournalContentSearch> getCTPersistence() {
		return _journalContentSearchLocalService.getCTPersistence();
	}

	@Override
	public Class<JournalContentSearch> getModelClass() {
		return _journalContentSearchLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<JournalContentSearch>, R, E>
				updateUnsafeFunction)
		throws E {

		return _journalContentSearchLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public JournalContentSearchLocalService getWrappedService() {
		return _journalContentSearchLocalService;
	}

	@Override
	public void setWrappedService(
		JournalContentSearchLocalService journalContentSearchLocalService) {

		_journalContentSearchLocalService = journalContentSearchLocalService;
	}

	private JournalContentSearchLocalService _journalContentSearchLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1832070044