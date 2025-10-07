/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.content.processor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.configuration.ExportImportServiceConfiguration;
import com.liferay.exportimport.configuration.ExportImportServiceConfigurationWhitelistedURLPatternsHelper;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.kernel.exception.ExportImportContentValidationException;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.test.util.TestReaderWriter;
import com.liferay.exportimport.test.util.TestUserIdStrategy;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutFriendlyURLRandomizerBumper;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.NavigableMap;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Michael Bowerman
 */
@RunWith(Arquillian.class)
public class LayoutReferencesExportImportContentProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testContentReferenceWithoutAssetRendererFactoryForClassName()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypeContentLayout(liveGroup);

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		FragmentRenderer fragmentRenderer =
			_fragmentRendererRegistry.getFragmentRenderer(
				"com.liferay.fragment.internal.renderer." +
					"ContentObjectFragmentRenderer");

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(null);

		FragmentEntryLink draftLayoutFragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), draftLayout.getGroupId(), 0,
				0, segmentsExperienceId, draftLayout.getPlid(),
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				JSONFactoryUtil.toString(
					fragmentRenderer.getConfigurationJSONObject(
						defaultFragmentRendererContext)),
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"itemSelector",
						JSONUtil.put(
							"className", RandomTestUtil.randomString()))
				).toString(),
				StringPool.BLANK, 0, fragmentRenderer.getKey(),
				fragmentRenderer.getType(),
				ServiceContextTestUtil.getServiceContext(
					liveGroup.getGroupId(), TestPropsValues.getUserId()));

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			draftLayoutFragmentEntryLink, draftLayout, null, 0,
			segmentsExperienceId);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		Assert.assertNotNull(
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				liveGroup.getGroupId(),
				draftLayoutFragmentEntryLink.getFragmentEntryLinkId(),
				layout.getPlid()));

		GroupTestUtil.enableLocalStaging(
			liveGroup, TestPropsValues.getUserId());

		Group stagingGroup = liveGroup.getStagingGroup();

		Assert.assertNotNull(
			_layoutLocalService.getLayoutByUuidAndGroupId(
				layout.getUuid(), stagingGroup.getGroupId(),
				layout.isPrivateLayout()));
	}

	@Test
	public void testExportDefaultGroupCompanyHostImportDefaultGroupCompanyHost()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(exportGroup, false);

		Group importGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(importGroup, false);

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(exportGroup);

		Assert.assertEquals(
			_getCompanyHostPortalURL(importGroup) +
				exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				_getCompanyHostPortalURL(exportGroup) +
					exportLayout.getFriendlyURL(),
				exportGroup, importGroup, true, true));
	}

	@Test
	public void testExportDefaultGroupCompanyHostImportNoVirtualHost()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(exportGroup);

		Group importGroup = GroupTestUtil.addGroup();

		Assert.assertEquals(
			_getCompanyHostPortalURL(importGroup) +
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING +
					importGroup.getFriendlyURL() +
						exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				_getCompanyHostPortalURL(exportGroup) +
					exportLayout.getFriendlyURL(),
				exportGroup, importGroup, true, false));
	}

	@Test
	public void testExportDefaultGroupCompanyHostImportPublicPagesVirtualHost()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(exportGroup);

		Group importGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(importGroup, false);

		Assert.assertEquals(
			_getGroupVirtualHostPortalURL(importGroup, false) +
				exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				_getCompanyHostPortalURL(exportGroup) +
					exportLayout.getFriendlyURL(),
				exportGroup, importGroup, true, false));
	}

	@Test
	public void testExportDefaultGroupPublicPagesVirtualHostImportDefaultGroupNoVirtualHost()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(exportGroup, false);

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(exportGroup);

		Group importGroup = GroupTestUtil.addGroup();

		Assert.assertEquals(
			_getCompanyHostPortalURL(importGroup) +
				exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				_getGroupVirtualHostPortalURL(exportGroup, false) +
					exportLayout.getFriendlyURL(),
				exportGroup, importGroup, true, true));
	}

	@Test
	public void testExportDefaultGroupPublicPagesVirtualHostImportDefaultGroupPublicPagesVirtualHost()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(exportGroup, false);

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(exportGroup);

		Group importGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(importGroup, false);

		Assert.assertEquals(
			_getGroupVirtualHostPortalURL(importGroup, false) +
				exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				_getGroupVirtualHostPortalURL(exportGroup, false) +
					exportLayout.getFriendlyURL(),
				exportGroup, importGroup, true, true));
	}

	@Test
	public void testExportDefaultGroupPublicPagesVirtualHostImportNoVirtualHost()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(exportGroup, false);

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(exportGroup);

		Group importGroup = GroupTestUtil.addGroup();

		Assert.assertEquals(
			_getCompanyHostPortalURL(importGroup) +
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING +
					importGroup.getFriendlyURL() +
						exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				_getGroupVirtualHostPortalURL(exportGroup, false) +
					exportLayout.getFriendlyURL(),
				exportGroup, importGroup, true, false));
	}

	@Test
	public void testExportDefaultGroupPublicPagesVirtualHostImportPublicPagesVirtualHost()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(exportGroup, false);

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(exportGroup);

		Group importGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(importGroup, false);

		Assert.assertEquals(
			_getGroupVirtualHostPortalURL(importGroup, false) +
				exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				_getGroupVirtualHostPortalURL(exportGroup, false) +
					exportLayout.getFriendlyURL(),
				exportGroup, importGroup, true, false));
	}

	@Test
	public void testExportDefaultGroupRelativeURLImportDefaultGroup()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(exportGroup);

		Group importGroup = GroupTestUtil.addGroup();

		Assert.assertEquals(
			exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				exportLayout.getFriendlyURL(), exportGroup, importGroup, true,
				true));
	}

	@Test
	public void testExportDefaultGroupRelativeURLImportNoVirtualHost()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(exportGroup);

		Group importGroup = GroupTestUtil.addGroup();

		Assert.assertEquals(
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING +
				importGroup.getFriendlyURL() + exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				exportLayout.getFriendlyURL(), exportGroup, importGroup, true,
				false));
	}

	@Test
	public void testExportDefaultGroupRelativeURLImportPublicPagesVirtualHost()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(exportGroup);

		Group importGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(importGroup, false);

		Assert.assertEquals(
			exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				exportLayout.getFriendlyURL(), exportGroup, importGroup, true,
				false));
	}

	@Test
	public void testExportDefaultGroupRelativeURLWithLocaleImportDefaultGroup()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(exportGroup);

		Group importGroup = GroupTestUtil.addGroup();

		Assert.assertEquals(
			StringPool.SLASH + LocaleUtil.getDefault() +
				exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				StringPool.SLASH + LocaleUtil.getDefault() +
					exportLayout.getFriendlyURL(),
				exportGroup, importGroup, true, true));
	}

	@Test
	public void testExportPrivatePagesVirtualHostURLImportDefaultGroup()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(exportGroup, true);

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(
			exportGroup, true);

		Group importGroup = GroupTestUtil.addGroup();

		Assert.assertEquals(
			_getCompanyHostPortalURL(importGroup) +
				PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING +
					importGroup.getFriendlyURL() +
						exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				_getGroupVirtualHostPortalURL(exportGroup, true) +
					exportLayout.getFriendlyURL(),
				exportGroup, importGroup, false, true));
	}

	@Test
	public void testExportPublicPagesVirtualHostImportDefaultGroupNoVirtualHost()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(exportGroup, false);

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(exportGroup);

		Group importGroup = GroupTestUtil.addGroup();

		Assert.assertEquals(
			_getCompanyHostPortalURL(importGroup) +
				exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				_getGroupVirtualHostPortalURL(exportGroup, false) +
					exportLayout.getFriendlyURL(),
				exportGroup, importGroup, false, true));
	}

	@Test
	public void testExportPublicPagesVirtualHostImportDefaultGroupPublicPagesVirtualHost()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(exportGroup, false);

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(exportGroup);

		Group importGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(importGroup, false);

		Assert.assertEquals(
			_getGroupVirtualHostPortalURL(importGroup, false) +
				exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				_getGroupVirtualHostPortalURL(exportGroup, false) +
					exportLayout.getFriendlyURL(),
				exportGroup, importGroup, false, true));
	}

	@Test
	public void testExportRelativeURLImportDefaultGroupNoVirtualHost()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(exportGroup, false);

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(exportGroup);

		Assert.assertEquals(
			exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				exportLayout.getFriendlyURL(), exportGroup,
				GroupTestUtil.addGroup(), false, true));
	}

	@Test
	public void testExternalURLNotModified() throws Exception {
		String exportedURL = RandomTestUtil.randomString(
			LayoutFriendlyURLRandomizerBumper.INSTANCE);

		Assert.assertEquals(
			exportedURL,
			_exportAndImportLayoutURL(
				exportedURL, GroupTestUtil.addGroup(),
				GroupTestUtil.addGroup()));
	}

	@Test
	public void testPrivateVirtualHostLayoutFriendlyURLFormatModifiedWhenNecessary()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(exportGroup, true);

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(
			exportGroup, true);

		Group importGroup = GroupTestUtil.addGroup();

		Assert.assertEquals(
			_getCompanyHostPortalURL(importGroup) +
				PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING +
					importGroup.getFriendlyURL() +
						exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				_getGroupVirtualHostPortalURL(exportGroup, true) +
					exportLayout.getFriendlyURL(),
				exportGroup, importGroup));
	}

	@Test
	public void testPrivateVirtualHostLayoutFriendlyURLFormatPreservedWhenPossible()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(exportGroup, true);

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(
			exportGroup, true);

		Group importGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(importGroup, true);

		Assert.assertEquals(
			_getGroupVirtualHostPortalURL(importGroup, true) +
				exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				_getGroupVirtualHostPortalURL(exportGroup, true) +
					exportLayout.getFriendlyURL(),
				exportGroup, importGroup));
	}

	@Test
	public void testPublicVirtualHostLayoutFriendlyURLFormatModifiedWhenNecessary()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(exportGroup, false);

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(exportGroup);

		Group importGroup = GroupTestUtil.addGroup();

		Assert.assertEquals(
			_getCompanyHostPortalURL(importGroup) +
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING +
					importGroup.getFriendlyURL() +
						exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				_getGroupVirtualHostPortalURL(exportGroup, false) +
					exportLayout.getFriendlyURL(),
				exportGroup, importGroup));
	}

	@Test
	public void testPublicVirtualHostLayoutFriendlyURLFormatPreservedWhenPossible()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(exportGroup, false);

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(exportGroup);

		Group importGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(importGroup, false);

		Assert.assertEquals(
			_getGroupVirtualHostPortalURL(importGroup, false) +
				exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				_getGroupVirtualHostPortalURL(exportGroup, false) +
					exportLayout.getFriendlyURL(),
				exportGroup, importGroup));
	}

	@Test
	public void testRelativeLayoutFriendlyURLFormatModifiedWhenNecessary()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(exportGroup, false);

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(exportGroup);

		Group importGroup = GroupTestUtil.addGroup();

		Assert.assertEquals(
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING +
				importGroup.getFriendlyURL() + exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				exportLayout.getFriendlyURL(), exportGroup, importGroup));
	}

	@Test
	public void testRelativeLayoutFriendlyURLFormatPreservedWhenPossible()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(exportGroup, false);

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(exportGroup);

		Group importGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(importGroup, false);

		Assert.assertEquals(
			exportLayout.getFriendlyURL(),
			_exportAndImportLayoutURL(
				exportLayout.getFriendlyURL(), exportGroup, importGroup));
	}

	@Test
	public void testRelativePublicDefaultPageURLWithLocale() throws Exception {
		Group exportGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(exportGroup, false);

		Group importGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(importGroup, false);

		Locale siteDefaultLocale = _portal.getSiteDefaultLocale(exportGroup);

		String url = StringPool.SLASH + siteDefaultLocale.getLanguage();

		Assert.assertEquals(
			url, _exportAndImportLayoutURL(url, exportGroup, importGroup));
	}

	@Test
	public void testRelativePublicPageURLWithSlashWithLocale()
		throws Exception {

		Group exportGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(exportGroup, false);

		Group importGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(importGroup, false);

		Locale siteDefaultLocale = _portal.getSiteDefaultLocale(exportGroup);

		String layoutName = RandomTestUtil.randomString(
			LayoutFriendlyURLRandomizerBumper.INSTANCE,
			NumericStringRandomizerBumper.INSTANCE,
			UniqueStringRandomizerBumper.INSTANCE);

		layoutName = layoutName + StringPool.SLASH + layoutName;

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(
			exportGroup.getGroupId(), layoutName, false, null, false);

		String url = StringBundler.concat(
			StringPool.SLASH, siteDefaultLocale.getLanguage(),
			exportLayout.getFriendlyURL());

		Assert.assertEquals(
			url, _exportAndImportLayoutURL(url, exportGroup, importGroup));
	}

	@Test
	public void testValidateContentRelativePublicDefaultPageURLWithLocale()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(group, false);

		Locale siteDefaultLocale = _portal.getSiteDefaultLocale(group);

		_layoutReferencesExportImportContentProcessor.validateContentReferences(
			group.getGroupId(),
			StringBundler.concat(
				_CONTENT_PREFIX, StringPool.SLASH,
				siteDefaultLocale.getLanguage(), _CONTENT_POSTFIX));
	}

	@Test
	public void testValidateContentRelativePublicPageURLWithSlashWithLocale()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHost(group, false);

		Locale siteDefaultLocale = _portal.getSiteDefaultLocale(group);

		String layoutName = RandomTestUtil.randomString(
			LayoutFriendlyURLRandomizerBumper.INSTANCE,
			NumericStringRandomizerBumper.INSTANCE,
			UniqueStringRandomizerBumper.INSTANCE);

		layoutName = layoutName + StringPool.SLASH + layoutName;

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			group.getGroupId(), layoutName, false, null, false);

		String url = StringBundler.concat(
			StringPool.SLASH, siteDefaultLocale.getLanguage(),
			layout.getFriendlyURL());

		_layoutReferencesExportImportContentProcessor.validateContentReferences(
			group.getGroupId(),
			StringBundler.concat(_CONTENT_PREFIX, url, _CONTENT_POSTFIX));
	}

	@Test(expected = ExportImportContentValidationException.class)
	public void testValidateURLWithWhitelistedExactMatchPatternFailCase()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		String urlSegment1 = RandomTestUtil.randomString(
			LayoutFriendlyURLRandomizerBumper.INSTANCE);

		String exactMatchPattern = StringBundler.concat(
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
			group.getFriendlyURL(), StringPool.SLASH, urlSegment1);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						ExportImportServiceConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"validateLayoutReferencesWhitelistedURLPatterns",
							new String[] {exactMatchPattern}
						).build())) {

			_exportImportServiceConfigurationWhitelistedURLPatternsHelper.
				rebuildURLPatternMapper(TestPropsValues.getCompanyId());

			_layoutReferencesExportImportContentProcessor.
				validateContentReferences(
					group.getGroupId(),
					StringBundler.concat(
						_CONTENT_PREFIX, exactMatchPattern, StringPool.SLASH,
						RandomTestUtil.randomString(
							LayoutFriendlyURLRandomizerBumper.INSTANCE),
						_CONTENT_POSTFIX));
		}
		finally {
			_exportImportServiceConfigurationWhitelistedURLPatternsHelper.
				rebuildURLPatternMapper(TestPropsValues.getCompanyId());
		}
	}

	@Test
	public void testValidateURLWithWhitelistedExactMatchPatternPassCase()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		String urlSegment = RandomTestUtil.randomString(
			LayoutFriendlyURLRandomizerBumper.INSTANCE);

		String exactMatchPattern = StringBundler.concat(
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
			group.getFriendlyURL(), StringPool.SLASH, urlSegment);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						ExportImportServiceConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"validateLayoutReferencesWhitelistedURLPatterns",
							new String[] {exactMatchPattern}
						).build())) {

			_exportImportServiceConfigurationWhitelistedURLPatternsHelper.
				rebuildURLPatternMapper(TestPropsValues.getCompanyId());

			_layoutReferencesExportImportContentProcessor.
				validateContentReferences(
					group.getGroupId(),
					StringBundler.concat(
						_CONTENT_PREFIX, exactMatchPattern, _CONTENT_POSTFIX));
		}
		finally {
			_exportImportServiceConfigurationWhitelistedURLPatternsHelper.
				rebuildURLPatternMapper(TestPropsValues.getCompanyId());
		}
	}

	@Test(expected = ExportImportContentValidationException.class)
	public void testValidateURLWithWhitelistedStarPatternFailCase()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						ExportImportServiceConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"validateLayoutReferencesWhitelistedURLPatterns",
							new String[] {
								StringBundler.concat(
									PropsValues.
										LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
									group.getFriendlyURL(), StringPool.SLASH,
									RandomTestUtil.randomString(
										LayoutFriendlyURLRandomizerBumper.
											INSTANCE),
									StringPool.SLASH, StringPool.STAR)
							}
						).build())) {

			_exportImportServiceConfigurationWhitelistedURLPatternsHelper.
				rebuildURLPatternMapper(TestPropsValues.getCompanyId());

			_layoutReferencesExportImportContentProcessor.
				validateContentReferences(
					group.getGroupId(),
					StringBundler.concat(
						_CONTENT_PREFIX,
						PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
						group.getFriendlyURL(), StringPool.SLASH,
						RandomTestUtil.randomString(
							LayoutFriendlyURLRandomizerBumper.INSTANCE),
						StringPool.SLASH, RandomTestUtil.randomString(),
						_CONTENT_POSTFIX));
		}
		finally {
			_exportImportServiceConfigurationWhitelistedURLPatternsHelper.
				rebuildURLPatternMapper(TestPropsValues.getCompanyId());
		}
	}

	@Test
	public void testValidateURLWithWhitelistedStarPatternPassCase()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		String urlSegment = RandomTestUtil.randomString(
			LayoutFriendlyURLRandomizerBumper.INSTANCE);

		String prefixPattern = StringBundler.concat(
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
			group.getFriendlyURL(), StringPool.SLASH, urlSegment,
			StringPool.SLASH);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						ExportImportServiceConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"validateLayoutReferencesWhitelistedURLPatterns",
							new String[] {prefixPattern + StringPool.STAR}
						).build())) {

			_exportImportServiceConfigurationWhitelistedURLPatternsHelper.
				rebuildURLPatternMapper(TestPropsValues.getCompanyId());

			_layoutReferencesExportImportContentProcessor.
				validateContentReferences(
					group.getGroupId(),
					StringBundler.concat(
						_CONTENT_PREFIX, prefixPattern,
						RandomTestUtil.randomString(), _CONTENT_POSTFIX));
		}
		finally {
			_exportImportServiceConfigurationWhitelistedURLPatternsHelper.
				rebuildURLPatternMapper(TestPropsValues.getCompanyId());
		}
	}

	private String _exportAndImportLayoutURL(
			String url, Group exportGroup, Group importGroup)
		throws Exception {

		return _exportAndImportLayoutURL(
			url, exportGroup, importGroup, false, false);
	}

	private String _exportAndImportLayoutURL(
			String url, Group exportGroup, Group importGroup,
			boolean exportFromDefaultGroup, boolean importToDefaultGroup)
		throws Exception {

		TestReaderWriter testReaderWriter = new TestReaderWriter();

		Group defaultGroup = _groupLocalService.fetchGroup(
			exportGroup.getCompanyId(),
			PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME);

		if (defaultGroup != null) {
			defaultGroup = _groupLocalService.fetchGroup(
				defaultGroup.getGroupId());
		}

		String exportGroupKey = exportGroup.getGroupKey();

		String importGroupKey = importGroup.getGroupKey();

		try {
			if (exportFromDefaultGroup) {
				if (defaultGroup != null) {
					defaultGroup.setGroupKey(_GROUP_KEY);

					defaultGroup = _groupLocalService.updateGroup(defaultGroup);
				}

				exportGroup.setGroupKey(
					PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME);

				exportGroup = _groupLocalService.updateGroup(exportGroup);
			}

			PortletDataContext exportPortletDataContext =
				PortletDataContextFactoryUtil.createExportPortletDataContext(
					exportGroup.getCompanyId(), exportGroup.getGroupId(),
					new HashMap<>(),
					new Date(System.currentTimeMillis() - Time.HOUR),
					new Date(), testReaderWriter);

			Document document = SAXReaderUtil.createDocument();

			Element manifestRootElement = document.addElement("root");

			manifestRootElement.addElement("header");

			testReaderWriter.addEntry("/manifest.xml", document.asXML());

			Element rootElement = SAXReaderUtil.createElement("root");

			exportPortletDataContext.setExportDataRootElement(rootElement);

			Element missingReferencesElement = rootElement.addElement(
				"missing-references");

			exportPortletDataContext.setMissingReferencesElement(
				missingReferencesElement);

			StagedModel referrerStagedModel = JournalTestUtil.addArticle(
				exportGroup.getGroupId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString());

			String contentToExport = _CONTENT_PREFIX + url + _CONTENT_POSTFIX;

			String exportedContent =
				_layoutReferencesExportImportContentProcessor.
					replaceExportContentReferences(
						exportPortletDataContext, referrerStagedModel,
						contentToExport, true, false);

			if (exportFromDefaultGroup) {
				exportGroup.setGroupKey(exportGroupKey);

				exportGroup = _groupLocalService.updateGroup(exportGroup);

				if (defaultGroup != null) {
					defaultGroup.setGroupKey(
						PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME);

					defaultGroup = _groupLocalService.updateGroup(defaultGroup);
				}
			}

			if (importToDefaultGroup) {
				if (defaultGroup != null) {
					defaultGroup.setGroupKey(_GROUP_KEY);

					defaultGroup = _groupLocalService.updateGroup(defaultGroup);
				}

				importGroup.setGroupKey(
					PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME);

				importGroup = _groupLocalService.updateGroup(importGroup);
			}

			PortletDataContext importPortletDataContext =
				PortletDataContextFactoryUtil.createImportPortletDataContext(
					importGroup.getCompanyId(), importGroup.getGroupId(),
					new HashMap<>(), new TestUserIdStrategy(),
					testReaderWriter);

			importPortletDataContext.setImportDataRootElement(rootElement);
			importPortletDataContext.setMissingReferencesElement(
				missingReferencesElement);

			String importedContent =
				_layoutReferencesExportImportContentProcessor.
					replaceImportContentReferences(
						importPortletDataContext, referrerStagedModel,
						exportedContent);

			if (importToDefaultGroup) {
				importGroup.setGroupKey(importGroupKey);

				importGroup = _groupLocalService.updateGroup(importGroup);

				if (defaultGroup != null) {
					defaultGroup.setGroupKey(
						PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME);

					defaultGroup = _groupLocalService.updateGroup(defaultGroup);
				}
			}

			return importedContent.substring(
				_CONTENT_PREFIX.length(),
				importedContent.length() - _CONTENT_POSTFIX.length());
		}
		catch (Exception exception) {
			exportGroup.setGroupKey(exportGroupKey);

			_groupLocalService.updateGroup(exportGroup);

			importGroup.setGroupKey(importGroupKey);

			_groupLocalService.updateGroup(importGroup);

			if (defaultGroup != null) {
				defaultGroup.setGroupKey(
					PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME);

				_groupLocalService.updateGroup(defaultGroup);
			}

			throw exception;
		}
	}

	private String _getCompanyHostPortalURL(Group group) throws Exception {
		Company company = _companyLocalService.getCompany(group.getCompanyId());

		return _portal.getPortalURL(
			company.getVirtualHostname(), _portal.getPortalServerPort(false),
			false);
	}

	private String _getGroupVirtualHostPortalURL(
		Group group, boolean privateLayout) {

		LayoutSet layoutSet = null;

		if (privateLayout) {
			layoutSet = group.getPrivateLayoutSet();
		}
		else {
			layoutSet = group.getPublicLayoutSet();
		}

		NavigableMap<String, String> virtualHostnames =
			layoutSet.getVirtualHostnames();

		return _portal.getPortalURL(
			virtualHostnames.firstKey(), _portal.getPortalServerPort(false),
			false);
	}

	private static final String _CONTENT_POSTFIX = "\">link</a>";

	private static final String _CONTENT_PREFIX = "<a href=\"";

	private static final String _GROUP_KEY = RandomTestUtil.randomString(
		NumericStringRandomizerBumper.INSTANCE,
		UniqueStringRandomizerBumper.INSTANCE);

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ExportImportServiceConfigurationWhitelistedURLPatternsHelper
		_exportImportServiceConfigurationWhitelistedURLPatternsHelper;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentRendererRegistry _fragmentRendererRegistry;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(filter = "content.processor.type=LayoutReferences")
	private ExportImportContentProcessor<String>
		_layoutReferencesExportImportContentProcessor;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}