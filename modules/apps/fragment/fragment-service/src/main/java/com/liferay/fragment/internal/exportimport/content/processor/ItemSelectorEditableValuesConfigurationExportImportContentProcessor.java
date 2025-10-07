/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.exportimport.content.processor;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = "content.processor.type=FragmentEntryLinkEditableValues",
	service = ExportImportContentProcessor.class
)
public class ItemSelectorEditableValuesConfigurationExportImportContentProcessor
	extends BaseEditableValuesConfigurationExportImportContentProcessor {

	@Override
	protected String getConfigurationType() {
		return "itemSelector";
	}

	@Override
	protected FragmentEntryConfigurationParser
		getFragmentEntryConfigurationParser() {

		return _fragmentEntryConfigurationParser;
	}

	@Override
	protected void replaceExportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			JSONObject configurationValueJSONObject,
			boolean exportReferencedContent)
		throws Exception {

		long classNameId = configurationValueJSONObject.getLong("classNameId");
		long classPK = configurationValueJSONObject.getLong("classPK");

		if ((classNameId == 0) || (classPK == 0)) {
			return;
		}

		_exportDDMTemplateReference(
			portletDataContext, stagedModel, configurationValueJSONObject);

		String className = _portal.fetchClassName(classNameId);

		configurationValueJSONObject.put("className", className);

		ExportImportContentProcessorUtil.exportContentReference(
			className, classPK, exportReferencedContent,
			_infoItemServiceRegistry, portletDataContext, stagedModel);
	}

	@Override
	protected void replaceImportContentReferences(
		PortletDataContext portletDataContext,
		JSONObject configurationValueJSONObject) {

		String className = configurationValueJSONObject.getString("className");

		if (Validator.isNull(className)) {
			return;
		}

		if (configurationValueJSONObject.has("template")) {
			JSONObject templateJSONObject =
				configurationValueJSONObject.getJSONObject("template");

			String ddmTemplateKey = templateJSONObject.getString("templateKey");

			if (Validator.isNull(ddmTemplateKey)) {
				ExportImportContentProcessorUtil.replaceImportContentReferences(
					configurationValueJSONObject, portletDataContext);

				return;
			}

			Map<String, String> ddmTemplateKeys =
				(Map<String, String>)portletDataContext.getNewPrimaryKeysMap(
					DDMTemplate.class + ".ddmTemplateKey");

			String importedDDMTemplateKey = MapUtil.getString(
				ddmTemplateKeys, ddmTemplateKey, ddmTemplateKey);

			templateJSONObject.put("templateKey", importedDDMTemplateKey);
		}

		ExportImportContentProcessorUtil.replaceImportContentReferences(
			configurationValueJSONObject, portletDataContext);
	}

	private void _exportDDMTemplateReference(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			JSONObject configurationValueJSONObject)
		throws Exception {

		if (!configurationValueJSONObject.has("template")) {
			return;
		}

		JSONObject templateJSONObject =
			configurationValueJSONObject.getJSONObject("template");

		String ddmTemplateKey = templateJSONObject.getString("templateKey");

		if (Validator.isNull(ddmTemplateKey)) {
			return;
		}

		DDMTemplate ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
			portletDataContext.getScopeGroupId(),
			_portal.getClassNameId(DDMStructure.class), ddmTemplateKey);

		if (ddmTemplate != null) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, stagedModel, ddmTemplate,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}
	}

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Portal _portal;

}