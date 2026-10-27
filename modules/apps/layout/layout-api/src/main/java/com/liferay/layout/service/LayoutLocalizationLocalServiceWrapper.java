/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service;

import com.liferay.layout.model.LayoutLocalization;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link LayoutLocalizationLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutLocalizationLocalService
 * @generated
 */
public class LayoutLocalizationLocalServiceWrapper
	implements LayoutLocalizationLocalService,
			   ServiceWrapper<LayoutLocalizationLocalService> {

	public LayoutLocalizationLocalServiceWrapper() {
		this(null);
	}

	public LayoutLocalizationLocalServiceWrapper(
		LayoutLocalizationLocalService layoutLocalizationLocalService) {

		_layoutLocalizationLocalService = layoutLocalizationLocalService;
	}

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
	@Override
	public LayoutLocalization addLayoutLocalization(
		LayoutLocalization layoutLocalization) {

		return _layoutLocalizationLocalService.addLayoutLocalization(
			layoutLocalization);
	}

	@Override
	public LayoutLocalization addLayoutLocalization(
		long groupId, String content, String languageId, long plid,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _layoutLocalizationLocalService.addLayoutLocalization(
			groupId, content, languageId, plid, serviceContext);
	}

	/**
	 * Creates a new layout localization with the primary key. Does not add the layout localization to the database.
	 *
	 * @param layoutLocalizationId the primary key for the new layout localization
	 * @return the new layout localization
	 */
	@Override
	public LayoutLocalization createLayoutLocalization(
		long layoutLocalizationId) {

		return _layoutLocalizationLocalService.createLayoutLocalization(
			layoutLocalizationId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutLocalizationLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public LayoutLocalization deleteLayoutLocalization(
		LayoutLocalization layoutLocalization) {

		return _layoutLocalizationLocalService.deleteLayoutLocalization(
			layoutLocalization);
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
	@Override
	public LayoutLocalization deleteLayoutLocalization(
			long layoutLocalizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutLocalizationLocalService.deleteLayoutLocalization(
			layoutLocalizationId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutLocalizationLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _layoutLocalizationLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _layoutLocalizationLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _layoutLocalizationLocalService.dynamicQuery();
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

		return _layoutLocalizationLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _layoutLocalizationLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _layoutLocalizationLocalService.dynamicQuery(
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

		return _layoutLocalizationLocalService.dynamicQueryCount(dynamicQuery);
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

		return _layoutLocalizationLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public LayoutLocalization fetchLayoutLocalization(
		long layoutLocalizationId) {

		return _layoutLocalizationLocalService.fetchLayoutLocalization(
			layoutLocalizationId);
	}

	@Override
	public LayoutLocalization fetchLayoutLocalization(
		long groupId, String languageId, long plid) {

		return _layoutLocalizationLocalService.fetchLayoutLocalization(
			groupId, languageId, plid);
	}

	/**
	 * Returns the layout localization matching the UUID and group.
	 *
	 * @param uuid the layout localization's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout localization, or <code>null</code> if a matching layout localization could not be found
	 */
	@Override
	public LayoutLocalization fetchLayoutLocalizationByUuidAndGroupId(
		String uuid, long groupId) {

		return _layoutLocalizationLocalService.
			fetchLayoutLocalizationByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _layoutLocalizationLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _layoutLocalizationLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _layoutLocalizationLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the layout localization with the primary key.
	 *
	 * @param layoutLocalizationId the primary key of the layout localization
	 * @return the layout localization
	 * @throws PortalException if a layout localization with the primary key could not be found
	 */
	@Override
	public LayoutLocalization getLayoutLocalization(long layoutLocalizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutLocalizationLocalService.getLayoutLocalization(
			layoutLocalizationId);
	}

	@Override
	public LayoutLocalization getLayoutLocalization(
			String languageId, long plid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutLocalizationLocalService.getLayoutLocalization(
			languageId, plid);
	}

	/**
	 * Returns the layout localization matching the UUID and group.
	 *
	 * @param uuid the layout localization's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout localization
	 * @throws PortalException if a matching layout localization could not be found
	 */
	@Override
	public LayoutLocalization getLayoutLocalizationByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutLocalizationLocalService.
			getLayoutLocalizationByUuidAndGroupId(uuid, groupId);
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
	@Override
	public java.util.List<LayoutLocalization> getLayoutLocalizations(
		int start, int end) {

		return _layoutLocalizationLocalService.getLayoutLocalizations(
			start, end);
	}

	/**
	 * Returns all the layout localizations matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout localizations
	 * @param companyId the primary key of the company
	 * @return the matching layout localizations, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<LayoutLocalization>
		getLayoutLocalizationsByUuidAndCompanyId(String uuid, long companyId) {

		return _layoutLocalizationLocalService.
			getLayoutLocalizationsByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<LayoutLocalization>
		getLayoutLocalizationsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutLocalization>
				orderByComparator) {

		return _layoutLocalizationLocalService.
			getLayoutLocalizationsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of layout localizations.
	 *
	 * @return the number of layout localizations
	 */
	@Override
	public int getLayoutLocalizationsCount() {
		return _layoutLocalizationLocalService.getLayoutLocalizationsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _layoutLocalizationLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutLocalizationLocalService.getPersistedModel(primaryKeyObj);
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
	@Override
	public LayoutLocalization updateLayoutLocalization(
		LayoutLocalization layoutLocalization) {

		return _layoutLocalizationLocalService.updateLayoutLocalization(
			layoutLocalization);
	}

	@Override
	public LayoutLocalization updateLayoutLocalization(
		String content, String languageId, long plid,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _layoutLocalizationLocalService.updateLayoutLocalization(
			content, languageId, plid, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _layoutLocalizationLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<LayoutLocalization> getCTPersistence() {
		return _layoutLocalizationLocalService.getCTPersistence();
	}

	@Override
	public Class<LayoutLocalization> getModelClass() {
		return _layoutLocalizationLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<LayoutLocalization>, R, E>
				updateUnsafeFunction)
		throws E {

		return _layoutLocalizationLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public LayoutLocalizationLocalService getWrappedService() {
		return _layoutLocalizationLocalService;
	}

	@Override
	public void setWrappedService(
		LayoutLocalizationLocalService layoutLocalizationLocalService) {

		_layoutLocalizationLocalService = layoutLocalizationLocalService;
	}

	private LayoutLocalizationLocalService _layoutLocalizationLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2099013791