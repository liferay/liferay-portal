/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMField;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DDMFieldLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DDMFieldLocalService
 * @generated
 */
public class DDMFieldLocalServiceWrapper
	implements DDMFieldLocalService, ServiceWrapper<DDMFieldLocalService> {

	public DDMFieldLocalServiceWrapper() {
		this(null);
	}

	public DDMFieldLocalServiceWrapper(
		DDMFieldLocalService ddmFieldLocalService) {

		_ddmFieldLocalService = ddmFieldLocalService;
	}

	/**
	 * Adds the ddm field to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFieldLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmField the ddm field
	 * @return the ddm field that was added
	 */
	@Override
	public DDMField addDDMField(DDMField ddmField) {
		return _ddmFieldLocalService.addDDMField(ddmField);
	}

	/**
	 * Creates a new ddm field with the primary key. Does not add the ddm field to the database.
	 *
	 * @param fieldId the primary key for the new ddm field
	 * @return the new ddm field
	 */
	@Override
	public DDMField createDDMField(long fieldId) {
		return _ddmFieldLocalService.createDDMField(fieldId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmFieldLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the ddm field from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFieldLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmField the ddm field
	 * @return the ddm field that was removed
	 */
	@Override
	public DDMField deleteDDMField(DDMField ddmField) {
		return _ddmFieldLocalService.deleteDDMField(ddmField);
	}

	/**
	 * Deletes the ddm field with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFieldLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fieldId the primary key of the ddm field
	 * @return the ddm field that was removed
	 * @throws PortalException if a ddm field with the primary key could not be found
	 */
	@Override
	public DDMField deleteDDMField(long fieldId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmFieldLocalService.deleteDDMField(fieldId);
	}

	@Override
	public void deleteDDMFormValues(long storageId) {
		_ddmFieldLocalService.deleteDDMFormValues(storageId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmFieldLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ddmFieldLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ddmFieldLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ddmFieldLocalService.dynamicQuery();
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

		return _ddmFieldLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldModelImpl</code>.
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

		return _ddmFieldLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldModelImpl</code>.
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

		return _ddmFieldLocalService.dynamicQuery(
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

		return _ddmFieldLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ddmFieldLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DDMField fetchDDMField(long fieldId) {
		return _ddmFieldLocalService.fetchDDMField(fieldId);
	}

	@Override
	public com.liferay.dynamic.data.mapping.model.DDMFieldAttribute
		fetchDDMFieldAttribute(
			long fieldId, String attributeName, String languageId) {

		return _ddmFieldLocalService.fetchDDMFieldAttribute(
			fieldId, attributeName, languageId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ddmFieldLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the ddm field with the primary key.
	 *
	 * @param fieldId the primary key of the ddm field
	 * @return the ddm field
	 * @throws PortalException if a ddm field with the primary key could not be found
	 */
	@Override
	public DDMField getDDMField(long fieldId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmFieldLocalService.getDDMField(fieldId);
	}

	@Override
	public java.util.List
		<com.liferay.dynamic.data.mapping.model.DDMFieldAttribute>
			getDDMFieldAttributes(long storageId, String attributeName) {

		return _ddmFieldLocalService.getDDMFieldAttributes(
			storageId, attributeName);
	}

	/**
	 * Returns a range of all the ddm fields.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm fields
	 * @param end the upper bound of the range of ddm fields (not inclusive)
	 * @return the range of ddm fields
	 */
	@Override
	public java.util.List<DDMField> getDDMFields(int start, int end) {
		return _ddmFieldLocalService.getDDMFields(start, end);
	}

	@Override
	public java.util.List<DDMField> getDDMFields(
		long storageId, String fieldName) {

		return _ddmFieldLocalService.getDDMFields(storageId, fieldName);
	}

	/**
	 * Returns the number of ddm fields.
	 *
	 * @return the number of ddm fields
	 */
	@Override
	public int getDDMFieldsCount() {
		return _ddmFieldLocalService.getDDMFieldsCount();
	}

	@Override
	public com.liferay.dynamic.data.mapping.storage.DDMFormValues
		getDDMFormValues(
			com.liferay.dynamic.data.mapping.model.DDMForm ddmForm,
			long storageId) {

		return _ddmFieldLocalService.getDDMFormValues(ddmForm, storageId);
	}

	@Override
	public com.liferay.dynamic.data.mapping.storage.DDMFormValues
		getDDMFormValues(
			com.liferay.dynamic.data.mapping.model.DDMForm ddmForm,
			long storageId, String languageId) {

		return _ddmFieldLocalService.getDDMFormValues(
			ddmForm, storageId, languageId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ddmFieldLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ddmFieldLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmFieldLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the ddm field in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFieldLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmField the ddm field
	 * @return the ddm field that was updated
	 */
	@Override
	public DDMField updateDDMField(DDMField ddmField) {
		return _ddmFieldLocalService.updateDDMField(ddmField);
	}

	@Override
	public void updateDDMFormValues(
			long structureId, long storageId,
			com.liferay.dynamic.data.mapping.storage.DDMFormValues
				ddmFormValues)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ddmFieldLocalService.updateDDMFormValues(
			structureId, storageId, ddmFormValues);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ddmFieldLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DDMField> getCTPersistence() {
		return _ddmFieldLocalService.getCTPersistence();
	}

	@Override
	public Class<DDMField> getModelClass() {
		return _ddmFieldLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DDMField>, R, E> updateUnsafeFunction)
		throws E {

		return _ddmFieldLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DDMFieldLocalService getWrappedService() {
		return _ddmFieldLocalService;
	}

	@Override
	public void setWrappedService(DDMFieldLocalService ddmFieldLocalService) {
		_ddmFieldLocalService = ddmFieldLocalService;
	}

	private DDMFieldLocalService _ddmFieldLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:682595537