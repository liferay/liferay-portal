/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.admin.web.internal.exportimport.data.handler;

import com.liferay.asset.categories.admin.web.constants.AssetCategoriesAdminPortletKeys;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.xml.Element;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = "jakarta.portlet.name=" + AssetCategoriesAdminPortletKeys.ASSET_CATEGORIES_ADMIN,
	service = PortletDataHandler.class
)
public class AssetCategoryPortletDataHandler extends BasePortletDataHandler {

	public static final String NAMESPACE = "asset_category";

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Activate
	protected void activate() {
		setDataAlwaysStaged(true);
		setDeletionSystemEventStagedModelTypes(
			new StagedModelType(AssetCategory.class),
			new StagedModelType(AssetVocabulary.class));
		setExportControls(
			new PortletDataHandlerBoolean(
				NAMESPACE, "categories", true, false, null,
				AssetCategory.class.getName()),
			new PortletDataHandlerBoolean(
				NAMESPACE, "vocabularies", true, false, null,
				AssetVocabulary.class.getName()));
		setPublishToLiveByDefault(true);
		setRank(110);
		setStagingControls(getExportControls());
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletDataContext.addPrimaryKey(
				AssetCategoryPortletDataHandler.class, "deleteData")) {

			return portletPreferences;
		}

		_assetVocabularyLocalService.deleteVocabularies(
			portletDataContext.getScopeGroupId());

		return portletPreferences;
	}

	@Override
	protected String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		Element rootElement = addExportDataRootElement(portletDataContext);

		rootElement.addAttribute(
			"group-id", String.valueOf(portletDataContext.getScopeGroupId()));

		if (portletDataContext.getBooleanParameter(NAMESPACE, "categories")) {
			ActionableDynamicQuery categoryActionableDynamicQuery =
				_assetCategoryLocalService.getExportActionableDynamicQuery(
					portletDataContext);

			categoryActionableDynamicQuery.performActions();
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "vocabularies")) {
			ActionableDynamicQuery vocabularyActionableDynamicQuery =
				_assetVocabularyLocalService.getExportActionableDynamicQuery(
					portletDataContext);

			vocabularyActionableDynamicQuery.performActions();
		}

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		if (portletDataContext.getBooleanParameter(NAMESPACE, "categories")) {
			Element categoriesElement =
				portletDataContext.getImportDataGroupElement(
					AssetCategory.class);

			List<Element> categoryElements = categoriesElement.elements();

			for (Element categoryElement : categoryElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, categoryElement);
			}
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "vocabularies")) {
			Element vocabulariesElement =
				portletDataContext.getImportDataGroupElement(
					AssetVocabulary.class);

			List<Element> vocabularyElements = vocabulariesElement.elements();

			for (Element vocabularyElement : vocabularyElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, vocabularyElement);
			}
		}

		return null;
	}

	@Override
	protected void doPrepareManifestSummary(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {

		if (ExportImportDateUtil.isRangeFromLastPublishDate(
				portletDataContext)) {

			_staging.populateLastPublishDateCounts(
				portletDataContext,
				new StagedModelType[] {
					new StagedModelType(AssetCategory.class.getName()),
					new StagedModelType(AssetVocabulary.class.getName())
				});

			return;
		}

		ActionableDynamicQuery categoryActionableDynamicQuery =
			_assetCategoryLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		categoryActionableDynamicQuery.performCount();

		ActionableDynamicQuery vocabularyActionableDynamicQuery =
			_assetVocabularyLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		vocabularyActionableDynamicQuery.performCount();
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private Staging _staging;

}