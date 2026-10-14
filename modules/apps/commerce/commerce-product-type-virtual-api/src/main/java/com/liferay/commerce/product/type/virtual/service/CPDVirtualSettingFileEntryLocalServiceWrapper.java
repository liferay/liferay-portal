/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CPDVirtualSettingFileEntryLocalService}.
 *
 * @author Marco Leo
 * @see CPDVirtualSettingFileEntryLocalService
 * @generated
 */
public class CPDVirtualSettingFileEntryLocalServiceWrapper
	implements CPDVirtualSettingFileEntryLocalService,
			   ServiceWrapper<CPDVirtualSettingFileEntryLocalService> {

	public CPDVirtualSettingFileEntryLocalServiceWrapper() {
		this(null);
	}

	public CPDVirtualSettingFileEntryLocalServiceWrapper(
		CPDVirtualSettingFileEntryLocalService
			cpdVirtualSettingFileEntryLocalService) {

		_cpdVirtualSettingFileEntryLocalService =
			cpdVirtualSettingFileEntryLocalService;
	}

	/**
	 * Adds the cpd virtual setting file entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDVirtualSettingFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpdVirtualSettingFileEntry the cpd virtual setting file entry
	 * @return the cpd virtual setting file entry that was added
	 */
	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDVirtualSettingFileEntry addCPDVirtualSettingFileEntry(
				com.liferay.commerce.product.type.virtual.model.
					CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry) {

		return _cpdVirtualSettingFileEntryLocalService.
			addCPDVirtualSettingFileEntry(cpdVirtualSettingFileEntry);
	}

	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDVirtualSettingFileEntry addCPDVirtualSettingFileEntry(
					long userId, long groupId,
					long cpDefinitionVirtualSettingId, long fileEntryId,
					String url, String version)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _cpdVirtualSettingFileEntryLocalService.
			addCPDVirtualSettingFileEntry(
				userId, groupId, cpDefinitionVirtualSettingId, fileEntryId, url,
				version);
	}

	@Override
	public com.liferay.portal.kernel.repository.model.FileEntry addFileEntry(
			long userId, long groupId, String className, long classPK,
			String serviceName, long folderId, java.io.InputStream inputStream,
			String fileName, String mimeType)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpdVirtualSettingFileEntryLocalService.addFileEntry(
			userId, groupId, className, classPK, serviceName, folderId,
			inputStream, fileName, mimeType);
	}

	/**
	 * Creates a new cpd virtual setting file entry with the primary key. Does not add the cpd virtual setting file entry to the database.
	 *
	 * @param CPDefinitionVirtualSettingFileEntryId the primary key for the new cpd virtual setting file entry
	 * @return the new cpd virtual setting file entry
	 */
	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDVirtualSettingFileEntry createCPDVirtualSettingFileEntry(
				long CPDefinitionVirtualSettingFileEntryId) {

		return _cpdVirtualSettingFileEntryLocalService.
			createCPDVirtualSettingFileEntry(
				CPDefinitionVirtualSettingFileEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpdVirtualSettingFileEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteCPDVirtualSettingFileEntries(
			long cpDefinitionVirtualSettingId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpdVirtualSettingFileEntryLocalService.
			deleteCPDVirtualSettingFileEntries(cpDefinitionVirtualSettingId);
	}

	/**
	 * Deletes the cpd virtual setting file entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDVirtualSettingFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpdVirtualSettingFileEntry the cpd virtual setting file entry
	 * @return the cpd virtual setting file entry that was removed
	 */
	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDVirtualSettingFileEntry deleteCPDVirtualSettingFileEntry(
				com.liferay.commerce.product.type.virtual.model.
					CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry) {

		return _cpdVirtualSettingFileEntryLocalService.
			deleteCPDVirtualSettingFileEntry(cpdVirtualSettingFileEntry);
	}

	/**
	 * Deletes the cpd virtual setting file entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDVirtualSettingFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPDefinitionVirtualSettingFileEntryId the primary key of the cpd virtual setting file entry
	 * @return the cpd virtual setting file entry that was removed
	 * @throws PortalException if a cpd virtual setting file entry with the primary key could not be found
	 */
	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDVirtualSettingFileEntry deleteCPDVirtualSettingFileEntry(
					long CPDefinitionVirtualSettingFileEntryId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _cpdVirtualSettingFileEntryLocalService.
			deleteCPDVirtualSettingFileEntry(
				CPDefinitionVirtualSettingFileEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpdVirtualSettingFileEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpdVirtualSettingFileEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpdVirtualSettingFileEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpdVirtualSettingFileEntryLocalService.dynamicQuery();
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

		return _cpdVirtualSettingFileEntryLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
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

		return _cpdVirtualSettingFileEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
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

		return _cpdVirtualSettingFileEntryLocalService.dynamicQuery(
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

		return _cpdVirtualSettingFileEntryLocalService.dynamicQueryCount(
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

		return _cpdVirtualSettingFileEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDVirtualSettingFileEntry fetchCPDVirtualSettingFileEntry(
				long CPDefinitionVirtualSettingFileEntryId) {

		return _cpdVirtualSettingFileEntryLocalService.
			fetchCPDVirtualSettingFileEntry(
				CPDefinitionVirtualSettingFileEntryId);
	}

	/**
	 * Returns the cpd virtual setting file entry matching the UUID and group.
	 *
	 * @param uuid the cpd virtual setting file entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cpd virtual setting file entry, or <code>null</code> if a matching cpd virtual setting file entry could not be found
	 */
	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDVirtualSettingFileEntry
				fetchCPDVirtualSettingFileEntryByUuidAndGroupId(
					String uuid, long groupId) {

		return _cpdVirtualSettingFileEntryLocalService.
			fetchCPDVirtualSettingFileEntryByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpdVirtualSettingFileEntryLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the cpd virtual setting file entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @return the range of cpd virtual setting file entries
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.product.type.virtual.model.
			CPDVirtualSettingFileEntry> getCPDVirtualSettingFileEntries(
				int start, int end) {

		return _cpdVirtualSettingFileEntryLocalService.
			getCPDVirtualSettingFileEntries(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.product.type.virtual.model.
			CPDVirtualSettingFileEntry> getCPDVirtualSettingFileEntries(
				long cpDefinitionVirtualSettingId) {

		return _cpdVirtualSettingFileEntryLocalService.
			getCPDVirtualSettingFileEntries(cpDefinitionVirtualSettingId);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.product.type.virtual.model.
			CPDVirtualSettingFileEntry> getCPDVirtualSettingFileEntries(
				long cpDefinitionVirtualSettingId, int start, int end) {

		return _cpdVirtualSettingFileEntryLocalService.
			getCPDVirtualSettingFileEntries(
				cpDefinitionVirtualSettingId, start, end);
	}

	/**
	 * Returns all the cpd virtual setting file entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the cpd virtual setting file entries
	 * @param companyId the primary key of the company
	 * @return the matching cpd virtual setting file entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.product.type.virtual.model.
			CPDVirtualSettingFileEntry>
				getCPDVirtualSettingFileEntriesByUuidAndCompanyId(
					String uuid, long companyId) {

		return _cpdVirtualSettingFileEntryLocalService.
			getCPDVirtualSettingFileEntriesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of cpd virtual setting file entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the cpd virtual setting file entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching cpd virtual setting file entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.product.type.virtual.model.
			CPDVirtualSettingFileEntry>
				getCPDVirtualSettingFileEntriesByUuidAndCompanyId(
					String uuid, long companyId, int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.product.type.virtual.model.
							CPDVirtualSettingFileEntry> orderByComparator) {

		return _cpdVirtualSettingFileEntryLocalService.
			getCPDVirtualSettingFileEntriesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cpd virtual setting file entries.
	 *
	 * @return the number of cpd virtual setting file entries
	 */
	@Override
	public int getCPDVirtualSettingFileEntriesCount() {
		return _cpdVirtualSettingFileEntryLocalService.
			getCPDVirtualSettingFileEntriesCount();
	}

	@Override
	public int getCPDVirtualSettingFileEntriesCount(
		long cpDefinitionVirtualSettingId) {

		return _cpdVirtualSettingFileEntryLocalService.
			getCPDVirtualSettingFileEntriesCount(cpDefinitionVirtualSettingId);
	}

	/**
	 * Returns the cpd virtual setting file entry with the primary key.
	 *
	 * @param CPDefinitionVirtualSettingFileEntryId the primary key of the cpd virtual setting file entry
	 * @return the cpd virtual setting file entry
	 * @throws PortalException if a cpd virtual setting file entry with the primary key could not be found
	 */
	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDVirtualSettingFileEntry getCPDVirtualSettingFileEntry(
					long CPDefinitionVirtualSettingFileEntryId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _cpdVirtualSettingFileEntryLocalService.
			getCPDVirtualSettingFileEntry(
				CPDefinitionVirtualSettingFileEntryId);
	}

	/**
	 * Returns the cpd virtual setting file entry matching the UUID and group.
	 *
	 * @param uuid the cpd virtual setting file entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cpd virtual setting file entry
	 * @throws PortalException if a matching cpd virtual setting file entry could not be found
	 */
	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDVirtualSettingFileEntry
					getCPDVirtualSettingFileEntryByUuidAndGroupId(
						String uuid, long groupId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _cpdVirtualSettingFileEntryLocalService.
			getCPDVirtualSettingFileEntryByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _cpdVirtualSettingFileEntryLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpdVirtualSettingFileEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpdVirtualSettingFileEntryLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpdVirtualSettingFileEntryLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the cpd virtual setting file entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDVirtualSettingFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpdVirtualSettingFileEntry the cpd virtual setting file entry
	 * @return the cpd virtual setting file entry that was updated
	 */
	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDVirtualSettingFileEntry updateCPDVirtualSettingFileEntry(
				com.liferay.commerce.product.type.virtual.model.
					CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry) {

		return _cpdVirtualSettingFileEntryLocalService.
			updateCPDVirtualSettingFileEntry(cpdVirtualSettingFileEntry);
	}

	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDVirtualSettingFileEntry updateCPDVirtualSettingFileEntry(
					long cpdVirtualSettingFileEntryId, long fileEntryId,
					String url, String version)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _cpdVirtualSettingFileEntryLocalService.
			updateCPDVirtualSettingFileEntry(
				cpdVirtualSettingFileEntryId, fileEntryId, url, version);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpdVirtualSettingFileEntryLocalService.getBasePersistence();
	}

	@Override
	public CPDVirtualSettingFileEntryLocalService getWrappedService() {
		return _cpdVirtualSettingFileEntryLocalService;
	}

	@Override
	public void setWrappedService(
		CPDVirtualSettingFileEntryLocalService
			cpdVirtualSettingFileEntryLocalService) {

		_cpdVirtualSettingFileEntryLocalService =
			cpdVirtualSettingFileEntryLocalService;
	}

	private CPDVirtualSettingFileEntryLocalService
		_cpdVirtualSettingFileEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:766260085