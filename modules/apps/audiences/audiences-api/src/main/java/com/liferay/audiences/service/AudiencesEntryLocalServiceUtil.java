/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service;

import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for AudiencesEntry. This utility wraps
 * <code>com.liferay.audiences.service.impl.AudiencesEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AudiencesEntryLocalService
 * @generated
 */
public class AudiencesEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.audiences.service.impl.AudiencesEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static AudiencesEntry addAudiencesEntry(
		AudiencesEntry audiencesEntry) {

		return getService().addAudiencesEntry(audiencesEntry);
	}

	public static AudiencesEntry addAudiencesEntry(
			String externalReferenceCode, String json, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAudiencesEntry(
			externalReferenceCode, json, name, serviceContext);
	}

	/**
	 * Creates a new audiences entry with the primary key. Does not add the audiences entry to the database.
	 *
	 * @param audiencesEntryId the primary key for the new audiences entry
	 * @return the new audiences entry
	 */
	public static AudiencesEntry createAudiencesEntry(long audiencesEntryId) {
		return getService().createAudiencesEntry(audiencesEntryId);
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
	 * Deletes the audiences entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AudiencesEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param audiencesEntry the audiences entry
	 * @return the audiences entry that was removed
	 */
	public static AudiencesEntry deleteAudiencesEntry(
		AudiencesEntry audiencesEntry) {

		return getService().deleteAudiencesEntry(audiencesEntry);
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
	public static AudiencesEntry deleteAudiencesEntry(long audiencesEntryId)
		throws PortalException {

		return getService().deleteAudiencesEntry(audiencesEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryModelImpl</code>.
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

	public static AudiencesEntry fetchAudiencesEntry(long audiencesEntryId) {
		return getService().fetchAudiencesEntry(audiencesEntryId);
	}

	public static AudiencesEntry fetchAudiencesEntryByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchAudiencesEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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
	public static List<AudiencesEntry> getAudiencesEntries(int start, int end) {
		return getService().getAudiencesEntries(start, end);
	}

	public static List<AudiencesEntry> getAudiencesEntries(
		long companyId, int start, int end,
		OrderByComparator<AudiencesEntry> orderByComparator) {

		return getService().getAudiencesEntries(
			companyId, start, end, orderByComparator);
	}

	public static List<AudiencesEntry> getAudiencesEntries(
			long companyId, String name, int start, int end,
			OrderByComparator<AudiencesEntry> orderByComparator)
		throws PortalException {

		return getService().getAudiencesEntries(
			companyId, name, start, end, orderByComparator);
	}

	/**
	 * Returns the number of audiences entries.
	 *
	 * @return the number of audiences entries
	 */
	public static int getAudiencesEntriesCount() {
		return getService().getAudiencesEntriesCount();
	}

	public static int getAudiencesEntriesCount(long companyId) {
		return getService().getAudiencesEntriesCount(companyId);
	}

	public static int getAudiencesEntriesCount(long companyId, String name) {
		return getService().getAudiencesEntriesCount(companyId, name);
	}

	/**
	 * Returns the audiences entry with the primary key.
	 *
	 * @param audiencesEntryId the primary key of the audiences entry
	 * @return the audiences entry
	 * @throws PortalException if a audiences entry with the primary key could not be found
	 */
	public static AudiencesEntry getAudiencesEntry(long audiencesEntryId)
		throws PortalException {

		return getService().getAudiencesEntry(audiencesEntryId);
	}

	public static AudiencesEntry getAudiencesEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getAudiencesEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
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

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
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
	public static AudiencesEntry updateAudiencesEntry(
		AudiencesEntry audiencesEntry) {

		return getService().updateAudiencesEntry(audiencesEntry);
	}

	public static AudiencesEntry updateAudiencesEntry(
			long audiencesEntryId, String externalReferenceCode, String json,
			String name)
		throws PortalException {

		return getService().updateAudiencesEntry(
			audiencesEntryId, externalReferenceCode, json, name);
	}

	public static AudiencesEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AudiencesEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			AudiencesEntryLocalServiceUtil.class,
			AudiencesEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:2035146410