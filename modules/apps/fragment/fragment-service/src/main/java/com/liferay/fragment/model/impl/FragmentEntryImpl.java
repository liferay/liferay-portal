/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.model.impl;

import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentExportImportConstants;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryVersion;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSON;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.zip.ZipWriter;

/**
 * @author Eudaldo Alonso
 */
public class FragmentEntryImpl extends FragmentEntryBaseImpl {

	@Override
	public FragmentEntry fetchDraftFragmentEntry() {
		if (isDraft()) {
			return null;
		}

		return FragmentEntryLocalServiceUtil.fetchDraft(getFragmentEntryId());
	}

	@Override
	public String getContent() {
		return StringPool.BLANK;
	}

	@Override
	public int getGlobalUsageCount() {
		return FragmentEntryLinkLocalServiceUtil.
			getFragmentEntryLinksCountByFragmentEntryId(getFragmentEntryId());
	}

	@Override
	public String getIcon() {
		if (Validator.isNull(_icon)) {
			if (isTypeInput()) {
				_icon = "forms";
			}
			else if (isTypeReact()) {
				_icon = "react";
			}
			else {
				_icon = "code";
			}
		}

		return _icon;
	}

	@Override
	public String getImagePreviewURL(ThemeDisplay themeDisplay) {
		if (Validator.isNotNull(_imagePreviewURL) &&
			!_imagePreviewURL.endsWith(StringPool.SLASH)) {

			return _imagePreviewURL;
		}

		try {
			FileEntry fileEntry = _getPreviewFileEntry();

			if (fileEntry == null) {
				return StringPool.BLANK;
			}

			return DLURLHelperUtil.getImagePreviewURL(fileEntry, themeDisplay);
		}
		catch (Exception exception) {
			_log.error("Unable to get image preview URL", exception);
		}

		return StringPool.BLANK;
	}

	@JSON
	@Override
	public int getStatus() {
		if (isHead()) {
			return WorkflowConstants.STATUS_APPROVED;
		}

		return WorkflowConstants.STATUS_DRAFT;
	}

	@Override
	public String getTypeLabel() {
		return FragmentConstants.getTypeLabel(getType());
	}

	@Override
	public int getUsageCount() {
		return FragmentEntryLinkLocalServiceUtil.
			getAllFragmentEntryLinksCountByFragmentEntryId(
				getGroupId(), getFragmentEntryId());
	}

	@Override
	public boolean isApproved() {
		return isHead();
	}

	@Override
	public boolean isDraft() {
		return !isHead();
	}

	@Override
	public boolean isTypeComponent() {
		if (getType() == FragmentConstants.TYPE_COMPONENT) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isTypeInput() {
		if (getType() == FragmentConstants.TYPE_INPUT) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isTypeReact() {
		if (getType() == FragmentConstants.TYPE_REACT) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isTypeSection() {
		if (getType() == FragmentConstants.TYPE_SECTION) {
			return true;
		}

		return false;
	}

	@Override
	public void populateVersionModel(
		FragmentEntryVersion fragmentEntryVersion) {

		super.populateVersionModel(fragmentEntryVersion);

		fragmentEntryVersion.setStatus(super.getStatus());
	}

	@Override
	public void populateZipWriter(ZipWriter zipWriter, String path)
		throws Exception {

		if (isMarketplace() || isTypeReact()) {
			return;
		}

		path = path + StringPool.SLASH + getFragmentEntryKey();

		JSONObject jsonObject = JSONUtil.put(
			"cacheable",
			() -> {
				if (isCacheable()) {
					return true;
				}

				return null;
			}
		).put(
			"configurationPath", "configuration.json"
		).put(
			"cssPath", "index.css"
		).put(
			"htmlPath", "index.html"
		).put(
			"icon",
			() -> {
				if (Validator.isNotNull(_icon)) {
					return _icon;
				}

				return null;
			}
		).put(
			"jsPath", "index.js"
		).put(
			"name", getName()
		);

		FileEntry previewFileEntry = _getPreviewFileEntry();

		if (previewFileEntry != null) {
			jsonObject.put(
				"thumbnailPath",
				"thumbnail." + previewFileEntry.getExtension());
		}

		String typeLabel = getTypeLabel();

		if (Validator.isNotNull(typeLabel)) {
			jsonObject.put("type", typeLabel);
		}

		String typeOptions = getTypeOptions();

		if (Validator.isNotNull(typeOptions)) {
			jsonObject.put(
				"typeOptions", JSONFactoryUtil.createJSONObject(typeOptions));
		}

		zipWriter.addEntry(
			path + StringPool.SLASH +
				FragmentExportImportConstants.FILE_NAME_FRAGMENT,
			jsonObject.toString(2));

		zipWriter.addEntry(path + "/configuration.json", getConfiguration());
		zipWriter.addEntry(path + "/index.css", getCss());
		zipWriter.addEntry(path + "/index.js", getJs());
		zipWriter.addEntry(path + "/index.html", getHtml());

		if (previewFileEntry != null) {
			zipWriter.addEntry(
				path + "/thumbnail." + previewFileEntry.getExtension(),
				previewFileEntry.getContentStream());
		}
	}

	@Override
	public void setIcon(String icon) {
		_icon = icon;
	}

	@Override
	public void setImagePreviewURL(String imagePreviewURL) {
		_imagePreviewURL = imagePreviewURL;
	}

	private FileEntry _getPreviewFileEntry() {
		if (getPreviewFileEntryId() <= 0) {
			return null;
		}

		try {
			return PortletFileRepositoryUtil.getPortletFileEntry(
				getPreviewFileEntryId());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get file entry preview ", portalException);
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentEntryImpl.class);

	private String _icon;
	private String _imagePreviewURL;

}