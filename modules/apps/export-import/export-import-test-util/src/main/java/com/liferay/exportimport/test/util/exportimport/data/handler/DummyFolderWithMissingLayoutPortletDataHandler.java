/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test.util.exportimport.data.handler;

import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.portlet.data.handler.helper.PortletDataHandlerHelper;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.exportimport.test.util.internal.exportimport.staged.model.repository.DummyFolderStagedModelRepository;
import com.liferay.exportimport.test.util.model.DummyFolder;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.xml.Element;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Akos Thurzo
 */
@Component(service = PortletDataHandler.class)
public class DummyFolderWithMissingLayoutPortletDataHandler
	extends BasePortletDataHandler {

	public static final String NAMESPACE = "missing-dummy-folder";

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Override
	public boolean validateSchemaVersion(String schemaVersion) {
		return _portletDataHandlerHelper.validateSchemaVersion(
			schemaVersion, getSchemaVersion());
	}

	@Activate
	protected void activate() {
		setDeletionSystemEventStagedModelTypes(
			new StagedModelType(DummyFolder.class));
		setRank(110);
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletDataContext.addPrimaryKey(
				DummyFolderWithMissingLayoutPortletDataHandler.class,
				"deleteData")) {

			return portletPreferences;
		}

		_dummyFolderStagedModelRepository.deleteStagedModels(
			portletDataContext);

		return portletPreferences;
	}

	@Override
	protected String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		Element rootElement = addExportDataRootElement(portletDataContext);

		DummyFolderStagedModelRepository dummyFolderStagedModelRepository =
			(DummyFolderStagedModelRepository)_dummyFolderStagedModelRepository;

		List<DummyFolder> dummyFolders =
			dummyFolderStagedModelRepository.getDummyFolders(
				portletDataContext.getScopeGroupId());

		List<Layout> layouts = _layoutLocalService.getLayouts(
			portletDataContext.getScopeGroupId(), false);

		Layout layout = layouts.get(0);

		for (DummyFolder dummyFolder : dummyFolders) {
			Element dummyFolderElement =
				portletDataContext.getExportDataElement(dummyFolder);

			portletDataContext.addReferenceElement(
				dummyFolder, dummyFolderElement, layout,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY, true);

			portletDataContext.addClassedModel(
				dummyFolderElement,
				ExportImportPathUtil.getModelPath(dummyFolder), dummyFolder);
		}

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		if (!portletDataContext.getBooleanParameter(NAMESPACE, "entries")) {
			return null;
		}

		Element dummiesElement = portletDataContext.getImportDataGroupElement(
			DummyFolder.class);

		List<Element> dummyFolderElements = dummiesElement.elements();

		for (Element dummyFolderElement : dummyFolderElements) {
			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, dummyFolderElement);
		}

		return null;
	}

	@Override
	protected void doPrepareManifestSummary(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {

		ActionableDynamicQuery entryExportActionableDynamicQuery =
			_dummyFolderStagedModelRepository.getExportActionableDynamicQuery(
				portletDataContext);

		entryExportActionableDynamicQuery.performCount();
	}

	@Reference(
		target = "(model.class.name=com.liferay.exportimport.test.util.model.DummyFolder)"
	)
	private StagedModelRepository<DummyFolder>
		_dummyFolderStagedModelRepository;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private PortletDataHandlerHelper _portletDataHandlerHelper;

}