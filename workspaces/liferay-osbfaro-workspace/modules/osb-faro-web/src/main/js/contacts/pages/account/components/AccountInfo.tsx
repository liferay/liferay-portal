import AccountDetailsModal from './AccountDetailsModal';
import Button from '@clayui/button';
import Card from 'shared/components/Card';
import classNames from 'classnames';
import ClayEmptyState from '@clayui/empty-state';
import ClayLink from '@clayui/link';
import React, {useState} from 'react';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import {Text} from '@clayui/core';
import {toThousands} from 'shared/util/numbers';
import {useParams} from 'react-router-dom';

export interface IAccount {
	accountName?: string;
	accountType?: string;
	annualRevenue?: number;
	country?: string;
	fields?: Array<{
		dataSourceId?: string;
		dataSourceName?: string;
		name: string;
		value?: string;
	}>;
	id?: string;
	industry?: string;
	lifecycleStage?: string | null;
	numberOfEmployees?: number;
	website?: string;
}

interface IAccountInfoProps {
	account?: IAccount;
	className?: string;
	loading?: boolean;
}

const infoDataLabels = {
	accountType: Liferay.Language.get('account-type'),
	annualRevenue: Liferay.Language.get('annual-revenue'),
	industry: Liferay.Language.get('industry'),
	numberOfEmployees: Liferay.Language.get('company-size'),
	website: Liferay.Language.get('website'),
};

const normalizeHref = (value: string) =>
	/^https?:\/\//i.test(value) ? value : `https://${value}`;

const infoItem = (label: string, value?: string, link?: boolean) => (
	<div className="d-flex justify-content-between mb-2">
		<Text color="secondary" size={3}>
			{label}
		</Text>
		{link && value ? (
			<ClayLink href={normalizeHref(value)} target="_blank">
				<Text size={3}>{value}</Text>
			</ClayLink>
		) : (
			<Text color="secondary" size={3}>
				{value}
			</Text>
		)}
	</div>
);

const AccountInfo: React.FC<IAccountInfoProps> = ({
	account,
	className,
	loading,
}) => {
	const [isDetailsModalOpen, setIsDetailsModalOpen] = useState(false);
	const {id: accountId} = useParams<{id: string}>();

	const getValue = (key: keyof typeof infoDataLabels): string | undefined => {
		if (!account) {
			return undefined;
		}
		if (key === 'annualRevenue') {
			return account.annualRevenue !== undefined
				? toThousands(account.annualRevenue)
				: undefined;
		}
		if (key === 'numberOfEmployees') {
			return account.numberOfEmployees !== undefined
				? toThousands(account.numberOfEmployees)
				: undefined;
		}
		return account[key];
	};

	const infoKeys = Object.keys(infoDataLabels) as Array<
		keyof typeof infoDataLabels
	>;
	const isEmpty = !loading && infoKeys.every((key) => !getValue(key));

	return (
		<>
			<Card className={classNames(className, 'p-3')} minHeight={260}>
				<Card.Title>
					<Text size={4} weight="semi-bold">
						<span className="text-uppercase">
							{Liferay.Language.get(
								'general-account-information'
							)}
						</span>
					</Text>
				</Card.Title>
				<Card.Body
					alignCenter={loading || isEmpty}
					className={classNames('mt-4', {
						'justify-content-end p-0': !loading && !isEmpty,
					})}
				>
					<StatesRenderer empty={isEmpty} loading={loading}>
						<StatesRenderer.Loading />
						<StatesRenderer.Empty>
							<ClayEmptyState
								className="mt-n5 text-center"
								description={Liferay.Language.get(
									'account-attributes-will-appear-here-when-available'
								)}
								small
								title={Liferay.Language.get(
									'no-account-attributes-available'
								)}
							/>
						</StatesRenderer.Empty>
						<StatesRenderer.Success>
							{infoKeys.map((key) => (
								<React.Fragment key={key}>
									{infoItem(
										infoDataLabels[key],
										getValue(key),
										key === 'website'
									)}
								</React.Fragment>
							))}
							<Button
								borderless
								className="ml-auto rounded-lg"
								onClick={() => setIsDetailsModalOpen(true)}
								size="sm"
							>
								{Liferay.Language.get('view-all')}
							</Button>
						</StatesRenderer.Success>
					</StatesRenderer>
				</Card.Body>
			</Card>

			{isDetailsModalOpen && accountId && (
				<AccountDetailsModal
					accountId={accountId}
					accountName={account?.accountName}
					onClose={() => setIsDetailsModalOpen(false)}
				/>
			)}
		</>
	);
};

export default AccountInfo;
