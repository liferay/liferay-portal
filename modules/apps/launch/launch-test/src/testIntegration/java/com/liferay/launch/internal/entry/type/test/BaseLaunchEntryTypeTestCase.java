/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.entry.type.test;

import com.liferay.launch.entry.type.LaunchEntryType;
import com.liferay.launch.entry.type.registry.LaunchEntryTypeRegistry;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.preview.PreviewableResolverUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Alejandro Tardín
 */
public abstract class BaseLaunchEntryTypeTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		group = GroupTestUtil.addGroup();

		serviceContext = ServiceContextTestUtil.getServiceContext(
			group.getGroupId(), TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testAddDraft() throws Exception {
		long classPK = addEntry();

		String entryTitle = getEntryTitle(classPK);

		LaunchEntryType.Version version = getPublishedVersion(classPK);

		LaunchEntryType.Draft draft1 = addDraft(classPK);
		LaunchEntryType.Draft draft2 = addDraft(classPK);

		Assert.assertNotEquals(draft1.getDraftId(), draft2.getDraftId());

		String title1 = RandomTestUtil.randomString();

		updateDraft(title1, draft1);

		String title2 = RandomTestUtil.randomString();

		updateDraft(title2, draft2);

		Assert.assertEquals(title1, _getDraftTitle(classPK, draft1));
		Assert.assertEquals(title2, _getDraftTitle(classPK, draft2));

		Assert.assertEquals(entryTitle, getEntryTitle(classPK));

		String title3 = RandomTestUtil.randomString();

		updateLiveEntry(title3, classPK);

		Assert.assertEquals(title3, getEntryTitle(classPK));

		Assert.assertEquals(title1, _getDraftTitle(classPK, draft1));
		Assert.assertEquals(title2, _getDraftTitle(classPK, draft2));

		LaunchEntryType launchEntryType = getLaunchEntryType();

		Assert.assertEquals(
			entryTitle,
			getDraftTitle(
				launchEntryType.addDraft(classPK, version.getVersionKey())));

		Assert.assertEquals(title3, getDraftTitle(addDraft(classPK)));

		try {
			launchEntryType.addDraft(
				classPK, String.valueOf(draft1.getDraftId()));

			Assert.fail();
		}
		catch (PortalException portalException) {
			Assert.assertNotNull(portalException);
		}
	}

	@Test
	public void testDeleteDraft() throws Exception {
		long classPK = addEntry();

		String entryTitle = getEntryTitle(classPK);

		LaunchEntryType.Draft draft = addDraft(classPK);

		LaunchEntryType launchEntryType = getLaunchEntryType();

		launchEntryType.deleteDraft(classPK, draft.getDraftId());

		Assert.assertNull(
			launchEntryType.fetchDraft(classPK, draft.getDraftId()));

		launchEntryType.deleteDraft(classPK, draft.getDraftId());

		Assert.assertEquals(entryTitle, getEntryTitle(classPK));
		Assert.assertNotNull(launchEntryType.fetchPublishedVersion(classPK));
	}

	@Test
	public void testFetchDraft() throws Exception {
		long classPK = addEntry();

		String title = RandomTestUtil.randomString();

		LaunchEntryType.Draft draft = addDraft(classPK);

		updateDraft(title, draft);

		LaunchEntryType.Draft fetchedDraft = getDraft(
			classPK, draft.getDraftId());

		Assert.assertEquals(draft.getDraftId(), fetchedDraft.getDraftId());
		Assert.assertEquals(title, getDraftTitle(fetchedDraft));

		LaunchEntryType launchEntryType = getLaunchEntryType();

		Assert.assertNull(
			launchEntryType.fetchDraft(classPK, RandomTestUtil.randomLong()));
	}

	@Test
	public void testFetchItem() throws Exception {
		long classPK = addEntry();

		LaunchEntryType.Item item = getItem(classPK);

		Assert.assertEquals(group.getGroupId(), item.getGroupId());
		Assert.assertEquals(
			getEntryTitle(classPK), item.getTitle(LocaleUtil.getSiteDefault()));
		Assert.assertNotNull(item.getTypeName(LocaleUtil.getSiteDefault()));

		List<Serializable> listedPrimaryKeys = getListedPrimaryKeys(classPK);

		Assert.assertTrue(
			listedPrimaryKeys.toString(),
			listedPrimaryKeys.contains(item.getPrimaryKey()));

		String title = RandomTestUtil.randomString();

		updateDraft(title, addDraft(classPK));

		item = getItem(classPK);

		Assert.assertNotEquals(
			title, item.getTitle(LocaleUtil.getSiteDefault()));

		LaunchEntryType launchEntryType = getLaunchEntryType();

		Assert.assertNull(
			launchEntryType.fetchItem(RandomTestUtil.randomLong()));
	}

	@Test
	public void testFetchPublishedVersion() throws Exception {
		long classPK = addEntry();

		LaunchEntryType.Version publishedVersion = getPublishedVersion(classPK);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, publishedVersion.getStatus());
	}

	@Test
	public void testFetchVersion() throws Exception {
		long classPK = addEntry();

		LaunchEntryType.Version publishedVersion = getPublishedVersion(classPK);

		LaunchEntryType.Version fetchedVersion = getVersion(
			classPK, publishedVersion.getVersionKey());

		Assert.assertEquals(
			publishedVersion.getVersionKey(), fetchedVersion.getVersionKey());

		LaunchEntryType launchEntryType = getLaunchEntryType();

		Assert.assertNull(
			launchEntryType.fetchVersion(
				classPK, String.valueOf(RandomTestUtil.randomLong())));
		Assert.assertNull(
			launchEntryType.fetchVersion(
				RandomTestUtil.randomLong(), publishedVersion.getVersionKey()));

		LaunchEntryType.Draft draft = addDraft(classPK);

		Assert.assertNull(
			launchEntryType.fetchVersion(
				classPK, String.valueOf(draft.getDraftId())));
	}

	@Test
	public void testGetClassPK() throws Exception {
		long classPK = addEntry();

		LaunchEntryType launchEntryType = getLaunchEntryType();

		Assert.assertEquals(
			classPK, launchEntryType.getClassPK(getSelectedItemData(classPK)));
	}

	@Test
	public void testGetEditURL() throws Exception {
		String editURL = _getEditURL(addDraft(addEntry()));

		Map<String, String[]> parameterMap = HttpComponentsUtil.getParameterMap(
			HttpComponentsUtil.getQueryString(editURL));

		Assert.assertArrayEquals(
			editURL, new String[] {_REDIRECT},
			parameterMap.get(getBackURLParameterName()));
	}

	@Test
	public void testGetPreviewBaseModel() throws Exception {
		long classPK = addEntry();

		String entryTitle = getEntryTitle(classPK);

		String title1 = RandomTestUtil.randomString();

		LaunchEntryType.Draft draft1 = addDraft(classPK);

		updateDraft(title1, draft1);

		String title2 = RandomTestUtil.randomString();

		LaunchEntryType.Draft draft2 = addDraft(classPK);

		updateDraft(title2, draft2);

		draft1 = getDraft(classPK, draft1.getDraftId());

		try (SafeCloseable safeCloseable = _setPreviewIdWithSafeCloseable(
				classPK, draft1.getPreviewPrimaryKey())) {

			Assert.assertEquals(title1, getEntryTitle(classPK));
		}

		draft2 = getDraft(classPK, draft2.getDraftId());

		try (SafeCloseable safeCloseable = _setPreviewIdWithSafeCloseable(
				classPK, draft2.getPreviewPrimaryKey())) {

			Assert.assertEquals(title2, getEntryTitle(classPK));
		}

		Assert.assertEquals(entryTitle, getEntryTitle(classPK));

		LaunchEntryType launchEntryType = getLaunchEntryType();

		LaunchEntryType.Version publishedVersion1 =
			launchEntryType.publishDraft(classPK, draft1.getDraftId());

		launchEntryType.publishDraft(classPK, draft2.getDraftId());

		LaunchEntryType.Version version1 = getVersion(
			classPK, publishedVersion1.getVersionKey());

		try (SafeCloseable safeCloseable = _setPreviewIdWithSafeCloseable(
				classPK, version1.getPreviewPrimaryKey())) {

			Assert.assertEquals(title1, getEntryTitle(classPK));
		}

		Assert.assertEquals(title2, getEntryTitle(classPK));
	}

	@Test
	public void testGetVersions() throws Exception {
		long classPK = addEntry();

		LaunchEntryType launchEntryType = getLaunchEntryType();

		LaunchEntryType.Draft draft = addDraft(classPK);

		LaunchEntryType.Version publishedVersion = launchEntryType.publishDraft(
			classPK,
			addDraft(
				classPK
			).getDraftId());

		List<String> versionKeys = TransformUtil.transform(
			launchEntryType.getVersions(classPK, LocaleUtil.getSiteDefault()),
			LaunchEntryType.Version::getVersionKey);

		Assert.assertTrue(
			versionKeys.toString(),
			versionKeys.contains(publishedVersion.getVersionKey()));
		Assert.assertFalse(
			versionKeys.toString(),
			versionKeys.contains(String.valueOf(draft.getDraftId())));

		updateDraft(RandomTestUtil.randomString(), draft);
		updateDraft(RandomTestUtil.randomString(), draft);

		Assert.assertEquals(
			versionKeys,
			TransformUtil.transform(
				launchEntryType.getVersions(
					classPK, LocaleUtil.getSiteDefault()),
				LaunchEntryType.Version::getVersionKey));

		for (String versionKey : versionKeys) {
			LaunchEntryType.Version version = getVersion(classPK, versionKey);

			Assert.assertEquals(versionKey, version.getVersionKey());

			Assert.assertNotNull(
				versionKey, launchEntryType.addDraft(classPK, versionKey));
		}
	}

	@Test
	public void testPublishDraft() throws Exception {
		long classPK = addEntry();

		LaunchEntryType.Version baseVersion = getPublishedVersion(classPK);

		String title1 = RandomTestUtil.randomString();

		LaunchEntryType.Draft draft1 = addDraft(classPK);

		updateDraft(title1, draft1);

		String title2 = RandomTestUtil.randomString();

		LaunchEntryType.Draft draft2 = addDraft(classPK);

		updateDraft(title2, draft2);

		Assert.assertNotEquals(title1, getEntryTitle(classPK));
		Assert.assertNotEquals(title2, getEntryTitle(classPK));

		LaunchEntryType launchEntryType = getLaunchEntryType();

		LaunchEntryType.Version publishedVersion2 =
			launchEntryType.publishDraft(classPK, draft2.getDraftId());

		_assertPublishedVersion(
			classPK, draft2, publishedVersion2, title2, baseVersion);

		Assert.assertEquals(title1, _getDraftTitle(classPK, draft1));

		LaunchEntryType.Version publishedVersion1 =
			launchEntryType.publishDraft(classPK, draft1.getDraftId());

		_assertPublishedVersion(
			classPK, draft1, publishedVersion1, title1, publishedVersion2);

		try {
			launchEntryType.publishDraft(classPK, draft1.getDraftId());

			Assert.fail();
		}
		catch (PortalException portalException) {
			Assert.assertNotNull(portalException);
		}
	}

	protected LaunchEntryType.Draft addDraft(long classPK) throws Exception {
		LaunchEntryType launchEntryType = getLaunchEntryType();

		LaunchEntryType.Version publishedVersion = getPublishedVersion(classPK);

		return launchEntryType.addDraft(
			classPK, publishedVersion.getVersionKey());
	}

	protected abstract long addEntry() throws Exception;

	protected abstract String getBackURLParameterName();

	protected LaunchEntryType.Draft getDraft(long classPK, long draftId) {
		LaunchEntryType launchEntryType = getLaunchEntryType();

		LaunchEntryType.Draft draft = launchEntryType.fetchDraft(
			classPK, draftId);

		Assert.assertNotNull(String.valueOf(draftId), draft);

		return draft;
	}

	protected abstract String getDraftTitle(LaunchEntryType.Draft draft)
		throws Exception;

	protected abstract String getEntryTitle(long classPK) throws Exception;

	protected LaunchEntryType.Item getItem(long classPK) {
		LaunchEntryType launchEntryType = getLaunchEntryType();

		LaunchEntryType.Item item = launchEntryType.fetchItem(classPK);

		Assert.assertNotNull(item);

		return item;
	}

	protected LaunchEntryType getLaunchEntryType() {
		Class<?> modelClass = getModelClass();

		return _launchEntryTypeRegistry.getLaunchEntryType(
			modelClass.getName());
	}

	protected abstract List<Serializable> getListedPrimaryKeys(long classPK)
		throws Exception;

	protected abstract Class<?> getModelClass();

	protected LaunchEntryType.Version getPublishedVersion(long classPK) {
		LaunchEntryType launchEntryType = getLaunchEntryType();

		LaunchEntryType.Version publishedVersion =
			launchEntryType.fetchPublishedVersion(classPK);

		Assert.assertNotNull(publishedVersion);

		return publishedVersion;
	}

	protected abstract Map<String, String> getSelectedItemData(long classPK)
		throws Exception;

	protected LaunchEntryType.Version getVersion(
		long classPK, String versionKey) {

		LaunchEntryType launchEntryType = getLaunchEntryType();

		LaunchEntryType.Version version = launchEntryType.fetchVersion(
			classPK, versionKey);

		Assert.assertNotNull(versionKey, version);

		return version;
	}

	protected abstract void updateDraft(
			String title, LaunchEntryType.Draft draft)
		throws Exception;

	protected abstract void updateLiveEntry(String title, long classPK)
		throws Exception;

	protected Group group;
	protected ServiceContext serviceContext;

	private void _assertPublishedVersion(
			long classPK, LaunchEntryType.Draft draft,
			LaunchEntryType.Version publishedVersion, String title,
			LaunchEntryType.Version previousPublishedVersion)
		throws Exception {

		Assert.assertEquals(title, getEntryTitle(classPK));
		Assert.assertNotEquals(
			previousPublishedVersion.getVersionKey(),
			publishedVersion.getVersionKey());
		Assert.assertEquals(
			publishedVersion.getVersionKey(),
			getPublishedVersion(
				classPK
			).getVersionKey());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			getVersion(
				classPK, publishedVersion.getVersionKey()
			).getStatus());

		LaunchEntryType launchEntryType = getLaunchEntryType();

		Assert.assertNull(
			launchEntryType.fetchDraft(classPK, draft.getDraftId()));
	}

	private String _getDraftTitle(long classPK, LaunchEntryType.Draft draft)
		throws Exception {

		return getDraftTitle(getDraft(classPK, draft.getDraftId()));
	}

	private String _getEditURL(LaunchEntryType.Draft draft) throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			ContentLayoutTestUtil.getThemeDisplay(
				_companyLocalService.getCompany(group.getCompanyId()), group,
				LayoutTestUtil.addTypePortletLayout(group)));

		serviceContext.setRequest(mockHttpServletRequest);

		return draft.getEditURL(
			_REDIRECT,
			RequestBackedPortletURLFactoryUtil.create(mockHttpServletRequest));
	}

	private SafeCloseable _setPreviewIdWithSafeCloseable(
		long classPK, Serializable previewPrimaryKey) {

		Long previewId = PreviewableResolverUtil.addPreviewableMap(
			HashMapBuilder.<Class<?>, Map<Serializable, Serializable>>put(
				getModelClass(),
				HashMapBuilder.<Serializable, Serializable>put(
					() -> {
						LaunchEntryType.Item item = getItem(classPK);

						return item.getPrimaryKey();
					},
					previewPrimaryKey
				).build()
			).build());

		SafeCloseable safeCloseable =
			PreviewableResolverUtil.setPreviewIdWithSafeCloseable(previewId);

		return () -> {
			safeCloseable.close();

			PreviewableResolverUtil.removePreviewableMap(previewId);
		};
	}

	private static final String _REDIRECT =
		"/group/control_panel/manage?p_p_id=" +
			"com_liferay_launch_web_portlet_LaunchPortlet";

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private LaunchEntryTypeRegistry _launchEntryTypeRegistry;

}