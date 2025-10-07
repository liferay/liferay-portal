/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayForm from '@clayui/form';
import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import classnames from 'classnames';
import {fetch, sub} from 'frontend-js-web';
import React, {
	useCallback,
	useContext,
	useEffect,
	useMemo,
	useState,
} from 'react';

import ServiceProvider from '../../ServiceProvider/index';
import {CommerceContext} from '../../index';
import skuOptionsAtom from '../../utilities/atoms/skuOptionsAtom';
import {CHANNEL_RESOURCE_ENDPOINT, FIELD_TYPE} from '../../utilities/constants';
import {
	CP_INSTANCE_CHANGED,
	CP_QUANTITY_SELECTOR_CHANGED,
	CP_UNIT_OF_MEASURE_SELECTOR_CHANGED,
	CURRENT_ORDER_UPDATED,
	FDS_UPDATE_DISPLAY,
} from '../../utilities/eventsDefinitions';
import {formatCartItem} from '../add_to_cart/data';
import {adaptLegacyPriceModel, isNonnull} from '../price/util/index';
import Asterisk from '../product_options/Asterisk';
import ProductOptionCheckbox from '../product_options/ProductOptionCheckbox';
import ProductOptionCheckboxMultiple from '../product_options/ProductOptionCheckboxMultiple';
import ProductOptionDate from '../product_options/ProductOptionDate';
import ProductOptionNumeric from '../product_options/ProductOptionNumeric';
import ProductOptionRadio from '../product_options/ProductOptionRadio';
import ProductOptionSelect from '../product_options/ProductOptionSelect';
import ProductOptionText from '../product_options/ProductOptionText';
import QuantitySelector from '../quantity_selector/QuantitySelector';
import TierPrice from '../tier_price/TierPrice';
import UnitOfMeasureSelector from '../unit_of_measure_selector/UnitOfMeasureSelector';
import MiniCartContext from './MiniCartContext';

const MINI_CART_NAMESPACE = 'minicart_';

const CartResource = ServiceProvider.DeliveryCartAPI('v1');

const getProductOptionsURL = (channelId, productId) => {
	const url = new URL(
		`${themeDisplay.getPathContext()}${CHANNEL_RESOURCE_ENDPOINT}/${channelId}/products/${productId}/product-options`,
		themeDisplay.getPortalURL()
	);

	return url.toString();
};

