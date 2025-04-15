/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.display.context.helper;

import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.display.context.helper.BaseRequestHelper;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Iván Zaera
 */
public class MBRequestHelper extends BaseRequestHelper {

	public MBRequestHelper(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);
	}

	public MBCategory getCategory() {
		if (_category == null) {
			HttpServletRequest httpServletRequest = getRequest();

			_category = (MBCategory)httpServletRequest.getAttribute(
				WebKeys.MESSAGE_BOARDS_CATEGORY);
		}

		return _category;
	}

	public long getParentCategoryId() {
		if (_parentCategoryId == null) {
			_parentCategoryId = BeanParamUtil.getLong(
				getCategory(), getRequest(), "parentCategoryId",
				MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
		}

		return _parentCategoryId;
	}

	private MBCategory _category;
	private Long _parentCategoryId;

}