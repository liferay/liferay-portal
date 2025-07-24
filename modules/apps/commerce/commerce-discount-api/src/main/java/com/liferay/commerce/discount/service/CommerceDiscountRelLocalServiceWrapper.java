/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceDiscountRelLocalService}.
 *
 * @author Marco Leo
 * @see CommerceDiscountRelLocalService
 * @generated
 */
public class CommerceDiscountRelLocalServiceWrapper
	implements CommerceDiscountRelLocalService,
			   ServiceWrapper<CommerceDiscountRelLocalService> {

	public CommerceDiscountRelLocalServiceWrapper() {
		this(null);
	}

	public CommerceDiscountRelLocalServiceWrapper(
		CommerceDiscountRelLocalService commerceDiscountRelLocalService) {

		_commerceDiscountRelLocalService = commerceDiscountRelLocalService;
	}

	/**
	 * Adds the commerce discount rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountRel the commerce discount rel
	 * @return the commerce discount rel that was added
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRel
		addCommerceDiscountRel(
			com.liferay.commerce.discount.model.CommerceDiscountRel
				commerceDiscountRel) {

		return _commerceDiscountRelLocalService.addCommerceDiscountRel(
			commerceDiscountRel);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRel
			addCommerceDiscountRel(
				long commerceDiscountId, String className, long classPK,
				com.liferay.portal.kernel.util.UnicodeProperties
					typeSettingsUnicodeProperties,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelLocalService.addCommerceDiscountRel(
			commerceDiscountId, className, classPK,
			typeSettingsUnicodeProperties, serviceContext);
	}

	/**
	 * Creates a new commerce discount rel with the primary key. Does not add the commerce discount rel to the database.
	 *
	 * @param commerceDiscountRelId the primary key for the new commerce discount rel
	 * @return the new commerce discount rel
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRel
		createCommerceDiscountRel(long commerceDiscountRelId) {

		return _commerceDiscountRelLocalService.createCommerceDiscountRel(
			commerceDiscountRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRel
			deleteCommerceDiscountRel(
				com.liferay.commerce.discount.model.CommerceDiscount
					commerceDiscount,
				com.liferay.commerce.discount.model.CommerceDiscountRel
					commerceDiscountRel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelLocalService.deleteCommerceDiscountRel(
			commerceDiscount, commerceDiscountRel);
	}

	/**
	 * Deletes the commerce discount rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountRel the commerce discount rel
	 * @return the commerce discount rel that was removed
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRel
		deleteCommerceDiscountRel(
			com.liferay.commerce.discount.model.CommerceDiscountRel
				commerceDiscountRel) {

		return _commerceDiscountRelLocalService.deleteCommerceDiscountRel(
			commerceDiscountRel);
	}

	/**
	 * Deletes the commerce discount rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountRelId the primary key of the commerce discount rel
	 * @return the commerce discount rel that was removed
	 * @throws PortalException if a commerce discount rel with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRel
			deleteCommerceDiscountRel(long commerceDiscountRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelLocalService.deleteCommerceDiscountRel(
			commerceDiscountRelId);
	}

	@Override
	public void deleteCommerceDiscountRels(
			com.liferay.commerce.discount.model.CommerceDiscount
				commerceDiscount)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceDiscountRelLocalService.deleteCommerceDiscountRels(
			commerceDiscount);
	}

	@Override
	public void deleteCommerceDiscountRels(String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceDiscountRelLocalService.deleteCommerceDiscountRels(
			className, classPK);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceDiscountRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceDiscountRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceDiscountRelLocalService.dynamicQuery();
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

		return _commerceDiscountRelLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountRelModelImpl</code>.
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

		return _commerceDiscountRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountRelModelImpl</code>.
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

		return _commerceDiscountRelLocalService.dynamicQuery(
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

		return _commerceDiscountRelLocalService.dynamicQueryCount(dynamicQuery);
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

		return _commerceDiscountRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRel
		fetchCommerceDiscountRel(long commerceDiscountRelId) {

		return _commerceDiscountRelLocalService.fetchCommerceDiscountRel(
			commerceDiscountRelId);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRel
		fetchCommerceDiscountRel(
			long commerceDiscountId, String className, long classPK) {

		return _commerceDiscountRelLocalService.fetchCommerceDiscountRel(
			commerceDiscountId, className, classPK);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRel
		fetchCommerceDiscountRel(String className, long classPK) {

		return _commerceDiscountRelLocalService.fetchCommerceDiscountRel(
			className, classPK);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceDiscountRelLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRel>
			getCategoriesByCommerceDiscountId(
				long commerceDiscountId, String name, int start, int end) {

		return _commerceDiscountRelLocalService.
			getCategoriesByCommerceDiscountId(
				commerceDiscountId, name, start, end);
	}

	@Override
	public int getCategoriesByCommerceDiscountIdCount(
		long commerceDiscountId, String name) {

		return _commerceDiscountRelLocalService.
			getCategoriesByCommerceDiscountIdCount(commerceDiscountId, name);
	}

	@Override
	public long[] getClassPKs(long commerceDiscountId, String className) {
		return _commerceDiscountRelLocalService.getClassPKs(
			commerceDiscountId, className);
	}

	/**
	 * Returns the commerce discount rel with the primary key.
	 *
	 * @param commerceDiscountRelId the primary key of the commerce discount rel
	 * @return the commerce discount rel
	 * @throws PortalException if a commerce discount rel with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRel
			getCommerceDiscountRel(long commerceDiscountRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelLocalService.getCommerceDiscountRel(
			commerceDiscountRelId);
	}

	/**
	 * Returns a range of all the commerce discount rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @return the range of commerce discount rels
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRel>
			getCommerceDiscountRels(int start, int end) {

		return _commerceDiscountRelLocalService.getCommerceDiscountRels(
			start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRel>
			getCommerceDiscountRels(long classNameId, long classPK) {

		return _commerceDiscountRelLocalService.getCommerceDiscountRels(
			classNameId, classPK);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRel>
			getCommerceDiscountRels(
				long classNameId, long classPK, String unitOfMeasureKey) {

		return _commerceDiscountRelLocalService.getCommerceDiscountRels(
			classNameId, classPK, unitOfMeasureKey);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRel>
			getCommerceDiscountRels(long commerceDiscountId, String className) {

		return _commerceDiscountRelLocalService.getCommerceDiscountRels(
			commerceDiscountId, className);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRel>
			getCommerceDiscountRels(
				long commerceDiscountId, String className, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.discount.model.CommerceDiscountRel>
						orderByComparator) {

		return _commerceDiscountRelLocalService.getCommerceDiscountRels(
			commerceDiscountId, className, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce discount rels.
	 *
	 * @return the number of commerce discount rels
	 */
	@Override
	public int getCommerceDiscountRelsCount() {
		return _commerceDiscountRelLocalService.getCommerceDiscountRelsCount();
	}

	@Override
	public int getCommerceDiscountRelsCount(
		long commerceDiscountId, String className) {

		return _commerceDiscountRelLocalService.getCommerceDiscountRelsCount(
			commerceDiscountId, className);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRel>
			getCommercePricingClassesByCommerceDiscountId(
				long commerceDiscountId, String title, int start, int end) {

		return _commerceDiscountRelLocalService.
			getCommercePricingClassesByCommerceDiscountId(
				commerceDiscountId, title, start, end);
	}

	@Override
	public int getCommercePricingClassesByCommerceDiscountIdCount(
		long commerceDiscountId, String title) {

		return _commerceDiscountRelLocalService.
			getCommercePricingClassesByCommerceDiscountIdCount(
				commerceDiscountId, title);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRel>
			getCPDefinitionsByCommerceDiscountId(
				long commerceDiscountId, String name, String languageId,
				int start, int end) {

		return _commerceDiscountRelLocalService.
			getCPDefinitionsByCommerceDiscountId(
				commerceDiscountId, name, languageId, start, end);
	}

	@Override
	public int getCPDefinitionsByCommerceDiscountIdCount(
		long commerceDiscountId, String name, String languageId) {

		return _commerceDiscountRelLocalService.
			getCPDefinitionsByCommerceDiscountIdCount(
				commerceDiscountId, name, languageId);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRel>
			getCPInstancesByCommerceDiscountId(
				long commerceDiscountId, String sku, int start, int end) {

		return _commerceDiscountRelLocalService.
			getCPInstancesByCommerceDiscountId(
				commerceDiscountId, sku, start, end);
	}

	@Override
	public int getCPInstancesByCommerceDiscountIdCount(
		long commerceDiscountId, String sku) {

		return _commerceDiscountRelLocalService.
			getCPInstancesByCommerceDiscountIdCount(commerceDiscountId, sku);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceDiscountRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceDiscountRelLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the commerce discount rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountRel the commerce discount rel
	 * @return the commerce discount rel that was updated
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRel
		updateCommerceDiscountRel(
			com.liferay.commerce.discount.model.CommerceDiscountRel
				commerceDiscountRel) {

		return _commerceDiscountRelLocalService.updateCommerceDiscountRel(
			commerceDiscountRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceDiscountRelLocalService.getBasePersistence();
	}

	@Override
	public CommerceDiscountRelLocalService getWrappedService() {
		return _commerceDiscountRelLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceDiscountRelLocalService commerceDiscountRelLocalService) {

		_commerceDiscountRelLocalService = commerceDiscountRelLocalService;
	}

	private CommerceDiscountRelLocalService _commerceDiscountRelLocalService;

}