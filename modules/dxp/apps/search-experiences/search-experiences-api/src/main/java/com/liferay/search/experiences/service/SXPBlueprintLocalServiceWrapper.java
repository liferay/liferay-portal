/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link SXPBlueprintLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see SXPBlueprintLocalService
 * @generated
 */
public class SXPBlueprintLocalServiceWrapper
	implements ServiceWrapper<SXPBlueprintLocalService>,
			   SXPBlueprintLocalService {

	public SXPBlueprintLocalServiceWrapper() {
		this(null);
	}

	public SXPBlueprintLocalServiceWrapper(
		SXPBlueprintLocalService sxpBlueprintLocalService) {

		_sxpBlueprintLocalService = sxpBlueprintLocalService;
	}

	@Override
	public com.liferay.search.experiences.model.SXPBlueprint addSXPBlueprint(
			String externalReferenceCode, long userId, String configurationJSON,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String elementInstancesJSON, String schemaVersion,
			java.util.Map<java.util.Locale, String> titleMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpBlueprintLocalService.addSXPBlueprint(
			externalReferenceCode, userId, configurationJSON, descriptionMap,
			elementInstancesJSON, schemaVersion, titleMap, serviceContext);
	}

	/**
	 * Adds the sxp blueprint to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SXPBlueprintLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sxpBlueprint the sxp blueprint
	 * @return the sxp blueprint that was added
	 */
	@Override
	public com.liferay.search.experiences.model.SXPBlueprint addSXPBlueprint(
		com.liferay.search.experiences.model.SXPBlueprint sxpBlueprint) {

		return _sxpBlueprintLocalService.addSXPBlueprint(sxpBlueprint);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpBlueprintLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new sxp blueprint with the primary key. Does not add the sxp blueprint to the database.
	 *
	 * @param sxpBlueprintId the primary key for the new sxp blueprint
	 * @return the new sxp blueprint
	 */
	@Override
	public com.liferay.search.experiences.model.SXPBlueprint createSXPBlueprint(
		long sxpBlueprintId) {

		return _sxpBlueprintLocalService.createSXPBlueprint(sxpBlueprintId);
	}

	@Override
	public void deleteCompanySXPBlueprints(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_sxpBlueprintLocalService.deleteCompanySXPBlueprints(companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpBlueprintLocalService.deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the sxp blueprint with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SXPBlueprintLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sxpBlueprintId the primary key of the sxp blueprint
	 * @return the sxp blueprint that was removed
	 * @throws PortalException if a sxp blueprint with the primary key could not be found
	 */
	@Override
	public com.liferay.search.experiences.model.SXPBlueprint deleteSXPBlueprint(
			long sxpBlueprintId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpBlueprintLocalService.deleteSXPBlueprint(sxpBlueprintId);
	}

	/**
	 * Deletes the sxp blueprint from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SXPBlueprintLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sxpBlueprint the sxp blueprint
	 * @return the sxp blueprint that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.search.experiences.model.SXPBlueprint deleteSXPBlueprint(
			com.liferay.search.experiences.model.SXPBlueprint sxpBlueprint)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpBlueprintLocalService.deleteSXPBlueprint(sxpBlueprint);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _sxpBlueprintLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _sxpBlueprintLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _sxpBlueprintLocalService.dynamicQuery();
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

		return _sxpBlueprintLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.search.experiences.model.impl.SXPBlueprintModelImpl</code>.
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

		return _sxpBlueprintLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.search.experiences.model.impl.SXPBlueprintModelImpl</code>.
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

		return _sxpBlueprintLocalService.dynamicQuery(
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

		return _sxpBlueprintLocalService.dynamicQueryCount(dynamicQuery);
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

		return _sxpBlueprintLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.search.experiences.model.SXPBlueprint fetchSXPBlueprint(
		long sxpBlueprintId) {

		return _sxpBlueprintLocalService.fetchSXPBlueprint(sxpBlueprintId);
	}

	@Override
	public com.liferay.search.experiences.model.SXPBlueprint
		fetchSXPBlueprintByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _sxpBlueprintLocalService.
			fetchSXPBlueprintByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the sxp blueprint with the matching UUID and company.
	 *
	 * @param uuid the sxp blueprint's UUID
	 * @param companyId the primary key of the company
	 * @return the matching sxp blueprint, or <code>null</code> if a matching sxp blueprint could not be found
	 */
	@Override
	public com.liferay.search.experiences.model.SXPBlueprint
		fetchSXPBlueprintByUuidAndCompanyId(String uuid, long companyId) {

		return _sxpBlueprintLocalService.fetchSXPBlueprintByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _sxpBlueprintLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _sxpBlueprintLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _sxpBlueprintLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _sxpBlueprintLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpBlueprintLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the sxp blueprint with the primary key.
	 *
	 * @param sxpBlueprintId the primary key of the sxp blueprint
	 * @return the sxp blueprint
	 * @throws PortalException if a sxp blueprint with the primary key could not be found
	 */
	@Override
	public com.liferay.search.experiences.model.SXPBlueprint getSXPBlueprint(
			long sxpBlueprintId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpBlueprintLocalService.getSXPBlueprint(sxpBlueprintId);
	}

	@Override
	public com.liferay.search.experiences.model.SXPBlueprint
			getSXPBlueprintByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpBlueprintLocalService.getSXPBlueprintByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the sxp blueprint with the matching UUID and company.
	 *
	 * @param uuid the sxp blueprint's UUID
	 * @param companyId the primary key of the company
	 * @return the matching sxp blueprint
	 * @throws PortalException if a matching sxp blueprint could not be found
	 */
	@Override
	public com.liferay.search.experiences.model.SXPBlueprint
			getSXPBlueprintByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpBlueprintLocalService.getSXPBlueprintByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the sxp blueprints.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.search.experiences.model.impl.SXPBlueprintModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of sxp blueprints
	 * @param end the upper bound of the range of sxp blueprints (not inclusive)
	 * @return the range of sxp blueprints
	 */
	@Override
	public java.util.List<com.liferay.search.experiences.model.SXPBlueprint>
		getSXPBlueprints(int start, int end) {

		return _sxpBlueprintLocalService.getSXPBlueprints(start, end);
	}

	@Override
	public java.util.List<com.liferay.search.experiences.model.SXPBlueprint>
		getSXPBlueprints(long companyId) {

		return _sxpBlueprintLocalService.getSXPBlueprints(companyId);
	}

	/**
	 * Returns the number of sxp blueprints.
	 *
	 * @return the number of sxp blueprints
	 */
	@Override
	public int getSXPBlueprintsCount() {
		return _sxpBlueprintLocalService.getSXPBlueprintsCount();
	}

	@Override
	public com.liferay.search.experiences.model.SXPBlueprint updateStatus(
			long userId, long sxpBlueprintId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpBlueprintLocalService.updateStatus(
			userId, sxpBlueprintId, status, serviceContext);
	}

	@Override
	public com.liferay.search.experiences.model.SXPBlueprint updateSXPBlueprint(
			String externalReferenceCode, long userId, long sxpBlueprintId,
			String configurationJSON,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String elementInstancesJSON, String schemaVersion,
			java.util.Map<java.util.Locale, String> titleMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpBlueprintLocalService.updateSXPBlueprint(
			externalReferenceCode, userId, sxpBlueprintId, configurationJSON,
			descriptionMap, elementInstancesJSON, schemaVersion, titleMap,
			serviceContext);
	}

	/**
	 * Updates the sxp blueprint in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SXPBlueprintLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sxpBlueprint the sxp blueprint
	 * @return the sxp blueprint that was updated
	 */
	@Override
	public com.liferay.search.experiences.model.SXPBlueprint updateSXPBlueprint(
		com.liferay.search.experiences.model.SXPBlueprint sxpBlueprint) {

		return _sxpBlueprintLocalService.updateSXPBlueprint(sxpBlueprint);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _sxpBlueprintLocalService.getBasePersistence();
	}

	@Override
	public SXPBlueprintLocalService getWrappedService() {
		return _sxpBlueprintLocalService;
	}

	@Override
	public void setWrappedService(
		SXPBlueprintLocalService sxpBlueprintLocalService) {

		_sxpBlueprintLocalService = sxpBlueprintLocalService;
	}

	private SXPBlueprintLocalService _sxpBlueprintLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-212745653