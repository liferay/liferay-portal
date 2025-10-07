/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service;

import com.liferay.depot.model.DepotEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for DepotEntry. This utility wraps
 * <code>com.liferay.depot.service.impl.DepotEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DepotEntryLocalService
 * @generated
 */
public class DepotEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.depot.service.impl.DepotEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the depot entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntry the depot entry
	 * @return the depot entry that was added
	 */
	public static DepotEntry addDepotEntry(DepotEntry depotEntry) {
		return getService().addDepotEntry(depotEntry);
	}

	public static DepotEntry addDepotEntry(
			com.liferay.portal.kernel.model.Group group,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addDepotEntry(group, serviceContext);
	}

	public static DepotEntry addDepotEntry(
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addDepotEntry(
			nameMap, descriptionMap, type, serviceContext);
	}

	/**
	 * Creates a new depot entry with the primary key. Does not add the depot entry to the database.
	 *
	 * @param depotEntryId the primary key for the new depot entry
	 * @return the new depot entry
	 */
	public static DepotEntry createDepotEntry(long depotEntryId) {
		return getService().createDepotEntry(depotEntryId);
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
	 * Deletes the depot entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntry the depot entry
	 * @return the depot entry that was removed
	 * @throws PortalException
	 */
	public static DepotEntry deleteDepotEntry(DepotEntry depotEntry)
		throws PortalException {

		return getService().deleteDepotEntry(depotEntry);
	}

	/**
	 * Deletes the depot entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntryId the primary key of the depot entry
	 * @return the depot entry that was removed
	 * @throws PortalException if a depot entry with the primary key could not be found
	 */
	public static DepotEntry deleteDepotEntry(long depotEntryId)
		throws PortalException {

		return getService().deleteDepotEntry(depotEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.depot.model.impl.DepotEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.depot.model.impl.DepotEntryModelImpl</code>.
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

	public static DepotEntry fetchDepotEntry(long depotEntryId) {
		return getService().fetchDepotEntry(depotEntryId);
	}

	/**
	 * Returns the depot entry matching the UUID and group.
	 *
	 * @param uuid the depot entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching depot entry, or <code>null</code> if a matching depot entry could not be found
	 */
	public static DepotEntry fetchDepotEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchDepotEntryByUuidAndGroupId(uuid, groupId);
	}

	public static DepotEntry fetchGroupDepotEntry(long groupId) {
		return getService().fetchGroupDepotEntry(groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the depot entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.depot.model.impl.DepotEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of depot entries
	 * @param end the upper bound of the range of depot entries (not inclusive)
	 * @return the range of depot entries
	 */
	public static List<DepotEntry> getDepotEntries(int start, int end) {
		return getService().getDepotEntries(start, end);
	}

	public static List<DepotEntry> getDepotEntries(long companyId, int type) {
		return getService().getDepotEntries(companyId, type);
	}

	/**
	 * Returns all the depot entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the depot entries
	 * @param companyId the primary key of the company
	 * @return the matching depot entries, or an empty list if no matches were found
	 */
	public static List<DepotEntry> getDepotEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getDepotEntriesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of depot entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the depot entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of depot entries
	 * @param end the upper bound of the range of depot entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching depot entries, or an empty list if no matches were found
	 */
	public static List<DepotEntry> getDepotEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DepotEntry> orderByComparator) {

		return getService().getDepotEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of depot entries.
	 *
	 * @return the number of depot entries
	 */
	public static int getDepotEntriesCount() {
		return getService().getDepotEntriesCount();
	}

	public static int getDepotEntriesCount(long companyId, int type) {
		return getService().getDepotEntriesCount(companyId, type);
	}

	/**
	 * Returns the depot entry with the primary key.
	 *
	 * @param depotEntryId the primary key of the depot entry
	 * @return the depot entry
	 * @throws PortalException if a depot entry with the primary key could not be found
	 */
	public static DepotEntry getDepotEntry(long depotEntryId)
		throws PortalException {

		return getService().getDepotEntry(depotEntryId);
	}

	/**
	 * Returns the depot entry matching the UUID and group.
	 *
	 * @param uuid the depot entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching depot entry
	 * @throws PortalException if a matching depot entry could not be found
	 */
	public static DepotEntry getDepotEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getDepotEntryByUuidAndGroupId(uuid, groupId);
	}

	public static List<Long> getDepotEntryGroupIds(long companyId, int type) {
		return getService().getDepotEntryGroupIds(companyId, type);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	public static List<DepotEntry> getDepotEntryGroupRelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getDepotEntryGroupRelsByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static List<DepotEntry> getGroupConnectedDepotEntries(
			long groupId, boolean ddmStructuresAvailable, int start, int end)
		throws PortalException {

		return getService().getGroupConnectedDepotEntries(
			groupId, ddmStructuresAvailable, start, end);
	}

	public static List<DepotEntry> getGroupConnectedDepotEntries(
			long groupId, int type, int start, int end)
		throws PortalException {

		return getService().getGroupConnectedDepotEntries(
			groupId, type, start, end);
	}

	public static int getGroupConnectedDepotEntriesCount(
		long groupId, int type) {

		return getService().getGroupConnectedDepotEntriesCount(groupId, type);
	}

	public static DepotEntry getGroupDepotEntry(long groupId)
		throws PortalException {

		return getService().getGroupDepotEntry(groupId);
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
	 * Updates the depot entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntry the depot entry
	 * @return the depot entry that was updated
	 */
	public static DepotEntry updateDepotEntry(DepotEntry depotEntry) {
		return getService().updateDepotEntry(depotEntry);
	}

	public static DepotEntry updateDepotEntry(
			long depotEntryId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<String, Boolean> depotAppCustomizationMap,
			com.liferay.portal.kernel.util.UnicodeProperties
				typeSettingsUnicodeProperties,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateDepotEntry(
			depotEntryId, nameMap, descriptionMap, depotAppCustomizationMap,
			typeSettingsUnicodeProperties, serviceContext);
	}

	public static DepotEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DepotEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			DepotEntryLocalServiceUtil.class, DepotEntryLocalService.class);

}