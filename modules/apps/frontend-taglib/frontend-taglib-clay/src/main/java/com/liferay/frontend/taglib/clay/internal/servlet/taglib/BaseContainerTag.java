/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.internal.servlet.taglib;

import com.liferay.frontend.js.loader.modules.extender.esm.ESImportUtil;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolvedPackageNameUtil;
import com.liferay.frontend.taglib.clay.internal.servlet.ServletContextUtil;
import com.liferay.frontend.taglib.clay.internal.servlet.taglib.util.ServicesProvider;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;
import com.liferay.taglib.util.AttributesTagSupport;
import com.liferay.taglib.util.InlineUtil;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Chema Balsas
 */
public class BaseContainerTag extends AttributesTagSupport {

	@Override
	public int doEndTag() throws JspException {
		try {
			if (_hasBodyContent()) {
				processEndBodyTag();
			}

			return processEndTag();
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
		finally {
			doClearTag();
		}
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			_tagAction = processStartTag();

			if (_hasBodyContent()) {
				processStartBodyTag();
			}

			return _tagAction;
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
	}

	public Map<String, Object> getAdditionalProps() {
		return _additionalProps;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getCssClass()}
	 */
	@Deprecated
	public String getClassName() {
		return getCssClass();
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public String getComponentId() {
		return _componentId;
	}

	public String getContainerElement() {
		return _containerElement;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public String getContributorKey() {
		return _contributorKey;
	}

	public String getCssClass() {
		return _cssClass;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public Map<String, String> getData() {
		return _data;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public String getDefaultEventHandler() {
		return _defaultEventHandler;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getCssClass()}
	 */
	@Deprecated
	public String getElementClasses() {
		return getCssClass();
	}

	public String getHydratedContainerElement() {
		return _hydratedContainerElement;
	}

	public String getId() {
		return _id;
	}

	public String getNamespace() {
		if (_namespace != null) {
			return _namespace;
		}

		HttpServletRequest httpServletRequest = getRequest();

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		if (portletResponse != null) {
			_namespace = portletResponse.getNamespace();
		}

		return _namespace;
	}

	public String getPropsTransformer() {
		return _propsTransformer;
	}

	public void setAdditionalProps(Map<String, Object> additionalProps) {
		_additionalProps = additionalProps;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #setCssClass(String)}
	 */
	@Deprecated
	public void setClassName(String className) {
		setCssClass(className);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public void setComponentId(String componentId) {
		_componentId = componentId;
	}

	public void setContainerElement(String containerElement) {
		_containerElement = containerElement;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public void setContributorKey(String contributorKey) {
		_contributorKey = contributorKey;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public void setData(Map<String, String> data) {
		_data = data;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public void setDefaultEventHandler(String defaultEventHandler) {
		_defaultEventHandler = defaultEventHandler;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #setCssClass(String)}
	 */
	@Deprecated
	public void setElementClasses(String elementClasses) {
		setCssClass(elementClasses);
	}

	public void setHydratedContainerElement(String hydratedContainerElement) {
		_hydratedContainerElement = hydratedContainerElement;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setNamespace(String namespace) {
		_namespace = namespace;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setPropsTransformer(String propsTransformer) {
		_propsTransformer = propsTransformer;
	}

	public void setPropsTransformerServletContext(
		ServletContext propsTransformerServletContext) {

		_propsTransformerServletContext = propsTransformerServletContext;
	}

	protected void cleanUp() {
		_additionalProps = null;
		_componentId = null;
		_containerElement = null;
		_contributorKey = null;
		_cssClass = null;
		_data = null;
		_defaultEventHandler = null;
		_elementClasses = null;
		_hydratedContainerElement = "div";
		_id = null;
		_namespace = null;
		_propsTransformer = null;
		_propsTransformerServletContext = null;
		_tagAction = EVAL_BODY_INCLUDE;
	}

	protected void doClearTag() {
		clearDynamicAttributes();
		clearParams();
		clearProperties();

		cleanUp();
	}

	protected String getHydratedModuleName() {
		return null;
	}

	protected ServletContext getPropsTransformerServletContext() {
		if (_propsTransformerServletContext != null) {
			return _propsTransformerServletContext;
		}

		return pageContext.getServletContext();
	}

	protected Map<String, Object> prepareProps(Map<String, Object> props) {
		if (_additionalProps != null) {
			props.put("additionalProps", _additionalProps);
		}

		props.put("cssClass", getCssClass());

		String defaultEventHandler = getDefaultEventHandler();

		if (Validator.isNotNull(defaultEventHandler)) {
			props.put("defaultEventHandler", defaultEventHandler);
		}

		props.put("hasBodyContent", _hasBodyContent());
		props.put("id", getId());

		props.putAll(getDynamicAttributes());

		return props;
	}

	protected String processBodyCssClasses(Set<String> cssClasses) {
		cssClasses.add("tag-body-content");

		return StringUtil.merge(cssClasses, StringPool.SPACE);
	}

	protected String processCssClasses(Set<String> cssClasses) {
		String cssClass = getCssClass();

		if (Validator.isNotNull(cssClass)) {
			cssClasses.addAll(StringUtil.split(cssClass, CharPool.SPACE));
		}

		return StringUtil.merge(cssClasses, StringPool.SPACE);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #prepareProps()}
	 */
	@Deprecated
	protected Map<String, Object> processData(Map<String, Object> data) {
		return data;
	}

	protected void processEndBodyTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("</div>");
	}

	protected int processEndTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("</");
		jspWriter.write(_containerElement);
		jspWriter.write(">");

		String hydratedModuleName = getHydratedModuleName();

		if (hydratedModuleName != null) {
			String propsTransformer = null;

			if (Validator.isNotNull(_propsTransformer)) {
				if (ESImportUtil.isESImport(_propsTransformer)) {
					propsTransformer = _propsTransformer;
				}
				else {
					String resolvedPackageName = NPMResolvedPackageNameUtil.get(
						getPropsTransformerServletContext());

					propsTransformer =
						resolvedPackageName + "/" + _propsTransformer;
				}
			}
			else if (Validator.isNotNull(getDefaultEventHandler())) {
				propsTransformer =
					"{DefaultEventHandlersPropsTransformer} from " +
						"frontend-taglib-clay";
			}

			ComponentDescriptor componentDescriptor = new ComponentDescriptor(
				hydratedModuleName, getId(), new LinkedHashSet<>(),
				_isPositionInLine(), propsTransformer);

			ReactRenderer reactRenderer = ServicesProvider.getReactRenderer();

			reactRenderer.renderReact(
				componentDescriptor, prepareProps(new HashMap<>()),
				getRequest(), jspWriter);

			jspWriter.write("</");
			jspWriter.write(_hydratedContainerElement);
			jspWriter.write(">");
		}

		return EVAL_PAGE;
	}

	protected void processStartBodyTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("<div ");

		writeBodyCssClassAttribute();

		jspWriter.write(">");
	}

	protected int processStartTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		if (getHydratedModuleName() != null) {
			jspWriter.write("<");
			jspWriter.write(_hydratedContainerElement);
			jspWriter.write(">");
		}

		if (_containerElement == null) {
			setContainerElement("div");
		}

		jspWriter.write("<");
		jspWriter.write(_containerElement);

		writeCssClassAttribute();

		if (Validator.isNotNull(getId())) {
			writeIdAttribute();
		}

		writeDynamicAttributes();

		jspWriter.write(">");

		return EVAL_BODY_INCLUDE;
	}

	protected void writeBodyCssClassAttribute() throws Exception {
		_writeCssClassAttribute(processBodyCssClasses(new LinkedHashSet<>()));
	}

	protected void writeCssClassAttribute() throws Exception {
		_writeCssClassAttribute(processCssClasses(new LinkedHashSet<>()));
	}

	protected void writeDynamicAttributes() throws Exception {
		Map<String, Object> escapedDynamicAttributes = new HashMap<>();

		Map<String, Object> dynamicAttributes = getDynamicAttributes();

		for (Map.Entry<String, Object> entry : dynamicAttributes.entrySet()) {
			if (entry.getValue() instanceof String) {
				escapedDynamicAttributes.put(
					entry.getKey(),
					HtmlUtil.escapeAttribute((String)entry.getValue()));
			}
			else {
				escapedDynamicAttributes.put(entry.getKey(), entry.getValue());
			}
		}

		String dynamicAttributesString = InlineUtil.buildDynamicAttributes(
			escapedDynamicAttributes);

		if (!dynamicAttributesString.isEmpty()) {
			JspWriter jspWriter = pageContext.getOut();

			jspWriter.write(StringPool.SPACE);
			jspWriter.write(dynamicAttributesString);
		}
	}

	protected void writeIdAttribute() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write(" id=\"");
		jspWriter.write(getId());
		jspWriter.write("\"");
	}

	private boolean _hasBodyContent() {
		if ((_tagAction == EVAL_BODY_INCLUDE) &&
			(getHydratedModuleName() != null)) {

			return true;
		}

		return false;
	}

	private boolean _isPositionInLine() {
		HttpServletRequest httpServletRequest = getRequest();

		String fragmentId = ParamUtil.getString(httpServletRequest, "p_f_id");

		if (Validator.isNotNull(fragmentId)) {
			return true;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay.isIsolated() || themeDisplay.isLifecycleResource() ||
			themeDisplay.isStateExclusive()) {

			return true;
		}

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String portletId = portletDisplay.getId();

		if (Validator.isNotNull(portletId) &&
			themeDisplay.isPortletEmbedded(
				themeDisplay.getScopeGroupId(), themeDisplay.getLayout(),
				portletId)) {

			return true;
		}

		return false;
	}

	private void _writeCssClassAttribute(String cssClasses) throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write(" class=\"");
		jspWriter.write(cssClasses);
		jspWriter.write("\"");
	}

	private Map<String, Object> _additionalProps;
	private String _componentId;
	private String _containerElement;
	private String _contributorKey;
	private String _cssClass;
	private Map<String, String> _data;
	private String _defaultEventHandler;
	private String _elementClasses;
	private String _hydratedContainerElement = "div";
	private String _id;
	private String _namespace;
	private String _propsTransformer;
	private ServletContext _propsTransformerServletContext;
	private int _tagAction = EVAL_BODY_INCLUDE;

}