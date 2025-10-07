/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Accessor;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class Entity implements Comparable<Entity> {

	public static final Accessor<Entity, String> NAME_ACCESSOR =
		new Accessor<Entity, String>() {

			@Override
			public String get(Entity entity) {
				return entity.getName();
			}

			@Override
			public Class<String> getAttributeClass() {
				return String.class;
			}

			@Override
			public Class<Entity> getTypeClass() {
				return Entity.class;
			}

		};

	public static EntityColumn getEntityColumn(
		String name, List<EntityColumn> entityColumns) {

		for (EntityColumn entityColumn : entityColumns) {
			if (name.equals(entityColumn.getName())) {
				return entityColumn;
			}
		}

		throw new RuntimeException("Entity column " + name + " not found");
	}

	public static boolean hasEntityColumn(
		ServiceBuilder serviceBuilder, String name,
		List<EntityColumn> entityColumns) {

		return hasEntityColumn(serviceBuilder, name, null, entityColumns);
	}

	public static boolean hasEntityColumn(
		ServiceBuilder serviceBuilder, String name, String type,
		List<EntityColumn> entityColumns) {

		int index = entityColumns.indexOf(
			new EntityColumn(serviceBuilder, name));

		if (index != -1) {
			EntityColumn entityColumn = entityColumns.get(index);

			if ((type == null) || type.equals(entityColumn.getType())) {
				return true;
			}
		}

		return false;
	}

	public Entity(ServiceBuilder serviceBuilder, String name) {
		this(
			serviceBuilder, null, null, null, name, null, null, null, null,
			null, null, false, false, null, false, true, true, null, null, null,
			null, null, true, false, false, false, false, false, null, false,
			null, null, false, null, null, null, null, null, null, null, null,
			null, null, null, false);
	}

	public Entity(
		ServiceBuilder serviceBuilder, String packagePath,
		String apiPackagePath, String portletShortName, String name,
		String variableName, String pluralName, String pluralVariableName,
		String humanName, String table, String alias, boolean uuid,
		boolean uuidAccessor, String externalReferenceCode,
		boolean localService, boolean remoteService, boolean persistence,
		String persistenceClassName, String finderClassName, String dataSource,
		String sessionFactory, String txManager, boolean cacheEnabled,
		boolean changeTrackingEnabled, boolean dynamicUpdateEnabled,
		boolean jsonEnabled, boolean mvccEnabled, boolean trashEnabled,
		String uadApplicationName, boolean uadAutoDelete, String uadOutputPath,
		String uadPackagePath, boolean deprecated,
		List<EntityColumn> pkEntityColumns,
		List<EntityColumn> regularEntityColumns,
		List<EntityColumn> blobEntityColumns,
		List<EntityColumn> collectionEntityColumns,
		List<EntityColumn> entityColumns, EntityOrder entityOrder,
		List<EntityFinder> entityFinders,
		List<EntityFinder> indexOnlyEntityFinders,
		List<Entity> referenceEntities,
		List<String> unresolvedReferenceEntityNames,
		List<String> txRequiredMethodNames, boolean resourceActionModel) {

		_serviceBuilder = serviceBuilder;
		_packagePath = packagePath;
		_apiPackagePath = apiPackagePath;
		_portletShortName = portletShortName;
		_name = name;
		_variableName = GetterUtil.getString(
			variableName, TextFormatter.format(name, TextFormatter.I));

		_pluralName = GetterUtil.getString(
			pluralName, serviceBuilder.formatPlural(name));

		if (Validator.isNotNull(pluralVariableName)) {
			_pluralVariableName = pluralVariableName;
		}
		else if (Validator.isNotNull(pluralName)) {
			_pluralVariableName = TextFormatter.format(
				pluralName, TextFormatter.I);
		}
		else {
			_pluralVariableName = serviceBuilder.formatPlural(_variableName);
		}

		_humanName = GetterUtil.getString(
			humanName, ServiceBuilder.toHumanName(name));
		_table = table;
		_alias = alias;
		_uuid = uuid;
		_uuidAccessor = uuidAccessor;
		_externalReferenceCode = externalReferenceCode;
		_localService = localService;
		_remoteService = remoteService;
		_persistence = persistence;
		_persistenceClassName = persistenceClassName;
		_finderClassName = finderClassName;
		_changeTrackingEnabled = changeTrackingEnabled;
		_dynamicUpdateEnabled = dynamicUpdateEnabled;
		_jsonEnabled = jsonEnabled;
		_mvccEnabled = mvccEnabled;
		_trashEnabled = trashEnabled;
		_uadApplicationName = uadApplicationName;
		_uadAutoDelete = uadAutoDelete;
		_uadOutputPath = uadOutputPath;
		_uadPackagePath = uadPackagePath;
		_deprecated = deprecated;
		_pkEntityColumns = pkEntityColumns;
		_regularEntityColumns = regularEntityColumns;
		_blobEntityColumns = blobEntityColumns;
		_collectionEntityColumns = collectionEntityColumns;
		_entityColumns = entityColumns;
		_entityOrder = entityOrder;
		_entityFinders = entityFinders;
		_indexOnlyEntityFinders = indexOnlyEntityFinders;
		_referenceEntities = referenceEntities;
		_unresolvedReferenceEntityNames = unresolvedReferenceEntityNames;
		_txRequiredMethodNames = txRequiredMethodNames;
		_resourceActionModel = resourceActionModel;

		_dataSource = GetterUtil.getString(dataSource, _DATA_SOURCE_DEFAULT);
		_sessionFactory = GetterUtil.getString(
			sessionFactory, _SESSION_FACTORY_DEFAULT);
		_txManager = GetterUtil.getString(txManager, _TX_MANAGER_DEFAULT);

		if (entityColumns == null) {
			_databaseRegularEntityColumns = null;
		}
		else {
			_databaseRegularEntityColumns = new ArrayList<>(
				regularEntityColumns);
		}

		if (entityFinders != null) {
			Set<EntityColumn> finderEntityColumns = new HashSet<>();

			for (EntityFinder entityFinder : _entityFinders) {
				finderEntityColumns.addAll(entityFinder.getEntityColumns());
			}

			_finderEntityColumns = new ArrayList<>(finderEntityColumns);

			Collections.sort(_finderEntityColumns);
		}
		else {
			_finderEntityColumns = Collections.emptyList();
		}

		if ((blobEntityColumns != null) && !blobEntityColumns.isEmpty()) {
			for (EntityColumn entityColumn : _blobEntityColumns) {
				if (!entityColumn.isLazy()) {
					cacheEnabled = false;

					break;
				}
			}
		}

		_cacheEnabled = cacheEnabled;

		boolean containerModel = false;

		if ((entityColumns != null) && !entityColumns.isEmpty()) {
			for (EntityColumn entityColumn : _entityColumns) {
				if (entityColumn.isContainerModel() ||
					entityColumn.isParentContainerModel()) {

					containerModel = true;

					break;
				}
			}
		}

		_containerModel = containerModel;
	}

	public void addReferenceEntity(Entity referenceEntity) {
		_referenceEntities.add(referenceEntity);
	}

	@Override
	public int compareTo(Entity entity) {
		return _name.compareToIgnoreCase(entity._name);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Entity)) {
			return false;
		}

		Entity entity = (Entity)object;

		return _name.equals(entity.getName());
	}

	public String getAlias() {
		return _alias;
	}

	public String getApiPackagePath() {
		return _apiPackagePath;
	}

	public List<EntityColumn> getBadEntityColumns() {
		List<EntityColumn> entityColumns = ListUtil.copy(_entityColumns);

		Iterator<EntityColumn> iterator = entityColumns.iterator();

		while (iterator.hasNext()) {
			EntityColumn entityColumn = iterator.next();

			String name = entityColumn.getName();

			if (name.equals(entityColumn.getDBName())) {
				iterator.remove();
			}
		}

		return entityColumns;
	}

	public List<EntityColumn> getBlobEntityColumns() {
		return _blobEntityColumns;
	}

	public List<EntityColumn> getCollectionEntityColumns() {
		return _collectionEntityColumns;
	}

	public List<EntityFinder> getCollectionEntityFinders() {
		List<EntityFinder> entityFinders = new ArrayList<>(
			_entityFinders.size());

		for (EntityFinder entityFinder : _entityFinders) {
			if (entityFinder.isCollection() &&
				!entityFinder.hasCustomComparator()) {

				entityFinders.add(entityFinder);
			}
		}

		return entityFinders;
	}

	public String getConstantName() {
		return TextFormatter.format(
			TextFormatter.format(_name, TextFormatter.H), TextFormatter.A);
	}

	public Set<String> getCTColumnResolutionTypeNames() {
		Set<String> ctColumnResolutionTypeNames = new TreeSet<>();

		for (EntityColumn entityColumn : getEntityColumns()) {
			ctColumnResolutionTypeNames.add(
				entityColumn.getCTColumnResolutionTypeName());
		}

		return ctColumnResolutionTypeNames;
	}

	public List<EntityColumn> getDatabaseRegularEntityColumns() {
		return _databaseRegularEntityColumns;
	}

	public String getDataSource() {
		return _dataSource;
	}

	public EntityColumn getEntityColumn(String name) {
		return getEntityColumn(name, _entityColumns);
	}

	public List<EntityColumn> getEntityColumns() {
		return _entityColumns;
	}

	public List<EntityFinder> getEntityFinders() {
		return _entityFinders;
	}

	public EntityOrder getEntityOrder() {
		return _entityOrder;
	}

	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	public EntityColumn getFilterPKEntityColumn() {
		for (EntityColumn entityColumn : _entityColumns) {
			if (entityColumn.isFilterPrimary()) {
				return entityColumn;
			}
		}

		return _getPKEntityColumn();
	}

	public String getFinderClassName() {
		return _finderClassName;
	}

	public List<EntityColumn> getFinderEntityColumns() {
		return _finderEntityColumns;
	}

	public String getHumanName() {
		return _humanName;
	}

	public List<EntityFinder> getIndexOnlyEntityFinders() {
		return _indexOnlyEntityFinders;
	}

	public Entity getLocalizedEntity() {
		return _localizedEntity;
	}

	public List<EntityColumn> getLocalizedEntityColumns() {
		return _localizedEntityColumns;
	}

	public String getModelBaseInterfaceNames() {
		List<String> interfaceNames = new ArrayList<>();

		if (isAttachedModel()) {
			interfaceNames.add("AttachedModel");
		}
		else if (isTypedModel()) {
			interfaceNames.add("TypedModel");
		}

		interfaceNames.add("BaseModel<" + _name + ">");

		if (isChangeTrackingEnabled()) {
			interfaceNames.add("CTModel<" + _name + ">");
		}

		if (isContainerModel()) {
			interfaceNames.add("ContainerModel");
		}

		if (isExternalReferenceCodeModel()) {
			interfaceNames.add("ExternalReferenceCodeModel");
		}

		if (isLocalizedModel()) {
			interfaceNames.add("LocalizedModel");
		}

		if (isMvccEnabled()) {
			interfaceNames.add("MVCCModel");
		}

		if (isResourcedModel()) {
			interfaceNames.add("ResourcedModel");
		}

		if (isShardedModel()) {
			interfaceNames.add("ShardedModel");
		}

		if (isStagedGroupedModel()) {
			interfaceNames.add("StagedGroupedModel");
		}
		else {
			if (isGroupedModel()) {
				interfaceNames.add("GroupedModel");
			}

			if (isStagedAuditedModel()) {
				interfaceNames.add("StagedAuditedModel");
			}
			else {
				if (isStagedModel()) {
					interfaceNames.add("StagedModel");
				}

				if (isAuditedModel() && !isGroupedModel()) {
					interfaceNames.add("AuditedModel");
				}
			}
		}

		if (isTrashEnabled()) {
			interfaceNames.add("TrashedModel");
		}

		if (_versionEntity != null) {
			interfaceNames.add("VersionedModel<" + _name + "Version>");
		}
		else if (_versionedEntity != null) {
			interfaceNames.add("VersionModel<" + _versionedEntity._name + ">");
		}

		if (isWorkflowEnabled()) {
			interfaceNames.add("WorkflowedModel");
		}

		interfaceNames.sort(null);

		StringBundler sb = new StringBundler(2 * interfaceNames.size());

		for (String interfaceName : interfaceNames) {
			sb.append(interfaceName);
			sb.append(", ");
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public String getName() {
		return _name;
	}

	public Set<String> getOverrideColumnNames() {
		Set<String> overrideColumnName = new HashSet<>();

		if (isAttachedModel()) {
			overrideColumnName.add("classPK");
		}

		if (isAuditedModel()) {
			overrideColumnName.add("companyId");
			overrideColumnName.add("createDate");
			overrideColumnName.add("modifiedDate");
			overrideColumnName.add("userId");
			overrideColumnName.add("userName");
			overrideColumnName.add("userUuid");
		}

		if (isChangeTrackingEnabled()) {
			overrideColumnName.add("ctCollectionId");
			overrideColumnName.add("primaryKey");
		}

		if (isExternalReferenceCodeModel()) {
			overrideColumnName.add("externalReferenceCode");
		}

		if (isGroupedModel()) {
			overrideColumnName.add("groupId");
		}

		if (isMvccEnabled()) {
			overrideColumnName.add("mvccVersion");
		}

		if (isShardedModel()) {
			overrideColumnName.add("companyId");
		}

		if (isStagedGroupedModel()) {
			overrideColumnName.add("lastPublishDate");
		}

		if (isStagedModel()) {
			overrideColumnName.add("companyId");
			overrideColumnName.add("createDate");
			overrideColumnName.add("modifiedDate");
			overrideColumnName.add("stagedModelType");
			overrideColumnName.add("uuid");
		}

		if (isResourcedModel()) {
			overrideColumnName.add("resourcePrimKey");
		}

		if (isTrashEnabled()) {
			overrideColumnName.add("status");
		}

		if (isTypedModel()) {
			overrideColumnName.add("className");
			overrideColumnName.add("classNameId");
		}

		if (_versionEntity != null) {
			overrideColumnName.add("headId");
			overrideColumnName.add("primaryKey");
		}
		else if (_versionedEntity != null) {
			overrideColumnName.add("primaryKey");
			overrideColumnName.add("version");
		}

		if (isWorkflowEnabled()) {
			overrideColumnName.add("status");
			overrideColumnName.add("statusByUserId");
			overrideColumnName.add("statusByUserName");
			overrideColumnName.add("statusByUserUuid");
			overrideColumnName.add("statusDate");
		}

		return overrideColumnName;
	}

	public String getPackagePath() {
		return _packagePath;
	}

	public List<String> getParentTransients() {
		return _parentTransients;
	}

	public String getPersistenceClassName() {
		return _persistenceClassName;
	}

	public String getPKClassName() {
		if (hasCompoundPK()) {
			return _name + "PK";
		}

		EntityColumn entityColumn = _getPKEntityColumn();

		return entityColumn.getType();
	}

	public String getPKDBName() {
		if (hasCompoundPK()) {
			return getVariableName() + "PK";
		}

		EntityColumn entityColumn = _getPKEntityColumn();

		return entityColumn.getDBName();
	}

	public List<String> getPKEntityColumnDBNames() {
		List<String> pkEntityColumnDBNames = new ArrayList<>(
			_pkEntityColumns.size());

		for (EntityColumn entityColumn : _pkEntityColumns) {
			pkEntityColumnDBNames.add(entityColumn.getDBName());
		}

		return pkEntityColumnDBNames;
	}

	public List<EntityColumn> getPKEntityColumns() {
		return _pkEntityColumns;
	}

	public String getPKMethodName() {
		EntityColumn entityColumn = _getPKEntityColumn();

		return entityColumn.getMethodName();
	}

	public String getPKVariableName() {
		if (hasCompoundPK()) {
			return getVariableName() + "PK";
		}

		EntityColumn entityColumn = _getPKEntityColumn();

		return entityColumn.getName();
	}

	public String getPluralHumanName() {
		return _serviceBuilder.formatPlural(_humanName);
	}

	public String getPluralName() {
		return _pluralName;
	}

	public String getPluralPKVariableName() {
		if (hasCompoundPK()) {
			return getVariableName() + "PKs";
		}

		EntityColumn entityColumn = _getPKEntityColumn();

		return entityColumn.getPluralName();
	}

	public String getPluralVariableName() {
		return _pluralVariableName;
	}

	public String getPortletShortName() {
		return _portletShortName;
	}

	public List<Entity> getReferenceEntities() {
		return _referenceEntities;
	}

	public List<EntityColumn> getRegularEntityColumns() {
		return _regularEntityColumns;
	}

	public String getSessionFactory() {
		return _sessionFactory;
	}

	public String getShortName() {
		if (_name.startsWith(_portletShortName)) {
			return _name.substring(_portletShortName.length());
		}

		return _name;
	}

	public String getSpringPropertyName() {
		return TextFormatter.format(_name, TextFormatter.L);
	}

	public String getTable() {
		return _table;
	}

	public List<String> getTransients() {
		return _transients;
	}

	public String getTXManager() {
		return _txManager;
	}

	public List<String> getTxRequiredMethodNames() {
		return _txRequiredMethodNames;
	}

	public Map<String, List<EntityColumn>>
		getUADAnonymizableEntityColumnsMap() {

		Map<String, List<EntityColumn>> uadAnonymizableEntityColumnsMap =
			new HashMap<>();

		for (EntityColumn entityColumn : _entityColumns) {
			if (entityColumn.isUADUserId()) {
				uadAnonymizableEntityColumnsMap.put(
					entityColumn.getName(), ListUtil.fromArray(entityColumn));
			}
		}

		for (EntityColumn entityColumn : _entityColumns) {
			if (entityColumn.isUADUserName()) {
				List<EntityColumn> uadAnonymizableEntityColumns =
					uadAnonymizableEntityColumnsMap.get(
						entityColumn.getUADUserIdColumnName());

				if (uadAnonymizableEntityColumns != null) {
					uadAnonymizableEntityColumns.add(entityColumn);
				}
			}
		}

		return uadAnonymizableEntityColumnsMap;
	}

	public String getUADApplicationName() {
		return _uadApplicationName;
	}

	public boolean getUADAutoDelete() {
		return _uadAutoDelete;
	}

	public List<EntityColumn> getUADEntityColumns() {
		List<EntityColumn> uadEntityColumns = new ArrayList<>();

		uadEntityColumns.add(_getPKEntityColumn());

		Map<String, List<EntityColumn>> uadAnonymizableEntityColumnsMap =
			getUADAnonymizableEntityColumnsMap();

		for (Map.Entry<String, List<EntityColumn>> entry :
				uadAnonymizableEntityColumnsMap.entrySet()) {

			uadEntityColumns.addAll(entry.getValue());
		}

		uadEntityColumns.addAll(getUADNonanonymizableEntityColumns());

		return uadEntityColumns;
	}

	public List<EntityColumn> getUADNonanonymizableEntityColumns() {
		return TransformUtil.transform(
			_entityColumns,
			entityColumn -> {
				if (entityColumn.isUADNonanonymizable()) {
					return entityColumn;
				}

				return null;
			});
	}

	public String getUADOutputPath() {
		return _uadOutputPath;
	}

	public String getUADPackagePath() {
		return _uadPackagePath;
	}

	public String getUADTestIntegrationOutputPath() {
		return StringUtil.replace(
			getUADOutputPath(), new String[] {"-uad/", "/main/"},
			new String[] {"-uad-test/", "/testIntegration/"});
	}

	public List<String> getUADUserIdColumnNames() {
		return TransformUtil.transform(
			_entityColumns,
			entityColumn -> {
				if (entityColumn.isUADUserId()) {
					return entityColumn.getName();
				}

				return null;
			});
	}

	public List<EntityFinder> getUniqueEntityFinders() {
		List<EntityFinder> entityFinders = ListUtil.copy(_entityFinders);

		Iterator<EntityFinder> iterator = entityFinders.iterator();

		while (iterator.hasNext()) {
			EntityFinder entityFinder = iterator.next();

			if (entityFinder.isCollection() && !entityFinder.isUnique()) {
				iterator.remove();
			}
		}

		return entityFinders;
	}

	public List<String> getUnresolvedResolvedReferenceEntityNames() {
		if (_unresolvedReferenceEntityNames == null) {
			return new ArrayList<>();
		}

		return _unresolvedReferenceEntityNames;
	}

	public String getVariableName() {
		return _variableName;
	}

	public Entity getVersionedEntity() {
		return _versionedEntity;
	}

	public Entity getVersionEntity() {
		return _versionEntity;
	}

	public boolean hasActionableDynamicQuery() {
		if (hasEntityColumns() && hasLocalService()) {
			if (hasCompoundPK()) {
				EntityColumn entityColumn = _pkEntityColumns.get(0);

				return entityColumn.isPrimitiveType();
			}

			return hasPrimitivePK();
		}

		return false;
	}

	public boolean hasArrayableOperator() {
		for (EntityFinder finder : _entityFinders) {
			if (finder.hasArrayableOperator()) {
				return true;
			}
		}

		return false;
	}

	public boolean hasCompoundPK() {
		if (_pkEntityColumns.size() > 1) {
			return true;
		}

		return false;
	}

	public boolean hasEagerBlobColumn() {
		if (ListUtil.isEmpty(_blobEntityColumns)) {
			return false;
		}

		for (EntityColumn blobEntityColumns : _blobEntityColumns) {
			if (!blobEntityColumns.isLazy()) {
				return true;
			}
		}

		return false;
	}

	public boolean hasEntityColumn(String name) {
		return hasEntityColumn(_serviceBuilder, name, _entityColumns);
	}

	public boolean hasEntityColumn(String name, String type) {
		return hasEntityColumn(_serviceBuilder, name, type, _entityColumns);
	}

	public boolean hasEntityColumns() {
		return ListUtil.isNotEmpty(_entityColumns);
	}

	public boolean hasExternalReferenceCode() {
		return !StringUtil.equals(_externalReferenceCode, "none");
	}

	public boolean hasFinderClassName() {
		return Validator.isNotNull(_finderClassName);
	}

	@Override
	public int hashCode() {
		return _name.hashCode();
	}

	public boolean hasLazyBlobEntityColumn() {
		if (ListUtil.isEmpty(_blobEntityColumns)) {
			return false;
		}

		for (EntityColumn blobEntityColumns : _blobEntityColumns) {
			if (blobEntityColumns.isLazy()) {
				return true;
			}
		}

		return false;
	}

	public boolean hasLocalService() {
		return _localService;
	}

	public boolean hasPersistence() {
		return _persistence;
	}

	public boolean hasPrimitivePK() {
		return hasPrimitivePK(true);
	}

	public boolean hasPrimitivePK(boolean includeWrappers) {
		if (_pkEntityColumns.size() != 1) {
			return false;
		}

		EntityColumn entityColumn = _getPKEntityColumn();

		return entityColumn.isPrimitiveType(includeWrappers);
	}

	public boolean hasRemoteService() {
		return _remoteService;
	}

	public boolean hasUuid() {
		return _uuid;
	}

	public boolean hasUuidAccessor() {
		return _uuidAccessor;
	}

	public boolean isAttachedModel() {
		if (!isTypedModel()) {
			return false;
		}

		if (hasEntityColumn("classPK")) {
			EntityColumn classPKEntityColumn = getEntityColumn("classPK");

			String classPKColType = classPKEntityColumn.getType();

			if (classPKColType.equals("long")) {
				return true;
			}
		}

		return false;
	}

	public boolean isAuditedModel() {
		if (hasEntityColumn("companyId") &&
			hasEntityColumn("createDate", "Date") &&
			hasEntityColumn("modifiedDate", "Date") &&
			hasEntityColumn("userId") && hasEntityColumn("userName")) {

			return true;
		}

		return false;
	}

	public boolean isCacheEnabled() {
		return _cacheEnabled;
	}

	public boolean isChangeTrackingEnabled() {
		return _changeTrackingEnabled;
	}

	public boolean isContainerModel() {
		return _containerModel;
	}

	public boolean isDefaultDataSource() {
		return _dataSource.equals(_DATA_SOURCE_DEFAULT);
	}

	public boolean isDefaultSessionFactory() {
		return _sessionFactory.equals(_SESSION_FACTORY_DEFAULT);
	}

	public boolean isDefaultTXManager() {
		return _txManager.equals(_TX_MANAGER_DEFAULT);
	}

	public boolean isDeprecated() {
		return _deprecated;
	}

	public boolean isDynamicUpdateEnabled() {
		return _dynamicUpdateEnabled;
	}

	public boolean isExternalReferenceCodeModel() {
		if (_serviceBuilder.isVersionGTE_7_4_0() &&
			hasEntityColumn("externalReferenceCode")) {

			return true;
		}

		return false;
	}

	public boolean isGroupedModel() {
		String pkVariableName = getPKVariableName();

		if (isAuditedModel() && hasEntityColumn("groupId") &&
			!pkVariableName.equals("groupId")) {

			return true;
		}

		return false;
	}

	public boolean isHierarchicalTree() {
		if (!hasPrimitivePK()) {
			return false;
		}

		EntityColumn entityColumn = _getPKEntityColumn();

		String methodName = entityColumn.getMethodName();

		if (_entityColumns.contains(
				new EntityColumn(_serviceBuilder, "parent" + methodName)) &&
			_entityColumns.contains(
				new EntityColumn(_serviceBuilder, "left" + methodName)) &&
			_entityColumns.contains(
				new EntityColumn(_serviceBuilder, "right" + methodName))) {

			return true;
		}

		return false;
	}

	public boolean isJsonEnabled() {
		return _jsonEnabled;
	}

	public boolean isLocalizedModel() {
		for (EntityColumn entityColumn : _entityColumns) {
			if (entityColumn.isLocalized()) {
				return true;
			}
		}

		return false;
	}

	public boolean isMvccEnabled() {
		return _mvccEnabled;
	}

	public boolean isOrdered() {
		if (_entityOrder != null) {
			return true;
		}

		return false;
	}

	public boolean isPermissionCheckEnabled() {
		for (EntityFinder entityFinder : _entityFinders) {
			if (isPermissionCheckEnabled(entityFinder)) {
				return true;
			}
		}

		return false;
	}

	public boolean isPermissionCheckEnabled(EntityFinder entityFinder) {
		boolean resourceActionModel = _resourceActionModel;

		if (_serviceBuilder.isVersionLTE_7_1_0()) {

			// See LPS-82433. Add this hack to prevent
			// 4d29a89578e0a712ddcb6793d93c8fc9128c3b03 in 7.1.x from requring
			// a major breaking change in portal-kernel.

			if (_packagePath.equals("com.liferay.portlet.asset") &&
				_name.equals("AssetTag")) {

				resourceActionModel = true;
			}
		}

		String entityFinderName = entityFinder.getName();

		if (_name.equals("Group") || _name.equals("User") ||
			entityFinderName.equals("UUID_G") || !entityFinder.isCollection() ||
			!hasPrimitivePK() || !resourceActionModel) {

			return false;
		}

		if (hasEntityColumn("groupId") &&
			!entityFinder.hasEntityColumn("groupId")) {

			return false;
		}

		EntityColumn entityColumn = _getPKEntityColumn();

		return StringUtil.equalsIgnoreCase("long", entityColumn.getType());
	}

	public boolean isPermissionedModel() {
		if (_serviceBuilder.isVersionLTE_7_2_0() &&
			hasEntityColumn("resourceBlockId")) {

			return true;
		}

		return false;
	}

	public boolean isPortalReference() {
		return _portalReference;
	}

	public boolean isResolved() {
		if ((_unresolvedReferenceEntityNames != null) &&
			_unresolvedReferenceEntityNames.isEmpty()) {

			return true;
		}

		return false;
	}

	public boolean isResourcedModel() {
		String pkVariableName = getPKVariableName();

		if (hasEntityColumn("resourcePrimKey") &&
			!pkVariableName.equals("resourcePrimKey")) {

			return true;
		}

		return false;
	}

	public boolean isShardedModel() {
		if (_packagePath.equals("com.liferay.portal") &&
			(_name.equals("Company") ||
			 (_serviceBuilder.isVersionGTE_7_4_0() &&
			  _name.equals("VirtualHost")))) {

			return false;
		}

		return hasEntityColumn("companyId");
	}

	public boolean isStagedAuditedModel() {
		if (isAuditedModel() && isStagedModel()) {
			return true;
		}

		return false;
	}

	public boolean isStagedGroupedModel() {
		if (isGroupedModel() && isStagedModel() &&
			hasEntityColumn("lastPublishDate", "Date")) {

			return true;
		}

		return false;
	}

	public boolean isStagedModel() {
		if (hasUuid() && hasEntityColumn("companyId") &&
			hasEntityColumn("createDate", "Date") &&
			hasEntityColumn("modifiedDate", "Date")) {

			return true;
		}

		return false;
	}

	public boolean isTrashEnabled() {
		return _trashEnabled;
	}

	public boolean isTreeModel() {
		return hasEntityColumn("treePath");
	}

	public boolean isTypedModel() {
		if (hasEntityColumn("classNameId")) {
			EntityColumn classNameIdEntityColumn = getEntityColumn(
				"classNameId");

			String classNameIdColType = classNameIdEntityColumn.getType();

			if (classNameIdColType.equals("long")) {
				return true;
			}
		}

		return false;
	}

	public boolean isUADEnabled() {
		for (EntityColumn entityColumn : _entityColumns) {
			if (entityColumn.isUADEnabled()) {
				return true;
			}
		}

		return false;
	}

	public boolean isWorkflowEnabled() {
		if (hasEntityColumn("status") && hasEntityColumn("statusByUserId") &&
			hasEntityColumn("statusByUserName") &&
			hasEntityColumn("statusDate")) {

			return true;
		}

		return false;
	}

	public void setApiPackagePath(String apiPackagePath) {
		_apiPackagePath = apiPackagePath;
	}

	public void setLocalizedEntity(Entity localizedEntity) {
		_localizedEntity = localizedEntity;

		_referenceEntities.add(localizedEntity);
	}

	public void setLocalizedEntityColumns(
		List<EntityColumn> localizedEntityColumns) {

		_localizedEntityColumns = localizedEntityColumns;
	}

	public void setParentTransients(List<String> transients) {
		_parentTransients = transients;
	}

	public void setPortalReference(boolean portalReference) {
		_portalReference = portalReference;
	}

	public void setResolved() {
		_unresolvedReferenceEntityNames = null;
	}

	public void setTransients(List<String> transients) {
		_transients = transients;
	}

	public void setVersionedEntity(Entity versionedEntity) {
		_versionedEntity = versionedEntity;
	}

	public void setVersionEntity(Entity versionEntity) {
		_versionEntity = versionEntity;

		_referenceEntities.add(versionEntity);
	}

	private EntityColumn _getPKEntityColumn() {
		if (_pkEntityColumns.isEmpty()) {
			throw new RuntimeException(
				"There is no primary key for entity " + _name);
		}

		return _pkEntityColumns.get(0);
	}

	private static final String _DATA_SOURCE_DEFAULT = "liferayDataSource";

	private static final String _SESSION_FACTORY_DEFAULT =
		"liferaySessionFactory";

	private static final String _TX_MANAGER_DEFAULT =
		"liferayTransactionManager";

	private final String _alias;
	private String _apiPackagePath;
	private List<EntityColumn> _blobEntityColumns;
	private final boolean _cacheEnabled;
	private boolean _changeTrackingEnabled;
	private final List<EntityColumn> _collectionEntityColumns;
	private final boolean _containerModel;
	private final List<EntityColumn> _databaseRegularEntityColumns;
	private final String _dataSource;
	private final boolean _deprecated;
	private final boolean _dynamicUpdateEnabled;
	private final List<EntityColumn> _entityColumns;
	private final List<EntityFinder> _entityFinders;
	private final EntityOrder _entityOrder;
	private final String _externalReferenceCode;
	private final String _finderClassName;
	private final List<EntityColumn> _finderEntityColumns;
	private final String _humanName;
	private final List<EntityFinder> _indexOnlyEntityFinders;
	private final boolean _jsonEnabled;
	private Entity _localizedEntity;
	private List<EntityColumn> _localizedEntityColumns;
	private final boolean _localService;
	private final boolean _mvccEnabled;
	private final String _name;
	private final String _packagePath;
	private List<String> _parentTransients;
	private final boolean _persistence;
	private final String _persistenceClassName;
	private final List<EntityColumn> _pkEntityColumns;
	private final String _pluralName;
	private final String _pluralVariableName;
	private boolean _portalReference;
	private final String _portletShortName;
	private final List<Entity> _referenceEntities;
	private final List<EntityColumn> _regularEntityColumns;
	private final boolean _remoteService;
	private final boolean _resourceActionModel;
	private ServiceBuilder _serviceBuilder;
	private final String _sessionFactory;
	private final String _table;
	private List<String> _transients;
	private final boolean _trashEnabled;
	private final String _txManager;
	private final List<String> _txRequiredMethodNames;
	private final String _uadApplicationName;
	private final boolean _uadAutoDelete;
	private final String _uadOutputPath;
	private final String _uadPackagePath;
	private List<String> _unresolvedReferenceEntityNames;
	private final boolean _uuid;
	private final boolean _uuidAccessor;
	private final String _variableName;
	private Entity _versionedEntity;
	private Entity _versionEntity;

}