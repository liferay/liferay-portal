/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.qualifier.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceQualifierEntryLocalService}.
 *
 * @author Riccardo Alberti
 * @see CommerceQualifierEntryLocalService
 * @generated
 */
public class CommerceQualifierEntryLocalServiceWrapper
	implements CommerceQualifierEntryLocalService,
			   ServiceWrapper<CommerceQualifierEntryLocalService> {

	public CommerceQualifierEntryLocalServiceWrapper() {
		this(null);
	}

	public CommerceQualifierEntryLocalServiceWrapper(
		CommerceQualifierEntryLocalService commerceQualifierEntryLocalService) {

		_commerceQualifierEntryLocalService =
			commerceQualifierEntryLocalService;
	}

	/**
	 * Adds the commerce qualifier entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceQualifierEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceQualifierEntry the commerce qualifier entry
	 * @return the commerce qualifier entry that was added
	 */
	@Override
	public com.liferay.commerce.qualifier.model.CommerceQualifierEntry
		addCommerceQualifierEntry(
			com.liferay.commerce.qualifier.model.CommerceQualifierEntry
				commerceQualifierEntry) {

		return _commerceQualifierEntryLocalService.addCommerceQualifierEntry(
			commerceQualifierEntry);
	}

	@Override
	public com.liferay.commerce.qualifier.model.CommerceQualifierEntry
			addCommerceQualifierEntry(
				long userId, String sourceClassName, long sourceClassPK,
				String sourceCommerceQualifierMetadataKey,
				String targetClassName, long targetClassPK,
				String targetCommerceQualifierMetadataKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceQualifierEntryLocalService.addCommerceQualifierEntry(
			userId, sourceClassName, sourceClassPK,
			sourceCommerceQualifierMetadataKey, targetClassName, targetClassPK,
			targetCommerceQualifierMetadataKey);
	}

	/**
	 * Creates a new commerce qualifier entry with the primary key. Does not add the commerce qualifier entry to the database.
	 *
	 * @param commerceQualifierEntryId the primary key for the new commerce qualifier entry
	 * @return the new commerce qualifier entry
	 */
	@Override
	public com.liferay.commerce.qualifier.model.CommerceQualifierEntry
		createCommerceQualifierEntry(long commerceQualifierEntryId) {

		return _commerceQualifierEntryLocalService.createCommerceQualifierEntry(
			commerceQualifierEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceQualifierEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce qualifier entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceQualifierEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceQualifierEntry the commerce qualifier entry
	 * @return the commerce qualifier entry that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.commerce.qualifier.model.CommerceQualifierEntry
			deleteCommerceQualifierEntry(
				com.liferay.commerce.qualifier.model.CommerceQualifierEntry
					commerceQualifierEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceQualifierEntryLocalService.deleteCommerceQualifierEntry(
			commerceQualifierEntry);
	}

	/**
	 * Deletes the commerce qualifier entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceQualifierEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceQualifierEntryId the primary key of the commerce qualifier entry
	 * @return the commerce qualifier entry that was removed
	 * @throws PortalException if a commerce qualifier entry with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.qualifier.model.CommerceQualifierEntry
			deleteCommerceQualifierEntry(long commerceQualifierEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceQualifierEntryLocalService.deleteCommerceQualifierEntry(
			commerceQualifierEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceQualifierEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public void deleteSourceCommerceQualifierEntries(
			String sourceClassName, long sourceClassPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceQualifierEntryLocalService.
			deleteSourceCommerceQualifierEntries(
				sourceClassName, sourceClassPK);
	}

	@Override
	public void deleteTargetCommerceQualifierEntries(
			String targetClassName, long targetClassPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceQualifierEntryLocalService.
			deleteTargetCommerceQualifierEntries(
				targetClassName, targetClassPK);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceQualifierEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceQualifierEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceQualifierEntryLocalService.dynamicQuery();
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

		return _commerceQualifierEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryModelImpl</code>.
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

		return _commerceQualifierEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryModelImpl</code>.
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

		return _commerceQualifierEntryLocalService.dynamicQuery(
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

		return _commerceQualifierEntryLocalService.dynamicQueryCount(
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

		return _commerceQualifierEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.qualifier.model.CommerceQualifierEntry
		fetchCommerceQualifierEntry(long commerceQualifierEntryId) {

		return _commerceQualifierEntryLocalService.fetchCommerceQualifierEntry(
			commerceQualifierEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceQualifierEntryLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the commerce qualifier entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce qualifier entries
	 * @param end the upper bound of the range of commerce qualifier entries (not inclusive)
	 * @return the range of commerce qualifier entries
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.qualifier.model.CommerceQualifierEntry>
			getCommerceQualifierEntries(int start, int end) {

		return _commerceQualifierEntryLocalService.getCommerceQualifierEntries(
			start, end);
	}

	/**
	 * Returns the number of commerce qualifier entries.
	 *
	 * @return the number of commerce qualifier entries
	 */
	@Override
	public int getCommerceQualifierEntriesCount() {
		return _commerceQualifierEntryLocalService.
			getCommerceQualifierEntriesCount();
	}

	/**
	 * Returns the commerce qualifier entry with the primary key.
	 *
	 * @param commerceQualifierEntryId the primary key of the commerce qualifier entry
	 * @return the commerce qualifier entry
	 * @throws PortalException if a commerce qualifier entry with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.qualifier.model.CommerceQualifierEntry
			getCommerceQualifierEntry(long commerceQualifierEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceQualifierEntryLocalService.getCommerceQualifierEntry(
			commerceQualifierEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceQualifierEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceQualifierEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceQualifierEntryLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.qualifier.model.CommerceQualifierEntry>
				getSourceCommerceQualifierEntries(
					long companyId, String sourceClassName, long sourceClassPK,
					String targetCommerceQualifierMetadataKey, String keywords,
					int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceQualifierEntryLocalService.
			getSourceCommerceQualifierEntries(
				companyId, sourceClassName, sourceClassPK,
				targetCommerceQualifierMetadataKey, keywords, start, end);
	}

	@Override
	public int getSourceCommerceQualifierEntriesCount(
			long companyId, String sourceClassName, long sourceClassPK,
			String targetCommerceQualifierMetadataKey, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceQualifierEntryLocalService.
			getSourceCommerceQualifierEntriesCount(
				companyId, sourceClassName, sourceClassPK,
				targetCommerceQualifierMetadataKey, keywords);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.qualifier.model.CommerceQualifierEntry>
				getTargetCommerceQualifierEntries(
					long companyId, String sourceCommerceQualifierMetadataKey,
					String targetClassName, long targetClassPK, String keywords,
					int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceQualifierEntryLocalService.
			getTargetCommerceQualifierEntries(
				companyId, sourceCommerceQualifierMetadataKey, targetClassName,
				targetClassPK, keywords, start, end);
	}

	@Override
	public int getTargetCommerceQualifierEntriesCount(
			long companyId, String sourceCommerceQualifierMetadataKey,
			String targetClassName, long targetClassPK, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceQualifierEntryLocalService.
			getTargetCommerceQualifierEntriesCount(
				companyId, sourceCommerceQualifierMetadataKey, targetClassName,
				targetClassPK, keywords);
	}

	/**
	 * Updates the commerce qualifier entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceQualifierEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceQualifierEntry the commerce qualifier entry
	 * @return the commerce qualifier entry that was updated
	 */
	@Override
	public com.liferay.commerce.qualifier.model.CommerceQualifierEntry
		updateCommerceQualifierEntry(
			com.liferay.commerce.qualifier.model.CommerceQualifierEntry
				commerceQualifierEntry) {

		return _commerceQualifierEntryLocalService.updateCommerceQualifierEntry(
			commerceQualifierEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceQualifierEntryLocalService.getBasePersistence();
	}

	@Override
	public CommerceQualifierEntryLocalService getWrappedService() {
		return _commerceQualifierEntryLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceQualifierEntryLocalService commerceQualifierEntryLocalService) {

		_commerceQualifierEntryLocalService =
			commerceQualifierEntryLocalService;
	}

	private CommerceQualifierEntryLocalService
		_commerceQualifierEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2075529414