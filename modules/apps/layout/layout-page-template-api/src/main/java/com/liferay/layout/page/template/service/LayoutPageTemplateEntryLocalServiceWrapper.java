/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link LayoutPageTemplateEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateEntryLocalService
 * @generated
 */
public class LayoutPageTemplateEntryLocalServiceWrapper
	implements LayoutPageTemplateEntryLocalService,
			   ServiceWrapper<LayoutPageTemplateEntryLocalService> {

	public LayoutPageTemplateEntryLocalServiceWrapper() {
		this(null);
	}

	public LayoutPageTemplateEntryLocalServiceWrapper(
		LayoutPageTemplateEntryLocalService
			layoutPageTemplateEntryLocalService) {

		_layoutPageTemplateEntryLocalService =
			layoutPageTemplateEntryLocalService;
	}

	@Override
	public LayoutPageTemplateEntry addGlobalLayoutPageTemplateEntry(
			com.liferay.portal.kernel.model.LayoutPrototype layoutPrototype)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.
			addGlobalLayoutPageTemplateEntry(layoutPrototype);
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
	@Override
	public LayoutPageTemplateEntry addLayoutPageTemplateEntry(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		return _layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
			layoutPageTemplateEntry);
	}

	@Override
	public LayoutPageTemplateEntry addLayoutPageTemplateEntry(
			com.liferay.portal.kernel.model.LayoutPrototype layoutPrototype)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
			layoutPrototype);
	}

	@Override
	public LayoutPageTemplateEntry addLayoutPageTemplateEntry(
			String externalReferenceCode, long userId, long groupId,
			long layoutPageTemplateCollectionId,
			String layoutPageTemplateEntryKey, long classNameId,
			long classTypeId, String name, int type, long previewFileEntryId,
			boolean defaultTemplate, long layoutPrototypeId, long plid,
			long masterLayoutPlid, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
			externalReferenceCode, userId, groupId,
			layoutPageTemplateCollectionId, layoutPageTemplateEntryKey,
			classNameId, classTypeId, name, type, previewFileEntryId,
			defaultTemplate, layoutPrototypeId, plid, masterLayoutPlid, status,
			serviceContext);
	}

	@Override
	public LayoutPageTemplateEntry addLayoutPageTemplateEntry(
			String externalReferenceCode, long userId, long groupId,
			long layoutPageTemplateCollectionId,
			String layoutPageTemplateEntryKey, long classNameId,
			long classTypeId, String name, int type, long masterLayoutPlid,
			int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
			externalReferenceCode, userId, groupId,
			layoutPageTemplateCollectionId, layoutPageTemplateEntryKey,
			classNameId, classTypeId, name, type, masterLayoutPlid, status,
			serviceContext);
	}

	@Override
	public LayoutPageTemplateEntry addLayoutPageTemplateEntry(
			String externalReferenceCode, long userId, long groupId,
			long layoutPageTemplateCollectionId,
			String layoutPageTemplateEntryKey, String name, int type,
			long masterLayoutPlid, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
			externalReferenceCode, userId, groupId,
			layoutPageTemplateCollectionId, layoutPageTemplateEntryKey, name,
			type, masterLayoutPlid, status, serviceContext);
	}

	@Override
	public LayoutPageTemplateEntry copyLayoutPageTemplateEntry(
			long userId, long groupId, long layoutPageTemplateCollectionId,
			long sourceLayoutPageTemplateEntryId, boolean copyPermissions,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		return _layoutPageTemplateEntryLocalService.copyLayoutPageTemplateEntry(
			userId, groupId, layoutPageTemplateCollectionId,
			sourceLayoutPageTemplateEntryId, copyPermissions, serviceContext);
	}

	/**
	 * Creates a new layout page template entry with the primary key. Does not add the layout page template entry to the database.
	 *
	 * @param layoutPageTemplateEntryId the primary key for the new layout page template entry
	 * @return the new layout page template entry
	 */
	@Override
	public LayoutPageTemplateEntry createLayoutPageTemplateEntry(
		long layoutPageTemplateEntryId) {

		return _layoutPageTemplateEntryLocalService.
			createLayoutPageTemplateEntry(layoutPageTemplateEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public LayoutPageTemplateEntry deleteLayoutPageTemplateEntry(
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.
			deleteLayoutPageTemplateEntry(layoutPageTemplateEntry);
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
	@Override
	public LayoutPageTemplateEntry deleteLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.
			deleteLayoutPageTemplateEntry(layoutPageTemplateEntryId);
	}

	@Override
	public LayoutPageTemplateEntry deleteLayoutPageTemplateEntry(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.
			deleteLayoutPageTemplateEntry(externalReferenceCode, groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _layoutPageTemplateEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _layoutPageTemplateEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _layoutPageTemplateEntryLocalService.dynamicQuery();
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

		return _layoutPageTemplateEntryLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _layoutPageTemplateEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _layoutPageTemplateEntryLocalService.dynamicQuery(
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

		return _layoutPageTemplateEntryLocalService.dynamicQueryCount(
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

		return _layoutPageTemplateEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public LayoutPageTemplateEntry fetchDefaultLayoutPageTemplateEntry(
		long groupId, long classNameId, long classTypeId) {

		return _layoutPageTemplateEntryLocalService.
			fetchDefaultLayoutPageTemplateEntry(
				groupId, classNameId, classTypeId);
	}

	@Override
	public LayoutPageTemplateEntry fetchFirstLayoutPageTemplateEntry(
		long layoutPrototypeId) {

		return _layoutPageTemplateEntryLocalService.
			fetchFirstLayoutPageTemplateEntry(layoutPrototypeId);
	}

	@Override
	public LayoutPageTemplateEntry fetchLayoutPageTemplateEntry(
		long layoutPageTemplateEntryId) {

		return _layoutPageTemplateEntryLocalService.
			fetchLayoutPageTemplateEntry(layoutPageTemplateEntryId);
	}

	@Override
	public LayoutPageTemplateEntry fetchLayoutPageTemplateEntry(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int type) {

		return _layoutPageTemplateEntryLocalService.
			fetchLayoutPageTemplateEntry(
				groupId, layoutPageTemplateCollectionId, name, type);
	}

	@Override
	public LayoutPageTemplateEntry fetchLayoutPageTemplateEntry(
		long groupId, String layoutPageTemplateEntryKey) {

		return _layoutPageTemplateEntryLocalService.
			fetchLayoutPageTemplateEntry(groupId, layoutPageTemplateEntryKey);
	}

	@Override
	public LayoutPageTemplateEntry
		fetchLayoutPageTemplateEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return _layoutPageTemplateEntryLocalService.
			fetchLayoutPageTemplateEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public LayoutPageTemplateEntry fetchLayoutPageTemplateEntryByPlid(
		long plid) {

		return _layoutPageTemplateEntryLocalService.
			fetchLayoutPageTemplateEntryByPlid(plid);
	}

	/**
	 * Returns the layout page template entry matching the UUID and group.
	 *
	 * @param uuid the layout page template entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchLayoutPageTemplateEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return _layoutPageTemplateEntryLocalService.
			fetchLayoutPageTemplateEntryByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _layoutPageTemplateEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _layoutPageTemplateEntryLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public LayoutPageTemplateEntry getFirstLayoutPageTemplateEntry(
			long layoutPrototypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.
			getFirstLayoutPageTemplateEntry(layoutPrototypeId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _layoutPageTemplateEntryLocalService.
			getIndexableActionableDynamicQuery();
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
	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		int start, int end) {

		return _layoutPageTemplateEntryLocalService.
			getLayoutPageTemplateEntries(start, end);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId) {

		return _layoutPageTemplateEntryLocalService.
			getLayoutPageTemplateEntries(groupId);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, int status) {

		return _layoutPageTemplateEntryLocalService.
			getLayoutPageTemplateEntries(groupId, status);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId) {

		return _layoutPageTemplateEntryLocalService.
			getLayoutPageTemplateEntries(
				groupId, layoutPageTemplateCollectionId);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int status) {

		return _layoutPageTemplateEntryLocalService.
			getLayoutPageTemplateEntries(
				groupId, layoutPageTemplateCollectionId, status);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int start, int end) {

		return _layoutPageTemplateEntryLocalService.
			getLayoutPageTemplateEntries(
				groupId, layoutPageTemplateCollectionId, start, end);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end) {

		return _layoutPageTemplateEntryLocalService.
			getLayoutPageTemplateEntries(
				groupId, layoutPageTemplateCollectionId, status, start, end);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryLocalService.
			getLayoutPageTemplateEntries(
				groupId, layoutPageTemplateCollectionId, status, start, end,
				orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryLocalService.
			getLayoutPageTemplateEntries(
				groupId, layoutPageTemplateCollectionId, start, end,
				orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryLocalService.
			getLayoutPageTemplateEntries(
				groupId, layoutPageTemplateCollectionId, name, status, start,
				end, orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryLocalService.
			getLayoutPageTemplateEntries(
				groupId, layoutPageTemplateCollectionId, name, start, end,
				orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry>
		getLayoutPageTemplateEntriesByLayoutPrototypeId(
			long layoutPrototypeId) {

		return _layoutPageTemplateEntryLocalService.
			getLayoutPageTemplateEntriesByLayoutPrototypeId(layoutPrototypeId);
	}

	/**
	 * Returns all the layout page template entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout page template entries
	 * @param companyId the primary key of the company
	 * @return the matching layout page template entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<LayoutPageTemplateEntry>
		getLayoutPageTemplateEntriesByUuidAndCompanyId(
			String uuid, long companyId) {

		return _layoutPageTemplateEntryLocalService.
			getLayoutPageTemplateEntriesByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<LayoutPageTemplateEntry>
		getLayoutPageTemplateEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryLocalService.
			getLayoutPageTemplateEntriesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of layout page template entries.
	 *
	 * @return the number of layout page template entries
	 */
	@Override
	public int getLayoutPageTemplateEntriesCount() {
		return _layoutPageTemplateEntryLocalService.
			getLayoutPageTemplateEntriesCount();
	}

	/**
	 * Returns the layout page template entry with the primary key.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the layout page template entry
	 * @return the layout page template entry
	 * @throws PortalException if a layout page template entry with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateEntry getLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntry(
			layoutPageTemplateEntryId);
	}

	@Override
	public LayoutPageTemplateEntry getLayoutPageTemplateEntry(
			long groupId, String layoutPageTemplateEntryKey)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return _layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntry(
			groupId, layoutPageTemplateEntryKey);
	}

	@Override
	public LayoutPageTemplateEntry
			getLayoutPageTemplateEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.
			getLayoutPageTemplateEntryByExternalReferenceCode(
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
	@Override
	public LayoutPageTemplateEntry getLayoutPageTemplateEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.
			getLayoutPageTemplateEntryByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _layoutPageTemplateEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public String getUniqueLayoutPageTemplateEntryName(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int type) {

		return _layoutPageTemplateEntryLocalService.
			getUniqueLayoutPageTemplateEntryName(
				groupId, layoutPageTemplateCollectionId, name, type);
	}

	@Override
	public LayoutPageTemplateEntry moveLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId,
			long targetLayoutPageTemplateCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.moveLayoutPageTemplateEntry(
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
	@Override
	public LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		return _layoutPageTemplateEntryLocalService.
			updateLayoutPageTemplateEntry(layoutPageTemplateEntry);
	}

	@Override
	public LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, boolean defaultTemplate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.
			updateLayoutPageTemplateEntry(
				layoutPageTemplateEntryId, defaultTemplate);
	}

	@Override
	public LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, long previewFileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.
			updateLayoutPageTemplateEntry(
				layoutPageTemplateEntryId, previewFileEntryId);
	}

	@Override
	public LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, long classNameId, long classTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.
			updateLayoutPageTemplateEntry(
				layoutPageTemplateEntryId, classNameId, classTypeId);
	}

	@Override
	public LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long userId, long layoutPageTemplateEntryId, String name,
			int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.
			updateLayoutPageTemplateEntry(
				userId, layoutPageTemplateEntryId, name, status);
	}

	@Override
	public LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.
			updateLayoutPageTemplateEntry(layoutPageTemplateEntryId, name);
	}

	@Override
	public LayoutPageTemplateEntry updateStatus(
			long userId, long layoutPageTemplateEntryId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryLocalService.updateStatus(
			userId, layoutPageTemplateEntryId, status);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _layoutPageTemplateEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<LayoutPageTemplateEntry> getCTPersistence() {
		return _layoutPageTemplateEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<LayoutPageTemplateEntry> getModelClass() {
		return _layoutPageTemplateEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<LayoutPageTemplateEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _layoutPageTemplateEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public LayoutPageTemplateEntryLocalService getWrappedService() {
		return _layoutPageTemplateEntryLocalService;
	}

	@Override
	public void setWrappedService(
		LayoutPageTemplateEntryLocalService
			layoutPageTemplateEntryLocalService) {

		_layoutPageTemplateEntryLocalService =
			layoutPageTemplateEntryLocalService;
	}

	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

}