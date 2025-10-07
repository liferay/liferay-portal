package ${packagePath}.service.base;

import ${apiPackagePath}.service.${entity.name}${sessionTypeName}Service;
import ${apiPackagePath}.service.${entity.name}${sessionTypeName}ServiceUtil;

import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.io.AutoDeleteFileInputStream;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import ${beanLocatorUtil};
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.CurrentConnectionUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Conjunction;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DefaultActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Disjunction;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.service.Base${sessionTypeName}ServiceImpl;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalServiceRegistry;
import com.liferay.portal.kernel.service.PersistedModelLocalServiceRegistryUtil;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.spring.extender.service.ServiceReference;

import java.io.InputStream;
import java.io.Serializable;

import java.lang.reflect.Field;

import java.sql.Blob;
import java.sql.Connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

<#if entity.hasEntityColumns()>
	<#if entity.hasCompoundPK()>
		import ${apiPackagePath}.service.persistence.${entity.name}PK;
	</#if>

	import ${apiPackagePath}.model.${entity.name};

	<#list entity.blobEntityColumns as entityColumn>
		<#if entityColumn.lazy>
			import ${apiPackagePath}.model.${entity.name}${entityColumn.methodName}BlobModel;
		</#if>
	</#list>

	import ${packagePath}.model.impl.${entity.name}Impl;
</#if>

<#if entity.localizedEntity??>
	<#assign localizedEntity = entity.localizedEntity />

	import ${apiPackagePath}.model.${localizedEntity.name};
</#if>

<#if entity.versionEntity??>
	<#assign versionEntity = entity.versionEntity />

	import ${apiPackagePath}.model.${versionEntity.name};
	import com.liferay.portal.kernel.service.version.VersionService;
	import com.liferay.portal.kernel.service.version.VersionServiceListener;
	<#if entity.localizedEntity??>
		<#assign
			localizedEntity = entity.localizedEntity
			localizedVersionEntity = localizedEntity.versionEntity
		/>

		import ${apiPackagePath}.model.${localizedVersionEntity.name};
	</#if>
</#if>

<#list referenceEntities as referenceEntity>
	<#if referenceEntity.hasEntityColumns() && referenceEntity.hasPersistence()>
		import ${referenceEntity.apiPackagePath}.service.persistence.${referenceEntity.name}Persistence;
		import ${referenceEntity.apiPackagePath}.service.persistence.${referenceEntity.name}Util;
	</#if>

	<#if referenceEntity.hasFinderClassName() && (stringUtil.equals(entity.name, "Counter") || !stringUtil.equals(referenceEntity.name, "Counter"))>
		import ${referenceEntity.apiPackagePath}.service.persistence.${referenceEntity.name}Finder;
		import ${referenceEntity.apiPackagePath}.service.persistence.${referenceEntity.name}FinderUtil;
	</#if>
</#list>

<#list entity.entityColumns as entityColumn>
	<#if entityColumn.isCollection() && entityColumn.isMappingManyToMany()>
		<#assign referenceEntity = serviceBuilder.getEntity(entityColumn.entityName) />

		<#if !referenceEntities?seq_contains(referenceEntity) && referenceEntity.hasEntityColumns() && referenceEntity.hasPersistence()>
			import ${referenceEntity.apiPackagePath}.service.persistence.${referenceEntity.name}Persistence;
		</#if>
	</#if>
</#list>

<#if stringUtil.equals(sessionTypeName, "Local")>
/**
 * Provides the base implementation for the ${entity.humanName} local service.
 *
 * <p>
 * This implementation exists only as a container for the default service methods generated by ServiceBuilder. All custom service methods should be put in {@link ${packagePath}.service.impl.${entity.name}LocalServiceImpl}.
 * </p>
 *
 * @author ${author}
 * @see ${packagePath}.service.impl.${entity.name}LocalServiceImpl
<#if classDeprecated>
 * @deprecated ${classDeprecatedComment}
</#if>
 * @generated
 */

<#if classDeprecated>
	@Deprecated
