/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Autocomplete from '@clayui/autocomplete';
import ClayButton from '@clayui/button';
import ClayForm, {ClayInput, ClayToggle} from '@clayui/form';
import ClayMultiSelect from '@clayui/multi-select';
import classNames from 'classnames';
import {useState} from 'react';
import {useForm} from 'react-hook-form';

import BaseWrapper from '../../../../../components/Input/base/BaseWrapper';
import Select from '../../../../../components/Select/Select';
import {useMarketplaceContext} from '../../../../../context/MarketplaceContext';
import {OrderCustomFields} from '../../../../../enums/Order';
import useDebounce from '../../../../../hooks/useDebounce';
import i18n from '../../../../../i18n';
import {Liferay} from '../../../../../liferay/liferay';
import zodSchema, {z, zodResolver} from '../../../../../schema/zod';
import {getProductType} from '../../../../../utils/productUtils';
import ProductPurchaseSolutionTrial from '../../../../ProductPurchase/services/ProductPurchasePreBuiltTrial';
import {useTrialProducts} from './useTrialProducts';

type NewTrialModalProps = {
	onClose: () => void;
	revalidate: () => void;
};

type MultiSelectValue = {
	key: string;
	label: string;
	value: string;
};

const defaultEmailAddress = Liferay.ThemeDisplay.getUserEmailAddress();

const NewTrialModal: React.FC<NewTrialModalProps> = ({onClose, revalidate}) => {
	const {channel, myUserAccount} = useMarketplaceContext();

	const {formState, handleSubmit, register, setValue, trigger, watch} =
		useForm({
			defaultValues: {
				_refInviteEmailAddresses: [
					{
						key: defaultEmailAddress,
						label: defaultEmailAddress,
						value: defaultEmailAddress,
					},
				],
				accountId: String(myUserAccount.accountBriefs[0].id),
				consoleInviteEmailAddresses: [defaultEmailAddress],
				product: undefined,
				sendNotificationEmail: false,
			},
			mode: 'all',
			reValidateMode: 'onChange',
			resolver: zodResolver(zodSchema.trialForm),
		});

	const {isValid} = formState;

	const _refInviteEmailAddresses = watch('_refInviteEmailAddresses');
	const {accountBriefs = []} = myUserAccount;

	const [search, setSearch] = useState('');
	const [emailAddressText, setEmailAddressText] = useState('');
	const debouncedSearch = useDebounce(search, 1500);

	const {data: apps, isValidating} = useTrialProducts(
		channel.id,
		debouncedSearch
	);

	const onSubmit = async (form: z.infer<typeof zodSchema.trialForm>) => {
		const product = form.product as DeliveryProduct;

		const {isDXP} = getProductType(product);

		if (isDXP) {
			return Liferay.Util.openToast({
				message: 'Not possible to create Trial for DXP Apps',
				type: 'danger',
			});
		}

		const account = accountBriefs.find(
			({id}) => id === Number(form.accountId)
		) as unknown as Account;

		const productPurchase = new ProductPurchaseSolutionTrial(
			account,
			product
		);

		try {
			await productPurchase.createOrder({
				customFields: {
					[OrderCustomFields.TRIAL_SETTINGS]: JSON.stringify({
						consoleInviteEmailAddresses:
							form.consoleInviteEmailAddresses,
						sendNotificationEmail: form.sendNotificationEmail,
					}),
				},
			} as Cart);

			await revalidate();

			setTimeout(() => revalidate(), 5000);

			Liferay.Util.openToast({
				message: 'Trial created successfully',
				type: 'success',
			});

			onClose();
		}
		catch (error) {
			console.error(error);

			Liferay.Util.openToast({
				message: i18n.translate('an-unexpected-error-occurred'),
				type: 'danger',
			});
		}
	};

	return (
		<div className="pb-8">
			<BaseWrapper boldLabel label="Cloud App" required>
				<Autocomplete
					filterKey="productName"
					items={apps?.items || []}
					loadingState={isValidating ? 1 : 4}
					messages={{
						loading: 'Loading...',
						notFound: 'No results found',
					}}
					onChange={setSearch}
					placeholder="Search for the app name"
					value={search}
				>
					{(product) => (
						<Autocomplete.Item
							{...({} as any)}
							disabled
							key={product.productId}
							onClick={() => {
								setValue('product', product as any);

								trigger();
							}}
						>
							{product.name}
						</Autocomplete.Item>
					)}
				</Autocomplete>
			</BaseWrapper>

			<Select
				{...register('accountId')}
				boldLabel
				defaultOptionLabel={i18n.translate('account-selection')}
				helpText="Account where this Order will be registered."
				label={i18n.translate('account-selection')}
				options={accountBriefs
					.sort((accountA, accountB) =>
						accountA.name.localeCompare(accountB.name)
					)
					.map((account) => ({
						key: account.id.toString(),
						name: account.name,
					}))}
				required
			/>

			<ClayInput.Group
				className={classNames('my-4', {
					'has-error':
						formState.errors.consoleInviteEmailAddresses?.length,
				})}
			>
				<div className="d-flex flex-column">
					<label htmlFor="allowed-email-domains">
						Invite Members
					</label>

					<small>
						Everyone with an email address at these list will be
						invited to the Cloud Environment.
					</small>
				</div>

				<ClayInput.Group>
					<ClayInput.GroupItem prepend>
						<ClayMultiSelect
							items={_refInviteEmailAddresses}
							onChange={setEmailAddressText}
							onItemsChange={(values: MultiSelectValue[]) => {
								setValue(
									'_refInviteEmailAddresses',
									values as any
								);

								setValue(
									'consoleInviteEmailAddresses',
									values.map(({value}) => value) as any
								);
							}}
							value={emailAddressText}
						/>
					</ClayInput.GroupItem>
				</ClayInput.Group>

				<ClayForm.FeedbackItem>
					{formState.errors.consoleInviteEmailAddresses?.[0]?.message}
				</ClayForm.FeedbackItem>
			</ClayInput.Group>

			<ClayToggle
				label="Send Notification Email"
				onToggle={(value) => setValue('sendNotificationEmail', value)}
				toggled={watch('sendNotificationEmail')}
			/>

			<div className="d-flex justify-content-end">
				<ClayButton
					disabled={!isValid}
					onClick={handleSubmit(onSubmit)}
				>
					Create Trial
				</ClayButton>
			</div>
		</div>
	);
};

export default NewTrialModal;
