/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLayout from '@clayui/layout';
import {
	ILearnResourceContext,
	ManageMembersList,
} from 'frontend-js-components-web';
import {navigate, sub} from 'frontend-js-web';
import React, {useState} from 'react';

import AddMembersInput from '../../common/components/AddMembersInput';
import {getImage} from '../../common/utils/getImage';
import {NewSpaceFormSection} from './NewSpaceFormSection';
import {SPACE_MEMBERS_CONFIG} from './spaceMembersConfig';

export interface AddSpaceMembersProps {
	assetLibraryCreatorUserId: string;
	assetLibraryId: string;
	assetLibraryName: string;
	baseAssetLibraryURL: string;
	externalReferenceCode: string;
	hasAssignMembersPermission: boolean;
	learnResources: ILearnResourceContext;
}

export function AddSpaceMembers({
	assetLibraryCreatorUserId,
	assetLibraryId,
	assetLibraryName,
	baseAssetLibraryURL,
	externalReferenceCode,
	hasAssignMembersPermission,
	learnResources,
}: AddSpaceMembersProps) {
	const [hasSelectedMembers, setHasSelectedMembers] = useState(false);

	const onContinueBtnClick = () => {
		navigate(baseAssetLibraryURL + assetLibraryId);
	};

	return (
		<ClayLayout.Row className="add-space-members m-2 m-md-4">
			<ClayLayout.Col className="px-md-4 px-xl-9" lg={6}>
				<NewSpaceFormSection
					description={Liferay.Language.get(
						'add-team-members-to-this-space-to-start-collaborating'
					)}
					learnResourceKey="space-memberships"
					learnResources={learnResources}
					step={2}
					title={sub(
						Liferay.Language.get('add-members-to-x'),
						assetLibraryName
					)}
					withForm={false}
				>
					<ManageMembersList
						className="c-p-4"
						config={SPACE_MEMBERS_CONFIG}
						emptyStateDescription={Liferay.Language.get(
							'add-members-to-this-space'
						)}
						externalReferenceCode={externalReferenceCode}
						hasAssignMembersPermission={hasAssignMembersPermission}
						onHasSelectedMembersChange={setHasSelectedMembers}
						ownerId={assetLibraryCreatorUserId}
						renderAddMembersInput={(api) => (
							<AddMembersInput {...api} />
						)}
					/>

					<ClayButton.Group className="mb-0 w-100" spaced vertical>
						<ClayButton
							className="mt-4"
							onClick={onContinueBtnClick}
						>
							{hasSelectedMembers
								? Liferay.Language.get('continue')
								: Liferay.Language.get(
										'continue-without-members'
									)}
						</ClayButton>
					</ClayButton.Group>
				</NewSpaceFormSection>
			</ClayLayout.Col>

			<ClayLayout.Col className="d-lg-flex d-none" lg={6}>
				<div className="border overflow-hidden rounded-lg">
					<img
						alt=""
						src={getImage('add_space_members_illustration.svg')}
					></img>
				</div>
			</ClayLayout.Col>
		</ClayLayout.Row>
	);
}

export default AddSpaceMembers;
