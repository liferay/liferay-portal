/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import DropDown from '@clayui/drop-down';
import ClayForm, {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {Fragment, useState} from 'react';
import {UseFormReturn} from 'react-hook-form';
import {useNavigate} from 'react-router-dom';

import {Header} from '../../../components/Header/Header';
import FormInput from '../../../components/Input/formInput';
import {Tooltip} from '../../../components/Tooltip/Tooltip';
import i18n from '../../../i18n';
import {Liferay} from '../../../liferay/liferay';
import {phones} from '../../../utils/phones';
import {getSiteURL} from '../../../utils/site';
import {PublisherForm, PublisherGateStep} from './PublisherGateSteps';

type PublisherGateFormProps = {
	form: UseFormReturn<PublisherForm, any>;
	listTypeDefinition?: ListTypeDefinition;
	setStep: React.Dispatch<React.SetStateAction<PublisherGateStep>>;
};

const tooltipText = {
	appPublisher: 'Ability to publish DXP and Cloud - Free or Charged',
	solutionPublisher:
		'Solutions built on Liferay, requires existing Liferay Partnership',
};

const PublisherGateForm: React.FC<PublisherGateFormProps> = ({
	form,
	listTypeDefinition,
	setStep,
}) => {
	const phone = form.watch('phone');
	const [currentPhonesFlags, setCurrentPhonesFlags] = useState(phone);
	const navigate = useNavigate();

	const listTypeEntries = listTypeDefinition?.listTypeEntries ?? [];

	const inputProps = {
		errors: form.formState.errors,
		register: form.register,
		required: true,
	};

	return (
		<>
			<div className="publisher-gate-page-body">
				<Header
					description={i18n.translate(
						'enter-your-contact-details-in-the-fields-below-in-the-request-description-enter-the-name-and-a-brief-description-of-the-app-you-would-like-to-submit-we-will-be-in-contact-regarding-your-request'
					)}
					title={i18n.translate(
						'request-a-marketplace-publisher-account'
					)}
				/>
				<ClayForm>
					<ClayForm.Group className="mb-0">
						<div className="d-flex justify-content-between">
							<div className="form-group mb-0 pr-2 w-50">
								<FormInput
									{...inputProps}
									boldLabel
									className="custom-input"
									label={i18n.translate('first-name')}
									name="firstName"
									placeholder={i18n.translate(
										'enter-first-name'
									)}
									type="text"
								/>
							</div>

							<div className="form-group mb-0 pl-2 w-50">
								<FormInput
									{...inputProps}
									boldLabel
									className="custom-input"
									label={i18n.translate('last-name')}
									name="lastName"
									placeholder={i18n.translate(
										'enter-last-name'
									)}
									type="email"
								/>
							</div>
						</div>
					</ClayForm.Group>

					<ClayForm.Group>
						<label className="required" htmlFor="phone">
							{i18n.translate('phone')}
						</label>

						<div className="d-flex justify-content-between purchased-solutions-phone">
							<div className="col-3 p-0">
								<DropDown
									closeOnClick
									tabIndex={0}
									trigger={
										<div className="align-items-center custom-input custom-select d-flex form-control p-2 rounded-xs">
											<ClayIcon
												className="mr-2"
												symbol={
													currentPhonesFlags?.flag as string
												}
											/>

											{currentPhonesFlags?.code}
										</div>
									}
								>
									<DropDown.ItemList items={phones as any}>
										{(item) => {
											const phone = item as any;

											return (
												<DropDown.Item
													onClick={() => {
														setCurrentPhonesFlags({
															code: phone.code,
															flag: phone.flag,
														});

														form.setValue('phone', {
															code: phone.code,
															flag: phone.flag,
														});
													}}
												>
													<ClayIcon
														className="mr-2"
														symbol={phone.flag}
													/>

													{phone.code}
												</DropDown.Item>
											);
										}}
									</DropDown.ItemList>
								</DropDown>

								<div className="form-feedback-group">
									<div className="form-text">
										{i18n.translate('intl-code')}
									</div>
								</div>
							</div>

							<div className="col-6">
								<FormInput
									{...inputProps}
									className="custom-input w-100"
									description={i18n.translate('phone-number')}
									name="phoneNumber"
									placeholder="___–___–____"
								/>
							</div>

							<div className="col-3 p-0">
								<FormInput
									{...inputProps}
									className="custom-input mr-0 pl-1 text-nowrap"
									description={i18n.translate(
										'extension-optional'
									)}
									name="extension"
									placeholder="Enter +ext"
								/>
							</div>
						</div>
					</ClayForm.Group>

					<ClayForm.Group>
						<label className="mb-4">
							Select your desired publisher type
						</label>

						{listTypeEntries.map((listTypeEntry, index) => (
							<Fragment key={index}>
								<ClayCheckbox
									aria-label={listTypeEntry.name}
									checked={form
										.watch('publisherType')
										.includes(listTypeEntry.key)}
									key={index}
									label={
										(
											<div className="d-flex justify-content-between w-25">
												{listTypeEntry.name}
												{(tooltipText as any)[
													listTypeEntry.key
												] && (
													<Tooltip
														showTooltipBackground={
															false
														}
														tooltip={
															(
																tooltipText as any
															)[listTypeEntry.key]
														}
													/>
												)}
											</div>
										) as any
									}
									value={listTypeEntry.key}
									{...form.register('publisherType')}
								/>
							</Fragment>
						))}
					</ClayForm.Group>

					<div className="form-group mb-5">
						<FormInput
							{...inputProps}
							boldLabel
							className="custom-input"
							label={i18n.translate('email')}
							name="emailAddress"
							placeholder={i18n.translate('enter-email-address')}
							type="email"
						/>
					</div>

					<div className="form-group mb-5">
						<FormInput
							{...inputProps}
							boldLabel
							className="custom-input"
							helpMessage={i18n.translate(
								'if-you-are-requesting-the-account-on-behalf-of-a-business-,-please-note-the-business-name'
							)}
							label={i18n.translate('request-description')}
							name="requestDescription"
							placeholder={i18n.translate(
								'enter-the-name-and-a-brief-description-of-the-app-you-would-like-to-submit'
							)}
							type="textarea"
						/>
					</div>
				</ClayForm>

				<hr className="mb-5 mt-8" />

				<div className="mb-8 purchased-solutions-button-container">
					<div className="align-items-center d-flex justify-content-between mb-4 w-100">
						<ClayButton
							className="p-3"
							displayType="unstyled"
							onClick={() => {
								window.location.href = `${Liferay.ThemeDisplay.getPortalURL()}${getSiteURL()}/home`;
							}}
						>
							{i18n.translate('cancel')}
						</ClayButton>

						<div>
							<ClayButton
								className="mr-4"
								displayType="secondary"
								onClick={() => navigate('/')}
							>
								{i18n.translate('back')}
							</ClayButton>

							<ClayButton
								disabled={!form.formState.isValid}
								onClick={() =>
									setStep(PublisherGateStep.SUMMARY)
								}
							>
								{i18n.translate('continue')}
							</ClayButton>
						</div>
					</div>
				</div>
			</div>
		</>
	);
};

export default PublisherGateForm;
