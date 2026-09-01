/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.servlet.taglib;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
public class FragmentBreadcrumbEntryContributorImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpFragmentBreadcrumbEntryContributorImpl();
		_setUpMockHttpServletRequest();
		_setUpPortal();
	}

	@After
	public void tearDown() {
		_designLibraryUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-97637")
	public void testGetBreadcrumbEntries() {
		_testGetBreadcrumbEntriesForDefaultCollection();
		_testGetBreadcrumbEntriesForFragmentEntry();
		_testGetBreadcrumbEntriesForSpecificCollection();
		_testGetBreadcrumbEntriesWhenNotInFragmentPortlet();
		_testGetBreadcrumbEntriesWhenScopeIsNotDesignLibrary();
	}

	private FragmentCollection _mockFragmentCollection(
		long fragmentCollectionId) {

		FragmentCollection fragmentCollection = Mockito.mock(
			FragmentCollection.class);

		Mockito.when(
			fragmentCollection.getFragmentCollectionId()
		).thenReturn(
			fragmentCollectionId
		);

		Mockito.when(
			fragmentCollection.getName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_fragmentCollectionLocalService.fetchFragmentCollection(
				fragmentCollectionId)
		).thenReturn(
			fragmentCollection
		);

		return fragmentCollection;
	}

	private FragmentEntry _mockFragmentEntry() {
		FragmentEntry fragmentEntry = Mockito.mock(FragmentEntry.class);

		Mockito.when(
			fragmentEntry.getFragmentCollectionId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			fragmentEntry.getFragmentEntryId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			fragmentEntry.getName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_fragmentEntryLocalService.fetchFragmentEntry(
				fragmentEntry.getFragmentEntryId())
		).thenReturn(
			fragmentEntry
		);

		return fragmentEntry;
	}

	private void _setParameter(String name, String value) {
		_mockHttpServletRequest.removeAllParameters();

		_mockHttpServletRequest.addParameter(name, value);
	}

	private void _setUpFragmentBreadcrumbEntryContributorImpl() {
		ReflectionTestUtil.setFieldValue(
			_fragmentBreadcrumbEntryContributorImpl,
			"_fragmentCollectionLocalService", _fragmentCollectionLocalService);
		ReflectionTestUtil.setFieldValue(
			_fragmentBreadcrumbEntryContributorImpl,
			"_fragmentEntryLocalService", _fragmentEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_fragmentBreadcrumbEntryContributorImpl, "_portal", _portal);
	}

	private void _setUpFragmentPortletInDesignLibraryScope() {
		Mockito.when(
			_portletDisplay.getPortletName()
		).thenReturn(
			FragmentPortletKeys.FRAGMENT
		);

		_designLibraryUtilMockedStatic.when(
			() -> DesignLibraryUtil.isDesignLibraryScope(_group)
		).thenReturn(
			true
		);
	}

	private void _setUpMockHttpServletRequest() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getPortletDisplay()
		).thenReturn(
			_portletDisplay
		);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			_group
		);

		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);
	}

	private void _setUpPortal() {
		Mockito.when(
			_portal.getPortletNamespace(FragmentPortletKeys.FRAGMENT)
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_portal.getControlPanelPortletURL(
				_mockHttpServletRequest, _group, FragmentPortletKeys.FRAGMENT,
				0L, 0L, PortletRequest.RENDER_PHASE)
		).thenReturn(
			Mockito.mock(PortletURL.class)
		);
	}

	private void _testGetBreadcrumbEntriesForDefaultCollection() {
		_setUpFragmentPortletInDesignLibraryScope();

		List<BreadcrumbEntry> originalBreadcrumbEntries =
			Collections.singletonList(new BreadcrumbEntry());

		Assert.assertSame(
			originalBreadcrumbEntries,
			_fragmentBreadcrumbEntryContributorImpl.getBreadcrumbEntries(
				originalBreadcrumbEntries, _mockHttpServletRequest));
	}

	private void _testGetBreadcrumbEntriesForFragmentEntry() {
		_setUpFragmentPortletInDesignLibraryScope();

		BreadcrumbEntry originalBreadcrumbEntry = new BreadcrumbEntry();

		FragmentEntry fragmentEntry = _mockFragmentEntry();

		_setParameter(
			"fragmentEntryId",
			String.valueOf(fragmentEntry.getFragmentEntryId()));

		FragmentCollection fragmentCollection = _mockFragmentCollection(
			fragmentEntry.getFragmentCollectionId());

		List<BreadcrumbEntry> breadcrumbEntries =
			_fragmentBreadcrumbEntryContributorImpl.getBreadcrumbEntries(
				Collections.singletonList(originalBreadcrumbEntry),
				_mockHttpServletRequest);

		BreadcrumbEntry fragmentCollectionBreadcrumbEntry =
			breadcrumbEntries.get(0);

		Assert.assertEquals(
			fragmentCollection.getName(),
			fragmentCollectionBreadcrumbEntry.getTitle());

		BreadcrumbEntry fragmentEntryBreadcrumbEntry = breadcrumbEntries.get(1);

		Assert.assertEquals(
			fragmentEntry.getName(), fragmentEntryBreadcrumbEntry.getTitle());

		Assert.assertSame(originalBreadcrumbEntry, breadcrumbEntries.get(2));

		Assert.assertEquals(
			breadcrumbEntries.toString(), 3, breadcrumbEntries.size());
	}

	private void _testGetBreadcrumbEntriesForSpecificCollection() {
		_setUpFragmentPortletInDesignLibraryScope();

		BreadcrumbEntry originalBreadcrumbEntry = new BreadcrumbEntry();

		FragmentCollection fragmentCollection = _mockFragmentCollection(
			RandomTestUtil.randomLong());

		_setParameter(
			"fragmentCollectionId",
			String.valueOf(fragmentCollection.getFragmentCollectionId()));

		List<BreadcrumbEntry> breadcrumbEntries =
			_fragmentBreadcrumbEntryContributorImpl.getBreadcrumbEntries(
				Collections.singletonList(originalBreadcrumbEntry),
				_mockHttpServletRequest);

		BreadcrumbEntry fragmentCollectionBreadcrumbEntry =
			breadcrumbEntries.get(0);

		Assert.assertEquals(
			fragmentCollection.getName(),
			fragmentCollectionBreadcrumbEntry.getTitle());

		Assert.assertSame(originalBreadcrumbEntry, breadcrumbEntries.get(1));

		Assert.assertEquals(
			breadcrumbEntries.toString(), 2, breadcrumbEntries.size());
	}

	private void _testGetBreadcrumbEntriesWhenNotInFragmentPortlet() {
		Mockito.when(
			_portletDisplay.getPortletName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		_designLibraryUtilMockedStatic.when(
			() -> DesignLibraryUtil.isDesignLibraryScope(_group)
		).thenReturn(
			true
		);

		List<BreadcrumbEntry> originalBreadcrumbEntries =
			Collections.singletonList(new BreadcrumbEntry());

		Assert.assertSame(
			originalBreadcrumbEntries,
			_fragmentBreadcrumbEntryContributorImpl.getBreadcrumbEntries(
				originalBreadcrumbEntries, _mockHttpServletRequest));
	}

	private void _testGetBreadcrumbEntriesWhenScopeIsNotDesignLibrary() {
		Mockito.when(
			_portletDisplay.getPortletName()
		).thenReturn(
			FragmentPortletKeys.FRAGMENT
		);

		_designLibraryUtilMockedStatic.when(
			() -> DesignLibraryUtil.isDesignLibraryScope(_group)
		).thenReturn(
			false
		);

		List<BreadcrumbEntry> originalBreadcrumbEntries =
			Collections.singletonList(new BreadcrumbEntry());

		Assert.assertSame(
			originalBreadcrumbEntries,
			_fragmentBreadcrumbEntryContributorImpl.getBreadcrumbEntries(
				originalBreadcrumbEntries, _mockHttpServletRequest));
	}

	private final MockedStatic<DesignLibraryUtil>
		_designLibraryUtilMockedStatic = Mockito.mockStatic(
			DesignLibraryUtil.class);
	private final FragmentBreadcrumbEntryContributorImpl
		_fragmentBreadcrumbEntryContributorImpl =
			new FragmentBreadcrumbEntryContributorImpl();
	private final FragmentCollectionLocalService
		_fragmentCollectionLocalService = Mockito.mock(
			FragmentCollectionLocalService.class);
	private final FragmentEntryLocalService _fragmentEntryLocalService =
		Mockito.mock(FragmentEntryLocalService.class);
	private final Group _group = Mockito.mock(Group.class);
	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();
	private final Portal _portal = Mockito.mock(Portal.class);
	private final PortletDisplay _portletDisplay = Mockito.mock(
		PortletDisplay.class);

}