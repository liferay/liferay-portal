/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service;

import com.liferay.layout.model.LayoutLocalization;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for LayoutLocalization. This utility wraps
 * <code>com.liferay.layout.service.impl.LayoutLocalizationLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutLocalizationLocalService
 * @generated
 */
public class LayoutLocalizationLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.layout.service.impl.LayoutLocalizationLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the layout localization to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutLocalizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutLocalization the layout localization
	 * @return the layout localization that was added
	 */
	public static LayoutLocalization addLayoutLocalization(
		LayoutLocalization layoutLocalization) {

		return getService().addLayoutLocalization(layoutLocalization);
	}

	public static LayoutLocalization addLayoutLocalization(
		long groupId, String content, String languageId, long plid,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().addLayoutLocalization(
			groupId, content, languageId, plid, serviceContext);
	}

	/**
	 * Creates a new layout localization with the primary key. Does not add the layout localization to the database.
	 *
	 * @param layoutLocalizationId the primary key for the new layout localization
	 * @return the new layout localization
	 */
	public static LayoutLocalization createLayoutLocalization(
		long layoutLocalizationId) {

		return getService().createLayoutLocalization(layoutLocalizationId);
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
	 * Deletes the layout localization from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutLocalizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutLocalization the layout localization
	 * @return the layout localization that was removed
	 */
	public static LayoutLocalization deleteLayoutLocalization(
		LayoutLocalization layoutLocalization) {

		return getService().deleteLayoutLocalization(layoutLocalization);
	}

	/**
	 * Deletes the layout localization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutLocalizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutLocalizationId the primary key of the layout localization
	 * @return the layout localization that was removed
	 * @throws PortalException if a layout localization with the primary key could not be found
	 */
	public static LayoutLocalization deleteLayoutLocalization(
			long layoutLocalizationId)
		throws PortalException {

		return getService().deleteLayoutLocalization(layoutLocalizationId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.model.impl.LayoutLocalizationModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.model.impl.LayoutLocalizationModelImpl</code>.
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

	public static LayoutLocalization fetchLayoutLocalization(
		long layoutLocalizationId) {

		return getService().fetchLayoutLocalization(layoutLocalizationId);
	}

	public static LayoutLocalization fetchLayoutLocalization(
		long groupId, String languageId, long plid) {

		return getService().fetchLayoutLocalization(groupId, languageId, plid);
	}

	/**
	 * Returns the layout localization matching the UUID and group.
	 *
	 * @param uuid the layout localization's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout localization, or <code>null</code> if a matching layout localization could not be found
	 */
	public static LayoutLocalization fetchLayoutLocalizationByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchLayoutLocalizationByUuidAndGroupId(
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
	 * Returns the layout localization with the primary key.
	 *
	 * @param layoutLocalizationId the primary key of the layout localization
	 * @return the layout localization
	 * @throws PortalException if a layout localization with the primary key could not be found
	 */
	public static LayoutLocalization getLayoutLocalization(
			long layoutLocalizationId)
		throws PortalException {

		return getService().getLayoutLocalization(layoutLocalizationId);
	}

	public static LayoutLocalization getLayoutLocalization(
			String languageId, long plid)
		throws PortalException {

		return getService().getLayoutLocalization(languageId, plid);
	}

	/**
	 * Returns the layout localization matching the UUID and group.
	 *
	 * @param uuid the layout localization's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout localization
	 * @throws PortalException if a matching layout localization could not be found
	 */
	public static LayoutLocalization getLayoutLocalizationByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getLayoutLocalizationByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the layout localizations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.model.impl.LayoutLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout localizations
	 * @param end the upper bound of the range of layout localizations (not inclusive)
	 * @return the range of layout localizations
	 */
	public static List<LayoutLocalization> getLayoutLocalizations(
		int start, int end) {

		return getService().getLayoutLocalizations(start, end);
	}

	/**
	 * Returns all the layout localizations matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout localizations
	 * @param companyId the primary key of the company
	 * @return the matching layout localizations, or an empty list if no matches were found
	 */
	public static List<LayoutLocalization>
		getLayoutLocalizationsByUuidAndCompanyId(String uuid, long companyId) {

		return getService().getLayoutLocalizationsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of layout localizations matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout localizations
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of layout localizations
	 * @param end the upper bound of the range of layout localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching layout localizations, or an empty list if no matches were found
	 */
	public static List<LayoutLocalization>
		getLayoutLocalizationsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<LayoutLocalization> orderByComparator) {

		return getService().getLayoutLocalizationsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of layout localizations.
	 *
	 * @return the number of layout localizations
	 */
	public static int getLayoutLocalizationsCount() {
		return getService().getLayoutLocalizationsCount();
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
	 * Updates the layout localization in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutLocalizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutLocalization the layout localization
	 * @return the layout localization that was updated
	 */
	public static LayoutLocalization updateLayoutLocalization(
		LayoutLocalization layoutLocalization) {

		return getService().updateLayoutLocalization(layoutLocalization);
	}

	public static LayoutLocalization updateLayoutLocalization(
		String content, String languageId, long plid,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().updateLayoutLocalization(
			content, languageId, plid, serviceContext);
	}

	public static LayoutLocalizationLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<LayoutLocalizationLocalService>
		_serviceSnapshot = new Snapshot<>(
			LayoutLocalizationLocalServiceUtil.class,
			LayoutLocalizationLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-506589787