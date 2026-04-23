<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/taglib/ui/input_localized/init.jsp" %>

<%
Map<String, Map<String, String>> languagesTranslationsAriaLabelsMap = new HashMap<String, Map<String, String>>();
%>

<c:if test="<%= Validator.isNotNull(inputAddon) %>">
	<div class="form-text">
		<span class="lfr-portal-tooltip" title="<%= HtmlUtil.escape(inputAddon) %>">
			<liferay-ui:message key="<%= HtmlUtil.escape(StringUtil.shorten(inputAddon, 40)) %>" />
		</span>
	</div>
</c:if>

<div class="input-group input-localized input-localized-<%= type %>" id="<%= namespace + id %>BoundingBox">
	<div class="input-group-item">
		<c:choose>
			<c:when test='<%= type.equals("editor") %>'>
				<liferay-ui:input-editor
					contents="<%= mainLanguageValue %>"
					contentsLanguageId="<%= languageId %>"
					cssClass='<%= "language-value " + cssClass %>'
					editorName="<%= editorName %>"
					name="<%= inputEditorName %>"
					onBlurMethod='<%= randomNamespace + "onBlurMethod" %>'
					onChangeMethod='<%= randomNamespace + "onChangeEditor" %>'
					onInitMethod='<%= randomNamespace + "onInitEditor" %>'
					placeholder="<%= placeholder %>"
					toolbarSet="<%= toolbarSet %>"
				/>

				<aui:script>
					var edited = false;

					function <%= namespace + randomNamespace %>onBlurMethod() {
						if (edited && Liferay.FeatureFlags['LPD-11228']) {
							Liferay.fire('journal:unlock')
							edited = false;

							var inputLocalized = Liferay.component('<%= namespace + HtmlUtil.escapeJS(fieldName) %>');

							var label = document.querySelector("label[for='"+inputLocalized.get('namespace')+inputLocalized.get('id')+"']").textContent

							Liferay.fire('journal:storeState', {fieldName: Liferay.Language.get('edit') +
							' ' + label});
						}
					}

					function <%= namespace + randomNamespace %>onChangeEditor() {
						if (Liferay.FeatureFlags['LPD-11228'] && document.activeElement.title === 'editor') {
							Liferay.fire('journal:lock')
							edited = true;
						}

						var inputLocalized = Liferay.component('<%= namespace + HtmlUtil.escapeJS(fieldName) %>');

						var editor = window['<%= namespace + HtmlUtil.escapeJS(inputEditorName) %>'];

						inputLocalized.updateInputLanguage(editor.getHTML());

					}

					function <%= namespace + randomNamespace %>onInitEditor() {
						Liferay.componentReady('<%= namespace + HtmlUtil.escapeJS(fieldName) %>')
							.then(inputLocalized => {
								var editor = window['<%= namespace + HtmlUtil.escapeJS(inputEditorName) %>'];
								inputLocalized.updateInputPlaceholder(editor);
							}
						);
					}
				</aui:script>
			</c:when>
			<c:when test='<%= type.equals("input") %>'>
				<input aria-describedby="<%= namespace + HtmlUtil.escapeAttribute(id + fieldSuffix) %>_desc" class="form-control language-value <%= cssClass %>" dir="<%= mainLanguageDir %>" <%= disabled ? "disabled=\"disabled\"" : "" %> id="<%= namespace + id + HtmlUtil.getAUICompatibleId(fieldSuffix) %>" name="<%= namespace + HtmlUtil.escapeAttribute(name + fieldSuffix) %>" <%= Validator.isNotNull(placeholder) ? "placeholder=\"" + LanguageUtil.get(resourceBundle, placeholder) + "\"" : StringPool.BLANK %> type="text" value="<%= HtmlUtil.escapeAttribute(mainLanguageValue) %>" <%= InlineUtil.buildDynamicAttributes(dynamicAttributes) %> />
			</c:when>
			<c:when test='<%= type.equals("textarea") %>'>
				<textarea maxlength="<%= maxLength %>" aria-labelledby='<%= namespace + id %> <%= namespace + id %>_maxCharacters' aria-describedby="<%= namespace + HtmlUtil.escapeAttribute(id + fieldSuffix) %>_desc" class="form-control language-value <%= cssClass %>" dir="<%= mainLanguageDir %>" <%= disabled ? "disabled=\"disabled\"" : "" %> id="<%= namespace + id + HtmlUtil.getAUICompatibleId(fieldSuffix) %>" name="<%= namespace + HtmlUtil.escapeAttribute(name + fieldSuffix) %>" <%= Validator.isNotNull(placeholder) ? "placeholder=\"" + LanguageUtil.get(resourceBundle, placeholder) + "\"" : StringPool.BLANK %> <%= InlineUtil.buildDynamicAttributes(dynamicAttributes) %>><%= HtmlUtil.escape(mainLanguageValue) %></textarea>

				<span class="sr-only" id="<%= namespace + id %>_maxCharacters">
					<liferay-ui:message key="characters-maximum" />: <%= maxLength %>
				</span>

				<c:if test="<%= autoSize %>">
					<aui:script use="aui-autosize-deprecated">
						A.one('#<%= namespace + id + HtmlUtil.getAUICompatibleId(fieldSuffix) %>').plug(A.Plugin.Autosize);
					</aui:script>
				</c:if>
			</c:when>
		</c:choose>
	</div>

	<div class="hide-accessible sr-only" id="<%= namespace + HtmlUtil.escapeAttribute(id + fieldSuffix) %>_desc"><%= defaultLocale.getDisplayName(LocaleUtil.fromLanguageId(LanguageUtil.getLanguageId(request))) %> <liferay-ui:message key="translation" /></div>

	<c:if test="<%= !availableLocales.isEmpty() && Validator.isNull(languageId) %>">

		<%
		languageIds.add(defaultLanguageId);

		for (Locale availableLocale : availableLocales) {
			String curLanguageId = LocaleUtil.toLanguageId(availableLocale);

			if (curLanguageId.equals(defaultLanguageId)) {
				continue;
			}

			String languageValue = null;

			if (Validator.isNotNull(xml)) {
				languageValue = LocalizationUtil.getLocalization(xml, curLanguageId, false);
			}

			if (Validator.isNotNull(languageValue) || (!ignoreRequestValue && Validator.isNotNull(ParamUtil.getString(request, name + StringPool.UNDERLINE + curLanguageId, languageValue)))) {
				languageIds.add(curLanguageId);
			}
		}

		for (int i = 0; i < languageIds.size(); i++) {
			String curLanguageId = languageIds.get(i);

			Locale curLocale = LocaleUtil.fromLanguageId(curLanguageId);

			String curLanguageDir = LanguageUtil.get(curLocale, "lang.dir");

			String languageValue = StringPool.BLANK;

			if (Validator.isNotNull(xml)) {
				languageValue = LocalizationUtil.getLocalization(xml, curLanguageId, false);
			}

			if (!ignoreRequestValue) {
				languageValue = ParamUtil.getString(request, name + StringPool.UNDERLINE + curLanguageId, languageValue);
			}
		%>

			<aui:input data-field-name="<%= HtmlUtil.escapeAttribute(id + fieldSuffix) %>" data-languageid="<%= curLanguageId %>" dir="<%= curLanguageDir %>" disabled="<%= disabled %>" id="<%= HtmlUtil.escapeAttribute(id + StringPool.UNDERLINE + curLanguageId) %>" name="<%= HtmlUtil.escapeAttribute(fieldNamePrefix + name + StringPool.UNDERLINE + curLanguageId + fieldNameSuffix) %>" type="hidden" value="<%= languageValue %>" />

		<%
		}

		String selectedLanguageName = LanguageUtil.get(request, "language." + selectedLanguageId);

		if (selectedLanguageName.contains("language.")) {
			selectedLanguageName = LanguageUtil.get(request, "language." + selectedLanguageId.substring(0, 2));
		}
		%>

		<div class="input-group-item input-group-item-shrink input-localized-content <%= languagesDropdownVisible ? "" : "hide" %>">

			<%
			String normalizedSelectedLanguageId = StringUtil.replace(selectedLanguageId, '_', '-');
			%>

			<liferay-ui:icon-menu
				direction="<%= languagesDropdownDirection %>"
				icon="<%= StringUtil.toLowerCase(normalizedSelectedLanguageId) %>"
				id='<%= namespace + id + "Menu" %>'
				markupView="lexicon"
				message="<%= StringPool.BLANK %>"
				showWhenSingleIcon="<%= true %>"
				triggerAriaLabel='<%= LanguageUtil.format(request, "current-translation-is-x-press-enter-to-select-another-language", new String[] {selectedLanguageName}, false) %>'
				triggerCssClass="input-localized-trigger"
				triggerLabel="<%= normalizedSelectedLanguageId %>"
				triggerType="button"
			>
				<div id="<%= namespace + id %>PaletteBoundingBox">
					<div class="input-localized-palette-container palette-container" id="<%= namespace + id %>PaletteContentBox">

						<%
						LinkedHashSet<String> uniqueLanguageIds = new LinkedHashSet<String>();

						uniqueLanguageIds.add(defaultLanguageId);

						for (Locale availableLocale : availableLocales) {
							String curLanguageId = LocaleUtil.toLanguageId(availableLocale);

							uniqueLanguageIds.add(curLanguageId);
						}

						int index = 0;

						for (String curLanguageId : uniqueLanguageIds) {
							String linkCssClass = "dropdown-item palette-item keep-aria-attributes";

							Locale curLocale = LocaleUtil.fromLanguageId(curLanguageId);

							if (errorLocales.contains(curLocale) || (curLanguageId.equals(selectedLanguageId) && errorLocales.isEmpty())) {
								linkCssClass += " active";
							}

							String title = HtmlUtil.escapeAttribute(curLocale.getDisplayName(LocaleUtil.fromLanguageId(LanguageUtil.getLanguageId(request)))) + " " + LanguageUtil.get(LocaleUtil.getDefault(), "translation");

							Map<String, Object> iconData = HashMapBuilder.<String, Object>put(
								"index", index++
							).put(
								"languageid", curLanguageId
							).put(
								"value", curLanguageId
							).build();

							String languageName = LanguageUtil.get(request, "language." + curLanguageId);

							if (languageName.contains("language.")) {
								languageName = LanguageUtil.get(request, "language." + curLanguageId.substring(0, 2));
							}

							String translationInstructionAnnouncement = LanguageUtil.format(request, "press-enter-to-edit-x-translation", new String[] {languageName}, false);

							Map<String, String> languageTranslationAriaLabelsMap = HashMapBuilder.put(
								"currentlySelected", LanguageUtil.format(request, "current-translation-is-x-press-enter-to-select-another-language", new String[] {languageName}, false)
							).put(
								"defaultStatus", LanguageUtil.format(request, "default-translation-is-x", new String[] {languageName}, false) + StringPool.SPACE + translationInstructionAnnouncement
							).put(
								"notTranslatedStatus", LanguageUtil.format(request, "not-translated-into-x", new String[] {languageName}, false) + StringPool.SPACE + translationInstructionAnnouncement
							).put(
								"translatedStatus", LanguageUtil.format(request, "translated-into-x", new String[] {languageName}, false) + StringPool.SPACE + translationInstructionAnnouncement
							).build();

							languagesTranslationsAriaLabelsMap.put(curLanguageId, languageTranslationAriaLabelsMap);

							String translationAriaLabel = languageTranslationAriaLabelsMap.get("notTranslatedStatus");

							String translationStatus = LanguageUtil.get(request, "not-translated");
							String translationStatusCssClass = "warning";

							if (languageIds.contains(curLanguageId)) {
								translationAriaLabel = languageTranslationAriaLabelsMap.get("translatedStatus");
								translationStatus = LanguageUtil.get(request, "translated");
								translationStatusCssClass = "success";
							}

							if (defaultLanguageId.equals(curLanguageId)) {
								translationAriaLabel = languageTranslationAriaLabelsMap.get("defaultStatus");
								translationStatus = LanguageUtil.get(request, "default");
								translationStatusCssClass = "info";
							}
						%>

							<liferay-util:buffer
								var="linkContent"
							>
								<span aria-label="<%= translationAriaLabel %>" role="button" tabindex="0">
									<%= StringUtil.replace(curLanguageId, '_', '-') %>

									<span class="dropdown-item-indicator-end w-auto">
										<span class="label label-<%= translationStatusCssClass %>"><%= translationStatus %></span>
									</span>
								</span>
							</liferay-util:buffer>

							<liferay-ui:icon
								alt="<%= title %>"
								data="<%= iconData %>"
								icon="<%= StringUtil.toLowerCase(StringUtil.replace(curLanguageId, '_', '-')) %>"
								iconCssClass="inline-item inline-item-before"
								linkCssClass="<%= linkCssClass %>"
								markupView="lexicon"
								message="<%= linkContent %>"
								url="javascript:void(0);"
							>
							</liferay-ui:icon>

						<%
						}
						%>

						<c:if test="<%= Validator.isNotNull(activeLanguageIds) && !activeLanguageIds.isEmpty() && adminMode %>">
							<li aria-hidden="true" class="dropdown-divider" role="presentation"></li>
							<li>
								<button class="dropdown-item" id="manage-translations" type="button">
									<span class="inline-item inline-item-before">
										<svg class="lexicon-icon lexicon-icon-automatic-translate" role="presentation">
											<use xlink:href="<%= themeDisplay.getPathThemeSpritemap() %>#automatic-translate" />
										</svg>
									</span>
									<span><liferay-ui:message key="manage-translations" /></span>
								</button>
							</li>
						</c:if>
					</div>
				</div>
			</liferay-ui:icon-menu>
		</div>
	</c:if>
