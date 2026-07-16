/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service;

import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationAudienceEntryRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService
 * @generated
 */
public class
	LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalServiceWrapper
		implements LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService,
				   ServiceWrapper
					   <LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService> {

	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalServiceWrapper() {
		this(null);
	}

	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalServiceWrapper(
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService
			layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService) {

		_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService =
			layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;
	}

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
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel) {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel);
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				long userId, long groupId, String audienceEntryERC,
				String layoutPageTemplateStructureRelElementVariationERC,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
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
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		createLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
			long
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId) {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			createLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			createPersistedModel(primaryKeyObj);
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
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel) {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
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
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				long
					layoutPageTemplateStructureRelElementVariationAudienceEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId);
	}

	@Override
	public void
		deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
			String layoutPageTemplateStructureRelElementVariationERC) {

		_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
				layoutPageTemplateStructureRelElementVariationERC);
	}

	@Override
	public void
		deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRelsByAudienceEntryERC(
			long companyId, String audienceEntryERC) {

		_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRelsByAudienceEntryERC(
				companyId, audienceEntryERC);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
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

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
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

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
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

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
			long
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId) {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			fetchLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId);
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchLayoutPageTemplateStructureRelElementVariationAudienceEntryRelByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
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
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchLayoutPageTemplateStructureRelElementVariationAudienceEntryRelByUuidAndGroupId(
			String uuid, long groupId) {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			fetchLayoutPageTemplateStructureRelElementVariationAudienceEntryRelByUuidAndGroupId(
				uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the layout page template structure rel element variation audience entry rel with the primary key.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationAudienceEntryRelId the primary key of the layout page template structure rel element variation audience entry rel
	 * @return the layout page template structure rel element variation audience entry rel
	 * @throws PortalException if a layout page template structure rel element variation audience entry rel with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				long
					layoutPageTemplateStructureRelElementVariationAudienceEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId);
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
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
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelByUuidAndGroupId(
				String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
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
	@Override
	public java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
				int start, int end) {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
				start, end);
	}

	@Override
	public java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
				String layoutPageTemplateStructureRelElementVariationERC) {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
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
	@Override
	public java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelsByUuidAndCompanyId(
				String uuid, long companyId) {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
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
	@Override
	public java.util.List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelsByUuidAndCompanyId(
				String uuid, long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator) {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of layout page template structure rel element variation audience entry rels.
	 *
	 * @return the number of layout page template structure rel element variation audience entry rels
	 */
	@Override
	public int
		getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelsCount() {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			getPersistedModel(primaryKeyObj);
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
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		updateLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel) {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			updateLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			getBasePersistence();
	}

	@Override
	public CTPersistence
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			getCTPersistence() {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			getCTPersistence();
	}

	@Override
	public Class<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
		getModelClass() {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction
				<CTPersistence
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>,
				 R, E> updateUnsafeFunction)
		throws E {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			updateWithUnsafeFunction(updateUnsafeFunction);
	}

	@Override
	public
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService
			getWrappedService() {

		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;
	}

	@Override
	public void setWrappedService(
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService
			layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService) {

		_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService =
			layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;
	}

	private
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService
			_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:220700058