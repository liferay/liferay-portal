/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.consent.management.platform.integration.internal.configuration.persistence.listener;

import com.liferay.consent.management.platform.integration.configuration.ConsentManagementPlatformConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Dictionary;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jorge García Jiménez
 */
public class ConsentManagementPlatformConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetInvalidElementName() {
		Assert.assertEquals(
			"base",
			_getInvalidElementName(
				"<!-- x --!><base href=\"https://liferay.com\">"));
		Assert.assertEquals(
			"base",
			_getInvalidElementName("<base href=\"https://liferay.com\">"));
		Assert.assertEquals(
			"base",
			_getInvalidElementName(
				"<script>x();</script/><base href=\"https://liferay.com\">"));
		Assert.assertEquals(
			"base",
			_getInvalidElementName(
				"<script>x();</script><base href=\"https://liferay.com\">"));
		Assert.assertEquals(
			"iframe",
			_getInvalidElementName(
				"<iframe src=\"https://liferay.com\"></iframe>"));
		Assert.assertEquals(
			"img",
			_getInvalidElementName("<img onerror=\"alert(1)\" src=\"x\">"));
		Assert.assertEquals(
			"meta",
			_getInvalidElementName(
				"<meta content=\"0;url=https://liferay.com\" " +
					"http-equiv=\"refresh\">"));
		Assert.assertEquals(
			"meta",
			_getInvalidElementName(
				"<script>x();</script/ ><meta " +
					"content=\"0;url=https://liferay.com\" " +
						"http-equiv=\"refresh\">"));
		Assert.assertEquals(
			"my-widget", _getInvalidElementName("<my-widget></my-widget>"));
		Assert.assertEquals(
			"style",
			_getInvalidElementName("<style>body { display: none; }</style>"));
		Assert.assertEquals(
			"svg", _getInvalidElementName("<svg onload=\"alert(1)\"></svg>"));
		Assert.assertNull(
			_getInvalidElementName(
				"<!-- <base href=\"https://liferay.com\"> -->"));
		Assert.assertNull(
			_getInvalidElementName("<Link as=\"script\" rel=\"preload\">"));
		Assert.assertNull(
			_getInvalidElementName(
				"<SCRIPT SRC=\"https://liferay.com/x.js\"></SCRIPT>"));
		Assert.assertNull(
			_getInvalidElementName("<script>if (a<b) { c(); }</script>"));
		Assert.assertNull(
			_getInvalidElementName("<script>x = \"<iframe>\";</script>"));
		Assert.assertNull(
			_getInvalidElementName(
				"<script>x();<base href=\"https://liferay.com\">"));
		Assert.assertNull(_getInvalidElementName(_SCRIPT_TAG_COOKIEBOT));
		Assert.assertNull(_getInvalidElementName(_SCRIPT_TAG_ONETRUST));
	}

	@Test
	public void testOnBeforeSaveInvalidScript() {
		try (MockedStatic<ResourceBundleUtil> resourceBundleUtilMockedStatic =
				Mockito.mockStatic(ResourceBundleUtil.class)) {

			_assertInvalidScript("<iframe></iframe>", _SCRIPT_TAG_COOKIEBOT);
			_assertInvalidScript(
				StringPool.BLANK, "<base href=\"https://liferay.com\">");
		}
	}

	@Test
	public void testOnBeforeSaveValidScript() throws Exception {
		_assertValidScript(StringPool.BLANK, _SCRIPT_TAG_COOKIEBOT);
	}

	private void _assertInvalidScript(
		String consentMappingScript, String scriptTag) {

		ConfigurationModelListenerException
			configurationModelListenerException = Assert.assertThrows(
				ConfigurationModelListenerException.class,
				() -> _configurationModelListener.onBeforeSave(
					ConsentManagementPlatformConfiguration.class.getName(),
					_createProperties(consentMappingScript, scriptTag)));

		Assert.assertEquals(
			ConsentManagementPlatformConfiguration.class,
			configurationModelListenerException.configurationClass);
	}

	private void _assertValidScript(
			String consentMappingScript, String scriptTag)
		throws Exception {

		Dictionary<String, Object> properties = _createProperties(
			consentMappingScript, scriptTag);

		_configurationModelListener.onBeforeSave(
			ConsentManagementPlatformConfiguration.class.getName(), properties);

		Assert.assertEquals(scriptTag, properties.get("scriptTag"));
	}

	private Dictionary<String, Object> _createProperties(
		String consentMappingScript, String scriptTag) {

		return HashMapDictionaryBuilder.<String, Object>put(
			"consentMappingScript", consentMappingScript
		).put(
			"scriptTag", scriptTag
		).build();
	}

	private String _getInvalidElementName(String html) {
		return ReflectionTestUtil.invoke(
			_configurationModelListener, "_getInvalidElementName",
			new Class<?>[] {String.class}, html);
	}

	private static final String _SCRIPT_TAG_COOKIEBOT = StringBundler.concat(
		"<link as=\"script\" href=\"https://consent.cookiebot.com/uc.js\" ",
		"rel=\"preload\"><script data-cbid=\"000000\" id=\"Cookiebot\" ",
		"src=\"https://consent.cookiebot.com/uc.js\" ",
		"type=\"text/javascript\"></script>");

	private static final String _SCRIPT_TAG_ONETRUST = StringBundler.concat(
		"<script src=\"https://cdn.cookielaw.org/scripttemplates",
		"/otSDKStub.js\" type=\"text/javascript\"></script>",
		"<script type=\"text/javascript\">function OptanonWrapper() {}",
		"</script>");

	private final ConfigurationModelListener _configurationModelListener =
		new ConsentManagementPlatformConfigurationModelListener();

}