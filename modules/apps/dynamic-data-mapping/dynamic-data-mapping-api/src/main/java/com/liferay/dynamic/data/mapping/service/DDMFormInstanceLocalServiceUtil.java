/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
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
 * Provides the local service utility for DDMFormInstance. This utility wraps
 * <code>com.liferay.dynamic.data.mapping.service.impl.DDMFormInstanceLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DDMFormInstanceLocalService
 * @generated
 */
public class DDMFormInstanceLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.dynamic.data.mapping.service.impl.DDMFormInstanceLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the ddm form instance to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFormInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmFormInstance the ddm form instance
	 * @return the ddm form instance that was added
	 */
	public static DDMFormInstance addDDMFormInstance(
		DDMFormInstance ddmFormInstance) {

		return getService().addDDMFormInstance(ddmFormInstance);
	}

	public static DDMFormInstance addFormInstance(
			long userId, long groupId, long ddmStructureId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			com.liferay.dynamic.data.mapping.storage.DDMFormValues
				settingsDDMFormValues,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFormInstance(
			userId, groupId, ddmStructureId, nameMap, descriptionMap,
			settingsDDMFormValues, serviceContext);
	}

	public static DDMFormInstance addFormInstance(
			long userId, long groupId, long ddmStructureId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			String serializedSettingsDDMFormValues,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFormInstance(
			userId, groupId, ddmStructureId, nameMap, descriptionMap,
			serializedSettingsDDMFormValues, serviceContext);
	}

	public static DDMFormInstance addFormInstance(
			long userId, long groupId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			com.liferay.dynamic.data.mapping.model.DDMForm ddmForm,
			com.liferay.dynamic.data.mapping.model.DDMFormLayout ddmFormLayout,
			com.liferay.dynamic.data.mapping.storage.DDMFormValues
				settingsDDMFormValues,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFormInstance(
			userId, groupId, nameMap, descriptionMap, ddmForm, ddmFormLayout,
			settingsDDMFormValues, serviceContext);
	}

	public static void addFormInstanceResources(
			DDMFormInstance ddmFormInstance, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addFormInstanceResources(
			ddmFormInstance, addGroupPermissions, addGuestPermissions);
	}

	public static void addFormInstanceResources(
			DDMFormInstance ddmFormInstance,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().addFormInstanceResources(
			ddmFormInstance, modelPermissions);
	}

	public static DDMFormInstance copyFormInstance(
			long userId, long groupId, Map<java.util.Locale, String> nameMap,
			DDMFormInstance sourceDDMFormInstance,
			com.liferay.dynamic.data.mapping.storage.DDMFormValues
				settingsDDMFormValues,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().copyFormInstance(
			userId, groupId, nameMap, sourceDDMFormInstance,
			settingsDDMFormValues, serviceContext);
	}

	/**
	 * Creates a new ddm form instance with the primary key. Does not add the ddm form instance to the database.
	 *
	 * @param formInstanceId the primary key for the new ddm form instance
	 * @return the new ddm form instance
	 */
	public static DDMFormInstance createDDMFormInstance(long formInstanceId) {
		return getService().createDDMFormInstance(formInstanceId);
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
	 * Deletes the ddm form instance from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFormInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmFormInstance the ddm form instance
	 * @return the ddm form instance that was removed
	 */
	public static DDMFormInstance deleteDDMFormInstance(
		DDMFormInstance ddmFormInstance) {

		return getService().deleteDDMFormInstance(ddmFormInstance);
	}

	/**
	 * Deletes the ddm form instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFormInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param formInstanceId the primary key of the ddm form instance
	 * @return the ddm form instance that was removed
	 * @throws PortalException if a ddm form instance with the primary key could not be found
	 */
	public static DDMFormInstance deleteDDMFormInstance(long formInstanceId)
		throws PortalException {

		return getService().deleteDDMFormInstance(formInstanceId);
	}

	public static void deleteFormInstance(DDMFormInstance ddmFormInstance)
		throws PortalException {

		getService().deleteFormInstance(ddmFormInstance);
	}

	public static void deleteFormInstance(long ddmFormInstanceId)
		throws PortalException {

		getService().deleteFormInstance(ddmFormInstanceId);
	}

	public static void deleteFormInstances(long groupId)
		throws PortalException {

		getService().deleteFormInstances(groupId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceModelImpl</code>.
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

	public static DDMFormInstance fetchDDMFormInstance(long formInstanceId) {
		return getService().fetchDDMFormInstance(formInstanceId);
	}

	/**
	 * Returns the ddm form instance matching the UUID and group.
	 *
	 * @param uuid the ddm form instance's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm form instance, or <code>null</code> if a matching ddm form instance could not be found
	 */
	public static DDMFormInstance fetchDDMFormInstanceByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchDDMFormInstanceByUuidAndGroupId(uuid, groupId);
	}

	public static DDMFormInstance fetchFormInstance(long ddmFormInstanceId) {
		return getService().fetchFormInstance(ddmFormInstanceId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the ddm form instance with the primary key.
	 *
	 * @param formInstanceId the primary key of the ddm form instance
	 * @return the ddm form instance
	 * @throws PortalException if a ddm form instance with the primary key could not be found
	 */
	public static DDMFormInstance getDDMFormInstance(long formInstanceId)
		throws PortalException {

		return getService().getDDMFormInstance(formInstanceId);
	}

	/**
	 * Returns the ddm form instance matching the UUID and group.
	 *
	 * @param uuid the ddm form instance's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm form instance
	 * @throws PortalException if a matching ddm form instance could not be found
	 */
	public static DDMFormInstance getDDMFormInstanceByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getDDMFormInstanceByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the ddm form instances.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm form instances
	 * @param end the upper bound of the range of ddm form instances (not inclusive)
	 * @return the range of ddm form instances
	 */
	public static List<DDMFormInstance> getDDMFormInstances(
		int start, int end) {

		return getService().getDDMFormInstances(start, end);
	}

	/**
	 * Returns all the ddm form instances matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm form instances
	 * @param companyId the primary key of the company
	 * @return the matching ddm form instances, or an empty list if no matches were found
	 */
	public static List<DDMFormInstance> getDDMFormInstancesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getDDMFormInstancesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of ddm form instances matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm form instances
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of ddm form instances
	 * @param end the upper bound of the range of ddm form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching ddm form instances, or an empty list if no matches were found
	 */
	public static List<DDMFormInstance> getDDMFormInstancesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator) {

		return getService().getDDMFormInstancesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of ddm form instances.
	 *
	 * @return the number of ddm form instances
	 */
	public static int getDDMFormInstancesCount() {
		return getService().getDDMFormInstancesCount();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static DDMFormInstance getFormInstance(long ddmFormInstanceId)
		throws PortalException {

		return getService().getFormInstance(ddmFormInstanceId);
	}

	public static DDMFormInstance getFormInstance(
			String uuid, long ddmFormInstanceId)
		throws PortalException {

		return getService().getFormInstance(uuid, ddmFormInstanceId);
	}

	public static DDMFormInstance getFormInstanceByStructureId(long structureId)
		throws PortalException {

		return getService().getFormInstanceByStructureId(structureId);
	}

	public static List<DDMFormInstance> getFormInstances(long groupId) {
		return getService().getFormInstances(groupId);
	}

	public static int getFormInstancesCount(long groupId) {
		return getService().getFormInstancesCount(groupId);
	}

	public static int getFormInstancesCount(String uuid)
		throws PortalException {

		return getService().getFormInstancesCount(uuid);
	}

	public static com.liferay.dynamic.data.mapping.storage.DDMFormValues
		getFormInstanceSettingsFormValues(DDMFormInstance formInstance) {

		return getService().getFormInstanceSettingsFormValues(formInstance);
	}

	public static com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings
			getFormInstanceSettingsModel(DDMFormInstance formInstance)
		throws PortalException {

		return getService().getFormInstanceSettingsModel(formInstance);
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

	public static List<DDMFormInstance> search(
		long companyId, long groupId, String keywords, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator) {

		return getService().search(
			companyId, groupId, keywords, start, end, orderByComparator);
	}

	public static List<DDMFormInstance> search(
		long companyId, long groupId, String[] names, String[] descriptions,
		boolean andOperator, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator) {

		return getService().search(
			companyId, groupId, names, descriptions, andOperator, start, end,
			orderByComparator);
	}

	public static int searchCount(
		long companyId, long groupId, String keywords) {

		return getService().searchCount(companyId, groupId, keywords);
	}

	public static int searchCount(
		long companyId, long groupId, String[] names, String[] descriptions,
		boolean andOperator) {

		return getService().searchCount(
			companyId, groupId, names, descriptions, andOperator);
	}

	public static void sendEmail(
			long userId, String message, String subject,
			String[] toEmailAddresses)
		throws Exception {

		getService().sendEmail(userId, message, subject, toEmailAddresses);
	}

	/**
	 * Updates the ddm form instance in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFormInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmFormInstance the ddm form instance
	 * @return the ddm form instance that was updated
	 */
	public static DDMFormInstance updateDDMFormInstance(
		DDMFormInstance ddmFormInstance) {

		return getService().updateDDMFormInstance(ddmFormInstance);
	}

	public static DDMFormInstance updateFormInstance(
			long formInstanceId,
			com.liferay.dynamic.data.mapping.storage.DDMFormValues
				settingsDDMFormValues)
		throws PortalException {

		return getService().updateFormInstance(
			formInstanceId, settingsDDMFormValues);
	}

	public static DDMFormInstance updateFormInstance(
			long userId, long ddmFormInstanceId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			com.liferay.dynamic.data.mapping.model.DDMForm ddmForm,
			com.liferay.dynamic.data.mapping.model.DDMFormLayout ddmFormLayout,
			com.liferay.dynamic.data.mapping.storage.DDMFormValues
				settingsDDMFormValues,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFormInstance(
			userId, ddmFormInstanceId, nameMap, descriptionMap, ddmForm,
			ddmFormLayout, settingsDDMFormValues, serviceContext);
	}

	public static DDMFormInstance updateFormInstance(
			long ddmFormInstanceId, long ddmStructureId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			com.liferay.dynamic.data.mapping.storage.DDMFormValues
				settingsDDMFormValues,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFormInstance(
			ddmFormInstanceId, ddmStructureId, nameMap, descriptionMap,
			settingsDDMFormValues, serviceContext);
	}

	public static DDMFormInstanceLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DDMFormInstanceLocalService>
		_serviceSnapshot = new Snapshot<>(
			DDMFormInstanceLocalServiceUtil.class,
			DDMFormInstanceLocalService.class);

}