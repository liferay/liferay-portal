/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.PortletPreferenceValue;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.search.experiences.model.SXPBlueprint;
import com.liferay.search.experiences.model.SXPElement;
import com.liferay.search.experiences.rest.dto.v1_0.Configuration;
import com.liferay.search.experiences.rest.dto.v1_0.GeneralConfiguration;
import com.liferay.search.experiences.rest.dto.v1_0.util.ConfigurationUtil;
import com.liferay.search.experiences.service.SXPBlueprintLocalService;
import com.liferay.search.experiences.service.SXPElementLocalService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Almir Ferreira
 */
@Ignore
@RunWith(Arquillian.class)
public class SXPBlueprintAndSXPElementUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = TestPropsValues.getUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, _user.getUserId());
	}

	@After
	public void tearDown() throws Exception {
		for (Long sxpBlueprintId : _sxpBlueprintIds) {
			_sxpBlueprintLocalService.deleteSXPBlueprint(sxpBlueprintId);
		}

		for (Long sxpElementId : _sxpElementIds) {
			_sxpElementLocalService.deleteSXPElement(sxpElementId);
		}
	}

	@Test
	public void testUpgradeEmptySXPBlueprint() throws Exception {
		SXPBlueprint sxpBlueprint = _addSXPBlueprint(null, null, null);

		_runAllUpgrades();

		_assertSXPBlueprint("[]", sxpBlueprint.getSXPBlueprintId());
	}

	@Test
	public void testUpgradePortletPreferenceValue() throws Exception {
		SXPBlueprint sxpBlueprint = _addSXPBlueprint(
			_sxpBlueprintConfigurationJSONObject.toString(), null, null);

		Long sxpBlueprintId = sxpBlueprint.getSXPBlueprintId();

		PortletPreferenceValue lowLevelSearchOptionsPortletPreferenceValue =
			_addPortletPreferenceValue(
				null, "attributes", _PORTLET_ID_LOW_LEVEL_SEARCH_OPTIONS,
				JSONUtil.put(
					JSONUtil.put(
						"key", "search.experiences.blueprint.id"
					).put(
						"value", sxpBlueprintId
					)
				).toString());

		JSONObject suggestionsJSONObject = JSONUtil.put(
			"attributes",
			JSONUtil.put(
				"includeAssetSearchSummary", true
			).put(
				"includeAssetURL", true
			).put(
				"sxpBlueprintId", sxpBlueprintId
			)
		).put(
			"contributorName", "sxpBlueprint"
		).put(
			"displayGroupName", "suggestions"
		).put(
			"size", "5"
		);

		PortletPreferenceValue searchBarPortletPreferenceValue =
			_addPortletPreferenceValue(
				suggestionsJSONObject.toString(),
				"suggestionsContributorConfigurations", _PORTLET_ID_SEARCH_BAR,
				null);

		PortletPreferenceValue sxpBlueprintOptionsPortletPreferenceValue =
			_addPortletPreferenceValue(
				null, "sxpBlueprintId", _PORTLET_ID_SXP_BLUEPRINT_OPTIONS,
				String.valueOf(sxpBlueprintId));

		_addPortletPreferenceValue(
			null, "sxpBlueprintId", _PORTLET_ID_SXP_BLUEPRINT_OPTIONS,
			String.valueOf(sxpBlueprintId + 1));

		_runUpgrade("v3_0_0.SXPBlueprintUpgradeProcess");

		_assertSXPBlueprint(StringPool.BLANK, sxpBlueprint.getSXPBlueprintId());

		_assertPortletPreferenceValue(
			StringPool.BLANK, "attributes",
			JSONUtil.put(
				JSONUtil.put(
					"key",
					"search.experiences.blueprint.external.reference.code"
				).put(
					"value", sxpBlueprint.getExternalReferenceCode()
				)
			).toString(),
			lowLevelSearchOptionsPortletPreferenceValue.
				getPortletPreferenceValueId());

		JSONObject attributesJSONObject = suggestionsJSONObject.getJSONObject(
			"attributes");

		attributesJSONObject.put(
			"sxpBlueprintExternalReferenceCode",
			sxpBlueprint.getExternalReferenceCode()
		).remove(
			"sxpBlueprintId"
		);

		_assertPortletPreferenceValue(
			suggestionsJSONObject.toString(),
			"suggestionsContributorConfigurations", StringPool.BLANK,
			searchBarPortletPreferenceValue.getPortletPreferenceValueId());

		_assertPortletPreferenceValue(
			StringPool.BLANK, "sxpBlueprintExternalReferenceCode",
			sxpBlueprint.getExternalReferenceCode(),
			sxpBlueprintOptionsPortletPreferenceValue.
				getPortletPreferenceValueId());
	}

	@Test
	public void testUpgradeSXPBlueprintAndSXPElement() throws Exception {
		SXPBlueprint sxpBlueprint = _addSXPBlueprint(
			_sxpBlueprintConfigurationJSONObject.toString(),
			_read("elementInstances"), null);
		SXPElement sxpElement = _addSXPElement(_read("elementDefinition"));

		_runAllUpgrades();

		_assertSXPBlueprint(
			_read("elementInstancesUpdated"), sxpBlueprint.getSXPBlueprintId());
		_assertSXPElement(
			_read("elementDefinitionUpdated"), sxpElement.getSXPElementId());
	}

	@Test
	public void testUpgradeSXPBlueprintWithEnableAndDisableContributors()
		throws Exception {

		SXPBlueprint disableAllContributorsSXPBlueprint = _addSXPBlueprint(
			_read("disableAllContributors"), null, "1.0");
		SXPBlueprint enableAllContributorsSXPBlueprint = _addSXPBlueprint(
			_read("enableAllContributors"), null, "1.0");
		SXPBlueprint enableSomeContributorsSXPBlueprint = _addSXPBlueprint(
			_read("enableSomeContributors"), null, "1.0");

		_runUpgrade("v3_1_2.SXPBlueprintUpgradeProcess");

		String[] wildcardArray = {"*"};

		_assertGeneralConfigurationExcludesAndIncludes(
			wildcardArray, new String[0],
			disableAllContributorsSXPBlueprint.getSXPBlueprintId());
		_assertGeneralConfigurationExcludesAndIncludes(
			new String[0], wildcardArray,
			enableAllContributorsSXPBlueprint.getSXPBlueprintId());

		_assertGeneralConfigurationExcludesAndIncludes(
			new String[0],
			new String[] {
				"com.liferay.journal.internal.search.spi.model.query." +
					"contributor.JournalArticleKeywordQueryContributor",
				"com.liferay.journal.internal.search.spi.model.query." +
					"contributor.JournalFolderKeywordQueryContributor"
			},
			enableSomeContributorsSXPBlueprint.getSXPBlueprintId());
	}

	@Rule
	public TestName testName = new TestName();

	private PortletPreferenceValue _addPortletPreferenceValue(
			String largeValue, String name, String portletId, String smallValue)
		throws Exception {

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.addPortletPreferences(
				TestPropsValues.getCompanyId(), _group.getGroupId(),
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, TestPropsValues.getPlid(),
				portletId + "_INSTANCE_" + RandomTestUtil.randomString(),
				_portletLocalService.getPortletById(portletId),
				PortletConstants.DEFAULT_PREFERENCES);

		PortletPreferenceValue portletPreferenceValue =
			_portletPreferenceValueLocalService.createPortletPreferenceValue(
				RandomTestUtil.randomLong());

		portletPreferenceValue.setCompanyId(TestPropsValues.getCompanyId());
		portletPreferenceValue.setPortletPreferencesId(
			portletPreferences.getPortletPreferencesId());
		portletPreferenceValue.setIndex(0);
		portletPreferenceValue.setLargeValue(largeValue);
		portletPreferenceValue.setName(name);
		portletPreferenceValue.setReadOnly(false);
		portletPreferenceValue.setSmallValue(smallValue);

		return _portletPreferenceValueLocalService.addPortletPreferenceValue(
			portletPreferenceValue);
	}

	private SXPBlueprint _addSXPBlueprint(
			String configurationJSON, String elementInstancesJSON,
			String schemaVersion)
		throws Exception {

		SXPBlueprint sxpBlueprint = _sxpBlueprintLocalService.addSXPBlueprint(
			null, _user.getUserId(), configurationJSON,
			Collections.singletonMap(LocaleUtil.US, StringPool.BLANK),
			elementInstancesJSON, schemaVersion,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			_serviceContext);

		_sxpBlueprintIds.add(sxpBlueprint.getSXPBlueprintId());

		return sxpBlueprint;
	}

	private SXPElement _addSXPElement(String elementDefinitionJSON)
		throws Exception {

		SXPElement sxpElement = _sxpElementLocalService.addSXPElement(
			null, _user.getUserId(),
			Collections.singletonMap(LocaleUtil.US, StringPool.BLANK),
			elementDefinitionJSON, StringPool.BLANK, StringPool.BLANK, true,
			StringPool.BLANK,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			0, _serviceContext);

		_sxpElementIds.add(sxpElement.getSXPElementId());

		return sxpElement;
	}

	private void _assertGeneralConfigurationExcludesAndIncludes(
			String[] expectedExcludes, String[] expectedIncludes,
			long sxpBlueprintId)
		throws Exception {

		SXPBlueprint sxpBlueprint = _sxpBlueprintLocalService.getSXPBlueprint(
			sxpBlueprintId);

		Configuration configuration = ConfigurationUtil.toConfiguration(
			sxpBlueprint.getConfigurationJSON());

		GeneralConfiguration generalConfiguration =
			configuration.getGeneralConfiguration();

		Assert.assertArrayEquals(
			expectedExcludes,
			generalConfiguration.getClauseContributorsExcludes());
		Assert.assertArrayEquals(
			expectedIncludes,
			generalConfiguration.getClauseContributorsIncludes());
	}

	private void _assertPortletPreferenceValue(
			String expectedLargeValue, String expectedName,
			String expectedSmallValue, long portletPreferenceValueId)
		throws Exception {

		PortletPreferenceValue portletPreferenceValue =
			_portletPreferenceValueLocalService.getPortletPreferenceValue(
				portletPreferenceValueId);

		Assert.assertEquals(
			expectedLargeValue, portletPreferenceValue.getLargeValue());
		Assert.assertEquals(expectedName, portletPreferenceValue.getName());
		Assert.assertEquals(
			expectedSmallValue, portletPreferenceValue.getSmallValue());
	}

	private void _assertSearch(
			String expectedTitle, IndexerFixture<?> indexerFixture)
		throws Exception {

		indexerFixture.reindex(_group.getCompanyId());

		List<Document> documents = ListUtil.fromArray(
			indexerFixture.search(_user.getFullName()));

		Assert.assertFalse(documents.isEmpty());
		Assert.assertTrue(
			ListUtil.exists(
				documents,
				document -> Objects.equals(
					document.get("title_en_US"), expectedTitle)));
	}

	private void _assertSXPBlueprint(
			String expectedElementInstancesJSON, long sxpBlueprintId)
		throws Exception {

		SXPBlueprint sxpBlueprint = _sxpBlueprintLocalService.fetchSXPBlueprint(
			sxpBlueprintId);

		Assert.assertNotNull(sxpBlueprint);

		if (Validator.isBlank(expectedElementInstancesJSON)) {
			Assert.assertTrue(
				Validator.isBlank(sxpBlueprint.getElementInstancesJSON()));
		}
		else {
			JSONAssert.assertEquals(
				expectedElementInstancesJSON,
				sxpBlueprint.getElementInstancesJSON(),
				JSONCompareMode.NON_EXTENSIBLE);
		}

		_assertSearch(
			sxpBlueprint.getTitle(LocaleUtil.US), _sxpBlueprintIndexerFixture);
	}

	private void _assertSXPElement(
			String expectedElementDefinitionJSON, long sxpElementId)
		throws Exception {

		SXPElement sxpElement = _sxpElementLocalService.fetchSXPElement(
			sxpElementId);

		Assert.assertNotNull(sxpElement);

		if (Validator.isBlank(expectedElementDefinitionJSON)) {
			Assert.assertTrue(
				Validator.isBlank(sxpElement.getElementDefinitionJSON()));
		}
		else {
			JSONAssert.assertEquals(
				expectedElementDefinitionJSON,
				sxpElement.getElementDefinitionJSON(),
				JSONCompareMode.NON_EXTENSIBLE);
		}

		_assertSearch(
			sxpElement.getTitle(LocaleUtil.US), _sxpElementIndexerFixture);
	}

	private String _read(String name) {
		Class<?> clazz = getClass();

		return StringUtil.read(
			clazz,
			StringBundler.concat(
				"dependencies/", clazz.getSimpleName(), StringPool.PERIOD,
				testName.getMethodName(), StringPool.DASH, name, ".json"));
	}

	private void _runAllUpgrades() throws Exception {
		_runUpgrade("v1_3_0.SXPBlueprintAndSXPElementUpgradeProcess");
		_runUpgrade("v2_0_1.SXPBlueprintUpgradeProcess");
		_runUpgrade("v2_0_2.SXPBlueprintAndSXPElementUpgradeProcess");
		_runUpgrade("v2_0_3.SXPElementUpgradeProcess");
		_runUpgrade("v3_0_0.SXPBlueprintUpgradeProcess");
		_runUpgrade("v3_1_0.SXPBlueprintAndSXPElementUpgradeProcess");
		_runUpgrade("v3_1_1.SXPBlueprintAndSXPElementUpgradeProcess");
		_runUpgrade("v3_1_2.SXPBlueprintUpgradeProcess");
	}

	private void _runUpgrade(String name) throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.search.experiences.internal.upgrade." + name);

		upgradeProcess.upgrade();

		_multiVMPool.clear();
	}

	private static final String _PORTLET_ID_LOW_LEVEL_SEARCH_OPTIONS =
		"com_liferay_portal_search_web_low_level_search_options_portlet_" +
			"LowLevelSearchOptionsPortlet";

	private static final String _PORTLET_ID_SEARCH_BAR =
		"com_liferay_portal_search_web_search_bar_portlet_SearchBarPortlet";

	private static final String _PORTLET_ID_SXP_BLUEPRINT_OPTIONS =
		"com_liferay_search_experiences_web_internal_blueprint_options_" +
			"portlet_SXPBlueprintOptionsPortlet";

	@Inject(
		filter = "(&(component.name=com.liferay.search.experiences.internal.upgrade.registry.SXPServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

	private ServiceContext _serviceContext;
	private final JSONObject _sxpBlueprintConfigurationJSONObject =
		JSONUtil.put(
			"generalConfiguration",
			JSONUtil.put(
				"searchableAssetTypes",
				JSONUtil.put("com.liferay.journal.model.JournalArticle"))
		).put(
			"queryConfiguration", JSONUtil.put("applyIndexerClauses", true)
		);
	private final List<Long> _sxpBlueprintIds = new ArrayList<>();
	private final IndexerFixture<SXPBlueprint> _sxpBlueprintIndexerFixture =
		new IndexerFixture<>(SXPBlueprint.class);

	@Inject
	private SXPBlueprintLocalService _sxpBlueprintLocalService;

	private final List<Long> _sxpElementIds = new ArrayList<>();
	private final IndexerFixture<SXPElement> _sxpElementIndexerFixture =
		new IndexerFixture<>(SXPElement.class);

	@Inject
	private SXPElementLocalService _sxpElementLocalService;

	private User _user;

}