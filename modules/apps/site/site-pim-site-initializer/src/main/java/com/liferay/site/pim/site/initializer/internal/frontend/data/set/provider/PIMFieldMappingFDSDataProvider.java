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
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorField;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;
import com.liferay.site.pim.site.initializer.internal.constants.PIMFDSNames;
import com.liferay.site.pim.site.initializer.internal.frontend.data.set.model.PIMFieldMappingFDSEntry;
import com.liferay.site.pim.site.initializer.internal.util.PIMFieldMappingUtil;
import com.liferay.site.pim.site.initializer.internal.util.PIMProductTypeUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "fds.data.provider.key=" + PIMFDSNames.FIELD_MAPPINGS,
	service = FDSDataProvider.class
)
public class PIMFieldMappingFDSDataProvider
	implements FDSDataProvider<PIMFieldMappingFDSEntry> {

	@Override
	public List<PIMFieldMappingFDSEntry> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		List<PIMFieldMappingFDSEntry> pimFieldMappingFDSEntries =
			_sortPIMFieldMappingFDSEntries(
				_getPIMFieldMappingFDSEntries(fdsKeywords, httpServletRequest),
				sort);

		return ListUtil.subList(
			pimFieldMappingFDSEntries, fdsPagination.getStartPosition(),
			fdsPagination.getEndPosition());
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		List<PIMFieldMappingFDSEntry> pimFieldMappingFDSEntries =
			_getPIMFieldMappingFDSEntries(fdsKeywords, httpServletRequest);

		return pimFieldMappingFDSEntries.size();
	}

	private static String _getFirstSourceAttribute(
		PIMFieldMappingFDSEntry pimFieldMappingFDSEntry) {

		List<String> sourceAttributes =
			pimFieldMappingFDSEntry.getSourceAttributes();

		if (sourceAttributes.isEmpty()) {
			return StringPool.BLANK;
		}

		return sourceAttributes.get(0);
	}

	private List<PIMFieldMappingFDSEntry> _filterPIMFieldMappingFDSEntries(
		HttpServletRequest httpServletRequest,
		List<PIMFieldMappingFDSEntry> pimFieldMappingFDSEntries) {

		String filter = ParamUtil.getString(httpServletRequest, "filter");

		boolean mapped =
			filter.contains("mapped eq true") ||
			filter.contains("mapped ne false");
		boolean unmapped =
			filter.contains("mapped eq false") ||
			filter.contains("mapped ne true");

		if (mapped == unmapped) {
			return pimFieldMappingFDSEntries;
		}

		return ListUtil.filter(
			pimFieldMappingFDSEntries,
			pimFieldMappingFDSEntry ->
				pimFieldMappingFDSEntry.isMapped() == mapped);
	}

	private JSONObject _getFieldMappingJSONObject(
			Map<String, Serializable> values)
		throws PortalException {

		String fieldMapping = MapUtil.getString(values, "fieldMapping");

		if (Validator.isNull(fieldMapping)) {
			return _jsonFactory.createJSONObject();
		}

		return _jsonFactory.createJSONObject(fieldMapping);
	}

	private String _getLabel(PIMConnectorField pimConnectorField) {
		String label = pimConnectorField.getLabel();

		if (pimConnectorField.isMultiple()) {
			return label + "[]";
		}

		return label;
	}

	private Map<String, String> _getObjectFieldLabels(
		long companyId, Locale locale) {

		Map<String, String> objectFieldLabels = new HashMap<>();

		for (ObjectDefinition objectDefinition :
				PIMProductTypeUtil.getObjectDefinitions(
					companyId, _objectDefinitionLocalService,
					_objectFolderLocalService)) {

			for (ObjectField objectField :
					PIMProductTypeUtil.getObjectFields(
						objectDefinition, _objectFieldLocalService)) {

				objectFieldLabels.put(
					objectField.getName(), objectField.getLabel(locale));
			}
		}

		return objectFieldLabels;
	}

	private List<PIMFieldMappingFDSEntry> _getPIMFieldMappingFDSEntries(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			ParamUtil.getLong(httpServletRequest, "objectEntryId"));

		if (objectEntry == null) {
			return Collections.emptyList();
		}

		Map<String, Serializable> values = objectEntry.getValues();

		PIMConnector pimConnector = _pimConnectorRegistry.getPIMConnector(
			MapUtil.getString(values, "key"));

		if (pimConnector == null) {
			return Collections.emptyList();
		}

		List<PIMFieldMappingFDSEntry> pimFieldMappingFDSEntries =
			new ArrayList<>();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Locale locale = themeDisplay.getLocale();

		JSONObject fieldMappingJSONObject = _getFieldMappingJSONObject(values);

		Map<String, String> objectFieldLabels = _getObjectFieldLabels(
			themeDisplay.getCompanyId(), locale);

		String mapChannelFieldURL = ParamUtil.getString(
			httpServletRequest, "mapChannelFieldURL");

		String keywords = StringUtil.toLowerCase(fdsKeywords.getKeywords());

		for (PIMConnectorField pimConnectorField :
				pimConnector.getPIMConnectorFields(locale)) {

			String label = _getLabel(pimConnectorField);

			if (Validator.isNotNull(keywords) &&
				!StringUtil.toLowerCase(
					label
				).contains(
					keywords
				)) {

				continue;
			}

			String name = pimConnectorField.getName();

			pimFieldMappingFDSEntries.add(
				new PIMFieldMappingFDSEntry(
					label,
					mapChannelFieldURL + "&channelField=" +
						URLCodec.encodeURL(name),
					pimConnectorField.isRequired(),
					_getSourceAttributes(
						fieldMappingJSONObject, name, objectFieldLabels)));
		}

		return _filterPIMFieldMappingFDSEntries(
			httpServletRequest, pimFieldMappingFDSEntries);
	}

	private List<String> _getSourceAttributes(
		JSONObject fieldMappingJSONObject, String name,
		Map<String, String> objectFieldLabels) {

		List<String> sourceAttributes = new ArrayList<>();

		JSONArray mappingsJSONArray = PIMFieldMappingUtil.getMappingsJSONArray(
			fieldMappingJSONObject, name);

		for (int i = 0; i < mappingsJSONArray.length(); i++) {
			JSONObject mappingJSONObject = mappingsJSONArray.getJSONObject(i);

			if (PIMFieldMappingUtil.isFixedValue(mappingJSONObject)) {
				String value = mappingJSONObject.getString("value");

				if (Validator.isNotNull(value)) {
					sourceAttributes.add(value);
				}

				continue;
			}

			String attribute = mappingJSONObject.getString("attribute");

			if (Validator.isNotNull(attribute)) {
				sourceAttributes.add(
					objectFieldLabels.getOrDefault(attribute, attribute));
			}
		}

		return sourceAttributes;
	}

	private List<PIMFieldMappingFDSEntry> _sortPIMFieldMappingFDSEntries(
		List<PIMFieldMappingFDSEntry> pimFieldMappingFDSEntries, Sort sort) {

		if (sort == null) {
			return pimFieldMappingFDSEntries;
		}

		String fieldName = sort.getFieldName();

		if (Validator.isNull(fieldName)) {
			return pimFieldMappingFDSEntries;
		}

		Comparator<PIMFieldMappingFDSEntry> comparator = null;

		if (fieldName.equals("channelField")) {
			comparator = Comparator.comparing(
				PIMFieldMappingFDSEntry::getChannelField,
				String.CASE_INSENSITIVE_ORDER);
		}
		else if (fieldName.equals("mapped")) {
			comparator = Comparator.comparing(
				PIMFieldMappingFDSEntry::isMapped);
		}
		else if (fieldName.equals("required")) {
			comparator = Comparator.comparing(
				PIMFieldMappingFDSEntry::isRequired);
		}
		else if (fieldName.equals("sourceAttributes")) {
			comparator = Comparator.comparing(
				PIMFieldMappingFDSDataProvider::_getFirstSourceAttribute,
				String.CASE_INSENSITIVE_ORDER);
		}
		else {
			return pimFieldMappingFDSEntries;
		}

		if (sort.isReverse()) {
			comparator = comparator.reversed();
		}

		return ListUtil.sort(pimFieldMappingFDSEntries, comparator);
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

	@Reference
	private PIMConnectorRegistry _pimConnectorRegistry;

}