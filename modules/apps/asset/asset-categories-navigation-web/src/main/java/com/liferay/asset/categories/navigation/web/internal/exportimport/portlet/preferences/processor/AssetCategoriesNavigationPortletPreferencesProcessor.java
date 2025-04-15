/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.navigation.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.asset.categories.navigation.constants.AssetCategoriesNavigationPortletKeys;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.portlet.preferences.processor.base.BaseExportImportPortletPreferencesProcessor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = "jakarta.portlet.name=" + AssetCategoriesNavigationPortletKeys.ASSET_CATEGORIES_NAVIGATION,
	service = ExportImportPortletPreferencesProcessor.class
)
public class AssetCategoriesNavigationPortletPreferencesProcessor
	extends BaseExportImportPortletPreferencesProcessor {

	@Override
	public List<Capability> getExportCapabilities() {
		return ListUtil.fromArray(_exportCapability);
	}

	@Override
	public List<Capability> getImportCapabilities() {
		return ListUtil.fromArray(_importCapability);
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

		String uuid = null;
		long groupId = 0;

		if (className.equals(AssetVocabulary.class.getName())) {
			AssetVocabulary assetVocabulary =
				_assetVocabularyLocalService.fetchAssetVocabulary(
					primaryKeyLong);

			if (assetVocabulary != null) {
				uuid = assetVocabulary.getUuid();
				groupId = assetVocabulary.getGroupId();

				portletDataContext.addReferenceElement(
					portlet, portletDataContext.getExportDataRootElement(),
					assetVocabulary,
					PortletDataContext.REFERENCE_TYPE_DEPENDENCY, true);
			}
		}

		if (Validator.isNull(uuid)) {
			return null;
		}

		return StringUtil.merge(new Object[] {uuid, groupId}, StringPool.POUND);
	}

	@Override
	protected Long getImportPortletPreferencesNewValue(
			PortletDataContext portletDataContext, Class<?> clazz,
			long companyGroupId, Map<Long, Long> primaryKeys,
			String portletPreferencesOldValue)
		throws Exception {

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
		}

		if (!className.equals(AssetVocabulary.class.getName())) {
			return null;
		}

		String uuid = oldValues[0];

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchAssetVocabularyByUuidAndGroupId(
				uuid, groupId);

		if (assetVocabulary != null) {
			return assetVocabulary.getVocabularyId();
		}

		return null;
	}

	private PortletPreferences _updateExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences, String portletId)
		throws Exception {

		Portlet portlet = _portletLocalService.getPortletById(
			portletDataContext.getCompanyId(), portletId);

		_updatePortletPreferencesExternalReferenceCodes(
			portlet, portletDataContext, portletPreferences);

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

			if (name.equals("assetVocabularyIds")) {
				updateImportPortletPreferencesClassPKs(
					portletDataContext, portletPreferences, name,
					AssetVocabulary.class, companyGroup.getGroupId());
			}
		}

		return portletPreferences;
	}

	private void _updatePortletPreferencesExternalReferenceCodes(
			Portlet portlet, PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {

		String[] assetVocabularyGroupExternalReferenceCodes =
			portletPreferences.getValues(
				"assetVocabularyGroupExternalReferenceCodes", null);

		updateExportPortletPreferencesExternalReferenceCodes(
			portletDataContext, portlet, portletPreferences,
			"assetVocabularyGroupExternalReferenceCodes",
			Group.class.getName());

		String[] newAssetVocabularyGroupExternalReferenceCodes =
			portletPreferences.getValues(
				"assetVocabularyGroupExternalReferenceCodes", null);

		if (newAssetVocabularyGroupExternalReferenceCodes == null) {
			return;
		}

		for (int i = 0; i < assetVocabularyGroupExternalReferenceCodes.length;
			 i++) {

			String assetVocabularyGroupExternalReferenceCode =
				assetVocabularyGroupExternalReferenceCodes[i];
			String newAssetVocabularyGroupExternalReferenceCode =
				newAssetVocabularyGroupExternalReferenceCodes[i];

			if (Objects.equals(
					assetVocabularyGroupExternalReferenceCode,
					newAssetVocabularyGroupExternalReferenceCode)) {

				continue;
			}

			String[] assetVocabularyExternalReferenceCodesValues =
				portletPreferences.getValues(
					"assetVocabularyExternalReferenceCodes_" +
						assetVocabularyGroupExternalReferenceCode,
					null);

			portletPreferences.setValues(
				"assetVocabularyExternalReferenceCodes_" +
					newAssetVocabularyGroupExternalReferenceCode,
				assetVocabularyExternalReferenceCodesValues);

			portletPreferences.reset(
				"assetVocabularyExternalReferenceCodes_" +
					assetVocabularyGroupExternalReferenceCode);
		}
	}

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(target = "(name=PortletDisplayTemplateExporter)")
	private Capability _exportCapability;

	@Reference(target = "(name=PortletDisplayTemplateImporter)")
	private Capability _importCapability;

	@Reference
	private PortletLocalService _portletLocalService;

}