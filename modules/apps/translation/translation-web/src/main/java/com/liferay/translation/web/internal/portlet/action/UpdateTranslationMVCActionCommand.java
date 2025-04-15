/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.portlet.action;

import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.translation.constants.TranslationPortletKeys;
import com.liferay.translation.service.TranslationEntryService;
import com.liferay.translation.url.provider.TranslationURLProvider;
import com.liferay.translation.web.internal.helper.TranslationRequestHelper;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia Garcia
 */
@Component(
	property = {
		"jakarta.portlet.name=" + TranslationPortletKeys.TRANSLATION,
		"mvc.command.name=/translation/update_translation"
	},
	service = MVCActionCommand.class
)
public class UpdateTranslationMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		try {
			long segmentsExperienceId = ParamUtil.getLong(
				actionRequest, "segmentsExperienceId");

			TranslationRequestHelper translationRequestHelper =
				new TranslationRequestHelper(
					_infoItemServiceRegistry, actionRequest,
					_segmentsExperienceLocalService);

			String className = translationRequestHelper.getClassName(
				segmentsExperienceId);
			long classPK = translationRequestHelper.getClassPK(
				segmentsExperienceId);

			InfoItemReference infoItemReference = new InfoItemReference(
				className, classPK);

			InfoItemObjectProvider<Object> infoItemObjectProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class,
					infoItemReference.getClassName(),
					ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

			if (infoItemObjectProvider == null) {
				throw new NoSuchModelException(
					"No info item object provider found for " +
						infoItemReference.getClassName());
			}

			long modifiedDateTime = ParamUtil.getLong(
				actionRequest, "modifiedDateTime");
			int workflowAction = ParamUtil.getInteger(
				actionRequest, "workflowAction",
				WorkflowConstants.ACTION_PUBLISH);

			InfoItemIdentifier infoItemIdentifier =
				infoItemReference.getInfoItemIdentifier();

			infoItemIdentifier.setVersion(InfoItemIdentifier.VERSION_LATEST);

			Object infoItem = infoItemObjectProvider.getInfoItem(
				infoItemIdentifier);

			InfoItemFieldValues sourceInfoItemFieldValues =
				_getInfoItemFieldValues(className, infoItem);

			if ((modifiedDateTime > 0) &&
				(workflowAction == WorkflowConstants.ACTION_PUBLISH)) {

				Object infoItemFieldValue = _getInfoItemFieldValue(
					"modifiedDate", sourceInfoItemFieldValues);

				if (Validator.isNotNull(infoItemFieldValue)) {
					int value = DateUtil.compareTo(
						(Date)infoItemFieldValue, new Date(modifiedDateTime));

					if (value > 0) {
						SessionErrors.add(actionRequest, "duplicateChanges");

						sendRedirect(
							actionRequest, actionResponse,
							_getRedirect(actionRequest, className, classPK));

						return;
					}
				}
			}

			long groupId = ParamUtil.getLong(actionRequest, "groupId");

