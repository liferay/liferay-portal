/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {Size} from '@clayui/modal/lib/types';
import {zodResolver} from '@hookform/resolvers/zod';
import {ChangeEvent, useState} from 'react';
import {useForm} from 'react-hook-form';

import {Input} from '../../../components/Input/Input';
import Form from '../../../components/MarketplaceForm';
import Modal from '../../../components/Modal';
import Select from '../../../components/Select/Select';
import useCommerceRegions from '../../../hooks/useCommerceRegions';
import i18n from '../../../i18n';
import {Liferay} from '../../../liferay/liferay';
import zodSchema, {z} from '../../../schema/zod';
import marketplaceOAuth2 from '../../../services/oauth/Marketplace';
import CommerceSelectAccount from '../../../services/rest/CommerceSelectAccount';
import AcountSelectDropDown from '../components/AccountSelectDropDown/AccountSelectDropDown';
import {AccountType} from '../constants';

import './CreateAccountModalForm.scss';

type CreateAccountModalFormProps = {
	modal: {
		observer: any;
		onClose: () => void;
		open: boolean;
	};
};

type FormFields = z.infer<typeof zodSchema.accountForm>;

const defaultFormValues = {
	accountImage: undefined,
	accountName: '',
	accountType: 'business',
	billingAddress: {
		city: '',
		country: '',
		countryISOCode: '',
		name: '',
		phoneNumber: '',
		regionISOCode: '',
		street1: '',
		street2: '',
		zip: '',
	},
	emailAddress: Liferay.ThemeDisplay.getUserEmailAddress(),
	taxNumber: '',
};

