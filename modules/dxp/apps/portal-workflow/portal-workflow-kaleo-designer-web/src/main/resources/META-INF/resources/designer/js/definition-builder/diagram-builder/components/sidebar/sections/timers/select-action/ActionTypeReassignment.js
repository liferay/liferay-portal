/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useState} from 'react';

import {retrieveUser, retrieveUsersBy} from '../../../../../../util/fetchUtil';
import AssetCreator from '../select-reassignment/AssetCreator';
import ResourceActions from '../select-reassignment/ResourceActions';
import Role from '../select-reassignment/Role';
import RoleType from '../select-reassignment/RoleType';
import ScriptedReassignment from '../select-reassignment/ScriptedReassignment';
import {SelectReassignment} from '../select-reassignment/SelectReassignment';
import User from '../select-reassignment/User';

const assignmentSectionComponents = {
	assetCreator: AssetCreator,
	resourceActions: ResourceActions,
	roleId: Role,
	roleType: RoleType,
	scriptedReassignment: ScriptedReassignment,
	user: User,
};

const ActionTypeReassignment = ({
	actionData,
	actionSectionsIndex,
	identifier,
	sectionsLength,
	setActionSections,
	setContentName,
	setErrors,
	timersIndex,
}) => {
	const reassignmentType = actionData.assignmentType;
	const [subSections, setSubSections] = useState(
		actionData?.sectionData?.length
			? actionData?.sectionData
			: [{identifier: `${Date.now()}-0`}]
	);

	useEffect(() => {
		if (reassignmentType === 'user' || reassignmentType === 'roleType') {
			setActionSections((currentSections) => {
				const updatedSections = [...currentSections];

				updatedSections[actionSectionsIndex].assignmentType =
					reassignmentType;
				updatedSections[actionSectionsIndex].sectionData = subSections;

				return updatedSections;
			});
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [subSections]);

	useEffect(() => {
		if (
			reassignmentType === 'user' &&
			actionData?.sectionData?.length &&
			actionData.sectionData.some(({emailAddress}) => emailAddress)
		) {
			const retrievedUsers = [];
			retrieveUsersBy(
				'emailAddress',
				actionData.sectionData.map(({emailAddress}) => emailAddress)
			)
				.then((response) => response.json())
				.then(({items}) => {
					items.forEach((item, index) => {
						retrievedUsers.push({
							emailAddress: item.emailAddress,
							identifier: `${Date.now()}-${index}`,
							name: item.name,
							screenName: item.alternateName,
							userId: item.id,
						});
					});
				})
				.then(() => {
					setSubSections(retrievedUsers);
				});
		}
		else if (
			reassignmentType === 'user' &&
			actionData?.sectionData?.length &&
			actionData.sectionData.some(({userId}) => userId)
		) {
			Promise.all(
				actionData.sectionData.map(
					({userExternalReferenceCode, userId}) =>
						retrieveUser(userExternalReferenceCode, userId).then(
							(response) => response.json()
						)
				)
			).then((users) => {
				setSubSections(
					users
						.filter((user) => user.id)
						.map((user, index) => ({
							emailAddress: user.emailAddress,
							identifier: `${Date.now()}-${index}`,
							name: user.name,
							screenName: user.alternateName,
							userExternalReferenceCode:
								user.externalReferenceCode,
							userId: user.id,
						}))
				);
			});
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	const updateReassignmentType = (value) => {
		setActionSections((currentSections) => {
			const updatedSections = [...currentSections];

			updatedSections[actionSectionsIndex].assignmentType = value;

			return updatedSections;
		});
	};

	const ReassignmentSectionComponent =
		assignmentSectionComponents[reassignmentType];

	return (
		<>
			<SelectReassignment
				currentAssignmentType={reassignmentType}
				setSection={updateReassignmentType}
				setSubSections={setSubSections}
			/>

			{subSections.map(
				({identifier: subSectionIdentifier, ...restProps}, index) => {
					return (
						ReassignmentSectionComponent && (
							<ReassignmentSectionComponent
								actionData={actionData}
								actionSectionsIndex={actionSectionsIndex}
								currentAssignmentType={reassignmentType}
								identifier={identifier}
								index={index}
								key={`section-${subSectionIdentifier}`}
								reassignmentType={reassignmentType}
								restProps={restProps}
								sectionsLength={sectionsLength}
								setActionSections={setActionSections}
								setContentName={setContentName}
								setErrors={setErrors}
								setSections={setSubSections}
								subSectionIdentifier={subSectionIdentifier}
								subSectionsLength={subSections.length}
								timersIndex={timersIndex}
							/>
						)
					);
				}
			)}
		</>
	);
};

export default ActionTypeReassignment;