function EditItem() {
	const [cpInstance, setCPInstance] = useState({});
	const [options, setOptions] = useState([]);
	const [quantity, setQuantity] = useState(1);
	const [quantitySelectorErrors, setQuantitySelectorErrors] = useState(false);
	const [skuOptionsAtomState, setSkuOptionsAtomState] =
		useLiferayState(skuOptionsAtom);
	const [skuUnitOfMeasure, setSkuUnitOfMeasure] = useState(null);

	const {miniCartErrors} = skuOptionsAtomState;

	const disabled = useMemo(
		() => miniCartErrors?.length || quantitySelectorErrors,
		[miniCartErrors, quantitySelectorErrors]
	);

	const {
		cartState: {
			channel: {channel},
			id: cartId,
		},
		cartState,
		closeCart,
		editedItem,
		isUpdating,
		setEditedItem,
		setIsUpdating,
	} = useContext(MiniCartContext);

	const backLabel = sub(
		Liferay.Language.get('go-to-x'),
		Liferay.Language.get('products')
	);

	const postChannelProductSkuBySkuOption = useCallback(
		({
			accountId,
			channelId,
			currencyCode,
			options,
			productId,
			quantity,
			unitOfMeasureKey,
		}) => {
			ServiceProvider.DeliveryCatalogAPI('v1')
				.postChannelProductSkuBySkuOption(
					channelId,
					productId,
					accountId,
					currencyCode,
					quantity,
					unitOfMeasureKey,
					options
				)
				.then((cpInstance) => {
					cpInstance.skuId = parseInt(cpInstance.id, 10);

					const dispatchedPayload = {
						MINI_CART_NAMESPACE,
						cpInstance,
					};

					Liferay.fire(
						`${MINI_CART_NAMESPACE}${CP_INSTANCE_CHANGED}`,
						dispatchedPayload
					);
				});
		},
		[]
	);

	const handleBack = useCallback(
		(refreshDataSet = false) => {
			setEditedItem(null);

			setSkuOptionsAtomState((skuOptionsAtomState) => ({
				...skuOptionsAtomState,
				miniCartErrors: [],
				miniCartSkuOptions: [],
				updating: false,
			}));

			if (editedItem?.dataSetId) {
				if (refreshDataSet) {
					Liferay.fire(FDS_UPDATE_DISPLAY, {
						id: editedItem.dataSetId,
					});
				}

				closeCart();
			}
		},
		[closeCart, editedItem, setEditedItem, setSkuOptionsAtomState]
	);

	const handleSave = useCallback(
		async (event) => {
			event.preventDefault();

			if (disabled) {
				return;
			}

			setIsUpdating(true);

			try {
				const didCPInstanceChange = editedItem.skuId !== cpInstance.id;

				if (didCPInstanceChange) {
					await CartResource.deleteItemById(editedItem.id);

					await CartResource.createItemByCartId(
						cartId,
						formatCartItem(
							cpInstance,
							MINI_CART_NAMESPACE,
							skuOptionsAtomState.miniCartSkuOptions,
							MINI_CART_NAMESPACE
						)
					);
				}
				else {
					const item = {...editedItem};

					delete item.dataSetId;

					await CartResource.updateItemById(editedItem.id, {
						...item,
						...formatCartItem(
							cpInstance,
							MINI_CART_NAMESPACE,
							skuOptionsAtomState.miniCartSkuOptions,
							MINI_CART_NAMESPACE
						),
					});
				}

				Liferay.fire(CURRENT_ORDER_UPDATED, {
					order: {
						currencyCode:
							Liferay?.CommerceContext?.currency?.currencyCode,
						id: cartId,
					},
					refreshItems: true,
				});

				handleBack(true);
			}
			catch (error) {
				Liferay.Util.openToast({
					message:
						error.detail ||
						error.errorDescription ||
						Liferay.Language.get(
							'an-unexpected-system-error-occurred'
						),
					type: 'danger',
				});
			}

			setIsUpdating(false);
		},
		[
			cartId,
			cpInstance,
			disabled,
			editedItem,
			handleBack,
			setIsUpdating,
			skuOptionsAtomState,
		]
	);

	const [price, setPrice] = useState(editedItem ? editedItem.price : null);

	const handleCPInstanceChanged = useCallback(
		({cpInstance}) => {
			setCPInstance((prevState) => ({
				...cpInstance,
				quantity: prevState?.quantity,
				settings: prevState?.settings,
				skuUnitOfMeasure: prevState?.skuUnitOfMeasure,
			}));
			setPrice(adaptLegacyPriceModel(cpInstance.price));
		},
		[setCPInstance, setPrice]
	);

	useEffect(() => {
		setCPInstance({
			id: editedItem.skuId,
			productId: editedItem.productId,
			quantity: editedItem.quantity,
			replacedSkuId: editedItem.replacedSkuId,
			settings: editedItem.settings,
			skuId: editedItem.skuId,
			skuOptions: JSON.parse(editedItem.options || '[]') || [],
			skuUnitOfMeasure: editedItem.skuUnitOfMeasure,
		});
		setQuantity(editedItem.quantity);
	}, [editedItem]);

	useEffect(() => {
		function handleUOMChanged({unitOfMeasure}) {
			setCPInstance((cpInstance) => ({
				...cpInstance,
				skuUnitOfMeasure: unitOfMeasure,
			}));
			setSkuUnitOfMeasure(unitOfMeasure);
		}

		Liferay.on(
			`${MINI_CART_NAMESPACE}${CP_INSTANCE_CHANGED}`,
			handleCPInstanceChanged
		);

		Liferay.on(
			`${MINI_CART_NAMESPACE}${CP_UNIT_OF_MEASURE_SELECTOR_CHANGED}`,
			handleUOMChanged
		);

		return () => {
			Liferay.detach(
				`${MINI_CART_NAMESPACE}${CP_INSTANCE_CHANGED}`,
				handleCPInstanceChanged
			);
			Liferay.detach(
				`${MINI_CART_NAMESPACE}${CP_UNIT_OF_MEASURE_SELECTOR_CHANGED}`,
				handleUOMChanged
			);
		};
	}, [handleCPInstanceChanged]);

	useEffect(() => {
		const productOptionsURL = getProductOptionsURL(
			channel.id,
			editedItem.productId
		);

		fetch(productOptionsURL)
			.then((response) => response.json())
			.then((data) => setOptions(data))
			.catch((error) => console.error(error));
	}, [channel.id, editedItem.productId]);

	useEffect(() => {
		Liferay.on(
			`${MINI_CART_NAMESPACE}${CP_INSTANCE_CHANGED}`,
			handleCPInstanceChanged
		);

		return () => {
			Liferay.detach(
				`${MINI_CART_NAMESPACE}${CP_INSTANCE_CHANGED}`,
				handleCPInstanceChanged
			);
		};
	}, [handleCPInstanceChanged]);

	return (
		<>
			<div className="d-flex flex-column h-100 mini-cart-edit-item overflow-hidden">
				<div className="align-items-center d-flex mini-cart-header px-4">
					<ClayButtonWithIcon
						aria-label={backLabel}
						displayType="unstyled"
						onClick={(event) => {
							event.preventDefault();

							handleBack();
						}}
						symbol="angle-left"
						title={backLabel}
					/>

					<span className="font-weight-bold ml-2 text-5">
						{sub(Liferay.Language.get('edit-x'), editedItem.name)}
					</span>
				</div>

				<div className="flex-grow-1 flex-shrink-1 overflow-auto p-4">
					{options?.items?.length > 0 ? (
						<>
							<div className="panel panel-unstyled">
								<div className="panel-header">
									<span className="panel-title">
										{Liferay.Language.get('edit-options')}
									</span>
								</div>
							</div>

							<ClayForm>
								<Options
									cartItemId={editedItem.id}
									channelId={channel.id}
									namespace={MINI_CART_NAMESPACE}
									productId={editedItem.productId}
									productOptions={options.items}
									selectedItem={editedItem}
								/>
							</ClayForm>
						</>
					) : null}

					{cpInstance.id ? (
						<>
							<div>
								<div className="panel panel-unstyled">
									<div className="panel-header">
										<span className="panel-title">
											{sub(
												Liferay.Language.get('edit-x'),
												Liferay.Language.get('quantity')
											)}
										</span>
									</div>
								</div>

								<label htmlFor="minicart-quantity-selector">
									{Liferay.Language.get('quantity')}

									<Asterisk required={true} />
								</label>

								<QuantitySelector
									alignment="bottom"
									allowedQuantities={
										cpInstance.settings?.allowedQuantities
									}
									disabled={isUpdating}
									max={cpInstance.settings?.maxQuantity}
									min={cpInstance.settings?.minQuantity}
									name="minicart-quantity-selector"
									namespace={MINI_CART_NAMESPACE}
									onUpdate={({
										errors,
										unitOfMeasure,
										value: newQuantity,
									}) => {
										setCPInstance((cpInstance) => ({
											...cpInstance,
											quantity: newQuantity,
										}));
										setQuantity(newQuantity);
										setQuantitySelectorErrors(
											errors && !!errors.length
										);

										if (!(errors && !!errors.length)) {
											postChannelProductSkuBySkuOption({
												accountId: cartState.accountId,
												channelId: channel.id,
												currencyCode:
													Liferay.CommerceContext
														? Liferay
																.CommerceContext
																.currency
																.currencyCode
														: '',
												options:
													cpInstance?.skuOptions ||
													[],
												productId: cpInstance.productId,
												quantity: newQuantity,
												unitOfMeasureKey:
													unitOfMeasure?.key ||
													skuUnitOfMeasure?.key,
											});
										}

										Liferay.fire(
											`${MINI_CART_NAMESPACE}${CP_QUANTITY_SELECTOR_CHANGED}`,
											{quantity: newQuantity}
										);
									}}
									quantity={quantity}
									step={
										skuUnitOfMeasure?.incrementalOrderQuantity ||
										cpInstance.settings?.multipleQuantity
									}
									{...cpInstance.settings}
									unitOfMeasure={skuUnitOfMeasure}
								/>
							</div>

							<div className="mt-4">
								<UnitOfMeasureSelector
									accountId={cartState.accountId}
									channelId={channel.id}
									cpInstanceId={cpInstance.id}
									disabled={isUpdating}
									label={Liferay.Language.get(
										'unit-of-measure'
									)}
									loadFinalPrice={true}
									name="minicart-uom-selector"
									namespace={MINI_CART_NAMESPACE}
									options={cpInstance?.skuOptions || []}
									panelLabel={sub(
										Liferay.Language.get('edit-x'),
										Liferay.Language.get('unit-of-measure')
									)}
									productId={cpInstance.productId}
									resetQuantity={false}
									useQuantity={true}
									value={cpInstance.skuUnitOfMeasure?.key}
								/>
							</div>

							<div className="mt-4 tier-price-table">
								<TierPrice
									accountId={cartState.accountId}
									autoload={false}
									channelId={channel.id}
									cpInstanceId={cpInstance.id}
									label={Liferay.Language.get(
										'unit-of-measure-table'
									)}
									namespace={MINI_CART_NAMESPACE}
									productId={cpInstance.productId}
								/>
							</div>
						</>
					) : (
						<></>
					)}
				</div>

				<div>
					<PriceRows price={price} />
				</div>

				<div className="mini-cart-footer px-4 py-2 text-right">
					<ClayButton
						className="mr-3"
						displayType="secondary"
						onClick={handleBack}
					>
						{Liferay.Language.get('cancel')}
					</ClayButton>

					<ClayButton
						disabled={disabled || isUpdating}
						onClick={handleSave}
					>
						{Liferay.Language.get('save')}
					</ClayButton>
				</div>
			</div>
		</>
	);
}

