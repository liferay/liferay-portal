/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceShippingFixedOptionLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShippingFixedOptionLocalService
 * @generated
 */
public class CommerceShippingFixedOptionLocalServiceWrapper
	implements CommerceShippingFixedOptionLocalService,
			   ServiceWrapper<CommerceShippingFixedOptionLocalService> {

	public CommerceShippingFixedOptionLocalServiceWrapper() {
		this(null);
	}

	public CommerceShippingFixedOptionLocalServiceWrapper(
		CommerceShippingFixedOptionLocalService
			commerceShippingFixedOptionLocalService) {

		_commerceShippingFixedOptionLocalService =
			commerceShippingFixedOptionLocalService;
	}

	/**
	 * Adds the commerce shipping fixed option to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingFixedOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingFixedOption the commerce shipping fixed option
	 * @return the commerce shipping fixed option that was added
	 */
	@Override
	public
		com.liferay.commerce.shipping.engine.fixed.model.
			CommerceShippingFixedOption addCommerceShippingFixedOption(
				com.liferay.commerce.shipping.engine.fixed.model.
					CommerceShippingFixedOption commerceShippingFixedOption) {

		return _commerceShippingFixedOptionLocalService.
			addCommerceShippingFixedOption(commerceShippingFixedOption);
	}

	@Override
	public
		com.liferay.commerce.shipping.engine.fixed.model.
			CommerceShippingFixedOption addCommerceShippingFixedOption(
					long userId, long groupId, long commerceShippingMethodId,
					java.math.BigDecimal amount,
					java.util.Map<java.util.Locale, String> descriptionMap,
					String key, java.util.Map<java.util.Locale, String> nameMap,
					double priority)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingFixedOptionLocalService.
			addCommerceShippingFixedOption(
				userId, groupId, commerceShippingMethodId, amount,
				descriptionMap, key, nameMap, priority);
	}

	/**
	 * Creates a new commerce shipping fixed option with the primary key. Does not add the commerce shipping fixed option to the database.
	 *
	 * @param commerceShippingFixedOptionId the primary key for the new commerce shipping fixed option
	 * @return the new commerce shipping fixed option
	 */
	@Override
	public
		com.liferay.commerce.shipping.engine.fixed.model.
			CommerceShippingFixedOption createCommerceShippingFixedOption(
				long commerceShippingFixedOptionId) {

		return _commerceShippingFixedOptionLocalService.
			createCommerceShippingFixedOption(commerceShippingFixedOptionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingFixedOptionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce shipping fixed option from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingFixedOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingFixedOption the commerce shipping fixed option
	 * @return the commerce shipping fixed option that was removed
	 */
	@Override
	public
		com.liferay.commerce.shipping.engine.fixed.model.
			CommerceShippingFixedOption deleteCommerceShippingFixedOption(
				com.liferay.commerce.shipping.engine.fixed.model.
					CommerceShippingFixedOption commerceShippingFixedOption) {

		return _commerceShippingFixedOptionLocalService.
			deleteCommerceShippingFixedOption(commerceShippingFixedOption);
	}

	/**
	 * Deletes the commerce shipping fixed option with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingFixedOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingFixedOptionId the primary key of the commerce shipping fixed option
	 * @return the commerce shipping fixed option that was removed
	 * @throws PortalException if a commerce shipping fixed option with the primary key could not be found
	 */
	@Override
	public
		com.liferay.commerce.shipping.engine.fixed.model.
			CommerceShippingFixedOption deleteCommerceShippingFixedOption(
					long commerceShippingFixedOptionId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingFixedOptionLocalService.
			deleteCommerceShippingFixedOption(commerceShippingFixedOptionId);
	}

	@Override
	public void deleteCommerceShippingFixedOptions(
		long commerceShippingMethodId) {

		_commerceShippingFixedOptionLocalService.
			deleteCommerceShippingFixedOptions(commerceShippingMethodId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingFixedOptionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceShippingFixedOptionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceShippingFixedOptionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceShippingFixedOptionLocalService.dynamicQuery();
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

		return _commerceShippingFixedOptionLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shipping.engine.fixed.model.impl.CommerceShippingFixedOptionModelImpl</code>.
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

		return _commerceShippingFixedOptionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shipping.engine.fixed.model.impl.CommerceShippingFixedOptionModelImpl</code>.
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

		return _commerceShippingFixedOptionLocalService.dynamicQuery(
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

		return _commerceShippingFixedOptionLocalService.dynamicQueryCount(
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

		return _commerceShippingFixedOptionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public
		com.liferay.commerce.shipping.engine.fixed.model.
			CommerceShippingFixedOption fetchCommerceShippingFixedOption(
				long commerceShippingFixedOptionId) {

		return _commerceShippingFixedOptionLocalService.
			fetchCommerceShippingFixedOption(commerceShippingFixedOptionId);
	}

	@Override
	public
		com.liferay.commerce.shipping.engine.fixed.model.
			CommerceShippingFixedOption fetchCommerceShippingFixedOption(
				long companyId, String key) {

		return _commerceShippingFixedOptionLocalService.
			fetchCommerceShippingFixedOption(companyId, key);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceShippingFixedOptionLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public java.util.List
		<com.liferay.commerce.shipping.engine.fixed.model.
			CommerceShippingFixedOption>
				getCommerceOrderTypeCommerceShippingFixedOptions(
					long companyId, long commerceOrderTypeId,
					long commerceShippingMethodId) {

		return _commerceShippingFixedOptionLocalService.
			getCommerceOrderTypeCommerceShippingFixedOptions(
				companyId, commerceOrderTypeId, commerceShippingMethodId);
	}

	/**
	 * Returns the commerce shipping fixed option with the primary key.
	 *
	 * @param commerceShippingFixedOptionId the primary key of the commerce shipping fixed option
	 * @return the commerce shipping fixed option
	 * @throws PortalException if a commerce shipping fixed option with the primary key could not be found
	 */
	@Override
	public
		com.liferay.commerce.shipping.engine.fixed.model.
			CommerceShippingFixedOption getCommerceShippingFixedOption(
					long commerceShippingFixedOptionId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingFixedOptionLocalService.
			getCommerceShippingFixedOption(commerceShippingFixedOptionId);
	}

	/**
	 * Returns a range of all the commerce shipping fixed options.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shipping.engine.fixed.model.impl.CommerceShippingFixedOptionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce shipping fixed options
	 * @param end the upper bound of the range of commerce shipping fixed options (not inclusive)
	 * @return the range of commerce shipping fixed options
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.shipping.engine.fixed.model.
			CommerceShippingFixedOption> getCommerceShippingFixedOptions(
				int start, int end) {

		return _commerceShippingFixedOptionLocalService.
			getCommerceShippingFixedOptions(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.shipping.engine.fixed.model.
			CommerceShippingFixedOption> getCommerceShippingFixedOptions(
				long commerceShippingMethodId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.shipping.engine.fixed.model.
						CommerceShippingFixedOption> orderByComparator) {

		return _commerceShippingFixedOptionLocalService.
			getCommerceShippingFixedOptions(
				commerceShippingMethodId, start, end, orderByComparator);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.shipping.engine.fixed.model.
			CommerceShippingFixedOption> getCommerceShippingFixedOptions(
					long companyId, long groupId, long commerceShippingMethodId,
					String keywords, int start, int end)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingFixedOptionLocalService.
			getCommerceShippingFixedOptions(
				companyId, groupId, commerceShippingMethodId, keywords, start,
				end);
	}

	/**
	 * Returns the number of commerce shipping fixed options.
	 *
	 * @return the number of commerce shipping fixed options
	 */
	@Override
	public int getCommerceShippingFixedOptionsCount() {
		return _commerceShippingFixedOptionLocalService.
			getCommerceShippingFixedOptionsCount();
	}

	@Override
	public long getCommerceShippingFixedOptionsCount(
			long companyId, long groupId, long commerceShippingMethodId,
			String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingFixedOptionLocalService.
			getCommerceShippingFixedOptionsCount(
				companyId, groupId, commerceShippingMethodId, keywords);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceShippingFixedOptionLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceShippingFixedOptionLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingFixedOptionLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.shipping.engine.fixed.model.
			CommerceShippingFixedOption> searchCommerceShippingFixedOption(
					com.liferay.portal.kernel.search.SearchContext
						searchContext)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingFixedOptionLocalService.
			searchCommerceShippingFixedOption(searchContext);
	}

	/**
	 * Updates the commerce shipping fixed option in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingFixedOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingFixedOption the commerce shipping fixed option
	 * @return the commerce shipping fixed option that was updated
	 */
	@Override
	public
		com.liferay.commerce.shipping.engine.fixed.model.
			CommerceShippingFixedOption updateCommerceShippingFixedOption(
				com.liferay.commerce.shipping.engine.fixed.model.
					CommerceShippingFixedOption commerceShippingFixedOption) {

		return _commerceShippingFixedOptionLocalService.
			updateCommerceShippingFixedOption(commerceShippingFixedOption);
	}

	@Override
	public
		com.liferay.commerce.shipping.engine.fixed.model.
			CommerceShippingFixedOption updateCommerceShippingFixedOption(
					long commerceShippingFixedOptionId,
					java.math.BigDecimal amount,
					java.util.Map<java.util.Locale, String> descriptionMap,
					String key, java.util.Map<java.util.Locale, String> nameMap,
					double priority)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingFixedOptionLocalService.
			updateCommerceShippingFixedOption(
				commerceShippingFixedOptionId, amount, descriptionMap, key,
				nameMap, priority);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceShippingFixedOptionLocalService.getBasePersistence();
	}

	@Override
	public CommerceShippingFixedOptionLocalService getWrappedService() {
		return _commerceShippingFixedOptionLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceShippingFixedOptionLocalService
			commerceShippingFixedOptionLocalService) {

		_commerceShippingFixedOptionLocalService =
			commerceShippingFixedOptionLocalService;
	}

	private CommerceShippingFixedOptionLocalService
		_commerceShippingFixedOptionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:510507252