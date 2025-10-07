/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPDefinition;
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
 * Provides the local service utility for CPDefinition. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPDefinitionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CPDefinitionLocalService
 * @generated
 */
public class CPDefinitionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPDefinitionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the cp definition to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinition the cp definition
	 * @return the cp definition that was added
	 */
	public static CPDefinition addCPDefinition(CPDefinition cpDefinition) {
		return getService().addCPDefinition(cpDefinition);
	}

	public static CPDefinition addCPDefinition(
			String externalReferenceCode, long userId, long groupId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> shortDescriptionMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> urlTitleMap,
			Map<java.util.Locale, String> metaTitleMap,
			Map<java.util.Locale, String> metaDescriptionMap,
			Map<java.util.Locale, String> metaKeywordsMap,
			String productTypeName, boolean ignoreSKUCombinations,
			boolean shippable, boolean freeShipping, boolean shipSeparately,
			double shippingExtraPrice, double width, double height,
			double depth, double weight, long cpTaxCategoryId,
			boolean taxExempt, boolean telcoOrElectronics,
			String ddmStructureKey, boolean published, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, String defaultSku, boolean subscriptionEnabled,
			int subscriptionLength, String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPDefinition(
			externalReferenceCode, userId, groupId, nameMap,
			shortDescriptionMap, descriptionMap, urlTitleMap, metaTitleMap,
			metaDescriptionMap, metaKeywordsMap, productTypeName,
			ignoreSKUCombinations, shippable, freeShipping, shipSeparately,
			shippingExtraPrice, width, height, depth, weight, cpTaxCategoryId,
			taxExempt, telcoOrElectronics, ddmStructureKey, published,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, defaultSku, subscriptionEnabled, subscriptionLength,
			subscriptionType, subscriptionTypeSettingsUnicodeProperties,
			maxSubscriptionCycles, deliverySubscriptionEnabled,
			deliverySubscriptionLength, deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles, status, serviceContext);
	}

	public static CPDefinition addCPDefinition(
			String externalReferenceCode, long userId, long groupId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> shortDescriptionMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> urlTitleMap,
			Map<java.util.Locale, String> metaTitleMap,
			Map<java.util.Locale, String> metaDescriptionMap,
			Map<java.util.Locale, String> metaKeywordsMap,
			String productTypeName, boolean ignoreSKUCombinations,
			boolean shippable, boolean freeShipping, boolean shipSeparately,
			double shippingExtraPrice, double width, double height,
			double depth, double weight, long cpTaxCategoryId,
			boolean taxExempt, boolean telcoOrElectronics,
			String ddmStructureKey, boolean published, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, String defaultSku, boolean subscriptionEnabled,
			int subscriptionLength, String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPDefinition(
			externalReferenceCode, userId, groupId, nameMap,
			shortDescriptionMap, descriptionMap, urlTitleMap, metaTitleMap,
			metaDescriptionMap, metaKeywordsMap, productTypeName,
			ignoreSKUCombinations, shippable, freeShipping, shipSeparately,
			shippingExtraPrice, width, height, depth, weight, cpTaxCategoryId,
			taxExempt, telcoOrElectronics, ddmStructureKey, published,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, defaultSku, subscriptionEnabled, subscriptionLength,
			subscriptionType, subscriptionTypeSettingsUnicodeProperties,
			maxSubscriptionCycles, status, serviceContext);
	}

	public static CPDefinition addOrUpdateCPDefinition(
			String externalReferenceCode, long userId, long cpDefinitionId,
			long groupId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> shortDescriptionMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> urlTitleMap,
			Map<java.util.Locale, String> metaTitleMap,
			Map<java.util.Locale, String> metaDescriptionMap,
			Map<java.util.Locale, String> metaKeywordsMap,
			String productTypeName, boolean ignoreSKUCombinations,
			boolean shippable, boolean freeShipping, boolean shipSeparately,
			double shippingExtraPrice, double width, double height,
			double depth, double weight, long cpTaxCategoryId,
			boolean taxExempt, boolean telcoOrElectronics,
			String ddmStructureKey, boolean published, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, String defaultSku, boolean subscriptionEnabled,
			int subscriptionLength, String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCPDefinition(
			externalReferenceCode, userId, cpDefinitionId, groupId, nameMap,
			shortDescriptionMap, descriptionMap, urlTitleMap, metaTitleMap,
			metaDescriptionMap, metaKeywordsMap, productTypeName,
			ignoreSKUCombinations, shippable, freeShipping, shipSeparately,
			shippingExtraPrice, width, height, depth, weight, cpTaxCategoryId,
			taxExempt, telcoOrElectronics, ddmStructureKey, published,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, defaultSku, subscriptionEnabled, subscriptionLength,
			subscriptionType, subscriptionTypeSettingsUnicodeProperties,
			maxSubscriptionCycles, deliverySubscriptionEnabled,
			deliverySubscriptionLength, deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles, status, serviceContext);
	}

	public static CPDefinition addOrUpdateCPDefinition(
			String externalReferenceCode, long userId, long groupId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> shortDescriptionMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> urlTitleMap,
			Map<java.util.Locale, String> metaTitleMap,
			Map<java.util.Locale, String> metaDescriptionMap,
			Map<java.util.Locale, String> metaKeywordsMap,
			String productTypeName, boolean ignoreSKUCombinations,
			boolean shippable, boolean freeShipping, boolean shipSeparately,
			double shippingExtraPrice, double width, double height,
			double depth, double weight, long cpTaxCategoryId,
			boolean taxExempt, boolean telcoOrElectronics,
			String ddmStructureKey, boolean published, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, String defaultSku, boolean subscriptionEnabled,
			int subscriptionLength, String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCPDefinition(
			externalReferenceCode, userId, groupId, nameMap,
			shortDescriptionMap, descriptionMap, urlTitleMap, metaTitleMap,
			metaDescriptionMap, metaKeywordsMap, productTypeName,
			ignoreSKUCombinations, shippable, freeShipping, shipSeparately,
			shippingExtraPrice, width, height, depth, weight, cpTaxCategoryId,
			taxExempt, telcoOrElectronics, ddmStructureKey, published,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, defaultSku, subscriptionEnabled, subscriptionLength,
			subscriptionType, subscriptionTypeSettingsUnicodeProperties,
			maxSubscriptionCycles, status, serviceContext);
	}

	public static void checkCPDefinitions() throws PortalException {
		getService().checkCPDefinitions();
	}

	public static CPDefinition cloneCPDefinition(
			long userId, long cpDefinitionId, long groupId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().cloneCPDefinition(
			userId, cpDefinitionId, groupId, serviceContext);
	}

	public static CPDefinition copyCPDefinition(long sourceCPDefinitionId)
		throws PortalException {

		return getService().copyCPDefinition(sourceCPDefinitionId);
	}

	public static CPDefinition copyCPDefinition(
			long sourceCPDefinitionId, long groupId, int status)
		throws PortalException {

		return getService().copyCPDefinition(
			sourceCPDefinitionId, groupId, status);
	}

	/**
	 * Creates a new cp definition with the primary key. Does not add the cp definition to the database.
	 *
	 * @param CPDefinitionId the primary key for the new cp definition
	 * @return the new cp definition
	 */
	public static CPDefinition createCPDefinition(long CPDefinitionId) {
		return getService().createCPDefinition(CPDefinitionId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteAssetCategoryCPDefinition(
			long cpDefinitionId, long categoryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().deleteAssetCategoryCPDefinition(
			cpDefinitionId, categoryId, serviceContext);
	}

	/**
	 * Deletes the cp definition from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinition the cp definition
	 * @return the cp definition that was removed
	 * @throws PortalException
	 */
	public static CPDefinition deleteCPDefinition(CPDefinition cpDefinition)
		throws PortalException {

		return getService().deleteCPDefinition(cpDefinition);
	}

	/**
	 * Deletes the cp definition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPDefinitionId the primary key of the cp definition
	 * @return the cp definition that was removed
	 * @throws PortalException if a cp definition with the primary key could not be found
	 */
	public static CPDefinition deleteCPDefinition(long CPDefinitionId)
		throws PortalException {

		return getService().deleteCPDefinition(CPDefinitionId);
	}

	public static void deleteCPDefinitions(long companyId)
		throws PortalException {

		getService().deleteCPDefinitions(companyId);
	}

	public static void deleteCPDefinitions(long cProductId, int status) {
		getService().deleteCPDefinitions(cProductId, status);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionModelImpl</code>.
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

	public static CPDefinition fetchCPDefinition(long CPDefinitionId) {
		return getService().fetchCPDefinition(CPDefinitionId);
	}

	public static CPDefinition fetchCPDefinitionByCProductExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchCPDefinitionByCProductExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CPDefinition fetchCPDefinitionByCProductId(long cProductId) {
		return getService().fetchCPDefinitionByCProductId(cProductId);
	}

	public static CPDefinition fetchCPDefinitionByFriendlyURL(
		long groupId, String friendlyURL) {

		return getService().fetchCPDefinitionByFriendlyURL(
			groupId, friendlyURL);
	}

	/**
	 * Returns the cp definition matching the UUID and group.
	 *
	 * @param uuid the cp definition's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition, or <code>null</code> if a matching cp definition could not be found
	 */
	public static CPDefinition fetchCPDefinitionByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchCPDefinitionByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.commerce.product.model.CPDefinitionLocalization
		fetchCPDefinitionLocalization(long CPDefinitionId, String languageId) {

		return getService().fetchCPDefinitionLocalization(
			CPDefinitionId, languageId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the cp definition with the primary key.
	 *
	 * @param CPDefinitionId the primary key of the cp definition
	 * @return the cp definition
	 * @throws PortalException if a cp definition with the primary key could not be found
	 */
	public static CPDefinition getCPDefinition(long CPDefinitionId)
		throws PortalException {

		return getService().getCPDefinition(CPDefinitionId);
	}

	public static CPDefinition getCPDefinitionByCProductId(long cProductId)
		throws PortalException {

		return getService().getCPDefinitionByCProductId(cProductId);
	}

	/**
	 * Returns the cp definition matching the UUID and group.
	 *
	 * @param uuid the cp definition's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition
	 * @throws PortalException if a matching cp definition could not be found
	 */
	public static CPDefinition getCPDefinitionByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getCPDefinitionByUuidAndGroupId(uuid, groupId);
	}

	public static Map<java.util.Locale, String> getCPDefinitionDescriptionMap(
		long cpDefinitionId) {

		return getService().getCPDefinitionDescriptionMap(cpDefinitionId);
	}

	public static com.liferay.commerce.product.model.CPDefinitionLocalization
			getCPDefinitionLocalization(long CPDefinitionId, String languageId)
		throws PortalException {

		return getService().getCPDefinitionLocalization(
			CPDefinitionId, languageId);
	}

	public static List<String> getCPDefinitionLocalizationLanguageIds(
		long cpDefinitionId) {

		return getService().getCPDefinitionLocalizationLanguageIds(
			cpDefinitionId);
	}

	public static List
		<com.liferay.commerce.product.model.CPDefinitionLocalization>
			getCPDefinitionLocalizations(long CPDefinitionId) {

		return getService().getCPDefinitionLocalizations(CPDefinitionId);
	}

	public static Map<java.util.Locale, String>
		getCPDefinitionMetaDescriptionMap(long cpDefinitionId) {

		return getService().getCPDefinitionMetaDescriptionMap(cpDefinitionId);
	}

	public static Map<java.util.Locale, String> getCPDefinitionMetaKeywordsMap(
		long cpDefinitionId) {

		return getService().getCPDefinitionMetaKeywordsMap(cpDefinitionId);
	}

	public static Map<java.util.Locale, String> getCPDefinitionMetaTitleMap(
		long cpDefinitionId) {

		return getService().getCPDefinitionMetaTitleMap(cpDefinitionId);
	}

	public static Map<java.util.Locale, String> getCPDefinitionNameMap(
		long cpDefinitionId) {

		return getService().getCPDefinitionNameMap(cpDefinitionId);
	}

	/**
	 * Returns a range of all the cp definitions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp definitions
	 * @param end the upper bound of the range of cp definitions (not inclusive)
	 * @return the range of cp definitions
	 */
	public static List<CPDefinition> getCPDefinitions(int start, int end) {
		return getService().getCPDefinitions(start, end);
	}

	public static List<CPDefinition> getCPDefinitions(
		long groupId, boolean subscriptionEnabled) {

		return getService().getCPDefinitions(groupId, subscriptionEnabled);
	}

	public static List<CPDefinition> getCPDefinitions(
		long groupId, int status, int start, int end) {

		return getService().getCPDefinitions(groupId, status, start, end);
	}

	public static List<CPDefinition> getCPDefinitions(
		long groupId, int status, int start, int end,
		OrderByComparator<CPDefinition> orderByComparator) {

		return getService().getCPDefinitions(
			groupId, status, start, end, orderByComparator);
	}

	public static List<CPDefinition> getCPDefinitions(
		long groupId, String productTypeName, String languageId, int status,
		int start, int end, OrderByComparator<CPDefinition> orderByComparator) {

		return getService().getCPDefinitions(
			groupId, productTypeName, languageId, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the cp definitions matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definitions
	 * @param companyId the primary key of the company
	 * @return the matching cp definitions, or an empty list if no matches were found
	 */
	public static List<CPDefinition> getCPDefinitionsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getCPDefinitionsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of cp definitions matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definitions
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of cp definitions
	 * @param end the upper bound of the range of cp definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching cp definitions, or an empty list if no matches were found
	 */
	public static List<CPDefinition> getCPDefinitionsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDefinition> orderByComparator) {

		return getService().getCPDefinitionsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp definitions.
	 *
	 * @return the number of cp definitions
	 */
	public static int getCPDefinitionsCount() {
		return getService().getCPDefinitionsCount();
	}

	public static int getCPDefinitionsCount(
		long groupId, boolean subscriptionEnabled) {

		return getService().getCPDefinitionsCount(groupId, subscriptionEnabled);
	}

	public static int getCPDefinitionsCount(long groupId, int status) {
		return getService().getCPDefinitionsCount(groupId, status);
	}

	public static int getCPDefinitionsCount(
		long groupId, String productTypeName, String languageId, int status) {

		return getService().getCPDefinitionsCount(
			groupId, productTypeName, languageId, status);
	}

	public static Map<java.util.Locale, String>
		getCPDefinitionShortDescriptionMap(long cpDefinitionId) {

		return getService().getCPDefinitionShortDescriptionMap(cpDefinitionId);
	}

	public static CPDefinition getCProductCPDefinition(
			long cProductId, int version)
		throws PortalException {

		return getService().getCProductCPDefinition(cProductId, version);
	}

	public static List<CPDefinition> getCProductCPDefinitions(
		long cProductId, int status, int start, int end) {

		return getService().getCProductCPDefinitions(
			cProductId, status, start, end);
	}

	public static List<CPDefinition> getCProductCPDefinitions(
		long cProductId, int status, int start, int end,
		OrderByComparator<CPDefinition> orderByComparator) {

		return getService().getCProductCPDefinitions(
			cProductId, status, start, end, orderByComparator);
	}

	public static com.liferay.commerce.product.model.CPAttachmentFileEntry
			getDefaultImageCPAttachmentFileEntry(long cpDefinitionId)
		throws PortalException {

		return getService().getDefaultImageCPAttachmentFileEntry(
			cpDefinitionId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static List<com.liferay.portal.kernel.search.facet.Facet> getFacets(
		String filterFields, String filterValues,
		com.liferay.portal.kernel.search.SearchContext searchContext) {

		return getService().getFacets(
			filterFields, filterValues, searchContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static String getLayoutPageTemplateEntryUuid(
		long groupId, long cpDefinitionId) {

		return getService().getLayoutPageTemplateEntryUuid(
			groupId, cpDefinitionId);
	}

	public static String getLayoutUuid(long groupId, long cpDefinitionId) {
		return getService().getLayoutUuid(groupId, cpDefinitionId);
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

	public static Map<java.util.Locale, String> getUrlTitleMap(
		long cpDefinitionId) {

		return getService().getUrlTitleMap(cpDefinitionId);
	}

	public static String getUrlTitleMapAsXML(long cpDefinitionId)
		throws PortalException {

		return getService().getUrlTitleMapAsXML(cpDefinitionId);
	}

	public static boolean hasChildCPDefinitions(long cpDefinitionId) {
		return getService().hasChildCPDefinitions(cpDefinitionId);
	}

	public static boolean isPublishedCPDefinition(CPDefinition cpDefinition) {
		return getService().isPublishedCPDefinition(cpDefinition);
	}

	public static boolean isPublishedCPDefinition(long cpDefinitionId) {
		return getService().isPublishedCPDefinition(cpDefinitionId);
	}

	public static boolean isVersionable(CPDefinition cpDefinition) {
		return getService().isVersionable(cpDefinition);
	}

	public static boolean isVersionable(long cpDefinitionId) {
		return getService().isVersionable(cpDefinitionId);
	}

	public static boolean isVersionable(
		long cpDefinitionId,
		jakarta.servlet.http.HttpServletRequest httpServletRequest) {

		return getService().isVersionable(cpDefinitionId, httpServletRequest);
	}

	public static void maintainVersionThreshold(long companyId, long cProductId)
		throws PortalException {

		getService().maintainVersionThreshold(companyId, cProductId);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPDefinition> searchCPDefinitions(
				long companyId, long[] groupIds, String keywords, int status,
				boolean ignoreCommerceAccountGroup, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCPDefinitions(
			companyId, groupIds, keywords, status, ignoreCommerceAccountGroup,
			start, end, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPDefinition> searchCPDefinitions(
				long companyId, long[] groupIds, String keywords,
				String filterFields, String filterValues, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCPDefinitions(
			companyId, groupIds, keywords, filterFields, filterValues, start,
			end, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPDefinition> searchCPDefinitionsByChannelGroupId(
				long companyId, long[] groupIds, long commerceChannelGroupId,
				String keywords, int status, boolean ignoreCommerceAccountGroup,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCPDefinitionsByChannelGroupId(
			companyId, groupIds, commerceChannelGroupId, keywords, status,
			ignoreCommerceAccountGroup, start, end, sort);
	}

	public static void updateAsset(
			long userId, CPDefinition cpDefinition, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws PortalException {

		getService().updateAsset(
			userId, cpDefinition, assetCategoryIds, assetTagNames,
			assetLinkEntryIds, priority);
	}

	/**
	 * Updates the cp definition in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinition the cp definition
	 * @return the cp definition that was updated
	 */
	public static CPDefinition updateCPDefinition(CPDefinition cpDefinition) {
		return getService().updateCPDefinition(cpDefinition);
	}

	public static CPDefinition updateCPDefinition(
			long cpDefinitionId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> shortDescriptionMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> urlTitleMap,
			Map<java.util.Locale, String> metaTitleMap,
			Map<java.util.Locale, String> metaDescriptionMap,
			Map<java.util.Locale, String> metaKeywordsMap,
			boolean ignoreSKUCombinations, boolean shippable,
			boolean freeShipping, boolean shipSeparately,
			double shippingExtraPrice, double width, double height,
			double depth, double weight, long cpTaxCategoryId,
			boolean taxExempt, boolean telcoOrElectronics,
			String ddmStructureKey, boolean published, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPDefinition(
			cpDefinitionId, nameMap, shortDescriptionMap, descriptionMap,
			urlTitleMap, metaTitleMap, metaDescriptionMap, metaKeywordsMap,
			ignoreSKUCombinations, shippable, freeShipping, shipSeparately,
			shippingExtraPrice, width, height, depth, weight, cpTaxCategoryId,
			taxExempt, telcoOrElectronics, ddmStructureKey, published,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, serviceContext);
	}

	public static CPDefinition updateCPDefinition(
			long cpDefinitionId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> shortDescriptionMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> urlTitleMap,
			Map<java.util.Locale, String> metaTitleMap,
			Map<java.util.Locale, String> metaDescriptionMap,
			Map<java.util.Locale, String> metaKeywordsMap,
			boolean ignoreSKUCombinations, String ddmStructureKey,
			boolean published, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPDefinition(
			cpDefinitionId, nameMap, shortDescriptionMap, descriptionMap,
			urlTitleMap, metaTitleMap, metaDescriptionMap, metaKeywordsMap,
			ignoreSKUCombinations, ddmStructureKey, published, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	public static CPDefinition updateCPDefinitionAccountGroupFilter(
			long cpDefinitionId, boolean enable)
		throws PortalException {

		return getService().updateCPDefinitionAccountGroupFilter(
			cpDefinitionId, enable);
	}

	public static CPDefinition updateCPDefinitionCategorization(
			long cpDefinitionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPDefinitionCategorization(
			cpDefinitionId, serviceContext);
	}

	public static CPDefinition updateCPDefinitionChannelFilter(
			long cpDefinitionId, boolean enable)
		throws PortalException {

		return getService().updateCPDefinitionChannelFilter(
			cpDefinitionId, enable);
	}

	public static CPDefinition updateCPDefinitionIgnoreSKUCombinations(
			long cpDefinitionId, boolean ignoreSKUCombinations,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPDefinitionIgnoreSKUCombinations(
			cpDefinitionId, ignoreSKUCombinations, serviceContext);
	}

	public static com.liferay.commerce.product.model.CPDefinitionLocalization
			updateCPDefinitionLocalization(
				CPDefinition cpDefinition, String languageId, String name,
				String shortDescription, String description, String metaTitle,
				String metaDescription, String metaKeywords)
		throws PortalException {

		return getService().updateCPDefinitionLocalization(
			cpDefinition, languageId, name, shortDescription, description,
			metaTitle, metaDescription, metaKeywords);
	}

	public static List
		<com.liferay.commerce.product.model.CPDefinitionLocalization>
				updateCPDefinitionLocalizations(
					CPDefinition cpDefinition, Map<String, String> nameMap,
					Map<String, String> shortDescriptionMap,
					Map<String, String> descriptionMap,
					Map<String, String> metaTitleMap,
					Map<String, String> metaDescriptionMap,
					Map<String, String> metaKeywordsMap)
			throws PortalException {

		return getService().updateCPDefinitionLocalizations(
			cpDefinition, nameMap, shortDescriptionMap, descriptionMap,
			metaTitleMap, metaDescriptionMap, metaKeywordsMap);
	}

	public static void updateCPDefinitionsByCPTaxCategoryId(
			long cpTaxCategoryId)
		throws PortalException {

		getService().updateCPDefinitionsByCPTaxCategoryId(cpTaxCategoryId);
	}

	public static CPDefinition updateExternalReferenceCode(
			String externalReferenceCode, long cpDefinitionId)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			externalReferenceCode, cpDefinitionId);
	}

	public static CPDefinition updateShippingInfo(
			long cpDefinitionId, boolean shippable, boolean freeShipping,
			boolean shipSeparately, double shippingExtraPrice, double width,
			double height, double depth, double weight,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateShippingInfo(
			cpDefinitionId, shippable, freeShipping, shipSeparately,
			shippingExtraPrice, width, height, depth, weight, serviceContext);
	}

	public static CPDefinition updateStatus(
			long userId, long cpDefinitionId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		return getService().updateStatus(
			userId, cpDefinitionId, status, serviceContext, workflowContext);
	}

	public static CPDefinition updateSubscriptionInfo(
			long cpDefinitionId, boolean subscriptionEnabled,
			int subscriptionLength, String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles)
		throws PortalException {

		return getService().updateSubscriptionInfo(
			cpDefinitionId, subscriptionEnabled, subscriptionLength,
			subscriptionType, subscriptionTypeSettingsUnicodeProperties,
			maxSubscriptionCycles, deliverySubscriptionEnabled,
			deliverySubscriptionLength, deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles);
	}

	public static CPDefinition updateTaxCategoryInfo(
			long cpDefinitionId, long cpTaxCategoryId, boolean taxExempt,
			boolean telcoOrElectronics)
		throws PortalException {

		return getService().updateTaxCategoryInfo(
			cpDefinitionId, cpTaxCategoryId, taxExempt, telcoOrElectronics);
	}

	public static CPDefinitionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPDefinitionLocalService> _serviceSnapshot =
		new Snapshot<>(
			CPDefinitionLocalServiceUtil.class, CPDefinitionLocalService.class);

}