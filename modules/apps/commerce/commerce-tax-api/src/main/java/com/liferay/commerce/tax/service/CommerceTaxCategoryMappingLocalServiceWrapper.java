/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceTaxCategoryMappingLocalService}.
 *
 * @author Marco Leo
 * @see CommerceTaxCategoryMappingLocalService
 * @generated
 */
public class CommerceTaxCategoryMappingLocalServiceWrapper
	implements CommerceTaxCategoryMappingLocalService,
			   ServiceWrapper<CommerceTaxCategoryMappingLocalService> {

	public CommerceTaxCategoryMappingLocalServiceWrapper() {
		this(null);
	}

	public CommerceTaxCategoryMappingLocalServiceWrapper(
		CommerceTaxCategoryMappingLocalService
			commerceTaxCategoryMappingLocalService) {

		_commerceTaxCategoryMappingLocalService =
			commerceTaxCategoryMappingLocalService;
	}

	/**
	 * Adds the commerce tax category mapping to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxCategoryMappingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxCategoryMapping the commerce tax category mapping
	 * @return the commerce tax category mapping that was added
	 */
	@Override
	public com.liferay.commerce.tax.model.CommerceTaxCategoryMapping
		addCommerceTaxCategoryMapping(
			com.liferay.commerce.tax.model.CommerceTaxCategoryMapping
				commerceTaxCategoryMapping) {

		return _commerceTaxCategoryMappingLocalService.
			addCommerceTaxCategoryMapping(commerceTaxCategoryMapping);
	}

	@Override
	public com.liferay.commerce.tax.model.CommerceTaxCategoryMapping
			addCommerceTaxCategoryMapping(
				String externalReferenceCode, long userId, long groupId,
				long commerceTaxMethodId, long cpTaxCategoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxCategoryMappingLocalService.
			addCommerceTaxCategoryMapping(
				externalReferenceCode, userId, groupId, commerceTaxMethodId,
				cpTaxCategoryId);
	}

	/**
	 * Creates a new commerce tax category mapping with the primary key. Does not add the commerce tax category mapping to the database.
	 *
	 * @param commerceTaxCategoryMappingId the primary key for the new commerce tax category mapping
	 * @return the new commerce tax category mapping
	 */
	@Override
	public com.liferay.commerce.tax.model.CommerceTaxCategoryMapping
		createCommerceTaxCategoryMapping(long commerceTaxCategoryMappingId) {

		return _commerceTaxCategoryMappingLocalService.
			createCommerceTaxCategoryMapping(commerceTaxCategoryMappingId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxCategoryMappingLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce tax category mapping from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxCategoryMappingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxCategoryMapping the commerce tax category mapping
	 * @return the commerce tax category mapping that was removed
	 */
	@Override
	public com.liferay.commerce.tax.model.CommerceTaxCategoryMapping
		deleteCommerceTaxCategoryMapping(
			com.liferay.commerce.tax.model.CommerceTaxCategoryMapping
				commerceTaxCategoryMapping) {

		return _commerceTaxCategoryMappingLocalService.
			deleteCommerceTaxCategoryMapping(commerceTaxCategoryMapping);
	}

	/**
	 * Deletes the commerce tax category mapping with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxCategoryMappingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxCategoryMappingId the primary key of the commerce tax category mapping
	 * @return the commerce tax category mapping that was removed
	 * @throws PortalException if a commerce tax category mapping with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.tax.model.CommerceTaxCategoryMapping
			deleteCommerceTaxCategoryMapping(long commerceTaxCategoryMappingId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxCategoryMappingLocalService.
			deleteCommerceTaxCategoryMapping(commerceTaxCategoryMappingId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxCategoryMappingLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceTaxCategoryMappingLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceTaxCategoryMappingLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceTaxCategoryMappingLocalService.dynamicQuery();
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

		return _commerceTaxCategoryMappingLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.model.impl.CommerceTaxCategoryMappingModelImpl</code>.
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

		return _commerceTaxCategoryMappingLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.model.impl.CommerceTaxCategoryMappingModelImpl</code>.
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

		return _commerceTaxCategoryMappingLocalService.dynamicQuery(
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

		return _commerceTaxCategoryMappingLocalService.dynamicQueryCount(
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

		return _commerceTaxCategoryMappingLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.tax.model.CommerceTaxCategoryMapping
		fetchCommerceTaxCategoryMapping(long commerceTaxCategoryMappingId) {

		return _commerceTaxCategoryMappingLocalService.
			fetchCommerceTaxCategoryMapping(commerceTaxCategoryMappingId);
	}

	@Override
	public com.liferay.commerce.tax.model.CommerceTaxCategoryMapping
		fetchCommerceTaxCategoryMapping(
			long commerceTaxMethodId, long cpTaxCategoryId) {

		return _commerceTaxCategoryMappingLocalService.
			fetchCommerceTaxCategoryMapping(
				commerceTaxMethodId, cpTaxCategoryId);
	}

	@Override
	public com.liferay.commerce.tax.model.CommerceTaxCategoryMapping
		fetchCommerceTaxCategoryMappingByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _commerceTaxCategoryMappingLocalService.
			fetchCommerceTaxCategoryMappingByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce tax category mapping matching the UUID and group.
	 *
	 * @param uuid the commerce tax category mapping's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce tax category mapping, or <code>null</code> if a matching commerce tax category mapping could not be found
	 */
	@Override
	public com.liferay.commerce.tax.model.CommerceTaxCategoryMapping
		fetchCommerceTaxCategoryMappingByUuidAndGroupId(
			String uuid, long groupId) {

		return _commerceTaxCategoryMappingLocalService.
			fetchCommerceTaxCategoryMappingByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceTaxCategoryMappingLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce tax category mapping with the primary key.
	 *
	 * @param commerceTaxCategoryMappingId the primary key of the commerce tax category mapping
	 * @return the commerce tax category mapping
	 * @throws PortalException if a commerce tax category mapping with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.tax.model.CommerceTaxCategoryMapping
			getCommerceTaxCategoryMapping(long commerceTaxCategoryMappingId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxCategoryMappingLocalService.
			getCommerceTaxCategoryMapping(commerceTaxCategoryMappingId);
	}

	@Override
	public com.liferay.commerce.tax.model.CommerceTaxCategoryMapping
			getCommerceTaxCategoryMappingByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxCategoryMappingLocalService.
			getCommerceTaxCategoryMappingByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce tax category mapping matching the UUID and group.
	 *
	 * @param uuid the commerce tax category mapping's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce tax category mapping
	 * @throws PortalException if a matching commerce tax category mapping could not be found
	 */
	@Override
	public com.liferay.commerce.tax.model.CommerceTaxCategoryMapping
			getCommerceTaxCategoryMappingByUuidAndGroupId(
				String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxCategoryMappingLocalService.
			getCommerceTaxCategoryMappingByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the commerce tax category mappings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.model.impl.CommerceTaxCategoryMappingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce tax category mappings
	 * @param end the upper bound of the range of commerce tax category mappings (not inclusive)
	 * @return the range of commerce tax category mappings
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.tax.model.CommerceTaxCategoryMapping>
			getCommerceTaxCategoryMappings(int start, int end) {

		return _commerceTaxCategoryMappingLocalService.
			getCommerceTaxCategoryMappings(start, end);
	}

	/**
	 * Returns all the commerce tax category mappings matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce tax category mappings
	 * @param companyId the primary key of the company
	 * @return the matching commerce tax category mappings, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.tax.model.CommerceTaxCategoryMapping>
			getCommerceTaxCategoryMappingsByUuidAndCompanyId(
				String uuid, long companyId) {

		return _commerceTaxCategoryMappingLocalService.
			getCommerceTaxCategoryMappingsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of commerce tax category mappings matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce tax category mappings
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of commerce tax category mappings
	 * @param end the upper bound of the range of commerce tax category mappings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching commerce tax category mappings, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.tax.model.CommerceTaxCategoryMapping>
			getCommerceTaxCategoryMappingsByUuidAndCompanyId(
				String uuid, long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.tax.model.CommerceTaxCategoryMapping>
						orderByComparator) {

		return _commerceTaxCategoryMappingLocalService.
			getCommerceTaxCategoryMappingsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce tax category mappings.
	 *
	 * @return the number of commerce tax category mappings
	 */
	@Override
	public int getCommerceTaxCategoryMappingsCount() {
		return _commerceTaxCategoryMappingLocalService.
			getCommerceTaxCategoryMappingsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceTaxCategoryMappingLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceTaxCategoryMappingLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceTaxCategoryMappingLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxCategoryMappingLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the commerce tax category mapping in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxCategoryMappingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxCategoryMapping the commerce tax category mapping
	 * @return the commerce tax category mapping that was updated
	 */
	@Override
	public com.liferay.commerce.tax.model.CommerceTaxCategoryMapping
		updateCommerceTaxCategoryMapping(
			com.liferay.commerce.tax.model.CommerceTaxCategoryMapping
				commerceTaxCategoryMapping) {

		return _commerceTaxCategoryMappingLocalService.
			updateCommerceTaxCategoryMapping(commerceTaxCategoryMapping);
	}

	@Override
	public com.liferay.commerce.tax.model.CommerceTaxCategoryMapping
			updateExternalReferenceCode(
				long commerceTaxCategoryMappingId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxCategoryMappingLocalService.
			updateExternalReferenceCode(
				commerceTaxCategoryMappingId, externalReferenceCode);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceTaxCategoryMappingLocalService.getBasePersistence();
	}

	@Override
	public CommerceTaxCategoryMappingLocalService getWrappedService() {
		return _commerceTaxCategoryMappingLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceTaxCategoryMappingLocalService
			commerceTaxCategoryMappingLocalService) {

		_commerceTaxCategoryMappingLocalService =
			commerceTaxCategoryMappingLocalService;
	}

	private CommerceTaxCategoryMappingLocalService
		_commerceTaxCategoryMappingLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1301536565