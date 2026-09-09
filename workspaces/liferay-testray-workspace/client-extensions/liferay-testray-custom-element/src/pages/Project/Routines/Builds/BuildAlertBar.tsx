/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert, {DisplayType as AlertDisplayType} from '@clayui/alert';
import ClayButton, {ButtonProps} from '@clayui/button';
import ClayLabel from '@clayui/label';
import {useNavigate} from 'react-router-dom';
import {useObjectPermission} from '~/hooks/data/useObjectPermission';

import i18n from '../../../../i18n';
import {TestrayBuild, TestrayTask} from '../../../../services/rest';
import {
	testrayBuildAlertProperties,
	testrayTaskAlertProperties,
} from '../../../../util/constants';
import {BuildImportStatuses} from '../../../../util/statuses';

type BuildAlertBarProps = {
	testrayBuild: TestrayBuild;
	testrayTask: TestrayTask;
};

const BuildAlertBar: React.FC<BuildAlertBarProps> = ({
	testrayBuild,
	testrayTask,
}) => {
	const navigate = useNavigate();

	const taskPermission = useObjectPermission('/tasks');

	if (testrayBuild.importStatus.key !== BuildImportStatuses.DONE) {
		const testrayBuildAlertProperty =
			testrayBuildAlertProperties[testrayBuild.importStatus.key];

		return (
			<ClayAlert
				className="build-alert-bar w-100"
				displayType={
					testrayBuildAlertProperty.displayType as AlertDisplayType
				}
				title={
					(
						<>
							<ClayLabel
								displayType={
									testrayBuildAlertProperty.displayType as AlertDisplayType
								}
							>
								{testrayBuildAlertProperty.label}
							</ClayLabel>

							{testrayBuildAlertProperty.text}
						</>
					) as unknown as string
				}
				variant="inline"
			/>
		);
	}

	if (!testrayTask && taskPermission.canCreate) {
		return (
			<ClayButton
				className="mb-4"
				onClick={() => navigate('testflow/create')}
			>
				{i18n.translate('analyze')}
			</ClayButton>
		);
	}

	const alertProperty =
		testrayTaskAlertProperties[testrayTask?.dueStatus?.key];

	if (!alertProperty) {
		return null;
	}

	return (
		<ClayAlert
			actions={
				<ClayButton
					displayType={
						alertProperty.displayType as ButtonProps['displayType']
					}
					onClick={() => navigate(`/testflow/${testrayTask.id}`)}
					outline
					small
				>
					{i18n.translate('view-task')}
				</ClayButton>
			}
			className="build-alert-bar w-100"
			displayType={alertProperty.displayType as AlertDisplayType}
			title={
				(
					<>
						<ClayLabel
							displayType={
								alertProperty.displayType as AlertDisplayType
							}
						>
							{alertProperty.label}
						</ClayLabel>

						{alertProperty.text}
					</>
				) as unknown as string
			}
			variant="inline"
		/>
	);
};

export default BuildAlertBar;
