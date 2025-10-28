<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/taglib/init.jsp" %>

<%
String randomNamespace = StringUtil.randomId() + StringPool.UNDERLINE;

String formName = namespace + request.getAttribute("liferay-ui:page-iterator:formName");
int cur = GetterUtil.getInteger((String)request.getAttribute("liferay-ui:page-iterator:cur"));
String curParam = (String)request.getAttribute("liferay-ui:page-iterator:curParam");
int delta = GetterUtil.getInteger((String)request.getAttribute("liferay-ui:page-iterator:delta"));
boolean deltaConfigurable = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:page-iterator:deltaConfigurable"));
String deltaParam = (String)request.getAttribute("liferay-ui:page-iterator:deltaParam");
boolean forcePost = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:page-iterator:forcePost"));
String id = (String)request.getAttribute("liferay-ui:page-iterator:id");
String jsCall = GetterUtil.getString((String)request.getAttribute("liferay-ui:page-iterator:jsCall"));
PortletURL portletURL = (PortletURL)request.getAttribute("liferay-ui:page-iterator:portletURL");
int total = GetterUtil.getInteger((String)request.getAttribute("liferay-ui:page-iterator:total"));
String url = StringPool.BLANK;
String urlAnchor = StringPool.BLANK;
int pages = GetterUtil.getInteger((String)request.getAttribute("liferay-ui:page-iterator:pages"));

int initialPages = 20;

if (portletURL != null) {
	String[] urlArray = PortalUtil.stripURLAnchor(portletURL.toString(), StringPool.POUND);

	url = urlArray[0];
	urlAnchor = urlArray[1];

	if (url.indexOf(CharPool.QUESTION) == -1) {
		url += "?";
	}
	else if (!url.endsWith("&")) {
		url += "&";
	}
}

if (Validator.isNull(id)) {
	id = PortalUtil.generateRandomKey(request, "taglib-page-iterator");
}

int start = (cur - 1) * delta;

int end = cur * delta;

if (end > total) {
	end = total;
}

if (deltaConfigurable) {
	url = HttpComponentsUtil.setParameter(url, namespace + deltaParam, String.valueOf(delta));
}

NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
%>

<c:if test="<%= forcePost && (portletURL != null) %>">

	<%
	url = url.split(namespace)[0];
	%>

	<liferay-util:html-bottom>
		<form action="<%= HtmlUtil.escapeAttribute(url) %>" id="<%= randomNamespace + namespace %>pageIteratorFm" method="post" name="<%= randomNamespace + namespace %>pageIteratorFm">
			<aui:input name="<%= curParam %>" type="hidden" />
			<liferay-portlet:renderURLParams portletURL="<%= portletURL %>" />
		</form>
	</liferay-util:html-bottom>
</c:if>

