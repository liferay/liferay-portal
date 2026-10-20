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
int maxPages = GetterUtil.getInteger((String)request.getAttribute("liferay-ui:page-iterator:maxPages"));
PortletURL portletURL = (PortletURL)request.getAttribute("liferay-ui:page-iterator:portletURL");
String target = (String)request.getAttribute("liferay-ui:page-iterator:target");
int total = GetterUtil.getInteger((String)request.getAttribute("liferay-ui:page-iterator:total"));
String type = (String)request.getAttribute("liferay-ui:page-iterator:type");
String url = StringPool.BLANK;
String urlAnchor = StringPool.BLANK;
int pages = GetterUtil.getInteger((String)request.getAttribute("liferay-ui:page-iterator:pages"));

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

int resultRowsSize = delta;

if (total < delta) {
	resultRowsSize = total;
}
else {
	resultRowsSize = total - ((cur - 1) * delta);

	if (resultRowsSize > delta) {
		resultRowsSize = delta;
	}
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

	<form action="<%= HtmlUtil.escapeAttribute(url) %>" id="<%= randomNamespace + namespace %>pageIteratorFm" method="post" name="<%= randomNamespace + namespace %>pageIteratorFm">
		<aui:input name="<%= curParam %>" type="hidden" />
		<liferay-portlet:renderURLParams portletURL="<%= portletURL %>" />
	</form>
</c:if>

<c:if test='<%= type.equals("approximate") || type.equals("more") || type.equals("regular") || (type.equals("article") && (total > resultRowsSize)) %>'>
	<div class="taglib-page-iterator" id="<%= namespace + id %>">
</c:if>

<c:if test='<%= type.equals("article") && (total > resultRowsSize) %>'>
	<div class="search-results">
		<liferay-ui:message key="pages" />:

		<%
		int pagesIteratorMax = maxPages;
		int pagesIteratorBegin = 1;
		int pagesIteratorEnd = pages;

		if (pages > pagesIteratorMax) {
			pagesIteratorBegin = cur - pagesIteratorMax;
			pagesIteratorEnd = cur + pagesIteratorMax;

			if (pagesIteratorBegin < 1) {
				pagesIteratorBegin = 1;
			}

			if (pagesIteratorEnd > pages) {
				pagesIteratorEnd = pages;
			}
		}

		String content = null;

		if (pagesIteratorEnd < pagesIteratorBegin) {
			content = StringPool.BLANK;
		}
		else {
			StringBundler sb = new StringBundler((pagesIteratorEnd - pagesIteratorBegin + 1) * 8);

			for (int i = pagesIteratorBegin; i <= pagesIteratorEnd; i++) {
				if (i == cur) {
					sb.append("<strong class='journal-article-page-number'>");
					sb.append(i);
					sb.append("</strong>");
				}
				else {
					sb.append("<a class='journal-article-page-number' href='");
					sb.append(_getHREF(formName, namespace + curParam, i, jsCall, url, urlAnchor));

					if (forcePost) {
						sb.append("' onClick='");
						sb.append(_getOnClick(namespace, curParam, i));
					}

					sb.append("'>");
					sb.append(i);
					sb.append("</a>");
				}

				sb.append("&nbsp;&nbsp;");
			}

			content = sb.toString();
		}
		%>

		<%= ContentSecurityPolicyHTMLRewriterUtil.rewriteInlineAttributes(content, request, false) %>
	</div>
</c:if>

<c:if test="<%= (total > delta) || (total > PropsValues.SEARCH_CONTAINER_PAGE_DELTA_VALUES[0]) %>">
	<div class="clearfix lfr-pagination">
		<c:if test='<%= type.equals("regular") %>'>
			<c:if test="<%= PropsValues.SEARCH_CONTAINER_PAGE_DELTA_VALUES.length > 0 %>">
				<div class="lfr-pagination-config">
					<div class="lfr-pagination-page-selector">

						<%
						String suffix = LanguageUtil.get(resourceBundle, "of") + StringPool.SPACE + numberFormat.format(pages);

						if (type.equals("approximate") || type.equals("more")) {
							suffix = StringPool.BLANK;
						}
						%>

						<liferay-ui:icon-menu
							cssClass="current-page-menu"
							direction="down"
							icon=""
							message='<%= LanguageUtil.get(resourceBundle, "page") + StringPool.SPACE + cur + StringPool.SPACE + suffix %>'
							showWhenSingleIcon="<%= true %>"
						>

							<%
							int pagesIteratorMax = maxPages;
							int pagesIteratorBegin = 1;
							int pagesIteratorEnd = pages;

							if (pages > pagesIteratorMax) {
								pagesIteratorBegin = cur - pagesIteratorMax;
								pagesIteratorEnd = cur + pagesIteratorMax;

								if (pagesIteratorBegin < 1) {
									pagesIteratorBegin = 1;
								}

								if (pagesIteratorEnd > pages) {
									pagesIteratorEnd = pages;
								}
							}

							for (int i = pagesIteratorBegin; i <= pagesIteratorEnd; i++) {
							%>

								<liferay-ui:icon
									message="<%= String.valueOf(i) %>"
									onClick='<%= forcePost ? _getOnClick(namespace, curParam, i) : "" %>'
									url="<%= HtmlUtil.escapeJSLink(HttpComponentsUtil.setParameter(url + urlAnchor, namespace + curParam, i)) %>"
								/>

							<%
							}
							%>

						</liferay-ui:icon-menu>
					</div>

					<div class="lfr-pagination-delta-selector">
						<c:choose>
							<c:when test="<%= !deltaConfigurable %>">
								&mdash;

								<liferay-ui:message arguments="<%= delta %>" key="x-items-per-page" />
							</c:when>
							<c:otherwise>
								<liferay-ui:icon-menu
									direction="down"
									icon=""
									message='<%= LanguageUtil.format(request, "x-items-per-page", delta) %>'
									showWhenSingleIcon="<%= true %>"
								>

									<%
									for (int curDelta : PropsValues.SEARCH_CONTAINER_PAGE_DELTA_VALUES) {
										if (curDelta > SearchContainer.MAX_DELTA) {
											continue;
										}

										String curDeltaURL = HttpComponentsUtil.setParameter(url + urlAnchor, namespace + deltaParam, curDelta);
									%>

										<liferay-ui:icon
											message="<%= String.valueOf(curDelta) %>"
											onClick='<%= forcePost ? _getOnClick(namespace, deltaParam, curDelta) : "" %>'
											url="<%= HtmlUtil.escapeJSLink(curDeltaURL) %>"
										/>

									<%
									}
									%>

								</liferay-ui:icon-menu>
							</c:otherwise>
						</c:choose>
					</div>
				</c:if>
			</div>
		</c:if>

		<c:if test='<%= type.equals("approximate") || type.equals("more") || type.equals("regular") %>'>
			<%@ include file="/html/taglib/ui/page_iterator/deprecated/showing_x_results.jspf" %>
		</c:if>

		<ul class="lfr-pagination-buttons pagination">
			<c:if test='<%= type.equals("approximate") || type.equals("more") || type.equals("regular") %>'>
				<li class="<%= (cur != 1) ? "" : "disabled" %> first page-item">
					<liferay-ui:csp>
						<a href="<%= (cur != 1) ? _getHREF(formName, namespace + curParam, 1, jsCall, url, urlAnchor) : "javascript:void(0);" %>" page-link onclick="<%= ((cur != 1) && forcePost) ? _getOnClick(namespace, curParam, 1) : "" %>" tabIndex="<%= (cur != 1) ? "0" : "-1" %>" target="<%= target %>">
							<%= PortalUtil.isRightToLeft(request) ? "&rarr;" : "&larr;" %> <liferay-ui:message key="first[pagination]" />
						</a>
					</liferay-ui:csp>
				</li>
			</c:if>

			<li class="<%= (cur != 1) ? "" : "disabled" %> page-item">
				<liferay-ui:csp>
					<a href="<%= (cur != 1) ? _getHREF(formName, namespace + curParam, cur - 1, jsCall, url, urlAnchor) : "javascript:void(0);" %>" page-link onclick="<%= ((cur != 1) && forcePost) ? _getOnClick(namespace, curParam, cur - 1) : "" %>" tabIndex="<%= (cur != 1) ? "0" : "-1" %>" target="<%= target %>">
						<liferay-ui:message key="previous" />
					</a>
				</liferay-ui:csp>
			</li>
			<li class="<%= (cur != pages) ? "" : "disabled" %> page-item">
				<liferay-ui:csp>
					<a href="<%= (cur != pages) ? _getHREF(formName, namespace + curParam, cur + 1, jsCall, url, urlAnchor) : "javascript:void(0);" %>" page-link onclick="<%= ((cur != pages) && forcePost) ? _getOnClick(namespace, curParam, cur + 1) : "" %>" tabIndex="<%= (cur != pages) ? "0" : "-1" %>" target="<%= target %>">
						<c:choose>
							<c:when test='<%= type.equals("approximate") || type.equals("more") %>'>
								<liferay-ui:message key="more" />
							</c:when>
							<c:otherwise>
								<liferay-ui:message key="next" />
							</c:otherwise>
						</c:choose>
					</a>
				</liferay-ui:csp>
			</li>

			<c:if test='<%= type.equals("regular") %>'>
				<li class="<%= (cur != pages) ? "" : "disabled" %> last page-item">
					<liferay-ui:csp>
						<a href="<%= (cur != pages) ? _getHREF(formName, namespace + curParam, pages, jsCall, url, urlAnchor) : "javascript:void(0);" %>" page-link onclick="<%= ((cur != pages) && forcePost) ? _getOnClick(namespace, curParam, pages) : "" %>" tabIndex="<%= (cur != pages) ? "0" : "-1" %>" target="<%= target %>">
							<liferay-ui:message key="last" /> <%= PortalUtil.isRightToLeft(request) ? "&larr;" : "&rarr;" %>
						</a>
					</liferay-ui:csp>
				</li>
			</c:if>
		</ul>
	</div>
</c:if>

<c:if test='<%= type.equals("approximate") || type.equals("more") || type.equals("regular") || (type.equals("article") && (total > resultRowsSize)) %>'>
	</div>
</c:if>

<aui:script>
	function <portlet:namespace />submitForm(curParam, cur) {
		var data = {};

		data[curParam] = cur;

		Liferay.Util.postForm(
			document.<%= randomNamespace + namespace %>pageIteratorFm,
			{
				data: data,
			}
		);
	}
</aui:script>

<%!
private String _getHREF(String formName, String curParam, int cur, String jsCall, String url, String urlAnchor) throws Exception {
	String href = null;

	if (Validator.isNotNull(url)) {
		href = HtmlUtil.escapeHREF(HttpComponentsUtil.addParameter(HttpComponentsUtil.removeParameter(url, curParam) + urlAnchor, curParam, cur));
	}
	else {
		href = "javascript:document." + formName + "." + curParam + ".value = '" + cur + "'; " + jsCall;
	}

	return href;
}

private String _getOnClick(String namespace, String curParam, int cur) {
	return "event.preventDefault(); " + namespace + "submitForm('" + namespace + curParam + "','" + cur + "');";
}
%>