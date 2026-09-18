/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceDiscountRule. This utility wraps
 * <code>com.liferay.commerce.discount.service.impl.CommerceDiscountRuleLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CommerceDiscountRuleLocalService
 * @generated
 */
public class CommerceDiscountRuleLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.discount.service.impl.CommerceDiscountRuleLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static CommerceDiscountRule addCommerceDiscountRule(
		CommerceDiscountRule commerceDiscountRule) {

		return getService().addCommerceDiscountRule(commerceDiscountRule);
	}

	public static CommerceDiscountRule addCommerceDiscountRule(
			long commerceDiscountId, String type, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceDiscountRule(
			commerceDiscountId, type, typeSettings, serviceContext);
	}

	public static CommerceDiscountRule addCommerceDiscountRule(
			long commerceDiscountId, String name, String type,
			String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceDiscountRule(
			commerceDiscountId, name, type, typeSettings, serviceContext);
	}

	public static CommerceDiscountRule addCommerceDiscountRule(
			String externalReferenceCode, long commerceDiscountId, String name,
			String type, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceDiscountRule(
			externalReferenceCode, commerceDiscountId, name, type, typeSettings,
			serviceContext);
	}

	public static CommerceDiscountRule addOrUpdateCommerceDiscountRule(
			String externalReferenceCode, long commerceDiscountRuleId,
			long commerceDiscountId, String name, String type,
			String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCommerceDiscountRule(
			externalReferenceCode, commerceDiscountRuleId, commerceDiscountId,
			name, type, typeSettings, serviceContext);
	}

	/**
	 * Creates a new commerce discount rule with the primary key. Does not add the commerce discount rule to the database.
	 *
	 * @param commerceDiscountRuleId the primary key for the new commerce discount rule
	 * @return the new commerce discount rule
	 */
	public static CommerceDiscountRule createCommerceDiscountRule(
		long commerceDiscountRuleId) {

		return getService().createCommerceDiscountRule(commerceDiscountRuleId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
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
	public static CommerceDiscountRule deleteCommerceDiscountRule(
			CommerceDiscountRule commerceDiscountRule)
		throws PortalException {

		return getService().deleteCommerceDiscountRule(commerceDiscountRule);
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
	public static CommerceDiscountRule deleteCommerceDiscountRule(
			long commerceDiscountRuleId)
		throws PortalException {

		return getService().deleteCommerceDiscountRule(commerceDiscountRuleId);
	}

	public static void deleteCommerceDiscountRules(long commerceDiscountId)
		throws PortalException {

		getService().deleteCommerceDiscountRules(commerceDiscountId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static CommerceDiscountRule fetchCommerceDiscountRule(
		long commerceDiscountRuleId) {

		return getService().fetchCommerceDiscountRule(commerceDiscountRuleId);
	}

	public static CommerceDiscountRule
		fetchCommerceDiscountRuleByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchCommerceDiscountRuleByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce discount rule with the primary key.
	 *
	 * @param commerceDiscountRuleId the primary key of the commerce discount rule
	 * @return the commerce discount rule
	 * @throws PortalException if a commerce discount rule with the primary key could not be found
	 */
	public static CommerceDiscountRule getCommerceDiscountRule(
			long commerceDiscountRuleId)
		throws PortalException {

		return getService().getCommerceDiscountRule(commerceDiscountRuleId);
	}

	public static CommerceDiscountRule
			getCommerceDiscountRuleByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCommerceDiscountRuleByExternalReferenceCode(
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
	public static List<CommerceDiscountRule> getCommerceDiscountRules(
		int start, int end) {

		return getService().getCommerceDiscountRules(start, end);
	}

	public static List<CommerceDiscountRule> getCommerceDiscountRules(
		long commerceDiscountId, int start, int end,
		OrderByComparator<CommerceDiscountRule> orderByComparator) {

		return getService().getCommerceDiscountRules(
			commerceDiscountId, start, end, orderByComparator);
	}

	public static List<CommerceDiscountRule> getCommerceDiscountRules(
			long commerceDiscountId, String name, int start, int end)
		throws PortalException {

		return getService().getCommerceDiscountRules(
			commerceDiscountId, name, start, end);
	}

	/**
	 * Returns the number of commerce discount rules.
	 *
	 * @return the number of commerce discount rules
	 */
	public static int getCommerceDiscountRulesCount() {
		return getService().getCommerceDiscountRulesCount();
	}

	public static int getCommerceDiscountRulesCount(long commerceDiscountId) {
		return getService().getCommerceDiscountRulesCount(commerceDiscountId);
	}

	public static int getCommerceDiscountRulesCount(
			long commerceDiscountId, String name)
		throws PortalException {

		return getService().getCommerceDiscountRulesCount(
			commerceDiscountId, name);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
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
	public static CommerceDiscountRule updateCommerceDiscountRule(
		CommerceDiscountRule commerceDiscountRule) {

		return getService().updateCommerceDiscountRule(commerceDiscountRule);
	}

	public static CommerceDiscountRule updateCommerceDiscountRule(
			long commerceDiscountRuleId, String type, String typeSettings)
		throws PortalException {

		return getService().updateCommerceDiscountRule(
			commerceDiscountRuleId, type, typeSettings);
	}

	public static CommerceDiscountRule updateCommerceDiscountRule(
			long commerceDiscountRuleId, String name, String type,
			String typeSettings)
		throws PortalException {

		return getService().updateCommerceDiscountRule(
			commerceDiscountRuleId, name, type, typeSettings);
	}

	public static CommerceDiscountRuleLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceDiscountRuleLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceDiscountRuleLocalServiceUtil.class,
			CommerceDiscountRuleLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1608909208