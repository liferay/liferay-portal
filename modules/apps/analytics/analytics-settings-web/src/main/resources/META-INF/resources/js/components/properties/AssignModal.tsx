/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {updateProperty} from '../../utils/api';
import Loading from '../Loading';
import {TProperty} from './Properties';
import SitesTab from './SitesTab';

interface IAssignModalProps {
	observer: any;
	onCancel: () => void;
	onSubmit: ({siteIds}: {siteIds: number[]}) => void;
	property: TProperty;
}

const AssignModal: React.FC<
	{children?: React.ReactNode | undefined} & IAssignModalProps
> = ({observer, onCancel, onSubmit, property}) => {
	const {
		name,
		dataSources: [{siteIds: initialSiteIds}],
	} = property;

	const [submitting, setSubmitting] = useState(false);
	const [siteIds, setSiteIds] = useState<number[]>(initialSiteIds);

	return (
		<ClayModal center observer={observer} size="lg">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{sub(Liferay.Language.get('assign-to-x'), name)}
			</ClayModal.Header>

			<ClayModal.Body>
				<SitesTab
					initialIds={siteIds}
					onSitesChange={setSiteIds}
					property={property}
				/>
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
								const {channelId, dataSources} = property;

								const {ok} = await updateProperty({
									channelId,
									dataSourceId: dataSources[0]?.dataSourceId,
									siteIds,
								});

								setSubmitting(false);

								ok && onSubmit({siteIds});
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
	onSubmit: ({siteIds}: {siteIds: number[]}) => void;
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
