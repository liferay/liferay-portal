/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.portlet.PortletURL;

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides a helper class to retrieve the {@link ItemSelectorRendering} and the
 * {@link PortletURL} for the item selector.
 *
 * @author Iván Zaera
 * @author Roberto Díaz
 */
@ProviderType
public interface ItemSelector {

	/**
	 * Returns the selected event name used to create the item selector URL.
	 *
	 * @param  itemSelectorURL the unescaped item selector URL
	 * @return the selected event name
	 */
	public String getItemSelectedEventName(String itemSelectorURL);

	public List<ItemSelectorCriterion> getItemSelectorCriteria(
		Map<String, String[]> parameters);

	/**
	 * Returns the item selector criteria that was used to create the item
	 * selector URL.
	 *
	 * @param  itemSelectorURL the unescaped item selector URL
	 * @return the item selector criteria
	 */
	public List<ItemSelectorCriterion> getItemSelectorCriteria(
		String itemSelectorURL);

	/**
	 * Returns the {@link ItemSelectorRendering} according to the parameters.
	 *
	 * @param  requestBackedPortletURLFactory the factory used to generate the
	 *         {@link PortletURL}
	 * @param  parameters the map of parameters received in the URL. The item
	 *         selector framework uses them to get and render the views.
	 * @param  themeDisplay the current theme display
	 * @return the {@link ItemSelectorRendering}
	 */
	public ItemSelectorRendering getItemSelectorRendering(
		RequestBackedPortletURLFactory requestBackedPortletURLFactory,
		Map<String, String[]> parameters, ThemeDisplay themeDisplay);

	/**
	 * Returns a generated item selector {@link PortletURL} to render the item
	 * selector and show the selection views, scoped to the group matching the
	 * {@link ItemSelectorCriterion} and {@link ItemSelectorReturnType}. This
	 * method is not recommended for external use.
	 *
	 * @param  requestBackedPortletURLFactory the factory used to generate the
	 *         {@link PortletURL}
	 * @param  group the group from which to select items
	 * @param  refererGroupId the group ID of the item selector client
	 * @param  itemSelectedEventName the event name for views to fire
	 * @param  itemSelectorCriteria an array of criteria for the item selector
	 *         to use to retrieve views
	 * @return a generated item selector {@link PortletURL}
	 */
	public PortletURL getItemSelectorURL(
		RequestBackedPortletURLFactory requestBackedPortletURLFactory,
		Group group, long refererGroupId, String itemSelectedEventName,
		ItemSelectorCriterion... itemSelectorCriteria);

	/**
	 * Returns a generated item selector {@link PortletURL} to render the item
	 * selector and show the selection views matching the {@link
	 * ItemSelectorCriterion} and {@link ItemSelectorReturnType}.
	 *
	 * @param  requestBackedPortletURLFactory the factory used to generate the
	 *         {@link PortletURL}
	 * @param  itemSelectedEventName the event name for views to fire
	 * @param  itemSelectorCriteria an array of criteria for the item selector
	 *         to use to retrieve the views
	 * @return a generated item selector {@link PortletURL}
	 */
	public PortletURL getItemSelectorURL(
		RequestBackedPortletURLFactory requestBackedPortletURLFactory,
		String itemSelectedEventName,
		ItemSelectorCriterion... itemSelectorCriteria);

}