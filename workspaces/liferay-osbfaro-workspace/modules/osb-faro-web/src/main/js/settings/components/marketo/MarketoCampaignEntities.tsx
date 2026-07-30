import ClayIcon from '@clayui/icon';
import ClayList from '@clayui/list';
import ClaySticker from '@clayui/sticker';
import Label from '@clayui/label';
import React from 'react';
import {ClayCheckbox} from '@clayui/form';
import {sub} from 'shared/util/lang';
import {toLocale} from 'shared/util/numbers';

interface IMarketoCampaignEntitiesProps {
	disabled?: boolean;
	enabledIndividuals: boolean;
	individualsSyncedCount?: number;
	loading?: boolean;
	onIndividualsChange: () => void;
}

const MarketoCampaignEntities: React.FC<IMarketoCampaignEntitiesProps> = ({
	disabled = false,
	enabledIndividuals,
	individualsSyncedCount,
	onIndividualsChange,
}) => {
	const configured =
		typeof individualsSyncedCount === 'number' &&
		individualsSyncedCount > 0;

	return (
		<div className="pt-1">
			<ClayList className="mb-0">
				<ClayList.Item flex>
					<ClayList.ItemField>
						<ClayCheckbox
							checked={enabledIndividuals}
							disabled={disabled}
							onChange={onIndividualsChange}
						/>
					</ClayList.ItemField>

					<ClayList.ItemField>
						<ClaySticker displayType="unstyled">
							<ClayIcon
								className="text-secondary"
								symbol="users"
							/>
						</ClaySticker>
					</ClayList.ItemField>

					<ClayList.ItemField
						className="d-flex justify-content-center"
						expand
					>
						<ClayList.ItemTitle>
							{Liferay.Language.get('individuals')}
						</ClayList.ItemTitle>

						<ClayList.ItemText>
							{Liferay.Language.get(
								'represents-fields-from-the-leads-and-companies-table-within-marketo'
							)}
						</ClayList.ItemText>

						{individualsSyncedCount !== undefined &&
							individualsSyncedCount >= 0 && (
								<ClayList.ItemText>
									{sub(
										Liferay.Language.get('x-items-synced'),
										[toLocale(individualsSyncedCount)]
									)}
								</ClayList.ItemText>
							)}
					</ClayList.ItemField>

					<ClayList.ItemField className="justify-content-center">
						<Label
							displayType={configured ? 'success' : 'secondary'}
						>
							{configured
								? Liferay.Language.get(
										'configured'
									).toUpperCase()
								: Liferay.Language.get(
										'unconfigured'
									).toUpperCase()}
						</Label>
					</ClayList.ItemField>
				</ClayList.Item>
			</ClayList>
		</div>
	);
};

export default MarketoCampaignEntities;
