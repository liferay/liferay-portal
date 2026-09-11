/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat740.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link LocalizedEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see LocalizedEntryLocalService
 * @generated
 */
public class LocalizedEntryLocalServiceWrapper
	implements LocalizedEntryLocalService,
			   ServiceWrapper<LocalizedEntryLocalService> {

	public LocalizedEntryLocalServiceWrapper() {
		this(null);
	}

	public LocalizedEntryLocalServiceWrapper(
		LocalizedEntryLocalService localizedEntryLocalService) {

		_localizedEntryLocalService = localizedEntryLocalService;
	}

	/**
	 * Adds the localized entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LocalizedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param localizedEntry the localized entry
	 * @return the localized entry that was added
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.compat740.model.
			LocalizedEntry addLocalizedEntry(
				com.liferay.portal.tools.service.builder.test.compat740.model.
					LocalizedEntry localizedEntry) {

		return _localizedEntryLocalService.addLocalizedEntry(localizedEntry);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.compat740.model.
		LocalizedEntryLocalization addLocalizedEntryLocalization(
				com.liferay.portal.tools.service.builder.test.compat740.model.
					LocalizedEntry localizedEntry,
				String languageId, String title, String content)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _localizedEntryLocalService.addLocalizedEntryLocalization(
			localizedEntry, languageId, title, content);
	}

	/**
	 * Creates a new localized entry with the primary key. Does not add the localized entry to the database.
	 *
	 * @param localizedEntryId the primary key for the new localized entry
	 * @return the new localized entry
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.compat740.model.
			LocalizedEntry createLocalizedEntry(long localizedEntryId) {

		return _localizedEntryLocalService.createLocalizedEntry(
			localizedEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _localizedEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the localized entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LocalizedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param localizedEntry the localized entry
	 * @return the localized entry that was removed
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.compat740.model.
			LocalizedEntry deleteLocalizedEntry(
				com.liferay.portal.tools.service.builder.test.compat740.model.
					LocalizedEntry localizedEntry) {

		return _localizedEntryLocalService.deleteLocalizedEntry(localizedEntry);
	}

	/**
	 * Deletes the localized entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LocalizedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param localizedEntryId the primary key of the localized entry
	 * @return the localized entry that was removed
	 * @throws PortalException if a localized entry with the primary key could not be found
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.compat740.model.
			LocalizedEntry deleteLocalizedEntry(long localizedEntryId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _localizedEntryLocalService.deleteLocalizedEntry(
			localizedEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _localizedEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _localizedEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _localizedEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _localizedEntryLocalService.dynamicQuery();
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

		return _localizedEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.compat740.model.impl.LocalizedEntryModelImpl</code>.
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

		return _localizedEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.compat740.model.impl.LocalizedEntryModelImpl</code>.
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

		return _localizedEntryLocalService.dynamicQuery(
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

		return _localizedEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _localizedEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public
		com.liferay.portal.tools.service.builder.test.compat740.model.
			LocalizedEntry fetchLocalizedEntry(long localizedEntryId) {

		return _localizedEntryLocalService.fetchLocalizedEntry(
			localizedEntryId);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.compat740.model.
		LocalizedEntryLocalization fetchLocalizedEntryLocalization(
			long localizedEntryId, String languageId) {

		return _localizedEntryLocalService.fetchLocalizedEntryLocalization(
			localizedEntryId, languageId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _localizedEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _localizedEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the localized entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.compat740.model.impl.LocalizedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of localized entries
	 * @param end the upper bound of the range of localized entries (not inclusive)
	 * @return the range of localized entries
	 */
	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.compat740.model.
			LocalizedEntry> getLocalizedEntries(int start, int end) {

		return _localizedEntryLocalService.getLocalizedEntries(start, end);
	}

	/**
	 * Returns the number of localized entries.
	 *
	 * @return the number of localized entries
	 */
	@Override
	public int getLocalizedEntriesCount() {
		return _localizedEntryLocalService.getLocalizedEntriesCount();
	}

	/**
	 * Returns the localized entry with the primary key.
	 *
	 * @param localizedEntryId the primary key of the localized entry
	 * @return the localized entry
	 * @throws PortalException if a localized entry with the primary key could not be found
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.compat740.model.
			LocalizedEntry getLocalizedEntry(long localizedEntryId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _localizedEntryLocalService.getLocalizedEntry(localizedEntryId);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.compat740.model.
		LocalizedEntryLocalization getLocalizedEntryLocalization(
				long localizedEntryId, String languageId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _localizedEntryLocalService.getLocalizedEntryLocalization(
			localizedEntryId, languageId);
	}

	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.compat740.model.
			LocalizedEntryLocalization> getLocalizedEntryLocalizations(
				long localizedEntryId) {

		return _localizedEntryLocalService.getLocalizedEntryLocalizations(
			localizedEntryId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _localizedEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _localizedEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the localized entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LocalizedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param localizedEntry the localized entry
	 * @return the localized entry that was updated
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.compat740.model.
			LocalizedEntry updateLocalizedEntry(
				com.liferay.portal.tools.service.builder.test.compat740.model.
					LocalizedEntry localizedEntry) {

		return _localizedEntryLocalService.updateLocalizedEntry(localizedEntry);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.compat740.model.
		LocalizedEntryLocalization updateLocalizedEntryLocalization(
				com.liferay.portal.tools.service.builder.test.compat740.model.
					LocalizedEntry localizedEntry,
				String languageId, String title, String content)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _localizedEntryLocalService.updateLocalizedEntryLocalization(
			localizedEntry, languageId, title, content);
	}

	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.compat740.model.
			LocalizedEntryLocalization> updateLocalizedEntryLocalizations(
					com.liferay.portal.tools.service.builder.test.compat740.
						model.LocalizedEntry localizedEntry,
					java.util.Map<String, String> titleMap,
					java.util.Map<String, String> contentMap)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _localizedEntryLocalService.updateLocalizedEntryLocalizations(
			localizedEntry, titleMap, contentMap);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _localizedEntryLocalService.getBasePersistence();
	}

	@Override
	public LocalizedEntryLocalService getWrappedService() {
		return _localizedEntryLocalService;
	}

	@Override
	public void setWrappedService(
		LocalizedEntryLocalService localizedEntryLocalService) {

		_localizedEntryLocalService = localizedEntryLocalService;
	}

	private LocalizedEntryLocalService _localizedEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-61685433