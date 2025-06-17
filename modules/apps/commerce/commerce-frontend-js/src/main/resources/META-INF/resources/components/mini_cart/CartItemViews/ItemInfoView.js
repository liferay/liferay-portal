/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayPanel from '@clayui/panel';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {sub} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useState} from 'react';

import {parseValue} from '../util/index';

function ItemInfoViewBundle({childItems, options}) {
	const [expanded, setExpanded] = useState(false);

	return options.length >= 1 ? (
		<ClayPanel
			className="item-info-collapse mb-0"
			collapsable
			displayTitle={sub(
				Liferay.Language.get('x-options'),
				expanded
					? Liferay.Language.get('hide')
					: Liferay.Language.get('show')
			)}
			displayType="secondary"
			expanded={expanded}
			onExpandedChange={(expanded) => {
				setExpanded(expanded);
			}}
			showCollapseIcon
		>
			<ClayPanel.Body>
				<div className="child-items">
					{options.map((option, index) => {
						const {
							skuId,
							skuOptionName,
							skuOptionValueNames,
							value,
						} = option;

						const childItem = (childItems || []).find(
							(childItem) =>
								childItem.skuId === parseInt(skuId, 10)
						);

						const {name, quantity, skuUnitOfMeasure} =
							childItem || {};

						const parsedSkuOptionValueNames =
							parseValue(skuOptionValueNames);
						const parsedValue = parseValue(value);

						return (parsedSkuOptionValueNames || parsedValue) &&
							parsedValue !== '{}' ? (
							name ? (
								<div
									className="item-info-extra pt-2"
									key={index}
								>
									<div className="h6 item-name">
										{skuOptionName}
									</div>

									<p className="item-sku">
										<span>
											<span>
												{parsedSkuOptionValueNames ||
													parsedValue}
											</span>

											<span className="pl-2">
												{`(${quantity} \u00D7 ${name} ${
													skuUnitOfMeasure?.key || ''
												})`}
											</span>
										</span>
									</p>
								</div>
							) : (
								<div
									className="item-info-extra pt-2"
									key={index}
								>
									<div className="h6 item-name">
										{skuOptionName}
									</div>

									<p className="item-sku">
										{parsedSkuOptionValueNames ||
											parsedValue}
									</p>
								</div>
							)
						) : null;
					})}
				</div>
			</ClayPanel.Body>
		</ClayPanel>
	) : (
		<div className="child-items">
			{childItems.map((item, index) => {
				const {name, quantity, skuUnitOfMeasure} = item;

				return (
					<div className="child-item" key={index}>
						<span>
							<>
								{quantity} &times; {name}
							</>
							<> {skuUnitOfMeasure?.key || ''}</>
						</span>
					</div>
				);
			})}
		</div>
	);
}

function ItemInfoViewReplacement({replacedSku}) {
	return (
		<div className="item-info-replacement">
			<ClayLabel displayType="info">
				{Liferay.Language.get('replacement')}
			</ClayLabel>

			<ClayTooltipProvider>
				<span
					data-tooltip-align="left"
					title={sub(
						Liferay.Language.get('replacement-product-for-x'),
						replacedSku
					)}
				>
					<ClayIcon aria-label="Info" symbol="info-circle" />
				</span>
			</ClayTooltipProvider>
		</div>
	);
}

function ItemInfoViewBase({name, sku}) {
	return (
		<div className="item-info-base">
			<div className="h5 item-name">{name}</div>

			<p className="item-sku">{sku}</p>
		</div>
	);
}

function ItemInfoView({childItems = [], name, options = [], replacedSku, sku}) {
	const hasReplacement = !!replacedSku;

	return (
		<>
			<ItemInfoViewBase name={name} sku={sku} />

			{hasReplacement && (
				<ItemInfoViewReplacement replacedSku={replacedSku} />
			)}

			<ItemInfoViewBundle childItems={childItems} options={options} />
		</>
	);
}

ItemInfoView.propTypes = {
	childItems: PropTypes.array,
	name: PropTypes.string.isRequired,
	options: PropTypes.array,
	sku: PropTypes.string.isRequired,
};

export default ItemInfoView;
