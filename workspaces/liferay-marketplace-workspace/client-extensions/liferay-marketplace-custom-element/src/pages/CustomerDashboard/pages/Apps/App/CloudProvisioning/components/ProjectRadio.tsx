/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {UseFormSetValue} from 'react-hook-form';
import {z} from 'zod';

import RadioCard from '../../../../../../../components/RadioCardList/components/RadioCard';
import i18n from '../../../../../../../i18n';
import zodSchema from '../../../../../../../schema/zod';
import {convertSize} from '../../../../../../../utils/filesize';
import {CloudProvisioningOutletContext} from '../pages/CloudProvisioningOutlet';

import './index.scss';

type ProjectRadioType = {
	projects: CloudProvisioningOutletContext['projects'];
	selectedProject: z.infer<typeof zodSchema.installProductSchema>['project'];
	setValue: UseFormSetValue<z.infer<typeof zodSchema.installProductSchema>>;
};

const ProjectRadio: React.FC<ProjectRadioType> = ({
	projects,
	selectedProject,
	setValue,
}) => (
	<>
		{projects
			.sort((projectA, projectB) => {
				const aIsDisabled = projectA.environments.some(
					({isExtensionEnvironment}) => !isExtensionEnvironment
				);
				const bIsDisabled = projectB.environments.some(
					({isExtensionEnvironment}) => !isExtensionEnvironment
				);

				// If one project is disabled and the other isn't, the disabled one goes last

				if (aIsDisabled && !bIsDisabled) {
					return 1;
				}

				if (!aIsDisabled && bIsDisabled) {
					return -1;
				}

				return projectA.rootProjectId.localeCompare(
					projectB.rootProjectId
				);
			})
			.map((project, index) => {
				const hasExtensionEnvironment = project.environments.some(
					({isExtensionEnvironment}) => isExtensionEnvironment
				);

				const disabled =
					!hasExtensionEnvironment || !project?.availabilityToProduct;

				return (
					<RadioCard
						activeRadio={
							selectedProject?.rootProjectId ===
							project.rootProjectId
						}
						disabled={disabled}
						fullTitle
						key={index}
						leftRadio
						selectRadio={() => setValue('project', project)}
						title={
							<div className="d-flex justify-content-between w-100">
								<div className="d-flex flex-column w-100">
									<div className="h5 m-0 project-selection-page-title-text">
										{project.rootProjectId.toUpperCase()}{' '}
									</div>

									<p className="m-0 project-selection-page-description-text">
										{`${project.environments.length} Environments, ${project.rootProjectPlanUsage.cpu.free}CPUs, ${convertSize(
											project.rootProjectPlanUsage.memory
												.free,
											'MB',
											'GB'
										)}GB RAM`}
									</p>

									{!hasExtensionEnvironment && (
										<small className="text-danger">
											This project has no extension
											environments
										</small>
									)}

									{!project?.availabilityToProduct && (
										<small className="text-danger">
											{i18n.translate(
												'the-selected-project-does-not-meet-the-necessary-resource-requirements-for-this-app-please-contact-sales-to-request-additional-resources'
											)}
										</small>
									)}
								</div>
							</div>
						}
					/>
				);
			})}
	</>
);

export default ProjectRadio;
