<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
List<FragmentCollection> fragmentCollections = (List<FragmentCollection>)request.getAttribute(FragmentWebKeys.FRAGMENT_COLLECTIONS);
Map<String, List<FragmentCollection>> inheritedFragmentCollections = (Map<String, List<FragmentCollection>>)request.getAttribute(FragmentWebKeys.INHERITED_FRAGMENT_COLLECTIONS);
List<FragmentCollection> systemFragmentCollections = (List<FragmentCollection>)request.getAttribute(FragmentWebKeys.SYSTEM_FRAGMENT_COLLECTIONS);

List<FragmentCollectionContributor> fragmentCollectionContributors = fragmentDisplayContext.getFragmentCollectionContributors(locale);
ImportDisplayContext importDisplayContext = new ImportDisplayContext(request, renderRequest, renderResponse);
%>

<liferay-ui:error embed="<%= false %>" exception="<%= DuplicateFragmentCollectionKeyException.class %>">

	<%
	DuplicateFragmentCollectionKeyException dfcke = (DuplicateFragmentCollectionKeyException)errorException;
	%>

	<liferay-ui:message arguments='<%= "<em>" + dfcke.getMessage() + "</em>" %>' key="a-fragment-set-with-the-key-x-already-exists" />
</liferay-ui:error>

<liferay-ui:error embed="<%= false %>" exception="<%= DuplicateFragmentEntryKeyException.class %>">

	<%
	DuplicateFragmentEntryKeyException dfeke = (DuplicateFragmentEntryKeyException)errorException;
	%>

	<liferay-ui:message arguments='<%= "<em>" + dfeke.getMessage() + "</em>" %>' key="a-fragment-entry-with-the-key-x-already-exists" />
</liferay-ui:error>

<liferay-ui:error embed="<%= false %>" exception="<%= InvalidFileException.class %>" message="the-selected-file-is-not-a-valid-zip-file" />

<liferay-ui:success key="fragmentEntryCopied" message="the-fragment-was-copied-successfully" />

<clay:container-fluid
	cssClass="container-view"
	size="xxxl"
