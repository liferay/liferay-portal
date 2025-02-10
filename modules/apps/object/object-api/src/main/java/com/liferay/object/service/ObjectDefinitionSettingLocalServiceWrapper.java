/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ObjectDefinitionSettingLocalService}.
 *
 * @author Marco Leo
 * @see ObjectDefinitionSettingLocalService
 * @generated
 */
public class ObjectDefinitionSettingLocalServiceWrapper
	implements ObjectDefinitionSettingLocalService,
			   ServiceWrapper<ObjectDefinitionSettingLocalService> {

	public ObjectDefinitionSettingLocalServiceWrapper() {
		this(null);
	}

	public ObjectDefinitionSettingLocalServiceWrapper(
		ObjectDefinitionSettingLocalService
			objectDefinitionSettingLocalService) {

		_objectDefinitionSettingLocalService =
			objectDefinitionSettingLocalService;
	}

	@Override
	public com.liferay.object.model.ObjectDefinitionSetting
			addObjectDefinitionSetting(
				long userId, long objectDefinitionId, String name, String value)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionSettingLocalService.addObjectDefinitionSetting(
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
	@Override
	public com.liferay.object.model.ObjectDefinitionSetting
		addObjectDefinitionSetting(
			com.liferay.object.model.ObjectDefinitionSetting
				objectDefinitionSetting) {

		return _objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			objectDefinitionSetting);
	}

	/**
	 * Creates a new object definition setting with the primary key. Does not add the object definition setting to the database.
	 *
	 * @param objectDefinitionSettingId the primary key for the new object definition setting
	 * @return the new object definition setting
	 */
	@Override
	public com.liferay.object.model.ObjectDefinitionSetting
		createObjectDefinitionSetting(long objectDefinitionSettingId) {

		return _objectDefinitionSettingLocalService.
			createObjectDefinitionSetting(objectDefinitionSettingId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionSettingLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.object.model.ObjectDefinitionSetting
			deleteObjectDefinitionSetting(long objectDefinitionSettingId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionSettingLocalService.
			deleteObjectDefinitionSetting(objectDefinitionSettingId);
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
	@Override
	public com.liferay.object.model.ObjectDefinitionSetting
		deleteObjectDefinitionSetting(
			com.liferay.object.model.ObjectDefinitionSetting
				objectDefinitionSetting) {

		return _objectDefinitionSettingLocalService.
			deleteObjectDefinitionSetting(objectDefinitionSetting);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionSettingLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _objectDefinitionSettingLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _objectDefinitionSettingLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _objectDefinitionSettingLocalService.dynamicQuery();
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

		return _objectDefinitionSettingLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _objectDefinitionSettingLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _objectDefinitionSettingLocalService.dynamicQuery(
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

		return _objectDefinitionSettingLocalService.dynamicQueryCount(
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

		return _objectDefinitionSettingLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.object.model.ObjectDefinitionSetting
		fetchObjectDefinitionSetting(long objectDefinitionSettingId) {

		return _objectDefinitionSettingLocalService.
			fetchObjectDefinitionSetting(objectDefinitionSettingId);
	}

	/**
	 * Returns the object definition setting with the matching UUID and company.
	 *
	 * @param uuid the object definition setting's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectDefinitionSetting
		fetchObjectDefinitionSettingByUuidAndCompanyId(
			String uuid, long companyId) {

		return _objectDefinitionSettingLocalService.
			fetchObjectDefinitionSettingByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _objectDefinitionSettingLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _objectDefinitionSettingLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _objectDefinitionSettingLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the object definition setting with the primary key.
	 *
	 * @param objectDefinitionSettingId the primary key of the object definition setting
	 * @return the object definition setting
	 * @throws PortalException if a object definition setting with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectDefinitionSetting
			getObjectDefinitionSetting(long objectDefinitionSettingId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionSettingLocalService.getObjectDefinitionSetting(
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
	@Override
	public com.liferay.object.model.ObjectDefinitionSetting
			getObjectDefinitionSettingByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionSettingLocalService.
			getObjectDefinitionSettingByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<com.liferay.object.model.ObjectDefinitionSetting>
		getObjectDefinitionSettings(int start, int end) {

		return _objectDefinitionSettingLocalService.getObjectDefinitionSettings(
			start, end);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectDefinitionSetting>
		getObjectDefinitionSettings(long objectDefinitionId) {

		return _objectDefinitionSettingLocalService.getObjectDefinitionSettings(
			objectDefinitionId);
	}

	/**
	 * Returns the number of object definition settings.
	 *
	 * @return the number of object definition settings
	 */
	@Override
	public int getObjectDefinitionSettingsCount() {
		return _objectDefinitionSettingLocalService.
			getObjectDefinitionSettingsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectDefinitionSettingLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionSettingLocalService.getPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.object.model.ObjectDefinitionSetting
		updateObjectDefinitionSetting(
			com.liferay.object.model.ObjectDefinitionSetting
				objectDefinitionSetting) {

		return _objectDefinitionSettingLocalService.
			updateObjectDefinitionSetting(objectDefinitionSetting);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _objectDefinitionSettingLocalService.getBasePersistence();
	}

	@Override
	public ObjectDefinitionSettingLocalService getWrappedService() {
		return _objectDefinitionSettingLocalService;
	}

	@Override
	public void setWrappedService(
		ObjectDefinitionSettingLocalService
			objectDefinitionSettingLocalService) {

		_objectDefinitionSettingLocalService =
			objectDefinitionSettingLocalService;
	}

	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

}