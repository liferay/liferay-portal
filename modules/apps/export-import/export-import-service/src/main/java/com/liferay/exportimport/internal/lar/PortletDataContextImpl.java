/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.lar;

import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.service.AssetLinkLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.adapter.StagedExpandoColumn;
import com.liferay.expando.kernel.service.ExpandoColumnLocalServiceUtil;
import com.liferay.expando.kernel.util.ExpandoUtil;
import com.liferay.exportimport.internal.util.ExportImportPermissionUtil;
import com.liferay.exportimport.internal.xstream.ConverterAdapter;
import com.liferay.exportimport.internal.xstream.XStreamStagedModelTypeHierarchyPermission;
import com.liferay.exportimport.internal.xstream.converter.TimestampConverter;
import com.liferay.exportimport.kernel.exception.ExportImportIOException;
import com.liferay.exportimport.kernel.lar.ExportImportClassedModelUtil;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.lar.UserIdStrategy;
import com.liferay.exportimport.kernel.xstream.XStreamAlias;
import com.liferay.exportimport.kernel.xstream.XStreamConverter;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.Conjunction;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.NoSuchRoleException;
import com.liferay.portal.kernel.exception.NoSuchTeamException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.AuditedModel;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletModel;
import com.liferay.portal.kernel.model.ResourcedModel;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.StagedGroupedModel;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.TypedModel;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.model.WorkflowedModel;
import com.liferay.portal.kernel.model.adapter.StagedGroupedWorkflowDefinitionLink;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TeamLocalServiceUtil;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateRange;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.kernel.xml.Attribute;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.model.adapter.util.ModelAdapterUtil;
import com.liferay.portal.workflow.util.WorkflowDefinitionManagerUtil;
import com.liferay.xstream.configurator.XStreamConfigurator;
import com.liferay.xstream.configurator.XStreamConfiguratorRegistryUtil;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

/**
 * <p>
 * Holds context information that is used during exporting and importing portlet
 * data.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Bruno Farache
 * @author Alexander Chow
 * @author Máté Thurzó
 */
public class PortletDataContextImpl implements PortletDataContext {

	public PortletDataContextImpl(LockManager lockManager) {
		this(lockManager, true);
	}

	public PortletDataContextImpl(
		LockManager lockManager, boolean createXstream) {

		if (createXstream) {
			synchronized (PortletDataContextImpl.class) {
				_initXStream();
			}
		}

		_lockManager = lockManager;
	}

	@Override
	public void addAssetCategories(
		String className, long classPK, long[] assetCategoryIds) {

		_assetCategoryIdsMap.put(
			_getPrimaryKeyString(className, (Serializable)classPK),
			assetCategoryIds);
	}

	@Override
	public void addAssetTags(
		String className, long classPK, String[] assetTagNames) {

		_assetTagNamesMap.put(
			_getPrimaryKeyString(className, (Serializable)classPK),
			assetTagNames);
	}

	@Override
	public void addClassedModel(
			Element element, String path, ClassedModel classedModel)
		throws PortalException {

		addClassedModel(
			element, path, classedModel, classedModel.getModelClass());
	}

	@Override
	public void addClassedModel(
			Element element, String path, ClassedModel classedModel,
			Class<?> clazz)
		throws PortalException {

		element.addAttribute("path", path);

		_populateClassNameAttribute(classedModel, element);

		Serializable classPK = ExportImportClassedModelUtil.getPrimaryKeyObj(
			classedModel);

		long classNameId = ExportImportClassedModelUtil.getClassNameId(
			classedModel);

		if (!hasPrimaryKey(String.class, path)) {
			if (classedModel instanceof AuditedModel) {
				AuditedModel auditedModel = (AuditedModel)classedModel;

				auditedModel.setUserUuid(auditedModel.getUserUuid());
			}

			if (_isResourceMain(classedModel)) {
				_addAssetLinks(classNameId, GetterUtil.getLong(classPK));

				addExpando(element, path, classedModel, clazz);

				if (getBooleanParameter(
						clazz.getName(), PortletDataHandlerKeys.LOCKS, false)) {

					addLocks(clazz, String.valueOf(classPK));
				}

				addPermissions(clazz, classPK);
			}

			_references.add(_getReferenceKey(classedModel));
		}

		if (classedModel instanceof AuditedModel) {
			AuditedModel auditedModel = (AuditedModel)classedModel;

			element.addAttribute("user-uuid", auditedModel.getUserUuid());
		}

		if (_isResourceMain(classedModel)) {
			double assetEntryPriority =
				AssetEntryLocalServiceUtil.getEntryPriority(
					classNameId, GetterUtil.getLong(classPK));

			element.addAttribute(
				"asset-entry-priority", String.valueOf(assetEntryPriority));
		}

		_addWorkflowDefinitionLink(classedModel);

		addZipEntry(path, classedModel);
	}

	/**
	 * @see #isWithinDateRange(Date)
	 */
	@Override
	public void addDateRangeCriteria(
		DynamicQuery dynamicQuery, String propertyName) {

		Criterion criterion = getDateRangeCriteria(propertyName);

		if (criterion == null) {
			return;
		}

		dynamicQuery.add(criterion);
	}

	@Override
	public void addDeletionSystemEventStagedModelTypes(
		StagedModelType... stagedModelTypes) {

		for (StagedModelType stagedModelType : stagedModelTypes) {
			_deletionSystemEventStagedModelTypes.add(stagedModelType);
		}
	}

	@Override
	public void addLocks(Class<?> clazz, String key) throws PortalException {
		if (!_locksMap.containsKey(
				_getPrimaryKeyString(clazz, (Serializable)key)) &&
			_lockManager.isLocked(clazz.getName(), key)) {

			addLocks(
				clazz.getName(), key,
				_lockManager.getLock(clazz.getName(), key));
		}
	}

	@Override
	public void addLocks(String className, String key, Lock lock) {
		_locksMap.put(_getPrimaryKeyString(className, (Serializable)key), lock);
	}

	@Override
	public void addPermissions(Class<?> clazz, Serializable classPK) {
		addPermissions(clazz.getName(), GetterUtil.getLong(classPK));
	}

	@Override
	public void addPermissions(String resourceName, long resourcePK) {
		if (!MapUtil.getBoolean(
				_parameterMap, PortletDataHandlerKeys.PERMISSIONS)) {

			return;
		}

		Map<Long, Set<String>> roleIdsToActionIds =
			ExportImportPermissionUtil.getRoleIdsToActionIds(
				_companyId, resourceName, resourcePK);

		List<KeyValuePair> permissionKeyValuePairs = TransformUtil.transform(
			roleIdsToActionIds.entrySet(),
			entry -> {
				long roleId = entry.getKey();

				Role role = RoleLocalServiceUtil.fetchRole(roleId);

				if (role == null) {
					return null;
				}

				String roleName = role.getName();

				if (role.isTeam()) {
					try {
						roleName = ExportImportPermissionUtil.getTeamRoleName(
							role.getDescriptiveName());
					}
					catch (PortalException portalException) {
						_log.error(portalException);
					}
				}

				return new KeyValuePair(
					roleName, StringUtil.merge(entry.getValue()));
			});

		if (permissionKeyValuePairs.isEmpty()) {
			return;
		}

		_permissionsMap.put(
			_getPrimaryKeyString(resourceName, (Serializable)resourcePK),
			permissionKeyValuePairs);
	}

	@Override
	public void addPermissions(
		String resourceName, long resourcePK,
		List<KeyValuePair> permissionKeyValuePairs) {

		_permissionsMap.put(
			_getPrimaryKeyString(resourceName, (Serializable)resourcePK),
			permissionKeyValuePairs);
	}

	@Override
	public void addPortalPermissions() {
		addPermissions(PortletKeys.PORTAL, getCompanyId());
	}

	@Override
	public void addPortletPermissions(String resourceName) {
		addPermissions(resourceName, getGroupId());
	}

	@Override
	public boolean addPrimaryKey(Class<?> clazz, String primaryKey) {
		return !_primaryKeys.add(
			_getPrimaryKeyString(clazz, (Serializable)primaryKey));
	}

	@Override
	public Element addReferenceElement(
		ClassedModel referrerClassedModel, Element element,
		ClassedModel classedModel, String referenceType, boolean missing) {

		return addReferenceElement(
			referrerClassedModel, element, classedModel,
			ExportImportClassedModelUtil.getClassName(classedModel),
			StringPool.BLANK, referenceType, missing);
	}

	@Override
	public Element addReferenceElement(
		ClassedModel referrerClassedModel, Element element,
		ClassedModel classedModel, String binPath, String referenceType,
		boolean missing) {

		return addReferenceElement(
			referrerClassedModel, element, classedModel,
			ExportImportClassedModelUtil.getClassName(classedModel), binPath,
			referenceType, missing);
	}

	@Override
	public Element addReferenceElement(
		ClassedModel referrerClassedModel, Element element,
		ClassedModel classedModel, String className, String binPath,
		String referenceType, boolean missing) {

		Element referenceElement = _addReferenceElement(
			referrerClassedModel, element, classedModel, className, binPath,
			referenceType, false);

		String referenceKey = _getReferenceKey(classedModel);

		if (missing) {
			referenceElement.addAttribute("missing", Boolean.TRUE.toString());

			if (_references.contains(referenceKey)) {
				return referenceElement;
			}

			if (_missingReferences.add(referenceKey)) {
				_addReferenceElement(
					referrerClassedModel, null, classedModel, className,
					binPath, referenceType, true);
			}
		}
		else {
			_references.add(referenceKey);

			referenceElement.addAttribute("missing", Boolean.FALSE.toString());

			cleanUpMissingReferences(classedModel);
		}

		return referenceElement;
	}

