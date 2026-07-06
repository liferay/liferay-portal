/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link AudiencesEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AudiencesEntryLocalService
 * @generated
 */
public class AudiencesEntryLocalServiceWrapper
	implements AudiencesEntryLocalService,
			   ServiceWrapper<AudiencesEntryLocalService> {

	public AudiencesEntryLocalServiceWrapper() {
		this(null);
	}

	public AudiencesEntryLocalServiceWrapper(
		AudiencesEntryLocalService audiencesEntryLocalService) {

		_audiencesEntryLocalService = audiencesEntryLocalService;
	}

	/**
	 * Adds the audiences entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AudiencesEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param audiencesEntry the audiences entry
	 * @return the audiences entry that was added
	 */
	@Override
	public com.liferay.audiences.model.AudiencesEntry addAudiencesEntry(
		com.liferay.audiences.model.AudiencesEntry audiencesEntry) {

		return _audiencesEntryLocalService.addAudiencesEntry(audiencesEntry);
	}

	@Override
	public com.liferay.audiences.model.AudiencesEntry addAudiencesEntry(
			String externalReferenceCode, String json, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryLocalService.addAudiencesEntry(
			externalReferenceCode, json, name, serviceContext);
	}

	/**
	 * Creates a new audiences entry with the primary key. Does not add the audiences entry to the database.
	 *
	 * @param audiencesEntryId the primary key for the new audiences entry
	 * @return the new audiences entry
	 */
	@Override
	public com.liferay.audiences.model.AudiencesEntry createAudiencesEntry(
		long audiencesEntryId) {

		return _audiencesEntryLocalService.createAudiencesEntry(
			audiencesEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the audiences entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AudiencesEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param audiencesEntry the audiences entry
	 * @return the audiences entry that was removed
	 */
	@Override
	public com.liferay.audiences.model.AudiencesEntry deleteAudiencesEntry(
		com.liferay.audiences.model.AudiencesEntry audiencesEntry) {

		return _audiencesEntryLocalService.deleteAudiencesEntry(audiencesEntry);
	}

	/**
	 * Deletes the audiences entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AudiencesEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param audiencesEntryId the primary key of the audiences entry
	 * @return the audiences entry that was removed
	 * @throws PortalException if a audiences entry with the primary key could not be found
	 */
	@Override
	public com.liferay.audiences.model.AudiencesEntry deleteAudiencesEntry(
			long audiencesEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryLocalService.deleteAudiencesEntry(
			audiencesEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _audiencesEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _audiencesEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _audiencesEntryLocalService.dynamicQuery();
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

		return _audiencesEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryModelImpl</code>.
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

		return _audiencesEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryModelImpl</code>.
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

		return _audiencesEntryLocalService.dynamicQuery(
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

		return _audiencesEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _audiencesEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.audiences.model.AudiencesEntry fetchAudiencesEntry(
		long audiencesEntryId) {

		return _audiencesEntryLocalService.fetchAudiencesEntry(
			audiencesEntryId);
	}

	@Override
	public com.liferay.audiences.model.AudiencesEntry
		fetchAudiencesEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _audiencesEntryLocalService.
			fetchAudiencesEntryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _audiencesEntryLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the audiences entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of audiences entries
	 * @param end the upper bound of the range of audiences entries (not inclusive)
	 * @return the range of audiences entries
	 */
	@Override
	public java.util.List<com.liferay.audiences.model.AudiencesEntry>
		getAudiencesEntries(int start, int end) {

		return _audiencesEntryLocalService.getAudiencesEntries(start, end);
	}

	@Override
	public java.util.List<com.liferay.audiences.model.AudiencesEntry>
		getAudiencesEntries(
			long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.audiences.model.AudiencesEntry>
					orderByComparator) {

		return _audiencesEntryLocalService.getAudiencesEntries(
			companyId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.audiences.model.AudiencesEntry>
			getAudiencesEntries(
				long companyId, String name, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.audiences.model.AudiencesEntry>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryLocalService.getAudiencesEntries(
			companyId, name, start, end, orderByComparator);
	}

	/**
	 * Returns the number of audiences entries.
	 *
	 * @return the number of audiences entries
	 */
	@Override
	public int getAudiencesEntriesCount() {
		return _audiencesEntryLocalService.getAudiencesEntriesCount();
	}

	@Override
	public int getAudiencesEntriesCount(long companyId) {
		return _audiencesEntryLocalService.getAudiencesEntriesCount(companyId);
	}

	@Override
	public int getAudiencesEntriesCount(long companyId, String name) {
		return _audiencesEntryLocalService.getAudiencesEntriesCount(
			companyId, name);
	}

	/**
	 * Returns the audiences entry with the primary key.
	 *
	 * @param audiencesEntryId the primary key of the audiences entry
	 * @return the audiences entry
	 * @throws PortalException if a audiences entry with the primary key could not be found
	 */
	@Override
	public com.liferay.audiences.model.AudiencesEntry getAudiencesEntry(
			long audiencesEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryLocalService.getAudiencesEntry(audiencesEntryId);
	}

	@Override
	public com.liferay.audiences.model.AudiencesEntry
			getAudiencesEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryLocalService.
			getAudiencesEntryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _audiencesEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _audiencesEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the audiences entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AudiencesEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param audiencesEntry the audiences entry
	 * @return the audiences entry that was updated
	 */
	@Override
	public com.liferay.audiences.model.AudiencesEntry updateAudiencesEntry(
		com.liferay.audiences.model.AudiencesEntry audiencesEntry) {

		return _audiencesEntryLocalService.updateAudiencesEntry(audiencesEntry);
	}

	@Override
	public com.liferay.audiences.model.AudiencesEntry updateAudiencesEntry(
			long audiencesEntryId, String externalReferenceCode, String json,
			String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryLocalService.updateAudiencesEntry(
			audiencesEntryId, externalReferenceCode, json, name);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _audiencesEntryLocalService.getBasePersistence();
	}

	@Override
	public AudiencesEntryLocalService getWrappedService() {
		return _audiencesEntryLocalService;
	}

	@Override
	public void setWrappedService(
		AudiencesEntryLocalService audiencesEntryLocalService) {

		_audiencesEntryLocalService = audiencesEntryLocalService;
	}

	private AudiencesEntryLocalService _audiencesEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-593247208