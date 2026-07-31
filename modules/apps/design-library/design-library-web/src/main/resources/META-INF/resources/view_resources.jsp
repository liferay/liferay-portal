<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
DepotEntry depotEntry = (DepotEntry)request.getAttribute(DesignLibraryWebKeys.DESIGN_LIBRARY_ENTRY);

ConnectedSitesDesignLibraryDisplayContext connectedSitesDesignLibraryDisplayContext = new ConnectedSitesDesignLibraryDisplayContext(depotEntry, request);

MembersDesignLibraryDisplayContext membersDesignLibraryDisplayContext = new MembersDesignLibraryDisplayContext(depotEntry, request);

ViewResourcesDesignLibraryDisplayContext viewResourcesDesignLibraryDisplayContext = new ViewResourcesDesignLibraryDisplayContext(depotEntry, request, liferayPortletResponse);
%>

<div>
	<div>
		<react:component
			module="{DesignLibraryBreadcrumb} from design-library-web"
			props="<%= viewResourcesDesignLibraryDisplayContext.getBreadcrumbProps() %>"
		/>
	</div>

	<c:choose>
		<c:when test="<%= viewResourcesDesignLibraryDisplayContext.hasContentAccess() %>">

			<%
			Map<String, Object> fdsAdditionalProps = viewResourcesDesignLibraryDisplayContext.getFDSAdditionalProps();

			Map<String, Object> membersFDSAdditionalProps = membersDesignLibraryDisplayContext.getMembersFDSAdditionalProps();
			%>

			<div class="p-4">
				<div class="row">
					<div class="col-12 col-lg-8">
						<div class="card">
							<div class="card-body">
								<div>
									<react:component
										module="{DesignLibraryAssetsSectionHeader} from design-library-web"
										props="<%= fdsAdditionalProps %>"
									/>
								</div>

								<div class="design-library-fds-wrapper design-library-fds-wrapper--resources">
									<frontend-data-set:headless-display
										additionalProps="<%= fdsAdditionalProps %>"
										apiURL="<%= viewResourcesDesignLibraryDisplayContext.getAPIURL() %>"
										emptyState="<%= viewResourcesDesignLibraryDisplayContext.getEmptyState() %>"
										fdsActionDropdownItems="<%= viewResourcesDesignLibraryDisplayContext.getFDSActionDropdownItems() %>"
										formName="fm"
										id="<%= DesignLibraryAdminFDSNames.DESIGN_LIBRARY_RESOURCES %>"
										propsTransformer="{DesignLibraryAssetsFDSPropsTransformer} from design-library-web"
									/>
								</div>
							</div>
						</div>
					</div>

					<div class="col-12 col-lg-4">
						<div class="card design-library-summary-card mb-4">
							<div class="card-body design-library-members-fds">
								<div>
									<react:component
										module="{DesignLibraryMembersSectionHeader} from design-library-web"
										props="<%= membersDesignLibraryDisplayContext.getMembersSectionHeaderProps() %>"
									/>
								</div>

								<clay:tabs
									tabsItems="<%= membersDesignLibraryDisplayContext.getMembersTabsItems() %>"
								>
									<clay:tabs-panel>
										<div class="design-library-summary-fds">
											<frontend-data-set:headless-display
												additionalProps="<%= membersFDSAdditionalProps %>"
												apiURL="<%= membersDesignLibraryDisplayContext.getMembersUsersAPIURL() %>"
												emptyState="<%= membersDesignLibraryDisplayContext.getMembersEmptyState() %>"
												formName="fm"
												id="<%= DesignLibraryAdminFDSNames.DESIGN_LIBRARY_MEMBERS_USERS %>"
												propsTransformer="{DesignLibraryMembersFDSPropsTransformer} from design-library-web"
												showManagementBar="<%= false %>"
												showPagination="<%= false %>"
												showSearch="<%= false %>"
												showSelectAll="<%= false %>"
												style="fluid"
											/>
										</div>
									</clay:tabs-panel>

									<clay:tabs-panel>
										<div class="design-library-summary-fds">
											<frontend-data-set:headless-display
												additionalProps="<%= membersFDSAdditionalProps %>"
												apiURL="<%= membersDesignLibraryDisplayContext.getMembersUserGroupsAPIURL() %>"
												emptyState="<%= membersDesignLibraryDisplayContext.getMembersEmptyState() %>"
												formName="fm"
												id="<%= DesignLibraryAdminFDSNames.DESIGN_LIBRARY_MEMBERS_USER_GROUPS %>"
												propsTransformer="{DesignLibraryMembersFDSPropsTransformer} from design-library-web"
												showManagementBar="<%= false %>"
												showPagination="<%= false %>"
												showSearch="<%= false %>"
												showSelectAll="<%= false %>"
												style="fluid"
											/>
										</div>
									</clay:tabs-panel>
								</clay:tabs>
							</div>
						</div>

						<div class="card design-library-summary-card">
							<div class="card-body">
								<div>
									<react:component
										module="{DesignLibraryConnectedSitesSectionHeader} from design-library-web"
										props="<%= connectedSitesDesignLibraryDisplayContext.getConnectedSitesSectionHeaderProps() %>"
									/>
								</div>

								<div class="design-library-summary-fds">
									<frontend-data-set:headless-display
										additionalProps="<%= connectedSitesDesignLibraryDisplayContext.getConnectedSitesFDSAdditionalProps() %>"
										apiURL="<%= connectedSitesDesignLibraryDisplayContext.getConnectedSitesAPIURL() %>"
										emptyState="<%= connectedSitesDesignLibraryDisplayContext.getConnectedSitesEmptyState() %>"
										formName="fm"
										id="<%= DesignLibraryAdminFDSNames.DESIGN_LIBRARY_CONNECTED_SITES %>"
										propsTransformer="{DesignLibraryConnectedSitesFDSPropsTransformer} from design-library-web"
										showManagementBar="<%= false %>"
										showPagination="<%= false %>"
										showSearch="<%= false %>"
										showSelectAll="<%= false %>"
										style="fluid"
									/>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<div class="p-4">
				<clay:alert
					displayType="info"
					message="you-do-not-have-access-to-any-content-in-this-design-library"
				/>
			</div>
		</c:otherwise>
	</c:choose>
</div>