<c:if test="<%= (total > delta) || (total > PropsValues.SEARCH_CONTAINER_PAGE_DELTA_VALUES[0]) %>">
	<div class="pagination-bar" data-qa-id="paginator" id="<%= namespace + id %>">

		<%
		String ariaPagination = namespace + id + "_ariaPagination";
		String ariaPaginationButton = namespace + id + "_ariaPaginationButton";
		String ariaPaginationPicker = namespace + id + "_ariaPaginationPicker";
		String ariaPaginationResults = namespace + id + "_ariaPaginationResults";
		%>

		<c:if test="<%= deltaConfigurable %>">
			<div class="dropdown pagination-items-per-page" id="<%= ariaPagination %>">
				<button aria-controls="<%= ariaPaginationPicker %>" aria-describedby="<%= ariaPaginationResults %>" aria-expanded="false" aria-haspopup="listbox" aria-label="<%= LanguageUtil.get(request, "items-per-page") %>" class="dropdown-toggle page-link" data-attribute="<%= delta %>" data-toggle="liferay-dropdown" id=<%= ariaPaginationButton %> role="combobox">
					<liferay-ui:message arguments="<%= delta %>" key="x-entries" />

					<aui:icon image="caret-double-l" markupView="lexicon" />
				</button>

				<ul aria-labelledby="<%= ariaPaginationButton %>" class="dropdown-menu dropdown-menu-top" id="<%= ariaPaginationPicker %>" role="listbox" tabindex="-1">

					<%
					for (int curDelta : PropsValues.SEARCH_CONTAINER_PAGE_DELTA_VALUES) {
						if (curDelta > SearchContainer.MAX_DELTA) {
							continue;
						}

						String curDeltaURL = HttpComponentsUtil.setParameter(url + urlAnchor, namespace + deltaParam, curDelta);
					%>

						<li role="presentation">
							<liferay-ui:csp>
								<a aria-selected="<%= (delta == curDelta) ? "true" : "false" %>" class="dropdown-item <%= (delta == curDelta) ? "active" : "" %>" href="<%= HtmlUtil.escapeHREF(curDeltaURL) %>" id="<%= randomNamespace + String.valueOf(curDelta) %>" name="<%= String.valueOf(curDelta) %>" onClick="<%= forcePost ? _getOnClick(namespace, deltaParam, curDelta) : "" %>" role="option">
									<%= String.valueOf(curDelta) %><span class="sr-only"><%= StringPool.NBSP %><liferay-ui:message key="entries-per-page" /></span>
								</a>
							</liferay-ui:csp>
						</li>

					<%
					}
					%>

				</ul>
			</div>

			<aui:script senna="temporary" type="text/javascript">
				function <portlet:namespace />handleDropdownKeyPress(button, list, options, dropdown) {
					function onButtonKeyDown(event) {
						if (event.key === 'ArrowDown' || event.key === 'ArrowUp' || event.key === 'Enter' || event.key === ' ') {
							event.preventDefault();

							button.setAttribute('aria-expanded', 'true');
							list.classList.add('show');

							var element = list.querySelector('.active') || list;

							if (element) {
								element.focus();
							}
						}
					}

					button.addEventListener('keydown', onButtonKeyDown );

					function onLeaveDropdown() {
						button.setAttribute('aria-expanded', 'false');
						list.classList.remove('show');
					}

					function handleKeyEvents(event) {
						var currentIndex = Array.from(options).indexOf(document.activeElement);

						if (event.key === 'ArrowDown') {
							event.preventDefault();

							if (currentIndex < options.length - 1) {
								options[currentIndex + 1].focus();
							}
						}
						else if (event.key === 'ArrowUp') {
							event.preventDefault();

							if (currentIndex > 0) {
								options[currentIndex - 1].focus();
							}
						}
						else if (event.key === 'Escape') {
							button.focus();
							onLeaveDropdown();
						}
					}

					list.addEventListener('keydown', handleKeyEvents);

					function dropdownFocusOut(event) {
						if (!dropdown.contains(event.relatedTarget)) {
							onLeaveDropdown();
						}
					}

					list.addEventListener('focusout', dropdownFocusOut );

					var destroyDropDownPagination = function () {
						button.removeEventListener('keydown', onButtonKeyDown);
						list.removeEventListener('focusout', dropdownFocusOut );
						list.removeEventListener('keydown', handleKeyEvents);
					};

					Liferay.once('beforeScreenFlip', destroyDropDownPagination);
				}

				var dropdown = document.getElementById("<%= ariaPagination %>");

				var button = dropdown.querySelector('.dropdown-toggle');
				var list = dropdown.querySelector('.dropdown-menu');
				var options = list.querySelectorAll('.dropdown-item');

				<portlet:namespace />handleDropdownKeyPress(button, list, options, dropdown);
			</aui:script>
		</c:if>

		<p aria-hidden="true" class="pagination-results" data-aria-hidden="true" id="<%= ariaPaginationResults %>">
			<liferay-ui:message arguments="<%= new Object[] {numberFormat.format(start + 1), numberFormat.format(end), numberFormat.format(total)} %>" key="showing-x-to-x-of-x-entries" />
		</p>

		<nav aria-label="<liferay-ui:message key="pagination" />">
			<ul class="pagination">
				<li class="page-item <%= (cur > 1) ? StringPool.BLANK : "disabled" %>">
					<liferay-ui:csp>
						<c:choose>
							<c:when test="<%= cur > 1 %>">
								<a class="lfr-portal-tooltip page-link" href="<%= _getHREF(formName, namespace + curParam, cur - 1, jsCall, url, urlAnchor) %>" onclick="<%= forcePost ? _getOnClick(namespace, curParam, cur -1) : "" %>" title="<%= LanguageUtil.get(request, "previous-page") %>">
							</c:when>
							<c:otherwise>
								<div class="page-link">
							</c:otherwise>
						</c:choose>

							<liferay-ui:icon
								icon='<%= PortalUtil.isRightToLeft(request) ? "angle-right" : "angle-left" %>'
								markupView="lexicon"
							/>

						<c:choose>
							<c:when test="<%= cur > 1 %>">
								</a>
							</c:when>
							<c:otherwise>
								</div>
							</c:otherwise>
						</c:choose>
					</liferay-ui:csp>
				</li>

				<c:choose>
					<c:when test="<%= pages <= 5 %>">

						<%
						for (int i = 1; i <= pages; i++) {
						%>

							<li class="page-item <%= (i == cur) ? "active" : StringPool.BLANK %>">
								<liferay-ui:csp>
									<c:choose>
										<c:when test="<%= i == cur %>">
											<a aria-current="page" aria-label="<%= LanguageUtil.format(request, "page-x", i) %>" class="page-link" href="<%= _getHREF(formName, namespace + curParam, i, jsCall, url, urlAnchor) %>" tabindex="0">
										</c:when>
										<c:otherwise>
											<a aria-label="<%= LanguageUtil.format(request, "page-x", i) %>" class="page-link" href="<%= _getHREF(formName, namespace + curParam, i, jsCall, url, urlAnchor) %>" onclick="<%= forcePost ? _getOnClick(namespace, curParam, i) : "" %>">
										</c:otherwise>
									</c:choose>

									<%= i %></a>
								</liferay-ui:csp>
							</li>

						<%
						}
						%>

					</c:when>
					<c:when test="<%= cur == 1 %>">
						<li class="active page-item">
							<a aria-current="page" aria-label="<%= LanguageUtil.format(request, "page-x", 1) %>" class="page-link" href="<%= _getHREF(formName, namespace + curParam, 1, jsCall, url, urlAnchor) %>" tabindex="0">1</a>
						</li>
						<li class="page-item">
							<liferay-ui:csp>
								<a aria-label="<%= LanguageUtil.format(request, "page-x", 2) %>" class="page-link" href="<%= _getHREF(formName, namespace + curParam, 2, jsCall, url, urlAnchor) %>" onclick="<%= forcePost ? _getOnClick(namespace, curParam, 2) : "" %>">2</a>
							</liferay-ui:csp>
						</li>
						<li class="page-item">
							<liferay-ui:csp>
								<a aria-label="<%= LanguageUtil.format(request, "page-x", 3) %>" class="page-link" href="<%= _getHREF(formName, namespace + curParam, 3, jsCall, url, urlAnchor) %>" onclick="<%= forcePost ? _getOnClick(namespace, curParam, 3) : "" %>">3</a>
							</liferay-ui:csp>
						</li>
						<li class="dropdown page-item">
							<button aria-controls="dropdown-pages-1" aria-haspopup="true" class="dropdown-toggle page-link page-link" data-toggle="liferay-dropdown" title="<%= LanguageUtil.get(request, "show-intermediate-pages") %>">
								<span aria-hidden="true">...</span>

								<span class="sr-only"><liferay-ui:message key="intermediate-pages" />&nbsp;<liferay-ui:message key="use-tab-to-navigate" /></span>
							</button>

							<div class="dropdown-menu dropdown-menu-top dropdown-menu-width-shrink">
								<ul aria-expanded="false" class="inline-scroller link-list" id="dropdown-pages-1" role="menu">

									<%
									for (int i = 4; i < initialPages; i++) {
										if (i >= pages) {
											break;
										}
									%>

										<li role="presentation">
											<liferay-ui:csp>
												<a aria-label="<%= LanguageUtil.format(request, "page-x", i) %>" class="dropdown-item" href="<%= _getHREF(formName, namespace + curParam, i, jsCall, url, urlAnchor) %>" id="<%= randomNamespace + String.valueOf(i) %>" onclick="<%= forcePost ? _getOnClick(namespace, curParam, i) : "" %>" role="menuitem"><%= i %></a>
											</liferay-ui:csp>
										</li>

									<%
									}
									%>

								</ul>
							</div>
						</li>
						<li class="page-item">
							<liferay-ui:csp>
								<a aria-label="<%= LanguageUtil.format(request, "page-x", pages) %>" class="page-link" href="<%= _getHREF(formName, namespace + curParam, pages, jsCall, url, urlAnchor) %>" onclick="<%= forcePost ? _getOnClick(namespace, curParam, pages) : "" %>"><%= pages %></a>
							</liferay-ui:csp>
						</li>
					</c:when>
					<c:when test="<%= cur == pages %>">
						<li class="page-item">
							<liferay-ui:csp>
								<a aria-label="<%= LanguageUtil.format(request, "page-x", 1) %>" class="page-link" href="<%= _getHREF(formName, namespace + curParam, 1, jsCall, url, urlAnchor) %>" onclick="<%= forcePost ? _getOnClick(namespace, curParam, 1) : "" %>">1</a>
							</liferay-ui:csp>
						</li>
						<li class="dropdown page-item">
							<button aria-controls="dropdown-pages-2" aria-haspopup="true" class="dropdown-toggle page-link" data-toggle="liferay-dropdown" title="<%= LanguageUtil.get(request, "show-intermediate-pages") %>">
								<span aria-hidden="true">...</span>

								<span class="sr-only"><liferay-ui:message key="intermediate-pages" />&nbsp;<liferay-ui:message key="use-tab-to-navigate" /></span>
							</button>

							<div class="dropdown-menu dropdown-menu-top dropdown-menu-width-shrink">
								<ul aria-expanded="false" class="inline-scroller link-list" data-max-index="<%= pages - 2 %>" id="dropdown-pages-2" role="menu">

									<%
									for (int i = 2; i < ((initialPages > (cur - 2)) ? cur - 2 : initialPages); i++) {
									%>

										<li role="presentation">
											<liferay-ui:csp>
												<a aria-label="<%= LanguageUtil.format(request, "page-x", i) %>" class="dropdown-item" href="<%= _getHREF(formName, namespace + curParam, i, jsCall, url, urlAnchor) %>" id="<%= randomNamespace + String.valueOf(i) %>" onclick="<%= forcePost ? _getOnClick(namespace, curParam, i) : "" %>" role="menuitem"><%= i %></a>
											</liferay-ui:csp>
										</li>

									<%
									}
									%>

								</ul>
							</div>
						</li>
						<li class="page-item">
							<liferay-ui:csp>
								<a aria-label="<%= LanguageUtil.format(request, "page-x", pages - 2) %>" class="page-link" href="<%= _getHREF(formName, namespace + curParam, pages - 2, jsCall, url, urlAnchor) %>" onclick="<%= forcePost ? _getOnClick(namespace, curParam, pages - 2) : "" %>"><%= pages - 2 %></a>
							</liferay-ui:csp>
						</li>
						<li class="page-item">
							<liferay-ui:csp>
								<a aria-label="<%= LanguageUtil.format(request, "page-x", pages - 1) %>" class="page-link" href="<%= _getHREF(formName, namespace + curParam, pages - 1, jsCall, url, urlAnchor) %>" onclick="<%= forcePost ? _getOnClick(namespace, curParam, pages - 1) : "" %>"><%= pages - 1 %></a>
							</liferay-ui:csp>
						</li>
						<li class="active page-item">
							<a aria-current="page" aria-label="<%= LanguageUtil.format(request, "page-x", pages) %>" class="page-link" href="<%= _getHREF(formName, namespace + curParam, pages, jsCall, url, urlAnchor) %>" tabindex="0"><%= pages %></a>
						</li>
					</c:when>
					<c:otherwise>
						<li class="page-item">
							<liferay-ui:csp>
								<a aria-label="<%= LanguageUtil.format(request, "page-x", 1) %>" class="page-link" href="<%= _getHREF(formName, namespace + curParam, 1, jsCall, url, urlAnchor) %>" onclick="<%= forcePost ? _getOnClick(namespace, curParam, 1) : "" %>">1</a>
							</liferay-ui:csp>
						</li>

						<c:if test="<%= (cur - 3) > 1 %>">
							<li class="dropdown page-item">
								<button aria-controls="dropdown-pages-3" aria-haspopup="true" class="dropdown-toggle page-link" data-toggle="liferay-dropdown" title="<%= LanguageUtil.get(request, "show-intermediate-pages") %>">
									<span aria-hidden="true">...</span>

									<span class="sr-only"><liferay-ui:message key="intermediate-pages" />&nbsp;<liferay-ui:message key="use-tab-to-navigate" /></span>
								</button>

								<div class="dropdown-menu dropdown-menu-top dropdown-menu-width-shrink">
									<ul aria-expanded="false" class="inline-scroller link-list" data-max-index="<%= cur - 1 %>" id="dropdown-pages-3">
						</c:if>

						<%
						for (int i = 2; i < ((initialPages > (cur - 1)) ? cur - 1 : initialPages); i++) {
						%>

							<li class="<%= ((cur - 3) > 1) ? "" : "page-item" %>" role="presentation">
								<liferay-ui:csp>
									<a aria-label="<%= LanguageUtil.format(request, "page-x", i) %>" class="<%= ((cur - 3) > 1) ? "dropdown-item" : "dropdown-item page-link" %>" href="<%= _getHREF(formName, namespace + curParam, i, jsCall, url, urlAnchor) %>" id="<%= randomNamespace + String.valueOf(i) %>" onclick="<%= forcePost ? _getOnClick(namespace, curParam, i) : "" %>" role="menuitem"><%= i %></a>
								</liferay-ui:csp>
							</li>

						<%
						}
						%>

						<c:if test="<%= (cur - 3) > 1 %>">
									</ul>
								</div>
							</li>
						</c:if>

						<c:if test="<%= (cur - 1) > 1 %>">
							<li class="page-item">
								<liferay-ui:csp>
									<a aria-label="<%= LanguageUtil.format(request, "page-x", cur - 1) %>" class="page-link" href="<%= _getHREF(formName, namespace + curParam, cur - 1, jsCall, url, urlAnchor) %>" onclick="<%= forcePost ? _getOnClick(namespace, curParam, cur - 1) : "" %>"><%= cur - 1 %></a>
								</liferay-ui:csp>
							</li>
						</c:if>

						<li class="active page-item">
							<a aria-current="page" aria-label="<%= LanguageUtil.format(request, "page-x", cur) %>" class="page-link" href="<%= _getHREF(formName, namespace + curParam, cur, jsCall, url, urlAnchor) %>" tabindex="0"><%= cur %></a>
						</li>

						<c:if test="<%= (cur + 1) < pages %>">
							<li class="page-item">
								<liferay-ui:csp>
									<a aria-label="<%= LanguageUtil.format(request, "page-x", cur + 1) %>" class="page-link" href="<%= _getHREF(formName, namespace + curParam, cur + 1, jsCall, url, urlAnchor) %>" onclick="<%= forcePost ? _getOnClick(namespace, curParam, cur + 1) : "" %>"><%= cur + 1 %></a>
								</liferay-ui:csp>
							</li>
						</c:if>

						<c:if test="<%= (cur + 3) < pages %>">
							<li class="dropdown page-item">
								<button aria-controls="dropdown-pages-4" aria-haspopup="true" class="dropdown-toggle page-link" data-toggle="liferay-dropdown" title="<%= LanguageUtil.get(request, "show-intermediate-pages") %>">
									<span aria-hidden="true">...</span>

									<span class="sr-only"><liferay-ui:message key="intermediate-pages" />&nbsp;<liferay-ui:message key="use-tab-to-navigate" /></span>
								</button>

								<div class="dropdown-menu dropdown-menu-top dropdown-menu-width-shrink">
									<ul aria-expanded="false" class="inline-scroller link-list" data-current-index="<%= cur + 2 %>" id="dropdown-pages-4">
						</c:if>

						<%
						int remainingPages = ((pages - (cur + 2)) < initialPages) ? (pages - (cur + 2)) : initialPages;

						for (int i = cur + 2; i < ((cur + 2) + remainingPages); i++) {
						%>

							<li class="<%= ((cur + 3) < pages) ? "" : "page-item" %>" role="presentation">
								<liferay-ui:csp>
									<a aria-label="<%= LanguageUtil.format(request, "page-x", i) %>" class="<%= ((cur + 3) < pages) ? "dropdown-item" : "dropdown-item page-link" %>" href="<%= _getHREF(formName, namespace + curParam, i, jsCall, url, urlAnchor) %>" id="<%= randomNamespace + String.valueOf(i) %>" onclick="<%= forcePost ? _getOnClick(namespace, curParam, i) : "" %>" role="menuitem"><%= i %></a>
								</liferay-ui:csp>
							</li>

						<%
						}
						%>

						<c:if test="<%= (cur + 3) < pages %>">
									</ul>
								</div>
							</li>
						</c:if>

						<li class="page-item">
							<liferay-ui:csp>
								<a aria-label="<%= LanguageUtil.format(request, "page-x", pages) %>" class="page-link" href="<%= _getHREF(formName, namespace + curParam, pages, jsCall, url, urlAnchor) %>" onclick="<%= forcePost ? _getOnClick(namespace, curParam, pages) : "" %>" role="menuitem"><%= pages %></a>
							</liferay-ui:csp>
						</li>
					</c:otherwise>
				</c:choose>

				<li class="page-item <%= (cur < pages) ? StringPool.BLANK : "disabled" %>">
					<liferay-ui:csp>
						<c:choose>
							<c:when test="<%= cur < pages %>">
								<a class="lfr-portal-tooltip page-link" href="<%= _getHREF(formName, namespace + curParam, cur + 1, jsCall, url, urlAnchor) %>" onclick="<%= forcePost ? _getOnClick(namespace, curParam, cur + 1) : "" %>" title="<%= LanguageUtil.get(request, "next-page") %>">
							</c:when>
							<c:otherwise>
								<div class="page-link">
							</c:otherwise>
						</c:choose>

							<liferay-ui:icon
								icon='<%= PortalUtil.isRightToLeft(request) ? "angle-left" : "angle-right" %>'
								markupView="lexicon"
							/>

						<c:choose>
							<c:when test="<%= cur < pages %>">
								</a>
							</c:when>
							<c:otherwise>
								</div>
							</c:otherwise>
						</c:choose>
					</liferay-ui:csp>
				</li>
			</ul>
		</nav>
	</div>
