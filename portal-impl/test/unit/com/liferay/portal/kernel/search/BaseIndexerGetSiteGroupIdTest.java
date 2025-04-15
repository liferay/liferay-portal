/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.search;

import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupWrapper;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceWrapper;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Collections;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Miguel Angelo Caldas Gallindo
 * @author André de Oliveira
 */
public class BaseIndexerGetSiteGroupIdTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		PropsTestUtil.setProps(Collections.emptyMap());

		_indexer = new TestIndexer();
	}

	@Test
	public void testGetSiteGroupId() throws Exception {
		long groupId = RandomTestUtil.randomLong();

		_setUpGroup(groupId, 0, false);

		Assert.assertEquals(groupId, _indexer.getSiteGroupId(groupId));
	}

	@Test
	public void testGetSiteGroupIdLayout() throws Exception {
		long groupId = RandomTestUtil.randomLong();
		long parentGroupId = RandomTestUtil.randomLong();

		_setUpGroup(groupId, parentGroupId, false);

		Assert.assertEquals(parentGroupId, _indexer.getSiteGroupId(groupId));
	}

	@Test
	public void testGetSiteGroupIdNonexistent() throws Exception {
		long groupId = RandomTestUtil.randomLong();

		_setUpGroup(0, 0, false);

		Assert.assertEquals(groupId, _indexer.getSiteGroupId(groupId));
	}

	@Test
	public void testIsStagingGroup() throws Exception {
		long groupId = RandomTestUtil.randomLong();

		_setUpGroup(groupId, 0, true);

		Assert.assertTrue(_indexer.isStagingGroup(groupId));
	}

	@Test
	public void testIsStagingGroupLayout() throws Exception {
		long groupId = RandomTestUtil.randomLong();
		long parentGroupId = RandomTestUtil.randomLong();

		_setUpGroup(groupId, parentGroupId, true);

		Assert.assertTrue(_indexer.isStagingGroup(groupId));
	}

	@Test
	public void testIsStagingGroupNonexistent() throws Exception {
		long groupId = RandomTestUtil.randomLong();

		_setUpGroup(0, 0, false);

		Assert.assertFalse(_indexer.isStagingGroup(groupId));
	}

	private Group _getGroup(
		long groupId, Group parentGroup, boolean stagingGroup) {

		if (groupId <= 0) {
			return null;
		}

		return new GroupWrapper(null) {

			@Override
			public long getGroupId() {
				return groupId;
			}

			@Override
			public Group getParentGroup() {
				return parentGroup;
			}

			@Override
			public long getParentGroupId() {
				if (parentGroup == null) {
					return 0;
				}

				return parentGroup.getGroupId();
			}

			@Override
			public boolean isLayout() {
				if (parentGroup == null) {
					return false;
				}

				return true;
			}

			@Override
			public boolean isStagingGroup() {
				return stagingGroup;
			}

		};
	}

	private void _setUpGroup(
		long groupId, long parentGroupId, boolean stagingGroup) {

		Group parentGroup = _getGroup(parentGroupId, null, stagingGroup);

		Group group = _getGroup(groupId, parentGroup, stagingGroup);

		ReflectionTestUtil.setFieldValue(
			GroupLocalServiceUtil.class, "_service",
			new GroupLocalServiceWrapper() {

				@Override
				public Group getGroup(long groupId) throws PortalException {
					if (group == null) {
						throw new NoSuchGroupException();
					}

					if (groupId == group.getGroupId()) {
						return group;
					}
					else if ((parentGroup != null) &&
							 (groupId == parentGroup.getGroupId())) {

						return parentGroup;
					}

					return null;
				}

			});
	}

	private BaseIndexer<Object> _indexer;

	private static class TestIndexer extends BaseIndexer<Object> {

		@Override
		public String getClassName() {
			return null;
		}

		@Override
		protected void doDelete(Object object) throws Exception {
		}

		@Override
		protected Document doGetDocument(Object object) throws Exception {
			return null;
		}

		@Override
		protected Summary doGetSummary(
				Document document, Locale locale, String snippet,
				PortletRequest portletRequest, PortletResponse portletResponse)
			throws Exception {

			return null;
		}

		@Override
		protected void doReindex(Object object) throws Exception {
		}

		@Override
		protected void doReindex(String className, long classPK)
			throws Exception {
		}

		@Override
		protected void doReindex(String[] ids) throws Exception {
		}

	}

}