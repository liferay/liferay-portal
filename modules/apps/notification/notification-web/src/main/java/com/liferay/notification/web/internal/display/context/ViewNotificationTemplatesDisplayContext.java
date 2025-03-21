/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.web.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.notification.constants.NotificationActionKeys;
import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.web.internal.constants.NotificationWebKeys;
import com.liferay.notification.web.internal.display.context.helper.NotificationRequestHelper;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Gabriel Albuquerque
 */
public class ViewNotificationTemplatesDisplayContext {

	public ViewNotificationTemplatesDisplayContext(
		HttpServletRequest httpServletRequest,
		ModelResourcePermission<NotificationTemplate>
			notificationTemplateModelResourcePermission) {

		_notificationTemplateModelResourcePermission =
			notificationTemplateModelResourcePermission;

		_notificationRequestHelper = new NotificationRequestHelper(
			httpServletRequest);
	}

	public String getAPIURL() {
		return "/o/notification/v1.0/notification-templates";
	}

	public CreationMenu getCreationMenu() {
		CreationMenu creationMenu = new CreationMenu();

		if (!_hasAddNotificationTemplatePermission()) {
			return creationMenu;
		}

		_addDropdownItem(
			creationMenu, "email", NotificationConstants.TYPE_EMAIL);

		_addDropdownItem(
			creationMenu, "user-notification",
			NotificationConstants.TYPE_USER_NOTIFICATION);

		return creationMenu;
	}

	public Object getEditorConfig() {
		HttpServletRequest httpServletRequest =
			_notificationRequestHelper.getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		EditorConfiguration editorConfiguration =
			EditorConfigurationFactoryUtil.getEditorConfiguration(
				themeDisplay.getPpid(), "rich_text", "ckeditor_classic",
				HashMapBuilder.<String, Object>put(
					"liferay-ui:input-editor:allowBrowseDocuments", true
				).put(
					"liferay-ui:input-editor:name", "richTextLocalizedEditor"
				).build(),
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY),
				RequestBackedPortletURLFactoryUtil.create(httpServletRequest));

		Map<String, Object> data = editorConfiguration.getData();

		return data.get("editorConfig");
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		return Arrays.asList(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					_getPortletURL()
				).setMVCRenderCommandName(
					"/notification_templates/edit_notification_template"
				).setParameter(
					"notificationTemplateId", "{id}"
				).buildString(),
				"view", "view",
				LanguageUtil.get(
					_notificationRequestHelper.getRequest(), "view"),
				"get", null, null),
			new FDSActionDropdownItem(
				getAPIURL() + "/{id}/copy", "copy", "copy",
				LanguageUtil.get(
					_notificationRequestHelper.getRequest(), "duplicate"),
				"post", "copy", "async"),
			new FDSActionDropdownItem(
				getAPIURL() + "/{id}", "trash", "delete",
				LanguageUtil.get(
					_notificationRequestHelper.getRequest(), "delete"),
				"delete", "delete", "async"),
			new FDSActionDropdownItem(
				_getPermissionsURL(), null, "permissions",
				LanguageUtil.get(
					_notificationRequestHelper.getRequest(), "permissions"),
				"get", "permissions", "modal-permissions"));
	}

	public NotificationTemplate getNotificationTemplate() {
		HttpServletRequest httpServletRequest =
			_notificationRequestHelper.getRequest();

		return (NotificationTemplate)httpServletRequest.getAttribute(
			NotificationWebKeys.NOTIFICATION_TEMPLATES);
	}

	public String getNotificationTemplateType() {
		HttpServletRequest httpServletRequest =
			_notificationRequestHelper.getRequest();

		return GetterUtil.getString(
			httpServletRequest.getAttribute(
				NotificationWebKeys.NOTIFICATION_TEMPLATE_TYPE));
	}

	private void _addDropdownItem(
		CreationMenu creationMenu, String labelKey,
		String notificationTemplateType) {

		creationMenu.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					_getPortletURL(), "mvcRenderCommandName",
					"/notification_templates/edit_notification_template",
					"backURL", _notificationRequestHelper.getCurrentURL(),
					"notificationTemplateType", notificationTemplateType);
				dropdownItem.setLabel(
					LanguageUtil.get(
						_notificationRequestHelper.getRequest(), labelKey));
			});
	}

	private String _getPermissionsURL() throws Exception {
		PortletURL portletURL = PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_notificationRequestHelper.getRequest(),
				"com_liferay_portlet_configuration_web_portlet_" +
					"PortletConfigurationPortlet",
				ActionRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_permissions.jsp"
		).setRedirect(
			_notificationRequestHelper.getCurrentURL()
		).setParameter(
			"modelResource", NotificationTemplate.class.getName()
		).setParameter(
			"modelResourceDescription", "{name}"
		).setParameter(
			"resourcePrimKey", "{id}"
		).buildPortletURL();

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			throw new PortalException(windowStateException);
		}

		return portletURL.toString();
	}

	private PortletURL _getPortletURL() throws PortletException {
		return PortletURLUtil.clone(
			PortletURLUtil.getCurrent(
				_notificationRequestHelper.getLiferayPortletRequest(),
				_notificationRequestHelper.getLiferayPortletResponse()),
			_notificationRequestHelper.getLiferayPortletResponse());
	}

	private boolean _hasAddNotificationTemplatePermission() {
		PortletResourcePermission portletResourcePermission =
			_notificationTemplateModelResourcePermission.
				getPortletResourcePermission();

		return portletResourcePermission.contains(
			_notificationRequestHelper.getPermissionChecker(), null,
			NotificationActionKeys.ADD_NOTIFICATION_TEMPLATE);
	}

	private final NotificationRequestHelper _notificationRequestHelper;
	private final ModelResourcePermission<NotificationTemplate>
		_notificationTemplateModelResourcePermission;

}