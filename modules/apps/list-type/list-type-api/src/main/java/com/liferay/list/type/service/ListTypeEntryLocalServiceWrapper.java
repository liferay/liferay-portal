/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ListTypeEntryLocalService}.
 *
 * @author Gabriel Albuquerque
 * @see ListTypeEntryLocalService
 * @generated
 */
public class ListTypeEntryLocalServiceWrapper
	implements ListTypeEntryLocalService,
			   ServiceWrapper<ListTypeEntryLocalService> {

	public ListTypeEntryLocalServiceWrapper() {
		this(null);
	}

	public ListTypeEntryLocalServiceWrapper(
		ListTypeEntryLocalService listTypeEntryLocalService) {

		_listTypeEntryLocalService = listTypeEntryLocalService;
	}

	/**
	 * Adds the list type entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ListTypeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param listTypeEntry the list type entry
	 * @return the list type entry that was added
	 */
	@Override
	public com.liferay.list.type.model.ListTypeEntry addListTypeEntry(
		com.liferay.list.type.model.ListTypeEntry listTypeEntry) {

		return _listTypeEntryLocalService.addListTypeEntry(listTypeEntry);
	}

	@Override
	public com.liferay.list.type.model.ListTypeEntry addListTypeEntry(
			String externalReferenceCode, long userId,
			long listTypeDefinitionId, String key,
			java.util.Map<java.util.Locale, String> nameMap, boolean system)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryLocalService.addListTypeEntry(
			externalReferenceCode, userId, listTypeDefinitionId, key, nameMap,
			system);
	}

	/**
	 * Creates a new list type entry with the primary key. Does not add the list type entry to the database.
	 *
	 * @param listTypeEntryId the primary key for the new list type entry
	 * @return the new list type entry
	 */
	@Override
	public com.liferay.list.type.model.ListTypeEntry createListTypeEntry(
		long listTypeEntryId) {

		return _listTypeEntryLocalService.createListTypeEntry(listTypeEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the list type entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ListTypeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param listTypeEntry the list type entry
	 * @return the list type entry that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.list.type.model.ListTypeEntry deleteListTypeEntry(
			com.liferay.list.type.model.ListTypeEntry listTypeEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryLocalService.deleteListTypeEntry(listTypeEntry);
	}

	/**
	 * Deletes the list type entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ListTypeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param listTypeEntryId the primary key of the list type entry
	 * @return the list type entry that was removed
	 * @throws PortalException if a list type entry with the primary key could not be found
	 */
	@Override
	public com.liferay.list.type.model.ListTypeEntry deleteListTypeEntry(
			long listTypeEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryLocalService.deleteListTypeEntry(listTypeEntryId);
	}

	@Override
	public void deleteListTypeEntryByListTypeDefinitionId(
			long listTypeDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_listTypeEntryLocalService.deleteListTypeEntryByListTypeDefinitionId(
			listTypeDefinitionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _listTypeEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _listTypeEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _listTypeEntryLocalService.dynamicQuery();
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

		return _listTypeEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.list.type.model.impl.ListTypeEntryModelImpl</code>.
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

		return _listTypeEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.list.type.model.impl.ListTypeEntryModelImpl</code>.
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

		return _listTypeEntryLocalService.dynamicQuery(
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

		return _listTypeEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _listTypeEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.list.type.model.ListTypeEntry fetchListTypeEntry(
		long listTypeEntryId) {

		return _listTypeEntryLocalService.fetchListTypeEntry(listTypeEntryId);
	}

	@Override
	public com.liferay.list.type.model.ListTypeEntry fetchListTypeEntry(
		long listTypeDefinitionId, String key) {

		return _listTypeEntryLocalService.fetchListTypeEntry(
			listTypeDefinitionId, key);
	}

	@Override
	public com.liferay.list.type.model.ListTypeEntry
		fetchListTypeEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId,
			long listTypeDefinitionId) {

		return _listTypeEntryLocalService.
			fetchListTypeEntryByExternalReferenceCode(
				externalReferenceCode, companyId, listTypeDefinitionId);
	}

	/**
	 * Returns the list type entry with the matching UUID and company.
	 *
	 * @param uuid the list type entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching list type entry, or <code>null</code> if a matching list type entry could not be found
	 */
	@Override
	public com.liferay.list.type.model.ListTypeEntry
		fetchListTypeEntryByUuidAndCompanyId(String uuid, long companyId) {

		return _listTypeEntryLocalService.fetchListTypeEntryByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _listTypeEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _listTypeEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _listTypeEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the list type entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.list.type.model.impl.ListTypeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of list type entries
	 * @param end the upper bound of the range of list type entries (not inclusive)
	 * @return the range of list type entries
	 */
	@Override
	public java.util.List<com.liferay.list.type.model.ListTypeEntry>
		getListTypeEntries(int start, int end) {

		return _listTypeEntryLocalService.getListTypeEntries(start, end);
	}

	@Override
	public java.util.List<com.liferay.list.type.model.ListTypeEntry>
		getListTypeEntries(long listTypeDefinitionId) {

		return _listTypeEntryLocalService.getListTypeEntries(
			listTypeDefinitionId);
	}

	@Override
	public java.util.List<com.liferay.list.type.model.ListTypeEntry>
		getListTypeEntries(long listTypeDefinitionId, int start, int end) {

		return _listTypeEntryLocalService.getListTypeEntries(
			listTypeDefinitionId, start, end);
	}

	@Override
	public java.util.List<com.liferay.list.type.model.ListTypeEntry>
		getListTypeEntries(
			long listTypeDefinitionId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.list.type.model.ListTypeEntry> orderByComparator) {

		return _listTypeEntryLocalService.getListTypeEntries(
			listTypeDefinitionId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.list.type.model.ListTypeEntry>
		getListTypeEntries(long[] listTypeDefinitionIds) {

		return _listTypeEntryLocalService.getListTypeEntries(
			listTypeDefinitionIds);
	}

	/**
	 * Returns the number of list type entries.
	 *
	 * @return the number of list type entries
	 */
	@Override
	public int getListTypeEntriesCount() {
		return _listTypeEntryLocalService.getListTypeEntriesCount();
	}

	@Override
	public int getListTypeEntriesCount(long listTypeDefinitionId) {
		return _listTypeEntryLocalService.getListTypeEntriesCount(
			listTypeDefinitionId);
	}

	/**
	 * Returns the list type entry with the primary key.
	 *
	 * @param listTypeEntryId the primary key of the list type entry
	 * @return the list type entry
	 * @throws PortalException if a list type entry with the primary key could not be found
	 */
	@Override
	public com.liferay.list.type.model.ListTypeEntry getListTypeEntry(
			long listTypeEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryLocalService.getListTypeEntry(listTypeEntryId);
	}

	@Override
	public com.liferay.list.type.model.ListTypeEntry getListTypeEntry(
			long listTypeDefinitionId, String key)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryLocalService.getListTypeEntry(
			listTypeDefinitionId, key);
	}

	@Override
	public com.liferay.list.type.model.ListTypeEntry
			getListTypeEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId,
				long listTypeDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryLocalService.
			getListTypeEntryByExternalReferenceCode(
				externalReferenceCode, companyId, listTypeDefinitionId);
	}

	/**
	 * Returns the list type entry with the matching UUID and company.
	 *
	 * @param uuid the list type entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching list type entry
	 * @throws PortalException if a matching list type entry could not be found
	 */
	@Override
	public com.liferay.list.type.model.ListTypeEntry
			getListTypeEntryByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryLocalService.getListTypeEntryByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.list.type.model.ListTypeEntry getOrAddEmptyListTypeEntry(
			long userId, long listTypeDefinitionId, String key)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryLocalService.getOrAddEmptyListTypeEntry(
			userId, listTypeDefinitionId, key);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _listTypeEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the list type entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ListTypeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param listTypeEntry the list type entry
	 * @return the list type entry that was updated
	 */
	@Override
	public com.liferay.list.type.model.ListTypeEntry updateListTypeEntry(
		com.liferay.list.type.model.ListTypeEntry listTypeEntry) {

		return _listTypeEntryLocalService.updateListTypeEntry(listTypeEntry);
	}

	@Override
	public com.liferay.list.type.model.ListTypeEntry updateListTypeEntry(
			String externalReferenceCode, long listTypeEntryId,
			java.util.Map<java.util.Locale, String> nameMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryLocalService.updateListTypeEntry(
			externalReferenceCode, listTypeEntryId, nameMap);
	}

	@Override
	public void updateUserId(long companyId, long oldUserId, long newUserId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_listTypeEntryLocalService.updateUserId(
			companyId, oldUserId, newUserId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _listTypeEntryLocalService.getBasePersistence();
	}

	@Override
	public ListTypeEntryLocalService getWrappedService() {
		return _listTypeEntryLocalService;
	}

	@Override
	public void setWrappedService(
		ListTypeEntryLocalService listTypeEntryLocalService) {

		_listTypeEntryLocalService = listTypeEntryLocalService;
	}

	private ListTypeEntryLocalService _listTypeEntryLocalService;

}