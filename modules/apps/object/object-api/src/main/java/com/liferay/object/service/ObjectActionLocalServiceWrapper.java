/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ObjectActionLocalService}.
 *
 * @author Marco Leo
 * @see ObjectActionLocalService
 * @generated
 */
public class ObjectActionLocalServiceWrapper
	implements ObjectActionLocalService,
			   ServiceWrapper<ObjectActionLocalService> {

	public ObjectActionLocalServiceWrapper() {
		this(null);
	}

	public ObjectActionLocalServiceWrapper(
		ObjectActionLocalService objectActionLocalService) {

		_objectActionLocalService = objectActionLocalService;
	}

	/**
	 * Adds the object action to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectActionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectAction the object action
	 * @return the object action that was added
	 */
	@Override
	public com.liferay.object.model.ObjectAction addObjectAction(
		com.liferay.object.model.ObjectAction objectAction) {

		return _objectActionLocalService.addObjectAction(objectAction);
	}

	@Override
	public com.liferay.object.model.ObjectAction addObjectAction(
			String externalReferenceCode, long userId, long objectDefinitionId,
			boolean active, String conditionExpression, String description,
			java.util.Map<java.util.Locale, String> errorMessageMap,
			java.util.Map<java.util.Locale, String> labelMap, String name,
			String objectActionExecutorKey, String objectActionTriggerKey,
			com.liferay.portal.kernel.util.UnicodeProperties
				parametersUnicodeProperties,
			boolean system)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectActionLocalService.addObjectAction(
			externalReferenceCode, userId, objectDefinitionId, active,
			conditionExpression, description, errorMessageMap, labelMap, name,
			objectActionExecutorKey, objectActionTriggerKey,
			parametersUnicodeProperties, system);
	}

	@Override
	public com.liferay.object.model.ObjectAction addOrUpdateObjectAction(
			String externalReferenceCode, long objectActionId, long userId,
			long objectDefinitionId, boolean active, String conditionExpression,
			String description,
			java.util.Map<java.util.Locale, String> errorMessageMap,
			java.util.Map<java.util.Locale, String> labelMap, String name,
			String objectActionExecutorKey, String objectActionTriggerKey,
			com.liferay.portal.kernel.util.UnicodeProperties
				parametersUnicodeProperties,
			boolean system)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectActionLocalService.addOrUpdateObjectAction(
			externalReferenceCode, objectActionId, userId, objectDefinitionId,
			active, conditionExpression, description, errorMessageMap, labelMap,
			name, objectActionExecutorKey, objectActionTriggerKey,
			parametersUnicodeProperties, system);
	}

	@Override
	public void addOrUpdateSubscriptionObjectActions(
			com.liferay.object.model.ObjectDefinition objectDefinition)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectActionLocalService.addOrUpdateSubscriptionObjectActions(
			objectDefinition);
	}

	/**
	 * Creates a new object action with the primary key. Does not add the object action to the database.
	 *
	 * @param objectActionId the primary key for the new object action
	 * @return the new object action
	 */
	@Override
	public com.liferay.object.model.ObjectAction createObjectAction(
		long objectActionId) {

		return _objectActionLocalService.createObjectAction(objectActionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectActionLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the object action with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectActionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectActionId the primary key of the object action
	 * @return the object action that was removed
	 * @throws PortalException if a object action with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectAction deleteObjectAction(
			long objectActionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectActionLocalService.deleteObjectAction(objectActionId);
	}

	/**
	 * Deletes the object action from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectActionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectAction the object action
	 * @return the object action that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.object.model.ObjectAction deleteObjectAction(
			com.liferay.object.model.ObjectAction objectAction)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectActionLocalService.deleteObjectAction(objectAction);
	}

	@Override
	public void deleteObjectActions(long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectActionLocalService.deleteObjectActions(objectDefinitionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectActionLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _objectActionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _objectActionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _objectActionLocalService.dynamicQuery();
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

		return _objectActionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectActionModelImpl</code>.
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

		return _objectActionLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectActionModelImpl</code>.
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

		return _objectActionLocalService.dynamicQuery(
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

		return _objectActionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _objectActionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.object.model.ObjectAction fetchObjectAction(
		long objectActionId) {

		return _objectActionLocalService.fetchObjectAction(objectActionId);
	}

	@Override
	public com.liferay.object.model.ObjectAction fetchObjectAction(
		long objectDefinitionId, String name) {

		return _objectActionLocalService.fetchObjectAction(
			objectDefinitionId, name);
	}

	@Override
	public com.liferay.object.model.ObjectAction fetchObjectAction(
		String externalReferenceCode, long objectDefinitionId) {

		return _objectActionLocalService.fetchObjectAction(
			externalReferenceCode, objectDefinitionId);
	}

	/**
	 * Returns the object action with the matching UUID and company.
	 *
	 * @param uuid the object action's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectAction
		fetchObjectActionByUuidAndCompanyId(String uuid, long companyId) {

		return _objectActionLocalService.fetchObjectActionByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _objectActionLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _objectActionLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _objectActionLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the object action with the primary key.
	 *
	 * @param objectActionId the primary key of the object action
	 * @return the object action
	 * @throws PortalException if a object action with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectAction getObjectAction(
			long objectActionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectActionLocalService.getObjectAction(objectActionId);
	}

	@Override
	public com.liferay.object.model.ObjectAction getObjectAction(
			long objectDefinitionId, String name, String objectActionTriggerKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectActionLocalService.getObjectAction(
			objectDefinitionId, name, objectActionTriggerKey);
	}

	/**
	 * Returns the object action with the matching UUID and company.
	 *
	 * @param uuid the object action's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object action
	 * @throws PortalException if a matching object action could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectAction
			getObjectActionByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectActionLocalService.getObjectActionByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectAction>
		getObjectActions(boolean active, String objectActionExecutorKey) {

		return _objectActionLocalService.getObjectActions(
			active, objectActionExecutorKey);
	}

	/**
	 * Returns a range of all the object actions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of object actions
	 */
	@Override
	public java.util.List<com.liferay.object.model.ObjectAction>
		getObjectActions(int start, int end) {

		return _objectActionLocalService.getObjectActions(start, end);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectAction>
		getObjectActions(long objectDefinitionId) {

		return _objectActionLocalService.getObjectActions(objectDefinitionId);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectAction>
		getObjectActions(
			long objectDefinitionId, String objectActionTriggerKey) {

		return _objectActionLocalService.getObjectActions(
			objectDefinitionId, objectActionTriggerKey);
	}

	/**
	 * Returns the number of object actions.
	 *
	 * @return the number of object actions
	 */
	@Override
	public int getObjectActionsCount() {
		return _objectActionLocalService.getObjectActionsCount();
	}

	@Override
	public java.util.Map
		<Long, java.util.List<com.liferay.object.model.ObjectAction>>
			getObjectActionsMap(
				long companyId, boolean active, String objectActionTriggerKey) {

		return _objectActionLocalService.getObjectActionsMap(
			companyId, active, objectActionTriggerKey);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectActionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectActionLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.object.model.ObjectAction updateActive(
			com.liferay.object.model.ObjectAction objectAction, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectActionLocalService.updateActive(objectAction, active);
	}

	/**
	 * Updates the object action in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectActionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectAction the object action
	 * @return the object action that was updated
	 */
	@Override
	public com.liferay.object.model.ObjectAction updateObjectAction(
		com.liferay.object.model.ObjectAction objectAction) {

		return _objectActionLocalService.updateObjectAction(objectAction);
	}

	@Override
	public com.liferay.object.model.ObjectAction updateObjectAction(
			String externalReferenceCode, long objectActionId, boolean active,
			String conditionExpression, String description,
			java.util.Map<java.util.Locale, String> errorMessageMap,
			java.util.Map<java.util.Locale, String> labelMap, String name,
			String objectActionExecutorKey, String objectActionTriggerKey,
			com.liferay.portal.kernel.util.UnicodeProperties
				parametersUnicodeProperties)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectActionLocalService.updateObjectAction(
			externalReferenceCode, objectActionId, active, conditionExpression,
			description, errorMessageMap, labelMap, name,
			objectActionExecutorKey, objectActionTriggerKey,
			parametersUnicodeProperties);
	}

	@Override
	public com.liferay.object.model.ObjectAction updateStatus(
			long objectActionId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectActionLocalService.updateStatus(objectActionId, status);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _objectActionLocalService.getBasePersistence();
	}

	@Override
	public ObjectActionLocalService getWrappedService() {
		return _objectActionLocalService;
	}

	@Override
	public void setWrappedService(
		ObjectActionLocalService objectActionLocalService) {

		_objectActionLocalService = objectActionLocalService;
	}

	private ObjectActionLocalService _objectActionLocalService;

}