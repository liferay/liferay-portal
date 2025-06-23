package ${configYAML.apiPackagePath}.resource.${escapedVersion}.test;

<#list allExternalSchemas?keys as schemaName>
	import ${configYAML.apiPackagePath}.client.dto.${escapedVersion}.${schemaName};
	import ${configYAML.apiPackagePath}.client.resource.${escapedVersion}.${schemaName}Resource;
	import ${configYAML.apiPackagePath}.client.serdes.${escapedVersion}.${schemaName}SerDes;
</#list>

<#list allSchemas?keys as schemaName>
	import ${configYAML.apiPackagePath}.client.dto.${escapedVersion}.${schemaName};
	import ${configYAML.apiPackagePath}.client.resource.${escapedVersion}.${schemaName}Resource;
	import ${configYAML.apiPackagePath}.client.serdes.${escapedVersion}.${schemaName}SerDes;
</#list>

import ${configYAML.apiPackagePath}.client.http.HttpInvoker;
import ${configYAML.apiPackagePath}.client.pagination.Page;
import ${configYAML.apiPackagePath}.client.pagination.Pagination;
import ${configYAML.apiPackagePath}.client.permission.Permission;
import ${configYAML.apiPackagePath}.client.resource.${escapedVersion}.${schemaName}Resource;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

<#if freeMarkerTool.isVersionCompatible(configYAML, 2)>
	import com.liferay.petra.function.transform.TransformUtil;

<#else>
	import com.liferay.portal.vulcan.util.TransformUtil;
</#if>

<#assign
	generatePermissionsJavaMethodSignatures = []
	javaDataType = freeMarkerTool.getJavaDataType(configYAML, openAPIYAML, schemaName)!""
	javaMethodSignatures = freeMarkerTool.getResourceTestCaseJavaMethodSignatures(configYAML, openAPIYAML, schemaName)
	properties = freeMarkerTool.getDTOProperties(configYAML, openAPIYAML, schema, allSchemas)

	generateBatch = freeMarkerTool.generateBatch(configYAML, javaDataType, javaMethodSignatures, schemaName)
	generateCRUD = freeMarkerTool.generateCRUD(configYAML, javaMethodSignatures, schemaName)
	generateDepotEntry = freeMarkerTool.containsJavaMethodSignature(javaMethodSignatures, "AssetLibrary")
	useDeleteAssetLibrary = freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, "deleteAssetLibrary" + schemaName) && properties?keys?seq_contains("externalReferenceCode")
	useDeleteByExternalReferenceCode = (freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, "deleteByExternalReferenceCode") || freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, "delete" + schemaName + "ByExternalReferenceCode")) && properties?keys?seq_contains("externalReferenceCode")
	useDeleteById = freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, "delete" + schemaName) && (properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id"))
	useDeleteSite = freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, "deleteSite" + schemaName) && properties?keys?seq_contains("externalReferenceCode")

	generateWaitForFinishMethod = freeMarkerTool.isVersionCompatible(configYAML, 8) && generateBatch && (useDeleteAssetLibrary || useDeleteByExternalReferenceCode || useDeleteById || useDeleteSite)
/>

<#list javaMethodSignatures as javaMethodSignature>
	<#if freeMarkerTool.isGeneratePermissions(configYAML, javaMethodSignature, javaMethodSignatures, schema, schemaName)>
		<#assign generatePermissionsJavaMethodSignatures = generatePermissionsJavaMethodSignatures + [javaMethodSignature] />
	</#if>
</#list>

<#if generateDepotEntry>
	import com.liferay.depot.model.DepotEntry;
	import com.liferay.depot.service.DepotEntryLocalServiceUtil;
</#if>

<#if generateWaitForFinishMethod>
	import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
	import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
	import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
</#if>

import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegate;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegateBuilder;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegateBuilderRegistry;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import java.io.File;

import java.lang.reflect.Method;

import java.net.URI;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

<#if schema.discriminator?has_content>
	import java.util.function.Supplier;
</#if>

import ${configYAML.javaEEPackage}.annotation.Generated;

import ${configYAML.javaEEPackage}.servlet.http.HttpServletRequest;

import ${configYAML.javaEEPackage}.ws.rs.core.MultivaluedHashMap;
import ${configYAML.javaEEPackage}.ws.rs.core.MultivaluedMap;
import ${configYAML.javaEEPackage}.ws.rs.core.PathSegment;
import ${configYAML.javaEEPackage}.ws.rs.core.UriBuilder;
import ${configYAML.javaEEPackage}.ws.rs.core.UriInfo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author ${configYAML.author}
 * @generated
 */
@Generated("")
public abstract class Base${schemaName}ResourceTestCase {

