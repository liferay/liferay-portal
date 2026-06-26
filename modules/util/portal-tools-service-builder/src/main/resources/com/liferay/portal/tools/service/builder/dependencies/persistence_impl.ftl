<#if entity.isHierarchicalTree()>
	<#if entity.hasEntityColumn("groupId")>
		<#assign scopeEntityColumn = entity.getEntityColumn("groupId") />
	<#else>
		<#assign scopeEntityColumn = entity.getEntityColumn("companyId") />
	</#if>

	<#assign pkEntityColumn = entity.PKEntityColumns?first />
</#if>

<#if osgiModule>
	<#assign ctPersistenceHelper = "ctPersistenceHelper" />
<#else>
	<#assign ctPersistenceHelper = "CTPersistenceHelperUtil" />
</#if>

<#if serviceBuilder.isVersionGTE_7_3_0() && !entity.isCacheEnabled()>
	<#assign
		entityCache = "dummyEntityCache"
		finderCache = "dummyFinderCache"
		finderCacheInstance = "dummyFinderCache"
	/>
<#elseif osgiModule>
	<#assign
		entityCache = "entityCache"
		finderCache = "finderCache"
		finderCacheInstance = "finderCache"
	/>
<#else>
	<#assign
		entityCache = "EntityCacheUtil"
		finderCache = "FinderCacheUtil"
		finderCacheInstance = "FinderCacheUtil.getFinderCache()"
	/>
</#if>

<#assign
	finderFieldSQLSuffix = "_SQL"
/>

package ${packagePath}.service.persistence.impl;

import ${serviceBuilder.getCompatJavaClassName("StringBundler")};

<#assign
	duplicateEntityExternalReferenceCode = serviceBuilder.getDuplicateEntityExternalReferenceCodeException(entity)
	noSuchEntity = serviceBuilder.getNoSuchEntityException(entity)
/>

import ${apiPackagePath}.exception.${duplicateEntityExternalReferenceCode}Exception;
import ${apiPackagePath}.exception.${noSuchEntity}Exception;
import ${apiPackagePath}.model.${entity.name};

<#if serviceBuilder.isDSLEnabled()>
	import ${apiPackagePath}.model.${entity.name}Table;
</#if>

import ${packagePath}.model.impl.${entity.name}Impl;
import ${packagePath}.model.impl.${entity.name}ModelImpl;
import ${apiPackagePath}.service.persistence.${entity.name}Persistence;
import ${apiPackagePath}.service.persistence.${entity.name}Util;

<#if entity.hasCompoundPK()>
	import ${apiPackagePath}.service.persistence.${entity.PKClassName};
</#if>

<#if dependencyInjectorDS>
	import ${packagePath}.service.persistence.impl.constants.${portletShortName}PersistenceConstants;
</#if>

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;

<#if !serviceBuilder.isVersionGTE_7_1_0()>
	import com.liferay.portal.kernel.configuration.Filter;
	import com.liferay.portal.kernel.dao.db.DBManagerUtil;
	import com.liferay.portal.kernel.dao.db.DBType;

	import java.lang.reflect.Array;
</#if>

import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;

<#if serviceBuilder.isVersionGTE_7_4_0()>
	import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
	import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
	<#if entity.isPermissionCheckEnabled()>
		import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
	</#if>

	import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
</#if>

import com.liferay.portal.kernel.service.persistence.impl.NestedSetsTreeManager;
import com.liferay.portal.kernel.service.persistence.impl.PersistenceNestedSetsTreeManager;
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;

<#if serviceBuilder.isVersionGTE_7_4_0()>
	import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
</#if>

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;

<#if osgiModule>
	import org.osgi.framework.ServiceRegistration;
<#else>
	import com.liferay.registry.ServiceRegistration;
</#if>

import java.io.Serializable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;

import java.math.BigDecimal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

<#list entity.entityColumns as entityColumn>
	<#if entityColumn.isCollection() && entityColumn.isMappingManyToMany()>
		<#assign referenceEntity = serviceBuilder.getEntity(entityColumn.entityName) />

		<#if referenceEntity.hasPersistence()>
			<#if dependencyInjectorDS>
				import ${referenceEntity.apiPackagePath}.model.${referenceEntity.name};
			<#else>
				import ${referenceEntity.apiPackagePath}.service.persistence.${referenceEntity.name}Persistence;
			</#if>
		</#if>
	</#if>
</#list>

<#if entity.localizedEntity??>
	<#assign localizedEntity = entity.localizedEntity />

	import ${apiPackagePath}.service.persistence.${localizedEntity.name}Persistence;
</#if>

<#if entity.versionedEntity?? && entity.versionedEntity.localizedEntity??>
	<#assign
		versionedEntity = entity.versionedEntity
		localizedVersionEntity = versionedEntity.localizedEntity.versionEntity
	/>

	import ${apiPackagePath}.service.persistence.${localizedVersionEntity.name}Persistence;
</#if>

/**
 * The persistence implementation for the ${entity.humanName} service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author ${author}
<#if classDeprecated>
 * @deprecated ${classDeprecatedComment}
</#if>
 * @generated
 */

<#assign
	columnBitmaskCacheEnabled = "${entity.name}ModelImpl.COLUMN_BITMASK_ENABLED"
	entityCacheEnabled = "${entity.name}ModelImpl.ENTITY_CACHE_ENABLED"
	finderCacheEnabled = "${entity.name}ModelImpl.FINDER_CACHE_ENABLED"
/>

<#if dependencyInjectorDS>
	@Component(service = ${entity.name}Persistence.class)

	<#assign
		columnBitmaskCacheEnabled = "_columnBitmaskEnabled"
		entityCacheEnabled = "entityCacheEnabled"
		finderCacheEnabled = "finderCacheEnabled"
	/>
</#if>

<#if classDeprecated>
	@Deprecated
