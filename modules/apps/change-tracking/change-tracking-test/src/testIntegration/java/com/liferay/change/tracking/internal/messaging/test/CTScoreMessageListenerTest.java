/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.messaging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTDestinationNames;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Truong
 */
@RunWith(Arquillian.class)
@Sync(destinationNames = CTDestinationNames.CT_SCORE)
public class CTScoreMessageListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testDoReceiveDecrementsScoreOnCTEntryDelete() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_ctCollection = _addCTCollection();

		DLFileEntry dlFileEntry = _addDLFileEntry(_ctCollection, group);

		int score = _ctCollection.getScore();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_dlFileEntryLocalService.deleteFileEntry(
				dlFileEntry.getFileEntryId());
		}

		Assert.assertTrue(_ctCollection.getScore() < score);

		GroupTestUtil.deleteGroup(group);
	}

	@Test
	public void testDoReceiveEscalatesFromMediumToLargeClassification()
		throws Exception {

		_ctCollection = _addCTCollection();

		long ctCollectionId = _ctCollection.getCtCollectionId();

		_setScore(ctCollectionId, 20000);

		List<JSONObject> originalJSONObjects =
			_getSizeClassificationNotificationJSONObjects(ctCollectionId);

		int originalNotificationsCount = originalJSONObjects.size();

		_sendCTScoreMessage(ctCollectionId, true);

		PortalCache<Long, String> scoreSizeClassificationPortalCache =
			PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.MULTI_VM,
				_PORTAL_CACHE_NAME_CT_SCORE_SIZE_CLASSIFICATION_CACHE);

		Assert.assertEquals(
			"large", scoreSizeClassificationPortalCache.get(ctCollectionId));

		PortalCache<Long, Integer> scorePortalCache =
			PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.SINGLE_VM,
				_PORTAL_CACHE_NAME_CT_SCORE_CACHE);

		Assert.assertNull(scorePortalCache.get(ctCollectionId));

		List<JSONObject> jsonObjects =
			_getSizeClassificationNotificationJSONObjects(ctCollectionId);

		Assert.assertEquals(
			jsonObjects.toString(), originalNotificationsCount + 1,
			jsonObjects.size());

		JSONObject jsonObject = jsonObjects.get(jsonObjects.size() - 1);

		Assert.assertEquals(
			"medium", jsonObject.getString("originalSizeClassification"));
		Assert.assertEquals(
			"large", jsonObject.getString("sizeClassification"));
	}

	@Test
	public void testDoReceiveEscalatesFromSmallToMediumClassification()
		throws Exception {

		_ctCollection = _addCTCollection();

		long ctCollectionId = _ctCollection.getCtCollectionId();

		_setScore(ctCollectionId, 10000);

		List<JSONObject> originalJSONObjects =
			_getSizeClassificationNotificationJSONObjects(ctCollectionId);

		int originalNotificationsCount = originalJSONObjects.size();

		_sendCTScoreMessage(ctCollectionId, true);

		PortalCache<Long, String> scoreSizeClassificationPortalCache =
			PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.MULTI_VM,
				_PORTAL_CACHE_NAME_CT_SCORE_SIZE_CLASSIFICATION_CACHE);

		Assert.assertEquals(
			"medium", scoreSizeClassificationPortalCache.get(ctCollectionId));

		PortalCache<Long, Integer> scorePortalCache =
			PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.SINGLE_VM,
				_PORTAL_CACHE_NAME_CT_SCORE_CACHE);

		Assert.assertNull(scorePortalCache.get(ctCollectionId));

		List<JSONObject> jsonObjects =
			_getSizeClassificationNotificationJSONObjects(ctCollectionId);

		Assert.assertEquals(
			jsonObjects.toString(), originalNotificationsCount + 1,
			jsonObjects.size());

		JSONObject jsonObject = jsonObjects.get(jsonObjects.size() - 1);

		Assert.assertEquals(
			"small", jsonObject.getString("originalSizeClassification"));
		Assert.assertEquals(
			"medium", jsonObject.getString("sizeClassification"));
	}

	@Test
	public void testDoReceiveIgnoresScoreUpdateOnReadOnlyCollection()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		_ctCollection = _addCTCollection();

		_addDLFileEntry(_ctCollection, group);

		int score = _ctCollection.getScore();

		_ctCollection.setStatus(WorkflowConstants.STATUS_APPROVED);

		_ctCollection = _ctCollectionLocalService.updateCTCollection(
			_ctCollection);

		_sendCTScoreMessage(_ctCollection.getCtCollectionId(), true);

		Assert.assertEquals(score, _ctCollection.getScore());

		GroupTestUtil.deleteGroup(group);
	}

	@Test
	public void testDoReceiveIncrementsScoreOnCTEntryCreate() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_ctCollection = _addCTCollection();

		int score = _ctCollection.getScore();

		_addDLFileEntry(_ctCollection, group);

		Assert.assertTrue(_ctCollection.getScore() > score);

		GroupTestUtil.deleteGroup(group);
	}

	@Test
	public void testDoReceiveUpdatesScoresOnCTEntryCrossCollectionMove()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		_sourceCTCollection = _addCTCollection();
		_targetCTCollection = _addCTCollection();

		_addDLFileEntry(_sourceCTCollection, group);

		int sourceScore = _sourceCTCollection.getScore();
		int targetScore = _targetCTCollection.getScore();

		List<CTEntry> ctEntries = _ctEntryLocalService.getCTCollectionCTEntries(
			_sourceCTCollection.getCtCollectionId());

		CTEntry ctEntry = ctEntries.get(0);

		ctEntry.setCtCollectionId(_targetCTCollection.getCtCollectionId());

		_ctEntryLocalService.updateCTEntry(ctEntry);

		int movedSourceScore = _sourceCTCollection.getScore();
		int movedTargetScore = _targetCTCollection.getScore();

		Assert.assertTrue(movedSourceScore < sourceScore);
		Assert.assertEquals(
			sourceScore - movedSourceScore, movedTargetScore - targetScore);

		GroupTestUtil.deleteGroup(group);
	}

	@Test
	public void testDoReceiveWithinSameTierDoesNotRewriteClassification()
		throws Exception {

		_ctCollection = _addCTCollection();

		long ctCollectionId = _ctCollection.getCtCollectionId();

		_setScore(ctCollectionId, 10001);

		List<JSONObject> originalJSONObjects =
			_getSizeClassificationNotificationJSONObjects(ctCollectionId);

		int originalNotificationsCount = originalJSONObjects.size();

		_sendCTScoreMessage(ctCollectionId, true);

		Assert.assertEquals(
			"medium", _ctCollection.getScoreSizeClassification());

		List<JSONObject> jsonObjects =
			_getSizeClassificationNotificationJSONObjects(ctCollectionId);

		Assert.assertEquals(
			jsonObjects.toString(), originalNotificationsCount,
			jsonObjects.size());
	}

	private CTCollection _addCTCollection() throws Exception {
		return _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);
	}

	private DLFileEntry _addDLFileEntry(CTCollection ctCollection, Group group)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			DLFolder dlFolder = DLTestUtil.addDLFolder(group.getGroupId());

			return DLTestUtil.addDLFileEntry(dlFolder.getFolderId());
		}
	}

	private List<JSONObject> _getSizeClassificationNotificationJSONObjects(
			long ctCollectionId)
		throws Exception {

		return TransformUtil.unsafeTransform(
			_userNotificationEventLocalService.getUserNotificationEvents(
				TestPropsValues.getUserId()),
			userNotificationEvent -> {
				if (!Objects.equals(
						CTPortletKeys.PUBLICATIONS,
						userNotificationEvent.getType())) {

					return null;
				}

				JSONObject jsonObject = _jsonFactory.createJSONObject(
					userNotificationEvent.getPayload());

				if ((jsonObject.getLong("ctCollectionId") != ctCollectionId) ||
					(jsonObject.getInt("notificationType") !=
						UserNotificationDefinition.
							NOTIFICATION_TYPE_UPDATE_ENTRY)) {

					return null;
				}

				return jsonObject;
			});
	}

	private void _sendCTScoreMessage(long ctCollectionId, boolean increment) {
		Message message = new Message();

		message.setValues(
			HashMapBuilder.<String, Object>put(
				"ctCollectionId", ctCollectionId
			).put(
				"increment", increment
			).put(
				"modelClassNameId",
				_classNameLocalService.getClassNameId(DLFileEntry.class)
			).build());

		MessageBusUtil.sendMessage(CTDestinationNames.CT_SCORE, message);
	}

	private void _setScore(long ctCollectionId, int score) {
		PortalCache<Long, Integer> portalCache =
			PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.SINGLE_VM,
				_PORTAL_CACHE_NAME_CT_SCORE_CACHE);

		portalCache.put(ctCollectionId, score);
	}

	private static final String _PORTAL_CACHE_NAME_CT_SCORE_CACHE =
		"com.liferay.change.tracking.internal.score.CTScoreCache";

	private static final String
		_PORTAL_CACHE_NAME_CT_SCORE_SIZE_CLASSIFICATION_CACHE =
			"com.liferay.change.tracking.internal.score." +
				"CTScoreSizeClassificationCache";

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTEntryLocalService _ctEntryLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@DeleteAfterTestRun
	private CTCollection _sourceCTCollection;

	@DeleteAfterTestRun
	private CTCollection _targetCTCollection;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}