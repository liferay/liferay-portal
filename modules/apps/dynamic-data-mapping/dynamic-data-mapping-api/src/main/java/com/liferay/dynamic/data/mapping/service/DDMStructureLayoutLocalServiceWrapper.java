/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DDMStructureLayoutLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DDMStructureLayoutLocalService
 * @generated
 */
public class DDMStructureLayoutLocalServiceWrapper
	implements DDMStructureLayoutLocalService,
			   ServiceWrapper<DDMStructureLayoutLocalService> {

	public DDMStructureLayoutLocalServiceWrapper() {
		this(null);
	}

	public DDMStructureLayoutLocalServiceWrapper(
		DDMStructureLayoutLocalService ddmStructureLayoutLocalService) {

		_ddmStructureLayoutLocalService = ddmStructureLayoutLocalService;
	}

	/**
	 * Adds the ddm structure layout to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructureLayout the ddm structure layout
	 * @return the ddm structure layout that was added
	 */
	@Override
	public DDMStructureLayout addDDMStructureLayout(
		DDMStructureLayout ddmStructureLayout) {

		return _ddmStructureLayoutLocalService.addDDMStructureLayout(
			ddmStructureLayout);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #addStructureLayout(long, long, long, String, long,
	 DDMFormLayout, ServiceContext)}
	 */
	@Deprecated
	@Override
	public DDMStructureLayout addStructureLayout(
			long userId, long groupId, long structureVersionId,
			com.liferay.dynamic.data.mapping.model.DDMFormLayout ddmFormLayout,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLayoutLocalService.addStructureLayout(
			userId, groupId, structureVersionId, ddmFormLayout, serviceContext);
	}

	@Override
	public DDMStructureLayout addStructureLayout(
			long userId, long groupId, long classNameId,
			String structureLayoutKey, long structureVersionId,
			com.liferay.dynamic.data.mapping.model.DDMFormLayout ddmFormLayout,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLayoutLocalService.addStructureLayout(
			userId, groupId, classNameId, structureLayoutKey,
			structureVersionId, ddmFormLayout, serviceContext);
	}

	@Override
	public DDMStructureLayout addStructureLayout(
			long userId, long groupId, long classNameId,
			String structureLayoutKey, long structureVersionId,
			java.util.Map<java.util.Locale, String> name,
			java.util.Map<java.util.Locale, String> description,
			String definition,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLayoutLocalService.addStructureLayout(
			userId, groupId, classNameId, structureLayoutKey,
			structureVersionId, name, description, definition, serviceContext);
	}

	/**
	 * Creates a new ddm structure layout with the primary key. Does not add the ddm structure layout to the database.
	 *
	 * @param structureLayoutId the primary key for the new ddm structure layout
	 * @return the new ddm structure layout
	 */
	@Override
	public DDMStructureLayout createDDMStructureLayout(long structureLayoutId) {
		return _ddmStructureLayoutLocalService.createDDMStructureLayout(
			structureLayoutId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLayoutLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the ddm structure layout from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructureLayout the ddm structure layout
	 * @return the ddm structure layout that was removed
	 */
	@Override
	public DDMStructureLayout deleteDDMStructureLayout(
		DDMStructureLayout ddmStructureLayout) {

		return _ddmStructureLayoutLocalService.deleteDDMStructureLayout(
			ddmStructureLayout);
	}

	/**
	 * Deletes the ddm structure layout with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param structureLayoutId the primary key of the ddm structure layout
	 * @return the ddm structure layout that was removed
	 * @throws PortalException if a ddm structure layout with the primary key could not be found
	 */
	@Override
	public DDMStructureLayout deleteDDMStructureLayout(long structureLayoutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLayoutLocalService.deleteDDMStructureLayout(
			structureLayoutId);
	}

	@Override
	public void deleteDDMStructureLayouts(
			long classNameId,
			com.liferay.dynamic.data.mapping.model.DDMStructureVersion
				ddmStructureVersion)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ddmStructureLayoutLocalService.deleteDDMStructureLayouts(
			classNameId, ddmStructureVersion);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLayoutLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public void deleteStructureLayout(DDMStructureLayout structureLayout) {
		_ddmStructureLayoutLocalService.deleteStructureLayout(structureLayout);
	}

	@Override
	public void deleteStructureLayout(long structureLayoutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ddmStructureLayoutLocalService.deleteStructureLayout(
			structureLayoutId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ddmStructureLayoutLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ddmStructureLayoutLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ddmStructureLayoutLocalService.dynamicQuery();
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

		return _ddmStructureLayoutLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutModelImpl</code>.
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

		return _ddmStructureLayoutLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutModelImpl</code>.
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

		return _ddmStructureLayoutLocalService.dynamicQuery(
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

		return _ddmStructureLayoutLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ddmStructureLayoutLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DDMStructureLayout fetchDDMStructureLayout(long structureLayoutId) {
		return _ddmStructureLayoutLocalService.fetchDDMStructureLayout(
			structureLayoutId);
	}

	/**
	 * Returns the ddm structure layout matching the UUID and group.
	 *
	 * @param uuid the ddm structure layout's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm structure layout, or <code>null</code> if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout fetchDDMStructureLayoutByUuidAndGroupId(
		String uuid, long groupId) {

		return _ddmStructureLayoutLocalService.
			fetchDDMStructureLayoutByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public DDMStructureLayout fetchStructureLayout(long structureLayoutId) {
		return _ddmStructureLayoutLocalService.fetchStructureLayout(
			structureLayoutId);
	}

	@Override
	public DDMStructureLayout fetchStructureLayout(
		long groupId, long classNameId, String structureLayoutKey) {

		return _ddmStructureLayoutLocalService.fetchStructureLayout(
			groupId, classNameId, structureLayoutKey);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ddmStructureLayoutLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the ddm structure layout with the primary key.
	 *
	 * @param structureLayoutId the primary key of the ddm structure layout
	 * @return the ddm structure layout
	 * @throws PortalException if a ddm structure layout with the primary key could not be found
	 */
	@Override
	public DDMStructureLayout getDDMStructureLayout(long structureLayoutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLayoutLocalService.getDDMStructureLayout(
			structureLayoutId);
	}

	/**
	 * Returns the ddm structure layout matching the UUID and group.
	 *
	 * @param uuid the ddm structure layout's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm structure layout
	 * @throws PortalException if a matching ddm structure layout could not be found
	 */
	@Override
	public DDMStructureLayout getDDMStructureLayoutByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLayoutLocalService.
			getDDMStructureLayoutByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the ddm structure layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm structure layouts
	 * @param end the upper bound of the range of ddm structure layouts (not inclusive)
	 * @return the range of ddm structure layouts
	 */
	@Override
	public java.util.List<DDMStructureLayout> getDDMStructureLayouts(
		int start, int end) {

		return _ddmStructureLayoutLocalService.getDDMStructureLayouts(
			start, end);
	}

	/**
	 * Returns all the ddm structure layouts matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm structure layouts
	 * @param companyId the primary key of the company
	 * @return the matching ddm structure layouts, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<DDMStructureLayout>
		getDDMStructureLayoutsByUuidAndCompanyId(String uuid, long companyId) {

		return _ddmStructureLayoutLocalService.
			getDDMStructureLayoutsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of ddm structure layouts matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm structure layouts
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of ddm structure layouts
	 * @param end the upper bound of the range of ddm structure layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching ddm structure layouts, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<DDMStructureLayout>
		getDDMStructureLayoutsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructureLayout>
				orderByComparator) {

		return _ddmStructureLayoutLocalService.
			getDDMStructureLayoutsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of ddm structure layouts.
	 *
	 * @return the number of ddm structure layouts
	 */
	@Override
	public int getDDMStructureLayoutsCount() {
		return _ddmStructureLayoutLocalService.getDDMStructureLayoutsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _ddmStructureLayoutLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ddmStructureLayoutLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ddmStructureLayoutLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLayoutLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public DDMStructureLayout getStructureLayout(long structureLayoutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLayoutLocalService.getStructureLayout(
			structureLayoutId);
	}

	@Override
	public DDMStructureLayout getStructureLayout(
			long groupId, long classNameId, String structureLayoutKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLayoutLocalService.getStructureLayout(
			groupId, classNameId, structureLayoutKey);
	}

	@Override
	public DDMStructureLayout getStructureLayoutByStructureVersionId(
			long structureVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLayoutLocalService.
			getStructureLayoutByStructureVersionId(structureVersionId);
	}

	@Override
	public com.liferay.dynamic.data.mapping.model.DDMFormLayout
		getStructureLayoutDDMFormLayout(DDMStructureLayout structureLayout) {

		return _ddmStructureLayoutLocalService.getStructureLayoutDDMFormLayout(
			structureLayout);
	}

	@Override
	public java.util.List<DDMStructureLayout> getStructureLayouts(
			long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLayoutLocalService.getStructureLayouts(
			groupId, start, end);
	}

	@Override
	public java.util.List<DDMStructureLayout> getStructureLayouts(
		long groupId, long classNameId, long structureVersionId) {

		return _ddmStructureLayoutLocalService.getStructureLayouts(
			groupId, classNameId, structureVersionId);
	}

	@Override
	public java.util.List<DDMStructureLayout> getStructureLayouts(
		long groupId, long classNameId, long structureVersionId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructureLayout>
			orderByComparator) {

		return _ddmStructureLayoutLocalService.getStructureLayouts(
			groupId, classNameId, structureVersionId, start, end,
			orderByComparator);
	}

	@Override
	public int getStructureLayoutsCount(long groupId) {
		return _ddmStructureLayoutLocalService.getStructureLayoutsCount(
			groupId);
	}

	@Override
	public int getStructureLayoutsCount(
		long groupId, long classNameId, long structureVersionId) {

		return _ddmStructureLayoutLocalService.getStructureLayoutsCount(
			groupId, classNameId, structureVersionId);
	}

	/**
	 * Updates the ddm structure layout in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructureLayout the ddm structure layout
	 * @return the ddm structure layout that was updated
	 */
	@Override
	public DDMStructureLayout updateDDMStructureLayout(
		DDMStructureLayout ddmStructureLayout) {

		return _ddmStructureLayoutLocalService.updateDDMStructureLayout(
			ddmStructureLayout);
	}

	@Override
	public DDMStructureLayout updateStructureLayout(
			long structureLayoutId,
			com.liferay.dynamic.data.mapping.model.DDMFormLayout ddmFormLayout,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLayoutLocalService.updateStructureLayout(
			structureLayoutId, ddmFormLayout, serviceContext);
	}

	@Override
	public DDMStructureLayout updateStructureLayout(
			long structureLayoutId, long structureVersionId,
			java.util.Map<java.util.Locale, String> name,
			java.util.Map<java.util.Locale, String> description,
			String definition,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLayoutLocalService.updateStructureLayout(
			structureLayoutId, structureVersionId, name, description,
			definition, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ddmStructureLayoutLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DDMStructureLayout> getCTPersistence() {
		return _ddmStructureLayoutLocalService.getCTPersistence();
	}

	@Override
	public Class<DDMStructureLayout> getModelClass() {
		return _ddmStructureLayoutLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DDMStructureLayout>, R, E>
				updateUnsafeFunction)
		throws E {

		return _ddmStructureLayoutLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DDMStructureLayoutLocalService getWrappedService() {
		return _ddmStructureLayoutLocalService;
	}

	@Override
	public void setWrappedService(
		DDMStructureLayoutLocalService ddmStructureLayoutLocalService) {

		_ddmStructureLayoutLocalService = ddmStructureLayoutLocalService;
	}

	private DDMStructureLayoutLocalService _ddmStructureLayoutLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1705599997