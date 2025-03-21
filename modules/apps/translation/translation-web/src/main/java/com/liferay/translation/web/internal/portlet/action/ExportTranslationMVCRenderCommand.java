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
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.translation.constants.TranslationPortletKeys;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporterRegistry;
import com.liferay.translation.web.internal.display.context.ExportTranslationDisplayContext;
import com.liferay.translation.web.internal.helper.TranslationRequestHelper;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"jakarta.portlet.name=" + TranslationPortletKeys.TRANSLATION,
		"mvc.command.name=/translation/export_translation"
	},
	service = MVCRenderCommand.class
)
public class ExportTranslationMVCRenderCommand implements MVCRenderCommand {

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

			List<Object> models = _getModels(translationRequestHelper);

			renderRequest.setAttribute(
				ExportTranslationDisplayContext.class.getName(),
				new ExportTranslationDisplayContext(
					translationRequestHelper.getClassNameId(),
					translationRequestHelper.getModelClassPKs(),
					translationRequestHelper.getGroupId(),
					_portal.getHttpServletRequest(renderRequest),
					_infoItemServiceRegistry,
					_portal.getLiferayPortletRequest(renderRequest),
					_portal.getLiferayPortletResponse(renderResponse), models,
					_getTitle(
						translationRequestHelper.getModelClassName(),
						models.get(0), themeDisplay.getLocale(), models.size()),
					_translationInfoItemFieldValuesExporterRegistry));

			return "/export_translation.jsp";
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}
	}

	private List<Object> _getModels(
			TranslationRequestHelper translationRequestHelper)
		throws PortalException {

		InfoItemObjectProvider<Object> infoItemObjectProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class,
				translationRequestHelper.getModelClassName(),
				ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		long[] classPKs = translationRequestHelper.getModelClassPKs();

		List<Object> models = new ArrayList<>(classPKs.length);

		for (long classPK : classPKs) {
			models.add(
				infoItemObjectProvider.getInfoItem(
					new ClassPKInfoItemIdentifier(classPK)));
		}

		return models;
	}

	private String _getTitle(
		String className, Object model, Locale locale, int size) {

		if (size > 1) {
			return _language.get(locale, "export-for-translation");
		}

		InfoItemFieldValuesProvider<Object> infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class, className);

		InfoFieldValue<Object> infoFieldValue =
			infoItemFieldValuesProvider.getInfoFieldValue(model, "title");

		if (infoFieldValue == null) {
			return _language.get(locale, "export-translation");
		}

		return (String)infoFieldValue.getValue(locale);
	}

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private TranslationInfoItemFieldValuesExporterRegistry
		_translationInfoItemFieldValuesExporterRegistry;

}