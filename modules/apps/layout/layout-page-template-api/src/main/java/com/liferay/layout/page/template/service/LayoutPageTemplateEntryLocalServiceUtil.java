/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for LayoutPageTemplateEntry. This utility wraps
 * <code>com.liferay.layout.page.template.service.impl.LayoutPageTemplateEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateEntryLocalService
 * @generated
 */
public class LayoutPageTemplateEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.layout.page.template.service.impl.LayoutPageTemplateEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static LayoutPageTemplateEntry addGlobalLayoutPageTemplateEntry(
			com.liferay.portal.kernel.model.LayoutPrototype layoutPrototype)
		throws PortalException {

		return getService().addGlobalLayoutPageTemplateEntry(layoutPrototype);
	}

	/**
	 * Adds the layout page template entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateEntry the layout page template entry
	 * @return the layout page template entry that was added
	 */
	public static LayoutPageTemplateEntry addLayoutPageTemplateEntry(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		return getService().addLayoutPageTemplateEntry(layoutPageTemplateEntry);
	}

	public static LayoutPageTemplateEntry addLayoutPageTemplateEntry(
			com.liferay.portal.kernel.model.LayoutPrototype layoutPrototype)
		throws PortalException {

		return getService().addLayoutPageTemplateEntry(layoutPrototype);
	}

	public static LayoutPageTemplateEntry addLayoutPageTemplateEntry(
			String externalReferenceCode, long userId, long groupId,
			long layoutPageTemplateCollectionId,
			String layoutPageTemplateEntryKey, long classNameId,
			long classTypeId, String name, int type, long previewFileEntryId,
			boolean defaultTemplate, long layoutPrototypeId, long plid,
			long masterLayoutPlid, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayoutPageTemplateEntry(
			externalReferenceCode, userId, groupId,
			layoutPageTemplateCollectionId, layoutPageTemplateEntryKey,
			classNameId, classTypeId, name, type, previewFileEntryId,
			defaultTemplate, layoutPrototypeId, plid, masterLayoutPlid, status,
			serviceContext);
	}

	public static LayoutPageTemplateEntry addLayoutPageTemplateEntry(
			String externalReferenceCode, long userId, long groupId,
			long layoutPageTemplateCollectionId,
			String layoutPageTemplateEntryKey, long classNameId,
			long classTypeId, String name, int type, long masterLayoutPlid,
			int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayoutPageTemplateEntry(
			externalReferenceCode, userId, groupId,
			layoutPageTemplateCollectionId, layoutPageTemplateEntryKey,
			classNameId, classTypeId, name, type, masterLayoutPlid, status,
			serviceContext);
	}

	public static LayoutPageTemplateEntry addLayoutPageTemplateEntry(
			String externalReferenceCode, long userId, long groupId,
			long layoutPageTemplateCollectionId,
			String layoutPageTemplateEntryKey, String name, int type,
			long masterLayoutPlid, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayoutPageTemplateEntry(
			externalReferenceCode, userId, groupId,
			layoutPageTemplateCollectionId, layoutPageTemplateEntryKey, name,
			type, masterLayoutPlid, status, serviceContext);
	}

	public static LayoutPageTemplateEntry copyLayoutPageTemplateEntry(
			long userId, long groupId, long layoutPageTemplateCollectionId,
			long sourceLayoutPageTemplateEntryId, boolean copyPermissions,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		return getService().copyLayoutPageTemplateEntry(
			userId, groupId, layoutPageTemplateCollectionId,
			sourceLayoutPageTemplateEntryId, copyPermissions, serviceContext);
	}

	/**
	 * Creates a new layout page template entry with the primary key. Does not add the layout page template entry to the database.
	 *
	 * @param layoutPageTemplateEntryId the primary key for the new layout page template entry
	 * @return the new layout page template entry
	 */
	public static LayoutPageTemplateEntry createLayoutPageTemplateEntry(
		long layoutPageTemplateEntryId) {

		return getService().createLayoutPageTemplateEntry(
			layoutPageTemplateEntryId);
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
	 * Deletes the layout page template entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateEntry the layout page template entry
	 * @return the layout page template entry that was removed
	 * @throws PortalException
	 */
	public static LayoutPageTemplateEntry deleteLayoutPageTemplateEntry(
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws PortalException {

		return getService().deleteLayoutPageTemplateEntry(
			layoutPageTemplateEntry);
	}

	/**
	 * Deletes the layout page template entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateEntryId the primary key of the layout page template entry
	 * @return the layout page template entry that was removed
	 * @throws PortalException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry deleteLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId)
		throws PortalException {

		return getService().deleteLayoutPageTemplateEntry(
			layoutPageTemplateEntryId);
	}

	public static LayoutPageTemplateEntry deleteLayoutPageTemplateEntry(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteLayoutPageTemplateEntry(
			externalReferenceCode, groupId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
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

	public static LayoutPageTemplateEntry fetchDefaultLayoutPageTemplateEntry(
		long groupId, long classNameId, long classTypeId) {

		return getService().fetchDefaultLayoutPageTemplateEntry(
			groupId, classNameId, classTypeId);
	}

	public static LayoutPageTemplateEntry fetchFirstLayoutPageTemplateEntry(
		long layoutPrototypeId) {

		return getService().fetchFirstLayoutPageTemplateEntry(
			layoutPrototypeId);
	}

	public static LayoutPageTemplateEntry fetchLayoutPageTemplateEntry(
		long layoutPageTemplateEntryId) {

		return getService().fetchLayoutPageTemplateEntry(
			layoutPageTemplateEntryId);
	}

	public static LayoutPageTemplateEntry fetchLayoutPageTemplateEntry(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int type) {

		return getService().fetchLayoutPageTemplateEntry(
			groupId, layoutPageTemplateCollectionId, name, type);
	}

	public static LayoutPageTemplateEntry fetchLayoutPageTemplateEntry(
		long groupId, String layoutPageTemplateEntryKey) {

		return getService().fetchLayoutPageTemplateEntry(
			groupId, layoutPageTemplateEntryKey);
	}

	public static LayoutPageTemplateEntry
		fetchLayoutPageTemplateEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return getService().fetchLayoutPageTemplateEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static LayoutPageTemplateEntry fetchLayoutPageTemplateEntryByPlid(
		long plid) {

		return getService().fetchLayoutPageTemplateEntryByPlid(plid);
	}

	/**
	 * Returns the layout page template entry matching the UUID and group.
	 *
	 * @param uuid the layout page template entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry
		fetchLayoutPageTemplateEntryByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchLayoutPageTemplateEntryByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static LayoutPageTemplateEntry getFirstLayoutPageTemplateEntry(
			long layoutPrototypeId)
		throws PortalException {

		return getService().getFirstLayoutPageTemplateEntry(layoutPrototypeId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the layout page template entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		int start, int end) {

		return getService().getLayoutPageTemplateEntries(start, end);
	}

	public static List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId) {

		return getService().getLayoutPageTemplateEntries(groupId);
	}

	public static List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, int status) {

		return getService().getLayoutPageTemplateEntries(groupId, status);
	}

	public static List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId) {

		return getService().getLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId);
	}

	public static List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int status) {

		return getService().getLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId, status);
	}

	public static List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int start, int end) {

		return getService().getLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId, start, end);
	}

	public static List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end) {

		return getService().getLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId, status, start, end);
	}

	public static List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getService().getLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId, status, start, end,
			orderByComparator);
	}

	public static List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getService().getLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId, start, end,
			orderByComparator);
	}

	public static List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getService().getLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId, name, status, start, end,
			orderByComparator);
	}

	public static List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getService().getLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId, name, start, end,
			orderByComparator);
	}

	public static List<LayoutPageTemplateEntry>
		getLayoutPageTemplateEntriesByLayoutPrototypeId(
			long layoutPrototypeId) {

		return getService().getLayoutPageTemplateEntriesByLayoutPrototypeId(
			layoutPrototypeId);
	}

	/**
	 * Returns all the layout page template entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout page template entries
	 * @param companyId the primary key of the company
	 * @return the matching layout page template entries, or an empty list if no matches were found
	 */
	public static List<LayoutPageTemplateEntry>
		getLayoutPageTemplateEntriesByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getLayoutPageTemplateEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of layout page template entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout page template entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching layout page template entries, or an empty list if no matches were found
	 */
	public static List<LayoutPageTemplateEntry>
		getLayoutPageTemplateEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getService().getLayoutPageTemplateEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of layout page template entries.
	 *
	 * @return the number of layout page template entries
	 */
	public static int getLayoutPageTemplateEntriesCount() {
		return getService().getLayoutPageTemplateEntriesCount();
	}

	/**
	 * Returns the layout page template entry with the primary key.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the layout page template entry
	 * @return the layout page template entry
	 * @throws PortalException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry getLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId)
		throws PortalException {

		return getService().getLayoutPageTemplateEntry(
			layoutPageTemplateEntryId);
	}

	public static LayoutPageTemplateEntry getLayoutPageTemplateEntry(
			long groupId, String layoutPageTemplateEntryKey)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getService().getLayoutPageTemplateEntry(
			groupId, layoutPageTemplateEntryKey);
	}

	public static LayoutPageTemplateEntry
			getLayoutPageTemplateEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getLayoutPageTemplateEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the layout page template entry matching the UUID and group.
	 *
	 * @param uuid the layout page template entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout page template entry
	 * @throws PortalException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry
			getLayoutPageTemplateEntryByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getLayoutPageTemplateEntryByUuidAndGroupId(
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

	public static String getUniqueLayoutPageTemplateEntryName(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int type) {

		return getService().getUniqueLayoutPageTemplateEntryName(
			groupId, layoutPageTemplateCollectionId, name, type);
	}

	public static LayoutPageTemplateEntry moveLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId,
			long targetLayoutPageTemplateCollectionId)
		throws PortalException {

		return getService().moveLayoutPageTemplateEntry(
			layoutPageTemplateEntryId, targetLayoutPageTemplateCollectionId);
	}

	/**
	 * Updates the layout page template entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateEntry the layout page template entry
	 * @return the layout page template entry that was updated
	 */
	public static LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		return getService().updateLayoutPageTemplateEntry(
			layoutPageTemplateEntry);
	}

	public static LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, boolean defaultTemplate)
		throws PortalException {

		return getService().updateLayoutPageTemplateEntry(
			layoutPageTemplateEntryId, defaultTemplate);
	}

	public static LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, long previewFileEntryId)
		throws PortalException {

		return getService().updateLayoutPageTemplateEntry(
			layoutPageTemplateEntryId, previewFileEntryId);
	}

	public static LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, long classNameId, long classTypeId)
		throws PortalException {

		return getService().updateLayoutPageTemplateEntry(
			layoutPageTemplateEntryId, classNameId, classTypeId);
	}

	public static LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long userId, long layoutPageTemplateEntryId, String name,
			int status)
		throws PortalException {

		return getService().updateLayoutPageTemplateEntry(
			userId, layoutPageTemplateEntryId, name, status);
	}

	public static LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, String name)
		throws PortalException {

		return getService().updateLayoutPageTemplateEntry(
			layoutPageTemplateEntryId, name);
	}

	public static LayoutPageTemplateEntry updateStatus(
			long userId, long layoutPageTemplateEntryId, int status)
		throws PortalException {

		return getService().updateStatus(
			userId, layoutPageTemplateEntryId, status);
	}

	public static LayoutPageTemplateEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<LayoutPageTemplateEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			LayoutPageTemplateEntryLocalServiceUtil.class,
			LayoutPageTemplateEntryLocalService.class);

}