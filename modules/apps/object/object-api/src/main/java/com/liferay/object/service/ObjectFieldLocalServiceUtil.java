/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectField;
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
 * Provides the local service utility for ObjectField. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectFieldLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see ObjectFieldLocalService
 * @generated
 */
public class ObjectFieldLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectFieldLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ObjectField addCustomObjectField(
			String externalReferenceCode, long userId,
			long listTypeDefinitionId, long objectDefinitionId,
			String businessType, String dbType, boolean indexed,
			boolean indexedAsKeyword, String indexedLanguageId,
			Map<java.util.Locale, String> labelMap, boolean localized,
			String name, String readOnly, String readOnlyConditionExpression,
			boolean required, boolean state,
			List<com.liferay.object.model.ObjectFieldSetting>
				objectFieldSettings)
		throws PortalException {

		return getService().addCustomObjectField(
			externalReferenceCode, userId, listTypeDefinitionId,
			objectDefinitionId, businessType, dbType, indexed, indexedAsKeyword,
			indexedLanguageId, labelMap, localized, name, readOnly,
			readOnlyConditionExpression, required, state, objectFieldSettings);
	}

	/**
	 * Adds the object field to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFieldLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectField the object field
	 * @return the object field that was added
	 */
	public static ObjectField addObjectField(ObjectField objectField) {
		return getService().addObjectField(objectField);
	}

	public static ObjectField addOrUpdateCustomObjectField(
			String externalReferenceCode, long objectFieldId, long userId,
			long listTypeDefinitionId, long objectDefinitionId,
			String businessType, String dbType, boolean indexed,
			boolean indexedAsKeyword, String indexedLanguageId,
			Map<java.util.Locale, String> labelMap, boolean localized,
			String name, String readOnly, String readOnlyConditionExpression,
			boolean required, boolean state,
			List<com.liferay.object.model.ObjectFieldSetting>
				objectFieldSettings)
		throws PortalException {

		return getService().addOrUpdateCustomObjectField(
			externalReferenceCode, objectFieldId, userId, listTypeDefinitionId,
			objectDefinitionId, businessType, dbType, indexed, indexedAsKeyword,
			indexedLanguageId, labelMap, localized, name, readOnly,
			readOnlyConditionExpression, required, state, objectFieldSettings);
	}

	public static ObjectField addOrUpdateSystemObjectField(
			String externalReferenceCode, long userId,
			long listTypeDefinitionId, long objectDefinitionId,
			String businessType, String dbColumnName, String dbTableName,
			String dbType, boolean indexed, boolean indexedAsKeyword,
			String indexedLanguageId, Map<java.util.Locale, String> labelMap,
			boolean localized, String name, String readOnly,
			String readOnlyConditionExpression, boolean required, boolean state,
			List<com.liferay.object.model.ObjectFieldSetting>
				objectFieldSettings)
		throws PortalException {

		return getService().addOrUpdateSystemObjectField(
			externalReferenceCode, userId, listTypeDefinitionId,
			objectDefinitionId, businessType, dbColumnName, dbTableName, dbType,
			indexed, indexedAsKeyword, indexedLanguageId, labelMap, localized,
			name, readOnly, readOnlyConditionExpression, required, state,
			objectFieldSettings);
	}

	public static ObjectField addSystemObjectField(
			String externalReferenceCode, long userId,
			long listTypeDefinitionId, long objectDefinitionId,
			String businessType, String dbColumnName, String dbTableName,
			String dbType, boolean indexed, boolean indexedAsKeyword,
			String indexedLanguageId, Map<java.util.Locale, String> labelMap,
			boolean localized, String name, String readOnly,
			String readOnlyConditionExpression, boolean required, boolean state,
			List<com.liferay.object.model.ObjectFieldSetting>
				objectFieldSettings)
		throws PortalException {

		return getService().addSystemObjectField(
			externalReferenceCode, userId, listTypeDefinitionId,
			objectDefinitionId, businessType, dbColumnName, dbTableName, dbType,
			indexed, indexedAsKeyword, indexedLanguageId, labelMap, localized,
			name, readOnly, readOnlyConditionExpression, required, state,
			objectFieldSettings);
	}

	/**
	 * Creates a new object field with the primary key. Does not add the object field to the database.
	 *
	 * @param objectFieldId the primary key for the new object field
	 * @return the new object field
	 */
	public static ObjectField createObjectField(long objectFieldId) {
		return getService().createObjectField(objectFieldId);
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
	 * Deletes the object field with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFieldLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFieldId the primary key of the object field
	 * @return the object field that was removed
	 * @throws PortalException if a object field with the primary key could not be found
	 */
	public static ObjectField deleteObjectField(long objectFieldId)
		throws PortalException {

		return getService().deleteObjectField(objectFieldId);
	}

	/**
	 * Deletes the object field from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFieldLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectField the object field
	 * @return the object field that was removed
	 * @throws PortalException
	 */
	public static ObjectField deleteObjectField(ObjectField objectField)
		throws PortalException {

		return getService().deleteObjectField(objectField);
	}

	public static void deleteObjectFieldByObjectDefinitionId(
			Long objectDefinitionId)
		throws PortalException {

		getService().deleteObjectFieldByObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static ObjectField deleteRelationshipTypeObjectField(
			long objectFieldId)
		throws PortalException {

		return getService().deleteRelationshipTypeObjectField(objectFieldId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
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

	public static ObjectField fetchObjectField(long objectFieldId) {
		return getService().fetchObjectField(objectFieldId);
	}

	public static ObjectField fetchObjectField(
		long objectDefinitionId, String name) {

		return getService().fetchObjectField(objectDefinitionId, name);
	}

	public static ObjectField fetchObjectField(
		String externalReferenceCode, long objectDefinitionId) {

		return getService().fetchObjectField(
			externalReferenceCode, objectDefinitionId);
	}

	/**
	 * Returns the object field with the matching UUID and company.
	 *
	 * @param uuid the object field's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchObjectFieldByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchObjectFieldByUuidAndCompanyId(uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<ObjectField> getActiveObjectFields(
			List<ObjectField> objectFields)
		throws PortalException {

		return getService().getActiveObjectFields(objectFields);
	}

	public static com.liferay.petra.sql.dsl.Column<?, ?> getColumn(
		long objectDefinitionId, String name) {

		return getService().getColumn(objectDefinitionId, name);
	}

	public static List<ObjectField> getCustomObjectFields(
		long objectDefinitionId) {

		return getService().getCustomObjectFields(objectDefinitionId);
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

	public static List<ObjectField> getListTypeDefinitionObjectFields(
		long listTypeDefinitionId, boolean state) {

		return getService().getListTypeDefinitionObjectFields(
			listTypeDefinitionId, state);
	}

	public static List<ObjectField> getLocalizedObjectFields(
		long objectDefinitionId) {

		return getService().getLocalizedObjectFields(objectDefinitionId);
	}

	/**
	 * Returns the object field with the primary key.
	 *
	 * @param objectFieldId the primary key of the object field
	 * @return the object field
	 * @throws PortalException if a object field with the primary key could not be found
	 */
	public static ObjectField getObjectField(long objectFieldId)
		throws PortalException {

		return getService().getObjectField(objectFieldId);
	}

	public static ObjectField getObjectField(
			long objectDefinitionId, String name)
		throws PortalException {

		return getService().getObjectField(objectDefinitionId, name);
	}

	public static ObjectField getObjectField(
			String externalReferenceCode, long objectDefinitionId)
		throws PortalException {

		return getService().getObjectField(
			externalReferenceCode, objectDefinitionId);
	}

	/**
	 * Returns the object field with the matching UUID and company.
	 *
	 * @param uuid the object field's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object field
	 * @throws PortalException if a matching object field could not be found
	 */
	public static ObjectField getObjectFieldByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getObjectFieldByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the object fields.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of object fields
	 */
	public static List<ObjectField> getObjectFields(int start, int end) {
		return getService().getObjectFields(start, end);
	}

	public static List<ObjectField> getObjectFields(long objectDefinitionId) {
		return getService().getObjectFields(objectDefinitionId);
	}

	public static List<ObjectField> getObjectFields(
		long objectDefinitionId, boolean system) {

		return getService().getObjectFields(objectDefinitionId, system);
	}

	public static List<ObjectField> getObjectFields(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return getService().getObjectFields(
			objectDefinitionId, start, end, orderByComparator);
	}

	public static List<ObjectField> getObjectFields(
		long objectDefinitionId, String dbTableName) {

		return getService().getObjectFields(objectDefinitionId, dbTableName);
	}

	public static List<ObjectField> getObjectFieldsByBusinessType(
		long objectDefinitionId, String businessType) {

		return getService().getObjectFieldsByBusinessType(
			objectDefinitionId, businessType);
	}

	public static List<ObjectField> getObjectFieldsByCompanyId(long companyId) {
		return getService().getObjectFieldsByCompanyId(companyId);
	}

	/**
	 * Returns the number of object fields.
	 *
	 * @return the number of object fields
	 */
	public static int getObjectFieldsCount() {
		return getService().getObjectFieldsCount();
	}

	public static int getObjectFieldsCount(long objectDefinitionId) {
		return getService().getObjectFieldsCount(objectDefinitionId);
	}

	public static int getObjectFieldsCount(
		long objectDefinitionId, boolean system) {

		return getService().getObjectFieldsCount(objectDefinitionId, system);
	}

	public static int getObjectFieldsCountByListTypeDefinitionId(
		long listTypeDefinitionId) {

		return getService().getObjectFieldsCountByListTypeDefinitionId(
			listTypeDefinitionId);
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

	public static com.liferay.petra.sql.dsl.Table getTable(
			long objectDefinitionId, String name)
		throws PortalException {

		return getService().getTable(objectDefinitionId, name);
	}

	/**
	 * Updates the object field in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFieldLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectField the object field
	 * @return the object field that was updated
	 */
	public static ObjectField updateObjectField(ObjectField objectField) {
		return getService().updateObjectField(objectField);
	}

	public static ObjectField updateObjectField(
			String externalReferenceCode, long objectFieldId, long userId,
			long listTypeDefinitionId, long objectDefinitionId,
			String businessType, String dbColumnName, String dbTableName,
			String dbType, boolean indexed, boolean indexedAsKeyword,
			String indexedLanguageId, Map<java.util.Locale, String> labelMap,
			boolean localized, String name, String readOnly,
			String readOnlyConditionExpression, boolean required, boolean state,
			boolean system,
			List<com.liferay.object.model.ObjectFieldSetting>
				objectFieldSettings)
		throws PortalException {

		return getService().updateObjectField(
			externalReferenceCode, objectFieldId, userId, listTypeDefinitionId,
			objectDefinitionId, businessType, dbColumnName, dbTableName, dbType,
			indexed, indexedAsKeyword, indexedLanguageId, labelMap, localized,
			name, readOnly, readOnlyConditionExpression, required, state,
			system, objectFieldSettings);
	}

	public static ObjectField updateRequired(
			long objectFieldId, boolean required)
		throws PortalException {

		return getService().updateRequired(objectFieldId, required);
	}

	public static void updateUserId(
			long companyId, long oldUserId, long newUserId)
		throws PortalException {

		getService().updateUserId(companyId, oldUserId, newUserId);
	}

	public static void validateExternalReferenceCode(
			String externalReferenceCode, long objectFieldId, long companyId,
			long objectDefinitionId)
		throws PortalException {

		getService().validateExternalReferenceCode(
			externalReferenceCode, objectFieldId, companyId,
			objectDefinitionId);
	}

	public static void validateReadOnlyAndReadOnlyConditionExpression(
			String businessType, String readOnly,
			String readOnlyConditionExpression, boolean required)
		throws PortalException {

		getService().validateReadOnlyAndReadOnlyConditionExpression(
			businessType, readOnly, readOnlyConditionExpression, required);
	}

	public static void validateRequired(
			String businessType, boolean objectDefinitionApproved,
			ObjectField oldObjectField, boolean required)
		throws PortalException {

		getService().validateRequired(
			businessType, objectDefinitionApproved, oldObjectField, required);
	}

	public static ObjectFieldLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectFieldLocalService> _serviceSnapshot =
		new Snapshot<>(
			ObjectFieldLocalServiceUtil.class, ObjectFieldLocalService.class);

}