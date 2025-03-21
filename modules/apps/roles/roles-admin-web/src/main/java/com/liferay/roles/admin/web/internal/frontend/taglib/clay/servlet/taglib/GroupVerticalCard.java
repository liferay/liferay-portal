/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseVerticalCard;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.util.HtmlUtil;

import jakarta.portlet.RenderRequest;

/**
 * @author Eudaldo Alonso
 */
public class GroupVerticalCard extends BaseVerticalCard {

	public GroupVerticalCard(
		Group group, RenderRequest renderRequest, RowChecker rowChecker) {

		super(group, renderRequest, rowChecker);

		_group = group;
	}

	@Override
	public String getIcon() {
		return "sites";
	}

	@Override
	public String getSubtitle() {
		return LanguageUtil.get(
			themeDisplay.getLocale(), _group.getTypeLabel());
	}

	@Override
	public String getTitle() {
		try {
			return HtmlUtil.escape(
				_group.getDescriptiveName(themeDisplay.getLocale()));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return HtmlUtil.escape(_group.getName(themeDisplay.getLocale()));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GroupVerticalCard.class);

	private final Group _group;

}