	@Override
	public boolean addScopedPrimaryKey(Class<?> clazz, String primaryKey) {
		boolean value = hasScopedPrimaryKey(clazz, primaryKey);

		if (!value) {
			_scopedPrimaryKeys.add(
				_getPrimaryKeyString(clazz, (Serializable)primaryKey));
		}

		return value;
	}

	@Override
	public void addScopedPrimaryKeys(Collection<String> scopedPrimaryKeys) {
		_scopedPrimaryKeys.addAll(scopedPrimaryKeys);
	}

	@Override
	public void addZipEntry(String path, byte[] bytes) {
		if (isPathProcessed(path)) {
			return;
		}

		try {
			ZipWriter zipWriter = getZipWriter();

			zipWriter.addEntry(path, bytes);
		}
		catch (IOException ioException) {
			ExportImportIOException exportImportIOException =
				new ExportImportIOException(
					PortletDataContextImpl.class.getName(), ioException);

			exportImportIOException.setFileName(path);
			exportImportIOException.setType(
				ExportImportIOException.ADD_ZIP_ENTRY_BYTES);

			throw new SystemException(exportImportIOException);
		}
	}

	@Override
	public void addZipEntry(String path, InputStream inputStream) {
		if (isPathProcessed(path)) {
			return;
		}

		try {
			ZipWriter zipWriter = getZipWriter();

			zipWriter.addEntry(path, inputStream);
		}
		catch (IOException ioException) {
			ExportImportIOException exportImportIOException =
				new ExportImportIOException(
					PortletDataContextImpl.class.getName(), ioException);

			exportImportIOException.setFileName(path);
			exportImportIOException.setType(
				ExportImportIOException.ADD_ZIP_ENTRY_STREAM);

			throw new SystemException(exportImportIOException);
		}
	}

	@Override
	public void addZipEntry(String path, Object object) {
		if (isPathProcessed(path)) {
			return;
		}

		try {
			ZipWriter zipWriter = getZipWriter();

			zipWriter.addEntry(path, toXML(object));
		}
		catch (IOException ioException) {
			ExportImportIOException exportImportIOException =
				new ExportImportIOException(
					PortletDataContextImpl.class.getName(), ioException);

			exportImportIOException.setFileName(path);
			exportImportIOException.setType(
				ExportImportIOException.ADD_ZIP_ENTRY_STRING);

			throw new SystemException(exportImportIOException);
		}
	}

	@Override
	public void addZipEntry(String path, String s) {
		if (isPathProcessed(path)) {
			return;
		}

		try {
			ZipWriter zipWriter = getZipWriter();

			zipWriter.addEntry(path, s);
		}
		catch (IOException ioException) {
			ExportImportIOException exportImportIOException =
				new ExportImportIOException(
					PortletDataContextImpl.class.getName(), ioException);

			exportImportIOException.setFileName(path);
			exportImportIOException.setType(
				ExportImportIOException.ADD_ZIP_ENTRY_STRING);

			throw new SystemException(exportImportIOException);
		}
	}

	@Override
	public void addZipEntry(String path, StringBuilder sb) {
		addZipEntry(path, sb.toString());
	}

	@Override
	public void cleanUpMissingReferences(ClassedModel classedModel) {
		if (_missingReferences.remove(_getReferenceKey(classedModel))) {
			Element missingReferenceElement = getMissingReferenceElement(
				classedModel);

			if (classedModel instanceof Layout) {
				missingReferenceElement.addAttribute(
					"element-path", "/manifest.xml");
			}
			else {
				missingReferenceElement.addAttribute(
					"element-path", _getPortletXmlPath());
			}
		}
	}

	@Override
	public void clearScopedPrimaryKeys() {
		_scopedPrimaryKeys.clear();
	}

	@Override
	public ServiceContext createServiceContext(
		Element element, ClassedModel classedModel) {

		return createServiceContext(
			element, classedModel, classedModel.getModelClass());
	}

	@Override
	public ServiceContext createServiceContext(StagedModel stagedModel) {
		return createServiceContext(stagedModel, stagedModel.getModelClass());
	}

	@Override
	public ServiceContext createServiceContext(
		StagedModel stagedModel, Class<?> clazz) {

		return createServiceContext(
			getImportDataStagedModelElement(stagedModel), stagedModel, clazz);
	}

	@Override
	public ServiceContext createServiceContext(
		String path, ClassedModel classedModel) {

		return createServiceContext(
			null, classedModel, classedModel.getModelClass());
	}

	@Override
	public Object fromXML(byte[] bytes) {
		if (ArrayUtil.isEmpty(bytes)) {
			return null;
		}

		return _xStream.fromXML(new String(bytes));
	}

	@Override
	public Object fromXML(String xml) {
		if (Validator.isNull(xml)) {
			return null;
		}

		return _xStream.fromXML(xml);
	}

	@Override
	public long[] getAssetCategoryIds(Class<?> clazz, Serializable classPK) {
		long[] assetCategoryIds = _assetCategoryIdsMap.get(
			_getPrimaryKeyString(clazz, classPK));

		if (assetCategoryIds == null) {
			return new long[0];
		}

		return assetCategoryIds;
	}

	@Override
	public Set<Long> getAssetLinkIds() {
		return _assetLinkIds;
	}

	@Override
	public String[] getAssetTagNames(Class<?> clazz, Serializable classPK) {
		return getAssetTagNames(_getPrimaryKeyString(clazz, classPK));
	}

	@Override
	public String[] getAssetTagNames(String className, Serializable classPK) {
		return getAssetTagNames(_getPrimaryKeyString(className, classPK));
	}

	@Override
	public Map<String, String[]> getAssetTagNamesMap() {
		return _assetTagNamesMap;
	}

	@Override
	public boolean getBooleanParameter(String namespace, String name) {
		return getBooleanParameter(namespace, name, true);
	}

	@Override
	public boolean getBooleanParameter(
		String namespace, String name, boolean useDefaultValue) {

		if (!useDefaultValue) {
			return MapUtil.getBoolean(
				getParameterMap(),
				PortletDataHandlerControl.getNamespacedName(namespace, name));
		}

		boolean defaultValue = MapUtil.getBoolean(
			getParameterMap(),
			PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT, true);

		return MapUtil.getBoolean(
			getParameterMap(),
			PortletDataHandlerControl.getNamespacedName(namespace, name),
			defaultValue);
	}

	@Override
	public ClassLoader getClassLoader() {
		return _xStream.getClassLoader();
	}

	@Override
	public long getCompanyGroupId() {
		return _companyGroupId;
	}

	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public String getDataStrategy() {
		return _dataStrategy;
	}

	@Override
	public DateRange getDateRange() {
		DateRange dateRange = null;

		if (hasDateRange()) {
			dateRange = new DateRange(_startDate, _endDate);
		}

		return dateRange;
	}

	@Override
	public Criterion getDateRangeCriteria(String propertyName) {
		if (!hasDateRange()) {
			return null;
		}

		Conjunction conjunction = RestrictionsFactoryUtil.conjunction();

		Property property = PropertyFactoryUtil.forName(propertyName);

		conjunction.add(property.le(_endDate));
		conjunction.add(property.ge(_startDate));

		return conjunction;
	}

	@Override
	public Set<StagedModelType> getDeletionSystemEventStagedModelTypes() {
		return _deletionSystemEventStagedModelTypes;
	}

	@Override
	public Date getEndDate() {
		return _endDate;
	}

	@Override
	public Element getExportDataElement(ClassedModel classedModel) {
		return getExportDataElement(
			classedModel,
			ExportImportClassedModelUtil.getClassSimpleName(classedModel));
	}

	@Override
	public Element getExportDataElement(
		ClassedModel classedModel, String modelClassSimpleName) {

		Element groupElement = getExportDataGroupElement(modelClassSimpleName);

		Map<String, Element> exportDataElements = _getExportDataElements(
			groupElement);

		Element element = null;

		if (classedModel instanceof StagedModel) {
			StagedModel stagedModel = (StagedModel)classedModel;

			String path = ExportImportPathUtil.getModelPath(stagedModel);

			element = exportDataElements.get(
				_getExportDataElementKey("path", path));

			if (element != null) {
				return element;
			}

			element = exportDataElements.get(
				_getExportDataElementKey("uuid", stagedModel.getUuid()));

			if (element != null) {
				return element;
			}
		}

		element = groupElement.addElement("staged-model");

		long groupId = _getGroupId(classedModel);

		if (groupId > 0) {
			element.addAttribute("group-id", String.valueOf(groupId));
		}

		if (classedModel instanceof StagedModel) {
			StagedModel stagedModel = (StagedModel)classedModel;

			element.addAttribute("uuid", stagedModel.getUuid());

			exportDataElements.putIfAbsent(
				_getExportDataElementKey(
					"path", ExportImportPathUtil.getModelPath(stagedModel)),
				element);
			exportDataElements.putIfAbsent(
				_getExportDataElementKey("uuid", stagedModel.getUuid()),
				element);
		}

		return element;
	}

