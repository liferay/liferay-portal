<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
List<AssetRendererFactory<?>> classTypesAssetRendererFactories = new ArrayList<>();
List<Map<String, Object>> classTypesList = new ArrayList<>();
%>

<liferay-frontend:fieldset
	cssClass="source-container"
	disabled="<%= editAssetListDisplayContext.isLiveGroup() %>"
>

	<%

	// Left list

	List<KeyValuePair> typesLeftList = new ArrayList<KeyValuePair>();

	long[] classNameIds = ArrayUtil.clone(editAssetListDisplayContext.getClassNameIds());

	Arrays.sort(classNameIds);
	%>

	<aui:select helpMessage="changing-this-setting-will-reset-all-mappings-for-this-collection" label="item-type" name="TypeSettingsProperties--anyAssetType--" title="item-type">
		<aui:option label='<%= StringPool.DASH + LanguageUtil.get(request, "not-selected") + StringPool.DASH %>' selected="<%= editAssetListDisplayContext.isNoAssetTypeSelected() %>" value="" />

		<optgroup label="<liferay-ui:message key="single-item-type" />">

			<%
			for (long classNameId : editAssetListDisplayContext.getAvailableClassNameIds()) {
				ClassName className = ClassNameLocalServiceUtil.getClassName(classNameId);

				ObjectDefinition objectDefinition = _fetchObjectDefinition(className, company);

				String label = _getLabel(className, locale, objectDefinition);

				if (Arrays.binarySearch(classNameIds, classNameId) < 0) {
					typesLeftList.add(new KeyValuePair(String.valueOf(classNameId), label));
				}
			%>

				<aui:option data-object="<%= objectDefinition != null %>" label="<%= label %>" selected="<%= (classNameIds.length == 1) && (classNameId == classNameIds[0]) %>" value="<%= classNameId %>" />

			<%
			}
			%>

		</optgroup>

		<optgroup label="<liferay-ui:message key="multiple-item-types" />">
			<aui:option label='<%= LanguageUtil.get(request, "select-types") %>' selected="<%= !editAssetListDisplayContext.isAnyAssetType() && !editAssetListDisplayContext.isNoAssetTypeSelected() && (classNameIds.length > 1) %>" value="<%= false %>" />
			<aui:option label="all-types" selected="<%= editAssetListDisplayContext.isAnyAssetType() %>" value="<%= true %>" />
		</optgroup>
	</aui:select>

	<aui:input name="TypeSettingsProperties--classNameIds--" type="hidden" />

	<%
	typesLeftList = ListUtil.sort(typesLeftList, new KeyValuePairComparator(false, true));

	// Right list

	List<KeyValuePair> typesRightList = new ArrayList<KeyValuePair>();

	for (long classNameId : editAssetListDisplayContext.getClassNameIds()) {
		ClassName className = ClassNameLocalServiceUtil.getClassName(classNameId);

		typesRightList.add(new KeyValuePair(String.valueOf(classNameId), _getLabel(className, locale, _fetchObjectDefinition(className, company))));
	}
	%>

	<div class="<%= editAssetListDisplayContext.isAnyAssetType() ? "hide" : "" %>" id="<portlet:namespace />classNamesBoxes">
		<liferay-ui:input-move-boxes
			leftBoxName="availableClassNameIds"
			leftList="<%= typesLeftList %>"
			leftTitle="available"
			rightBoxName="currentClassNameIds"
			rightList="<%= typesRightList %>"
			rightReorder="<%= Boolean.TRUE.toString() %>"
			rightTitle="in-use"
		/>
	</div>

	<%
	UnicodeProperties unicodeProperties = editAssetListDisplayContext.getUnicodeProperties();

	List<AssetRendererFactory<?>> assetRendererFactories = ListUtil.sort(AssetRendererFactoryRegistryUtil.getAssetRendererFactories(company.getCompanyId()), new AssetRendererFactoryTypeNameComparator(locale));

	for (AssetRendererFactory<?> assetRendererFactory : assetRendererFactories) {
		ClassTypeReader classTypeReader = assetRendererFactory.getClassTypeReader();

		List<ClassType> classTypes = classTypeReader.getAvailableClassTypes(editAssetListDisplayContext.getReferencedModelsGroupIds(), locale);

		if (classTypes.isEmpty()) {
			continue;
		}

		classTypes.sort(new ClassTypeNameComparator(locale));

		classTypesAssetRendererFactories.add(assetRendererFactory);

		String className = editAssetListDisplayContext.getClassName(assetRendererFactory);

		// Left list

		List<KeyValuePair> subtypesLeftList = new ArrayList<KeyValuePair>();

		boolean noAssetSubtypeSelected = false;

		if (Validator.isNull(unicodeProperties.getProperty("anyClassType" + className))) {
			noAssetSubtypeSelected = true;
		}

		boolean anyAssetSubtype = GetterUtil.getBoolean(unicodeProperties.getProperty("anyClassType" + className, Boolean.TRUE.toString()));

		if (noAssetSubtypeSelected) {
			anyAssetSubtype = false;
		}

		Long[] assetSelectedClassTypeIds = ArrayUtil.clone(editAssetListDisplayContext.getClassTypeIds(unicodeProperties, className, classTypes));

		Arrays.sort(assetSelectedClassTypeIds);

		long[] availableClassTypeIds = ListUtil.toLongArray(classTypes, ClassType::getClassTypeId);

		Arrays.sort(availableClassTypeIds);
	%>

		<div class='asset-subtype <%= (assetSelectedClassTypeIds.length < 1) ? StringPool.BLANK : "hide" %>' data-class-name="<%= className %>" id="<portlet:namespace /><%= className %>Options">
			<aui:select helpMessage="changing-this-setting-will-reset-all-mappings-for-this-collection" label='<%= LanguageUtil.get(request, "item-subtype") %>' name='<%= "TypeSettingsProperties--anyClassType" + className + "--" %>'>
				<aui:option label='<%= StringPool.DASH + LanguageUtil.get(request, "not-selected") + StringPool.DASH %>' selected="<%= editAssetListDisplayContext.isNoAssetTypeSelected() %>" value="" />

				<optgroup label="<%= LanguageUtil.get(request, "single-item-subtype") %>">

					<%
					for (ClassType classType : classTypes) {
						if (Arrays.binarySearch(assetSelectedClassTypeIds, classType.getClassTypeId()) < 0) {
							subtypesLeftList.add(new KeyValuePair(String.valueOf(classType.getClassTypeId()), HtmlUtil.escape(classType.getName())));
						}
					%>

						<aui:option label="<%= HtmlUtil.escapeAttribute(classType.getName()) %>" selected="<%= !anyAssetSubtype && (assetSelectedClassTypeIds.length == 1) && (assetSelectedClassTypeIds[0]).equals(classType.getClassTypeId()) && !noAssetSubtypeSelected %>" value="<%= classType.getClassTypeId() %>" />

					<%
					}

					for (Long assetSelectedClassTypeId : assetSelectedClassTypeIds) {
						if (Arrays.binarySearch(availableClassTypeIds, assetSelectedClassTypeId) >= 0) {
							continue;
						}
					%>

						<aui:option label='<%= LanguageUtil.format(request, "id-x", String.valueOf(assetSelectedClassTypeId)) %>' selected="<%= !anyAssetSubtype && (assetSelectedClassTypeIds.length == 1) && !noAssetSubtypeSelected %>" value="<%= assetSelectedClassTypeId %>" />

					<%
					}
					%>

				</optgroup>

				<optgroup label="<%= LanguageUtil.get(request, "multiple-item-subtypes") %>">
					<aui:option label='<%= LanguageUtil.get(request, "select-more-than-one") %>' selected="<%= !anyAssetSubtype && (assetSelectedClassTypeIds.length > 1) && !noAssetSubtypeSelected %>" value="<%= false %>" />
					<aui:option label="all-subtypes" selected="<%= anyAssetSubtype %>" value="<%= true %>" />
				</optgroup>
			</aui:select>

			<aui:input name='<%= "TypeSettingsProperties--classTypeIds" + className + "--" %>' type="hidden" />

			<c:if test="<%= assetListDisplayContext.getAssetListEntryType() == AssetListEntryTypeConstants.TYPE_DYNAMIC %>">
				<div class="asset-subtypefields-wrapper-enable hide" id="<portlet:namespace /><%= className %>subtypeFieldsFilterEnableWrapper">
					<aui:input inlineLabel="right" label="filter-by-field" labelCssClass="simple-toggle-switch" name='<%= "TypeSettingsProperties--subtypeFieldsFilterEnabled" + className + "--" %>' type="toggle-switch" value="<%= editAssetListDisplayContext.isSubtypeFieldsFilterEnabled() %>" />
				</div>

				<span class="asset-subtypefields-message" id="<portlet:namespace /><%= className %>ddmStructureFieldMessage">
					<c:if test="<%= Validator.isNotNull(editAssetListDisplayContext.getDDMStructureFieldLabel()) && (classNameIds[0] == PortalUtil.getClassNameId(assetRendererFactory.getClassName())) %>">
						<%= HtmlUtil.escape(editAssetListDisplayContext.getDDMStructureFieldLabel()) %>: <%= HtmlUtil.escape(editAssetListDisplayContext.getDDMStructureDisplayFieldValue()) %>
					</c:if>
				</span>

				<div class="asset-subtypefields-wrapper hide" id="<portlet:namespace /><%= className %>subtypeFieldsWrapper">

					<%
					for (ClassType classType : classTypes) {
						if (classType.getClassTypeFieldsCount() == 0) {
							continue;
						}
					%>

						<span class="asset-subtypefields hide" data-class-name="<%= className %>" id="<portlet:namespace /><%= classType.getClassTypeId() %>_<%= className %>Options">
							<portlet:renderURL var="selectStructureFieldURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
								<portlet:param name="mvcPath" value="/asset_list/select_structure_field.jsp" />
								<portlet:param name="className" value="<%= assetRendererFactory.getClassName() %>" />
								<portlet:param name="classTypeId" value="<%= String.valueOf(classType.getClassTypeId()) %>" />
								<portlet:param name="eventName" value='<%= liferayPortletResponse.getNamespace() + "selectDDMStructureField" %>' />
							</portlet:renderURL>

							<span class="asset-subtypefields-popup" id="<portlet:namespace /><%= classType.getClassTypeId() %>_<%= className %>PopUpButton">
								<clay:button
									borderless="<%= false %>"
									data-href="<%= selectStructureFieldURL.toString() %>"
									disabled="<%= !editAssetListDisplayContext.isSubtypeFieldsFilterEnabled() %>"
									displayType="secondary"
									label="select"
									type="button"
								/>
							</span>
						</span>

					<%
					}

					typesLeftList = ListUtil.sort(typesLeftList, new KeyValuePairComparator(false, true));
					%>

				</div>
			</c:if>

			<%

			// Right list

			List<KeyValuePair> subtypesRightList = new ArrayList<KeyValuePair>();

			for (long subtypeId : editAssetListDisplayContext.getClassTypeIds(unicodeProperties, className, classTypes)) {
				try {
					ClassType classType = classTypeReader.getClassType(subtypeId, locale);

					subtypesRightList.add(new KeyValuePair(String.valueOf(subtypeId), HtmlUtil.escape(classType.getName())));
				}
				catch (NoSuchModelException noSuchModelException) {
				}
				catch (PortalException portalException) {
					subtypesRightList.add(new KeyValuePair(String.valueOf(subtypeId), String.valueOf(subtypeId)));
				}
			}
			%>

			<div class="<%= (assetSelectedClassTypeIds.length > 1) ? StringPool.BLANK : "hide" %>" id="<portlet:namespace /><%= className %>Boxes">
				<liferay-ui:input-move-boxes
					leftBoxName='<%= className + "availableClassTypeIds" %>'
					leftList="<%= subtypesLeftList %>"
					leftTitle="available"
					rightBoxName='<%= className + "currentClassTypeIds" %>'
					rightList="<%= subtypesRightList %>"
					rightReorder="<%= Boolean.TRUE.toString() %>"
					rightTitle="in-use"
				/>
			</div>
		</div>

	<%
	}

	for (AssetRendererFactory<?> curAssetRendererFactory : classTypesAssetRendererFactories) {
		ClassTypeReader classTypeReader = curAssetRendererFactory.getClassTypeReader();

		List<Map<String, Object>> classSubtypes = new ArrayList<>();

		List<ClassType> assetAvailableClassTypes = classTypeReader.getAvailableClassTypes(editAssetListDisplayContext.getReferencedModelsGroupIds(), locale);

		if (assetAvailableClassTypes.isEmpty()) {
			continue;
		}

		for (ClassType classType : assetAvailableClassTypes) {
			List<ClassTypeField> classTypeFields = classType.getClassTypeFields();

			List<Map<String, Object>> classTypeFieldsList = new ArrayList<>();

			if (classTypeFields.isEmpty()) {
				continue;
			}

			String orderByColumn1 = editAssetListDisplayContext.getOrderByColumn1();
			String orderByColumn2 = editAssetListDisplayContext.getOrderByColumn2();

			for (ClassTypeField classTypeField : classTypeFields) {
				String value = editAssetListDisplayContext.encodeName(classTypeField.getClassTypeId(), classTypeField.getFieldReference(), null);
				String selectedOrderByColumn1 = StringPool.BLANK;
				String selectedOrderByColumn2 = StringPool.BLANK;

				if (orderByColumn1.equals(value)) {
					selectedOrderByColumn1 = "selected";
				}

				if (orderByColumn2.equals(value)) {
					selectedOrderByColumn2 = "selected";
				}

				classTypeFieldsList.add(
					HashMapBuilder.<String, Object>put(
						"label", HtmlUtil.escape(classTypeField.getLabel())
					).put(
						"selectedOrderByColumn1", selectedOrderByColumn1
					).put(
						"selectedOrderByColumn2", selectedOrderByColumn2
					).put(
						"value", value
					).build());
			}

			classSubtypes.add(
				HashMapBuilder.<String, Object>put(
					"classTypeFields", classTypeFieldsList
				).put(
					"classTypeId", classType.getClassTypeId()
				).put(
					"name", HtmlUtil.escape(classType.getName())
				).build());
		}

		classTypesList.add(
			HashMapBuilder.<String, Object>put(
				"className", editAssetListDisplayContext.getClassName(curAssetRendererFactory)
			).put(
				"classNameId", curAssetRendererFactory.getClassNameId()
			).put(
				"classSubtypes", classSubtypes
			).build());
	}
	%>

	<div class="asset-subtypefield-selected <%= Validator.isNull(editAssetListDisplayContext.getDDMStructureFieldName()) ? "hide" : StringPool.BLANK %>">
		<aui:input name="TypeSettingsProperties--ddmStructureFieldName--" type="hidden" value="<%= editAssetListDisplayContext.getDDMStructureFieldName() %>" />

		<aui:input name="TypeSettingsProperties--ddmStructureFieldValue--" type="hidden" value="<%= editAssetListDisplayContext.getDDMStructureFieldValue() %>" />

		<aui:input name="TypeSettingsProperties--ddmStructureDisplayFieldValue--" type="hidden" value="<%= editAssetListDisplayContext.getDDMStructureDisplayFieldValue() %>" />
	</div>
</liferay-frontend:fieldset>

<liferay-frontend:component
	componentId='<%= liferayPortletResponse.getNamespace() + "selectDDMStructureField" %>'
	context='<%=
		HashMapBuilder.<String, Object>put(
			"classTypes", classTypesList
		).put(
			"initialProperties", editAssetListDisplayContext.getTypePropertiesJSONArray()
		).put(
			"propertiesURL", editAssetListDisplayContext.getTypePropertiesURL()
		).build()
	%>'
	module="{Source} from asset-list-web"
/>

<%!
private ObjectDefinition _fetchObjectDefinition(ClassName className, Company company) {
	return ObjectDefinitionLocalServiceUtil.fetchObjectDefinitionByClassName(company.getCompanyId(), className.getValue());
}

private String _getLabel(ClassName className, Locale locale, ObjectDefinition objectDefinition) {
	String label = ResourceActionsUtil.getModelResource(locale, className.getValue());

	if ((objectDefinition != null) && objectDefinition.isCMS()) {
		label = StringUtil.appendParentheticalSuffix(label, "CMS");
	}

	return label;
}
%>