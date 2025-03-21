/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.exportimport.data.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.data.engine.model.DEDataDefinitionFieldLink;
import com.liferay.data.engine.service.DEDataDefinitionFieldLinkLocalService;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstanceLink;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.security.permission.DDMPermissionSupport;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureVersionLocalService;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelModifiedDateComparator;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Element;

import java.io.IOException;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 * @author Daniel Kocsis
 */
@Component(
	property = "jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING,
	service = StagedModelDataHandler.class
)
public class DDMStructureStagedModelDataHandler
	extends BaseStagedModelDataHandler<DDMStructure> {

	public static final String[] CLASS_NAMES = {DDMStructure.class.getName()};

	@Override
	public void deleteStagedModel(DDMStructure structure)
		throws PortalException {

		_ddmStructureLocalService.deleteStructure(structure);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		DDMStructure ddmStructure = fetchStagedModelByUuidAndGroupId(
			uuid, groupId);

		if (ddmStructure != null) {
			deleteStagedModel(ddmStructure);
		}
	}

	@Override
	public DDMStructure fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _ddmStructureLocalService.fetchDDMStructureByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public List<DDMStructure> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _ddmStructureLocalService.getDDMStructuresByUuidAndCompanyId(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			new StagedModelModifiedDateComparator<DDMStructure>());
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(DDMStructure structure) {
		return structure.getNameCurrentValue();
	}

	@Override
	public Map<String, String> getReferenceAttributes(
		PortletDataContext portletDataContext, DDMStructure structure) {

		Map<String, String> referenceAttributes = HashMapBuilder.put(
			"referenced-class-name", structure.getClassName()
		).put(
			"structure-key", structure.getStructureKey()
		).build();

		long guestUserId = 0;

		try {
			guestUserId = _userLocalService.getGuestUserId(
				structure.getCompanyId());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return referenceAttributes;
		}

		referenceAttributes.put(
			"preloaded",
			String.valueOf(_isPreloadedStructure(guestUserId, structure)));

		return referenceAttributes;
	}

	@Override
	public boolean validateReference(
		PortletDataContext portletDataContext, Element referenceElement) {

		validateMissingGroupReference(portletDataContext, referenceElement);

		String uuid = referenceElement.attributeValue("uuid");

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		long groupId = GetterUtil.getLong(
			referenceElement.attributeValue("group-id"));

		groupId = MapUtil.getLong(groupIds, groupId);

		boolean preloaded = GetterUtil.getBoolean(
			referenceElement.attributeValue("preloaded"));

		if (!preloaded) {
			return super.validateMissingReference(uuid, groupId);
		}

		long classNameId = _portal.getClassNameId(
			referenceElement.attributeValue("referenced-class-name"));
		String structureKey = referenceElement.attributeValue("structure-key");

		DDMStructure existingStructure =
			_fetchExistingStructureWithParentGroups(
				uuid, groupId, classNameId, structureKey, preloaded);

		if (existingStructure == null) {
			return false;
		}

		return true;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, DDMStructure structure)
		throws Exception {

		Element structureElement = portletDataContext.getExportDataElement(
			structure);

		if (structure.getParentStructureId() !=
				DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID) {

			DDMStructure parentStructure =
				_ddmStructureLocalService.getStructure(
					structure.getParentStructureId());

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, structure, parentStructure,
				PortletDataContext.REFERENCE_TYPE_PARENT);
		}

		List<DEDataDefinitionFieldLink> deDataDefinitionFieldLinks =
			ListUtil.concat(
				_deDataDefinitionFieldLinkLocalService.
					getDEDataDefinitionFieldLinksByClassNameIdAndClassPK(
						_portal.getClassNameId(DDMStructure.class.getName()),
						structure.getStructureId()),
				_deDataDefinitionFieldLinkLocalService.
					getDEDataDefinitionFieldLinksByClassNameIdAndClassPK(
						_portal.getClassNameId(
							DDMStructureLayout.class.getName()),
						structure.getDefaultDDMStructureLayoutId()));

		for (DEDataDefinitionFieldLink deDataDefinitionFieldLink :
				deDataDefinitionFieldLinks) {

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, structure, deDataDefinitionFieldLink,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}

		if (_isPreloadedStructure(
				_userLocalService.getGuestUserId(structure.getCompanyId()),
				structure)) {

			structureElement.addAttribute("preloaded", "true");
		}

		_exportDDMForm(portletDataContext, structure, structureElement);

		_exportDDMDataProviderInstances(
			portletDataContext, structure, structureElement);

		_exportDDMFormLayout(portletDataContext, structure, structureElement);

		portletDataContext.addClassedModel(
			structureElement, ExportImportPathUtil.getModelPath(structure),
			structure);

		portletDataContext.addPermissions(
			getResourceName(structure), structure.getPrimaryKey());
	}

	@Override
	protected void doImportMissingReference(
			PortletDataContext portletDataContext, Element referenceElement)
		throws PortletDataException {

		importMissingGroupReference(portletDataContext, referenceElement);

		String uuid = referenceElement.attributeValue("uuid");

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		long groupId = GetterUtil.getLong(
			referenceElement.attributeValue("group-id"));

		groupId = MapUtil.getLong(groupIds, groupId);

		long classNameId = _portal.getClassNameId(
			referenceElement.attributeValue("referenced-class-name"));
		String structureKey = referenceElement.attributeValue("structure-key");
		boolean preloaded = GetterUtil.getBoolean(
			referenceElement.attributeValue("preloaded"));

		DDMStructure existingStructure;

		if (!preloaded) {
			existingStructure = fetchMissingReference(uuid, groupId);
		}
		else {
			existingStructure = _fetchExistingStructureWithParentGroups(
				uuid, groupId, classNameId, structureKey, preloaded);
		}

		if (existingStructure == null) {
			return;
		}

		Map<Long, Long> structureIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				DDMStructure.class);

		long structureId = GetterUtil.getLong(
			referenceElement.attributeValue("class-pk"));

		structureIds.put(structureId, existingStructure.getStructureId());

		Map<String, String> structureKeys =
			(Map<String, String>)portletDataContext.getNewPrimaryKeysMap(
				DDMStructure.class + ".ddmStructureKey");

		structureKeys.put(structureKey, existingStructure.getStructureKey());

		Map<String, String> structureUuids =
			(Map<String, String>)portletDataContext.getNewPrimaryKeysMap(
				DDMStructure.class + ".ddmStructureUuid");

		structureUuids.put(uuid, existingStructure.getUuid());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, DDMStructure structure)
		throws Exception {

		long userId = portletDataContext.getUserId(structure.getUserUuid());

		Map<Long, Long> structureIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				DDMStructure.class);

		long parentStructureId = MapUtil.getLong(
			structureIds, structure.getParentStructureId(),
			structure.getParentStructureId());

		Map<String, String> structureKeys =
			(Map<String, String>)portletDataContext.getNewPrimaryKeysMap(
				DDMStructure.class + ".ddmStructureKey");

		Element structureElement = portletDataContext.getImportDataElement(
			structure);

		DDMForm ddmForm = _getImportDDMForm(
			portletDataContext, structureElement);

		long groupId = portletDataContext.getScopeGroupId();

		if (MapUtil.getBoolean(
				portletDataContext.getParameterMap(), "stagingSite")) {

			Group group = _groupLocalService.fetchGroup(groupId);

			if (group.isStaged() && !group.isStagingGroup()) {
				Group stagingGroup = _groupLocalService.fetchStagingGroup(
					groupId);

				groupId = stagingGroup.getGroupId();
			}
		}

		_updateDDMFormFieldsDDMStructureIds(ddmForm, structureIds);
		_updateDDMFormFieldsPredefinedValues(
			ddmForm, groupId, portletDataContext.getSourceGroupId());

		_importDDMDataProviderInstances(
			portletDataContext, structureElement, ddmForm);

		DDMFormLayout ddmFormLayout = _getImportDDMFormLayout(
			portletDataContext, structureElement);

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			structure);

		DDMStructure importedStructure = null;

		if (portletDataContext.isDataStrategyMirror()) {
			Element element =
				portletDataContext.getImportDataStagedModelElement(structure);

			boolean preloaded = GetterUtil.getBoolean(
				element.attributeValue("preloaded"));

			DDMStructure existingStructure = _fetchExistingStructure(
				structure.getUuid(), groupId, structure.getClassNameId(),
				structure.getStructureKey(), preloaded);

			if (existingStructure == null) {
				serviceContext.setUuid(structure.getUuid());

				existingStructure = _ddmStructureLocalService.fetchStructure(
					groupId, structure.getClassNameId(),
					structure.getStructureKey());

				String structureKey = null;

				if (existingStructure == null) {
					structureKey = structure.getStructureKey();
				}

				importedStructure = _ddmStructureLocalService.addStructure(
					null, userId, groupId, parentStructureId,
					structure.getClassNameId(), structureKey,
					structure.getNameMap(), structure.getDescriptionMap(),
					ddmForm, null, structure.getStorageType(),
					structure.getType(), serviceContext);

				DDMStructureVersion structureVersion =
					importedStructure.getLatestStructureVersion();

				_ddmStructureLayoutLocalService.addStructureLayout(
					userId, groupId, structure.getClassNameId(), structureKey,
					structureVersion.getStructureVersionId(), ddmFormLayout,
					serviceContext);
			}
			else if (_isModifiedStructure(existingStructure, structure)) {
				importedStructure = _ddmStructureLocalService.updateStructure(
					userId, existingStructure.getStructureId(),
					parentStructureId, structure.getNameMap(),
					structure.getDescriptionMap(), ddmForm, ddmFormLayout,
					serviceContext);
			}
			else {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Not importing DDM structure with key " +
							structure.getStructureKey() +
								" since it was not modified");
				}

				importedStructure = existingStructure;
			}
		}
		else {
			importedStructure = _ddmStructureLocalService.addStructure(
				null, userId, groupId, parentStructureId,
				structure.getClassNameId(), null, structure.getNameMap(),
				structure.getDescriptionMap(), ddmForm, ddmFormLayout,
				structure.getStorageType(), structure.getType(),
				serviceContext);
		}

		structureIds.put(
			structure.getStructureId(), importedStructure.getStructureId());

		portletDataContext.importClassedModel(structure, importedStructure);

		portletDataContext.importPermissions(
			getResourceName(structure), structure.getPrimaryKey(),
			importedStructure.getPrimaryKey());

		structureKeys.put(
			structure.getStructureKey(), importedStructure.getStructureKey());
	}

	protected String getResourceName(DDMStructure structure)
		throws PortalException {

		return ddmPermissionSupport.getStructureModelResourceName(
			structure.getClassName());
	}

	@Override
	protected String[] getSkipImportReferenceStagedModelNames() {
		return new String[] {DEDataDefinitionFieldLink.class.getName()};
	}

	@Reference
	protected DDMPermissionSupport ddmPermissionSupport;

	@Reference
	protected JSONFactory jsonFactory;

	private boolean _equalsJSON(String json1, String json2) {
		try {
			JsonNode jsonNode1 = _objectMapper.readTree(json1);
			JsonNode jsonNode2 = _objectMapper.readTree(json2);

			return jsonNode1.equals(jsonNode2);
		}
		catch (IOException ioException) {
			_log.error(ioException);

			return false;
		}
	}

	private void _exportDDMDataProviderInstances(
			PortletDataContext portletDataContext, DDMStructure structure,
			Element structureElement)
		throws Exception {

		Set<Long> ddmDataProviderInstanceIdsSet = new HashSet<>();

		List<DDMDataProviderInstanceLink> ddmDataProviderInstanceLinks =
			_ddmDataProviderInstanceLinkLocalService.
				getDataProviderInstanceLinks(structure.getStructureId());

		for (DDMDataProviderInstanceLink ddmDataProviderInstanceLink :
				ddmDataProviderInstanceLinks) {

			long ddmDataProviderInstanceId =
				ddmDataProviderInstanceLink.getDataProviderInstanceId();

			DDMDataProviderInstance ddmDataProviderInstance =
				_ddmDataProviderInstanceLocalService.getDDMDataProviderInstance(
					ddmDataProviderInstanceId);

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, structure, ddmDataProviderInstance,
				PortletDataContext.REFERENCE_TYPE_STRONG);

			ddmDataProviderInstanceIdsSet.add(
				ddmDataProviderInstance.getDataProviderInstanceId());
		}

		String ddmDataProviderInstanceIds = ArrayUtil.toString(
			ddmDataProviderInstanceIdsSet.toArray(new Long[0]),
			StringPool.BLANK);

		structureElement.addAttribute(
			_DDM_DATA_PROVIDER_INSTANCE_IDS, ddmDataProviderInstanceIds);
	}

	private void _exportDDMForm(
		PortletDataContext portletDataContext, DDMStructure structure,
		Element structureElement) {

		String ddmFormPath = ExportImportPathUtil.getModelPath(
			structure, "ddm-form.json");

		structureElement.addAttribute("ddm-form-path", ddmFormPath);

		DDMForm ddmForm = structure.getDDMForm();

		_exportReferencedDDMStructures(ddmForm, structure, portletDataContext);

		portletDataContext.addZipEntry(
			ddmFormPath, _ddm.getDDMFormJSONString(ddmForm));
	}

	private void _exportDDMFormLayout(
			PortletDataContext portletDataContext, DDMStructure structure,
			Element structureElement)
		throws Exception {

		DDMStructureVersion structureVersion = structure.getStructureVersion();

		DDMStructureLayout structureLayout =
			_ddmStructureLayoutLocalService.
				getStructureLayoutByStructureVersionId(
					structureVersion.getStructureVersionId());

		String ddmFormLayoutPath = ExportImportPathUtil.getModelPath(
			structure, "ddm-form-layout.json");

		structureElement.addAttribute(
			"ddm-form-layout-path", ddmFormLayoutPath);

		portletDataContext.addZipEntry(
			ddmFormLayoutPath, structureLayout.getDefinition());
	}

	private void _exportReferencedDDMStructure(
		DDMFormField ddmFormField, DDMStructure ddmStructure,
		PortletDataContext portletDataContext) {

		long ddmStructureId = GetterUtil.getLong(
			ddmFormField.getProperty("ddmStructureId"));

		DDMStructure referencedDDMStructure =
			_ddmStructureLocalService.fetchDDMStructure(ddmStructureId);

		if (referencedDDMStructure == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to find structure ", ddmStructureId,
						" referenced in definition of structure ",
						ddmStructure.getStructureId()));
			}

			return;
		}

		try {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, ddmStructure, referencedDDMStructure,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}
		catch (PortletDataException portletDataException) {
			_log.error(
				StringBundler.concat(
					"Unable to export structure ",
					referencedDDMStructure.getStructureId(), " as a reference ",
					"of structure ", ddmStructure.getStructureId()),
				portletDataException);
		}
	}

	private void _exportReferencedDDMStructures(
		DDMForm ddmForm, DDMStructure ddmStructure,
		PortletDataContext portletDataContext) {

		for (DDMFormField ddmFormField : ddmForm.getDDMFormFields()) {
			long referencedDDMStructureId = GetterUtil.getLong(
				ddmFormField.getProperty("ddmStructureId"));

			if (referencedDDMStructureId != 0) {
				_exportReferencedDDMStructure(
					ddmFormField, ddmStructure, portletDataContext);
			}
		}
	}

	private DDMStructure _fetchExistingStructure(
		String uuid, long groupId, long classNameId, String structureKey,
		boolean preloaded) {

		DDMStructure existingStructure = null;

		if (!preloaded) {
			existingStructure = fetchStagedModelByUuidAndGroupId(uuid, groupId);
		}
		else {
			existingStructure = _ddmStructureLocalService.fetchStructure(
				groupId, classNameId, structureKey);
		}

		return existingStructure;
	}

	private DDMStructure _fetchExistingStructureWithParentGroups(
		String uuid, long groupId, long classNameId, String structureKey,
		boolean preloaded) {

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return null;
		}

		long companyId = group.getCompanyId();

		while (group != null) {
			DDMStructure existingStructure = _fetchExistingStructure(
				uuid, group.getGroupId(), classNameId, structureKey, preloaded);

			if (existingStructure != null) {
				return existingStructure;
			}

			group = group.getParentGroup();
		}

		Group companyGroup = _groupLocalService.fetchCompanyGroup(companyId);

		if (companyGroup == null) {
			return null;
		}

		return _fetchExistingStructure(
			uuid, companyGroup.getGroupId(), classNameId, structureKey,
			preloaded);
	}

	private DDMForm _getImportDDMForm(
			PortletDataContext portletDataContext, Element structureElement)
		throws Exception {

		String ddmFormPath = structureElement.attributeValue("ddm-form-path");

		return _ddm.getDDMForm(
			portletDataContext.getZipEntryAsString(ddmFormPath));
	}

	private DDMFormLayout _getImportDDMFormLayout(
		PortletDataContext portletDataContext, Element structureElement) {

		String ddmFormLayoutPath = structureElement.attributeValue(
			"ddm-form-layout-path");

		String serializedDDMFormLayout = portletDataContext.getZipEntryAsString(
			ddmFormLayoutPath);

		DDMFormLayoutDeserializerDeserializeRequest.Builder builder =
			DDMFormLayoutDeserializerDeserializeRequest.Builder.newBuilder(
				serializedDDMFormLayout);

		DDMFormLayoutDeserializerDeserializeResponse
			ddmFormLayoutDeserializerDeserializeResponse =
				_jsonDDMFormLayoutDeserializer.deserialize(builder.build());

		return ddmFormLayoutDeserializerDeserializeResponse.getDDMFormLayout();
	}

	private long _getJSONArrayFirstValue(String value) {
		try {
			JSONArray jsonArray = jsonFactory.createJSONArray(value);

			return jsonArray.getLong(0);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return GetterUtil.getLong(value);
		}
	}

	private void _importDDMDataProviderInstances(
			PortletDataContext portletDataContext, Element structureElement,
			DDMForm ddmForm)
		throws Exception {

		String[] ddmDataProviderInstanceIds = StringUtil.split(
			structureElement.attributeValue(_DDM_DATA_PROVIDER_INSTANCE_IDS));

		if (ArrayUtil.isEmpty(ddmDataProviderInstanceIds)) {
			return;
		}

		Map<Long, Long> dataProviderInstanceIdsMap =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				DDMDataProviderInstance.class);

		for (String ddmDataProviderInstanceId : ddmDataProviderInstanceIds) {
			long oldDDMDataProviderInstanceId = GetterUtil.getLong(
				ddmDataProviderInstanceId);

			long newDDMDataProviderInstanceId = MapUtil.getLong(
				dataProviderInstanceIdsMap, oldDDMDataProviderInstanceId);

			StagedModelDataHandlerUtil.importReferenceStagedModel(
				portletDataContext, DDMDataProviderInstance.class,
				Long.valueOf(newDDMDataProviderInstanceId));
		}

		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		for (DDMFormField ddmFormField : ddmFormFields) {
			String ddmFormFieldType = ddmFormField.getType();

			if (!ddmFormFieldType.equals(DDMFormFieldType.SELECT) &&
				!ddmFormFieldType.equals(DDMFormFieldType.TEXT)) {

				continue;
			}

			long oldDDMDataProviderInstanceId = _getJSONArrayFirstValue(
				GetterUtil.getString(
					ddmFormField.getProperty("ddmDataProviderInstanceId")));

			long newDDMDataProviderInstanceId = MapUtil.getLong(
				dataProviderInstanceIdsMap, oldDDMDataProviderInstanceId);

			ddmFormField.setProperty(
				"ddmDataProviderInstanceId", newDDMDataProviderInstanceId);
		}
	}

	private boolean _isModifiedStructure(
		DDMStructure existingStructure, DDMStructure structure) {

		// Check modified date first

		int value = DateUtil.compareTo(
			existingStructure.getModifiedDate(), structure.getModifiedDate());

		if (value < 0) {
			return true;
		}

		// Check other attributes

		if (!_equalsJSON(
				existingStructure.getDefinition(), structure.getDefinition()) ||
			!Objects.equals(
				existingStructure.getDescriptionMap(),
				structure.getDescriptionMap()) ||
			!Objects.equals(
				existingStructure.getNameMap(), structure.getNameMap()) ||
			!Objects.equals(
				existingStructure.getStorageType(),
				structure.getStorageType()) ||
			!Objects.equals(existingStructure.getType(), structure.getType())) {

			return true;
		}

		return false;
	}

	private boolean _isPreloadedStructure(
		long guestUserId, DDMStructure structure) {

		if (guestUserId == structure.getUserId()) {
			return true;
		}

		DDMStructureVersion ddmStructureVersion = null;

		try {
			ddmStructureVersion =
				_ddmStructureVersionLocalService.getStructureVersion(
					structure.getStructureId(),
					DDMStructureConstants.VERSION_DEFAULT);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		if ((ddmStructureVersion != null) &&
			(guestUserId == ddmStructureVersion.getUserId())) {

			return true;
		}

		return false;
	}

	private void _updateDDMFormFieldsDDMStructureIds(
		DDMForm ddmForm, Map<Long, Long> structureIds) {

		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		for (DDMFormField ddmFormField : ddmFormFields) {
			long ddmStructureId = GetterUtil.getLong(
				ddmFormField.getProperty("ddmStructureId"));

			if (ddmStructureId != 0) {
				ddmFormField.setProperty(
					"ddmStructureId",
					MapUtil.getLong(
						structureIds, ddmStructureId, ddmStructureId));
			}
		}
	}

	private void _updateDDMFormFieldsPredefinedValues(
		DDMForm ddmForm, long groupId, long sourceId) {

		for (DDMFormField ddmFormField : ddmForm.getDDMFormFields()) {
			LocalizedValue localizedValue = ddmFormField.getPredefinedValue();

			Map<Locale, String> values = localizedValue.getValues();

			for (Map.Entry<Locale, String> entry : values.entrySet()) {
				if (!StringUtil.contains(
						entry.getValue(), String.valueOf(sourceId))) {

					continue;
				}

				entry.setValue(
					StringUtil.replace(
						entry.getValue(), String.valueOf(sourceId),
						String.valueOf(groupId)));
			}
		}
	}

	private static final String _DDM_DATA_PROVIDER_INSTANCE_IDS =
		"ddm-data-provider-instance-ids";

	private static final Log _log = LogFactoryUtil.getLog(
		DDMStructureStagedModelDataHandler.class);

	private static final ObjectMapper _objectMapper = new ObjectMapper();

	@Reference
	private DDM _ddm;

	@Reference
	private DDMDataProviderInstanceLinkLocalService
		_ddmDataProviderInstanceLinkLocalService;

	@Reference
	private DDMDataProviderInstanceLocalService
		_ddmDataProviderInstanceLocalService;

	@Reference
	private DDMStructureLayoutLocalService _ddmStructureLayoutLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DDMStructureVersionLocalService _ddmStructureVersionLocalService;

	@Reference
	private DEDataDefinitionFieldLinkLocalService
		_deDataDefinitionFieldLinkLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(target = "(ddm.form.layout.deserializer.type=json)")
	private DDMFormLayoutDeserializer _jsonDDMFormLayoutDeserializer;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}