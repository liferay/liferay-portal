/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.info.item.renderer;

import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class JournalArticleRendererUtil {

	public static boolean isShowArticle(
		HttpServletRequest httpServletRequest, JournalArticle article) {

		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(httpServletRequest);

		String mode = ParamUtil.getString(
			PortalUtil.getOriginalServletRequest(originalHttpServletRequest),
			"p_l_mode", Constants.VIEW);

		if (Objects.equals(Constants.EDIT, mode) ||
			Objects.equals(Constants.PREVIEW, mode)) {

			return true;
		}

		if ((article == null) || article.isExpired()) {
			return false;
		}

		return true;
	}

}