/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link LayoutSetPrototypeLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutSetPrototypeLocalService
 * @generated
 */
public class LayoutSetPrototypeLocalServiceWrapper
	implements LayoutSetPrototypeLocalService,
			   ServiceWrapper<LayoutSetPrototypeLocalService> {

	public LayoutSetPrototypeLocalServiceWrapper() {
		this(null);
	}

	public LayoutSetPrototypeLocalServiceWrapper(
		LayoutSetPrototypeLocalService layoutSetPrototypeLocalService) {

		_layoutSetPrototypeLocalService = layoutSetPrototypeLocalService;
	}

	/**
	 * Adds the layout set prototype to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSetPrototypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSetPrototype the layout set prototype
	 * @return the layout set prototype that was added
	 */
	@Override
	public LayoutSetPrototype addLayoutSetPrototype(
		LayoutSetPrototype layoutSetPrototype) {

		return _layoutSetPrototypeLocalService.addLayoutSetPrototype(
			layoutSetPrototype);
	}

	@Override
	public LayoutSetPrototype addLayoutSetPrototype(
			long userId, long companyId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean active, boolean layoutsUpdateable,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetPrototypeLocalService.addLayoutSetPrototype(
			userId, companyId, nameMap, descriptionMap, active,
			layoutsUpdateable, serviceContext);
	}

	/**
	 * Creates a new layout set prototype with the primary key. Does not add the layout set prototype to the database.
	 *
	 * @param layoutSetPrototypeId the primary key for the new layout set prototype
	 * @return the new layout set prototype
	 */
	@Override
	public LayoutSetPrototype createLayoutSetPrototype(
		long layoutSetPrototypeId) {

		return _layoutSetPrototypeLocalService.createLayoutSetPrototype(
			layoutSetPrototypeId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetPrototypeLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the layout set prototype from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSetPrototypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSetPrototype the layout set prototype
	 * @return the layout set prototype that was removed
	 * @throws PortalException
	 */
	@Override
	public LayoutSetPrototype deleteLayoutSetPrototype(
			LayoutSetPrototype layoutSetPrototype)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetPrototypeLocalService.deleteLayoutSetPrototype(
			layoutSetPrototype);
	}

	/**
	 * Deletes the layout set prototype with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSetPrototypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSetPrototypeId the primary key of the layout set prototype
	 * @return the layout set prototype that was removed
	 * @throws PortalException if a layout set prototype with the primary key could not be found
	 */
	@Override
	public LayoutSetPrototype deleteLayoutSetPrototype(
			long layoutSetPrototypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetPrototypeLocalService.deleteLayoutSetPrototype(
			layoutSetPrototypeId);
	}

	@Override
	public void deleteLayoutSetPrototypes()
		throws com.liferay.portal.kernel.exception.PortalException {

		_layoutSetPrototypeLocalService.deleteLayoutSetPrototypes();
	}

	@Override
	public void deleteNondefaultLayoutSetPrototypes(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_layoutSetPrototypeLocalService.deleteNondefaultLayoutSetPrototypes(
			companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetPrototypeLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _layoutSetPrototypeLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _layoutSetPrototypeLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _layoutSetPrototypeLocalService.dynamicQuery();
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

		return _layoutSetPrototypeLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetPrototypeModelImpl</code>.
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

		return _layoutSetPrototypeLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetPrototypeModelImpl</code>.
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

		return _layoutSetPrototypeLocalService.dynamicQuery(
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

		return _layoutSetPrototypeLocalService.dynamicQueryCount(dynamicQuery);
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

		return _layoutSetPrototypeLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public LayoutSetPrototype fetchLayoutSetPrototype(
		long layoutSetPrototypeId) {

		return _layoutSetPrototypeLocalService.fetchLayoutSetPrototype(
			layoutSetPrototypeId);
	}

	/**
	 * Returns the layout set prototype with the matching UUID and company.
	 *
	 * @param uuid the layout set prototype's UUID
	 * @param companyId the primary key of the company
	 * @return the matching layout set prototype, or <code>null</code> if a matching layout set prototype could not be found
	 */
	@Override
	public LayoutSetPrototype fetchLayoutSetPrototypeByUuidAndCompanyId(
		String uuid, long companyId) {

		return _layoutSetPrototypeLocalService.
			fetchLayoutSetPrototypeByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _layoutSetPrototypeLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _layoutSetPrototypeLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _layoutSetPrototypeLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the layout set prototype with the primary key.
	 *
	 * @param layoutSetPrototypeId the primary key of the layout set prototype
	 * @return the layout set prototype
	 * @throws PortalException if a layout set prototype with the primary key could not be found
	 */
	@Override
	public LayoutSetPrototype getLayoutSetPrototype(long layoutSetPrototypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetPrototypeLocalService.getLayoutSetPrototype(
			layoutSetPrototypeId);
	}

	/**
	 * Returns the layout set prototype with the matching UUID and company.
	 *
	 * @param uuid the layout set prototype's UUID
	 * @param companyId the primary key of the company
	 * @return the matching layout set prototype
	 * @throws PortalException if a matching layout set prototype could not be found
	 */
	@Override
	public LayoutSetPrototype getLayoutSetPrototypeByUuidAndCompanyId(
			String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetPrototypeLocalService.
			getLayoutSetPrototypeByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the layout set prototypes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @return the range of layout set prototypes
	 */
	@Override
	public java.util.List<LayoutSetPrototype> getLayoutSetPrototypes(
		int start, int end) {

		return _layoutSetPrototypeLocalService.getLayoutSetPrototypes(
			start, end);
	}

	@Override
	public java.util.List<LayoutSetPrototype> getLayoutSetPrototypes(
		long companyId) {

		return _layoutSetPrototypeLocalService.getLayoutSetPrototypes(
			companyId);
	}

	/**
	 * Returns the number of layout set prototypes.
	 *
	 * @return the number of layout set prototypes
	 */
	@Override
	public int getLayoutSetPrototypesCount() {
		return _layoutSetPrototypeLocalService.getLayoutSetPrototypesCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _layoutSetPrototypeLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetPrototypeLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<LayoutSetPrototype> search(
		long companyId, Boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetPrototype>
			orderByComparator) {

		return _layoutSetPrototypeLocalService.search(
			companyId, active, start, end, orderByComparator);
	}

	@Override
	public int searchCount(long companyId, Boolean active) {
		return _layoutSetPrototypeLocalService.searchCount(companyId, active);
	}

	/**
	 * Updates the layout set prototype in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSetPrototypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSetPrototype the layout set prototype
	 * @return the layout set prototype that was updated
	 */
	@Override
	public LayoutSetPrototype updateLayoutSetPrototype(
		LayoutSetPrototype layoutSetPrototype) {

		return _layoutSetPrototypeLocalService.updateLayoutSetPrototype(
			layoutSetPrototype);
	}

	@Override
	public LayoutSetPrototype updateLayoutSetPrototype(
			long layoutSetPrototypeId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean active, boolean layoutsUpdateable,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetPrototypeLocalService.updateLayoutSetPrototype(
			layoutSetPrototypeId, nameMap, descriptionMap, active,
			layoutsUpdateable, serviceContext);
	}

	@Override
	public LayoutSetPrototype updateLayoutSetPrototype(
			long layoutSetPrototypeId, String settings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetPrototypeLocalService.updateLayoutSetPrototype(
			layoutSetPrototypeId, settings);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _layoutSetPrototypeLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<LayoutSetPrototype> getCTPersistence() {
		return _layoutSetPrototypeLocalService.getCTPersistence();
	}

	@Override
	public Class<LayoutSetPrototype> getModelClass() {
		return _layoutSetPrototypeLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<LayoutSetPrototype>, R, E>
				updateUnsafeFunction)
		throws E {

		return _layoutSetPrototypeLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public LayoutSetPrototypeLocalService getWrappedService() {
		return _layoutSetPrototypeLocalService;
	}

	@Override
	public void setWrappedService(
		LayoutSetPrototypeLocalService layoutSetPrototypeLocalService) {

		_layoutSetPrototypeLocalService = layoutSetPrototypeLocalService;
	}

	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

}