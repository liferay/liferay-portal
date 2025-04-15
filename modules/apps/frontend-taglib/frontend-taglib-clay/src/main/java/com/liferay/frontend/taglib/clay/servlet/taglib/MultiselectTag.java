/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.BaseContainerTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.MultiselectItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.MultiselectLocator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.TagResourceBundleUtil;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Kresimir Coko
 */
public class MultiselectTag extends BaseContainerTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		setClearAllTitle(
			LanguageUtil.get(
				TagResourceBundleUtil.getResourceBundle(pageContext),
				"clear-all"));

		return super.doStartTag();
	}

	public String getClearAllTitle() {
		return _clearAllTitle;
	}

	public String getHelpText() {
		return _helpText;
	}

	public String getInputName() {
		return _inputName;
	}

	public String getInputValue() {
		return _inputValue;
	}

	public String getLabel() {
		return _label;
	}

	public MultiselectLocator getMultiselectLocator() {
		if (_multiselectLocator != null) {
			return _multiselectLocator;
		}

		return new MultiselectLocator();
	}

	public List<MultiselectItem> getSelectedMultiselectItems() {
		return _selectedMultiselectItems;
	}

	public List<MultiselectItem> getSourceMultiselectItems() {
		return _sourceMultiselectItems;
	}

	public boolean isDisabled() {
		return _disabled;
	}

	public boolean isDisabledClearAll() {
		return _disabledClearAll;
	}

	public boolean isValid() {
		return _valid;
	}

	public void setClearAllTitle(String clearAllTitle) {
		_clearAllTitle = clearAllTitle;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setDisabledClearAll(boolean disabledClearAll) {
		_disabledClearAll = disabledClearAll;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	public void setExtractData(Object extractData) {
	}

	public void setHelpText(String helpText) {
		_helpText = helpText;
	}

	public void setInputName(String inputName) {
		_inputName = inputName;
	}

	public void setInputValue(String inputValue) {
		_inputValue = inputValue;
	}

	public void setIsValid(boolean valid) {
		_valid = valid;
	}

	public void setLabel(String label) {
		_label = label;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #setLocator(Object)}
	 */
	@Deprecated
	public void setLabelLocator() {
	}

	public void setMultiselectLocator(MultiselectLocator multiselectLocator) {
		_multiselectLocator = multiselectLocator;
	}

	public void setSelectedMultiselectItems(
		List<MultiselectItem> selectedMultiselectItems) {

		_selectedMultiselectItems = selectedMultiselectItems;
	}

	public void setSourceMultiselectItems(
		List<MultiselectItem> sourceMultiselectItems) {

		_sourceMultiselectItems = sourceMultiselectItems;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #setLocator(Object)}
	 */
	@Deprecated
	public void setValueLocator() {
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_clearAllTitle = null;
		_disabled = false;
		_disabledClearAll = false;
		_helpText = null;
		_inputName = null;
		_inputValue = null;
		_label = null;
		_multiselectLocator = null;
		_selectedMultiselectItems = null;
		_sourceMultiselectItems = null;
		_valid = true;
	}

	@Override
	protected String getHydratedModuleName() {
		return "{Multiselect} from frontend-taglib-clay";
	}

	@Override
	protected Map<String, Object> prepareProps(Map<String, Object> props) {
		props.put("clearAllTitle", _clearAllTitle);
		props.put("disabled", _disabled);
		props.put("disabledClearAll", _disabledClearAll);
		props.put("helpText", _helpText);
		props.put("inputName", _inputName);
		props.put("inputValue", _inputValue);
		props.put("isValid", _valid);
		props.put("label", _label);

		if (_multiselectLocator != null) {
			props.put("multiselectLocator", _multiselectLocator);
		}

		props.put("selectedItems", _selectedMultiselectItems);
		props.put("sourceItems", _sourceMultiselectItems);

		return super.prepareProps(props);
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		cssClasses.add("form-control-inset");

		return super.processCssClasses(cssClasses);
	}

	@Override
	protected int processStartTag() throws Exception {
		super.processStartTag();

		JspWriter jspWriter = pageContext.getOut();

		String id = getId();

		if (_label != null) {
			jspWriter.write("<label");

			if (Validator.isNotNull(id)) {
				jspWriter.write(" for=\"");
				jspWriter.write(id);
				jspWriter.write("\"");
			}

			jspWriter.write(">");
			jspWriter.write(_label);
			jspWriter.write("</label>");
		}

		jspWriter.write("<div class=\"input-group\"><div class=\"");
		jspWriter.write("input-group-item\"><div class=\"form-control ");
		jspWriter.write("form-control-tag-group input-group\"><div class=\"");
		jspWriter.write("input-group-item\">");

		List<MultiselectItem> selectedMultiselectItems =
			getSelectedMultiselectItems();

		if (ListUtil.isNotEmpty(selectedMultiselectItems)) {
			MultiselectLocator multiselectLocator = getMultiselectLocator();

			for (MultiselectItem selectedItem : selectedMultiselectItems) {
				String selectedItemLabel = selectedItem.get(
					multiselectLocator.get("label"));

				LabelTag labelTag = new LabelTag();

				labelTag.setDismissible(true);
				labelTag.setLabel(selectedItemLabel);

				labelTag.doTag(pageContext);

				jspWriter.write("<input type=\"hidden\" name=\"");
				jspWriter.write(selectedItemLabel);
				jspWriter.write("\" ");
				jspWriter.write("value=\"");
				jspWriter.write(
					selectedItem.get(multiselectLocator.get("value")));
				jspWriter.write("\" />");
			}
		}

		jspWriter.write("<input ");

		super.writeCssClassAttribute();

		super.writeDynamicAttributes();

		if (_disabled) {
			jspWriter.write(" disabled");
		}

		if (Validator.isNotNull(id)) {
			super.writeIdAttribute();
		}

		jspWriter.write(" type=\"text\"");

		if (Validator.isNotNull(_inputValue)) {
			jspWriter.write(" value=\"");
			jspWriter.write(_inputValue);
			jspWriter.write("\"");
		}

		jspWriter.write(" /></div>");

		if (!_disabled && !_disabledClearAll &&
			!selectedMultiselectItems.isEmpty()) {

			jspWriter.write("<div class=\"input-group-item ");
			jspWriter.write("input-group-item-shrink\">");

			ButtonTag buttonTag = new ButtonTag();

			buttonTag.setCssClass("component-action");
			buttonTag.setDisplayType("unstyled");
			buttonTag.setIcon("times-circle");

			if (Validator.isNotNull(_clearAllTitle)) {
				String clearAllTitle = LanguageUtil.get(
					TagResourceBundleUtil.getResourceBundle(pageContext),
					_clearAllTitle);

				buttonTag.setDynamicAttribute(
					StringPool.BLANK, "title", HtmlUtil.escape(clearAllTitle));
			}

			buttonTag.doTag(pageContext);

			jspWriter.write("</div>");
		}

		jspWriter.write("</div>");

		if (_helpText != null) {
			jspWriter.write("<div class=\"form-feedback-group\"><div ");
			jspWriter.write("class=\"form-text\">");
			jspWriter.write(_helpText);
			jspWriter.write("</div></div>");
		}

		jspWriter.write("</div></div>");

		return SKIP_BODY;
	}

	@Override
	protected void writeCssClassAttribute() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write(" class=\"form-group\"");
	}

	@Override
	protected void writeDynamicAttributes() {
	}

	@Override
	protected void writeIdAttribute() {
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:multiselect:";

	private String _clearAllTitle;
	private boolean _disabled;
	private boolean _disabledClearAll;
	private String _helpText;
	private String _inputName;
	private String _inputValue;
	private String _label;
	private MultiselectLocator _multiselectLocator;
	private List<MultiselectItem> _selectedMultiselectItems;
	private List<MultiselectItem> _sourceMultiselectItems;
	private boolean _valid = true;

}