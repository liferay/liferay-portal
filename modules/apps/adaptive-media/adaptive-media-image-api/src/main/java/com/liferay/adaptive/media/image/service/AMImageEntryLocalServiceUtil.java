/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.service;

import com.liferay.adaptive.media.image.model.AMImageEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for AMImageEntry. This utility wraps
 * <code>com.liferay.adaptive.media.image.service.impl.AMImageEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AMImageEntryLocalService
 * @generated
 */
public class AMImageEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.adaptive.media.image.service.impl.AMImageEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static AMImageEntry addAMImageEntry(
			com.liferay.adaptive.media.image.configuration.
				AMImageConfigurationEntry amImageConfigurationEntry,
			com.liferay.portal.kernel.repository.model.FileVersion fileVersion,
			int height, int width, InputStream inputStream, long size)
		throws PortalException {

		return getService().addAMImageEntry(
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
	public static AMImageEntry addAMImageEntry(AMImageEntry amImageEntry) {
		return getService().addAMImageEntry(amImageEntry);
	}

	/**
	 * Creates a new am image entry with the primary key. Does not add the am image entry to the database.
	 *
	 * @param amImageEntryId the primary key for the new am image entry
	 * @return the new am image entry
	 */
	public static AMImageEntry createAMImageEntry(long amImageEntryId) {
		return getService().createAMImageEntry(amImageEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
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
	public static void deleteAMImageEntries(
		long companyId,
		com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry
			amImageConfigurationEntry) {

		getService().deleteAMImageEntries(companyId, amImageConfigurationEntry);
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
	public static AMImageEntry deleteAMImageEntry(AMImageEntry amImageEntry) {
		return getService().deleteAMImageEntry(amImageEntry);
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
	public static AMImageEntry deleteAMImageEntry(long amImageEntryId)
		throws PortalException {

		return getService().deleteAMImageEntry(amImageEntryId);
	}

	/**
	 * Deletes all the adaptive media images generated for a file version. This
	 * method deletes both the adaptive media image entry from the database and
	 * the bytes from the file store.
	 *
	 * @param fileVersion the file version
	 * @throws PortalException if the file version was not found
	 */
	public static void deleteAMImageEntryFileVersion(
			com.liferay.portal.kernel.repository.model.FileVersion fileVersion)
		throws PortalException {

		getService().deleteAMImageEntryFileVersion(fileVersion);
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
	public static void deleteAMImageEntryFileVersion(
			String configurationUuid, long fileVersionId)
		throws PortalException {

		getService().deleteAMImageEntryFileVersion(
			configurationUuid, fileVersionId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
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

	public static AMImageEntry fetchAMImageEntry(long amImageEntryId) {
		return getService().fetchAMImageEntry(amImageEntryId);
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
	public static AMImageEntry fetchAMImageEntry(
		String configurationUuid, long fileVersionId) {

		return getService().fetchAMImageEntry(configurationUuid, fileVersionId);
	}

	/**
	 * Returns the am image entry matching the UUID and group.
	 *
	 * @param uuid the am image entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching am image entry, or <code>null</code> if a matching am image entry could not be found
	 */
	public static AMImageEntry fetchAMImageEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchAMImageEntryByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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
	public static List<AMImageEntry> getAMImageEntries(int start, int end) {
		return getService().getAMImageEntries(start, end);
	}

	/**
	 * Returns the list of adaptive media image entries generated for the
	 * file version.
	 *
	 * @param fileVersionId the primary key of the file version
	 * @return the list of adaptive media image entries in the file version
	 */
	public static List<AMImageEntry> getAMImageEntries(long fileVersionId) {
		return getService().getAMImageEntries(fileVersionId);
	}

	/**
	 * Returns all the am image entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the am image entries
	 * @param companyId the primary key of the company
	 * @return the matching am image entries, or an empty list if no matches were found
	 */
	public static List<AMImageEntry> getAMImageEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getAMImageEntriesByUuidAndCompanyId(
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
	public static List<AMImageEntry> getAMImageEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AMImageEntry> orderByComparator) {

		return getService().getAMImageEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of am image entries.
	 *
	 * @return the number of am image entries
	 */
	public static int getAMImageEntriesCount() {
		return getService().getAMImageEntriesCount();
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
	public static int getAMImageEntriesCount(
		long companyId, String configurationUuid) {

		return getService().getAMImageEntriesCount(
			companyId, configurationUuid);
	}

	/**
	 * Returns the am image entry with the primary key.
	 *
	 * @param amImageEntryId the primary key of the am image entry
	 * @return the am image entry
	 * @throws PortalException if a am image entry with the primary key could not be found
	 */
	public static AMImageEntry getAMImageEntry(long amImageEntryId)
		throws PortalException {

		return getService().getAMImageEntry(amImageEntryId);
	}

	/**
	 * Returns the am image entry matching the UUID and group.
	 *
	 * @param uuid the am image entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching am image entry
	 * @throws PortalException if a matching am image entry could not be found
	 */
	public static AMImageEntry getAMImageEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getAMImageEntryByUuidAndGroupId(uuid, groupId);
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
	public static InputStream getAMImageEntryContentStream(
		com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry
			amImageConfigurationEntry,
		com.liferay.portal.kernel.repository.model.FileVersion fileVersion) {

		return getService().getAMImageEntryContentStream(
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
	public static int getExpectedAMImageEntriesCount(long companyId) {
		return getService().getExpectedAMImageEntriesCount(companyId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static int getPercentage(
		long companyId, String configurationUuid,
		int expectedAMImageEntriesCount) {

		return getService().getPercentage(
			companyId, configurationUuid, expectedAMImageEntriesCount);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static boolean hasAMImageEntryContent(
		String configurationUuid,
		com.liferay.portal.kernel.repository.model.FileVersion fileVersion) {

		return getService().hasAMImageEntryContent(
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
	public static AMImageEntry updateAMImageEntry(AMImageEntry amImageEntry) {
		return getService().updateAMImageEntry(amImageEntry);
	}

	public static AMImageEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AMImageEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			AMImageEntryLocalServiceUtil.class, AMImageEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-996827883