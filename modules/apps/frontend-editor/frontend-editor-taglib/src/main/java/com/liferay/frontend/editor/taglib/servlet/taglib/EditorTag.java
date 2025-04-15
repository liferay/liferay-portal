/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.taglib.servlet.taglib;

import com.liferay.frontend.editor.EditorRenderer;
import com.liferay.frontend.editor.taglib.internal.EditorRendererUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.servlet.PortalWebResourcesUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.BaseValidatorTagSupport;
import com.liferay.taglib.aui.AUIUtil;
import com.liferay.taglib.util.TagResourceBundleUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;

import java.io.IOException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Iván Zaera Avellón
 */
public class EditorTag extends BaseValidatorTagSupport {

	@Override
	public int doStartTag() throws JspException {
		EditorRenderer editorRenderer = _getEditorProvider();

		setAttributeNamespace(
			editorRenderer.getAttributeNamespace() + StringPool.COLON);

		return super.doStartTag();
	}

	public String getConfigKey() {
		return _configKey;
	}

	public Map<String, String> getConfigParams() {
		return _configParams;
	}

	public String getContents() {
		return _contents;
	}

	public String getContentsLanguageId() {
		return _contentsLanguageId;
	}

	public String getCssClass() {
		return _cssClass;
	}

	public Map<String, Object> getData() {
		return _data;
	}

	public String getEditorName() {
		return _editorName;
	}

	public Map<String, String> getFileBrowserParams() {
		return _fileBrowserParams;
	}

	public String getHeight() {
		return _height;
	}

	public String getInlineEditSaveURL() {
		return _inlineEditSaveURL;
	}

	@Override
	public String getInputName() {
		return _getConfigKey();
	}

	public String getName() {
		return _name;
	}

	public String getOnBlurMethod() {
		return _onBlurMethod;
	}

	public String getOnChangeMethod() {
		return _onChangeMethod;
	}

	public String getOnFocusMethod() {
		return _onFocusMethod;
	}

	public String getOnInitMethod() {
		return _onInitMethod;
	}

	public String getPlaceholder() {
		return _placeholder;
	}

	public String getResizeDirection() {
		return _resizeDirection;
	}

	public String getToolbarSet() {
		return _toolbarSet;
	}

	public String getWidth() {
		return _width;
	}

	public boolean isAllowBrowseDocuments() {
		return _allowBrowseDocuments;
	}

	public boolean isAutoCreate() {
		return _autoCreate;
	}

	public boolean isInlineEdit() {
		return _inlineEdit;
	}

	public boolean isRequired() {
		return _required;
	}

	public boolean isResizable() {
		return _resizable;
	}

	public boolean isShowSource() {
		return _showSource;
	}

	public boolean isSkipEditorLoading() {
		return _skipEditorLoading;
	}

	public void setAllowBrowseDocuments(boolean allowBrowseDocuments) {
		_allowBrowseDocuments = allowBrowseDocuments;
	}

	public void setAutoCreate(boolean autoCreate) {
		_autoCreate = autoCreate;
	}

	public void setConfigKey(String configKey) {
		_configKey = configKey;
	}

	public void setConfigParams(Map<String, String> configParams) {
		_configParams = configParams;
	}

	public void setContents(String contents) {
		_contents = contents;
	}

