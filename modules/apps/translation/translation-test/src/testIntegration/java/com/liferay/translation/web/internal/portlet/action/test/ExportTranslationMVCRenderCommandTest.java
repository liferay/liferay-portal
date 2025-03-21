/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Constructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
@Sync
public class ExportTranslationMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		String symbolicName = "com.liferay.translation.web";

		_bundle = BundleUtil.getBundle(bundle.getBundleContext(), symbolicName);

		Assert.assertNotNull(
			"Unable to find bundle with symbolic name: " + symbolicName,
			_bundle);

		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetModels() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		JournalArticle approvedJournalArticle = _addApprovedJournalArticle();
		JournalArticle expiredJournalArticle = _addExpiredJournalArticle(
			serviceContext);
		JournalArticle scheduledJournalArticle = _addScheduledJournalArticle(
			serviceContext);

		List<JournalArticle> journalArticles = Arrays.asList(
			approvedJournalArticle, expiredJournalArticle,
			scheduledJournalArticle);

		Class<?> clazz = _bundle.loadClass(
			"com.liferay.translation.web.internal.helper." +
				"TranslationRequestHelper");

		List<Object> objects = ReflectionTestUtil.invoke(
			_mvcRenderCommand, "_getModels", new Class<?>[] {clazz},
			_getTranslationRequestHelper(
				clazz, String.valueOf(approvedJournalArticle.getArticleId()),
				String.valueOf(expiredJournalArticle.getArticleId()),
				String.valueOf(scheduledJournalArticle.getArticleId())));

		Assert.assertEquals(objects.toString(), 3, objects.size());

		for (Object object : objects) {
			Assert.assertTrue(object instanceof JournalArticle);

			JournalArticle journalArticle = (JournalArticle)object;

			Assert.assertTrue(journalArticles.contains(journalArticle));
		}
	}

	private JournalArticle _addApprovedJournalArticle() throws Exception {
		return JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			Collections.emptyMap());
	}

	private JournalArticle _addExpiredJournalArticle(
			ServiceContext serviceContext)
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			Collections.emptyMap());

		return _journalArticleLocalService.expireArticle(
			TestPropsValues.getUserId(), _group.getGroupId(),
			journalArticle.getArticleId(), journalArticle.getVersion(), null,
			serviceContext);
	}

	private JournalArticle _addScheduledJournalArticle(
			ServiceContext serviceContext)
		throws Exception {

		LocalDateTime localDateTime = LocalDateTime.now();

		localDateTime = localDateTime.plusDays(1);

		ZonedDateTime zonedDateTime = localDateTime.atZone(
			ZoneId.systemDefault());

		Date displayDate = Date.from(zonedDateTime.toInstant());

		return JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true, RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), null,
			LocaleUtil.getSiteDefault(), displayDate, null, true, true,
			serviceContext);
	}

	private Object _getTranslationRequestHelper(
			Class<?> clazz, String... classPKs)
		throws Exception {

		Constructor<?> constructor = clazz.getDeclaredConstructor(
			HttpServletRequest.class, InfoItemServiceRegistry.class,
			SegmentsExperienceLocalService.class);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"classNameId",
			String.valueOf(
				_portal.getClassNameId(JournalArticle.class.getName())));
		mockHttpServletRequest.setParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockHttpServletRequest.setParameter("key", classPKs);

		return constructor.newInstance(
			mockHttpServletRequest, _infoItemServiceRegistry,
			_segmentsExperienceLocalService);
	}

	private Bundle _bundle;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject(filter = "mvc.command.name=/translation/export_translation")
	private MVCRenderCommand _mvcRenderCommand;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}