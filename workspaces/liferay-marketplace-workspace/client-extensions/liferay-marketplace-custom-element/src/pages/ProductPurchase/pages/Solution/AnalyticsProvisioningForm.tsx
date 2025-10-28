/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayMultiSelect from '@clayui/multi-select';
import {zodResolver} from '@hookform/resolvers/zod';
import classNames from 'classnames';
import {useMemo, useState} from 'react';
import {useForm} from 'react-hook-form';
import useSWR from 'swr';
import {z} from 'zod';

import HelpPopover from '../../../../components/HelpPopover';
import {Input} from '../../../../components/Input/Input';
import Loading from '../../../../components/Loading';
import ProductPurchase from '../../../../components/ProductPurchase';
import Select from '../../../../components/Select/Select';
import {Liferay} from '../../../../liferay/liferay';
import zodSchema from '../../../../schema/zod';
import analyticsOAuth2 from '../../../../services/oauth/Analytics';
import {useProductPurchaseOutletContext} from '../../ProductPurchaseOutlet';
import ProductPurchaseAnalytics from '../../services/ProductPurchaseAnalytics';

type MultiSelectValue = {
	key: string;
	label: string;
	value: string;
};

const DATA_CENTER_OPTIONS = [
	{
		key: 'internal',
		name: 'Internal',
	},
];

const AnalyticsProvisioning = () => {
	const [incidentReportContactsText, setIncidentReportContactsText] =
		useState('');

	const {handlePurchase, product, selectedAccount, setAlert} =
		useProductPurchaseOutletContext();

	const accountKey = selectedAccount.externalReferenceCode;

	const {
		data: analyticsPlan,
		error,
		isLoading,
	} = useSWR(
		accountKey ? `/ac-plan/${accountKey}` : null,
		async () => {
			return analyticsOAuth2.getPlan(accountKey);
		},
		{
			onSuccess: (productPurchase) => {
				if (!productPurchase) {
					return;
				}

				if (productPurchase?.product?.name?.includes('Basic')) {
					setAlert(
						<span>
							The basic plan supports up to 1,000 known
							individuals and 300,000 recorded page views. If you
							require more capacity visit the Customer Portal to
							contact your account manager about Liferay Analytics
							Cloud plan options.
						</span>
					);
				}
			},
		}
	);

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
			subscriptionType: 'Basic Plan',
			workspaceOwnerEmail: Liferay.ThemeDisplay.getUserEmailAddress(),
		},
		mode: 'all',
		resolver: zodResolver(zodSchema.analyticsProvisioning),
	});

	const subscriptionType = useMemo(() => {
		if (!analyticsPlan?.product) {
			return 'Basic Plan';
		}

		const name = analyticsPlan?.product?.name?.replace(
			'Analytics Cloud',
			''
		);

		return `${name} Plan`;
	}, [analyticsPlan]);

	const _refIncidentReportContacts = watch('_refIncidentReportContacts');

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

	if (isLoading || !accountKey) {
		return <Loading />;
	}

	if (error?.status) {
		return (
			<div className="align-items-center d-flex flex-column justify-content-center px-4 text-center">
				<div className="analytics-form-alert">
					<ClayIcon
						color="#0B5FFF"
						fontSize={32}
						symbol="warning-full"
					/>
				</div>

				<h2>DXP Version not Currently Supported</h2>

				<small>
					DXP versions below 7.4 are no longer supported. To access
					Analytics Cloud, <a href="">upgrade to the latest DXP</a>{' '}
					version or <a href="">visit the Customer Portal</a> to
					contact your account manager about Liferay Analytics Cloud
					plan options.
				</small>
			</div>
		);
	}

	return (
		<ProductPurchase.Shell
			footerProps={{
				backButtonProps: {className: 'd-none'},
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
				label="Subscription Type"
				readOnly
				required
				value={subscriptionType}
			/>

			<Input
				{...register('workspaceName')}
				errorMessage={formState.errors?.workspaceName?.message}
				label="Workspace Name"
				required
			/>

			<Input
				{...register('workspaceOwnerEmail')}
				errorMessage={formState.errors?.workspaceOwnerEmail?.message}
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
