<#assign
	group = themeDisplay.getScopeGroup()

	mapsAPIProvider = group.getLiveParentTypeSettingsProperty("mapsAPIProvider")!""

	companyPortletPreferences = prefsPropsUtil.getPreferences(companyId)
/>

<#if validator.isNull(mapsAPIProvider)>
	<#assign mapsAPIProvider = companyPortletPreferences.getValue("mapsAPIProvider", "Google") />
</#if>

<#assign featureCollectionJSONObject = jsonFactoryUtil.createJSONObject() />

<@liferay.silently featureCollectionJSONObject.put("type", "FeatureCollection") />

<#assign featureJSONArray = jsonFactoryUtil.createJSONArray() />

<#list entries as entry>
	<#assign
		assetRenderer = entry.getAssetRenderer()

		ddmFormValuesReader = assetRenderer.getDDMFormValuesReader()

		ddmFormFieldValues = ddmFormValuesReader.getDDMFormFieldValues("geolocation")

		coordinatesJSONObjects = []
	/>

	<#list ddmFormFieldValues as ddmFormFieldValue>
		<#assign
			value = ddmFormFieldValue.getValue()

			coordinatesJSONObject = jsonFactoryUtil.createJSONObject(value.getString(locale))

			coordinatesJSONObjects = coordinatesJSONObjects + [coordinatesJSONObject]
		/>
	</#list>

	<#list coordinatesJSONObjects as coordinatesJSONObject>
		<#assign featureJSONObject = jsonFactoryUtil.createJSONObject() />

		<@liferay.silently featureJSONObject.put("type", "Feature") />

		<#assign geometryJSONObject = jsonFactoryUtil.createJSONObject() />

		<@liferay.silently geometryJSONObject.put("type", "Point") />

		<#assign coordinatesJSONArray = [coordinatesJSONObject.getDouble("lng"), coordinatesJSONObject.getDouble("lat")] />

		<@liferay.silently geometryJSONObject.put("coordinates", coordinatesJSONArray) />

		<@liferay.silently featureJSONObject.put("geometry", geometryJSONObject) />

		<#assign propertiesJSONObject = jsonFactoryUtil.createJSONObject() />

		<@liferay.silently propertiesJSONObject.put("title", assetRenderer.getTitle(locale)) />

		<#assign entryAbstract>
			<@getAbstract asset = entry />
		</#assign>

		<@liferay.silently propertiesJSONObject.put("abstract", entryAbstract) />

		<#if stringUtil.equals(mapsAPIProvider, "Google")>
			<#assign
				images =
					{
						"com.liferay.document.library.kernel.model.DLFileEntry": "${themeDisplay.getProtocol()}://maps.google.com/mapfiles/ms/icons/green-dot.png",
						"com.liferay.journal.model.JournalArticle": "${themeDisplay.getProtocol()}://maps.google.com/mapfiles/ms/icons/blue-dot.png",
						"com.liferay.portlet.dynamicdatalists.model.DDLRecord": "${themeDisplay.getProtocol()}://maps.google.com/mapfiles/ms/icons/red-dot.png",
						"default": "${themeDisplay.getProtocol()}://maps.google.com/mapfiles/ms/icons/yellow-dot.png"
					}
			/>

			<#if images?keys?seq_contains(entry.getClassName())>
				<@liferay.silently propertiesJSONObject.put("icon", images[entry.getClassName()]) />
			<#else>
				<@liferay.silently propertiesJSONObject.put("icon", images["default"]) />
			</#if>
		</#if>

		<@liferay.silently featureJSONObject.put("properties", propertiesJSONObject) />

		<@liferay.silently featureJSONArray.put(featureJSONObject) />
	</#list>
</#list>

<@liferay.silently featureCollectionJSONObject.put("features", featureJSONArray) />

<style ${nonceAttribute} type="text/css">
	.asset-entry-abstract {
		min-width: 400px;
	}

	.leaflet-popup .asset-entry-abstract {
		display: inline-block;
	}

	.asset-entry-abstract .asset-entry-abstract-image {
		display: block;
		float: left;
		height: 128px;
		margin-right: 1em;
		text-align: center;
	}

	.asset-entry-abstract .asset-entry-abstract-image img {
		display: block;
		margin: 0 auto;
		max-height: 100%;
	}

	.asset-entry-abstract .taglib-icon {
		float: right;
	}
</style>

<@liferay_map["map-display"]
	name='Map'
	points="${featureCollectionJSONObject}"
/>

<@liferay_aui.script use="liferay-map-base">
	Liferay.componentReady('<@portlet.namespace />Map').then(
		function(map) {
			map.on(
				'featureClick',
				function(event) {
					var feature = event.feature;

					map.openDialog(
						{
							content: feature.getProperty('abstract'),
							marker: feature.getMarker(),
							position: feature.getGeometry().get('location')
						}
					);
				}
			);
		}
	);
</@liferay_aui.script>

<#macro getAbstract
	asset
>
	<div class="asset-entry-abstract" id="<@portlet.namespace />assetEntryAbstract">
		<#assign
			showEditURL = paramUtil.getBoolean(renderRequest, "showEditURL", true)

			assetRenderer = asset.getAssetRenderer()
		/>

		<#if showEditURL && assetRenderer.hasEditPermission(permissionChecker)>
			<#assign
				editPortletURL = assetRenderer.getURLEdit(renderRequest, renderResponse, windowStateFactory.getWindowState("NORMAL"), themeDisplay.getURLCurrent())
			/>

			<@liferay_ui.icon
				image="edit"
				label=true
				message="edit"
				url=editPortletURL.toString()
			/>
		</#if>

		<#if assetRenderer.getThumbnailPath(renderRequest)??>
			<div class="asset-entry-abstract-image">
				<img src="${assetRenderer.getThumbnailPath(renderRequest)}" />
			</div>
		</#if>

		<#assign assetURL = assetPublisherHelper.getAssetViewURL(renderRequest, renderResponse, assetRenderer, asset, !stringUtil.equals(assetLinkBehavior, "showFullContent")) />

		<div class="asset-entry-abstract-content">
			<h3><a href="${assetURL}">${htmlUtil.escape(assetRenderer.getTitle(locale))}</a></h3>

			<div>
				${htmlUtil.escape(assetRenderer.getSummary(renderRequest, renderResponse))}
			</div>
		</div>

		<div class="asset-entry-abstract-footer">
			<a href="${assetURL}">${languageUtil.get(locale, "read-more")} &raquo;</a>
		</div>
	</div>
</#macro>