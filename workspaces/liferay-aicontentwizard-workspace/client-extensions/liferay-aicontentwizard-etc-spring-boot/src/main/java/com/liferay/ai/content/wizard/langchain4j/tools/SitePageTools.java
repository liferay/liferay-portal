/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.content.wizard.langchain4j.tools;

import com.liferay.ai.content.wizard.langchain4j.descriptions.SitePageDescriptions;
import com.liferay.headless.delivery.client.dto.v1_0.SitePage;
import com.liferay.headless.delivery.client.resource.v1_0.SitePageResource;
import com.liferay.petra.string.StringBundler;

import dev.langchain4j.agent.tool.Tool;

import java.util.Objects;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.http.HttpHeaders;

/**
 * @author Keven Leone
 * @author Brian Wing Shun Chan
 */
public class SitePageTools extends BaseTools {

	public SitePageTools(ToolsContext toolsContext) {
		super(toolsContext);

		_sitePageResource = SitePageResource.builder(
		).endpoint(
			toolsContext.liferayDXPURL
		).header(
			HttpHeaders.AUTHORIZATION, toolsContext.authorization
		).build();
	}

	@Tool("Creates a page in the site with the specified description")
	public SitePage postSiteSitePage(SitePageDescriptions sitePageDescriptions)
		throws Exception {

		return _sitePageResource.postSiteSitePage(
			toolsContext.siteId,
			SitePage.toDTO(_toSitePageJSON(sitePageDescriptions)));
	}

	private JSONObject _createFragmentDefinitionJSONObject(String key) {
		return new JSONObject(
		).put(
			"definition",
			new JSONObject(
			).put(
				"fragment",
				new JSONObject(
				).put(
					"key", key
				)
			).put(
				"fragmentConfig", new JSONObject()
			).put(
				"fragmentFields", new JSONArray()
			)
		).put(
			"id",
			UUID.randomUUID(
			).toString()
		).put(
			"type", "Fragment"
		);
	}

	private JSONObject _createFragmentJSONObject(
		String fragmentKey, String headingLevel, String text) {

		JSONObject fragmentConfigJSONObject = new JSONObject();

		if (headingLevel != null) {
			fragmentConfigJSONObject.put("headingLevel", headingLevel);
		}

		return new JSONObject(
		).put(
			"fragment",
			new JSONObject(
			).put(
				"key", fragmentKey
			)
		).put(
			"fragmentConfig", fragmentConfigJSONObject
		).put(
			"fragmentFields",
			new JSONArray(
			).put(
				new JSONObject(
				).put(
					"id", "element-text"
				).put(
					"value",
					new JSONObject(
					).put(
						"fragmentLink",
						new JSONObject(
						).put(
							"value_i18n", new JSONObject()
						)
					).put(
						"text",
						new JSONObject(
						).put(
							"value_i18n",
							new JSONObject(
							).put(
								"en_US", text
							)
						)
					)
				)
			)
		).put(
			"type", "Fragment"
		);
	}

	private JSONObject _createFragmentViewportJSONObject(
		String id, String paddingLeft, String paddingRight) {

		return new JSONObject(
		).put(
			"fragmentViewportStyle",
			new JSONObject(
			).put(
				"paddingLeft", paddingLeft
			).put(
				"paddingRight", paddingRight
			)
		).put(
			"id", id
		);
	}

	private JSONObject _createPageElementJSONObject(String name) {
		if (Objects.equals(name, "button")) {
			return _createFragmentDefinitionJSONObject(
				"BASIC_COMPONENT-button");
		}

		if (Objects.equals(name, "card")) {
			return _createFragmentDefinitionJSONObject("BASIC_COMPONENT-card");
		}

		if (Objects.equals(name, "carousel")) {
			return _createFragmentDefinitionJSONObject(
				"BASIC_COMPONENT-slider");
		}

		if (Objects.equals(name, "footer")) {
			return _createFragmentDefinitionJSONObject(
				"FOOTERS-footer-nav-light");
		}

		if (Objects.equals(name, "header")) {
			return _createFragmentDefinitionJSONObject(
				"NAVIGATION_BARS-header-light");
		}

		if (Objects.equals(name, "heading")) {
			return _createFragmentDefinitionJSONObject(
				"BASIC_COMPONENT-heading");
		}

		if (Objects.equals(name, "hero banner")) {
			return _createSectionDefinitionJSONObject();
		}

		if (Objects.equals(name, "image")) {
			return _createFragmentDefinitionJSONObject("BASIC_COMPONENT-image");
		}

		if (Objects.equals(name, "paragraph")) {
			return _createFragmentDefinitionJSONObject(
				"BASIC_COMPONENT-paragraph");
		}

		if (Objects.equals(name, "social")) {
			return _createFragmentDefinitionJSONObject(
				"BASIC_COMPONENT-social");
		}

		if (Objects.equals(name, "video")) {
			return _createFragmentDefinitionJSONObject(
				"BASIC_COMPONENT-external-video");
		}

		return null;
	}

