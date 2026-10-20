import ClayIcon from 'shared/components/Icon';
import ClayList from '@clayui/list';
import ClaySticker from '@clayui/sticker';
import React from 'react';
import {ClayCheckbox} from '@clayui/form';
import {sub} from 'shared/util/lang';
import {toLocale} from 'shared/util/numbers';

interface ISalesforceEntitiesProps {
	accountsSyncedCount?: number;
	campaignsSyncedCount?: number;
	disabled?: boolean;
	enabledAccounts: boolean;
	enabledIndividuals: boolean;
	individualsSyncedCount?: number;
	loading?: boolean;
	onAccountsChange: () => void;
	onIndividualsChange: () => void;
	type?: string;
}

const SalesforceEntities: React.FC<ISalesforceEntitiesProps> = ({
	accountsSyncedCount,
	campaignsSyncedCount,
	disabled = false,
	enabledAccounts,
	enabledIndividuals,
	individualsSyncedCount,
	onAccountsChange,
	onIndividualsChange,
}) => (
	<div className="pt-1">
		<ClayList className="mb-0">
			<ClayList.Item flex>
				<ClayList.ItemField>
					<ClayCheckbox
						checked={enabledAccounts}
						disabled={disabled}
						onChange={onAccountsChange}
					/>
				</ClayList.ItemField>

				<ClayList.ItemField>
					<ClaySticker displayType="unstyled">
						<ClayIcon
							className="text-secondary"
							symbol="briefcase"
						/>
					</ClaySticker>
				</ClayList.ItemField>

				<ClayList.ItemField expand>
					<ClayList.ItemTitle>
						{Liferay.Language.get('accounts')}
					</ClayList.ItemTitle>

					<ClayList.ItemText>
						{Liferay.Language.get(
							'represents-fields-from-the-account-object-within-salesforce'
						)}
					</ClayList.ItemText>

					{accountsSyncedCount !== undefined &&
						accountsSyncedCount >= 0 && (
							<ClayList.ItemText>
								{sub(Liferay.Language.get('x-items-synced'), [
									toLocale(accountsSyncedCount),
								])}
							</ClayList.ItemText>
						)}
				</ClayList.ItemField>
			</ClayList.Item>

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
							'represents-fields-from-the-contact-or-lead-object-within-salesforce'
						)}
					</ClayList.ItemText>

					{individualsSyncedCount !== undefined &&
						individualsSyncedCount >= 0 && (
							<ClayList.ItemText>
								{sub(Liferay.Language.get('x-items-synced'), [
									toLocale(individualsSyncedCount),
								])}
							</ClayList.ItemText>
						)}
				</ClayList.ItemField>
			</ClayList.Item>

			<ClayList.Item flex>
				<ClayList.ItemField>
					<ClayCheckbox checked disabled readOnly />
				</ClayList.ItemField>

				<ClayList.ItemField>
					<ClaySticker displayType="unstyled">
						<ClayIcon
							className="text-secondary"
							symbol="megaphone"
						/>
					</ClaySticker>
				</ClayList.ItemField>

				<ClayList.ItemField expand>
					<ClayList.ItemTitle>
						{Liferay.Language.get('campaigns')}
					</ClayList.ItemTitle>

					<ClayList.ItemText>
						{Liferay.Language.get(
							'represents-fields-from-the-campaign-object-within-salesforce'
						)}
					</ClayList.ItemText>

					{campaignsSyncedCount !== undefined &&
						campaignsSyncedCount >= 0 && (
							<ClayList.ItemText>
								{sub(Liferay.Language.get('x-items-synced'), [
									toLocale(campaignsSyncedCount),
								])}
							</ClayList.ItemText>
						)}
				</ClayList.ItemField>
			</ClayList.Item>
		</ClayList>
	</div>
);

export default SalesforceEntities;
