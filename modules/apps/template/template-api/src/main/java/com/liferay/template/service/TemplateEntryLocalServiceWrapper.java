/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.template.model.TemplateEntry;

/**
 * Provides a wrapper for {@link TemplateEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see TemplateEntryLocalService
 * @generated
 */
public class TemplateEntryLocalServiceWrapper
	implements ServiceWrapper<TemplateEntryLocalService>,
			   TemplateEntryLocalService {

	public TemplateEntryLocalServiceWrapper() {
		this(null);
	}

	public TemplateEntryLocalServiceWrapper(
		TemplateEntryLocalService templateEntryLocalService) {

		_templateEntryLocalService = templateEntryLocalService;
	}

	@Override
	public TemplateEntry addTemplateEntry(
			String externalReferenceCode, long userId, long groupId,
			long ddmTemplateId, String infoItemClassName,
			String infoItemFormVariationKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _templateEntryLocalService.addTemplateEntry(
			externalReferenceCode, userId, groupId, ddmTemplateId,
			infoItemClassName, infoItemFormVariationKey, serviceContext);
	}

	/**
	 * Adds the template entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TemplateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param templateEntry the template entry
	 * @return the template entry that was added
	 */
	@Override
	public TemplateEntry addTemplateEntry(TemplateEntry templateEntry) {
		return _templateEntryLocalService.addTemplateEntry(templateEntry);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _templateEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new template entry with the primary key. Does not add the template entry to the database.
	 *
	 * @param templateEntryId the primary key for the new template entry
	 * @return the new template entry
	 */
	@Override
	public TemplateEntry createTemplateEntry(long templateEntryId) {
		return _templateEntryLocalService.createTemplateEntry(templateEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _templateEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void deleteTemplateEntries(long groupId) {
		_templateEntryLocalService.deleteTemplateEntries(groupId);
	}

	/**
	 * Deletes the template entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TemplateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param templateEntryId the primary key of the template entry
	 * @return the template entry that was removed
	 * @throws PortalException if a template entry with the primary key could not be found
	 */
	@Override
	public TemplateEntry deleteTemplateEntry(long templateEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _templateEntryLocalService.deleteTemplateEntry(templateEntryId);
	}

	/**
	 * Deletes the template entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TemplateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param templateEntry the template entry
	 * @return the template entry that was removed
	 */
	@Override
	public TemplateEntry deleteTemplateEntry(TemplateEntry templateEntry) {
		return _templateEntryLocalService.deleteTemplateEntry(templateEntry);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _templateEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _templateEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _templateEntryLocalService.dynamicQuery();
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

		return _templateEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
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

		return _templateEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
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

		return _templateEntryLocalService.dynamicQuery(
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

		return _templateEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _templateEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public TemplateEntry fetchTemplateEntry(long templateEntryId) {
		return _templateEntryLocalService.fetchTemplateEntry(templateEntryId);
	}

	@Override
	public TemplateEntry fetchTemplateEntryByDDMTemplateId(long ddmTemplateId) {
		return _templateEntryLocalService.fetchTemplateEntryByDDMTemplateId(
			ddmTemplateId);
	}

	@Override
	public TemplateEntry fetchTemplateEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return _templateEntryLocalService.
			fetchTemplateEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the template entry matching the UUID and group.
	 *
	 * @param uuid the template entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	@Override
	public TemplateEntry fetchTemplateEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return _templateEntryLocalService.fetchTemplateEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _templateEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _templateEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _templateEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _templateEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _templateEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns a range of all the template entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @return the range of template entries
	 */
	@Override
	public java.util.List<TemplateEntry> getTemplateEntries(
		int start, int end) {

		return _templateEntryLocalService.getTemplateEntries(start, end);
	}

	@Override
	public java.util.List<TemplateEntry> getTemplateEntries(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TemplateEntry>
			orderByComparator) {

		return _templateEntryLocalService.getTemplateEntries(
			groupId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<TemplateEntry> getTemplateEntries(
		long groupId, String infoItemClassName, String infoItemFormVariationKey,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TemplateEntry>
			orderByComparator) {

		return _templateEntryLocalService.getTemplateEntries(
			groupId, infoItemClassName, infoItemFormVariationKey, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<TemplateEntry> getTemplateEntries(
		long[] groupIds, String infoItemClassName,
		String infoItemFormVariationKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TemplateEntry>
			orderByComparator) {

		return _templateEntryLocalService.getTemplateEntries(
			groupIds, infoItemClassName, infoItemFormVariationKey, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the template entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the template entries
	 * @param companyId the primary key of the company
	 * @return the matching template entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<TemplateEntry> getTemplateEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return _templateEntryLocalService.getTemplateEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of template entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the template entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching template entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<TemplateEntry> getTemplateEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TemplateEntry>
			orderByComparator) {

		return _templateEntryLocalService.getTemplateEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of template entries.
	 *
	 * @return the number of template entries
	 */
	@Override
	public int getTemplateEntriesCount() {
		return _templateEntryLocalService.getTemplateEntriesCount();
	}

	@Override
	public int getTemplateEntriesCount(long groupId) {
		return _templateEntryLocalService.getTemplateEntriesCount(groupId);
	}

	/**
	 * Returns the template entry with the primary key.
	 *
	 * @param templateEntryId the primary key of the template entry
	 * @return the template entry
	 * @throws PortalException if a template entry with the primary key could not be found
	 */
	@Override
	public TemplateEntry getTemplateEntry(long templateEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _templateEntryLocalService.getTemplateEntry(templateEntryId);
	}

	@Override
	public TemplateEntry getTemplateEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _templateEntryLocalService.
			getTemplateEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the template entry matching the UUID and group.
	 *
	 * @param uuid the template entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching template entry
	 * @throws PortalException if a matching template entry could not be found
	 */
	@Override
	public TemplateEntry getTemplateEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _templateEntryLocalService.getTemplateEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public TemplateEntry updateTemplateEntry(long templateEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _templateEntryLocalService.updateTemplateEntry(templateEntryId);
	}

	/**
	 * Updates the template entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TemplateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param templateEntry the template entry
	 * @return the template entry that was updated
	 */
	@Override
	public TemplateEntry updateTemplateEntry(TemplateEntry templateEntry) {
		return _templateEntryLocalService.updateTemplateEntry(templateEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _templateEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<TemplateEntry> getCTPersistence() {
		return _templateEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<TemplateEntry> getModelClass() {
		return _templateEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<TemplateEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _templateEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public TemplateEntryLocalService getWrappedService() {
		return _templateEntryLocalService;
	}

	@Override
	public void setWrappedService(
		TemplateEntryLocalService templateEntryLocalService) {

		_templateEntryLocalService = templateEntryLocalService;
	}

	private TemplateEntryLocalService _templateEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:542573647