/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.taglib.servlet.taglib;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.taglib.internal.servlet.ServletContextUtil;
import com.liferay.dynamic.data.mapping.taglib.servlet.taglib.base.BaseHTMLTag;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.layout.item.selector.criterion.LayoutItemSelectorCriterion;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Bruno Basto
 */
public class HTMLTag extends BaseHTMLTag {

	@Override
	public int doStartTag() throws JspException {
		if (!getIgnoreRequestValue()) {
			DDMFormValues ddmFormValues = getDDMFormValuesFromRequest();

			if (ddmFormValues != null) {
				setDdmFormValues(ddmFormValues);
			}
		}

		return super.doStartTag();
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	protected DDMForm getDDMForm() {
		try {
			return DDMUtil.getDDMForm(getClassNameId(), getClassPK());
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(getLogMessage(), portalException);
			}
		}

		return null;
	}

	protected DDMFormValues getDDMFormValuesFromRequest() {
		String serializedDDMFormValues = ParamUtil.getString(
			getRequest(), getDDMFormValuesInputName());

		if (Validator.isNull(serializedDDMFormValues)) {
			return null;
		}

		DDMForm ddmForm = getDDMForm();

		try {
			return DDMUtil.getDDMFormValues(ddmForm, serializedDDMFormValues);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return null;
	}

	protected String getDDMFormValuesInputName() {
		String fieldsNamespace = GetterUtil.getString(getFieldsNamespace());

		return fieldsNamespace + "ddmFormValues";
	}

	protected Fields getFields() {
		try {
			long ddmStructureId = getClassPK();

			if (getClassNameId() == PortalUtil.getClassNameId(
					DDMTemplate.class)) {

				DDMTemplate ddmTemplate =
					DDMTemplateLocalServiceUtil.getTemplate(getClassPK());

				ddmStructureId = ddmTemplate.getClassPK();
			}

			if (getDdmFormValues() != null) {
				return DDMUtil.getFields(ddmStructureId, getDdmFormValues());
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(getLogMessage(), portalException);
			}
		}

		return null;
	}

	protected String getLogMessage() {
		if (getClassNameId() == PortalUtil.getClassNameId(DDMTemplate.class)) {
			return "Unable to retrieve DDM template with class PK " +
				getClassPK();
		}

		return "Unable to retrieve DDM structure with class PK " + getClassPK();
	}

	protected String getMode() {
		if (getClassNameId() != PortalUtil.getClassNameId(DDMTemplate.class)) {
			return null;
		}

		try {
			DDMTemplate ddmTemplate = DDMTemplateLocalServiceUtil.getTemplate(
				getClassPK());

			return ddmTemplate.getMode();
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(getLogMessage(), portalException);
			}
		}

		return null;
	}

	@Override
	protected int processEndTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("</div>");

		return EVAL_PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		super.setAttributes(httpServletRequest);

		setNamespacedAttribute(httpServletRequest, "ddmForm", getDDMForm());
		setNamespacedAttribute(
			httpServletRequest, "ddmFormValuesInputName",
			getDDMFormValuesInputName());
		setNamespacedAttribute(httpServletRequest, "fields", getFields());

		if (getGroupId() <= 0) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			setNamespacedAttribute(
				httpServletRequest, "groupId",
				String.valueOf(themeDisplay.getSiteGroupId()));
		}

		setNamespacedAttribute(
			httpServletRequest, "layoutSelectorURL", _getLayoutSelectorURL());
		setNamespacedAttribute(httpServletRequest, "mode", getMode());
	}

	private String _getLayoutSelectorURL() {
		String layoutSelectorURL = getLayoutSelectorURL();

		if (Validator.isNotNull(layoutSelectorURL)) {
			return layoutSelectorURL;
		}

		ItemSelector itemSelector = ServletContextUtil.getItemSelector();

		LayoutItemSelectorCriterion layoutItemSelectorCriterion =
			new LayoutItemSelectorCriterion();

		layoutItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new UUIDItemSelectorReturnType());

		return String.valueOf(
			itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(getRequest()),
				"selectLayout", layoutItemSelectorCriterion));
	}

	private static final Log _log = LogFactoryUtil.getLog(HTMLTag.class);

}