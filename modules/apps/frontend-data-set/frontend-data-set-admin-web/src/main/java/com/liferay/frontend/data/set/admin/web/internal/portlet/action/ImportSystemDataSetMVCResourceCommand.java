/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.admin.web.internal.portlet.action;

import com.liferay.frontend.data.set.FDSEntryItemImportPolicy;
import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.frontend.data.set.SystemFDSEntryRegistry;
import com.liferay.frontend.data.set.action.FDSCreationMenu;
import com.liferay.frontend.data.set.action.FDSCreationMenuRegistry;
import com.liferay.frontend.data.set.action.FDSItemsActions;
import com.liferay.frontend.data.set.action.FDSItemsActionsRegistry;
import com.liferay.frontend.data.set.admin.web.internal.constants.FDSAdminPortletKeys;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marko Cikos
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FDSAdminPortletKeys.FDS_ADMIN,
		"mvc.command.name=/frontend_data_set_admin/import_system_data_set"
	},
	service = MVCResourceCommand.class
)
public class ImportSystemDataSetMVCResourceCommand
	extends BaseTransactionalMVCResourceCommand {

	@Override
	protected void doTransactionalCommand(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ObjectDefinition dataSetObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				themeDisplay.getCompanyId(), "DataSet");

		String fdsName = ParamUtil.getString(resourceRequest, "name");

		SystemFDSEntry systemFDSEntry =
			_systemFDSEntryRegistry.getSystemFDSEntry(fdsName);

		ObjectEntry objectEntry = _objectEntryService.addOrUpdateObjectEntry(
			fdsName, 0, dataSetObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			HashMapBuilder.<String, Serializable>put(
				"additionalAPIURLParameters",
				systemFDSEntry.getAdditionalAPIURLParameters()
			).put(
				"defaultItemsPerPage", systemFDSEntry.getDefaultItemsPerPage()
			).put(
				"description", systemFDSEntry.getDescription()
			).put(
				"label", systemFDSEntry.getTitle()
			).put(
				"listOfItemsPerPage",
				StringUtil.merge(
					systemFDSEntry.getListOfItemsPerPage(), StringPool.COMMA)
			).put(
				"propsTransformer", systemFDSEntry.getPropsTransformer()
			).put(
				"restApplication", systemFDSEntry.getRESTApplication()
			).put(
				"restEndpoint", systemFDSEntry.getRESTEndpoint()
			).put(
				"restSchema", systemFDSEntry.getRESTSchema()
			).build(),
			new ServiceContext());

		ObjectDefinition dataSetActionObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				themeDisplay.getCompanyId(), "DataSetAction");

		FDSCreationMenu fdsCreationMenu =
			_fdsCreationMenuRegistry.getFDSCreationMenu(fdsName);

		if (fdsCreationMenu != null) {
			_addFDSCreationMenuObjectEntries(
				objectEntry.getObjectEntryId(),
				dataSetActionObjectDefinition.getDefaultLanguageId(),
				fdsCreationMenu, _portal.getHttpServletRequest(resourceRequest),
				dataSetActionObjectDefinition.getObjectDefinitionId());
		}

		FDSItemsActions fdsItemsActions =
			_fdsItemsActionsRegistry.getFDSItemsActions(fdsName);

		if (fdsItemsActions != null) {
			_addFDSItemsActionsObjectEntries(
				objectEntry.getObjectEntryId(),
				dataSetActionObjectDefinition.getDefaultLanguageId(),
				fdsItemsActions, _portal.getHttpServletRequest(resourceRequest),
				dataSetActionObjectDefinition.getObjectDefinitionId());
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, objectEntry);
	}

	private void _addFDSCreationMenuObjectEntries(
			long dataSetId, String defaultLanguageId,
			FDSCreationMenu fdsCreationMenu,
			HttpServletRequest httpServletRequest, long objectDefinitionId)
		throws Exception {

		CreationMenu creationMenu = fdsCreationMenu.getCreationMenu(
			httpServletRequest);

		List<DropdownItem> primaryDropdownItems =
			(List<DropdownItem>)creationMenu.get("primaryItems");

		FDSEntryItemImportPolicy fdsEntryItemImportPolicy =
			fdsCreationMenu.getFDSEntryItemImportPolicy();

		if (fdsEntryItemImportPolicy == FDSEntryItemImportPolicy.DETACHED) {
			for (DropdownItem dropdownItem : primaryDropdownItems) {
				Map<String, Serializable> objectEntryValues =
					HashMapBuilder.<String, Serializable>put(
						"icon",
						() -> _getOptionalValue(dropdownItem.get("icon"))
					).put(
						"label_i18n",
						() -> _getLocalizeableValue(
							defaultLanguageId,
							_getOptionalValue(dropdownItem.get("label")))
					).put(
						"r_dataSetToDataSetActions_l_dataSetId", dataSetId
					).put(
						"target", String.valueOf(dropdownItem.get("target"))
					).put(
						"type", "creation"
					).put(
						"url", () -> _getOptionalValue(dropdownItem.get("href"))
					).build();

				Object dataObject = dropdownItem.get("data");

				if (dataObject != null) {
					Map<String, Object> data = (Map<String, Object>)dataObject;

					objectEntryValues.putAll(
						HashMapBuilder.<String, Serializable>put(
							"confirmationMessage_i18n",
							() -> _getLocalizeableValue(
								defaultLanguageId,
								_getOptionalValue(
									data.get("confirmationMessage")))
						).put(
							"confirmationMessageType",
							() -> _getOptionalValue(
								data.get("confirmationMessageType"))
						).put(
							"modalSize",
							() -> _getOptionalValue(data.get("modalSize"))
						).put(
							"permissionKey",
							() -> _getOptionalValue(data.get("permissionKey"))
						).put(
							"title_i18n",
							() -> _getLocalizeableValue(
								defaultLanguageId,
								_getOptionalValue(data.get("title")))
						).build());
				}

				_objectEntryService.addObjectEntry(
					0, objectDefinitionId,
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					null, objectEntryValues, new ServiceContext());
			}
		}
		else if (fdsEntryItemImportPolicy ==
					FDSEntryItemImportPolicy.ITEM_PROXY) {

			for (DropdownItem dropdownItem : primaryDropdownItems) {
				Object id = null;

				Object dataObject = dropdownItem.get("data");

				if (dataObject != null) {
					Map<String, Object> data = (Map<String, Object>)dataObject;

					id = data.get("id");
				}

				if (id == null) {
					continue;
				}

				_objectEntryService.addOrUpdateObjectEntry(
					String.valueOf(id), 0, objectDefinitionId,
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					HashMapBuilder.<String, Serializable>put(
						"icon",
						() -> _getOptionalValue(dropdownItem.get("icon"))
					).put(
						"label_i18n",
						() -> _getLocalizeableValue(
							defaultLanguageId,
							_getOptionalValue(dropdownItem.get("label")))
					).put(
						"r_dataSetToDataSetActions_l_dataSetId", dataSetId
					).put(
						"target", FDSEntryItemImportPolicy.ITEM_PROXY
					).put(
						"type", "creation"
					).build(),
					new ServiceContext());
			}
		}
		else {
			_objectEntryService.addObjectEntry(
				0, objectDefinitionId,
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"r_dataSetToDataSetActions_l_dataSetId", dataSetId
				).put(
					"target", FDSEntryItemImportPolicy.GROUP_PROXY
				).put(
					"type", "creation"
				).build(),
				new ServiceContext());
		}
	}

	private void _addFDSItemsActionsObjectEntries(
			long dataSetId, String defaultLanguageId,
			FDSItemsActions fdsItemsActions,
			HttpServletRequest httpServletRequest, long objectDefinitionId)
		throws Exception {

		List<FDSActionDropdownItem> fdsActionDropdownItems =
			fdsItemsActions.getFDSActionDropdownItems(httpServletRequest);

		FDSEntryItemImportPolicy fdsEntryItemImportPolicy =
			fdsItemsActions.getFDSEntryItemImportPolicy();

		if (fdsEntryItemImportPolicy == FDSEntryItemImportPolicy.DETACHED) {
			for (FDSActionDropdownItem fdsActionDropdownItem :
					fdsActionDropdownItems) {

				Map<String, Object> data =
					(Map<String, Object>)fdsActionDropdownItem.get("data");

				_objectEntryService.addObjectEntry(
					0, objectDefinitionId,
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					null,
					HashMapBuilder.<String, Serializable>put(
						"confirmationMessage_i18n",
						() -> _getLocalizeableValue(
							defaultLanguageId,
							_getOptionalValue(data.get("confirmationMessage")))
					).put(
						"confirmationMessageType",
						() -> _getOptionalValue(
							data.get("confirmationMessageType"))
					).put(
						"errorMessage_i18n",
						() -> _getLocalizeableValue(
							defaultLanguageId,
							_getOptionalValue(data.get("errorMessage")))
					).put(
						"icon",
						() -> _getOptionalValue(
							fdsActionDropdownItem.get("icon"))
					).put(
						"label_i18n",
						() -> _getLocalizeableValue(
							defaultLanguageId,
							_getOptionalValue(
								fdsActionDropdownItem.get("label")))
					).put(
						"method", () -> _getOptionalValue(data.get("method"))
					).put(
						"modalSize",
						() -> _getOptionalValue(data.get("modalSize"))
					).put(
						"permissionKey",
						() -> _getOptionalValue(data.get("permissionKey"))
					).put(
						"r_dataSetToDataSetActions_l_dataSetId", dataSetId
					).put(
						"requestBody",
						() -> _getOptionalValue(data.get("requestBody"))
					).put(
						"successMessage_i18n",
						() -> _getLocalizeableValue(
							defaultLanguageId,
							_getOptionalValue(data.get("successMessage")))
					).put(
						"target",
						String.valueOf(fdsActionDropdownItem.get("target"))
					).put(
						"title_i18n",
						() -> _getLocalizeableValue(
							defaultLanguageId,
							_getOptionalValue(data.get("title")))
					).put(
						"type", "item"
					).put(
						"url",
						() -> _getOptionalValue(
							fdsActionDropdownItem.get("href"))
					).build(),
					new ServiceContext());
			}
		}
		else if (fdsEntryItemImportPolicy ==
					FDSEntryItemImportPolicy.ITEM_PROXY) {

			for (FDSActionDropdownItem fdsActionDropdownItem :
					fdsActionDropdownItems) {

				Map<String, Object> data =
					(Map<String, Object>)fdsActionDropdownItem.get("data");

				Object id = data.get("id");

				if (id == null) {
					continue;
				}

				_objectEntryService.addOrUpdateObjectEntry(
					String.valueOf(id), 0, objectDefinitionId,
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					HashMapBuilder.<String, Serializable>put(
						"icon",
						() -> _getOptionalValue(
							fdsActionDropdownItem.get("icon"))
					).put(
						"label_i18n",
						() -> _getLocalizeableValue(
							defaultLanguageId,
							_getOptionalValue(
								fdsActionDropdownItem.get("label")))
					).put(
						"r_dataSetToDataSetActions_l_dataSetId", dataSetId
					).put(
						"target", FDSEntryItemImportPolicy.ITEM_PROXY
					).put(
						"type", "item"
					).build(),
					new ServiceContext());
			}
		}
		else {
			_objectEntryService.addObjectEntry(
				0, objectDefinitionId,
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"r_dataSetToDataSetActions_l_dataSetId", dataSetId
				).put(
					"target", FDSEntryItemImportPolicy.GROUP_PROXY
				).put(
					"type", "item"
				).build(),
				new ServiceContext());
		}
	}

	private Serializable _getLocalizeableValue(
		String languageId, Object value) {

		if (value == null) {
			return null;
		}

		return HashMapBuilder.put(
			languageId, String.valueOf(value)
		).build();
	}

	private Serializable _getOptionalValue(Object value) {
		if (value == null) {
			return null;
		}

		return String.valueOf(value);
	}

	@Reference
	private FDSCreationMenuRegistry _fdsCreationMenuRegistry;

	@Reference
	private FDSItemsActionsRegistry _fdsItemsActionsRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private Portal _portal;

	@Reference
	private SystemFDSEntryRegistry _systemFDSEntryRegistry;

}