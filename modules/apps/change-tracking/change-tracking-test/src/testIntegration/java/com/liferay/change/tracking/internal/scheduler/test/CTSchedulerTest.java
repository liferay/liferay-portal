/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.scheduler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.scheduler.PublishScheduler;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brooke Dalton
 */
@RunWith(Arquillian.class)
public class CTSchedulerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testSchedulePublishTwice() throws Exception {
		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			JournalTestUtil.addArticle(
				TestPropsValues.getGroupId(), RandomTestUtil.randomString(),
				StringPool.BLANK);
		}

		Date date = new Date(System.currentTimeMillis() + 30000);

		_publishScheduler.schedulePublish(
			ctCollection.getCtCollectionId(), TestPropsValues.getUserId(),
			date);

		_publishScheduler.schedulePublish(
			ctCollection.getCtCollectionId(), TestPropsValues.getUserId(),
			date);
	}

	@Test
	public void testUnschedulePublishWithPendingWorkflow() throws Exception {
		_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
			null, TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			TestPropsValues.getGroupId(), JournalArticle.class.getName(), 0, 0,
			"Single Approver", 1);

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			JournalTestUtil.addArticle(
				TestPropsValues.getGroupId(), RandomTestUtil.randomString(),
				StringPool.BLANK);
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CTSettingsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"unapprovedChangesAllowed", true
						).build())) {

			Date date = new Date(System.currentTimeMillis() + 30000);

			_publishScheduler.schedulePublish(
				ctCollection.getCtCollectionId(), TestPropsValues.getUserId(),
				date);

			ctCollection = _ctCollectionLocalService.getCTCollection(
				ctCollection.getCtCollectionId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_SCHEDULED, ctCollection.getStatus());

			_publishScheduler.unschedulePublish(
				ctCollection.getCtCollectionId());

			ctCollection = _ctCollectionLocalService.getCTCollection(
				ctCollection.getCtCollectionId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_INCOMPLETE, ctCollection.getStatus());
		}
	}

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private PublishScheduler _publishScheduler;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}