/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CPDefinitionLocalService}.
 *
 * @author Marco Leo
 * @see CPDefinitionLocalService
 * @generated
 */
public class CPDefinitionLocalServiceWrapper
	implements CPDefinitionLocalService,
			   ServiceWrapper<CPDefinitionLocalService> {

	public CPDefinitionLocalServiceWrapper() {
		this(null);
	}

	public CPDefinitionLocalServiceWrapper(
		CPDefinitionLocalService cpDefinitionLocalService) {

		_cpDefinitionLocalService = cpDefinitionLocalService;
	}

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
	@Override
	public CPDefinition addCPDefinition(CPDefinition cpDefinition) {
		return _cpDefinitionLocalService.addCPDefinition(cpDefinition);
	}

	@Override
	public CPDefinition addCPDefinition(
			String externalReferenceCode, long userId, long groupId,
			long cpTaxCategoryId, boolean accountGroupFilterEnabled,
			boolean channelFilterEnabled, String ddmStructureKey,
			String defaultSku, long deliveryMaxSubscriptionCycles,
			boolean deliverySubscriptionEnabled, int deliverySubscriptionLength,
			String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			double depth,
			java.util.Map<java.util.Locale, String> descriptionMap,
			int displayDateDay, int displayDateHour, int displayDateMinute,
			int displayDateMonth, int displayDateYear, int expirationDateDay,
			int expirationDateHour, int expirationDateMinute,
			int expirationDateMonth, int expirationDateYear,
			boolean freeShipping, double height, boolean ignoreSKUCombinations,
			long maxSubscriptionCycles,
			java.util.Map<java.util.Locale, String> metaDescriptionMap,
			java.util.Map<java.util.Locale, String> metaKeywordsMap,
			java.util.Map<java.util.Locale, String> metaTitleMap,
			java.util.Map<java.util.Locale, String> nameMap,
			boolean neverExpire, String productTypeName, boolean published,
			boolean shipSeparately, boolean shippable,
			double shippingExtraPrice,
			java.util.Map<java.util.Locale, String> shortDescriptionMap,
			boolean subscriptionEnabled, int subscriptionLength,
			String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			boolean taxExempt, boolean telcoOrElectronics,
			java.util.Map<java.util.Locale, String> urlTitleMap, double weight,
			double width, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.addCPDefinition(
			externalReferenceCode, userId, groupId, cpTaxCategoryId,
			accountGroupFilterEnabled, channelFilterEnabled, ddmStructureKey,
			defaultSku, deliveryMaxSubscriptionCycles,
			deliverySubscriptionEnabled, deliverySubscriptionLength,
			deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties, depth,
			descriptionMap, displayDateDay, displayDateHour, displayDateMinute,
			displayDateMonth, displayDateYear, expirationDateDay,
			expirationDateHour, expirationDateMinute, expirationDateMonth,
			expirationDateYear, freeShipping, height, ignoreSKUCombinations,
			maxSubscriptionCycles, metaDescriptionMap, metaKeywordsMap,
			metaTitleMap, nameMap, neverExpire, productTypeName, published,
			shipSeparately, shippable, shippingExtraPrice, shortDescriptionMap,
			subscriptionEnabled, subscriptionLength, subscriptionType,
			subscriptionTypeSettingsUnicodeProperties, taxExempt,
			telcoOrElectronics, urlTitleMap, weight, width, status,
			serviceContext);
	}

	@Override
	public CPDefinition addOrUpdateCPDefinition(
			String externalReferenceCode, long userId, long groupId,
			long cpDefinitionId, long cpTaxCategoryId,
			boolean accountGroupFilterEnabled, boolean channelFilterEnabled,
			String ddmStructureKey, String defaultSku,
			long deliveryMaxSubscriptionCycles,
			boolean deliverySubscriptionEnabled, int deliverySubscriptionLength,
			String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			double depth,
			java.util.Map<java.util.Locale, String> descriptionMap,
			int displayDateDay, int displayDateHour, int displayDateMinute,
			int displayDateMonth, int displayDateYear, int expirationDateDay,
			int expirationDateHour, int expirationDateMinute,
			int expirationDateMonth, int expirationDateYear,
			boolean freeShipping, double height, boolean ignoreSKUCombinations,
			long maxSubscriptionCycles,
			java.util.Map<java.util.Locale, String> metaDescriptionMap,
			java.util.Map<java.util.Locale, String> metaKeywordsMap,
			java.util.Map<java.util.Locale, String> metaTitleMap,
			java.util.Map<java.util.Locale, String> nameMap,
			boolean neverExpire, String productTypeName, boolean published,
			boolean shipSeparately, boolean shippable,
			double shippingExtraPrice,
			java.util.Map<java.util.Locale, String> shortDescriptionMap,
			boolean subscriptionEnabled, int subscriptionLength,
			String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			boolean taxExempt, boolean telcoOrElectronics,
			java.util.Map<java.util.Locale, String> urlTitleMap, double weight,
			double width, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.addOrUpdateCPDefinition(
			externalReferenceCode, userId, groupId, cpDefinitionId,
			cpTaxCategoryId, accountGroupFilterEnabled, channelFilterEnabled,
			ddmStructureKey, defaultSku, deliveryMaxSubscriptionCycles,
			deliverySubscriptionEnabled, deliverySubscriptionLength,
			deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties, depth,
			descriptionMap, displayDateDay, displayDateHour, displayDateMinute,
			displayDateMonth, displayDateYear, expirationDateDay,
			expirationDateHour, expirationDateMinute, expirationDateMonth,
			expirationDateYear, freeShipping, height, ignoreSKUCombinations,
			maxSubscriptionCycles, metaDescriptionMap, metaKeywordsMap,
			metaTitleMap, nameMap, neverExpire, productTypeName, published,
			shipSeparately, shippable, shippingExtraPrice, shortDescriptionMap,
			subscriptionEnabled, subscriptionLength, subscriptionType,
			subscriptionTypeSettingsUnicodeProperties, taxExempt,
			telcoOrElectronics, urlTitleMap, weight, width, status,
			serviceContext);
	}

	@Override
	public void checkCPDefinitions()
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionLocalService.checkCPDefinitions();
	}

	@Override
	public CPDefinition cloneCPDefinition(
			long userId, long cpDefinitionId, long groupId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.cloneCPDefinition(
			userId, cpDefinitionId, groupId, serviceContext);
	}

	@Override
	public CPDefinition copyCPDefinition(long sourceCPDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.copyCPDefinition(sourceCPDefinitionId);
	}

	@Override
	public CPDefinition copyCPDefinition(
			long sourceCPDefinitionId, long groupId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.copyCPDefinition(
			sourceCPDefinitionId, groupId, status);
	}

	/**
	 * Creates a new cp definition with the primary key. Does not add the cp definition to the database.
	 *
	 * @param CPDefinitionId the primary key for the new cp definition
	 * @return the new cp definition
	 */
	@Override
	public CPDefinition createCPDefinition(long CPDefinitionId) {
		return _cpDefinitionLocalService.createCPDefinition(CPDefinitionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteAssetCategoryCPDefinition(
			long cpDefinitionId, long categoryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionLocalService.deleteAssetCategoryCPDefinition(
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
	@Override
	public CPDefinition deleteCPDefinition(CPDefinition cpDefinition)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.deleteCPDefinition(cpDefinition);
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
	@Override
	public CPDefinition deleteCPDefinition(long CPDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.deleteCPDefinition(CPDefinitionId);
	}

	@Override
	public void deleteCPDefinitions(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionLocalService.deleteCPDefinitions(companyId);
	}

	@Override
	public void deleteCPDefinitions(long cProductId, int status) {
		_cpDefinitionLocalService.deleteCPDefinitions(cProductId, status);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpDefinitionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpDefinitionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpDefinitionLocalService.dynamicQuery();
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

		return _cpDefinitionLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _cpDefinitionLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _cpDefinitionLocalService.dynamicQuery(
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

		return _cpDefinitionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _cpDefinitionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CPDefinition fetchCPDefinition(long CPDefinitionId) {
		return _cpDefinitionLocalService.fetchCPDefinition(CPDefinitionId);
	}

	@Override
	public CPDefinition fetchCPDefinitionByCProductExternalReferenceCode(
		String externalReferenceCode, long companyId, boolean excludeDraft) {

		return _cpDefinitionLocalService.
			fetchCPDefinitionByCProductExternalReferenceCode(
				externalReferenceCode, companyId, excludeDraft);
	}

	@Override
	public CPDefinition fetchCPDefinitionByCProductId(
		long cProductId, boolean excludeDraft) {

		return _cpDefinitionLocalService.fetchCPDefinitionByCProductId(
			cProductId, excludeDraft);
	}

	@Override
	public CPDefinition fetchCPDefinitionByFriendlyURL(
		long groupId, String friendlyURL) {

		return _cpDefinitionLocalService.fetchCPDefinitionByFriendlyURL(
			groupId, friendlyURL);
	}

	/**
	 * Returns the cp definition matching the UUID and group.
	 *
	 * @param uuid the cp definition's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition, or <code>null</code> if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition fetchCPDefinitionByUuidAndGroupId(
		String uuid, long groupId) {

		return _cpDefinitionLocalService.fetchCPDefinitionByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.commerce.product.model.CPDefinitionLocalization
		fetchCPDefinitionLocalization(long CPDefinitionId, String languageId) {

		return _cpDefinitionLocalService.fetchCPDefinitionLocalization(
			CPDefinitionId, languageId);
	}

	@Override
	public java.util.List<CPDefinition> findByExpirationDate(
		java.util.Date expirationDate,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<CPDefinition>
			queryDefinition) {

		return _cpDefinitionLocalService.findByExpirationDate(
			expirationDate, queryDefinition);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpDefinitionLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the cp definition with the primary key.
	 *
	 * @param CPDefinitionId the primary key of the cp definition
	 * @return the cp definition
	 * @throws PortalException if a cp definition with the primary key could not be found
	 */
	@Override
	public CPDefinition getCPDefinition(long CPDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.getCPDefinition(CPDefinitionId);
	}

	@Override
	public CPDefinition getCPDefinitionByCProductId(long cProductId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.getCPDefinitionByCProductId(
			cProductId);
	}

	/**
	 * Returns the cp definition matching the UUID and group.
	 *
	 * @param uuid the cp definition's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition
	 * @throws PortalException if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition getCPDefinitionByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.getCPDefinitionByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public java.util.Map<java.util.Locale, String>
		getCPDefinitionDescriptionMap(long cpDefinitionId) {

		return _cpDefinitionLocalService.getCPDefinitionDescriptionMap(
			cpDefinitionId);
	}

	@Override
	public com.liferay.commerce.product.model.CPDefinitionLocalization
			getCPDefinitionLocalization(long CPDefinitionId, String languageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.getCPDefinitionLocalization(
			CPDefinitionId, languageId);
	}

	@Override
	public java.util.List<String> getCPDefinitionLocalizationLanguageIds(
		long cpDefinitionId) {

		return _cpDefinitionLocalService.getCPDefinitionLocalizationLanguageIds(
			cpDefinitionId);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.product.model.CPDefinitionLocalization>
			getCPDefinitionLocalizations(long CPDefinitionId) {

		return _cpDefinitionLocalService.getCPDefinitionLocalizations(
			CPDefinitionId);
	}

	@Override
	public java.util.Map<java.util.Locale, String>
		getCPDefinitionMetaDescriptionMap(long cpDefinitionId) {

		return _cpDefinitionLocalService.getCPDefinitionMetaDescriptionMap(
			cpDefinitionId);
	}

	@Override
	public java.util.Map<java.util.Locale, String>
		getCPDefinitionMetaKeywordsMap(long cpDefinitionId) {

		return _cpDefinitionLocalService.getCPDefinitionMetaKeywordsMap(
			cpDefinitionId);
	}

	@Override
	public java.util.Map<java.util.Locale, String> getCPDefinitionMetaTitleMap(
		long cpDefinitionId) {

		return _cpDefinitionLocalService.getCPDefinitionMetaTitleMap(
			cpDefinitionId);
	}

	@Override
	public java.util.Map<java.util.Locale, String> getCPDefinitionNameMap(
		long cpDefinitionId) {

		return _cpDefinitionLocalService.getCPDefinitionNameMap(cpDefinitionId);
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
	@Override
	public java.util.List<CPDefinition> getCPDefinitions(int start, int end) {
		return _cpDefinitionLocalService.getCPDefinitions(start, end);
	}

	@Override
	public java.util.List<CPDefinition> getCPDefinitions(
		long groupId, boolean subscriptionEnabled) {

		return _cpDefinitionLocalService.getCPDefinitions(
			groupId, subscriptionEnabled);
	}

	@Override
	public java.util.List<CPDefinition> getCPDefinitions(
		long groupId, int status, int start, int end) {

		return _cpDefinitionLocalService.getCPDefinitions(
			groupId, status, start, end);
	}

	@Override
	public java.util.List<CPDefinition> getCPDefinitions(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinition>
			orderByComparator) {

		return _cpDefinitionLocalService.getCPDefinitions(
			groupId, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<CPDefinition> getCPDefinitions(
		long companyId, long accountEntryId, long[] accountGroupIds,
		long[] commerceChannelGroupIds, boolean published, int[] statuses,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinition>
			orderByComparator) {

		return _cpDefinitionLocalService.getCPDefinitions(
			companyId, accountEntryId, accountGroupIds, commerceChannelGroupIds,
			published, statuses, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definitions matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definitions
	 * @param companyId the primary key of the company
	 * @return the matching cp definitions, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPDefinition> getCPDefinitionsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _cpDefinitionLocalService.getCPDefinitionsByUuidAndCompanyId(
			uuid, companyId);
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
	@Override
	public java.util.List<CPDefinition> getCPDefinitionsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinition>
			orderByComparator) {

		return _cpDefinitionLocalService.getCPDefinitionsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp definitions.
	 *
	 * @return the number of cp definitions
	 */
	@Override
	public int getCPDefinitionsCount() {
		return _cpDefinitionLocalService.getCPDefinitionsCount();
	}

	@Override
	public int getCPDefinitionsCount(
		long groupId, boolean subscriptionEnabled) {

		return _cpDefinitionLocalService.getCPDefinitionsCount(
			groupId, subscriptionEnabled);
	}

	@Override
	public int getCPDefinitionsCount(long groupId, int status) {
		return _cpDefinitionLocalService.getCPDefinitionsCount(groupId, status);
	}

	@Override
	public java.util.Map<java.util.Locale, String>
		getCPDefinitionShortDescriptionMap(long cpDefinitionId) {

		return _cpDefinitionLocalService.getCPDefinitionShortDescriptionMap(
			cpDefinitionId);
	}

	@Override
	public CPDefinition getCProductCPDefinition(long cProductId, int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.getCProductCPDefinition(
			cProductId, version);
	}

	@Override
	public java.util.List<CPDefinition> getCProductCPDefinitions(
		long cProductId, int status, int start, int end) {

		return _cpDefinitionLocalService.getCProductCPDefinitions(
			cProductId, status, start, end);
	}

	@Override
	public java.util.List<CPDefinition> getCProductCPDefinitions(
		long cProductId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinition>
			orderByComparator) {

		return _cpDefinitionLocalService.getCProductCPDefinitions(
			cProductId, status, start, end, orderByComparator);
	}

	@Override
	public com.liferay.commerce.product.model.CPAttachmentFileEntry
			getDefaultImageCPAttachmentFileEntry(long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.getDefaultImageCPAttachmentFileEntry(
			cpDefinitionId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _cpDefinitionLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.search.facet.Facet>
		getFacets(
			String filterFields, String filterValues,
			com.liferay.portal.kernel.search.SearchContext searchContext) {

		return _cpDefinitionLocalService.getFacets(
			filterFields, filterValues, searchContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpDefinitionLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public String getLayoutPageTemplateEntryUuid(
		long groupId, long cpDefinitionId) {

		return _cpDefinitionLocalService.getLayoutPageTemplateEntryUuid(
			groupId, cpDefinitionId);
	}

	@Override
	public String getLayoutUuid(long groupId, long cpDefinitionId) {
		return _cpDefinitionLocalService.getLayoutUuid(groupId, cpDefinitionId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpDefinitionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.Map<java.util.Locale, String> getUrlTitleMap(
		long cpDefinitionId) {

		return _cpDefinitionLocalService.getUrlTitleMap(cpDefinitionId);
	}

	@Override
	public String getUrlTitleMapAsXML(long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.getUrlTitleMapAsXML(cpDefinitionId);
	}

	@Override
	public boolean hasChildCPDefinitions(long cpDefinitionId) {
		return _cpDefinitionLocalService.hasChildCPDefinitions(cpDefinitionId);
	}

	@Override
	public boolean isPublishedCPDefinition(CPDefinition cpDefinition) {
		return _cpDefinitionLocalService.isPublishedCPDefinition(cpDefinition);
	}

	@Override
	public boolean isPublishedCPDefinition(long cpDefinitionId) {
		return _cpDefinitionLocalService.isPublishedCPDefinition(
			cpDefinitionId);
	}

	@Override
	public boolean isVersionable(CPDefinition cpDefinition) {
		return _cpDefinitionLocalService.isVersionable(cpDefinition);
	}

	@Override
	public boolean isVersionable(long cpDefinitionId) {
		return _cpDefinitionLocalService.isVersionable(cpDefinitionId);
	}

	@Override
	public boolean isVersionable(
		long cpDefinitionId,
		jakarta.servlet.http.HttpServletRequest httpServletRequest) {

		return _cpDefinitionLocalService.isVersionable(
			cpDefinitionId, httpServletRequest);
	}

	@Override
	public void maintainVersionThreshold(long companyId, long cProductId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionLocalService.maintainVersionThreshold(
			companyId, cProductId);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPDefinition>
			searchCPDefinitions(
				long companyId, long[] groupIds, String keywords, int status,
				boolean ignoreCommerceAccountGroup, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.searchCPDefinitions(
			companyId, groupIds, keywords, status, ignoreCommerceAccountGroup,
			start, end, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPDefinition>
			searchCPDefinitions(
				long companyId, long[] groupIds, String keywords,
				String filterFields, String filterValues, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.searchCPDefinitions(
			companyId, groupIds, keywords, filterFields, filterValues, start,
			end, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPDefinition>
			searchCPDefinitionsByChannelGroupId(
				long companyId, long[] groupIds, long commerceChannelGroupId,
				String keywords, int status, boolean ignoreCommerceAccountGroup,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.searchCPDefinitionsByChannelGroupId(
			companyId, groupIds, commerceChannelGroupId, keywords, status,
			ignoreCommerceAccountGroup, start, end, sort);
	}

	@Override
	public void updateAsset(
			long userId, CPDefinition cpDefinition, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionLocalService.updateAsset(
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
	@Override
	public CPDefinition updateCPDefinition(CPDefinition cpDefinition) {
		return _cpDefinitionLocalService.updateCPDefinition(cpDefinition);
	}

	@Override
	public CPDefinition updateCPDefinition(
			long cpDefinitionId, long cpTaxCategoryId,
			boolean accountGroupFilterEnabled, boolean channelFilterEnabled,
			String ddmStructureKey, double depth,
			java.util.Map<java.util.Locale, String> descriptionMap,
			int displayDateDay, int displayDateHour, int displayDateMinute,
			int displayDateMonth, int displayDateYear, int expirationDateDay,
			int expirationDateHour, int expirationDateMinute,
			int expirationDateMonth, int expirationDateYear,
			boolean freeShipping, double height, boolean ignoreSKUCombinations,
			java.util.Map<java.util.Locale, String> metaDescriptionMap,
			java.util.Map<java.util.Locale, String> metaKeywordsMap,
			java.util.Map<java.util.Locale, String> metaTitleMap,
			java.util.Map<java.util.Locale, String> nameMap,
			boolean neverExpire, boolean published, boolean shipSeparately,
			boolean shippable, double shippingExtraPrice,
			java.util.Map<java.util.Locale, String> shortDescriptionMap,
			boolean taxExempt, boolean telcoOrElectronics,
			java.util.Map<java.util.Locale, String> urlTitleMap, double weight,
			double width,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.updateCPDefinition(
			cpDefinitionId, cpTaxCategoryId, accountGroupFilterEnabled,
			channelFilterEnabled, ddmStructureKey, depth, descriptionMap,
			displayDateDay, displayDateHour, displayDateMinute,
			displayDateMonth, displayDateYear, expirationDateDay,
			expirationDateHour, expirationDateMinute, expirationDateMonth,
			expirationDateYear, freeShipping, height, ignoreSKUCombinations,
			metaDescriptionMap, metaKeywordsMap, metaTitleMap, nameMap,
			neverExpire, published, shipSeparately, shippable,
			shippingExtraPrice, shortDescriptionMap, taxExempt,
			telcoOrElectronics, urlTitleMap, weight, width, serviceContext);
	}

	@Override
	public CPDefinition updateCPDefinitionAccountGroupFilter(
			long cpDefinitionId, boolean enable)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.updateCPDefinitionAccountGroupFilter(
			cpDefinitionId, enable);
	}

	@Override
	public CPDefinition updateCPDefinitionCategorization(
			long cpDefinitionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.updateCPDefinitionCategorization(
			cpDefinitionId, serviceContext);
	}

	@Override
	public CPDefinition updateCPDefinitionChannelFilter(
			long cpDefinitionId, boolean enable)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.updateCPDefinitionChannelFilter(
			cpDefinitionId, enable);
	}

	@Override
	public CPDefinition updateCPDefinitionIgnoreSKUCombinations(
			long cpDefinitionId, boolean ignoreSKUCombinations,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.
			updateCPDefinitionIgnoreSKUCombinations(
				cpDefinitionId, ignoreSKUCombinations, serviceContext);
	}

	@Override
	public com.liferay.commerce.product.model.CPDefinitionLocalization
			updateCPDefinitionLocalization(
				CPDefinition cpDefinition, String languageId,
				String description, String metaDescription, String metaKeywords,
				String metaTitle, String name, String shortDescription)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.updateCPDefinitionLocalization(
			cpDefinition, languageId, description, metaDescription,
			metaKeywords, metaTitle, name, shortDescription);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.product.model.CPDefinitionLocalization>
				updateCPDefinitionLocalizations(
					CPDefinition cpDefinition,
					java.util.Map<String, String> descriptionMap,
					java.util.Map<String, String> metaDescriptionMap,
					java.util.Map<String, String> metaKeywordsMap,
					java.util.Map<String, String> metaTitleMap,
					java.util.Map<String, String> nameMap,
					java.util.Map<String, String> shortDescriptionMap)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.updateCPDefinitionLocalizations(
			cpDefinition, descriptionMap, metaDescriptionMap, metaKeywordsMap,
			metaTitleMap, nameMap, shortDescriptionMap);
	}

	@Override
	public void updateCPDefinitionsByCPTaxCategoryId(long cpTaxCategoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionLocalService.updateCPDefinitionsByCPTaxCategoryId(
			cpTaxCategoryId);
	}

	@Override
	public CPDefinition updateExternalReferenceCode(
			String externalReferenceCode, long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.updateExternalReferenceCode(
			externalReferenceCode, cpDefinitionId);
	}

	@Override
	public CPDefinition updateShippingInfo(
			long cpDefinitionId, boolean shippable, boolean freeShipping,
			boolean shipSeparately, double shippingExtraPrice, double width,
			double height, double depth, double weight,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.updateShippingInfo(
			cpDefinitionId, shippable, freeShipping, shipSeparately,
			shippingExtraPrice, width, height, depth, weight, serviceContext);
	}

	@Override
	public CPDefinition updateStatus(
			long userId, long cpDefinitionId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.updateStatus(
			userId, cpDefinitionId, status, serviceContext, workflowContext);
	}

	@Override
	public CPDefinition updateSubscriptionInfo(
			long cpDefinitionId, boolean subscriptionEnabled,
			int subscriptionLength, String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.updateSubscriptionInfo(
			cpDefinitionId, subscriptionEnabled, subscriptionLength,
			subscriptionType, subscriptionTypeSettingsUnicodeProperties,
			maxSubscriptionCycles, deliverySubscriptionEnabled,
			deliverySubscriptionLength, deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles);
	}

	@Override
	public CPDefinition updateTaxCategoryInfo(
			long cpDefinitionId, long cpTaxCategoryId, boolean taxExempt,
			boolean telcoOrElectronics)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLocalService.updateTaxCategoryInfo(
			cpDefinitionId, cpTaxCategoryId, taxExempt, telcoOrElectronics);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpDefinitionLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CPDefinition> getCTPersistence() {
		return _cpDefinitionLocalService.getCTPersistence();
	}

	@Override
	public Class<CPDefinition> getModelClass() {
		return _cpDefinitionLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CPDefinition>, R, E>
				updateUnsafeFunction)
		throws E {

		return _cpDefinitionLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CPDefinitionLocalService getWrappedService() {
		return _cpDefinitionLocalService;
	}

	@Override
	public void setWrappedService(
		CPDefinitionLocalService cpDefinitionLocalService) {

		_cpDefinitionLocalService = cpDefinitionLocalService;
	}

	private CPDefinitionLocalService _cpDefinitionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1444118985