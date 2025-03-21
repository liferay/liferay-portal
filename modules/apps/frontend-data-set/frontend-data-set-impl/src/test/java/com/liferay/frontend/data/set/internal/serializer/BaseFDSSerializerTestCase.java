/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.serializer;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.mockito.Mockito;

/**
 * @author Daniel Sanz
 */
public abstract class BaseFDSSerializerTestCase {

	public void setUp() {
		defaultPagination = JSONUtil.put(
			"deltas",
			() -> JSONUtil.toJSONArray(
				ListUtil.fromArray(
					PropsValues.SEARCH_CONTAINER_PAGE_DELTA_VALUES),
				itemsPerPage -> JSONUtil.put("label", itemsPerPage))
		).put(
			"initialDelta", PropsValues.SEARCH_CONTAINER_PAGE_DEFAULT_DELTA
		).toString();
	}

	protected void mockLanguage() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(LocaleUtil.US, null)
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			language.get(Mockito.eq(LocaleUtil.US), Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArgument(1, String.class)
		);

		Mockito.when(
			language.get(
				Mockito.eq(ResourceBundleUtil.EMPTY_RESOURCE_BUNDLE),
				Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArgument(1, String.class)
		);

		languageUtil.setLanguage(language);

		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getLocale(httpServletRequest)
		).thenReturn(
			LocaleUtil.US
		);

		portalUtil.setPortal(portal);

		ResourceBundleLoader resourceBundleLoader = Mockito.mock(
			ResourceBundleLoader.class);

		Mockito.when(
			resourceBundleLoader.loadResourceBundle(
				Mockito.nullable(Locale.class))
		).thenReturn(
			ResourceBundleUtil.EMPTY_RESOURCE_BUNDLE
		);

		ResourceBundleLoaderUtil.setPortalResourceBundleLoader(
			resourceBundleLoader);
	}

	protected static final String[] CONTENT_RENDERERS =
		RandomTestUtil.randomStrings(2);

	protected static final int[] DEFAULT_ITEMS_PER_PAGE_ARRAY = {
		RandomTestUtil.randomInt(), RandomTestUtil.randomInt()
	};

	protected static final String[] DESCRIPTIONS = RandomTestUtil.randomStrings(
		2);

	protected static final String[] FDS_NAMES = RandomTestUtil.randomStrings(2);

	protected static final String[] FIELD_NAMES = RandomTestUtil.randomStrings(
		3);

	protected static final String[] ICONS = RandomTestUtil.randomStrings(2);

	protected static final String[] IDS = RandomTestUtil.randomStrings(4);

	protected static final String[] IMAGES = RandomTestUtil.randomStrings(2);

	protected static final String ITEM_KEY = RandomTestUtil.randomString();

	protected static final String[] LABELS = RandomTestUtil.randomStrings(4);

	protected static final String LINK = RandomTestUtil.randomString();

	protected static final int[][] LIST_OF_ITEMS_PER_PAGE_ARRAY = {
		{RandomTestUtil.randomInt(), RandomTestUtil.randomInt()},
		{
			RandomTestUtil.randomInt(), RandomTestUtil.randomInt(),
			RandomTestUtil.randomInt()
		},
		{-1, 3, 0, 5}, {3, 5}
	};

	protected static final String[] PROPS_TRANSFORMERS =
		RandomTestUtil.randomStrings(2);

	protected static final String[] STICKERS = RandomTestUtil.randomStrings(2);

	protected static final String[] SYMBOLS = RandomTestUtil.randomStrings(2);

	protected static final String[] TITLES = RandomTestUtil.randomStrings(3);

	protected static final String URL = RandomTestUtil.randomString();

	protected String defaultPagination;
	protected final HttpServletRequest httpServletRequest = Mockito.mock(
		HttpServletRequest.class);

}