/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.lar;

import com.liferay.exportimport.internal.data.handler.BatchEnginePortletDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.lar.DeletionSystemEventImporter;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.ElementHandler;
import com.liferay.portal.kernel.xml.ElementProcessor;

import java.io.StringReader;

import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * @author Zsolt Berentey
 */
@Component(service = DeletionSystemEventImporter.class)
public class DeletionSystemEventImporterImpl
	implements DeletionSystemEventImporter {

	public void importDeletionSystemEvents(
			final PortletDataContext portletDataContext)
		throws Exception {

		if (!MapUtil.getBoolean(
				portletDataContext.getParameterMap(),
				PortletDataHandlerKeys.DELETIONS)) {

			return;
		}

		String xml = portletDataContext.getZipEntryAsString(
			ExportImportPathUtil.getSourceRootPath(portletDataContext) +
				"/deletion-system-events.xml");

		if (xml == null) {
			return;
		}

		XMLReader xmlReader = SecureXMLFactoryProviderUtil.newXMLReader();

		ElementHandler elementHandler = new ElementHandler(
			new ElementProcessor() {

				@Override
				public void processElement(Element element) {
					_importDeletionSystemEvents(portletDataContext, element);
				}

			},
			new String[] {"deletion-system-event"});

		xmlReader.setContentHandler(elementHandler);

		xmlReader.parse(new InputSource(new StringReader(xml)));

		_importBatchDeletions(portletDataContext);
	}

	private void _importBatchDeletions(PortletDataContext portletDataContext)
		throws Exception {

		Element rootElement = portletDataContext.getImportDataRootElement();

		Element sitePortletsElement = rootElement.element("site-portlets");

		if (sitePortletsElement == null) {
			return;
		}

		for (Element portletElement : sitePortletsElement.elements()) {
			String portletId = portletElement.attributeValue("portlet-id");

			Portlet portlet = _portletLocalService.getPortletById(
				portletDataContext.getCompanyId(), portletId);

			if (!portlet.isActive() || portlet.isUndeployedPortlet()) {
				continue;
			}

			PortletDataHandler portletDataHandler =
				_portletDataHandlerProvider.provide(portletId);

			if (portletDataHandler instanceof BatchEnginePortletDataHandler) {
				portletDataHandler.deleteData(
					portletDataContext, portletId, null);
			}
		}
	}

	private void _importDeletionSystemEvents(
		PortletDataContext portletDataContext, Element element) {

		StagedModelType stagedModelType = new StagedModelType(
			element.attributeValue("class-name"),
			element.attributeValue("referrer-class-name"));

		if (!_shouldImportDeletionSystemEvent(
				portletDataContext, stagedModelType)) {

			return;
		}

		try {
			StagedModelDataHandlerUtil.deleteStagedModel(
				portletDataContext, element);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to process deletion for ", stagedModelType,
						" with UUID ", element.attributeValue("uuid")),
					exception);
			}
		}
	}

	private boolean _shouldImportDeletionSystemEvent(
		PortletDataContext portletDataContext,
		StagedModelType stagedModelType) {

		Set<StagedModelType> stagedModelTypes =
			portletDataContext.getDeletionSystemEventStagedModelTypes();

		if (stagedModelTypes.contains(stagedModelType)) {
			return true;
		}

		for (StagedModelType curStagedModelType : stagedModelTypes) {
			if ((curStagedModelType.getClassNameId() ==
					stagedModelType.getClassNameId()) &&
				(StagedModelType.REFERRER_CLASS_NAME_ALL.equals(
					curStagedModelType.getReferrerClassName()) ||
				 (Validator.isNotNull(stagedModelType.getReferrerClassName()) &&
				  StagedModelType.REFERRER_CLASS_NAME_ANY.equals(
					  curStagedModelType.getReferrerClassName())))) {

				return true;
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeletionSystemEventImporterImpl.class);

	@Reference
	private PortletDataHandlerProvider _portletDataHandlerProvider;

	@Reference
	private PortletLocalService _portletLocalService;

}