/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service;

import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for LayoutPageTemplateStructure. This utility wraps
 * <code>com.liferay.layout.page.template.service.impl.LayoutPageTemplateStructureLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateStructureLocalService
 * @generated
 */
public class LayoutPageTemplateStructureLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.layout.page.template.service.impl.LayoutPageTemplateStructureLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the layout page template structure to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructure the layout page template structure
	 * @return the layout page template structure that was added
	 */
	public static LayoutPageTemplateStructure addLayoutPageTemplateStructure(
		LayoutPageTemplateStructure layoutPageTemplateStructure) {

		return getService().addLayoutPageTemplateStructure(
			layoutPageTemplateStructure);
	}

	public static LayoutPageTemplateStructure addLayoutPageTemplateStructure(
			long userId, long groupId, long plid, long segmentsExperienceId,
			String data,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayoutPageTemplateStructure(
			userId, groupId, plid, segmentsExperienceId, data, serviceContext);
	}

	/**
	 * Creates a new layout page template structure with the primary key. Does not add the layout page template structure to the database.
	 *
	 * @param layoutPageTemplateStructureId the primary key for the new layout page template structure
	 * @return the new layout page template structure
	 */
	public static LayoutPageTemplateStructure createLayoutPageTemplateStructure(
		long layoutPageTemplateStructureId) {

		return getService().createLayoutPageTemplateStructure(
			layoutPageTemplateStructureId);
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
	 * Deletes the layout page template structure from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructure the layout page template structure
	 * @return the layout page template structure that was removed
	 */
	public static LayoutPageTemplateStructure deleteLayoutPageTemplateStructure(
		LayoutPageTemplateStructure layoutPageTemplateStructure) {

		return getService().deleteLayoutPageTemplateStructure(
			layoutPageTemplateStructure);
	}

	/**
	 * Deletes the layout page template structure with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureId the primary key of the layout page template structure
	 * @return the layout page template structure that was removed
	 * @throws PortalException if a layout page template structure with the primary key could not be found
	 */
	public static LayoutPageTemplateStructure deleteLayoutPageTemplateStructure(
			long layoutPageTemplateStructureId)
		throws PortalException {

		return getService().deleteLayoutPageTemplateStructure(
			layoutPageTemplateStructureId);
	}

	public static LayoutPageTemplateStructure deleteLayoutPageTemplateStructure(
			long groupId, long plid)
		throws PortalException {

		return getService().deleteLayoutPageTemplateStructure(groupId, plid);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureModelImpl</code>.
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

	public static LayoutPageTemplateStructure fetchLayoutPageTemplateStructure(
		long layoutPageTemplateStructureId) {

		return getService().fetchLayoutPageTemplateStructure(
			layoutPageTemplateStructureId);
	}

	public static LayoutPageTemplateStructure fetchLayoutPageTemplateStructure(
		long groupId, long plid) {

		return getService().fetchLayoutPageTemplateStructure(groupId, plid);
	}

	/**
	 * Returns the layout page template structure matching the UUID and group.
	 *
	 * @param uuid the layout page template structure's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout page template structure, or <code>null</code> if a matching layout page template structure could not be found
	 */
	public static LayoutPageTemplateStructure
		fetchLayoutPageTemplateStructureByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchLayoutPageTemplateStructureByUuidAndGroupId(
			uuid, groupId);
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
	 * Returns the layout page template structure with the primary key.
	 *
	 * @param layoutPageTemplateStructureId the primary key of the layout page template structure
	 * @return the layout page template structure
	 * @throws PortalException if a layout page template structure with the primary key could not be found
	 */
	public static LayoutPageTemplateStructure getLayoutPageTemplateStructure(
			long layoutPageTemplateStructureId)
		throws PortalException {

		return getService().getLayoutPageTemplateStructure(
			layoutPageTemplateStructureId);
	}

	/**
	 * Returns the layout page template structure matching the UUID and group.
	 *
	 * @param uuid the layout page template structure's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout page template structure
	 * @throws PortalException if a matching layout page template structure could not be found
	 */
	public static LayoutPageTemplateStructure
			getLayoutPageTemplateStructureByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getLayoutPageTemplateStructureByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the layout page template structures.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout page template structures
	 * @param end the upper bound of the range of layout page template structures (not inclusive)
	 * @return the range of layout page template structures
	 */
	public static List<LayoutPageTemplateStructure>
		getLayoutPageTemplateStructures(int start, int end) {

		return getService().getLayoutPageTemplateStructures(start, end);
	}

	/**
	 * Returns all the layout page template structures matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout page template structures
	 * @param companyId the primary key of the company
	 * @return the matching layout page template structures, or an empty list if no matches were found
	 */
	public static List<LayoutPageTemplateStructure>
		getLayoutPageTemplateStructuresByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getLayoutPageTemplateStructuresByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of layout page template structures matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout page template structures
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of layout page template structures
	 * @param end the upper bound of the range of layout page template structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching layout page template structures, or an empty list if no matches were found
	 */
	public static List<LayoutPageTemplateStructure>
		getLayoutPageTemplateStructuresByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<LayoutPageTemplateStructure> orderByComparator) {

		return getService().getLayoutPageTemplateStructuresByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of layout page template structures.
	 *
	 * @return the number of layout page template structures
	 */
	public static int getLayoutPageTemplateStructuresCount() {
		return getService().getLayoutPageTemplateStructuresCount();
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
	 * Updates the layout page template structure in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructure the layout page template structure
	 * @return the layout page template structure that was updated
	 */
	public static LayoutPageTemplateStructure updateLayoutPageTemplateStructure(
		LayoutPageTemplateStructure layoutPageTemplateStructure) {

		return getService().updateLayoutPageTemplateStructure(
			layoutPageTemplateStructure);
	}

	public static LayoutPageTemplateStructure
			updateLayoutPageTemplateStructureData(
				long userId, long groupId, long plid, long segmentsExperienceId,
				String data)
		throws PortalException {

		return getService().updateLayoutPageTemplateStructureData(
			userId, groupId, plid, segmentsExperienceId, data);
	}

	public static LayoutPageTemplateStructure
			updateLayoutPageTemplateStructureData(
				long userId, long groupId, long plid, String data)
		throws PortalException {

		return getService().updateLayoutPageTemplateStructureData(
			userId, groupId, plid, data);
	}

	public static LayoutPageTemplateStructureLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<LayoutPageTemplateStructureLocalService>
		_serviceSnapshot = new Snapshot<>(
			LayoutPageTemplateStructureLocalServiceUtil.class,
			LayoutPageTemplateStructureLocalService.class);

}