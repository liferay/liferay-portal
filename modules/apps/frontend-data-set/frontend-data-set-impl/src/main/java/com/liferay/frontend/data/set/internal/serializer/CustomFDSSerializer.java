/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.serializer;

import com.liferay.client.extension.type.FDSCellRendererCET;
import com.liferay.client.extension.type.FDSFilterCET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.frontend.data.set.FDSEntryItemImportPolicy;
import com.liferay.frontend.data.set.action.util.FDSActionUtil;
import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilterRegistry;
import com.liferay.frontend.data.set.internal.url.FDSAPIURLBuilder;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSSortItem;
import com.liferay.frontend.data.set.model.FDSSortItemBuilder;
import com.liferay.frontend.data.set.serializer.FDSSerializer;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
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
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Sanz
 */
@Component(
	property = "frontend.data.set.serializer.type=" + FDSSerializer.TYPE_CUSTOM,
	service = FDSSerializer.class
)
public class CustomFDSSerializer
	extends BaseFDSSerializer implements FDSSerializer {

	@Override
	public boolean isAvailable(
		String fdsName, HttpServletRequest httpServletRequest) {

		ObjectEntry objectEntry = _getObjectEntry(
			fdsName, _getObjectDefinition(httpServletRequest));

		if (objectEntry == null) {
			return false;
		}

		Map<String, Object> properties = objectEntry.getProperties();

		if (properties.isEmpty()) {
			return false;
		}

		return _isActive(objectEntry);
	}

	@Override
	public String serializeAdditionalAPIURLParameters(
		String fdsName, HttpServletRequest httpServletRequest,
		boolean interpolate, JSONObject tokenResolutionsJSONObject) {

		Map<String, Object> properties = getDataSetObjectEntryProperties(
			fdsName, httpServletRequest);

		return createFDSAPIURLBuilder(
			httpServletRequest,
			String.valueOf(properties.get("restApplication")),
			String.valueOf(properties.get("restEndpoint")),
			String.valueOf(properties.get("restSchema"))
		).addQueryString(
			String.valueOf(properties.get("additionalAPIURLParameters"))
		).setTokenResolutions(
			tokenResolutionsJSONObject
		).buildQueryString(
			interpolate
		);
	}

	@Override
	public String serializeAPIURL(
		String fdsName, HttpServletRequest httpServletRequest,
		boolean interpolate, JSONObject tokenResolutionsJSONObject) {

		Map<String, Object> properties = getDataSetObjectEntryProperties(
			fdsName, httpServletRequest);

		FDSAPIURLBuilder fdsAPIURLBuilder = createFDSAPIURLBuilder(
			httpServletRequest,
			String.valueOf(properties.get("restApplication")),
			String.valueOf(properties.get("restEndpoint")),
			String.valueOf(properties.get("restSchema"))
		).setTokenResolutions(
			tokenResolutionsJSONObject
		);

		List<ObjectEntry> objectEntries = getSortedRelatedObjectEntries(
			fdsName, httpServletRequest, (Predicate)null, "tableSectionsOrder",
			"dataSetToDataSetTableSections");

		if (objectEntries == null) {
			return fdsAPIURLBuilder.build(interpolate);
		}

		String nestedFields = StringPool.BLANK;
		int nestedFieldsDepth = 1;

		for (ObjectEntry objectEntry : objectEntries) {
			Map<String, Object> objectEntryProperties =
				objectEntry.getProperties();

			String[] fieldNames = StringUtil.split(
				StringUtil.replace(
					String.valueOf(objectEntryProperties.get("fieldName")),
					"[]", StringPool.PERIOD),
				CharPool.PERIOD);

			if (fieldNames.length > 1) {
				for (int i = 0; i < (fieldNames.length - 1); i++) {
					nestedFields = StringUtil.add(nestedFields, fieldNames[i]);
				}

				if (fieldNames.length > nestedFieldsDepth) {
					nestedFieldsDepth = fieldNames.length - 1;
				}
			}
		}

		if (nestedFields.equals(StringPool.BLANK)) {
			return fdsAPIURLBuilder.build(interpolate);
		}

		fdsAPIURLBuilder.addParameter(
			"nestedFields",
			StringUtil.replaceLast(
				nestedFields, CharPool.COMMA, StringPool.BLANK));

		if (nestedFieldsDepth > 1) {
			fdsAPIURLBuilder.addParameter(
				"nestedFieldsDepth", String.valueOf(nestedFieldsDepth));
		}

		return fdsAPIURLBuilder.build(interpolate);
	}

	@Override
	public List<FDSActionDropdownItem> serializeBulkActions(
		String fdsName, HttpServletRequest httpServletRequest) {

		// TODO

		return Collections.emptyList();
	}

	@Override
	public CreationMenu serializeCreationMenu(
		String fdsName, HttpServletRequest httpServletRequest) {

		CreationMenu creationMenu = new CreationMenu();

		CreationMenu systemCreationMenu =
			_systemFDSSerializer.serializeCreationMenu(
				fdsName, httpServletRequest);

		List<DropdownItem> systemDropdownItems =
			(List<DropdownItem>)systemCreationMenu.get("primaryItems");

		List<DropdownItem> customDropdownItems = TransformUtil.transform(
			getSortedRelatedObjectEntries(
				fdsName, httpServletRequest,
				(ObjectEntry objectEntry) ->
					Objects.equals(_getType(objectEntry), "creation") &&
					_isActive(objectEntry),
				"creationActionsOrder", "dataSetToDataSetActions"),
			objectEntry -> {
				Map<String, Object> properties = objectEntry.getProperties();

				FDSActionDropdownItem fdsActionDropdownItem =
					new FDSActionDropdownItem(
						String.valueOf(properties.get("url")),
						String.valueOf(properties.get("icon")),
						FDSActionUtil.getFDSCreationActionId(
							objectEntry.getExternalReferenceCode()),
						String.valueOf(properties.get("label")), null,
						String.valueOf(properties.get("permissionKey")),
						String.valueOf(properties.get("target")));

				fdsActionDropdownItem.putData(
					"disableHeader",
					(boolean)Validator.isNull(properties.get("title")));
				fdsActionDropdownItem.putData(
					"size", properties.get("modalSize"));
				fdsActionDropdownItem.putData("title", properties.get("title"));

				return fdsActionDropdownItem;
			});

		for (DropdownItem customDropdownItem : customDropdownItems) {
			if (Objects.equals(
					customDropdownItem.get("target"),
					FDSEntryItemImportPolicy.GROUP_PROXY.toString())) {

				for (DropdownItem systemDropdownItem : systemDropdownItems) {
					creationMenu.addPrimaryDropdownItem(systemDropdownItem);
				}
			}
			else if (Objects.equals(
						customDropdownItem.get("target"),
						FDSEntryItemImportPolicy.ITEM_PROXY.toString())) {

				for (DropdownItem systemDropdownItem : systemDropdownItems) {
					if (systemDropdownItem.hasSameDataId(customDropdownItem)) {
						creationMenu.addPrimaryDropdownItem(systemDropdownItem);

						break;
					}
				}
			}
			else {
				creationMenu.addPrimaryDropdownItem(customDropdownItem);
			}
		}

		return creationMenu;
	}

	@Override
	public JSONArray serializeFilters(
		List<FDSFilter> fdsFilters, String fdsName,
		HttpServletRequest httpServletRequest) {

		return serializeFilters(fdsName, httpServletRequest);
	}

	@Override
	public JSONArray serializeFilters(
		String fdsName, HttpServletRequest httpServletRequest) {

		try {
			return _serializeFilters(fdsName, httpServletRequest);
		}
		catch (Exception exception) {
			_log.error("Unable to serialize filters", exception);

			return _jsonFactory.createJSONArray();
		}
	}

	@Override
	public List<FDSActionDropdownItem> serializeItemsActions(
		String fdsName, HttpServletRequest httpServletRequest) {

		List<FDSActionDropdownItem> fdsActionDropdownItems = new ArrayList<>();

		List<FDSActionDropdownItem> systemFDSActionDropdownItems =
			_systemFDSSerializer.serializeItemsActions(
				fdsName, httpServletRequest);

		List<FDSActionDropdownItem> customFDSActionDropdownItems =
			TransformUtil.transform(
				getSortedRelatedObjectEntries(
					fdsName, httpServletRequest,
					(ObjectEntry objectEntry) ->
						Objects.equals(_getType(objectEntry), "item") &&
						_isActive(objectEntry),
					"itemActionsOrder", "dataSetToDataSetActions"),
				objectEntry -> {
					Map<String, Object> properties =
						objectEntry.getProperties();

					FDSActionDropdownItem fdsActionDropdownItem =
						new FDSActionDropdownItem(
							String.valueOf(
								properties.get("confirmationMessage")),
							String.valueOf(properties.get("url")),
							String.valueOf(properties.get("icon")),
							FDSActionUtil.getFDSItemActionId(
								objectEntry.getExternalReferenceCode()),
							String.valueOf(properties.get("label")),
							String.valueOf(properties.get("method")),
							String.valueOf(properties.get("permissionKey")),
							String.valueOf(properties.get("target")));

					fdsActionDropdownItem.putData(
						"disableHeader",
						(boolean)Validator.isNull(properties.get("title")));
					fdsActionDropdownItem.putData(
						"errorMessage", properties.get("errorMessage"));
					fdsActionDropdownItem.putData(
						"requestBody", properties.get("requestBody"));
					fdsActionDropdownItem.putData(
						"size", properties.get("modalSize"));
					fdsActionDropdownItem.putData(
						"status", properties.get("confirmationMessageType"));
					fdsActionDropdownItem.putData(
						"successMessage", properties.get("successMessage"));
					fdsActionDropdownItem.putData(
						"title", properties.get("title"));

					return fdsActionDropdownItem;
				});

		for (FDSActionDropdownItem customFDSActionDropdownItem :
				customFDSActionDropdownItems) {

			if (Objects.equals(
					customFDSActionDropdownItem.get("target"),
					FDSEntryItemImportPolicy.GROUP_PROXY.toString())) {

				fdsActionDropdownItems.addAll(systemFDSActionDropdownItems);
			}
			else if (Objects.equals(
						customFDSActionDropdownItem.get("target"),
						FDSEntryItemImportPolicy.ITEM_PROXY.toString())) {

				for (FDSActionDropdownItem systemFDSActionDropdownItem :
						systemFDSActionDropdownItems) {

					if (systemFDSActionDropdownItem.hasSameDataId(
							customFDSActionDropdownItem)) {

						fdsActionDropdownItems.add(systemFDSActionDropdownItem);

						break;
					}
				}
			}
			else {
				fdsActionDropdownItems.add(customFDSActionDropdownItem);
			}
		}

		return fdsActionDropdownItems;
	}

	@Override
	public JSONObject serializePagination(
		String fdsName, HttpServletRequest httpServletRequest) {

		Map<String, Object> properties = getDataSetObjectEntryProperties(
			fdsName, httpServletRequest);

		return JSONUtil.put(
			"deltas",
			() -> {
				String[] listOfItemsPerPage = StringUtil.split(
					String.valueOf(properties.get("listOfItemsPerPage")),
					StringPool.COMMA_AND_SPACE);

				if (ArrayUtil.isNotEmpty(listOfItemsPerPage)) {
					return JSONUtil.toJSONArray(
						listOfItemsPerPage,
						(String itemsPerPage) -> {
							if (GetterUtil.getInteger(itemsPerPage) < 1) {
								return null;
							}

							return JSONUtil.put(
								"label", GetterUtil.getInteger(itemsPerPage));
						});
				}

				return JSONUtil.toJSONArray(
					ListUtil.fromArray(
						PropsValues.SEARCH_CONTAINER_PAGE_DELTA_VALUES),
					itemsPerPage -> JSONUtil.put("label", itemsPerPage));
			}
		).put(
			"initialDelta",
			() -> {
				Integer defaultItemsPerPage = GetterUtil.getInteger(
					String.valueOf(properties.get("defaultItemsPerPage")));

				if (defaultItemsPerPage > 1) {
					return defaultItemsPerPage;
				}

				return PropsValues.SEARCH_CONTAINER_PAGE_DEFAULT_DELTA;
			}
		);
	}

	@Override
	public String serializePropsTransformer(
		String fdsName, HttpServletRequest httpServletRequest) {

		Map<String, Object> properties = getDataSetObjectEntryProperties(
			fdsName, httpServletRequest);

		return String.valueOf(properties.get("propsTransformer"));
	}

	@Override
	public List<FDSSortItem> serializeSorts(
		String fdsName, HttpServletRequest httpServletRequest) {

		List<FDSSortItem> fdsSortItems = new ArrayList<>();

		List<FDSSortItem> systemFDSSortItems =
			_systemFDSSerializer.serializeSorts(fdsName, httpServletRequest);

		List<FDSSortItem> customFDSSortItems = TransformUtil.transform(
			getSortedRelatedObjectEntries(
				fdsName, httpServletRequest,
				(ObjectEntry objectEntry) -> _isActive(objectEntry),
				"sortsOrder", "dataSetToDataSetSorts"),
			objectEntry -> {
				Map<String, Object> properties = objectEntry.getProperties();

				String label = (String)properties.get("label");

				if (Validator.isNull(label)) {
					Map<String, String> labelI18n =
						(Map<String, String>)properties.get("label_i18n");

					label = labelI18n.get(
						LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()));
				}

				return FDSSortItemBuilder.setActive(
					Boolean.valueOf(String.valueOf(properties.get("default")))
				).setDirection(
					String.valueOf(properties.get("orderType"))
				).setKey(
					String.valueOf(properties.get("fieldName"))
				).setLabel(
					label
				).build();
			});

		for (FDSSortItem customFDSSortItem : customFDSSortItems) {
			if (Objects.equals(
					customFDSSortItem.get("direction"),
					FDSEntryItemImportPolicy.GROUP_PROXY.toString())) {

				fdsSortItems.addAll(
					_systemFDSSerializer.serializeSorts(
						fdsName, httpServletRequest));
			}
			else if (Objects.equals(
						customFDSSortItem.get("direction"),
						FDSEntryItemImportPolicy.ITEM_PROXY.toString())) {

				for (FDSSortItem systemFDSSortItem : systemFDSSortItems) {
					if (Objects.equals(
							systemFDSSortItem.get("key"),
							customFDSSortItem.get("key"))) {

						fdsSortItems.add(systemFDSSortItem);

						break;
					}
				}
			}
			else {
				fdsSortItems.add(customFDSSortItem);
			}
		}

		return fdsSortItems;
	}

	@Override
	public JSONArray serializeViews(
		String fdsName, HttpServletRequest httpServletRequest) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		Map<String, Object> dataSetObjectEntryProperties =
			getDataSetObjectEntryProperties(fdsName, httpServletRequest);

		String defaultVisualizationMode = String.valueOf(
			dataSetObjectEntryProperties.get("defaultVisualizationMode"));

		jsonArray.put(
			() -> {
				List<ObjectEntry> objectEntries = getRelatedObjectEntries(
					fdsName, httpServletRequest, (Predicate)null,
					"dataSetToDataSetCardsSections");

				if (objectEntries.isEmpty()) {
					return null;
				}

				return JSONUtil.put(
					"contentRenderer", "cards"
				).put(
					"default", defaultVisualizationMode.equals("cards")
				).put(
					"label", LanguageUtil.get(httpServletRequest, "cards")
				).put(
					"name", "cards"
				).put(
					"schema", _serializeViewSchema(objectEntries)
				).put(
					"thumbnail", "cards2"
				);
			}
		).put(
			() -> {
				List<ObjectEntry> objectEntries = getRelatedObjectEntries(
					fdsName, httpServletRequest, (Predicate)null,
					"dataSetToDataSetListSections");

				if (objectEntries.isEmpty()) {
					return null;
				}

				return JSONUtil.put(
					"contentRenderer", "list"
				).put(
					"default", defaultVisualizationMode.equals("list")
				).put(
					"label", LanguageUtil.get(httpServletRequest, "list")
				).put(
					"name", "list"
				).put(
					"schema", _serializeViewSchema(objectEntries)
				).put(
					"thumbnail", "list"
				);
			}
		).put(
			() -> {
				List<ObjectEntry> objectEntries = getSortedRelatedObjectEntries(
					fdsName, httpServletRequest, (Predicate)null,
					"tableSectionsOrder", "dataSetToDataSetTableSections");

				if (objectEntries.isEmpty()) {
					return null;
				}

				JSONArray fieldsJSONArray = JSONUtil.toJSONArray(
					objectEntries,
					(ObjectEntry objectEntry) -> {
						Map<String, Object> properties =
							objectEntry.getProperties();

						JSONObject jsonObject = JSONUtil.put(
							"contentRenderer",
							String.valueOf(properties.get("renderer"))
						).put(
							"fieldName",
							String.valueOf(properties.get("fieldName"))
						).put(
							"label",
							MapUtil.getWithFallbackKey(
								properties, "label", "fieldName")
						).put(
							"sortable", (boolean)properties.get("sortable")
						);

						String rendererType = String.valueOf(
							properties.get("rendererType"));

						if (!Objects.equals(rendererType, "clientExtension")) {
							return jsonObject;
						}

						String externalReferenceCode = String.valueOf(
							properties.get("renderer"));

						FDSCellRendererCET fdsCellRendererCET =
							(FDSCellRendererCET)cetManager.getCET(
								PortalUtil.getCompanyId(httpServletRequest),
								externalReferenceCode);

						if (fdsCellRendererCET == null) {
							if (_log.isWarnEnabled()) {
								_log.warn(
									"No frontend data set cell renderer " +
										"client extension type found for " +
											externalReferenceCode);
							}

							return jsonObject.put(
								"contentRenderer", "default"
							).put(
								"contentRendererClientExtension", false
							);
						}

						return jsonObject.put(
							"contentRendererClientExtension", true
						).put(
							"contentRendererModuleURL",
							"default from " + fdsCellRendererCET.getURL()
						);
					});

				return JSONUtil.put(
					"contentRenderer", "table"
				).put(
					"default", defaultVisualizationMode.equals("table")
				).put(
					"label", LanguageUtil.get(httpServletRequest, "table")
				).put(
					"name", "table"
				).put(
					"schema", JSONUtil.put("fields", fieldsJSONArray)
				).put(
					"thumbnail", "table"
				);
			}
		);

		return jsonArray;
	}

	protected Map<String, Object> getDataSetObjectEntryProperties(
		String externalReferenceCode, HttpServletRequest httpServletRequest) {

		ObjectEntry objectEntry = _getObjectEntry(
			externalReferenceCode, _getObjectDefinition(httpServletRequest));

		if (objectEntry != null) {
			return objectEntry.getProperties();
		}

		return Collections.emptyMap();
	}

	protected List<ObjectEntry> getRelatedObjectEntries(
		String externalReferenceCode, HttpServletRequest httpServletRequest,
		Predicate<ObjectEntry> predicate, String... relationshipNames) {

		List<ObjectEntry> objectEntries = new ArrayList<>();

		ObjectDefinition objectDefinition = _getObjectDefinition(
			httpServletRequest);

		ObjectEntry objectEntry = _getObjectEntry(
			externalReferenceCode, objectDefinition);

		for (String relationshipName : relationshipNames) {
			objectEntries.addAll(
				_getRelatedObjectEntries(
					objectDefinition, objectEntry, predicate,
					relationshipName));
		}

		return objectEntries;
	}

	protected List<ObjectEntry> getSortedRelatedObjectEntries(
		String externalReferenceCode, HttpServletRequest httpServletRequest,
		Predicate<ObjectEntry> predicate, String propertyKey,
		String... relationshipNames) {

		ObjectEntry objectEntry = _getObjectEntry(
			externalReferenceCode, _getObjectDefinition(httpServletRequest));

		List<ObjectEntry> objectEntries = getRelatedObjectEntries(
			externalReferenceCode, httpServletRequest, predicate,
			relationshipNames);

		objectEntries.sort(
			new ObjectEntryComparator(
				ListUtil.toList(
					ListUtil.fromString(
						MapUtil.getString(
							objectEntry.getProperties(), propertyKey),
						StringPool.COMMA),
					GetterUtil::getLong)));

		return objectEntries;
	}

	@Reference
	protected CETManager cetManager;

	@Reference
	protected FDSFilterRegistry fdsFilterRegistry;

	private JSONObject _getDateJSONObject(Object object) {
		if (object == null) {
			return null;
		}

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(Date.from(Instant.parse(String.valueOf(object))));

		return JSONUtil.put(
			"day", calendar.get(Calendar.DATE)
		).put(
			"month", calendar.get(Calendar.MONTH) + 1
		).put(
			"year", calendar.get(Calendar.YEAR)
		);
	}

	private ObjectDefinition _getObjectDefinition(
		HttpServletRequest httpServletRequest) {

		return _objectDefinitionLocalService.fetchObjectDefinition(
			PortalUtil.getCompanyId(httpServletRequest), "DataSet");
	}

	private ObjectEntry _getObjectEntry(
		String externalReferenceCode, ObjectDefinition objectDefinition) {

		ObjectEntry objectEntry = null;

		DTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				false, null, null, null, null,
				LocaleUtil.getMostRelevantLocale(), null, null);
		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					objectDefinition.getStorageType()));

		ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);

		try {
			objectEntry = defaultObjectEntryManager.getObjectEntry(
				objectDefinition.getCompanyId(), dtoConverterContext,
				externalReferenceCode, objectDefinition, null);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get data set object entry with external " +
						"reference code " + externalReferenceCode,
					exception);
			}
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(false);
		}

		return objectEntry;
	}

	private Collection<ObjectEntry> _getRelatedObjectEntries(
		ObjectDefinition objectDefinition, ObjectEntry objectEntry,
		Predicate<ObjectEntry> predicate, String relationshipName) {

		Collection<ObjectEntry> objectEntries = null;

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					objectDefinition.getStorageType()));

		ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);

		try {
			Page<ObjectEntry> relatedObjectEntriesPage =
				defaultObjectEntryManager.getRelatedObjectEntries(
					new DefaultDTOConverterContext(
						false, null, null, null, null,
						LocaleUtil.getMostRelevantLocale(), null, null),
					objectEntry.getId(),
					_objectRelationshipLocalService.getObjectRelationship(
						objectDefinition.getObjectDefinitionId(),
						relationshipName),
					Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS));

			objectEntries = relatedObjectEntriesPage.getItems();

			if (predicate != null) {
				objectEntries.removeIf(
					predicateObjectEntry -> !predicate.test(
						predicateObjectEntry));
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get related object entries for " +
						relationshipName,
					exception);
			}
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(false);
		}

		return objectEntries;
	}

	private String _getType(ObjectEntry objectEntry) {
		Map<String, Object> properties = objectEntry.getProperties();

		return GetterUtil.getString(properties.get("type"));
	}

	private Boolean _isActive(ObjectEntry objectEntry) {
		Map<String, Object> properties = objectEntry.getProperties();

		return (Boolean)properties.get("active");
	}

	private Boolean _isCollection(String fieldName, String sourceType) {
		return fieldName.contains(StringPool.OPEN_BRACKET) &&
			   Objects.equals(sourceType, "OBJECT_PICKLIST");
	}

	private JSONObject _serializeFilter(
			HttpServletRequest httpServletRequest, ObjectEntry objectEntry)
		throws Exception {

		Map<String, Object> properties = objectEntry.getProperties();

		String fieldName = String.valueOf(properties.get("fieldName"));

		fieldName = fieldName.replaceAll(
			"(\\[\\]|\\.)", StringPool.FORWARD_SLASH);

		String clientExtensionEntryERC = MapUtil.getString(
			properties, "clientExtensionEntryERC");

		if (Validator.isNotNull(clientExtensionEntryERC)) {
			return _serializeFilterClientExtension(
				clientExtensionEntryERC, fieldName, httpServletRequest,
				properties);
		}

		String type = MapUtil.getString(properties, "type");

		if (Objects.equals(type, "date") || Objects.equals(type, "date-time")) {
			return _serializeFilterDateOrDateTime(fieldName, properties, type);
		}

		String sourceType = MapUtil.getString(properties, "sourceType");

		if (Validator.isNotNull(sourceType)) {
			return _serializeFilterSelection(
				fieldName, httpServletRequest, properties, sourceType);
		}

		return null;
	}

	private JSONObject _serializeFilterClientExtension(
		String clientExtensionEntryERC, String fieldName,
		HttpServletRequest httpServletRequest, Map<String, Object> properties) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		FDSFilterCET fdsFilterCET = (FDSFilterCET)cetManager.getCET(
			themeDisplay.getCompanyId(), clientExtensionEntryERC);

		if (fdsFilterCET == null) {
			_log.error(
				"No frontend data set filter client extension exists with " +
					"the external reference code " + clientExtensionEntryERC);

			return null;
		}

		return JSONUtil.put(
			"clientExtensionFilterURL", fdsFilterCET.getURL()
		).put(
			"entityFieldType", FDSEntityFieldTypes.STRING
		).put(
			"id", fieldName
		).put(
			"label",
			MapUtil.getWithFallbackKey(properties, "label", "fieldName")
		).put(
			"type", "clientExtension"
		);
	}

	private JSONObject _serializeFilterDateOrDateTime(
			String fieldName, Map<String, Object> properties, String type)
		throws Exception {

		JSONObject fromJSONObject = _getDateJSONObject(properties.get("from"));
		JSONObject toJSONObject = _getDateJSONObject(properties.get("to"));

		boolean active = (fromJSONObject != null) || (toJSONObject != null);

		return JSONUtil.put(
			"active", active
		).put(
			"entityFieldType",
			Objects.equals(type, "date") ? FDSEntityFieldTypes.DATE :
				FDSEntityFieldTypes.DATE_TIME
		).put(
			"id", fieldName
		).put(
			"label",
			MapUtil.getWithFallbackKey(properties, "label", "fieldName")
		).put(
			"preloadedData",
			() -> {
				if (!active) {
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

	private JSONArray _serializeFilters(
			String fdsName, HttpServletRequest httpServletRequest)
		throws Exception {

		return JSONUtil.toJSONArray(
			getSortedRelatedObjectEntries(
				fdsName, httpServletRequest,
				(ObjectEntry objectEntry) -> _isActive(objectEntry),
				"filtersOrder", "dataSetToDataSetClientExtensionFilters",
				"dataSetToDataSetDateFilters",
				"dataSetToDataSetSelectionFilters"),
			(ObjectEntry objectEntry) -> _serializeFilter(
				httpServletRequest, objectEntry));
	}

	private JSONObject _serializeFilterSelection(
			String fieldName, HttpServletRequest httpServletRequest,
			Map<String, Object> properties, String sourceType)
		throws Exception {

		if (Objects.equals(
				sourceType, FDSEntryItemImportPolicy.ITEM_PROXY.toString())) {

			JSONArray jsonArray = _systemFDSSerializer.serializeFilters(
				MapUtil.getString(
					properties,
					"r_dataSetToDataSetSelectionFilters_l_dataSetERC"),
				httpServletRequest);

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);

				if (Objects.equals(fieldName, jsonObject.getString("id"))) {
					return jsonObject;
				}
			}
		}

		JSONObject jsonObject = JSONUtil.put(
			"autocompleteEnabled", true
		).put(
			"entityFieldType",
			() -> {
				if (_isCollection(
						String.valueOf(properties.get("fieldName")),
						sourceType)) {

					return FDSEntityFieldTypes.COLLECTION;
				}

				return FDSEntityFieldTypes.STRING;
			}
		).put(
			"id",
			() -> {
				if (!Objects.equals(sourceType, "OBJECT_PICKLIST")) {
					return fieldName;
				}

				int index = fieldName.lastIndexOf(StringPool.FORWARD_SLASH);

				if (index <= 0) {
					return fieldName;
				}

				return fieldName.substring(0, index);
			}
		).put(
			"label",
			MapUtil.getWithFallbackKey(properties, "label", "fieldName")
		).put(
			"multiple", properties.get("multiple")
		).put(
			"type", "selection"
		);

		String source = MapUtil.getString(properties, "source");

		if (Objects.equals(sourceType, "API_REST_APPLICATION")) {
			return jsonObject.put(
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
							MapUtil.getString(properties, "preselectedValues"));

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

		if (!Objects.equals(sourceType, "OBJECT_PICKLIST")) {
			return null;
		}

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.
				getListTypeDefinitionByExternalReferenceCode(
					source, PortalUtil.getCompanyId(httpServletRequest));

		List<ListTypeEntry> listTypeEntries =
			_listTypeEntryLocalService.getListTypeEntries(
				listTypeDefinition.getListTypeDefinitionId());

		return jsonObject.put(
			"items",
			JSONUtil.toJSONArray(
				listTypeEntries,
				listTypeEntry -> JSONUtil.put(
					"key", listTypeEntry.getKey()
				).put(
					"label",
					listTypeEntry.getName(
						PortalUtil.getLocale(httpServletRequest))
				).put(
					"value", listTypeEntry.getKey()
				))
		).put(
			"preloadedData",
			() -> {
				JSONArray selectedItemsJSONArray =
					_jsonFactory.createJSONArray();

				JSONArray preselectedValuesJSONArray =
					_jsonFactory.createJSONArray(
						MapUtil.getString(properties, "preselectedValues"));

				for (int i = 0; i < preselectedValuesJSONArray.length(); i++) {
					JSONObject preselectedValueJSONObject =
						preselectedValuesJSONArray.getJSONObject(i);

					for (ListTypeEntry listTypeEntry : listTypeEntries) {
						if (!Objects.equals(
								listTypeEntry.getExternalReferenceCode(),
								preselectedValueJSONObject.getString(
									"value"))) {

							continue;
						}

						selectedItemsJSONArray.put(
							JSONUtil.put(
								"label",
								listTypeEntry.getName(
									PortalUtil.getLocale(httpServletRequest))
							).put(
								"value", listTypeEntry.getKey()
							));
					}
				}

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

	private JSONObject _serializeViewSchema(
			Collection<ObjectEntry> objectEntries)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		for (ObjectEntry objectEntry : objectEntries) {
			Map<String, Object> properties = objectEntry.getProperties();

			jsonObject.put(
				String.valueOf(properties.get("name")),
				String.valueOf(properties.get("fieldName")));
		}

		return jsonObject;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CustomFDSSerializer.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference(
		target = "(frontend.data.set.serializer.type=" + FDSSerializer.TYPE_SYSTEM + ")"
	)
	private FDSSerializer _systemFDSSerializer;

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