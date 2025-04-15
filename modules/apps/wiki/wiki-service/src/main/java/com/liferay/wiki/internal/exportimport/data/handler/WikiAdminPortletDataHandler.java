/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.internal.exportimport.data.handler;

import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.wiki.constants.WikiConstants;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.internal.util.WikiCacheThreadLocal;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageDisplay;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiPageLocalService;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Farache
 * @author Jorge Ferrer
 * @author Marcellus Tavares
 * @author Juan Fernández
 * @author Zsolt Berentey
 * @author Máté Thurzó
 * @author Gergely Mathe
 */
@Component(
	property = "jakarta.portlet.name=" + WikiPortletKeys.WIKI_ADMIN,
	service = PortletDataHandler.class
)
public class WikiAdminPortletDataHandler extends BasePortletDataHandler {

	public static final String[] CLASS_NAMES = {
		WikiNode.class.getName(), WikiPage.class.getName()
	};

	public static final String NAMESPACE = "wiki";

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String getResourceName() {
		return WikiConstants.RESOURCE_NAME;
	}

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Override
	public String getServiceName() {
		return WikiConstants.SERVICE_NAME;
	}

	@Override
	public PortletPreferences importData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws PortletDataException {

		boolean clearCache = WikiCacheThreadLocal.isClearCache();

		WikiCacheThreadLocal.setClearCache(false);

		try {
			return super.importData(
				portletDataContext, portletId, portletPreferences, data);
		}
		finally {
			WikiCacheThreadLocal.setClearCache(clearCache);

			_portalCache.removeAll();
		}
	}

	@Override
	public boolean isEnabled(long companyId) {
		return FeatureFlagManagerUtil.isEnabled(companyId, "LPD-35013");
	}

	@Activate
	protected void activate() {
		setDeletionSystemEventStagedModelTypes(
			new StagedModelType(WikiNode.class),
			new StagedModelType(WikiPage.class));
		setExportControls(
			new PortletDataHandlerBoolean(
				NAMESPACE, "wiki-nodes", false, true, null,
				WikiNode.class.getName()),
			new PortletDataHandlerBoolean(
				NAMESPACE, "wiki-pages", true, false,
				new PortletDataHandlerControl[] {
					new PortletDataHandlerBoolean(
						NAMESPACE, "referenced-content")
				},
				WikiPage.class.getName()));
		setStagingControls(getExportControls());

		_portalCache = _multiVMPool.getPortalCache(
			WikiPageDisplay.class.getName());
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletDataContext.addPrimaryKey(
				WikiPortletDataHandler.class, "deleteData")) {

			return portletPreferences;
		}

		_wikiNodeLocalService.deleteNodes(portletDataContext.getScopeGroupId());

		return portletPreferences;
	}

	@Override
	protected String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		Element rootElement = addExportDataRootElement(portletDataContext);

		if (!portletDataContext.getBooleanParameter(NAMESPACE, "wiki-pages")) {
			return getExportDataRootElementString(rootElement);
		}

		portletDataContext.addPortletPermissions(WikiConstants.RESOURCE_NAME);

		rootElement.addAttribute(
			"group-id", String.valueOf(portletDataContext.getScopeGroupId()));

		ActionableDynamicQuery nodeActionableDynamicQuery =
			_wikiNodeLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		nodeActionableDynamicQuery.performActions();

		ActionableDynamicQuery pageActionableDynamicQuery =
			_wikiPageLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		pageActionableDynamicQuery.performActions();

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		if (!portletDataContext.getBooleanParameter(NAMESPACE, "wiki-pages")) {
			return null;
		}

		portletDataContext.importPortletPermissions(
			WikiConstants.RESOURCE_NAME);

		Element nodesElement = portletDataContext.getImportDataGroupElement(
			WikiNode.class);

		List<Element> nodeElements = nodesElement.elements();

		for (Element nodeElement : nodeElements) {
			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, nodeElement);
		}

		Element pagesElement = portletDataContext.getImportDataGroupElement(
			WikiPage.class);

		List<Element> pageElements = pagesElement.elements();

		for (Element pageElement : pageElements) {
			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, pageElement);
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
					new StagedModelType(WikiNode.class.getName()),
					new StagedModelType(WikiPage.class.getName())
				});

			return;
		}

		ActionableDynamicQuery nodeActionableDynamicQuery =
			_wikiNodeLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		nodeActionableDynamicQuery.performCount();

		ActionableDynamicQuery pageExportActionableDynamicQuery =
			_wikiPageLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		pageExportActionableDynamicQuery.performCount();
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private MultiVMPool _multiVMPool;

	private PortalCache<?, ?> _portalCache;

	@Reference
	private Staging _staging;

	@Reference
	private WikiNodeLocalService _wikiNodeLocalService;

	@Reference
	private WikiPageLocalService _wikiPageLocalService;

}