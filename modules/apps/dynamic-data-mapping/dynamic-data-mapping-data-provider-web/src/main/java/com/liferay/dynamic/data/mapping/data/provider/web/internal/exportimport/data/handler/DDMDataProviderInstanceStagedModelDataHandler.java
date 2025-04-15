/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.data.provider.web.internal.exportimport.data.handler;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProvider;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderRegistry;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelModifiedDateComparator;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.xml.Element;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dylan Rebelak
 */
@Component(
	property = "jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_DATA_PROVIDER,
	service = StagedModelDataHandler.class
)
public class DDMDataProviderInstanceStagedModelDataHandler
	extends BaseStagedModelDataHandler<DDMDataProviderInstance> {

	public static final String[] CLASS_NAMES = {
		DDMDataProviderInstance.class.getName()
	};

	@Override
	public void deleteStagedModel(DDMDataProviderInstance dataProviderInstance)
		throws PortalException {

		_ddmDataProviderInstanceLocalService.deleteDataProviderInstance(
			dataProviderInstance);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		DDMDataProviderInstance dataProviderInstance =
			fetchStagedModelByUuidAndGroupId(uuid, groupId);

		if (dataProviderInstance != null) {
			deleteStagedModel(dataProviderInstance);
		}
	}

	@Override
	public DDMDataProviderInstance fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _ddmDataProviderInstanceLocalService.
			fetchDDMDataProviderInstanceByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public List<DDMDataProviderInstance> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _ddmDataProviderInstanceLocalService.
			getDDMDataProviderInstancesByUuidAndCompanyId(
				uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new StagedModelModifiedDateComparator
					<DDMDataProviderInstance>());
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(DDMDataProviderInstance dataProviderInstance) {
		return dataProviderInstance.getNameCurrentValue();
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext,
			DDMDataProviderInstance dataProviderInstance)
		throws Exception {

		Element dataProviderInstanceElement =
			portletDataContext.getExportDataElement(dataProviderInstance);

		portletDataContext.addClassedModel(
			dataProviderInstanceElement,
			ExportImportPathUtil.getModelPath(dataProviderInstance),
			dataProviderInstance);
	}

	@Override
	protected void doImportMissingReference(
		PortletDataContext portletDataContext, String uuid, long groupId,
		long ddmDataProviderInstanceId) {

		DDMDataProviderInstance existingDDMDataProviderInstance =
			fetchMissingReference(uuid, groupId);

		if (existingDDMDataProviderInstance == null) {
			return;
		}

		Map<Long, Long> ddmDataProviderInstanceIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				DDMDataProviderInstance.class);

		ddmDataProviderInstanceIds.put(
			ddmDataProviderInstanceId,
			existingDDMDataProviderInstance.getDataProviderInstanceId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext,
			DDMDataProviderInstance dataProviderInstance)
		throws Exception {

		DDMDataProvider ddmDataProvider =
			_ddmDataProviderRegistry.getDDMDataProvider(
				dataProviderInstance.getType());

		if (ddmDataProvider == null) {
			throw new IllegalStateException(
				"No such DataProvider of type " +
					dataProviderInstance.getType());
		}

		DDMDataProviderInstance importedDataProviderInstance =
			(DDMDataProviderInstance)dataProviderInstance.clone();

		importedDataProviderInstance.setGroupId(
			portletDataContext.getScopeGroupId());

		DDMDataProviderInstance existingDataProviderInstance =
			_stagedModelRepository.fetchStagedModelByUuidAndGroupId(
				dataProviderInstance.getUuid(),
				portletDataContext.getScopeGroupId());

		if ((existingDataProviderInstance == null) ||
			!portletDataContext.isDataStrategyMirror()) {

			importedDataProviderInstance =
				_stagedModelRepository.addStagedModel(
					portletDataContext, importedDataProviderInstance);
		}
		else {
			importedDataProviderInstance.setMvccVersion(
				existingDataProviderInstance.getMvccVersion());
			importedDataProviderInstance.setDataProviderInstanceId(
				existingDataProviderInstance.getDataProviderInstanceId());

			importedDataProviderInstance =
				_stagedModelRepository.updateStagedModel(
					portletDataContext, importedDataProviderInstance);
		}

		long userId = portletDataContext.getUserId(
			dataProviderInstance.getUserUuid());

		DDMForm ddmForm = DDMFormFactory.create(ddmDataProvider.getSettings());

		DDMFormValues ddmFormValues = _deserialize(
			dataProviderInstance.getDefinition(), ddmForm);

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			dataProviderInstance);

		_ddmDataProviderInstanceLocalService.updateDataProviderInstance(
			userId, importedDataProviderInstance.getDataProviderInstanceId(),
			dataProviderInstance.getNameMap(),
			dataProviderInstance.getDescriptionMap(), ddmFormValues,
			serviceContext);

		portletDataContext.importClassedModel(
			dataProviderInstance, importedDataProviderInstance);
	}

	@Override
	protected StagedModelRepository<DDMDataProviderInstance>
		getStagedModelRepository() {

		return _stagedModelRepository;
	}

	private DDMFormValues _deserialize(String content, DDMForm ddmForm) {
		DDMFormValuesDeserializerDeserializeRequest.Builder builder =
			DDMFormValuesDeserializerDeserializeRequest.Builder.newBuilder(
				content, ddmForm);

		DDMFormValuesDeserializerDeserializeResponse
			ddmFormValuesDeserializerDeserializeResponse =
				_jsonDDMFormValuesDeserializer.deserialize(builder.build());

		return ddmFormValuesDeserializerDeserializeResponse.getDDMFormValues();
	}

	@Reference
	private DDMDataProviderInstanceLocalService
		_ddmDataProviderInstanceLocalService;

	@Reference
	private DDMDataProviderRegistry _ddmDataProviderRegistry;

	@Reference(target = "(ddm.form.values.deserializer.type=json)")
	private DDMFormValuesDeserializer _jsonDDMFormValuesDeserializer;

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance)"
	)
	private StagedModelRepository<DDMDataProviderInstance>
		_stagedModelRepository;

}