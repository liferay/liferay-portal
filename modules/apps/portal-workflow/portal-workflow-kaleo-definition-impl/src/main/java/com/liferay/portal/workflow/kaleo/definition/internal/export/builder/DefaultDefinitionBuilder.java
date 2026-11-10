/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.internal.export.builder;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.workflow.kaleo.definition.Definition;
import com.liferay.portal.workflow.kaleo.definition.Node;
import com.liferay.portal.workflow.kaleo.definition.Transition;
import com.liferay.portal.workflow.kaleo.definition.export.builder.DefinitionBuilder;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoTransition;
import com.liferay.portal.workflow.kaleo.service.KaleoConditionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNodeLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskFormLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTransitionLocalService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = DefinitionBuilder.class)
public class DefaultDefinitionBuilder implements DefinitionBuilder {

	@Override
	public Definition buildDefinition(long kaleoDefinitionId)
		throws PortalException {

		KaleoDefinition kaleoDefinition =
			_kaleoDefinitionLocalService.getKaleoDefinition(kaleoDefinitionId);

		return _buildDefinition(
			_kaleoDefinitionVersionLocalService.getKaleoDefinitionVersion(
				kaleoDefinition.getCompanyId(), kaleoDefinition.getName(),
				StringBundler.concat(
					kaleoDefinition.getVersion(), CharPool.PERIOD, 0)));
	}

	@Override
	public Definition buildDefinition(long companyId, String name, int version)
		throws PortalException {

		return _buildDefinition(
			_kaleoDefinitionVersionLocalService.getKaleoDefinitionVersion(
				companyId, name,
				StringBundler.concat(version, CharPool.PERIOD, 0)));
	}

	@Activate
	protected void activate() {
		_addNodeBuilder(new ConditionNodeBuilder(_kaleoConditionLocalService));
		_addNodeBuilder(new ForkNodeBuilder());
		_addNodeBuilder(new JoinNodeBuilder());
		_addNodeBuilder(new StateNodeBuilder());
		_addNodeBuilder(
			new TaskNodeBuilder(
				_kaleoTaskFormLocalService, _kaleoTaskLocalService));
		_addNodeBuilder(new JoinXorNodeBuilder());
	}

	private void _addNodeBuilder(NodeBuilder<? extends Node> nodeBuilder) {
		_nodeBuilders.put(
			String.valueOf(nodeBuilder.getNodeType()), nodeBuilder);
	}

	private Definition _buildDefinition(
			KaleoDefinitionVersion kaleoDefinitionVersion)
		throws PortalException {

		Definition definition = new Definition(
			kaleoDefinitionVersion.getName(),
			kaleoDefinitionVersion.getDescription(),
			kaleoDefinitionVersion.getContent(),
			_getVersion(kaleoDefinitionVersion.getVersion()));

		List<KaleoNode> kaleoNodes =
			_kaleoNodeLocalService.getKaleoDefinitionVersionKaleoNodes(
				kaleoDefinitionVersion.getKaleoDefinitionVersionId());

		for (KaleoNode kaleoNode : kaleoNodes) {
			NodeBuilder<? extends Node> nodeBuilder = _nodeBuilders.get(
				kaleoNode.getType());

			Node node = nodeBuilder.buildNode(kaleoNode);

			definition.addNode(node);
		}

		List<KaleoTransition> kaleoTransitions =
			_kaleoTransitionLocalService.
				getKaleoDefinitionVersionKaleoTransitions(
					kaleoDefinitionVersion.getKaleoDefinitionVersionId());

		for (KaleoTransition kaleoTransition : kaleoTransitions) {
			String sourceNodeName = kaleoTransition.getSourceKaleoNodeName();

			Node sourceNode = definition.getNode(sourceNodeName);

			String targetNodeName = kaleoTransition.getTargetKaleoNodeName();

			Node targetNode = definition.getNode(targetNodeName);

			Transition transition = new Transition(
				kaleoTransition.isDefaultTransition(),
				kaleoTransition.getLabelMap(), kaleoTransition.getName(),
				sourceNode, targetNode);

			sourceNode.addOutgoingTransition(transition);

			targetNode.addIncomingTransition(transition);
		}

		return definition;
	}

	private int _getVersion(String version) {
		int[] versionParts = StringUtil.split(version, StringPool.PERIOD, 0);

		return versionParts[0];
	}

	@Reference
	private KaleoConditionLocalService _kaleoConditionLocalService;

	@Reference
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Reference
	private KaleoDefinitionVersionLocalService
		_kaleoDefinitionVersionLocalService;

	@Reference
	private KaleoNodeLocalService _kaleoNodeLocalService;

	@Reference
	private KaleoTaskFormLocalService _kaleoTaskFormLocalService;

	@Reference
	private KaleoTaskLocalService _kaleoTaskLocalService;

	@Reference
	private KaleoTransitionLocalService _kaleoTransitionLocalService;

	private final Map<String, NodeBuilder<? extends Node>> _nodeBuilders =
		new HashMap<>();

}