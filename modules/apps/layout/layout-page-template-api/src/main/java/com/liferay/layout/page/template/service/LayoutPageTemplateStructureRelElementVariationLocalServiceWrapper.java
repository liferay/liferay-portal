/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service;

import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link LayoutPageTemplateStructureRelElementVariationLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateStructureRelElementVariationLocalService
 * @generated
 */
public class LayoutPageTemplateStructureRelElementVariationLocalServiceWrapper
	implements LayoutPageTemplateStructureRelElementVariationLocalService,
			   ServiceWrapper
				   <LayoutPageTemplateStructureRelElementVariationLocalService> {

	public LayoutPageTemplateStructureRelElementVariationLocalServiceWrapper() {
		this(null);
	}

	public LayoutPageTemplateStructureRelElementVariationLocalServiceWrapper(
		LayoutPageTemplateStructureRelElementVariationLocalService
			layoutPageTemplateStructureRelElementVariationLocalService) {

		_layoutPageTemplateStructureRelElementVariationLocalService =
			layoutPageTemplateStructureRelElementVariationLocalService;
	}

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
	@Override
	public LayoutPageTemplateStructureRelElementVariation
		addLayoutPageTemplateStructureRelElementVariation(
			LayoutPageTemplateStructureRelElementVariation
				layoutPageTemplateStructureRelElementVariation) {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			addLayoutPageTemplateStructureRelElementVariation(
				layoutPageTemplateStructureRelElementVariation);
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariation
			addOrUpdateLayoutPageTemplateStructureRelElementVariation(
				String externalReferenceCode, long userId, long groupId,
				boolean active, String hide,
				java.util.Map<java.util.Locale, String> htmlMap,
				java.util.Map<java.util.Locale, String> jsMap, String name,
				long plid, String segmentsExperienceERC, String targetElement,
				String[] audienceEntryERCs,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			addOrUpdateLayoutPageTemplateStructureRelElementVariation(
				externalReferenceCode, userId, groupId, active, hide, htmlMap,
				jsMap, name, plid, segmentsExperienceERC, targetElement,
				audienceEntryERCs, serviceContext);
	}

	/**
	 * Creates a new layout page template structure rel element variation with the primary key. Does not add the layout page template structure rel element variation to the database.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationId the primary key for the new layout page template structure rel element variation
	 * @return the new layout page template structure rel element variation
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation
		createLayoutPageTemplateStructureRelElementVariation(
			long layoutPageTemplateStructureRelElementVariationId) {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			createLayoutPageTemplateStructureRelElementVariation(
				layoutPageTemplateStructureRelElementVariationId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			createPersistedModel(primaryKeyObj);
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
	@Override
	public LayoutPageTemplateStructureRelElementVariation
		deleteLayoutPageTemplateStructureRelElementVariation(
			LayoutPageTemplateStructureRelElementVariation
				layoutPageTemplateStructureRelElementVariation) {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
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
	@Override
	public LayoutPageTemplateStructureRelElementVariation
			deleteLayoutPageTemplateStructureRelElementVariation(
				long layoutPageTemplateStructureRelElementVariationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			deleteLayoutPageTemplateStructureRelElementVariation(
				layoutPageTemplateStructureRelElementVariationId);
	}

	@Override
	public void deleteLayoutPageTemplateStructureRelElementVariation(
		String externalReferenceCode, long groupId) {

		_layoutPageTemplateStructureRelElementVariationLocalService.
			deleteLayoutPageTemplateStructureRelElementVariation(
				externalReferenceCode, groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _layoutPageTemplateStructureRelElementVariationLocalService.
			dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _layoutPageTemplateStructureRelElementVariationLocalService.
			dynamicQuery();
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

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			dynamicQuery(dynamicQuery, start, end, orderByComparator);
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

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			dynamicQueryCount(dynamicQuery);
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

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariation
		fetchLayoutPageTemplateStructureRelElementVariation(
			long layoutPageTemplateStructureRelElementVariationId) {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			fetchLayoutPageTemplateStructureRelElementVariation(
				layoutPageTemplateStructureRelElementVariationId);
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariation
		fetchLayoutPageTemplateStructureRelElementVariationByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
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
	@Override
	public LayoutPageTemplateStructureRelElementVariation
		fetchLayoutPageTemplateStructureRelElementVariationByUuidAndGroupId(
			String uuid, long groupId) {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			fetchLayoutPageTemplateStructureRelElementVariationByUuidAndGroupId(
				uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the layout page template structure rel element variation with the primary key.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationId the primary key of the layout page template structure rel element variation
	 * @return the layout page template structure rel element variation
	 * @throws PortalException if a layout page template structure rel element variation with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation
			getLayoutPageTemplateStructureRelElementVariation(
				long layoutPageTemplateStructureRelElementVariationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			getLayoutPageTemplateStructureRelElementVariation(
				layoutPageTemplateStructureRelElementVariationId);
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariation
			getLayoutPageTemplateStructureRelElementVariationByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
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
	@Override
	public LayoutPageTemplateStructureRelElementVariation
			getLayoutPageTemplateStructureRelElementVariationByUuidAndGroupId(
				String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			getLayoutPageTemplateStructureRelElementVariationByUuidAndGroupId(
				uuid, groupId);
	}

	@Override
	public java.util.List<LayoutPageTemplateStructureRelElementVariation>
		getLayoutPageTemplateStructureRelElementVariations(
			boolean active, long plid, String segmentsExperienceERC) {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			getLayoutPageTemplateStructureRelElementVariations(
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
	@Override
	public java.util.List<LayoutPageTemplateStructureRelElementVariation>
		getLayoutPageTemplateStructureRelElementVariations(int start, int end) {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			getLayoutPageTemplateStructureRelElementVariations(start, end);
	}

	@Override
	public java.util.List<LayoutPageTemplateStructureRelElementVariation>
		getLayoutPageTemplateStructureRelElementVariations(long plid) {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			getLayoutPageTemplateStructureRelElementVariations(plid);
	}

	@Override
	public java.util.List<LayoutPageTemplateStructureRelElementVariation>
		getLayoutPageTemplateStructureRelElementVariations(
			long plid, String segmentsExperienceERC) {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			getLayoutPageTemplateStructureRelElementVariations(
				plid, segmentsExperienceERC);
	}

	/**
	 * Returns all the layout page template structure rel element variations matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout page template structure rel element variations
	 * @param companyId the primary key of the company
	 * @return the matching layout page template structure rel element variations, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<LayoutPageTemplateStructureRelElementVariation>
		getLayoutPageTemplateStructureRelElementVariationsByUuidAndCompanyId(
			String uuid, long companyId) {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
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
	@Override
	public java.util.List<LayoutPageTemplateStructureRelElementVariation>
		getLayoutPageTemplateStructureRelElementVariationsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRelElementVariation>
					orderByComparator) {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			getLayoutPageTemplateStructureRelElementVariationsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of layout page template structure rel element variations.
	 *
	 * @return the number of layout page template structure rel element variations
	 */
	@Override
	public int getLayoutPageTemplateStructureRelElementVariationsCount() {
		return _layoutPageTemplateStructureRelElementVariationLocalService.
			getLayoutPageTemplateStructureRelElementVariationsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _layoutPageTemplateStructureRelElementVariationLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			getPersistedModel(primaryKeyObj);
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
	@Override
	public LayoutPageTemplateStructureRelElementVariation
		updateLayoutPageTemplateStructureRelElementVariation(
			LayoutPageTemplateStructureRelElementVariation
				layoutPageTemplateStructureRelElementVariation) {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			updateLayoutPageTemplateStructureRelElementVariation(
				layoutPageTemplateStructureRelElementVariation);
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariation
			updateLayoutPageTemplateStructureRelElementVariation(
				String externalReferenceCode, long groupId, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			updateLayoutPageTemplateStructureRelElementVariation(
				externalReferenceCode, groupId, active);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _layoutPageTemplateStructureRelElementVariationLocalService.
			getBasePersistence();
	}

	@Override
	public CTPersistence<LayoutPageTemplateStructureRelElementVariation>
		getCTPersistence() {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			getCTPersistence();
	}

	@Override
	public Class<LayoutPageTemplateStructureRelElementVariation>
		getModelClass() {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction
				<CTPersistence<LayoutPageTemplateStructureRelElementVariation>,
				 R, E> updateUnsafeFunction)
		throws E {

		return _layoutPageTemplateStructureRelElementVariationLocalService.
			updateWithUnsafeFunction(updateUnsafeFunction);
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariationLocalService
		getWrappedService() {

		return _layoutPageTemplateStructureRelElementVariationLocalService;
	}

	@Override
	public void setWrappedService(
		LayoutPageTemplateStructureRelElementVariationLocalService
			layoutPageTemplateStructureRelElementVariationLocalService) {

		_layoutPageTemplateStructureRelElementVariationLocalService =
			layoutPageTemplateStructureRelElementVariationLocalService;
	}

	private LayoutPageTemplateStructureRelElementVariationLocalService
		_layoutPageTemplateStructureRelElementVariationLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:72931764