export default EditItem;

const Options = ({
	cartItemId,
	channelId,
	disabled,
	productId,
	productOptions,
	selectedItem,
}) =>
	productOptions.map((productOption) => {
		let Component = ProductOptionCheckbox;
		let props = {
			componentId: `${MINI_CART_NAMESPACE}${cartItemId}_${productOption.id}`,
			isFromMiniCart: true,
			json: selectedItem.options,
			namespace: MINI_CART_NAMESPACE,
			productOption,
		};

		if (productOption.fieldType === FIELD_TYPE.checkboxMultiple) {
			Component = ProductOptionCheckboxMultiple;
		}
		else if (productOption.fieldType === FIELD_TYPE.date) {
			Component = ProductOptionDate;
		}
		else if (productOption.fieldType === FIELD_TYPE.document_library) {
			return;
		}
		else if (productOption.fieldType === FIELD_TYPE.numeric) {
			Component = ProductOptionNumeric;
		}
		else if (productOption.fieldType === FIELD_TYPE.radio) {
			Component = ProductOptionRadio;
			props = {
				...props,
				accountId: CommerceContext.account.accountId,
				channelId,
				minQuantity: selectedItem.quantity,
				productId,
				sku: {skuId: selectedItem.skuId},
			};
		}
		else if (productOption.fieldType === FIELD_TYPE.select) {
			Component = ProductOptionSelect;
			props = {
				...props,
				accountId: CommerceContext.account.accountId,
				channelId,
				minQuantity: selectedItem.quantity,
				productId,
				sku: {skuId: selectedItem.skuId},
			};
		}
		else if (productOption.fieldType === FIELD_TYPE.text) {
			Component = ProductOptionText;
		}

		return (
			<Component disabled={disabled} key={productOption.id} {...props} />
		);
	});