</div>

<div class="form-text"><%= HtmlUtil.escape(helpMessage) %></div>

<c:if test="<%= Validator.isNotNull(maxLength) %>">
	<aui:script use="aui-char-counter">
		new A.CharCounter(
			{
				input: '#<%= namespace + id + HtmlUtil.getAUICompatibleId(fieldSuffix) %>:not(textarea)',
				maxLength: <%= maxLength %>
			}
		);
	</aui:script>
</c:if>

<c:choose>
	<c:when test="<%= !availableLocales.isEmpty() && Validator.isNull(languageId) %>">

		<%
		String modules = "liferay-input-localized";

		if (type.equals("editor")) {
			Editor editor = InputEditorTag.getEditor(request, editorName);

			modules += StringPool.COMMA + StringUtil.merge(editor.getJavaScriptModules());
		}
		%>

		<aui:script use="<%= modules %>">
			const inputLocalizedId =
				'<%= namespace + id + HtmlUtil.getAUICompatibleId(fieldSuffix) %>';

			var defaultLanguageId = '<%= defaultLanguageId %>';

			var available = {};

			var errors = {};

			<%
			for (Locale availableLocale : availableLocales) {
			%>

				available['<%= LocaleUtil.toLanguageId(availableLocale) %>'] = '<%= HtmlUtil.escapeJS(availableLocale.getDisplayName(locale)) %>';

			<%
			}
			%>

			var availableLanguageIds = A.Array.dedupe(
				[defaultLanguageId].concat(A.Object.keys(available))
			);

			<%
			for (Locale errorLocale : errorLocales) {
			%>

				errors['<%= LocaleUtil.toLanguageId(errorLocale) %>'] = '<%= errorLocale.getDisplayName(locale) %>';

			<%
			}
			%>

			var errorLanguageIds = A.Array.dedupe(A.Object.keys(errors));
			var placeholder = '#<%= namespace + id + HtmlUtil.getAUICompatibleId(fieldSuffix) %>';

			<c:if test='<%= type.equals("editor") %>'>
				placeholder = placeholder + 'Editor';
			</c:if>

			var inputLocalizedProps = {
				adminMode: <%= adminMode %>,
				availableLocales: available,
				boundingBox: '#<%= namespace + id %>PaletteBoundingBox',
				columns: 20,
				contentBox: '#<%= namespace + id %>PaletteContentBox',
				defaultLanguageId: defaultLanguageId,

				<c:if test='<%= type.equals("editor") %>'>
					editor: window['<%= namespace + HtmlUtil.escapeJS(fieldName) + "Editor" %>'],
				</c:if>

				fieldPrefix: '<%= fieldPrefix %>',
				fieldPrefixSeparator: '<%= fieldPrefixSeparator %>',
				helpMessage: '<%= HtmlUtil.escapeJS(helpMessage) %>',
				id: '<%= id %>',
				inputPlaceholder: placeholder,
				inputBox: '#<%= namespace + id %>BoundingBox',
				items: availableLanguageIds,
				itemsError: errorLanguageIds,
				languagesTranslationsAriaLabels: <%= JSONFactoryUtil.looseSerializeDeep(languagesTranslationsAriaLabelsMap) %>,
				lazy: <%= !type.equals("editor") %>,
				name: '<%= HtmlUtil.escapeJS(name) %>',
				namespace: '<%= namespace %>',
				selectedLanguageId: '<%= selectedLanguageId %>',
				toggleSelection: false,
				translatedLanguages: '<%= StringUtil.merge(languageIds) %>',
			};

			const PATH_CONTEXT = Liferay.ThemeDisplay.getPathContext();

			function <%= namespace + randomNamespace + id %>waitFor(isEditor) {
				const elementNameCallback = isEditor
					? () => {
							const editor =
								window[
									'<%= namespace + HtmlUtil.escapeJS(inputEditorName) %>'
								];

							const nativeEditor = editor?.getNativeEditor();

							return nativeEditor?.element?.getId();
						}
					: () => inputLocalizedId;

				return new Promise((resolve) => {
					const startTime = window.performance.now();

					const check = () => {
						if (window.performance.now() - startTime >= 200) {
							resolve(null);

							return;
						}

						const element = A.one('#' + elementNameCallback());

						if (element) {
							resolve(element);

							return;
						}

						window.requestAnimationFrame(check);
					};

					window.requestAnimationFrame(check);
				});
			}

			<c:choose>
				<c:when test="<%= Validator.isNotNull(activeLanguageIds) && !activeLanguageIds.isEmpty() %>">

					Promise.all([
						import(PATH_CONTEXT + '/o/frontend-js-components-web/__liferay__/index.js'),
						import(PATH_CONTEXT + '/o/frontend-js-react-web/__liferay__/index.js'),
						import(PATH_CONTEXT + '/o/frontend-js-state-web/__liferay__/index.js'),
					]).then(
						([
							frontendJsComponentsWebModule,
							frontendJsReactWebModule,
							frontendJsStateWebModule,
						]) => {

							// Wrapping in a timeout to deal with React's async rendering

							setTimeout(() => {
								<%= namespace + randomNamespace + id %>waitFor(
									<%= type.equals("editor") %>
								).then(() => {
									Liferay.InputLocalized.register(inputLocalizedId, {
										activeLanguageIds:
											<%= JSONFactoryUtil.createJSONArray(activeLanguageIds) %>,
										frontendJsComponentsWebModule,
										frontendJsReactWebModule,
										frontendJsStateWebModule,
										...inputLocalizedProps,
									});
								});
							});
						}
					);
				</c:when>
				<c:otherwise>
					Promise.all([
						import(PATH_CONTEXT + '/o/frontend-js-components-web/__liferay__/index.js'),
						import(PATH_CONTEXT + '/o/frontend-js-state-web/__liferay__/index.js'),
					]).then(([frontendJsComponentsWebModule, frontendJsStateWebModule]) => {

						// Wrapping in a timeout to deal with React's async rendering

						setTimeout(() => {
							<%= namespace + randomNamespace + id %>waitFor(
								<%= type.equals("editor") %>
							).then(() => {
								Liferay.InputLocalized.register(inputLocalizedId, {
									frontendJsComponentsWebModule,
									frontendJsStateWebModule,
									...inputLocalizedProps,
								});
							});
						});
					});
				</c:otherwise>
			</c:choose>

			<c:if test="<%= autoFocus %>">
				Liferay.Util.focusFormField('#<%= namespace + HtmlUtil.escapeJS(id + HtmlUtil.getAUICompatibleId(fieldSuffix)) %>');
			</c:if>
		</aui:script>
	</c:when>
	<c:otherwise>
		<c:if test="<%= autoFocus %>">
			<aui:script>
				Liferay.Util.focusFormField('#<%= namespace + HtmlUtil.escapeJS(id + HtmlUtil.getAUICompatibleId(fieldSuffix)) %>');
			</aui:script>
		</c:if>
	</c:otherwise>
</c:choose>