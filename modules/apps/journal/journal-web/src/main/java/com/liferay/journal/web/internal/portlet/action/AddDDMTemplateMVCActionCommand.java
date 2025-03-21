/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.exception.TemplateCreationDisabledException;
import com.liferay.dynamic.data.mapping.exception.TemplateNameException;
import com.liferay.dynamic.data.mapping.exception.TemplateScriptException;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateService;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.File;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/add_ddm_template"
	},
	service = MVCActionCommand.class
)
public class AddDDMTemplateMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(actionRequest);

		long groupId = ParamUtil.getLong(uploadPortletRequest, "groupId");
		long classPK = ParamUtil.getLong(uploadPortletRequest, "classPK");
		String templateKey = ParamUtil.getString(
			uploadPortletRequest, "templateKey");
		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			uploadPortletRequest, "name");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			uploadPortletRequest, "description");
		String script = ActionUtil.getScript(uploadPortletRequest);
		boolean cacheable = ParamUtil.getBoolean(
			uploadPortletRequest, "cacheable");

		String smallImageSource = ParamUtil.getString(
			uploadPortletRequest, "smallImageSource", "none");

		boolean smallImage = !Objects.equals(smallImageSource, "none");

		String smallImageURL = StringPool.BLANK;
		File smallImageFile = null;

		if (Objects.equals(smallImageSource, "url")) {
			smallImageURL = ParamUtil.getString(
				uploadPortletRequest, "smallImageURL");
		}
		else if (Objects.equals(smallImageSource, "file")) {
			smallImageFile = uploadPortletRequest.getFile("smallImageFile");
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMTemplate.class.getName(), uploadPortletRequest);

		DDMTemplate ddmTemplate = null;

		try {
			ddmTemplate = _ddmTemplateService.addTemplate(
				null, groupId, _portal.getClassNameId(DDMStructure.class),
				classPK, _portal.getClassNameId(JournalArticle.class),
				templateKey, nameMap, descriptionMap,
				DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, StringPool.BLANK,
				TemplateConstants.LANG_TYPE_FTL, script, cacheable, smallImage,
				smallImageURL, smallImageFile, serviceContext);
		}
		catch (PortalException portalException) {
			SessionErrors.add(actionRequest, portalException.getClass());

			String message = null;

			if (portalException instanceof TemplateCreationDisabledException) {
				message =
					"the-template-could-not-be-created-because-template-" +
						"creation-is-disabled";
			}

			if (portalException instanceof TemplateNameException) {
				message = "please-enter-a-valid-name";
			}

			if (portalException instanceof TemplateScriptException) {
				message = "please-enter-a-valid-script";
			}

			if (message == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(portalException);
				}

				message = "an-unexpected-error-occurred";
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"error",
					_language.get(themeDisplay.getRequest(), message)));

			return;
		}

		boolean saveAndContinue = ParamUtil.getBoolean(
			uploadPortletRequest, "saveAndContinue");

		if (saveAndContinue) {
			actionRequest.setAttribute(
				WebKeys.REDIRECT,
				PortletURLBuilder.createRenderURL(
					_portal.getLiferayPortletResponse(actionResponse)
				).setMVCPath(
					"/edit_ddm_template.jsp"
				).setRedirect(
					ParamUtil.getString(uploadPortletRequest, "redirect")
				).setParameter(
					"ddmTemplateId", ddmTemplate.getTemplateId()
				).buildString());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddDDMTemplateMVCActionCommand.class);

	@Reference
	private DDMTemplateService _ddmTemplateService;

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

}