/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.rule.ClassTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PropsValues;

import org.junit.runner.Description;

/**
 * @author Jonathan McCann
 */
public class LanguageIdTestRule extends ClassTestRule<Void> {

	public static final LanguageIdTestRule INSTANCE = new LanguageIdTestRule();

	@Override
	protected void afterClass(Description description, Void v)
		throws Throwable {

		LanguageIds languageIds = description.getAnnotation(LanguageIds.class);

		if (languageIds == null) {
			return;
		}

		_setCompanyLocales(
			_originalAvailableLanguageIds, _originalDefaultLanguageId,
			_originalLocalesEnabled);
	}

	@Override
	protected Void beforeClass(Description description) throws Throwable {
		LanguageIds languageIds = description.getAnnotation(LanguageIds.class);

		if (languageIds == null) {
			return null;
		}

		_originalAvailableLanguageIds = StringUtil.merge(
			LocaleUtil.toLanguageIds(LanguageUtil.getAvailableLocales()));
		_originalDefaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getDefault());
		_originalLocalesEnabled = PropsValues.LOCALES_ENABLED;

		_setCompanyLocales(
			ArrayUtil.toString(
				languageIds.availableLanguageIds(), StringPool.BLANK),
			languageIds.defaultLanguageId(),
			languageIds.availableLanguageIds());

		return null;
	}

	private void _setCompanyLocales(
			String availableLanguageIds, String defaultLanguageId,
			String[] localesEnabled)
		throws Exception {

		LanguageUtil.init();

		CompanyTestUtil.resetCompanyLocales(
			PortalUtil.getDefaultCompanyId(), availableLanguageIds,
			defaultLanguageId);

		PropsValues.LOCALES_ENABLED = localesEnabled;
	}

	private String _originalAvailableLanguageIds;
	private String _originalDefaultLanguageId;
	private String[] _originalLocalesEnabled;

}