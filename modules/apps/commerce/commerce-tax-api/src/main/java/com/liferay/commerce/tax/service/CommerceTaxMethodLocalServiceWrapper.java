/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceTaxMethodLocalService}.
 *
 * @author Marco Leo
 * @see CommerceTaxMethodLocalService
 * @generated
 */
public class CommerceTaxMethodLocalServiceWrapper
	implements CommerceTaxMethodLocalService,
			   ServiceWrapper<CommerceTaxMethodLocalService> {

	public CommerceTaxMethodLocalServiceWrapper() {
		this(null);
	}

	public CommerceTaxMethodLocalServiceWrapper(
		CommerceTaxMethodLocalService commerceTaxMethodLocalService) {

		_commerceTaxMethodLocalService = commerceTaxMethodLocalService;
	}

	/**
	 * Adds the commerce tax method to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxMethodLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxMethod the commerce tax method
	 * @return the commerce tax method that was added
	 */
	@Override
	public com.liferay.commerce.tax.model.CommerceTaxMethod
		addCommerceTaxMethod(
			com.liferay.commerce.tax.model.CommerceTaxMethod
				commerceTaxMethod) {

		return _commerceTaxMethodLocalService.addCommerceTaxMethod(
			commerceTaxMethod);
	}

	@Override
	public com.liferay.commerce.tax.model.CommerceTaxMethod
			addCommerceTaxMethod(
				long userId, long groupId,
				java.util.Map<java.util.Locale, String> nameMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				String engineKey, boolean percentage, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxMethodLocalService.addCommerceTaxMethod(
			userId, groupId, nameMap, descriptionMap, engineKey, percentage,
			active);
	}

	/**
	 * Creates a new commerce tax method with the primary key. Does not add the commerce tax method to the database.
	 *
	 * @param commerceTaxMethodId the primary key for the new commerce tax method
	 * @return the new commerce tax method
	 */
	@Override
	public com.liferay.commerce.tax.model.CommerceTaxMethod
		createCommerceTaxMethod(long commerceTaxMethodId) {

		return _commerceTaxMethodLocalService.createCommerceTaxMethod(
			commerceTaxMethodId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxMethodLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce tax method from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxMethodLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxMethod the commerce tax method
	 * @return the commerce tax method that was removed
	 */
	@Override
	public com.liferay.commerce.tax.model.CommerceTaxMethod
		deleteCommerceTaxMethod(
			com.liferay.commerce.tax.model.CommerceTaxMethod
				commerceTaxMethod) {

		return _commerceTaxMethodLocalService.deleteCommerceTaxMethod(
			commerceTaxMethod);
	}

	/**
	 * Deletes the commerce tax method with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxMethodLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxMethodId the primary key of the commerce tax method
	 * @return the commerce tax method that was removed
	 * @throws PortalException if a commerce tax method with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.tax.model.CommerceTaxMethod
			deleteCommerceTaxMethod(long commerceTaxMethodId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxMethodLocalService.deleteCommerceTaxMethod(
			commerceTaxMethodId);
	}

	@Override
	public void deleteCommerceTaxMethods(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceTaxMethodLocalService.deleteCommerceTaxMethods(groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxMethodLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceTaxMethodLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceTaxMethodLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceTaxMethodLocalService.dynamicQuery();
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

		return _commerceTaxMethodLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.model.impl.CommerceTaxMethodModelImpl</code>.
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

		return _commerceTaxMethodLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.model.impl.CommerceTaxMethodModelImpl</code>.
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

		return _commerceTaxMethodLocalService.dynamicQuery(
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

		return _commerceTaxMethodLocalService.dynamicQueryCount(dynamicQuery);
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

		return _commerceTaxMethodLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.tax.model.CommerceTaxMethod
		fetchCommerceTaxMethod(long commerceTaxMethodId) {

		return _commerceTaxMethodLocalService.fetchCommerceTaxMethod(
			commerceTaxMethodId);
	}

	@Override
	public com.liferay.commerce.tax.model.CommerceTaxMethod
		fetchCommerceTaxMethod(long groupId, String engineKey) {

		return _commerceTaxMethodLocalService.fetchCommerceTaxMethod(
			groupId, engineKey);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceTaxMethodLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce tax method with the primary key.
	 *
	 * @param commerceTaxMethodId the primary key of the commerce tax method
	 * @return the commerce tax method
	 * @throws PortalException if a commerce tax method with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.tax.model.CommerceTaxMethod
			getCommerceTaxMethod(long commerceTaxMethodId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxMethodLocalService.getCommerceTaxMethod(
			commerceTaxMethodId);
	}

	/**
	 * Returns a range of all the commerce tax methods.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.model.impl.CommerceTaxMethodModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce tax methods
	 * @param end the upper bound of the range of commerce tax methods (not inclusive)
	 * @return the range of commerce tax methods
	 */
	@Override
	public java.util.List<com.liferay.commerce.tax.model.CommerceTaxMethod>
		getCommerceTaxMethods(int start, int end) {

		return _commerceTaxMethodLocalService.getCommerceTaxMethods(start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.tax.model.CommerceTaxMethod>
		getCommerceTaxMethods(long groupId, boolean active) {

		return _commerceTaxMethodLocalService.getCommerceTaxMethods(
			groupId, active);
	}

	/**
	 * Returns the number of commerce tax methods.
	 *
	 * @return the number of commerce tax methods
	 */
	@Override
	public int getCommerceTaxMethodsCount() {
		return _commerceTaxMethodLocalService.getCommerceTaxMethodsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceTaxMethodLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceTaxMethodLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxMethodLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.commerce.tax.model.CommerceTaxMethod setActive(
			long commerceTaxMethodId, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxMethodLocalService.setActive(
			commerceTaxMethodId, active);
	}

	/**
	 * Updates the commerce tax method in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxMethodLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxMethod the commerce tax method
	 * @return the commerce tax method that was updated
	 */
	@Override
	public com.liferay.commerce.tax.model.CommerceTaxMethod
		updateCommerceTaxMethod(
			com.liferay.commerce.tax.model.CommerceTaxMethod
				commerceTaxMethod) {

		return _commerceTaxMethodLocalService.updateCommerceTaxMethod(
			commerceTaxMethod);
	}

	@Override
	public com.liferay.commerce.tax.model.CommerceTaxMethod
			updateCommerceTaxMethod(
				long commerceTaxMethodId,
				java.util.Map<java.util.Locale, String> nameMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				boolean percentage, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxMethodLocalService.updateCommerceTaxMethod(
			commerceTaxMethodId, nameMap, descriptionMap, percentage, active);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceTaxMethodLocalService.getBasePersistence();
	}

	@Override
	public CommerceTaxMethodLocalService getWrappedService() {
		return _commerceTaxMethodLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceTaxMethodLocalService commerceTaxMethodLocalService) {

		_commerceTaxMethodLocalService = commerceTaxMethodLocalService;
	}

	private CommerceTaxMethodLocalService _commerceTaxMethodLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1078499951