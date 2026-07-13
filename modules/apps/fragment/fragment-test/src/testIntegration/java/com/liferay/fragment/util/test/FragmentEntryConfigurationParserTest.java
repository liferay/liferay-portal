/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class FragmentEntryConfigurationParserTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetConfigurationDefaultValuesJSONObject() throws Exception {
		JSONObject configurationDefaultValuesJSONObject =
			_fragmentEntryConfigurationParser.
				getConfigurationDefaultValuesJSONObject(
					_readJSONObject("configuration.json"));

		JSONObject expectedConfigurationDefaultValuesJSONObject =
			_readJSONObject("expected-configuration-default-values.json");

		Assert.assertEquals(
			expectedConfigurationDefaultValuesJSONObject.toString(),
			configurationDefaultValuesJSONObject.toString());
	}

	@Test
	@TestInfo("LPD-67912")
	public void testGetConfigurationJSONObjectURLConfigurationWithMappedLayout()
		throws Exception {

		Group group1 = _groupLocalService.getGroup(
			TestPropsValues.getGroupId());

		Layout layout1 = LayoutTestUtil.addTypeContentLayout(group1);

		ServiceContext serviceContext = _getAndPushServiceContext(
			group1, layout1);

		String name = RandomTestUtil.randomString();

		JSONObject configurationValuesJSONObject =
			_fragmentEntryConfigurationParser.getConfigurationJSONObject(
				JSONUtil.put(
					"fieldSets",
					JSONUtil.put(
						JSONUtil.put(
							"fields",
							JSONUtil.put(
								JSONUtil.put(
									"label", RandomTestUtil.randomString()
								).put(
									"name", name
								).put(
									"type", "url"
								))))),
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						name,
						JSONUtil.put(
							"layout",
							JSONUtil.put(
								"externalReferenceCode",
								layout1.getExternalReferenceCode()
							).put(
								"title", name
							)))),
				LocaleUtil.US);

		Assert.assertEquals(
			_portal.getLayoutFullURL(layout1, serviceContext.getThemeDisplay()),
			configurationValuesJSONObject.getString(name));

		Group group2 = GroupTestUtil.addGroup();

		Layout layout2 = _layoutLocalService.addLayout(
			layout1.getExternalReferenceCode(), TestPropsValues.getUserId(),
			group2.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			LayoutConstants.TYPE_CONTENT, false, StringPool.BLANK,
			serviceContext);

		configurationValuesJSONObject =
			_fragmentEntryConfigurationParser.getConfigurationJSONObject(
				JSONUtil.put(
					"fieldSets",
					JSONUtil.put(
						JSONUtil.put(
							"fields",
							JSONUtil.put(
								JSONUtil.put(
									"label", RandomTestUtil.randomString()
								).put(
									"name", name
								).put(
									"type", "url"
								))))),
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						name,
						JSONUtil.put(
							"layout",
							JSONUtil.put(
								"externalReferenceCode",
								layout1.getExternalReferenceCode()
							).put(
								"scopeExternalReferenceCode",
								group2.getExternalReferenceCode()
							).put(
								"title", name
							)))),
				LocaleUtil.US);

		Assert.assertEquals(
			_portal.getLayoutFullURL(layout2, serviceContext.getThemeDisplay()),
			configurationValuesJSONObject.getString(name));

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo("LPD-97996")
	public void testGetFieldValueURLConfiguration() throws Exception {
		_testGetFieldValueURLConfiguration(null, StringPool.BLANK);
		_testGetFieldValueURLConfiguration(null, RandomTestUtil.randomString());

		Group group = _groupLocalService.getGroup(TestPropsValues.getGroupId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		ServiceContext serviceContext = _getAndPushServiceContext(
			group, layout);

		_testGetFieldValueURLConfiguration(
			StringPool.POUND,
			JSONUtil.put(
				"layout",
				JSONUtil.put(
					"externalReferenceCode", RandomTestUtil.randomString())
			).toString());

		_testGetFieldValueURLConfiguration(
			_portal.getLayoutFullURL(layout, serviceContext.getThemeDisplay()),
			JSONUtil.put(
				"layout",
				JSONUtil.put(
					"externalReferenceCode", layout.getExternalReferenceCode())
			).toString());

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo("LPD-77079")
	public void testGetFieldValueWithLocalizableFields() {
		_testGetFieldValueWithLocalizableField(
			"checkbox", Boolean.FALSE, Boolean.TRUE);
		_testGetFieldValueWithLocalizableField(
			"colorPicker", "#0F0303", "#35CC58");
		_testGetFieldValueWithLocalizableField("length", "300px", "320px");
		_testGetFieldValueWithLocalizableField(
			"text", RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
	}

	@Test
	public void testTranslateConfigurationEn() throws Exception {
		_testTranslateConfiguration("en");
	}

	@Test
	public void testTranslateConfigurationEs() throws Exception {
		_testTranslateConfiguration("es");
	}

	private ServiceContext _getAndPushServiceContext(Group group, Layout layout)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		HttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_companyLocalService.getCompany(group.getCompanyId()), group,
				layout);

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());

		serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		return serviceContext;
	}

	private ResourceBundle _getResourceBundle(String language) {
		Class<?> clazz = getClass();

		Package pkg = clazz.getPackage();

		return ResourceBundleUtil.getBundle(
			pkg.getName() + ".dependencies.content.Language",
			new Locale(language), clazz);
	}

	private JSONObject _readJSONObject(String fileName) throws Exception {
		return JSONFactoryUtil.createJSONObject(
			new String(
				FileUtil.getBytes(getClass(), "dependencies/" + fileName)));
	}

	private void _testGetFieldValueURLConfiguration(
		Object expectedValue, String value) {

		String name = RandomTestUtil.randomString();

		Assert.assertEquals(
			expectedValue,
			_fragmentEntryConfigurationParser.getFieldValue(
				JSONUtil.put(
					"fieldSets",
					JSONUtil.put(
						JSONUtil.put(
							"fields",
							JSONUtil.put(
								JSONUtil.put(
									"label", RandomTestUtil.randomString()
								).put(
									"name", name
								).put(
									"type", "url"
								))))),
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(name, value)),
				name));
	}

	private void _testGetFieldValueWithLocalizableField(
		String fieldType, Object expectedEnglishValue,
		Object expectedSpanishValue) {

		String fieldName = RandomTestUtil.randomString();

		JSONObject configurationJSONObject = JSONUtil.put(
			"fieldSets",
			JSONUtil.put(
				JSONUtil.put(
					"fields",
					JSONUtil.put(
						JSONUtil.put(
							"defaultValue", expectedEnglishValue
						).put(
							"label", fieldName
						).put(
							"localizable", true
						).put(
							"name", fieldName
						).put(
							"type", fieldType
						)))));

		JSONObject valueJSONObject =
			(JSONObject)_fragmentEntryConfigurationParser.getFieldValue(
				configurationJSONObject,
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						fieldName,
						JSONUtil.put(
							LocaleUtil.toLanguageId(LocaleUtil.US),
							expectedEnglishValue
						).put(
							LocaleUtil.toLanguageId(LocaleUtil.SPAIN),
							expectedSpanishValue
						))),
				fieldName);

		Object englishValue = null;
		Object spanishValue = null;

		if (expectedEnglishValue instanceof Boolean) {
			englishValue = valueJSONObject.getBoolean(
				LocaleUtil.toLanguageId(LocaleUtil.US));
			spanishValue = valueJSONObject.getBoolean(
				LocaleUtil.toLanguageId(LocaleUtil.SPAIN));
		}
		else {
			englishValue = valueJSONObject.getString(
				LocaleUtil.toLanguageId(LocaleUtil.US));
			spanishValue = valueJSONObject.getString(
				LocaleUtil.toLanguageId(LocaleUtil.SPAIN));
		}

		Assert.assertEquals(expectedEnglishValue, englishValue);
		Assert.assertEquals(expectedSpanishValue, spanishValue);
	}

	private void _testTranslateConfiguration(String language) throws Exception {
		JSONObject configurationJSONObject = _readJSONObject(
			"configuration_untranslated.json");

		JSONObject expectedConfigurationTranslatedJSONObject = _readJSONObject(
			String.format(
				"expected_configuration_translated_%s.json", language));

		Assert.assertEquals(
			expectedConfigurationTranslatedJSONObject.toString(),
			String.valueOf(
				_fragmentEntryConfigurationParser.translateConfiguration(
					configurationJSONObject, _getResourceBundle(language))));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Portal _portal;

}