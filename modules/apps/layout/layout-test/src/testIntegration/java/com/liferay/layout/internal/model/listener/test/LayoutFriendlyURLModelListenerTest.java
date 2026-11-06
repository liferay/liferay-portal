/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.thread.local.BatchEngineThreadLocal;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.friendly.url.constants.FriendlyURLEntryConstants;
import com.liferay.friendly.url.exception.DuplicateFriendlyURLEntryException;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.layout.friendly.url.LayoutFriendlyURLEntryHelper;
import com.liferay.layout.test.util.LayoutFriendlyURLRandomizerBumper;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class LayoutFriendlyURLModelListenerTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_classNameId = _layoutFriendlyURLEntryHelper.getClassNameId(false);

		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());
	}

	@Test
	@TestInfo({"LPD-74225", "LPD-113926"})
	public void testCreateLayoutFriendlyURL() throws Exception {
		_testCreateLayoutFriendlyURL();
		_testCreateLayoutFriendlyURLWithHistoricalFriendlyURL();
		_testCreateLayoutFriendlyURLWithMainFriendlyURL();
	}

	@Test
	@TestInfo({"LPD-74225", "LPD-113926"})
	public void testUpdateLayoutFriendlyURL() throws Exception {
		_testUpdateLayoutFriendlyURL();
		_testUpdateLayoutFriendlyURLWithHistoricalFriendlyURL();
		_testUpdateLayoutFriendlyURLWithMainFriendlyURL();
	}

	private Layout _addLayout(String friendlyURL) throws Exception {
		return LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), false,
			Collections.singletonMap(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(LocaleUtil.getSiteDefault(), friendlyURL));
	}

	private String _getRandomFriendlyURL() {
		String urlTitle = StringUtil.toLowerCase(
			RandomTestUtil.randomString(
				LayoutFriendlyURLRandomizerBumper.INSTANCE));

		return StringPool.FORWARD_SLASH + urlTitle;
	}

	private void _testCreateLayoutFriendlyURL() throws Exception {
		_testCreateLayoutFriendlyURL(1);

		ExportImportThreadLocal.setLayoutImportInProcess(true);

		try {
			_testCreateLayoutFriendlyURL(0);

			try (SafeCloseable safeCloseable =
					BatchEngineThreadLocal.
						setBatchImportInProcessWithSafeCloseable(true)) {

				_testCreateLayoutFriendlyURL(1);
			}
		}
		finally {
			ExportImportThreadLocal.setLayoutImportInProcess(false);
		}

		_stagingLocalService.enableLocalStaging(
			TestPropsValues.getUserId(), _group, false, false, _serviceContext);

		_testCreateLayoutFriendlyURL(0);

		try (SafeCloseable safeCloseable =
				BatchEngineThreadLocal.setBatchImportInProcessWithSafeCloseable(
					true)) {

			_testCreateLayoutFriendlyURL(1);
		}
	}

	private void _testCreateLayoutFriendlyURL(int count) throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		List<FriendlyURLEntry> friendlyURLEntries =
			_friendlyURLEntryLocalService.getFriendlyURLEntries(
				_group.getGroupId(), _classNameId, layout.getPlid());

		Assert.assertEquals(
			friendlyURLEntries.toString(), count, friendlyURLEntries.size());
	}

	private void _testCreateLayoutFriendlyURLWithHistoricalFriendlyURL()
		throws Exception {

		try (SafeCloseable safeCloseable =
				BatchEngineThreadLocal.setBatchImportInProcessWithSafeCloseable(
					true)) {

			Layout layout1 = LayoutTestUtil.addTypePortletLayout(_group);

			String friendlyURL = layout1.getFriendlyURL();

			_updateFriendlyURL(_getRandomFriendlyURL(), layout1);

			try {
				_addLayout(friendlyURL);

				Assert.fail();
			}
			catch (ModelListenerException modelListenerException) {
				Throwable throwable = modelListenerException.getCause();

				Assert.assertTrue(
					throwable instanceof DuplicateFriendlyURLEntryException);
			}

			ExportImportThreadLocal.setLayoutStagingInProcess(true);

			Layout layout2 = null;

			try {
				layout2 = _addLayout(friendlyURL);
			}
			finally {
				ExportImportThreadLocal.setLayoutStagingInProcess(false);
			}

			Assert.assertEquals(friendlyURL, layout2.getFriendlyURL());

			FriendlyURLEntryLocalization friendlyURLEntryLocalization =
				_friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
					_group.getGroupId(), _classNameId,
					FriendlyURLEntryConstants.
						FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
					layout2.getDefaultLanguageId(), friendlyURL);

			Assert.assertEquals(
				layout2.getPlid(), friendlyURLEntryLocalization.getClassPK());
		}
	}

	private void _testCreateLayoutFriendlyURLWithMainFriendlyURL()
		throws Exception {

		try (SafeCloseable safeCloseable =
				BatchEngineThreadLocal.setBatchImportInProcessWithSafeCloseable(
					true)) {

			Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

			String friendlyURL = _getRandomFriendlyURL();

			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				_group.getGroupId(), _classNameId, layout.getPlid(),
				Collections.singletonMap(
					layout.getDefaultLanguageId(), friendlyURL),
				_serviceContext);

			ExportImportThreadLocal.setLayoutStagingInProcess(true);

			try {
				_addLayout(friendlyURL);

				Assert.fail();
			}
			catch (ModelListenerException modelListenerException) {
				Throwable throwable = modelListenerException.getCause();

				Assert.assertTrue(
					throwable instanceof DuplicateFriendlyURLEntryException);
			}
			finally {
				ExportImportThreadLocal.setLayoutStagingInProcess(false);
			}

			FriendlyURLEntryLocalization friendlyURLEntryLocalization =
				_friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
					_group.getGroupId(), _classNameId,
					FriendlyURLEntryConstants.
						FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
					layout.getDefaultLanguageId(), friendlyURL);

			Assert.assertEquals(
				layout.getPlid(), friendlyURLEntryLocalization.getClassPK());
		}
	}

	private void _testUpdateLayoutFriendlyURL() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_testUpdateLayoutFriendlyURL(1, layout);

		ExportImportThreadLocal.setLayoutImportInProcess(true);

		try {
			_testUpdateLayoutFriendlyURL(0, layout);

			try (SafeCloseable safeCloseable =
					BatchEngineThreadLocal.
						setBatchImportInProcessWithSafeCloseable(true)) {

				_testUpdateLayoutFriendlyURL(1, layout);
			}
		}
		finally {
			ExportImportThreadLocal.setLayoutImportInProcess(false);
		}

		_stagingLocalService.enableLocalStaging(
			TestPropsValues.getUserId(), _group, false, false, _serviceContext);

		_testUpdateLayoutFriendlyURL(0, layout);

		try (SafeCloseable safeCloseable =
				BatchEngineThreadLocal.setBatchImportInProcessWithSafeCloseable(
					true)) {

			_testUpdateLayoutFriendlyURL(1, layout);
		}
	}

	private void _testUpdateLayoutFriendlyURL(int count, Layout layout)
		throws Exception {

		List<FriendlyURLEntry> friendlyURLEntries =
			_friendlyURLEntryLocalService.getFriendlyURLEntries(
				_group.getGroupId(), _classNameId, layout.getPlid());

		layout = _updateFriendlyURL(_getRandomFriendlyURL(), layout);

		List<FriendlyURLEntry> actualFriendlyURLEntries =
			_friendlyURLEntryLocalService.getFriendlyURLEntries(
				_group.getGroupId(), _classNameId, layout.getPlid());

		Assert.assertEquals(
			actualFriendlyURLEntries.toString(),
			friendlyURLEntries.size() + count, actualFriendlyURLEntries.size());
	}

	private void _testUpdateLayoutFriendlyURLWithHistoricalFriendlyURL()
		throws Exception {

		try (SafeCloseable safeCloseable =
				BatchEngineThreadLocal.setBatchImportInProcessWithSafeCloseable(
					true)) {

			Layout layout1 = LayoutTestUtil.addTypePortletLayout(_group);

			String friendlyURL = layout1.getFriendlyURL();

			_updateFriendlyURL(_getRandomFriendlyURL(), layout1);

			Layout layout2 = LayoutTestUtil.addTypePortletLayout(_group);

			try {
				_updateFriendlyURL(friendlyURL, layout2);

				Assert.fail();
			}
			catch (ModelListenerException modelListenerException) {
				Throwable throwable = modelListenerException.getCause();

				Assert.assertTrue(
					throwable instanceof DuplicateFriendlyURLEntryException);
			}

			ExportImportThreadLocal.setLayoutStagingInProcess(true);

			try {
				layout2 = _updateFriendlyURL(friendlyURL, layout2);
			}
			finally {
				ExportImportThreadLocal.setLayoutStagingInProcess(false);
			}

			Assert.assertEquals(friendlyURL, layout2.getFriendlyURL());

			FriendlyURLEntryLocalization friendlyURLEntryLocalization =
				_friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
					_group.getGroupId(), _classNameId,
					FriendlyURLEntryConstants.
						FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
					layout2.getDefaultLanguageId(), friendlyURL);

			Assert.assertEquals(
				layout2.getPlid(), friendlyURLEntryLocalization.getClassPK());
		}
	}

	private void _testUpdateLayoutFriendlyURLWithMainFriendlyURL()
		throws Exception {

		try (SafeCloseable safeCloseable =
				BatchEngineThreadLocal.setBatchImportInProcessWithSafeCloseable(
					true)) {

			Layout layout1 = LayoutTestUtil.addTypePortletLayout(_group);
			Layout layout2 = LayoutTestUtil.addTypePortletLayout(_group);

			String friendlyURL = _getRandomFriendlyURL();

			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				_group.getGroupId(), _classNameId, layout1.getPlid(),
				Collections.singletonMap(
					layout1.getDefaultLanguageId(), friendlyURL),
				_serviceContext);

			ExportImportThreadLocal.setLayoutStagingInProcess(true);

			try {
				_updateFriendlyURL(friendlyURL, layout2);

				Assert.fail();
			}
			catch (ModelListenerException modelListenerException) {
				Throwable throwable = modelListenerException.getCause();

				Assert.assertTrue(
					throwable instanceof DuplicateFriendlyURLEntryException);
			}
			finally {
				ExportImportThreadLocal.setLayoutStagingInProcess(false);
			}

			FriendlyURLEntryLocalization friendlyURLEntryLocalization =
				_friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
					_group.getGroupId(), _classNameId,
					FriendlyURLEntryConstants.
						FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
					layout1.getDefaultLanguageId(), friendlyURL);

			Assert.assertEquals(
				layout1.getPlid(), friendlyURLEntryLocalization.getClassPK());
		}
	}

	private Layout _updateFriendlyURL(String friendlyURL, Layout layout)
		throws Exception {

		return _layoutLocalService.updateFriendlyURL(
			TestPropsValues.getUserId(), layout.getPlid(), friendlyURL,
			layout.getDefaultLanguageId());
	}

	private long _classNameId;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutFriendlyURLEntryHelper _layoutFriendlyURLEntryHelper;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private StagingLocalService _stagingLocalService;

}