/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.layout.display.page;

import com.liferay.asset.util.AssetHelper;
import com.liferay.friendly.url.info.item.provider.InfoItemFriendlyURLProvider;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class ObjectEntryLayoutDisplayPageProviderTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_groupLocalService = Mockito.mock(GroupLocalService.class);
		_objectDefinition = Mockito.mock(ObjectDefinition.class);
		_objectEntryLocalService = Mockito.mock(ObjectEntryLocalService.class);

		_objectEntryLayoutDisplayPageProvider =
			new ObjectEntryLayoutDisplayPageProvider(
				Mockito.mock(AssetHelper.class), _groupLocalService,
				Mockito.mock(InfoItemFriendlyURLProvider.class),
				_objectDefinition,
				Mockito.mock(ObjectDefinitionLocalService.class),
				_objectEntryLocalService,
				Mockito.mock(ObjectEntryManager.class),
				Mockito.mock(ObjectRelationshipLocalService.class),
				Mockito.mock(UserLocalService.class));
	}

	@Test
	public void testGetLayoutDisplayPageObjectProvider() {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(_COMPANY_ID)) {

			_testGetLayoutDisplayPageObjectProviderWithGroupPrefix();
			_testGetLayoutDisplayPageObjectProviderWithGroupPrefixAndNestedURLTitle();
			_testGetLayoutDisplayPageObjectProviderWithSlashWithoutGroupPrefix();
			_testGetLayoutDisplayPageObjectProviderWithoutSlash();
		}
	}

	private void _assertGetLayoutDisplayPageObjectProvider(
		ObjectEntry expectedObjectEntry, String urlTitle) {

		LayoutDisplayPageObjectProvider<ObjectEntry>
			layoutDisplayPageObjectProvider =
				_objectEntryLayoutDisplayPageProvider.
					getLayoutDisplayPageObjectProvider(
						_REQUEST_GROUP_ID, urlTitle);

		Assert.assertSame(
			expectedObjectEntry,
			layoutDisplayPageObjectProvider.getDisplayObject());
	}

	private void _setUpFriendlyURLGroup(String friendlyURL, long groupId) {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			_groupLocalService.fetchFriendlyURLGroup(_COMPANY_ID, friendlyURL)
		).thenReturn(
			group
		);
	}

	private ObjectEntry _setUpObjectEntry(long groupId, String urlTitle) {
		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			_objectEntryLocalService.fetchObjectEntry(
				groupId, _objectDefinition, urlTitle)
		).thenReturn(
			objectEntry
		);

		return objectEntry;
	}

	private void _testGetLayoutDisplayPageObjectProviderWithGroupPrefix() {
		ObjectEntry objectEntry = _setUpObjectEntry(_GROUP_ID, "parent/child");

		_setUpFriendlyURLGroup(
			StringPool.SLASH + _GROUP_FRIENDLY_URL, _GROUP_ID);

		_assertGetLayoutDisplayPageObjectProvider(
			objectEntry, _GROUP_FRIENDLY_URL + "/parent/child");
	}

	private void _testGetLayoutDisplayPageObjectProviderWithGroupPrefixAndNestedURLTitle() {
		ObjectEntry objectEntry = _setUpObjectEntry(
			_GROUP_ID, "parent/child/grandchild");

		_setUpFriendlyURLGroup(
			StringPool.SLASH + _GROUP_FRIENDLY_URL, _GROUP_ID);

		_assertGetLayoutDisplayPageObjectProvider(
			objectEntry, _GROUP_FRIENDLY_URL + "/parent/child/grandchild");
	}

	private void _testGetLayoutDisplayPageObjectProviderWithSlashWithoutGroupPrefix() {
		ObjectEntry objectEntry = _setUpObjectEntry(
			_REQUEST_GROUP_ID, "parent/child");

		_assertGetLayoutDisplayPageObjectProvider(objectEntry, "parent/child");
	}

	private void _testGetLayoutDisplayPageObjectProviderWithoutSlash() {
		ObjectEntry objectEntry = _setUpObjectEntry(
			_REQUEST_GROUP_ID, _URL_TITLE);

		_assertGetLayoutDisplayPageObjectProvider(objectEntry, _URL_TITLE);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _GROUP_FRIENDLY_URL =
		RandomTestUtil.randomString();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final long _REQUEST_GROUP_ID = RandomTestUtil.randomLong();

	private static final String _URL_TITLE = RandomTestUtil.randomString();

	private GroupLocalService _groupLocalService;
	private ObjectDefinition _objectDefinition;
	private ObjectEntryLayoutDisplayPageProvider
		_objectEntryLayoutDisplayPageProvider;
	private ObjectEntryLocalService _objectEntryLocalService;

}