/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CPSpecificationOptionLocalService}.
 *
 * @author Marco Leo
 * @see CPSpecificationOptionLocalService
 * @generated
 */
public class CPSpecificationOptionLocalServiceWrapper
	implements CPSpecificationOptionLocalService,
			   ServiceWrapper<CPSpecificationOptionLocalService> {

	public CPSpecificationOptionLocalServiceWrapper() {
		this(null);
	}

	public CPSpecificationOptionLocalServiceWrapper(
		CPSpecificationOptionLocalService cpSpecificationOptionLocalService) {

		_cpSpecificationOptionLocalService = cpSpecificationOptionLocalService;
	}

	/**
	 * Adds the cp specification option to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPSpecificationOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpSpecificationOption the cp specification option
	 * @return the cp specification option that was added
	 */
	@Override
	public CPSpecificationOption addCPSpecificationOption(
		CPSpecificationOption cpSpecificationOption) {

		return _cpSpecificationOptionLocalService.addCPSpecificationOption(
			cpSpecificationOption);
	}

	@Override
	public CPSpecificationOption addCPSpecificationOption(
			String externalReferenceCode, long userId, long cpOptionCategoryId,
			long[] listTypeDefinitionIds,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean facetable, String key, double priority, boolean visible,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpSpecificationOptionLocalService.addCPSpecificationOption(
			externalReferenceCode, userId, cpOptionCategoryId,
			listTypeDefinitionIds, titleMap, descriptionMap, facetable, key,
			priority, visible, serviceContext);
	}

	/**
	 * Creates a new cp specification option with the primary key. Does not add the cp specification option to the database.
	 *
	 * @param CPSpecificationOptionId the primary key for the new cp specification option
	 * @return the new cp specification option
	 */
	@Override
	public CPSpecificationOption createCPSpecificationOption(
		long CPSpecificationOptionId) {

		return _cpSpecificationOptionLocalService.createCPSpecificationOption(
			CPSpecificationOptionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpSpecificationOptionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the cp specification option from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPSpecificationOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpSpecificationOption the cp specification option
	 * @return the cp specification option that was removed
	 * @throws PortalException
	 */
	@Override
	public CPSpecificationOption deleteCPSpecificationOption(
			CPSpecificationOption cpSpecificationOption)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpSpecificationOptionLocalService.deleteCPSpecificationOption(
			cpSpecificationOption);
	}

	/**
	 * Deletes the cp specification option with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPSpecificationOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPSpecificationOptionId the primary key of the cp specification option
	 * @return the cp specification option that was removed
	 * @throws PortalException if a cp specification option with the primary key could not be found
	 */
	@Override
	public CPSpecificationOption deleteCPSpecificationOption(
			long CPSpecificationOptionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpSpecificationOptionLocalService.deleteCPSpecificationOption(
			CPSpecificationOptionId);
	}

	@Override
	public void deleteCPSpecificationOptions(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpSpecificationOptionLocalService.deleteCPSpecificationOptions(
			companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpSpecificationOptionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpSpecificationOptionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpSpecificationOptionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpSpecificationOptionLocalService.dynamicQuery();
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

		return _cpSpecificationOptionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPSpecificationOptionModelImpl</code>.
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

		return _cpSpecificationOptionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPSpecificationOptionModelImpl</code>.
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

		return _cpSpecificationOptionLocalService.dynamicQuery(
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

		return _cpSpecificationOptionLocalService.dynamicQueryCount(
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

		return _cpSpecificationOptionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CPSpecificationOption fetchCPSpecificationOption(
		long CPSpecificationOptionId) {

		return _cpSpecificationOptionLocalService.fetchCPSpecificationOption(
			CPSpecificationOptionId);
	}

	@Override
	public CPSpecificationOption fetchCPSpecificationOption(
		long companyId, String key) {

		return _cpSpecificationOptionLocalService.fetchCPSpecificationOption(
			companyId, key);
	}

	@Override
	public CPSpecificationOption
		fetchCPSpecificationOptionByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _cpSpecificationOptionLocalService.
			fetchCPSpecificationOptionByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp specification option with the matching UUID and company.
	 *
	 * @param uuid the cp specification option's UUID
	 * @param companyId the primary key of the company
	 * @return the matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption fetchCPSpecificationOptionByUuidAndCompanyId(
		String uuid, long companyId) {

		return _cpSpecificationOptionLocalService.
			fetchCPSpecificationOptionByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpSpecificationOptionLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the cp specification option with the primary key.
	 *
	 * @param CPSpecificationOptionId the primary key of the cp specification option
	 * @return the cp specification option
	 * @throws PortalException if a cp specification option with the primary key could not be found
	 */
	@Override
	public CPSpecificationOption getCPSpecificationOption(
			long CPSpecificationOptionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpSpecificationOptionLocalService.getCPSpecificationOption(
			CPSpecificationOptionId);
	}

	@Override
	public CPSpecificationOption getCPSpecificationOption(
			long companyId, String key)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpSpecificationOptionLocalService.getCPSpecificationOption(
			companyId, key);
	}

	@Override
	public CPSpecificationOption
			getCPSpecificationOptionByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpSpecificationOptionLocalService.
			getCPSpecificationOptionByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp specification option with the matching UUID and company.
	 *
	 * @param uuid the cp specification option's UUID
	 * @param companyId the primary key of the company
	 * @return the matching cp specification option
	 * @throws PortalException if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption getCPSpecificationOptionByUuidAndCompanyId(
			String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpSpecificationOptionLocalService.
			getCPSpecificationOptionByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the cp specification options.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @return the range of cp specification options
	 */
	@Override
	public java.util.List<CPSpecificationOption> getCPSpecificationOptions(
		int start, int end) {

		return _cpSpecificationOptionLocalService.getCPSpecificationOptions(
			start, end);
	}

	/**
	 * Returns the number of cp specification options.
	 *
	 * @return the number of cp specification options
	 */
	@Override
	public int getCPSpecificationOptionsCount() {
		return _cpSpecificationOptionLocalService.
			getCPSpecificationOptionsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _cpSpecificationOptionLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpSpecificationOptionLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpSpecificationOptionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpSpecificationOptionLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPSpecificationOption> searchCPSpecificationOptions(
				long companyId, Boolean facetable, Boolean visible,
				String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _cpSpecificationOptionLocalService.searchCPSpecificationOptions(
			companyId, facetable, visible, keywords, start, end, sort);
	}

	@Override
	public CPSpecificationOption updateCPOptionCategoryId(
			long cpSpecificationOptionId, long cpOptionCategoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpSpecificationOptionLocalService.updateCPOptionCategoryId(
			cpSpecificationOptionId, cpOptionCategoryId);
	}

	/**
	 * Updates the cp specification option in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPSpecificationOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpSpecificationOption the cp specification option
	 * @return the cp specification option that was updated
	 */
	@Override
	public CPSpecificationOption updateCPSpecificationOption(
		CPSpecificationOption cpSpecificationOption) {

		return _cpSpecificationOptionLocalService.updateCPSpecificationOption(
			cpSpecificationOption);
	}

	@Override
	public CPSpecificationOption updateCPSpecificationOption(
			String externalReferenceCode, long cpSpecificationOptionId,
			long cpOptionCategoryId, long[] listTypeDefinitionIds,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean facetable, String key, double priority, boolean visible,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpSpecificationOptionLocalService.updateCPSpecificationOption(
			externalReferenceCode, cpSpecificationOptionId, cpOptionCategoryId,
			listTypeDefinitionIds, titleMap, descriptionMap, facetable, key,
			priority, visible, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpSpecificationOptionLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CPSpecificationOption> getCTPersistence() {
		return _cpSpecificationOptionLocalService.getCTPersistence();
	}

	@Override
	public Class<CPSpecificationOption> getModelClass() {
		return _cpSpecificationOptionLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CPSpecificationOption>, R, E>
				updateUnsafeFunction)
		throws E {

		return _cpSpecificationOptionLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CPSpecificationOptionLocalService getWrappedService() {
		return _cpSpecificationOptionLocalService;
	}

	@Override
	public void setWrappedService(
		CPSpecificationOptionLocalService cpSpecificationOptionLocalService) {

		_cpSpecificationOptionLocalService = cpSpecificationOptionLocalService;
	}

	private CPSpecificationOptionLocalService
		_cpSpecificationOptionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1029373219