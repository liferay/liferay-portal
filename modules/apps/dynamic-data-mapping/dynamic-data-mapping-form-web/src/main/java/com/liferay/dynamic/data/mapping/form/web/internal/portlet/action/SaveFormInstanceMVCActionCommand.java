/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.helper.SaveFormInstanceMVCCommandHelper;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.LiferayFileItemException;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Basto
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
		"mvc.command.name=/dynamic_data_mapping_form/save_form_instance"
	},
	service = MVCActionCommand.class
)
public class SaveFormInstanceMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	protected void doService(
			ActionRequest actionRequest, ActionResponse actionResponse,
			LiferayPortletURL portletURL)
		throws Exception {

		DDMFormInstance ddmFormInstance =
			saveFormInstanceMVCCommandHelper.saveFormInstance(
				actionRequest, actionResponse, true);

		portletURL.setParameter(
			"formInstanceId",
			String.valueOf(ddmFormInstance.getFormInstanceId()));
	}

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		UploadException uploadException =
			(UploadException)actionRequest.getAttribute(
				WebKeys.UPLOAD_EXCEPTION);

		if ((uploadException != null) &&
			uploadException.isExceededLiferayFileItemSizeLimit()) {

			throw new LiferayFileItemException(uploadException.getCause());
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		LiferayPortletURL portletURL = PortletURLFactoryUtil.create(
			actionRequest, themeDisplay.getPpid(), PortletRequest.RENDER_PHASE);

		String mvcRenderCommandName = ParamUtil.getString(
			actionRequest, "mvcRenderCommandName");

		portletURL.setParameter("mvcRenderCommandName", mvcRenderCommandName);

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		try {
			doService(actionRequest, actionResponse, portletURL);

			portletURL.setParameter("redirect", redirect);

			actionRequest.setAttribute(WebKeys.REDIRECT, portletURL.toString());
		}
		catch (DDMFormValidationException.MustSetValidCharactersForFieldName
					ddmFormValidationException) {

			SessionErrors.add(
				actionRequest, ddmFormValidationException.getClass(),
				ddmFormValidationException);
		}
		catch (DDMFormValidationException.MustSetValidFormRuleExpression
					ddmFormValidationException) {

			SessionErrors.add(
				actionRequest, ddmFormValidationException.getClass(),
				ddmFormValidationException);
		}
		catch (DDMFormValidationException.MustSetValidValidationExpression
					ddmFormValidationException) {

			SessionErrors.add(
				actionRequest, ddmFormValidationException.getClass(),
				ddmFormValidationException);
		}
	}

	@Reference
	protected SaveFormInstanceMVCCommandHelper saveFormInstanceMVCCommandHelper;

}