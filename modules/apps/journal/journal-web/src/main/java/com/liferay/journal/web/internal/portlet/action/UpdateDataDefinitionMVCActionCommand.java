/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.data.engine.field.type.util.LocalizedValueUtil;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinitionField;
import com.liferay.data.engine.rest.dto.v2_0.DataLayout;
import com.liferay.data.engine.rest.resource.exception.DataDefinitionValidationException;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.persistence.JournalArticleUtil;
import com.liferay.journal.web.internal.exception.DDMStructureValidationModelListenerException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/update_data_definition"
	},
	service = MVCActionCommand.class
)
public class UpdateDataDefinitionMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	public boolean processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		try {
			return super.processAction(actionRequest, actionResponse);
		}
		catch (PortletException portletException) {
			if (portletException.getCause() instanceof
					DataDefinitionValidationException) {

				DataDefinitionValidationException
					dataDefinitionValidationException =
						(DataDefinitionValidationException)
							portletException.getCause();

				SessionErrors.add(
					actionRequest, dataDefinitionValidationException.getClass(),
					dataDefinitionValidationException);
			}
			else if (portletException.getCause() instanceof
						DDMStructureValidationModelListenerException) {

				DDMStructureValidationModelListenerException
					ddmStructureValidationModelListenerException =
						(DDMStructureValidationModelListenerException)
							portletException.getCause();

				SessionErrors.add(
					actionRequest,
					ddmStructureValidationModelListenerException.getClass(),
					ddmStructureValidationModelListenerException);

				hideDefaultErrorMessage(actionRequest);
			}
			else {
				throw portletException;
			}
		}

		return false;
	}

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		DataDefinitionResource.Builder dataDefinitionResourcedBuilder =
			_dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource =
			dataDefinitionResourcedBuilder.user(
				themeDisplay.getUser()
			).build();

		long dataDefinitionId = ParamUtil.getLong(
			actionRequest, "dataDefinitionId");

		String dataDefinitionString = ParamUtil.getString(
			actionRequest, "dataDefinition");

		DataDefinition dataDefinition = DataDefinition.toDTO(
			dataDefinitionString);

		_restoreDefaultValues(dataDefinitionId, dataDefinition);

		dataDefinition.setDataDefinitionKey(
			() -> ParamUtil.getString(actionRequest, "structureKey"));
		dataDefinition.setDefaultDataLayout(
			() -> DataLayout.toDTO(
				ParamUtil.getString(actionRequest, "dataLayout")));
		dataDefinition.setDescription(
			() -> LocalizedValueUtil.toStringObjectMap(
				_localization.getLocalizationMap(
					actionRequest, "description")));

		dataDefinitionResource.putDataDefinition(
			dataDefinitionId, dataDefinition);

		List<JournalArticle> journalArticles =
			_journalArticleLocalService.getStructureArticles(dataDefinitionId);

		for (JournalArticle journalArticle : journalArticles) {
			JournalArticleUtil.clearCache(journalArticle);
		}
	}

	private void _restoreDefaultValues(
			long dataDefinitionId, DataDefinition dataDefinition)
		throws Exception {

		DDMStructure ddmStructure = _ddmStructureLocalService.getDDMStructure(
			dataDefinitionId);

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmStructure.getFullHierarchyDDMFormFieldsMap(true);

		for (DataDefinitionField dataDefinitionField :
				dataDefinition.getDataDefinitionFields()) {

			DDMFormField ddmFormField = ddmFormFieldsMap.get(
				dataDefinitionField.getName());

			if (ddmFormField != null) {
				dataDefinitionField.setDefaultValue(
					() -> LocalizedValueUtil.toLocalizedValuesMap(
						ddmFormField.getPredefinedValue()));
			}
		}
	}

	@Reference
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private Localization _localization;

}