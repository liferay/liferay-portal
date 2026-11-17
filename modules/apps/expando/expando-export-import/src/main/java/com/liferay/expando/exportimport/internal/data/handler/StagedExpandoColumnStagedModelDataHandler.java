/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.exportimport.internal.data.handler;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.adapter.StagedExpandoColumn;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.model.adapter.StagedExpandoTable;
import com.liferay.exportimport.kernel.lar.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.exportimport.util.ScopeUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.model.adapter.util.ModelAdapterUtil;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Akos Thurzo
 */
@Component(service = StagedModelDataHandler.class)
public class StagedExpandoColumnStagedModelDataHandler
	extends BaseStagedModelDataHandler<StagedExpandoColumn> {

	public static final String[] CLASS_NAMES = {
		StagedExpandoColumn.class.getName()
	};

	@Override
	public void deleteStagedModel(StagedExpandoColumn stagedExpandoColumn)
		throws PortalException {

		_stagedModelRepository.deleteStagedModel(stagedExpandoColumn);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		_stagedModelRepository.deleteStagedModel(
			uuid, groupId, className, extraData);
	}

	@Override
	public List<StagedExpandoColumn> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _stagedModelRepository.fetchStagedModelsByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public boolean validateReference(
		PortletDataContext portletDataContext, Element referenceElement) {

		validateMissingGroupReference(portletDataContext, referenceElement);

		String uuid = referenceElement.attributeValue("uuid");

		List<StagedExpandoColumn> stagedExpandoColumns =
			fetchStagedModelsByUuidAndCompanyId(
				uuid, portletDataContext.getCompanyId());

		return ListUtil.isNotEmpty(stagedExpandoColumns);
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext,
			StagedExpandoColumn stagedExpandoColumn)
		throws Exception {

		ExpandoTable expandoTable = _expandoTableLocalService.getTable(
			stagedExpandoColumn.getTableId());

		StagedExpandoTable stagedExpandoTable = ModelAdapterUtil.adapt(
			expandoTable, ExpandoTable.class, StagedExpandoTable.class);

		StagedModelDataHandlerUtil.exportReferenceStagedModel(
			portletDataContext, stagedExpandoColumn, stagedExpandoTable,
			PortletDataContext.REFERENCE_TYPE_PARENT);

		Element stagedExpandoColumnElement =
			portletDataContext.getExportDataElement(stagedExpandoColumn);

		portletDataContext.addClassedModel(
			stagedExpandoColumnElement,
			ExportImportPathUtil.getModelPath(stagedExpandoColumn),
			stagedExpandoColumn);
	}

	@Override
	protected void doImportMissingReference(
			PortletDataContext portletDataContext, Element referenceElement)
		throws PortletDataException {

		importMissingGroupReference(portletDataContext, referenceElement);

		String uuid = referenceElement.attributeValue("uuid");

		List<StagedExpandoColumn> stagedExpandoColumns =
			fetchStagedModelsByUuidAndCompanyId(
				uuid, portletDataContext.getCompanyId());

		if (ListUtil.isEmpty(stagedExpandoColumns)) {
			return;
		}

		StagedExpandoColumn existingStagedExpandoColumn =
			stagedExpandoColumns.get(0);

		Map<Long, Long> columnIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				ExpandoColumn.class);

		long columnId = GetterUtil.getLong(
			referenceElement.attributeValue("class-pk"));

		columnIds.put(columnId, existingStagedExpandoColumn.getColumnId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext,
			StagedExpandoColumn stagedExpandoColumn)
		throws Exception {

		ScopeUtil.validateInstanceScoped(portletDataContext);

		Map<Long, Long> tableIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				ExpandoTable.class);

		long tableId = MapUtil.getLong(
			tableIds, stagedExpandoColumn.getTableId(),
			stagedExpandoColumn.getTableId());

		StagedExpandoColumn importedExpandoColumn =
			(StagedExpandoColumn)stagedExpandoColumn.clone();

		importedExpandoColumn.setTableId(tableId);

		List<StagedExpandoColumn> stagedExpandoColumns =
			_stagedModelRepository.fetchStagedModelsByUuidAndCompanyId(
				stagedExpandoColumn.getUuid(),
				portletDataContext.getCompanyId());

		if (ListUtil.isEmpty(stagedExpandoColumns)) {
			importedExpandoColumn = _stagedModelRepository.addStagedModel(
				portletDataContext, importedExpandoColumn);
		}
		else {
			StagedExpandoColumn existingExpandoColumn =
				stagedExpandoColumns.get(0);

			importedExpandoColumn.setColumnId(
				existingExpandoColumn.getColumnId());

			importedExpandoColumn = _stagedModelRepository.updateStagedModel(
				portletDataContext, importedExpandoColumn);
		}

		portletDataContext.importClassedModel(
			stagedExpandoColumn, importedExpandoColumn);
	}

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.expando.kernel.model.adapter.StagedExpandoColumn)"
	)
	private StagedModelRepository<StagedExpandoColumn> _stagedModelRepository;

}