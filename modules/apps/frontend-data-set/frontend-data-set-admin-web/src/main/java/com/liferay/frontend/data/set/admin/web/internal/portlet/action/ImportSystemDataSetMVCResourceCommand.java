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
import com.liferay.frontend.data.set.action.util.FDSActionUtil;
import com.liferay.frontend.data.set.admin.web.internal.constants.FDSAdminPortletKeys;
import com.liferay.frontend.data.set.filter.BaseClientExtensionFDSFilter;
import com.liferay.frontend.data.set.filter.BaseDateRangeFDSFilter;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilterRegistry;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSSortItem;
import com.liferay.frontend.data.set.sort.FDSSorts;
import com.liferay.frontend.data.set.sort.FDSSortsRegistry;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.FDSViewRegistry;
import com.liferay.frontend.data.set.view.cards.BaseCardsFDSView;
import com.liferay.frontend.data.set.view.list.BaseListFDSView;
import com.liferay.frontend.data.set.view.table.BaseTableFDSView;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaField;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

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
				dataSetActionObjectDefinition.getDefaultLanguageId(),
				fdsCreationMenu, _portal.getHttpServletRequest(resourceRequest),
				dataSetActionObjectDefinition.getObjectDefinitionId(),
				objectEntry);
		}

		_addFDSFiltersObjectEntries(
			_fdsFilterRegistry.getFDSFilters(fdsName),
			_portal.getHttpServletRequest(resourceRequest), objectEntry);

		FDSItemsActions fdsItemsActions =
			_fdsItemsActionsRegistry.getFDSItemsActions(fdsName);

		if (fdsItemsActions != null) {
			_addFDSItemsActionsObjectEntries(
				dataSetActionObjectDefinition.getDefaultLanguageId(),
				fdsItemsActions, _portal.getHttpServletRequest(resourceRequest),
				dataSetActionObjectDefinition.getObjectDefinitionId(),
				objectEntry);
		}

		FDSSorts fdsSorts = _fdsSortsRegistry.getFDSSorts(fdsName);

		if (fdsSorts != null) {
			_addFDSSortsObjectEntries(
				fdsSorts, _portal.getHttpServletRequest(resourceRequest),
				objectEntry);
		}

		for (FDSView fdsView : _fdsViewRegistry.getFDSViews(fdsName)) {
			if (fdsView instanceof BaseCardsFDSView) {
				_addBaseCardsFDSViewObjectEntries(
					(BaseCardsFDSView)fdsView,
					_portal.getHttpServletRequest(resourceRequest),
					objectEntry);
			}

			if (fdsView instanceof BaseListFDSView) {
				_addBaseListFDSViewObjectEntries(
					(BaseListFDSView)fdsView,
					_portal.getHttpServletRequest(resourceRequest),
					objectEntry);
			}

			if (fdsView instanceof BaseTableFDSView) {
				_addBaseTableFDSViewObjectEntries(
					(BaseTableFDSView)fdsView,
					_portal.getHttpServletRequest(resourceRequest),
					objectEntry);
			}
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, objectEntry);
	}

	private void _addBaseCardsFDSViewObjectEntries(
			BaseCardsFDSView baseCardsFDSView,
			HttpServletRequest httpServletRequest, ObjectEntry objectEntry)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_CARDS_SECTION",
					_portal.getCompanyId(httpServletRequest));

		Map<String, String> map = HashMapBuilder.put(
			"description", baseCardsFDSView.getDescription()
		).put(
			"image", baseCardsFDSView.getImage()
		).put(
			"symbol", baseCardsFDSView.getSymbol()
		).put(
			"title", baseCardsFDSView.getTitle()
		).build();

		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (Validator.isNull(entry.getValue())) {
				continue;
			}

			_objectEntryService.addObjectEntry(
				0, objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"fieldName", entry.getValue()
				).put(
					"name", entry.getKey()
				).put(
					"r_dataSetToDataSetCardsSections_l_dataSetId",
					objectEntry.getObjectEntryId()
				).build(),
				new ServiceContext());
		}

		if (baseCardsFDSView.isDefault()) {
			Map<String, Serializable> values = objectEntry.getValues();

			values.put("defaultVisualizationMode", "cards");

			_objectEntryService.updateObjectEntry(
				objectEntry.getObjectEntryId(), values, new ServiceContext());
		}
	}

	private void _addBaseListFDSViewObjectEntries(
			BaseListFDSView baseListFDSView,
			HttpServletRequest httpServletRequest, ObjectEntry objectEntry)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_LIST_SECTION",
					_portal.getCompanyId(httpServletRequest));

		Map<String, String> map = HashMapBuilder.put(
			"description", baseListFDSView.getDescription()
		).put(
			"image", baseListFDSView.getImage()
		).put(
			"symbol", baseListFDSView.getSymbol()
		).put(
			"title", baseListFDSView.getTitle()
		).build();

		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (Validator.isNull(entry.getValue())) {
				continue;
			}

			_objectEntryService.addObjectEntry(
				0, objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"fieldName", entry.getValue()
				).put(
					"name", entry.getKey()
				).put(
					"r_dataSetToDataSetListSections_l_dataSetId",
					objectEntry.getObjectEntryId()
				).build(),
				new ServiceContext());
		}

		if (baseListFDSView.isDefault()) {
			Map<String, Serializable> values = objectEntry.getValues();

			values.put("defaultVisualizationMode", "list");

			_objectEntryService.updateObjectEntry(
				objectEntry.getObjectEntryId(), values, new ServiceContext());
		}
	}

	private void _addBaseTableFDSViewObjectEntries(
			BaseTableFDSView baseTableFDSView,
			HttpServletRequest httpServletRequest, ObjectEntry objectEntry)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_TABLE_SECTION",
					_portal.getCompanyId(httpServletRequest));

		FDSTableSchema fdsTableSchema = baseTableFDSView.getFDSTableSchema(
			_portal.getLocale(httpServletRequest));

		Map<String, FDSTableSchemaField> map =
			fdsTableSchema.getFDSTableSchemaFieldsMap();

		for (FDSTableSchemaField fdsTableSchemaField : map.values()) {
			_objectEntryService.addObjectEntry(
				0, objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"externalReferenceCode",
					StringBundler.concat(
						objectEntry.getExternalReferenceCode(), "_",
						fdsTableSchemaField.getFieldName())
				).put(
					"fieldName",
					StringUtil.removeLast(
						fdsTableSchemaField.getFieldName(), ".LANG")
				).put(
					"label_i18n",
					() -> {
						String label = fdsTableSchemaField.getLabel();

						if (fdsTableSchemaField.isLocalizeLabel()) {
							return _getI18nMap(label);
						}

						return HashMapBuilder.put(
							objectDefinition.getDefaultLanguageId(), label
						).put(
							LocaleUtil.toLanguageId(
								_portal.getLocale(httpServletRequest)),
							label
						).build();
					}
				).put(
					"r_dataSetToDataSetTableSections_l_dataSetId",
					objectEntry.getObjectEntryId()
				).put(
					"renderer",
					() -> {
						if (fdsTableSchemaField.
								isContentRendererClientExtension()) {

							return null;
						}

						String contentRenderer =
							fdsTableSchemaField.getContentRenderer();

						if (Validator.isNotNull(contentRenderer)) {
							return contentRenderer;
						}

						return "default";
					}
				).put(
					"rendererType",
					() -> {
						if (fdsTableSchemaField.
								isContentRendererClientExtension()) {

							return "clientExtension";
						}

						return "internal";
					}
				).put(
					"sortable", fdsTableSchemaField.isSortable()
				).put(
					"type", "string"
				).build(),
				new ServiceContext());
		}

		if (baseTableFDSView.isDefault()) {
			Map<String, Serializable> values = objectEntry.getValues();

			values.put("defaultVisualizationMode", "table");

			_objectEntryService.updateObjectEntry(
				objectEntry.getObjectEntryId(), values, new ServiceContext());
		}
	}

	private void _addFDSCreationMenuObjectEntries(
			String defaultLanguageId, FDSCreationMenu fdsCreationMenu,
			HttpServletRequest httpServletRequest, long objectDefinitionId,
			ObjectEntry objectEntry)
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
						"r_dataSetToDataSetActions_l_dataSetId",
						objectEntry.getObjectEntryId()
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
					FDSActionUtil.getFDSCreationActionExternalReferenceCode(
						objectEntry.getExternalReferenceCode(),
						String.valueOf(id)),
					0, objectDefinitionId,
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
						"r_dataSetToDataSetActions_l_dataSetId",
						objectEntry.getObjectEntryId()
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
					"r_dataSetToDataSetActions_l_dataSetId",
					objectEntry.getObjectEntryId()
				).put(
					"target", FDSEntryItemImportPolicy.GROUP_PROXY
				).put(
					"type", "creation"
				).build(),
				new ServiceContext());
		}
	}

	private void _addFDSFiltersObjectEntries(
			List<FDSFilter> fdsFilters, HttpServletRequest httpServletRequest,
			ObjectEntry objectEntry)
		throws Exception {

		for (FDSFilter fdsFilter : fdsFilters) {
			Map<String, Serializable> values =
				HashMapBuilder.<String, Serializable>put(
					"entityFieldType", fdsFilter.getEntityFieldType()
				).put(
					"fieldName", fdsFilter.getId()
				).put(
					"label_i18n", _getI18nMap(fdsFilter.getLabel())
				).put(
					"type", fdsFilter.getType()
				).build();

			String externalReferenceCode = StringPool.BLANK;

			if (fdsFilter instanceof BaseClientExtensionFDSFilter) {
				externalReferenceCode = "L_DATA_SET_CLIENT_EXTENSION_FILTER";

				BaseClientExtensionFDSFilter clientExtensionFDSFilter =
					(BaseClientExtensionFDSFilter)fdsFilter;

				values.put(
					"clientExtensionEntryERC",
					clientExtensionFDSFilter.getCETExternalReferenceCode());

				values.put(
					"r_dataSetToDataSetClientExtensionFilters_l_dataSetId",
					objectEntry.getObjectEntryId());
			}
			else if (fdsFilter instanceof BaseDateRangeFDSFilter) {
				externalReferenceCode = "L_DATA_SET_DATE_FILTER";

				JSONObject jsonObject = JSONUtil.put(
					"preloadedData", fdsFilter.getPreloadedData());

				values.put("from", jsonObject.getString("from"));

				values.put(
					"r_dataSetToDataSetDateFilters_l_dataSetId",
					objectEntry.getObjectEntryId());
				values.put("to", jsonObject.getString("to"));
				values.put("type", fdsFilter.getEntityFieldType());
			}
			else if (fdsFilter instanceof BaseSelectionFDSFilter) {
				externalReferenceCode = "L_DATA_SET_SELECTION_FILTER";

				values.put("sourceType", FDSEntryItemImportPolicy.ITEM_PROXY);
				values.put(
					"r_dataSetToDataSetSelectionFilters_l_dataSetId",
					objectEntry.getObjectEntryId());
			}
			else {
				throw new IllegalArgumentException(
					StringBundler.concat(
						"FDSFilter is not an instance of ",
						"BaseClientExtensionFDSFilter, ",
						"BaseDateRangeFDSFilter, or BaseSelectionFDSFilter"));
			}

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.
					getObjectDefinitionByExternalReferenceCode(
						externalReferenceCode,
						_portal.getCompanyId(httpServletRequest));

			_objectEntryService.addObjectEntry(
				0, objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null, values, new ServiceContext());
		}
	}

	private void _addFDSItemsActionsObjectEntries(
			String defaultLanguageId, FDSItemsActions fdsItemsActions,
			HttpServletRequest httpServletRequest, long objectDefinitionId,
			ObjectEntry objectEntry)
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

				Object id = data.get("id");

				if (id == null) {
					continue;
				}

				_objectEntryService.addOrUpdateObjectEntry(
					FDSActionUtil.getFDSItemActionExternalReferenceCode(
						objectEntry.getExternalReferenceCode(),
						String.valueOf(id)),
					0, objectDefinitionId,
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
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
						"r_dataSetToDataSetActions_l_dataSetId",
						objectEntry.getObjectEntryId()
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
					FDSActionUtil.getFDSItemActionExternalReferenceCode(
						objectEntry.getExternalReferenceCode(),
						String.valueOf(id)),
					0, objectDefinitionId,
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
						"r_dataSetToDataSetActions_l_dataSetId",
						objectEntry.getObjectEntryId()
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
					"r_dataSetToDataSetActions_l_dataSetId",
					objectEntry.getObjectEntryId()
				).put(
					"target", FDSEntryItemImportPolicy.GROUP_PROXY
				).put(
					"type", "item"
				).build(),
				new ServiceContext());
		}
	}

	private void _addFDSSortsObjectEntries(
			FDSSorts fdsSorts, HttpServletRequest httpServletRequest,
			ObjectEntry objectEntry)
		throws Exception {

		List<FDSSortItem> fdsSortItems = fdsSorts.getFDSSortItems(
			httpServletRequest);

		if (ListUtil.isEmpty(fdsSortItems)) {
			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_SORT",
					_portal.getCompanyId(httpServletRequest));

		if (fdsSorts.getFDSEntryItemImportPolicy() ==
				FDSEntryItemImportPolicy.GROUP_PROXY) {

			_objectEntryService.addObjectEntry(
				0, objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"default", false
				).put(
					"fieldName", "*"
				).put(
					"label_i18n",
					() -> _getLocalizeableValue(
						objectDefinition.getDefaultLanguageId(), "*")
				).put(
					"orderType", FDSEntryItemImportPolicy.GROUP_PROXY
				).put(
					"r_dataSetToDataSetSorts_l_dataSetId",
					objectEntry.getObjectEntryId()
				).build(),
				new ServiceContext());

			return;
		}

		for (FDSSortItem fdsSortItem : fdsSortItems) {
			_objectEntryService.addObjectEntry(
				0, objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"default",
					() -> _getOptionalValue(fdsSortItem.get("active"))
				).put(
					"fieldName", () -> _getOptionalValue(fdsSortItem.get("key"))
				).put(
					"label_i18n",
					() -> _getLocalizeableValue(
						objectDefinition.getDefaultLanguageId(),
						_getOptionalValue(fdsSortItem.get("label")))
				).put(
					"orderType",
					() -> {
						if (fdsSorts.getFDSEntryItemImportPolicy() ==
								FDSEntryItemImportPolicy.DETACHED) {

							return _getOptionalValue(
								fdsSortItem.get("direction"));
						}

						return FDSEntryItemImportPolicy.ITEM_PROXY;
					}
				).put(
					"r_dataSetToDataSetSorts_l_dataSetId",
					objectEntry.getObjectEntryId()
				).build(),
				new ServiceContext());
		}
	}

	private HashMap<String, String> _getI18nMap(String key) {
		HashMap<String, String> labels = new HashMap<>();

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", locale, getClass());

			String label = LanguageUtil.get(resourceBundle, key);

			if (Validator.isNull(label)) {
				label = StringPool.BLANK;
			}

			labels.put(LocaleUtil.toLanguageId(locale), label);
		}

		return labels;
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
	private FDSFilterRegistry _fdsFilterRegistry;

	@Reference
	private FDSItemsActionsRegistry _fdsItemsActionsRegistry;

	@Reference
	private FDSSortsRegistry _fdsSortsRegistry;

	@Reference
	private FDSViewRegistry _fdsViewRegistry;

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