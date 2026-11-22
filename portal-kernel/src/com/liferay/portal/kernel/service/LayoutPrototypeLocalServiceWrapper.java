/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link LayoutPrototypeLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPrototypeLocalService
 * @generated
 */
public class LayoutPrototypeLocalServiceWrapper
	implements LayoutPrototypeLocalService,
			   ServiceWrapper<LayoutPrototypeLocalService> {

	public LayoutPrototypeLocalServiceWrapper() {
		this(null);
	}

	public LayoutPrototypeLocalServiceWrapper(
		LayoutPrototypeLocalService layoutPrototypeLocalService) {

		_layoutPrototypeLocalService = layoutPrototypeLocalService;
	}

	/**
	 * Adds the layout prototype to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPrototypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPrototype the layout prototype
	 * @return the layout prototype that was added
	 */
	@Override
	public LayoutPrototype addLayoutPrototype(LayoutPrototype layoutPrototype) {
		return _layoutPrototypeLocalService.addLayoutPrototype(layoutPrototype);
	}

	@Override
	public LayoutPrototype addLayoutPrototype(
			long userId, long companyId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean active, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPrototypeLocalService.addLayoutPrototype(
			userId, companyId, nameMap, descriptionMap, active, serviceContext);
	}

	/**
	 * Creates a new layout prototype with the primary key. Does not add the layout prototype to the database.
	 *
	 * @param layoutPrototypeId the primary key for the new layout prototype
	 * @return the new layout prototype
	 */
	@Override
	public LayoutPrototype createLayoutPrototype(long layoutPrototypeId) {
		return _layoutPrototypeLocalService.createLayoutPrototype(
			layoutPrototypeId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPrototypeLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the layout prototype from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPrototypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPrototype the layout prototype
	 * @return the layout prototype that was removed
	 * @throws PortalException
	 */
	@Override
	public LayoutPrototype deleteLayoutPrototype(
			LayoutPrototype layoutPrototype)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPrototypeLocalService.deleteLayoutPrototype(
			layoutPrototype);
	}

	/**
	 * Deletes the layout prototype with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPrototypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPrototypeId the primary key of the layout prototype
	 * @return the layout prototype that was removed
	 * @throws PortalException if a layout prototype with the primary key could not be found
	 */
	@Override
	public LayoutPrototype deleteLayoutPrototype(long layoutPrototypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPrototypeLocalService.deleteLayoutPrototype(
			layoutPrototypeId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPrototypeLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _layoutPrototypeLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _layoutPrototypeLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _layoutPrototypeLocalService.dynamicQuery();
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

		return _layoutPrototypeLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutPrototypeModelImpl</code>.
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

		return _layoutPrototypeLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutPrototypeModelImpl</code>.
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

		return _layoutPrototypeLocalService.dynamicQuery(
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

		return _layoutPrototypeLocalService.dynamicQueryCount(dynamicQuery);
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

		return _layoutPrototypeLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public LayoutPrototype fetchLayoutPrototype(long layoutPrototypeId) {
		return _layoutPrototypeLocalService.fetchLayoutPrototype(
			layoutPrototypeId);
	}

	@Override
	public LayoutPrototype fetchLayoutPrototype(
		long companyId, String name, java.util.Locale locale) {

		return _layoutPrototypeLocalService.fetchLayoutPrototype(
			companyId, name, locale);
	}

	/**
	 * Returns the layout prototype with the matching UUID and company.
	 *
	 * @param uuid the layout prototype's UUID
	 * @param companyId the primary key of the company
	 * @return the matching layout prototype, or <code>null</code> if a matching layout prototype could not be found
	 */
	@Override
	public LayoutPrototype fetchLayoutPrototypeByUuidAndCompanyId(
		String uuid, long companyId) {

		return _layoutPrototypeLocalService.
			fetchLayoutPrototypeByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public LayoutPrototype fetchLayoutProtoype(long companyId, String name) {
		return _layoutPrototypeLocalService.fetchLayoutProtoype(
			companyId, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _layoutPrototypeLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _layoutPrototypeLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _layoutPrototypeLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the layout prototype with the primary key.
	 *
	 * @param layoutPrototypeId the primary key of the layout prototype
	 * @return the layout prototype
	 * @throws PortalException if a layout prototype with the primary key could not be found
	 */
	@Override
	public LayoutPrototype getLayoutPrototype(long layoutPrototypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPrototypeLocalService.getLayoutPrototype(
			layoutPrototypeId);
	}

	@Override
	public LayoutPrototype getLayoutPrototype(
			long companyId, String name, java.util.Locale locale)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPrototypeLocalService.getLayoutPrototype(
			companyId, name, locale);
	}

	/**
	 * Returns the layout prototype with the matching UUID and company.
	 *
	 * @param uuid the layout prototype's UUID
	 * @param companyId the primary key of the company
	 * @return the matching layout prototype
	 * @throws PortalException if a matching layout prototype could not be found
	 */
	@Override
	public LayoutPrototype getLayoutPrototypeByUuidAndCompanyId(
			String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPrototypeLocalService.
			getLayoutPrototypeByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the layout prototypes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout prototypes
	 * @param end the upper bound of the range of layout prototypes (not inclusive)
	 * @return the range of layout prototypes
	 */
	@Override
	public java.util.List<LayoutPrototype> getLayoutPrototypes(
		int start, int end) {

		return _layoutPrototypeLocalService.getLayoutPrototypes(start, end);
	}

	/**
	 * Returns the number of layout prototypes.
	 *
	 * @return the number of layout prototypes
	 */
	@Override
	public int getLayoutPrototypesCount() {
		return _layoutPrototypeLocalService.getLayoutPrototypesCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _layoutPrototypeLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPrototypeLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<LayoutPrototype> search(
		long companyId, Boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutPrototype>
			orderByComparator) {

		return _layoutPrototypeLocalService.search(
			companyId, active, start, end, orderByComparator);
	}

	/**
	 * Updates the layout prototype in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPrototypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPrototype the layout prototype
	 * @return the layout prototype that was updated
	 */
	@Override
	public LayoutPrototype updateLayoutPrototype(
		LayoutPrototype layoutPrototype) {

		return _layoutPrototypeLocalService.updateLayoutPrototype(
			layoutPrototype);
	}

	@Override
	public LayoutPrototype updateLayoutPrototype(
			long layoutPrototypeId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean active, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPrototypeLocalService.updateLayoutPrototype(
			layoutPrototypeId, nameMap, descriptionMap, active, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _layoutPrototypeLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<LayoutPrototype> getCTPersistence() {
		return _layoutPrototypeLocalService.getCTPersistence();
	}

	@Override
	public Class<LayoutPrototype> getModelClass() {
		return _layoutPrototypeLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<LayoutPrototype>, R, E>
				updateUnsafeFunction)
		throws E {

		return _layoutPrototypeLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public LayoutPrototypeLocalService getWrappedService() {
		return _layoutPrototypeLocalService;
	}

	@Override
	public void setWrappedService(
		LayoutPrototypeLocalService layoutPrototypeLocalService) {

		_layoutPrototypeLocalService = layoutPrototypeLocalService;
	}

	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1566913786