	@Override
	public Element getExportDataGroupElement(
		Class<? extends StagedModel> clazz) {

		return getExportDataGroupElement(clazz.getSimpleName());
	}

	@Override
	public Element getExportDataRootElement() {
		return _exportDataRootElement;
	}

	@Override
	public String getExportImportProcessId() {
		return _exportImportProcessId;
	}

	@Override
	public long getGroupId() {
		return _groupId;
	}

	@Override
	public Element getImportDataElement(StagedModel stagedModel) {
		return getImportDataElement(
			ExportImportClassedModelUtil.getClassSimpleName(stagedModel),
			"uuid", stagedModel.getUuid());
	}

	@Override
	public Element getImportDataElement(
		String name, String attribute, String value) {

		Element groupElement = getImportDataGroupElement(name);

		if (_importDataElementsMap == null) {
			return _getDataElement(groupElement, attribute, value);
		}

		Map<String, List<Element>> importDataElements = _getImportDataElements(
			name, attribute, groupElement);

		List<Element> elements = importDataElements.get(value);

		if (ListUtil.isEmpty(elements)) {
			return null;
		}

		return elements.get(0);
	}

	@Override
	public Element getImportDataGroupElement(
		Class<? extends StagedModel> clazz) {

		return getImportDataGroupElement(clazz.getSimpleName());
	}

	@Override
	public Element getImportDataRootElement() {
		return _importDataRootElement;
	}

	@Override
	public Element getImportDataStagedModelElement(StagedModel stagedModel) {
		String path = ExportImportPathUtil.getModelPath(stagedModel);

		return getImportDataElement(
			ExportImportClassedModelUtil.getClassSimpleName(stagedModel),
			"path", path);
	}

	@Override
	public long[] getLayoutIds() {
		return _layoutIds;
	}

	@Override
	public String getLayoutSetPrototypeUuid() {
		return _layoutSetPrototypeUuid;
	}

	@Override
	public Map<String, Lock> getLocks() {
		return _locksMap;
	}

	@Override
	public ManifestSummary getManifestSummary() {
		return _manifestSummary;
	}

	@Override
	public Element getMissingReferenceElement(ClassedModel classedModel) {
		return _searchFirstChildElementWithPredicate(
			_missingReferencesElement, "missing-reference",
			childElement ->
				Objects.equals(
					childElement.attributeValue("class-name"),
					ExportImportClassedModelUtil.getClassName(classedModel)) &&
				Objects.equals(
					childElement.attributeValue("class-pk"),
					String.valueOf(classedModel.getPrimaryKeyObj())));
	}

	@Override
	public Element getMissingReferencesElement() {
		return _missingReferencesElement;
	}

	@Override
	public Object getNewPrimaryKey(String className, Object newPrimaryKey) {
		Map<?, ?> primaryKeys = getNewPrimaryKeysMap(className);

		return primaryKeys.get(newPrimaryKey);
	}

	@Override
	public Map<?, ?> getNewPrimaryKeysMap(Class<?> clazz) {
		return getNewPrimaryKeysMap(clazz.getName());
	}

	@Override
	public Map<?, ?> getNewPrimaryKeysMap(String className) {
		Map<?, ?> map = _newPrimaryKeysMaps.get(className);

		if (map == null) {
			map = new HashMap<>();

			_newPrimaryKeysMaps.put(className, map);
		}

		return map;
	}

	@Override
	public Map<String, Map<?, ?>> getNewPrimaryKeysMaps() {
		return _newPrimaryKeysMaps;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return _parameterMap;
	}

	@Override
	public Map<String, List<KeyValuePair>> getPermissions() {
		return _permissionsMap;
	}

	@Override
	public long getPlid() {
		return _plid;
	}

	@Override
	public String getPortletId() {
		return _portletId;
	}

	@Override
	public Set<String> getPrimaryKeys() {
		return _primaryKeys;
	}

	@Override
	public Element getReferenceDataElement(
		Element parentElement, Class<?> clazz, long classPK) {

		List<Element> referenceDataElements = getReferenceDataElements(
			getReferenceElements(
				parentElement, clazz.getName(), 0, null, (Serializable)classPK,
				null),
			clazz);

		if (referenceDataElements.isEmpty()) {
			return null;
		}

		return referenceDataElements.get(0);
	}

	@Override
	public Element getReferenceDataElement(
		Element parentElement, Class<?> clazz, long groupId, String uuid) {

		List<Element> referenceDataElements = getReferenceDataElements(
			getReferenceElements(
				parentElement, clazz.getName(), groupId, uuid, null, null),
			clazz);

		if (referenceDataElements.isEmpty()) {
			return null;
		}

		return referenceDataElements.get(0);
	}

	@Override
	public Element getReferenceDataElement(
		StagedModel parentStagedModel, Class<?> clazz, long classPK) {

		Element parentElement = getImportDataStagedModelElement(
			parentStagedModel);

		return getReferenceDataElement(parentElement, clazz, classPK);
	}

	@Override
	public Element getReferenceDataElement(
		StagedModel parentStagedModel, Class<?> clazz, long groupId,
		String uuid) {

		Element parentElement = getImportDataStagedModelElement(
			parentStagedModel);

		return getReferenceDataElement(parentElement, clazz, groupId, uuid);
	}

	@Override
	public List<Element> getReferenceDataElements(
		Element parentElement, Class<?> clazz, String referenceType) {

		return getReferenceDataElements(
			getReferenceElements(
				parentElement, clazz.getName(), 0, null, null, referenceType),
			clazz);
	}

	@Override
	public List<Element> getReferenceDataElements(
		StagedModel parentStagedModel, Class<?> clazz) {

		return getReferenceDataElements(parentStagedModel, clazz, null);
	}

	@Override
	public List<Element> getReferenceDataElements(
		StagedModel parentStagedModel, Class<?> clazz, String referenceType) {

		return getReferenceDataElements(
			getReferenceElements(
				parentStagedModel, clazz.getName(), null, referenceType),
			clazz);
	}

	@Override
	public Element getReferenceElement(
		Element parentElement, Class<?> clazz, long groupId, String uuid,
		String referenceType) {

		List<Element> referenceElements = getReferenceElements(
			parentElement, clazz.getName(), groupId, uuid, null, referenceType);

		if (!referenceElements.isEmpty()) {
			return referenceElements.get(0);
		}

		return null;
	}

	@Override
	public Element getReferenceElement(
		StagedModel parentStagedModel, String className, Serializable classPK) {

		List<Element> referenceElements = getReferenceElements(
			parentStagedModel, className, classPK, null);

		if (!referenceElements.isEmpty()) {
			return referenceElements.get(0);
		}

		return null;
	}

	@Override
	public Element getReferenceElement(String className, Serializable classPK) {
		Element parentElement = getImportDataRootElement();

		List<Element> referenceElements = getReferenceElements(
			parentElement, className, 0, null, classPK, null);

		if (ListUtil.isNotEmpty(referenceElements)) {
			return referenceElements.get(0);
		}

		return null;
	}

	@Override
	public List<Element> getReferenceElements(
		StagedModel parentStagedModel, Class<?> clazz) {

		return getReferenceElements(
			parentStagedModel, clazz.getName(), null, null);
	}

	@Override
	public String getRootPortletId() {
		return _rootPortletId;
	}

	@Override
	public Set<String> getScopedPrimaryKeys() {
		return _scopedPrimaryKeys;
	}

	@Override
	public long getScopeGroupId() {
		return _scopeGroupId;
	}

	@Override
	public String getScopeLayoutUuid() {
		return _scopeLayoutUuid;
	}

	@Override
	public String getScopeType() {
		return _scopeType;
	}

	@Override
	public long getSourceCompanyGroupId() {
		return _sourceCompanyGroupId;
	}

	@Override
	public long getSourceCompanyId() {
		return _sourceCompanyId;
	}

	@Override
	public long getSourceGroupId() {
		return _sourceGroupId;
	}

	@Override
	public long getSourceUserPersonalSiteGroupId() {
		return _sourceUserPersonalSiteGroupId;
	}

	@Override
	public Date getStartDate() {
		return _startDate;
	}

	@Override
	public String getType() {
		return _type;
	}

	@Override
	public long getUserId(String userUuid) {
		return _userIdStrategy.getUserId(userUuid);
	}

	@Override
	public UserIdStrategy getUserIdStrategy() {
		return _userIdStrategy;
	}

	@Override
	public long getUserPersonalSiteGroupId() {
		return _userPersonalSiteGroupId;
	}

	@Override
	public byte[] getZipEntryAsByteArray(String path) {
		if (!Validator.isFilePath(path, false)) {
			return null;
		}

		return getZipReader().getEntryAsByteArray(path);
	}

	@Override
	public InputStream getZipEntryAsInputStream(String path) {
		if (!Validator.isFilePath(path, false)) {
			return null;
		}

		return getZipReader().getEntryAsInputStream(path);
	}

	@Override
	public Object getZipEntryAsObject(Element element, String path) {
		Object object = getZipEntryAsObject(path);

		if (object instanceof TypedModel) {
			Attribute classNameAttribute = element.attribute(
				"attached-class-name");

			if (classNameAttribute != null) {
				TypedModel typedModel = (TypedModel)object;

				typedModel.setClassNameId(
					PortalUtil.getClassNameId(classNameAttribute.getText()));
			}
		}

		return object;
	}

