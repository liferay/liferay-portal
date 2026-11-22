/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.service.access.policy.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for SAPEntry. This utility wraps
 * <code>com.liferay.portal.security.service.access.policy.service.impl.SAPEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see SAPEntryLocalService
 * @generated
 */
public class SAPEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.security.service.access.policy.service.impl.SAPEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static SAPEntry addSAPEntry(
			long userId, String allowedServiceSignatures,
			boolean defaultSAPEntry, boolean enabled, String name,
			Map<java.util.Locale, String> titleMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addSAPEntry(
			userId, allowedServiceSignatures, defaultSAPEntry, enabled, name,
			titleMap, serviceContext);
	}

	/**
	 * Adds the sap entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SAPEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sapEntry the sap entry
	 * @return the sap entry that was added
	 */
	public static SAPEntry addSAPEntry(SAPEntry sapEntry) {
		return getService().addSAPEntry(sapEntry);
	}

	public static void checkSystemSAPEntries(long companyId)
		throws PortalException {

		getService().checkSystemSAPEntries(companyId);
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
	 * Creates a new sap entry with the primary key. Does not add the sap entry to the database.
	 *
	 * @param sapEntryId the primary key for the new sap entry
	 * @return the new sap entry
	 */
	public static SAPEntry createSAPEntry(long sapEntryId) {
		return getService().createSAPEntry(sapEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the sap entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SAPEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sapEntryId the primary key of the sap entry
	 * @return the sap entry that was removed
	 * @throws PortalException if a sap entry with the primary key could not be found
	 */
	public static SAPEntry deleteSAPEntry(long sapEntryId)
		throws PortalException {

		return getService().deleteSAPEntry(sapEntryId);
	}

	/**
	 * Deletes the sap entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SAPEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sapEntry the sap entry
	 * @return the sap entry that was removed
	 * @throws PortalException
	 */
	public static SAPEntry deleteSAPEntry(SAPEntry sapEntry)
		throws PortalException {

		return getService().deleteSAPEntry(sapEntry);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.service.access.policy.model.impl.SAPEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.service.access.policy.model.impl.SAPEntryModelImpl</code>.
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

	public static SAPEntry fetchSAPEntry(long sapEntryId) {
		return getService().fetchSAPEntry(sapEntryId);
	}

	public static SAPEntry fetchSAPEntry(long companyId, String name) {
		return getService().fetchSAPEntry(companyId, name);
	}

	/**
	 * Returns the sap entry with the matching UUID and company.
	 *
	 * @param uuid the sap entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching sap entry, or <code>null</code> if a matching sap entry could not be found
	 */
	public static SAPEntry fetchSAPEntryByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchSAPEntryByUuidAndCompanyId(uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<SAPEntry> getCompanySAPEntries(
		long companyId, int start, int end) {

		return getService().getCompanySAPEntries(companyId, start, end);
	}

	public static List<SAPEntry> getDefaultSAPEntries(
		long companyId, boolean defaultSAPEntry) {

		return getService().getDefaultSAPEntries(companyId, defaultSAPEntry);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
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
	 * Returns a range of all the sap entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.service.access.policy.model.impl.SAPEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of sap entries
	 * @param end the upper bound of the range of sap entries (not inclusive)
	 * @return the range of sap entries
	 */
	public static List<SAPEntry> getSAPEntries(int start, int end) {
		return getService().getSAPEntries(start, end);
	}

	/**
	 * Returns the number of sap entries.
	 *
	 * @return the number of sap entries
	 */
	public static int getSAPEntriesCount() {
		return getService().getSAPEntriesCount();
	}

	/**
	 * Returns the sap entry with the primary key.
	 *
	 * @param sapEntryId the primary key of the sap entry
	 * @return the sap entry
	 * @throws PortalException if a sap entry with the primary key could not be found
	 */
	public static SAPEntry getSAPEntry(long sapEntryId) throws PortalException {
		return getService().getSAPEntry(sapEntryId);
	}

	public static SAPEntry getSAPEntry(long companyId, String name)
		throws PortalException {

		return getService().getSAPEntry(companyId, name);
	}

	/**
	 * Returns the sap entry with the matching UUID and company.
	 *
	 * @param uuid the sap entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching sap entry
	 * @throws PortalException if a matching sap entry could not be found
	 */
	public static SAPEntry getSAPEntryByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getSAPEntryByUuidAndCompanyId(uuid, companyId);
	}

	public static SAPEntry updateSAPEntry(
			long sapEntryId, String allowedServiceSignatures,
			boolean defaultSAPEntry, boolean enabled, String name,
			Map<java.util.Locale, String> titleMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateSAPEntry(
			sapEntryId, allowedServiceSignatures, defaultSAPEntry, enabled,
			name, titleMap, serviceContext);
	}

	/**
	 * Updates the sap entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SAPEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sapEntry the sap entry
	 * @return the sap entry that was updated
	 */
	public static SAPEntry updateSAPEntry(SAPEntry sapEntry) {
		return getService().updateSAPEntry(sapEntry);
	}

	public static SAPEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SAPEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			SAPEntryLocalServiceUtil.class, SAPEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:511641560