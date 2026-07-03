<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-ui:error exception="<%= CaptchaConfigurationException.class %>" message="a-captcha-error-occurred-please-contact-an-administrator" />
<liferay-ui:error exception="<%= CaptchaException.class %>" message="captcha-verification-failed" />
<liferay-ui:error exception="<%= CaptchaTextException.class %>" message="text-verification-failed" />

<%
Collection<ConvertProcess> convertProcesses = ConvertProcessUtil.getEnabledConvertProcesses();
%>

<liferay-ui:error exception="<%= FileSystemStoreRootDirException.class %>" message="the-root-directories-of-the-selected-file-system-stores-are-not-valid" />

<c:choose>
	<c:when test="<%= convertProcesses.isEmpty() %>">
		<div class="alert alert-info">
			<liferay-ui:message key="no-document-migration-processes-are-available" />
		</div>
	</c:when>
	<c:otherwise>
		<div class="sheet">
			<div class="panel-group panel-group-flush">

				<%
				int i = 0;

				for (ConvertProcess convertProcess : convertProcesses) {
					Class<?> clazz = convertProcess.getClass();
					String parameterDescription = convertProcess.getParameterDescription();
					String[] parameterNames = convertProcess.getParameterNames();
				%>

					<aui:fieldset collapsed="<%= false %>" collapsible="<%= true %>" label="<%= convertProcess.getDescription() %>">
						<c:choose>
							<c:when test="<%= convertProcess.hasCustomView() %>">

								<%
								convertProcess.includeCustomView(request, PipingServletResponseFactory.createPipingServletResponse(pageContext));
								%>

							</c:when>
							<c:when test="<%= parameterNames == null %>">
								<div class="alert alert-info">
									<liferay-ui:message key="<%= convertProcess.getConfigurationErrorMessage() %>" />
								</div>
							</c:when>
							<c:otherwise>
								<aui:field-wrapper label='<%= Validator.isNotNull(parameterDescription) ? parameterDescription : "" %>'>

									<%
									for (String parameterName : parameterNames) {
									%>

										<c:choose>
											<c:when test="<%= parameterName.contains(StringPool.EQUAL) && parameterName.contains(StringPool.SEMICOLON) %>">

												<%
												String[] parameterPair = StringUtil.split(parameterName, CharPool.EQUAL);

												String[] parameterSelectEntries = StringUtil.split(parameterPair[1], CharPool.SEMICOLON);
												%>

												<aui:select label="<%= parameterPair[0] %>" name="<%= clazz.getName() + StringPool.PERIOD + parameterPair[0] %>">

													<%
													for (String parameterSelectEntry : parameterSelectEntries) {
													%>

														<aui:option label="<%= parameterSelectEntry %>" />

													<%
													}
													%>

												</aui:select>
											</c:when>
											<c:otherwise>

												<%
												String[] parameterPair = StringUtil.split(parameterName, CharPool.EQUAL);

												String currentParameterName = null;
												String currentParameterType = null;

												if (parameterPair.length > 1) {
													currentParameterName = parameterPair[0];
													currentParameterType = parameterPair[1];
												}
												else {
													currentParameterName = parameterName;
												}
												%>

												<aui:input cssClass="lfr-input-text-container" label="<%= currentParameterName %>" name="<%= clazz.getName() + StringPool.PERIOD + currentParameterName %>" type='<%= (currentParameterType != null) ? currentParameterType : "" %>' />
											</c:otherwise>
										</c:choose>

									<%
									}
									%>

								</aui:field-wrapper>

								<liferay-captcha:captcha />

								<aui:button-row>
									<aui:button cssClass="save-server-button" data-cmd='<%= "convertProcess." + clazz.getName() %>' value="execute" />
								</aui:button-row>
							</c:otherwise>
						</c:choose>
					</aui:fieldset>

				<%
					i++;
				}
				%>

			</div>
		</div>
	</c:otherwise>
</c:choose>