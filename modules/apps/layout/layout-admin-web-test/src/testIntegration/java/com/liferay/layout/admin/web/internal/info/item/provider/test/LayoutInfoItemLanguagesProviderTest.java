/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.translation.info.item.provider.InfoItemLanguagesProvider;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class LayoutInfoItemLanguagesProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetAvailableLanguages() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		InfoItemLanguagesProvider<Object> infoItemLanguagesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemLanguagesProvider.class, Layout.class.getName());

		Assert.assertArrayEquals(
			infoItemLanguagesProvider.getAvailableLanguageIds(layout),
			layout.getAvailableLanguageIds());
	}

	@Test
	public void testGetAvailableLanguagesContentLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		InfoItemLanguagesProvider<Object> infoItemLanguagesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemLanguagesProvider.class, Layout.class.getName());

		Assert.assertArrayEquals(
			_getAvailableLocalesLayoutTranslatedLanguages(layout),
			infoItemLanguagesProvider.getAvailableLanguageIds(layout));
	}

	@Test
	public void testGetAvailableLanguagesWithoutLocalesInheritedFromCompany()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		layout.setNameMap(
			HashMapBuilder.put(
				LocaleUtil.SPAIN, RandomTestUtil.randomString()
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build());

		layout = _layoutLocalService.updateLayout(layout);

		String[] availableLanguages = {"en_US"};

		_group = _groupLocalService.updateGroup(
			_group.getGroupId(),
			UnicodePropertiesBuilder.create(
				true
			).fastLoad(
				_group.getTypeSettings()
			).put(
				PropsKeys.LOCALES, StringUtil.merge(availableLanguages)
			).put(
				"inheritLocales", Boolean.FALSE.toString()
			).buildString());

		InfoItemLanguagesProvider<Object> infoItemLanguagesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemLanguagesProvider.class, Layout.class.getName());

		Assert.assertArrayEquals(
			availableLanguages,
			infoItemLanguagesProvider.getAvailableLanguageIds(layout));
	}

	private String[] _getAvailableLocalesLayoutTranslatedLanguages(
			Layout layout)
		throws Exception {

		Set<String> availableLocales = new HashSet<>();

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				_group.getGroupId(), layout.getPlid());

		Set<Locale> siteAvailableLocales = _language.getAvailableLocales(
			_group.getGroupId());

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			JSONObject editableValuesJSONObject =
				fragmentEntryLink.getEditableValuesJSONObject();

			for (String translatableFragment : _TRANSLATABLE_FRAGMENTS) {
				availableLocales.addAll(
					_getTranslatableFragmentLocales(
						editableValuesJSONObject, translatableFragment,
						siteAvailableLocales));
			}
		}

		availableLocales.add(layout.getDefaultLanguageId());

		return availableLocales.toArray(new String[0]);
	}

	private Set<String> _getTranslatableFragmentLocales(
		JSONObject jsonObject, String key, Set<Locale> siteAvailableLocales) {

		Set<String> availableLocales = new HashSet<>();

		JSONObject editableFragmentJSONObject = jsonObject.getJSONObject(key);

		if (!(editableFragmentJSONObject instanceof JSONObject) ||
			(editableFragmentJSONObject.length() <= 0)) {

			return availableLocales;
		}

		Iterator<String> editableFragmentIterator =
			editableFragmentJSONObject.keys();

		while (editableFragmentIterator.hasNext()) {
			Object editableValueObject = editableFragmentJSONObject.get(
				editableFragmentIterator.next());

			if (!(editableValueObject instanceof JSONObject)) {
				continue;
			}

			JSONObject editableValueJSONObject =
				(JSONObject)editableValueObject;

			if (editableValueJSONObject.length() <= 0) {
				continue;
			}

			for (Locale siteAvailableLocale : siteAvailableLocales) {
				Object valueObject = editableValueJSONObject.get(
					_language.getLanguageId(siteAvailableLocale));

				if (valueObject != null) {
					availableLocales.add(
						LocaleUtil.toLanguageId(siteAvailableLocale));
				}
			}
		}

		return availableLocales;
	}

	private static final String[] _TRANSLATABLE_FRAGMENTS = {
		FragmentEntryProcessorConstants.
			KEY_BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR,
		FragmentEntryProcessorConstants.KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR
	};

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private Language _language;

	@Inject
	private LayoutLocalService _layoutLocalService;

}