</c:if>

<c:if test="<%= pages > initialPages %>">
	<aui:script sandbox="<%= true %>">
		Liferay.component(
			'<%= randomNamespace %>dynamicInlineScroll',
			new Liferay.Util.DynamicInlineScroll(
				{
					applyNamespaceToCurParam: <%= Validator.isNotNull(namespace) %>,
					cur: '<%= cur %>',
					curParam: '<%= curParam %>',
					forcePost: <%= forcePost %>,
					formName: '<%= formName %>',
					initialPages: '<%= initialPages %>',
					jsCall: '<%= jsCall %>',
					namespace: '<%= namespace %>',
					pages: '<%= pages %>',
					randomNamespace: '<%= randomNamespace %>',
					url: '<%= HtmlUtil.escapeJS(HttpComponentsUtil.removeParameter(url, namespace + curParam)) %>',
					urlAnchor: '<%= urlAnchor %>'
				}
			),
			{
				portletId: '<%= portletDisplay.getId() %>'
			}
		);
	</aui:script>
</c:if>

<aui:script>
	function <portlet:namespace />submitForm(curParam, cur) {
		var data = {};

		data[curParam] = cur;

		Liferay.Util.postForm(
			document.<%= randomNamespace + namespace %>pageIteratorFm,
			{
				data: data
			}
		);
	}
</aui:script>

<%!
private String _getHREF(String formName, String curParam, int cur, String jsCall, String url, String urlAnchor) throws Exception {
	if (Validator.isNotNull(url)) {
		return HtmlUtil.escapeHREF(HttpComponentsUtil.addParameter(HttpComponentsUtil.removeParameter(url, curParam) + urlAnchor, curParam, cur));
	}

	return "javascript:document." + formName + "." + curParam + ".value = '" + cur + "'; " + jsCall;
}

private String _getOnClick(String namespace, String curParam, int cur) {
	return "event.preventDefault(); " + namespace + "submitForm('" + namespace + curParam + "','" + cur + "');";
}
%>