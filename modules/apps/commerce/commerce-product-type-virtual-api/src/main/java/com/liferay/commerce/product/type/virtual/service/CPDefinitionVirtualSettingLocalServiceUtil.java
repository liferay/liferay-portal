/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.service;

import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for CPDefinitionVirtualSetting. This utility wraps
 * <code>com.liferay.commerce.product.type.virtual.service.impl.CPDefinitionVirtualSettingLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CPDefinitionVirtualSettingLocalService
 * @generated
 */
public class CPDefinitionVirtualSettingLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.type.virtual.service.impl.CPDefinitionVirtualSettingLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static CPDefinitionVirtualSetting addCPDefinitionVirtualSetting(
		CPDefinitionVirtualSetting cpDefinitionVirtualSetting) {

		return getService().addCPDefinitionVirtualSetting(
			cpDefinitionVirtualSetting);
	}

	public static CPDefinitionVirtualSetting addCPDefinitionVirtualSetting(
			String className, long classPK, long fileEntryId, String url,
			int activationStatus, long duration, int maxUsages,
			boolean useSample, long sampleFileEntryId, String sampleURL,
			boolean termsOfUseRequired,
			Map<java.util.Locale, String> termsOfUseContentMap,
			long termsOfUseJournalArticleResourcePrimKey, boolean override,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPDefinitionVirtualSetting(
			className, classPK, fileEntryId, url, activationStatus, duration,
			maxUsages, useSample, sampleFileEntryId, sampleURL,
			termsOfUseRequired, termsOfUseContentMap,
			termsOfUseJournalArticleResourcePrimKey, override, serviceContext);
	}

	public static void cloneCPDefinitionVirtualSetting(
		long cpDefinitionId, long newCPDefinitionId) {

		getService().cloneCPDefinitionVirtualSetting(
			cpDefinitionId, newCPDefinitionId);
	}

	/**
	 * Creates a new cp definition virtual setting with the primary key. Does not add the cp definition virtual setting to the database.
	 *
	 * @param CPDefinitionVirtualSettingId the primary key for the new cp definition virtual setting
	 * @return the new cp definition virtual setting
	 */
	public static CPDefinitionVirtualSetting createCPDefinitionVirtualSetting(
		long CPDefinitionVirtualSettingId) {

		return getService().createCPDefinitionVirtualSetting(
			CPDefinitionVirtualSettingId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
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
	public static CPDefinitionVirtualSetting deleteCPDefinitionVirtualSetting(
		CPDefinitionVirtualSetting cpDefinitionVirtualSetting) {

		return getService().deleteCPDefinitionVirtualSetting(
			cpDefinitionVirtualSetting);
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
	public static CPDefinitionVirtualSetting deleteCPDefinitionVirtualSetting(
			long CPDefinitionVirtualSettingId)
		throws PortalException {

		return getService().deleteCPDefinitionVirtualSetting(
			CPDefinitionVirtualSettingId);
	}

	public static CPDefinitionVirtualSetting deleteCPDefinitionVirtualSetting(
			String className, long classPK)
		throws PortalException {

		return getService().deleteCPDefinitionVirtualSetting(
			className, classPK);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static CPDefinitionVirtualSetting fetchCPDefinitionVirtualSetting(
		long CPDefinitionVirtualSettingId) {

		return getService().fetchCPDefinitionVirtualSetting(
			CPDefinitionVirtualSettingId);
	}

	public static CPDefinitionVirtualSetting fetchCPDefinitionVirtualSetting(
		String className, long classPK) {

		return getService().fetchCPDefinitionVirtualSetting(className, classPK);
	}

	/**
	 * Returns the cp definition virtual setting matching the UUID and group.
	 *
	 * @param uuid the cp definition virtual setting's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition virtual setting, or <code>null</code> if a matching cp definition virtual setting could not be found
	 */
	public static CPDefinitionVirtualSetting
		fetchCPDefinitionVirtualSettingByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchCPDefinitionVirtualSettingByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the cp definition virtual setting with the primary key.
	 *
	 * @param CPDefinitionVirtualSettingId the primary key of the cp definition virtual setting
	 * @return the cp definition virtual setting
	 * @throws PortalException if a cp definition virtual setting with the primary key could not be found
	 */
	public static CPDefinitionVirtualSetting getCPDefinitionVirtualSetting(
			long CPDefinitionVirtualSettingId)
		throws PortalException {

		return getService().getCPDefinitionVirtualSetting(
			CPDefinitionVirtualSettingId);
	}

	public static CPDefinitionVirtualSetting getCPDefinitionVirtualSetting(
			String className, long classPK)
		throws PortalException {

		return getService().getCPDefinitionVirtualSetting(className, classPK);
	}

	/**
	 * Returns the cp definition virtual setting matching the UUID and group.
	 *
	 * @param uuid the cp definition virtual setting's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition virtual setting
	 * @throws PortalException if a matching cp definition virtual setting could not be found
	 */
	public static CPDefinitionVirtualSetting
			getCPDefinitionVirtualSettingByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getCPDefinitionVirtualSettingByUuidAndGroupId(
			uuid, groupId);
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
	public static List<CPDefinitionVirtualSetting>
		getCPDefinitionVirtualSettings(int start, int end) {

		return getService().getCPDefinitionVirtualSettings(start, end);
	}

	/**
	 * Returns all the cp definition virtual settings matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition virtual settings
	 * @param companyId the primary key of the company
	 * @return the matching cp definition virtual settings, or an empty list if no matches were found
	 */
	public static List<CPDefinitionVirtualSetting>
		getCPDefinitionVirtualSettingsByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getCPDefinitionVirtualSettingsByUuidAndCompanyId(
			uuid, companyId);
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
	public static List<CPDefinitionVirtualSetting>
		getCPDefinitionVirtualSettingsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CPDefinitionVirtualSetting> orderByComparator) {

		return getService().getCPDefinitionVirtualSettingsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp definition virtual settings.
	 *
	 * @return the number of cp definition virtual settings
	 */
	public static int getCPDefinitionVirtualSettingsCount() {
		return getService().getCPDefinitionVirtualSettingsCount();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
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
	public static CPDefinitionVirtualSetting updateCPDefinitionVirtualSetting(
		CPDefinitionVirtualSetting cpDefinitionVirtualSetting) {

		return getService().updateCPDefinitionVirtualSetting(
			cpDefinitionVirtualSetting);
	}

	public static CPDefinitionVirtualSetting updateCPDefinitionVirtualSetting(
			long cpDefinitionVirtualSettingId, long fileEntryId, String url,
			int activationStatus, long duration, int maxUsages,
			boolean useSample, long sampleFileEntryId, String sampleURL,
			boolean termsOfUseRequired,
			Map<java.util.Locale, String> termsOfUseContentMap,
			long termsOfUseJournalArticleResourcePrimKey, boolean override,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPDefinitionVirtualSetting(
			cpDefinitionVirtualSettingId, fileEntryId, url, activationStatus,
			duration, maxUsages, useSample, sampleFileEntryId, sampleURL,
			termsOfUseRequired, termsOfUseContentMap,
			termsOfUseJournalArticleResourcePrimKey, override, serviceContext);
	}

	public static CPDefinitionVirtualSetting updateCPDefinitionVirtualSetting(
			long cpDefinitionVirtualSettingId, long fileEntryId, String url,
			int activationStatus, long duration, int maxUsages,
			boolean useSample, long sampleFileEntryId, String sampleURL,
			boolean termsOfUseRequired,
			Map<java.util.Locale, String> termsOfUseContentMap,
			long termsOfUseJournalArticleResourcePrimKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPDefinitionVirtualSetting(
			cpDefinitionVirtualSettingId, fileEntryId, url, activationStatus,
			duration, maxUsages, useSample, sampleFileEntryId, sampleURL,
			termsOfUseRequired, termsOfUseContentMap,
			termsOfUseJournalArticleResourcePrimKey, serviceContext);
	}

	public static CPDefinitionVirtualSettingLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPDefinitionVirtualSettingLocalService>
		_serviceSnapshot = new Snapshot<>(
			CPDefinitionVirtualSettingLocalServiceUtil.class,
			CPDefinitionVirtualSettingLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:644056326