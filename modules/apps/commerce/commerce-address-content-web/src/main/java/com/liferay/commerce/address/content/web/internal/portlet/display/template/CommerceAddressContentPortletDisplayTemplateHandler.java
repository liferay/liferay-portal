/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.address.content.web.internal.portlet.display.template;

import com.liferay.commerce.address.content.web.internal.display.context.CommerceAddressDisplayContext;
import com.liferay.commerce.address.content.web.internal.portlet.CommerceAddressContentPortlet;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.service.CommerceAddressLocalService;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portlet.display.template.BasePortletDisplayTemplateHandler;
import com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ADDRESS_CONTENT,
	service = TemplateHandler.class
)
public class CommerceAddressContentPortletDisplayTemplateHandler
	extends BasePortletDisplayTemplateHandler {

	@Override
	public String getClassName() {
		return CommerceAddressContentPortlet.class.getName();
	}

	@Override
	public String getName(Locale locale) {
		StringBundler sb = new StringBundler(3);

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		sb.append(
			_portal.getPortletTitle(
				CommercePortletKeys.COMMERCE_ADDRESS_CONTENT, resourceBundle));

		sb.append(StringPool.SPACE);
		sb.append(_language.get(locale, "template"));

		return sb.toString();
	}

	@Override
	public String getResourceName() {
		return CommercePortletKeys.COMMERCE_ADDRESS_CONTENT;
	}

	@Override
	public Map<String, TemplateVariableGroup> getTemplateVariableGroups(
			long classPK, String language, Locale locale)
		throws Exception {

		Map<String, TemplateVariableGroup> templateVariableGroups =
			super.getTemplateVariableGroups(classPK, language, locale);

		TemplateVariableGroup templateVariableGroup =
			templateVariableGroups.get("fields");

		templateVariableGroup.empty();

		templateVariableGroup.addCollectionVariable(
			"commerce-addresses", List.class,
			PortletDisplayTemplateConstants.ENTRIES, "commerce-address",
			CommerceAddress.class, "curCommerceAddress", "CommerceAddressId");
		templateVariableGroup.addVariable(
			"commerce-address-display-context",
			CommerceAddressDisplayContext.class,
			"commerceAddressDisplayContext");

		TemplateVariableGroup commerceAddressServicesTemplateVariableGroup =
			new TemplateVariableGroup(
				"commerce-address-services", getRestrictedVariables(language));

		commerceAddressServicesTemplateVariableGroup.setAutocompleteEnabled(
			false);

		commerceAddressServicesTemplateVariableGroup.addServiceLocatorVariables(
			CommerceAddressLocalService.class, CommerceAddressService.class);

		templateVariableGroups.put(
			commerceAddressServicesTemplateVariableGroup.getLabel(),
			commerceAddressServicesTemplateVariableGroup);

		return templateVariableGroups;
	}

	@Override
	protected String getTemplatesConfigPath() {
		return "com/liferay/commerce/address/content/web/internal/portlet" +
			"/display/template/dependencies/portlet-display-templates.xml";
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}