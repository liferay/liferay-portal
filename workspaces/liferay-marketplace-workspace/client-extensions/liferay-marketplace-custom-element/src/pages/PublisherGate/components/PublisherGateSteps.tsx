/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useState} from 'react';
import {useForm} from 'react-hook-form';
import {z} from 'zod';

import {useMarketplaceContext} from '../../../context/MarketplaceContext';
import useListTypeDefinition from '../../../hooks/useListTypeDefinition';
import i18n from '../../../i18n';
import zodSchema, {zodResolver} from '../../../schema/zod';
import fetcher from '../../../services/fetcher';
import PublisherGateForm from './PublisherGateForm';
import PublisherGateSummary from './PublisherGateSummary';
import PubliserhRequestedCard from './PublisherRequestedCard';
import PublisherSummaryContent from './PublisherSummaryContent';

export type PublisherForm = z.infer<typeof zodSchema.becomePublisherForm>;

export enum PublisherGateStep {
	FORM = 'form',
	SUMMARY = 'summary',
	REQUESTED = 'requested',
}

const PublisherGateSteps = () => {
	const {myUserAccount} = useMarketplaceContext();
	const [step, setStep] = useState<PublisherGateStep>(PublisherGateStep.FORM);
	const userPhone =
		myUserAccount?.userAccountContactInformation?.telephones || [];

	const form = useForm<PublisherForm>({
		defaultValues: {
			emailAddress: myUserAccount ? myUserAccount?.emailAddress : '',
			extension: userPhone?.length ? userPhone[0]?.extension : '',
			firstName: myUserAccount ? myUserAccount?.givenName : '',
			lastName: myUserAccount ? myUserAccount?.familyName : '',
			phone: {
				code: '+1',
				flag: 'en-us',
			},
			phoneNumber: userPhone?.length ? userPhone[0]?.phoneNumber : '',
			publisherType: ['appPublisher'],
			requestDescription: '',
		},
		mode: 'onBlur',
		resolver: zodResolver(zodSchema.becomePublisherForm),
	});

	const userInfo = form.watch();

	const {data} = useListTypeDefinition('PUBLISHER-TYPE');

	const submit = async (form: PublisherForm) => {
		const formData = {...form, intlCode: form?.phone?.code};

		delete formData.phone;

		try {
			await fetcher.post('o/c/requestpublisheraccounts/', formData);

			setStep(PublisherGateStep.REQUESTED);
		}
		catch (error) {
			console.error(error);
		}
	};

	const StepsAccount = {
		[PublisherGateStep.FORM]: {
			component: (
				<PublisherGateForm
					form={form}
					listTypeDefinition={data}
					setStep={setStep}
				/>
			),
		},
		[PublisherGateStep.SUMMARY]: {
			component: (
				<PublisherGateSummary
					setStep={setStep}
					submit={form.handleSubmit(submit)}
				>
					<div className="mt-8">
						<PublisherSummaryContent
							title={i18n.translate('request-details')}
							userInfo={
								{
									...userInfo,
									phone: {
										code: userInfo?.phone?.code as string,
										flag: userInfo?.phone?.flag as string,
									},
									publisherType: userInfo.publisherType.map(
										(type) =>
											data?.listTypeEntries.find(
												({key}) => type === key
											)?.name || type
									),
								} as any
							}
						/>
					</div>
				</PublisherGateSummary>
			),
		},
		[PublisherGateStep.REQUESTED]: {
			component: <PubliserhRequestedCard />,
		},
	};

	return StepsAccount[step].component;
};

export default PublisherGateSteps;
