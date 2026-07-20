/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CPDefinitionVirtualSettingLocalService}.
 *
 * @author Marco Leo
 * @see CPDefinitionVirtualSettingLocalService
 * @generated
 */
public class CPDefinitionVirtualSettingLocalServiceWrapper
	implements CPDefinitionVirtualSettingLocalService,
			   ServiceWrapper<CPDefinitionVirtualSettingLocalService> {

	public CPDefinitionVirtualSettingLocalServiceWrapper() {
		this(null);
	}

	public CPDefinitionVirtualSettingLocalServiceWrapper(
		CPDefinitionVirtualSettingLocalService
			cpDefinitionVirtualSettingLocalService) {

		_cpDefinitionVirtualSettingLocalService =
			cpDefinitionVirtualSettingLocalService;
	}

	/**
	 * Adds the cp definition virtual setting to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionVirtualSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionVirtualSetting the cp definition virtual setting
	 * @return the cp definition virtual setting that was added
	 */
	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDefinitionVirtualSetting addCPDefinitionVirtualSetting(
				com.liferay.commerce.product.type.virtual.model.
					CPDefinitionVirtualSetting cpDefinitionVirtualSetting) {

		return _cpDefinitionVirtualSettingLocalService.
			addCPDefinitionVirtualSetting(cpDefinitionVirtualSetting);
	}

	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDefinitionVirtualSetting addCPDefinitionVirtualSetting(
					String className, long classPK, long fileEntryId,
					String url, int activationStatus, long duration,
					int maxUsages, boolean useSample, long sampleFileEntryId,
					String sampleURL, boolean termsOfUseRequired,
					java.util.Map<java.util.Locale, String>
						termsOfUseContentMap,
					long termsOfUseJournalArticleResourcePrimKey,
					boolean override,
					com.liferay.portal.kernel.service.ServiceContext
						serviceContext)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionVirtualSettingLocalService.
			addCPDefinitionVirtualSetting(
				className, classPK, fileEntryId, url, activationStatus,
				duration, maxUsages, useSample, sampleFileEntryId, sampleURL,
				termsOfUseRequired, termsOfUseContentMap,
				termsOfUseJournalArticleResourcePrimKey, override,
				serviceContext);
	}

	@Override
	public void cloneCPDefinitionVirtualSetting(
		long cpDefinitionId, long newCPDefinitionId) {

		_cpDefinitionVirtualSettingLocalService.cloneCPDefinitionVirtualSetting(
			cpDefinitionId, newCPDefinitionId);
	}

	/**
	 * Creates a new cp definition virtual setting with the primary key. Does not add the cp definition virtual setting to the database.
	 *
	 * @param CPDefinitionVirtualSettingId the primary key for the new cp definition virtual setting
	 * @return the new cp definition virtual setting
	 */
	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDefinitionVirtualSetting createCPDefinitionVirtualSetting(
				long CPDefinitionVirtualSettingId) {

		return _cpDefinitionVirtualSettingLocalService.
			createCPDefinitionVirtualSetting(CPDefinitionVirtualSettingId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionVirtualSettingLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the cp definition virtual setting from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionVirtualSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionVirtualSetting the cp definition virtual setting
	 * @return the cp definition virtual setting that was removed
	 */
	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDefinitionVirtualSetting deleteCPDefinitionVirtualSetting(
				com.liferay.commerce.product.type.virtual.model.
					CPDefinitionVirtualSetting cpDefinitionVirtualSetting) {

		return _cpDefinitionVirtualSettingLocalService.
			deleteCPDefinitionVirtualSetting(cpDefinitionVirtualSetting);
	}

	/**
	 * Deletes the cp definition virtual setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionVirtualSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPDefinitionVirtualSettingId the primary key of the cp definition virtual setting
	 * @return the cp definition virtual setting that was removed
	 * @throws PortalException if a cp definition virtual setting with the primary key could not be found
	 */
	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDefinitionVirtualSetting deleteCPDefinitionVirtualSetting(
					long CPDefinitionVirtualSettingId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionVirtualSettingLocalService.
			deleteCPDefinitionVirtualSetting(CPDefinitionVirtualSettingId);
	}

	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDefinitionVirtualSetting deleteCPDefinitionVirtualSetting(
					String className, long classPK)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionVirtualSettingLocalService.
			deleteCPDefinitionVirtualSetting(className, classPK);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionVirtualSettingLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpDefinitionVirtualSettingLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpDefinitionVirtualSettingLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpDefinitionVirtualSettingLocalService.dynamicQuery();
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

		return _cpDefinitionVirtualSettingLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDefinitionVirtualSettingModelImpl</code>.
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

		return _cpDefinitionVirtualSettingLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDefinitionVirtualSettingModelImpl</code>.
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

		return _cpDefinitionVirtualSettingLocalService.dynamicQuery(
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

		return _cpDefinitionVirtualSettingLocalService.dynamicQueryCount(
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

		return _cpDefinitionVirtualSettingLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDefinitionVirtualSetting fetchCPDefinitionVirtualSetting(
				long CPDefinitionVirtualSettingId) {

		return _cpDefinitionVirtualSettingLocalService.
			fetchCPDefinitionVirtualSetting(CPDefinitionVirtualSettingId);
	}

	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDefinitionVirtualSetting fetchCPDefinitionVirtualSetting(
				String className, long classPK) {

		return _cpDefinitionVirtualSettingLocalService.
			fetchCPDefinitionVirtualSetting(className, classPK);
	}

	/**
	 * Returns the cp definition virtual setting matching the UUID and group.
	 *
	 * @param uuid the cp definition virtual setting's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition virtual setting, or <code>null</code> if a matching cp definition virtual setting could not be found
	 */
	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDefinitionVirtualSetting
				fetchCPDefinitionVirtualSettingByUuidAndGroupId(
					String uuid, long groupId) {

		return _cpDefinitionVirtualSettingLocalService.
			fetchCPDefinitionVirtualSettingByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpDefinitionVirtualSettingLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the cp definition virtual setting with the primary key.
	 *
	 * @param CPDefinitionVirtualSettingId the primary key of the cp definition virtual setting
	 * @return the cp definition virtual setting
	 * @throws PortalException if a cp definition virtual setting with the primary key could not be found
	 */
	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDefinitionVirtualSetting getCPDefinitionVirtualSetting(
					long CPDefinitionVirtualSettingId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionVirtualSettingLocalService.
			getCPDefinitionVirtualSetting(CPDefinitionVirtualSettingId);
	}

	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDefinitionVirtualSetting getCPDefinitionVirtualSetting(
					String className, long classPK)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionVirtualSettingLocalService.
			getCPDefinitionVirtualSetting(className, classPK);
	}

	/**
	 * Returns the cp definition virtual setting matching the UUID and group.
	 *
	 * @param uuid the cp definition virtual setting's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition virtual setting
	 * @throws PortalException if a matching cp definition virtual setting could not be found
	 */
	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDefinitionVirtualSetting
					getCPDefinitionVirtualSettingByUuidAndGroupId(
						String uuid, long groupId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionVirtualSettingLocalService.
			getCPDefinitionVirtualSettingByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the cp definition virtual settings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDefinitionVirtualSettingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp definition virtual settings
	 * @param end the upper bound of the range of cp definition virtual settings (not inclusive)
	 * @return the range of cp definition virtual settings
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.product.type.virtual.model.
			CPDefinitionVirtualSetting> getCPDefinitionVirtualSettings(
				int start, int end) {

		return _cpDefinitionVirtualSettingLocalService.
			getCPDefinitionVirtualSettings(start, end);
	}

	/**
	 * Returns all the cp definition virtual settings matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition virtual settings
	 * @param companyId the primary key of the company
	 * @return the matching cp definition virtual settings, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.product.type.virtual.model.
			CPDefinitionVirtualSetting>
				getCPDefinitionVirtualSettingsByUuidAndCompanyId(
					String uuid, long companyId) {

		return _cpDefinitionVirtualSettingLocalService.
			getCPDefinitionVirtualSettingsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of cp definition virtual settings matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition virtual settings
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of cp definition virtual settings
	 * @param end the upper bound of the range of cp definition virtual settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching cp definition virtual settings, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.product.type.virtual.model.
			CPDefinitionVirtualSetting>
				getCPDefinitionVirtualSettingsByUuidAndCompanyId(
					String uuid, long companyId, int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.product.type.virtual.model.
							CPDefinitionVirtualSetting> orderByComparator) {

		return _cpDefinitionVirtualSettingLocalService.
			getCPDefinitionVirtualSettingsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp definition virtual settings.
	 *
	 * @return the number of cp definition virtual settings
	 */
	@Override
	public int getCPDefinitionVirtualSettingsCount() {
		return _cpDefinitionVirtualSettingLocalService.
			getCPDefinitionVirtualSettingsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _cpDefinitionVirtualSettingLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpDefinitionVirtualSettingLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpDefinitionVirtualSettingLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionVirtualSettingLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the cp definition virtual setting in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionVirtualSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionVirtualSetting the cp definition virtual setting
	 * @return the cp definition virtual setting that was updated
	 */
	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDefinitionVirtualSetting updateCPDefinitionVirtualSetting(
				com.liferay.commerce.product.type.virtual.model.
					CPDefinitionVirtualSetting cpDefinitionVirtualSetting) {

		return _cpDefinitionVirtualSettingLocalService.
			updateCPDefinitionVirtualSetting(cpDefinitionVirtualSetting);
	}

	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDefinitionVirtualSetting updateCPDefinitionVirtualSetting(
					long cpDefinitionVirtualSettingId, long fileEntryId,
					String url, int activationStatus, long duration,
					int maxUsages, boolean useSample, long sampleFileEntryId,
					String sampleURL, boolean termsOfUseRequired,
					java.util.Map<java.util.Locale, String>
						termsOfUseContentMap,
					long termsOfUseJournalArticleResourcePrimKey,
					boolean override,
					com.liferay.portal.kernel.service.ServiceContext
						serviceContext)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionVirtualSettingLocalService.
			updateCPDefinitionVirtualSetting(
				cpDefinitionVirtualSettingId, fileEntryId, url,
				activationStatus, duration, maxUsages, useSample,
				sampleFileEntryId, sampleURL, termsOfUseRequired,
				termsOfUseContentMap, termsOfUseJournalArticleResourcePrimKey,
				override, serviceContext);
	}

	@Override
	public
		com.liferay.commerce.product.type.virtual.model.
			CPDefinitionVirtualSetting updateCPDefinitionVirtualSetting(
					long cpDefinitionVirtualSettingId, long fileEntryId,
					String url, int activationStatus, long duration,
					int maxUsages, boolean useSample, long sampleFileEntryId,
					String sampleURL, boolean termsOfUseRequired,
					java.util.Map<java.util.Locale, String>
						termsOfUseContentMap,
					long termsOfUseJournalArticleResourcePrimKey,
					com.liferay.portal.kernel.service.ServiceContext
						serviceContext)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionVirtualSettingLocalService.
			updateCPDefinitionVirtualSetting(
				cpDefinitionVirtualSettingId, fileEntryId, url,
				activationStatus, duration, maxUsages, useSample,
				sampleFileEntryId, sampleURL, termsOfUseRequired,
				termsOfUseContentMap, termsOfUseJournalArticleResourcePrimKey,
				serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpDefinitionVirtualSettingLocalService.getBasePersistence();
	}

	@Override
	public CPDefinitionVirtualSettingLocalService getWrappedService() {
		return _cpDefinitionVirtualSettingLocalService;
	}

	@Override
	public void setWrappedService(
		CPDefinitionVirtualSettingLocalService
			cpDefinitionVirtualSettingLocalService) {

		_cpDefinitionVirtualSettingLocalService =
			cpDefinitionVirtualSettingLocalService;
	}

	private CPDefinitionVirtualSettingLocalService
		_cpDefinitionVirtualSettingLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1829592922