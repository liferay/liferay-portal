/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class InputMoveBoxesTag extends IncludeTag {

	public String getCssClass() {
		return _cssClass;
	}

	public int getLeftBoxMaxItems() {
		return _leftBoxMaxItems;
	}

	public String getLeftBoxName() {
		return _leftBoxName;
	}

	public List<KeyValuePair> getLeftList() {
		return _leftList;
	}

	public String getLeftOnChange() {
		return _leftOnChange;
	}

	public String getLeftReorder() {
		return _leftReorder;
	}

	public String getLeftTitle() {
		return _leftTitle;
	}

	public int getRightBoxMaxItems() {
		return _rightBoxMaxItems;
	}

	public String getRightBoxName() {
		return _rightBoxName;
	}

	public List<KeyValuePair> getRightList() {
		return _rightList;
	}

	public String getRightOnChange() {
		return _rightOnChange;
	}

	public String getRightReorder() {
		return _rightReorder;
	}

	public String getRightTitle() {
		return _rightTitle;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setLeftBoxMaxItems(int leftBoxMaxItems) {
		_leftBoxMaxItems = leftBoxMaxItems;
	}

	public void setLeftBoxName(String leftBoxName) {
		_leftBoxName = leftBoxName;
	}

	public void setLeftList(List<KeyValuePair> leftList) {
		_leftList = leftList;
	}

	public void setLeftOnChange(String leftOnChange) {
		_leftOnChange = leftOnChange;
	}

	public void setLeftReorder(String leftReorder) {
		_leftReorder = leftReorder;
	}

	public void setLeftTitle(String leftTitle) {
		_leftTitle = leftTitle;
	}

	public void setRightBoxMaxItems(int rightBoxMaxItems) {
		_rightBoxMaxItems = rightBoxMaxItems;
	}

	public void setRightBoxName(String rightBoxName) {
		_rightBoxName = rightBoxName;
	}

	public void setRightList(List<KeyValuePair> rightList) {
		_rightList = rightList;
	}

	public void setRightOnChange(String rightOnChange) {
		_rightOnChange = rightOnChange;
	}

	public void setRightReorder(String rightReorder) {
		_rightReorder = rightReorder;
	}

	public void setRightTitle(String rightTitle) {
		_rightTitle = rightTitle;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_cssClass = null;
		_leftBoxMaxItems = null;
		_leftBoxName = null;
		_leftList = null;
		_leftOnChange = null;
		_leftReorder = null;
		_leftTitle = null;
		_rightBoxMaxItems = null;
		_rightBoxName = null;
		_rightList = null;
		_rightOnChange = null;
		_rightReorder = null;
		_rightTitle = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-ui:input-move-boxes:cssClass", _cssClass);
		httpServletRequest.setAttribute(
			"liferay-ui:input-move-boxes:leftBoxMaxItems", _leftBoxMaxItems);
		httpServletRequest.setAttribute(
			"liferay-ui:input-move-boxes:leftBoxName", _leftBoxName);
		httpServletRequest.setAttribute(
			"liferay-ui:input-move-boxes:leftList", _leftList);
		httpServletRequest.setAttribute(
			"liferay-ui:input-move-boxes:leftOnChange", _leftOnChange);
		httpServletRequest.setAttribute(
			"liferay-ui:input-move-boxes:leftReorder", _leftReorder);
		httpServletRequest.setAttribute(
			"liferay-ui:input-move-boxes:leftTitle", _leftTitle);
		httpServletRequest.setAttribute(
			"liferay-ui:input-move-boxes:rightBoxMaxItems", _rightBoxMaxItems);
		httpServletRequest.setAttribute(
			"liferay-ui:input-move-boxes:rightBoxName", _rightBoxName);
		httpServletRequest.setAttribute(
			"liferay-ui:input-move-boxes:rightList", _rightList);
		httpServletRequest.setAttribute(
			"liferay-ui:input-move-boxes:rightOnChange", _rightOnChange);
		httpServletRequest.setAttribute(
			"liferay-ui:input-move-boxes:rightReorder", _rightReorder);
		httpServletRequest.setAttribute(
			"liferay-ui:input-move-boxes:rightTitle", _rightTitle);
	}

	private static final String _PAGE =
		"/html/taglib/ui/input_move_boxes/page.jsp";

	private String _cssClass;
	private Integer _leftBoxMaxItems;
	private String _leftBoxName;
	private List<KeyValuePair> _leftList;
	private String _leftOnChange;
	private String _leftReorder;
	private String _leftTitle;
	private Integer _rightBoxMaxItems;
	private String _rightBoxName;
	private List<KeyValuePair> _rightList;
	private String _rightOnChange;
	private String _rightReorder;
	private String _rightTitle;

}