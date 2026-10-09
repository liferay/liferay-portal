/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.exportimport.internal.staged.model.repository;

import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.model.adapter.StagedExpandoTable;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.model.adapter.util.ModelAdapterUtil;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Akos Thurzo
 */
@Component(
	property = "model.class.name=com.liferay.expando.model.adapter.StagedExpandoTable",
	service = StagedModelRepository.class
)
public class StagedExpandoTableStagedModelRepository
	implements StagedModelRepository<StagedExpandoTable> {

	@Override
	public StagedExpandoTable addStagedModel(
			PortletDataContext portletDataContext,
			StagedExpandoTable stagedExpandoTable)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTableLocalService.addTable(
			portletDataContext.getCompanyId(),
			stagedExpandoTable.getClassName(), stagedExpandoTable.getName());

		return ModelAdapterUtil.adapt(
			expandoTable, ExpandoTable.class, StagedExpandoTable.class);
	}

	@Override
	public void deleteStagedModel(StagedExpandoTable stagedExpandoTable)
		throws PortalException {

		_expandoTableLocalService.deleteTable(stagedExpandoTable);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {
	}

	@Override
	public void deleteStagedModels(PortletDataContext portletDataContext)
		throws PortalException {

		List<ExpandoTable> expandoTables =
			_expandoTableLocalService.getExpandoTables(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (ExpandoTable expandoTable : expandoTables) {
			if (expandoTable.getCompanyId() !=
					portletDataContext.getCompanyId()) {

				continue;
			}

			_expandoTableLocalService.deleteTable(expandoTable);
		}
	}

	@Override
	public StagedExpandoTable fetchMissingReference(String uuid, long groupId) {
		return null;
	}

	@Override
	public StagedExpandoTable fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return null;
	}

	@Override
	public List<StagedExpandoTable> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		DynamicQuery dynamicQuery = _expandoTableLocalService.dynamicQuery();

		Property companyIdProperty = PropertyFactoryUtil.forName("companyId");

		dynamicQuery.add(companyIdProperty.eq(companyId));

		Property classNameIdProperty = PropertyFactoryUtil.forName(
			"classNameId");

		String className = _parseClassName(uuid);

		dynamicQuery.add(
			classNameIdProperty.eq(_portal.getClassNameId(className)));

		Property nameProperty = PropertyFactoryUtil.forName("name");

		String name = _parseName(uuid);

		dynamicQuery.add(nameProperty.eq(name));

		List<ExpandoTable> expandoTables =
			_expandoTableLocalService.dynamicQuery(dynamicQuery);

		if (ListUtil.isNotEmpty(expandoTables)) {
			return ModelAdapterUtil.adapt(
				expandoTables, ExpandoTable.class, StagedExpandoTable.class);
		}

		return Collections.emptyList();
	}

	@Override
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		final PortletDataContext portletDataContext) {

		ExportActionableDynamicQuery exportActionableDynamicQuery =
			new ExportActionableDynamicQuery() {

				@Override
				public long performCount() throws PortalException {
					ManifestSummary manifestSummary =
						portletDataContext.getManifestSummary();

					StagedModelType stagedModelType = getStagedModelType();

					long modelAdditionCount = super.performCount();

					manifestSummary.addModelAdditionCount(
						stagedModelType, modelAdditionCount);

					long modelDeletionCount =
						_exportImportHelper.getModelDeletionCount(
							portletDataContext, stagedModelType);

					manifestSummary.addModelDeletionCount(
						stagedModelType, modelDeletionCount);

					return modelAdditionCount;
				}

			};

		exportActionableDynamicQuery.setBaseLocalService(
			_expandoTableLocalService);

		Class<? extends ExpandoTableLocalService>
			expandoTableLocalServiceClass =
				_expandoTableLocalService.getClass();

		exportActionableDynamicQuery.setClassLoader(
			expandoTableLocalServiceClass.getClassLoader());

		exportActionableDynamicQuery.setCompanyId(
			portletDataContext.getCompanyId());
		exportActionableDynamicQuery.setModelClass(ExpandoTable.class);
		exportActionableDynamicQuery.setPerformActionMethod(
			(ExpandoTable expandoTable) -> {
				ClassName className = _classNameLocalService.fetchClassName(
					expandoTable.getClassNameId());

				if (className == null) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"No class name exists for class name ID " +
								expandoTable.getClassNameId());
					}

					return;
				}

				StagedExpandoTable stagedExpandoTable = ModelAdapterUtil.adapt(
					expandoTable, ExpandoTable.class, StagedExpandoTable.class);

				StagedModelDataHandlerUtil.exportStagedModel(
					portletDataContext, stagedExpandoTable);
			});
		exportActionableDynamicQuery.setPrimaryKeyPropertyName("tableId");
		exportActionableDynamicQuery.setStagedModelType(
			new StagedModelType(StagedExpandoTable.class));

		return exportActionableDynamicQuery;
	}

	@Override
	public void restoreStagedModel(
			PortletDataContext portletDataContext,
			StagedExpandoTable stagedModel)
		throws PortletDataException {
	}

	@Override
	public StagedExpandoTable saveStagedModel(
			StagedExpandoTable stagedExpandoTable)
		throws PortalException {

		ExpandoTable expandoTable =
			_expandoTableLocalService.updateExpandoTable(stagedExpandoTable);

		return ModelAdapterUtil.adapt(
			expandoTable, ExpandoTable.class, StagedExpandoTable.class);
	}

	@Override
	public StagedExpandoTable updateStagedModel(
			PortletDataContext portletDataContext,
			StagedExpandoTable stagedExpandoTable)
		throws PortalException {

		throw new UnsupportedOperationException();
	}

	private String _parseClassName(String uuid) {
		return uuid.substring(0, uuid.indexOf(StringPool.POUND));
	}

	private String _parseName(String uuid) {
		return uuid.substring(uuid.indexOf(StringPool.POUND) + 1);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StagedExpandoTableStagedModelRepository.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private Portal _portal;

}