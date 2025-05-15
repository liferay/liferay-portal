/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import Form, {ClayInput} from '@clayui/form';
import ClayLayout from '@clayui/layout';
import React from 'react';

import {FieldText} from '../components/forms';
import {CreateSpaceStepOneIllustration} from './CreateSpaceStepOneIllustration';
import {StepOneFormSection} from './StepOneFormSection';

interface NewSpaceProps {}

const NewSpace: React.FC<NewSpaceProps> = () => {
	return (
		<ClayLayout.Row className="p-4">
			<StepOneFormSection
				description={Liferay.Language.get(
					'spaces-are-essential-for-organizing-defining-and-managing-your-content-and-files'
				)}
				linkLabel={Liferay.Language.get('learn-more-about-spaces')}
				linkUrl="/"
				step={1}
				title={Liferay.Language.get('create-a-space')}
			>
				<FieldText
					label={Liferay.Language.get('space-name')}
					name="spaceName"
					placeholder={Liferay.Language.get('enter-a-space-name')}
					required
				/>

				<Form.Group>
					<label htmlFor="spaceDescription">
						{Liferay.Language.get('description')}
					</label>

					<ClayInput
						component="textarea"
						id="spaceDescription"
						placeholder={Liferay.Language.get(
							'enter-a-decription-for-your-space'
						)}
						type="text"
					/>
				</Form.Group>

				<ClayButton.Group className="mb-0 w-100" spaced vertical>
					<ClayButton className="mt-4">
						{Liferay.Language.get('add-members')}
					</ClayButton>

					<ClayButton
						borderless
						className="mt-2"
						displayType="secondary"
						outline
					>
						{Liferay.Language.get('create-a-space-without-members')}
					</ClayButton>
				</ClayButton.Group>
			</StepOneFormSection>

			<CreateSpaceStepOneIllustration />
		</ClayLayout.Row>
	);
};

export default NewSpace;
