/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service;

import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
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
 * Provides the local service utility for LayoutPageTemplateStructureRelElementVariation. This utility wraps
 * <code>com.liferay.layout.page.template.service.impl.LayoutPageTemplateStructureRelElementVariationLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateStructureRelElementVariationLocalService
 * @generated
 */
public class LayoutPageTemplateStructureRelElementVariationLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.layout.page.template.service.impl.LayoutPageTemplateStructureRelElementVariationLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the layout page template structure rel element variation to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureRelElementVariationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRelElementVariation the layout page template structure rel element variation
	 * @return the layout page template structure rel element variation that was added
	 */
	public static LayoutPageTemplateStructureRelElementVariation
		addLayoutPageTemplateStructureRelElementVariation(
			LayoutPageTemplateStructureRelElementVariation
				layoutPageTemplateStructureRelElementVariation) {

		return getService().addLayoutPageTemplateStructureRelElementVariation(
			layoutPageTemplateStructureRelElementVariation);
	}

	public static LayoutPageTemplateStructureRelElementVariation
			addOrUpdateLayoutPageTemplateStructureRelElementVariation(
				String externalReferenceCode, long userId, long groupId,
				String[] audienceEntryERCs, boolean active, String hide,
				Map<java.util.Locale, String> htmlMap,
				Map<java.util.Locale, String> jsMap, String name, long plid,
				String segmentsExperienceERC, String targetElement,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().
			addOrUpdateLayoutPageTemplateStructureRelElementVariation(
				externalReferenceCode, userId, groupId, audienceEntryERCs,
				active, hide, htmlMap, jsMap, name, plid, segmentsExperienceERC,
				targetElement, serviceContext);
	}

	/**
	 * Creates a new layout page template structure rel element variation with the primary key. Does not add the layout page template structure rel element variation to the database.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationId the primary key for the new layout page template structure rel element variation
	 * @return the new layout page template structure rel element variation
	 */
	public static LayoutPageTemplateStructureRelElementVariation
		createLayoutPageTemplateStructureRelElementVariation(
			long layoutPageTemplateStructureRelElementVariationId) {

		return getService().
			createLayoutPageTemplateStructureRelElementVariation(
				layoutPageTemplateStructureRelElementVariationId);
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
	 * Deletes the layout page template structure rel element variation from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureRelElementVariationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRelElementVariation the layout page template structure rel element variation
	 * @return the layout page template structure rel element variation that was removed
	 */
	public static LayoutPageTemplateStructureRelElementVariation
		deleteLayoutPageTemplateStructureRelElementVariation(
			LayoutPageTemplateStructureRelElementVariation
				layoutPageTemplateStructureRelElementVariation) {

		return getService().
			deleteLayoutPageTemplateStructureRelElementVariation(
				layoutPageTemplateStructureRelElementVariation);
	}

	/**
	 * Deletes the layout page template structure rel element variation with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureRelElementVariationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRelElementVariationId the primary key of the layout page template structure rel element variation
	 * @return the layout page template structure rel element variation that was removed
	 * @throws PortalException if a layout page template structure rel element variation with the primary key could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation
			deleteLayoutPageTemplateStructureRelElementVariation(
				long layoutPageTemplateStructureRelElementVariationId)
		throws PortalException {

		return getService().
			deleteLayoutPageTemplateStructureRelElementVariation(
				layoutPageTemplateStructureRelElementVariationId);
	}

	public static void deleteLayoutPageTemplateStructureRelElementVariation(
		String externalReferenceCode, long groupId) {

		getService().deleteLayoutPageTemplateStructureRelElementVariation(
			externalReferenceCode, groupId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
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

	public static LayoutPageTemplateStructureRelElementVariation
		fetchLayoutPageTemplateStructureRelElementVariation(
			long layoutPageTemplateStructureRelElementVariationId) {

		return getService().fetchLayoutPageTemplateStructureRelElementVariation(
			layoutPageTemplateStructureRelElementVariationId);
	}

	public static LayoutPageTemplateStructureRelElementVariation
		fetchLayoutPageTemplateStructureRelElementVariationByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return getService().
			fetchLayoutPageTemplateStructureRelElementVariationByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the layout page template structure rel element variation matching the UUID and group.
	 *
	 * @param uuid the layout page template structure rel element variation's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation
		fetchLayoutPageTemplateStructureRelElementVariationByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().
			fetchLayoutPageTemplateStructureRelElementVariationByUuidAndGroupId(
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
	 * Returns the layout page template structure rel element variation with the primary key.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationId the primary key of the layout page template structure rel element variation
	 * @return the layout page template structure rel element variation
	 * @throws PortalException if a layout page template structure rel element variation with the primary key could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation
			getLayoutPageTemplateStructureRelElementVariation(
				long layoutPageTemplateStructureRelElementVariationId)
		throws PortalException {

		return getService().getLayoutPageTemplateStructureRelElementVariation(
			layoutPageTemplateStructureRelElementVariationId);
	}

	public static LayoutPageTemplateStructureRelElementVariation
			getLayoutPageTemplateStructureRelElementVariationByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().
			getLayoutPageTemplateStructureRelElementVariationByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the layout page template structure rel element variation matching the UUID and group.
	 *
	 * @param uuid the layout page template structure rel element variation's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout page template structure rel element variation
	 * @throws PortalException if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation
			getLayoutPageTemplateStructureRelElementVariationByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().
			getLayoutPageTemplateStructureRelElementVariationByUuidAndGroupId(
				uuid, groupId);
	}

	public static List<LayoutPageTemplateStructureRelElementVariation>
		getLayoutPageTemplateStructureRelElementVariations(
			boolean active, long plid, String segmentsExperienceERC) {

		return getService().getLayoutPageTemplateStructureRelElementVariations(
			active, plid, segmentsExperienceERC);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @return the range of layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		getLayoutPageTemplateStructureRelElementVariations(int start, int end) {

		return getService().getLayoutPageTemplateStructureRelElementVariations(
			start, end);
	}

	public static List<LayoutPageTemplateStructureRelElementVariation>
		getLayoutPageTemplateStructureRelElementVariations(long plid) {

		return getService().getLayoutPageTemplateStructureRelElementVariations(
			plid);
	}

	public static List<LayoutPageTemplateStructureRelElementVariation>
		getLayoutPageTemplateStructureRelElementVariations(
			long plid, String segmentsExperienceERC) {

		return getService().getLayoutPageTemplateStructureRelElementVariations(
			plid, segmentsExperienceERC);
	}

	/**
	 * Returns all the layout page template structure rel element variations matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout page template structure rel element variations
	 * @param companyId the primary key of the company
	 * @return the matching layout page template structure rel element variations, or an empty list if no matches were found
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		getLayoutPageTemplateStructureRelElementVariationsByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().
			getLayoutPageTemplateStructureRelElementVariationsByUuidAndCompanyId(
				uuid, companyId);
	}

	/**
	 * Returns a range of layout page template structure rel element variations matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout page template structure rel element variations
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching layout page template structure rel element variations, or an empty list if no matches were found
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		getLayoutPageTemplateStructureRelElementVariationsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator) {

		return getService().
			getLayoutPageTemplateStructureRelElementVariationsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of layout page template structure rel element variations.
	 *
	 * @return the number of layout page template structure rel element variations
	 */
	public static int
		getLayoutPageTemplateStructureRelElementVariationsCount() {

		return getService().
			getLayoutPageTemplateStructureRelElementVariationsCount();
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
	 * Updates the layout page template structure rel element variation in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureRelElementVariationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRelElementVariation the layout page template structure rel element variation
	 * @return the layout page template structure rel element variation that was updated
	 */
	public static LayoutPageTemplateStructureRelElementVariation
		updateLayoutPageTemplateStructureRelElementVariation(
			LayoutPageTemplateStructureRelElementVariation
				layoutPageTemplateStructureRelElementVariation) {

		return getService().
			updateLayoutPageTemplateStructureRelElementVariation(
				layoutPageTemplateStructureRelElementVariation);
	}

	public static LayoutPageTemplateStructureRelElementVariationLocalService
		getService() {

		return _serviceSnapshot.get();
	}

	private static final Snapshot
		<LayoutPageTemplateStructureRelElementVariationLocalService>
			_serviceSnapshot = new Snapshot<>(
				LayoutPageTemplateStructureRelElementVariationLocalServiceUtil.
					class,
				LayoutPageTemplateStructureRelElementVariationLocalService.
					class);

}
// LIFERAY-SERVICE-BUILDER-HASH:2083718361