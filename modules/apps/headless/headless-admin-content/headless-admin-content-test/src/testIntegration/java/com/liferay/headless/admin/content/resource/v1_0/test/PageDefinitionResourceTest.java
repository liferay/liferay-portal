/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.content.client.dto.v1_0.PageDefinition;
import com.liferay.headless.admin.content.client.dto.v1_0.PageElement;
import com.liferay.headless.admin.content.client.http.HttpInvoker;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import jakarta.servlet.http.HttpServletResponse;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pavel Savinov
 */
@RunWith(Arquillian.class)
public class PageDefinitionResourceTest
	extends BasePageDefinitionResourceTestCase {

	@Override
	@Test
	public void testPostSitePageDefinitionPreview() throws Exception {
		HttpInvoker.HttpResponse httpResponse =
			pageDefinitionResource.postSitePageDefinitionPreviewHttpResponse(
				testGroup.getGroupId(),
				new PageDefinition() {

					@Override
					public PageElement getPageElement() {
						return _getRowPageElement();
					}

				});

		Assert.assertThat(
			httpResponse.getContent(),
			CoreMatchers.containsString("Hello, World!"));

		httpResponse =
			pageDefinitionResource.postSitePageDefinitionPreviewHttpResponse(
				testGroup.getGroupId(), randomPageDefinition());

		Assert.assertEquals(
			httpResponse.getStatusCode(),
			HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

		// Root preview

		httpResponse =
			pageDefinitionResource.postSitePageDefinitionPreviewHttpResponse(
				testGroup.getGroupId(),
				new PageDefinition() {

					@Override
					public PageElement getPageElement() {
						return new PageElement() {

							@Override
							public PageElement[] getPageElements() {
								return new PageElement[] {_getRowPageElement()};
							}

							@Override
							public Type getType() {
								return Type.ROOT;
							}

						};
					}

				});

		Assert.assertThat(
			httpResponse.getContent(),
			CoreMatchers.containsString("Hello, World!"));

		httpResponse =
			pageDefinitionResource.postSitePageDefinitionPreviewHttpResponse(
				testGroup.getGroupId(), randomPageDefinition());

		Assert.assertEquals(
			httpResponse.getStatusCode(),
			HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	private PageElement[] _getChildrenPageElements() {
		return new PageElement[] {
			new PageElement() {

				@Override
				public Object getDefinition() {
					return JSONUtil.put(
						"fragment",
						JSONUtil.put(
							"collectionName", "Basic Components"
						).put(
							"key", "BASIC_COMPONENT-heading"
						).put(
							"name", "Heading"
						)
					).put(
						"fragmentConfig",
						JSONUtil.put(
							"bottomSpacing", "2"
						).put(
							"headingLevel", "h1"
						).put(
							"textAlign", "left"
						)
					).put(
						"fragmentFields",
						JSONUtil.put(
							JSONUtil.put(
								"id", "element-text"
							).put(
								"value",
								JSONUtil.put(
									"text",
									JSONUtil.put(
										"value_i18n",
										JSONUtil.put(
											LocaleUtil.toLanguageId(
												LocaleUtil.
													getMostRelevantLocale()),
											"Hello, World!")))
							))
					);
				}

				@Override
				public Type getType() {
					return Type.FRAGMENT;
				}

			}
		};
	}

	private PageElement _getRowPageElement() {
		return new PageElement() {

			@Override
			public Object getDefinition() {
				return JSONUtil.put(
					"gutters", true
				).put(
					"numberOfColumns", 1
				);
			}

			@Override
			public PageElement[] getPageElements() {
				return new PageElement[] {
					new PageElement() {

						@Override
						public Object getDefinition() {
							return JSONUtil.put("size", 12);
						}

						@Override
						public PageElement[] getPageElements() {
							return _getChildrenPageElements();
						}

						@Override
						public Type getType() {
							return Type.COLUMN;
						}

					}
				};
			}

			@Override
			public Type getType() {
				return Type.ROW;
			}

		};
	}

}