			InfoItemFieldValues infoItemFieldValues =
				InfoItemFieldValues.builder(
				).infoItemReference(
					infoItemReference
				).infoFieldValues(
					_getInfoFieldValues(
						actionRequest, sourceInfoItemFieldValues, className,
						infoItem)
				).build();

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				actionRequest);

			_translationEntryService.addOrUpdateTranslationEntry(
				groupId, _getSourceLanguageId(actionRequest),
				_getTargetLanguageId(actionRequest), infoItemReference,
				infoItemFieldValues, serviceContext);

			String portletResource = ParamUtil.getString(
				actionRequest, "portletResource");

			if (Validator.isNotNull(portletResource)) {
				hideDefaultErrorMessage(actionRequest);

				MultiSessionMessages.add(
					actionRequest, portletResource + "requestProcessed");
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			SessionErrors.add(actionRequest, exception.getClass(), exception);

			actionResponse.setRenderParameter(
				"mvcRenderCommandName", "/translation/translate");
		}
	}

	private Map<String, String[]> _getInfoFieldParameterValues(
		PortletRequest portletRequest) {

		Map<String, String[]> values = new HashMap<>();

		Map<String, String[]> parameterMap = portletRequest.getParameterMap();

		for (String parameterName : parameterMap.keySet()) {
			if (parameterName.startsWith(_PARAMETER_NAME_INFO_FIELD)) {
				values.put(
					parameterName.substring(
						_PARAMETER_NAME_INFO_FIELD.length(),
						parameterName.length() - 2),
					portletRequest.getParameterValues(parameterName));
			}
		}

		return values;
	}

	private <T> List<InfoField<?>> _getInfoFields(String className, T object) {
		InfoItemFormProvider<T> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, className);

		InfoForm infoForm = infoItemFormProvider.getInfoForm(object);

		return infoForm.getAllInfoFields();
	}

	private <T> List<InfoFieldValue<Object>> _getInfoFieldValues(
		ActionRequest actionRequest, InfoItemFieldValues infoItemFieldValues,
		String className, T object) {

		List<InfoFieldValue<Object>> infoFieldValues = new ArrayList<>();

		Map<String, String[]> infoFieldParameterValues =
			_getInfoFieldParameterValues(actionRequest);

		for (InfoField<?> infoField : _getInfoFields(className, object)) {
			String[] infoFieldParameterValue = infoFieldParameterValues.get(
				infoField.getUniqueId());

			if (ArrayUtil.isNotEmpty(infoFieldParameterValue)) {
				Locale sourceLocale = _getSourceLocale(actionRequest);

				List<InfoFieldValue<Object>> sourceInfoFieldValues =
					new ArrayList<>(
						infoItemFieldValues.getInfoFieldValues(
							infoField.getUniqueId()));

				for (int i = 0; i < infoFieldParameterValue.length; i++) {
					InfoFieldValue<Object> sourceInfoFieldValue =
						sourceInfoFieldValues.get(i);

					infoFieldValues.add(
						new InfoFieldValue<>(
							infoField,
							InfoLocalizedValue.builder(
							).value(
								_getTargetLocale(actionRequest),
								infoFieldParameterValue[i]
							).value(
								sourceLocale,
								sourceInfoFieldValue.getValue(sourceLocale)
							).build()));
				}
			}
		}

		return infoFieldValues;
	}

	private Object _getInfoItemFieldValue(
		String infoFieldName, InfoItemFieldValues infoItemFieldValues) {

		InfoFieldValue<Object> infoFieldValue =
			infoItemFieldValues.getInfoFieldValue(infoFieldName);

		if (infoFieldValue == null) {
			return null;
		}

		return infoFieldValue.getValue();
	}

	private <T> InfoItemFieldValues _getInfoItemFieldValues(
		String className, T object) {

		InfoItemFieldValuesProvider<Object> infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class, className);

		return infoItemFieldValuesProvider.getInfoItemFieldValues(object);
	}

	private String _getRedirect(
			ActionRequest actionRequest, String className, long classPK)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletURLBuilder.AfterParameterStep afterParameterStep =
			PortletURLBuilder.create(
				_translationURLProvider.getTranslateURL(
					themeDisplay.getScopeGroupId(),
					_portal.getClassNameId(className), classPK,
					RequestBackedPortletURLFactoryUtil.create(actionRequest))
			).setRedirect(
				ParamUtil.getString(actionRequest, "redirect")
			).setPortletResource(
				() -> {
					PortletDisplay portletDisplay =
						themeDisplay.getPortletDisplay();

					return portletDisplay.getId();
				}
			).setParameter(
				"backURLTitle",
				ParamUtil.getString(actionRequest, "backURLTitle")
			).setParameter(
				"sourceLanguageId", _getSourceLanguageId(actionRequest)
			).setParameter(
				"targetLanguageId", _getTargetLanguageId(actionRequest)
			);

		Map<String, String[]> infoFieldParameterValues =
			_getInfoFieldParameterValues(actionRequest);

		if (infoFieldParameterValues.isEmpty()) {
			return afterParameterStep.buildString();
		}

		for (Map.Entry<String, String[]> entry :
				infoFieldParameterValues.entrySet()) {

			String[] values = entry.getValue();

			if (ArrayUtil.isEmpty(values)) {
				continue;
			}

			afterParameterStep.setParameter(entry.getKey(), values[0]);
		}

		return afterParameterStep.buildString();
	}

	private String _getSourceLanguageId(ActionRequest actionRequest) {
		return ParamUtil.getString(actionRequest, "sourceLanguageId");
	}

	private Locale _getSourceLocale(ActionRequest actionRequest) {
		return LocaleUtil.fromLanguageId(_getSourceLanguageId(actionRequest));
	}

	private String _getTargetLanguageId(ActionRequest actionRequest) {
		return ParamUtil.getString(actionRequest, "targetLanguageId");
	}

	private Locale _getTargetLocale(ActionRequest actionRequest) {
		return LocaleUtil.fromLanguageId(_getTargetLanguageId(actionRequest));
	}

	private static final String _PARAMETER_NAME_INFO_FIELD = "infoField--";

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateTranslationMVCActionCommand.class);

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private TranslationEntryService _translationEntryService;

	@Reference
	private TranslationURLProvider _translationURLProvider;

}