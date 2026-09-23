/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.frontend.data.set.provider;

import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorChannelField;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;
import com.liferay.site.pim.site.initializer.constants.PIMObjectFolderConstants;
import com.liferay.site.pim.site.initializer.internal.constants.PIMFDSNames;
import com.liferay.site.pim.site.initializer.internal.frontend.data.set.model.PIMConnectorChannelFieldDisplay;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
@Component(
	property = "fds.data.provider.key=" + PIMFDSNames.FIELD_MAPPINGS,
	service = FDSDataProvider.class
)
public class PIMConnectorChannelFieldFDSDataProvider
	implements FDSDataProvider<PIMConnectorChannelFieldDisplay> {

	@Override
	public List<PIMConnectorChannelFieldDisplay> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		return ListUtil.subList(
			_sort(
				_getPIMConnectorChannelFieldDisplays(
					fdsKeywords, httpServletRequest),
				sort),
			fdsPagination.getStartPosition(), fdsPagination.getEndPosition());
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		List<PIMConnectorChannelField> pimConnectorChannelFields =
			_getPIMConnectorChannelFields(fdsKeywords, httpServletRequest);

		return pimConnectorChannelFields.size();
	}

	private Map<String, List<Map<String, Serializable>>>
		_getFieldMappingValuesMap(List<ObjectEntry> objectEntries) {

		Map<String, List<Map<String, Serializable>>> fieldMappingValuesMap =
			new HashMap<>();

		for (ObjectEntry objectEntry : objectEntries) {
			Map<String, Serializable> values = objectEntry.getValues();

			String channelFieldName = MapUtil.getString(
				values, "channelFieldName");

			List<Map<String, Serializable>> fieldMappingValues =
				fieldMappingValuesMap.computeIfAbsent(
					channelFieldName, key -> new ArrayList<>());

			fieldMappingValues.add(values);
		}

		Comparator<Map<String, Serializable>> comparator =
			Comparator.comparingInt(
				values -> GetterUtil.getInteger(values.get("priority")));

		for (List<Map<String, Serializable>> fieldMappingValues :
				fieldMappingValuesMap.values()) {

			ListUtil.sort(fieldMappingValues, comparator);
		}

		return fieldMappingValuesMap;
	}

	private Map<String, List<Map<String, Serializable>>>
			_getFieldMappingValuesMap(ObjectEntry objectEntry)
		throws PortalException {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				fetchObjectRelationshipByExternalReferenceCode(
					"L_PIM_CONNECTOR_TO_PIM_CONNECTOR_FIELD_MAPPINGS",
					objectEntry.getObjectDefinitionId());

		if (objectRelationship == null) {
			return Collections.emptyMap();
		}

		return _getFieldMappingValuesMap(
			_objectEntryLocalService.getOneToManyObjectEntries(
				objectEntry.getGroupId(),
				objectRelationship.getObjectRelationshipId(), null, false,
				objectEntry.getObjectEntryId(), true, null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null));
	}

	private String _getLabel(
		PIMConnectorChannelField pimConnectorChannelField) {

		String label = pimConnectorChannelField.getLabel();

		if (pimConnectorChannelField.isMultiple()) {
			return label + "[]";
		}

		return label;
	}

	private Map<String, String> _getObjectFieldLabels(
		long companyId, Locale locale) {

		Map<String, String> objectFieldLabels = new HashMap<>();

		ObjectFolder objectFolder =
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				PIMObjectFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES,
				companyId);

		if (objectFolder == null) {
			return objectFieldLabels;
		}

		for (ObjectDefinition objectDefinition :
				_objectDefinitionLocalService.getObjectFolderObjectDefinitions(
					objectFolder.getObjectFolderId())) {

			for (ObjectField objectField :
					_objectFieldLocalService.getObjectFields(
						objectDefinition.getObjectDefinitionId())) {

				objectFieldLabels.put(
					objectField.getName(), objectField.getLabel(locale));
			}
		}

		return objectFieldLabels;
	}

	private List<PIMConnectorChannelFieldDisplay>
			_getPIMConnectorChannelFieldDisplays(
				FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		List<PIMConnectorChannelField> pimConnectorChannelFields =
			_getPIMConnectorChannelFields(fdsKeywords, httpServletRequest);

		if (pimConnectorChannelFields.isEmpty()) {
			return Collections.emptyList();
		}

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			ParamUtil.getLong(httpServletRequest, "objectEntryId"));

		if (objectEntry == null) {
			return Collections.emptyList();
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Locale locale = themeDisplay.getLocale();

		Map<String, List<Map<String, Serializable>>> fieldMappingValuesMap =
			_getFieldMappingValuesMap(objectEntry);

		Map<String, String> objectFieldLabels = _getObjectFieldLabels(
			themeDisplay.getCompanyId(), locale);

		String editFieldMappingURL = ParamUtil.getString(
			httpServletRequest, "editFieldMappingURL");

		return TransformUtil.transform(
			pimConnectorChannelFields,
			pimConnectorChannelField -> new PIMConnectorChannelFieldDisplay(
				_getLabel(pimConnectorChannelField),
				editFieldMappingURL + "&channelField=" +
					URLCodec.encodeURL(pimConnectorChannelField.getName()),
				locale, pimConnectorChannelField.isRequired(),
				_getSourceAttributes(
					fieldMappingValuesMap.get(
						pimConnectorChannelField.getName()),
					objectFieldLabels)));
	}

	private List<PIMConnectorChannelField> _getPIMConnectorChannelFields(
		FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest) {

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			ParamUtil.getLong(httpServletRequest, "objectEntryId"));

		if (objectEntry == null) {
			return Collections.emptyList();
		}

		PIMConnector pimConnector = _pimConnectorRegistry.getPIMConnector(
			MapUtil.getString(objectEntry.getValues(), "key"));

		if (pimConnector == null) {
			return Collections.emptyList();
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String keywords = StringUtil.toLowerCase(fdsKeywords.getKeywords());

		return TransformUtil.transform(
			pimConnector.getPIMConnectorChannelFields(themeDisplay.getLocale()),
			pimConnectorChannelField -> {
				if (Validator.isNull(keywords)) {
					return pimConnectorChannelField;
				}

				String lowerCaseLabel = StringUtil.toLowerCase(
					_getLabel(pimConnectorChannelField));

				if (!lowerCaseLabel.contains(keywords)) {
					return null;
				}

				return pimConnectorChannelField;
			});
	}

	private List<String> _getSourceAttributes(
		List<Map<String, Serializable>> fieldMappingValues,
		Map<String, String> objectFieldLabels) {

		return TransformUtil.transform(
			fieldMappingValues,
			values -> {
				if (Objects.equals(
						MapUtil.getString(values, "type"), _TYPE_FIXED_VALUE)) {

					String value = MapUtil.getString(values, "value");

					if (Validator.isNull(value)) {
						return null;
					}

					return value;
				}

				String sourceFieldName = MapUtil.getString(
					values, "sourceFieldName");

				if (Validator.isNull(sourceFieldName)) {
					return null;
				}

				return objectFieldLabels.getOrDefault(
					sourceFieldName, sourceFieldName);
			});
	}

	private List<PIMConnectorChannelFieldDisplay> _sort(
		List<PIMConnectorChannelFieldDisplay> pimConnectorChannelFieldDisplays,
		Sort sort) {

		if (sort == null) {
			return pimConnectorChannelFieldDisplays;
		}

		String fieldName = sort.getFieldName();

		if (Validator.isNull(fieldName)) {
			return pimConnectorChannelFieldDisplays;
		}

		Comparator<PIMConnectorChannelFieldDisplay> comparator = null;

		if (Objects.equals(fieldName, "channelField")) {
			comparator = Comparator.comparing(
				PIMConnectorChannelFieldDisplay::getChannelField,
				String.CASE_INSENSITIVE_ORDER);
		}
		else if (Objects.equals(fieldName, "required")) {
			comparator = Comparator.comparing(
				PIMConnectorChannelFieldDisplay::isRequired);
		}
		else if (Objects.equals(fieldName, "sourceAttributes")) {
			comparator = Comparator.comparing(
				(PIMConnectorChannelFieldDisplay
					pimConnectorChannelFieldDisplay) -> {

					List<String> sourceAttributes =
						pimConnectorChannelFieldDisplay.getSourceAttributes();

					if (sourceAttributes.isEmpty()) {
						return StringPool.BLANK;
					}

					return sourceAttributes.get(0);
				},
				String.CASE_INSENSITIVE_ORDER);
		}
		else if (Objects.equals(fieldName, "status")) {
			comparator = Comparator.comparing(
				PIMConnectorChannelFieldDisplay::isMapped);
		}
		else {
			return pimConnectorChannelFieldDisplays;
		}

		if (sort.isReverse()) {
			comparator = comparator.reversed();
		}

		return ListUtil.sort(pimConnectorChannelFieldDisplays, comparator);
	}

	private static final String _TYPE_FIXED_VALUE = "fixedValue";

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private PIMConnectorRegistry _pimConnectorRegistry;

}