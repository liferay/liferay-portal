/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.configuration.DLFileEntryConfigurationProvider;
import com.liferay.document.library.constants.DLFileEntryConfigurationConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Marco Galluzzi
 */
public class DLFileEntryConfigurationDisplayContext {

	public DLFileEntryConfigurationDisplayContext(
		DLFileEntryConfigurationProvider dlFileEntryConfigurationProvider,
		HttpServletRequest httpServletRequest,
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		_dlFileEntryConfigurationProvider = dlFileEntryConfigurationProvider;
		_httpServletRequest = httpServletRequest;
		_scope = scope;
		_scopePK = scopePK;
	}

	public int getMaxNumberOfPages() throws PortalException {
		return _dlFileEntryConfigurationProvider.getMaxNumberOfPages(
			_scope, _scopePK);
	}

	public int getMaxNumberOfPagesLimit() throws PortalException {
		return _dlFileEntryConfigurationProvider.getMaxNumberOfPagesLimit(
			_scope, _scopePK);
	}

	public String getMaxNumberOfPagesLimitExceededErrorMessage()
		throws PortalException {

		return _getLimitExceededErrorMessage(getMaxNumberOfPagesLimit());
	}

	public long getPreviewableProcessorMaxSize() throws PortalException {
		return _dlFileEntryConfigurationProvider.getPreviewableProcessorMaxSize(
			_scope, _scopePK);
	}

	public long getPreviewableProcessorMaxSizeLimit() throws PortalException {
		return _dlFileEntryConfigurationProvider.
			getPreviewableProcessorMaxSizeLimit(_scope, _scopePK);
	}

	public String getPreviewableProcessorMaxSizeLimitExceededErrorMessage()
		throws PortalException {

		return _getLimitExceededErrorMessage(
			getPreviewableProcessorMaxSizeLimit());
	}

	public int getUnlimitedMaxNumberOfPages() {
		return DLFileEntryConfigurationConstants.MAX_NUMBER_OF_PAGES_UNLIMITED;
	}

	public long getUnlimitedPreviewableProcessorMaxSize() {
		return DLFileEntryConfigurationConstants.
			PREVIEWABLE_PROCESSOR_MAX_SIZE_UNLIMITED;
	}

	private String _getLimitExceededErrorMessage(long limitValue)
		throws PortalException {

		return LanguageUtil.format(
			_httpServletRequest,
			"this-limit-is-higher-than-x-limit-enter-maximum-value-x",
			new Object[] {_getSuperiorScopeLabel(), limitValue});
	}

	private String _getSuperiorScopeLabel() {
		if (_scope == ExtendedObjectClassDefinition.Scope.COMPANY) {
			return LanguageUtil.get(_httpServletRequest, "system-settings");
		}
		else if (_scope == ExtendedObjectClassDefinition.Scope.GROUP) {
			return LanguageUtil.get(_httpServletRequest, "instance-settings");
		}

		return StringPool.BLANK;
	}

	private final DLFileEntryConfigurationProvider
		_dlFileEntryConfigurationProvider;
	private final HttpServletRequest _httpServletRequest;
	private final ExtendedObjectClassDefinition.Scope _scope;
	private final long _scopePK;

}