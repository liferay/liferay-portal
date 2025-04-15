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

import java.util.Enumeration;
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

		Group group = null;

		if (oldValues.length > 1) {
			Map<Long, Long> newPrimaryKeysMap =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					Group.class);

			long groupId = MapUtil.getLong(
				newPrimaryKeysMap, GetterUtil.getLong(oldValues[1]));

			if (groupId != 0) {
				group = _groupLocalService.fetchGroup(groupId);
			}
		}

		if (group != null) {
			return group.getExternalReferenceCode() + "&&" + oldValues[0];
		}

		return oldValues[2] + "&&" + oldValues[0];
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

	private PortletPreferences _updateExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences, String portletId)
		throws Exception {

		Portlet portlet = _portletLocalService.getPortletById(
			portletDataContext.getCompanyId(), portletId);

		Enumeration<String> enumeration = portletPreferences.getNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			if (name.equals(
					CategoryFacetPortletPreferences.
						PREFERENCE_GROUP_VOCABULARY_EXTERNAL_REFERENCE_CODES)) {

				updateExportPortletPreferencesExternalReferenceCodes(
					portletDataContext, portlet, portletPreferences, name,
					AssetVocabulary.class.getName());
			}
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

		Enumeration<String> enumeration = portletPreferences.getNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			if (name.equals(
					CategoryFacetPortletPreferences.
						PREFERENCE_GROUP_VOCABULARY_EXTERNAL_REFERENCE_CODES)) {

				updateImportPortletPreferencesExternalReferenceCodes(
					portletDataContext, portletPreferences, name,
					AssetVocabulary.class, companyGroup.getGroupId());
			}
			else if (name.equals("vocabularyIds")) {
				updateImportPortletPreferencesExternalReferenceCodes(
					portletDataContext, portletPreferences, name,
					AssetVocabulary.class, companyGroup.getGroupId());

				portletPreferences.setValues(
					CategoryFacetPortletPreferences.
						PREFERENCE_GROUP_VOCABULARY_EXTERNAL_REFERENCE_CODES,
					portletPreferences.getValues("vocabularyIds", null));

				portletPreferences.reset("vocabularyIds");
			}
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