/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.exportimport.content.processor;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLFileEntryJSONHelper;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldValueTransformer;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesTransformer;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.journal.article.dynamic.data.mapping.form.field.type.constants.JournalArticleDDMFormFieldTypeConstants;
import com.liferay.journal.exception.NoSuchArticleException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.layout.dynamic.data.mapping.form.field.type.constants.LayoutDDMFormFieldTypeConstants;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Kocsis
 */
@Component(
	property = "model.class.name=com.liferay.dynamic.data.mapping.storage.DDMFormValues",
	service = ExportImportContentProcessor.class
)
public class DDMFormValuesExportImportContentProcessor
	implements ExportImportContentProcessor<DDMFormValues> {

	@Override
	public DDMFormValues replaceExportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			DDMFormValues ddmFormValues, boolean exportReferencedContent,
			boolean escapeContent)
		throws Exception {

		DDMFormValuesTransformer ddmFormValuesTransformer =
			new DDMFormValuesTransformer(ddmFormValues);

		ddmFormValuesTransformer.addTransformer(
			new FileEntryExportDDMFormFieldValueTransformer(
				portletDataContext, stagedModel, exportReferencedContent));
		ddmFormValuesTransformer.addTransformer(
			new JournalArticleExportDDMFormFieldValueTransformer(
				portletDataContext, stagedModel, exportReferencedContent));
		ddmFormValuesTransformer.addTransformer(
			new LayoutExportDDMFormFieldValueTransformer(
				portletDataContext, stagedModel));

		ddmFormValuesTransformer.transform();

		return ddmFormValues;
	}

	@Override
	public DDMFormValues replaceImportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			DDMFormValues ddmFormValues)
		throws Exception {

		DDMFormValuesTransformer ddmFormValuesTransformer =
			new DDMFormValuesTransformer(ddmFormValues);

		ddmFormValuesTransformer.addTransformer(
			new FileEntryImportDDMFormFieldValueTransformer(
				portletDataContext, stagedModel));
		ddmFormValuesTransformer.addTransformer(
			new JournalArticleImportDDMFormFieldValueTransformer(
				portletDataContext, stagedModel));
		ddmFormValuesTransformer.addTransformer(
			new LayoutImportDDMFormFieldValueTransformer(portletDataContext));

		ddmFormValuesTransformer.transform();

		return ddmFormValues;
	}

	@Override
	public void validateContentReferences(
		long groupId, DDMFormValues ddmFormValues) {
	}

	protected class LayoutImportDDMFormFieldValueTransformer
		implements DDMFormFieldValueTransformer {

		public LayoutImportDDMFormFieldValueTransformer(
			PortletDataContext portletDataContext) {

			_portletDataContext = portletDataContext;
		}

		@Override
		public String getFieldType() {
			return LayoutDDMFormFieldTypeConstants.LINK_TO_LAYOUT;
		}

		@Override
		public void transform(DDMFormFieldValue ddmFormFieldValue)
			throws PortalException {

			Value value = ddmFormFieldValue.getValue();

			for (Locale locale : value.getAvailableLocales()) {
				String valueString = value.getString(locale);

				JSONObject jsonObject = null;

				try {
					jsonObject = _jsonFactory.createJSONObject(valueString);
				}
				catch (JSONException jsonException) {
					if (_log.isDebugEnabled()) {
						_log.debug("Unable to parse JSON", jsonException);
					}

					continue;
				}

				if (jsonObject.length() == 0) {
					continue;
				}

				Layout importedLayout = _fetchImportedLayout(
					_portletDataContext, jsonObject);

				if (importedLayout != null) {
					value.addString(
						locale,
						toJSON(
							importedLayout, locale,
							jsonObject.getString("name")));

					continue;
				}

				Element missingReferencesElement =
					_portletDataContext.getMissingReferencesElement();

				List<Element> elements = missingReferencesElement.elements();

				for (Element element : elements) {
					String className = element.attributeValue("class-name");

					if (className.equals(Layout.class.getName())) {
						String uuid = element.attributeValue("uuid");

						if (jsonObject.has("id") &&
							!Objects.equals(uuid, jsonObject.getString("id"))) {

							continue;
						}

						String privateLayout = element.attributeValue(
							"private-layout");

						importedLayout =
							_layoutLocalService.fetchLayoutByUuidAndGroupId(
								uuid, _portletDataContext.getScopeGroupId(),
								Boolean.valueOf(privateLayout));
					}
				}

				if (importedLayout != null) {
					value.addString(
						locale,
						toJSON(
							importedLayout, locale,
							jsonObject.getString("name")));
				}
			}
		}

		protected String toJSON(Layout layout, Locale locale, String name)
			throws PortalException {

			return JSONUtil.put(
				"groupId", layout.getGroupId()
			).put(
				"id", layout.getUuid()
			).put(
				"layoutId", layout.getLayoutId()
			).put(
				"name", _getName(layout, locale, name)
			).put(
				"privateLayout", layout.isPrivateLayout()
			).put(
				"value", layout.getFriendlyURL(locale)
			).toString();
		}

		private Layout _fetchImportedLayout(
			PortletDataContext portletDataContext, JSONObject jsonObject) {

			Map<Long, Layout> layouts =
				(Map<Long, Layout>)portletDataContext.getNewPrimaryKeysMap(
					Layout.class + ".layout");

			long layoutId = jsonObject.getLong("layoutId");

			Layout layout = layouts.get(layoutId);

			if (layout == null) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to find layout with ID " + layoutId);
				}
			}

			return layout;
		}

		private String _getName(Layout layout, Locale locale, String name)
			throws PortalException {

			try {
				return layout.getBreadcrumb(locale);
			}
			catch (NoSuchLayoutException noSuchLayoutException) {
				if (_log.isWarnEnabled()) {
					_log.warn(noSuchLayoutException);
				}
			}

			return name;
		}

		private final PortletDataContext _portletDataContext;

	}

	private boolean _hasNotExportableStatus(
		StagedModel stagedModel, int status) {

		StagedModelDataHandler<?> stagedModelDataHandler =
			StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
				stagedModel.getModelClassName());

		return !ArrayUtil.contains(
			stagedModelDataHandler.getExportableStatuses(), status);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormValuesExportImportContentProcessor.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileEntryJSONHelper _dlFileEntryJSONHelper;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	private class FileEntryExportDDMFormFieldValueTransformer
		implements DDMFormFieldValueTransformer {

		public FileEntryExportDDMFormFieldValueTransformer(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			boolean exportReferencedContent) {

			_portletDataContext = portletDataContext;
			_stagedModel = stagedModel;
			_exportReferencedContent = exportReferencedContent;
		}

		@Override
		public String getFieldType() {
			return DDMFormFieldTypeConstants.DOCUMENT_LIBRARY;
		}

		@Override
		public void transform(DDMFormFieldValue ddmFormFieldValue)
			throws PortalException {

			Value value = ddmFormFieldValue.getValue();

			for (Locale locale : value.getAvailableLocales()) {
				String valueString = value.getString(locale);

				JSONObject jsonObject = null;

				try {
					jsonObject = _jsonFactory.createJSONObject(valueString);
				}
				catch (JSONException jsonException) {
					if (_log.isDebugEnabled()) {
						_log.debug("Unable to parse JSON", jsonException);
					}

					continue;
				}

				long groupId = GetterUtil.getLong(jsonObject.get("groupId"));
				String uuid = jsonObject.getString("uuid");

				if ((groupId == 0) || Validator.isNull(uuid)) {
					continue;
				}

				FileEntry fileEntry = null;

				try {
					fileEntry = _dlAppLocalService.getFileEntryByUuidAndGroupId(
						uuid, groupId);
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn("Unable to find file entry", portalException);
					}

					continue;
				}

				FileVersion fileVersion = fileEntry.getFileVersion();

				boolean disposableDependency = _hasNotExportableStatus(
					fileEntry, fileVersion.getStatus());

				if (_exportReferencedContent && !disposableDependency) {
					StagedModelDataHandlerUtil.exportReferenceStagedModel(
						_portletDataContext, _stagedModel, fileEntry,
						PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
				}
				else {
					Element entityElement =
						_portletDataContext.getExportDataElement(_stagedModel);

					String referenceType =
						PortletDataContext.REFERENCE_TYPE_DEPENDENCY;

					if (disposableDependency) {
						referenceType =
							PortletDataContext.
								REFERENCE_TYPE_DEPENDENCY_DISPOSABLE;
					}

					_portletDataContext.addReferenceElement(
						_stagedModel, entityElement, fileEntry, referenceType,
						true);
				}
			}
		}

		private final boolean _exportReferencedContent;
		private final PortletDataContext _portletDataContext;
		private final StagedModel _stagedModel;

	}

	private class FileEntryImportDDMFormFieldValueTransformer
		implements DDMFormFieldValueTransformer {

		public FileEntryImportDDMFormFieldValueTransformer(
			PortletDataContext portletDataContext, StagedModel stagedModel) {

			_portletDataContext = portletDataContext;
			_stagedModel = stagedModel;
		}

		@Override
		public String getFieldType() {
			return DDMFormFieldTypeConstants.DOCUMENT_LIBRARY;
		}

		@Override
		public void transform(DDMFormFieldValue ddmFormFieldValue)
			throws PortalException {

			Value value = ddmFormFieldValue.getValue();

			for (Locale locale : value.getAvailableLocales()) {
				String valueString = value.getString(locale);

				JSONObject jsonObject = null;

				try {
					jsonObject = _jsonFactory.createJSONObject(valueString);
				}
				catch (JSONException jsonException) {
					if (_log.isDebugEnabled()) {
						_log.debug("Unable to parse JSON", jsonException);
					}

					continue;
				}

				FileEntry importedFileEntry = fetchImportedFileEntry(
					_portletDataContext, jsonObject);

				if (importedFileEntry == null) {
					continue;
				}

				jsonObject.remove("html");

				value.addString(
					locale,
					_dlFileEntryJSONHelper.getFileEntryJSON(
						importedFileEntry, jsonObject));
			}
		}

		protected FileEntry fetchImportedFileEntry(
				PortletDataContext portletDataContext, JSONObject jsonObject)
			throws PortalException {

			long classPK = GetterUtil.getLong(jsonObject.get("classPK"));

			Map<Long, Long> classPKs =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					DLFileEntry.class);

			long newClassPK = MapUtil.getLong(classPKs, classPK, classPK);

			if (newClassPK > 0) {
				try {
					return _dlAppLocalService.getFileEntry(newClassPK);
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to find file entry with file entry ID " +
								newClassPK,
							portalException);
					}
				}
			}

			// Importing legacy data

			Map<Long, Long> groupIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					Group.class);

			long groupId = jsonObject.getLong("groupId");
			String uuid = jsonObject.getString("uuid");

			groupId = MapUtil.getLong(groupIds, groupId, groupId);

			if ((groupId <= 0) || Validator.isNull(uuid)) {
				return null;
			}

			try {
				return _dlAppLocalService.getFileEntryByUuidAndGroupId(
					uuid, groupId);
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to find file entry", portalException);
				}
			}

			return null;
		}

		private final PortletDataContext _portletDataContext;
		private final StagedModel _stagedModel;

	}

	private class JournalArticleExportDDMFormFieldValueTransformer
		implements DDMFormFieldValueTransformer {

		public JournalArticleExportDDMFormFieldValueTransformer(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			boolean exportReferencedContent) {

			_portletDataContext = portletDataContext;
			_stagedModel = stagedModel;
			_exportReferencedContent = exportReferencedContent;
		}

		@Override
		public String getFieldType() {
			return JournalArticleDDMFormFieldTypeConstants.JOURNAL_ARTICLE;
		}

		@Override
		public void transform(DDMFormFieldValue ddmFormFieldValue)
			throws PortalException {

			Value value = ddmFormFieldValue.getValue();

			for (Locale locale : value.getAvailableLocales()) {
				String valueString = value.getString(locale);

				JSONObject jsonObject = null;

				try {
					jsonObject = _jsonFactory.createJSONObject(valueString);
				}
				catch (JSONException jsonException) {
					if (_log.isDebugEnabled()) {
						_log.debug("Unable to parse JSON", jsonException);
					}

					continue;
				}

				long classPK = GetterUtil.getLong(jsonObject.get("classPK"));
				String className = GetterUtil.getString(
					jsonObject.get("className"));

				if ((classPK == 0) ||
					!className.equals(JournalArticle.class.getName())) {

					continue;
				}

				JournalArticle journalArticle =
					_journalArticleLocalService.fetchLatestArticle(classPK);

				if (journalArticle == null) {
					continue;
				}

				jsonObject.put(
					"groupId", journalArticle.getGroupId()
				).put(
					"uuid", journalArticle.getUuid()
				);

				value.addString(locale, jsonObject.toString());

				boolean disposableDependency = _hasNotExportableStatus(
					journalArticle, journalArticle.getStatus());

				if (_exportReferencedContent && !disposableDependency) {
					StagedModelDataHandlerUtil.exportReferenceStagedModel(
						_portletDataContext, _stagedModel, journalArticle,
						PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
				}
				else {
					String referenceType =
						PortletDataContext.REFERENCE_TYPE_DEPENDENCY;

					if (disposableDependency) {
						referenceType =
							PortletDataContext.
								REFERENCE_TYPE_DEPENDENCY_DISPOSABLE;
					}

					Element entityElement =
						_portletDataContext.getExportDataElement(_stagedModel);

					_portletDataContext.addReferenceElement(
						_stagedModel, entityElement, journalArticle,
						referenceType, true);
				}
			}
		}

		private final boolean _exportReferencedContent;
		private final PortletDataContext _portletDataContext;
		private final StagedModel _stagedModel;

	}

	private class JournalArticleImportDDMFormFieldValueTransformer
		implements DDMFormFieldValueTransformer {

		public JournalArticleImportDDMFormFieldValueTransformer(
			PortletDataContext portletDataContext, StagedModel stagedModel) {

			_portletDataContext = portletDataContext;
			_stagedModel = stagedModel;
		}

		@Override
		public String getFieldType() {
			return JournalArticleDDMFormFieldTypeConstants.JOURNAL_ARTICLE;
		}

		@Override
		public void transform(DDMFormFieldValue ddmFormFieldValue)
			throws PortalException {

			Value value = ddmFormFieldValue.getValue();

			for (Locale locale : value.getAvailableLocales()) {
				String valueString = value.getString(locale);

				JSONObject jsonObject = null;

				try {
					jsonObject = _jsonFactory.createJSONObject(valueString);
				}
				catch (JSONException jsonException) {
					if (_log.isDebugEnabled()) {
						_log.debug("Unable to parse JSON", jsonException);
					}

					continue;
				}

				JournalArticle journalArticle = fetchJournalArticle(jsonObject);

				if (journalArticle == null) {
					continue;
				}

				jsonObject.put("classPK", journalArticle.getResourcePrimKey());

				value.addString(locale, jsonObject.toString());
			}
		}

		protected JournalArticle fetchJournalArticle(JSONObject jsonObject)
			throws PortalException {

			long classPK = GetterUtil.getLong(jsonObject.get("classPK"));

			Map<Long, Long> classPKs =
				(Map<Long, Long>)_portletDataContext.getNewPrimaryKeysMap(
					JournalArticle.class);

			long newClassPK = MapUtil.getLong(classPKs, classPK, classPK);

			if (newClassPK > 0) {
				try {
					return _journalArticleLocalService.getLatestArticle(
						newClassPK);
				}
				catch (NoSuchArticleException noSuchArticleException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to find journal article with primary key " +
								newClassPK,
							noSuchArticleException);
					}
				}
			}

			// Legacy import

			String uuid = jsonObject.getString("uuid");
			long groupId = jsonObject.getLong("groupId");

			Map<Long, Long> groupIds =
				(Map<Long, Long>)_portletDataContext.getNewPrimaryKeysMap(
					Group.class);

			groupId = MapUtil.getLong(groupIds, groupId, groupId);

			return _journalArticleLocalService.
				fetchJournalArticleByUuidAndGroupId(uuid, groupId);
		}

		private final PortletDataContext _portletDataContext;
		private final StagedModel _stagedModel;

	}

	private class LayoutExportDDMFormFieldValueTransformer
		implements DDMFormFieldValueTransformer {

		public LayoutExportDDMFormFieldValueTransformer(
			PortletDataContext portletDataContext, StagedModel stagedModel) {

			_portletDataContext = portletDataContext;
			_stagedModel = stagedModel;
		}

		@Override
		public String getFieldType() {
			return LayoutDDMFormFieldTypeConstants.LINK_TO_LAYOUT;
		}

		@Override
		public void transform(DDMFormFieldValue ddmFormFieldValue)
			throws PortalException {

			Value value = ddmFormFieldValue.getValue();

			for (Locale locale : value.getAvailableLocales()) {
				String valueString = value.getString(locale);

				if (Validator.isNull(valueString)) {
					continue;
				}

				JSONObject jsonObject = null;

				try {
					jsonObject = _jsonFactory.createJSONObject(valueString);
				}
				catch (JSONException jsonException) {
					if (_log.isDebugEnabled()) {
						_log.debug("Unable to parse JSON", jsonException);
					}

					continue;
				}

				long groupId = GetterUtil.getLong(jsonObject.get("groupId"));
				long layoutId = GetterUtil.getLong(
					jsonObject.getLong("layoutId"));
				boolean privateLayout = jsonObject.getBoolean("privateLayout");

				Layout layout = _layoutLocalService.fetchLayout(
					groupId, privateLayout, layoutId);

				if (layout == null) {
					continue;
				}

				Element entityElement =
					_portletDataContext.getExportDataElement(_stagedModel);

				_portletDataContext.addReferenceElement(
					_stagedModel, entityElement, layout,
					PortletDataContext.REFERENCE_TYPE_LAZY, true);
			}
		}

		private final PortletDataContext _portletDataContext;
		private final StagedModel _stagedModel;

	}

}