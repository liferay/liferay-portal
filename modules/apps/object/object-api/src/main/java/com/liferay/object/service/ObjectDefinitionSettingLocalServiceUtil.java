/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for ObjectDefinitionSetting. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectDefinitionSettingLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see ObjectDefinitionSettingLocalService
 * @generated
 */
public class ObjectDefinitionSettingLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectDefinitionSettingLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ObjectDefinitionSetting addObjectDefinitionSetting(
			long userId, long objectDefinitionId, String name, String value)
		throws PortalException {

		return getService().addObjectDefinitionSetting(
			userId, objectDefinitionId, name, value);
	}

	/**
	 * Adds the object definition setting to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectDefinitionSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectDefinitionSetting the object definition setting
	 * @return the object definition setting that was added
	 */
	public static ObjectDefinitionSetting addObjectDefinitionSetting(
		ObjectDefinitionSetting objectDefinitionSetting) {

		return getService().addObjectDefinitionSetting(objectDefinitionSetting);
	}

	/**
	 * Creates a new object definition setting with the primary key. Does not add the object definition setting to the database.
	 *
	 * @param objectDefinitionSettingId the primary key for the new object definition setting
	 * @return the new object definition setting
	 */
	public static ObjectDefinitionSetting createObjectDefinitionSetting(
		long objectDefinitionSettingId) {

		return getService().createObjectDefinitionSetting(
			objectDefinitionSettingId);
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
	 * Deletes the object definition setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectDefinitionSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectDefinitionSettingId the primary key of the object definition setting
	 * @return the object definition setting that was removed
	 * @throws PortalException if a object definition setting with the primary key could not be found
	 */
	public static ObjectDefinitionSetting deleteObjectDefinitionSetting(
			long objectDefinitionSettingId)
		throws PortalException {

		return getService().deleteObjectDefinitionSetting(
			objectDefinitionSettingId);
	}

	/**
	 * Deletes the object definition setting from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectDefinitionSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectDefinitionSetting the object definition setting
	 * @return the object definition setting that was removed
	 */
	public static ObjectDefinitionSetting deleteObjectDefinitionSetting(
		ObjectDefinitionSetting objectDefinitionSetting) {

		return getService().deleteObjectDefinitionSetting(
			objectDefinitionSetting);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectDefinitionSettingModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectDefinitionSettingModelImpl</code>.
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

	public static ObjectDefinitionSetting fetchObjectDefinitionSetting(
		long objectDefinitionSettingId) {

		return getService().fetchObjectDefinitionSetting(
			objectDefinitionSettingId);
	}

	/**
	 * Returns the object definition setting with the matching UUID and company.
	 *
	 * @param uuid the object definition setting's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting
		fetchObjectDefinitionSettingByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchObjectDefinitionSettingByUuidAndCompanyId(
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
	 * Returns the object definition setting with the primary key.
	 *
	 * @param objectDefinitionSettingId the primary key of the object definition setting
	 * @return the object definition setting
	 * @throws PortalException if a object definition setting with the primary key could not be found
	 */
	public static ObjectDefinitionSetting getObjectDefinitionSetting(
			long objectDefinitionSettingId)
		throws PortalException {

		return getService().getObjectDefinitionSetting(
			objectDefinitionSettingId);
	}

	/**
	 * Returns the object definition setting with the matching UUID and company.
	 *
	 * @param uuid the object definition setting's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object definition setting
	 * @throws PortalException if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting
			getObjectDefinitionSettingByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getObjectDefinitionSettingByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the object definition settings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @return the range of object definition settings
	 */
	public static List<ObjectDefinitionSetting> getObjectDefinitionSettings(
		int start, int end) {

		return getService().getObjectDefinitionSettings(start, end);
	}

	public static List<ObjectDefinitionSetting> getObjectDefinitionSettings(
		long objectDefinitionId) {

		return getService().getObjectDefinitionSettings(objectDefinitionId);
	}

	/**
	 * Returns the number of object definition settings.
	 *
	 * @return the number of object definition settings
	 */
	public static int getObjectDefinitionSettingsCount() {
		return getService().getObjectDefinitionSettingsCount();
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
	 * Updates the object definition setting in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectDefinitionSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectDefinitionSetting the object definition setting
	 * @return the object definition setting that was updated
	 */
	public static ObjectDefinitionSetting updateObjectDefinitionSetting(
		ObjectDefinitionSetting objectDefinitionSetting) {

		return getService().updateObjectDefinitionSetting(
			objectDefinitionSetting);
	}

	public static ObjectDefinitionSettingLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectDefinitionSettingLocalService>
		_serviceSnapshot = new Snapshot<>(
			ObjectDefinitionSettingLocalServiceUtil.class,
			ObjectDefinitionSettingLocalService.class);

}