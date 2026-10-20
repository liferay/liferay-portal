/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ObjectValidationRuleLocalService}.
 *
 * @author Marco Leo
 * @see ObjectValidationRuleLocalService
 * @generated
 */
public class ObjectValidationRuleLocalServiceWrapper
	implements ObjectValidationRuleLocalService,
			   ServiceWrapper<ObjectValidationRuleLocalService> {

	public ObjectValidationRuleLocalServiceWrapper() {
		this(null);
	}

	public ObjectValidationRuleLocalServiceWrapper(
		ObjectValidationRuleLocalService objectValidationRuleLocalService) {

		_objectValidationRuleLocalService = objectValidationRuleLocalService;
	}

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
	@Override
	public com.liferay.object.model.ObjectValidationRule
		addObjectValidationRule(
			com.liferay.object.model.ObjectValidationRule
				objectValidationRule) {

		return _objectValidationRuleLocalService.addObjectValidationRule(
			objectValidationRule);
	}

	@Override
	public com.liferay.object.model.ObjectValidationRule
			addObjectValidationRule(
				String externalReferenceCode, long userId,
				long objectDefinitionId, boolean active, String engine,
				java.util.Map<java.util.Locale, String> errorLabelMap,
				java.util.Map<java.util.Locale, String> nameMap,
				String outputType, String script, boolean system,
				java.util.List
					<com.liferay.object.model.ObjectValidationRuleSetting>
						objectValidationRuleSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectValidationRuleLocalService.addObjectValidationRule(
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
	@Override
	public com.liferay.object.model.ObjectValidationRule
		createObjectValidationRule(long objectValidationRuleId) {

		return _objectValidationRuleLocalService.createObjectValidationRule(
			objectValidationRuleId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectValidationRuleLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.object.model.ObjectValidationRule
			deleteObjectValidationRule(long objectValidationRuleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectValidationRuleLocalService.deleteObjectValidationRule(
			objectValidationRuleId);
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
	@Override
	public com.liferay.object.model.ObjectValidationRule
			deleteObjectValidationRule(
				com.liferay.object.model.ObjectValidationRule
					objectValidationRule)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectValidationRuleLocalService.deleteObjectValidationRule(
			objectValidationRule);
	}

	@Override
	public void deleteObjectValidationRules(Long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectValidationRuleLocalService.deleteObjectValidationRules(
			objectDefinitionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectValidationRuleLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _objectValidationRuleLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _objectValidationRuleLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _objectValidationRuleLocalService.dynamicQuery();
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

		return _objectValidationRuleLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _objectValidationRuleLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _objectValidationRuleLocalService.dynamicQuery(
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

		return _objectValidationRuleLocalService.dynamicQueryCount(
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

		return _objectValidationRuleLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.object.model.ObjectValidationRule
		fetchObjectValidationRule(long objectValidationRuleId) {

		return _objectValidationRuleLocalService.fetchObjectValidationRule(
			objectValidationRuleId);
	}

	@Override
	public com.liferay.object.model.ObjectValidationRule
		fetchObjectValidationRule(
			String externalReferenceCode, long objectDefinitionId) {

		return _objectValidationRuleLocalService.fetchObjectValidationRule(
			externalReferenceCode, objectDefinitionId);
	}

	/**
	 * Returns the object validation rule with the matching UUID and company.
	 *
	 * @param uuid the object validation rule's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object validation rule, or <code>null</code> if a matching object validation rule could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectValidationRule
		fetchObjectValidationRuleByUuidAndCompanyId(
			String uuid, long companyId) {

		return _objectValidationRuleLocalService.
			fetchObjectValidationRuleByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _objectValidationRuleLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _objectValidationRuleLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _objectValidationRuleLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the object validation rule with the primary key.
	 *
	 * @param objectValidationRuleId the primary key of the object validation rule
	 * @return the object validation rule
	 * @throws PortalException if a object validation rule with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectValidationRule
			getObjectValidationRule(long objectValidationRuleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectValidationRuleLocalService.getObjectValidationRule(
			objectValidationRuleId);
	}

	/**
	 * Returns the object validation rule with the matching UUID and company.
	 *
	 * @param uuid the object validation rule's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object validation rule
	 * @throws PortalException if a matching object validation rule could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectValidationRule
			getObjectValidationRuleByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectValidationRuleLocalService.
			getObjectValidationRuleByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectValidationRule>
		getObjectValidationRules(boolean active, String engine) {

		return _objectValidationRuleLocalService.getObjectValidationRules(
			active, engine);
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
	@Override
	public java.util.List<com.liferay.object.model.ObjectValidationRule>
		getObjectValidationRules(int start, int end) {

		return _objectValidationRuleLocalService.getObjectValidationRules(
			start, end);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectValidationRule>
		getObjectValidationRules(long objectDefinitionId) {

		return _objectValidationRuleLocalService.getObjectValidationRules(
			objectDefinitionId);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectValidationRule>
		getObjectValidationRules(long objectDefinitionId, boolean active) {

		return _objectValidationRuleLocalService.getObjectValidationRules(
			objectDefinitionId, active);
	}

	/**
	 * Returns the number of object validation rules.
	 *
	 * @return the number of object validation rules
	 */
	@Override
	public int getObjectValidationRulesCount() {
		return _objectValidationRuleLocalService.
			getObjectValidationRulesCount();
	}

	@Override
	public int getObjectValidationRulesCount(
		long objectDefinitionId, boolean active) {

		return _objectValidationRuleLocalService.getObjectValidationRulesCount(
			objectDefinitionId, active);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectValidationRuleLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectValidationRuleLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void unassociateObjectField(
		com.liferay.object.model.ObjectField objectField) {

		_objectValidationRuleLocalService.unassociateObjectField(objectField);
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
	@Override
	public com.liferay.object.model.ObjectValidationRule
		updateObjectValidationRule(
			com.liferay.object.model.ObjectValidationRule
				objectValidationRule) {

		return _objectValidationRuleLocalService.updateObjectValidationRule(
			objectValidationRule);
	}

	@Override
	public com.liferay.object.model.ObjectValidationRule
			updateObjectValidationRule(
				String externalReferenceCode, long objectValidationRuleId,
				boolean active, String engine,
				java.util.Map<java.util.Locale, String> errorLabelMap,
				java.util.Map<java.util.Locale, String> nameMap,
				String outputType, String script,
				java.util.List
					<com.liferay.object.model.ObjectValidationRuleSetting>
						objectValidationRuleSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectValidationRuleLocalService.updateObjectValidationRule(
			externalReferenceCode, objectValidationRuleId, active, engine,
			errorLabelMap, nameMap, outputType, script,
			objectValidationRuleSettings);
	}

	@Override
	public void validate(
			com.liferay.portal.kernel.model.BaseModel<?> baseModel,
			long objectDefinitionId,
			com.liferay.portal.kernel.json.JSONObject payloadJSONObject,
			long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectValidationRuleLocalService.validate(
			baseModel, objectDefinitionId, payloadJSONObject, userId);
	}

	@Override
	public void validate(
			java.util.List<String> externalReferenceCodes,
			com.liferay.object.model.ObjectEntry objectEntry, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectValidationRuleLocalService.validate(
			externalReferenceCodes, objectEntry, userId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _objectValidationRuleLocalService.getBasePersistence();
	}

	@Override
	public ObjectValidationRuleLocalService getWrappedService() {
		return _objectValidationRuleLocalService;
	}

	@Override
	public void setWrappedService(
		ObjectValidationRuleLocalService objectValidationRuleLocalService) {

		_objectValidationRuleLocalService = objectValidationRuleLocalService;
	}

	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1264271286