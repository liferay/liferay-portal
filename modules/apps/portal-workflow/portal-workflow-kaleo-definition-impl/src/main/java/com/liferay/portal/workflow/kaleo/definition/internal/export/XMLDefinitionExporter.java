/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.internal.export;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.workflow.kaleo.definition.Definition;
import com.liferay.portal.workflow.kaleo.definition.Node;
import com.liferay.portal.workflow.kaleo.definition.NodeType;
import com.liferay.portal.workflow.kaleo.definition.exception.KaleoDefinitionValidationException;
import com.liferay.portal.workflow.kaleo.definition.export.DefinitionExporter;
import com.liferay.portal.workflow.kaleo.definition.export.NodeExporter;
import com.liferay.portal.workflow.kaleo.definition.export.builder.DefinitionBuilder;

import java.io.IOException;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = DefinitionExporter.class)
public class XMLDefinitionExporter implements DefinitionExporter {

	@Override
	public String export(Definition definition) throws PortalException {
		return _export(definition);
	}

	@Override
	public String export(long kaleoDefinitionId) throws PortalException {
		Definition definition = _definitionBuilder.buildDefinition(
			kaleoDefinitionId);

		return _export(definition);
	}

	@Override
	public String export(long companyId, String name, int version)
		throws PortalException {

		Definition definition = _definitionBuilder.buildDefinition(
			companyId, name, version);

		return _export(definition);
	}

	public void setVersion(String version) {
		_version = version;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_namespace = "urn:liferay.com:liferay-workflow_" + _version;
		_schemaVersion = StringUtil.replace(
			_version, CharPool.PERIOD, CharPool.UNDERLINE);

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, NodeExporter.class, null,
			ServiceReferenceMapperFactory.create(
				bundleContext,
				(nodeExporter, emitter) -> emitter.emit(
					nodeExporter.getNodeType())));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private String _export(Definition definition)
		throws KaleoDefinitionValidationException {

		try {
			Document document = SAXReaderUtil.createDocument();

			Element workflowDefinitionElement = document.addElement(
				"workflow-definition");

			workflowDefinitionElement.addAttribute(
				"xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			workflowDefinitionElement.addAttribute(
				"xsi:schemaLocation",
				StringBundler.concat(
					"urn:liferay.com:liferay-workflow_", _version,
					" http://www.liferay.com/dtd/liferay-workflow-definition_",
					_schemaVersion, ".xsd"));
			workflowDefinitionElement.addNamespace(
				"", "urn:liferay.com:liferay-workflow_" + _version);

			Element nameElement = workflowDefinitionElement.addElement(
				"name", _namespace);

			nameElement.addText(definition.getName());

			if (Validator.isNotNull(definition.getDescription())) {
				Element descriptionElement =
					workflowDefinitionElement.addElement(
						"description", _namespace);

				descriptionElement.addText(definition.getDescription());
			}

			Element versionElement = workflowDefinitionElement.addElement(
				"version", _namespace);

			versionElement.addText(String.valueOf(definition.getVersion()));

			Collection<Node> nodes = definition.getNodes();

			for (Node node : nodes) {
				NodeExporter nodeExporter = _serviceTrackerMap.getService(
					node.getNodeType());

				nodeExporter.exportNode(
					node, workflowDefinitionElement, _namespace);
			}

			return document.formattedString();
		}
		catch (IOException ioException) {
			throw new SystemException(
				"Unable to export definition", ioException);
		}
	}

	@Reference
	private DefinitionBuilder _definitionBuilder;

	private String _namespace;
	private String _schemaVersion;
	private ServiceTrackerMap<NodeType, NodeExporter> _serviceTrackerMap;
	private String _version = ReleaseInfo.getVersion();

}