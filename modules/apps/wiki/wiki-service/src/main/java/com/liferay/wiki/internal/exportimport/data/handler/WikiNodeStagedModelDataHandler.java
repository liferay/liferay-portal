/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.internal.exportimport.data.handler;

import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.wiki.configuration.WikiGroupServiceConfiguration;
import com.liferay.wiki.constants.WikiConstants;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.service.WikiNodeLocalService;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Zsolt Berentey
 */
@Component(service = StagedModelDataHandler.class)
public class WikiNodeStagedModelDataHandler
	extends BaseStagedModelDataHandler<WikiNode> {

	public static final String[] CLASS_NAMES = {WikiNode.class.getName()};

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		WikiNode wikiNode = fetchStagedModelByUuidAndGroupId(uuid, groupId);

		if (wikiNode != null) {
			deleteStagedModel(wikiNode);
		}
	}

	@Override
	public void deleteStagedModel(WikiNode node) throws PortalException {
		_wikiNodeLocalService.deleteNode(node);
	}

	@Override
	public WikiNode fetchStagedModelByExternalReferenceCodeAndGroupId(
		String externalReferenceCode, long groupId) {

		return _wikiNodeLocalService.fetchWikiNodeByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	@Override
	public WikiNode fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _wikiNodeLocalService.fetchWikiNodeByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public List<WikiNode> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _wikiNodeLocalService.getWikiNodesByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public boolean isEnabled(long companyId) {
		return FeatureFlagManagerUtil.isEnabled(companyId, "LPD-35013");
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, WikiNode node)
		throws Exception {

		Element nodeElement = portletDataContext.getExportDataElement(node);

		portletDataContext.addClassedModel(
			nodeElement, ExportImportPathUtil.getModelPath(node), node);
	}

	@Override
	protected void doImportMissingReference(
			PortletDataContext portletDataContext, String uuid, long groupId,
			long nodeId)
		throws Exception {

		WikiNode existingNode = fetchMissingReference(uuid, groupId);

		if (existingNode == null) {
			return;
		}

		Map<Long, Long> nodeIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				WikiNode.class);

		nodeIds.put(nodeId, existingNode.getNodeId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, WikiNode node)
		throws Exception {

		long userId = portletDataContext.getUserId(node.getUserUuid());

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			node);

		serviceContext.setAttribute("lastPostDate", node.getLastPostDate());

		WikiNode importedNode = null;

		WikiNode existingNode = fetchExistingStagedModel(
			node, portletDataContext.getScopeGroupId());

		String nodeName = node.getName();

		WikiNode nodeWithSameName = _wikiNodeLocalService.fetchNode(
			portletDataContext.getScopeGroupId(), nodeName);

		if (portletDataContext.isDataStrategyMirror()) {
			if (existingNode == null) {
				if (nodeWithSameName != null) {
					nodeName = _getNodeName(
						portletDataContext, node, nodeName, 2);
				}

				serviceContext.setUuid(node.getUuid());

				importedNode = _wikiNodeLocalService.addNode(
					node.getExternalReferenceCode(), userId, nodeName,
					node.getDescription(), serviceContext);
			}
			else {
				String uuid = existingNode.getUuid();

				if ((nodeWithSameName != null) &&
					!uuid.equals(nodeWithSameName.getUuid())) {

					nodeName = _getNodeName(
						portletDataContext, node, nodeName, 2);
				}

				importedNode = _wikiNodeLocalService.updateNode(
					existingNode.getNodeId(), nodeName, node.getDescription(),
					serviceContext);

				importedNode.setUuid(node.getUuid());

				importedNode = _wikiNodeLocalService.updateWikiNode(
					importedNode);
			}
		}
		else {
			WikiGroupServiceConfiguration wikiGroupServiceConfiguration =
				_configurationProvider.getConfiguration(
					WikiGroupServiceConfiguration.class,
					new GroupServiceSettingsLocator(
						portletDataContext.getScopeGroupId(),
						WikiConstants.SERVICE_NAME));

			String initialNodeName =
				wikiGroupServiceConfiguration.initialNodeName();

			if ((existingNode != null) &&
				initialNodeName.equals(existingNode.getName())) {

				importedNode = _wikiNodeLocalService.updateNode(
					existingNode.getNodeId(), nodeName, node.getDescription(),
					serviceContext);
			}
			else {
				importedNode = _wikiNodeLocalService.addNode(
					node.getExternalReferenceCode(), userId,
					_getNodeName(portletDataContext, node, nodeName, 2),
					node.getDescription(), serviceContext);
			}
		}

		portletDataContext.importClassedModel(node, importedNode);
	}

	@Override
	protected void doRestoreStagedModel(
			PortletDataContext portletDataContext, WikiNode node)
		throws Exception {

		WikiNode existingNode = fetchStagedModelByUuidAndGroupId(
			node.getUuid(), portletDataContext.getScopeGroupId());

		if ((existingNode == null) || !existingNode.isInTrash()) {
			return;
		}

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			WikiNode.class.getName());

		if (trashHandler.isRestorable(existingNode.getNodeId())) {
			trashHandler.restoreTrashEntry(
				portletDataContext.getUserId(node.getUserUuid()),
				existingNode.getNodeId());
		}
	}

	private String _getNodeName(
			PortletDataContext portletDataContext, WikiNode node, String name,
			int count)
		throws Exception {

		WikiNode existingNode = _wikiNodeLocalService.fetchNode(
			portletDataContext.getScopeGroupId(), name);

		if (existingNode == null) {
			return name;
		}

		String nodeName = node.getName();

		return _getNodeName(
			portletDataContext, node,
			StringBundler.concat(nodeName, StringPool.SPACE, count), ++count);
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private WikiNodeLocalService _wikiNodeLocalService;

}