/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.google.docs.internal.display.context;

import com.liferay.document.library.display.context.DLFilePicker;
import com.liferay.document.library.google.docs.internal.helper.GoogleDocsMetadataHelper;
import com.liferay.document.library.google.docs.internal.util.constants.GoogleDocsConstants;
import com.liferay.document.library.google.drive.configuration.DLGoogleDriveCompanyConfiguration;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.template.URLTemplateResource;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil_IW;
import com.liferay.portal.security.key.secret.SecretResolverUtil;

/**
 * @author Iván Zaera
 * @author Sergio González
 */
public class GoogleDocsDLFilePicker implements DLFilePicker {

	public GoogleDocsDLFilePicker(
			GoogleDocsMetadataHelper googleDocsMetadataHelper, String namespace,
			String onFilePickCallback, ThemeDisplay themeDisplay)
		throws PortalException {

		_googleDocsMetadataHelper = googleDocsMetadataHelper;
		_namespace = namespace;
		_onFilePickCallback = onFilePickCallback;

		_companyId = themeDisplay.getCompanyId();

		_dlGoogleDriveCompanyConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				DLGoogleDriveCompanyConfiguration.class, _companyId);
	}

	@Override
	public String getCurrentIconURL() {
		if (_googleDocsMetadataHelper != null) {
			return _googleDocsMetadataHelper.getFieldValue(getIconFieldName());
		}

		return StringPool.BLANK;
	}

	@Override
	public String getCurrentTitle() {
		if (_googleDocsMetadataHelper != null) {
			return _googleDocsMetadataHelper.getFieldValue(
				getFileNameFieldName());
		}

		return StringPool.BLANK;
	}

	@Override
	public String getDescriptionFieldName() {
		return GoogleDocsConstants.DDM_FIELD_NAME_DESCRIPTION;
	}

	@Override
	public String getFileNameFieldName() {
		return GoogleDocsConstants.DDM_FIELD_NAME_NAME;
	}

	@Override
	public String getIconFieldName() {
		return GoogleDocsConstants.DDM_FIELD_NAME_ICON_URL;
	}

	@Override
	public String getJavaScript() throws PortalException {
		String templateId =
			"/com/liferay/document/library/google/docs/internal/display" +
				"/context/dependencies/google_file_picker.ftl";

		Class<?> clazz = getClass();

		URLTemplateResource templateResource = new URLTemplateResource(
			templateId, clazz.getResource(templateId));

		Template template = TemplateManagerUtil.getTemplate(
			TemplateConstants.LANG_TYPE_FTL, templateResource, false);

		template.put(
			"googleAppsAPIKey",
			SecretResolverUtil.resolve(
				_companyId, _dlGoogleDriveCompanyConfiguration.pickerAPIKey()));
		template.put(
			"googleClientId", _dlGoogleDriveCompanyConfiguration.clientId());
		template.put("htmlUtil", HtmlUtil_IW.getInstance());
		template.put("namespace", _namespace);
		template.put("onFilePickCallback", _onFilePickCallback);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		return unsyncStringWriter.toString();
	}

	@Override
	public String getJavaScriptModuleName() {
		return "FilePicker";
	}

	@Override
	public String getOnClickCallback() {
		return "openPicker";
	}

	@Override
	public String getTitleFieldName() {
		return GoogleDocsConstants.DDM_FIELD_NAME_TITLE;
	}

	private final long _companyId;
	private final DLGoogleDriveCompanyConfiguration
		_dlGoogleDriveCompanyConfiguration;
	private final GoogleDocsMetadataHelper _googleDocsMetadataHelper;
	private final String _namespace;
	private final String _onFilePickCallback;

}