</#if>
public class ${entity.name}PersistenceImpl extends BasePersistenceImpl<${entity.name}<#if serviceBuilder.isVersionGTE_7_4_0()>, ${noSuchEntity}Exception</#if>> implements ${entity.name}Persistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>${entity.name}Util</code> to access the ${entity.humanName} persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	public static final String FINDER_CLASS_NAME_ENTITY = ${entity.name}Impl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY + ".List2";

	<#if serviceBuilder.isVersionGTE_7_3_0()>
		<#assign columnBitmaskEnabled = (entity.databaseRegularEntityColumns?size &lt; 64) && !entity.hasEagerBlobColumn() />
	<#else>
		<#assign columnBitmaskEnabled = (entity.finderEntityColumns?size &gt; 0) && (entity.finderEntityColumns?size &lt; 64) && !entity.hasEagerBlobColumn() />
	</#if>

	<#if !serviceBuilder.isVersionGTE_7_1_0()>
		private static Object _split(Object array, int splitSize) {
			int length = Array.getLength(array);

			int pageCount = length / splitSize;

			if ((length % splitSize) > 0) {
				pageCount++;
			}

			Class<?> clazz = array.getClass();

			Class<?> componentType = clazz.getComponentType();

			Object newArray = Array.newInstance(
				componentType, pageCount, splitSize);

			if (pageCount == 1) {
				Array.set(newArray, 0, array);

				return newArray;
			}

			for (int i = 0; i < pageCount; i++) {
				int end = Math.min(length, splitSize * (i + 1));
				int start = splitSize * i;

				int elementLength = end - start;

				Object element = Array.newInstance(componentType, elementLength);

				System.arraycopy(array, start, element, 0, elementLength);

				Array.set(newArray, i, element);
			}

			return newArray;
		}

		private int _databaseInMaxParameters;
	</#if>

	<#if !serviceBuilder.isVersionGTE_7_4_0()>
		private FinderPath _finderPathWithPaginationFindAll;
		private FinderPath _finderPathWithoutPaginationFindAll;
	</#if>

	<#if !serviceBuilder.isVersionGTE_7_4_0()>
		private FinderPath _finderPathCountAll;
	</#if>

	<#if entity.isHierarchicalTree()>
		private FinderPath _finderPathWithPaginationCountAncestors;
		private FinderPath _finderPathWithPaginationCountDescendants;
		private FinderPath _finderPathWithPaginationGetAncestors;
		private FinderPath _finderPathWithPaginationGetDescendants;
	</#if>

	<#list entity.entityFinders as entityFinder>
		<#include "persistence_impl_finder_finder_path.ftl">

		<#include "persistence_impl_finder_find.ftl">

		<#include "persistence_impl_finder_remove.ftl">

		<#include "persistence_impl_finder_count.ftl">

		<#include "persistence_impl_finder_fields.ftl">
	</#list>

	public ${entity.name}PersistenceImpl() {
		<#if entity.badEntityColumns?size != 0>
			Map<String, String> dbColumnNames = new HashMap<String, String>();

			<#list entity.badEntityColumns as badEntityColumn>
				dbColumnNames.put("${badEntityColumn.name}", "${badEntityColumn.DBName}");
			</#list>

			<#if serviceBuilder.isVersionLTE_7_1_0()>
				try {
					Field field = BasePersistenceImpl.class.getDeclaredField("_dbColumnNames");

					field.setAccessible(true);

					field.set(this, dbColumnNames);
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception, exception);
					}
				}
			<#else>
				setDBColumnNames(dbColumnNames);
			</#if>
		</#if>

		setModelClass(${entity.name}.class);

		<#if !serviceBuilder.isVersionLTE_7_1_0()>
			setModelImplClass(${entity.name}Impl.class);
			setModelPKClass(${entity.PKClassName}.class);
			<#if serviceBuilder.isVersionLTE_7_2_0() && !dependencyInjectorDS>
				setEntityCacheEnabled(${entityCacheEnabled});
			</#if>
		</#if>

		<#if serviceBuilder.isDSLEnabled()>
			setTable(${entity.name}Table.INSTANCE);
		</#if>
	}

	<#if !serviceBuilder.isVersionGTE_7_4_0()>
		/**
		 * Caches the ${entity.humanName} in the entity cache if it is enabled.
		 *
		 * @param ${entity.variableName} the ${entity.humanName}
		 */
		@Override
		public void cacheResult(${entity.name} ${entity.variableName}) {
			<#if entity.isChangeTrackingEnabled()>
				try (SafeCloseable safeCloseable = CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(${entity.variableName}.getCtCollectionId())) {
			</#if>

			${entityCache}.putResult(
					<#if serviceBuilder.isVersionLTE_7_2_0()>
						${entityCacheEnabled},
					</#if>
					${entity.name}Impl.class, ${entity.variableName}.getPrimaryKey(), ${entity.variableName});

			<#list entity.uniqueEntityFinders as uniqueEntityFinder>
				<#assign entityColumns = uniqueEntityFinder.entityColumns />

				${finderCache}.putResult(
					_finderPathFetchBy${uniqueEntityFinder.name},
					new Object[] {
						<#list entityColumns as entityColumn>
							<#if stringUtil.equals(entityColumn.type, "boolean")>
								${entity.variableName}.is${entityColumn.methodName}()
							<#else>
								${entity.variableName}.get${entityColumn.methodName}()
							</#if>

							<#if entityColumn_has_next>
								,
							</#if>
						</#list>
					},
					${entity.variableName});
			</#list>

			<#if serviceBuilder.isVersionLTE_7_2_0()>
				${entity.variableName}.resetOriginalValues();
			</#if>

			<#if entity.isChangeTrackingEnabled()>
				}
			</#if>
		}

		private int _valueObjectFinderCacheListThreshold;

		/**
		 * Caches the ${entity.pluralHumanName} in the entity cache if it is enabled.
		 *
		 * @param ${entity.pluralVariableName} the ${entity.pluralHumanName}
		 */
		@Override
		public void cacheResult(List<${entity.name}> ${entity.pluralVariableName}) {
			if ((_valueObjectFinderCacheListThreshold == 0) ||
				((_valueObjectFinderCacheListThreshold > 0) &&
				 (${entity.pluralVariableName}.size() > _valueObjectFinderCacheListThreshold))) {

				return;
			}

			for (${entity.name} ${entity.variableName} : ${entity.pluralVariableName}) {
				<#if entity.isChangeTrackingEnabled()>
					try (SafeCloseable safeCloseable = CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(${entity.variableName}.getCtCollectionId())) {
				</#if>

				<#if (cacheFields?size > 0)>
					${entity.name} cached${entity.name} = (${entity.name})${entityCache}.getResult(
						<#if serviceBuilder.isVersionLTE_7_2_0()>
							${entityCacheEnabled},
						</#if>
						${entity.name}Impl.class, ${entity.variableName}.getPrimaryKey());

					if (cached${entity.name} == null) {
						cacheResult(${entity.variableName});
					}
					else {
						${entity.name}ModelImpl ${entity.variableName}ModelImpl = (${entity.name}ModelImpl)${entity.variableName};
						${entity.name}ModelImpl cached${entity.name}ModelImpl = (${entity.name}ModelImpl)cached${entity.name};

						<#list cacheFields as cacheField>
							<#assign
								getterPrefix = serviceBuilder.getCacheFieldGetterPrefix(cacheField)
								methodName = serviceBuilder.getCacheFieldMethodName(cacheField)
							/>

							${entity.variableName}ModelImpl.set${methodName}(cached${entity.name}ModelImpl.${getterPrefix}${methodName}());
						</#list>
					}
				<#else>
					if (${entityCache}.getResult(
						<#if serviceBuilder.isVersionLTE_7_2_0()>
							${entityCacheEnabled},
						</#if>
						${entity.name}Impl.class, ${entity.variableName}.getPrimaryKey()) == null) {
						cacheResult(${entity.variableName});
					}
					<#if serviceBuilder.isVersionLTE_7_2_0()>
						else {
							${entity.variableName}.resetOriginalValues();
						}
					</#if>
				</#if>

				<#if entity.isChangeTrackingEnabled()>
					}
				</#if>
			}
		}
	</#if>

	<#if !serviceBuilder.isVersionGTE_7_4_0()>
		/**
		 * Clears the cache for all ${entity.pluralHumanName}.
		 *
		 * <p>
		 * The <code>com.liferay.portal.kernel.dao.orm.EntityCache</code> and <code>com.liferay.portal.kernel.dao.orm.FinderCache</code> are both cleared by this method.
		 * </p>
		 */
		@Override
		public void clearCache() {
			${entityCache}.clearCache(${entity.name}Impl.class);

			<#if serviceBuilder.isVersionGTE_7_4_0()>
				${finderCache}.clearCache(${entity.name}Impl.class);
			<#else>
				${finderCache}.clearCache(FINDER_CLASS_NAME_ENTITY);
				${finderCache}.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
				${finderCache}.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
			</#if>
		}

		/**
		 * Clears the cache for the ${entity.humanName}.
		 *
		 * <p>
		 * The <code>com.liferay.portal.kernel.dao.orm.EntityCache</code> and <code>com.liferay.portal.kernel.dao.orm.FinderCache</code> are both cleared by this method.
		 * </p>
		 */
		@Override
		public void clearCache(${entity.name} ${entity.variableName}) {
			<#if serviceBuilder.isVersionGTE_7_3_0()>
				${entityCache}.removeResult(${entity.name}Impl.class, ${entity.variableName});
			<#else>
				${entityCache}.removeResult(${entityCacheEnabled}, ${entity.name}Impl.class, ${entity.variableName}.getPrimaryKey());

				${finderCache}.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
				${finderCache}.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

				<#if entity.uniqueEntityFinders?size &gt; 0>
					clearUniqueFindersCache((${entity.name}ModelImpl)${entity.variableName}, true);
				</#if>
			</#if>
		}

		@Override
		public void clearCache(List<${entity.name}> ${entity.pluralVariableName}) {
			<#if serviceBuilder.isVersionLTE_7_2_0()>
				${finderCache}.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
				${finderCache}.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
			</#if>

			for (${entity.name} ${entity.variableName} : ${entity.pluralVariableName}) {
				<#if serviceBuilder.isVersionGTE_7_3_0()>
					${entityCache}.removeResult(${entity.name}Impl.class, ${entity.variableName});
				<#else>
					${entityCache}.removeResult(${entityCacheEnabled}, ${entity.name}Impl.class, ${entity.variableName}.getPrimaryKey());

					<#if entity.uniqueEntityFinders?size &gt; 0>
						clearUniqueFindersCache((${entity.name}ModelImpl)${entity.variableName}, true);
					</#if>
				</#if>
			}
		}

		<#if serviceBuilder.isVersionGTE_7_3_0()>
			@Override
		</#if>
		public void clearCache(Set<Serializable> primaryKeys) {
			<#if serviceBuilder.isVersionGTE_7_4_0()>
				${finderCache}.clearCache(${entity.name}Impl.class);
			<#else>
				${finderCache}.clearCache(FINDER_CLASS_NAME_ENTITY);
				${finderCache}.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
				${finderCache}.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
			</#if>

			for (Serializable primaryKey : primaryKeys) {
				${entityCache}.removeResult(
					<#if serviceBuilder.isVersionLTE_7_2_0()>
						${entityCacheEnabled},
					</#if>
					${entity.name}Impl.class, primaryKey);
			}
		}
	</#if>

	<#if !serviceBuilder.isVersionGTE_7_4_0() && entity.uniqueEntityFinders?size &gt; 0>
		protected void cacheUniqueFindersCache(${entity.name}ModelImpl ${entity.variableName}ModelImpl) {
			<#if entity.isChangeTrackingEnabled()>
				try (SafeCloseable safeCloseable = CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(${entity.variableName}ModelImpl.getCtCollectionId())) {
			</#if>

			<#list entity.uniqueEntityFinders as uniqueEntityFinder>
				<#assign entityColumns = uniqueEntityFinder.entityColumns />

				<#if serviceBuilder.isVersionGTE_7_4_0()>
					${finderCache}.putResult(_finderPathFetchBy${uniqueEntityFinder.name}, ${entity.variableName}ModelImpl);
				<#else>
					<#if uniqueEntityFinder_index == 0>
						Object[]
					</#if>
					args = new Object[] {
						<#list entityColumns as entityColumn>
							<#if stringUtil.equals(entityColumn.type, "boolean")>
								${entity.variableName}ModelImpl.is${entityColumn.methodName}()
							<#elseif stringUtil.equals(entityColumn.type, "Date")>
								_getTime(${entity.variableName}ModelImpl.get${entityColumn.methodName}())
							<#else>
								${entity.variableName}ModelImpl.get${entityColumn.methodName}()
							</#if>

							<#if entityColumn_has_next>
								,
							</#if>
						</#list>
					};

					<#if serviceBuilder.isVersionLTE_7_3_0()>
						${finderCache}.putResult(_finderPathCountBy${uniqueEntityFinder.name}, args, Long.valueOf(1), false);
					</#if>
					${finderCache}.putResult(_finderPathFetchBy${uniqueEntityFinder.name}, args, ${entity.variableName}ModelImpl
						<#if serviceBuilder.isVersionLTE_7_3_0()>
							, false
						</#if>
						);
				</#if>
			</#list>

			<#if entity.isChangeTrackingEnabled()>
				}
			</#if>

		}

		<#if serviceBuilder.isVersionLTE_7_2_0()>
			protected void clearUniqueFindersCache(${entity.name}ModelImpl ${entity.variableName}ModelImpl, boolean clearCurrent) {
				<#list entity.uniqueEntityFinders as uniqueEntityFinder>
					<#assign entityColumns = uniqueEntityFinder.entityColumns />

					if (clearCurrent) {
						Object[] args = new Object[] {
							<#list entityColumns as entityColumn>
								<#if stringUtil.equals(entityColumn.type, "boolean")>
									${entity.variableName}ModelImpl.is${entityColumn.methodName}()
								<#elseif stringUtil.equals(entityColumn.type, "Date")>
									_getTime(${entity.variableName}ModelImpl.get${entityColumn.methodName}())
								<#else>
									${entity.variableName}ModelImpl.get${entityColumn.methodName}()
								</#if>

								<#if entityColumn_has_next>
									,
								</#if>
							</#list>
						};

						${finderCache}.removeResult(_finderPathCountBy${uniqueEntityFinder.name}, args);
						${finderCache}.removeResult(_finderPathFetchBy${uniqueEntityFinder.name}, args);
					}

					if (
						<#if columnBitmaskEnabled>
							(${entity.variableName}ModelImpl.getColumnBitmask() & _finderPathFetchBy${uniqueEntityFinder.name}.getColumnBitmask()) != 0
						<#else>
							<#list entityColumns as entityColumn>
								<#if entityColumn.isPrimitiveType()>
									<#if stringUtil.equals(entityColumn.type, "boolean")>
										(${entity.variableName}ModelImpl.is${entityColumn.methodName}() != ${entity.variableName}ModelImpl.getOriginal${entityColumn.methodName}())
									<#else>
										(${entity.variableName}ModelImpl.get${entityColumn.methodName}() != ${entity.variableName}ModelImpl.getOriginal${entityColumn.methodName}())
									</#if>
								<#else>
									!Objects.equals(${entity.variableName}ModelImpl.get${entityColumn.methodName}(), ${entity.variableName}ModelImpl.getOriginal${entityColumn.methodName}())
								</#if>

								<#if entityColumn_has_next>
									||
								</#if>
							</#list>
						</#if>
						) {

						Object[] args = new Object[] {
							<#list entityColumns as entityColumn>
								<#if stringUtil.equals(entityColumn.type, "Date")>
									_getTime(${entity.variableName}ModelImpl.getOriginal${entityColumn.methodName}())
								<#else>
									${entity.variableName}ModelImpl.getOriginal${entityColumn.methodName}()
								</#if>

								<#if entityColumn_has_next>
									,
								</#if>
							</#list>
						};

						${finderCache}.removeResult(_finderPathCountBy${uniqueEntityFinder.name}, args);
						${finderCache}.removeResult(_finderPathFetchBy${uniqueEntityFinder.name}, args);
					}
				</#list>
			}
		</#if>
	</#if>

	/**
	 * Creates a new ${entity.humanName} with the primary key. Does not add the ${entity.humanName} to the database.
	 *
	 * @param ${entity.PKVariableName} the primary key for the new ${entity.humanName}
	 * @return the new ${entity.humanName}
	 */
	@Override
	public ${entity.name} create(${entity.PKClassName} ${entity.PKVariableName}) {
		${entity.name} ${entity.variableName} = new ${entity.name}Impl();

		${entity.variableName}.setNew(true);
		${entity.variableName}.setPrimaryKey(${entity.PKVariableName});

		<#if entity.hasUuid()>
			String uuid = PortalUUIDUtil.generate();

			${entity.variableName}.setUuid(uuid);
		</#if>

		<#if entity.isShardedModel()>
			${entity.variableName}.setCompanyId(CompanyThreadLocal.getCompanyId());
		</#if>

		return ${entity.variableName};
	}

	/**
	 * Removes the ${entity.humanName} with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ${entity.PKVariableName} the primary key of the ${entity.humanName}
	 * @return the ${entity.humanName} that was removed
	 * @throws ${noSuchEntity}Exception if a ${entity.humanName} with the primary key could not be found
	 */
	@Override
	public ${entity.name} remove(${entity.PKClassName} ${entity.PKVariableName}) throws ${noSuchEntity}Exception {
		return remove((Serializable)${entity.PKVariableName});
	}

	<#if !serviceBuilder.isVersionGTE_7_4_0()>
		/**
		 * Removes the ${entity.humanName} with the primary key from the database. Also notifies the appropriate model listeners.
		 *
		 * @param primaryKey the primary key of the ${entity.humanName}
		 * @return the ${entity.humanName} that was removed
		 * @throws ${noSuchEntity}Exception if a ${entity.humanName} with the primary key could not be found
		 */
		@Override
		public ${entity.name} remove(Serializable primaryKey) throws ${noSuchEntity}Exception {
			Session session = null;

			try {
				session = openSession();

				${entity.name} ${entity.variableName} = (${entity.name})session.get(${entity.name}Impl.class, primaryKey);

				if (${entity.variableName} == null) {
					if (_log.isDebugEnabled()) {
						_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
					}

					throw new ${noSuchEntity}Exception(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				return remove(${entity.variableName});
			}
			catch (${noSuchEntity}Exception noSuchEntityException) {
				throw noSuchEntityException;
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}
	</#if>

	@Override
	protected ${entity.name} removeImpl(${entity.name} ${entity.variableName}) {
		<#list entity.entityColumns as entityColumn>
			<#if entityColumn.isCollection() && entityColumn.isMappingManyToMany()>
				<#assign referenceEntity = serviceBuilder.getEntity(entityColumn.entityName) />

				${entity.variableName}To${referenceEntity.name}TableMapper.deleteLeftPrimaryKeyTableMappings(${entity.variableName}.getPrimaryKey());
			</#if>
		</#list>

		<#if entity.localizedEntity??>
			<#assign
				localizedEntity = entity.localizedEntity
				pkEntityColumn = entity.PKEntityColumns?first
			/>

			${localizedEntity.variableName}Persistence.removeBy${pkEntityColumn.methodName}(${entity.variableName}.get${pkEntityColumn.methodName}());
		</#if>

		<#if entity.versionedEntity?? && entity.versionedEntity.localizedEntity??>
			<#assign
				versionedEntity = entity.versionedEntity
				localizedVersionEntity = versionedEntity.localizedEntity.versionEntity
				pkEntityColumn = versionedEntity.PKEntityColumns?first
			/>

			${localizedVersionEntity.variableName}Persistence.removeBy${pkEntityColumn.methodName}_Version(${entity.variableName}.getVersionedModelId(), ${entity.variableName}.getVersion());
		</#if>

		Session session = null;

		try {
			session = openSession();

			<#if entity.isHierarchicalTree()>
				if (rebuildTreeEnabled) {
					if (session.isDirty()) {
						session.flush();
					}

					nestedSetsTreeManager.delete(${entity.variableName});

					clearCache();

					session.clear();
				}
			</#if>

			if (!session.contains(${entity.variableName})) {
				${entity.variableName} = (${entity.name})session.get(${entity.name}Impl.class, ${entity.variableName}.getPrimaryKeyObj());
			}

			<#if entity.isChangeTrackingEnabled()>
				if ((${entity.variableName} != null) && ${ctPersistenceHelper}.isRemove(${entity.variableName})) {
			<#else>
				if (${entity.variableName} != null) {
			</#if>

				session.delete(${entity.variableName});
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (${entity.variableName} != null) {
			clearCache(${entity.variableName});
		}

		return ${entity.variableName};
	}

	@Override
	public ${entity.name} updateImpl(${apiPackagePath}.model.${entity.name} ${entity.variableName}) {
		boolean isNew = ${entity.variableName}.isNew();

		<#if entity.isHierarchicalTree() || (entity.collectionEntityFinders?size != 0) || (entity.uniqueEntityFinders?size &gt; 0) || entity.hasEntityColumn("createDate", "Date") || entity.hasEntityColumn("externalReferenceCode") || entity.hasEntityColumn("modifiedDate", "Date")>
			if (!(${entity.variableName} instanceof ${entity.name}ModelImpl)) {
				InvocationHandler invocationHandler = null;

				if (ProxyUtil.isProxyClass(${entity.variableName}.getClass())) {
					invocationHandler = ProxyUtil.getInvocationHandler(${entity.variableName});

					throw new IllegalArgumentException("Implement ModelWrapper in ${entity.variableName} proxy " + invocationHandler.getClass());
				}

				throw new IllegalArgumentException("Implement ModelWrapper in custom ${entity.name} implementation " + ${entity.variableName}.getClass());
			}

			${entity.name}ModelImpl ${entity.variableName}ModelImpl = (${entity.name}ModelImpl)${entity.variableName};
		</#if>

		<#if entity.hasUuid()>
			if (Validator.isNull(${entity.variableName}.getUuid())) {
				String uuid = PortalUUIDUtil.generate();

				${entity.variableName}.setUuid(uuid);
			}
		</#if>

		<#if entity.hasEntityColumn("externalReferenceCode")>
			if (Validator.isNull(${entity.variableName}.getExternalReferenceCode())) {
				<#if entity.hasUuid()>
					${entity.variableName}.setExternalReferenceCode(${entity.variableName}.getUuid());
				<#else>
					${entity.variableName}.setExternalReferenceCode(String.valueOf(${entity.variableName}.getPrimaryKey()));
				</#if>
			}
			else {
				<#if serviceBuilder.isVersionGTE_7_3_0()>
					if (!Objects.equals(${entity.variableName}ModelImpl.getColumnOriginalValue("externalReferenceCode"), ${entity.variableName}.getExternalReferenceCode())) {
				<#else>
					if (!Objects.equals(${entity.variableName}ModelImpl.getOriginalExternalReferenceCode(), ${entity.variableName}.getExternalReferenceCode())) {
				</#if>

					long userId = GetterUtil.getLong(PrincipalThreadLocal.getName());

					if (userId > 0) {
						<#if entity.hasEntityColumn("companyId")>
							long companyId = ${entity.variableName}.getCompanyId();
						<#else>
							long companyId = 0;
						</#if>

						<#if entity.hasEntityColumn("groupId")>
							long groupId = ${entity.variableName}.getGroupId();
						<#else>
							long groupId = 0;
						</#if>

						long classPK = 0;

						if (!isNew) {
							classPK = ${entity.variableName}.getPrimaryKey();
						}

						try {
							${entity.variableName}.setExternalReferenceCode(SanitizerUtil.sanitize(companyId, groupId, userId, ${apiPackagePath}.model.${entity.name}.class.getName(), classPK, ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL, ${entity.variableName}.getExternalReferenceCode(), null));
						}
						catch (SanitizerException sanitizerException) {
							throw new SystemException(sanitizerException);
						}
					}
				}

				<#if entity.hasExternalReferenceCode()>
					<#if serviceBuilder.isVersionGTE_7_4_0()>
						<#if entity.versionEntity??>
							${entity.name} erc${entity.name} = fetchByERC_${entity.externalReferenceCode?cap_first[0..0]}_Head(${entity.variableName}.getExternalReferenceCode(), ${entity.variableName}.get${entity.externalReferenceCode?cap_first}Id(), ${entity.variableName}.isHead());
						<#else>
							${entity.name} erc${entity.name} = fetchByERC_${entity.externalReferenceCode?cap_first[0..0]}(${entity.variableName}.getExternalReferenceCode(), ${entity.variableName}.get${entity.externalReferenceCode?cap_first}Id());
						</#if>
					<#else>
						${entity.name} erc${entity.name} = fetchBy${entity.externalReferenceCode?cap_first[0..0]}_ERC(${entity.variableName}.get${entity.externalReferenceCode?cap_first}Id(), ${entity.variableName}.getExternalReferenceCode());
					</#if>

					if (isNew) {
						if (erc${entity.name} != null) {
								throw new ${duplicateEntityExternalReferenceCode}Exception("Duplicate ${entity.humanName} with external reference code " + ${entity.variableName}.getExternalReferenceCode() + " and ${entity.externalReferenceCode} " + ${entity.variableName}.get${entity.externalReferenceCode?cap_first}Id());
						}
					}
					else {
						if ((erc${entity.name} != null) &&
							(${entity.variableName}.get${entity.PKMethodName}() != erc${entity.name}.get${entity.PKMethodName}())) {
								throw new ${duplicateEntityExternalReferenceCode}Exception("Duplicate ${entity.humanName} with external reference code " + ${entity.variableName}.getExternalReferenceCode() + " and ${entity.externalReferenceCode} " + ${entity.variableName}.get${entity.externalReferenceCode?cap_first}Id());
						}
					}
				</#if>
			}
		</#if>

		<#if entity.hasEntityColumn("createDate", "Date") && entity.hasEntityColumn("modifiedDate", "Date")>
			ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();
		</#if>

		<#if entity.hasEntityColumn("createDate", "Date")>
			if (isNew && (${entity.variableName}.getCreateDate() == null)) {

			<#if !entity.hasEntityColumn("modifiedDate", "Date")>
				ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();

				Date date = new Date();
			</#if>

				if (serviceContext == null) {
					${entity.variableName}.setCreateDate(date);
				}
				else {
					${entity.variableName}.setCreateDate(serviceContext.getCreateDate(date));
				}
			}
		</#if>

		<#if entity.hasEntityColumn("modifiedDate", "Date")>
			if (!${entity.variableName}ModelImpl.hasSetModifiedDate()) {

			<#if !entity.hasEntityColumn("createDate", "Date")>
				ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();

				Date date = new Date();
			</#if>

				if (serviceContext == null) {
					${entity.variableName}.setModifiedDate(date);
				}
				else {
					${entity.variableName}.setModifiedDate(serviceContext.getModifiedDate(date));
				}
			}
		</#if>

		<#assign sanitizeTuples = modelHintsUtil.getSanitizeTuples("${apiPackagePath}.model.${entity.name}") />

		<#if sanitizeTuples?size != 0>
			long userId = GetterUtil.getLong(PrincipalThreadLocal.getName());

			if (userId > 0) {
				<#if entity.hasEntityColumn("companyId")>
					long companyId = ${entity.variableName}.getCompanyId();
				<#else>
					long companyId = 0;
				</#if>

				<#if entity.hasEntityColumn("groupId")>
					long groupId = ${entity.variableName}.getGroupId();
				<#else>
					long groupId = 0;
				</#if>

				long ${entity.PKVariableName} = 0;

				if (!isNew) {
					${entity.PKVariableName} = ${entity.variableName}.getPrimaryKey();
				}

				try {
					<#list sanitizeTuples as sanitizeTuple>
						<#assign
							colMethodName = textFormatter.format(sanitizeTuple.getObject(0), 6)

							contentType = "\"" + sanitizeTuple.getObject(1) + "\""
						/>

						<#if contentType == "\"text/html\"">
							<#assign contentType = "ContentTypes.TEXT_HTML" />
						<#elseif contentType == "\"text/plain\"">
							<#assign contentType = "ContentTypes.TEXT_PLAIN" />
						</#if>

						<#assign modes = "\"" + sanitizeTuple.getObject(2) + "\"" />

						<#if modes == "\"ALL\"">
							<#assign modes = "Sanitizer.MODE_ALL" />
						<#elseif modes == "\"BAD_WORDS\"">
							<#assign modes = "Sanitizer.MODE_BAD_WORDS" />
						<#elseif modes == "\"XSS\"">
							<#assign modes = "Sanitizer.MODE_XSS" />
						<#else>
							<#assign modes = "StringUtil.split(\"" + sanitizeTuple.getObject(2) + "\")" />
						</#if>

						${entity.variableName}.set${colMethodName}(SanitizerUtil.sanitize(companyId, groupId, userId, ${apiPackagePath}.model.${entity.name}.class.getName(), ${entity.PKVariableName}, ${contentType}, ${modes}, ${entity.variableName}.get${colMethodName}(), null));
					</#list>
				}
				catch (SanitizerException sanitizerException) {
					throw new SystemException(sanitizerException);
				}
			}
		</#if>

		Session session = null;

		try {
			session = openSession();

			<#if entity.isHierarchicalTree()>
				if (rebuildTreeEnabled) {
					if (session.isDirty()) {
						session.flush();
					}

					if (isNew) {
						nestedSetsTreeManager.insert(${entity.variableName}, fetchByPrimaryKey(${entity.variableName}.getParent${pkEntityColumn.methodName}()));
					}
					<#if serviceBuilder.isVersionGTE_7_3_0()>
						else if ((${entity.variableName}ModelImpl.getColumnOriginalValue("parent${pkEntityColumn.methodName}") != null) && !Objects.equals(${entity.variableName}.getParent${pkEntityColumn.methodName}(), ${entity.variableName}ModelImpl.getColumnOriginalValue("parent${pkEntityColumn.methodName}"))){
							nestedSetsTreeManager.move(${entity.variableName}, fetchByPrimaryKey(${entity.variableName}ModelImpl.getColumnOriginalValue("parent${pkEntityColumn.methodName}")), fetchByPrimaryKey(${entity.variableName}.getParent${pkEntityColumn.methodName}()));
					<#else>
						else if (${entity.variableName}.getParent${pkEntityColumn.methodName}() != ${entity.variableName}ModelImpl.getOriginalParent${pkEntityColumn.methodName}()){
							nestedSetsTreeManager.move(${entity.variableName}, fetchByPrimaryKey(${entity.variableName}ModelImpl.getOriginalParent${pkEntityColumn.methodName}()), fetchByPrimaryKey(${entity.variableName}.getParent${pkEntityColumn.methodName}()));
					</#if>
					}

					clearCache();

					session.clear();
				}
			</#if>

			<#if entity.isChangeTrackingEnabled()>
				if (${ctPersistenceHelper}.isInsert(${entity.variableName})) {
					if (!isNew) {
						session.evict(${entity.name}Impl.class, ${entity.variableName}.getPrimaryKeyObj());
					}
			<#else>
				if (isNew) {
			</#if>

				session.save(${entity.variableName});

				<#if serviceBuilder.isVersionLTE_7_2_0()>
					${entity.variableName}.setNew(false);
				</#if>
			}
			else {
				<#if entity.versionedEntity??>
					throw new IllegalArgumentException("${entity.name} is read only, create a new version instead");
				<#elseif entity.hasLazyBlobEntityColumn() && !serviceBuilder.isVersionGTE_7_4_0()>

					<#-- Workaround for HHH-2680 -->

					<#if serviceBuilder.isVersionGTE_7_3_0()>
						session.evict(${entity.name}Impl.class, ${entity.variableName}.getPrimaryKeyObj());
					<#else>
						session.evict(${entity.variableName});
					</#if>

					session.saveOrUpdate(${entity.variableName});
				<#else>
					${entity.variableName} = (${entity.name})session.merge(${entity.variableName});
				</#if>
			}

			<#if entity.hasLazyBlobEntityColumn()>
				session.flush();
				session.clear();
			</#if>
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		<#if serviceBuilder.isVersionGTE_7_3_0()>
			<#if serviceBuilder.isVersionGTE_7_4_0()>
				cacheUniqueFindersResult(${entity.variableName}, false);
			<#else>
				${entityCache}.putResult(
					${entity.name}Impl.class,
					<#if (entity.collectionEntityFinders?size != 0) || (entity.uniqueEntityFinders?size &gt; 0)>
						${entity.variableName}ModelImpl
					<#else>
						${entity.variableName}
					</#if>
					, false, true);

				<#if entity.uniqueEntityFinders?size &gt; 0>
					cacheUniqueFindersCache(${entity.variableName}ModelImpl);
				</#if>
			</#if>

			if (isNew) {
				${entity.variableName}.setNew(false);
			}
		<#else>
			${finderCache}.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

			<#if columnBitmaskEnabled>
				if (!${columnBitmaskCacheEnabled}) {
					${finderCache}.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
				}
				else
			</#if>

			if (isNew) {
				<#if entity.finderEntityColumns?size &gt; 64>
					${finderCache}.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
				<#else>
					<#if columnBitmaskEnabled && (entity.collectionEntityFinders?size != 0)>
						Object[]
						<#list entity.collectionEntityFinders as entityFinder>
							<#assign entityColumns = entityFinder.entityColumns />

							args = new Object[] {
								<#list entityColumns as entityColumn>
									<#if stringUtil.equals(entityColumn.type, "boolean")>
										${entity.variableName}ModelImpl.is${entityColumn.methodName}()
									<#else>
										${entity.variableName}ModelImpl.get${entityColumn.methodName}()
									</#if>

									<#if entityColumn_has_next>
										,
									</#if>
								</#list>
							};

							${finderCache}.removeResult(_finderPathCountBy${entityFinder.name}, args);
							${finderCache}.removeResult(_finderPathWithoutPaginationFindBy${entityFinder.name}, args);
						</#list>
					</#if>

					${finderCache}.removeResult(_finderPathCountAll, FINDER_ARGS_EMPTY);
					${finderCache}.removeResult(_finderPathWithoutPaginationFindAll, FINDER_ARGS_EMPTY);
				</#if>
			}

			<#if entity.collectionEntityFinders?size != 0>
				else {
					<#list entity.collectionEntityFinders as entityFinder>
						<#assign entityColumns = entityFinder.entityColumns />
						if (
							<#if columnBitmaskEnabled>
								(${entity.variableName}ModelImpl.getColumnBitmask() & _finderPathWithoutPaginationFindBy${entityFinder.name}.getColumnBitmask()) != 0
							<#else>
								<#list entityColumns as entityColumn>
									<#if entityColumn.isPrimitiveType()>
										<#if stringUtil.equals(entityColumn.type, "boolean")>
											(${entity.variableName}.is${entityColumn.methodName}() != ${entity.variableName}ModelImpl.getOriginal${entityColumn.methodName}())
										<#else>
											(${entity.variableName}.get${entityColumn.methodName}() != ${entity.variableName}ModelImpl.getOriginal${entityColumn.methodName}())
										</#if>
									<#else>
										!Objects.equals(${entity.variableName}.get${entityColumn.methodName}(), ${entity.variableName}ModelImpl.getOriginal${entityColumn.methodName}())
									</#if>

									<#if entityColumn_has_next>
										||
									</#if>
								</#list>
							</#if>
							) {

							Object[] args = new Object[] {
								<#list entityColumns as entityColumn>
									${entity.variableName}ModelImpl.getOriginal${entityColumn.methodName}()

									<#if entityColumn_has_next>
										,
									</#if>
								</#list>
							};

							${finderCache}.removeResult(_finderPathCountBy${entityFinder.name}, args);
							${finderCache}.removeResult(_finderPathWithoutPaginationFindBy${entityFinder.name}, args);

							args = new Object[] {
								<#list entityColumns as entityColumn>
									<#if stringUtil.equals(entityColumn.type, "boolean")>
										${entity.variableName}ModelImpl.is${entityColumn.methodName}()
									<#else>
										${entity.variableName}ModelImpl.get${entityColumn.methodName}()
									</#if>

									<#if entityColumn_has_next>
										,
									</#if>
								</#list>
							};

							${finderCache}.removeResult(_finderPathCountBy${entityFinder.name}, args);
							${finderCache}.removeResult(_finderPathWithoutPaginationFindBy${entityFinder.name}, args);
						}
					</#list>
				}
			</#if>

			${entityCache}.putResult(${entityCacheEnabled}, ${entity.name}Impl.class, ${entity.variableName}.getPrimaryKey(), ${entity.variableName}, false);

			<#if entity.uniqueEntityFinders?size &gt; 0>
				clearUniqueFindersCache(${entity.variableName}ModelImpl, false);
				cacheUniqueFindersCache(${entity.variableName}ModelImpl);
			</#if>
		</#if>

		${entity.variableName}.resetOriginalValues();

		return ${entity.variableName};
	}

	<#if !serviceBuilder.isVersionGTE_7_4_0()>
		/**
		 * Returns the ${entity.humanName} with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
		 *
		 * @param primaryKey the primary key of the ${entity.humanName}
		 * @return the ${entity.humanName}
		 * @throws ${noSuchEntity}Exception if a ${entity.humanName} with the primary key could not be found
		 */
		@Override
		public ${entity.name} findByPrimaryKey(Serializable primaryKey) throws ${noSuchEntity}Exception {
			${entity.name} ${entity.variableName} = fetchByPrimaryKey(primaryKey);

			if (${entity.variableName} == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new ${noSuchEntity}Exception(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return ${entity.variableName};
		}
	</#if>

	/**
	 * Returns the ${entity.humanName} with the primary key or throws a <code>${noSuchEntity}Exception</code> if it could not be found.
	 *
	 * @param ${entity.PKVariableName} the primary key of the ${entity.humanName}
	 * @return the ${entity.humanName}
	 * @throws ${noSuchEntity}Exception if a ${entity.humanName} with the primary key could not be found
	 */
	@Override
	public ${entity.name} findByPrimaryKey(${entity.PKClassName} ${entity.PKVariableName}) throws ${noSuchEntity}Exception {
		return findByPrimaryKey((Serializable)${entity.PKVariableName});
	}

	<#if serviceBuilder.isVersionLTE_7_1_0()>
		/**
		 * Returns the ${entity.humanName} with the primary key or returns <code>null</code> if it could not be found.
		 *
		 * @param primaryKey the primary key of the ${entity.humanName}
		 * @return the ${entity.humanName}, or <code>null</code> if a ${entity.humanName} with the primary key could not be found
		 */
		@Override
		public ${entity.name} fetchByPrimaryKey(Serializable primaryKey) {
			Serializable serializable = ${entityCache}.getResult(${entityCacheEnabled}, ${entity.name}Impl.class, primaryKey);

			if (serializable == nullModel) {
				return null;
			}

			${entity.name} ${entity.variableName} = (${entity.name})serializable;

			if (${entity.variableName} == null) {
				Session session = null;

				try {
					session = openSession();

					${entity.variableName} = (${entity.name})session.get(${entity.name}Impl.class, primaryKey);

					if (${entity.variableName} != null) {
						cacheResult(${entity.variableName});
					}
					else {
						${entityCache}.putResult(${entityCacheEnabled}, ${entity.name}Impl.class, primaryKey, nullModel);
					}
				}
				catch (Exception exception) {
					<#if serviceBuilder.isVersionLTE_7_2_0()>
						${entityCache}.removeResult(${entityCacheEnabled}, ${entity.name}Impl.class, primaryKey);
					</#if>

					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return ${entity.variableName};
		}
	<#elseif entity.isChangeTrackingEnabled() && !serviceBuilder.isVersionGTE_7_4_0()>
		/**
		 * Returns the ${entity.humanName} with the primary key or returns <code>null</code> if it could not be found.
		 *
		 * @param primaryKey the primary key of the ${entity.humanName}
		 * @return the ${entity.humanName}, or <code>null</code> if a ${entity.humanName} with the primary key could not be found
		 */
		@Override
		public ${entity.name} fetchByPrimaryKey(Serializable primaryKey) {
			if (${ctPersistenceHelper}.isProductionMode(${entity.name}.class, primaryKey)) {
				try (SafeCloseable safeCloseable = CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {
					return super.fetchByPrimaryKey(primaryKey);
				}
			}

			${entity.name} ${entity.variableName} = (${entity.name})${entityCache}.getResult(${entity.name}Impl.class, primaryKey);

			if (${entity.variableName} != null) {
				return ${entity.variableName};
			}

			Session session = null;

			try {
				session = openSession();

				${entity.variableName} = (${entity.name})session.get(${entity.name}Impl.class, primaryKey);

				if (${entity.variableName} != null) {
					cacheResult(${entity.variableName});
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}

			return ${entity.variableName};
		}
	</#if>

	<#if entity.isChangeTrackingEnabled() && serviceBuilder.isVersionGTE_7_4_0()>
		@Override
		protected CTPersistenceHelper getCTPersistenceHelper() {
			<#if osgiModule>
				return ctPersistenceHelper;
			<#else>
				return CTPersistenceHelperUtil.getCTPersistenceHelper();
			</#if>
		}
	</#if>

	/**
	 * Returns the ${entity.humanName} with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ${entity.PKVariableName} the primary key of the ${entity.humanName}
	 * @return the ${entity.humanName}, or <code>null</code> if a ${entity.humanName} with the primary key could not be found
	 */
	@Override
	public ${entity.name} fetchByPrimaryKey(${entity.PKClassName} ${entity.PKVariableName}) {
		return fetchByPrimaryKey((Serializable)${entity.PKVariableName});
	}

	<#if serviceBuilder.isVersionLTE_7_1_0()>
		@Override
		public Map<Serializable, ${entity.name}> fetchByPrimaryKeys(Set<Serializable> primaryKeys) {
			if (primaryKeys.isEmpty()) {
				return Collections.emptyMap();
			}

			Map<Serializable, ${entity.name}> map = new HashMap<Serializable, ${entity.name}>();

			<#if entity.hasCompoundPK()>
				for (Serializable primaryKey : primaryKeys) {
					${entity.name} ${entity.variableName} = fetchByPrimaryKey(primaryKey);

					if (${entity.variableName} != null) {
						map.put(primaryKey, ${entity.variableName});
					}
				}

				return map;
			<#else>
				if (primaryKeys.size() == 1) {
					Iterator<Serializable> iterator = primaryKeys.iterator();

					Serializable primaryKey = iterator.next();

					${entity.name} ${entity.variableName} = fetchByPrimaryKey(primaryKey);

					if (${entity.variableName} != null) {
						map.put(primaryKey, ${entity.variableName});
					}

					return map;
				}

				if ((${databaseInMaxParameters} > 0) && (primaryKeys.size() > ${databaseInMaxParameters})) {
					Iterator<Serializable> iterator = primaryKeys.iterator();

					while (iterator.hasNext()) {
						Set<Serializable> page = new HashSet<>();

						for (int i = 0; (i < ${databaseInMaxParameters}) && iterator.hasNext(); i++) {
							page.add(iterator.next());
						}

						map.putAll(fetchByPrimaryKeys(page));
					}

					return map;
				}

				Set<Serializable> uncachedPrimaryKeys = null;

				for (Serializable primaryKey : primaryKeys) {
					Serializable serializable = ${entityCache}.getResult(${entityCacheEnabled}, ${entity.name}Impl.class, primaryKey);

					if (serializable != nullModel) {
						if (serializable == null) {
							if (uncachedPrimaryKeys == null) {
								uncachedPrimaryKeys = new HashSet<Serializable>();
							}

							uncachedPrimaryKeys.add(primaryKey);
						}
						else {
							map.put(primaryKey, (${entity.name})serializable);
						}
					}
				}

				if (uncachedPrimaryKeys == null) {
					return map;
				}

				StringBundler sb = new StringBundler(uncachedPrimaryKeys.size() * 2 + 1);

				sb.append(_SQL_SELECT_${entity.alias?upper_case}_WHERE_PKS_IN);

				<#if stringUtil.equals(entity.PKClassName, "String")>
					for (int i = 0; i < uncachedPrimaryKeys.size(); i++) {
						sb.append("?");

						sb.append(",");
					}
				<#else>
					for (Serializable primaryKey : uncachedPrimaryKeys) {
						sb.append((${entity.PKClassName})primaryKey);

						sb.append(",");
					}
				</#if>

				sb.setIndex(sb.index() - 1);

				sb.append(")");

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					<#if stringUtil.equals(entity.PKClassName, "String")>
						QueryPos queryPos = QueryPos.getInstance(query);

						for (Serializable primaryKey : uncachedPrimaryKeys) {
							queryPos.add((String)primaryKey);
						}
					</#if>

					for (${entity.name} ${entity.variableName} : (List<${entity.name}>)query.list()) {
						map.put(${entity.variableName}.getPrimaryKeyObj(), ${entity.variableName});

						cacheResult(${entity.variableName});

						uncachedPrimaryKeys.remove(${entity.variableName}.getPrimaryKeyObj());
					}

					for (Serializable primaryKey : uncachedPrimaryKeys) {
						${entityCache}.putResult(${entityCacheEnabled}, ${entity.name}Impl.class, primaryKey, nullModel);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}

				return map;
			</#if>
		}
	<#elseif entity.isChangeTrackingEnabled() && !serviceBuilder.isVersionGTE_7_4_0()>
		@Override
		public Map<Serializable, ${entity.name}> fetchByPrimaryKeys(Set<Serializable> primaryKeys) {
			if (${ctPersistenceHelper}.isProductionMode(${entity.name}.class)) {
				try (SafeCloseable safeCloseable = CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {
					return super.fetchByPrimaryKeys(primaryKeys);
				}
			}

			if (primaryKeys.isEmpty()) {
				return Collections.emptyMap();
			}

			Map<Serializable, ${entity.name}> map = new HashMap<Serializable, ${entity.name}>();

			if (primaryKeys.size() == 1) {
				Iterator<Serializable> iterator = primaryKeys.iterator();

				Serializable primaryKey = iterator.next();

				${entity.name} ${entity.variableName} = fetchByPrimaryKey(primaryKey);

				if (${entity.variableName} != null) {
					map.put(primaryKey, ${entity.variableName});
				}

				return map;
			}

			Set<Serializable> uncachedPrimaryKeys = null;

			for (Serializable primaryKey : primaryKeys) {
				try (SafeCloseable safeCloseable = ${ctPersistenceHelper}.setCTCollectionIdWithSafeCloseable(${entity.name}.class, primaryKey)) {

					${entity.name} ${entity.variableName} = (${entity.name})${entityCache}.getResult(${entity.name}Impl.class, primaryKey);

					if (${entity.variableName} == null) {
						if (uncachedPrimaryKeys == null) {
							uncachedPrimaryKeys = new HashSet<>();
						}

						uncachedPrimaryKeys.add(primaryKey);
					}
					else {
						map.put(primaryKey, ${entity.variableName});
					}
				}
			}

			if (uncachedPrimaryKeys == null) {
				return map;
			}

			if ((${databaseInMaxParameters} > 0) && (primaryKeys.size() > ${databaseInMaxParameters})) {
				Iterator<Serializable> iterator = primaryKeys.iterator();

				while (iterator.hasNext()) {
					Set<Serializable> page = new HashSet<>();

					for (int i = 0; (i < ${databaseInMaxParameters}) && iterator.hasNext();i++) {
						page.add(iterator.next());
					}

					map.putAll(fetchByPrimaryKeys(page));
				}

				return map;
			}

			StringBundler sb = new StringBundler(primaryKeys.size() * 2 + 1);

			sb.append(getSelectSQL());
			sb.append(" WHERE ");
			sb.append(getPKDBName());
			sb.append(" IN (");

			for (Serializable primaryKey : primaryKeys) {
				sb.append((${entity.PKClassName})primaryKey);

				sb.append(",");
			}

			sb.setIndex(sb.index() - 1);

			sb.append(")");

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				for (${entity.name} ${entity.variableName} : (List<${entity.name}>)query.list()) {
					map.put(${entity.variableName}.getPrimaryKeyObj(), ${entity.variableName});

					cacheResult(${entity.variableName});
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}

			return map;
		}
	</#if>

	<#if !serviceBuilder.isVersionGTE_7_4_0()>
		/**
		 * Returns all the ${entity.pluralHumanName}.
		 *
		 * @return the ${entity.pluralHumanName}
		 */
		@Override
		public List<${entity.name}> findAll() {
			return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}

		/**
		 * Returns a range of all the ${entity.pluralHumanName}.
		 *
		 * <p>
		 * <#include "range_comment.ftl">
		 * </p>
		 *
		 * @param start the lower bound of the range of ${entity.pluralHumanName}
		 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
		 * @return the range of ${entity.pluralHumanName}
		 */
		@Override
		public List<${entity.name}> findAll(int start, int end) {
			return findAll(start, end, null);
		}

		/**
		 * Returns an ordered range of all the ${entity.pluralHumanName}.
		 *
		 * <p>
		 * <#include "range_comment.ftl">
		 * </p>
		 *
		 * @param start the lower bound of the range of ${entity.pluralHumanName}
		 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
		 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
		 * @return the ordered range of ${entity.pluralHumanName}
		 */
		@Override
		public List<${entity.name}> findAll(int start, int end, OrderByComparator<${entity.name}> orderByComparator) {
			return findAll(start, end, orderByComparator, true);
		}

		/**
		 * Returns an ordered range of all the ${entity.pluralHumanName}.
		 *
		 * <p>
		 * <#include "range_comment.ftl">
		 * </p>
		 *
		 * @param start the lower bound of the range of ${entity.pluralHumanName}
		 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
		 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
		 * @param useFinderCache whether to use the finder cache
		 * @return the ordered range of ${entity.pluralHumanName}
		 */
		@Override
		public List<${entity.name}> findAll(int start, int end, OrderByComparator<${entity.name}> orderByComparator, boolean useFinderCache) {
			<#if entity.isChangeTrackingEnabled()>
				try (SafeCloseable safeCloseable = ${ctPersistenceHelper}.setCTCollectionIdWithSafeCloseable(${entity.name}.class)) {
			</#if>

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) && (orderByComparator == null)) {
				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindAll;
					finderArgs = FINDER_ARGS_EMPTY;
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindAll;
				finderArgs = new Object[] {start, end, orderByComparator};
			}

			List<${entity.name}> list = null;

			if (useFinderCache) {
				list = (List<${entity.name}>)${finderCache}.getResult(finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_${entity.alias?upper_case});

					appendOrderByComparator(sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_${entity.alias?upper_case};

					sql = sql.concat(${entity.name}ModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<${entity.name}>)QueryUtil.list(query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						${finderCache}.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					<#if serviceBuilder.isVersionLTE_7_2_0()>
						if (useFinderCache) {
							${finderCache}.removeResult(finderPath, finderArgs);
						}
					</#if>

					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;

			<#if entity.isChangeTrackingEnabled()>
				}
			</#if>
		}
	</#if>

	<#if !serviceBuilder.isVersionGTE_7_4_0()>
		/**
		 * Removes all the ${entity.pluralHumanName} from the database.
		 *
		 */
		@Override
		public void removeAll() {
			for (${entity.name} ${entity.variableName} : findAll()) {
				remove(${entity.variableName});
			}
		}
	</#if>

	<#if !serviceBuilder.isVersionGTE_7_4_0()>
		/**
		 * Returns the number of ${entity.pluralHumanName}.
		 *
		 * @return the number of ${entity.pluralHumanName}
		 */
		@Override
		public int countAll() {
			<#if entity.isChangeTrackingEnabled()>
				try (SafeCloseable safeCloseable = ${ctPersistenceHelper}.setCTCollectionIdWithSafeCloseable(${entity.name}.class)) {
			</#if>

			Long count = (Long)${finderCache}.getResult(_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery("SELECT COUNT(${entity.alias}) FROM ${entity.name} ${entity.alias}");

					count = (Long)query.uniqueResult();

					${finderCache}.putResult(_finderPathCountAll, FINDER_ARGS_EMPTY, count);
				}
				catch (Exception exception) {
					<#if serviceBuilder.isVersionLTE_7_2_0()>
						${finderCache}.removeResult(_finderPathCountAll, FINDER_ARGS_EMPTY);
					</#if>

					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();

			<#if entity.isChangeTrackingEnabled()>
				}
			</#if>
		}
	</#if>

	<#list entity.entityColumns as entityColumn>
		<#if entityColumn.isCollection() && entityColumn.isMappingManyToMany()>
			<#assign referenceEntity = serviceBuilder.getEntity(entityColumn.entityName) />

			/**
			 * Returns the primaryKeys of ${referenceEntity.pluralHumanName} associated with the ${entity.humanName}.
			 *
			 * @param pk the primary key of the ${entity.humanName}
			 * @return long[] of the primaryKeys of ${referenceEntity.pluralHumanName} associated with the ${entity.humanName}
			 */
			@Override
			public long[] get${referenceEntity.name}PrimaryKeys(${entity.PKClassName} pk) {
				long[] pks = ${entity.variableName}To${referenceEntity.name}TableMapper.getRightPrimaryKeys(pk);

				return pks.clone();
			}

			<#if dependencyInjectorDS>
				/**
				 * Returns all the ${entity.humanName} associated with the ${referenceEntity.humanName}.
				 *
				 * @param pk the primary key of the ${referenceEntity.humanName}
				 * @return the ${entity.pluralHumanName} associated with the ${referenceEntity.humanName}
				 */
				@Override
				public List<${entity.name}> get${referenceEntity.name}${entity.pluralName}(${referenceEntity.PKClassName} pk) {
					return get${referenceEntity.name}${entity.pluralName}(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
				}

				/**
				 * Returns all the ${entity.humanName} associated with the ${referenceEntity.humanName}.
				 *
				 * <p>
				 * <#include "range_comment.ftl">
				 * </p>
				 *
				 * @param pk the primary key of the ${referenceEntity.humanName}
				 * @param start the lower bound of the range of ${referenceEntity.pluralHumanName}
				 * @param end the upper bound of the range of ${referenceEntity.pluralHumanName} (not inclusive)
				 * @return the range of ${entity.pluralHumanName} associated with the ${referenceEntity.humanName}
				 */
				@Override
				public List<${entity.name}> get${referenceEntity.name}${entity.pluralName}(${referenceEntity.PKClassName} pk, int start, int end) {
					return get${referenceEntity.name}${entity.pluralName}(pk, start, end, null);
				}

				/**
				 * Returns all the ${entity.humanName} associated with the ${referenceEntity.humanName}.
				 *
				 * <p>
				 * <#include "range_comment.ftl">
				 * </p>
				 *
				 * @param pk the primary key of the ${referenceEntity.humanName}
				 * @param start the lower bound of the range of ${referenceEntity.pluralHumanName}
				 * @param end the upper bound of the range of ${referenceEntity.pluralHumanName} (not inclusive)
				 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
				 * @return the ordered range of ${entity.pluralHumanName} associated with the ${referenceEntity.humanName}
				 */
				@Override
				public List<${entity.name}> get${referenceEntity.name}${entity.pluralName}(${referenceEntity.PKClassName} pk, int start, int end, OrderByComparator<${entity.name}> orderByComparator) {
					return ${entity.variableName}To${referenceEntity.name}TableMapper.getLeftBaseModels(pk, start, end, orderByComparator);
				}
			<#else>
				/**
				 * Returns all the ${referenceEntity.pluralHumanName} associated with the ${entity.humanName}.
				 *
				 * @param pk the primary key of the ${entity.humanName}
				 * @return the ${referenceEntity.pluralHumanName} associated with the ${entity.humanName}
				 */
				@Override
				public List<${referenceEntity.apiPackagePath}.model.${referenceEntity.name}> get${referenceEntity.pluralName}(${entity.PKClassName} pk) {
					return get${referenceEntity.pluralName}(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
				}

				/**
				 * Returns a range of all the ${referenceEntity.pluralHumanName} associated with the ${entity.humanName}.
				 *
				 * <p>
				 * <#include "range_comment.ftl">
				 * </p>
				 *
				 * @param pk the primary key of the ${entity.humanName}
				 * @param start the lower bound of the range of ${entity.pluralHumanName}
				 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
				 * @return the range of ${referenceEntity.pluralHumanName} associated with the ${entity.humanName}
				 */
				@Override
				public List<${referenceEntity.apiPackagePath}.model.${referenceEntity.name}> get${referenceEntity.pluralName}(${entity.PKClassName} pk, int start, int end) {
					return get${referenceEntity.pluralName}(pk, start, end, null);
				}

				/**
				 * Returns an ordered range of all the ${referenceEntity.pluralHumanName} associated with the ${entity.humanName}.
				 *
				 * <p>
				 * <#include "range_comment.ftl">
				 * </p>
				 *
				 * @param pk the primary key of the ${entity.humanName}
				 * @param start the lower bound of the range of ${entity.pluralHumanName}
				 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
				 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
				 * @return the ordered range of ${referenceEntity.pluralHumanName} associated with the ${entity.humanName}
				 */
				@Override
				public List<${referenceEntity.apiPackagePath}.model.${referenceEntity.name}> get${referenceEntity.pluralName}(${entity.PKClassName} pk, int start, int end, OrderByComparator<${referenceEntity.apiPackagePath}.model.${referenceEntity.name}> orderByComparator) {
					return ${entity.variableName}To${referenceEntity.name}TableMapper.getRightBaseModels(pk, start, end, orderByComparator);
				}
			</#if>

			/**
			 * Returns the number of ${referenceEntity.pluralHumanName} associated with the ${entity.humanName}.
			 *
			 * @param pk the primary key of the ${entity.humanName}
			 * @return the number of ${referenceEntity.pluralHumanName} associated with the ${entity.humanName}
			 */
			@Override
			public int get${referenceEntity.pluralName}Size(${entity.PKClassName} pk) {
				long[] pks = ${entity.variableName}To${referenceEntity.name}TableMapper.getRightPrimaryKeys(pk);

				return pks.length;
			}

			/**
			 * Returns <code>true</code> if the ${referenceEntity.humanName} is associated with the ${entity.humanName}.
			 *
			 * @param pk the primary key of the ${entity.humanName}
			 * @param ${referenceEntity.variableName}PK the primary key of the ${referenceEntity.humanName}
			 * @return <code>true</code> if the ${referenceEntity.humanName} is associated with the ${entity.humanName}; <code>false</code> otherwise
			 */
			@Override
			public boolean contains${referenceEntity.name}(${entity.PKClassName} pk, ${referenceEntity.PKClassName} ${referenceEntity.variableName}PK) {
				return ${entity.variableName}To${referenceEntity.name}TableMapper.containsTableMapping(pk, ${referenceEntity.variableName}PK);
			}

			/**
			 * Returns <code>true</code> if the ${entity.humanName} has any ${referenceEntity.pluralHumanName} associated with it.
			 *
			 * @param pk the primary key of the ${entity.humanName} to check for associations with ${referenceEntity.pluralHumanName}
			 * @return <code>true</code> if the ${entity.humanName} has any ${referenceEntity.pluralHumanName} associated with it; <code>false</code> otherwise
			 */
			@Override
			public boolean contains${referenceEntity.pluralName}(${entity.PKClassName} pk) {
				if (get${referenceEntity.pluralName}Size(pk)> 0) {
					return true;
				}
				else {
					return false;
				}
			}

			<#if entityColumn.isMappingManyToMany()>
				/**
				 * Adds an association between the ${entity.humanName} and the ${referenceEntity.humanName}. Also notifies the appropriate model listeners and clears the mapping table finder cache.
				 *
				 * @param pk the primary key of the ${entity.humanName}
				 * @param ${referenceEntity.variableName}PK the primary key of the ${referenceEntity.humanName}
				<#if serviceBuilder.isVersionGTE_7_4_0()>
				 * @return <code>true</code> if an association between the ${entity.humanName} and the ${referenceEntity.humanName} was added; <code>false</code> if they were already associated
				</#if>
				 */
				@Override
				<#if serviceBuilder.isVersionGTE_7_4_0()>
					public boolean add${referenceEntity.name}(${entity.PKClassName} pk, ${referenceEntity.PKClassName} ${referenceEntity.variableName}PK) {
				<#else>
					public void add${referenceEntity.name}(${entity.PKClassName} pk, ${referenceEntity.PKClassName} ${referenceEntity.variableName}PK) {
				</#if>

					${entity.name} ${entity.variableName} = fetchByPrimaryKey(pk);

					if (${entity.variableName} == null) {
						<#if serviceBuilder.isVersionGTE_7_4_0()>
							return ${entity.variableName}To${referenceEntity.name}TableMapper.addTableMapping(CompanyThreadLocal.getCompanyId(), pk, ${referenceEntity.variableName}PK);
						<#else>
							${entity.variableName}To${referenceEntity.name}TableMapper.addTableMapping(CompanyThreadLocal.getCompanyId(), pk, ${referenceEntity.variableName}PK);
						</#if>
					}
					else {
						<#if serviceBuilder.isVersionGTE_7_4_0()>
							return ${entity.variableName}To${referenceEntity.name}TableMapper.addTableMapping(${entity.variableName}.getCompanyId(), pk, ${referenceEntity.variableName}PK);
						<#else>
							${entity.variableName}To${referenceEntity.name}TableMapper.addTableMapping(${entity.variableName}.getCompanyId(), pk, ${referenceEntity.variableName}PK);
						</#if>
					}
				}

				/**
				 * Adds an association between the ${entity.humanName} and the ${referenceEntity.humanName}. Also notifies the appropriate model listeners and clears the mapping table finder cache.
				 *
				 * @param pk the primary key of the ${entity.humanName}
				 * @param ${referenceEntity.variableName} the ${referenceEntity.humanName}
				<#if serviceBuilder.isVersionGTE_7_4_0()>
				 * @return <code>true</code> if an association between the ${entity.humanName} and the ${referenceEntity.humanName} was added; <code>false</code> if they were already associated
				</#if>
				 */
				@Override
				<#if serviceBuilder.isVersionGTE_7_4_0()>
					public boolean add${referenceEntity.name}(${entity.PKClassName} pk, ${referenceEntity.apiPackagePath}.model.${referenceEntity.name} ${referenceEntity.variableName}) {
				<#else>
					public void add${referenceEntity.name}(${entity.PKClassName} pk, ${referenceEntity.apiPackagePath}.model.${referenceEntity.name} ${referenceEntity.variableName}) {
				</#if>

					${entity.name} ${entity.variableName} = fetchByPrimaryKey(pk);

					if (${entity.variableName} == null) {
						<#if serviceBuilder.isVersionGTE_7_4_0()>
							return ${entity.variableName}To${referenceEntity.name}TableMapper.addTableMapping(CompanyThreadLocal.getCompanyId(), pk, ${referenceEntity.variableName}.getPrimaryKey());
						<#else>
							${entity.variableName}To${referenceEntity.name}TableMapper.addTableMapping(CompanyThreadLocal.getCompanyId(), pk, ${referenceEntity.variableName}.getPrimaryKey());
						</#if>
					}
					else {
						<#if serviceBuilder.isVersionGTE_7_4_0()>
							return ${entity.variableName}To${referenceEntity.name}TableMapper.addTableMapping(${entity.variableName}.getCompanyId(), pk, ${referenceEntity.variableName}.getPrimaryKey());
						<#else>
							${entity.variableName}To${referenceEntity.name}TableMapper.addTableMapping(${entity.variableName}.getCompanyId(), pk, ${referenceEntity.variableName}.getPrimaryKey());
						</#if>
					}
				}

				/**
				 * Adds an association between the ${entity.humanName} and the ${referenceEntity.pluralHumanName}. Also notifies the appropriate model listeners and clears the mapping table finder cache.
				 *
				 * @param pk the primary key of the ${entity.humanName}
				 * @param ${referenceEntity.variableName}PKs the primary keys of the ${referenceEntity.pluralHumanName}
				<#if serviceBuilder.isVersionGTE_7_4_0()>
				 * @return <code>true</code> if at least one association between the ${entity.humanName} and the ${referenceEntity.pluralHumanName} was added; <code>false</code> if they were all already associated
				</#if>
				 */
				@Override
				<#if serviceBuilder.isVersionGTE_7_4_0()>
					public boolean add${referenceEntity.pluralName}(${entity.PKClassName} pk, ${referenceEntity.PKClassName}[] ${referenceEntity.variableName}PKs) {
				<#else>
					public void add${referenceEntity.pluralName}(${entity.PKClassName} pk, ${referenceEntity.PKClassName}[] ${referenceEntity.variableName}PKs) {
				</#if>

					long companyId = 0;

					${entity.name} ${entity.variableName} = fetchByPrimaryKey(pk);

					if (${entity.variableName} == null) {
						companyId = CompanyThreadLocal.getCompanyId();
					}
					else {
						companyId = ${entity.variableName}.getCompanyId();
					}

					<#if serviceBuilder.isVersionGTE_7_4_0()>
						long[] addedKeys = ${entity.variableName}To${referenceEntity.name}TableMapper.addTableMappings(companyId, pk, ${referenceEntity.variableName}PKs);

						if (addedKeys.length > 0) {
							return true;
						}

						return false;
					<#else>
						${entity.variableName}To${referenceEntity.name}TableMapper.addTableMappings(companyId, pk, ${referenceEntity.variableName}PKs);
					</#if>
				}

				/**
				 * Adds an association between the ${entity.humanName} and the ${referenceEntity.pluralHumanName}. Also notifies the appropriate model listeners and clears the mapping table finder cache.
				 *
				 * @param pk the primary key of the ${entity.humanName}
				 * @param ${referenceEntity.pluralVariableName} the ${referenceEntity.pluralHumanName}
				<#if serviceBuilder.isVersionGTE_7_4_0()>
				 * @return <code>true</code> if at least one association between the ${entity.humanName} and the ${referenceEntity.pluralHumanName} was added; <code>false</code> if they were all already associated
				</#if>
				 */
				@Override
				<#if serviceBuilder.isVersionGTE_7_4_0()>
					public boolean add${referenceEntity.pluralName}(${entity.PKClassName} pk, List<${referenceEntity.apiPackagePath}.model.${referenceEntity.name}> ${referenceEntity.pluralVariableName}) {
						return add${referenceEntity.pluralName}(pk, ListUtil.toLongArray(${referenceEntity.pluralVariableName}, ${referenceEntity.apiPackagePath}.model.${referenceEntity.name}.${textFormatter.format(textFormatter.format(referenceEntity.getPKVariableName(), 7), 0)}_ACCESSOR));
					}
				<#else>
					public void add${referenceEntity.pluralName}(${entity.PKClassName} pk, List<${referenceEntity.apiPackagePath}.model.${referenceEntity.name}> ${referenceEntity.pluralVariableName}) {
						add${referenceEntity.pluralName}(pk, ListUtil.toLongArray(${referenceEntity.pluralVariableName}, ${referenceEntity.apiPackagePath}.model.${referenceEntity.name}.${textFormatter.format(textFormatter.format(referenceEntity.getPKVariableName(), 7), 0)}_ACCESSOR));
					}
				</#if>

				/**
				 * Clears all associations between the ${entity.humanName} and its ${referenceEntity.pluralHumanName}. Also notifies the appropriate model listeners and clears the mapping table finder cache.
				 *
				 * @param pk the primary key of the ${entity.humanName} to clear the associated ${referenceEntity.pluralHumanName} from
				 */
				@Override
				public void clear${referenceEntity.pluralName}(${entity.PKClassName} pk) {
					${entity.variableName}To${referenceEntity.name}TableMapper.deleteLeftPrimaryKeyTableMappings(pk);
				}

				/**
				 * Removes the association between the ${entity.humanName} and the ${referenceEntity.humanName}. Also notifies the appropriate model listeners and clears the mapping table finder cache.
				 *
				 * @param pk the primary key of the ${entity.humanName}
				 * @param ${referenceEntity.variableName}PK the primary key of the ${referenceEntity.humanName}
				 */
				@Override
				public void remove${referenceEntity.name}(${entity.PKClassName} pk, ${referenceEntity.PKClassName} ${referenceEntity.variableName}PK) {
					${entity.variableName}To${referenceEntity.name}TableMapper.deleteTableMapping(pk, ${referenceEntity.variableName}PK);
				}

				/**
				 * Removes the association between the ${entity.humanName} and the ${referenceEntity.humanName}. Also notifies the appropriate model listeners and clears the mapping table finder cache.
				 *
				 * @param pk the primary key of the ${entity.humanName}
				 * @param ${referenceEntity.variableName} the ${referenceEntity.humanName}
				 */
				@Override
				public void remove${referenceEntity.name}(${entity.PKClassName} pk, ${referenceEntity.apiPackagePath}.model.${referenceEntity.name} ${referenceEntity.variableName}) {
					${entity.variableName}To${referenceEntity.name}TableMapper.deleteTableMapping(pk, ${referenceEntity.variableName}.getPrimaryKey());
				}

				/**
				 * Removes the association between the ${entity.humanName} and the ${referenceEntity.pluralHumanName}. Also notifies the appropriate model listeners and clears the mapping table finder cache.
				 *
				 * @param pk the primary key of the ${entity.humanName}
				 * @param ${referenceEntity.variableName}PKs the primary keys of the ${referenceEntity.pluralHumanName}
				 */
				@Override
				public void remove${referenceEntity.pluralName}(${entity.PKClassName} pk, ${referenceEntity.PKClassName}[] ${referenceEntity.variableName}PKs) {
					${entity.variableName}To${referenceEntity.name}TableMapper.deleteTableMappings(pk, ${referenceEntity.variableName}PKs);
				}

				/**
				 * Removes the association between the ${entity.humanName} and the ${referenceEntity.pluralHumanName}. Also notifies the appropriate model listeners and clears the mapping table finder cache.
				 *
				 * @param pk the primary key of the ${entity.humanName}
				 * @param ${referenceEntity.pluralVariableName} the ${referenceEntity.pluralHumanName}
				 */
				@Override
				public void remove${referenceEntity.pluralName}(${entity.PKClassName} pk, List<${referenceEntity.apiPackagePath}.model.${referenceEntity.name}> ${referenceEntity.pluralVariableName}) {
					remove${referenceEntity.pluralName}(pk, ListUtil.toLongArray(${referenceEntity.pluralVariableName}, ${referenceEntity.apiPackagePath}.model.${referenceEntity.name}.${textFormatter.format(textFormatter.format(referenceEntity.getPKVariableName(), 7), 0)}_ACCESSOR));
				}

				/**
				 * Sets the ${referenceEntity.pluralHumanName} associated with the ${entity.humanName}, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
				 *
				 * @param pk the primary key of the ${entity.humanName}
				 * @param ${referenceEntity.variableName}PKs the primary keys of the ${referenceEntity.pluralHumanName} to be associated with the ${entity.humanName}
				 */
				@Override
				public void set${referenceEntity.pluralName}(${entity.PKClassName} pk, ${referenceEntity.PKClassName}[] ${referenceEntity.variableName}PKs) {
					Set<Long> new${referenceEntity.name}PKsSet = SetUtil.fromArray(${referenceEntity.variableName}PKs);
					Set<Long> old${referenceEntity.name}PKsSet = SetUtil.fromArray(${entity.variableName}To${referenceEntity.name}TableMapper.getRightPrimaryKeys(pk));

					Set<Long> remove${referenceEntity.name}PKsSet = new HashSet<Long>(old${referenceEntity.name}PKsSet);

					remove${referenceEntity.name}PKsSet.removeAll(new${referenceEntity.name}PKsSet);

					${entity.variableName}To${referenceEntity.name}TableMapper.deleteTableMappings(pk, ArrayUtil.toLongArray(remove${referenceEntity.name}PKsSet));

					new${referenceEntity.name}PKsSet.removeAll(old${referenceEntity.name}PKsSet);

					long companyId = 0;

					${entity.name} ${entity.variableName} = fetchByPrimaryKey(pk);

					if (${entity.variableName} == null) {
						companyId = CompanyThreadLocal.getCompanyId();
					}
					else {
						companyId = ${entity.variableName}.getCompanyId();
					}

					${entity.variableName}To${referenceEntity.name}TableMapper.addTableMappings(companyId, pk, ArrayUtil.toLongArray(new${referenceEntity.name}PKsSet));
				}

				/**
				 * Sets the ${referenceEntity.pluralHumanName} associated with the ${entity.humanName}, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
				 *
				 * @param pk the primary key of the ${entity.humanName}
				 * @param ${referenceEntity.pluralVariableName} the ${referenceEntity.pluralHumanName} to be associated with the ${entity.humanName}
				 */
				@Override
				public void set${referenceEntity.pluralName}(${entity.PKClassName} pk, List<${referenceEntity.apiPackagePath}.model.${referenceEntity.name}> ${referenceEntity.pluralVariableName}) {
					try {
						${referenceEntity.PKClassName}[] ${referenceEntity.variableName}PKs = new ${referenceEntity.PKClassName}[${referenceEntity.pluralVariableName}.size()];

						for (int i = 0; i < ${referenceEntity.pluralVariableName}.size(); i++) {
							${referenceEntity.apiPackagePath}.model.${referenceEntity.name} ${referenceEntity.variableName} = ${referenceEntity.pluralVariableName}.get(i);

							${referenceEntity.variableName}PKs[i] = ${referenceEntity.variableName}.getPrimaryKey();
						}

						set${referenceEntity.pluralName}(pk, ${referenceEntity.variableName}PKs);
					}
					catch (Exception exception) {
						throw processException(exception);
					}
				}
			</#if>
		</#if>
	</#list>

	<#if entity.badEntityColumns?size != 0>
		@Override
		public Set<String> getBadColumnNames() {
			return _badColumnNames;
		}
	</#if>

	<#if entity.hasCompoundPK()>
		@Override
		public Set<String> getCompoundPKColumnNames() {
			return _compoundPKColumnNames;
		}
	</#if>

	<#if !serviceBuilder.isVersionLTE_7_1_0()>
		@Override
		protected EntityCache getEntityCache() {
			<#if serviceBuilder.isVersionGTE_7_3_0() && !entity.isCacheEnabled()>
				return dummyEntityCache;
			<#elseif osgiModule>
				return entityCache;
			<#else>
				return EntityCacheUtil.getEntityCache();
			</#if>
		}

		@Override
		protected String getPKDBName() {
			return "${entity.PKDBName}";
		}
		<#if serviceBuilder.isVersionGTE_7_4_0() && !stringUtil.equals(entity.PKDBName, entity.PKVariableName)>

			@Override
			protected String getPKFieldName() {
				return "${entity.PKVariableName}";
			}
		</#if>

		@Override
		protected String getSelectSQL() {
			return _SQL_SELECT_${entity.alias?upper_case};
		}
	</#if>

	<#if entity.isChangeTrackingEnabled()>
		@Override
		public Set<String> getCTColumnNames(CTColumnResolutionType ctColumnResolutionType) {
			return _ctColumnNamesMap.getOrDefault(ctColumnResolutionType, Collections.emptySet());
		}

		@Override
		public List<String> getMappingTableNames() {
			return _mappingTableNames;
		}

		@Override
		public Map<String, Integer> getTableColumnsMap() {
			return ${entity.name}ModelImpl.TABLE_COLUMNS_MAP;
		}

		@Override
		public String getTableName() {
			return "${entity.table}";
		}

		@Override
		public List<String[]> getUniqueIndexColumnNames() {
			return _uniqueIndexColumnNames;
		}

		private static final Map<CTColumnResolutionType, Set<String>> _ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(CTColumnResolutionType.class);
		private static final List<String> _mappingTableNames = new ArrayList<String>();
		private static final List<String[]> _uniqueIndexColumnNames = new ArrayList<String[]>();

		static {
			<#list entity.getCTColumnResolutionTypeNames() as ctColumnResolutionTypeName>
				<#if !stringUtil.equals(ctColumnResolutionTypeName, "Pk")>
					Set<String> ct${ctColumnResolutionTypeName}ColumnNames = new HashSet<String>();
				</#if>
			</#list>

			<#list entity.entityColumns as entityColumn>
				<#if !stringUtil.equals(entityColumn.getCTColumnResolutionTypeName(), "Pk")>
					ct${entityColumn.getCTColumnResolutionTypeName()}ColumnNames.add("${entityColumn.DBName}");
				</#if>
			</#list>

			<#list entity.getCTColumnResolutionTypeNames() as ctColumnResolutionTypeName>
				<#if stringUtil.equals(ctColumnResolutionTypeName, "Pk")>
					_ctColumnNamesMap.put(CTColumnResolutionType.${stringUtil.toUpperCase(ctColumnResolutionTypeName)}, Collections.singleton("${entity.PKDBName}"));
				<#else>
					_ctColumnNamesMap.put(CTColumnResolutionType.${stringUtil.toUpperCase(ctColumnResolutionTypeName)}, ct${ctColumnResolutionTypeName}ColumnNames);
				</#if>
			</#list>

			<#list entity.entityColumns as entityColumn>
				<#if entityColumn.isCollection() && entityColumn.isMappingManyToMany()>
					_mappingTableNames.add("${entityColumn.mappingTableName}");
				</#if>
			</#list>

			<#list entity.entityFinders as entityFinder>
				<#if entityFinder.isUnique()>
					<#assign entityColumns = entityFinder.entityColumns />

					_uniqueIndexColumnNames.add(
						new String[] {
							<#list entityColumns as entityColumn>
								"${entityColumn.DBName}"

								<#if entityColumn_has_next>
									,
								</#if>
							</#list>
						});
				</#if>
			</#list>
		}
	<#else>
		@Override
		protected Map<String, Integer> getTableColumnsMap() {
			return ${entity.name}ModelImpl.TABLE_COLUMNS_MAP;
		}
	</#if>

	<#if entity.isHierarchicalTree()>
		@Override
		public long countAncestors(${entity.name} ${entity.variableName}) {
			Object[] finderArgs = new Object[] {${entity.variableName}.get${scopeEntityColumn.methodName}(), ${entity.variableName}.getLeft${pkEntityColumn.methodName}(), ${entity.variableName}.getRight${pkEntityColumn.methodName}()};

			Long count = (Long)${finderCache}.getResult(_finderPathWithPaginationCountAncestors, finderArgs, this);

			if (count == null) {
				try {
					count = nestedSetsTreeManager.countAncestors(${entity.variableName});

					${finderCache}.putResult(_finderPathWithPaginationCountAncestors, finderArgs, count);
				}
				catch (SystemException systemException) {
					<#if serviceBuilder.isVersionLTE_7_2_0()>
						${finderCache}.removeResult(_finderPathWithPaginationCountAncestors, finderArgs);
					</#if>

					throw systemException;
				}
			}

			return count.intValue();
		}

		@Override
		public long countDescendants(${entity.name} ${entity.variableName}) {
			Object[] finderArgs = new Object[] {${entity.variableName}.get${scopeEntityColumn.methodName}(), ${entity.variableName}.getLeft${pkEntityColumn.methodName}(), ${entity.variableName}.getRight${pkEntityColumn.methodName}()};

			Long count = (Long)${finderCache}.getResult(_finderPathWithPaginationCountDescendants, finderArgs, this);

			if (count == null) {
				try {
					count = nestedSetsTreeManager.countDescendants(${entity.variableName});

					${finderCache}.putResult(_finderPathWithPaginationCountDescendants, finderArgs, count);
				}
				catch (SystemException systemException) {
					<#if serviceBuilder.isVersionLTE_7_2_0()>
						${finderCache}.removeResult(_finderPathWithPaginationCountDescendants, finderArgs);
					</#if>

					throw systemException;
				}
			}

			return count.intValue();
		}

		@Override
		public List<${entity.name}> getAncestors(${entity.name} ${entity.variableName}) {
			Object[] finderArgs = new Object[] {${entity.variableName}.get${scopeEntityColumn.methodName}(), ${entity.variableName}.getLeft${pkEntityColumn.methodName}(), ${entity.variableName}.getRight${pkEntityColumn.methodName}()};

			List<${entity.name}> list = (List<${entity.name}>)${finderCache}.getResult(_finderPathWithPaginationGetAncestors, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (${entity.name} temp${entity.name} : list) {
					if ((${entity.variableName}.getLeft${pkEntityColumn.methodName}() < temp${entity.name}.getLeft${pkEntityColumn.methodName}()) || (${entity.variableName}.getRight${pkEntityColumn.methodName}() > temp${entity.name}.getRight${pkEntityColumn.methodName}())) {
						list = null;

						break;
					}
				}
			}

			if (list == null) {
				try {
					list = nestedSetsTreeManager.getAncestors(${entity.variableName});

					cacheResult(list);

					${finderCache}.putResult(_finderPathWithPaginationGetAncestors, finderArgs, list);
				}
				catch (SystemException systemException) {
					<#if serviceBuilder.isVersionLTE_7_2_0()>
						${finderCache}.removeResult(_finderPathWithPaginationGetAncestors, finderArgs);
					</#if>

					throw systemException;
				}
			}

			return list;
		}

		@Override
		public List<${entity.name}> getDescendants(${entity.name} ${entity.variableName}) {
			Object[] finderArgs = new Object[] {${entity.variableName}.get${scopeEntityColumn.methodName}(), ${entity.variableName}.getLeft${pkEntityColumn.methodName}(), ${entity.variableName}.getRight${pkEntityColumn.methodName}()};

			List<${entity.name}> list = (List<${entity.name}>)${finderCache}.getResult(_finderPathWithPaginationGetDescendants, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (${entity.name} temp${entity.name} : list) {
					if ((${entity.variableName}.getLeft${pkEntityColumn.methodName}() > temp${entity.name}.getLeft${pkEntityColumn.methodName}()) || (${entity.variableName}.getRight${pkEntityColumn.methodName}() < temp${entity.name}.getRight${pkEntityColumn.methodName}())) {
						list = null;

						break;
					}
				}
			}

			if (list == null) {
				try {
					list = nestedSetsTreeManager.getDescendants(${entity.variableName});

					cacheResult(list);

					${finderCache}.putResult(_finderPathWithPaginationGetDescendants, finderArgs, list);
				}
				catch (SystemException systemException) {
					<#if serviceBuilder.isVersionLTE_7_2_0()>
						${finderCache}.removeResult(_finderPathWithPaginationGetDescendants, finderArgs);
					</#if>

					throw systemException;
				}
			}

			return list;
		}

		/**
		 * Rebuilds the ${entity.pluralHumanName} tree for the scope using the modified pre-order tree traversal algorithm.
		 *
		 * <p>
		 * Only call this method if the tree has become stale through operations other than normal CRUD. Under normal circumstances the tree is automatically rebuilt whenver necessary.
		 * </p>
		 *
		 * @param ${scopeEntityColumn.name} the ID of the scope
		 * @param force whether to force the rebuild even if the tree is not stale
		 */
		@Override
		public void rebuildTree(long ${scopeEntityColumn.name}, boolean force) {
			if (!rebuildTreeEnabled) {
				return;
			}

			if (force || (countOrphanTreeNodes(${scopeEntityColumn.name}) > 0)) {
				Session session = null;

				try {
					session = openSession();

					if (session.isDirty()) {
						session.flush();
					}

					SQLQuery selectSQLQuery = session.createSQLQuery("SELECT ${pkEntityColumn.DBName} FROM ${entity.table} WHERE ${scopeEntityColumn.DBName} = ? AND parent${pkEntityColumn.methodName} = ? ORDER BY ${pkEntityColumn.DBName} ASC");

					selectSQLQuery.addScalar("${pkEntityColumn.name}", com.liferay.portal.kernel.dao.orm.Type.LONG);

					SQLQuery updateSQLQuery = session.createSQLQuery("UPDATE ${entity.table} SET left${pkEntityColumn.methodName} = ?, right${pkEntityColumn.methodName} = ? WHERE ${pkEntityColumn.DBName} = ?");

					rebuildTree(session, selectSQLQuery, updateSQLQuery, ${scopeEntityColumn.name}, 0, 0);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}

				clearCache();
			}
		}

		@Override
		public void setRebuildTreeEnabled(boolean rebuildTreeEnabled) {
			this.rebuildTreeEnabled = rebuildTreeEnabled;
		}

		protected long countOrphanTreeNodes(long ${scopeEntityColumn.name}) {
			Session session = null;

			try {
				session = openSession();

				SQLQuery sqlQuery = session.createSynchronizedSQLQuery("SELECT COUNT(*) AS COUNT_VALUE FROM ${entity.table} WHERE ${scopeEntityColumn.DBName} = ? AND (left${pkEntityColumn.methodName} = 0 OR left${pkEntityColumn.methodName} IS NULL OR right${pkEntityColumn.methodName} = 0 OR right${pkEntityColumn.methodName} IS NULL)");

				sqlQuery.addScalar(COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

				QueryPos queryPos = QueryPos.getInstance(sqlQuery);

				queryPos.add(${scopeEntityColumn.name});

				return (Long)sqlQuery.uniqueResult();
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		protected long rebuildTree(Session session, SQLQuery selectSQLQuery, SQLQuery updateSQLQuery, long ${scopeEntityColumn.name}, long parent${pkEntityColumn.methodName}, long left${pkEntityColumn.methodName}) {
			long right${pkEntityColumn.methodName} = left${pkEntityColumn.methodName} + 1;

			QueryPos queryPos = QueryPos.getInstance(selectSQLQuery);

			queryPos.add(${scopeEntityColumn.name});
			queryPos.add(parent${pkEntityColumn.methodName});

			List<Long> ${pkEntityColumn.pluralName} = selectSQLQuery.list();

			for (long ${pkEntityColumn.name} : ${pkEntityColumn.pluralName}) {
				right${pkEntityColumn.methodName} = rebuildTree(session, selectSQLQuery, updateSQLQuery, ${scopeEntityColumn.name}, ${pkEntityColumn.name}, right${pkEntityColumn.methodName});
			}

			if (parent${pkEntityColumn.methodName} > 0) {
				queryPos = QueryPos.getInstance(updateSQLQuery);

				queryPos.add(left${pkEntityColumn.methodName});
				queryPos.add(right${pkEntityColumn.methodName});
				queryPos.add(parent${pkEntityColumn.methodName});

				updateSQLQuery.executeUpdate();
			}

			return right${pkEntityColumn.methodName} + 1;
		}
	</#if>

	/**
	 * Initializes the ${entity.humanName} persistence.
	 */
	<#if dependencyInjectorDS>
		@Activate
		<#if serviceBuilder.isVersionGTE_7_4_0()>
			public void activate() {
		<#elseif serviceBuilder.isVersionGTE_7_3_0()>
			public void activate(BundleContext bundleContext) {
				_bundleContext = bundleContext;
		<#else>
			public void activate() {
				${entity.name}ModelImpl.setEntityCacheEnabled(entityCacheEnabled);
				${entity.name}ModelImpl.setFinderCacheEnabled(finderCacheEnabled);
		</#if>
	<#else>
		public void afterPropertiesSet() {
	</#if>
		<#if serviceBuilder.isVersionGTE_7_3_0() && serviceBuilder.isVersionLTE_7_3_0()>
			<#if osgiModule>
				<#if !dependencyInjectorDS>
					Bundle bundle = FrameworkUtil.getBundle(${entity.name}PersistenceImpl.class);

					_bundleContext = bundle.getBundleContext();
				</#if>

				_argumentsResolverServiceRegistration = _bundleContext.registerService(
					ArgumentsResolver.class, new ${entity.name}ModelArgumentsResolver(),
						MapUtil.singletonDictionary("model.class.name", ${entity.name}.class.getName()));
			<#else>
				Registry registry = RegistryUtil.getRegistry();

				_argumentsResolverServiceRegistration = registry.registerService(
					ArgumentsResolver.class, new ${entity.name}ModelArgumentsResolver(),
						HashMapBuilder.<String, Object>put(
							"model.class.name", ${entity.name}.class.getName()
						).build());
			</#if>
		</#if>

		<#if !serviceBuilder.isVersionGTE_7_4_0()>
			_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(PropsUtil.get(
				<#if serviceBuilder.isVersionGTE_7_1_0()>
					PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD
				<#else>
					"value.object.finder.cache.list.threshold"
				</#if>
			));
		</#if>

		<#list entity.entityColumns as entityColumn>
			<#if entityColumn.isCollection() && entityColumn.isMappingManyToMany()>
				<#assign
					referenceEntity = serviceBuilder.getEntity(entityColumn.entityName)

					entityMapping = serviceBuilder.getEntityMapping(entityColumn.mappingTableName)

					companyEntity = serviceBuilder.getEntity(entityMapping.getEntityName(0))
				/>

				<#if dependencyInjectorDS>
					${entity.variableName}To${referenceEntity.name}TableMapper = TableMapperFactory.getTableMapper("${entityColumn.mappingTableName}#${entity.PKDBName}", "${entityColumn.mappingTableName}", "${companyEntity.PKDBName}", "${entity.PKDBName}", "${referenceEntity.PKDBName}", this, ${referenceEntity.name}.class);
				<#else>
					${entity.variableName}To${referenceEntity.name}TableMapper = TableMapperFactory.getTableMapper("${entityColumn.mappingTableName}", "${companyEntity.PKDBName}", "${entity.PKDBName}", "${referenceEntity.PKDBName}", this, ${referenceEntity.variableName}Persistence);
				</#if>
			</#if>
		</#list>

		<#if !serviceBuilder.isVersionGTE_7_4_0()>
			_finderPathWithPaginationFindAll =
				<#if serviceBuilder.isVersionGTE_7_3_0()>
					_createFinderPath(
				<#else>
					new FinderPath(
						${entityCacheEnabled},
						${finderCacheEnabled},
						${entity.name}Impl.class,
				</#if>
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
				"findAll", new String[0]
				<#if serviceBuilder.isVersionGTE_7_3_0()>
					, new String[0], true
				</#if>
				);

			_finderPathWithoutPaginationFindAll =
				<#if serviceBuilder.isVersionGTE_7_3_0()>
					_createFinderPath(
				<#else>
					new FinderPath(
						${entityCacheEnabled},
						${finderCacheEnabled},
						${entity.name}Impl.class,
				</#if>
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
				"findAll", new String[0]
				<#if serviceBuilder.isVersionGTE_7_3_0()>
					, new String[0], true
				</#if>
				);
		</#if>

		<#if !serviceBuilder.isVersionGTE_7_4_0()>
			_finderPathCountAll =
				<#if serviceBuilder.isVersionGTE_7_3_0()>
					_createFinderPath(
				<#else>
					new FinderPath(
						${entityCacheEnabled},
						${finderCacheEnabled},
						Long.class,
				</#if>
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
				"countAll", new String[0]
				<#if serviceBuilder.isVersionGTE_7_3_0()>
					, new String[0], false
				</#if>
				);
		</#if>

		<#if entity.isHierarchicalTree()>
			_finderPathWithPaginationCountAncestors =
				<#if serviceBuilder.isVersionGTE_7_4_0()>
					new FinderPath(
				<#elseif serviceBuilder.isVersionGTE_7_3_0()>
					_createFinderPath(
				<#else>
					new FinderPath(
						${entityCacheEnabled},
						${finderCacheEnabled},
						Long.class,
				</#if>
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
				"countAncestors",
				new String[] {Long.class.getName(), Long.class.getName(), Long.class.getName()}
				<#if serviceBuilder.isVersionGTE_7_3_0()>
					, new String[]{"${scopeEntityColumn.DBName}", "left${pkEntityColumn.methodName}", "right${pkEntityColumn.methodName}"}, false
				</#if>
				);

			_finderPathWithPaginationCountDescendants =
				<#if serviceBuilder.isVersionGTE_7_4_0()>
					new FinderPath(
				<#elseif serviceBuilder.isVersionGTE_7_3_0()>
					_createFinderPath(
				<#else>
					new FinderPath(
						${entityCacheEnabled},
						${finderCacheEnabled},
						Long.class,
				</#if>
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
				"countDescendants",
				new String[] {Long.class.getName(), Long.class.getName(), Long.class.getName()}
				<#if serviceBuilder.isVersionGTE_7_3_0()>
					, new String[]{"${scopeEntityColumn.DBName}", "left${pkEntityColumn.methodName}", "right${pkEntityColumn.methodName}"}, false
				</#if>
				);

			_finderPathWithPaginationGetAncestors =
				<#if serviceBuilder.isVersionGTE_7_4_0()>
					new FinderPath(
				<#elseif serviceBuilder.isVersionGTE_7_3_0()>
					_createFinderPath(
				<#else>
					new FinderPath(
						${entityCacheEnabled},
						${finderCacheEnabled},
						${entity.name}Impl.class,
				</#if>
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
				"getAncestors",
				new String[] {Long.class.getName(), Long.class.getName(), Long.class.getName()}
				<#if serviceBuilder.isVersionGTE_7_3_0()>
					, new String[]{"${scopeEntityColumn.DBName}", "left${pkEntityColumn.methodName}", "right${pkEntityColumn.methodName}"}, true
				</#if>
				);

			_finderPathWithPaginationGetDescendants =
				<#if serviceBuilder.isVersionGTE_7_4_0()>
					new FinderPath(
				<#elseif serviceBuilder.isVersionGTE_7_3_0()>
					_createFinderPath(
				<#else>
					new FinderPath(
						${entityCacheEnabled},
						${finderCacheEnabled},
						${entity.name}Impl.class,
				</#if>
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
				"getDescendants",
				new String[] {Long.class.getName(), Long.class.getName(), Long.class.getName()}
				<#if serviceBuilder.isVersionGTE_7_3_0()>
					, new String[]{"${scopeEntityColumn.DBName}", "left${pkEntityColumn.methodName}", "right${pkEntityColumn.methodName}"}, true
				</#if>
				);
		</#if>

		<#list entity.entityFinders as entityFinder>
			<#assign
				entityColumns = entityFinder.entityColumns

				caseInsensitiveBitmask = 0
				convertNullBitmask = 0
				normalizationBit = 1
			/>

			<#list entityColumns as entityColumn>
				<#if stringUtil.equals(entityColumn.type, "String") && !entityColumn.isCaseSensitive()>
					<#assign caseInsensitiveBitmask = caseInsensitiveBitmask + normalizationBit />
				</#if>
				<#if stringUtil.equals(entityColumn.type, "String") && entityColumn.isConvertNull()>
					<#assign convertNullBitmask = convertNullBitmask + normalizationBit />
				</#if>
				<#assign normalizationBit = normalizationBit * 2 />
			</#list>

			<#if !entityFinder.collectionPersistenceFinderEnabled>
				<#if entityFinder.isCollection()>
					_finderPathWithPaginationFindBy${entityFinder.name} =
						<#if serviceBuilder.isVersionGTE_7_4_0()>
							new FinderPath(
						<#elseif serviceBuilder.isVersionGTE_7_3_0()>
							_createFinderPath(
						<#else>
							new FinderPath(
								${entityCacheEnabled},
								${finderCacheEnabled},
								${entity.name}Impl.class,
						</#if>
						FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
						"findBy${entityFinder.name}",
						new String[] {
							<#list entityColumns as entityColumn>
								${serviceBuilder.getPrimitiveObj("${entityColumn.type}")}.class.getName(),
							</#list>

							Integer.class.getName(), Integer.class.getName(), OrderByComparator.class.getName()
						}
						<#if serviceBuilder.isVersionGTE_7_3_0()>
							,
							new String[] {
								<#list entityColumns as entityColumn>
									"${entityColumn.DBName}"

									<#if entityColumn_has_next>
										,
									</#if>
								</#list>
								},
							true
						</#if>
						);

					<#if !entityFinder.hasCustomComparator()>
						_finderPathWithoutPaginationFindBy${entityFinder.name} =
							<#if serviceBuilder.isVersionGTE_7_4_0()>
								new FinderPath(
							<#elseif serviceBuilder.isVersionGTE_7_3_0()>
								_createFinderPath(
							<#else>
								new FinderPath(
									${entityCacheEnabled},
									${finderCacheEnabled},
									${entity.name}Impl.class,
							</#if>
							FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
							"findBy${entityFinder.name}",
							new String[] {
								<#list entityColumns as entityColumn>
									${serviceBuilder.getPrimitiveObj("${entityColumn.type}")}.class.getName()

									<#if entityColumn_has_next>
										,
									</#if>
								</#list>
							}
							<#if serviceBuilder.isVersionGTE_7_3_0()>
								,
								new String[] {
									<#list entityColumns as entityColumn>
											"${entityColumn.DBName}"

										<#if entityColumn_has_next>
											,
										</#if>
									</#list>
									},
								<#if serviceBuilder.isVersionGTE_7_4_0() && ((caseInsensitiveBitmask > 0) || (convertNullBitmask > 0))>
									${caseInsensitiveBitmask}, ${convertNullBitmask},
									true,
									null
								<#else>
									true
								</#if>
							<#elseif columnBitmaskEnabled>
								,

								<#list entityColumns as entityColumn>
									<#if serviceBuilder.isVersionGTE_7_3_0()>
										${entity.name}ModelImpl.getColumnBitmask("${entityColumn.DBName}")
									<#else>
										${entity.name}ModelImpl.${entityColumn.name?upper_case}_COLUMN_BITMASK
									</#if>

									<#if entityColumn_has_next>
										|
									</#if>
								</#list>

								<#if entity.entityOrder??>
									<#list entity.entityOrder.entityColumns as entityColumn>
										<#if !entityColumns?seq_contains(entityColumn) && !entity.PKEntityColumns?seq_contains(entityColumn)>
											| ${entity.name}ModelImpl.${entityColumn.name?upper_case}_COLUMN_BITMASK
										</#if>
									</#list>
								</#if>
							</#if>

							);
					</#if>
				</#if>

				<#if (!entityFinder.isCollection() || entityFinder.isUnique()) && !entityFinder.uniquePersistenceFinderEnabled>
					_finderPathFetchBy${entityFinder.name} =
						<#if serviceBuilder.isVersionGTE_7_4_0()>
							createUniqueFinderPath(
						<#elseif serviceBuilder.isVersionGTE_7_3_0()>
							_createFinderPath(
						<#else>
							new FinderPath(
								${entityCacheEnabled},
								${finderCacheEnabled},
								${entity.name}Impl.class,
						</#if>
						FINDER_CLASS_NAME_ENTITY,
						"fetchBy${entityFinder.name}",
						new String[] {
							<#list entityColumns as entityColumn>
								${serviceBuilder.getPrimitiveObj("${entityColumn.type}")}.class.getName()

								<#if entityColumn_has_next>
									,
								</#if>
							</#list>
						}
						<#if serviceBuilder.isVersionGTE_7_3_0()>
							,
							new String[] {
								<#list entityColumns as entityColumn>
									"${entityColumn.DBName}"

									<#if entityColumn_has_next>
										,
									</#if>
								</#list>
								}
							<#if serviceBuilder.isVersionGTE_7_4_0()>
								,
								${caseInsensitiveBitmask}, ${convertNullBitmask},
								<#if entityFinder.isPretouch()>true<#else>false</#if>

								<#list entityColumns as entityColumn>
									,
									<#if stringUtil.equals(entityColumn.type, "String") && !entityColumn.isCaseSensitive()>
										convertCaseFunction(${entity.name}::get${entityColumn.methodName})
									<#elseif stringUtil.equals(entityColumn.type, "String") && entityColumn.isConvertNull()>
										convertNullFunction(${entity.name}::get${entityColumn.methodName})
									<#elseif stringUtil.equals(entityColumn.type, "Date")>
										convertDateFunction(${entity.name}::get${entityColumn.methodName})
									<#else>
										${entity.name}::<#if stringUtil.equals(entityColumn.type, "boolean")>is<#else>get</#if>${entityColumn.methodName}
									</#if>
								</#list>
							<#else>
								,
								true
							</#if>
						<#elseif columnBitmaskEnabled>
							,

							<#list entityColumns as entityColumn>
								${entity.name}ModelImpl.${entityColumn.name?upper_case}_COLUMN_BITMASK

								<#if entityColumn_has_next>
									|
								</#if>
							</#list>
						</#if>

						);

					<#if !serviceBuilder.isVersionGTE_7_4_0() && entityFinder.isPretouch()>
						_finderPathFetchBy${entityFinder.name}.touch();
					</#if>
				</#if>

				<#if !entityFinder.hasCustomComparator() && (entityFinder.isCollection() || serviceBuilder.isVersionLTE_7_3_0())>
					_finderPathCountBy${entityFinder.name} =
						<#if serviceBuilder.isVersionGTE_7_4_0()>
							new FinderPath(
						<#elseif serviceBuilder.isVersionGTE_7_3_0()>
							_createFinderPath(
						<#else>
							new FinderPath(
								${entityCacheEnabled},
								${finderCacheEnabled},
								Long.class,
						</#if>
						<#if serviceBuilder.isVersionGTE_7_4_0() && entityFinder.hasArrayableOperator()>
							FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
						<#else>
							FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
						</#if>
						"countBy${entityFinder.name}",
						new String[] {
							<#list entityColumns as entityColumn>
								${serviceBuilder.getPrimitiveObj("${entityColumn.type}")}.class.getName()

								<#if entityColumn_has_next>
									,
								</#if>
							</#list>
						}
						<#if serviceBuilder.isVersionGTE_7_3_0()>
							,
							new String[] {
							<#list entityColumns as entityColumn>
								"${entityColumn.DBName}"

								<#if entityColumn_has_next>
									,
								</#if>
							</#list>
							},
							<#if serviceBuilder.isVersionGTE_7_4_0() && ((caseInsensitiveBitmask > 0) || (convertNullBitmask > 0))>
								${caseInsensitiveBitmask}, ${convertNullBitmask},
								false,
								null
							<#else>
								false
							</#if>
						</#if>
						);
				</#if>

				<#if entityFinder.hasCustomComparator() || entityFinder.hasArrayableOperator()>
					_finderPathWithPaginationCountBy${entityFinder.name} =
						<#if serviceBuilder.isVersionGTE_7_4_0()>
							new FinderPath(
						<#elseif serviceBuilder.isVersionGTE_7_3_0()>
							_createFinderPath(
						<#else>
							new FinderPath(
								${entityCacheEnabled},
								${finderCacheEnabled},
								Long.class,
						</#if>
						FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
						"countBy${entityFinder.name}",
						new String[] {
							<#list entityColumns as entityColumn>
								${serviceBuilder.getPrimitiveObj("${entityColumn.type}")}.class.getName()

								<#if entityColumn_has_next>
									,
								</#if>
							</#list>
						}
						<#if serviceBuilder.isVersionGTE_7_3_0()>
							,
							new String[] {
							<#list entityColumns as entityColumn>
								"${entityColumn.DBName}"

								<#if entityColumn_has_next>
									,
								</#if>
							</#list>
							},
							false
						</#if>
						);
				</#if>
			</#if>

			<#if entityFinder.collectionPersistenceFinderEnabled>
				<#assign filterEnabled = entity.isPermissionCheckEnabled(entityFinder) />

				_collectionPersistenceFinderBy${entityFinder.name} =
					<#if filterEnabled>
						new FilterCollectionPersistenceFinder<>(
					<#else>
						new CollectionPersistenceFinder<>(
					</#if>
						this,
						new FinderPath(
							FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
							"findBy${entityFinder.name}",
							new String[] {
								<#list entityColumns as entityColumn>
									${serviceBuilder.getPrimitiveObj("${entityColumn.type}")}.class.getName(),
								</#list>

								Integer.class.getName(), Integer.class.getName(), OrderByComparator.class.getName()
							},
							new String[] {
								<#list entityColumns as entityColumn>
									"${entityColumn.DBName}"

									<#if entityColumn_has_next>
										,
									</#if>
								</#list>
								},
							true),
						<#if !entityFinder.hasCustomComparator()>
							new FinderPath(
								FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
								"findBy${entityFinder.name}",
								new String[] {
									<#list entityColumns as entityColumn>
										${serviceBuilder.getPrimitiveObj("${entityColumn.type}")}.class.getName()

										<#if entityColumn_has_next>
											,
										</#if>
									</#list>
								},
								new String[] {
									<#list entityColumns as entityColumn>
										"${entityColumn.DBName}"

										<#if entityColumn_has_next>
											,
										</#if>
									</#list>
									},
								<#if (caseInsensitiveBitmask > 0) || (convertNullBitmask > 0)>
									${caseInsensitiveBitmask}, ${convertNullBitmask},
									true,
									null
								<#else>
									true
								</#if>
								),
						<#else>
							null,
						</#if>
						<#if !entityFinder.hasCustomComparator()>
							new FinderPath(
								<#if entityFinder.hasArrayableOperator()>
									FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
								<#else>
									FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
								</#if>
								"countBy${entityFinder.name}",
								new String[] {
									<#list entityColumns as entityColumn>
										${serviceBuilder.getPrimitiveObj("${entityColumn.type}")}.class.getName()

										<#if entityColumn_has_next>
											,
										</#if>
									</#list>
								},
								new String[] {
									<#list entityColumns as entityColumn>
										"${entityColumn.DBName}"

										<#if entityColumn_has_next>
											,
										</#if>
									</#list>
									},
								<#if (caseInsensitiveBitmask > 0) || (convertNullBitmask > 0)>
									${caseInsensitiveBitmask}, ${convertNullBitmask},
									false,
									null
								<#else>
									false
								</#if>
								),
						<#else>
							new FinderPath(
								FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
								"countBy${entityFinder.name}",
								new String[] {
									<#list entityColumns as entityColumn>
										${serviceBuilder.getPrimitiveObj("${entityColumn.type}")}.class.getName()

										<#if entityColumn_has_next>
											,
										</#if>
									</#list>
								},
								new String[] {
									<#list entityColumns as entityColumn>
										"${entityColumn.DBName}"

										<#if entityColumn_has_next>
											,
										</#if>
									</#list>
									},
								false),
						</#if>
						_SQL_SELECT_${entity.alias?upper_case}_WHERE,
						_SQL_COUNT_${entity.alias?upper_case}_WHERE,
						${entity.name}ModelImpl.ORDER_BY_JPQL,
						_ENTITY_ALIAS_PREFIX,
						"${entityFinder.where!}",
						"${entityFinder.DBWhere!}",
						<#list entityColumns as entityColumn>
							<#if entity.hasCompoundPK() && entityColumn.isPrimary()>
								<#assign columnName = "id." + entityColumn.name />
							<#else>
								<#assign columnName = entityColumn.name />
							</#if>

							<#if entityColumn.hasArrayableOperator()>
								new ArrayableFinderColumn<>(
									"${entity.alias}.",
									"${columnName}",
									<#if columnName != entityColumn.DBName>
										"${entityColumn.DBName}",
									</#if>
									${entityColumn.finderColumnTypeName},
									"${entityColumn.comparator}",
									${entityColumn.isArrayableAndOperator()?c},
									${entityColumn.isCaseSensitive()?c},
									${entityColumn.isConvertNull()?c},
									<#if stringUtil.equals(entityColumn.type, "boolean")>
										${entity.name}::is${entityColumn.methodName}
									<#else>
										${entity.name}::get${entityColumn.methodName}
									</#if>
								)
							<#else>
								new FinderColumn<>(
									"${entity.alias}.",
									"${columnName}",
									<#if columnName != entityColumn.DBName>
										"${entityColumn.DBName}",
									</#if>
									${entityColumn.finderColumnTypeName},
									"${entityColumn.comparator}",
									${entityColumn.isCaseSensitive()?c},
									${entityColumn.isConvertNull()?c},
									<#if stringUtil.equals(entityColumn.type, "boolean")>
										${entity.name}::is${entityColumn.methodName}
									<#else>
										${entity.name}::get${entityColumn.methodName}
									</#if>
								)
							</#if>

							<#if entityColumn_has_next>
								,
							</#if>
						</#list>
					);
			</#if>

			<#if entityFinder.uniquePersistenceFinderEnabled>
				_uniquePersistenceFinderBy${entityFinder.name} =
					new UniquePersistenceFinder<>(
						this,
						createUniqueFinderPath(
							FINDER_CLASS_NAME_ENTITY,
							"fetchBy${entityFinder.name}",
							new String[] {
								<#list entityColumns as entityColumn>
									${serviceBuilder.getPrimitiveObj("${entityColumn.type}")}.class.getName()

									<#if entityColumn_has_next>
										,
									</#if>
								</#list>
							},
							new String[] {
								<#list entityColumns as entityColumn>
									"${entityColumn.DBName}"

									<#if entityColumn_has_next>
										,
									</#if>
								</#list>
								},
							${caseInsensitiveBitmask}, ${convertNullBitmask},
							<#if entityFinder.isPretouch()>true<#else>false</#if>

							<#list entityColumns as entityColumn>
								,
								<#if stringUtil.equals(entityColumn.type, "String") && !entityColumn.isCaseSensitive()>
									convertCaseFunction(${entity.name}::get${entityColumn.methodName})
								<#elseif stringUtil.equals(entityColumn.type, "String") && entityColumn.isConvertNull()>
									convertNullFunction(${entity.name}::get${entityColumn.methodName})
								<#elseif stringUtil.equals(entityColumn.type, "Date")>
									convertDateFunction(${entity.name}::get${entityColumn.methodName})
								<#else>
									${entity.name}::<#if stringUtil.equals(entityColumn.type, "boolean")>is<#else>get</#if>${entityColumn.methodName}
								</#if>
							</#list>
							),
						_SQL_SELECT_${entity.alias?upper_case}_WHERE,
						"${entityFinder.where!}",
						<#list entityColumns as entityColumn>
							<#if entity.hasCompoundPK() && entityColumn.isPrimary()>
								<#assign columnName = "id." + entityColumn.name />
							<#else>
								<#assign columnName = entityColumn.name />
							</#if>

							new FinderColumn<>(
								"${entity.alias}.",
								"${columnName}",
								<#if columnName != entityColumn.DBName>
									"${entityColumn.DBName}",
								</#if>
								${entityColumn.finderColumnTypeName},
								"${entityColumn.comparator}",
								${entityColumn.isCaseSensitive()?c},
								${entityColumn.isConvertNull()?c},
								<#if stringUtil.equals(entityColumn.type, "boolean")>
									${entity.name}::is${entityColumn.methodName}
								<#else>
									${entity.name}::get${entityColumn.methodName}
								</#if>
							)

							<#if entityColumn_has_next>
								,
							</#if>
						</#list>
					);
			</#if>
		</#list>

		${entity.name}Util.setPersistence(this);
	}

	<#if dependencyInjectorDS>
		@Deactivate
		public void deactivate() {
	<#else>
		public void destroy() {
	</#if>

		${entity.name}Util.setPersistence(null);

		${entityCache}.removeCache(${entity.name}Impl.class.getName());

		<#if serviceBuilder.isVersionGTE_7_3_0() && serviceBuilder.isVersionLTE_7_3_0()>
			_argumentsResolverServiceRegistration.unregister();

			for (ServiceRegistration<FinderPath> serviceRegistration :
				_serviceRegistrations) {

				serviceRegistration.unregister();
			}
		<#elseif serviceBuilder.isVersionLTE_7_2_0()>
			${finderCache}.removeCache(FINDER_CLASS_NAME_ENTITY);
			${finderCache}.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
			${finderCache}.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		</#if>

		<#list entity.entityColumns as entityColumn>
			<#if entityColumn.isCollection() && entityColumn.isMappingManyToMany()>
				<#if dependencyInjectorDS>
					TableMapperFactory.removeTableMapper("${entityColumn.mappingTableName}#${entity.PKDBName}");
				<#else>
					TableMapperFactory.removeTableMapper("${entityColumn.mappingTableName}");
				</#if>
			</#if>
		</#list>
	}

	<#if dependencyInjectorDS>
		<#include "persistence_references.ftl">

		<#if serviceBuilder.isVersionLTE_7_2_0()>
			private boolean _columnBitmaskEnabled;
		</#if>
	<#elseif !serviceBuilder.isVersionGTE_7_1_0()>
		@Override
		public void setSessionFactory(SessionFactory sessionFactory) {
			super.setSessionFactory(sessionFactory);

			DBType dbType = DBManagerUtil.getDBType(sessionFactory.getDialect());

			_databaseInMaxParameters = GetterUtil.getInteger(PropsUtil.get("database.in.max.parameters", new Filter(dbType.getName())));
		}
	</#if>

	<#if osgiModule>
		<#if serviceBuilder.isVersionGTE_7_3_0() && serviceBuilder.isVersionLTE_7_3_0()>
			private BundleContext _bundleContext;
		</#if>

		<#if entity.isChangeTrackingEnabled()>
			<#if dependencyInjectorDS>
				@Reference
			<#else>
				@ServiceReference(type = CTPersistenceHelper.class)
			</#if>

			protected CTPersistenceHelper ctPersistenceHelper;
		</#if>

		<#if serviceBuilder.isVersionLTE_7_2_0() || entity.isCacheEnabled()>
			<#if dependencyInjectorDS>
				@Reference
			<#else>
				@ServiceReference(type = EntityCache.class)
			</#if>
			protected EntityCache entityCache;

			<#if dependencyInjectorDS>
				@Reference
			<#else>
				@ServiceReference(type = FinderCache.class)
			</#if>
			protected FinderCache finderCache;
		</#if>
	</#if>

	<#list entity.entityColumns as entityColumn>
		<#if entityColumn.isCollection() && entityColumn.isMappingManyToMany()>
			<#assign referenceEntity = serviceBuilder.getEntity(entityColumn.entityName) />

			<#if !dependencyInjectorDS>
				@BeanReference(type = ${referenceEntity.name}Persistence.class)
				protected ${referenceEntity.name}Persistence ${referenceEntity.variableName}Persistence;
			</#if>

			protected TableMapper<${entity.name}, ${referenceEntity.apiPackagePath}.model.${referenceEntity.name}> ${entity.variableName}To${referenceEntity.name}TableMapper;
		</#if>
	</#list>

	<#if entity.localizedEntity??>
		<#assign localizedEntity = entity.localizedEntity />

		<#if dependencyInjectorDS>
			@Reference
		<#else>
			@BeanReference(type = ${localizedEntity.name}Persistence.class)
		</#if>

		protected ${localizedEntity.name}Persistence ${localizedEntity.variableName}Persistence;
	</#if>

	<#if entity.versionedEntity?? && entity.versionedEntity.localizedEntity??>
		<#assign
			versionedEntity = entity.versionedEntity
			localizedVersionEntity = versionedEntity.localizedEntity.versionEntity
		/>

		<#if dependencyInjectorDS>
			@Reference
		<#else>
			@BeanReference(type = ${localizedVersionEntity.name}Persistence.class)
		</#if>

		protected ${localizedVersionEntity.name}Persistence ${localizedVersionEntity.variableName}Persistence;
	</#if>

	<#if entity.isHierarchicalTree()>
		protected NestedSetsTreeManager<${entity.name}> nestedSetsTreeManager = new PersistenceNestedSetsTreeManager<${entity.name}>(this, "${entity.table}", "${entity.name}", ${entity.name}Impl.class, "${pkEntityColumn.DBName}", "${scopeEntityColumn.DBName}", "left${pkEntityColumn.methodName}", "right${pkEntityColumn.methodName}");
		protected boolean rebuildTreeEnabled = true;
	</#if>

	<#assign hasDateFinder = false />

	<#list entity.entityFinders as entityFinder>
		<#if (!entityFinder.collectionPersistenceFinderEnabled && !entityFinder.uniquePersistenceFinderEnabled) || entityFinder.hasArrayableOperator()>
			<#list entityFinder.entityColumns as entityColumn>
				<#if stringUtil.equals(entityColumn.type, "Date")>
					<#assign hasDateFinder = true />

					<#break>
				</#if>
			</#list>
		</#if>

		<#if hasDateFinder>
			<#break>
		</#if>
	</#list>

	<#if !hasDateFinder>
		<#list entity.uniqueEntityFinders as uniqueEntityFinder>
			<#list uniqueEntityFinder.entityColumns as entityColumn>
				<#if stringUtil.equals(entityColumn.type, "Date")>
					<#assign hasDateFinder = true />

					<#break>
				</#if>
			</#list>

			<#if hasDateFinder>
				<#break>
			</#if>
		</#list>
	</#if>

	<#if hasDateFinder>
		private static Long _getTime(Date date) {
			if (date == null) {
				return null;
			}

			return date.getTime();
		}
	</#if>

	<#if !serviceBuilder.isVersionGTE_7_4_0() || entity.hasCollectionEntityFinder()>
		private static final String _ENTITY_ALIAS_PREFIX = ${entity.name}ModelImpl.ENTITY_ALIAS + ".";
	</#if>

	private static final String _SQL_SELECT_${entity.alias?upper_case} = "SELECT ${entity.alias} FROM ${entity.name} ${entity.alias}";

	<#if !entity.hasCompoundPK() && serviceBuilder.isVersionLTE_7_1_0()>
		private static final String _SQL_SELECT_${entity.alias?upper_case}_WHERE_PKS_IN = "SELECT ${entity.alias} FROM ${entity.name} ${entity.alias} WHERE ${entity.PKDBName} IN (";
	</#if>

	<#if entity.entityFinders?size != 0>
		private static final String _SQL_SELECT_${entity.alias?upper_case}_WHERE = "SELECT ${entity.alias} FROM ${entity.name} ${entity.alias} WHERE ";
	</#if>

	<#if entity.hasCollectionEntityFinder() || (serviceBuilder.isVersionLTE_7_3_0() && (entity.entityFinders?size != 0))>
		private static final String _SQL_COUNT_${entity.alias?upper_case}_WHERE = "SELECT COUNT(${entity.alias}) FROM ${entity.name} ${entity.alias} WHERE ";
	</#if>

	<#if entity.isPermissionCheckEnabled()>
		<#assign hasNonDelegatedCollectionFilterFinder = false />

		<#list entity.entityFinders as orderByEntityTableEntityFinder>
			<#if orderByEntityTableEntityFinder.isCollection() && !orderByEntityTableEntityFinder.finderDelegationEnabled>
				<#assign hasNonDelegatedCollectionFilterFinder = true />

				<#break>
			</#if>
		</#list>

		<#if hasNonDelegatedCollectionFilterFinder>
			private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN = "${entity.alias}.${entity.filterPKEntityColumn.DBName}";

			<#if entity.isPermissionedModel()>
				<#if entity.hasEntityColumn("userId")>
					private static final String _FILTER_ENTITY_TABLE_FILTER_USERID_COLUMN = "${entity.alias}.userId";
				<#else>
					private static final String _FILTER_ENTITY_TABLE_FILTER_USERID_COLUMN = null;
				</#if>
			<#else>
				private static final String _FILTER_SQL_SELECT_${entity.alias?upper_case}_WHERE = "SELECT DISTINCT {${entity.alias}.*} FROM ${entity.table} ${entity.alias} WHERE ";

				private static final String _FILTER_SQL_SELECT_${entity.alias?upper_case}_NO_INLINE_DISTINCT_WHERE_1 = "SELECT {${entity.table}.*} FROM (SELECT DISTINCT ${entity.alias}.${entity.PKDBName} FROM ${entity.table} ${entity.alias} WHERE ";

				private static final String _FILTER_SQL_SELECT_${entity.alias?upper_case}_NO_INLINE_DISTINCT_WHERE_2 = ") TEMP_TABLE INNER JOIN ${entity.table} ON TEMP_TABLE.${entity.PKDBName} = ${entity.table}.${entity.PKDBName}";

				private static final String _FILTER_SQL_COUNT_${entity.alias?upper_case}_WHERE = "SELECT COUNT(DISTINCT ${entity.alias}.${entity.PKDBName}) AS COUNT_VALUE FROM ${entity.table} ${entity.alias} WHERE ";

				private static final String _FILTER_ENTITY_ALIAS = "${entity.alias}";

				private static final String _FILTER_ENTITY_TABLE = "${entity.table}";

				private static final String _ORDER_BY_ENTITY_TABLE = "${entity.table}.";
			</#if>
		</#if>
	</#if>

	<#if !serviceBuilder.isVersionGTE_7_4_0()>
		private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No ${entity.name} exists with the primary key ";
	</#if>

	<#if entity.entityFinders?size != 0>
		private static final String _NO_SUCH_ENTITY_WITH_KEY = "No ${entity.name} exists with the key {";
	</#if>

	<#assign logEmissionEnabled = !serviceBuilder.isVersionGTE_7_4_0() />

	<#if !logEmissionEnabled>
		<#list entity.entityFinders as logEmissionEntityFinder>
			<#if !logEmissionEntityFinder.isCollection() || logEmissionEntityFinder.isUnique()>
				<#assign logEmissionEnabled = true />

				<#break>
			</#if>
		</#list>
	</#if>

	<#if logEmissionEnabled>
		private static final Log _log = LogFactoryUtil.getLog(${entity.name}PersistenceImpl.class);
	</#if>

	<#if entity.badEntityColumns?size != 0>
		private static final Set<String> _badColumnNames = SetUtil.fromArray(
			new String[] {
				<#list entity.badEntityColumns as badEntityColumn>
					"${badEntityColumn.name}"

					<#if badEntityColumn_has_next>
						,
					</#if>
				</#list>
			});
	</#if>

	<#if entity.hasCompoundPK()>
		private static final Set<String> _compoundPKColumnNames = SetUtil.fromArray(
			new String[] {
				<#list entity.PKEntityColumns as entityColumn>
					"${entityColumn.name}"

					<#if entityColumn_has_next>
						,
					</#if>
				</#list>
			});
	</#if>

	<#if serviceBuilder.isVersionGTE_7_4_0()>
		@Override
		protected FinderCache getFinderCache() {
			<#if !entity.isCacheEnabled()>
				return dummyFinderCache;
			<#elseif osgiModule>
				return finderCache;
			<#else>
				return FinderCacheUtil.getFinderCache();
			</#if>
		}
	</#if>

	<#if serviceBuilder.isVersionGTE_7_3_0() && serviceBuilder.isVersionLTE_7_3_0()>
		private FinderPath _createFinderPath(
			String cacheName, String methodName, String[] params,
			String[] columnNames, boolean baseModelResult) {

			FinderPath finderPath = new FinderPath(cacheName, methodName, params, columnNames, baseModelResult);

			if (!cacheName.equals(FINDER_CLASS_NAME_LIST_WITH_PAGINATION)) {
				<#if osgiModule>
					_serviceRegistrations.add(_bundleContext.registerService(FinderPath.class, finderPath, MapUtil.singletonDictionary("cache.name", cacheName)));
				<#else>
					Registry registry = RegistryUtil.getRegistry();

					_serviceRegistrations.add(
						registry.registerService(
							FinderPath.class, finderPath,
							HashMapBuilder.<String, Object>put(
								"cache.name", cacheName
							).build()));
				</#if>
			}

			return finderPath;
		}

		private Set<ServiceRegistration<FinderPath>> _serviceRegistrations = new HashSet<>();

		private ServiceRegistration<ArgumentsResolver> _argumentsResolverServiceRegistration;

		<#include "model_arguments_resolver.ftl">
	</#if>

}

<#function bindParameter entityColumns>
	<#list entityColumns as entityColumn>
		<#if !entityColumn.hasArrayableOperator() || stringUtil.equals(entityColumn.type, "String")>
			<#return true>
		</#if>
	</#list>

	<#return false>
</#function>

<#macro finderQPos
	_arrayable = false
>
	<#list entityColumns as entityColumn>
		<#if _arrayable && entityColumn.hasArrayableOperator()>
			<#if stringUtil.equals(entityColumn.type, "String")>
				for (String ${entityColumn.name} : ${entityColumn.pluralName}) {
					if (${entityColumn.name} != null && !${entityColumn.name}.isEmpty()) {
						queryPos.add(${entityColumn.name});
					}
				}
			</#if>
		<#elseif entityColumn.isPrimitiveType()>
			queryPos.add(${entityColumn.name}${serviceBuilder.getPrimitiveObjValue("${entityColumn.type}")});

		<#else>
			if (bind${entityColumn.methodName}) {
				queryPos.add(
					<#if stringUtil.equals(entityColumn.type, "Date")>
						new Timestamp(${entityColumn.name}.getTime())
					<#elseif stringUtil.equals(entityColumn.type, "String") && !entityColumn.isCaseSensitive()>
						StringUtil.toLowerCase(${entityColumn.name})
					<#else>
						${entityColumn.name}
					</#if>
				);
			}
		</#if>
	</#list>
</#macro>