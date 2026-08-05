/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.sample.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.portal.kernel.util.IntegerWrapper;

import java.util.List;

/**
 * @author Chema Balsas
 */
public class NavigationBarsDisplayContext {

	public List<NavigationItem> getNavigationItems() {
		if (_navigationItems != null) {
			return _navigationItems;
		}

		_navigationItems = new NavigationItemList() {
			{
				IntegerWrapper integerWrapper = new IntegerWrapper(1);

				while (true) {
					if (integerWrapper.getValue() == 8) {
						break;
					}

					add(
						navigationItem -> {
							if (integerWrapper.getValue() == 4) {
								navigationItem.setActive(true);
							}

							if (integerWrapper.getValue() == 5) {
								navigationItem.setDisabled(true);
							}

							navigationItem.setHref(
								"#" + integerWrapper.getValue());
							navigationItem.setLabel(
								"Page " + integerWrapper.getValue());
						});

					integerWrapper.increment();
				}
			}
		};

		return _navigationItems;
	}

	private List<NavigationItem> _navigationItems;

}