<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ExportTranslationDisplayContext exportTranslationDisplayContext = (ExportTranslationDisplayContext)request.getAttribute(ExportTranslationDisplayContext.class.getName());

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(exportTranslationDisplayContext.getRedirect());
portletDisplay.setURLBackTitle(ParamUtil.getString(request, "backURLTitle"));

renderResponse.setTitle(exportTranslationDisplayContext.getTitle());
%>

<div class="translation">
	<clay:container-fluid
		cssClass="container-view translation-export"
	>
		<clay:sheet>
			<div>
				<aui:select label="export-file-format" name="exportMimeType" wrapperCssClass="w-50"></aui:select>
				<aui:select label="original-language" name="sourceLanguageId"></aui:select>

				<div class="form-group">
					<label class="mb-2"><liferay-ui:message key="languages-to-translate-to" /></label>

					<div class="row">

						<%
						for (Locale availableLocale : LanguageUtil.getAvailableLocales(themeDisplay.getSiteGroupId())) {
						%>

							<div class="col-md-4 py-2">
								<div class="custom-checkbox custom-control">
									<label>
										<input class="custom-control-input" name="targetLanguageIds" type="checkbox" />

										<span class="custom-control-label">
											<span class="custom-control-label-text"><%= exportTranslationDisplayContext.getDisplayName(locale, availableLocale) %></span>
										</span>
									</label>
								</div>
							</div>

						<%
						}
						%>

					</div>
				</div>

				<%
				Map<String, Object> exportTranslationData = exportTranslationDisplayContext.getExportTranslationData();
				%>

				<c:choose>
					<c:when test='<%= (boolean)exportTranslationData.get("multiplePagesSelected") %>'>
						<c:if test='<%= (boolean)exportTranslationData.get("multipleExperiences") %>'>
							<div class="form-group">
								<label class="mb-2"><liferay-ui:message key="export-experiences" /></label>

								<div class="custom-radio">
									<clay:radio
										checked="<%= true %>"
										disabled="<%= true %>"
										label='<%= LanguageUtil.get(request, "default-experience") %>'
										name="exportExperience"
										value="default"
									/>

									<div class="form-text">
										<liferay-ui:message key="export-default-experience-help-message" />
									</div>

									<clay:radio
										disabled="<%= true %>"
										label='<%= LanguageUtil.get(request, "all-experiences") %>'
										name="exportExperience"
										value="all"
									/>

									<div class="form-text">
										<liferay-ui:message key="export-all-experiences-help-message" />
									</div>
								</div>
							</div>
						</c:if>
					</c:when>
					<c:otherwise>

						<%
						List<Map<String, String>> experiences = exportTranslationDisplayContext.getExperiences();
						%>

						<c:if test="<%= (experiences != null) && (experiences.size() > 1) %>">
							<div class="form-group">
								<label class="mb-2"><liferay-ui:message key="select-experiences" /></label>

								<ul class="list-group translation-experiences-wrapper">

									<%
									for (Map<String, String> experience : experiences) {
									%>

										<li class="list-group-item list-group-item-flex">
											<clay:content-col>
												<div class="custom-checkbox custom-control">
													<label>
														<input checked class="custom-control-input" id="experience_<%= experience.get("value") %>" type="checkbox" />

														<span class="custom-control-label"></span>
													</label>
												</div>
											</clay:content-col>

											<clay:content-col
												expand="<%= true %>"
											>
												<clay:content-row
													containerElement="label"
													cssClass="list-group-label"
													for='<%= "experience_" + experience.get("value") %>'
												>
													<clay:content-col
														cssClass="translation-experience-name"
													>
														<div class="text-truncate" title="<%= experience.get("label") %>">
															<%= experience.get("label") %>
														</div>
													</clay:content-col>

													<clay:content-col
														cssClass="text-right"
														expand="<%= true %>"
													>
														<span class="small text-secondary text-truncate"><%= experience.get("segment") %></span>
													</clay:content-col>
												</clay:content-row>
											</clay:content-col>
										</li>

									<%
									}
									%>

								</ul>
							</div>
						</c:if>
					</c:otherwise>
				</c:choose>

				<div class="btn-group">
					<div class="btn-group-item">
						<clay:button
							disabled="<%= true %>"
							displayType="primary"
							label="export"
							type="submit"
						/>
					</div>

					<%
					String redirectURL = exportTranslationDisplayContext.getRedirect();
					%>

					<c:if test="<%= Validator.isNotNull(redirectURL) %>">
						<div class="btn-group-item">
							<clay:link
								displayType="secondary"
								href="<%= redirectURL %>"
								label="cancel"
								type="button"
							/>
						</div>
					</c:if>
				</div>

				<react:component
					module="{ExportTranslation} from translation-web"
					props="<%= exportTranslationData %>"
				/>
			</div>
		</clay:sheet>
	</clay:container-fluid>
</div>