/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.events.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.LifecycleEvent;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.configuration.SegmentsCompanyConfiguration;
import com.liferay.segments.configuration.SegmentsConfiguration;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.constants.SegmentsWebKeys;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class SegmentsServicePreActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testProcessLifecycleEvent1() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"segmentationEnabled", true
					).build())) {

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							SegmentsCompanyConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"segmentationEnabled", true
							).build())) {

				_segmentsConfigurationProvider.
					clearSegmentsCompanyConfigurations();

				LifecycleAction lifecycleAction = _getLifecycleAction();

				MockHttpServletRequest mockHttpServletRequest =
					new MockHttpServletRequest();

				ServiceContext serviceContext =
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), TestPropsValues.getUserId());

				Map<Locale, String> nameMap = Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString());

				Layout layout = _layoutLocalService.addLayout(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, 0, 0,
					nameMap, nameMap, Collections.emptyMap(),
					Collections.emptyMap(), Collections.emptyMap(),
					LayoutConstants.TYPE_CONTENT,
					UnicodePropertiesBuilder.put(
						LayoutTypeSettingsConstants.KEY_PUBLISHED, "true"
					).buildString(),
					false, false, Collections.emptyMap(), 0, serviceContext);

				mockHttpServletRequest.setAttribute(
					WebKeys.THEME_DISPLAY, _getThemeDisplay(layout));

				LifecycleEvent lifecycleEvent = new LifecycleEvent(
					mockHttpServletRequest, new MockHttpServletResponse());

				lifecycleAction.processLifecycleEvent(lifecycleEvent);

				Assert.assertNotNull(
					mockHttpServletRequest.getAttribute(
						SegmentsWebKeys.SEGMENTS_EXPERIENCE_IDS));
			}
		}
	}

	@Test
	public void testProcessLifecycleEvent2() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"segmentationEnabled", true
					).build())) {

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							SegmentsCompanyConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"segmentationEnabled", true
							).build())) {

				_segmentsConfigurationProvider.
					clearSegmentsCompanyConfigurations();

				LifecycleAction lifecycleAction = _getLifecycleAction();

				MockHttpServletRequest mockHttpServletRequest =
					new MockHttpServletRequest();

				Map<Locale, String> nameMap = Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString());

				Layout layout = _layoutLocalService.addLayout(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, 0, 0,
					nameMap, nameMap, Collections.emptyMap(),
					Collections.emptyMap(), Collections.emptyMap(),
					LayoutConstants.TYPE_CONTENT,
					UnicodePropertiesBuilder.put(
						LayoutTypeSettingsConstants.KEY_PUBLISHED, "true"
					).buildString(),
					false, false, Collections.emptyMap(), 0,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), TestPropsValues.getUserId()));

				mockHttpServletRequest.setAttribute(
					WebKeys.THEME_DISPLAY, _getThemeDisplay(layout));

				mockHttpServletRequest.setParameter("p_l_mode", Constants.EDIT);

				SegmentsExperience segmentsExperience =
					_segmentsExperienceLocalService.addSegmentsExperience(
						null, TestPropsValues.getUserId(), _group.getGroupId(),
						SegmentsEntryConstants.ID_DEFAULT, layout.getPlid(),
						RandomTestUtil.randomLocaleStringMap(), true,
						new UnicodeProperties(true),
						ServiceContextTestUtil.getServiceContext(
							_group.getGroupId()));

				String portletNamespace = _portal.getPortletNamespace(
					ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET);

				mockHttpServletRequest.setParameter(
					portletNamespace + "segmentsExperienceId",
					String.valueOf(
						segmentsExperience.getSegmentsExperienceId()));

				LifecycleEvent lifecycleEvent = new LifecycleEvent(
					mockHttpServletRequest, new MockHttpServletResponse());

				lifecycleAction.processLifecycleEvent(lifecycleEvent);

				Assert.assertArrayEquals(
					new long[] {segmentsExperience.getSegmentsExperienceId()},
					(long[])mockHttpServletRequest.getAttribute(
						SegmentsWebKeys.SEGMENTS_EXPERIENCE_IDS));
			}
		}
	}

	@Test
	public void testProcessLifecycleEventWithCachedSegmentsEntryId()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"segmentationEnabled", true
					).build())) {

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							SegmentsCompanyConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"segmentationEnabled", true
							).build())) {

				_segmentsConfigurationProvider.
					clearSegmentsCompanyConfigurations();

				LifecycleAction lifecycleAction = _getLifecycleAction();

				MockHttpServletRequest mockHttpServletRequest =
					new MockHttpServletRequest();

				ServiceContext serviceContext =
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), TestPropsValues.getUserId());

				Map<Locale, String> nameMap = Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString());

				Layout layout = _layoutLocalService.addLayout(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, 0, 0,
					nameMap, nameMap, Collections.emptyMap(),
					Collections.emptyMap(), Collections.emptyMap(),
					LayoutConstants.TYPE_CONTENT,
					UnicodePropertiesBuilder.put(
						LayoutTypeSettingsConstants.KEY_PUBLISHED, "true"
					).buildString(),
					false, false, Collections.emptyMap(), 0, serviceContext);

				mockHttpServletRequest.setAttribute(
					WebKeys.THEME_DISPLAY, _getThemeDisplay(layout));

				LifecycleEvent lifecycleEvent = new LifecycleEvent(
					mockHttpServletRequest, new MockHttpServletResponse());

				mockHttpServletRequest.setAttribute(
					SegmentsWebKeys.SEGMENTS_ENTRY_IDS,
					new long[] {1234567890L});

				lifecycleAction.processLifecycleEvent(lifecycleEvent);

				Assert.assertTrue(
					ArrayUtil.contains(
						(long[])mockHttpServletRequest.getAttribute(
							SegmentsWebKeys.SEGMENTS_ENTRY_IDS),
						1234567890L));
			}
		}
	}

	@Test
	public void testProcessLifecycleEventWithoutContentLayout()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"segmentationEnabled", true
					).build())) {

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							SegmentsCompanyConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"segmentationEnabled", true
							).build())) {

				_segmentsConfigurationProvider.
					clearSegmentsCompanyConfigurations();

				LifecycleAction lifecycleAction = _getLifecycleAction();

				MockHttpServletRequest mockHttpServletRequest =
					new MockHttpServletRequest();

				Layout layout = LayoutTestUtil.addTypePortletLayout(
					_group.getGroupId());

				mockHttpServletRequest.setAttribute(
					WebKeys.THEME_DISPLAY, _getThemeDisplay(layout));

				LifecycleEvent lifecycleEvent = new LifecycleEvent(
					mockHttpServletRequest, new MockHttpServletResponse());

				lifecycleAction.processLifecycleEvent(lifecycleEvent);

				Assert.assertNull(
					mockHttpServletRequest.getAttribute(
						SegmentsWebKeys.SEGMENTS_EXPERIENCE_IDS));
			}
		}
	}

	@Test
	public void testProcessLifecycleEventWithoutLayout() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"segmentationEnabled", true
					).build())) {

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							SegmentsCompanyConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"segmentationEnabled", true
							).build())) {

				_segmentsConfigurationProvider.
					clearSegmentsCompanyConfigurations();

				LifecycleAction lifecycleAction = _getLifecycleAction();

				MockHttpServletRequest mockHttpServletRequest =
					new MockHttpServletRequest();

				mockHttpServletRequest.setAttribute(
					WebKeys.THEME_DISPLAY, _getThemeDisplay(null));

				LifecycleEvent lifecycleEvent = new LifecycleEvent(
					mockHttpServletRequest, new MockHttpServletResponse());

				lifecycleAction.processLifecycleEvent(lifecycleEvent);

				Assert.assertNull(
					mockHttpServletRequest.getAttribute(
						SegmentsWebKeys.SEGMENTS_EXPERIENCE_IDS));
			}
		}
	}

	@Test
	public void testProcessLifecycleUsesCorrectSegmentsExperienceWithCachedSegmentsEntryId()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"segmentationEnabled", true
					).build())) {

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							SegmentsCompanyConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"segmentationEnabled", true
							).build())) {

				_segmentsConfigurationProvider.
					clearSegmentsCompanyConfigurations();

				LifecycleAction lifecycleAction = _getLifecycleAction();

				MockHttpServletRequest mockHttpServletRequest =
					new MockHttpServletRequest();

				ServiceContext serviceContext =
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), TestPropsValues.getUserId());

				Map<Locale, String> nameMap = Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString());

				Layout layout = _layoutLocalService.addLayout(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, 0, 0,
					nameMap, nameMap, Collections.emptyMap(),
					Collections.emptyMap(), Collections.emptyMap(),
					LayoutConstants.TYPE_CONTENT,
					UnicodePropertiesBuilder.put(
						LayoutTypeSettingsConstants.KEY_PUBLISHED, "true"
					).buildString(),
					false, false, Collections.emptyMap(), 0, serviceContext);

				mockHttpServletRequest.setAttribute(
					WebKeys.THEME_DISPLAY, _getThemeDisplay(layout));

				LifecycleEvent lifecycleEvent = new LifecycleEvent(
					mockHttpServletRequest, new MockHttpServletResponse());

				mockHttpServletRequest.setAttribute(
					SegmentsWebKeys.SEGMENTS_ENTRY_IDS, new long[] {0L});

				lifecycleAction.processLifecycleEvent(lifecycleEvent);

				List<SegmentsExperience> segmentsExperiences =
					_segmentsExperienceLocalService.getSegmentsExperiences(
						_group.getGroupId(), layout.getPlid());

				long[] expectedSegmentsExperienceIds =
					new long[segmentsExperiences.size()];

				for (int i = 0; i < segmentsExperiences.size(); i++) {
					SegmentsExperience segmentsExperience =
						segmentsExperiences.get(i);

					expectedSegmentsExperienceIds[i] =
						segmentsExperience.getSegmentsExperienceId();
				}

				Assert.assertArrayEquals(
					expectedSegmentsExperienceIds,
					(long[])mockHttpServletRequest.getAttribute(
						SegmentsWebKeys.SEGMENTS_EXPERIENCE_IDS));
			}
		}
	}

	private LifecycleAction _getLifecycleAction() {
		Bundle bundle = FrameworkUtil.getBundle(
			SegmentsServicePreActionTest.class);

		ServiceTrackerList<LifecycleAction> lifecycleActions =
			ServiceTrackerListFactory.open(
				bundle.getBundleContext(), LifecycleAction.class,
				"(key=servlet.service.events.pre)");

		for (LifecycleAction lifecycleAction : lifecycleActions) {
			Class<?> clazz = lifecycleAction.getClass();

			if (Objects.equals(
					clazz.getName(),
					"com.liferay.segments.internal.events." +
						"SegmentsServicePreAction")) {

				return lifecycleAction;
			}
		}

		throw new AssertionError("SegmentsServicePreAction is not registered");
	}

	private ThemeDisplay _getThemeDisplay(Layout layout) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLayout(layout);
		themeDisplay.setLifecycleRender(true);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsConfigurationProvider _segmentsConfigurationProvider;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}