/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.constants.FieldMappingConstants;
import com.liferay.osb.faro.engine.client.model.DataSource;
import com.liferay.osb.faro.engine.client.model.DataSourceField;
import com.liferay.osb.faro.engine.client.model.DataSourceFieldCatalogEntry;
import com.liferay.osb.faro.engine.client.model.Field;
import com.liferay.osb.faro.engine.client.model.FieldMapping;
import com.liferay.osb.faro.engine.client.model.Provider;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.model.provider.SalesforceProvider;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.osb.faro.web.internal.helper.ContactsCSVHelper;
import com.liferay.osb.faro.web.internal.model.display.contacts.DataSourceMappingDisplay;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Inácio Nery
 */
public class DataSourceFaroControllerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_contactsCSVHelper.getDataSourceFields(
				Mockito.eq(32783L), Mockito.isNull(), Mockito.eq(1),
				Mockito.eq(true))
		).thenReturn(
			Arrays.asList(
				new DataSourceField("City", Arrays.asList("Mountain View")),
				new DataSourceField("Country", Arrays.asList("United States")),
				new DataSourceField("Department", Arrays.asList("Legal")),
				new DataSourceField("Division", Arrays.asList("Operations")),
				new DataSourceField(
					"Email Address", Arrays.asList("jane.doe@liferay.com")),
				new DataSourceField(
					"Employee Name", Arrays.asList("Jane, Doe")),
				new DataSourceField(
					"Employment Status", Arrays.asList("Active")),
				new DataSourceField("Entity", Arrays.asList("Liferay")),
				new DataSourceField("Job", Arrays.asList("Sr Counsel")),
				new DataSourceField("Region", Arrays.asList("LATAM")),
				new DataSourceField(
					"Sub-Department", Arrays.asList("Not Applicable")),
				new DataSourceField(
					"Supervisor Email", Arrays.asList("john.doe@liferay.com")),
				new DataSourceField(
					"Supervisor Name", Arrays.asList("John, Doe")))
		);

		ReflectionTestUtils.setField(
			_dataSourceFaroController, "_contactsCSVHelper",
			_contactsCSVHelper);

		Mockito.when(
			_contactsEngineClient.getFieldMappings(
				Mockito.eq(_faroProject),
				Mockito.eq(FieldMappingConstants.CONTEXT_DEMOGRAPHICS),
				Mockito.anyList(), Mockito.eq(1), Mockito.eq(10000),
				Mockito.isNull())
		).thenReturn(
			new Results<FieldMapping>(
				Arrays.asList(
					_createFieldMapping("city", "City"),
					_createFieldMapping("country", "Country"),
					_createFieldMapping("department", "Department"),
					_createFieldMapping("division", "Division"),
					_createFieldMapping(
						"employmentStatus", "Employment Status"),
					_createFieldMapping("liferayEntity", "Entity"),
					_createFieldMapping("region", "Region"),
					_createFieldMapping("subDepartment", "Sub-Department"),
					_createFieldMapping(
						"email", "emailAddress", "Employee Email"),
					_createFieldMapping("givenName", "firstName"),
					_createFieldMapping("jobTitle", "jobTitle", "Job")),
				11)
		);

		Mockito.when(
			_contactsEngineClient.getFieldMappings(
				Mockito.eq(_faroProject),
				Mockito.eq(FieldMappingConstants.CONTEXT_DEMOGRAPHICS),
				Mockito.isNull(), Mockito.isNull())
		).thenReturn(
			new Results<FieldMapping>()
		);

		Mockito.when(
			_contactsEngineClient.getFieldNamesList(
				Mockito.eq(_faroProject), Mockito.anyList(),
				Mockito.eq(FieldMappingConstants.OWNER_TYPE_INDIVIDUAL),
				Mockito.anyList())
		).thenReturn(
			Arrays.asList(
				Arrays.asList("city"), Arrays.asList("country"),
				Arrays.asList("department"),
				Arrays.asList("department", "subDepartment"),
				Arrays.asList("division"), Arrays.asList("email"),
				Arrays.asList("email"), Arrays.asList("employmentStatus"),
				Arrays.asList("familyName", "givenName"),
				Arrays.asList("familyName", "givenName"),
				Arrays.asList("jobTitle"), Arrays.asList("liferayEntity"),
				Arrays.asList("Region", "region"))
		);

		Mockito.when(
			_contactsEngineClient.getFieldsList(
				Mockito.eq(_faroProject),
				Mockito.eq(FieldMappingConstants.CONTEXT_DEMOGRAPHICS),
				Mockito.anyList(), Mockito.eq(1), Mockito.eq(10000),
				Mockito.isNull())
		).thenReturn(
			Arrays.asList(
				Arrays.asList(
					_createField("city", "City", "Mountain View"),
					_createField("country", "Country", "United States"),
					_createField("department", "Department", "Legal"),
					_createField("division", "Division", "Operations"),
					_createField(
						"email", "emailAddress", "jane.doe@liferay.com"),
					_createField(
						"employmentStatus", "Employment Status", "Active"),
					_createField("familyName", "lastName", "Doe"),
					_createField("givenName", "firstName", "John"),
					_createField("jobTitle", "jobTitle", "Sr Counsel"),
					_createField("liferayEntity", "Entity", "Liferay"),
					_createField("region", "Region", "LATAM"),
					_createField("region", "Region", "LATAM"),
					_createField(
						"subDepartment", "Sub-Department", "Not Applicable")))
		);

		ReflectionTestUtils.setField(
			_dataSourceFaroController, "contactsEngineClient",
			_contactsEngineClient);

		Mockito.when(
			_faroProjectLocalService.getFaroProjectByGroupId(Mockito.anyLong())
		).thenReturn(
			_faroProject
		);

		ReflectionTestUtils.setField(
			_dataSourceFaroController, "faroProjectLocalService",
			_faroProjectLocalService);
	}

	@Test
	public void testCreateTypeSalesforce() throws Exception {
		DataSource dataSource = Mockito.mock(DataSource.class);

		Mockito.when(
			dataSource.getProvider()
		).thenReturn(
			new SalesforceProvider()
		);

		Mockito.when(
			_contactsEngineClient.addDataSource(
				Mockito.eq(_faroProject), Mockito.isNull(), Mockito.anyLong(),
				Mockito.eq("Salesforce"), Mockito.isNull(),
				Mockito.any(Provider.class), Mockito.isNull(),
				Mockito.eq("ACTIVE"))
		).thenReturn(
			dataSource
		);

		SalesforceProvider.CampaignsConfiguration campaignsConfiguration =
			new SalesforceProvider.CampaignsConfiguration();

		campaignsConfiguration.setEnableAllCampaigns(true);

		SalesforceProvider.OpportunitiesConfiguration
			opportunitiesConfiguration =
				new SalesforceProvider.OpportunitiesConfiguration();

		opportunitiesConfiguration.setEnableAllOpportunities(true);

		try (MockedStatic<PermissionThreadLocal>
				permissionThreadLocalMockedStatic = Mockito.mockStatic(
					PermissionThreadLocal.class)) {

			permissionThreadLocalMockedStatic.when(
				PermissionThreadLocal::getPermissionChecker
			).thenReturn(
				Mockito.mock(PermissionChecker.class)
			);

			_dataSourceFaroController.createTypeSalesforce(
				32719L, new FaroParam<>(),
				new FaroParam<>(campaignsConfiguration), new FaroParam<>(),
				new FaroParam<>(), null, "Salesforce",
				new FaroParam<>(opportunitiesConfiguration), "ACTIVE", null);
		}

		ArgumentCaptor<Provider> providerArgumentCaptor =
			ArgumentCaptor.forClass(Provider.class);

		Mockito.verify(
			_contactsEngineClient
		).addDataSource(
			Mockito.eq(_faroProject), Mockito.isNull(), Mockito.anyLong(),
			Mockito.eq("Salesforce"), Mockito.isNull(),
			providerArgumentCaptor.capture(), Mockito.isNull(),
			Mockito.eq("ACTIVE")
		);

		SalesforceProvider salesforceProvider =
			(SalesforceProvider)providerArgumentCaptor.getValue();

		campaignsConfiguration = salesforceProvider.getCampaignsConfiguration();

		Assert.assertTrue(campaignsConfiguration.isEnableAllCampaigns());

		opportunitiesConfiguration =
			salesforceProvider.getOpportunitiesConfiguration();

		Assert.assertTrue(
			opportunitiesConfiguration.isEnableAllOpportunities());
	}

	@Test
	public void testDiscoverFieldCatalog() throws Exception {
		Map<String, List<DataSourceFieldCatalogEntry>>
			dataSourceFieldCatalogEntries =
				HashMapBuilder.<String, List<DataSourceFieldCatalogEntry>>put(
					"ACCOUNT",
					Arrays.asList(
						new DataSourceFieldCatalogEntry(
							"Name", true, true, "string"))
				).build();

		Mockito.when(
			_contactsEngineClient.discoverDataSourceFieldCatalog(
				Mockito.eq(_faroProject), Mockito.any(DataSource.class))
		).thenReturn(
			dataSourceFieldCatalogEntries
		);

		Assert.assertSame(
			dataSourceFieldCatalogEntries,
			_dataSourceFaroController.discoverFieldCatalog(
				32719L, null, "https://test.my.salesforce.com/"));

		ArgumentCaptor<DataSource> argumentCaptor = ArgumentCaptor.forClass(
			DataSource.class);

		Mockito.verify(
			_contactsEngineClient
		).discoverDataSourceFieldCatalog(
			Mockito.eq(_faroProject), argumentCaptor.capture()
		);

		DataSource dataSource = argumentCaptor.getValue();

		Assert.assertTrue(
			dataSource.getProvider() instanceof SalesforceProvider);
		Assert.assertEquals(
			"https://test.my.salesforce.com", dataSource.getUrl());
		Assert.assertNull(dataSource.getCredentials());
	}

	@Test
	public void testGenerateDataSourceAccessToken() throws Exception {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		ReflectionTestUtil.setFieldValue(
			FaroPropsValues.class, "FARO_URL", "https://faro.test");

		String dataSourceAccessToken =
			_dataSourceFaroController.generateDataSourceAccessToken(
				12345L, 67890L);

		Assert.assertNotNull(dataSourceAccessToken);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			new String(
				Base64.decode(dataSourceAccessToken), StandardCharsets.UTF_8));

		Assert.assertEquals(
			"https://faro.test/o/faro/contacts/12345/data_source/connect",
			jsonObject.getString("url"));

		String token = jsonObject.getString("token");

		Assert.assertNotNull(token);
		Assert.assertFalse(token.isEmpty());
	}

	@Test
	public void testGetDataSourceMappingDisplays() throws Exception {
		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(Mockito.any(Locale.class), Mockito.anyString())
		).thenAnswer(
			AdditionalAnswers.returnsArgAt(1)
		);

		ReflectionTestUtil.setFieldValue(
			LanguageUtil.class, "_language", language);

		MockedStatic<PermissionThreadLocal> permissionThreadLocalMockedStatic =
			Mockito.mockStatic(PermissionThreadLocal.class);

		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		User user = Mockito.mock(User.class);

		Mockito.when(
			user.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			permissionChecker.getUser()
		).thenReturn(
			user
		);

		permissionThreadLocalMockedStatic.when(
			PermissionThreadLocal::getPermissionChecker
		).thenReturn(
			permissionChecker
		);

		List<DataSourceMappingDisplay> dataSourceMappingDisplays =
			_dataSourceFaroController.getDataSourceMappingDisplays(
				32719, 32783, null);

		Assert.assertEquals(
			dataSourceMappingDisplays.toString(), 13,
			dataSourceMappingDisplays.size());
	}

	@Test
	public void testGetFieldCatalog() throws Exception {
		Map<String, List<DataSourceFieldCatalogEntry>>
			dataSourceFieldCatalogEntries =
				HashMapBuilder.<String, List<DataSourceFieldCatalogEntry>>put(
					"ACCOUNT",
					Arrays.asList(
						new DataSourceFieldCatalogEntry(
							"Industry", false, false, "picklist"),
						new DataSourceFieldCatalogEntry(
							"Name", true, true, "string"))
				).build();

		Mockito.when(
			_contactsEngineClient.getDataSourceFieldCatalog(
				_faroProject, "32783", true)
		).thenReturn(
			dataSourceFieldCatalogEntries
		);

		Assert.assertSame(
			dataSourceFieldCatalogEntries,
			_dataSourceFaroController.getFieldCatalog(32719L, "32783", true));

		Mockito.verify(
			_contactsEngineClient
		).getDataSourceFieldCatalog(
			_faroProject, "32783", true
		);
	}

	@Test
	public void testPatchFieldSelection() throws Exception {
		Map<String, Set<String>> selectedFieldNames =
			HashMapBuilder.<String, Set<String>>put(
				"ACCOUNT", new HashSet<>(Arrays.asList("Industry", "Name"))
			).build();

		_dataSourceFaroController.patchFieldSelection(
			32719L, "32783", new FaroParam<>(selectedFieldNames));

		Mockito.verify(
			_contactsEngineClient
		).updateDataSourceFieldSelection(
			_faroProject, "32783", selectedFieldNames
		);
	}

	@Test
	public void testPatchTypeSalesforce() throws Exception {
		SalesforceProvider existingSalesforceProvider =
			new SalesforceProvider();

		SalesforceProvider.AccountsConfiguration accountsConfiguration =
			new SalesforceProvider.AccountsConfiguration();

		accountsConfiguration.setEnableAllAccounts(true);

		existingSalesforceProvider.setAccountsConfiguration(
			accountsConfiguration);

		DataSource existingDataSource = Mockito.mock(DataSource.class);

		Mockito.when(
			existingDataSource.getProvider()
		).thenReturn(
			existingSalesforceProvider
		);

		Mockito.when(
			_contactsEngineClient.getDataSource(_faroProject, "32720")
		).thenReturn(
			existingDataSource
		);

		DataSource dataSource = Mockito.mock(DataSource.class);

		Mockito.when(
			dataSource.getProvider()
		).thenReturn(
			new SalesforceProvider()
		);

		Mockito.when(
			_contactsEngineClient.patchDataSource(
				Mockito.eq(_faroProject), Mockito.eq("32720"), Mockito.isNull(),
				Mockito.isNull(), Mockito.isNull(), Mockito.any(Provider.class),
				Mockito.isNull(), Mockito.isNull(), Mockito.anyLong())
		).thenReturn(
			dataSource
		);

		SalesforceProvider.CampaignsConfiguration campaignsConfiguration =
			new SalesforceProvider.CampaignsConfiguration();

		campaignsConfiguration.setEnableAllCampaigns(true);

		SalesforceProvider.OpportunitiesConfiguration
			opportunitiesConfiguration =
				new SalesforceProvider.OpportunitiesConfiguration();

		opportunitiesConfiguration.setEnableAllOpportunities(true);

		try (MockedStatic<PermissionThreadLocal>
				permissionThreadLocalMockedStatic = Mockito.mockStatic(
					PermissionThreadLocal.class)) {

			permissionThreadLocalMockedStatic.when(
				PermissionThreadLocal::getPermissionChecker
			).thenReturn(
				Mockito.mock(PermissionChecker.class)
			);

			_dataSourceFaroController.patchTypeSalesforce(
				32719L, "32720", new FaroParam<>(),
				new FaroParam<>(campaignsConfiguration), new FaroParam<>(),
				new FaroParam<>(), null, null,
				new FaroParam<>(opportunitiesConfiguration), null, null);
		}

		ArgumentCaptor<Provider> providerArgumentCaptor =
			ArgumentCaptor.forClass(Provider.class);

		Mockito.verify(
			_contactsEngineClient
		).patchDataSource(
			Mockito.eq(_faroProject), Mockito.eq("32720"), Mockito.isNull(),
			Mockito.isNull(), Mockito.isNull(),
			providerArgumentCaptor.capture(), Mockito.isNull(),
			Mockito.isNull(), Mockito.anyLong()
		);

		SalesforceProvider salesforceProvider =
			(SalesforceProvider)providerArgumentCaptor.getValue();

		accountsConfiguration = salesforceProvider.getAccountsConfiguration();

		Assert.assertTrue(accountsConfiguration.isEnableAllAccounts());

		campaignsConfiguration = salesforceProvider.getCampaignsConfiguration();

		Assert.assertTrue(campaignsConfiguration.isEnableAllCampaigns());

		opportunitiesConfiguration =
			salesforceProvider.getOpportunitiesConfiguration();

		Assert.assertTrue(
			opportunitiesConfiguration.isEnableAllOpportunities());
	}

	private Field _createField(String name, String sourceName, String value) {
		return new Field() {
			{
				setContext("demographics");
				setDataSourceName("Liferay");
				setFieldType("Text");
				setName(name);
				setOwnerType("individual");
				setSourceName(sourceName);
				setValue(value);
			}
		};
	}

	private FieldMapping _createFieldMapping(
		String name, String... dataSourceFieldNames) {

		Map<String, String> dataSourceFieldNamesMap = new HashMap<>();

		for (int i = 0; i < dataSourceFieldNames.length; i++) {
			dataSourceFieldNamesMap.put("" + i, dataSourceFieldNames[i]);
		}

		return new FieldMapping() {
			{
				setContext("demographics");
				setDataSourceFieldNames(dataSourceFieldNamesMap);
				setDateModified(new Date());
				setDisplayName(name);
				setFieldName(name);
				setFieldType("Text");
				setOwnerType("individual");
			}
		};
	}

	private final ContactsCSVHelper _contactsCSVHelper = Mockito.mock(
		ContactsCSVHelper.class);
	private final ContactsEngineClient _contactsEngineClient = Mockito.mock(
		ContactsEngineClient.class);
	private final DataSourceFaroController _dataSourceFaroController =
		new DataSourceFaroController();
	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);

}