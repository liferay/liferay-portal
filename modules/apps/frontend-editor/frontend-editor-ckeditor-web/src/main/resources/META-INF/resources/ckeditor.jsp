<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String portletId = portletDisplay.getId();

boolean autoCreate = GetterUtil.getBoolean((String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":autoCreate"));
String contents = (String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":contents");
String cssClass = GetterUtil.getString((String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":cssClass"));
Map<String, Object> data = (Map<String, Object>)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":data");
String editorName = (String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":editorName");
String initMethod = (String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":initMethod");
boolean inlineEdit = GetterUtil.getBoolean((String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":inlineEdit"));
String inlineEditSaveURL = GetterUtil.getString((String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":inlineEditSaveURL"));
String name = GetterUtil.getString((String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":name"));

String onBlurMethod = (String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":onBlurMethod");

if (Validator.isNotNull(onBlurMethod)) {
	onBlurMethod = namespace + onBlurMethod;
}

String onChangeMethod = (String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":onChangeMethod");

if (Validator.isNotNull(onChangeMethod)) {
	onChangeMethod = namespace + onChangeMethod;
}

String onFocusMethod = (String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":onFocusMethod");

if (Validator.isNotNull(onFocusMethod)) {
	onFocusMethod = namespace + onFocusMethod;
}

String onInitMethod = (String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":onInitMethod");

if (Validator.isNotNull(onInitMethod)) {
	onInitMethod = namespace + onInitMethod;
}

String placeholder = GetterUtil.getString((String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":placeholder"));
boolean required = GetterUtil.getBoolean((String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":required"));
boolean skipEditorLoading = GetterUtil.getBoolean((String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":skipEditorLoading"));
String toolbarSet = (String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":toolbarSet");

String ariaLabel = StringPool.BLANK;

if (Validator.isNotNull(placeholder)) {
	ariaLabel = LanguageUtil.get(request, placeholder);
}

