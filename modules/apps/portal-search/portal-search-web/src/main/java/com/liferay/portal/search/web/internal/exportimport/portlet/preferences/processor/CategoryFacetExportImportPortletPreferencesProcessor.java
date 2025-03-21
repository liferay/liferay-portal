/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.portlet.preferences.processor.base.BaseExportImportPortletPreferencesProcessor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.search.web.internal.category.facet.constants.CategoryFacetPortletKeys;
import com.liferay.portal.search.web.internal.category.facet.portlet.CategoryFacetPortletPreferences;

import jakarta.portlet.PortletPreferences;

import java.util.Enumeration;
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
public class CategoryFacetExportImportPortletPreferencesProcessor
	extends BaseExportImportPortletPreferencesProcessor {

	@Override
	public List<Capability> getExportCapabilities() {
		return ListUtil.fromArray(exportCapability);
	}

	@Override
	public List<Capability> getImportCapabilities() {
		return ListUtil.fromArray(importCapability);
	}

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
	protected String getExportPortletPreferencesValue(
			PortletDataContext portletDataContext, Portlet portlet,
			String className, long primaryKeyLong)
		throws Exception {

		if (!className.equals(AssetVocabulary.class.getName())) {
			return null;
		}

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchAssetVocabulary(primaryKeyLong);

		if (assetVocabulary == null) {
			return null;
		}

		long groupId = assetVocabulary.getGroupId();

		portletDataContext.addReferenceElement(
			portlet, portletDataContext.getExportDataRootElement(),
			assetVocabulary, PortletDataContext.REFERENCE_TYPE_DEPENDENCY,
			true);

		String groupExternalReferenceCode = StringPool.BLANK;

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group != null) {
			groupExternalReferenceCode = group.getExternalReferenceCode();
		}

		return StringUtil.merge(
			new Object[] {
				assetVocabulary.getExternalReferenceCode(), groupId,
				groupExternalReferenceCode
			},
			StringPool.POUND);
	}

	@Override
	protected Long getImportPortletPreferencesNewValue(
			PortletDataContext portletDataContext, Class<?> clazz,
			long companyGroupId, Map<Long, Long> primaryKeys,
			String portletPreferencesOldValue)
		throws Exception {

		_importReferenceStagedModel(portletDataContext);

		if (Validator.isNumber(portletPreferencesOldValue)) {
			long oldPrimaryKey = GetterUtil.getLong(portletPreferencesOldValue);

			return MapUtil.getLong(primaryKeys, oldPrimaryKey, oldPrimaryKey);
		}

		String className = clazz.getName();

		String[] oldValues = StringUtil.split(
			portletPreferencesOldValue, StringPool.POUND);

		long groupId = portletDataContext.getScopeGroupId();

		if (oldValues.length > 1) {
			Map<Long, Long> groupIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					Group.class);

			groupId = MapUtil.getLong(
				groupIds, GetterUtil.getLong(oldValues[1]));

			if ((groupId == 0) && (oldValues.length > 2)) {
				Group group =
					_groupLocalService.fetchGroupByExternalReferenceCode(
						oldValues[2], portletDataContext.getCompanyId());

				groupId = group.getGroupId();
			}
		}

		if (!className.equals(AssetVocabulary.class.getName())) {
			return null;
		}

		String erc = oldValues[0];

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.
				fetchAssetVocabularyByExternalReferenceCode(erc, groupId);

		if (assetVocabulary == null) {
			return null;
		}

		return assetVocabulary.getVocabularyId();
	}

	@Reference(target = "(name=PortletDisplayTemplateExporter)")
	protected Capability exportCapability;

	@Reference(target = "(name=PortletDisplayTemplateImporter)")
	protected Capability importCapability;

	private void _importReferenceStagedModel(
			PortletDataContext portletDataContext)
		throws Exception {

		Element importDataRootElement =
			portletDataContext.getImportDataRootElement();

		Element referencesElement = importDataRootElement.element("references");

		if (referencesElement == null) {
			return;
		}

		for (Element referenceElement : referencesElement.elements()) {
			String className = referenceElement.attributeValue("class-name");

			if ((className == null) ||
				!className.equals(AssetVocabulary.class.getName())) {

				continue;
			}

			long classPK = GetterUtil.getLong(
				referenceElement.attributeValue("class-pk"));

			StagedModelDataHandlerUtil.importReferenceStagedModel(
				portletDataContext, className, Long.valueOf(classPK));
		}
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
						PREFERENCE_VOCABULARY_IDS)) {

				updateExportPortletPreferencesClassPKs(
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
						PREFERENCE_VOCABULARY_IDS)) {

				updateImportPortletPreferencesClassPKs(
					portletDataContext, portletPreferences, name,
					AssetVocabulary.class, companyGroup.getGroupId());
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