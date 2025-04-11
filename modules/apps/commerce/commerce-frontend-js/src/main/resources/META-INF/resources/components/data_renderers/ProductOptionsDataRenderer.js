/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {parseValue} from '../mini_cart/util';

const ProductOptionsDataRenderer = ({itemData: {options = '[]', skuId}}) => {
	const productOptions = [];

	try {
		productOptions.push(...JSON.parse(options));
	}
	catch (_notValidJson) {
		productOptions.push(
			...options.split(/,\s*[^","]/).map((value) => ({
				value: [value.endsWith('}') ? `{${value}` : value],
			}))
		);
	}

	return (
		<>
			{productOptions.length ? (
				<div className="item-options">
					{productOptions.map(
						(
							{
								skuId: optionSkuId,
								skuOptionName,
								skuOptionValueNames,
								value,
							},
							index
						) =>
							!optionSkuId ||
							(optionSkuId && optionSkuId !== skuId) ? (
								<div
									className="item-info-extra pt-2"
									key={index}
								>
									<div className="h6 item-name">
										{skuOptionName}
									</div>

									<p className="item-sku">
										{parseValue(skuOptionValueNames) ||
											parseValue(value)}
									</p>
								</div>
							) : null
					)}
				</div>
			) : null}
		</>
	);
};

export default ProductOptionsDataRenderer;
