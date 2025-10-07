/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model.impl;

import com.liferay.commerce.media.CommerceMediaResolverUtil;
import com.liferay.commerce.product.constants.CPConfigurationEntrySettingConstants;
import com.liferay.commerce.product.exception.CPDefinitionMetaDescriptionException;
import com.liferay.commerce.product.exception.CPDefinitionMetaKeywordsException;
import com.liferay.commerce.product.exception.CPDefinitionMetaTitleException;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationEntrySetting;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionLocalization;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalServiceUtil;
import com.liferay.commerce.product.service.CPConfigurationEntryLocalServiceUtil;
import com.liferay.commerce.product.service.CPConfigurationEntrySettingLocalServiceUtil;
import com.liferay.commerce.product.service.CPConfigurationListLocalServiceUtil;
import com.liferay.commerce.product.service.CPDefinitionLocalServiceUtil;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalServiceUtil;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueLocalServiceUtil;
import com.liferay.commerce.product.service.CPInstanceLocalServiceUtil;
import com.liferay.commerce.product.service.CPTaxCategoryLocalServiceUtil;
import com.liferay.commerce.product.service.CProductLocalServiceUtil;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Marco Leo
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 * @author Luca Pellizzon
 */
public class CPDefinitionImpl extends CPDefinitionBaseImpl {

	public static CPDefinitionMetaDescriptionException validateMetaDescription(
		String value) {

		return validateMetaDescription(value, true);
	}

	public static CPDefinitionMetaDescriptionException validateMetaDescription(
		String value, boolean checkMaxLength) {

		if (Validator.isNull(value)) {
			return null;
		}

		int maxLength = ModelHintsUtil.getMaxLength(
			CPDefinitionLocalization.class.getName(), "metaDescription");

		if (checkMaxLength && (value.length() > maxLength)) {
			return new CPDefinitionMetaDescriptionException();
		}

		return null;
	}

	public static CPDefinitionMetaKeywordsException validateMetaKeyword(
		String value) {

		return validateMetaKeyword(value, true);
	}

	public static CPDefinitionMetaKeywordsException validateMetaKeyword(
		String value, boolean checkMaxLength) {

		if (Validator.isNull(value)) {
			return null;
		}

		int maxLength = ModelHintsUtil.getMaxLength(
			CPDefinitionLocalization.class.getName(), "metaKeyword");

		if (checkMaxLength && (value.length() > maxLength)) {
			return new CPDefinitionMetaKeywordsException();
		}

		return null;
	}

	public static CPDefinitionMetaTitleException validateMetaTitle(
		String value) {

		return validateMetaTitle(value, true);
	}

	public static CPDefinitionMetaTitleException validateMetaTitle(
		String value, boolean checkMaxLength) {

		if (Validator.isNull(value)) {
			return null;
		}

		int maxLength = ModelHintsUtil.getMaxLength(
			CPDefinitionLocalization.class.getName(), "metaTitle");

		if (checkMaxLength && (value.length() > maxLength)) {
			return new CPDefinitionMetaTitleException();
		}

		return null;
	}

	@Override
	public Object clone() {
		CPDefinitionImpl cpDefinitionImpl = (CPDefinitionImpl)super.clone();

		cpDefinitionImpl.setDescriptionMap(getDescriptionMap());
		cpDefinitionImpl.setNameMap(getNameMap());
		cpDefinitionImpl.setShortDescriptionMap(getShortDescriptionMap());
		cpDefinitionImpl.setUrlTitleMap(getUrlTitleMap());

		return cpDefinitionImpl;
	}

	@Override
	public boolean equals(Object object) {
		return super.equals(object);
	}

	@Override
	public CPConfigurationEntry fetchCPConfigurationEntry(
		long cpConfigurationListId) {

		return _fetchCPConfigurationEntry(
			cpConfigurationListId,
			CPConfigurationEntryLocalServiceUtil.getCPConfigurationEntries(
				ClassNameLocalServiceUtil.getClassNameId(CPDefinition.class),
				getCPDefinitionId()));
	}

