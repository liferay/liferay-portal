/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.configuration.persistence.listener;

import com.liferay.document.library.configuration.DLFileEntryConfiguration;
import com.liferay.document.library.configuration.DLFileEntryConfigurationProvider;
import com.liferay.document.library.constants.DLFileEntryConfigurationConstants;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "model.class.name=com.liferay.document.library.configuration.DLFileEntryConfiguration",
	service = ConfigurationModelListener.class
)
public class DLFileEntryConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		if (CompanyThreadLocal.isUpgradingPortalInstance()) {
			return;
		}

		ExtendedObjectClassDefinition.Scope scope = null;
		long scopePK = 0;

		long companyId = GetterUtil.getLong(properties.get("companyId"));
		long groupId = GetterUtil.getLong(properties.get("groupId"));

		if (groupId > 0) {
			scope = ExtendedObjectClassDefinition.Scope.GROUP;
			scopePK = groupId;
		}
		else if (companyId > 0) {
			scope = ExtendedObjectClassDefinition.Scope.COMPANY;
			scopePK = companyId;
		}
		else {
			return;
		}

		int maxNumberOfPages = GetterUtil.getInteger(
			properties.get("maxNumberOfPages"));

		if (_isLimitExceeded(
				_dlFileEntryConfigurationProvider.getMaxNumberOfPagesLimit(
					scope, scopePK),
				DLFileEntryConfigurationConstants.MAX_NUMBER_OF_PAGES_UNLIMITED,
				maxNumberOfPages)) {

			throw new ConfigurationModelListenerException(
				_getMessage("maximum-number-of-pages-limit-is-invalid"),
				DLFileEntryConfiguration.class, getClass(), properties);
		}

		long previewableProcessorMaxSize = GetterUtil.getLong(
			properties.get("previewableProcessorMaxSize"));

		if (_isLimitExceeded(
				_dlFileEntryConfigurationProvider.
					getPreviewableProcessorMaxSizeLimit(scope, scopePK),
				DLFileEntryConfigurationConstants.
					PREVIEWABLE_PROCESSOR_MAX_SIZE_UNLIMITED,
				previewableProcessorMaxSize)) {

			throw new ConfigurationModelListenerException(
				_getMessage("maximum-file-size-limit-is-invalid"),
				DLFileEntryConfiguration.class, getClass(), properties);
		}
	}

	private String _getMessage(String key) {
		return _language.get(LocaleThreadLocal.getThemeDisplayLocale(), key);
	}

	private boolean _isLimitExceeded(
		long limit, long unlimitedValue, long value) {

		if ((limit != unlimitedValue) &&
			((value == unlimitedValue) || (limit < value))) {

			return true;
		}

		return false;
	}

	@Reference
	private DLFileEntryConfigurationProvider _dlFileEntryConfigurationProvider;

	@Reference
	private Language _language;

}