/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayCheckbox, ClayInput} from '@clayui/form';
import ClayMultiSelect from '@clayui/multi-select';
import {zodResolver} from '@hookform/resolvers/zod';
import classNames from 'classnames';
import {useEffect, useMemo, useState} from 'react';
import {useForm} from 'react-hook-form';
import {useNavigate} from 'react-router-dom';
import {z} from 'zod';

import HelpPopover from '../../../../components/HelpPopover';
import {Input} from '../../../../components/Input/Input';
import ProductPurchase from '../../../../components/ProductPurchase';
import Select from '../../../../components/Select/Select';
import {useMarketplaceContext} from '../../../../context/MarketplaceContext';
import {Liferay} from '../../../../liferay/liferay';
import zodSchema from '../../../../schema/zod';
import {useProductPurchaseOutletContext} from '../../ProductPurchaseOutlet';
import regions from '../../constants/regions';
import ProductPurchaseAnalytics from '../../services/ProductPurchaseAnalytics';

type MultiSelectValue = {
	key: string;
	label: string;
	value: string;
};

const DATA_CENTER_OPTIONS = [
	{
		key: 'stg',
		name: 'STG',
	},
];

const countries = [...new Set([...regions.map(({country}) => country)])].sort();

const AnalyticsProvisioning = () => {
	const navigate = useNavigate();
	const {handlePurchase, product, selectedAccount} =
		useProductPurchaseOutletContext();
	const {properties} = useMarketplaceContext();
	const [allowedEmailDomainsText, setAllowedEmailDomainsText] = useState('');
	const [incidentReportContactsText, setIncidentReportContactsText] =
		useState('');

	const {formState, handleSubmit, register, setValue, watch} = useForm<
		z.infer<typeof zodSchema.analyticsProvisioning>
	>({
		defaultValues: {
			_refAllowedEmailDomains: [],
			_refIncidentReportContacts: [],
			acceptTerms: false,
			allowedEmailDomains: [],
			dataCenterLocation: DATA_CENTER_OPTIONS[0].key,
			incidentReportContacts: [],
			workspaceOwnerEmail: Liferay.ThemeDisplay.getUserEmailAddress(),
		},
		mode: 'all',
		resolver: zodResolver(zodSchema.analyticsProvisioning),
	});

	const _refAllowedEmailDomains = watch('_refAllowedEmailDomains');
	const _refIncidentReportContacts = watch('_refIncidentReportContacts');
	const timezone = watch('timezone');

	const regionOptions = useMemo(
		() =>
			regions
				.filter((region) => region.country === timezone)
				.map((region) => ({
					key: region.timeZoneId,
					name: region.displayTimeZone,
				})),
		[timezone]
	);

	const onSubmit = async (
		form: z.infer<typeof zodSchema.analyticsProvisioning>
	) => {
		const productPurchase = new ProductPurchaseAnalytics(
			selectedAccount,
			product
		);

		productPurchase.setForm(form);

		await handlePurchase(productPurchase);
	};

	useEffect(() => {
		if (timezone && regionOptions.length) {
			setValue('region', regionOptions[0].key);
		}
	}, [regionOptions, timezone, setValue]);

	return (
		<ProductPurchase.Shell
			footerProps={{
				backButtonProps: {onClick: () => navigate('../')},
				continueButtonProps: {
					children: 'Finish Setup',
					disabled: formState.isSubmitting,
					onClick: handleSubmit(onSubmit),
					type: 'submit',
				},
			}}
			title="Create an Analytics Cloud Workspace"
		>
			<h3 className="sheet-subtitle">General</h3>

			<Input
				{...register('workspaceName')}
				errorMessage={formState.errors.workspaceName?.message}
				label="Workspace Name"
				required
			/>

			<Input
				{...register('workspaceOwnerEmail')}
				disabled
				label="Workspace Owner Email"
				required
			/>

			<Select
				{...register('dataCenterLocation')}
				boldLabel
				helpText={`Select a server to store your data. This could have implications to your organization's policy on user data storage.`}
				label="Data Center Location"
				options={DATA_CENTER_OPTIONS}
				required
			/>

			<div>
				<div className="d-flex flex-column">
					<label htmlFor="timezone">Timezone</label>
					<small>
						Select a timezone that will be used for all data
						reporting in your workspace.
					</small>
				</div>

				<div className="row">
					<div className="col-3">
						<Select
							{...register('timezone')}
							defaultValue="UTC"
							id="timezone"
							options={countries.map((country) => ({
								key: country,
								name: country,
							}))}
						/>
					</div>

					<div className="col-9">
						<Select
							{...register('region')}
							options={regionOptions}
						/>
					</div>
				</div>
			</div>

			<Input
				{...register('friendlyWorkspaceURL')}
				errorMessage={formState.errors.friendlyWorkspaceURL?.message}
				helpMessage={`You can only set your friendly workspace URL once. ${properties.analyticsCloudURL}/workspace`}
				label="Set a Friendly Workspace URL"
				prependGroupItemSymbol="/"
			/>

			<ClayInput.Group
				className={classNames('mt-4', {
					'has-error': formState.errors.allowedEmailDomains?.message,
				})}
			>
				<div className="d-flex flex-column">
					<label htmlFor="allowed-email-domains">
						Allowed Email Domains
					</label>
					<small>
						Anyone with an email address at these domains can
						request access to your workspace.
					</small>
				</div>

				<ClayInput.Group>
					<ClayInput.GroupItem prepend shrink>
						<ClayInput.GroupText>@</ClayInput.GroupText>
					</ClayInput.GroupItem>

					<ClayInput.GroupItem prepend>
						<ClayMultiSelect
							id="allowed-email-domains"
							items={_refAllowedEmailDomains}
							onChange={setAllowedEmailDomainsText}
							onItemsChange={(values: MultiSelectValue[]) => {
								setValue('_refAllowedEmailDomains', values);

								setValue(
									'allowedEmailDomains',
									values.map(({value}) => value)
								);
							}}
							value={allowedEmailDomainsText}
						/>
					</ClayInput.GroupItem>
				</ClayInput.Group>

				<ClayForm.FeedbackItem>
					{formState.errors.allowedEmailDomains?.message}
				</ClayForm.FeedbackItem>
			</ClayInput.Group>

			<h3 className="mt-4 sheet-subtitle">Security</h3>

			<ClayForm.Group
				className={classNames('mt-4', {
					'has-error':
						formState.errors.incidentReportContacts?.length ||
						formState.errors.incidentReportContacts?.message,
				})}
			>
				<div className="d-flex flex-column">
					<div>
						<label
							className="required"
							htmlFor="incident-report-contacts"
						>
							Add Incident Report Contacts{' '}
						</label>

						<HelpPopover header="Incident Report Contact">
							<span>
								This person will be contacted in the event of:
							</span>

							<ul>
								<li>Services interruptions;</li>
								<li>Security incidents;</li>
								<li>
									Other urgent service updates that require
									action.
								</li>
							</ul>
						</HelpPopover>
					</div>
					<small>
						Who should we contact in case of a security breach?
					</small>
				</div>

				<ClayMultiSelect
					id="incident-report-contacts"
					items={_refIncidentReportContacts}
					onChange={setIncidentReportContactsText}
					onItemsChange={(values: MultiSelectValue[]) => {
						setValue('_refIncidentReportContacts', values);

						setValue(
							'incidentReportContacts',
							values.map(({value}) => value)
						);
					}}
					value={incidentReportContactsText}
				/>

				<ClayForm.FeedbackItem>
					{Array.isArray(formState.errors.incidentReportContacts)
						? formState.errors.incidentReportContacts?.[0]?.message
						: formState.errors.incidentReportContacts?.message}
				</ClayForm.FeedbackItem>
			</ClayForm.Group>

			<ClayForm.Group
				className={classNames('mt-4', {
					'has-error': formState.errors.acceptTerms?.message,
				})}
			>
				<ClayCheckbox
					{...({} as any)}
					{...register('acceptTerms')}
					id="accept-terms"
					label="I agree"
				/>

				<ClayForm.FeedbackItem>
					{formState.errors.acceptTerms?.message}
				</ClayForm.FeedbackItem>

				<label className="font-weight-normal" htmlFor="accept-terms">
					By selecting &quot;I Agree&quot;, you agree to our{' '}
					<a
						href="https://www.liferay.com/legal/marketplace-terms-of-service"
						target="_blank"
					>
						Terms and Conditions
					</a>{' '}
					including our{' '}
					<a
						href="https://www.liferay.com/privacy-policy"
						target="_blank"
					>
						Privacy Policy.
					</a>
				</label>
			</ClayForm.Group>
		</ProductPurchase.Shell>
	);
};

export default AnalyticsProvisioning;
