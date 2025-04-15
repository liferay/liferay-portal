/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v2_0.DataLayout;
import com.liferay.data.engine.rest.resource.exception.DataDefinitionValidationException;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.exception.DuplicateFileEntryTypeException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryTypeException;
import com.liferay.document.library.kernel.exception.NoSuchMetadataSetException;
import com.liferay.document.library.kernel.exception.RequiredFileEntryTypeException;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeService;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.exception.RequiredStructureException;
import com.liferay.dynamic.data.mapping.exception.StructureDefinitionException;
import com.liferay.dynamic.data.mapping.exception.StructureDuplicateElementException;
import com.liferay.dynamic.data.mapping.exception.StructureNameException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
		"mvc.command.name=/document_library/edit_file_entry_type"
	},
	service = MVCActionCommand.class
)
public class EditFileEntryTypeMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	public boolean processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		try {
			return super.processAction(actionRequest, actionResponse);
		}
		catch (PortletException portletException) {
			Throwable throwable = portletException.getCause();

			if (throwable instanceof DataDefinitionValidationException ||
				throwable instanceof DuplicateFileEntryTypeException ||
				throwable instanceof NoSuchMetadataSetException ||
				throwable instanceof RequiredStructureException ||
				throwable instanceof StructureDefinitionException ||
				throwable instanceof StructureDuplicateElementException ||
				throwable instanceof StructureNameException) {

				SessionErrors.add(
					actionRequest, throwable.getClass(), throwable);
			}
			else if (throwable instanceof RequiredFileEntryTypeException) {
				SessionErrors.add(actionRequest, throwable.getClass());

				actionResponse.setRenderParameter(
					"navigation", "file_entry_types");
			}
			else if (throwable instanceof NoSuchFileEntryTypeException ||
					 throwable instanceof NoSuchStructureException ||
					 throwable instanceof PrincipalException) {

				SessionErrors.add(actionRequest, throwable.getClass());

				actionResponse.setRenderParameter(
					"mvcPath", "/document_library/error.jsp");
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

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (cmd.equals(Constants.ADD)) {
			_addFileEntryType(actionRequest);
		}
		else if (cmd.equals(Constants.UPDATE)) {
			_updateFileEntryType(actionRequest);
		}
		else if (cmd.equals(Constants.DELETE)) {
			_deleteFileEntryType(actionRequest);
		}
		else if (cmd.equals(Constants.SUBSCRIBE)) {
			_subscribeFileEntryType(actionRequest);
		}
		else if (cmd.equals(Constants.UNSUBSCRIBE)) {
			_unsubscribeFileEntryType(actionRequest);
		}

		if (SessionErrors.isEmpty(actionRequest)) {
			SessionMessages.add(
				actionRequest,
				_portal.getPortletId(actionRequest) +
					SessionMessages.KEY_SUFFIX_REFRESH_PORTLET,
				DLPortletKeys.DOCUMENT_LIBRARY);

			String redirect = _portal.escapeRedirect(
				ParamUtil.getString(actionRequest, "redirect"));

			if (Validator.isNotNull(redirect)) {
				actionResponse.sendRedirect(redirect);
			}
		}
	}

	private void _addFileEntryType(ActionRequest actionRequest)
		throws Exception {

		DataDefinitionResource.Builder dataDefinitionResourceBuilder =
			_dataDefinitionResourceFactory.create();

		DataDefinition dataDefinition = DataDefinition.toDTO(
			ParamUtil.getString(actionRequest, "dataDefinition"));
		long[] ddmStructureIds = _getLongArray(
			actionRequest, "ddmStructuresSearchContainerPrimaryKeys");

		if (ArrayUtil.isEmpty(dataDefinition.getDataDefinitionFields()) &&
			ArrayUtil.isEmpty(ddmStructureIds)) {

			throw new DataDefinitionValidationException.MustSetFields();
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		dataDefinition.setDefaultDataLayout(
			() -> DataLayout.toDTO(
				ParamUtil.getString(actionRequest, "dataLayout")));

		DataDefinitionResource dataDefinitionResource =
			dataDefinitionResourceBuilder.user(
				themeDisplay.getUser()
			).build();

		dataDefinition =
			dataDefinitionResource.postSiteDataDefinitionByContentType(
				themeDisplay.getScopeGroupId(), "document-library",
				dataDefinition);

		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name");

		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DLFileEntryType.class.getName(), actionRequest);

		DLFileEntryType fileEntryType =
			_dlFileEntryTypeService.addFileEntryType(
				null, themeDisplay.getScopeGroupId(), dataDefinition.getId(),
				null, nameMap, descriptionMap, serviceContext);

		_dlFileEntryTypeLocalService.addDDMStructureLinks(
			fileEntryType.getFileEntryTypeId(),
			SetUtil.fromArray(ddmStructureIds));
	}

	private void _deleteFileEntryType(ActionRequest actionRequest)
		throws Exception {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			long fileEntryTypeId = ParamUtil.getLong(
				actionRequest, "fileEntryTypeId");

			DLFileEntryType fileEntryType =
				_dlFileEntryTypeService.getFileEntryType(fileEntryTypeId);

			DataDefinitionResource.Builder dataDefinitionResourceBuilder =
				_dataDefinitionResourceFactory.create();

			DataDefinitionResource dataDefinitionResource =
				dataDefinitionResourceBuilder.user(
					themeDisplay.getUser()
				).build();

			dataDefinitionResource.deleteDataDefinition(
				fileEntryType.getDataDefinitionId());

			_dlFileEntryTypeService.deleteFileEntryType(fileEntryTypeId);
		}
		catch (RequiredStructureException requiredStructureException) {
			throw new RequiredFileEntryTypeException(
				requiredStructureException);
		}
	}

	private long[] _getLongArray(PortletRequest portletRequest, String name) {
		String value = portletRequest.getParameter(name);

		if (value == null) {
			return null;
		}

		return StringUtil.split(GetterUtil.getString(value), 0L);
	}

	private void _subscribeFileEntryType(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long fileEntryTypeId = ParamUtil.getLong(
			actionRequest, "fileEntryTypeId");

		_dlAppService.subscribeFileEntryType(
			themeDisplay.getScopeGroupId(), fileEntryTypeId);
	}

	private void _unsubscribeFileEntryType(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long fileEntryTypeId = ParamUtil.getLong(
			actionRequest, "fileEntryTypeId");

		_dlAppService.unsubscribeFileEntryType(
			themeDisplay.getScopeGroupId(), fileEntryTypeId);
	}

	private void _updateFileEntryType(ActionRequest actionRequest)
		throws Exception {

		DataDefinitionResource.Builder dataDefinitionResourceBuilder =
			_dataDefinitionResourceFactory.create();

		DataDefinition dataDefinition = DataDefinition.toDTO(
			ParamUtil.getString(actionRequest, "dataDefinition"));

		long[] ddmStructureIds = _getLongArray(
			actionRequest, "ddmStructuresSearchContainerPrimaryKeys");

		if (ArrayUtil.isEmpty(dataDefinition.getDataDefinitionFields()) &&
			ArrayUtil.isEmpty(ddmStructureIds)) {

			throw new DataDefinitionValidationException.MustSetFields();
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long fileEntryTypeId = ParamUtil.getLong(
			actionRequest, "fileEntryTypeId");

		dataDefinition.setDefaultDataLayout(
			() -> DataLayout.toDTO(
				ParamUtil.getString(actionRequest, "dataLayout")));

		DataDefinitionResource dataDefinitionResource =
			dataDefinitionResourceBuilder.user(
				themeDisplay.getUser()
			).build();

		dataDefinitionResource.putDataDefinition(
			ParamUtil.getLong(actionRequest, "dataDefinitionId"),
			dataDefinition);

		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name");

		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");

		_dlFileEntryTypeService.updateFileEntryType(
			fileEntryTypeId, nameMap, descriptionMap);

		if (ddmStructureIds != null) {
			_dlFileEntryTypeLocalService.updateDDMStructureLinks(
				fileEntryTypeId, SetUtil.fromArray(ddmStructureIds));
		}
	}

	@Reference
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private DLFileEntryTypeService _dlFileEntryTypeService;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

}