/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.internal.display.context;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.item.selector.provider.GroupItemSelectorProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Cristina González
 */
public class GroupSelectorDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Mockito.when(
			FrameworkUtil.getBundle(Mockito.any())
		).thenReturn(
			bundleContext.getBundle()
		);

		Mockito.when(
			PortalUtil.getCompanyId(Mockito.any(PortletRequest.class))
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			PortalUtil.getHttpServletRequest(
				Mockito.any(LiferayPortletRequest.class))
		).thenAnswer(
			invocationOnMock -> {
				LiferayPortletRequest liferayPortletRequest =
					invocationOnMock.getArgument(0);

				return liferayPortletRequest.getHttpServletRequest();
			}
		);

		_groupItemSelectorProviderServiceRegistration =
			bundleContext.registerService(
				GroupItemSelectorProvider.class,
				new MockGroupItemSelectorProvider("test"), null);
		_itemSelectorServiceRegistration = bundleContext.registerService(
			ItemSelector.class, _itemSelector, null);
		_spaceDepotGroupItemSelectorProviderServiceRegistration =
			bundleContext.registerService(
				GroupItemSelectorProvider.class,
				new MockGroupItemSelectorProvider("space-depot"), null);
	}

	@AfterClass
	public static void tearDownClass() {
		_frameworkUtilMockedStatic.close();
		_groupItemSelectorProviderServiceRegistration.unregister();
		_itemSelectorServiceRegistration.unregister();
		_portalUtilMockedStatic.close();
		_spaceDepotGroupItemSelectorProviderServiceRegistration.unregister();
	}

	@After
	public void tearDown() {
		Mockito.reset(_itemSelector);
	}

	@Test
	public void testGetGroupItemSelectorIcon() {
		GroupSelectorDisplayContext groupSelectorDisplayContext =
			new GroupSelectorDisplayContext(
				"test", new MockLiferayResourceRequest());

		Assert.assertEquals(
			"icon", groupSelectorDisplayContext.getGroupItemSelectorIcon());
	}

	@Test
	public void testGetGroupItemSelectorLabel() {
		GroupSelectorDisplayContext groupSelectorDisplayContext =
			new GroupSelectorDisplayContext(new MockLiferayResourceRequest());

		Assert.assertEquals(
			"label",
			groupSelectorDisplayContext.getGroupItemSelectorLabel("test"));
	}

	@Test
	public void testGetGroupTypes() {
		_testGetGroupTypesWithDefaultSelectedTabRequestAttribute();
		_testGetGroupTypesWithFileItemSelectorCriterion();
		_testGetGroupTypesWithJournalArticleInfoItemItemSelectorCriterion();
		_testGetGroupTypesWithLegacyItemSelectorViewSelectedTab();
		_testGetGroupTypesWithObjectEntryItemSelectorViewSelectedTab();
		_testGetGroupTypesWithoutJournalArticleInfoItemItemSelectorCriterion();
	}

	private void _testGetGroupTypesWithDefaultSelectedTabRequestAttribute() {
		Mockito.when(
			_itemSelector.getItemSelectorCriteria(Mockito.anyMap())
		).thenReturn(
			Collections.singletonList(new InfoItemItemSelectorCriterion())
		);

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.addParameter("criteria", "infoitem");
		mockLiferayResourceRequest.setAttribute(
			"liferay-item-selector:group-selector:selectedTab",
			"com.liferay.document.library.item.selector.web.internal.info." +
				"item.DLInfoItemItemSelectorView_Documents and Media");

		GroupSelectorDisplayContext groupSelectorDisplayContext =
			new GroupSelectorDisplayContext(mockLiferayResourceRequest);

		Assert.assertEquals(
			Collections.singleton("test"),
			groupSelectorDisplayContext.getGroupTypes());
	}

	private void _testGetGroupTypesWithFileItemSelectorCriterion() {
		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.addParameter("criteria", "file");

		GroupSelectorDisplayContext groupSelectorDisplayContext =
			new GroupSelectorDisplayContext(mockLiferayResourceRequest);

		Assert.assertEquals(
			Collections.singleton("test"),
			groupSelectorDisplayContext.getGroupTypes());
	}

	private void _testGetGroupTypesWithJournalArticleInfoItemItemSelectorCriterion() {
		InfoItemItemSelectorCriterion infoItemItemSelectorCriterion =
			new InfoItemItemSelectorCriterion();

		infoItemItemSelectorCriterion.setItemType(
			"com.liferay.journal.model.JournalArticle");

		Mockito.when(
			_itemSelector.getItemSelectorCriteria(Mockito.anyMap())
		).thenReturn(
			Collections.singletonList(infoItemItemSelectorCriterion)
		);

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.addParameter("criteria", "infoitem");

		GroupSelectorDisplayContext groupSelectorDisplayContext =
			new GroupSelectorDisplayContext(mockLiferayResourceRequest);

		Assert.assertEquals(
			Collections.singleton("test"),
			groupSelectorDisplayContext.getGroupTypes());
	}

	private void _testGetGroupTypesWithLegacyItemSelectorViewSelectedTab() {
		Mockito.when(
			_itemSelector.getItemSelectorCriteria(Mockito.anyMap())
		).thenReturn(
			Collections.singletonList(new InfoItemItemSelectorCriterion())
		);

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.addParameter("criteria", "infoitem");
		mockLiferayResourceRequest.addParameter(
			"selectedTab",
			"com.liferay.commerce.order.content.web.internal.item.selector." +
				"CommerceOrderItemSelectorView_Orders");

		GroupSelectorDisplayContext groupSelectorDisplayContext =
			new GroupSelectorDisplayContext(mockLiferayResourceRequest);

		Assert.assertEquals(
			Collections.singleton("test"),
			groupSelectorDisplayContext.getGroupTypes());
	}

	private void _testGetGroupTypesWithObjectEntryItemSelectorViewSelectedTab() {
		Mockito.when(
			_itemSelector.getItemSelectorCriteria(Mockito.anyMap())
		).thenReturn(
			Collections.singletonList(new InfoItemItemSelectorCriterion())
		);

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.addParameter("criteria", "infoitem");
		mockLiferayResourceRequest.addParameter(
			"selectedTab",
			"com.liferay.object.web.internal.item.selector." +
				"ObjectEntryItemSelectorView_Basic Web Content");

		GroupSelectorDisplayContext groupSelectorDisplayContext =
			new GroupSelectorDisplayContext(mockLiferayResourceRequest);

		Assert.assertEquals(
			SetUtil.fromArray("space-depot", "test"),
			groupSelectorDisplayContext.getGroupTypes());
	}

	private void _testGetGroupTypesWithoutJournalArticleInfoItemItemSelectorCriterion() {
		Mockito.when(
			_itemSelector.getItemSelectorCriteria(Mockito.anyMap())
		).thenReturn(
			Collections.singletonList(new InfoItemItemSelectorCriterion())
		);

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.addParameter("criteria", "infoitem");

		GroupSelectorDisplayContext groupSelectorDisplayContext =
			new GroupSelectorDisplayContext(mockLiferayResourceRequest);

		Assert.assertEquals(
			SetUtil.fromArray("space-depot", "test"),
			groupSelectorDisplayContext.getGroupTypes());
	}

	private static final MockedStatic<FrameworkUtil>
		_frameworkUtilMockedStatic = Mockito.mockStatic(FrameworkUtil.class);
	private static ServiceRegistration<GroupItemSelectorProvider>
		_groupItemSelectorProviderServiceRegistration;
	private static final ItemSelector _itemSelector = Mockito.mock(
		ItemSelector.class);
	private static ServiceRegistration<ItemSelector>
		_itemSelectorServiceRegistration;
	private static final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);
	private static ServiceRegistration<GroupItemSelectorProvider>
		_spaceDepotGroupItemSelectorProviderServiceRegistration;

	private static class MockGroupItemSelectorProvider
		implements GroupItemSelectorProvider {

		public MockGroupItemSelectorProvider(String groupType) {
			_groupType = groupType;
		}

		@Override
		public String getEmptyResultsMessage() {
			return null;
		}

		@Override
		public List<Group> getGroups(
			long companyId, long groupId, String keywords, int start, int end) {

			return Collections.singletonList(Mockito.mock(Group.class));
		}

		@Override
		public int getGroupsCount(
			long companyId, long groupId, String keywords) {

			return 3;
		}

		@Override
		public String getGroupType() {
			return _groupType;
		}

		@Override
		public String getIcon() {
			return "icon";
		}

		@Override
		public String getLabel(Locale locale) {
			return "label";
		}

		private final String _groupType;

	}

}