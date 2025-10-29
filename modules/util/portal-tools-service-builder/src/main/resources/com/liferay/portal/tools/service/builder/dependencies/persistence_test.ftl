<#assign parentPKColumn = "" />

<#if entity.isHierarchicalTree()>
	<#if entity.hasEntityColumn("groupId")>
		<#assign scopeEntityColumn = entity.getEntityColumn("groupId") />
	<#else>
		<#assign scopeEntityColumn = entity.getEntityColumn("companyId") />
	</#if>

	<#assign
		pkEntityColumn = entity.PKEntityColumns?first

		parentPKColumn = entity.getEntityColumn("parent" + pkEntityColumn.methodName)
	/>
</#if>

package ${packagePath}.service.persistence.test;

<#assign
	duplicateEntityExternalReferenceCode = serviceBuilder.getDuplicateEntityExternalReferenceCodeException(entity)
	noSuchEntity = serviceBuilder.getNoSuchEntityException(entity)
/>

import ${apiPackagePath}.exception.${duplicateEntityExternalReferenceCode}Exception;
import ${apiPackagePath}.exception.${noSuchEntity}Exception;
import ${apiPackagePath}.model.${entity.name};
import ${apiPackagePath}.service.${entity.name}LocalServiceUtil;
import ${apiPackagePath}.service.persistence.${entity.name}PK;
import ${apiPackagePath}.service.persistence.${entity.name}Persistence;
import ${apiPackagePath}.service.persistence.${entity.name}Util;

import ${beanLocatorUtil};

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.jdbc.OutputBlob;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.security.permission.SimplePermissionChecker;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.math.BigDecimal;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
<#if classDeprecated>
 * @deprecated ${classDeprecatedComment}
</#if>
 * @generated
 */

<#if classDeprecated>
	@Deprecated
</#if>

<#if osgiModule || serviceBuilder.isVersionGTE_7_2_0()>
	@RunWith(Arquillian.class)
