/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.exception.DuplicateFragmentCollectionKeyException;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.headless.admin.fragment.client.dto.v1_0.FragmentSet;
import com.liferay.headless.admin.fragment.client.pagination.Page;
import com.liferay.headless.admin.fragment.client.problem.Problem;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.props.test.util.PropsTemporarySwapper;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.InputStream;

import java.text.DateFormat;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-39244"), @FeatureFlag("LPD-57283")}
)
@RunWith(Arquillian.class)
public class FragmentSetResourceTest extends BaseFragmentSetResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_depotEntry = _addDepotEntry(DepotConstants.TYPE_DESIGN_LIBRARY);
	}

	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();

		_testBatchEngineDeleteImportTask();
	}

	@Override
	@Test
	@TestInfo("LPD-97408")
	public void testDeleteDesignLibraryFragmentSet() throws Exception {
		super.testDeleteDesignLibraryFragmentSet();

		_testDeleteDesignLibraryFragmentSetWithAssetLibraryExternalReferenceCodeProblemException();
		_testDeleteDesignLibraryFragmentSetWithFeatureFlagDisabledProblemException();
		_testDeleteDesignLibraryFragmentSetWithSiteExternalReferenceCodeProblemException();
	}

	@Override
	@Test
	public void testDeleteSiteFragmentSet() throws Exception {
		super.testDeleteSiteFragmentSet();

		FragmentSet fragmentSet = testGetSiteFragmentSetsPage_addFragmentSet(
			testGroup.getExternalReferenceCode(), randomFragmentSet());

		fragmentSetResource.deleteSiteFragmentSet(
			testGroup.getExternalReferenceCode(),
			fragmentSet.getExternalReferenceCode());

		Assert.assertNull(
			_fragmentCollectionLocalService.
				fetchFragmentCollectionByExternalReferenceCode(
					fragmentSet.getExternalReferenceCode(),
					testGroup.getGroupId()));
	}

	@Override
	@Test
	public void testGetSiteFragmentSetsPage() throws Exception {
		super.testGetSiteFragmentSetsPage();

		FragmentSet nonmarketplaceFragmentSet = randomFragmentSet();

		nonmarketplaceFragmentSet.setMarketplace(false);

		nonmarketplaceFragmentSet = testGetSiteFragmentSetsPage_addFragmentSet(
			testGroup.getExternalReferenceCode(), nonmarketplaceFragmentSet);

		FragmentSet marketplaceFragmentSet = randomFragmentSet();

		marketplaceFragmentSet.setMarketplace(true);

		marketplaceFragmentSet = testGetSiteFragmentSetsPage_addFragmentSet(
			testGroup.getExternalReferenceCode(), marketplaceFragmentSet);

		_assertGetSiteFragmentSetsPageWithFilter(
			nonmarketplaceFragmentSet, "marketplace eq false",
			marketplaceFragmentSet);
		_assertGetSiteFragmentSetsPageWithFilter(
			marketplaceFragmentSet, "marketplace eq true",
			nonmarketplaceFragmentSet);
	}

	@Override
	@Test
	public void testPostSiteFragmentSet() throws Exception {
		FragmentSet randomFragmentSet = randomFragmentSet();

		FragmentSet postFragmentSet = testPostSiteFragmentSet_addFragmentSet(
			randomFragmentSet);

		assertEquals(randomFragmentSet, postFragmentSet);
		assertValid(postFragmentSet);

		FragmentSet duplicateERCFragmentSet = randomFragmentSet();

		duplicateERCFragmentSet.setExternalReferenceCode(
			postFragmentSet.getExternalReferenceCode());

		_assertProblemException(
			"CONFLICT",
			_language.get(
				LocaleUtil.getDefault(),
				"this-external-reference-code-is-already-in-use"),
			() -> fragmentSetResource.postSiteFragmentSet(
				testGroup.getExternalReferenceCode(), duplicateERCFragmentSet));

		FragmentSet duplicateKeyFragmentSet = randomFragmentSet();

		duplicateKeyFragmentSet.setKey(postFragmentSet.getKey());

		_assertProblemException(
			"CONFLICT",
			_language.format(
				LocaleUtil.getDefault(),
				"a-fragment-set-with-the-key-x-already-exists",
				duplicateKeyFragmentSet.getKey()),
			() -> fragmentSetResource.postSiteFragmentSet(
				testGroup.getExternalReferenceCode(), duplicateKeyFragmentSet));

		FragmentSet invalidNameFragmentSet = randomFragmentSet();

		invalidNameFragmentSet.setName(
			RandomTestUtil.randomString() + StringPool.PERIOD +
				RandomTestUtil.randomString());

		_assertProblemException(
			"BAD_REQUEST",
			_language.get(LocaleUtil.getDefault(), "name-is-invalid"),
			() -> fragmentSetResource.postSiteFragmentSet(
				testGroup.getExternalReferenceCode(), invalidNameFragmentSet));

		_testPostSiteFragmentSetBatch();
	}

	@Override
	@Test
	public void testPutSiteFragmentSet() throws Exception {
		FragmentSet fragmentSet = randomFragmentSet();

		FragmentSet putFragmentSet = fragmentSetResource.putSiteFragmentSet(
			testGroup.getExternalReferenceCode(),
			fragmentSet.getExternalReferenceCode(), fragmentSet);

		assertEquals(fragmentSet, putFragmentSet);
		assertValid(putFragmentSet);

		FragmentSet getFragmentSet = fragmentSetResource.getSiteFragmentSet(
			testGroup.getExternalReferenceCode(),
			putFragmentSet.getExternalReferenceCode());

		assertEquals(fragmentSet, getFragmentSet);
		assertValid(getFragmentSet);

		fragmentSet.setDescription(RandomTestUtil.randomString());
		fragmentSet.setName(RandomTestUtil.randomString());

		putFragmentSet = fragmentSetResource.putSiteFragmentSet(
			testGroup.getExternalReferenceCode(),
			fragmentSet.getExternalReferenceCode(), fragmentSet);

		assertEquals(fragmentSet, putFragmentSet);
		assertValid(putFragmentSet);

		getFragmentSet = fragmentSetResource.getSiteFragmentSet(
			testGroup.getExternalReferenceCode(),
			putFragmentSet.getExternalReferenceCode());

		assertEquals(fragmentSet, getFragmentSet);
		assertValid(getFragmentSet);

		String originalExternalReferenceCode =
			fragmentSet.getExternalReferenceCode();
		String originalKey = fragmentSet.getKey();

		Boolean originalMarketplace = fragmentSet.getMarketplace();

		fragmentSet.setDescription((String)null);
		fragmentSet.setExternalReferenceCode(RandomTestUtil.randomString());
		fragmentSet.setKey(RandomTestUtil.randomString());
		fragmentSet.setMarketplace(!originalMarketplace);

		putFragmentSet = fragmentSetResource.putSiteFragmentSet(
			testGroup.getExternalReferenceCode(), originalExternalReferenceCode,
			fragmentSet);

		Assert.assertTrue(Validator.isNull(putFragmentSet.getDescription()));
		Assert.assertEquals(
			originalExternalReferenceCode,
			putFragmentSet.getExternalReferenceCode());
		Assert.assertEquals(originalKey, putFragmentSet.getKey());
		Assert.assertEquals(
			originalMarketplace, putFragmentSet.getMarketplace());

		FragmentSet duplicateKeyFragmentSet = randomFragmentSet();

		duplicateKeyFragmentSet.setKey(originalKey);

		_assertProblemException(
			"CONFLICT",
			_language.format(
				LocaleUtil.getDefault(),
				"a-fragment-set-with-the-key-x-already-exists",
				duplicateKeyFragmentSet.getKey()),
			() -> fragmentSetResource.putSiteFragmentSet(
				testGroup.getExternalReferenceCode(),
				duplicateKeyFragmentSet.getExternalReferenceCode(),
				duplicateKeyFragmentSet));

		FragmentSet nullNameFragmentSet =
			testPutSiteFragmentSet_addFragmentSet();

		nullNameFragmentSet.setName((String)null);

		_assertProblemException(
			"BAD_REQUEST",
			_language.get(LocaleUtil.getDefault(), "name-is-invalid"),
			() -> fragmentSetResource.putSiteFragmentSet(
				testGroup.getExternalReferenceCode(),
				nullNameFragmentSet.getExternalReferenceCode(),
				nullNameFragmentSet));

		_testPutSiteFragmentSetBatch();
		_testPutSiteFragmentSetWithDates();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"description", "externalReferenceCode", "key", "marketplace", "name"
		};
	}

	@Override
	protected FragmentSet randomFragmentSet() throws Exception {
		FragmentSet fragmentSet = super.randomFragmentSet();

		fragmentSet.setDateCreated(new Date(System.currentTimeMillis()));
		fragmentSet.setDateModified(new Date(System.currentTimeMillis()));

		return fragmentSet;
	}

	@Override
	protected FragmentSet testDeleteDesignLibraryFragmentSet_addFragmentSet()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			_depotEntry.getGroup());

		return new FragmentSet() {
			{
				setExternalReferenceCode(
					fragmentCollection::getExternalReferenceCode);
			}
		};
	}

	@Override
	protected String
			testDeleteDesignLibraryFragmentSet_getDesignLibraryExternalReferenceCode()
		throws Exception {

		Group group = _depotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetSiteFragmentSetsPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		return new HashMap<>();
	}

	@Override
	protected FragmentSet testPostSiteFragmentSet_addFragmentSet(
			FragmentSet fragmentSet)
		throws Exception {

		return fragmentSetResource.postSiteFragmentSet(
			testGroup.getExternalReferenceCode(), fragmentSet);
	}

	private DepotEntry _addDepotEntry(int type) throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, type,
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId()));
	}

	private FragmentCollection _addFragmentCollection(Group group)
		throws Exception {

		return _fragmentCollectionLocalService.addFragmentCollection(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));
	}

	private void _assertFragmentCollection(FragmentSet fragmentSet, Group group)
		throws Exception {

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.
				getFragmentCollectionByExternalReferenceCode(
					fragmentSet.getExternalReferenceCode(), group.getGroupId());

		Assert.assertEquals(
			fragmentSet.getName(), fragmentCollection.getName());
		Assert.assertEquals(
			fragmentSet.getDescription(), fragmentCollection.getDescription());
	}

	private void _assertGetSiteFragmentSetsPageWithFilter(
			FragmentSet expectedFragmentSet, String filterString,
			FragmentSet notExpectedFragmentSet)
		throws Exception {

		Page<FragmentSet> page = fragmentSetResource.getSiteFragmentSetsPage(
			testGroup.getExternalReferenceCode(), filterString, null);

		List<FragmentSet> fragmentSets = (List<FragmentSet>)page.getItems();

		assertContains(expectedFragmentSet, fragmentSets);

		for (FragmentSet fragmentSet : fragmentSets) {
			Assert.assertNotEquals(
				notExpectedFragmentSet.getExternalReferenceCode(),
				fragmentSet.getExternalReferenceCode());
		}
	}

	private void _assertProblemException(
			String status, String title,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(status, problem.getStatus());
			Assert.assertEquals(title, problem.getTitle());
		}
	}

	private String _exportFragmentSetsToJSON(String siteExternalReferenceCode)
		throws Exception {

		JSONObject exportTaskJSONObject = _waitForExportFinish(
			"COMPLETED",
			HTTPTestUtil.invokeToJSONObject(
				null,
				"headless-admin-fragment/v1.0/sites/" +
					siteExternalReferenceCode +
						"/fragment-sets/export-batch?contentType=JSON",
				Http.Method.POST));

		try (InputStream inputStream = HTTPTestUtil.invokeToInputStream(
				null,
				StringBundler.concat(
					"headless-batch-engine/v1.0/export-task",
					"/by-external-reference-code/",
					exportTaskJSONObject.getString("externalReferenceCode"),
					"/content"),
				HashMapBuilder.put(
					HttpHeaders.ACCEPT, ContentTypes.APPLICATION_OCTET_STREAM
				).build(),
				Http.Method.GET)) {

			ZipInputStream zipInputStream = new ZipInputStream(inputStream);

			zipInputStream.getNextEntry();

			return StringUtil.read(zipInputStream);
		}
	}

	private void _testBatchEngineDeleteImportTask() throws Exception {
		FragmentSet fragmentSet1 = testPostSiteFragmentSet_addFragmentSet(
			randomFragmentSet());
		FragmentSet fragmentSet2 = testPostSiteFragmentSet_addFragmentSet(
			randomFragmentSet());

		waitForFinish(
			"COMPLETED",
			HTTPTestUtil.invokeToJSONObject(
				_exportFragmentSetsToJSON(testGroup.getExternalReferenceCode()),
				"headless-admin-fragment/v1.0/sites/" +
					irrelevantGroup.getExternalReferenceCode() +
						"/fragment-sets/batch?createStrategy=INSERT",
				Http.Method.POST));

		testBatchEngineDeleteImportTask_deleteFragmentSet(
			200, fragmentSet2.getExternalReferenceCode(),
			"siteExternalReferenceCode",
			irrelevantGroup.getExternalReferenceCode());

		_assertFragmentCollection(fragmentSet1, irrelevantGroup);

		Assert.assertNull(
			_fragmentCollectionLocalService.
				fetchFragmentCollectionByExternalReferenceCode(
					fragmentSet2.getExternalReferenceCode(),
					irrelevantGroup.getGroupId()));
	}

	private void _testDeleteDesignLibraryFragmentSetWithAssetLibraryExternalReferenceCodeProblemException()
		throws Exception {

		DepotEntry assetLibraryDepotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		Group group = assetLibraryDepotEntry.getGroup();

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> fragmentSetResource.deleteDesignLibraryFragmentSet(
				group.getExternalReferenceCode(),
				RandomTestUtil.randomString()));
	}

	private void _testDeleteDesignLibraryFragmentSetWithFeatureFlagDisabledProblemException()
		throws Exception {

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					FeatureFlagConstants.getKey("LPD-57283"),
					Boolean.FALSE.toString())) {

			Group group = _depotEntry.getGroup();

			FragmentCollection fragmentCollection = _addFragmentCollection(
				group);

			_assertProblemException(
				"BAD_REQUEST",
				"Feature flag LPD-57283 is disabled for company " +
					testCompany.getCompanyId(),
				() -> fragmentSetResource.deleteDesignLibraryFragmentSet(
					group.getExternalReferenceCode(),
					fragmentCollection.getExternalReferenceCode()));
		}
	}

	private void _testDeleteDesignLibraryFragmentSetWithSiteExternalReferenceCodeProblemException()
		throws Exception {

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> fragmentSetResource.deleteDesignLibraryFragmentSet(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString()));
	}

	private void _testPostSiteFragmentSetBatch() throws Exception {
		FragmentSet fragmentSet1 = testPostSiteFragmentSet_addFragmentSet(
			randomFragmentSet());
		FragmentSet fragmentSet2 = testPostSiteFragmentSet_addFragmentSet(
			randomFragmentSet());

		String fragmentSetsJSON = _exportFragmentSetsToJSON(
			testGroup.getExternalReferenceCode());

		waitForFinish(
			"COMPLETED",
			HTTPTestUtil.invokeToJSONObject(
				fragmentSetsJSON,
				"headless-admin-fragment/v1.0/sites/" +
					irrelevantGroup.getExternalReferenceCode() +
						"/fragment-sets/batch?createStrategy=INSERT",
				Http.Method.POST));

		_assertFragmentCollection(fragmentSet1, irrelevantGroup);
		_assertFragmentCollection(fragmentSet2, irrelevantGroup);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineImportTaskExecutorImpl",
				LoggerTestUtil.ERROR)) {

			waitForFinish(
				"FAILED",
				HTTPTestUtil.invokeToJSONObject(
					fragmentSetsJSON,
					"headless-admin-fragment/v1.0/sites/" +
						irrelevantGroup.getExternalReferenceCode() +
							"/fragment-sets/batch?createStrategy=INSERT",
					Http.Method.POST));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertFalse(logEntries.toString(), logEntries.isEmpty());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertTrue(
				String.valueOf(throwable),
				throwable instanceof DuplicateFragmentCollectionKeyException);
		}

		_assertFragmentCollection(fragmentSet1, irrelevantGroup);
		_assertFragmentCollection(fragmentSet2, irrelevantGroup);
	}

	private void _testPutSiteFragmentSetBatch() throws Exception {
		FragmentSet fragmentSet1 = testPutSiteFragmentSet_addFragmentSet();
		FragmentSet fragmentSet2 = testPutSiteFragmentSet_addFragmentSet();

		waitForFinish(
			"COMPLETED",
			HTTPTestUtil.invokeToJSONObject(
				_exportFragmentSetsToJSON(testGroup.getExternalReferenceCode()),
				"headless-admin-fragment/v1.0/sites/" +
					irrelevantGroup.getExternalReferenceCode() +
						"/fragment-sets/batch?createStrategy=INSERT",
				Http.Method.POST));

		fragmentSet2.setDescription(RandomTestUtil.randomString());
		fragmentSet2.setName(RandomTestUtil.randomString());

		FragmentSet putFragmentSet = fragmentSetResource.putSiteFragmentSet(
			testGroup.getExternalReferenceCode(),
			fragmentSet2.getExternalReferenceCode(), fragmentSet2);

		waitForFinish(
			"COMPLETED",
			HTTPTestUtil.invokeToJSONObject(
				_exportFragmentSetsToJSON(testGroup.getExternalReferenceCode()),
				"headless-admin-fragment/v1.0/sites/" +
					irrelevantGroup.getExternalReferenceCode() +
						"/fragment-sets/batch?createStrategy=UPSERT",
				Http.Method.POST));

		_assertFragmentCollection(fragmentSet1, irrelevantGroup);
		_assertFragmentCollection(putFragmentSet, irrelevantGroup);
	}

	private void _testPutSiteFragmentSetWithDates() throws Exception {
		FragmentSet fragmentSet = randomFragmentSet();

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd");

		Date date = dateFormat.parse("2023-01-01");

		fragmentSet.setDateCreated(date);
		fragmentSet.setDateModified(date);

		FragmentSet putFragmentSet = fragmentSetResource.putSiteFragmentSet(
			testGroup.getExternalReferenceCode(),
			fragmentSet.getExternalReferenceCode(), fragmentSet);

		Assert.assertEquals(
			fragmentSet.getDateCreated(), putFragmentSet.getDateCreated());
		Assert.assertEquals(
			fragmentSet.getDateModified(), putFragmentSet.getDateModified());

		fragmentSet.setDateCreated(RandomTestUtil.nextDate());
		fragmentSet.setDateModified(new Date(date.getTime() + Time.SECOND));

		putFragmentSet = fragmentSetResource.putSiteFragmentSet(
			testGroup.getExternalReferenceCode(),
			fragmentSet.getExternalReferenceCode(), fragmentSet);

		Assert.assertEquals(date, putFragmentSet.getDateCreated());
		Assert.assertEquals(
			fragmentSet.getDateModified(), putFragmentSet.getDateModified());
	}

	private JSONObject _waitForExportFinish(
			String expectedExecuteStatus, JSONObject jsonObject)
		throws Exception {

		String externalReferenceCode = jsonObject.getString(
			"externalReferenceCode");

		long time = System.currentTimeMillis() + _EXPORT_TIMEOUT;

		while (true) {
			jsonObject = HTTPTestUtil.invokeToJSONObject(
				null,
				"headless-batch-engine/v1.0/export-task" +
					"/by-external-reference-code/" + externalReferenceCode,
				Http.Method.GET);

			String executeStatus = jsonObject.getString("executeStatus");

			if (StringUtil.equals(executeStatus, "COMPLETED") ||
				StringUtil.equals(executeStatus, "FAILED")) {

				Assert.assertEquals(expectedExecuteStatus, executeStatus);

				return jsonObject;
			}

			if (System.currentTimeMillis() > time) {
				throw new AssertionError(
					StringBundler.concat(
						"Export task ", externalReferenceCode,
						" did not finish within ", _EXPORT_TIMEOUT, " ms"));
			}

			Thread.sleep(_EXPORT_POLL_INTERVAL);
		}
	}

	private static final long _EXPORT_POLL_INTERVAL = 500;

	private static final long _EXPORT_TIMEOUT = 60000;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private Language _language;

}