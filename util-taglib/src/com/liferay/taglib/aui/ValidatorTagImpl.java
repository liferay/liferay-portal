/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.servlet.taglib.aui.ValidatorTag;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.BaseValidatorTagSupport;
import com.liferay.taglib.aui.base.BaseValidatorTagImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTag;

/**
 * @author Julio Camarero
 * @author Brian Wing Shun Chan
 */
public class ValidatorTagImpl
	extends BaseValidatorTagImpl implements BodyTag, ValidatorTag {

	public ValidatorTagImpl() {
	}

	public ValidatorTagImpl(
		String name, String errorMessage, String body, boolean custom) {

		setName(name);
		setErrorMessage(errorMessage);

		_body = body;
		_custom = custom;
	}

	@Override
	public void cleanUp() {
		super.cleanUp();

		_body = null;
		_custom = false;
	}

	@Override
	public int doAfterBody() {
		BodyContent bodyContent = getBodyContent();

		if (bodyContent != null) {
			_body = bodyContent.getString();
		}

		return SKIP_BODY;
	}

	@Override
	public int doEndTag() {
		BaseValidatorTagSupport baseValidatorTagSupport =
			(BaseValidatorTagSupport)findAncestorWithClass(
				this, BaseValidatorTagSupport.class);

		String name = getName();

		_custom = ModelHintsUtil.isCustomValidator(name);

		if (_custom) {
			StringBundler sb = new StringBundler(3);

			String namespace = baseValidatorTagSupport.getInputName();

			sb.append(namespace);

			sb.append(StringPool.UNDERLINE);

			HttpServletRequest httpServletRequest =
				(HttpServletRequest)pageContext.getRequest();

			sb.append(
				PortalUtil.getUniqueElementId(
					httpServletRequest, namespace, name));

			name = sb.toString();
		}

		ValidatorTag validatorTag = new ValidatorTagImpl(
			name, getErrorMessage(), _body, _custom);

		baseValidatorTagSupport.addValidatorTag(name, validatorTag);

		return EVAL_BODY_BUFFERED;
	}

	@Override
	public String getBody() {
		if (Validator.isNull(_body)) {
			return StringPool.DOUBLE_APOSTROPHE;
		}

		return _body.trim();
	}

	@Override
	public String getErrorMessage() {
		String errorMessage = super.getErrorMessage();

		if (errorMessage == null) {
			return StringPool.BLANK;
		}

		return errorMessage;
	}

	@Override
	public boolean isCustom() {
		return _custom;
	}

	public void setBody(String body) {
		_body = body;
	}

	private String _body;
	private boolean _custom;

}