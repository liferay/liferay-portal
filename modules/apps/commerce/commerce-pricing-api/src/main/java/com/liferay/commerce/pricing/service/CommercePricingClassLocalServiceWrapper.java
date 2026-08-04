/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service;

import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CommercePricingClassLocalService}.
 *
 * @author Riccardo Alberti
 * @see CommercePricingClassLocalService
 * @generated
 */
public class CommercePricingClassLocalServiceWrapper
	implements CommercePricingClassLocalService,
			   ServiceWrapper<CommercePricingClassLocalService> {

	public CommercePricingClassLocalServiceWrapper() {
		this(null);
	}

	public CommercePricingClassLocalServiceWrapper(
		CommercePricingClassLocalService commercePricingClassLocalService) {

		_commercePricingClassLocalService = commercePricingClassLocalService;
	}

	/**
	 * Adds the commerce pricing class to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePricingClassLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePricingClass the commerce pricing class
	 * @return the commerce pricing class that was added
	 */
	@Override
	public CommercePricingClass addCommercePricingClass(
		CommercePricingClass commercePricingClass) {

		return _commercePricingClassLocalService.addCommercePricingClass(
			commercePricingClass);
	}

	@Override
	public CommercePricingClass addCommercePricingClass(
			long userId, java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassLocalService.addCommercePricingClass(
			userId, titleMap, descriptionMap, serviceContext);
	}

	@Override
	public CommercePricingClass addCommercePricingClass(
			String externalReferenceCode, long userId,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassLocalService.addCommercePricingClass(
			externalReferenceCode, userId, titleMap, descriptionMap,
			serviceContext);
	}

	@Override
	public CommercePricingClass addOrUpdateCommercePricingClass(
			String externalReferenceCode, long commercePricingClassId,
			long userId, java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassLocalService.
			addOrUpdateCommercePricingClass(
				externalReferenceCode, commercePricingClassId, userId, titleMap,
				descriptionMap, serviceContext);
	}

	/**
	 * Creates a new commerce pricing class with the primary key. Does not add the commerce pricing class to the database.
	 *
	 * @param commercePricingClassId the primary key for the new commerce pricing class
	 * @return the new commerce pricing class
	 */
	@Override
	public CommercePricingClass createCommercePricingClass(
		long commercePricingClassId) {

		return _commercePricingClassLocalService.createCommercePricingClass(
			commercePricingClassId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce pricing class from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePricingClassLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePricingClass the commerce pricing class
	 * @return the commerce pricing class that was removed
	 * @throws PortalException
	 */
	@Override
	public CommercePricingClass deleteCommercePricingClass(
			CommercePricingClass commercePricingClass)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassLocalService.deleteCommercePricingClass(
			commercePricingClass);
	}

	/**
	 * Deletes the commerce pricing class with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePricingClassLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePricingClassId the primary key of the commerce pricing class
	 * @return the commerce pricing class that was removed
	 * @throws PortalException if a commerce pricing class with the primary key could not be found
	 */
	@Override
	public CommercePricingClass deleteCommercePricingClass(
			long commercePricingClassId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassLocalService.deleteCommercePricingClass(
			commercePricingClassId);
	}

	@Override
	public void deleteCommercePricingClasses(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePricingClassLocalService.deleteCommercePricingClasses(
			companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commercePricingClassLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commercePricingClassLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commercePricingClassLocalService.dynamicQuery();
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

		return _commercePricingClassLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.pricing.model.impl.CommercePricingClassModelImpl</code>.
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

		return _commercePricingClassLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.pricing.model.impl.CommercePricingClassModelImpl</code>.
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

		return _commercePricingClassLocalService.dynamicQuery(
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

		return _commercePricingClassLocalService.dynamicQueryCount(
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

		return _commercePricingClassLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CommercePricingClass fetchCommercePricingClass(
		long commercePricingClassId) {

		return _commercePricingClassLocalService.fetchCommercePricingClass(
			commercePricingClassId);
	}

	@Override
	public CommercePricingClass
		fetchCommercePricingClassByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _commercePricingClassLocalService.
			fetchCommercePricingClassByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce pricing class with the matching UUID and company.
	 *
	 * @param uuid the commerce pricing class's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce pricing class, or <code>null</code> if a matching commerce pricing class could not be found
	 */
	@Override
	public CommercePricingClass fetchCommercePricingClassByUuidAndCompanyId(
		String uuid, long companyId) {

		return _commercePricingClassLocalService.
			fetchCommercePricingClassByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commercePricingClassLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce pricing class with the primary key.
	 *
	 * @param commercePricingClassId the primary key of the commerce pricing class
	 * @return the commerce pricing class
	 * @throws PortalException if a commerce pricing class with the primary key could not be found
	 */
	@Override
	public CommercePricingClass getCommercePricingClass(
			long commercePricingClassId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassLocalService.getCommercePricingClass(
			commercePricingClassId);
	}

	@Override
	public long[] getCommercePricingClassByCPDefinition(long cpDefinitionId) {
		return _commercePricingClassLocalService.
			getCommercePricingClassByCPDefinition(cpDefinitionId);
	}

	@Override
	public CommercePricingClass getCommercePricingClassByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassLocalService.
			getCommercePricingClassByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce pricing class with the matching UUID and company.
	 *
	 * @param uuid the commerce pricing class's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce pricing class
	 * @throws PortalException if a matching commerce pricing class could not be found
	 */
	@Override
	public CommercePricingClass getCommercePricingClassByUuidAndCompanyId(
			String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassLocalService.
			getCommercePricingClassByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public int getCommercePricingClassCountByCPDefinitionId(
		long cpDefinitionId, String title) {

		return _commercePricingClassLocalService.
			getCommercePricingClassCountByCPDefinitionId(cpDefinitionId, title);
	}

	/**
	 * Returns a range of all the commerce pricing classes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.pricing.model.impl.CommercePricingClassModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce pricing classes
	 * @param end the upper bound of the range of commerce pricing classes (not inclusive)
	 * @return the range of commerce pricing classes
	 */
	@Override
	public java.util.List<CommercePricingClass> getCommercePricingClasses(
		int start, int end) {

		return _commercePricingClassLocalService.getCommercePricingClasses(
			start, end);
	}

	@Override
	public java.util.List<CommercePricingClass> getCommercePricingClasses(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommercePricingClass>
			orderByComparator) {

		return _commercePricingClassLocalService.getCommercePricingClasses(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce pricing classes.
	 *
	 * @return the number of commerce pricing classes
	 */
	@Override
	public int getCommercePricingClassesCount() {
		return _commercePricingClassLocalService.
			getCommercePricingClassesCount();
	}

	@Override
	public int getCommercePricingClassesCount(long companyId) {
		return _commercePricingClassLocalService.getCommercePricingClassesCount(
			companyId);
	}

	@Override
	public int getCommercePricingClassesCount(
		long cpDefinitionId, String title) {

		return _commercePricingClassLocalService.getCommercePricingClassesCount(
			cpDefinitionId, title);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commercePricingClassLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commercePricingClassLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public CommercePricingClass getOrAddEmptyCommercePricingClass(
			String externalReferenceCode, long companyId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassLocalService.
			getOrAddEmptyCommercePricingClass(
				externalReferenceCode, companyId, userId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePricingClassLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public java.util.List<CommercePricingClass> searchByCPDefinitionId(
		long cpDefinitionId, String title, int start, int end) {

		return _commercePricingClassLocalService.searchByCPDefinitionId(
			cpDefinitionId, title, start, end);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommercePricingClass> searchCommercePricingClasses(
				long companyId, String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassLocalService.searchCommercePricingClasses(
			companyId, keywords, start, end, sort);
	}

	/**
	 * Updates the commerce pricing class in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePricingClassLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePricingClass the commerce pricing class
	 * @return the commerce pricing class that was updated
	 */
	@Override
	public CommercePricingClass updateCommercePricingClass(
		CommercePricingClass commercePricingClass) {

		return _commercePricingClassLocalService.updateCommercePricingClass(
			commercePricingClass);
	}

	@Override
	public CommercePricingClass updateCommercePricingClass(
			long commercePricingClassId, long userId,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassLocalService.updateCommercePricingClass(
			commercePricingClassId, userId, titleMap, descriptionMap,
			serviceContext);
	}

	@Override
	public CommercePricingClass updateCommercePricingClassExternalReferenceCode(
			String externalReferenceCode, long commercePricingClassId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassLocalService.
			updateCommercePricingClassExternalReferenceCode(
				externalReferenceCode, commercePricingClassId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commercePricingClassLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CommercePricingClass> getCTPersistence() {
		return _commercePricingClassLocalService.getCTPersistence();
	}

	@Override
	public Class<CommercePricingClass> getModelClass() {
		return _commercePricingClassLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CommercePricingClass>, R, E>
				updateUnsafeFunction)
		throws E {

		return _commercePricingClassLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CommercePricingClassLocalService getWrappedService() {
		return _commercePricingClassLocalService;
	}

	@Override
	public void setWrappedService(
		CommercePricingClassLocalService commercePricingClassLocalService) {

		_commercePricingClassLocalService = commercePricingClassLocalService;
	}

	private CommercePricingClassLocalService _commercePricingClassLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1301183075