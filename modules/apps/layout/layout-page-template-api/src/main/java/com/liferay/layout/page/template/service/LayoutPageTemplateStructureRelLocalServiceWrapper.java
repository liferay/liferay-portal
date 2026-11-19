/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service;

import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link LayoutPageTemplateStructureRelLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateStructureRelLocalService
 * @generated
 */
public class LayoutPageTemplateStructureRelLocalServiceWrapper
	implements LayoutPageTemplateStructureRelLocalService,
			   ServiceWrapper<LayoutPageTemplateStructureRelLocalService> {

	public LayoutPageTemplateStructureRelLocalServiceWrapper() {
		this(null);
	}

	public LayoutPageTemplateStructureRelLocalServiceWrapper(
		LayoutPageTemplateStructureRelLocalService
			layoutPageTemplateStructureRelLocalService) {

		_layoutPageTemplateStructureRelLocalService =
			layoutPageTemplateStructureRelLocalService;
	}

	/**
	 * Adds the layout page template structure rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRel the layout page template structure rel
	 * @return the layout page template structure rel that was added
	 */
	@Override
	public LayoutPageTemplateStructureRel addLayoutPageTemplateStructureRel(
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel) {

		return _layoutPageTemplateStructureRelLocalService.
			addLayoutPageTemplateStructureRel(layoutPageTemplateStructureRel);
	}

	@Override
	public LayoutPageTemplateStructureRel addLayoutPageTemplateStructureRel(
			long userId, long groupId, long layoutPageTemplateStructureId,
			long segmentsExperienceId, String data,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelLocalService.
			addLayoutPageTemplateStructureRel(
				userId, groupId, layoutPageTemplateStructureId,
				segmentsExperienceId, data, serviceContext);
	}

	/**
	 * Creates a new layout page template structure rel with the primary key. Does not add the layout page template structure rel to the database.
	 *
	 * @param layoutPageTemplateStructureRelId the primary key for the new layout page template structure rel
	 * @return the new layout page template structure rel
	 */
	@Override
	public LayoutPageTemplateStructureRel createLayoutPageTemplateStructureRel(
		long layoutPageTemplateStructureRelId) {

		return _layoutPageTemplateStructureRelLocalService.
			createLayoutPageTemplateStructureRel(
				layoutPageTemplateStructureRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the layout page template structure rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRel the layout page template structure rel
	 * @return the layout page template structure rel that was removed
	 */
	@Override
	public LayoutPageTemplateStructureRel deleteLayoutPageTemplateStructureRel(
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel) {

		return _layoutPageTemplateStructureRelLocalService.
			deleteLayoutPageTemplateStructureRel(
				layoutPageTemplateStructureRel);
	}

	/**
	 * Deletes the layout page template structure rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRelId the primary key of the layout page template structure rel
	 * @return the layout page template structure rel that was removed
	 * @throws PortalException if a layout page template structure rel with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel deleteLayoutPageTemplateStructureRel(
			long layoutPageTemplateStructureRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelLocalService.
			deleteLayoutPageTemplateStructureRel(
				layoutPageTemplateStructureRelId);
	}

	@Override
	public void deleteLayoutPageTemplateStructureRelsBySegmentsExperienceId(
		long segmentsExperienceId) {

		_layoutPageTemplateStructureRelLocalService.
			deleteLayoutPageTemplateStructureRelsBySegmentsExperienceId(
				segmentsExperienceId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _layoutPageTemplateStructureRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _layoutPageTemplateStructureRelLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _layoutPageTemplateStructureRelLocalService.dynamicQuery();
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

		return _layoutPageTemplateStructureRelLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelModelImpl</code>.
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

		return _layoutPageTemplateStructureRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelModelImpl</code>.
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

		return _layoutPageTemplateStructureRelLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
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

		return _layoutPageTemplateStructureRelLocalService.dynamicQueryCount(
			dynamicQuery);
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

		return _layoutPageTemplateStructureRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public LayoutPageTemplateStructureRel fetchLayoutPageTemplateStructureRel(
		long layoutPageTemplateStructureRelId) {

		return _layoutPageTemplateStructureRelLocalService.
			fetchLayoutPageTemplateStructureRel(
				layoutPageTemplateStructureRelId);
	}

	@Override
	public LayoutPageTemplateStructureRel fetchLayoutPageTemplateStructureRel(
		long layoutPageTemplateStructureId, long segmentsExperienceId) {

		return _layoutPageTemplateStructureRelLocalService.
			fetchLayoutPageTemplateStructureRel(
				layoutPageTemplateStructureId, segmentsExperienceId);
	}

	/**
	 * Returns the layout page template structure rel matching the UUID and group.
	 *
	 * @param uuid the layout page template structure rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel
		fetchLayoutPageTemplateStructureRelByUuidAndGroupId(
			String uuid, long groupId) {

		return _layoutPageTemplateStructureRelLocalService.
			fetchLayoutPageTemplateStructureRelByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _layoutPageTemplateStructureRelLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _layoutPageTemplateStructureRelLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _layoutPageTemplateStructureRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the layout page template structure rel with the primary key.
	 *
	 * @param layoutPageTemplateStructureRelId the primary key of the layout page template structure rel
	 * @return the layout page template structure rel
	 * @throws PortalException if a layout page template structure rel with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel getLayoutPageTemplateStructureRel(
			long layoutPageTemplateStructureRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelLocalService.
			getLayoutPageTemplateStructureRel(layoutPageTemplateStructureRelId);
	}

	/**
	 * Returns the layout page template structure rel matching the UUID and group.
	 *
	 * @param uuid the layout page template structure rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout page template structure rel
	 * @throws PortalException if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel
			getLayoutPageTemplateStructureRelByUuidAndGroupId(
				String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelLocalService.
			getLayoutPageTemplateStructureRelByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the layout page template structure rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @return the range of layout page template structure rels
	 */
	@Override
	public java.util.List<LayoutPageTemplateStructureRel>
		getLayoutPageTemplateStructureRels(int start, int end) {

		return _layoutPageTemplateStructureRelLocalService.
			getLayoutPageTemplateStructureRels(start, end);
	}

	@Override
	public java.util.List<LayoutPageTemplateStructureRel>
		getLayoutPageTemplateStructureRels(long layoutPageTemplateStructureId) {

		return _layoutPageTemplateStructureRelLocalService.
			getLayoutPageTemplateStructureRels(layoutPageTemplateStructureId);
	}

	@Override
	public java.util.List<LayoutPageTemplateStructureRel>
		getLayoutPageTemplateStructureRelsBySegmentsExperienceId(
			long segmentsExperienceId) {

		return _layoutPageTemplateStructureRelLocalService.
			getLayoutPageTemplateStructureRelsBySegmentsExperienceId(
				segmentsExperienceId);
	}

	/**
	 * Returns all the layout page template structure rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout page template structure rels
	 * @param companyId the primary key of the company
	 * @return the matching layout page template structure rels, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<LayoutPageTemplateStructureRel>
		getLayoutPageTemplateStructureRelsByUuidAndCompanyId(
			String uuid, long companyId) {

		return _layoutPageTemplateStructureRelLocalService.
			getLayoutPageTemplateStructureRelsByUuidAndCompanyId(
				uuid, companyId);
	}

	/**
	 * Returns a range of layout page template structure rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout page template structure rels
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching layout page template structure rels, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<LayoutPageTemplateStructureRel>
		getLayoutPageTemplateStructureRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateStructureRel> orderByComparator) {

		return _layoutPageTemplateStructureRelLocalService.
			getLayoutPageTemplateStructureRelsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of layout page template structure rels.
	 *
	 * @return the number of layout page template structure rels
	 */
	@Override
	public int getLayoutPageTemplateStructureRelsCount() {
		return _layoutPageTemplateStructureRelLocalService.
			getLayoutPageTemplateStructureRelsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _layoutPageTemplateStructureRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the layout page template structure rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateStructureRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRel the layout page template structure rel
	 * @return the layout page template structure rel that was updated
	 */
	@Override
	public LayoutPageTemplateStructureRel updateLayoutPageTemplateStructureRel(
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel) {

		return _layoutPageTemplateStructureRelLocalService.
			updateLayoutPageTemplateStructureRel(
				layoutPageTemplateStructureRel);
	}

	@Override
	public LayoutPageTemplateStructureRel updateLayoutPageTemplateStructureRel(
			long layoutPageTemplateStructureId, long segmentsExperienceId,
			String data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelLocalService.
			updateLayoutPageTemplateStructureRel(
				layoutPageTemplateStructureId, segmentsExperienceId, data);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _layoutPageTemplateStructureRelLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<LayoutPageTemplateStructureRel> getCTPersistence() {
		return _layoutPageTemplateStructureRelLocalService.getCTPersistence();
	}

	@Override
	public Class<LayoutPageTemplateStructureRel> getModelClass() {
		return _layoutPageTemplateStructureRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<LayoutPageTemplateStructureRel>, R, E>
				updateUnsafeFunction)
		throws E {

		return _layoutPageTemplateStructureRelLocalService.
			updateWithUnsafeFunction(updateUnsafeFunction);
	}

	@Override
	public LayoutPageTemplateStructureRelLocalService getWrappedService() {
		return _layoutPageTemplateStructureRelLocalService;
	}

	@Override
	public void setWrappedService(
		LayoutPageTemplateStructureRelLocalService
			layoutPageTemplateStructureRelLocalService) {

		_layoutPageTemplateStructureRelLocalService =
			layoutPageTemplateStructureRelLocalService;
	}

	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2113082754