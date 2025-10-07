/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.subscription.service.SubscriptionLocalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = "path=/cms/subscribe_content_item", service = StrutsAction.class
)
public class SubscribeContentItemStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				ParamUtil.getLong(httpServletRequest, "objectDefinitionId"));

		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			_objectEntryService.getModelResourcePermission(
				objectDefinition.getObjectDefinitionId());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long classPK = ParamUtil.getLong(httpServletRequest, "classPK");

		modelResourcePermission.check(
			themeDisplay.getPermissionChecker(), classPK, ActionKeys.UPDATE);

		String cmd = ParamUtil.getString(httpServletRequest, "cmd");

		ClassName className = _classNameLocalService.getClassName(
			ParamUtil.getLong(httpServletRequest, "classNameId"));

		if (cmd.equals(Constants.SUBSCRIBE)) {
			_subscriptionLocalService.addSubscription(
				themeDisplay.getUserId(), themeDisplay.getScopeGroupId(),
				className.getClassName(), classPK);
		}
		else {
			_subscriptionLocalService.deleteSubscription(
				themeDisplay.getUserId(), className.getClassName(), classPK);
		}

		ServletResponseUtil.write(
			httpServletResponse,
			JSONUtil.toString(_jsonFactory.createJSONObject()));

		return null;
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

}