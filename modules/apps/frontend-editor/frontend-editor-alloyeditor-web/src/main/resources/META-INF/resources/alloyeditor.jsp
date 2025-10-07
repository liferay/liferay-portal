<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String portletId = portletDisplay.getId();

boolean autoCreate = GetterUtil.getBoolean((String)request.getAttribute(AlloyEditorConstants.ATTRIBUTE_NAMESPACE + ":autoCreate"));
String contents = GetterUtil.getString((String)request.getAttribute(AlloyEditorConstants.ATTRIBUTE_NAMESPACE + ":contents"));
String contentsLanguageId = (String)request.getAttribute(AlloyEditorConstants.ATTRIBUTE_NAMESPACE + ":contentsLanguageId");
String cssClass = GetterUtil.getString((String)request.getAttribute(AlloyEditorConstants.ATTRIBUTE_NAMESPACE + ":cssClass"));
Map<String, Object> data = (Map<String, Object>)request.getAttribute(AlloyEditorConstants.ATTRIBUTE_NAMESPACE + ":data");
String editorName = (String)request.getAttribute(AlloyEditorConstants.ATTRIBUTE_NAMESPACE + ":editorName");
String initMethod = (String)request.getAttribute(AlloyEditorConstants.ATTRIBUTE_NAMESPACE + ":initMethod");
String name = namespace + GetterUtil.getString((String)request.getAttribute(AlloyEditorConstants.ATTRIBUTE_NAMESPACE + ":name"));
String onBlurMethod = (String)request.getAttribute(AlloyEditorConstants.ATTRIBUTE_NAMESPACE + ":onBlurMethod");
String onChangeMethod = (String)request.getAttribute(AlloyEditorConstants.ATTRIBUTE_NAMESPACE + ":onChangeMethod");
String onFocusMethod = (String)request.getAttribute(AlloyEditorConstants.ATTRIBUTE_NAMESPACE + ":onFocusMethod");
String onInitMethod = (String)request.getAttribute(AlloyEditorConstants.ATTRIBUTE_NAMESPACE + ":onInitMethod");
String placeholder = GetterUtil.getString((String)request.getAttribute(AlloyEditorConstants.ATTRIBUTE_NAMESPACE + ":placeholder"));
String required = (String)request.getAttribute(AlloyEditorConstants.ATTRIBUTE_NAMESPACE + ":required");
boolean showSource = GetterUtil.getBoolean((String)request.getAttribute(AlloyEditorConstants.ATTRIBUTE_NAMESPACE + ":showSource"));
boolean skipEditorLoading = GetterUtil.getBoolean((String)request.getAttribute(AlloyEditorConstants.ATTRIBUTE_NAMESPACE + ":skipEditorLoading"));

if (Validator.isNull(placeholder)) {
	placeholder = "write-your-content-here";
}

JSONObject editorConfigJSONObject = null;

if (data != null) {
	editorConfigJSONObject = (JSONObject)data.get("editorConfig");
}

EditorOptions editorOptions = null;

if (data != null) {
	editorOptions = (EditorOptions)data.get("editorOptions");
}

Map<String, Object> editorOptionsDynamicAttributes = null;

if (editorOptions != null) {
	editorOptionsDynamicAttributes = editorOptions.getDynamicAttributes();
}
%>

<c:if test="<%= !skipEditorLoading %>">
	<liferay-editor:resources
		editorName="<%= editorName %>"
	/>
</c:if>

<aui:script senna="temporary" type="text/javascript">
	CKEDITOR.ADDITIONAL_RESOURCE_PARAMS = {
		languageId: themeDisplay.getLanguageId(),
	};

	CKEDITOR.disableAutoInline = true;

	CKEDITOR.dtd.$removeEmpty.i = 0;
	CKEDITOR.dtd.$removeEmpty.span = 0;

	CKEDITOR.env.isCompatible = true;
</aui:script>

<liferay-util:html-top>
	<aui:link hashedFile="<%= true %>" href="frontend-editor-alloyeditor-web/css/main.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<liferay-util:buffer
	var="alloyEditor"
>
	<div class="alloy-editor <%= HtmlUtil.escapeAttribute(cssClass) %>" contenteditable="false" data-placeholder="<%= LanguageUtil.get(request, placeholder) %>" data-required="<%= required %>" id="<%= HtmlUtil.escapeAttribute(name) %>" name="<%= HtmlUtil.escapeAttribute(name) %>"></div>

	<div class="alloy-editor-placeholder <%= HtmlUtil.escapeAttribute(cssClass) %>">
		<liferay-ui:message key="<%= placeholder %>" />

		<c:if test="<%= Boolean.parseBoolean(required) %>">
			<clay:icon
				cssClass="reference-mark text-warning"
				symbol="asterisk"
			/>

			<span class="hide-accessible sr-only"><liferay-ui:message key="required" /></span>
		</c:if>
	</div>

	<aui:icon cssClass="alloy-editor-icon" image="text-editor" markupView="lexicon" />
</liferay-util:buffer>

<liferay-util:buffer
	var="editor"
