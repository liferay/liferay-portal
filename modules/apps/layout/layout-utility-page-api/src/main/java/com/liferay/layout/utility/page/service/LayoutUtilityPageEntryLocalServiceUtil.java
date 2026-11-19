/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.service;

import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for LayoutUtilityPageEntry. This utility wraps
 * <code>com.liferay.layout.utility.page.service.impl.LayoutUtilityPageEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutUtilityPageEntryLocalService
 * @generated
 */
public class LayoutUtilityPageEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.layout.utility.page.service.impl.LayoutUtilityPageEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the layout utility page entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutUtilityPageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutUtilityPageEntry the layout utility page entry
	 * @return the layout utility page entry that was added
	 */
	public static LayoutUtilityPageEntry addLayoutUtilityPageEntry(
		LayoutUtilityPageEntry layoutUtilityPageEntry) {

		return getService().addLayoutUtilityPageEntry(layoutUtilityPageEntry);
	}

	public static LayoutUtilityPageEntry addLayoutUtilityPageEntry(
			String externalReferenceCode, long userId, long groupId, long plid,
			long previewFileEntryId, boolean defaultLayoutUtilityPageEntry,
			String name, String type, String masterLayoutPageTemplateEntryERC,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayoutUtilityPageEntry(
			externalReferenceCode, userId, groupId, plid, previewFileEntryId,
			defaultLayoutUtilityPageEntry, name, type,
			masterLayoutPageTemplateEntryERC, serviceContext);
	}

	public static LayoutUtilityPageEntry copyLayoutUtilityPageEntry(
			long userId, long groupId, long sourceLayoutUtilityPageEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		return getService().copyLayoutUtilityPageEntry(
			userId, groupId, sourceLayoutUtilityPageEntryId, serviceContext);
	}

	/**
	 * Creates a new layout utility page entry with the primary key. Does not add the layout utility page entry to the database.
	 *
	 * @param LayoutUtilityPageEntryId the primary key for the new layout utility page entry
	 * @return the new layout utility page entry
	 */
	public static LayoutUtilityPageEntry createLayoutUtilityPageEntry(
		long LayoutUtilityPageEntryId) {

		return getService().createLayoutUtilityPageEntry(
			LayoutUtilityPageEntryId);
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
	 * Deletes the layout utility page entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutUtilityPageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutUtilityPageEntry the layout utility page entry
	 * @return the layout utility page entry that was removed
	 * @throws PortalException
	 */
	public static LayoutUtilityPageEntry deleteLayoutUtilityPageEntry(
			LayoutUtilityPageEntry layoutUtilityPageEntry)
		throws PortalException {

		return getService().deleteLayoutUtilityPageEntry(
			layoutUtilityPageEntry);
	}

	/**
	 * Deletes the layout utility page entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutUtilityPageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the layout utility page entry
	 * @return the layout utility page entry that was removed
	 * @throws PortalException if a layout utility page entry with the primary key could not be found
	 */
	public static LayoutUtilityPageEntry deleteLayoutUtilityPageEntry(
			long LayoutUtilityPageEntryId)
		throws PortalException {

		return getService().deleteLayoutUtilityPageEntry(
			LayoutUtilityPageEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.utility.page.model.impl.LayoutUtilityPageEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.utility.page.model.impl.LayoutUtilityPageEntryModelImpl</code>.
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

	public static LayoutUtilityPageEntry fetchDefaultLayoutUtilityPageEntry(
		long groupId, String type) {

		return getService().fetchDefaultLayoutUtilityPageEntry(groupId, type);
	}

	public static LayoutUtilityPageEntry fetchLayoutUtilityPageEntry(
		long LayoutUtilityPageEntryId) {

		return getService().fetchLayoutUtilityPageEntry(
			LayoutUtilityPageEntryId);
	}

	public static LayoutUtilityPageEntry fetchLayoutUtilityPageEntry(
		long groupId, String name, String type) {

		return getService().fetchLayoutUtilityPageEntry(groupId, name, type);
	}

	public static LayoutUtilityPageEntry
		fetchLayoutUtilityPageEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return getService().fetchLayoutUtilityPageEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static LayoutUtilityPageEntry fetchLayoutUtilityPageEntryByPlid(
		long plid) {

		return getService().fetchLayoutUtilityPageEntryByPlid(plid);
	}

	/**
	 * Returns the layout utility page entry matching the UUID and group.
	 *
	 * @param uuid the layout utility page entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	public static LayoutUtilityPageEntry
		fetchLayoutUtilityPageEntryByUuidAndGroupId(String uuid, long groupId) {

		return getService().fetchLayoutUtilityPageEntryByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static LayoutUtilityPageEntry getDefaultLayoutUtilityPageEntry(
			long groupId, String type)
		throws PortalException {

		return getService().getDefaultLayoutUtilityPageEntry(groupId, type);
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
	 * Returns a range of all the layout utility page entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.utility.page.model.impl.LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of layout utility page entries
	 */
	public static List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		int start, int end) {

		return getService().getLayoutUtilityPageEntries(start, end);
	}

	public static List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId) {

		return getService().getLayoutUtilityPageEntries(groupId);
	}

	/**
	 * Returns all the layout utility page entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout utility page entries
	 * @param companyId the primary key of the company
	 * @return the matching layout utility page entries, or an empty list if no matches were found
	 */
	public static List<LayoutUtilityPageEntry>
		getLayoutUtilityPageEntriesByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getLayoutUtilityPageEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of layout utility page entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout utility page entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching layout utility page entries, or an empty list if no matches were found
	 */
	public static List<LayoutUtilityPageEntry>
		getLayoutUtilityPageEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return getService().getLayoutUtilityPageEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of layout utility page entries.
	 *
	 * @return the number of layout utility page entries
	 */
	public static int getLayoutUtilityPageEntriesCount() {
		return getService().getLayoutUtilityPageEntriesCount();
	}

	/**
	 * Returns the layout utility page entry with the primary key.
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the layout utility page entry
	 * @return the layout utility page entry
	 * @throws PortalException if a layout utility page entry with the primary key could not be found
	 */
	public static LayoutUtilityPageEntry getLayoutUtilityPageEntry(
			long LayoutUtilityPageEntryId)
		throws PortalException {

		return getService().getLayoutUtilityPageEntry(LayoutUtilityPageEntryId);
	}

	public static LayoutUtilityPageEntry
			getLayoutUtilityPageEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getLayoutUtilityPageEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the layout utility page entry matching the UUID and group.
	 *
	 * @param uuid the layout utility page entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout utility page entry
	 * @throws PortalException if a matching layout utility page entry could not be found
	 */
	public static LayoutUtilityPageEntry
			getLayoutUtilityPageEntryByUuidAndGroupId(String uuid, long groupId)
		throws PortalException {

		return getService().getLayoutUtilityPageEntryByUuidAndGroupId(
			uuid, groupId);
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

	public static LayoutUtilityPageEntry setDefaultLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId)
		throws PortalException {

		return getService().setDefaultLayoutUtilityPageEntry(
			layoutUtilityPageEntryId);
	}

	/**
	 * Updates the layout utility page entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutUtilityPageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutUtilityPageEntry the layout utility page entry
	 * @return the layout utility page entry that was updated
	 */
	public static LayoutUtilityPageEntry updateLayoutUtilityPageEntry(
		LayoutUtilityPageEntry layoutUtilityPageEntry) {

		return getService().updateLayoutUtilityPageEntry(
			layoutUtilityPageEntry);
	}

	public static LayoutUtilityPageEntry updateLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId, long previewFileEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateLayoutUtilityPageEntry(
			layoutUtilityPageEntryId, previewFileEntryId, serviceContext);
	}

	public static LayoutUtilityPageEntry updateLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateLayoutUtilityPageEntry(
			layoutUtilityPageEntryId, name, serviceContext);
	}

	public static LayoutUtilityPageEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<LayoutUtilityPageEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			LayoutUtilityPageEntryLocalServiceUtil.class,
			LayoutUtilityPageEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-579832017