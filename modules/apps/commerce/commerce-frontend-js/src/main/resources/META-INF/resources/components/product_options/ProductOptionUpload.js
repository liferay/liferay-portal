/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import React, {useCallback, useEffect, useState} from 'react';

import skuOptionsAtom from '../../utilities/atoms/skuOptionsAtom';
import DDMFormHandler from '../../utilities/forms/DDMFormHandler';
import {getSkuOptionsErrors} from './utils';

const CP_CONTENT_WEB_PORTLET_KEY =
	'com_liferay_commerce_product_content_web_internal_portlet_CPContentPortlet';

const ProductOptionUpload = ({
	componentId,
	cpDefinitionId,
	forceRequired = false,
	namespace,
	productOption,
}) => {
	const [, setHasErrors] = useState(false);
	const [skuOptionsAtomState, setSkuOptionsAtomState] =
		useLiferayState(skuOptionsAtom);

	const handleChange = useCallback(
		({value = '{}'}) => {
			let currentSkuOptions = skuOptionsAtomState.skuOptions.slice();

			const currentSkuOption = currentSkuOptions.find(
				(skuOption) => skuOption.skuOptionKey === productOption.key
			);

			if (currentSkuOption) {
				currentSkuOptions = currentSkuOptions.map((skuOption) => {
					if (skuOption.skuOptionKey === productOption.key) {
						return {
							key: productOption.key,
							skuOptionKey: productOption.key,
							skuOptionName: productOption.name,
							value: [value],
						};
					}

					return skuOption;
				});
			}
			else {
				currentSkuOptions = [
					...currentSkuOptions,
					{
						key: productOption.key,
						skuOptionKey: productOption.key,
						skuOptionName: productOption.name,
						value: [value],
					},
				];
			}

			if (
				(forceRequired || productOption.required) &&
				(!value || value === '{}')
			) {
				setHasErrors(true);
			}

			setSkuOptionsAtomState({
				...skuOptionsAtomState,
				errors: getSkuOptionsErrors(
					(forceRequired || productOption.required) &&
						(!value || value === '{}'),
					false,
					productOption,
					skuOptionsAtomState
				),
				namespace,
				skuOptions: currentSkuOptions,
			});
		},
		[
			forceRequired,
			namespace,
			productOption,
			skuOptionsAtomState,
			setSkuOptionsAtomState,
		]
	);

	useEffect(() => {
		Liferay.componentReady('ProductOptions' + cpDefinitionId).then(
			(DDMFormInstance) => {
				if (DDMFormInstance) {
					new DDMFormHandler({
						DDMFormInstance,
						cpDefinitionId,
						forceRequired: forceRequired || productOption.required,
						key: productOption.key,
						namespace,
						portletId: CP_CONTENT_WEB_PORTLET_KEY,
					});
				}
			}
		);
	}, [cpDefinitionId, forceRequired, namespace, productOption]);

	useEffect(() => {
		const handler = (payload) => handleChange(payload);

		Liferay.on('product-option-upload-update', handler);

		return () => {
			Liferay.detach('product-option-upload-update', handler);
		};
	}, [handleChange]);

	return <div id={componentId} />;
};

export default ProductOptionUpload;