	public void setContentsLanguageId(String contentsLanguageId) {
		_contentsLanguageId = contentsLanguageId;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setData(Map<String, Object> data) {
		_data = data;
	}

	public void setEditorName(String editorName) {
		_editorName = editorName;
	}

	public void setFileBrowserParams(Map<String, String> fileBrowserParams) {
		_fileBrowserParams = fileBrowserParams;
	}

	public void setHeight(String height) {
		_height = height;
	}

	public void setInlineEdit(boolean inlineEdit) {
		_inlineEdit = inlineEdit;
	}

	public void setInlineEditSaveURL(String inlineEditSaveURL) {
		_inlineEditSaveURL = inlineEditSaveURL;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setOnBlurMethod(String onBlurMethod) {
		_onBlurMethod = onBlurMethod;
	}

	public void setOnChangeMethod(String onChangeMethod) {
		_onChangeMethod = onChangeMethod;
	}

	public void setOnFocusMethod(String onFocusMethod) {
		_onFocusMethod = onFocusMethod;
	}

	public void setOnInitMethod(String onInitMethod) {
		_onInitMethod = onInitMethod;
	}

	public void setPlaceholder(String placeholder) {
		_placeholder = placeholder;
	}

	public void setRequired(boolean required) {
		_required = required;
	}

	public void setResizable(boolean resizable) {
		_resizable = resizable;
	}

	public void setResizeDirection(String resizeDirection) {
		_resizeDirection = resizeDirection;
	}

	public void setShowSource(boolean showSource) {
		_showSource = showSource;
	}

	public void setSkipEditorLoading(boolean skipEditorLoading) {
		_skipEditorLoading = skipEditorLoading;
	}

	public void setToolbarSet(String toolbarSet) {
		_toolbarSet = toolbarSet;
	}

	public void setWidth(String width) {
		_width = width;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_allowBrowseDocuments = true;
		_autoCreate = true;
		_configKey = null;
		_configParams = null;
		_contents = null;
		_contentsLanguageId = null;
		_cssClass = null;
		_data = null;
		_editorName = null;
		_fileBrowserParams = null;
		_height = null;
		_inlineEdit = false;
		_inlineEditSaveURL = null;
		_name = "editor";
		_onBlurMethod = null;
		_onChangeMethod = null;
		_onFocusMethod = null;
		_onInitMethod = null;
		_placeholder = null;
		_required = false;
		_resizable = true;
		_resizeDirection = "vertical";
		_showSource = true;
		_skipEditorLoading = false;
		_toolbarSet = _TOOLBAR_SET_DEFAULT;
		_width = null;
	}

	@Override
	protected String getPage() {
		EditorRenderer editorRenderer = _getEditorProvider();

		return editorRenderer.getJspPath();
	}

	@Override
	protected void includePage(
			String page, HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		setServletContext(
			PortalWebResourcesUtil.getServletContext(_getEditorResourceType()));

		super.includePage(page, httpServletResponse);
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		setNamespacedAttribute(
			httpServletRequest, "allowBrowseDocuments",
			String.valueOf(_allowBrowseDocuments));
		setNamespacedAttribute(
			httpServletRequest, "autoCreate", String.valueOf(_autoCreate));
		setNamespacedAttribute(
			httpServletRequest, "configParams", _configParams);
		setNamespacedAttribute(httpServletRequest, "contents", _contents);
		setNamespacedAttribute(
			httpServletRequest, "contentsLanguageId", _getContentsLanguageId());
		setNamespacedAttribute(httpServletRequest, "cssClass", _cssClass);
		setNamespacedAttribute(
			httpServletRequest, "cssClasses", _getCssClasses());
		setNamespacedAttribute(
			httpServletRequest, "editorName", _getResolvedEditorName());
		setNamespacedAttribute(
			httpServletRequest, "fileBrowserParams", _fileBrowserParams);
		setNamespacedAttribute(httpServletRequest, "height", _height);
		setNamespacedAttribute(httpServletRequest, "initMethod", "initEditor");
		setNamespacedAttribute(
			httpServletRequest, "inlineEdit", String.valueOf(_inlineEdit));
		setNamespacedAttribute(
			httpServletRequest, "inlineEditSaveURL", _inlineEditSaveURL);
		setNamespacedAttribute(httpServletRequest, "name", _name);
		setNamespacedAttribute(
			httpServletRequest, "onBlurMethod", _onBlurMethod);
		setNamespacedAttribute(
			httpServletRequest, "onChangeMethod", _onChangeMethod);
		setNamespacedAttribute(
			httpServletRequest, "onFocusMethod", _onFocusMethod);
		setNamespacedAttribute(
			httpServletRequest, "onInitMethod", _onInitMethod);

		if (Validator.isNull(_placeholder)) {
			_placeholder = LanguageUtil.get(
				TagResourceBundleUtil.getResourceBundle(pageContext),
				"write-your-content-here");
		}

		setNamespacedAttribute(httpServletRequest, "placeholder", _placeholder);

		setNamespacedAttribute(
			httpServletRequest, "required", String.valueOf(_required));
		setNamespacedAttribute(
			httpServletRequest, "resizable", String.valueOf(_resizable));
		setNamespacedAttribute(
			httpServletRequest, "resizeDirection", _resizeDirection);
		setNamespacedAttribute(
			httpServletRequest, "showSource", String.valueOf(_showSource));
		setNamespacedAttribute(
			httpServletRequest, "skipEditorLoading",
			String.valueOf(_skipEditorLoading));
		setNamespacedAttribute(
			httpServletRequest, "toolbarSet", _getToolbarSet());
		setNamespacedAttribute(httpServletRequest, "width", _width);

		setNamespacedAttribute(
			httpServletRequest, "data",
			ProxyUtil.newProxyInstance(
				ClassLoader.getSystemClassLoader(), new Class<?>[] {Map.class},
				new LazyDataInvocationHandler()));
	}

	private String _getConfigKey() {
		String configKey = _configKey;

		if (Validator.isNull(configKey)) {
			configKey = _name;
		}

		return configKey;
	}

	private String _getContentsLanguageId() {
		if (_contentsLanguageId == null) {
			HttpServletRequest httpServletRequest = getRequest();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_contentsLanguageId = themeDisplay.getLanguageId();
		}

		return _contentsLanguageId;
	}

	private String _getCssClasses() {
		String cssClasses = "portlet ";

		HttpServletRequest httpServletRequest = getRequest();

		Portlet portlet = (Portlet)httpServletRequest.getAttribute(
			WebKeys.RENDER_PORTLET);

		if (portlet != null) {
			cssClasses += portlet.getCssClassWrapper();
		}

		return cssClasses;
	}

	private Map<String, Object> _getData() {
		HttpServletRequest httpServletRequest = getRequest();

		String portletId = GetterUtil.getString(
			(String)httpServletRequest.getAttribute(WebKeys.PORTLET_ID));

		Map<String, Object> attributes = new HashMap<>();

		Enumeration<String> enumeration =
			httpServletRequest.getAttributeNames();

		while (enumeration.hasMoreElements()) {
			String attributeName = enumeration.nextElement();

			if (attributeName.startsWith(getAttributeNamespace())) {
				attributes.put(
					attributeName,
					httpServletRequest.getAttribute(attributeName));
			}
		}

		attributes.put(getAttributeNamespace() + "namespace", _getNamespace());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		EditorConfiguration editorConfiguration =
			EditorConfigurationFactoryUtil.getEditorConfiguration(
				PortletIdCodec.decodePortletName(portletId), _getConfigKey(),
				_getResolvedEditorName(), attributes, themeDisplay,
				_getRequestBackedPortletURLFactory());

		Map<String, Object> data = editorConfiguration.getData();

		if (MapUtil.isNotEmpty(_data)) {
			MapUtil.merge(_data, data);
		}

		return data;
	}

	private EditorRenderer _getEditorProvider() {
		return EditorRendererUtil.getEditorRenderer(_getResolvedEditorName());
	}

	private String _getEditorResourceType() {
		EditorRenderer editorRenderer = _getEditorProvider();

		return editorRenderer.getResourceType();
	}

	private String _getNamespace() {
		HttpServletRequest httpServletRequest = getRequest();

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);
		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		if ((portletRequest == null) || (portletResponse == null)) {
			return AUIUtil.getNamespace(httpServletRequest);
		}

		return AUIUtil.getNamespace(portletRequest, portletResponse);
	}

	private RequestBackedPortletURLFactory
		_getRequestBackedPortletURLFactory() {

		HttpServletRequest httpServletRequest = getRequest();

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		if (portletRequest == null) {
			return RequestBackedPortletURLFactoryUtil.create(
				httpServletRequest);
		}

		return RequestBackedPortletURLFactoryUtil.create(portletRequest);
	}

	private String _getResolvedEditorName() {
		if (Validator.isNull(_editorName)) {
			return _EDITOR_WYSIWYG_DEFAULT;
		}

		EditorRenderer editorRenderer = EditorRendererUtil.getEditorRenderer(
			_editorName);

		if (editorRenderer == null) {
			return _EDITOR_WYSIWYG_DEFAULT;
		}

		return _editorName;
	}

	private String _getToolbarSet() {
		if (Validator.isNotNull(_toolbarSet)) {
			return _toolbarSet;
		}

		return _TOOLBAR_SET_DEFAULT;
	}

	private static final String _EDITOR_WYSIWYG_DEFAULT = PropsUtil.get(
		PropsKeys.EDITOR_WYSIWYG_DEFAULT);

	private static final String _TOOLBAR_SET_DEFAULT = "liferay";

	private boolean _allowBrowseDocuments = true;
	private boolean _autoCreate = true;
	private String _configKey;
	private Map<String, String> _configParams;
	private String _contents;
	private String _contentsLanguageId;
	private String _cssClass;
	private Map<String, Object> _data;
	private String _editorName;
	private Map<String, String> _fileBrowserParams;
	private String _height;
	private boolean _inlineEdit;
	private String _inlineEditSaveURL;
	private String _name = "editor";
	private String _onBlurMethod;
	private String _onChangeMethod;
	private String _onFocusMethod;
	private String _onInitMethod;
	private String _placeholder;
	private boolean _required;
	private boolean _resizable = true;
	private String _resizeDirection = "vertical";
	private boolean _showSource = true;
	private boolean _skipEditorLoading;
	private String _toolbarSet = _TOOLBAR_SET_DEFAULT;
	private String _width;

	private class LazyDataInvocationHandler implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws ReflectiveOperationException {

			if (_data == null) {
				_data = _getData();
			}

			return method.invoke(_data, args);
		}

		private Map<String, Object> _data;

	}

}