/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.web.internal.portlet.action;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.dynamic.data.lists.constants.DDLRecordConstants;
import com.liferay.dynamic.data.lists.exception.RecordSetDDMStructureIdException;
import com.liferay.dynamic.data.lists.exception.RecordSetNameException;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.service.DDLRecordLocalService;
import com.liferay.dynamic.data.mapping.exception.RequiredStructureException;
import com.liferay.dynamic.data.mapping.exception.StructureDefinitionException;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesMerger;
import com.liferay.dynamic.data.mapping.util.FieldsToDDMFormValuesConverter;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.RequiredWorkflowDefinitionException;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.kaleo.exception.DuplicateKaleoDefinitionNameException;
import com.liferay.portal.workflow.kaleo.exception.KaleoDefinitionContentException;
import com.liferay.portal.workflow.kaleo.exception.KaleoDefinitionNameException;
import com.liferay.portal.workflow.kaleo.exception.NoSuchDefinitionException;
import com.liferay.portal.workflow.kaleo.exception.NoSuchDefinitionVersionException;
import com.liferay.portal.workflow.kaleo.forms.exception.KaleoProcessDDMTemplateIdException;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess;
import com.liferay.portal.workflow.kaleo.forms.service.KaleoProcessService;
import com.liferay.portal.workflow.kaleo.forms.service.permission.KaleoProcessPermission;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
public abstract class BaseKaleoFormsMVCActionCommand
	extends BaseMVCActionCommand {

	/**
	 * Checks the permission for the action ID.
	 *
	 * @param  serviceContext the service context to be applied
	 * @param  actionId the action ID
	 * @throws Exception if an exception occurred
	 */
	protected void checkKaleoProcessPermission(
			ServiceContext serviceContext, String actionId)
		throws Exception {

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		long kaleoProcessId = ParamUtil.getLong(
			httpServletRequest, "kaleoProcessId");

		KaleoProcessPermission.check(
			permissionChecker, kaleoProcessId, actionId);
	}

	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof DuplicateKaleoDefinitionNameException ||
			throwable instanceof KaleoDefinitionContentException ||
			throwable instanceof KaleoDefinitionNameException ||
			throwable instanceof KaleoProcessDDMTemplateIdException ||
			throwable instanceof NoSuchDefinitionException ||
			throwable instanceof NoSuchDefinitionVersionException ||
			throwable instanceof RecordSetDDMStructureIdException ||
			throwable instanceof RecordSetNameException ||
			throwable instanceof RequiredStructureException ||
			throwable instanceof RequiredWorkflowDefinitionException ||
			throwable instanceof StructureDefinitionException ||
			throwable instanceof WorkflowException) {

			return true;
		}

		return false;
	}

	/**
	 * Updates the DDL record by replacing its values, or creates a new DDL
	 * record if one does not exist in the request. This method also updates the
	 * Kaleo process's asset entry.
	 *
	 * @param  serviceContext the service context to be applied
	 * @return the DDL record
	 * @throws Exception if an exception occurred
	 */
	protected DDLRecord updateDDLRecord(ServiceContext serviceContext)
		throws Exception {

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		long ddlRecordId = ParamUtil.getLong(httpServletRequest, "ddlRecordId");

		long kaleoProcessId = ParamUtil.getLong(
			httpServletRequest, "kaleoProcessId");

		KaleoProcess kaleoProcess = kaleoProcessService.getKaleoProcess(
			kaleoProcessId);

		DDLRecord ddlRecord = ddlRecordLocalService.fetchDDLRecord(ddlRecordId);

		DDLRecordSet ddlRecordSet = kaleoProcess.getDDLRecordSet();

		DDMFormValues ddmFormValues = ddm.getDDMFormValues(
			ddlRecordSet.getDDMStructureId(), kaleoProcess.getDDMTemplateId(),
			StringPool.BLANK, serviceContext);

		if (ddlRecord == null) {
			long ddlRecordSetId = ParamUtil.getLong(
				httpServletRequest, "ddlRecordSetId");

			ddlRecord = ddlRecordLocalService.addRecord(
				serviceContext.getUserId(), serviceContext.getScopeGroupId(),
				ddlRecordSetId, DDLRecordConstants.DISPLAY_INDEX_DEFAULT,
				ddmFormValues, serviceContext);
		}
		else {
			boolean majorVersion = ParamUtil.getBoolean(
				serviceContext, "majorVersion");

			long ddmTemplateId = ParamUtil.getLong(
				httpServletRequest, "ddmTemplateId");

			DDMStructure ddmStructure = ddlRecordSet.getDDMStructure(
				ddmTemplateId);

			long ddmStructureId = ddmStructure.getPrimaryKey();

			DDMFormValues ddlRecordDDMFormValues = ddlRecord.getDDMFormValues();

			Fields reviewFormFields = ddm.getFields(
				ddmStructureId, ddlRecordDDMFormValues);

			DDMFormValues reviewFormDDMFormValues =
				fieldsToDDMFormValuesConverter.convert(
					ddmStructure, reviewFormFields);

			_removeRemovedByReviewerDDMFormFieldValues(
				ddlRecordDDMFormValues.getDDMFormFieldValues(),
				_getRemovedByReviewerDDMFormFieldValues(
					ddmFormValues.getDDMFormFieldValues(),
					reviewFormDDMFormValues.getDDMFormFieldValues()));

			ddmFormValues = ddmFormValuesMerger.merge(
				ddmFormValues, ddlRecordDDMFormValues);

			ddlRecord = ddlRecordLocalService.updateRecord(
				serviceContext.getUserId(), ddlRecordId, majorVersion,
				DDLRecordConstants.DISPLAY_INDEX_DEFAULT, ddmFormValues,
				serviceContext);
		}

		_updateAssetEntry(
			serviceContext.getUserId(), ddlRecord, kaleoProcess,
			serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(), serviceContext.getLocale(),
			serviceContext.getAssetPriority());

		return ddlRecord;
	}

	@Reference
	protected AssetEntryLocalService assetEntryLocalService;

	@Reference
	protected DDLRecordLocalService ddlRecordLocalService;

	@Reference
	protected DDM ddm;

	@Reference
	protected DDMFormValuesMerger ddmFormValuesMerger;

	@Reference
	protected FieldsToDDMFormValuesConverter fieldsToDDMFormValuesConverter;

	@Reference
	protected KaleoProcessService kaleoProcessService;

	@Reference
	protected Portal portal;

	private DDMFormFieldValue _getNameAndInstanceIdDDMFormFieldValue(
		List<DDMFormFieldValue> ddmFormFieldValues, String name,
		String instanceId) {

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			if (name.equals(ddmFormFieldValue.getName()) &&
				instanceId.equals(ddmFormFieldValue.getInstanceId())) {

				return ddmFormFieldValue;
			}
		}

		return null;
	}

	private List<DDMFormFieldValue> _getRemovedByReviewerDDMFormFieldValues(
		List<DDMFormFieldValue> reviewedDDMFormFieldValues,
		List<DDMFormFieldValue> beforeReviewDDMFormFieldValues) {

		return TransformUtil.transform(
			beforeReviewDDMFormFieldValues,
			beforeReviewDDMFormFieldValue -> {
				DDMFormFieldValue actualDDMFormFieldValue =
					_getNameAndInstanceIdDDMFormFieldValue(
						reviewedDDMFormFieldValues,
						beforeReviewDDMFormFieldValue.getName(),
						beforeReviewDDMFormFieldValue.getInstanceId());

				if (actualDDMFormFieldValue == null) {
					DDMFormField ddmFormField =
						beforeReviewDDMFormFieldValue.getDDMFormField();

					if (!ddmFormField.isReadOnly()) {
						return beforeReviewDDMFormFieldValue;
					}

					return null;
				}

				List<DDMFormFieldValue>
					nestedRemovedByReviewerDDMFormFieldValues =
						_getRemovedByReviewerDDMFormFieldValues(
							actualDDMFormFieldValue.
								getNestedDDMFormFieldValues(),
							beforeReviewDDMFormFieldValue.
								getNestedDDMFormFieldValues());

				if (nestedRemovedByReviewerDDMFormFieldValues.isEmpty()) {
					return null;
				}

				beforeReviewDDMFormFieldValue.setNestedDDMFormFields(
					nestedRemovedByReviewerDDMFormFieldValues);

				return beforeReviewDDMFormFieldValue;
			});
	}

	private void _removeRemovedByReviewerDDMFormFieldValues(
		List<DDMFormFieldValue> currentDDMFormFieldValues,
		List<DDMFormFieldValue> removedByReviewerDDMFormFieldValues) {

		List<DDMFormFieldValue> pendingRemovalDDMFormFieldValues =
			new ArrayList<>();

		for (DDMFormFieldValue currentDDMFormFieldValue :
				currentDDMFormFieldValues) {

			DDMFormFieldValue actualDDMFormFieldValue =
				_getNameAndInstanceIdDDMFormFieldValue(
					removedByReviewerDDMFormFieldValues,
					currentDDMFormFieldValue.getName(),
					currentDDMFormFieldValue.getInstanceId());

			if (actualDDMFormFieldValue != null) {
				if (actualDDMFormFieldValue.equals(currentDDMFormFieldValue)) {
					pendingRemovalDDMFormFieldValues.add(
						currentDDMFormFieldValue);
				}
				else {
					_removeRemovedByReviewerDDMFormFieldValues(
						currentDDMFormFieldValue.getNestedDDMFormFieldValues(),
						actualDDMFormFieldValue.getNestedDDMFormFieldValues());
				}
			}
		}

		if (!pendingRemovalDDMFormFieldValues.isEmpty()) {
			currentDDMFormFieldValues.removeAll(
				pendingRemovalDDMFormFieldValues);
		}
	}

	/**
	 * Updates the Kaleo process's asset entry with new asset categories, tag
	 * names, and link entries, removing and adding them as necessary.
	 *
	 * @param  userId the primary key of the user updating the record's asset
	 *         entry
	 * @param  ddlRecord the DDL record
	 * @param  kaleoProcess the Kaleo process
	 * @param  assetCategoryIds the primary keys of the new asset categories
	 * @param  assetTagNames the new asset tag names
	 * @param  locale the locale to apply to the asset
	 * @param  priority the new priority
	 * @throws PortalException if a portal exception occurred
	 */
	private void _updateAssetEntry(
			long userId, DDLRecord ddlRecord, KaleoProcess kaleoProcess,
			long[] assetCategoryIds, String[] assetTagNames, Locale locale,
			Double priority)
		throws Exception {

		DDLRecordSet ddlRecordSet = ddlRecord.getRecordSet();

		DDMStructure ddmStructure = ddlRecordSet.getDDMStructure();

		String ddmStructureName = ddmStructure.getName(locale);

		String ddlRecordSetName = ddlRecordSet.getName(locale);

		String title = LanguageUtil.format(
			locale, "new-x-for-list-x",
			new Object[] {ddmStructureName, ddlRecordSetName}, false);

		assetEntryLocalService.updateEntry(
			userId, kaleoProcess.getGroupId(), kaleoProcess.getCreateDate(),
			kaleoProcess.getModifiedDate(), KaleoProcess.class.getName(),
			ddlRecord.getRecordId(), kaleoProcess.getUuid(), 0,
			assetCategoryIds, assetTagNames, true, true, null, null, null, null,
			ContentTypes.TEXT_HTML, title, null, StringPool.BLANK, null, null,
			0, 0, priority);
	}

}