>
	<c:choose>
		<c:when test="<%= showSource %>">
			<div class="alloy-editor-wrapper" id="<%= HtmlUtil.escapeAttribute(name) %>Wrapper">
				<div class="wrapper">
					<%= alloyEditor %>

					<div id="<%= HtmlUtil.escapeAttribute(name) %>Source">
						<div class="lfr-source-editor-code"></div>
					</div>
				</div>
			</div>

			<div class="alloy-editor-switch hide">
				<button class="btn btn-secondary btn-sm hide lfr-portal-tooltip" data-title="<%= LanguageUtil.get(resourceBundle, "fullscreen") %>" id="<%= HtmlUtil.escapeAttribute(name) %>Fullscreen" type="button">
					<aui:icon image="expand" markupView="lexicon" />
				</button>

				<button class="btn btn-secondary btn-sm hide lfr-portal-tooltip" data-title="<%= LanguageUtil.get(resourceBundle, "dark-theme") %>" id="<%= HtmlUtil.escapeAttribute(name) %>SwitchTheme" type="button">
					<aui:icon image="moon" markupView="lexicon" />
				</button>

				<button class="btn btn-secondary btn-sm editor-view lfr-portal-tooltip" data-title="<%= LanguageUtil.get(resourceBundle, "code-view") %>" id="<%= HtmlUtil.escapeAttribute(name) %>Switch" type="button">
					<aui:icon image="code" markupView="lexicon" />
				</button>
			</div>
		</c:when>
		<c:otherwise>
			<%= alloyEditor %>
		</c:otherwise>
	</c:choose>
</liferay-util:buffer>

<div class="alloy-editor-container" id="<%= HtmlUtil.escapeAttribute(name) %>Container">
	<c:if test="<%= autoCreate %>">
		<%= editor %>
	</c:if>
</div>

<%
String modules = "liferay-alloy-editor";

if (showSource) {
	modules += ",liferay-alloy-editor-source";
}

name = HtmlUtil.escapeJS(name);
%>

