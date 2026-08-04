/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CPOptionCategoryLocalService}.
 *
 * @author Marco Leo
 * @see CPOptionCategoryLocalService
 * @generated
 */
public class CPOptionCategoryLocalServiceWrapper
	implements CPOptionCategoryLocalService,
			   ServiceWrapper<CPOptionCategoryLocalService> {

	public CPOptionCategoryLocalServiceWrapper() {
		this(null);
	}

	public CPOptionCategoryLocalServiceWrapper(
		CPOptionCategoryLocalService cpOptionCategoryLocalService) {

		_cpOptionCategoryLocalService = cpOptionCategoryLocalService;
	}

	/**
	 * Adds the cp option category to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpOptionCategory the cp option category
	 * @return the cp option category that was added
	 */
	@Override
	public CPOptionCategory addCPOptionCategory(
		CPOptionCategory cpOptionCategory) {

		return _cpOptionCategoryLocalService.addCPOptionCategory(
			cpOptionCategory);
	}

	@Override
	public CPOptionCategory addCPOptionCategory(
			String externalReferenceCode, long userId,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			double priority, String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryLocalService.addCPOptionCategory(
			externalReferenceCode, userId, titleMap, descriptionMap, priority,
			key, serviceContext);
	}

	@Override
	public CPOptionCategory addOrUpdateCPOptionCategory(
			String externalReferenceCode, long userId, long cpOptionCategoryId,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			double priority, String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryLocalService.addOrUpdateCPOptionCategory(
			externalReferenceCode, userId, cpOptionCategoryId, titleMap,
			descriptionMap, priority, key, serviceContext);
	}

	/**
	 * Creates a new cp option category with the primary key. Does not add the cp option category to the database.
	 *
	 * @param CPOptionCategoryId the primary key for the new cp option category
	 * @return the new cp option category
	 */
	@Override
	public CPOptionCategory createCPOptionCategory(long CPOptionCategoryId) {
		return _cpOptionCategoryLocalService.createCPOptionCategory(
			CPOptionCategoryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteCPOptionCategories(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpOptionCategoryLocalService.deleteCPOptionCategories(companyId);
	}

	/**
	 * Deletes the cp option category from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpOptionCategory the cp option category
	 * @return the cp option category that was removed
	 * @throws PortalException
	 */
	@Override
	public CPOptionCategory deleteCPOptionCategory(
			CPOptionCategory cpOptionCategory)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryLocalService.deleteCPOptionCategory(
			cpOptionCategory);
	}

	/**
	 * Deletes the cp option category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPOptionCategoryId the primary key of the cp option category
	 * @return the cp option category that was removed
	 * @throws PortalException if a cp option category with the primary key could not be found
	 */
	@Override
	public CPOptionCategory deleteCPOptionCategory(long CPOptionCategoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryLocalService.deleteCPOptionCategory(
			CPOptionCategoryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpOptionCategoryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpOptionCategoryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpOptionCategoryLocalService.dynamicQuery();
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

		return _cpOptionCategoryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionCategoryModelImpl</code>.
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

		return _cpOptionCategoryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionCategoryModelImpl</code>.
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

		return _cpOptionCategoryLocalService.dynamicQuery(
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

		return _cpOptionCategoryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _cpOptionCategoryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CPOptionCategory fetchCPOptionCategory(long CPOptionCategoryId) {
		return _cpOptionCategoryLocalService.fetchCPOptionCategory(
			CPOptionCategoryId);
	}

	@Override
	public CPOptionCategory fetchCPOptionCategory(long companyId, String key) {
		return _cpOptionCategoryLocalService.fetchCPOptionCategory(
			companyId, key);
	}

	@Override
	public CPOptionCategory fetchCPOptionCategoryByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _cpOptionCategoryLocalService.
			fetchCPOptionCategoryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp option category with the matching UUID and company.
	 *
	 * @param uuid the cp option category's UUID
	 * @param companyId the primary key of the company
	 * @return the matching cp option category, or <code>null</code> if a matching cp option category could not be found
	 */
	@Override
	public CPOptionCategory fetchCPOptionCategoryByUuidAndCompanyId(
		String uuid, long companyId) {

		return _cpOptionCategoryLocalService.
			fetchCPOptionCategoryByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpOptionCategoryLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the cp option categories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp option categories
	 * @param end the upper bound of the range of cp option categories (not inclusive)
	 * @return the range of cp option categories
	 */
	@Override
	public java.util.List<CPOptionCategory> getCPOptionCategories(
		int start, int end) {

		return _cpOptionCategoryLocalService.getCPOptionCategories(start, end);
	}

	@Override
	public java.util.List<CPOptionCategory> getCPOptionCategories(
		long companyId, int start, int end) {

		return _cpOptionCategoryLocalService.getCPOptionCategories(
			companyId, start, end);
	}

	/**
	 * Returns the number of cp option categories.
	 *
	 * @return the number of cp option categories
	 */
	@Override
	public int getCPOptionCategoriesCount() {
		return _cpOptionCategoryLocalService.getCPOptionCategoriesCount();
	}

	/**
	 * Returns the cp option category with the primary key.
	 *
	 * @param CPOptionCategoryId the primary key of the cp option category
	 * @return the cp option category
	 * @throws PortalException if a cp option category with the primary key could not be found
	 */
	@Override
	public CPOptionCategory getCPOptionCategory(long CPOptionCategoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryLocalService.getCPOptionCategory(
			CPOptionCategoryId);
	}

	@Override
	public CPOptionCategory getCPOptionCategory(long companyId, String key)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryLocalService.getCPOptionCategory(
			companyId, key);
	}

	@Override
	public CPOptionCategory getCPOptionCategoryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryLocalService.
			getCPOptionCategoryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp option category with the matching UUID and company.
	 *
	 * @param uuid the cp option category's UUID
	 * @param companyId the primary key of the company
	 * @return the matching cp option category
	 * @throws PortalException if a matching cp option category could not be found
	 */
	@Override
	public CPOptionCategory getCPOptionCategoryByUuidAndCompanyId(
			String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryLocalService.
			getCPOptionCategoryByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _cpOptionCategoryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpOptionCategoryLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public CPOptionCategory getOrAddEmptyCPOptionCategory(
			String externalReferenceCode, long companyId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryLocalService.getOrAddEmptyCPOptionCategory(
			externalReferenceCode, companyId, userId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpOptionCategoryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPOptionCategory> searchCPOptionCategories(
				long companyId, String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryLocalService.searchCPOptionCategories(
			companyId, keywords, start, end, sort);
	}

	/**
	 * Updates the cp option category in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpOptionCategory the cp option category
	 * @return the cp option category that was updated
	 */
	@Override
	public CPOptionCategory updateCPOptionCategory(
		CPOptionCategory cpOptionCategory) {

		return _cpOptionCategoryLocalService.updateCPOptionCategory(
			cpOptionCategory);
	}

	@Override
	public CPOptionCategory updateCPOptionCategory(
			String externalReferenceCode, long cpOptionCategoryId,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			double priority, String key)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryLocalService.updateCPOptionCategory(
			externalReferenceCode, cpOptionCategoryId, titleMap, descriptionMap,
			priority, key);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpOptionCategoryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CPOptionCategory> getCTPersistence() {
		return _cpOptionCategoryLocalService.getCTPersistence();
	}

	@Override
	public Class<CPOptionCategory> getModelClass() {
		return _cpOptionCategoryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CPOptionCategory>, R, E>
				updateUnsafeFunction)
		throws E {

		return _cpOptionCategoryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CPOptionCategoryLocalService getWrappedService() {
		return _cpOptionCategoryLocalService;
	}

	@Override
	public void setWrappedService(
		CPOptionCategoryLocalService cpOptionCategoryLocalService) {

		_cpOptionCategoryLocalService = cpOptionCategoryLocalService;
	}

	private CPOptionCategoryLocalService _cpOptionCategoryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:497840900