	@Override
	public Object getZipEntryAsObject(String path) {
		return _objectsMap.computeIfAbsent(
			path, key -> fromXML(getZipEntryAsString(key)));
	}

	@Override
	public String getZipEntryAsString(String path) {
		if (!Validator.isFilePath(path, false)) {
			return null;
		}

		return getZipReader().getEntryAsString(path);
	}

	@Override
	public List<String> getZipFolderEntries(String path) {
		if (!Validator.isFilePath(path, false)) {
			return null;
		}

		return getZipReader().getFolderEntries(path);
	}

	@Override
	public ZipReader getZipReader() {
		return _zipReader;
	}

	@Override
	public ZipWriter getZipWriter() {
		return _zipWriter;
	}

	@Override
	public boolean hasDateRange() {
		if (_startDate != null) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasNotUniquePerLayout(String dataKey) {
		return _notUniquePerLayout.contains(dataKey);
	}

	@Override
	public boolean hasPrimaryKey(Class<?> clazz, String primaryKey) {
		return _primaryKeys.contains(
			_getPrimaryKeyString(clazz, (Serializable)primaryKey));
	}

	@Override
	public boolean hasScopedPrimaryKey(Class<?> clazz, String primaryKey) {
		return _scopedPrimaryKeys.contains(
			_getPrimaryKeyString(clazz, (Serializable)primaryKey));
	}

	@Override
	public void importClassedModel(
			ClassedModel classedModel, ClassedModel newClassedModel)
		throws PortalException {

		importClassedModel(
			classedModel, newClassedModel, classedModel.getModelClass());
	}

	@Override
	public void importClassedModel(
			ClassedModel classedModel, ClassedModel newClassedModel,
			Class<?> clazz)
		throws PortalException {

		if (!_isResourceMain(classedModel)) {
			return;
		}

		Serializable primaryKeyObj =
			ExportImportClassedModelUtil.getPrimaryKeyObj(classedModel);

		Serializable newPrimaryKeyObj =
			ExportImportClassedModelUtil.getPrimaryKeyObj(newClassedModel);

		Map<Serializable, Serializable> newPrimaryKeysMap =
			(Map<Serializable, Serializable>)getNewPrimaryKeysMap(clazz);

		newPrimaryKeysMap.put(primaryKeyObj, newPrimaryKeyObj);

		if ((classedModel instanceof StagedGroupedModel) &&
			(newClassedModel instanceof StagedGroupedModel)) {

			Map<Long, Long> groupIds = (Map<Long, Long>)getNewPrimaryKeysMap(
				Group.class);

			StagedGroupedModel stagedGroupedModel =
				(StagedGroupedModel)classedModel;

			if (!groupIds.containsKey(stagedGroupedModel.getGroupId())) {
				StagedGroupedModel newStagedGroupedModel =
					(StagedGroupedModel)newClassedModel;

				groupIds.put(
					stagedGroupedModel.getGroupId(),
					newStagedGroupedModel.getGroupId());
			}
		}

		_importWorkflowDefinitionLink(clazz, newClassedModel);

		importLocks(
			clazz, String.valueOf(primaryKeyObj),
			String.valueOf(newPrimaryKeyObj));
		importPermissions(clazz, primaryKeyObj, newPrimaryKeyObj);
	}

	@Override
	public void importLocks(Class<?> clazz, String key, String newKey)
		throws PortalException {

		Lock lock = _locksMap.get(
			_getPrimaryKeyString(clazz, (Serializable)key));

		if (lock == null) {
			return;
		}

		long userId = getUserId(lock.getUserUuid());

		long expirationTime = 0;

		if (lock.getExpirationDate() != null) {
			Date expirationDate = lock.getExpirationDate();

			expirationTime = expirationDate.getTime();
		}

		_lockManager.lock(
			userId, clazz.getName(), newKey, lock.getOwner(),
			lock.isInheritable(), expirationTime);
	}

	@Override
	public void importPermissions(
			Class<?> clazz, Serializable classPK, Serializable newClassPK)
		throws PortalException {

		importPermissions(
			clazz.getName(), GetterUtil.getLong(classPK),
			GetterUtil.getLong(newClassPK));
	}

	@Override
	public void importPermissions(
			String resourceName, long resourcePK, long newResourcePK)
		throws PortalException {

		if (!MapUtil.getBoolean(
				_parameterMap, PortletDataHandlerKeys.PERMISSIONS)) {

			return;
		}

		List<KeyValuePair> permissionKeyValuePairs = _permissionsMap.get(
			_getPrimaryKeyString(resourceName, (Serializable)resourcePK));

		if (permissionKeyValuePairs == null) {
			return;
		}

		Map<Long, Set<String>> existingRoleIdsToActionIds =
			ExportImportPermissionUtil.getRoleIdsToActionIds(
				_companyId, resourceName, newResourcePK);

		Map<Long, String[]> importedRoleIdsToActionIds = new HashMap<>();

		for (KeyValuePair permissionKeyValuePair : permissionKeyValuePairs) {
			String roleName = permissionKeyValuePair.getKey();

			Team team = null;

			if (ExportImportPermissionUtil.isTeamRoleName(roleName)) {
				roleName = roleName.substring(
					ExportImportPermissionUtil.ROLE_TEAM_PREFIX.length());

				try {
					team = TeamLocalServiceUtil.getTeam(_groupId, roleName);
				}
				catch (NoSuchTeamException noSuchTeamException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Team " + roleName + " does not exist",
							noSuchTeamException);
					}

					continue;
				}
			}

			Role role = null;

			try {
				if (team != null) {
					role = RoleLocalServiceUtil.getTeamRole(
						_companyId, team.getTeamId());
				}
				else {
					role = RoleLocalServiceUtil.getRole(_companyId, roleName);
				}
			}
			catch (NoSuchRoleException noSuchRoleException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Role " + roleName + " does not exist",
						noSuchRoleException);
				}

