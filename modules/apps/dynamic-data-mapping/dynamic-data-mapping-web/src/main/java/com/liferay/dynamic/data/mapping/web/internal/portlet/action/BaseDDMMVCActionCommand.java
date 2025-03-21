/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.portal.kernel.exception.PortletPreferencesException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.StrictPortletPreferencesImpl;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;

import java.util.Objects;

/**
 * @author Leonardo Barros
 */
public abstract class BaseDDMMVCActionCommand extends BaseMVCActionCommand {

	protected String getSaveAndContinueRedirect(
			ActionRequest actionRequest, DDMStructure structure,
			String redirect)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String availableFields = ParamUtil.getString(
			actionRequest, "availableFields");
		String eventName = ParamUtil.getString(actionRequest, "eventName");

		LiferayPortletURL portletURL = PortletURLFactoryUtil.create(
			actionRequest, themeDisplay.getPpid(), PortletRequest.RENDER_PHASE);

		portletURL.setParameter("mvcPath", "/edit_structure.jsp");
		portletURL.setParameter("redirect", redirect, false);
		portletURL.setParameter(
			"groupId", String.valueOf(structure.getGroupId()), false);
		portletURL.setParameter(
			"classNameId",
			String.valueOf(PortalUtil.getClassNameId(DDMStructure.class)),
			false);
		portletURL.setParameter(
			"classPK", String.valueOf(structure.getStructureId()), false);
		portletURL.setParameter("availableFields", availableFields, false);
		portletURL.setParameter("eventName", eventName, false);
		portletURL.setWindowState(actionRequest.getWindowState());

		return portletURL.toString();
	}

	protected String getSaveAndContinueRedirect(
			ActionRequest actionRequest, DDMTemplate template, String redirect)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String portletResourceNamespace = ParamUtil.getString(
			actionRequest, "portletResourceNamespace");
		long classNameId = ParamUtil.getLong(actionRequest, "classNameId");
		long classPK = ParamUtil.getLong(actionRequest, "classPK");
		String structureAvailableFields = ParamUtil.getString(
			actionRequest, "structureAvailableFields");

		LiferayPortletURL portletURL = PortletURLFactoryUtil.create(
			actionRequest, themeDisplay.getPpid(), PortletRequest.RENDER_PHASE);

		portletURL.setParameter("mvcPath", "/edit_template.jsp");
		portletURL.setParameter("redirect", redirect, false);
		portletURL.setParameter(
			"portletResourceNamespace", portletResourceNamespace, false);
		portletURL.setParameter(
			"templateId", String.valueOf(template.getTemplateId()), false);
		portletURL.setParameter(
			"groupId", String.valueOf(template.getGroupId()), false);
		portletURL.setParameter(
			"classNameId", String.valueOf(classNameId), false);
		portletURL.setParameter("classPK", String.valueOf(classPK), false);
		portletURL.setParameter("type", template.getType(), false);
		portletURL.setParameter("mode", template.getMode(), false);
		portletURL.setParameter(
			"structureAvailableFields", structureAvailableFields, false);
		portletURL.setWindowState(actionRequest.getWindowState());

		return portletURL.toString();
	}

	protected void setRedirectAttribute(ActionRequest actionRequest) {
		actionRequest.setAttribute(
			WebKeys.REDIRECT, _getRedirect(actionRequest));
	}

	protected void setRedirectAttribute(
			ActionRequest actionRequest, DDMStructure structure)
		throws Exception {

		String redirect = _getRedirect(actionRequest);

		boolean saveAndContinue = ParamUtil.getBoolean(
			actionRequest, "saveAndContinue");

		if (saveAndContinue) {
			redirect = getSaveAndContinueRedirect(
				actionRequest, structure, redirect);
		}

		actionRequest.setAttribute(WebKeys.REDIRECT, redirect);
	}

	protected void setRedirectAttribute(
			ActionRequest actionRequest, DDMTemplate template)
		throws Exception {

		String redirect = _getRedirect(actionRequest);

		boolean saveAndContinue = ParamUtil.getBoolean(
			actionRequest, "saveAndContinue");

		if (saveAndContinue) {
			redirect = getSaveAndContinueRedirect(
				actionRequest, template, redirect);
		}

		actionRequest.setAttribute(WebKeys.REDIRECT, redirect);
	}

	protected void updatePortletPreferences(
			ActionRequest actionRequest, DDMTemplate template)
		throws Exception {

		PortletPreferences portletPreferences = _getStrictPortletPreferences(
			actionRequest);

		if (portletPreferences == null) {
			return;
		}

		if (Objects.equals(
				template.getType(),
				DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY)) {

			portletPreferences.setValue(
				"displayDDMTemplateId",
				String.valueOf(template.getTemplateId()));
		}
		else if (Objects.equals(
					template.getMode(),
					DDMTemplateConstants.TEMPLATE_MODE_CREATE)) {

			portletPreferences.setValue(
				"formDDMTemplateId", String.valueOf(template.getTemplateId()));
		}

		portletPreferences.store();
	}

	private String _getRedirect(ActionRequest actionRequest) {
		String redirect = ParamUtil.getString(actionRequest, "redirect");

		String closeRedirect = ParamUtil.getString(
			actionRequest, "closeRedirect");

		if (Validator.isNull(closeRedirect)) {
			return redirect;
		}

		redirect = HttpComponentsUtil.setParameter(
			redirect, "closeRedirect", closeRedirect);

		SessionMessages.add(
			actionRequest,
			PortalUtil.getPortletId(actionRequest) +
				SessionMessages.KEY_SUFFIX_CLOSE_REDIRECT,
			closeRedirect);

		return redirect;
	}

	private PortletPreferences _getStrictPortletPreferences(
			ActionRequest actionRequest)
		throws Exception {

		String portletResource = ParamUtil.getString(
			actionRequest, "portletResource");

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return _getStrictPortletPreferences(
			themeDisplay.getLayout(), portletResource);
	}

	private PortletPreferences _getStrictPortletPreferences(
			Layout layout, String portletId)
		throws Exception {

		if (Validator.isNull(portletId)) {
			return null;
		}

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getStrictPortletSetup(
				layout, portletId);

		if (portletPreferences instanceof StrictPortletPreferencesImpl) {
			throw new PortletPreferencesException.MustBeStrict(portletId);
		}

		return portletPreferences;
	}

}