	private JSONObject _createRowDefinitionJSONObject(JSONArray jsonArray) {
		JSONArray pageElementsJSONArray = new JSONArray();

		for (int i = 0; i < jsonArray.length(); i++) {
			pageElementsJSONArray.put(
				new JSONObject(
				).put(
					"definition",
					new JSONObject(
					).put(
						"size", 12 / jsonArray.length()
					)
				).put(
					"id",
					UUID.randomUUID(
					).toString()
				).put(
					"pageElements", jsonArray
				).put(
					"type", "Column"
				));
		}

		return new JSONObject(
		).put(
			"definition",
			new JSONObject(
			).put(
				"fragmentStyle",
				new JSONObject(
				).put(
					"marginBottom", "3"
				).put(
					"overflow", "visible"
				)
			).put(
				"gutters", true
			).put(
				"numberOfColumns", pageElementsJSONArray.length()
			)
		).put(
			"id",
			UUID.randomUUID(
			).toString()
		).put(
			"pageElements", pageElementsJSONArray
		).put(
			"type", "Row"
		);
	}

	private JSONObject _createSectionDefinitionJSONObject() {
		return new JSONObject(
		).put(
			"definition",
			new JSONObject(
			).put(
				"fragmentStyle",
				new JSONObject(
				).put(
					"backgroundColor", "gray500Color"
				).put(
					"borderWidth", "0"
				).put(
					"paddingBottom", "10"
				).put(
					"paddingTop", "10"
				).put(
					"textAlign", "center"
				)
			).put(
				"fragmentViewports",
				new JSONArray(
				).put(
					_createFragmentViewportJSONObject(
						"landscapeMobile", "4", "4")
				).put(
					_createFragmentViewportJSONObject(
						"portraitMobile", "3", "3")
				).put(
					_createFragmentViewportJSONObject("tablet", "5", "5")
				)
			).put(
				"layout",
				new JSONObject(
				).put(
					"borderWidth", 0
				).put(
					"paddingBottom", 0
				).put(
					"paddingTop", 0
				).put(
					"widthType", "Fluid"
				)
			)
		).put(
			"pageElements",
			new JSONArray(
			).put(
				new JSONObject(
				).put(
					"definition", new JSONObject()
				).put(
					"pageElements",
					new JSONArray(
					).put(
						_createFragmentJSONObject(
							"BASIC_COMPONENT-heading", "h1",
							"Banner Title Example")
					).put(
						_createFragmentJSONObject(
							"BASIC_COMPONENT-paragraph", null,
							StringBundler.concat(
								"<span class=\"lead\">This is a simple banner ",
								"component to call extra attention to ",
								"featured content or information.</span>"))
					).put(
						new JSONObject(
						).put(
							"fragment",
							new JSONObject(
							).put(
								"key", "BASIC_COMPONENT-button"
							)
						).put(
							"fragmentConfig",
							new JSONObject(
							).put(
								"buttonSize", "nm"
							).put(
								"buttonType", "primary"
							)
						).put(
							"type", "Fragment"
						)
					)
				).put(
					"type", "Section"
				)
			)
		).put(
			"type", "Section"
		);
	}

	private String _toSitePageJSON(SitePageDescriptions sitePageDescriptions) {
		JSONArray pageElementsJSONArray = new JSONArray();

		for (SitePageDescriptions.Structure structure :
				sitePageDescriptions.structures) {

			if (structure.name != null) {
				JSONObject jsonObject = _createPageElementJSONObject(
					structure.name.toString());

				if (jsonObject != null) {
					pageElementsJSONArray.put(jsonObject);
				}
			}
			else if (structure.components != null) {
				JSONArray jsonArray = new JSONArray();

				for (SitePageDescriptions.Component component :
						structure.components) {

					JSONObject jsonObject = _createPageElementJSONObject(
						component.name);

					if (jsonObject == null) {
						continue;
					}

					jsonArray.put(jsonObject);
				}

				pageElementsJSONArray.put(
					_createRowDefinitionJSONObject(jsonArray));
			}
		}

		return new JSONObject(
		).put(
			"pageDefinition",
			new JSONObject(
			).put(
				"pageElement",
				new JSONObject(
				).put(
					"id",
					UUID.randomUUID(
					).toString()
				).put(
					"pageElements", pageElementsJSONArray
				).put(
					"type", "Root"
				)
			)
		).put(
			"title", sitePageDescriptions.title
		).toString();
	}

	private final SitePageResource _sitePageResource;

}