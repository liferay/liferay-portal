/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the CPDefinition service. Represents a row in the &quot;CPDefinition&quot; database table, with each column mapped to a property of this class.
 *
 * @author Marco Leo
 * @see CPDefinitionModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.commerce.product.model.impl.CPDefinitionImpl"
)
@ProviderType
public interface CPDefinition extends CPDefinitionModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.commerce.product.model.impl.CPDefinitionImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<CPDefinition, Long> CP_DEFINITION_ID_ACCESSOR =
		new Accessor<CPDefinition, Long>() {

			@Override
			public Long get(CPDefinition cpDefinition) {
				return cpDefinition.getCPDefinitionId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<CPDefinition> getTypeClass() {
				return CPDefinition.class;
			}

		};

	public Object clone();

	@Override
	public boolean equals(Object object);

	public CPConfigurationEntry fetchCPConfigurationEntry(
		long cpConfigurationListId);

	public CPConfigurationEntry fetchMasterCPConfigurationEntry()
		throws com.liferay.portal.kernel.exception.PortalException;

	public CommerceCatalog getCommerceCatalog();

	public java.util.List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			int type, int status)
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<CPDefinitionOptionRel> getCPDefinitionOptionRels();

	public java.util.List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues();

	public java.util.List<CPInstance> getCPInstances();

	public CProduct getCProduct()
		throws com.liferay.portal.kernel.exception.PortalException;

	public CPTaxCategory getCPTaxCategory()
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getDefaultImageThumbnailSrc(long commerceAccountId)
		throws Exception;

	public com.liferay.portal.kernel.util.UnicodeProperties
		getDeliverySubscriptionTypeSettingsUnicodeProperties();

	public java.util.Map<java.util.Locale, String> getDescriptionMap();

	public CPConfigurationList getMasterCPConfigurationList()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.Map<java.util.Locale, String> getMetaDescriptionMap();

	public java.util.Map<java.util.Locale, String> getMetaKeywordsMap();

	public java.util.Map<java.util.Locale, String> getMetaTitleMap();

	public String getNameCurrentValue();

	public java.util.Map<java.util.Locale, String> getNameMap();

	public java.util.Map<java.util.Locale, String> getShortDescriptionMap();

	public com.liferay.portal.kernel.util.UnicodeProperties
		getSubscriptionTypeSettingsUnicodeProperties();

	public String getURL(String languageId);

	public java.util.Map<java.util.Locale, String> getUrlTitleMap();

	public int hashCode();

	public void setDeliverySubscriptionTypeSettingsUnicodeProperties(
		com.liferay.portal.kernel.util.UnicodeProperties
			deliverySubscriptionTypeSettingsUnicodeProperties);

	public void setDescriptionMap(
		java.util.Map<java.util.Locale, String> descriptionMap);

	public void setNameMap(java.util.Map<java.util.Locale, String> nameMap);

	public void setShortDescriptionMap(
		java.util.Map<java.util.Locale, String> shortDescriptionMap);

	public void setSubscriptionTypeSettingsUnicodeProperties(
		com.liferay.portal.kernel.util.UnicodeProperties
			subscriptionTypeSettingsUnicodeProperties);

	public void setUrlTitleMap(
		java.util.Map<java.util.Locale, String> urlTitleMap);

}