</#if>
	public abstract class ${entity.name}LocalServiceBaseImpl extends BaseLocalServiceImpl implements ${entity.name}LocalService,
	<#if dependencyInjectorDS>
		AopService,
	<#elseif osgiModule && entity.isChangeTrackingEnabled()>
		CTService<${entity.name}>,
	</#if>

	IdentifiableOSGiService

	<#if entity.versionEntity??>
		<#assign versionEntity = entity.versionEntity />
		, VersionService<${entity.name}, ${versionEntity.name}>
	</#if>

	{

		/*
		 * NOTE FOR DEVELOPERS:
		 *
		 * Never modify or reference this class directly. Use <code>${apiPackagePath}.service.${entity.name}LocalService</code> via injection or a <code>org.osgi.util.tracker.ServiceTracker</code> or use <code>${apiPackagePath}.service.${entity.name}LocalServiceUtil</code>.
		 */
<#else>
/**
 * Provides the base implementation for the ${entity.humanName} remote service.
 *
 * <p>
 * This implementation exists only as a container for the default service methods generated by ServiceBuilder. All custom service methods should be put in {@link ${packagePath}.service.impl.${entity.name}ServiceImpl}.
 * </p>
 *
 * @author ${author}
 * @see ${packagePath}.service.impl.${entity.name}ServiceImpl
<#if classDeprecated>
 * @deprecated ${classDeprecatedComment}
</#if>
 * @generated
 */

<#if classDeprecated>
	@Deprecated
</#if>

	public abstract class ${entity.name}ServiceBaseImpl extends BaseServiceImpl implements ${entity.name}Service,
		<#if dependencyInjectorDS>
			AopService,
		</#if>

		IdentifiableOSGiService {

		/*
		 * NOTE FOR DEVELOPERS:
		 *
		 * Never modify or reference this class directly. Use <code>${apiPackagePath}.service.${entity.name}Service</code> via injection or a <code>org.osgi.util.tracker.ServiceTracker</code> or use <code>${apiPackagePath}.service.${entity.name}ServiceUtil</code>.
		 */
</#if>

	<#if stringUtil.equals(sessionTypeName, "Local") && entity.hasEntityColumns() && entity.hasPersistence()>
		<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "add" + entity.name, [apiPackagePath + ".model." + entity.name], []) />

		/**
		 * Adds the ${entity.humanName} to the database. Also notifies the appropriate model listeners.
		 *
		 * <p>
		 * <strong>Important:</strong> Inspect ${entity.name}LocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
		 * </p>
		 *
		 * @param ${entity.variableName} the ${entity.humanName}
		 * @return the ${entity.humanName} that was added
		<#list serviceBaseExceptions as exception>
		 * @throws ${exception}
		</#list>
		 */
		@Indexable(type = IndexableType.REINDEX)
		@Override
		public ${entity.name} add${entity.name}(${entity.name} ${entity.variableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
			${entity.variableName}.setNew(true);

			return ${entity.variableName}Persistence.update(${entity.variableName});
		}

		<#if entity.versionEntity??>
			/**
			 * Creates a new ${entity.humanName}. Does not add the ${entity.humanName} to the database.
			 *
			 * @return the new ${entity.humanName}
			 */
			@Override
			@Transactional(enabled = false)
			public ${entity.name} create() {
				long primaryKey = counterLocalService.increment(${entity.name}.class.getName());

				${entity.name} draft${entity.name} = ${entity.variableName}Persistence.create(primaryKey);

				draft${entity.name}.setHeadId(primaryKey);

				return draft${entity.name};
			}
		<#else>
			/**
			 * Creates a new ${entity.humanName} with the primary key. Does not add the ${entity.humanName} to the database.
			 *
			 * @param ${entity.PKVariableName} the primary key for the new ${entity.humanName}
			 * @return the new ${entity.humanName}
			 */
			@Override
			@Transactional(enabled = false)
			public ${entity.name} create${entity.name}(${entity.PKClassName} ${entity.PKVariableName}) {
				return ${entity.variableName}Persistence.create(${entity.PKVariableName});
			}
		</#if>

		<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "delete" + entity.name, [entity.PKClassName], ["PortalException"]) />

		/**
		 * Deletes the ${entity.humanName} with the primary key from the database. Also notifies the appropriate model listeners.
		 *
		 * <p>
		 * <strong>Important:</strong> Inspect ${entity.name}LocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
		 * </p>
		 *
		 * @param ${entity.PKVariableName} the primary key of the ${entity.humanName}
		 * @return the ${entity.humanName} that was removed
		<#list serviceBaseExceptions as exception>
		<#if stringUtil.equals(exception, "PortalException")>
		 * @throws PortalException if a ${entity.humanName} with the primary key could not be found
		<#else>
		 * @throws ${exception}
		</#if>
		</#list>
		 */
		@Indexable(type = IndexableType.DELETE)
		@Override
		public ${entity.name} delete${entity.name}(${entity.PKClassName} ${entity.PKVariableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
			<#if entity.versionEntity??>
				<#if !serviceBaseExceptions?seq_contains("PortalException")>
					try {
				</#if>

				${entity.name} ${entity.variableName} = ${entity.variableName}Persistence.fetchByPrimaryKey(${entity.PKVariableName});

				if (${entity.variableName} != null) {
					delete(${entity.variableName});
				}

				return ${entity.variableName};

				<#if !serviceBaseExceptions?seq_contains("PortalException")>
					}
					catch (PortalException portalException) {
						throw new SystemException(portalException);
					}
				</#if>
			<#else>
				return ${entity.variableName}Persistence.remove(${entity.PKVariableName});
			</#if>
		}

		<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "delete" + entity.name, [apiPackagePath + ".model." + entity.name], []) />

		/**
		 * Deletes the ${entity.humanName} from the database. Also notifies the appropriate model listeners.
		 *
		 * <p>
		 * <strong>Important:</strong> Inspect ${entity.name}LocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
		 * </p>
		 *
		 * @param ${entity.variableName} the ${entity.humanName}
		 * @return the ${entity.humanName} that was removed
		<#list serviceBaseExceptions as exception>
		 * @throws ${exception}
		</#list>
		 */
		@Indexable(type = IndexableType.DELETE)
		@Override
		public ${entity.name} delete${entity.name}(${entity.name} ${entity.variableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
			<#if entity.versionEntity??>
				<#if !serviceBaseExceptions?seq_contains("PortalException")>
					try {
				</#if>

				delete(${entity.variableName});

				return ${entity.variableName};

				<#if !serviceBaseExceptions?seq_contains("PortalException")>
					}
					catch (PortalException portalException) {
						throw new SystemException(portalException);
					}
				</#if>
			<#else>
				return ${entity.variableName}Persistence.remove(${entity.variableName});
			</#if>
		}

		<#if serviceBuilder.isDSLEnabled()>
			@Override
			public <T> T dslQuery(DSLQuery dslQuery) {
				return ${entity.variableName}Persistence.dslQuery(dslQuery);
			}

			@Override
			public int dslQueryCount(DSLQuery dslQuery) {
				Long count = dslQuery(dslQuery);

				return count.intValue();
			}
		</#if>

		@Override
		public DynamicQuery dynamicQuery() {
			Class<?> clazz = getClass();

			return DynamicQueryFactoryUtil.forClass(${entity.name}.class, clazz.getClassLoader());
		}

		/**
		 * Performs a dynamic query on the database and returns the matching rows.
		 *
		 * @param dynamicQuery the dynamic query
		 * @return the matching rows
		 */
		@Override
		public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
			return ${entity.variableName}Persistence.findWithDynamicQuery(dynamicQuery);
		}

		/**
		 * Performs a dynamic query on the database and returns a range of the matching rows.
		 *
		 * <p>
		 * <#include "range_comment.ftl">
		 * </p>
		 *
		 * @param dynamicQuery the dynamic query
		 * @param start the lower bound of the range of model instances
		 * @param end the upper bound of the range of model instances (not inclusive)
		 * @return the range of matching rows
		 */
		@Override
		public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery, int start, int end) {
			return ${entity.variableName}Persistence.findWithDynamicQuery(dynamicQuery, start, end);
		}

		/**
		 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
		 *
		 * <p>
		 * <#include "range_comment.ftl">
		 * </p>
		 *
		 * @param dynamicQuery the dynamic query
		 * @param start the lower bound of the range of model instances
		 * @param end the upper bound of the range of model instances (not inclusive)
		 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
		 * @return the ordered range of matching rows
		 */
		@Override
		public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery, int start, int end, OrderByComparator<T> orderByComparator) {
			return ${entity.variableName}Persistence.findWithDynamicQuery(dynamicQuery, start, end, orderByComparator);
		}

		/**
		 * Returns the number of rows matching the dynamic query.
		 *
		 * @param dynamicQuery the dynamic query
		 * @return the number of rows matching the dynamic query
		 */
		@Override
		public long dynamicQueryCount(DynamicQuery dynamicQuery) {
			return ${entity.variableName}Persistence.countWithDynamicQuery(dynamicQuery);
		}

		/**
		 * Returns the number of rows matching the dynamic query.
		 *
		 * @param dynamicQuery the dynamic query
		 * @param projection the projection to apply to the query
		 * @return the number of rows matching the dynamic query
		 */
		@Override
		public long dynamicQueryCount(DynamicQuery dynamicQuery, Projection projection) {
			return ${entity.variableName}Persistence.countWithDynamicQuery(dynamicQuery, projection);
		}

		<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "fetch" + entity.name, [entity.PKClassName], []) />

		@Override
		public ${entity.name} fetch${entity.name}(${entity.PKClassName} ${entity.PKVariableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
			return ${entity.variableName}Persistence.fetchByPrimaryKey(${entity.PKVariableName});
		}

		<#if entity.hasUuid() && entity.hasEntityColumn("companyId") && (!entity.hasEntityColumn("groupId") || stringUtil.equals(entity.name, "Group")) && !entity.versionEntity??>
			/**
			 * Returns the ${entity.humanName} with the matching UUID and company.
			 *
			 * @param uuid the ${entity.humanName}'s UUID
			 * @param companyId the primary key of the company
			 * @return the matching ${entity.humanName}, or <code>null</code> if a matching ${entity.humanName} could not be found
			<#list serviceBaseExceptions as exception>
			 * @throws ${exception}
			</#list>
			 */
			@Override
			public ${entity.name} fetch${entity.name}ByUuidAndCompanyId(String uuid, long companyId) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
				return ${entity.variableName}Persistence.fetchByUuid_C_First(uuid, companyId, null);
			}
		</#if>

		<#if entity.hasUuid() && entity.hasEntityColumn("groupId") && !stringUtil.equals(entity.name, "Group") && !entity.versionEntity??>
			<#if stringUtil.equals(entity.name, "Layout")>
				/**
				 * Returns the ${entity.humanName} matching the UUID, group, and privacy.
				 *
				 * @param uuid the ${entity.humanName}'s UUID
				 * @param groupId the primary key of the group
				 * @param privateLayout whether the ${entity.humanName} is private to the group
				 * @return the matching ${entity.humanName}, or <code>null</code> if a matching ${entity.humanName} could not be found
				<#list serviceBaseExceptions as exception>
				 * @throws ${exception}
				</#list>
				 */
				@Override
				public ${entity.name} fetch${entity.name}ByUuidAndGroupId(String uuid, long groupId, boolean privateLayout) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
					return ${entity.variableName}Persistence.fetchByUUID_G_P(uuid, groupId, privateLayout);
				}
			<#else>
				/**
				 * Returns the ${entity.humanName} matching the UUID and group.
				 *
				 * @param uuid the ${entity.humanName}'s UUID
				 * @param groupId the primary key of the group
				 * @return the matching ${entity.humanName}, or <code>null</code> if a matching ${entity.humanName} could not be found
				<#list serviceBaseExceptions as exception>
				 * @throws ${exception}
				</#list>
				 */
				@Override
				public ${entity.name} fetch${entity.name}ByUuidAndGroupId(String uuid, long groupId) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
					return ${entity.variableName}Persistence.fetchByUUID_G(uuid, groupId);
				}
			</#if>
		</#if>

		<#if entity.hasExternalReferenceCode()>
			<#if serviceBuilder.isVersionGTE_7_4_0()>
				<#if entity.versionEntity??>
					@Override
					public ${entity.name} fetch${entity.name}ByExternalReferenceCode(String externalReferenceCode, long ${entity.externalReferenceCode}Id) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
						return ${entity.variableName}Persistence.fetchByERC_${entity.externalReferenceCode?cap_first[0..0]}_Head(externalReferenceCode, ${entity.externalReferenceCode}Id, true);
					}

					@Override
					public ${entity.name} fetch${entity.name}ByExternalReferenceCode(String externalReferenceCode, long ${entity.externalReferenceCode}Id, boolean head) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
						return ${entity.variableName}Persistence.fetchByERC_${entity.externalReferenceCode?cap_first[0..0]}_Head(externalReferenceCode, ${entity.externalReferenceCode}Id, head);
					}

					@Override
					public ${entity.name} get${entity.name}ByExternalReferenceCode(String externalReferenceCode, long ${entity.externalReferenceCode}Id) throws PortalException {
						return ${entity.variableName}Persistence.findByERC_${entity.externalReferenceCode?cap_first[0..0]}_Head(externalReferenceCode, ${entity.externalReferenceCode}Id, true);
					}

					@Override
					public ${entity.name} get${entity.name}ByExternalReferenceCode(String externalReferenceCode, long ${entity.externalReferenceCode}Id, boolean head) throws PortalException {
						return ${entity.variableName}Persistence.findByERC_${entity.externalReferenceCode?cap_first[0..0]}_Head(externalReferenceCode, ${entity.externalReferenceCode}Id, head);
					}
				<#else>
					@Override
					public ${entity.name} fetch${entity.name}ByExternalReferenceCode(String externalReferenceCode, long ${entity.externalReferenceCode}Id) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
						return ${entity.variableName}Persistence.fetchByERC_${entity.externalReferenceCode?cap_first[0..0]}(externalReferenceCode, ${entity.externalReferenceCode}Id);
					}

					@Override
					public ${entity.name} get${entity.name}ByExternalReferenceCode(String externalReferenceCode, long ${entity.externalReferenceCode}Id) throws PortalException {
						return ${entity.variableName}Persistence.findByERC_${entity.externalReferenceCode?cap_first[0..0]}(externalReferenceCode, ${entity.externalReferenceCode}Id);
					}
				</#if>
			<#else>
				@Deprecated
				@Override
				public ${entity.name} fetch${entity.name}ByExternalReferenceCode(long ${entity.externalReferenceCode}Id, String externalReferenceCode) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
					return ${entity.variableName}Persistence.fetchBy${entity.externalReferenceCode?cap_first[0..0]}_ERC(${entity.externalReferenceCode}Id, externalReferenceCode);
				}

				@Deprecated
				@Override
				public ${entity.name} fetch${entity.name}ByReferenceCode(long ${entity.externalReferenceCode}Id, String externalReferenceCode) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
					return fetch${entity.name}ByExternalReferenceCode(${entity.externalReferenceCode}Id, externalReferenceCode);
				}

				@Deprecated
				@Override
				public ${entity.name} get${entity.name}ByExternalReferenceCode(long ${entity.externalReferenceCode}Id, String externalReferenceCode) throws PortalException {
					return ${entity.variableName}Persistence.findBy${entity.externalReferenceCode?cap_first[0..0]}_ERC(${entity.externalReferenceCode}Id, externalReferenceCode);
				}
			</#if>
		</#if>

		<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "get" + entity.name, [entity.PKClassName], ["PortalException"]) />

		/**
		 * Returns the ${entity.humanName} with the primary key.
		 *
		 * @param ${entity.PKVariableName} the primary key of the ${entity.humanName}
		 * @return the ${entity.humanName}
		<#list serviceBaseExceptions as exception>
		<#if stringUtil.equals(exception, "PortalException")>
		 * @throws PortalException if a ${entity.humanName} with the primary key could not be found
		<#else>
		 * @throws ${exception}
		</#if>
		</#list>
		 */
		@Override
		public ${entity.name} get${entity.name}(${entity.PKClassName} ${entity.PKVariableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
			return ${entity.variableName}Persistence.findByPrimaryKey(${entity.PKVariableName});
		}

		<#if entity.hasActionableDynamicQuery()>
			@Override
			public ActionableDynamicQuery getActionableDynamicQuery() {
				ActionableDynamicQuery actionableDynamicQuery = new DefaultActionableDynamicQuery();

				actionableDynamicQuery.setBaseLocalService(${entity.variableName}LocalService);
				actionableDynamicQuery.setClassLoader(getClassLoader());
				actionableDynamicQuery.setModelClass(${entity.name}.class);

				<#if entity.hasPrimitivePK()>
					actionableDynamicQuery.setPrimaryKeyPropertyName("${entity.PKVariableName}");
				<#else>
					<#assign
						pkEntityColumn = entity.PKEntityColumns?first
					/>

					actionableDynamicQuery.setPrimaryKeyPropertyName("primaryKey.${pkEntityColumn.name}");

					<#list entity.PKEntityColumns as pkEntityColumn>
						<#if stringUtil.equals(pkEntityColumn.name, "groupId")>
							actionableDynamicQuery.setGroupIdPropertyName("primaryKey.groupId");
						</#if>
					</#list>
				</#if>

				return actionableDynamicQuery;
			}

			@Override
			public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery() {
				IndexableActionableDynamicQuery indexableActionableDynamicQuery = new IndexableActionableDynamicQuery();

				indexableActionableDynamicQuery.setBaseLocalService(${entity.variableName}LocalService);
				indexableActionableDynamicQuery.setClassLoader(getClassLoader());
				indexableActionableDynamicQuery.setModelClass(${entity.name}.class);

				<#if entity.hasPrimitivePK()>
					indexableActionableDynamicQuery.setPrimaryKeyPropertyName("${entity.PKVariableName}");
				<#else>
					<#assign
						pkEntityColumn = entity.PKEntityColumns?first
					/>

					indexableActionableDynamicQuery.setPrimaryKeyPropertyName("primaryKey.${pkEntityColumn.name}");

					<#list entity.PKEntityColumns as pkEntityColumn>
						<#if stringUtil.equals(pkEntityColumn.name, "groupId")>
							indexableActionableDynamicQuery.setGroupIdPropertyName("primaryKey.groupId");
						</#if>
					</#list>
				</#if>

				return indexableActionableDynamicQuery;
			}

			protected void initActionableDynamicQuery(ActionableDynamicQuery actionableDynamicQuery) {
				actionableDynamicQuery.setBaseLocalService(${entity.variableName}LocalService);
				actionableDynamicQuery.setClassLoader(getClassLoader());
				actionableDynamicQuery.setModelClass(${entity.name}.class);

				<#if entity.hasPrimitivePK()>
					actionableDynamicQuery.setPrimaryKeyPropertyName("${entity.PKVariableName}");
				<#else>
					<#assign
						pkEntityColumn = entity.PKEntityColumns?first
					/>

					actionableDynamicQuery.setPrimaryKeyPropertyName("primaryKey.${pkEntityColumn.name}");

					<#list entity.PKEntityColumns as pkEntityColumn>
						<#if stringUtil.equals(pkEntityColumn.name, "groupId")>
							actionableDynamicQuery.setGroupIdPropertyName("primaryKey.groupId");
						</#if>
					</#list>
				</#if>
			}

			<#if entity.isStagedModel()>
				@Override
				public ExportActionableDynamicQuery getExportActionableDynamicQuery(final PortletDataContext portletDataContext) {
					final ExportActionableDynamicQuery exportActionableDynamicQuery = new ExportActionableDynamicQuery() {

						@Override
						public long performCount() throws PortalException {
							ManifestSummary manifestSummary = portletDataContext.getManifestSummary();

							StagedModelType stagedModelType = getStagedModelType();

							long modelAdditionCount = super.performCount();

							manifestSummary.addModelAdditionCount(stagedModelType, modelAdditionCount);

							long modelDeletionCount = ExportImportHelperUtil.getModelDeletionCount(portletDataContext, stagedModelType);

							manifestSummary.addModelDeletionCount(stagedModelType, modelDeletionCount);

							return modelAdditionCount;
						}

						<#if entity.isResourcedModel()>
							@Override
							protected Projection getCountProjection() {
								return ProjectionFactoryUtil.countDistinct("resourcePrimKey");
							}
						</#if>
					};

					initActionableDynamicQuery(exportActionableDynamicQuery);

					exportActionableDynamicQuery.setAddCriteriaMethod(
						new ActionableDynamicQuery.AddCriteriaMethod() {

							@Override
							public void addCriteria(DynamicQuery dynamicQuery) {
								<#if entity.isWorkflowEnabled()>
									Criterion modifiedDateCriterion = portletDataContext.getDateRangeCriteria("modifiedDate");

									<#if entity.isStagedGroupedModel()>
										if (modifiedDateCriterion != null) {
											Conjunction conjunction = RestrictionsFactoryUtil.conjunction();

											conjunction.add(modifiedDateCriterion);

											Disjunction disjunction = RestrictionsFactoryUtil.disjunction();

											disjunction.add(RestrictionsFactoryUtil.gtProperty("modifiedDate", "lastPublishDate"));

											Property lastPublishDateProperty = PropertyFactoryUtil.forName("lastPublishDate");

											disjunction.add(lastPublishDateProperty.isNull());

											conjunction.add(disjunction);

											modifiedDateCriterion = conjunction;
										}
									</#if>

									Criterion statusDateCriterion = portletDataContext.getDateRangeCriteria("statusDate");

									if ((modifiedDateCriterion != null) && (statusDateCriterion != null)) {
										Disjunction disjunction = RestrictionsFactoryUtil.disjunction();

										disjunction.add(modifiedDateCriterion);
										disjunction.add(statusDateCriterion);

										dynamicQuery.add(disjunction);
									}
								<#else>
									portletDataContext.addDateRangeCriteria(dynamicQuery, "modifiedDate");
								</#if>

								<#if entity.isTypedModel()>
									StagedModelType stagedModelType = exportActionableDynamicQuery.getStagedModelType();

									long referrerClassNameId = stagedModelType.getReferrerClassNameId();

									Property classNameIdProperty = PropertyFactoryUtil.forName("classNameId");

									if ((referrerClassNameId != StagedModelType.REFERRER_CLASS_NAME_ID_ALL) && (referrerClassNameId != StagedModelType.REFERRER_CLASS_NAME_ID_ANY)) {
										dynamicQuery.add(classNameIdProperty.eq(stagedModelType.getReferrerClassNameId()));
									}
									else if (referrerClassNameId == StagedModelType.REFERRER_CLASS_NAME_ID_ANY) {
										dynamicQuery.add(classNameIdProperty.isNotNull());
									}
								</#if>

								<#if entity.isWorkflowEnabled()>
									Property workflowStatusProperty = PropertyFactoryUtil.forName("status");

									if (portletDataContext.isInitialPublication()) {
										dynamicQuery.add(workflowStatusProperty.ne(WorkflowConstants.STATUS_IN_TRASH));
									}
									else {
										StagedModelDataHandler<?> stagedModelDataHandler = StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(${entity.name}.class.getName());

										dynamicQuery.add(workflowStatusProperty.in(stagedModelDataHandler.getExportableStatuses()));
									}
								</#if>
							}

						});

					exportActionableDynamicQuery.setCompanyId(portletDataContext.getCompanyId());

					<#if entity.isStagedGroupedModel()>
						exportActionableDynamicQuery.setGroupId(portletDataContext.getScopeGroupId());
					</#if>

					exportActionableDynamicQuery.setPerformActionMethod(
						new ActionableDynamicQuery.PerformActionMethod<${entity.name}>() {

							@Override
							public void performAction(${entity.name} ${entity.variableName}) throws PortalException {
								StagedModelDataHandlerUtil.exportStagedModel(portletDataContext, ${entity.variableName});
							}

						});
					<#if entity.isTypedModel()>
						exportActionableDynamicQuery.setStagedModelType(new StagedModelType(PortalUtil.getClassNameId(${entity.name}.class.getName()), StagedModelType.REFERRER_CLASS_NAME_ID_ALL));
					<#else>
						exportActionableDynamicQuery.setStagedModelType(new StagedModelType(PortalUtil.getClassNameId(${entity.name}.class.getName())));
					</#if>

					return exportActionableDynamicQuery;
				}
			</#if>
		</#if>

		<#if serviceBuilder.isVersionGTE_7_3_0()>
			/**
			 * @throws PortalException
			 */
		</#if>
		<#if serviceBuilder.isVersionGTE_7_4_0()>
			@Override
		</#if>
		<#if serviceBuilder.isVersionGTE_7_3_0()>
			public PersistedModel createPersistedModel(Serializable primaryKeyObj) throws PortalException {
				return ${entity.variableName}Persistence.create(

				<#if entity.hasPrimitivePK()>
					((${serviceBuilder.getPrimitiveObj("${entity.PKClassName}")})
				<#else>
					(${entity.PKClassName})
				</#if>

				primaryKeyObj

				<#if entity.hasPrimitivePK()>
					)${serviceBuilder.getPrimitiveObjValue(serviceBuilder.getPrimitiveObj("${entity.PKClassName}"))}
				</#if>

				);
			}
		</#if>

		/**
		 * @throws PortalException
		 */
		@Override
		public PersistedModel deletePersistedModel(PersistedModel persistedModel) throws PortalException {
			<#if serviceBuilder.isVersionGTE_7_4_0()>
				if (_log.isWarnEnabled()) {
					_log.warn("Implement ${entity.name}LocalServiceImpl#delete${entity.name}(${entity.name}) to avoid orphaned data");
				}
			</#if>

			return ${entity.variableName}LocalService.delete${entity.name}((${entity.name})persistedModel);
		}

		<#if serviceBuilder.isVersionGTE_7_4_0()>
			@Override
		</#if>
		public BasePersistence<${entity.name}> getBasePersistence() {
			return ${entity.variableName}Persistence;
		}

		/**
		 * @throws PortalException
		 */
		@Override
		public PersistedModel getPersistedModel(Serializable primaryKeyObj) throws PortalException {
			return ${entity.variableName}Persistence.findByPrimaryKey(primaryKeyObj);
		}

		<#if entity.isResourcedModel()>
			@Override
			public List<? extends PersistedModel> getPersistedModel(long resourcePrimKey) throws PortalException {
				return ${entity.variableName}Persistence.findByResourcePrimKey(resourcePrimKey);
			}
		</#if>

		<#if entity.hasUuid() && entity.hasEntityColumn("companyId") && !entity.versionEntity??>
			<#if entity.hasEntityColumn("groupId") && !stringUtil.equals(entity.name, "Group")>
				/**
				 * Returns all the ${entity.pluralHumanName} matching the UUID and company.
				 *
				 * @param uuid the UUID of the ${entity.pluralHumanName}
				 * @param companyId the primary key of the company
				 * @return the matching ${entity.pluralHumanName}, or an empty list if no matches were found
				 */
				@Override
				public List<${entity.name}> get${entity.pluralName}ByUuidAndCompanyId(String uuid, long companyId) {
					return ${entity.variableName}Persistence.findByUuid_C(uuid, companyId);
				}

				/**
				 * Returns a range of ${entity.pluralHumanName} matching the UUID and company.
				 *
				 * @param uuid the UUID of the ${entity.pluralHumanName}
				 * @param companyId the primary key of the company
				 * @param start the lower bound of the range of ${entity.pluralHumanName}
				 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
				 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
				 * @return the range of matching ${entity.pluralHumanName}, or an empty list if no matches were found
				 */
				@Override
				public List<${entity.name}> get${entity.pluralName}ByUuidAndCompanyId(String uuid, long companyId, int start, int end, OrderByComparator<${entity.name}> orderByComparator) {
					return ${entity.variableName}Persistence.findByUuid_C(uuid, companyId, start, end, orderByComparator);
				}
			<#else>
				/**
				 * Returns the ${entity.humanName} with the matching UUID and company.
				 *
				 * @param uuid the ${entity.humanName}'s UUID
				 * @param companyId the primary key of the company
				 * @return the matching ${entity.humanName}
				<#list serviceBaseExceptions as exception>
				<#if stringUtil.equals(exception, "PortalException")>
				 * @throws PortalException if a matching ${entity.humanName} could not be found
				<#else>
				 * @throws ${exception}
				</#if>
				</#list>
				 */
				@Override
				public ${entity.name} get${entity.name}ByUuidAndCompanyId(String uuid, long companyId) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
					return ${entity.variableName}Persistence.findByUuid_C_First(uuid, companyId, null);
				}
			</#if>
		</#if>

		<#if entity.hasUuid() && entity.hasEntityColumn("groupId") && !stringUtil.equals(entity.name, "Group") && !entity.versionEntity??>
			<#if stringUtil.equals(entity.name, "Layout")>
				/**
				 * Returns the ${entity.humanName} matching the UUID, group, and privacy.
				 *
				 * @param uuid the ${entity.humanName}'s UUID
				 * @param groupId the primary key of the group
				 * @param privateLayout whether the ${entity.humanName} is private to the group
				 * @return the matching ${entity.humanName}
				<#list serviceBaseExceptions as exception>
				<#if stringUtil.equals(exception, "PortalException")>
				 * @throws PortalException if a matching ${entity.humanName} could not be found
				<#else>
				 * @throws ${exception}
				</#if>
				</#list>
				 */
				@Override
				public ${entity.name} get${entity.name}ByUuidAndGroupId(String uuid, long groupId, boolean privateLayout) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
					return ${entity.variableName}Persistence.findByUUID_G_P(uuid, groupId, privateLayout);
				}
			<#else>
				/**
				 * Returns the ${entity.humanName} matching the UUID and group.
				 *
				 * @param uuid the ${entity.humanName}'s UUID
				 * @param groupId the primary key of the group
				 * @return the matching ${entity.humanName}
				<#list serviceBaseExceptions as exception>
				<#if stringUtil.equals(exception, "PortalException")>
				 * @throws PortalException if a matching ${entity.humanName} could not be found
				<#else>
				 * @throws ${exception}
				</#if>
				</#list>
				 */
				@Override
				public ${entity.name} get${entity.name}ByUuidAndGroupId(String uuid, long groupId) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
					return ${entity.variableName}Persistence.findByUUID_G(uuid, groupId);
				}
			</#if>
		</#if>

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
		public List<${entity.name}> get${entity.pluralName}(int start, int end) {
			return ${entity.variableName}Persistence.findAll(start, end);
		}

		/**
		 * Returns the number of ${entity.pluralHumanName}.
		 *
		 * @return the number of ${entity.pluralHumanName}
		 */
		@Override
		public int get${entity.pluralName}Count() {
			return ${entity.variableName}Persistence.countAll();
		}

		<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "update" + entity.name, [apiPackagePath + ".model." + entity.name], []) />

		/**
		 * Updates the ${entity.humanName} in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
		 *
		 * <p>
		 * <strong>Important:</strong> Inspect ${entity.name}LocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
		 * </p>
		 *
		<#if entity.versionEntity??>
		 	* @param draft${entity.name} the ${entity.humanName}
		<#else>
			* @param ${entity.variableName} the ${entity.humanName}
		</#if>
		 * @return the ${entity.humanName} that was updated
		<#list serviceBaseExceptions as exception>
		 * @throws ${exception}
		</#list>
		 */
		@Indexable(type = IndexableType.REINDEX)
		@Override
		<#if entity.versionEntity??>
			public ${entity.name} update${entity.name}(${entity.name} draft${entity.name}) throws PortalException {
				return updateDraft(draft${entity.name});
			}
		<#else>
			public ${entity.name} update${entity.name}(${entity.name} ${entity.variableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
				return ${entity.variableName}Persistence.update(${entity.variableName});
			}
		</#if>

		<#list entity.entityColumns as entityColumn>
			<#if entityColumn.isCollection() && entityColumn.isMappingManyToMany()>
				<#assign
					referenceEntity = serviceBuilder.getEntity(entityColumn.entityName)

					serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "add" + referenceEntity.name + entity.name, [referenceEntity.PKClassName, entity.PKClassName], [])
				/>

				/**
				<#list serviceBaseExceptions as exception>
				 * @throws ${exception}
				</#list>
				 */
				@Override
				<#if serviceBuilder.isVersionGTE_7_4_0()>
					public boolean add${referenceEntity.name}${entity.name}(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}, ${entity.PKClassName} ${entity.PKVariableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
						return ${referenceEntity.variableName}Persistence.add${entity.name}(${referenceEntity.PKVariableName}, ${entity.PKVariableName});
					}
				<#else>
					public void add${referenceEntity.name}${entity.name}(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}, ${entity.PKClassName} ${entity.PKVariableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
						${referenceEntity.variableName}Persistence.add${entity.name}(${referenceEntity.PKVariableName}, ${entity.PKVariableName});
					}
				</#if>

				<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "add" + referenceEntity.name + entity.name, [referenceEntity.PKClassName, apiPackagePath + ".model." + entity.name], []) />

				/**
				<#list serviceBaseExceptions as exception>
				 * @throws ${exception}
				</#list>
				 */
				@Override
				<#if serviceBuilder.isVersionGTE_7_4_0()>
					public boolean add${referenceEntity.name}${entity.name}(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}, ${entity.name} ${entity.variableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
						return ${referenceEntity.variableName}Persistence.add${entity.name}(${referenceEntity.PKVariableName}, ${entity.variableName});
					}
				<#else>
					public void add${referenceEntity.name}${entity.name}(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}, ${entity.name} ${entity.variableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
						${referenceEntity.variableName}Persistence.add${entity.name}(${referenceEntity.PKVariableName}, ${entity.variableName});
					}
				</#if>

				<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "add" + referenceEntity.name + entity.pluralName, [referenceEntity.PKClassName, entity.PKClassName + "[]"], []) />

				/**
				<#list serviceBaseExceptions as exception>
				 * @throws ${exception}
				</#list>
				 */
				@Override
				<#if serviceBuilder.isVersionGTE_7_4_0()>
					public boolean add${referenceEntity.name}${entity.pluralName}(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}, ${entity.PKClassName}[] ${entity.pluralPKVariableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
						return ${referenceEntity.variableName}Persistence.add${entity.pluralName}(${referenceEntity.PKVariableName}, ${entity.pluralPKVariableName});
					}
				<#else>
					public void add${referenceEntity.name}${entity.pluralName}(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}, ${entity.PKClassName}[] ${entity.pluralPKVariableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
						${referenceEntity.variableName}Persistence.add${entity.pluralName}(${referenceEntity.PKVariableName}, ${entity.pluralPKVariableName});
					}
				</#if>

				<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "add" + referenceEntity.name + entity.pluralName, [referenceEntity.PKClassName, "java.util.List<" + entity.name + ">"], []) />

				/**
				<#list serviceBaseExceptions as exception>
				 * @throws ${exception}
				</#list>
				 */
				@Override
				<#if serviceBuilder.isVersionGTE_7_4_0()>
					public boolean add${referenceEntity.name}${entity.pluralName}(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}, List<${entity.name}> ${entity.pluralVariableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
						return ${referenceEntity.variableName}Persistence.add${entity.pluralName}(${referenceEntity.PKVariableName}, ${entity.pluralVariableName});
					}
				<#else>
					public void add${referenceEntity.name}${entity.pluralName}(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}, List<${entity.name}> ${entity.pluralVariableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
						${referenceEntity.variableName}Persistence.add${entity.pluralName}(${referenceEntity.PKVariableName}, ${entity.pluralVariableName});
					}
				</#if>

				<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "clear" + referenceEntity.name + entity.pluralName, [referenceEntity.PKClassName], []) />

				/**
				<#list serviceBaseExceptions as exception>
				 * @throws ${exception}
				</#list>
				 */
				@Override
				public void clear${referenceEntity.name}${entity.pluralName}(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
					${referenceEntity.variableName}Persistence.clear${entity.pluralName}(${referenceEntity.PKVariableName});
				}

				<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "delete" + referenceEntity.name + entity.name, [referenceEntity.PKClassName, entity.PKClassName], []) />

				/**
				<#list serviceBaseExceptions as exception>
				 * @throws ${exception}
				</#list>
				 */
				@Override
				public void delete${referenceEntity.name}${entity.name}(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}, ${entity.PKClassName} ${entity.PKVariableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
					${referenceEntity.variableName}Persistence.remove${entity.name}(${referenceEntity.PKVariableName}, ${entity.PKVariableName});
				}

				<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "delete" + referenceEntity.name + entity.name, [referenceEntity.PKClassName, apiPackagePath + ".model." + entity.name], []) />

				/**
				<#list serviceBaseExceptions as exception>
				 * @throws ${exception}
				</#list>
				 */
				@Override
				public void delete${referenceEntity.name}${entity.name}(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}, ${entity.name} ${entity.variableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
					${referenceEntity.variableName}Persistence.remove${entity.name}(${referenceEntity.PKVariableName}, ${entity.variableName});
				}

				<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "delete" + referenceEntity.name + entity.pluralName, [referenceEntity.PKClassName, entity.PKClassName + "[]"], []) />

				/**
				<#list serviceBaseExceptions as exception>
				 * @throws ${exception}
				</#list>
				 */
				@Override
				public void delete${referenceEntity.name}${entity.pluralName}(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}, ${entity.PKClassName}[] ${entity.pluralPKVariableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
					${referenceEntity.variableName}Persistence.remove${entity.pluralName}(${referenceEntity.PKVariableName}, ${entity.pluralPKVariableName});
				}

				<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "delete" + referenceEntity.name + entity.pluralName, [referenceEntity.PKClassName, "java.util.List<" + entity.name + ">"], []) />

				/**
				<#list serviceBaseExceptions as exception>
				 * @throws ${exception}
				</#list>
				 */
				@Override
				public void delete${referenceEntity.name}${entity.pluralName}(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}, List<${entity.name}> ${entity.pluralVariableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
					${referenceEntity.variableName}Persistence.remove${entity.pluralName}(${referenceEntity.PKVariableName}, ${entity.pluralVariableName});
				}

				/**
				 * Returns the ${referenceEntity.PKVariableName}s of the ${referenceEntity.pluralHumanName} associated with the ${entity.humanName}.
				 *
				 * @param ${entity.PKVariableName} the ${entity.PKVariableName} of the ${entity.humanName}
				 * @return long[] the ${referenceEntity.PKVariableName}s of ${referenceEntity.pluralHumanName} associated with the ${entity.humanName}
				 */
				@Override
				public long[] get${referenceEntity.name}PrimaryKeys(${entity.PKClassName} ${entity.PKVariableName}) {
					return ${entity.variableName}Persistence.get${referenceEntity.name}PrimaryKeys(${entity.PKVariableName});
				}

				<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "get" + referenceEntity.name + entity.pluralName, [referenceEntity.PKClassName], []) />

				/**
				<#list serviceBaseExceptions as exception>
				 * @throws ${exception}
				</#list>
				 */
				@Override
				public List<${entity.name}> get${referenceEntity.name}${entity.pluralName}(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
					<#if dependencyInjectorDS>
						return ${entity.variableName}Persistence.get${referenceEntity.name}${entity.pluralName}(${referenceEntity.PKVariableName});
					<#else>
						return ${referenceEntity.variableName}Persistence.get${entity.pluralName}(${referenceEntity.PKVariableName});
					</#if>
				}

				<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "get" + referenceEntity.name + entity.pluralName, [referenceEntity.PKClassName, "int", "int"], []) />

				/**
				<#list serviceBaseExceptions as exception>
				 * @throws ${exception}
				</#list>
				 */
				@Override
				public List<${entity.name}> get${referenceEntity.name}${entity.pluralName}(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}, int start, int end) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
					<#if dependencyInjectorDS>
						return ${entity.variableName}Persistence.get${referenceEntity.name}${entity.pluralName}(${referenceEntity.PKVariableName}, start, end);
					<#else>
						return ${referenceEntity.variableName}Persistence.get${entity.pluralName}(${referenceEntity.PKVariableName}, start, end);
					</#if>
				}

				<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "get" + referenceEntity.name + entity.pluralName, [referenceEntity.PKClassName, "int", "int", "com.liferay.portal.kernel.util.OrderByComparator"], []) />

				/**
				<#list serviceBaseExceptions as exception>
				 * @throws ${exception}
				</#list>
				 */
				@Override
				public List<${entity.name}> get${referenceEntity.name}${entity.pluralName}(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}, int start, int end, OrderByComparator<${entity.name}> orderByComparator) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
					<#if dependencyInjectorDS>
						return ${entity.variableName}Persistence.get${referenceEntity.name}${entity.pluralName}(${referenceEntity.PKVariableName}, start, end, orderByComparator);
					<#else>
						return ${referenceEntity.variableName}Persistence.get${entity.pluralName}(${referenceEntity.PKVariableName}, start, end, orderByComparator);
					</#if>
				}

				<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "get" + referenceEntity.name + entity.pluralName + "Count", [referenceEntity.PKClassName], []) />

				/**
				<#list serviceBaseExceptions as exception>
				 * @throws ${exception}
				</#list>
				 */
				@Override
				public int get${referenceEntity.name}${entity.pluralName}Count(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
					return ${referenceEntity.variableName}Persistence.get${entity.pluralName}Size(${referenceEntity.PKVariableName});
				}

				<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "has" + referenceEntity.name + entity.name, [referenceEntity.PKClassName, entity.PKClassName], []) />

				/**
				<#list serviceBaseExceptions as exception>
				 * @throws ${exception}
				</#list>
				 */
				@Override
				public boolean has${referenceEntity.name}${entity.name}(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}, ${entity.PKClassName} ${entity.PKVariableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
					return ${referenceEntity.variableName}Persistence.contains${entity.name}(${referenceEntity.PKVariableName}, ${entity.PKVariableName});
				}

				<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "has" + referenceEntity.name + entity.pluralName, [referenceEntity.PKClassName], []) />

				/**
				<#list serviceBaseExceptions as exception>
				 * @throws ${exception}
				</#list>
				 */
				@Override
				public boolean has${referenceEntity.name}${entity.pluralName}(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
					return ${referenceEntity.variableName}Persistence.contains${entity.pluralName}(${referenceEntity.PKVariableName});
				}

				<#assign serviceBaseExceptions = serviceBuilder.getServiceBaseExceptions(methods, "set" + referenceEntity.name + entity.pluralName, [referenceEntity.PKClassName, entity.PKClassName + "[]"], []) />

				/**
				<#list serviceBaseExceptions as exception>
				 * @throws ${exception}
				</#list>
				 */
				@Override
				public void set${referenceEntity.name}${entity.pluralName}(${referenceEntity.PKClassName} ${referenceEntity.PKVariableName}, ${entity.PKClassName}[] ${entity.pluralPKVariableName}) <#if (serviceBaseExceptions?size gt 0)>throws ${stringUtil.merge(serviceBaseExceptions)} </#if>{
					${referenceEntity.variableName}Persistence.set${entity.pluralName}(${referenceEntity.PKVariableName}, ${entity.pluralPKVariableName});
				}
			</#if>
		</#list>
	</#if>

	<#if stringUtil.equals(sessionTypeName, "Local") && (entity.localizedEntity??) && entity.hasPersistence()>
		<#assign
			localizedEntity = entity.localizedEntity
			localizedEntityColumns = entity.localizedEntityColumns
			pkEntityColumn = entity.PKEntityColumns?first
		/>

		@Override
		public ${localizedEntity.name} fetch${localizedEntity.name}(${entity.PKClassName} ${entity.PKVariableName}, String languageId) {
			return ${localizedEntity.variableName}Persistence.fetchBy${pkEntityColumn.methodName}_LanguageId(${entity.PKVariableName}, languageId);
		}

		@Override
		public ${localizedEntity.name} get${localizedEntity.name}(${entity.PKClassName} ${entity.PKVariableName}, String languageId) throws PortalException {
			return ${localizedEntity.variableName}Persistence.findBy${pkEntityColumn.methodName}_LanguageId(${entity.PKVariableName}, languageId);
		}

		@Override
		public List<${localizedEntity.name}> get${localizedEntity.pluralName}(${entity.PKClassName} ${entity.PKVariableName}) {
			return ${localizedEntity.variableName}Persistence.findBy${pkEntityColumn.methodName}(${entity.PKVariableName});
		}

		<#assign entityVariableName = entity.variableName />

		<#if entity.versionEntity??>
			<#assign entityVariableName = "draft" + entity.name />
		</#if>

		@Override
		public ${localizedEntity.name} update${localizedEntity.name}(
			${entity.name} ${entityVariableName}, String languageId,
			<#list localizedEntityColumns as entityColumn>
				String ${entityColumn.name}

				<#if entityColumn?has_next>
					,
				</#if>
			</#list>
			) throws PortalException {

			${entityVariableName} = ${entity.variableName}Persistence.findByPrimaryKey(${entityVariableName}.getPrimaryKey());

			<#if entity.versionEntity??>
				if (${entityVariableName}.isHead()) {
					throw new IllegalArgumentException("Can only update draft entries " + ${entityVariableName}.getPrimaryKey());
				}
			</#if>

			${localizedEntity.name} ${localizedEntity.variableName} = ${localizedEntity.variableName}Persistence.fetchBy${pkEntityColumn.methodName}_LanguageId(${entityVariableName}.get${pkEntityColumn.methodName}(), languageId);

			return _update${localizedEntity.name}(${entityVariableName}, ${localizedEntity.variableName}, languageId,
				<#list localizedEntityColumns as entityColumn>
					${entityColumn.name}

					<#if entityColumn?has_next>
						,
					</#if>
				</#list>
			);
		}

		@Override
		public List<${localizedEntity.name}> update${localizedEntity.pluralName}(
			${entity.name} ${entityVariableName},
			<#list localizedEntityColumns as entityColumn>
				Map<String, String> ${entityColumn.name}Map

				<#if entityColumn?has_next>
					,
				</#if>
			</#list>
			) throws PortalException {

			${entityVariableName} = ${entity.variableName}Persistence.findByPrimaryKey(${entityVariableName}.getPrimaryKey());

			<#if entity.versionEntity??>
				if (${entityVariableName}.isHead()) {
					throw new IllegalArgumentException("Can only update draft entries " + ${entityVariableName}.getPrimaryKey());
				}
			</#if>

			Map<String, String[]> localizedValuesMap = new HashMap<String, String[]>();

			<#list localizedEntityColumns as entityColumn>
				for (Map.Entry<String, String> entry : ${entityColumn.name}Map.entrySet()) {
					String languageId = entry.getKey();

					String[] localizedValues = localizedValuesMap.get(languageId);

					if (localizedValues == null) {
						localizedValues = new String[${localizedEntityColumns?size}];

						localizedValuesMap.put(languageId, localizedValues);
					}

					localizedValues[${entityColumn?index}] = entry.getValue();
				}
			</#list>

			List<${localizedEntity.name}> ${localizedEntity.pluralVariableName} = new ArrayList<${localizedEntity.name}>(localizedValuesMap.size());

			for (${localizedEntity.name} ${localizedEntity.variableName} : ${localizedEntity.variableName}Persistence.findBy${pkEntityColumn.methodName}(${entityVariableName}.get${pkEntityColumn.methodName}())) {
				String[] localizedValues = localizedValuesMap.remove(${localizedEntity.variableName}.getLanguageId());

				if (localizedValues == null) {
					${localizedEntity.variableName}Persistence.remove(${localizedEntity.variableName});
				}
				else {
					<#if entity.versionEntity??>
						<#list entity.entityColumns as entityColumn>
							<#if !stringUtil.equals(entityColumn.name, "headId") && localizedEntity.hasEntityColumn(entityColumn.name) && !stringUtil.equals(entityColumn.name, "mvccVersion") && !stringUtil.equals(entityColumn.name, pkEntityColumn.name)>
								${localizedEntity.variableName}.set${entityColumn.methodName}(${entityVariableName}.get${entityColumn.methodName}());
							</#if>
						</#list>
					<#else>
						<#list entity.entityColumns as entityColumn>
							<#if localizedEntity.hasEntityColumn(entityColumn.name) && !stringUtil.equals(entityColumn.name, "mvccVersion") && !stringUtil.equals(entityColumn.name, pkEntityColumn.name)>
								${localizedEntity.variableName}.set${entityColumn.methodName}(${entityVariableName}.get${entityColumn.methodName}());
							</#if>
						</#list>
					</#if>

					<#list localizedEntityColumns as entityColumn>
						${localizedEntity.variableName}.set${entityColumn.methodName}(localizedValues[${entityColumn?index}]);
					</#list>

					${localizedEntity.pluralVariableName}.add(${localizedEntity.variableName}Persistence.update(${localizedEntity.variableName}));
				}
			}

			long batchCounter = counterLocalService.increment(${localizedEntity.name}.class.getName(), localizedValuesMap.size()) - localizedValuesMap.size();

			for (Map.Entry<String, String[]> entry : localizedValuesMap.entrySet()) {
				String languageId = entry.getKey();
				String[] localizedValues = entry.getValue();

				${localizedEntity.name} ${localizedEntity.variableName} = ${localizedEntity.variableName}Persistence.create(++batchCounter);

				<#if entity.versionEntity??>
					${localizedEntity.variableName}.setHeadId(${localizedEntity.variableName}.getPrimaryKey());

					<#list entity.entityColumns as entityColumn>
						<#if !stringUtil.equals(entityColumn.name, "headId") && localizedEntity.hasEntityColumn(entityColumn.name) && !stringUtil.equals(entityColumn.name, "mvccVersion")>
							${localizedEntity.variableName}.set${entityColumn.methodName}(${entityVariableName}.get${entityColumn.methodName}());
						</#if>
					</#list>
				<#else>
					<#list entity.entityColumns as entityColumn>
						<#if localizedEntity.hasEntityColumn(entityColumn.name) && !stringUtil.equals(entityColumn.name, "mvccVersion")>
							${localizedEntity.variableName}.set${entityColumn.methodName}(${entityVariableName}.get${entityColumn.methodName}());
						</#if>
					</#list>
				</#if>

				${localizedEntity.variableName}.setLanguageId(languageId);

				<#list localizedEntityColumns as entityColumn>
					${localizedEntity.variableName}.set${entityColumn.methodName}(localizedValues[${entityColumn?index}]);
				</#list>

				${localizedEntity.pluralVariableName}.add(${localizedEntity.variableName}Persistence.update(${localizedEntity.variableName}));
			}

			return ${localizedEntity.pluralVariableName};
		}

		private ${localizedEntity.name} _update${localizedEntity.name}(
			${entity.name} ${entityVariableName}, ${localizedEntity.name} ${localizedEntity.variableName}, String languageId,
			<#list localizedEntityColumns as entityColumn>
				String ${entityColumn.name}

				<#if entityColumn?has_next>
					,
				</#if>
			</#list>
			) throws PortalException {

			if (${localizedEntity.variableName} == null) {
				long ${localizedEntity.variableName}Id = counterLocalService.increment(${localizedEntity.name}.class.getName());

				${localizedEntity.variableName} = ${localizedEntity.variableName}Persistence.create(${localizedEntity.variableName}Id);

				${localizedEntity.variableName}.set${pkEntityColumn.methodName}(${entityVariableName}.get${pkEntityColumn.methodName}());
				${localizedEntity.variableName}.setLanguageId(languageId);
			}

			<#if entity.versionEntity??>
				${localizedEntity.variableName}.setHeadId(${localizedEntity.variableName}.getPrimaryKey());

				<#list entity.entityColumns as entityColumn>
					<#if localizedEntity.hasEntityColumn(entityColumn.name) && !stringUtil.equals(entityColumn.name, "mvccVersion") && !stringUtil.equals(entityColumn.name, "headId") && !stringUtil.equals(entityColumn.name, pkEntityColumn.name)>
						${localizedEntity.variableName}.set${entityColumn.methodName}(${entityVariableName}.get${entityColumn.methodName}());
					</#if>
				</#list>
			<#else>

				<#list entity.entityColumns as entityColumn>
					<#if localizedEntity.hasEntityColumn(entityColumn.name) && !stringUtil.equals(entityColumn.name, "mvccVersion") && !stringUtil.equals(entityColumn.name, pkEntityColumn.name)>
						${localizedEntity.variableName}.set${entityColumn.methodName}(${entityVariableName}.get${entityColumn.methodName}());
					</#if>
				</#list>
			</#if>

			<#list localizedEntityColumns as entityColumn>
				${localizedEntity.variableName}.set${entityColumn.methodName}(${entityColumn.name});
			</#list>

			return ${localizedEntity.variableName}Persistence.update(${localizedEntity.variableName});
		}
	</#if>

	<#if !dependencyInjectorDS>
		<#list referenceEntities as referenceEntity>
			<#if referenceEntity.hasLocalService()>
				/**
				 * Returns the ${referenceEntity.humanName} local service.
				 *
				 * @return the ${referenceEntity.humanName} local service
				 */

				<#if !classDeprecated && referenceEntity.isDeprecated()>
					@SuppressWarnings("deprecation")
				</#if>

				public ${referenceEntity.apiPackagePath}.service.${referenceEntity.name}LocalService get${referenceEntity.name}LocalService() {
					return ${referenceEntity.variableName}LocalService;
				}

				/**
				 * Sets the ${referenceEntity.humanName} local service.
				 *
				 * @param ${referenceEntity.variableName}LocalService the ${referenceEntity.humanName} local service
				 */

				<#if !classDeprecated && referenceEntity.isDeprecated()>
					@SuppressWarnings("deprecation")
				</#if>

				public void set${referenceEntity.name}LocalService(${referenceEntity.apiPackagePath}.service.${referenceEntity.name}LocalService ${referenceEntity.variableName}LocalService) {
					this.${referenceEntity.variableName}LocalService = ${referenceEntity.variableName}LocalService;
				}
			</#if>

			<#if !stringUtil.equals(sessionTypeName, "Local") && referenceEntity.hasRemoteService()>
				/**
				 * Returns the ${referenceEntity.humanName} remote service.
				 *
				 * @return the ${referenceEntity.humanName} remote service
				 */

				<#if !classDeprecated && referenceEntity.isDeprecated()>
					@SuppressWarnings("deprecation")
				</#if>

				public ${referenceEntity.apiPackagePath}.service.${referenceEntity.name}Service get${referenceEntity.name}Service() {
					return ${referenceEntity.variableName}Service;
				}

				/**
				 * Sets the ${referenceEntity.humanName} remote service.
				 *
				 * @param ${referenceEntity.variableName}Service the ${referenceEntity.humanName} remote service
				 */

				<#if !classDeprecated && referenceEntity.isDeprecated()>
					@SuppressWarnings("deprecation")
				</#if>

				public void set${referenceEntity.name}Service(${referenceEntity.apiPackagePath}.service.${referenceEntity.name}Service ${referenceEntity.variableName}Service) {
					this.${referenceEntity.variableName}Service = ${referenceEntity.variableName}Service;
				}
			</#if>

			<#if referenceEntity.hasEntityColumns() && referenceEntity.hasPersistence()>
				/**
				 * Returns the ${referenceEntity.humanName} persistence.
				 *
				 * @return the ${referenceEntity.humanName} persistence
				 */
				public ${referenceEntity.name}Persistence get${referenceEntity.name}Persistence() {
					return ${referenceEntity.variableName}Persistence;
				}

				/**
				 * Sets the ${referenceEntity.humanName} persistence.
				 *
				 * @param ${referenceEntity.variableName}Persistence the ${referenceEntity.humanName} persistence
				 */
				public void set${referenceEntity.name}Persistence(${referenceEntity.name}Persistence ${referenceEntity.variableName}Persistence) {
					this.${referenceEntity.variableName}Persistence = ${referenceEntity.variableName}Persistence;
				}
			</#if>

			<#if referenceEntity.hasFinderClassName() && (stringUtil.equals(entity.name, "Counter") || !stringUtil.equals(referenceEntity.name, "Counter"))>
				/**
				 * Returns the ${referenceEntity.humanName} finder.
				 *
				 * @return the ${referenceEntity.humanName} finder
				 */
				public ${referenceEntity.name}Finder get${referenceEntity.name}Finder() {
					return ${referenceEntity.variableName}Finder;
				}

				/**
				 * Sets the ${referenceEntity.humanName} finder.
				 *
				 * @param ${referenceEntity.variableName}Finder the ${referenceEntity.humanName} finder
				 */
				public void set${referenceEntity.name}Finder(${referenceEntity.name}Finder ${referenceEntity.variableName}Finder) {
					this.${referenceEntity.variableName}Finder = ${referenceEntity.variableName}Finder;
				}
			</#if>
		</#list>
	</#if>

	<#assign
		lazyBlobExists = entity.hasLazyBlobEntityColumn() && stringUtil.equals(sessionTypeName, "Local") && entity.hasPersistence()
		localizedEntityExists = stringUtil.equals(sessionTypeName, "Local") && entity.localizedEntity?? && entity.versionEntity?? && entity.hasPersistence()
	/>

	<#if lazyBlobExists>
		<#list entity.blobEntityColumns as entityColumn>
			<#if entityColumn.lazy>
				@Override
				public ${entity.name}${entityColumn.methodName}BlobModel get${entityColumn.methodName}BlobModel(Serializable primaryKey) {
					Session session = null;

					try {
						session = ${entity.variableName}Persistence.openSession();

						return (${apiPackagePath}.model.${entity.name}${entityColumn.methodName}BlobModel)session.get(${entity.name}${entityColumn.methodName}BlobModel.class, primaryKey);
					}
					catch (Exception exception) {
						throw ${entity.variableName}Persistence.processException(exception);
					}
					finally {
						${entity.variableName}Persistence.closeSession(session);
					}
				}

				@Override
				@Transactional(readOnly = true)
				public InputStream open${entityColumn.methodName}InputStream(<#if entity.hasCompoundPK()>Serializable<#else>long</#if> ${entity.PKVariableName}) {
					try {
						${entity.name}${entityColumn.methodName}BlobModel
							${entity.name}${entityColumn.methodName}BlobModel = get${entityColumn.methodName}BlobModel(
								${entity.PKVariableName});

						Blob blob = ${entity.name}${entityColumn.methodName}BlobModel.get${entityColumn.methodName}Blob();

						if (blob == null) {
							return _EMPTY_INPUT_STREAM;
						}

						InputStream inputStream = blob.getBinaryStream();

						if (_useTempFile) {
							inputStream = new AutoDeleteFileInputStream(
								_file.createTempFile(inputStream));
						}

						return inputStream;
					}
					catch (Exception exception) {
						throw new SystemException(exception);
					}
				}
			</#if>
		</#list>
	</#if>

	<#if !dependencyInjectorDS>
		public void afterPropertiesSet() {
			<#if serviceBuilder.isVersionLTE_7_3_0()>
				<#if stringUtil.equals(sessionTypeName, "Local") && entity.hasEntityColumns() && entity.hasPersistence()>
					<#if validator.isNotNull(pluginName)>
						PersistedModelLocalServiceRegistryUtil.register("${apiPackagePath}.model.${entity.name}", ${entity.variableName}LocalService);
					<#else>
						persistedModelLocalServiceRegistry.register("${apiPackagePath}.model.${entity.name}", ${entity.variableName}LocalService);
					</#if>
				</#if>
			</#if>

			<#if localizedEntityExists>
				<#assign localizedEntity = entity.localizedEntity />

				registerListener(new ${localizedEntity.name}VersionServiceListener());
			</#if>

			<#if lazyBlobExists>
				DB db = DBManagerUtil.getDB();

				if ((db.getDBType() != DBType.DB2) &&
					(db.getDBType() != DBType.MYSQL) &&
					(db.getDBType() != DBType.MARIADB)

					<#if serviceBuilder.isVersionLTE_7_3_0()>
						&& (db.getDBType() != DBType.SYBASE)
					</#if>

					) {

					_useTempFile = true;
				}
			</#if>

			${entity.name}${sessionTypeName}ServiceUtil.setService(${entity.variableName}${sessionTypeName}Service);
		}
	</#if>

	<#if dependencyInjectorDS>
		<#if lazyBlobExists || localizedEntityExists>
			@Activate
			protected void activate() {
				<#if localizedEntityExists>
					<#assign localizedEntity = entity.localizedEntity />

					registerListener(new ${localizedEntity.name}VersionServiceListener());
				</#if>

				<#if lazyBlobExists>
					DB db = DBManagerUtil.getDB();

					if ((db.getDBType() != DBType.DB2) &&
						(db.getDBType() != DBType.MYSQL) &&
						(db.getDBType() != DBType.MARIADB)

						<#if serviceBuilder.isVersionLTE_7_3_0()>
							&& (db.getDBType() != DBType.SYBASE)
						</#if>

						) {

						_useTempFile = true;
					}
				</#if>
			}
		</#if>

		@Deactivate
		protected void deactivate() {
			<#if serviceBuilder.isVersionLTE_7_3_0()>
				${entity.name}${sessionTypeName}ServiceUtil.setService(null);
			</#if>
		}

		@Override
		public Class<?>[] getAopInterfaces() {
			return new Class<?>[] {
				${entity.name}${sessionTypeName}Service.class, IdentifiableOSGiService.class

				<#if stringUtil.equals(sessionTypeName, "Local") && entity.hasEntityColumns() && entity.hasPersistence()>
					<#if entity.isChangeTrackingEnabled()>
						, CTService.class
					</#if>

					, PersistedModelLocalService.class
				</#if>
			};
		}

		@Override
		public void setAopProxy(Object aopProxy) {
			${entity.variableName}${sessionTypeName}Service = (${entity.name}${sessionTypeName}Service)aopProxy;

			<#if serviceBuilder.isVersionLTE_7_3_0()>
				${entity.name}${sessionTypeName}ServiceUtil.setService(${entity.variableName}${sessionTypeName}Service);
			</#if>
		}
	<#else>
		public void destroy() {
			<#if serviceBuilder.isVersionLTE_7_3_0()>
				<#if stringUtil.equals(sessionTypeName, "Local") && entity.hasEntityColumns() && entity.hasPersistence()>
					<#if validator.isNotNull(pluginName)>
						PersistedModelLocalServiceRegistryUtil.unregister("${apiPackagePath}.model.${entity.name}");
					<#else>
						persistedModelLocalServiceRegistry.unregister("${apiPackagePath}.model.${entity.name}");
					</#if>
				</#if>
			</#if>

			${entity.name}${sessionTypeName}ServiceUtil.setService(null);
		}
	</#if>

	<#if stringUtil.equals(sessionTypeName, "Local") && entity.versionEntity?? && entity.hasPersistence()>
		<#assign
			versionEntity = entity.versionEntity
			pkEntityMethod = entity.PKEntityColumns?first.methodName
		/>

		@Indexable(type = IndexableType.REINDEX)
		@Override
		public ${entity.name} checkout(${entity.name} published${entity.name}, int version) throws PortalException {
			if (!published${entity.name}.isHead()) {
				throw new IllegalArgumentException("Unable to checkout with unpublished changes " + published${entity.name}.getHeadId());
			}

			${entity.name} draft${entity.name} = ${entity.variableName}Persistence.fetchByHeadId(published${entity.name}.getPrimaryKey());

			if (draft${entity.name} != null) {
				throw new IllegalArgumentException("Unable to checkout with unpublished changes " + published${entity.name}.getPrimaryKey());
			}

			${versionEntity.name} ${versionEntity.variableName} = getVersion(published${entity.name}, version);

			draft${entity.name} = _createDraft(published${entity.name});

			${versionEntity.variableName}.populateVersionedModel(draft${entity.name});

			draft${entity.name} = ${entity.variableName}Persistence.update(draft${entity.name});

			for (VersionServiceListener<${entity.name}, ${versionEntity.name}> versionServiceListener : _versionServiceListeners) {
				versionServiceListener.afterCheckout(draft${entity.name}, version);
			}

			return draft${entity.name};
		}

		@Indexable(type = IndexableType.DELETE)
		@Override
		public ${entity.name} delete(${entity.name} published${entity.name}) throws PortalException {
			if (!published${entity.name}.isHead()) {
				throw new IllegalArgumentException("${entity.name} is a draft " + published${entity.name}.getPrimaryKey());
			}

			${entity.name} draft${entity.name} = ${entity.variableName}Persistence.fetchByHeadId(published${entity.name}.getPrimaryKey());

			if (draft${entity.name} != null) {
				deleteDraft(draft${entity.name});
			}

			for (${versionEntity.name} ${versionEntity.variableName} : getVersions(published${entity.name})) {
				${versionEntity.variableName}Persistence.remove(${versionEntity.variableName});
			}

			${entity.variableName}Persistence.remove(published${entity.name});

			for (VersionServiceListener<${entity.name}, ${versionEntity.name}> versionServiceListener : _versionServiceListeners) {
				versionServiceListener.afterDelete(published${entity.name});
			}

			return published${entity.name};
		}

		@Indexable(type = IndexableType.DELETE)
		@Override
		public ${entity.name} deleteDraft(${entity.name} draft${entity.name})
			throws PortalException {

			if (draft${entity.name}.isHead()) {
				throw new IllegalArgumentException("${entity.name} is not a draft " + draft${entity.name}.getPrimaryKey());
			}

			${entity.variableName}Persistence.remove(draft${entity.name});

			for (VersionServiceListener<${entity.name}, ${versionEntity.name}> versionServiceListener : _versionServiceListeners) {
				versionServiceListener.afterDeleteDraft(draft${entity.name});
			}

			return draft${entity.name};
		}

		@Override
		public ${versionEntity.name} deleteVersion(${versionEntity.name} ${versionEntity.variableName}) throws PortalException {
			${versionEntity.name} latest${versionEntity.name} = ${versionEntity.variableName}Persistence.findBy${pkEntityMethod}_First(${versionEntity.variableName}.getVersionedModelId(), null);

			if (latest${versionEntity.name}.getVersion() == ${versionEntity.variableName}.getVersion()) {
				throw new IllegalArgumentException("Unable to delete latest version " + ${versionEntity.variableName}.getVersion());
			}

			${versionEntity.variableName} = ${versionEntity.variableName}Persistence.remove(${versionEntity.variableName});

			for (VersionServiceListener<${entity.name}, ${versionEntity.name}> versionServiceListener : _versionServiceListeners) {
				versionServiceListener.afterDeleteVersion(${versionEntity.variableName});
			}

			return ${versionEntity.variableName};
		}

		@Override
		public ${entity.name} fetchDraft(${entity.name} ${entity.variableName}) {
			if (${entity.variableName}.isHead()) {
				return ${entity.variableName}Persistence.fetchByHeadId(${entity.variableName}.getPrimaryKey());
			}

			return ${entity.variableName};
		}

		@Override
		public ${entity.name} fetchDraft(long primaryKey) {
			return ${entity.variableName}Persistence.fetchByHeadId(primaryKey);
		}

		@Override
		public ${versionEntity.name} fetchLatestVersion(${entity.name} ${entity.variableName}) {
			long primaryKey = ${entity.variableName}.getHeadId();

			if (${entity.variableName}.isHead()) {
				primaryKey = ${entity.variableName}.getPrimaryKey();
			}

			return ${versionEntity.variableName}Persistence.fetchBy${pkEntityMethod}_First(primaryKey, null);
		}

		@Override
		public ${entity.name} fetchPublished(${entity.name} ${entity.variableName}) {
			if (${entity.variableName}.isHead()) {
				return ${entity.variableName};
			}

			if (${entity.variableName}.getHeadId() == ${entity.variableName}.getPrimaryKey()) {
				return null;
			}

			return ${entity.variableName}Persistence.fetchByPrimaryKey(${entity.variableName}.getHeadId());
		}

		@Override
		public ${entity.name} fetchPublished(long primaryKey) {
			${entity.name} ${entity.variableName} = ${entity.variableName}Persistence.fetchByPrimaryKey(primaryKey);

			if ((${entity.variableName} == null) || (${entity.variableName}.getHeadId() == ${entity.variableName}.getPrimaryKey())) {
				return null;
			}

			return ${entity.variableName};
		}

		@Override
		public ${entity.name} getDraft(${entity.name} ${entity.variableName}) throws PortalException {
			if (!${entity.variableName}.isHead()) {
				return ${entity.variableName};
			}

			${entity.name} draft${entity.name} = ${entity.variableName}Persistence.fetchByHeadId(${entity.variableName}.getPrimaryKey());

			if (draft${entity.name} == null) {
				draft${entity.name} = ${entity.variableName}LocalService.updateDraft(_createDraft(${entity.variableName}));
			}

			return draft${entity.name};
		}

		@Override
		public ${entity.name} getDraft(long primaryKey) throws PortalException {
			${entity.name} draft${entity.name} = ${entity.variableName}Persistence.fetchByHeadId(primaryKey);

			if (draft${entity.name} == null) {
				${entity.name} ${entity.variableName} = ${entity.variableName}Persistence.findByPrimaryKey(primaryKey);

				draft${entity.name} = ${entity.variableName}LocalService.updateDraft(_createDraft(${entity.variableName}));
			}

			return draft${entity.name};
		}

		@Override
		public ${versionEntity.name} getVersion(${entity.name} ${entity.variableName}, int version) throws PortalException {
			long primaryKey = ${entity.variableName}.getHeadId();

			if (${entity.variableName}.isHead()) {
				primaryKey = ${entity.variableName}.getPrimaryKey();
			}

			return ${versionEntity.variableName}Persistence.findBy${pkEntityMethod}_Version(primaryKey, version);
		}

		@Override
		public List<${versionEntity.name}> getVersions(${entity.name} ${entity.variableName}) {
			long primaryKey = ${entity.variableName}.getPrimaryKey();

			if (!${entity.variableName}.isHead()) {
				if (${entity.variableName}.getHeadId() == ${entity.variableName}.getPrimaryKey()) {
					return Collections.emptyList();
				}

				primaryKey = ${entity.variableName}.getHeadId();
			}

			return ${versionEntity.variableName}Persistence.findBy${pkEntityMethod}(primaryKey);
		}

		@Indexable(type = IndexableType.REINDEX)
		@Override
		public ${entity.name} publishDraft(${entity.name} draft${entity.name}) throws PortalException {
			if (draft${entity.name}.isHead()) {
				throw new IllegalArgumentException("Can only publish drafts " + draft${entity.name}.getPrimaryKey());
			}

			${entity.name} head${entity.name} = null;

			int version = 1;

			if (draft${entity.name}.getHeadId() == draft${entity.name}.getPrimaryKey()) {
				head${entity.name} = create();

				draft${entity.name}.setHeadId(head${entity.name}.getPrimaryKey());
			}
			else {
				head${entity.name} = ${entity.variableName}Persistence.findByPrimaryKey(draft${entity.name}.getHeadId());

				${versionEntity.name} latest${versionEntity.name} = ${versionEntity.variableName}Persistence.findBy${pkEntityMethod}_First(draft${entity.name}.getHeadId(), null);

				version = latest${versionEntity.name}.getVersion() + 1;
			}

			${versionEntity.name} ${versionEntity.variableName} = ${versionEntity.variableName}Persistence.create(counterLocalService.increment(${versionEntity.name}.class.getName()));

			${versionEntity.variableName}.setVersion(version);
			${versionEntity.variableName}.setVersionedModelId(head${entity.name}.getPrimaryKey());

			draft${entity.name}.populateVersionModel(${versionEntity.variableName});

			${versionEntity.variableName}Persistence.update(${versionEntity.variableName});

			${versionEntity.variableName}.populateVersionedModel(head${entity.name});

			head${entity.name}.setHeadId(-head${entity.name}.getPrimaryKey());

			head${entity.name} = ${entity.variableName}Persistence.update(head${entity.name});

			for (VersionServiceListener<${entity.name}, ${versionEntity.name}> versionServiceListener : _versionServiceListeners) {
				versionServiceListener.afterPublishDraft(draft${entity.name}, version);
			}

			deleteDraft(draft${entity.name});

			return head${entity.name};
		}

		@Override
		public void registerListener(VersionServiceListener<${entity.name}, ${versionEntity.name}> versionServiceListener) {
			_versionServiceListeners.add(versionServiceListener);
		}

		@Override
		public void unregisterListener(VersionServiceListener<${entity.name}, ${versionEntity.name}> versionServiceListener) {
			_versionServiceListeners.remove(versionServiceListener);
		}

		@Indexable(type = IndexableType.REINDEX)
		@Override
		public ${entity.name} updateDraft(${entity.name} draft${entity.name}) throws PortalException {
			if (draft${entity.name}.isHead()) {
				throw new IllegalArgumentException("Can only update draft entries " + draft${entity.name}.getPrimaryKey());
			}

			${entity.name} previous${entity.name} = ${entity.variableName}Persistence.fetchByPrimaryKey(draft${entity.name}.getPrimaryKey());

			draft${entity.name} = ${entity.variableName}Persistence.update(draft${entity.name});

			if (previous${entity.name} == null) {
				for (VersionServiceListener<${entity.name}, ${versionEntity.name}> versionServiceListener : _versionServiceListeners) {
					versionServiceListener.afterCreateDraft(draft${entity.name});
				}
			}
			else {
				for (VersionServiceListener<${entity.name}, ${versionEntity.name}> versionServiceListener : _versionServiceListeners) {
					versionServiceListener.afterUpdateDraft(draft${entity.name});
				}
			}

			return draft${entity.name};
		}

		private ${entity.name} _createDraft(${entity.name} published${entity.name}) throws PortalException {
			${entity.name} draft${entity.name} = create();

			<#list entity.entityColumns as entityColumn>
				<#if stringUtil.equals(entityColumn.methodName, "HeadId")>
					draft${entity.name}.setHeadId(published${entity.name}.getPrimaryKey());
				<#elseif !entityColumn.isPrimary() && !stringUtil.equals(entityColumn.methodName, "MvccVersion") && !entityColumn.isMappingManyToMany()>
					draft${entity.name}.set${entityColumn.methodName}(published${entity.name}.get${entityColumn.methodName}());
				</#if>
			</#list>

			draft${entity.name}.resetOriginalValues();

			return draft${entity.name};
		}

		private final Set<VersionServiceListener<${entity.name}, ${versionEntity.name}>> _versionServiceListeners = Collections.newSetFromMap(new ConcurrentHashMap<VersionServiceListener<${entity.name}, ${versionEntity.name}>, Boolean>());

	</#if>

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		<#if stringUtil.equals(sessionTypeName, "Local")>
			return ${entity.name}LocalService.class.getName();
		<#else>
			return ${entity.name}Service.class.getName();
		</#if>
	}

	<#if entity.hasEntityColumns()>
		<#if entity.hasPersistence() && entity.isChangeTrackingEnabled() && stringUtil.equals(sessionTypeName, "Local")>
			@Override
			public CTPersistence<${entity.name}> getCTPersistence() {
				return ${entity.variableName}Persistence;
			}

			@Override
			public Class<${entity.name}> getModelClass() {
				return ${entity.name}.class;
			}

			@Override
			public <R, E extends Throwable> R updateWithUnsafeFunction(UnsafeFunction<CTPersistence<${entity.name}>, R, E> updateUnsafeFunction) throws E {
				return updateUnsafeFunction.apply(${entity.variableName}Persistence);
			}
		<#else>
			protected Class<?> getModelClass() {
				return ${entity.name}.class;
			}
		</#if>

		protected String getModelClassName() {
			return ${entity.name}.class.getName();
		}
	</#if>

	<#if entity.hasPersistence()>
		/**
		 * Performs a SQL query.
		 *
		 * @param sql the sql query
		 */
		protected void runSQL(String sql) {
			<#if entity.hasEntityColumns()>
				DataSource dataSource = ${entity.variableName}Persistence.getDataSource();
			<#else>
				DataSource dataSource = InfrastructureUtil.getDataSource();
			</#if>

			DB db = DBManagerUtil.getDB();

			Connection currentConnection = CurrentConnectionUtil.getConnection(dataSource);

			try {
				if (currentConnection != null) {
					db.runSQL(currentConnection, new String[] {sql});

					return;
				}

				try (Connection connection = dataSource.getConnection()) {
					db.runSQL(connection, new String[] {sql});
				}
			}
			catch (Exception exception) {
				throw new SystemException(exception);
			}
		}
	</#if>

	<#list referenceEntities as referenceEntity>
		<#if referenceEntity.hasLocalService()>
			<#if !dependencyInjectorDS || (referenceEntity.apiPackagePath != apiPackagePath) || (entity == referenceEntity)>
				<#if dependencyInjectorDS>
					<#if !stringUtil.equals(sessionTypeName, "Local") || (entity != referenceEntity)>
						@Reference
					</#if>
				<#elseif osgiModule && (referenceEntity.apiPackagePath != apiPackagePath)>
					@ServiceReference(type = ${referenceEntity.apiPackagePath}.service.${referenceEntity.name}LocalService.class)
				<#else>
					@BeanReference(type = ${referenceEntity.apiPackagePath}.service.${referenceEntity.name}LocalService.class)
				</#if>

				<#if !classDeprecated && referenceEntity.isDeprecated()>
					@SuppressWarnings("deprecation")
				</#if>

				protected ${referenceEntity.apiPackagePath}.service.${referenceEntity.name}LocalService ${referenceEntity.variableName}LocalService;
			</#if>
		</#if>

		<#if !stringUtil.equals(sessionTypeName, "Local") && referenceEntity.hasRemoteService()>
			<#if !dependencyInjectorDS || (referenceEntity.apiPackagePath != apiPackagePath) || (entity == referenceEntity)>
				<#if dependencyInjectorDS>
					<#if entity != referenceEntity>
						@Reference
					</#if>
				<#elseif osgiModule && (referenceEntity.apiPackagePath != apiPackagePath)>
					@ServiceReference(type = ${referenceEntity.apiPackagePath}.service.${referenceEntity.name}Service.class)
				<#else>
					@BeanReference(type = ${referenceEntity.apiPackagePath}.service.${referenceEntity.name}Service.class)
				</#if>

				<#if !classDeprecated && referenceEntity.isDeprecated()>
					@SuppressWarnings("deprecation")
				</#if>

				protected ${referenceEntity.apiPackagePath}.service.${referenceEntity.name}Service ${referenceEntity.variableName}Service;
			</#if>
		</#if>

		<#if referenceEntity.hasEntityColumns() && referenceEntity.hasPersistence()>
			<#if !dependencyInjectorDS || (referenceEntity.apiPackagePath == apiPackagePath)>
				<#if dependencyInjectorDS>
					@Reference
				<#elseif osgiModule && (referenceEntity.apiPackagePath != apiPackagePath)>
					@ServiceReference(type = ${referenceEntity.name}Persistence.class)
				<#else>
					@BeanReference(type = ${referenceEntity.name}Persistence.class)
				</#if>

				protected ${referenceEntity.name}Persistence ${referenceEntity.variableName}Persistence;
			</#if>
		</#if>

		<#if referenceEntity.hasFinderClassName() && (stringUtil.equals(entity.name, "Counter") || !stringUtil.equals(referenceEntity.name, "Counter"))>
			<#if !dependencyInjectorDS || (referenceEntity.apiPackagePath == apiPackagePath)>
				<#if dependencyInjectorDS>
					@Reference
				<#elseif osgiModule && (referenceEntity.apiPackagePath != apiPackagePath)>
					@ServiceReference(type = ${referenceEntity.name}Finder.class)
				<#else>
					@BeanReference(type = ${referenceEntity.name}Finder.class)
				</#if>

				protected ${referenceEntity.name}Finder ${referenceEntity.variableName}Finder;
			</#if>
		</#if>
	</#list>

	<#list entity.entityColumns as entityColumn>
		<#if entityColumn.isCollection() && entityColumn.isMappingManyToMany()>
			<#assign referenceEntity = serviceBuilder.getEntity(entityColumn.entityName) />

			<#if !referenceEntities?seq_contains(referenceEntity) && referenceEntity.hasEntityColumns() && referenceEntity.hasPersistence()>
				<#if !dependencyInjectorDS || (referenceEntity.apiPackagePath == apiPackagePath)>
					<#if dependencyInjectorDS>
						@Reference
					<#elseif osgiModule && (referenceEntity.apiPackagePath != apiPackagePath)>
						@ServiceReference(type = ${referenceEntity.name}Persistence.class)
					<#else>
						@BeanReference(type = ${referenceEntity.name}Persistence.class)
					</#if>
					protected ${referenceEntity.name}Persistence ${referenceEntity.variableName}Persistence;
				</#if>
			</#if>
		</#if>
	</#list>

	private static final Log _log = LogFactoryUtil.getLog(${entity.name}${sessionTypeName}ServiceBaseImpl.class);

	<#if lazyBlobExists>
		<#if dependencyInjectorDS>
			@Reference
		<#else>
			@BeanReference(type = File.class)
		</#if>
		protected File _file;

		private static final InputStream _EMPTY_INPUT_STREAM = new UnsyncByteArrayInputStream(new byte[0]);

		private boolean _useTempFile;
	</#if>

	<#if serviceBuilder.isVersionLTE_7_3_0()>
		<#if stringUtil.equals(sessionTypeName, "Local") && entity.hasEntityColumns() && entity.hasPersistence() && !dependencyInjectorDS>
			<#if validator.isNull(pluginName)>
				<#if osgiModule>
					@ServiceReference(type = PersistedModelLocalServiceRegistry.class)
				<#else>
					@BeanReference(type = PersistedModelLocalServiceRegistry.class)
				</#if>

				protected PersistedModelLocalServiceRegistry persistedModelLocalServiceRegistry;
			</#if>
		</#if>
	</#if>

	<#if stringUtil.equals(sessionTypeName, "Local") && entity.localizedEntity?? && entity.versionEntity?? && entity.hasPersistence()>
		<#assign
			localizedEntity = entity.localizedEntity
			versionEntity = entity.versionEntity
			localizedVersionEntity = localizedEntity.versionEntity
			pkEntityMethod = entity.PKEntityColumns?first.methodName
		/>

		private class ${localizedEntity.name}VersionServiceListener implements VersionServiceListener<${entity.name}, ${versionEntity.name}> {

			@Override
			public void afterCheckout(${entity.name} draft${entity.name}, int version) throws PortalException {
				Map<String, ${localizedEntity.name}> published${localizedEntity.name}Map = new HashMap<String, ${localizedEntity.name}>();

				for (${localizedEntity.name} published${localizedEntity.name} : ${localizedEntity.variableName}Persistence.findBy${pkEntityMethod}(draft${entity.name}.getHeadId())) {
					published${localizedEntity.name}Map.put(published${localizedEntity.name}.getLanguageId(), published${localizedEntity.name});
				}

				List<${localizedVersionEntity.name}> ${localizedVersionEntity.pluralVariableName} = ${localizedVersionEntity.variableName}Persistence.findBy${pkEntityMethod}_Version(draft${entity.name}.getHeadId(), version);

				long ${localizedVersionEntity.variableName}BatchCounter = counterLocalService.increment(${localizedVersionEntity.name}.class.getName(), ${localizedVersionEntity.pluralVariableName}.size()) - ${localizedVersionEntity.pluralVariableName}.size();

				for (${localizedVersionEntity.name} ${localizedVersionEntity.variableName} : ${localizedVersionEntity.pluralVariableName}) {
					${localizedEntity.name} draft${localizedEntity.name} = ${localizedEntity.variableName}Persistence.create(++${localizedVersionEntity.variableName}BatchCounter);

					long headId = draft${localizedEntity.name}.getPrimaryKey();

					${localizedEntity.name} published${localizedEntity.name} = published${localizedEntity.name}Map.get(${localizedVersionEntity.variableName}.getLanguageId());

					if (published${localizedEntity.name} != null) {
						headId = published${localizedEntity.name}.getPrimaryKey();
					}

					draft${localizedEntity.name}.setHeadId(headId);

					draft${localizedEntity.name}.set${pkEntityMethod}(draft${entity.name}.getPrimaryKey());
					draft${localizedEntity.name}.setLanguageId(${localizedVersionEntity.variableName}.getLanguageId());

					<#list localizedEntityColumns as entityColumn>
						draft${localizedEntity.name}.set${entityColumn.methodName}(${localizedVersionEntity.variableName}.get${entityColumn.methodName}());
					</#list>

					${localizedEntity.variableName}Persistence.update(draft${localizedEntity.name});
				}
			}

			@Override
			public void afterCreateDraft(${entity.name} draft${entity.name}) throws PortalException {
				if (draft${entity.name}.getHeadId() == draft${entity.name}.getPrimaryKey()) {
					return;
				}

				for (${localizedEntity.name} published${localizedEntity.name} : ${localizedEntity.variableName}Persistence.findBy${pkEntityMethod}(draft${entity.name}.getHeadId())) {
					_update${localizedEntity.name}(
						draft${entity.name}, null, published${localizedEntity.name}.getLanguageId(),
							<#list localizedEntityColumns as entityColumn>
								published${localizedEntity.name}.get${entityColumn.methodName}()

								<#if entityColumn?has_next>
									,
								</#if>
							</#list>
						);
				}
			}

			@Override
			public void afterDelete(${entity.name} published${entity.name}) throws PortalException {
			}

			@Override
			public void afterDeleteDraft(${entity.name} draft${entity.name}) throws PortalException {
			}

			@Override
			public void afterDeleteVersion(${versionEntity.name} ${versionEntity.variableName}) throws PortalException {
			}

			@Override
			public void afterPublishDraft(${entity.name} draft${entity.name}, int version) throws PortalException {
				Map<String, ${localizedEntity.name}> draft${localizedEntity.name}Map = new HashMap<String, ${localizedEntity.name}>();

				for (${localizedEntity.name} draft${localizedEntity.name} : ${localizedEntity.variableName}Persistence.findBy${pkEntityMethod}(draft${entity.name}.getPrimaryKey())) {
					draft${localizedEntity.name}Map.put(draft${localizedEntity.name}.getLanguageId(), draft${localizedEntity.name});
				}

				long ${localizedVersionEntity.variableName}BatchCounter = counterLocalService.increment(${localizedVersionEntity.name}.class.getName(), draft${localizedEntity.name}Map.size()) - draft${localizedEntity.name}Map.size();

				for (${localizedEntity.name} published${localizedEntity.name} : ${localizedEntity.variableName}Persistence.findBy${pkEntityMethod}(draft${entity.name}.getHeadId())) {
					${localizedEntity.name} draft${localizedEntity.name} = draft${localizedEntity.name}Map.remove(published${localizedEntity.name}.getLanguageId());

					if (draft${localizedEntity.name} == null) {
						${localizedEntity.variableName}Persistence.remove(published${localizedEntity.name});
					}
					else {
						published${localizedEntity.name}.setHeadId(-published${localizedEntity.name}.getPrimaryKey());

						<#list localizedEntityColumns as entityColumn>
							published${localizedEntity.name}.set${entityColumn.methodName}(draft${localizedEntity.name}.get${entityColumn.methodName}());
						</#list>

						${localizedEntity.variableName}Persistence.update(published${localizedEntity.name});

						_publish${localizedVersionEntity.name}(published${localizedEntity.name}, ++${localizedVersionEntity.variableName}BatchCounter, version);
					}
				}

				long ${localizedEntity.variableName}BatchCounter = counterLocalService.increment(${localizedEntity.name}.class.getName(), draft${localizedEntity.name}Map.size()) - draft${localizedEntity.name}Map.size();

				for (${localizedEntity.name} draft${localizedEntity.name} : draft${localizedEntity.name}Map.values()) {
					${localizedEntity.name} ${localizedEntity.variableName} = ${localizedEntity.variableName}Persistence.create(++${localizedEntity.variableName}BatchCounter);

					${localizedEntity.variableName}.setHeadId(${localizedEntity.variableName}.getPrimaryKey());
					${localizedEntity.variableName}.set${pkEntityMethod}(draft${entity.name}.getHeadId());
					${localizedEntity.variableName}.setLanguageId(draft${localizedEntity.name}.getLanguageId());

					<#list localizedEntityColumns as entityColumn>
						${localizedEntity.variableName}.set${entityColumn.methodName}(draft${localizedEntity.name}.get${entityColumn.methodName}());
					</#list>

					${localizedEntity.variableName}Persistence.update(${localizedEntity.variableName});

					_publish${localizedVersionEntity.name}(${localizedEntity.variableName}, ++${localizedVersionEntity.variableName}BatchCounter, version);
				}
			}

			@Override
			public void afterUpdateDraft(${entity.name} draft${entity.name}) {
			}

			private void _publish${localizedVersionEntity.name}(${localizedEntity.name} ${localizedEntity.variableName}, long primaryKey, int version) {
				${localizedVersionEntity.name} ${localizedVersionEntity.variableName} = ${localizedVersionEntity.variableName}Persistence.create(primaryKey);

				${localizedVersionEntity.variableName}.setVersion(version);
				${localizedVersionEntity.variableName}.setVersionedModelId(${localizedEntity.variableName}.getPrimaryKey());

				${localizedEntity.variableName}.populateVersionModel(${localizedVersionEntity.variableName});

				${localizedVersionEntity.variableName}Persistence.update(${localizedVersionEntity.variableName});
			}

		}
	</#if>
}