/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.bulk.selection.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Veronica Gonzalez
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-17564"))
@RunWith(Arquillian.class)
public class UpdateReviewDateObjectBulkSelectionActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testDoExecuteClearsReviewDate() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry(
			_DISPLAY_DATE, _ORIGINAL_REVIEW_DATE);

		_invokeDoExecute(objectEntry, null);

		ObjectEntry updatedObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				objectEntry.getObjectEntryId());

		Assert.assertNull(updatedObjectEntry.getReviewDate());
		Assert.assertEquals(_DISPLAY_DATE, updatedObjectEntry.getDisplayDate());
	}

	@Test
	public void testDoExecuteUpdatesReviewDatePreservingDisplayDate()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry(
			_DISPLAY_DATE, _ORIGINAL_REVIEW_DATE);

		_invokeDoExecute(objectEntry, _NEW_REVIEW_DATE);

		ObjectEntry updatedObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				objectEntry.getObjectEntryId());

		Assert.assertEquals(
			_NEW_REVIEW_DATE, updatedObjectEntry.getReviewDate());
		Assert.assertEquals(_DISPLAY_DATE, updatedObjectEntry.getDisplayDate());
	}

	private ObjectEntry _addObjectEntry(Date displayDate, Date reviewDate)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"text"
					).build()));

		objectDefinition =
			ObjectDefinitionLocalServiceUtil.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		return ObjectEntryLocalServiceUtil.addObjectEntry(
			0L, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"displayDate", displayDate
			).put(
				"reviewDate", reviewDate
			).put(
				"text", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _invokeDoExecute(ObjectEntry objectEntry, Date reviewDate)
		throws Exception {

		ReflectionTestUtil.invoke(
			_updateReviewDateObjectBulkSelectionAction, "doExecute",
			new Class<?>[] {User.class, Map.class, Object.class},
			TestPropsValues.getUser(),
			HashMapBuilder.<String, Serializable>put(
				"reviewDate", reviewDate
			).build(),
			objectEntry);
	}

	private static final Date _DISPLAY_DATE = new Date(1700000000000L);

	private static final Date _NEW_REVIEW_DATE = new Date(4100000000000L);

	private static final Date _ORIGINAL_REVIEW_DATE = new Date(1700000100000L);

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.bulk.selection.UpdateReviewDateObjectBulkSelectionAction"
	)
	private BulkSelectionAction<Object>
		_updateReviewDateObjectBulkSelectionAction;

}