/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {VerticalNav as ClayVerticalNav} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import {FeatureIndicator} from 'frontend-js-components-web';
import React from 'react';

export default function VerticalNav({
	activation,
	active,
	additionalProps: _additionalProps,
	componentId: _componentId,
	cssClass,
	decorated,
	defaultExpandedKeys,
	items,
	large,
	locale: _locale,
	portletId: _portletId,
	portletNamespace: _portletNamespace,
	triggerLabel,
	...otherProps
}) {
	return (
		<ClayVerticalNav
			activation={activation}
			active={active}
			className={cssClass}
			decorated={decorated}
			defaultExpandedKeys={new Set(defaultExpandedKeys)}
			items={items}
			large={large}
			triggerLabel={triggerLabel}
			{...otherProps}
		>
			{(item) => (
				<ClayVerticalNav.Item
					active={active ? active === item.id : item.active}
					href={item.href}
					items={item.items}
					key={item.id}
					textValue={item.label}
				>
					{item.label}

					{item.icons?.map((icon) => {
						return (
							<ClayIcon
								className="c-ml-2 c-mr-2 text-muted"
								key={icon.symbol}
								symbol={icon.symbol}
								title={icon.title}
							/>
						);
					})}

					{item.labelItems && (
						<span className="inline-item inline-item-after">
							{item.labelItems.map(({label, ...props}) => (
								<ClayLabel
									className="c-ml-2"
									key={label}
									{...props}
								>
									{label}
								</ClayLabel>
							))}
						</span>
					)}

					{item.deprecated ? (
						<span className="inline-item inline-item-after">
							<FeatureIndicator type="deprecated" />
						</span>
					) : null}
				</ClayVerticalNav.Item>
			)}
		</ClayVerticalNav>
	);
}
