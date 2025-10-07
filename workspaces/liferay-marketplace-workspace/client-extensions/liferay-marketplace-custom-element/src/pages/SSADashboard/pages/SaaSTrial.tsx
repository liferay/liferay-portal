/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {useModal} from '@clayui/modal';

import Page from '../../../components/Page';
import i18n from '../../../i18n';
import TrialListView from '../components/TrialListView/TrialListView';
import useSSAActions from '../useSSAActions';

export default function SaaSTrials() {
	const actions = useSSAActions();
	const createTrialFormModal = useModal();

	return (
		<Page
			description={i18n.translate('manage-your-teams-trial')}
			pageRendererProps={{className: 'border py-2'}}
			rightButton={
				<ClayButton
					onClick={() => createTrialFormModal.onOpenChange(true)}
				>
					{i18n.translate('add-new-trial')}
				</ClayButton>
			}
			title="SaaS Demos"
		>
			<TrialListView
				actions={actions}
				createTrialFormModal={createTrialFormModal}
				isSortable
				managementToolbarProps={{
					searchVisible: true,
					visible: true,
				}}
			/>
		</Page>
	);
}
