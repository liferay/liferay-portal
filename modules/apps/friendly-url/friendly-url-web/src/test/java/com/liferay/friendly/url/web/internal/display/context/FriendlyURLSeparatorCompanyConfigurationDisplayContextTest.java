/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.web.internal.display.context;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.friendly.url.configuration.manager.FriendlyURLSeparatorConfigurationManager;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutFriendlyURLComposite;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.impl.CompanyImpl;
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Mikel Lorza
 */
public class FriendlyURLSeparatorCompanyConfigurationDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_friendlyURLResolverRegistryUtilMockedStatic = Mockito.mockStatic(
			FriendlyURLResolverRegistryUtil.class);

		_friendlyURLResolverRegistryUtilMockedStatic.when(
			FriendlyURLResolverRegistryUtil::getFriendlyURLResolversAsCollection
		).thenReturn(
			ListUtil.fromArray(
				new FriendlyURLResolverImpl(
					FriendlyURLResolverConstants.URL_SEPARATOR_BLOGS_ENTRY,
					_SIMPLE_CLASS_NAME_BLOGS_ENTRY, "/blogs1/"),
				new FriendlyURLResolverImpl(
					FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE,
					_SIMPLE_CLASS_NAME_JOURNAL_ARTICLE, "/journal1/"))
		);

		_friendlyURLSeparatorConfigurationManager = Mockito.mock(
			FriendlyURLSeparatorConfigurationManager.class);

		_jsonFactory = Mockito.mock(JSONFactory.class);

		Mockito.when(
			_friendlyURLSeparatorConfigurationManager.
				getFriendlyURLSeparatorsJSONObject(Mockito.anyLong())
		).thenReturn(
			JSONUtil.put(
				_SIMPLE_CLASS_NAME_BLOGS_ENTRY, "blog-test1"
			).put(
				_SIMPLE_CLASS_NAME_JOURNAL_ARTICLE, "journal-test1"
			)
		);

		_portal = Mockito.mock(Portal.class);

		Mockito.when(
			_portal.getPortletNamespace(Mockito.anyString())
		).thenReturn(
			_INSTANCE_SETTINGS_PORTLET_NAMESPACE
		);
	}

	@After
	public void tearDown() {
		_friendlyURLResolverRegistryUtilMockedStatic.close();
	}

	@Test
	public void testGetConfigurableFriendlyURLSeparatorsJSONArray()
		throws Exception {

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_getThemeDisplay()
		);

		FriendlyURLSeparatorCompanyConfigurationDisplayContext
			friendlyURLSeparatorCompanyConfigurationDisplayContext =
				new FriendlyURLSeparatorCompanyConfigurationDisplayContext(
					_friendlyURLSeparatorConfigurationManager,
					httpServletRequest, _jsonFactory,
					Mockito.mock(Language.class), _portal);

		Assert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"defaultValue", "b"
				).put(
					"name", _NAME_BLOGS_ENTRY
				).put(
					"value", "blog-test1"
				),
				JSONUtil.put(
					"defaultValue", "w"
				).put(
					"name", _NAME_JOURNAL_ARTICLE
				).put(
					"value", "journal-test1"
				)
			).toString(),
			String.valueOf(
				friendlyURLSeparatorCompanyConfigurationDisplayContext.
					getConfigurableFriendlyURLSeparatorsJSONArray()));
	}

	@Test
	public void testGetConfigurableFriendlyURLSeparatorsJSONArrayWithSomeErrors()
		throws Exception {

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_getThemeDisplay()
		);

		Mockito.when(
			httpServletRequest.getParameterMap()
		).thenReturn(
			HashMapBuilder.put(
				_SIMPLE_CLASS_NAME_JOURNAL_ARTICLE, new String[] {"web"}
			).put(
				"errors",
				new String[] {
					JSONUtil.put(
						_NAME_JOURNAL_ARTICLE,
						"error-other-asset-type-may-use-this-prefix"
					).toString()
				}
			).build()
		);

		Mockito.when(
			httpServletRequest.getParameter(_SIMPLE_CLASS_NAME_JOURNAL_ARTICLE)
		).thenReturn(
			"web"
		);

		FriendlyURLSeparatorCompanyConfigurationDisplayContext
			friendlyURLSeparatorCompanyConfigurationDisplayContext =
				new FriendlyURLSeparatorCompanyConfigurationDisplayContext(
					_friendlyURLSeparatorConfigurationManager,
					httpServletRequest, _jsonFactory,
					Mockito.mock(Language.class), _portal);

		Assert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"defaultValue", "b"
				).put(
					"name", _NAME_BLOGS_ENTRY
				).put(
					"value", "blog-test1"
				),
				JSONUtil.put(
					"defaultValue", "w"
				).put(
					"name", _NAME_JOURNAL_ARTICLE
				).put(
					"value", "web"
				)
			).toString(),
			String.valueOf(
				friendlyURLSeparatorCompanyConfigurationDisplayContext.
					getConfigurableFriendlyURLSeparatorsJSONArray()));
	}

	@Test
	public void testGetErrorsJSONObject() {
		FriendlyURLSeparatorCompanyConfigurationDisplayContext
			friendlyURLSeparatorCompanyConfigurationDisplayContext =
				new FriendlyURLSeparatorCompanyConfigurationDisplayContext(
					_friendlyURLSeparatorConfigurationManager,
					Mockito.mock(HttpServletRequest.class), _jsonFactory,
					Mockito.mock(Language.class), _portal);

		Assert.assertEquals(
			_jsonFactory.createJSONObject(),
			friendlyURLSeparatorCompanyConfigurationDisplayContext.
				getErrorsJSONObject());
	}

	@Test
	public void testGetErrorsJSONObjectWithSomeErrors() throws JSONException {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		JSONObject errorsJSONObject = JSONUtil.put(
			_NAME_JOURNAL_ARTICLE,
			"error-other-asset-type-may-use-this-prefix");

		Mockito.when(
			httpServletRequest.getParameter("errors")
		).thenReturn(
			errorsJSONObject.toString()
		);

		Mockito.when(
			_jsonFactory.createJSONObject(Mockito.anyString())
		).thenReturn(
			errorsJSONObject
		);

		FriendlyURLSeparatorCompanyConfigurationDisplayContext
			friendlyURLSeparatorCompanyConfigurationDisplayContext =
				new FriendlyURLSeparatorCompanyConfigurationDisplayContext(
					_friendlyURLSeparatorConfigurationManager,
					httpServletRequest, _jsonFactory,
					Mockito.mock(Language.class), _portal);

		Assert.assertEquals(
			errorsJSONObject,
			friendlyURLSeparatorCompanyConfigurationDisplayContext.
				getErrorsJSONObject());
	}

	@Test
	public void testGetSeparatorFieldsProps() throws Exception {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_getThemeDisplay()
		);

		FriendlyURLSeparatorCompanyConfigurationDisplayContext
			friendlyURLSeparatorCompanyConfigurationDisplayContext =
				new FriendlyURLSeparatorCompanyConfigurationDisplayContext(
					_friendlyURLSeparatorConfigurationManager,
					httpServletRequest, _jsonFactory,
					Mockito.mock(Language.class), _portal);

		Map<String, Object> actualSeparatorFieldsProps =
			friendlyURLSeparatorCompanyConfigurationDisplayContext.
				getSeparatorFieldsProps();

		Assert.assertNull(actualSeparatorFieldsProps.get("errors"));

		JSONArray fieldsJSONArray = (JSONArray)actualSeparatorFieldsProps.get(
			"fields");

		Assert.assertNotNull(fieldsJSONArray);
		Assert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"defaultValue", "b"
				).put(
					"name", _NAME_BLOGS_ENTRY
				).put(
					"value", "blog-test1"
				),
				JSONUtil.put(
					"defaultValue", "w"
				).put(
					"name", _NAME_JOURNAL_ARTICLE
				).put(
					"value", "journal-test1"
				)
			).toString(),
			fieldsJSONArray.toString());

		Assert.assertNotNull(actualSeparatorFieldsProps.get("url"));
		Assert.assertEquals(
			"http://www.sitename.com", actualSeparatorFieldsProps.get("url"));
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		Company company = new CompanyImpl();

		company.setCompanyId(0);
		company.setGroupId(0);

		themeDisplay.setCompany(company);

		Layout layout = new LayoutImpl();

		layout.setType(LayoutConstants.TYPE_CONTROL_PANEL);

		themeDisplay.setLayout(layout);

		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setPpid(_INSTANCE_SETTINGS_PORTLET_NAMESPACE);

		return themeDisplay;
	}

	private static final String _INSTANCE_SETTINGS_PORTLET_NAMESPACE =
		StringPool.UNDERLINE + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS +
			StringPool.UNDERLINE;

	private static final String _NAME_BLOGS_ENTRY =
		_INSTANCE_SETTINGS_PORTLET_NAMESPACE +
			FriendlyURLSeparatorCompanyConfigurationDisplayContextTest.
				_SIMPLE_CLASS_NAME_BLOGS_ENTRY;

	private static final String _NAME_JOURNAL_ARTICLE =
		_INSTANCE_SETTINGS_PORTLET_NAMESPACE +
			FriendlyURLSeparatorCompanyConfigurationDisplayContextTest.
				_SIMPLE_CLASS_NAME_JOURNAL_ARTICLE;

	private static final String _SIMPLE_CLASS_NAME_BLOGS_ENTRY = "BlogsEntry";

	private static final String _SIMPLE_CLASS_NAME_JOURNAL_ARTICLE =
		"JournalArticle";

	private static MockedStatic<FriendlyURLResolverRegistryUtil>
		_friendlyURLResolverRegistryUtilMockedStatic;
	private static FriendlyURLSeparatorConfigurationManager
		_friendlyURLSeparatorConfigurationManager;
	private static JSONFactory _jsonFactory;
	private static Portal _portal;

	private static class FriendlyURLResolverImpl
		implements FriendlyURLResolver {

		public FriendlyURLResolverImpl(
			String defaultURLSeparator, String key, String urlSeparator) {

			_defaultURLSeparator = defaultURLSeparator;
			_key = key;
			_urlSeparator = urlSeparator;
		}

		@Override
		public String getActualURL(
				long companyId, long groupId, boolean privateLayout,
				String mainPath, String friendlyURL,
				Map<String, String[]> params,
				Map<String, Object> requestContext)
			throws PortalException {

			return null;
		}

		@Override
		public String getDefaultURLSeparator() {
			return _defaultURLSeparator;
		}

		@Override
		public String getKey() {
			return _key;
		}

		@Override
		public LayoutFriendlyURLComposite getLayoutFriendlyURLComposite(
				long companyId, long groupId, boolean privateLayout,
				String friendlyURL, Map<String, String[]> params,
				Map<String, Object> requestContext)
			throws PortalException {

			return null;
		}

		@Override
		public String getURLSeparator() {
			return _urlSeparator;
		}

		@Override
		public boolean isURLSeparatorConfigurable() {
			return true;
		}

		private final String _defaultURLSeparator;
		private final String _key;
		private final String _urlSeparator;

	}

}