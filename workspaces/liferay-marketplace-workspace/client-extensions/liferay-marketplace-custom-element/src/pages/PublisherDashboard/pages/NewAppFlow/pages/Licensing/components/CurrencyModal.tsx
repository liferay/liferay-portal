/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker, useModal} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import React, {ComponentProps, useState} from 'react';

import Modal from '../../../../../../../components/Modal';
import {
	NewAppTypes,
	useNewAppContext,
} from '../../../../../../../context/NewAppContext';
import {ProductLicenseTier} from '../../../../../../../enums/Product';
import i18n from '../../../../../../../i18n';
import {currenciesCode} from '../../../../../../../utils/currencies';

export type CurrencyFlagProps = (typeof currenciesCode)[number];

const CurrencyFlag = ({flag, iconSrc}: CurrencyFlagProps) => {
	if (iconSrc) {
		return (
			<img
				alt={`${flag} Flag`}
				className="currency-selector-icon ml-2"
				src={iconSrc}
			/>
		);
	}

	return (
		<ClayIcon
			className="currency-selector-icon ml-2"
			symbol={flag || 'en-us'}
		/>
	);
};

const CurrencyTrigger = (selectedNewCurrency: string) =>
	React.forwardRef<HTMLDivElement, {children?: React.ReactNode}>(
		({children, ...props}, ref) => {
			const currency = currenciesCode.find(
				(item) => item.code === selectedNewCurrency
			);

			return (
				<div
					ref={ref}
					{...props}
					className="form-control form-control-select"
					tabIndex={0}
				>
					{children || 'Choose a option'}

					{currency && (
						<span className="ml-2">
							<CurrencyFlag {...currency} />
						</span>
					)}
				</div>
			);
		}
	);

const CurrencyModal = ({observer, onClose}: ReturnType<typeof useModal>) => {
	const [selectedNewCurrency, setSelectedNewCurrency] = useState('');

	const [
		{
			licensing: {prices},
		},
		dispatch,
	] = useNewAppContext();

	const activeCurrencies = Object.keys(prices);

	const handleAddCurrency = () => {
		dispatch({
			payload: {
				currency: selectedNewCurrency,
				licenseTier: ProductLicenseTier.STANDARD,
			},
			type: NewAppTypes.SET_LICENSING_ADD_PRICE,
		});

		onClose();

		setSelectedNewCurrency('');
	};

	return (
		<Modal
			className="currency-selector-modal"
			last={
				<ClayButton.Group spaced>
					<ClayButton displayType="secondary" onClick={onClose}>
						{i18n.translate('cancel')}
					</ClayButton>

					<ClayButton
						disabled={!selectedNewCurrency}
						onClick={handleAddCurrency}
					>
						{i18n.translate('confirm')}
					</ClayButton>
				</ClayButton.Group>
			}
			observer={observer}
			size={'md' as ComponentProps<typeof Modal>['size']}
			subtitle="Choose one of the following currencies"
			title="Select Desired Currency"
			visible
		>
			<div className="currency-selector-container">
				<label htmlFor="currency-picker" id="currency-picker-label">
					{i18n.translate('choose-currency')}
				</label>

				<Picker
					as={CurrencyTrigger(selectedNewCurrency)}
					id="currency-picker"
					items={currenciesCode}
					messages={{
						itemDescribedby: Liferay.Language.get(
							'you-are-currently-on-a-text-element,-inside-of-a-list-box'
						),
						itemSelected: Liferay.Language.get('x-selected'),
						scrollToBottomAriaLabel:
							Liferay.Language.get('scroll-to-bottom'),
						scrollToTopAriaLabel:
							Liferay.Language.get('scroll-to-top'),
					}}
					onSelectionChange={(key: any) =>
						setSelectedNewCurrency(key)
					}
					width={200}
				>
					{(item) => (
						<Option
							disabled={activeCurrencies.includes(item.code)}
							key={item.code}
							textValue={item.code}
						>
							<span className="align-items-center d-flex">
								<span>{item.code}</span>

								<CurrencyFlag {...item} />
							</span>
						</Option>
					)}
				</Picker>
			</div>
		</Modal>
	);
};

export {CurrencyFlag};
export default CurrencyModal;
