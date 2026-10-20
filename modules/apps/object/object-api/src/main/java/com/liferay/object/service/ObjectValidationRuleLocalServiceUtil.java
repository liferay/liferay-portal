/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectValidationRule;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for ObjectValidationRule. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectValidationRuleLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see ObjectValidationRuleLocalService
 * @generated
 */
public class ObjectValidationRuleLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectValidationRuleLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the object validation rule to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectValidationRuleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectValidationRule the object validation rule
	 * @return the object validation rule that was added
	 */
	public static ObjectValidationRule addObjectValidationRule(
		ObjectValidationRule objectValidationRule) {

		return getService().addObjectValidationRule(objectValidationRule);
	}

	public static ObjectValidationRule addObjectValidationRule(
			String externalReferenceCode, long userId, long objectDefinitionId,
			boolean active, String engine,
			Map<java.util.Locale, String> errorLabelMap,
			Map<java.util.Locale, String> nameMap, String outputType,
			String script, boolean system,
			List<com.liferay.object.model.ObjectValidationRuleSetting>
				objectValidationRuleSettings)
		throws PortalException {

		return getService().addObjectValidationRule(
			externalReferenceCode, userId, objectDefinitionId, active, engine,
			errorLabelMap, nameMap, outputType, script, system,
			objectValidationRuleSettings);
	}

	/**
	 * Creates a new object validation rule with the primary key. Does not add the object validation rule to the database.
	 *
	 * @param objectValidationRuleId the primary key for the new object validation rule
	 * @return the new object validation rule
	 */
	public static ObjectValidationRule createObjectValidationRule(
		long objectValidationRuleId) {

		return getService().createObjectValidationRule(objectValidationRuleId);
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
	 * Deletes the object validation rule with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectValidationRuleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectValidationRuleId the primary key of the object validation rule
	 * @return the object validation rule that was removed
	 * @throws PortalException if a object validation rule with the primary key could not be found
	 */
	public static ObjectValidationRule deleteObjectValidationRule(
			long objectValidationRuleId)
		throws PortalException {

		return getService().deleteObjectValidationRule(objectValidationRuleId);
	}

	/**
	 * Deletes the object validation rule from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectValidationRuleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectValidationRule the object validation rule
	 * @return the object validation rule that was removed
	 * @throws PortalException
	 */
	public static ObjectValidationRule deleteObjectValidationRule(
			ObjectValidationRule objectValidationRule)
		throws PortalException {

		return getService().deleteObjectValidationRule(objectValidationRule);
	}

	public static void deleteObjectValidationRules(Long objectDefinitionId)
		throws PortalException {

		getService().deleteObjectValidationRules(objectDefinitionId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
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

	public static ObjectValidationRule fetchObjectValidationRule(
		long objectValidationRuleId) {

		return getService().fetchObjectValidationRule(objectValidationRuleId);
	}

	public static ObjectValidationRule fetchObjectValidationRule(
		String externalReferenceCode, long objectDefinitionId) {

		return getService().fetchObjectValidationRule(
			externalReferenceCode, objectDefinitionId);
	}

	/**
	 * Returns the object validation rule with the matching UUID and company.
	 *
	 * @param uuid the object validation rule's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object validation rule, or <code>null</code> if a matching object validation rule could not be found
	 */
	public static ObjectValidationRule
		fetchObjectValidationRuleByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchObjectValidationRuleByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the object validation rule with the primary key.
	 *
	 * @param objectValidationRuleId the primary key of the object validation rule
	 * @return the object validation rule
	 * @throws PortalException if a object validation rule with the primary key could not be found
	 */
	public static ObjectValidationRule getObjectValidationRule(
			long objectValidationRuleId)
		throws PortalException {

		return getService().getObjectValidationRule(objectValidationRuleId);
	}

	/**
	 * Returns the object validation rule with the matching UUID and company.
	 *
	 * @param uuid the object validation rule's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object validation rule
	 * @throws PortalException if a matching object validation rule could not be found
	 */
	public static ObjectValidationRule
			getObjectValidationRuleByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getObjectValidationRuleByUuidAndCompanyId(
			uuid, companyId);
	}

	public static List<ObjectValidationRule> getObjectValidationRules(
		boolean active, String engine) {

		return getService().getObjectValidationRules(active, engine);
	}

	/**
	 * Returns a range of all the object validation rules.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @return the range of object validation rules
	 */
	public static List<ObjectValidationRule> getObjectValidationRules(
		int start, int end) {

		return getService().getObjectValidationRules(start, end);
	}

	public static List<ObjectValidationRule> getObjectValidationRules(
		long objectDefinitionId) {

		return getService().getObjectValidationRules(objectDefinitionId);
	}

	public static List<ObjectValidationRule> getObjectValidationRules(
		long objectDefinitionId, boolean active) {

		return getService().getObjectValidationRules(
			objectDefinitionId, active);
	}

	/**
	 * Returns the number of object validation rules.
	 *
	 * @return the number of object validation rules
	 */
	public static int getObjectValidationRulesCount() {
		return getService().getObjectValidationRulesCount();
	}

	public static int getObjectValidationRulesCount(
		long objectDefinitionId, boolean active) {

		return getService().getObjectValidationRulesCount(
			objectDefinitionId, active);
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

	public static void unassociateObjectField(
		com.liferay.object.model.ObjectField objectField) {

		getService().unassociateObjectField(objectField);
	}

	/**
	 * Updates the object validation rule in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectValidationRuleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectValidationRule the object validation rule
	 * @return the object validation rule that was updated
	 */
	public static ObjectValidationRule updateObjectValidationRule(
		ObjectValidationRule objectValidationRule) {

		return getService().updateObjectValidationRule(objectValidationRule);
	}

	public static ObjectValidationRule updateObjectValidationRule(
			String externalReferenceCode, long objectValidationRuleId,
			boolean active, String engine,
			Map<java.util.Locale, String> errorLabelMap,
			Map<java.util.Locale, String> nameMap, String outputType,
			String script,
			List<com.liferay.object.model.ObjectValidationRuleSetting>
				objectValidationRuleSettings)
		throws PortalException {

		return getService().updateObjectValidationRule(
			externalReferenceCode, objectValidationRuleId, active, engine,
			errorLabelMap, nameMap, outputType, script,
			objectValidationRuleSettings);
	}

	public static void validate(
			com.liferay.portal.kernel.model.BaseModel<?> baseModel,
			long objectDefinitionId,
			com.liferay.portal.kernel.json.JSONObject payloadJSONObject,
			long userId)
		throws PortalException {

		getService().validate(
			baseModel, objectDefinitionId, payloadJSONObject, userId);
	}

	public static void validate(
			List<String> externalReferenceCodes,
			com.liferay.object.model.ObjectEntry objectEntry, long userId)
		throws PortalException {

		getService().validate(externalReferenceCodes, objectEntry, userId);
	}

	public static ObjectValidationRuleLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectValidationRuleLocalService>
		_serviceSnapshot = new Snapshot<>(
			ObjectValidationRuleLocalServiceUtil.class,
			ObjectValidationRuleLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:904551089