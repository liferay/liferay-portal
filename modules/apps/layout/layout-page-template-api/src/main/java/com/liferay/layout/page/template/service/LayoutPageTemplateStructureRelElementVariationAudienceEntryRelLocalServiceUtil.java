/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service;

import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationAudienceEntryRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for LayoutPageTemplateStructureRelElementVariationAudienceEntryRel. This utility wraps
 * <code>com.liferay.layout.page.template.service.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService
 * @generated
 */
public class
	LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.layout.page.template.service.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the layout page template structure rel element variation audience entry rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRelElementVariationAudienceEntryRel the layout page template structure rel element variation audience entry rel
	 * @return the layout page template structure rel element variation audience entry rel that was added
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel) {

		return getService().
			addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel);
	}

	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				long userId, long groupId, String audienceEntryERC,
				String layoutPageTemplateStructureRelElementVariationERC,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().
			addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				userId, groupId, audienceEntryERC,
				layoutPageTemplateStructureRelElementVariationERC,
				serviceContext);
	}

	/**
	 * Creates a new layout page template structure rel element variation audience entry rel with the primary key. Does not add the layout page template structure rel element variation audience entry rel to the database.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationAudienceEntryRelId the primary key for the new layout page template structure rel element variation audience entry rel
	 * @return the new layout page template structure rel element variation audience entry rel
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		createLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
			long
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId) {

		return getService().
			createLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId);
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
	 * Deletes the layout page template structure rel element variation audience entry rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRelElementVariationAudienceEntryRel the layout page template structure rel element variation audience entry rel
	 * @return the layout page template structure rel element variation audience entry rel that was removed
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel) {

		return getService().
			deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel);
	}

	/**
	 * Deletes the layout page template structure rel element variation audience entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRelElementVariationAudienceEntryRelId the primary key of the layout page template structure rel element variation audience entry rel
	 * @return the layout page template structure rel element variation audience entry rel that was removed
	 * @throws PortalException if a layout page template structure rel element variation audience entry rel with the primary key could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				long
					layoutPageTemplateStructureRelElementVariationAudienceEntryRelId)
		throws PortalException {

		return getService().
			deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId);
	}

	public static void
		deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
			String layoutPageTemplateStructureRelElementVariationERC) {

		getService().
			deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
				layoutPageTemplateStructureRelElementVariationERC);
	}

	public static void
		deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRelsByAudienceEntryERC(
			long companyId, String audienceEntryERC) {

		getService().
			deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRelsByAudienceEntryERC(
				companyId, audienceEntryERC);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
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

	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
			long
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId) {

		return getService().
			fetchLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId);
	}

	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchLayoutPageTemplateStructureRelElementVariationAudienceEntryRelByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return getService().
			fetchLayoutPageTemplateStructureRelElementVariationAudienceEntryRelByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the layout page template structure rel element variation audience entry rel matching the UUID and group.
	 *
	 * @param uuid the layout page template structure rel element variation audience entry rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchLayoutPageTemplateStructureRelElementVariationAudienceEntryRelByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().
			fetchLayoutPageTemplateStructureRelElementVariationAudienceEntryRelByUuidAndGroupId(
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
	 * Returns the layout page template structure rel element variation audience entry rel with the primary key.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationAudienceEntryRelId the primary key of the layout page template structure rel element variation audience entry rel
	 * @return the layout page template structure rel element variation audience entry rel
	 * @throws PortalException if a layout page template structure rel element variation audience entry rel with the primary key could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				long
					layoutPageTemplateStructureRelElementVariationAudienceEntryRelId)
		throws PortalException {

		return getService().
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId);
	}

	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the layout page template structure rel element variation audience entry rel matching the UUID and group.
	 *
	 * @param uuid the layout page template structure rel element variation audience entry rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout page template structure rel element variation audience entry rel
	 * @throws PortalException if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelByUuidAndGroupId(
				uuid, groupId);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variation audience entry rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @return the range of layout page template structure rel element variation audience entry rels
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
				int start, int end) {

		return getService().
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
				start, end);
	}

	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
				String layoutPageTemplateStructureRelElementVariationERC) {

		return getService().
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
				layoutPageTemplateStructureRelElementVariationERC);
	}

	/**
	 * Returns all the layout page template structure rel element variation audience entry rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout page template structure rel element variation audience entry rels
	 * @param companyId the primary key of the company
	 * @return the matching layout page template structure rel element variation audience entry rels, or an empty list if no matches were found
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelsByUuidAndCompanyId(
				String uuid, long companyId) {

		return getService().
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelsByUuidAndCompanyId(
				uuid, companyId);
	}

	/**
	 * Returns a range of layout page template structure rel element variation audience entry rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout page template structure rel element variation audience entry rels
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching layout page template structure rel element variation audience entry rels, or an empty list if no matches were found
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelsByUuidAndCompanyId(
				String uuid, long companyId, int start, int end,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator) {

		return getService().
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of layout page template structure rel element variation audience entry rels.
	 *
	 * @return the number of layout page template structure rel element variation audience entry rels
	 */
	public static int
		getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelsCount() {

		return getService().
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelsCount();
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
	 * Updates the layout page template structure rel element variation audience entry rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRelElementVariationAudienceEntryRel the layout page template structure rel element variation audience entry rel
	 * @return the layout page template structure rel element variation audience entry rel that was updated
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		updateLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel) {

		return getService().
			updateLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel);
	}

	public static
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService
			getService() {

		return _serviceSnapshot.get();
	}

	private static final Snapshot
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService>
			_serviceSnapshot = new Snapshot<>(
				LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalServiceUtil.class,
				LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:310072702