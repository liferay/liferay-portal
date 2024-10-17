/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayBadge from '@clayui/badge';
import { UseFormSetValue } from 'react-hook-form';
import { useOutletContext } from 'react-router-dom';
import { z } from 'zod';

import RadioCard from '../../../../../../../components/RadioCardList/components/RadioCard';
import i18n from '../../../../../../../i18n';
import zodSchema from '../../../../../../../schema/zod';
import { ConsoleUserProject } from '../../../../../../../services/oauth/types';
import { CloudProvisioningOutletContext } from '../pages/CloudProvisioningOutlet';

type EnvironmentRadioProps = {
	selectedEnvironment?: z.infer<
		typeof zodSchema.installProductSchema
	>['environment'];
	selectedProject: ConsoleUserProject;
	setValue: UseFormSetValue<z.infer<typeof zodSchema.installProductSchema>>;
};

const CUSTOMFIELD_KEY = 'cloud-provisioning';

const EnvironmentRadio: React.FC<EnvironmentRadioProps> = ({
	selectedEnvironment,
	selectedProject,
	setValue,
}) => {
	const { placedOrder } = useOutletContext<CloudProvisioningOutletContext>();
	const deploiments = JSON.parse(
		placedOrder.customFields[CUSTOMFIELD_KEY]
	)[0].deployments;

	const handleSelectRadio = (selectedRadio: RadioOption<any>) => {
		setValue('environment', selectedRadio.value);
	};

	return (
		<>
			{selectedProject?.environments?.map((projectEnvironment, index) => {
				const [projectName = '', environment = ''] =
					projectEnvironment.projectId.split('-');

				const hasDisabled = deploiments.find((deployment: any) => {
					const [_, hasInstallationOnEnvironment = ''] =
						deployment.projectId.split('-');

					return hasInstallationOnEnvironment === environment;
				});

				return (
					<RadioCard
						activeRadio={
							projectEnvironment.projectId ===
							selectedEnvironment?.projectId
						}
						disabled={hasDisabled}
						key={index}
						leftRadio
						selectRadio={() =>
							handleSelectRadio({
								index,
								value: projectEnvironment,
							})
						}
						title={
							<>
								<div>
									<span className="h5 mr-3">
										{projectName.toUpperCase()}
									</span>

									<ClayBadge
										className="text-uppercase"
										label={environment}
									>
										{environment}
									</ClayBadge>
								</div>

								{hasDisabled && (
									<span className="text-red">
										{i18n.translate(
											'this-app-is-already-installed-in-this-environment'
										)}
									</span>
								)}
							</>
						}
					/>
				);
			})}
		</>
	);
};

export default EnvironmentRadio;
