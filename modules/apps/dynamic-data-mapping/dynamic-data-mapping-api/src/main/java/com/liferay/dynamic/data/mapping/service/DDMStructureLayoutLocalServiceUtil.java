/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
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
 * Provides the local service utility for DDMStructureLayout. This utility wraps
 * <code>com.liferay.dynamic.data.mapping.service.impl.DDMStructureLayoutLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DDMStructureLayoutLocalService
 * @generated
 */
public class DDMStructureLayoutLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.dynamic.data.mapping.service.impl.DDMStructureLayoutLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the ddm structure layout to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructureLayout the ddm structure layout
	 * @return the ddm structure layout that was added
	 */
	public static DDMStructureLayout addDDMStructureLayout(
		DDMStructureLayout ddmStructureLayout) {

		return getService().addDDMStructureLayout(ddmStructureLayout);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #addStructureLayout(long, long, long, String, long,
	 DDMFormLayout, ServiceContext)}
	 */
	@Deprecated
	public static DDMStructureLayout addStructureLayout(
			long userId, long groupId, long structureVersionId,
			com.liferay.dynamic.data.mapping.model.DDMFormLayout ddmFormLayout,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addStructureLayout(
			userId, groupId, structureVersionId, ddmFormLayout, serviceContext);
	}

	public static DDMStructureLayout addStructureLayout(
			long userId, long groupId, long classNameId,
			String structureLayoutKey, long structureVersionId,
			com.liferay.dynamic.data.mapping.model.DDMFormLayout ddmFormLayout,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addStructureLayout(
			userId, groupId, classNameId, structureLayoutKey,
			structureVersionId, ddmFormLayout, serviceContext);
	}

	public static DDMStructureLayout addStructureLayout(
			long userId, long groupId, long classNameId,
			String structureLayoutKey, long structureVersionId,
			Map<java.util.Locale, String> name,
			Map<java.util.Locale, String> description, String definition,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addStructureLayout(
			userId, groupId, classNameId, structureLayoutKey,
			structureVersionId, name, description, definition, serviceContext);
	}

	/**
	 * Creates a new ddm structure layout with the primary key. Does not add the ddm structure layout to the database.
	 *
	 * @param structureLayoutId the primary key for the new ddm structure layout
	 * @return the new ddm structure layout
	 */
	public static DDMStructureLayout createDDMStructureLayout(
		long structureLayoutId) {

		return getService().createDDMStructureLayout(structureLayoutId);
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
	 * Deletes the ddm structure layout from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructureLayout the ddm structure layout
	 * @return the ddm structure layout that was removed
	 */
	public static DDMStructureLayout deleteDDMStructureLayout(
		DDMStructureLayout ddmStructureLayout) {

		return getService().deleteDDMStructureLayout(ddmStructureLayout);
	}

	/**
	 * Deletes the ddm structure layout with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param structureLayoutId the primary key of the ddm structure layout
	 * @return the ddm structure layout that was removed
	 * @throws PortalException if a ddm structure layout with the primary key could not be found
	 */
	public static DDMStructureLayout deleteDDMStructureLayout(
			long structureLayoutId)
		throws PortalException {

		return getService().deleteDDMStructureLayout(structureLayoutId);
	}

	public static void deleteDDMStructureLayouts(
			long classNameId,
			com.liferay.dynamic.data.mapping.model.DDMStructureVersion
				ddmStructureVersion)
		throws PortalException {

		getService().deleteDDMStructureLayouts(
			classNameId, ddmStructureVersion);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteStructureLayout(
		DDMStructureLayout structureLayout) {

		getService().deleteStructureLayout(structureLayout);
	}

	public static void deleteStructureLayout(long structureLayoutId)
		throws PortalException {

		getService().deleteStructureLayout(structureLayoutId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutModelImpl</code>.
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

	public static DDMStructureLayout fetchDDMStructureLayout(
		long structureLayoutId) {

		return getService().fetchDDMStructureLayout(structureLayoutId);
	}

	/**
	 * Returns the ddm structure layout matching the UUID and group.
	 *
	 * @param uuid the ddm structure layout's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm structure layout, or <code>null</code> if a matching ddm structure layout could not be found
	 */
	public static DDMStructureLayout fetchDDMStructureLayoutByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchDDMStructureLayoutByUuidAndGroupId(
			uuid, groupId);
	}

	public static DDMStructureLayout fetchStructureLayout(
		long structureLayoutId) {

		return getService().fetchStructureLayout(structureLayoutId);
	}

	public static DDMStructureLayout fetchStructureLayout(
		long groupId, long classNameId, String structureLayoutKey) {

		return getService().fetchStructureLayout(
			groupId, classNameId, structureLayoutKey);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the ddm structure layout with the primary key.
	 *
	 * @param structureLayoutId the primary key of the ddm structure layout
	 * @return the ddm structure layout
	 * @throws PortalException if a ddm structure layout with the primary key could not be found
	 */
	public static DDMStructureLayout getDDMStructureLayout(
			long structureLayoutId)
		throws PortalException {

		return getService().getDDMStructureLayout(structureLayoutId);
	}

	/**
	 * Returns the ddm structure layout matching the UUID and group.
	 *
	 * @param uuid the ddm structure layout's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm structure layout
	 * @throws PortalException if a matching ddm structure layout could not be found
	 */
	public static DDMStructureLayout getDDMStructureLayoutByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getDDMStructureLayoutByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the ddm structure layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm structure layouts
	 * @param end the upper bound of the range of ddm structure layouts (not inclusive)
	 * @return the range of ddm structure layouts
	 */
	public static List<DDMStructureLayout> getDDMStructureLayouts(
		int start, int end) {

		return getService().getDDMStructureLayouts(start, end);
	}

	/**
	 * Returns all the ddm structure layouts matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm structure layouts
	 * @param companyId the primary key of the company
	 * @return the matching ddm structure layouts, or an empty list if no matches were found
	 */
	public static List<DDMStructureLayout>
		getDDMStructureLayoutsByUuidAndCompanyId(String uuid, long companyId) {

		return getService().getDDMStructureLayoutsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of ddm structure layouts matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm structure layouts
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of ddm structure layouts
	 * @param end the upper bound of the range of ddm structure layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching ddm structure layouts, or an empty list if no matches were found
	 */
	public static List<DDMStructureLayout>
		getDDMStructureLayoutsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<DDMStructureLayout> orderByComparator) {

		return getService().getDDMStructureLayoutsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of ddm structure layouts.
	 *
	 * @return the number of ddm structure layouts
	 */
	public static int getDDMStructureLayoutsCount() {
		return getService().getDDMStructureLayoutsCount();
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

	public static DDMStructureLayout getStructureLayout(long structureLayoutId)
		throws PortalException {

		return getService().getStructureLayout(structureLayoutId);
	}

	public static DDMStructureLayout getStructureLayout(
			long groupId, long classNameId, String structureLayoutKey)
		throws PortalException {

		return getService().getStructureLayout(
			groupId, classNameId, structureLayoutKey);
	}

	public static DDMStructureLayout getStructureLayoutByStructureVersionId(
			long structureVersionId)
		throws PortalException {

		return getService().getStructureLayoutByStructureVersionId(
			structureVersionId);
	}

	public static com.liferay.dynamic.data.mapping.model.DDMFormLayout
		getStructureLayoutDDMFormLayout(DDMStructureLayout structureLayout) {

		return getService().getStructureLayoutDDMFormLayout(structureLayout);
	}

	public static List<DDMStructureLayout> getStructureLayouts(
			long groupId, int start, int end)
		throws PortalException {

		return getService().getStructureLayouts(groupId, start, end);
	}

	public static List<DDMStructureLayout> getStructureLayouts(
		long groupId, long classNameId, long structureVersionId) {

		return getService().getStructureLayouts(
			groupId, classNameId, structureVersionId);
	}

	public static List<DDMStructureLayout> getStructureLayouts(
		long groupId, long classNameId, long structureVersionId, int start,
		int end, OrderByComparator<DDMStructureLayout> orderByComparator) {

		return getService().getStructureLayouts(
			groupId, classNameId, structureVersionId, start, end,
			orderByComparator);
	}

	public static int getStructureLayoutsCount(long groupId) {
		return getService().getStructureLayoutsCount(groupId);
	}

	public static int getStructureLayoutsCount(
		long groupId, long classNameId, long structureVersionId) {

		return getService().getStructureLayoutsCount(
			groupId, classNameId, structureVersionId);
	}

	/**
	 * Updates the ddm structure layout in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructureLayout the ddm structure layout
	 * @return the ddm structure layout that was updated
	 */
	public static DDMStructureLayout updateDDMStructureLayout(
		DDMStructureLayout ddmStructureLayout) {

		return getService().updateDDMStructureLayout(ddmStructureLayout);
	}

	public static DDMStructureLayout updateStructureLayout(
			long structureLayoutId,
			com.liferay.dynamic.data.mapping.model.DDMFormLayout ddmFormLayout,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateStructureLayout(
			structureLayoutId, ddmFormLayout, serviceContext);
	}

	public static DDMStructureLayout updateStructureLayout(
			long structureLayoutId, long structureVersionId,
			Map<java.util.Locale, String> name,
			Map<java.util.Locale, String> description, String definition,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateStructureLayout(
			structureLayoutId, structureVersionId, name, description,
			definition, serviceContext);
	}

	public static DDMStructureLayoutLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DDMStructureLayoutLocalService>
		_serviceSnapshot = new Snapshot<>(
			DDMStructureLayoutLocalServiceUtil.class,
			DDMStructureLayoutLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-858363400