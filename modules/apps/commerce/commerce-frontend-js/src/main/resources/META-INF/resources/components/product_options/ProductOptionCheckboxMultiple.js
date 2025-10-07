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

const ProductOptionCheckboxMultiple = ({
	forceRequired,
	isFromMiniCart,
	json,
	namespace,
	productOption,
}) => {
	const errorsKey = isFromMiniCart ? 'miniCartErrors' : 'errors';
	const [hasErrors, setHasErrors] = useState(false);
	const skuOptionsKey = isFromMiniCart ? 'miniCartSkuOptions' : 'skuOptions';
	const [skuOptionsAtomState, setSkuOptionsAtomState] =
		useLiferayState(skuOptionsAtom);

	const [productOptionValues, setProductOptionValues] = useState(
		productOption.productOptionValues
	);

	const getHasError = (optionValues, required) => {
		const hasSelectedOptions = optionValues.some(({selected}) => selected);

		return hasSelectedOptions ? !hasSelectedOptions : required;
	};

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
				...(!isFromMiniCart && {namespace}),
			}),

		// eslint-disable-next-line react-hooks/exhaustive-deps
		[hasErrors]
	);

	useEffect(() => {
		const option = json
			? JSON.parse(json)?.find(({key}) => key === productOption.key)
			: null;
		let preselected = false;

		let skuOptionValueNames = [];

		let value = option?.value || [];

		const newProductOptionValues = productOptionValues.map(
			(productOptionValue) => {
				let selected = false;

				if (isFromMiniCart) {
					if (option) {
						selected = option.value.includes(
							productOptionValue.key
						);
					}
				}
				else {
					if (productOptionValue.preselected) {
						preselected = true;
						selected = true;
						skuOptionValueNames = [productOptionValue.name];
						value = [productOptionValue.key];
					}
				}

				return {
					...productOptionValue,
					selected,
				};
			}
		);

		setProductOptionValues(newProductOptionValues);

		const required = productOption.required && !preselected;

		if (required) {
			setHasErrors(getHasError(newProductOptionValues, required));
		}

		setSkuOptionsAtomState({
			...skuOptionsAtomState,
			[errorsKey]: getSkuOptionsErrors(
				required,
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
							skuOptionValueNames,
							value,
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

		let currentSkuOptions = skuOptionsAtomState[skuOptionsKey].slice();

		const curSkuOptionIndex = currentSkuOptions.findIndex(
			(skuOption) => skuOption.skuOptionKey === productOption.key
		);

		const currentSkuOption = currentSkuOptions.filter(
			(skuOption) => skuOption.skuOptionKey === productOption.key
		)[0];

		const curProductOptionValue =
			productOptionValues.find(
				(productOptionValue) => productOptionValue.key === value
			) || {};

		if (currentSkuOption) {
			currentSkuOptions = currentSkuOptions.map((skuOption) => {
				if (skuOption.skuOptionKey === productOption.key) {
					return {
						key: productOption.key,
						skuOptionKey: productOption.key,
						skuOptionName: productOption.name,
						skuOptionValueNames: checked
							? [
									...currentSkuOptions[curSkuOptionIndex]
										.skuOptionValueNames,
									curProductOptionValue.name,
								]
							: currentSkuOptions[
									curSkuOptionIndex
								].skuOptionValueNames.filter(
									(curVal) =>
										curVal !== curProductOptionValue.name
								),
						value: checked
							? [
									...currentSkuOptions[curSkuOptionIndex]
										.value,
									value,
								]
							: currentSkuOptions[curSkuOptionIndex].value.filter(
									(curVal) => !(curVal === value)
								),
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
					skuOptionValueNames: [curProductOptionValue.name],
					value: [value],
				},
			];
		}

		const curProductOptionValueIndex = productOptionValues.findIndex(
			(productOptionValue) => productOptionValue.key === value
		);

		productOptionValues[curProductOptionValueIndex] = {
			...productOptionValues[curProductOptionValueIndex],
			selected: checked,
		};

		setProductOptionValues(productOptionValues);

		const required = forceRequired || productOption.required;
		const hasError = getHasError(productOptionValues, required);

		setHasErrors(hasError);

		setSkuOptionsAtomState({
			...skuOptionsAtomState,
			[errorsKey]: getSkuOptionsErrors(
				hasError,
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
			<label>
				{getProductOptionName(productOption.name)}

				<Asterisk
					required={isRequired(
						forceRequired,
						skuOptionsAtomState.isAdmin,
						productOption
					)}
				/>
			</label>

			{productOptionValues.map(({key, name, selected}) => (
				<ClayCheckbox
					checked={selected}
					key={key}
					label={name}
					name={key}
					onChange={handleChange}
					value={key}
				/>
			))}

			{hasErrors && (
				<ClayForm.FeedbackItem>
					<ClayForm.FeedbackIndicator symbol="exclamation-full" />

					{Liferay.Language.get('this-field-is-required')}
				</ClayForm.FeedbackItem>
			)}
		</ClayForm.Group>
	);
};

export default ProductOptionCheckboxMultiple;
