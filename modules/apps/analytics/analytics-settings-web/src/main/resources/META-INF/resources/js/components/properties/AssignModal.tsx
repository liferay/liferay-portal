/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import ClayTabs from '@clayui/tabs';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {updateProperty} from '../../utils/api';
import Loading from '../Loading';
import ChannelTab from './ChannelTab';
import {TProperty} from './Properties';
import SitesTab from './SitesTab';

interface IAssignModalProps {
	observer: any;
	onCancel: () => void;
	onSubmit: ({
		commerceChannelIds,
		siteIds,
	}: {
		commerceChannelIds: number[];
		siteIds: number[];
	}) => void;
	property: TProperty;
}

export enum ETabs {
	Channel = 0,
	Sites = 1,
}

const AssignModal: React.FC<
	{children?: React.ReactNode | undefined} & IAssignModalProps
> = ({observer, onCancel, onSubmit, property}) => {
	const {
		name,
		dataSources: [
			{
				commerceChannelIds: initialCommerceChannelIds,
				siteIds: initialSiteIds,
			},
		],
	} = property;

	const [activeTabKeyValue, setActiveTabKeyValue] = useState<ETabs>(
		ETabs.Channel
	);
	const [submitting, setSubmitting] = useState(false);
	const [commerceChannelIds, setCommerceChannelIds] = useState<number[]>(
		initialCommerceChannelIds
	);
	const [siteIds, setSiteIds] = useState<number[]>(initialSiteIds);

	return (
		<ClayModal center observer={observer} size="lg">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{sub(Liferay.Language.get('assign-to-x'), name)}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayTabs displayType="underline" modern>
					<ClayTabs.Item
						active={activeTabKeyValue === ETabs.Channel}
						innerProps={{
							'aria-controls': 'tabpanel-1',
						}}
						onClick={() => setActiveTabKeyValue(ETabs.Channel)}
					>
						{Liferay.Language.get('channel')}
					</ClayTabs.Item>

					<ClayTabs.Item
						active={activeTabKeyValue === ETabs.Sites}
						innerProps={{
							'aria-controls': 'tabpanel-2',
						}}
						onClick={() => setActiveTabKeyValue(ETabs.Sites)}
					>
						{Liferay.Language.get('sites')}
					</ClayTabs.Item>
				</ClayTabs>

				<ClayTabs.Content activeIndex={activeTabKeyValue} fade>
					<ClayTabs.TabPane
						aria-labelledby="tab-1"
						data-testid={ETabs.Channel}
					>
						<ChannelTab
							initialIds={commerceChannelIds}
							onChannelsChange={setCommerceChannelIds}
							property={property}
						/>
					</ClayTabs.TabPane>

					<ClayTabs.TabPane
						aria-labelledby="tab-2"
						data-testid={ETabs.Sites}
					>
						<SitesTab
							initialIds={siteIds}
							onSitesChange={setSiteIds}
							property={property}
						/>
					</ClayTabs.TabPane>
				</ClayTabs.Content>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={() => onCancel()}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={submitting}
							displayType="primary"
							onClick={async () => {
								const {
									channelId,
									commerceSyncEnabled,
									dataSources,
								} = property;

								const {ok} = await updateProperty({
									channelId,
									commerceChannelIds,
									commerceSyncEnabled,
									dataSourceId: dataSources[0]?.dataSourceId,
									siteIds,
								});

								setSubmitting(false);

								ok && onSubmit({commerceChannelIds, siteIds});
							}}
						>
							{submitting && <Loading inline />}

							{Liferay.Language.get('assign')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

interface IAssignModalWrapperProps {
	observer: any;
	onCancel: () => void;
	onSubmit: ({
		commerceChannelIds,
		siteIds,
	}: {
		commerceChannelIds: number[];
		siteIds: number[];
	}) => void;
	property: TProperty | null;
}

const AssignModalWrapper: React.FC<
	{children?: React.ReactNode | undefined} & IAssignModalWrapperProps
> = ({property, ...otherProps}) => {
	if (!property) {
		return null;
	}

	return <AssignModal {...otherProps} property={property} />;
};

export default AssignModalWrapper;
