/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.servlet.taglib.util;

import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.web.internal.display.context.FragmentDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Víctor Galán
 */
public class FragmentCollectionActionDropdownItemsProvider {

	public FragmentCollectionActionDropdownItemsProvider(
		FragmentDisplayContext fragmentDisplayContext,
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_fragmentDisplayContext = fragmentDisplayContext;
		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						_fragmentDisplayContext::hasUpdatePermission,
						dropdownItem -> {
							dropdownItem.setHref(
								PortletURLBuilder.createRenderURL(
									_renderResponse
								).setMVCRenderCommandName(
									"/fragment/edit_fragment_collection"
								).setRedirect(
									_fragmentDisplayContext.getRedirect()
								).setParameter(
									"fragmentCollectionId",
									_fragmentDisplayContext.
										getFragmentCollectionId()
								).buildString());
							dropdownItem.setIcon("pencil");
							dropdownItem.setLabel(
								LanguageUtil.get(_httpServletRequest, "edit"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> {
							FragmentCollection fragmentCollection =
								_fragmentDisplayContext.getFragmentCollection();

							return !fragmentCollection.isMarketplace();
						},
						dropdownItem -> {
							ResourceURL
								exportFragmentCompositionsAndFragmentEntriesURL =
									_renderResponse.createResourceURL();

							exportFragmentCompositionsAndFragmentEntriesURL.
								setParameter(
									"fragmentCollectionId",
									String.valueOf(
										_fragmentDisplayContext.
											getFragmentCollectionId()));
							exportFragmentCompositionsAndFragmentEntriesURL.
								setResourceID(
									"/fragment/export_fragment_collections");

							dropdownItem.setHref(
								exportFragmentCompositionsAndFragmentEntriesURL.
									toString());

							dropdownItem.setIcon("export");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "export"));
						}
					).add(
						_fragmentDisplayContext::hasUpdatePermission,
						dropdownItem -> {
							dropdownItem.setHref(
								PortletURLBuilder.createRenderURL(
									_renderResponse
								).setMVCRenderCommandName(
									"/fragment/view_import"
								).setParameter(
									"fragmentCollectionId",
									_fragmentDisplayContext.
										getFragmentCollectionId()
								).buildString());
							dropdownItem.setIcon("import");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "import"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						_fragmentDisplayContext::hasDeletePermission,
						dropdownItem -> {
							dropdownItem.putData(
								"action", "deleteFragmentCollection");
							dropdownItem.putData(
								"deleteFragmentCollectionURL",
								PortletURLBuilder.createActionURL(
									_renderResponse
								).setActionName(
									"/fragment/delete_fragment_collection"
								).setParameter(
									"fragmentCollectionId",
									_fragmentDisplayContext.
										getFragmentCollectionId()
								).buildString());
							dropdownItem.setIcon("trash");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "delete"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private final FragmentDisplayContext _fragmentDisplayContext;
	private final HttpServletRequest _httpServletRequest;
	private final RenderResponse _renderResponse;

}