/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.admin.web.internal.exportimport.data.handler;

import com.liferay.asset.category.property.model.AssetCategoryProperty;
import com.liferay.asset.category.property.service.AssetCategoryPropertyLocalService;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelModifiedDateComparator;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Element;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Zsolt Berentey
 * @author Gergely Mathe
 * @author Máté Thurzó
 */
@Component(service = StagedModelDataHandler.class)
public class AssetCategoryStagedModelDataHandler
	extends BaseStagedModelDataHandler<AssetCategory> {

	public static final String[] CLASS_NAMES = {AssetCategory.class.getName()};

	@Override
	public void deleteStagedModel(AssetCategory category)
		throws PortalException {

		_assetCategoryLocalService.deleteCategory(category);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		AssetCategory category = fetchStagedModelByUuidAndGroupId(
			uuid, groupId);

		if (category != null) {
			deleteStagedModel(category);
		}
	}

	@Override
	public AssetCategory fetchStagedModelByExternalReferenceCodeAndGroupId(
		String externalReferenceCode, long groupId) {

		return _assetCategoryLocalService.
			fetchAssetCategoryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public AssetCategory fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _assetCategoryLocalService.fetchAssetCategoryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public List<AssetCategory> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _assetCategoryLocalService.getAssetCategoriesByUuidAndCompanyId(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			new StagedModelModifiedDateComparator<AssetCategory>());
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(AssetCategory category) {
		return category.getTitleCurrentValue();
	}

	protected ServiceContext createServiceContext(
		PortletDataContext portletDataContext, AssetCategory category) {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setCreateDate(category.getCreateDate());
		serviceContext.setModifiedDate(category.getModifiedDate());
		serviceContext.setScopeGroupId(portletDataContext.getScopeGroupId());

		return serviceContext;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, AssetCategory category)
		throws Exception {

		if (category.getParentCategoryId() !=
				AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {

			AssetCategory parentCategory =
				_assetCategoryLocalService.fetchAssetCategory(
					category.getParentCategoryId());

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, category, parentCategory,
				PortletDataContext.REFERENCE_TYPE_PARENT);
		}
		else {
			AssetVocabulary vocabulary =
				_assetVocabularyLocalService.fetchAssetVocabulary(
					category.getVocabularyId());

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, category, vocabulary,
				PortletDataContext.REFERENCE_TYPE_PARENT);
		}

		Element categoryElement = portletDataContext.getExportDataElement(
			category);

		category.setUserUuid(category.getUserUuid());

		List<AssetCategoryProperty> assetCategoryProperties =
			_assetCategoryPropertyLocalService.getCategoryProperties(
				category.getCategoryId());

		for (AssetCategoryProperty assetCategoryProperty :
				assetCategoryProperties) {

			if (!_exists(categoryElement, assetCategoryProperty)) {
				Element propertyElement = categoryElement.addElement(
					"property");

				propertyElement.addAttribute(
					"userUuid", assetCategoryProperty.getUserUuid());
				propertyElement.addAttribute(
					"key", assetCategoryProperty.getKey());
				propertyElement.addAttribute(
					"value", assetCategoryProperty.getValue());
			}
		}

		String categoryPath = ExportImportPathUtil.getModelPath(category);

		categoryElement.addAttribute("path", categoryPath);

		_exportAssetDisplayPage(portletDataContext, category);

		portletDataContext.addReferenceElement(
			category, categoryElement, category,
			PortletDataContext.REFERENCE_TYPE_DEPENDENCY, false);

		portletDataContext.addPermissions(
			AssetCategory.class, category.getCategoryId());

		portletDataContext.addZipEntry(categoryPath, category);
	}

	@Override
	protected void doImportMissingReference(
			PortletDataContext portletDataContext, String uuid, long groupId,
			long categoryId)
		throws Exception {

		AssetCategory existingCategory = fetchMissingReference(uuid, groupId);

		if (existingCategory == null) {
			return;
		}

		Map<Long, Long> categoryIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				AssetCategory.class);

		categoryIds.put(categoryId, existingCategory.getCategoryId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, AssetCategory category)
		throws Exception {

		long userId = portletDataContext.getUserId(category.getUserUuid());

		Map<Long, Long> vocabularyIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				AssetVocabulary.class);

		long vocabularyId = MapUtil.getLong(
			vocabularyIds, category.getVocabularyId(),
			category.getVocabularyId());

		Map<Long, Long> categoryIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				AssetCategory.class);

		long parentCategoryId = MapUtil.getLong(
			categoryIds, category.getParentCategoryId(),
			category.getParentCategoryId());

		Element categoryElement = portletDataContext.getImportDataElement(
			category);

		List<Element> propertyElements = categoryElement.elements("property");

		String[] properties = new String[propertyElements.size()];

		for (int i = 0; i < propertyElements.size(); i++) {
			Element propertyElement = propertyElements.get(i);

			String key = propertyElement.attributeValue("key");
			String value = propertyElement.attributeValue("value");

			properties[i] = StringBundler.concat(
				key, AssetCategoryConstants.PROPERTY_KEY_VALUE_SEPARATOR,
				value);
		}

		ServiceContext serviceContext = createServiceContext(
			portletDataContext, category);

		AssetCategory importedCategory = null;

		AssetCategory existingCategory = fetchExistingStagedModel(
			category, portletDataContext.getScopeGroupId());

		if (existingCategory == null) {
			String name = _getCategoryName(
				null, portletDataContext.getScopeGroupId(), parentCategoryId,
				category.getName(), vocabularyId, 2);

			importedCategory = _assetCategoryLocalService.addCategory(
				category.getExternalReferenceCode(), userId,
				portletDataContext.getScopeGroupId(), parentCategoryId,
				_getCategoryTitleMap(
					portletDataContext.getScopeGroupId(), category, name),
				category.getDescriptionMap(), vocabularyId, category.isSystem(),
				properties, serviceContext);
		}
		else {
			String name = _getCategoryName(
				category.getExternalReferenceCode(),
				portletDataContext.getScopeGroupId(), parentCategoryId,
				category.getName(), vocabularyId, 2);

			importedCategory = _assetCategoryLocalService.updateCategory(
				existingCategory.getExternalReferenceCode(), userId,
				existingCategory.getCategoryId(), parentCategoryId,
				_getCategoryTitleMap(
					portletDataContext.getScopeGroupId(), category, name),
				category.getDescriptionMap(), vocabularyId, properties,
				serviceContext);
		}

		importedCategory.setUuid(category.getUuid());
		importedCategory.setModifiedDate(category.getModifiedDate());

		importedCategory = _assetCategoryLocalService.updateAssetCategory(
			importedCategory);

		categoryIds.put(
			category.getCategoryId(), importedCategory.getCategoryId());

		Map<String, String> categoryUuids =
			(Map<String, String>)portletDataContext.getNewPrimaryKeysMap(
				AssetCategory.class + ".uuid");

		categoryUuids.put(category.getUuid(), importedCategory.getUuid());

		portletDataContext.importPermissions(
			AssetCategory.class, category.getCategoryId(),
			importedCategory.getCategoryId());

		_importAssetDisplayPage(portletDataContext, category, importedCategory);
	}

	private boolean _exists(
		Element categoryElement, AssetCategoryProperty categoryProperty) {

		String key = categoryProperty.getKey();

		for (Element propertyElement : categoryElement.elements("property")) {
			if (key.equals(propertyElement.attributeValue("key"))) {
				return true;
			}
		}

		return false;
	}

	private void _exportAssetDisplayPage(
			PortletDataContext portletDataContext, AssetCategory category)
		throws Exception {

		AssetDisplayPageEntry assetDisplayPageEntry =
			_assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
				category.getGroupId(),
				_portal.getClassNameId(AssetCategory.class),
				category.getCategoryId());

		if (assetDisplayPageEntry != null) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, category, assetDisplayPageEntry,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}
	}

	private String _getCategoryName(
		String externalReferenceCode, long groupId, long parentCategoryId,
		String name, long vocabularyId, int count) {

		AssetCategory category = _assetCategoryLocalService.fetchCategory(
			groupId, parentCategoryId, name, vocabularyId);

		if ((category == null) ||
			Objects.equals(
				externalReferenceCode, category.getExternalReferenceCode())) {

			return name;
		}

		name = StringUtil.appendParentheticalSuffix(name, count);

		return _getCategoryName(
			externalReferenceCode, groupId, parentCategoryId, name,
			vocabularyId, ++count);
	}

	private Map<Locale, String> _getCategoryTitleMap(
			long groupId, AssetCategory category, String name)
		throws Exception {

		Map<Locale, String> titleMap = category.getTitleMap();

		Locale locale = _portal.getSiteDefaultLocale(groupId);

		if (titleMap.isEmpty() || !Objects.equals(category.getName(), name) ||
			!titleMap.containsKey(locale)) {

			titleMap.put(locale, name);
		}

		return titleMap;
	}

	private void _importAssetDisplayPage(
			PortletDataContext portletDataContext, AssetCategory category,
			AssetCategory importedCategory)
		throws Exception {

		List<Element> assetDisplayPageEntryElements =
			portletDataContext.getReferenceDataElements(
				category, AssetDisplayPageEntry.class);

		Map<Long, Long> categoryNewPrimaryKeys =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				AssetCategory.class);

		categoryNewPrimaryKeys.put(
			category.getCategoryId(), importedCategory.getCategoryId());

		if (ListUtil.isEmpty(assetDisplayPageEntryElements)) {
			AssetDisplayPageEntry existingAssetDisplayPageEntry =
				_assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
					importedCategory.getGroupId(),
					_portal.getClassNameId(AssetCategory.class.getName()),
					importedCategory.getCategoryId());

			if (existingAssetDisplayPageEntry != null) {
				_assetDisplayPageEntryLocalService.deleteAssetDisplayPageEntry(
					existingAssetDisplayPageEntry);
			}

			return;
		}

		for (Element assetDisplayPageEntryElement :
				assetDisplayPageEntryElements) {

			String path = assetDisplayPageEntryElement.attributeValue("path");

			AssetDisplayPageEntry assetDisplayPageEntry =
				(AssetDisplayPageEntry)portletDataContext.getZipEntryAsObject(
					path);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, assetDisplayPageEntryElement);

			Map<Long, Long> assetDisplayPageEntries =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					AssetDisplayPageEntry.class);

			long assetDisplayPageEntryId = MapUtil.getLong(
				assetDisplayPageEntries,
				assetDisplayPageEntry.getAssetDisplayPageEntryId(),
				assetDisplayPageEntry.getAssetDisplayPageEntryId());

			AssetDisplayPageEntry existingAssetDisplayPageEntry =
				_assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
					assetDisplayPageEntryId);

			if (existingAssetDisplayPageEntry != null) {
				existingAssetDisplayPageEntry.setClassPK(
					importedCategory.getCategoryId());

				_assetDisplayPageEntryLocalService.updateAssetDisplayPageEntry(
					existingAssetDisplayPageEntry);
			}
		}
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetCategoryPropertyLocalService
		_assetCategoryPropertyLocalService;

	@Reference
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private Portal _portal;

}