<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/portal/init.jsp" %>

<div class="position-relative pt-0" id="wrapper">
	<header class="mb-4" id="banner">
		<div class="mb-4 navbar navbar-classic navbar-top py-3">
			<div class="container">
				<div class="align-items-center d-inline-flex logo">

					<%
					Group group = layout.getGroup();
					%>

					<img alt="<%= HtmlUtil.escapeAttribute(group.getDescriptiveName(locale)) %>" height="56" src="<%= HtmlUtil.escape(themeDisplay.getCompanyLogo()) %>" />

					<h1 class="font-weight-bold h2 mb-0 text-dark">
						<%= PropsValues.COMPANY_DEFAULT_NAME %>
					</h1>
				</div>
			</div>
		</div>
	</header>

	<div class="container" id="content">
		<div class="pl-4 position-relative pr-4 pt-4 sheet sheet-lg" id="main-content">
			<h2 class="sheet-title" title="<liferay-ui:message key="basic-configuration" />">
				<liferay-ui:message key="basic-configuration" />
			</h2>

			<%
			UnicodeProperties unicodeProperties = (UnicodeProperties)session.getAttribute(WebKeys.SETUP_WIZARD_PROPERTIES);
			%>

			<c:choose>
				<c:when test="<%= unicodeProperties == null %>">

					<%
					boolean defaultDatabase = SetupWizardUtil.isDefaultDatabase(request);
					%>

					<aui:form action='<%= themeDisplay.getPathMain() + "/portal/setup_wizard" %>' method="post" name="fm" onSubmit="updateConfiguration(event);">
						<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />

						<div class="row">
							<aui:fieldset cssClass="col-md-6">
								<h3 class="sheet-subtitle">
									<liferay-ui:message key="portal" />
								</h3>

								<aui:input label="portal-name" name="companyName" required="<%= true %>" value="<%= PropsValues.COMPANY_DEFAULT_NAME %>" />

								<aui:field-wrapper label="default-language" name="companyLocale">
									<div class="form-group-autofit">
										<div class="form-group-item">
											<aui:select label="" name="companyLocale">

												<%
												String languageId = GetterUtil.getString((String)session.getAttribute(WebKeys.SETUP_WIZARD_DEFAULT_LOCALE), SetupWizardUtil.getDefaultLanguageId());

												for (Locale curLocale : LanguageUtil.getAvailableLocales()) {
												%>

													<aui:option label="<%= curLocale.getDisplayName(curLocale) %>" selected="<%= languageId.equals(LocaleUtil.toLanguageId(curLocale)) %>" value="<%= LocaleUtil.toLanguageId(curLocale) %>" />

												<%
												}
												%>

											</aui:select>
										</div>

										<aui:button name="changeLanguageButton" value="change" />
									</div>
								</aui:field-wrapper>

								<aui:input label="time-zone" name="companyTimeZoneId" type="timeZone" value="<%= SetupWizardUtil.getDefaultTimeZoneId() %>" />
							</aui:fieldset>

							<aui:fieldset cssClass="col-md-6">
								<h3 class="sheet-subtitle">
									<liferay-ui:message key="administrator-user" />
								</h3>

								<aui:input label="first-name" name="adminFirstName" required="<%= true %>" value="<%= PropsValues.DEFAULT_ADMIN_FIRST_NAME %>" />

								<aui:input label="last-name" name="adminLastName" required="<%= true %>" value="<%= PropsValues.DEFAULT_ADMIN_LAST_NAME %>" />

								<aui:input label="email" name="adminEmailAddress" required="<%= true %>">
									<aui:validator name="email" />
								</aui:input>
							</aui:fieldset>
						</div>

						<div class="row">
							<aui:fieldset cssClass="col-md-12">
								<h3 class="sheet-subtitle">
									<liferay-ui:message key="database" />
								</h3>

								<aui:input name="defaultDatabase" type="hidden" value="<%= defaultDatabase %>" />

								<div id="defaultDatabaseOptions">
									<c:choose>
										<c:when test="<%= defaultDatabase %>">
											<p>
												<strong><liferay-ui:message key="default-database" /> (<liferay-ui:message key="database.hypersonic" />)</strong>
											</p>

											<liferay-ui:message key="this-database-is-useful-for-development-and-demo'ing-purposes" />
										</c:when>
										<c:otherwise>
											<div class="h4">
												<liferay-ui:message key="configured-database" />
											</div>

											<dl class="database-values dl-horizontal">
												<c:choose>
													<c:when test="<%= Validator.isNotNull(PropsValues.JDBC_DEFAULT_JNDI_NAME) %>">
														<dt title="<liferay-ui:message key="jdbc-default-jndi-name" />">
															<liferay-ui:message key="jdbc-default-jndi-name" />
														</dt>
														<dd>
															<%= PropsValues.JDBC_DEFAULT_JNDI_NAME %>
														</dd>
													</c:when>
													<c:otherwise>
														<dt title="<liferay-ui:message key="jdbc-url" />">
															<liferay-ui:message key="jdbc-url" />
														</dt>
														<dd>
															<%= PropsValues.JDBC_DEFAULT_URL %>
														</dd>
														<dt title="<liferay-ui:message key="jdbc-driver-class-name" />">
															<liferay-ui:message key="jdbc-driver-class-name" />
														</dt>
														<dd>
															<%= PropsValues.JDBC_DEFAULT_DRIVER_CLASS_NAME %>
														</dd>
														<dt title="<liferay-ui:message key="user-name" />">
															<liferay-ui:message key="user-name" />
														</dt>
														<dd>
															<%= PropsValues.JDBC_DEFAULT_USERNAME %>
														</dd>
														<dt title="<liferay-ui:message key="password" />">
															<liferay-ui:message key="password" />
														</dt>
														<dd>
															<%= StringPool.EIGHT_STARS %>
														</dd>
													</c:otherwise>
												</c:choose>
											</dl>
										</c:otherwise>
									</c:choose>

									<c:if test="<%= Validator.isNull(PropsValues.JDBC_DEFAULT_JNDI_NAME) %>">
										<a href="<%= HttpComponentsUtil.addParameter(themeDisplay.getPathMain() + "/portal/setup_wizard", "defaultDatabase", false) %>" id="customDatabaseOptionsLink">
											(<liferay-ui:message key="change" />)
										</a>
									</c:if>
								</div>

								<div class="hide" id="customDatabaseOptions">
									<div class="connection-messages" id="connectionMessages"></div>

									<a class="d-inline-block database-options mb-3" href="<%= HttpComponentsUtil.addParameter(themeDisplay.getPathMain() + "/portal/setup_wizard", "defaultDatabase", true) %>" id="defaultDatabaseOptionsLink">
										&laquo; <liferay-ui:message key='<%= defaultDatabase ? "use-default-database" : "use-configured-database" %>' />
									</a>

									<aui:select cssClass="database-type" name="databaseType">

										<%
										for (DBType dbType : DBManagerUtil.getDBTypes()) {
											String dbTypeString = dbType.toString();

											String driverClassName = PropsUtil.get(PropsKeys.SETUP_DATABASE_DRIVER_CLASS_NAME, new Filter(dbTypeString));

											String url = PropsUtil.get(PropsKeys.SETUP_DATABASE_URL, new Filter(dbTypeString));
										%>

											<aui:option
												data='<%=
													HashMapBuilder.<String, Object>put(
														"driverClassName", driverClassName
													).put(
														"url", url
													).build()
												%>'
												label='<%= "database." + dbTypeString %>'
												selected="<%= PropsValues.JDBC_DEFAULT_URL.contains(dbTypeString) %>"
												value="<%= dbTypeString %>"
											/>

										<%
										}
										%>

									</aui:select>

									<aui:input id="jdbcDefaultURL" label="jdbc-url" name='<%= "properties--" + PropsKeys.JDBC_DEFAULT_URL + "--" %>' required="<%= true %>" value="<%= PropsValues.JDBC_DEFAULT_URL %>" />

									<aui:input id="jdbcDefaultDriverName" label="jdbc-driver-class-name" name='<%= "properties--" + PropsKeys.JDBC_DEFAULT_DRIVER_CLASS_NAME + "--" %>' required="<%= true %>" value="<%= PropsValues.JDBC_DEFAULT_DRIVER_CLASS_NAME %>" />

									<aui:input id="jdbcDefaultUserName" label="user-name" name='<%= "properties--" + PropsKeys.JDBC_DEFAULT_USERNAME + "--" %>' value="<%= PropsValues.JDBC_DEFAULT_USERNAME %>" />

									<aui:input id="jdbcDefaultPassword" label="password" name='<%= "properties--" + PropsKeys.JDBC_DEFAULT_PASSWORD + "--" %>' type="password" value="<%= PropsValues.JDBC_DEFAULT_PASSWORD %>" />
								</div>
							</aui:fieldset>
						</div>

						<div class="hide row" id="sampleData">
							<aui:fieldset cssClass="col-md-12">
								<h3 class="sheet-subtitle">
									<liferay-ui:message key="sample-data" />
								</h3>

								<aui:input disabled="<%= true %>" helpMessage="add-sample-data-help" id="addSampleData" label="add-sample-data" name='<%= "properties--" + PropsKeys.SETUP_WIZARD_ADD_SAMPLE_DATA + "--" %>' type="checkbox" value="<%= PropsValues.SETUP_WIZARD_ADD_SAMPLE_DATA %>" />
							</aui:fieldset>
						</div>

						<aui:button-row>
							<aui:button name="finishButton" type="submit" value="finish-configuration" />

							<div class="btn float-right" id="basicConfiguration">

								<%
								request.setAttribute("liferay-learn:message:key", "setup-wizard-basic-configuration");
								request.setAttribute("liferay-learn:message:resource", "portal-web");
								%>

								<liferay-util:dynamic-include key="/html/portal/setup_wizard.jsp#help_link" />
							</div>

							<div class="btn float-right hide" id="databaseConfiguration">

								<%
								request.setAttribute("liferay-learn:message:key", "setup-wizard-database-configuration");
								request.setAttribute("liferay-learn:message:resource", "portal-web");
								%>

								<liferay-util:dynamic-include key="/html/portal/setup_wizard.jsp#help_link" />
							</div>
						</aui:button-row>
					</aui:form>

					<aui:script use="aui-base,aui-loading-mask-deprecated,io">
						var customDatabaseOptions = A.one('#customDatabaseOptions');
						var customDatabaseOptionsLink = A.one('#customDatabaseOptionsLink');
						var databaseSelector = A.one('#databaseType');
						var defaultDatabase = A.one('#defaultDatabase');
						var defaultDatabaseOptions = A.one('#defaultDatabaseOptions');
						var defaultDatabaseOptionsLink = A.one('#defaultDatabaseOptionsLink');

						var jdbcDefaultDriverClassName = A.one('#jdbcDefaultDriverName');
						var jdbcDefaultURL = A.one('#jdbcDefaultURL');
						var jdbcDefaultUserName = A.one('#jdbcDefaultUserName');

						var sampleData = A.one('#sampleData');
						var addSampleData = A.one('#addSampleData');

						var basicConfiguration = A.one('#basicConfiguration');
						var databaseConfiguration = A.one('#databaseConfiguration');

						var command = A.one('#<%= Constants.CMD %>');
						var setupForm = A.one('#fm');

						var connectionMessages = A.one('#connectionMessages');

						var toggleDatabaseOptions = function (showDefault, event) {
							if (event) {
								event.preventDefault();
							}

							defaultDatabaseOptions.toggle(showDefault);

							customDatabaseOptions.toggle(!showDefault);

							sampleData.toggle(!showDefault);

							defaultDatabase.val(showDefault);

							databaseConfiguration.toggle(!showDefault);

							basicConfiguration.toggle(showDefault);
						};

						databaseSelector.on('focus', function () {
							addSampleData.removeAttribute('disabled');
						});

						jdbcDefaultURL.on('focus', function () {
							addSampleData.removeAttribute('disabled');
						});

						jdbcDefaultUserName.on('focus', function () {
							addSampleData.removeAttribute('disabled');
						});

						if (customDatabaseOptionsLink) {
							customDatabaseOptionsLink.on(
								'click',
								A.bind(toggleDatabaseOptions, null, false)
							);
						}

						if (defaultDatabaseOptionsLink) {
							defaultDatabaseOptionsLink.on(
								'click',
								A.bind(toggleDatabaseOptions, null, true)
							);
						}

						var onChangeDatabaseSelector = function () {
							var index = databaseSelector.get('selectedIndex');

							var selectedOption = databaseSelector.get('options').item(index);

							var databaseURL = selectedOption.attr('data-url');
							var driverClassName = selectedOption.attr('data-driverClassName');

							jdbcDefaultDriverClassName.val(driverClassName);
							jdbcDefaultURL.val(databaseURL);
						};

						databaseSelector.on('change', onChangeDatabaseSelector);

						A.one('#changeLanguageButton').on('click', function (event) {
							command.val('<%= Constants.TRANSLATE %>');

							setupForm.submit();
						});

						var loadingMask = new A.LoadingMask({
							'strings.loading':
								'<%= UnicodeLanguageUtil.get(request, "liferay-is-being-installed") %>',
							'target': A.one('#main-content'),
						});

						var updateMessage = function (message) {
							connectionMessages.html(
								'<div class="alert alert-danger"><span class="alert-indicator"><svg aria-hidden="true" class="lexicon-icon lexicon-icon-exclamation-full"><use xlink:href="<%= themeDisplay.getPathThemeSpritemap() %>#exclamation-full"></use></svg></span><strong class="lead"><liferay-ui:message key="error-colon" /></strong>' +
									message +
									'</div>'
							);
						};

						var startInstall = function () {
							connectionMessages.empty();

							loadingMask.show();
						};

						var updateConfiguration = function (event) {
							var form = document.fm;

							if (defaultDatabase.val() == 'true') {
								startInstall();

								command.val('<%= Constants.UPDATE %>');

								submitForm(form);
							}
							else {
								command.val('<%= Constants.TEST %>');

								startInstall();

								Liferay.Util.fetch(form.action, {
									body: new FormData(form),
									method: 'POST',
								})
									.then(function (response) {
										return response.json();
									})
									.then(function (responseData) {
										command.val('<%= Constants.UPDATE %>');

										if (!responseData.success) {
											updateMessage(responseData.message);

											loadingMask.hide();
										}
										else {
											submitForm(form);
										}
									})
									.catch(function () {
										loadingMask.hide();

										updateMessage(
											'<%= UnicodeLanguageUtil.get(request, "an-unexpected-error-occurred-while-connecting-to-the-database") %>'
										);
									});
							}
						};
					</aui:script>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="<%= GetterUtil.getBoolean((Boolean)session.getAttribute(WebKeys.SETUP_WIZARD_PROPERTIES_FILE_CREATED)) %>">
							<div class="alert alert-success">
								<span class="alert-indicator">
									<svg aria-hidden="true" class="lexicon-icon lexicon-icon-check-circle-full">
										<use xlink:href="<%= themeDisplay.getPathThemeSpritemap() %>#check-circle-full"></use>
									</svg>
								</span>

								<strong class="lead"><liferay-ui:message key="success-colon" /></strong><liferay-ui:message key="your-configuration-was-saved-successfully" />
							</div>

							<p class="lfr-setup-notice">

								<%
								String taglibArguments = "<span class=\"lfr-inline-code\">" + PropsValues.LIFERAY_HOME + StringPool.SLASH + SetupWizardUtil.PROPERTIES_FILE_NAME + "</span>";
								%>

								<liferay-ui:message arguments="<%= taglibArguments %>" key="the-configuration-was-saved-in" translateArguments="<%= false %>" />
							</p>

							<%
							boolean passwordUpdated = GetterUtil.getBoolean((Boolean)session.getAttribute(WebKeys.SETUP_WIZARD_PASSWORD_UPDATED));
							%>

							<c:if test="<%= !passwordUpdated %>">
								<p class="lfr-setup-notice">
									<liferay-ui:message arguments="<%= PropsValues.DEFAULT_ADMIN_PASSWORD %>" key="your-password-is-x.-you-will-be-required-to-change-your-password-the-next-time-you-log-into-the-portal" translateArguments="<%= false %>" />
								</p>
							</c:if>

							<div class="alert alert-info">
								<span class="alert-indicator">
									<svg aria-hidden="true" class="lexicon-icon lexicon-icon-info-circle">
										<use xlink:href="<%= themeDisplay.getPathThemeSpritemap() %>#info-circle"></use>
									</svg>
								</span>

								<strong class="lead"><liferay-ui:message key="info" />:</strong><liferay-ui:message key="changes-will-take-effect-once-the-portal-is-restarted-please-restart-the-portal-now" />
							</div>
						</c:when>
						<c:otherwise>
							<p>
								<div class="alert alert-warning">
									<span class="alert-indicator">
										<svg aria-hidden="true" class="lexicon-icon lexicon-icon-warning-full">
											<use xlink:href="<%= themeDisplay.getPathThemeSpritemap() %>#warning-full"></use>
										</svg>
									</span>

									<%
									String taglibArguments = "<span class=\"lfr-inline-code\">" + PropsValues.LIFERAY_HOME + "</span>";
									%>

									<strong class="lead"><liferay-ui:message key="warning-colon" /></strong><liferay-ui:message arguments="<%= taglibArguments %>" key="sorry,-we-were-not-able-to-save-the-configuration-file-in-x" translateArguments="<%= false %>" />
								</div>
							</p>

							<aui:input cssClass="properties-text" label="" name="portal-ext" type="textarea" value="<%= unicodeProperties.toString() %>" wrap="soft" />
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

	<footer id="footer">
		<div class="container">
			<div class="row">
				<div class="col-md-12 text-center text-md-left">
					<liferay-util:buffer
						var="poweredByLiferay"
					>
						<a class="text-white" href="http://www.liferay.com" rel="external">Liferay</a>
					</liferay-util:buffer>

					<liferay-ui:message arguments="<%= poweredByLiferay %>" key="powered-by-x" />
				</div>
			</div>
		</div>
	</footer>
</div>