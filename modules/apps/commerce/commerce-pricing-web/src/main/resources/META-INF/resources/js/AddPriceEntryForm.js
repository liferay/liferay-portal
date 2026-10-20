/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInput, ClaySelectWithOption} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import {CommerceServiceProvider, commerceEvents} from 'commerce-frontend-js';
import PropTypes from 'prop-types';
import React, {useMemo, useRef, useState} from 'react';

import '../css/main.scss';

function AddPriceEntryForm({
	basePrice,
	cpInstanceId,
	currency,
	dataSetId,
	maxFractionDigits,
	namespace,
	priceLists,
	unitOfMeasures,
}) {
	const [inputGroups, setInputGroups] = useState([
		{
			currency: (priceLists || [])[0]?.currency || currency,
			id: 'inputGroup-0',
			price: basePrice,
			priceListId: (priceLists || [])[0]?.value || 0,
			unitOfMeasureKey: (unitOfMeasures || [])[0]?.value || null,
		},
	]);
	const inputGroupCounterRef = useRef(0);
	const hasUnitOfMeasuresRef = useRef(!!(unitOfMeasures || []).length);

	const AdminPricingResource = useMemo(
		() => CommerceServiceProvider.AdminPricingAPI('v2'),
		[]
	);

	const closeModal = (modalConfig = {}) => {
		window.parent.Liferay.fire('closeModal', modalConfig);

		if (dataSetId) {
			window.parent.Liferay.fire(commerceEvents.FDS_UPDATE_DISPLAY, {
				id: dataSetId,
			});
		}
	};

	const getInputGroup = (inputGroupId) => {
		const inputGroup = inputGroups.find((item) => item.id === inputGroupId);

		if (inputGroup) {
			return inputGroup;
		}

		throw new Error(`No input group found for id ${inputGroupId}`);
	};

	const onChangePriceHandler = ({inputGroupId, price}) => {
		const inputGroup = getInputGroup(inputGroupId);
		inputGroup.price = price;
		setInputGroups([...inputGroups]);
	};

	const onChangePriceListHandler = ({
		currency,
		inputGroupId,
		priceListId,
	}) => {
		const inputGroup = getInputGroup(inputGroupId);
		inputGroup.currency = currency;
		inputGroup.priceListId = priceListId;
		setInputGroups([...inputGroups]);
	};

	const onChangeUnitOfMeasureHandler = ({inputGroupId, unitOfMeasureKey}) => {
		const inputGroup = getInputGroup(inputGroupId);
		inputGroup.unitOfMeasureKey = unitOfMeasureKey;
		setInputGroups([...inputGroups]);
	};

	const onRemoveInputGroupHandler = (id) => {
		removeInputGroup(id);
	};

	const removeInputGroup = (id) => {
		setInputGroups((inputGroups) =>
			inputGroups.filter((inputGroup) => inputGroup.id !== id)
		);
	};

	const submitForm = async (event) => {
		event.preventDefault();

		const form = event.currentTarget;

		const error = form.querySelector('.has-error');

		if (!error) {
			const promises = inputGroups.map((inputGroup) => {
				return AdminPricingResource.addPriceEntry(
					inputGroup.priceListId,
					{
						price: inputGroup.price,
						priceListId: inputGroup.priceListId,
						skuId: cpInstanceId,
						unitOfMeasureKey: inputGroup.unitOfMeasureKey,
					}
				)
					.then((result) => {
						result.inputGroupId = inputGroup.id;
						result.success = true;

						return result;
					})
					.catch((error) => {
						error.success = false;

						return error;
					});
			});

			const results = await Promise.all(promises);

			const validResults = results.filter((result) => result.success);

			if (validResults.length === inputGroups.length) {
				closeModal({
					id: `${namespace}addPriceEntryDialog`,
				});
			}
			else {
				validResults.forEach((result) => {
					removeInputGroup(result.inputGroupId);
				});
				Liferay.Util.openToast({
					message: Liferay.Language.get(
						'your-request-failed-to-complete-for-these-items'
					),
					title: Liferay.Language.get('error'),
					type: 'danger',
				});
			}
		}
	};

	return (
		<ClayForm
			className="add-price-entry-modal-form lfr-form-content m-2"
			id={`${namespace}addPriceEntryForm`}
			onSubmit={submitForm}
		>
			{inputGroups.map((inputGroup, index) => (
				<ClayLayout.Sheet
					className="add-price-entry-sheet"
					key={`${namespace}inputGroup${index}`}
					size="lg"
				>
					<ClayLayout.ContainerFluid>
						<ClayLayout.Row justify="start">
							<ClayLayout.Col
								size={hasUnitOfMeasuresRef.current ? 4 : 5}
							>
								<label
									htmlFor={`${namespace}commercePriceList${index}`}
								>
									{Liferay.Language.get('price-list')}

									<span className="inline-item-after reference-mark text-warning">
										<ClayIcon symbol="asterisk" />
									</span>
								</label>

								<ClaySelectWithOption
									id={`${namespace}commercePriceList${index}`}
									name={`${namespace}commercePriceList${index}`}
									onChange={({target}) => {
										onChangePriceListHandler({
											currency: priceLists.find(
												(item) =>
													item.value === target.value
											).currency,
											inputGroupId: inputGroup.id,
											priceListId: target.value,
										});
									}}
									options={priceLists}
									required={true}
								/>
							</ClayLayout.Col>

							{hasUnitOfMeasuresRef.current ? (
								<ClayLayout.Col size={4}>
									<label
										htmlFor={`${namespace}commerceUnitOfMeasure${index}`}
									>
										{Liferay.Language.get(
											'unit-of-measure'
										)}

										<span className="inline-item-after reference-mark text-warning">
											<ClayIcon symbol="asterisk" />
										</span>
									</label>

									<ClaySelectWithOption
										id={`${namespace}commerceUnitOfMeasure${index}`}
										name={`${namespace}commerceUnitOfMeasure${index}`}
										onChange={({target}) => {
											onChangeUnitOfMeasureHandler({
												inputGroupId: inputGroup.id,
												unitOfMeasureKey: target.value,
											});
										}}
										options={unitOfMeasures}
										required={true}
									/>
								</ClayLayout.Col>
							) : (
								<></>
							)}

							<ClayLayout.Col
								size={hasUnitOfMeasuresRef.current ? 3 : 5}
							>
								<label
									htmlFor={`${namespace}commercePrice${index}`}
								>
									{Liferay.Language.get('unit-price')}

									<span className="inline-item-after reference-mark text-warning">
										<ClayIcon symbol="asterisk" />
									</span>
								</label>

								<ClayInput.Group>
									<ClayInput.GroupItem prepend>
										<ClayInput
											id={`${namespace}commercePrice${index}`}
											min={0}
											name={`${namespace}commercePrice${index}`}
											onChange={({target}) => {
												onChangePriceHandler({
													inputGroupId: inputGroup.id,
													price: target.value,
												});
											}}
											required={true}
											step={
												1 /
												Math.pow(10, maxFractionDigits)
											}
											type="number"
											value={inputGroup.price}
										/>
									</ClayInput.GroupItem>

									<ClayInput.GroupItem append shrink>
										<ClayInput.GroupText>
											{inputGroup.currency
												? inputGroup.currency
												: currency}
										</ClayInput.GroupText>
									</ClayInput.GroupItem>
								</ClayInput.Group>
							</ClayLayout.Col>

							<ClayLayout.Col
								className="align-content-end d-flex pb-1"
								size={1}
							>
								{index !== 0 && (
									<ClayButtonWithIcon
										aria-label={Liferay.Language.get(
											'remove-entry'
										)}
										borderless
										className="align-self-end"
										displayType="secondary"
										monospaced
										onClick={() =>
											onRemoveInputGroupHandler(
												inputGroup.id
											)
										}
										size="sm"
										symbol="trash"
									/>
								)}
							</ClayLayout.Col>
						</ClayLayout.Row>
					</ClayLayout.ContainerFluid>
				</ClayLayout.Sheet>
			))}

			<ClayLayout.SheetFooter>
				<ClayButton
					displayType="secondary"
					onClick={() => {
						setInputGroups((prevState) => [
							...prevState,
							{
								id: `inputGroup-${++inputGroupCounterRef.current}`,
								price: basePrice,
								priceListId: (priceLists || [])[0]?.value || 0,
								unitOfMeasureKey:
									(unitOfMeasures || [])[0]?.value || null,
							},
						]);
					}}
				>
					<span className="inline-item inline-item-before">
						<ClayIcon symbol="plus" />
					</span>

					{Liferay.Language.get('add-entry')}
				</ClayButton>
			</ClayLayout.SheetFooter>
		</ClayForm>
	);
}

AddPriceEntryForm.defaultProps = {
	basePrice: 0,
	maxFractionDigits: 2,
};

AddPriceEntryForm.propTypes = {
	basePrice: PropTypes.number.isRequired,
	cpInstanceId: PropTypes.number.isRequired,
	currency: PropTypes.string.isRequired,
	dataSetId: PropTypes.string,
	maxFractionDigits: PropTypes.number,
	namespace: PropTypes.string.isRequired,
	priceLists: PropTypes.arrayOf(
		PropTypes.shape({
			currency: PropTypes.string.isRequired,
			label: PropTypes.string.isRequired,
			value: PropTypes.number.isRequired,
		})
	).isRequired,
	unitOfMeasures: PropTypes.arrayOf(
		PropTypes.shape({
			label: PropTypes.string.isRequired,
			value: PropTypes.string.isRequired,
		})
	),
};

export default AddPriceEntryForm;
