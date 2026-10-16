<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
portletDisplay.setURLBack(editAssetListDisplayContext.getBackURL());
portletDisplay.setURLBackTitle(ParamUtil.getString(request, "backURLTitle"));

AssetListEntry assetListEntry = assetListDisplayContext.getAssetListEntry();
%>

<portlet:actionURL name="/asset_list/add_asset_entry_selection" var="addAssetEntrySelectionURL">
	<portlet:param name="mvcPath" value="/edit_asset_list_entry.jsp" />
</portlet:actionURL>

<portlet:actionURL name="/asset_list/update_asset_list_entry_manual" var="updateAssetListEntryURL" />

<c:choose>
	<c:when test="<%= Validator.isNull(assetListEntry.getAssetEntryType()) %>">
		<liferay-frontend:edit-form
			action="<%= updateAssetListEntryURL %>"
			cssClass="pt-0"
			fluid="<%= true %>"
			method="post"
			name="fm"
		>
			<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
			<aui:input name="backURL" type="hidden" value="<%= editAssetListDisplayContext.getBackURL() %>" />
			<aui:input name="assetListEntryId" type="hidden" value="<%= assetListDisplayContext.getAssetListEntryId() %>" />

			<div class="mb-3 text-muted">
				<liferay-ui:message key="choose-the-asset-type-you-want-to-use-for-this-manual-collection" />
			</div>

			<liferay-frontend:edit-form-body>
				<liferay-util:include page="/asset_list/source.jsp" servletContext="<%= application %>" />
			</liferay-frontend:edit-form-body>

			<c:if test="<%= !editAssetListDisplayContext.isLiveGroup() %>">
				<liferay-frontend:edit-form-footer>
					<liferay-frontend:edit-form-buttons
						redirect="<%= editAssetListDisplayContext.getBackURL() %>"
						submitDisabled="<%= editAssetListDisplayContext.isNoAssetTypeSelected() %>"
						submitId="saveButton"
						submitOnClick='<%= liferayPortletResponse.getNamespace() + "saveSelectBoxes();" %>'
					/>
				</liferay-frontend:edit-form-footer>
			</c:if>
		</liferay-frontend:edit-form>
	</c:when>
	<c:otherwise>
		<liferay-frontend:edit-form
			action="<%= addAssetEntrySelectionURL %>"
			cssClass="pt-0"
			fluid="<%= true %>"
			method="post"
			name="fm"
		>
			<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
			<aui:input name="backURL" type="hidden" value="<%= editAssetListDisplayContext.getBackURL() %>" />
			<aui:input name="assetListEntryId" type="hidden" value="<%= assetListDisplayContext.getAssetListEntryId() %>" />
			<aui:input name="segmentsEntryId" type="hidden" value="<%= assetListDisplayContext.getSegmentsEntryId() %>" />
			<aui:input name="assetEntryIds" type="hidden" />

			<liferay-frontend:edit-form-body>
				<h3 class="sheet-title">
					<clay:content-row
						verticalAlign="center"
					>
						<clay:content-col>
							<%= HtmlUtil.escape(editAssetListDisplayContext.getSegmentsEntryName(editAssetListDisplayContext.getSegmentsEntryId(), locale)) %>
						</clay:content-col>

						<clay:content-col
							cssClass="inline-item-after"
						>

							<%
							AssetListEntryVariationActionDropdownItemsProvider assetListEntryVariationActionDropdownItemsProvider = new AssetListEntryVariationActionDropdownItemsProvider(editAssetListDisplayContext, liferayPortletRequest, liferayPortletResponse);
							%>

							<clay:dropdown-actions
								aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
								dropdownItems="<%= assetListEntryVariationActionDropdownItemsProvider.getActionDropdownItems() %>"
								propsTransformer="{AssetListEntryVariationDefaultPropsTransformer} from asset-list-web"
								title='<%= LanguageUtil.get(request, "show-actions") %>'
							/>
						</clay:content-col>
					</clay:content-row>
				</h3>

				<h3 class="sheet-title text-uppercase">
					<clay:content-row
						containerElement="span"
						noGutters="true"
					>
						<clay:content-col
							containerElement="span"
							expand="<%= true %>"
						>
							<span class="heading-text">
								<liferay-ui:message key="collection-items" />
							</span>
						</clay:content-col>

						<c:if test="<%= !editAssetListDisplayContext.isLiveGroup() %>">
							<clay:content-col
								containerElement="span"
							>
								<clay:button
									additionalProps='<%=
										HashMapBuilder.<String, Object>put(
											"assetEntryTypes", editAssetListDisplayContext.getAssetEntryTypesJSONArray()
										).put(
											"groupIds", editAssetListDisplayContext.getConnectedGroupIdsString()
										).build()
									%>'
									aria-label='<%= LanguageUtil.get(request, "select-items") %>'
									cssClass="btn btn-secondary btn-sm"
									label='<%= LanguageUtil.get(request, "select") %>'
									propsTransformer="{EditAssetListEntryManualDefaultPropsTransformer} from asset-list-web"
								/>
							</clay:content-col>
						</c:if>
					</clay:content-row>
				</h3>

				<liferay-ui:search-container
					compactEmptyResultsMessage="<%= true %>"
					emptyResultsMessage="no-collection-items-are-selected"
					id="assetEntriesSearchContainer"
					searchContainer="<%= editAssetListDisplayContext.getSearchContainer() %>"
				>
					<liferay-ui:search-container-row
						className="com.liferay.asset.list.model.AssetListEntryAssetEntryRel"
						escapedModel="<%= true %>"
						keyProperty="entryId"
						modelVar="assetListEntryAssetEntryRel"
					>

						<%
						AssetEntry assetEntry = AssetEntryServiceUtil.getEntry(assetListEntryAssetEntryRel.getAssetEntryId());

						AssetRendererFactory<?> assetRendererFactory = AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(assetEntry.getClassName());

						AssetRenderer<?> assetRenderer = assetRendererFactory.getAssetRenderer(assetEntry.getClassPK(), AssetRendererFactory.TYPE_LATEST);
						%>

						<liferay-ui:search-container-column-text
							name="title"
							truncate="<%= true %>"
						>
							<div class="d-flex">
								<%= HtmlUtil.escape(assetRenderer.getTitle(locale)) %>

								<c:if test="<%= !assetEntry.isVisible() %>">
									(<div class="ml-1">
										<liferay-portal-workflow:status
											showStatusLabel="<%= false %>"
											status="<%= assetRenderer.getStatus() %>"
											statusMessage='<%= (assetRenderer.getStatus() == 0) ? "not-visible" : WorkflowConstants.getStatusLabel(assetRenderer.getStatus()) %>'
										/>
									</div>)
								</c:if>
							</div>
						</liferay-ui:search-container-column-text>

						<liferay-ui:search-container-column-text
							name="type"
							value="<%= assetRendererFactory.getTypeName(locale) %>"
						/>

						<liferay-ui:search-container-column-date
							name="modified-date"
							value="<%= assetEntry.getModifiedDate() %>"
						/>

						<c:if test="<%= !editAssetListDisplayContext.isLiveGroup() %>">
							<liferay-ui:search-container-column-jsp
								path="/asset_list/asset_selection_order_up_action.jsp"
							/>

							<liferay-ui:search-container-column-jsp
								path="/asset_list/asset_selection_order_down_action.jsp"
							/>

							<liferay-ui:search-container-column-jsp
								path="/asset_list/asset_selection_action.jsp"
							/>
						</c:if>
					</liferay-ui:search-container-row>

					<liferay-ui:search-iterator
						markupView="lexicon"
					/>
				</liferay-ui:search-container>
			</liferay-frontend:edit-form-body>
		</liferay-frontend:edit-form>
	</c:otherwise>
</c:choose>