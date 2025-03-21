/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.web.internal.portlet;

import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.layout.set.prototype.configuration.LayoutSetPrototypeConfiguration;
import com.liferay.layout.set.prototype.constants.LayoutSetPrototypePortletKeys;
import com.liferay.layout.set.prototype.helper.LayoutSetPrototypeHelper;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.NoSuchLayoutSetPrototypeException;
import com.liferay.portal.kernel.exception.RequiredLayoutSetPrototypeException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.LayoutSetPrototypeService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	configurationPid = "com.liferay.layout.set.prototype.configuration.LayoutSetPrototypeConfiguration",
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-layout-set-prototype",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.icon=/icons/layout_set_prototypes.png",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Site Templates",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + LayoutSetPrototypePortletKeys.LAYOUT_SET_PROTOTYPE,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class LayoutSetPrototypePortlet extends MVCPortlet {

	public void changeDisplayStyle(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		hideDefaultSuccessMessage(actionRequest);

		String displayStyle = ParamUtil.getString(
			actionRequest, "displayStyle");

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(actionRequest);

		portalPreferences.setValue(
			LayoutSetPrototypePortletKeys.LAYOUT_SET_PROTOTYPE, "display-style",
			displayStyle);
	}

	public void deleteLayoutSetPrototypes(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] layoutSetPrototypeIds = null;

		long layoutSetPrototypeId = ParamUtil.getLong(
			actionRequest, "layoutSetPrototypeId");

		if (layoutSetPrototypeId > 0) {
			layoutSetPrototypeIds = new long[] {layoutSetPrototypeId};
		}
		else {
			layoutSetPrototypeIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");
		}

		for (long curLayoutSetPrototypeId : layoutSetPrototypeIds) {
			layoutSetPrototypeService.deleteLayoutSetPrototype(
				curLayoutSetPrototypeId);
		}
	}

	public void resetMergeFailCount(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long layoutSetPrototypeId = ParamUtil.getLong(
			actionRequest, "layoutSetPrototypeId");

		layoutSetPrototypeHelper.setMergeFailCount(
			layoutSetPrototypeService.getLayoutSetPrototype(
				layoutSetPrototypeId),
			0);
	}

	public void updateLayoutSetPrototype(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long layoutSetPrototypeId = ParamUtil.getLong(
			actionRequest, "layoutSetPrototypeId");

		Map<Locale, String> nameMap = localization.getLocalizationMap(
			actionRequest, "name");
		Map<Locale, String> descriptionMap = localization.getLocalizationMap(
			actionRequest, "description");
		boolean active = ParamUtil.getBoolean(actionRequest, "active");
		boolean layoutsUpdateable = ParamUtil.getBoolean(
			actionRequest, "layoutsUpdateable");
		boolean readyForPropagation = ParamUtil.getBoolean(
			actionRequest, "readyForPropagation");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		LayoutSetPrototype layoutSetPrototype = null;

		if (layoutSetPrototypeId <= 0) {

			// Add layout prototoype

			layoutSetPrototype =
				layoutSetPrototypeService.addLayoutSetPrototype(
					nameMap, descriptionMap, active, layoutsUpdateable,
					readyForPropagation, serviceContext);
		}
		else {

			// Update layout prototoype

			layoutSetPrototype =
				layoutSetPrototypeService.getLayoutSetPrototype(
					layoutSetPrototypeId);

			UnicodeProperties oldSettingsUnicodeProperties =
				layoutSetPrototype.getSettingsProperties();

			boolean oldReadyForPropagation = GetterUtil.getBoolean(
				oldSettingsUnicodeProperties.getProperty(
					"readyForPropagation"));

			_addSessionMessages(
				actionRequest, oldReadyForPropagation, readyForPropagation);

			layoutSetPrototype =
				layoutSetPrototypeService.updateLayoutSetPrototype(
					layoutSetPrototypeId, nameMap, descriptionMap, active,
					layoutsUpdateable, readyForPropagation, serviceContext);
		}

		// Custom JSPs

		String customJspServletContextName = ParamUtil.getString(
			actionRequest, "customJspServletContextName");

		UnicodeProperties settingsUnicodeProperties =
			layoutSetPrototype.getSettingsProperties();

		settingsUnicodeProperties.setProperty(
			"customJspServletContextName", customJspServletContextName);

		layoutSetPrototypeService.updateLayoutSetPrototype(
			layoutSetPrototype.getLayoutSetPrototypeId(),
			settingsUnicodeProperties.toString());
	}

	public void updateLayoutSetPrototypeAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long layoutSetPrototypeId = ParamUtil.getLong(
			actionRequest, "layoutSetPrototypeId");

		LayoutSetPrototype layoutSetPrototype =
			layoutSetPrototypeService.fetchLayoutSetPrototype(
				layoutSetPrototypeId);

		if (layoutSetPrototype == null) {
			return;
		}

		boolean active = ParamUtil.getBoolean(
			actionRequest, "active", layoutSetPrototype.isActive());

		UnicodeProperties settingsUnicodeProperties =
			layoutSetPrototype.getSettingsProperties();

		boolean layoutsUpdateable = GetterUtil.getBoolean(
			settingsUnicodeProperties.getProperty("layoutsUpdateable"));

		boolean oldReadyForPropagation = GetterUtil.getBoolean(
			settingsUnicodeProperties.getProperty("readyForPropagation"));

		boolean readyForPropagation = ParamUtil.getBoolean(
			actionRequest, "readyForPropagation", oldReadyForPropagation);

		_addSessionMessages(
			actionRequest, oldReadyForPropagation, readyForPropagation);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		layoutSetPrototypeService.updateLayoutSetPrototype(
			layoutSetPrototypeId, layoutSetPrototype.getNameMap(),
			layoutSetPrototype.getDescriptionMap(), active, layoutsUpdateable,
			readyForPropagation, serviceContext);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_layoutSetPrototypeConfiguration = ConfigurableUtil.createConfigurable(
			LayoutSetPrototypeConfiguration.class, properties);
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PanelCategoryHelper panelCategoryHelper = new PanelCategoryHelper(
			panelAppRegistry);

		renderRequest.setAttribute(
			ApplicationListWebKeys.PANEL_CATEGORY_HELPER, panelCategoryHelper);

		if (SessionErrors.contains(
				renderRequest, PrincipalException.getNestedClasses())) {

			include("/error.jsp", renderRequest, renderResponse);
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof NoSuchLayoutSetPrototypeException ||
			throwable instanceof PrincipalException ||
			throwable instanceof RequiredLayoutSetPrototypeException) {

			return true;
		}

		return false;
	}

	protected boolean isTriggerPropagation() {
		try {
			return _layoutSetPrototypeConfiguration.triggerPropagation();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	@Reference
	protected LayoutSetPrototypeHelper layoutSetPrototypeHelper;

	@Reference
	protected LayoutSetPrototypeService layoutSetPrototypeService;

	@Reference
	protected Localization localization;

	@Reference
	protected PanelAppRegistry panelAppRegistry;

	private void _addSessionMessages(
		ActionRequest actionRequest, boolean oldReadyForPropagation,
		boolean readyForPropagation) {

		if (oldReadyForPropagation && !readyForPropagation) {
			SessionMessages.add(actionRequest, "disablePropagation");
		}

		if (!oldReadyForPropagation && readyForPropagation) {
			if (isTriggerPropagation()) {
				SessionMessages.add(actionRequest, "triggerPropagation");
			}
			else {
				SessionMessages.add(actionRequest, "enablePropagation");
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSetPrototypePortlet.class);

	private volatile LayoutSetPrototypeConfiguration
		_layoutSetPrototypeConfiguration;

}