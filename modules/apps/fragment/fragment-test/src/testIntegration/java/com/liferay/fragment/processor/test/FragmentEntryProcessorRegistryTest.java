/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.processor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.DefaultEditableValuesFragmentEntryProcessor;
import com.liferay.fragment.processor.DefaultFragmentEntryProcessorContext;
import com.liferay.fragment.processor.DocumentFragmentEntryProcessor;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class FragmentEntryProcessorRegistryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetAvailableTagsJSONArray() {
		JSONArray availableTagsJSONArray =
			_fragmentEntryProcessorRegistry.getAvailableTagsJSONArray();

		for (int i = 0; i < availableTagsJSONArray.length(); i++) {
			JSONObject tagsJSONArrayJSONObject =
				availableTagsJSONArray.getJSONObject(i);

			String name = tagsJSONArrayJSONObject.getString("name");

			Assert.assertFalse(name.startsWith("lfr-editable:"));
		}
	}

	@Test
	public void testProcessFragmentEntryLinkHTML() throws Exception {
		_testProcessFragmentEntryLinkHTMLWithMarkers();
		_testProcessFragmentEntryLinkHTMLWithoutMarkers();
		_testProcessFragmentEntryLinkHTMLWithThirdPartyDefaultEditableValuesFragmentEntryProcessor();
		_testProcessFragmentEntryLinkHTMLWithThirdPartyDocumentFragmentEntryProcessor();
	}

	private Object _getDocument(String html) {
		PortalCache<String, Object> portalCache =
			PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.SINGLE_VM,
				"com.liferay.fragment.internal.processor." +
					"FragmentEntryProcessorRegistryImpl._documentPortalCache");

		return portalCache.get(html);
	}

	private HttpServletRequest _getHttpServletRequest() throws Exception {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());

		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		return httpServletRequest;
	}

	private String _processFragmentEntryLinkHTML(
			JSONObject editableValuesJSONObject, String html)
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		fragmentEntryLink.setHtml(html);

		return _fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
			editableValuesJSONObject, fragmentEntryLink,
			new DefaultFragmentEntryProcessorContext(
				TestPropsValues.getCompanyId(), _getHttpServletRequest(),
				new MockHttpServletResponse(), LocaleUtil.getDefault(),
				FragmentEntryLinkConstants.VIEW, _group.getGroupId()));
	}

	private void _testProcessFragmentEntryLinkHTMLWithMarkers()
		throws Exception {

		String name = RandomTestUtil.randomString();

		String html = StringBundler.concat(
			"<lfr-editable id=\"text\" type=\"text\">", name,
			"</lfr-editable><div><span>", name, "</div>");

		Assert.assertEquals(
			StringBundler.concat(
				" <div>", name, "</div><div><span>", name, "</span></div>"),
			_processFragmentEntryLinkHTML(
				JSONFactoryUtil.createJSONObject(), html));

		Assert.assertNotNull(_getDocument(StringPool.SPACE + html));
	}

	private void _testProcessFragmentEntryLinkHTMLWithThirdPartyDefaultEditableValuesFragmentEntryProcessor()
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			FragmentEntryProcessorRegistryTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<DefaultEditableValuesFragmentEntryProcessor>
			serviceRegistration = bundleContext.registerService(
				DefaultEditableValuesFragmentEntryProcessor.class,
				ProxyUtil.newDelegateProxyInstance(
					DefaultEditableValuesFragmentEntryProcessor.class.
						getClassLoader(),
					DefaultEditableValuesFragmentEntryProcessor.class,
					new Object(), _defaultEditableValuesFragmentEntryProcessor),
				HashMapDictionaryBuilder.<String, Object>put(
					"fragment.entry.processor.key",
					FragmentEntryProcessorConstants.
						KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR
				).put(
					"service.ranking", Integer.MAX_VALUE
				).build());

		try {
			JSONObject editableValuesJSONObject =
				JSONFactoryUtil.createJSONObject();

			String name = RandomTestUtil.randomString();

			Assert.assertEquals(
				" <div><span>" + name + "</span></div>",
				_processFragmentEntryLinkHTML(
					editableValuesJSONObject, "<div><span>" + name + "</div>"));

			Assert.assertNotNull(
				_getDocument(" <div><span>" + name + "</div>"));

			Assert.assertEquals(
				String.valueOf(
					JSONUtil.put(
						FragmentEntryProcessorConstants.
							KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
						JSONFactoryUtil.createJSONObject())),
				editableValuesJSONObject.toString());
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private void _testProcessFragmentEntryLinkHTMLWithThirdPartyDocumentFragmentEntryProcessor()
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			FragmentEntryProcessorRegistryTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		AtomicInteger atomicInteger = new AtomicInteger();

		ServiceRegistration<DocumentFragmentEntryProcessor>
			serviceRegistration = bundleContext.registerService(
				DocumentFragmentEntryProcessor.class,
				(document, fragmentEntryLink, fragmentEntryProcessorContext) ->
					atomicInteger.incrementAndGet(),
				null);

		try {
			String name = RandomTestUtil.randomString();

			Assert.assertEquals(
				" <div><span>" + name + "</span></div>",
				_processFragmentEntryLinkHTML(
					JSONFactoryUtil.createJSONObject(),
					"<div><span>" + name + "</div>"));

			Assert.assertEquals(1, atomicInteger.get());
			Assert.assertNotNull(
				_getDocument(" <div><span>" + name + "</div>"));
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private void _testProcessFragmentEntryLinkHTMLWithoutMarkers()
		throws Exception {

		String name = RandomTestUtil.randomString();

		String html = " <div><span>" + StringUtil.toUpperCase(name) + "</div>";

		JSONObject editableValuesJSONObject =
			JSONFactoryUtil.createJSONObject();

		Assert.assertEquals(
			html,
			_processFragmentEntryLinkHTML(
				editableValuesJSONObject,
				"<div><span>${\"" + name + "\"?upper_case}</div>"));

		Assert.assertNull(_getDocument(html));

		Assert.assertEquals(
			String.valueOf(
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
					JSONFactoryUtil.createJSONObject())),
			editableValuesJSONObject.toString());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "fragment.entry.processor.key=" + FragmentEntryProcessorConstants.KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR
	)
	private DefaultEditableValuesFragmentEntryProcessor
		_defaultEditableValuesFragmentEntryProcessor;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@DeleteAfterTestRun
	private Group _group;

}