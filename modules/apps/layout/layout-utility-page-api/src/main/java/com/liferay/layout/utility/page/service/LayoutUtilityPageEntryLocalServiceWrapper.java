/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.service;

import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link LayoutUtilityPageEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutUtilityPageEntryLocalService
 * @generated
 */
public class LayoutUtilityPageEntryLocalServiceWrapper
	implements LayoutUtilityPageEntryLocalService,
			   ServiceWrapper<LayoutUtilityPageEntryLocalService> {

	public LayoutUtilityPageEntryLocalServiceWrapper() {
		this(null);
	}

	public LayoutUtilityPageEntryLocalServiceWrapper(
		LayoutUtilityPageEntryLocalService layoutUtilityPageEntryLocalService) {

		_layoutUtilityPageEntryLocalService =
			layoutUtilityPageEntryLocalService;
	}

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
	@Override
	public LayoutUtilityPageEntry addLayoutUtilityPageEntry(
		LayoutUtilityPageEntry layoutUtilityPageEntry) {

		return _layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
			layoutUtilityPageEntry);
	}

	@Override
	public LayoutUtilityPageEntry addLayoutUtilityPageEntry(
			String externalReferenceCode, long userId, long groupId, long plid,
			long previewFileEntryId, boolean defaultLayoutUtilityPageEntry,
			String name, String type, String masterLayoutPageTemplateEntryERC,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
			externalReferenceCode, userId, groupId, plid, previewFileEntryId,
			defaultLayoutUtilityPageEntry, name, type,
			masterLayoutPageTemplateEntryERC, serviceContext);
	}

	@Override
	public LayoutUtilityPageEntry copyLayoutUtilityPageEntry(
			long userId, long groupId, long sourceLayoutUtilityPageEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		return _layoutUtilityPageEntryLocalService.copyLayoutUtilityPageEntry(
			userId, groupId, sourceLayoutUtilityPageEntryId, serviceContext);
	}

	/**
	 * Creates a new layout utility page entry with the primary key. Does not add the layout utility page entry to the database.
	 *
	 * @param LayoutUtilityPageEntryId the primary key for the new layout utility page entry
	 * @return the new layout utility page entry
	 */
	@Override
	public LayoutUtilityPageEntry createLayoutUtilityPageEntry(
		long LayoutUtilityPageEntryId) {

		return _layoutUtilityPageEntryLocalService.createLayoutUtilityPageEntry(
			LayoutUtilityPageEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public LayoutUtilityPageEntry deleteLayoutUtilityPageEntry(
			LayoutUtilityPageEntry layoutUtilityPageEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryLocalService.deleteLayoutUtilityPageEntry(
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
	@Override
	public LayoutUtilityPageEntry deleteLayoutUtilityPageEntry(
			long LayoutUtilityPageEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryLocalService.deleteLayoutUtilityPageEntry(
			LayoutUtilityPageEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _layoutUtilityPageEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _layoutUtilityPageEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _layoutUtilityPageEntryLocalService.dynamicQuery();
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

		return _layoutUtilityPageEntryLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _layoutUtilityPageEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _layoutUtilityPageEntryLocalService.dynamicQuery(
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

		return _layoutUtilityPageEntryLocalService.dynamicQueryCount(
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

		return _layoutUtilityPageEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public LayoutUtilityPageEntry fetchDefaultLayoutUtilityPageEntry(
		long groupId, String type) {

		return _layoutUtilityPageEntryLocalService.
			fetchDefaultLayoutUtilityPageEntry(groupId, type);
	}

	@Override
	public LayoutUtilityPageEntry fetchLayoutUtilityPageEntry(
		long LayoutUtilityPageEntryId) {

		return _layoutUtilityPageEntryLocalService.fetchLayoutUtilityPageEntry(
			LayoutUtilityPageEntryId);
	}

	@Override
	public LayoutUtilityPageEntry fetchLayoutUtilityPageEntry(
		long groupId, String name, String type) {

		return _layoutUtilityPageEntryLocalService.fetchLayoutUtilityPageEntry(
			groupId, name, type);
	}

	@Override
	public LayoutUtilityPageEntry
		fetchLayoutUtilityPageEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return _layoutUtilityPageEntryLocalService.
			fetchLayoutUtilityPageEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public LayoutUtilityPageEntry fetchLayoutUtilityPageEntryByPlid(long plid) {
		return _layoutUtilityPageEntryLocalService.
			fetchLayoutUtilityPageEntryByPlid(plid);
	}

	/**
	 * Returns the layout utility page entry matching the UUID and group.
	 *
	 * @param uuid the layout utility page entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchLayoutUtilityPageEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return _layoutUtilityPageEntryLocalService.
			fetchLayoutUtilityPageEntryByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _layoutUtilityPageEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public LayoutUtilityPageEntry getDefaultLayoutUtilityPageEntry(
			long groupId, String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryLocalService.
			getDefaultLayoutUtilityPageEntry(groupId, type);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _layoutUtilityPageEntryLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _layoutUtilityPageEntryLocalService.
			getIndexableActionableDynamicQuery();
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
	@Override
	public java.util.List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		int start, int end) {

		return _layoutUtilityPageEntryLocalService.getLayoutUtilityPageEntries(
			start, end);
	}

	@Override
	public java.util.List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId) {

		return _layoutUtilityPageEntryLocalService.getLayoutUtilityPageEntries(
			groupId);
	}

	/**
	 * Returns all the layout utility page entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout utility page entries
	 * @param companyId the primary key of the company
	 * @return the matching layout utility page entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<LayoutUtilityPageEntry>
		getLayoutUtilityPageEntriesByUuidAndCompanyId(
			String uuid, long companyId) {

		return _layoutUtilityPageEntryLocalService.
			getLayoutUtilityPageEntriesByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<LayoutUtilityPageEntry>
		getLayoutUtilityPageEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutUtilityPageEntry> orderByComparator) {

		return _layoutUtilityPageEntryLocalService.
			getLayoutUtilityPageEntriesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of layout utility page entries.
	 *
	 * @return the number of layout utility page entries
	 */
	@Override
	public int getLayoutUtilityPageEntriesCount() {
		return _layoutUtilityPageEntryLocalService.
			getLayoutUtilityPageEntriesCount();
	}

	/**
	 * Returns the layout utility page entry with the primary key.
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the layout utility page entry
	 * @return the layout utility page entry
	 * @throws PortalException if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry getLayoutUtilityPageEntry(
			long LayoutUtilityPageEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryLocalService.getLayoutUtilityPageEntry(
			LayoutUtilityPageEntryId);
	}

	@Override
	public LayoutUtilityPageEntry
			getLayoutUtilityPageEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryLocalService.
			getLayoutUtilityPageEntryByExternalReferenceCode(
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
	@Override
	public LayoutUtilityPageEntry getLayoutUtilityPageEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryLocalService.
			getLayoutUtilityPageEntryByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _layoutUtilityPageEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public LayoutUtilityPageEntry setDefaultLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryLocalService.
			setDefaultLayoutUtilityPageEntry(layoutUtilityPageEntryId);
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
	@Override
	public LayoutUtilityPageEntry updateLayoutUtilityPageEntry(
		LayoutUtilityPageEntry layoutUtilityPageEntry) {

		return _layoutUtilityPageEntryLocalService.updateLayoutUtilityPageEntry(
			layoutUtilityPageEntry);
	}

	@Override
	public LayoutUtilityPageEntry updateLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId, long previewFileEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryLocalService.updateLayoutUtilityPageEntry(
			layoutUtilityPageEntryId, previewFileEntryId, serviceContext);
	}

	@Override
	public LayoutUtilityPageEntry updateLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryLocalService.updateLayoutUtilityPageEntry(
			layoutUtilityPageEntryId, name, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _layoutUtilityPageEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<LayoutUtilityPageEntry> getCTPersistence() {
		return _layoutUtilityPageEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<LayoutUtilityPageEntry> getModelClass() {
		return _layoutUtilityPageEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<LayoutUtilityPageEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _layoutUtilityPageEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public LayoutUtilityPageEntryLocalService getWrappedService() {
		return _layoutUtilityPageEntryLocalService;
	}

	@Override
	public void setWrappedService(
		LayoutUtilityPageEntryLocalService layoutUtilityPageEntryLocalService) {

		_layoutUtilityPageEntryLocalService =
			layoutUtilityPageEntryLocalService;
	}

	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:237549645