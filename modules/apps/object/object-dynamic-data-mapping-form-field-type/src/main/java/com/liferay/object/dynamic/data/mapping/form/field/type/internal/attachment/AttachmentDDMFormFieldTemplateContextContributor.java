/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.dynamic.data.mapping.form.field.type.internal.attachment;

import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.service.DLAppLocalService;
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
import com.liferay.object.field.attachment.AttachmentManager;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProvider;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
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

		DDMForm ddmForm = ddmFormField.getDDMForm();
		HttpServletRequest httpServletRequest =
			ddmFormFieldRenderingContext.getHttpServletRequest();
		boolean localizedObjectField = GetterUtil.getBoolean(
			ddmFormField.getProperty("localizedObjectField"));

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long maximumFileSize = _attachmentManager.getMaximumFileSize(
			GetterUtil.getLong(ddmFormField.getProperty("objectFieldId")),
			themeDisplay.isSignedIn());

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			GetterUtil.getLong(ddmFormField.getProperty("objectFieldId")));

		return HashMapBuilder.<String, Object>put(
			ObjectFieldSettingConstants.NAME_ACCEPTED_FILE_EXTENSIONS,
			ddmFormField.getProperty(
				ObjectFieldSettingConstants.NAME_ACCEPTED_FILE_EXTENSIONS)
		).put(
			ObjectFieldSettingConstants.NAME_STORAGE_DEPOT_GROUP,
			_getGroupExternalReferenceCode(objectField)
		).put(
			ObjectFieldSettingConstants.NAME_STORAGE_DL_FOLDER_PATH,
			ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.NAME_STORAGE_DL_FOLDER_PATH,
				objectField)
		).put(
			"deleteURL",
			() -> {
				if (!Objects.equals(
						ddmFormField.getProperty(
							ObjectFieldSettingConstants.NAME_FILE_SOURCE),
						ObjectFieldSettingConstants.
							VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA)) {

					return null;
				}

				RequestBackedPortletURLFactory requestBackedPortletURLFactory =
					RequestBackedPortletURLFactoryUtil.create(
						httpServletRequest);

				return PortletURLBuilder.create(
					requestBackedPortletURLFactory.createActionURL(
						GetterUtil.getString(
							ddmFormField.getProperty("portletId")))
				).setActionName(
					"/object_entries/delete_attachment"
				).buildString();
			}
		).put(
			"fileEntryProperties",
			_getFileEntryProperties(
				ddmFormField, ddmFormFieldRenderingContext,
				localizedObjectField, themeDisplay)
		).put(
			"fileSource", ddmFormField.getProperty("fileSource")
		).put(
			"localizedObjectField", localizedObjectField
		).put(
			"maximumFileSize", maximumFileSize
		).put(
			"objectFieldId",
			GetterUtil.getLong(ddmFormField.getProperty("objectFieldId"))
		).put(
			"overallMaximumUploadRequestSize",
			_uploadServletRequestConfigurationProvider.getMaxSize()
		).put(
			"tip",
			_language.format(
				themeDisplay.getLocale(), "upload-a-x-no-larger-than-x",
				new Object[] {
					ddmFormField.getProperty(
						ObjectFieldSettingConstants.
							NAME_ACCEPTED_FILE_EXTENSIONS),
					_language.formatStorageSize(
						maximumFileSize, themeDisplay.getLocale())
				})
		).put(
			"url",
			_getURL(
				ddmFormField, ddmFormFieldRenderingContext, httpServletRequest)
		).put(
			"value",
			_getValue(ddmFormFieldRenderingContext, localizedObjectField)
		).putAll(
			DDMFormFieldTemplateContextContributorUtil.
				getLocalizationParameters(
					ddmFormField, ddmForm.getDefaultLocale())
		).build();
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
		boolean localizedObjectField, ThemeDisplay themeDisplay) {

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
						ddmFormField, themeDisplay,
						GetterUtil.getLong(entry.getValue())));
			}

			return _jsonFactory.createJSONObject(localizedValue);
		}

		return _getFileEntryProperties(
			ddmFormField, themeDisplay,
			GetterUtil.getLong(ddmFormFieldRenderingContext.getValue()));
	}

	private Map<String, String> _getFileEntryProperties(
		DDMFormField ddmFormField, ThemeDisplay themeDisplay, long value) {

		try {
			FileEntry fileEntry = _dlAppLocalService.getFileEntry(value);

			return HashMapBuilder.put(
				"contentURL",
				() -> {
					String url = GetterUtil.getString(
						ddmFormField.getProperty("contentURL"));

					if (Validator.isNotNull(url)) {
						return url;
					}

					ObjectEntry objectEntry =
						_objectEntryLocalService.fetchObjectEntry(
							GetterUtil.getLong(
								ddmFormField.getProperty("objectEntryId")));

					if (objectEntry == null) {
						return StringPool.BLANK;
					}

					return ObjectFieldUtil.getAttachmentDownloadURL(
						_dlURLHelper, fileEntry, objectEntry.getGroupId(),
						GetterUtil.getString(
							ddmFormField.getProperty(
								"objectDefinitionExternalReferenceCode")),
						objectEntry, _objectEntryService,
						_objectFieldLocalService.fetchObjectField(
							GetterUtil.getLong(
								ddmFormField.getProperty("objectFieldId"))),
						_getPermissionChecker(themeDisplay), themeDisplay);
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

	private String _getGroupExternalReferenceCode(ObjectField objectField) {
		String groupId = ObjectFieldSettingUtil.getValue(
			ObjectFieldSettingConstants.NAME_STORAGE_DEPOT_GROUP, objectField);

		Group group = _groupLocalService.fetchGroup(
			GetterUtil.getLong(groupId));

		if (group != null) {
			return group.getExternalReferenceCode();
		}

		return null;
	}

	private long _getGroupId(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext,
		HttpServletRequest httpServletRequest) {

		if (GetterUtil.getBoolean(ddmFormField.getProperty("groupAware"))) {
			long groupId = GetterUtil.getLong(
				ddmFormFieldRenderingContext.getProperty("groupId"));

			if (groupId != 0) {
				return groupId;
			}
		}

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

	private PermissionChecker _getPermissionChecker(ThemeDisplay themeDisplay) {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker == null) {
			permissionChecker = themeDisplay.getPermissionChecker();
		}

		return permissionChecker;
	}

	private String _getURL(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext,
		HttpServletRequest httpServletRequest) {

		String url = GetterUtil.getString(ddmFormField.getProperty("url"));

		if (Validator.isNotNull(url)) {
			return url;
		}

		String fileSource = GetterUtil.getString(
			ddmFormField.getProperty(
				ObjectFieldSettingConstants.NAME_FILE_SOURCE));

		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest);

		if (Objects.equals(
				fileSource, ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA)) {

			return _getItemSelectorURL(
				_getGroupId(
					ddmFormField, ddmFormFieldRenderingContext,
					httpServletRequest),
				ddmFormFieldRenderingContext.getPortletNamespace(),
				requestBackedPortletURLFactory);
		}
		else if (Objects.equals(
					fileSource,
					ObjectFieldSettingConstants.
						VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA)) {

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
	private AttachmentManager _attachmentManager;

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
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private UploadServletRequestConfigurationProvider
		_uploadServletRequestConfigurationProvider;

}