/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service;

import com.liferay.journal.model.JournalContentSearch;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for JournalContentSearch. This utility wraps
 * <code>com.liferay.journal.service.impl.JournalContentSearchLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see JournalContentSearchLocalService
 * @generated
 */
public class JournalContentSearchLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.journal.service.impl.JournalContentSearchLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static JournalContentSearch addJournalContentSearch(
		JournalContentSearch journalContentSearch) {

		return getService().addJournalContentSearch(journalContentSearch);
	}

	/**
	 * Creates a new journal content search with the primary key. Does not add the journal content search to the database.
	 *
	 * @param contentSearchId the primary key for the new journal content search
	 * @return the new journal content search
	 */
	public static JournalContentSearch createJournalContentSearch(
		long contentSearchId) {

		return getService().createJournalContentSearch(contentSearchId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteArticleContentSearch(
		long groupId, boolean privateLayout, long layoutId, String portletId,
		String articleId) {

		getService().deleteArticleContentSearch(
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
	public static JournalContentSearch deleteJournalContentSearch(
		JournalContentSearch journalContentSearch) {

		return getService().deleteJournalContentSearch(journalContentSearch);
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
	public static JournalContentSearch deleteJournalContentSearch(
			long contentSearchId)
		throws PortalException {

		return getService().deleteJournalContentSearch(contentSearchId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalContentSearchModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalContentSearchModelImpl</code>.
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

	public static JournalContentSearch fetchJournalContentSearch(
		long contentSearchId) {

		return getService().fetchJournalContentSearch(contentSearchId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<JournalContentSearch> getArticleContentSearches() {
		return getService().getArticleContentSearches();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the journal content search with the primary key.
	 *
	 * @param contentSearchId the primary key of the journal content search
	 * @return the journal content search
	 * @throws PortalException if a journal content search with the primary key could not be found
	 */
	public static JournalContentSearch getJournalContentSearch(
			long contentSearchId)
		throws PortalException {

		return getService().getJournalContentSearch(contentSearchId);
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
	public static List<JournalContentSearch> getJournalContentSearchs(
		int start, int end) {

		return getService().getJournalContentSearchs(start, end);
	}

	/**
	 * Returns the number of journal content searches.
	 *
	 * @return the number of journal content searches
	 */
	public static int getJournalContentSearchsCount() {
		return getService().getJournalContentSearchsCount();
	}

	public static List<Long> getLayoutIds(
		long groupId, boolean privateLayout, String articleId) {

		return getService().getLayoutIds(groupId, privateLayout, articleId);
	}

	public static int getLayoutIdsCount(
		long groupId, boolean privateLayout, String articleId) {

		return getService().getLayoutIdsCount(
			groupId, privateLayout, articleId);
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
	 * Updates the journal content search in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalContentSearchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalContentSearch the journal content search
	 * @return the journal content search that was updated
	 */
	public static JournalContentSearch updateJournalContentSearch(
		JournalContentSearch journalContentSearch) {

		return getService().updateJournalContentSearch(journalContentSearch);
	}

	public static JournalContentSearchLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<JournalContentSearchLocalService>
		_serviceSnapshot = new Snapshot<>(
			JournalContentSearchLocalServiceUtil.class,
			JournalContentSearchLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1287658595