/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.portlet.action;

import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.translation.constants.TranslationActionKeys;
import com.liferay.translation.constants.TranslationConstants;
import com.liferay.translation.constants.TranslationPortletKeys;
import com.liferay.translation.info.field.TranslationInfoFieldChecker;
import com.liferay.translation.info.item.provider.InfoItemLanguagesProvider;
import com.liferay.translation.model.TranslationEntry;
import com.liferay.translation.service.TranslationEntryLocalService;
import com.liferay.translation.translator.Translator;
import com.liferay.translation.translator.TranslatorRegistry;
import com.liferay.translation.web.internal.display.context.TranslateDisplayContext;
import com.liferay.translation.web.internal.helper.TranslationRequestHelper;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ambrín Chaudhary
 */
@Component(
	property = {
		"jakarta.portlet.name=" + TranslationPortletKeys.TRANSLATION,
		"mvc.command.name=/translation/translate"
	},
	service = MVCRenderCommand.class
)
public class TranslateMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			long segmentsExperienceId = ParamUtil.getLong(
				renderRequest, "segmentsExperienceId");

			TranslationRequestHelper translationRequestHelper =
				new TranslationRequestHelper(
					_infoItemServiceRegistry, renderRequest,
					_segmentsExperienceLocalService);

			String className = translationRequestHelper.getClassName(
				segmentsExperienceId);

			long classPK = translationRequestHelper.getClassPK(
				segmentsExperienceId);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			_translator = _translatorRegistry.getCompanyTranslator(
				themeDisplay.getCompanyId());

			Object object = _getInfoItem(
				className, classPK,
				ParamUtil.getString(
					renderRequest, "version",
					InfoItemIdentifier.VERSION_LATEST));

			if (object == null) {
				return _getErrorJSP(
					renderRequest, renderResponse, segmentsExperienceId,
					translationRequestHelper);
			}

			InfoItemLanguagesProvider<Object> infoItemLanguagesProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemLanguagesProvider.class, className);

			if (infoItemLanguagesProvider == null) {
				return _getErrorJSP(
					renderRequest, renderResponse, segmentsExperienceId,
					translationRequestHelper);
			}

			List<String> availableSourceLanguageIds = Arrays.asList(
				infoItemLanguagesProvider.getAvailableLanguageIds(object));

			String sourceLanguageId = ParamUtil.getString(
				renderRequest, "sourceLanguageId",
				infoItemLanguagesProvider.getDefaultLanguageId(object));

			List<String> availableTargetLanguageIds =
				_getAvailableTargetLanguageIds(
					className, object, sourceLanguageId, themeDisplay);

			InfoItemFormProvider<Object> infoItemFormProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemFormProvider.class, className);

			if (infoItemFormProvider == null) {
				return _getErrorJSP(
					renderRequest, renderResponse, segmentsExperienceId,
					translationRequestHelper);
			}

			InfoForm infoForm = infoItemFormProvider.getInfoForm(object);

			InfoItemFieldValues sourceInfoItemFieldValues =
				_getSourceInfoItemFieldValues(className, object);

			if (sourceInfoItemFieldValues == null) {
				return _getErrorJSP(
					renderRequest, renderResponse, segmentsExperienceId,
					translationRequestHelper);
			}

			String targetLanguageId = ParamUtil.getString(
				renderRequest, "targetLanguageId",
				_getDefaultTargetLanguageId(availableTargetLanguageIds));

			renderRequest.setAttribute(
				TranslateDisplayContext.class.getName(),
				new TranslateDisplayContext(
					availableSourceLanguageIds, availableTargetLanguageIds,
					() -> _translator != null,
					translationRequestHelper.getModelClassName(),
					translationRequestHelper.getModelClassPK(), infoForm,
					_portal.getLiferayPortletRequest(renderRequest),
					_portal.getLiferayPortletResponse(renderResponse), object,
					segmentsExperienceId, sourceInfoItemFieldValues,
					sourceLanguageId,
					_getTargetInfoItemFieldValues(
						className, classPK, sourceInfoItemFieldValues,
						targetLanguageId),
					targetLanguageId, _translationInfoFieldChecker));

			return "/translate.jsp";
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	private <T> List<String> _getAvailableTargetLanguageIds(
			String className, T object, String sourceLanguageId,
			ThemeDisplay themeDisplay)
		throws PortalException {

		InfoItemPermissionProvider infoItemPermissionProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemPermissionProvider.class, className);

		if (infoItemPermissionProvider == null) {
			return Collections.emptyList();
		}

		boolean hasUpdatePermission = infoItemPermissionProvider.hasPermission(
			themeDisplay.getPermissionChecker(), object, ActionKeys.UPDATE);

		return TransformUtil.transform(
			_language.getAvailableLocales(themeDisplay.getSiteGroupId()),
			locale -> {
				String languageId = LocaleUtil.toLanguageId(locale);

				if (!Objects.equals(languageId, sourceLanguageId) &&
					(hasUpdatePermission ||
					 _hasTranslatePermission(languageId, themeDisplay))) {

					return languageId;
				}

				return null;
			});
	}

	private String _getDefaultTargetLanguageId(
		List<String> availableTargetLanguageIds) {

		if (availableTargetLanguageIds.isEmpty()) {
			return StringPool.BLANK;
		}

		return availableTargetLanguageIds.get(0);
	}

	private String _getErrorJSP(
			RenderRequest renderRequest, RenderResponse renderResponse,
			long segmentsExperienceId,
			TranslationRequestHelper translationRequestHelper)
		throws PortalException {

		renderRequest.setAttribute(
			TranslateDisplayContext.class.getName(),
			new TranslateDisplayContext(
				Collections.emptyList(), Collections.emptyList(),
				() -> _translator != null,
				translationRequestHelper.getModelClassName(),
				translationRequestHelper.getModelClassPK(), null,
				_portal.getLiferayPortletRequest(renderRequest),
				_portal.getLiferayPortletResponse(renderResponse), null,
				segmentsExperienceId, null, null, null, null,
				_translationInfoFieldChecker));

		return "/translate.jsp";
	}

	private Object _getInfoItem(
		String className, long classPK, String version) {

		try {
			InfoItemObjectProvider<Object> infoItemObjectProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class, className,
					ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

			if (infoItemObjectProvider == null) {
				return null;
			}

			InfoItemIdentifier infoItemIdentifier =
				new ClassPKInfoItemIdentifier(classPK);

			infoItemIdentifier.setVersion(version);

			return infoItemObjectProvider.getInfoItem(infoItemIdentifier);
		}
		catch (NoSuchInfoItemException noSuchInfoItemException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchInfoItemException);
			}

			return null;
		}
	}

	private <T> InfoItemFieldValues _getSourceInfoItemFieldValues(
		String className, T object) {

		InfoItemFieldValuesProvider<T> infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class, className);

		if (infoItemFieldValuesProvider == null) {
			return null;
		}

		return infoItemFieldValuesProvider.getInfoItemFieldValues(object);
	}

	private <T> InfoItemFieldValues _getTargetInfoItemFieldValues(
			String className, long classPK,
			InfoItemFieldValues infoItemFieldValues, String targetLanguageId)
		throws PortalException {

		TranslationEntry translationEntry =
			_translationEntryLocalService.fetchTranslationEntry(
				className, classPK, targetLanguageId);

		if ((translationEntry == null) || translationEntry.isApproved()) {
			return infoItemFieldValues;
		}

		return _translationEntryLocalService.getInfoItemFieldValues(
			translationEntry.getGroupId(), translationEntry.getClassName(),
			translationEntry.getClassPK(), translationEntry.getContent());
	}

	private boolean _hasTranslatePermission(
		String languageId, ThemeDisplay themeDisplay) {

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		String name = TranslationConstants.RESOURCE_NAME + "." + languageId;

		return permissionChecker.hasPermission(
			themeDisplay.getScopeGroup(), name, name,
			TranslationActionKeys.TRANSLATE);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TranslateMVCRenderCommand.class);

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private TranslationEntryLocalService _translationEntryLocalService;

	@Reference
	private TranslationInfoFieldChecker _translationInfoFieldChecker;

	private Translator _translator;

	@Reference
	private TranslatorRegistry _translatorRegistry;

}