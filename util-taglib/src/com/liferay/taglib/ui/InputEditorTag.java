/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.editor.Editor;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
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

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Brian Wing Shun Chan
 */
public class InputEditorTag extends BaseValidatorTagSupport {

	public static Editor getEditor(
		HttpServletRequest httpServletRequest, String editorName) {

		if (Validator.isNull(editorName) ||
			!_serviceTrackerMap.containsKey(editorName)) {

			return _serviceTrackerMap.getService(_EDITOR_WYSIWYG_DEFAULT);
		}

		return _serviceTrackerMap.getService(editorName);
	}

	public Map<String, String> getConfigParams() {
		return _configParams;
	}

	public String getContents() {
		return _contents;
	}

	public String getCssClass() {
		return _cssClass;
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
		return getConfigKey();
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

	/**
	 * @deprecated As of Wilberforce (7.0.x), replaced by {@link
	 *             #setEditorName(String)}
	 */
	@Deprecated
	public void setEditorImpl(String editorImpl) {
		_editorName = PropsUtil.get(editorImpl);
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

	/**
	 * @deprecated As of Wilberforce (7.0.x), replaced by {@link
	 *             #setContents(String)}
	 */
	@Deprecated
	public void setInitMethod(String initMethod) {
		_initMethod = initMethod;
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
		_initMethod = "initEditor";
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
		_showSource = true;
		_skipEditorLoading = false;
		_toolbarSet = _TOOLBAR_SET_DEFAULT;
		_width = null;
	}

	protected String getConfigKey() {
		String configKey = _configKey;

		if (Validator.isNull(configKey)) {
			configKey = _name;
		}

		return configKey;
	}

	protected String getContentsLanguageId() {
		if (_contentsLanguageId == null) {
			HttpServletRequest httpServletRequest = getRequest();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_contentsLanguageId = themeDisplay.getLanguageId();
		}

		return _contentsLanguageId;
	}

	protected String getCssClasses() {
		String cssClasses = "portlet ";

		HttpServletRequest httpServletRequest = getRequest();

		Portlet portlet = (Portlet)httpServletRequest.getAttribute(
			WebKeys.RENDER_PORTLET);

		if (portlet != null) {
			cssClasses += portlet.getCssClassWrapper();
		}

		return cssClasses;
	}

	protected Map<String, Object> getData() {
		HttpServletRequest httpServletRequest = getRequest();

		String portletId = GetterUtil.getString(
			(String)httpServletRequest.getAttribute(WebKeys.PORTLET_ID));

		Map<String, Object> attributes = new HashMap<>();

		Enumeration<String> enumeration =
			httpServletRequest.getAttributeNames();

		while (enumeration.hasMoreElements()) {
			String attributeName = enumeration.nextElement();

			if (attributeName.startsWith("liferay-ui:input-editor")) {
				attributes.put(
					attributeName,
					httpServletRequest.getAttribute(attributeName));
			}
		}

		attributes.put("liferay-ui:input-editor:namespace", getNamespace());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		EditorConfiguration editorConfiguration =
			EditorConfigurationFactoryUtil.getEditorConfiguration(
				PortletIdCodec.decodePortletName(portletId), getConfigKey(),
				getEditorName(httpServletRequest), attributes, themeDisplay,
				getRequestBackedPortletURLFactory());

		Map<String, Object> data = editorConfiguration.getData();

		if (MapUtil.isNotEmpty(_data)) {
			MapUtil.merge(_data, data);
		}

		return data;
	}

	protected Editor getEditor(HttpServletRequest httpServletRequest) {
		return getEditor(httpServletRequest, _editorName);
	}

	protected String getEditorName(HttpServletRequest httpServletRequest) {
		Editor editor = getEditor(httpServletRequest);

		return editor.getName();
	}

	protected String getEditorResourceType() {
		Editor editor = getEditor(getRequest());

		return editor.getResourceType();
	}

	protected String getNamespace() {
		HttpServletRequest httpServletRequest = getRequest();

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);
		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		if ((portletRequest == null) || (portletResponse == null)) {
			return AUIUtil.getNamespace(httpServletRequest);
		}

		return AUIUtil.getNamespace(portletRequest, portletResponse);
	}

	@Override
	protected String getPage() {
		Editor editor = getEditor(getRequest());

		return editor.getJspPath();
	}

	protected RequestBackedPortletURLFactory
		getRequestBackedPortletURLFactory() {

		HttpServletRequest httpServletRequest = getRequest();

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);

		if (portletRequest == null) {
			return RequestBackedPortletURLFactoryUtil.create(
				httpServletRequest);
		}

		return RequestBackedPortletURLFactoryUtil.create(portletRequest);
	}

