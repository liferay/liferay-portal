/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.service;

import com.liferay.adaptive.media.image.model.AMImageEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link AMImageEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AMImageEntryLocalService
 * @generated
 */
public class AMImageEntryLocalServiceWrapper
	implements AMImageEntryLocalService,
			   ServiceWrapper<AMImageEntryLocalService> {

	public AMImageEntryLocalServiceWrapper() {
		this(null);
	}

	public AMImageEntryLocalServiceWrapper(
		AMImageEntryLocalService amImageEntryLocalService) {

		_amImageEntryLocalService = amImageEntryLocalService;
	}

	/**
	 * Adds an adaptive media image entry in the database and stores the image
	 * bytes in the file store.
	 *
	 * @param amImageConfigurationEntry the configuration used to create the
	 adaptive media image
	 * @param fileVersion the file version used to create the adaptive media
	 image
	 * @param height the adaptive media image's height
	 * @param width the adaptive media image's width
	 * @param inputStream the adaptive media image's input stream to store in
	 the file store
	 * @param size the adaptive media image's size
	 * @return the adaptive media image
	 * @throws PortalException if an adaptive media image already exists for the
	 file version and configuration
	 */
	@Override
	public AMImageEntry addAMImageEntry(
			com.liferay.adaptive.media.image.configuration.
				AMImageConfigurationEntry amImageConfigurationEntry,
			com.liferay.portal.kernel.repository.model.FileVersion fileVersion,
			int height, int width, java.io.InputStream inputStream, long size)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _amImageEntryLocalService.addAMImageEntry(
			amImageConfigurationEntry, fileVersion, height, width, inputStream,
			size);
	}

	/**
	 * Adds the am image entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AMImageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param amImageEntry the am image entry
	 * @return the am image entry that was added
	 */
	@Override
	public AMImageEntry addAMImageEntry(AMImageEntry amImageEntry) {
		return _amImageEntryLocalService.addAMImageEntry(amImageEntry);
	}

	/**
	 * Creates a new am image entry with the primary key. Does not add the am image entry to the database.
	 *
	 * @param amImageEntryId the primary key for the new am image entry
	 * @return the new am image entry
	 */
	@Override
	public AMImageEntry createAMImageEntry(long amImageEntryId) {
		return _amImageEntryLocalService.createAMImageEntry(amImageEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _amImageEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes all the adaptive media images generated for the configuration in
	 * the company. This method deletes both the adaptive media image entry from
	 * the database and the bytes from the file store.
	 *
	 * @param companyId the primary key of the company
	 * @param amImageConfigurationEntry the configuration used to create the
	 adaptive media image
	 */
	@Override
	public void deleteAMImageEntries(
		long companyId,
		com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry
			amImageConfigurationEntry) {

		_amImageEntryLocalService.deleteAMImageEntries(
			companyId, amImageConfigurationEntry);
	}

	/**
	 * Deletes the am image entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AMImageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param amImageEntry the am image entry
	 * @return the am image entry that was removed
	 */
	@Override
	public AMImageEntry deleteAMImageEntry(AMImageEntry amImageEntry) {
		return _amImageEntryLocalService.deleteAMImageEntry(amImageEntry);
	}

	/**
	 * Deletes the am image entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AMImageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param amImageEntryId the primary key of the am image entry
	 * @return the am image entry that was removed
	 * @throws PortalException if a am image entry with the primary key could not be found
	 */
	@Override
	public AMImageEntry deleteAMImageEntry(long amImageEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _amImageEntryLocalService.deleteAMImageEntry(amImageEntryId);
	}

	/**
	 * Deletes all the adaptive media images generated for a file version. This
	 * method deletes both the adaptive media image entry from the database and
	 * the bytes from the file store.
	 *
	 * @param fileVersion the file version
	 * @throws PortalException if the file version was not found
	 */
	@Override
	public void deleteAMImageEntryFileVersion(
			com.liferay.portal.kernel.repository.model.FileVersion fileVersion)
		throws com.liferay.portal.kernel.exception.PortalException {

		_amImageEntryLocalService.deleteAMImageEntryFileVersion(fileVersion);
	}

	/**
	 * Deletes adaptive media images generated for a file version under a given
	 * configuration. This method deletes both the adaptive media image entry
	 * from the database and the bytes from the file store.
	 *
	 * @param configurationUuid the configuration UUID
	 * @param fileVersionId the primary key of the file version
	 * @throws PortalException if the file version was not found
	 */
	@Override
	public void deleteAMImageEntryFileVersion(
			String configurationUuid, long fileVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_amImageEntryLocalService.deleteAMImageEntryFileVersion(
			configurationUuid, fileVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _amImageEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _amImageEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _amImageEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _amImageEntryLocalService.dynamicQuery();
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

		return _amImageEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
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

		return _amImageEntryLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
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

		return _amImageEntryLocalService.dynamicQuery(
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

		return _amImageEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _amImageEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public AMImageEntry fetchAMImageEntry(long amImageEntryId) {
		return _amImageEntryLocalService.fetchAMImageEntry(amImageEntryId);
	}

	/**
	 * Returns the adaptive media image entry generated for the configuration
	 * and file version.
	 *
	 * @param configurationUuid the UUID of the configuration used to create
	 the adaptive media image
	 * @param fileVersionId the primary key of the file version
	 * @return the matching adaptive media image entry, or <code>null</code> if
	 a matching adaptive media image entry could not be found
	 */
	@Override
	public AMImageEntry fetchAMImageEntry(
		String configurationUuid, long fileVersionId) {

		return _amImageEntryLocalService.fetchAMImageEntry(
			configurationUuid, fileVersionId);
	}

	/**
	 * Returns the am image entry matching the UUID and group.
	 *
	 * @param uuid the am image entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching am image entry, or <code>null</code> if a matching am image entry could not be found
	 */
	@Override
	public AMImageEntry fetchAMImageEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return _amImageEntryLocalService.fetchAMImageEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _amImageEntryLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the am image entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @return the range of am image entries
	 */
	@Override
	public java.util.List<AMImageEntry> getAMImageEntries(int start, int end) {
		return _amImageEntryLocalService.getAMImageEntries(start, end);
	}

	/**
	 * Returns the list of adaptive media image entries generated for the
	 * file version.
	 *
	 * @param fileVersionId the primary key of the file version
	 * @return the list of adaptive media image entries in the file version
	 */
	@Override
	public java.util.List<AMImageEntry> getAMImageEntries(long fileVersionId) {
		return _amImageEntryLocalService.getAMImageEntries(fileVersionId);
	}

	/**
	 * Returns all the am image entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the am image entries
	 * @param companyId the primary key of the company
	 * @return the matching am image entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<AMImageEntry> getAMImageEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return _amImageEntryLocalService.getAMImageEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of am image entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the am image entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching am image entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<AMImageEntry> getAMImageEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AMImageEntry>
			orderByComparator) {

		return _amImageEntryLocalService.getAMImageEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of am image entries.
	 *
	 * @return the number of am image entries
	 */
	@Override
	public int getAMImageEntriesCount() {
		return _amImageEntryLocalService.getAMImageEntriesCount();
	}

	/**
	 * Returns the number of adaptive media image entries generated for the
	 * configuration in the company.
	 *
	 * @param companyId the primary key of the company
	 * @param configurationUuid the UUID of the configuration used to create
	 the adaptive media image
	 * @return the number of adaptive media image entries in the company for the
	 configuration
	 */
	@Override
	public int getAMImageEntriesCount(
		long companyId, String configurationUuid) {

		return _amImageEntryLocalService.getAMImageEntriesCount(
			companyId, configurationUuid);
	}

	/**
	 * Returns the am image entry with the primary key.
	 *
	 * @param amImageEntryId the primary key of the am image entry
	 * @return the am image entry
	 * @throws PortalException if a am image entry with the primary key could not be found
	 */
	@Override
	public AMImageEntry getAMImageEntry(long amImageEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _amImageEntryLocalService.getAMImageEntry(amImageEntryId);
	}

	/**
	 * Returns the am image entry matching the UUID and group.
	 *
	 * @param uuid the am image entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching am image entry
	 * @throws PortalException if a matching am image entry could not be found
	 */
	@Override
	public AMImageEntry getAMImageEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _amImageEntryLocalService.getAMImageEntryByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns the input stream of the adaptive media image generated for a file
	 * version and configuration.
	 *
	 * @param amImageConfigurationEntry the configuration used to create the
	 adaptive media image
	 * @param fileVersion the file version used to create the adaptive media
	 image
	 * @return the input stream of the adaptive media image generated for a file
	 version and configuration
	 */
	@Override
	public java.io.InputStream getAMImageEntryContentStream(
		com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry
			amImageConfigurationEntry,
		com.liferay.portal.kernel.repository.model.FileVersion fileVersion) {

		return _amImageEntryLocalService.getAMImageEntryContentStream(
			amImageConfigurationEntry, fileVersion);
	}

	/**
	 * Returns the total number of adaptive media images that are expected to be
	 * in a company once they are generated. The number of adaptive media images
	 * could be less if there are images that haven't generated the adaptive
	 * media image yet.
	 *
	 * @param companyId the primary key of the company
	 * @return the number of expected adaptive media images for a company
	 */
	@Override
	public int getExpectedAMImageEntriesCount(long companyId) {
		return _amImageEntryLocalService.getExpectedAMImageEntriesCount(
			companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _amImageEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _amImageEntryLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public int getPercentage(
		long companyId, String configurationUuid,
		int expectedAMImageEntriesCount) {

		return _amImageEntryLocalService.getPercentage(
			companyId, configurationUuid, expectedAMImageEntriesCount);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _amImageEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public boolean hasAMImageEntryContent(
		String configurationUuid,
		com.liferay.portal.kernel.repository.model.FileVersion fileVersion) {

		return _amImageEntryLocalService.hasAMImageEntryContent(
			configurationUuid, fileVersion);
	}

	/**
	 * Updates the am image entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AMImageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param amImageEntry the am image entry
	 * @return the am image entry that was updated
	 */
	@Override
	public AMImageEntry updateAMImageEntry(AMImageEntry amImageEntry) {
		return _amImageEntryLocalService.updateAMImageEntry(amImageEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _amImageEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<AMImageEntry> getCTPersistence() {
		return _amImageEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<AMImageEntry> getModelClass() {
		return _amImageEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AMImageEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _amImageEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public AMImageEntryLocalService getWrappedService() {
		return _amImageEntryLocalService;
	}

	@Override
	public void setWrappedService(
		AMImageEntryLocalService amImageEntryLocalService) {

		_amImageEntryLocalService = amImageEntryLocalService;
	}

	private AMImageEntryLocalService _amImageEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1966264911