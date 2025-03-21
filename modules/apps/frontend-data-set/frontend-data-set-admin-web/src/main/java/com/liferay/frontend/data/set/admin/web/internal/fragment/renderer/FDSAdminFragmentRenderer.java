/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.admin.web.internal.fragment.renderer;

import com.liferay.client.extension.type.FDSCellRendererCET;
import com.liferay.client.extension.type.FDSFilterCET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.renderer.FDSRenderer;
import com.liferay.frontend.data.set.url.FDSAPIURLResolver;
import com.liferay.frontend.data.set.url.FDSAPIURLResolverRegistry;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import java.time.Instant;

import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Sanz
 * @author Marko Cikos
 */
@Component(service = FragmentRenderer.class)
public class FDSAdminFragmentRenderer implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "content-display";
	}

	@Override
	public String getConfiguration(
		FragmentRendererContext fragmentRendererContext) {

		return JSONUtil.put(
			"fieldSets",
			JSONUtil.putAll(
				JSONUtil.put(
					"fields",
					JSONUtil.putAll(
						JSONUtil.put(
							"label", "data-set-view"
						).put(
							"name", "itemSelector"
						).put(
							"type", "itemSelector"
						).put(
							"typeOptions", JSONUtil.put("itemType", "FDSView")
						))))
		).toString();
	}

	@Override
	public String getIcon() {
		return "table";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "data-set");
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		if (!FeatureFlagManagerUtil.isEnabled("LPS-164563")) {
			return false;
		}

		return true;
	}

	@Override
	public void render(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);

			PrintWriter printWriter = httpServletResponse.getWriter();

			FragmentEntryLink fragmentEntryLink =
				fragmentRendererContext.getFragmentEntryLink();

			JSONObject jsonObject =
				(JSONObject)_fragmentEntryConfigurationParser.getFieldValue(
					getConfiguration(fragmentRendererContext),
					fragmentEntryLink.getEditableValues(),
					fragmentRendererContext.getLocale(), "itemSelector");

			String externalReferenceCode = jsonObject.getString(
				"externalReferenceCode");

			ObjectEntry dataSetObjectEntry = null;

			ObjectDefinition dataSetObjectDefinition =
				_dataSetObjectDefinitionLocalService.fetchObjectDefinition(
					fragmentEntryLink.getCompanyId(), "DataSet");

			if (Validator.isNotNull(externalReferenceCode)) {
				try {
					dataSetObjectEntry = _getObjectEntry(
						fragmentEntryLink.getCompanyId(), externalReferenceCode,
						dataSetObjectDefinition);
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to get frontend data set view with " +
								"external reference code " +
									externalReferenceCode,
							exception);
					}
				}
			}

			if ((dataSetObjectEntry == null) &&
				fragmentRendererContext.isEditMode()) {

				printWriter.write(
					StringBundler.concat(
						"<div class=\"portlet-msg-info\">",
						_language.get(
							httpServletRequest, "select-a-data-set-view"),
						"</div>"));
			}

			if (dataSetObjectEntry == null) {
				return;
			}

			if (FeatureFlagManagerUtil.isEnabled("LPD-37531")) {
				_fdsRenderer.render(
					HashMapBuilder.<String, Object>put(
						"namespace",
						fragmentRendererContext.getFragmentElementId()
					).put(
						"style", "fluid"
					).build(),
					fragmentRendererContext.getFragmentElementId(),
					externalReferenceCode, httpServletRequest,
					httpServletResponse, true, null, printWriter);
			}
			else {
				printWriter.write(
					_buildFragmentHTML(
						dataSetObjectDefinition, dataSetObjectEntry,
						fragmentRendererContext, httpServletRequest));
			}
		}
		catch (Exception exception) {
			_log.error("Unable to render frontend data set view", exception);

			throw new IOException(exception);
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(false);
		}
	}

	private String _buildFragmentHTML(
			ObjectDefinition dataSetObjectDefinition,
			ObjectEntry dataSetObjectEntry,
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest)
		throws Exception {

		StringBundler sb = new StringBundler(5);

		sb.append("<div id=\"");
		sb.append(fragmentRendererContext.getFragmentElementId());
		sb.append("\" >");

		ComponentDescriptor componentDescriptor = new ComponentDescriptor(
			"{FrontendDataSet} from frontend-data-set-web",
			fragmentRendererContext.getFragmentElementId(), null, true);

		Writer writer = new CharArrayWriter();

		FragmentEntryLink fragmentEntryLink =
			fragmentRendererContext.getFragmentEntryLink();

		Map<String, Object> dataSetObjectEntryProperties =
			dataSetObjectEntry.getProperties();

		Set<ObjectEntry> dataSetTableSectionObjectEntries =
			_getDataSetTableSectionObjectEntries(
				dataSetObjectDefinition, dataSetObjectEntry);

		_reactRenderer.renderReact(
			componentDescriptor,
			HashMapBuilder.<String, Object>put(
				"additionalAPIURLParameters",
				dataSetObjectEntry.getPropertyValue(
					"additionalAPIURLParameters")
			).put(
				"apiURL",
				_getAPIURL(
					dataSetObjectEntry, dataSetTableSectionObjectEntries,
					httpServletRequest)
			).put(
				"creationMenu",
				_getCreationMenuJSONObject(
					dataSetObjectDefinition, dataSetObjectEntry)
			).put(
				"filters",
				_getFiltersJSONArray(
					dataSetObjectDefinition, dataSetObjectEntry,
					httpServletRequest)
			).put(
				"id", "FDS_" + fragmentRendererContext.getFragmentElementId()
			).put(
				"itemsActions",
				_getItemsActionsJSONArray(
					dataSetObjectDefinition, dataSetObjectEntry)
			).put(
				"namespace", fragmentRendererContext.getFragmentElementId()
			).put(
				"pagination", _getPaginationJSONObject(dataSetObjectEntry)
			).put(
				"sorts",
				_getSortsJSONArray(dataSetObjectDefinition, dataSetObjectEntry)
			).put(
				"style", "fluid"
			).put(
				"views",
				_getFDSViewsJSONArray(
					fragmentEntryLink.getCompanyId(),
					_getRelatedObjectEntries(
						dataSetObjectDefinition, dataSetObjectEntry, null,
						"dataSetToDataSetCardsSections"),
					String.valueOf(
						dataSetObjectEntryProperties.get(
							"defaultVisualizationMode")),
					dataSetTableSectionObjectEntries,
					_getRelatedObjectEntries(
						dataSetObjectDefinition, dataSetObjectEntry, null,
						"dataSetToDataSetListSections"),
					httpServletRequest)
			).build(),
			httpServletRequest, writer);

		sb.append(writer.toString());

		sb.append("</div>");

		return sb.toString();
	}

	private String _getAPIURL(
			ObjectEntry dataSetObjectEntry,
			Set<ObjectEntry> dataSetTableSectionObjectEntries,
			HttpServletRequest httpServletRequest)
		throws Exception {

		StringBundler sb = new StringBundler(3);

		sb.append("/o");

		Map<String, Object> properties = dataSetObjectEntry.getProperties();

		String restApplication = String.valueOf(
			properties.get("restApplication"));

		sb.append(
			StringUtil.replaceLast(restApplication, "/v1.0", StringPool.BLANK));

		sb.append(String.valueOf(properties.get("restEndpoint")));

		return _resolveParameters(
			_interpolateURL(
				_getNestedFields(
					sb.toString(), dataSetTableSectionObjectEntries),
				httpServletRequest),
			restApplication, String.valueOf(properties.get("restSchema")),
			httpServletRequest);
	}

	private JSONObject _getCreationMenuJSONObject(
			ObjectDefinition dataSetObjectDefinition,
			ObjectEntry dataSetObjectEntry)
		throws Exception {

		return JSONUtil.put(
			"primaryItems",
			JSONUtil.toJSONArray(
				_getSortedRelatedObjectEntries(
					dataSetObjectDefinition, dataSetObjectEntry,
					"creationActionsOrder",
					(ObjectEntry objectEntry) ->
						Objects.equals(_getType(objectEntry), "creation") &&
						_isActive(objectEntry),
					"dataSetToDataSetActions"),
				(ObjectEntry objectEntry) -> {
					Map<String, Object> properties =
						objectEntry.getProperties();

					return JSONUtil.put(
						"data",
						JSONUtil.put(
							"disableHeader",
							(boolean)Validator.isNull(properties.get("title"))
						).put(
							"permissionKey", properties.get("permissionKey")
						).put(
							"size", properties.get("modalSize")
						).put(
							"title", properties.get("title")
						)
					).put(
						"href", properties.get("url")
					).put(
						"icon", properties.get("icon")
					).put(
						"label", properties.get("label")
					).put(
						"target", properties.get("target")
					);
				}));
	}

	private Set<ObjectEntry> _getDataSetTableSectionObjectEntries(
			ObjectDefinition dataSetObjectDefinition,
			ObjectEntry dataSetObjectEntry)
		throws Exception {

		return _getSortedRelatedObjectEntries(
			dataSetObjectDefinition, dataSetObjectEntry, "tableSectionsOrder",
			(Predicate)null, "dataSetToDataSetTableSections");
	}

	private JSONObject _getDateJSONObject(Object isoDate) {
		if (isoDate == null) {
			return null;
		}

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(Date.from(Instant.parse(String.valueOf(isoDate))));

		return JSONUtil.put(
			"day", calendar.get(Calendar.DATE)
		).put(
			"month", calendar.get(Calendar.MONTH) + 1
		).put(
			"year", calendar.get(Calendar.YEAR)
		);
	}

	private JSONObject _getFDSCardsViewJSONObject(
			Collection<ObjectEntry> dataSetCardsSectionObjectEntries,
			String defaultVisualizationMode,
			HttpServletRequest httpServletRequest)
		throws Exception {

		return JSONUtil.put(
			"contentRenderer", "cards"
		).put(
			"default", defaultVisualizationMode.equals("cards")
		).put(
			"label", _language.get(httpServletRequest, "cards")
		).put(
			"name", "cards"
		).put(
			"schema", _getViewSchemaJSONObject(dataSetCardsSectionObjectEntries)
		).put(
			"thumbnail", "cards2"
		);
	}

	private JSONObject _getFDSListViewJSONObject(
			String defaultVisualizationMode,
			Collection<ObjectEntry> dataSetListSectionObjectEntries,
			HttpServletRequest httpServletRequest)
		throws Exception {

		return JSONUtil.put(
			"contentRenderer", "list"
		).put(
			"default", defaultVisualizationMode.equals("list")
		).put(
			"label", _language.get(httpServletRequest, "list")
		).put(
			"name", "list"
		).put(
			"schema", _getViewSchemaJSONObject(dataSetListSectionObjectEntries)
		).put(
			"thumbnail", "list"
		);
	}

	private JSONObject _getFDSTableViewJSONObject(
			long companyId, String defaultVisualizationMode,
			Set<ObjectEntry> dataSetTableSectionObjectEntries,
			HttpServletRequest httpServletRequest)
		throws Exception {

		return JSONUtil.put(
			"contentRenderer", "table"
		).put(
			"default", defaultVisualizationMode.equals("table")
		).put(
			"label", _language.get(httpServletRequest, "table")
		).put(
			"name", "table"
		).put(
			"schema",
			JSONUtil.put(
				"fields",
				_getFieldsJSONArray(
					companyId, dataSetTableSectionObjectEntries))
		).put(
			"thumbnail", "table"
		);
	}

	private JSONArray _getFDSViewsJSONArray(
			long companyId,
			Collection<ObjectEntry> dataSetCardsSectionObjectEntries,
			String defaultVisualizationMode,
			Set<ObjectEntry> dataSetTableSectionObjectEntries,
			Collection<ObjectEntry> dataSetListSectionObjectEntries,
			HttpServletRequest httpServletRequest)
		throws Exception {

		JSONArray viewsJSONArray = _jsonFactory.createJSONArray();

		if (!dataSetCardsSectionObjectEntries.isEmpty()) {
			viewsJSONArray.put(
				_getFDSCardsViewJSONObject(
					dataSetCardsSectionObjectEntries, defaultVisualizationMode,
					httpServletRequest));
		}

		if (!dataSetListSectionObjectEntries.isEmpty()) {
			viewsJSONArray.put(
				_getFDSListViewJSONObject(
					defaultVisualizationMode, dataSetListSectionObjectEntries,
					httpServletRequest));
		}

		if (!dataSetTableSectionObjectEntries.isEmpty()) {
			viewsJSONArray.put(
				_getFDSTableViewJSONObject(
					companyId, defaultVisualizationMode,
					dataSetTableSectionObjectEntries, httpServletRequest));
		}

		return viewsJSONArray;
	}

	private JSONArray _getFieldsJSONArray(
			long companyId, Set<ObjectEntry> dataSetTableSectionObjectEntries)
		throws Exception {

		return JSONUtil.toJSONArray(
			dataSetTableSectionObjectEntries,
			(ObjectEntry objectEntry) -> {
				Map<String, Object> properties = objectEntry.getProperties();

				JSONObject jsonObject = JSONUtil.put(
					"contentRenderer",
					String.valueOf(properties.get("renderer"))
				).put(
					"fieldName", String.valueOf(properties.get("fieldName"))
				).put(
					"label", _getLabelValue("label", "fieldName", properties)
				).put(
					"sortable", (boolean)properties.get("sortable")
				);

				String rendererType = String.valueOf(
					properties.get("rendererType"));

				if (!Objects.equals(rendererType, "clientExtension")) {
					return jsonObject;
				}

				FDSCellRendererCET fdsCellRendererCET =
					(FDSCellRendererCET)_cetManager.getCET(
						companyId, String.valueOf(properties.get("renderer")));

				return jsonObject.put(
					"contentRendererClientExtension", true
				).put(
					"contentRendererModuleURL",
					"default from " + fdsCellRendererCET.getURL()
				);
			});
	}

	private JSONArray _getFiltersJSONArray(
			ObjectDefinition dataSetObjectDefinition,
			ObjectEntry dataSetObjectEntry,
			HttpServletRequest httpServletRequest)
		throws Exception {

		return JSONUtil.toJSONArray(
			_getSortedRelatedObjectEntries(
				dataSetObjectDefinition, dataSetObjectEntry, "filtersOrder",
				(ObjectEntry objectEntry) -> _isActive(objectEntry),
				"dataSetToDataSetClientExtensionFilters",
				"dataSetToDataSetDateFilters",
				"dataSetToDataSetSelectionFilters"),
			(ObjectEntry objectEntry) -> {
				Map<String, Object> properties = objectEntry.getProperties();

				String fieldName = String.valueOf(properties.get("fieldName"));

				fieldName = fieldName.replaceAll(
					"(\\[\\]|\\.)", StringPool.FORWARD_SLASH);

				String type = MapUtil.getString(properties, "type");

				if (Objects.equals(type, "date") ||
					Objects.equals(type, "date-time")) {

					JSONObject fromJSONObject = _getDateJSONObject(
						properties.get("from"));
					JSONObject toJSONObject = _getDateJSONObject(
						properties.get("to"));

					boolean hasPreloadedData =
						(fromJSONObject != null) || (toJSONObject != null);

					return JSONUtil.put(
						"active", hasPreloadedData
					).put(
						"entityFieldType",
						Objects.equals(type, "date") ?
							FDSEntityFieldTypes.DATE :
								FDSEntityFieldTypes.DATE_TIME
					).put(
						"id", fieldName
					).put(
						"label",
						_getLabelValue("label", "fieldName", properties)
					).put(
						"preloadedData",
						() -> {
							if (!hasPreloadedData) {
								return null;
							}

							return JSONUtil.put(
								"from", fromJSONObject
							).put(
								"to", toJSONObject
							);
						}
					).put(
						"type", "dateRange"
					);
				}

				String source = MapUtil.getString(properties, "source");

				if (Validator.isNotNull(source)) {
					String finalFieldName = fieldName;
					String sourceType = MapUtil.getString(
						properties, "sourceType");

					JSONObject selectionFilterJSONObject = JSONUtil.put(
						"autocompleteEnabled", true
					).put(
						"entityFieldType", FDSEntityFieldTypes.STRING
					).put(
						"id",
						() -> {
							if (Objects.equals(
									sourceType, "API_REST_APPLICATION")) {

								return finalFieldName;
							}

							int index = finalFieldName.lastIndexOf(
								StringPool.FORWARD_SLASH);

							if (index <= 0) {
								return finalFieldName;
							}

							return finalFieldName.substring(0, index);
						}
					).put(
						"label",
						_getLabelValue("label", "fieldName", properties)
					).put(
						"multiple", properties.get("multiple")
					).put(
						"type", "selection"
					);

					if (Validator.isNotNull(sourceType) &&
						Objects.equals(sourceType, "API_REST_APPLICATION")) {

						return selectionFilterJSONObject.put(
							"apiURL", source
						).put(
							"itemKey", properties.get("itemKey")
						).put(
							"itemLabel", properties.get("itemLabel")
						).put(
							"preloadedData",
							() -> {
								JSONArray selectedItemsJSONArray =
									_jsonFactory.createJSONArray(
										MapUtil.getString(
											properties, "preselectedValues"));

								if (JSONUtil.isEmpty(selectedItemsJSONArray)) {
									return null;
								}

								return JSONUtil.put(
									"exclude",
									() -> Boolean.FALSE.equals(
										(Boolean)properties.get("include"))
								).put(
									"selectedItems", selectedItemsJSONArray
								);
							}
						);
					}

					ThemeDisplay themeDisplay =
						(ThemeDisplay)httpServletRequest.getAttribute(
							WebKeys.THEME_DISPLAY);

					ListTypeDefinition listTypeDefinition =
						_listTypeDefinitionLocalService.
							getListTypeDefinitionByExternalReferenceCode(
								source, themeDisplay.getCompanyId());

					List<ListTypeEntry> listTypeEntries =
						_listTypeEntryLocalService.getListTypeEntries(
							listTypeDefinition.getListTypeDefinitionId());

					return selectionFilterJSONObject.put(
						"items",
						JSONUtil.toJSONArray(
							listTypeEntries,
							listTypeEntry -> JSONUtil.put(
								"key", listTypeEntry.getKey()
							).put(
								"label",
								listTypeEntry.getName(themeDisplay.getLocale())
							).put(
								"value", listTypeEntry.getKey()
							))
					).put(
						"preloadedData",
						() -> {
							JSONArray selectedItemsJSONArray =
								_getSelectedItemsJSONArray(
									listTypeEntries, themeDisplay.getLocale(),
									MapUtil.getString(
										properties, "preselectedValues"));

							if (JSONUtil.isEmpty(selectedItemsJSONArray)) {
								return null;
							}

							return JSONUtil.put(
								"exclude",
								() -> Boolean.FALSE.equals(
									(Boolean)properties.get("include"))
							).put(
								"selectedItems", selectedItemsJSONArray
							);
						}
					);
				}

				String clientExtensionEntryERC = MapUtil.getString(
					properties, "clientExtensionEntryERC");

				if (Validator.isNull(clientExtensionEntryERC)) {
					return null;
				}

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				FDSFilterCET fdsFilterCET = (FDSFilterCET)_cetManager.getCET(
					themeDisplay.getCompanyId(), clientExtensionEntryERC);

				if (fdsFilterCET == null) {
					_log.error(
						StringBundler.concat(
							"No frontend data set filter client extension ",
							"exists with the external reference code ",
							clientExtensionEntryERC));

					return null;
				}

				return JSONUtil.put(
					"clientExtensionFilterURL", fdsFilterCET.getURL()
				).put(
					"entityFieldType", FDSEntityFieldTypes.STRING
				).put(
					"id", fieldName
				).put(
					"label", _getLabelValue("label", "fieldName", properties)
				).put(
					"type", "clientExtension"
				);
			});
	}

	private JSONArray _getItemsActionsJSONArray(
			ObjectDefinition dataSetObjectDefinition,
			ObjectEntry dataSetObjectEntry)
		throws Exception {

		return JSONUtil.toJSONArray(
			_getSortedRelatedObjectEntries(
				dataSetObjectDefinition, dataSetObjectEntry, "itemActionsOrder",
				(ObjectEntry objectEntry) ->
					Objects.equals(_getType(objectEntry), "item") &&
					_isActive(objectEntry),
				"dataSetToDataSetActions"),
			(ObjectEntry objectEntry) -> {
				Map<String, Object> properties = objectEntry.getProperties();

				return JSONUtil.put(
					"data",
					JSONUtil.put(
						"confirmationMessage",
						properties.get("confirmationMessage")
					).put(
						"disableHeader",
						(boolean)Validator.isNull(properties.get("title"))
					).put(
						"errorMessage", properties.get("errorMessage")
					).put(
						"method", properties.get("method")
					).put(
						"permissionKey", properties.get("permissionKey")
					).put(
						"requestBody", properties.get("requestBody")
					).put(
						"size", properties.get("modalSize")
					).put(
						"status", properties.get("confirmationMessageType")
					).put(
						"successMessage", properties.get("successMessage")
					).put(
						"title", properties.get("title")
					)
				).put(
					"href", properties.get("url")
				).put(
					"icon", properties.get("icon")
				).put(
					"label", properties.get("label")
				).put(
					"target", properties.get("target")
				);
			});
	}

	private String _getLabelValue(
		String defaultKey, String fallbackKey,
		Map<String, Object> dataSetTableSectionProperties) {

		String value = String.valueOf(
			dataSetTableSectionProperties.get(defaultKey));

		if (Validator.isNotNull(value)) {
			return value;
		}

		return String.valueOf(dataSetTableSectionProperties.get(fallbackKey));
	}

	private String _getNestedFields(
			String apiURL, Set<ObjectEntry> dataSetTableSectionObjectEntries)
		throws Exception {

		if (dataSetTableSectionObjectEntries == null) {
			return apiURL;
		}

		String nestedFields = StringPool.BLANK;
		int nestedFieldsDepth = 1;

		for (ObjectEntry fdsFieldObjectEntry :
				dataSetTableSectionObjectEntries) {

			Map<String, Object> properties =
				fdsFieldObjectEntry.getProperties();

			String[] fieldNameList = StringUtil.split(
				StringUtil.replace(
					String.valueOf(properties.get("fieldName")), "[]",
					StringPool.PERIOD),
				CharPool.PERIOD);

			if (fieldNameList.length > 1) {
				String[] fieldsName = new String[fieldNameList.length - 1];

				System.arraycopy(
					fieldNameList, 0, fieldsName, 0, fieldNameList.length - 1);

				for (String fieldName : fieldsName) {
					nestedFields = StringUtil.add(nestedFields, fieldName);
				}

				if (fieldNameList.length > nestedFieldsDepth) {
					nestedFieldsDepth = fieldNameList.length - 1;
				}
			}
		}

		if (nestedFields.equals(StringPool.BLANK)) {
			return apiURL;
		}

		StringBundler sb = new StringBundler(5);

		sb.append(apiURL);
		sb.append("?nestedFields=");
		sb.append(
			StringUtil.replaceLast(
				nestedFields, CharPool.COMMA, StringPool.BLANK));

		if (nestedFieldsDepth > 1) {
			sb.append("&nestedFieldsDepth=");
			sb.append(nestedFieldsDepth);
		}

		return sb.toString();
	}

	private ObjectEntry _getObjectEntry(
			long companyId, String externalReferenceCode,
			ObjectDefinition dataSetObjectDefinition)
		throws Exception {

		DTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				false, null, null, null, null,
				LocaleUtil.getMostRelevantLocale(), null, null);

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_dataSetObjectEntryManagerRegistry.getObjectEntryManager(
					dataSetObjectDefinition.getStorageType()));

		return defaultObjectEntryManager.getObjectEntry(
			companyId, dtoConverterContext, externalReferenceCode,
			dataSetObjectDefinition, null);
	}

	private JSONObject _getPaginationJSONObject(ObjectEntry objectEntry)
		throws Exception {

		Map<String, Object> properties = objectEntry.getProperties();

		return JSONUtil.put(
			"deltas",
			JSONUtil.toJSONArray(
				StringUtil.split(
					String.valueOf(properties.get("listOfItemsPerPage")),
					StringPool.COMMA_AND_SPACE),
				(String itemPerPage) -> JSONUtil.put(
					"label", GetterUtil.getInteger(itemPerPage)))
		).put(
			"initialDelta",
			String.valueOf(properties.get("defaultItemsPerPage"))
		);
	}

	private Collection<ObjectEntry> _getRelatedObjectEntries(
			ObjectDefinition dataSetObjectDefinition,
			ObjectEntry dataSetObjectEntry, Predicate<ObjectEntry> predicate,
			String relationshipName)
		throws Exception {

		DTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				false, null, null, null, null,
				LocaleUtil.getMostRelevantLocale(), null, null);

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_dataSetObjectEntryManagerRegistry.getObjectEntryManager(
					dataSetObjectDefinition.getStorageType()));

		Page<ObjectEntry> relatedObjectEntriesPage =
			defaultObjectEntryManager.getObjectEntryRelatedObjectEntries(
				dtoConverterContext, dataSetObjectDefinition,
				dataSetObjectEntry.getId(), relationshipName,
				Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS));

		Collection<ObjectEntry> objectEntries =
			relatedObjectEntriesPage.getItems();

		if (predicate != null) {
			objectEntries.removeIf(objectEntry -> !predicate.test(objectEntry));
		}

		return objectEntries;
	}

	private JSONArray _getSelectedItemsJSONArray(
			List<ListTypeEntry> listTypeEntries, Locale locale,
			String preselectedValues)
		throws JSONException {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		JSONArray preselectedValuesJSONArray = _jsonFactory.createJSONArray(
			preselectedValues);

		for (int i = 0; i < preselectedValuesJSONArray.length(); i++) {
			JSONObject jsonObject = preselectedValuesJSONArray.getJSONObject(i);

			for (ListTypeEntry listTypeEntry : listTypeEntries) {
				if (Objects.equals(
						listTypeEntry.getExternalReferenceCode(),
						jsonObject.getString("value"))) {

					jsonArray.put(
						JSONUtil.put(
							"label", listTypeEntry.getName(locale)
						).put(
							"value", listTypeEntry.getKey()
						));

					break;
				}
			}
		}

		return jsonArray;
	}

	private Set<ObjectEntry> _getSortedRelatedObjectEntries(
			ObjectDefinition dataSetObjectDefinition,
			ObjectEntry dataSetObjectEntry,
			String dataSetObjectEntryComparatorIdsPropertyKey,
			Predicate<ObjectEntry> predicate, String... relationshipNames)
		throws Exception {

		Set<ObjectEntry> objectEntries = new TreeSet<>(
			new ObjectEntryComparator(
				ListUtil.toList(
					ListUtil.fromString(
						MapUtil.getString(
							dataSetObjectEntry.getProperties(),
							dataSetObjectEntryComparatorIdsPropertyKey),
						StringPool.COMMA),
					Long::parseLong)));

		for (String relationshipName : relationshipNames) {
			objectEntries.addAll(
				_getRelatedObjectEntries(
					dataSetObjectDefinition, dataSetObjectEntry, predicate,
					relationshipName));
		}

		return objectEntries;
	}

	private JSONArray _getSortsJSONArray(
			ObjectDefinition dataSetObjectDefinition,
			ObjectEntry dataSetObjectEntry)
		throws Exception {

		return JSONUtil.toJSONArray(
			_getSortedRelatedObjectEntries(
				dataSetObjectDefinition, dataSetObjectEntry, "sortsOrder",
				(ObjectEntry objectEntry) -> _isActive(objectEntry),
				"dataSetToDataSetSorts"),
			(ObjectEntry objectEntry) -> {
				Map<String, Object> properties = objectEntry.getProperties();

				String label = (String)properties.get("label");

				if (Validator.isNull(label)) {
					Map<String, String> labelI18n =
						(Map<String, String>)properties.get("label_i18n");

					label = labelI18n.get(
						LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()));
				}

				return JSONUtil.put(
					"active", properties.get("default")
				).put(
					"default", properties.get("default")
				).put(
					"direction", properties.get("orderType")
				).put(
					"key", properties.get("fieldName")
				).put(
					"label", label
				);
			});
	}

	private String _getType(ObjectEntry objectEntry) {
		Map<String, Object> properties = objectEntry.getProperties();

		return GetterUtil.getString(properties.get("type"));
	}

	private JSONObject _getViewSchemaJSONObject(
			Collection<ObjectEntry> fdsViewObjectEntries)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		for (ObjectEntry dataSetObjectEntry : fdsViewObjectEntries) {
			Map<String, Object> properties = dataSetObjectEntry.getProperties();

			jsonObject.put(
				String.valueOf(properties.get("name")),
				String.valueOf(properties.get("fieldName")));
		}

		return jsonObject;
	}

	private String _interpolateURL(
		String apiURL, HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		apiURL = StringUtil.replace(
			apiURL, "{siteId}", String.valueOf(themeDisplay.getScopeGroupId()));
		apiURL = StringUtil.replace(
			apiURL, "{scopeKey}",
			String.valueOf(themeDisplay.getScopeGroupId()));
		apiURL = StringUtil.replace(
			apiURL, "{userId}", String.valueOf(themeDisplay.getUserId()));

		if (StringUtil.contains(apiURL, "{") && _log.isWarnEnabled()) {
			_log.warn("Unsupported parameter in API URL: " + apiURL);
		}

		return apiURL;
	}

	private Boolean _isActive(ObjectEntry objectEntry) {
		if (!FeatureFlagManagerUtil.isEnabled("LPD-37531")) {
			return true;
		}

		Map<String, Object> properties = objectEntry.getProperties();

		return (Boolean)properties.get("active");
	}

	private String _resolveParameters(
		String apiURL, String restApplication, String restSchema,
		HttpServletRequest httpServletRequest) {

		FDSAPIURLResolver fdsAPIURLResolver =
			_fdsAPIURLResolverRegistry.getFDSAPIURLResolver(
				restApplication, restSchema);

		if (fdsAPIURLResolver != null) {
			try {
				return fdsAPIURLResolver.resolve(apiURL, httpServletRequest);
			}
			catch (PortalException portalException) {
				_log.error(portalException);

				return apiURL;
			}
		}

		return apiURL;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FDSAdminFragmentRenderer.class);

	@Reference
	private CETManager _cetManager;

	@Reference
	private ObjectDefinitionLocalService _dataSetObjectDefinitionLocalService;

	@Reference
	private ObjectEntryManagerRegistry _dataSetObjectEntryManagerRegistry;

	@Reference
	private FDSAPIURLResolverRegistry _fdsAPIURLResolverRegistry;

	@Reference
	private FDSRenderer _fdsRenderer;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private ReactRenderer _reactRenderer;

	private static class ObjectEntryComparator
		implements Comparator<ObjectEntry> {

		public ObjectEntryComparator(List<Long> ids) {
			_ids = ids;
		}

		@Override
		public int compare(
			ObjectEntry dataSetObjectEntry1, ObjectEntry dataSetObjectEntry2) {

			long id1 = dataSetObjectEntry1.getId();
			long id2 = dataSetObjectEntry2.getId();

			int index1 = _ids.indexOf(id1);
			int index2 = _ids.indexOf(id2);

			if ((index1 == -1) && (index2 == -1)) {
				Date date = dataSetObjectEntry1.getDateCreated();

				return date.compareTo(dataSetObjectEntry2.getDateCreated());
			}

			if (index1 == -1) {
				return 1;
			}

			if (index2 == -1) {
				return -1;
			}

			return Long.compare(index1, index2);
		}

		private final List<Long> _ids;

	}

}