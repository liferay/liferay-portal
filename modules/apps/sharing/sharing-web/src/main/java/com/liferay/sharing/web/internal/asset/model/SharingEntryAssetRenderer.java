/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.asset.model;

import com.liferay.asset.kernel.model.BaseAssetRenderer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.sharing.interpreter.SharingEntryInterpreter;
import com.liferay.sharing.interpreter.SharingEntryInterpreterProvider;
import com.liferay.sharing.model.SharingEntry;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Mikel Lorza
 */
public class SharingEntryAssetRenderer extends BaseAssetRenderer<SharingEntry> {

	public SharingEntryAssetRenderer(
		SharingEntry sharingEntry,
		SharingEntryInterpreterProvider sharingEntryInterpreterProvider) {

		_sharingEntry = sharingEntry;
		_sharingEntryInterpreterProvider = sharingEntryInterpreterProvider;
	}

	@Override
	public SharingEntry getAssetObject() {
		return _sharingEntry;
	}

	@Override
	public String getClassName() {
		return SharingEntry.class.getName();
	}

	@Override
	public long getClassPK() {
		return _sharingEntry.getSharingEntryId();
	}

	@Override
	public long getGroupId() {
		return _sharingEntry.getGroupId();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		try {
			SharingEntryInterpreter sharingEntryInterpreter =
				_sharingEntryInterpreterProvider.getSharingEntryInterpreter(
					_sharingEntry);

			if (sharingEntryInterpreter == null) {
				return StringPool.BLANK;
			}

			return sharingEntryInterpreter.getAssetTypeTitle(
				_sharingEntry, locale);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return StringPool.BLANK;
	}

	@Override
	public long getUserId() {
		return _sharingEntry.getUserId();
	}

	@Override
	public String getUserName() {
		return _sharingEntry.getUserName();
	}

	@Override
	public String getUuid() {
		return _sharingEntry.getUuid();
	}

	@Override
	public boolean include(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String template) {

		return false;
	}

	@Override
	public boolean isDisplayable() {
		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SharingEntryAssetRenderer.class);

	private final SharingEntry _sharingEntry;
	private final SharingEntryInterpreterProvider
		_sharingEntryInterpreterProvider;

}