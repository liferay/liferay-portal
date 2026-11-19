/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CPDefinitionSpecificationOptionValueLocalService}.
 *
 * @author Marco Leo
 * @see CPDefinitionSpecificationOptionValueLocalService
 * @generated
 */
public class CPDefinitionSpecificationOptionValueLocalServiceWrapper
	implements CPDefinitionSpecificationOptionValueLocalService,
			   ServiceWrapper
				   <CPDefinitionSpecificationOptionValueLocalService> {

	public CPDefinitionSpecificationOptionValueLocalServiceWrapper() {
		this(null);
	}

	public CPDefinitionSpecificationOptionValueLocalServiceWrapper(
		CPDefinitionSpecificationOptionValueLocalService
			cpDefinitionSpecificationOptionValueLocalService) {

		_cpDefinitionSpecificationOptionValueLocalService =
			cpDefinitionSpecificationOptionValueLocalService;
	}

	/**
	 * Adds the cp definition specification option value to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionSpecificationOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionSpecificationOptionValue the cp definition specification option value
	 * @return the cp definition specification option value that was added
	 */
	@Override
	public CPDefinitionSpecificationOptionValue
		addCPDefinitionSpecificationOptionValue(
			CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue) {

		return _cpDefinitionSpecificationOptionValueLocalService.
			addCPDefinitionSpecificationOptionValue(
				cpDefinitionSpecificationOptionValue);
	}

	@Override
	public CPDefinitionSpecificationOptionValue
			addCPDefinitionSpecificationOptionValue(
				String externalReferenceCode, long cpDefinitionId,
				long cpSpecificationOptionId, long cpOptionCategoryId,
				double priority,
				java.util.Map<java.util.Locale, String> valueMap,
				boolean visible,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionSpecificationOptionValueLocalService.
			addCPDefinitionSpecificationOptionValue(
				externalReferenceCode, cpDefinitionId, cpSpecificationOptionId,
				cpOptionCategoryId, priority, valueMap, visible,
				serviceContext);
	}

	/**
	 * Creates a new cp definition specification option value with the primary key. Does not add the cp definition specification option value to the database.
	 *
	 * @param CPDefinitionSpecificationOptionValueId the primary key for the new cp definition specification option value
	 * @return the new cp definition specification option value
	 */
	@Override
	public CPDefinitionSpecificationOptionValue
		createCPDefinitionSpecificationOptionValue(
			long CPDefinitionSpecificationOptionValueId) {

		return _cpDefinitionSpecificationOptionValueLocalService.
			createCPDefinitionSpecificationOptionValue(
				CPDefinitionSpecificationOptionValueId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionSpecificationOptionValueLocalService.
			createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the cp definition specification option value from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionSpecificationOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionSpecificationOptionValue the cp definition specification option value
	 * @return the cp definition specification option value that was removed
	 * @throws PortalException
	 */
	@Override
	public CPDefinitionSpecificationOptionValue
			deleteCPDefinitionSpecificationOptionValue(
				CPDefinitionSpecificationOptionValue
					cpDefinitionSpecificationOptionValue)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionSpecificationOptionValueLocalService.
			deleteCPDefinitionSpecificationOptionValue(
				cpDefinitionSpecificationOptionValue);
	}

	@Override
	public CPDefinitionSpecificationOptionValue
			deleteCPDefinitionSpecificationOptionValue(
				CPDefinitionSpecificationOptionValue
					cpDefinitionSpecificationOptionValue,
				boolean makeCopy)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionSpecificationOptionValueLocalService.
			deleteCPDefinitionSpecificationOptionValue(
				cpDefinitionSpecificationOptionValue, makeCopy);
	}

	/**
	 * Deletes the cp definition specification option value with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionSpecificationOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPDefinitionSpecificationOptionValueId the primary key of the cp definition specification option value
	 * @return the cp definition specification option value that was removed
	 * @throws PortalException if a cp definition specification option value with the primary key could not be found
	 */
	@Override
	public CPDefinitionSpecificationOptionValue
			deleteCPDefinitionSpecificationOptionValue(
				long CPDefinitionSpecificationOptionValueId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionSpecificationOptionValueLocalService.
			deleteCPDefinitionSpecificationOptionValue(
				CPDefinitionSpecificationOptionValueId);
	}

	@Override
	public void deleteCPDefinitionSpecificationOptionValues(long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionSpecificationOptionValueLocalService.
			deleteCPDefinitionSpecificationOptionValues(cpDefinitionId);
	}

	@Override
	public void deleteCPDefinitionSpecificationOptionValues(
			long cpDefinitionId, boolean makeCopy)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionSpecificationOptionValueLocalService.
			deleteCPDefinitionSpecificationOptionValues(
				cpDefinitionId, makeCopy);
	}

	@Override
	public void deleteCPSpecificationOptionDefinitionValues(
			long cpSpecificationOptionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionSpecificationOptionValueLocalService.
			deleteCPSpecificationOptionDefinitionValues(
				cpSpecificationOptionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionSpecificationOptionValueLocalService.
			deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpDefinitionSpecificationOptionValueLocalService.dslQuery(
			dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpDefinitionSpecificationOptionValueLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpDefinitionSpecificationOptionValueLocalService.dynamicQuery();
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

		return _cpDefinitionSpecificationOptionValueLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
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

		return _cpDefinitionSpecificationOptionValueLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
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

		return _cpDefinitionSpecificationOptionValueLocalService.dynamicQuery(
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

		return _cpDefinitionSpecificationOptionValueLocalService.
			dynamicQueryCount(dynamicQuery);
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

		return _cpDefinitionSpecificationOptionValueLocalService.
			dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public CPDefinitionSpecificationOptionValue
		fetchCPDefinitionSpecificationOptionValue(
			long CPDefinitionSpecificationOptionValueId) {

		return _cpDefinitionSpecificationOptionValueLocalService.
			fetchCPDefinitionSpecificationOptionValue(
				CPDefinitionSpecificationOptionValueId);
	}

	@Override
	public CPDefinitionSpecificationOptionValue
		fetchCPDefinitionSpecificationOptionValue(
			long cpDefinitionId, long cpDefinitionSpecificationOptionValueId) {

		return _cpDefinitionSpecificationOptionValueLocalService.
			fetchCPDefinitionSpecificationOptionValue(
				cpDefinitionId, cpDefinitionSpecificationOptionValueId);
	}

	@Override
	public CPDefinitionSpecificationOptionValue
		fetchCPDefinitionSpecificationOptionValue(
			long cpDefinitionId, String key) {

		return _cpDefinitionSpecificationOptionValueLocalService.
			fetchCPDefinitionSpecificationOptionValue(cpDefinitionId, key);
	}

	@Override
	public CPDefinitionSpecificationOptionValue
		fetchCPDefinitionSpecificationOptionValueByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _cpDefinitionSpecificationOptionValueLocalService.
			fetchCPDefinitionSpecificationOptionValueByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp definition specification option value matching the UUID and group.
	 *
	 * @param uuid the cp definition specification option value's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition specification option value, or <code>null</code> if a matching cp definition specification option value could not be found
	 */
	@Override
	public CPDefinitionSpecificationOptionValue
		fetchCPDefinitionSpecificationOptionValueByUuidAndGroupId(
			String uuid, long groupId) {

		return _cpDefinitionSpecificationOptionValueLocalService.
			fetchCPDefinitionSpecificationOptionValueByUuidAndGroupId(
				uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpDefinitionSpecificationOptionValueLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the cp definition specification option value with the primary key.
	 *
	 * @param CPDefinitionSpecificationOptionValueId the primary key of the cp definition specification option value
	 * @return the cp definition specification option value
	 * @throws PortalException if a cp definition specification option value with the primary key could not be found
	 */
	@Override
	public CPDefinitionSpecificationOptionValue
			getCPDefinitionSpecificationOptionValue(
				long CPDefinitionSpecificationOptionValueId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionSpecificationOptionValueLocalService.
			getCPDefinitionSpecificationOptionValue(
				CPDefinitionSpecificationOptionValueId);
	}

	@Override
	public CPDefinitionSpecificationOptionValue
			getCPDefinitionSpecificationOptionValueByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionSpecificationOptionValueLocalService.
			getCPDefinitionSpecificationOptionValueByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp definition specification option value matching the UUID and group.
	 *
	 * @param uuid the cp definition specification option value's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition specification option value
	 * @throws PortalException if a matching cp definition specification option value could not be found
	 */
	@Override
	public CPDefinitionSpecificationOptionValue
			getCPDefinitionSpecificationOptionValueByUuidAndGroupId(
				String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionSpecificationOptionValueLocalService.
			getCPDefinitionSpecificationOptionValueByUuidAndGroupId(
				uuid, groupId);
	}

	/**
	 * Returns a range of all the cp definition specification option values.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @return the range of cp definition specification option values
	 */
	@Override
	public java.util.List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues(int start, int end) {

		return _cpDefinitionSpecificationOptionValueLocalService.
			getCPDefinitionSpecificationOptionValues(start, end);
	}

	@Override
	public java.util.List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues(long cpSpecificationOptionId) {

		return _cpDefinitionSpecificationOptionValueLocalService.
			getCPDefinitionSpecificationOptionValues(cpSpecificationOptionId);
	}

	@Override
	public java.util.List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues(
			long cpDefinitionId, Boolean visible, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionSpecificationOptionValue> orderByComparator) {

		return _cpDefinitionSpecificationOptionValueLocalService.
			getCPDefinitionSpecificationOptionValues(
				cpDefinitionId, visible, start, end, orderByComparator);
	}

	@Override
	public java.util.List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues(
			long cpSpecificationOptionId, int start, int end) {

		return _cpDefinitionSpecificationOptionValueLocalService.
			getCPDefinitionSpecificationOptionValues(
				cpSpecificationOptionId, start, end);
	}

	@Override
	public java.util.List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues(
			long cpDefinitionId, long cpOptionCategoryId, Boolean visible) {

		return _cpDefinitionSpecificationOptionValueLocalService.
			getCPDefinitionSpecificationOptionValues(
				cpDefinitionId, cpOptionCategoryId, visible);
	}

	@Override
	public java.util.List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValuesByC_CSO(
			long cpDefinitionId, long cpSpecificationOptionId) {

		return _cpDefinitionSpecificationOptionValueLocalService.
			getCPDefinitionSpecificationOptionValuesByC_CSO(
				cpDefinitionId, cpSpecificationOptionId);
	}

	/**
	 * Returns all the cp definition specification option values matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition specification option values
	 * @param companyId the primary key of the company
	 * @return the matching cp definition specification option values, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValuesByUuidAndCompanyId(
			String uuid, long companyId) {

		return _cpDefinitionSpecificationOptionValueLocalService.
			getCPDefinitionSpecificationOptionValuesByUuidAndCompanyId(
				uuid, companyId);
	}

	/**
	 * Returns a range of cp definition specification option values matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition specification option values
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching cp definition specification option values, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValuesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionSpecificationOptionValue> orderByComparator) {

		return _cpDefinitionSpecificationOptionValueLocalService.
			getCPDefinitionSpecificationOptionValuesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp definition specification option values.
	 *
	 * @return the number of cp definition specification option values
	 */
	@Override
	public int getCPDefinitionSpecificationOptionValuesCount() {
		return _cpDefinitionSpecificationOptionValueLocalService.
			getCPDefinitionSpecificationOptionValuesCount();
	}

	@Override
	public int getCPDefinitionSpecificationOptionValuesCount(
		long cpDefinitionId, Boolean visible) {

		return _cpDefinitionSpecificationOptionValueLocalService.
			getCPDefinitionSpecificationOptionValuesCount(
				cpDefinitionId, visible);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _cpDefinitionSpecificationOptionValueLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpDefinitionSpecificationOptionValueLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpDefinitionSpecificationOptionValueLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionSpecificationOptionValueLocalService.
			getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the cp definition specification option value in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionSpecificationOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionSpecificationOptionValue the cp definition specification option value
	 * @return the cp definition specification option value that was updated
	 */
	@Override
	public CPDefinitionSpecificationOptionValue
		updateCPDefinitionSpecificationOptionValue(
			CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue) {

		return _cpDefinitionSpecificationOptionValueLocalService.
			updateCPDefinitionSpecificationOptionValue(
				cpDefinitionSpecificationOptionValue);
	}

	@Override
	public CPDefinitionSpecificationOptionValue
			updateCPDefinitionSpecificationOptionValue(
				String externalReferenceCode,
				long cpDefinitionSpecificationOptionValueId,
				long cpOptionCategoryId, String key, double priority,
				java.util.Map<java.util.Locale, String> valueMap,
				boolean visible,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionSpecificationOptionValueLocalService.
			updateCPDefinitionSpecificationOptionValue(
				externalReferenceCode, cpDefinitionSpecificationOptionValueId,
				cpOptionCategoryId, key, priority, valueMap, visible,
				serviceContext);
	}

	@Override
	public CPDefinitionSpecificationOptionValue updateCPOptionCategoryId(
			long cpDefinitionSpecificationOptionValueId,
			long cpOptionCategoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionSpecificationOptionValueLocalService.
			updateCPOptionCategoryId(
				cpDefinitionSpecificationOptionValueId, cpOptionCategoryId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpDefinitionSpecificationOptionValueLocalService.
			getBasePersistence();
	}

	@Override
	public CTPersistence<CPDefinitionSpecificationOptionValue>
		getCTPersistence() {

		return _cpDefinitionSpecificationOptionValueLocalService.
			getCTPersistence();
	}

	@Override
	public Class<CPDefinitionSpecificationOptionValue> getModelClass() {
		return _cpDefinitionSpecificationOptionValueLocalService.
			getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction
				<CTPersistence<CPDefinitionSpecificationOptionValue>, R, E>
					updateUnsafeFunction)
		throws E {

		return _cpDefinitionSpecificationOptionValueLocalService.
			updateWithUnsafeFunction(updateUnsafeFunction);
	}

	@Override
	public CPDefinitionSpecificationOptionValueLocalService
		getWrappedService() {

		return _cpDefinitionSpecificationOptionValueLocalService;
	}

	@Override
	public void setWrappedService(
		CPDefinitionSpecificationOptionValueLocalService
			cpDefinitionSpecificationOptionValueLocalService) {

		_cpDefinitionSpecificationOptionValueLocalService =
			cpDefinitionSpecificationOptionValueLocalService;
	}

	private CPDefinitionSpecificationOptionValueLocalService
		_cpDefinitionSpecificationOptionValueLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1630191880