/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.exportimport.content.processor;

import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.util.configuration.FragmentConfigurationField;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Objects;

/**
 * @author Víctor Galán
 */
public abstract class
	BaseEditableValuesConfigurationExportImportContentProcessor
		implements ExportImportContentProcessor<JSONObject> {

	@Override
	public JSONObject replaceExportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			JSONObject editableValuesJSONObject,
			boolean exportReferencedContent, boolean escapeContent)
		throws Exception {

		List<FragmentConfigurationField> fragmentConfigurationFields =
			_getFragmentConfigurationFields((FragmentEntryLink)stagedModel);

		if (ListUtil.isEmpty(fragmentConfigurationFields)) {
			return editableValuesJSONObject;
		}

		JSONObject editableProcessorJSONObject =
			editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

		if (editableProcessorJSONObject == null) {
			return editableValuesJSONObject;
		}

		for (FragmentConfigurationField fragmentConfigurationField :
				fragmentConfigurationFields) {

			JSONObject jsonObject = editableProcessorJSONObject.getJSONObject(
				fragmentConfigurationField.getName());

			if (jsonObject != null) {
				replaceExportContentReferences(
					portletDataContext, stagedModel, jsonObject,
					exportReferencedContent);
			}
		}

		return editableValuesJSONObject;
	}

	@Override
	public JSONObject replaceImportContentReferences(
		PortletDataContext portletDataContext, StagedModel stagedModel,
		JSONObject editableValuesJSONObject) {

		List<FragmentConfigurationField> fragmentConfigurationFields =
			_getFragmentConfigurationFields((FragmentEntryLink)stagedModel);

		if (ListUtil.isEmpty(fragmentConfigurationFields)) {
			return editableValuesJSONObject;
		}

		JSONObject editableProcessorJSONObject =
			editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

		if (editableProcessorJSONObject == null) {
			return editableValuesJSONObject;
		}

		for (FragmentConfigurationField fragmentConfigurationField :
				fragmentConfigurationFields) {

			JSONObject jsonObject = editableProcessorJSONObject.getJSONObject(
				fragmentConfigurationField.getName());

			if (jsonObject != null) {
				String className = jsonObject.getString("className");

				if (Validator.isNotNull(className)) {
					jsonObject.put(
						"classNameId", PortalUtil.getClassNameId(className));
				}

				replaceImportContentReferences(portletDataContext, jsonObject);
			}
		}

		return editableValuesJSONObject;
	}

	@Override
	public void validateContentReferences(long groupId, JSONObject jsonObject)
		throws PortalException {
	}

	protected abstract String getConfigurationType();

	protected abstract FragmentEntryConfigurationParser
		getFragmentEntryConfigurationParser();

	protected abstract void replaceExportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			JSONObject configurationValueJSONObject,
			boolean exportReferencedContent)
		throws Exception;

	protected abstract void replaceImportContentReferences(
		PortletDataContext portletDataContext,
		JSONObject configurationValueJSONObject);

	private List<FragmentConfigurationField> _getFragmentConfigurationFields(
		FragmentEntryLink fragmentEntryLink) {

		FragmentEntryConfigurationParser fragmentEntryConfigurationParser =
			getFragmentEntryConfigurationParser();

		return ListUtil.filter(
			fragmentEntryConfigurationParser.getFragmentConfigurationFields(
				fragmentEntryLink.getConfigurationJSONObject()),
			fragmentConfigurationField -> Objects.equals(
				fragmentConfigurationField.getType(), getConfigurationType()));
	}

}