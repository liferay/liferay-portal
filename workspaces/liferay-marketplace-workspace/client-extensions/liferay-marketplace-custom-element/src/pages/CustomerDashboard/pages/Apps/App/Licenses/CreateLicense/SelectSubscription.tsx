/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import classNames from 'classnames';
import {useParams} from 'react-router-dom';
import useSWR from 'swr';

import RadioCardList from '../../../../../../../components/RadioCardList/RadioCardList';
import i18n from '../../../../../../../i18n';
import koroneikiOAuth2 from '../../../../../../../services/oauth/Koroneiki';
import {formatDate} from '../../../../../../../utils/date';

type SubscriptionSelectionProps = {
	onSelectSubscription: (subscription: any) => void;
	selectedSubscriptionValue?: any;
};

const SelectSubscription = ({
	onSelectSubscription,
	selectedSubscriptionValue,
}: SubscriptionSelectionProps) => {
	const params = useParams();
	const orderId = Number(params.orderId);

	const {
		data: subscriptions = [],
		isLoading,
		isValidating,
	} = useSWR(`/subcriptions/${orderId}`, () =>
		koroneikiOAuth2.getSubscriptions(orderId)
	);

	return (
		<div className="mb-4 mt-3">
			<p>Generate licenses with a selected subscription term.</p>

			{(isLoading || isValidating) && <ClayLoadingIndicator />}

			<RadioCardList
				contentList={subscriptions.map((licenseKey, index) => {
					const availableKeys = Math.abs(
						licenseKey.provisionedCount - licenseKey.purchasedCount
					);

					return {
						description: (
							<small
								className={classNames({
									'text-danger': availableKeys <= 0,
									'text-success': availableKeys > 0,
								})}
							>
								{i18n.sub('key-activations-available-x-of-x', [
									availableKeys.toString(),
									licenseKey.purchasedCount.toString(),
								])}
							</small>
						),
						disabled: availableKeys <= 0,
						id: index,
						label: `${formatDate(licenseKey.startDate)} - ${
							licenseKey?.endDate
								? formatDate(
										new Date(
											licenseKey.endDate
										).toISOString()
									)
								: 'DNE'
						}`,
						selected:
							selectedSubscriptionValue?.name === licenseKey.name,
						title: (
							<h3 className="mt-0 text-capitalize">
								{licenseKey.name}
							</h3>
						),
						value: licenseKey,
					};
				})}
				leftRadio
				onSelect={({value}) => onSelectSubscription(value)}
			/>
		</div>
	);
};

export default SelectSubscription;
