<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

if (Validator.isNull(redirect)) {
	PortletURL portletURL = renderResponse.createRenderURL();

	redirect = portletURL.toString();
}

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle(assetListDisplayContext.getAssetListEntryTitle());
%>

<c:if test="<%= !editAssetListDisplayContext.isSegmentationEnabled(company.getCompanyId()) %>">
	<clay:stripe
		defaultTitleDisabled="<%= true %>"
		dismissible="<%= true %>"
		displayType="warning"
	>
		<strong><liferay-ui:message key="personalized-variations-cannot-be-displayed-because-segmentation-is-disabled" /></strong>

		<%
		String segmentsConfigurationURL = editAssetListDisplayContext.getSegmentsCompanyConfigurationURL();
		%>

		<c:choose>
			<c:when test="<%= segmentsConfigurationURL != null %>">
				<clay:link
					href="<%= segmentsConfigurationURL %>"
					label="to-enable,-go-to-instance-settings"
				/>
			</c:when>
			<c:otherwise>
				<span><liferay-ui:message key="contact-your-system-administrator-to-enable-it" /></span>
			</c:otherwise>
		</c:choose>
	</clay:stripe>
</c:if>

<clay:container-fluid
	cssClass="container-view"
>

	<%
	AssetListEntry assetListEntry = assetListDisplayContext.getAssetListEntry();
	%>

	<clay:row>
		<clay:col
			lg="3"
		>
			<div>
				<span aria-hidden="true" class="loading-animation loading-animation-sm mt-4"></span>

				<c:choose>
					<c:when test='<%= FeatureFlagManagerUtil.isEnabled(company.getCompanyId(), "LPD-109242") %>'>
						<react:component
							module="{AudienceVariationsNav} from asset-list-web"
							props="<%= editAssetListDisplayContext.getAudienceVariationsData() %>"
						/>
					</c:when>
					<c:otherwise>
						<react:component
							module="{VariationsNav} from asset-list-web"
							props="<%= editAssetListDisplayContext.getData() %>"
						/>
					</c:otherwise>
				</c:choose>
			</div>
		</clay:col>

		<clay:col
			lg="9"
		>
			<c:choose>
				<c:when test="<%= assetListEntry.getType() == AssetListEntryTypeConstants.TYPE_DYNAMIC %>">
					<liferay-util:include page="/edit_asset_list_entry_dynamic.jsp" servletContext="<%= application %>" />
				</c:when>
				<c:otherwise>
					<liferay-util:include page="/edit_asset_list_entry_manual.jsp" servletContext="<%= application %>" />
				</c:otherwise>
			</c:choose>
		</clay:col>
	</clay:row>
</clay:container-fluid>

<aui:script>
	<portlet:actionURL name="/asset_list/add_asset_list_entry_variation" var="addAssetListEntryVariationURL">
		<portlet:param name="assetListEntryId" value="<%= String.valueOf(editAssetListDisplayContext.getAssetListEntryId()) %>" />
		<portlet:param name="type" value="<%= String.valueOf(editAssetListDisplayContext.getAssetListEntryType()) %>" />
	</portlet:actionURL>

	function <portlet:namespace />openSelectSegmentsEntryDialog() {
		Liferay.Util.openSelectionModal({
			id: '<portlet:namespace />selectEntity',
			onSelect: function (event) {
				const valueJSON = JSON.parse(event.value);

				Liferay.Util.postForm(document.<portlet:namespace />fm, {
					data: {
						segmentsEntryId: valueJSON.segmentsEntryId,
					},
					url: '<%= addAssetListEntryVariationURL %>',
				});
			},
			selectEventName: '<portlet:namespace />selectEntity',
			title: '<liferay-ui:message arguments="personalized-variation" key="new-x" />',
			url: '<%= editAssetListDisplayContext.getSelectSegmentsEntryURL() %>',
		});
	}

	function <portlet:namespace />saveSelectBoxes() {
		var form = document.<portlet:namespace />fm;

		<%
		List<AssetRendererFactory<?>> assetRendererFactories = ListUtil.sort(AssetRendererFactoryRegistryUtil.getAssetRendererFactories(company.getCompanyId()), new AssetRendererFactoryTypeNameComparator(locale));

		for (AssetRendererFactory<?> assetRendererFactory : assetRendererFactories) {
			ClassTypeReader classTypeReader = assetRendererFactory.getClassTypeReader();

			List<ClassType> classTypes = classTypeReader.getAvailableClassTypes(editAssetListDisplayContext.getReferencedModelsGroupIds(), locale);

			if (classTypes.isEmpty()) {
				continue;
			}

			String className = assetListDisplayContext.getClassName(assetRendererFactory);
		%>

			Liferay.Util.setFormValues(form, {
				classTypeIds<%= className %>: Liferay.Util.getSelectedOptionValues(
					Liferay.Util.getFormElement(
						form,
						'<%= className %>currentClassTypeIds'
					)
				),
			});

		<%
		}
		%>

		var currentClassNameIdsSelect = Liferay.Util.getFormElement(
			form,
			'currentClassNameIds'
		);

		if (currentClassNameIdsSelect) {
			Liferay.Util.postForm(form, {
				data: {
					classNameIds: Liferay.Util.getSelectedOptionValues(
						currentClassNameIdsSelect
					),
				},
			});
		}
		else {
			submitForm(form);
		}
	}
</aui:script>