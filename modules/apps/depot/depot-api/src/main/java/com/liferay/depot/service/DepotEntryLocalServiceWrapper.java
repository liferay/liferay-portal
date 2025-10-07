/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service;

import com.liferay.depot.model.DepotEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DepotEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DepotEntryLocalService
 * @generated
 */
public class DepotEntryLocalServiceWrapper
	implements DepotEntryLocalService, ServiceWrapper<DepotEntryLocalService> {

	public DepotEntryLocalServiceWrapper() {
		this(null);
	}

	public DepotEntryLocalServiceWrapper(
		DepotEntryLocalService depotEntryLocalService) {

		_depotEntryLocalService = depotEntryLocalService;
	}

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
	@Override
	public DepotEntry addDepotEntry(DepotEntry depotEntry) {
		return _depotEntryLocalService.addDepotEntry(depotEntry);
	}

	@Override
	public DepotEntry addDepotEntry(
			com.liferay.portal.kernel.model.Group group,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryLocalService.addDepotEntry(group, serviceContext);
	}

	@Override
	public DepotEntry addDepotEntry(
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryLocalService.addDepotEntry(
			nameMap, descriptionMap, type, serviceContext);
	}

	/**
	 * Creates a new depot entry with the primary key. Does not add the depot entry to the database.
	 *
	 * @param depotEntryId the primary key for the new depot entry
	 * @return the new depot entry
	 */
	@Override
	public DepotEntry createDepotEntry(long depotEntryId) {
		return _depotEntryLocalService.createDepotEntry(depotEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryLocalService.createPersistedModel(primaryKeyObj);
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
	@Override
	public DepotEntry deleteDepotEntry(DepotEntry depotEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryLocalService.deleteDepotEntry(depotEntry);
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
	@Override
	public DepotEntry deleteDepotEntry(long depotEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryLocalService.deleteDepotEntry(depotEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _depotEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _depotEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _depotEntryLocalService.dynamicQuery();
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

		return _depotEntryLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _depotEntryLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _depotEntryLocalService.dynamicQuery(
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

		return _depotEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _depotEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DepotEntry fetchDepotEntry(long depotEntryId) {
		return _depotEntryLocalService.fetchDepotEntry(depotEntryId);
	}

	/**
	 * Returns the depot entry matching the UUID and group.
	 *
	 * @param uuid the depot entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching depot entry, or <code>null</code> if a matching depot entry could not be found
	 */
	@Override
	public DepotEntry fetchDepotEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return _depotEntryLocalService.fetchDepotEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public DepotEntry fetchGroupDepotEntry(long groupId) {
		return _depotEntryLocalService.fetchGroupDepotEntry(groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _depotEntryLocalService.getActionableDynamicQuery();
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
	@Override
	public java.util.List<DepotEntry> getDepotEntries(int start, int end) {
		return _depotEntryLocalService.getDepotEntries(start, end);
	}

	@Override
	public java.util.List<DepotEntry> getDepotEntries(
		long companyId, int type) {

		return _depotEntryLocalService.getDepotEntries(companyId, type);
	}

	/**
	 * Returns all the depot entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the depot entries
	 * @param companyId the primary key of the company
	 * @return the matching depot entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<DepotEntry> getDepotEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return _depotEntryLocalService.getDepotEntriesByUuidAndCompanyId(
			uuid, companyId);
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
	@Override
	public java.util.List<DepotEntry> getDepotEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
			orderByComparator) {

		return _depotEntryLocalService.getDepotEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of depot entries.
	 *
	 * @return the number of depot entries
	 */
	@Override
	public int getDepotEntriesCount() {
		return _depotEntryLocalService.getDepotEntriesCount();
	}

	@Override
	public int getDepotEntriesCount(long companyId, int type) {
		return _depotEntryLocalService.getDepotEntriesCount(companyId, type);
	}

	/**
	 * Returns the depot entry with the primary key.
	 *
	 * @param depotEntryId the primary key of the depot entry
	 * @return the depot entry
	 * @throws PortalException if a depot entry with the primary key could not be found
	 */
	@Override
	public DepotEntry getDepotEntry(long depotEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryLocalService.getDepotEntry(depotEntryId);
	}

	/**
	 * Returns the depot entry matching the UUID and group.
	 *
	 * @param uuid the depot entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching depot entry
	 * @throws PortalException if a matching depot entry could not be found
	 */
	@Override
	public DepotEntry getDepotEntryByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryLocalService.getDepotEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public java.util.List<Long> getDepotEntryGroupIds(
		long companyId, int type) {

		return _depotEntryLocalService.getDepotEntryGroupIds(companyId, type);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public java.util.List<DepotEntry> getDepotEntryGroupRelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _depotEntryLocalService.getDepotEntryGroupRelsByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _depotEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public java.util.List<DepotEntry> getGroupConnectedDepotEntries(
			long groupId, boolean ddmStructuresAvailable, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryLocalService.getGroupConnectedDepotEntries(
			groupId, ddmStructuresAvailable, start, end);
	}

	@Override
	public java.util.List<DepotEntry> getGroupConnectedDepotEntries(
			long groupId, int type, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryLocalService.getGroupConnectedDepotEntries(
			groupId, type, start, end);
	}

	@Override
	public int getGroupConnectedDepotEntriesCount(long groupId, int type) {
		return _depotEntryLocalService.getGroupConnectedDepotEntriesCount(
			groupId, type);
	}

	@Override
	public DepotEntry getGroupDepotEntry(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryLocalService.getGroupDepotEntry(groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _depotEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _depotEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryLocalService.getPersistedModel(primaryKeyObj);
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
	@Override
	public DepotEntry updateDepotEntry(DepotEntry depotEntry) {
		return _depotEntryLocalService.updateDepotEntry(depotEntry);
	}

	@Override
	public DepotEntry updateDepotEntry(
			long depotEntryId, java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			java.util.Map<String, Boolean> depotAppCustomizationMap,
			com.liferay.portal.kernel.util.UnicodeProperties
				typeSettingsUnicodeProperties,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryLocalService.updateDepotEntry(
			depotEntryId, nameMap, descriptionMap, depotAppCustomizationMap,
			typeSettingsUnicodeProperties, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _depotEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DepotEntry> getCTPersistence() {
		return _depotEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<DepotEntry> getModelClass() {
		return _depotEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DepotEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _depotEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DepotEntryLocalService getWrappedService() {
		return _depotEntryLocalService;
	}

	@Override
	public void setWrappedService(
		DepotEntryLocalService depotEntryLocalService) {

		_depotEntryLocalService = depotEntryLocalService;
	}

	private DepotEntryLocalService _depotEntryLocalService;

}