/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.template.helper;

import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.service.DDLRecordLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldValueRendererRegistryUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Locale;

/**
 * @author Leonardo Barros
 */
public class DDLDisplayTemplateHelper {

	public DDLDisplayTemplateHelper(DLURLHelper dlURLHelper) {
		_dlURLHelper = dlURLHelper;
	}

	public String getDocumentLibraryPreviewURL(
			DDMFormFieldValue recordFieldValue, Locale locale)
		throws PortalException {

		Value value = recordFieldValue.getValue();

		String valueString = value.getString(locale);

		if (Validator.isNull(valueString)) {
			return StringPool.BLANK;
		}

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(valueString);

		String uuid = jsonObject.getString("uuid");
		long groupId = jsonObject.getLong("groupId");

		FileEntry fileEntry =
			DLAppLocalServiceUtil.getFileEntryByUuidAndGroupId(uuid, groupId);

		return _dlURLHelper.getPreviewURL(
			fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
			false, true);
	}

	public String getHTMLContent(
		DDMFormFieldValue recordFieldValue, Locale locale) {

		Value value = recordFieldValue.getValue();

		String valueString = value.getString(locale);

		if (Validator.isNull(valueString)) {
			return StringPool.BLANK;
		}

		return HtmlUtil.escapeJS(valueString);
	}

	public String getLayoutFriendlyURL(
			DDMFormFieldValue recordFieldValue, ThemeDisplay themeDisplay)
		throws PortalException {

		Value value = recordFieldValue.getValue();

		String valueString = value.getString(themeDisplay.getLocale());

		if (Validator.isNull(valueString)) {
			return StringPool.BLANK;
		}

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(valueString);

		return PortalUtil.getLayoutFriendlyURL(
			LayoutLocalServiceUtil.getLayout(
				jsonObject.getLong("groupId"),
				jsonObject.getBoolean("privateLayout"),
				jsonObject.getLong("layoutId")),
			themeDisplay);
	}

	public List<DDLRecord> getRecords(long recordSetId) throws PortalException {
		return DDLRecordLocalServiceUtil.getRecords(recordSetId);
	}

	public List<DDLRecord> getRecords(
			long recordSetId, int status, int start, int end,
			OrderByComparator<DDLRecord> orderByComparator)
		throws PortalException {

		return DDLRecordLocalServiceUtil.getRecords(
			recordSetId, status, start, end, orderByComparator);
	}

	public String renderRecordFieldValue(
			DDMFormFieldValue recordFieldValue, Locale locale)
		throws PortalException {

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			DDMFormFieldValueRendererRegistryUtil.getDDMFormFieldValueRenderer(
				recordFieldValue.getType());

		return ddmFormFieldValueRenderer.render(recordFieldValue, locale);
	}

	private final DLURLHelper _dlURLHelper;

}