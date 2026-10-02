/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.changeset.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ChangesetEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ChangesetEntryLocalService
 * @generated
 */
public class ChangesetEntryLocalServiceWrapper
	implements ChangesetEntryLocalService,
			   ServiceWrapper<ChangesetEntryLocalService> {

	public ChangesetEntryLocalServiceWrapper() {
		this(null);
	}

	public ChangesetEntryLocalServiceWrapper(
		ChangesetEntryLocalService changesetEntryLocalService) {

		_changesetEntryLocalService = changesetEntryLocalService;
	}

	/**
	 * Adds the changeset entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ChangesetEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param changesetEntry the changeset entry
	 * @return the changeset entry that was added
	 */
	@Override
	public com.liferay.changeset.model.ChangesetEntry addChangesetEntry(
		com.liferay.changeset.model.ChangesetEntry changesetEntry) {

		return _changesetEntryLocalService.addChangesetEntry(changesetEntry);
	}

	@Override
	public com.liferay.changeset.model.ChangesetEntry addChangesetEntry(
			long userId, long changesetCollectionId,
			String classExternalReferenceCode, long classNameId, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _changesetEntryLocalService.addChangesetEntry(
			userId, changesetCollectionId, classExternalReferenceCode,
			classNameId, classPK);
	}

	/**
	 * Creates a new changeset entry with the primary key. Does not add the changeset entry to the database.
	 *
	 * @param changesetEntryId the primary key for the new changeset entry
	 * @return the new changeset entry
	 */
	@Override
	public com.liferay.changeset.model.ChangesetEntry createChangesetEntry(
		long changesetEntryId) {

		return _changesetEntryLocalService.createChangesetEntry(
			changesetEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _changesetEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteChangesetEntries(long changesetCollectionId) {
		_changesetEntryLocalService.deleteChangesetEntries(
			changesetCollectionId);
	}

	@Override
	public void deleteChangesetEntries(java.util.Set<Long> changesetEntryIds) {
		_changesetEntryLocalService.deleteChangesetEntries(changesetEntryIds);
	}

	/**
	 * Deletes the changeset entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ChangesetEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param changesetEntry the changeset entry
	 * @return the changeset entry that was removed
	 */
	@Override
	public com.liferay.changeset.model.ChangesetEntry deleteChangesetEntry(
		com.liferay.changeset.model.ChangesetEntry changesetEntry) {

		return _changesetEntryLocalService.deleteChangesetEntry(changesetEntry);
	}

	/**
	 * Deletes the changeset entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ChangesetEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param changesetEntryId the primary key of the changeset entry
	 * @return the changeset entry that was removed
	 * @throws PortalException if a changeset entry with the primary key could not be found
	 */
	@Override
	public com.liferay.changeset.model.ChangesetEntry deleteChangesetEntry(
			long changesetEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _changesetEntryLocalService.deleteChangesetEntry(
			changesetEntryId);
	}

	@Override
	public void deleteEntry(long changesetId, long classNameId, long classPK) {
		_changesetEntryLocalService.deleteEntry(
			changesetId, classNameId, classPK);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _changesetEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _changesetEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _changesetEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _changesetEntryLocalService.dynamicQuery();
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

		return _changesetEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetEntryModelImpl</code>.
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

		return _changesetEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetEntryModelImpl</code>.
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

		return _changesetEntryLocalService.dynamicQuery(
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

		return _changesetEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _changesetEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.changeset.model.ChangesetEntry fetchChangesetEntry(
		long changesetEntryId) {

		return _changesetEntryLocalService.fetchChangesetEntry(
			changesetEntryId);
	}

	@Override
	public com.liferay.changeset.model.ChangesetEntry fetchChangesetEntry(
		long changesetCollectionId, long classNameId, long classPK) {

		return _changesetEntryLocalService.fetchChangesetEntry(
			changesetCollectionId, classNameId, classPK);
	}

	@Override
	public com.liferay.changeset.model.ChangesetEntry fetchChangesetEntry(
		long changesetCollectionId, String classExternalReferenceCode,
		long classNameId) {

		return _changesetEntryLocalService.fetchChangesetEntry(
			changesetCollectionId, classExternalReferenceCode, classNameId);
	}

	@Override
	public com.liferay.changeset.model.ChangesetEntry fetchOrAddChangesetEntry(
			long changesetCollectionId, long classNameId, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _changesetEntryLocalService.fetchOrAddChangesetEntry(
			changesetCollectionId, classNameId, classPK);
	}

	@Override
	public com.liferay.changeset.model.ChangesetEntry fetchOrAddChangesetEntry(
			long changesetCollectionId, String classExternalReferenceCode,
			long classNameId, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _changesetEntryLocalService.fetchOrAddChangesetEntry(
			changesetCollectionId, classExternalReferenceCode, classNameId,
			classPK);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _changesetEntryLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the changeset entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of changeset entries
	 * @param end the upper bound of the range of changeset entries (not inclusive)
	 * @return the range of changeset entries
	 */
	@Override
	public java.util.List<com.liferay.changeset.model.ChangesetEntry>
		getChangesetEntries(int start, int end) {

		return _changesetEntryLocalService.getChangesetEntries(start, end);
	}

	@Override
	public java.util.List<com.liferay.changeset.model.ChangesetEntry>
		getChangesetEntries(long changesetCollectionId, long classNameId) {

		return _changesetEntryLocalService.getChangesetEntries(
			changesetCollectionId, classNameId);
	}

	/**
	 * Returns the number of changeset entries.
	 *
	 * @return the number of changeset entries
	 */
	@Override
	public int getChangesetEntriesCount() {
		return _changesetEntryLocalService.getChangesetEntriesCount();
	}

	@Override
	public long getChangesetEntriesCount(
		long changesetCollectionId, long classNameId) {

		return _changesetEntryLocalService.getChangesetEntriesCount(
			changesetCollectionId, classNameId);
	}

	@Override
	public long getChangesetEntriesCount(
		long changesetCollectionId, long classNameId,
		java.util.Set<Long> classPKs) {

		return _changesetEntryLocalService.getChangesetEntriesCount(
			changesetCollectionId, classNameId, classPKs);
	}

	/**
	 * Returns the changeset entry with the primary key.
	 *
	 * @param changesetEntryId the primary key of the changeset entry
	 * @return the changeset entry
	 * @throws PortalException if a changeset entry with the primary key could not be found
	 */
	@Override
	public com.liferay.changeset.model.ChangesetEntry getChangesetEntry(
			long changesetEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _changesetEntryLocalService.getChangesetEntry(changesetEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _changesetEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _changesetEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _changesetEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the changeset entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ChangesetEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param changesetEntry the changeset entry
	 * @return the changeset entry that was updated
	 */
	@Override
	public com.liferay.changeset.model.ChangesetEntry updateChangesetEntry(
		com.liferay.changeset.model.ChangesetEntry changesetEntry) {

		return _changesetEntryLocalService.updateChangesetEntry(changesetEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _changesetEntryLocalService.getBasePersistence();
	}

	@Override
	public ChangesetEntryLocalService getWrappedService() {
		return _changesetEntryLocalService;
	}

	@Override
	public void setWrappedService(
		ChangesetEntryLocalService changesetEntryLocalService) {

		_changesetEntryLocalService = changesetEntryLocalService;
	}

	private ChangesetEntryLocalService _changesetEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:563032832