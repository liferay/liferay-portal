/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMField;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for DDMField. This utility wraps
 * <code>com.liferay.dynamic.data.mapping.service.impl.DDMFieldLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DDMFieldLocalService
 * @generated
 */
public class DDMFieldLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.dynamic.data.mapping.service.impl.DDMFieldLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static DDMField addDDMField(DDMField ddmField) {
		return getService().addDDMField(ddmField);
	}

	/**
	 * Creates a new ddm field with the primary key. Does not add the ddm field to the database.
	 *
	 * @param fieldId the primary key for the new ddm field
	 * @return the new ddm field
	 */
	public static DDMField createDDMField(long fieldId) {
		return getService().createDDMField(fieldId);
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
	 * Deletes the ddm field from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFieldLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmField the ddm field
	 * @return the ddm field that was removed
	 */
	public static DDMField deleteDDMField(DDMField ddmField) {
		return getService().deleteDDMField(ddmField);
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
	public static DDMField deleteDDMField(long fieldId) throws PortalException {
		return getService().deleteDDMField(fieldId);
	}

	public static void deleteDDMFormValues(long storageId) {
		getService().deleteDDMFormValues(storageId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldModelImpl</code>.
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

	public static DDMField fetchDDMField(long fieldId) {
		return getService().fetchDDMField(fieldId);
	}

	public static com.liferay.dynamic.data.mapping.model.DDMFieldAttribute
		fetchDDMFieldAttribute(
			long fieldId, String attributeName, String languageId) {

		return getService().fetchDDMFieldAttribute(
			fieldId, attributeName, languageId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the ddm field with the primary key.
	 *
	 * @param fieldId the primary key of the ddm field
	 * @return the ddm field
	 * @throws PortalException if a ddm field with the primary key could not be found
	 */
	public static DDMField getDDMField(long fieldId) throws PortalException {
		return getService().getDDMField(fieldId);
	}

	public static List<com.liferay.dynamic.data.mapping.model.DDMFieldAttribute>
		getDDMFieldAttributes(long storageId, String attributeName) {

		return getService().getDDMFieldAttributes(storageId, attributeName);
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
	public static List<DDMField> getDDMFields(int start, int end) {
		return getService().getDDMFields(start, end);
	}

	public static List<DDMField> getDDMFields(
		long storageId, String fieldName) {

		return getService().getDDMFields(storageId, fieldName);
	}

	/**
	 * Returns the number of ddm fields.
	 *
	 * @return the number of ddm fields
	 */
	public static int getDDMFieldsCount() {
		return getService().getDDMFieldsCount();
	}

	public static com.liferay.dynamic.data.mapping.storage.DDMFormValues
		getDDMFormValues(
			com.liferay.dynamic.data.mapping.model.DDMForm ddmForm,
			long storageId) {

		return getService().getDDMFormValues(ddmForm, storageId);
	}

	public static com.liferay.dynamic.data.mapping.storage.DDMFormValues
		getDDMFormValues(
			com.liferay.dynamic.data.mapping.model.DDMForm ddmForm,
			long storageId, String languageId) {

		return getService().getDDMFormValues(ddmForm, storageId, languageId);
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
	 * Updates the ddm field in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFieldLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmField the ddm field
	 * @return the ddm field that was updated
	 */
	public static DDMField updateDDMField(DDMField ddmField) {
		return getService().updateDDMField(ddmField);
	}

	public static void updateDDMFormValues(
			long structureId, long storageId,
			com.liferay.dynamic.data.mapping.storage.DDMFormValues
				ddmFormValues)
		throws PortalException {

		getService().updateDDMFormValues(structureId, storageId, ddmFormValues);
	}

	public static DDMFieldLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DDMFieldLocalService> _serviceSnapshot =
		new Snapshot<>(
			DDMFieldLocalServiceUtil.class, DDMFieldLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1970525551