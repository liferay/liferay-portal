import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import client from 'shared/apollo/client';
import Form, {
	toPromise,
	validateMaxLength,
	validateRequired,
} from 'shared/components/form';
import Loading, {Align} from 'shared/components/Loading';
import React from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {DataTypes} from 'shared/types/DataTypes';
import {Attribute, Event} from 'event-analysis/utils/types';
import {connect, ConnectedProps} from 'react-redux';
import {DATA_TYPE_LABELS_MAP} from 'event-analysis/utils/utils';
import {debounce, get} from 'lodash/fp';
import {DocumentNode, useMutation, useQuery} from '@apollo/client';

import {
	EventAttributeDefinitionData,
	EventAttributeDefinitionVariables,
	UpdateEventAttributeDefinitionVariables,
} from 'event-analysis/queries/EventAttributeDefinitionQuery';
import {
	EventDefinitionData,
	EventDefinitionVariables,
	UpdateEventDefinitionVariables,
} from 'event-analysis/queries/EventDefinitionQuery';
import {SafeResults} from 'shared/hoc/util';
import {sequence} from 'shared/util/promise';
import {sub} from 'shared/util/lang';

const DATA_TYPE_OPTIONS = [
	DataTypes.Boolean,
	DataTypes.Date,
	DataTypes.Duration,
	DataTypes.Number,
	DataTypes.String,
];

const connector = connect(null, {addAlert});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IEditAttributeEventModalProps extends PropsFromRedux {
	id: string;
	mutation: DocumentNode;
	observer: Observer;
	onClose: (save: boolean) => void;
	query: DocumentNode;
	showTypecast?: boolean;
}

const EditAttributeEventModal: React.FC<IEditAttributeEventModalProps> = ({
	addAlert,
	id,
	mutation,
	observer,
	onClose,
	query,
	showTypecast,
}) => {
	const [update] = useMutation<
		EventDefinitionData | EventAttributeDefinitionData,
		UpdateEventDefinitionVariables | UpdateEventAttributeDefinitionVariables
	>(mutation);
	const result = useQuery<
		EventDefinitionData | EventAttributeDefinitionData,
		EventDefinitionVariables | EventAttributeDefinitionVariables
	>(query, {
		fetchPolicy: 'no-cache',
		variables: {id},
	});

	const dataMapper = get(
		showTypecast ? 'eventAttributeDefinition' : 'eventDefinition'
	);

	const debouncedValidateDisplayName = debounce(250)((
		value: string
	): Promise<string> => {
		let error = '';

		if (value && value !== dataMapper(result.data).displayName) {
			return client
				.query({
					fetchPolicy: 'no-cache',
					query,
					variables: {displayName: value.trim()},
				})
				.then(({data}) => {
					if (dataMapper(data)) {
						error = sub(
							Liferay.Language.get(
								'an-x-already-exists-with-that-display-name.-please-enter-a-different-display-name'
							),
							[
								showTypecast
									? Liferay.Language.get(
											'attribute[noun]'
										).toLowerCase()
									: Liferay.Language.get(
											'event'
										).toLowerCase(),
							]
						) as string;
					}

					return error;
				})
				.catch(() => {
					error = Liferay.Language.get(
						'there-was-an-error-processing-your-request.-please-try-again'
					);

					return error;
				});
		}
		else {
			return toPromise(error);
		}
	});

	const validateDisplayName = (value: any): Promise<any> =>
		Promise.resolve(debouncedValidateDisplayName(value));

	return (
		<ClayModal observer={observer}>
			<SafeResults {...result} page={false} pageDisplay={false}>
				{(item: {
					eventAttributeDefinition: Attribute;
					eventDefinition: Event;
				}) => {
					const {dataType, description, displayName, name} =
						dataMapper(item);

					return (
						<>
							<ClayModal.Header>
								{displayName
									? `${name} - ${displayName}`
									: name}
							</ClayModal.Header>

							<Form
								initialValues={
									showTypecast
										? {
												dataType,
												description: description || '',
												displayName:
													displayName || name || '',
											}
										: {
												description: description || '',
												displayName:
													displayName || name || '',
											}
								}
								onSubmit={(variables, {setSubmitting}) => {
									update({
										variables: {
											id,
											...variables,
										},
									})
										.then(({data}) => {
											const {displayName, name} = get(
												showTypecast
													? 'updateEventAttributeDefinition'
													: 'updateEventDefinition'
											)(data);
											addAlert({
												alertType: Alert.Types.Success,
												message: sub(
													Liferay.Language.get(
														'x-has-been-updated'
													),
													[displayName || name]
												) as string,
											});

											setSubmitting(false);

											onClose(true);
										})
										.catch(() => {
											addAlert({
												alertType: Alert.Types.Error,
												message: Liferay.Language.get(
													'there-was-an-error-processing-your-request.-please-try-again'
												),
											});

											setSubmitting(false);
										});
								}}
							>
								{({handleSubmit, isSubmitting, isValid}) => (
									<Form.Form onSubmit={handleSubmit}>
										<ClayModal.Body>
											<Form.Group autoFit>
												<Form.GroupItem>
													<Form.Input
														label={Liferay.Language.get(
															'display-name'
														)}
														name="displayName"
														required
														type="text"
														validate={sequence([
															validateRequired,
															validateMaxLength(
																255
															),
															validateDisplayName,
														])}
													/>
												</Form.GroupItem>
											</Form.Group>

											<Form.Group autoFit>
												<Form.GroupItem>
													<Form.Input
														label={Liferay.Language.get(
															'description'
														)}
														name="description"
														type="textarea"
														validate={sequence([
															validateMaxLength(
																255
															),
														])}
													/>
												</Form.GroupItem>
											</Form.Group>

											{showTypecast && (
												<Form.Group>
													<Form.GroupItem>
														<Form.Select
															label={Liferay.Language.get(
																'default-data-typecast'
															)}
															name="dataType"
														>
															{DATA_TYPE_OPTIONS.map(
																(value) => (
																	<Form.Select.Item
																		key={
																			value
																		}
																		value={
																			value
																		}
																	>
																		{
																			DATA_TYPE_LABELS_MAP[
																				value
																			]
																		}
																	</Form.Select.Item>
																)
															)}
														</Form.Select>
													</Form.GroupItem>

													<Form.GroupItem className="text-secondary">
														{Liferay.Language.get(
															'data-typecast-determines-how-attributes-can-be-analyzed'
														)}
													</Form.GroupItem>

													<Form.GroupItem className="text-secondary">
														{Liferay.Language.get(
															'e.g.-typecasting-to-number-will-support-greater-than-or-less-than-conditions'
														)}
													</Form.GroupItem>
												</Form.Group>
											)}
										</ClayModal.Body>

										<ClayModal.Footer
											last={
												<ClayButton.Group spaced>
													<ClayButton
														className="button-root"
														displayType="secondary"
														onClick={() =>
															onClose(false)
														}
													>
														{Liferay.Language.get(
															'cancel'
														)}
													</ClayButton>

													<ClayButton
														className="button-root"
														disabled={!isValid}
														displayType="primary"
														type="submit"
													>
														{isSubmitting && (
															<Loading
																align={
																	Align.Left
																}
															/>
														)}

														{Liferay.Language.get(
															'save'
														)}
													</ClayButton>
												</ClayButton.Group>
											}
										/>
									</Form.Form>
								)}
							</Form>
						</>
					);
				}}
			</SafeResults>
		</ClayModal>
	);
};

export default connector(EditAttributeEventModal);