				continue;
			}

			if (isOriginalPrivateLayout() &&
				resourceName.equals(Layout.class.getName()) &&
				roleName.equals(RoleConstants.GUEST) &&
				!_isGroupLayoutSetPrototype()) {

				continue;
			}

			String[] actionIds = StringUtil.split(
				permissionKeyValuePair.getValue());

			importedRoleIdsToActionIds.put(role.getRoleId(), actionIds);
		}

		Map<Long, String[]> roleIdsToActionIds =
			ExportImportPermissionUtil.
				mergeImportedPermissionsWithExistingPermissions(
					existingRoleIdsToActionIds, importedRoleIdsToActionIds);

		ExportImportPermissionUtil.updateResourcePermissions(
			_companyId, _groupId, resourceName, newResourcePK,
			roleIdsToActionIds);
	}

	@Override
	public void importPortalPermissions() throws PortalException {
		importPermissions(
			PortletKeys.PORTAL, getSourceCompanyId(), getCompanyId());
	}

	@Override
	public void importPortletPermissions(String resourceName)
		throws PortalException {

		if (getGroupId() != getScopeGroupId()) {
			return;
		}

		importPermissions(resourceName, getSourceGroupId(), getScopeGroupId());
	}

	@Override
	public boolean isDataStrategyMirror() {
		if (_dataStrategy.equals(PortletDataHandlerKeys.DATA_STRATEGY_MIRROR) ||
			_dataStrategy.equals(
				PortletDataHandlerKeys.DATA_STRATEGY_MIRROR_OVERWRITE)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isDataStrategyMirrorWithOverwriting() {
		return _dataStrategy.equals(
			PortletDataHandlerKeys.DATA_STRATEGY_MIRROR_OVERWRITE);
	}

	@Override
	public boolean isInitialPublication() {
		Group group = null;

		try {
			group = GroupLocalServiceUtil.getGroup(getGroupId());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		if (ExportImportThreadLocal.isStagingInProcess() && (group != null) &&
			group.hasStagingGroup()) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isMissingReference(Element referenceElement) {
		Attribute missingAttribute = referenceElement.attribute("missing");

		if ((missingAttribute != null) &&
			!GetterUtil.getBoolean(
				referenceElement.attributeValue("missing"))) {

			return false;
		}

		if (_missingReferences.isEmpty()) {
			List<Element> missingReferenceElements =
				_missingReferencesElement.elements();

			for (Element missingReferenceElement : missingReferenceElements) {
				if (Validator.isNotNull(
						missingReferenceElement.attributeValue(
							"element-path"))) {

					continue;
				}

				String missingReferenceClassName =
					missingReferenceElement.attributeValue("class-name");
				String missingReferenceClassPK =
					missingReferenceElement.attributeValue("class-pk");

				String missingReferenceKey = _getReferenceKey(
					missingReferenceClassName, missingReferenceClassPK);

				_missingReferences.add(missingReferenceKey);
			}
		}

		String className = referenceElement.attributeValue("class-name");
		String classPK = referenceElement.attributeValue("class-pk");

		return _missingReferences.contains(
			_getReferenceKey(className, classPK));
	}

	@Override
	public boolean isModelCounted(String className, Serializable classPK) {
		String modelCountedPrimaryKey = StringBundler.concat(
			className, StringPool.POUND, classPK);

		return addPrimaryKey(String.class, modelCountedPrimaryKey);
	}

	@Override
	public boolean isOriginalPrivateLayout() {
		return _originalPrivateLayout;
	}

	@Override
	public boolean isPathExportedInScope(String path) {
		return addScopedPrimaryKey(String.class, path);
	}

	@Override
	public boolean isPathNotProcessed(String path) {
		return !isPathProcessed(path);
	}

	@Override
	public boolean isPathProcessed(String path) {
		addScopedPrimaryKey(String.class, path);

		return addPrimaryKey(String.class, path);
	}

	@Override
	public boolean isPerformDirectBinaryImport() {
		return MapUtil.getBoolean(
			_parameterMap, PortletDataHandlerKeys.PERFORM_DIRECT_BINARY_IMPORT);
	}

	@Override
	public boolean isPrivateLayout() {
		return _privateLayout;
	}

	@Override
	public boolean isStagedModelCounted(StagedModel stagedModel) {
		return isModelCounted(
			ExportImportClassedModelUtil.getClassName(stagedModel),
			ExportImportClassedModelUtil.getPrimaryKeyObj(stagedModel));
	}

	@Override
	public boolean isValidateExistingDataHandler() {
		return _validateExistingDataHandler;
	}

	/**
	 * @see #addDateRangeCriteria(DynamicQuery, String)
	 */
	@Override
	public boolean isWithinDateRange(Date modifiedDate) {
		if (!hasDateRange()) {
			return true;
		}
		else if ((_startDate.compareTo(modifiedDate) <= 0) &&
				 _endDate.after(modifiedDate)) {

			return true;
		}

		return false;
	}

	@Override
	public void putNotUniquePerLayout(String dataKey) {
		_notUniquePerLayout.add(dataKey);
	}

	@Override
	public void removePrimaryKey(String path) {
		String primaryKeyString = _getPrimaryKeyString(String.class, path);

		_primaryKeys.remove(primaryKeyString);
		_scopedPrimaryKeys.remove(primaryKeyString);
	}

	@Override
	public void setClassLoader(ClassLoader classLoader) {
		_xStream.setClassLoader(classLoader);
	}

	@Override
	public void setCompanyGroupId(long companyGroupId) {
		_companyGroupId = companyGroupId;
	}

	@Override
	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	@Override
	public void setDataStrategy(String dataStrategy) {
		_dataStrategy = dataStrategy;
	}

	@Override
	public void setEndDate(Date endDate) {
		_endDate = endDate;
	}

	@Override
	public void setExportDataRootElement(Element exportDataRootElement) {
		_exportDataElementsMap.clear();

		_exportDataRootElement = exportDataRootElement;
	}

	@Override
	public void setExportImportProcessId(String exportImportProcessId) {
		_exportImportProcessId = exportImportProcessId;
	}

	@Override
	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	@Override
	public void setImportDataElementCacheEnabled(
		boolean importDataElementCacheEnabled) {

		if (importDataElementCacheEnabled) {
			_importDataElementsMap = new HashMap<>();
			_importDataGroupElements = new HashMap<>();
		}
		else {
			_importDataElementsMap = null;
			_importDataGroupElements = null;
		}
	}

	@Override
	public void setImportDataRootElement(Element importDataRootElement) {
		if (_importDataElementsMap != null) {
			_importDataElementsMap.clear();
			_importDataGroupElements.clear();
		}

		_importDataRootElement = importDataRootElement;
	}

	@Override
	public void setLayoutIds(long[] layoutIds) {
		_layoutIds = layoutIds;
	}

	@Override
	public void setLayoutSetPrototypeUuid(String layoutSetPrototypeUuid) {
		_layoutSetPrototypeUuid = layoutSetPrototypeUuid;
	}

	@Override
	public void setManifestSummary(ManifestSummary manifestSummary) {
		_manifestSummary = manifestSummary;
	}

	@Override
	public void setMissingReferencesElement(Element missingReferencesElement) {
		_missingReferencesElement = missingReferencesElement;
	}

	@Override
	public void setNewLayouts(List<Layout> newLayouts) {
		_newLayouts = newLayouts;
	}

	@Override
	public void setOriginalPrivateLayout(boolean originalPrivateLayout) {
		_originalPrivateLayout = originalPrivateLayout;
	}

	@Override
	public void setParameterMap(Map<String, String[]> parameterMap) {
		_parameterMap = parameterMap;
	}

	@Override
	public void setPlid(long plid) {
		_plid = plid;
	}

	@Override
	public void setPortletId(String portletId) {
		_portletId = portletId;

		if (Validator.isNotNull(portletId)) {
			_rootPortletId = PortletIdCodec.decodePortletName(portletId);
		}
		else {
			_rootPortletId = null;
		}
	}

	@Override
	public void setPrivateLayout(boolean privateLayout) {
		_privateLayout = privateLayout;
	}

	@Override
	public void setScopeGroupId(long scopeGroupId) {
		_scopeGroupId = scopeGroupId;
	}

	@Override
	public void setScopeLayoutUuid(String scopeLayoutUuid) {
		_scopeLayoutUuid = scopeLayoutUuid;
	}

	@Override
	public void setScopeType(String scopeType) {
		_scopeType = scopeType;
	}

	@Override
	public void setSourceCompanyGroupId(long sourceCompanyGroupId) {
		_sourceCompanyGroupId = sourceCompanyGroupId;
	}

	@Override
	public void setSourceCompanyId(long sourceCompanyId) {
		_sourceCompanyId = sourceCompanyId;
	}

	@Override
	public void setSourceGroupId(long sourceGroupId) {
		_sourceGroupId = sourceGroupId;
	}

	@Override
	public void setSourceUserPersonalSiteGroupId(
		long sourceUserPersonalSiteGroupId) {

		_sourceUserPersonalSiteGroupId = sourceUserPersonalSiteGroupId;
	}

	@Override
	public void setStartDate(Date startDate) {
		_startDate = startDate;
	}

	@Override
	public void setType(String type) {
		_type = type;
	}

	@Override
	public void setUserIdStrategy(UserIdStrategy userIdStrategy) {
		_userIdStrategy = userIdStrategy;
	}

	@Override
	public void setUserPersonalSiteGroupId(long userPersonalSiteGroupId) {
		_userPersonalSiteGroupId = userPersonalSiteGroupId;
	}

	@Override
	public void setValidateExistingDataHandler(
		boolean validateExistingDataHandler) {

		_validateExistingDataHandler = validateExistingDataHandler;
	}

	@Override
	public void setZipReader(ZipReader zipReader) {
		_zipReader = zipReader;
	}

	@Override
	public void setZipWriter(ZipWriter zipWriter) {
		_zipWriter = zipWriter;
	}

	@Override
	public String toXML(Object object) {
		return _xStream.toXML(object);
	}

	protected void addExpando(
		Element element, String path, ClassedModel classedModel,
		Class<?> clazz) {

		String className = clazz.getName();

		if (!_expandoColumnsMap.containsKey(className)) {
			List<ExpandoColumn> expandoColumns =
				ExpandoColumnLocalServiceUtil.getDefaultTableColumns(
					_companyId, className);

			for (ExpandoColumn expandoColumn : expandoColumns) {
				addPermissions(
					ExpandoColumn.class,
					Long.valueOf(expandoColumn.getColumnId()));
			}

			_expandoColumnsMap.put(className, expandoColumns);

			for (ExpandoColumn expandoColumn : expandoColumns) {
				StagedExpandoColumn stagedExpandoColumn =
					ModelAdapterUtil.adapt(
						expandoColumn, ExpandoColumn.class,
						StagedExpandoColumn.class);

				addReferenceElement(
					classedModel, element, stagedExpandoColumn,
					REFERENCE_TYPE_DEPENDENCY, true);
			}
		}

		ExpandoBridge expandoBridge = classedModel.getExpandoBridge();

		if (expandoBridge == null) {
			return;
		}

		Map<String, Serializable> expandoBridgeAttributes =
			expandoBridge.getAttributes();

		if (!expandoBridgeAttributes.isEmpty()) {
			String expandoPath = ExportImportPathUtil.getExpandoPath(path);

			element.addAttribute("expando-path", expandoPath);

			addZipEntry(expandoPath, expandoBridgeAttributes);
		}
	}

	protected ServiceContext createServiceContext(
		Element element, ClassedModel classedModel, Class<?> clazz) {

		ServiceContext serviceContext = new ServiceContext();

		// Theme display

		serviceContext.setCompanyId(getCompanyId());
		serviceContext.setScopeGroupId(getScopeGroupId());

		// Dates

		if (classedModel instanceof AuditedModel) {
			AuditedModel auditedModel = (AuditedModel)classedModel;

			serviceContext.setCreateDate(auditedModel.getCreateDate());
			serviceContext.setModifiedDate(auditedModel.getModifiedDate());
			serviceContext.setUserId(getUserId(auditedModel));
		}
		else if (classedModel instanceof StagedModel) {
			StagedModel stagedModel = (StagedModel)classedModel;

			serviceContext.setCreateDate(stagedModel.getCreateDate());
			serviceContext.setModifiedDate(stagedModel.getModifiedDate());
		}

		// Permissions

		String xml = getZipEntryAsString(
			ExportImportPathUtil.getSourceRootPath(this) +
				"/portlet-data-permissions.xml");

		if (!MapUtil.getBoolean(
				_parameterMap, PortletDataHandlerKeys.PERMISSIONS) ||
			Validator.isNull(xml)) {

			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);
		}

		// Asset

		if (_isResourceMain(classedModel)) {
			Serializable classPKObj =
				ExportImportClassedModelUtil.getPrimaryKeyObj(classedModel);

			serviceContext.setAssetCategoryIds(
				getAssetCategoryIds(clazz, classPKObj));
			serviceContext.setAssetTagNames(
				getAssetTagNames(clazz, classPKObj));
		}

		if (element != null) {
			Attribute assetPriorityAttribute = element.attribute(
				"asset-entry-priority");

			if (assetPriorityAttribute != null) {
				serviceContext.setAssetPriority(
					GetterUtil.getDouble(assetPriorityAttribute.getValue()));
			}
		}

		// Expando

		String expandoPath = null;

		if (element != null) {
			expandoPath = element.attributeValue("expando-path");
		}
		else {
			expandoPath = ExportImportPathUtil.getExpandoPath(
				ExportImportPathUtil.getModelPath(
					this, classedModel.getModelClassName(),
					ExportImportClassedModelUtil.getClassPK(classedModel)));
		}

		if (Validator.isNotNull(expandoPath)) {
			try {
				Map<String, Serializable> expandoBridgeAttributes =
					(Map<String, Serializable>)getZipEntryAsObject(expandoPath);

				if (expandoBridgeAttributes != null) {
					List<String> warningMessages =
						ExpandoUtil.fillMissingDefaultLocaleValues(
							expandoBridgeAttributes);

					if (!warningMessages.isEmpty()) {
						String externalReferenceCode = null;

						if (classedModel instanceof
								ExternalReferenceCodeModel) {

							ExternalReferenceCodeModel
								externalReferenceCodeModel =
									(ExternalReferenceCodeModel)classedModel;

							externalReferenceCode =
								externalReferenceCodeModel.
									getExternalReferenceCode();
						}

						ExportImportReportEntryLocalServiceUtil.
							getOrAddExportImportReportEntry(
								getGroupId(), getCompanyId(),
								externalReferenceCode,
								ExportImportClassedModelUtil.getClassNameId(
									classedModel),
								ExportImportClassedModelUtil.getClassPK(
									classedModel),
								GetterUtil.getLong(getExportImportProcessId()),
								ExportImportReportEntryConstants.TYPE_ERROR,
								StringUtil.merge(warningMessages, "\n"), null,
								clazz.getName());
					}

					serviceContext.setExpandoBridgeAttributes(
						expandoBridgeAttributes);
				}
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		// Workflow

		if (classedModel instanceof WorkflowedModel) {
			WorkflowedModel workflowedModel = (WorkflowedModel)classedModel;

			if (workflowedModel.getStatus() ==
					WorkflowConstants.STATUS_APPROVED) {

				serviceContext.setWorkflowAction(
					WorkflowConstants.ACTION_PUBLISH);
			}
			else if (workflowedModel.getStatus() ==
						WorkflowConstants.STATUS_DRAFT) {

				serviceContext.setWorkflowAction(
					WorkflowConstants.ACTION_SAVE_DRAFT);
			}
		}

		return serviceContext;
	}

	protected String[] getAssetTagNames(String key) {
		String[] assetTagNames = _assetTagNamesMap.get(key);

		if (assetTagNames == null) {
			return new String[0];
		}

		return assetTagNames;
	}

	protected Element getExportDataGroupElement(String name) {
		if (_exportDataRootElement == null) {
			PortletDataException portletDataException =
				new PortletDataException(
					PortletDataException.EXPORT_DATA_GROUP_ELEMENT);

			portletDataException.setStagedModelClassName(name);

			throw new SystemException(portletDataException);
		}

		Element groupElement = _exportDataRootElement.element(name);

		if (groupElement == null) {
			groupElement = _exportDataRootElement.addElement(name);
		}

		return groupElement;
	}

	protected Element getImportDataGroupElement(String name) {
		if (_importDataRootElement == null) {
			PortletDataException portletDataException =
				new PortletDataException(
					PortletDataException.IMPORT_DATA_GROUP_ELEMENT);

			portletDataException.setStagedModelClassName(name);

			throw new SystemException(portletDataException);
		}

		if (Validator.isNull(name)) {
			return SAXReaderUtil.createElement("EMPTY-ELEMENT");
		}

		if (_importDataGroupElements != null) {
			Element groupElement = _importDataGroupElements.get(name);

			if (groupElement != null) {
				return groupElement;
			}
		}

		Element groupElement = _deepSearchForFirstChildElement(
			_importDataRootElement, name);

		if (groupElement == null) {
			groupElement = SAXReaderUtil.createElement("EMPTY-ELEMENT");
		}

		if (_importDataGroupElements != null) {
			_importDataGroupElements.put(name, groupElement);
		}

		return groupElement;
	}

	protected List<Element> getReferenceDataElements(
		List<Element> referenceElements, Class<?> clazz) {

		return TransformUtil.transform(
			referenceElements,
			referenceElement -> {
				Element referenceDataElement = null;

				String path = referenceElement.attributeValue("path");

				if (Validator.isNotNull(path)) {
					referenceDataElement = getImportDataElement(
						clazz.getSimpleName(), "path", path);
				}
				else {
					String groupId = referenceElement.attributeValue(
						"group-id");
					String uuid = referenceElement.attributeValue("uuid");

					Element groupElement = getImportDataGroupElement(
						clazz.getSimpleName());

					if (_importDataElementsMap == null) {
						referenceDataElement =
							_searchFirstChildElementWithPredicate(
								groupElement, "staged-model",
								childElement -> {
									if (!Objects.equals(
											childElement.attributeValue("uuid"),
											uuid)) {

										return false;
									}

									if ((groupId == null) ||
										Objects.equals(
											childElement.attributeValue(
												"group-id"),
											groupId)) {

										return true;
									}

									return false;
								});
					}
					else {
						Map<String, List<Element>> importDataElements =
							_getImportDataElements(
								clazz.getSimpleName(), "uuid", groupElement);

						referenceDataElement = _searchFirstElementWithPredicate(
							importDataElements.get(uuid),
							element ->
								(groupId == null) ||
								Objects.equals(
									element.attributeValue("group-id"),
									groupId));
					}
				}

				if (referenceDataElement == null) {
					return null;
				}

				return referenceDataElement;
			});
	}

	protected List<Element> getReferenceElements(
		Element parentElement, String className, long groupId, String uuid,
		Serializable classPK, String referenceType) {

		if (parentElement == null) {
			return Collections.emptyList();
		}

		Element referencesElement = parentElement.element("references");

		if (referencesElement == null) {
			return Collections.emptyList();
		}

		return TransformUtil.transform(
			referencesElement.elements("reference"),
			referenceElement -> {
				if (!Objects.equals(
						referenceElement.attributeValue("class-name"),
						className) ||
					((groupId > 0) &&
					 !Objects.equals(
						 referenceElement.attributeValue("group-id"),
						 String.valueOf(groupId)))) {

					return null;
				}

				if (Validator.isNotNull(uuid) &&
					!Objects.equals(
						referenceElement.attributeValue("uuid"), uuid)) {

					return null;
				}

				if (Validator.isNotNull(classPK) &&
					!Objects.equals(
						referenceElement.attributeValue("class-pk"),
						String.valueOf(classPK))) {

					return null;
				}

				if ((referenceType != null) &&
					!Objects.equals(
						referenceElement.attributeValue("type"),
						String.valueOf(referenceType))) {

					return null;
				}

				return referenceElement;
			});
	}

	protected List<Element> getReferenceElements(
		StagedModel parentStagedModel, String className, Serializable classPK,
		String referenceType) {

		Element stagedModelElement = getImportDataStagedModelElement(
			parentStagedModel);

		return getReferenceElements(
			stagedModelElement, className, 0, null, classPK, referenceType);
	}

	protected long getUserId(AuditedModel auditedModel) {
		try {
			return getUserId(auditedModel.getUserUuid());
		}
		catch (SystemException systemException) {
			_log.error(systemException);
		}

		return 0;
	}

	private void _addAssetLinks(long classNameId, long classPK) {
		List<AssetLink> assetLinks = AssetLinkLocalServiceUtil.getLinks(
			classNameId, classPK);

		for (AssetLink assetLink : assetLinks) {
			_assetLinkIds.add(assetLink.getLinkId());
		}
	}

	private Element _addReferenceElement(
		ClassedModel referrerClassedModel, Element element,
		ClassedModel classedModel, String className, String binPath,
		String referenceType, boolean missing) {

		if (!missing) {
			Element originalImportDataRootElement = getImportDataRootElement();

			try {
				setImportDataRootElement(element);

				Element referenceElement = getReferenceElement(
					ExportImportClassedModelUtil.getClassName(classedModel),
					classedModel.getPrimaryKeyObj());

				if (referenceElement != null) {
					return referenceElement;
				}
			}
			finally {
				setImportDataRootElement(originalImportDataRootElement);
			}
		}

		Element referenceElement = null;

		if (missing) {
			Element referencesElement = _missingReferencesElement;

			referenceElement = referencesElement.addElement(
				"missing-reference");
		}
		else {
			Element referencesElement = element.element("references");

			if (referencesElement == null) {
				referencesElement = element.addElement("references");
			}

			referenceElement = referencesElement.addElement("reference");
		}

		referenceElement.addAttribute("class-name", className);

		referenceElement.addAttribute(
			"class-pk", String.valueOf(classedModel.getPrimaryKeyObj()));

		_populateClassNameAttribute(classedModel, referenceElement);

		if (missing) {
			if (classedModel instanceof StagedModel) {
				referenceElement.addAttribute(
					"display-name",
					StagedModelDataHandlerUtil.getDisplayName(
						(StagedModel)classedModel));
			}
			else {
				referenceElement.addAttribute(
					"display-name",
					String.valueOf(classedModel.getPrimaryKeyObj()));
			}
		}

		if (classedModel instanceof
				ExternalReferenceCodeModel externalReferenceCodeModel) {

			referenceElement.addAttribute(
				"external-reference-code",
				externalReferenceCodeModel.getExternalReferenceCode());
		}

		long groupId = _getGroupId(classedModel);

		if (groupId > 0) {
			referenceElement.addAttribute("group-id", String.valueOf(groupId));

			try {
				Group group = GroupLocalServiceUtil.getGroup(groupId);

				long liveGroupId = group.getLiveGroupId();

				if (group.isStagedRemotely()) {
					liveGroupId = group.getRemoteLiveGroupId();
				}

				if (liveGroupId == GroupConstants.DEFAULT_LIVE_GROUP_ID) {
					liveGroupId = group.getGroupId();
				}

				referenceElement.addAttribute("group-key", group.getGroupKey());
				referenceElement.addAttribute(
					"live-group-id", String.valueOf(liveGroupId));

				if (group.isLayout()) {
					try {
						Layout scopeLayout = LayoutLocalServiceUtil.getLayout(
							group.getClassPK());

						referenceElement.addAttribute(
							"scope-layout-uuid", scopeLayout.getUuid());
					}
					catch (NoSuchLayoutException noSuchLayoutException) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								"Unable to find layout " + group.getClassPK(),
								noSuchLayoutException);
						}
					}
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to find group " + groupId, exception);
				}
			}
		}

		if (Validator.isNotNull(binPath)) {
			referenceElement.addAttribute("path", binPath);
		}

		referenceElement.addAttribute("type", referenceType);

		if (missing) {
			referenceElement.addAttribute(
				"referrer-class-name",
				ExportImportClassedModelUtil.getClassName(
					referrerClassedModel));

			if (referrerClassedModel instanceof PortletModel) {
				Portlet portlet = (Portlet)referrerClassedModel;

				referenceElement.addAttribute(
					"referrer-display-name", portlet.getRootPortletId());
			}
			else if (referrerClassedModel instanceof StagedModel) {
				StagedModel referrerStagedModel =
					(StagedModel)referrerClassedModel;

				referenceElement.addAttribute(
					"referrer-display-name",
					StagedModelDataHandlerUtil.getDisplayName(
						referrerStagedModel));
			}
		}

		if (classedModel instanceof StagedModel) {
			StagedModel stagedModel = (StagedModel)classedModel;

			referenceElement.addAttribute("uuid", stagedModel.getUuid());
			referenceElement.addAttribute(
				"company-id", String.valueOf(stagedModel.getCompanyId()));

			Map<String, String> referenceAttributes =
				StagedModelDataHandlerUtil.getReferenceAttributes(
					this, stagedModel);

			for (Map.Entry<String, String> referenceAttribute :
					referenceAttributes.entrySet()) {

				referenceElement.addAttribute(
					referenceAttribute.getKey(), referenceAttribute.getValue());
			}
		}

		return referenceElement;
	}

	private void _addWorkflowDefinitionLink(ClassedModel classedModel)
		throws PortletDataException {

		if (classedModel instanceof StagedGroupedModel ||
			classedModel instanceof WorkflowedModel) {

			StagedGroupedModel stagedGroupedModel =
				(StagedGroupedModel)classedModel;

			List<WorkflowDefinitionLink> workflowDefinitionLinks =
				WorkflowDefinitionLinkLocalServiceUtil.
					fetchWorkflowDefinitionLinks(
						stagedGroupedModel.getCompanyId(),
						stagedGroupedModel.getGroupId(),
						ExportImportClassedModelUtil.getClassName(
							stagedGroupedModel),
						ExportImportClassedModelUtil.getClassPK(
							stagedGroupedModel));

			for (WorkflowDefinitionLink workflowDefinitionLink :
					workflowDefinitionLinks) {

				StagedGroupedWorkflowDefinitionLink
					stagedGroupedWorkflowDefinitionLink =
						ModelAdapterUtil.adapt(
							workflowDefinitionLink,
							WorkflowDefinitionLink.class,
							StagedGroupedWorkflowDefinitionLink.class);

				StagedModelDataHandlerUtil.exportStagedModel(
					this, stagedGroupedWorkflowDefinitionLink);
			}
		}
	}

	private Element _deepSearchForFirstChildElement(
		Element parentElement, String childElementName) {

		Queue<Element> queue = new LinkedList<>();

		queue.add(parentElement);

		Element currentElement = null;

		while ((currentElement = queue.poll()) != null) {
			for (Element childElement : currentElement.elements()) {
				if (childElementName.equals(childElement.getName())) {
					return childElement;
				}

				queue.add(childElement);
			}
		}

		return null;
	}

	private Element _getDataElement(
		Element parentElement, String attribute, String value) {

		if (parentElement == null) {
			return null;
		}

		return _searchFirstChildElementWithPredicate(
			parentElement, "staged-model",
			childElement -> Objects.equals(
				childElement.attributeValue(attribute), value));
	}

	private String _getExportDataElementKey(String attribute, String value) {
		return StringBundler.concat(attribute, CharPool.POUND, value);
	}

	private Map<String, Element> _getExportDataElements(Element groupElement) {
		Map<String, Element> exportDataElements = _exportDataElementsMap.get(
			groupElement.getName());

		if (exportDataElements != null) {
			return exportDataElements;
		}

		exportDataElements = new HashMap<>();

		for (Element element : groupElement.elements("staged-model")) {
			exportDataElements.putIfAbsent(
				_getExportDataElementKey(
					"path", element.attributeValue("path")),
				element);
			exportDataElements.putIfAbsent(
				_getExportDataElementKey(
					"uuid", element.attributeValue("uuid")),
				element);
		}

		_exportDataElementsMap.put(groupElement.getName(), exportDataElements);

		return exportDataElements;
	}

	private long _getGroupId(ClassedModel classedModel) {
		if (classedModel instanceof GroupedModel) {
			GroupedModel groupedModel = (GroupedModel)classedModel;

			return groupedModel.getGroupId();
		}

		return 0;
	}

	private Map<String, List<Element>> _getImportDataElements(
		String name, String attribute, Element groupElement) {

		String key = StringBundler.concat(name, CharPool.POUND, attribute);

		Map<String, List<Element>> importDataElements =
			_importDataElementsMap.get(key);

		if (importDataElements != null) {
			return importDataElements;
		}

		importDataElements = new HashMap<>();

		for (Element element : groupElement.elements("staged-model")) {
			List<Element> elements = importDataElements.computeIfAbsent(
				element.attributeValue(attribute),
				attributeValue -> new ArrayList<>());

			elements.add(element);
		}

		_importDataElementsMap.put(key, importDataElements);

		return importDataElements;
	}

	private long _getOldPrimaryKey(Map<Long, Long> map, long value) {
		for (Map.Entry<Long, Long> entry : map.entrySet()) {
			if (entry.getValue() == value) {
				return entry.getKey();
			}
		}

		return 0;
	}

	private String _getPortletXmlPath() {
		if (_exportDataRootElement == null) {
			return StringPool.BLANK;
		}

		Element element = _exportDataRootElement.getParent();

		Element parentElement = null;

		while (element != null) {
			parentElement = element;

			element = element.getParent();
		}

		if (parentElement == null) {
			return StringPool.BLANK;
		}

		return parentElement.attributeValue("self-path");
	}

	private String _getPrimaryKeyString(
		Class<?> clazz, Serializable primaryKey) {

		return _getPrimaryKeyString(clazz.getName(), primaryKey);
	}

	private String _getPrimaryKeyString(
		String className, Serializable primaryKey) {

		return StringBundler.concat(className, StringPool.POUND, primaryKey);
	}

	private String _getReferenceKey(ClassedModel classedModel) {
		return _getReferenceKey(
			ExportImportClassedModelUtil.getClassName(classedModel),
			String.valueOf(classedModel.getPrimaryKeyObj()));
	}

	private String _getReferenceKey(String className, String classPK) {
		return StringBundler.concat(className, StringPool.POUND, classPK);
	}

	private void _importWorkflowDefinitionLink(
			Class<?> clazz, ClassedModel classedModel)
		throws PortletDataException {

		Element stagedGroupedWorkflowDefinitionLinkElements =
			getImportDataGroupElement(
				StagedGroupedWorkflowDefinitionLink.class);

		String className = clazz.getName();

		Map<Long, Long> primaryKeys = (Map<Long, Long>)getNewPrimaryKeysMap(
			className);

		for (Element stagedGroupedWorkflowDefinitionLinkElement :
				stagedGroupedWorkflowDefinitionLinkElements.elements()) {

			String referrerClassName = GetterUtil.getString(
				stagedGroupedWorkflowDefinitionLinkElement.attributeValue(
					"referrer-class-name"));
			long referrerClassPK = GetterUtil.getLong(
				stagedGroupedWorkflowDefinitionLinkElement.attributeValue(
					"referrer-class-pk"));

			long newPrimaryKey = GetterUtil.getLong(
				classedModel.getPrimaryKeyObj());

			long oldPrimaryKey = _getOldPrimaryKey(primaryKeys, newPrimaryKey);

			if (!referrerClassName.equals(className) ||
				(referrerClassPK != oldPrimaryKey)) {

				continue;
			}

			String displayName =
				stagedGroupedWorkflowDefinitionLinkElement.attributeValue(
					"display-name");

			if (Validator.isNull(displayName)) {
				continue;
			}

			WorkflowDefinition workflowDefinition = null;

			try {
				workflowDefinition =
					WorkflowDefinitionManagerUtil.
						liberalGetLatestWorkflowDefinition(
							getCompanyId(), displayName);
			}
			catch (WorkflowException workflowException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to get workflow definition with name " +
							displayName,
						workflowException);
				}

				continue;
			}

			long typePK = GetterUtil.getLong(
				stagedGroupedWorkflowDefinitionLinkElement.attributeValue(
					"type-pk"),
				-1);

			if (typePK != -1) {
				Map<Long, Long> ddmPrimaryKeys =
					(Map<Long, Long>)getNewPrimaryKeysMap(
						DDMStructure.class.getName());

				typePK = ddmPrimaryKeys.getOrDefault(typePK, typePK);
			}

			if ((workflowDefinition != null) &&
				!WorkflowDefinitionLinkLocalServiceUtil.
					hasWorkflowDefinitionLink(
						getCompanyId(), getScopeGroupId(), className,
						newPrimaryKey, typePK)) {

				PermissionChecker permissionChecker =
					PermissionThreadLocal.getPermissionChecker();

				try {
					WorkflowDefinitionLinkLocalServiceUtil.
						addWorkflowDefinitionLink(
							null, permissionChecker.getUserId(), getCompanyId(),
							getScopeGroupId(), className, newPrimaryKey, typePK,
							workflowDefinition.getName(),
							workflowDefinition.getVersion());
				}
				catch (PortalException portalException) {
					throw new PortletDataException(
						portalException.getMessage(), portalException);
				}
			}
		}
	}

	private void _initXStream() {
		ClassLoader classLoader =
			XStreamConfiguratorRegistryUtil.getConfiguratorsClassLoader(
				AggregateClassLoader.getAggregateClassLoader(
					PortalClassLoaderUtil.getClassLoader(),
					XStream.class.getClassLoader()));

		long modifiedCount = XStreamConfiguratorRegistryUtil.getModifiedCount();

		if ((_xStream != null) && (_modifiedCount == modifiedCount) &&
			classLoader.equals(_classLoader)) {

			return;
		}

		_modifiedCount = modifiedCount;

		_classLoader = classLoader;

		_xStream = new XStream(
			null, new XppDriver(), new ClassLoaderReference(classLoader));

		_xStream.omitField(HashMap.class, "cache_bitmask");

		_xStream.registerConverter(
			new ConverterAdapter(new TimestampConverter()),
			XStream.PRIORITY_VERY_HIGH);

		List<XStreamConfigurator> xStreamConfigurators =
			XStreamConfiguratorRegistryUtil.getXStreamConfigurators();

		if (xStreamConfigurators.isEmpty()) {
			return;
		}

		List<String> allowedTypeNames = new ArrayList<>();

		for (XStreamConfigurator xStreamConfigurator : xStreamConfigurators) {
			List<XStreamAlias> xStreamAliases =
				xStreamConfigurator.getXStreamAliases();

			if (ListUtil.isNotEmpty(xStreamAliases)) {
				for (XStreamAlias xStreamAlias : xStreamAliases) {
					_xStream.alias(
						xStreamAlias.getName(), xStreamAlias.getClazz());
				}
			}

			List<XStreamConverter> xStreamConverters =
				xStreamConfigurator.getXStreamConverters();

			if (ListUtil.isNotEmpty(xStreamConverters)) {
				for (XStreamConverter xStreamConverter : xStreamConverters) {
					_xStream.registerConverter(
						new ConverterAdapter(xStreamConverter),
						XStream.PRIORITY_VERY_HIGH);
				}
			}

			allowedTypeNames.addAll(
				TransformUtil.transform(
					xStreamConfigurator.getAllowedXStreamTypes(),
					xStreamType -> xStreamType.getTypeExpression()));
		}

		// For default permissions, first wipe than add default

		_xStream.addPermission(NoTypePermission.NONE);

		// Add permissions

		_xStream.addPermission(PrimitiveTypePermission.PRIMITIVES);
		_xStream.addPermission(
			XStreamStagedModelTypeHierarchyPermission.STAGED_MODELS);

		_xStream.allowTypes(_XSTREAM_DEFAULT_ALLOWED_TYPES);

		_xStream.allowTypeHierarchy(List.class);
		_xStream.allowTypeHierarchy(Map.class);
		_xStream.allowTypeHierarchy(Timestamp.class);
		_xStream.allowTypeHierarchy(Set.class);

		_xStream.allowTypes(allowedTypeNames.toArray(new String[0]));

		_xStream.allowTypesByWildcard(
			new String[] {
				"com.thoughtworks.xstream.mapper.DynamicProxyMapper*"
			});
	}

	private boolean _isGroupLayoutSetPrototype() throws PortalException {
		Group group = GroupLocalServiceUtil.getGroup(getGroupId());

		return group.isLayoutSetPrototype();
	}

	private boolean _isResourceMain(ClassedModel classedModel) {
		if (classedModel instanceof ResourcedModel) {
			ResourcedModel resourcedModel = (ResourcedModel)classedModel;

			return resourcedModel.isResourceMain();
		}

		return true;
	}

	private void _populateClassNameAttribute(
		ClassedModel classedModel, Element element) {

		if (classedModel instanceof TypedModel) {
			TypedModel typedModel = (TypedModel)classedModel;

			element.addAttribute(
				"attached-class-name", typedModel.getClassName());
		}
	}

	private Element _searchFirstChildElementWithPredicate(
		Element parentElement, String childElementName,
		Predicate<Element> childElementPredicate) {

		for (Element childElement : parentElement.elements(childElementName)) {
			if (childElementPredicate.test(childElement)) {
				return childElement;
			}
		}

		return null;
	}

	private Element _searchFirstElementWithPredicate(
		List<Element> elements, Predicate<Element> elementPredicate) {

		if (elements == null) {
			return null;
		}

		for (Element element : elements) {
			if (elementPredicate.test(element)) {
				return element;
			}
		}

		return null;
	}

	private static final Class<?>[] _XSTREAM_DEFAULT_ALLOWED_TYPES = {
		boolean[].class, byte[].class, Date.class, Date[].class, double[].class,
		float[].class, int[].class, Locale.class, long[].class, Number.class,
		Number[].class, short[].class, String.class, String[].class
	};

	private static final Log _log = LogFactoryUtil.getLog(
		PortletDataContextImpl.class);

	private static ClassLoader _classLoader;
	private static long _modifiedCount;
	private static transient XStream _xStream;

	private final Map<String, long[]> _assetCategoryIdsMap = new HashMap<>();
	private final Set<Long> _assetLinkIds = new HashSet<>();
	private final Map<String, String[]> _assetTagNamesMap = new HashMap<>();
	private long _companyGroupId;
	private long _companyId;
	private String _dataStrategy;
	private final Set<StagedModelType> _deletionSystemEventStagedModelTypes =
		new HashSet<>();
	private Date _endDate;
	private final Map<String, List<ExpandoColumn>> _expandoColumnsMap =
		new HashMap<>();
	private final Map<String, Map<String, Element>> _exportDataElementsMap =
		new HashMap<>();
	private transient Element _exportDataRootElement;
	private String _exportImportProcessId;
	private long _groupId;
	private Map<String, Map<String, List<Element>>> _importDataElementsMap;
	private Map<String, Element> _importDataGroupElements;
	private transient Element _importDataRootElement;
	private transient long[] _layoutIds;
	private String _layoutSetPrototypeUuid;
	private final transient LockManager _lockManager;
	private final transient Map<String, Lock> _locksMap = new HashMap<>();
	private transient ManifestSummary _manifestSummary = new ManifestSummary();
	private final transient Set<String> _missingReferences = new HashSet<>();
	private transient Element _missingReferencesElement;
	private transient List<Layout> _newLayouts;
	private final Map<String, Map<?, ?>> _newPrimaryKeysMaps = new HashMap<>();
	private final Set<String> _notUniquePerLayout = new HashSet<>();
	private final Map<String, Object> _objectsMap = new HashMap<>();
	private boolean _originalPrivateLayout;
	private Map<String, String[]> _parameterMap;
	private final Map<String, List<KeyValuePair>> _permissionsMap =
		new HashMap<>();
	private long _plid;
	private String _portletId;
	private final Set<String> _primaryKeys = new HashSet<>();
	private boolean _privateLayout;
	private final Set<String> _references = new HashSet<>();
	private String _rootPortletId;
	private final Set<String> _scopedPrimaryKeys = new HashSet<>();
	private long _scopeGroupId;
	private String _scopeLayoutUuid;
	private String _scopeType;
	private long _sourceCompanyGroupId;
	private long _sourceCompanyId;
	private long _sourceGroupId;
	private long _sourceUserPersonalSiteGroupId;
	private Date _startDate;
	private String _type;
	private transient UserIdStrategy _userIdStrategy;
	private long _userPersonalSiteGroupId;
	private boolean _validateExistingDataHandler;
	private transient ZipReader _zipReader;
	private transient ZipWriter _zipWriter;

}