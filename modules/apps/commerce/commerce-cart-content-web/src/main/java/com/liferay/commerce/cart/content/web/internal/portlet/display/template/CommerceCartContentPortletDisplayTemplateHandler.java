/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.cart.content.web.internal.portlet.display.template;

import com.liferay.commerce.cart.content.web.internal.display.context.CommerceCartContentDisplayContext;
import com.liferay.commerce.cart.content.web.internal.portlet.CommerceCartContentPortlet;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
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
	property = "jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_CART_CONTENT,
	service = TemplateHandler.class
)
public class CommerceCartContentPortletDisplayTemplateHandler
	extends BasePortletDisplayTemplateHandler {

	@Override
	public String getClassName() {
		return CommerceCartContentPortlet.class.getName();
	}

	@Override
	public String getName(Locale locale) {
		StringBundler sb = new StringBundler(3);

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		sb.append(
			_portal.getPortletTitle(
				CommercePortletKeys.COMMERCE_CART_CONTENT, resourceBundle));

		sb.append(StringPool.SPACE);
		sb.append(_language.get(locale, "template"));

		return sb.toString();
	}

	@Override
	public String getResourceName() {
		return CommercePortletKeys.COMMERCE_CART_CONTENT;
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

		templateVariableGroup.addVariable(
			"commerce-cart-content-display-context",
			CommerceCartContentDisplayContext.class,
			"commerceCartContentDisplayContext");
		templateVariableGroup.addCollectionVariable(
			"commerce-order-items", List.class,
			PortletDisplayTemplateConstants.ENTRIES, "commerce-order-item",
			CommerceOrderItem.class, "curCommerceOrderItem",
			"CommerceOrderItemId");

		TemplateVariableGroup commerceOrderItemsServicesTemplateVariableGroup =
			new TemplateVariableGroup(
				"commerce-order-item-services",
				getRestrictedVariables(language));

		commerceOrderItemsServicesTemplateVariableGroup.setAutocompleteEnabled(
			false);

		commerceOrderItemsServicesTemplateVariableGroup.
			addServiceLocatorVariables(CommerceOrderItemLocalService.class);

		templateVariableGroups.put(
			commerceOrderItemsServicesTemplateVariableGroup.getLabel(),
			commerceOrderItemsServicesTemplateVariableGroup);

		return templateVariableGroups;
	}

	@Override
	protected String getTemplatesConfigPath() {
		return "com/liferay/commerce/cart/content/web/internal/portlet" +
			"/display/template/dependencies/cart/portlet-display-templates.xml";
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}