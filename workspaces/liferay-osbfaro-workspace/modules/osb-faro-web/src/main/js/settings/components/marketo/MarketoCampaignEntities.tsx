import ClayIcon from '@clayui/icon';
import ClayList from '@clayui/list';
import ClaySticker from '@clayui/sticker';
import React from 'react';
import {ClayCheckbox} from '@clayui/form';

interface IMarketoCampaignEntitiesProps {
	disabled?: boolean;
	enabledIndividuals: boolean;
	loading?: boolean;
	onIndividualsChange: () => void;
}

const MarketoCampaignEntities: React.FC<IMarketoCampaignEntitiesProps> = ({
	disabled = false,
	enabledIndividuals,
	onIndividualsChange,
}) => (
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
						<ClayIcon className="text-secondary" symbol="users" />
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
				</ClayList.ItemField>
			</ClayList.Item>
		</ClayList>
	</div>
);

export default MarketoCampaignEntities;
