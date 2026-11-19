/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service;

import com.liferay.knowledge.base.model.KBTemplate;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link KBTemplateLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see KBTemplateLocalService
 * @generated
 */
public class KBTemplateLocalServiceWrapper
	implements KBTemplateLocalService, ServiceWrapper<KBTemplateLocalService> {

	public KBTemplateLocalServiceWrapper() {
		this(null);
	}

	public KBTemplateLocalServiceWrapper(
		KBTemplateLocalService kbTemplateLocalService) {

		_kbTemplateLocalService = kbTemplateLocalService;
	}

	/**
	 * Adds the kb template to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbTemplate the kb template
	 * @return the kb template that was added
	 */
	@Override
	public KBTemplate addKBTemplate(KBTemplate kbTemplate) {
		return _kbTemplateLocalService.addKBTemplate(kbTemplate);
	}

	@Override
	public KBTemplate addKBTemplate(
			long userId, String title, String content,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbTemplateLocalService.addKBTemplate(
			userId, title, content, serviceContext);
	}

	/**
	 * Creates a new kb template with the primary key. Does not add the kb template to the database.
	 *
	 * @param kbTemplateId the primary key for the new kb template
	 * @return the new kb template
	 */
	@Override
	public KBTemplate createKBTemplate(long kbTemplateId) {
		return _kbTemplateLocalService.createKBTemplate(kbTemplateId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbTemplateLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteGroupKBTemplates(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kbTemplateLocalService.deleteGroupKBTemplates(groupId);
	}

	/**
	 * Deletes the kb template from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbTemplate the kb template
	 * @return the kb template that was removed
	 * @throws PortalException
	 */
	@Override
	public KBTemplate deleteKBTemplate(KBTemplate kbTemplate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbTemplateLocalService.deleteKBTemplate(kbTemplate);
	}

	/**
	 * Deletes the kb template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbTemplateId the primary key of the kb template
	 * @return the kb template that was removed
	 * @throws PortalException if a kb template with the primary key could not be found
	 */
	@Override
	public KBTemplate deleteKBTemplate(long kbTemplateId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbTemplateLocalService.deleteKBTemplate(kbTemplateId);
	}

	@Override
	public void deleteKBTemplates(long[] kbTemplateIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kbTemplateLocalService.deleteKBTemplates(kbTemplateIds);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbTemplateLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _kbTemplateLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _kbTemplateLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _kbTemplateLocalService.dynamicQuery();
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

		return _kbTemplateLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBTemplateModelImpl</code>.
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

		return _kbTemplateLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBTemplateModelImpl</code>.
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

		return _kbTemplateLocalService.dynamicQuery(
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

		return _kbTemplateLocalService.dynamicQueryCount(dynamicQuery);
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

		return _kbTemplateLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public KBTemplate fetchKBTemplate(long kbTemplateId) {
		return _kbTemplateLocalService.fetchKBTemplate(kbTemplateId);
	}

	/**
	 * Returns the kb template matching the UUID and group.
	 *
	 * @param uuid the kb template's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kb template, or <code>null</code> if a matching kb template could not be found
	 */
	@Override
	public KBTemplate fetchKBTemplateByUuidAndGroupId(
		String uuid, long groupId) {

		return _kbTemplateLocalService.fetchKBTemplateByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _kbTemplateLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _kbTemplateLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _kbTemplateLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kb template with the primary key.
	 *
	 * @param kbTemplateId the primary key of the kb template
	 * @return the kb template
	 * @throws PortalException if a kb template with the primary key could not be found
	 */
	@Override
	public KBTemplate getKBTemplate(long kbTemplateId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbTemplateLocalService.getKBTemplate(kbTemplateId);
	}

	/**
	 * Returns the kb template matching the UUID and group.
	 *
	 * @param uuid the kb template's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kb template
	 * @throws PortalException if a matching kb template could not be found
	 */
	@Override
	public KBTemplate getKBTemplateByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbTemplateLocalService.getKBTemplateByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the kb templates.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kb templates
	 * @param end the upper bound of the range of kb templates (not inclusive)
	 * @return the range of kb templates
	 */
	@Override
	public java.util.List<KBTemplate> getKBTemplates(int start, int end) {
		return _kbTemplateLocalService.getKBTemplates(start, end);
	}

	/**
	 * Returns all the kb templates matching the UUID and company.
	 *
	 * @param uuid the UUID of the kb templates
	 * @param companyId the primary key of the company
	 * @return the matching kb templates, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<KBTemplate> getKBTemplatesByUuidAndCompanyId(
		String uuid, long companyId) {

		return _kbTemplateLocalService.getKBTemplatesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of kb templates matching the UUID and company.
	 *
	 * @param uuid the UUID of the kb templates
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of kb templates
	 * @param end the upper bound of the range of kb templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching kb templates, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<KBTemplate> getKBTemplatesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBTemplate>
			orderByComparator) {

		return _kbTemplateLocalService.getKBTemplatesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of kb templates.
	 *
	 * @return the number of kb templates
	 */
	@Override
	public int getKBTemplatesCount() {
		return _kbTemplateLocalService.getKBTemplatesCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kbTemplateLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbTemplateLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<KBTemplate> search(
		long groupId, String title, String content, java.util.Date startDate,
		java.util.Date endDate, boolean andOperator, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBTemplate>
			orderByComparator) {

		return _kbTemplateLocalService.search(
			groupId, title, content, startDate, endDate, andOperator, start,
			end, orderByComparator);
	}

	/**
	 * Updates the kb template in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbTemplate the kb template
	 * @return the kb template that was updated
	 */
	@Override
	public KBTemplate updateKBTemplate(KBTemplate kbTemplate) {
		return _kbTemplateLocalService.updateKBTemplate(kbTemplate);
	}

	@Override
	public KBTemplate updateKBTemplate(
			long kbTemplateId, String title, String content,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbTemplateLocalService.updateKBTemplate(
			kbTemplateId, title, content, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _kbTemplateLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<KBTemplate> getCTPersistence() {
		return _kbTemplateLocalService.getCTPersistence();
	}

	@Override
	public Class<KBTemplate> getModelClass() {
		return _kbTemplateLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<KBTemplate>, R, E>
				updateUnsafeFunction)
		throws E {

		return _kbTemplateLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public KBTemplateLocalService getWrappedService() {
		return _kbTemplateLocalService;
	}

	@Override
	public void setWrappedService(
		KBTemplateLocalService kbTemplateLocalService) {

		_kbTemplateLocalService = kbTemplateLocalService;
	}

	private KBTemplateLocalService _kbTemplateLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1115154707