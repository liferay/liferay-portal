/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.dynamic.data.mapping.form.field.type.internal.attachment;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldTemplateContextContributorUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldValueUtil;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.file.criterion.FileItemSelectorCriterion;
import com.liferay.object.configuration.ObjectConfiguration;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProvider;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	configurationPid = "com.liferay.object.configuration.ObjectConfiguration",
	property = "ddm.form.field.type.name=" + ObjectDDMFormFieldTypeConstants.ATTACHMENT,
	service = DDMFormFieldTemplateContextContributor.class
)
public class AttachmentDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		int maximumFileSize = _getMaximumFileSize(
			ddmFormField, ddmFormFieldRenderingContext.getHttpServletRequest());

		Map<String, Object> parameters = HashMapBuilder.<String, Object>put(
			"acceptedFileExtensions",
			ddmFormField.getProperty("acceptedFileExtensions")
		).put(
			"deleteURL",
			() -> {
				if (!Objects.equals(
						ddmFormField.getProperty("fileSource"),
						ObjectFieldSettingConstants.VALUE_USER_COMPUTER)) {

					return null;
				}

				RequestBackedPortletURLFactory requestBackedPortletURLFactory =
					RequestBackedPortletURLFactoryUtil.create(
						ddmFormFieldRenderingContext.getHttpServletRequest());

				return PortletURLBuilder.create(
					requestBackedPortletURLFactory.createActionURL(
						GetterUtil.getString(
							ddmFormField.getProperty("portletId")))
				).setActionName(
					"/object_entries/delete_attachment"
				).buildString();
			}
		).put(
			"fileSource", ddmFormField.getProperty("fileSource")
		).put(
			"maximumFileSize", maximumFileSize
		).put(
			"overallMaximumUploadRequestSize",
			_uploadServletRequestConfigurationProvider.getMaxSize()
		).put(
			"tip",
			_language.format(
				ddmFormFieldRenderingContext.getLocale(),
				"upload-a-x-no-larger-than-x-mb",
				new Object[] {
					ddmFormField.getProperty("acceptedFileExtensions"),
					maximumFileSize
				})
		).put(
			"url", _getURL(ddmFormField, ddmFormFieldRenderingContext)
		).build();

		if (FeatureFlagManagerUtil.isEnabled("LPD-32050")) {
			boolean localizedObjectField = GetterUtil.getBoolean(
				ddmFormField.getProperty("localizedObjectField"));

			parameters.put(
				"fileEntryProperties",
				_getFileEntryProperties(
					ddmFormField, ddmFormFieldRenderingContext,
					localizedObjectField));
			parameters.put("localizedObjectField", localizedObjectField);
			parameters.put(
				"value",
				_getValue(ddmFormFieldRenderingContext, localizedObjectField));

			DDMForm ddmForm = ddmFormField.getDDMForm();

			parameters.putAll(
				DDMFormFieldTemplateContextContributorUtil.
					getLocalizationParameters(
						ddmFormField, ddmForm.getDefaultLocale()));
		}
		else {
			parameters.putAll(
				_getFileEntryProperties(
					ddmFormField,
					ddmFormFieldRenderingContext.getHttpServletRequest(),
					GetterUtil.getLong(
						ddmFormFieldRenderingContext.getValue())));
		}

		return parameters;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_objectConfiguration = ConfigurableUtil.createConfigurable(
			ObjectConfiguration.class, properties);
	}

	private Object _getFileEntryProperties(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext,
		boolean localizedObjectField) {

		if (localizedObjectField) {
			JSONObject localizedValueJSONObject =
				DDMFormFieldValueUtil.getValueJSONObject(
					ddmFormFieldRenderingContext);

			Map<String, Object> localizedValue =
				localizedValueJSONObject.toMap();

			for (Map.Entry<String, Object> entry : localizedValue.entrySet()) {
				localizedValue.put(
					entry.getKey(),
					_getFileEntryProperties(
						ddmFormField,
						ddmFormFieldRenderingContext.getHttpServletRequest(),
						GetterUtil.getLong(entry.getValue())));
			}

			return _jsonFactory.createJSONObject(localizedValue);
		}

		return _getFileEntryProperties(
			ddmFormField, ddmFormFieldRenderingContext.getHttpServletRequest(),
			GetterUtil.getLong(ddmFormFieldRenderingContext.getValue()));
	}

	private Map<String, String> _getFileEntryProperties(
		DDMFormField ddmFormField, HttpServletRequest httpServletRequest,
		long value) {

		try {
			FileEntry fileEntry = _dlAppLocalService.getFileEntry(value);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return HashMapBuilder.put(
				"contentURL",
				() -> {
					String url = GetterUtil.getString(
						ddmFormField.getProperty("contentURL"));

					if (Validator.isNotNull(url)) {
						return url;
					}

					url = _dlURLHelper.getDownloadURL(
						fileEntry, fileEntry.getFileVersion(), themeDisplay,
						StringPool.BLANK);

					url = HttpComponentsUtil.addParameter(
						url, "objectDefinitionExternalReferenceCode",
						GetterUtil.getString(
							ddmFormField.getProperty(
								"objectDefinitionExternalReferenceCode")));
					url = HttpComponentsUtil.addParameter(
						url, "objectEntryExternalReferenceCode",
						GetterUtil.getString(
							ddmFormField.getProperty(
								"objectEntryExternalReferenceCode")));

					return url;
				}
			).put(
				"title", fileEntry.getFileName()
			).build();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return new HashMap<>();
		}
	}

	private long _getGroupId(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		if (GetterUtil.getBoolean(ddmFormField.getProperty("groupAware"))) {
			long groupId = GetterUtil.getLong(
				ddmFormFieldRenderingContext.getProperty("groupId"));

			if (groupId != 0) {
				return groupId;
			}
		}

		HttpServletRequest httpServletRequest =
			ddmFormFieldRenderingContext.getHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return themeDisplay.getCompanyGroupId();
	}

	private String _getItemSelectorURL(
		long groupId, String portletNamespace,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		FileItemSelectorCriterion fileItemSelectorCriterion =
			new FileItemSelectorCriterion();

		fileItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new FileEntryItemSelectorReturnType());

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory,
				_groupLocalService.fetchGroup(groupId), groupId,
				portletNamespace + "selectAttachmentEntry",
				fileItemSelectorCriterion));
	}

	private int _getMaximumFileSize(
		DDMFormField ddmFormField, HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		int maximumFileSize = GetterUtil.getInteger(
			ddmFormField.getProperty("maximumFileSize"));

		if (themeDisplay.isSignedIn() ||
			(maximumFileSize <
				_objectConfiguration.maximumFileSizeForGuestUsers())) {

			return maximumFileSize;
		}

		return _objectConfiguration.maximumFileSizeForGuestUsers();
	}

	private String _getURL(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		String url = GetterUtil.getString(ddmFormField.getProperty("url"));

		if (Validator.isNotNull(url)) {
			return url;
		}

		String fileSource = GetterUtil.getString(
			ddmFormField.getProperty("fileSource"));

		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(
				ddmFormFieldRenderingContext.getHttpServletRequest());

		if (Objects.equals(fileSource, "documentsAndMedia")) {
			return _getItemSelectorURL(
				_getGroupId(ddmFormField, ddmFormFieldRenderingContext),
				ddmFormFieldRenderingContext.getPortletNamespace(),
				requestBackedPortletURLFactory);
		}
		else if (Objects.equals(fileSource, "userComputer")) {
			return PortletURLBuilder.create(
				requestBackedPortletURLFactory.createActionURL(
					GetterUtil.getString(ddmFormField.getProperty("portletId")))
			).setActionName(
				"/object_entries/upload_attachment"
			).setParameter(
				"objectFieldId",
				GetterUtil.getLong(ddmFormField.getProperty("objectFieldId"))
			).buildString();
		}

		return StringPool.BLANK;
	}

	private Object _getValue(
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext,
		boolean localizedObjectField) {

		if (localizedObjectField) {
			return DDMFormFieldValueUtil.getValueJSONObject(
				ddmFormFieldRenderingContext);
		}

		return ddmFormFieldRenderingContext.getValue();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AttachmentDDMFormFieldTemplateContextContributor.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	private volatile ObjectConfiguration _objectConfiguration;

	@Reference
	private UploadServletRequestConfigurationProvider
		_uploadServletRequestConfigurationProvider;

}