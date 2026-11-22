/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.service.access.policy.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link SAPEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see SAPEntryLocalService
 * @generated
 */
public class SAPEntryLocalServiceWrapper
	implements SAPEntryLocalService, ServiceWrapper<SAPEntryLocalService> {

	public SAPEntryLocalServiceWrapper() {
		this(null);
	}

	public SAPEntryLocalServiceWrapper(
		SAPEntryLocalService sapEntryLocalService) {

		_sapEntryLocalService = sapEntryLocalService;
	}

	@Override
	public com.liferay.portal.security.service.access.policy.model.SAPEntry
			addSAPEntry(
				long userId, String allowedServiceSignatures,
				boolean defaultSAPEntry, boolean enabled, String name,
				java.util.Map<java.util.Locale, String> titleMap,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sapEntryLocalService.addSAPEntry(
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
	@Override
	public com.liferay.portal.security.service.access.policy.model.SAPEntry
		addSAPEntry(
			com.liferay.portal.security.service.access.policy.model.SAPEntry
				sapEntry) {

		return _sapEntryLocalService.addSAPEntry(sapEntry);
	}

	@Override
	public void checkSystemSAPEntries(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_sapEntryLocalService.checkSystemSAPEntries(companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sapEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new sap entry with the primary key. Does not add the sap entry to the database.
	 *
	 * @param sapEntryId the primary key for the new sap entry
	 * @return the new sap entry
	 */
	@Override
	public com.liferay.portal.security.service.access.policy.model.SAPEntry
		createSAPEntry(long sapEntryId) {

		return _sapEntryLocalService.createSAPEntry(sapEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sapEntryLocalService.deletePersistedModel(persistedModel);
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
	@Override
	public com.liferay.portal.security.service.access.policy.model.SAPEntry
			deleteSAPEntry(long sapEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sapEntryLocalService.deleteSAPEntry(sapEntryId);
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
	@Override
	public com.liferay.portal.security.service.access.policy.model.SAPEntry
			deleteSAPEntry(
				com.liferay.portal.security.service.access.policy.model.SAPEntry
					sapEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sapEntryLocalService.deleteSAPEntry(sapEntry);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _sapEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _sapEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _sapEntryLocalService.dynamicQuery();
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

		return _sapEntryLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _sapEntryLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _sapEntryLocalService.dynamicQuery(
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

		return _sapEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _sapEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.security.service.access.policy.model.SAPEntry
		fetchSAPEntry(long sapEntryId) {

		return _sapEntryLocalService.fetchSAPEntry(sapEntryId);
	}

	@Override
	public com.liferay.portal.security.service.access.policy.model.SAPEntry
		fetchSAPEntry(long companyId, String name) {

		return _sapEntryLocalService.fetchSAPEntry(companyId, name);
	}

	/**
	 * Returns the sap entry with the matching UUID and company.
	 *
	 * @param uuid the sap entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching sap entry, or <code>null</code> if a matching sap entry could not be found
	 */
	@Override
	public com.liferay.portal.security.service.access.policy.model.SAPEntry
		fetchSAPEntryByUuidAndCompanyId(String uuid, long companyId) {

		return _sapEntryLocalService.fetchSAPEntryByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _sapEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List
		<com.liferay.portal.security.service.access.policy.model.SAPEntry>
			getCompanySAPEntries(long companyId, int start, int end) {

		return _sapEntryLocalService.getCompanySAPEntries(
			companyId, start, end);
	}

	@Override
	public java.util.List
		<com.liferay.portal.security.service.access.policy.model.SAPEntry>
			getDefaultSAPEntries(long companyId, boolean defaultSAPEntry) {

		return _sapEntryLocalService.getDefaultSAPEntries(
			companyId, defaultSAPEntry);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _sapEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _sapEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _sapEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sapEntryLocalService.getPersistedModel(primaryKeyObj);
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
	@Override
	public java.util.List
		<com.liferay.portal.security.service.access.policy.model.SAPEntry>
			getSAPEntries(int start, int end) {

		return _sapEntryLocalService.getSAPEntries(start, end);
	}

	/**
	 * Returns the number of sap entries.
	 *
	 * @return the number of sap entries
	 */
	@Override
	public int getSAPEntriesCount() {
		return _sapEntryLocalService.getSAPEntriesCount();
	}

	/**
	 * Returns the sap entry with the primary key.
	 *
	 * @param sapEntryId the primary key of the sap entry
	 * @return the sap entry
	 * @throws PortalException if a sap entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.security.service.access.policy.model.SAPEntry
			getSAPEntry(long sapEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sapEntryLocalService.getSAPEntry(sapEntryId);
	}

	@Override
	public com.liferay.portal.security.service.access.policy.model.SAPEntry
			getSAPEntry(long companyId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sapEntryLocalService.getSAPEntry(companyId, name);
	}

	/**
	 * Returns the sap entry with the matching UUID and company.
	 *
	 * @param uuid the sap entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching sap entry
	 * @throws PortalException if a matching sap entry could not be found
	 */
	@Override
	public com.liferay.portal.security.service.access.policy.model.SAPEntry
			getSAPEntryByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sapEntryLocalService.getSAPEntryByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.security.service.access.policy.model.SAPEntry
			updateSAPEntry(
				long sapEntryId, String allowedServiceSignatures,
				boolean defaultSAPEntry, boolean enabled, String name,
				java.util.Map<java.util.Locale, String> titleMap,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sapEntryLocalService.updateSAPEntry(
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
	@Override
	public com.liferay.portal.security.service.access.policy.model.SAPEntry
		updateSAPEntry(
			com.liferay.portal.security.service.access.policy.model.SAPEntry
				sapEntry) {

		return _sapEntryLocalService.updateSAPEntry(sapEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _sapEntryLocalService.getBasePersistence();
	}

	@Override
	public SAPEntryLocalService getWrappedService() {
		return _sapEntryLocalService;
	}

	@Override
	public void setWrappedService(SAPEntryLocalService sapEntryLocalService) {
		_sapEntryLocalService = sapEntryLocalService;
	}

	private SAPEntryLocalService _sapEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2028804625