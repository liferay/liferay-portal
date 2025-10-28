/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Text} from '@clayui/core';
import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayList from '@clayui/list';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal, {useModal} from '@clayui/modal';
import {API, stringUtils} from '@liferay/object-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';
import {Elements, FlowElement, isNode} from 'react-flow-renderer';

import {TYPES} from '../ModelBuilderContext/typesEnum';
import {ObjectRelationshipEdgeData, TAction} from '../types';

import './ModalPublishObjectDefinitions.scss';

enum STATUS {
	APPROVED = 0,
	DRAFT = 2,
	PENDING = 1,
	REJECTED = -1,
}

interface ModalPublishObjectDefinitionsProps {
	disableAutoClose: boolean;
	dispatch: React.Dispatch<TAction>;
	elements: Elements<ObjectDefinitionNodeData | ObjectRelationshipEdgeData[]>;
	handleOnClose: () => void;
}

interface ObjectDefinitionStatus {
	code: number;
	label?: string;
	label_i18n?: string;
}

interface SelectedDraftObjectDefinition {
	errorMessage?: string;
	id: number;
	status: ObjectDefinitionStatus;
}

type TStatus = 'danger' | 'info' | 'success' | 'warning';

export function ModalPublishObjectDefinitions({
	disableAutoClose,
	dispatch,
	elements,
	handleOnClose,
}: ModalPublishObjectDefinitionsProps) {
	const {observer, onClose} = useModal({
		onClose: () => handleOnClose(),
	});

	const objectDefinitionNodes = elements.filter((element) =>
		isNode(element)
	) as Elements<ObjectDefinitionNodeData>;

	const [draftObjectDefinitionNodes] = useState<
		Elements<ObjectDefinitionNodeData>
	>(
		objectDefinitionNodes.filter(
			(objectDefinitionNode) =>
				objectDefinitionNode.data?.status?.code === STATUS.DRAFT &&
				!objectDefinitionNode.data.linkedObjectDefinition
		)
	);
	const [modalHeaderMessage, setModalHeaderMessage] = useState<string>(
		Liferay.Language.get('confirm-publishing')
	);
	const [publishObjectDefinitionsStatus, setPublishObjectDefinitionsStatus] =
		useState<number>(STATUS.DRAFT);
	const [
		selectAllDraftObjectDefinitions,
		setSelectAllDraftObjectDefinitions,
	] = useState<boolean>(false);
	const [selectedDraftObjectDefinitions, setSelectedDraftObjectDefinitions] =
		useState<SelectedDraftObjectDefinition[]>([]);

	const updateObjectDefinitionStatus = (
		selectedDraftObjectDefinitions: SelectedDraftObjectDefinition[],
		objectDefinitionId: number,
		objectDefinitionStatus: ObjectDefinitionStatus,
		errorMessage?: string
	) => {
		return selectedDraftObjectDefinitions.map(
			(selectedDraftObjectDefinition) => {
				if (selectedDraftObjectDefinition.id === objectDefinitionId) {
					return {
						id: objectDefinitionId,
						status: objectDefinitionStatus,
						...(objectDefinitionStatus.code === STATUS.REJECTED && {
							errorMessage,
						}),
					};
				}
				else {
					return selectedDraftObjectDefinition;
				}
			}
		) as SelectedDraftObjectDefinition[];
	};

	const publishObjectDefinition = (
		objectDefinitionId: number
	): Promise<ObjectDefinition | number> => {

		// eslint-disable-next-line no-async-promise-executor
		return new Promise<ObjectDefinition | number>(async (resolve) => {
			try {
				const objectDefinitionResponse: any =
					await API.postObjectDefinitionPublish(objectDefinitionId);

				const objectDefinitionResponseJSON =
					await objectDefinitionResponse.json();

				if (!objectDefinitionResponse.ok) {
					const {detail, title} = objectDefinitionResponseJSON;

					if (detail) {
						const [details] = JSON.parse(detail);
						throw new Error(details.message);
					}
					else if (title) {
						throw new Error(title);
					}
				}

				setSelectedDraftObjectDefinitions((prevState) =>
					updateObjectDefinitionStatus(
						prevState,
						objectDefinitionId,
						objectDefinitionResponseJSON.status
					)
				);

				resolve(objectDefinitionResponseJSON);
			}
			catch (error: any) {
				setSelectedDraftObjectDefinitions((prevState) =>
					updateObjectDefinitionStatus(
						prevState,
						objectDefinitionId,
						{code: STATUS.REJECTED},
						error.message
					)
				);

				// don't throw reject, so that it doesn't go to the catch flow of the promise.all

				resolve(STATUS.REJECTED);
			}
		});
	};

	const handleOnClickPublish = async () => {
		setModalHeaderMessage(`${Liferay.Language.get('publishing')}...`);
		setPublishObjectDefinitionsStatus(STATUS.PENDING);

		try {
			const publishObjectDefinitionResponses = [];

			for (const selectedDraftObjectDefinition of selectedDraftObjectDefinitions) {
				setSelectedDraftObjectDefinitions((prevState) =>
					updateObjectDefinitionStatus(
						prevState,
						selectedDraftObjectDefinition.id,
						selectedDraftObjectDefinition.status
					)
				);

				const publishObjectDefinitionResponse =
					await publishObjectDefinition(
						selectedDraftObjectDefinition.id
					);

				publishObjectDefinitionResponses.push(
					publishObjectDefinitionResponse
				);
			}

			const hasRejectedPublishObjectDefinitionResponses =
				publishObjectDefinitionResponses.some(
					(publishObjectDefinitionResponse) =>
						typeof publishObjectDefinitionResponse === 'number' &&
						publishObjectDefinitionResponse === STATUS.REJECTED
				);
			const acceptedPublishObjectDefinitionResponses =
				publishObjectDefinitionResponses.filter(
					(publishObjectDefinitionResponse) =>
						typeof publishObjectDefinitionResponse === 'object'
				);

			setModalHeaderMessage(
				!hasRejectedPublishObjectDefinitionResponses
					? Liferay.Language.get('successfully-published')
					: Liferay.Language.get('published-with-errors')
			);
			setPublishObjectDefinitionsStatus(
				!hasRejectedPublishObjectDefinitionResponses
					? STATUS.APPROVED
					: STATUS.REJECTED
			);

			const newElements = elements.map((element) => {
				if (isNode(element)) {
					const elementId =
						(element as FlowElement<ObjectDefinitionNodeData>).data
							?.id || 0;

					const currentObjectDefinitionPublishedResponse = (
						acceptedPublishObjectDefinitionResponses as ObjectDefinition[]
					).find(
						(acceptedPublishObjectDefinitionResponse) =>
							acceptedPublishObjectDefinitionResponse.id ===
							elementId
					);

					if (currentObjectDefinitionPublishedResponse) {
						return {
							...element,
							data: {
								...element.data,
								status: currentObjectDefinitionPublishedResponse.status,
							},
						};
					}

					return element;
				}

				return element;
			}) as Elements<ObjectDefinitionNodeData>;

			dispatch({
				payload: {
					newElements,
				},
				type: TYPES.SET_ELEMENTS,
			});
		}
		catch (error) {
			setModalHeaderMessage(Liferay.Language.get('confirm-publishing'));
			setPublishObjectDefinitionsStatus(STATUS.REJECTED);
		}
	};

	const handleSelectAllObjectDefinitions = (
		actionType?: 'checkAll' | 'checkRemoveAll'
	): void => {
		if (actionType) {
			const allSelectedDraftObjectDefinitions =
				selectedDraftObjectDefinitions.length ===
				draftObjectDefinitionNodes.length;

			if (
				allSelectedDraftObjectDefinitions &&
				actionType !== 'checkAll'
			) {
				setSelectAllDraftObjectDefinitions(false);
				setSelectedDraftObjectDefinitions([]);
			}
			else {
				const newSelectedDraftObjectDefinitions =
					draftObjectDefinitionNodes.map(
						(draftObjectDefinitionNode) => {
							const {data} = draftObjectDefinitionNode;

							return {id: data?.id!, status: data?.status!};
						}
					);

				setSelectAllDraftObjectDefinitions(true);
				setSelectedDraftObjectDefinitions(
					newSelectedDraftObjectDefinitions
				);
			}
		}
	};

	const handleCheckboxChange = (objectDefinitionId: number): void => {
		if (
			selectedDraftObjectDefinitions.some(
				(selectedDraftObjectDefinition) =>
					selectedDraftObjectDefinition.id === objectDefinitionId
			)
		) {
			setSelectedDraftObjectDefinitions(
				selectedDraftObjectDefinitions.filter(
					(selectedDraftObjectDefinition) =>
						selectedDraftObjectDefinition.id !== objectDefinitionId
				)
			);
		}
		else {
			const selectedDraftObjectDefinitionNode =
				objectDefinitionNodes.find(
					(objectDefinitionNode) =>
						objectDefinitionNode.data?.id === objectDefinitionId
				)!;

			setSelectedDraftObjectDefinitions([
				...selectedDraftObjectDefinitions,
				{
					id: objectDefinitionId,
					status: selectedDraftObjectDefinitionNode.data?.status!,
				},
			]);
		}
	};

	const modalStatus = (): TStatus => {
		switch (publishObjectDefinitionsStatus) {
			case STATUS.APPROVED:
				return 'success';
			case STATUS.PENDING:
				return 'info';
			case STATUS.REJECTED:
				return 'warning';
			default:
				return 'warning';
		}
	};

	useEffect(
		() =>
			setSelectAllDraftObjectDefinitions(
				!!selectedDraftObjectDefinitions.length
			),
		[selectedDraftObjectDefinitions]
	);

	return (
		<ClayModal
			className="lfr-object__object-view-modal-object-definitions"
			disableAutoClose={disableAutoClose}
			observer={observer}
			status={modalStatus()}
		>
			{publishObjectDefinitionsStatus !== STATUS.PENDING ? (
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
				>
					{modalHeaderMessage}
				</ClayModal.Header>
			) : (
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
					withTitle={false}
				>
					<ClayModal.ItemGroup>
						<ClayModal.Item>
							<ClayModal.Title>
								<ClayModal.TitleIndicator>
									<ClayIcon
										color="blue"
										symbol="info-circle"
									/>
								</ClayModal.TitleIndicator>

								{modalHeaderMessage}
							</ClayModal.Title>
						</ClayModal.Item>
					</ClayModal.ItemGroup>
				</ClayModal.Header>
			)}

			<ClayModal.Body>
				<div className="c-mb-sm-4">
					<Text size={3}>
						{`${Liferay.Language.get(
							'publishing-all-draft-objects-at-once-can-make-them-available-for-creating-entries'
						)} ${Liferay.Language.get(
							'please-check-before-confirming'
						)}`}
					</Text>
				</div>

				{publishObjectDefinitionsStatus === STATUS.DRAFT && (
					<div
						className={`lfr-object__object-view-modal-object-definitions-select-all-checkbox c-px-sm-3 c-mb-sm-2 ${
							selectAllDraftObjectDefinitions ? 'active' : ''
						}`}
					>
						<ClayCheckbox
							checked={selectAllDraftObjectDefinitions}
							indeterminate={
								selectAllDraftObjectDefinitions &&
								selectedDraftObjectDefinitions.length !==
									draftObjectDefinitionNodes.length
							}
							label={`${sub(
								Liferay.Language.get('x-of-x-items-selected'),
								selectedDraftObjectDefinitions.length,
								draftObjectDefinitionNodes.length
							)}`}
							onChange={() =>
								handleSelectAllObjectDefinitions(
									'checkRemoveAll'
								)
							}
						/>

						<ClayButton
							aria-labelledby={Liferay.Language.get('select-all')}
							className="c-px-sm-0 text-3 text-weight-semi-bold"
							displayType="link"
							onClick={() =>
								handleSelectAllObjectDefinitions('checkAll')
							}
							size="sm"
						>
							{Liferay.Language.get('select-all')}
						</ClayButton>
					</div>
				)}

				<ClayList className="container-list">
					{draftObjectDefinitionNodes.map(
						(draftObjectDefinitionNode) => {
							const {data, id} = draftObjectDefinitionNode;

							const selectedDraftObjectDefinition =
								selectedDraftObjectDefinitions.find(
									(draftObjectDefinition) =>
										draftObjectDefinition.id === data?.id!
								);

							const isDraftObjectDefinitionSelected =
								selectedDraftObjectDefinition?.id === data?.id!;

							return (
								<ClayList.Item
									className={`lfr-object__object-view-modal-object-definitions-list-item ${
										isDraftObjectDefinitionSelected
											? 'active'
											: ''
									}`}
									key={id}
								>
									<div>
										{publishObjectDefinitionsStatus ===
											STATUS.DRAFT && (
											<ClayCheckbox
												checked={
													isDraftObjectDefinitionSelected
												}
												disabled={
													selectedDraftObjectDefinition?.status !==
														undefined &&
													[
														STATUS.APPROVED,
														STATUS.PENDING,
													].includes(
														selectedDraftObjectDefinition
															?.status?.code
													)
												}
												onChange={() =>
													handleCheckboxChange(
														data?.id!
													)
												}
											/>
										)}

										<ClayIcon symbol="catalog" />

										<div>
											<div>
												<Text
													size={3}
													weight="semi-bold"
												>
													{stringUtils.getLocalizableLabel(
														{
															fallbackLabel:
																data?.name,
															labels: data?.label,
														}
													)}
												</Text>
											</div>

											{selectedDraftObjectDefinition
												?.status?.code ===
												STATUS.REJECTED && (
												<span className="rejected text-danger">
													<ClayIcon
														color="danger"
														symbol="exclamation-full"
													/>

													<Text size={2}>
														{
															selectedDraftObjectDefinition?.errorMessage
														}
													</Text>
												</span>
											)}
										</div>
									</div>

									<div>
										{selectedDraftObjectDefinition?.status
											?.code === STATUS.PENDING && (
											<ClayLoadingIndicator
												displayType="secondary"
												size="sm"
											/>
										)}

										{selectedDraftObjectDefinition?.status
											?.code === STATUS.APPROVED && (
											<Text color="success">
												<ClayIcon symbol="check" />
											</Text>
										)}
									</div>
								</ClayList.Item>
							);
						}
					)}
				</ClayList>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					publishObjectDefinitionsStatus === STATUS.APPROVED ||
					publishObjectDefinitionsStatus === STATUS.REJECTED ? (
						<ClayButton.Group key={1} spaced>
							<ClayButton
								aria-labelledby={Liferay.Language.get('close')}
								displayType="primary"
								onClick={onClose}
								size="sm"
							>
								{Liferay.Language.get('close')}
							</ClayButton>
						</ClayButton.Group>
					) : (
						<ClayButton.Group key={2} spaced>
							<>
								{publishObjectDefinitionsStatus !==
									STATUS.PENDING && (
									<ClayButton
										aria-labelledby={Liferay.Language.get(
											'cancel'
										)}
										className="c-mr-sm-2"
										displayType="secondary"
										onClick={onClose}
										size="sm"
									>
										{Liferay.Language.get('cancel')}
									</ClayButton>
								)}
								<ClayButton
									aria-labelledby={
										publishObjectDefinitionsStatus ===
										STATUS.PENDING
											? Liferay.Language.get(
													'please-wait'
												) + '...'
											: Liferay.Language.get(
													'publish-objects'
												)
									}
									disabled={
										!selectedDraftObjectDefinitions.length ||
										publishObjectDefinitionsStatus ===
											STATUS.PENDING
									}
									displayType="primary"
									onClick={handleOnClickPublish}
									size="sm"
								>
									{publishObjectDefinitionsStatus ===
									STATUS.PENDING
										? Liferay.Language.get('please-wait') +
											'...'
										: Liferay.Language.get(
												'publish-objects'
											)}
								</ClayButton>
							</>
						</ClayButton.Group>
					)
				}
			></ClayModal.Footer>
		</ClayModal>
	);
}
