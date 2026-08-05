/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLink from '@clayui/link';
import ClayNavigationBar from '@clayui/navigation-bar';
import {FeatureIndicator} from 'frontend-js-components-web';
import React from 'react';

export default function NavigationBar({
	activeItemAriaCurrent,
	cssClass,
	inverted,
	navigationItems,
}) {
	return (
		<ClayNavigationBar
			aria-current={activeItemAriaCurrent}
			className={cssClass}
			fluidSize="xxxl"
			inverted={inverted}
			triggerLabel={navigationItems.find(({active}) => active)?.label}
		>
			{navigationItems.map(
				(
					{active, deprecated = false, disabled = false, href, label},
					index
				) => {
					const LinkOrButton =
						href && !disabled ? ClayLink : ClayButton;
					const LinkOrButtonProps = href && !disabled ? {href} : {};

					return (
						<ClayNavigationBar.Item
							active={active}
							data-nav-item-index={index}
							key={label}
						>
							<LinkOrButton
								{...LinkOrButtonProps}
								disabled={disabled}
							>
								{label}

								{deprecated ? (
									<span className="ml-2">
										<FeatureIndicator type="deprecated" />
									</span>
								) : null}
							</LinkOrButton>
						</ClayNavigationBar.Item>
					);
				}
			)}
		</ClayNavigationBar>
	);
}
