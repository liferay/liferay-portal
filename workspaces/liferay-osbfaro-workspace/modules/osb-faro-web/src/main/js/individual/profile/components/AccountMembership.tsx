import Card from 'shared/components/Card';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {
	DataDrivenConfig,
	GeneralInfoSection,
} from 'shared/components/GeneralInfoSection';
import {formatUTCDate, getCustomDateFormat} from 'shared/util/date';
import {Map} from 'immutable';
import {Routes, toRoute} from 'shared/util/router';
import {SectionHeader} from 'shared/components/SectionHeader';
import {toCurrency} from 'shared/util/numbers';
import {useLocale} from 'shared/hooks/useLocale';

const accountMembershipConfig: DataDrivenConfig = [
	{
		columnClass: 'col-12 col-lg-6 mb-3 mb-lg-0',
		items: [
			{
				className: 'col-12 col-md-6',
				icon: 'order-form',
				key: 'accountName',
			},
			{className: 'col-12 col-md-6', key: 'id'},
			{className: 'col-12 col-md-6', key: 'industry'},
			{className: 'col-12 col-md-6', key: 'currencyCode'},
			{className: 'col-12 col-md-6', key: 'accountType'},
			{className: 'col-12 col-md-6', key: 'annualRevenue'},
			{className: 'col-12 col-md-6', key: 'numberOfEmployees'},
		],
		title: Liferay.Language.get('general-account-information'),
	},
	{
		columnClass: 'col-12 col-md-6 col-lg-3 mb-3 mb-md-0',
		items: [
			{className: 'col-12', icon: 'heart', key: 'customerSince'},
			{className: 'col-12', key: 'lastActivityDate'},
			{className: 'col-12', key: 'createdDate'},
		],
		title: Liferay.Language.get('account-relationship-details'),
	},
	{
		columnClass: 'col-12 col-md-6 col-lg-3 mb-3 mb-md-0',
		items: [
			{className: 'col-12', icon: 'globe-pin', key: 'country'},
			{className: 'col-12', key: 'state'},
		],
		title: Liferay.Language.get('account-location'),
	},
];

const dateKeys = ['createdDate', 'lastActivityDate'];

interface IAccountMembershipProps {
	accountData?: Map<string, any>;
	channelId?: string;
	children?: React.ReactNode;
	groupId?: string;
	loading?: boolean;
	showEmptyState?: boolean;
}

const ACCOUNT_MEMBERSHIP_LABEL_MAP: Record<string, string> = {
	accountName: Liferay.Language.get('account-name'),
	accountType: Liferay.Language.get('account-type'),
	annualRevenue: Liferay.Language.get('annual-revenue'),
	country: Liferay.Language.get('country'),
	createdDate: Liferay.Language.get('created-date'),
	currencyCode: Liferay.Language.get('currency-code'),
	customerSince: Liferay.Language.get('customer-since'),
	id: 'accountId',
	industry: Liferay.Language.get('industry'),
	lastActivityDate: Liferay.Language.get('last-activity-date'),
	numberOfEmployees: Liferay.Language.get('number-of-employees'),
	state: Liferay.Language.get('state'),
};

const AccountMembership: React.FC<IAccountMembershipProps> = ({
	accountData,
	channelId,
	children: emptyState,
	groupId,
	loading = false,
	showEmptyState = false,
}) => {
	const locale = useLocale();

	const getValue = (key: string): string | undefined => {
		const data = accountData?.get(key);

		if (!data) {
			return undefined;
		}

		if (dateKeys.includes(key)) {
			return formatUTCDate(data, getCustomDateFormat());
		}

		if (key === 'annualRevenue') {
			return toCurrency(
				parseFloat(data),
				accountData?.get('currencyCode'),
				locale
			);
		}

		return data;
	};

	const getHref = (key: string): string | undefined => {
		const accountId = accountData?.get('id');

		if (key === 'accountName' && accountId && channelId && groupId) {
			return toRoute(Routes.CONTACTS_ACCOUNT, {
				channelId,
				groupId,
				id: accountId,
			});
		}

		return undefined;
	};

	const sectionContent = accountData ? (
		<GeneralInfoSection
			config={accountMembershipConfig}
			getHref={getHref}
			getValue={getValue}
			languageMap={ACCOUNT_MEMBERSHIP_LABEL_MAP}
			loading={loading}
		/>
	) : (
		<Card className="p-5">
			<Card.Body className="p-5">
				<NoResultsDisplay
					description={
						<>
							<p className="mb-2">
								{Liferay.Language.get(
									'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
								)}
							</p>
							<ClayLink
								className="d-block mb-3"
								decoration="underline"
								href={URLConstants.DataSourceConnection}
								target="_blank"
							>
								{Liferay.Language.get(
									'learn-more-about-data-sources'
								)}
								<span className="inline-item inline-item-after">
									<ClayIcon fontSize={8} symbol="shortcut" />
								</span>
							</ClayLink>
						</>
					}
					primary
					title={Liferay.Language.get('no-data-was-found')}
				/>
			</Card.Body>
		</Card>
	);

	return (
		<>
			<SectionHeader
				icon="briefcase"
				title={Liferay.Language.get('account-membership')}
			/>

			{showEmptyState && !loading ? emptyState : sectionContent}
		</>
	);
};

export default AccountMembership;
