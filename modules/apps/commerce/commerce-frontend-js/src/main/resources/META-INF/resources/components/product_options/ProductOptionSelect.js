/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import ClayForm, {ClaySelect} from '@clayui/form';
import ClayLabel from '@clayui/label';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import classnames from 'classnames';
import React, {useCallback, useEffect, useState} from 'react';

import ServiceProvider from '../../ServiceProvider/index';
import skuOptionsAtom from '../../utilities/atoms/skuOptionsAtom';
import {
	CP_INSTANCE_CHANGED,
	CP_OPTION_CHANGED,
} from '../../utilities/eventsDefinitions';
import Asterisk from './Asterisk';
import {
	INITIAL_SKU_OPTIONS_ATOM_STATE,
	getInitialProductOptionValue,
	getName,
	getProductOptionName,
	getSkuOptionsErrors,
	isRequired,
} from './utils';

const ProductOptionSelect = ({
	accountId,
	channelId,
	componentId,
	forceRequired = false,
	isAdmin = false,
	isFromMiniCart = false,
	json,
	minQuantity,
	namespace,
	productId,
	productOption,
	sku,
}) => {
	const defaultErrorMessage = Liferay.Language.get('this-field-is-required');
	const emptyTextValue = Liferay.Language.get('choose-an-option');
	const [errorMessage, setErrorMessage] = useState('' + defaultErrorMessage);
	const errorsKey = isFromMiniCart ? 'miniCartErrors' : 'errors';
	const [hasErrors, setHasErrors] = useState(false);
	const isMounted = useIsMounted();
	const optionIsRequired = isRequired(forceRequired, isAdmin, productOption);
	const [resetErrorMessage, setResetErrorMessage] = useState(false);
	const [selectedSkuId, setSelectedSkuId] = useState(sku?.id);
	const skuOptionsKey = isFromMiniCart ? 'miniCartSkuOptions' : 'skuOptions';

	const [skuOptionsAtomState, setSkuOptionsAtomState] =
		useLiferayState(skuOptionsAtom);

	const currentJSONObject = json
		? JSON.parse(json).filter(
				(jsonObject) => jsonObject.key === productOption.key
			)[0]
		: null;

	const initialProductOptionValue = isAdmin
		? {key: currentJSONObject?.value[0]}
		: getInitialProductOptionValue({
				currentJSONObject,
				isFromMiniCart,
				productOption,
			});

	const [selectedProductOptionValue, setSelectedProductOptionValue] =
		useState({
			productOptionValueId: initialProductOptionValue?.id,
			skuId: selectedSkuId,
		});
	const [selectedProductOptionValueKey, setSelectedProductOptionValueKey] =
		useState(initialProductOptionValue?.key);

	const [productOptionValues, setProductOptionValues] = useState(
		productOption.productOptionValues
	);

	const DeliveryCatalogAPIServiceProvider =
		ServiceProvider.DeliveryCatalogAPI('v1');

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
		const required =
			(isAdmin && forceRequired && !initialProductOptionValue?.key) ||
			(productOption.required &&
				!productOption.skuContributor &&
				!initialProductOptionValue);

		if (required) {
			setHasErrors(true);
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
							price: initialProductOptionValue?.price,
							priceType: initialProductOptionValue?.priceType,
							quantity: initialProductOptionValue?.quantity,
							skuId: initialProductOptionValue?.skuId,
							skuOptionKey: productOption.key,
							skuOptionName: productOption.name,
							skuOptionValueKey: initialProductOptionValue?.key,
							skuOptionValueNames: [
								initialProductOptionValue?.name,
							],
							value: initialProductOptionValue?.key || '',
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

	const onChange = (value) => {
		if (skuOptionsAtomState.updating) {
			return;
		}

		if (value === emptyTextValue) {
			value = '';
		}

		setSkuOptionsAtomState({...skuOptionsAtomState, updating: true});

		const valueArray = value.split('[$SEPARATOR$]');

		if (isAdmin) {
			setSelectedProductOptionValueKey(valueArray[1]);

			const required = forceRequired && !valueArray[1];

			setHasErrors(required);

			return setSkuOptionsAtomState({
				...skuOptionsAtomState,
				[errorsKey]: getSkuOptionsErrors(
					required,
					isFromMiniCart,
					productOption,
					skuOptionsAtomState
				),
				updating: false,
			});
		}

		let currentSkuOptions = skuOptionsAtomState[skuOptionsKey].slice();

		const currentProductOptionValue = productOptionValues.filter(
			(productOptionValue) => productOptionValue.key === valueArray[1]
		)[0];

		if (!currentProductOptionValue) {
			setResetErrorMessage(true);
			setSelectedProductOptionValueKey('');

			currentSkuOptions = currentSkuOptions.filter(
				(skuOption) => skuOption.skuOptionKey !== productOption.key
			);

			setSkuOptionsAtomState({
				...skuOptionsAtomState,
				[errorsKey]: getSkuOptionsErrors(
					optionIsRequired,
					isFromMiniCart,
					productOption,
					skuOptionsAtomState
				),
				[skuOptionsKey]: currentSkuOptions,
				updating: false,
			});

			setTimeout(() =>
				Liferay.fire(`${namespace}${CP_OPTION_CHANGED}`, {
					productOptionId: productOption.id,
					productOptionValueId: valueArray[0],
					skuId: selectedSkuId,
					skuOptions: currentSkuOptions,
				})
			);

			if (optionIsRequired) {
				setHasErrors(optionIsRequired);

				return;
			}
		}
		else {
			setSelectedProductOptionValue({
				...selectedProductOptionValue,
				productOptionValueId: valueArray[0],
				skuId: selectedSkuId,
			});
			setSelectedProductOptionValueKey(valueArray[1]);

			const currentSkuOption = currentSkuOptions.filter(
				(skuOption) => skuOption.skuOptionKey === productOption.key
			)[0];

			if (currentSkuOption) {
				currentSkuOptions = currentSkuOptions.map((skuOption) => {
					if (skuOption.skuOptionKey === productOption.key) {
						return {
							key: productOption.key,
							price: currentProductOptionValue.price,
							priceType: currentProductOptionValue.priceType,
							quantity: currentProductOptionValue.quantity,
							skuId: currentProductOptionValue.skuId,
							skuOptionKey: productOption.key,
							skuOptionName: productOption.name,
							skuOptionValueKey: valueArray[1],
							skuOptionValueNames: [
								currentProductOptionValue.name,
							],
							value: valueArray[1],
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
						price: currentProductOptionValue.price,
						priceType: currentProductOptionValue.priceType,
						quantity: currentProductOptionValue.quantity,
						skuId: currentProductOptionValue.skuId,
						skuOptionKey: productOption.key,
						skuOptionName: productOption.name,
						skuOptionValueKey: valueArray[1],
						skuOptionValueNames: [currentProductOptionValue.name],
						value: valueArray[1],
					},
				];
			}
		}

		let currentSkuId = selectedSkuId;

		DeliveryCatalogAPIServiceProvider.postChannelProductSkuBySkuOption(
			channelId,
			productId,
			accountId,
			Liferay.CommerceContext
				? Liferay.CommerceContext.currency.currencyCode
				: '',
			minQuantity,
			null,
			currentSkuOptions
		)
			.then((cpInstance) => {
				setSelectedProductOptionValue({
					...selectedProductOptionValue,
					productOptionValueId: valueArray[0],
					skuId: cpInstance.id,
				});

				const currentCPInstanceSkuOption = currentSkuOptions.filter(
					(skuOption) => skuOption.skuOptionKey === productOption.key
				)[0];

				if (currentCPInstanceSkuOption) {
					const curIndex = currentSkuOptions.findIndex(
						(skuOption) =>
							skuOption.skuOptionKey === productOption.key
					);

					currentSkuOptions[curIndex] = {
						...currentCPInstanceSkuOption,
						key: productOption.key,
					};
				}

				setSkuOptionsAtomState({
					...skuOptionsAtomState,
					[skuOptionsKey]: currentSkuOptions,
				});

				cpInstance.skuOptions = currentSkuOptions;
				cpInstance.skuId = parseInt(cpInstance.id, 10);

				currentSkuId = cpInstance.skuId;

				setSelectedSkuId(cpInstance.skuId);

				const dispatchedPayload = {
					cpInstance,
					namespace,
				};

				Liferay.fire(
					`${namespace}${CP_INSTANCE_CHANGED}`,
					dispatchedPayload
				);
			})
			.finally(() => {
				if (isMounted()) {
					setHasErrors(false);
					setSkuOptionsAtomState({
						...skuOptionsAtomState,
						[errorsKey]: getSkuOptionsErrors(
							false,
							isFromMiniCart,
							productOption,
							skuOptionsAtomState
						),
						[skuOptionsKey]: currentSkuOptions,
						updating: false,
					});

					setTimeout(() =>
						Liferay.fire(`${namespace}${CP_OPTION_CHANGED}`, {
							productOptionId: productOption.id,
							productOptionValueId: valueArray[0],
							skuId: currentSkuId,
							skuOptions: currentSkuOptions,
						})
					);
				}
			});
	};

	const handleChange = ({target: {value}}) => {
		onChange(value);
	};
	const handleSelectionChange = (value) => {
		onChange(value);
	};

	const updateProductOptionValuesHandler = useCallback(
		({productOptionId, productOptionValueId, skuId, skuOptions}) => {
			DeliveryCatalogAPIServiceProvider.postChannelProductProductOptionProductOptionValues(
				channelId,
				productId,
				productOption.id,
				accountId,
				Liferay.CommerceContext
					? Liferay.CommerceContext.currency.currencyCode
					: '',
				productOptionValueId,
				skuId,
				1,
				-1,
				skuOptions
			).then((responseProductOptionValues) => {
				setProductOptionValues(responseProductOptionValues.items);

				if (!Liferay.CommerceContext.showUnselectableOptions) {
					const currentProductOptionValues =
						responseProductOptionValues.items.filter(
							(productOptionValue) =>
								productOptionValue.productOptionId.toString() ===
									productOption.id.toString() &&
								productOptionValue.selectable
						);

					if (
						!currentProductOptionValues.length &&
						optionIsRequired
					) {
						setErrorMessage(defaultErrorMessage);
						setHasErrors(true);
						setSelectedProductOptionValue({
							...selectedProductOptionValue,
							productOptionValueId: 0,
						});
						setSelectedProductOptionValueKey('');
					}

					return;
				}

				const currentProductOptionValue =
					responseProductOptionValues.items.find(
						(productOptionValue) =>
							productOptionValue.key ===
							selectedProductOptionValueKey
					);

				if (productOptionId === productOption.id) {
					setResetErrorMessage(false);

					if (resetErrorMessage) {
						setErrorMessage(defaultErrorMessage);
					}

					if (optionIsRequired && resetErrorMessage) {
						setHasErrors(true);
					}

					return;
				}

				if (
					selectedProductOptionValueKey &&
					(!currentProductOptionValue ||
						!currentProductOptionValue.selectable)
				) {
					setResetErrorMessage(false);
					setHasErrors(true);

					if (resetErrorMessage) {
						setErrorMessage(defaultErrorMessage);
					}
					else {
						setErrorMessage(currentProductOptionValue.infoMessage);
					}
				}
				else if (!currentProductOptionValue && optionIsRequired) {
					setErrorMessage(defaultErrorMessage);
					setHasErrors(true);
				}
				else {
					setHasErrors(false);
				}
			});
		},

		// eslint-disable-next-line react-hooks/exhaustive-deps
		[resetErrorMessage, selectedProductOptionValueKey]
	);

	useEffect(() => {
		Liferay.on(
			`${namespace}${CP_OPTION_CHANGED}`,
			updateProductOptionValuesHandler
		);

		return () =>
			Liferay.detach(
				`${namespace}${CP_OPTION_CHANGED}`,
				updateProductOptionValuesHandler
			);
	}, [namespace, updateProductOptionValuesHandler]);

	return (
		<ClayForm.Group className={classnames({'has-error': hasErrors})}>
			<label htmlFor={componentId}>
				{getProductOptionName(productOption.name)}

				<Asterisk required={optionIsRequired} />
			</label>

			{!isAdmin && Liferay.CommerceContext.showUnselectableOptions ? (
				<Picker
					data-sku-contributor={productOption.skuContributor}
					defaultSelectedKey={
						selectedProductOptionValue?.productOptionValueId +
						'[$SEPARATOR$]' +
						selectedProductOptionValueKey
					}
					disabled={skuOptionsAtomState.updating}
					id={componentId}
					onSelectionChange={handleSelectionChange}
					placeholder={emptyTextValue}
				>
					<Option key="">{emptyTextValue}</Option>

					{productOptionValues.map(
						({
							id,
							infoMessage,
							key,
							name,
							relativePriceFormatted,
							selectable,
							skuId,
							visible,
						}) => {
							if (!visible) {
								return;
							}

							const optionName = getName(
								key,
								name,
								selectedProductOptionValueKey,
								skuId,
								relativePriceFormatted
							);

							return (
								<Option
									disabled={!selectable}
									key={id + '[$SEPARATOR$]' + key}
									textValue={optionName}
								>
									{optionName}

									{infoMessage && (
										<ClayLabel
											className="float-right"
											displayType="warning"
										>
											{infoMessage}
										</ClayLabel>
									)}
								</Option>
							);
						}
					)}
				</Picker>
			) : (
				<ClaySelect
					data-sku-contributor={productOption.skuContributor}
					disabled={skuOptionsAtomState.updating}
					id={componentId}
					name={productOption.key}
					onChange={handleChange}
				>
					<ClaySelect.Option
						label={emptyTextValue}
						selected={!selectedProductOptionValueKey}
					/>

					{productOptionValues.map(
						({
							id,
							key,
							name,
							relativePriceFormatted,
							selectable,
							skuId,
							visible,
						}) => {
							if (isAdmin || (selectable && visible)) {
								return (
									<ClaySelect.Option
										key={id}
										label={getName(
											key,
											name,
											selectedProductOptionValueKey,
											skuId,
											relativePriceFormatted
										)}
										selected={
											selectedProductOptionValueKey ===
											key
										}
										value={id + '[$SEPARATOR$]' + key}
									/>
								);
							}
						}
					)}
				</ClaySelect>
			)}

			{hasErrors && (
				<ClayForm.FeedbackItem>
					<ClayForm.FeedbackIndicator symbol="exclamation-full" />

					{errorMessage}
				</ClayForm.FeedbackItem>
			)}
		</ClayForm.Group>
	);
};

export default ProductOptionSelect;
