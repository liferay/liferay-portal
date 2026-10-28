/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.entry.manager.test;

import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.launch.entry.manager.LaunchEntryManagerRegistry;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.UnsafeSupplierValue;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
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
public abstract class BaseLaunchEntryManagerTestCase<T> {

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

		T entryValue = getEntryValue(classPK);

		LaunchEntryManager.Draft draft1 = addDraft(classPK);
		LaunchEntryManager.Draft draft2 = addDraft(classPK);

		Assert.assertNotEquals(draft1.getDraftId(), draft2.getDraftId());

		T value1 = createValue();

		updateDraft(draft1, value1);

		T value2 = createValue();

		updateDraft(draft2, value2);

		Assert.assertEquals(value1, _getDraftValue(classPK, draft1));
		Assert.assertEquals(value2, _getDraftValue(classPK, draft2));

		Assert.assertEquals(entryValue, getEntryValue(classPK));

		T value3 = createValue();

		updateLiveEntry(classPK, value3);

		Assert.assertEquals(value3, getEntryValue(classPK));

		Assert.assertEquals(value1, _getDraftValue(classPK, draft1));
		Assert.assertEquals(value2, _getDraftValue(classPK, draft2));

		Assert.assertEquals(value3, getDraftValue(addDraft(classPK)));

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		Assert.assertThrows(
			PortalException.class,
			() -> launchEntryManager.addDraft(
				classPK, RandomTestUtil.randomLong(),
				String.valueOf(draft1.getDraftId())));
	}

	@Test
	public void testDeleteDraft() throws Exception {
		long classPK = addEntry();

		T entryValue = getEntryValue(classPK);
		LaunchEntryManager.Version publishedVersion = getPublishedVersion(
			classPK);

		LaunchEntryManager.Draft draft = addDraft(classPK);

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		launchEntryManager.deleteDraft(classPK, draft.getDraftId());

		Assert.assertNull(
			launchEntryManager.fetchDraft(classPK, draft.getDraftId()));

		launchEntryManager.deleteDraft(classPK, draft.getDraftId());

		Assert.assertEquals(entryValue, getEntryValue(classPK));

		LaunchEntryManager.Version currentPublishedVersion =
			getPublishedVersion(classPK);

		Assert.assertEquals(
			publishedVersion.getVersionKey(),
			currentPublishedVersion.getVersionKey());
	}

	@Test
	public void testFetchDraft() throws Exception {
		long classPK = addEntry();

		T value = createValue();

		LaunchEntryManager.Draft draft = addDraft(classPK);

		updateDraft(draft, value);

		LaunchEntryManager.Draft fetchedDraft = getDraft(
			classPK, draft.getDraftId());

		Assert.assertEquals(draft.getDraftId(), fetchedDraft.getDraftId());
		Assert.assertEquals(value, getDraftValue(fetchedDraft));

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		Assert.assertNull(
			launchEntryManager.fetchDraft(
				classPK, RandomTestUtil.randomLong()));
	}

	@Test
	public void testFetchItem() throws Exception {
		long classPK = addEntry();

		LaunchEntryManager.Item item = getItem(classPK);

		Assert.assertEquals(group.getGroupId(), item.getGroupId());
		Assert.assertNotNull(item.getTitle(LocaleUtil.getSiteDefault()));
		Assert.assertNotNull(item.getTypeName(LocaleUtil.getSiteDefault()));

		List<Serializable> listedPrimaryKeys = getListedPrimaryKeys(classPK);

		Assert.assertTrue(
			listedPrimaryKeys.toString(),
			listedPrimaryKeys.contains(item.getPrimaryKey()));

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		Assert.assertNull(
			launchEntryManager.fetchItem(RandomTestUtil.randomLong()));
	}

	@Test
	public void testFetchPublishedVersion() throws Exception {
		long classPK = addEntry();

		LaunchEntryManager.Version publishedVersion = getPublishedVersion(
			classPK);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, publishedVersion.getStatus());
	}

	@Test
	public void testFetchVersion() throws Exception {
		long classPK = addEntry();

		LaunchEntryManager.Version publishedVersion = getPublishedVersion(
			classPK);

		LaunchEntryManager.Version fetchedVersion = getVersion(
			classPK, publishedVersion.getVersionKey());

		Assert.assertEquals(
			publishedVersion.getVersionKey(), fetchedVersion.getVersionKey());

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		Assert.assertNull(
			launchEntryManager.fetchVersion(
				classPK, String.valueOf(RandomTestUtil.randomLong())));
		Assert.assertNull(
			launchEntryManager.fetchVersion(
				RandomTestUtil.randomLong(), publishedVersion.getVersionKey()));

		LaunchEntryManager.Draft draft = addDraft(classPK);

		Assert.assertNull(
			launchEntryManager.fetchVersion(
				classPK, String.valueOf(draft.getDraftId())));
	}

	@Test
	public void testGetClassPK() throws Exception {
		long classPK = addEntry();

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		Assert.assertEquals(
			classPK,
			launchEntryManager.getClassPK(getSelectedItemData(classPK)));
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

		T entryValue = getEntryValue(classPK);

		T value1 = createValue();

		LaunchEntryManager.Draft draft1 = addDraft(classPK);

		updateDraft(draft1, value1);

		T value2 = createValue();

		LaunchEntryManager.Draft draft2 = addDraft(classPK);

		updateDraft(draft2, value2);

		draft1 = getDraft(classPK, draft1.getDraftId());

		try (SafeCloseable safeCloseable = setPreviewIdWithSafeCloseable(
				classPK, draft1::getPreviewBaseModel)) {

			Assert.assertEquals(value1, getEntryValue(classPK));
		}

		draft2 = getDraft(classPK, draft2.getDraftId());

		try (SafeCloseable safeCloseable = setPreviewIdWithSafeCloseable(
				classPK, draft2::getPreviewBaseModel)) {

			Assert.assertEquals(value2, getEntryValue(classPK));
		}

		Assert.assertEquals(entryValue, getEntryValue(classPK));
	}

	@Test
	public void testGetVersions() throws Exception {
		long classPK = addEntry();

		LaunchEntryManager.Draft draft = addDraft(classPK);
		LaunchEntryManager.Draft publishedDraft = addDraft(classPK);

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		LaunchEntryManager.Version publishedVersion =
			launchEntryManager.publishDraft(
				classPK, publishedDraft.getDraftId());

		List<String> versionKeys = TransformUtil.transform(
			launchEntryManager.getVersions(classPK),
			LaunchEntryManager.Version::getVersionKey);

		Assert.assertTrue(
			versionKeys.toString(),
			versionKeys.contains(publishedVersion.getVersionKey()));
		Assert.assertFalse(
			versionKeys.toString(),
			versionKeys.contains(String.valueOf(draft.getDraftId())));

		updateDraft(draft, createValue());
		updateDraft(draft, createValue());

		Assert.assertEquals(
			versionKeys,
			TransformUtil.transform(
				launchEntryManager.getVersions(classPK),
				LaunchEntryManager.Version::getVersionKey));

		for (String versionKey : versionKeys) {
			LaunchEntryManager.Version version = getVersion(
				classPK, versionKey);

			Assert.assertEquals(versionKey, version.getVersionKey());

			Assert.assertNotNull(
				versionKey,
				launchEntryManager.addDraft(
					classPK, RandomTestUtil.randomLong(), versionKey));
		}
	}

	@Test
	public void testPublishDraft() throws Exception {
		long classPK = addEntry();

		T value1 = createValue();

		LaunchEntryManager.Draft draft1 = addDraft(classPK);

		updateDraft(draft1, value1);

		T value2 = createValue();

		LaunchEntryManager.Draft draft2 = addDraft(classPK);

		updateDraft(draft2, value2);

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		LaunchEntryManager.Version publishedVersion2 =
			launchEntryManager.publishDraft(classPK, draft2.getDraftId());

		_assertPublishedVersion(classPK, draft2, value2, publishedVersion2);

		Assert.assertEquals(value1, _getDraftValue(classPK, draft1));

		LaunchEntryManager.Version publishedVersion1 =
			launchEntryManager.publishDraft(classPK, draft1.getDraftId());

		_assertPublishedVersion(classPK, draft1, value1, publishedVersion1);

		Assert.assertThrows(
			PortalException.class,
			() -> launchEntryManager.publishDraft(
				classPK, draft1.getDraftId()));
	}

	protected LaunchEntryManager.Draft addDraft(long classPK) throws Exception {
		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		LaunchEntryManager.Version publishedVersion = getPublishedVersion(
			classPK);

		return launchEntryManager.addDraft(
			classPK, RandomTestUtil.randomLong(),
			publishedVersion.getVersionKey());
	}

	protected abstract long addEntry() throws Exception;

	protected abstract T createValue();

	protected abstract String getBackURLParameterName();

	protected LaunchEntryManager.Draft getDraft(long classPK, long draftId) {
		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		LaunchEntryManager.Draft draft = launchEntryManager.fetchDraft(
			classPK, draftId);

		Assert.assertNotNull(String.valueOf(draftId), draft);

		return draft;
	}

	protected abstract T getDraftValue(LaunchEntryManager.Draft draft)
		throws Exception;

	protected abstract T getEntryValue(long classPK) throws Exception;

	protected LaunchEntryManager.Item getItem(long classPK) {
		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		LaunchEntryManager.Item item = launchEntryManager.fetchItem(classPK);

		Assert.assertNotNull(item);

		return item;
	}

	protected LaunchEntryManager getLaunchEntryManager() {
		Class<?> modelClass = getModelClass();

		return _launchEntryManagerRegistry.getLaunchEntryManager(
			modelClass.getName());
	}

	protected abstract List<Serializable> getListedPrimaryKeys(long classPK)
		throws Exception;

	protected abstract Class<?> getModelClass();

	protected LaunchEntryManager.Version getPublishedVersion(long classPK) {
		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		LaunchEntryManager.Version publishedVersion =
			launchEntryManager.fetchPublishedVersion(classPK);

		Assert.assertNotNull(publishedVersion);

		return publishedVersion;
	}

	protected abstract Map<String, String> getSelectedItemData(long classPK)
		throws Exception;

	protected LaunchEntryManager.Version getVersion(
		long classPK, String versionKey) {

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		LaunchEntryManager.Version version = launchEntryManager.fetchVersion(
			classPK, versionKey);

		Assert.assertNotNull(versionKey, version);

		return version;
	}

	protected SafeCloseable setPreviewIdWithSafeCloseable(
		long classPK,
		UnsafeSupplier<BaseModel<?>, PortalException> unsafeSupplier) {

		Long previewId = PreviewableResolverUtil.addPreviewableMap(
			HashMapBuilder.<Class<?>, Map<Serializable, Object>>put(
				getModelClass(),
				HashMapBuilder.<Serializable, Object>put(
					() -> {
						LaunchEntryManager.Item item = getItem(classPK);

						return item.getPrimaryKey();
					},
					new UnsafeSupplierValue<>(unsafeSupplier)
				).build()
			).build());

		SafeCloseable safeCloseable =
			PreviewableResolverUtil.setPreviewIdWithSafeCloseable(previewId);

		return () -> {
			safeCloseable.close();

			PreviewableResolverUtil.removePreviewableMap(previewId);
		};
	}

	protected abstract void updateDraft(LaunchEntryManager.Draft draft, T value)
		throws Exception;

	protected abstract void updateLiveEntry(long classPK, T value)
		throws Exception;

	protected Group group;
	protected ServiceContext serviceContext;

	private void _assertPublishedVersion(
			long classPK, LaunchEntryManager.Draft draft, T expectedValue,
			LaunchEntryManager.Version publishedVersion)
		throws Exception {

		Assert.assertEquals(expectedValue, getEntryValue(classPK));

		LaunchEntryManager.Version currentPublishedVersion =
			getPublishedVersion(classPK);

		Assert.assertEquals(
			publishedVersion.getVersionKey(),
			currentPublishedVersion.getVersionKey());

		LaunchEntryManager.Version version = getVersion(
			classPK, publishedVersion.getVersionKey());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, version.getStatus());

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		Assert.assertNull(
			launchEntryManager.fetchDraft(classPK, draft.getDraftId()));
	}

	private T _getDraftValue(long classPK, LaunchEntryManager.Draft draft)
		throws Exception {

		return getDraftValue(getDraft(classPK, draft.getDraftId()));
	}

	private String _getEditURL(LaunchEntryManager.Draft draft)
		throws Exception {

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

	private static final String _REDIRECT =
		"/group/control_panel/manage?p_p_id=" +
			"com_liferay_launch_web_portlet_LaunchPortlet";

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private LaunchEntryManagerRegistry _launchEntryManagerRegistry;

}