/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.internal.exportimport.portlet.preferences.processor;

import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.wiki.constants.WikiConstants;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiPageLocalService;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = "jakarta.portlet.name=" + WikiPortletKeys.WIKI,
	service = ExportImportPortletPreferencesProcessor.class
)
public class WikiExportImportPortletPreferencesProcessor
	implements ExportImportPortletPreferencesProcessor {

	@Override
	public List<Capability> getExportCapabilities() {
		return ListUtil.fromArray(
			_portletDisplayTemplateExporter,
			_wikiCommentsAndRatingsExporterImporterCapability);
	}

	@Override
	public List<Capability> getImportCapabilities() {
		return ListUtil.fromArray(
			_wikiCommentsAndRatingsExporterImporterCapability,
			_referencedStagedModelImporter, _portletDisplayTemplateImporter);
	}

	@Override
	public PortletPreferences processExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		if (!_exportImportHelper.isExportPortletData(portletDataContext) ||
			!portletDataContext.getBooleanParameter(
				_wikiPortletDataHandler.getNamespace(), "wiki-pages")) {

			return portletPreferences;
		}

		try {
			portletDataContext.addPortletPermissions(
				WikiConstants.RESOURCE_NAME);
		}
		catch (PortalException portalException) {
			PortletDataException portletDataException =
				new PortletDataException(portalException);

			portletDataException.setPortletId(WikiPortletKeys.WIKI);
			portletDataException.setType(
				PortletDataException.EXPORT_PORTLET_PERMISSIONS);

			throw portletDataException;
		}

		try {
			String portletId = portletDataContext.getPortletId();

			ActionableDynamicQuery nodeActionableDynamicQuery =
				_wikiNodeLocalService.getExportActionableDynamicQuery(
					portletDataContext);

			nodeActionableDynamicQuery.setPerformActionMethod(
				(WikiNode wikiNode) ->
					StagedModelDataHandlerUtil.exportReferenceStagedModel(
						portletDataContext, portletId, wikiNode));

			nodeActionableDynamicQuery.performActions();

			ActionableDynamicQuery pageActionableDynamicQuery =
				_wikiPageLocalService.getExportActionableDynamicQuery(
					portletDataContext);

			pageActionableDynamicQuery.setPerformActionMethod(
				(WikiPage wikiPage) ->
					StagedModelDataHandlerUtil.exportReferenceStagedModel(
						portletDataContext, portletId, wikiPage));

			pageActionableDynamicQuery.performActions();
		}
		catch (PortalException portalException) {
			PortletDataException portletDataException =
				new PortletDataException(portalException);

			portletDataException.setPortletId(WikiPortletKeys.WIKI);
			portletDataException.setType(
				PortletDataException.EXPORT_PORTLET_DATA);

			throw portletDataException;
		}

		Group group = _groupLocalService.fetchGroup(
			portletDataContext.getGroupId());

		String hiddenNodeNames = portletPreferences.getValue(
			"hiddenNodes", null);

		for (String hiddenNodeName : StringUtil.split(hiddenNodeNames)) {
			_exportNode(portletDataContext, group, hiddenNodeName);
		}

		String visibleNodeNames = portletPreferences.getValue(
			"visibleNodes", null);

		for (String visibleNodeName : StringUtil.split(visibleNodeNames)) {
			_exportNode(portletDataContext, group, visibleNodeName);
		}

		return portletPreferences;
	}

	@Override
	public PortletPreferences processImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		if (!portletDataContext.getBooleanParameter(
				_wikiPortletDataHandler.getNamespace(), "wiki-pages")) {

			return portletPreferences;
		}

		try {
			portletDataContext.importPortletPermissions(
				WikiConstants.RESOURCE_NAME);
		}
		catch (PortalException portalException) {
			PortletDataException portletDataException =
				new PortletDataException(portalException);

			portletDataException.setPortletId(WikiPortletKeys.WIKI);
			portletDataException.setType(
				PortletDataException.IMPORT_PORTLET_PERMISSIONS);

			throw portletDataException;
		}

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

		return portletPreferences;
	}

	private void _exportNode(
			PortletDataContext portletDataContext, Group group, String nodeName)
		throws PortletDataException {

		if (ExportImportThreadLocal.isStagingInProcess() &&
			!group.isStagedPortlet(portletDataContext.getPortletId())) {

			return;
		}

		WikiNode node = _wikiNodeLocalService.fetchNode(
			portletDataContext.getScopeGroupId(), nodeName);

		if (node == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to export referenced wiki node " + nodeName);
			}

			return;
		}

		StagedModelDataHandlerUtil.exportReferenceStagedModel(
			portletDataContext, portletDataContext.getPortletId(), node);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WikiExportImportPortletPreferencesProcessor.class);

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(target = "(name=PortletDisplayTemplateExporter)")
	private Capability _portletDisplayTemplateExporter;

	@Reference(target = "(name=PortletDisplayTemplateImporter)")
	private Capability _portletDisplayTemplateImporter;

	@Reference(target = "(name=ReferencedStagedModelImporter)")
	private Capability _referencedStagedModelImporter;

	@Reference(
		target = "(component.name=com.liferay.wiki.internal.exportimport.portlet.preferences.processor.WikiCommentsAndRatingsExporterImporterCapability)"
	)
	private Capability _wikiCommentsAndRatingsExporterImporterCapability;

	@Reference
	private WikiNodeLocalService _wikiNodeLocalService;

	@Reference
	private WikiPageLocalService _wikiPageLocalService;

	@Reference(target = "(jakarta.portlet.name=" + WikiPortletKeys.WIKI + ")")
	private PortletDataHandler _wikiPortletDataHandler;

}