/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib;

import com.liferay.portal.kernel.servlet.taglib.aui.ValidatorTag;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.taglib.aui.ValidatorTagImpl;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Julio Camarero
 * @author Jorge Ferrer
 * @author Brian Wing Shun Chan
 */
public abstract class BaseValidatorTagSupport extends IncludeTag {

	public void addRequiredValidatorTag() {
		addRequiredValidatorTag(null);
	}

	public void addRequiredValidatorTag(String errorMessage) {
		ValidatorTag validatorTag = new ValidatorTagImpl(
			"required", errorMessage, null, false);

		addValidatorTag("required", validatorTag);
	}

	public void addValidatorTag(
		String validatorName, ValidatorTag validatorTag) {

		if (_validatorTags == null) {
			_validatorTags = new HashMap<>();
		}

		_validatorTags.put(validatorName, validatorTag);
	}

	@Override
	public int doEndTag() throws JspException {
		updateFormValidatorTags();

		return super.doEndTag();
	}

	public abstract String getInputName();

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_validatorTags = null;
	}

	protected Map<String, ValidatorTag> getValidatorTags() {
		return _validatorTags;
	}

	protected void updateFormValidatorTags() {
		if (_validatorTags == null) {
			return;
		}

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		Map<String, List<ValidatorTag>> validatorTagsMap =
			(Map<String, List<ValidatorTag>>)httpServletRequest.getAttribute(
				"LIFERAY_SHARED_aui:form:validatorTagsMap");

		if (validatorTagsMap != null) {
			List<ValidatorTag> validatorTags = ListUtil.fromMapValues(
				_validatorTags);

			validatorTagsMap.put(getInputName(), validatorTags);
		}
	}

	private Map<String, ValidatorTag> _validatorTags;

}