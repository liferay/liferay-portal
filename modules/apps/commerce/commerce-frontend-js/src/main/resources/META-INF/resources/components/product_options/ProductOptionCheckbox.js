/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayCheckbox} from '@clayui/form';
import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import classnames from 'classnames';
import React, {useEffect, useState} from 'react';

import skuOptionsAtom from '../../utilities/atoms/skuOptionsAtom';
import Asterisk from './Asterisk';
import {
	INITIAL_SKU_OPTIONS_ATOM_STATE,
	getProductOptionName,
	getSkuOptionsErrors,
	isRequired,
} from './utils';

const ProductOptionCheckbox = ({
	componentId,
	forceRequired = false,
	isFromMiniCart = false,
	json,
	namespace,
	productOption,
}) => {
	const errorsKey = isFromMiniCart ? 'miniCartErrors' : 'errors';
	const [hasErrors, setHasErrors] = useState(false);
	const [isChecked, setIsChecked] = useState(false);
	const skuOptionsKey = isFromMiniCart ? 'miniCartSkuOptions' : 'skuOptions';

	const [skuOptionsAtomState, setSkuOptionsAtomState] =
		useLiferayState(skuOptionsAtom);

	useEffect(
		() =>
			setSkuOptionsAtomState({
				...skuOptionsAtomState,
				[errorsKey]: getSkuOptionsErrors(
					hasErrors,
					isFromMiniCart,
					productOption,
					skuOptionsAtomState
				),
			}),

		// eslint-disable-next-line react-hooks/exhaustive-deps
		[hasErrors]
	);

	useEffect(() => {
		let checked = false;
		let value = '';

		if (isFromMiniCart) {
			const option = JSON.parse(json).find(
				({key}) => key === productOption.key
			);

			if (option) {
				checked = option.value.includes(option.key);
				[value] = option.value;

				setIsChecked(checked);
			}
		}

		if (productOption.required) {
			setHasErrors(checked ? !checked : productOption.required);
		}

		setSkuOptionsAtomState({
			...skuOptionsAtomState,
			[errorsKey]: getSkuOptionsErrors(
				productOption.required,
				isFromMiniCart,
				productOption,
				skuOptionsAtomState
			),
			...(!isFromMiniCart && {namespace}),
			[skuOptionsKey]: isFromMiniCart
				? JSON.parse(json)
				: [
						...(skuOptionsAtomState[skuOptionsKey] || []),
						{
							key: productOption.key,
							skuOptionKey: productOption.key,
							skuOptionName: productOption.name,
							value: checked ? [value] : [],
						},
					],
		});

		return () =>
			isFromMiniCart
				? setSkuOptionsAtomState({
						...skuOptionsAtomState,
						miniCartErrors: [],
						miniCartSkuOptions: [],
					})
				: setSkuOptionsAtomState(INITIAL_SKU_OPTIONS_ATOM_STATE);

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	const handleChange = ({target: {checked, value}}) => {
		if (skuOptionsAtomState.updating) {
			return;
		}

		setSkuOptionsAtomState({...skuOptionsAtomState, updating: true});

		setIsChecked(checked);

		let currentSkuOptions = skuOptionsAtomState[skuOptionsKey].slice();

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
						value: checked ? [value] : [],
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
					value: checked ? [value] : [],
				},
			];
		}

		const required = (forceRequired || productOption.required) && !checked;

		setHasErrors(required);

		setSkuOptionsAtomState({
			...skuOptionsAtomState,
			[errorsKey]: getSkuOptionsErrors(
				required,
				isFromMiniCart,
				productOption,
				skuOptionsAtomState
			),
			[skuOptionsKey]: currentSkuOptions,
			updating: false,
		});
	};

	return (
		<ClayForm.Group className={classnames({'has-error': hasErrors})}>
			<label htmlFor={componentId}>
				{getProductOptionName(productOption.name)}

				<Asterisk
					required={isRequired(
						forceRequired,
						skuOptionsAtomState.isAdmin,
						productOption
					)}
				/>
			</label>

			<ClayCheckbox
				checked={isChecked}
				disabled={skuOptionsAtomState.updating}
				id={componentId}
				name={productOption.key}
				onChange={handleChange}
				value={productOption.key}
			/>

			{hasErrors && (
				<ClayForm.FeedbackItem>
					<ClayForm.FeedbackIndicator symbol="exclamation-full" />

					{Liferay.Language.get('this-field-is-required')}
				</ClayForm.FeedbackItem>
			)}
		</ClayForm.Group>
	);
};

export default ProductOptionCheckbox;
