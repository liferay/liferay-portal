import Card from 'shared/components/Card';
import ClayIcon from '@clayui/icon';
import Label from '@clayui/label';
import React from 'react';
import {getAccountInfoDisplayValues} from 'contacts/pages/account/utils/accountInfo';
import {sub} from 'shared/util/lang';
import {Text} from '@clayui/core';

/**
 * Read-only summary of an account's firmographics, rendered as a single
 * horizontal bar at the top of the account Overview tab.
 *
 * Not to be confused with `AccountInfo`, the vertical key/value card on the
 * account Profile tab. A value that is missing renders blank rather than a
 * placeholder.
 */

interface IAccountInfoBarProps {
	accountName?: string;
	accountType?: string;
	annualRevenue?: number;
	country?: string;
	industry?: string;
	lifecycleStage?: string | null;
}

const infoItem = (symbol: string, value: string) => (
	<span className="align-items-center d-flex mr-4">
		<ClayIcon className="mr-2 text-secondary" symbol={symbol} />

		<Text size={3}>{value}</Text>
	</span>
);

const AccountInfoBar: React.FC<IAccountInfoBarProps> = ({
	accountName,
	accountType,
	annualRevenue,
	country,
	industry,
	lifecycleStage,
}) => {
	const {lifecycleStage: lifecycleStageValue, revenue} =
		getAccountInfoDisplayValues({annualRevenue, lifecycleStage});

	return (
		<Card className="mb-4">
			<Card.Body className="align-items-center d-flex flex-row flex-wrap justify-content-between p-3">
				<Text size={5} weight="semi-bold">
					{accountName}
				</Text>

				<div className="align-items-center d-flex flex-wrap">
					{country && infoItem('globe', country)}

					{revenue &&
						infoItem(
							'dollar-symbol',
							sub(Liferay.Language.get('x-revenue'), [
								revenue,
							]) as string
						)}

					{industry && infoItem('briefcase', industry)}

					{(lifecycleStageValue || accountType) && (
						<span className="align-self-stretch border-left mr-4" />
					)}

					{lifecycleStageValue && (
						<Label
							className="mr-2"
							displayType={lifecycleStageValue.displayType}
							inverse
						>
							{sub(Liferay.Language.get('lifecycle-x'), [
								lifecycleStageValue.label,
							])}
						</Label>
					)}

					{accountType && (
						<Label displayType="info" inverse>
							{sub(Liferay.Language.get('type-x'), [accountType])}
						</Label>
					)}
				</div>
			</Card.Body>
		</Card>
	);
};

export default AccountInfoBar;
