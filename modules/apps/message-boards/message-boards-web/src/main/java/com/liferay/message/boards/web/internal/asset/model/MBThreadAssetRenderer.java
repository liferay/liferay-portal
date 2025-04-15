/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.asset.model;

import com.liferay.asset.kernel.model.BaseAssetRenderer;
import com.liferay.message.boards.model.MBThread;
import com.liferay.petra.string.StringPool;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Adolfo Pérez
 */
public class MBThreadAssetRenderer extends BaseAssetRenderer<MBThread> {

	public MBThreadAssetRenderer(MBThread mbThread) {
		_mbThread = mbThread;
	}

	@Override
	public MBThread getAssetObject() {
		return _mbThread;
	}

	@Override
	public String getClassName() {
		return MBThread.class.getName();
	}

	@Override
	public long getClassPK() {
		return _mbThread.getThreadId();
	}

	@Override
	public long getGroupId() {
		return _mbThread.getGroupId();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		return _mbThread.getTitle();
	}

	@Override
	public long getUserId() {
		return _mbThread.getUserId();
	}

	@Override
	public String getUserName() {
		return _mbThread.getUserName();
	}

	@Override
	public String getUuid() {
		return _mbThread.getUuid();
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

	private final MBThread _mbThread;

}