const PriceRow = ({children, priceName}) => {
	return (
		<div className="align-items-baseline d-flex justify-content-between mb-2">
			<span className="text-2">{priceName}</span>

			{children}
		</div>
	);
};

const PriceRows = ({price}) => {
	const hasPromoPrice = isNonnull(price?.promoPrice);
	const hasDiscountPercentage = isNonnull(price?.discountPercentage);
	const priceOnApplication = price.priceOnApplication;

	return (
		<>
			{price && !priceOnApplication && (
				<div className="mini-cart-prices p-4">
					<PriceRow priceName={Liferay.Language.get('list-price')}>
						<span
							className={classnames({
								'price-line-through':
									hasPromoPrice || hasDiscountPercentage,
							})}
						>
							{price.priceFormatted}
						</span>
					</PriceRow>

					{hasPromoPrice ? (
						<PriceRow
							priceName={Liferay.Language.get('promo-price')}
						>
							<span
								className={classnames({
									'price-line-through': hasDiscountPercentage,
								})}
							>
								{price.promoPriceFormatted}
							</span>
						</PriceRow>
					) : null}

					{hasDiscountPercentage ? (
						<PriceRow priceName={Liferay.Language.get('discount')}>
							<span className="price-discount">
								{`-${price.discountPercentage}%`}
							</span>
						</PriceRow>
					) : null}

					<PriceRow
						priceName={Liferay.Language.get('price-as-configured')}
					>
						<span className="text-7">
							{price.finalPriceFormatted}
						</span>
					</PriceRow>
				</div>
			)}

			{price && priceOnApplication && (
				<div className="mini-cart-prices p-4">
					<PriceRow
						priceName={Liferay.Language.get('price-as-configured')}
					>
						<span className="price-on-application price-value text-3">
							{Liferay.Language.get('price-on-application')}
						</span>
					</PriceRow>
				</div>
			)}
		</>
	);
};