	@ClassRule
	@Rule
	<#if generateCRUD>
		public static final AggregateTestRule aggregateTestRule = new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);
	<#else>
		public static final LiferayIntegrationTestRule liferayIntegrationTestRule = new LiferayIntegrationTestRule();
	</#if>

	@BeforeClass
	public static void setUpClass() throws Exception {
		_format = FastDateFormatFactoryUtil.getSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(testGroup.getCompanyId());

		<#if generateDepotEntry>
			irrelevantDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
				Collections.singletonMap(LocaleUtil.getDefault(), RandomTestUtil.randomString()), null,
				new ServiceContext() {
					{
						setCompanyId(testCompany.getCompanyId());
						setUserId(TestPropsValues.getUserId());
					}
				});
			irrelevantDepotEntryGroup = irrelevantDepotEntry.getGroup();
			testDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
				Collections.singletonMap(LocaleUtil.getDefault(), RandomTestUtil.randomString()), null,
				new ServiceContext() {
					{
						setCompanyId(testCompany.getCompanyId());
						setUserId(TestPropsValues.getUserId());
					}
				});
			testDepotEntryGroup = testDepotEntry.getGroup();
		</#if>

		_${schemaVarName}Resource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		${schemaVarName}Resource = ${schemaName}Resource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		<#if generateWaitForFinishMethod>
			importTaskResource = ImportTaskResource.builder(
			).authentication(
				_testCompanyAdminUser.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(), 8080, "http"
			).locale(
				LocaleUtil.getDefault()
			).build();
		</#if>

		<#if (generatePermissionsJavaMethodSignatures?size > 0)>
			permissions${schemaName}Resource = ${schemaName}Resource.builder(
			).authentication(
				_testCompanyAdminUser.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(), 8080, "http"
			).locale(
				LocaleUtil.getDefault()
			).parameter(
				"nestedFields", "permissions"
			).build();
		</#if>
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(irrelevantGroup);
		GroupTestUtil.deleteGroup(testGroup);
	}

	<#if javaDataTypeMap?keys?seq_contains(schemaName)>
		@Test
		public void testClientSerDesToDTO() throws Exception {
			ObjectMapper objectMapper = getClientSerDesObjectMapper();

			${schemaName} ${schemaVarName}1 = random${schemaName}();

			String json = objectMapper.writeValueAsString(${schemaVarName}1);

			${schemaName} ${schemaVarName}2 = ${schemaName}SerDes.toDTO(json);

			Assert.assertTrue(equals(${schemaVarName}1, ${schemaVarName}2));
		}

		@Test
		public void testClientSerDesToJSON() throws Exception {
			ObjectMapper objectMapper = getClientSerDesObjectMapper();

			${schemaName} ${schemaVarName} = random${schemaName}();

			String json1 = objectMapper.writeValueAsString(${schemaVarName});
			String json2 = ${schemaName}SerDes.toJSON(${schemaVarName});

			Assert.assertEquals(
				objectMapper.readTree(json1), objectMapper.readTree(json2));
		}

		protected ObjectMapper getClientSerDesObjectMapper() {
			return new ObjectMapper() {
				{
					configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
					configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
					enable(SerializationFeature.INDENT_OUTPUT);
					setDateFormat(new ISO8601DateFormat());
					setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
					setSerializationInclusion(JsonInclude.Include.NON_NULL);
					setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
					setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
				}
			};
		}

		@Test
		public void testEscapeRegexInStringFields() throws Exception {
			String regex = "^[0-9]+(\\.[0-9]{1,2})\"?";

			${schemaName} ${schemaVarName} = random${schemaName}();

			<#list properties?keys as propertyName>
				<#if stringUtil.equals(properties[propertyName], "String")>
					${schemaVarName}.set${propertyName?cap_first}(regex);
				</#if>
			</#list>

			String json = ${schemaName}SerDes.toJSON(${schemaVarName});

			Assert.assertFalse(json.contains(regex));

			${schemaVarName} = ${schemaName}SerDes.toDTO(json);

			<#list properties?keys as propertyName>
				<#if stringUtil.equals(properties[propertyName], "String")>
					Assert.assertEquals(regex, ${schemaVarName}.get${propertyName?cap_first}());
				</#if>
			</#list>
		}
	</#if>

	<#assign
		enumSchemas = freeMarkerTool.getDTOEnumSchemas(configYAML, openAPIYAML, schema)
		generateGetMultipartFilesMethod = false
		generateSchemaGraphQLAddMethod = false
		generateSearchTestRule = false
		getIdMethodName = properties?keys?seq_contains("id")?then("getId", "get" + schemaName + "Id")
		idParameterName = properties?keys?seq_contains("id")?then("id", schemaVarName + "Id")
		randomDataTypes = ["Boolean", "Double", "Integer", "Long", "String"]
	/>

	<#list javaMethodSignatures as javaMethodSignature>
		<#assign parameters = freeMarkerTool.getResourceTestCaseParameters(configYAML, javaMethodSignature.javaMethodParameters, javaMethodSignature.operation, allSchemas, false) />

		<#if freeMarkerTool.isVersionCompatible(configYAML, 8) && stringUtil.equals(javaMethodSignature.methodName, "delete" + schemaName + "Batch")>
			<#if !useDeleteByExternalReferenceCode && !useDeleteById>
				<#continue>
			</#if>

			@Test
			public void testDelete${schemaName}Batch() throws Exception {
				<#assign
					getJavaMethodSignature = (freeMarkerTool.getJavaMethodSignature(javaMethodSignatures, "get" + schemaName))!""
					getterJavaMethodParametersMap = {}
				 />

				<#if useDeleteByExternalReferenceCode>
					${schemaName} ${schemaVarName}1 = test${javaMethodSignature.methodName?cap_first}_add${schemaName}();

					test${javaMethodSignature.methodName?cap_first}_delete${schemaName}(202, ${schemaVarName}1.getExternalReferenceCode(), null);

					<#if getJavaMethodSignature?has_content>
						assertHttpResponseStatusCode(
							404,
							${schemaVarName}Resource.${getJavaMethodSignature.methodName}HttpResponse(
								<@getGetParameters
									javaMethodSignature = getJavaMethodSignature
									testJavaMethodName = javaMethodSignature.methodName
									varName = schemaVarName + "1"
								/>));
					</#if>
				</#if>

				<#if useDeleteById>
					<#if !useDeleteByExternalReferenceCode>${schemaName}</#if> ${schemaVarName}1 = test${javaMethodSignature.methodName?cap_first}_add${schemaName}();

					test${javaMethodSignature.methodName?cap_first}_delete${schemaName}(202, null, ${schemaVarName}1.${getIdMethodName}());

					<#if getJavaMethodSignature?has_content>
						assertHttpResponseStatusCode(
							404,
							${schemaVarName}Resource.${getJavaMethodSignature.methodName}HttpResponse(
								<@getGetParameters
									javaMethodSignature = getJavaMethodSignature
									testJavaMethodName = javaMethodSignature.methodName
									varName = schemaVarName + "1"
								/>));
					</#if>
				</#if>

				<#if useDeleteByExternalReferenceCode && useDeleteById>
					${schemaVarName}1 = test${javaMethodSignature.methodName?cap_first}_add${schemaName}();
					${schemaName} ${schemaVarName}2 = test${javaMethodSignature.methodName?cap_first}_add${schemaName}();

					test${javaMethodSignature.methodName?cap_first}_delete${schemaName}(202, ${schemaVarName}2.getExternalReferenceCode(), ${schemaVarName}1.${getIdMethodName}());

					<#if getJavaMethodSignature?has_content>
						assertHttpResponseStatusCode(
							404,
							${schemaVarName}Resource.${getJavaMethodSignature.methodName}HttpResponse(
								<@getGetParameters
									javaMethodSignature = getJavaMethodSignature
									testJavaMethodName = javaMethodSignature.methodName
									varName = schemaVarName + "1"
								/>));
						assertHttpResponseStatusCode(
							200,
							${schemaVarName}Resource.${getJavaMethodSignature.methodName}HttpResponse(
								<@getGetParameters
									javaMethodSignature = getJavaMethodSignature
									testJavaMethodName = javaMethodSignature.methodName
									varName = schemaVarName + "2"
								/>));
					</#if>

					test${javaMethodSignature.methodName?cap_first}_delete${schemaName}(202, ${schemaVarName}2.getExternalReferenceCode(), ${schemaVarName}1.${getIdMethodName}());

					<#if getJavaMethodSignature?has_content>
						assertHttpResponseStatusCode(
							404,
							${schemaVarName}Resource.${getJavaMethodSignature.methodName}HttpResponse(
								<@getGetParameters
									javaMethodSignature = getJavaMethodSignature
									testJavaMethodName = javaMethodSignature.methodName
									varName = schemaVarName + "2"
								/>));
					</#if>
				</#if>
			}

			protected ${schemaName} test${javaMethodSignature.methodName?cap_first}_add${schemaName}() throws Exception {
				<#if (properties?keys?seq_contains("externalReferenceCode") || properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id")) && freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, "delete" + schemaName)>
					return testDelete${schemaName}_add${schemaName}();
				<#else>
					throw new UnsupportedOperationException("This method needs to be implemented");
				</#if>
			}

			protected void test${javaMethodSignature.methodName?cap_first}_delete${schemaName}(int expectedStatusCode, String externalReferenceCode, ${properties[idParameterName]} id) throws Exception {
				HttpInvoker.HttpResponse httpResponse = ${schemaVarName}Resource.${javaMethodSignature.methodName}HttpResponse(
					<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
						<#if freeMarkerTool.isQueryParameter(javaMethodParameter, javaMethodSignature.operation)>
							null,
						</#if>
					</#list>

					JSONUtil.putAll(
						JSONUtil.put(
							"externalReferenceCode", () -> externalReferenceCode
						).put(
							"${idParameterName}", () -> id
						)));

				Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

				waitForFinish("COMPLETED", JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
			}

			<@getTestGetterMethods
				getterJavaMethodParametersMap = getterJavaMethodParametersMap
				testJavaMethodName = javaMethodSignature.methodName
			/>
		<#elseif stringUtil.endsWith(javaMethodSignature.methodName, schemaName + "Batch") || stringUtil.endsWith(javaMethodSignature.methodName, schemaNames + "PageExportBatch")>
			<#continue>
		<#elseif freeMarkerTool.hasHTTPMethod(javaMethodSignature, "delete")>
			<#assign getterJavaMethodParametersMap = {} />

			@Test
			public void test${javaMethodSignature.methodName?cap_first}() throws Exception {
				<#if properties?keys?seq_contains("externalReferenceCode") || properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id")>
					@SuppressWarnings("PMD.UnusedLocalVariable")
					${schemaName} ${schemaVarName} = test${javaMethodSignature.methodName?cap_first}_add${schemaName}();

					assertHttpResponseStatusCode(
						204,
						${schemaVarName}Resource.${javaMethodSignature.methodName}HttpResponse(
							<@getGetParameters
								allowBodyParameters = true
								allowQueryParameter = false
								javaMethodSignature = javaMethodSignature
								testJavaMethodName = javaMethodSignature.methodName
								varName = schemaVarName
							/>));

					<#if freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, "get" + javaMethodSignature.methodName?remove_beginning("delete"))>
						<#assign getJavaMethodSignature = freeMarkerTool.getJavaMethodSignature(javaMethodSignatures, "get" + javaMethodSignature.methodName?remove_beginning("delete")) />

						assertHttpResponseStatusCode(
							404,
							${schemaVarName}Resource.${getJavaMethodSignature.methodName}HttpResponse(
								<@getGetParameters
									javaMethodSignature = getJavaMethodSignature
									testJavaMethodName = javaMethodSignature.methodName
									varName = schemaVarName
								/>));
						assertHttpResponseStatusCode(
							404,
							${schemaVarName}Resource.${getJavaMethodSignature.methodName}HttpResponse(
								<@getGetParameters
									defaultParameter = true
									javaMethodSignature = getJavaMethodSignature
									testJavaMethodName = javaMethodSignature.methodName
									varName = schemaVarName
								/>));
					</#if>
				<#else>
					Assert.assertTrue(false);
				</#if>
			}

			<@getTestAdderMethod javaMethodSignature = javaMethodSignature />

			<@getTestGetterMethods
				getterJavaMethodParametersMap = getterJavaMethodParametersMap
				testJavaMethodName = javaMethodSignature.methodName
			/>
		<#elseif freeMarkerTool.hasHTTPMethod(javaMethodSignature, "get") && javaMethodSignature.returnType?contains("Page<")>
			<#if javaMethodSignature.methodName?contains("Permission")>
				@Test
				public void test${javaMethodSignature.methodName?cap_first}() throws Exception {
					@SuppressWarnings("PMD.UnusedLocalVariable")
					${schemaName} post${schemaName} = test${javaMethodSignature.methodName?cap_first}_add${schemaName}();

					Page<Permission> page = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
						<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
							<#if freeMarkerTool.isExternalReferenceCodeParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
								post${schemaName}.getExternalReferenceCode()
							<#elseif freeMarkerTool.isIdParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
								post${schemaName}.${getIdMethodName}()
							<#elseif stringUtil.equals(javaMethodParameter.parameterName, "assetLibraryExternalReferenceCode")>
								testDepotEntryGroup.getExternalReferenceCode()
							<#elseif stringUtil.equals(javaMethodParameter.parameterName, "assetLibraryId")>
								testDepotEntry.getDepotEntryId()
							<#elseif stringUtil.equals(javaMethodParameter.parameterName, "roleNames")>
								RoleConstants.GUEST
							<#elseif stringUtil.equals(javaMethodParameter.parameterName, "siteExternalReferenceCode")>
								testGroup.getExternalReferenceCode()
							<#elseif stringUtil.equals(javaMethodParameter.parameterName, "siteId")>
								testGroup.getGroupId()
							<#else>
								null
							</#if>

							<#sep>, </#sep>
						</#list>
					);

					Assert.assertNotNull(page);
				}

				<@getTestAdderMethod javaMethodSignature = javaMethodSignature />
			<#elseif !javaMethodSignature.methodName?contains("Permission")>
				@Test
				public void test${javaMethodSignature.methodName?cap_first}() throws Exception {
					<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
						${javaMethodParameter.parameterType} ${javaMethodParameter.parameterName} = test${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}();
						${javaMethodParameter.parameterType} irrelevant${javaMethodParameter.parameterName?cap_first} = test${javaMethodSignature.methodName?cap_first}_getIrrelevant${javaMethodParameter.parameterName?cap_first}();
					</#list>

					Page<${schemaName}> page = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
						<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
							<#if stringUtil.equals(javaMethodParameter.parameterName, "keywords")>
								null
							<#elseif stringUtil.equals(javaMethodParameter.parameterName, "pagination")>
								Pagination.of(1, 10)
							<#elseif stringUtil.equals(javaMethodParameter.parameterName, "search")>
								null
							<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
								${javaMethodParameter.parameterName}
							<#elseif stringUtil.equals(javaMethodParameter.parameterType, "java.lang.String")>
								RandomTestUtil.randomString()
							<#elseif stringUtil.equals(javaMethodParameter.parameterType, "boolean")>
								RandomTestUtil.randomBoolean()
							<#elseif stringUtil.equals(javaMethodParameter.parameterType, "double")>
								RandomTestUtil.randomDouble()
							<#elseif stringUtil.equals(javaMethodParameter.parameterType, "long")>
								RandomTestUtil.randomLong()
							<#elseif stringUtil.equals(javaMethodParameter.parameterType, "java.util.Date")>
								RandomTestUtil.nextDate()
							<#else>
								null
							</#if>

							<#sep>, </#sep>
						</#list>
					);

					long totalCount = page.getTotalCount();

					<#if freeMarkerTool.hasPathParameter(javaMethodSignature)>
						if (<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
								(irrelevant${javaMethodParameter.parameterName?cap_first} != null)

								<#sep>&& </#sep>
							</#list>) {

							${schemaName} irrelevant${schemaName} = test${javaMethodSignature.methodName?cap_first}_add${schemaName}(
								<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
									irrelevant${javaMethodParameter.parameterName?cap_first},
								</#list>

								randomIrrelevant${schemaName}());

							page = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
								<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
									<#if stringUtil.equals(javaMethodParameter.parameterName, "pagination")>
										Pagination.of(1, (int) totalCount + 1)
									<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
										irrelevant${javaMethodParameter.parameterName?cap_first}
									<#else>
										null
									</#if>

									<#sep>, </#sep>
								</#list>
							);

							Assert.assertEquals(totalCount + 1, page.getTotalCount());

							assertContains(irrelevant${schemaName}, (List<${schemaName}>)page.getItems());
							assertValid(
								page,
								test${javaMethodSignature.methodName?cap_first}_getExpectedActions(
									<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
										irrelevant${javaMethodParameter.parameterName?cap_first}<#sep>, </#sep>
									</#list>
								));
						}
					</#if>

					${schemaName} ${schemaVarName}1 = test${javaMethodSignature.methodName?cap_first}_add${schemaName}(
						<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
							${javaMethodParameter.parameterName},
						</#list>

						random${schemaName}());

					${schemaName} ${schemaVarName}2 = test${javaMethodSignature.methodName?cap_first}_add${schemaName}(
						<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
							${javaMethodParameter.parameterName},
						</#list>

						random${schemaName}());

					page = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
						<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
							<#if stringUtil.equals(javaMethodParameter.parameterName, "pagination")>
								Pagination.of(1, 10)
							<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
								${javaMethodParameter.parameterName}
							<#else>
								null
							</#if>

							<#sep>, </#sep>
						</#list>
					);

					Assert.assertEquals(totalCount + 2, page.getTotalCount());

					assertContains(${schemaVarName}1, (List<${schemaName}>)page.getItems());
					assertContains(${schemaVarName}2, (List<${schemaName}>)page.getItems());
					assertValid(page, test${javaMethodSignature.methodName?cap_first}_getExpectedActions(
						<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
							${javaMethodParameter.parameterName}<#sep>, </#sep>
						</#list>
					));

					<#if generatePermissionsJavaMethodSignatures?seq_contains(javaMethodSignature)>
						for (${schemaName} ${schemaVarName} : page.getItems()) {
							Assert.assertNull(${schemaVarName}.getPermissions());
						}

						page = permissions${schemaName}Resource.${javaMethodSignature.methodName}(
							<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
								<#if stringUtil.equals(javaMethodParameter.parameterName, "pagination")>
									Pagination.of(1, 10)
								<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
									${javaMethodParameter.parameterName}
								<#else>
									null
								</#if>

								<#sep>, </#sep>
							</#list>
						);

						for (${schemaName} ${schemaVarName} : page.getItems()) {
							Assert.assertNotNull(${schemaVarName}.getPermissions());
						}
					</#if>

					<#if freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, "delete" + schemaName)>
						<#assign deleteJavaMethodSignature = freeMarkerTool.getJavaMethodSignature(javaMethodSignatures, "delete" + schemaName) />

						<#if properties?keys?seq_contains("externalReferenceCode") || properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id")>
							${schemaVarName}Resource.delete${schemaName}(
								<#list deleteJavaMethodSignature.javaMethodParameters as javaMethodParameter>
									<#if freeMarkerTool.isPathParameter(javaMethodParameter, deleteJavaMethodSignature.operation) && freeMarkerTool.isExternalReferenceCodeParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, deleteJavaMethodSignature.path, schemaName)>
										${schemaVarName}1.getExternalReferenceCode()
									<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, deleteJavaMethodSignature.operation) && freeMarkerTool.isIdParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, deleteJavaMethodSignature.path, schemaName)>
										${schemaVarName}1.${getIdMethodName}()
									<#else>
										null
									</#if>

									<#sep>, </#sep>
								</#list>
							);

							${schemaVarName}Resource.delete${schemaName}(
								<#list deleteJavaMethodSignature.javaMethodParameters as javaMethodParameter>
									<#if freeMarkerTool.isPathParameter(javaMethodParameter, deleteJavaMethodSignature.operation) && freeMarkerTool.isExternalReferenceCodeParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, deleteJavaMethodSignature.path, schemaName)>
										${schemaVarName}2.getExternalReferenceCode()
									<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, deleteJavaMethodSignature.operation) && freeMarkerTool.isIdParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, deleteJavaMethodSignature.path, schemaName)>
										${schemaVarName}2.${getIdMethodName}()
									<#else>
										null
									</#if>

									<#sep>, </#sep>
								</#list>
							);
						</#if>
					</#if>
				}

				protected Map<String, Map<String, String>> test${javaMethodSignature.methodName?cap_first}_getExpectedActions(
					<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
						${javaMethodParameter.parameterType} ${javaMethodParameter.parameterName}<#sep>, </#sep>
					</#list>
				) throws Exception {
					Map<String, Map<String, String>> expectedActions = new HashMap<>();

					<#if (javaMethodSignature.pathJavaMethodParameters?size == 1) && freeMarkerTool.hasPath(javaMethodSignatures, javaMethodSignature.path + "/batch")>
						<#assign firstPathJavaMethodParameter = javaMethodSignature.pathJavaMethodParameters[0] />

						Map createBatchAction = new HashMap<>();
						createBatchAction.put("method", "POST");
						createBatchAction.put("href", "http://localhost:8080/o${configYAML.application.baseURI}/${openAPIYAML.info.version}${javaMethodSignature.path}/batch".replace("{${firstPathJavaMethodParameter.parameterName}}", String.valueOf(${firstPathJavaMethodParameter.parameterName})));

						expectedActions.put("createBatch", createBatchAction);
					</#if>

					return expectedActions;
				}

				<#if parameters?contains("com.liferay.portal.kernel.search.filter.Filter filter")>
					<#assign generateSearchTestRule = true />

					@Test
					public void test${javaMethodSignature.methodName?cap_first}WithFilterDateTimeEquals() throws Exception {
						List<EntityField> entityFields = getEntityFields(EntityField.Type.DATE_TIME);

						if (entityFields.isEmpty()) {
							return;
						}

						<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
							${javaMethodParameter.parameterType} ${javaMethodParameter.parameterName} = test${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}();
						</#list>

						${schemaName} ${schemaVarName}1 = random${schemaName}();

						${schemaVarName}1 = test${javaMethodSignature.methodName?cap_first}_add${schemaName}(
							<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
								${javaMethodParameter.parameterName},
							</#list>

							${schemaVarName}1);

						for (EntityField entityField : entityFields) {
							Page<${schemaName}> page = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
								<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
									<#if stringUtil.equals(javaMethodParameter.parameterName, "filter")>
										getFilterString(entityField, "between", ${schemaVarName}1)
									<#elseif stringUtil.equals(javaMethodParameter.parameterName, "pagination")>
										Pagination.of(1, 2)
									<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
										${javaMethodParameter.parameterName}
									<#else>
										null
									</#if>

									<#sep>, </#sep>
								</#list>
							);

							assertEquals(Collections.singletonList(${schemaVarName}1), (List<${schemaName}>)page.getItems());
						}
					}

					@Test
					public void test${javaMethodSignature.methodName?cap_first}WithFilterDoubleEquals() throws Exception {
						test${javaMethodSignature.methodName?cap_first}WithFilter("eq", EntityField.Type.DOUBLE);
					}

					@Test
					public void test${javaMethodSignature.methodName?cap_first}WithFilterStringContains() throws Exception {
						test${javaMethodSignature.methodName?cap_first}WithFilter("contains", EntityField.Type.STRING);
					}

					@Test
					public void test${javaMethodSignature.methodName?cap_first}WithFilterStringEquals() throws Exception {
						test${javaMethodSignature.methodName?cap_first}WithFilter("eq", EntityField.Type.STRING);
					}

					@Test
					public void test${javaMethodSignature.methodName?cap_first}WithFilterStringStartsWith() throws Exception {
						test${javaMethodSignature.methodName?cap_first}WithFilter("startswith", EntityField.Type.STRING);
					}

					protected void test${javaMethodSignature.methodName?cap_first}WithFilter(String operator, EntityField.Type type)
						throws Exception {

						List<EntityField> entityFields = getEntityFields(type);

						if (entityFields.isEmpty()) {
							return;
						}

						<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
							${javaMethodParameter.parameterType} ${javaMethodParameter.parameterName} = test${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}();
						</#list>

						${schemaName} ${schemaVarName}1 = test${javaMethodSignature.methodName?cap_first}_add${schemaName}(

						<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
							${javaMethodParameter.parameterName},
						</#list>

						random${schemaName}());

						@SuppressWarnings("PMD.UnusedLocalVariable")
						${schemaName} ${schemaVarName}2 = test${javaMethodSignature.methodName?cap_first}_add${schemaName}(
							<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
								${javaMethodParameter.parameterName},
							</#list>

							random${schemaName}());

						for (EntityField entityField : entityFields) {
							Page<${schemaName}> page = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
								<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
									<#if stringUtil.equals(javaMethodParameter.parameterName, "filter")>
										getFilterString(entityField, operator, ${schemaVarName}1)
									<#elseif stringUtil.equals(javaMethodParameter.parameterName, "pagination")>
										Pagination.of(1, 2)
									<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
										${javaMethodParameter.parameterName}
									<#else>
										null
									</#if>

									<#sep>, </#sep>
								</#list>
							);

							assertEquals(Collections.singletonList(${schemaVarName}1), (List<${schemaName}>)page.getItems());
						}
					}
				</#if>

				<#if parameters?contains("Pagination pagination")>
					@Test
					public void test${javaMethodSignature.methodName?cap_first}WithPagination() throws Exception {
						<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
							${javaMethodParameter.parameterType} ${javaMethodParameter.parameterName} = test${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}();
						</#list>

						Page<${schemaName}> ${schemaVarNames}Page = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
							<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
								<#if freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
									${javaMethodParameter.parameterName}
								<#else>
									null
								</#if>

								<#sep>, </#sep>
							</#list>
						);

						int totalCount = GetterUtil.getInteger(${schemaVarNames}Page.getTotalCount());

						${schemaName} ${schemaVarName}1 = test${javaMethodSignature.methodName?cap_first}_add${schemaName}(
							<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
								${javaMethodParameter.parameterName},
							</#list>

							random${schemaName}());

						${schemaName} ${schemaVarName}2 = test${javaMethodSignature.methodName?cap_first}_add${schemaName}(
							<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
								${javaMethodParameter.parameterName},
							</#list>

							random${schemaName}());

						${schemaName} ${schemaVarName}3 = test${javaMethodSignature.methodName?cap_first}_add${schemaName}(
							<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
								${javaMethodParameter.parameterName},
							</#list>

							random${schemaName}());

						// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

						int pageSizeLimit = 500;

						if (totalCount >= (pageSizeLimit - 2)) {
							Page<${schemaName}> page1 = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
								<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
									<#if stringUtil.equals(javaMethodParameter.parameterName, "pagination")>
										Pagination.of((int) Math.ceil((totalCount + 1.0) / pageSizeLimit), pageSizeLimit)
									<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
										${javaMethodParameter.parameterName}
									<#else>
										null
									</#if>

									<#sep>, </#sep>
								</#list>
							);

							Assert.assertEquals(totalCount + 3, page1.getTotalCount());

							assertContains(${schemaVarName}1, (List<${schemaName}>)page1.getItems());

							Page<${schemaName}> page2 = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
								<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
									<#if stringUtil.equals(javaMethodParameter.parameterName, "pagination")>
										Pagination.of((int) Math.ceil((totalCount + 2.0) / pageSizeLimit), pageSizeLimit)
									<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
										${javaMethodParameter.parameterName}
									<#else>
										null
									</#if>

									<#sep>, </#sep>
								</#list>
							);

							assertContains(${schemaVarName}2, (List<${schemaName}>)page2.getItems());

							Page<${schemaName}> page3 = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
								<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
									<#if stringUtil.equals(javaMethodParameter.parameterName, "pagination")>
										Pagination.of((int) Math.ceil((totalCount + 3.0) / pageSizeLimit), pageSizeLimit)
									<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
										${javaMethodParameter.parameterName}
									<#else>
										null
									</#if>

									<#sep>, </#sep>
								</#list>
							);

							assertContains(${schemaVarName}3, (List<${schemaName}>)page3.getItems());
						}
						else {
							Page<${schemaName}> page1 = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
								<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
									<#if stringUtil.equals(javaMethodParameter.parameterName, "pagination")>
										Pagination.of(1, totalCount + 2)
									<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
										${javaMethodParameter.parameterName}
									<#else>
										null
									</#if>

									<#sep>, </#sep>
								</#list>
							);

							List<${schemaName}> ${schemaVarNames}1 = (List<${schemaName}>)page1.getItems();

							Assert.assertEquals(${schemaVarNames}1.toString(), totalCount + 2, ${schemaVarNames}1.size());

							Page<${schemaName}> page2 = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
								<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
									<#if stringUtil.equals(javaMethodParameter.parameterName, "pagination")>
										Pagination.of(2, totalCount + 2)
									<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
										${javaMethodParameter.parameterName}
									<#else>
										null
									</#if>

									<#sep>, </#sep>
								</#list>
							);

							Assert.assertEquals(totalCount + 3, page2.getTotalCount());

							List<${schemaName}> ${schemaVarNames}2 = (List<${schemaName}>)page2.getItems();

							Assert.assertEquals(${schemaVarNames}2.toString(), 1, ${schemaVarNames}2.size());

							Page<${schemaName}> page3 = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
								<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
									<#if stringUtil.equals(javaMethodParameter.parameterName, "pagination")>
										Pagination.of(1, (int) totalCount + 3)
									<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
										${javaMethodParameter.parameterName}
									<#else>
										null
									</#if>

									<#sep>, </#sep>
								</#list>
							);

							assertContains(${schemaVarName}1, (List<${schemaName}>)page3.getItems());
							assertContains(${schemaVarName}2, (List<${schemaName}>)page3.getItems());
							assertContains(${schemaVarName}3, (List<${schemaName}>)page3.getItems());
						}
					}
				</#if>

				<#if parameters?contains("com.liferay.portal.kernel.search.Sort[] sorts")>
					@Test
					public void test${javaMethodSignature.methodName?cap_first}WithSortDateTime() throws Exception {
						test${javaMethodSignature.methodName?cap_first}WithSort(
							EntityField.Type.DATE_TIME,
							(entityField, ${schemaVarName}1, ${schemaVarName}2) -> {
								BeanTestUtil.setProperty(${schemaVarName}1, entityField.getName(), new Date(System.currentTimeMillis() - 2 * Time.MINUTE));
							});
					}

					@Test
					public void test${javaMethodSignature.methodName?cap_first}WithSortDouble() throws Exception {
						test${javaMethodSignature.methodName?cap_first}WithSort(
							EntityField.Type.DOUBLE,
							(entityField, ${schemaVarName}1, ${schemaVarName}2) -> {
								BeanTestUtil.setProperty(${schemaVarName}1, entityField.getName(), 0.1);
								BeanTestUtil.setProperty(${schemaVarName}2, entityField.getName(), 0.5);
							});
					}

					@Test
					public void test${javaMethodSignature.methodName?cap_first}WithSortInteger() throws Exception {
						test${javaMethodSignature.methodName?cap_first}WithSort(
							EntityField.Type.INTEGER,
							(entityField, ${schemaVarName}1, ${schemaVarName}2) -> {
								BeanTestUtil.setProperty(${schemaVarName}1, entityField.getName(), 0);
								BeanTestUtil.setProperty(${schemaVarName}2, entityField.getName(), 1);
							});
					}

					@Test
					public void test${javaMethodSignature.methodName?cap_first}WithSortString() throws Exception {
						test${javaMethodSignature.methodName?cap_first}WithSort(
							EntityField.Type.STRING,
							(entityField, ${schemaVarName}1, ${schemaVarName}2) -> {

								Class<?> clazz = ${schemaVarName}1.getClass();

								String entityFieldName = entityField.getName();

								java.lang.reflect.Method method = clazz.getMethod( "get" + StringUtil.upperCaseFirstLetter(entityFieldName));

								Class<?> returnType = method.getReturnType();

								if (returnType.isAssignableFrom(Map.class)) {
									BeanTestUtil.setProperty(${schemaVarName}1, entityFieldName, Collections.singletonMap("Aaa", "Aaa"));
									BeanTestUtil.setProperty(${schemaVarName}2, entityFieldName, Collections.singletonMap("Bbb", "Bbb"));
								}
								else if (entityFieldName.contains("email")) {
									BeanTestUtil.setProperty(${schemaVarName}1, entityFieldName, "aaa" + StringUtil.toLowerCase(RandomTestUtil.randomString()) + "@liferay.com");
									BeanTestUtil.setProperty(${schemaVarName}2, entityFieldName, "bbb" + StringUtil.toLowerCase(RandomTestUtil.randomString()) + "@liferay.com");
								}
								else {
									BeanTestUtil.setProperty(${schemaVarName}1, entityFieldName, "aaa" + StringUtil.toLowerCase(RandomTestUtil.randomString()));
									BeanTestUtil.setProperty(${schemaVarName}2, entityFieldName, "bbb" + StringUtil.toLowerCase(RandomTestUtil.randomString()));
								}
							});
					}

					protected void test${javaMethodSignature.methodName?cap_first}WithSort(EntityField.Type type, UnsafeTriConsumer<EntityField, ${schemaName}, ${schemaName}, Exception> unsafeTriConsumer) throws Exception {
						List<EntityField> entityFields = getEntityFields(type);

						if (entityFields.isEmpty()) {
							return;
						}

						<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
							${javaMethodParameter.parameterType} ${javaMethodParameter.parameterName} = test${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}();
						</#list>

						${schemaName} ${schemaVarName}1 = random${schemaName}();
						${schemaName} ${schemaVarName}2 = random${schemaName}();

						for (EntityField entityField : entityFields) {
							unsafeTriConsumer.accept(entityField, ${schemaVarName}1, ${schemaVarName}2);
						}

						${schemaVarName}1 = test${javaMethodSignature.methodName?cap_first}_add${schemaName}(
							<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
								${javaMethodParameter.parameterName},
							</#list>

							${schemaVarName}1);

						${schemaVarName}2 = test${javaMethodSignature.methodName?cap_first}_add${schemaName}(
							<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
								${javaMethodParameter.parameterName},
							</#list>

							${schemaVarName}2);

						Page<${schemaName}> page = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
							<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
								<#if stringUtil.equals(javaMethodParameter.parameterName, "pagination")>
									null
								<#elseif stringUtil.equals(javaMethodParameter.parameterName, "sorts")>
									null
								<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
									${javaMethodParameter.parameterName}
								<#else>
									null
								</#if>

								<#sep>, </#sep>
							</#list>
						);

						for (EntityField entityField : entityFields) {
							Page<${schemaName}> ascPage = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
								<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
									<#if stringUtil.equals(javaMethodParameter.parameterName, "pagination")>
										Pagination.of(1, (int) page.getTotalCount() + 1)
									<#elseif stringUtil.equals(javaMethodParameter.parameterName, "sorts")>
										entityField.getName() + ":asc"
									<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
										${javaMethodParameter.parameterName}
									<#else>
										null
									</#if>

									<#sep>, </#sep>
								</#list>
							);

							assertContains(${schemaVarName}1, (List<${schemaName}>)ascPage.getItems());
							assertContains(${schemaVarName}2, (List<${schemaName}>)ascPage.getItems());

							Page<${schemaName}> descPage = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
								<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
									<#if stringUtil.equals(javaMethodParameter.parameterName, "pagination")>
										Pagination.of(1, (int) page.getTotalCount() + 1)
									<#elseif stringUtil.equals(javaMethodParameter.parameterName, "sorts")>
										entityField.getName() + ":desc"
									<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
										${javaMethodParameter.parameterName}
									<#else>
										null
									</#if>

									<#sep>, </#sep>
								</#list>
							);

							assertContains(${schemaVarName}2, (List<${schemaName}>)descPage.getItems());
							assertContains(${schemaVarName}1, (List<${schemaName}>)descPage.getItems());
						}
					}
				</#if>

				protected ${schemaName} test${javaMethodSignature.methodName?cap_first}_add${schemaName}(
					<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
						${javaMethodParameter.parameterType} ${javaMethodParameter.parameterName},
					</#list>

					${schemaName} ${schemaVarName}
				) throws Exception {
					<#if (javaMethodSignature.pathJavaMethodParameters?size == 1)>
						<#assign firstPathJavaMethodParameter = javaMethodSignature.pathJavaMethodParameters[0] />

						<#if freeMarkerTool.hasPostSchemaJavaMethodSignature(javaMethodSignatures, firstPathJavaMethodParameter.parameterName, schemaName)>
							<#assign postSchemaJavaMethodSignature = freeMarkerTool.getPostSchemaJavaMethodSignature(javaMethodSignatures, firstPathJavaMethodParameter.parameterName, schemaName) />

							return ${schemaVarName}Resource.${postSchemaJavaMethodSignature.methodName}(${firstPathJavaMethodParameter.parameterName}, ${schemaVarName}

							<#if freeMarkerTool.hasRequestBodyMediaType(postSchemaJavaMethodSignature, "multipart/form-data")>
								<#assign generateGetMultipartFilesMethod = true />

								, getMultipartFiles()
							</#if>

							);
						<#else>
							throw new UnsupportedOperationException("This method needs to be implemented");
						</#if>
					<#else>
						throw new UnsupportedOperationException("This method needs to be implemented");
					</#if>
				}

				<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
					protected ${javaMethodParameter.parameterType} test${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}() throws Exception {
						<#if generateDepotEntry && stringUtil.equals(javaMethodParameter.parameterName, "assetLibraryExternalReferenceCode")>
							return testDepotEntryGroup.getExternalReferenceCode();
						<#elseif generateDepotEntry && stringUtil.equals(javaMethodParameter.parameterName, "assetLibraryId")>
							return testDepotEntry.getDepotEntryId();
						<#elseif stringUtil.equals(javaMethodParameter.parameterName, "siteExternalReferenceCode")>
							return testGroup.getExternalReferenceCode();
						<#elseif stringUtil.equals(javaMethodParameter.parameterName, "siteId")>
							return testGroup.getGroupId();
						<#else>
							throw new UnsupportedOperationException("This method needs to be implemented");
						</#if>
					}

					protected ${javaMethodParameter.parameterType} test${javaMethodSignature.methodName?cap_first}_getIrrelevant${javaMethodParameter.parameterName?cap_first}() throws Exception {
						<#if generateDepotEntry && stringUtil.equals(javaMethodParameter.parameterName, "assetLibraryExternalReferenceCode")>
							return irrelevantDepotEntryGroup.getExternalReferenceCode();
						<#elseif generateDepotEntry && stringUtil.equals(javaMethodParameter.parameterName, "assetLibraryId")>
							return irrelevantDepotEntry.getDepotEntryId();
						<#elseif stringUtil.equals(javaMethodParameter.parameterName, "siteExternalReferenceCode")>
							return irrelevantGroup.getExternalReferenceCode();
						<#elseif stringUtil.equals(javaMethodParameter.parameterName, "siteId")>
							return irrelevantGroup.getGroupId();
						<#else>
							return null;
						</#if>
					}
				</#list>
			</#if>
		<#elseif freeMarkerTool.hasHTTPMethod(javaMethodSignature, "get") && javaMethodSignature.returnType?ends_with(schemaName)>
			<#assign getterJavaMethodParametersMap = {} />

			@Test
			public void test${javaMethodSignature.methodName?cap_first}() throws Exception {
				<#if properties?keys?seq_contains("externalReferenceCode") || properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id")>
					${schemaName} post${schemaName} = test${javaMethodSignature.methodName?cap_first}_add${schemaName}();

					${schemaName} get${schemaName} = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
						<@getGetParameters
							allowQueryParameter = false
							javaMethodSignature = javaMethodSignature
							testJavaMethodName = javaMethodSignature.methodName
							varName = "post" + schemaName
						/>);

					assertEquals(post${schemaName}, get${schemaName});
					assertValid(get${schemaName});

					<#if generatePermissionsJavaMethodSignatures?seq_contains(javaMethodSignature)>
						Assert.assertNull(get${schemaName}.getPermissions());

						get${schemaName} = permissions${schemaName}Resource.${javaMethodSignature.methodName}(
							<@getGetParameters
								allowQueryParameter = false
								javaMethodSignature = javaMethodSignature
								testJavaMethodName = javaMethodSignature.methodName
								varName = "post" + schemaName
							/>);

						Assert.assertNotNull(get${schemaName}.getPermissions());
					</#if>
				<#else>
					Assert.assertTrue(false);
				</#if>
			}

			<#if generateCRUD && stringUtil.equals(javaMethodSignature.methodName, "get" + schemaName) && (properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id"))>
				@Test
				public void testVulcanCRUDItemDelegateGetItem() throws Exception {
					${schemaName} post${schemaName} = test${javaMethodSignature.methodName?cap_first}_add${schemaName}();

					${schemaName} get${schemaName} = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
						<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
							<#if freeMarkerTool.isIdParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
								post${schemaName}.${getIdMethodName}()
							<#else>
								null
							</#if>

							<#sep>, </#sep>
						</#list>
					);

					VulcanCRUDItemDelegate vulcanCRUDItemDelegate = _vulcanCRUDItemDelegateBuilderRegistry.builder(
						testCompany,
						"${schemaJavaType}"
					).acceptLanguage(
						new AcceptLanguage() {

							@Override
							public List<Locale> getLocales() {
								return Arrays.asList(LocaleUtil.getDefault());
							}

							@Override
							public String getPreferredLanguageId() {
								return LocaleUtil.toLanguageId(
									LocaleUtil.getDefault());
							}

							@Override
							public Locale getPreferredLocale() {
								return LocaleUtil.getDefault();
							}

						}
					).groupLocalService(
						_groupLocalService
					).httpServletRequest(
						testVulcanCRUDItemDelegate_getHttpServletRequest()
					).httpServletResponse(
						new MockHttpServletResponse()
					).resourceActionLocalService(
						_resourceActionLocalService
					).resourcePermissionLocalService(
						_resourcePermissionLocalService
					).roleLocalService(
						_roleLocalService
					).scopeChecker(
						_scopeChecker
					).uriInfo(
						testVulcanCRUDItemDelegate_getUriInfo()
					).user(
						testVulcanCRUDItemDelegate_getUser()
					).build();

					Object item = vulcanCRUDItemDelegate.getItem(post${schemaName}.${getIdMethodName}());

					assertEquals(get${schemaName}, ${schemaName}SerDes.toDTO(item.toString()));
				}

				protected HttpServletRequest testVulcanCRUDItemDelegate_getHttpServletRequest() {
					return new MockHttpServletRequest() {
						@Override
						public StringBuffer getRequestURL() {
							return new StringBuffer(StringBundler.concat("http://localhost:8080/o/v1.0/", RandomTestUtil.randomString(), "/", RandomTestUtil.randomString()));
						}
					};
				}

				protected UriInfo testVulcanCRUDItemDelegate_getUriInfo() {
					String applicationPath = RandomTestUtil.randomString() + "/";
					String resourcePath = RandomTestUtil.randomString();

					return new UriInfo() {

						@Override
						public String getPath() {
							return resourcePath;
						}

						@Override
						public String getPath(boolean decode) {
							return getPath();
						}

						@Override
						public List<PathSegment> getPathSegments() {
							return Collections.emptyList();
						}

						@Override
						public List<PathSegment> getPathSegments(boolean decode) {
							return getPathSegments();
						}

						@Override
						public URI getRequestUri() {
							return URI.create("http://localhost:8080/o/" + applicationPath + resourcePath);
						}

						@Override
						public UriBuilder getRequestUriBuilder() {
							return UriBuilder.fromUri(getRequestUri());
						}

						@Override
						public URI getAbsolutePath() {
							return getRequestUri();
						}

						@Override
						public UriBuilder getAbsolutePathBuilder() {
							return getRequestUriBuilder();
						}

						@Override
						public URI getBaseUri() {
							return URI.create("http://localhost:8080/o/" + applicationPath);
						}

						@Override
						public UriBuilder getBaseUriBuilder() {
							return UriBuilder.fromUri(getBaseUri());
						}

						@Override
						public MultivaluedMap<String, String> getPathParameters() {
							return new MultivaluedHashMap<>();
						}

						@Override
						public MultivaluedMap<String, String> getPathParameters(boolean decode) {
							return getPathParameters();
						}

						@Override
						public MultivaluedMap<String, String> getQueryParameters() {
							return new MultivaluedHashMap<>();
						}

						@Override
						public MultivaluedMap<String, String> getQueryParameters(boolean decode) {
							return getQueryParameters();
						}

						@Override
						public List<String> getMatchedURIs() {
							return Collections.emptyList();
						}

						@Override
						public List<String> getMatchedURIs(boolean decode) {
							return getMatchedURIs();
						}

						@Override
						public List<Object> getMatchedResources() {
							return Collections.emptyList();
						}

						@Override
						public URI resolve(URI requestUri) {
							return getBaseUri().resolve(requestUri);
						}

						@Override
						public URI relativize(URI uri) {
							return getBaseUri().relativize(uri);
						}

					};
				}

				protected com.liferay.portal.kernel.model.User testVulcanCRUDItemDelegate_getUser(){
					return _testCompanyAdminUser;
				}
			</#if>

			<@getTestAdderMethod javaMethodSignature = javaMethodSignature />

			<@getTestGetterMethods
				getterJavaMethodParametersMap = getterJavaMethodParametersMap
				testJavaMethodName = javaMethodSignature.methodName
			/>
		<#elseif freeMarkerTool.hasHTTPMethod(javaMethodSignature, "patch") && freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, "get" + javaMethodSignature.methodName?remove_beginning("patch")) && javaMethodSignature.returnType?ends_with(schemaName)>
			@Test
			public void test${javaMethodSignature.methodName?cap_first}() throws Exception {
				<#if !(properties?keys?seq_contains("externalReferenceCode") || properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id"))>
					Assert.assertTrue(false);
				<#else>
					${schemaName} post${schemaName} = test${javaMethodSignature.methodName?cap_first}_add${schemaName}();

					${schemaName} randomPatch${schemaName} = randomPatch${schemaName}();

					<#if freeMarkerTool.hasRequestBodyMediaType(javaMethodSignature, "multipart/form-data")>
						<#assign generateGetMultipartFilesMethod = true />

						Map<String, File> multipartFiles = getMultipartFiles();
					</#if>

					@SuppressWarnings("PMD.UnusedLocalVariable")
					${schemaName} patch${schemaName} = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
						<#assign parameterNames = [] />

						<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
							<#if freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
								<#if freeMarkerTool.isExternalReferenceCodeParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
									<#assign parameterNames = parameterNames + ["post${schemaName}.getExternalReferenceCode()"] />
								<#elseif freeMarkerTool.isIdParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
									<#assign parameterNames = parameterNames + ["post${schemaName}.${getIdMethodName}()"] />
								<#elseif properties?keys?seq_contains(javaMethodParameter.parameterName)>
									<#assign parameterNames = parameterNames + ["post${schemaName}.get${javaMethodParameter.parameterName?cap_first}()"] />
								<#else>
									<#assign parameterNames = parameterNames + ["null"] />
								</#if>
							<#elseif freeMarkerTool.isQueryParameter(javaMethodParameter, javaMethodSignature.operation)>
								<#assign parameterNames = parameterNames + ["test${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}()"] />
							</#if>
						</#list>

						${parameterNames?join(", ")} , randomPatch${schemaName}

						<#if freeMarkerTool.hasRequestBodyMediaType(javaMethodSignature, "multipart/form-data")>
							, multipartFiles
						</#if>
					);

					${schemaName} expectedPatch${schemaName} = post${schemaName}.clone();

					BeanTestUtil.copyProperties(randomPatch${schemaName}, expectedPatch${schemaName});

					${schemaName} get${schemaName} = ${schemaVarName}Resource.get${javaMethodSignature.methodName?remove_beginning("patch")}(
						<#assign parameterNames = [] />

						<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
							<#if freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
								<#if freeMarkerTool.isExternalReferenceCodeParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
									<#assign parameterNames = parameterNames + ["patch${schemaName}.getExternalReferenceCode()"] />
								<#elseif freeMarkerTool.isIdParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
									<#assign parameterNames = parameterNames + ["patch${schemaName}.${getIdMethodName}()"] />
								<#elseif properties?keys?seq_contains(javaMethodParameter.parameterName)>
									<#assign parameterNames = parameterNames + ["patch${schemaName}.get${javaMethodParameter.parameterName?cap_first}()"] />
								<#else>
									<#assign parameterNames = parameterNames + ["null"] />
								</#if>
							</#if>
						</#list>

						${parameterNames?join(", ")}
					);

					assertEquals(expectedPatch${schemaName}, get${schemaName});
					assertValid(get${schemaName});

					<#if freeMarkerTool.hasRequestBodyMediaType(javaMethodSignature, "multipart/form-data")>
						assertValid(get${schemaName}, multipartFiles);
					</#if>
				</#if>
			}

			<@getTestAdderMethod javaMethodSignature = javaMethodSignature />

			<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
				<#if freeMarkerTool.isQueryParameter(javaMethodParameter, javaMethodSignature.operation)>
					protected ${javaMethodParameter.parameterType} test${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}() throws Exception {
						throw new UnsupportedOperationException("This method needs to be implemented");
					}
				</#if>
			</#list>
		<#elseif freeMarkerTool.hasHTTPMethod(javaMethodSignature, "post") && javaMethodSignature.returnType?ends_with(schemaName)>
			@Test
			public void test${javaMethodSignature.methodName?cap_first}() throws Exception {
				${schemaName} random${schemaName} = random${schemaName}();

				<#if freeMarkerTool.hasRequestBodyMediaType(javaMethodSignature, "multipart/form-data")>
					<#assign generateGetMultipartFilesMethod = true />

					Map<String, File> multipartFiles = getMultipartFiles();
				</#if>

				${schemaName} post${schemaName} = test${javaMethodSignature.methodName?cap_first}_add${schemaName}(random${schemaName}

					<#if freeMarkerTool.hasRequestBodyMediaType(javaMethodSignature, "multipart/form-data")>
						, multipartFiles
					</#if>
				);

				assertEquals(random${schemaName}, post${schemaName});
				assertValid(post${schemaName});

				<#if freeMarkerTool.hasRequestBodyMediaType(javaMethodSignature, "multipart/form-data")>
					assertValid(post${schemaName}, multipartFiles);
				</#if>

				<#if generatePermissionsJavaMethodSignatures?seq_contains(javaMethodSignature)>
					${schemaName} randomPermissions${schemaName}1 = randomPermissions${schemaName}();

					${schemaName} postPermissions${schemaName}1 = test${javaMethodSignature.methodName?cap_first}_add${schemaName}(randomPermissions${schemaName}1);

					Assert.assertNull(postPermissions${schemaName}1.getPermissions());

					${schemaName} randomPermissions${schemaName}2 = randomPermissions${schemaName}();

					${schemaName} postPermissions${schemaName}2 = test${javaMethodSignature.methodName?cap_first}_addPermissions${schemaName}(randomPermissions${schemaName}2);

					Assert.assertNotNull(postPermissions${schemaName}2.getPermissions());
				</#if>

				<#if schema.discriminator?has_content>
					<#assign discriminatorPropertyName = schema.discriminator.propertyName />

					<#list schema.discriminator.mapping as mappingName, mappingSchema>
						<#assign
							childSchemaName = freeMarkerTool.getReferenceName(mappingSchema)

							childSchemaVarName = freeMarkerTool.getSchemaVarName(childSchemaName)

							allChildProperties = properties + freeMarkerTool.getDTOProperties(configYAML, openAPIYAML, allSchemas[childSchemaName], allSchemas)
						/>

						${childSchemaName} ${childSchemaVarName} = new ${childSchemaName}() {
							{
								<#list allChildProperties?keys as propertyName>
									<#if generateDepotEntry && stringUtil.equals(propertyName, "assetLibraryExternalReferenceCode")>
										${propertyName} = testDepotEntryGroup.getExternalReferenceCode();
									<#elseif generateDepotEntry && stringUtil.equals(propertyName, "assetLibraryId")>
										${propertyName} = testDepotEntry.getDepotEntryId();
									<#elseif stringUtil.equals(propertyName, "siteExternalReferenceCode")>
										${propertyName} = testGroup.getExternalReferenceCode();
									<#elseif stringUtil.equals(propertyName, "siteId")>
										${propertyName} = testGroup.getGroupId();
									<#elseif stringUtil.equals(allChildProperties[propertyName], "Integer")>
										${propertyName} = RandomTestUtil.randomInt();
									<#elseif propertyName?contains("email") && stringUtil.equals(allChildProperties[propertyName], "String")>
										${propertyName} = StringUtil.toLowerCase(RandomTestUtil.randomString()) + "@liferay.com";
									<#elseif stringUtil.equals(allChildProperties[propertyName], "String")>
										${propertyName} = StringUtil.toLowerCase(RandomTestUtil.randomString());
									<#elseif randomDataTypes?seq_contains(allChildProperties[propertyName])>
										${propertyName} = RandomTestUtil.random${allChildProperties[propertyName]}();
									<#elseif stringUtil.equals(allChildProperties[propertyName], "Date")>
										${propertyName} = RandomTestUtil.nextDate();
									</#if>
								</#list>

								${discriminatorPropertyName} = ${discriminatorPropertyName?cap_first}.create("${mappingName}");
							}
						};

						assertEquals(${childSchemaVarName}, test${javaMethodSignature.methodName?cap_first}_add${schemaName}(${childSchemaVarName}));
					</#list>
				</#if>
			}

			protected ${schemaName} test${javaMethodSignature.methodName?cap_first}_add${schemaName}(${schemaName} ${schemaVarName}

			<#if freeMarkerTool.hasRequestBodyMediaType(javaMethodSignature, "multipart/form-data")>
				, Map<String, File> multipartFiles
			</#if>

			) throws Exception {
				<#if (javaMethodSignature.pathJavaMethodParameters?size == 1)>
					<#assign
						firstPathJavaMethodParameter = javaMethodSignature.pathJavaMethodParameters[0]
						modifiedPathJavaMethodParameterName = firstPathJavaMethodParameter.parameterName?remove_beginning("parent")?remove_ending("Id")?cap_first
					/>

					<#if freeMarkerTool.hasPostSchemaJavaMethodSignature(javaMethodSignatures, firstPathJavaMethodParameter.parameterName, schemaName) && stringUtil.equals(javaMethodSignature.methodName, "post" + modifiedPathJavaMethodParameterName + schemaName)>
						<#if freeMarkerTool.isCollection(javaMethodSignature, javaMethodSignatures, modifiedPathJavaMethodParameterName + schemaNames)>
							return ${schemaVarName}Resource.post${modifiedPathJavaMethodParameterName}${schemaName}(testGet${modifiedPathJavaMethodParameterName}${schemaNames}Page_get<#if stringUtil.startsWith(firstPathJavaMethodParameter.parameterName, "parent")>Parent</#if>${modifiedPathJavaMethodParameterName}Id(), ${schemaVarName}
						<#else>
							return ${schemaVarName}Resource.post${modifiedPathJavaMethodParameterName}${schemaName}(testGet${modifiedPathJavaMethodParameterName}${schemaName}_get${modifiedPathJavaMethodParameterName}Id(${schemaVarName})
						</#if>

						<#if freeMarkerTool.hasRequestBodyMediaType(javaMethodSignature, "multipart/form-data")>
							, multipartFiles
						</#if>
						);
					<#else>
						throw new UnsupportedOperationException("This method needs to be implemented");
					</#if>
				<#else>
					throw new UnsupportedOperationException("This method needs to be implemented");
				</#if>
			}

			<#if generatePermissionsJavaMethodSignatures?seq_contains(javaMethodSignature)>
				protected ${schemaName} test${javaMethodSignature.methodName?cap_first}_addPermissions${schemaName}(${schemaName} ${schemaVarName}) throws Exception {
					<#if (javaMethodSignature.pathJavaMethodParameters?size == 1)>
						<#assign
							firstPathJavaMethodParameter = javaMethodSignature.pathJavaMethodParameters[0]
							modifiedPathJavaMethodParameterName = firstPathJavaMethodParameter.parameterName?remove_beginning("parent")?remove_ending("Id")?cap_first
						/>

						<#if freeMarkerTool.hasPostSchemaJavaMethodSignature(javaMethodSignatures, firstPathJavaMethodParameter.parameterName, schemaName) && stringUtil.equals(javaMethodSignature.methodName, "post" + modifiedPathJavaMethodParameterName + schemaName)>
							<#if freeMarkerTool.isCollection(javaMethodSignature, javaMethodSignatures, modifiedPathJavaMethodParameterName + schemaNames)>
								return permissions${schemaName}Resource.post${modifiedPathJavaMethodParameterName}${schemaName}(testGet${modifiedPathJavaMethodParameterName}${schemaNames}Page_get<#if stringUtil.startsWith(firstPathJavaMethodParameter.parameterName, "parent")>Parent</#if>${modifiedPathJavaMethodParameterName}Id(), ${schemaVarName}
							<#else>
								return permissions${schemaName}Resource.post${modifiedPathJavaMethodParameterName}${schemaName}(testGet${modifiedPathJavaMethodParameterName}${schemaName}_get${modifiedPathJavaMethodParameterName}Id(${schemaVarName})
							</#if>
							);
						<#else>
							throw new UnsupportedOperationException("This method needs to be implemented");
						</#if>
					<#else>
						throw new UnsupportedOperationException("This method needs to be implemented");
					</#if>
				}
			</#if>
		<#elseif freeMarkerTool.hasHTTPMethod(javaMethodSignature, "put") && javaMethodSignature.methodName?contains("Permission")>
			@Test
			public void test${javaMethodSignature.methodName?cap_first}() throws Exception {
				@SuppressWarnings("PMD.UnusedLocalVariable")
				${schemaName} ${schemaVarName} = test${javaMethodSignature.methodName?cap_first}_add${schemaName}();

				@SuppressWarnings("PMD.UnusedLocalVariable")
				com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

				assertHttpResponseStatusCode(
					200,
					${schemaVarName}Resource.${javaMethodSignature.methodName}HttpResponse(
						<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
							<@getPermissionParameter
								javaMethodParameter = javaMethodParameter
								javaMethodSignature = javaMethodSignature
								roleName = "role.getName()"
								schemaVarNameId = "${schemaVarName}.${getIdMethodName}()"
							>
								<#if javaMethodSignature.methodName?contains("AssetLibrary") || javaMethodSignature.methodName?contains("Site")>
									"PERMISSIONS"
								<#else>
									"VIEW"
								</#if>
							</@getPermissionParameter>

							<#sep>, </#sep>
						</#list>
					));

				assertHttpResponseStatusCode(
					404,
					${schemaVarName}Resource.${javaMethodSignature.methodName}HttpResponse(
						<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
							<#assign schemaVarNameId>
								<@getDefaultParameter javaMethodParameter = javaMethodParameter />
							</#assign>

							<@getPermissionParameter
								javaMethodParameter = javaMethodParameter
								javaMethodSignature = javaMethodSignature
								roleName = "\"-\""
								schemaVarNameId = schemaVarNameId
							>
								"-"
							</@getPermissionParameter>

							<#sep>, </#sep>
						</#list>
					));
			}

			<@getTestAdderMethod javaMethodSignature = javaMethodSignature />
		<#elseif freeMarkerTool.hasHTTPMethod(javaMethodSignature, "put") && javaMethodSignature.returnType?ends_with(schemaName)>
			<#assign
				addResourceGetterMethod = false
				getterJavaMethodParametersMap = {}
			/>

			@Test
			public void test${javaMethodSignature.methodName?cap_first}() throws Exception {
				<#if !(properties?keys?seq_contains("externalReferenceCode") || properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id"))>
					Assert.assertTrue(false);
				<#else>
					${schemaName} post${schemaName} = test${javaMethodSignature.methodName?cap_first}_add${schemaName}();

					${schemaName} random${schemaName} = random${schemaName}();

					<#if freeMarkerTool.hasRequestBodyMediaType(javaMethodSignature, "multipart/form-data")>
						<#assign generateGetMultipartFilesMethod = true />

						Map<String, File> multipartFiles = getMultipartFiles();
					</#if>

					${schemaName} put${schemaName} = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
						<@getPutParameters
							hasMultipartFiles = true
							javaMethodSignature = javaMethodSignature
							newVarName = "random" + schemaName
							varName = "post" + schemaName
						/>
					);

					assertEquals(random${schemaName}, put${schemaName});
					assertValid(put${schemaName});

					<#if generatePermissionsJavaMethodSignatures?seq_contains(javaMethodSignature)>
						Assert.assertNull(put${schemaName}.getPermissions());
					</#if>

					${schemaName} get${schemaName} =
						<#assign getJavaMethodSignatureMethodName = javaMethodSignature.methodName?replace("put", "get", "f") />

						<#if freeMarkerTool.containsJavaMethodSignature(javaMethodSignatures, getJavaMethodSignatureMethodName)>
							${schemaVarName}Resource.${getJavaMethodSignatureMethodName}(
								<@getGetParameters
									allowQueryParameter = false
									javaMethodSignature = freeMarkerTool.getJavaMethodSignature(javaMethodSignatures, getJavaMethodSignatureMethodName)
									testJavaMethodName = javaMethodSignature.methodName
									varName = "put" + schemaName
								/>);
						<#else>
							<#assign addResourceGetterMethod = true />

							test${javaMethodSignature.methodName?cap_first}_get${schemaName}(
								<@getGetParameters
									javaMethodSignature = javaMethodSignature
									skipQueryParameter = true
									testJavaMethodName = javaMethodSignature.methodName
									varName = "put" + schemaName
								/>);
						</#if>

					assertEquals(random${schemaName}, get${schemaName});
					assertValid(get${schemaName});

					<#if freeMarkerTool.hasRequestBodyMediaType(javaMethodSignature, "multipart/form-data")>
						assertValid(get${schemaName}, multipartFiles);
					</#if>

					<#if generatePermissionsJavaMethodSignatures?seq_contains(javaMethodSignature)>
						${schemaName} randomPermissions${schemaName} = randomPermissions${schemaName}();

						put${schemaName} = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
							<@getPutParameters
								hasMultipartFiles = true
								javaMethodSignature = javaMethodSignature
								newVarName = "randomPermissions" + schemaName
								varName = "post" + schemaName
							/>
						);

						assertEquals(randomPermissions${schemaName}, put${schemaName});
						assertValid(put${schemaName});

						Assert.assertNull(put${schemaName}.getPermissions());

						put${schemaName} = permissions${schemaName}Resource.${javaMethodSignature.methodName}(
							<@getPutParameters
								hasMultipartFiles = true
								javaMethodSignature = javaMethodSignature
								newVarName = "randomPermissions" + schemaName
								varName = "post" + schemaName
							/>
						);

						Assert.assertNotNull(put${schemaName}.getPermissions());
					</#if>

					<#if javaMethodSignature.methodName?ends_with("ByExternalReferenceCode")>
						${schemaName} new${schemaName} = test${javaMethodSignature.methodName?cap_first}_create${schemaName}();

						put${schemaName} = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
							<@getPutParameters
								hasMultipartFiles = false
								javaMethodSignature = javaMethodSignature
								newVarName = "new" + schemaName
								varName = "new" + schemaName
							/>
						);

						assertEquals(new${schemaName}, put${schemaName});
						assertValid(put${schemaName});

						get${schemaName} =

						<#assign getJavaMethodSignatureMethodName = javaMethodSignature.methodName?replace("put", "get", "f") />

						<#if freeMarkerTool.containsJavaMethodSignature(javaMethodSignatures, getJavaMethodSignatureMethodName)>
							${schemaVarName}Resource.${getJavaMethodSignatureMethodName}(
								<@getGetParameters
									allowQueryParameter = false
									javaMethodSignature = freeMarkerTool.getJavaMethodSignature(javaMethodSignatures, getJavaMethodSignatureMethodName)
									testJavaMethodName = javaMethodSignature.methodName
									varName = "put" + schemaName
								/>);
						<#else>
							<#assign addResourceGetterMethod = true />

							test${javaMethodSignature.methodName?cap_first}_get${schemaName}(
								<@getGetParameters
									javaMethodSignature = javaMethodSignature
									skipQueryParameter = true
									testJavaMethodName = javaMethodSignature.methodName
									varName = "put" + schemaName
								/>);
						</#if>

						assertEquals(new${schemaName}, get${schemaName});

						Assert.assertEquals(new${schemaName}.getExternalReferenceCode(), put${schemaName}.getExternalReferenceCode());
					</#if>
				</#if>
			}

			<#if addResourceGetterMethod>
				protected ${schemaName} test${javaMethodSignature.methodName?cap_first}_get${schemaName}(
					<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
						${javaMethodParameter.parameterType} ${javaMethodParameter.parameterName}

						<#sep>, </#sep>
					</#list>
				) {
					throw new UnsupportedOperationException("This method needs to be implemented");
				}
			</#if>

			<@getTestAdderMethod javaMethodSignature = javaMethodSignature />

			<@getTestGetterMethods
				getterJavaMethodParametersMap = getterJavaMethodParametersMap
				testJavaMethodName = javaMethodSignature.methodName
			/>

			<#if javaMethodSignature.methodName?cap_first?ends_with("ByExternalReferenceCode")>
				protected ${schemaName} test${javaMethodSignature.methodName?cap_first}_create${schemaName}() throws Exception {
					return random${schemaName}();
				}
			</#if>
		<#elseif (properties?keys?seq_contains("externalReferenceCode") || properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id")) && stringUtil.equals(javaMethodSignature.returnType, "void")>
			<#assign getterJavaMethodParametersMap = {} />

			@Test
			public void test${javaMethodSignature.methodName?cap_first}() throws Exception {
				@SuppressWarnings("PMD.UnusedLocalVariable")
				${schemaName} ${schemaVarName} = test${javaMethodSignature.methodName?cap_first}_add${schemaName}();

				<#if javaMethodSignature.methodName?contains("Permission")>
					@SuppressWarnings("PMD.UnusedLocalVariable")
					com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
				</#if>

				assertHttpResponseStatusCode(
					204,
					${schemaVarName}Resource.${javaMethodSignature.methodName}HttpResponse(
						<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
							<#if freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
								<#if freeMarkerTool.isExternalReferenceCodeParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
									${schemaVarName}.getExternalReferenceCode()
								<#elseif freeMarkerTool.isIdParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
									${schemaVarName}.${getIdMethodName}()
								<#elseif properties?keys?seq_contains(javaMethodParameter.parameterName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
									${schemaVarName}.get${javaMethodParameter.parameterName?cap_first}()
								<#else>
									<#assign getterJavaMethodParametersMap = getterJavaMethodParametersMap + {javaMethodParameter.parameterName: javaMethodParameter} />

									<#if properties?keys?seq_contains(javaMethodParameter.parameterName)>
										test${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}(${schemaVarName})
									<#else>
										test${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}()
									</#if>
								</#if>
							<#elseif stringUtil.equals(javaMethodParameter.parameterName, schemaVarName)>
								${schemaVarName}
							<#else>
								null
							</#if>

							<#sep>, </#sep>
						</#list>
					));

				assertHttpResponseStatusCode(
					404,
					${schemaVarName}Resource.${javaMethodSignature.methodName}HttpResponse(
						<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
							<#if freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
								<#if (freeMarkerTool.isExternalReferenceCodeParameter(javaMethodParameter, schemaName) || freeMarkerTool.isIdParameter(javaMethodParameter, schemaName)) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
									<@getDefaultParameter javaMethodParameter = javaMethodParameter />
								<#elseif properties?keys?seq_contains(javaMethodParameter.parameterName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
									${schemaVarName}.get${javaMethodParameter.parameterName?cap_first}()
								<#else>
									<#assign getterJavaMethodParametersMap = getterJavaMethodParametersMap + {javaMethodParameter.parameterName: javaMethodParameter} />

									<#if properties?keys?seq_contains(javaMethodParameter.parameterName)>
										test${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}(${schemaVarName})
									<#else>
										test${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}()
									</#if>
								</#if>
							<#elseif stringUtil.equals(javaMethodParameter.parameterName, schemaVarName)>
								${schemaVarName}
							<#else>
								null
							</#if>

							<#sep>, </#sep>
						</#list>
					));
			}

			<@getTestGetterMethods
				getterJavaMethodParametersMap = getterJavaMethodParametersMap
				testJavaMethodName = javaMethodSignature.methodName
			/>

			<@getTestAdderMethod javaMethodSignature = javaMethodSignature />
		<#elseif !freeMarkerTool.isReturnTypeRelatedSchema(javaMethodSignature, relatedSchemaNames)>
			@Test
			public void test${javaMethodSignature.methodName?cap_first}() throws Exception {
				Assert.assertTrue(false);
			}
		</#if>

		<#assign
			generateTestGraphQLAddMethod = false
			graphQLNamespace = freeMarkerTool.getGraphQLNamespace(configYAML, openAPIYAML)
		/>

		<#if configYAML.generateGraphQL && freeMarkerTool.hasHTTPMethod(javaMethodSignature, "delete") && stringUtil.equals(freeMarkerTool.getGraphQLPropertyName(javaMethodSignature, javaMethodSignatures), "delete" + schemaName)>
			@Test
			public void testGraphQL${javaMethodSignature.methodName?cap_first}() throws Exception {
				<#if javaMethodSignature.pathJavaMethodParameters?size == 0 || !(properties?keys?seq_contains("externalReferenceCode") || properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id"))>
					Assert.assertTrue(false);
				<#else>
					<#assign generateTestGraphQLAddMethod = true />

					// No namespace

					${schemaName} ${schemaVarName}1 = testGraphQL${javaMethodSignature.methodName?cap_first}_add${schemaName}();

					Assert.assertTrue(
						JSONUtil.getValueAsBoolean(
							invokeGraphQLMutation(
								new GraphQLField(
									"delete${schemaName}",
									new HashMap<String, Object>() {
										{
											put(
												<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
													<#if freeMarkerTool.isExternalReferenceCodeParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
														"${javaMethodParameter.parameterName}",
														<#if stringUtil.equals(properties["externalReferenceCode"], "String")>
															<@getQuotedString unquotedString = "${schemaVarName}1.getExternalReferenceCode()" />
														<#else>
															${schemaVarName}1.getExternalReferenceCode()
														</#if>
													<#elseif freeMarkerTool.isIdParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
														"${javaMethodParameter.parameterName}",
														<#if stringUtil.equals(properties[idParameterName], "String")>
															<@getQuotedString unquotedString = "${schemaVarName}1.${getIdMethodName}()" />
														<#else>
															${schemaVarName}1.${getIdMethodName}()
														</#if>
													</#if>
												</#list>
											);
										}
									})),
							"JSONObject/data",
							"Object/delete${schemaName}"));

					<#if freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, "get" + javaMethodSignature.methodName?remove_beginning("delete"))>
						JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
							invokeGraphQLQuery(
								new GraphQLField(
									"${schemaName?uncap_first}",
									new HashMap<String, Object>() {
										{
											put(
												<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
													<#if freeMarkerTool.isExternalReferenceCodeParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
														"${javaMethodParameter.parameterName}",
														<#if stringUtil.equals(properties["externalReferenceCode"], "String")>
															<@getQuotedString unquotedString = "${schemaVarName}1.getExternalReferenceCode()" />
														<#else>
															${schemaVarName}1.getExternalReferenceCode()
														</#if>
													<#elseif freeMarkerTool.isIdParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
														"${javaMethodParameter.parameterName}",
														<#if stringUtil.equals(properties[idParameterName], "String")>
															<@getQuotedString unquotedString = "${schemaVarName}1.${getIdMethodName}()" />
														<#else>
															${schemaVarName}1.${getIdMethodName}()
														</#if>
													</#if>
												</#list>
											);
										}
									},
									new GraphQLField("${idParameterName}"))),
							"JSONArray/errors");

						Assert.assertTrue(errorsJSONArray1.length() > 0);
					</#if>

					<#if freeMarkerTool.isVersionCompatible(configYAML, 5)>

						// Using the namespace ${graphQLNamespace}

						${schemaName} ${schemaVarName}2 = testGraphQL${javaMethodSignature.methodName?cap_first}_add${schemaName}();

						Assert.assertTrue(
							JSONUtil.getValueAsBoolean(
								invokeGraphQLMutation(
									new GraphQLField(
										"${graphQLNamespace}",
										new GraphQLField(
											"delete${schemaName}",
											new HashMap<String, Object>() {
												{
													put(
														<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
															<#if freeMarkerTool.isExternalReferenceCodeParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
																"${javaMethodParameter.parameterName}",
																<#if stringUtil.equals(properties["externalReferenceCode"], "String")>
																	<@getQuotedString unquotedString = "${schemaVarName}2.getExternalReferenceCode()" />
																<#else>
																	${schemaVarName}2.getExternalReferenceCode()
																</#if>
															<#elseif freeMarkerTool.isIdParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
																"${javaMethodParameter.parameterName}",
																<#if stringUtil.equals(properties[idParameterName], "String")>
																	<@getQuotedString unquotedString = "${schemaVarName}2.${getIdMethodName}()" />
																<#else>
																	${schemaVarName}2.${getIdMethodName}()
																</#if>
															</#if>
														</#list>
													);
												}
											}))),
								"JSONObject/data",
								"JSONObject/${graphQLNamespace}",
								"Object/delete${schemaName}"));

						<#if freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, "get" + javaMethodSignature.methodName?remove_beginning("delete"))>
							JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
								invokeGraphQLQuery(
									new GraphQLField(
										"${graphQLNamespace}",
										new GraphQLField(
											"${schemaName?uncap_first}",
											new HashMap<String, Object>() {
												{
													put(
														<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
															<#if freeMarkerTool.isExternalReferenceCodeParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
																"${javaMethodParameter.parameterName}",
																<#if stringUtil.equals(properties["externalReferenceCode"], "String")>
																	<@getQuotedString unquotedString = "${schemaVarName}2.getExternalReferenceCode()" />
																<#else>
																	${schemaVarName}2.getExternalReferenceCode()
																</#if>
															<#elseif freeMarkerTool.isIdParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
																"${javaMethodParameter.parameterName}",
																<#if stringUtil.equals(properties[idParameterName], "String")>
																	<@getQuotedString unquotedString = "${schemaVarName}2.${getIdMethodName}()" />
																<#else>
																	${schemaVarName}2.${getIdMethodName}()
																</#if>
															</#if>
														</#list>
													);
												}
											},
											new GraphQLField("${idParameterName}")))),
								"JSONArray/errors");

							Assert.assertTrue(errorsJSONArray2.length() > 0);
						</#if>
					</#if>
				</#if>
			}
		<#elseif configYAML.generateGraphQL && freeMarkerTool.hasHTTPMethod(javaMethodSignature, "get") && javaMethodSignature.returnType?contains("Page<") && stringUtil.equals(freeMarkerTool.getGraphQLPropertyName(javaMethodSignature, javaMethodSignatures), schemaVarNames)>
			@Test
			public void testGraphQL${javaMethodSignature.methodName?cap_first}() throws Exception {
				<#if !(properties?keys?seq_contains("externalReferenceCode") || properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id"))>
					Assert.assertTrue(false);
				<#else>
					<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
						${javaMethodParameter.parameterType} ${javaMethodParameter.parameterName} = test${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}();
					</#list>

					GraphQLField graphQLField = new GraphQLField(
						"${schemaVarNames}",
						new HashMap<String, Object>() {
							{
								<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
									<#if stringUtil.equals(javaMethodParameter.parameterName, "pagination")>
										put("page", 1);
										put("pageSize", 10);
									</#if>
								</#list>

								<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
									<#if stringUtil.equals(javaMethodParameter.parameterName, "siteId")>
										put("siteKey", <@getQuotedString unquotedString="${javaMethodParameter.parameterName}" />);
									<#else>
										put("${javaMethodParameter.parameterName}",
											<#if stringUtil.equals(javaMethodParameter.parameterType, "java.lang.String") || stringUtil.equals(javaMethodParameter.parameterName, "assetLibraryId")>
												<@getQuotedString unquotedString = "${javaMethodParameter.parameterName}" />
											<#else>
												${javaMethodParameter.parameterName}
											</#if>
										);
									</#if>
								</#list>
							}
						},
						new GraphQLField("items", getGraphQLFields()),
						new GraphQLField("page"),
						new GraphQLField("totalCount"));

					// No namespace

					JSONObject ${schemaVarNames}JSONObject = JSONUtil.getValueAsJSONObject(
						invokeGraphQLQuery(graphQLField),
						"JSONObject/data",
						"JSONObject/${schemaVarNames}");

					long totalCount = ${schemaVarNames}JSONObject.getLong("totalCount");

					<#assign generateTestGraphQLAddMethod = true />

					${schemaName} ${schemaVarName}1 = testGraphQL${javaMethodSignature.methodName?cap_first}_add${schemaName}();
					${schemaName} ${schemaVarName}2 = testGraphQL${javaMethodSignature.methodName?cap_first}_add${schemaName}();

					${schemaVarNames}JSONObject = JSONUtil.getValueAsJSONObject(
						invokeGraphQLQuery(graphQLField),
						"JSONObject/data",
						"JSONObject/${schemaVarNames}");

					Assert.assertEquals(totalCount + 2, ${schemaVarNames}JSONObject.getLong("totalCount"));

					assertContains(${schemaVarName}1, Arrays.asList(${schemaName}SerDes.toDTOs(${schemaVarNames}JSONObject.getString("items"))));
					assertContains(${schemaVarName}2, Arrays.asList(${schemaName}SerDes.toDTOs(${schemaVarNames}JSONObject.getString("items"))));

					<#if freeMarkerTool.isVersionCompatible(configYAML, 5)>

						// Using the namespace ${graphQLNamespace}

						${schemaVarNames}JSONObject = JSONUtil.getValueAsJSONObject(
							invokeGraphQLQuery(new GraphQLField("${graphQLNamespace}", graphQLField)),
							"JSONObject/data",
							"JSONObject/${graphQLNamespace}",
							"JSONObject/${schemaVarNames}");

						Assert.assertEquals(totalCount + 2, ${schemaVarNames}JSONObject.getLong("totalCount"));

						assertContains(${schemaVarName}1, Arrays.asList(${schemaName}SerDes.toDTOs(${schemaVarNames}JSONObject.getString("items"))));
						assertContains(${schemaVarName}2, Arrays.asList(${schemaName}SerDes.toDTOs(${schemaVarNames}JSONObject.getString("items"))));
					</#if>
				</#if>
			}
		<#elseif configYAML.generateGraphQL && freeMarkerTool.hasHTTPMethod(javaMethodSignature, "get") && javaMethodSignature.returnType?ends_with(schemaName)>
			<#assign getterJavaMethodParametersMap = {} />

			@Test
			public void testGraphQL${javaMethodSignature.methodName?cap_first}() throws Exception {
				<#if javaMethodSignature.pathJavaMethodParameters?size != 0 && (properties?keys?seq_contains("externalReferenceCode") || properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id"))>
					<#assign generateTestGraphQLAddMethod = true />

					${schemaName} ${schemaVarName} = testGraphQL${javaMethodSignature.methodName?cap_first}_add${schemaName}();

					// No namespace

					Assert.assertTrue(
						equals(${schemaVarName},
						${schemaName}SerDes.toDTO(
							JSONUtil.getValueAsString(
								invokeGraphQLQuery(
									new GraphQLField(
										"${freeMarkerTool.getGraphQLPropertyName(javaMethodSignature, javaMethodSignatures)}",
										new HashMap<String, Object>() {
											{
												<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
													<#if freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
														<#if freeMarkerTool.isExternalReferenceCodeParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
															put("${javaMethodParameter.parameterName}",
																<#if stringUtil.equals(properties["externalReferenceCode"], "String")>
																	<@getQuotedString unquotedString = "${schemaVarName}.getExternalReferenceCode()" />
																<#else>
																	${schemaVarName}.getExternalReferenceCode()
																</#if>
															);
														<#elseif freeMarkerTool.isIdParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
															put("${javaMethodParameter.parameterName}",
																<#if stringUtil.equals(properties[idParameterName], "String")>
																	<@getQuotedString unquotedString = "${schemaVarName}.${getIdMethodName}()" />
																<#else>
																	${schemaVarName}.${getIdMethodName}()
																</#if>
															);
														<#elseif properties?keys?seq_contains(javaMethodParameter.parameterName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
															<#if stringUtil.equals(javaMethodParameter.parameterName, "siteId")>
																put("siteKey", <@getQuotedString unquotedString="${schemaVarName}.get${javaMethodParameter.parameterName?cap_first}()" />);
															<#else>
																put("${javaMethodParameter.parameterName}",
																	<#if stringUtil.equals(javaMethodParameter.parameterType, "java.lang.String") || stringUtil.equals(javaMethodParameter.parameterName, "assetLibraryId")>
																		<@getQuotedString unquotedString = "${schemaVarName}.get${javaMethodParameter.parameterName?cap_first}()" />
																	<#else>
																		${schemaVarName}.get${javaMethodParameter.parameterName?cap_first}()
																	</#if>
																);
															</#if>
														<#else>
															<#assign
																getterJavaMethodParametersMap = getterJavaMethodParametersMap + {javaMethodParameter.parameterName: javaMethodParameter}
																getterMethodArgument = ""
															/>

															<#if properties?keys?seq_contains(javaMethodParameter.parameterName)>
																<#assign getterMethodArgument = "${schemaVarName}" />
															</#if>

															<#if stringUtil.equals(javaMethodParameter.parameterName, "siteId")>
																put("siteKey", <@getQuotedString unquotedString="testGraphQL${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}(${getterMethodArgument})" />);
															<#else>
																put("${javaMethodParameter.parameterName}",
																<#if stringUtil.equals(javaMethodParameter.parameterType, "java.lang.String") || stringUtil.equals(javaMethodParameter.parameterName, "assetLibraryId")>
																	<@getQuotedString unquotedString = "testGraphQL${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}(${getterMethodArgument})" />
																<#else>
																	testGraphQL${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}(${getterMethodArgument})
																</#if>
																);
															</#if>
														</#if>
													</#if>
												</#list>
											}
										},
										getGraphQLFields())),
								"JSONObject/data",
								"Object/${freeMarkerTool.getGraphQLPropertyName(javaMethodSignature, javaMethodSignatures)}"))));

					<#if freeMarkerTool.isVersionCompatible(configYAML, 5)>

						// Using the namespace ${graphQLNamespace}

						Assert.assertTrue(
							equals(${schemaVarName},
							${schemaName}SerDes.toDTO(
								JSONUtil.getValueAsString(
									invokeGraphQLQuery(
										new GraphQLField(
											"${graphQLNamespace}",
											new GraphQLField(
												"${freeMarkerTool.getGraphQLPropertyName(javaMethodSignature, javaMethodSignatures)}",
												new HashMap<String, Object>() {
													{
														<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
															<#if freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
																<#if freeMarkerTool.isExternalReferenceCodeParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
																	put("${javaMethodParameter.parameterName}",
																		<#if stringUtil.equals(properties["externalReferenceCode"], "String")>
																			<@getQuotedString unquotedString = "${schemaVarName}.getExternalReferenceCode()" />
																		<#else>
																			${schemaVarName}.getExternalReferenceCode()
																		</#if>
																	);
																<#elseif freeMarkerTool.isIdParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
																	put("${javaMethodParameter.parameterName}",
																		<#if stringUtil.equals(properties[idParameterName], "String")>
																			<@getQuotedString unquotedString = "${schemaVarName}.${getIdMethodName}()" />
																		<#else>
																			${schemaVarName}.${getIdMethodName}()
																		</#if>
																	);
																<#elseif properties?keys?seq_contains(javaMethodParameter.parameterName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
																	<#if stringUtil.equals(javaMethodParameter.parameterName, "siteId")>
																		put("siteKey", <@getQuotedString unquotedString="${schemaVarName}.get${javaMethodParameter.parameterName?cap_first}()" />);
																	<#else>
																		put("${javaMethodParameter.parameterName}",
																			<#if stringUtil.equals(javaMethodParameter.parameterType, "java.lang.String") || stringUtil.equals(javaMethodParameter.parameterName, "assetLibraryId")>
																				<@getQuotedString unquotedString = "${schemaVarName}.get${javaMethodParameter.parameterName?cap_first}()" />
																			<#else>
																				${schemaVarName}.get${javaMethodParameter.parameterName?cap_first}()
																			</#if>
																		);
																	</#if>
																<#else>
																	<#assign getterMethodArgument = "" />

																	<#if properties?keys?seq_contains(javaMethodParameter.parameterName)>
																		<#assign getterMethodArgument = "${schemaVarName}" />
																	</#if>

																	<#if stringUtil.equals(javaMethodParameter.parameterName, "siteId")>
																		put("siteKey", <@getQuotedString unquotedString="testGraphQL${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}(${getterMethodArgument})" />);
																	<#else>
																		<#assign getterJavaMethodParametersMap = getterJavaMethodParametersMap + {javaMethodParameter.parameterName: javaMethodParameter} />

																		put("${javaMethodParameter.parameterName}",
																			<#if stringUtil.equals(javaMethodParameter.parameterType, "java.lang.String") || stringUtil.equals(javaMethodParameter.parameterName, "assetLibraryId")>
																				<@getQuotedString unquotedString = "testGraphQL${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}(${getterMethodArgument})" />
																			<#else>
																				testGraphQL${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}(${getterMethodArgument})
																			</#if>
																		);
																	</#if>
																</#if>
															</#if>
														</#list>
													}
												},
												getGraphQLFields()))),
									"JSONObject/data",
									"JSONObject/${graphQLNamespace}",
									"Object/${freeMarkerTool.getGraphQLPropertyName(javaMethodSignature, javaMethodSignatures)}"))));
					</#if>
				<#else>
					Assert.assertTrue(true);
				</#if>
			}

			<@getTestGetterMethods
				getterJavaMethodParametersMap = getterJavaMethodParametersMap
				testJavaMethodName = javaMethodSignature.methodName
				testNamePrefix = "testGraphQL"
			/>

			@Test
			public void testGraphQL${javaMethodSignature.methodName?cap_first}NotFound() throws Exception {
				<#if javaMethodSignature.javaMethodParameters?size != 0 && (properties?keys?seq_contains("externalReferenceCode") || properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id"))>
					<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
						<#if !stringUtil.equals(javaMethodParameter.parameterName, "assetLibraryExternalReferenceCode") && !stringUtil.equals(javaMethodParameter.parameterName, "assetLibraryId") && !stringUtil.equals(javaMethodParameter.parameterName, "siteExternalReferenceCode") && !stringUtil.equals(javaMethodParameter.parameterName, "siteId") && freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
							${javaMethodParameter.parameterType} irrelevant${javaMethodParameter.parameterName?cap_first} =
							<#if stringUtil.equals(javaMethodParameter.parameterType, "java.lang.Boolean")>
								RandomTestUtil.randomBoolean();
							<#elseif stringUtil.equals(javaMethodParameter.parameterType, "java.lang.Integer")>
								RandomTestUtil.randomInt();
							<#elseif stringUtil.equals(javaMethodParameter.parameterType, "java.lang.Long")>
								RandomTestUtil.randomLong();
							<#elseif stringUtil.equals(javaMethodParameter.parameterType, "java.lang.Double")>
								RandomTestUtil.randomDouble();
							<#elseif stringUtil.equals(javaMethodParameter.parameterType, "java.lang.String")>
								<@getQuotedString unquotedString="RandomTestUtil.randomString()" />;
							<#else>
								null;
							</#if>
						</#if>
					</#list>

					// No namespace

					Assert.assertEquals(
						"Not Found",
						JSONUtil.getValueAsString(
							invokeGraphQLQuery(
								new GraphQLField(
									"${freeMarkerTool.getGraphQLPropertyName(javaMethodSignature, javaMethodSignatures)}",
									new HashMap<String, Object>() {
										{
											<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
												<#if freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
													<#if stringUtil.equals(javaMethodParameter.parameterName, "assetLibraryExternalReferenceCode")>
														put("assetLibraryExternalReferenceCode", <@getQuotedString unquotedString="irrelevantDepotEntryGroup.getExternalReferenceCode()" />);
													<#elseif stringUtil.equals(javaMethodParameter.parameterName, "assetLibraryId")>
														put("assetLibraryId", <@getQuotedString unquotedString="irrelevantDepotEntry.getDepotEntryId()" />);
													<#elseif stringUtil.equals(javaMethodParameter.parameterName, "siteExternalReferenceCode")>
														put("siteExternalReferenceCode", <@getQuotedString unquotedString="irrelevantGroup.getExternalReferenceCode()" />);
													<#elseif stringUtil.equals(javaMethodParameter.parameterName, "siteId")>
														put("siteKey", <@getQuotedString unquotedString="irrelevantGroup.getGroupId()" />);
													<#else>
														put("${javaMethodParameter.parameterName}", irrelevant${javaMethodParameter.parameterName?cap_first});
													</#if>
												</#if>
											</#list>
										}
									},
									getGraphQLFields())),
							"JSONArray/errors", "Object/0", "JSONObject/extensions", "Object/code"));

					<#if freeMarkerTool.isVersionCompatible(configYAML, 5)>

						// Using the namespace ${graphQLNamespace}

						Assert.assertEquals(
							"Not Found",
							JSONUtil.getValueAsString(
								invokeGraphQLQuery(
									new GraphQLField(
										"${graphQLNamespace}",
										new GraphQLField(
											"${freeMarkerTool.getGraphQLPropertyName(javaMethodSignature, javaMethodSignatures)}",
											new HashMap<String, Object>() {
												{
													<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
														<#if freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation)>
															<#if stringUtil.equals(javaMethodParameter.parameterName, "assetLibraryExternalReferenceCode")>
																put("assetLibraryExternalReferenceCode", <@getQuotedString unquotedString="irrelevantDepotEntryGroup.getExternalReferenceCode()" />);
															<#elseif stringUtil.equals(javaMethodParameter.parameterName, "assetLibraryId")>
																put("assetLibraryId", <@getQuotedString unquotedString="irrelevantDepotEntry.getDepotEntryId()" />);
															<#elseif stringUtil.equals(javaMethodParameter.parameterName, "siteExternalReferenceCode")>
																put("siteExternalReferenceCode", <@getQuotedString unquotedString="irrelevantGroup.getExternalReferenceCode()" />);
															<#elseif stringUtil.equals(javaMethodParameter.parameterName, "siteId")>
																put("siteKey", <@getQuotedString unquotedString="irrelevantGroup.getGroupId()" />);
															<#else>
																put("${javaMethodParameter.parameterName}", irrelevant${javaMethodParameter.parameterName?cap_first});
															</#if>
														</#if>
													</#list>
												}
											},
											getGraphQLFields()))),
								"JSONArray/errors", "Object/0", "JSONObject/extensions", "Object/code"));
					</#if>
				<#else>
					Assert.assertTrue(true);
				</#if>
				}
		<#elseif configYAML.generateGraphQL && freeMarkerTool.hasHTTPMethod(javaMethodSignature, "post") && stringUtil.equals(javaMethodSignature.methodName, "postSite" + schemaName) && javaMethodSignature.returnType?ends_with(schemaName) && !freeMarkerTool.hasRequestBodyMediaType(javaMethodSignature, "multipart/form-data")>
			@Test
			public void testGraphQL${javaMethodSignature.methodName?cap_first}() throws Exception {
				<#if !(properties?keys?seq_contains("externalReferenceCode") || properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id"))>
					Assert.assertTrue(false);
				<#else>
					${schemaName} random${schemaName} = random${schemaName}();

					${schemaName} ${schemaVarName} = testGraphQL${schemaName}_add${schemaName}(random${schemaName});

					Assert.assertTrue(equals(random${schemaName}, ${schemaVarName}));
				</#if>
			}
		</#if>

		<#if generateTestGraphQLAddMethod>
			<#assign generateSchemaGraphQLAddMethod = true />

			protected ${schemaName} testGraphQL${javaMethodSignature.methodName?cap_first}_add${schemaName}() throws Exception {
				return testGraphQL${schemaName}_add${schemaName}();
			}
		</#if>
	</#list>

	<#if freeMarkerTool.isVersionCompatible(configYAML, 8) && generateBatch>
		@Test
		public void testBatchEngineDeleteImportTask() throws Exception {
			<#if useDeleteAssetLibrary || useDeleteByExternalReferenceCode || useDeleteById || useDeleteSite>
				<#assign
					getAssetLibraryJavaMethodSignature = (freeMarkerTool.getJavaMethodSignature(javaMethodSignatures, "getAssetLibrary" + schemaName))!""
					getJavaMethodSignature = (freeMarkerTool.getJavaMethodSignature(javaMethodSignatures, "get" + schemaName))!""
					getSiteJavaMethodSignature = (freeMarkerTool.getJavaMethodSignature(javaMethodSignatures, "getSite" + schemaName))!""
					getterJavaMethodParametersMap = {}
				/>

				<#if useDeleteAssetLibrary>
					${schemaName} ${schemaVarName}1 = testBatchEngineDeleteImportTask_addAssetLibrary${schemaName}();

					testBatchEngineDeleteImportTask_delete${schemaName}(200, ${schemaVarName}1.getExternalReferenceCode(),<#if useDeleteById> null,</#if> "assetLibraryExternalReferenceCode", testDepotEntryGroup.getExternalReferenceCode());

					<#if getAssetLibraryJavaMethodSignature?has_content>
						assertHttpResponseStatusCode(
							404,
							${schemaVarName}Resource.${getAssetLibraryJavaMethodSignature.methodName}HttpResponse(
								<@getGetParameters
									javaMethodSignature = getAssetLibraryJavaMethodSignature
									testJavaMethodName = "batchEngineDeleteImportTask"
									varName = schemaVarName + "1"
								/>));
					</#if>
				</#if>

				<#if useDeleteByExternalReferenceCode>
					<#if !useDeleteAssetLibrary>${schemaName}</#if> ${schemaVarName}1 = testBatchEngineDeleteImportTask_add${schemaName}();

					testBatchEngineDeleteImportTask_delete${schemaName}(200, ${schemaVarName}1.getExternalReferenceCode()<#if useDeleteById>, null</#if>);

					<#if getJavaMethodSignature?has_content>
						assertHttpResponseStatusCode(
							404,
							${schemaVarName}Resource.${getJavaMethodSignature.methodName}HttpResponse(
								<@getGetParameters
									javaMethodSignature = getJavaMethodSignature
									testJavaMethodName = "batchEngineDeleteImportTask"
									varName = schemaVarName + "1"
								/>));
					</#if>
				</#if>

				<#if useDeleteById>
					<#if !useDeleteAssetLibrary && !useDeleteByExternalReferenceCode>${schemaName}</#if> ${schemaVarName}1 = testBatchEngineDeleteImportTask_add${schemaName}();

					testBatchEngineDeleteImportTask_delete${schemaName}(200, null, ${schemaVarName}1.${getIdMethodName}());

					<#if getJavaMethodSignature?has_content>
						assertHttpResponseStatusCode(
							404,
							${schemaVarName}Resource.${getJavaMethodSignature.methodName}HttpResponse(
								<@getGetParameters
									javaMethodSignature = getJavaMethodSignature
									testJavaMethodName = "batchEngineDeleteImportTask"
									varName = schemaVarName + "1"
								/>));
					</#if>
				</#if>

				<#if useDeleteSite>
					<#if !useDeleteAssetLibrary && !useDeleteByExternalReferenceCode && !useDeleteById>${schemaName}</#if> ${schemaVarName}1 = testBatchEngineDeleteImportTask_addSite${schemaName}();

					testBatchEngineDeleteImportTask_delete${schemaName}(200, ${schemaVarName}1.getExternalReferenceCode(),<#if useDeleteById> null,</#if> "siteExternalReferenceCode", testGroup.getExternalReferenceCode());

					<#if getSiteJavaMethodSignature?has_content>
						assertHttpResponseStatusCode(
							404,
							${schemaVarName}Resource.${getSiteJavaMethodSignature.methodName}HttpResponse(
								<@getGetParameters
									javaMethodSignature = getSiteJavaMethodSignature
									testJavaMethodName = "batchEngineDeleteImportTask"
									varName = schemaVarName + "1"
								/>));
					</#if>
				</#if>

				<#if (useDeleteAssetLibrary || useDeleteSite) && useDeleteById>
					<#if useDeleteAssetLibrary>
						${schemaVarName}1 = testBatchEngineDeleteImportTask_addAssetLibrary${schemaName}();
						${schemaName} ${schemaVarName}2 = testBatchEngineDeleteImportTask_addAssetLibrary${schemaName}();

						testBatchEngineDeleteImportTask_delete${schemaName}(200, ${schemaVarName}2.getExternalReferenceCode(), ${schemaVarName}1.${getIdMethodName}(), "assetLibraryExternalReferenceCode", testDepotEntryGroup.getExternalReferenceCode());

						<#if getJavaMethodSignature?has_content>
							assertHttpResponseStatusCode(
								404,
								${schemaVarName}Resource.${getJavaMethodSignature.methodName}HttpResponse(
									<@getGetParameters
										javaMethodSignature = getJavaMethodSignature
										testJavaMethodName = "batchEngineDeleteImportTask"
										varName = schemaVarName + "1"
									/>));
							assertHttpResponseStatusCode(
								200,
								${schemaVarName}Resource.${getJavaMethodSignature.methodName}HttpResponse(
									<@getGetParameters
										javaMethodSignature = getJavaMethodSignature
										testJavaMethodName = "batchEngineDeleteImportTask"
										varName = schemaVarName + "2"
									/>));
						</#if>

						testBatchEngineDeleteImportTask_delete${schemaName}(200, ${schemaVarName}2.getExternalReferenceCode(), ${schemaVarName}1.${getIdMethodName}(), "assetLibraryExternalReferenceCode", testDepotEntryGroup.getExternalReferenceCode());

						<#if getJavaMethodSignature?has_content>
							assertHttpResponseStatusCode(
								404,
								${schemaVarName}Resource.${getJavaMethodSignature.methodName}HttpResponse(
									<@getGetParameters
										javaMethodSignature = getJavaMethodSignature
										testJavaMethodName = "batchEngineDeleteImportTask"
										varName = schemaVarName + "2"
									/>));
						</#if>
					</#if>

					<#if useDeleteSite>
						${schemaVarName}1 = testBatchEngineDeleteImportTask_addSite${schemaName}();
						<#if !useDeleteAssetLibrary>${schemaName}</#if> ${schemaVarName}2 = testBatchEngineDeleteImportTask_addSite${schemaName}();

						testBatchEngineDeleteImportTask_delete${schemaName}(200, ${schemaVarName}2.getExternalReferenceCode(), ${schemaVarName}1.${getIdMethodName}(), "siteExternalReferenceCode", testGroup.getExternalReferenceCode());

						<#if getJavaMethodSignature?has_content>
							assertHttpResponseStatusCode(
								404,
								${schemaVarName}Resource.${getJavaMethodSignature.methodName}HttpResponse(
									<@getGetParameters
										javaMethodSignature = getJavaMethodSignature
										testJavaMethodName = "batchEngineDeleteImportTask"
										varName = schemaVarName + "1"
									/>));
							assertHttpResponseStatusCode(
								200,
								${schemaVarName}Resource.${getJavaMethodSignature.methodName}HttpResponse(
									<@getGetParameters
										javaMethodSignature = getJavaMethodSignature
										testJavaMethodName = "batchEngineDeleteImportTask"
										varName = schemaVarName + "2"
									/>));
						</#if>

						testBatchEngineDeleteImportTask_delete${schemaName}(200, ${schemaVarName}2.getExternalReferenceCode(), ${schemaVarName}1.${getIdMethodName}(), "siteExternalReferenceCode", testGroup.getExternalReferenceCode());

						<#if getJavaMethodSignature?has_content>
							assertHttpResponseStatusCode(
								404,
								${schemaVarName}Resource.${getJavaMethodSignature.methodName}HttpResponse(
									<@getGetParameters
										javaMethodSignature = getJavaMethodSignature
										testJavaMethodName = "batchEngineDeleteImportTask"
										varName = schemaVarName + "2"
									/>));
						</#if>
					</#if>

					<#if useDeleteAssetLibrary && useDeleteSite>
						${schemaVarName}1 = testBatchEngineDeleteImportTask_addSite${schemaName}();

						testBatchEngineDeleteImportTask_delete${schemaName}(400, ${schemaVarName}1.getExternalReferenceCode(), ${schemaVarName}1.${getIdMethodName}(), "assetLibraryExternalReferenceCode", testDepotEntryGroup.getExternalReferenceCode(), "siteExternalReferenceCode", testGroup.getExternalReferenceCode());

						<#if getJavaMethodSignature?has_content>
							assertHttpResponseStatusCode(
								200,
								${schemaVarName}Resource.${getJavaMethodSignature.methodName}HttpResponse(
									<@getGetParameters
										javaMethodSignature = getJavaMethodSignature
										testJavaMethodName = "batchEngineDeleteImportTask"
										varName = schemaVarName + "1"
									/>));
						</#if>
					</#if>
				</#if>

				<#if useDeleteByExternalReferenceCode && useDeleteById>
					${schemaVarName}1 = testBatchEngineDeleteImportTask_add${schemaName}();
					${schemaName} ${schemaVarName}2 = testBatchEngineDeleteImportTask_add${schemaName}();

					testBatchEngineDeleteImportTask_delete${schemaName}(200, ${schemaVarName}2.getExternalReferenceCode(), ${schemaVarName}1.${getIdMethodName}());

					<#if getJavaMethodSignature?has_content>
						assertHttpResponseStatusCode(
							404,
							${schemaVarName}Resource.${getJavaMethodSignature.methodName}HttpResponse(
								<@getGetParameters
									javaMethodSignature = getJavaMethodSignature
									testJavaMethodName = "batchEngineDeleteImportTask"
									varName = schemaVarName + "1"
								/>));
						assertHttpResponseStatusCode(
							200,
							${schemaVarName}Resource.${getJavaMethodSignature.methodName}HttpResponse(
								<@getGetParameters
									javaMethodSignature = getJavaMethodSignature
									testJavaMethodName = "batchEngineDeleteImportTask"
									varName = schemaVarName + "2"
								/>));
					</#if>

					testBatchEngineDeleteImportTask_delete${schemaName}(200, ${schemaVarName}2.getExternalReferenceCode(), ${schemaVarName}1.${getIdMethodName}());

					<#if getJavaMethodSignature?has_content>
						assertHttpResponseStatusCode(
							404,
							${schemaVarName}Resource.${getJavaMethodSignature.methodName}HttpResponse(
								<@getGetParameters
									javaMethodSignature = getJavaMethodSignature
									testJavaMethodName = "batchEngineDeleteImportTask"
									varName = schemaVarName + "2"
								/>));
					</#if>
				</#if>

				}

				<#if useDeleteByExternalReferenceCode || useDeleteById>
					protected ${schemaName} testBatchEngineDeleteImportTask_add${schemaName}() throws Exception {
						<#if (properties?keys?seq_contains("externalReferenceCode") || properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id")) && freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, "delete" + schemaName)>
							return testDelete${schemaName}_add${schemaName}();
						<#else>
							throw new UnsupportedOperationException("This method needs to be implemented");
						</#if>
					}
				</#if>

				<#if useDeleteAssetLibrary>
					protected ${schemaName} testBatchEngineDeleteImportTask_addAssetLibrary${schemaName}() throws Exception {
						<#if (properties?keys?seq_contains("externalReferenceCode") || properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id")) && freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, "deleteAssetLibrary" + schemaName)>
							return testDeleteAssetLibrary${schemaName}_add${schemaName}();
						<#else>
							throw new UnsupportedOperationException("This method needs to be implemented");
						</#if>
					}
				</#if>

				<#if useDeleteSite>
					protected ${schemaName} testBatchEngineDeleteImportTask_addSite${schemaName}() throws Exception {
						<#if (properties?keys?seq_contains("externalReferenceCode") || properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id")) && freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, "deleteSite" + schemaName)>
							return testDeleteSite${schemaName}_add${schemaName}();
						<#else>
							throw new UnsupportedOperationException("This method needs to be implemented");
						</#if>
					}
				</#if>

				protected void testBatchEngineDeleteImportTask_delete${schemaName}(int expectedStatusCode, String externalReferenceCode,<#if useDeleteById> ${properties[idParameterName]} id,</#if> String... parameters) throws Exception {
					ImportTaskResource importTaskResource = ImportTaskResource.builder(
						).authentication(
							_testCompanyAdminUser.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
						).endpoint(
							testCompany.getVirtualHostname(), 8080, "http"
						).parameters(
							parameters
						).build();

					HttpResponse httpResponse = importTaskResource.deleteImportTaskHttpResponse(
						"${configYAML.apiPackagePath}.dto.${escapedVersion}.${schemaName}",
						null, null, null, null,
						JSONUtil.putAll(
							JSONUtil.put(
								"externalReferenceCode", () -> externalReferenceCode
							)
							<#if useDeleteById>
							.put(
								"${idParameterName}", () -> id
							)
							</#if>
						));

					Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

					if (expectedStatusCode == 200) {
						waitForFinish("COMPLETED", JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
					}
				}

				<@getTestGetterMethods
					getterJavaMethodParametersMap = getterJavaMethodParametersMap
					testJavaMethodName = "batchEngineDeleteImportTask"
				/>
			<#else>
				Assert.assertTrue(true); }
			</#if>
	</#if>

	<#if generateSearchTestRule>
		@Rule
		public SearchTestRule searchTestRule = new SearchTestRule();
	</#if>

	<#list relatedSchemaNames as relatedSchemaName>
		<#assign
			relatedSchemaProperties = freeMarkerTool.getDTOProperties(configYAML, openAPIYAML, relatedSchemaName, allSchemas)
			relatedSchemaVarName = freeMarkerTool.getSchemaVarName(relatedSchemaName)
		/>

		<#list javaMethodSignatures as javaMethodSignature>
			<#if freeMarkerTool.hasHTTPMethod(javaMethodSignature, "get") && javaMethodSignature.returnType?ends_with("." + relatedSchemaName)>
				@Test
				public void test${javaMethodSignature.methodName?cap_first}() throws Exception {
					${schemaName} post${schemaName} = testGet${schemaName}_add${schemaName}();

					${relatedSchemaName} post${relatedSchemaName} = test${javaMethodSignature.methodName?cap_first}_add${relatedSchemaName}(post${schemaName}.${getIdMethodName}(), random${relatedSchemaName}());

					${relatedSchemaName} get${relatedSchemaName} = ${schemaVarName}Resource.${javaMethodSignature.methodName}(post${schemaName}.${getIdMethodName}());

					assertEquals(post${relatedSchemaName}, get${relatedSchemaName});
					assertValid(get${relatedSchemaName});
				}

				protected ${relatedSchemaName} test${javaMethodSignature.methodName?cap_first}_add${relatedSchemaName}(long ${schemaVarName}Id, ${relatedSchemaName} ${relatedSchemaVarName}) throws Exception {
					<#if freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, javaMethodSignature.methodName?replace("get", "post"))>
						return ${schemaVarName}Resource.${javaMethodSignature.methodName?replace("get", "post")}(${schemaVarName}Id, ${relatedSchemaVarName});
					<#else>
						throw new UnsupportedOperationException("This method needs to be implemented");
					</#if>
				}
			<#elseif freeMarkerTool.hasHTTPMethod(javaMethodSignature, "patch") && javaMethodSignature.returnType?ends_with("." + relatedSchemaName)>
				@Test
				public void test${javaMethodSignature.methodName?cap_first}() throws Exception {
					${schemaName} post${schemaName} = testPatch${schemaName}_add${schemaName}();

					test${javaMethodSignature.methodName?cap_first}_add${relatedSchemaName}(post${schemaName}.${getIdMethodName}(), random${relatedSchemaName}());

					${relatedSchemaName} random${relatedSchemaName} = random${relatedSchemaName}();

					${relatedSchemaName} patch${relatedSchemaName} = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
						<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
							<#if freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation) && stringUtil.equals(javaMethodParameter.parameterName, schemaVarName + "Id")>
								post${schemaName}.${getIdMethodName}()
							<#elseif stringUtil.equals(javaMethodParameter.parameterName, relatedSchemaVarName)>
								random${relatedSchemaName}
							<#else>
								null
							</#if>

							<#sep>, </#sep>
						</#list>
					);

					assertEquals(random${relatedSchemaName}, patch${relatedSchemaName});
					assertValid(patch${relatedSchemaName});
				}

				protected ${relatedSchemaName} test${javaMethodSignature.methodName?cap_first}_add${relatedSchemaName}(long ${schemaVarName}Id, ${relatedSchemaName} ${relatedSchemaVarName}) throws Exception {
					<#if freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, javaMethodSignature.methodName?replace("patch", "post"))>
						return ${schemaVarName}Resource.${javaMethodSignature.methodName?replace("patch", "post")}(${schemaVarName}Id, ${relatedSchemaVarName});
					<#else>
						throw new UnsupportedOperationException("This method needs to be implemented");
					</#if>
				}
			<#elseif freeMarkerTool.hasHTTPMethod(javaMethodSignature, "post") && javaMethodSignature.returnType?ends_with("." + relatedSchemaName)>
				@Test
				public void test${javaMethodSignature.methodName?cap_first}() throws Exception {
					Assert.assertTrue(true);
				}
			<#elseif freeMarkerTool.hasHTTPMethod(javaMethodSignature, "put") && javaMethodSignature.returnType?ends_with("." + relatedSchemaName)>
				@Test
				public void test${javaMethodSignature.methodName?cap_first}() throws Exception {
					${schemaName} post${schemaName} = testPut${schemaName}_add${schemaName}();

					test${javaMethodSignature.methodName?cap_first}_add${relatedSchemaName}(post${schemaName}.${getIdMethodName}(), random${relatedSchemaName}());

					${relatedSchemaName} random${relatedSchemaName} = random${relatedSchemaName}();

					${relatedSchemaName} put${relatedSchemaName} = ${schemaVarName}Resource.${javaMethodSignature.methodName}(
						<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
							<#if freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation) && stringUtil.equals(javaMethodParameter.parameterName, schemaVarName + "Id")>
								post${schemaName}.${getIdMethodName}()
							<#elseif stringUtil.equals(javaMethodParameter.parameterName, relatedSchemaVarName)>
								random${relatedSchemaName}
							<#else>
								null
							</#if>

							<#sep>, </#sep>
						</#list>
					);

					assertEquals(random${relatedSchemaName}, put${relatedSchemaName});
					assertValid(put${relatedSchemaName});
				}

				protected ${relatedSchemaName} test${javaMethodSignature.methodName?cap_first}_add${relatedSchemaName}(long ${schemaVarName}Id, ${relatedSchemaName} ${relatedSchemaVarName}) throws Exception {
					<#if freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, javaMethodSignature.methodName?replace("put", "post"))>
						return ${schemaVarName}Resource.${javaMethodSignature.methodName?replace("put", "post")}(${schemaVarName}Id, ${relatedSchemaVarName});
					<#else>
						throw new UnsupportedOperationException("This method needs to be implemented");
					</#if>
				}
			</#if>
		</#list>
	</#list>

	<#if generateSchemaGraphQLAddMethod>
		<#if freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, "postSite" + schemaName)>
			protected void appendGraphQLFieldValue(StringBuilder sb, Object value) throws Exception {
				if (value instanceof Object[]) {
					StringBuilder arraySB = new StringBuilder("[");

					for (Object object : (Object[])value) {
						if (arraySB.length() > 1) {
							arraySB.append(", ");
						}

						arraySB.append("{");

						Class<?> clazz = object.getClass();

						for (java.lang.reflect.Field field : getDeclaredFields(clazz.getSuperclass())) {
							arraySB.append(field.getName());
							arraySB.append(": ");

							appendGraphQLFieldValue(arraySB, field.get(object));

							arraySB.append(", ");
						}

						arraySB.setLength(arraySB.length() - 2);

						arraySB.append("}");
					}

					arraySB.append("]");

					sb.append(arraySB.toString());
				}
				else if (value instanceof String) {
					sb.append("\"");
					sb.append(value);
					sb.append("\"");
				}
				else {
					sb.append(value);
				}
			}

			protected ${schemaName} testGraphQL${schemaName}_add${schemaName}() throws Exception {
				return testGraphQL${schemaName}_add${schemaName}(random${schemaName}());
			}

			protected ${schemaName} testGraphQL${schemaName}_add${schemaName}(${schemaName} ${schemaVarName}) throws Exception {
				JSONDeserializer<${schemaName}> jsonDeserializer = JSONFactoryUtil.createJSONDeserializer();

				StringBuilder sb = new StringBuilder("{");

				for (java.lang.reflect.Field field : getDeclaredFields(${schemaName}.class)) {
					if (!ArrayUtil.contains(getAdditionalAssertFieldNames(), field.getName())) {
						continue;
					}

					if (sb.length() > 1) {
						sb.append(", ");
					}

					sb.append(field.getName());
					sb.append(": ");

					appendGraphQLFieldValue(sb, field.get(${schemaVarName}));
				}

				sb.append("}");

				List<GraphQLField> graphQLFields = getGraphQLFields();

				<#if properties?keys?seq_contains("externalReferenceCode")>
					graphQLFields.add(new GraphQLField("externalReferenceCode"));
				</#if>

				<#if properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id")>
					graphQLFields.add(new GraphQLField("${idParameterName}"));
				</#if>

				return jsonDeserializer.deserialize(
					JSONUtil.getValueAsString(
						invokeGraphQLMutation(
							new GraphQLField(
								"createSite${schemaName}",
								new HashMap<String, Object>() {
									{
										put("siteKey", <@getQuotedString unquotedString="testGroup.getGroupId()" />);
										put("${schemaVarName}", sb.toString());
									}
								},
								graphQLFields)),
						"JSONObject/data",
						"JSONObject/createSite${schemaName}"),
					${schemaName}.class);
			}
		<#else>
			protected ${schemaName} testGraphQL${schemaName}_add${schemaName}() throws Exception {
				throw new UnsupportedOperationException("This method needs to be implemented");
			}
		</#if>
	</#if>

	protected void assertContains(${schemaClientJavaType} ${schemaVarName}, List<${schemaClientJavaType}> ${schemaVarNames}) {
		boolean contains = false;

		for (${schemaClientJavaType} item : ${schemaVarNames}) {
			if (equals(${schemaVarName}, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(${schemaVarNames} + " does not contain " + ${schemaVarName}, contains);
	}

	protected void assertHttpResponseStatusCode(int expectedHttpResponseStatusCode, HttpInvoker.HttpResponse actualHttpResponse) {
		Assert.assertEquals(expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(${schemaClientJavaType} ${schemaVarName}1, ${schemaClientJavaType} ${schemaVarName}2) {
		Assert.assertTrue(${schemaVarName}1 + " does not equal " + ${schemaVarName}2, equals(${schemaVarName}1, ${schemaVarName}2));
	}

	protected void assertEquals(List<${schemaClientJavaType}> ${schemaVarNames}1, List<${schemaClientJavaType}> ${schemaVarNames}2) {
		Assert.assertEquals(${schemaVarNames}1.size(), ${schemaVarNames}2.size());

		for (int i = 0; i < ${schemaVarNames}1.size(); i++) {
			${schemaClientJavaType} ${schemaVarName}1 = ${schemaVarNames}1.get(i);
			${schemaClientJavaType} ${schemaVarName}2 = ${schemaVarNames}2.get(i);

			assertEquals(${schemaVarName}1, ${schemaVarName}2);
		}
	}

	<#list relatedSchemaNames as relatedSchemaName>
		<#assign
			relatedSchemaProperties = freeMarkerTool.getDTOProperties(configYAML, openAPIYAML, relatedSchemaName, allSchemas)
			relatedSchemaVarName = freeMarkerTool.getSchemaVarName(relatedSchemaName)
		/>

		protected void assertEquals(${relatedSchemaName} ${relatedSchemaVarName}1, ${relatedSchemaName} ${relatedSchemaVarName}2) {
			Assert.assertTrue(${relatedSchemaVarName}1 + " does not equal " + ${relatedSchemaVarName}2, equals(${relatedSchemaVarName}1, ${relatedSchemaVarName}2));
		}
	</#list>

	protected void assertEqualsIgnoringOrder(List<${schemaClientJavaType}> ${schemaVarNames}1, List<${schemaClientJavaType}> ${schemaVarNames}2) {
		Assert.assertEquals(${schemaVarNames}1.size(), ${schemaVarNames}2.size());

		for (${schemaClientJavaType} ${schemaVarName}1 : ${schemaVarNames}1) {
			boolean contains = false;

			for (${schemaClientJavaType} ${schemaVarName}2 : ${schemaVarNames}2) {
				if (equals(${schemaVarName}1, ${schemaVarName}2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(${schemaVarNames}2 + " does not contain " + ${schemaVarName}1, contains);
		}
	}

	protected void assertValid(${schemaClientJavaType} ${schemaVarName}) throws Exception {
		boolean valid = true;

		<#if properties?keys?seq_contains("dateCreated")>
			if (${schemaVarName}.getDateCreated() == null) {
				valid = false;
			}
		</#if>

		<#if properties?keys?seq_contains("dateModified")>
			if (${schemaVarName}.getDateModified() == null) {
				valid = false;
			}
		</#if>

		<#if properties?keys?seq_contains("externalReferenceCode") && freeMarkerTool.isSchemaPropertyRequired(openAPIYAML, schemaName, "externalReferenceCode")>
			if (${schemaVarName}.getExternalReferenceCode() == null) {
				valid = false;
			}
		</#if>

		<#if properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id")>
			if (${schemaVarName}.${getIdMethodName}() == null) {
				valid = false;
			}
		</#if>

		<#if properties?keys?seq_contains("siteId")>
			<#if generateDepotEntry>
				if (!Objects.equals(${schemaVarName}.getAssetLibraryKey(), testDepotEntryGroup.getGroupKey()) && !Objects.equals(${schemaVarName}.getSiteId(), testGroup.getGroupId())) {
					valid = false;
				}
			<#else>
				if (!Objects.equals(${schemaVarName}.getSiteId(), testGroup.getGroupId())) {
					valid = false;
				}
			</#if>
		</#if>

		for (String additionalAssertFieldName : getAdditionalAssertFieldNames()) {
			<#list properties?keys as propertyName>
				<#if stringUtil.equals(propertyName, "dateCreated") ||
					 stringUtil.equals(propertyName, "dateModified") ||
					 stringUtil.equals(propertyName, "id") ||
					 stringUtil.equals(propertyName, "siteId")>

					 <#continue>
				</#if>

				if (Objects.equals("${propertyName}", additionalAssertFieldName)) {
					<#assign capitalizedPropertyName = propertyName?cap_first />

					<#if enumSchemas?keys?seq_contains(properties[propertyName])>
						<#assign capitalizedPropertyName = properties[propertyName] />
					</#if>

					if (${schemaVarName}.get${capitalizedPropertyName}() == null) {
						valid = false;
					}

					continue;
				}
			</#list>

			<#if schema.discriminator?has_content>
				<#list schema.discriminator.mapping as mappingName, mappingSchema>
					<#assign childSchemaProperties = freeMarkerTool.getDTOProperties(configYAML, openAPIYAML, allSchemas[freeMarkerTool.getReferenceName(mappingSchema)], allSchemas) />

					<#if childSchemaProperties?has_content>
						<#list childSchemaProperties?keys as propertyName>
							if (Objects.equals("${propertyName}", additionalAssertFieldName)) {
								if (!(${schemaVarName} instanceof ${mappingName})) {
									continue;
								}

								<#assign capitalizedPropertyName = propertyName?cap_first />

								<#if enumSchemas?keys?seq_contains(childSchemaProperties[propertyName])>
									<#assign capitalizedPropertyName = childSchemaProperties[propertyName] />
								</#if>

								if (((${mappingName})${schemaVarName}).get${capitalizedPropertyName}() == null) {
									valid = false;
								}

								continue;
							}
						</#list>
					</#if>
				</#list>
			</#if>

			throw new IllegalArgumentException("Invalid additional assert field name " + additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	<#if generateGetMultipartFilesMethod>
		protected void assertValid(${schemaClientJavaType} ${schemaVarName}, Map<String, File> multipartFiles) throws Exception {
			throw new UnsupportedOperationException("This method needs to be implemented");
		}
	</#if>

	protected void assertValid(Page<${schemaClientJavaType}> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(Page<${schemaClientJavaType}> page, Map<String, Map<String, String>> expectedActions) {
		boolean valid = false;

		java.util.Collection<${schemaClientJavaType}> ${schemaVarNames} = page.getItems();

		int size = ${schemaVarNames}.size();

		if ((page.getLastPage() > 0) && (page.getPage() > 0) && (page.getPageSize() > 0) && (page.getTotalCount() > 0) && (size > 0)) {
			valid = true;
		}

		Assert.assertTrue(valid);

		assertValid(page.getActions(), expectedActions);
	}

	protected void assertValid(Map<String, Map<String, String>> actions1, Map<String, Map<String, String>> actions2) {
		for (String key : actions2.keySet()) {
			Map action = actions1.get(key);

			Assert.assertNotNull(key + " does not contain an action", action);

			Map<String, String> expectedAction = actions2.get(key);

			Assert.assertEquals(expectedAction.get("method"), action.get("method"));
			Assert.assertEquals(expectedAction.get("href"), action.get("href"));
		}
	}

	<#list relatedSchemaNames as relatedSchemaName>
		<#assign
			relatedSchemaProperties = freeMarkerTool.getDTOProperties(configYAML, openAPIYAML, relatedSchemaName, allSchemas)
			relatedSchemaVarName = freeMarkerTool.getSchemaVarName(relatedSchemaName)
		/>

		protected void assertValid(${configYAML.apiPackagePath}.client.dto.${escapedVersion}.${relatedSchemaName} ${relatedSchemaVarName}) {
			boolean valid = true;

			<#if relatedSchemaProperties?keys?seq_contains("dateCreated")>
				if (${relatedSchemaVarName}.getDateCreated() == null) {
					valid = false;
				}
			</#if>

			<#if relatedSchemaProperties?keys?seq_contains("dateModified")>
				if (${relatedSchemaVarName}.getDateModified() == null) {
					valid = false;
				}
			</#if>

			<#if relatedSchemaProperties?keys?seq_contains("externalReferenceCode")>
				if (${relatedSchemaVarName}.getExternalReferenceCode() == null) {
					valid = false;
				}
			</#if>

			<#if relatedSchemaProperties?keys?seq_contains("id")>
				if (${relatedSchemaVarName}.getId() == null) {
					valid = false;
				}
			</#if>

			<#if relatedSchemaProperties?keys?seq_contains("siteId")>
				if (!Objects.equals(${relatedSchemaVarName}.getSiteId(), testGroup.getGroupId())) {
					valid = false;
				}
			</#if>

			for (String additionalAssertFieldName : getAdditional${relatedSchemaName}AssertFieldNames()) {
				<#list relatedSchemaProperties?keys as propertyName>
					<#if stringUtil.equals(propertyName, "dateCreated") ||
						 stringUtil.equals(propertyName, "dateModified") ||
						 stringUtil.equals(propertyName, "id") ||
						 stringUtil.equals(propertyName, "siteId")>

						 <#continue>
					</#if>

					if (Objects.equals("${propertyName}", additionalAssertFieldName)) {
						<#assign capitalizedPropertyName = propertyName?cap_first />

						<#if enumSchemas?keys?seq_contains(relatedSchemaProperties[propertyName])>
							<#assign capitalizedPropertyName = relatedSchemaProperties[propertyName] />
						</#if>

						if (${relatedSchemaVarName}.get${capitalizedPropertyName}() == null) {
							valid = false;
						}

						continue;
					}
				</#list>

				throw new IllegalArgumentException("Invalid additional assert field name " + additionalAssertFieldName);
			}

			Assert.assertTrue(valid);
		}
	</#list>

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	<#list relatedSchemaNames as relatedSchemaName>
		protected String[] getAdditional${relatedSchemaName}AssertFieldNames() {
			return new String[0];
		}
	</#list>

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		<#if properties?keys?seq_contains("siteId")>
			graphQLFields.add(new GraphQLField("siteId"));
		</#if>

		<#if freeMarkerTool.getJavaDataType(configYAML, openAPIYAML, schemaName)??>
			for (java.lang.reflect.Field field : getDeclaredFields(${freeMarkerTool.getJavaDataType(configYAML, openAPIYAML, schemaName)}.class)) {
				if (!ArrayUtil.contains(getAdditionalAssertFieldNames(), field.getName())){
					continue;
				}

				graphQLFields.addAll(getGraphQLFields(field));
			}
		</#if>

		return graphQLFields;
	}

	protected List<GraphQLField> getGraphQLFields(java.lang.reflect.Field... fields) throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (java.lang.reflect.Field field : fields) {
			com.liferay.portal.vulcan.graphql.annotation.GraphQLField vulcanGraphQLField = field.getAnnotation(com.liferay.portal.vulcan.graphql.annotation.GraphQLField.class);

			if (vulcanGraphQLField != null) {
				Class<?> clazz = field.getType();

				if (clazz.isArray()) {
					clazz = clazz.getComponentType();
				}

				List<GraphQLField> childrenGraphQLFields = getGraphQLFields(getDeclaredFields(clazz));

				graphQLFields.add(new GraphQLField(field.getName(), childrenGraphQLFields));
			}
		}

		return graphQLFields;
	}

	protected String[] getIgnoredEntityFieldNames() {
		return new String[0];
	}

	protected boolean equals(${schemaClientJavaType} ${schemaVarName}1, ${schemaClientJavaType} ${schemaVarName}2) {
		if (${schemaVarName}1 == ${schemaVarName}2) {
			return true;
		}

		<#if !properties?keys?seq_contains("assetLibraryKey") && properties?keys?seq_contains("siteId")>
			if (!Objects.equals(${schemaVarName}1.getSiteId(), ${schemaVarName}2.getSiteId())) {
				return false;
			}
		</#if>

		for (String additionalAssertFieldName : getAdditionalAssertFieldNames()) {
			<#list properties?keys as propertyName>
				<#if stringUtil.equals(propertyName, "assetLibraryKey") || stringUtil.equals(propertyName, "siteId")>
					 <#continue>
				</#if>

				if (Objects.equals("${propertyName}", additionalAssertFieldName)) {
					<#assign capitalizedPropertyName = propertyName?cap_first />

					<#if enumSchemas?keys?seq_contains(properties[propertyName])>
						<#assign capitalizedPropertyName = properties[propertyName] />
					</#if>

					<#if stringUtil.startsWith(properties[propertyName], "Map<")>
						if (!equals((Map)${schemaVarName}1.get${capitalizedPropertyName}(), (Map)${schemaVarName}2.get${capitalizedPropertyName}())) {
							return false;
						}
					<#else>
						if (!Objects.deepEquals(${schemaVarName}1.get${capitalizedPropertyName}(), ${schemaVarName}2.get${capitalizedPropertyName}())) {
							return false;
						}
					</#if>

					continue;
				}
			</#list>

			<#if schema.discriminator?has_content>
				<#list schema.discriminator.mapping as mappingName, mappingSchema>
					<#assign childSchemaProperties = freeMarkerTool.getDTOProperties(configYAML, openAPIYAML, allSchemas[freeMarkerTool.getReferenceName(mappingSchema)], allSchemas) />

					<#if childSchemaProperties?has_content>
						<#list childSchemaProperties?keys as propertyName>
							if (Objects.equals("${propertyName}", additionalAssertFieldName)) {
								if (!(${schemaVarName}1 instanceof ${mappingName}) || !(${schemaVarName}2 instanceof ${mappingName})) {
									continue;
								}

								<#assign capitalizedPropertyName = propertyName?cap_first />

								<#if enumSchemas?keys?seq_contains(childSchemaProperties[propertyName])>
									<#assign capitalizedPropertyName = childSchemaProperties[propertyName] />
								</#if>

								if (!Objects.deepEquals(((${mappingName})${schemaVarName}1).get${capitalizedPropertyName}(), ((${mappingName})${schemaVarName}2).get${capitalizedPropertyName}())) {
									return false;
								}

								continue;
							}
						</#list>
					</#if>
				</#list>
			</#if>

			throw new IllegalArgumentException("Invalid additional assert field name " + additionalAssertFieldName);
		}

		return true;
	}

	protected boolean equals(Map<String, Object> map1, Map<String, Object> map2) {
		if (Objects.equals(map1.keySet(), map2.keySet())) {
			for (Map.Entry<String, Object> entry : map1.entrySet()) {
				if (entry.getValue() instanceof Map) {
					if (!equals((Map)entry.getValue(), (Map)map2.get(entry.getKey()))) {
						return false;
					}
				}
				else if (!Objects.deepEquals(entry.getValue(), map2.get(entry.getKey()))){
					return false;
				}
			}

			return true;
		}

		return false;
	}

	<#list relatedSchemaNames as relatedSchemaName>
		<#assign
			relatedSchemaProperties = freeMarkerTool.getDTOProperties(configYAML, openAPIYAML, relatedSchemaName, allSchemas)
			relatedSchemaVarName = freeMarkerTool.getSchemaVarName(relatedSchemaName)
		/>

		protected boolean equals(${relatedSchemaName} ${relatedSchemaVarName}1, ${relatedSchemaName} ${relatedSchemaVarName}2) {
			if (${relatedSchemaVarName}1 == ${relatedSchemaVarName}2) {
				return true;
			}

			for (String additionalAssertFieldName : getAdditional${relatedSchemaName}AssertFieldNames()) {
				<#list relatedSchemaProperties?keys as propertyName>
					if (Objects.equals("${propertyName}", additionalAssertFieldName)) {
						<#assign capitalizedPropertyName = propertyName?cap_first />

						<#if enumSchemas?keys?seq_contains(relatedSchemaProperties[propertyName])>
							<#assign capitalizedPropertyName = relatedSchemaProperties[propertyName] />
						</#if>

						if (!Objects.deepEquals(${relatedSchemaVarName}1.get${capitalizedPropertyName}(), ${relatedSchemaVarName}2.get${capitalizedPropertyName}())) {
							return false;
						}

						continue;
					}
				</#list>

				throw new IllegalArgumentException("Invalid additional assert field name " + additionalAssertFieldName);
			}

			return true;
		}
	</#list>

	protected java.lang.reflect.Field[] getDeclaredFields(Class clazz) throws Exception {
		if (clazz.getClassLoader() == null) {
			return new java.lang.reflect.Field[0];
		}

		return TransformUtil.transform(
			ReflectionUtil.getDeclaredFields(clazz),
			field -> {
				if (field.isSynthetic()) {
					return null;
				}

				return field;
			},
			java.lang.reflect.Field.class);
	}

	protected java.util.Collection<EntityField> getEntityFields() throws Exception {
		if (!(_${schemaVarName}Resource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException("Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource = (EntityModelResource)_${schemaVarName}Resource;

		EntityModel entityModel = entityModelResource.getEntityModel(new MultivaluedHashMap());

		if (entityModel == null) {
			return Collections.emptyList();
		}

		Map<String, EntityField> entityFieldsMap = entityModel.getEntityFieldsMap();

		return entityFieldsMap.values();
	}

	protected List<EntityField> getEntityFields(EntityField.Type type) throws Exception {
		return TransformUtil.transform(
			getEntityFields(),
			entityField -> {
				if (!Objects.equals(entityField.getType(), type) ||
					ArrayUtil.contains(
					   getIgnoredEntityFieldNames(), entityField.getName())) {

					return null;
				}

				return entityField;
			});
	}

	protected String getFilterString(EntityField entityField, String operator, ${schemaClientJavaType} ${schemaVarName}) {
		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		<#list properties?keys as propertyName>
			if (entityFieldName.equals("${propertyName}")) {
				<#if stringUtil.equals(properties[propertyName], "Date")>
					if (operator.equals("between")) {
						Date date = ${schemaVarName}.get${propertyName?cap_first}();

						sb = new StringBundler();

						sb.append("(");
						sb.append(entityFieldName);
						sb.append(" gt ");
						sb.append(_format.format(date.getTime() - 2 * Time.SECOND));
						sb.append(" and ");
						sb.append(entityFieldName);
						sb.append(" lt ");
						sb.append(_format.format(date.getTime() + 2 * Time.SECOND));
						sb.append(")");
					}
					else {
						sb.append(entityFieldName);

						sb.append(" ");
						sb.append(operator);
						sb.append(" ");

						sb.append(_format.format(${schemaVarName}.get${propertyName?cap_first}()));
					}

					return sb.toString();
				<#elseif stringUtil.equals(properties[propertyName], "Double")>
					sb.append(String.valueOf(${schemaVarName}.get${propertyName?cap_first}()));

					return sb.toString();
				<#elseif stringUtil.equals(properties[propertyName], "Integer")>
					sb.append(String.valueOf(${schemaVarName}.get${propertyName?cap_first}()));

					return sb.toString();
				<#elseif stringUtil.equals(properties[propertyName], "String")>
					Object object = ${schemaVarName}.get${propertyName?cap_first}();

					String value = String.valueOf(object);

					if (operator.equals("contains")) {
						sb = new StringBundler();

						sb.append("contains(");
						sb.append(entityFieldName);
						sb.append(",'");

						if ((object != null) && (value.length() > 2)) {
							sb.append(value.substring(1, value.length() - 1));
						}
						else {
							sb.append(value);
						}

						sb.append("')");
					}
					else if (operator.equals("startswith")) {
						sb = new StringBundler();

						sb.append("startswith(");
						sb.append(entityFieldName);
						sb.append(",'");

						if ((object != null) && (value.length() > 1)) {
							sb.append(value.substring(0, value.length() - 1));
						}
						else {
							sb.append(value);
						}

						sb.append("')");
					}
					else {
						sb.append("'");
						sb.append(value);
						sb.append("'");
					}

					return sb.toString();
				<#else>
					throw new IllegalArgumentException("Invalid entity field " + entityFieldName);
				</#if>
			}
		</#list>

		throw new IllegalArgumentException("Invalid entity field " + entityFieldName);
	}

	<#if generateGetMultipartFilesMethod>
		protected Map<String, File> getMultipartFiles() throws Exception {
			throw new UnsupportedOperationException("This method needs to be implemented");
		}
	</#if>

	protected String invoke(String query) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(
			JSONUtil.put(
				"query", query
			).toString(),
			"application/json");
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
		httpInvoker.path("http://localhost:8080/o/graphql");
		httpInvoker.userNameAndPassword("test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	protected JSONObject invokeGraphQLMutation(GraphQLField graphQLField) throws Exception {
		GraphQLField mutationGraphQLField = new GraphQLField("mutation", graphQLField);

		return JSONFactoryUtil.createJSONObject(invoke(mutationGraphQLField.toString()));
	}

	protected JSONObject invokeGraphQLQuery(GraphQLField graphQLField) throws Exception {
		GraphQLField queryGraphQLField = new GraphQLField("query", graphQLField);

		return JSONFactoryUtil.createJSONObject(invoke(queryGraphQLField.toString()));
	}

	<#if javaDataTypeMap?keys?seq_contains(schemaName)>
		protected ${schemaName} random${schemaName}() throws Exception {
			<#if schema.discriminator?has_content>
				<#assign discriminatorPropertyName = schema.discriminator.propertyName />

				List<Supplier<${schemaName}>> suppliers = Arrays.asList(

				<#list schema.discriminator.mapping as mappingName, mappingSchema>
					<#assign
						childSchemaName = freeMarkerTool.getReferenceName(mappingSchema)

						childProperties = freeMarkerTool.getDTOProperties(configYAML, openAPIYAML, childSchemaName, allSchemas)
					/>

					() -> {
						${childSchemaName} ${schemaVarName} = new ${childSchemaName}();

						<#list properties?keys as propertyName>
							<#if stringUtil.equals(propertyName, "siteId")>
								${schemaVarName}.setGroupId(testGroup.getGroupId());
							<#elseif stringUtil.equals(properties[propertyName], "Integer")>
								${schemaVarName}.set${propertyName?cap_first}(RandomTestUtil.randomInt());
							<#elseif propertyName?contains("email") && stringUtil.equals(properties[propertyName], "String")>
								${schemaVarName}.set${propertyName?cap_first}(StringUtil.toLowerCase(RandomTestUtil.randomString()) + "@liferay.com");
							<#elseif stringUtil.equals(properties[propertyName], "String")>
								${schemaVarName}.set${propertyName?cap_first}(StringUtil.toLowerCase(RandomTestUtil.randomString()));
							<#elseif randomDataTypes?seq_contains(properties[propertyName])>
								${schemaVarName}.set${propertyName?cap_first}(RandomTestUtil.random${properties[propertyName]}());
							<#elseif stringUtil.equals(properties[propertyName], "Date")>
								${schemaVarName}.set${propertyName?cap_first}(RandomTestUtil.nextDate());
							</#if>
						</#list>

						<#list childProperties?keys as propertyName>
							<#if stringUtil.equals(propertyName, "siteId")>
								${schemaVarName}.setGroupId(testGroup.getGroupId());
							<#elseif stringUtil.equals(childProperties[propertyName], "Integer")>
								${schemaVarName}.set${propertyName?cap_first}(RandomTestUtil.randomInt());
							<#elseif propertyName?contains("email") && stringUtil.equals(childProperties[propertyName], "String")>
								${schemaVarName}.set${propertyName?cap_first}(StringUtil.toLowerCase(RandomTestUtil.randomString()) + "@liferay.com");
							<#elseif stringUtil.equals(childProperties[propertyName], "String")>
								${schemaVarName}.set${propertyName?cap_first}(StringUtil.toLowerCase(RandomTestUtil.randomString()));
							<#elseif randomDataTypes?seq_contains(childProperties[propertyName])>
								${schemaVarName}.set${propertyName?cap_first}(RandomTestUtil.random${childProperties[propertyName]}());
							<#elseif stringUtil.equals(childProperties[propertyName], "Date")>
								${schemaVarName}.set${propertyName?cap_first}(RandomTestUtil.nextDate());
							</#if>
						</#list>

						${schemaVarName}.set${discriminatorPropertyName?cap_first}(${schemaName}.${discriminatorPropertyName?cap_first}.create("${mappingName}"));

						return ${schemaVarName};
					}

					<#if mappingName?has_next>
						,
					</#if>
				</#list>

				);

				Supplier<${schemaName}> supplier = suppliers.get(RandomTestUtil.randomInt(0, suppliers.size() - 1));

				return supplier.get();
			<#else>
				return new ${schemaName}() {
					{
						<#list properties?keys as propertyName>
							<#if stringUtil.equals(propertyName, "assetLibraryExternalReferenceCode")>
								${propertyName} = testDepotEntryGroup.getExternalReferenceCode();
							<#elseif stringUtil.equals(propertyName, "assetLibraryId")>
								${propertyName} = testDepotEntry.getDepotEntryId();
							<#elseif stringUtil.equals(propertyName, "siteExternalReferenceCode")>
								${propertyName} = testGroup.getExternalReferenceCode();
							<#elseif stringUtil.equals(propertyName, "siteId")>
								${propertyName} = testGroup.getGroupId();
							<#elseif stringUtil.equals(properties[propertyName], "Integer")>
								${propertyName} = RandomTestUtil.randomInt();
							<#elseif propertyName?contains("email") && stringUtil.equals(properties[propertyName], "String")>
								${propertyName} = StringUtil.toLowerCase(RandomTestUtil.randomString()) + "@liferay.com";
							<#elseif stringUtil.equals(properties[propertyName], "String")>
								${propertyName} = StringUtil.toLowerCase(RandomTestUtil.randomString());
							<#elseif randomDataTypes?seq_contains(properties[propertyName])>
								${propertyName} = RandomTestUtil.random${properties[propertyName]}();
							<#elseif stringUtil.equals(properties[propertyName], "Date")>
								${propertyName} = RandomTestUtil.nextDate();
							</#if>
						</#list>
					}
				};
			</#if>
		}

		protected ${schemaName} randomIrrelevant${schemaName}() throws Exception {
			${schemaName} randomIrrelevant${schemaName} = random${schemaName}();

			<#if properties?keys?seq_contains("assetLibraryExternalReferenceCode")>
			   randomIrrelevant${schemaName}.setAssetLibraryExternalReferenceCode(irrelevantDepotEntryGroup.getExternalReferenceCode());
			</#if>

			<#if properties?keys?seq_contains("assetLibraryId")>
			   randomIrrelevant${schemaName}.setAssetLibraryId(irrelevantDepotEntry.getGroupId());
			</#if>

			<#if properties?keys?seq_contains("siteExternalReferenceCode")>
			   randomIrrelevant${schemaName}.setSiteExternalReferenceCode(irrelevantGroup.getExternalReferenceCode());
			</#if>

			<#if properties?keys?seq_contains("siteId")>
				randomIrrelevant${schemaName}.setSiteId(irrelevantGroup.getGroupId());
			</#if>

			return randomIrrelevant${schemaName};
		}

		protected ${schemaName} randomPatch${schemaName}() throws Exception {
			return random${schemaName}();
		}

		<#if (generatePermissionsJavaMethodSignatures?size > 0)>
			protected ${schemaName} randomPermissions${schemaName}() throws Exception {
				${schemaName} ${schemaVarName} = random${schemaName}();

				com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

				${schemaVarName}.setPermissions(
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"VIEW"});
								setRoleName(role.getName());
							}
						}
					});

				return ${schemaVarName};
			}
		</#if>
	</#if>

	<#list relatedSchemaNames as relatedSchemaName>
		protected ${relatedSchemaName} random${relatedSchemaName}() throws Exception {
			return new ${relatedSchemaName}() {
				{
					<#assign relatedSchemaProperties = freeMarkerTool.getDTOProperties(configYAML, openAPIYAML, relatedSchemaName, allSchemas) />

					<#list relatedSchemaProperties?keys as propertyName>
						<#if randomDataTypes?seq_contains(relatedSchemaProperties[propertyName])>
							${propertyName} = RandomTestUtil.random${relatedSchemaProperties[propertyName]}();
						<#elseif stringUtil.equals(relatedSchemaProperties[propertyName], "Date")>
							${propertyName} = RandomTestUtil.nextDate();
						</#if>
					</#list>
				}
			};
		}
	</#list>

	<#if generateWaitForFinishMethod>
		protected final JSONObject waitForFinish(String expectedExecuteStatus, JSONObject jsonObject) throws Exception {
			while (true) {
				ImportTask importTask = importTaskResource.getImportTask(jsonObject.getLong("id"));

				ImportTask.ExecuteStatus executeStatus = importTask.getExecuteStatus();

				if (StringUtil.equals(executeStatus.getValue(), "COMPLETED") ||
					StringUtil.equals(executeStatus.getValue(), "FAILED")) {

					Assert.assertEquals(expectedExecuteStatus, executeStatus.getValue());

					return jsonObject;
				}
			}
		}
	</#if>

	protected ${schemaName}Resource ${schemaVarName}Resource;

	<#if generateWaitForFinishMethod>
		protected ImportTaskResource importTaskResource;
	</#if>

	protected com.liferay.portal.kernel.model.Group irrelevantGroup;

	<#if (generatePermissionsJavaMethodSignatures?size > 0)>
		protected ${schemaName}Resource permissions${schemaName}Resource;
	</#if>

	protected com.liferay.portal.kernel.model.Company testCompany;

	<#if generateDepotEntry>
		protected DepotEntry irrelevantDepotEntry;
		protected com.liferay.portal.kernel.model.Group irrelevantDepotEntryGroup;
		protected DepotEntry testDepotEntry;
		protected com.liferay.portal.kernel.model.Group testDepotEntryGroup;
	</#if>

	protected com.liferay.portal.kernel.model.Group testGroup;

	protected static class BeanTestUtil {

		public static void copyProperties(Object source, Object target) throws Exception {
			Class<?> sourceClass = source.getClass();

			Class<?> targetClass = target.getClass();

			for (java.lang.reflect.Field field : _getAllDeclaredFields(sourceClass)) {
				if (field.isSynthetic()) {
					continue;
				}

				Method getMethod = _getMethod(sourceClass, field.getName(), "get");

				try {
					Method setMethod = _getMethod(targetClass, field.getName(), "set", getMethod.getReturnType());

					setMethod.invoke(target, getMethod.invoke(source));
				}
				catch (Exception e) {
					continue;
				}
			}
		}

		public static boolean hasProperty(Object bean, String name) {
			Method setMethod = _getMethod(bean.getClass(), "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod != null) {
				return true;
			}

			return false;
		}

		public static void setProperty(Object bean, String name, Object value) throws Exception {
			Class<?> clazz = bean.getClass();

			Method setMethod = _getMethod(clazz, "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod == null) {
				throw new NoSuchMethodException();
			}

			Class<?>[] parameterTypes = setMethod.getParameterTypes();

			setMethod.invoke(bean, _translateValue(parameterTypes[0], value));
		}

		private static List<java.lang.reflect.Field> _getAllDeclaredFields(Class<?> clazz) {
			List<java.lang.reflect.Field> fields = new ArrayList<>();

			while ((clazz != null) && (clazz != Object.class)) {
				for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
					fields.add(field);
				}

				clazz = clazz.getSuperclass();
			}

			return fields;
		}

		private static Method _getMethod(Class<?> clazz, String name) {
			for (Method method : clazz.getMethods()) {
				if (name.equals(method.getName()) &&
					(method.getParameterCount() == 1) &&
					_parameterTypes.contains(method.getParameterTypes()[0])) {

					return method;
				}
			}

			return null;
		}

		private static Method _getMethod(Class<?> clazz, String fieldName, String prefix, Class<?>... parameterTypes) throws Exception {
			return clazz.getMethod(prefix + StringUtil.upperCaseFirstLetter(fieldName), parameterTypes);
		}

		private static Object _translateValue(Class<?> parameterType, Object value) {
			if ((value instanceof Integer) && parameterType.equals(Long.class)) {
				Integer intValue = (Integer)value;

				return intValue.longValue();
			}

			return value;
		}

		private static final Set<Class<?>> _parameterTypes = new HashSet<>(Arrays.asList(Boolean.class, Date.class, Double.class, Integer.class, Long.class, Map.class, String.class));

	}

	protected class GraphQLField {

		public GraphQLField(String key, GraphQLField... graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(String key, List<GraphQLField> graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(String key, Map<String, Object> parameterMap, GraphQLField... graphQLFields) {
			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = Arrays.asList(graphQLFields);
		}

		public GraphQLField(String key, Map<String, Object> parameterMap, List<GraphQLField> graphQLFields) {
			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = graphQLFields;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(_key);

			if (!_parameterMap.isEmpty()) {
				sb.append("(");

				for (Map.Entry<String, Object> entry :
					_parameterMap.entrySet()) {

					sb.append(entry.getKey());
					sb.append(": ");
					sb.append(entry.getValue());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append(")");
			}

			if (!_graphQLFields.isEmpty()) {
				sb.append("{");

				for (GraphQLField graphQLField : _graphQLFields) {
					sb.append(graphQLField.toString());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append("}");
			}

			return sb.toString();
		}

		private final List<GraphQLField> _graphQLFields;
		private final String _key;
		private final Map<String, Object> _parameterMap;

	}

	private static final com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(Base${schemaName}ResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private ${configYAML.apiPackagePath}.resource.${escapedVersion}.${schemaName}Resource _${schemaVarName}Resource;

	<#if generateCRUD>
		@Inject
		private GroupLocalService _groupLocalService;

		@Inject
		private ResourceActionLocalService _resourceActionLocalService;

		@Inject
		private ResourcePermissionLocalService _resourcePermissionLocalService;

		@Inject
		private RoleLocalService _roleLocalService;

		@Inject
		private ScopeChecker _scopeChecker;

		@Inject
		private UserLocalService _userLocalService;

		@Inject
		private VulcanCRUDItemDelegateBuilderRegistry _vulcanCRUDItemDelegateBuilderRegistry;
	</#if>

}

<#macro getDefaultParameter
	javaMethodParameter
>
	<#if stringUtil.equals(javaMethodParameter.parameterType, "java.lang.Double")>
		0D
	<#elseif stringUtil.equals(javaMethodParameter.parameterType, "java.lang.Integer")>
		0
	<#elseif stringUtil.equals(javaMethodParameter.parameterType, "java.lang.Long")>
		0L
	<#elseif stringUtil.equals(javaMethodParameter.parameterType, "java.lang.String")>
		"-"
	<#else>
		null
	</#if>
</#macro>

<#macro getGetParameters
	javaMethodSignature
	testJavaMethodName
	varName
	allowBodyParameters = false
	allowQueryParameter = true
	defaultParameter = false
	skipQueryParameter = false
>
	<#assign printed = false />

	<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
		<#if stringUtil.equals(javaMethodParameter.parameterName, "pagination")>
			<#if printed>,</#if>

			<#assign printed = true />

			Pagination.of(1, 2)
		<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation) || (freeMarkerTool.isQueryParameter(javaMethodParameter, javaMethodSignature.operation) && !skipQueryParameter)>
			<#if printed>,</#if>

			<#assign printed = true />

			<#if freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation) || (freeMarkerTool.isQueryParameter(javaMethodParameter, javaMethodSignature.operation) && allowQueryParameter)>
				<#if freeMarkerTool.isExternalReferenceCodeParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName) && properties?keys?seq_contains("externalReferenceCode")>
					<#if defaultParameter>
						<@getDefaultParameter javaMethodParameter = javaMethodParameter />
					<#else>
						${varName}.getExternalReferenceCode()
					</#if>
				<#elseif freeMarkerTool.isIdParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName) && (properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id"))>
					<#if defaultParameter>
						<@getDefaultParameter javaMethodParameter = javaMethodParameter />
					<#else>
						${varName}.${getIdMethodName}()
					</#if>
				<#elseif properties?keys?seq_contains(javaMethodParameter.parameterName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
					${varName}.get${javaMethodParameter.parameterName?cap_first}()
				<#else>
					<#assign getterJavaMethodParametersMap = getterJavaMethodParametersMap + {javaMethodParameter.parameterName: javaMethodParameter} />

					test${testJavaMethodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}(
						<#if properties?keys?seq_contains(javaMethodParameter.parameterName)>
							${varName}
						</#if>
					)
				</#if>
			<#else>
				null
			</#if>
		<#elseif !freeMarkerTool.isQueryParameter(javaMethodParameter, javaMethodSignature.operation) && allowBodyParameters>
			<#if printed>,</#if>

			<#assign printed = true />

			null
		</#if>
	</#list>
</#macro>

<#macro getPermissionParameter
	javaMethodParameter
	javaMethodSignature
	roleName
	schemaVarNameId
>
	<#if freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation) && stringUtil.equals(javaMethodParameter.parameterName, schemaVarName + "Id")>
		${schemaVarNameId}
	<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation) && properties?keys?seq_contains(javaMethodParameter.parameterName)>
		${schemaVarName}.get${javaMethodParameter.parameterName?cap_first}()
	<#elseif stringUtil.equals(javaMethodParameter.parameterName, "assetLibraryExternalReferenceCode")>
		testDepotEntryGroup.getExternalReferenceCode()
	<#elseif stringUtil.equals(javaMethodParameter.parameterName, "assetLibraryId")>
		testDepotEntry.getDepotEntryId()
	<#elseif stringUtil.equals(javaMethodParameter.parameterName, "siteExternalReferenceCode")>
		testGroup.getExternalReferenceCode()
	<#elseif stringUtil.equals(javaMethodParameter.parameterName, "siteId")>
		testGroup.getGroupId()
	<#elseif stringUtil.equals(javaMethodParameter.parameterName, schemaVarName)>
		${schemaVarName}
	<#elseif stringUtil.equals(javaMethodParameter.parameterType, "[Lcom.liferay.portal.vulcan.permission.Permission;")>
		new Permission[] {
			new Permission() {
				{
					setActionIds(new String[] {
						<#nested>
					});
					setRoleName(${roleName});
				}
			}
		}
	<#else>
		null
	</#if>
</#macro>

<#macro getPutParameters
	hasMultipartFiles
	javaMethodSignature
	newVarName
	varName
>
	<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
		<#if freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation) && freeMarkerTool.isIdParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
			${varName}.${getIdMethodName}()
		<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation) && freeMarkerTool.isExternalReferenceCodeParameter(javaMethodParameter, schemaName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
			${varName}.getExternalReferenceCode()
		<#elseif freeMarkerTool.isPathParameter(javaMethodParameter, javaMethodSignature.operation) && properties?keys?seq_contains(javaMethodParameter.parameterName) && freeMarkerTool.isParameterNameSchemaRelated(javaMethodParameter.parameterName, javaMethodSignature.path, schemaName)>
			${varName}.get${javaMethodParameter.parameterName?cap_first}()
		<#elseif stringUtil.equals(javaMethodParameter.parameterName, "multipartBody") || stringUtil.equals(javaMethodParameter.parameterName, schemaVarName)>
			${newVarName}
		<#else>
			<#assign getterJavaMethodParametersMap = getterJavaMethodParametersMap + {javaMethodParameter.parameterName: javaMethodParameter} />

			test${javaMethodSignature.methodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}(
				<#if properties?keys?seq_contains(javaMethodParameter.parameterName)>
					${varName}
				</#if>
			)
		</#if>

		<#sep>, </#sep>
	</#list>

	<#if freeMarkerTool.hasRequestBodyMediaType(javaMethodSignature, "multipart/form-data") && !hasMultipartFiles>
		, getMultipartFiles()
	<#elseif freeMarkerTool.hasRequestBodyMediaType(javaMethodSignature, "multipart/form-data") && hasMultipartFiles>
		, multipartFiles
	</#if>
</#macro>

<#macro getQuotedString
	unquotedString
>
	"\"" + ${unquotedString} + "\""
</#macro>

<#macro getTestAdderMethod
	javaMethodSignature
>
	<#if properties?keys?seq_contains("externalReferenceCode") || properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id")>
		protected ${schemaName} test${javaMethodSignature.methodName?cap_first}_add${schemaName}() throws Exception {
			<#assign postSchemaJavaMethodSignature = "" />

			<#if javaMethodSignature.methodName?contains("AssetLibrary")>
				<#if freeMarkerTool.hasPostSchemaJavaMethodSignature(javaMethodSignatures, "assetLibraryExternalReferenceCode", schemaName)>
					<#assign postSchemaJavaMethodSignature = freeMarkerTool.getPostSchemaJavaMethodSignature(javaMethodSignatures, "assetLibraryExternalReferenceCode", schemaName) />

					return ${schemaVarName}Resource.${postSchemaJavaMethodSignature.methodName}(testDepotEntryGroup.getExternalReferenceCode(), random${schemaName}()

					<#if freeMarkerTool.hasRequestBodyMediaType(postSchemaJavaMethodSignature, "multipart/form-data")>
						<#assign generateGetMultipartFilesMethod = true />

						, getMultipartFiles()
					</#if>

					);
				<#elseif freeMarkerTool.hasPostSchemaJavaMethodSignature(javaMethodSignatures, "assetLibraryId", schemaName)>
					<#assign postSchemaJavaMethodSignature = freeMarkerTool.getPostSchemaJavaMethodSignature(javaMethodSignatures, "assetLibraryId", schemaName) />

					return ${schemaVarName}Resource.${postSchemaJavaMethodSignature.methodName}(testDepotEntry.getDepotEntryId(), random${schemaName}()

					<#if freeMarkerTool.hasRequestBodyMediaType(postSchemaJavaMethodSignature, "multipart/form-data")>
						<#assign generateGetMultipartFilesMethod = true />

						, getMultipartFiles()
					</#if>

					);
				</#if>
			<#elseif freeMarkerTool.hasPostSchemaJavaMethodSignature(javaMethodSignatures, "siteExternalReferenceCode", schemaName)>
				<#assign postSchemaJavaMethodSignature = freeMarkerTool.getPostSchemaJavaMethodSignature(javaMethodSignatures, "siteExternalReferenceCode", schemaName) />

				return ${schemaVarName}Resource.${postSchemaJavaMethodSignature.methodName}(testGroup.getExternalReferenceCode(), random${schemaName}()

				<#if freeMarkerTool.hasRequestBodyMediaType(postSchemaJavaMethodSignature, "multipart/form-data")>
					<#assign generateGetMultipartFilesMethod = true />

					, getMultipartFiles()
				</#if>

				);
			<#elseif freeMarkerTool.hasPostSchemaJavaMethodSignature(javaMethodSignatures, "siteId", schemaName)>
				<#assign postSchemaJavaMethodSignature = freeMarkerTool.getPostSchemaJavaMethodSignature(javaMethodSignatures, "siteId", schemaName) />

				return ${schemaVarName}Resource.${postSchemaJavaMethodSignature.methodName}(testGroup.getGroupId(), random${schemaName}()

				<#if freeMarkerTool.hasRequestBodyMediaType(postSchemaJavaMethodSignature, "multipart/form-data")>
					<#assign generateGetMultipartFilesMethod = true />

					, getMultipartFiles()
				</#if>

				);
			<#elseif (javaMethodSignature.javaMethodParameters?size > 0)>
				<#assign firstParameterName = javaMethodSignature.javaMethodParameters[0].parameterName />

				<#if freeMarkerTool.hasPostSchemaJavaMethodSignature(javaMethodSignatures, firstParameterName, schemaName)>
					<#assign postSchemaJavaMethodSignature = freeMarkerTool.getPostSchemaJavaMethodSignature(javaMethodSignatures, firstParameterName, schemaName) />
				<#elseif (firstParameterName?? && firstParameterName?has_content) && freeMarkerTool.hasPostSchemaJavaMethodSignature(javaMethodSignatures, "parent" + firstParameterName?cap_first, schemaName)>
					<#assign postSchemaJavaMethodSignature = freeMarkerTool.getPostSchemaJavaMethodSignature(javaMethodSignatures, "parent" + firstParameterName?cap_first, schemaName) />
				</#if>

				<#if postSchemaJavaMethodSignature?has_content>
					return test${postSchemaJavaMethodSignature.methodName?cap_first}_add${schemaName}(random${schemaName}()

					<#if freeMarkerTool.hasRequestBodyMediaType(postSchemaJavaMethodSignature, "multipart/form-data")>
						<#assign generateGetMultipartFilesMethod = true />

						, getMultipartFiles()
					</#if>

					);
				</#if>
			</#if>

			<#if !postSchemaJavaMethodSignature?has_content>
				throw new UnsupportedOperationException("This method needs to be implemented");
			</#if>
		}
	</#if>
</#macro>

<#macro getTestGetterMethods
	getterJavaMethodParametersMap
	testJavaMethodName
	testNamePrefix = "test"
>
	<#list getterJavaMethodParametersMap?values as javaMethodParameter>
		protected ${javaMethodParameter.parameterType} ${testNamePrefix}${testJavaMethodName?cap_first}_get${javaMethodParameter.parameterName?cap_first}(
			<#if properties?keys?seq_contains(javaMethodParameter.parameterName)>
				${schemaName} ${schemaVarName}
			</#if>
		) throws Exception {
			throw new UnsupportedOperationException("This method needs to be implemented");
		}
	</#list>
</#macro>