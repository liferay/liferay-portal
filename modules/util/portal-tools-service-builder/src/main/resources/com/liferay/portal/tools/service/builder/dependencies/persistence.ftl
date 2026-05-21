package ${apiPackagePath}.service.persistence;

import ${serviceBuilder.getCompatJavaClassName("ProviderType")};

<#assign noSuchEntity = serviceBuilder.getNoSuchEntityException(entity) />

import ${apiPackagePath}.exception.${noSuchEntity}Exception;
import ${apiPackagePath}.model.${entity.name};

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * The persistence interface for the ${entity.humanName} service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author ${author}
 * @see ${entity.name}Util
<#if classDeprecated>
 * @deprecated ${classDeprecatedComment}
</#if>
 * @generated
 */

<#if classDeprecated>
	@Deprecated
</#if>

@ProviderType
public interface ${entity.name}Persistence extends BasePersistence<${entity.name}>
	<#if entity.isChangeTrackingEnabled()>
		, CTPersistence<${entity.name}>
	</#if>

	{

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ${entity.name}Util} to access the ${entity.humanName} persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	<#if serviceBuilder.isVersionLTE_7_1_0()>
		@Override
		public Map<Serializable, ${entity.name}> fetchByPrimaryKeys(Set<Serializable> primaryKeys);
	</#if>

	<#list methods as method>
		<#if method.isPublic() && serviceBuilder.isCustomMethod(method) && !serviceBuilder.isBasePersistenceMethod(method) && !stringUtil.equals(method.name, "fetchByPrimaryKeys")>
			${serviceBuilder.getJavadocComment(method)}

			<#if serviceBuilder.hasAnnotation(method, "Deprecated")>
				@Deprecated
			</#if>

			<#assign parameters = method.parameters />

			<#if stringUtil.equals(method.name, "getBadColumnNames")>
				@Override
			</#if>

			public ${serviceBuilder.getTypeGenericsName(method.returns)} ${method.name} (

			<#list parameters as parameter>
				${serviceBuilder.getTypeGenericsName(parameter.type)} ${parameter.name}

				<#if parameter_has_next>
					,
				</#if>
			</#list>

			)

			<#list method.exceptions as exception>
				<#if exception_index == 0>
					throws
				</#if>

				${exception.fullyQualifiedName}

				<#if exception_has_next>
					,
				</#if>
			</#list>

			;
		</#if>
	</#list>

	<#if serviceBuilder.isVersionGTE_7_4_0()>
		<#list entity.uniqueEntityFinders as entityFinder>
			<#assign entityColumns = entityFinder.entityColumns />

			/**
			 * Returns the ${entity.humanName} where ${entityFinder.getHumanConditions(false)} or returns <code>null</code> if it could not be found. Uses the finder cache.
			 *
			<#list entityColumns as entityColumn>
			 * @param ${entityColumn.name} the ${entityColumn.humanName}
			</#list>
			 * @return the matching ${entity.humanName}, or <code>null</code> if a matching ${entity.humanName} could not be found
			 */
			public default ${entity.name} fetchBy${entityFinder.name}(

			<#list entityColumns as entityColumn>
				${entityColumn.type} ${entityColumn.name}

				<#if entityColumn_has_next>
					,
				</#if>
			</#list>

			) {
				return fetchBy${entityFinder.name}(

				<#list entityColumns as entityColumn>
					${entityColumn.name},
				</#list>

				true);
			}
		</#list>

		<#list entity.collectionEntityFinders as entityFinder>
			<#assign entityColumns = entityFinder.entityColumns />

			<#if !entityFinder.isUnique()>
				/**
				 * Returns all the ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(false)}.
				 *
				<#list entityColumns as entityColumn>
				 * @param ${entityColumn.name} the ${entityColumn.humanName}
				</#list>
				 * @return the matching ${entity.pluralHumanName}
				 */
				public default java.util.List<${entity.name}> findBy${entityFinder.name}(

			<#list entityColumns as entityColumn>
				${entityColumn.type} ${entityColumn.name}

				<#if entityColumn_has_next>
					,
				</#if>
			</#list>

			) {
				return findBy${entityFinder.name}(

				<#list entityColumns as entityColumn>
					${entityColumn.name},
				</#list>

				com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
			}

			/**
			 * Returns a range of all the ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(false)}.
			 *
			 * <p>
			 * <#include "range_comment.ftl">
			 * </p>
			 *
			<#list entityColumns as entityColumn>
			 * @param ${entityColumn.name} the ${entityColumn.humanName}
			</#list>
			 * @param start the lower bound of the range of ${entity.pluralHumanName}
			 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
			 * @return the range of matching ${entity.pluralHumanName}
			 */
			public default java.util.List<${entity.name}> findBy${entityFinder.name}(

			<#list entityColumns as entityColumn>
				${entityColumn.type} ${entityColumn.name},
			</#list>

			int start, int end) {
				return findBy${entityFinder.name}(

				<#list entityColumns as entityColumn>
					${entityColumn.name},
				</#list>

				start, end, null, true);
			}

			/**
			 * Returns an ordered range of all the ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(false)}.
			 *
			 * <p>
			 * <#include "range_comment.ftl">
			 * </p>
			 *
			<#list entityColumns as entityColumn>
			 * @param ${entityColumn.name} the ${entityColumn.humanName}
			</#list>
			 * @param start the lower bound of the range of ${entity.pluralHumanName}
			 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
			 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
			 * @return the ordered range of matching ${entity.pluralHumanName}
			 */
			public default java.util.List<${entity.name}> findBy${entityFinder.name}(

				<#list entityColumns as entityColumn>
					${entityColumn.type} ${entityColumn.name},
				</#list>

				int start, int end, com.liferay.portal.kernel.util.OrderByComparator<${entity.name}> orderByComparator) {

					return findBy${entityFinder.name}(

					<#list entityColumns as entityColumn>
						${entityColumn.name},
					</#list>

					start, end, orderByComparator, true);
				}
			</#if>

			<#if entityFinder.hasArrayableOperator()>
				<#assign entityColumns = entityFinder.entityColumns />

				/**
				 * Returns all the ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(true)}.
				 *
				<#list entityColumns as entityColumn>
					<#if entityColumn.hasArrayableOperator()>
				 * @param ${entityColumn.pluralName} the ${entityColumn.pluralHumanName}
					<#else>
				 * @param ${entityColumn.name} the ${entityColumn.humanName}
					</#if>
				</#list>
				 * @return the matching ${entity.pluralHumanName}
				 */
				public default java.util.List<${entity.name}> findBy${entityFinder.name}(

				<#list entityColumns as entityColumn>
					<#if entityColumn.hasArrayableOperator()>
						${entityColumn.type}[] ${entityColumn.pluralName}
					<#else>
						${entityColumn.type} ${entityColumn.name}
					</#if>

					<#if entityColumn_has_next>
						,
					</#if>
				</#list>

				) {
					return findBy${entityFinder.name}(

					<#list entityColumns as entityColumn>
						<#if entityColumn.hasArrayableOperator()>
							${entityColumn.pluralName},
						<#else>
							${entityColumn.name},
						</#if>
					</#list>

					com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
				}

				/**
				 * Returns a range of all the ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(true)}.
				 *
				 * <p>
				 * <#include "range_comment.ftl">
				 * </p>
				 *
				<#list entityColumns as entityColumn>
					<#if entityColumn.hasArrayableOperator()>
				 * @param ${entityColumn.pluralName} the ${entityColumn.pluralHumanName}
					<#else>
				 * @param ${entityColumn.name} the ${entityColumn.humanName}
					</#if>
				</#list>
				 * @param start the lower bound of the range of ${entity.pluralHumanName}
				 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
				 * @return the range of matching ${entity.pluralHumanName}
				 */
				public default java.util.List<${entity.name}> findBy${entityFinder.name}(

				<#list entityColumns as entityColumn>
					<#if entityColumn.hasArrayableOperator()>
						${entityColumn.type}[] ${entityColumn.pluralName},
					<#else>
						${entityColumn.type} ${entityColumn.name},
					</#if>
				</#list>

				int start, int end) {
					return findBy${entityFinder.name}(

					<#list entityColumns as entityColumn>
						<#if entityColumn.hasArrayableOperator()>
							${entityColumn.pluralName},
						<#else>
							${entityColumn.name},
						</#if>
					</#list>

					start, end, null, true);
				}

				/**
				 * Returns an ordered range of all the ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(true)}.
				 *
				 * <p>
				 * <#include "range_comment.ftl">
				 * </p>
				 *
				<#list entityColumns as entityColumn>
					<#if entityColumn.hasArrayableOperator()>
				 * @param ${entityColumn.pluralName} the ${entityColumn.pluralHumanName}
					<#else>
				 * @param ${entityColumn.name} the ${entityColumn.humanName}
					</#if>
				</#list>
				 * @param start the lower bound of the range of ${entity.pluralHumanName}
				 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
				 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
				 * @return the ordered range of matching ${entity.pluralHumanName}
				 */
				public default java.util.List<${entity.name}> findBy${entityFinder.name}(

				<#list entityColumns as entityColumn>
					<#if entityColumn.hasArrayableOperator()>
						${entityColumn.type}[] ${entityColumn.pluralName},
					<#else>
						${entityColumn.type} ${entityColumn.name},
					</#if>
				</#list>

				int start, int end, com.liferay.portal.kernel.util.OrderByComparator<${entity.name}> orderByComparator) {

					return findBy${entityFinder.name}(

					<#list entityColumns as entityColumn>
						<#if entityColumn.hasArrayableOperator()>
							${entityColumn.pluralName},
						<#else>
							${entityColumn.name},
						</#if>
					</#list>

					start, end, orderByComparator, true);
				}
			</#if>

			<#if entity.isPermissionCheckEnabled(entityFinder) && !entityFinder.isUnique()>
				/**
				 * Returns all the ${entity.pluralHumanName} that the user has permission to view where ${entityFinder.getHumanConditions(false)}.
				 *
				<#list entityColumns as entityColumn>
				 * @param ${entityColumn.name} the ${entityColumn.humanName}
				</#list>
				 * @return the matching ${entity.pluralHumanName} that the user has permission to view
				 */
				public default java.util.List<${entity.name}> filterFindBy${entityFinder.name}(

				<#list entityColumns as entityColumn>
					${entityColumn.type} ${entityColumn.name}

					<#if entityColumn_has_next>
						,
					</#if>
				</#list>

				) {
					return filterFindBy${entityFinder.name}(

					<#list entityColumns as entityColumn>
						${entityColumn.name},
					</#list>

					com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
				}

				/**
				 * Returns a range of all the ${entity.pluralHumanName} that the user has permission to view where ${entityFinder.getHumanConditions(false)}.
				 *
				 * <p>
				 * <#include "range_comment.ftl">
				 * </p>
				 *
				<#list entityColumns as entityColumn>
				 * @param ${entityColumn.name} the ${entityColumn.humanName}
				</#list>
				 * @param start the lower bound of the range of ${entity.pluralHumanName}
				 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
				 * @return the range of matching ${entity.pluralHumanName} that the user has permission to view
				 */
				public default java.util.List<${entity.name}> filterFindBy${entityFinder.name}(

				<#list entityColumns as entityColumn>
					${entityColumn.type} ${entityColumn.name},
				</#list>

				int start, int end) {
					return filterFindBy${entityFinder.name}(

					<#list entityColumns as entityColumn>
						${entityColumn.name},
					</#list>

					start, end, null);
				}
			</#if>

			<#if entity.isPermissionCheckEnabled(entityFinder) && !entityFinder.isUnique() && entityFinder.hasArrayableOperator()>
				/**
				 * Returns all the ${entity.pluralHumanName} that the user has permission to view where ${entityFinder.getHumanConditions(true)}.
				 *
				<#list entityColumns as entityColumn>
					<#if entityColumn.hasArrayableOperator()>
				 * @param ${entityColumn.pluralName} the ${entityColumn.pluralHumanName}
					<#else>
				 * @param ${entityColumn.name} the ${entityColumn.humanName}
					</#if>
				</#list>
				 * @return the matching ${entity.pluralHumanName} that the user has permission to view
				 */
				public default java.util.List<${entity.name}> filterFindBy${entityFinder.name}(

				<#list entityColumns as entityColumn>
					<#if entityColumn.hasArrayableOperator()>
						${entityColumn.type}[] ${entityColumn.pluralName}
					<#else>
						${entityColumn.type} ${entityColumn.name}
					</#if>

					<#if entityColumn_has_next>
						,
					</#if>
				</#list>

				) {
					return filterFindBy${entityFinder.name}(

					<#list entityColumns as entityColumn>
						<#if entityColumn.hasArrayableOperator()>
							${entityColumn.pluralName},
						<#else>
							${entityColumn.name},
						</#if>
					</#list>

					com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
				}

				/**
				 * Returns a range of all the ${entity.pluralHumanName} that the user has permission to view where ${entityFinder.getHumanConditions(true)}.
				 *
				 * <p>
				 * <#include "range_comment.ftl">
				 * </p>
				 *
				<#list entityColumns as entityColumn>
					<#if entityColumn.hasArrayableOperator()>
				 * @param ${entityColumn.pluralName} the ${entityColumn.pluralHumanName}
					<#else>
				 * @param ${entityColumn.name} the ${entityColumn.humanName}
					</#if>
				</#list>
				 * @param start the lower bound of the range of ${entity.pluralHumanName}
				 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
				 * @return the range of matching ${entity.pluralHumanName} that the user has permission to view
				 */
				public default java.util.List<${entity.name}> filterFindBy${entityFinder.name}(

				<#list entityColumns as entityColumn>
					<#if entityColumn.hasArrayableOperator()>
						${entityColumn.type}[] ${entityColumn.pluralName},
					<#else>
						${entityColumn.type} ${entityColumn.name},
					</#if>
				</#list>

				int start, int end) {
					return filterFindBy${entityFinder.name}(

					<#list entityColumns as entityColumn>
						<#if entityColumn.hasArrayableOperator()>
							${entityColumn.pluralName},
						<#else>
							${entityColumn.name},
						</#if>
					</#list>

					start, end, null);
				}
			</#if>
		</#list>
	</#if>

}