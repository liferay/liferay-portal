/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service;

import com.liferay.commerce.shop.by.diagram.model.CSDiagramSetting;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CSDiagramSettingLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CSDiagramSettingLocalService
 * @generated
 */
public class CSDiagramSettingLocalServiceWrapper
	implements CSDiagramSettingLocalService,
			   ServiceWrapper<CSDiagramSettingLocalService> {

	public CSDiagramSettingLocalServiceWrapper() {
		this(null);
	}

	public CSDiagramSettingLocalServiceWrapper(
		CSDiagramSettingLocalService csDiagramSettingLocalService) {

		_csDiagramSettingLocalService = csDiagramSettingLocalService;
	}

	/**
	 * Adds the cs diagram setting to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CSDiagramSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param csDiagramSetting the cs diagram setting
	 * @return the cs diagram setting that was added
	 */
	@Override
	public CSDiagramSetting addCSDiagramSetting(
		CSDiagramSetting csDiagramSetting) {

		return _csDiagramSettingLocalService.addCSDiagramSetting(
			csDiagramSetting);
	}

	@Override
	public CSDiagramSetting addCSDiagramSetting(
			long userId, long cpDefinitionId, long cpAttachmentFileEntryId,
			String color, double radius, String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramSettingLocalService.addCSDiagramSetting(
			userId, cpDefinitionId, cpAttachmentFileEntryId, color, radius,
			type);
	}

	/**
	 * Creates a new cs diagram setting with the primary key. Does not add the cs diagram setting to the database.
	 *
	 * @param CSDiagramSettingId the primary key for the new cs diagram setting
	 * @return the new cs diagram setting
	 */
	@Override
	public CSDiagramSetting createCSDiagramSetting(long CSDiagramSettingId) {
		return _csDiagramSettingLocalService.createCSDiagramSetting(
			CSDiagramSettingId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramSettingLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the cs diagram setting from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CSDiagramSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param csDiagramSetting the cs diagram setting
	 * @return the cs diagram setting that was removed
	 */
	@Override
	public CSDiagramSetting deleteCSDiagramSetting(
		CSDiagramSetting csDiagramSetting) {

		return _csDiagramSettingLocalService.deleteCSDiagramSetting(
			csDiagramSetting);
	}

	/**
	 * Deletes the cs diagram setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CSDiagramSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CSDiagramSettingId the primary key of the cs diagram setting
	 * @return the cs diagram setting that was removed
	 * @throws PortalException if a cs diagram setting with the primary key could not be found
	 */
	@Override
	public CSDiagramSetting deleteCSDiagramSetting(long CSDiagramSettingId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramSettingLocalService.deleteCSDiagramSetting(
			CSDiagramSettingId);
	}

	@Override
	public CSDiagramSetting deleteCSDiagramSettingByCPDefinitionId(
		long cpDefinitionId) {

		return _csDiagramSettingLocalService.
			deleteCSDiagramSettingByCPDefinitionId(cpDefinitionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramSettingLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _csDiagramSettingLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _csDiagramSettingLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _csDiagramSettingLocalService.dynamicQuery();
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

		return _csDiagramSettingLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shop.by.diagram.model.impl.CSDiagramSettingModelImpl</code>.
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

		return _csDiagramSettingLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shop.by.diagram.model.impl.CSDiagramSettingModelImpl</code>.
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

		return _csDiagramSettingLocalService.dynamicQuery(
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

		return _csDiagramSettingLocalService.dynamicQueryCount(dynamicQuery);
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

		return _csDiagramSettingLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CSDiagramSetting fetchCSDiagramSetting(long CSDiagramSettingId) {
		return _csDiagramSettingLocalService.fetchCSDiagramSetting(
			CSDiagramSettingId);
	}

	@Override
	public CSDiagramSetting fetchCSDiagramSettingByCPDefinitionId(
		long cpDefinitionId) {

		return _csDiagramSettingLocalService.
			fetchCSDiagramSettingByCPDefinitionId(cpDefinitionId);
	}

	/**
	 * Returns the cs diagram setting with the matching UUID and company.
	 *
	 * @param uuid the cs diagram setting's UUID
	 * @param companyId the primary key of the company
	 * @return the matching cs diagram setting, or <code>null</code> if a matching cs diagram setting could not be found
	 */
	@Override
	public CSDiagramSetting fetchCSDiagramSettingByUuidAndCompanyId(
		String uuid, long companyId) {

		return _csDiagramSettingLocalService.
			fetchCSDiagramSettingByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _csDiagramSettingLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the cs diagram setting with the primary key.
	 *
	 * @param CSDiagramSettingId the primary key of the cs diagram setting
	 * @return the cs diagram setting
	 * @throws PortalException if a cs diagram setting with the primary key could not be found
	 */
	@Override
	public CSDiagramSetting getCSDiagramSetting(long CSDiagramSettingId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramSettingLocalService.getCSDiagramSetting(
			CSDiagramSettingId);
	}

	/**
	 * Returns the cs diagram setting with the matching UUID and company.
	 *
	 * @param uuid the cs diagram setting's UUID
	 * @param companyId the primary key of the company
	 * @return the matching cs diagram setting
	 * @throws PortalException if a matching cs diagram setting could not be found
	 */
	@Override
	public CSDiagramSetting getCSDiagramSettingByUuidAndCompanyId(
			String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramSettingLocalService.
			getCSDiagramSettingByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the cs diagram settings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shop.by.diagram.model.impl.CSDiagramSettingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cs diagram settings
	 * @param end the upper bound of the range of cs diagram settings (not inclusive)
	 * @return the range of cs diagram settings
	 */
	@Override
	public java.util.List<CSDiagramSetting> getCSDiagramSettings(
		int start, int end) {

		return _csDiagramSettingLocalService.getCSDiagramSettings(start, end);
	}

	/**
	 * Returns the number of cs diagram settings.
	 *
	 * @return the number of cs diagram settings
	 */
	@Override
	public int getCSDiagramSettingsCount() {
		return _csDiagramSettingLocalService.getCSDiagramSettingsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _csDiagramSettingLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _csDiagramSettingLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _csDiagramSettingLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramSettingLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the cs diagram setting in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CSDiagramSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param csDiagramSetting the cs diagram setting
	 * @return the cs diagram setting that was updated
	 */
	@Override
	public CSDiagramSetting updateCSDiagramSetting(
		CSDiagramSetting csDiagramSetting) {

		return _csDiagramSettingLocalService.updateCSDiagramSetting(
			csDiagramSetting);
	}

	@Override
	public CSDiagramSetting updateCSDiagramSetting(
			long csDiagramSettingId, long cpAttachmentFileEntryId, String color,
			double radius, String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramSettingLocalService.updateCSDiagramSetting(
			csDiagramSettingId, cpAttachmentFileEntryId, color, radius, type);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _csDiagramSettingLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CSDiagramSetting> getCTPersistence() {
		return _csDiagramSettingLocalService.getCTPersistence();
	}

	@Override
	public Class<CSDiagramSetting> getModelClass() {
		return _csDiagramSettingLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CSDiagramSetting>, R, E>
				updateUnsafeFunction)
		throws E {

		return _csDiagramSettingLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CSDiagramSettingLocalService getWrappedService() {
		return _csDiagramSettingLocalService;
	}

	@Override
	public void setWrappedService(
		CSDiagramSettingLocalService csDiagramSettingLocalService) {

		_csDiagramSettingLocalService = csDiagramSettingLocalService;
	}

	private CSDiagramSettingLocalService _csDiagramSettingLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1666777043