	protected String getToolbarSet() {
		if (Validator.isNotNull(_toolbarSet)) {
			return _toolbarSet;
		}

		return _TOOLBAR_SET_DEFAULT;
	}

	@Override
	protected void includePage(
			String page, HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		setServletContext(
			PortalWebResourcesUtil.getServletContext(getEditorResourceType()));

		super.includePage(page, httpServletResponse);
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:allowBrowseDocuments",
			String.valueOf(_allowBrowseDocuments));
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:autoCreate", String.valueOf(_autoCreate));
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:configParams", _configParams);
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:contents", _contents);
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:contentsLanguageId",
			getContentsLanguageId());
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:cssClass", _cssClass);
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:cssClasses", getCssClasses());
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:data",
			_mapProxyProviderFunction.apply(new LazyDataInvocationHandler()));
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:editorName",
			getEditorName(httpServletRequest));
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:fileBrowserParams", _fileBrowserParams);
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:height", _height);
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:initMethod", _initMethod);
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:inlineEdit", String.valueOf(_inlineEdit));
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:inlineEditSaveURL", _inlineEditSaveURL);
		httpServletRequest.setAttribute("liferay-ui:input-editor:name", _name);
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:onBlurMethod", _onBlurMethod);
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:onChangeMethod", _onChangeMethod);
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:onFocusMethod", _onFocusMethod);
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:onInitMethod", _onInitMethod);
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:placeholder", _placeholder);
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:required", String.valueOf(_required));
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:resizable", String.valueOf(_resizable));
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:showSource", String.valueOf(_showSource));
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:skipEditorLoading",
			String.valueOf(_skipEditorLoading));
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:toolbarSet", getToolbarSet());
		httpServletRequest.setAttribute(
			"liferay-ui:input-editor:width", _width);
	}

	private static final String _EDITOR_WYSIWYG_DEFAULT = PropsUtil.get(
		PropsKeys.EDITOR_WYSIWYG_DEFAULT);

	private static final String _TOOLBAR_SET_DEFAULT = "liferay";

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static final Function<InvocationHandler, Map<?, ?>>
		_mapProxyProviderFunction = ProxyUtil.getProxyProviderFunction(
			Map.class);

	private static final ServiceTrackerMap<String, Editor> _serviceTrackerMap =
		ServiceTrackerMapFactory.openSingleValueMap(
			_bundleContext, Editor.class, null,
			new ServiceReferenceMapper<String, Editor>() {

				@Override
				public void map(
					ServiceReference<Editor> serviceReference,
					Emitter<String> emitter) {

					Editor editor = _bundleContext.getService(serviceReference);

					emitter.emit(editor.getName());
				}

			});

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
	private String _initMethod = "initEditor";
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
	private boolean _showSource = true;
	private boolean _skipEditorLoading;
	private String _toolbarSet = _TOOLBAR_SET_DEFAULT;
	private String _width;

	private class LazyDataInvocationHandler implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws ReflectiveOperationException {

			if (_data == null) {
				_data = getData();
			}

			return method.invoke(_data, args);
		}

		private Map<String, Object> _data;

	}

}