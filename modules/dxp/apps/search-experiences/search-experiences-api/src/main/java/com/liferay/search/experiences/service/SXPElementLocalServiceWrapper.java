/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link SXPElementLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see SXPElementLocalService
 * @generated
 */
public class SXPElementLocalServiceWrapper
	implements ServiceWrapper<SXPElementLocalService>, SXPElementLocalService {

	public SXPElementLocalServiceWrapper() {
		this(null);
	}

	public SXPElementLocalServiceWrapper(
		SXPElementLocalService sxpElementLocalService) {

		_sxpElementLocalService = sxpElementLocalService;
	}

	@Override
	public com.liferay.search.experiences.model.SXPElement addSXPElement(
			String externalReferenceCode, long userId,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String elementDefinitionJSON, String fallbackDescription,
			String fallbackTitle, boolean readOnly, String schemaVersion,
			java.util.Map<java.util.Locale, String> titleMap, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpElementLocalService.addSXPElement(
			externalReferenceCode, userId, descriptionMap,
			elementDefinitionJSON, fallbackDescription, fallbackTitle, readOnly,
			schemaVersion, titleMap, type, serviceContext);
	}

	/**
	 * Adds the sxp element to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SXPElementLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sxpElement the sxp element
	 * @return the sxp element that was added
	 */
	@Override
	public com.liferay.search.experiences.model.SXPElement addSXPElement(
		com.liferay.search.experiences.model.SXPElement sxpElement) {

		return _sxpElementLocalService.addSXPElement(sxpElement);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpElementLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new sxp element with the primary key. Does not add the sxp element to the database.
	 *
	 * @param sxpElementId the primary key for the new sxp element
	 * @return the new sxp element
	 */
	@Override
	public com.liferay.search.experiences.model.SXPElement createSXPElement(
		long sxpElementId) {

		return _sxpElementLocalService.createSXPElement(sxpElementId);
	}

	@Override
	public void deleteCompanySXPElements(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_sxpElementLocalService.deleteCompanySXPElements(companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpElementLocalService.deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the sxp element with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SXPElementLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sxpElementId the primary key of the sxp element
	 * @return the sxp element that was removed
	 * @throws PortalException if a sxp element with the primary key could not be found
	 */
	@Override
	public com.liferay.search.experiences.model.SXPElement deleteSXPElement(
			long sxpElementId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpElementLocalService.deleteSXPElement(sxpElementId);
	}

	/**
	 * Deletes the sxp element from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SXPElementLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sxpElement the sxp element
	 * @return the sxp element that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.search.experiences.model.SXPElement deleteSXPElement(
			com.liferay.search.experiences.model.SXPElement sxpElement)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpElementLocalService.deleteSXPElement(sxpElement);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _sxpElementLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _sxpElementLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _sxpElementLocalService.dynamicQuery();
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

		return _sxpElementLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.search.experiences.model.impl.SXPElementModelImpl</code>.
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

		return _sxpElementLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.search.experiences.model.impl.SXPElementModelImpl</code>.
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

		return _sxpElementLocalService.dynamicQuery(
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

		return _sxpElementLocalService.dynamicQueryCount(dynamicQuery);
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

		return _sxpElementLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.search.experiences.model.SXPElement fetchSXPElement(
		long sxpElementId) {

		return _sxpElementLocalService.fetchSXPElement(sxpElementId);
	}

	@Override
	public com.liferay.search.experiences.model.SXPElement
		fetchSXPElementByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _sxpElementLocalService.fetchSXPElementByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the sxp element with the matching UUID and company.
	 *
	 * @param uuid the sxp element's UUID
	 * @param companyId the primary key of the company
	 * @return the matching sxp element, or <code>null</code> if a matching sxp element could not be found
	 */
	@Override
	public com.liferay.search.experiences.model.SXPElement
		fetchSXPElementByUuidAndCompanyId(String uuid, long companyId) {

		return _sxpElementLocalService.fetchSXPElementByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _sxpElementLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _sxpElementLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _sxpElementLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _sxpElementLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpElementLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the sxp element with the primary key.
	 *
	 * @param sxpElementId the primary key of the sxp element
	 * @return the sxp element
	 * @throws PortalException if a sxp element with the primary key could not be found
	 */
	@Override
	public com.liferay.search.experiences.model.SXPElement getSXPElement(
			long sxpElementId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpElementLocalService.getSXPElement(sxpElementId);
	}

	@Override
	public com.liferay.search.experiences.model.SXPElement
			getSXPElementByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpElementLocalService.getSXPElementByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the sxp element with the matching UUID and company.
	 *
	 * @param uuid the sxp element's UUID
	 * @param companyId the primary key of the company
	 * @return the matching sxp element
	 * @throws PortalException if a matching sxp element could not be found
	 */
	@Override
	public com.liferay.search.experiences.model.SXPElement
			getSXPElementByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpElementLocalService.getSXPElementByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the sxp elements.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.search.experiences.model.impl.SXPElementModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of sxp elements
	 * @param end the upper bound of the range of sxp elements (not inclusive)
	 * @return the range of sxp elements
	 */
	@Override
	public java.util.List<com.liferay.search.experiences.model.SXPElement>
		getSXPElements(int start, int end) {

		return _sxpElementLocalService.getSXPElements(start, end);
	}

	@Override
	public java.util.List<com.liferay.search.experiences.model.SXPElement>
		getSXPElements(long companyId, boolean readOnly) {

		return _sxpElementLocalService.getSXPElements(companyId, readOnly);
	}

	/**
	 * Returns the number of sxp elements.
	 *
	 * @return the number of sxp elements
	 */
	@Override
	public int getSXPElementsCount() {
		return _sxpElementLocalService.getSXPElementsCount();
	}

	@Override
	public com.liferay.search.experiences.model.SXPElement updateSXPElement(
			String externalReferenceCode, long userId, long sxpElementId,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String elementDefinitionJSON, boolean hidden, String schemaVersion,
			java.util.Map<java.util.Locale, String> titleMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sxpElementLocalService.updateSXPElement(
			externalReferenceCode, userId, sxpElementId, descriptionMap,
			elementDefinitionJSON, hidden, schemaVersion, titleMap,
			serviceContext);
	}

	/**
	 * Updates the sxp element in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SXPElementLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sxpElement the sxp element
	 * @return the sxp element that was updated
	 */
	@Override
	public com.liferay.search.experiences.model.SXPElement updateSXPElement(
		com.liferay.search.experiences.model.SXPElement sxpElement) {

		return _sxpElementLocalService.updateSXPElement(sxpElement);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _sxpElementLocalService.getBasePersistence();
	}

	@Override
	public SXPElementLocalService getWrappedService() {
		return _sxpElementLocalService;
	}

	@Override
	public void setWrappedService(
		SXPElementLocalService sxpElementLocalService) {

		_sxpElementLocalService = sxpElementLocalService;
	}

	private SXPElementLocalService _sxpElementLocalService;

}