>
	<clay:row>
		<clay:col
			lg="3"
		>
			<portlet:renderURL var="editFragmentCollectionURL">
				<portlet:param name="mvcRenderCommandName" value="/fragment/edit_fragment_collection" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
			</portlet:renderURL>

			<c:choose>
				<c:when test="<%= ListUtil.isNotEmpty(fragmentCollections) || ListUtil.isNotEmpty(fragmentCollectionContributors) || MapUtil.isNotEmpty(inheritedFragmentCollections) %>">
					<clay:content-row
						cssClass="mb-4"
						verticalAlign="center"
					>
						<clay:content-col
							expand="<%= true %>"
						>
							<strong class="text-uppercase">
								<liferay-ui:message key="fragment-sets" />
							</strong>
						</clay:content-col>

						<clay:content-col>
							<ul class="align-items-center navbar-nav">
								<li>
									<c:if test="<%= FragmentPermission.contains(permissionChecker, scopeGroupId, FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES) %>">
										<clay:link
											borderless="<%= true %>"
											cssClass="component-action lfr-portal-tooltip"
											href="<%= editFragmentCollectionURL %>"
											icon="plus"
											title='<%= LanguageUtil.get(request, "add-fragment-set") %>'
											type="button"
										/>
									</c:if>
								</li>

								<c:if test="<%= fragmentDisplayContext.isShowMarketplace() %>">
									<li>
										<div>
											<react:component
												module="{MarketplaceButton} from layout-js-components-web"
												props="<%= fragmentDisplayContext.getMarketplaceProps() %>"
											/>
										</div>
									</li>
								</c:if>

								<li>

									<%
									Map<String, Object> fragmentCollectionsViewContext = fragmentDisplayContext.getFragmentCollectionsViewContext();
									%>

									<clay:dropdown-actions
										additionalProps='<%=
											HashMapBuilder.<String, Object>put(
												"deleteFragmentCollectionURL", fragmentCollectionsViewContext.get("deleteFragmentCollectionURL")
											).put(
												"exportFragmentCollectionsURL", fragmentCollectionsViewContext.get("exportFragmentCollectionsURL")
											).put(
												"importURL", fragmentCollectionsViewContext.get("importURL")
											).put(
												"viewDeleteFragmentCollectionsURL", fragmentCollectionsViewContext.get("viewDeleteFragmentCollectionsURL")
											).put(
												"viewExportFragmentCollectionsURL", fragmentCollectionsViewContext.get("viewExportFragmentCollectionsURL")
											).put(
												"viewImportURL", fragmentCollectionsViewContext.get("viewImportURL")
											).build()
										%>'
										aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
										dropdownItems="<%= fragmentDisplayContext.getCollectionsDropdownItems() %>"
										propsTransformer="{FragmentCollectionViewDefaultPropsTransformer} from fragment-web"
										title='<%= LanguageUtil.get(request, "fragment-sets-options") %>'
									/>
								</li>
							</ul>
						</clay:content-col>
					</clay:content-row>

					<c:if test="<%= ListUtil.isNotEmpty(fragmentCollectionContributors) || ListUtil.isNotEmpty(systemFragmentCollections) %>">
						<span class="text-truncate">
							<liferay-ui:message key="default" />
						</span>

						<clay:vertical-nav
							verticalNavItems="<%= fragmentDisplayContext.getVerticalNavItemList(systemFragmentCollections, fragmentCollectionContributors) %>"
						/>
					</c:if>

					<%
					for (Map.Entry<String, List<FragmentCollection>> entry : inheritedFragmentCollections.entrySet()) {
					%>

						<span class="text-truncate"><%= entry.getKey() %></span>

						<clay:vertical-nav
							verticalNavItems="<%= fragmentDisplayContext.getVerticalNavItemList(entry.getValue()) %>"
						/>

					<%
					}
					%>

					<c:if test="<%= ListUtil.isNotEmpty(fragmentCollections) %>">
						<span class="text-truncate"><%= HtmlUtil.escape(fragmentDisplayContext.getGroupName(scopeGroupId)) %></span>

						<clay:vertical-nav
							verticalNavItems="<%= fragmentDisplayContext.getVerticalNavItemList(fragmentCollections) %>"
						/>
					</c:if>
				</c:when>
				<c:otherwise>
					<p class="text-uppercase">
						<strong><liferay-ui:message key="fragment-sets" /></strong>
					</p>

					<liferay-frontend:empty-result-message
						actionDropdownItems="<%= FragmentPermission.contains(permissionChecker, scopeGroupId, FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES) ? fragmentDisplayContext.getActionDropdownItems() : null %>"
						additionalProps="<%= fragmentDisplayContext.getFragmentCollectionsViewContext() %>"
						animationType="<%= EmptyResultMessageKeys.AnimationType.NONE %>"
						buttonPropsTransformer="{FragmentCollectionViewButtonPropsTransformer} from fragment-web"
						description='<%= LanguageUtil.get(request, "fragment-sets-are-needed-to-create-fragments") %>'
						elementType='<%= LanguageUtil.get(request, "fragment-sets") %>'
						propsTransformer="{FragmentCollectionViewDefaultPropsTransformer} from fragment-web"
						propsTransformerServletContext="<%= application %>"
					/>
				</c:otherwise>
			</c:choose>
		</clay:col>

		<clay:col
			lg="9"
		>

			<%
			FragmentCollectionContributor fragmentCollectionContributor = fragmentDisplayContext.getFragmentCollectionContributor();
			%>

			<c:if test="<%= (fragmentDisplayContext.getFragmentCollection() != null) || (fragmentCollectionContributor != null) %>">
				<clay:sheet
					size="full"
				>
					<h2 class="sheet-title">
						<clay:content-row
							verticalAlign="center"
						>
							<clay:content-col>
								<%= fragmentDisplayContext.getFragmentCollectionName() %>
							</clay:content-col>

							<c:if test="<%= (fragmentCollectionContributor != null) && fragmentCollectionContributor.isDeprecated() %>">
								<div class="c-ml-3">
									<liferay-frontend:feature-indicator
										interactive="<%= true %>"
										type="deprecated"
									/>
								</div>
							</c:if>

							<c:if test="<%= fragmentDisplayContext.isShowFragmentCollectionActions() %>">
								<clay:content-col
									cssClass="inline-item-after"
								>

									<%
									FragmentCollectionActionDropdownItemsProvider fragmentCollectionActionDropdownItemsProvider = new FragmentCollectionActionDropdownItemsProvider(fragmentDisplayContext, request, renderResponse);
									%>

									<clay:dropdown-actions
										aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
										dropdownItems="<%= fragmentCollectionActionDropdownItemsProvider.getActionDropdownItems() %>"
										propsTransformer="{FragmentCollectionDropdownPropsTransformer} from fragment-web"
									/>
								</clay:content-col>
							</c:if>
						</clay:content-row>
					</h2>

					<clay:sheet-section>
						<c:if test="<%= !ListUtil.isEmpty(fragmentDisplayContext.getNavigationItems()) %>">
							<clay:navigation-bar
								navigationItems="<%= fragmentDisplayContext.getNavigationItems() %>"
							/>
						</c:if>

						<c:choose>
							<c:when test="<%= fragmentDisplayContext.isSelectedFragmentCollectionContributor() %>">
								<liferay-util:include page="/view_contributed_fragment_entries.jsp" servletContext="<%= application %>" />
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="<%= fragmentDisplayContext.isViewResources() %>">
										<liferay-util:include page="/view_resources.jsp" servletContext="<%= application %>" />
									</c:when>
									<c:otherwise>
										<liferay-util:include page="/view_fragment_entries.jsp" servletContext="<%= application %>" />
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
					</clay:sheet-section>
				</clay:sheet>
			</c:if>
		</clay:col>
	</clay:row>
</clay:container-fluid>

<aui:form cssClass="hide" name="fm">
</aui:form>

<%
List<String> draftFragmentsImporterResultEntries = importDisplayContext.getFragmentsImporterResultEntries(FragmentsImporterResultEntry.Status.IMPORTED_DRAFT);
%>

<aui:script>
	<c:if test="<%= ListUtil.isNotEmpty(draftFragmentsImporterResultEntries) %>">
		Liferay.Util.openToast({
			message:
				'<liferay-ui:message arguments='<%= "<strong>" + StringUtil.merge(draftFragmentsImporterResultEntries, StringPool.COMMA_AND_SPACE) + "</strong>" %>' key="the-following-fragments-have-validation-issues.-they-have-been-left-in-draft-status-x" />',
			title: '<liferay-ui:message key="warning" />:',
			type: 'warning',
		});
	</c:if>

	<%
	List<String> invalidFragmentsImporterResultEntries = importDisplayContext.getFragmentsImporterResultEntries(FragmentsImporterResultEntry.Status.INVALID);
	%>

	<c:if test="<%= ListUtil.isNotEmpty(invalidFragmentsImporterResultEntries) %>">
		Liferay.Util.openToast({
			message:
				'<liferay-ui:message arguments='<%= "<strong>" + StringUtil.merge(invalidFragmentsImporterResultEntries, StringPool.COMMA_AND_SPACE) + "</strong>" %>' key="the-following-fragments-could-not-be-imported-x" />',
			title: '<liferay-ui:message key="warning" />:',
			type: 'warning',
		});
	</c:if>
</aui:script>