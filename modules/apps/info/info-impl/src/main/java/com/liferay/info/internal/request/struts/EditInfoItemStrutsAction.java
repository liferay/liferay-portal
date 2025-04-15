/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.internal.request.struts;

import com.liferay.captcha.util.CaptchaUtil;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.util.configuration.FragmentConfigurationField;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.info.exception.InfoFormInvalidGroupException;
import com.liferay.info.exception.InfoFormInvalidLayoutModeException;
import com.liferay.info.exception.InfoFormPrincipalException;
import com.liferay.info.exception.InfoFormValidationException;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.DateInfoFieldType;
import com.liferay.info.field.type.RelationshipInfoFieldType;
import com.liferay.info.internal.request.helper.InfoRequestFieldValuesProviderHelper;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.creator.InfoItemCreator;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.item.updater.InfoItemFieldValuesUpdater;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.type.WebURL;
import com.liferay.layout.constants.LayoutWebKeys;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureItemUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.captcha.CaptchaException;
import com.liferay.portal.kernel.exception.InfoFormException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.staging.StagingGroupHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(
	property = "path=/portal/edit_info_item", service = StrutsAction.class
)
public class EditInfoItemStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String formItemId = ParamUtil.getString(
			httpServletRequest, "formItemId");

		Map<String, InfoFieldValue<Object>> infoFieldValues = null;

		try {
			infoFieldValues =
				_infoRequestFieldValuesProviderHelper.getInfoFieldValues(
					httpServletRequest);
		}
		catch (InfoFormException infoFormException) {
			if (_log.isDebugEnabled()) {
				_log.debug(infoFormException);
			}

			SessionErrors.add(
				httpServletRequest, formItemId, infoFormException);

			httpServletResponse.sendRedirect(
				httpServletRequest.getHeader(HttpHeaders.REFERER));

			return null;
		}

		LayoutStructure layoutStructure = _getLayoutStructure(
			httpServletRequest);

		if (layoutStructure == null) {
			throw new InfoFormException();
		}

		String redirect = null;
		boolean success = false;

		try {
			if (!Objects.equals(
					Constants.VIEW,
					ParamUtil.getString(httpServletRequest, "p_l_mode"))) {

				throw new InfoFormInvalidLayoutModeException();
			}

			Layout layout = _layoutLocalService.fetchLayout(
				ParamUtil.getLong(httpServletRequest, "plid"));

			if ((layout == null) || layout.isDraftLayout()) {
				throw new InfoFormInvalidLayoutModeException();
			}

			long groupId = ParamUtil.getLong(httpServletRequest, "groupId");

			if ((groupId == 0) ||
				_stagingGroupHelper.isLocalStagingGroup(groupId) ||
				_stagingGroupHelper.isRemoteStagingGroup(groupId)) {

				throw new InfoFormInvalidGroupException();
			}

			FragmentEntryLink captchaFragmentEntryLink =
				_getCaptchaFragmentEntryLink(formItemId, layoutStructure);

			if (captchaFragmentEntryLink != null) {
				try {
					CaptchaUtil.check(httpServletRequest);
				}
				catch (CaptchaException captchaException) {
					throw new InfoFormValidationException.InvalidCaptcha(
						captchaException,
						captchaFragmentEntryLink.getFragmentEntryLinkId());
				}
			}

			_validateRequiredFields(
				httpServletRequest, infoFieldValues, layoutStructure);

			Object infoItem = null;

			String className = _portal.fetchClassName(
				ParamUtil.getLong(httpServletRequest, "classNameId"));

			InfoItemIdentifier infoItemIdentifier = _getInfoItemIdentifier(
				httpServletRequest);

			int status = ParamUtil.getInteger(
				httpServletRequest, "status",
				WorkflowConstants.STATUS_APPROVED);

			if (infoItemIdentifier != null) {
				InfoItemObjectProvider<Object> infoItemObjectProvider =
					_infoItemServiceRegistry.getFirstInfoItemService(
						InfoItemObjectProvider.class, className,
						infoItemIdentifier.getInfoItemServiceFilter());

				InfoItemFieldValuesUpdater<Object> infoItemFieldValuesUpdater =
					_infoItemServiceRegistry.getFirstInfoItemService(
						InfoItemFieldValuesUpdater.class, className);

				if (infoItemFieldValuesUpdater == null) {
					throw new InfoFormException();
				}

				infoItem =
					infoItemFieldValuesUpdater.updateFromInfoItemFieldValues(
						infoItemObjectProvider.getInfoItem(infoItemIdentifier),
						InfoItemFieldValues.builder(
						).infoFieldValues(
							new ArrayList<>(infoFieldValues.values())
						).infoItemReference(
							new InfoItemReference(className, 0)
						).build(),
						status);
			}
			else {
				InfoItemCreator<Object> infoItemCreator =
					_infoItemServiceRegistry.getFirstInfoItemService(
						InfoItemCreator.class, className);

				if (infoItemCreator == null) {
					throw new InfoFormException();
				}

				infoItem = infoItemCreator.createFromInfoItemFieldValues(
					groupId,
					InfoItemFieldValues.builder(
					).infoFieldValues(
						new ArrayList<>(infoFieldValues.values())
					).infoItemReference(
						new InfoItemReference(className, 0)
					).build(),
					status);
			}

			String displayPageURL = _getDisplayPageURL(
				className,
				ParamUtil.getString(httpServletRequest, "displayPage"),
				httpServletRequest, infoItem);

			redirect = ParamUtil.getString(httpServletRequest, "redirect");

			if (Validator.isNotNull(displayPageURL)) {
				redirect = displayPageURL;
			}
			else if (Validator.isNull(redirect)) {
				redirect = ParamUtil.getString(httpServletRequest, "backURL");

				LayoutStructureItem formLayoutStructureItem =
					layoutStructure.getLayoutStructureItem(formItemId);

				if (formLayoutStructureItem == null) {
					throw new InfoFormException();
				}

				FormStyledLayoutStructureItem formStyledLayoutStructureItem =
					(FormStyledLayoutStructureItem)formLayoutStructureItem;

				JSONObject successMessageJSONObject =
					formStyledLayoutStructureItem.getSuccessMessageJSONObject();

				if ((successMessageJSONObject == null) ||
					!Objects.equals(
						successMessageJSONObject.getString("type"), "none")) {

					SessionMessages.add(httpServletRequest, formItemId);
				}
			}

			success = true;
		}
		catch (InfoFormValidationException.InvalidCaptcha
					infoFormValidationException) {

			if (_log.isDebugEnabled()) {
				_log.debug(infoFormValidationException);
			}

			SessionErrors.add(
				httpServletRequest, InfoFormException.class,
				infoFormValidationException);
		}
		catch (InfoFormValidationException infoFormValidationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(infoFormValidationException);
			}

			boolean hasInfoFormValidationExceptionCustomValidationErrors =
				false;

			if (infoFormValidationException instanceof
					InfoFormValidationException.RuleValidation) {

				InfoFormValidationException.RuleValidation
					infoFormValidationExceptionRuleValidation =
						(InfoFormValidationException.RuleValidation)
							infoFormValidationException;

				for (InfoFormValidationException.CustomValidation
						infoFormValidationExceptionCustomValidation :
							infoFormValidationExceptionRuleValidation.
								getCustomValidations()) {

					if (Validator.isNotNull(
							infoFormValidationExceptionCustomValidation.
								getInfoFieldUniqueId())) {

						SessionErrors.add(
							httpServletRequest, InfoFormException.class,
							infoFormValidationExceptionCustomValidation);
					}
					else {
						SessionErrors.add(
							httpServletRequest, formItemId,
							infoFormValidationExceptionCustomValidation);
					}

					hasInfoFormValidationExceptionCustomValidationErrors = true;
				}
			}

			if (!hasInfoFormValidationExceptionCustomValidationErrors) {
				SessionErrors.add(
					httpServletRequest, formItemId,
					infoFormValidationException);
			}

			if (Validator.isNotNull(
					infoFormValidationException.getInfoFieldUniqueId())) {

				SessionErrors.add(
					httpServletRequest, InfoFormException.class,
					infoFormValidationException);
			}
		}
		catch (InfoFormException infoFormException) {
			if (_log.isDebugEnabled()) {
				_log.debug(infoFormException);
			}

			SessionErrors.add(
				httpServletRequest, formItemId, infoFormException);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			InfoFormException infoFormException = new InfoFormException();

			if (exception instanceof PrincipalException) {
				infoFormException = new InfoFormPrincipalException();
			}

			SessionErrors.add(
				httpServletRequest, formItemId, infoFormException);
		}

		if (!success && (infoFieldValues != null)) {
			Map<String, Object> infoFormParameterMap = new HashMap<>();

			for (InfoFieldValue<Object> infoFieldValue :
					infoFieldValues.values()) {

				InfoField<?> infoField = infoFieldValue.getInfoField();

				infoFormParameterMap.put(
					infoField.getName(), _getValue(infoFieldValue));

				if (infoField.getInfoFieldType() ==
						RelationshipInfoFieldType.INSTANCE) {

					UploadServletRequest uploadServletRequest =
						_portal.getUploadServletRequest(httpServletRequest);

					String labelParameterName = infoField.getName() + "-label";

					String label = ParamUtil.getString(
						uploadServletRequest, labelParameterName);

					infoFormParameterMap.put(labelParameterName, label);
				}
			}

			SessionMessages.add(
				httpServletRequest, "infoFormParameterMap" + formItemId,
				infoFormParameterMap);
		}

		if (Validator.isNull(redirect)) {
			redirect = httpServletRequest.getHeader(HttpHeaders.REFERER);
		}

		String notificationText = ParamUtil.getString(
			httpServletRequest, "notificationText");

		if (success && Validator.isNotNull(notificationText)) {
			SessionMessages.add(
				httpServletRequest, "form_requestProcessedSuccess",
				notificationText);
		}

		httpServletResponse.sendRedirect(_portal.escapeRedirect(redirect));

		return null;
	}

	@Activate
	@Modified
	protected void activate() {
		_infoRequestFieldValuesProviderHelper =
			new InfoRequestFieldValuesProviderHelper(_infoItemServiceRegistry);
	}

	private FragmentEntryLink _getCaptchaFragmentEntryLink(
			String formItemId, LayoutStructure layoutStructure)
		throws InfoFormException {

		for (String itemId :
				LayoutStructureItemUtil.getChildrenItemIds(
					formItemId, layoutStructure)) {

			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItem(itemId);

			if (!(layoutStructureItem instanceof
					FragmentStyledLayoutStructureItem)) {

				continue;
			}

			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)layoutStructureItem;

			long fragmentEntryLinkId =
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId();

			if (fragmentEntryLinkId <= 0) {
				continue;
			}

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
					fragmentEntryLinkId);

			if (!fragmentEntryLink.isTypeInput()) {
				continue;
			}

			if (_isCaptchaFragmentEntry(
					fragmentEntryLink.getFragmentEntryId(),
					fragmentEntryLink.getRendererKey())) {

				return fragmentEntryLink;
			}
		}

		return null;
	}

	private String _getDisplayPageURL(
		String className, String displayPage,
		HttpServletRequest httpServletRequest, Object infoItem) {

		if (infoItem == null) {
			return StringPool.BLANK;
		}

		InfoItemFieldValuesProvider<Object> infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class, className);

		if (infoItemFieldValuesProvider == null) {
			return StringPool.BLANK;
		}

		try {
			InfoFieldValue<Object> infoFieldValue =
				infoItemFieldValuesProvider.getInfoFieldValue(
					infoItem, displayPage);

			if (infoFieldValue == null) {
				return StringPool.BLANK;
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Object value = infoFieldValue.getValue(themeDisplay.getLocale());

			if (value instanceof WebURL) {
				WebURL webURL = (WebURL)value;

				return webURL.getURL();
			}

			return GetterUtil.getString(value);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return StringPool.BLANK;
	}

	private InfoItemIdentifier _getInfoItemIdentifier(
		HttpServletRequest httpServletRequest) {

		long classPK = ParamUtil.getLong(httpServletRequest, "classPK");
		String externalReferenceCode = ParamUtil.getString(
			httpServletRequest, "externalReferenceCode");

		if (classPK > 0) {
			return new ClassPKInfoItemIdentifier(classPK);
		}
		else if (Validator.isNotNull(externalReferenceCode)) {
			return new ERCInfoItemIdentifier(externalReferenceCode);
		}

		return null;
	}

	private LayoutStructure _getLayoutStructure(
		HttpServletRequest httpServletRequest) {

		LayoutStructure layoutStructure =
			(LayoutStructure)httpServletRequest.getAttribute(
				LayoutWebKeys.LAYOUT_STRUCTURE);

		if (layoutStructure != null) {
			return layoutStructure;
		}

		return _layoutStructureProvider.getLayoutStructure(
			ParamUtil.getLong(httpServletRequest, "plid"),
			ParamUtil.getLong(httpServletRequest, "segmentsExperienceId"));
	}

	private Object _getValue(InfoFieldValue<?> infoFieldValue) {
		if (infoFieldValue == null) {
			return null;
		}

		InfoField<?> infoField = infoFieldValue.getInfoField();

		if (infoField.getInfoFieldType() == DateInfoFieldType.INSTANCE) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd");

			try {
				return simpleDateFormat.format(infoFieldValue.getValue());
			}
			catch (IllegalArgumentException illegalArgumentException) {
				if (_log.isDebugEnabled()) {
					_log.debug(illegalArgumentException);
				}

				return null;
			}
		}

		Object value = infoFieldValue.getValue();

		if (value instanceof List) {
			return ListUtil.toString((List<?>)value, StringPool.BLANK);
		}

		if (value instanceof InfoLocalizedValue) {
			InfoLocalizedValue<String> infoLocalizedValue =
				(InfoLocalizedValue<String>)value;

			return infoLocalizedValue.getValues();
		}

		return String.valueOf(value);
	}

	private boolean _isCaptchaFragmentEntry(
		long fragmentEntryId, String rendererKey) {

		FragmentEntry fragmentEntry = null;

		if (Validator.isNotNull(rendererKey)) {
			fragmentEntry =
				_fragmentCollectionContributorRegistry.getFragmentEntry(
					rendererKey);
		}

		if ((fragmentEntry == null) && (fragmentEntryId > 0)) {
			fragmentEntry = _fragmentEntryLocalService.fetchFragmentEntry(
				fragmentEntryId);
		}

		if ((fragmentEntry == null) ||
			Validator.isNull(fragmentEntry.getTypeOptions())) {

			return false;
		}

		try {
			JSONObject typeOptionsJSONObject = _jsonFactory.createJSONObject(
				fragmentEntry.getTypeOptions());

			JSONArray fieldTypesJSONArray = typeOptionsJSONObject.getJSONArray(
				"fieldTypes");

			if ((fieldTypesJSONArray != null) &&
				JSONUtil.hasValue(fieldTypesJSONArray, "captcha")) {

				return true;
			}
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}
		}

		return false;
	}

	private void _validateRequiredField(
			Map<String, InfoFieldValue<Object>> infoFieldValues,
			LayoutStructureItem layoutStructureItem)
		throws InfoFormValidationException.RequiredInfoField {

		if (!Objects.equals(
				LayoutDataItemTypeConstants.TYPE_FRAGMENT,
				layoutStructureItem.getItemType())) {

			return;
		}

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)layoutStructureItem;

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		if ((fragmentEntryLink == null) ||
			!GetterUtil.getBoolean(
				_fragmentEntryConfigurationParser.getFieldValue(
					fragmentEntryLink.getEditableValues(),
					new FragmentConfigurationField(
						"inputRequired", "boolean", "false", false, "checkbox"),
					LocaleUtil.getMostRelevantLocale()))) {

			return;
		}

		String inputFieldId = GetterUtil.getString(
			_fragmentEntryConfigurationParser.getFieldValue(
				fragmentEntryLink.getEditableValues(),
				new FragmentConfigurationField(
					"inputFieldId", "string", "", false, "text"),
				LocaleUtil.getMostRelevantLocale()));

		if (!infoFieldValues.containsKey(inputFieldId) ||
			Validator.isNull(infoFieldValues.get(inputFieldId))) {

			throw new InfoFormValidationException.RequiredInfoField(
				inputFieldId);
		}
	}

	private void _validateRequiredFields(
			HttpServletRequest httpServletRequest,
			Map<String, InfoFieldValue<Object>> infoFieldValues,
			LayoutStructure layoutStructure)
		throws InfoFormException {

		String formItemId = ParamUtil.getString(
			httpServletRequest, "formItemId");

		LayoutStructureItem formLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(formItemId);

		if (formLayoutStructureItem == null) {
			throw new InfoFormException();
		}

		for (String itemId :
				LayoutStructureItemUtil.getChildrenItemIds(
					formLayoutStructureItem.getItemId(), layoutStructure)) {

			_validateRequiredField(
				infoFieldValues,
				layoutStructure.getLayoutStructureItem(itemId));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditInfoItemStrutsAction.class);

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	private volatile InfoRequestFieldValuesProviderHelper
		_infoRequestFieldValuesProviderHelper;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutStructureProvider _layoutStructureProvider;

	@Reference
	private Portal _portal;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}