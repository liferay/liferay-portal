/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.resource.bundle;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.language.LanguageResources;
import com.liferay.translation.constants.TranslationConstants;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "bundle.symbolic.name=com.liferay.translation.web",
	service = ResourceBundleLoader.class
)
public class TranslationResourceBundleLoader implements ResourceBundleLoader {

	@Override
	public ResourceBundle loadResourceBundle(Locale locale) {
		return new AggregateResourceBundle(
			LanguageResources.getResourceBundle(locale),
			new ResourceBundle() {

				@Override
				public Enumeration<String> getKeys() {
					return Collections.enumeration(
						TransformUtil.transformToList(
							PropsValues.LOCALES,
							languageId -> _PREFIX + languageId));
				}

				@Override
				protected Object handleGetObject(String key) {
					String languageId = StringUtil.removeSubstring(
						key, _PREFIX);

					Locale keyLocale = LocaleUtil.fromLanguageId(languageId);

					return StringBundler.concat(
						languageId, StringPool.SPACE, StringPool.DASH,
						StringPool.SPACE, keyLocale.getDisplayName(locale));
				}

			});
	}

	private static final String _PREFIX =
		"model.resource." + TranslationConstants.RESOURCE_NAME +
			StringPool.PERIOD;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

}