/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.blueprint.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalServiceUtil;
import com.liferay.expando.kernel.service.ExpandoTableLocalServiceUtil;
import com.liferay.expando.test.util.ExpandoTestUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.service.StagingLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalFolderServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.Field;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.search.experiences.blueprint.search.request.enhancer.SXPBlueprintSearchRequestEnhancer;
import com.liferay.search.experiences.internal.blueprint.test.util.SXPBlueprintSearchResultTestUtil;
import com.liferay.search.experiences.model.SXPBlueprint;
import com.liferay.search.experiences.model.SXPElement;
import com.liferay.search.experiences.rest.dto.v1_0.util.ConfigurationUtil;
import com.liferay.search.experiences.rest.dto.v1_0.util.ElementInstanceUtil;
import com.liferay.search.experiences.rest.dto.v1_0.util.SXPBlueprintUtil;
import com.liferay.search.experiences.service.SXPBlueprintLocalService;
import com.liferay.search.experiences.service.SXPElementLocalServiceUtil;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
public class SXPBlueprintSearchResultTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		WorkflowThreadLocal.setEnabled(false);

		_sxpElements = SXPElementLocalServiceUtil.getSXPElements(
			TestPropsValues.getCompanyId(), true);
	}

	@AfterClass
	public static void tearDownClass() {
		WorkflowThreadLocal.setEnabled(true);
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());

		_user = TestPropsValues.getUser();

		_journalArticleBuilder = new JournalArticleBuilder(
			_group, _journalArticles, _serviceContext, _user);
		_sxpBlueprint = _sxpBlueprintLocalService.addSXPBlueprint(
			null, _user.getUserId(), _configurationJSONObject.toString(),
			Collections.singletonMap(LocaleUtil.US, ""), null, "",
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			_serviceContext);
	}

	@Test
	public void testBoostAssetType() throws Exception {
		_updateConfigurationJSON(
			"generalConfiguration",
			JSONUtil.put(
				"searchableAssetTypes",
				JSONUtil.putAll(
					"com.liferay.journal.model.JournalArticle",
					"com.liferay.journal.model.JournalFolder")));

		_journalArticleBuilder.setTitle(
			"Article Coca Cola"
		).setContent(
			"Cola"
		).setJournalFolder(
			"Cola"
		).build();

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 10000
				).put(
					"entry_class_name",
					"com.liferay.journal.model.JournalFolder"
				).build()
			},
			new String[] {"Boost Asset Type"});

		_keywords = "Coca Cola";

		_assertSearch("[Cola, Article Coca Cola]");

		_updateElementInstancesJSON(null, null);

		_assertSearch("[Article Coca Cola, Cola]");
	}

	@Test
	public void testBoostContentsForTheCurrentLanguage() throws Exception {
		LocaleThreadLocal.setDefaultLocale(LocaleUtil.GERMANY);
		_journalArticleBuilder.setTitle(
			"cola cola de_DE"
		).setContent(
			"cola"
		).build();

		_journalArticleBuilder.setTitle(
			"fanta cola de_DE"
		).build();

		LocaleThreadLocal.setDefaultLocale(LocaleUtil.SPAIN);

		_journalArticleBuilder.setTitle(
			"coca cola es_ES"
		).setContent(
			"cola"
		).build();

		_journalArticleBuilder.setTitle(
			"pepsi cola es_ES"
		).build();

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 1000
				).build()
			},
			new String[] {"Boost Contents for the Current Language"});

		_keywords = "cola";

		_assertSearch(
			"[coca cola es_ES, pepsi cola es_ES, cola cola de_DE, fanta cola " +
				"de_DE]");

		LocaleThreadLocal.setDefaultLocale(LocaleUtil.GERMANY);

		_assertSearch(
			"[cola cola de_DE, fanta cola de_DE, coca cola es_ES, pepsi cola " +
				"es_ES]");

		LocaleThreadLocal.setDefaultLocale(LocaleUtil.US);
	}

	@Test
	public void testBoostContentsInACategory() throws Exception {
		_journalArticleBuilder.setTitle(
			"Article"
		).build();

		_journalArticleBuilder.setTitle(
			"Article With Category"
		).setAssetCategory(
			_addAssetCategory("Important", _user)
		).build();

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 100
				).put(
					"group_asset_category_external_reference_codes",
					new String[] {
						_group.getExternalReferenceCode() + "&&" +
							_assetCategory.getExternalReferenceCode()
					}
				).build()
			},
			new String[] {"Boost Contents in a Category"});

		_keywords = "Article";

		_assertSearch("[Article With Category, Article]");

		_updateElementInstancesJSON(null, null);

		_assertSearchIgnoreRelevance("[Article, Article With Category]");
	}

	@Test
	public void testBoostContentsInACategoryByKeywordMatch() throws Exception {
		_journalArticleBuilder.setTitle(
			"Article"
		).build();

		_journalArticleBuilder.setTitle(
			"Article With Category"
		).setAssetCategory(
			_addAssetCategory("Promoted", _addGroupUser(_group, "Employee"))
		).build();

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 100
				).put(
					"group_asset_category_external_reference_codes",
					new String[] {
						_group.getExternalReferenceCode() + "&&" +
							_assetCategory.getExternalReferenceCode()
					}
				).put(
					"keywords", "Article"
				).build()
			},
			new String[] {"Boost Contents in a Category by Keyword Match"});

		_keywords = "Article";

		_assertSearch("[Article With Category, Article]");

		_updateElementInstancesJSON(null, null);

		_assertSearch("[Article, Article With Category]");
	}

	@Test
	public void testBoostContentsInACategoryForAPeriodOfTime()
		throws Exception {

		_journalArticleBuilder.setTitle(
			"Article"
		).build();

		_journalArticleBuilder.setTitle(
			"Article With Category"
		).setAssetCategory(
			_addAssetCategory("Promoted", _addGroupUser(_group, "Customers"))
		).build();

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 1000
				).put(
					"end_date",
					DateUtil.getDate(
						new Date(System.currentTimeMillis() + Time.DAY),
						"yyyyMMdd", LocaleUtil.US)
				).put(
					"group_asset_category_external_reference_codes",
					new String[] {
						_group.getExternalReferenceCode() + "&&" +
							_assetCategory.getExternalReferenceCode()
					}
				).put(
					"start_date",
					DateUtil.getDate(
						new Date(System.currentTimeMillis() - Time.DAY),
						"yyyyMMdd", LocaleUtil.US)
				).build()
			},
			new String[] {"Boost Contents in a Category for a Period of Time"});

		_keywords = "Article";

		_assertSearch("[Article With Category, Article]");

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 1000
				).put(
					"end_date",
					DateUtil.getDate(
						new Date(System.currentTimeMillis() - Time.DAY),
						"yyyyMMdd", LocaleUtil.US)
				).put(
					"group_asset_category_external_reference_codes",
					new String[] {
						_group.getExternalReferenceCode() + "&&" +
							_assetCategory.getExternalReferenceCode()
					}
				).put(
					"start_date",
					DateUtil.getDate(
						new Date(System.currentTimeMillis() - (Time.DAY * 2)),
						"yyyyMMdd", LocaleUtil.US)
				).build()
			},
			new String[] {"Boost Contents in a Category for a Period of Time"});

		_assertSearch("[Article, Article With Category]");

		_updateElementInstancesJSON(null, null);

		_assertSearch("[Article, Article With Category]");
	}

	@Test
	public void testBoostContentsInACategoryForAUserSegment() throws Exception {
		_journalArticleBuilder.setTitle(
			"Article"
		).build();

		_journalArticleBuilder.setTitle(
			"Article With Category"
		).setAssetCategory(
			_addAssetCategory("Promoted", _addGroupUser(_group, "Employee"))
		).build();

		_keywords = "Article";

		SegmentsEntry segmentsEntry = _addSegmentsEntry(_user);

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 1000
				).put(
					"group_asset_category_external_reference_codes",
					new String[] {
						_group.getExternalReferenceCode() + "&&" +
							_assetCategory.getExternalReferenceCode()
					}
				).put(
					"user_segment_ids", segmentsEntry.getSegmentsEntryId()
				).build()
			},
			new String[] {"Boost Contents in a Category for User Segments"});

		_assertSearch("[Article With Category, Article]");

		User user = UserTestUtil.addUser(_group.getGroupId());

		_setCurrentUser(user);

		_assertSearch("[Article, Article With Category]");

		_updateElementInstancesJSON(null, null);

		_assertSearch("[Article, Article With Category]");
	}

	@Test
	public void testBoostContentsInACategoryForGuestUsers() throws Exception {
		_journalArticleBuilder.setTitle(
			"Article"
		).build();

		_journalArticleBuilder.setTitle(
			"Article With Category"
		).setAssetCategory(
			_addAssetCategory("Promoted", _user)
		).build();

		_setCurrentUser(_userLocalService.getGuestUser(_group.getCompanyId()));

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 100
				).put(
					"group_asset_category_external_reference_codes",
					new String[] {
						_group.getExternalReferenceCode() + "&&" +
							_assetCategory.getExternalReferenceCode()
					}
				).build()
			},
			new String[] {"Boost Contents in a Category for Guest Users"});

		_keywords = "Article";

		_assertSearch("[Article With Category, Article]");

		User user = UserTestUtil.addUser(_group.getGroupId());

		_setCurrentUser(user);

		_assertSearch("[Article, Article With Category]");

		_updateElementInstancesJSON(null, null);

		_assertSearch("[Article, Article With Category]");
	}

	@Test
	public void testBoostContentsInACategoryForNewUserAccounts()
		throws Exception {

		_journalArticleBuilder.setTitle(
			"Article"
		).build();

		_journalArticleBuilder.setTitle(
			"Article With Category"
		).setAssetCategory(
			_addAssetCategory("New User", _addGroupUser(_group, "Employee"))
		).build();

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 1000
				).put(
					"group_asset_category_external_reference_codes",
					new String[] {
						_group.getExternalReferenceCode() + "&&" +
							_assetCategory.getExternalReferenceCode()
					}
				).put(
					"time_range", "30d"
				).build()
			},
			new String[] {
				"Boost Contents in a Category for New User Accounts"
			});

		_keywords = "Article";

		_assertSearch("[Article With Category, Article]");

		User user = UserTestUtil.addUser(_group.getGroupId());

		Calendar calendar = CalendarFactoryUtil.getCalendar();

		calendar.add(Calendar.DAY_OF_MONTH, -60);

		Date date = calendar.getTime();

		user.setCreateDate(date);

		user = _userLocalService.updateUser(user);

		_setCurrentUser(user);

		_assertSearch("[Article, Article With Category]");

		_updateElementInstancesJSON(null, null);

		_assertSearch("[Article, Article With Category]");
	}

	@Test
	public void testBoostContentsInACategoryForTheTimeOfDay() throws Exception {
		_journalArticleBuilder.setTitle(
			"Article"
		).build();

		_journalArticleBuilder.setTitle(
			"Article With Category"
		).setAssetCategory(
			_addAssetCategory("Time", _user)
		).build();

		LocalDateTime localDateTime = LocalDateTime.now();

		String[] timeOfDays = _getTimeOfDayAndNextTimeOfDay(
			localDateTime.toLocalTime());

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 100
				).put(
					"group_asset_category_external_reference_codes",
					new String[] {
						_group.getExternalReferenceCode() + "&&" +
							_assetCategory.getExternalReferenceCode()
					}
				).put(
					"time_of_day", timeOfDays[0]
				).build()
			},
			new String[] {"Boost Contents in a Category for the Time of Day"});

		_keywords = "Article";

		_assertSearch("[Article With Category, Article]");

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 100
				).put(
					"group_asset_category_external_reference_codes",
					new String[] {
						_group.getExternalReferenceCode() + "&&" +
							_assetCategory.getExternalReferenceCode()
					}
				).put(
					"time_of_day", timeOfDays[1]
				).build()
			},
			new String[] {"Boost Contents in a Category for the Time of Day"});

		_assertSearch("[Article, Article With Category]");

		_updateElementInstancesJSON(null, null);

		_assertSearch("[Article, Article With Category]");
	}

	@Test
	public void testBoostContentsOnMySites() throws Exception {
		Group groupA = _addGroup();

		_journalArticleBuilder.setTitle(
			"Site Other Group"
		).setContent(
			"Site"
		).setGroup(
			groupA
		).build();

		Group groupB = _addGroup();

		_journalArticleBuilder.setTitle(
			"Site Group B"
		).setGroup(
			groupB
		).build();

		User userSiteB = UserTestUtil.addUser(groupB.getGroupId());

		_setCurrentUser(userSiteB);

		_keywords = "Site";

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 100
				).build()
			},
			new String[] {"Boost Contents on My Sites"});

		_assertSearch("[Site Group B, Site Other Group]");

		_updateElementInstancesJSON(null, null);

		_assertSearch("[Site Other Group, Site Group B]");
	}

	@Test
	public void testBoostContentsWithMoreVersions() throws Exception {
		_journalArticleBuilder.setTitle(
			"Article 1.0"
		).setContent(
			"Article"
		).build();

		_journalArticleBuilder.setTitle(
			"Article 2.0"
		).build();

		JournalArticle journalArticle = _journalArticles.get(1);

		journalArticle.setVersion(2.0);

		_journalArticleLocalService.updateJournalArticle(journalArticle);

		_journalArticles.set(
			0,
			JournalTestUtil.updateArticle(
				_journalArticles.get(0), "Article 1.1"));

		_keywords = "Article";

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 100
				).put(
					"factor", 10
				).put(
					"modifier", "sqrt"
				).build()
			},
			new String[] {"Boost Contents With More Versions"});

		_assertSearch("[Article 2.0, Article 1.1]");

		_updateElementInstancesJSON(null, null);

		_assertSearch("[Article 1.1, Article 2.0]");
	}

	@Test
	public void testBoostFreshness() throws Exception {
		_journalArticleBuilder.setTitle(
			"First Created"
		).setContent(
			"Created"
		).build();

		_journalArticleBuilder.setTitle(
			"Second Created"
		).build();

		JournalArticle journalArticle = _journalArticles.get(0);

		journalArticle.setModifiedDate(
			DateUtil.newDate(System.currentTimeMillis() - Time.HOUR));

		_journalArticleLocalService.updateJournalArticle(journalArticle);

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 100
				).put(
					"decay", 0.5
				).put(
					"offset", "0s"
				).put(
					"scale", "2s"
				).build()
			},
			new String[] {"Boost Freshness"});

		_keywords = "Created";

		_assertSearch("[Second Created, First Created]");

		_updateElementInstancesJSON(null, null);

		_assertSearch("[First Created, Second Created]");
	}

	@Test
	public void testBoostLongerContents() throws Exception {
		_journalArticleBuilder.setTitle(
			"Article 1"
		).setContent(
			"Article"
		).build();

		_journalArticleBuilder.setTitle(
			"Article 2"
		).setContent(
			"Content Content"
		).build();

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 1000
				).put(
					"factor", 1.5
				).put(
					"modifier", "ln"
				).build()
			},
			new String[] {"Boost Longer Contents"});

		_keywords = "Article";

		_assertSearch("[Article 2, Article 1]");

		_updateElementInstancesJSON(null, null);

		_assertSearch("[Article 1, Article 2]");
	}

	@Test
	public void testBoostProximity() throws Exception {
		ExpandoTable expandoTable = ExpandoTableLocalServiceUtil.fetchTable(
			_group.getCompanyId(),
			ClassNameLocalServiceUtil.getClassNameId(JournalArticle.class),
			"CUSTOM_FIELDS");

		if (expandoTable == null) {
			expandoTable = ExpandoTableLocalServiceUtil.addTable(
				_group.getCompanyId(),
				ClassNameLocalServiceUtil.getClassNameId(JournalArticle.class),
				"CUSTOM_FIELDS");

			_expandoTables.add(expandoTable);
		}

		String fieldName = "location";

		ExpandoColumn expandoColumn = ExpandoTestUtil.addColumn(
			expandoTable, fieldName, ExpandoColumnConstants.GEOLOCATION);

		_expandoColumns.add(expandoColumn);

		UnicodeProperties unicodeProperties =
			expandoColumn.getTypeSettingsProperties();

		unicodeProperties.setProperty(
			ExpandoColumnConstants.INDEX_TYPE,
			String.valueOf(ExpandoColumnConstants.GEOLOCATION));

		expandoColumn.setTypeSettingsProperties(unicodeProperties);

		ExpandoColumnLocalServiceUtil.updateExpandoColumn(expandoColumn);

		_journalArticleBuilder.setTitle(
			"Branch SF"
		).setGeolocation(
			fieldName, 64.01, -117.42
		).build();

		_journalArticleBuilder.setTitle(
			"Branch LA"
		).setGeolocation(
			fieldName, 24.03, -107.44
		).build();

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 100
				).put(
					"decay", 0.3
				).put(
					"field", "expando__custom_fields__location_geolocation"
				).put(
					"ipstack.latitude", "24.03"
				).put(
					"ipstack.longitude", "-107.44"
				).put(
					"offset", "0"
				).put(
					"scale", "100km"
				).build()
			},
			new String[] {"Boost Proximity"});

		_keywords = "branch";

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				_getConfigurationTemporarySwapper(
					"2345", "34.94.32.240", "true")) {

			_assertSearch("[Branch LA, Branch SF]");
		}

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 100
				).put(
					"decay", 0.3
				).put(
					"field", "expando__custom_fields__location_geolocation"
				).put(
					"ipstack.latitude", "64.01"
				).put(
					"ipstack.longitude", "-117.42"
				).put(
					"offset", "0"
				).put(
					"scale", "100km"
				).build()
			},
			new String[] {"Boost Proximity"});

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				_getConfigurationTemporarySwapper(
					"2345", "64.225.32.7", "true")) {

			_assertSearch("[Branch SF, Branch LA]");
		}

		_updateElementInstancesJSON(null, null);

		_assertSearchIgnoreRelevance("[Branch LA, Branch SF]");
	}

	@Test
	public void testBoostTaggedContents() throws Exception {
		_assetTag = AssetTagLocalServiceUtil.addTag(
			null, _user.getUserId(), _group.getGroupId(), "Boost",
			_serviceContext);

		_journalArticleBuilder.setTitle(
			"Article"
		).build();

		_journalArticleBuilder.setTitle(
			"Tagged Article"
		).setAssetTag(
			_assetTag
		).build();

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"asset_tags", new String[] {_assetTag.getName()}
				).put(
					"boost", 1000
				).build()
			},
			new String[] {"Boost Tagged Contents"});

		_keywords = "Article";

		_assertSearch("[Tagged Article, Article]");

		_updateElementInstancesJSON(null, null);

		_assertSearch("[Article, Tagged Article]");
	}

	@Test
	public void testBoostTagsMatch() throws Exception {
		_assetTag = AssetTagLocalServiceUtil.addTag(
			null, _user.getUserId(), _group.getGroupId(), "cola",
			_serviceContext);

		_journalArticleBuilder.setTitle(
			"coca cola"
		).build();

		_journalArticleBuilder.setTitle(
			"pepsi"
		).setAssetTag(
			_assetTag
		).build();

		_keywords = "cola";

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 100
				).build()
			},
			new String[] {"Boost Tags Match"});

		_assertSearch("[pepsi, coca cola]");

		_updateElementInstancesJSON(null, null);

		_assertSearch("[coca cola, pepsi]");
	}

	@Test
	public void testBoostWebContentsByKeywordsMatch() throws Exception {
		_journalArticleBuilder.setTitle(
			"beta alpha"
		).setContent(
			"alpha alpha"
		).build();

		_journalArticleBuilder.setTitle(
			"charlie alpha"
		).build();

		_keywords = "alpha";

		JournalArticle journalArticle = _journalArticles.get(1);

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"article_ids", new String[] {journalArticle.getArticleId()}
				).put(
					"boost", 100
				).put(
					"values", "alpha"
				).build()
			},
			new String[] {"Boost Web Contents by Keyword Match"});

		_assertSearch("[charlie alpha, beta alpha]");

		_updateElementInstancesJSON(null, null);

		_assertSearch("[beta alpha, charlie alpha]");
	}

	@Test
	public void testCustomParameterWithinPasteAnyElasticSearchQueryElement()
		throws Exception {

		_updateConfigurationJSON(
			"parameterConfiguration",
			JSONUtil.put(
				"parameters",
				JSONUtil.put("myparam", JSONUtil.put("type", "String"))));

		_journalArticleBuilder.setTitle(
			"Coca Cola"
		).setContent(
			"cola cola"
		).build();

		_journalArticleBuilder.setTitle(
			"liferay"
		).build();

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"occur", "must"
				).put(
					"query",
					JSONUtil.put(
						"match",
						JSONUtil.put(
							"title_en_US", JSONUtil.put("query", "${myparam}")))
				).build()
			},
			new String[] {"Paste Any Elasticsearch Query"});

		try {
			_assertSearchIgnoreRelevance("[Coca Cola, liferay]");
		}
		catch (RuntimeException runtimeException) {
			Throwable[] suppressed = runtimeException.getSuppressed();

			for (int i = 0; i < 3; i++) {
				suppressed = suppressed[0].getSuppressed();
			}

			Assert.assertEquals(
				"[com.liferay.search.experiences.blueprint.exception." +
					"UnresolvedTemplateVariableException: Unresolved " +
						"template variables: [myparam]]",
				Arrays.toString(suppressed));
		}

		_assertSearchIgnoreRelevance(
			"[liferay]",
			searchRequestBuilder -> searchRequestBuilder.withSearchContext(
				searchContext -> searchContext.setAttribute(
					"myparam", "liferay")));
	}

	@Test
	public void testFilterByExactTermsMatch() throws Exception {
		_journalArticleBuilder.setTitle(
			"coca cola filter"
		).build();

		_journalArticleBuilder.setTitle(
			"pepsi cola filter"
		).build();

		_journalArticleBuilder.setTitle(
			"sprite cola"
		).build();

		_keywords = "cola";

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"field", "title_en_US"
				).put(
					"values", new String[] {"filter"}
				).build()
			},
			new String[] {"Filter by Exact Terms Match"});

		_assertSearchIgnoreRelevance("[coca cola filter, pepsi cola filter]");

		_updateElementInstancesJSON(null, null);

		_assertSearchIgnoreRelevance(
			"[coca cola filter, pepsi cola filter, sprite cola]");
	}

	@Test
	public void testHideByExactTermMatch() throws Exception {
		_journalArticleBuilder.setTitle(
			"Out of the Folder"
		).build();

		JournalFolder journalFolder = JournalFolderServiceUtil.addFolder(
			null, _group.getGroupId(), 0, RandomTestUtil.randomString(),
			StringPool.BLANK, _serviceContext);

		_journalArticleBuilder.setTitle(
			"In Folder"
		).setJournalFolder(
			journalFolder
		).build();

		_keywords = "folder";

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"field", "folderId"
				).put(
					"value", String.valueOf(journalFolder.getFolderId())
				).build()
			},
			new String[] {"Hide by Exact Term Match"});

		_assertSearchIgnoreRelevance("[Out of the Folder]");

		_updateElementInstancesJSON(null, null);

		_assertSearchIgnoreRelevance("[In Folder, Out of the Folder]");
	}

	@Test
	public void testHideComments() throws Exception {
		_journalArticleBuilder.setTitle(
			"Article"
		).build();

		JournalArticle journalArticle = _journalArticles.get(0);

		CommentManagerUtil.addComment(
			_user.getUserId(), _serviceContext.getScopeGroupId(),
			JournalArticle.class.getName(), journalArticle.getResourcePrimKey(),
			"Article Comment",
			new IdentityServiceContextFunction(_serviceContext));

		_updateConfigurationJSON(
			"generalConfiguration",
			JSONUtil.put(
				"searchableAssetTypes",
				JSONUtil.putAll(
					"com.liferay.journal.model.JournalArticle",
					"com.liferay.message.boards.model.MBMessage")));

		_updateConfigurationJSON(
			"queryConfiguration", JSONUtil.put("applyIndexerClauses", false));

		_updateSXPBlueprint();

		_updateElementInstancesJSON(
			new Object[] {_getTextMatchOverMultipleFields()},
			new String[] {"Text Match Over Multiple Fields"});

		_keywords = "Article";

		_assertSearchIgnoreRelevance("[Article, Article Comment]");

		_updateElementInstancesJSON(
			new Object[] {_getTextMatchOverMultipleFields(), null},
			new String[] {"Text Match Over Multiple Fields", "Hide Comments"});

		_assertSearchIgnoreRelevance("[Article]");
	}

	@Test
	public void testHideContentsInACategory() throws Exception {
		_journalArticleBuilder.setTitle(
			"Without Category"
		).build();

		_journalArticleBuilder.setTitle(
			"Hidden Category"
		).setAssetCategory(
			_addAssetCategory("Hidden", _addGroupUser(_group, "Employee"))
		).build();

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"group_asset_category_external_reference_codes",
					new String[] {
						_group.getExternalReferenceCode() + "&&" +
							_assetCategory.getExternalReferenceCode()
					}
				).build()
			},
			new String[] {"Hide Contents in a Category"});

		_keywords = "Category";

		_assertSearchIgnoreRelevance("[Without Category]");

		_updateElementInstancesJSON(null, null);

		_assertSearchIgnoreRelevance("[Hidden Category, Without Category]");
	}

	@Test
	public void testHideContentsInACategoryForGuestUsers() throws Exception {
		User siteUser = UserTestUtil.addUser(_group.getGroupId());

		_journalArticleBuilder.setTitle(
			"Guest Users"
		).build();

		_journalArticleBuilder.setTitle(
			"Non-Guest Users"
		).setAssetCategory(
			_addAssetCategory("Hide from Guest Users", siteUser)
		).build();

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"group_asset_category_external_reference_codes",
					new String[] {
						_group.getExternalReferenceCode() + "&&" +
							_assetCategory.getExternalReferenceCode()
					}
				).build()
			},
			new String[] {"Hide Contents in a Category for Guest Users"});

		_keywords = "Guest";

		_setCurrentUser(siteUser);

		_assertSearchIgnoreRelevance("[Guest Users, Non-Guest Users]");

		_setCurrentUser(_userLocalService.getGuestUser(_group.getCompanyId()));

		_assertSearchIgnoreRelevance("[Guest Users]");

		_updateElementInstancesJSON(null, null);

		_assertSearchIgnoreRelevance("[Guest Users, Non-Guest Users]");
	}

	@Test
	public void testHideHiddenContents() throws Exception {
		_journalArticleBuilder.setTitle(
			"Cafe Rio"
		).setContent(
			"Los Angeles"
		).build();

		_journalArticleBuilder.setTitle(
			"Cloud Cafe"
		).setContent(
			"Orange County"
		).build();

		_journalArticleBuilder.setTitle(
			"Denny's"
		).setContent(
			"Los Angeles"
		).build();

		_journalArticleBuilder.setTitle(
			"Starbucks Cafe"
		).setContent(
			"Los Angeles"
		).build();

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"occur", "must_not"
				).put(
					"query",
					SXPBlueprintSearchResultTestUtil.getMatchQueryJSONObject(
						200, "los angeles")
				).build(),
				_getTextMatchOverMultipleFields(), null
			},
			new String[] {
				"Paste Any Elasticsearch Query",
				"Text Match Over Multiple Fields", "Hide Hidden Contents"
			});

		_keywords = "cafe";

		_assertSearchIgnoreRelevance("[Cloud Cafe]");

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"occur", "must_not"
				).put(
					"query",
					SXPBlueprintSearchResultTestUtil.getMatchQueryJSONObject(
						200, "orange county")
				).build(),
				_getTextMatchOverMultipleFields(), null
			},
			new String[] {
				"Paste Any Elasticsearch Query",
				"Text Match Over Multiple Fields", "Hide Hidden Contents"
			});

		_assertSearchIgnoreRelevance("[Cafe Rio, Starbucks Cafe]");
	}

	@Test
	public void testHideTaggedContents() throws Exception {
		_journalArticleBuilder.setTitle(
			"do not hide me"
		).build();

		_journalArticleBuilder.setTitle(
			"hide me"
		).setAssetTag(
			AssetTestUtil.addTag(_group.getGroupId(), "hide")
		).setJournalFolder(
			"Folder"
		).build();

		_keywords = "hide me";

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"asset_tag", "hide"
				).build()
			},
			new String[] {"Hide Tagged Contents"});

		_assertSearchIgnoreRelevance("[do not hide me]");

		_updateElementInstancesJSON(null, null);

		_assertSearchIgnoreRelevance("[do not hide me, hide me]");
	}

	@Test
	public void testLimitSearchToContentsCreatedWithinAPeriodOfTime()
		throws Exception {

		_journalArticleBuilder.setTitle(
			"Coca Cola"
		).setContent(
			"cola cola"
		).build();

		_journalArticleBuilder.setTitle(
			"Pepsi Cola"
		).build();

		JournalArticle journalArticle = _journalArticles.get(0);

		journalArticle.setCreateDate(
			DateUtil.newDate(System.currentTimeMillis() - Time.DAY));

		_journalArticleLocalService.updateJournalArticle(journalArticle);

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"end_date",
					DateUtil.getDate(
						new Date(System.currentTimeMillis()), "yyyyMMddHHmmss",
						LocaleUtil.US)
				).put(
					"start_date",
					DateUtil.getDate(
						new Date(System.currentTimeMillis() - Time.HOUR),
						"yyyyMMddHHmmss", LocaleUtil.US)
				).build()
			},
			new String[] {
				"Limit Search to Contents Created Within a Period of Time"
			});

		_assertSearchIgnoreRelevance("[Pepsi Cola]");

		_updateElementInstancesJSON(null, null);

		_assertSearchIgnoreRelevance("[Coca Cola, Pepsi Cola]");
	}

	@Test
	public void testLimitSearchToHeadVersion() throws Exception {
		_updateConfigurationJSON(
			"queryConfiguration", JSONUtil.put("applyIndexerClauses", false));

		_journalArticleBuilder.setTitle(
			"Article 1.0"
		).build();

		_journalArticles.set(
			0,
			JournalTestUtil.updateArticle(
				_journalArticles.get(0), "Article 1.1"));

		_journalArticles.set(
			0,
			JournalTestUtil.updateArticle(
				_journalArticles.get(0), "Article 1.2"));

		_updateElementInstancesJSON(
			new Object[] {_getTextMatchOverMultipleFields()},
			new String[] {"Text Match Over Multiple Fields"});

		_keywords = "Article";

		_assertSearchIgnoreRelevance("[Article 1.0, Article 1.1, Article 1.2]");

		_updateElementInstancesJSON(
			new Object[] {_getTextMatchOverMultipleFields(), null},
			new String[] {
				"Text Match Over Multiple Fields",
				"Limit Search to Head Version"
			});

		_assertSearch("[Article 1.2]");
	}

	@Test
	public void testLimitSearchToMyContents() throws Exception {
		User newUser = UserTestUtil.addUser(_group.getGroupId());

		_setCurrentUser(newUser);

		_journalArticleBuilder.setTitle(
			"Article 1 New User"
		).build();

		_journalArticleBuilder.setTitle(
			"Article 2 New User"
		).build();

		_setCurrentUser(_user);

		_journalArticleBuilder.setTitle(
			"Article 1 Default User"
		).build();

		_journalArticleBuilder.setTitle(
			"Article 2 Default User"
		).build();

		_keywords = "Article";

		_updateElementInstancesJSON(
			null, new String[] {"Limit Search to My Contents"});

		_assertSearchIgnoreRelevance(
			"[Article 1 Default User, Article 2 Default User]");

		_updateElementInstancesJSON(null, null);

		_assertSearchIgnoreRelevance(
			"[Article 1 Default User, Article 1 New User, Article 2 Default " +
				"User, Article 2 New User]");
	}

	@Test
	public void testLimitSearchToMySites() throws Exception {
		Group groupA = _addGroup();

		_journalArticleBuilder.setTitle(
			"Site A"
		).setGroup(
			groupA
		).build();

		Group groupB = _addGroup();

		_journalArticleBuilder.setTitle(
			"Site B"
		).setGroup(
			groupB
		).build();

		_journalArticleBuilder.setTitle(
			"Current Site"
		).build();

		User user = UserTestUtil.addUser(groupA.getGroupId());

		_setCurrentUser(user);

		_keywords = "Site";

		_updateElementInstancesJSON(
			null, new String[] {"Limit Search to My Sites"});

		_assertSearchIgnoreRelevance("[Site A]");

		_updateElementInstancesJSON(null, null);

		_assertSearchIgnoreRelevance("[Current Site, Site A, Site B]");
	}

	@Test
	public void testLimitSearchToPDFFiles() throws Exception {
		_updateConfigurationJSON(
			"generalConfiguration",
			JSONUtil.put(
				"searchableAssetTypes",
				JSONUtil.putAll(
					"com.liferay.document.library.kernel.model.DLFileEntry",
					"com.liferay.journal.model.JournalArticle")));

		_addFileEntry("PDF file", ".pdf");

		_journalArticleBuilder.setTitle(
			"Article file 1"
		).build();

		_updateElementInstancesJSON(
			null, new String[] {"Limit Search to PDF Files"});

		_keywords = "file";

		_assertSearch("[PDF file]");

		_updateElementInstancesJSON(null, null);

		_assertSearchIgnoreRelevance("[Article file 1, PDF file]");
	}

	@Test
	public void testLimitSearchToPublishedContents() throws Exception {
		_updateConfigurationJSON(
			"queryConfiguration", JSONUtil.put("applyIndexerClauses", false));

		_journalArticleBuilder.setTitle(
			"Article 1"
		).build();

		_journalArticleBuilder.setTitle(
			"Article 2"
		).build();

		_journalArticleBuilder.setTitle(
			"Draft Article"
		).setApproved(
			false
		).setWorkflowEnabled(
			true
		).build();

		_updateElementInstancesJSON(
			new Object[] {_getTextMatchOverMultipleFields()},
			new String[] {"Text Match Over Multiple Fields"});

		_keywords = "Article";

		_assertSearchIgnoreRelevance("[Article 1, Article 2, Draft Article]");

		_updateElementInstancesJSON(
			new Object[] {_getTextMatchOverMultipleFields(), null},
			new String[] {
				"Text Match Over Multiple Fields",
				"Limit Search to Published Contents"
			});

		_assertSearchIgnoreRelevance("[Article 1, Article 2]");
	}

	@Test
	public void testLimitSearchToTheCurrentSite() throws Exception {
		Group groupA = _addGroup();

		_journalArticleBuilder.setTitle(
			"Site A"
		).setGroup(
			groupA
		).build();

		_journalArticleBuilder.setTitle(
			"Current Site"
		).build();

		_keywords = "Site";

		User user = UserTestUtil.addUser(groupA.getGroupId());

		_setCurrentUser(user);

		_updateElementInstancesJSON(
			null, new String[] {"Limit Search to the Current Site"});

		_assertSearchIgnoreRelevance("[Current Site]");

		_updateElementInstancesJSON(null, null);

		_assertSearchIgnoreRelevance("[Current Site, Site A]");
	}

	@Test
	public void testLimitSearchToTheseSites() throws Exception {
		Group groupA = _addGroup();

		_journalArticleBuilder.setTitle(
			"Site A"
		).setGroup(
			groupA
		).build();

		Group groupB = _addGroup();

		_journalArticleBuilder.setTitle(
			"Site B"
		).setGroup(
			groupB
		).build();

		_journalArticleBuilder.setTitle(
			"Current Site"
		).build();

		_keywords = "Site";

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"scope_group_external_reference_codes",
					new String[] {
						groupA.getExternalReferenceCode(),
						groupB.getExternalReferenceCode()
					}
				).build()
			},
			new String[] {"Limit Search to These Sites"});

		_assertSearchIgnoreRelevance("[Site A, Site B]");

		_updateElementInstancesJSON(null, null);

		_assertSearchIgnoreRelevance("[Current Site, Site A, Site B]");
	}

	@Test
	public void testMatch() throws Exception {
		_journalArticleBuilder.setTitle(
			"Cafe Rio"
		).setContent(
			"Los Angeles"
		).build();

		_journalArticleBuilder.setTitle(
			"Cloud Cafe"
		).setContent(
			"Orange County"
		).build();

		_journalArticleBuilder.setTitle(
			"Denny's"
		).setContent(
			"Los Angeles"
		).build();

		_journalArticleBuilder.setTitle(
			"Starbucks Cafe"
		).setContent(
			"Los Angeles"
		).build();

		_updateElementInstancesJSON(null, null);

		_keywords = "cafe";

		_assertSearchIgnoreRelevance("[Cafe Rio, Cloud Cafe, Starbucks Cafe]");

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"occur", "must"
				).put(
					"query",
					SXPBlueprintSearchResultTestUtil.getMatchQueryJSONObject(
						200, "los angeles")
				).build()
			},
			new String[] {"Paste Any Elasticsearch Query"});

		_assertSearchIgnoreRelevance("[Cafe Rio, Starbucks Cafe]");

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"occur", "must"
				).put(
					"query",
					SXPBlueprintSearchResultTestUtil.getMatchQueryJSONObject(
						200, "orange county")
				).build()
			},
			new String[] {"Paste Any Elasticsearch Query"});

		_assertSearchIgnoreRelevance("[Cloud Cafe]");
	}

	@Test
	public void testPhraseMatch() throws Exception {
		_journalArticleBuilder.setTitle(
			"this coca looks like a kind of drink"
		).setContent(
			"coca coca"
		).build();

		_journalArticleBuilder.setTitle(
			"this looks like a kind of coca drink"
		).build();

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"occur", "must"
				).put(
					"query",
					SXPBlueprintSearchResultTestUtil.
						getMultiMatchQueryJSONObject(
							1, SXPBlueprintSearchResultTestUtil.FIELDS, null,
							"or", "${keywords}", "most_fields")
				).build(),
				HashMapBuilder.<String, Object>put(
					"boost", 100
				).put(
					"fields", SXPBlueprintSearchResultTestUtil.FIELDS
				).put(
					"keywords", "${keywords}"
				).put(
					"type", "phrase"
				).build()
			},
			new String[] {
				"Paste Any Elasticsearch Query", "Boost All Keywords Match"
			});

		_keywords = "coca drink";

		_assertSearch(
			"[this looks like a kind of coca drink, this coca looks like a " +
				"kind of drink]");

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"occur", "must"
				).put(
					"query",
					SXPBlueprintSearchResultTestUtil.
						getMultiMatchQueryJSONObject(
							10, SXPBlueprintSearchResultTestUtil.FIELDS, null,
							"or", "${keywords}", "most_fields")
				).build()
			},
			new String[] {"Paste Any Elasticsearch Query"});

		_assertSearch(
			"[this coca looks like a kind of drink, this looks like a kind " +
				"of coca drink]");
	}

	@Test
	public void testSchedulingAware() throws Exception {
		_journalArticleBuilder.setTitle(
			"Article 1"
		).build();

		_updateConfigurationJSON(
			"queryConfiguration", JSONUtil.put("applyIndexerClauses", false));

		Calendar calendar = CalendarFactoryUtil.getCalendar();

		calendar.add(Calendar.DAY_OF_MONTH, +1);

		Date displayDate = calendar.getTime();

		_journalArticles.add(
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				PortalUtil.getClassNameId(JournalArticle.class),
				StringPool.BLANK, true,
				HashMapBuilder.put(
					LocaleUtil.US, "Article Scheduled"
				).build(),
				null,
				HashMapBuilder.put(
					LocaleUtil.US, StringPool.BLANK
				).build(),
				null, LocaleUtil.getSiteDefault(), displayDate, null, false,
				true, _serviceContext));

		Map<String, Object> textMatchOverMultipleFields =
			_getTextMatchOverMultipleFields();

		textMatchOverMultipleFields.replace(
			"fields", new String[] {"title_${context.language_id}^2"});

		_updateElementInstancesJSON(
			new Object[] {textMatchOverMultipleFields},
			new String[] {"Text Match Over Multiple Fields"});

		_keywords = "Article";

		_assertSearchIgnoreRelevance("[Article 1, Article Scheduled]");

		_updateElementInstancesJSON(
			new Object[] {textMatchOverMultipleFields, null},
			new String[] {
				"Text Match Over Multiple Fields", "Scheduling Aware"
			});

		_assertSearchIgnoreRelevance("[Article 1]");
	}

	@Test
	public void testSearch() throws Exception {
		_journalArticleBuilder.setTitle(
			"Cafe Rio"
		).setContent(
			"Los Angeles"
		).build();

		_journalArticleBuilder.setTitle(
			"Cloud Cafe"
		).setContent(
			"Orange County"
		).build();

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"occur", "should"
				).put(
					"query",
					SXPBlueprintSearchResultTestUtil.getMatchQueryJSONObject(
						200, "los angeles")
				).build(),
				_getTextMatchOverMultipleFields()
			},
			new String[] {
				"Paste Any Elasticsearch Query",
				"Text Match Over Multiple Fields"
			});

		_keywords = "cafe";

		_assertSearchIgnoreRelevance("[Cafe Rio, Cloud Cafe]");

		_updateElementInstancesJSON(null, null);

		_assertSearchIgnoreRelevance("[Cafe Rio, Cloud Cafe]");

		_journalArticleBuilder.setTitle(
			"Coca Cola"
		).build();

		_journalArticleBuilder.setTitle(
			"Pepsi Cola"
		).build();

		_keywords = "cola +coca";

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 1
				).put(
					"fields", SXPBlueprintSearchResultTestUtil.FIELDS
				).put(
					"operator", "and"
				).build()
			},
			new String[] {"Search with Query String Syntax"});

		_assertSearch("[Coca Cola]");

		_keywords = "cola -coca";

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"boost", 1
				).put(
					"fields", SXPBlueprintSearchResultTestUtil.FIELDS
				).put(
					"operator", "and"
				).build()
			},
			new String[] {"Search with Query String Syntax"});

		_assertSearch("[Pepsi Cola]");
	}

	@FeatureFlag("LPS-129412")
	@Test
	public void testSearchableAssetTypesWithSubtype() throws Exception {
		JournalTestUtil.addArticle(
			_group.getGroupId(), "Basic Article",
			RandomTestUtil.randomString());

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		JournalArticleLocalServiceUtil.addArticle(
			null, _user.getUserId(), _group.getGroupId(), 0,
			HashMapBuilder.put(
				LocaleUtil.US, "Custom Article"
			).build(),
			null, DDMStructureTestUtil.getSampleStructuredContent(),
			ddmStructure.getStructureId(), StringPool.BLANK, _serviceContext);

		_keywords = "Article";

		_assertSearchIgnoreRelevance("[Basic Article, Custom Article]");

		String searchableAssetType = StringBundler.concat(
			JournalArticle.class.getName(), "&&",
			_group.getExternalReferenceCode(), "&&",
			ddmStructure.getExternalReferenceCode());

		_updateConfigurationJSON(
			"generalConfiguration",
			JSONUtil.put(
				"searchableAssetTypes", JSONUtil.put(searchableAssetType)));

		_assertSearch("[Custom Article]");
	}

	@Test
	public void testStagingAware() throws Exception {
		_updateConfigurationJSON(
			"queryConfiguration", JSONUtil.put("applyIndexerClauses", false));

		_enableLocalStaging();

		Group stagingGroup = _group.getStagingGroup();

		_journalArticleBuilder.setTitle(
			"Article 1"
		).build();

		_journalArticleBuilder.setTitle(
			"Article 2"
		).build();

		_journalArticleBuilder.setTitle(
			"Staged Article"
		).setGroup(
			stagingGroup
		).build();

		Map<String, Object> textMatchOverMultipleFields =
			_getTextMatchOverMultipleFields();

		textMatchOverMultipleFields.replace(
			"fields", new String[] {"title_${context.language_id}^2"});

		_updateElementInstancesJSON(
			new Object[] {textMatchOverMultipleFields},
			new String[] {"Text Match Over Multiple Fields"});

		_keywords = "Article";

		_assertSearchIgnoreRelevance("[Article 1, Article 2, Staged Article]");

		_updateElementInstancesJSON(
			new Object[] {textMatchOverMultipleFields, null},
			new String[] {"Text Match Over Multiple Fields", "Staging Aware"});

		_assertSearchIgnoreRelevance("[Article 1, Article 2]");
	}

	@Test
	public void testTextMatchOverMultipleFields_bestFields() throws Exception {
		_journalArticleBuilder.setTitle(
			"coca cola"
		).build();

		_journalArticleBuilder.setTitle(
			"coca coca"
		).setContent(
			"cola cola"
		).build();

		_updateElementInstancesJSON(
			new Object[] {_getTextMatchOverMultipleFields()},
			new String[] {"Text Match Over Multiple Fields"});

		_keywords = "coca cola";

		_assertSearch("[coca cola, coca coca]");
	}

	@Test
	public void testTextMatchOverMultipleFields_boolPrefix() throws Exception {
		_journalArticleBuilder.setTitle(
			"lorem ipsum sit"
		).setContent(
			"ipsum sit sit"
		).build();

		_journalArticleBuilder.setTitle(
			"lorem ipsum dolor"
		).setContent(
			"ipsum sit"
		).build();

		_journalArticleBuilder.setTitle(
			"amet"
		).setContent(
			"ipsum sit sit"
		).build();

		_journalArticleBuilder.setTitle(
			"nunquis"
		).setContent(
			"non-lorem ipsum sit"
		).build();

		Map<String, Object> textMatchOverMultipleFields =
			_getTextMatchOverMultipleFields();

		textMatchOverMultipleFields.replace("fuzziness", "0");
		textMatchOverMultipleFields.replace("operator", "and");
		textMatchOverMultipleFields.replace("type", "bool_prefix");

		_updateElementInstancesJSON(
			new Object[] {textMatchOverMultipleFields},
			new String[] {"Text Match Over Multiple Fields"});

		_keywords = "lorem dol";

		_assertSearchIgnoreRelevance("[lorem ipsum dolor]");

		textMatchOverMultipleFields.replace("operator", "or");

		_updateElementInstancesJSON(
			new Object[] {textMatchOverMultipleFields},
			new String[] {"Text Match Over Multiple Fields"});

		_assertSearchIgnoreRelevance(
			"[lorem ipsum dolor, lorem ipsum sit, nunquis]");
	}

	@Test
	public void testTextMatchOverMultipleFields_crossFields() throws Exception {
		_journalArticleBuilder.setTitle(
			"alpha beta"
		).setContent(
			"foxtrot, golf"
		).build();

		_journalArticleBuilder.setTitle(
			"alpha edison"
		).setContent(
			"hotel golf"
		).build();

		_journalArticleBuilder.setTitle(
			"beta charlie"
		).setContent(
			"alpha"
		).build();

		_journalArticleBuilder.setTitle(
			"edison india"
		).setContent(
			"beta"
		).build();

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"occur", "must"
				).put(
					"query",
					SXPBlueprintSearchResultTestUtil.
						getMultiMatchQueryJSONObject(
							1, SXPBlueprintSearchResultTestUtil.FIELDS, null,
							"and", "${keywords}", "cross_fields")
				).build()
			},
			new String[] {"Paste Any Elasticsearch Query"});

		_keywords = "alpha golf";

		_assertSearchIgnoreRelevance("[alpha beta, alpha edison]");

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"occur", "must"
				).put(
					"query",
					SXPBlueprintSearchResultTestUtil.
						getMultiMatchQueryJSONObject(
							1, SXPBlueprintSearchResultTestUtil.FIELDS, null,
							"or", "${keywords}", "cross_fields")
				).build()
			},
			new String[] {"Paste Any Elasticsearch Query"});

		_assertSearchIgnoreRelevance(
			"[alpha beta, alpha edison, beta charlie]");
	}

	@Test
	public void testTextMatchOverMultipleFields_mostFields() throws Exception {
		_updateConfigurationJSON(
			"queryConfiguration", JSONUtil.put("applyIndexerClauses", false));

		_journalArticleBuilder.setTitleMap(
			HashMapBuilder.put(
				LocaleUtil.GERMANY, "coca cola most fields"
			).put(
				LocaleUtil.SPAIN, "coca cola most fields"
			).put(
				LocaleUtil.US, "coca cola most fields"
			).build()
		).build();

		_journalArticleBuilder.setTitle(
			"coca cola"
		).build();

		Map<String, Object> textMatchOverMultipleFields =
			_getTextMatchOverMultipleFields();

		String[] fields = {"title_en_US", "title_de_DE", "title_es_ES"};

		textMatchOverMultipleFields.replace("type", "most_fields");
		textMatchOverMultipleFields.replace("fields", fields);

		_updateElementInstancesJSON(
			new Object[] {textMatchOverMultipleFields},
			new String[] {"Text Match Over Multiple Fields"});

		_keywords = "coca cola";

		_assertSearch("[coca cola most fields, coca cola]");
	}

	@Test
	public void testTextMatchOverMultipleFields_phrase() throws Exception {
		_journalArticleBuilder.setTitle(
			"listen something"
		).setContent(
			"do not listen to birds"
		).build();

		_journalArticleBuilder.setTitle(
			"listen to birds"
		).setContent(
			"listen listen to birds"
		).build();

		_journalArticleBuilder.setTitle(
			"listen to planes"
		).setContent(
			"listen to birds"
		).build();

		_journalArticleBuilder.setTitle(
			"silence"
		).setContent(
			"listen listen to birds"
		).build();

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"occur", "must"
				).put(
					"query",
					SXPBlueprintSearchResultTestUtil.
						getMultiMatchQueryJSONObject(
							1, SXPBlueprintSearchResultTestUtil.FIELDS, null,
							null, "${keywords}", "phrase")
				).build()
			},
			new String[] {"Paste Any Elasticsearch Query"});

		_keywords = "listen listen";

		_assertSearch("[listen to birds, silence]");
	}

	@Test
	public void testTextMatchOverMultipleFields_phrasePrefix()
		throws Exception {

		_journalArticleBuilder.setTitle(
			"clouds"
		).setContent(
			"simple things are beautiful sometimes"
		).build();

		_journalArticleBuilder.setTitle(
			"watch birds on the sky"
		).setContent(
			"simple things are beautiful"
		).build();

		_journalArticleBuilder.setTitle(
			"watch planes on the sky"
		).setContent(
			"simple things are not good"
		).build();

		_journalArticleBuilder.setTitle(
			"watch trains"
		).setContent(
			"simple things are bad"
		).build();

		_updateElementInstancesJSON(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"occur", "must"
				).put(
					"query",
					SXPBlueprintSearchResultTestUtil.
						getMultiMatchQueryJSONObject(
							1, SXPBlueprintSearchResultTestUtil.FIELDS, null,
							null, "${keywords}", "phrase_prefix")
				).build()
			},
			new String[] {"Paste Any Elasticsearch Query"});

		_keywords = "simple things are beau";

		_assertSearch("[watch birds on the sky, clouds]");
	}

	@Rule
	public TestName testName = new TestName();

	private AssetCategory _addAssetCategory(String title, User user)
		throws Exception {

		if (_assetVocabulary == null) {
			_assetVocabulary =
				AssetVocabularyLocalServiceUtil.addDefaultVocabulary(
					_group.getGroupId());
		}

		_assetCategory = AssetCategoryLocalServiceUtil.addCategory(
			user.getUserId(), _group.getGroupId(), title,
			_assetVocabulary.getVocabularyId(), _serviceContext);

		return _assetCategory;
	}

	private void _addFileEntry(String sourceFileName, String extension)
		throws Exception {

		Class<?> clazz = getClass();

		String clazzName = clazz.getName();

		String fileName = StringBundler.concat(
			"dependencies/", clazz.getSimpleName(), StringPool.PERIOD,
			testName.getMethodName(), extension);

		DLAppLocalServiceUtil.addFileEntry(
			null, _user.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, sourceFileName,
			ContentTypes.APPLICATION_PDF,
			FileUtil.getBytes(
				SXPBlueprintSearchResultTest.class,
				StringUtils.replace(clazzName, ".", "/") + fileName),
			null, null, null, _serviceContext);
	}

	private Group _addGroup() throws Exception {
		Group group = GroupTestUtil.addGroup(
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			RandomTestUtil.randomString(), _serviceContext);

		_groups.add(group);

		return group;
	}

	private User _addGroupUser(Group group, String roleName) throws Exception {
		Role role = RoleTestUtil.addRole(roleName, RoleConstants.TYPE_REGULAR);

		return UserTestUtil.addGroupUser(group, role.getName());
	}

	private SegmentsEntry _addSegmentsEntry(User user) throws Exception {
		Criteria criteria = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria, String.format("(firstName eq '%s')", user.getFirstName()),
			Criteria.Conjunction.AND);

		return SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));
	}

	private void _assertSearch(
			String expected,
			Consumer<SearchRequestBuilder>... searchRequestBuilderConsumer)
		throws Exception {

		SearchResponse searchResponse = _getSearchResponseSearchPage(
			searchRequestBuilderConsumer);

		String message =
			_getScore(searchResponse) + searchResponse.getRequestString();

		DocumentsAssert.assertValues(
			message, searchResponse.getDocuments(), "title_en_US", expected);

		if (!Objects.equals(_sxpBlueprint.getElementInstancesJSON(), "{}")) {
			searchResponse = _getSearchResponsePreview(
				searchRequestBuilderConsumer);

			message =
				_getScore(searchResponse) + searchResponse.getRequestString();

			DocumentsAssert.assertValues(
				message, searchResponse.getDocuments(), "title_en_US",
				expected);
		}
	}

	private void _assertSearchIgnoreRelevance(
			String expected,
			Consumer<SearchRequestBuilder>... searchRequestBuilderConsumer)
		throws Exception {

		SearchResponse searchResponse = _getSearchResponseSearchPage(
			searchRequestBuilderConsumer);

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchResponse.getRequestString(), searchResponse.getDocuments(),
			"title_en_US", expected);

		if (!Objects.equals(_sxpBlueprint.getElementInstancesJSON(), "{}")) {
			searchResponse = _getSearchResponsePreview(
				searchRequestBuilderConsumer);

			DocumentsAssert.assertValuesIgnoreRelevance(
				searchResponse.getRequestString(),
				searchResponse.getDocuments(), "title_en_US", expected);
		}
	}

	private void _enableLocalStaging() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		Map<String, Serializable> attributes = serviceContext.getAttributes();

		attributes.putAll(
			ExportImportConfigurationParameterMapFactoryUtil.
				buildParameterMap());

		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), _group, false, false, serviceContext);
	}

	private ConfigurationTemporarySwapper _getConfigurationTemporarySwapper(
			String apiKey, String apiURL, String enabled)
		throws Exception {

		return new ConfigurationTemporarySwapper(
			"com.liferay.search.experiences.internal.configuration." +
				"IpstackConfiguration",
			HashMapDictionaryBuilder.put(
				"apiKey", (Object)apiKey
			).put(
				"apiURL", apiURL
			).put(
				"enabled", enabled
			).build());
	}

	private String _getScore(SearchResponse searchResponse) {
		SearchHits searchHits = searchResponse.getSearchHits();

		List<SearchHit> searchHitsList = searchHits.getSearchHits();

		String message = StringPool.NEW_LINE;

		for (SearchHit searchHit : searchHitsList) {
			Document document = searchHit.getDocument();

			Map<String, Field> fields = document.getFields();

			message = StringBundler.concat(
				message, "Score: ", searchHit.getScore(), " Title: \"",
				fields.get("title_en_US"), StringPool.QUOTE,
				StringPool.NEW_LINE);
		}

		return message;
	}

	private SearchResponse _getSearchResponsePreview(
			Consumer<SearchRequestBuilder>... searchRequestBuilderConsumer)
		throws Exception {

		com.liferay.search.experiences.rest.dto.v1_0.SXPBlueprint sxpBlueprint =
			new com.liferay.search.experiences.rest.dto.v1_0.SXPBlueprint() {
				{
					configuration = ConfigurationUtil.toConfiguration(
						_sxpBlueprint.getConfigurationJSON());
					elementInstances = ElementInstanceUtil.toElementInstances(
						_sxpBlueprint.getElementInstancesJSON());
				}
			};

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				TestPropsValues.getCompanyId()
			).emptySearchEnabled(
				true
			).queryString(
				_keywords
			).withSearchContext(
				_searchContext -> {
					_searchContext.setAttribute(
						"scope_group_id", _group.getGroupId());
					_searchContext.setAttribute(
						"search.experiences.scope.group.id",
						_group.getGroupId());
					_searchContext.setTimeZone(_user.getTimeZone());
					_searchContext.setUserId(_serviceContext.getUserId());
				}
			).withSearchRequestBuilder(
				searchRequestBuilderConsumer
			);

		_sxpBlueprintSearchRequestEnhancer.enhance(
			searchRequestBuilder,
			String.valueOf(SXPBlueprintUtil.unpack(sxpBlueprint)));

		return _searcher.search(searchRequestBuilder.build());
	}

	private SearchResponse _getSearchResponseSearchPage(
			Consumer<SearchRequestBuilder>... searchRequestBuilderConsumer)
		throws Exception {

		return _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				TestPropsValues.getCompanyId()
			).emptySearchEnabled(
				true
			).queryString(
				_keywords
			).withSearchContext(
				_searchContext -> {
					_searchContext.setAttribute(
						"search.experiences.blueprint.external.reference.code",
						_sxpBlueprint.getExternalReferenceCode());
					_searchContext.setAttribute(
						"search.experiences.scope.group.id",
						_group.getGroupId());
					_searchContext.setTimeZone(_user.getTimeZone());
					_searchContext.setUserId(_serviceContext.getUserId());
				}
			).withSearchRequestBuilder(
				searchRequestBuilderConsumer
			).build());
	}

	private Map<String, Object> _getTextMatchOverMultipleFields() {
		return HashMapBuilder.<String, Object>put(
			"boost", 1
		).put(
			"fields", SXPBlueprintSearchResultTestUtil.FIELDS
		).put(
			"fuzziness", "AUTO"
		).put(
			"keywords", "${keywords}"
		).put(
			"minimum_should_match", 0
		).put(
			"operator", "or"
		).put(
			"slop", 0
		).put(
			"type", "best_fields"
		).build();
	}

	private String[] _getTimeOfDayAndNextTimeOfDay(LocalTime localTime) {
		if (_isBetween(localTime, _LOCAL_TIME_04, _LOCAL_TIME_12)) {
			return new String[] {"morning", "afternoon"};
		}
		else if (_isBetween(localTime, _LOCAL_TIME_12, _LOCAL_TIME_17)) {
			return new String[] {"afternoon", "evening"};
		}
		else if (_isBetween(localTime, _LOCAL_TIME_17, _LOCAL_TIME_20)) {
			return new String[] {"evening", "night"};
		}

		return new String[] {"night", "morning"};
	}

	private boolean _isBetween(
		LocalTime localTime, LocalTime startLocalTime, LocalTime endLocalTime) {

		if (!localTime.isBefore(startLocalTime) &&
			localTime.isBefore(endLocalTime)) {

			return true;
		}

		return false;
	}

	private void _setCurrentUser(User user) {
		_serviceContext.setUserId(user.getUserId());
	}

	private void _updateConfigurationJSON(
			String configurationName, JSONObject jsonObject)
		throws Exception {

		JSONObject configurationJSONObject = JSONFactoryUtil.createJSONObject(
			_sxpBlueprint.getConfigurationJSON());

		_sxpBlueprint.setConfigurationJSON(
			configurationJSONObject.put(
				configurationName, jsonObject
			).toString());

		_updateSXPBlueprint();
	}

	private void _updateElementInstancesJSON(
			Object[] configurationValuesArray, String[] sxpElementNames)
		throws Exception {

		String elementInstancesJSON = "{}";

		if (sxpElementNames != null) {
			elementInstancesJSON =
				SXPBlueprintSearchResultTestUtil.getElementInstancesJSON(
					configurationValuesArray, sxpElementNames, _sxpElements);
		}

		_sxpBlueprint.setElementInstancesJSON(elementInstancesJSON);

		_updateSXPBlueprint();
	}

	private void _updateSXPBlueprint() throws Exception {
		_sxpBlueprintLocalService.updateSXPBlueprint(
			_sxpBlueprint.getExternalReferenceCode(), _sxpBlueprint.getUserId(),
			_sxpBlueprint.getSXPBlueprintId(),
			_sxpBlueprint.getConfigurationJSON(),
			_sxpBlueprint.getDescriptionMap(),
			_sxpBlueprint.getElementInstancesJSON(),
			_sxpBlueprint.getSchemaVersion(), _sxpBlueprint.getTitleMap(),
			_serviceContext);
	}

	private static final LocalTime _LOCAL_TIME_04 = LocalTime.of(4, 0, 0);

	private static final LocalTime _LOCAL_TIME_12 = LocalTime.of(12, 0, 0);

	private static final LocalTime _LOCAL_TIME_17 = LocalTime.of(17, 0, 0);

	private static final LocalTime _LOCAL_TIME_20 = LocalTime.of(20, 0, 0);

	@Inject
	private static JournalArticleLocalService _journalArticleLocalService;

	private static List<SXPElement> _sxpElements;

	@Inject
	private static UserLocalService _userLocalService;

	private AssetCategory _assetCategory;
	private AssetTag _assetTag;
	private AssetVocabulary _assetVocabulary;
	private final JSONObject _configurationJSONObject = JSONUtil.put(
		"generalConfiguration",
		JSONUtil.put(
			"searchableAssetTypes",
			JSONUtil.put("com.liferay.journal.model.JournalArticle"))
	).put(
		"queryConfiguration", JSONUtil.put("applyIndexerClauses", true)
	);

	@DeleteAfterTestRun
	private final List<ExpandoColumn> _expandoColumns = new ArrayList<>();

	@DeleteAfterTestRun
	private final List<ExpandoTable> _expandoTables = new ArrayList<>();

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups = new ArrayList<>();

	private JournalArticleBuilder _journalArticleBuilder;
	private final List<JournalArticle> _journalArticles = new ArrayList<>();
	private JournalFolder _journalFolder;
	private String _keywords;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private SXPBlueprint _sxpBlueprint;

	@Inject
	private SXPBlueprintLocalService _sxpBlueprintLocalService;

	@Inject
	private SXPBlueprintSearchRequestEnhancer
		_sxpBlueprintSearchRequestEnhancer;

	private User _user;

	@Inject(
		filter = "segments.criteria.contributor.key=user",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor _userSegmentsCriteriaContributor;

	private class JournalArticleBuilder {

		public JournalArticleBuilder(
			Group group, List<JournalArticle> journalArticles,
			ServiceContext serviceContext, User user) {

			_journalArticles = journalArticles;
			_serviceContext = serviceContext;

			_defaultGroup = group;
			_defaultUser = user;

			_reset();
		}

		public void build() throws Exception {
			if (_assetCategory != null) {
				_serviceContext.setAssetCategoryIds(
					new long[] {_assetCategory.getCategoryId()});
			}

			if (_assetTag != null) {
				_serviceContext.setAssetTagNames(
					new String[] {_assetTag.getName()});
			}

			if ((_latitude != 200) && (_longitude != 200)) {
				_serviceContext.setExpandoBridgeAttributes(
					Collections.singletonMap(
						_fieldName,
						JSONUtil.put(
							"latitude", _latitude
						).put(
							"longitude", _longitude
						).toString()));
			}

			long journalFolderId = 0;

			if (_journalFolder != null) {
				journalFolderId = _journalFolder.getFolderId();
			}

			if (_titleMap != null) {
				_journalArticles.add(
					_addJournalArticle(
						_getGroupId(), journalFolderId, _titleMap, _content,
						_workflowEnabled, _approved));
			}
			else {
				_journalArticles.add(
					_addJournalArticle(
						_getGroupId(), journalFolderId, _title, _content,
						_workflowEnabled, _approved));
			}

			_reset();
		}

		public JournalArticleBuilder setApproved(boolean approved) {
			_approved = approved;

			return this;
		}

		public JournalArticleBuilder setAssetCategory(
			AssetCategory assetCategory) {

			_assetCategory = assetCategory;

			return this;
		}

		public JournalArticleBuilder setAssetTag(AssetTag assetTag) {
			_assetTag = assetTag;

			return this;
		}

		public JournalArticleBuilder setContent(String content) {
			_content = content;

			return this;
		}

		public JournalArticleBuilder setGeolocation(
			String fieldName, double latitude, double longitude) {

			_fieldName = fieldName;
			_latitude = latitude;
			_longitude = longitude;

			return this;
		}

		public JournalArticleBuilder setGroup(Group group) {
			_group = group;

			return this;
		}

		public JournalArticleBuilder setJournalFolder(
			JournalFolder journalFolder) {

			_journalFolder = journalFolder;

			return this;
		}

		public JournalArticleBuilder setJournalFolder(String title)
			throws Exception {

			_journalFolder = JournalFolderServiceUtil.addFolder(
				null, _getGroupId(), 0, title, StringPool.BLANK,
				_serviceContext);

			return this;
		}

		public JournalArticleBuilder setTitle(String title) {
			_title = title;

			return this;
		}

		public JournalArticleBuilder setTitleMap(Map<Locale, String> titleMap) {
			_titleMap = titleMap;

			return this;
		}

		public JournalArticleBuilder setWorkflowEnabled(
			boolean workflowEnabled) {

			_workflowEnabled = workflowEnabled;

			return this;
		}

		private JournalArticle _addJournalArticle(
				long groupId, long folderId, Map<Locale, String> titleMap,
				String content, boolean workflowEnabled, boolean approved)
			throws Exception {

			return JournalTestUtil.addArticle(
				groupId, folderId,
				PortalUtil.getClassNameId(JournalArticle.class), titleMap, null,
				HashMapBuilder.put(
					LocaleUtil.US, content
				).build(),
				LocaleUtil.getSiteDefault(), workflowEnabled, approved,
				_serviceContext);
		}

		private JournalArticle _addJournalArticle(
				long groupId, long folderId, String name, String content,
				boolean workflowEnabled, boolean approved)
			throws Exception {

			return JournalTestUtil.addArticle(
				groupId, folderId,
				PortalUtil.getClassNameId(JournalArticle.class),
				HashMapBuilder.put(
					LocaleUtil.US, name
				).build(),
				null,
				HashMapBuilder.put(
					LocaleUtil.US, content
				).build(),
				LocaleUtil.getSiteDefault(), workflowEnabled, approved,
				_serviceContext);
		}

		private long _getGroupId() {
			if (_group != null) {
				return _group.getGroupId();
			}

			return _defaultGroup.getGroupId();
		}

		private void _reset() {
			_approved = true;
			_assetCategory = null;
			_assetTag = null;
			_content = StringPool.BLANK;
			_fieldName = StringPool.BLANK;
			_group = null;
			_journalFolder = null;
			_latitude = 200;
			_longitude = 200;
			_title = StringPool.BLANK;
			_titleMap = null;
			_workflowEnabled = false;
		}

		private boolean _approved;
		private AssetCategory _assetCategory;
		private AssetTag _assetTag;
		private String _content;
		private final Group _defaultGroup;
		private final User _defaultUser;
		private String _fieldName;
		private Group _group;
		private final List<JournalArticle> _journalArticles;
		private JournalFolder _journalFolder;
		private double _latitude;
		private double _longitude;
		private ServiceContext _serviceContext;
		private String _title;
		private Map<Locale, String> _titleMap;
		private boolean _workflowEnabled;

	}

}