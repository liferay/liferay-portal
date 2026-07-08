import ClayIcon from '@clayui/icon';
import ClayList from '@clayui/list';
import ClaySticker from '@clayui/sticker';
import React from 'react';
import {ClayCheckbox} from '@clayui/form';
import {sub} from 'shared/util/lang';

/**
 * TODO [Marketo]: This component is derived from
 * SalesforceAccountsAndIndividuals. Replace the Salesforce-worded language key
 * ('represents-fields-from-the-contact-or-lead-object-within-salesforce') once
 * the Marketo Campaign copy is defined.
 */

interface IMarketoCampaignEntitiesProps {
	disabled?: boolean;
	enabledIndividual: boolean;
	individualsSyncedCount?: number;
	loading?: boolean;
	onIndividualChange: () => void;
	type?: string;
}

const MarketoCampaignEntities: React.FC<IMarketoCampaignEntitiesProps> = ({
	disabled = false,
	enabledIndividual,
	individualsSyncedCount,
	onIndividualChange,
}) => (
	<div className="pt-1">
		<ClayList className="mb-0">
			<ClayList.Item flex>
				<ClayList.ItemField>
					<ClayCheckbox
						checked={enabledIndividual}
						disabled={disabled}
						onChange={onIndividualChange}
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

					{/* TODO [Marketo]: Salesforce-worded language key. */}
					<ClayList.ItemText>
						{Liferay.Language.get(
							'represents-fields-from-the-contact-or-lead-object-within-salesforce'
						)}
					</ClayList.ItemText>

					{individualsSyncedCount !== undefined &&
						individualsSyncedCount >= 0 && (
							<ClayList.ItemText>
								{sub(Liferay.Language.get('x-items-synced'), [
									individualsSyncedCount,
								])}
							</ClayList.ItemText>
						)}
				</ClayList.ItemField>
			</ClayList.Item>
		</ClayList>
	</div>
);

export default MarketoCampaignEntities;