if (!inlineEdit) {
	name = namespace + name;
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
		inlineEdit="<%= inlineEdit %>"
		inlineEditSaveURL="<%= inlineEditSaveURL %>"
	/>
</c:if>

<%
String textareaName = HtmlUtil.escapeAttribute(name);

String modules = "aui-node-base";

if (inlineEdit && Validator.isNotNull(inlineEditSaveURL)) {
	textareaName = textareaName + "_original";

	modules += ",inline-editor-ckeditor";
}
%>

<liferay-util:buffer
	var="editor"
>
	<c:if test="<%= Validator.isNotNull(placeholder) %>">
		<label class="control-label" for="<%= HtmlUtil.escapeAttribute(name) %>">
			<liferay-ui:message key="<%= placeholder %>" />

			<c:if test="<%= required %>">
				<clay:icon
					cssClass="reference-mark text-warning"
					symbol="asterisk"
				/>

				<span class="hide-accessible sr-only"><liferay-ui:message key="required" /></span>
			</c:if>
		</label>
	</c:if>

	<textarea class="hide" id="<%= textareaName %>" name="<%= textareaName %>"></textarea>
</liferay-util:buffer>

<div class="<%= HtmlUtil.escapeAttribute(cssClass) %>" id="<%= HtmlUtil.escapeAttribute(name) %>Container">
	<c:if test="<%= autoCreate %>">
		<%= editor %>
	</c:if>
</div>

<aui:script type="text/javascript">
	CKEDITOR.ADDITIONAL_RESOURCE_PARAMS = {
		languageId: themeDisplay.getLanguageId(),
	};

	CKEDITOR.disableAutoInline = true;

	CKEDITOR.dtd.$removeEmpty.i = 0;
	CKEDITOR.dtd.$removeEmpty.span = 0;
</aui:script>

<%
name = HtmlUtil.escapeJS(name);
%>

<aui:script use="<%= modules %>">
	var UA = A.UA;

	var windowNode = A.getWin();

	var instanceDataReady = false;
	var instancePendingData = null;

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

	var onLocaleChangedHandler = function (event) {
		var contentsLanguage = event.item.getAttribute('data-value');
		var contentsLanguageDir = Liferay.Language.direction[contentsLanguage];

		var nativeEditor = window['<%= name %>'].getNativeEditor();

		nativeEditor.config.contentsLanguage = contentsLanguage;
		nativeEditor.config.contentsLangDirection = contentsLanguageDir;
	};

	var preventImageDragoverHandler = windowNode.on('dragover', (event) => {
		var validDropTarget = event.target.getDOMNode().isContentEditable;

		if (!validDropTarget) {
			event.preventDefault();
		}
	});

	var preventImageDropHandler = windowNode.on('drop', (event) => {
		var element = event.target.getDOMNode();
		var validDropTarget =
			element.isContentEditable || !!element.getAttribute('droppable');

		var droppedFiles = event._event.dataTransfer.files || [];

		if (!validDropTarget && droppedFiles.length > 0) {
			event.preventDefault();
			event.stopImmediatePropagation();
		}
	});

	var eventHandles = [
		Liferay.on('inputLocalized:localeChanged', onLocaleChangedHandler),
		preventImageDragoverHandler,
		preventImageDropHandler,
	];

	window['<%= name %>'] = {
		create: function () {
			if (!window['<%= name %>'].instanceReady) {
				createEditor();
			}
		},

		destroy: function () {
			window['<%= name %>'].dispose();

			window['<%= name %>'] = null;

			Liferay.namespace('EDITORS').ckeditor.removeInstance();
		},

		dispose: function () {
			var editor = CKEDITOR.instances['<%= name %>'];

			if (editor) {
				editor.destroy();

				window['<%= name %>'].instanceReady = false;
			}

			new A.EventHandle(eventHandles).detach();

			var editorEl = document.getElementById('<%= name %>');

			if (editorEl) {
				editorEl.parentNode.removeChild(editorEl);
			}
		},

		focus: function () {
			CKEDITOR.instances['<%= name %>'].focus();
		},

		getCkData: function () {
			var data;

			if (!window['<%= name %>'].instanceReady) {
				data = getInitialContent();
			}
			else {
				data = CKEDITOR.instances['<%= name %>'].getData();

				if (CKEDITOR.env.gecko && CKEDITOR.tools.trim(data) == '<br />') {
					data = '';
				}
			}

			return data;
		},

		getHTML: function () {
			return window['<%= name %>'].getCkData();
		},

		getNativeEditor: function () {
			return CKEDITOR.instances['<%= name %>'];
		},

		getText: function () {
			var data;

			if (!window['<%= name %>'].instanceReady) {
				data = getInitialContent();
			}
			else {
				var editor = CKEDITOR.instances['<%= name %>'];

				data = editor.editable().getText();
			}

			return data;
		},

		instanceReady: false,

		<c:if test="<%= Validator.isNotNull(onBlurMethod) %>">
			onBlurCallback: function () {
				window['<%= HtmlUtil.escapeJS(onBlurMethod) %>'](
					CKEDITOR.instances['<%= name %>']
				);
			},
		</c:if>

		<c:if test="<%= Validator.isNotNull(onChangeMethod) %>">
			onChangeCallback: function () {
				var ckEditor = CKEDITOR.instances['<%= name %>'];
				var dirty = ckEditor.checkDirty();

				if (dirty) {
					window['<%= HtmlUtil.escapeJS(onChangeMethod) %>'](
						window['<%= name %>'].getHTML()
					);

					ckEditor.resetDirty();
				}
			},
		</c:if>

		<c:if test="<%= Validator.isNotNull(onFocusMethod) %>">
			onFocusCallback: function () {
				window['<%= HtmlUtil.escapeJS(onFocusMethod) %>'](
					CKEDITOR.instances['<%= name %>']
				);
			},
		</c:if>

		setHTML: function (value) {
			var ckEditorInstance = CKEDITOR.instances['<%= name %>'];

			var win = window['<%= name %>'];

			var setHTML = function (data) {
				if (instanceDataReady) {
					ckEditorInstance.setData(data);
				}
				else {
					instancePendingData = data;
				}

				win._setStyles();
			};

			if (win.instanceReady) {
				setHTML(value);
			}
			else {
				instancePendingData = value;
			}
		},
	};

	var addAUIClass = function (iframe) {
		if (iframe) {
			var iframeWin = iframe.getDOM().contentWindow;

			if (iframeWin) {
				var iframeDoc = iframeWin.document.documentElement;

				A.one(iframeDoc).addClass('aui');
			}
		}
	};

	window['<%= name %>']._setStyles = function () {
		<c:if test="<%= Validator.isNotNull(ariaLabel) %>">
			var ckEditorInstance = CKEDITOR.instances['<%= name %>'];

			var editable = ckEditorInstance && ckEditorInstance.editable();

			if (editable) {
				editable.setAttribute(
					'aria-label',
					'<%= HtmlUtil.escapeJS(ariaLabel) %>'
				);
			}
		</c:if>

		var ckEditor = A.one('#cke_<%= name %>');

		if (ckEditor) {
			var iframe = ckEditor.one('iframe');

			if (iframe) {
				iframe.attr(
					'aria-labelledby',
					'<%= HtmlUtil.escapeAttribute(namespace) %>Aria ' +
						iframe._node.attributes['aria-describedby'].value
				);
			}

			addAUIClass(iframe);

			var ckePanelDelegate = Liferay.Data['<%= name %>Handle'];

			if (!ckePanelDelegate) {
				ckePanelDelegate = ckEditor.delegate(
					'click',
					(event) => {
						var panelFrame = A.one('.cke_combopanel .cke_panel_frame');

						addAUIClass(panelFrame);

						ckePanelDelegate.detach();

						Liferay.Data['<%= name %>Handle'] = null;
					},
					'.cke_combo'
				);

				Liferay.Data['<%= name %>Handle'] = ckePanelDelegate;
			}
		}
	};

	Liferay.fire('editorAPIReady', {
		editor: window['<%= name %>'],
		editorName: '<%= name %>',
	});

	<c:if test="<%= inlineEdit && Validator.isNotNull(inlineEditSaveURL) %>">
		var inlineEditor;

		Liferay.on('toggleControls', (event) => {
			if (event.src === 'ui') {
				var ckEditor = CKEDITOR.instances['<%= name %>'];

				if (event.enabled && !ckEditor) {
					createEditor();
				}
				else if (ckEditor) {
					inlineEditor.destroy();
					ckEditor.destroy();

					var editorNode = A.one('#<%= name %>');

					if (editorNode) {
						editorNode.removeAttribute('contenteditable');
						editorNode.removeClass('lfr-editable');
					}
				}
			}
		});
	</c:if>

	var ckEditorContent;
	var currentToolbarSet;

	var initialToolbarSet =
		'<%= TextFormatter.format(HtmlUtil.escapeJS(toolbarSet), TextFormatter.M) %>';

	function getToolbarSet(toolbarSet) {
		var Util = Liferay.Util;

		if (Util.isPhone()) {
			toolbarSet = 'phone';
		}
		else if (Util.isTablet()) {
			toolbarSet = 'tablet';
		}

		return toolbarSet;
	}

	var afterVal = function () {
		return new A.Do.AlterReturn(
			'Return editor content',
			window['<%= name %>'].getHTML()
		);
	};

	var createEditor = function () {
		var editorContainer = A.one('#<%= name %>Container');
		var editorNode = A.one('#<%= name %>');

		if (!editorNode) {
			editorContainer.setHTML('');

			editorNode = A.Node.create('<%= HtmlUtil.escapeJS(editor) %>');

			editorContainer.appendChild(editorNode);
		}

		if (editorNode) {
			editorNode.attr('contenteditable', true);
			editorNode.addClass('lfr-editable');

			eventHandles.push(A.Do.after(afterVal, editorNode, 'val', this));
		}

		function initData() {
			if (!ckEditorContent) {
				ckEditorContent = getInitialContent();
			}

			var ckEditor = CKEDITOR.instances['<%= name %>'];

			ckEditor.setData(ckEditorContent, () => {
				ckEditor.resetDirty();

				ckEditorContent = '';
			});

			window['<%= name %>']._setStyles();

			<c:if test="<%= Validator.isNotNull(onInitMethod) %>">
				window['<%= HtmlUtil.escapeJS(onInitMethod) %>']();
			</c:if>

			Liferay.component('<%= name %>', window['<%= name %>'], {
				portletId: '<%= portletId %>',
			});
		}

		function initEditor(config) {
			CKEDITOR.<%= inlineEdit ? "inline" : "replace" %>(
				'<%= name %>',
				config
			);

			Liferay.on('<%= name %>selectItem', (event) => {
				CKEDITOR.tools.callFunction(event.ckeditorfuncnum, event.value);
			});

			var ckEditor = CKEDITOR.instances['<%= name %>'];

			<liferay-util:dynamic-include key='<%= "com.liferay.frontend.editor.ckeditor.web#" + editorName + "#onEditorCreate" %>' />

			Liferay.namespace('EDITORS').ckeditor.addInstance();

			<c:if test="<%= inlineEdit && Validator.isNotNull(inlineEditSaveURL) %>">
				inlineEditor = new Liferay.CKEditorInline({
					editor: ckEditor,
					editorName: '<%= name %>',
					namespace: '<portlet:namespace />',
					saveURL: '<%= inlineEditSaveURL %>',
				});
			</c:if>

			var customDataProcessorLoaded = false;

			<%
			boolean useCustomDataProcessor = (editorOptionsDynamicAttributes != null) && GetterUtil.getBoolean(editorOptionsDynamicAttributes.get("useCustomDataProcessor"));
			%>

			<c:if test="<%= useCustomDataProcessor %>">
				ckEditor.on('customDataProcessorLoaded', () => {
					customDataProcessorLoaded = true;

					if (instanceReady) {
						initData();
					}

					// LPS-118801

					var editorPath =
						'<%= HtmlUtil.escapeJS(PortalWebResourcesUtil.getContextPath(PortalWebResourceConstants.RESOURCE_TYPE_EDITOR_CKEDITOR)) %>';

					document
						.querySelectorAll(
							'link[href*="' +
								editorPath +
								'"],script[src*="' +
								editorPath +
								'"]'
						)
						.forEach((tag) => {
							tag.setAttribute('data-senna-track', 'temporary');
						});
				});
			</c:if>

			var instanceReady = false;

			ckEditor.on('instanceReady', () => {
				<c:choose>
					<c:when test="<%= useCustomDataProcessor %>">
						instanceReady = true;

						if (customDataProcessorLoaded) {
							initData();
						}
					</c:when>
					<c:otherwise>
						initData();
					</c:otherwise>
				</c:choose>

				window['<%= name %>'].instanceReady = true;

				<c:if test="<%= Validator.isNotNull(onBlurMethod) %>">
					CKEDITOR.instances['<%= name %>'].on(
						'blur',
						window['<%= name %>'].onBlurCallback
					);
				</c:if>

				<c:if test="<%= Validator.isNotNull(onChangeMethod) %>">
					var contentChangeHandle = setInterval(() => {
						try {
							window['<%= name %>'].onChangeCallback();
						}
						catch (e) {}
					}, 300);

					var clearContentChangeHandle = function (event) {
						if (event.portletId === '<%= portletId %>') {
							clearInterval(contentChangeHandle);

							Liferay.detach('destroyPortlet', clearContentChangeHandle);
						}
					};

					Liferay.on('destroyPortlet', clearContentChangeHandle);
				</c:if>

				<c:if test="<%= Validator.isNotNull(onFocusMethod) %>">
					CKEDITOR.instances['<%= name %>'].on(
						'focus',
						window['<%= name %>'].onFocusCallback
					);
				</c:if>

				<c:if test="<%= !(inlineEdit && Validator.isNotNull(inlineEditSaveURL)) %>">
					var initialEditor = CKEDITOR.instances['<%= name %>'].id;

					eventHandles.push(
						A.getWin().on(
							'resize',
							A.debounce(() => {
								if (
									currentToolbarSet !=
									getToolbarSet(initialToolbarSet)
								) {
									var ckeditorInstance =
										CKEDITOR.instances['<%= name %>'];

									if (ckeditorInstance) {
										var currentEditor = ckeditorInstance.id;

										if (currentEditor === initialEditor) {
											var currentDialog =
												CKEDITOR.dialog.getCurrent();

											if (currentDialog) {
												currentDialog.hide();
											}

											ckEditorContent =
												ckeditorInstance.getData();

											window['<%= name %>'].dispose();

											window['<%= name %>'].create();

											CKEDITOR.instances['<%= name %>'].setData(
												ckEditorContent
											);

											initialEditor =
												CKEDITOR.instances['<%= name %>'].id;
										}
									}
								}
							}, 250)
						)
					);
				</c:if>
			});

			ckEditor.on('dataReady', (event) => {
				if (instancePendingData !== null) {
					var pendingData = instancePendingData;

					instancePendingData = null;

					ckEditor.setData(pendingData);
				}
				else {
					instanceDataReady = true;
				}

				window['<%= name %>']._setStyles();
			});

			ckEditor.on('drop', function (event) {
				var data = event.data.dataTransfer.getData('text/html');

				if (data) {
					var fragment = CKEDITOR.htmlParser.fragment.fromHtml(data);

					var element = fragment.children[0];

					if (element.hasClass('cke_widget_image')) {
						element = element.children[0];
					}

					if (this.pasteFilter && element.name) {
						return this.pasteFilter.check(element.name);
					}
				}
			});

			ckEditor.on('setData', (event) => {
				instanceDataReady = false;
			});

			if (UA.edge && parseInt(UA.edge, 10) >= 14) {
				var resetActiveElementValidation = function (activeElement) {
					activeElement = A.one(activeElement);

					var activeElementAncestor = activeElement.ancestor();

					if (
						activeElementAncestor.hasClass('has-error') ||
						activeElementAncestor.hasClass('has-success')
					) {
						activeElementAncestor.removeClass('has-error');
						activeElementAncestor.removeClass('has-success');

						var formValidatorStack = activeElementAncestor.one(
							'.form-validator-stack'
						);

						if (formValidatorStack) {
							formValidatorStack.remove();
						}
					}
				};

				var onBlur = function (activeElement) {
					resetActiveElementValidation(activeElement);

					setTimeout(() => {
						if (activeElement) {
							ckEditor.focusManager.blur(true);
							activeElement.focus();
						}
					}, 0);
				};

				ckEditor.on('instanceReady', () => {
					var editorWrapper = A.one('#cke_<%= name %>');

					if (editorWrapper) {
						editorWrapper.once('mouseenter', function (event) {
							ckEditor.once(
								'focus',
								onBlur.bind(this, document.activeElement)
							);
							ckEditor.focus();
						});
					}
				});
			}
		}

		currentToolbarSet = getToolbarSet(initialToolbarSet);

		var defaultConfig = {
			filebrowserBrowseUrl: '',
			filebrowserFlashBrowseUrl: '',
			filebrowserImageBrowseLinkUrl: '',
			filebrowserImageBrowseUrl: '',
			filebrowserUploadUrl: null,
			toolbar: currentToolbarSet,
		};

		var editorConfig =
			<%= Validator.isNotNull(editorConfigJSONObject) ? editorConfigJSONObject : "{}" %>;

		var config = A.merge(defaultConfig, editorConfig);

		var editorTransformerURLs = config.editorTransformerURLs;

		if (editorTransformerURLs) {
			var loadingIndicator = document.createElement('span');

			loadingIndicator.classList.add('loading-animation');
			loadingIndicator.setAttribute('aria-hidden', true);

			editorContainer.appendChild(loadingIndicator);

			Liferay.Util.loadEditorClientExtensions({
				config: config,
				onLoad: ({transformedConfig}) => {
					if (loadingIndicator) {
						loadingIndicator.remove();
					}

					initEditor(transformedConfig);
				},
			});
		}
		else {
			initEditor(config);
		}
	};

	<%
	String toogleControlsStatus = GetterUtil.getString(SessionClicks.get(request, "com.liferay.frontend.js.web_toggleControls", "visible"));
	%>

	<c:if test='<%= autoCreate && ((inlineEdit && toogleControlsStatus.equals("visible")) || !inlineEdit) %>'>
		createEditor();
	</c:if>
</aui:script>

<%!
public String marshallParams(Map<String, String> params) {
	if (params == null) {
		return StringPool.BLANK;
	}

	StringBundler sb = new StringBundler(4 * params.size());

	for (Map.Entry<String, String> configParam : params.entrySet()) {
		sb.append(StringPool.AMPERSAND);
		sb.append(configParam.getKey());
		sb.append(StringPool.EQUAL);
		sb.append(URLCodec.encodeURL(configParam.getValue()));
	}

	return sb.toString();
}
%>