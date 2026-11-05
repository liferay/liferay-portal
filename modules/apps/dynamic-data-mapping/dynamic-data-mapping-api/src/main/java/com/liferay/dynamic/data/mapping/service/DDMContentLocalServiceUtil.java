/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMContent;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for DDMContent. This utility wraps
 * <code>com.liferay.dynamic.data.mapping.service.impl.DDMContentLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DDMContentLocalService
 * @generated
 */
public class DDMContentLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.dynamic.data.mapping.service.impl.DDMContentLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the ddm content to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMContentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmContent the ddm content
	 * @return the ddm content that was added
	 */
	public static DDMContent addDDMContent(DDMContent ddmContent) {
		return getService().addDDMContent(ddmContent);
	}

	/**
	 * Creates a new ddm content with the primary key. Does not add the ddm content to the database.
	 *
	 * @param contentId the primary key for the new ddm content
	 * @return the new ddm content
	 */
	public static DDMContent createDDMContent(long contentId) {
		return getService().createDDMContent(contentId);
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
	 * Deletes the ddm content from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMContentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmContent the ddm content
	 * @return the ddm content that was removed
	 */
	public static DDMContent deleteDDMContent(DDMContent ddmContent) {
		return getService().deleteDDMContent(ddmContent);
	}

	/**
	 * Deletes the ddm content with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMContentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param contentId the primary key of the ddm content
	 * @return the ddm content that was removed
	 * @throws PortalException if a ddm content with the primary key could not be found
	 */
	public static DDMContent deleteDDMContent(long contentId)
		throws PortalException {

		return getService().deleteDDMContent(contentId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMContentModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMContentModelImpl</code>.
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

	public static DDMContent fetchDDMContent(long contentId) {
		return getService().fetchDDMContent(contentId);
	}

	/**
	 * Returns the ddm content matching the UUID and group.
	 *
	 * @param uuid the ddm content's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm content, or <code>null</code> if a matching ddm content could not be found
	 */
	public static DDMContent fetchDDMContentByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchDDMContentByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the ddm content with the primary key.
	 *
	 * @param contentId the primary key of the ddm content
	 * @return the ddm content
	 * @throws PortalException if a ddm content with the primary key could not be found
	 */
	public static DDMContent getDDMContent(long contentId)
		throws PortalException {

		return getService().getDDMContent(contentId);
	}

	/**
	 * Returns the ddm content matching the UUID and group.
	 *
	 * @param uuid the ddm content's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm content
	 * @throws PortalException if a matching ddm content could not be found
	 */
	public static DDMContent getDDMContentByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getDDMContentByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the ddm contents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMContentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm contents
	 * @param end the upper bound of the range of ddm contents (not inclusive)
	 * @return the range of ddm contents
	 */
	public static List<DDMContent> getDDMContents(int start, int end) {
		return getService().getDDMContents(start, end);
	}

	/**
	 * Returns all the ddm contents matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm contents
	 * @param companyId the primary key of the company
	 * @return the matching ddm contents, or an empty list if no matches were found
	 */
	public static List<DDMContent> getDDMContentsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getDDMContentsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of ddm contents matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm contents
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of ddm contents
	 * @param end the upper bound of the range of ddm contents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching ddm contents, or an empty list if no matches were found
	 */
	public static List<DDMContent> getDDMContentsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMContent> orderByComparator) {

		return getService().getDDMContentsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of ddm contents.
	 *
	 * @return the number of ddm contents
	 */
	public static int getDDMContentsCount() {
		return getService().getDDMContentsCount();
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

	/**
	 * Updates the ddm content in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMContentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmContent the ddm content
	 * @return the ddm content that was updated
	 */
	public static DDMContent updateDDMContent(DDMContent ddmContent) {
		return getService().updateDDMContent(ddmContent);
	}

	public static DDMContentLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DDMContentLocalService> _serviceSnapshot =
		new Snapshot<>(
			DDMContentLocalServiceUtil.class, DDMContentLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:722567835