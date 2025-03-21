/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.media.query.Condition;
import com.liferay.adaptive.media.image.media.query.MediaQuery;
import com.liferay.adaptive.media.image.media.query.MediaQueryProvider;
import com.liferay.adaptive.media.image.model.AMImageEntry;
import com.liferay.adaptive.media.image.service.AMImageEntryLocalService;
import com.liferay.adaptive.media.image.url.AMImageURLFactory;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.processor.RawMetadataProcessor;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.dynamic.data.mapping.model.DDMField;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.DDMStorageEngineManager;
import com.liferay.dynamic.data.mapping.util.comparator.StructureStructureKeyComparator;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.net.URI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/get_available_image_configurations"
	},
	service = MVCResourceCommand.class
)
public class GetAvailableImageConfigurationsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		long fileEntryId = ParamUtil.getLong(resourceRequest, "fileEntryId");

		FileEntry fileEntry = _dlAppService.getFileEntry(fileEntryId);

		JSONArray jsonArray = JSONUtil.put(
			JSONUtil.put(
				"label",
				_language.get(
					_portal.getHttpServletRequest(resourceRequest), "auto")
			).put(
				"size", fileEntry.getSize() / 1000
			).put(
				"value", "auto"
			).put(
				"width", _getFileEntryWidth(fileEntry)
			));

		Map<String, String> mediaQueriesMap = new HashMap<>();

		List<MediaQuery> mediaQueries = _mediaQueryProvider.getMediaQueries(
			fileEntry);

		for (MediaQuery mediaQuery : mediaQueries) {
			List<Condition> conditions = mediaQuery.getConditions();

			StringBundler sb = new StringBundler();

			for (Condition condition : conditions) {
				sb.append(StringPool.OPEN_PARENTHESIS);
				sb.append(condition.getAttribute());
				sb.append(StringPool.COLON);
				sb.append(condition.getValue());
				sb.append(StringPool.CLOSE_PARENTHESIS);

				if (conditions.indexOf(condition) != (conditions.size() - 1)) {
					sb.append(" and ");
				}
			}

			List<String> mediaQuerySources = StringUtil.split(
				mediaQuery.getSrc(), CharPool.COMMA);

			for (String mediaQuerySource : mediaQuerySources) {
				mediaQueriesMap.put(mediaQuerySource.trim(), sb.toString());
			}
		}

		FileVersion fileVersion = fileEntry.getFileVersion();

		List<AMImageEntry> amImageEntries =
			_amImageEntryLocalService.getAMImageEntries(
				fileVersion.getFileVersionId());

		for (AMImageEntry amImageEntry : amImageEntries) {
			JSONObject jsonObject = JSONUtil.put(
				"label", amImageEntry.getConfigurationUuid()
			).put(
				"size", amImageEntry.getSize() / 1000
			).put(
				"value", amImageEntry.getConfigurationUuid()
			).put(
				"width", amImageEntry.getWidth()
			);

			AMImageConfigurationEntry amImageConfigurationEntry =
				_amImageConfigurationHelper.getAMImageConfigurationEntry(
					fileEntry.getCompanyId(),
					amImageEntry.getConfigurationUuid());

			if (amImageConfigurationEntry != null) {
				URI uri = _amImageURLFactory.createFileEntryURL(
					fileEntry.getFileVersion(), amImageConfigurationEntry);

				jsonObject.put(
					"mediaQuery", mediaQueriesMap.get(uri.toString())
				).put(
					"url", uri.toString()
				);
			}

			jsonArray.put(jsonObject);
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, jsonArray);
	}

	private int _getFileEntryWidth(FileEntry fileEntry) throws Exception {
		FileVersion fileVersion = fileEntry.getLatestFileVersion(true);

		List<DDMStructure> ddmStructures =
			_ddmStructureLocalService.getClassStructures(
				fileVersion.getCompanyId(),
				_portal.getClassNameId(RawMetadataProcessor.class),
				StructureStructureKeyComparator.getInstance(false));

		for (DDMStructure ddmStructure : ddmStructures) {
			DLFileEntryMetadata fileEntryMetadata =
				_dlFileEntryMetadataLocalService.fetchFileEntryMetadata(
					ddmStructure.getStructureId(),
					fileVersion.getFileVersionId());

			if (fileEntryMetadata == null) {
				continue;
			}

			try {
				DDMFormValues ddmFormValues =
					_ddmStorageEngineManager.getDDMFormValues(
						fileEntryMetadata.getDDMStorageId());

				if (ddmFormValues == null) {
					continue;
				}

				List<DDMField> ddmFields = _ddmFieldLocalService.getDDMFields(
					fileEntryMetadata.getDDMStorageId(), "TIFF_IMAGE_WIDTH");

				if (ListUtil.isEmpty(ddmFields)) {
					return 0;
				}

				DDMField ddmField = ddmFields.get(0);

				DDMFieldAttribute ddmFieldAttribute =
					_ddmFieldLocalService.fetchDDMFieldAttribute(
						ddmField.getFieldId(), StringPool.BLANK,
						StringPool.BLANK);

				if (ddmFieldAttribute == null) {
					return 0;
				}

				return GetterUtil.getInteger(
					ddmFieldAttribute.getAttributeValue());
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to find dynamic data mapping form values ",
							"for ", fileVersion.getFileVersionId(),
							" in structure ", ddmStructure.getStructureKey()));
				}

				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		return 0;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetAvailableImageConfigurationsMVCResourceCommand.class);

	@Reference
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Reference
	private AMImageEntryLocalService _amImageEntryLocalService;

	@Reference
	private AMImageURLFactory _amImageURLFactory;

	@Reference
	private DDMFieldLocalService _ddmFieldLocalService;

	@Reference
	private DDMStorageEngineManager _ddmStorageEngineManager;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLFileEntryMetadataLocalService _dlFileEntryMetadataLocalService;

	@Reference
	private Language _language;

	@Reference
	private MediaQueryProvider _mediaQueryProvider;

	@Reference
	private Portal _portal;

}