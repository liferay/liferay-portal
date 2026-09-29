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
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
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
import com.liferay.site.pim.site.initializer.connector.PIMConnectorChannelField;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;
import com.liferay.site.pim.site.initializer.constants.PIMObjectFolderConstants;
import com.liferay.site.pim.site.initializer.internal.constants.PIMFDSNames;
import com.liferay.site.pim.site.initializer.internal.frontend.data.set.model.PIMConnectorChannelFieldDisplay;
import com.liferay.site.pim.site.initializer.internal.util.PIMConnectorFieldMappingsUtil;

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

	private ObjectEntry _fetchObjectEntry(
		HttpServletRequest httpServletRequest) {

		return _objectEntryLocalService.fetchObjectEntry(
			ParamUtil.getLong(httpServletRequest, "objectEntryId"));
	}

	private Comparator<PIMConnectorChannelFieldDisplay> _getComparator(
		String fieldName) {

		if (Objects.equals(fieldName, "channelField")) {
			return Comparator.comparing(
				PIMConnectorChannelFieldDisplay::getChannelField,
				String.CASE_INSENSITIVE_ORDER);
		}

		if (Objects.equals(fieldName, "required")) {
			return Comparator.comparing(
				PIMConnectorChannelFieldDisplay::isRequired);
		}

		if (Objects.equals(fieldName, "sourceAttributes")) {
			return Comparator.comparing(
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

		if (Objects.equals(fieldName, "status")) {
			return Comparator.comparing(
				PIMConnectorChannelFieldDisplay::isMapped);
		}

		return null;
	}

	private String _getLabel(
		PIMConnectorChannelField pimConnectorChannelField) {

		String label = pimConnectorChannelField.getLabel();

		if (pimConnectorChannelField.isMultiple()) {
			return label + "[]";
		}

		return label;
	}

	private Map<String, String> _getObjectFieldLabelMap(
		long companyId, Locale locale,
		Map<String, List<ObjectEntry>> objectEntriesMap) {

		if (objectEntriesMap.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, String> objectFieldLabelMap = new HashMap<>();

		for (ObjectDefinition objectDefinition :
				_objectDefinitionService.getCMSObjectDefinitions(
					companyId,
					new String[] {
						PIMObjectFolderConstants.
							EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES
					})) {

			String objectDefinitionLabel = objectDefinition.getLabel(locale);

			for (ObjectField objectField :
					_objectFieldLocalService.getObjectFields(
						objectDefinition.getObjectDefinitionId())) {

				String objectFieldLabel = objectField.getLabel(locale);

				objectFieldLabelMap.put(
					objectDefinition.getClassName() + StringPool.POUND +
						objectField.getName(),
					StringBundler.concat(
						objectDefinitionLabel, StringPool.SLASH,
						objectFieldLabel));
				objectFieldLabelMap.putIfAbsent(
					StringPool.POUND + objectField.getName(), objectFieldLabel);
			}
		}

		return objectFieldLabelMap;
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

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String apiURL = PIMConnectorFieldMappingsUtil.getAPIURL(
			themeDisplay.getCompanyId());

		String editFieldMappingsURL = ParamUtil.getString(
			httpServletRequest, "editFieldMappingsURL");

		Locale locale = themeDisplay.getLocale();

		Map<String, List<ObjectEntry>> objectEntriesMap = new HashMap<>();

		for (ObjectEntry objectEntry :
				PIMConnectorFieldMappingsUtil.getObjectEntries(
					_fetchObjectEntry(httpServletRequest))) {

			List<ObjectEntry> objectEntries = objectEntriesMap.computeIfAbsent(
				MapUtil.getString(objectEntry.getValues(), "channelFieldName"),
				key -> new ArrayList<>());

			objectEntries.add(objectEntry);
		}

		Map<String, String> objectFieldLabelMap = _getObjectFieldLabelMap(
			themeDisplay.getCompanyId(), locale, objectEntriesMap);

		return TransformUtil.transform(
			pimConnectorChannelFields,
			pimConnectorChannelField -> {
				List<ObjectEntry> objectEntries = objectEntriesMap.get(
					pimConnectorChannelField.getName());

				return new PIMConnectorChannelFieldDisplay(
					apiURL, _getLabel(pimConnectorChannelField),
					TransformUtil.transform(
						objectEntries, ObjectEntry::getObjectEntryId),
					editFieldMappingsURL + "&channelField=" +
						URLCodec.encodeURL(pimConnectorChannelField.getName()),
					locale, pimConnectorChannelField.isRequired(),
					_getSourceAttributes(objectEntries, objectFieldLabelMap));
			});
	}

	private List<PIMConnectorChannelField> _getPIMConnectorChannelFields(
		FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest) {

		ObjectEntry objectEntry = _fetchObjectEntry(httpServletRequest);

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
		List<ObjectEntry> objectEntries,
		Map<String, String> objectFieldLabelMap) {

		return TransformUtil.transform(
			objectEntries,
			objectEntry -> {
				Map<String, Serializable> values = objectEntry.getValues();

				if (Objects.equals(
						MapUtil.getString(values, "type"),
						PIMConnectorFieldMappingsUtil.TYPE_FIXED_VALUE)) {

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

				return objectFieldLabelMap.getOrDefault(
					MapUtil.getString(values, "sourceClassName") +
						StringPool.POUND + sourceFieldName,
					sourceFieldName);
			});
	}

	private List<PIMConnectorChannelFieldDisplay> _sort(
		List<PIMConnectorChannelFieldDisplay> pimConnectorChannelFieldDisplays,
		Sort sort) {

		if (sort == null) {
			return pimConnectorChannelFieldDisplays;
		}

		Comparator<PIMConnectorChannelFieldDisplay> comparator = _getComparator(
			sort.getFieldName());

		if (comparator == null) {
			return pimConnectorChannelFieldDisplays;
		}

		if (sort.isReverse()) {
			comparator = comparator.reversed();
		}

		return ListUtil.sort(pimConnectorChannelFieldDisplays, comparator);
	}

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private PIMConnectorRegistry _pimConnectorRegistry;

}