/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_7.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.ratings.kernel.service.RatingsEntryLocalService;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class CMSCommentRatingsUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		String defaultLanguageId = UpgradeProcessUtil.getDefaultLanguageId(
			TestPropsValues.getCompanyId());

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.fromLanguageId(defaultLanguageId),
				RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.fromLanguageId(defaultLanguageId),
				RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", depotEntry.getCompanyId());

		ObjectEntry objectEntry = _addObjectEntry(
			defaultLanguageId, depotEntry, objectDefinition);

		long commentId1 = _addComment(
			objectDefinition.getClassName(), objectEntry.getObjectEntryId(),
			depotEntry.getGroupId());

		_ratingsEntryLocalService.updateEntry(
			TestPropsValues.getUserId(), objectDefinition.getClassName(),
			commentId1, 1, ServiceContextTestUtil.getServiceContext());

		_ratingsEntryLocalService.updateEntry(
			TestPropsValues.getUserId(), objectDefinition.getClassName(),
			objectEntry.getObjectEntryId(), 1,
			ServiceContextTestUtil.getServiceContext());

		String className = RandomTestUtil.randomString();
		long classPK = RandomTestUtil.randomLong();

		long commentId2 = _addComment(
			className, classPK, depotEntry.getGroupId());

		_ratingsEntryLocalService.updateEntry(
			TestPropsValues.getUserId(), className, commentId2, 1,
			ServiceContextTestUtil.getServiceContext());

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_7." +
				"CMSCommentRatingsUpgradeProcess");

		upgradeProcess.upgrade();

		_assertRatings(
			"com.liferay.message.boards.model.MBDiscussion", commentId1);
		_assertRatings(className, commentId2);
		_assertRatings(
			objectDefinition.getClassName(), objectEntry.getObjectEntryId());

		Assert.assertNull(
			_ratingsEntryLocalService.fetchEntry(
				TestPropsValues.getUserId(), objectDefinition.getClassName(),
				commentId1));
		Assert.assertNull(
			_ratingsStatsLocalService.fetchStats(
				objectDefinition.getClassName(), commentId1));
	}

	private long _addComment(String className, long classPK, long groupId)
		throws Exception {

		return _commentManager.addComment(
			TestPropsValues.getUserId(), groupId, className, classPK,
			RandomTestUtil.randomString(),
			new IdentityServiceContextFunction(
				ServiceContextTestUtil.getServiceContext(
					groupId, TestPropsValues.getUserId())));
	}

	private ObjectEntry _addObjectEntry(
			String defaultLanguageId, DepotEntry depotEntry,
			ObjectDefinition objectDefinition)
		throws Exception {

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					"L_CONTENTS", depotEntry.getGroupId(),
					depotEntry.getCompanyId());

		return _objectEntryLocalService.addObjectEntry(
			depotEntry.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), defaultLanguageId,
			HashMapBuilder.<String, Serializable>put(
				"content_i18n",
				HashMapBuilder.put(
					defaultLanguageId, RandomTestUtil.randomString()
				).build()
			).put(
				"title_i18n",
				HashMapBuilder.put(
					defaultLanguageId, RandomTestUtil.randomString()
				).build()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				depotEntry.getGroupId(), TestPropsValues.getUserId()));
	}

	private void _assertRatings(String className, long classPK)
		throws Exception {

		Assert.assertNotNull(
			_ratingsEntryLocalService.fetchEntry(
				TestPropsValues.getUserId(), className, classPK));
		Assert.assertNotNull(
			_ratingsStatsLocalService.fetchStats(className, classPK));
	}

	@Inject
	private CommentManager _commentManager;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private RatingsEntryLocalService _ratingsEntryLocalService;

	@Inject
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.upgrade.registry.SiteCMSSiteInitializerUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}