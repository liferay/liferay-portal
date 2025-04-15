/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.servlet.taglib;

import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.aui.ButtonTag;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class EditFormButtonsTag extends IncludeTag {

	public String getRedirect() {
		return _redirect;
	}

	public String getSubmitId() {
		return _submitId;
	}

	public String getSubmitLabel() {
		return _submitLabel;
	}

	public String getSubmitOnClick() {
		return _submitOnClick;
	}

	public boolean isSubmitDisabled() {
		return _submitDisabled;
	}

	public void setRedirect(String redirect) {
		_redirect = redirect;
	}

	public void setSubmitDisabled(boolean submitDisabled) {
		_submitDisabled = submitDisabled;
	}

	public void setSubmitId(String submitId) {
		_submitId = submitId;
	}

	public void setSubmitLabel(String submitLabel) {
		_submitLabel = submitLabel;
	}

	public void setSubmitOnClick(String submitOnClick) {
		_submitOnClick = submitOnClick;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_redirect = null;
		_submitDisabled = false;
		_submitId = null;
		_submitLabel = null;
		_submitOnClick = null;
	}

	@Override
	protected int processEndTag() throws Exception {
		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay.isStatePopUp()) {
			_addCancelButton();
		}

		ButtonTag submitButtonTag = new ButtonTag();

		submitButtonTag.setDisabled(isSubmitDisabled());
		submitButtonTag.setId(getSubmitId());
		submitButtonTag.setOnClick(getSubmitOnClick());
		submitButtonTag.setType("submit");
		submitButtonTag.setValue(_getSubmitLabel());

		submitButtonTag.doTag(pageContext);

		if (!themeDisplay.isStatePopUp()) {
			_addCancelButton();
		}

		return EVAL_PAGE;
	}

	private void _addCancelButton() throws Exception {
		ButtonTag cancelButtonTag = new ButtonTag();

		cancelButtonTag.setType("cancel");

		if (Validator.isNotNull(getRedirect())) {
			cancelButtonTag.setHref(getRedirect());
		}

		cancelButtonTag.doTag(pageContext);
	}

	private String _getSubmitLabel() {
		String submitLabel = getSubmitLabel();

		if (Validator.isNotNull(submitLabel)) {
			return submitLabel;
		}

		return "save";
	}

	private String _redirect;
	private boolean _submitDisabled;
	private String _submitId;
	private String _submitLabel;
	private String _submitOnClick;

}