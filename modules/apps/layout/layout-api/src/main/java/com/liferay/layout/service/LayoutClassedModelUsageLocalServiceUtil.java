/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service;

import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for LayoutClassedModelUsage. This utility wraps
 * <code>com.liferay.layout.service.impl.LayoutClassedModelUsageLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutClassedModelUsageLocalService
 * @generated
 */
public class LayoutClassedModelUsageLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.layout.service.impl.LayoutClassedModelUsageLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the layout classed model usage to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutClassedModelUsageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutClassedModelUsage the layout classed model usage
	 * @return the layout classed model usage that was added
	 */
	public static LayoutClassedModelUsage addLayoutClassedModelUsage(
		LayoutClassedModelUsage layoutClassedModelUsage) {

		return getService().addLayoutClassedModelUsage(layoutClassedModelUsage);
	}

	public static LayoutClassedModelUsage addLayoutClassedModelUsage(
		long groupId, String classExternalReferenceCode, long classNameId,
		long classPK, String containerKey, long containerType, long plid,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().addLayoutClassedModelUsage(
			groupId, classExternalReferenceCode, classNameId, classPK,
			containerKey, containerType, plid, serviceContext);
	}

	/**
	 * Creates a new layout classed model usage with the primary key. Does not add the layout classed model usage to the database.
	 *
	 * @param layoutClassedModelUsageId the primary key for the new layout classed model usage
	 * @return the new layout classed model usage
	 */
	public static LayoutClassedModelUsage createLayoutClassedModelUsage(
		long layoutClassedModelUsageId) {

		return getService().createLayoutClassedModelUsage(
			layoutClassedModelUsageId);
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
	 * Deletes the layout classed model usage from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutClassedModelUsageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutClassedModelUsage the layout classed model usage
	 * @return the layout classed model usage that was removed
	 */
	public static LayoutClassedModelUsage deleteLayoutClassedModelUsage(
		LayoutClassedModelUsage layoutClassedModelUsage) {

		return getService().deleteLayoutClassedModelUsage(
			layoutClassedModelUsage);
	}

	/**
	 * Deletes the layout classed model usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutClassedModelUsageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutClassedModelUsageId the primary key of the layout classed model usage
	 * @return the layout classed model usage that was removed
	 * @throws PortalException if a layout classed model usage with the primary key could not be found
	 */
	public static LayoutClassedModelUsage deleteLayoutClassedModelUsage(
			long layoutClassedModelUsageId)
		throws PortalException {

		return getService().deleteLayoutClassedModelUsage(
			layoutClassedModelUsageId);
	}

	public static void deleteLayoutClassedModelUsages(
		long classNameId, long classPK) {

		getService().deleteLayoutClassedModelUsages(classNameId, classPK);
	}

	public static void deleteLayoutClassedModelUsages(
		String containerKey, long containerType, long plid) {

		getService().deleteLayoutClassedModelUsages(
			containerKey, containerType, plid);
	}

	public static void deleteLayoutClassedModelUsagesByPlid(long plid) {
		getService().deleteLayoutClassedModelUsagesByPlid(plid);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.model.impl.LayoutClassedModelUsageModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.model.impl.LayoutClassedModelUsageModelImpl</code>.
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

	public static LayoutClassedModelUsage fetchLayoutClassedModelUsage(
		long layoutClassedModelUsageId) {

		return getService().fetchLayoutClassedModelUsage(
			layoutClassedModelUsageId);
	}

	public static LayoutClassedModelUsage fetchLayoutClassedModelUsage(
		long groupId, String classExternalReferenceCode, long classNameId,
		long classPK, String containerKey, long containerType, long plid) {

		return getService().fetchLayoutClassedModelUsage(
			groupId, classExternalReferenceCode, classNameId, classPK,
			containerKey, containerType, plid);
	}

	/**
	 * Returns the layout classed model usage matching the UUID and group.
	 *
	 * @param uuid the layout classed model usage's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage
		fetchLayoutClassedModelUsageByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchLayoutClassedModelUsageByUuidAndGroupId(
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
	 * Returns the layout classed model usage with the primary key.
	 *
	 * @param layoutClassedModelUsageId the primary key of the layout classed model usage
	 * @return the layout classed model usage
	 * @throws PortalException if a layout classed model usage with the primary key could not be found
	 */
	public static LayoutClassedModelUsage getLayoutClassedModelUsage(
			long layoutClassedModelUsageId)
		throws PortalException {

		return getService().getLayoutClassedModelUsage(
			layoutClassedModelUsageId);
	}

	/**
	 * Returns the layout classed model usage matching the UUID and group.
	 *
	 * @param uuid the layout classed model usage's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout classed model usage
	 * @throws PortalException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage
			getLayoutClassedModelUsageByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getLayoutClassedModelUsageByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the layout classed model usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.model.impl.LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> getLayoutClassedModelUsages(
		int start, int end) {

		return getService().getLayoutClassedModelUsages(start, end);
	}

	public static List<LayoutClassedModelUsage> getLayoutClassedModelUsages(
		long classNameId, long classPK) {

		return getService().getLayoutClassedModelUsages(classNameId, classPK);
	}

	public static List<LayoutClassedModelUsage> getLayoutClassedModelUsages(
		long classNameId, long classPK, int type, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getService().getLayoutClassedModelUsages(
			classNameId, classPK, type, start, end, orderByComparator);
	}

	public static List<LayoutClassedModelUsage> getLayoutClassedModelUsages(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getService().getLayoutClassedModelUsages(
			classNameId, classPK, start, end, orderByComparator);
	}

	public static List<LayoutClassedModelUsage> getLayoutClassedModelUsages(
		long companyId, long classNameId, long containerType) {

		return getService().getLayoutClassedModelUsages(
			companyId, classNameId, containerType);
	}

	public static List<LayoutClassedModelUsage>
		getLayoutClassedModelUsagesByPlid(long plid) {

		return getService().getLayoutClassedModelUsagesByPlid(plid);
	}

	/**
	 * Returns all the layout classed model usages matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout classed model usages
	 * @param companyId the primary key of the company
	 * @return the matching layout classed model usages, or an empty list if no matches were found
	 */
	public static List<LayoutClassedModelUsage>
		getLayoutClassedModelUsagesByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getLayoutClassedModelUsagesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of layout classed model usages matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout classed model usages
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching layout classed model usages, or an empty list if no matches were found
	 */
	public static List<LayoutClassedModelUsage>
		getLayoutClassedModelUsagesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getService().getLayoutClassedModelUsagesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of layout classed model usages.
	 *
	 * @return the number of layout classed model usages
	 */
	public static int getLayoutClassedModelUsagesCount() {
		return getService().getLayoutClassedModelUsagesCount();
	}

	public static int getLayoutClassedModelUsagesCount(
		long classNameId, long classPK) {

		return getService().getLayoutClassedModelUsagesCount(
			classNameId, classPK);
	}

	public static int getLayoutClassedModelUsagesCount(
		long classNameId, long classPK, int type) {

		return getService().getLayoutClassedModelUsagesCount(
			classNameId, classPK, type);
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
	 * Updates the layout classed model usage in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutClassedModelUsageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutClassedModelUsage the layout classed model usage
	 * @return the layout classed model usage that was updated
	 */
	public static LayoutClassedModelUsage updateLayoutClassedModelUsage(
		LayoutClassedModelUsage layoutClassedModelUsage) {

		return getService().updateLayoutClassedModelUsage(
			layoutClassedModelUsage);
	}

	public static LayoutClassedModelUsage updateLayoutClassedModelUsage(
			long classNameId, long classPK, String containerKey,
			long containerType, long layoutClassedModelUsageId, long plid)
		throws PortalException {

		return getService().updateLayoutClassedModelUsage(
			classNameId, classPK, containerKey, containerType,
			layoutClassedModelUsageId, plid);
	}

	public static LayoutClassedModelUsageLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<LayoutClassedModelUsageLocalService>
		_serviceSnapshot = new Snapshot<>(
			LayoutClassedModelUsageLocalServiceUtil.class,
			LayoutClassedModelUsageLocalService.class);

}