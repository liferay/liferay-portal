/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.portlet.action;

import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.translation.constants.TranslationPortletKeys;
import com.liferay.translation.service.TranslationEntryLocalService;
import com.liferay.translation.web.internal.display.context.ImportTranslationDisplayContext;
import com.liferay.translation.web.internal.helper.TranslationRequestHelper;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"jakarta.portlet.name=" + TranslationPortletKeys.TRANSLATION,
		"mvc.command.name=/translation/import_translation"
	},
	service = MVCRenderCommand.class
)
public class ImportTranslationMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			TranslationRequestHelper translationRequestHelper =
				new TranslationRequestHelper(
					_infoItemServiceRegistry, renderRequest,
					_segmentsExperienceLocalService);

			renderRequest.setAttribute(
				ImportTranslationDisplayContext.class.getName(),
				new ImportTranslationDisplayContext(
					translationRequestHelper.getClassNameId(),
					translationRequestHelper.getModelClassPK(),
					themeDisplay.getCompanyId(),
					ParamUtil.getLong(renderRequest, "groupId"),
					_portal.getHttpServletRequest(renderRequest),
					_portal.getLiferayPortletResponse(renderResponse),
					_getTitle(
						translationRequestHelper.getModelClassName(),
						translationRequestHelper.getModelClassPKs(),
						themeDisplay.getLocale()),
					_translationEntryLocalService,
					_workflowDefinitionLinkLocalService));

			return "/import_translation.jsp";
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}
	}

	private Object _getModel(String className, long classPK)
		throws PortalException {

		InfoItemObjectProvider<Object> infoItemObjectProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, className,
				ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		return infoItemObjectProvider.getInfoItem(
			new ClassPKInfoItemIdentifier(classPK));
	}

	private String _getTitle(String className, long[] classPKs, Locale locale)
		throws PortalException {

		if ((classPKs.length != 1) || (classPKs[0] == 0)) {
			return StringPool.BLANK;
		}

		InfoItemFieldValuesProvider<Object> infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class, className);

		InfoFieldValue<Object> infoFieldValue = _getTitleInfoFieldValue(
			infoItemFieldValuesProvider, _getModel(className, classPKs[0]));

		return (String)infoFieldValue.getValue(locale);
	}

	private InfoFieldValue<Object> _getTitleInfoFieldValue(
		InfoItemFieldValuesProvider<Object> infoItemFieldValuesProvider,
		Object object) {

		InfoFieldValue<Object> infoFieldValue =
			infoItemFieldValuesProvider.getInfoFieldValue(object, "title");

		if (infoFieldValue != null) {
			return infoFieldValue;
		}

		return infoItemFieldValuesProvider.getInfoFieldValue(object, "name");
	}

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private TranslationEntryLocalService _translationEntryLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}