/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service;

import com.liferay.layout.content.model.LayoutContentVersionPreview;
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
 * Provides the local service utility for LayoutContentVersionPreview. This utility wraps
 * <code>com.liferay.layout.content.service.impl.LayoutContentVersionPreviewLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Lourdes Fernández Besada
 * @see LayoutContentVersionPreviewLocalService
 * @generated
 */
public class LayoutContentVersionPreviewLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.layout.content.service.impl.LayoutContentVersionPreviewLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the layout content version preview to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionPreviewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersionPreview the layout content version preview
	 * @return the layout content version preview that was added
	 */
	public static LayoutContentVersionPreview addLayoutContentVersionPreview(
		LayoutContentVersionPreview layoutContentVersionPreview) {

		return getService().addLayoutContentVersionPreview(
			layoutContentVersionPreview);
	}

	public static LayoutContentVersionPreview addLayoutContentVersionPreview(
			long userId, long layoutContentVersionId, String html,
			String languageId, String segmentsExperienceERC)
		throws PortalException {

		return getService().addLayoutContentVersionPreview(
			userId, layoutContentVersionId, html, languageId,
			segmentsExperienceERC);
	}

	/**
	 * Creates a new layout content version preview with the primary key. Does not add the layout content version preview to the database.
	 *
	 * @param layoutContentVersionPreviewId the primary key for the new layout content version preview
	 * @return the new layout content version preview
	 */
	public static LayoutContentVersionPreview createLayoutContentVersionPreview(
		long layoutContentVersionPreviewId) {

		return getService().createLayoutContentVersionPreview(
			layoutContentVersionPreviewId);
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
	 * Deletes the layout content version preview from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionPreviewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersionPreview the layout content version preview
	 * @return the layout content version preview that was removed
	 */
	public static LayoutContentVersionPreview deleteLayoutContentVersionPreview(
		LayoutContentVersionPreview layoutContentVersionPreview) {

		return getService().deleteLayoutContentVersionPreview(
			layoutContentVersionPreview);
	}

	/**
	 * Deletes the layout content version preview with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionPreviewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersionPreviewId the primary key of the layout content version preview
	 * @return the layout content version preview that was removed
	 * @throws PortalException if a layout content version preview with the primary key could not be found
	 */
	public static LayoutContentVersionPreview deleteLayoutContentVersionPreview(
			long layoutContentVersionPreviewId)
		throws PortalException {

		return getService().deleteLayoutContentVersionPreview(
			layoutContentVersionPreviewId);
	}

	public static void deleteLayoutContentVersionPreviews(
			long layoutContentVersionId)
		throws PortalException {

		getService().deleteLayoutContentVersionPreviews(layoutContentVersionId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionPreviewModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionPreviewModelImpl</code>.
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

	public static LayoutContentVersionPreview fetchLayoutContentVersionPreview(
		long layoutContentVersionPreviewId) {

		return getService().fetchLayoutContentVersionPreview(
			layoutContentVersionPreviewId);
	}

	public static LayoutContentVersionPreview fetchLayoutContentVersionPreview(
			long layoutContentVersionId, String languageId,
			String segmentsExperienceERC)
		throws PortalException {

		return getService().fetchLayoutContentVersionPreview(
			layoutContentVersionId, languageId, segmentsExperienceERC);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the layout content version preview with the primary key.
	 *
	 * @param layoutContentVersionPreviewId the primary key of the layout content version preview
	 * @return the layout content version preview
	 * @throws PortalException if a layout content version preview with the primary key could not be found
	 */
	public static LayoutContentVersionPreview getLayoutContentVersionPreview(
			long layoutContentVersionPreviewId)
		throws PortalException {

		return getService().getLayoutContentVersionPreview(
			layoutContentVersionPreviewId);
	}

	/**
	 * Returns a range of all the layout content version previews.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionPreviewModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout content version previews
	 * @param end the upper bound of the range of layout content version previews (not inclusive)
	 * @return the range of layout content version previews
	 */
	public static List<LayoutContentVersionPreview>
		getLayoutContentVersionPreviews(int start, int end) {

		return getService().getLayoutContentVersionPreviews(start, end);
	}

	public static List<LayoutContentVersionPreview>
			getLayoutContentVersionPreviews(long layoutContentVersionId)
		throws PortalException {

		return getService().getLayoutContentVersionPreviews(
			layoutContentVersionId);
	}

	/**
	 * Returns the number of layout content version previews.
	 *
	 * @return the number of layout content version previews
	 */
	public static int getLayoutContentVersionPreviewsCount() {
		return getService().getLayoutContentVersionPreviewsCount();
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

	public static Map<String, List<String>>
			getSegmentsExperienceERCsLanguageIds(long layoutContentVersionId)
		throws PortalException {

		return getService().getSegmentsExperienceERCsLanguageIds(
			layoutContentVersionId);
	}

	/**
	 * Updates the layout content version preview in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionPreviewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersionPreview the layout content version preview
	 * @return the layout content version preview that was updated
	 */
	public static LayoutContentVersionPreview updateLayoutContentVersionPreview(
		LayoutContentVersionPreview layoutContentVersionPreview) {

		return getService().updateLayoutContentVersionPreview(
			layoutContentVersionPreview);
	}

	public static LayoutContentVersionPreviewLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<LayoutContentVersionPreviewLocalService>
		_serviceSnapshot = new Snapshot<>(
			LayoutContentVersionPreviewLocalServiceUtil.class,
			LayoutContentVersionPreviewLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:26252163