	@Override
	public CPConfigurationEntry fetchMasterCPConfigurationEntry()
		throws PortalException {

		if (_cpConfigurationEntry != null) {
			return _cpConfigurationEntry;
		}

		CPConfigurationList cpConfigurationList =
			CPConfigurationListLocalServiceUtil.getMasterCPConfigurationList(
				getGroupId());

		_cpConfigurationEntry =
			CPConfigurationEntryLocalServiceUtil.fetchCPConfigurationEntry(
				PortalUtil.getClassNameId(CPDefinition.class.getName()),
				getCPDefinitionId(),
				cpConfigurationList.getCPConfigurationListId());

		return _cpConfigurationEntry;
	}

	@Override
	public String[] getAvailableLanguageIds() {
		Set<String> availableLanguageIds = new TreeSet<>();

		availableLanguageIds.addAll(
			CPDefinitionLocalServiceUtil.getCPDefinitionLocalizationLanguageIds(
				getCPDefinitionId()));

		return availableLanguageIds.toArray(new String[0]);
	}

	@Override
	public CommerceCatalog getCommerceCatalog() {
		return CommerceCatalogLocalServiceUtil.fetchCommerceCatalogByGroupId(
			getGroupId());
	}

	@Override
	public List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			int type, int status)
		throws PortalException {

		return CPAttachmentFileEntryLocalServiceUtil.getCPAttachmentFileEntries(
			ClassNameLocalServiceUtil.getClassNameId(CPDefinition.class),
			getCPDefinitionId(), type, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);
	}

	@Override
	public List<CPDefinitionOptionRel> getCPDefinitionOptionRels() {
		return CPDefinitionOptionRelLocalServiceUtil.getCPDefinitionOptionRels(
			getCPDefinitionId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	public List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues() {

		return CPDefinitionSpecificationOptionValueLocalServiceUtil.
			getCPDefinitionSpecificationOptionValues(
				getCPDefinitionId(), true, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);
	}

	@Override
	public List<CPInstance> getCPInstances() {
		return CPInstanceLocalServiceUtil.getCPDefinitionInstances(
			getCPDefinitionId(), WorkflowConstants.STATUS_ANY,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	@Override
	public CProduct getCProduct() throws PortalException {
		return CProductLocalServiceUtil.getCProduct(getCProductId());
	}

	@Override
	public CPTaxCategory getCPTaxCategory() throws PortalException {
		if (getCPTaxCategoryId() <= 0) {
			return null;
		}

		return CPTaxCategoryLocalServiceUtil.getCPTaxCategory(
			getCPTaxCategoryId());
	}

	@Override
	public String getDefaultImageThumbnailSrc(long commerceAccountId)
		throws Exception {

		CPAttachmentFileEntry cpAttachmentFileEntry =
			CPDefinitionLocalServiceUtil.getDefaultImageCPAttachmentFileEntry(
				getCPDefinitionId());

		if (cpAttachmentFileEntry == null) {
			return CommerceMediaResolverUtil.getDefaultURL(getGroupId());
		}

		return CommerceMediaResolverUtil.getThumbnailURL(
			commerceAccountId,
			cpAttachmentFileEntry.getCPAttachmentFileEntryId(), false);
	}

	@Override
	public String getDefaultLanguageId() {
		CommerceCatalog commerceCatalog = getCommerceCatalog();

		if (commerceCatalog == null) {
			return LanguageUtil.getLanguageId(LocaleUtil.getSiteDefault());
		}

		return commerceCatalog.getCatalogDefaultLanguageId();
	}

	@Override
	public UnicodeProperties
		getDeliverySubscriptionTypeSettingsUnicodeProperties() {

		if (_deliverySubscriptionTypeSettingsUnicodeProperties == null) {
			_deliverySubscriptionTypeSettingsUnicodeProperties =
				UnicodePropertiesBuilder.create(
					true
				).fastLoad(
					getDeliverySubscriptionTypeSettings()
				).build();
		}

		return _deliverySubscriptionTypeSettingsUnicodeProperties;
	}

	@Override
	public Map<Locale, String> getDescriptionMap() {
		if (_descriptionMap != null) {
			return _descriptionMap;
		}

		_descriptionMap =
			CPDefinitionLocalServiceUtil.getCPDefinitionDescriptionMap(
				getCPDefinitionId());

		return _descriptionMap;
	}

	@Override
	public CPConfigurationList getMasterCPConfigurationList()
		throws PortalException {

		return CPConfigurationListLocalServiceUtil.getMasterCPConfigurationList(
			getGroupId());
	}

	@Override
	public Map<Locale, String> getMetaDescriptionMap() {
		if (_metaDescriptionMap != null) {
			return _metaDescriptionMap;
		}

		_metaDescriptionMap =
			CPDefinitionLocalServiceUtil.getCPDefinitionMetaDescriptionMap(
				getCPDefinitionId());

		return _metaDescriptionMap;
	}

	@Override
	public Map<Locale, String> getMetaKeywordsMap() {
		if (_metaKeywordsMap != null) {
			return _metaKeywordsMap;
		}

		_metaKeywordsMap =
			CPDefinitionLocalServiceUtil.getCPDefinitionMetaKeywordsMap(
				getCPDefinitionId());

		return _metaKeywordsMap;
	}

	@Override
	public Map<Locale, String> getMetaTitleMap() {
		if (_metaTitleMap != null) {
			return _metaTitleMap;
		}

		_metaTitleMap =
			CPDefinitionLocalServiceUtil.getCPDefinitionMetaTitleMap(
				getCPDefinitionId());

		return _metaTitleMap;
	}

	@Override
	public String getNameCurrentValue() {
		Locale locale = LocaleThreadLocal.getThemeDisplayLocale();

		return getName(LocaleUtil.toLanguageId(locale), true);
	}

	@Override
	public Map<Locale, String> getNameMap() {
		if (_nameMap != null) {
			return _nameMap;
		}

		_nameMap = CPDefinitionLocalServiceUtil.getCPDefinitionNameMap(
			getCPDefinitionId());

		return _nameMap;
	}

	@Override
	public Map<Locale, String> getShortDescriptionMap() {
		if (_shortDescriptionMap != null) {
			return _shortDescriptionMap;
		}

		_shortDescriptionMap =
			CPDefinitionLocalServiceUtil.getCPDefinitionShortDescriptionMap(
				getCPDefinitionId());

		return _shortDescriptionMap;
	}

	@Override
	public UnicodeProperties getSubscriptionTypeSettingsUnicodeProperties() {
		if (_subscriptionTypeSettingsUnicodeProperties == null) {
			_subscriptionTypeSettingsUnicodeProperties =
				UnicodePropertiesBuilder.create(
					true
				).fastLoad(
					getSubscriptionTypeSettings()
				).build();
		}

		return _subscriptionTypeSettingsUnicodeProperties;
	}

	@Override
	public String getURL(String languageId) {
		long classNameId = PortalUtil.getClassNameId(CProduct.class);

		FriendlyURLEntry friendlyURLEntry = null;

		try {
			friendlyURLEntry =
				FriendlyURLEntryLocalServiceUtil.getMainFriendlyURLEntry(
					classNameId, getCProductId());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return StringPool.BLANK;
		}

		return friendlyURLEntry.getUrlTitle(languageId);
	}

	@Override
	public Map<Locale, String> getUrlTitleMap() {
		if (_urlTitleMap != null) {
			return _urlTitleMap;
		}

		_urlTitleMap = CPDefinitionLocalServiceUtil.getUrlTitleMap(
			getCPDefinitionId());

		return _urlTitleMap;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public void setDeliverySubscriptionTypeSettings(
		String subscriptionTypeSettings) {

		super.setDeliverySubscriptionTypeSettings(subscriptionTypeSettings);

		_deliverySubscriptionTypeSettingsUnicodeProperties = null;
	}

	@Override
	public void setDeliverySubscriptionTypeSettingsUnicodeProperties(
		UnicodeProperties deliverySubscriptionTypeSettingsUnicodeProperties) {

		_deliverySubscriptionTypeSettingsUnicodeProperties =
			deliverySubscriptionTypeSettingsUnicodeProperties;

		if (_deliverySubscriptionTypeSettingsUnicodeProperties == null) {
			_deliverySubscriptionTypeSettingsUnicodeProperties =
				new UnicodeProperties();
		}

		super.setDeliverySubscriptionTypeSettings(
			_deliverySubscriptionTypeSettingsUnicodeProperties.toString());
	}

	@Override
	public void setDescriptionMap(Map<Locale, String> descriptionMap) {
		_descriptionMap = descriptionMap;
	}

	@Override
	public void setNameMap(Map<Locale, String> nameMap) {
		_nameMap = nameMap;
	}

	@Override
	public void setShortDescriptionMap(
		Map<Locale, String> shortDescriptionMap) {

		_shortDescriptionMap = shortDescriptionMap;
	}

	@Override
	public void setSubscriptionTypeSettings(String subscriptionTypeSettings) {
		super.setSubscriptionTypeSettings(subscriptionTypeSettings);

		_subscriptionTypeSettingsUnicodeProperties = null;
	}

	@Override
	public void setSubscriptionTypeSettingsUnicodeProperties(
		UnicodeProperties subscriptionTypeSettingsUnicodeProperties) {

		_subscriptionTypeSettingsUnicodeProperties =
			subscriptionTypeSettingsUnicodeProperties;

		if (_subscriptionTypeSettingsUnicodeProperties == null) {
			_subscriptionTypeSettingsUnicodeProperties =
				new UnicodeProperties();
		}

		super.setSubscriptionTypeSettings(
			_subscriptionTypeSettingsUnicodeProperties.toString());
	}

	@Override
	public void setUrlTitleMap(Map<Locale, String> urlTitleMap) {
		_urlTitleMap = urlTitleMap;
	}

	private CPConfigurationEntry _fetchCPConfigurationEntry(
		long cpConfigurationListId,
		List<CPConfigurationEntry> cpConfigurationEntries) {

		for (CPConfigurationEntry cpConfigurationEntry :
				cpConfigurationEntries) {

			if (cpConfigurationEntry.getCPConfigurationListId() ==
					cpConfigurationListId) {

				return cpConfigurationEntry;
			}

			CPConfigurationEntrySetting cpConfigurationEntrySetting =
				CPConfigurationEntrySettingLocalServiceUtil.
					fetchCPConfigurationEntrySetting(
						cpConfigurationEntry.getCPConfigurationEntryId(),
						CPConfigurationEntrySettingConstants.TYPE_INDEX_IDS);

			if ((cpConfigurationEntrySetting != null) &&
				StringUtil.contains(
					cpConfigurationEntrySetting.getValue(),
					String.valueOf(cpConfigurationListId))) {

				return cpConfigurationEntry;
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionImpl.class);

	private CPConfigurationEntry _cpConfigurationEntry;
	private UnicodeProperties
		_deliverySubscriptionTypeSettingsUnicodeProperties;
	private Map<Locale, String> _descriptionMap;
	private Map<Locale, String> _metaDescriptionMap;
	private Map<Locale, String> _metaKeywordsMap;
	private Map<Locale, String> _metaTitleMap;
	private Map<Locale, String> _nameMap;
	private Map<Locale, String> _shortDescriptionMap;
	private UnicodeProperties _subscriptionTypeSettingsUnicodeProperties;
	private Map<Locale, String> _urlTitleMap;

}