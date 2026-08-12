import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayList from '@clayui/list';
import ClaySticker from '@clayui/sticker';
import React from 'react';
import {sub} from 'shared/util/lang';
import {Text} from '@clayui/core';
import {toLocale} from 'shared/util/numbers';

const DATA_SOURCE_STATUSES = {
	CONFIGURED: {
		display: 'success',
		label: Liferay.Language.get('connected'),
	},
	UNCONFIGURED: {
		display: 'secondary',
		label: Liferay.Language.get('disconnected'),
	},
};

interface IReviewSyncedDataFragmentProps {
	channelsMetric?: {
		channelsCount: 0;
		groupsCount: 0;
		individualsCount: 0;
	};
	contactsSelected: boolean;
	sitesSelected: boolean;
}

const ReviewSyncedDataFragment: React.FC<IReviewSyncedDataFragmentProps> = ({
	channelsMetric,
	contactsSelected,
	sitesSelected,
}) => {
	const getLabelProps = (selected: boolean) =>
		selected
			? DATA_SOURCE_STATUSES.CONFIGURED
			: DATA_SOURCE_STATUSES.UNCONFIGURED;

	const {display: contactsDisplay, label: contactslabel} =
		getLabelProps(contactsSelected);

	const {display: sitesDisplay, label: sitesLabel} =
		getLabelProps(sitesSelected);

	const renderMetricValue = (message: string, count: number | undefined) => {
		if (!channelsMetric) {
			return null;
		}

		return (
			<ClayList.ItemText>
				{sub(message, [toLocale(count ?? 0)])}
			</ClayList.ItemText>
		);
	};

	return (
		<>
			<div className="mb-2">
				<Text size={2} weight="semi-bold">
					{Liferay.Language.get('connection-status').toUpperCase()}
				</Text>
			</div>

			<ClayList>
				<ClayList.Item flex>
					<ClayList.ItemField>
						<ClaySticker displayType="unstyled">
							<ClayIcon
								className="text-secondary"
								symbol="nodes"
							/>
						</ClaySticker>
					</ClayList.ItemField>

					<ClayList.ItemField expand>
						<ClayList.ItemTitle>
							{Liferay.Language.get('properties')}
						</ClayList.ItemTitle>
						<ClayList.ItemText>
							{Liferay.Language.get(
								'used-to-aggregate-data-on-your-users-and-sites'
							)}
						</ClayList.ItemText>

						{renderMetricValue(
							Liferay.Language.get(
								'x-properties-are-connected-to-this-data-source'
							),
							channelsMetric?.channelsCount
						)}
					</ClayList.ItemField>

					<ClayList.ItemField className="justify-content-center">
						<ClayLabel displayType={sitesDisplay as any}>
							{sitesLabel}
						</ClayLabel>
					</ClayList.ItemField>
				</ClayList.Item>

				<ClayList.Item flex>
					<ClayList.ItemField>
						<ClaySticker displayType="unstyled">
							<ClayIcon
								className="text-secondary"
								symbol="picture"
							/>
						</ClaySticker>
					</ClayList.ItemField>

					<ClayList.ItemField expand>
						<ClayList.ItemTitle>
							{Liferay.Language.get('sites')}
						</ClayList.ItemTitle>
						<ClayList.ItemText>
							{Liferay.Language.get(
								'represents-the-sites-synced-from-liferay-portal,-under-dxp-instance-settings-analytics-cloud'
							)}
						</ClayList.ItemText>

						{renderMetricValue(
							Liferay.Language.get('x-items-synced'),
							channelsMetric?.groupsCount
						)}
					</ClayList.ItemField>

					<ClayList.ItemField className="justify-content-center">
						<ClayLabel displayType={sitesDisplay as any}>
							{sitesLabel}
						</ClayLabel>
					</ClayList.ItemField>
				</ClayList.Item>

				<ClayList.Item flex>
					<ClayList.ItemField>
						<ClaySticker displayType="unstyled">
							<ClayIcon
								className="text-secondary"
								symbol="users"
							/>
						</ClaySticker>
					</ClayList.ItemField>

					<ClayList.ItemField expand>
						<ClayList.ItemTitle>
							{Liferay.Language.get('individuals')}
						</ClayList.ItemTitle>
						<ClayList.ItemText>
							{Liferay.Language.get(
								'represents-the-fields-synced-from-the-contact-object-within-liferay-portal,-under-dxp-instance-settings-analytics-cloud'
							)}
						</ClayList.ItemText>

						{renderMetricValue(
							Liferay.Language.get('x-items-synced'),
							channelsMetric?.individualsCount
						)}
					</ClayList.ItemField>

					<ClayList.ItemField className="justify-content-center">
						<ClayLabel displayType={contactsDisplay as any}>
							{contactslabel}
						</ClayLabel>
					</ClayList.ItemField>
				</ClayList.Item>
			</ClayList>
		</>
	);
};

export {ReviewSyncedDataFragment};
