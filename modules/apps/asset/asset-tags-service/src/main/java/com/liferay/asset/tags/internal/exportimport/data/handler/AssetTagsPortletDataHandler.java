/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.internal.exportimport.data.handler;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.tags.constants.AssetTagsAdminPortletKeys;
import com.liferay.asset.tags.internal.configuration.AssetTagsServiceConfigurationValues;
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
 * @author Daniel Kocsis
 */
@Component(
	property = "jakarta.portlet.name=" + AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN,
	service = PortletDataHandler.class
)
public class AssetTagsPortletDataHandler extends BasePortletDataHandler {

	public static final String NAMESPACE = "asset_tag";

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Activate
	protected void activate() {
		setDataAlwaysStaged(true);
		setDeletionSystemEventStagedModelTypes(
			new StagedModelType(AssetTag.class));

		PortletDataHandlerBoolean tagsPortletDataHandlerBoolean =
			new PortletDataHandlerBoolean(
				NAMESPACE, "tags", true, false, null, AssetTag.class.getName());

		setExportControls(tagsPortletDataHandlerBoolean);

		PortletDataHandlerBoolean mergeTagsByNamePortletDataHandlerBoolean =
			new PortletDataHandlerBoolean(
				NAMESPACE, "merge-tags-by-name",
				AssetTagsServiceConfigurationValues.STAGING_MERGE_TAGS_BY_NAME,
				false, null);

		setImportControls(
			tagsPortletDataHandlerBoolean,
			mergeTagsByNamePortletDataHandlerBoolean);

		setStagingControls(
			tagsPortletDataHandlerBoolean,
			mergeTagsByNamePortletDataHandlerBoolean);

		setPublishToLiveByDefault(true);
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletDataContext.addPrimaryKey(
				AssetTagsPortletDataHandler.class, "deleteData")) {

			return portletPreferences;
		}

		_assetTagLocalService.deleteGroupTags(
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

		if (!portletDataContext.getBooleanParameter(NAMESPACE, "tags")) {
			return getExportDataRootElementString(rootElement);
		}

		ActionableDynamicQuery actionableDynamicQuery =
			_assetTagLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		actionableDynamicQuery.performActions();

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		if (!portletDataContext.getBooleanParameter(NAMESPACE, "tags")) {
			return null;
		}

		Element tagsElement = portletDataContext.getImportDataGroupElement(
			AssetTag.class);

		List<Element> tagElements = tagsElement.elements();

		for (Element tagElement : tagElements) {
			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, tagElement);
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
					new StagedModelType(AssetTag.class.getName())
				});

			return;
		}

		ActionableDynamicQuery actionableDynamicQuery =
			_assetTagLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		actionableDynamicQuery.performCount();
	}

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private Staging _staging;

}