/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.taglib.servlet.taglib;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagServiceUtil;
import com.liferay.taglib.TagSupport;

import jakarta.servlet.jsp.JspException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergio González
 */
public class AssetTagsAvailableTag<R> extends TagSupport {

	@Override
	public int doStartTag() throws JspException {
		try {
			_assetTags = AssetTagServiceUtil.getTags(_className, _classPK);

			if (!_assetTags.isEmpty()) {
				return EVAL_BODY_INCLUDE;
			}

			return SKIP_BODY;
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
		finally {
			_className = null;
			_classPK = 0;
		}
	}

	public List<AssetTag> getAssetTags() {
		return _assetTags;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	private List<AssetTag> _assetTags = new ArrayList<>();
	private String _className;
	private long _classPK;

}