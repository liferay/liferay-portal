<#include "../init.ftl">

<#assign alt = "" />

<#if fieldRawValue?has_content>
	<#assign
		fileJSONObject = getFileJSONObject(fieldRawValue)

		alt = fileJSONObject.getString("alt")
		src = fileJSONObject.getString("data")
	/>

	<#if !validator.isNotNull(src)>
		<#attempt>
			<#assign fileEntry = getFileEntry(fileJSONObject) />
			<#recover>
				<#assign fileEntry = "" />
		</#attempt>
	</#if>
</#if>

<@liferay_aui["field-wrapper"] data=data>
	<#if hasFieldValue || showEmptyFieldLabel>
		<label>
			<@liferay_ui.message key=escape(label) />
		</label>
	</#if>

	<#if hasFieldValue>
		[
			<@liferay_ui.csp>
				<a href="javascript:void(0);" id="${portletNamespace}${namespacedFieldName}ToggleImage" onClick="${portletNamespace}${namespacedFieldName}ToggleImage();">${languageUtil.get(locale, "show")}</a>
			</@liferay_ui.csp>
		]

		<div class="hide wcm-image-preview" id="${portletNamespace}${namespacedFieldName}Container">
			<#if validator.isNotNull(src)>
				<img alt="${escapeAttribute(alt)}" class="img-polaroid" id="${portletNamespace}${namespacedFieldName}Image" src="${escapeAttribute(src)}" />
			<#else>
				<img alt="${escapeAttribute(alt)}" class="img-polaroid" id="${portletNamespace}${namespacedFieldName}Image" src="${getFileEntryURL(fileEntry)}" />
			</#if>
		</div>

		<#if !disabled>
			<#if validator.isNotNull(src)>
				<@liferay_aui.input
					name="${namespacedFieldName}URL"
					type="hidden"
					value="${src}"
				/>
			<#else>
				<@liferay_aui.input
					name="${namespacedFieldName}URL"
					type="hidden"
					value="${getFileEntryURL(fileEntry)}"
				/>
			</#if>

			<@liferay_aui.input
				label="image-description"
				name="${namespacedFieldName}Alt"
				type="hidden"
				value="${alt}"
			/>
		</#if>
	</#if>

	${fieldStructure.children}
</@>

<@liferay_aui.script>
	function ${portletNamespace}${namespacedFieldName}ToggleImage() {
		var toggleText = '${languageUtil.get(locale, "show")}';
		var containerNode = document.getElementById('${portletNamespace}${namespacedFieldName}Container');

		if (containerNode) {
			if (containerNode.classList.contains('hide')) {
				toggleText = '${languageUtil.get(locale, "hide")}';

				containerNode.classList.remove('hide');
			}
			else {
				containerNode.classList.add('hide');
			}
		}

		var imageToggle = document.getElementById('${portletNamespace}${namespacedFieldName}ToggleImage');

		if (imageToggle) {
			imageToggle.innerHTML = toggleText;
		}
	}
</@>