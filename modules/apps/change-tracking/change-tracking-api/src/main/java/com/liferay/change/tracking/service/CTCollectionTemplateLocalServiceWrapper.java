/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CTCollectionTemplateLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see CTCollectionTemplateLocalService
 * @generated
 */
public class CTCollectionTemplateLocalServiceWrapper
	implements CTCollectionTemplateLocalService,
			   ServiceWrapper<CTCollectionTemplateLocalService> {

	public CTCollectionTemplateLocalServiceWrapper() {
		this(null);
	}

	public CTCollectionTemplateLocalServiceWrapper(
		CTCollectionTemplateLocalService ctCollectionTemplateLocalService) {

		_ctCollectionTemplateLocalService = ctCollectionTemplateLocalService;
	}

	/**
	 * Adds the ct collection template to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCollectionTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctCollectionTemplate the ct collection template
	 * @return the ct collection template that was added
	 */
	@Override
	public com.liferay.change.tracking.model.CTCollectionTemplate
		addCTCollectionTemplate(
			com.liferay.change.tracking.model.CTCollectionTemplate
				ctCollectionTemplate) {

		return _ctCollectionTemplateLocalService.addCTCollectionTemplate(
			ctCollectionTemplate);
	}

	@Override
	public com.liferay.change.tracking.model.CTCollectionTemplate
			addCTCollectionTemplate(
				long userId, String name, String description, String json)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionTemplateLocalService.addCTCollectionTemplate(
			userId, name, description, json);
	}

	/**
	 * Creates a new ct collection template with the primary key. Does not add the ct collection template to the database.
	 *
	 * @param ctCollectionTemplateId the primary key for the new ct collection template
	 * @return the new ct collection template
	 */
	@Override
	public com.liferay.change.tracking.model.CTCollectionTemplate
		createCTCollectionTemplate(long ctCollectionTemplateId) {

		return _ctCollectionTemplateLocalService.createCTCollectionTemplate(
			ctCollectionTemplateId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionTemplateLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the ct collection template from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCollectionTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctCollectionTemplate the ct collection template
	 * @return the ct collection template that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.change.tracking.model.CTCollectionTemplate
			deleteCTCollectionTemplate(
				com.liferay.change.tracking.model.CTCollectionTemplate
					ctCollectionTemplate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionTemplateLocalService.deleteCTCollectionTemplate(
			ctCollectionTemplate);
	}

	/**
	 * Deletes the ct collection template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCollectionTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctCollectionTemplateId the primary key of the ct collection template
	 * @return the ct collection template that was removed
	 * @throws PortalException if a ct collection template with the primary key could not be found
	 */
	@Override
	public com.liferay.change.tracking.model.CTCollectionTemplate
			deleteCTCollectionTemplate(long ctCollectionTemplateId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionTemplateLocalService.deleteCTCollectionTemplate(
			ctCollectionTemplateId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionTemplateLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ctCollectionTemplateLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ctCollectionTemplateLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ctCollectionTemplateLocalService.dynamicQuery();
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

		return _ctCollectionTemplateLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTCollectionTemplateModelImpl</code>.
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

		return _ctCollectionTemplateLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTCollectionTemplateModelImpl</code>.
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

		return _ctCollectionTemplateLocalService.dynamicQuery(
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

		return _ctCollectionTemplateLocalService.dynamicQueryCount(
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

		return _ctCollectionTemplateLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.change.tracking.model.CTCollectionTemplate
		fetchCTCollectionTemplate(long ctCollectionTemplateId) {

		return _ctCollectionTemplateLocalService.fetchCTCollectionTemplate(
			ctCollectionTemplateId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ctCollectionTemplateLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the ct collection template with the primary key.
	 *
	 * @param ctCollectionTemplateId the primary key of the ct collection template
	 * @return the ct collection template
	 * @throws PortalException if a ct collection template with the primary key could not be found
	 */
	@Override
	public com.liferay.change.tracking.model.CTCollectionTemplate
			getCTCollectionTemplate(long ctCollectionTemplateId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionTemplateLocalService.getCTCollectionTemplate(
			ctCollectionTemplateId);
	}

	/**
	 * Returns a range of all the ct collection templates.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTCollectionTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ct collection templates
	 * @param end the upper bound of the range of ct collection templates (not inclusive)
	 * @return the range of ct collection templates
	 */
	@Override
	public java.util.List
		<com.liferay.change.tracking.model.CTCollectionTemplate>
			getCTCollectionTemplates(int start, int end) {

		return _ctCollectionTemplateLocalService.getCTCollectionTemplates(
			start, end);
	}

	@Override
	public java.util.List
		<com.liferay.change.tracking.model.CTCollectionTemplate>
			getCTCollectionTemplates(long companyId, int start, int end) {

		return _ctCollectionTemplateLocalService.getCTCollectionTemplates(
			companyId, start, end);
	}

	/**
	 * Returns the number of ct collection templates.
	 *
	 * @return the number of ct collection templates
	 */
	@Override
	public int getCTCollectionTemplatesCount() {
		return _ctCollectionTemplateLocalService.
			getCTCollectionTemplatesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ctCollectionTemplateLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ctCollectionTemplateLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionTemplateLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public java.util.Set<String> getTokens() {
		return _ctCollectionTemplateLocalService.getTokens();
	}

	@Override
	public String parseTokens(long ctCollectionTemplateId, String s) {
		return _ctCollectionTemplateLocalService.parseTokens(
			ctCollectionTemplateId, s);
	}

	/**
	 * Updates the ct collection template in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCollectionTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctCollectionTemplate the ct collection template
	 * @return the ct collection template that was updated
	 */
	@Override
	public com.liferay.change.tracking.model.CTCollectionTemplate
		updateCTCollectionTemplate(
			com.liferay.change.tracking.model.CTCollectionTemplate
				ctCollectionTemplate) {

		return _ctCollectionTemplateLocalService.updateCTCollectionTemplate(
			ctCollectionTemplate);
	}

	@Override
	public com.liferay.change.tracking.model.CTCollectionTemplate
			updateCTCollectionTemplate(
				long ctCollectionTemplateId, String name, String description,
				String json)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionTemplateLocalService.updateCTCollectionTemplate(
			ctCollectionTemplateId, name, description, json);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ctCollectionTemplateLocalService.getBasePersistence();
	}

	@Override
	public CTCollectionTemplateLocalService getWrappedService() {
		return _ctCollectionTemplateLocalService;
	}

	@Override
	public void setWrappedService(
		CTCollectionTemplateLocalService ctCollectionTemplateLocalService) {

		_ctCollectionTemplateLocalService = ctCollectionTemplateLocalService;
	}

	private CTCollectionTemplateLocalService _ctCollectionTemplateLocalService;

}