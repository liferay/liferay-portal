<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewTranslationDisplayContext viewTranslationDisplayContext = (ViewTranslationDisplayContext)request.getAttribute(ViewTranslationDisplayContext.class.getName());
%>

<liferay-util:html-top>
	<aui:link hashedFile="<%= true %>" href="translation-web/css/main.css" rel="stylesheet" />
</liferay-util:html-top>

<clay:container-fluid
	cssClass="container-view translation"
>
	<div class="sheet translation-edit-body-form">
		<clay:row>
			<clay:col
				md="6"
			>

				<%
				String sourceLanguageIdTitle = viewTranslationDisplayContext.getLanguageIdTitle(viewTranslationDisplayContext.getSourceLanguageId());
				%>

				<clay:icon
					symbol="<%= StringUtil.toLowerCase(sourceLanguageIdTitle) %>"
				/>

				<span class="ml-1"> <%= sourceLanguageIdTitle %> </span>

				<hr class="separator" />
			</clay:col>

			<clay:col
				md="6"
			>

				<%
				String targetLanguageIdTitle = viewTranslationDisplayContext.getLanguageIdTitle(viewTranslationDisplayContext.getTargetLanguageId());
				%>

				<clay:icon
					symbol="<%= StringUtil.toLowerCase(targetLanguageIdTitle) %>"
				/>

				<span class="ml-1"> <%= targetLanguageIdTitle %> </span>

				<hr class="separator" />
			</clay:col>
		</clay:row>

		<%
		for (InfoFieldSetEntry infoFieldSetEntry : viewTranslationDisplayContext.getInfoFieldSetEntries()) {
			List<InfoField> infoFields = viewTranslationDisplayContext.getInfoFields(infoFieldSetEntry);

			if (ListUtil.isEmpty(infoFields)) {
				continue;
			}

			String infoFieldSetLabel = viewTranslationDisplayContext.getInfoFieldSetLabel(infoFieldSetEntry, locale);
		%>

			<c:if test="<%= Validator.isNotNull(infoFieldSetLabel) %>">
				<clay:row>
					<clay:col
						md="6"
					>
						<div class="fieldset-title">
							<%= infoFieldSetLabel %>
						</div>
					</clay:col>

					<clay:col
						md="6"
					>
						<div class="fieldset-title">
							<%= infoFieldSetLabel %>
						</div>
					</clay:col>
				</clay:row>
			</c:if>

			<%
			for (InfoField<TextInfoFieldType> infoField : infoFields) {
				boolean html = viewTranslationDisplayContext.isHTMLInfoFieldType(infoField);
				String label = viewTranslationDisplayContext.getInfoFieldLabel(infoField);

				boolean multiline = html || viewTranslationDisplayContext.getBooleanValue(infoField, TextInfoFieldType.MULTILINE);

				String sourceContentDir = LanguageUtil.get(viewTranslationDisplayContext.getSourceLocale(), "lang.dir");

				List<String> sourceStringValues = viewTranslationDisplayContext.getStringValues(infoField, viewTranslationDisplayContext.getSourceLocale());

				Iterator<String> sourceStringValuesIterator = sourceStringValues.iterator();

				List<String> targetStringValues = viewTranslationDisplayContext.getStringValues(infoField, viewTranslationDisplayContext.getTargetLocale());

				Iterator<String> targetStringValuesIterator = targetStringValues.iterator();

				while (sourceStringValuesIterator.hasNext() && targetStringValuesIterator.hasNext()) {
					String sourceContent = sourceStringValuesIterator.next();
					String targetContent = targetStringValuesIterator.next();
			%>

					<clay:row>
						<clay:col
							md="6"
						>
							<c:choose>
								<c:when test="<%= html %>">
									<label class="control-label">
										<%= label %>
									</label>

									<div class="translation-editor-preview" dir="<%= sourceContentDir %>">
										<%= sourceContent %>
									</div>
								</c:when>
								<c:otherwise>
									<aui:input dir="<%= sourceContentDir %>" label="<%= label %>" name="<%= infoField.getUniqueId() %>" readonly="true" tabIndex="-1" type='<%= multiline ? "textarea" : "text" %>' value="<%= sourceContent %>" />
								</c:otherwise>
							</c:choose>
						</clay:col>

						<clay:col
							md="6"
						>
							<c:choose>
								<c:when test="<%= html %>">
									<label class="control-label">
										<%= label %>
									</label>

									<div class="translation-editor-preview" dir="<%= LanguageUtil.get(viewTranslationDisplayContext.getTargetLocale(), "lang.dir") %>">
										<%= targetContent %>
									</div>
								</c:when>
								<c:otherwise>
									<aui:input dir='<%= LanguageUtil.get(viewTranslationDisplayContext.getTargetLocale(), "lang.dir") %>' label="<%= label %>" name="<%= infoField.getUniqueId() %>" readonly="true" tabIndex="-1" type='<%= multiline ? "textarea" : "text" %>' value="<%= targetContent %>" />
								</c:otherwise>
							</c:choose>
						</clay:col>
					</clay:row>

		<%
				}
			}
		}
		%>

	</div>
</clay:container-fluid>