</#if>
public class ${entity.name}PersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE, new TransactionalTestRule(Propagation.REQUIRED
			<#if osgiModule>
				, "${apiPackagePath}.service"
			</#if>
		));

	@Before
	public void setUp() {
		_persistence = ${entity.name}Util.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<${entity.name}> iterator = _${entity.pluralVariableName}.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		<#if entity.hasCompoundPK()>
			${entity.PKClassName} pk = new ${entity.PKClassName}(

			<#list entity.PKEntityColumns as entityColumn>
				<#if stringUtil.equals(entityColumn.type, "int")>
					RandomTestUtil.nextInt()
				<#elseif stringUtil.equals(entityColumn.type, "long")>
					<#if stringUtil.equals(entityColumn.name, "companyId")>
						CompanyThreadLocal.getCompanyId()
					<#else>
						RandomTestUtil.nextLong()
					</#if>
				<#elseif stringUtil.equals(entityColumn.type, "String")>
					<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

					<#if maxLength < 8>
						RandomTestUtil.randomString(${maxLength})
					<#else>
						RandomTestUtil.randomString()
					</#if>
				</#if>

				<#if entityColumn_has_next>
					,
				</#if>
			</#list>

			);
		<#else>
			<#assign entityColumn = entity.PKEntityColumns[0] />

			${entityColumn.type} pk =

			<#if stringUtil.equals(entityColumn.type, "int")>
				RandomTestUtil.nextInt()
			<#elseif stringUtil.equals(entityColumn.type, "long")>
				RandomTestUtil.nextLong()
			<#elseif stringUtil.equals(entityColumn.type, "String")>
				<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

				<#if maxLength < 8>
					RandomTestUtil.randomString(${maxLength})
				<#else>
					RandomTestUtil.randomString()
				</#if>
			</#if>

			;
		</#if>

		${entity.name} ${entity.variableName} = _persistence.create(pk);

		Assert.assertNotNull(${entity.variableName});

		Assert.assertEquals(${entity.variableName}.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		${entity.name} new${entity.name} = add${entity.name}();

		_persistence.remove(new${entity.name});

		${entity.name} existing${entity.name} = _persistence.fetchByPrimaryKey(new${entity.name}.getPrimaryKey());

		Assert.assertNull(existing${entity.name});
	}

	@Test
	public void testUpdateNew() throws Exception {
		add${entity.name}();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		<#if entity.hasCompoundPK()>
			${entity.PKClassName} pk = new ${entity.PKClassName}(

			<#list entity.PKEntityColumns as entityColumn>
				<#if stringUtil.equals(entityColumn.type, "int")>
					RandomTestUtil.nextInt()
				<#elseif stringUtil.equals(entityColumn.type, "long")>
					RandomTestUtil.nextLong()
				<#elseif stringUtil.equals(entityColumn.type, "String")>
					<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

					<#if maxLength < 8>
						RandomTestUtil.randomString(${maxLength})
					<#else>
						RandomTestUtil.randomString()
					</#if>
				</#if>

				<#if entityColumn_has_next>
					,
				</#if>
			</#list>

			);
		<#else>
			<#assign entityColumn = entity.PKEntityColumns[0] />

			${entityColumn.type} pk =

			<#if stringUtil.equals(entityColumn.type, "int")>
				RandomTestUtil.nextInt()
			<#elseif stringUtil.equals(entityColumn.type, "long")>
				RandomTestUtil.nextLong()
			<#elseif stringUtil.equals(entityColumn.type, "String")>
				<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

				<#if maxLength < 8>
					RandomTestUtil.randomString(${maxLength})
				<#else>
					RandomTestUtil.randomString()
				</#if>
			</#if>

			;
		</#if>

		${entity.name} new${entity.name} = _persistence.create(pk);

		<#assign hasEagerBlob = false />

		<#list entity.regularEntityColumns as entityColumn>
			<#if !entityColumn.primary && (validator.isNull(parentPKColumn) || (parentPKColumn.name != entityColumn.name))>
				<#if stringUtil.equals(entityColumn.type, "Blob")>
					String new${entityColumn.methodName}String = RandomTestUtil.randomString();

					byte[] new${entityColumn.methodName}Bytes = new${entityColumn.methodName}String.getBytes("UTF-8");

					Blob new${entityColumn.methodName}Blob = new OutputBlob(new ByteArrayInputStream(new${entityColumn.methodName}Bytes), new${entityColumn.methodName}Bytes.length);

					<#if !entityColumn.isLazy()>
						<#assign hasEagerBlob = true />
					</#if>
				</#if>

				new${entity.name}.set${entityColumn.methodName}(

				<#if stringUtil.equals(entityColumn.type, "boolean")>
					RandomTestUtil.randomBoolean()
				<#elseif stringUtil.equals(entityColumn.type, "double")>
					RandomTestUtil.nextDouble()
				<#elseif stringUtil.equals(entityColumn.type, "int")>
					RandomTestUtil.nextInt()
				<#elseif stringUtil.equals(entityColumn.type, "long")>
					RandomTestUtil.nextLong()
				<#elseif stringUtil.equals(entityColumn.type, "BigDecimal")>
					new BigDecimal(RandomTestUtil.nextDouble())
				<#elseif stringUtil.equals(entityColumn.type, "Date")>
					RandomTestUtil.nextDate()
				<#elseif stringUtil.equals(entityColumn.type, "Blob")>
					new${entityColumn.methodName}Blob
				<#elseif stringUtil.equals(entityColumn.type, "Map")>
					new HashMap<String, Serializable>()
				<#elseif stringUtil.equals(entityColumn.type, "String")>
					<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

					<#if maxLength < 8>
						RandomTestUtil.randomString(${maxLength})
					<#else>
						RandomTestUtil.randomString()
					</#if>
				</#if>

				);
			</#if>
		</#list>

		_${entity.pluralVariableName}.add(_persistence.update(new${entity.name}));

		<#if hasEagerBlob>
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		</#if>

		${entity.name} existing${entity.name} = _persistence.findByPrimaryKey(new${entity.name}.getPrimaryKey());

		<#list entity.regularEntityColumns as entityColumn>
			<#if stringUtil.equals(entityColumn.type, "Blob")>
				Blob existing${entityColumn.methodName} = existing${entity.name}.get${entityColumn.methodName}();

				Assert.assertTrue(Arrays.equals(existing${entityColumn.methodName}.getBytes(1, (int)existing${entityColumn.methodName}.length()), new${entityColumn.methodName}Bytes));
			<#elseif stringUtil.equals(entityColumn.type, "boolean")>
				Assert.assertEquals(existing${entity.name}.is${entityColumn.methodName}(), new${entity.name}.is${entityColumn.methodName}());
			<#elseif stringUtil.equals(entityColumn.type, "Date")>
				Assert.assertEquals(Time.getShortTimestamp(existing${entity.name}.get${entityColumn.methodName}()), Time.getShortTimestamp(new${entity.name}.get${entityColumn.methodName}()));
			<#elseif stringUtil.equals(entityColumn.type, "double")>
				AssertUtils.assertEquals(existing${entity.name}.get${entityColumn.methodName}(), new${entity.name}.get${entityColumn.methodName}());
			<#else>
				Assert.assertEquals(existing${entity.name}.get${entityColumn.methodName}(), new${entity.name}.get${entityColumn.methodName}());
			</#if>
		</#list>
	}

	<#if entity.hasExternalReferenceCode()>
		<#if entity.versionEntity??>
			@Test
			public void testCreateDraft() throws Exception {
				${entity.name} ${entity.variableName} = add${entity.name}();

				<#assign entityColumn = entity.PKEntityColumns[0] />

				${entityColumn.type} pk =

				<#if stringUtil.equals(entityColumn.type, "int")>
					RandomTestUtil.nextInt()
				<#elseif stringUtil.equals(entityColumn.type, "long")>
					RandomTestUtil.nextLong()
				<#elseif stringUtil.equals(entityColumn.type, "String")>
					<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

					<#if maxLength < 8>
						RandomTestUtil.randomString(${maxLength})
					<#else>
						RandomTestUtil.randomString()
					</#if>
				</#if>

				;

				${entity.name} draft${entity.name} = _persistence.create(pk);

				<#list entity.regularEntityColumns as entityColumn>
					<#if !entityColumn.primary && (validator.isNull(parentPKColumn) || (parentPKColumn.name != entityColumn.name))>
						draft${entity.name}.set${entityColumn.methodName}(

						<#if stringUtil.equals(entityColumn.name, "headId")>
							-
						</#if>

						${entity.variableName}.get${entityColumn.methodName}());
					</#if>
				</#list>

				_${entity.pluralVariableName}.add(_persistence.update(draft${entity.name}));

				<#list entity.regularEntityColumns as entityColumn>
					<#if !entityColumn.primary && (validator.isNull(parentPKColumn) || (parentPKColumn.name != entityColumn.name))>
						<#if stringUtil.equals(entityColumn.name, "headId")>
							Assert.assertEquals(${entity.variableName}.get${entityColumn.methodName}(), -draft${entity.name}.get${entityColumn.methodName}());
						<#elseif stringUtil.equals(entityColumn.name, "status")>
							Assert.assertEquals(2, draft${entity.name}.get${entityColumn.methodName}());
						<#elseif stringUtil.equals(entityColumn.type, "Blob")>
							Blob ${entityColumn.methodName} = ${entity.variableName}.get${entityColumn.methodName}();
							Blob draft${entityColumn.methodName} = draft${entity.name}.get${entityColumn.methodName}();

							Assert.assertTrue(Arrays.equals(${entityColumn.variableName}.getBytes(1, (int)${entityColumn.variableName}.length()), Arrays.equals(draft${entityColumn.name}.getBytes(1, (int)draft${entityColumn.name}.length()));
						<#elseif stringUtil.equals(entityColumn.type, "boolean")>
							Assert.assertEquals(${entity.variableName}.is${entityColumn.methodName}(), draft${entity.name}.is${entityColumn.methodName}());
						<#elseif stringUtil.equals(entityColumn.type, "Date")>
							Assert.assertEquals(Time.getShortTimestamp(${entity.variableName}.get${entityColumn.methodName}()), Time.getShortTimestamp(draft${entity.name}.get${entityColumn.methodName}()));
						<#elseif stringUtil.equals(entityColumn.type, "double")>
							AssertUtils.assertEquals(${entity.variableName}.get${entityColumn.methodName}(), draft${entity.name}.get${entityColumn.methodName}());
						<#else>
							Assert.assertEquals(${entity.variableName}.get${entityColumn.methodName}(), draft${entity.name}.get${entityColumn.methodName}());
						</#if>
					</#if>
				</#list>

			}

			@Test(expected = ${duplicateEntityExternalReferenceCode}Exception.class)
			public void testCreateWithExistingExternalReferenceCodeHead()
				throws Exception {

				${entity.name} ${entity.variableName}1 = add${entity.name}();

				<#assign entityColumn = entity.PKEntityColumns[0] />

				${entityColumn.type} pk =

				<#if stringUtil.equals(entityColumn.type, "int")>
					RandomTestUtil.nextInt()
				<#elseif stringUtil.equals(entityColumn.type, "long")>
					RandomTestUtil.nextLong()
				<#elseif stringUtil.equals(entityColumn.type, "String")>
					<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

					<#if maxLength < 8>
						RandomTestUtil.randomString(${maxLength})
					<#else>
						RandomTestUtil.randomString()
					</#if>
				</#if>

				;

				${entity.name} ${entity.variableName}2 = _persistence.create(pk);

				<#list entity.regularEntityColumns as entityColumn>
					<#if !entityColumn.primary && (validator.isNull(parentPKColumn) || (parentPKColumn.name != entityColumn.name))>
						<#if stringUtil.equals(entityColumn.type, "Blob")>
							String ${entityColumn.name}String = RandomTestUtil.randomString();

							byte[] ${entityColumn.name}Bytes = ${entityColumn.name}String.getBytes("UTF-8");

							Blob ${entityColumn.name}Blob = new OutputBlob(new ByteArrayInputStream(${entityColumn.name}Bytes), ${entityColumn.name}Bytes.length);
						</#if>

						${entity.variableName}2.set${entityColumn.methodName}(

						<#if stringUtil.equals(entityColumn.name, "externalReferenceCode") || stringUtil.equals(entityColumn.name, "groupId")>
							${entity.variableName}1.get${entityColumn.methodName}()
						<#elseif stringUtil.equals(entityColumn.name, "headId")>
							- RandomTestUtil.nextLong()
						<#elseif stringUtil.equals(entityColumn.type, "boolean")>
							RandomTestUtil.randomBoolean()
						<#elseif stringUtil.equals(entityColumn.type, "double")>
							RandomTestUtil.nextDouble()
						<#elseif stringUtil.equals(entityColumn.type, "int")>
							RandomTestUtil.nextInt()
						<#elseif stringUtil.equals(entityColumn.type, "long")>
							RandomTestUtil.nextLong()
						<#elseif stringUtil.equals(entityColumn.type, "BigDecimal")>
							new BigDecimal(RandomTestUtil.nextDouble())
						<#elseif stringUtil.equals(entityColumn.type, "Blob")>
							${entityColumn.name}Blob
						<#elseif stringUtil.equals(entityColumn.type, "Date")>
							RandomTestUtil.nextDate()
						<#elseif stringUtil.equals(entityColumn.type, "Map")>
							new HashMap<String, Serializable>()
						<#elseif stringUtil.equals(entityColumn.type, "String")>
							<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

							<#if maxLength < 8>
								RandomTestUtil.randomString(${maxLength})
							<#else>
								RandomTestUtil.randomString()
							</#if>
						</#if>

						);
					</#if>
				</#list>

				_${entity.pluralVariableName}.add(_persistence.update(${entity.variableName}2));
			}
		</#if>

		@Test(expected = ${duplicateEntityExternalReferenceCode}Exception.class)
		public void testUpdateWithExistingExternalReferenceCode() throws Exception {
			${entity.name} ${entity.variableName} = add${entity.name}();

			${entity.name} new${entity.name} = add${entity.name}();

			new${entity.name}.set${entity.externalReferenceCode?cap_first}Id(${entity.variableName}.get${entity.externalReferenceCode?cap_first}Id());

			new${entity.name} = _persistence.update(new${entity.name});

			Session session = _persistence.getCurrentSession();

			session.evict(new${entity.name});

			new${entity.name}.setExternalReferenceCode(${entity.variableName}.getExternalReferenceCode());

			_persistence.update(new${entity.name});
		}
	</#if>

	<#list entity.entityFinders as entityFinder>
		@Test
		public void testCountBy${entityFinder.name}() throws Exception {
			_persistence.countBy${entityFinder.name}(

			<#assign hasString = false />

			<#list entityFinder.entityColumns as entityColumn>
				<#if stringUtil.equals(entityColumn.type, "boolean")>
					RandomTestUtil.randomBoolean()
				<#elseif stringUtil.equals(entityColumn.type, "double")>
					RandomTestUtil.nextDouble()
				<#elseif stringUtil.equals(entityColumn.type, "int")>
					RandomTestUtil.nextInt()
				<#elseif stringUtil.equals(entityColumn.type, "long")>
					RandomTestUtil.nextLong()
				<#elseif stringUtil.equals(entityColumn.type, "Date")>
					RandomTestUtil.nextDate()
				<#elseif stringUtil.equals(entityColumn.type, "String")>
					<#assign hasString = true />

					""
				<#else>
					(${entityColumn.type})null
				</#if>

				<#if entityColumn_has_next>
					,
				</#if>
			</#list>

			);

			<#if hasString>
				_persistence.countBy${entityFinder.name}(

					<#list entityFinder.entityColumns as entityColumn>
						<#if stringUtil.equals(entityColumn.type, "boolean")>
							RandomTestUtil.randomBoolean()
						<#elseif stringUtil.equals(entityColumn.type, "double")>
							0D
						<#elseif stringUtil.equals(entityColumn.type, "int")>
							0
						<#elseif stringUtil.equals(entityColumn.type, "long")>
							0L
						<#elseif stringUtil.equals(entityColumn.type, "Date")>
							RandomTestUtil.nextDate()
						<#elseif stringUtil.equals(entityColumn.type, "String")>
							"null"
						<#else>
							(${entityColumn.type})null
						</#if>

						<#if entityColumn_has_next>
							,
						</#if>
					</#list>

				);
			</#if>

			_persistence.countBy${entityFinder.name}(

				<#list entityFinder.entityColumns as entityColumn>
					<#if stringUtil.equals(entityColumn.type, "boolean")>
						RandomTestUtil.randomBoolean()
					<#elseif stringUtil.equals(entityColumn.type, "double")>
						0D
					<#elseif stringUtil.equals(entityColumn.type, "int")>
						0
					<#elseif stringUtil.equals(entityColumn.type, "long")>
						0L
					<#elseif stringUtil.equals(entityColumn.type, "Date")>
						RandomTestUtil.nextDate()
					<#else>
						(${entityColumn.type})null
					</#if>

					<#if entityColumn_has_next>
						,
					</#if>
				</#list>

			);
		}

		<#if entityFinder.hasArrayableOperator()>
			@Test
			public void testCountBy${entityFinder.name}Arrayable() throws Exception {
				_persistence.countBy${entityFinder.name}(

				<#list entityFinder.entityColumns as entityColumn>
					<#if entityColumn.hasArrayableOperator()>
						new ${entityColumn.type}[]{

						<#if stringUtil.equals(entityColumn.type, "boolean")>
							RandomTestUtil.randomBoolean()
						<#elseif stringUtil.equals(entityColumn.type, "double")>
							RandomTestUtil.nextDouble(), 0D
						<#elseif stringUtil.equals(entityColumn.type, "int")>
							RandomTestUtil.nextInt(), 0
						<#elseif stringUtil.equals(entityColumn.type, "long")>
							RandomTestUtil.nextLong(), 0L
						<#elseif stringUtil.equals(entityColumn.type, "Date")>
							RandomTestUtil.nextDate(), null
						<#elseif stringUtil.equals(entityColumn.type, "String")>
							<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

							<#if maxLength < 8>
								RandomTestUtil.randomString(${maxLength}), "", "null", null, null
							<#else>
								RandomTestUtil.randomString(), "", "null", null, null
							</#if>
						<#else>
							null
						</#if>
					<#else>
						<#if stringUtil.equals(entityColumn.type, "boolean")>
							RandomTestUtil.randomBoolean()
						<#elseif stringUtil.equals(entityColumn.type, "double")>
							RandomTestUtil.nextDouble()
						<#elseif stringUtil.equals(entityColumn.type, "int")>
							RandomTestUtil.nextInt()
						<#elseif stringUtil.equals(entityColumn.type, "long")>
							RandomTestUtil.nextLong()
						<#elseif stringUtil.equals(entityColumn.type, "Date")>
							RandomTestUtil.nextDate()
						<#elseif stringUtil.equals(entityColumn.type, "String")>
							<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

							<#if maxLength < 8>
								RandomTestUtil.randomString(${maxLength})
							<#else>
								RandomTestUtil.randomString()
							</#if>
						<#else>
							null
						</#if>
					</#if>

					<#if entityColumn.hasArrayableOperator()>
						}
					</#if>

					<#if entityColumn_has_next>
						,
					</#if>
				</#list>

				);
			}
		</#if>
	</#list>

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		${entity.name} new${entity.name} = add${entity.name}();

		${entity.name} existing${entity.name} = _persistence.findByPrimaryKey(new${entity.name}.getPrimaryKey());

		Assert.assertEquals(existing${entity.name}, new${entity.name});
	}

	@Test(expected = ${noSuchEntity}Exception.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		<#if entity.hasCompoundPK()>
			${entity.PKClassName} pk = new ${entity.PKClassName}(

			<#list entity.PKEntityColumns as entityColumn>
				<#if stringUtil.equals(entityColumn.type, "int")>
					RandomTestUtil.nextInt()
				<#elseif stringUtil.equals(entityColumn.type, "long")>
					RandomTestUtil.nextLong()
				<#elseif stringUtil.equals(entityColumn.type, "String")>
					<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

					<#if maxLength < 8>
						RandomTestUtil.randomString(${maxLength})
					<#else>
						RandomTestUtil.randomString()
					</#if>
				</#if>

				<#if entityColumn_has_next>
					,
				</#if>
			</#list>

			);
		<#else>
			<#assign entityColumn = entity.PKEntityColumns[0] />

			${entityColumn.type} pk =

			<#if stringUtil.equals(entityColumn.type, "int")>
				RandomTestUtil.nextInt()
			<#elseif stringUtil.equals(entityColumn.type, "long")>
				RandomTestUtil.nextLong()
			<#elseif stringUtil.equals(entityColumn.type, "String")>
				<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

				<#if maxLength < 8>
					RandomTestUtil.randomString(${maxLength})
				<#else>
					RandomTestUtil.randomString()
				</#if>
			</#if>

			;
		</#if>

		_persistence.findByPrimaryKey(pk);
	}

	<#if !entity.hasCompoundPK()>
		@Test
		public void testFindAll() throws Exception {
			_persistence.findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
		}

		<#list entity.entityFinders as entityFinder>
			<#if stringUtil.equals(entityFinder.name, "GroupId") && entity.isPermissionCheckEnabled(entityFinder)>
				@Test
				public void testFilterFindByGroupId() throws Exception {
					<#if serviceBuilder.isVersionGTE_7_4_0()>
						PermissionThreadLocal.setPermissionChecker(
							new SimplePermissionChecker() {
								{
									init(TestPropsValues.getUser());
								}

								@Override
								public boolean isCompanyAdmin(long companyId) {
									return false;
								}

							});

						Assert.assertTrue(InlineSQLHelperUtil.isEnabled(0));

						_persistence.filterFindByGroupId(0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
					</#if>

					_persistence.filterFindByGroupId(0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
				}
				<#break>
			</#if>
		</#list>

		protected OrderByComparator<${entity.name}> getOrderByComparator() {
			return OrderByComparatorFactoryUtil.create(
				"${entity.table}",

				<#assign appendComma = false />

				<#list entity.regularEntityColumns as entityColumn>
					<#assign entityColumnType = serviceBuilder.getSqlType(entity.getName(), entityColumn) />

					<#if !stringUtil.equals(entityColumnType, "BLOB") && !stringUtil.equals(entityColumnType, "CLOB")>
						<#if appendComma>
							,
						</#if>

						<#assign appendComma = true />

						"${entityColumn.name}", true
					</#if>
				</#list>

				);
		}
	</#if>

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		${entity.name} new${entity.name} = add${entity.name}();

		${entity.name} existing${entity.name} = _persistence.fetchByPrimaryKey(new${entity.name}.getPrimaryKey());

		Assert.assertEquals(existing${entity.name}, new${entity.name});
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		<#if entity.hasCompoundPK()>
			${entity.PKClassName} pk = new ${entity.PKClassName}(

			<#list entity.PKEntityColumns as entityColumn>
				<#if stringUtil.equals(entityColumn.type, "int")>
					RandomTestUtil.nextInt()
				<#elseif stringUtil.equals(entityColumn.type, "long")>
					RandomTestUtil.nextLong()
				<#elseif stringUtil.equals(entityColumn.type, "String")>
					<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

					<#if maxLength < 8>
						RandomTestUtil.randomString(${maxLength})
					<#else>
						RandomTestUtil.randomString()
					</#if>
				</#if>

				<#if entityColumn_has_next>
					,
				</#if>
			</#list>

			);
		<#else>
			<#assign entityColumn = entity.PKEntityColumns[0] />

			${entityColumn.type} pk =

			<#if stringUtil.equals(entityColumn.type, "int")>
				RandomTestUtil.nextInt()
			<#elseif stringUtil.equals(entityColumn.type, "long")>
				RandomTestUtil.nextLong()
			<#elseif stringUtil.equals(entityColumn.type, "String")>
				<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

				<#if maxLength < 8>
					RandomTestUtil.randomString(${maxLength})
				<#else>
					RandomTestUtil.randomString()
				</#if>
			</#if>

			;
		</#if>

		${entity.name} missing${entity.name} = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missing${entity.name});
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist() throws Exception {
		${entity.name} new${entity.name}1 = add${entity.name}();
		${entity.name} new${entity.name}2 = add${entity.name}();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(new${entity.name}1.getPrimaryKey());
		primaryKeys.add(new${entity.name}2.getPrimaryKey());

		Map<Serializable, ${entity.name}> ${entity.pluralVariableName} = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ${entity.pluralVariableName}.size());
		Assert.assertEquals(new${entity.name}1, ${entity.pluralVariableName}.get(new${entity.name}1.getPrimaryKey()));
		Assert.assertEquals(new${entity.name}2, ${entity.pluralVariableName}.get(new${entity.name}2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist() throws Exception {
		<#if entity.hasCompoundPK()>
			${entity.PKClassName} pk1 = new ${entity.PKClassName}(

			<#list entity.PKEntityColumns as entityColumn>
				<#if stringUtil.equals(entityColumn.type, "int")>
					RandomTestUtil.nextInt()
				<#elseif stringUtil.equals(entityColumn.type, "long")>
					RandomTestUtil.nextLong()
				<#elseif stringUtil.equals(entityColumn.type, "String")>
					<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

					<#if maxLength < 8>
						RandomTestUtil.randomString(${maxLength})
					<#else>
						RandomTestUtil.randomString()
					</#if>
				</#if>

				<#if entityColumn_has_next>
					,
				</#if>
			</#list>

			);

			${entity.PKClassName} pk2 = new ${entity.PKClassName}(

			<#list entity.PKEntityColumns as entityColumn>
				<#if stringUtil.equals(entityColumn.type, "int")>
					RandomTestUtil.nextInt()
				<#elseif stringUtil.equals(entityColumn.type, "long")>
					RandomTestUtil.nextLong()
				<#elseif stringUtil.equals(entityColumn.type, "String")>
					<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

					<#if maxLength < 8>
						RandomTestUtil.randomString(${maxLength})
					<#else>
						RandomTestUtil.randomString()
					</#if>
				</#if>

				<#if entityColumn_has_next>
					,
				</#if>
			</#list>

			);
		<#else>
			<#assign entityColumn = entity.PKEntityColumns[0] />

			${entityColumn.type} pk1 =

			<#if stringUtil.equals(entityColumn.type, "int")>
				RandomTestUtil.nextInt()
			<#elseif stringUtil.equals(entityColumn.type, "long")>
				RandomTestUtil.nextLong()
			<#elseif stringUtil.equals(entityColumn.type, "String")>
				<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

				<#if maxLength < 8>
					RandomTestUtil.randomString(${maxLength})
				<#else>
					RandomTestUtil.randomString()
				</#if>
			</#if>

			;

			${entityColumn.type} pk2 =

			<#if stringUtil.equals(entityColumn.type, "int")>
				RandomTestUtil.nextInt()
			<#elseif stringUtil.equals(entityColumn.type, "long")>
				RandomTestUtil.nextLong()
			<#elseif stringUtil.equals(entityColumn.type, "String")>
				<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

				<#if maxLength < 8>
					RandomTestUtil.randomString(${maxLength})
				<#else>
					RandomTestUtil.randomString()
				</#if>
			</#if>

			;
		</#if>

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ${entity.name}> ${entity.pluralVariableName} = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(${entity.pluralVariableName}.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist() throws Exception {
		${entity.name} new${entity.name} = add${entity.name}();

		<#if entity.hasCompoundPK()>
			${entity.PKClassName} pk = new ${entity.PKClassName}(

			<#list entity.PKEntityColumns as entityColumn>
				<#if stringUtil.equals(entityColumn.type, "int")>
					RandomTestUtil.nextInt()
				<#elseif stringUtil.equals(entityColumn.type, "long")>
					RandomTestUtil.nextLong()
				<#elseif stringUtil.equals(entityColumn.type, "String")>
					<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

					<#if maxLength < 8>
						RandomTestUtil.randomString(${maxLength})
					<#else>
						RandomTestUtil.randomString()
					</#if>
				</#if>

				<#if entityColumn_has_next>
					,
				</#if>
			</#list>

			);
		<#else>
			${entityColumn.type} pk =

			<#if stringUtil.equals(entityColumn.type, "int")>
				RandomTestUtil.nextInt()
			<#elseif stringUtil.equals(entityColumn.type, "long")>
				RandomTestUtil.nextLong()
			<#elseif stringUtil.equals(entityColumn.type, "String")>
				<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

				<#if maxLength < 8>
					RandomTestUtil.randomString(${maxLength})
				<#else>
					RandomTestUtil.randomString()
				</#if>
			</#if>

			;
		</#if>

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(new${entity.name}.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ${entity.name}> ${entity.pluralVariableName} = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ${entity.pluralVariableName}.size());
		Assert.assertEquals(new${entity.name}, ${entity.pluralVariableName}.get(new${entity.name}.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ${entity.name}> ${entity.pluralVariableName} = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(${entity.pluralVariableName}.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		${entity.name} new${entity.name} = add${entity.name}();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(new${entity.name}.getPrimaryKey());

		Map<Serializable, ${entity.name}> ${entity.pluralVariableName} = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ${entity.pluralVariableName}.size());
		Assert.assertEquals(new${entity.name}, ${entity.pluralVariableName}.get(new${entity.name}.getPrimaryKey()));
	}



	<#if entity.uniqueEntityFinders?size != 0>
		@Test
		public void testResetOriginalValues() throws Exception {
			${entity.name} new${entity.name} = add${entity.name}();

			_persistence.clearCache();

			<#if serviceBuilder.isVersionGTE_7_3_0()>
				_assertOriginalValues(_persistence.findByPrimaryKey(new${entity.name}.getPrimaryKey()));
			<#else>
				${entity.name} existing${entity.name} = _persistence.findByPrimaryKey(new${entity.name}.getPrimaryKey());

				<#list entity.uniqueEntityFinders as uniqueEntityFinder>
					<#assign entityColumns = uniqueEntityFinder.entityColumns />

					<#list entityColumns as entityColumn>
						<#if entityColumn.isInterfaceColumn()>
							<#if stringUtil.equals(entityColumn.type, "double")>
								AssertUtils.assertEquals(existing${entity.name}.get${entityColumn.methodName}(), ReflectionTestUtil.<Double>invoke(existing${entity.name}, "getOriginal${entityColumn.methodName}", new Class<?>[0]));
							<#elseif entityColumn.isPrimitiveType()>
								Assert.assertEquals(${serviceBuilder.getPrimitiveObj(entityColumn.type)}.valueOf(existing${entity.name}.get${entityColumn.methodName}()), ReflectionTestUtil.<${serviceBuilder.getPrimitiveObj(entityColumn.type)}>invoke(existing${entity.name}, "getOriginal${entityColumn.methodName}", new Class<?>[0]));
							<#else>
								Assert.assertEquals(existing${entity.name}.get${entityColumn.methodName}(), ReflectionTestUtil.invoke(existing${entity.name}, "getOriginal${entityColumn.methodName}", new Class<?>[0]));
							</#if>
						</#if>
					</#list>
				</#list>
			</#if>
		}

		<#if serviceBuilder.isVersionGTE_7_3_0()>

			private void _assertOriginalValues(${entity.name} ${entity.variableName}) {
				<#list entity.uniqueEntityFinders as uniqueEntityFinder>
					<#assign entityColumns = uniqueEntityFinder.entityColumns />

					<#list entityColumns as entityColumn>
						<#if entityColumn.isInterfaceColumn()>
							<#if stringUtil.equals(entityColumn.type, "double")>
								AssertUtils.assertEquals(${entity.variableName}.get${entityColumn.methodName}(), ReflectionTestUtil.<Double>invoke(${entity.variableName}, "getColumnOriginalValue", new Class<?>[]{String.class}, "${entityColumn.DBName}"));
							<#elseif entityColumn.isPrimitiveType()>
								Assert.assertEquals(${serviceBuilder.getPrimitiveObj(entityColumn.type)}.valueOf(${entity.variableName}.get${entityColumn.methodName}()), ReflectionTestUtil.<${serviceBuilder.getPrimitiveObj(entityColumn.type)}>invoke(${entity.variableName}, "getColumnOriginalValue", new Class<?>[]{String.class}, "${entityColumn.DBName}"));
							<#else>
								Assert.assertEquals(${entity.variableName}.get${entityColumn.methodName}(), ReflectionTestUtil.invoke(${entity.variableName}, "getColumnOriginalValue", new Class<?>[]{String.class}, "${entityColumn.DBName}"));
							</#if>
						</#if>
					</#list>
				</#list>
			}
		</#if>
	</#if>

	protected ${entity.name} add${entity.name}() throws Exception {
		<#if entity.hasCompoundPK()>
			${entity.PKClassName} pk = new ${entity.PKClassName}(

			<#list entity.PKEntityColumns as entityColumn>
				<#if stringUtil.equals(entityColumn.type, "int")>
					RandomTestUtil.nextInt()
				<#elseif stringUtil.equals(entityColumn.type, "long")>
					RandomTestUtil.nextLong()
				<#elseif stringUtil.equals(entityColumn.type, "String")>
					<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

					<#if maxLength < 8>
						RandomTestUtil.randomString(${maxLength})
					<#else>
						RandomTestUtil.randomString()
					</#if>
				</#if>

				<#if entityColumn_has_next>
					,
				</#if>
			</#list>

			);
		<#else>
			<#assign entityColumn = entity.PKEntityColumns[0] />

			${entityColumn.type} pk =

			<#if stringUtil.equals(entityColumn.type, "int")>
				RandomTestUtil.nextInt()
			<#elseif stringUtil.equals(entityColumn.type, "long")>
				RandomTestUtil.nextLong()
			<#elseif stringUtil.equals(entityColumn.type, "String")>
				<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

				<#if maxLength < 8>
					RandomTestUtil.randomString(${maxLength})
				<#else>
					RandomTestUtil.randomString()
				</#if>
			</#if>

			;
		</#if>

		${entity.name} ${entity.variableName} = _persistence.create(pk);

		<#list entity.regularEntityColumns as entityColumn>
			<#if !entityColumn.primary && (validator.isNull(parentPKColumn) || (parentPKColumn.name != entityColumn.name))>
				<#if stringUtil.equals(entityColumn.type, "Blob")>
					String ${entityColumn.name}String = RandomTestUtil.randomString();

					byte[] ${entityColumn.name}Bytes = ${entityColumn.name}String.getBytes("UTF-8");

					Blob ${entityColumn.name}Blob = new OutputBlob(new ByteArrayInputStream(${entityColumn.name}Bytes), ${entityColumn.name}Bytes.length);
				</#if>

				${entity.variableName}.set${entityColumn.methodName}(

				<#if stringUtil.equals(entityColumn.name, "headId") && entity.versionEntity??>
					-pk
				<#elseif stringUtil.equals(entityColumn.type, "boolean")>
					RandomTestUtil.randomBoolean()
				<#elseif stringUtil.equals(entityColumn.type, "double")>
					RandomTestUtil.nextDouble()
				<#elseif stringUtil.equals(entityColumn.type, "int")>
					RandomTestUtil.nextInt()
				<#elseif stringUtil.equals(entityColumn.type, "long")>
					RandomTestUtil.nextLong()
				<#elseif stringUtil.equals(entityColumn.type, "BigDecimal")>
					new BigDecimal(RandomTestUtil.nextDouble())
				<#elseif stringUtil.equals(entityColumn.type, "Blob")>
					${entityColumn.name}Blob
				<#elseif stringUtil.equals(entityColumn.type, "Date")>
					RandomTestUtil.nextDate()
				<#elseif stringUtil.equals(entityColumn.type, "Map")>
					new HashMap<String, Serializable>()
				<#elseif stringUtil.equals(entityColumn.type, "String")>
					<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

					<#if maxLength < 8>
						RandomTestUtil.randomString(${maxLength})
					<#else>
						RandomTestUtil.randomString()
					</#if>
				</#if>

				);
			</#if>
		</#list>

		_${entity.pluralVariableName}.add(_persistence.update(${entity.variableName}));

		return ${entity.variableName};
	}

	<#if entity.isHierarchicalTree()>
		@Test
		public void testMoveTree() throws Exception {
			long ${scopeEntityColumn.name} = RandomTestUtil.nextLong();

			${entity.name} root${entity.name} = add${entity.name}(${scopeEntityColumn.name}, null);

			long previousRootLeft${pkEntityColumn.methodName} = root${entity.name}.getLeft${pkEntityColumn.methodName}();
			long previousRootRight${pkEntityColumn.methodName} = root${entity.name}.getRight${pkEntityColumn.methodName}();

			${entity.name} child${entity.name} = add${entity.name}(${scopeEntityColumn.name}, root${entity.name}.get${pkEntityColumn.methodName}());

			root${entity.name} = _persistence.fetchByPrimaryKey(root${entity.name}.getPrimaryKey());

			Assert.assertEquals(previousRootLeft${pkEntityColumn.methodName}, root${entity.name}.getLeft${pkEntityColumn.methodName}());
			Assert.assertEquals(previousRootRight${pkEntityColumn.methodName} + 2, root${entity.name}.getRight${pkEntityColumn.methodName}());
			Assert.assertEquals(root${entity.name}.getLeft${pkEntityColumn.methodName}() + 1, child${entity.name}.getLeft${pkEntityColumn.methodName}());
			Assert.assertEquals(root${entity.name}.getRight${pkEntityColumn.methodName}() - 1, child${entity.name}.getRight${pkEntityColumn.methodName}());
		}

		@Test
		public void testMoveTreeFromLeft() throws Exception {
			long ${scopeEntityColumn.name} = RandomTestUtil.nextLong();

			${entity.name} parent${entity.name} = add${entity.name}(${scopeEntityColumn.name}, null);

			${entity.name} child${entity.name} = add${entity.name}(${scopeEntityColumn.name}, parent${entity.name}.get${pkEntityColumn.methodName}());

			parent${entity.name} = _persistence.fetchByPrimaryKey(parent${entity.name}.getPrimaryKey());

			${entity.name} root${entity.name} = add${entity.name}(${scopeEntityColumn.name}, null);

			long previousRootLeft${pkEntityColumn.methodName} = root${entity.name}.getLeft${pkEntityColumn.methodName}();
			long previousRootRight${pkEntityColumn.methodName} = root${entity.name}.getRight${pkEntityColumn.methodName}();

			parent${entity.name}.setParent${pkEntityColumn.methodName}(root${entity.name}.get${pkEntityColumn.methodName}());

			_persistence.update(parent${entity.name});

			root${entity.name} = _persistence.fetchByPrimaryKey(root${entity.name}.getPrimaryKey());
			child${entity.name} = _persistence.fetchByPrimaryKey(child${entity.name}.getPrimaryKey());

			Assert.assertEquals(previousRootLeft${pkEntityColumn.methodName} - 4, root${entity.name}.getLeft${pkEntityColumn.methodName}());
			Assert.assertEquals(previousRootRight${pkEntityColumn.methodName}, root${entity.name}.getRight${pkEntityColumn.methodName}());
			Assert.assertEquals(root${entity.name}.getLeft${pkEntityColumn.methodName}() + 1, parent${entity.name}.getLeft${pkEntityColumn.methodName}());
			Assert.assertEquals(root${entity.name}.getRight${pkEntityColumn.methodName}() - 1, parent${entity.name}.getRight${pkEntityColumn.methodName}());
			Assert.assertEquals(parent${entity.name}.getLeft${pkEntityColumn.methodName}() + 1, child${entity.name}.getLeft${pkEntityColumn.methodName}());
			Assert.assertEquals(parent${entity.name}.getRight${pkEntityColumn.methodName}() - 1, child${entity.name}.getRight${pkEntityColumn.methodName}());
		}

		@Test
		public void testMoveTreeFromRight() throws Exception {
			long ${scopeEntityColumn.name} = RandomTestUtil.nextLong();

			${entity.name} root${entity.name} = add${entity.name}(${scopeEntityColumn.name}, null);

			long previousRootLeft${pkEntityColumn.methodName} = root${entity.name}.getLeft${pkEntityColumn.methodName}();
			long previousRootRight${pkEntityColumn.methodName} = root${entity.name}.getRight${pkEntityColumn.methodName}();

			${entity.name} parent${entity.name} = add${entity.name}(${scopeEntityColumn.name}, null);

			${entity.name} child${entity.name} = add${entity.name}(${scopeEntityColumn.name}, parent${entity.name}.get${pkEntityColumn.methodName}());

			parent${entity.name} = _persistence.fetchByPrimaryKey(parent${entity.name}.getPrimaryKey());

			parent${entity.name}.setParent${pkEntityColumn.methodName}(root${entity.name}.get${pkEntityColumn.methodName}());

			_persistence.update(parent${entity.name});

			root${entity.name} = _persistence.fetchByPrimaryKey(root${entity.name}.getPrimaryKey());
			child${entity.name} = _persistence.fetchByPrimaryKey(child${entity.name}.getPrimaryKey());

			Assert.assertEquals(previousRootLeft${pkEntityColumn.methodName}, root${entity.name}.getLeft${pkEntityColumn.methodName}());
			Assert.assertEquals(previousRootRight${pkEntityColumn.methodName} + 4, root${entity.name}.getRight${pkEntityColumn.methodName}());
			Assert.assertEquals(root${entity.name}.getLeft${pkEntityColumn.methodName}() + 1, parent${entity.name}.getLeft${pkEntityColumn.methodName}());
			Assert.assertEquals(root${entity.name}.getRight${pkEntityColumn.methodName}() - 1, parent${entity.name}.getRight${pkEntityColumn.methodName}());
			Assert.assertEquals(parent${entity.name}.getLeft${pkEntityColumn.methodName}() + 1, child${entity.name}.getLeft${pkEntityColumn.methodName}());
			Assert.assertEquals(parent${entity.name}.getRight${pkEntityColumn.methodName}() - 1, child${entity.name}.getRight${pkEntityColumn.methodName}());
		}

		@Test
		public void testMoveTreeIntoTreeFromLeft() throws Exception {
			long ${scopeEntityColumn.name} = RandomTestUtil.nextLong();

			${entity.name} parent${entity.name} = add${entity.name}(${scopeEntityColumn.name}, null);

			${entity.name} parentChild${entity.name} = add${entity.name}(${scopeEntityColumn.name}, parent${entity.name}.get${pkEntityColumn.methodName}());

			parent${entity.name} = _persistence.fetchByPrimaryKey(parent${entity.name}.getPrimaryKey());

			${entity.name} root${entity.name} = add${entity.name}(${scopeEntityColumn.name}, null);

			${entity.name} leftRootChild${entity.name} = add${entity.name}(${scopeEntityColumn.name}, root${entity.name}.get${pkEntityColumn.methodName}());

			root${entity.name} = _persistence.fetchByPrimaryKey(root${entity.name}.getPrimaryKey());

			${entity.name} rightRootChild${entity.name} = add${entity.name}(${scopeEntityColumn.name}, root${entity.name}.get${pkEntityColumn.methodName}());

			root${entity.name} = _persistence.fetchByPrimaryKey(root${entity.name}.getPrimaryKey());

			long previousRootLeft${pkEntityColumn.methodName} = root${entity.name}.getLeft${pkEntityColumn.methodName}();
			long previousRootRight${pkEntityColumn.methodName} = root${entity.name}.getRight${pkEntityColumn.methodName}();

			parent${entity.name}.setParent${pkEntityColumn.methodName}(rightRootChild${entity.name}.get${pkEntityColumn.methodName}());

			_persistence.update(parent${entity.name});

			root${entity.name} = _persistence.fetchByPrimaryKey(root${entity.name}.getPrimaryKey());
			leftRootChild${entity.name} = _persistence.fetchByPrimaryKey(leftRootChild${entity.name}.getPrimaryKey());
			rightRootChild${entity.name} = _persistence.fetchByPrimaryKey(rightRootChild${entity.name}.getPrimaryKey());
			parentChild${entity.name} = _persistence.fetchByPrimaryKey(parentChild${entity.name}.getPrimaryKey());

			Assert.assertEquals(previousRootLeft${pkEntityColumn.methodName} - 4, root${entity.name}.getLeft${pkEntityColumn.methodName}());
			Assert.assertEquals(previousRootRight${pkEntityColumn.methodName}, root${entity.name}.getRight${pkEntityColumn.methodName}());
			Assert.assertEquals(root${entity.name}.getLeft${pkEntityColumn.methodName}() + 1, leftRootChild${entity.name}.getLeft${pkEntityColumn.methodName}());
			Assert.assertEquals(root${entity.name}.getRight${pkEntityColumn.methodName}() - 7, leftRootChild${entity.name}.getRight${pkEntityColumn.methodName}());
			Assert.assertEquals(root${entity.name}.getLeft${pkEntityColumn.methodName}() + 3, rightRootChild${entity.name}.getLeft${pkEntityColumn.methodName}());
			Assert.assertEquals(root${entity.name}.getRight${pkEntityColumn.methodName}() - 1, rightRootChild${entity.name}.getRight${pkEntityColumn.methodName}());
			Assert.assertEquals(rightRootChild${entity.name}.getLeft${pkEntityColumn.methodName}() + 1, parent${entity.name}.getLeft${pkEntityColumn.methodName}());
			Assert.assertEquals(rightRootChild${entity.name}.getRight${pkEntityColumn.methodName}() - 1, parent${entity.name}.getRight${pkEntityColumn.methodName}());
			Assert.assertEquals(parent${entity.name}.getLeft${pkEntityColumn.methodName}() + 1, parentChild${entity.name}.getLeft${pkEntityColumn.methodName}());
			Assert.assertEquals(parent${entity.name}.getRight${pkEntityColumn.methodName}() - 1, parentChild${entity.name}.getRight${pkEntityColumn.methodName}());
		}

		@Test
		public void testMoveTreeIntoTreeFromRight() throws Exception {
			long ${scopeEntityColumn.name} = RandomTestUtil.nextLong();

			${entity.name} root${entity.name} = add${entity.name}(${scopeEntityColumn.name}, null);

			${entity.name} leftRootChild${entity.name} = add${entity.name}(${scopeEntityColumn.name}, root${entity.name}.get${pkEntityColumn.methodName}());

			root${entity.name} = _persistence.fetchByPrimaryKey(root${entity.name}.getPrimaryKey());

			${entity.name} rightRootChild${entity.name} = add${entity.name}(${scopeEntityColumn.name}, root${entity.name}.get${pkEntityColumn.methodName}());

			root${entity.name} = _persistence.fetchByPrimaryKey(root${entity.name}.getPrimaryKey());

			long previousRootLeft${pkEntityColumn.methodName} = root${entity.name}.getLeft${pkEntityColumn.methodName}();
			long previousRootRight${pkEntityColumn.methodName} = root${entity.name}.getRight${pkEntityColumn.methodName}();

			${entity.name} parent${entity.name} = add${entity.name}(${scopeEntityColumn.name}, null);

			${entity.name} parentChild${entity.name} = add${entity.name}(${scopeEntityColumn.name}, parent${entity.name}.get${pkEntityColumn.methodName}());

			parent${entity.name} = _persistence.fetchByPrimaryKey(parent${entity.name}.getPrimaryKey());

			parent${entity.name}.setParent${pkEntityColumn.methodName}(leftRootChild${entity.name}.get${pkEntityColumn.methodName}());

			_persistence.update(parent${entity.name});

			root${entity.name} = _persistence.fetchByPrimaryKey(root${entity.name}.getPrimaryKey());
			leftRootChild${entity.name} = _persistence.fetchByPrimaryKey(leftRootChild${entity.name}.getPrimaryKey());
			rightRootChild${entity.name} = _persistence.fetchByPrimaryKey(rightRootChild${entity.name}.getPrimaryKey());
			parentChild${entity.name} = _persistence.fetchByPrimaryKey(parentChild${entity.name}.getPrimaryKey());

			Assert.assertEquals(previousRootLeft${pkEntityColumn.methodName}, root${entity.name}.getLeft${pkEntityColumn.methodName}());
			Assert.assertEquals(previousRootRight${pkEntityColumn.methodName} + 4, root${entity.name}.getRight${pkEntityColumn.methodName}());
			Assert.assertEquals(root${entity.name}.getLeft${pkEntityColumn.methodName}() + 1, leftRootChild${entity.name}.getLeft${pkEntityColumn.methodName}());
			Assert.assertEquals(root${entity.name}.getRight${pkEntityColumn.methodName}() - 3, leftRootChild${entity.name}.getRight${pkEntityColumn.methodName}());
			Assert.assertEquals(root${entity.name}.getLeft${pkEntityColumn.methodName}() + 7, rightRootChild${entity.name}.getLeft${pkEntityColumn.methodName}());
			Assert.assertEquals(root${entity.name}.getRight${pkEntityColumn.methodName}() - 1, rightRootChild${entity.name}.getRight${pkEntityColumn.methodName}());
			Assert.assertEquals(leftRootChild${entity.name}.getLeft${pkEntityColumn.methodName}() + 1, parent${entity.name}.getLeft${pkEntityColumn.methodName}());
			Assert.assertEquals(leftRootChild${entity.name}.getRight${pkEntityColumn.methodName}() - 1, parent${entity.name}.getRight${pkEntityColumn.methodName}());
			Assert.assertEquals(parent${entity.name}.getLeft${pkEntityColumn.methodName}() + 1, parentChild${entity.name}.getLeft${pkEntityColumn.methodName}());
			Assert.assertEquals(parent${entity.name}.getRight${pkEntityColumn.methodName}() - 1, parentChild${entity.name}.getRight${pkEntityColumn.methodName}());
		}

		protected ${entity.name} add${entity.name}(long ${scopeEntityColumn.name}, Long parent${pkEntityColumn.methodName}) throws Exception {
			<#if entity.hasCompoundPK()>
				${entity.PKClassName} pk = new ${entity.PKClassName}(

				<#list entity.PKEntityColumns as entityColumn>
					<#if stringUtil.equals(entityColumn.type, "int")>
						RandomTestUtil.nextInt()
					<#elseif stringUtil.equals(entityColumn.type, "long")>
						RandomTestUtil.nextLong()
					<#elseif stringUtil.equals(entityColumn.type, "String")>
						<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

						<#if maxLength < 8>
							RandomTestUtil.randomString(${maxLength})
						<#else>
							RandomTestUtil.randomString()
						</#if>
					</#if>

					<#if entityColumn_has_next>
						,
					</#if>
				</#list>

				);
			<#else>
				<#assign entityColumn = entity.PKEntityColumns[0] />

				${entityColumn.type} pk =

				<#if stringUtil.equals(entityColumn.type, "int")>
					RandomTestUtil.nextInt()
				<#elseif stringUtil.equals(entityColumn.type, "long")>
					RandomTestUtil.nextLong()
				<#elseif stringUtil.equals(entityColumn.type, "String")>
					<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

					<#if maxLength < 8>
						RandomTestUtil.randomString(${maxLength})
					<#else>
						RandomTestUtil.randomString()
					</#if>
				</#if>

				;
			</#if>

			${entity.name} ${entity.variableName} = _persistence.create(pk);

			<#list entity.regularEntityColumns as entityColumn>
				<#if !entityColumn.primary && (validator.isNull(parentPKColumn) || (parentPKColumn.name != entityColumn.name))>
					<#if entityColumn.name ="${scopeEntityColumn.name}">
						${entity.variableName}.set${entityColumn.methodName}(${scopeEntityColumn.name});
					<#else>
						<#if stringUtil.equals(entityColumn.type, "Blob")>
							String ${entityColumn.name}String = RandomTestUtil.randomString();

							byte[] ${entityColumn.name}Bytes = ${entityColumn.name}String.getBytes("UTF-8");

							Blob ${entityColumn.name}Blob = new OutputBlob(new ByteArrayInputStream(${entityColumn.name}Bytes), ${entityColumn.name}Bytes.length);
						</#if>

						${entity.variableName}.set${entityColumn.methodName}(

						<#if stringUtil.equals(entityColumn.type, "boolean")>
							RandomTestUtil.randomBoolean()
						<#elseif stringUtil.equals(entityColumn.type, "double")>
							RandomTestUtil.nextDouble()
						<#elseif stringUtil.equals(entityColumn.type, "int")>
							RandomTestUtil.nextInt()
						<#elseif stringUtil.equals(entityColumn.type, "long")>
							RandomTestUtil.nextLong()
						<#elseif stringUtil.equals(entityColumn.type, "Blob")>
							${entityColumn.name}Blob
						<#elseif stringUtil.equals(entityColumn.type, "Date")>
							RandomTestUtil.nextDate()
						<#elseif stringUtil.equals(entityColumn.type, "String")>
							<#assign maxLength = serviceBuilder.getMaxLength(entity.getName(), entityColumn) />

							<#if maxLength < 8>
								RandomTestUtil.randomString(${maxLength})
							<#else>
								RandomTestUtil.randomString()
							</#if>
						<#elseif stringUtil.equals(entityColumn.type, "Map")>
							new HashMap();
						</#if>

						);
					</#if>
				</#if>
			</#list>

			if (parent${pkEntityColumn.methodName} != null) {
				${entity.variableName}.setParent${pkEntityColumn.methodName}(parent${pkEntityColumn.methodName});
			}

			_${entity.pluralVariableName}.add(_persistence.update(${entity.variableName}));

			return ${entity.variableName};
		}
	</#if>

	private List<${entity.name}> _${entity.pluralVariableName} = new ArrayList<${entity.name}>();
	private ${entity.name}Persistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}