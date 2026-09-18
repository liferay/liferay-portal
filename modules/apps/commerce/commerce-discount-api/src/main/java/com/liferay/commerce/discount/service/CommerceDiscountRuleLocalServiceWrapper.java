/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceDiscountRuleLocalService}.
 *
 * @author Marco Leo
 * @see CommerceDiscountRuleLocalService
 * @generated
 */
public class CommerceDiscountRuleLocalServiceWrapper
	implements CommerceDiscountRuleLocalService,
			   ServiceWrapper<CommerceDiscountRuleLocalService> {

	public CommerceDiscountRuleLocalServiceWrapper() {
		this(null);
	}

	public CommerceDiscountRuleLocalServiceWrapper(
		CommerceDiscountRuleLocalService commerceDiscountRuleLocalService) {

		_commerceDiscountRuleLocalService = commerceDiscountRuleLocalService;
	}

	/**
	 * Adds the commerce discount rule to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountRuleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountRule the commerce discount rule
	 * @return the commerce discount rule that was added
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
		addCommerceDiscountRule(
			com.liferay.commerce.discount.model.CommerceDiscountRule
				commerceDiscountRule) {

		return _commerceDiscountRuleLocalService.addCommerceDiscountRule(
			commerceDiscountRule);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
			addCommerceDiscountRule(
				long commerceDiscountId, String type, String typeSettings,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleLocalService.addCommerceDiscountRule(
			commerceDiscountId, type, typeSettings, serviceContext);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
			addCommerceDiscountRule(
				long commerceDiscountId, String name, String type,
				String typeSettings,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleLocalService.addCommerceDiscountRule(
			commerceDiscountId, name, type, typeSettings, serviceContext);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
			addCommerceDiscountRule(
				String externalReferenceCode, long commerceDiscountId,
				String name, String type, String typeSettings,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleLocalService.addCommerceDiscountRule(
			externalReferenceCode, commerceDiscountId, name, type, typeSettings,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
			addOrUpdateCommerceDiscountRule(
				String externalReferenceCode, long commerceDiscountRuleId,
				long commerceDiscountId, String name, String type,
				String typeSettings,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleLocalService.
			addOrUpdateCommerceDiscountRule(
				externalReferenceCode, commerceDiscountRuleId,
				commerceDiscountId, name, type, typeSettings, serviceContext);
	}

	/**
	 * Creates a new commerce discount rule with the primary key. Does not add the commerce discount rule to the database.
	 *
	 * @param commerceDiscountRuleId the primary key for the new commerce discount rule
	 * @return the new commerce discount rule
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
		createCommerceDiscountRule(long commerceDiscountRuleId) {

		return _commerceDiscountRuleLocalService.createCommerceDiscountRule(
			commerceDiscountRuleId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce discount rule from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountRuleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountRule the commerce discount rule
	 * @return the commerce discount rule that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
			deleteCommerceDiscountRule(
				com.liferay.commerce.discount.model.CommerceDiscountRule
					commerceDiscountRule)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleLocalService.deleteCommerceDiscountRule(
			commerceDiscountRule);
	}

	/**
	 * Deletes the commerce discount rule with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountRuleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountRuleId the primary key of the commerce discount rule
	 * @return the commerce discount rule that was removed
	 * @throws PortalException if a commerce discount rule with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
			deleteCommerceDiscountRule(long commerceDiscountRuleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleLocalService.deleteCommerceDiscountRule(
			commerceDiscountRuleId);
	}

	@Override
	public void deleteCommerceDiscountRules(long commerceDiscountId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceDiscountRuleLocalService.deleteCommerceDiscountRules(
			commerceDiscountId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceDiscountRuleLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceDiscountRuleLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceDiscountRuleLocalService.dynamicQuery();
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

		return _commerceDiscountRuleLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountRuleModelImpl</code>.
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

		return _commerceDiscountRuleLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountRuleModelImpl</code>.
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

		return _commerceDiscountRuleLocalService.dynamicQuery(
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

		return _commerceDiscountRuleLocalService.dynamicQueryCount(
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

		return _commerceDiscountRuleLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
		fetchCommerceDiscountRule(long commerceDiscountRuleId) {

		return _commerceDiscountRuleLocalService.fetchCommerceDiscountRule(
			commerceDiscountRuleId);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
		fetchCommerceDiscountRuleByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _commerceDiscountRuleLocalService.
			fetchCommerceDiscountRuleByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceDiscountRuleLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce discount rule with the primary key.
	 *
	 * @param commerceDiscountRuleId the primary key of the commerce discount rule
	 * @return the commerce discount rule
	 * @throws PortalException if a commerce discount rule with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
			getCommerceDiscountRule(long commerceDiscountRuleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleLocalService.getCommerceDiscountRule(
			commerceDiscountRuleId);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
			getCommerceDiscountRuleByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleLocalService.
			getCommerceDiscountRuleByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns a range of all the commerce discount rules.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountRuleModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discount rules
	 * @param end the upper bound of the range of commerce discount rules (not inclusive)
	 * @return the range of commerce discount rules
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRule>
			getCommerceDiscountRules(int start, int end) {

		return _commerceDiscountRuleLocalService.getCommerceDiscountRules(
			start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRule>
			getCommerceDiscountRules(
				long commerceDiscountId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.discount.model.CommerceDiscountRule>
						orderByComparator) {

		return _commerceDiscountRuleLocalService.getCommerceDiscountRules(
			commerceDiscountId, start, end, orderByComparator);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRule>
				getCommerceDiscountRules(
					long commerceDiscountId, String name, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleLocalService.getCommerceDiscountRules(
			commerceDiscountId, name, start, end);
	}

	/**
	 * Returns the number of commerce discount rules.
	 *
	 * @return the number of commerce discount rules
	 */
	@Override
	public int getCommerceDiscountRulesCount() {
		return _commerceDiscountRuleLocalService.
			getCommerceDiscountRulesCount();
	}

	@Override
	public int getCommerceDiscountRulesCount(long commerceDiscountId) {
		return _commerceDiscountRuleLocalService.getCommerceDiscountRulesCount(
			commerceDiscountId);
	}

	@Override
	public int getCommerceDiscountRulesCount(
			long commerceDiscountId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleLocalService.getCommerceDiscountRulesCount(
			commerceDiscountId, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceDiscountRuleLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceDiscountRuleLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the commerce discount rule in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountRuleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountRule the commerce discount rule
	 * @return the commerce discount rule that was updated
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
		updateCommerceDiscountRule(
			com.liferay.commerce.discount.model.CommerceDiscountRule
				commerceDiscountRule) {

		return _commerceDiscountRuleLocalService.updateCommerceDiscountRule(
			commerceDiscountRule);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
			updateCommerceDiscountRule(
				long commerceDiscountRuleId, String type, String typeSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleLocalService.updateCommerceDiscountRule(
			commerceDiscountRuleId, type, typeSettings);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
			updateCommerceDiscountRule(
				long commerceDiscountRuleId, String name, String type,
				String typeSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleLocalService.updateCommerceDiscountRule(
			commerceDiscountRuleId, name, type, typeSettings);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceDiscountRuleLocalService.getBasePersistence();
	}

	@Override
	public CommerceDiscountRuleLocalService getWrappedService() {
		return _commerceDiscountRuleLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceDiscountRuleLocalService commerceDiscountRuleLocalService) {

		_commerceDiscountRuleLocalService = commerceDiscountRuleLocalService;
	}

	private CommerceDiscountRuleLocalService _commerceDiscountRuleLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-639307238