<aui:script use="<%= modules %>">
	var windowNode = A.getWin();

	<%
	Locale contentsLocale = LocaleUtil.fromLanguageId(contentsLanguageId);

	contentsLanguageId = LocaleUtil.toLanguageId(contentsLocale);
	%>

	var alloyEditor;

	var documentBrowseLinkCallback = function (editor, linkHref, callback) {
		Liferay.Util.openSelectionModal({
			onSelect: function (selectedItem) {
				if (selectedItem) {
					callback(selectedItem);
				}
			},
			selectEventName: editor.name + 'selectDocument',
			title: '<liferay-ui:message key="select-item" />',
			url: linkHref,
		});
	};

	var getInitialContent = function () {
		var data;

		if (window['<%= HtmlUtil.escapeJS(namespace + initMethod) %>']) {
			data = <%= HtmlUtil.escapeJS(namespace + initMethod) %>();
		}
		else {
			data =
				'<%= (contents != null) ? HtmlUtil.escapeJS(contents) : StringPool.BLANK %>';
		}

		return data;
	};

	var createInstance = function () {
		var editorContainer = A.one('#<%= name %>Container');
		var editorNode = A.one('#<%= name %>');

		if (!editorNode) {
			editorContainer.setHTML('');

			editorNode = A.Node.create('<%= HtmlUtil.escapeJS(editor) %>');

			editorContainer.appendChild(editorNode);
		}

		if (editorNode) {
			editorNode.attr('contenteditable', true);
		}

		function initInstance(editorConfig) {
			var plugins = [];

			<c:if test="<%= showSource %>">
				plugins.push(A.Plugin.LiferayAlloyEditorSource);
			</c:if>

			alloyEditor = new A.LiferayAlloyEditor({
				contents: '<%= HtmlUtil.escapeJS(contents) %>',
				editorConfig: editorConfig,
				editorPaths: [
					'<%= PortalWebResourcesUtil.getContextPath(PortalWebResourceConstants.RESOURCE_TYPE_EDITOR_ALLOYEDITOR) %>',
					'<%= PortalWebResourcesUtil.getContextPath(PortalWebResourceConstants.RESOURCE_TYPE_EDITOR_CKEDITOR) %>',
				],
				namespace: '<%= name %>',

				<c:if test="<%= Validator.isNotNull(onBlurMethod) %>">
					onBlurMethod: '<%= HtmlUtil.escapeJS(namespace + onBlurMethod) %>',
				</c:if>

				<c:if test="<%= Validator.isNotNull(onChangeMethod) %>">
					onChangeMethod:
						'<%= HtmlUtil.escapeJS(namespace + onChangeMethod) %>',
				</c:if>

				<c:if test="<%= Validator.isNotNull(onFocusMethod) %>">
					onFocusMethod:
						'<%= HtmlUtil.escapeJS(namespace + onFocusMethod) %>',
				</c:if>

				<c:if test="<%= Validator.isNotNull(onInitMethod) %>">
					onInitMethod: '<%= HtmlUtil.escapeJS(namespace + onInitMethod) %>',
				</c:if>

				plugins: plugins,
				portletId: '<%= portletId %>',
				textMode:
					<%= (editorOptions != null) ? editorOptions.isTextMode() : Boolean.FALSE.toString() %>,

				useCustomDataProcessor:
					<%= (editorOptionsDynamicAttributes != null) && GetterUtil.getBoolean(editorOptionsDynamicAttributes.get("useCustomDataProcessor")) %>,
			}).render();

			CKEDITOR.dom.selection.prototype.selectElement = function (element) {
				this.isLocked = 0;

				var range = new CKEDITOR.dom.range(this.root);

				range.setEndAfter(element);
				range.setStartBefore(element);

				this.selectRanges([range]);
			};

			<liferay-util:dynamic-include key='<%= "com.liferay.frontend.editor.alloyeditor.web#" + editorName + "#onEditorCreate" %>' />

			Liferay.namespace('EDITORS').alloyEditor.addInstance();
		}

		var editorConfig = <%= Validator.isNotNull(editorConfigJSONObject) %>
			? <%= editorConfigJSONObject %>
			: {};

		if (editorConfig.extraPlugins) {
			editorConfig.extraPlugins = A.Array.filter(
				editorConfig.extraPlugins.split(','),
				(item) => {
					return item !== 'ae_embed';
				}
			).join(',');
		}

		editorConfig.removePlugins = editorConfig.removePlugins
			? editorConfig.removePlugins + ',ae_embed'
			: 'ae_embed';

		var uiNode =
			Liferay.Util.getOpener() !== window.self
				? document.querySelector('#main-content')
				: null;

		editorConfig = A.merge(
			{
				documentBrowseLinkCallback: documentBrowseLinkCallback,
				htmlEncodeOutput: true,
				spritemap: '<%= themeDisplay.getPathThemeSpritemap() %>',
				title: false,
				uiNode: uiNode,
			},
			editorConfig
		);

		var editorTransformerURLs = editorConfig.editorTransformerURLs;

		if (editorTransformerURLs) {
			var loadingIndicator = document.createElement('span');

			loadingIndicator.classList.add('loading-animation');
			loadingIndicator.setAttribute('aria-hidden', true);

			editorContainer.appendChild(loadingIndicator);

			Liferay.Util.loadEditorClientExtensions({
				config: editorConfig,
				onLoad: ({transformedConfig}) => {
					if (loadingIndicator) {
						loadingIndicator.remove();
					}

					initInstance(transformedConfig);
				},
			});
		}
		else {
			initInstance(editorConfig);
		}
	};

	var ignoreClass = ['ddm-options-target'];

	var preventImageDragoverHandler = windowNode.on('dragover', (event) => {
		var validDropTarget = event.target.getDOMNode().isContentEditable;

		if (!validDropTarget) {
			event.preventDefault();
		}
	});

	var preventImageDropHandler = windowNode.on('drop', (event) => {
		var node = event.target.getDOMNode();
		var ignoreNode = node.className.split(' ').filter((value) => {
			return ignoreClass.includes(value);
		});
		var validDropTarget = ignoreNode.length > 0 ? true : node.isContentEditable;

		if (!validDropTarget) {
			event.preventDefault();
			event.stopImmediatePropagation();
		}
	});

	var eventHandles = [preventImageDragoverHandler, preventImageDropHandler];

	window['<%= name %>'] = {
		create: function () {
			if (!alloyEditor) {
				var editorNode = A.Node.create('<%= HtmlUtil.escapeJS(editor) %>');

				var editorContainer = A.one('#<%= name %>Container');

				editorContainer.appendChild(editorNode);

				window['<%= name %>'].initEditor();
			}
		},

		destroy: function () {
			window['<%= name %>'].dispose();

			window['<%= name %>'] = null;

			Liferay.namespace('EDITORS').alloyEditor.removeInstance();
		},

		dispose: function () {
			if (alloyEditor) {
				alloyEditor.destroy();

				alloyEditor = null;
			}

			new A.EventHandle(eventHandles).detach();

			var editorNode = document.getElementById('<%= name %>');

			if (editorNode) {
				editorNode.parentNode.removeChild(editorNode);
			}
		},

		focus: function () {
			if (alloyEditor) {
				alloyEditor.focus();
			}
		},

		getHTML: function () {
			var data = '';

			if (alloyEditor && alloyEditor.instanceReady) {
				data = alloyEditor.getHTML();
			}
			else {
				data = getInitialContent();
			}

			return data;
		},

		getNativeEditor: function () {
			var nativeEditor;

			if (alloyEditor) {
				nativeEditor = alloyEditor.getEditor();
			}

			return nativeEditor;
		},

		getText: function () {
			var data = '';

			if (alloyEditor && alloyEditor.instanceReady) {
				data = alloyEditor.getText();
			}
			else {
				data = getInitialContent();
			}

			return data;
		},

		initEditor: function () {
			createInstance();
		},

		instanceReady: false,

		setHTML: function (value) {
			if (alloyEditor) {
				alloyEditor.setHTML(value);
			}
		},
	};

	Liferay.fire('editorAPIReady', {
		editor: window['<%= name %>'],
		editorName: '<%= name %>',
	});

	<c:if test="<%= autoCreate %>">
		window['<%= name %>'].initEditor();
	</c:if>
</aui:script>