const CreateAccountModalForm: React.FC<CreateAccountModalFormProps> = ({
	modal,
}) => {
	const {
		formState: {errors, isSubmitting},
		handleSubmit,
		register,
		reset,
		setValue,
		watch,
	} = useForm<FormFields>({
		defaultValues: defaultFormValues,
		resolver: zodResolver(zodSchema.accountForm),
	});

	const [previewImg, setPreviewImg] = useState('');
	const {accountImage, accountType, billingAddress} = watch();
	const {data: regionsResponse} = useCommerceRegions();

	const regions = regionsResponse?.items || [];

	const states =
		regions.find((region) => region.a2 === billingAddress?.country)
			?.regions ?? [];

	const isTypeExistingBusiness = accountType === 'existing-business';

	const getAccountType = (key: string) => {
		return AccountType.find((option) => option.key === key);
	};

	const handleOnClose = () => {
		reset(defaultFormValues);

		modal.onClose();
	};

	const onSubmit = async (data: FormFields) => {
		try {
			const account = await marketplaceOAuth2.createAccount(data);
			await CommerceSelectAccount.selectAccount(account.id);
			window.location.reload();
		}
		catch (error) {
			console.error(error);

			Liferay.Util.openToast({
				message: i18n.translate('an-unexpected-error-occurred'),
				type: 'danger',
			});
		}

		handleOnClose();
	};

	if (!modal.open) {
		return null;
	}

	const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
		const file = event.target.files?.[0];

		if (!file) {
			setValue('accountImage', undefined);

			return;
		}
		setValue('accountImage', file);

		const url = URL.createObjectURL(file);
		setPreviewImg(url);
	};

	const handleDeleteImage = () => {
		setValue('accountImage', undefined);
		setPreviewImg('');
	};

	return (
		<Modal
			observer={modal.observer}
			size={'md' as Size}
			visible={modal.open}
		>
			<div className="create-account-modal-form-container">
				<div>
					<h1 className="create-account-modal-form-title">
						{i18n.translate('new-account')}
					</h1>

					<p className="text-muted">
						{i18n.translate(
							'create-a-new-marketplace-acccount-or-join-an-already-existing-one'
						)}
					</p>
				</div>
				<ClayForm.Group className="mb-3 pr-2 w-100">
					<Form.Label className="mb-2" info="Placeholder" required>
						{i18n.translate('account-type')}
					</Form.Label>

					<AcountSelectDropDown
						onChange={(value: string) =>
							setValue('accountType', value)
						}
						options={AccountType}
						value={getAccountType(accountType)}
					/>
				</ClayForm.Group>

				{isTypeExistingBusiness ? (
					<div className="c-gap-2 d-flex my-5">
						<div>
							<ClayIcon
								className="text-muted"
								symbol="warning-full"
							/>
						</div>
						<div>
							<p>
								<strong>
									{i18n.translate(
										'contact-your-administrator-to-become-part-of-an-account'
									)}
								</strong>
							</p>
							<p>
								{i18n.translate(
									'to-join-an-existing-business-account-pleasecontact-your-administrator-who-can-add-you-once-added-you-will-automatically-become-part-of-that-account-and-will-be-able-to-manage-it-and-make-purchases-on-markeplace'
								)}
							</p>
							<p>
								<strong>
									Any questions?
									<a
										className="ml-1"
										href="mailto:marketplace-admin@liferay.com"
									>
										marketplace-admin@liferay.com
									</a>
								</strong>
							</p>
						</div>
					</div>
				) : (
					<>
						<div className="mt-6">
							<h3>{i18n.translate('account-details')}</h3>

							<hr className="mb-2 mt-2" />
						</div>
						<ClayForm.Group className="pr-2 w-100">
							<div className="account-image-container">
								{accountImage ? (
									<img
										className="account-image"
										src={previewImg}
									/>
								) : (
									<div className="account-image account-image-default align-items-center d-flex justify-content-center py-5">
										<ClayIcon
											aria-label="New App logo"
											className="text-muted"
											symbol="picture"
										/>
									</div>
								)}
								<input
									accept="image/jpeg, image/png"
									hidden
									id="file"
									name="file"
									onChange={handleFileChange}
									type="file"
								/>

								<label
									className="btn btn-primary btn-sm m-0"
									htmlFor="file"
								>
									{i18n.translate('upload-image')}
								</label>

								{accountImage && (
									<Button
										className="btn btn-secondary btn-sm m-0"
										onClick={handleDeleteImage}
									>
										{i18n.translate('delete')}
									</Button>
								)}
							</div>
						</ClayForm.Group>
						<ClayForm.Group>
							<div className="c-gap-4 d-flex m-0">
								<div className="pr-2 w-100">
									<Form.Label className="mb-2" required>
										{i18n.translate('account-name')}
									</Form.Label>

									<Input
										{...register('accountName')}
										errorMessage={
											errors.accountName?.message
										}
										name="accountName"
										required
										type="text"
									/>
								</div>
								<div className="pr-2 w-100">
									<Form.Label className="mb-2" required>
										{i18n.translate('email-address')}
									</Form.Label>

									<Input
										{...register('emailAddress')}
										disabled
										required
										type="text"
									/>
								</div>
							</div>
							<div>
								<Form.Label className="mb-2" required>
									{i18n.translate('tax-vat-number')}
								</Form.Label>

								<Input
									{...register('taxNumber')}
									errorMessage={errors.taxNumber?.message}
									required
									type="text"
								/>
							</div>
						</ClayForm.Group>

						<ClayForm.Group className="mb-3 mt-2 pr-2 w-100">
							<div>
								<h3>{i18n.translate('billing-address')}</h3>

								<hr className="mb-3" />
							</div>

							<div className="c-gap-4 d-flex">
								<div className="pr-2 w-100">
									<Form.Label className="mb-2" required>
										{i18n.translate('address-name')}
									</Form.Label>

									<Input
										{...register('billingAddress.name')}
										errorMessage={
											errors.billingAddress?.name?.message
										}
										name="billingAddress.name"
										required
										type="text"
									/>
								</div>
								<div className="pr-2 w-100">
									<Form.Label className="mb-2" required>
										Phone
									</Form.Label>

									<Input
										{...register(
											'billingAddress.phoneNumber'
										)}
										errorMessage={
											errors.billingAddress?.phoneNumber
												?.message
										}
										type="text"
									/>
								</div>
							</div>
							<div className="c-gap-4 d-flex">
								<div className="pr-2 w-100">
									<Form.Label className="mb-2" required>
										Street address
									</Form.Label>

									<Input
										{...register('billingAddress.street1')}
										errorMessage={
											errors.billingAddress?.street1
												?.message
										}
										name="billingAddress.street1"
										required
										type="text"
									/>
								</div>
								<div className="pr-2 w-100">
									<Form.Label className="mb-2">
										Apt, Suite, Unit (optional)
									</Form.Label>

									<Input
										{...register('billingAddress.street2')}
										errorMessage={
											errors.billingAddress?.street2
												?.message
										}
										type="text"
									/>
								</div>
							</div>
							<div className="c-gap-4 d-flex">
								<div className="pr-2 w-100">
									<Form.Label className="mb-2" required>
										Country
									</Form.Label>

									<Select
										className="custom-input"
										name="country"
										onChange={({target: {value}}) => {
											const states =
												regions.find(
													(region) =>
														region.a2 === value
												)?.regions ?? [];

											const regionISOCode =
												!!states.length &&
												states[0].regionCode;

											setValue(
												'billingAddress.country',
												value
											);
											setValue(
												'billingAddress.countryISOCode',
												value
											);
											setValue(
												'billingAddress.regionISOCode',
												regionISOCode as string
											);
										}}
										options={regions.map((region) => ({
											key: region.a2,
											name:
												region.title_i18n[
													Liferay.ThemeDisplay.getLanguageId()
												] ||
												region.title_i18n[
													Liferay.ThemeDisplay.getDefaultLanguageId()
												] ||
												region.name,
										}))}
										required
										value={billingAddress.country}
									/>
								</div>
								<div className="pr-2 w-100">
									<Form.Label className="mb-2" required>
										State/Region
									</Form.Label>

									<Select
										boldLabel
										className="custom-input"
										defaultOption={false}
										disabled={!states.length}
										name="regionISOCode"
										onChange={({target: {value}}) =>
											setValue(
												'billingAddress.regionISOCode',
												value
											)
										}
										options={states.map((state) => ({
											key: state.regionCode,
											name: state.name,
										}))}
										required={!!states.length}
										value={billingAddress.regionISOCode}
									/>
								</div>
							</div>
							<div className="c-gap-4 d-flex">
								<div className="pr-2 w-100">
									<Form.Label className="mb-2" required>
										City
									</Form.Label>

									<Input
										{...register('billingAddress.city')}
										errorMessage={
											errors.billingAddress?.city?.message
										}
										name="billingAddress.city"
										required
										type="text"
									/>
								</div>
								<div className="pr-2 w-100">
									<Form.Label className="mb-2" required>
										Zip Code
									</Form.Label>

									<Input
										{...register('billingAddress.zip')}
										errorMessage={
											errors.billingAddress?.zip?.message
										}
										type="text"
									/>
								</div>
							</div>
						</ClayForm.Group>
					</>
				)}

				<div className="d-flex justify-content-end">
					<Button
						className="mr-2"
						disabled={isSubmitting}
						displayType="secondary"
						onClick={handleOnClose}
					>
						{i18n.translate('cancel')}
					</Button>

					<Button
						disabled={isSubmitting || isTypeExistingBusiness}
						displayType="primary"
						onClick={handleSubmit(onSubmit)}
					>
						<div className="align-items-center d-flex">
							{isSubmitting && (
								<ClayLoadingIndicator className="mr-3 my-0" />
							)}

							{i18n.translate('save')}
						</div>
					</Button>
				</div>
			</div>
		</Modal>
	);
};

export default CreateAccountModalForm;
