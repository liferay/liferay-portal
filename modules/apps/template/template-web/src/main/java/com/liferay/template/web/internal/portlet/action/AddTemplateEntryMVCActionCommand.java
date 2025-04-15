/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.exception.TemplateNameException;
import com.liferay.dynamic.data.mapping.exception.TemplateScriptException;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateHandlerRegistryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.template.constants.TemplatePortletKeys;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.service.TemplateEntryLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + TemplatePortletKeys.TEMPLATE,
		"mvc.command.name=/template/add_template_entry"
	},
	service = MVCActionCommand.class
)
public class AddTemplateEntryMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String infoItemClassName = ParamUtil.getString(
			actionRequest, "infoItemClassName");
		String infoItemFormVariationKey = ParamUtil.getString(
			actionRequest, "infoItemFormVariationKey");

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			new String[] {
				LocaleUtil.toLanguageId(themeDisplay.getSiteDefaultLocale())
			},
			new String[] {ParamUtil.getString(actionRequest, "name")});

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMTemplate.class.getName(), actionRequest);

		try {
			DDMTemplate ddmTemplate = _ddmTemplateLocalService.addTemplate(
				null, themeDisplay.getUserId(),
				serviceContext.getScopeGroupId(),
				_portal.getClassNameId(TemplateEntry.class), 0,
				_portal.getClassNameId(TemplateEntry.class), nameMap,
				Collections.emptyMap(),
				DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, StringPool.BLANK,
				TemplateConstants.LANG_TYPE_FTL, _getScript(), serviceContext);

			serviceContext = ServiceContextFactory.getInstance(
				TemplateEntry.class.getName(), actionRequest);

			TemplateEntry templateEntry =
				_templateEntryLocalService.addTemplateEntry(
					null, themeDisplay.getUserId(),
					serviceContext.getScopeGroupId(),
					ddmTemplate.getTemplateId(), infoItemClassName,
					infoItemFormVariationKey, serviceContext);

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"redirectURL",
					PortletURLBuilder.createRenderURL(
						_portal.getLiferayPortletResponse(actionResponse)
					).setMVCRenderCommandName(
						"/template/edit_ddm_template"
					).setRedirect(
						ParamUtil.getString(actionRequest, "redirect")
					).setTabs1(
						"information-templates"
					).setParameter(
						"ddmTemplateId", templateEntry.getDDMTemplateId()
					).setParameter(
						"templateEntryId", templateEntry.getTemplateEntryId()
					).buildString()));
		}
		catch (PortalException portalException) {
			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"error",
					_getErrorJSONObject(portalException, themeDisplay)));
		}
	}

	private JSONObject _getErrorJSONObject(
		PortalException portalException, ThemeDisplay themeDisplay) {

		if (portalException instanceof TemplateNameException) {
			return JSONUtil.put(
				"name",
				_language.get(
					themeDisplay.getLocale(), "please-enter-a-valid-name"));
		}
		else if (portalException instanceof TemplateScriptException) {
			return JSONUtil.put(
				"other",
				_language.get(
					themeDisplay.getLocale(), "please-enter-a-valid-script"));
		}

		if (_log.isDebugEnabled()) {
			_log.debug(portalException);
		}

		return JSONUtil.put(
			"other",
			_language.get(
				themeDisplay.getLocale(), "an-unexpected-error-occurred"));
	}

	private String _getScript() {
		TemplateHandler templateHandler =
			TemplateHandlerRegistryUtil.getTemplateHandler(
				_portal.getClassNameId(InfoItemFormProvider.class));

		if (templateHandler != null) {
			return templateHandler.getTemplatesHelpContent(
				TemplateConstants.LANG_TYPE_FTL);
		}

		return "<#-- Empty script -->";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddTemplateEntryMVCActionCommand.class);

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

	@Reference
	private TemplateEntryLocalService _templateEntryLocalService;

}