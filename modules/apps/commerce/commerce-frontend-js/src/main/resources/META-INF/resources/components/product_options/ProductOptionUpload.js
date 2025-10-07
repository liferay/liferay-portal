/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import classnames from 'classnames';
import React, {useCallback, useEffect, useState} from 'react';

import skuOptionsAtom from '../../utilities/atoms/skuOptionsAtom';
import DDMFormHandler from '../../utilities/forms/DDMFormHandler';
import {INITIAL_SKU_OPTIONS_ATOM_STATE, getSkuOptionsErrors} from './utils';

import './product_option_upload.scss';

const CP_CONTENT_WEB_PORTLET_KEY =
	'com_liferay_commerce_product_content_web_internal_portlet_CPContentPortlet';

const ProductOptionUpload = ({
	componentId,
	cpDefinitionId,
	forceRequired = false,
	namespace,
	productOption,
}) => {
	const [hasErrors, setHasErrors] = useState(false);
	const [skuOptionsAtomState, setSkuOptionsAtomState] =
		useLiferayState(skuOptionsAtom);
	const isMounted = useIsMounted();

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

			const required =
				(forceRequired || productOption.required) &&
				(!value || value === '{}');

			setHasErrors(required);

			setSkuOptionsAtomState({
				...skuOptionsAtomState,
				errors: getSkuOptionsErrors(
					required,
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
		const required = forceRequired || productOption.required;

		setHasErrors(required);

		setSkuOptionsAtomState((skuOptionsAtomState) => ({
			...skuOptionsAtomState,
			errors: getSkuOptionsErrors(
				required,
				false,
				productOption,
				skuOptionsAtomState
			),
			namespace,
			skuOptions: [
				...(skuOptionsAtomState.skuOptions || []),
				{
					key: productOption.key,
					skuOptionKey: productOption.key,
					skuOptionName: productOption.name,
					value: ['{}'],
				},
			],
		}));

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
	}, [
		cpDefinitionId,
		forceRequired,
		namespace,
		productOption,
		setSkuOptionsAtomState,
	]);

	useEffect(() => {
		const handler = (payload) => handleChange(payload);

		Liferay.on('product-option-upload-update', handler);

		return () => {
			Liferay.detach('product-option-upload-update', handler);

			if (!isMounted()) {
				setSkuOptionsAtomState(INITIAL_SKU_OPTIONS_ATOM_STATE);
			}
		};
	}, [handleChange, isMounted, setSkuOptionsAtomState]);

	return (
		<ClayForm.Group
			className={classnames('product-option-upload', {
				'has-error': hasErrors,
			})}
		>
			<div id={componentId} />

			{hasErrors && (
				<ClayForm.FeedbackItem>
					<ClayForm.FeedbackIndicator symbol="exclamation-full" />

					{Liferay.Language.get('this-field-is-required')}
				</ClayForm.FeedbackItem>
			)}
		</ClayForm.Group>
	);
};

export default ProductOptionUpload;
