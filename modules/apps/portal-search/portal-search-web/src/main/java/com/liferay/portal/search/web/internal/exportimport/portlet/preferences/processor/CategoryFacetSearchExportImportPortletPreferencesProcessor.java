/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.web.internal.category.facet.constants.CategoryFacetPortletKeys;
import com.liferay.portal.search.web.internal.category.facet.portlet.CategoryFacetPortletPreferences;

import jakarta.portlet.PortletPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Felipe Lorenz, Joshua Cords
 */
@Component(
	property = "jakarta.portlet.name=" + CategoryFacetPortletKeys.CATEGORY_FACET,
	service = ExportImportPortletPreferencesProcessor.class
)
public class CategoryFacetSearchExportImportPortletPreferencesProcessor
	extends BaseSearchExportImportPortletPreferencesProcessor {

	@Override
	public PortletPreferences processExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		try {
			return _updateExportPortletPreferences(
				portletDataContext, portletPreferences,
				portletDataContext.getPortletId());
		}
		catch (Exception exception) {
			throw new PortletDataException(
				"Unable to update asset categories navigation portlet " +
					"preferences during export",
				exception);
		}
	}

	@Override
	public PortletPreferences processImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		try {
			return _updateImportPortletPreferences(
				portletDataContext, portletPreferences);
		}
		catch (Exception exception) {
			throw new PortletDataException(
				"Unable to update asset categories navigation portlet " +
					"preferences during import",
				exception);
		}
	}

	@Override
	protected String getExportPortletPreferencesExternalReferenceCode(
		PortletDataContext portletDataContext, Portlet portlet,
		String className, String externalReferenceCode) {

		if (!className.equals(AssetVocabulary.class.getName())) {
			return null;
		}

		String[] externalReferenceCodeParts = StringUtil.split(
			externalReferenceCode, "&&");

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCodeParts[0], portletDataContext.getCompanyId());

		if (group == null) {
			return externalReferenceCode;
		}

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.
				fetchAssetVocabularyByExternalReferenceCode(
					externalReferenceCodeParts[1], group.getGroupId());

		if (assetVocabulary == null) {
			return StringUtil.merge(
				new Object[] {
					externalReferenceCodeParts[1], group.getGroupId(),
					externalReferenceCodeParts[0]
				},
				StringPool.POUND);
		}

		portletDataContext.addReferenceElement(
			portlet, portletDataContext.getExportDataRootElement(),
			assetVocabulary, PortletDataContext.REFERENCE_TYPE_DEPENDENCY,
			true);

		String groupExternalReferenceCode =
			getGroupExportPortletPreferencesExternalReferenceCode(
				portletDataContext, externalReferenceCodeParts[0]);

		return StringUtil.merge(
			new Object[] {
				assetVocabulary.getExternalReferenceCode(), group.getGroupId(),
				groupExternalReferenceCode
			},
			StringPool.POUND);
	}

	@Override
	protected String getExportPortletPreferencesValue(
			PortletDataContext portletDataContext, Portlet portlet,
			String className, long primaryKeyLong)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method is deprecated and replaced by " +
				"getGroupExportPortletPreferencesExternalReferenceCode");
	}

	protected String getImportPortletPreferencesNewExternalReferenceCode(
			PortletDataContext portletDataContext, Class<?> clazz,
			long companyGroupId, Map<String, String[]> primaryKeys,
			String portletPreferencesOldExternalReferenceCode)
		throws Exception {

		String className = clazz.getName();

		if (!className.equals(AssetVocabulary.class.getName())) {
			return null;
		}

		String[] oldValues = StringUtil.split(
			portletPreferencesOldExternalReferenceCode, StringPool.POUND);

		if (oldValues.length == 1) {
			return _getAssetVocabularyPortletPreferenceNewValue(
				GetterUtil.getLong(oldValues[0]), portletDataContext);
		}
		else if (oldValues.length == 3) {
			String groupExternalReferenceCode =
				_getGroupExternalReferenceCodeNewValue(
					oldValues[2], GetterUtil.getLong(oldValues[1]),
					portletDataContext);

			if (groupExternalReferenceCode != null) {
				return groupExternalReferenceCode + "&&" + oldValues[0];
			}
		}

		return null;
	}

	@Override
	protected Long getImportPortletPreferencesNewValue(
			PortletDataContext portletDataContext, Class<?> clazz,
			long companyGroupId, Map<Long, Long> primaryKeys,
			String portletPreferencesOldValue)
		throws Exception {

		throw new Exception(
			"This method is deprecated and replaced by " +
				"getImportPortletPreferencesNewExternalReferenceCode");
	}

	private String _getAssetVocabularyPortletPreferenceNewValue(
		long oldAssetVocabularyId, PortletDataContext portletDataContext) {

		Map<Long, Long> newPrimaryKeysMap =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				AssetVocabulary.class);

		long newAssetVocabularyId = MapUtil.getLong(
			newPrimaryKeysMap, oldAssetVocabularyId);

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchAssetVocabulary(
				newAssetVocabularyId);

		if (assetVocabulary != null) {
			Group group = _groupLocalService.fetchGroup(
				assetVocabulary.getGroupId());

			return group.getExternalReferenceCode() + "&&" +
				assetVocabulary.getExternalReferenceCode();
		}

		return null;
	}

	private String _getGroupExternalReferenceCodeNewValue(
		String oldExternalReferenceCode, long oldGroupId,
		PortletDataContext portletDataContext) {

		Map<Long, Long> newPrimaryKeysMap =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		long newGroupId = MapUtil.getLong(newPrimaryKeysMap, oldGroupId);

		if (newGroupId != 0) {
			Group group = _groupLocalService.fetchGroup(newGroupId);

			return group.getExternalReferenceCode();
		}

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			oldExternalReferenceCode, portletDataContext.getCompanyId());

		if (group != null) {
			return group.getExternalReferenceCode();
		}

		return null;
	}

	private String[] _getGroupVocabularyExternalReferenceCodes(
		String[] vocabularyIds) {

		if (vocabularyIds == null) {
			return null;
		}

		List<String> values = new ArrayList<>();

		for (String vocabularyId : vocabularyIds) {
			AssetVocabulary assetVocabulary =
				_assetVocabularyLocalService.fetchAssetVocabulary(
					GetterUtil.getLong(vocabularyId));

			if (assetVocabulary == null) {
				continue;
			}

			Group group = _groupLocalService.fetchGroup(
				assetVocabulary.getGroupId());

			if (group == null) {
				continue;
			}

			values.add(
				StringUtil.merge(
					new Object[] {
						assetVocabulary.getExternalReferenceCode(),
						group.getGroupId(), group.getExternalReferenceCode()
					},
					StringPool.POUND));
		}

		return values.toArray(new String[0]);
	}

	private PortletPreferences _updateExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences, String portletId)
		throws Exception {

		String[] groupVocabularyExternalReferenceCodes =
			portletPreferences.getValues(
				CategoryFacetPortletPreferences.
					PREFERENCE_GROUP_VOCABULARY_EXTERNAL_REFERENCE_CODES,
				null);
		String[] vocabularyIds = portletPreferences.getValues(
			"vocabularyIds", null);

		if (groupVocabularyExternalReferenceCodes != null) {
			updateExportPortletPreferencesExternalReferenceCodes(
				portletDataContext,
				_portletLocalService.getPortletById(
					portletDataContext.getCompanyId(), portletId),
				portletPreferences,
				CategoryFacetPortletPreferences.
					PREFERENCE_GROUP_VOCABULARY_EXTERNAL_REFERENCE_CODES,
				AssetVocabulary.class.getName());
		}
		else if (vocabularyIds != null) {
			portletPreferences.setValues(
				CategoryFacetPortletPreferences.
					PREFERENCE_GROUP_VOCABULARY_EXTERNAL_REFERENCE_CODES,
				_getGroupVocabularyExternalReferenceCodes(
					portletPreferences.getValues("vocabularyIds", null)));

			portletPreferences.reset("vocabularyIds");
		}

		return portletPreferences;
	}

	private PortletPreferences _updateImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {

		Company company = _companyLocalService.getCompanyById(
			portletDataContext.getCompanyId());

		Group companyGroup = company.getGroup();

		String[] groupVocabularyExternalReferenceCodes =
			portletPreferences.getValues(
				CategoryFacetPortletPreferences.
					PREFERENCE_GROUP_VOCABULARY_EXTERNAL_REFERENCE_CODES,
				null);
		String[] vocabularyIds = portletPreferences.getValues(
			"vocabularyIds", null);

		if (groupVocabularyExternalReferenceCodes != null) {
			updateImportPortletPreferencesExternalReferenceCodes(
				portletDataContext, portletPreferences,
				CategoryFacetPortletPreferences.
					PREFERENCE_GROUP_VOCABULARY_EXTERNAL_REFERENCE_CODES,
				AssetVocabulary.class, companyGroup.getGroupId());
		}
		else if (vocabularyIds != null) {
			updateImportPortletPreferencesExternalReferenceCodes(
				portletDataContext, portletPreferences, "vocabularyIds",
				AssetVocabulary.class, companyGroup.getGroupId());

			portletPreferences.setValues(
				CategoryFacetPortletPreferences.
					PREFERENCE_GROUP_VOCABULARY_EXTERNAL_REFERENCE_CODES,
				portletPreferences.getValues("vocabularyIds", null));

			portletPreferences.reset("vocabularyIds");
		}

		return portletPreferences;
	}

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private PortletLocalService _portletLocalService;

}