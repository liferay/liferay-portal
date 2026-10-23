import * as API from 'shared/api';
import autobind from 'autobind-decorator';
import BaseEditPage from 'shared/components/base-edit-page';
import ConfirmationDialog from 'shared/components/ConfirmationDialog';
import getCN from 'classnames';
import {LoadingDialog} from 'shared/components/modals/LoadingModal';
import omitDefinedProps from 'shared/util/omitDefinedProps';
import React from 'react';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {connect} from 'react-redux';
import {PropTypes} from 'prop-types';
import {Routes, SEGMENTS, toRoute} from 'shared/util/router';
import {Segment} from 'shared/util/records';
import {SegmentCategories} from 'shared/util/constants';

const MessageKeys = {
	ExternalReferenceCodeIsAlreadyUsed:
		'external-reference-code-is-already-used',
	NameCannotBeBlank: 'name-cannot-be-blank',
	NameIsAlreadyUsed: 'name-is-already-used'
};

const ERRORS = {
	[MessageKeys.ExternalReferenceCodeIsAlreadyUsed]: {
		alertType: Alert.Types.Warning,
		message: Liferay.Language.get(
			'this-segment-erc-is-currently-in-use.-please-try-a-different-one'
		)
	},
	[MessageKeys.NameCannotBeBlank]: {
		alertType: Alert.Types.Error,
		message: Liferay.Language.get('name-cannot-be-blank')
	},

	[MessageKeys.NameIsAlreadyUsed]: {
		alertType: Alert.Types.Warning,
		message: Liferay.Language.get(
			'this-segment-name-is-currently-in-use.-please-try-a-different-one'
		)
	}
};
export default WrappedComponent => {
	class BaseEdit extends React.Component {
		static propTypes = {
			addAlert: PropTypes.func.isRequired,
			channelId: PropTypes.string,
			groupId: PropTypes.string.isRequired,
			history: PropTypes.object.isRequired,
			id: PropTypes.string,
			segment: PropTypes.instanceOf(Segment)
		};

		state = {
			deleteConfirmationOpen: false,
			saving: false,
			onDelete: false
		};

		componentDidMount() {
			this._startDate = Date.now();
		}

		@autobind
		deleteSegment() {
			this.setState({deleteConfirmationOpen: true});
		}

		getPageTitle() {
			const {id, segment, segmentCategory} = this.props;

			if (segmentCategory === SegmentCategories.Account) {
				return id && segment
					? Liferay.Language.get('edit-accounts-segment')
					: Liferay.Language.get('create-accounts-segment');
			}

			return id && segment
				? Liferay.Language.get('edit-individuals-segment')
				: Liferay.Language.get('create-individuals-segment');
		}

		@autobind
		handleSubmit(form, formRef, submitFn) {
			const {addAlert, channelId, groupId, history, id} = this.props;

			const {setSubmitting} = formRef.current;

			this.setState({saving: true});

			submitFn(form)
				.then(segment => {
					if (
						(Array.isArray(segment) && segment.length) ||
						(segment && !Array.isArray(segment))
					) {
						history.push(
							toRoute(Routes.CONTACTS_ENTITY, {
								channelId,
								groupId,
								id: segment.id || segment[0].id,
								type: SEGMENTS
							})
						);

						addAlert({
							alertType: Alert.Types.Success,
							message: Liferay.Language.get(
								'changes-to-segment-saved'
							)
						});
					}

					setSubmitting(false);

					return segment;
				})
				.catch(error => {
					const {alertType, message} = ERRORS[error.message];

					addAlert({
						alertType,
						message
					});

					setSubmitting(false);
				})
				.finally(() => this.setState({saving: false}));
		}

		render() {
			const {
				addAlert,
				channelId,
				className,
				groupId,
				history,
				id,
				segment,
				type,
				...otherProps
			} = this.props;

			const {deleteConfirmationOpen, onDelete, saving} = this.state;

			const editing = !!id;

			return (
				<>
					<BaseEditPage
						className={getCN('segment-edit-root', className, {
							editing
						})}
						documentTitle={`${this.getPageTitle()} - ${Liferay.Language.get(
							'segment'
						)}`}
					>
						<WrappedComponent
							{...omitDefinedProps(otherProps, BaseEdit.propTypes)}
							channelId={channelId}
							editing={editing}
							groupId={groupId}
							id={id}
							onDelete={onDelete}
							onDeleteSegment={this.deleteSegment}
							onSubmit={this.handleSubmit}
							segment={segment}
						/>
					</BaseEditPage>

					{saving && (
						<LoadingDialog
							message={Liferay.Language.get(
								'this-will-only-take-a-moment'
							)}
							title={
								editing
									? Liferay.Language.get('updating')
									: Liferay.Language.get('creating')
							}
						/>
					)}

					{deleteConfirmationOpen && (
						<ConfirmationDialog
							message={
								<div>
									<div className='h4 text-secondary'>
										{Liferay.Language.get(
											'are-you-sure-you-want-to-delete-this-segment'
										)}
									</div>

									<p>
										{Liferay.Language.get(
											'you-will-lose-all-data-related-to-this-segment.-you-will-not-be-able-to-undo-this-operation'
										)}
									</p>
								</div>
							}
							onClose={() =>
								this.setState({deleteConfirmationOpen: false})
							}
							onSubmit={() => {
								this.setState({onDelete: true});

								return API.individualSegment
									.delete({
										groupId,
										ids: [id]
									})
									.then(() => {
										addAlert({
											alertType: Alert.Types.Success,
											message: Liferay.Language.get(
												'the-segment-has-been-deleted'
											)
										});

										history.push(
											toRoute(Routes.CONTACTS_LIST_ENTITY, {
												channelId,
												groupId,
												type: SEGMENTS
											})
										);
									})
									.catch(() => {
										addAlert({
											alertType: Alert.Types.Error,
											message: Liferay.Language.get('error'),
											timeout: false
										});

										this.setState({onDelete: false});
									});
							}}
							submitButtonDisplay='warning'
							submitMessage={Liferay.Language.get('delete')}
							title={Liferay.Language.get('warning')}
							type='warning'
						/>
					)}
				</>
			);
		}
	}

	return connect(null, {
		addAlert
	})(BaseEdit);
};
